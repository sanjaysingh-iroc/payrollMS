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

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class OpenJobReport extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(OpenJobReport.class);

	String strSessionEmpId = null;
	UtilityFunctions uF = new UtilityFunctions();

	List<FillEducational> eduList;
	List<FillSkills> skillsList ;
	List<FillWLocation> workList;
	String f_wlocation;
	List<FillDesig> desigList;
	List<FillGrade> gradeList;
	List<FillWLocation> workLocationList;
	List<FillServices> serviceslist;
	List<FillOrganisation> organisationList;
	
	String f_org;
	String empGrade;
	String location;
	String designation;
	String services;
	String fdate;
	String tdate;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		/*
		 * boolean isView = CF.getAccess(session, request, uF); if(!isView){
		 * request.setAttribute(PAGE, PAccessDenied);
		 * request.setAttribute(TITLE, TAccessDenied); return ACCESS_DENIED; }
		 */
		
		if(getF_wlocation()==null){
			setF_wlocation((String)session.getAttribute(WLOCATIONID));
		}
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getStrSkills()==null)
		{
			setStrSkills(new String[1]);
			strSkills[0]="";
			
		}
		if(getStrMinEducation()==null)
		{
			setStrMinEducation(new String[1]);
			strMinEducation[0]="";
			
		}
		organisationList = new FillOrganisation(request).fillOrganisation();
		workLocationList = new FillWLocation(request).fillWLocation(getF_org());
		if(getLocation()==null){
			setLocation(workLocationList.get(0).getwLocationId());
		}

		desigList = new FillDesig(request).fillDesig(uF.parseToInt(getF_org()));
		
		
		gradeList = new FillGrade(request).fillGrade();
		serviceslist = new FillServices(request).fillServices();
		
		
		workList = new FillWLocation(request).fillWLocation(getF_org());
		skillsList = new FillSkills(request).fillSkills();
		eduList = new FillEducational(request).fillEducationalQual();
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, "Open Jobs");
		request.setAttribute(PAGE, "/jsp/recruitment/openjobreport.jsp");

		preparejobreport();
//		prepareCandidateStats();
		return LOAD;

	}

	public void preparejobreport() {

		List<List<String>> alopenjobreport = new ArrayList<List<String>>();

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		int wlocation_id = 0;
		Map<String, String> hmpanelname = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);

			String query1 = "select depart_id,wlocation_id from employee_official_details where emp_id=?";
			pst = con.prepareStatement(query1);
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()) {
				wlocation_id = rst.getInt(2);
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("Select recruitment_id,panel_employee_id from recruitment_details where job_approval_status=1");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				hmpanelname.put(rst.getString("recruitment_id"), uF.showData(getAppendData(con,rst.getString("panel_employee_id")), ""));
			}
			rst.close();
			pst.close();
			
			// preeparing finalised application i.e ACCEPTED
			Map<String, String> hmAccepted = new HashMap<String, String>();
			pst = con.prepareStatement("select rd.recruitment_id,count(*) as count from candidate_personal_details "
							+ "join recruitment_details rd using(job_code) where candidate_status=1 and "
							+ "application_status=2 and candidate_final_status=1  group by rd.recruitment_id");

			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				hmAccepted.put(rst.getString("recruitment_id"), rst.getString("count"));
			}
			rst.close();
			pst.close();

			Map<String, String> hmApplications = new HashMap<String, String>();
			Map<String, String> hmShortlisted = new HashMap<String, String>();
			Map<String, String> hmRejected = new HashMap<String, String>();
			Map<String, String> hmFinalised = new HashMap<String, String>();
			int dblTotalApplication = 0;

			pst = con.prepareStatement("select  count(*) as count,recruitment_id, application_status, candidate_final_status from candidate_personal_details  group by recruitment_id, application_status, candidate_final_status");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {

				if (hmApplications.get(rst.getString("recruitment_id")) != null) {
					dblTotalApplication = uF.parseToInt(hmApplications.get(rst.getString("recruitment_id")));
					dblTotalApplication += uF.parseToInt(rst.getString("count"));
					hmApplications.put(rst.getString("recruitment_id"), String.valueOf(dblTotalApplication));
				} else {

					hmApplications.put(rst.getString("recruitment_id"), rst.getString("count"));
				}

				if (uF.parseToInt(rst.getString("application_status")) == 2) {

					int approvecount = uF.parseToInt(uF.showData(hmShortlisted.get(rst.getString("recruitment_id")), "0"));
					approvecount += uF.parseToInt(rst.getString("count"));
					hmShortlisted.put(rst.getString("recruitment_id"), "" + approvecount);

				} else if (uF.parseToInt(rst.getString("application_status")) == -1) {

					hmRejected.put(rst.getString("recruitment_id"), rst.getString("count"));
				}
				if (uF.parseToInt(rst.getString("candidate_final_status")) == 1 && uF.parseToInt(rst.getString("application_status")) == 2) {

					hmFinalised.put(rst.getString("recruitment_id"), rst.getString("count"));
				}
			}
			rst.close();
			pst.close();
			
			StringBuilder sbQquery = new StringBuilder();
			sbQquery.append("select designation_name,priority_job_int,job_code,recruitment_id,no_position,custum_designation " +
					"from recruitment_details left join designation_details using(designation_id) where job_approval_status=1 ");

			if (uF.parseToInt(getLocation()) > 0) {
				sbQquery.append(" and wlocation="+ uF.parseToInt(getLocation()));
			}
			if (uF.parseToInt(getF_org())> 0) {
				sbQquery.append(" and org_id="+uF.parseToInt(getF_org()));
			}
			if (uF.parseToInt(getEmpGrade()) > 0) {
				sbQquery.append("and grade_id="+ uF.parseToInt(getEmpGrade()));
			}
			if (uF.parseToInt(getDesignation()) > 0) {
				sbQquery.append(" and designation_id="+ uF.parseToInt(getDesignation()));
			}
			if (uF.parseToInt(getServices()) > 0) {
				sbQquery.append(" and services="+ uF.parseToInt(getServices()));
			}
			if (getReportStatus() == null || getReportStatus().equalsIgnoreCase("1")) {
				sbQquery.append("and close_job_status='f'");
			} else if (getReportStatus().equalsIgnoreCase("-1")) {
				sbQquery.append("and close_job_status='t' ");
			}
			if ((getFdate() != null && !getFdate().equals(""))
					&& (getTdate() != null && !getTdate().equals(""))) {
				sbQquery.append(" and effective_date >='" + uF.getDateFormat(getFdate(), DATE_FORMAT)
						+ "' and effective_date <='" + uF.getDateFormat(getTdate(), DATE_FORMAT) + "' ");
			}
			
			//sbQquery.append(" order by job_code desc");
			sbQquery.append(" order by job_approval_status desc,recruitment_id desc");
        
			pst = con.prepareStatement(sbQquery.toString());
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("pst=====>"+pst);
			while (rst.next()) {
				List<String> job_code_info = new ArrayList<String>();

				job_code_info.add(rst.getString("recruitment_id"));
				job_code_info.add(rst.getString("priority_job_int"));
				job_code_info.add(rst.getString("job_code"));
				job_code_info.add(rst.getString("designation_name"));
				job_code_info.add(hmpanelname.get(rst.getString("recruitment_id")));
				job_code_info.add(rst.getString("no_position"));
				job_code_info.add(uF.showData(hmApplications.get(rst.getString("recruitment_id")), "0"));
				job_code_info.add(uF.showData(hmAccepted.get(rst.getString("recruitment_id")), "0"));
				job_code_info.add(uF.showData(hmFinalised.get(rst.getString("recruitment_id")), "0"));
				job_code_info.add(uF.showData(hmShortlisted.get(rst.getString("recruitment_id")), "0"));
				job_code_info.add(uF.showData(hmRejected.get(rst.getString("recruitment_id")), "0"));

				alopenjobreport.add(job_code_info);
			}
			rst.close();
			pst.close();

			request.setAttribute("alopenjobreport", alopenjobreport);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private String getAppendData(Connection con, String strID) {
		StringBuilder sb = new StringBuilder();
//		EncryptionUtils encryption = new EncryptionUtils();// Created By Dattatray Date : 20-July-2021 Note : Encryption
		if (strID != null && !strID.equals("")) {
			int flag=0;
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);

				String[] temp = strID.split(",");

				for (int i =0; i < temp.length; i++) {
	
					if(temp[i]!=null && !temp[i].equals("")){
					 if(flag==0){  //encryption.encrypt(temp[i])
						 sb.append("<a href=\"MyProfile.action?empId="+temp[i]+"\">"+hmEmpName.get(temp[i].trim())+"</a>");// Created By Dattatray Date : 20-July-2021 Note : empId Encryption
						 flag=1;
					 }else{	
						sb.append(", " +"<a href=\"MyProfile.action?empId="+temp[i]+"\">"+ hmEmpName.get(temp[i].trim())+"</a>");// Created By Dattatray Date : 20-July-2021 Note : empId Encryption
					 }
					}
			}

		}

		return sb.toString();
	}

	private HttpServletRequest request;

	String reportStatus;

	public String getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public List<FillEducational> getEduList() {
		return eduList;
	}

	public void setEduList(List<FillEducational> eduList) {
		this.eduList = eduList;
	}

	public List<FillSkills> getSkillsList() {
		return skillsList;
	}

	public void setSkillsList(List<FillSkills> skillsList) {
		this.skillsList = skillsList;
	}
	

		String	strExperience;
		String[]	strSkills;
		String[]	strMinEducation;
		
		public String getStrExperience() {
			return strExperience;
		}
		
		public void setStrExperience(String strExperience) {
			this.strExperience = strExperience;
		}

		
		public String[] getStrSkills() {
			return strSkills;
		}

		public void setStrSkills(String[] strSkills) {
			this.strSkills = strSkills;
		}

		public String[] getStrMinEducation() {
			return strMinEducation;
		}

		public void setStrMinEducation(String[] strMinEducation) {
			this.strMinEducation = strMinEducation;
		}

		public List<FillWLocation> getWorkList() {
			return workList;
		}

		public void setWorkList(List<FillWLocation> workList) {
			this.workList = workList;
		}

		public String getF_wlocation() {
			return f_wlocation;
		}

		public void setF_wlocation(String f_wlocation) {
			this.f_wlocation = f_wlocation;
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


		
}
