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

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class ExGratiaSlabs extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	 
	private static Logger log = Logger.getLogger(ExGratiaSlabs.class);
	HttpSession session;
	CommonFunctions CF;
	
	String userscreen;
	String navigationId;
	String toPage;
	String toTab;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/reports/master/ExGratiaSlabs.jsp"); 
		request.setAttribute(TITLE, "Ex Gratia Slabs");
		
		viewExGratiaSlabs();
		
		return LOAD;
	}
	
	
	public String viewExGratiaSlabs(){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from EX_GRATIA_SLAB_DETAILS order by GRATIA_SLAB_ID");
			rs = pst.executeQuery();
			List<Map<String,String>> gratiaSlabList = new ArrayList<Map<String,String>>(); 
			while(rs.next()){
				Map<String,String> hmInner = new HashMap<String, String>();
				hmInner.put("GRATIA_SLAB_ID",rs.getString("GRATIA_SLAB_ID"));
				hmInner.put("EX_GRATIA_SLAB",rs.getString("EX_GRATIA_SLAB"));
				hmInner.put("SLAB_FROM",rs.getString("SLAB_FROM"));
				hmInner.put("SLAB_TO",rs.getString("SLAB_TO"));
				hmInner.put("SLAB_PERCENTAGE",rs.getString("SLAB_PERCENTAGE"));
				
				gratiaSlabList.add(hmInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("gratiaSlabList", gratiaSlabList);
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

	public String getToTab() {
		return toTab;
	}

	public void setToTab(String toTab) {
		this.toTab = toTab;
	}

	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}
