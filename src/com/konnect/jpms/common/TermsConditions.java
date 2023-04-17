package com.konnect.jpms.common;

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

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TermsConditions extends ActionSupport implements ServletRequestAware, IStatements {
 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	private static Logger log = Logger.getLogger(TermsConditions.class);
	
	public String execute() throws Exception {
		
		session = request.getSession(true);
		CF  = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		
		
		request.setAttribute(PAGE, "/jsp/common/TermsConditions.jsp");
		
		
		
		if(getStrTermsConditions()!=null){
			return addTermsConditions();
		}
		
		
		return SUCCESS;
	}
	  
	public String addTermsConditions() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("update user_details set is_termscondition = true where user_id=?");
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(USERID)));
			pst.execute();
			pst.close();
			
			CF.setTermsCondition(true);
			
			return "dashboard";
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
	}
 
	
	String strTermsConditions;
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrTermsConditions() {
		return strTermsConditions;
	}

	public void setStrTermsConditions(String strTermsConditions) {
		this.strTermsConditions = strTermsConditions;
	}

}