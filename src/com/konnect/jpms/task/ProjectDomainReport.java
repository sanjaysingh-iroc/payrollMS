package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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

public class ProjectDomainReport extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/task/ProjectDomainReport.jsp");
		request.setAttribute(TITLE, "Project Domain Report");
		UtilityFunctions uF = new UtilityFunctions();
		viewDomain(uF);			 
		return SUCCESS;
	}
	
	public String viewDomain(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			Map hmWLocationMap = CF.getWorkLocationMap(con);
			Map hmLevelMap = CF.getLevelMap(con);
			
			List<String> alInner = new ArrayList<String>();
			Map hmProjectDomainMap = new LinkedHashMap();
			
			pst = con.prepareStatement("select * from domain_details order by domain_name");
			rs = pst.executeQuery();
			while(rs.next()) {
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("domain_id"));
				alInner.add(rs.getString("domain_code"));
				alInner.add(rs.getString("domain_name"));
				alInner.add(uF.showData(rs.getString("domain_description"), "-"));
				hmProjectDomainMap.put(rs.getString("domain_id"), alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmProjectDomainMap", hmProjectDomainMap);
			
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
