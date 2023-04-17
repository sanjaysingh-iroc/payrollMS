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
import com.opensymphony.xwork2.ActionSupport;
import com.konnect.jpms.util.UtilityFunctions;

public class GoalKRACompany_1 extends ActionSupport implements ServletRequestAware, IStatements {
	public HttpSession session;
	public CommonFunctions CF;
	
	private String strSessionEmpId;
	private String strEmpOrgId;
	private String strUserTypeId;
	private String strUserType;
	private String strBaseUserType;
	private String dataType;
	
	public String execute() {
//		System.out.println("GoalKRACompanyScoreCard::");
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
		
		getBscList(uF);
		
		return LOAD;
	}
	
	
	public void getBscList(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, List<String>> hmBscData = new LinkedHashMap<String, List<String>>();
		try {
			con = db.makeConnection(con);
			pst=con.prepareStatement("select * from bsc_details order by bsc_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("bsc_id"));
				innerList.add(rs.getString("bsc_name"));
				innerList.add(rs.getString("bsc_vision"));
				hmBscData.put(rs.getString("bsc_id"), innerList);
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmBscData", hmBscData);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
		
	}
	
	
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
