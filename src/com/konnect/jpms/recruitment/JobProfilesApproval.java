package com.konnect.jpms.recruitment;

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

public class JobProfilesApproval extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;

	String strSessionEmpId = null;
	String strEmpOrgId = null; 
	private List<FillOrganisation> organisationList;
	private String f_org;
	private String strOrg;
	private String alertStatus;
	private String alert_type;
	
	private String alertID;
	private String currUserType;
	private List<FillDesig> desigList;
	private List<FillGrade> gradeList;
	private List<FillWLocation> workLocationList;
	private List<FillServices> serviceslist;

	private String empGrade;
	private String location;
	private String location1;
	private String designation;
	private String services;
	private String checkStatus;
	private String fdate;
	private String tdate;
	private String fromPage;
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);

			 if(getLocation()==null && getLocation1()!=null){
			 setLocation(getLocation1()); 
		 }

			setStrOrg(getF_org());

		organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		workLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));

		desigList = new FillDesig(request).fillDesigByOrgOrAccessOrg(uF.parseToInt(getF_org()), (String)session.getAttribute(ORG_ACCESS));
		gradeList = new FillGrade(request).fillGrade();
		serviceslist = new FillServices(request).fillServices(getF_org(), uF);

		viewAllJobProfilesList(uF);
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		getSelectedFilter(uF);
		
		System.out.println("getFromPage() ========>> " + getFromPage());
		return LOAD;

	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if(strUserType != null && !strUserType.equals(MANAGER) && !strUserType.equals(EMPLOYEE)) {
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
			
			alFilter.add("LOCATION");
			if(getLocation()!=null) {
				String strLocation="";
				for(int i=0;workLocationList!=null && i<workLocationList.size();i++) {
					if(getLocation().equals(workLocationList.get(i).getwLocationId())) {
						strLocation=workLocationList.get(i).getwLocationName();
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
			
			alFilter.add("DESIG");
			if(getDesignation()!=null) {
				String strDesig="";
				for(int i=0;desigList!=null && i<desigList.size();i++) {
					if(getDesignation().equals(desigList.get(i).getDesigId())) {
						strDesig=desigList.get(i).getDesigCodeName();
					}
				}
				if(strDesig!=null && !strDesig.equals("")) {
					hmFilter.put("DESIG", strDesig);
				} else {
					hmFilter.put("DESIG", "All Designations");
				}
			} else {
				hmFilter.put("DESIG", "All Designations");
			}
			
		}

		alFilter.add("STATUS");
		if(getCheckStatus() != null && !getCheckStatus().equals("")) {
			if(uF.parseToInt(getCheckStatus())==1) { 
				hmFilter.put("STATUS", "Approved");
			} else if(uF.parseToInt(getCheckStatus())==0) {
				hmFilter.put("STATUS", "Pending");
			} else if(uF.parseToInt(getCheckStatus())== -1) {
				hmFilter.put("STATUS", "Denied");
			} else {
				hmFilter.put("STATUS", "All");
			}
		} else {
			hmFilter.put("STATUS", "All");
		}
		
		alFilter.add("FROMTO");
		if ((getFdate() != null && !getFdate().equals("") && !getFdate().equals("From Date")) && (getTdate() != null && !getTdate().equals("") && !getTdate().equals("To Date"))) {
			hmFilter.put("FROMTO", uF.getDateFormat(getFdate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getTdate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		} else {
			hmFilter.put("FROMTO", "-");
		}
		
		String selectedFilter= CF.getSelectedFilter(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	
	private void viewAllJobProfilesList(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);

		// List<List<String>> requestList = new ArrayList<List<String>>();
		List<String> requestList = new ArrayList<String>();
		List<String> innerRequestList;

		int depart_id = 0;
		int wlocation_id = 0;

		try {

			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
			String query1 = "select depart_id,wlocation_id from employee_official_details where emp_id=?";
			pst = con.prepareStatement(query1);
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			System.out.println("pst1 ===> " + pst);
			rst = pst.executeQuery();
			
			while (rst.next()) {
				depart_id = rst.getInt(1);
				wlocation_id = rst.getInt(2);
			}
			rst.close();
			pst.close();

			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);

			// querying for planned records in company
			Map<String, String> hmPlannedCount = new HashMap<String, String>();
			pst = con.prepareStatement("select recruitment_id,resource_requirement from recruitment_details rd,"
					+ "resource_planner_details rpd where rpd.designation_id=rd.designation_id and"
					+ " date_part('year', effective_date)=ryear and date_part('month', effective_date)=rmonth");
//			System.out.println("pst2 ===> " + pst);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				hmPlannedCount.put(rst.getString("recruitment_id"), rst.getString("resource_requirement"));
			}
			rst.close();
			pst.close();

			StringBuilder strQuery = new StringBuilder();

			strQuery.append("(select d.designation_id, r.existing_emp_count,r.recruitment_id,job_profile_updated_date,job_profile_updated_by,"
					+ "d.designation_name,g.grade_code,w.wlocation_name,r.no_position,r.effective_date,r.job_code,r.job_approval_status,"
					+ "di.dept_name,e.emp_fname,e.emp_mname,e.emp_lname,l.level_code,l.level_name,close_job_status,r.custum_designation," +
					"r.priority_job_int,r.added_by from recruitment_details r left join grades_details g using(grade_id)"
					+ " join work_location_info w on r.wlocation=w.wlocation_id left join employee_personal_details e on"
					+ " r.added_by=e.emp_per_id left join department_info di on r.dept_id=di.dept_id "
					+ " left  join designation_details d on r.designation_id=d.designation_id left join level_details l on r.level_id=l.level_id" 
					+ " where r.status=1 and r.job_approval_status = 0 ");

			if (strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strUserType.equalsIgnoreCase(RECRUITER))) {
//				strQuery.append(" and r.dept_id=" + depart_id);
				strQuery.append(" and (r.added_by=" + uF.parseToInt(strSessionEmpId) +" or r.hiring_manager like '%,"+strSessionEmpId+",%' )");
				// +" and r.added_by="+uF.parseToInt(strSessionEmpId)
			}

			if (uF.parseToInt(getLocation()) > 0) {
				strQuery.append(" and r.wlocation=" + uF.parseToInt(getLocation()));
			}
			if (uF.parseToInt(getF_org()) > 0) {
				strQuery.append(" and w.org_id=" + uF.parseToInt(getF_org()));
			}
			if (uF.parseToInt(getEmpGrade()) > 0) {
				strQuery.append("and r.grade_id=" + uF.parseToInt(getEmpGrade()));
			}
			if (uF.parseToInt(getDesignation()) > 0) {
				strQuery.append(" and r.designation_id=" + uF.parseToInt(getDesignation()));
			}
			
			if ((getFdate() != null && !getFdate().equals("")  && !getFdate().equals("From Date")) && (getTdate() != null && !getTdate().equals("") && !getTdate().equals("To Date"))) {

				strQuery.append(" and r.effective_date >='" + uF.getDateFormat(getFdate(), DATE_FORMAT) + "' and r.effective_date <='" + uF.getDateFormat(getTdate(), DATE_FORMAT)
						+ "' ");
			}

			if (getCheckStatus() != null && !getCheckStatus().equals("") && uF.parseToInt(getCheckStatus()) > -2) {
				strQuery.append(" and r.job_approval_status = " + getCheckStatus() + " ");
			}
			
			String strMessage="";
			
			if (strUserType != null && (strUserType.equalsIgnoreCase(MANAGER))) {
			strQuery.append(" order by r.job_approval_status desc,r.job_profile_updated_date desc,r.approved_date desc) ");
			strMessage = "waiting for profile updation";
			}else if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(RECRUITER))) {
			strQuery.append(" order by r.job_approval_status desc,r.job_profile_updated_date desc,r.approved_date desc) ");
			strMessage = "waiting for approval";
			} else {
				strQuery.append(" order by r.job_approval_status desc,r.job_profile_updated_date desc,r.job_approval_date desc,r.approved_date desc) ");
				strMessage = "waiting for approval";
			}
			
			
			strQuery.append("union all (select d.designation_id, r.existing_emp_count,r.recruitment_id,job_profile_updated_date,job_profile_updated_by,"
					+ "d.designation_name,g.grade_code,w.wlocation_name,r.no_position,r.effective_date,r.job_code,r.job_approval_status,"
					+ "di.dept_name,e.emp_fname,e.emp_mname,e.emp_lname,l.level_code,l.level_name,close_job_status,r.custum_designation,r.priority_job_int," +
					"r.added_by from recruitment_details r left join grades_details g using(grade_id)"
					+ " join work_location_info w on r.wlocation=w.wlocation_id left join employee_personal_details e on"
					+ " r.added_by=e.emp_per_id left join department_info di on r.dept_id=di.dept_id "
					+ " left  join designation_details d on r.designation_id=d.designation_id left join level_details l on r.level_id=l.level_id" 
					+ " where r.status=1 and r.job_approval_status != 0 ");

			if (strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strUserType.equalsIgnoreCase(RECRUITER))) {
//				strQuery.append(" and r.dept_id=" + depart_id);
				strQuery.append(" and (r.added_by=" + uF.parseToInt(strSessionEmpId) +" or r.hiring_manager like '%,"+strSessionEmpId+",%' )");
				// +" and r.added_by="+uF.parseToInt(strSessionEmpId)
			}

			if (uF.parseToInt(getLocation()) > 0) {
				strQuery.append(" and r.wlocation=" + uF.parseToInt(getLocation()));
			}
			if (uF.parseToInt(getF_org()) > 0) {
				strQuery.append(" and w.org_id=" + uF.parseToInt(getF_org()));
			}
			if (uF.parseToInt(getEmpGrade()) > 0) {
				strQuery.append("and r.grade_id=" + uF.parseToInt(getEmpGrade()));
			}
			if (uF.parseToInt(getDesignation()) > 0) {
				strQuery.append(" and r.designation_id=" + uF.parseToInt(getDesignation()));
			}
			
			if ((getFdate() != null && !getFdate().equals("") && !getFdate().equals("From Date")) && (getTdate() != null && !getTdate().equals("") && !getTdate().equals("To Date"))) {

				strQuery.append(" and r.effective_date >='" + uF.getDateFormat(getFdate(), DATE_FORMAT) + "' and r.effective_date <='" + uF.getDateFormat(getTdate(), DATE_FORMAT)
						+ "' ");
			}

			if (getCheckStatus() != null && !getCheckStatus().equals("") && uF.parseToInt(getCheckStatus()) > -2) {
				strQuery.append(" and r.job_approval_status = " + getCheckStatus() + " ");
			}
			
			
			if (strUserType != null && (strUserType.equalsIgnoreCase(MANAGER))) {
				strQuery.append(" order by r.job_approval_status desc,r.job_profile_updated_date desc,r.job_approval_date desc,r.approved_date desc) ");
				strMessage = "waiting for profile updation";
			}else if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(RECRUITER))) {
				strQuery.append(" order by r.job_approval_status desc,r.job_profile_updated_date desc,r.job_approval_date desc,r.approved_date desc) ");
				strMessage = "waiting for approval";
			} else{
				strQuery.append(" order by r.job_approval_status desc,r.job_profile_updated_date desc,r.job_approval_date desc,r.approved_date desc) ");
				strMessage = "waiting for approval";
			}
			int nCount = 0;
			pst = con.prepareStatement(strQuery.toString());
			System.out.println("pst3====>" + pst);
			rst = pst.executeQuery(); 
			StringBuilder sbRequirements = new StringBuilder();
			StringBuilder sbStauts = new StringBuilder();

			while (rst.next()) {

				sbRequirements.replace(0, sbRequirements.length(), "");
				sbStauts.replace(0, sbStauts.length(), "");

				innerRequestList = new ArrayList<String>();

				innerRequestList.add(rst.getString("recruitment_id"));
				innerRequestList.add(rst.getString("job_code"));
				innerRequestList.add(rst.getString("designation_name"));

				innerRequestList.add(uF.showData("[" + rst.getString("level_code") + "] " + rst.getString("level_name"), ""));

				innerRequestList.add(uF.showData(rst.getString("grade_code"), ""));
				innerRequestList.add(uF.showData(rst.getString("dept_name"), ""));
				innerRequestList.add(rst.getString("wlocation_name"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rst.getString("emp_mname");
					}
				}
				
				innerRequestList.add(rst.getString("emp_fname") +strEmpMName+ " " + rst.getString("emp_lname"));

				innerRequestList.add(rst.getString("no_position"));
				innerRequestList.add(rst.getString("existing_emp_count"));
				innerRequestList.add(uF.showData(hmPlannedCount.get(rst.getString("recruitment_id")), "0"));
				innerRequestList.add(uF.getDateFormat(rst.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()));
				innerRequestList.add(uF.getDateFormat(rst.getString("job_profile_updated_date"), DBDATE, DATE_FORMAT));
				innerRequestList.add(uF.showData(hmEmpName.get(rst.getString("job_profile_updated_by")), "-"));

				StringBuilder sbApproveDeny = new StringBuilder();
				String strnCount = String.valueOf(nCount);

				if (rst.getInt("job_approval_status") == 0) {
					
					sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus" + nCount + "\"> ");
					if (rst.getString("job_profile_updated_date") != null) {
					 /*sbStauts.append("<img src=\"images1/icons/pending.png\" title=\"Waiting for approval\" /></div>");*/
						sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for approval\" ></i></div>");
						
					}else{
						/*sbStauts.append("<img src=\"images1/icons/pullout.png\" title=\"Waiting for profile updation\" /></div>");*/
						sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\"  title=\"Waiting for profile updation\"></i></div>");
						
					}
					
			
					if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || (strUserType.equalsIgnoreCase(RECRUITER) && uF.parseToInt(rst.getString("added_by")) == uF.parseToInt(strSessionEmpId)))) {
						
						strMessage = "waiting for approval";
						sbApproveDeny.append("<div style=\"float:right;\" id=\"myDivM" + nCount + "\" > ");

						if (uF.parseToInt(rst.getString("job_profile_updated_by")) > 0) {
						
							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"approveJob('" + nCount + "','" + rst.getString("recruitment_id")
									+ "');\" ><i class=\"fa fa-check-circle checknew\" style=\"padding-top: 0px !important;\" aria-hidden=\"true\" title=\"Approve\"></i></a> ");

							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to decline this request?'))denyProfile('" + strnCount
									+ "','" + rst.getString("recruitment_id") + "');\">" + " <i class=\"fa fa-times-circle cross\" style=\"padding-top: 0px !important;\" aria-hidden=\"true\"  title=\"Decline\"></i></a> ");
						}

						sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"viewProfile('" + rst.getString("recruitment_id") + "')\">View</a> ");
						sbApproveDeny.append("</div>");

					} else { // / MANAGER 
						
						sbApproveDeny.append("<div style=\"float:right;\" id=\"myDivM" + nCount + "\" > ");
						sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"AddUpdateProfile('" + rst.getString("recruitment_id")
								+ "','"+getFromPage()+"')\">Click here to update profile</a>  ");
						sbApproveDeny.append("</div>");
					
					}

				} else if (rst.getInt("job_approval_status") == 1) {
					strMessage = "approved";
					sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus" + nCount + "\"> ");
					/*sbStauts.append("<img src=\"images1/icons/approved.png\" title=\"Approved\" /></div>");*/
					sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i></div>");
					

				} else if (rst.getInt("job_approval_status") == -1) {
					strMessage = "denied";
					sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus" + nCount + "\"> ");
					/*sbStauts.append("<img src=\"images1/icons/denied.png\" title=\"Denied\" /></div>");*/
					sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\" ></i></div>");
					

				}

				String openFont = "", closeFont = "";
				if (rst.getInt("priority_job_int") == 1) {
					openFont = "<span class=\"high\">";
					closeFont = "</span>";
				} else if (rst.getInt("priority_job_int") == 2) {
					openFont = "<span class=\"medium\">";
					closeFont = "</span>";
				} else {
					openFont = "<span class=\"low\">";
					closeFont = "</span>";
				}

				innerRequestList.add(sbApproveDeny.toString());
				innerRequestList.add(rst.getString("priority_job_int"));

				sbRequirements.append(sbStauts + openFont + "Profile against the <a href=\"javascript:void(0)\" onclick=\"reportJobProfilePopUp(" + rst.getString("recruitment_id")
					+ ")\">" + rst.getString("job_code") + "</a>  <a href=\"javascript:void(0);\" onclick=\"getDesignationDetails('"+rst.getString("designation_id")+"', '"+rst.getString("designation_name")+"')\">("
					+ rst.getString("designation_name") + ")</a>, requested by <strong>" + rst.getString("emp_fname") +strEmpMName+ " " + rst.getString("emp_lname")
					+ "</strong> for <strong>" + rst.getString("no_position") + "</strong> resources, is "+strMessage+". " + closeFont + sbApproveDeny.toString());
				requestList.add(sbRequirements.toString());

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
	    
//	    System.out.println("requestList ==>"+requestList.size());
//		System.out.println("requestList ==>"+requestList);
  
		request.setAttribute("requestList", requestList);
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}


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

	public String getLocation1() {
		return location1;
	}

	public void setLocation1(String location1) {
		this.location1 = location1;
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

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

}
