package com.konnect.jpms.reports.factory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayMode;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class OvertimeMuster extends ActionSupport implements ServletRequestAware, IStatements, ServletResponseAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(OvertimeMuster.class);

	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	
	String financialYear;
	String strMonth;

	String strLocation;
	String strDepartment;
	String strSbu;
	
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	
	List<FillOrganisation> orgList;
	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	
	List<FillFinancialYears> financialYearList; 
	List<FillMonth> monthList;
	List<FillServices> serviceList;
	
	String exportType;

	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute(PAGE, POvertimeMuster);
		request.setAttribute(TITLE, TOvertimeMuster);
		
		UtilityFunctions uF = new UtilityFunctions();
		if(getF_org()==null) {
			setF_org((String)session.getAttribute(ORGID));
		}
		if(getStrMonth()==null || getStrMonth().trim().equals("")) {
			setStrMonth("1");
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
		
		System.out.println("getFinancialYear() ===>> " + getFinancialYear());
		viewOvertimeMuster(uF);
		return loadOvertimeMuster(uF);

	}

	
	public String loadOvertimeMuster(UtilityFunctions uF) {
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
		
		alFilter.add("FINANCIALYEAR");
		String[] strFinancialYears = null;
		if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equals("null")) {
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
 
	public void viewOvertimeMuster(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			Map<String, String> hmEmpDept = CF.getEmpDepartmentMap(con);
			Map<String, String> hmDeptMap = CF.getDeptMap(con);
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);
			if(hmEmpWlocationMap == null) hmEmpWlocationMap = new HashMap<String, String>();
			
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			System.out.println("getFinancialYear() ===>> " + getFinancialYear());
			if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equals("null")) {
				
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			} else {
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			}
			
			List<FillPayMode> alPaymentMode = new FillPayMode().fillPaymentMode();
			Map<String, String> hmPayMode = new HashMap<String, String>();
			for(int i = 0; alPaymentMode!=null && i < alPaymentMode.size();i++){
				hmPayMode.put(alPaymentMode.get(i).getPayModeId(), alPaymentMode.get(i).getPayModeName());
			}
			
						
			Map<String, String> hmOrg =  CF.getOrgName(con);
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select earning_deduction, pg.emp_id, month, year, amount,eod.org_id,pg.paid_date,eod.payment_mode,paid_from,paid_to,paycycle" +
					" from employee_personal_details epd, employee_official_details eod, payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(getF_department() != null && getF_department().length>0) {
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
			sbQuery.append(" and pg.month=? and pg.financial_year_from_date=? and pg.financial_year_to_date=? and pg.is_paid=true " +
					"and pg.salary_head_id = ? and pg.amount >0 order by eod.emp_id,eod.org_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, OVER_TIME);
			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmEmpOvertime = new LinkedHashMap<String, String>();
			Map<String, String> hmEmpOrgName = new HashMap<String, String>();
			Map<String, String> hmEmpPaidDate = new HashMap<String, String>();
			Map<String, String> hmEmpPayMode = new HashMap<String, String>();
			String startDate = null;
			String endDate = null;
			String paycycle = null;
			Set<String> empSetList = new HashSet<String>();
			while(rs.next()){
				double dblAmount = rs.getDouble("amount");
				hmEmpOvertime.put(rs.getString("emp_id"), uF.formatIntoTwoDecimal(dblAmount));
				hmEmpOrgName.put(rs.getString("emp_id"), uF.showData(hmOrg.get(rs.getString("org_id")), ""));
				hmEmpPaidDate.put(rs.getString("emp_id"), uF.getDateFormat(rs.getString("paid_date"), DBDATE, CF.getStrReportDateFormat()));
				hmEmpPayMode.put(rs.getString("emp_id"), uF.showData(hmPayMode.get(rs.getString("payment_mode")), ""));
				
				if(paycycle == null){
					startDate = uF.getDateFormat(rs.getString("paid_from"), DBDATE, DATE_FORMAT);
					endDate = uF.getDateFormat(rs.getString("paid_to"), DBDATE, DATE_FORMAT);
					paycycle = rs.getString("paycycle");
				}
				empSetList.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbEmp = null;
			Iterator<String> it1 = empSetList.iterator();
			while(it1.hasNext()){
				String strEmp = it1.next();
				if(sbEmp == null){
					sbEmp = new StringBuilder();
					sbEmp.append(strEmp);
				} else {
					sbEmp.append(","+strEmp);
				}
			}
			Map<String,String> hmEmpOvertimeHours = new HashMap<String, String>();
			if(sbEmp != null && startDate !=null && endDate !=null && paycycle != null){
				pst = con.prepareStatement("select emp_id,approved_ot_hours from overtime_hours where paycle=? and to_date(paycycle_from::text,'yyyy-MM-dd')=? " +
						"and to_date(paycycle_to::text,'yyyy-MM-dd')=?");
				pst.setInt(1,uF.parseToInt(paycycle));
				pst.setDate(2, uF.getDateFormat(startDate, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(endDate, DATE_FORMAT));
				rs = pst.executeQuery();
				while(rs.next()){
					double dblAmount =uF.parseToDouble(hmEmpOvertimeHours.get(rs.getString("emp_id")));
					dblAmount += uF.parseToDouble(rs.getString("approved_ot_hours"));
					
					hmEmpOvertimeHours.put(rs.getString("emp_id"),""+dblAmount);
					
				}	
				rs.close();
				pst.close();	
			}
			
			
			String strMonthName = uF.getShortMonth(uF.parseToInt(getStrMonth()))+" ";
			String strFYName = uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yy")+"-"+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "yy");
			
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
			
			alInnerExport.add(new DataStyle("Overtime report of " + strMonthName + strFYName, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employee Code", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employee Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Organization", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Department", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Work Location", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Standard Work Hours/ Days", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Overtime Work Hours/ Days", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Total Overtime Work in Days", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Earning during the month", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Total", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Date on which overtime paid", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Payment Mode", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			
			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con);
			if(hmWorkLocation == null) hmWorkLocation = new HashMap<String, Map<String, String>>();
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			Iterator<String> it = hmEmpOvertime.keySet().iterator();
			int count=0;
			double dblNetPayTotal = 0;
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				dblNetPayTotal+=uF.parseToDouble((String)hmEmpOvertime.get(strEmpId));
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(uF.showData((String)hmEmpCode.get(strEmpId), ""));
				alInner.add(uF.showData((String)hmEmpName.get(strEmpId), ""));
				alInner.add(uF.showData(hmEmpOrgName.get(strEmpId), ""));
				alInner.add(uF.showData((String)hmDeptMap.get((String)hmEmpDept.get(strEmpId)), ""));
				alInner.add(uF.showData(hmWLocation.get(hmEmpWlocationMap.get(strEmpId)), ""));
				
				Map<String, String> hmWorkLInner = hmWorkLocation.get(hmEmpWlocationMap.get(strEmpId));
				if(hmWorkLInner == null) hmWorkLInner = new HashMap<String, String>();
				String locationstarttime = hmWorkLInner.get("WL_START_TIME");
				String locationendtime = hmWorkLInner.get("WL_END_TIME");
				Time t = uF.getTimeFormat(locationstarttime, DBTIME);
				long long_startTime = t.getTime();
				Time t1 = uF.getTimeFormat(locationendtime, DBTIME);
				long long_endTime = t1.getTime();
				double total_time = uF.parseToDouble(uF.getTimeDiffInHoursMins(long_startTime,long_endTime));
				alInner.add(""+total_time);
				
				alInner.add(uF.showData(hmEmpOvertimeHours.get(strEmpId), ""));
				
				String strTotalDays = "";
				if(uF.parseToDouble(hmEmpOvertimeHours.get(strEmpId)) > 0.0d){
					double dblTotalDays = uF.parseToDouble(hmEmpOvertimeHours.get(strEmpId)) / total_time;
					strTotalDays = uF.formatIntoTwoDecimalWithOutComma(dblTotalDays);
				}
				alInner.add(strTotalDays);
				alInner.add(uF.showData(hmEmpOvertime.get(strEmpId), "0"));
				alInner.add(uF.showData(hmEmpOvertime.get(strEmpId), "0"));
				alInner.add(uF.showData(hmEmpPaidDate.get(strEmpId), ""));				
				alInner.add(uF.showData(hmEmpPayMode.get(strEmpId), ""));
				
				reportList.add(alInner);
				
				alInnerExport.add(new DataStyle(uF.showData((String)hmEmpCode.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData((String)hmEmpName.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(hmEmpOrgName.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData((String)hmDeptMap.get((String)hmEmpDept.get(strEmpId)), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(hmWLocation.get(hmEmpWlocationMap.get(strEmpId)), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(""+total_time, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(hmEmpOvertimeHours.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(strTotalDays, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(hmEmpOvertime.get(strEmpId), "0"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(hmEmpOvertime.get(strEmpId), "0"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(hmEmpPaidDate.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(hmEmpPayMode.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
				
			}
			
			request.setAttribute("reportList", reportList);
			session.setAttribute("reportListExport", reportListExport);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	

	private HttpServletResponse response;
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;		
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
	
}
	

	

