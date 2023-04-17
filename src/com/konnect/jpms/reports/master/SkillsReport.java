package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillAmountType;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SkillsReport extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	List<FillLevel> levelList;
	List<FillAmountType> amountTypeList;
	CommonFunctions CF = null;
	String strUserType;
	
	HttpSession session;
	List<FillOrganisation> orgList;
	String strOrg;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	private static Logger log = Logger.getLogger(SkillsReport.class);
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		 
		UtilityFunctions uF = new UtilityFunctions();
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute(PAGE, PSkills);
		request.setAttribute(TITLE, TSkills);
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(uF.parseToInt(getStrOrg()) == 0  && orgList!=null && orgList.size()>0) {
				setStrOrg(orgList.get(0).getOrgId());
			}
		} else {
			if(uF.parseToInt(getStrOrg()) == 0) {
				setStrOrg((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
		}
		
		viewSkills(uF);
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
	

	
	public String viewSkills(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
//			if(uF.parseToInt(getStrOrg())>0) {
//				pst = con.prepareStatement("select od.org_name,od.org_code,sd.* from skills_details sd, org_details od where sd.org_id =? and sd.org_id=od.org_id order by skill_name");
//				pst.setInt(1, uF.parseToInt(getStrOrg()));
//			} else {
//				pst = con.prepareStatement("select od.org_name,od.org_code,sd.* from skills_details sd, org_details od where sd.org_id=od.org_id order by skill_name");
//			}
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select od.org_name,od.org_code,sd.* from skills_details sd, org_details od where sd.org_id=od.org_id ");
			if(uF.parseToInt(getStrOrg())>0){
				sbQuery.append(" and sd.org_id = "+uF.parseToInt(getStrOrg()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and sd.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by skill_name");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			
			Map<String, List<List<String>>> hmSkillsetOrgwise = new HashMap<String, List<List<String>>>();
			Map<String, String> hmOrgName = new HashMap<String, String>();
			List<List<String>> skillSetList = new ArrayList<List<String>>();
			
			while(rs.next()){
				skillSetList = hmSkillsetOrgwise.get(rs.getString("org_id"));
				if(skillSetList == null) skillSetList = new ArrayList<List<String>>();
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getInt("skill_id")+"");
				alInner.add(rs.getString("skill_name"));
				alInner.add(uF.showData(rs.getString("skill_description"), ""));
				skillSetList.add(alInner);
				
				hmOrgName.put(rs.getString("org_id"), rs.getString("org_name")+" ["+rs.getString("org_code")+"]");
				
				hmSkillsetOrgwise.put(rs.getString("org_id"), skillSetList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmOrgName", hmOrgName);
			request.setAttribute("hmSkillsetOrgwise", hmSkillsetOrgwise);
			
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
