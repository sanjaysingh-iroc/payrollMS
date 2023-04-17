package com.konnect.jpms.leave;

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

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApproveAdvance extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	String strUserType;
	String strSessionEmpId;
	String destinations;
	
	public String execute() throws Exception {
		request.setAttribute(PAGE, PTravelAdvanceReport);
		request.setAttribute(TITLE, "Advance Report");
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		System.out.println("getStrApprove() ===>> " + getStrApprove());
		if(getStrAdvAmount()!=null && getStrAdvAmount().length()>0){
			updateAdvancesStatus();
			return "update";
		}
		viewAdvances();
		
		return loadAdvanceEntry();
	}
	public String loadAdvanceEntry() {
		return LOAD;
	}
	
	String strAdvId;
	String strComment;
	String strAdvAmount;
	String strStatus;
	String strApprove;
	String strDeny;
	
	public String viewAdvances() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from travel_advance where advance_id=?");
			pst.setInt(1, uF.parseToInt(getStrAdvId()));
			rs = pst.executeQuery();
			
			while(rs.next()){
				setStrAdvAmount(rs.getString("advance_amount"));
				setStrAdvId(rs.getString("advance_id"));
				setStrComment(rs.getString("comments"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	
	public String updateAdvancesStatus() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update travel_advance set comments=?, approved_by=?, approved_date=?, approved_amount=?, advance_status=?, advance_amount=? where advance_id=?");
			
			pst.setString(1, getStrComment());
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDouble(4, uF.parseToDouble(getStrAdvAmount()));
			
			if(getStrApprove()!=null){
				pst.setInt(5, 1);
			}else{
				pst.setInt(5, -1);
			}
			pst.setDouble(6, uF.parseToDouble(getStrAdvAmount()));
			pst.setInt(7, uF.parseToInt(getStrAdvId()));
			pst.execute();
			pst.close();
		

		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
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
	public String getStrAdvId() {
		return strAdvId;
	}
	public void setStrAdvId(String strAdvId) {
		this.strAdvId = strAdvId;
	}
	public String getStrComment() {
		return strComment;
	}
	public void setStrComment(String strComment) {
		this.strComment = strComment;
	}
	public String getStrAdvAmount() {
		return strAdvAmount;
	}
	public void setStrAdvAmount(String strAdvAmount) {
		this.strAdvAmount = strAdvAmount;
	}
	public String getStrStatus() {
		return strStatus;
	}
	public void setStrStatus(String strStatus) {
		this.strStatus = strStatus;
	}
	public String getStrApprove() {
		return strApprove;
	}
	public void setStrApprove(String strApprove) {
		this.strApprove = strApprove;
	}
	public String getStrDeny() {
		return strDeny;
	}
	public void setStrDeny(String strDeny) {
		this.strDeny = strDeny;
	}
	
}