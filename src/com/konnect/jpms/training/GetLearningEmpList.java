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
import com.konnect.jpms.select.FillEmployee;
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

public class GetLearningEmpList extends ActionSupport implements
		ServletRequestAware,IStatements {

	private static final long serialVersionUID = 1L;

	
	private String lPlanID;
	private boolean boolPublished;
	
	 List<String> strOrgList = new ArrayList<String>();;

	 private List<FillDesig> desigList;
	 private List<FillGrade> gradeList;
	 private List<FillEmployee> empList;
	
	 private List<FillOrganisation> organisationList;
	 private String f_org;
	
	 private String[] f_strWLocation; 
	 private String[] f_department;
	 private String[] f_level; 
	 private String[] f_service;
	 private String[] f_desig;
	 private String[] f_grade;
	
	 private List<FillWLocation> wLocationList;
	 private List<FillDepartment> departmentList;
	 private List<FillLevel> levelList;
	 private List<FillServices> serviceList;
	
	
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	private String alignWith;
	private String attribIds;
	private String skillIds;
	
	CommonFunctions CF = null;
	
	private String strGapEmpId;
	private String strLocation;
	private String strDepartment;
	private String strLevel;
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
			
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		
		desigList = new FillDesig(request).fillDesig(uF.parseToInt(getF_org()));
		gradeList = new FillGrade(request).fillGradeByOrg(uF.parseToInt(getF_org()));
//		empList=new FillEmployee(request).fillTrainingEmployee(empLocation,null,strlearnerLevel,strstrlearnerDesignation,strlearnerGrade,f_organization);
		
		if(getAlignWith() == null || uF.parseToInt(getAlignWith()) == 0  ){
			setAlignWith("3");
		}
		
		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		
//		System.out.println("f_org==>"+f_org+"==>f_strWLocation==>"+f_strWLocation+"==>f_department==>"+f_department+"==>f_level==>"+f_level);

		if (uF.parseToInt(getAlignWith()) == 1) {
			String strCurrDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String strPrevDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 30), DBDATE, DATE_FORMAT);
			empList=new FillEmployee(request).fillTrainingInductionEmployee(f_org, f_strWLocation, f_department, f_service, f_level, f_desig, f_grade,strCurrDate,strPrevDate);
		} else if (uF.parseToInt(getAlignWith()) == 2) {
			empList = new FillEmployee(request).fillTrainingEmployee(getSkillIds(), getAttribIds(), getAlignWith(), f_org, f_strWLocation, f_department, f_service, f_level, f_desig, f_grade);
		} else if (uF.parseToInt(getAlignWith()) == 3) {
			empList=new FillEmployee(request).fillTrainingEmployee(f_org, f_strWLocation, f_department, f_service, f_level, f_desig, f_grade);
		} else if (uF.parseToInt(getAlignWith()) == 4) {
			empList=new FillEmployee(request).fillTrainingProbationEmployee(f_org, f_strWLocation, f_department, f_service, f_level, f_desig, f_grade);
		}
		
		getInfo();
		getSelectEmployeeList();
		return SUCCESS;
	}

	private void getInfo() {
		Connection con = null;
		Database db =new Database();
		db.setRequest(request);
		try{
			con =db.makeConnection(con);
			Map<String,String> hmEmpLocation=CF.getEmpWlocationMap(con);
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null,null); 
			Map<String, String> hmEmpCodeDesig =CF.getEmpDesigMap(con);
			request.setAttribute("hmEmpLocation", hmEmpLocation);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
	}

private void getSelectEmployeeList() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		UtilityFunctions uF=new UtilityFunctions();
		try {
			
			con=db.makeConnection(con);

			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			String selectLearnerIDs=null;
			pst = con.prepareStatement("select * from learning_plan_details where learning_plan_id = ?");
			pst.setInt(1, uF.parseToInt(getlPlanID()));
			
			rst = pst.executeQuery();
			while (rst.next()) {
				selectLearnerIDs=rst.getString("learner_ids");
			}
			rst.close();
			pst.close();
			
			if(uF.parseToInt(getStrGapEmpId()) > 0){
				if(selectLearnerIDs!=null && !selectLearnerIDs.equals("")){
					selectLearnerIDs +=getStrGapEmpId()+",";
				} else {
					selectLearnerIDs =","+getStrGapEmpId()+",";
				}
			}
			
			
			List<List<String>> selectEmpList=new ArrayList<List<String>>();
			Map<String,String> hmCheckEmpList=new HashMap<String, String>();
//			String empids="";
			if(selectLearnerIDs!=null && !selectLearnerIDs.equals("")){
				List<String> tmpselectEmpList=Arrays.asList(selectLearnerIDs.split(","));				
				if(tmpselectEmpList != null && !tmpselectEmpList.isEmpty()){
					for(String empId:tmpselectEmpList){
						List<String> innerList = new ArrayList<String>();
						if(empId.equals("0") || empId.equals("")){
							continue;
						}
						innerList.add(empId);
						innerList.add(hmEmpName.get(empId));
						selectEmpList.add(innerList);
						hmCheckEmpList.put(empId.trim(), empId.trim());
					}
				}
			}else{
				selectEmpList=null;
			}
//			System.out.println("selectEmpList ===> " +selectEmpList);
//			System.out.println("hmCheckEmpList ===> " +hmCheckEmpList);
//			System.out.println("selectLearnerIDs ===> " +selectLearnerIDs);
			
			request.setAttribute("selectEmpList", selectEmpList);
			request.setAttribute("hmCheckEmpList", hmCheckEmpList);
			request.setAttribute("selectLearnerIDs", selectLearnerIDs);
			
			
			List<String> alAttendEmp = new ArrayList<String>();
			pst = con.prepareStatement("select * from training_attend_details where learning_plan_id = ?");
			pst.setInt(1, uF.parseToInt(getlPlanID()));
			rst = pst.executeQuery();
			while (rst.next()) {
				if(!alAttendEmp.contains(rst.getString("emp_id"))){
					alAttendEmp.add(rst.getString("emp_id"));
				}
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select * from course_read_details where learning_plan_id = ?");
			pst.setInt(1, uF.parseToInt(getlPlanID()));
			rst = pst.executeQuery();
			while (rst.next()) {
				if(!alAttendEmp.contains(rst.getString("emp_id"))){
					alAttendEmp.add(rst.getString("emp_id"));
				}
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select * from assessment_question_answer where learning_plan_id = ?");
			pst.setInt(1, uF.parseToInt(getlPlanID()));
			rst = pst.executeQuery();
			while (rst.next()) {
				if(!alAttendEmp.contains(rst.getString("emp_id"))){
					alAttendEmp.add(rst.getString("emp_id"));
				}
			}
			rst.close();
			pst.close();
			request.setAttribute("alAttendEmp", alAttendEmp);
//			System.out.println("alAttendEmp==>"+alAttendEmp);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<String> getStrOrgList() {
		return strOrgList;
	}

	public void setStrOrgList(List<String> strOrgList) {
		this.strOrgList = strOrgList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
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

	public String[] getF_desig() {
		return f_desig;
	}

	public void setF_desig(String[] f_desig) {
		this.f_desig = f_desig;
	}

	public String[] getF_grade() {
		return f_grade;
	}

	public void setF_grade(String[] f_grade) {
		this.f_grade = f_grade;
	}

	public String getlPlanID() {
		return lPlanID;
	}

	public void setlPlanID(String lPlanID) {
		this.lPlanID = lPlanID;
	}

	public boolean isBoolPublished() {
		return boolPublished;
	}

	public void setBoolPublished(boolean boolPublished) {
		this.boolPublished = boolPublished;
	}

	public String getAlignWith() {
		return alignWith;
	}

	public void setAlignWith(String alignWith) {
		this.alignWith = alignWith;
	}

	public String getAttribIds() {
		return attribIds;
	}

	public void setAttribIds(String attribIds) {
		this.attribIds = attribIds;
	}

	public String getSkillIds() {
		return skillIds;
	}

	public void setSkillIds(String skillIds) {
		this.skillIds = skillIds;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrGapEmpId() {
		return strGapEmpId;
	}

	public void setStrGapEmpId(String strGapEmpId) {
		this.strGapEmpId = strGapEmpId;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	
}
