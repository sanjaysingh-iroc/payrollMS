package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import com.konnect.jpms.select.FillBloodGroup;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillDesignation;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class BloodGroupReport extends ActionSupport implements IConstants, ServletRequestAware {
	HttpSession session;
	CommonFunctions CF = null;
	String strUserType;
	private String f_org;
	private String strDepartment;
	private String strDesignation;
	private String strBloodGroup;
	private String strEmployeType;
	private List<FillOrganisation> organisationList;
	private List<FillDepartment> departmentList;
	private List<FillDesig> designationList;
	private List<FillBloodGroup> bloodGroupList;
	private List<FillEmploymentType> employementTypeList;
	private String[] f_department;
	private String[] f_designation;
	private String[] f_bloodGroup;
	private String[] f_employeType;
	private String fromPage;
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;//
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/reports/BloodGroupReport.jsp");
		request.setAttribute(TITLE, "BloodGroup Report");

		if (getF_org() == null || getF_org().trim().equals("")) {
			setF_org((String) session.getAttribute(ORGID));
		}
		if (getStrDepartment() != null && !getStrDepartment().equals("") && !getStrDepartment().equalsIgnoreCase("null")) {
			setF_department((getStrDepartment().split(",")));
		}
		if (getStrDesignation() != null && !getStrDesignation().equals("") && !getStrDesignation().equalsIgnoreCase("null")) {
			setF_designation((getStrDesignation().split(",")));
		}
		if (getStrBloodGroup() != null && !getStrBloodGroup().equals("") && !getStrBloodGroup().equalsIgnoreCase("null")) {
			setF_bloodGroup((getStrBloodGroup().split(",")));
		}
		if (getStrEmployeType() != null && !getStrEmployeType().equals("") && !getStrEmployeType().equalsIgnoreCase("null")) {
			setF_employeType((getStrEmployeType().split(",")));
		}
		
		viewBloodGroupReport(uF);
		loadAttendanceReport(uF);
		
		if (getFromPage() != null && getFromPage().equalsIgnoreCase("ajax")) {
			return VIEW;
		}
		return LOAD;

	};

	public void loadAttendanceReport(UtilityFunctions uF) {

		if (strUserType != null) {
		}
		organisationList = new FillOrganisation(request).fillOrganisation();
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
//		System.out.println("==>DepartmentList" + departmentList.size());
		designationList = new FillDesig(request).fillDesig(uF.parseToInt(getF_org()));
//		System.out.println("==>designation" + designationList.size());
		bloodGroupList = new FillBloodGroup().fillBloodGroup();
//		System.out.println("==>bloodGroup" + bloodGroupList.size());
		employementTypeList = new FillEmploymentType().fillEmploymentType(request);
//		for (int i = 0; i < employementTypeList.size(); i++) {
//			System.out.println("==>Employe" + employementTypeList.get(i).getEmpTypeId());
//		}
//		System.out.println("==>Employe" + employementTypeList);
		getSelectedFilter(uF);

	}

	private void viewBloodGroupReport(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		int empid = uF.parseToInt((String) session.getAttribute(EMPID));
		System.out.println("request" + empid);
		try {
			con = db.makeConnection(con);

			List<Map<String, String>> emplist = new ArrayList<Map<String, String>>();

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select  dd.designation_name ,epd.blood_group ,epd.empcode,epd.emp_fname,epd.emp_lname,di.dept_name from " +
				"employee_personal_details epd, employee_official_details eod left join grades_details gd on gd.grade_id = eod.grade_id " +
				"left join designation_details dd on dd.designation_id = gd.designation_id left join department_info di on " +
				"di.dept_id=eod.depart_id where epd.emp_per_id = eod.emp_id and epd.is_alive=true and epd.blood_group !='null' ");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append("  and eod.org_id ="+uF.parseToInt(getF_org()));
			}
			
			if (getF_designation() != null && getF_designation().length > 0) {
				sbQuery.append(" and dd.designation_id in (" + StringUtils.join(getF_designation(), ",") + ") ");
			}
			
			if (getF_department() != null && getF_department().length > 0) {
				sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}

			if (getF_bloodGroup() != null && getF_bloodGroup().length > 0) {
				sbQuery.append(" and blood_group in ( '" + StringUtils.join(getF_bloodGroup(), "' , '") + "') ");
			}

			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst====>" + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hmBloodgroupdata = new HashMap<String, String>();
				hmBloodgroupdata.put("empDepartmentName", uF.showData(rs.getString("dept_name"), ""));
				hmBloodgroupdata.put("empName", uF.showData(rs.getString("emp_fname") + " " + rs.getString("emp_lname"), ""));
				hmBloodgroupdata.put("empCode", uF.showData(rs.getString("empcode"), ""));
				hmBloodgroupdata.put("empBloodGroup", uF.showData(rs.getString("blood_group"), ""));
				hmBloodgroupdata.put("empDesignation", uF.showData(rs.getString("designation_name"), ""));
				emplist.add(hmBloodgroupdata);
			}
			rs.close();
			pst.close();
			request.setAttribute("reportList", emplist);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String, String> hmFilter = new HashMap<String, String>();
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
				hmFilter.put("DEPARTMENT", "All Department");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Department");
		}
		
		
		alFilter.add("DESIG");
		if (getF_designation() != null) {
			String strDesig = "";
			int k = 0;
			for (int i = 0; designationList != null && i < designationList.size(); i++) {
				for (int j = 0; j < getF_designation().length; j++) {
					if (getF_designation()[j].equals(designationList.get(i).getDesigId())) {
						if (k == 0) {
							strDesig = designationList.get(i).getDesigCodeName();
						} else {
							strDesig += ", " + designationList.get(i).getDesigCodeName();
						}
						k++;
					}
				}
			}
			if (strDesig != null && !strDesig.equals("")) {
				hmFilter.put("DESIG", strDesig);
			} else {
				hmFilter.put("DESIG", "All Designation");
			}
		} else {
			hmFilter.put("DESIG", "All Designation");
		}
		
		
		alFilter.add("BLOODGROUP");
		if (getF_department() != null) {
			String strBooldGroup = "";
			int k = 0;
			for (int i = 0; bloodGroupList != null && i < bloodGroupList.size(); i++) {
				for (int j = 0; j < getF_bloodGroup().length; j++) {
					if (getF_bloodGroup()[j].equals(bloodGroupList.get(i).getBloodGroupId())) {
						if (k == 0) {
							strBooldGroup = bloodGroupList.get(i).getBloodGroupName();
						} else {
							strBooldGroup += ", " + bloodGroupList.get(i).getBloodGroupName();
						}
						k++;
					}
				}
			}
			if (strBooldGroup != null && !strBooldGroup.equals("")) {
				hmFilter.put("BLOODGROUP", strBooldGroup);
			} else {
				hmFilter.put("BLOODGROUP", "All Blood Group");
			}
		} else {
			hmFilter.put("BLOODGROUP", "All Blood Group");
		}
		
		
		alFilter.add("EMPTYPE");
		if (getF_employeType() != null) {
			String stremptype = "";
			int k = 0;
			for (int i = 0; employementTypeList != null && i < employementTypeList.size(); i++) {
				for (int j = 0; j < getF_employeType().length; j++) {
					if (getF_employeType()[j].equals(employementTypeList.get(i).getEmpTypeId())) {
						if (k == 0) {
							stremptype = employementTypeList.get(i).getEmpTypeName();
						} else {
							stremptype += ", " + employementTypeList.get(i).getEmpTypeName();
						}
						k++;
					}
				}
			}
			if (stremptype != null && !stremptype.equals("")) {
				hmFilter.put("EMPTYPE", stremptype);
			} else {
				hmFilter.put("EMPTYPE", "All Employee Type");
			}
		} else {
			hmFilter.put("EMPTYPE", "All Employee Type");
		}
		
		String selectedFilter = CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}

	public List<FillDesig> getDesignationList() {
		return designationList;
	}

	public void setDesignationList(List<FillDesig> designationList) {
		this.designationList = designationList;
	}

	public String[] getF_designation() {
		return f_designation;
	}

	public void setF_designation(String[] f_designation) {
		this.f_designation = f_designation;
	}

	public String[] getF_bloodGroup() {
		return f_bloodGroup;
	}

	public void setF_bloodGroup(String[] f_bloodGroup) {
		this.f_bloodGroup = f_bloodGroup;
	}
	public List<FillBloodGroup> getBloodGroupList() {
		return bloodGroupList;
	}

	public void setBloodGroupList(List<FillBloodGroup> bloodGroupList) {
		this.bloodGroupList = bloodGroupList;
	}

	public List<FillEmploymentType> getEmployementTypeList() {
		return employementTypeList;
	}

	public void setEmployementTypeList(List<FillEmploymentType> employementTypeList) {
		this.employementTypeList = employementTypeList;
	}

	public String[] getF_employeType() {
		return f_employeType;
	}

	public void setF_employeType(String[] f_employeType) {
		this.f_employeType = f_employeType;
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

	public String getStrBloodGroup() {
		return strBloodGroup;
	}

	public void setStrBloodGroup(String strBloodGroup) {
		this.strBloodGroup = strBloodGroup;
	}

	public String getStrEmployeType() {
		return strEmployeType;
	}

	public void setStrEmployeType(String strEmployeType) {
		this.strEmployeType = strEmployeType;
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

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}
}
