package com.konnect.jpms.reports.advance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.reports.EmployeeReport;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesignation;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class RetirementReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String retireFrom;
	String retireTo;
	private String strStartDate;
	private String strEndDate;
	private String strDepartment;
	private String strDesignation;
	private List<FillDepartment> departmentList;
	private List<FillDesignation> designationList;
	private String[] f_department;
	private String[] f_designation;
	String f_org;
	CommonFunctions CF=null;
	private String fromPage;
	
	private static Logger log = Logger.getLogger(EmployeeReport.class);
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		
		request.setAttribute(PAGE, "/jsp/reports/advance/RetirementReport.jsp");
		request.setAttribute(TITLE, "Retirement Report");
		System.out.println("StartDate"+strStartDate);
		System.out.println("strEndDate"+strEndDate);
		if(strStartDate != null && strStartDate.equals("")) {
			strStartDate = null;
		}
		if(strEndDate != null && strEndDate.equals("")) {
			strEndDate = null;
		}
		if (getStrDepartment() != null && !getStrDepartment().equals("") && !getStrDepartment().equalsIgnoreCase("null")) {
			setF_department((getStrDepartment().split(",")));
		}
		System.out.println("==>Department==>" + getStrDepartment());

		if (getStrDesignation() != null && !getStrDesignation().equals("") && !getStrDesignation().equalsIgnoreCase("null")) {
			setF_designation((getStrDesignation().split(",")));
		}
		if (getF_org() == null || getF_org().trim().equals("")) {
			setF_org((String) session.getAttribute(ORGID));
		}
		viewEmployee(uF);
		 loadEmployee(uF);
		 if (getFromPage() != null && getFromPage().equalsIgnoreCase("ajax")) {
				return VIEW;
			}
		 return LOAD;
		
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String, String> hmFilter = new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		alFilter.add("DEPARTMENT");
		if (getF_department() != null) {
			String strDepartment = "";
			int k = 0;
			for (int i = 0; departmentList != null && i < departmentList.size(); i++) {
				for (int j = 0; j < getF_department().length; j++) {
					if (getF_department()[j].equals(departmentList.get(i).getDeptId())) {
						if (k == 0) {
							strDepartment = departmentList.get(i).getDeptName();
						} else {
							strDepartment += ", " + departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if (strDepartment != null && !strDepartment.equals("")) {
				hmFilter.put("DEPARTMENT", strDepartment);
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}

		String selectedFilter = CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	public void loadEmployee(UtilityFunctions uF) {
		
		
		if(uF.parseToInt(getF_org())>0){
			
			departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
			System.out.println("==>DepartmentList" + departmentList.size());
			designationList = new FillDesignation(request).fillDesignation();
			System.out.println("==>designation" + designationList.size());
			
		}
		//orgList = new FillOrganisation(request).fillOrganisation();
		getSelectedFilter(uF);
		
	}

	public String viewEmployee(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
		try {
					con = db.makeConnection(con); 	
					
					Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
					boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
					
					
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select date_part('year',age('"+timeStamp+"',epd.emp_date_of_birth)) as age,epd.empcode,epd.emp_fname,epd.emp_mname, epd.emp_lname,epd.joining_date,di.dept_name ,dd.designation_name from employee_personal_details epd, employee_official_details eod left join grades_details gd on gd.grade_id = eod.grade_id left join designation_details dd on dd.designation_id = gd.designation_id left join department_info di on di.dept_id=eod.depart_id where epd.emp_per_id = eod.emp_id and epd.is_alive=true ");
					if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
						sbQuery.append(" and 60 < date_part('year',age('"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' , epd.emp_date_of_birth))");
						sbQuery.append("  and 60 < date_part('year',age('"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' , epd.emp_date_of_birth))");	
					}else {
						sbQuery.append("and 60 < date_part('year',age('"+timeStamp+"',epd.emp_date_of_birth)) and 60 < date_part('year',age('2016-06-15',epd.emp_date_of_birth))");
					}
					con.createStatement();
					pst = con.prepareStatement(sbQuery.toString());
					System.out.println("Query=====>"+pst.toString());
					rs=pst.executeQuery();
					System.out.println("ResultSet=====>"+rs.toString());
					List<Map<String, String>> outerList=new ArrayList<Map<String, String>>();
					
					while(rs.next()){
						Map<String, String> valuesMap = new HashMap<String, String>();
						valuesMap.put("empCode",rs.getString("empcode"));
						
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
					
						
						valuesMap.put("empName",rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
					//	System.out.println("Name"+rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
						valuesMap.put("deptName",rs.getString("dept_name")!=null?rs.getString("dept_name"):"-");
						valuesMap.put("designationName",rs.getString("designation_name")!=null?rs.getString("designation_name"):"-");
						valuesMap.put("age",rs.getString("age"));
						valuesMap.put("joining_date",rs.getString("joining_date"));
						outerList.add(valuesMap);
					}
					rs.close();
					pst.close();
					
					for (int i = 0; i < outerList.size(); i++) {
						System.out.println("Name of Emp"+outerList.get(i).get("empName"));
					}
	
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
	
	
	public String getRetireFrom() {
		return retireFrom;
	}

	public void setRetireFrom(String retireFrom) {
		this.retireFrom = retireFrom;
	}

	public String getRetireTo() {
		return retireTo;
	}

	public void setRetireTo(String retireTo) {
		this.retireTo = retireTo;
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

	
	
	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillDesignation> getDesignationList() {
		return designationList;
	}

	public void setDesignationList(List<FillDesignation> designationList) {
		this.designationList = designationList;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}

	public String[] getF_designation() {
		return f_designation;
	}

	public void setF_designation(String[] f_designation) {
		this.f_designation = f_designation;
	}
	
	
	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrDesignation() {
		return strDesignation;
	}

	public void setStrDesignation(String strDesignation) {
		this.strDesignation = strDesignation;
	}
	
	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
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
	
	public String getFromPage() {
		return fromPage;
	}


	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
}
