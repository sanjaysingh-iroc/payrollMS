package com.konnect.jpms.payroll;

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
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
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

public class ExGratiaForm  extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	String strUserType = null;

	CommonFunctions CF = null;

	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	
	String[] f_strWLocation; 
	String[] f_level;
	String[] f_department;
	String[] f_service;
	
	List<FillPayCycles> paycycleList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillWLocation> wLocationList;
	List<FillOrganisation> organisationList;
	
	String paycycle;
	String f_org;

	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/payroll/ExGratiaForm.jsp");
		request.setAttribute(TITLE, "Ex-Gratia Form");

		strEmpId = (String) session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(USERTYPE);
		
		UtilityFunctions uF = new UtilityFunctions();

//			boolean isView = CF.getAccess(session, request, uF);
//			if (!isView) {
//				request.setAttribute(PAGE, PAccessDenied);
//				request.setAttribute(TITLE, TAccessDenied);
//				return ACCESS_DENIED;
//			}
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		
		if(getF_org()==null){
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
		
		viewExGratiaForm(uF);
		
		loadExGratiaForm(uF);

		return LOAD;
	}

	public void loadExGratiaForm(UtilityFunctions uF) {

		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		
		getSelectedFilter(uF);

	}

	public String viewExGratiaForm(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			String []strPayCycleDates;
			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, getF_org());
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			}
			
			pst = con.prepareStatement("select emp_id from payroll_generation where paid_from = ? and paid_to = ? and paycycle = ? group by emp_id order by emp_id ");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
			rs = pst.executeQuery();
			List<String> ckEmpPayList = new ArrayList<String>();
			while(rs.next()){
				ckEmpPayList.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("ckEmpPayList", ckEmpPayList);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive = true and joining_date<= ? ");
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
                	 sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            } 
            if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            sbQuery.append(" and eod.emp_id in ( select esd.emp_id from emp_salary_details esd, (select max(effective_date) as max_date, emp_id " +
            		" from emp_salary_details where isdisplay = true and is_approved=true group by emp_id ) as b where esd.effective_date = b.max_date " +
            		"and b.emp_id = esd.emp_id and isdisplay = true and is_approved=true and esd.salary_head_id=? and esd.effective_date <=?)");
            sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(2, EXGRATIA);
			pst.setDate(3,  uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			System.out.println("pst===>" + pst);
			rs = pst.executeQuery();
			List<List<String>> alEmpReport = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> alEmpReportInner = new ArrayList<String>();
				alEmpReportInner.add(rs.getString("emp_per_id"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				alEmpReportInner.add(uF.showData(rs.getString("emp_fname"), "")+strEmpMName+" "+uF.showData(rs.getString("emp_lname"), ""));
				alEmpReport.add(alEmpReportInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("alEmpReport", alEmpReport);

			pst = con.prepareStatement("select * from emp_exgratia_details where paid_from = ? and paid_to=? and pay_paycycle=?");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
//			System.out.println("pst===>" + pst);
			rs = pst.executeQuery();
			Map<String, String> hmExGratia = new HashMap<String, String>();
			Map<String, String> hmExGratiaId = new HashMap<String, String>();
			Map<String, String> hmExGratiaValue = new HashMap<String, String>();
			while (rs.next()) {
				hmExGratia.put(rs.getString("emp_id"),rs.getString("is_approved"));
				hmExGratiaId.put(rs.getString("emp_id"),rs.getString("emp_exgratia_id"));
				hmExGratiaValue.put(rs.getString("emp_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("pay_amount"))));
			}
			rs.close();
			pst.close();
			
			getExGratiaCalculationDetails(con,uF,CF,strPayCycleDates,alEmpReport);
			
			request.setAttribute("hmExGratia", hmExGratia);
			request.setAttribute("hmExGratiaId", hmExGratiaId);
			request.setAttribute("hmExGratiaValue", hmExGratiaValue);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;

	}

	public void getExGratiaCalculationDetails(Connection con, UtilityFunctions uF, CommonFunctions CF, String[] strPayCycleDates, List<List<String>> alEmpReport) {

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			if(hmEmpCodeName==null) hmEmpCodeName = new HashMap<String, String>();
			
			String[] strFinancialDates = CF.getFinancialYear(con, strPayCycleDates[1], CF, uF);
			StringBuilder sbEmp = null;
			for(int i=0;alEmpReport!=null && i<alEmpReport.size();i++){
				List<String> alEmp = alEmpReport.get(i); 
				if(sbEmp==null){
					sbEmp = new StringBuilder();
					sbEmp.append(alEmp.get(0));
				} else {
					sbEmp.append(","+alEmp.get(0));
				}
			}
			
			
			pst = con.prepareStatement("select * from EX_GRATIA_DETAILS where FINANCIAL_YEAR_FROM =? and FINANCIAL_YEAR_TO =? and ORG_ID=? and PAYCYCLE_FROM=? and PAYCYCLE_TO=? and PAYCYCLE=?");
			pst.setDate(1, uF.getDateFormat(strFinancialDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_org()));
			pst.setDate(4, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(strPayCycleDates[2]));
//			System.out.println("pst===>" + pst);
			rs = pst.executeQuery(); 
			Map<String,String> hmExGratiaMap = new HashMap<String, String>();
			while(rs.next()){				
				hmExGratiaMap.put("EX_GRATIA_ID",rs.getString("EX_GRATIA_ID"));
				hmExGratiaMap.put("FINANCIAL_YEAR_FROM",uF.getDateFormat(rs.getString("FINANCIAL_YEAR_FROM"), DBDATE, DATE_FORMAT));
				hmExGratiaMap.put("FINANCIAL_YEAR_TO",uF.getDateFormat(rs.getString("FINANCIAL_YEAR_TO"), DBDATE, DATE_FORMAT));
				hmExGratiaMap.put("NET_PROFIT",rs.getString("NET_PROFIT"));
				hmExGratiaMap.put("PAYCYCLE_FROM",uF.getDateFormat(rs.getString("PAYCYCLE_FROM"), DBDATE, CF.getStrReportDateFormat()));
				hmExGratiaMap.put("PAYCYCLE_TO",uF.getDateFormat(rs.getString("PAYCYCLE_TO"), DBDATE, CF.getStrReportDateFormat()));
				hmExGratiaMap.put("PAYCYCLE",rs.getString("PAYCYCLE"));
				hmExGratiaMap.put("ADDED_BY",uF.showData(hmEmpCodeName.get(rs.getString("ADDED_BY")), ""));
				hmExGratiaMap.put("ENTRY_DATE",uF.getDateFormat(rs.getString("ENTRY_DATE"), DBDATE, CF.getStrReportDateFormat()));
				hmExGratiaMap.put("ORG_ID",rs.getString("ORG_ID"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from EX_GRATIA_SLAB_DETAILS where ? between SLAB_FROM and SLAB_TO");
			pst.setDouble(1, uF.parseToDouble(hmExGratiaMap.get("NET_PROFIT")));
//			System.out.println("pst===>" + pst);
			rs = pst.executeQuery();
			Map<String,String> hmGratiaSlab = new HashMap<String, String>(); 
			while(rs.next()){
				hmGratiaSlab.put("GRATIA_SLAB_ID",rs.getString("GRATIA_SLAB_ID"));
				hmGratiaSlab.put("EX_GRATIA_SLAB",rs.getString("EX_GRATIA_SLAB"));
				hmGratiaSlab.put("SLAB_FROM",rs.getString("SLAB_FROM"));
				hmGratiaSlab.put("SLAB_TO",rs.getString("SLAB_TO"));
				hmGratiaSlab.put("SLAB_PERCENTAGE",rs.getString("SLAB_PERCENTAGE"));
			}
			rs.close();
			pst.close();
			
			Map<String,String> hmExGratiaCalAmt = new HashMap<String, String>();
			if(hmGratiaSlab.size()>0){
				if(sbEmp !=null){
					pst = con.prepareStatement("select emp_id,sum(amount) as amount from payroll_generation where financial_year_from_date=? and financial_year_to_date=? " +
							"and salary_head_id in ("+BASIC+","+DA+") and emp_id in (select emp_id from employee_official_details where org_id=? " +
							"and emp_id in("+sbEmp.toString()+")) group by emp_id");
					pst.setDate(1, uF.getDateFormat(strFinancialDates[0], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialDates[1], DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(getF_org()));
//					System.out.println("pst===>" + pst);
					rs = pst.executeQuery(); 
					Map<String,String> hmBasicDA = new HashMap<String, String>();
					double dblEmpTotalBasicDA = 0.0d;
					while(rs.next()){				
						hmBasicDA.put(rs.getString("emp_id"),rs.getString("amount"));
						dblEmpTotalBasicDA += uF.parseToDouble(rs.getString("amount"));
					}
					rs.close();
					pst.close();
					
					double dblTotalExGratiaAmt = (uF.parseToDouble(hmExGratiaMap.get("NET_PROFIT")) * uF.parseToDouble(hmGratiaSlab.get("SLAB_PERCENTAGE"))) / 100;
					double dblEmpExGratia =  dblTotalExGratiaAmt / dblEmpTotalBasicDA * 100;
					for(int i=0;alEmpReport!=null && i<alEmpReport.size();i++){  
						List<String> alEmp = alEmpReport.get(i); 
						double dblEmpExGratiaAmt = (uF.parseToDouble(hmBasicDA.get(alEmp.get(0))) * uF.parseToDouble(uF.formatIntoTwoDecimal(dblEmpExGratia))) / 100;
						hmExGratiaCalAmt.put(alEmp.get(0), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEmpExGratiaAmt));
					}
				}
			}
//			System.out.println("hmExGratiaCalAmt===>" + hmExGratiaCalAmt);
			request.setAttribute("hmExGratiaCalAmt", hmExGratiaCalAmt);
			
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
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
			alFilter.add("ORGANISATION");
			if(getF_org()!=null) {
				String strOrg="";
				for(int i=0;organisationList!=null && i<organisationList.size();i++) {
					if(getF_org().equals(organisationList.get(i).getOrgId())) {
						strOrg=organisationList.get(i).getOrgName();
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
			
			alFilter.add("PAYCYCLE");	
			if(getPaycycle()!=null){
				String strPayCycle="";
				int k=0;
				for(int i=0;paycycleList!=null && i<paycycleList.size();i++){
					if(getPaycycle().equals(paycycleList.get(i).getPaycycleId())){
						if(k==0){
							strPayCycle=paycycleList.get(i).getPaycycleName();
						}else{
							strPayCycle+=", "+paycycleList.get(i).getPaycycleName();
						}
						k++;
					}
				}
				if(strPayCycle!=null && !strPayCycle.equals("")){
					hmFilter.put("PAYCYCLE", strPayCycle);
				}else{
					hmFilter.put("PAYCYCLE", "All Paycycle");
				}
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
			if(getF_level()!=null){
				String strLevel="";
				int k=0;
				for(int i=0;levelList!=null && i<levelList.size();i++){
					for(int j=0;j<getF_level().length;j++){
						if(getF_level()[j].equals(levelList.get(i).getLevelId())){
							if(k==0){
								strLevel=levelList.get(i).getLevelCodeName();
							}else{
								strLevel+=", "+levelList.get(i).getLevelCodeName();
							}
							k++;
						}
					}
				}
				if(strLevel!=null && !strLevel.equals("")){
					hmFilter.put("LEVEL", strLevel);
				}else{
					hmFilter.put("LEVEL", "All Levels");
				}
			}else{
				hmFilter.put("LEVEL", "All Levels");
			}
			
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
    }

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

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

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
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

}
