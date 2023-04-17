package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.ComparatorWeight;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class PayrollRegister extends ActionSupport implements ServletRequestAware, IStatements {

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
	
	String strApprove;
	String financialYear; 
	String paycycle;
	String approvePC;
	String strMonth;
	String []chbxApprove;
	List<FillMonth> alMonthList;
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	String strEmployeType;
	
	String[] f_strWLocation; 
	String[] f_department;
	String[] f_level; 
	String[] f_employeType;
	String[] f_service;
	
	List<FillPayCycles> paycycleList ;
	List<FillFinancialYears> financialYearList;
	List<FillLevel> levelList;
	List<FillWLocation> wLocationList;
	
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	
	List<FillOrganisation> organisationList;
	List<FillEmploymentType> employementTypeList;
	String f_org;
	
	private static Logger log = Logger.getLogger(PayrollRegister.class);
	
	public String execute() throws Exception {
		
		//System.out.println("in execute function=====");
			
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, PProllRegister);
		request.setAttribute(TITLE, TPayrollRegister);
		
		strEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(USERTYPE);
		
//			boolean isView  = CF.getAccess(session, request, uF);
//			if(!isView){
//				request.setAttribute(PAGE, PAccessDenied);
//				request.setAttribute(TITLE, TAccessDenied);
//				return ACCESS_DENIED;
//			}
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
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
		
		String[] strPayCycleDates = null;
		
		if(getApprovePC()!=null && !getApprovePC().equalsIgnoreCase("NULL") && getApprovePC().length()>0 && getStrApprove()!=null){
			strPayCycleDates = getApprovePC().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}else if (getPaycycle() != null) {
			strPayCycleDates = getPaycycle().split("-");
//				strPayCycleDates = CF.getPrevPayCycle(strPayCycleDates[1], CF.getStrTimeZone(), CF);
		} else {
			strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
			strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}
		strD1 = strPayCycleDates[0];
		strD2 = strPayCycleDates[1];
		strPC = strPayCycleDates[2];
		
		request.setAttribute("salaryStructure", CF.getStrSalaryStructure());
		int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
		//System.out.println("nSalaryStrucuterType==>"+nSalaryStrucuterType);
		if(nSalaryStrucuterType == S_GRADE_WISE){
			
			viewApporvedPayrollByGrade(uF);
		} else {
			viewApporvedPayroll(uF);
		}
			
		return loadPaySlips();
	}
	
	
	private void viewApporvedPayrollByGrade(UtilityFunctions uF) {
		
		//System.out.println("in viewApporvedPayrollByGrade===");
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			con = db.makeConnection(con);
			
			Map hmEmpMap = CF.getEmpNameMap(con,strUserType, strEmpId);
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map hmSalaryDetails = CF.getSalaryHeadsMap(con);
			Map<String, String> hmEmpCode =CF.getEmpCodeMap(con);
			Map<String, String> hmEmpDepartment =CF.getEmpDepartmentMap(con);			
			Map<String, String> hmDept =CF.getDeptMap(con);
			if(hmDept == null) hmDept = new HashMap<String, String>();
			Map<String, String> hmEmpJoiningDate=CF.getEmpJoiningDateMap(con, uF);
			
			
			String strEmpIds = getEmpPayrollHistory(con,uF);

			Map<String, Map<String, String>> hmEmpHistory = (Map<String, Map<String, String>>) request.getAttribute("hmEmpHistory");
			if(hmEmpHistory == null) hmEmpHistory = new HashMap<String, Map<String,String>>(); 
			
			double dblNetAmount = 0.0d;
			Map hmInner = new HashMap();
			Map hmSalary = new HashMap();
			List alEarnings = new ArrayList();
			List alDeductions = new ArrayList();
			Map hmPayPayroll = new HashMap();
			Map hmEmpPayroll = null;
			Map hmIsApprovedSalary = new HashMap();
			Map<String, String> hmPresentDays=new HashMap<String, String>();
			String strEmpIdOld = null;
			String strEmpIdNew = null; 
			double dblGross = 0;
			double dblNet = 0;
			if(strEmpIds !=null && !strEmpIds.equals("") && strEmpIds.length() > 0){
				StringBuilder sbQuery = new StringBuilder();
				Map<String, String> hmEmpPaidDays = new HashMap<String, String>();
				sbQuery.append("select sum(paid_days) as paid_days,emp_id from approve_attendance where paycycle= ? and emp_id in ("+strEmpIds+") group by emp_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strPC));
				rs = pst.executeQuery();
				while(rs.next()) {
					hmEmpPaidDays.put(rs.getString("emp_id"), rs.getString("paid_days"));
				}
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select * from payroll_generation pg, employee_official_details eod where eod.emp_id = pg.emp_id and pg.is_paid=true and paycycle= ?");
				sbQuery.append(" and pg.emp_id in ("+strEmpIds+") ");
				sbQuery.append(" order by pg.emp_id,pg.salary_head_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strPC));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					strEmpIdNew = rs.getString("emp_id");
					
					if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
						hmEmpPayroll = new HashMap();
						dblNet = 0;
					}
					
					if("E".equalsIgnoreCase(rs.getString("earning_deduction")) && !alEarnings.contains(rs.getString("salary_head_id"))){
						alEarnings.add(rs.getString("salary_head_id"));
					}else if("D".equalsIgnoreCase(rs.getString("earning_deduction")) && !alDeductions.contains(rs.getString("salary_head_id"))){
						alDeductions.add(rs.getString("salary_head_id"));
					}
					
	//				hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
					
					
					hmEmpPayroll.put(rs.getString("salary_head_id"),  uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("amount"))));
					
					
					if("E".equalsIgnoreCase(rs.getString("earning_deduction"))){
						double dblAmount = rs.getDouble("amount");
						dblGross = uF.parseToDouble((String)hmEmpPayroll.get("GROSS"));
						//System.out.println("uF.parseToDouble((String)hmEmpPayroll.get(GROSS))=="+uF.parseToDouble((String)hmEmpPayroll.get("GROSS")));
						dblNet += dblAmount;
						hmEmpPayroll.put("GROSS",  uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(dblGross + dblAmount)));
						
					}else{
						double dblAmount = rs.getDouble("amount");
	//					double dblAmount = uF.parseToDouble((String)hmEmpPayroll.get("GROSS"));
						dblNet -= dblAmount;
	//					hmEmpPayroll.put("GROSS",  (dblGross + dblAmount)+"");
					}
					
					
					Map<String, String> hmCurrency = (Map)hmCurrencyDetails.get(rs.getString("currency_id"));
					if(hmCurrency==null)hmCurrency = new HashMap<String, String>();
					
//					hmEmpPayroll.put("NET", uF.showData(hmCurrency.get("LONG_CURR"), "")+" "+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNet));
					hmEmpPayroll.put("NET", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNet));
					hmPayPayroll.put(strEmpIdNew, hmEmpPayroll);
					
					
	
					if(rs.getBoolean("is_paid")){
						hmIsApprovedSalary.put(strEmpIdNew, rs.getString("is_paid"));
					}
					if(!hmPresentDays.containsKey(rs.getString("emp_id"))){
						hmPresentDays.put(rs.getString("emp_id"), hmEmpPaidDays.get(rs.getString("emp_id")));
					}
					
					strEmpIdOld = strEmpIdNew;
				}
				rs.close();
				pst.close();
			}
//			System.out.println("getStrMonth()== 1 =>"+getStrMonth());
			
			if(getStrMonth()!=null){
				setStrMonth(getStrMonth());
			}else{
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			}
			
			Map<String, String> hmOrg =  CF.getOrgName(con);
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			
			Map<String, String> hmEmpOrgId =  CF.getEmpOrgIdList(con, uF);
			if(hmEmpOrgId == null) hmEmpOrgId = new HashMap<String, String>();
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			if(hmWLocation == null) hmWLocation = new HashMap<String, String>();
			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
			if(hmEmpWlocationMap == null) hmEmpWlocationMap = new HashMap<String, String>();
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMapId(con);
			if(hmEmpCodeDesig==null) hmEmpCodeDesig = new HashMap<String, String>();
			Map<String, String> hmCodeDesig = CF.getDesigMap(con);
			if(hmCodeDesig == null) hmCodeDesig = new HashMap<String, String>();
			
			Map<String, String> hmEmpGradeMap = CF.getEmpGradeMap(con);
			if(hmEmpGradeMap == null) hmEmpGradeMap = new HashMap<String, String>();
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			if(hmGradeMap == null) hmGradeMap = new HashMap<String, String>();
			Map<String, String> hmGradeDesig = CF.getGradeDesig(con);
			if(hmGradeDesig == null) hmGradeDesig = new HashMap<String, String>();
			
			List alReportList = new ArrayList();
			List alReportInner = new ArrayList();
			Set set = hmPayPayroll.keySet();
			Iterator it = set.iterator();
			double amount=0.0;
			while(it.hasNext()){
				
				alReportInner = new ArrayList();
				
				String strEmpId = (String)it.next();
				Map hmPayroll = (Map)hmPayPayroll.get(strEmpId);
				if(hmPayroll==null)hmPayroll=new HashMap();
				
				Map<String, String> hm = hmEmpHistory.get(strEmpId);
				
				alReportInner.add(uF.showData((String)hmEmpCode.get(strEmpId), ""));
				alReportInner.add(uF.showData((String)hmEmpMap.get(strEmpId), ""));
				
				String strOrg = uF.showData(hmOrg.get(hmEmpOrgId.get(strEmpId)), "");
				if(hm != null && uF.parseToInt(hm.get("EMP_ORG")) > 0){
					strOrg = uF.showData(hmOrg.get(hm.get("EMP_ORG")), "");
				}
				alReportInner.add(strOrg);
				
				String strDepartment = uF.showData((String) hmDept.get((String)hmEmpDepartment.get(strEmpId)),"");
				if(hm != null && uF.parseToInt(hm.get("EMP_DEPART")) > 0){
					strDepartment = uF.showData(hmDept.get(hm.get("EMP_DEPART")), "");
				}
				alReportInner.add(strDepartment);
				
				String strWLocation = uF.showData((String) hmWLocation.get((String)hmEmpWlocationMap.get(strEmpId)),"");
				if(hm != null && uF.parseToInt(hm.get("EMP_WLOCATION")) > 0){
					strWLocation = uF.showData(hmWLocation.get(hm.get("EMP_WLOCATION")), "");
				}
				alReportInner.add(strWLocation);
				
				String strDesig = uF.showData((String) hmCodeDesig.get((String)hmEmpCodeDesig.get(strEmpId)),"");
				String strGrade = uF.showData((String) hmGradeMap.get((String)hmEmpGradeMap.get(strEmpId)),"");
				if(hm != null && uF.parseToInt(hm.get("EMP_GRADE")) > 0){
					strDesig = uF.showData(hmCodeDesig.get(hmGradeDesig.get(hm.get("EMP_GRADE"))), "");
					strGrade = uF.showData(hmGradeMap.get(hm.get("EMP_GRADE")), "");
				}
//				alReportInner.add(uF.showData((String) hmCodeDesig.get((String)hmEmpCodeDesig.get(strEmpId)),"")+" ("+uF.showData((String) hmGradeMap.get((String)hmEmpGradeMap.get(strEmpId)),"")+")");
				alReportInner.add(strDesig+" ("+strGrade+")");
				
				alReportInner.add(uF.showData((String)hmEmpJoiningDate.get(strEmpId),""));
				alReportInner.add(uF.showData((String)hmPresentDays.get(strEmpId),""));
				
				alReportInner.add((String)hmPayroll.get("NET"));
				alReportInner.add((String)hmPayroll.get("GROSS"));
			
				for(int i=0; i<alEarnings.size(); i++){
					
					alReportInner.add(uF.showData((String)hmPayroll.get((String)alEarnings.get(i)), "0"));
	    		}
	    	
	    		for(int i=0; i<alDeductions.size(); i++){
	    			alReportInner.add(uF.showData((String)hmPayroll.get((String)alDeductions.get(i)), "0"));
	    		}
	    		
	    		alReportList.add(alReportInner);
			}
		
			
			
			request.setAttribute("reportList", alReportList);
			request.setAttribute("alEarnings", alEarnings);
			request.setAttribute("alDeductions", alDeductions);
			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
			request.setAttribute("hmPayPayroll", hmPayPayroll);
			request.setAttribute("hmEmpMap", hmEmpMap);
			request.setAttribute("hmIsApprovedSalary", hmIsApprovedSalary);
			
			
		//System.out.println("alReportList===>"+alReportList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public String loadPaySlips(){
		UtilityFunctions uF=new UtilityFunctions();
		paycycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		alMonthList = new FillMonth().fillMonth();
		
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		employementTypeList = new FillEmploymentType().fillEmploymentType(request);
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++){
				if(getF_org().equals(organisationList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=organisationList.get(i).getOrgName();
					} else {
						strOrg+=", "+organisationList.get(i).getOrgName();
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
		if (getPaycycle() != null) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			
			strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
		}
		hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}


	public String viewApporvedPayroll(UtilityFunctions uF){
		//System.out.println("viewApporvedPayroll===");
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map hmEmpMap = CF.getEmpNameMap(con,strUserType, strEmpId);
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map hmSalaryDetails = CF.getSalaryHeadsMap(con);
			Map<String, String> hmEmpCode =CF.getEmpCodeMap(con);
			Map<String, String> hmEmpPanNo =CF.getEmpPANNoMap(con);
			Map<String, String> hmEmpDepartment =CF.getEmpDepartmentMap(con);			
			Map<String, String> hmDept =CF.getDeptMap(con);
			if(hmDept == null) hmDept = new HashMap<String, String>();
			Map<String, String> hmEmpJoiningDate=CF.getEmpJoiningDateMap(con, uF);
			
			
			String strEmpIds = getEmpPayrollHistory(con,uF);

			Map<String, Map<String, String>> hmEmpHistory = (Map<String, Map<String, String>>) request.getAttribute("hmEmpHistory");
			if(hmEmpHistory == null) hmEmpHistory = new HashMap<String, Map<String,String>>(); 
			
			double dblNetAmount = 0.0d;
			Map hmInner = new HashMap();
			Map hmSalary = new HashMap();
			List alEarnings = new ArrayList();
			List alDeductions = new ArrayList();
		
			Map hmPayPayroll = new HashMap();
			Map hmEmpPayroll = null;
			Map hmIsApprovedSalary = new HashMap();
			Map<String, String> hmPresentDays=new HashMap<String, String>();
			String strEmpIdOld = null;
			String strEmpIdNew = null; 
			double dblGross = 0;
			double dblNet = 0;
			double totalNetAmt=0;
			double totalGrossAmt=0;
			double amt=0;
			//Map<String,String> GrandtotalList=new HashMap<String,String>();
			if(strEmpIds !=null && !strEmpIds.equals("") && strEmpIds.length() > 0){
				
				StringBuilder sbQuery = new StringBuilder();
				Map<String, String> hmEmpPaidDays = new HashMap<String, String>();
				sbQuery.append("select sum(paid_days) as paid_days,emp_id from approve_attendance where paycycle= ? and emp_id in ("+strEmpIds+") group by emp_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strPC));
				rs = pst.executeQuery();
				while(rs.next()) {
					hmEmpPaidDays.put(rs.getString("emp_id"), rs.getString("paid_days"));
				}
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select * from payroll_generation pg, employee_official_details eod where eod.emp_id = pg.emp_id and pg.is_paid=true and paycycle= ?");
				sbQuery.append(" and pg.emp_id in ("+strEmpIds+") ");
				sbQuery.append(" order by pg.emp_id,pg.salary_head_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strPC));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					strEmpIdNew = rs.getString("emp_id");
					
					if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
						hmEmpPayroll = new HashMap();
						dblNet = 0;
					}
					
					if("E".equalsIgnoreCase(rs.getString("earning_deduction")) && !alEarnings.contains(rs.getString("salary_head_id"))) {
						alEarnings.add(rs.getString("salary_head_id"));

					} else if("D".equalsIgnoreCase(rs.getString("earning_deduction")) && !alDeductions.contains(rs.getString("salary_head_id"))) {
						alDeductions.add(rs.getString("salary_head_id"));
					
					}
	//				hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
					
					hmEmpPayroll.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("amount"))));
					
					if("E".equalsIgnoreCase(rs.getString("earning_deduction"))) {
						double dblAmount = rs.getDouble("amount");
						dblGross = uF.parseToDouble((String)hmEmpPayroll.get("GROSS"));
						dblNet += dblAmount;
						hmEmpPayroll.put("GROSS",  uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(dblGross + dblAmount)));
					
					} else {
						double dblAmount = rs.getDouble("amount");
						dblNet -= dblAmount;
					}
					
					Map<String, String> hmCurrency = (Map)hmCurrencyDetails.get(rs.getString("currency_id"));
					if(hmCurrency==null)hmCurrency = new HashMap<String, String>();
					
					hmEmpPayroll.put("NET", uF.formatIntoTwoDecimal(dblNet));
					hmPayPayroll.put(strEmpIdNew, hmEmpPayroll);
	
					if(rs.getBoolean("is_paid")){
						hmIsApprovedSalary.put(strEmpIdNew, rs.getString("is_paid"));
					}
					if(!hmPresentDays.containsKey(rs.getString("emp_id"))){
						hmPresentDays.put(rs.getString("emp_id"), hmEmpPaidDays.get(rs.getString("emp_id")));
					}
					
					strEmpIdOld = strEmpIdNew;
				}
				rs.close();
				pst.close();
			}
	
			if(getStrMonth()!=null) {
				setStrMonth(getStrMonth());
			} else {
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			}
		
			
			Map<String, String> hmOrg =  CF.getOrgName(con);
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			
			
			Map<String, String> hmEmpOrgId =  CF.getEmpOrgIdList(con, uF);
			if(hmEmpOrgId == null) hmEmpOrgId = new HashMap<String, String>();
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			if(hmWLocation == null) hmWLocation = new HashMap<String, String>();
			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
			if(hmEmpWlocationMap == null) hmEmpWlocationMap = new HashMap<String, String>();
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMapId(con);
			if(hmEmpCodeDesig==null) hmEmpCodeDesig = new HashMap<String, String>();
			Map<String, String> hmCodeDesig = CF.getDesigMap(con);
			if(hmCodeDesig == null) hmCodeDesig = new HashMap<String, String>();
			
			Map<String, String> hmEmpGradeMap = CF.getEmpGradeMap(con);
			if(hmEmpGradeMap == null) hmEmpGradeMap = new HashMap<String, String>();
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			if(hmGradeMap == null) hmGradeMap = new HashMap<String, String>();
			Map<String, String> hmGradeDesig = CF.getGradeDesig(con);
			if(hmGradeDesig == null) hmGradeDesig = new HashMap<String, String>();
			
			List alReportList = new ArrayList();
			List alReportInner = new ArrayList();
			
		
			Set set = hmPayPayroll.keySet();
			Iterator it = set.iterator();
			
			List<String>alEmp=new ArrayList<String>();
			Map<String,String> totalSalaryHead=new HashMap<String,String>();
			int count=0;
			while(it.hasNext()){
				count++;
				alReportInner = new ArrayList();
				
				String strEmpId = (String)it.next();
				alEmp.add(strEmpId);
				
				Map hmPayroll = (Map)hmPayPayroll.get(strEmpId);
				if(hmPayroll==null)hmPayroll=new HashMap();
				
				Map<String, String> hm = hmEmpHistory.get(strEmpId);
				
				alReportInner.add(Integer.toString(count)); //0
				
				alReportInner.add(uF.showData((String)hmEmpCode.get(strEmpId), "")); //1
				alReportInner.add(uF.showData((String)hmEmpMap.get(strEmpId), "")); //2
				alReportInner.add(uF.showData((String)hmEmpPanNo.get(strEmpId), "")); //3
				
				String strOrg = uF.showData(hmOrg.get(hmEmpOrgId.get(strEmpId)), "");
				if(hm != null && uF.parseToInt(hm.get("EMP_ORG")) > 0){
					strOrg = uF.showData(hmOrg.get(hm.get("EMP_ORG")), "");
				}
				alReportInner.add(strOrg); //4
				
				String strDepartment = uF.showData((String) hmDept.get((String)hmEmpDepartment.get(strEmpId)),"");
				if(hm != null && uF.parseToInt(hm.get("EMP_DEPART")) > 0){
					strDepartment = uF.showData(hmDept.get(hm.get("EMP_DEPART")), "");
				}
				alReportInner.add(strDepartment); //5
				
				String strWLocation = uF.showData((String) hmWLocation.get((String)hmEmpWlocationMap.get(strEmpId)),"");
				if(hm != null && uF.parseToInt(hm.get("EMP_WLOCATION")) > 0){
					strWLocation = uF.showData(hmWLocation.get(hm.get("EMP_WLOCATION")), "");
				}
				alReportInner.add(strWLocation); //6
				
				String strDesig = uF.showData((String) hmCodeDesig.get((String)hmEmpCodeDesig.get(strEmpId)),"");
				String strGrade = uF.showData((String) hmGradeMap.get((String)hmEmpGradeMap.get(strEmpId)),"");
				if(hm != null && uF.parseToInt(hm.get("EMP_GRADE")) > 0){
					strDesig = uF.showData(hmCodeDesig.get(hmGradeDesig.get(hm.get("EMP_GRADE"))), "");
					strGrade = uF.showData(hmGradeMap.get(hm.get("EMP_GRADE")), "");
				}
//				alReportInner.add(uF.showData((String) hmCodeDesig.get((String)hmEmpCodeDesig.get(strEmpId)),"")+" ("+uF.showData((String) hmGradeMap.get((String)hmEmpGradeMap.get(strEmpId)),"")+")");
				alReportInner.add(strDesig+" ("+strGrade+")"); //7
				
				alReportInner.add(uF.showData((String)hmEmpJoiningDate.get(strEmpId),"")); //8
				alReportInner.add(uF.showData((String)hmPresentDays.get(strEmpId),"")); //9
				
				//alReportInner.add((String)hmPayroll.get("NET"));
				//alReportInner.add((String)hmPayroll.get("GROSS"));
				alReportInner.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble((String)hmPayroll.get("NET"))));  //10
				alReportInner.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble((String)hmPayroll.get("GROSS"))));  //11
				
				totalNetAmt = totalNetAmt+uF.parseToDouble((String)hmPayroll.get("NET"));
				totalGrossAmt = totalGrossAmt+uF.parseToDouble((String)hmPayroll.get("GROSS"));
				

				
				for(int i=0; i<alEarnings.size(); i++){
					alReportInner.add(uF.showData((String)hmPayroll.get((String)alEarnings.get(i)), "0"));

				}
	    	
	    		for(int i=0; i<alDeductions.size(); i++){
	    			alReportInner.add(uF.showData((String)hmPayroll.get((String)alDeductions.get(i)), "0"));
	    	
	    		}
	    		
	    		alReportList.add(alReportInner);
			}
			
			//System.out.println("alEmp"+alEmp);
			
			alReportInner = new ArrayList();
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("Grand Total:");
		
	
	
	//************************************** for earning amount******************		
			double totalEarningAmt=0;
			double totalDeductAmt=0;
			
			alReportInner.add(uF.getRoundOffValue(0, totalNetAmt)); //10
			alReportInner.add(uF.getRoundOffValue(0, totalGrossAmt)); // 11 uF.parseToInt(CF.getRoundOffCondtion())

			
			//System.out.println("in alEarnings"+alEarnings.size());
			Map<String,String>hmSallaryTotal=new LinkedHashMap<String,String>();
			for(int ii=0; ii<alEarnings.size(); ii++){	
			
				for(int i=0;i<alEmp.size();i++){
					
					Map hmPayroll = (Map)hmPayPayroll.get(alEmp.get(i));
					 double amtE= uF.parseToDouble((String)hmPayroll.get((String)alEarnings.get(ii)));
					 totalEarningAmt=totalEarningAmt+amtE;
					
				}
				
				alReportInner.add(uF.getRoundOffValue(0,totalEarningAmt));
				totalEarningAmt=0;
				
			}
			
		
	//************************************** for deduction amount******************		
			for(int ii=0; ii<alDeductions.size(); ii++){	
				
				for(int i=0;i<alEmp.size();i++){
					
					Map hmPayroll = (Map)hmPayPayroll.get(alEmp.get(i));
					 double amtD= uF.parseToDouble((String)hmPayroll.get((String)alDeductions.get(ii)));
					 totalDeductAmt = totalDeductAmt+amtD;
				}
				alReportInner.add(uF.getRoundOffValue(0,totalDeductAmt)); //uF.parseToInt(CF.getRoundOffCondtion())

				totalDeductAmt=0;
			
			}
			
//*******************************************************************
			
			//System.out.println("alReportInner"+alReportInner.size());
			
    		alReportList.add(alReportInner);
		
			request.setAttribute("reportList", alReportList);
			request.setAttribute("alEarnings", alEarnings);
			request.setAttribute("alDeductions", alDeductions);
			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
			request.setAttribute("hmPayPayroll", hmPayPayroll);
			request.setAttribute("hmEmpMap", hmEmpMap);
			request.setAttribute("hmIsApprovedSalary", hmIsApprovedSalary);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
		return SUCCESS;
		
	}
	
	public String getEmpPayrollHistory(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder sbEmp = null;
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from payroll_history ph,employee_official_details eod where ph.emp_id=eod.emp_id and paycycle_from =? and paycycle_to=? and paycycle= ? ");
			
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and ph.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and ph.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
            if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                	sbQuery.append(" ph.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            } 
            
            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
                sbQuery.append(" and ph.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and ph.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and ph.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and ph.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by ph.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPC));
//			System.out.println("PR/961---pst1====>"+pst);     
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
//			System.out.println("1 hmEmpHistory====>"+hmEmpHistory);
//			System.out.println("1 empSetlist====>"+empSetlist.toString());
			
			sbQuery = new StringBuilder();
			sbQuery.append("select distinct(pg.emp_id) as emp_id from payroll_generation pg, employee_official_details eod where eod.emp_id = pg.emp_id and pg.is_paid=true and paid_from= ? and paid_to=? and  paycycle= ?");
			
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
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
            sbQuery.append(" and pg.emp_id not in (select emp_id from payroll_history where paycycle_from =? and paycycle_to=? and paycycle= ?) ");
			sbQuery.append(" order by pg.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPC));
			pst.setDate(4, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(strPC));
//			System.out.println("pst2====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()){
				empSetlist.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
//			System.out.println("2 empSetlist====>"+empSetlist.toString());
			
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


	public String getStrApprove() {
		return strApprove;
	}


	public void setStrApprove(String strApprove) {
		this.strApprove = strApprove;
	}


	public String[] getChbxApprove() {
		return chbxApprove;
	}


	public void setChbxApprove(String[] chbxApprove) {
		this.chbxApprove = chbxApprove;
	}


	public List<FillMonth> getAlMonthList() {
		return alMonthList;
	}


	public void setAlMonthList(List<FillMonth> alMonthList) {
		this.alMonthList = alMonthList;
	}


	public String getApprovePC() {
		return approvePC;
	}


	public void setApprovePC(String approvePC) {
		this.approvePC = approvePC;
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


	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}


	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
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
