package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddRequestPopUp extends ActionSupport implements
		ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;
	String manlocation;
	String recruitmentID = null;
	String level_id;
	String desig_id;
	String grade_id;
	String service_id;
	String[] skills;
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

	
		desigList = new FillDesig(request).fillDesig();
		

		gradeList = new FillGrade(request).fillGrade();
	

		workLocationList = new FillWLocation(request).fillWLocation();

		skillslist = new FillSkills(request).fillSkills();
		serviceslist = new FillServices(request).fillServices();
		levelslist = new FillLevel(request).fillLevel();

		manlocation = getManagerLocation(strSessionEmpId);

		recruitmentID = request.getParameter("RID");
	

		if (recruitmentID != null && !recruitmentID.equals("")) {
			getRecruitmentRequest(recruitmentID);
			request.setAttribute("updateRequest", "update");
			return "edit";
		}
		
		return LOAD;

	}

	private void getRecruitmentRequest(String recruitmentID2) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		List<String> requestList = null;
		con = db.makeConnection(con);
		try {
			String query = "select r.recruitment_id,r.designation_id,r.grade_id,r.wlocation,r.no_position,r.comments,"
					+ "r.skills,r.added_by,r.services,r.level_id,r.effective_date from recruitment_details r where r.recruitment_id=?";
			pst = con.prepareStatement(query);
			pst.setInt(1, uF.parseToInt(recruitmentID2));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				requestList = new ArrayList<String>();
				requestList.add(rst.getString(1));
				requestList.add(rst.getString(2));
				desig_id = rst.getString(2);
				requestList.add(rst.getString(3));
				grade_id = rst.getString(3);
				requestList.add(rst.getString(4));
				manlocation = rst.getString(4);
				requestList.add(rst.getString(5));
				requestList.add(rst.getString(6));
				requestList.add(rst.getString(7));
				String temp=rst.getString(7);
				skills = temp.split(", ");
				requestList.add(rst.getString(8));
				requestList.add(rst.getString(9));
				service_id = rst.getString(9);
				requestList.add(rst.getString(10));
				level_id = rst.getString(10);
				requestList.add(uF.getDateFormat(rst.getString(11), DBDATE,
						DATE_FORMAT));

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
	
		request.setAttribute("requestList1", requestList);

	}

	private String getManagerLocation(String strSessionEmpId2) {
		// select w.wlocation_name from employee_official_details
		// e,work_location_info w where e.wlocation_id=w.wlocation_id and
		// e.emp_id=722
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
			pst.setInt(1, uF.parseToInt(strSessionEmpId2));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				location = rst.getString(1);
			}
			rst.close();
    		pst.close();
    		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	
		return location;
	}

	List<FillDesig> desigList;
	List<FillGrade> gradeList;
	List<FillWLocation> workLocationList;

	List<FillServices> serviceslist;
	List<FillSkills> skillslist;

	List<FillLevel> levelslist;

	
	public String[] getSkills() {
		return skills;
	}

	public void setSkills(String[] skills) {
		this.skills = skills;
	}

	public String getDesig_id() {
		return desig_id;
	}

	public void setDesig_id(String desig_id) {
		this.desig_id = desig_id;
	}

	public String getGrade_id() {
		return grade_id;
	}

	public void setGrade_id(String grade_id) {
		this.grade_id = grade_id;
	}

	public String getService_id() {
		return service_id;
	}

	public void setService_id(String service_id) {
		this.service_id = service_id;
	}

	public String getLevel_id() {
		return level_id;
	}

	public void setLevel_id(String level_id) {
		this.level_id = level_id;
	}

	public String getRecruitmentID() {
		return recruitmentID;
	}

	public void setRecruitmentID(String recruitmentID) {
		this.recruitmentID = recruitmentID;
	}

	public List<FillLevel> getLevelslist() {
		return levelslist;
	}

	public void setLevelslist(List<FillLevel> levelslist) {
		this.levelslist = levelslist;
	}

	public String getManlocation() {
		return manlocation;
	}

	public void setManlocation(String manlocation) {
		this.manlocation = manlocation;
	}

	public List<FillServices> getServiceslist() {
		return serviceslist;
	}

	public void setServiceslist(List<FillServices> serviceslist) {
		this.serviceslist = serviceslist;
	}

	public List<FillSkills> getSkillslist() {
		return skillslist;
	}

	public void setSkillslist(List<FillSkills> skillslist) {
		this.skillslist = skillslist;
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

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
}
