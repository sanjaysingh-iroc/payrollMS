package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillRound;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddCriteriaPanel extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	String organisation;
	String strUserType = null;
	String strSessionEmpId = null;
	private String recruitId;
	private String formName;
	private List<FillRound> roundList;

	private  String jobid;
	private List<FillLevel> levelList;
	private List<FillGrade> gradeList;
	private List<FillDesig> desigList;
	private List<FillEmployee> empList;
	private List<FillWLocation> workList;

	private String type;

	public String execute() throws Exception {

		request.setAttribute(PAGE, "/jsp/recruitment/AddCriteriaPanel.jsp");
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		if (getOrganisation() == null) {
			setOrganisation((String) session.getAttribute(ORGID));
		}

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		request.setAttribute(TITLE, "Round & Panel Information");
		UtilityFunctions uF = new UtilityFunctions();
		
		organisationList = new FillOrganisation(request).fillOrganisation();
		workList = new FillWLocation(request).fillWLocation(getOrganisation());
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getOrganisation()));
		desigList = new FillDesig(request).fillDesig(uF.parseToInt(getOrganisation()));
		gradeList = new FillGrade(request).fillGradeByOrg(uF.parseToInt(getOrganisation()));
		
		roundList = new FillRound(request).fillRound(getRecruitId());

	//===start parvez date: 19-10-2021===
//		empList = new FillEmployee(request).fillEmployeeNameByOrg(uF.parseToInt(getOrganisation()));
		empList = new FillEmployee(request).fillEmployeeNameForRecruitment(uF.parseToInt(getOrganisation()));
	//===end parvez date: 19-10-2021===
		request.setAttribute("empList", empList);

		getSelectEmployeeList(uF);
		addRoundOne(uF);
		getRoundIds(uF);
		getEmpIdsRoundwise(uF);
		getRoundIds();
		return "popup";

	}

	private void getRoundIds() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			StringBuilder sb = new StringBuilder("");
			con = db.makeConnection(con);
			pst = con.prepareStatement("select distinct(round_id),recruitment_id from panel_interview_details where recruitment_id = ? order by round_id");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			// System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			// System.out.println("new Date ===> "+ new Date());
			sb.append("<option value=\"\">" + "Select" + "</option>");
			while (rs.next()) {
				/*
				 * String strsb =
				 * hmRoundIds.get(rs.getString("recruitment_id")); if(strsb ==
				 * null || strsb.equals("")) sb = new StringBuilder();
				 */
				sb.append("<option value=\"" + rs.getString("round_id") + "\">" + "Round " + rs.getString("round_id") + "</option>");
			}
			rs.close();
			pst.close();

			// System.out.println("sb.toString() ===> "+sb.toString());
			request.setAttribute("option", sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getEmpIdsRoundwise(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;

		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpProfile = CF.getEmpProfileImage(con);

			Map<String, String> hmEmpCode = new HashMap<String, String>();
			pst = con.prepareStatement("select emp_per_id,empcode from employee_personal_details");
			rst = pst.executeQuery();
			// System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				hmEmpCode.put(rst.getString("emp_per_id"), rst.getString("empcode"));
			}
			rst.close();
			pst.close();

			Map<String, List<List<String>>> hmEmpIdsRoundwise = new LinkedHashMap<String, List<List<String>>>();
			pst = con
					.prepareStatement("select distinct(panel_emp_id), round_id, recruitment_id from panel_interview_details where recruitment_id=? and panel_emp_id is not null order by panel_emp_id,round_id");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			// System.out.println("pst======>"+pst);
			rst = pst.executeQuery();
			// System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				List<List<String>> listEmpIds = hmEmpIdsRoundwise.get(rst.getString("round_id"));
				if (listEmpIds == null)
					listEmpIds = new ArrayList<List<String>>();

				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("panel_emp_id"));
				if (rst.getString("panel_emp_id") != null) {
					innerList.add("<img style=\"margin-right:5px\" class=\"lazy img-circle\" width=\"18\" src=\"userImages/avatar_photo.png\" data-original=\"userImages/"
							+ uF.showData(hmEmpProfile.get(rst.getString("panel_emp_id").trim()), "avatar_photo.png") + "\">"
							+ hmEmpName.get(rst.getString("panel_emp_id").trim()) + " [" + hmEmpCode.get(rst.getString("panel_emp_id").trim()) + "]");
				} else {
					innerList.add("");
				}
				listEmpIds.add(innerList);
				hmEmpIdsRoundwise.put(rst.getString("round_id"), listEmpIds);
			}
			rst.close();
			pst.close();

			request.setAttribute("hmEmpIdsRoundwise", hmEmpIdsRoundwise);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getRoundIds(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		List<String> listRoundId = new ArrayList<String>();
		Map<String, String> hmRoundName = new HashMap<String, String>();

		try {
			con = db.makeConnection(con);
			Map<String, String> hmAssessmentName = CF.getAssessmentNameMap(con, uF);
			Map<String, String> hmRoundAssessment = new HashMap<String, String>();
			pst = con.prepareStatement("select distinct(round_id), recruitment_id, assessment_id from panel_interview_details where recruitment_id=? and panel_emp_id is null order by round_id");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
//			 System.out.println("pst======>"+pst);
			rst = pst.executeQuery();
			// System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				listRoundId.add(rst.getString("round_id"));
				hmRoundAssessment.put(rst.getString("round_id")+"_ASSESSID", rst.getString("assessment_id"));
				hmRoundAssessment.put(rst.getString("round_id")+"_ASSESSNAME", hmAssessmentName.get(rst.getString("assessment_id")));
			}
			rst.close();
			pst.close();

//			System.out.println("hmRoundAssessment ===>> " + hmRoundAssessment);
			
			pst = con.prepareStatement("select distinct(round_id), round_name, recruitment_id from panel_interview_details where recruitment_id=? and round_name is not null order by round_id");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			// System.out.println("pst======>"+pst);
			rst = pst.executeQuery();
			// System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				hmRoundName.put(rst.getString("round_id"), rst.getString("round_name"));
			}
			rst.close();
			pst.close();
			request.setAttribute("listRoundId", listRoundId);
			request.setAttribute("hmRoundName", hmRoundName);
			request.setAttribute("hmRoundAssessment", hmRoundAssessment);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void addRoundOne(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		try {
			boolean flag = false;
			con = db.makeConnection(con);
			pst = con.prepareStatement("select round_id from panel_interview_details where recruitment_id=? and round_id = 1");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rst = pst.executeQuery();
			// System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				flag = true;
			}
			rst.close();
			pst.close();
			if (flag == false) {
				pst = con.prepareStatement("insert into panel_interview_details(recruitment_id,round_id,added_by,added_date)values(?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getRecruitId()));
				pst.setInt(2, 1);
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
				pst.executeUpdate();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getSelectEmployeeList(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;

		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpProfile = CF.getEmpProfileImage(con);

			pst = con.prepareStatement("select panel_employee_id,effective_date from recruitment_details where recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			// System.out.println("pst======>"+pst);
			rst = pst.executeQuery();
			// System.out.println("new Date ===> "+ new Date());
			String selectEmpIDs = null;
			String effectDate = null;
			while (rst.next()) {
				selectEmpIDs = rst.getString("panel_employee_id");
				effectDate = rst.getString("effective_date");
			}
			rst.close();
			pst.close();

			Map<String, String> sltEmpDtCmprHm = new HashMap<String, String>();
			if(effectDate!=null && effectDate.length() > 0){
				boolean comparedate = uF.getCurrentDate(CF.getStrTimeZone()).before(uF.getDateFormatUtil(effectDate, DBDATE));
				if (selectEmpIDs != null && !selectEmpIDs.equals("")) {
					String tmpsltempids = selectEmpIDs.substring(1, selectEmpIDs.length() - 1);
					List<String> selectedEmpIdsLst = Arrays.asList(tmpsltempids.split(","));
	
					for (int i = 0; selectedEmpIdsLst != null && i < selectedEmpIdsLst.size(); i++) {
						sltEmpDtCmprHm.put(selectedEmpIdsLst.get(i).trim(), "" + comparedate);
					}
				}
			}
			// System.out.println("sltEmpDtCmprHm ========== "+sltEmpDtCmprHm);

			Map<String, String> hmEmpCode = new HashMap<String, String>();
			pst = con.prepareStatement("select emp_per_id,empcode from employee_personal_details");
			rst = pst.executeQuery();
			// System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				hmEmpCode.put(rst.getString("emp_per_id"), rst.getString("empcode"));
			}
			rst.close();
			pst.close();

			List<String> selectEmpIds = new ArrayList<String>();
			List<String> selectEmpNameList = new ArrayList<String>();
			if (selectEmpIDs != null && !selectEmpIDs.equals("")) {
				Set<String> empSet = new HashSet<String>(Arrays.asList(selectEmpIDs.split(",")));
				Iterator<String> itr = empSet.iterator();
				while (itr.hasNext()) {
					String empId = (String) itr.next();
					if (empId != null && !empId.equals("")) {
						// <img
						// class=\"lazy\" src=\"userImages/avatar_photo.png\"  data-original=\"userImages/"+uF.showData(empImageMap.get(empList.get(i).trim()),
						// "avatar_photo.png")+"\" border=\"0\" height=\"16px\" width=\"16px\" title=\""+hmEmpName.get(empList.get(i).trim())+"\"/>
						selectEmpNameList.add("<img style=\"margin-right:5px\" class=\"lazy img-circle\" width=\"18\" src=\"userImages/avatar_photo.png\" data-original=\"userImages/"+ uF.showData(hmEmpProfile.get(empId.trim()), "avatar_photo.png")+ "\">"+ hmEmpName.get(empId.trim())+ " ["+ hmEmpCode.get(empId.trim()) + "]");
						selectEmpIds.add(empId.trim());
					}
				}
			} else {
				selectEmpNameList = null;
			}

			Map<String, String> hmWlocation = new HashMap<String, String>();
			pst = con.prepareStatement("select emp_id,wlocation_name from employee_official_details join work_location_info using (wlocation_id) where emp_id > 0");
			rst = pst.executeQuery();
			// System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				hmWlocation.put(rst.getString("emp_id"), rst.getString("wlocation_name"));
			}
			rst.close();
			pst.close();

			request.setAttribute("selectEmpIds", selectEmpIds);
			request.setAttribute("sltEmpDtCmprHm", sltEmpDtCmprHm);

			request.setAttribute("hmWlocation", hmWlocation);

			request.setAttribute("selectEmpNameList", selectEmpNameList);
	

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	// Variablessssssss=================================

	public String getJobid() {
		return jobid;
	}

	public void setJobid(String jobid) {
		this.jobid = jobid;
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

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}
	

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public List<FillWLocation> getWorkList() {
		return workList;
	}

	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
	}

	public List<FillRound> getRoundList() {
		return roundList;
	}

	public void setRoundList(List<FillRound> roundList) {
		this.roundList = roundList;
	}

	List<FillOrganisation> organisationList;

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}