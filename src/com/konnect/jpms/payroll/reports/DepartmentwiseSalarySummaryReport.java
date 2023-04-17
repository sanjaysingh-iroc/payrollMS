package com.konnect.jpms.payroll.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DepartmentwiseSalarySummaryReport  extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(DepartmentwiseSalarySummaryReport.class);
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strSalaryhead;
	String f_salaryheadtype;
	String strMonth;
	
	String[] f_salaryhead;
	String financialYear;
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	
	List<FillSalaryHeads> salaryHeadList;
	List<FillOrganisation> orgList;	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;

	List<FillFinancialYears> financialYearList; 
	List<FillMonth> monthList;
	
	String exportType; 
	
	public String execute() throws Exception {
		
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute(TITLE, "Departmentwise Salary Summary Report");
		request.setAttribute(PAGE, "/jsp/payroll/reports/DepartmentwiseSalarySummaryReport.jsp");
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));

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
		if(getStrSalaryhead() != null && !getStrSalaryhead().equals("")) {
			setF_salaryhead(getStrSalaryhead().split(","));
		} else {
			setF_salaryhead(null);
		}
		
		if(getStrMonth()==null){
			setStrMonth("1");
		}
		
		
		viewReport(uF, getF_salaryheadtype());
		/*if(getExportType()!= null && getExportType().equals("pdf")){
			getReconciliationPdfReport(uF);
		}*/

		return loadReport(uF,getF_salaryheadtype());

	}
   
	
	public String loadReport(UtilityFunctions uF,String headType) {
		
		salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsByOrgWithoutCTC(headType, getF_org());
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillMonth();
		
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
				
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null) {
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
		
		alFilter.add("SALARYHEADS");
		if(getF_salaryhead()!=null)  {
			String strSalaryHeads="";
			int k=0;
			for(int i=0;salaryHeadList!=null && i<salaryHeadList.size();i++){
				if(getF_salaryhead().equals(salaryHeadList.get(i).getSalaryHeadId())) {
					if(k==0) {
						strSalaryHeads=salaryHeadList.get(i).getSalaryHeadName();
					} else {
						strSalaryHeads+=", "+salaryHeadList.get(i).getSalaryHeadName();
					}
					k++;
				}
			}
			if(strSalaryHeads!=null && !strSalaryHeads.equals("")) {
				hmFilter.put("SALARYHEADS", strSalaryHeads);
			} else {
				hmFilter.put("SALARYHEADS", "");
			}
		} else {
			hmFilter.put("SALARYHEADS", "");
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
		
		alFilter.add("MONTH");
		int nselectedMonth = uF.parseToInt(getStrMonth());
		String strMonth = uF.getMonth(nselectedMonth);
		hmFilter.put("MONTH", strMonth);
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	public void viewReport(UtilityFunctions uF,String headType ) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
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
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
			String strMonth=null;
			String strYear=null;			
			Map<String, Map<String, String>> hmDeptwiseAmt = new HashMap<String, Map<String, String>>();			
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select month,year,salary_head_id,earning_deduction,eod.depart_id,sum(amount)as amount from employee_personal_details epd, " +
					"employee_official_details eod, payroll_generation pg " +
					"where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id " +
					"and month=? and financial_year_from_date=? and financial_year_to_date=? ");
				if(getF_salaryhead()!=null && getF_salaryhead().length > 0) {			
					sbQuery.append("and salary_head_id  in (0");
				}
				for(int i=0;getF_salaryhead()!=null && i<getF_salaryhead().length;i++) {
					sbQuery.append(","+getF_salaryhead()[i]); 
//					System.out.println("salaryhead====>"+getF_salaryhead()[i]);
				}
				if(getF_salaryhead()!=null && getF_salaryhead().length > 0) {
					sbQuery.append(")");
				}
				
				if(headType != null && (headType.equalsIgnoreCase("E") || headType.equalsIgnoreCase("D"))) {
					sbQuery.append(" and earning_deduction= '"+headType+"' ");
				}
//				sbQuery.append(" and pg.emp_id in ("+strPrevMonthEmpIds+") ");
				sbQuery.append(" group by month,year,eod.depart_id,salary_head_id,earning_deduction order by eod.depart_id");
				pst = con.prepareStatement(sbQuery.toString());
				//pst.setInt(1, uF.parseToInt(prevMonth));
				pst.setInt(1, uF.parseToInt(getStrMonth()));
				pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
				pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst  =======>"+pst);
				rs = pst.executeQuery();
				Set<String> salaryHeadESet = new HashSet<String>();
				Set<String> salaryHeadDSet = new HashSet<String>();
				if (rs.next()){
					request.setAttribute("selectedMonth", rs.getString("month"));
					request.setAttribute("selectedYear", rs.getString("year"));
				}
//				double headTotal = 0.00d;
				Map<String,String> hmHeadTotalMap = new HashMap<String,String>();
				while(rs.next()) {
					
					if(rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("E")) {
						salaryHeadESet.add(rs.getString("salary_head_id"));
					}
					if(rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("D")) {
						salaryHeadDSet.add(rs.getString("salary_head_id"));
					}
					
					Map<String, String> hmSalaryHeadAmt = hmDeptwiseAmt.get(rs.getString("depart_id"));
					if(hmSalaryHeadAmt == null) hmSalaryHeadAmt = new HashMap<String, String>();
					double dblAmount = rs.getDouble("amount");
					//System.out.println("dblAmount====>"+dblAmount);
					double headTotal = uF.parseToDouble(hmHeadTotalMap.get(rs.getString("salary_head_id")));
					headTotal += dblAmount;
					//System.out.println("headTotal====>"+headTotal);
					hmHeadTotalMap.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimal(headTotal));
					hmSalaryHeadAmt.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimal(dblAmount));
					hmDeptwiseAmt.put(rs.getString("depart_id"), hmSalaryHeadAmt);
				}

				rs.close();
				pst.close();
//				System.out.println("hmHeadTotalMap======>"+hmHeadTotalMap);
//				System.out.println("hmDeptwiseAmt ===>> " + hmDeptwiseAmt);
//			    System.out.println("salaryHeadESet ===>> " + salaryHeadESet);
//				System.out.println("salaryHeadDSet ===>> " + salaryHeadDSet);

					
			Map<String, String> hmDept =CF.getDeptMap(con); 
			Map hmSalaryDetails = CF.getSalaryHeadsMap(con);
			
			Iterator<String> it = hmDeptwiseAmt.keySet().iterator();
			int count=0;
			double dblDepartAmountTotal = 0.0d;
			List<List<String>> reportList = new ArrayList<List<String>>();
			while(it.hasNext()){
				String departId = (String)it.next();
				double dblDepartTotAmount = 0.0d;
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add(uF.showData(""+hmDept.get(departId),""));
				Map<String, String> hmSalaryHeadAmt = hmDeptwiseAmt.get(departId);
				Iterator<String> itSalHeadE = salaryHeadESet.iterator();
				while(itSalHeadE.hasNext()) {
					String strSalHeadE = itSalHeadE.next();
					dblDepartTotAmount = dblDepartTotAmount + uF.parseToDouble(hmSalaryHeadAmt.get(strSalHeadE));
					alInner.add(uF.showData(hmSalaryHeadAmt.get(strSalHeadE), "0"));
				}
				Iterator<String> itSalHeadD = salaryHeadDSet.iterator();
				while(itSalHeadD.hasNext()) {
					String strSalHeadD = itSalHeadD.next();
					dblDepartTotAmount = dblDepartTotAmount + uF.parseToDouble(hmSalaryHeadAmt.get(strSalHeadD));
					alInner.add(uF.showData(hmSalaryHeadAmt.get(strSalHeadD), "0"));
				}
				alInner.add(uF.formatIntoOneDecimal(dblDepartTotAmount));
				reportList.add(alInner);
			}
			
			
		
				
			//double dblDepartAmountTotal = 0.0d;
			//List<List<String>> reportList = new ArrayList<List<String>>();
			List<String>headTotList = new ArrayList<String>();
			double dblHeadTotAmount = 0.0d;
			headTotList.add("");
			headTotList.add("Total");
			Iterator<String> itSalHeadE = salaryHeadESet.iterator();
			while(itSalHeadE.hasNext()) {
				String strSalHeadE = itSalHeadE.next();
				dblHeadTotAmount = dblHeadTotAmount + uF.parseToDouble(hmHeadTotalMap.get(strSalHeadE));
				//System.out.println(strSalHeadE + " ======>> "+ uF.parseToDouble(hmHeadTotalMap.get(strSalHeadE)));
				headTotList.add(uF.showData(hmHeadTotalMap.get(strSalHeadE), "0"));
			}
			Iterator<String> itSalHeadD = salaryHeadDSet.iterator();
			while(itSalHeadD.hasNext()) {
				String strSalHeadD = itSalHeadD.next();
				dblHeadTotAmount = dblHeadTotAmount + uF.parseToDouble(hmHeadTotalMap.get(strSalHeadD));
				//System.out.println(strSalHeadD + " ======>> "+ uF.parseToDouble(hmHeadTotalMap.get(strSalHeadD)));
				headTotList.add(uF.showData(hmHeadTotalMap.get(strSalHeadD), "0"));
			}
			headTotList.add(uF.formatIntoOneDecimal(dblHeadTotAmount));
			reportList.add(headTotList);
			
			//System.out.println("reportList ===>> " + reportList);

			request.setAttribute("reportList", reportList);
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			request.setAttribute("salaryHeadESet", salaryHeadESet);
			request.setAttribute("salaryHeadDSet", salaryHeadDSet);
			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
			request.setAttribute("hmDept", hmDept);
						
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;


	}
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	

	public String[] getF_salaryhead() {
		return f_salaryhead;
	}
	

	public void setF_salaryhead(String[] f_salaryhead) {
		this.f_salaryhead = f_salaryhead;
	}

	public String getF_salaryheadtype() {
		return f_salaryheadtype;
	}
	

	public void setF_salaryheadtype(String f_salaryheadtype) {
		this.f_salaryheadtype = f_salaryheadtype;
	}


	public String getFinancialYear() {
		return financialYear;
	}


	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}


	public String getStrMonth() {
		return strMonth;
	}


	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
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


	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}


	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
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

	public String getStrSalaryhead() {
		return strSalaryhead;
	}

	public void setStrSalaryhead(String strSalaryhead) {
		this.strSalaryhead = strSalaryhead;
	}
	
}
