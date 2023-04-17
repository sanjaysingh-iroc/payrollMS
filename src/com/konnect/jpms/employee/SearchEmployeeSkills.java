package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.views.xslt.ArrayAdapter;

import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;


public class SearchEmployeeSkills extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId; 
	CommonFunctions CF = null;
	String strUserType = null;
	
	private List <FillSkills> skillList;
	private String from;
	private String skills;
	private String[] strSkills;
	private static Logger log = Logger.getLogger(SearchEmployeeSkills.class);
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute("arrEnabledModules", CF.getArrEnabledModules());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		request.setAttribute(PAGE, PSearchEmployeeSkills);
		request.setAttribute(TITLE, TSearchEmployee+" Skills");
		
//		System.out.println("getFromPage 1==>"+getFrom());
//		System.out.println("getFromPage 2==>"+(String)request.getParameter("from"));
		if(getFrom() == null || getFrom().equals("") || getFrom().equalsIgnoreCase("null")) {
			boolean isView  = CF.getAccess(session, request, uF);
			if(!isView){
				request.setAttribute(PAGE, PAccessDenied);
				request.setAttribute(TITLE, TAccessDenied);
				return ACCESS_DENIED;
			}
		}
		skillList = new FillSkills(request).fillSkillsWithId();
		
		if(strUserType.equals(MANAGER)) {
			searchTeamEmployee(uF);
		} else {
			if(getStrSkills()!=null && getStrSkills().length>0){
				searchEmployee(uF);
			}
		}
		
		employeeSkillRatingOnAssessments(uF);
//		System.out.println("getFromPage 3==>"+getFrom());
		if(getFrom() != null && getFrom().equals("TS")) {
			return VIEW;
		}
		return SUCCESS;

	}
	
   public void employeeSkillRatingOnAssessments(UtilityFunctions uF){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from(select sum(marks) as marks ,sum(weightage) as weightage," +
					"user_type_id,emp_id,learning_plan_id from assessment_question_answer " + //,assessment_details_id
					"group by user_type_id,emp_id,learning_plan_id)as a"); //,assessment_details_id

			rs = pst.executeQuery();

			Map<String, Map<String, String>> hmEmpLPlanRating = new HashMap<String, Map<String, String>>();
			Map<String, String> hmLearningPlanRating = new HashMap<String, String>();
			
			while (rs.next()) {
				hmLearningPlanRating = hmEmpLPlanRating.get(rs.getString("emp_id"));
				
				if(hmLearningPlanRating == null) hmLearningPlanRating = new HashMap<String, String>();
				double dblAverage = 0;
				if(uF.parseToDouble(rs.getString("weightage")) > 0) {
					dblAverage = (uF.parseToDouble(rs.getString("marks")) * 100) / uF.parseToDouble(rs.getString("weightage"));
				}
				hmLearningPlanRating.put(rs.getString("learning_plan_id"), uF.formatIntoTwoDecimal(dblAverage) );
				hmEmpLPlanRating.put(rs.getString("emp_id"), hmLearningPlanRating);
			}
			rs.close();
			pst.close();
			
			Map<String,Map<String, String>> empSkillAvgRating = new HashMap<String, Map<String,String>>();
			Set<String> keys=hmEmpLPlanRating.keySet();
			Iterator<String> it = keys.iterator();
			while(it.hasNext()){
				String empId = (String)it.next();
				Map<String, String> hmLearningPlanRate = hmEmpLPlanRating.get(empId);
				List<String> empSkillList = getEmpSkillsList(con, uF, empId);
				Map<String, List<String>> hmEmpLPlanData = getEmpLearningPlanAndSkillsList(con, uF, empId);
				
				Set<String> keys1 = hmEmpLPlanData.keySet();
				Iterator<String> it1 = keys1.iterator();
//				System.out.println("empSkillList ===> " + empSkillList);
				
				Map<String, String> hmSkillRating = new HashMap<String, String>();
				Map<String, String> hmSkillCnt = new HashMap<String, String>();
				while(it1.hasNext()) {
					String lPlanId = (String)it1.next();
					List<String> innerList = hmEmpLPlanData.get(lPlanId);
					
					List<String> empLplanSkillList = null;
					if(innerList.get(1) != null && !innerList.get(1).equals("")) {
						empLplanSkillList = Arrays.asList(innerList.get(1).split(","));
					}
//					System.out.println("empId ===> " + empId + " innerList ===> " + innerList);
					for(int i=0; empSkillList != null && !empSkillList.isEmpty() && i< empSkillList.size(); i++) {
						if(empLplanSkillList != null && empLplanSkillList.contains(empSkillList.get(i))) {
							double lPlanSkillRating = uF.parseToDouble(hmSkillRating.get(empSkillList.get(i)));
							int skillCnt = uF.parseToInt(hmSkillCnt.get(empSkillList.get(i)));
							skillCnt++;
							lPlanSkillRating += uF.parseToDouble(hmLearningPlanRate.get(lPlanId));
							hmSkillRating.put(empSkillList.get(i), lPlanSkillRating+"");
							hmSkillCnt.put(empSkillList.get(i), skillCnt+"");
//							System.out.println("empSkillList.get(i) ===> " + empSkillList.get(i) + " hmSkillRating ===> " + hmSkillRating + " hmSkillCnt ===> " + hmSkillCnt);
						}
					}
				}
//				System.out.println("OUT hmSkillRating ===> " + hmSkillRating + " hmSkillCnt ===> " + hmSkillCnt);
				
				
				Map<String, String> hmSkillAvgRating = new HashMap<String, String>();
				Set<String> keyss = hmSkillRating.keySet();
				Iterator<String> itt = keyss.iterator();
				while(itt.hasNext()) {
					String skillId = (String)itt.next();
					double totSkillRating = uF.parseToDouble(hmSkillRating.get(skillId));
					int totSkillCnt = uF.parseToInt(hmSkillCnt.get(skillId));
					double avgSkillRating = 0.0d;
					if(totSkillCnt > 0) {
						avgSkillRating = totSkillRating / totSkillCnt;
					}
					hmSkillAvgRating.put(skillId, avgSkillRating+"");	
				}
				
				empSkillAvgRating.put(empId, hmSkillAvgRating);
			}
			
//			System.out.println("empSkillAvgRating ===>> " + empSkillAvgRating);
			request.setAttribute("empSkillAvgRating", empSkillAvgRating);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private Map<String, List<String>> getEmpLearningPlanAndSkillsList(Connection con, UtilityFunctions uF, String empId) {
		
		ResultSet rs = null;
		PreparedStatement pst = null;
//		List<List<String>> empLPlanAndSkillsList = new ArrayList<List<String>>();
		Map<String, List<String>> hmEmpLPlanData = new HashMap<String, List<String>>();
		try {
			pst = con.prepareStatement("select learning_plan_id,skills from learning_plan_details where learning_plan_id in(select " +
					"distinct(learning_plan_id) from learning_plan_stage_details where learning_type = 'Assessment') and " +
					"learner_ids like '%,"+ empId +",%'"); //,assessment_details_id
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("learning_plan_id"));
				innerList.add(rs.getString("skills"));
//				empLPlanAndSkillsList.add(innerList);
				hmEmpLPlanData.put(rs.getString("learning_plan_id"), innerList);
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmEmpLPlanData;
	}


	private List<String> getEmpSkillsList(Connection con, UtilityFunctions uF, String empId) {
		
		ResultSet rs = null;
		PreparedStatement pst = null;
		List<String> empSkillsList = new ArrayList<String>();
		try {
			pst = con.prepareStatement("select skill_id from skills_description where emp_id = ? "); //,assessment_details_id
			pst.setInt(1, uF.parseToInt(empId));
			rs = pst.executeQuery();
			while (rs.next()) {
				empSkillsList.add(rs.getString("skill_id"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return empSkillsList;
	}


	public void searchTeamEmployee(UtilityFunctions uF){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;

		try {
			con = db.makeConnection(con);
			Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
			Map<String, Map<String, String>> hmWLocation = CF.getWorkLocationMap(con);
//			log.debug("strUserType===>"+strUserType);
//			log.debug("strSessionEmpId===>"+strSessionEmpId);
			StringBuilder sbSkills = new StringBuilder();
//			sbSkills.append("select epd.*,sd.* from employee_personal_details epd, employee_official_details eod, skills_description sd where " +
//					"epd.emp_per_id = eod.emp_id and epd.emp_per_id = sd.emp_id and is_alive = true and supervisor_emp_id = "+strSessionEmpId+" ");
			
			sbSkills.append("select emp_per_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					"and is_alive = true and supervisor_emp_id = "+strSessionEmpId+" ");
			
			if(getStrSkills()!=null && getStrSkills().length>0) {
				String skillIds = CF.getAppendDatasWithoutStartEndComma(getStrSkills());
				setStrSkills(skillIds.split(","));
				sbSkills.append("and emp_per_id in(select distinct(emp_id) from skills_description where skill_id in ("+skillIds+"))");
//					sbSkills.append(" and skill_id in ("+skillIds+") ");
			}
			sbSkills.append(" order by emp_per_id");

			pst = con.prepareStatement(sbSkills.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			
			CF.getElementList(con, request);
			
			List<String> empIdList = new ArrayList<String>();
			Map<String, List<List<String>>> hmEmpSkills = new HashMap<String, List<List<String>>>();
			Map<String, Map<String, String>> hmEmpProfile = new HashMap<String, Map<String, String>>();
			
			Map hmEAttributeData = new HashMap();
			while(rs!=null && rs.next()) {
				
				empIdList.add(rs.getString("emp_per_id"));
				
				hmEmpSkills.put(rs.getString("emp_per_id"), CF.selectSkills(con, uF.parseToInt(rs.getString("emp_per_id"))));
				
				hmEmpProfile.put(rs.getString("emp_per_id"), CF.getEmpProfileDetail(con, request, session, CF, uF, strUserType, rs.getString("emp_per_id")));
				
				hmEAttributeData.put(rs.getString("emp_per_id"), CF.getAttributes(con, request, rs.getString("emp_per_id")));
			
			}
			rs.close();
			pst.close();
			
			
//			System.out.println("hmEmpSkills ===>> " +hmEmpSkills);
			
			request.setAttribute(TITLE, "Search Skills");
//			request.setAttribute("hmSkills", hmSkills);
			
			request.setAttribute("empIdList", empIdList);
			request.setAttribute("hmEmpSkills", hmEmpSkills);
			request.setAttribute("hmEmpProfile", hmEmpProfile);
			request.setAttribute("hmEAttributeData", hmEAttributeData);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void searchEmployee(UtilityFunctions uF){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;

		try {
			con = db.makeConnection(con);
			
			StringBuilder sbSkills = new StringBuilder();
			sbSkills.append("select emp_per_id from employee_personal_details where is_alive = true ");
			if(getStrSkills()!=null && getStrSkills().length>0) {
				String skillIds = CF.getAppendDatasWithoutStartEndComma(getStrSkills());
				setStrSkills(skillIds.split(","));
				sbSkills.append("and emp_per_id in(select distinct(emp_id) from skills_description where skill_id in ("+skillIds+"))");
//					sbSkills.append(" and skill_id in ("+skillIds+") ");
			}
			sbSkills.append(" order by emp_per_id");

			pst = con.prepareStatement(sbSkills.toString());
//			System.out.println("pst ===> " + pst);
			
			rs = pst.executeQuery();
			
			CF.getElementList(con, request);
			
			List<String> empIdList = new ArrayList<String>();
			Map<String, List<List<String>>> hmEmpSkills = new HashMap<String, List<List<String>>>();
			Map<String, Map<String, String>> hmEmpProfile = new HashMap<String, Map<String, String>>();
			
			Map hmEAttributeData = new HashMap();
			while(rs!=null && rs.next()) {
				
				empIdList.add(rs.getString("emp_per_id"));
				
				hmEmpSkills.put(rs.getString("emp_per_id"), CF.selectSkills(con, uF.parseToInt(rs.getString("emp_per_id"))));
				
				hmEmpProfile.put(rs.getString("emp_per_id"), CF.getEmpProfileDetail(con, request, session, CF, uF, strUserType, rs.getString("emp_per_id")));
				
				hmEAttributeData.put(rs.getString("emp_per_id"), CF.getAttributes(con, request, rs.getString("emp_per_id")));
				
			}
			rs.close();
			pst.close();

			
			request.setAttribute(TITLE, "Search Skills");
//			request.setAttribute("hmSkills", hmSkills);
			
			request.setAttribute("empIdList", empIdList);
			request.setAttribute("hmEmpSkills", hmEmpSkills);
			request.setAttribute("hmEmpProfile", hmEmpProfile);
			request.setAttribute("hmEAttributeData", hmEAttributeData);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String[] getStrSkills() {
		return strSkills;
	}

	public void setStrSkills(String[] strSkills) {
		this.strSkills = strSkills;
	}

	public List<FillSkills> getSkillList() {
		return skillList;
	}

	public void setSkillList(List<FillSkills> skillList) {
		this.skillList = skillList;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	
}
