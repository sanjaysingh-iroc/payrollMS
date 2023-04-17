package com.konnect.jpms.formmanagement;

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

public class FormList extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	List <FillOrganisation> orgList;
	String strOrg;

	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		 
		request.setAttribute(PAGE, "/jsp/formmanagement/FormList.jsp");
		request.setAttribute(TITLE, "Form Settings");
		
		UtilityFunctions uF = new UtilityFunctions();
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		} 
		
		if(uF.parseToInt(getStrOrg()) == 0) {
			setStrOrg((String)session.getAttribute(ORGID));
		}
		
		formList(uF);
		
		return loadFormList(uF);
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
	

	
	private void formList(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db=new Database();
		db.setRequest(request);
		
		try {
			con=db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			Map<String, String> hmNodes = CF.getNodes(con);
			Map<String, String> hmEmployeeNameMap = new HashMap<String, String>();
			Map<String, String> hmEmployeeCodeMap = new HashMap<String, String>();
			CF.getEmpNameCodeMap(con, null, null, hmEmployeeCodeMap, hmEmployeeNameMap);
			
			pst = con.prepareStatement("select * from form_management_details where org_id =? order by form_id desc");
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();			
			List<Map<String, String>> alReport = new ArrayList<Map<String,String>>();
			while(rs.next()){
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("FORM_ID", rs.getString("form_id"));
				hmInner.put("FORM_NAME", uF.showData(rs.getString("form_name"), ""));
				hmInner.put("FORM_NODE_ID", uF.showData(rs.getString("node_id"), ""));
				hmInner.put("FORM_NODE", uF.showData(hmNodes.get(rs.getString("node_id")), ""));
				hmInner.put("FORM_ORG_ID", uF.showData(rs.getString("org_id"), ""));
				hmInner.put("FORM_ADDED_BY", uF.showData(hmEmployeeNameMap.get(rs.getString("added_by")), ""));
				hmInner.put("FORM_ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				alReport.add(hmInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReport", alReport);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private String loadFormList(UtilityFunctions uF) {
		orgList = new FillOrganisation(request).fillOrganisation();
		
		getSelectedFilter(uF);
		
		return SUCCESS;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
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
