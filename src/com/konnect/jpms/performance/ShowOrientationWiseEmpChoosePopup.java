package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.recruitment.GetIdealCandidateDesig;
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

public class ShowOrientationWiseEmpChoosePopup extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;

//	List<FillOrientation> orientationList;
	private List<FillLevel> levelList;
	private List<FillWLocation> workList;
	private List<FillDepartment> departmentList;
	private List<FillDesig> desigList;
	private List<FillGrade> gradeList;
	private List<FillEmployee> empList;
//	List<FillEmployee> finalizationList;
	private List<FillOrganisation> organisationList;
	
	
	private String hideID;
	private String lblID; 
	private String appID;
	private String type;
	private String hideIdValue;
	private String from;
	private List<String> empvalue = new ArrayList<String>();
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
//		orientationList = new FillOrientation().fillOrientation();
		levelList = new FillLevel(request).fillLevel();
		workList = new FillWLocation(request).fillWLocation();
		departmentList = new FillDepartment(request).fillDepartment();
		organisationList = new FillOrganisation(request).fillOrganisation();
		desigList = new FillDesig(request).fillDesig();
		gradeList = new FillGrade(request).fillGrade();
		empList = fillEmployeeName();
		
		UtilityFunctions uF = new UtilityFunctions();	
		
//		System.out.println("getHideID() ===>> " +getHideID());
//		System.out.println("getLblID() ===>> " +getLblID());
//		System.out.println("getType() ===>> " +getType());
//		System.out.println("getFrom() ===>> " +getFrom());
		
	    if(getAppID() != null && uF.parseToInt(getAppID()) > 0) {
	    	getAppraisalData();
	    }
	   
	    if(getHideIdValue() != null && getHideIdValue().length() > 1) {
	    	empvalue = Arrays.asList(getHideIdValue().substring(1, getHideIdValue().length()-1).split(","));
	    }
	   
//	    System.out.println("java getFRom==>"+getFrom());
		return LOAD;

	}
	
	public List<FillEmployee> fillEmployeeName() {

		List<FillEmployee> al = new ArrayList<FillEmployee>();
		UtilityFunctions uF = new UtilityFunctions();

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			StringBuilder sbque = new StringBuilder();
			
			sbque.append("select * from employee_official_details eod,employee_personal_details epd,user_details ud " +
					" where epd.emp_per_id=eod.emp_id and eod.emp_id = ud.emp_id and epd.is_alive=true ");
			if(getType() != null && getType().equalsIgnoreCase("HR")) {
				sbque.append(" and (usertype_id = 7 or usertype_id = 1)");
			} else if(getType() != null && getType().equalsIgnoreCase("Manager")) {
//				sbque.append(" and (usertype_id = 2 or usertype_id = 7 or usertype_id = 1)");
				sbque.append(" and usertype_id in (2,7,1,5,13)");
			} else if(getType() != null && getType().equalsIgnoreCase("CEO")) {
				sbque.append(" and (usertype_id = 5 or usertype_id = 1)");
			} else if(getType() != null && getType().equalsIgnoreCase("HOD")) {
//				sbque.append(" and (usertype_id = 13 or usertype_id = 7 or usertype_id = 1)");
				sbque.append(" and usertype_id in (7,1,13)");
			} 
			sbque.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbque.toString());
//			System.out.println("pst==>"+pst);
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;

	}
	
	private void getAppraisalData() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		con = db.makeConnection(con);

		try {

//			List<String> appraisalData = new ArrayList<String>();
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(getAppID()));
			rs = pst.executeQuery();
			
			while (rs.next()) {
				String empIDs = null;
				if(getType().equals("HR")){
					empIDs = rs.getString("hr_ids");
				}else if(getType().equals("Manager")){
					empIDs = rs.getString("supervisor_id");
				}else if(getType().equals("Peer")){
					empIDs = rs.getString("peer_ids");
				} else if(getType().equals("CEO")){
					empIDs = rs.getString("ceo_ids");
				} else if(getType().equals("HOD")){
					empIDs = rs.getString("hod_ids");
				}

				if (empIDs == null || empIDs.equals("")) {
					empvalue.add("0");
				} else {
					if(empIDs.length() > 1) {
						empvalue = Arrays.asList(empIDs.substring(1, empIDs.length()-1).split(","));
					} 
				}		
				

			}
			rs.close();
			pst.close();
			
//			request.setAttribute("appraisalData", appraisalData);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

//	public List<FillOrientation> getOrientationList() {
//		return orientationList;
//	}
//
//	public void setOrientationList(List<FillOrientation> orientationList) {
//		this.orientationList = orientationList;
//	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
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

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String getHideID() {
		return hideID;
	}

	public List<String> getEmpvalue() {
		return empvalue;
	}

	public void setEmpvalue(List<String> empvalue) {
		this.empvalue = empvalue;
	}

	public void setHideID(String hideID) {
		this.hideID = hideID;
	}

	public String getLblID() {
		return lblID;
	}

	public void setLblID(String lblID) {
		this.lblID = lblID;
	}

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getHideIdValue() {
		return hideIdValue;
	}

	public void setHideIdValue(String hideIdValue) {
		this.hideIdValue = hideIdValue;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	
	
}
