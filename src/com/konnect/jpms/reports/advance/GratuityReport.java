package com.konnect.jpms.reports.advance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GratuityReport extends ActionSupport implements ServletRequestAware, IStatements {

	CommonFunctions CF = null;
	HttpSession session;
	List<FillOrganisation> orgList;	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	String f_department;
	String f_strWLocation;
	String f_org;
	
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

	public String getF_department() {
		return f_department;
	}

	public void setF_department(String f_department) {
		this.f_department = f_department;
	}

	public String getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	String level;
	private HttpServletRequest request;
	public String execute() throws Exception {

		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		UtilityFunctions uF=new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/reports/advance/GratuityReport.jsp");
		request.setAttribute(TITLE, "Gratuity Report");
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
		
		
		getBirthday();
		 
		return LOAD;
	}

public void getBirthday(){
	
	Connection con = null;
	PreparedStatement pst=null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	UtilityFunctions uF=new UtilityFunctions();
	
	try {
		con = db.makeConnection(con);
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
		boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
		Map<String,String> empGratuityMap=new HashMap<String,String>();
		pst=con.prepareStatement("select  sum(amount) as amount,emp_id from payroll_generation where salary_head_id=? group by emp_id");
		pst.setInt(1, GRATUITY);
		rs=pst.executeQuery();
		while(rs.next()){
			empGratuityMap.put(rs.getString("emp_id"),rs.getString("amount"));
		}
		rs.close();
		pst.close();
		
		Map<String,String> gratuityMap=new HashMap<String,String>();
		pst=con.prepareStatement("select  * from gratuity_details");
		rs=pst.executeQuery();
		while(rs.next()){
			gratuityMap.put("SERVICE_FROM",rs.getString("service_from"));
			gratuityMap.put("SERVICE_TO",rs.getString("service_to"));
			gratuityMap.put("DAYS",rs.getString("gratuity_days"));
			gratuityMap.put("MAX_AMOUNT",rs.getString("max_amount"));
		}
		rs.close();
		pst.close();

//		String[] strApprovePayCycle=CF.getCurrentPayCycle(CF.getStrTimeZone(), CF);
		pst = con.prepareStatement("select sum(amount) as amount,emp_id from ( select esd.* from emp_salary_details esd, ( select max(effective_date) as effective_date, emp_id from emp_salary_details where effective_date<=? and is_approved= 1 group by emp_id ) a where salary_head_id in(1,2,227) and a.effective_date = esd.effective_date and esd.emp_id = a.emp_id) a group by emp_id");
		pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
		Map<String,String> empSalaryMap=new HashMap<String,String>();
		rs = pst.executeQuery();
		while(rs.next()){
			empSalaryMap.put(rs.getString("emp_id"),rs.getString("amount"));
		}
		rs.close();
		pst.close();
		
		
		StringBuilder sbQuery = new StringBuilder(" select a.*,wli.wlocation_name from (select a.*,level_name from (select a.*,dd.designation_name,level_id from (select * from(select a.*,di.dept_name from(select * from(SELECT *, datediff(yy, joining_date, ?) as experience,CAST(datediff(dd, joining_date, ?) as float)/365 as current_experience FROM employee_personal_details epd , employee_official_details eod where epd.emp_per_id=eod.emp_id and epd.is_alive = 1 )a where emp_per_id>0 ");
		if(uF.parseToInt(getF_org())>0){
			sbQuery.append(" and a.org_id="+uF.parseToInt(getF_org()));
		}			
		if(uF.parseToInt(getF_strWLocation())>0){
			sbQuery.append(" and a.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
		}
		if(uF.parseToInt(getF_department())>0){
			sbQuery.append(" and a.depart_id = "+uF.parseToInt(getF_department()));			
		}
		if(uF.parseToInt(getLevel())>0){
			sbQuery.append(" and a.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getLevel())+")");
		}
		
		
		sbQuery.append(" )a LEFT JOIN department_info di  on di.dept_id=a.depart_id )a LEFT JOIN grades_details gd  on a.grade_id=gd.grade_id )a LEFT JOIN designation_details dd on a.designation_id=dd.designation_id)a LEFT JOIN level_details ld on a.level_id=ld.level_id)a LEFT JOIN work_location_info wli on a.wlocation_id=wli.wlocation_id order by emp_fname,emp_lname");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
		pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
		rs = pst.executeQuery();
		List<List<String>> outerList=new ArrayList<List<String>>();
		while (rs.next()) {
			List<String> alBirthDays = new ArrayList<String>();
				alBirthDays.add(rs.getString("emp_per_id"));
				alBirthDays.add(rs.getString("empcode"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				alBirthDays.add(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				alBirthDays.add(uF.showData(rs.getString("emp_gender"),"N/A"));
				alBirthDays.add(rs.getString("dept_name")!=null?rs.getString("dept_name"):"-");
				alBirthDays.add(rs.getString("designation_name")!=null?rs.getString("designation_name"):"-");
				alBirthDays.add(rs.getString("grade_name")!=null?rs.getString("grade_name"):"-");
				alBirthDays.add(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT));
				alBirthDays.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE,DATE_FORMAT));
//				alBirthDays.add(rs.getString("level_name")!=null?rs.getString("level_name"):"-");
				
				alBirthDays.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("current_experience"))));
				double recievedAmt=uF.parseToDouble(empGratuityMap.get(rs.getString("emp_per_id")));

				

				if(uF.parseToDouble(rs.getString("current_experience"))>=uF.parseToDouble(gratuityMap.get("SERVICE_FROM"))){
					
					
					double amount=uF.parseToDouble(empSalaryMap.get(rs.getString("emp_per_id")))*uF.parseToDouble(gratuityMap.get("DAYS"))*uF.parseToDouble(rs.getString("current_experience"))/26;
					
					double maxAmount=uF.parseToDouble(gratuityMap.get("MAX_AMOUNT"));
					if(amount<maxAmount){
						alBirthDays.add(uF.formatIntoOneDecimal(amount));
						alBirthDays.add(uF.formatIntoOneDecimal(amount-recievedAmt));
					}else{
						alBirthDays.add(uF.formatIntoOneDecimal(maxAmount));
						alBirthDays.add(uF.formatIntoOneDecimal(maxAmount-recievedAmt));

					}
					
				}else{
					alBirthDays.add("0");
					alBirthDays.add("0");

				}
				alBirthDays.add(uF.showData(empGratuityMap.get(rs.getString("emp_per_id")), "0"));
				
			
			outerList.add(alBirthDays);
		}
		rs.close();
		pst.close();
		
		request.setAttribute("outerList",outerList);
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}
@Override
public void setServletRequest(HttpServletRequest request) {
	this.request = request;
}
}