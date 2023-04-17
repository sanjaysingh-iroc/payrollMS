package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPerspective;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MyGoalPopUp extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;
	
	private String goalid;
	private String goaltype;
	private String goalTypePG;
	
	
	private List<FillOrientation> orientationList;
	private List<FillLevel> levelList;
	private List<FillGrade> gradeList;
	private List<FillEmployee> empList; 
	private List<FillAttribute> attributeList;
	private List<FillDesig> desigList;
	
	private List<FillFrequency> frequencyList;
	
	private List<FillWLocation> workList;
	private List<FillDepartment> departmentList;
	private List<FillOrganisation> organisationList;
	private List<FillPerspective> perspectiveList;
	
	private List<FillYears> yearsList;
	private List<FillYears> yearTypeList;
	private List<FillMonth> monthsList;
	private List<FillMonth> quartersList;
	private List<FillMonth> halfYearsList;
	
	private String userlocation;
	
	private String operation;
	private String type;
	private String typeas;
	private String strID;
	private String from;
	private String fromPage;
	
	private String currUserType;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
//		attributeList = new FillAttribute(request).fillAttribute();
		attributeList = new ArrayList<FillAttribute>();
		getattribute();
		
		getTeamGoals();
		getGoalTypeDetails();
		getOrgName();
		
		loadData(uF);
		//===start parvez date: 04-09-2021===
		getPolicyDetails(uF);
		//===end parvez date: 04-09-2021===
		
//		System.out.println("from java popup ==>" + getFromPage());
		return LOAD;

	}
	
	
	private void loadData(UtilityFunctions uF) {
		orientationList = new FillOrientation(request).fillOrientation();
		perspectiveList = new FillPerspective(request).fillPerspective();
		getOrientationDetailsList();
		getPerspectiveDetailsList();
	
		frequencyList = new FillFrequency(request).fillFrequency();
		
		yearsList = new FillYears(request).fillYearsFromCurrentToNext10Year();
		yearTypeList = new FillYears().fillYearType();
		monthsList = new FillMonth().fillMonth();
		quartersList = new FillMonth().fillQuarters();
		halfYearsList = new FillMonth().fillHalfYears();
		
		getFrequenyDetailsList();
		
		organisationList = new FillOrganisation(request).fillOrganisation();
		workList = new FillWLocation(request).fillWLocation(getStrID());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getStrID()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrID()));
		getLevelDetailsList();
		gradeList = new FillGrade(request).fillGradeByOrg(uF.parseToInt(getStrID()));
		getgradeDetailsList();
		desigList = new FillDesig(request).fillDesig(uF.parseToInt(getStrID()));
		
		userlocation = getLocation();   
		
		empList = new FillEmployee(request).fillEmployeeNameByLocation(userlocation);
		if(goaltype!=null && (goaltype.equals("3") || goaltype.equals("4"))){
			 //&& (goaltype.equals("3") || goaltype.equals("4"))
			String supervisorId="";
			if(goaltype.equals("3")){
				supervisorId=getSupervisorID();
			}else if(goaltype.equals("4")){
				supervisorId=getSupervisorIDIndividual();
			}
			empList = new FillEmployee(request).fillEmployeeNameBySupervisor(supervisorId);
			getEmpDetailsList();
			request.setAttribute("supervisorId", supervisorId);
		} else {
			empList = new FillEmployee(request).fillEmployeeNameByOrg(uF.parseToInt(getStrID()));
			getEmpDetailsList();
		}
		getData();//	Created By Dattatray Date:08-09-21
	}

//	Created By Dattatray Date:08-09-21
	private void getData() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uf = new UtilityFunctions();
		String strAttributeName = "";
		String strAttributeId = "";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_attribute limit 1");
			rs = pst.executeQuery();
			while (rs.next()) {
				strAttributeId = rs.getString("arribute_id");
				strAttributeName = rs.getString("attribute_name");
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select * from appraisal_frequency where appraisal_frequency_id !=2 limit 1");
			rs = pst.executeQuery();
			String strFeqId = "";
			String strFeqName = "";
			while(rs.next()){
				strFeqId = rs.getString("appraisal_frequency_id");
				strFeqName = rs.getString("frequency_name");
			}	
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from apparisal_orientation where apparisal_orientation_id = 6");
			rs = pst.executeQuery();
			String strOrientationId = "";
			String strOrientationName = "";
			while(rs.next()){
				strOrientationId = rs.getString("apparisal_orientation_id");
				strOrientationName = rs.getString("orientation_name");
			}	
			rs.close();
			pst.close();
			
			
			request.setAttribute("strAttributeId", strAttributeId);
			request.setAttribute("strAttributeName", strAttributeName);
			
			request.setAttribute("strFeqId", strFeqId);
			request.setAttribute("strFeqName", strFeqName);
			
			request.setAttribute("strOrientationId", strOrientationId);
			request.setAttribute("strOrientationName", strOrientationName);
			
//			System.out.println("strAttributeId : "+strAttributeId);
//			System.out.println("strAttributeName : "+strAttributeName);
//			
//			System.out.println("strFeqId : "+strFeqId);
//			System.out.println("strFeqName : "+strFeqName);
//			
//			System.out.println("strOrientationId : "+strOrientationId);
//			System.out.println("strOrientationName : "+strOrientationName);
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	private void getFrequenyDetailsList() {

		StringBuilder sb = new StringBuilder("");

		for (int i = 0; i < frequencyList.size(); i++) {
			FillFrequency fillFrequency = frequencyList.get(i);

			sb.append("<option value=\"" + fillFrequency.getId() + "\">"
					+ fillFrequency.getName() + "</option>");
		}
		request.setAttribute("frequencyOption", sb.toString());
		
		StringBuilder sbdates = new StringBuilder("");
		for (int i = 1; i <=31; i++) {
			sbdates.append("<option value=\"" + i + "\">" + i + "</option>");
		}
		request.setAttribute("datesOption", sbdates.toString());

	}


	private void getOrgName() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String orgName = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpLocation = CF.getEmpWlocationMap(con);
			Map<String, String> hmWLocation = CF.getWLocationMap(con, null, null);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			request.setAttribute("hmEmpLocation", hmEmpLocation);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			
			StringBuilder sb = new StringBuilder("");
			StringBuilder sbAjax = new StringBuilder("");
			pst = con.prepareStatement("select * from appraisal_element order by appraisal_element_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				sb.append("<option value='" + rs.getString("appraisal_element_id") + "'>"+ rs.getString("appraisal_element_name") + "</option>");
				sbAjax.append("<option value=\"" + rs.getString("appraisal_element_id") + "\">"+ rs.getString("appraisal_element_name") + "</option>");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("elementOptions", sb.toString());
			request.setAttribute("elementOptionsAjax", sbAjax.toString());
			
			pst = con.prepareStatement("select od.org_id,od.org_name from org_details od where od.org_id = ?");
			pst.setInt(1, uF.parseToInt(strID));
			rs = pst.executeQuery();
			while (rs.next()) {
				orgName = rs.getString("org_name");
			}
			rs.close();
			pst.close();
			request.setAttribute("orgName",orgName);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	private void getTeamGoals() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		StringBuilder sb = new StringBuilder("");
		try {
			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from goal_details where emp_ids like '%," + strSessionEmpId + ",%' and goal_type = 3 order by goal_id ");
			pst = con.prepareStatement("select * from goal_details where goal_type=3 and goal_id in(select goal_parent_id from goal_details " +
					" where goal_type=4 and emp_ids like '%,"+strSessionEmpId+",%') and (is_close is null or is_close = false) order by goal_id desc");
			rs = pst.executeQuery();
//			System.out.println("pst ===> "+pst);
			while (rs.next()) {
				sb.append("<option value='" + rs.getString("goal_id") + "'>" + rs.getString("goal_title") + "</option>");
			}
			rs.close();
			pst.close();
			request.setAttribute("optionTeamGoals",sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private String getLocation() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String location = "";

		con = db.makeConnection(con);
		try {
			pst = con
					.prepareStatement("select e.wlocation_id from employee_official_details e where e.emp_id=?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
			while (rst.next()) {
				location = rst.getString(1);
			}
			rst.close();
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return location;
	}


	private void getGoalTypeDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> hmGoalType = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
		    request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			pst = con.prepareStatement("select * from goal_type_details order by goal_type_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmGoalType.put(rs.getString("goal_type_id"),rs.getString("goal_type_name"));				
			}
			rs.close();
			pst.close();
			
			
			
			request.setAttribute("hmGoalType",hmGoalType);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private String getSupervisorIDIndividual() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uf = new UtilityFunctions();
		String supervisor_id="";
		try {

			con = db.makeConnection(con);

			String team_parentId="";
			pst = con.prepareStatement("select goal_parent_id from goal_details where goal_id=?");
			pst.setInt(1,uf.parseToInt(goalid));			
			rs = pst.executeQuery();
			while (rs.next()) {
				team_parentId=rs.getString("goal_parent_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select emp_ids from goal_details where goal_id=?");
			pst.setInt(1,uf.parseToInt(team_parentId));			
			rs = pst.executeQuery();
			while (rs.next()) {
				supervisor_id=rs.getString("emp_ids");
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
		return supervisor_id;
	}

	private String getSupervisorID() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uf = new UtilityFunctions();
		String supervisor_id="";
		try {

			con = db.makeConnection(con);

			pst = con.prepareStatement("select emp_ids from goal_details where goal_id=?");
			pst.setInt(1,uf.parseToInt(goalid));			
			rs = pst.executeQuery();
			while (rs.next()) {
				supervisor_id=rs.getString("emp_ids");
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
		return supervisor_id;
	}

//	private String getAppendData(String strID, Map<String, String> mp) {
//		StringBuilder sb = new StringBuilder();
//		Map<String, String> hmDesignation = CF.getEmpDesigMap();
//		if (strID != null && !strID.equals("")) {
//
//			if (strID.contains(",")) {
//
//				String[] temp = strID.split(",");
//
//				for (int i = 0; i < temp.length; i++) {
//					if (i == 0) {
//						sb.append(mp.get(temp[i].trim())+"("+hmDesignation.get(temp[i].trim())+")");
//					} else {
//						sb.append("," + mp.get(temp[i].trim())+"("+hmDesignation.get(temp[i].trim())+")");
//					}
//				}
//			} else {
//				return mp.get(strID)+"("+hmDesignation.get(strID)+")";
//			}
//
//		} else {
//			return null;
//		}
//
//		return sb.toString();
//	}
	
	public void getattribute() {

		StringBuilder sb = new StringBuilder("");

		for (int i = 0; attributeList!=null && i < attributeList.size(); i++) {
			FillAttribute fillAttribute = attributeList.get(i);

			sb.append("<option value='" + fillAttribute.getId() + "'>"
					+ fillAttribute.getName() + "</option>");

		}

		request.setAttribute("attribute", sb.toString());

	}

	private void getEmpDetailsList() {
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < empList.size(); i++) {
			FillEmployee fillEmployee = empList.get(i);

			sb.append("<option value='" + fillEmployee.getEmployeeId() + "'>"
					+ fillEmployee.getEmployeeCode() + "</option>");

		}
		request.setAttribute("empListOption", sb.toString()); 

	}

	private void getgradeDetailsList() {
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < gradeList.size(); i++) {
			FillGrade fillGrade = gradeList.get(i);

			sb.append("<option value='" + fillGrade.getGradeId() + "'>"
					+ fillGrade.getGradeCode() + "</option>");

		}
		request.setAttribute("gradeListOption", sb.toString()); 
	}

	private void getLevelDetailsList() {
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < levelList.size(); i++) {
			FillLevel fillLevel = levelList.get(i);
			sb.append("<option value='" + fillLevel.getLevelId() + "'>"
					+ fillLevel.getLevelCodeName() + "</option>");
		}
		request.setAttribute("levelListOption", sb.toString());
	}

	
	public void getOrientationDetailsList() {
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < orientationList.size(); i++) {
			FillOrientation fillOrientation = orientationList.get(i);
			sb.append("<option value=\"" + fillOrientation.getId() + "\">"+ fillOrientation.getName() + "</option>");
		}
		request.setAttribute("orientation", sb.toString());

	}
	
	public void getPerspectiveDetailsList() {
		StringBuilder sb = new StringBuilder("");
//		System.out.println("perspectiveList ===>> " + perspectiveList!=null ? perspectiveList.size() : 0);
		for (int i = 0; perspectiveList !=null && i < perspectiveList.size(); i++) {
			FillPerspective fillPerspective = perspectiveList.get(i);
			sb.append("<option value=\"" + fillPerspective.getPerspectiveId() + "\">"+ fillPerspective.getPerspectiveName() + "</option>");
		}
		request.setAttribute("perspective", sb.toString());
	}
	
	//===start parvez 04-09-2021===
	
	private void getPolicyDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String policy_id = null;
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			String empLevelId = CF.getEmpLevelId(con, strSessionEmpId);
//			System.out.println("MGPU/509---empLevelId="+empLevelId);
//			System.out.println("MGPU/510---strSessionEmpId="+strSessionEmpId);
//			System.out.println("MGPU/511--levelList="+getLevelList());
			
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
			
			pst = con.prepareStatement("select policy_id from work_flow_policy_details where type='"+WORK_FLOW_PERSONAL_GOAL+"' and level_id=? and wlocation_id=?");
			pst.setInt(1, uF.parseToInt(empLevelId));
			pst.setInt(2, uF.parseToInt(getLocation()));
//			System.out.println("MGPU/518--pst====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				policy_id=rs.getString("policy_id");
			}
			rs.close();
			pst.close();
//			System.out.println("policy_id ===>> " + policy_id);
			
			if(uF.parseToInt(policy_id) == 0){
				pst = con.prepareStatement("select policy_count from work_flow_member wfm,work_flow_policy wfp where wfp.group_id=wfm.group_id " +
						"and wfp.work_flow_member_id=wfm.work_flow_member_id and wfm.wlocation_id=? and wfm.is_default = true");
				pst.setInt(1, uF.parseToInt(getLocation()));
//				System.out.println("MGPU/531--pst="+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					policy_id=rs.getString("policy_count");
				}
				rs.close();
				pst.close();
			}
			
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			if(hmUserTypeIdMap==null) hmUserTypeIdMap = new HashMap<String, String>();
			
			Map<String, String> hmWorkflowData = new HashMap<String, String>();
			String workflowEmpId = null;
			pst = con.prepareStatement("select user_type_id, emp_id from work_flow_details where effective_id =? and effective_type='"+WORK_FLOW_PERSONAL_GOAL+"' order by member_position");
			pst.setInt(1, uF.parseToInt(goalid));
//			System.out.println("MGPU/547--pst====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				hmWorkflowData.put(rs.getString("user_type_id"), rs.getString("emp_id"));
				workflowEmpId = rs.getString("emp_id");
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(policy_id)>0) {
				
				pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
						" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
				pst.setInt(1,uF.parseToInt(policy_id));
				rs=pst.executeQuery();
				Map<String,List<String>> hmMemberMap=new LinkedHashMap<String, List<String>>();
				while(rs.next()) {
					List<String> innerList=new ArrayList<String>();
					innerList.add(rs.getString("member_type"));
					innerList.add(rs.getString("member_id"));
					innerList.add(rs.getString("member_position"));
					innerList.add(rs.getString("work_flow_mem"));
					innerList.add(rs.getString("work_flow_member_id"));
					
					hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
				}
				rs.close();
				pst.close();
				
				
				Map<String,String> hmMemberOption=new LinkedHashMap<String,String>();
				
//				System.out.println("hmMemberMap ===>> " + hmMemberMap);
				Iterator<String> it=hmMemberMap.keySet().iterator();
				while(it.hasNext()) {
					String work_flow_member_id=it.next();
					List<String> innerList=hmMemberMap.get(work_flow_member_id);
					
					if(uF.parseToInt(innerList.get(0))==1) {
						int memid=uF.parseToInt(innerList.get(1));
						
						switch(memid) {
						
						case 1:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname, epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=1 "
										+ " and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
										+ " and epd.is_alive=true order by epd.emp_fname ");
								rs = pst.executeQuery();
								List<List<String>> outerList=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									alList.add(rs.getString("emp_lname"));
									
									outerList.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList!=null && !outerList.isEmpty()) {
									StringBuilder sbComboBox=new StringBuilder();
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList.size();i++) {
										List<String> alList=outerList.get(i);
										sbComboBox.append("<option value=\""+alList.get(0)+"\""+(((uF.parseToInt(hmWorkflowData.get("1")) == 0 && i == 0) || uF.parseToInt(hmWorkflowData.get("1")) == uF.parseToInt(alList.get(0))) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									}
									sbComboBox.append("</select>");								
									
									String optionTr="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr);
								}
								break;
							
						case 2:
								StringBuilder sbQuery = new StringBuilder();
								sbQuery.append("select * from (select distinct(supervisor_emp_id) as supervisor_emp_id from employee_official_details where emp_id > 0 and wlocation_id=? ");
								sbQuery.append(" and grade_id in (select grade_id from level_details l, designation_details di, grades_details gd where l.level_id = di.level_id and di.designation_id = gd.designation_id and l.level_id = ?) and supervisor_emp_id!=0 ");
								if(strUserType !=null && strUserType.equals(EMPLOYEE)) {
									sbQuery.append(" and supervisor_emp_id = (select supervisor_emp_id from employee_official_details where emp_id ="+strSessionEmpId+")");
								}
								sbQuery.append(" ) as a, employee_personal_details epd,user_details ud where a.supervisor_emp_id=epd.emp_per_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE' and epd.is_alive=true order by epd.emp_fname");
								pst = con.prepareStatement(sbQuery.toString());
								pst.setInt(1, uF.parseToInt(getLocation()));
								pst.setInt(2, uF.parseToInt(empLevelId));
								rs = pst.executeQuery(); 
//								System.out.println("MGPU/643--pst ===>> " + pst);
								List<List<String>> outerList11=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(hmUserTypeIdMap.get(MANAGER));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									alList.add(rs.getString("emp_lname"));
									
									outerList11.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList11!=null && !outerList11.isEmpty()) {
									StringBuilder sbComboBox11=new StringBuilder();
									sbComboBox11.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox11.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList11.size();i++) {
										List<String> alList=outerList11.get(i);
										sbComboBox11.append("<option value=\""+alList.get(0)+"\""+(((uF.parseToInt(hmWorkflowData.get("2")) == 0 && i == 0) || uF.parseToInt(hmWorkflowData.get("2")) == uF.parseToInt(alList.get(0))) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									}
									sbComboBox11.append("</select>");								
									
									String optionTr11="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox11.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr11);
								}
							
								break;
							
						case 3:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=3 "
												+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
												+ " and grade_id in (select grade_id from level_details l, designation_details di, grades_details gd where l.level_id = di.level_id and di.designation_id = gd.designation_id and l.level_id = ?)"
												+ " and epd.is_alive=true order by epd.emp_fname ");
								pst.setInt(1, uF.parseToInt(getLocation()));
								pst.setInt(2, uF.parseToInt(empLevelId));
								rs = pst.executeQuery();
//								System.out.println("MGPU/692--pst="+pst);
								List<List<String>> outerList1=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									
									alList.add(rs.getString("emp_lname"));
									
									outerList1.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList1!=null && !outerList1.isEmpty()) {
									StringBuilder sbComboBox1=new StringBuilder();
									sbComboBox1.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox1.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList1.size();i++) {
										List<String> alList=outerList1.get(i);
										sbComboBox1.append("<option value=\""+alList.get(0)+"\""+(((uF.parseToInt(hmWorkflowData.get("3")) == 0 && i == 0) || uF.parseToInt(hmWorkflowData.get("3")) == uF.parseToInt(alList.get(0))) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									}
									sbComboBox1.append("</select>");								
									
									String optionTr1="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox1.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr1);
								}
								break;
						
						case 4:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=4 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+getLocation()+",%' and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
										+ " and epd.is_alive=true order by epd.emp_fname");
//								pst.setInt(1, uF.parseToInt(getStrWlocation()));
								rs = pst.executeQuery();
//								System.out.println("MGPU/739--pst="+pst);
								List<List<String>> outerList2=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname"));
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									alList.add(rs.getString("emp_lname"));
									
									outerList2.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList2!=null && !outerList2.isEmpty()) {
									StringBuilder sbComboBox2=new StringBuilder();
									sbComboBox2.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox2.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList2.size();i++) {
										List<String> alList=outerList2.get(i);
										sbComboBox2.append("<option value=\""+alList.get(0)+"\""+(((uF.parseToInt(hmWorkflowData.get("4")) == 0 && i == 0) || uF.parseToInt(hmWorkflowData.get("4")) == uF.parseToInt(alList.get(0))) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									}
									sbComboBox2.append("</select>");								
									
									String optionTr2="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox2.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr2);
								}
								break;
						
						case 5:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
									"employee_personal_details epd where ud.usertype_id=5 and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+getLocation()+",%' " +
									"and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and epd.is_alive=true " +
									" union " +
									"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
									"employee_personal_details epd where ud.usertype_id=1 and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and " +
									"epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and epd.is_alive=true");
//								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
//										+ " employee_official_details eod,employee_personal_details epd where (ud.usertype_id=5 or ud.usertype_id=1)"
//										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+getStrWlocation()+",%' and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
//										+ " and epd.is_alive=true order by epd.emp_fname");
//								System.out.println("MGPU/790--pst="+pst);
								rs = pst.executeQuery();
								List<List<String>> outerList3=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									
									alList.add(rs.getString("emp_lname"));
									
									outerList3.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList3!=null && !outerList3.isEmpty()) {
									StringBuilder sbComboBox3=new StringBuilder();
									sbComboBox3.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox3.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList3.size();i++) {
										List<String> alList=outerList3.get(i);
										sbComboBox3.append("<option value=\""+alList.get(0)+"\""+(((uF.parseToInt(hmWorkflowData.get("5")) == 0 && i == 0) || uF.parseToInt(hmWorkflowData.get("5")) == uF.parseToInt(alList.get(0))) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									}
									sbComboBox3.append("</select>");								
									
									String optionTr3="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox3.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr3);
								}
								break;
							
						case 6:
							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
								"employee_personal_details epd where ud.usertype_id=6 and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+getLocation()+",%' " +
								"and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and epd.is_alive=true " +
								" union " +
								"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
								"employee_personal_details epd where ud.usertype_id=1 and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and " +
								"epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and epd.is_alive=true");
//							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
//										+ " employee_official_details eod,employee_personal_details epd where (ud.usertype_id=6 or ud.usertype_id=1) "
//										+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
//										+ " and epd.is_alive=true order by epd.emp_fname");
//								pst.setInt(1, uF.parseToInt(getStrWlocation()));
								rs = pst.executeQuery();
//								System.out.println("MGPU/845--pst="+pst);
								List<List<String>> outerList4=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									
									alList.add(rs.getString("emp_lname"));
									
									outerList4.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList4!=null && !outerList4.isEmpty()) {
									StringBuilder sbComboBox4=new StringBuilder();
									sbComboBox4.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox4.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList4.size();i++) {
										List<String> alList=outerList4.get(i);
										sbComboBox4.append("<option value=\""+alList.get(0)+"\""+(((uF.parseToInt(hmWorkflowData.get("6")) == 0 && i == 0) || uF.parseToInt(hmWorkflowData.get("6")) == uF.parseToInt(alList.get(0))) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									}
									sbComboBox4.append("</select>");								
									
									String optionTr4="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox4.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr4);
								}
								break;
							
						case 7:
							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
									"employee_personal_details epd where ud.usertype_id=7 and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+getLocation()+",%' " +
									"and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and epd.is_alive=true " +
									" union " +
									"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
									"employee_personal_details epd where ud.usertype_id=1 and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and " +
									"epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and epd.is_alive=true");
								
								rs = pst.executeQuery();
//								System.out.println("MGPU/895--pst="+pst);
								List<List<String>> outerList5=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(hmUserTypeIdMap.get(HRMANAGER));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									alList.add(rs.getString("emp_lname"));
									
									outerList5.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList5!=null && !outerList5.isEmpty()) {
									StringBuilder sbComboBox5=new StringBuilder();
									sbComboBox5.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox5.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList5.size();i++) {
										List<String> alList=outerList5.get(i);
										sbComboBox5.append("<option value=\""+alList.get(0)+"\""+(((uF.parseToInt(hmWorkflowData.get("7")) == 0 && i == 0) || uF.parseToInt(hmWorkflowData.get("7")) == uF.parseToInt(alList.get(0))) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									}
									sbComboBox5.append("</select>");								
									
									String optionTr5="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox5.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr5);
								}
								break;	
								
						case 13:
							pst = con.prepareStatement("select * from  employee_personal_details epd, employee_official_details eod,user_details ud where epd.emp_per_id=eod.emp_id "
									+" and eod.emp_id = ud.emp_id and epd.emp_per_id = ud.emp_id and usertype_id = 13 and wlocation_id=? and ud.status='ACTIVE' and epd.is_alive=true "
									+" order by epd.emp_fname");
							pst.setInt(1, uF.parseToInt(getLocation()));
//							System.out.println("MGPU/939--pst="+pst);
							rs = pst.executeQuery();
							
							List<List<String>> outerHODList=new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList=new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname")); 
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);
								
								
								alList.add(rs.getString("emp_lname"));
								
								outerHODList.add(alList);									
							}
							rs.close();
							pst.close();
							
							if(outerHODList!=null && !outerHODList.isEmpty()){
								StringBuilder sbComboBox11=new StringBuilder();
								sbComboBox11.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								sbComboBox11.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
								for(int i=0;i<outerHODList.size();i++){
									List<String> alList=outerHODList.get(i);
									sbComboBox11.append("<option value=\""+alList.get(0)+"\""+(((uF.parseToInt(hmWorkflowData.get("13")) == 0 && i == 0) || uF.parseToInt(hmWorkflowData.get("13")) == uF.parseToInt(alList.get(0))) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
//									sbComboBox11.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
								}
								sbComboBox11.append("</select>");								
								
								String optionTr11="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox11.toString()+"</td></tr>";
								
								hmMemberOption.put(innerList.get(4), optionTr11);
							}
						
							break;
						
						}						
						
					} else if(uF.parseToInt(innerList.get(0))==3) {
						int memid=uF.parseToInt(innerList.get(1));
						
						
						List<List<String>> outerList=new ArrayList<List<String>>();
						pst = con.prepareStatement("select emp_id from specific_emp se,employee_personal_details epd where se.emp_id=epd.emp_per_id " +
								"and se.policy_id = ? and epd.is_alive=true order by epd.emp_fname"); 
						pst.setInt(1,uF.parseToInt(policy_id));
						rs = pst.executeQuery();
						while (rs.next()) {
							List<String> alList = new ArrayList<String>();
							alList.add(rs.getString("emp_id"));
							outerList.add(alList);
						}
						rs.close();
						pst.close();
						
						if(outerList!=null && !outerList.isEmpty()) {
							StringBuilder sbComboBox = new StringBuilder();
							sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
							String a = "";
							sbComboBox.append("<option value=\""+a+"\">Select "+innerList.get(3)+"</option>");
//							System.out.println("hmWorkflowData ===>> " + hmWorkflowData + " -- hmWorkflowData.get(memid) ===>> " + uF.parseToInt(hmWorkflowData.get(memid)));
							
							for(int i=0;i<outerList.size();i++) {
								List<String> alList=outerList.get(i);
								sbComboBox.append("<option value=\""+alList.get(0)+"\" "+((uF.parseToInt(workflowEmpId) == uF.parseToInt(alList.get(0)) ) ? " selected" : "")+">"+hmEmpCodeName.get(alList.get(0).trim())+"</option>");
							}
							sbComboBox.append("</select>");								
							
							String optionTr="<tr><td class=\"txtlabel alignRight\">Your work flow:<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
							
							hmMemberOption.put(innerList.get(4), optionTr);
						}
					}
				}
				request.setAttribute("hmMemberOption",hmMemberOption);
				request.setAttribute("policy_id",policy_id);
				
			}
						
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	//===end parvez date: 04-09-2021===
	
	
	public String getGoaltype() {
		return goaltype;
	}

	public void setGoaltype(String goaltype) {
		this.goaltype = goaltype;
	}

	public String getGoalid() {
		return goalid;
	}

	public void setGoalid(String goalid) {
		this.goalid = goalid;
	}

	public List<FillOrientation> getOrientationList() {
		return orientationList;
	}

	public void setOrientationList(List<FillOrientation> orientationList) {
		this.orientationList = orientationList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public List<FillAttribute> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<FillAttribute> attributeList) {
		this.attributeList = attributeList;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public List<FillFrequency> getFrequencyList() {
		return frequencyList;
	}

	public void setFrequencyList(List<FillFrequency> frequencyList) {
		this.frequencyList = frequencyList;
	}

	public List<FillWLocation> getWorkList() {
		return workList;
	}

	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getUserlocation() {
		return userlocation;
	}

	public void setUserlocation(String userlocation) {
		this.userlocation = userlocation;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getTypeas() {
		return typeas;
	}

	public void setTypeas(String typeas) {
		this.typeas = typeas;
	}

	public String getStrID() {
		return strID;
	}

	public void setStrID(String strID) {
		this.strID = strID;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public List<FillPerspective> getPerspectiveList() {
		return perspectiveList;
	}

	public void setPerspectiveList(List<FillPerspective> perspectiveList) {
		this.perspectiveList = perspectiveList;
	}


	public List<FillYears> getYearsList() {
		return yearsList;
	}


	public void setYearsList(List<FillYears> yearsList) {
		this.yearsList = yearsList;
	}


	public List<FillYears> getYearTypeList() {
		return yearTypeList;
	}


	public void setYearTypeList(List<FillYears> yearTypeList) {
		this.yearTypeList = yearTypeList;
	}


	public List<FillMonth> getMonthsList() {
		return monthsList;
	}


	public void setMonthsList(List<FillMonth> monthsList) {
		this.monthsList = monthsList;
	}


	public List<FillMonth> getQuartersList() {
		return quartersList;
	}


	public void setQuartersList(List<FillMonth> quartersList) {
		this.quartersList = quartersList;
	}


	public List<FillMonth> getHalfYearsList() {
		return halfYearsList;
	}


	public void setHalfYearsList(List<FillMonth> halfYearsList) {
		this.halfYearsList = halfYearsList;
	}

	public String getGoalTypePG() {
		return goalTypePG;
	}

	public void setGoalTypePG(String goalTypePG) {
		this.goalTypePG = goalTypePG;
	}
	
	
}