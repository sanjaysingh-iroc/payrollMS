package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetStatesCountrywise extends ActionSupport implements ServletRequestAware {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5061018781779792337L;

	private String country;
	private String count;
	public String execute() throws Exception {
		
			getStatesOptions();
			
			return SUCCESS;
	}
	
	private String getStatesOptions() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder("");
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from state ");
			if(country != null && uF.parseToInt(country) > 0) {
				sbQuery.append("where country_id = "+country+"");
			}
			// Added by M@yuri 17-Oct-2016
			
			sbQuery.append("order by state_name");
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
 					sb.append("<option value=" + rs.getString("state_id") + ">" + rs.getString("state_name") + "</option>");
			}
            rs.close();
            pst.close();
			request.setAttribute("states", sb.toString());
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return sb.toString();
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
	
	
}
