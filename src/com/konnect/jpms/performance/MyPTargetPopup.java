package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MyPTargetPopup extends ActionSupport implements
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
	
	private String userlocation;
	
	private String operation;
	private String type;
	
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		orientationList = new FillOrientation(request).fillOrientation();
		getOrientationDetailsList();

		levelList = new FillLevel(request).fillLevel();
		getLevelDetailsList();

		gradeList = new FillGrade(request).fillGrade();
		getgradeDetailsList();
		
		desigList = new FillDesig(request).fillDesig();
		
		frequencyList=new FillFrequency(request).fillFrequency();
		
		levelList = new FillLevel(request).fillLevel();
		workList = new FillWLocation(request).fillWLocation();
		departmentList = new FillDepartment(request).fillDepartment();
		organisationList = new FillOrganisation(request).fillOrganisation();
		
		
		desigList = new FillDesig(request).fillDesig();

		gradeList = new FillGrade(request).fillGrade();

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
		}else{
		empList = new FillEmployee(request).fillEmployeeName();
		getEmpDetailsList();
		}
		attributeList = new FillAttribute().fillAttribute();
		
		getattribute(); 		
		
		getGoalTypeDetails();

		return LOAD;

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
		try {
			con = db.makeConnection(con);
			
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

		for (int i = 0; i < attributeList.size(); i++) {
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

	public String getOperation() {
		return operation;
	}


	public void setOperation(String operation) {
		this.operation = operation;
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
}
