package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetEmployeeList extends ActionSupport implements ServletRequestAware, IStatements {

	private String grade;
	private String location;
	private String depart;
	
	private String strOrg;
	private String level;
	private String design;
	private String page;
	private String type;
	CommonFunctions CF=null;
	HttpSession session;

	private List<FillEmployee> empList;

	private static final long serialVersionUID = 1L;

	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		
		empList = getEmployeeList();
		
		return SUCCESS;

	}

	private List<FillEmployee> getEmployeeList() {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			UtilityFunctions uF=new UtilityFunctions();
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
//			System.out.println("getStrOrg() ===> "+getStrOrg());
//			System.out.println("getLocation() ===> "+getLocation());
//			System.out.println("getDepart() ===> "+getDepart());
//			System.out.println("getLevel() ===> "+getLevel());
//			System.out.println("getDesign() ===> "+getDesign());
//			System.out.println("getGrade() ===> "+getGrade());

			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd,user_details ud " +
					" where epd.emp_per_id=eod.emp_id and epd.is_alive = true and eod.emp_id = ud.emp_id ");
			if(getPage() != null && getPage().equals("SOrient")) {
				if(getType() != null && getType().equals("HR")) {
					sbQuery.append(" and (usertype_id = 7 or usertype_id = 1)");
				} else if(getType() != null && getType().equals("Manager")) {
					sbQuery.append(" and (usertype_id = 2 or usertype_id = 7 or usertype_id = 1)");
				} else if(getType() != null && getType().equalsIgnoreCase("CEO")) {
					sbQuery.append(" and (usertype_id = 5 or usertype_id = 1)");
				} else if(getType() != null && getType().equalsIgnoreCase("HOD")) {
					sbQuery.append(" and (usertype_id = 13 or usertype_id = 7 or usertype_id = 1)");
				} 
			}
			
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
			if(getDesign()!=null && !getDesign().equals("") && !getDesign().equals("0")){
				sbQuery.append(" and eod.grade_id in (SELECT grade_id FROM grades_details where designation_id in " +
						" (SELECT designation_id FROM designation_details  WHERE designation_id in (" + getDesign() + ")))  ");
			}
			if(getGrade()!=null && !getGrade().equals("")){
				sbQuery.append("  and eod.grade_id in(SELECT grade_id FROM grades_details where grade_id in (" + getGrade()+ ") ) ");
			}
			
			sbQuery.append(" order by epd.emp_fname");
			
			
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst getEmp==========>"+pst);
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
			
//			System.out.println("al==>"+al);
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

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
}
