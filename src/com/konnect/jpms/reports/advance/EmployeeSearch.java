package com.konnect.jpms.reports.advance;

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

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.reports.EmployeeReport;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeSearch extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String f_strWLocation; 
	String f_department;
	String f_level; 
	
	String empcode;
	String empFname;
	String empLname;
	public List<FillEmployee> getSupervisorList() {
		return supervisorList;
	}

	public void setSupervisorList(List<FillEmployee> supervisorList) {
		this.supervisorList = supervisorList;
	}




	String supervisor;
	List<FillEmployee> supervisorList;
	
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillWLocation> wLocationList;
	List<FillOrganisation> orgList;
	String f_org;
	CommonFunctions CF=null;
	private static Logger log = Logger.getLogger(EmployeeReport.class);
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		if(getEmpcode()==null){
			setEmpcode("%");
		}
		if(getEmpFname()==null){
			setEmpFname("%");
		}
		if(getEmpLname()==null){
			setEmpLname("%");
		}
		
		request.setAttribute(PAGE, "/jsp/reports/advance/EmployeeSearch.jsp");
		 

		

		
//		if(getF_strWLocation()==null){
//			setF_strWLocation((String)session.getAttribute(WLOCATIONID));
//		}
//		
//		if(getF_org()==null){
//			setF_org((String)session.getAttribute(ORGID));
//		}
		
		
			request.setAttribute(TITLE, "Employee Search");
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		viewEmployee(uF);
		return loadEmployee(uF);
		
	}

	public String loadEmployee(UtilityFunctions uF) {
		
		
		if(uF.parseToInt(getF_org())>0){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
			levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
			departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
			
		}else{
			wLocationList = new FillWLocation(request).fillWLocation();
			levelList = new FillLevel(request).fillLevel();
			departmentList = new FillDepartment(request).fillDepartment();
		}
		orgList = new FillOrganisation(request).fillOrganisation();
		supervisorList = new FillEmployee(request).fillSupervisorNameCode(0, getF_org(), null);
		
		return LOAD;
	}

	public String viewEmployee(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con,null,null);
		

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpExperience = getPreEmpExperience(con);
			
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append(" select a.*,wli.wlocation_name from (select a.*,level_name from (select a.*,dd.designation_name,level_id from (select * from(select a.*,di.dept_name from(select * from(SELECT *, datediff(yy, emp_date_of_birth, ?) as their_age,DATEADD(yy,60,emp_date_of_birth) as retiring_date,CAST(datediff(dd, joining_date, ?) as float)/365 as current_experience FROM employee_personal_details epd , employee_official_details eod where epd.emp_per_id=eod.emp_id and epd.is_alive = 1 )a where emp_per_id>0 ");

					sbQuery.append(" and upper(emp_fname) like upper('%"+getEmpFname()+"%')");
					sbQuery.append(" and upper(emp_lname) like upper('%"+getEmpLname()+"%')");
					sbQuery.append(" and upper(empcode) like upper('%"+getEmpcode()+"%')");
					if(uF.parseToInt(getSupervisor())>0){
						sbQuery.append(" and a.supervisor_emp_id="+uF.parseToInt(getSupervisor()));
					}
					
					if(uF.parseToInt(getF_org())>0){
						sbQuery.append(" and a.org_id="+uF.parseToInt(getF_org()));
					}			
					if(uF.parseToInt(getF_strWLocation())>0){
						sbQuery.append(" and a.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
					}
					if(uF.parseToInt(getF_department())>0){
						sbQuery.append(" and a.depart_id = "+uF.parseToInt(getF_department()));			
					}
					if(uF.parseToInt(getF_level())>0){
						sbQuery.append(" and a.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
					}
					
					sbQuery.append(" )a LEFT JOIN department_info di  on di.dept_id=a.depart_id )a LEFT JOIN grades_details gd  on a.grade_id=gd.grade_id )a LEFT JOIN designation_details dd on a.designation_id=dd.designation_id)a LEFT JOIN level_details ld on a.level_id=ld.level_id)a LEFT JOIN work_location_info wli on a.wlocation_id=wli.wlocation_id order by emp_fname,emp_lname");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					rs=pst.executeQuery();
					List<List<String>> outerList=new ArrayList<List<String>>();

					while(rs.next()){
						
//						double preExperience=uF.parseToDouble(hmEmpExperience.get(rs.getString("emp_per_id")));
						
						List<String> alBirthDays = new ArrayList<String>();
						alBirthDays.add(rs.getString("emp_per_id"));
						alBirthDays.add(rs.getString("empcode"));
						alBirthDays.add(rs.getString("emp_fname")+" "+rs.getString("emp_mname")+" "+rs.getString("emp_lname"));
						alBirthDays.add(rs.getString("designation_name")!=null?rs.getString("designation_name"):"-");
						alBirthDays.add(rs.getString("grade_name")!=null?rs.getString("grade_name"):"-");
						alBirthDays.add(rs.getString("dept_name")!=null?rs.getString("dept_name"):"-");
						
					
						alBirthDays.add(uF.showData(hmEmpCodeName.get(rs.getString("supervisor_emp_id")), "-"));
						alBirthDays.add(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT));
						
						alBirthDays.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
						alBirthDays.add(rs.getString("their_age"));
						alBirthDays.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("current_experience"))));
						alBirthDays.add(uF.showData(uF.formatIntoOneDecimal(uF.parseToDouble(hmEmpExperience.get(rs.getString("emp_per_id")))), "0"));
						
						alBirthDays.add(rs.getString("emp_contactno"));
//						alBirthDays.add(rs.getString("level_name")!=null?rs.getString("level_name"):"-");
						
						
					
						outerList.add(alBirthDays);
					}
					rs.close();
					pst.close();
					
			
//			String strEmpId = null;
			request.setAttribute("reportList", outerList);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	
	public String getEmpcode() {
		return empcode;
	}

	public void setEmpcode(String empcode) {
		this.empcode = empcode;
	}

	public String getEmpFname() {
		return empFname;
	}

	public void setEmpFname(String empFname) {
		this.empFname = empFname;
	}

	public String getEmpLname() {
		return empLname;
	}

	public void setEmpLname(String empLname) {
		this.empLname = empLname;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public Map<String, String> getPreEmpExperience(Connection con) {

		Map<String, String> hmEmpExperience = new HashMap<String, String>();

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement("select CAST(sum(their_age) as float)/365 as experience,emp_id from (select datediff(dd, from_date, to_date) as their_age,emp_id from emp_prev_employment where from_date is not null and to_date is not null )a group by emp_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmEmpExperience.put(rs.getString("emp_id"), rs.getString("experience"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
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
		return hmEmpExperience;
	}

	


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

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

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}
	
}
