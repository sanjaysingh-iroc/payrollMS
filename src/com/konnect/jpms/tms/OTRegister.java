package com.konnect.jpms.tms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.joda.time.LocalDate;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class OTRegister extends ActionSupport implements ServletRequestAware, IStatements {

	HttpSession session;
	String paycycle;
	String strUserType = null;
	String strSessionEmpId=null;
	String[] f_wLocation; 
	String[] f_department;  
	String[] f_service;
	
	String strSearch;
	String strDate;
	String strD1;
	String strD2;
	String payCycleNo;
	String empContractor;
	 
	List<FillPayCycles> paycycleList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillWLocation> wLocationList;

	List<FillOrganisation> organisationList;
	String f_org;
	CommonFunctions CF = null; 
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID); 
		
		request.setAttribute(PAGE, "/jsp/tms/OTRegister.jsp");
		request.setAttribute(TITLE, "Overtime Hours"); 
		
		UtilityFunctions uF = new UtilityFunctions();
		String action = (String)request.getParameter("action");
		String month = (String)request.getParameter("month");
		String empId = (String)request.getParameter("empId");
			try {
			
			/*boolean isView  = CF.getAccess(session, request, uF);
			if(!isView){
				request.setAttribute(PAGE, PAccessDenied);
				request.setAttribute(TITLE, TAccessDenied);
				return ACCESS_DENIED;
			}*/
			
			if(getF_org()==null){
				setF_org((String)session.getAttribute(ORGID));
			}
			
			Connection con = null;
			Database db = new Database();
			db.setRequest(request);
			
			try {
				
				con = db.makeConnection(con);
				String[] strPayCycleDates = null;

				if (getPaycycle() != null) {
					strPayCycleDates = getPaycycle().split("-");
					strD1 = strPayCycleDates[0];
					strD2 = strPayCycleDates[1];
					setPayCycleNo(strPayCycleDates[2]);
				} else {
					strPayCycleDates = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, getF_org());
					setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);

					strD1 = strPayCycleDates[0];
					strD2 = strPayCycleDates[1];
					setPayCycleNo(strPayCycleDates[2]);

				}
				viewOverTimeHours(con, uF);
			
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				db.closeConnection(con);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return loadOverTimeHours(uF);
	}
	
	public void viewOverTimeHours(Connection con, UtilityFunctions uF){
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpPanNo = CF.getEmpPANNoMap(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			List<String> alDates = new ArrayList<String>();
			String s = uF.getDateFormat(""+uF.getDateFormat(strD1, DATE_FORMAT), DBDATE, DBDATE);
			String e = uF.getDateFormat(""+uF.getDateFormat(strD2, DATE_FORMAT), DBDATE, DBDATE);
			LocalDate start = LocalDate.parse(s);
			LocalDate end = LocalDate.parse(e);
			while (!start.isAfter(end)) {
				alDates.add(uF.getDateFormat(""+start, DBDATE, DATE_FORMAT));
			    start = start.plusDays(1);
			}			
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery = new StringBuilder();
			
			List<OTRegisterDomain> oTList = new ArrayList<OTRegisterDomain>();
			
			//	pst = con.prepareStatement("select * from employee_personal_details epd ,overtime_hours ot where epd.emp_per_id = ot.emp_id and to_date(_date::text, 'YYYY-MM-DD') between ? and ? and emp_id in("+strEmpIds+") order by emp_id");
				sbQuery.append("select * from employee_personal_details epd ,employee_official_details eod,overtime_hours ot where epd.emp_per_id = ot.emp_id and eod.emp_id = ot.emp_id and eod.emp_id = epd.emp_per_id  and to_date(_date::text, 'YYYY-MM-DD') between ? and ? " +
						"and epd.is_alive = true and epd.employment_end_date is null ");
				
				if(empContractor != null){
					sbQuery.append(" and emp_contractor ="+empContractor );
				}
				
				if(strUserType!=null && (strUserType.equalsIgnoreCase(MANAGER))){
					sbQuery.append(" and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
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
	            
	            if(getF_wLocation()!=null && getF_wLocation().length>0){
	                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wLocation(), ",")+") ");
	            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            
	            if(uF.parseToInt(getF_org())>0){
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
	            if(getStrSearch()!=null && !getStrSearch().trim().equals("") && !getStrSearch().trim().equalsIgnoreCase("NULL")){
	            	if(flagMiddleName) {
						sbQuery.append(" and (upper(emp_fname)||' '||upper(emp_mname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%' or upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%')");
					} else {
						sbQuery.append(" and upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%'");
					}
 				}
	            
	            sbQuery.append(" order by ot.emp_id desc");
				
				pst = con.prepareStatement(sbQuery.toString());
				
			//	pst.setInt(1, Integer.parseInt(empContractor));
				pst.setDate(1, uF.getDateFormat((String)alDates.get(0), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat((String)alDates.get(alDates.size()-1), DATE_FORMAT));
				
//				System.out.println("Query OverTime---------------------------"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					OTRegisterDomain domain  = new OTRegisterDomain();
					domain.setoTEmp_id(rs.getString("emp_id"));
					domain.setoTHours(rs.getString("approved_ot_hours"));
					domain.setoTDate(rs.getString("_date"));
					domain.setoTGender(rs.getString("emp_gender"));
					domain.setoTMname(rs.getString("emp_mname"));
					oTList.add(domain);
				
				}
				rs.close();
				pst.close();
			
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
			List<String> alInner = new ArrayList<String>();
			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
			List alLegends = new ArrayList();

			Map<String, String> hmOTHours = new HashMap<String, String>();
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			
			alInnerExport.add(new DataStyle("OverTime Register",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employee Id",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Name of workman",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Father's / Husband's name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Sex",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Pan No",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Designation/ nature of employment",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Date on which overtime worked",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Total overtime worked",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Normal rates of wages ",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Overtime rate of wages",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Overtimes earnings",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Date on which overtime wages paid",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Remarks",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			reportListExport.add(alInnerExport);
			
			 for(int i=0;i<oTList.size();i++)
			 {
				 alInner = new ArrayList<String>();
				 alInnerExport = new ArrayList<DataStyle>();
				 alInner.add(CF.getEmpCodeByEmpId(con, oTList.get(i).getoTEmp_id()));
				 alInner.add((String)hmEmpName.get(oTList.get(i).getoTEmp_id()));
				 alInner.add(oTList.get(i).getoTMname());
				 alInner.add(uF.getGender(oTList.get(i).getoTGender()));
				 alInner.add(uF.showData((String)hmEmpPanNo.get(oTList.get(i).getoTEmp_id()),""));
				 alInner.add(hmEmpDesigMap.get(oTList.get(i).getoTEmp_id()));
				 alInner.add(oTList.get(i).getoTDate());
				 alInner.add(oTList.get(i).getoTHours());
				 alInner.add("NA");
				 alInner.add("NA");
				 alInner.add("NA");
				 alInner.add("NA");
				 alInner.add("NA");
				 
				 alInnerExport.add(new DataStyle(""+CF.getEmpCodeByEmpId(con, oTList.get(i).getoTEmp_id()),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				 alInnerExport.add(new DataStyle(""+(String)hmEmpName.get(oTList.get(i).getoTEmp_id()),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				 alInnerExport.add(new DataStyle(""+oTList.get(i).getoTMname(),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				 alInnerExport.add(new DataStyle(""+uF.getGender(oTList.get(i).getoTGender()),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				 alInnerExport.add(new DataStyle(""+uF.showData((String)hmEmpPanNo.get(oTList.get(i).getoTEmp_id()),""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				 alInnerExport.add(new DataStyle(""+hmEmpDesigMap.get(oTList.get(i).getoTEmp_id()),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				 alInnerExport.add(new DataStyle(""+oTList.get(i).getoTDate(),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				 alInnerExport.add(new DataStyle(""+oTList.get(i).getoTHours(),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				 alInnerExport.add(new DataStyle(""+"NA",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				 alInnerExport.add(new DataStyle(""+"NA",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				 alInnerExport.add(new DataStyle(""+"NA",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				 alInnerExport.add(new DataStyle(""+"NA",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				 alInnerExport.add(new DataStyle(""+"NA",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				 
				 reportList.add(alInner);
				 reportListExport.add(alInnerExport);

			 }
			 
			Map<String, String> hmCheckPayroll = new HashMap<String, String>(); 
			pst = con.prepareStatement("select emp_id from payroll_generation where paycycle=? group by emp_id");
			pst.setInt(1, uF.parseToInt(getPayCycleNo()));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmCheckPayroll.put(rs.getString("emp_id"), rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("alDates", alDates);
			
			request.setAttribute("reportList", reportList);
			session.setAttribute("reportListExport", reportListExport);
			
			
		} catch (Exception e) {
			e.printStackTrace();
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

	}
	
	public String loadOverTimeHours(UtilityFunctions uF){
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
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
		if(getF_wLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getF_wLocation().length;j++) {
					if(getF_wLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
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
		
		alFilter.add("PAYCYCLE");
		String strPaycycle = "";
		String[] strPayCycleDates = null;
		if (getPaycycle() != null) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			
			strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
		}
		hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		
		alFilter.add("Emlpoyee/Contractor");
		if(empContractor != null){
			hmFilter.put("EMPLOYEE/CONTRACTOR", empContractor);
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	
	public HttpSession getSession() {
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public String getStrSessionEmpId() {
		return strSessionEmpId;
	}

	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
	}

	public String[] getF_wLocation() {
		return f_wLocation;
	}

	public void setF_wLocation(String[] f_wLocation) {
		this.f_wLocation = f_wLocation;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
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

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
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

	public CommonFunctions getCF() {
		return CF;
	}

	public void setCF(CommonFunctions cF) {
		CF = cF;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	public String getStrD1() {
		return strD1;
	}

	public void setStrD1(String strD1) {
		this.strD1 = strD1;
	}

	public String getStrD2() {
		return strD2;
	}

	public void setStrD2(String strD2) {
		this.strD2 = strD2;
	}

	public String getPayCycleNo() {
		return payCycleNo;
	}

	public void setPayCycleNo(String payCycleNo) {
		this.payCycleNo = payCycleNo;
	}


	public String getStrSearch() {
		return strSearch;
	}

	public void setStrSearch(String strSearch) {
		this.strSearch = strSearch;
	}

	public String getEmpContractor() {
		return empContractor;
	}

	public void setEmpContractor(String empContractor) {
		this.empContractor = empContractor;
	}

	
	
}
