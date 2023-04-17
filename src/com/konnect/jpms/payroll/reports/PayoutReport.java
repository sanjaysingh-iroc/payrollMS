package com.konnect.jpms.payroll.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillPayMode;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PayoutReport extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(PayoutReport.class);
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	String strEmployeType;
	
	String paycycle;
	String f_paymentMode; 
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	String[] f_employeType;
	
	List<FillPayCycles> paycycleList;
	List<FillPayMode> paymentModeList;
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillEmploymentType> employementTypeList;
	
	String exportType;
	

	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute(TITLE, "Payout Report");
		request.setAttribute(PAGE, "/jsp/payroll/reports/PayoutReport.jsp");
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
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
		if(getStrEmployeType() != null && !getStrEmployeType().equals("")) {
			setF_employeType(getStrEmployeType().split(","));
		} else {
			setF_employeType(null);
		}
		
		if(getF_paymentMode()==null){
			setF_paymentMode("1");
		}
	
		viewPayoutReport(uF);
	
		return loadPayoutReport(uF);
	}

	private String loadPayoutReport(UtilityFunctions uF) {
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		paymentModeList = new FillPayMode().fillPaymentMode();
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		employementTypeList = new FillEmploymentType().fillEmploymentType(request);
		
		getSelectedFilter(uF);
		
		return LOAD;
	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("PAYMENTMODE");
		if(getF_paymentMode()!=null)  {
			String strPayMode="";
			int k=0;
			for(int i=0;paymentModeList!=null && i<paymentModeList.size();i++){
				if(getF_paymentMode().equals(paymentModeList.get(i).getPayModeId())) {
					if(k==0) {
						strPayMode=paymentModeList.get(i).getPayModeName();
					} else {
						strPayMode+=", "+paymentModeList.get(i).getPayModeName();
					}
					k++;
				}
			}
			if(strPayMode!=null && !strPayMode.equals("")) {
				hmFilter.put("PAYMENTMODE", strPayMode);
			} else {
				hmFilter.put("PAYMENTMODE", "");
			}
			
		} else {
			hmFilter.put("PAYMENTMODE", "");
		}
		
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
		
		alFilter.add("EMPTYPE");
		if (getF_employeType() != null) {
			String stremptype = "";
			int k = 0;
			for (int i = 0; employementTypeList != null && i < employementTypeList.size(); i++) {
				for (int j = 0; j < getF_employeType().length; j++) {
					if (getF_employeType()[j].equals(employementTypeList.get(i).getEmpTypeId())) {
						if (k == 0) {
							stremptype = employementTypeList.get(i).getEmpTypeName();
						} else {
							stremptype += ", " + employementTypeList.get(i).getEmpTypeName();
						}
						k++;
					}
				}
			}
			if (stremptype != null && !stremptype.equals("")) {
				hmFilter.put("EMPTYPE", stremptype);
			} else {
				hmFilter.put("EMPTYPE", "All Employee Type");
			}
		} else {
			hmFilter.put("EMPTYPE", "All Employee Type");
		}
		
		
		alFilter.add("PAYCYCLE");
		String strPaycycle = "";
		String[] strPayCycleDates = null;
		if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			
			strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
		}
		hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}


	private void viewPayoutReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		
		try {
			
			
			String[] strPayCycleDates = null;

			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF,getF_org(), request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			}
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmBankNameMap = CF.getBankNameMap(con,uF);
			Map<String, Map<String, String>> hmBankMap = CF.getBankMap(con,uF);
			
			Map<String, String> hmOrg = CF.getOrgName(con);
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			Map<String, String> hmDept =CF.getDeptMap(con);
			if(hmDept == null) hmDept = new HashMap<String, String>();
			
			Map<String,Map<String,String>> hmPayPayroll = viewApporvedPayroll(con,uF,strPayCycleDates);
			String strEmpIds = (String) request.getAttribute("strEmpIds");
//			System.out.println("main strempids======>"+strEmpIds);
			
			if(strEmpIds !=null && !strEmpIds.equals("") && strEmpIds.length() > 0){
				
				Map<String, Map<String, String>> hmEmpHistory = (Map<String, Map<String, String>>) request.getAttribute("hmEmpHistory");
				if(hmEmpHistory == null) hmEmpHistory = new HashMap<String, Map<String,String>>(); 
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from employee_personal_details epd ,employee_official_details eod, department_info di where epd.emp_per_id=eod.emp_id  and eod.depart_id=di.dept_id");
				sbQuery.append(" and eod.emp_id in ("+strEmpIds+") ");
				if (getF_employeType() != null && getF_employeType().length > 0) {
					sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
				}
//				if(uF.parseToInt(getF_paymentMode())>0){
//					sbQuery.append(" and eod.payment_mode = "+uF.parseToInt(getF_paymentMode()));
//				}
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				List<List<String>> empOuterList=new ArrayList<List<String>>();
				while(rs.next()){
					
					Map<String,String> hmEmpPayroll=hmPayPayroll.get(rs.getString("emp_id"));
					if(hmEmpPayroll==null)
						continue;
					
					Map<String, String> hm = hmEmpHistory.get(rs.getString("emp_id"));
					
					List<String> empList=new ArrayList<String>();
					empList.add(rs.getString("emp_id")); //0
					empList.add(rs.getString("empcode")); //1
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
				
					
					empList.add(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")); //2
					empList.add(rs.getString("emp_pan_no")); //3
					
					String strDepartment = uF.showData(rs.getString("dept_name"),"");
					if(hm != null && uF.parseToInt(hm.get("EMP_DEPART")) > 0){
						strDepartment = uF.showData(hmDept.get(hm.get("EMP_DEPART")), "");
					}
					empList.add(strDepartment); // 4
					
					if(uF.parseToInt(getF_paymentMode())==1) {
						empList.add(uF.showData(hmBankNameMap.get(rs.getString("emp_bank_name")),"")); //5
						Map<String, String> hmInner = hmBankMap.get(rs.getString("emp_bank_name"));
						empList.add(uF.showData((hmInner != null && hmInner.get("IFSC_CODE") != null) ? hmInner.get("IFSC_CODE") : "", "-")); //6
//						empList.add("");
					}else if(uF.parseToInt(getF_paymentMode())==3){
						empList.add("Cheque Payment"); //5
						empList.add(""); //6
					}else{
						empList.add("Cash"); //5
						empList.add(""); //6
					}
					
					empList.add(rs.getString("emp_bank_acct_nbr")); //7
					empList.add(uF.showData(hmEmpPayroll.get("NET"), "0")); //8
					
					String strOrg = uF.showData(hmOrg.get(rs.getString("org_id")), "");
					if(hm != null && uF.parseToInt(hm.get("EMP_ORG")) > 0){
						strOrg = uF.showData(hmOrg.get(hm.get("EMP_ORG")), "");
					}
					empList.add(strOrg); //9
					
					empOuterList.add(empList);
					
				}
				rs.close();
				pst.close();
				
				
				List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
				List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("Payout Report from "+strPayCycleDates[0]+" - "+strPayCycleDates[1],Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 
				alInnerExport.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			   	alInnerExport.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			   	alInnerExport.add(new DataStyle("Pan No",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			   	alInnerExport.add(new DataStyle("Organization",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			   	alInnerExport.add(new DataStyle("Department",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			   	alInnerExport.add(new DataStyle("Bank & Branch",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			   	alInnerExport.add(new DataStyle("Bank IFSC Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			   	alInnerExport.add(new DataStyle("Saving Account No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			   	alInnerExport.add(new DataStyle("Net Salary",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			   
			   	reportListExport.add(alInnerExport); 
			   	
			   	double total=0;
			   	List<List<String>> reportList = new ArrayList<List<String>>();
			   	int i=0;
				for(;empOuterList!=null && i<empOuterList.size();i++){ 
					List<String> empList=empOuterList.get(i);
					
					alInnerExport=new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(""+(i+1),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(empList.get(1),""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(empList.get(2),""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(empList.get(3),""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(empList.get(9),""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));				
					alInnerExport.add(new DataStyle(uF.showData(empList.get(4),""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(empList.get(5),""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(empList.get(6),""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(empList.get(7),""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(empList.get(8),""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					
					reportListExport.add(alInnerExport);
					
					total+=uF.parseToDouble(empList.get(7));
					
					List<String> alInner = new ArrayList<String>();
					alInner.add(""+(i+1));
					alInner.add(uF.showData(empList.get(1),""));
					alInner.add(uF.showData(empList.get(2),""));
					alInner.add(uF.showData(empList.get(3),""));
					alInner.add(uF.showData(empList.get(9),""));
					alInner.add(uF.showData(empList.get(4),""));
					alInner.add(uF.showData(empList.get(5),""));
					alInner.add(uF.showData(empList.get(6),""));
					alInner.add(uF.showData(empList.get(7),""));
					alInner.add(uF.showData(empList.get(8),""));
					
					reportList.add(alInner);
				}
				
				if(i > 0){
					List<String> alInner = new ArrayList<String>();
					alInner.add("");
					alInner.add("");
					alInner.add("");
					alInner.add("");
					alInner.add("");
					alInner.add("");
					alInner.add("");
					alInner.add("");
					alInner.add("Total");
					alInner.add(""+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),total)+"");
					
					reportList.add(alInner);
					
					alInnerExport=new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("Total",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle( uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),total),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					
					reportListExport.add(alInnerExport);
				}
				
				request.setAttribute("empOuterList", empOuterList);
				request.setAttribute("reportList", reportList);
				request.setAttribute("reportListExport", reportListExport);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public Map<String,Map<String,String>> viewApporvedPayroll(Connection con,UtilityFunctions uF,String[] strPayCycleDates){
		
		PreparedStatement pst=null;
		ResultSet rs = null;
		Map<String,Map<String,String>> hmPayPayroll = new LinkedHashMap<String,Map<String,String>> ();

		try {
			
			String strEmpIds = getEmpPayrollHistory(con,uF,strPayCycleDates);
			if(strEmpIds !=null && !strEmpIds.equals("") && strEmpIds.length() > 0){
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from payroll_generation pg, employee_official_details eod where eod.emp_id = pg.emp_id and paid_from= ? and paid_to=? and pg.is_paid=true ");
				sbQuery.append(" and pg.emp_id in ("+strEmpIds+") ");
				if(getF_paymentMode()==null || uF.parseToInt(getF_paymentMode())==1) {
					sbQuery.append(" and pg.payment_mode= 1");
				} else {
					sbQuery.append(" and pg.payment_mode= "+getF_paymentMode());
				}
				sbQuery.append(" order by pg.emp_id, earning_deduction desc, salary_head_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				rs = pst.executeQuery();
				Map<String,String> hmEmpPayroll = null;
				String strEmpIdOld = null;
				String strEmpIdNew = null; 
				double dblGross = 0;
				double dblNet = 0;
				while(rs.next()) {
					strEmpIdNew = rs.getString("emp_id");
					
					if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
						hmEmpPayroll = new HashMap<String,String>();
						dblNet = 0;
					}
					
					hmEmpPayroll.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
					
					
					if("E".equalsIgnoreCase(rs.getString("earning_deduction"))){
						double dblAmount = rs.getDouble("amount");
						dblGross = uF.parseToDouble((String)hmEmpPayroll.get("GROSS"));
						dblNet += dblAmount;
						hmEmpPayroll.put("GROSS",  uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(dblGross + dblAmount)));
						
					}else{
						double dblAmount = rs.getDouble("amount");
						dblNet -= dblAmount;
					}
					
					hmEmpPayroll.put("NET",uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNet));
					hmPayPayroll.put(strEmpIdNew, hmEmpPayroll); 
					
					strEmpIdOld = strEmpIdNew;
				}
				rs.close();
				pst.close();
			}
			
			request.setAttribute("strEmpIds", strEmpIds);
			
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
		return hmPayPayroll;
	}

	private String getEmpPayrollHistory(Connection con, UtilityFunctions uF,String[] strPayCycleDates) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder sbEmp = null;
		try {
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from payroll_history where paycycle_from =? and paycycle_to=? and paycycle= ? ");
			if(getF_level()!=null && getF_level().length>0){
	            sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	        }
	        if(getF_department()!=null && getF_department().length>0){
	            sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
	        if(getF_service()!=null && getF_service().length>0){
	            sbQuery.append(" and (");
	            for(int i=0; i<getF_service().length; i++){
	            	sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
	                
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
			sbQuery.append(" order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
	//		System.out.println("pst====>"+pst);     
			rs = pst.executeQuery();
			Set<String> empSetlist = new HashSet<String>();
			Map<String, Map<String, String>> hmEmpHistory = new HashMap<String, Map<String,String>>(); 
			while (rs.next()){
				empSetlist.add(rs.getString("emp_id"));
				Map<String, String> hm = new HashMap<String, String>();
				hm.put("EMP_ORG", rs.getString("org_id"));
				hm.put("EMP_WLOCATION", rs.getString("wlocation_id"));
				hm.put("EMP_DEPART", rs.getString("depart_id"));
				hm.put("EMP_GRADE", rs.getString("grade_id"));
				
				hmEmpHistory.put(rs.getString("emp_id"), hm);
				
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpHistory", hmEmpHistory);
	//		System.out.println("1 empSetlist====>"+empSetlist.toString());
			
			sbQuery = new StringBuilder();
			sbQuery.append("select distinct(pg.emp_id) as emp_id from payroll_generation pg, employee_official_details eod where eod.emp_id = pg.emp_id and pg.is_paid=true and paid_from= ? and paid_to=? and paycycle= ?");
			
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
	        if(getF_paymentMode()==null || uF.parseToInt(getF_paymentMode())==1){
				sbQuery.append(" and pg.payment_mode= 1");
			}else{
				sbQuery.append(" and pg.payment_mode= "+getF_paymentMode());
			}
	        sbQuery.append(" and pg.emp_id not in (select emp_id from payroll_history where paycycle_from =? and paycycle_to=? and paycycle= ?) ");
			sbQuery.append(" order by pg.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
			pst.setDate(4, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(strPayCycleDates[2]));
	//		System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()){
				empSetlist.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
	//		System.out.println("2 empSetlist====>"+empSetlist.toString());
			
			Iterator<String> it = empSetlist.iterator();
			while(it.hasNext()){
				String strEmp = it.next();
				if(sbEmp == null){
					sbEmp = new StringBuilder();
					sbEmp.append(strEmp);
				} else {
					sbEmp.append(","+strEmp);
				}
			}
			request.setAttribute("strTotalEmp", ""+empSetlist.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}
		return sbEmp!=null ? sbEmp.toString() : null;
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

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getF_paymentMode() {
		return f_paymentMode;
	}

	public void setF_paymentMode(String f_paymentMode) {
		this.f_paymentMode = f_paymentMode;
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

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public List<FillPayMode> getPaymentModeList() {
		return paymentModeList;
	}

	public void setPaymentModeList(List<FillPayMode> paymentModeList) {
		this.paymentModeList = paymentModeList;
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

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
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

	public String[] getF_employeType() {
		return f_employeType;
	}

	public void setF_employeType(String[] f_employeType) {
		this.f_employeType = f_employeType;
	}

	public List<FillEmploymentType> getEmployementTypeList() {
		return employementTypeList;
	}

	public void setEmployementTypeList(List<FillEmploymentType> employementTypeList) {
		this.employementTypeList = employementTypeList;
	}

	public String getStrEmployeType() {
		return strEmployeType;
	}

	public void setStrEmployeType(String strEmployeType) {
		this.strEmployeType = strEmployeType;
	}
	
}
