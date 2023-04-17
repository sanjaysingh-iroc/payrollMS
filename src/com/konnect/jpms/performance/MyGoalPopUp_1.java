package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPerspective;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MyGoalPopUp_1 extends ActionSupport implements
		ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;
	
	private String goalid;
	private String goaltype;
	
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
		
//		System.out.println("from java popup ==>" + getFromPage());
		return LOAD;

	}
	
	
	private void loadData(UtilityFunctions uF) {
		orientationList = new FillOrientation(request).fillOrientation();
		perspectiveList = new FillPerspective(request).fillPerspective();
		getOrientationDetailsList();
		getPerspectiveDetailsList();
	
		frequencyList = new FillFrequency(request).fillFrequency();
		
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
	
		if(userlocation != null &&  userlocation.length() > 0 && userlocation.equals(" ")){
			empList = new FillEmployee(request).fillEmployeeNameByLocation(userlocation);
		}
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
		}else{
			empList = new FillEmployee(request).fillEmployeeNameByOrg(uF.parseToInt(getStrID()));
			getEmpDetailsList();
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
			pst = con.prepareStatement("select e.wlocation_id from employee_official_details e where e.emp_id=?");
			
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			System.out.println("pst===>"+pst);
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
		for (int i = 0; perspectiveList !=null && i < perspectiveList.size(); i++) {
			FillPerspective fillPerspective = perspectiveList.get(i);
			sb.append("<option value=\"" + fillPerspective.getPerspectiveId() + "\">"+ fillPerspective.getPerspectiveName() + "</option>");
		}
		request.setAttribute("perspective", sb.toString());
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
	
	
}
