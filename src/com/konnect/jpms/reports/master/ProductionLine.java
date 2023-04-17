package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

public class ProductionLine extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	String strUsertypeId;
	String strUserType;
	
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
		strUsertypeId = (String) session.getAttribute(BASEUSERTYPEID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
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
		viewProductionLine(uF);
		
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
					if(getStrOrg().equals(orgList.get(i).getOrgId())) {
						strOrg=orgList.get(i).getOrgName();
					}
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
	
	public String viewProductionLine(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("SELECT * FROM production_line_details where org_id=? order by production_line_name");
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			List<Map<String, String>> prodLineList = new ArrayList<Map<String, String>>();
			while(rs.next()) {
				Map<String, String> hmInnerPL = new HashMap<String, String>();
				
				hmInnerPL.put("PRODUCTION_LINE_ID", rs.getString("production_line_id"));
				hmInnerPL.put("PRODUCTION_LINE_CODE", rs.getString("production_line_code"));
				hmInnerPL.put("PRODUCTION_LINE_NAME", rs.getString("production_line_name"));
				hmInnerPL.put("PRODUCTION_LINE_ORG_ID", rs.getString("org_id"));

				prodLineList.add(hmInnerPL);
			}
			rs.close();
			pst.close();
			request.setAttribute("prodLineList", prodLineList);
			
			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con);
			if(hmSalaryHeadsMap == null) hmSalaryHeadsMap = new LinkedHashMap<String, String>();
			
			pst = con.prepareStatement("select * from production_line_heads plh, level_details ld where plh.level_id=ld.level_id and ld.org_id=?");
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			Map<String, List<Map<String, String>>> hmProdSalaryHeads = new HashMap<String, List<Map<String,String>>>();
			while(rs.next()){
				List<Map<String, String>> alPLHeadsList = hmProdSalaryHeads.get(rs.getString("production_line_id"));
				if(alPLHeadsList == null) alPLHeadsList = new ArrayList<Map<String,String>>();
					
				Map<String, String> hmPLHeads = new HashMap<String, String>();
				hmPLHeads.put("PRODUCTION_LINE_HEAD_ID", rs.getString("production_line_head_id"));
				hmPLHeads.put("PRODUCTION_LINE_ID", rs.getString("production_line_id"));
				hmPLHeads.put("PRODUCTION_LINE_LEVEL_ID", rs.getString("level_id"));
				hmPLHeads.put("PRODUCTION_LINE_LEVEL_CODE", rs.getString("level_code"));
				hmPLHeads.put("PRODUCTION_LINE_LEVEL_NAME", rs.getString("level_name"));
				
				StringBuilder sbSalaryHeads = null;
				if (rs.getString("salary_heads")!=null){
					List<String> al = Arrays.asList(rs.getString("salary_heads").split(","));
					for(int i = 0; al!=null && i < al.size(); i++){
						if(uF.parseToInt(al.get(i).trim()) > 0){
							if(sbSalaryHeads == null){
								sbSalaryHeads = new StringBuilder();
								sbSalaryHeads.append(uF.showData(hmSalaryHeadsMap.get(al.get(i).trim()), ""));
							} else {
								sbSalaryHeads.append(", "+uF.showData(hmSalaryHeadsMap.get(al.get(i).trim()), ""));
							}
						}
					}
				}
				hmPLHeads.put("PRODUCTION_LINE_SALARY_HEAD", (sbSalaryHeads != null ? sbSalaryHeads.toString() : ""));
				
				alPLHeadsList.add(hmPLHeads);
				
				hmProdSalaryHeads.put(rs.getString("production_line_id"), alPLHeadsList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProdSalaryHeads", hmProdSalaryHeads);
			
			
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