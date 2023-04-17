package com.konnect.jpms.recruitment;

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
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetHiringEmployeeList extends ActionSupport implements
		ServletRequestAware, IStatements {
	
	HttpSession session;
	CommonFunctions CF;
	
	String grade;
	String location;
	String depart;
	
	String strOrg;
	String level;
	String design;
	String supervisor;
	
	List<FillEmployee> empList;
	List<FillWLocation> wlocationList;
	List<FillDepartment> departList;
	List<FillLevel> levelList;
	List<FillDesig> desigList;
	UtilityFunctions uF = new UtilityFunctions();
	
	private static final long serialVersionUID = 1L;

	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		
		if(strOrg != null){
			wlocationList = new FillWLocation(request).fillWLocation(getStrOrg());
			departList = new FillDepartment(request).fillDepartment(uF.parseToInt(getStrOrg()));
			levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
			desigList = new FillDesig(request).fillDesig(uF.parseToInt(getStrOrg()));
		}
//		FillWLocation	
//		FillDepartment
//		FillLevel
//		FillDesig
//		FillDesignation
		empList=getEmployeeList();
//		System.out.println("GHEL/68--empList="+empList.toString());
		
		return SUCCESS;

	}

	private List<FillEmployee> getEmployeeList() {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
//		System.out.println("StrOrg ===>  " +strOrg);
		try {

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String,String> hmEmpLocation=CF.getEmpWlocationMap(con);
			Map<String, String> hmWLocation =CF.getWLocationMap(con, null, null); 
			Map<String, String> hmEmpCodeDesig =CF.getEmpDesigMap(con);
			request.setAttribute("hmEmpLocation", hmEmpLocation);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and is_alive=true ");
			
			if(getStrOrg()!=null && !getStrOrg().equals("")){
				sbQuery.append(" and eod.org_id in ("+getStrOrg()+") ");
			}
			if(getLocation()!=null && !getLocation().equals("")){
				sbQuery.append(" and eod.wlocation_id in ("+getLocation()+") ");
			}
			if(getDepart()!=null && !getDepart().equals("")){
				sbQuery.append(" and eod.depart_id in ("+getDepart()+") ");
			}
			if(getLevel()!=null && !getLevel().equals("")){
				sbQuery.append(" and eod.grade_id in (SELECT grade_id FROM grades_details where designation_id in " +
						" (SELECT designation_id FROM designation_details  WHERE level_id in (" + getLevel()+ "))) ");
			}
			if(getDesign()!=null && !getDesign().equals("")){
				sbQuery.append(" and eod.grade_id in (SELECT grade_id FROM grades_details where designation_id in " +
						" (SELECT designation_id FROM designation_details  WHERE designation_id in (" + getDesign() + ")))  ");
			}
			if(getGrade()!=null && !getGrade().equals("")){
				sbQuery.append("  and eod.grade_id in(SELECT grade_id FROM grades_details where grade_id in (" + getGrade()+ ") ) ");
			}
			/*if(getSupervisor()!=null && !getSupervisor().equals("")){
				String supervisor = getSupervisor().substring(1, getSupervisor().length()-1);
				sbQuery.append(" and eod.supervisor_emp_id in ("+supervisor+") ");
			}*/
			
		//===start parvez date: 19-10-2021===
			sbQuery.append(" and eod.emp_id not in (select emp_id from emp_off_board where approved_1=1 and approved_2=1)");
		//===end parvez date: 19-10-2021===
			
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("GHEL/124--pst=====>"+pst);
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDepart() {
		return depart;
	}

	public void setDepart(String depart) {
		this.depart = depart;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getDesign() {
		return design;
	}

	public void setDesign(String design) {
		this.design = design;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public List<FillWLocation> getWlocationList() {
		return wlocationList;
	}

	public void setWlocationList(List<FillWLocation> wlocationList) {
		this.wlocationList = wlocationList;
	}

	public List<FillDepartment> getDepartList() {
		return departList;
	}

	public void setDepartList(List<FillDepartment> departList) {
		this.departList = departList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}