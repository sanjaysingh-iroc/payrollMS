package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectServiceReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/task/ProjectServiceReport.jsp");
		request.setAttribute(TITLE, "Project Service Report");
		UtilityFunctions uF = new UtilityFunctions();
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		viewServices(uF);			 
		return SUCCESS;
 
	}
	
	   
	public String viewServices(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			Map hmWLocationMap = CF.getWorkLocationMap(con);
			Map hmLevelMap = CF.getLevelMap(con);
			
			//List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			//List<String> alLinkedLevels = new ArrayList<String>();
			
			Map hmProjectServiceMap = new LinkedHashMap();
			Map<String, List<List<String>>> hmProjectSkillMap = new HashMap<String, List<List<String>>>();
			Map<String, List<List<String>>> hProjectRateMap = new HashMap<String, List<List<String>>>();
			
			pst = con.prepareStatement("select * from services_project order by service_name");
			rs = pst.executeQuery();
			while(rs.next()) {
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("service_project_id"));
				alInner.add(rs.getString("service_name"));
				alInner.add(uF.showData(rs.getString("service_desc"), "-"));
				alInner.add(uF.showData(rs.getString("service_desc"), "-"));
				hmProjectServiceMap.put(rs.getString("service_project_id"), alInner);
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select * from service_skills_details");
			rs = pst.executeQuery();
			List<List<String>> skillList = new ArrayList<List<String>>();
			while(rs.next()) {
				
				skillList = hmProjectSkillMap.get(rs.getString("service_id"));
				if(skillList == null) skillList = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("service_skill_id"));
				innerList.add(rs.getString("skill_id"));
				innerList.add(CF.getSkillNameBySkillId(con, rs.getString("skill_id")));
				innerList.add(rs.getString("skill_description"));
				skillList.add(innerList);
				
				hmProjectSkillMap.put(rs.getString("service_id"), skillList);
			}
			rs.close();
			pst.close();

			Map<String, Map<String, String>> hmCurrData = CF.getCurrencyDetails(con);
//			pst = con.prepareStatement("select a.*,wli.wlocation_name,ld.level_name from (select * from level_skill_rates lsr where skill_name in(select skill_name from skills_details where service_project_id=?) and service_project_id=?) as a LEFT JOIN work_location_info wli ON (wli.wlocation_id=a.wlocation_id) LEFT JOIN level_details ld ON (a.level_id=ld.level_id)");
			pst = con.prepareStatement("select * from level_skill_rates order by service_project_id");
			rs = pst.executeQuery();
			List<List<String>> skillRateList = new ArrayList<List<String>>();
			while(rs.next()) {
				
				skillRateList = hProjectRateMap.get(rs.getString("service_project_id")+"_"+rs.getString("skill_id"));
				if(skillRateList == null) skillRateList = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("emp_skill_rate_id"));
				Map hmWLocation = (Map)hmWLocationMap.get(rs.getString("wlocation_id"));
				if(hmWLocation == null) hmWLocation = new HashMap();
				
				innerList.add(uF.showData((String)hmWLocation.get("WL_NAME"), ""));
				innerList.add(uF.showData((String)hmLevelMap.get(rs.getString("level_id")), ""));
				innerList.add(uF.showData(rs.getString("rate_per_day"), ""));
				innerList.add(uF.showData(rs.getString("rate_per_hour"), ""));
				innerList.add(uF.showData(rs.getString("rate_per_month"), ""));
				Map<String, String> hmCurr = hmCurrData.get(rs.getString("curr_id"));
				innerList.add(hmCurr != null ? hmCurr.get("SHORT_CURR") : "");
				
				skillRateList.add(innerList);
				
				hProjectRateMap.put(rs.getString("service_project_id")+"_"+rs.getString("skill_id"), skillRateList);
				
			}
			rs.close();
			pst.close();
			
			
			Map<String, List<List<String>>> hmServiceTasksMap = new HashMap<String, List<List<String>>>();
			pst = con.prepareStatement("select * from service_tasks_details");
			rs = pst.executeQuery();
			while(rs.next()) {
				
				List<List<String>> predefinedTaskList = hmServiceTasksMap.get(rs.getString("service_id"));
				if(predefinedTaskList == null) predefinedTaskList = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("service_task_id"));
				innerList.add(rs.getString("task_name"));
				innerList.add(rs.getString("task_description"));
				predefinedTaskList.add(innerList);
				
				hmServiceTasksMap.put(rs.getString("service_id"), predefinedTaskList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hProjectRateMap ===>>> " + hProjectRateMap);
			
			
			
//			pst = con.prepareStatement("select count(*) as count, grade_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and epd.is_alive=true and eod.emp_id >0 group by grade_id");
//			rs = pst.executeQuery();
//
//			Map hmEmpGradeMap = new HashMap();
//			while(rs.next()){
//				hmEmpGradeMap.put(rs.getString("grade_id"), rs.getString("count"));
//			}
//			
//			
			request.setAttribute("hmProjectServiceMap", hmProjectServiceMap);
			request.setAttribute("hmProjectSkillMap", hmProjectSkillMap);
			request.setAttribute("hProjectRateMap", hProjectRateMap);
			request.setAttribute("hmServiceTasksMap", hmServiceTasksMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}
