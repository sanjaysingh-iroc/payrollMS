package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Goals extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -8347978133635278063L;
	HttpSession session;
	String strSessionEmpId;
	String strEmpOrgId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF;

	private List<FillOrganisation> organisationList;
	private List<FillWLocation> wLocationList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillDesig> desigList;
	
	private String f_org;
	private String f_Location;
	private String f_department;
	private String f_level;
	private String f_desig;
	private String strOrg;
	
	private String dataType;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		
		request.setAttribute(PAGE, "/jsp/performance/Goals.jsp");
		request.setAttribute(TITLE, TGoals);
		
		if(getF_Location() == null){
			 setF_Location((String)session.getAttribute(WLOCATIONID)); 
		 }
		
		if(f_org == null){
			setStrOrg(strEmpOrgId);
		}else{
			setStrOrg(f_org);
		}
		
		if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")){
			setDataType("L");
		}
		
		UtilityFunctions uF = new UtilityFunctions();		
		List<String> empList=getEmployeeList(uF);		
		
		getEmpGoalDetails(uF,empList);
//		getEmpGoalRating();
		
		return loadGoalData(uF);
	}
	

	
	public Map<String, String> getEmpGoalRating(Connection con, UtilityFunctions uF, String empID) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmGoalAverage = new HashMap<String, String>();
		try { 
			pst=con.prepareStatement("select question_bank_id,aaa.goal_kra_target_id,goal_title,average from (select question_bank_id,question_text," +
					"goal_kra_target_id,(marks*100/weightage) as average from (select question_bank_id,question_text,goal_kra_target_id from " +
					"question_bank where goal_kra_target_id in(select goal_id from goal_details where is_measure_kra = false and emp_ids like " +
					"'%,"+empID+",%' and (goal_type = ? or goal_type = ?))) as a, appraisal_question_answer aqa where " +
					"a.question_bank_id=aqa.question_id and weightage>0) as aaa, goal_details gd where aaa.goal_kra_target_id = gd.goal_id");
			pst.setInt(1, INDIVIDUAL_GOAL); 
			pst.setInt(2, PERSONAL_GOAL); 
			rs=pst.executeQuery();
			
			while (rs.next()) {
				hmGoalAverage.put(rs.getString("goal_kra_target_id"), rs.getString("average"));
			}
			rs.close();
			pst.close();
			
//			request.setAttribute("hmGoalAverage", hmGoalAverage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmGoalAverage;
	}
	
	private void getEmpGoalDetails(UtilityFunctions uF,List<String> empList) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
			request.setAttribute("hmEmpCodeName", hmEmpCodeName);
			Map<String, List<List<String>>> hmTeamGoal= new HashMap<String, List<List<String>>>();
			Map<String, List<List<String>>> hmPersonalGoal= new HashMap<String, List<List<String>>>();
			Map<String, Map<String, String>> hmEmpGoalRating = new HashMap<String, Map<String,String>>();
			
			for(int i=0;empList!=null && !empList.isEmpty() && i<empList.size();i++) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from goal_details gd where gd.emp_ids like '%,"+empList.get(i)+ ",%' and " +
						" gd.goal_type=? and is_measure_kra = false ");
				if(getDataType() != null && getDataType().equals("L")) {
					sbQuery.append(" and is_close = false ");
				} else if(getDataType() != null && getDataType().equals("C")) {
					sbQuery.append(" and is_close = true ");
				}
				sbQuery.append(" order by gd.goal_id");
				pst=con.prepareStatement(sbQuery.toString());
				pst.setInt(1, INDIVIDUAL_GOAL);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<List<String>> outerList=hmTeamGoal.get(empList.get(i));
					if(outerList==null) outerList=new ArrayList<List<String>>();
					
					List<String> innerList=new ArrayList<String>();
					innerList.add(empList.get(i));
					innerList.add(rs.getString("goal_id"));
					innerList.add(rs.getString("goal_objective"));
					innerList.add(rs.getString("goal_title"));
					
					innerList.add(rs.getString("measure_type"));
					if(uF.parseToInt(strSessionEmpId)>0 && uF.parseToInt(strSessionEmpId)==uF.parseToInt(rs.getString("user_id"))) {
						innerList.add("Self");
					} else {
						innerList.add(hmEmpCodeName.get(rs.getString("user_id")));
					}
					innerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat()));
					String tGoalId = getPerentGoalId(con, uF, rs.getString("goal_id"));
					innerList.add(tGoalId); // team goal id 7
					String mGoalId = getPerentGoalId(con, uF, tGoalId);
					innerList.add(mGoalId); // manager goal id 8
					String cGoalId = getPerentGoalId(con, uF, mGoalId);
					innerList.add(cGoalId); // corporate goal id 9
					
					outerList.add(innerList);
					hmTeamGoal.put(empList.get(i), outerList);
				}
				rs.close();
				pst.close();
				
				StringBuilder sbQuery1 = new StringBuilder();
				sbQuery1.append("select * from goal_details where goal_type = " + PERSONAL_GOAL + " and is_measure_kra = false and emp_ids like '%," + empList.get(i) + ",%' ");
				if(getDataType() != null && getDataType().equals("L")) {
					sbQuery1.append(" and is_close = false ");
				} else if(getDataType() != null && getDataType().equals("C")) {
					sbQuery1.append(" and is_close = true ");
				}
				
				pst=con.prepareStatement(sbQuery1.toString());
				rs = pst.executeQuery();
				while (rs.next()) {
					List<List<String>> outerList=hmPersonalGoal.get(empList.get(i));
					if(outerList==null) outerList=new ArrayList<List<String>>();
					
					List<String> innerList=new ArrayList<String>();
					innerList.add(empList.get(i));
					innerList.add(rs.getString("goal_id"));
					innerList.add(rs.getString("goal_objective"));
					innerList.add(rs.getString("goal_title"));
					
					innerList.add(rs.getString("measure_type"));
					if(uF.parseToInt(strSessionEmpId) > 0 && uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("user_id"))) {
						innerList.add("Self");
					} else {
						innerList.add(hmEmpCodeName.get(rs.getString("user_id")));
					}
					innerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat()));
					
					outerList.add(innerList);
					hmPersonalGoal.put(empList.get(i), outerList);
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmGoalRating = getEmpGoalRating(con, uF, empList.get(i));
				hmEmpGoalRating.put(empList.get(i), hmGoalRating);
			}
			
			pst=con.prepareStatement("select emp_image,emp_per_id from employee_personal_details ");
			rs=pst.executeQuery();
			Map<String,String> empImageMap=new HashMap<String,String>();
			while(rs.next()){
				empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("empImageMap", empImageMap);
			request.setAttribute("hmTeamGoal", hmTeamGoal);
			request.setAttribute("hmPersonalGoal", hmPersonalGoal);
			request.setAttribute("hmEmpGoalRating", hmEmpGoalRating);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private String getPerentGoalId(Connection con, UtilityFunctions uF, String goalID) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String goalId = null;
		try {
			String query1 = "select goal_parent_id from goal_details where goal_id = ?";
			pst = con.prepareStatement(query1);
			pst.setInt(1, uF.parseToInt(goalID));
			rs = pst.executeQuery();
			while (rs.next()) {
				goalId = rs.getString("goal_parent_id");
			}
			rs.close();
			pst.close();
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return goalId;
	}



	private List<String> getEmployeeList(UtilityFunctions uF) {
		List<String> al = new ArrayList<String>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			//if(uF.parseToInt(getF_org())==0 && uF.parseToInt(getF_Location())==0 && uF.parseToInt(getF_department())==0 && uF.parseToInt(getF_level())==0 && uF.parseToInt(getF_desig())==0 ){
			if(strSessionUserType!=null && strSessionUserType.equals(EMPLOYEE)){
				al.add(strSessionEmpId);
			}else{
				
				/*String query1 = "select depart_id from employee_official_details where emp_id=?";
				pst = con.prepareStatement(query1);
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				rs = pst.executeQuery();
				int depart_id = 0;
				while (rs.next()) {
					depart_id = rs.getInt("depart_id");
				}*/
				
				StringBuilder sbQuery=new StringBuilder();
				sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and is_alive = true ");
				
				if(uF.parseToInt(getF_department())<=0 && strSessionUserType!=null && strSessionUserType.equals(MANAGER)) {
						sbQuery.append(" and eod.supervisor_emp_id = " + strSessionEmpId);
				} else {
				
					if(uF.parseToInt(getF_org())>0) {
						sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
					}
					if(uF.parseToInt(getF_Location())>0) {
						sbQuery.append(" and eod.wlocation_id="+uF.parseToInt(getF_Location()));
					}
					/*else if(uF.parseToInt(getF_Location())<=0 && strSessionUserType!=null && (strSessionUserType.equals(HRMANAGER) || strSessionUserType.equals(MANAGER))){
						String wlocation=(String) session.getAttribute(WLOCATIONID);
						sbQuery.append(" and eod.wlocation_id="+uF.parseToInt(wlocation));
					}*/
					if(uF.parseToInt(getF_department())>0) {
						sbQuery.append(" and eod.depart_id="+uF.parseToInt(getF_department()));
					}
					if(uF.parseToInt(getF_level())>0) {
						sbQuery.append(" and eod.grade_id in (SELECT grade_id FROM grades_details where designation_id in " +
								" (SELECT designation_id FROM designation_details  WHERE level_id="+uF.parseToInt(getF_level())+")) ");
					}
					if(uF.parseToInt(getF_desig())>0) {
						sbQuery.append(" and eod.grade_id in (SELECT grade_id FROM grades_details where designation_id in " +
								" (SELECT designation_id FROM designation_details  WHERE designation_id="+uF.parseToInt(getF_desig())+"))  ");
					}	
				}
				sbQuery.append(" order by epd.emp_fname");			
				
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
//				System.out.println("pst =====> " + pst);
				al.add(strSessionEmpId);
				while (rs.next()) {
					if(!al.contains(rs.getString("emp_per_id")))
						al.add(rs.getString("emp_per_id"));
				}
				rs.close();
				pst.close();
			}
			
			request.setAttribute("empList", al);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
		return al;
	}


	private String loadGoalData(UtilityFunctions uF) {
		
		organisationList = new FillOrganisation(request).fillOrganisation();
		wLocationList = new FillWLocation(request).fillWLocation(getStrOrg());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getStrOrg()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
		desigList = new FillDesig(request).fillDesig(uF.parseToInt(getStrOrg()));
		
		return SUCCESS;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public String getF_level() {
		return f_level;
	}

	public void setF_level(String f_level) {
		this.f_level = f_level;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
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

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getF_Location() {
		return f_Location;
	}

	public void setF_Location(String f_Location) {
		this.f_Location = f_Location;
	}

	public String getF_department() {
		return f_department;
	}

	public void setF_department(String f_department) {
		this.f_department = f_department;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public String getF_desig() {
		return f_desig;
	}

	public void setF_desig(String f_desig) {
		this.f_desig = f_desig;
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

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}
