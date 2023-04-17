package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetReportCurrSign extends ActionSupport implements ServletRequestAware{

	private static final long serialVersionUID = 1L;

	String currId;
	public String execute() {
	
		UtilityFunctions uF = new UtilityFunctions();
		getReportCurrSign(uF);
		
		return SUCCESS;
	}
	
private void getReportCurrSign(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			String shortCurr = "";
			pst = con.prepareStatement("select short_currency,long_currency from currency_details where currency_id = ?");
			pst.setInt(1, uF.parseToInt(getCurrId()));
			rs = pst.executeQuery();			
			while(rs.next()) {
				shortCurr = uF.showData(rs.getString("short_currency"), "");
			}
            rs.close();
            pst.close();
			
            request.setAttribute("shortCurr", shortCurr);
		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
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
	
	public String getCurrId() {
		return currId;
	}
	
	public void setCurrId(String currId) {
		this.currId = currId;
	}
	
}
