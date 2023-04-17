package com.konnect.jpms.payroll.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
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

public class DepartmentEmpwiseReport extends  ActionSupport implements ServletRequestAware, IStatements { 

	/**
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	String strUserType = null;
	       
	CommonFunctions CF = null; 
	 
	String strD1 = null;
	String strD2 = null;
	String strPC = null;

	String financialYear; 
	String paycycle;
	String strMonth;
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
	
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	
	private static Logger log = Logger.getLogger(DepartmentwiseReport.class);
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			session = request.getSession();
			request.setAttribute(PAGE, "/jsp/payroll/reports/DepartmentEmpwiseReport.jsp");
			request.setAttribute(TITLE, "Departmentwise Report");
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if(CF==null)return LOGIN;
			
			strEmpId =(String) session.getAttribute(EMPID);
			strUserType =(String) session.getAttribute(USERTYPE);
			
			
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
		
			
			viewDepartmentWiseReport(uF);
			
			
			alMonthList = new FillMonth().fillMonth();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return loadPaySlips(uF);
	}
	
	
	public String viewDepartmentWiseReport(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		List<String> alInner = new ArrayList<String>();
		try {
			
			con = db.makeConnection(con);
			DecimalFormat df = new DecimalFormat("#.##"); 
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
			List<List<String>> reportList = new ArrayList<List<String>>();
			Map<String, Map<String, String>> hmEmpHistory = (Map<String, Map<String, String>>) request.getAttribute("hmEmpHistory");
			if(hmEmpHistory == null) hmEmpHistory = new HashMap<String, Map<String,String>>(); 
			
			Map<String, String> hmDepartment =CF.getDepartmentMap(con, null, null);
			if(hmDepartment == null) hmDepartment = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from department_info where org_id=?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			rs = pst.executeQuery();
			StringBuilder sbr = null;
			

			alInnerExport.add(new DataStyle("Department EmployeeWise Report",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Department",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Total Employee",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Gross Salary",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Averge",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			reportListExport.add(alInnerExport);
			
			while(rs.next()){
				if(sbr == null){
					sbr = new StringBuilder();
					sbr.append(rs.getString("dept_id"));
				} else {
					sbr.append(","+rs.getString("dept_id"));
				}
				
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery = new StringBuilder();
			
			if(sbr!=null && sbr.length()>0){
				
				sbQuery.append("select depart_id,emp_count,amount, (amount/emp_count) as averge from("+
											" select eod.depart_id,count(distinct(pg.emp_id)) as emp_count, sum(amount) as amount from payroll_generation pg," +
											" employee_official_details eod where pg.emp_id=eod.emp_id and pg.paid_from=? and pg.paid_to=? and" +
											" eod.depart_id in("+sbr.toString()+") and earning_deduction='E' ");
				

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
		                sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
						sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					}
		            
		            if(uF.parseToInt(getF_org())>0){
						sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
					}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
						sbQuery.append(" and eod.org_id in("+(String)session.getAttribute(ORG_ACCESS)+")");
					}
	            
	            sbQuery.append(" group by eod.depart_id) a");
	            
	            pst = con.prepareStatement(sbQuery.toString());
	            
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				 System.out.println("pst==== >"+pst);
				rs = pst.executeQuery();
				 System.out.println("pst==== >"+pst);
				int empTotal = 0;
				double totalAmount = 0;
				double totalAvg = 0;
				while(rs.next()){
					
					
					alInner = new ArrayList<String>();
					alInnerExport= new ArrayList<DataStyle>();
					
					int empCount = uF.parseToInt(rs.getString("emp_count"));
					empTotal +=empCount;
					
					double amount = uF.parseToDouble(rs.getString("amount"));
					totalAmount +=amount;
					
					double	totAvg = Double.valueOf(df.format(uF.parseToDouble(rs.getString("averge"))));
					totalAvg += Double.valueOf(df.format(totAvg));
					
					alInner.add(uF.showData(hmDepartment.get(rs.getString("depart_id")), ""));
					alInner.add(uF.showData(rs.getString("emp_count"),""));
					alInner.add(uF.showData(rs.getString("amount"), ""));
					alInner.add(Double.toString(totAvg));
					

					alInnerExport.add(new DataStyle(""+uF.showData(hmDepartment.get(rs.getString("depart_id")), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(""+uF.showData(rs.getString("emp_count"),""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(""+uF.showData(rs.getString("amount"), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(""+Double.toString(totAvg),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					
					reportList.add(alInner);
					reportListExport.add(alInnerExport);
					
				}
				rs.close();
				pst.close();
				
				alInner = new ArrayList<String>();
				alInnerExport= new ArrayList<DataStyle>();
				
				alInner.add("Total");
				alInner.add(uF.showData(Integer.toString(empTotal),""));
				alInner.add(uF.showData(Double.toString(totalAmount), ""));
				alInner.add(uF.showData(Double.toString(Double.valueOf(df.format(totalAvg))), ""));

				
				alInnerExport.add(new DataStyle("Total",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+uF.showData(Integer.toString(empTotal),""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+uF.showData(Double.toString(totalAmount), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+uF.showData(Double.toString(Double.valueOf(df.format(totalAvg))), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
		
			
				reportList.add(alInner);
				reportListExport.add(alInnerExport);
				
			}
			
			request.setAttribute("reportList",reportList);
			session.setAttribute("reportListExport",reportListExport);
				
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
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		
		
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


	public String getStrMonth() {
		return strMonth;
	}


	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
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


}
