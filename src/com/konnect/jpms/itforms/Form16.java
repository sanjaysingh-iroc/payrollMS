package com.konnect.jpms.itforms;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Form16 extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	public CommonFunctions CF;
	public String strUserType;
	public String strSessionEmpId;
	 
	String strSubmit;
	String financialYear;
	List<FillFinancialYears> financialYearList; 

	List<FillPayCycles> paycycleList;
	List<FillOrganisation> orgList; 
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	
	String exportType;
	
	String proPage;
	String minLimit;
	
	String strSearch;
	
	String emp_id;
	String formType;
	
public String execute() throws Exception {
	session = request.getSession();
	CF = (CommonFunctions)session.getAttribute(CommonFunctions);
	if(CF==null)return LOGIN;
	
	UtilityFunctions uF = new UtilityFunctions();		
	request.setAttribute(PAGE, PForm16);
	request.setAttribute(TITLE, TForm16);
	
	strUserType = (String)session.getAttribute(USERTYPE);
	strSessionEmpId = (String)session.getAttribute(EMPID);
	
	request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
	
	if(uF.parseToInt(getProPage()) == 0) {
		setProPage("1");
	}
	
	if(getF_org()==null || getF_org().trim().equals("")) {
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
	
	if(getFormType()!=null && getFormType().trim().equalsIgnoreCase("revoke")) {
		revokeForm16(uF);
	} else if(getFormType()!=null && getFormType().trim().equalsIgnoreCase("form16")) {
		generateForm16PDF(uF);
		return null;
	} else if(getFormType()!=null && getFormType().trim().equalsIgnoreCase("approveRelease")) {
		generateForm16PDF(uF);
	}
	
	getSearchAutoCompleteData(uF);
	viewForm16(CF);
	return loadForm16Filter(uF);
}


private void revokeForm16(UtilityFunctions uF) {
	PreparedStatement pst = null;
	ResultSet rs = null;
	Connection con = null;
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
		con.setAutoCommit(false);
		Map<String, String> hmEmployeeMap = CF.getEmpNameMap(con, null, null);
		pst = con.prepareStatement("select * from form16_documents where financial_year_start=? and financial_year_end=? and emp_id=?");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3, uF.parseToInt(getEmp_id()));
//		System.out.println("pst=====>"+pst);
		rs = pst.executeQuery();
		String strFileName = null;
		boolean flag = false;
		while(rs.next()) {
			strFileName = rs.getString("form16_name");
			flag = true;
		}
		rs.close();
		pst.close();
//		System.out.println("strFileName=====>"+strFileName+"---flag====>"+flag);
		
		if(flag && strFileName!=null && !strFileName.trim().equals("") && !strFileName.trim().equalsIgnoreCase("NULL")) {
			pst = con.prepareStatement("delete from form16_documents where financial_year_start=? and financial_year_end=? and emp_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getEmp_id()));
//			System.out.println("pst=====>"+pst);
			int x = pst.executeUpdate();
			if(x > 0) {
				String directory = CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_DOCUMENT+"/"+I_FORM16+"/"+getEmp_id();
				File form16File = new File(directory + File.separator + strFileName);
//				System.out.println("form16File=====>"+form16File.getPath());
				if(form16File.isFile()) {
//				    System.out.println("delete isFile");
				    if(form16File.exists()) {
				    	form16File.delete();
				    	con.commit();
//					    System.out.println("delete exists");
					    request.setAttribute(MESSAGE, SUCCESSM+uF.showData(hmEmployeeMap.get(getEmp_id()), "")+"'s Form 16 successfully revoked."+END);
					} else {
						con.rollback();
//					    System.out.println("delete exists fail");
					    request.setAttribute(MESSAGE, ERRORM+uF.showData(hmEmployeeMap.get(getEmp_id()), "")+"'s Form 16 revoke failed. Please, try again!"+END);
					}
				} else {
					con.rollback();
					request.setAttribute(MESSAGE, ERRORM+uF.showData(hmEmployeeMap.get(getEmp_id()), "")+"'s Form 16 revoke failed. Please, try again!"+END);
				}
			}
		}
		
	} catch (Exception e) {
//		request.setAttribute(MESSAGE, ERRORM+uF.showData(hmEmployeeMap.get(getEmp_id()), "")+"'s Form 16 revoke failed. Please, try again!"+END);
		request.setAttribute(MESSAGE, ERRORM+"Form 16 revoke failed. Please, try again!"+END);
		try {
			con.rollback();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}

private void generateForm16PDF(UtilityFunctions uF) {
	
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

		String slabType = CF.getEmpIncomeTaxSlabType(con, CF, getEmp_id(), strFinancialYearStart, strFinancialYearEnd);
		
		Map<String, String> hmSectionMap = CF.getSectionMap(con, strFinancialYearStart, strFinancialYearEnd);
//		Map<String,String> hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
		int nEmpLevelId = CF.getEmpLevelId(getEmp_id(), request);
		Map<String, String> hmSalaryHeadMap = CF.getSalaryHeadsMap(con, nEmpLevelId);
		
//		Map<String, String> hmDept = CF.getDeptMap(con);
		Map<String, String> hmEmpAgeMap = CF.getEmpAgeMap(con, CF);
//		Map<String, String> hmEmpSlabMap = CF.getEmpSlabMap(con, CF);

		Map hmEmpMertoMap = new HashMap();
		Map hmEmpWlocationMap = new HashMap();
		Map hmEmpStateMap = new HashMap();
		CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
		Map<String, String> hmOtherDetailsMap = getOtherDetailsMap(uF.parseToInt(getEmp_id()));
		Map<String, String> hmEmpCodeDesig =CF.getEmpDesigMap(con);
		
		Map<String,Map<String,String>> hmPrevEmpTds = CF.getPrevEmpTdsDetails(con, uF, strFinancialYearStart, strFinancialYearEnd);
		Map hmEmpGenderMap = CF.getEmpGenderMap(con); 
		
		Map<String, String> hmStates = CF.getStateMap(con);
		String orgId = CF.getEmpOrgId(con, uF, getEmp_id());
		String locationID=""+hmEmpWlocationMap.get(getEmp_id());
		
		pst = con.prepareStatement("select * from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id" +
			" and epd.emp_per_id in (select emp_id from authorised_details where wlocation_id=? and financial_year_start=?" +
			" and financial_year_end=?)");
		pst.setInt(1, uF.parseToInt(locationID));
		pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//		System.out.println("pst====>"+pst);
		rs = pst.executeQuery();
		Map<String, String> hmAuthorised = new HashMap<String, String>();
		int nAuthorizeEmpId = 0;
		while (rs.next()) {
		//	String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
		
			String strMiddleName = "";
			
			if(flagMiddleName) {
				if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
					strMiddleName = " "+rs.getString("emp_mname");
				}
			}
			
			String strEmpName = rs.getString("emp_fname") + uF.showData(strMiddleName, "")+" "+ rs.getString("emp_lname");
			hmAuthorised.put("EMP_NAME", strEmpName);
			hmAuthorised.put("EMP_MIDDLE_NAME", strMiddleName);
			
			nAuthorizeEmpId = uF.parseToInt(rs.getString("emp_id"));
		}
		rs.close();
		pst.close();
		
		
		
		String strAuthorizeDesignation = null;
		if(nAuthorizeEmpId > 0) {
			strAuthorizeDesignation = CF.getEmpDesigMapByEmpId(con, ""+nAuthorizeEmpId);
		}
		
		
		pst = con.prepareStatement("select * from org_details where org_id = ? ");
		pst.setInt(1, uF.parseToInt(orgId));
//		System.out.println("pst====>"+pst);
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
		}
		rs.close();
		pst.close();  
		
		Map<String, String> hmReimbursementAmt = new HashMap<String, String>();
		if(CF.getIsReceipt()) {
			String[] firstArr = CF.getPayCycleFromDate(con, strFinancialYearStart, CF.getStrTimeZone(), CF, orgId);
			String[] secondArr = null;
			if(uF.parseToInt(uF.getDateFormat(hmOrg.get("ORG_START_PAYCYCLE"), DATE_FORMAT, "dd")) > 1) {
				secondArr = CF.getPrevPayCycleByOrg(con, strFinancialYearEnd, CF.getStrTimeZone(), CF, orgId);
			} else {
				secondArr = CF.getPayCycleFromDate(con, strFinancialYearEnd, CF.getStrTimeZone(), CF, orgId);
			}
			pst = con.prepareStatement("select emp_id,sum(reimbursement_amount) as reimbursement_amount " +
				"from emp_reimbursement where approval_1 =1 and ispaid=true and (ref_document is null or ref_document='' " +
				"or upper(ref_document) ='NULL') and from_date>=? and to_date<=? and emp_id=? group by emp_id");
			pst.setDate(1, uF.getDateFormat(firstArr[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(secondArr[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
//			System.out.println("pst====>"+pst);
			while(rs.next()) {
				hmReimbursementAmt.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("reimbursement_amount")));
			}
			rs.close();
			pst.close();
//			System.out.println("hmReimbursementAmt====>"+hmReimbursementAmt);
		}
		
		StringBuilder sbQuery=new StringBuilder();
		sbQuery.append("SELECT eod.emp_id,epd.emp_fname,epd.emp_mname,epd.emp_lname,epd.empcode,eod.depart_id,epd.emp_pan_no,epd.employment_end_date,epd.joining_date,epd.emp_address1,epd.emp_city_id FROM employee_official_details eod, employee_personal_details epd WHERE epd.emp_per_id=eod.emp_id  " +
			"and eod.emp_id in (select distinct emp_id from payroll_generation where financial_year_from_date = ? and financial_year_to_date = ?) and eod.emp_id=? ");
		sbQuery.append(" order by emp_id"); 
		pst = con.prepareStatement(sbQuery.toString());
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3, uF.parseToInt(getEmp_id()));
//		System.out.println("==generateForm16KPCA=====0========"+new Date());
//		System.out.println("pst=0=="+pst);
		rs = pst.executeQuery();
		
		Map<String, String> hmEmployeeMap = new HashMap<String, String>();
		Map<String, String> hmEmpJoiningDate = new HashMap<String, String>();
		Map<String, String> hmEmpCode = new HashMap<String, String>();
		Map<String, String> hmEmpDepartment = new HashMap<String, String>();
		Map<String, String> hmEmpPanNo = new HashMap<String, String>();
		Map<String, String> hmEmpEndDate = new HashMap<String, String>(); 
		Map<String, String> hmEmpAddressMap = new HashMap<String, String>();
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
			
			String empAddress = rs.getString("emp_address1")!=null && !rs.getString("emp_address1").equals("") ? rs.getString("emp_address1") : "";
			hmEmpAddressMap.put(rs.getString("emp_id"), empAddress+"\n"+uF.showData(rs.getString("emp_city_id"), ""));
		}
		rs.close();
		pst.close();
//		System.out.println("=======1========"+new Date());
		
		pst = con.prepareStatement("SELECT count(*) as cnt,emp_id FROM emp_family_members WHERE member_type='CHILD' and emp_id=? group by emp_id");
		pst.setInt(1, uF.parseToInt(getEmp_id()));
		rs = pst.executeQuery();
//		System.out.println(" pst==>"+pst);
		Map<String, String> hmEmpChildCnt = new HashMap<String, String>();
		while(rs.next()) {
			hmEmpChildCnt.put(rs.getString("emp_id"), rs.getString("cnt"));
		}
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("select count(*) as cnt,emp_id from (select distinct(paycycle),emp_id from payroll_generation pg " +
				"where financial_year_from_date=? and financial_year_to_date =? and emp_id=? and is_paid = true " +
				"and salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") " +
				" and earning_deduction='E' group by paycycle,emp_id order by emp_id,paycycle) a group by emp_id");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3, uF.parseToInt(getEmp_id()));
		rs = pst.executeQuery();
//		System.out.println(" pst==>"+pst);
		Map<String, String> hmMonthPaid = new HashMap<String, String>();
		while(rs.next()) {
			hmMonthPaid.put(rs.getString("emp_id"), rs.getString("cnt"));
		}
		rs.close();
		pst.close();
//		System.out.println(" hmMonthPaid==>"+hmMonthPaid);
		
		pst = con.prepareStatement("select distinct(pg.emp_id),pg.month,pg.year,pg.paycycle,pg.paid_days,pg.total_days from (" +
				"select max(paycycle) as paycycle,emp_id from (select distinct(paycycle),emp_id from payroll_generation pg " +
				"where financial_year_from_date=? and financial_year_to_date =? and is_paid = true " +
				"and salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") " +
				"and earning_deduction='E' and emp_id=? group by paycycle,emp_id order by emp_id,paycycle) a group by emp_id ) a,payroll_generation pg " +
				"where a.emp_id=pg.emp_id and a.paycycle=pg.paycycle and pg.emp_id in (select emp_per_id from employee_personal_details " +
				"where is_alive=false and employment_end_date between ? and ?) order by pg.emp_id");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3, uF.parseToInt(getEmp_id()));
		pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//		System.out.println(" pst==>"+pst);
		rs = pst.executeQuery();
		Map<String, String> hmLastPaycycle = new HashMap<String, String>();
		while(rs.next()) {
			hmLastPaycycle.put(rs.getString("emp_id"), rs.getString("emp_id"));
			hmLastPaycycle.put(rs.getString("emp_id")+"_MONTH", rs.getString("month"));
			hmLastPaycycle.put(rs.getString("emp_id")+"_YEAR", rs.getString("year"));
			hmLastPaycycle.put(rs.getString("emp_id")+"_PAYCYCLE", rs.getString("paycycle"));
			hmLastPaycycle.put(rs.getString("emp_id")+"_PAIDDAYS", rs.getString("paid_days"));
			hmLastPaycycle.put(rs.getString("emp_id")+"_TOTALDAYS", rs.getString("total_days"));
		}
		rs.close();
		pst.close();
//		System.out.println("hmLastPaycycle==>"+hmLastPaycycle);
		
		pst = con.prepareStatement("select * from employee_personal_details where employment_end_date between ? and ? and is_alive=false and emp_per_id=? order by emp_per_id"); 
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3, uF.parseToInt(getEmp_id()));
//		System.out.println(" pst==>"+pst);
		rs = pst.executeQuery();
		Map<String, String> hmExEmp = new HashMap<String, String>();
		while(rs.next()) {
			hmExEmp.put(rs.getString("emp_per_id"), uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));
		}
		rs.close();
		pst.close();
//		System.out.println("hmExEmp==>"+hmExEmp);
		
		Map hmPayrollDetails = new HashMap();			
//		List al = new ArrayList();			
//		double dblInvestmentExemption = 0.0d;
		
		Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
		pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1 ");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//		System.out.println(" pst==>"+pst);
		rs = pst.executeQuery();
		while(rs.next()) {
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
			
//			dblInvestmentExemption = 100000;
		}
		rs.close();
		pst.close();
//		System.out.println("=======2========"+new Date());
		
		pst = con.prepareStatement("select sum(amount) as amount,salary_head_id,emp_id,earning_deduction from payroll_generation pg where financial_year_from_date=? and financial_year_to_date =? and is_paid = true and emp_id=? and salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") group by salary_head_id, emp_id,earning_deduction order by emp_id,earning_deduction desc");
//		pst = con.prepareStatement("select sum(amount) as amount,salary_head_id,emp_id,earning_deduction from payroll_generation pg where " +
//			"financial_year_from_date=? and financial_year_to_date=? and is_paid = true and emp_id=? and salary_head_id not in " +
//			"("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") " +
//			" and paycycle not in (select paycycle from payroll_generation pg where financial_year_from_date=? and financial_year_to_date =? " +
//			"and is_paid = true and emp_id=? and salary_head_id in(451)) group by salary_head_id, emp_id,earning_deduction order by emp_id,earning_deduction desc");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3, uF.parseToInt(getEmp_id()));
//		pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//		pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//		pst.setInt(6, uF.parseToInt(getEmp_id()));
//		System.out.println(" pst==>"+pst);
		rs = pst.executeQuery();					
		String strEmpIdNew = null;
		String strEmpIdOld = null;
		Map hmInner1 = new HashMap();
		double dblGross = 0.0d;
		Map<String,String> hmLeaveEncashmet = new HashMap<String, String>();
		while(rs.next()) {
			strEmpIdNew = rs.getString("emp_id");
			
			if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
				hmInner1 = new HashMap();
//				dblGross = 0.0d;
				dblGross = uF.parseToDouble(hmReimbursementAmt.get(strEmpIdNew));
			}
							
			hmInner1.put(rs.getString("salary_head_id"), rs.getString("amount"));   
			if(rs.getString("earning_deduction").equalsIgnoreCase("E")) {
				dblGross += rs.getDouble("amount");
			}
			hmInner1.put("GROSS", dblGross+"");
			hmPayrollDetails.put(strEmpIdNew, hmInner1);
			
			if(uF.parseToInt(rs.getString("salary_head_id")) == LEAVE_ENCASHMENT && hmExEmp.containsKey(rs.getString("emp_id"))) { // && hmExEmp.containsKey(rs.getString("emp_id")) //  now leave encashment is added in other exemption for all employee 06 Jul 2018
				hmLeaveEncashmet.put(rs.getString("emp_id"), rs.getString("amount"));
			}
			
			strEmpIdOld = strEmpIdNew;
		}
		rs.close();
		pst.close();
//		System.out.println("hmPayrollDetails ===>> " + hmPayrollDetails);
//		System.out.println("=======3========"+new Date());
		
		Map<String, String> hmEmployerPF = new HashMap<String, String>();
		boolean IsAddEmployerPFInTDSCal = CF.getFeatureManagementStatus(request, uF, F_ADD_EMPLYOER_PF_IN_TDS_CALCLATION);
//		System.out.println("IsAddEmployerPFInTDSCal ===>> " + IsAddEmployerPFInTDSCal);
		if(IsAddEmployerPFInTDSCal) {
			pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? "
					+ "and org_id in (select org_id from employee_official_details where emp_id=?) and level_id in (select ld.level_id from grades_details gd, "
					+ "level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  "
					+ "and gd.grade_id in (select grade_id from employee_official_details where emp_id=? ))");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getEmp_id()));
			pst.setInt(4, uF.parseToInt(getEmp_id()));
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
//			System.out.println("hmEPFPolicy ===>> " + hmEPFPolicy);
			
			pst = con.prepareStatement("select pg.emp_id,sum(erpf_contribution) as erpf_contribution,sum(erps_contribution) as erps_contribution," +
					"sum(erdli_contribution) as erdli_contribution,sum(pf_admin_charges) as pf_admin_charges,sum(edli_admin_charges) as edli_admin_charges " +
					"from emp_epf_details eed,payroll_generation pg where eed.emp_id=pg.emp_id and financial_year_start=? and financial_year_end=? " +
					"and _month=month and financial_year_from_date=? and financial_year_to_date=? and pg.salary_head_id=? and pg.emp_id=? group by pg.emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, EMPLOYEE_EPF);
			pst.setInt(6, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
	 		while(rs.next()) {
				double dblERPF = hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_ERPF_CONTRIBUTION")) ? rs.getDouble("erpf_contribution") : 0.0d;
				dblERPF += hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_ERPS_CONTRIBUTION")) ? rs.getDouble("erps_contribution") : 0.0d;
				dblERPF += hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_ERDLI_CONTRIBUTION")) ? rs.getDouble("erdli_contribution") : 0.0d;
				dblERPF += hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_PF_ADMIN_CHARGES")) ? rs.getDouble("pf_admin_charges") : 0.0d;
				dblERPF += hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_EDLI_ADMIN_CHARGES")) ? rs.getDouble("edli_admin_charges") : 0.0d;
				
				hmEmployerPF.put(rs.getString("emp_id"), uF.formatIntoTwoDecimal(Math.round(dblERPF)));
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmployerPF =========>> " + hmEmployerPF);
		}
		
		
		pst = con.prepareStatement("select * from exemption_details where exemption_from=? and exemption_to=? and (slab_type=? or slab_type=2)");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3, uF.parseToInt(slabType));
		rs = pst.executeQuery();
//		System.out.println(" pst==>"+pst);
		Map hmExemption = new HashMap();
		Map<String, Map<String, List<String>>> hmExemptionDataUnderSection = new HashMap<String, Map<String, List<String>>>();
		while(rs.next()) {
//			hmExemption.put(rs.getString("exemption_name"), rs.getString("exemption_limit"));
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
			hmExemptionData.put(rs.getString("exemption_id"), innerList);
			hmExemptionDataUnderSection.put(rs.getString("under_section"), hmExemptionData);
		}
		rs.close();
		pst.close();
		
//		System.out.println("hmExemptionDataUnderSection ===>> " + hmExemptionDataUnderSection);
//		System.out.println("hmExemption ===>> " + hmExemption);
		
//		System.out.println("=======4========"+new Date());
		
		pst = con.prepareStatement("select * from section_details where under_section in (8,9) and financial_year_start=? and financial_year_end=? and (slab_type=? or slab_type=2) ");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3, uF.parseToInt(slabType));
		rs = pst.executeQuery();
//		System.out.println(" pst==>"+pst);
		List<String> chapter1SectionList = new ArrayList<String>();
		List<String> chapter2SectionList = new ArrayList<String>();
		Map<String, String> hmSectionPFApplicable = new HashMap<String, String>();
		while(rs.next()) {
			if(uF.parseToInt(rs.getString("under_section"))==8) {
				chapter1SectionList.add(rs.getString("section_id"));
			} else {
				chapter2SectionList.add(rs.getString("section_id"));
			}
			hmSectionPFApplicable.put(rs.getString("section_id"), rs.getString("is_pf_applicable"));
		}
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		rs = pst.executeQuery();
//		System.out.println(" pst==>"+pst);
		Map hmHRAExemption = new HashMap();
		while(rs.next()) {
			hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
			hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
			hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
			hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
		}
		rs.close();
		pst.close();
//		System.out.println("=======5========"+new Date());
		
		Map hmSectionLimitA = new HashMap();
		Map hmSectionLimitP = new HashMap();			
		Map hmSectionLimitEmp = new HashMap();
		Map<String, String> hmSectionAdjustedGrossIncomeLimitStatus = new HashMap<String, String>();
//		pst = con.prepareStatement(selectSection);
		pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and (slab_type=? or slab_type=2) order by section_code");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3, uF.parseToInt(slabType));
		rs = pst.executeQuery();
//		System.out.println(" pst==>"+pst);
		while (rs.next()) {
			
			if(rs.getString("section_limit_type").equalsIgnoreCase("A")) {
				hmSectionLimitA.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
				hmSectionLimitA.put(rs.getString("section_id")+"_CEILING_AMT", rs.getBoolean("is_ceiling_applicable") ? rs.getDouble("ceiling_amount") : "0");
			} else{
				hmSectionLimitP.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
				hmSectionLimitP.put(rs.getString("section_id")+"_CEILING_AMT", rs.getBoolean("is_ceiling_applicable") ? rs.getDouble("ceiling_amount") : "0");
			}
			hmSectionAdjustedGrossIncomeLimitStatus.put(rs.getString("section_id"), rs.getString("is_adjusted_gross_income_limit"));
		}
		rs.close();
		pst.close();
//		System.out.println("=======6========"+new Date());
//		dblInvestmentExemption = uF.parseToDouble(""+hmSectionLimitA.get("3"));
		
		//pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, section_details sd where sd.section_id=id.section_id and fy_from = ? and fy_to=? and status = true and section_code in ('HRA') group by emp_id ");
		pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, exemption_details ed " +
			"where ed.salary_head_id=id.salary_head_id and id.fy_from = ? and id.fy_to=? and status = true and ed.salary_head_id=? " +
			"and trail_status = 1 and ed.exemption_from=? and ed.exemption_to=? and emp_id=? and (ed.slab_type=? or ed.slab_type=2) and parent_section = 0 group by emp_id");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3, HRA);
		pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(6, uF.parseToInt(getEmp_id()));
		pst.setInt(7, uF.parseToInt(slabType));
		rs = pst.executeQuery();
//		System.out.println(" pst==>"+pst);
		Map hmRentPaid = new HashMap();
		while(rs.next()) {
			hmRentPaid.put(rs.getString("emp_id"), rs.getString("amount_paid"));
		}
		rs.close();
		pst.close();
//		System.out.println("hmRentPaid ===>> " + hmRentPaid);
		
//		System.out.println("=======7========"+new Date());
		
//		pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd  where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and section_code not in ('HRA') and sd.section_id not in (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') and isdisplay=true and parent_section=0 group by emp_id, sd.section_id order by emp_id ");
		pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
			"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.financial_year_start=? " +
			"and sd.financial_year_end=? and (sd.slab_type=? or sd.slab_type=2) and section_code not in ('HRA') and isdisplay=true and parent_section=0 and under_section=8 and emp_id=? group by emp_id, sd.section_id order by emp_id "); //and sd.section_id !=11
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(5, uF.parseToInt(slabType));
		pst.setInt(6, uF.parseToInt(getEmp_id()));
		rs = pst.executeQuery();
//		System.out.println(" pst under_section=8 ==>"+pst);
//		Map hmInvestment = new HashMap();
		Map<String, String> hmEmpExemptionsCH1Map = new HashMap<String, String>();
		Map<String, Map<String, String>> hmEmpInvestment = new HashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> hmEmpActualInvestment = new HashMap<String, Map<String, String>>();
		double dblInvestmentLimit = 0;
		double dblInvestmentCeilingLimit = 0;
		double dblInvestmentEmp = 0;
		
		while(rs.next()) {
			String strSectionId = rs.getString("section_id");
			double dblInvestment = rs.getDouble("amount_paid");
			
			if(hmSectionLimitA.containsKey(strSectionId)) {
				dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
				dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_CEILING_AMT"));
				if(dblInvestmentCeilingLimit>0) {
					dblInvestmentLimit = dblInvestmentCeilingLimit;
				}
			} else {
				dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
				dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
				dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_CEILING_AMT"));
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
			} else {
				dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH1Map.get(rs.getString("emp_id"))) + dblInvestment;
				hmEmpExemptionsCH1Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
				hmInvest.put(strSectionId, ""+dblInvestment);
			}
			hmEmpInvestment.put(rs.getString("emp_id"), hmInvest);
			
			Map<String,String> hmActualInvest=hmEmpActualInvestment.get(rs.getString("emp_id"));
			if(hmActualInvest==null) hmActualInvest=new HashMap<String, String>();
			hmActualInvest.put(strSectionId, ""+dblInvestment);
			
			hmEmpActualInvestment.put(rs.getString("emp_id"), hmActualInvest);
			
			if(rs.getString("emp_id").equals("32")) {
//				System.out.println("dblInvestment==="+dblInvestment);
//				System.out.println("dblInvestmentLimit==="+dblInvestmentLimit);
//				System.out.println("dblInvestmentEmp==="+dblInvestmentEmp);					
			}
		}
		rs.close();
		pst.close();			
//		System.out.println("=======8========"+new Date());
//		System.out.println("hmEmpInvestment ===>> " + hmEmpInvestment.get("32"));    
		
		
		Map<String, Map<String, Map<String, Map<String, String>>>> hmEmpIncludeSubSectionData = new HashMap<String, Map<String, Map<String, Map<String, String>>>>();
		pst = con.prepareStatement("select id.*, sd.section_id, sd.is_adjusted_gross_income_limit, sd.include_sub_section from investment_details id, section_details sd where sd.section_id=id.section_id and " +
			"id.fy_from=? and id.fy_to=? and status=true and trail_status=1 and sd.financial_year_start=? and sd.financial_year_end=? and (sd.slab_type=? or sd.slab_type=2) and section_code not in ('HRA')  and isdisplay=true and parent_section>0 " +
			"and emp_id in ("+getEmp_id()+") and sub_section_no>0 and (is_adjusted_gross_income_limit is null or is_adjusted_gross_income_limit=false) and (include_sub_section is not null or include_sub_section !='') order by emp_id,sd.section_id"); //and sd.section_id !=11
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(5, uF.parseToInt(slabType));
		rs = pst.executeQuery();
//		System.out.println(" pst==>"+pst);
		while(rs.next()) {
			String strSectionId = rs.getString("section_id");
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
		pst = con.prepareStatement("select id.*, sd.section_id, sd.is_adjusted_gross_income_limit, sd.include_sub_section from investment_details id, section_details sd where sd.section_id=id.section_id and " +
			"id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and (sd.slab_type=? or sd.slab_type=2) " +
			" and section_code not in ('HRA') and isdisplay=true and parent_section>0 and under_section=8 and emp_id in ("+getEmp_id()+") and sub_section_no>0 and " +
			"(is_adjusted_gross_income_limit is null or is_adjusted_gross_income_limit = false) order by emp_id,sd.section_id"); //and sd.section_id !=11
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(5, uF.parseToInt(slabType));
		rs = pst.executeQuery();
//		System.out.println(" pst==>"+pst);
		dblInvestmentLimit = 0;
		dblInvestmentEmp = 0;
		String oldSectionId = null;
		while(rs.next()) {
			String strSectionId = rs.getString("section_id");
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
				
//				---------------
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
//				--------------------
				
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
//				System.out.println(strSectionId+" -- dblSecInvestment =====>> "  +dblSecInvestment);
				hmInvest.put(strSectionId, ""+dblSecInvestment);
				hmEmpInvestment.put(rs.getString("emp_id"), hmInvest);
				
				hmSubSecMinusAmt.put(strSectionId, ""+dblSubSecMinusInvestment);
				hmEmpSubSecMinusAmt.put(rs.getString("emp_id"), hmSubSecMinusAmt);
				
				if(oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) {
					oldSectionId = strSectionId;
				}
				
			} else {
			
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
			
		//===start parvez date: 18-05-2022 Note: Repeated code===		
				/*if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
					dblSecInvestment += dblSubSecLimit;
				} else {
					dblSecInvestment += dblInvestment;
				}*/
		//===end parvez date: 18-05-2022===		
				hmInvest.put(strSectionId, ""+dblSecInvestment);
				hmEmpInvestment.put(rs.getString("emp_id"), hmInvest);
				
				if(oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) {
					oldSectionId = strSectionId;
				}
			}
		}
		rs.close();
		pst.close();
		
//		System.out.println("hmEmpInvestment ===>> " +hmEmpInvestment);
		
		pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
			" where sd.section_id=id.section_id and id.fy_from=? and id.fy_to=? and status=true and trail_status=1 and sd.financial_year_start=? " +
			" and sd.financial_year_end=? and (sd.slab_type=? or sd.slab_type=2) and section_code not in ('HRA') and isdisplay=true and parent_section=0 and under_section=9 and emp_id=? group by emp_id, sd.section_id order by emp_id"); //and sd.section_id !=11
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(5, uF.parseToInt(slabType));
		pst.setInt(6, uF.parseToInt(getEmp_id()));
		rs = pst.executeQuery();
//		System.out.println(" pst==>>>>>>>>>>>>> " + pst);
		Map<String, Map<String, String>> hmEmpInvestment1 = new HashMap<String, Map<String, String>>();
		Map<String, String> hmEmpExemptionsCH2Map = new HashMap<String, String>();
		Map<String, Map<String, String>> hmEmpActualInvestment1 = new HashMap<String, Map<String, String>>();
		dblInvestmentLimit = 0;
		dblInvestmentCeilingLimit = 0;
		dblInvestmentEmp = 0;
		while(rs.next()) {
			String strSectionId = rs.getString("section_id");
			double dblInvestment = rs.getDouble("amount_paid");
			
			if(hmSectionLimitA.containsKey(strSectionId)) {
				dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
				dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_CEILING_AMT"));
				if(dblInvestmentCeilingLimit>0) {
					dblInvestmentLimit = dblInvestmentCeilingLimit;
				}
			} else {
				dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
				dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
				dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_CEILING_AMT"));
				if(dblInvestmentCeilingLimit>0) {
					dblInvestmentLimit = dblInvestmentCeilingLimit;
				}
			}
			Map<String,String> hmInvest=hmEmpInvestment1.get(rs.getString("emp_id"));
			if(hmInvest==null) hmInvest=new HashMap<String, String>();
			
			if(dblInvestment>=dblInvestmentLimit && dblInvestmentLimit>=0) {
				dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
				hmEmpExemptionsCH2Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
				hmInvest.put(strSectionId, ""+dblInvestmentLimit);
			} else {
				dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestment;
				hmEmpExemptionsCH2Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
				hmInvest.put(strSectionId, ""+dblInvestment);
			}
			hmEmpInvestment1.put(rs.getString("emp_id"), hmInvest);
			
			Map<String,String> hmActualInvest=hmEmpActualInvestment1.get(rs.getString("emp_id"));
			if(hmActualInvest==null) hmActualInvest=new HashMap<String, String>();
			hmActualInvest.put(strSectionId, ""+dblInvestment);
			
			hmEmpActualInvestment1.put(rs.getString("emp_id"), hmActualInvest);
			
			if(rs.getString("emp_id").equals("93")) {
//				System.out.println("dblInvestment===>>>>>>>>>>>"+dblInvestment);
//				System.out.println("dblInvestmentLimit===>>>>>>>>>>>"+dblInvestmentLimit);
//				System.out.println("dblInvestmentEmp===>>>>>>>>>>>"+dblInvestmentEmp);					
			}
		}
		rs.close();
		pst.close();
//		System.out.println("hmEmpInvestment1 55 ===>>>>>>>>>>>> " + hmEmpInvestment1.get("55"));
		
		pst = con.prepareStatement("select id.*, sd.section_id, sd.is_adjusted_gross_income_limit, sd.include_sub_section from investment_details id, section_details sd where sd.section_id=id.section_id and " +
			"id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and (sd.slab_type=? or sd.slab_type=2) and section_code not in ('HRA') and " +
			"isdisplay=true and parent_section>0 and under_section=9 and emp_id=? and sub_section_no>0 and (is_adjusted_gross_income_limit is null or is_adjusted_gross_income_limit = false) order by emp_id, sd.section_id"); //and sd.section_id !=11
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(5, uF.parseToInt(slabType));
		pst.setInt(6, uF.parseToInt(getEmp_id()));
		rs = pst.executeQuery();
		dblInvestmentLimit = 0;
		dblInvestmentEmp = 0;
		oldSectionId = null;
		while(rs.next()) {
			String strSectionId = rs.getString("section_id");
			String strSubSecNo = rs.getString("sub_section_no");
			double dblInvestment = rs.getDouble("amount_paid");
			String strSubSecLimitType = rs.getString("sub_section_limit_type");
//					System.out.println("strSubSecLimitType ===>> "  +strSubSecLimitType);
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
				
//				---------------
				Map<String,String> hmSubSecMinusAmt = hmEmpSubSecMinusAmt.get(rs.getString("emp_id"));
				if(hmSubSecMinusAmt==null) hmSubSecMinusAmt = new HashMap<String, String>();
				
				Map<String,String> hmInvest = hmEmpInvestment1.get(rs.getString("emp_id"));
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
//				--------------------
				
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
//				System.out.println(strSectionId+" -- dblSecInvestment =====>> "  +dblSecInvestment);
				
				
				if(hmSectionLimitA.containsKey(strSectionId)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_CEILING_AMT"));
					if(dblInvestmentCeilingLimit>0) {
						dblInvestmentLimit = dblInvestmentCeilingLimit;
					}
				} else {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_CEILING_AMT"));
					if(dblInvestmentCeilingLimit>0) {
						dblInvestmentLimit = dblInvestmentCeilingLimit;
					}
				}
				
				if(dblSecInvestment>=dblInvestmentLimit && dblInvestmentLimit>=0) {
					hmInvest.put(strSectionId, ""+dblInvestmentLimit);
				} else {
					hmInvest.put(strSectionId, ""+dblSecInvestment);
				}
				hmEmpInvestment1.put(rs.getString("emp_id"), hmInvest);
				
				hmSubSecMinusAmt.put(strSectionId, ""+dblSubSecMinusInvestment);
				hmEmpSubSecMinusAmt.put(rs.getString("emp_id"), hmSubSecMinusAmt);
				
				if(oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) {
					oldSectionId = strSectionId;
				}
				
			} else {
			
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
	//					System.out.println("dblSecInvestment =====>> "  +dblSecInvestment);
				if(hmSectionLimitA.containsKey(strSectionId)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_CEILING_AMT"));
	//					System.out.println("dblInvestmentCeilingLimit ===>> " + dblInvestmentCeilingLimit);
					if(dblInvestmentCeilingLimit>0) {
						dblInvestmentLimit = dblInvestmentCeilingLimit;
					}
				} else {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_CEILING_AMT"));
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
		}
		rs.close();
		pst.close();
		
		
//		System.out.println("=======9========"+new Date());
		
		/*pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
				"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.section_id=3 " +
				"and isdisplay=true and parent_section=0 and sd.financial_year_start=? and sd.financial_year_end=? and emp_id=? and (sd.slab_type=? or sd.slab_type=2) group by emp_id, sd.section_id order by emp_id");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(5, uF.parseToInt(getEmp_id()));
		pst.setInt(6, uF.parseToInt(slabType));
		rs = pst.executeQuery();
//		System.out.println(" pst==>"+pst);
		Map<String, String> hmEmp80C = new HashMap<String, String>();
		dblInvestmentLimit = 0;
		dblInvestmentCeilingLimit = 0;
		dblInvestmentEmp = 0;
		
		while(rs.next()) {
			String strSectionId = rs.getString("section_id"); 
			double dblInvestment = rs.getDouble("amount_paid");
			
			if(hmSectionLimitA.containsKey(strSectionId)) {
				dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
				dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_CEILING_AMT"));
				if(dblInvestmentCeilingLimit>0) {
					dblInvestmentLimit = dblInvestmentCeilingLimit;
				}
			} else{
				dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
				dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
				dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_CEILING_AMT"));
				if(dblInvestmentCeilingLimit>0) {
					dblInvestmentLimit = dblInvestmentCeilingLimit;
				}
			}
			
			if(dblInvestment>=dblInvestmentLimit) {
				dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
				hmEmp80C.put(rs.getString("emp_id"), dblInvestmentLimit+"");
			} else{
				dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestment;
				hmEmp80C.put(rs.getString("emp_id"), dblInvestment+"");
			}
			
		}
		rs.close();
		pst.close();	*/		
//		System.out.println("hmEmpInvestment1==="+hmEmpInvestment1.get("654"));
//		System.out.println("=======10========"+new Date());
		
		/*pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
			"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to =? and status = true and trail_status = 1 and sd.section_id=1 " +
			"and isdisplay=true and parent_section=0 and sd.financial_year_start=? and sd.financial_year_end=? and emp_id=? and (sd.slab_type=? or sd.slab_type=2) group by emp_id, sd.section_id order by emp_id");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(5, uF.parseToInt(getEmp_id()));
		pst.setInt(6, uF.parseToInt(slabType));
		rs = pst.executeQuery();
//		System.out.println(" pst 80D ==>"+pst);
		Map<String, String> hmEmp80D = new HashMap<String, String>();
		dblInvestmentLimit = 0;
		dblInvestmentCeilingLimit = 0;
		dblInvestmentEmp = 0;
		
		while(rs.next()) {
			String strSectionId = rs.getString("section_id");
			double dblInvestment = rs.getDouble("amount_paid");
			
			if(hmSectionLimitA.containsKey(strSectionId)) {
				dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
				dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_CEILING_AMT"));
				if(dblInvestmentCeilingLimit>0) {
					dblInvestmentLimit = dblInvestmentCeilingLimit;
				}
			} else{
				dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
				dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
				dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_CEILING_AMT"));
				if(dblInvestmentCeilingLimit>0) {
					dblInvestmentLimit = dblInvestmentCeilingLimit;
				}
			}
			
			
			if(dblInvestment>=dblInvestmentLimit) {
				dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
				hmEmp80D.put(rs.getString("emp_id"), dblInvestmentLimit+"");
			} else{
				dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestment;
				hmEmp80D.put(rs.getString("emp_id"), dblInvestment+"");
			}
			
		}
		rs.close();
		pst.close();*/
//		System.out.println("=======11========"+new Date());
		
		pst = con.prepareStatement("select * from investment_details id, section_details sd where sd.section_id=id.section_id and id.fy_from=? and id.fy_to=? " +
			"and status = true and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and (sd.slab_type=? or sd.slab_type=2) and section_code not in ('HRA') and isdisplay=true " +
			"and parent_section>0 and emp_id=? order by emp_id"); //and sd.section_id !=11
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(5, uF.parseToInt(slabType));
		pst.setInt(6, uF.parseToInt(getEmp_id()));
		rs = pst.executeQuery();
//		System.out.println(" pst==>"+pst);
		
		Map<String, Map<String, List<Map<String, String>>>> hmEmpSubInvestment = new HashMap<String, Map<String, List<Map<String, String>>>>();
//		Map<String, List<Map<String, String>>> hmSubInvestment = new HashMap<String, List<Map<String, String>>>();	
		dblInvestmentLimit = 0;
		dblInvestmentEmp = 0;
		
		while(rs.next()) {
			String strSectionId = rs.getString("parent_section");
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
		
//		System.out.println("hmEmpSubInvestment==="+hmEmpSubInvestment.get("683"));
		
		
		
		/**
		 * HOME LOAN INTEREST EXEMPTION 
		 */
		/*pst = con.prepareStatement("select * from section_details where section_id = 11 and financial_year_start=? and financial_year_end=?");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		rs = pst.executeQuery();
//		System.out.println(" pst==>"+pst);
		double dblLoanExemptionLimit = 0;
		while (rs.next()) {
			dblLoanExemptionLimit = rs.getDouble("section_exemption_limit");
			if(rs.getBoolean("is_ceiling_applicable")) {
				dblLoanExemptionLimit = rs.getDouble("ceiling_amount");
			}
		}
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details where fy_from =? and fy_to =? and status = true" +
				" and trail_status = 1 and parent_section=0 and  section_id in (select section_id from section_details where section_id = 11 and financial_year_start=? " +
				"and financial_year_end=?) and emp_id=? group by emp_id");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(5, uF.parseToInt(getEmp_id()));
		rs = pst.executeQuery();
//		System.out.println(" pst==>"+pst);
		Map<String, String> hmEmpHomeLoanMap = new HashMap<String, String>();
		while (rs.next()) {
			if(uF.parseToDouble(rs.getString("amount_paid"))>dblLoanExemptionLimit) {
				hmEmpHomeLoanMap.put(rs.getString("emp_id"), dblLoanExemptionLimit+"");
			} else{
				hmEmpHomeLoanMap.put(rs.getString("emp_id"), rs.getString("amount_paid"));
			}
		}
		rs.close();
		pst.close();*/
		
//		System.out.println("=======12========"+new Date());
		/**
		 * HOME LOAN INTEREST EXEMPTION 
		 */
		
		Map<String,String> hmEmpIncomeFromOtherSourcesMap = new HashMap<String,String>();
		Map<String,String> hmEmpLessIncomeFromOtherSourcesMap = new HashMap<String,String>();
		pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
			"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and status = true and sd.section_id >=13 and sd.section_id <=17 " +
			"and parent_section = 0 and isdisplay=false and financial_year_start=? and financial_year_end=? and (sd.slab_type=? or sd.slab_type=2) and emp_id=? group by emp_id, sd.section_id order by emp_id");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(5, uF.parseToInt(slabType));
		pst.setInt(6, uF.parseToInt(getEmp_id()));
//		System.out.println("pst ========>> " + pst);
		rs = pst.executeQuery();			
		double dblInvestmentIncomeSourcesEmp = 0;		
		while (rs.next()) {
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
		
//		System.out.println("=======13========"+new Date());
		
//		Here is Under Section 10 & 16 Investment Detail Data Salary Headwise 
		
		pst = con.prepareStatement("select sum(amount_paid) as amount_paid, ed.salary_head_id, emp_id from investment_details id, exemption_details ed " +
			"where ed.salary_head_id=id.salary_head_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and status = true and under_section in (4,5) " +
			"and exemption_from=? and exemption_to=? and (ed.slab_type=? or ed.slab_type=2) and id.salary_head_id>0 and id.parent_section=0 and emp_id=? group by emp_id, ed.salary_head_id order by emp_id");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(5, uF.parseToInt(slabType));
		pst.setInt(6, uF.parseToInt(getEmp_id()));
//		System.out.println("pst========"+pst);
		rs = pst.executeQuery();			
		Map<String, Map<String, String>> hmUnderSection10_16Map = new HashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> hmUnderSection10_16PaidMap = new HashMap<String, Map<String, String>>();
		while (rs.next()) {
			String strsalaryheadid = rs.getString("salary_head_id");
			double dblInvestment = rs.getDouble("amount_paid");
			
			Map hmEmpPayrollDetails = (Map)hmPayrollDetails.get(getEmp_id());
			if(hmEmpPayrollDetails == null) hmEmpPayrollDetails = new HashMap();
			
			if(dblInvestment == 0) {
				dblInvestment = uF.parseToDouble((String)hmEmpPayrollDetails.get(strsalaryheadid));
			}
			dblInvestmentLimit = uF.parseToDouble((String)hmExemption.get(strsalaryheadid));
			double dblSalHeadAmt = uF.parseToDouble((String)hmEmpPayrollDetails.get(strsalaryheadid));
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
//		System.out.println("hmUnderSection10_16PaidMap ===>> " + hmUnderSection10_16PaidMap);
//		System.out.println("hmUnderSection10_16Map ===>> " + hmUnderSection10_16Map);
		
		
		pst = con.prepareStatement("select * from exemption_details where exemption_from = ? and exemption_to = ? and (slab_type=? or slab_type=2)");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3, uF.parseToInt(slabType));
//		System.out.println("pst========"+pst);
		rs = pst.executeQuery();			
		while (rs.next()) {
			String strsalaryheadid = rs.getString("salary_head_id");
			
			Map hmEmpPayrollDetails = (Map)hmPayrollDetails.get(getEmp_id());
			if(hmEmpPayrollDetails == null) hmEmpPayrollDetails = new HashMap();
			
			double dblInvestment = uF.parseToDouble((String)hmEmpPayrollDetails.get(strsalaryheadid));
			if(uF.parseToInt(strsalaryheadid) == LTA) {
				dblInvestmentLimit = dblInvestment;
			} else {
				dblInvestmentLimit = uF.parseToDouble((String)hmExemption.get(strsalaryheadid));
			}
			Map<String, String> hmInner = (Map<String, String>)hmUnderSection10_16Map.get(getEmp_id());
			if(hmInner==null) hmInner = new HashMap<String, String>();
			
			double dblAllowanceExempt = Math.min(dblInvestment, dblInvestmentLimit);
			
			if(!hmInner.containsKey(rs.getString("salary_head_id"))) {
				hmInner.put(rs.getString("salary_head_id"), ""+dblAllowanceExempt);				
				hmUnderSection10_16Map.put(getEmp_id(), hmInner);
			}
			Map<String, String> hmInner11 = (Map<String, String>)hmUnderSection10_16PaidMap.get(getEmp_id());
			if(hmInner11 == null) hmInner11 = new HashMap<String, String>();
			
			if(!hmInner.containsKey(rs.getString("salary_head_id"))) {
				hmInner11.put(rs.getString("salary_head_id"), ""+dblInvestment);				
				hmUnderSection10_16PaidMap.put(getEmp_id(), hmInner11);
			}
		} 
		rs.close();
		pst.close();
		
//		System.out.println("1 hmUnderSection10_16PaidMap ===>> " + hmUnderSection10_16PaidMap);
//		System.out.println("1 hmUnderSection10_16Map ===>> " + hmUnderSection10_16Map);
		
//		System.out.println("=======13========"+new Date());
//		System.out.println("hmUnderSection10Map====="+hmUnderSection10Map);
		Map<String, String> hmUS10_16_SalHeadData = new HashMap<String, String>();
		Map<String, Map<String, String>> hmSectionwiseSubSecAdjusted10PerLimitAmt = new HashMap<String, Map<String,String>>();
		
		Map hmTaxLiability = new HashMap();
		Set set = hmPayrollDetails.keySet();
		Iterator it = set.iterator();
		while(it.hasNext()) {
			Map hmTaxInner = new HashMap();
			String strEmpId = (String)it.next();
			Map hmEmpPayrollDetails = (Map)hmPayrollDetails.get(strEmpId);
			if(hmEmpPayrollDetails == null) hmEmpPayrollDetails = new HashMap();
			
			String strLevel = CF.getEmpLevelId(con, strEmpId);
			int nEmpOrgId = uF.parseToInt(CF.getEmpOrgId(con, uF, strEmpId));
			
			double dblPerkAlignTDSAmount = CF.getPerkAlignTDSAmount(con, CF,uF, uF.parseToInt(strEmpId), strFinancialYearStart, strFinancialYearEnd, nEmpOrgId, uF.parseToInt(strLevel));
//			System.out.println("dblPerkAlignTDSAmount====>"+dblPerkAlignTDSAmount);
			
			double dblBasic = uF.parseToDouble((String)hmEmpPayrollDetails.get(BASIC+""));
			double dblDA = uF.parseToDouble((String)hmEmpPayrollDetails.get(DA+""));
			
			String strTDSAmt = (String)hmSalaryHeadMap.get(TDS+"");
			
//			String strConveyanceAllowance = (String)hmSalaryHeadMap.get(CONVEYANCE_ALLOWANCE+"");
//			String strProfessionalTax = (String)hmSalaryHeadMap.get(PROFESSIONAL_TAX+"");
			
			double dblGross1 = uF.parseToDouble((String)hmEmpPayrollDetails.get("GROSS"));
//			System.out.println("1 dblGross1====>"+dblGross1);
			dblGross1 = dblGross1 - dblPerkAlignTDSAmount;
			dblGross = dblGross - dblPerkAlignTDSAmount;
//			System.out.println("2 dblGross1====>"+dblGross1);
			
//			BY RAHUL PATIL
			
//			double dblGratuity = 0.0d;
//			if(uF.parseToInt(strEmpId) == 589 && strFinancialYearStart.trim().equals("01/04/2016") && strFinancialYearEnd.trim().equals("31/03/2017")) {
//				dblGratuity = 14423d;
//				dblGross1 = dblGross1 + dblGratuity; 
//				dblGross = dblGross + dblGratuity; 
//			}
			
			hmEmpPayrollDetails.put("GROSS",""+dblGross1);
			
//			double dblProfessionalTaxPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(PROFESSIONAL_TAX+""));
//			double dblProfessionalTaxExemptLimit = uF.parseToDouble((String)hmExemption.get(PROFESSIONAL_TAX+""));
//			double dblProfessionalTaxExempt = Math.min(dblProfessionalTaxPaid, dblProfessionalTaxExemptLimit);
//			System.out.println("dblProfessionalTaxExempt ==================>> " + dblProfessionalTaxExempt);
			
			
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
			
	//===start parvez date: 18-05-2022===		
			double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_EDU_TAX"));
			double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_STD_TAX"));
	//===end parvez date: 18-05-2022===		
			double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_FLAT_TDS"));
			
			// Exemptions needs to worked out as other exemptions				
//			double dblHomeLoanTaxExempt = uF.parseToDouble(hmEmpHomeLoanMap.get(strEmpId));

			double dblEPFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EMPLOYEE_EPF+""));
			
//			double dblEPRFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EMPLOYER_EPF+""));
			double dblEPRFPaid = uF.parseToDouble(hmEmployerPF.get(strEmpId));
//			System.out.println("dblEPRFPaid ===>> " + dblEPRFPaid);
			if(hmEmpPayrollDetails.containsKey(EMPLOYER_EPF+"")) {
				dblEPRFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EMPLOYER_EPF+""));
//				System.out.println("in if dblEPRFPaid ===>> " + dblEPRFPaid);
			}
			
			double dblEPVFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(VOLUNTARY_EPF+""));
			
//			double dblChapterVIA1 = uF.parseToDouble(hmEmpExemptionsCH1Map.get(strEmpId));
//			double dblChapterVIA2 = uF.parseToDouble(hmEmpExemptionsCH2Map.get(strEmpId));
			/**
			 * Change by RAHUL PATIL on 31Aug18 based on Crave Infotech Case found
			 * */
//			double dbl80C = uF.parseToDouble(hmEmp80C.get(strEmpId));
//			double dbl80D = uF.parseToDouble(hmEmp80D.get(strEmpId));
			
//			double dblTotalInvestment =dblChapterVIA1+dblChapterVIA2;
//			double dblTotalInvestment =dblChapterVIA1+dblChapterVIA2 + dblEPFPaid;
			/**
			 * Change by RAHUL PATIL on 31Aug18 based on Crave Infotech Case found
			 * */
//			double dblTotalInvestment =dbl80C + dblEPFPaid + dblEPRFPaid + dblEPVFPaid;
			double dblEmpPF = dblEPFPaid + dblEPRFPaid + dblEPVFPaid;
//			double dblTotalInvestment =0.0d; //dblInvestment + dblEPFPaid;
			/**
			 * Change by RAHUL PATIL on 31Aug18 based on Crave Infotech Case found
			 * */
//			double dblInvestmentExempt = Math.min(dblTotalInvestment, dblInvestmentExemption);
			
			if(strEmpId.equals("93")) {
//				System.out.println("dblInvestment==="+dblInvestment);
//				System.out.println("dblEPFPaid==="+dblEPFPaid);
//				System.out.println("dblEPRFPaid==="+dblEPRFPaid);
//				System.out.println("dblEPVFPaid==="+dblEPVFPaid);
//				System.out.println("dblInvestmentExempt==="+dblInvestmentExempt);
//				System.out.println("dblTotalInvestment===="+dblTotalInvestment);
//				System.out.println("dblInvestmentExemption="+dblInvestmentExemption);
			}
//			
			
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
//					System.out.println("pst========"+pst);
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
//			double dblConveyanceAllowanceLimit = strEmpId.equals("924") ? 15071 : (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) * uF.parseToInt(hmMonthPaid.get(strEmpId));
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
			
//			String strHRA = (String)hmSalaryHeadMap.get(HRA+"");
			String strHRA = (String)hmEmpPayrollDetails.get(HRA+"");
			
			String strCondition1 = (String)hmHRAExemption.get("CONDITION_1");
			String strCondition2 = (String)hmHRAExemption.get("CONDITION_2");
			String strCondition3 = (String)hmHRAExemption.get("CONDITION_3");
			
			double dblCondition1 = uF.parseToDouble(strCondition1);
			double dblCondition2 = uF.parseToDouble(strCondition2);
			double dblCondition3 = uF.parseToDouble(strCondition3);
			
//			double dblHRA1 = dblCondition1 * (dblBasic + dblDA) / 100;
//			double dblHRA2 = dblCondition2 * (dblBasic + dblDA) / 100;
//			double dblHRA3 = dblCondition3 * (dblBasic + dblDA) / 100;
			double dblHRA1 = dblCondition1 * dblHraSalHeadsAmount / 100;
			double dblHRA2 = dblCondition2 * dblHraSalHeadsAmount / 100;
			double dblHRA3 = dblCondition3 * dblHraSalHeadsAmount / 100;
			
//			System.out.println("dblHraSalHeadsAmount ===>> " + dblHraSalHeadsAmount);
//			System.out.println("dblHRA1 ===>> " + dblHRA1);
//			System.out.println("dblHRA2 ===>> " + dblHRA2);
//			System.out.println("dblHRA3 ===>> " + dblHRA3);
			double dblActualHRAPaid = uF.parseToDouble(strHRA);
			double dblActualRentPaid = uF.parseToDouble((String)hmRentPaid.get(strEmpId));
//			System.out.println("dblActualRentPaid ===>> " + dblActualRentPaid);
			
			double dblHRAExemption = Math.min(dblActualHRAPaid, dblActualRentPaid);
			boolean isMetro = uF.parseToBoolean((String)hmEmpMertoMap.get(strEmpId));
			double dblHRA = 0;
			if(isMetro) {
				if(dblHRA1<dblHRA2) {
					dblHRA = dblHRA1; 
					hmTaxInner.put("dblHRA", "Rent paid over "+dblCondition1+"% of salary");
				} else {
					dblHRA = dblHRA2;
					hmTaxInner.put("dblHRA", "");
					hmTaxInner.put("dblHRA", dblCondition2+"% of salary in metro cities");
				}
			} else {
				if(dblHRA1<dblHRA3) {
					dblHRA = dblHRA1;
					hmTaxInner.put("dblHRA", "");
					hmTaxInner.put("dblHRA", "Rent paid over "+dblCondition1+"% of salary");
				} else {
					dblHRA = dblHRA3;
					hmTaxInner.put("dblHRA", "");
					hmTaxInner.put("dblHRA", dblCondition3+"% of salary in other cities");
				}
			}
			
//			System.out.println("dblHRAExemption 1 ==========>> " + dblHRAExemption + " --- dblHRA ====>> " + dblHRA);
			
			dblHRAExemption = Math.min(dblHRAExemption, dblHRA);   
//			System.out.println("dblHRAExemption 2 ===>> " + dblHRAExemption);
			ApprovePayroll objApprovePayroll = new ApprovePayroll();
			objApprovePayroll.setServletRequest(request);
			objApprovePayroll.session = session;
			objApprovePayroll.CF = CF;
			
			Map hmEmpRentPaidMap = objApprovePayroll.getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
//			double dblHRAExemptions = objApprovePayroll.getHRAExemptionCalculation(con, uF,null, hmEmpPayrollDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, 0, 0, hmEmpMertoMap, hmEmpRentPaidMap);
			double dblHRAExemptions = objApprovePayroll.getHRAExemptionCalculation(con, uF,null, hmEmpPayrollDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, dblActualHRAPaid, dblHraSalHeadsAmount, hmEmpMertoMap, hmEmpRentPaidMap);
			dblHRAExemption = dblHRAExemptions;
			
//			System.out.println("dblHRAExemption 3 ==========>> " + dblHRAExemption);
			hmUS10_16_SalHeadData.put(HRA+"_PAID", "");
			hmUS10_16_SalHeadData.put(HRA+"_EXEMPT", ""+dblHRAExemption);
			
//			System.out.println("hmUS10_16_SalHeadData ==========>> " + hmUS10_16_SalHeadData);
			
			double dblIncomeFromOther = uF.parseToDouble(hmEmpIncomeFromOtherSourcesMap.get(strEmpId));
			double dblLessIncomeFromOther = uF.parseToDouble(hmEmpLessIncomeFromOtherSourcesMap.get(strEmpId));
			
			double dblUS10_16Exempt = 0.0d;
			Map<String, String> hmUS10Inner= (Map<String, String>)hmUnderSection10_16Map.get(getEmp_id());
	        if(hmUS10Inner==null) hmUS10Inner=new HashMap<String, String>();
	        
	        Map<String, String> hmUS10InnerPaid= (Map<String, String>)hmUnderSection10_16PaidMap.get(getEmp_id());
			if(hmUS10InnerPaid==null) hmUS10InnerPaid=new HashMap<String, String>();
			
			if(hmEmpPayrollDetails.containsKey(REIMBURSEMENT+"")) {
//				System.out.println("REIMBURSEMENT ===>> " + hmUS10InnerPaid.get(REIMBURSEMENT+""));
				hmUS10_16_SalHeadData.put(REIMBURSEMENT+"_PAID", hmUS10InnerPaid.get(REIMBURSEMENT+""));
				hmUS10_16_SalHeadData.put(REIMBURSEMENT+"_EXEMPT", hmUS10InnerPaid.get(REIMBURSEMENT+""));
			}
			
//			System.out.println("hmExemptionDataUnderSection ===>> " + hmExemptionDataUnderSection);
			Iterator<String> itUnderSection = hmExemptionDataUnderSection.keySet().iterator();
	        while (itUnderSection.hasNext()) {
	        	String underSectionId = itUnderSection.next();
		        Map<String, List<String>> hmUS10ExemptionData = hmExemptionDataUnderSection.get(underSectionId);
		        Iterator<String> itUS10Examption = hmUS10ExemptionData.keySet().iterator();
		        while (itUS10Examption.hasNext()) {
					String exemptionId = itUS10Examption.next();
					List<String> innerList = hmUS10ExemptionData.get(exemptionId);
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
//			System.out.println("dblUS10_16Exempt ===>> " + dblUS10_16Exempt);
	        
			/*Map<String, String> hmUS10Inner= (Map<String, String>)hmUnderSection10Map.get(strEmpId);
			if(hmUS10Inner==null) hmUS10Inner=new HashMap<String, String>();
			double dblMedicalAllowanceExemptLimit =uF.parseToDouble((String)hmUS10Inner.get(""+MEDICAL_ALLOWANCE));
			
			Map<String, String> hmUS10InnerPaid= (Map<String, String>)hmUnderSection10PaidMap.get(strEmpId);
			if(hmUS10InnerPaid==null) hmUS10InnerPaid=new HashMap<String, String>();
			double dblMedicalAllowancePaid =uF.parseToDouble((String)hmUS10InnerPaid.get(""+MEDICAL_ALLOWANCE));
			double dblMedicalAllowanceExempt = Math.min(dblMedicalAllowancePaid, dblMedicalAllowanceExemptLimit);
			hmUS10_16_SalHeadData.put(MEDICAL_ALLOWANCE+"_PAID", ""+dblMedicalAllowancePaid);
			hmUS10_16_SalHeadData.put(MEDICAL_ALLOWANCE+"_EXEMPT", ""+dblMedicalAllowanceExempt);
			
			double dblLTAExemptLimit =uF.parseToDouble((String)hmUS10Inner.get(""+LTA));
			double dblLTAPaid =uF.parseToDouble((String)hmUS10InnerPaid.get(""+LTA));
			double dblLTAExempt = Math.min(dblLTAPaid, dblLTAExemptLimit);
			hmUS10_16_SalHeadData.put(LTA+"_PAID", ""+dblLTAPaid);
			hmUS10_16_SalHeadData.put(LTA+"_EXEMPT", ""+dblLTAExempt);*/
			
			double dblVIA2Exempt = 0.0d;
			double dblVIA1Exempt = 0.0d;
			double dblAddExemptInAdjustedGrossIncome = 0.0d;
			
			dblInvestmentLimit = 0;
			
//			Here we Calculate VI A1
			/**
			 * Change by RAHUL PATIL on 31Aug18 based on Crave Infotech Case found
			 * */
//			System.out.println("hmEmpInvestment(strEmpId) 55 ================>> " + hmEmpInvestment.get(strEmpId));
			Map<String,String> hmInvest = hmEmpInvestment.get(strEmpId);
			if(hmInvest == null) hmInvest = new HashMap<String, String>();
			Iterator<String> it1 = hmInvest.keySet().iterator();
			List<String> alSectionId = new ArrayList<String>();
//			System.out.println("hmSectionPFApplicable ============>> " + hmSectionPFApplicable);
			while(it1.hasNext()) {
				String strSectionId = it1.next();
//				System.out.println("strSectionId ===================>> " + strSectionId);
				if(hmSectionLimitA.containsKey(strSectionId)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
					double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
//					System.out.println("form16/1888---slabType="+slabType);
					if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId))) {
						alSectionId.add(strSectionId);
						dblVIA1Invest += dblEmpPF;
					}
//					System.out.println("strSectionId ===>> " + strSectionId + " -- dblInvestmentLimit ===>> " + dblInvestmentLimit);
					if(dblInvestmentLimit>=0) {
						dblVIA1Invest = Math.min(dblVIA1Invest, dblInvestmentLimit);
					}
					if(uF.parseToBoolean(hmSectionAdjustedGrossIncomeLimitStatus.get(strSectionId))) {
						dblAddExemptInAdjustedGrossIncome += dblVIA1Invest;
					}
					
//					System.out.println("dblVIA1Invest ===================>> " + dblVIA1Invest);
					dblVIA1Exempt += dblVIA1Invest;
//					System.out.println("dblVIA1Exempt ===================>> " + dblVIA1Exempt);
				} else {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
//					System.out.println("form16/1907---slabType="+slabType);
					if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId))) {
						alSectionId.add(strSectionId);
						dblVIA1Invest += dblEmpPF;
					}
					dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
					if(dblInvestmentLimit>=0) {
						dblVIA1Invest = Math.min(dblVIA1Invest, dblInvestmentLimit);
					}
					if(uF.parseToBoolean(hmSectionAdjustedGrossIncomeLimitStatus.get(strSectionId))) {
						dblAddExemptInAdjustedGrossIncome += dblVIA1Invest;
					}
//					System.out.println("dblVIA1Invest else ===================>> " + dblVIA1Invest);
					dblVIA1Exempt += dblVIA1Invest;
//					System.out.println("dblVIA1Exempt else ===================>> " + dblVIA1Exempt);
					
				}
			
				
				/*if(hmSectionLimitA.containsKey(strSectionId)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
					double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
					if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId))) {
						dblVIA1Invest += dblEmpPF;
					}
					dblVIA1Exempt += Math.min(dblVIA1Invest, dblInvestmentLimit);
				} else {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
					if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId))) {
						dblVIA1Invest += dblEmpPF;
					}
					dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
					dblVIA1Exempt += Math.min(dblVIA1Invest, dblInvestmentLimit);
				}*/
				
			}
			/*if(alSectionId == null || alSectionId.size()==0) {
				dblVIA1Exempt += dblEmpPF;
			}*/
			
//			Here we Calculate VI A2 
			/**
			 * Change by RAHUL PATIL on 31Aug18 based on Craveinfotech Case found
			 * */
//			System.out.println("hmEmpInvestment1.get(strEmpId) 55 ================>> " + hmEmpInvestment1.get(strEmpId));
			Map<String,String> hmInvest2 = hmEmpInvestment1.get(strEmpId);
			if(hmInvest2 == null) hmInvest2 = new HashMap<String, String>();
			Iterator<String> it2 = hmInvest2.keySet().iterator();
			while(it2.hasNext()) {
				String strSectionId = it2.next();
				if(uF.parseToBoolean(hmSectionAdjustedGrossIncomeLimitStatus.get(strSectionId))) {
					dblAddExemptInAdjustedGrossIncome += uF.parseToDouble(hmInvest2.get(strSectionId));;
				}
				dblVIA2Exempt += uF.parseToDouble(hmInvest2.get(strSectionId));
			}
			if(strEmpId.equals("55")) {
//				System.out.println("dblGross1==="+dblGross1);
//				System.out.println("dblProfessionalTaxExempt==="+dblProfessionalTaxExempt);
//				System.out.println("dblHRAExemption==="+dblHRAExemption);
//				System.out.println("dblConveyanceAllowanceExempt==="+dblConveyanceAllowanceExempt);
//				System.out.println("dblInvestmentExempt==="+dblInvestmentExempt);
//				System.out.println("dblHomeLoanTaxExempt===="+dblHomeLoanTaxExempt); 
//				System.out.println("dbl80D="+dbl80D);
//				System.out.println("dblEducationAllowanceExempt="+dblEducationAllowanceExempt);
//				System.out.println("dblMedicalAllowanceExempt="+dblMedicalAllowanceExempt);
//				System.out.println("dblLTAExempt="+dblLTAExempt);
//				System.out.println("dblOtherExemptions="+dblOtherExemptions);
//				System.out.println("dblInvest="+dblInvest);
			}
			
			double dblAdjustedGrossTotalIncome = (dblPrevOrgGross + dblGross1 + dblAddExemptInAdjustedGrossIncome + dblIncomeFromOther) - dblUS10_16Exempt - dblVIA1Exempt - dblVIA2Exempt - dblLessIncomeFromOther; //- dblHomeLoanTaxExempt
//			System.out.println("dblAdjustedGrossTotalIncome ===>> " + dblAdjustedGrossTotalIncome);
			request.setAttribute("dblAdjustedGrossTotalIncome", ""+dblAdjustedGrossTotalIncome);
			
			pst = con.prepareStatement("select * from section_details where financial_year_start=? and financial_year_end=? and (slab_type=? or slab_type=2)" +
				" and section_code not in ('HRA') and isdisplay=true and is_adjusted_gross_income_limit=true "); //and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(slabType));
//			System.out.println(" pst==>"+pst);
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
//			System.out.println(" hmSectionwiseSubSecIsAdjustedStatus ===>> " + hmSectionwiseSubSecIsAdjustedStatus);
			
			
			Map<String, List<Map<String, String>>> hmSubInvestment = hmEmpSubInvestment.get(strEmpId);
			if(hmSubInvestment ==null)hmSubInvestment = new HashMap<String, List<Map<String,String>>>();
			
			Map<String,String> hmInvest11 = new HashMap<String, String>();
			
			Iterator<String> itSubSec = hmSectionwiseSubSecIsAdjustedStatus.keySet().iterator();
			
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
					double dblInvestment = uF.parseToDouble(hm.get("PAID_AMOUNT"));
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
//						System.out.println(dblSubSecLimit+ " -- dbl10PerOfAdjustedIncome ===>> " + dbl10PerOfAdjustedIncome);
						if(hm.get("SUB_SECTION_LIMIT_TYPE") != null && hm.get("SUB_SECTION_LIMIT_TYPE").equals("%")) {
							dblSubSecLimit = (dbl10PerOfAdjustedIncome * dblSubSecLimit) / 100;
						}
//						System.out.println("dblSubSecLimit ===>> " +dblSubSecLimit +" -- dblInvestment ===>> " + dblInvestment);
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
//			System.out.println("hmSectionwiseSubSecAdjusted10PerLimitAmt ===>> " +hmSectionwiseSubSecAdjusted10PerLimitAmt);
			
			request.setAttribute("hmSectionwiseSubSecAdjusted10PerLimitAmt", hmSectionwiseSubSecAdjusted10PerLimitAmt);
//			System.out.println("hmInvest11 ===>> " + hmInvest11);
			
			double dblVIA1ExemptIsAdjustedLimit = 0.0d;
			
			Iterator<String> it11 = hmInvest11.keySet().iterator();
			while(it11.hasNext()) {
				String strSectionId = it11.next();
				
				if(hmSectionLimitA.containsKey(strSectionId)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
					double dblVIA1Invest = uF.parseToDouble(hmInvest11.get(strSectionId));
//					System.out.println("form16/2097---slabType="+slabType);
					if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId))) {
						alSectionId.add(strSectionId);
						dblVIA1Invest += dblEmpPF;
					}
//					System.out.println("strSectionId ===>> " + strSectionId + " -- dblInvestmentLimit ===>> " + dblInvestmentLimit);
					if(dblInvestmentLimit>=0) {
						dblVIA1Invest = Math.min(dblVIA1Invest, dblInvestmentLimit);
					}
//					System.out.println("dblVIA1Invest ===>> " + dblVIA1Invest);
					dblVIA1ExemptIsAdjustedLimit += dblVIA1Invest;
				} else {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					double dblVIA1Invest = uF.parseToDouble(hmInvest11.get(strSectionId));
//					System.out.println("form16/2111---slabType="+slabType);
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
//				System.out.println("dblEmpPF : =======================+>> " + dblEmpPF);
				dblVIA1Exempt += dblEmpPF;
//				System.out.println("dblVIA1Exempt dblEmpPF : =======================+>> " + dblVIA1Exempt);
			}
			
//			int slabType = uF.parseToInt(hmEmpSlabMap.get(strEmpId));
			//System.out.println("strSlabType:"+slabType);
			if(strEmpId.equals("329")) {
//				System.out.println("dblPrevOrgGross==="+dblPrevOrgGross);
//				System.out.println("dblGross1==="+dblGross1);
//				System.out.println("dblAddExemptInAdjustedGrossIncome==="+dblAddExemptInAdjustedGrossIncome);
//				System.out.println("dblIncomeFromOther==="+dblIncomeFromOther);
//				System.out.println("dblUS10_16Exempt==="+dblUS10_16Exempt);
//				System.out.println("dblVIA1Exempt==="+dblVIA1Exempt);
//				System.out.println("dblVIA2Exempt==="+dblVIA2Exempt);
//				System.out.println("dblVIA1ExemptIsAdjustedLimit==="+dblVIA1ExemptIsAdjustedLimit);
			}
			
			double dblNetTaxableIncome = (dblPrevOrgGross + dblGross1 + dblAddExemptInAdjustedGrossIncome + dblIncomeFromOther)- dblUS10_16Exempt - dblVIA1Exempt - dblVIA2Exempt - dblVIA1ExemptIsAdjustedLimit; //- dblHomeLoanTaxExempt
			
//			double dblNetTaxableIncome = dblGross1 - dblUS10_16Exempt - dblVIA1Exempt - dblVIA2Exempt; // dbl80D -  - dblHomeLoanTaxExempt
			
//			double dblNetTaxableIncome = dblGross1 - dblProfessionalTaxExempt - dblHRAExemption - dblConveyanceAllowanceExempt - dblInvestmentExempt - 
//				dblHomeLoanTaxExempt - dblEducationAllowanceExempt - dblMedicalAllowanceExempt -dblLTAExempt - dblOtherExemptions - dblInvest; // dbl80D -
//			double dblNetTaxableIncome = dblGross1 - dblProfessionalTaxExempt - dblHRAExemption - dblConveyanceAllowanceExempt - dblInvestmentExempt - dblOtherExemptions - dblHomeLoanTaxExempt - dblIncomeFromOther;
			
			if(strEmpId.equalsIgnoreCase("55")) {
//				System.out.println(strEmpId+" dblNetTaxableIncome=====>"+dblNetTaxableIncome);  
			}
			
			hmTaxInner.put("dblIncomeFromOther", dblIncomeFromOther+"");  
			hmTaxInner.put("dblActualHRAPaid", dblActualHRAPaid+"");  
			hmTaxInner.put("dblActualRentPaid", dblActualRentPaid+"");
			hmTaxInner.put("dblHRA1", dblHRA+"");
			hmTaxInner.put("dblCondition1", dblCondition1+"");
			hmTaxInner.put("dblHRAExemption", dblHRAExemption+"");
	//===start parvez date: 31-03-2022===		
			hmTaxInner.put("dblPrevOrgGross", dblPrevOrgGross+"");
			hmTaxInner.put("dblPrevOrgTDSAmount", dblPrevOrgTDSAmount);
	//===end parvez date: 31-03-2022===		
			
//			hmTaxInner.put("dblProfessionalTaxPaid", dblProfessionalTaxPaid+"");
//			hmTaxInner.put("dblProfessionalTaxExempt", dblProfessionalTaxExempt+"");
			
			hmTaxInner.put("dblConveyanceAllowancePaid", dblConveyanceAllowancePaid+"");
			hmTaxInner.put("dblConveyanceAllowanceExempt", dblConveyanceAllowanceExempt+"");
			
//			hmTaxInner.put("dblOtherExemptions", dblOtherExemptions+"");
//			hmTaxInner.put("dblHomeLoanTaxExempt", dblHomeLoanTaxExempt+"");
			
			hmTaxInner.put("dblInvestment", dblVIA1Exempt+"");
//			hmTaxInner.put("dblInvestment", hmEmpExemptionsMap.get(strEmpId) +"");
			hmTaxInner.put("hmEmpExemptionsCH1Map", hmEmpExemptionsCH1Map.get(strEmpId) +"");
			hmTaxInner.put("hmEmpExemptionsCH2Map", hmEmpExemptionsCH2Map.get(strEmpId) +"");
			hmTaxInner.put(TDS, strTDSAmt);
			
			hmTaxInner.put("dblEducationAllowancePaid", dblEducationAllowancePaid+"");
			hmTaxInner.put("dblEducationAllowanceExempt", dblEducationAllowanceExempt+""); 
			
//			hmTaxInner.put("dblMedicalAllowanceExempt", dblMedicalAllowanceExempt+"");
//			hmTaxInner.put("dblMedicalAllowancePaid", dblMedicalAllowancePaid+"");
			hmTaxInner.put("dblEmpPF", dblEmpPF+""); 
			
//			hmTaxInner.put("dblLTAExempt", dblLTAExempt+"");
//			hmTaxInner.put("dblLTAPaid", dblLTAPaid+"");

			double TDSPayable = 0;
			if(strEmpId!=null && strEmpId.equalsIgnoreCase("231")) {					
//				System.out.println("==================================================================================");
//				System.out.println("dblGross="+dblGross1);
//				System.out.println("dblProfessionalTaxExempt="+dblProfessionalTaxExempt);
//				System.out.println("dblHRAExemption="+dblHRAExemption);
//				System.out.println("dblConveyanceAllowanceExempt="+dblConveyanceAllowanceExempt); 
//				System.out.println("dblInvestment="+dblInvestmentExempt);
//				System.out.println("dblHomeLoanTaxExempt="+dblHomeLoanTaxExempt);
//				System.out.println("dblOtherExemptions="+dblOtherExemptions);
//				System.out.println("dblIncomeFromOther="+dblIncomeFromOther);
//				System.out.println("dblNetTaxableIncome="+dblNetTaxableIncome);    
//				System.out.println("dblCess1="+dblCess1);
//				System.out.println("dblCess2="+dblCess2);
//				System.out.println("strFinancialYearStart="+strFinancialYearStart);
//				System.out.println("strFinancialYearEnd="+strFinancialYearEnd);
//				System.out.println("Gender="+(String)hmEmpGenderMap.get(strEmpId));
//				System.out.println("Age="+(String)hmEmpAgeMap.get(strEmpId));					
//				System.out.println("dblEducationAllowancePaid="+dblEducationAllowancePaid);
//				System.out.println("dblEducationAllowanceExempt="+dblEducationAllowanceExempt);
				
//				TDSPayable = calculateTDS(dblNetTaxableIncome, dblCess1, dblCess2, strFinancialYearStart, strFinancialYearEnd, strEmpId, (String)hmEmpGenderMap.get(strEmpId), (String)hmEmpAgeMap.get(strEmpId));		
			}
			TDSPayable = calculateTDS(con, dblNetTaxableIncome, dblCess1, dblCess2, strFinancialYearStart, strFinancialYearEnd, strEmpId, (String)hmEmpGenderMap.get(strEmpId), (String)hmEmpAgeMap.get(strEmpId), slabType);
		
		//===start parvez date: 01-04-2022===
			hmTaxInner.put("TAX_LIABILITY", TDSPayable+"");
		//===end parvez date: 01-04-2022===
			
			TDSPayable = TDSPayable - dblPrevOrgTDSAmount;
//			double dblRebate = 0;
//			if(dblNetTaxableIncome<=500000 && TDSPayable<=500000) {
//				if(TDSPayable>=2000) {
//					dblRebate = 2000;
//				} else if(TDSPayable>0 && TDSPayable<2000) {
//					dblRebate = TDSPayable;
//				}
//			}
			double dblMaxTaxableIncome = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_MAX_TAX_INCOME"));
			double dblRebateAmt = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_REBATE_AMOUNT"));
			double dblRebate = 0;
			if(dblNetTaxableIncome <= dblMaxTaxableIncome && TDSPayable <= dblMaxTaxableIncome) {
				if(TDSPayable>=dblRebateAmt) {
					dblRebate = dblRebateAmt;
				} else if(TDSPayable > 0 && TDSPayable < dblRebateAmt) {
					dblRebate = TDSPayable;
				}
			}
			
			double dblCess1Amount = (dblCess1 * (TDSPayable-dblRebate) / 100);
			double dblCess2Amount = (dblCess2 * (TDSPayable-dblRebate) / 100);
			
			/*if(strEmpId.equals("231")) {
				System.out.println(" TAX_LIABILITY==>"+TDSPayable);        
				System.out.println(" CESS1==>"+dblCess1);
				System.out.println(" CESS2==>"+dblCess2);
				System.out.println(" CESS1_AMOUNT==>"+dblCess1Amount);
				System.out.println(" CESS2_AMOUNT==>"+dblCess2Amount);
				System.out.println(" dblRebate==>"+dblRebate);
				System.out.println(" TOTAL_TAX_LIABILITY==>"+((TDSPayable-dblRebate) + dblCess1Amount+ dblCess2Amount));
			}*/
			
		//===start parvez date: 01-04-2022===	
//			hmTaxInner.put("TAX_LIABILITY", TDSPayable+"");
		//===end parvez date: 01-04-2022===	
			
			hmTaxInner.put("CESS1", dblCess1+"");
			hmTaxInner.put("CESS2", dblCess2+"");
			hmTaxInner.put("CESS1_AMOUNT", dblCess1Amount+"");
			hmTaxInner.put("CESS2_AMOUNT", dblCess2Amount+"");
			hmTaxInner.put("TOTAL_TAX_LIABILITY", ((TDSPayable-dblRebate) + dblCess1Amount+ dblCess2Amount)+""); 
			hmTaxInner.put("TAX_REBATE", dblRebate+"");
			hmTaxLiability.put(strEmpId, hmTaxInner);
		}
		
		
		pst = con.prepareStatement("select sum(amount) as amount, emp_id from payroll_generation where salary_head_id = ? and financial_year_from_date=? and financial_year_to_date=? and emp_id=? and is_paid = true group by emp_id");
		pst.setInt(1, TDS);
		pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(4, uF.parseToInt(getEmp_id()));
		rs = pst.executeQuery();
//		System.out.println(" pst==>"+pst);
//		System.out.println("9 date==>"+new Date());
		Map<String, String> hmPaidTdsMap = new HashMap<String, String>();
		while(rs.next()) {
			hmPaidTdsMap.put(rs.getString("emp_id"), rs.getString("amount"));
		}
		rs.close();
		pst.close();
//		System.out.println("=======14========"+new Date());      
		
		pst = con.prepareStatement("select * from document_activities where effective_date =? and document_name=? and emp_id=?");
		pst.setDate(1, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setString(2, "Form 16");
		pst.setInt(3, uF.parseToInt(getEmp_id()));
		rs = pst.executeQuery();
//		System.out.println(" pst==>"+pst);
//		System.out.println("10 date==>"+new Date());
		Map<String, String> hmForm16GeneratedFiles = new HashMap<String, String>();
		while(rs.next()) {
			hmForm16GeneratedFiles.put(rs.getString("emp_id"), rs.getString("document_id"));
		}
		rs.close();
		pst.close();
		
		Map hmTaxInner = (Map)hmTaxLiability.get(getEmp_id());
		if(hmTaxInner==null)hmTaxInner=new HashMap();
		double dblConveyanceAllowancePaid = uF.parseToDouble((String)hmTaxInner.get("dblConveyanceAllowancePaid"));
		double dblConveyanceAllowanceExempt = uF.parseToDouble((String)hmTaxInner.get("dblConveyanceAllowanceExempt"));
		
		String strQ1TotPaidAmt = getEmpQuarterWisePaidSalary(con, uF, "4,5,6", strFinancialYearStart, strFinancialYearEnd);
		String strQ2TotPaidAmt = getEmpQuarterWisePaidSalary(con, uF, "7,8,9", strFinancialYearStart, strFinancialYearEnd);
		String strQ3TotPaidAmt = getEmpQuarterWisePaidSalary(con, uF, "10,11,12", strFinancialYearStart, strFinancialYearEnd);
		String strQ4TotPaidAmt = getEmpQuarterWisePaidSalary(con, uF, "1,2,3", strFinancialYearStart, strFinancialYearEnd);
		
		String strQ1TotTaxDeducted = getEmpQuarterWiseTaxDeducted(con, uF, "4,5,6", strFinancialYearStart, strFinancialYearEnd);
		String strQ2TotTaxDeducted = getEmpQuarterWiseTaxDeducted(con, uF, "7,8,9", strFinancialYearStart, strFinancialYearEnd);
		String strQ3TotTaxDeducted = getEmpQuarterWiseTaxDeducted(con, uF, "10,11,12", strFinancialYearStart, strFinancialYearEnd);
		String strQ4TotTaxDeducted = getEmpQuarterWiseTaxDeducted(con, uF, "1,2,3", strFinancialYearStart, strFinancialYearEnd);
		
		double dblOfAllQTotPaidAmt = uF.parseToDouble(strQ1TotPaidAmt) + uF.parseToDouble(strQ2TotPaidAmt) + uF.parseToDouble(strQ3TotPaidAmt) + uF.parseToDouble(strQ4TotPaidAmt);
		double dblOfAllQTotTaxDeducted = uF.parseToDouble(strQ1TotTaxDeducted) + uF.parseToDouble(strQ2TotTaxDeducted) + uF.parseToDouble(strQ3TotTaxDeducted) + uF.parseToDouble(strQ4TotTaxDeducted);
		
		
		Map<String, String> hmQ1ChallanPaidData = getEmpQuarterWiseTaxChallanPaidData(con, uF, "4,5,6", strFinancialYearStart, strFinancialYearEnd);
		Map<String, String> hmQ2ChallanPaidData = getEmpQuarterWiseTaxChallanPaidData(con, uF, "7,8,9", strFinancialYearStart, strFinancialYearEnd);
		Map<String, String> hmQ3ChallanPaidData = getEmpQuarterWiseTaxChallanPaidData(con, uF, "10,11,12", strFinancialYearStart, strFinancialYearEnd);
		Map<String, String> hmQ4ChallanPaidData = getEmpQuarterWiseTaxChallanPaidData(con, uF, "1,2,3", strFinancialYearStart, strFinancialYearEnd);
		
		Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
		Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
		Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
		Font small = new Font(Font.FontFamily.HELVETICA, 7);
		Font smallBold = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD);
		Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.ITALIC); 
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, buffer);
        document.open();
        
        PdfPTable table = new PdfPTable(6);
		table.setWidthPercentage(100);        
        
        //New Row
		//Bank Copy
        PdfPCell row1 =new PdfPCell(new Paragraph("", small));
        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
        row1.setBorder(Rectangle.NO_BORDER);
        row1.setPadding(2.5f);
        row1.setColspan(6);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("FORM NO. 16",normalwithbold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(6);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("[See rule 31(1)(a)]", small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(6);
        table.addCell(row1);

        row1 =new PdfPCell(new Paragraph("PART A",normalwithbold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(6);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Certificate under section 203 of the Income- Tax Act, 1961 for tax deducted at source on salary",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(6);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Certificate No. ",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
        row1.setPadding(2.5f);
        row1.setColspan(3);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Last Update On",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(3);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Name and address of the Employer",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(3);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Name and address of the Employee",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(3);
        table.addCell(row1);
        
//        row1 =new PdfPCell(new Paragraph(CF.getStrOrgName()+"\n"+CF.getStrOrgAddress(), small));
//        String orgAddress="KIRTANE & PANDIT -M/S\n" +
//        		"73/2/2,SANGATI,BHAKTI MARG,\n" +
//        		"OFF LAW COLLEGE ROAD,PUNE-41104\n" +
//        		"Maharashtra\n" +
//        		"+(91)20-25433104\n" +
//        		"accounts@kirtanepandit.com";
//        row1 =new PdfPCell(new Paragraph(orgAddress, small)); 
        String orgAddress = uF.showData(hmOrg.get("ORG_NAME"), "")+"\n"+uF.showData(hmOrg.get("ORG_ADDRESS"), "")+"\n"+uF.showData(hmStates.get(hmOrg.get("ORG_STATE_ID")), "")+"\n"+uF.showData(hmOrg.get("ORG_CONTACT"), "")+"\n"+uF.showData(hmOrg.get("ORG_EMAIL"), ""); 
        row1 =new PdfPCell(new Paragraph(orgAddress, small));
        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
        row1.setPadding(2.5f);
        row1.setIndent(5.0f);
        row1.setColspan(3);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.showData(hmEmployeeMap.get(getEmp_id()), "")+"\n"+uF.showData(hmEmpAddressMap.get(getEmp_id()), ""), small));
        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
        row1.setPadding(2.5f);
        row1.setIndent(5.0f);
        row1.setColspan(3);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("PAN of the Deductor",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(2);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("TAN of the Deductor",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(2);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("PAN of the Employee",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Employee Reference No.\nprovided by the Employer\n(If applicable)",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
  //===start parvez date: 01-4-2022===      
        row1 =new PdfPCell(new Paragraph("Current-Org", small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(1);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.showData(hmOtherDetailsMap.get("DEDUCTOR_PAN"), " "), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(1);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Current-Org", small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(1);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.showData(hmOtherDetailsMap.get("DEDUCTOR_TAN"), " "), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(1);
        table.addCell(row1);
  //===end parvez date: 01-04-2022===      
        
        row1 =new PdfPCell(new Paragraph(uF.showData(hmOtherDetailsMap.get("EMPLOYEE_PAN"), " "), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("", small));
        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
    //===start parvez date: 01-04-2022===
        if(hmOtherDetailsMap.get("PREV_ORG_DEDUCTOR_PAN")!=null || hmOtherDetailsMap.get("PREV_ORG_DEDUCTOR_TAN") != null){
        	row1 =new PdfPCell(new Paragraph("Previous-Org", small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(1);
	        table.addCell(row1);
        	
        	row1 =new PdfPCell(new Paragraph(uF.showData(hmOtherDetailsMap.get("PREV_ORG_DEDUCTOR_PAN"), " "), small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(1);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Previous-Org", small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(1);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmOtherDetailsMap.get("PREV_ORG_DEDUCTOR_TAN"), " "), small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(1);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmOtherDetailsMap.get("EMPLOYEE_PAN"), " "), small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("", small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
        }
    //===end parvez date: 01-04-2022===    
        
        row1 =new PdfPCell(new Paragraph("CIT(TDS)",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setColspan(3);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Assessment Year",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Period With the Employer",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setColspan(2);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
//        String citData="The Commissioner of Income Tax (TDS)\n" +
//		"4th Floor, A Wing , PMT Commercial Complex, Shankar Sheth  Road,\nSwargate , Pune – 411037";
        String citData=uF.showData(hmOtherDetailsMap.get("CIT_ADDRESS"), " ");
        
        row1 =new PdfPCell(new Paragraph(citData, small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setColspan(3);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph((uF.parseToInt(uF.getDateFormat(strFinancialYearStart,DATE_FORMAT, "yyyy"))+1) +"-"+ (uF.parseToInt(uF.getDateFormat(strFinancialYearEnd,DATE_FORMAT, "yyyy"))+1), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.getDateFormat(strFinancialYearStart,DATE_FORMAT, "yyyy"), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.getDateFormat(strFinancialYearEnd,DATE_FORMAT, "yyyy"), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Summary of amount paid / credited and tax deducted at source thereon in respect of the employee",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(6);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Quarter(s)",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Receipt Numbers of original\n statements of TDS\nunder sub-section (3) of\nsection 200",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Amount Paid/ credited",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Amount of Tax Deducted (Rs.)",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Amount of Tax deposited/ remitted\n(Rs.)",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(2);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Q1", small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("", small));//GDIXXFIG
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(strQ1TotPaidAmt, small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(strQ1TotTaxDeducted, small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(strQ1TotTaxDeducted, small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(2);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Q2", small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("", small)); //GDIXCEAE
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(strQ2TotPaidAmt, small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(strQ2TotTaxDeducted, small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(strQ2TotTaxDeducted, small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(2);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Q3", small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("", small)); //QARNYPZF
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(strQ3TotPaidAmt, small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(strQ3TotTaxDeducted, small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(strQ3TotTaxDeducted, small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(2);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Q4", small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("", small)); //QQOGQCVA
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(strQ4TotPaidAmt, small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(strQ4TotTaxDeducted, small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(strQ4TotTaxDeducted, small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(2);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Total(Rs.)", small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("", small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.formatIntoTwoDecimal(dblOfAllQTotPaidAmt), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.formatIntoTwoDecimal(dblOfAllQTotTaxDeducted), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.formatIntoTwoDecimal(dblOfAllQTotTaxDeducted), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(2);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("I. DETAILS OF TAX DEDUCTED AND DEPOSITED IN THE CENTRAL GOVERNMENT ACCOUNT THROUGH BOOK ADJUSTMENT\n" +
        	"(The deductor to provide payment wise details of tax deducted and deposited with respect to the deductee)",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(6);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("SI. No.",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setRowspan(2);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Tax Deducted in respect of the Deductee\n (Rs.)",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setRowspan(2);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Book Identification Number (BIN)",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(4);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Receipt Numbers of\n Form No. 24G",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("DDO serial number in \nForm No.24G",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Date of transfer voucher\n (dd/mm/yyyy)",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Status of matching with\n Form No. 24G",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Total (Rs.)", small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(4);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("II. DETAILS OF TAX DEDUCTED AND DEPOSITED IN THE CENTRAL GOVERNMENT ACCOUNT THROUGH CHALLAN\n" +
        		"(The deductor to provide payment wise details of tax deducted and deposited with respect to the deductee)",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(6);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("SI. No.",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setRowspan(2);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Tax Deducted in respect of the Deductee\n (Rs.)",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setRowspan(2);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Challan Identification Number (CIN)",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        row1.setColspan(4);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("BSR Code of the Bank Branch",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Date on which tax deposited\n(dd/mm/yyyy)",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Challan Serial Number",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("Status of matching with\n OLTAS",smallBold));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("1", small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.formatIntoTwoDecimal(uF.parseToDouble(hmQ1ChallanPaidData.get("CHALLAN_PAID_AMOUNT"))), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.showData(hmQ1ChallanPaidData.get("CHALLAN_PAID_BSR_CODE"), ""), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.showData(hmQ1ChallanPaidData.get("CHALLAN_PAID_DATE"), ""), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.showData(hmQ1ChallanPaidData.get("PAID_CHALLAN_NO"), ""), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("", small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("2", small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.formatIntoTwoDecimal(uF.parseToDouble(hmQ2ChallanPaidData.get("CHALLAN_PAID_AMOUNT"))), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.showData(hmQ2ChallanPaidData.get("CHALLAN_PAID_BSR_CODE"), ""), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.showData(hmQ2ChallanPaidData.get("CHALLAN_PAID_DATE"), ""), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.showData(hmQ2ChallanPaidData.get("PAID_CHALLAN_NO"), ""), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("", small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("3", small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.formatIntoTwoDecimal(uF.parseToDouble(hmQ3ChallanPaidData.get("CHALLAN_PAID_AMOUNT"))), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.showData(hmQ3ChallanPaidData.get("CHALLAN_PAID_BSR_CODE"), ""), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.showData(hmQ3ChallanPaidData.get("CHALLAN_PAID_DATE"), ""), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.showData(hmQ3ChallanPaidData.get("PAID_CHALLAN_NO"), ""), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("", small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("4", small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.formatIntoTwoDecimal(uF.parseToDouble(hmQ4ChallanPaidData.get("CHALLAN_PAID_AMOUNT"))), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.showData(hmQ4ChallanPaidData.get("CHALLAN_PAID_BSR_CODE"), ""), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.showData(hmQ4ChallanPaidData.get("CHALLAN_PAID_DATE"), ""), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph(uF.showData(hmQ4ChallanPaidData.get("PAID_CHALLAN_NO"), ""), small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        row1 =new PdfPCell(new Paragraph("", small));
        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
        row1.setPadding(2.5f);
        table.addCell(row1);
        
        document.add(table);
        
        document.newPage();
        
        PdfPTable table1 = new PdfPTable(6);
		table1.setWidthPercentage(100);        
        
        
        PdfPCell row2 =new PdfPCell(new Paragraph("Emp Code ",smallBold));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.NO_BORDER);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(uF.showData(hmEmpCode.get(getEmp_id()), ""), small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.NO_BORDER);
        row2.setPadding(2.5f);
        row2.setColspan(4);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("PAN  "+uF.showData(hmEmpPanNo.get(getEmp_id()), ""),smallBold));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.NO_BORDER);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("Emp Name ",smallBold));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.NO_BORDER);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(uF.showData(hmEmployeeMap.get(getEmp_id()), ""), small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.NO_BORDER);
        row2.setPadding(2.5f);
        row2.setColspan(4);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("Certificate No. ",smallBold));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.NO_BORDER);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("Part B (Annexure)",normalwithbold));
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setPadding(2.5f);
        row2.setColspan(6);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("Details of salary paid and other tax deducted", smallBold));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(6);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("RS.", small));
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("RS.", small));
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("RS.", small));
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("RS.", small));
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("1. Gross Salary", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblGross), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("  a)Salary as per provisions contained in sc 17 (1)", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        row2.setIndent(5.0f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("  b) Value of perquisites u/s 17(2)\n(as per Form No. 12BA, wherever applicable)", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setIndent(5.0f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("  c) Profits in Lieu of salary under 17(3)\n(as per Form No. 12BA, wherever applicable)", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setIndent(5.0f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("  d)Total", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setIndent(5.0f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblGross), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("2. Less: allowance to the extent exempt u/s 10", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        Map<String, String> hmUS10_16Inner= (Map<String, String>)hmUnderSection10_16Map.get(getEmp_id());
        if(hmUS10_16Inner==null) hmUS10_16Inner=new HashMap<String, String>();
//        System.out.println("hmUS10_16Inner ===>> " + hmUS10_16Inner);
        
        Map<String, String> hmUS10_16InnerPaid= (Map<String, String>)hmUnderSection10_16PaidMap.get(getEmp_id());
		if(hmUS10_16InnerPaid==null) hmUS10_16InnerPaid=new HashMap<String, String>();
		
        Map<String, List<String>> hmExemptionData = hmExemptionDataUnderSection.get("4"); //US10
        if(hmExemptionData == null) hmExemptionData = new HashMap<String, List<String>>();
        Iterator<String> itExamption = hmExemptionData.keySet().iterator();
        double dblTotUS10ExemptAmt = 0.0d;
        int exempCnt = 0;
//        System.out.println("hmUS10_16InnerPaid ===>> " + hmUS10_16InnerPaid);
//        System.out.println("hmUS10_16Inner ===>> " + hmUS10_16Inner);
        String[] exempSerials = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
        while (itExamption.hasNext()) {
			String exemptionId = itExamption.next();
			List<String> innerList = hmExemptionData.get(exemptionId);
			
//			System.out.println("exemptionId ===>> " + exemptionId + " innerList ===>> " + innerList);
			double dblAmtPaid = uF.parseToDouble(hmUS10_16InnerPaid.get(innerList.get(6)));
			double dblAmtExempt = uF.parseToDouble(hmUS10_16Inner.get(innerList.get(6)));
//			System.out.println(innerList.get(6)+" -- dblAmtExempt ===>> " + dblAmtExempt);
			
			if(uF.parseToInt(innerList.get(6)) == HRA || uF.parseToInt(innerList.get(6)) == CONVEYANCE_ALLOWANCE || uF.parseToInt(innerList.get(6)) == EDUCATION_ALLOWANCE) {
				dblAmtPaid = uF.parseToDouble(hmUS10_16_SalHeadData.get(innerList.get(6)+"_PAID"));
				dblAmtExempt = uF.parseToDouble(hmUS10_16_SalHeadData.get(innerList.get(6)+"_EXEMPT"));
//				System.out.println("dblAmtExempt ===>> " + dblAmtExempt);
			}
			if(uF.parseToInt(innerList.get(6)) == 0) {
				dblAmtExempt = uF.parseToDouble(innerList.get(3));
			}
			dblTotUS10ExemptAmt += dblAmtExempt;
			
			row2 =new PdfPCell(new Paragraph("  "+exempSerials[exempCnt]+") "+innerList.get(1), small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setIndent(5.0f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblAmtPaid), small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("", small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("", small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblAmtExempt), small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
			
	        exempCnt++;
		}
        
        /*row2 =new PdfPCell(new Paragraph("  a)Conveyance Allowance", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setIndent(5.0f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblConveyanceAllowancePaid), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblConveyanceAllowanceExempt), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);*/
        
        
        
        /*double dblMedicalAllowanceExempt =uF.parseToDouble((String)hmTaxInner.get("dblMedicalAllowanceExempt"));
        double dblMedicalAllowancePaid =uF.parseToDouble((String)hmTaxInner.get("dblMedicalAllowancePaid"));
        row2 =new PdfPCell(new Paragraph("  b)Medical Allowance", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setIndent(5.0f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblMedicalAllowancePaid), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblMedicalAllowanceExempt), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);*/
        
        
        /*double dblHRAExemption = uF.parseToDouble((String)hmTaxInner.get("dblHRAExemption"));
        row2 =new PdfPCell(new Paragraph("  c)HRA", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setIndent(5.0f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblHRAExemption), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);*/
        
        
        /*double dblEducationAllowancePaid = uF.parseToDouble((String)hmTaxInner.get("dblEducationAllowancePaid"));
		double dblEducationAllowanceExempt = uF.parseToDouble((String)hmTaxInner.get("dblEducationAllowanceExempt"));
        row2 =new PdfPCell(new Paragraph("  d) Education Allowance", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setIndent(5.0f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblEducationAllowancePaid), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblEducationAllowanceExempt), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);*/
        
        
        /*double dblLTAPaid = uF.parseToDouble((String)hmTaxInner.get("dblLTAPaid"));
		double dblLTAExempt = uF.parseToDouble((String)hmTaxInner.get("dblLTAExempt"));
        row2 =new PdfPCell(new Paragraph("  e) Leave Travel Allowances[L.T.C.]", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setIndent(5.0f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblLTAPaid), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblLTAExempt), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);*/
        
        
        /*double dblOtherExemption = uF.parseToDouble((String)hmTaxInner.get("dblOtherExemptions"));
//        System.out.println("dblOtherExemption ===>> " + dblOtherExemption);
        String strOtherExemption = "   f) Other Exemptions";
        if(uF.parseToInt(getEmp_id()) == 589 && strFinancialYearStart.trim().equals("01/04/2016") && strFinancialYearEnd.trim().equals("31/03/2017")) { 
        	strOtherExemption = "   f) Gratuity";
        }	        
        row2 =new PdfPCell(new Paragraph(strOtherExemption, small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setIndent(5.0f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblOtherExemption), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblOtherExemption), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);*/
        
        
//        double dblGrossNet = Math.round(dblGross) - Math.round(dblHRAExemption) - Math.round(dblConveyanceAllowanceExempt) - Math.round(dblOtherExemption) - Math.round(dblHomeLoanTaxExempt) - Math.round(dblMedicalAllowanceExempt);
//        double dblGrossNet = Math.round(dblGross) - Math.round(dblHRAExemption) - Math.round(dblConveyanceAllowanceExempt) - Math.round(dblOtherExemption) - Math.round(dblMedicalAllowanceExempt) - Math.round(dblEducationAllowanceExempt) - Math.round(dblLTAExempt);
        
        double dblGrossNet = Math.round(dblGross) - Math.round(dblTotUS10ExemptAmt);
        if(dblGrossNet<0) {
        	dblGrossNet=0;
        }
        row2 =new PdfPCell(new Paragraph("3. Balance (1-2)", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblGrossNet), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("4. Less: allowance to the extent exempt u/s 16", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        
        Map<String, List<String>> hmExemptionDataUS16 = hmExemptionDataUnderSection.get("5"); //US16
        if(hmExemptionDataUS16 == null) hmExemptionDataUS16 = new HashMap<String, List<String>>();
        
        Iterator<String> itExamptionUS16 = hmExemptionDataUS16.keySet().iterator();
        double dblTotUS16ExemptAmt = 0.0d;
        int exempCntUS16 = 0;
        String[] exempSerialsUS16 = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
        while (itExamptionUS16.hasNext()) {
			String exemptionId = itExamptionUS16.next();
			List<String> innerList = hmExemptionDataUS16.get(exemptionId);
			double dblAmtPaid = uF.parseToDouble(hmUS10_16InnerPaid.get(innerList.get(6)));
			double dblAmtExempt = uF.parseToDouble(hmUS10_16Inner.get(innerList.get(6)));
//			if(uF.parseToInt(innerList.get(6)) == HRA || uF.parseToInt(innerList.get(6)) == CONVEYANCE_ALLOWANCE || uF.parseToInt(innerList.get(6)) == EDUCATION_ALLOWANCE) {
//				dblAmtPaid = uF.parseToDouble(hmUS10_16_SalHeadData.get(innerList.get(6)+"_PAID"));
//				dblAmtExempt = uF.parseToDouble(hmUS10_16_SalHeadData.get(innerList.get(6)+"_EXEMPT"));
//			}
			if(uF.parseToInt(innerList.get(6)) == 0) {
				dblAmtExempt = uF.parseToDouble(innerList.get(3));
			}
			dblTotUS16ExemptAmt += dblAmtExempt;
			
			row2 =new PdfPCell(new Paragraph("  "+exempSerialsUS16[exempCntUS16]+") "+innerList.get(1), small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setIndent(5.0f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblAmtExempt), small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("", small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("", small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("", small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
			
	        exempCntUS16++;
		}
        
        
        /*row2 =new PdfPCell(new Paragraph("  a)Entertainment Allowance", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setIndent(5.0f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        
        double dblProfessionalTaxPaid = uF.parseToDouble((String)hmTaxInner.get("dblProfessionalTaxPaid"));
		double dblProfessionalTaxExempt = uF.parseToDouble((String)hmTaxInner.get("dblProfessionalTaxExempt"));
        row2 =new PdfPCell(new Paragraph("  b) Professional Tax", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setIndent(5.0f);
        row2.setColspan(2);
        table1.addCell(row2);
        
//        row2 =new PdfPCell(new Paragraph(""+Math.round(dblProfessionalTaxPaid), small));
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblProfessionalTaxExempt), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
//        row2 =new PdfPCell(new Paragraph(""+Math.round(dblProfessionalTaxExempt), small));
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);*/
        
        
        row2 =new PdfPCell(new Paragraph("5. Aggregate of 4(a) and (b)", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+(Math.round(dblTotUS16ExemptAmt)), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        
        /*double dblHomeLoanTaxExempt = uF.parseToDouble((String)hmTaxInner.get("dblHomeLoanTaxExempt"));
        row2 =new PdfPCell(new Paragraph("6. Home Loan Interest", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));  
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblHomeLoanTaxExempt), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);*/
        
//        dblGrossNet -=  (Math.round(dblProfessionalTaxExempt)+Math.round(dblEducationAllowanceExempt));
        dblGrossNet -= Math.round(dblTotUS16ExemptAmt);
        if(dblGrossNet<0) {
        	dblGrossNet=0;
        }
//        dblGrossNet -= Math.round(dblHomeLoanTaxExempt);
//        row2 =new PdfPCell(new Paragraph("7. Income Chargeable under the \nhead 'salaries ((3-5)-6)", small));
        row2 =new PdfPCell(new Paragraph("6. Income Chargeable under the \nhead 'salaries (3-5)", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblGrossNet), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        double dblIncomeFromOther=uF.parseToDouble((String)hmTaxInner.get("dblIncomeFromOther"));
  //===start parvez date: 31-03-2022===      
        row2 =new PdfPCell(new Paragraph("7. a). + Any other income reported by the employee", small));
  //===end parvez date: 31-03-2022===      
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblIncomeFromOther), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
   //===start parvez date: 31-03-2022===
        double dblPrevOrgGross=uF.parseToDouble((String)hmTaxInner.get("dblPrevOrgGross"));
        row2 =new PdfPCell(new Paragraph("   b). Income from previous Organization by the employee", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblPrevOrgGross), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        dblGrossNet += Math.round(dblIncomeFromOther)+Math.round(dblPrevOrgGross);
  //===end parvez date: 31-03-2022===      
        if(dblGrossNet<0) {
        	dblGrossNet=0;
        }
        row2 =new PdfPCell(new Paragraph("8. Gross Total Income (6+7)", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblGrossNet), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("9. Less: Deductions under Chapter VI-A", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        double dblInvestment = uF.parseToDouble((String)hmTaxInner.get("dblInvestment"));
		double dblChapterVIA = 0.0d;
		List<String> alUnderSection=new ArrayList<String>();
		Map<String,String> hmInvest=hmEmpInvestment.get(getEmp_id());
		if(hmInvest==null) hmInvest = new HashMap<String, String>();
//		if(hmInvest!=null && hmInvest.size()>0) {
//			Iterator<String> it1=hmInvest.keySet().iterator();
			int ii=0;
//		  	while(it1.hasNext()) {
			for(int a=0;chapter1SectionList!=null && a<chapter1SectionList.size();a++) {
//		  		String strSectionId=it1.next();
				String strSectionId=chapter1SectionList.get(a);
//		  		String strAmt=hmInvest.get(strSectionId);
				double strAmt = 0.0d;
				String strSectionAmt = uF.showData(hmInvest.get(strSectionId), "");
				Map<String, String> hmSubSecAdjusted10PerLimitAmt = hmSectionwiseSubSecAdjusted10PerLimitAmt.get(strSectionId);
				if(hmSubSecAdjusted10PerLimitAmt==null) hmSubSecAdjusted10PerLimitAmt = new HashMap<String, String>();
				
		  		Map<String, List<Map<String, String>>> hmSubInvestment = (Map<String, List<Map<String, String>>>) hmEmpSubInvestment.get("" + getEmp_id());
		  		if(hmSubInvestment==null) hmSubInvestment= new HashMap<String, List<Map<String, String>>>();
		  		List<Map<String, String>> subInvestList = (List<Map<String, String>>) hmSubInvestment.get("" +strSectionId);
		  		String strUnderSection="";
		  		
//		  		if(hmSectionMap.get(strSectionId).equals("80C and 80CCC")) {
//		  		if(hmSectionMap.containsKey(strSectionId)) {
//		  			strAmt=""+Math.round(dblInvestment);
//		  		}

				if(hmSectionLimitA.containsKey(strSectionId)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_CEILING_AMT"));
					if(dblInvestmentCeilingLimit>0) {
						dblInvestmentLimit = dblInvestmentCeilingLimit;
					}
				} else {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
					dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_CEILING_AMT"));
					if(dblInvestmentCeilingLimit>0) {
						dblInvestmentLimit = dblInvestmentCeilingLimit;
					}
				}
			
		  		strAmt = uF.parseToDouble(strSectionAmt);
		  		if(dblInvestmentLimit>=0) {
		  			strAmt = Math.min(strAmt, dblInvestmentLimit);
		  		}
//		  		dblChapterVIA += uF.parseToDouble(strSectionAmt);
//		  		System.out.println("dblChapterVIA ===>> " + dblChapterVIA);
		  		if(ii==0) {
		  			row2 =new PdfPCell(new Paragraph("A). Sections 80C, 80CCC and 80CCD", small));
			        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        row2.setIndent(5.0f);
			        row2.setColspan(2);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small)); 
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
		  			
		  		}
		  		ii++;
		  		
		  		row2 =new PdfPCell(new Paragraph("   "+ii+". "+uF.showData(hmSectionMap.get(strSectionId), ""), small));
		        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
		        row2.setBorder(Rectangle.LEFT);
		        row2.setPadding(2.5f);
		        row2.setIndent(10.0f);
		        row2.setColspan(2);
		        table1.addCell(row2);
		        
		        Map<String,String> hmAcutualInvest = hmEmpActualInvestment.get(getEmp_id());
		        if(hmAcutualInvest == null) hmAcutualInvest = new HashMap<String, String>();
		        
		        row2 =new PdfPCell(new Paragraph((subInvestList==null ? ""+Math.round(uF.parseToDouble(hmAcutualInvest.get(strSectionId))) : ""), small));
		        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row2.setBorder(Rectangle.LEFT);
		        row2.setPadding(2.5f);
		        table1.addCell(row2);
		        
		        row2 =new PdfPCell(new Paragraph("", small)); 
		        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row2.setBorder(Rectangle.LEFT);
		        row2.setPadding(2.5f);
		        table1.addCell(row2);
		        
		        row2 =new PdfPCell(new Paragraph("", small));
		        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row2.setBorder(Rectangle.LEFT);
		        row2.setPadding(2.5f);
		        table1.addCell(row2);
		        
//		        row2 =new PdfPCell(new Paragraph((subInvestList==null && !hmSectionMap.get(strSectionId).equals("80C and 80CCC") ? ""+Math.round(uF.parseToDouble(strAmt)) : ""), small));
		        row2 =new PdfPCell(new Paragraph((subInvestList==null && !hmSectionMap.containsKey(strSectionId) ? ""+Math.round(strAmt) : ""), small));
		        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
		        row2.setPadding(2.5f);
		        table1.addCell(row2);
		  		
		        int k=0;
//		        if(hmSectionMap.get(strSectionId).equals("80C and 80CCC")) {
		        if(hmSectionMap.containsKey(strSectionId) && uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId))) {
		        	k++;
		        	strAmt += uF.parseToDouble(""+hmTaxInner.get("dblEmpPF"));
//			  		dblChapterVIA += uF.parseToDouble(""+hmTaxInner.get("dblEmpPF"));
			  		strAmt = Math.min(strAmt, dblInvestmentLimit);
//			  		dblChapterVIA = Math.min(dblChapterVIA, dblInvestmentLimit);
		        	row2 =new PdfPCell(new Paragraph("   "+(k)+". PF", small));
			        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        row2.setIndent(15.0f);
			        row2.setColspan(2);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph(""+Math.round(uF.parseToDouble(""+hmTaxInner.get("dblEmpPF"))), small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small)); 
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
		  		}
		  		
//		        System.out.println("strAmt ==============>> " + strAmt);
		        double totSubSecAmtOfSection = 0;
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
						/*if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
							dblSubSecAmt = (dblPaidAmt * dblSubSecAmt) / 100;
						}*/
					} else {
//						dblSubSecAmt = dblPaidAmt;
						dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
						if(dblSubSec10PerAdjLimitAmt>0) {
							dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
						} else if(dblSubSec10PerAdjLimitAmt<0) {
							dblSubSecAmt = 0;
						}
						/*dblSubSecAmt = dblPaidAmt;*/
					}
//					System.out.println("dblSubSecAmt ===>> " + dblSubSecAmt);
					totSubSecAmtOfSection += dblSubSecAmt;
					k++;
					row2 =new PdfPCell(new Paragraph("   "+(k)+". "+uF.showData(hm.get("SECTION_NAME"), ""), small));
			        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        row2.setIndent(15.0f);
			        row2.setColspan(2);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph(""+Math.round(dblPaidAmt), small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small)); 
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
					
		  		}
		  		
//		  		System.out.println("totSubSecAmtOfSection ===>> " + totSubSecAmtOfSection);
//		  		System.out.println("subInvestList ===>> " + subInvestList + " -- hmSectionMap ===>> " + hmSectionMap);
		  		
//		  		if(subInvestList!=null || hmSectionMap.get(strSectionId).equals("80C and 80CCC")) {
		  		if(subInvestList!=null || hmSectionMap.containsKey(strSectionId)) { //
		  			if(totSubSecAmtOfSection>0 && strAmt>totSubSecAmtOfSection) {
		  				strAmt = totSubSecAmtOfSection;
		  				if(hmSectionMap.containsKey(strSectionId) && uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId))) {
		  					strAmt += uF.parseToDouble(""+hmTaxInner.get("dblEmpPF"));
		  				}
		  				if(dblInvestmentLimit>=0) {
		  					strAmt = Math.min(strAmt, dblInvestmentLimit);
		  				}
	  					dblChapterVIA += strAmt;
		  			} else {
			  			dblChapterVIA += strAmt;
			  		}
//		  			System.out.println("dblChapterVIA ===>> " + dblChapterVIA);
		  			
		  			row2 =new PdfPCell(new Paragraph("", small));
			        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        row2.setColspan(2);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small)); 
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph(""+Math.round(strAmt), small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
		  		} else {
		  			dblChapterVIA += strAmt;
		  		}
		  		
		  	}
//		}
//		System.out.println("dblChapterVIA OUT =====>> " + dblChapterVIA);
		
		Map<String,String> hmInvest1 = hmEmpInvestment1.get(getEmp_id());
		if(hmInvest1==null) hmInvest1 = new HashMap<String, String>();
//		if(hmInvest1!=null && hmInvest1.size()>0) {
//			Iterator<String> it2=hmInvest1.keySet().iterator();
			ii=0;
//		  	while(it2.hasNext()) {
			for(int a=0;chapter2SectionList!=null && a<chapter2SectionList.size();a++) {
//		  		String strSectionId=it2.next();
				String strSectionId = chapter2SectionList.get(a);
				Map<String, String> hmSubSecAdjusted10PerLimitAmt = hmSectionwiseSubSecAdjusted10PerLimitAmt.get(strSectionId);
				if(hmSubSecAdjusted10PerLimitAmt==null) hmSubSecAdjusted10PerLimitAmt = new HashMap<String, String>();
				
//		  		String strAmt=hmInvest1.get(strSectionId);
				if(hmSectionLimitA.containsKey(strSectionId)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_CEILING_AMT"));
					if(dblInvestmentCeilingLimit>0) {
						dblInvestmentLimit = dblInvestmentCeilingLimit;
					}
				} else {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					double dblVIA1Invest = uF.parseToDouble(hmInvest1.get(strSectionId));
					dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_CEILING_AMT"));
					if(dblInvestmentCeilingLimit>0) {
						dblInvestmentLimit = dblInvestmentCeilingLimit;
					}
				}
				
				String strAmt = uF.showData(hmInvest1.get(strSectionId), "");
				if(dblInvestmentLimit>=0) {
					strAmt = ""+Math.min(uF.parseToDouble(strAmt), dblInvestmentLimit);
				}
//		  		dblChapterVIA+=uF.parseToDouble(strAmt);
		  		
		  		Map<String, List<Map<String, String>>> hmSubInvestment = (Map<String, List<Map<String, String>>>) hmEmpSubInvestment.get("" + getEmp_id());
		  		if(hmSubInvestment==null) hmSubInvestment= new HashMap<String, List<Map<String, String>>>();
		  		List<Map<String, String>> subInvestList = (List<Map<String, String>>) hmSubInvestment.get("" +strSectionId);
		  		String strUnderSection="";
		  		
		  		if(ii==0) {
		  			row2 =new PdfPCell(new Paragraph("B). Other sections (eg. 80E, 80G, 80TTA, etc.)\n under VI-A", small));
			        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        row2.setIndent(5.0f);
			        row2.setColspan(2);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small)); 
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
		  		}
		  		ii++;
		  		
		  		row2 =new PdfPCell(new Paragraph("    "+ii+". "+uF.showData(hmSectionMap.get(strSectionId), ""), small));
		        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
		        row2.setBorder(Rectangle.LEFT);
		        row2.setPadding(2.5f);
		        row2.setIndent(10.0f);
		        row2.setColspan(2);
		        table1.addCell(row2);
		        
		        Map<String,String> hmAcutualInvest = hmEmpActualInvestment1.get(getEmp_id());
		        if(hmAcutualInvest==null) hmAcutualInvest = new HashMap<String, String>();
		        
		        row2 =new PdfPCell(new Paragraph((subInvestList==null ? ""+Math.round(uF.parseToDouble(hmAcutualInvest.get(strSectionId))) : ""), small));
		        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row2.setBorder(Rectangle.LEFT);
		        row2.setPadding(2.5f);
		        table1.addCell(row2);
		        
		        row2 =new PdfPCell(new Paragraph("", small)); 
		        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row2.setBorder(Rectangle.LEFT);
		        row2.setPadding(2.5f);
		        table1.addCell(row2);
		        
		        row2 =new PdfPCell(new Paragraph("", small));
		        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row2.setBorder(Rectangle.LEFT);
		        row2.setPadding(2.5f);
		        table1.addCell(row2);
		        
		        row2 =new PdfPCell(new Paragraph(""+(subInvestList==null ? ""+Math.round(uF.parseToDouble(strAmt)) : ""), small));
		        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
		        row2.setPadding(2.5f);
		        table1.addCell(row2);
		  		
		        double totSubSecAmtOfSection = 0;
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
						/*if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
							dblSubSecAmt = (dblPaidAmt * dblSubSecAmt) / 100;
						}*/
					} else {
						dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
						if(dblSubSec10PerAdjLimitAmt>0) {
							dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
						} else if(dblSubSec10PerAdjLimitAmt<0) {
							dblSubSecAmt = 0;
						}
						/*dblSubSecAmt = dblPaidAmt;*/
					}
//					System.out.println("2 dblSubSecAmt ===>> " + dblSubSecAmt);
					
					totSubSecAmtOfSection += dblSubSecAmt;
					row2 =new PdfPCell(new Paragraph("   "+(j+1)+". "+uF.showData(hm.get("SECTION_NAME"), ""), small));
			        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        row2.setIndent(15.0f);
			        row2.setColspan(2);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph(""+Math.round(dblPaidAmt), small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small)); 
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
					
		  		}
		  		
		  		if(subInvestList!=null) {
//		  			System.out.println("totSubSecAmtOfSection =========>> " + totSubSecAmtOfSection + " -- strAmt ===>> " + strAmt);
		  			if(totSubSecAmtOfSection>0 && (uF.parseToDouble(strAmt)>totSubSecAmtOfSection || uF.parseToDouble(strAmt)<0)) {
		  				strAmt = ""+totSubSecAmtOfSection;
		  				if(dblInvestmentLimit>=0) {
		  					strAmt = ""+Math.min(uF.parseToDouble(strAmt), dblInvestmentLimit);
		  				}
		  			}
//		  			System.out.println("strAmt =========>> " + strAmt);
		  			
		  			dblChapterVIA+=uF.parseToDouble(strAmt);
		  			
		  			row2 =new PdfPCell(new Paragraph("", small));
			        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        row2.setColspan(2);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small)); 
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("", small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph(""+Math.round(uF.parseToDouble(strAmt)), small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
		  		} else {
		  			dblChapterVIA+=uF.parseToDouble(strAmt);
		  		}
		  	}
//		}
        
		row2 =new PdfPCell(new Paragraph("10. Aggregate of deductible Amount under Chapter VI-A", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        row2.setNoWrap(true);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblChapterVIA), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        dblGrossNet -= Math.round(dblChapterVIA);
        if(dblGrossNet<0) {
        	dblGrossNet=0;
        }
        row2 =new PdfPCell(new Paragraph("11. Total Income (8-10) [Round off u/s 288B]", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblGrossNet), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        double dblTaxLiability = uF.parseToDouble((String)hmTaxInner.get("TAX_LIABILITY"));
        row2 =new PdfPCell(new Paragraph("12. Tax on Total Income", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblTaxLiability), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        double dblRebate = uF.parseToDouble((String)hmTaxInner.get("TAX_REBATE")) ;
   //===start parvez date: 01-04-2022===     
        row2 =new PdfPCell(new Paragraph("13.a) less Tax Rebate u/s 87 A", small));
   //===end parvez date: 01-04-2022===     
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblRebate), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
   //===start parvez date: 01-04-2022===
//        System.out.println("dblPrevOrgTDSAmount="+hmTaxInner.get("dblPrevOrgTDSAmount"));
//        String strPrevOrgTDSAmount = (String) hmTaxInner.get("dblPrevOrgTDSAmount");
        double dblPrevOrgTDSAmount = uF.parseToDouble(hmTaxInner.get("dblPrevOrgTDSAmount")+"");
        row2 =new PdfPCell(new Paragraph("   b. less Tax paid in previous Organization", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblPrevOrgTDSAmount), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
   //===end parvez date: 01-04-2022===     
        
        double dblCess1 = uF.parseToDouble((String)hmTaxInner.get("CESS1"));
		double dblCess1Amount = uF.parseToDouble((String)hmTaxInner.get("CESS1_AMOUNT"));
        row2 =new PdfPCell(new Paragraph("14. Add: Ed. Cess @ "+dblCess1+"%", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblCess1Amount), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        double dblCess2 = uF.parseToDouble((String)hmTaxInner.get("CESS2")) ;
		double dblCess2Amount = uF.parseToDouble((String)hmTaxInner.get("CESS2_AMOUNT"));
        row2 =new PdfPCell(new Paragraph("15. Add: She Cess @ "+dblCess2+"%", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblCess2Amount), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        double dblTotalTaxLiability = uF.parseToDouble((String)hmTaxInner.get("TOTAL_TAX_LIABILITY"));
        row2 =new PdfPCell(new Paragraph("16. Tax Payable ((12-13)+14+15)", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblTotalTaxLiability), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("17. Less: relief under section 89 (attach form 10E)", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("18. Tax Payable (16+17)  [Round off u/s 288B]", small));  
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(dblTotalTaxLiability), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("19. Tax deducted at source u/s 192 (1)", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(uF.parseToDouble((String)hmPaidTdsMap.get(getEmp_id()))), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("20. Tax Payable/ Refundable (18-19)", small));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small)); 
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("", small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(""+Math.round(Math.round(dblTotalTaxLiability) - Math.round(uF.parseToDouble((String)hmPaidTdsMap.get(getEmp_id())))), small));
        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("Verification",smallBold));
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setPadding(2.5f);
        row2.setColspan(6);
        table1.addCell(row2);
        
        String strAuthorisedName=uF.showData(hmAuthorised.get("EMP_NAME"), "");
//        String str1="Suhas Deshpande";
        String strAuthorisedMiddleName=uF.showData(hmAuthorised.get("EMP_MIDDLE_NAME"), "");
//        String str2="Gajanan";
        
        row2 =new PdfPCell(new Paragraph("I "+strAuthorisedName+", son/ daughter of "+strAuthorisedMiddleName+" working " +
		"in the capacity of "+uF.showData(strAuthorizeDesignation, "")+" (designation) do hereby certify that the information given above is ture," +
		"complete and correct and is based on the books of account,documents,TDS statements," +
		"TDS deposited and other available records.",smallBold));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(6);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("Place:",smallBold));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("Pune",smallBold));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("\n\n(Signature of person responsible for deduction of tax)",smallBold));
        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
        row2.setPadding(2.5f);
        row2.setColspan(3);
        row2.setRowspan(2);	        
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("Date:",smallBold));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
//        String strDate="31.05.2014";
        String strDate = strFinancialYearEnd;
        row2 =new PdfPCell(new Paragraph(strDate,smallBold));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph("Designation",smallBold));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setPadding(2.5f);
        table1.addCell(row2);
        
        row2 =new PdfPCell(new Paragraph(uF.showData(strAuthorizeDesignation, ""),smallBold));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(2);
        table1.addCell(row2);
        
//        String strPartner = "SUHAS GAJANAN DESHPANDE";
        row2 =new PdfPCell(new Paragraph("Full Name: "+strAuthorisedName,smallBold));
        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
        row2.setPadding(2.5f);
        row2.setColspan(3);
        table1.addCell(row2);
        
        document.add(table1);
        
        document.close();
          
        if(getFormType()!=null && getFormType().trim().equalsIgnoreCase("approveRelease")) {
        	String filename="Form16_"+getEmp_id()+"_"+uF.getDateFormat(strFinancialYearStart,DATE_FORMAT, "yyyy") +".pdf";
        	String directory = CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_DOCUMENT+"/"+I_FORM16+"/"+getEmp_id();
        	
			FileUtils.forceMkdir(new File(directory));
			byte[] bytes = buffer.toByteArray();
			File form16File = new File(directory + File.separator + filename);
			FileOutputStream fileOuputStream = new FileOutputStream(form16File); 
			fileOuputStream.write(bytes);
			
			boolean isFileExist = false;
			if(form16File.isFile()) {
//			    System.out.println("isFile");
			    if(form16File.exists()) {
					isFileExist = true;
//				    System.out.println("exists");
				} else {
//				    System.out.println("exists fail");
				}   
			}
			
			
			if(isFileExist) {
				Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
				
				pst = con.prepareStatement("insert into form16_documents (emp_id,financial_year_start,financial_year_end,form16_name," +
						"approved_by,approved_date) values(?,?,?,?, ?,?)");
				pst.setInt(1, uF.parseToInt(getEmp_id()));
				pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(4, filename);
				pst.setInt(5, uF.parseToInt(strSessionEmpId));
				pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
				int x = pst.executeUpdate();
				if(x > 0) {
					String strDomain = request.getServerName().split("\\.")[0];
					String alertData = "<div style=\"float: left;\"> Form 16 has been released by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
					String alertAction = "MyForm16.action?pType=WR";
					
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(getEmp_id());
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
//					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//					userAlerts.setStrDomain(strDomain);
//					userAlerts.setStrEmpId(getEmp_id());
//					userAlerts.set_type(FORM16_RELEASE_ALERT);
//					userAlerts.setStatus(INSERT_ALERT);
//					Thread t = new Thread(userAlerts);
//					t.run();
					request.setAttribute(MESSAGE, SUCCESSM+uF.showData(hmEmployeeMap.get(getEmp_id()), "")+"'s Form 16 successfully approved and released."+END);
				} else {
					request.setAttribute(MESSAGE, ERRORM+uF.showData(hmEmployeeMap.get(getEmp_id()), "")+"'s Form 16 approve and release failed. Please, try again!"+END);
					
					form16File.delete();
				}
				
			}
			
        } else {
			String filename="Form16_"+getEmp_id()+"_"+uF.getDateFormat(strFinancialYearStart,DATE_FORMAT, "yyyy") +".pdf";
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename="+filename+"");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
        }
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	
}


	private Map<String, String> getEmpQuarterWiseTaxChallanPaidData(Connection con, UtilityFunctions uF, String strMonths, String strFinancialYearStart, String strFinancialYearEnd) {
		
		Map<String, String> hmChallanData = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from challan_details pg where challan_type=? and financial_year_from_date=? and financial_year_to_date=? " +
					"and emp_id=? and is_paid=true ");
			if(strMonths!=null && strMonths.length()>0){
                sbQuery.append(" and (");
                List<String> al = Arrays.asList(strMonths.split(","));
                for(int i=0; i<al.size(); i++){
                    sbQuery.append(" month like '%,"+al.get(i)+",%'");
                    
                    if(i<al.size()-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            } 
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, TDS);
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
//			System.out.println("pst===>>"+pst);
			double dblTaxAmount = 0.0d;
			StringBuilder sbBSRCode = new StringBuilder();
			StringBuilder sbChallanPaidDate = new StringBuilder();
			StringBuilder sbChallanNo = new StringBuilder();
			List<String> alBSRCode = new ArrayList<String>();
			List<String> alChallanPaidDate = new ArrayList<String>();
			List<String> alChallanNo = new ArrayList<String>();
			while(rs.next()) {
				
				double dblChallanPaidAmt = uF.parseToDouble(hmChallanData.get("CHALLAN_PAID_AMOUNT"));
				dblChallanPaidAmt += rs.getDouble("amount");
				hmChallanData.put("CHALLAN_PAID_AMOUNT", dblChallanPaidAmt+"");
				
				if(!alBSRCode.contains(rs.getString("brc_code"))) {
					if(hmChallanData.get("CHALLAN_PAID_BSR_CODE") == null) {
						sbBSRCode.append(rs.getString("brc_code"));
					} else {
						sbBSRCode.append(", "+rs.getString("brc_code"));
					}
					alBSRCode.add(rs.getString("brc_code"));
					hmChallanData.put("CHALLAN_PAID_BSR_CODE", sbBSRCode.toString());
				}
				
				if(!alChallanPaidDate.contains(rs.getString("paid_date"))) {
					if(hmChallanData.get("CHALLAN_PAID_DATE") == null) {
						sbChallanPaidDate.append(uF.getDateFormat(rs.getString("paid_date"), DBDATE, DATE_FORMAT));
					} else {
						sbChallanPaidDate.append(", "+uF.getDateFormat(rs.getString("paid_date"), DBDATE, DATE_FORMAT));
					}
					hmChallanData.put("CHALLAN_PAID_DATE", sbChallanPaidDate.toString());
					alChallanPaidDate.add(rs.getString("paid_date"));
				}
				
				if(!alChallanNo.contains(rs.getString("challan_no"))) {
					if(hmChallanData.get("PAID_CHALLAN_NO") == null) {
						sbChallanNo.append(rs.getString("challan_no"));
					} else {
						sbChallanNo.append(", "+rs.getString("challan_no"));
					}
					hmChallanData.put("PAID_CHALLAN_NO", sbChallanNo.toString());
					alChallanNo.add(rs.getString("challan_no"));
				}
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return hmChallanData;
	}


	private String getEmpQuarterWiseTaxDeducted(Connection con, UtilityFunctions uF, String strMonths, String strFinancialYearStart, String strFinancialYearEnd) {
		
		String strTotTaxAmt = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select * from emp_tds_details pg where financial_year_start=? and financial_year_end=? " +
				"and _month in ("+strMonths+") and emp_id=? order by _month");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
//			System.out.println("pst===>>"+pst);
			double dblTaxAmount = 0.0d;
			while(rs.next()) {
				
				double dblActualTDSAmount = rs.getDouble("actual_tds_amount");
				double dblFlatTDSAmount = rs.getDouble("flat_tds_amount");
				if(dblActualTDSAmount > 0) {
					dblTaxAmount = dblTaxAmount + dblActualTDSAmount;
				} else if(dblFlatTDSAmount>0) {
					dblTaxAmount = dblTaxAmount + dblFlatTDSAmount;
				}
			}
			rs.close();
			pst.close();
			strTotTaxAmt = uF.formatIntoTwoDecimal(dblTaxAmount);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return strTotTaxAmt;
	}
	
	
	public String getEmpQuarterWisePaidSalary(Connection con, UtilityFunctions uF, String strMonths, String strFinancialYearStart, String strFinancialYearEnd) {
		
		String strTotAmt = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select * from payroll_generation pg where financial_year_from_date=? and financial_year_to_date=? " +
				"and month in ("+strMonths+") and is_paid=true and emp_id=? order by month,earning_deduction desc");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
//			System.out.println("pst===>>"+pst);
			double dblNetAmount = 0.0d;
			while(rs.next()) {
				String strEarningDeduction = rs.getString("earning_deduction");
				double dblAmount = rs.getDouble("amount");
				
				if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("E")) {
					dblNetAmount = dblNetAmount +  dblAmount;
				} else if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("D")) {
//					dblNetAmount = dblNetAmount -  dblAmount;
				}
			}
			rs.close();
			pst.close();
			strTotAmt = uF.formatIntoTwoDecimal(dblNetAmount);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return strTotAmt;
	}


	public Map<String, String> getOtherDetailsMap(int empid) {
		Map<String, String> hmOtherDetailsMap = new HashMap<String, String>();
	
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
	
			con = db.makeConnection(con);
	
			pst = con.prepareStatement("select org_tan_no,org_pan_no from org_details where org_id=(select org_id from employee_official_details where emp_id=?)");
			pst.setInt(1,empid);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmOtherDetailsMap.put("DEDUCTOR_TAN", rs.getString("org_tan_no"));
				hmOtherDetailsMap.put("DEDUCTOR_PAN", rs.getString("org_pan_no"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select emp_pan_no from employee_personal_details where emp_per_id=?");
			pst.setInt(1, empid);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmOtherDetailsMap.put("EMPLOYEE_PAN",rs.getString("emp_pan_no"));
			}
			rs.close();
			pst.close();
	
			pst = con.prepareStatement("select wlocation_cit_address from work_location_info where wlocation_id=(select wlocation_id from employee_official_details where emp_id=?)");
			pst.setInt(1, empid);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmOtherDetailsMap.put("CIT_ADDRESS",rs.getString("wlocation_cit_address"));
			}
			rs.close();
			pst.close();
			
	//===start parvez date: 01-04-2022===		
			pst = con.prepareStatement("select org_pan_no,org_tan_no from prev_earn_deduct_details where emp_id=?");
			pst.setInt(1, empid);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmOtherDetailsMap.put("PREV_ORG_DEDUCTOR_TAN", rs.getString("org_tan_no"));
				hmOtherDetailsMap.put("PREV_ORG_DEDUCTOR_PAN", rs.getString("org_pan_no"));
			}
			rs.close();
			pst.close();
	//===end parvez date: 01-04-2022===		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmOtherDetailsMap;
	} 
		
	private void getSearchAutoCompleteData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
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
			
			
			SortedSet<String> setSearchList = new TreeSet<String>();
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("SELECT eod.emp_id,epd.emp_fname,epd.emp_mname,epd.emp_lname,epd.empcode,eod.depart_id,epd.emp_pan_no,epd.employment_end_date," +
					"epd.joining_date,epd.emp_address1,epd.emp_city_id, epd.emp_gender FROM employee_official_details eod, employee_personal_details epd " +
					"WHERE eod.emp_id > 0 and epd.emp_per_id=eod.emp_id and eod.emp_id in (select distinct emp_id from payroll_generation where financial_year_from_date = ? and financial_year_to_date = ?) and is_form16=true ");
			if(getF_level()!=null && getF_level().length>0) {
	            sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	        }
		    if(getF_department()!=null && getF_department().length>0) {
	            sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
	        if(getF_service()!=null && getF_service().length>0) {
	            sbQuery.append(" and (");
	            for(int j=0; j<getF_service().length; j++) {
	            	sbQuery.append(" eod.service_id like '%,"+getF_service()[j]+",%'");
	                if(j<getF_service().length-1) {
	                    sbQuery.append(" OR "); 
	                }
	            }
	            sbQuery.append(" ) ");
	            
	        } 
	        if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by epd.emp_fname,epd.emp_lname"); 
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst == Search ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strMiddleName = "";
				
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strMiddleName = " "+rs.getString("emp_mname");
					}
				}
				
				setSearchList.add(rs.getString("emp_fname")+strMiddleName+" "+rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbData = null;
			Iterator<String> it = setSearchList.iterator();
			while (it.hasNext()) {
				String strData = it.next();
				if(sbData == null) {
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null) {
				sbData = new StringBuilder();
			}
			request.setAttribute("sbData", sbData.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private String loadForm16Filter(UtilityFunctions uF) {
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else{
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
				
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	
	public void viewForm16(CommonFunctions CF) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF =new UtilityFunctions();
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
//			System.out.println("strFinancialYearStart====>"+strFinancialYearStart+"----strFinancialYearEnd===>"+strFinancialYearEnd);
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME)); 
			
			
			Map<String, String> hmEmpSlabType = CF.getEmpIncomeTaxSlabTypeMap(con, CF, strFinancialYearStart, strFinancialYearEnd);
			
			Map<String, String> hmOrg = CF.getOrgDetails(con, uF, getF_org());
			
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			if(hmEmpCodeName == null) hmEmpCodeName = new HashMap<String, String>();
			Map<String, String> hmSectionMap = CF.getSectionMap(con,strFinancialYearStart,strFinancialYearEnd);
			Map<String,String> hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
			Map<String, String> hmDept =CF.getDeptMap(con);	
			Map<String, String> hmEmpAgeMap =CF.getEmpAgeMap(con, CF);
//			Map<String, String> hmEmpSlabMap =CF.getEmpSlabMap(con, CF);
			
			Map hmEmpMertoMap = new HashMap();
			Map hmEmpWlocationMap = new HashMap();
			Map hmEmpStateMap = new HashMap();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
		
			Map<String,Map<String,String>> hmPrevEmpTds = CF.getPrevEmpTdsDetails(con,uF,strFinancialYearStart,strFinancialYearEnd);
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("SELECT count(eod.emp_id) as empCount FROM employee_official_details eod, employee_personal_details epd " +
					"WHERE eod.emp_id > 0 and epd.emp_per_id=eod.emp_id and eod.emp_id in (select distinct emp_id from payroll_generation where financial_year_from_date = ? and financial_year_to_date = ?) and is_form16=true ");
			if(getF_level()!=null && getF_level().length>0) {
	            sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	        }
		    if(getF_department()!=null && getF_department().length>0) {
	            sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
	        if(getF_service()!=null && getF_service().length>0) {
	            sbQuery.append(" and (");
	            for(int j=0; j<getF_service().length; j++) {
	            	sbQuery.append(" eod.service_id like '%,"+getF_service()[j]+",%'");
	                
	                if(j<getF_service().length-1) {
	                    sbQuery.append(" OR "); 
	                }
	            }
	            sbQuery.append(" ) ");
	            
	        } 
	        if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
	        if(getStrSearch()!=null && !getStrSearch().trim().equals("") && !getStrSearch().trim().equalsIgnoreCase("NULL")) {
	        	if(flagMiddleName) {
					sbQuery.append(" and (upper(emp_fname)||' '||upper(emp_mname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%' or upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%')");
				} else {
					sbQuery.append(" and upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%'");
				}
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst=0=="+pst);
			rs = pst.executeQuery();
			int recCnt = 0;
			int pageCount = 0;
			while(rs.next()) {
				recCnt = rs.getInt("empCount");
				pageCount = rs.getInt("empCount")/10;
				if(rs.getInt("empCount")%10 != 0) {
					pageCount++;
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("pageCount", pageCount+"");
			request.setAttribute("recCnt", recCnt+"");
			
			sbQuery=new StringBuilder();
			sbQuery.append("SELECT eod.emp_id,epd.emp_fname,epd.emp_mname,epd.emp_lname,epd.empcode,eod.depart_id,epd.emp_pan_no,epd.employment_end_date," +
				"epd.joining_date,epd.emp_address1,epd.emp_city_id, epd.emp_gender,eod.slab_type FROM employee_official_details eod, employee_personal_details epd " +
				"WHERE eod.emp_id > 0 and epd.emp_per_id=eod.emp_id and eod.emp_id in (select distinct emp_id from payroll_generation where financial_year_from_date = ? and financial_year_to_date = ?) and is_form16=true ");
			if(getF_level()!=null && getF_level().length>0) {
	            sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	        }
		    if(getF_department()!=null && getF_department().length>0) {
	            sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
	        if(getF_service()!=null && getF_service().length>0) {
	            sbQuery.append(" and (");
	            for(int j=0; j<getF_service().length; j++) {
	            	sbQuery.append(" eod.service_id like '%,"+getF_service()[j]+",%'");
	                
	                if(j<getF_service().length-1) {
	                    sbQuery.append(" OR "); 
	                }
	            }
	            sbQuery.append(" ) ");
	        } 
	        if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
	        if(getStrSearch()!=null && !getStrSearch().trim().equals("") && !getStrSearch().trim().equalsIgnoreCase("NULL")) {
	        	if(flagMiddleName) {
					sbQuery.append(" and (upper(emp_fname)||' '||upper(emp_mname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%' or upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%')");
				} else {
					sbQuery.append(" and upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%'");
				}
			}
//	        sbQuery.append(" and epd.emp_per_id in (724) ");
			sbQuery.append(" order by epd.emp_fname, epd.emp_lname"); 
			int intOffset = uF.parseToInt(minLimit);
			sbQuery.append(" limit 10 offset "+intOffset+"");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst=1=="+pst);
			rs = pst.executeQuery();
			Map<String, String> hmEmployeeMap = new LinkedHashMap<String, String>();
			Map<String, String> hmEmpJoiningDate = new HashMap<String, String>();
			Map<String, String> hmEmpCode = new HashMap<String, String>();
			Map<String, String> hmEmpDepartment = new HashMap<String, String>();
			Map<String, String> hmEmpPanNo = new HashMap<String, String>();
			Map<String, String> hmEmpEndDate = new HashMap<String, String>();
			Map<String, String> hmEmpAddressMap = new HashMap<String, String>();
			Map<String, String> hmEmpGenderMap = new HashMap<String, String>();

			StringBuilder sbEmp = null;
			while (rs.next()) {
				
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
				
				String empAddress = rs.getString("emp_address1")!=null && !rs.getString("emp_address1").equals("") ? rs.getString("emp_address1") : "";
				hmEmpAddressMap.put(rs.getString("emp_id"), empAddress+"\n"+uF.showData(rs.getString("emp_city_id"), ""));
				hmEmpGenderMap.put(rs.getString("emp_id"), rs.getString("emp_gender"));
				if(sbEmp == null) {
					sbEmp = new StringBuilder();
					sbEmp.append(rs.getString("emp_id"));
				} else {
					sbEmp.append(","+rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmployeeMap =====>> " + hmEmployeeMap);
			
	//		System.out.println("=======1========"+new Date());
			
			if(sbEmp!=null && sbEmp.length() > 0) {
	//			System.out.println("=======sbEmp========"+sbEmp.toString());
				List<String> alEmp = new ArrayList<String>(Arrays.asList(sbEmp.toString().split(",")));
	//			System.out.println("=======alEmp========"+alEmp);
				
				pst = con.prepareStatement("select * from form16_documents where financial_year_start=? and financial_year_end=? and emp_id in ("+sbEmp.toString()+")");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
	//			System.out.println(" pst==>"+pst);
				Map<String, Map<String, String>> hmEmpForm16Status = new HashMap<String, Map<String, String>>();
				while(rs.next()) {
					alEmp.remove(rs.getString("emp_id"));
					
					Map<String, String> hmEmpForm16 = new HashMap<String, String>();
					hmEmpForm16.put("FORM16_DOCUMENT_ID", rs.getString("form16_document_id"));
					hmEmpForm16.put("EMP_ID", rs.getString("emp_id"));
					hmEmpForm16.put("FINANCIAL_YEAR_START", uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, CF.getStrReportDateFormat()));
					hmEmpForm16.put("FINANCIAL_YEAR_END", uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, CF.getStrReportDateFormat()));
					hmEmpForm16.put("FORM16_NAME", rs.getString("form16_name"));
					hmEmpForm16.put("APPROVED_BY", uF.showData(hmEmpCodeName.get(rs.getString("approved_by")), ""));
					hmEmpForm16.put("APPROVED_DATE", uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()));
					
					hmEmpForm16Status.put(rs.getString("emp_id"), hmEmpForm16);
				}
				rs.close();
				pst.close();
	//			System.out.println("=======after alEmp========"+alEmp);
				if(alEmp!=null && alEmp.size() > 0) {
					String strEmpIds = StringUtils.join(alEmp.toArray(),",");
					
					Map<String, String> hmReimbursementAmt = new HashMap<String, String>();
					if(CF.getIsReceipt()) {
						String[] firstArr = CF.getPayCycleFromDate(con, strFinancialYearStart, CF.getStrTimeZone(), CF, getF_org());
						String[] secondArr = null;
						if(uF.parseToInt(uF.getDateFormat(hmOrg.get("ORG_START_PAYCYCLE"), DATE_FORMAT, "dd")) > 1) {
							secondArr = CF.getPrevPayCycleByOrg(con, strFinancialYearEnd, CF.getStrTimeZone(), CF, getF_org());
						} else {
							secondArr = CF.getPayCycleFromDate(con, strFinancialYearEnd, CF.getStrTimeZone(), CF, getF_org());
						}
						pst = con.prepareStatement("select emp_id,sum(reimbursement_amount) as reimbursement_amount from emp_reimbursement where approval_1=1 and ispaid=true " +
							"and (ref_document is null or ref_document='' or upper(ref_document) ='NULL') and from_date>=? and to_date<=? and emp_id in ("+strEmpIds+") group by emp_id");
						pst.setDate(1, uF.getDateFormat(firstArr[0], DATE_FORMAT));
						pst.setDate(2, uF.getDateFormat(secondArr[1], DATE_FORMAT));
						rs = pst.executeQuery();
	//					System.out.println("pst====>"+pst);
						while(rs.next()) {
							hmReimbursementAmt.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("reimbursement_amount")));
						}
						rs.close();
						pst.close();
					}
					
					pst = con.prepareStatement("SELECT count(*) as cnt,emp_id FROM emp_family_members WHERE member_type='CHILD' and emp_id in ("+strEmpIds+") group by emp_id");
					rs = pst.executeQuery();
		//			System.out.println(" pst==>"+pst);
					Map<String, String> hmEmpChildCnt = new HashMap<String, String>();
					while(rs.next()) {
						hmEmpChildCnt.put(rs.getString("emp_id"), rs.getString("cnt"));
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select count(*) as cnt,emp_id from (select distinct(paycycle),emp_id from payroll_generation pg " +
						"where financial_year_from_date=? and financial_year_to_date =? and is_paid = true " +
						"and salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") " +
						" and earning_deduction='E' and pg.emp_id in ("+strEmpIds+") group by paycycle,emp_id order by emp_id,paycycle) a group by emp_id");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					rs = pst.executeQuery();
		//			System.out.println(" pst==>"+pst);
					Map<String, String> hmMonthPaid = new HashMap<String, String>();
					while(rs.next()) {
						hmMonthPaid.put(rs.getString("emp_id"), rs.getString("cnt"));
					}
					rs.close();
					pst.close();
		//			System.out.println("hmMonthPaid==>"+hmMonthPaid);
					
					pst = con.prepareStatement("select distinct(pg.emp_id),pg.month,pg.year,pg.paycycle,pg.paid_days,pg.total_days from (" +
						"select max(paycycle) as paycycle,emp_id from (select distinct(paycycle),emp_id from payroll_generation pg " +
						"where financial_year_from_date=? and financial_year_to_date =? and is_paid = true " +
						"and salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") " +
						"and earning_deduction='E' and pg.emp_id in ("+strEmpIds+") group by paycycle,emp_id order by emp_id,paycycle) a group by emp_id ) a,payroll_generation pg " +
						"where a.emp_id=pg.emp_id and a.paycycle=pg.paycycle and pg.emp_id in (select emp_per_id from employee_personal_details " +
						"where is_alive=false and employment_end_date between ? and ?) order by pg.emp_id");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		//			System.out.println(" pst==>"+pst);
					rs = pst.executeQuery();
					Map<String, String> hmLastPaycycle = new HashMap<String, String>();
					while(rs.next()) {
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
					
					pst = con.prepareStatement("select * from employee_personal_details where is_alive=false and employment_end_date between ? and ? and emp_per_id in ("+strEmpIds+") order by emp_per_id");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		//			System.out.println(" pst==>"+pst);
					rs = pst.executeQuery();
					Map<String, String> hmExEmp = new HashMap<String, String>();
					while(rs.next()) {
						hmExEmp.put(rs.getString("emp_per_id"), uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));
					}
					rs.close();
					pst.close();
		//			System.out.println("hmExEmp==>"+hmExEmp);
					
					Map hmPayrollDetails = new HashMap();			
//					List al = new ArrayList();			
//					double dblInvestmentExemption = 0.0d;
					
					Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
					pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1 ");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		//			System.out.println(" pst==>"+pst);
					rs = pst.executeQuery();
					while(rs.next()) {
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
					
					pst = con.prepareStatement("select sum(amount) as amount,salary_head_id,emp_id,earning_deduction from payroll_generation pg " +
						"where financial_year_from_date=? and financial_year_to_date =? and is_paid = true and " +
						"salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") " +
						" and emp_id in ("+strEmpIds+") group by salary_head_id, emp_id,earning_deduction order by emp_id,earning_deduction desc");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
	//				System.out.println(" pst==>"+pst);
					rs = pst.executeQuery();					
					String strEmpIdNew = null;
					String strEmpIdOld = null;
					Map hmInner1 = new HashMap();
					double dblGross = 0.0d;
					Map<String,String> hmLeaveEncashmet = new HashMap<String, String>();
					while(rs.next()) {
						strEmpIdNew = rs.getString("emp_id");
						if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
							hmInner1 = new HashMap();
	//						dblGross = 0.0d;
							dblGross = uF.parseToDouble(hmReimbursementAmt.get(strEmpIdNew)); 
						}
										
						hmInner1.put(rs.getString("salary_head_id"), rs.getString("amount"));   
						if(rs.getString("earning_deduction").equalsIgnoreCase("E")) {
							dblGross += rs.getDouble("amount");
						}
						/*if(uF.parseToInt(strEmpIds)==320){
							System.out.println("Form16/5721--dblGross="+dblGross);
						}*/
						hmInner1.put("GROSS", dblGross+""); 
						hmPayrollDetails.put(strEmpIdNew, hmInner1);
						
						if(uF.parseToInt(rs.getString("salary_head_id")) == LEAVE_ENCASHMENT && hmExEmp.containsKey(rs.getString("emp_id"))) {
							hmLeaveEncashmet.put(rs.getString("emp_id"), rs.getString("amount"));
						}
						
						strEmpIdOld = strEmpIdNew;
					}
					rs.close();
					pst.close();
		//			System.out.println("=======3========"+new Date());
	//				System.out.println("hmPayrollDetails 464 ===>> "  +hmPayrollDetails.get("464"));
					
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
		//			System.out.println(" pst==>"+pst);
					List<List<String>> chapter1SectionList = new ArrayList<List<String>>();
					List<List<String>> chapter2SectionList = new ArrayList<List<String>>();
					Map<String, String> hmSectionPFApplicable = new HashMap<String, String>();
					while(rs.next()) {
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
//						System.out.println("Form16/5778--slab_type="+rs.getString("slab_type"));
						hmSectionPFApplicable.put(rs.getString("section_id")+"_"+rs.getString("slab_type"), rs.getString("is_pf_applicable"));
					}
					rs.close();
					pst.close();
//					System.out.println("hmSectionPFApplicable ===>> " + hmSectionPFApplicable);
					
					pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					rs = pst.executeQuery();
		//			System.out.println(" pst==>"+pst);
					Map hmHRAExemption = new HashMap();
					while(rs.next()) {
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
					
//					dblInvestmentExemption = uF.parseToDouble(""+hmSectionLimitA.get("3"));
					
		//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, section_details sd where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to=? and status = true and section_code in ('HRA') and trail_status = 1 group by emp_id ");
					pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, exemption_details ed " +
						"where ed.salary_head_id=id.salary_head_id and id.fy_from=? and id.fy_to=? and status=true and ed.salary_head_id=? " +
						"and trail_status=1 and ed.exemption_from=? and ed.exemption_to=? and emp_id in ("+strEmpIds+") and parent_section = 0 group by emp_id ");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3, HRA);
					pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					rs = pst.executeQuery();
		//			System.out.println(" pst==>"+pst);
					Map hmRentPaid = new HashMap();
					while(rs.next()) {
						hmRentPaid.put(rs.getString("emp_id"), rs.getString("amount_paid"));
					}
					rs.close();
					pst.close();
		//			System.out.println("=======7========"+new Date());
					
					pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, sd.slab_type, emp_id from investment_details id, section_details sd where sd.section_id=id.section_id " +
						"and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and section_code not in ('HRA') " +
						"and isdisplay=true and parent_section=0 and under_section=8 and emp_id in ("+strEmpIds+") group by emp_id, sd.section_id,sd.slab_type order by emp_id"); // and sd.section_id !=11
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
		//			System.out.println("=======8========"+new Date());
		//			System.out.println("hmEmpInvestment==="+hmEmpInvestment.get("654"));    
					
					
					Map<String, Map<String, Map<String, Map<String, String>>>> hmEmpIncludeSubSectionData = new HashMap<String, Map<String, Map<String, Map<String, String>>>>();
					pst = con.prepareStatement("select id.*, sd.section_id, sd.is_adjusted_gross_income_limit, sd.include_sub_section,sd.slab_type from investment_details id, section_details sd where sd.section_id=id.section_id and " +
						"id.fy_from=? and id.fy_to=? and status=true and trail_status=1 and sd.financial_year_start=? and sd.financial_year_end=? and section_code not in ('HRA')  and isdisplay=true and parent_section>0 " +
						"and emp_id in ("+strEmpIds+") and sub_section_no>0 and (is_adjusted_gross_income_limit is null or is_adjusted_gross_income_limit=false) and (include_sub_section is not null or include_sub_section !='') " +
						"order by emp_id,sd.section_id,sd.slab_type"); //and sd.section_id !=11
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					rs = pst.executeQuery();
//					System.out.println(" pst==>"+pst);
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
						"section_code not in ('HRA')  and isdisplay=true and parent_section>0 and under_section=8 and emp_id in ("+strEmpIds+") and sub_section_no>0 and " +
						"(is_adjusted_gross_income_limit is null or is_adjusted_gross_income_limit = false) order by emp_id,sd.section_id"); //and sd.section_id !=11
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					rs = pst.executeQuery();
//					System.out.println(" pst==>"+pst);
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
//							---------------
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
//							--------------------
							
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
//							System.out.println(strSectionId+" -- dblSecInvestment =====>> "  +dblSecInvestment);
							
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
					//===start parvez date: 18-05-2022 Note: Repeadted Code===		
							/*if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
								dblSecInvestment += dblSubSecLimit;
							} else {
								dblSecInvestment += dblInvestment;
							}*/
					//===end parvez date: 18-05-2022===		
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
						
					
					pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, sd.slab_type, emp_id from investment_details id, section_details sd " +
						"where sd.section_id=id.section_id and id.fy_from=? and id.fy_to=? and status=true and trail_status = 1 and sd.financial_year_start=? " +
						"and sd.financial_year_end=? and section_code not in ('HRA')  and isdisplay=true and parent_section=0 and " +
						"under_section=9 and emp_id in ("+strEmpIds+") group by emp_id, sd.section_id, sd.slab_type order by emp_id "); //and sd.section_id !=11
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
					
					while(rs.next()) {
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
						
						if(rs.getString("emp_id").equals("618")) {
		//					System.out.println("dblInvestment==="+dblInvestment);
		//					System.out.println("dblInvestmentLimit==="+dblInvestmentLimit);
		//					System.out.println("dblInvestmentEmp==="+dblInvestmentEmp);					
						}
					}
					rs.close();
					pst.close();
					
					
					pst = con.prepareStatement("select id.*, sd.section_id, sd.slab_type, sd.is_adjusted_gross_income_limit, sd.include_sub_section from investment_details id, section_details sd where sd.section_id=id.section_id and " +
						"id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and " +
						"section_code not in ('HRA')  and isdisplay=true and parent_section>0 and under_section=9 and emp_id in ("+strEmpIds+") and sub_section_no>0 and " +
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
//						System.out.println("emp_id ============>> " + rs.getString("emp_id") + " -- dblInvestment ===>> " + dblInvestment+" -- strSectionId ===>> " + strSectionId +" -- strSubSecNo ===>> " + strSubSecNo);
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
//							---------------
//							System.out.println("emp_id ============>> " + rs.getString("emp_id"));
							Map<String,String> hmSubSecMinusAmt = hmEmpSubSecMinusAmt.get(rs.getString("emp_id"));
							if(hmSubSecMinusAmt==null) hmSubSecMinusAmt = new HashMap<String, String>();
							
							Map<String,String> hmInvest = hmEmpInvestment1.get(rs.getString("emp_id"));
							if(hmInvest==null) hmInvest = new HashMap<String, String>();
//							System.out.println("emp_id ============>> " + rs.getString("emp_id") + " -- hmInvest =====>> " + hmInvest);
//							System.out.println("emp_id ============>> " + rs.getString("emp_id") + " -- oldSectionId =====>> " + oldSectionId+" -- strSectionId =====>> " + strSectionId);
							double dblSecInvestment=0;
							double dblSubSecMinusInvestment=0;
							if((oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) && hmInvest.keySet().contains(strSectionId)) {
								dblSecInvestment = 0;
								dblSubSecMinusInvestment=0;
							} else {
								dblSecInvestment = uF.parseToDouble(hmInvest.get(strSectionId));
								dblSubSecMinusInvestment = uF.parseToDouble(hmSubSecMinusAmt.get(strSectionId));
							}
//							--------------------
							
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
//							System.out.println("dblSubSecLimit ===>> " + dblSubSecLimit +" -- dblInvestment ===>> " + dblInvestment);
							
							if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
								dblSecInvestment += dblSubSecLimit;
							} else {
//								System.out.println("dblSubSecMinActAmt ===>> " + dblSubSecMinActAmt +" -- dblSubSecMinusInvestment ===>> " + dblSubSecMinusInvestment);
								if(dblInvestment>0 && dblSubSecMinActAmt>0 && dblSubSecMinActAmt>dblSubSecMinusInvestment) {
//									System.out.println("dblSubSecLimit =====================>>" + dblSubSecLimit);
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
//							System.out.println(strSectionId+" -- dblSecInvestment =====>> "  +dblSecInvestment);
							
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
					
					
		//			System.out.println("hmEmpInvestment1==="+hmEmpInvestment1.get("654"));
		//			System.out.println("=======9========"+new Date());
					
					/*pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
							"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.section_id=3 " +
							"and isdisplay=true and parent_section=0 and sd.financial_year_start=? and sd.financial_year_end=? " +
							"and emp_id in ("+strEmpIds+") group by emp_id, sd.section_id order by emp_id");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					rs = pst.executeQuery();
		//			System.out.println(" pst==>"+pst);
					Map<String, String> hmEmp80C = new HashMap<String, String>();
					dblInvestmentLimit = 0;
					dblInvestmentCeilingLimit = 0;
					dblInvestmentEmp = 0;
					
					while(rs.next()) {
						String strSectionId = rs.getString("section_id"); 
						double dblInvestment = rs.getDouble("amount_paid");
						
						if(hmSectionLimitA.containsKey(strSectionId)) {
							dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
							dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_CEILING_AMT"));
							if(dblInvestmentCeilingLimit>0) {
								dblInvestmentLimit = dblInvestmentCeilingLimit;
							}
						} else{
							dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
							dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
							dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_CEILING_AMT"));
							if(dblInvestmentCeilingLimit>0) {
								dblInvestmentLimit = dblInvestmentCeilingLimit;
							}
						}
						
						
						if(dblInvestment>=dblInvestmentLimit) {
							dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
							hmEmp80C.put(rs.getString("emp_id"), dblInvestmentLimit+"");
						} else{
							dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestment;
							hmEmp80C.put(rs.getString("emp_id"), dblInvestment+"");
						}
						
					}
					rs.close();
					pst.close();*/			
		//			System.out.println("hmEmpInvestment1==="+hmEmpInvestment1.get("654"));
		//			System.out.println("=======10========"+new Date());
					
					/*pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id,sd.slab_type, emp_id from investment_details id, section_details sd " +
						" where sd.section_id=id.section_id and id.fy_from=? and id.fy_to=? and status=true and trail_status=1 and sd.section_id=1 " +
						" and isdisplay=true and parent_section=0 and sd.financial_year_start=? and sd.financial_year_end=? and emp_id in ("+strEmpIds+") " +
						"group by emp_id, sd.section_id,sd.slab_type order by emp_id ");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					rs = pst.executeQuery();
		//			System.out.println(" pst==>"+pst);
					Map<String, String> hmEmp80D = new HashMap<String, String>();
					dblInvestmentLimit = 0;
					dblInvestmentCeilingLimit = 0;
					dblInvestmentEmp = 0;
					
					while(rs.next()) {
						String strSectionId = rs.getString("section_id");
						String slabType = rs.getString("slab_type");
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
						
						
						if(dblInvestment>=dblInvestmentLimit) {
							dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
							hmEmp80D.put(rs.getString("emp_id"), dblInvestmentLimit+"");
						} else{
							dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestment;
							hmEmp80D.put(rs.getString("emp_id"), dblInvestment+"");
						}
						
					}
					rs.close();
					pst.close();*/
		//			System.out.println("=======11========"+new Date());
					
					pst = con.prepareStatement("select * from investment_details id, section_details sd  where sd.section_id=id.section_id and id.fy_from=? and id.fy_to=? " +
						"and status=true and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and section_code not in ('HRA') and isdisplay=true " +
						"and parent_section>0 and emp_id in ("+strEmpIds+") order by emp_id"); //and sd.section_id !=11
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
					/*pst = con.prepareStatement("select * from section_details where section_id = 11 and financial_year_start=? and financial_year_end=?");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					rs = pst.executeQuery();
		//			System.out.println(" pst==>"+pst);
					double dblLoanExemptionLimit = 0;
					while (rs.next()) {
						dblLoanExemptionLimit = rs.getDouble("section_exemption_limit");
						if(rs.getBoolean("is_ceiling_applicable")) {
							dblLoanExemptionLimit = rs.getDouble("ceiling_amount");
						}
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details where fy_from =? and fy_to =? and status = true" +
						" and trail_status = 1 and parent_section=0 and  section_id in (select section_id from section_details where section_id = 11 and financial_year_start=? " +
						"and financial_year_end=?) and emp_id in ("+strEmpIds+") group by emp_id");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					rs = pst.executeQuery();
		//			System.out.println(" pst==>"+pst);
					Map<String, String> hmEmpHomeLoanMap = new HashMap<String, String>();
					while (rs.next()) {
						
						if(uF.parseToDouble(rs.getString("amount_paid"))>dblLoanExemptionLimit) {
							hmEmpHomeLoanMap.put(rs.getString("emp_id"), dblLoanExemptionLimit+"");
						} else{
							hmEmpHomeLoanMap.put(rs.getString("emp_id"), rs.getString("amount_paid"));
						}
					}
					rs.close();
					pst.close();*/
					
		//			System.out.println("=======12========"+new Date());
					/**
					 * HOME LOAN INTEREST EXEMPTION 
					 */
					
					Map<String,String> hmEmpIncomeFromOtherSourcesMap = new HashMap<String,String>();
					Map<String,String> hmEmpLessIncomeFromOtherSourcesMap = new HashMap<String,String>();
					pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, sd.slab_type, emp_id from investment_details id, section_details sd " +
						"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and status = true and sd.section_id >=13 and sd.section_id <=17 " +
						"and parent_section = 0 and isdisplay=false and financial_year_start=? and financial_year_end=? and emp_id in ("+strEmpIds+") group by emp_id, sd.section_id,sd.slab_type order by emp_id");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//						System.out.println("pst ========>> " + pst);
					rs = pst.executeQuery();			
					double dblInvestmentIncomeSourcesEmp = 0;		
					while (rs.next()) {
						String strSectionId = rs.getString("section_id");
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
					
	//				Map<String,String> hmEmpUnderSection10Map = new HashMap<String,String>();			
					
					
					
		//			System.out.println("=======13========"+new Date());
		//			System.out.println("hmUnderSection10Map====="+hmUnderSection10Map);
		//			System.out.println("hmUnderSection10PaidMap====="+hmUnderSection10PaidMap);
					
					Map<String, Map<String, String>> hmUnderSection10_16Map = new HashMap<String, Map<String, String>>();
					Map<String, Map<String, String>> hmUnderSection10_16PaidMap = new HashMap<String, Map<String, String>>();
					
					Map hmTaxLiability = new HashMap();
					Set set = hmPayrollDetails.keySet();
					Iterator it = set.iterator();
					while(it.hasNext()) {
						Map hmTaxInner = new HashMap();
						Map<String, String> hmUS10_16_SalHeadData = new HashMap<String, String>();
						String strEmpId = (String)it.next();
						String slabType = hmEmpSlabType.get(strEmpId);
//						System.out.println("slabType =================>> " + slabType);
						
						Map hmEmpPayrollDetails = (Map)hmPayrollDetails.get(strEmpId);
						if(hmEmpPayrollDetails == null) hmEmpPayrollDetails = new HashMap();
						
						pst = con.prepareStatement("select sum(amount_paid) as amount_paid, ed.salary_head_id, emp_id from investment_details id, exemption_details ed " +
							"where ed.salary_head_id=id.salary_head_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and status = true and under_section in (4,5) " +
							"and exemption_from=? and exemption_to=? and (ed.slab_type=? or ed.slab_type=2) and id.salary_head_id>0 and id.parent_section=0 " +
							"and emp_id in ("+strEmpId+") group by emp_id, ed.salary_head_id order by emp_id");
						pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setInt(5, uF.parseToInt(slabType));
//						System.out.println("pst========"+pst);
						rs = pst.executeQuery();			
	//						double dblUnderSection10Emp = 0;		
						while (rs.next()) {
							String strsalaryheadid = rs.getString("salary_head_id");
							double dblInvestment = rs.getDouble("amount_paid");				
							if(dblInvestment == 0) {
								dblInvestment = uF.parseToDouble((String)hmEmpPayrollDetails.get(strsalaryheadid));
							}
							dblInvestmentLimit = uF.parseToDouble((String)hmExemption.get(strsalaryheadid));
							double dblSalHeadAmt = uF.parseToDouble((String)hmEmpPayrollDetails.get(strsalaryheadid));
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
//						System.out.println("hmUnderSection10_16Map ===>> " + hmUnderSection10_16Map);
//						System.out.println("hmUnderSection10_16PaidMap ===>> " + hmUnderSection10_16PaidMap);
							
						pst = con.prepareStatement("select * from exemption_details where exemption_from=? and exemption_to=? and (slab_type=? or slab_type=2)");
						pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setInt(3, uF.parseToInt(slabType));
//						System.out.println("pst========"+pst);
						rs = pst.executeQuery();			
						while (rs.next()) {
							String strsalaryheadid = rs.getString("salary_head_id");
							
							double dblInvestment = uF.parseToDouble((String)hmEmpPayrollDetails.get(strsalaryheadid));
							if(uF.parseToInt(strsalaryheadid) == LTA) {
								dblInvestmentLimit = dblInvestment;
							} else {
								dblInvestmentLimit = uF.parseToDouble((String)hmExemption.get(strsalaryheadid));
							}
							
							Map<String, String> hmInner= (Map<String, String>)hmUnderSection10_16Map.get(strEmpId);
							if(hmInner==null) hmInner=new HashMap<String, String>();
							
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
//						System.out.println("1 hmUnderSection10_16Map ===>> " + hmUnderSection10_16Map);
//						System.out.println("1 hmUnderSection10_16PaidMap ===>> " + hmUnderSection10_16PaidMap);
						
						String strLevel = CF.getEmpLevelId(con, strEmpId);
						int nEmpOrgId = uF.parseToInt(CF.getEmpOrgId(con, uF, strEmpId));
						
						Map<String, String> hmEmployerPF = new HashMap<String, String>();
						boolean IsAddEmployerPFInTDSCal = CF.getFeatureManagementStatus(request, uF, F_ADD_EMPLYOER_PF_IN_TDS_CALCLATION);
						if(IsAddEmployerPFInTDSCal) {
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
	//						System.out.println(" pst==>"+pst);
					 		while(rs.next()) {
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
	//					System.out.println("dblPerkAlignTDSAmount====>"+dblPerkAlignTDSAmount);
						
						double dblBasic = uF.parseToDouble((String)hmEmpPayrollDetails.get(BASIC+""));
						double dblDA = uF.parseToDouble((String)hmEmpPayrollDetails.get(DA+""));
						
						String strTDSAmt = (String)hmSalaryHeadMap.get(TDS+"");
						
	//					String strConveyanceAllowance = (String)hmSalaryHeadMap.get(CONVEYANCE_ALLOWANCE+"");
	//					String strProfessionalTax = (String)hmSalaryHeadMap.get(PROFESSIONAL_TAX+"");
						
						double dblGross1 = uF.parseToDouble((String)hmEmpPayrollDetails.get("GROSS"));
//						System.out.println("1 dblGross1====>"+dblGross1);
						dblGross1 = dblGross1 - dblPerkAlignTDSAmount;
//						System.out.println("2 dblGross1====>"+dblGross1);
	//					double dblGratuity = 0.0d;
	//					if(uF.parseToInt(strEmpId) == 589 && strFinancialYearStart.trim().equals("01/04/2016") && strFinancialYearEnd.trim().equals("31/03/2017")) {
	//						dblGratuity = 14423d;
	//						dblGross1 = dblGross1 + dblGratuity; 
	//					}
						
						hmEmpPayrollDetails.put("GROSS",""+dblGross1);
						
						
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
						
	//					double dblProfessionalTaxPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(PROFESSIONAL_TAX+""));
	//					double dblProfessionalTaxExemptLimit = uF.parseToDouble((String)hmExemption.get(PROFESSIONAL_TAX+""));
	//					double dblProfessionalTaxExempt = Math.min(dblProfessionalTaxPaid, dblProfessionalTaxExemptLimit);
						
				//===start parvez date: 18-05-2022===		
//						double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_EDU_TAX"));
//						double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_STD_TAX"));
						double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_EDU_TAX"));
						double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_STD_TAX"));
				//===end parvez date: 18-05-2022===		
						double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_FLAT_TDS"));
						
						// Exemptions needs to worked out as other exemptions				
						   
//						double dblHomeLoanTaxExempt = uF.parseToDouble(hmEmpHomeLoanMap.get(strEmpId));
							
	//					double dblOtherExemptions = 0;
		//				Set setSalaryMap = hmSalaryHeadMap.keySet();
		//				Iterator itSalaryMap = setSalaryMap.iterator();
		//				while(itSalaryMap.hasNext()) {
		//					String strSalaryHead = (String)itSalaryMap.next();
		//					String strSalaryHeadName = (String)hmSalaryHeadMap.get(strSalaryHead);
		//					
		//					if(uF.parseToInt(strSalaryHead) == PROFESSIONAL_TAX ||  uF.parseToInt(strSalaryHead) == CONVEYANCE_ALLOWANCE ||  uF.parseToInt(strSalaryHead) == EDUCATION_ALLOWANCE ||  uF.parseToInt(strSalaryHead) == MEDICAL_ALLOWANCE) {
		//						continue;
		//					}
		//					
		//					double dblOtherExemptionsPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(strSalaryHead));
		//					double dblOtherExemptionsExemptLimit = uF.parseToDouble((String)hmExemption.get(strSalaryHead));
		//					double dblOtherExemptionsTaxExempt = Math.min(dblOtherExemptionsPaid, dblOtherExemptionsExemptLimit);
		//					dblOtherExemptions += dblOtherExemptionsTaxExempt; 
		//				}
	//					dblOtherExemptions = uF.parseToDouble(hmLeaveEncashmet.get(strEmpId));
	//					if(uF.parseToInt(strEmpId) == 589 && strFinancialYearStart.trim().equals("01/04/2016") && strFinancialYearEnd.trim().equals("31/03/2017")) {
	//						dblOtherExemptions = dblGratuity; 
	//					}
						
						double dblInvestment = uF.parseToDouble((String)hmInvestment.get(strEmpId));
		//				double dblInvestment = uF.parseToDouble((String)hmEmpExemptionsMap.get(strEmpId));
						double dblEPFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EMPLOYEE_EPF+""));
						
	//					double dblEPRFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EMPLOYER_EPF+""));
						double dblEPRFPaid = uF.parseToDouble(hmEmployerPF.get(strEmpId));
	//					System.out.println("dblEPRFPaid before ===>> " + dblEPRFPaid);
						if(hmEmpPayrollDetails.containsKey(EMPLOYER_EPF+"")) {
							dblEPRFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EMPLOYER_EPF+""));
						}
	//					System.out.println("dblEPRFPaid after ===>> " + dblEPRFPaid);
						double dblEPVFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(VOLUNTARY_EPF+""));
						
						double dblChapterVIA1 = uF.parseToDouble(hmEmpExemptionsCH1Map.get(strEmpId));				
						double dblChapterVIA2 = uF.parseToDouble(hmEmpExemptionsCH2Map.get(strEmpId));
						/**
						 * Change by RAHUL PATIL on 31Aug18 based on Crave Infotech Case found
						 * */
	//					double dbl80C=uF.parseToDouble(hmEmp80C.get(strEmpId));
	//					double dbl80D=uF.parseToDouble(hmEmp80D.get(strEmpId));
		//				double dblTotalInvestment =dblChapterVIA1+dblChapterVIA2;
		//				double dblTotalInvestment =dblChapterVIA1+dblChapterVIA2 + dblEPFPaid;
						/**
						 * Change by RAHUL PATIL on 31Aug18 based on Crave Infotech Case found
						 * */
	//					double dblTotalInvestment =dbl80C + dblEPFPaid + dblEPRFPaid + dblEPVFPaid;
						double dblEmpPF = dblEPFPaid + dblEPRFPaid + dblEPVFPaid;
						
		//				double dblTotalInvestment =0.0d; //dblInvestment + dblEPFPaid;  
						/**
						 * Change by RAHUL PATIL on 31Aug18 based on Crave Infotech Case found
						 * */
	//					double dblInvestmentExempt = Math.min(dblTotalInvestment, dblInvestmentExemption);
						  
						 
						if(strEmpId.equals("618")) {
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
		//				double dblConveyanceAllowanceLimit = strEmpId.equals("924") ? 15071 : (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) * uF.parseToInt(hmMonthPaid.get(strEmpId));
						double dblConveyanceAllowancePaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(CONVEYANCE_ALLOWANCE+""));
						double dblConveyanceAllowanceExempt = Math.min(dblConveyanceAllowancePaid, dblConveyanceAllowanceLimit);
						hmUS10_16_SalHeadData.put(CONVEYANCE_ALLOWANCE+"_PAID", ""+dblConveyanceAllowancePaid);
						hmUS10_16_SalHeadData.put(CONVEYANCE_ALLOWANCE+"_EXEMPT", (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+"")) >0) ? ""+dblConveyanceAllowanceExempt : ""+dblConveyanceAllowancePaid);
						
						
						int nEmpChildCnt = uF.parseToInt(hmEmpChildCnt.get(strEmpId)) > 2 ? 2 : uF.parseToInt(hmEmpChildCnt.get(strEmpId));
						double dblEducationAllowanceLimit = ((uF.parseToDouble((String)hmExemption.get(EDUCATION_ALLOWANCE+""))/12) * nEmpChildCnt) * uF.parseToInt(hmMonthPaid.get(strEmpId));
	//					double dblEducationAllowancePaid = uF.parseToInt(hmEmpChildCnt.get(strEmpId)) > 2 ? uF.parseToDouble((String)hmEmpPayrollDetails.get(EDUCATION_ALLOWANCE+"")) : 0.0d;
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
//				        System.out.println("hmUS10Inner ===============>> " + hmUS10Inner);
				        
				        Map<String, String> hmUS10InnerPaid= (Map<String, String>)hmUnderSection10_16PaidMap.get(strEmpId);
						if(hmUS10InnerPaid==null) hmUS10InnerPaid=new HashMap<String, String>();
						
						
						if(hmEmpPayrollDetails.containsKey(REIMBURSEMENT+"")) {
//							System.out.println("REIMBURSEMENT ===>> " + hmUS10InnerPaid.get(REIMBURSEMENT+""));
							hmUS10_16_SalHeadData.put(REIMBURSEMENT+"_PAID", hmUS10InnerPaid.get(REIMBURSEMENT+""));
							hmUS10_16_SalHeadData.put(REIMBURSEMENT+"_EXEMPT", hmUS10InnerPaid.get(REIMBURSEMENT+""));
						}
						
//						System.out.println("hmExemptionDataUnderSection ===>> " + hmExemptionDataUnderSection);
						Iterator<String> itUnderSection = hmExemptionDataUnderSection.keySet().iterator();
				        while (itUnderSection.hasNext()) {
				        	String underSectionId = itUnderSection.next();
					        Map<String, List<String>> hmUS10ExemptionData = hmExemptionDataUnderSection.get(underSectionId);
					        Iterator<String> itUS10Examption = hmUS10ExemptionData.keySet().iterator();
					        while (itUS10Examption.hasNext()) {
								String exemptionId = itUS10Examption.next();
								List<String> innerList = hmUS10ExemptionData.get(exemptionId);
//								System.out.println("exemptionId ===>> "+ exemptionId +" -- innerList ===>> " + innerList);
								if(uF.parseToInt(innerList.get(8)) == uF.parseToInt(slabType) || uF.parseToInt(innerList.get(8)) == 2) {
									double dblAmtExempt = uF.parseToDouble(hmUS10Inner.get(innerList.get(6)));
//									System.out.println("dblAmtExempt ===>> " + dblAmtExempt);
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
				        
//				        System.out.println("dblUS10_16Exempt ===>> " + dblUS10_16Exempt);
				        double dblVIA2Exempt = 0.0d;
						double dblVIA1Exempt = 0.0d;
						double dblAddExemptInAdjustedGrossIncome = 0.0d;
						
						dblInvestmentLimit = 0;
	//					double dblInvest = 0.0d;
						
	//					Here we Calculate VI A1
						/**
						 * Change by RAHUL PATIL on 31Aug18 based on Crave Infotech Case found
						 * */
//						System.out.println(strEmpId + " -- hmEmpInvestment(strEmpId) ================>> " + hmEmpInvestment.get(strEmpId));
						/*if(uF.parseToInt(strEmpId)==312){
							System.out.println("form16/7085--hmSectionLimitA ===>" + hmSectionLimitA);
							System.out.println("form16/7086--hmEmpInvestment ===>" + hmEmpInvestment.get(strEmpId));
							System.out.println("form16/7087--slabType="+slabType);
						}*/
						Map<String,String> hmInvest = hmEmpInvestment.get(strEmpId);
						if(hmInvest == null) hmInvest = new HashMap<String, String>();
						Iterator<String> it1 = hmInvest.keySet().iterator();
						List<String> alSectionId = new ArrayList<String>();
						while(it1.hasNext()) {
							String strSectionId = it1.next();
							
							if(hmSectionLimitA.containsKey(strSectionId+"_"+slabType)) {
								dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType));
								double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
//								System.out.println("form16/7091---slabType="+slabType);
								if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId+"_"+slabType))) {
									alSectionId.add(strSectionId);
									dblVIA1Invest += dblEmpPF;
								}
//								System.out.println("strSectionId ===>> " + strSectionId + " -- dblInvestmentLimit ===>> " + dblInvestmentLimit);
								if(dblInvestmentLimit>=0) {
									dblVIA1Invest = Math.min(dblVIA1Invest, dblInvestmentLimit);
								}
								if(uF.parseToBoolean(hmSectionAdjustedGrossIncomeLimitStatus.get(strSectionId+"_"+slabType))) {
									dblAddExemptInAdjustedGrossIncome += dblVIA1Invest;
								}
								dblVIA1Exempt += dblVIA1Invest;
								/*if(uF.parseToInt(strEmpId)==312){
									System.out.println("Form16/7111--if--dblVIA1Exempt="+dblVIA1Exempt);
								}*/
							} else {
								dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType));
								double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
//								System.out.println("form16/7107---slabType="+slabType);
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
								/*if(uF.parseToInt(strEmpId)==312){
									System.out.println("Form16/7130--else--dblVIA1Exempt="+dblVIA1Exempt);
								}*/
							}
							
					//===start parvez date: 17-05-2022===		
							/*if(hmSectionLimitA.containsKey(strSectionId+"_"+uF.parseToInt(slabType))) {
								dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+uF.parseToInt(slabType)));
								double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
//								System.out.println("form16/7091---slabType="+slabType);
								if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId+"_"+uF.parseToInt(slabType)))) {
									alSectionId.add(strSectionId);
									dblVIA1Invest += dblEmpPF;
								}
//								System.out.println("strSectionId ===>> " + strSectionId + " -- dblInvestmentLimit ===>> " + dblInvestmentLimit);
								if(dblInvestmentLimit>=0) {
									dblVIA1Invest = Math.min(dblVIA1Invest, dblInvestmentLimit);
								}
								if(uF.parseToBoolean(hmSectionAdjustedGrossIncomeLimitStatus.get(strSectionId+"_"+uF.parseToInt(slabType)))) {
									dblAddExemptInAdjustedGrossIncome += dblVIA1Invest;
								}
								dblVIA1Exempt += dblVIA1Invest;
								if(uF.parseToInt(strEmpId)==312){
									System.out.println("Form16/7111--if--dblVIA1Exempt="+dblVIA1Exempt);
								}
							} else {
								dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+uF.parseToInt(slabType)));
								double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
//								System.out.println("form16/7107---slabType="+slabType);
								if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId+"_"+uF.parseToInt(slabType)))) {
									alSectionId.add(strSectionId);
									dblVIA1Invest += dblEmpPF;
								}
								dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
								if(dblInvestmentLimit>=0) {
									dblVIA1Invest = Math.min(dblVIA1Invest, dblInvestmentLimit);
								}
								if(uF.parseToBoolean(hmSectionAdjustedGrossIncomeLimitStatus.get(strSectionId+"_"+uF.parseToInt(slabType)))) {
									dblAddExemptInAdjustedGrossIncome += dblVIA1Invest;
								}
								dblVIA1Exempt += dblVIA1Invest;
								if(uF.parseToInt(strEmpId)==312){
									System.out.println("Form16/7130--else--dblVIA1Exempt="+dblVIA1Exempt);
								}
							}*/
						//===end parvez date: 17-05-2022===	
						
							/*String strSectionId = it1.next();
							if(hmSectionLimitA.containsKey(strSectionId)) {
								dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
								double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
								if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId))) {
									dblVIA1Invest += dblEmpPF;
								}
								dblVIA1Exempt += Math.min(dblVIA1Invest, dblInvestmentLimit);
							} else {
								dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
								double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
								if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId))) {
									dblVIA1Invest += dblEmpPF;
								}
								dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
								dblVIA1Exempt += Math.min(dblVIA1Invest, dblInvestmentLimit);
							}*/
						}
						
						
						/*if(alSectionId == null || alSectionId.size()==0) {
							dblVIA1Exempt += dblEmpPF;
						}*/
						
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
//						System.out.println("dblAdjustedGrossTotalIncome ===>> " + dblAdjustedGrossTotalIncome);
						request.setAttribute("dblAdjustedGrossTotalIncome", ""+dblAdjustedGrossTotalIncome);
						
						pst = con.prepareStatement("select * from section_details where financial_year_start=? and financial_year_end=? " +
							" and (slab_type=? or slab_type=2) and section_code not in ('HRA') and isdisplay=true and is_adjusted_gross_income_limit=true "); //and sd.section_id !=11
						pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setInt(3, uF.parseToInt(slabType));
//						System.out.println(" pst==>"+pst);
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
//						System.out.println(" hmSectionwiseSubSecIsAdjustedStatus ===>> " + hmSectionwiseSubSecIsAdjustedStatus);
						
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
//									System.out.println(dblSubSecLimit+ " -- dbl10PerOfAdjustedIncome ===>> " + dbl10PerOfAdjustedIncome);
									if(hm.get("SUB_SECTION_LIMIT_TYPE") != null && hm.get("SUB_SECTION_LIMIT_TYPE").equals("%")) {
										dblSubSecLimit = (dbl10PerOfAdjustedIncome * dblSubSecLimit) / 100;
									}
//									System.out.println("dblSubSecLimit ===>> " +dblSubSecLimit +" -- dblInvestment ===>> " + dblInvestment);
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
//						System.out.println("hmSectionwiseSubSecAdjusted10PerLimitAmt ===>> " +hmSectionwiseSubSecAdjusted10PerLimitAmt);
						
						request.setAttribute("hmSectionwiseSubSecAdjusted10PerLimitAmt", hmSectionwiseSubSecAdjusted10PerLimitAmt);
//						System.out.println("hmInvest11 ===>> " + hmInvest11);
						
						double dblVIA1ExemptIsAdjustedLimit = 0.0d;
						
						Iterator<String> it11 = hmInvest11.keySet().iterator();
						while(it11.hasNext()) {
							String strSectionId = it11.next();
							
							if(hmSectionLimitA.containsKey(strSectionId+"_"+slabType)) {
								dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType));
								double dblVIA1Invest = uF.parseToDouble(hmInvest11.get(strSectionId));
//								System.out.println("form16/7280---slabType="+slabType);
								if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId+"_"+slabType))) {
									alSectionId.add(strSectionId);
									dblVIA1Invest += dblEmpPF;
								}
//								System.out.println("strSectionId ===>> " + strSectionId + " -- dblInvestmentLimit ===>> " + dblInvestmentLimit);
								if(dblInvestmentLimit>=0) {
									dblVIA1Invest = Math.min(dblVIA1Invest, dblInvestmentLimit);
								}
								dblVIA1ExemptIsAdjustedLimit += dblVIA1Invest;
							} else {
								dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType));
								double dblVIA1Invest = uF.parseToDouble(hmInvest11.get(strSectionId));
//								System.out.println("form16/7293---slabType="+slabType);
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
							/*if(uF.parseToInt(strEmpId)==312){
								System.out.println("Form16/7317--dblVIA1Exempt="+dblVIA1Exempt);
							}*/
						}
						
//						System.out.println("dblVIA1ExemptIsAdjustedLimit ===>> " + dblVIA1ExemptIsAdjustedLimit + " --- dblUS10_16Exempt ===>> " + dblUS10_16Exempt);
					//	double dblNetTaxableIncome = (dblPrevOrgGross + dblGross1 + dblAddExemptInAdjustedGrossIncome + dblIncomeFromOther)- dblUS10_16Exempt - dblVIA1Exempt - dblVIA2Exempt - dblVIA1ExemptIsAdjustedLimit; //- dblHomeLoanTaxExempt
//						System.out.println(" dblVIA1Exempt ===>> " + dblVIA1Exempt+" --- dblVIA2Exempt ===>> " + dblVIA2Exempt +" --- dblGross1 ===>> " + dblGross1 +" --- dblPrevOrgGross ===>> " + dblPrevOrgGross);
//						System.out.println(" dblAddExemptInAdjustedGrossIncome ===>> " + dblAddExemptInAdjustedGrossIncome+" --- dblIncomeFromOther ===>> " + dblIncomeFromOther);
//						String strSlabType = CF.getEmpIncomeTaxSlabType(con, CF, strEmpId, strFinancialYearStart, strFinancialYearEnd);
//						int slabType = uF.parseToInt(strSlabType);
						
						double dblNetTaxableIncome = (dblPrevOrgGross + dblGross1 + dblAddExemptInAdjustedGrossIncome + dblIncomeFromOther)- dblUS10_16Exempt - dblVIA1Exempt - dblVIA2Exempt - dblVIA1ExemptIsAdjustedLimit; //- dblHomeLoanTaxExempt
//						System.out.println("dblNetTaxableIncome:"+dblNetTaxableIncome);//20883.0
						/*if(uF.parseToInt(strEmpId)==312){
							System.out.println("form16/7328--dblPrevOrgGross="+dblPrevOrgGross+"---dblGross1="+dblGross1+"--dblAddExemptInAdjustedGrossIncome="+dblAddExemptInAdjustedGrossIncome
									+"---dblIncomeFromOther="+dblIncomeFromOther+"---dblUS10_16Exempt="+dblUS10_16Exempt+"---dblVIA1Exempt="+dblVIA1Exempt+"---dblVIA2Exempt="+dblVIA2Exempt
									+"--dblVIA1ExemptIsAdjustedLimit="+dblVIA1ExemptIsAdjustedLimit);
						}*/
						
//						double dblNetTaxableIncome = dblGross1 - dblUS10_16Exempt - dblVIA1Exempt - dblVIA2Exempt; // dbl80D -  dblHomeLoanTaxExempt -
					
		//				double dblNetTaxableIncome = dblGross1 - dblProfessionalTaxExempt - dblHRAExemption - dblConveyanceAllowanceExempt - dblInvestment;
		//				double dblNetTaxableIncome = dblGross1 - dblProfessionalTaxExempt - dblHRAExemption - dblConveyanceAllowanceExempt - dblInvestmentExempt;
						
						/*double dblNetTaxableIncome = dblGross1 - dblProfessionalTaxExempt - dblHRAExemption - dblConveyanceAllowanceExempt - dblVIA1Exempt - 
						dblHomeLoanTaxExempt - dblEducationAllowanceExempt - dblMedicalAllowanceExempt -dblLTAExempt - dblOtherExemptions - dblVIA2Exempt; //dbl80D -
						 
	*/	//				double dblNetTaxableIncome = dblGross1 - dblProfessionalTaxExempt - dblHRAExemption - dblConveyanceAllowanceExempt - dblInvestmentExempt - dblOtherExemptions - dblHomeLoanTaxExempt - dblIncomeFromOther;
						
						if(strEmpId.equalsIgnoreCase("464")) {
	//						System.out.println(strEmpId+" dblEducationAllowancePaid =====> " +dblEducationAllowancePaid);
	//						System.out.println(strEmpId+" dblEducationAllowanceExempt =====> " +dblEducationAllowanceExempt);
						}
						
						hmTaxInner.put("dblIncomeFromOther", dblIncomeFromOther+"");  
						hmTaxInner.put("dblActualHRAPaid", dblActualHRAPaid+"");  
						hmTaxInner.put("dblActualRentPaid", dblActualRentPaid+"");
						hmTaxInner.put("dblHRA1", dblHRA+"");
						hmTaxInner.put("dblCondition1", dblCondition1+"");
						hmTaxInner.put("dblHRAExemption", dblHRAExemption+"");
				//===start parvez date: 31-03-2022===		
						hmTaxInner.put("dblPrevOrgGross", dblPrevOrgGross+"");
						hmTaxInner.put("dblPrevOrgTDSAmount", dblPrevOrgTDSAmount);
				//===end parvez date: 31-03-2022===
						
	//					hmTaxInner.put("dblProfessionalTaxPaid", dblProfessionalTaxPaid+"");
	//					hmTaxInner.put("dblProfessionalTaxExempt", dblProfessionalTaxExempt+"");
						
						hmTaxInner.put("dblConveyanceAllowancePaid", dblConveyanceAllowancePaid+"");
						hmTaxInner.put("dblConveyanceAllowanceExempt", dblConveyanceAllowanceExempt+"");
						
	//					hmTaxInner.put("dblOtherExemptions", dblOtherExemptions+"");
//						hmTaxInner.put("dblHomeLoanTaxExempt", dblHomeLoanTaxExempt+"");
						
	//					hmTaxInner.put("dblInvestment", dblInvestmentExempt+"");
		//				hmTaxInner.put("dblInvestment", hmEmpExemptionsMap.get(strEmpId) +"");
						hmTaxInner.put("hmEmpExemptionsCH1Map", hmEmpExemptionsCH1Map.get(strEmpId) +"");
						hmTaxInner.put("hmEmpExemptionsCH2Map", hmEmpExemptionsCH2Map.get(strEmpId) +"");
						hmTaxInner.put(TDS, strTDSAmt);
						
						hmTaxInner.put("dblEducationAllowancePaid", dblEducationAllowancePaid+"");
						hmTaxInner.put("dblEducationAllowanceExempt", dblEducationAllowanceExempt+""); 
						
	//					hmTaxInner.put("dblMedicalAllowanceExempt", dblMedicalAllowanceExempt+""); 
	//					hmTaxInner.put("dblMedicalAllowancePaid", dblMedicalAllowancePaid+"");
						hmTaxInner.put("dblEmpPF", dblEmpPF+""); 
						
	//					hmTaxInner.put("dblLTAExempt", dblLTAExempt+"");
	//					hmTaxInner.put("dblLTAPaid", dblLTAPaid+"");
						hmTaxInner.put("hmUS10_16_SalHeadData", hmUS10_16_SalHeadData);
						
						double TDSPayable = 0;
						if(strEmpId!=null && strEmpId.equalsIgnoreCase("618")) {					
							
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
						
						TDSPayable = calculateTDS(con, dblNetTaxableIncome, dblCess1, dblCess2, strFinancialYearStart, strFinancialYearEnd, strEmpId, (String)hmEmpGenderMap.get(strEmpId), (String)hmEmpAgeMap.get(strEmpId), slabType);
				//===start parvez date: 01-04-2022===
						
						hmTaxInner.put("TAX_LIABILITY", TDSPayable+"");
				//===end parvez date: 01-04-2022===		
						TDSPayable = TDSPayable - dblPrevOrgTDSAmount;
						
		//				if(strEmpId.equalsIgnoreCase("924")) {
		//					System.out.println(strEmpId+" dblNetTaxableIncome=====>"+dblNetTaxableIncome);  
		//					System.out.println(strEmpId+" TDSPayable=====>"+TDSPayable);  
		//				} 
						
		//				double dblRebate = 0;
		//				if(dblNetTaxableIncome<=500000 && TDSPayable<=500000) {
		//					if(TDSPayable>=2000) {
		//						dblRebate = 2000;
		//					} else if(TDSPayable>0 && TDSPayable<2000) {
		//						dblRebate = TDSPayable;
		//					}
		//				}
						
						double dblMaxTaxableIncome = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_MAX_TAX_INCOME"));
						double dblRebateAmt = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_REBATE_AMOUNT"));
						
						double dblRebate = 0;
						if(dblNetTaxableIncome <= dblMaxTaxableIncome && TDSPayable <= dblMaxTaxableIncome) {
							if(TDSPayable>=dblRebateAmt) {
								dblRebate = dblRebateAmt;
							} else if(TDSPayable > 0 && TDSPayable < dblRebateAmt) {
								dblRebate = TDSPayable;
							}
						}
						
						double dblCess1Amount = (dblCess1 * (TDSPayable-dblRebate) / 100);
						double dblCess2Amount = (dblCess2 * (TDSPayable-dblRebate) / 100);
						
						
				//===start parvez date: 01-04-2022===	
						/*if(uF.parseToInt(strEmpId)==312){
							System.out.println("TDSPayable="+TDSPayable+"---dblNetTaxableIncome="+dblNetTaxableIncome+"---slabType="+slabType
									+"----dblCess1="+dblCess1+"----dblCess2="+dblCess2);
						}*/
//						hmTaxInner.put("TAX_LIABILITY", TDSPayable+"");
				//===end parvez date: 01-04-2022===		
						hmTaxInner.put("CESS1", dblCess1+"");
						hmTaxInner.put("CESS2", dblCess2+"");
						hmTaxInner.put("CESS1_AMOUNT", dblCess1Amount+"");
						hmTaxInner.put("CESS2_AMOUNT", dblCess2Amount+"");
						hmTaxInner.put("TOTAL_TAX_LIABILITY", ((TDSPayable-dblRebate) + dblCess1Amount+ dblCess2Amount)+""); 
						hmTaxInner.put("TAX_REBATE", dblRebate+"");
						hmTaxLiability.put(strEmpId, hmTaxInner);
					}
					
					
					pst = con.prepareStatement("select sum(amount) as amount, emp_id from payroll_generation where salary_head_id = ? " +
							"and financial_year_from_date=? and financial_year_to_date=? and is_paid = true and emp_id in ("+strEmpIds+") group by emp_id");
					pst.setInt(1, TDS);
					pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					rs = pst.executeQuery();
		//			System.out.println(" pst==>"+pst);
		//			System.out.println("9 date==>"+new Date());
					Map<String, String> hmPaidTdsMap = new HashMap<String, String>();
					while(rs.next()) {
						hmPaidTdsMap.put(rs.getString("emp_id"), rs.getString("amount"));
					}
					rs.close();
					pst.close();
		//			System.out.println("=======14========"+new Date());   
					
					request.setAttribute("hmExemption", hmExemption);
					request.setAttribute("hmHRAExemption", hmHRAExemption);
					request.setAttribute("hmRentPaid", hmRentPaid);
					request.setAttribute("hmInvestment", hmInvestment);
					request.setAttribute("hmTaxLiability", hmTaxLiability);
					request.setAttribute("hmSalaryHeadMap", hmSalaryHeadMap);
					request.setAttribute("hmPayrollDetails", hmPayrollDetails);
					request.setAttribute("hmPaidTdsMap", hmPaidTdsMap);
					
					request.setAttribute("hmEmpInvestment", hmEmpInvestment);
					request.setAttribute("hmEmpSubInvestment", hmEmpSubInvestment);  
					request.setAttribute("hmSectionMap", hmSectionMap);
					request.setAttribute("hmEmpInvestment1", hmEmpInvestment1);
					request.setAttribute("hmUnderSection10_16Map", hmUnderSection10_16Map);
					request.setAttribute("hmUnderSection10_16PaidMap", hmUnderSection10_16PaidMap);
					request.setAttribute("hmExemptionDataUnderSection", hmExemptionDataUnderSection);
					
					request.setAttribute("chapter1SectionList", chapter1SectionList);
					request.setAttribute("chapter2SectionList", chapter2SectionList);
					request.setAttribute("hmSectionPFApplicable", hmSectionPFApplicable);
					
					request.setAttribute("hmEmpActualInvestment", hmEmpActualInvestment);
					request.setAttribute("hmEmpActualInvestment1", hmEmpActualInvestment1);
					request.setAttribute("hmSectionLimitA", hmSectionLimitA);
					request.setAttribute("hmSectionLimitP", hmSectionLimitP);
				}
	
				request.setAttribute("hmEmployeeMap", hmEmployeeMap);
				request.setAttribute("hmEmpJoiningDate", hmEmpJoiningDate);
				request.setAttribute("hmEmpCode", hmEmpCode);
				request.setAttribute("hmEmpDepartment", hmEmpDepartment);
				request.setAttribute("hmEmpPanNo", hmEmpPanNo);
				request.setAttribute("hmEmpEndDate", hmEmpEndDate);
				request.setAttribute("hmEmpAddressMap", hmEmpAddressMap);
	
				request.setAttribute("hmEmpForm16Status", hmEmpForm16Status);
				
			}
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpSlabType", hmEmpSlabType);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	public double calculateTDS(Connection con, double dblTotalTaxableSalary, double dblCess1, double dblCess2, String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, String strGender, String strAge,String slabType) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		double dblTotalTDSPayable = 0.0d;
		
		try {
			int countBug = 0;
			double dblUpperDeductionSlabLimit = 0;
			double dblLowerDeductionSlabLimit = 0;
			double dblTotalNetTaxableSalary = 0; 
			do {
				pst = con.prepareStatement("select * from deduction_tax_details where age_from <= ? and age_to > ? and gender = ? and financial_year_from = ? and financial_year_to = ?  and _from <= ? and _to>? and slab_type = ? order by _from limit 1");
				pst.setDouble(1, uF.parseToDouble(strAge));
				pst.setDouble(2, uF.parseToDouble(strAge));
				pst.setString(3, strGender);
				pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDouble(6, dblTotalTaxableSalary);
				pst.setDouble(7, dblUpperDeductionSlabLimit);
				pst.setInt(8,uF.parseToInt(slabType));
				rs = pst.executeQuery();
//				System.out.println("pst=====>"+pst);
//				System.out.println("11 date==>"+new Date());  
				double dblDeductionAmount = 0;
				String strDeductionType = null;
				if(rs.next()) {
					dblDeductionAmount = rs.getDouble("deduction_amount");
					strDeductionType = rs.getString("deduction_type");
					dblUpperDeductionSlabLimit = rs.getDouble("_to");
					dblLowerDeductionSlabLimit = rs.getDouble("_from");
				}
				rs.close();
				pst.close();
				
				if(countBug==0) {
					dblTotalNetTaxableSalary = dblTotalTaxableSalary;
				}
				
				if(dblTotalTaxableSalary>=dblUpperDeductionSlabLimit) {
					dblTotalTDSPayable += ((dblDeductionAmount /100) *  (dblUpperDeductionSlabLimit - dblLowerDeductionSlabLimit) );
					
//					System.out.println("=====IF=========");
//					System.out.println("dblTotalTDSPayable=====>"+dblTotalTDSPayable);
//					System.out.println("dblDeductionAmount=====>"+dblDeductionAmount);
//					System.out.println("dblTotalNetTaxableSalary=====>"+dblTotalNetTaxableSalary);
					
				} else {
					dblTotalTDSPayable += ((dblDeductionAmount /100) *  dblTotalNetTaxableSalary );
				
//					System.out.println("=====ELSE=========");
//					System.out.println("dblTotalTDSPayable=====>"+dblTotalTDSPayable);
//					System.out.println("dblDeductionAmount=====>"+dblDeductionAmount);
//					System.out.println("dblTotalNetTaxableSalary=====>"+dblTotalNetTaxableSalary);
					  
				}
				dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblUpperDeductionSlabLimit;

				if(countBug==15)break;		// in case of any bug, this condition is used to avoid any stoppage 
				countBug++;
				
			} while(dblTotalNetTaxableSalary>0);
			

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
		} finally {
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
		return dblTotalTDSPayable;

	}
	
	private void getSelectedFilter(UtilityFunctions uF) {


		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++) {
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
		
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);

	}
	
	
	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	public HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
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

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
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

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
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

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
	}

	public String getProPage() {
		return proPage;
	}

	public void setProPage(String proPage) {
		this.proPage = proPage;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}

	public String getStrSearch() {
		return strSearch;
	}

	public void setStrSearch(String strSearch) {
		this.strSearch = strSearch;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
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