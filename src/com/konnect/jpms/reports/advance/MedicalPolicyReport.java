package com.konnect.jpms.reports.advance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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

public class MedicalPolicyReport extends ActionSupport implements ServletRequestAware, IStatements {

	CommonFunctions CF = null;
	HttpSession session;
	String salaryHead;
	List<FillOrganisation> orgList;	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	String f_department;
	String f_strWLocation;
	String f_org;
	
	public String getSalaryHead() {
		return salaryHead;
	}

	public void setSalaryHead(String salaryHead) {
		this.salaryHead = salaryHead;
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
		request.setAttribute(PAGE, "/jsp/reports/advance/MedicalPolicyReport.jsp");
		request.setAttribute(TITLE, "Medical/LTA Report");
		
		
		
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
		
		if(getSalaryHead()==null){
			setSalaryHead(LTA+"");
		}
		request.setAttribute("salaryHead", salaryHead);
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
		
		pst = con.prepareStatement("select csh.*,epd.empcode,epd.emp_fname,emp_mname,emp_lname from ctc_salary_head csh, employee_personal_details epd where salary_head_id=? and csh.emp_id=epd.emp_per_id");
		pst.setInt(1, uF.parseToInt(getSalaryHead()));
		rs=pst.executeQuery();
		List<List<String>> reportList=new ArrayList<List<String>>();
		while(rs.next()){
			List<String> innerList=new ArrayList<String>();
			innerList.add(rs.getString("ctc_salary_head_id"));
			innerList.add(rs.getString("emp_id"));
			innerList.add(rs.getString("empcode"));
			
			String strEmpMName = "";
			if(flagMiddleName) {
				if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
					strEmpMName = " "+rs.getString("emp_mname");
				}
			}
			
			innerList.add(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
			innerList.add(rs.getString("opening_amount"));
			innerList.add(rs.getString("paid_amount"));
			innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
			innerList.add(uF.parseToDouble(rs.getString("opening_amount"))- uF.parseToDouble(rs.getString("paid_amount"))+"");
			reportList.add(innerList);
		}
		rs.close();
		pst.close();
		
		request.setAttribute("reportList",reportList);
		
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
