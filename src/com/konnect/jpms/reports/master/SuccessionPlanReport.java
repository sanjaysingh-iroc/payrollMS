package com.konnect.jpms.reports.master;

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

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SuccessionPlanReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(LevelReport.class);
	HttpSession session;
	CommonFunctions CF;
	String strUserType = null;
	List<FillOrganisation> orgList;
	String strOrg;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, PSuccessionPlan);
		request.setAttribute(TITLE, TSuccessionPlan);

		strUserType = (String) session.getAttribute(USERTYPE);
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(uF.parseToInt(getStrOrg()) == 0  && orgList!=null && orgList.size()>0){
				setStrOrg(orgList.get(0).getOrgId());
			}
		}else{
			if(uF.parseToInt(getStrOrg()) == 0){
				setStrOrg((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
		}
		
		viewLevel();
		getSelectedFilter(uF);
		
		return LOAD;
 
	}
	
	   
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getStrOrg()!=null) {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++) {
//				for(int j=0;j<getF_sbu().length;j++) {
					if(getStrOrg().equals(orgList.get(i).getOrgId())) {
						strOrg=orgList.get(i).getOrgName();
					}
//				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organizations");
			}
		} else {
			hmFilter.put("ORGANISATION", "All Organizations");
		}
		
		String selectedFilter = CF.getSelectedFilter(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	
	public String viewLevel(){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			List<String> alInner = new ArrayList<String>();
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			Map<String, List<List<String>>> hmLevelMapOrgwise = new LinkedHashMap<String,  List<List<String>>>();
			Map<String, String> hmOrgName = new HashMap<String, String>();
			Map hmDesigMap = new HashMap();
			Map hmGradeMap = new HashMap();
			
//			List<List<String>> alLGD = new ArrayList<List<String>>();
//			if(uF.parseToInt(getStrOrg()) > 0) {
//				pst = con.prepareStatement("select od.org_name,od.org_code,ld.* from level_details ld, org_details od where ld.org_id=? and ld.org_id=od.org_id order by level_id");
//				pst.setInt(1, uF.parseToInt(getStrOrg()));
//			} else {
//				pst = con.prepareStatement("select od.org_name,od.org_code,ld.* from level_details ld, org_details od where ld.org_id=od.org_id order by level_id");
//			}
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select od.org_name,od.org_code,ld.* from level_details ld, org_details od where ld.org_id=od.org_id ");
			if(uF.parseToInt(getStrOrg())>0){
				sbQuery.append(" and ld.org_id = "+uF.parseToInt(getStrOrg()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and ld.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by level_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			List<List<String>> levelList = new ArrayList<List<String>>();
			while(rs.next()){
				
				levelList = hmLevelMapOrgwise.get(rs.getString("org_id"));
				if(levelList == null) levelList = new ArrayList<List<String>>();
				
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("level_id"));
				alInner.add(rs.getString("level_code"));
				alInner.add(rs.getString("level_name"));
				levelList.add(alInner);
				
				hmOrgName.put(rs.getString("org_id"), rs.getString("org_name")+" ["+rs.getString("org_code")+"]");
				
				hmLevelMapOrgwise.put(rs.getString("org_id"), levelList);
				
//				alLGD.add(alInner);  
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select * from designation_details order by level_id, designation_id");
			rs = pst.executeQuery();
			String strLevelIdOld = null;
			String strLevelIdNew = null;
			
			while(rs.next()){
				strLevelIdNew = rs.getString("level_id");
				if(strLevelIdNew!=null && !strLevelIdNew.equalsIgnoreCase(strLevelIdOld)){
					alInner = new ArrayList<String>();
				}
				
				alInner.add(rs.getString("designation_id"));
				alInner.add(rs.getString("designation_code"));
				alInner.add(rs.getString("designation_name"));
				
				hmDesigMap.put(rs.getString("level_id"), alInner);
				
				strLevelIdOld = strLevelIdNew;
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select * from grades_details order by designation_id, grade_id");
			rs = pst.executeQuery();
			String strDesigIdNew = null;
			String strDesigIdOld = null;
			while(rs.next()){
				strDesigIdNew = rs.getString("designation_id");
				if(strDesigIdNew!=null && !strDesigIdNew.equalsIgnoreCase(strDesigIdOld)){
					alInner = new ArrayList<String>();
				}
				
				alInner.add(rs.getString("grade_id"));
				alInner.add(rs.getString("grade_code"));
				alInner.add(rs.getString("grade_name"));
				
				hmGradeMap.put(rs.getString("designation_id"), alInner);
				
				strDesigIdOld = strDesigIdNew;
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select count(*) as count, grade_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and epd.is_alive=true and eod.emp_id >0 group by grade_id");
			rs = pst.executeQuery();
			Map hmEmpGradeMap = new HashMap();
			while(rs.next()){
				hmEmpGradeMap.put(rs.getString("grade_id"), rs.getString("count"));
			}
			rs.close();
			pst.close();
			
			String []arrEnabledModules = CF.getArrEnabledModules();
			if(ArrayUtils.contains(arrEnabledModules, MODULE_PERFORMANCE_MANAGEMENT+"")>=0){
				Map<String, String> hmCriteriaId = new HashMap<String, String>();
				pst = con.prepareStatement("select * from successionplan_criteria_details");
				rs = pst.executeQuery();
				while(rs.next()){
					
					hmCriteriaId.put(rs.getString("designation_id"), rs.getString("successionplan_criteria_id"));
				}
				rs.close();
				pst.close();
//				System.out.println("hmCriteriaId ===> " + hmCriteriaId);
				request.setAttribute("hmCriteriaId", hmCriteriaId);  
			}
			
			request.setAttribute("hmOrgName", hmOrgName);
			request.setAttribute("hmLevelMapOrgwise", hmLevelMapOrgwise);
			request.setAttribute("hmDesigMap", hmDesigMap);
			request.setAttribute("hmGradeMap", hmGradeMap);
			request.setAttribute("hmEmpGradeMap", hmEmpGradeMap);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}

}
