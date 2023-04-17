package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillManager;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class NewGoalPopUp extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	private String strUserType = null;
	private String strSessionEmpId = null;
	private  String strEmpOrgId = null;
	private String goalid;
	private String goaltype;
	private String strOrg;
	private String dataType;
	private String currUserType;

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
	private List<FillManager> managerList;
	
	private List<FillYears> yearsList;
	private List<FillYears> yearTypeList;
	private List<FillMonth> monthsList;
	private List<FillMonth> quartersList;
	private List<FillMonth> halfYearsList;
	
	private String userlocation;
	private String score;
	private String compGoalId;
	private String strDepart;
	private String strDepartName;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		attributeList = new ArrayList<FillAttribute>();
		
		getElementList(uF);
		getattribute(); 		
		
//		System.out.println("getStrOrg() ===>> " + getStrOrg());
		if(uF.parseToInt(getStrOrg())==0) {
			setStrOrg(strEmpOrgId);
		}
//		System.out.println("after getStrOrg() ===>> " + getStrOrg());
		
		frequencyList=new FillFrequency(request).fillFrequency();
	
		yearsList = new FillYears(request).fillYearsFromCurrentToNext10Year();
		yearTypeList = new FillYears().fillYearType();
		monthsList = new FillMonth().fillMonth();
		quartersList = new FillMonth().fillQuarters();
		halfYearsList = new FillMonth().fillHalfYears();
		
		managerList = new FillManager(request).fillManager();
		userlocation = getLocation();
		
		orientationList = new FillOrientation(request).fillOrientation();
		getOrientationDetailsList();
		workList = new FillWLocation(request).fillWLocation(getStrOrg());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getStrOrg()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
		getLevelDetailsList();
		desigList = new FillDesig(request).fillDesig(uF.parseToInt(getStrOrg()));
		gradeList = new FillGrade(request).fillGradeByOrg(uF.parseToInt(getStrOrg()));
		getgradeDetailsList();
		
		empList = new FillEmployee(request).fillEmployeeNameByLocation(userlocation);
		if(goaltype!=null && (goaltype.equals("3") || goaltype.equals("4"))){
			 //&& (goaltype.equals("3") || goaltype.equals("4"))
			String supervisorId="";
			if(goaltype.equals("3")){
				supervisorId = getSupervisorID();
			}else if(goaltype.equals("4")){
				supervisorId = getSupervisorIDIndividual();
				getEmployeeForIndividualGoal();
			}
//			System.out.println("supervisorId ============ =======>"+supervisorId);
			if(supervisorId!= null && !supervisorId.equals("")){
				supervisorId = supervisorId.substring(1, supervisorId.length()-1);
			}
//			System.out.println("supervisorId after ============ =======>"+supervisorId);
//			empList = new FillEmployee(request).fillEmployeeName(); // commented date 18 Sep 2017
//			empList = new FillEmployee(request).fillEmployeeNameByOrg(uF.parseToInt(getStrOrg()));
			empList = new FillEmployee(request).fillEmployeeNameOrgLocationDepartSBUDesigGrade(CF, getStrOrg(), null, getStrDepart(), null, null, null, null, null, false);
//			empList = new FillEmployee().fillEmployeeNameBySupervisor(supervisorId);
//			getEmpDetailsList();
			request.setAttribute("supervisorId", supervisorId);
		}else{
			empList = new FillEmployee(request).fillEmployeeNameByOrg(uF.parseToInt(getStrOrg()));
//			getEmpDetailsList();
		}
//		attributeList = new FillAttribute().fillAttribute();
		getGoalTypeDetails();

		return LOAD;
	}
	
	
	public void getElementList(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		String orgName = null;
		String orgID = null;
		try {
			
			StringBuilder sb = new StringBuilder("");
			
			con = db.makeConnection(con);
			Map<String, String> hmEmpLocation = CF.getEmpWlocationMap(con);
			Map<String, String> hmWLocation = CF.getWLocationMap(con,null, null);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			request.setAttribute("hmEmpLocation", hmEmpLocation);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			request.setAttribute("hmEmpName", hmEmpName);
			pst = con.prepareStatement("select * from appraisal_element order by appraisal_element_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				sb.append("<option value='" + rs.getString("appraisal_element_id") + "'>"+ rs.getString("appraisal_element_name") + "</option>");
			}
			request.setAttribute("elementOptions",sb.toString());
			
			pst = con.prepareStatement("select od.org_id,od.org_name from goal_details gd, org_details od where od.org_id=gd.org_id and gd.goal_id=?");
			pst.setInt(1, uF.parseToInt(goalid));
			rs = pst.executeQuery();
			while (rs.next()) {
				orgName = rs.getString("org_name");
				orgID = rs.getString("org_id");
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(getStrOrg())==0 && uF.parseToInt(orgID)>0) {
				setStrOrg(orgID);
			}
			
			request.setAttribute("orgName",orgName);
			request.setAttribute("orgID",orgID);
			
			Map<String, String> hmOrgName = new HashMap<String, String>();
			pst = con.prepareStatement("select od.org_id,od.org_name from org_details od"); // where od.org_id = ?
//			pst.setInt(1, uF.parseToInt(strOrg));
			rs = pst.executeQuery();
			while (rs.next()) {
//				orgName = rs.getString("org_name");
				hmOrgName.put(rs.getString("org_id"), rs.getString("org_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmOrgName",hmOrgName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private String getEmployeeForIndividualGoal() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String location = "";
		StringBuilder teamGoalEmpId =  new StringBuilder();
		String teamGoalPerentID = "";
		con = db.makeConnection(con);
		try {
			
			List<String> alWithDepartWithTeam = new ArrayList<String>();
			pst = con.prepareStatement("select * from goal_details where goal_id in (select goal_parent_id from goal_details where goal_id in (select goal_parent_id from goal_details where goal_id=?))");
			pst.setInt(1, uF.parseToInt(goalid));
			rst = pst.executeQuery();
			System.out.println("pst ===>> " + pst);
			while (rst.next()) {
				if(rst.getString("with_depart_with_team")!=null) {
					alWithDepartWithTeam = Arrays.asList(rst.getString("with_depart_with_team").split(","));
				}
			}
			rst.close();
			pst.close();
			
			if(alWithDepartWithTeam.size()==0 || uF.parseToBoolean(alWithDepartWithTeam.get(1))) {
				pst = con.prepareStatement("select * from goal_details where goal_id=?");
				pst.setInt(1, uF.parseToInt(goalid));
				rst = pst.executeQuery();
				while (rst.next()) {
					teamGoalEmpId.append(rst.getString("emp_ids"));
					teamGoalPerentID = rst.getString("goal_parent_id");
				}
				rst.close();
				pst.close();
				
				List<String> teamindEmplist = Arrays.asList(teamGoalEmpId.toString().split(","));
				if(!teamGoalPerentID.equals("")) {
					pst = con.prepareStatement("select * from goal_details where goal_id=?");
					pst.setInt(1, uF.parseToInt(teamGoalPerentID));
					rst = pst.executeQuery();
					while (rst.next()) {
						List<String> managerindEmplist = new ArrayList<String>();
						if(rst.getString("emp_ids") != null) {
							managerindEmplist = Arrays.asList(rst.getString("emp_ids").split(","));
						}
						for(int i=0; managerindEmplist != null && i<managerindEmplist.size(); i++) {
	//						System.out.println("ID Before ===> "+managerindEmplist.get(i));
							if(!teamindEmplist.contains(rst.getString("emp_ids"))) {
		//						System.out.println("ID After ===> "+managerindEmplist.get(i));
								teamGoalEmpId.append(managerindEmplist.get(i)+",");
							}
						}
						teamGoalPerentID = rst.getString("goal_parent_id");
					}
					rst.close();
					pst.close();
				}
			} else {
				empList = new FillEmployee(request).fillEmployeeNameOrgLocationDepartSBUDesigGrade(CF, getStrOrg(), null, getStrDepart(), null, null, null, null, null, false);
				for(int i=0; empList!=null && i<empList.size(); i++) {
					teamGoalEmpId.append(empList.get(i).getEmployeeId()+",");
				}
			}
			List<String> individualEmplist = Arrays.asList(teamGoalEmpId.toString().split(","));
			
			request.setAttribute("individualEmplist",individualEmplist);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return location;
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
			pst = con.prepareStatement("select e.wlocation_id from employee_official_details e where e.emp_id=?");
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
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
		    request.setAttribute("hmFeatureStatus", hmFeatureStatus);
		    
			String strOrgCurrId = CF.getOrgCurrencyIdByOrg(con, getStrOrg());
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			String strCurrency = "";
			if(uF.parseToInt(strOrgCurrId) > 0){
				Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(strOrgCurrId);
				if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
				strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
			} 
			request.setAttribute("strCurrency",strCurrency);
			
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
		UtilityFunctions uF = new UtilityFunctions();
		String supervisor_id="";
		try {

			con = db.makeConnection(con);

			String team_parentId="";
			pst = con.prepareStatement("select goal_parent_id from goal_details where goal_id=?");
			pst.setInt(1,uF.parseToInt(goalid));			
			rs = pst.executeQuery();
			while (rs.next()) {
				team_parentId=rs.getString("goal_parent_id");
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmDepartName = CF.getDepartmentMap(con, null, null);
			pst = con.prepareStatement("select emp_ids,depart_id from goal_details where goal_id=?");
			pst.setInt(1,uF.parseToInt(team_parentId));			
			rs = pst.executeQuery();
			while (rs.next()) {
				supervisor_id = rs.getString("emp_ids");
				setStrDepart(uF.showData(rs.getString("depart_id"), "0"));
				setStrDepartName(uF.showData(hmDepartName.get(rs.getString("depart_id")), "All Departments"));
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
		UtilityFunctions uF = new UtilityFunctions();
		String supervisor_id="";
		try {

			con = db.makeConnection(con);

			Map<String, String> hmDepartName = CF.getDepartmentMap(con, null, null);
			pst = con.prepareStatement("select emp_ids,depart_id from goal_details where goal_id=?");
			pst.setInt(1,uF.parseToInt(goalid));			
			rs = pst.executeQuery();
			while (rs.next()) {
				supervisor_id = rs.getString("emp_ids");
				setStrDepart(uF.showData(rs.getString("depart_id"), "0"));
				setStrDepartName(uF.showData(hmDepartName.get(rs.getString("depart_id")), "All Departments"));
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

		for (int i = 0; i < attributeList.size(); i++) {
			FillAttribute fillAttribute = attributeList.get(i);

			sb.append("<option value='" + fillAttribute.getId() + "'>"
					+ fillAttribute.getName() + "</option>");

		}

		request.setAttribute("attribute", sb.toString());

	}

//	private void getEmpDetailsList() {
//		StringBuilder sb = new StringBuilder("");
//		for (int i = 0; i < empList.size(); i++) {
//			FillEmployee fillEmployee = empList.get(i);
//
//			sb.append("<option value='" + fillEmployee.getEmployeeId() + "'>"
//					+ fillEmployee.getEmployeeCode() + "</option>");
//
//		}
//		request.setAttribute("empListOption", sb.toString()); 
//
//	}

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

			sb.append("<option value=\"" + fillOrientation.getId() + "\">"
					+ fillOrientation.getName() + "</option>");

		}
		request.setAttribute("orientation", sb.toString());

	}
	
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

	public List<FillManager> getManagerList() {
		return managerList;
	}

	public void setManagerList(List<FillManager> managerList) {
		this.managerList = managerList;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}
	
	public String getCompGoalId() {
		return compGoalId;
	}

	public void setCompGoalId(String compGoalId) {
		this.compGoalId = compGoalId;
	}

	public String getStrDepart() {
		return strDepart;
	}

	public void setStrDepart(String strDepart) {
		this.strDepart = strDepart;
	}

	public List<FillYears> getYearsList() {
		return yearsList;
	}

	public void setYearsList(List<FillYears> yearsList) {
		this.yearsList = yearsList;
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

	public List<FillYears> getYearTypeList() {
		return yearTypeList;
	}

	public void setYearTypeList(List<FillYears> yearTypeList) {
		this.yearTypeList = yearTypeList;
	}

	public String getStrDepartName() {
		return strDepartName;
	}

	public void setStrDepartName(String strDepartName) {
		this.strDepartName = strDepartName;
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
}
