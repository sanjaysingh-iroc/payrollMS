package com.konnect.jpms.master;

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

import com.konnect.jpms.reports.master.ServiceReport;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SubjectList extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ServiceReport.class);
	
	HttpSession session;
	CommonFunctions CF;
	String strUsertypeId;
	String strUserType;
	String strBaseUserType;
	
	List<FillOrganisation> orgList;
	String strOrg;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) {
			return LOGIN;
		}
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strUsertypeId = (String) session.getAttribute(BASEUSERTYPEID);
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/master/SubjectList.jsp");
		request.setAttribute(TITLE, "Subject");
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		System.out.println("in Subject");
		if(strBaseUserType!=null && !strBaseUserType.equalsIgnoreCase(ADMIN)) {
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(uF.parseToInt(getStrOrg()) == 0 && orgList!=null && orgList.size()>0) {
				setStrOrg(orgList.get(0).getOrgId());
			}
		} else {
			if(uF.parseToInt(getStrOrg()) == 0) {
				setStrOrg((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
		}
		viewSubjects(uF);
		
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
	

	
	public String viewSubjects(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("SELECT csd.*,od.org_name,od.org_code FROM course_subject_details csd, org_details od where csd.org_id = od.org_id ");
			if(uF.parseToInt(getStrOrg())>0) {
				sbQuery.append(" and csd.org_id = "+uF.parseToInt(getStrOrg()));
			} else if(strBaseUserType!=null && !strBaseUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and csd.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by course_subject_name");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			Map<String, List<List<String>>> hmSubjectsOrgwise = new HashMap<String, List<List<String>>>();
			List<List<String>> subjectList = new ArrayList<List<String>>();
			Map<String, String> hmOrgName = new HashMap<String, String>();
			List<String> subjectIdList = new ArrayList<String>();
			while(rs.next()) {
				subjectIdList.add(rs.getString("course_subject_id"));
				subjectList = hmSubjectsOrgwise.get(rs.getString("org_id"));
				if(subjectList == null) subjectList = new ArrayList<List<String>>();
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("course_subject_id"));
				alInner.add(rs.getString("course_subject_name"));
				alInner.add(rs.getString("subject_desc"));
				hmOrgName.put(rs.getString("org_id"), rs.getString("org_name") + " [" +rs.getString("org_code") +"]");
//				alInner.add("<a href="+request.getContextPath()+"/AddService.action?E="+rs.getString("service_id")+">Edit</a> | <a onclick=\"return confirm('"+ConfirmDelete+"');\" href="+request.getContextPath()+"/AddService.action?D="+rs.getString("service_id")+">Delete</a>");
				subjectList.add(alInner);	
				hmSubjectsOrgwise.put(rs.getString("org_id"), subjectList);
			}
			rs.close();
			pst.close();
			
			
			/*Map<String, String> hmSBUEmpCount = new HashMap<String, String>();
			for(int i=0; sbuIdList != null && !sbuIdList.isEmpty() && i<sbuIdList.size(); i++) {
				pst = con.prepareStatement("select count(*) as count from employee_official_details eod, employee_personal_details epd" +
						" where epd.emp_per_id = eod.emp_id and epd.is_alive=true and eod.emp_id >0 and service_id like '%," + sbuIdList.get(i) + ",%'");
				rs = pst.executeQuery();
				while(rs.next()) {
					hmSBUEmpCount.put(sbuIdList.get(i), rs.getString("count"));
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("hmSBUEmpCount", hmSBUEmpCount);
			*/
			
			request.setAttribute("hmOrgName", hmOrgName);
			request.setAttribute("hmSubjectsOrgwise", hmSubjectsOrgwise);
			
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
