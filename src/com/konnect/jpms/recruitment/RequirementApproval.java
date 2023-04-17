package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.task.FillProjectList;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class RequirementApproval extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	boolean isEmpUserType = false; 
	String strEmpOrgId = null;
	CommonFunctions CF = null;

	String strSessionEmpId = null;

	private List<FillOrganisation> organisationList;
	private String f_org;
	private String strOrg;
	private String alertStatus;
	private String alert_type;
	
	private String currUserType;
	private String alertID;
	
	
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
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId  = (String) session.getAttribute(ORGID);

		UtilityFunctions uF = new UtilityFunctions();

		if(getLocation()==null && getLocation1()!=null) {
			 setLocation(getLocation1());
		 }
//		 System.out.println("ORG_ACCESS ===> "+(String)session.getAttribute(ORG_ACCESS));
		
		setStrOrg(getF_org());
		if(strUserType != null && strUserType.equals(MANAGER)) {
			organisationList = new FillOrganisation(request).fillOrganisation(strEmpOrgId);
			setStrOrg(strEmpOrgId);
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}
		
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
//		System.out.println("CurrenuserType===>"+getCurrUserType());
//		System.out.println("WLOCATION_ACCESS ===> "+(String)session.getAttribute(WLOCATION_ACCESS));	
		workLocationList = new FillWLocation(request).fillWLocation(getStrOrg(), (String)session.getAttribute(WLOCATION_ACCESS));
		desigList = new FillDesig(request).fillDesigByOrgOrAccessOrg(uF.parseToInt(getStrOrg()), (String)session.getAttribute(ORG_ACCESS));
		gradeList = new FillGrade(request).fillGrade();
		serviceslist = new FillServices(request).fillServices(getStrOrg(), uF);

		viewAllRequestList();
		viewAllProjectResourceRequestList(uF);
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		getSelectedFilter(uF);
		
		return LOAD;

	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if((strUserType != null && !strUserType.equals(MANAGER)) || (strUserType != null && !strUserType.equals(CEO)) || (getCurrUserType() != null && getCurrUserType().equals(strBaseUserType))) {
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
		
		String selectedFilter= CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
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

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public List<FillServices> getServiceslist() {
		return serviceslist;
	}

	public void setServiceslist(List<FillServices> serviceslist) {
		this.serviceslist = serviceslist;
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

	
	
	private void viewAllProjectResourceRequestList(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);

		// List<List<String>> requestList = new ArrayList<List<String>>();
		List<String> proResRequestList = new ArrayList<String>();
		try {

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));

			Map<String, String> hmSkillName = CF.getSkillNameMap(con);
			Map<String, String> hmDesigName = CF.getDesigMap(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select * from resource_plan_request_details where approve_status=1 ");
			if ((getFdate() != null && !getFdate().equals("") && !getFdate().equals("From Date")) && (getTdate() != null && !getTdate().equals("") && !getTdate().equals("To Date"))) {
				strQuery.append(" and entry_date  between '" + uF.getDateFormat(getFdate(), DATE_FORMAT) + "' and '" + uF.getDateFormat(getTdate(), DATE_FORMAT)+ "' ");
			}
			if (uF.parseToInt(getLocation()) > 0) {
				strQuery.append(" and r.wlocation=" + uF.parseToInt(getLocation()));
			}
			if (uF.parseToInt(getF_org()) > 0) {
				strQuery.append(" and pro_id in (select pro_id from projectmntnc where org_id=" + uF.parseToInt(getF_org())+")");
			}
			if (uF.parseToInt(getDesignation()) > 0) {
				strQuery.append(" and desig_id=" + uF.parseToInt(getDesignation()));
			}
			strQuery.append(" order by pro_start_date,skill_id");
			pst = con.prepareStatement(strQuery.toString());
			System.out.println("pst ====>> " + pst);
			rst = pst.executeQuery(); 
			int nCount = 0;
			StringBuilder sbProResRequirements = new StringBuilder();
			while (rst.next()) {
				sbProResRequirements.replace(0, sbProResRequirements.length(), "");
				StringBuilder sbApproveDeny = new StringBuilder();
				if (rst.getInt("approve_status")==0) {
					sbApproveDeny.append("<div style=\"float:right;\" id=\"myDivM" + nCount + "\" > ");
//					sbApproveDeny.append("<select name=\"strDesig\" id=\"strDesig"+rst.getString("resource_plan_request_id")+"\" style=\"width:150px !important;\" >" +
//							"<option value=\"\">Select Designation</option>"+sbDesigList.toString()+"</select>");
					sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"approveDenyProResReq('"+nCount+"','1','" + rst.getString("resource_plan_request_id")
						+ "');\" ><i class=\"fa fa-check-circle checknew\" style=\"padding-top: 0px !important;\" aria-hidden=\"true\" title=\"Approve\"></i></a> ");
					sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"approveDenyProResReq('"+nCount+"','-1','" + rst.getString("resource_plan_request_id")
						+ "');\" ><i class=\"fa fa-times-circle cross\" style=\"padding-top: 0px !important;\" aria-hidden=\"true\" title=\"Deny\"></i></a> ");
					sbApproveDeny.append("</div>");
				}
				
				//<input type=\"checkbox\" name=\"proResReqId\" id=\"proResReqId\" value=\""+rst.getString("resource_plan_request_id")+"\" onclick=\"checkAll();\">
				
				sbProResRequirements.append("<div><span style=\"float:left; width: 100%;\">A request <b>"+uF.showData(hmSkillName.get(rst.getString("skill_id")), "-")+"</b> for <b>"+uF.showData(rst.getString("min_exp"), "0")
					+"</b> to <b>"+uF.showData(rst.getString("max_exp"), "0")+"</b> years has been raised by <b>"+uF.showData(hmEmpNames.get(rst.getString("requested_by")), "-")
					+"</b> for <b>"+uF.showData(hmDesigName.get(rst.getString("desig_id")), "-")+"</b> on <b>"+uF.getDateFormat(rst.getString("request_date"), DBTIMESTAMP, DATE_FORMAT_STR)
					+"</b> to be fulfilled by <b>"+uF.getDateFormat(rst.getString("pro_start_date"), DBDATE, DATE_FORMAT_STR)+"</b>.</span></div>"); // "+sbApproveDeny.toString()+"
				proResRequestList.add(sbProResRequirements.toString());

				nCount++;
			}
			rst.close();
			pst.close();
			request.setAttribute("proResRequestList", proResRequestList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	private void viewAllRequestList() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

//		List<List<String>> requestList = new ArrayList<List<String>>();
		List<String> requestList = new ArrayList<String>();
		List<String> innerRequestList;

		/*int depart_id = 0;
		int wlocation_id = 0;*/

		try {
			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			if(hmUserTypeIdMap == null) hmUserTypeIdMap = new HashMap<String, String>();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
					"and effective_type='"+WORK_FLOW_RECRUITMENT+"' and effective_id in (select recruitment_id from recruitment_details where recruitment_id > 0 ");
			if ((getFdate() != null && !getFdate().equals("") && !getFdate().equals("From Date")) && (getTdate() != null && !getTdate().equals("") && !getTdate().equals("To Date"))) {
				sbQuery.append(" and effective_date >='" + uF.getDateFormat(getFdate(), DATE_FORMAT) + "' and effective_date <='" + uF.getDateFormat(getTdate(), DATE_FORMAT)+ "' ");
			}
			sbQuery.append(") group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmNextApproval ===>> " + hmNextApproval);
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
				" and is_approved=0 and effective_type='"+WORK_FLOW_RECRUITMENT+"' and effective_id in (select recruitment_id from recruitment_details where recruitment_id > 0 ");
			if ((getFdate() != null && !getFdate().equals("") && !getFdate().equals("From Date")) && (getTdate() != null && !getTdate().equals("") && !getTdate().equals("To Date"))) {
				sbQuery.append(" and effective_date >='" + uF.getDateFormat(getFdate(), DATE_FORMAT) + "' and effective_date <='" + uF.getDateFormat(getTdate(), DATE_FORMAT)+ "' ");
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(") and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(") and user_type_id=? ");
			}
			sbQuery.append(" group by effective_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(2, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(2, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(3, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmMemNextApproval.put(rs.getString("effective_id")+"_"+rs.getString("user_type_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmMemNextApproval ===>> " + hmMemNextApproval);
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_RECRUITMENT+"' and effective_id in (select recruitment_id from recruitment_details where recruitment_id > 0 ");
			if ((getFdate() != null && !getFdate().equals("") && !getFdate().equals("From Date")) && (getTdate() != null && !getTdate().equals("") && !getTdate().equals("To Date"))) {
				sbQuery.append(" and effective_date >='" + uF.getDateFormat(getFdate(), DATE_FORMAT) + "' and effective_date <='" + uF.getDateFormat(getTdate(), DATE_FORMAT)+ "' ");
			}
			sbQuery.append(") group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("effective_id"))) {
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select recruitment_id from recruitment_details where recruitment_id > 0 and status=-1 ");
			if ((getFdate() != null && !getFdate().equals("") && !getFdate().equals("From Date")) && (getTdate() != null && !getTdate().equals("") && !getTdate().equals("To Date"))) {
				sbQuery.append(" and effective_date >='" + uF.getDateFormat(getFdate(), DATE_FORMAT) + "' and effective_date <='" + uF.getDateFormat(getTdate(), DATE_FORMAT)+ "' ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("recruitment_id"))) {
					deniedList.add(rs.getString("recruitment_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
					" and effective_type='"+WORK_FLOW_RECRUITMENT+"' and effective_id in (select recruitment_id from recruitment_details where recruitment_id > 0 ");
			if ((getFdate() != null && !getFdate().equals("") && !getFdate().equals("From Date")) && (getTdate() != null && !getTdate().equals("") && !getTdate().equals("To Date"))) {
				sbQuery.append(" and effective_date >='" + uF.getDateFormat(getFdate(), DATE_FORMAT) + "' and effective_date <='" + uF.getDateFormat(getTdate(), DATE_FORMAT)+ "' ");
			}
			sbQuery.append(") group by effective_id,is_approved");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();
					
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where effective_type='"+WORK_FLOW_RECRUITMENT+"' and effective_id in (select recruitment_id from recruitment_details where recruitment_id > 0 ");
			if ((getFdate() != null && !getFdate().equals("") && !getFdate().equals("From Date")) && (getTdate() != null && !getTdate().equals("") && !getTdate().equals("To Date"))) {
				sbQuery.append(" and effective_date >='" + uF.getDateFormat(getFdate(), DATE_FORMAT) + "' and effective_date <='" + uF.getDateFormat(getTdate(), DATE_FORMAT)+ "' ");
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(") and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(") and user_type_id=? ");
			}
			sbQuery.append(" order by effective_id,member_position");
//			sbQuery.append(") and user_type_id=? order by effective_id,member_position");
			pst = con.prepareStatement(sbQuery.toString());
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(1, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(1, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(2, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();
			Map<String, List<String>> hmCheckEmpUserType = new HashMap<String, List<String>>();
			while(rs.next()) {
				List<String> checkEmpList = hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList == null)checkEmpList = new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("effective_id")+"_"+rs.getString("emp_id"));
				if(checkEmpUserTypeList == null)checkEmpUserTypeList = new ArrayList<String>();				
				checkEmpUserTypeList.add(rs.getString("user_type_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
				hmCheckEmpUserType.put(rs.getString("effective_id")+"_"+rs.getString("emp_id"), checkEmpUserTypeList);
			}
			rs.close();
			pst.close();

			// querying for planned records in company
			Map<String, String> hmPlannedCount = new HashMap<String, String>();
			pst = con.prepareStatement("select recruitment_id,resource_requirement from recruitment_details rd, resource_planner_details rpd " +
					" where rpd.designation_id=rd.designation_id and date_part('year', effective_date)=ryear and " +
					"date_part('month', effective_date)=rmonth and requirement_status = 'generate'");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				hmPlannedCount.put(rs.getString("recruitment_id"), rs.getString("resource_requirement"));
			}
			rs.close();
			pst.close();

			
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("(select r1.*, wfd.user_type_id as user_type from (select d.designation_id, r.priority_job_int,r.status,r.recruitment_id,r.custum_designation,e.emp_fname,e.emp_mname,e.emp_lname,"
					+ "w.wlocation_id,w.wlocation_name,r.entry_date,r.no_position,r.target_deadline,r.comments,existing_emp_count,"
					+ "d.designation_name,r.dept_id,r.added_by,r.req_form_type,r.hiring_manager from recruitment_details r join work_location_info w on(r.wlocation=w.wlocation_id) "
					+ "join employee_personal_details e on (r.added_by=e.emp_per_id) left join designation_details d using(designation_id) "
					+ "where recruitment_id>0 and requirement_status = 'generate' and r.status = 0 ");


			if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN)) {
				strQuery.append(" and (r.added_by=" + uF.parseToInt(strSessionEmpId) +" or r.hiring_manager like '%,"+strSessionEmpId+",%' " +
					" or r.recruitment_id in (select effective_id from work_flow_details where effective_type = '"+WORK_FLOW_RECRUITMENT+"'");
				strQuery.append(" and emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
					sbQuery.append(" and user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else {
					sbQuery.append(" and user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}

				strQuery.append(") ) ");
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
			if (uF.parseToInt(getServices()) > 0) {
				strQuery.append(" and r.services=" + uF.parseToInt(getServices()));
			}
			if ((getFdate() != null && !getFdate().equals("") && !getFdate().equals("From Date")) && (getTdate() != null && !getTdate().equals("") && !getTdate().equals("To Date"))) {
				strQuery.append(" and r.effective_date >='" + uF.getDateFormat(getFdate(), DATE_FORMAT) + "' and r.effective_date <='" + uF.getDateFormat(getTdate(), DATE_FORMAT)+ "' ");
			}
			if (getCheckStatus() != null && uF.parseToInt(getCheckStatus()) > -2) {
				strQuery.append(" and r.status = " + uF.parseToInt(getCheckStatus()) + " ");
			}
			strQuery.append(" order by r.status desc,r.recruitment_id desc) r1 , work_flow_details wfd " +
				"where r1.recruitment_id = wfd.effective_id  and wfd.effective_type = '"+WORK_FLOW_RECRUITMENT+"') ");
			
			//union
			strQuery.append(" union all (select r2.*, wfd.user_type_id as user_type from (select d.designation_id, r.priority_job_int,r.status,r.recruitment_id,r.custum_designation,e.emp_fname,e.emp_mname,e.emp_lname,"
					+ "w.wlocation_id,w.wlocation_name,r.entry_date,r.no_position,r.target_deadline,r.comments,existing_emp_count,"
					+ "d.designation_name,r.dept_id,r.added_by,r.req_form_type,r.hiring_manager from recruitment_details r join work_location_info w on(r.wlocation=w.wlocation_id) "
					+ "join employee_personal_details e on (r.added_by=e.emp_per_id) left join designation_details d using(designation_id) "
					+ "where recruitment_id>0 and requirement_status = 'generate' and r.status != 0 ");

//			if (strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strUserType.equalsIgnoreCase(RECRUITER))) {
////				strQuery.append(" and r.dept_id=" + depart_id);
//				strQuery.append(" and (r.added_by=" + uF.parseToInt(strSessionEmpId) +" or r.hiring_manager like '%,"+strSessionEmpId+",%' )");
//				// +" and r.added_by="+uF.parseToInt(strSessionEmpId)
//			}
			if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN)) {
				strQuery.append(" and (r.added_by=" + uF.parseToInt(strSessionEmpId) +" or r.hiring_manager like '%,"+strSessionEmpId+",%' " +
					" or r.recruitment_id in (select effective_id from work_flow_details where effective_type = '"+WORK_FLOW_RECRUITMENT+"'");
				strQuery.append(" and emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
					sbQuery.append(" and user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else {
					sbQuery.append(" and user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
//				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(CEO)) {
//					strQuery.append(" and (user_type_id = "+uF.parseToInt(strUserTypeId)+" or user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
//				} else {
//					strQuery.append(" and user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
//				}
				strQuery.append(") ) ");
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
			if (uF.parseToInt(getServices()) > 0) {
				strQuery.append(" and r.services=" + uF.parseToInt(getServices()));
			}
			if ((getFdate() != null && !getFdate().equals("") && !getFdate().equals("From Date")) && (getTdate() != null && !getTdate().equals("") && !getTdate().equals("To Date"))) {
				strQuery.append(" and r.effective_date >='" + uF.getDateFormat(getFdate(), DATE_FORMAT) + "' and r.effective_date <='" + uF.getDateFormat(getTdate(), DATE_FORMAT)+ "' ");
			}
			if (getCheckStatus() != null && uF.parseToInt(getCheckStatus()) > -2) {
				strQuery.append(" and r.status = " + uF.parseToInt(getCheckStatus()) + " ");
			}
			strQuery.append(" order by r.status desc, r.recruitment_id desc) r2, work_flow_details wfd where r2.recruitment_id = wfd.effective_id " +
				" and wfd.effective_type = '"+WORK_FLOW_RECRUITMENT+"' ) ");
			
//			strQuery.append(" order by r.status desc,r.recruitment_id desc ");
			
			pst = con.prepareStatement(strQuery.toString());
//			System.out.println("pst ====> " + pst);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("pst ====> " + pst);
			int nCount = 0;
			StringBuilder sbRequirements = new StringBuilder();
			StringBuilder sbDesig = new StringBuilder();
			StringBuilder sbApproveDeny = new StringBuilder();
			StringBuilder sbStauts = new StringBuilder();
			List<String> alList = new ArrayList<String>();
			Map<String, String> hmUserTypeName = CF.getUserTypeMap(con);
			String usetTypeName = "";
			while (rs.next()) {
				
				List<String> checkEmpList = hmCheckEmp.get(rs.getString("recruitment_id"));
				if(checkEmpList==null) checkEmpList = new ArrayList<String>();
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("recruitment_id")+"_"+strSessionEmpId);
				if(checkEmpUserTypeList==null) checkEmpUserTypeList = new ArrayList<String>();
				
//				System.out.println("checkEmpUserTypeList ===>> " + checkEmpUserTypeList);
				boolean checkGHRInWorkflow = true;
				if(checkEmpUserTypeList.contains(hmUserTypeIdMap.get(HRMANAGER)) && !checkEmpUserTypeList.contains(hmUserTypeIdMap.get(ADMIN)) && strUserType != null && strUserType.equals(ADMIN)) {
					checkGHRInWorkflow = false;
				}
				
				List<String> checkHiringManagerList = new ArrayList<String>();
				
				if(rs.getString("hiring_manager") != null) {
					checkHiringManagerList = Arrays.asList(rs.getString("hiring_manager").split(","));
				}
//				System.out.println(rs.getString("recruitment_id")+" -- checkEmpUserTypeList ===>> " + checkEmpUserTypeList);
//				System.out.println("RID ===>> "+rs.getString("recruitment_id")+"USRTYPE ===>> "+rs.getString("user_type"));
//				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(HRMANAGER)) {
				if(!checkEmpList.contains(strSessionEmpId) && !checkHiringManagerList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN) && uF.parseToInt(strSessionEmpId) != uF.parseToInt(rs.getString("added_by"))) {
					continue;
				}
//				System.out.println("1 RID ===>> "+rs.getString("recruitment_id")+"USRTYPE ===>> "+rs.getString("user_type"));
//				if(strUserType.equalsIgnoreCase(HRMANAGER) && (rs.getString("wlocation_id")!=null && !rs.getString("wlocation_id").trim().equals(locationID))) {
//					continue;
//				}
				
				String userType = rs.getString("user_type");				
				if((!checkEmpList.contains(strSessionEmpId) && (checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) || (strUserType!=null && strUserType.equalsIgnoreCase(ADMIN))) && alList.contains(rs.getString("recruitment_id"))) {
//					System.out.println("2 RID ===>> "+rs.getString("recruitment_id")+" -- USRTYPE ===>> "+rs.getString("user_type"));
					continue;
				} else if(!checkEmpList.contains(strSessionEmpId) && strUserType !=null && strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("recruitment_id"))) {
//				} else if((( (!checkEmpList.contains(strSessionEmpId) && (checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) ) || (checkEmpUserTypeList.contains(userType) && (checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) ) ) || 
//					System.out.println("3 RID ===>> "+rs.getString("recruitment_id")+" -- USRTYPE ===>> "+rs.getString("user_type"));
					userType = strUserTypeId;
					alList.add(rs.getString("recruitment_id"));
				} else if(strUserType !=null && !strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("recruitment_id")) && (checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by")) ) ) {
//				} else if((( (!checkEmpList.contains(strSessionEmpId) && (checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) ) || (checkEmpUserTypeList.contains(userType) && (checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) ) ) ||
					if( (!checkEmpList.contains(strSessionEmpId) && (checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) ) 
						|| (checkEmpUserTypeList.contains(userType) && (checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) )) {
//					System.out.println("3---1 RID ===>> "+rs.getString("recruitment_id")+" -- USRTYPE ===>> "+rs.getString("user_type"));
					if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
						userType = strBaseUserTypeId;	
					} else {
						userType = strUserTypeId;
					}
					alList.add(rs.getString("recruitment_id"));
					} else {
//						System.out.println("3---1 else RID ===>> "+rs.getString("recruitment_id")+" -- USRTYPE ===>> "+rs.getString("user_type"));
						continue;
					}
				} else if(!checkEmpUserTypeList.contains(userType)) {
//					System.out.println("4 RID ===>> "+rs.getString("recruitment_id")+" -- USRTYPE ===>> "+rs.getString("user_type"));
					continue;	
				} 
				
//				System.out.println("strSessionEmpId ===>> " + strSessionEmpId + " -- checkEmpList ===>> " + checkEmpList);
				if(checkEmpList.contains(strSessionEmpId) && uF.parseToInt(rs.getString("req_form_type"))==0) {
					usetTypeName = "["+hmUserTypeName.get(rs.getString("user_type"))+"]";
//					System.out.println("usetTypeName  ===>> " + usetTypeName);
				}else {
					usetTypeName = "";
					userType = "1";
				}
				
				sbRequirements.replace(0, sbRequirements.length(), "");
				sbDesig.replace(0, sbDesig.length(), "");
				sbApproveDeny.replace(0, sbApproveDeny.length(), "");
				sbStauts.replace(0, sbStauts.length(), "");
				
				innerRequestList = new ArrayList<String>();
				if (rs.getInt("priority_job_int") == 1) {
					innerRequestList.add("<img src=\"images1/icons/exclamation_mark_icon.png\" width=\"20\" height=\"20\"/>");
				} else {
					innerRequestList.add("");
				}
				
				innerRequestList.add("");
				if (rs.getString("custum_designation") != null && !rs.getString("custum_designation").equals("") && rs.getString("designation_name") == null) {
					if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(HRMANAGER) && !strUserType.equalsIgnoreCase(RECRUITER)) {
						innerRequestList.add("<b>"+rs.getString("custum_designation")+"</b>");
						sbDesig.append("<b>"+rs.getString("custum_designation")+"</b>");
					}else{
						innerRequestList.add("<a href=\"javascript:void(0)\" onclick=\"addDesignation('"+rs.getString("recruitment_id")+"', '"+nCount+"', '"+userType+"');\"> "
								+ rs.getString("custum_designation") + "</a> (new)");
						
						sbDesig.append("<span id=\"myDivDesig" + nCount + "\">");
						sbDesig.append("<a href=\"javascript:void(0)\" onclick=\"addDesignation('"+rs.getString("recruitment_id")+"', '"+nCount+"', '"+userType+"');\"> "
								+ rs.getString("custum_designation") + "</a>");
						sbDesig.append("</span>");
						sbDesig.append(" (new)");
					}
				} else {
					innerRequestList.add(rs.getString("designation_name"));
					sbDesig.append("<a href=\"javascript:void(0);\" onclick=\"getDesignationDetails('"+rs.getString("designation_id")+"','"+rs.getString("designation_name")+"')\">"+rs.getString("designation_name")+"</a>");
				}

				innerRequestList.add(rs.getString("wlocation_name"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				
				innerRequestList.add(rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				innerRequestList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				innerRequestList.add(rs.getString("no_position"));
				innerRequestList.add(rs.getString("existing_emp_count"));
				innerRequestList.add(uF.showData(hmPlannedCount.get(rs.getString("recruitment_id")), "0"));

				innerRequestList.add(uF.getDateFormat(rs.getString("target_deadline"), DBDATE, CF.getStrReportDateFormat()));

				String strnCount = String.valueOf(nCount);
				
//				System.out.println("userType ===>> " + userType);
//				System.out.println(rs.getString("recruitment_id") + " -- hmNextApproval ===>> " + uF.parseToInt(hmNextApproval.get(rs.getString("recruitment_id"))));
//				System.out.println(rs.getString("recruitment_id") + " -- hmMemNextApproval ===>> " + uF.parseToInt(hmMemNextApproval.get(rs.getString("recruitment_id")+"_"+userType)));
				if(deniedList.contains(rs.getString("recruitment_id"))) {
//					System.out.println("===>> 1");
					sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus" + nCount + "\" > ");
					/*sbStauts.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
					sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
					
					sbStauts.append("</div>");
					sbApproveDeny.append("<div style=\"float:right;\" id=\"myDivM" + nCount + "\" > ");
					sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("recruitment_id")+"');\" style=\"margin-left: 10px;\">View</a>");
					sbApproveDeny.append("</div>");
				} else if(rs.getInt("status")==1) {
//					System.out.println("===>> 2");
					sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus" + nCount + "\" > ");
					/*sbStauts.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
					sbStauts.append("</div>");
				} else if(uF.parseToInt(hmAnyOneApproval.get(rs.getString("recruitment_id")))==1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("recruitment_id")))==rs.getInt("status")) {
//					System.out.println("===>> 3");
					sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus" + nCount + "\" > ");
					/*sbStauts.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
					sbStauts.append("</div>");
				} else if((strUserType != null && strUserType.equals(ADMIN) && (usetTypeName == null || usetTypeName.equals(""))) || (uF.parseToInt(hmNextApproval.get(rs.getString("recruitment_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("recruitment_id")+"_"+userType)) && uF.parseToInt(hmNextApproval.get(rs.getString("recruitment_id")))>0)) {
//					System.out.println("===>> 4 -- usetTypeName ===>> " + usetTypeName);
					
					sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus" + nCount + "\" > ");
					/*sbStauts.append("<img src=\"images1/icons/pending.png\" title=\"Waiting for approval\" />");*/
					sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for approval\"></i>");
					
					sbStauts.append("</div>");
					
					sbApproveDeny.append("<div style=\"float:right;\" id=\"myDivM" + nCount + "\" > ");
					if (rs.getString("custum_designation") != null && !rs.getString("custum_designation").equals("") && rs.getString("designation_name") == null) {
						sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"alert('Please approve custom Designation, waiting for Designation approval.')\" style=\"margin-left: 10px;\">Waiting...</a>");
					} else {
						sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"viewProfileRequest('"+ rs.getString("recruitment_id")+"','"+getCurrUserType()+"');\" >" +
							"<i class=\"fa fa-eye\" aria-hidden=\"true\" style=\"font-size: 16px;\" title=\"View Job Details\"></i></a> ");
						sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"approveRequest('"+ nCount +"','"+ rs.getString("recruitment_id")+"','"+userType+"','"+getCurrUserType()+"');\" >" +
							"<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve to Create Job Profile\"></i></a> ");
						sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to decline this request?'))denyRequest('" + strnCount
							+ "','" + rs.getString("recruitment_id") + "','"+userType+"','"+getCurrUserType()+"');\">" + " <i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denial of Job Requirement\"></i></a>  ");
						if(uF.parseToInt(rs.getString("req_form_type")) > 0) {
							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"editRequestWOWorkflow('" + strnCount + "','" + rs.getString("recruitment_id") + "','" + getCurrUserType() + "');\">"
								+ " <img src=\"images1/edit.png\" title=\"Edit Request\" /> </a> ");
						} else {
							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"editRequest('" + strnCount + "','" + rs.getString("recruitment_id") + "','" + getCurrUserType() + "');\">"
								+ " <img src=\"images1/edit.png\" title=\"Edit Request\" /> </a> ");
						}
					}
					sbApproveDeny.append("</div>");
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("recruitment_id")))<uF.parseToInt(hmMemNextApproval.get(rs.getString("recruitment_id")+"_"+userType)) || (uF.parseToInt(hmNextApproval.get(rs.getString("recruitment_id")))==0 && uF.parseToInt(hmNextApproval.get(rs.getString("recruitment_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("recruitment_id")+"_"+userType)))) {
//					System.out.println("===>> 5");
//					System.out.println("status ===>> " + rs.getInt("status") +" strUserType ===>> " + strUserType);
						if(rs.getInt("status")==0) {
//							System.out.println("===>> in status 0");
							if(strUserType.equalsIgnoreCase(ADMIN) && uF.parseToInt(strUserTypeId) == uF.parseToInt(userType)) { //!checkEmpList.contains(strSessionEmpId) && 
//								System.out.println("===>> in ADMIN");
								sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus" + nCount + "\" > ");
								/*sbStauts.append("<img src=\"images1/icons/pending.png\" title=\"Waiting for approval\" />");*/
								sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Waiting for approval\"></i>");
								
								sbStauts.append("</div>");
								
								sbApproveDeny.append("<div style=\"float:right;\" id=\"myDivM" + nCount + "\" > ");
								if (rs.getString("custum_designation") != null && !rs.getString("custum_designation").equals("") && rs.getString("designation_name") == null) {
									sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"alert('Please approve custom Designation, waiting for Designation approval.')\" style=\"margin-left: 10px;\">Waiting...</a>");
								} else {
									sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"viewProfileRequest('"+ rs.getString("recruitment_id")+"','"+getCurrUserType()+"');\" >" +
										"<i class=\"fa fa-eye\" aria-hidden=\"true\" style=\"font-size: 16px;\" title=\"View Job Details\"></i></a> ");
									sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"approveRequest('"+ nCount +"','"+ rs.getString("recruitment_id")+"','"+userType+"','"+getCurrUserType()+"');\" >" +
											"<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve to Create Job Profile\"></i></a> ");
									sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to decline this request?'))denyRequest('" + strnCount
											+ "','" + rs.getString("recruitment_id") + "','"+userType+"','"+getCurrUserType()+"');\">" + " <i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denial of Job Requirement\"></i></a>  ");
									if(uF.parseToInt(rs.getString("req_form_type")) > 0) {
										sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"editRequestWOWorkflow('" + strnCount + "','" + rs.getString("recruitment_id") + "','" + getCurrUserType() + "');\">"
												+ " <img src=\"images1/edit.png\" title=\"Edit Request\" /> </a> ");
									} else {
										sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"editRequest('" + strnCount + "','" + rs.getString("recruitment_id") + "','" + getCurrUserType() + "');\">"
											+ " <img src=\"images1/edit.png\" title=\"Edit Request\" /> </a> ");
									}
								}
								sbApproveDeny.append("</div>");
							} else {
								sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus" + nCount + "\" > ");
								/*sbStauts.append("<img src=\"images1/icons/pullout.png\" title=\"Waiting for workflow\" />");*/
								sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" title=\"Waiting for workflow\" ></i>");
								sbStauts.append("</div>");
								
//								****************** Workflow *******************************
								sbApproveDeny.append("<div style=\"float:right;\" id=\"myDivM" + nCount + "\" > ");
								if (rs.getString("custum_designation") != null && !rs.getString("custum_designation").equals("") && rs.getString("designation_name") == null) {
									sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"alert('Please approve custom Designation, waiting for Designation approval.')\" style=\"margin-left: 10px;\">Waiting...</a>");
								} else {
									sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("recruitment_id")+"');\" style=\"margin-left: 10px;\">Workflow Status</a>");
									if(checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) {
										if(uF.parseToInt(rs.getString("req_form_type")) > 0) {
											sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"editRequestWOWorkflow('" + strnCount + "','" + rs.getString("recruitment_id") + "','" + getCurrUserType() + "');\">"
													+ " <img src=\"images1/edit.png\" title=\"Edit Request\" /> </a> ");
										} else {
											sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"editRequest('" + strnCount + "','" + rs.getString("recruitment_id") + "','" + getCurrUserType() + "');\">"
												+ " <img src=\"images1/edit.png\" title=\"Edit Request\" /> </a> ");
										}
									}
									if(!checkGHRInWorkflow) {
										sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"viewProfileRequest('"+ rs.getString("recruitment_id")+"','"+getCurrUserType()+"');\" >" +
											"<i class=\"fa fa-eye\" aria-hidden=\"true\" style=\"font-size: 16px;\" title=\"View Job Details\"></i></a> ");
										sbApproveDeny.append("&nbsp;|&nbsp;<a href=\"javascript:void(0)\" onclick=\"approveRequest('"+ nCount +"','"+ rs.getString("recruitment_id")+"','','"+getCurrUserType()+"');\" >" +
											"<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve to Create Job Profile ("+ADMIN+")\"></i></a> ");
										sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to decline this request?'))denyRequest('" + strnCount
											+ "','" + rs.getString("recruitment_id") + "','','"+getCurrUserType()+"');\">" + " <i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denial of Job Requirement ("+ADMIN+")\"></i></a>  ");
									}
								}
								sbApproveDeny.append("</div>");
//								****************** Workflow *******************************
								
							}
						} else if(rs.getInt("status")==1) {
							sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus" + nCount + "\" > ");
							/*sbStauts.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
							sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\" ></i>");
							
							sbStauts.append("</div>");
						} else {
							sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus" + nCount + "\" > ");
							/*sbStauts.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
							sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
							sbStauts.append("</div>");
						}
						
				} else {
					sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus" + nCount + "\" > ");
					/*sbStauts.append("<img src=\"images1/icons/pullout.png\" title=\"Waiting for workflow\" /> </a> ");*/
					sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\"  title=\"Waiting for workflow\" ></i> ");
					sbStauts.append("</div>");
					
//					****************** Workflow *******************************
					sbApproveDeny.append("<div style=\"float:right;\" id=\"myDivM" + nCount + "\" > ");
					if (rs.getString("custum_designation") != null && !rs.getString("custum_designation").equals("") && rs.getString("designation_name") == null) {
						sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"alert('Please approve custom Designation, waiting for Designation approval.')\" style=\"margin-left: 10px;\">Waiting...</a>");
					} else {
						sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("recruitment_id")+"');\" style=\"margin-left: 10px;\">Workflow Status</a>");
						if(checkHiringManagerList.contains(strSessionEmpId) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("added_by"))) {
							if(uF.parseToInt(rs.getString("req_form_type")) > 0) {
								sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"editRequestWOWorkflow('" + strnCount + "','" + rs.getString("recruitment_id") + "','" + getCurrUserType() + "');\">"
									+ " <img src=\"images1/edit.png\" title=\"Edit Request\" /> </a> ");
							} else {
								sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"editRequest('" + strnCount + "','" + rs.getString("recruitment_id") + "','" + getCurrUserType() + "');\">"
									+ " <img src=\"images1/edit.png\" title=\"Edit Request\" /> </a> ");
							}
						}
					}
					sbApproveDeny.append("</div>");
//					****************** Workflow *******************************
				}

				
				String openFont = "", closeFont="";
				if (rs.getInt("priority_job_int") == 1) {
					openFont = "<span class=\"high\" style=\"float: left; width: 85%;\">";
					closeFont = "</span>";
				} else if (rs.getInt("priority_job_int") == 2) {
					openFont = "<span class=\"medium\" style=\"float: left; width: 85%;\">";
					closeFont = "</span>";
				} else {
					openFont = "<span class=\"low\" style=\"float: left; width: 85%;\">";
					closeFont = "</span>";
				}
				innerRequestList.add(sbApproveDeny.toString());
				//requestList.add(innerRequestList);
				

				sbRequirements.append(""+sbStauts + openFont + " A request for the requirement of <strong>"+rs.getString("no_position")+"</strong> resources has been generated by <strong>"+rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname")+"</strong> for "+sbDesig+" designation and needs to be accomplished by "+uF.showData(uF.getDateFormat(rs.getString("target_deadline"), DBDATE, CF.getStrReportDateFormat()), "No deadline")+". "+usetTypeName+"" + closeFont +"&nbsp;"+sbApproveDeny.toString()); //+ sbWorkFlow.toString()
//				System.out.println("sbRequirements ===> "+sbRequirements.toString());
				requestList.add(sbRequirements.toString());
				nCount++;
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
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

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

}
