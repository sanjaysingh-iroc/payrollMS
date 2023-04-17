package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProbationReport  extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(ProbationReport.class);

	String strSessionEmpId;
	String f_service;
	List<FillServices> serviceList;
 
	String financialYear;
	String strMonth;
	
	List<FillFinancialYears> financialYearList; 
	List<FillMonth> monthList;
	
	String f_strWLocation;
	String f_department;
	String f_level;
	String f_org;
	List<FillOrganisation> orgList;
	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillPayCycles> paycycleList ;
	
	String paycycle; 
	String strStartDate;
	String strEndDate;
	String submit;
	String emp_id;
	
	String[] empIds;
	
	String effectiveType;
	String effectiveDate;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		request.setAttribute(PAGE, "/jsp/reports/ProbationReport.jsp");
		request.setAttribute(TITLE, "Probations");

		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		String[] arrDates = null;
		if (getPaycycle() != null) {
			arrDates = getPaycycle().split("-");
			setPaycycle(arrDates[0] + "-" + arrDates[1] + "-" + arrDates[2]);
		} else {
			arrDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
			setPaycycle(arrDates[0] + "-" + arrDates[1] + "-" + arrDates[2]);
		}
		
		if(getStrStartDate()==null){
			setStrStartDate(arrDates[0]);
		}
		if(getStrEndDate()==null){
			setStrEndDate(arrDates[1]);
		}
		
		if(getSubmit()!=null && getSubmit().equals("Submit")){
			updateProbation(uF);				
		}
		
		getProbationReport(uF);

		return loadProbationReport(uF);

	}
	
	
	private void updateProbation(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs =null;	
		
		
		try {

			con = db.makeConnection(con);
			Date startdate=uF.getDateFormat(getStrStartDate(), DATE_FORMAT);
			Date enddate=uF.getDateFormat(getStrEndDate(), DATE_FORMAT);
			
			String startMonth=uF.getDateFormat(getStrStartDate(), DATE_FORMAT, "MM");
			String startYear=uF.getDateFormat(getStrStartDate(), DATE_FORMAT, "yyyy");
			
			String endMonth=uF.getDateFormat(getStrEndDate(), DATE_FORMAT, "MM");
			String endYear=uF.getDateFormat(getStrEndDate(), DATE_FORMAT, "yyyy");
			
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_activity_details ead,employee_official_details eod,employee_personal_details epd " +
					" where ead.emp_id=eod.emp_id and ead.emp_id=epd.emp_per_id and eod.emp_id=epd.emp_per_id and ead.activity_id=7 " +
					" and ead.emp_id in (select emp_id from probation_policy where is_probation=true) and ead.probation_period>0 " +
					" and ead.emp_activity_id in (select max(emp_activity_id) as emp_activity_id from employee_activity_details where activity_id=7 group by emp_id) ");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
			}
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and ead.wlocation_id="+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and ead.department_id="+uF.parseToInt(getF_department()));
			}
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and eod.service_id like '%,"+uF.parseToInt(getF_service())+",%' ");
			}
			if((String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			Map<String,String> hmEmpProbationDate=new HashMap<String, String>();
			Map<String,Map<String,String>> hmEmp=new HashMap<String, Map<String,String>>();
			while (rs.next()) {
				Date probationDate=uF.getFutureDate(rs.getDate("joining_date"), rs.getInt("probation_period"));
				String probationMonth=uF.getDateFormat(""+probationDate, DBDATE, "MM");
				String probationYear=uF.getDateFormat(""+probationDate, DBDATE, "yyyy");
				
				if((uF.parseToInt(startMonth)<=uF.parseToInt(probationMonth) && uF.parseToInt(startYear)<=uF.parseToInt(probationYear)) && (uF.parseToInt(endMonth)>=uF.parseToInt(probationMonth) && uF.parseToInt(endYear)>=uF.parseToInt(probationYear))){
					hmEmpProbationDate.put(rs.getString("emp_id"),uF.getDateFormat(""+probationDate, DBDATE, DATE_FORMAT));
					
					Map<String,String> hmEmpData=new HashMap<String, String>();
					hmEmpData.put("EMP_WLOCATION",rs.getString("wlocation_id"));
					hmEmpData.put("EMP_DEPARTMENT",rs.getString("department_id"));
					hmEmpData.put("EMP_LEVEL",rs.getString("level_id"));
					hmEmpData.put("EMP_DESIGNATION",rs.getString("desig_id"));
					hmEmpData.put("EMP_GRADE",rs.getString("grade_id"));
					hmEmpData.put("EMP_STATUS_CODE",rs.getString("emp_status_code"));
					hmEmpData.put("EMP_EFFECTIVE_DATE",uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
					hmEmpData.put("EMP_NOTICE_PERIOD",rs.getString("notice_period"));
					hmEmpData.put("EMP_PROBATION_PERIOD",rs.getString("probation_period"));
					hmEmpData.put("EMP_PROBATION_END_DATE",uF.getDateFormat(""+probationDate, DBDATE, DATE_FORMAT));
					
					hmEmp.put(rs.getString("emp_id"),hmEmpData);
				}
				
			}
			rs.close();
			pst.close();
			
			for(int i=0;i<getEmpIds().length;i++){
//				System.out.println("getEmpIds()=======>"+getEmpIds()[i]);
				
				Map<String,String> hmEmpData=hmEmp.get(getEmpIds()[i].trim());
				if(hmEmpData==null){
					continue;
				}
				String effectiveDate=hmEmpProbationDate.get(getEmpIds()[i].trim());
				if(getEffectiveType()!=null && getEffectiveType().equals("2")){
					effectiveDate=getEffectiveDate();
				}
				
				pst = con.prepareStatement("insert into employee_activity_details (wlocation_id, department_id, level_id, desig_id, grade_id, " +
						"emp_status_code, activity_id, reason, effective_date, entry_date, user_id, emp_id, notice_period, probation_period) " +
						"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(hmEmpData.get("EMP_WLOCATION")));
				pst.setInt(2, uF.parseToInt(hmEmpData.get("EMP_DEPARTMENT")));
				pst.setInt(3, uF.parseToInt(hmEmpData.get("EMP_LEVEL")));
				pst.setInt(4, uF.parseToInt(hmEmpData.get("EMP_DESIGNATION")));
				pst.setInt(5, uF.parseToInt(hmEmpData.get("EMP_GRADE")));
				pst.setString(6, hmEmpData.get("EMP_STATUS_CODE"));
				pst.setInt(7, 9);
				pst.setString(8, "");
				pst.setDate(9, uF.getDateFormat(effectiveDate, DATE_FORMAT));
				pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(11, uF.parseToInt(strSessionEmpId));
				pst.setInt(12, uF.parseToInt(getEmpIds()[i].trim()));
				pst.setInt(13, uF.parseToInt(hmEmpData.get("EMP_NOTICE_PERIOD")));
				pst.setInt(14, uF.parseToInt(hmEmpData.get("EMP_PROBATION_PERIOD")));
				pst.execute();
				pst.close();
				
				
				
				pst = con.prepareStatement("update probation_policy set is_probation=false, probation_end_date=?, probation_end_by=? where emp_id=?");
				pst.setDate(1, uF.getDateFormat(effectiveDate, DATE_FORMAT));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setInt(3, uF.parseToInt(getEmpIds()[i].trim()));
				int x = pst.executeUpdate();
				pst.close();
				
				if(x==0){
					pst = con.prepareStatement("insert into probation_policy (is_probation, probation_end_date, probation_end_by, emp_id) values (?,?,?,?)");
					pst.setBoolean(1, false);
					pst.setDate(2, uF.getDateFormat(effectiveDate, DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
					pst.setInt(4, uF.parseToInt(getEmpIds()[i].trim()));
					pst.execute();
					pst.close();
				}
				
				request.setAttribute(MESSAGE, SUCCESSM+"Probation approved successfully for selected employees."+END);
				
			}
			
						
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, ERRORM+"Probation Approved Failed"+END);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	private void getProbationReport(UtilityFunctions uF) {
		
	
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs =null;	
		
		
		try {

			con = db.makeConnection(con);
		
			//Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap();
			Map<String, String> hmEmpCodeDesig =CF.getEmpDesigMap(con);
			
			Map<String, Map<String, String>> hmWorkLocation =CF.getWorkLocationMap(con);
			Map<String, String> hmDept =CF.getDeptMap(con);
			
			
			Map<String, String> hmEmpWlocationMap =new HashMap<String, String>();
			CF.getEmpWlocationMap(con, null, hmEmpWlocationMap, null, null);
			
			Date startdate=uF.getDateFormat(getStrStartDate(), DATE_FORMAT);
			Date enddate=uF.getDateFormat(getStrEndDate(), DATE_FORMAT);
			
			String startMonth=uF.getDateFormat(getStrStartDate(), DATE_FORMAT, "MM");
			String startYear=uF.getDateFormat(getStrStartDate(), DATE_FORMAT, "yyyy");
			
			String endMonth=uF.getDateFormat(getStrEndDate(), DATE_FORMAT, "MM");
			String endYear=uF.getDateFormat(getStrEndDate(), DATE_FORMAT, "yyyy");
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_activity_details ead,employee_official_details eod,employee_personal_details epd " +
					" where ead.emp_id=eod.emp_id and ead.emp_id=epd.emp_per_id and eod.emp_id=epd.emp_per_id and ead.activity_id=7 " +
					" and ead.emp_id in (select emp_id from probation_policy where is_probation=true) and ead.probation_period>0 " +
					" and ead.emp_activity_id in (select max(emp_activity_id) as emp_activity_id from employee_activity_details where activity_id=7 group by emp_id) ");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
			}
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and ead.wlocation_id="+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and ead.department_id="+uF.parseToInt(getF_department()));
			}
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and eod.service_id like '%,"+uF.parseToInt(getF_service())+",%' ");
			}
			
			if(uF.parseToInt(getEmp_id())>0){
				sbQuery.append(" and eod.emp_id="+uF.parseToInt(getEmp_id()));
			}
			if((String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			List<List<String>> outerList=new ArrayList<List<String>>();
			while (rs.next()) {
				Date probationDate=uF.getFutureDate(rs.getDate("joining_date"), rs.getInt("probation_period"));
				String probationMonth=uF.getDateFormat(""+probationDate, DBDATE, "MM");
				String probationYear=uF.getDateFormat(""+probationDate, DBDATE, "yyyy");
								
				if((uF.parseToInt(startMonth)<=uF.parseToInt(probationMonth) && uF.parseToInt(startYear)<=uF.parseToInt(probationYear)) && (uF.parseToInt(endMonth)>=uF.parseToInt(probationMonth) && uF.parseToInt(endYear)>=uF.parseToInt(probationYear))){
					
					List<String> innerList=new ArrayList<String>();
					innerList.add(rs.getString("emp_id"));
					innerList.add(rs.getString("empcode"));
					innerList.add(rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
					innerList.add(hmEmpCodeDesig.get(rs.getString("emp_id")));
					Map<String,String> hmWLocation=hmWorkLocation.get(rs.getString("wlocation_id"));
					if(hmWLocation==null) hmWLocation=new HashMap<String, String>();
					innerList.add(hmWLocation.get("WL_NAME"));
					innerList.add(hmDept.get(rs.getString("depart_id")));
					innerList.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));
					innerList.add(uF.getDateFormat(""+probationDate, DBDATE, CF.getStrReportDateFormat()));
					
					outerList.add(innerList);
					
				}
				
			}
			rs.close();
			pst.close();			
			request.setAttribute("outerList", outerList);
			
			
			Date currDate=uF.getCurrentDate(CF.getStrTimeZone());
			
			String currentDate=uF.getDateFormat(""+currDate, DBDATE, DATE_FORMAT);
			
			StringBuilder sb = new StringBuilder();
		 	sb.append("<div id=\"popup_name\" class=\"popup_block\">" + 
					   "<h2 class=\"textcolorWhite\">Approve Probation</h2>" +
					   "<table>");			 	
		 	sb.append("<tr><td colspan=\"2\" class=\"textcolorWhite\"><input type=\"radio\" name=\"effectiveType\" value=\"1\" checked />Actual Employee Probation End Date</td></tr>");
		 	sb.append("<tr><td class=\"textcolorWhite\"><input type=\"radio\" name=\"effectiveType\" value=\"2\"/>Enter New Probation End Date for selected employees </td>" +
		 			"<td><input type=\"text\" name=\"effectiveDate\" id=\"effectiveDate\" value=\""+currentDate+"\" style=\"width:70px;\"/></td>" +
		 			"</tr>");
		 	sb.append("<tr><td>&nbsp;</td>" +
		 			"<td><input type=\"submit\" name=\"submit\" value=\"Submit\" class=\"input_button\"/></td>" +
		 			"</tr>");
			sb.append("</table></div>");
			
			request.setAttribute("approvePopUp", sb.toString());
			
						
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	
	public String loadProbationReport(UtilityFunctions uF) {
		paycycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillMonth();
		
		orgList = new FillOrganisation(request).fillOrganisation();
		orgList.add(new FillOrganisation("0","All Organization"));
		
		Collections.sort(orgList, new Comparator<FillOrganisation>() {

			@Override
			public int compare(FillOrganisation o1, FillOrganisation o2) {
				return o1.getOrgId().compareTo(o2.getOrgId());
			}
		});
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
			orgList = new FillOrganisation(request).fillOrganisation();
		}
		
		if(getF_org()!=null && uF.parseToInt(getF_org())>-1 && getF_strWLocation()!=null && uF.parseToInt(getF_strWLocation())>-1){
			departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
			departmentList.add(new FillDepartment("0","All Departments"));
		}else{
			departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
			departmentList.add(new FillDepartment("0","All Departments"));
		}
		
		Collections.sort(departmentList, new Comparator<FillDepartment>() {

			@Override
			public int compare(FillDepartment o1, FillDepartment o2) {
				return o1.getDeptId().compareTo(o2.getDeptId());
			}
		});
		
		
		if(getF_org()!=null && uF.parseToInt(getF_org())>-1 && getF_strWLocation()!=null && uF.parseToInt(getF_strWLocation())>-1 && getF_department()!=null && uF.parseToInt(getF_department())>-1){
			levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
			levelList.add(new FillLevel("0","All Levels"));
			
		}else{		
			levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
			levelList.add(new FillLevel("0","All Levels"));
		}
		
		Collections.sort(levelList, new Comparator<FillLevel>() {

			@Override
			public int compare(FillLevel o1, FillLevel o2) {
				return o1.getLevelId().compareTo(o2.getLevelId());
			}
		});
		
		if(getF_org()!=null && uF.parseToInt(getF_org())>-1 && getF_strWLocation()!=null && uF.parseToInt(getF_strWLocation())>-1 && getF_department()!=null && uF.parseToInt(getF_department())>-1){
			serviceList = new FillServices(request).fillServices(getF_org(), uF);
			serviceList.add(new FillServices("0","All Service"));
			 
		}else{		
			serviceList = new FillServices(request).fillServices(getF_org(), uF);
			serviceList.add(new FillServices("0","All Service"));
		}
		
		Collections.sort(serviceList, new Comparator<FillServices>() {

			@Override
			public int compare(FillServices o1, FillServices o2) {
				return o1.getServiceId().compareTo(o2.getServiceId());
			}
		});
		
		return LOAD;
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


	public String getF_strWLocation() {
		return f_strWLocation;
	}


	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}


	public String getF_department() {
		return f_department;
	}


	public void setF_department(String f_department) {
		this.f_department = f_department;
	}


	public String getF_level() {
		return f_level;
	}


	public void setF_level(String f_level) {
		this.f_level = f_level;
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


	public String getF_service() {
		return f_service;
	}


	public void setF_service(String f_service) {
		this.f_service = f_service;
	}


	public List<FillServices> getServiceList() {
		return serviceList;
	}


	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
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


	public String getSubmit() {
		return submit;
	}


	public void setSubmit(String submit) {
		this.submit = submit;
	}


	public String[] getEmpIds() {
		return empIds;
	}


	public void setEmpIds(String[] empIds) {
		this.empIds = empIds;
	} 


	public String getEffectiveType() {
		return effectiveType;
	}


	public void setEffectiveType(String effectiveType) {
		this.effectiveType = effectiveType;
	}


	public String getEffectiveDate() {
		return effectiveDate;
	}


	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}


	public String getEmp_id() {
		return emp_id;
	}


	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

}
