package com.konnect.jpms.payroll.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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

public class DepartmentwiseReport extends  ActionSupport implements ServletRequestAware, IStatements {

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
	
	
	List<FillPayCycles> paycycleList ;
	List<FillFinancialYears> financialYearList;
	
	String paycycleDate;
	String strStartDate;
	String strEndDate; 
	
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	String[] f_employeType;
	
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillEmploymentType> employementTypeList;
	
	String exportType;
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	String strEmployeType;
	
	private static Logger log = Logger.getLogger(DepartmentwiseReport.class);
	
	public String execute() throws Exception {
		session = request.getSession();
		request.setAttribute(PAGE, "/jsp/payroll/reports/DepartmentwiseReport.jsp");
		request.setAttribute(TITLE, "Departmentwise Report");
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strEmpId =(String) session.getAttribute(EMPID);
		strUserType =(String) session.getAttribute(USERTYPE);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		String[] strPayCycleDates = null;
		if (getPaycycle() != null) {
			strPayCycleDates = getPaycycle().split("-");
		} else {
			strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}
		
		strD1 = strPayCycleDates[0];
		strD2 = strPayCycleDates[1];
		strPC = strPayCycleDates[2];
		
		if(getStrStartDate()==null){
			setStrStartDate(strD1);
		}
		if(getStrEndDate()==null){
			setStrEndDate(strD2);
		}
		
		request.setAttribute("strD1",strD1);
		request.setAttribute("strD2",strD2);
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
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
		
		viewDepartmentWiseReport(uF);
		
		alMonthList = new FillMonth().fillMonth();
		
		return loadPaySlips(uF);
	}
	
	
	public String loadPaySlips(UtilityFunctions uF){
		
		paycycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		alMonthList = new FillMonth().fillMonth();

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
		
		
		if(uF.parseToInt(getPaycycleDate()) == 1 || uF.parseToInt(getPaycycleDate()) == 0) {
			alFilter.add("PAYCYCLE");
			if (getPaycycle() != null) {
				String strPayCycle = "";
				int k = 0;
				for (int i = 0; paycycleList != null && i < paycycleList.size(); i++) {
					if (getPaycycle().equals(paycycleList.get(i).getPaycycleId())) {
						if (k == 0) {
							strPayCycle = paycycleList.get(i).getPaycycleName();
						} else {
							strPayCycle += ", " + paycycleList.get(i).getPaycycleName();
						}
						k++;
					}
				}
				if (strPayCycle != null && !strPayCycle.equals("")) {
					hmFilter.put("PAYCYCLE", strPayCycle);
				} else {
					hmFilter.put("PAYCYCLE", "All Paycycle");
				}
			}
		}
		
		if(uF.parseToInt(getPaycycleDate()) == 2) {
			alFilter.add("PERIOD");
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				hmFilter.put("PERIOD", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
			}
		}
		
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	public String viewDepartmentWiseReport(UtilityFunctions uF){
			
			Connection con = null;
			PreparedStatement pst=null;
			ResultSet rs = null;
			Database db = new Database();
			db.setRequest(request);
	
			try {
				
				con = db.makeConnection(con);
				
				String strEmpIds = getEmpPayrollHistory(con,uF);
	
				Map<String, Map<String, String>> hmEmpHistory = (Map<String, Map<String, String>>) request.getAttribute("hmEmpHistory");
				if(hmEmpHistory == null) hmEmpHistory = new HashMap<String, Map<String,String>>(); 
				
				if(strEmpIds !=null && !strEmpIds.equals("") && strEmpIds.length() > 0){
					Map<String, String> hmEmpJoiningDate=CF.getEmpJoiningDateMap(con, uF);
					Map<String,String> hmEmpMap = CF.getEmpNameMap(con,null,null);
					Map<String,String> hmSalaryDetails = CF.getSalaryHeadsMap(con);
					Map<String, String> hmEmpCode =CF.getEmpCodeMap(con);
					Map<String, String> hmEmpDepartment =CF.getEmpDepartmentMap(con);			
					Map<String, String> hmDept =CF.getDeptMap(con);
					Map<String, String> hmEmpUAN =CF.getEmpUANMap(con);
					Map<String, String> hmEmpPanNo =CF.getEmpPANNoMap(con); //added by parvez
					
					
					Map<String, String> hmSalaryWeight = new HashMap<String, String>();
					Map<String, Boolean> hmSalaryVariable = new HashMap<String, Boolean>();
		
					pst = con.prepareStatement("select salary_head_id, weight,is_variable from salary_details order by level_id,weight");
					rs = pst.executeQuery();
					while(rs.next()){
						if(hmSalaryWeight.get(rs.getString("salary_head_id"))==null){
						hmSalaryWeight.put(rs.getString("salary_head_id"), rs.getString("weight"));
						hmSalaryVariable.put(rs.getString("salary_head_id"),uF.parseToBoolean(rs.getString("is_variable")));
		
						}
					}
					rs.close();
					pst.close();
//					System.out.println("hmSalaryVariable ===>> " + hmSalaryVariable);
					
					pst=con.prepareStatement("select org_id,org_name from org_details");
					Map<String, String> hmOrg=new HashMap<String, String>();
					rs=pst.executeQuery();
					while(rs.next()){
						hmOrg.put(rs.getString("org_id"), rs.getString("org_name"));
					}
					rs.close();
					pst.close();
					
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
					sbQuery.append("select * from payroll_generation pg, employee_official_details eod where eod.emp_id = pg.emp_id and pg.is_paid=true ");
					sbQuery.append(" and pg.emp_id in ("+strEmpIds+") ");
					if(getPaycycleDate()!=null && getPaycycleDate().equals("2")) {
						sbQuery.append(" and paid_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '" + uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
					} else {
						sbQuery.append(" and paycycle="+uF.parseToInt(strPC));
					} 
//					
//					if(uF.parseToInt(getF_org())>0){
//						sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
//					}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
//							sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//					}
//					if(getF_strWLocation()!=null && getF_strWLocation().length>0){
//			            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//			        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
//						sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//					}
//		            if(getF_department()!=null && getF_department().length>0){
//		                sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
//		            }
//		            if(getF_level()!=null && getF_level().length>0){
//		                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
//		            }
//		            if(getF_service()!=null && getF_service().length>0){
//						sbQuery.append(" and pg.service_id in ("+StringUtils.join(getF_service(), ",")+") ");
//					}
					sbQuery.append(" order by eod.depart_id, pg.emp_id, earning_deduction desc, salary_head_id");
					pst = con.prepareStatement(sbQuery.toString());
//					System.out.println("pst==="+pst);
					rs = pst.executeQuery();
					
		//			List<String> alEarnings = new ArrayList<String>();
		//			List<String> alDeductions = new ArrayList<String>();
					
					List<String> alEarningss = new ArrayList<String>();
					List<String> alDeductionss = new ArrayList<String>();
					
					List<ComparatorWeight> alEarnings = new ArrayList<ComparatorWeight>();
					List<ComparatorWeight> alDeductions = new ArrayList<ComparatorWeight>();
					
					Map<String,Map<String,String>> hmPayPayroll = new LinkedHashMap<String,Map<String,String>>();
					Map<String, String> hmPresentDays=new HashMap<String, String>();
					Map<String,List<String>> deptEmpMap=new LinkedHashMap<String,List<String>>();
					Map<String,Map<String,String>> empSalaryMap=new HashMap<String,Map<String,String>>();
					
					while(rs.next()){
						Map<String, String> hm = hmEmpHistory.get(rs.getString("emp_id"));
						String strDepartment = rs.getString("depart_id");
						if(hm != null && uF.parseToInt(hm.get("EMP_DEPART")) > 0){
							strDepartment = uF.showData(hm.get("EMP_DEPART"), "0");
						}
//						List<String>empList=deptEmpMap.get(rs.getString("depart_id"));
						List<String>empList=deptEmpMap.get(strDepartment);
						if(empList==null)empList=new ArrayList<String>();
						if(!empList.contains(rs.getString("emp_id"))){
							empList.add(rs.getString("emp_id"));
						}
						
						deptEmpMap.put(strDepartment,empList);
						
						Map<String,String> salaryMap=empSalaryMap.get(rs.getString("emp_id"));
						if(salaryMap==null)salaryMap=new HashMap<String,String>();
						salaryMap.put(rs.getString("salary_head_id"), rs.getString("amount"));
						
						if("E".equalsIgnoreCase(rs.getString("earning_deduction"))){
							double dblAmount = rs.getDouble("amount");
							double dblGross = uF.parseToDouble(salaryMap.get("GROSS"));
							dblGross += dblAmount;
							salaryMap.put("GROSS",  uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(dblGross)));
							
							double dblNet = uF.parseToDouble(salaryMap.get("NET"));
							dblNet+=dblAmount;
							salaryMap.put("NET",  uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(dblNet)));
						}else{
							double dblAmount = rs.getDouble("amount");
							double dblNet = uF.parseToDouble(salaryMap.get("NET"));
							dblNet-=dblAmount;
							salaryMap.put("NET",  uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(dblNet)));
							
						}
						empSalaryMap.put(rs.getString("emp_id"),salaryMap);
						
						
						if("E".equalsIgnoreCase(rs.getString("earning_deduction")) && !alEarningss.contains(rs.getString("salary_head_id"))){
							//alEarnings.add(rs.getString("salary_head_id"));
							alEarningss.add(rs.getString("salary_head_id"));
							alEarnings.add(new ComparatorWeight(rs.getString("salary_head_id"),uF.parseToInt(hmSalaryWeight.get(rs.getString("salary_head_id"))),hmSalaryVariable.get(rs.getString("salary_head_id"))));
						}else if("D".equalsIgnoreCase(rs.getString("earning_deduction")) && !alDeductionss.contains(rs.getString("salary_head_id"))){
		//					alDeductions.add(rs.getString("salary_head_id"));
							alDeductionss.add(rs.getString("salary_head_id"));
							alDeductions.add(new ComparatorWeight(rs.getString("salary_head_id"),uF.parseToInt(hmSalaryWeight.get(rs.getString("salary_head_id"))),hmSalaryVariable.get(rs.getString("salary_head_id"))));
						}
		
						if(!hmPresentDays.containsKey(rs.getString("emp_id"))){
//							hmPresentDays.put(rs.getString("emp_id"), (uF.parseToInt(rs.getString("present_days")) == 0) ? rs.getString("paid_days") : rs.getString("present_days"));
							hmPresentDays.put(rs.getString("emp_id"), hmEmpPaidDays.get(rs.getString("emp_id")));
						}
					}
					rs.close();
					pst.close();
					
//					System.out.println("alEarnings==="+alEarnings);
//					System.out.println("alDeductions==="+alDeductions);
					Collections.sort(alEarnings);
		//			System.out.println("alEarnings==="+alEarnings);
		
					
					List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
					List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
					
					alInnerExport.add(new DataStyle(uF.showData(hmOrg.get(getF_org()), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					if(getPaycycleDate()!=null && getPaycycleDate().equals("2")) {
						alInnerExport.add(new DataStyle("Departmentwise Report from "+getStrStartDate()+" - "+getStrEndDate(),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					} else {
						alInnerExport.add(new DataStyle("Departmentwise Report from "+strD1+" - "+strD2,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					}
					String curr_date=uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
					alInnerExport.add(new DataStyle("Date- "+curr_date,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					
				   	alInnerExport.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("UAN",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				   	alInnerExport.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				   	alInnerExport.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				   	alInnerExport.add(new DataStyle("Employee PAN No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				   	alInnerExport.add(new DataStyle("Joining Date",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 
				   	alInnerExport.add(new DataStyle("Present Days",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 
					alInnerExport.add(new DataStyle("Department",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					
					boolean flag=false; 
		//			double grossEarning=0;
					for (int ii=0; ii<alEarnings.size(); ii++){
						ComparatorWeight comparatorWeight=alEarnings.get(ii);
						
						if(flag!=comparatorWeight.isVariable()){
							flag=true;
//							alInnerExport.add(new DataStyle("Gross Earning",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						}
		//				alInnerExport.add(new DataStyle(((String)hmSalaryDetails.get((String)alEarnings.get(ii)))+"(+)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle(((String)hmSalaryDetails.get(((ComparatorWeight)alEarnings.get(ii)).getStrName()))+"(+)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		//				}
						
					}
					alInnerExport.add(new DataStyle("Gross",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					for (int ii=0; ii<alDeductions.size(); ii++){
		//				alInnerExport.add(new DataStyle(((String)hmSalaryDetails.get((String)alDeductions.get(ii)))+"(-)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle(((String)hmSalaryDetails.get(((ComparatorWeight)alDeductions.get(ii)).getStrName()))+"(-)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						
					}
					alInnerExport.add(new DataStyle("Net",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					
					reportListExport.add(alInnerExport); 
					
					
					Set set = deptEmpMap.keySet();
					
					Iterator it = set.iterator();
					List<List<String>> alReportList = new ArrayList<List<String>>();
					Map<String,String> GrandtotalList=new HashMap<String,String>();
					int count=0;
					while(it.hasNext()){
						
						String deptId = (String)it.next();
						
						List<String>empList=deptEmpMap.get(deptId);
						double dblNet=0;
						double dblGross=0;
						Map<String,String> totalSalaryHead=new HashMap<String,String>();
						
						for(String emp:empList){
							count++;
							
							Map<String, String> hm = hmEmpHistory.get(emp);
							Map<String,String> salaryMap=empSalaryMap.get(emp);
							
							List<String> alReportInner = new ArrayList<String>();
							alReportInner.add(""+count);							//0
							alReportInner.add(uF.showData(hmEmpUAN.get(emp),""));	//1
							alReportInner.add(uF.showData(hmEmpCode.get(emp),""));	//2
							alReportInner.add(uF.showData(hmEmpMap.get(emp),""));	//3
							alReportInner.add(uF.showData(hmEmpPanNo.get(emp),""));	//4
							alReportInner.add(uF.showData(hmEmpJoiningDate.get(emp),""));	//5
							alReportInner.add(uF.showData(hmPresentDays.get(emp),""));		//6
							
							String strDepartment = uF.showData((String) hmDept.get((String)hmEmpDepartment.get(emp)),"");
							if(hm != null && uF.parseToInt(hm.get("EMP_DEPART")) > 0){
								strDepartment = uF.showData(hmDept.get(hm.get("EMP_DEPART")), "");
							}
							alReportInner.add(strDepartment);
//							alReportInner.add(uF.showData(hmDept.get(hmEmpDepartment.get(emp)),""));	
							
							/*alReportInner.add(salaryMap.get("NET"));
							alReportInner.add(salaryMap.get("GROSS"));*/
							
							dblNet+=uF.parseToDouble(salaryMap.get("NET"));
							dblGross += uF.parseToDouble(salaryMap.get("GROSS"));
							
							
							alInnerExport=new ArrayList<DataStyle>();
							alInnerExport.add(new DataStyle(uF.showData(""+count, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle(uF.showData(hmEmpUAN.get(emp), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle(uF.showData(hmEmpCode.get(emp), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
							alInnerExport.add(new DataStyle(uF.showData(hmEmpMap.get(emp), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle(uF.showData(hmEmpPanNo.get(emp), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle(uF.showData(hmEmpJoiningDate.get(emp), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle(uF.showData(hmPresentDays.get(emp), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
							alInnerExport.add(new DataStyle(uF.showData(hmDept.get(hmEmpDepartment.get(emp)), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
							boolean flag1=false;
							double grossEarning=0;
							for(int i=0; i<alEarnings.size(); i++){
		//						double earningHead=uF.parseToDouble(salaryMap.get(alEarnings.get(i)))+uF.parseToDouble(totalSalaryHead.get(alEarnings.get(i)));
		//						totalSalaryHead.put(alEarnings.get(i), earningHead+"");
								ComparatorWeight comparatorWeight=alEarnings.get(i);
								
								if(flag1!=comparatorWeight.isVariable()){
									flag1=true;
//									alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),grossEarning),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
									//alReportInner.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),grossEarning));
		
								}
									grossEarning += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(salaryMap.get(((ComparatorWeight)alEarnings.get(i)).getStrName()))));
		//						}else{
									double earningHead = uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(salaryMap.get(((ComparatorWeight)alEarnings.get(i)).getStrName())))) +uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()))));
									totalSalaryHead.put(((ComparatorWeight)alEarnings.get(i)).getStrName(), earningHead+"");
									
									alReportInner.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(salaryMap.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0"))));
									alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(salaryMap.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					    		
		//						}
							}
							alReportInner.add(salaryMap.get("GROSS"));
							alInnerExport.add(new DataStyle(uF.showData(salaryMap.get("GROSS"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				    		for(int i=0; i<alDeductions.size(); i++){
		//		    			double earningHead=uF.parseToDouble(salaryMap.get(alDeductions.get(i)))+uF.parseToDouble(totalSalaryHead.get(alDeductions.get(i)));
		//						totalSalaryHead.put(alDeductions.get(i), earningHead+"");
				    			
				    			double earningHead = uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(salaryMap.get(((ComparatorWeight)alDeductions.get(i)).getStrName())))) + uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()))));
				    			totalSalaryHead.put(((ComparatorWeight)alDeductions.get(i)).getStrName(), earningHead+"");
				    			
				    			//https://www.google.co.in/
				    			alReportInner.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(salaryMap.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0"))));
				    			
				    			alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(salaryMap.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				    		}
				    		alReportInner.add(salaryMap.get("NET"));
				    		alInnerExport.add(new DataStyle(uF.showData(salaryMap.get("NET"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				    		
				    		alReportList.add(alReportInner);
				    		
				    		reportListExport.add(alInnerExport);
							
						}
						
						
						List<String> alReportInner = new ArrayList<String>();
						alReportInner.add(" ");
						alReportInner.add("");
						alReportInner.add("");
						alReportInner.add("");
						alReportInner.add("");
						alReportInner.add("");
						alReportInner.add("");
						alReportInner.add("Total");				
						
						if(GrandtotalList.size()==0){
							GrandtotalList.put("1","");
							GrandtotalList.put("2","");
							GrandtotalList.put("3","");
							GrandtotalList.put("4","");
							GrandtotalList.put("5","");
							GrandtotalList.put("6","");
							GrandtotalList.put("7","");
							GrandtotalList.put("8","Grand Total");				
						}
						
						
						alInnerExport=new ArrayList<DataStyle>();
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("Total",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						
						boolean flag1=false;
						double grossEarning=0;
						double GrandgrossEarning=0;
//						int mpkey=8;
						int mpkey=9;
						for(int i=0; i<alEarnings.size(); i++) {
							ComparatorWeight comparatorWeight = alEarnings.get(i);
							
							if(flag1 != comparatorWeight.isVariable()) {
								flag1 = true;
//								alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),grossEarning),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//								alReportInner.add(""+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),grossEarning)+"");
//								GrandtotalList.put((mpkey++)+"", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),GrandgrossEarning));
							}
								grossEarning += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()))));
								alReportInner.add(""+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0")))+"");
								double a = uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0")))) + uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(GrandtotalList.get(mpkey+""))));
								GrandgrossEarning+=a;
								GrandtotalList.put(mpkey+"", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),a));
								alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
								mpkey++;
		//					}
			    		}
						alReportInner.add(""+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblGross)+"");
						alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblGross),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						
						if(GrandtotalList.size()==0) {
							GrandtotalList.put(mpkey+"",uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblGross));
						} else {
							double b = uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),  dblGross)) + uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(GrandtotalList.get(mpkey+""))));
							GrandtotalList.put(mpkey+"", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),b));
						}
						
						
			    		for(int i=0; i<alDeductions.size(); i++){
			    			alReportInner.add(""+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0")))+"");
			    			alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			    			
							double a = uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0")))) + uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(GrandtotalList.get(mpkey+1+i+""))));
							GrandtotalList.put(mpkey+1+i+"",uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),a));
			    		}
			    		alReportInner.add(""+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNet)+"");
			    		alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNet),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			    		if(GrandtotalList.size()==0) {
							GrandtotalList.put((mpkey+1+alDeductions.size())+"",uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNet));
						} else {
							double a = uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblNet)) + uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(GrandtotalList.get((mpkey+1+alDeductions.size())+""))));
							GrandtotalList.put((mpkey+1+alDeductions.size())+"",uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),a));
						}
			    		
			    		alReportList.add(alReportInner);
						
			    		reportListExport.add(alInnerExport);
					}
					
					
					List<String> alReportInner = new ArrayList<String>();
					
					if(GrandtotalList.size()>0) {
						alInnerExport = new ArrayList<DataStyle>();
						for(int i=0;i<GrandtotalList.size();i++) {
							alReportInner.add(""+GrandtotalList.get(i+1+"")+"");
							alInnerExport.add(new DataStyle(GrandtotalList.get(i+1+""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						}
						
					}
					
					if (GrandtotalList.size() > 0) {
						alReportList.add(alReportInner);
						reportListExport.add(alInnerExport);
					}
					request.setAttribute("reportListExport", reportListExport);
					
					request.setAttribute("reportList", alReportList);
					request.setAttribute("alEarnings", alEarnings);
					request.setAttribute("alDeductions", alDeductions);
					request.setAttribute("hmSalaryDetails", hmSalaryDetails);
					request.setAttribute("hmPayPayroll", hmPayPayroll);
					request.setAttribute("hmEmpMap", hmEmpMap);
				}
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
			sbQuery.append("select * from payroll_history ph,employee_official_details eod where ph.emp_id=eod.emp_id and ph.emp_id > 0");
			
			if(getPaycycleDate()!=null && getPaycycleDate().equals("2")){
				sbQuery.append(" and paid_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '" + uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
			}else{
				sbQuery.append(" and paycycle_from = to_date('" + uF.getDateFormat(strD1, DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD')" +
				" and paycycle_to=to_date('" + uF.getDateFormat(strD2, DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD')" +
				" and paycycle="+uF.parseToInt(strPC));
			}
			
			if(getF_level()!=null && getF_level().toString().trim().length()>0){
	            sbQuery.append(" and ph.grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	        }
	        if(getF_department()!=null && getF_department().toString().trim().length()>0){
	            sbQuery.append(" and ph.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
	        if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
	        if(getF_service()!=null && getF_service().toString().trim().length()>0){
	            sbQuery.append(" and (");
	            for(int i=0; i<getF_service().length; i++){
	            	sbQuery.append(" ph.service_id like '%,"+getF_service()[i]+",%'");
	                if(i<getF_service().length-1){
	                    sbQuery.append(" OR "); 
	                }
	            }
	            sbQuery.append(" ) ");
	        } 
	        
	        if(getF_strWLocation()!=null && getF_strWLocation().toString().trim().length()>0){
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
//			System.out.println("pst 0 ====>"+pst);     
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
			sbQuery.append("select distinct(pg.emp_id) as emp_id from payroll_generation pg, employee_official_details eod where eod.emp_id = pg.emp_id and pg.is_paid=true ");
			if(getPaycycleDate()!=null && getPaycycleDate().equals("2")){
				sbQuery.append(" and paid_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '" + uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
			}else{
				sbQuery.append(" and paid_from = to_date('" + uF.getDateFormat(strD1, DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD')" +
				" and paid_to=to_date('" + uF.getDateFormat(strD2, DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD')" +
				" and paycycle="+uF.parseToInt(strPC));
			}
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
//			System.out.println("pst 1 ====>"+pst);
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
	
	
//public String viewDepartmentWiseReport(UtilityFunctions uF){
//		
//		Connection con = null;
//		PreparedStatement pst=null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//
//		try {
//			
//			con = db.makeConnection(con);
//			
//			Map<String, String> hmEmpJoiningDate=CF.getEmpJoiningDateMap(con, uF);
//			Map<String,String> hmEmpMap = CF.getEmpNameMap(con,null,null);
//			Map<String,String> hmSalaryDetails = CF.getSalaryHeadsMap(con);
//			Map<String, String> hmEmpCode =CF.getEmpCodeMap(con);
//			Map<String, String> hmEmpDepartment =CF.getEmpDepartmentMap(con);			
//			Map<String, String> hmDept =CF.getDeptMap(con);
//			
//			Map<String, String> hmSalaryWeight = new HashMap<String, String>();
//			Map<String, Boolean> hmSalaryVariable = new HashMap<String, Boolean>();
//
//			pst = con.prepareStatement("select salary_head_id, weight,is_variable from salary_details order by level_id,weight");
//			rs = pst.executeQuery();
//			while(rs.next()){
//				if(hmSalaryWeight.get(rs.getString("salary_head_id"))==null){
//				hmSalaryWeight.put(rs.getString("salary_head_id"), rs.getString("weight"));
//				hmSalaryVariable.put(rs.getString("salary_head_id"),uF.parseToBoolean(rs.getString("is_variable")));
//
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			
//			
//			pst=con.prepareStatement("select org_id,org_name from org_details");
//			Map<String, String> hmOrg=new HashMap<String, String>();
//			rs=pst.executeQuery();
//			while(rs.next()){
//				hmOrg.put(rs.getString("org_id"), rs.getString("org_name"));
//			}
//			rs.close();
//			pst.close();
//			
//		
//			
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from payroll_generation pg, employee_official_details eod where eod.emp_id = pg.emp_id and pg.is_paid=true ");
//			
//			if(getPaycycleDate()!=null && getPaycycleDate().equals("2")){
//				sbQuery.append(" and paid_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '" + uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
//			}else{
//				sbQuery.append(" and paycycle="+uF.parseToInt(strPC));
//			}
//			
//			if(uF.parseToInt(getF_org())>0){
//				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
//			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
//					sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//			}
//			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
//	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
//				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//			}
//            if(getF_department()!=null && getF_department().length>0){
//                sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
//            }
//            if(getF_level()!=null && getF_level().length>0){
//                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
//            }
//            if(getF_service()!=null && getF_service().length>0){
//				sbQuery.append(" and pg.service_id in ("+StringUtils.join(getF_service(), ",")+") ");
//			}
//			sbQuery.append(" order by eod.depart_id,pg.emp_id, earning_deduction desc, salary_head_id");
//			pst = con.prepareStatement(sbQuery.toString());
//			
//			
////			System.out.println("pst==="+pst);
//			
//			rs = pst.executeQuery();
//			
////			List<String> alEarnings = new ArrayList<String>();
////			List<String> alDeductions = new ArrayList<String>();
//			
//			List<String> alEarningss = new ArrayList<String>();
//			List<String> alDeductionss = new ArrayList<String>();
//			
//			List<ComparatorWeight> alEarnings = new ArrayList<ComparatorWeight>();
//			List<ComparatorWeight> alDeductions = new ArrayList<ComparatorWeight>();
//			
//			Map<String,Map<String,String>> hmPayPayroll = new LinkedHashMap<String,Map<String,String>>();
//			Map<String, String> hmPresentDays=new HashMap<String, String>();
//			Map<String,List<String>> deptEmpMap=new LinkedHashMap<String,List<String>>();
//			Map<String,Map<String,String>> empSalaryMap=new HashMap<String,Map<String,String>>();
//			
//			while(rs.next()){
//				
//				List<String>empList=deptEmpMap.get(rs.getString("depart_id"));
//				if(empList==null)empList=new ArrayList<String>();
//				if(!empList.contains(rs.getString("emp_id"))){
//					empList.add(rs.getString("emp_id"));
//				}
//				
//				deptEmpMap.put(rs.getString("depart_id"),empList);
//				
//				Map<String,String> salaryMap=empSalaryMap.get(rs.getString("emp_id"));
//				if(salaryMap==null)salaryMap=new HashMap<String,String>();
//				salaryMap.put(rs.getString("salary_head_id"), rs.getString("amount"));
//				
//				
//				
//				if("E".equalsIgnoreCase(rs.getString("earning_deduction"))){
//					double dblAmount = rs.getDouble("amount");
//					double dblGross = uF.parseToDouble(salaryMap.get("GROSS"));
//					dblGross += dblAmount;
//					salaryMap.put("GROSS",  uF.formatIntoTwoDecimal((dblGross)));
//					
//					double dblNet = uF.parseToDouble(salaryMap.get("NET"));
//					dblNet+=dblAmount;
//					salaryMap.put("NET",  uF.formatIntoTwoDecimal((dblNet)));
//				}else{
//					double dblAmount = rs.getDouble("amount");
//					double dblNet = uF.parseToDouble(salaryMap.get("NET"));
//					dblNet-=dblAmount;
//					salaryMap.put("NET",  uF.formatIntoTwoDecimal((dblNet)));
//					
//				}
//				empSalaryMap.put(rs.getString("emp_id"),salaryMap);
//				
//				
//				if("E".equalsIgnoreCase(rs.getString("earning_deduction")) && !alEarningss.contains(rs.getString("salary_head_id"))){
//					//alEarnings.add(rs.getString("salary_head_id"));
//					
//					alEarningss.add(rs.getString("salary_head_id"));
//					alEarnings.add(new ComparatorWeight(rs.getString("salary_head_id"),uF.parseToInt(hmSalaryWeight.get(rs.getString("salary_head_id"))),hmSalaryVariable.get(rs.getString("salary_head_id"))));
//					
//				}else if("D".equalsIgnoreCase(rs.getString("earning_deduction")) && !alDeductionss.contains(rs.getString("salary_head_id"))){
////					alDeductions.add(rs.getString("salary_head_id"));
//					alDeductionss.add(rs.getString("salary_head_id"));
//					alDeductions.add(new ComparatorWeight(rs.getString("salary_head_id"),uF.parseToInt(hmSalaryWeight.get(rs.getString("salary_head_id"))),hmSalaryVariable.get(rs.getString("salary_head_id"))));
//				}
//
//				if(!hmPresentDays.containsKey(rs.getString("emp_id"))){
//					hmPresentDays.put(rs.getString("emp_id"), rs.getString("present_days"));
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			
////			System.out.println("alEarnings==="+alEarnings);
//			Collections.sort(alEarnings);
////			System.out.println("alEarnings==="+alEarnings);
//
//			
//			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
//			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
//			
//			alInnerExport.add(new DataStyle(uF.showData(hmOrg.get(getF_org()), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			if(getPaycycleDate()!=null && getPaycycleDate().equals("2")){
//				alInnerExport.add(new DataStyle("Departmentwise Report from "+getStrStartDate()+" - "+getStrEndDate(),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			}else{
//				alInnerExport.add(new DataStyle("Departmentwise Report from "+strD1+" - "+strD2,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			}
//			String curr_date=uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
//			alInnerExport.add(new DataStyle("Date- "+curr_date,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			
//		   	alInnerExport.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//		   	alInnerExport.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//		   	alInnerExport.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//		   	alInnerExport.add(new DataStyle("Joining Date",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 
//		   	alInnerExport.add(new DataStyle("Present Days",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 
//			alInnerExport.add(new DataStyle("Department",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			
//			boolean flag=false; 
////			double grossEarning=0;
//			for (int ii=0; ii<alEarnings.size(); ii++){
//				ComparatorWeight comparatorWeight=alEarnings.get(ii);
//				
//				if(flag!=comparatorWeight.isVariable()){
//					flag=true;
//					alInnerExport.add(new DataStyle("Gross Earning",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				}
////				alInnerExport.add(new DataStyle(((String)hmSalaryDetails.get((String)alEarnings.get(ii)))+"(+)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(((String)hmSalaryDetails.get(((ComparatorWeight)alEarnings.get(ii)).getStrName()))+"(+)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
////				}
//				
//			}
//			alInnerExport.add(new DataStyle("Gross",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			for (int ii=0; ii<alDeductions.size(); ii++){
////				alInnerExport.add(new DataStyle(((String)hmSalaryDetails.get((String)alDeductions.get(ii)))+"(-)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(((String)hmSalaryDetails.get(((ComparatorWeight)alDeductions.get(ii)).getStrName()))+"(-)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//			}
//			alInnerExport.add(new DataStyle("Net",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			
//			reportListExport.add(alInnerExport); 
//			
//			
//			Set set = deptEmpMap.keySet();
//			
//			Iterator it = set.iterator();
//			List<List<String>> alReportList = new ArrayList<List<String>>();
//			Map<String,String> GrandtotalList=new HashMap<String,String>();
//			int count=0;
//			while(it.hasNext()){
//				
//				String deptId = (String)it.next();
//				
//				List<String>empList=deptEmpMap.get(deptId);
//				double dblNet=0;
//				double dblGross=0;
//				Map<String,String> totalSalaryHead=new HashMap<String,String>();
//				
//				for(String emp:empList){
//					count++;
//					Map<String,String> salaryMap=empSalaryMap.get(emp);
//					List<String> alReportInner = new ArrayList<String>();
//					alReportInner.add(""+count);
//					alReportInner.add(hmEmpCode.get(emp));
//					alReportInner.add(hmEmpMap.get(emp));
//					alReportInner.add(uF.showData(hmEmpJoiningDate.get(emp),""));
//					alReportInner.add(uF.showData(hmPresentDays.get(emp),""));
//					alReportInner.add(uF.showData( hmDept.get(hmEmpDepartment.get(emp)),""));	
//					/*alReportInner.add(salaryMap.get("NET"));
//					alReportInner.add(salaryMap.get("GROSS"));*/
//					
//					dblNet+=uF.parseToDouble(salaryMap.get("NET"));
//					dblGross+=uF.parseToDouble(salaryMap.get("GROSS"));
//					
//					
//					alInnerExport=new ArrayList<DataStyle>();
//					alInnerExport.add(new DataStyle(uF.showData(""+count, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//					alInnerExport.add(new DataStyle(uF.showData(hmEmpCode.get(emp), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
//					alInnerExport.add(new DataStyle(uF.showData(hmEmpMap.get(emp), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//					alInnerExport.add(new DataStyle(uF.showData(hmEmpJoiningDate.get(emp), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//					alInnerExport.add(new DataStyle(uF.showData(hmPresentDays.get(emp), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
//					alInnerExport.add(new DataStyle(uF.showData(hmDept.get(hmEmpDepartment.get(emp)), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//					boolean flag1=false;
//					double grossEarning=0;
//					for(int i=0; i<alEarnings.size(); i++){
////						double earningHead=uF.parseToDouble(salaryMap.get(alEarnings.get(i)))+uF.parseToDouble(totalSalaryHead.get(alEarnings.get(i)));
////						totalSalaryHead.put(alEarnings.get(i), earningHead+"");
//						ComparatorWeight comparatorWeight=alEarnings.get(i);
//						
//						if(flag1!=comparatorWeight.isVariable()){
//							flag1=true;
//							alInnerExport.add(new DataStyle(uF.formatIntoTwoDecimal(grossEarning),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//							alReportInner.add(uF.formatIntoTwoDecimal(grossEarning));
//
//						}
//							grossEarning+=uF.parseToDouble(salaryMap.get(((ComparatorWeight)alEarnings.get(i)).getStrName()));
////						}else{
//							double earningHead=uF.parseToDouble(salaryMap.get(((ComparatorWeight)alEarnings.get(i)).getStrName()))+uF.parseToDouble(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()));
//							totalSalaryHead.put(((ComparatorWeight)alEarnings.get(i)).getStrName(), earningHead+"");
//							
//							alReportInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(uF.showData(salaryMap.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0"))));
//							alInnerExport.add(new DataStyle(uF.formatIntoTwoDecimal(uF.parseToDouble(uF.showData(salaryMap.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			    		
////						}
//						}
//					alReportInner.add(salaryMap.get("GROSS"));
//					alInnerExport.add(new DataStyle(uF.showData(salaryMap.get("GROSS"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//		    		for(int i=0; i<alDeductions.size(); i++){
////		    			double earningHead=uF.parseToDouble(salaryMap.get(alDeductions.get(i)))+uF.parseToDouble(totalSalaryHead.get(alDeductions.get(i)));
////						totalSalaryHead.put(alDeductions.get(i), earningHead+"");
//		    			
//		    			double earningHead=uF.parseToDouble(salaryMap.get(((ComparatorWeight)alDeductions.get(i)).getStrName()))+uF.parseToDouble(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()));
//		    			totalSalaryHead.put(((ComparatorWeight)alDeductions.get(i)).getStrName(), earningHead+"");
//		    			
//		    			https://www.google.co.in/
//		    			alReportInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(uF.showData(salaryMap.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0"))));
//		    			
//		    			alInnerExport.add(new DataStyle(uF.formatIntoTwoDecimal(uF.parseToDouble(uF.showData(salaryMap.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//		    		}
//		    		alReportInner.add(salaryMap.get("NET"));
//		    		alInnerExport.add(new DataStyle(uF.showData(salaryMap.get("NET"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//		    		
//		    		alReportList.add(alReportInner);
//		    		
//		    		reportListExport.add(alInnerExport);
//					
//				}
//				
//				
//				List<String> alReportInner = new ArrayList<String>();
//				alReportInner.add(" ");
//				alReportInner.add("");
//				alReportInner.add("");
//				alReportInner.add("");
//				alReportInner.add("");
//				alReportInner.add("Total");				
//				
//				if(GrandtotalList.size()==0){
//					GrandtotalList.put("1","");
//					GrandtotalList.put("2","");
//					GrandtotalList.put("3","");
//					GrandtotalList.put("4","");
//					GrandtotalList.put("5","");
//					GrandtotalList.put("6","Grand Total");				
//				}
//				
//				
//				alInnerExport=new ArrayList<DataStyle>();
//				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
//				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
//				alInnerExport.add(new DataStyle("Total",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				boolean flag1=false;
//				double grossEarning=0;
//				double GrandgrossEarning=0;
//				int mpkey=7;
//				for(int i=0; i<alEarnings.size(); i++){
//					ComparatorWeight comparatorWeight=alEarnings.get(i);
//					
//					if(flag1!=comparatorWeight.isVariable()){
//						flag1=true;
//						alInnerExport.add(new DataStyle(uF.formatIntoTwoDecimal(grossEarning),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//						alReportInner.add(""+uF.formatIntoTwoDecimal(grossEarning)+"");
//						GrandtotalList.put((mpkey++)+"", uF.formatIntoTwoDecimal(GrandgrossEarning));
//					}
//						grossEarning+=uF.parseToDouble(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()));
//						alReportInner.add(""+uF.formatIntoTwoDecimal(uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0")))+"");
//						double a=uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0"))+uF.parseToDouble(GrandtotalList.get(mpkey+""));
//						GrandgrossEarning+=a;
//						GrandtotalList.put(mpkey+"", uF.formatIntoTwoDecimal(a));
//						alInnerExport.add(new DataStyle(uF.formatIntoTwoDecimal(uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//						mpkey++;
////					}
//					
//	    		}
//				alReportInner.add(""+uF.formatIntoTwoDecimal(dblGross)+"");
//				alInnerExport.add(new DataStyle(uF.formatIntoTwoDecimal(dblGross),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				if(GrandtotalList.size()==0){
//					GrandtotalList.put(mpkey+"",uF.formatIntoTwoDecimal(dblGross));
//				}else{
//					double b=dblGross+uF.parseToDouble(GrandtotalList.get(mpkey+""));
//					GrandtotalList.put(mpkey+"", uF.formatIntoTwoDecimal(b));
//				}
//				
//				
//	    		for(int i=0; i<alDeductions.size(); i++){
//	    			alReportInner.add(""+uF.formatIntoTwoDecimal(uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0")))+"");
//	    			alInnerExport.add(new DataStyle(uF.formatIntoTwoDecimal(uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//	    			
//					double a=uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0"))+uF.parseToDouble(GrandtotalList.get(mpkey+1+i+""));
//					GrandtotalList.put(mpkey+1+i+"",uF.formatIntoTwoDecimal(a));
//	    		}
//	    		alReportInner.add(""+uF.formatIntoTwoDecimal(dblNet)+"");
//	    		alInnerExport.add(new DataStyle(uF.formatIntoTwoDecimal(dblNet),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//	    		if(GrandtotalList.size()==0){
//					GrandtotalList.put((mpkey+1+alDeductions.size())+"",uF.formatIntoTwoDecimal(dblNet));
//				}else{
//					double a=dblNet+uF.parseToDouble(GrandtotalList.get((mpkey+1+alDeductions.size())+""));
//					GrandtotalList.put((mpkey+1+alDeductions.size())+"",uF.formatIntoTwoDecimal(a));
//					
//					
//				}
//	    		
//	    		alReportList.add(alReportInner);
//				
//	    		reportListExport.add(alInnerExport);
//				
//				
//			}
//			
//			
//			List<String> alReportInner = new ArrayList<String>();
//			
//			if(GrandtotalList.size()>0){
//				alInnerExport=new ArrayList<DataStyle>();
//				for(int i=0;i<GrandtotalList.size();i++){
//					alReportInner.add(""+GrandtotalList.get(i+1+"")+"");
//					alInnerExport.add(new DataStyle(GrandtotalList.get(i+1+""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				}
//				
//			}
//			
//			if (GrandtotalList.size() > 0) {
//				alReportList.add(alReportInner);
//				reportListExport.add(alInnerExport);
//			}
//			request.setAttribute("reportListExport",reportListExport);
//			
//			
//			request.setAttribute("reportList", alReportList);
//			request.setAttribute("alEarnings", alEarnings);
//			request.setAttribute("alDeductions", alDeductions);
//			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
//			request.setAttribute("hmPayPayroll", hmPayPayroll);
//			request.setAttribute("hmEmpMap", hmEmpMap);
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


	public String getApprovePC() {
		return approvePC;
	}


	public void setApprovePC(String approvePC) {
		this.approvePC = approvePC;
	}


	public String getStrMonth() {
		return strMonth;
	}


	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
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


	public String getPaycycleDate() {
		return paycycleDate;
	}


	public void setPaycycleDate(String paycycleDate) {
		this.paycycleDate = paycycleDate;
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

	public String getStrStartDate() {
		return strStartDate;
	}

	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}

	public String getStrEndDate() {
		return strEndDate;
	}

	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
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
