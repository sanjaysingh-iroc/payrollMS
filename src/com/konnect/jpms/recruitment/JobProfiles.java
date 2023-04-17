package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class JobProfiles extends ActionSupport implements ServletRequestAware,
		IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(RequirementApproval.class);
	String strSessionEmpId = null;
	
	List<FillOrganisation> organisationList;
	String f_org;

	public String execute() throws Exception { 
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, "Job Profiles");
		request.setAttribute(PAGE, "/jsp/recruitment/JobProfiles.jsp");

		if(getLocation()==null){
			setLocation((String)session.getAttribute(WLOCATIONID));
		}
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		 
		organisationList = new FillOrganisation(request).fillOrganisation();
		workLocationList = new FillWLocation(request).fillWLocation(getF_org());
		desigList = new FillDesig(request).fillDesig();
		gradeList = new FillGrade(request).fillGrade();
		serviceslist = new FillServices(request).fillServices();
		
		
		viewAllJobProfilesList(uF);
		return LOAD;

	}
	List<FillDesig> desigList;
	List<FillGrade> gradeList;
	List<FillWLocation> workLocationList;
	List<FillServices> serviceslist;

	String empGrade;
	String location;
	String designation;
	String services;
	String checkStatus;
	String fdate;
	String tdate;
	
	
	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public List<FillWLocation> getWorkLocationList() {
		return workLocationList;
	}

	public void setWorkLocationList(List<FillWLocation> workLocationList) {
		this.workLocationList = workLocationList;
	}

	public List<FillServices> getServiceslist() {
		return serviceslist;
	}

	public void setServiceslist(List<FillServices> serviceslist) {
		this.serviceslist = serviceslist;
	}

	public String getEmpGrade() {
		return empGrade;
	}

	public void setEmpGrade(String empGrade) {
		this.empGrade = empGrade;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getServices() {
		return services;
	}

	public void setServices(String services) {
		this.services = services;
	}

	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}

	public String getFdate() {
		return fdate;
	}

	public void setFdate(String fdate) {
		this.fdate = fdate;
	}

	public String getTdate() {
		return tdate;
	}

	public void setTdate(String tdate) {
		this.tdate = tdate;
	}

	private void viewAllJobProfilesList(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);

		List<List<String>> requestList = new ArrayList<List<String>>();
		List<String> innerRequestList;
		int depart_id = 0;
		int wlocation_id = 0;
		try {
			con = db.makeConnection(con);
			/*if (getFdate() == null && getTdate() == null) {

				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(CF
						.getStrTimeZone()));
				int nMaxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				int nMinDate = cal.getActualMinimum(Calendar.DAY_OF_MONTH);

				setFdate(uF.getDateFormat(
						nMinDate + "/" + (cal.get(Calendar.MONTH) + 1) + "/"
								+ cal.get(Calendar.YEAR), DATE_FORMAT,
						DATE_FORMAT));
				setTdate(uF.getDateFormat(
						nMaxDate + "/" + (cal.get(Calendar.MONTH) + 1) + "/"
								+ cal.get(Calendar.YEAR), DATE_FORMAT,
						DATE_FORMAT));

			}*/
			
			//querying for planned records in company 
			Map<String,String> hmPlannedCount=new HashMap<String,String>();
			pst=con.prepareStatement("select recruitment_id,resource_requirement from recruitment_details rd," +
					"resource_planner_details rpd where rpd.designation_id=rd.designation_id and" +
					" date_part('year', effective_date)=ryear and date_part('month', effective_date)=rmonth");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				hmPlannedCount.put(rst.getString("recruitment_id"),rst.getString("resource_requirement"));
			}
			rst.close();
			pst.close();
			
			
			Map<String,String> hmEmpName=CF.getEmpNameMap(con,null,null);
			
	
			String query1 = "select depart_id,wlocation_id from employee_official_details where emp_id=?";
			pst = con.prepareStatement(query1);
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
			while (rst.next()) {
				depart_id = rst.getInt(1);
				wlocation_id = rst.getInt(2);
			}
			rst.close();
			pst.close();
			
			// grade_id grade_code grades_details wlocation_id wlocation_name
			// work_location_info
			
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select r.recruitment_id,job_profile_updated_by,job_profile_updated_date,d.designation_name,g.grade_code,w.wlocation_name,r.no_position,r.custum_designation,r.existing_emp_count, r.effective_date,r.job_code,s.service_name,di.dept_name,r.job_approval_status from recruitment_details r left join work_location_info w on(r.wlocation=w.wlocation_id) left join employee_personal_details e on (r.added_by=e.emp_per_id) left join designation_details d using(designation_id) left  join services s on (r.services=s.service_id) left join department_info di on (r.dept_id=di.dept_id) left  join grades_details g on (r.grade_id=g.grade_id) where r.status=1 ");
			
			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN))) {
				
			}else if (strUserType != null
					&& (strUserType.equalsIgnoreCase(MANAGER))) {
				strQuery.append(" and r.wlocation=" + wlocation_id);
				strQuery.append(" and r.dept_id=" + depart_id);
				strQuery.append(" and r.added_by="+uF.parseToInt(strSessionEmpId));
			}
			if (uF.parseToInt(getLocation()) > 0) {
				strQuery.append(" and r.wlocation="
						+ uF.parseToInt(getLocation()));
			}
			if (uF.parseToInt(getF_org())> 0) {
				strQuery.append(" and w.org_id="
						+uF.parseToInt(getF_org()));
			}
			if (uF.parseToInt(getEmpGrade()) > 0) {
		
				strQuery.append("and r.grade_id="
						+ uF.parseToInt(getEmpGrade()));
			}
			if (uF.parseToInt(getDesignation()) > 0) {
		
				strQuery.append(" and r.designation_id="
						+ uF.parseToInt(getDesignation()));
			}
			if (uF.parseToInt(getServices()) > 0) {
		
				strQuery.append(" and r.services="
						+ uF.parseToInt(getServices()));
			}
			if ((getFdate() != null && !getFdate().equals(""))
					&& (getTdate() != null && !getTdate().equals(""))) {
		
		
				strQuery.append(" and r.effective_date >='"
						+ uF.getDateFormat(getFdate(), DATE_FORMAT)
						+ "' and r.effective_date <='"
						+ uF.getDateFormat(getTdate(), DATE_FORMAT) + "' ");
			}
			/*if (getCheckStatus() != null && uF.parseToInt(getCheckStatus()) > -2) {
				System.out.println("in status");
				System.out.println("in status==>" + getCheckStatus());
				strQuery.append(" and r.status = " + getCheckStatus() + " ");
			}*/

			strQuery.append(" order by r.job_code desc");
			
			
			pst = con.prepareStatement(strQuery.toString());
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			int nCount = 0;
			while (rst.next()) {
				innerRequestList = new ArrayList<String>();
				innerRequestList.add(rst.getString("job_code"));
				innerRequestList.add(rst.getString("designation_name"));
				innerRequestList.add(uF.showData(rst.getString("grade_code"),""));
				innerRequestList.add(uF.showData(rst.getString("dept_name"),""));
				innerRequestList.add(uF.showData(rst.getString("service_name"),""));
				innerRequestList.add(uF.showData(rst.getString("wlocation_name"),""));
				innerRequestList.add(rst.getString("no_position"));
				innerRequestList.add(uF.getDateFormat(rst.getString("effective_date"), DBDATE, DATE_FORMAT));
				innerRequestList.add(rst.getString("job_approval_status"));
				innerRequestList.add(rst.getString("recruitment_id"));
				
				innerRequestList.add(uF.getDateFormat(rst.getString("job_profile_updated_date"), DBDATE, DATE_FORMAT));
				

				innerRequestList.add(uF.showData(hmEmpName.get(rst.getString("job_profile_updated_by")),"-"));

				
				innerRequestList.add(rst.getString("existing_emp_count"));
				innerRequestList.add(uF.showData(hmPlannedCount.get(rst.getString("recruitment_id")), "0"));
				
				requestList.add(innerRequestList);
				nCount++;
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		request.setAttribute("requestList", requestList);
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

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}
}
