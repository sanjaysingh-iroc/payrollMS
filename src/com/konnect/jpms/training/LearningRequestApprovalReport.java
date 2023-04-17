package com.konnect.jpms.training;

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

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LearningRequestApprovalReport extends ActionSupport implements ServletRequestAware, IStatements {

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
	String f_strWLocation; 
	String f_level;
	String f_department;
	String f_service;
	String f_employeType;
	String f_grade;
	private String strOrg;
	private String alertStatus;
	private String alert_type;
	
	private String currUserType;
	private String alertID;
	
	
	private List<FillDesig> desigList;
	private List<FillGrade> gradeList;
	private List<FillWLocation> workLocationList;
	private List<FillServices> serviceList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;

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
		
		request.setAttribute(PAGE, "LearningRequestApprovalReport.jsp");
		request.setAttribute(TITLE, "Approve Learning Request");

		if(getLocation()==null && getLocation1()!=null) {
			 setLocation(getLocation1());
		 }

		
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
		serviceList = new FillServices(request).fillServices(getStrOrg(), uF);
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));

//		System.out.println("LRAR/120--getF_strWLocation="+getF_strWLocation());
//		System.out.println("LRAR/121--getF_org="+getF_org());
		viewAllRequestList();
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
			if(getF_strWLocation()!=null) {
				String strLocation="";
				for(int i=0;workLocationList!=null && i<workLocationList.size();i++) {
					if(getF_strWLocation().equals(workLocationList.get(i).getwLocationId())) {
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
			
			alFilter.add("DEPARTMENT");
			if(getF_department()!=null) {
				String strDepart = "";
				for(int i=0; departmentList!=null && i<departmentList.size();i++) {
					if(getF_department().equals(departmentList.get(i).getDeptId())) {
						strDepart=departmentList.get(i).getDeptName();
					}
				}
				if(strDepart!=null && !strDepart.equals("")) {
					hmFilter.put("DEPARTMENT", strDepart);
				} else {
					hmFilter.put("DEPARTMENT", "All Departments");
				}
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
			
			
			/*alFilter.add("LEVEL");
			if(getF_level()!=null) {
				String strLevel="";
				for(int i=0; levelList!=null && i<levelList.size();i++) {
					if(getF_level().equals(levelList.get(i).getLevelId())) {
						strLevel=levelList.get(i).getLevelCodeName();
					}
				}
				if(strLevel!=null && !strLevel.equals("")) {
					hmFilter.put("LEVEL", strLevel);
				} else {
					hmFilter.put("LEVEL", "All Levels");
				}
			} else {
				hmFilter.put("LEVEL", "All Levels");
			}*/
			
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
	
	private void viewAllRequestList(){

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		List<List<String>> requestList = new ArrayList<List<String>>();
//		List<String> requestList = new ArrayList<String>();
//		List<String> innerRequestList;

		/*int depart_id = 0;
		int wlocation_id = 0;*/

		try {
			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			if(hmUserTypeIdMap == null) hmUserTypeIdMap = new HashMap<String, String>();
			
			StringBuilder sbQuery=new StringBuilder();
			/*sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
					"and effective_type='"+WORK_FLOW_LEARNING_REQUEST+"' and effective_id in (select nominated_details_id from learning_nominee_details " +
					"where nominated_details_id > 0 ");*/
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
					"and effective_type='"+WORK_FLOW_LEARNING_REQUEST+"' and effective_id in (select nominated_details_id from learning_nominee_details " +
					"where nominated_details_id > 0 and requested_by in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id");
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            sbQuery.append(")");
			if ((getFdate() != null && !getFdate().equals("") && !getFdate().equals("From Date")) && (getTdate() != null && !getTdate().equals("") && !getTdate().equals("To Date"))) {
//				sbQuery.append(" and request_date >='" + uF.getDateFormat(getFdate(), DATE_FORMAT) + "' and request_date <='" + uF.getDateFormat(getTdate(), DATE_FORMAT)+ "' ");
				sbQuery.append(" and request_date >='" + uF.getDateFormat(getFdate(), DATE_FORMAT) + "' and request_date <='" + uF.getDateFormat(getTdate(), DATE_FORMAT)+ "' ");
			}
			sbQuery.append(") group by effective_id");
//			System.out.println("LRAR/302--sbQuery="+sbQuery);
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
				" and is_approved=0 and effective_type='"+WORK_FLOW_LEARNING_REQUEST+"' and effective_id in (select nominated_details_id from learning_nominee_details where nominated_details_id > 0 " +
				"and requested_by in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id");
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            sbQuery.append(")");
			if ((getFdate() != null && !getFdate().equals("") && !getFdate().equals("From Date")) && (getTdate() != null && !getTdate().equals("") && !getTdate().equals("To Date"))) {
				sbQuery.append(" and request_date >='" + uF.getDateFormat(getFdate(), DATE_FORMAT) + "' and request_date <='" + uF.getDateFormat(getTdate(), DATE_FORMAT)+ "' ");
			}
			sbQuery.append(")");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append(" group by effective_id,user_type_id");
//			System.out.println("LRAR/342--sbQuery="+sbQuery);
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
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_LEARNING_REQUEST+"' and effective_id in (select nominated_details_id from learning_nominee_details where nominated_details_id > 0 " +
					"and requested_by in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id");
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            sbQuery.append(")");
			if ((getFdate() != null && !getFdate().equals("") && !getFdate().equals("From Date")) && (getTdate() != null && !getTdate().equals("") && !getTdate().equals("To Date"))) {
				sbQuery.append(" and request_date >='" + uF.getDateFormat(getFdate(), DATE_FORMAT) + "' and request_date <='" + uF.getDateFormat(getTdate(), DATE_FORMAT)+ "' ");
			}
			sbQuery.append(") group by effective_id");
//			System.out.println("LRAR/387--sbQuery="+sbQuery);
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
			sbQuery.append("select nominated_details_id from learning_nominee_details where nominated_details_id > 0 and approve_status=-1 ");
			sbQuery.append("and requested_by in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id");
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(")");
			if ((getFdate() != null && !getFdate().equals("") && !getFdate().equals("From Date")) && (getTdate() != null && !getTdate().equals("") && !getTdate().equals("To Date"))) {
				sbQuery.append(" and request_date >='" + uF.getDateFormat(getFdate(), DATE_FORMAT) + "' and request_date <='" + uF.getDateFormat(getTdate(), DATE_FORMAT)+ "' ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("nominated_details_id"))) {
					deniedList.add(rs.getString("nominated_details_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
					" and effective_type='"+WORK_FLOW_LEARNING_REQUEST+"' and effective_id in (select nominated_details_id from learning_nominee_details where nominated_details_id > 0 " +
					"and requested_by in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id");
			
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            sbQuery.append(")");
			if ((getFdate() != null && !getFdate().equals("") && !getFdate().equals("From Date")) && (getTdate() != null && !getTdate().equals("") && !getTdate().equals("To Date"))) {
				sbQuery.append(" and request_date >='" + uF.getDateFormat(getFdate(), DATE_FORMAT) + "' and request_date <='" + uF.getDateFormat(getTdate(), DATE_FORMAT)+ "' ");
			}
			sbQuery.append(") group by effective_id,is_approved");
//			System.out.println("LRAR/440--sbQuery"+sbQuery);
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and effective_type='"+WORK_FLOW_LEARNING_REQUEST+"' and effective_id in (select lnd.nominated_details_id from learning_nominee_details lnd,learning_plan_details lpd " +
							"where lnd.learning_plan_id=lpd.learning_plan_id and lnd.learning_plan_id > 0 and lnd.requested_by in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id");
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(")) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("LRAR/351--pst="+pst);
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();	
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();	
			
			while(rs.next()) {
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where effective_type='"+WORK_FLOW_LEARNING_REQUEST+"'" +
					" and effective_id in (select lnd.nominated_details_id from learning_nominee_details lnd,learning_plan_details lpd where lnd.learning_plan_id=lpd.learning_plan_id " +
					"and lnd.learning_plan_id > 0 and lnd.requested_by in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(")) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("LRAR/387--pst="+pst);
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();	
			while(rs.next()) {
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
					
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where effective_type='"+WORK_FLOW_LEARNING_REQUEST+"' and effective_id in " +
					"(select nominated_details_id from learning_nominee_details where nominated_details_id > 0 ");
			sbQuery.append("and requested_by in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
			"where epd.emp_per_id = eod.emp_id");
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            sbQuery.append(")");
			if ((getFdate() != null && !getFdate().equals("") && !getFdate().equals("From Date")) && (getTdate() != null && !getTdate().equals("") && !getTdate().equals("To Date"))) {
				sbQuery.append(" and request_date >='" + uF.getDateFormat(getFdate(), DATE_FORMAT) + "' and request_date <='" + uF.getDateFormat(getTdate(), DATE_FORMAT)+ "' ");
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
//			System.out.println("LRAR/418--pst="+pst);
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

			StringBuilder strQuery = new StringBuilder();
//			strQuery.append("Select lnd.*,learning_plan_name from learning_nominee_details lnd, learning_plan_details lpd where lnd.learning_plan_id=lpd.learning_plan_id");
			/*strQuery.append("select * from learning_nominee_details lnd, work_flow_details wfd,learning_plan_details lpd where " +
					"lnd.nominated_details_id = wfd.effective_id and lnd.learning_plan_id = lpd.learning_plan_id and emp_id = ? and user_type_id = ?");*/
			
			/*strQuery.append("select e.*,wfd.user_type_id as user_type from (select * from learning_nominee_details lnd,learning_plan_details lpd " +
					"where lnd.learning_plan_id=lpd.learning_plan_id and lnd.nominated_details_id > 0 and approve_status=0) e, work_flow_details wfd " +
					"where e.nominated_details_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_LEARNING_REQUEST+"' ");*/
			
			strQuery.append("select e.*,wfd.user_type_id as user_type from (select * from learning_nominee_details lnd,learning_plan_details lpd " +
					"where lnd.learning_plan_id=lpd.learning_plan_id and lnd.nominated_details_id > 0 and lnd.requested_by in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id");
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			
			strQuery.append(")) e, work_flow_details wfd " +
					"where e.nominated_details_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_LEARNING_REQUEST+"' ");
			
			if(strUserType != null && !strUserType.equals(ADMIN)) {
				strQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
//					strQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
					strQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else {
					strQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
			}
			strQuery.append("order by e.request_date desc");
//			System.out.println("LRAR/380--strQuery="+strQuery);
			pst = con.prepareStatement(strQuery.toString());
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			/*if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(2, uF.parseToInt(strBaseUserTypeId));
			} else{
				pst.setInt(2, uF.parseToInt(strUserTypeId));
			}*/
			
//			System.out.println("LRAR/465--pst="+pst);
			rs = pst.executeQuery();
			StringBuilder sbRequirements = new StringBuilder();
			StringBuilder sbStauts = new StringBuilder();
			StringBuilder sbApproveDeny = new StringBuilder();
			List<String> alList = new ArrayList<String>();
			List<String> alEmployeeList = new ArrayList<String>();
			Map<String, String> hmUserTypeName = CF.getUserTypeMap(con);
			int nCount = 0;
			String usetTypeName = "";
			
			while (rs.next()) {
				List<String> checkEmpList = hmCheckEmp.get(rs.getString("nominated_details_id"));
				if(checkEmpList==null) checkEmpList = new ArrayList<String>();
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("nominated_details_id")+"_"+strSessionEmpId);
				if(checkEmpUserTypeList==null) checkEmpUserTypeList = new ArrayList<String>();
				
				boolean checkGHRInWorkflow = true;
				if(checkEmpUserTypeList.contains(hmUserTypeIdMap.get(HRMANAGER)) && !checkEmpUserTypeList.contains(hmUserTypeIdMap.get(ADMIN)) && strUserType != null && strUserType.equals(ADMIN)) {
					checkGHRInWorkflow = false;
				}
				
				if (!checkEmpList.contains(strSessionEmpId) && strUserType != null && !strUserType.equalsIgnoreCase(ADMIN)) {
					continue;
				}
				
				String userType = rs.getString("user_type");
				
				if (!checkEmpList.contains(strSessionEmpId) && strUserType != null && strUserType.equalsIgnoreCase(ADMIN)
						&& alList.contains(rs.getString("nominated_details_id"))) {
					continue;
				} else if (!checkEmpList.contains(strSessionEmpId) && strUserType != null && strUserType.equalsIgnoreCase(ADMIN)
						&& !alList.contains(rs.getString("nominated_details_id"))) {
					userType = strUserTypeId;
					alList.add(rs.getString("nominated_details_id"));
				} else if (!checkEmpUserTypeList.contains(userType)) {
					// System.out.println("4 RID ===>> "+rs.getString("recruitment_id")+" -- USRTYPE ===>> "+rs.getString("user_type"));
					continue;
				}
				
				if (!alEmployeeList.contains(rs.getString("requested_by"))) {
					alEmployeeList.add(rs.getString("requested_by"));
				}
				
				List<String> alInner = new ArrayList<String>();
				
				alInner.add(uF.showData(hmEmpName.get(rs.getString("requested_by")), "")); //0
				alInner.add(rs.getString("learning_plan_name")); //1
				alInner.add(uF.getDateFormat(rs.getString("request_date"), DBDATE, CF.getStrReportDateFormat())); //2
				
				alInner.add(uF.showData(rs.getString("approve_reason"), "-")); //3
				
				StringBuilder sbCheckApproveby = new StringBuilder();
				
				if (hmAnyOneApproeBy != null && hmAnyOneApproeBy.get(rs.getString("nominated_details_id")) != null) {
					
					sbCheckApproveby.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("nominated_details_id")+"');\" style=\"margin-left: 10px;\">View</a>");
				} else if (hmotherApproveBy != null && hmotherApproveBy.get(rs.getString("nominated_details_id")) != null) {
					sbCheckApproveby.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("nominated_details_id")+"');\" style=\"margin-left: 10px;\">View</a>");
				} else {
					sbCheckApproveby.append("");
				}
				
				if (deniedList.contains(rs.getString("nominated_details_id"))) {
					 /*alInner.add("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>"); //4
					alInner.add(sbCheckApproveby.toString()); //5
				} else if (rs.getInt("approve_status") == 1) {
					/*alInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>"); //8
					alInner.add(sbCheckApproveby.toString()); //4
				} else if (uF.parseToInt(hmAnyOneApproval.get(rs.getString("nominated_details_id"))) == 1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("nominated_details_id"))) == rs.getInt("is_approved")) {
					/*alInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>"); //8
					alInner.add(sbCheckApproveby.toString()); //4
				} else if (uF.parseToInt(hmNextApproval.get(rs.getString("nominated_details_id"))) == uF.parseToInt(hmMemNextApproval.get(rs.getString("nominated_details_id") + "_" + userType)) && uF.parseToInt(hmNextApproval.get(rs.getString("nominated_details_id"))) > 0) {
//					System.out.println("In else::");
					
					alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveDeny('1','"+ rs.getString("nominated_details_id")+"','"+userType+"','"+getCurrUserType()+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
							" <a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','" + rs.getString("nominated_details_id") + "','"+userType+"','"+getCurrUserType()+"');\">" +
									"<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a> ");
					
					alInner.add(sbCheckApproveby.toString()); //4
				} else if (uF.parseToInt(hmNextApproval.get(rs.getString("nominated_details_id"))) < uF.parseToInt(hmMemNextApproval.get(rs.getString("nominated_details_id") + "_" + userType))
						|| (uF.parseToInt(hmNextApproval.get(rs.getString("nominated_details_id"))) == 0 && uF.parseToInt(hmNextApproval.get(rs.getString("nominated_details_id"))) == uF.parseToInt(hmMemNextApproval.get(rs.getString("nominated_details_id") + "_" + userType)))) {
					
//					System.out.println("in else if");
					// if(!checkEmpList.contains(strSessionEmpId) &&
					// (strUserType.equalsIgnoreCase(ADMIN) ||
					// strUserType.equalsIgnoreCase(HRMANAGER))) {
					if (rs.getInt("approve_status") == 0) {
//						System.out.println("strUserType-===>"+strUserType);
						if (strUserType.equalsIgnoreCase(ADMIN) && uF.parseToInt(strUserTypeId) == uF.parseToInt(userType)) {
							alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveDeny('1','"+ rs.getString("nominated_details_id")+"','','"+getCurrUserType()+"');\">" +
									"<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve Leave\"></i></a> "
									+ "&nbsp;<a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','" + rs.getString("nominated_details_id") + "','"+userType+"','"+getCurrUserType()+"');\">" +
											"<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Deny Leave\"></i></a> "); //8
						} else {
							
							StringBuffer sbWork = new StringBuffer();
							/*sbWork.append("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
							sbWork.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Waiting for workflow\"></i>");
							
//							System.out.println("checkGHRInWorkflow-===>"+checkGHRInWorkflow);
							if (!checkGHRInWorkflow) {
								sbWork.append("<a href=\"javascript:void(0);\" onclick=\"approveDeny('1','"+ rs.getString("nominated_details_id")+"','','"+getCurrUserType()+"');\">" +
										"<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve Leave (" + ADMIN+ ")\"></i></a> "
										+ "&nbsp;<a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','" + rs.getString("nominated_details_id") + "','','"+getCurrUserType()+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Deny Leave (" + ADMIN+ ")\"></i></a> ");
							}
							alInner.add(sbWork.toString()); 
						}
						alInner.add(sbCheckApproveby.toString()); 
						
					} else if (rs.getInt("approve_status") == 1) {
						alInner.add("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved \"></i>"); //8
						alInner.add(sbCheckApproveby.toString()); 
					} else {
						alInner.add("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i>"); //8
						alInner.add(sbCheckApproveby.toString()); 
					}
					
				} else {
					// alInner.add("<img title=\"Cancel Leave\" src=\"images1/icons/pullout.png\" border=\"0\" />");
					
					if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN)) {
					
						alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveDeny('1','"+ rs.getString("nominated_details_id")+"','"+userType+"','"+getCurrUserType()+"');\">" +
								"<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve Leave\"></i></a> "
								+ "&nbsp;<a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','"+ rs.getString("nominated_details_id") + "','"+userType+"','"+getCurrUserType()+"');\">" +
										"<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Deny Leave\"></i></a> "); //8
					} else {
						/*alInner.add("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Waiting for workflow\" style=\"color:#f7ee1d\"></i>"); //8
						
					}
					alInner.add(sbCheckApproveby.toString()); 
				}
				
				alInner.add(rs.getString("approve_status")); 
				alInner.add(uF.showData(hmUserTypeName.get(userType), "")); 
				StringBuilder sb = new StringBuilder();
				requestList.add(alInner);
			}
			
			
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

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
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

	public String getFromPage() {
		return fromPage;
	}


	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}


	public String getF_strWLocation() {
		return f_strWLocation;
	}


	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}


	public String getF_level() {
		return f_level;
	}


	public void setF_level(String f_level) {
		this.f_level = f_level;
	}


	public String getF_department() {
		return f_department;
	}


	public void setF_department(String f_department) {
		this.f_department = f_department;
	}


	public String getF_service() {
		return f_service;
	}


	public void setF_service(String f_service) {
		this.f_service = f_service;
	}


	public String getF_employeType() {
		return f_employeType;
	}


	public void setF_employeType(String f_employeType) {
		this.f_employeType = f_employeType;
	}


	public String getF_grade() {
		return f_grade;
	}


	public void setF_grade(String f_grade) {
		this.f_grade = f_grade;
	}
	
	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}
	
}
