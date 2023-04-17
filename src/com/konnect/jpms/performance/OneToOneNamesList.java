package com.konnect.jpms.performance;

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

public class OneToOneNamesList extends ActionSupport implements ServletRequestAware, IStatements {
	HttpSession session;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	private String strSearchJob;
	private String callFrom;
	String strSessionEmpId = null;

	CommonFunctions CF = null;
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
			
		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		
		strSessionEmpId = (String) session.getAttribute(EMPID);

		UtilityFunctions uF = new UtilityFunctions();
		
		getReviewNamesList(uF);
		return LOAD;
	}
	
	private void getReviewNamesList(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
    	Map<String, String> hmOnetoOne = new LinkedHashMap<String, String>();
    	List<String> alOnetoOnelist = new ArrayList<String>();
		Map<String,List<String>> hmReviewNames = new LinkedHashMap<String,List<String>>(); 
	    try {
	    	con = db.makeConnection(con);
	    		
			pst = con.prepareStatement("select * from OneToOne_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				hmOnetoOne.put(rst.getString("id"), rst.getString("name"));
				alOnetoOnelist.add(rst.getString("id"));
			}
			rst.close();
			pst.close();
	    }catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("hmOnetoOne", hmOnetoOne);
		request.setAttribute("onetoOnelist", alOnetoOnelist);
	  }
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}
