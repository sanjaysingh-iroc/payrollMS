package com.konnect.jpms.task.tax;

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

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DocumentCategories extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	
	List<FillOrganisation> orgList;
	String strOrg;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	public String execute() throws Exception {
		
		request.setAttribute(PAGE, "/jsp/task/tax/DocumentCategories.jsp");
		request.setAttribute(TITLE, "Document Categories");
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView) {
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		orgList = new FillOrganisation(request).fillOrganisation();
		if(getStrOrg()==null && orgList!=null && orgList.size()>0) {
			setStrOrg((String)session.getAttribute(ORGID));
		}
		
		viewDocumentCategory(uF);
		getSelectedFilter(uF);
		
		return SUCCESS;
 
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
	

	private void viewDocumentCategory(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select pcd.*,od.org_name,od.org_code from project_category_details pcd, org_details od where pcd.org_id = od.org_id and pcd.project_category_id>1");
			if(uF.parseToInt(getStrOrg()) > 0) {
				sbQuery.append(" and pcd.org_id = "+uF.parseToInt(getStrOrg())+" ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			Map<String, String> hmCategoryOrgName = new HashMap<String, String>();
			Map<String, List<Map<String, String>>> hmDocumentCategory = new HashMap<String, List<Map<String, String>>>();
			while(rs.next()) {
				List<Map<String, String>> alList = (List<Map<String, String>>) hmDocumentCategory.get(rs.getString("org_id"));
				if(alList == null) alList = new ArrayList<Map<String, String>>();
				
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("PROJECT_CATEGORY_ID", rs.getString("project_category_id"));
				hmInner.put("ORG_ID", rs.getString("org_id"));
				hmInner.put("ORG_NAME", CF.getOrgNameById(con, rs.getString("org_id")));
				hmInner.put("PROJECT_CATEGORY", rs.getString("project_category"));
				hmInner.put("PROJECT_DESCRIPTION", rs.getString("project_description"));
				hmInner.put("ADDED_BY", CF.getEmpNameMapByEmpId(con,rs.getString("added_by")));
				hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("UPDATE_BY", CF.getEmpNameMapByEmpId(con,rs.getString("updated_by")));
				hmInner.put("UPDATE_DATE", uF.getDateFormat(rs.getString("update_date"), DBDATE, CF.getStrReportDateFormat()));
				alList.add(hmInner);
				
				hmCategoryOrgName.put(rs.getString("org_id"), rs.getString("org_name") + " [" +rs.getString("org_code") +"]");
				
				hmDocumentCategory.put(rs.getString("org_id"), alList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmCategoryOrgName", hmCategoryOrgName);
			request.setAttribute("hmDocumentCategory", hmDocumentCategory);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}
