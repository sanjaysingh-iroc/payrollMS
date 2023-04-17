package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DeleteBillingHead extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;

	CommonFunctions CF; 
	HttpSession session;
	String strSessionUserId;
	String strOrgId;
	

	String billingHeadId; 
	
	public String execute() throws Exception {
		session = request.getSession();
		strSessionUserId = (String)session.getAttribute(USERID);
		strOrgId = (String)session.getAttribute(ORGID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
//		System.out.println("getType() =====>> " + getType());
		
		UtilityFunctions uF = new UtilityFunctions();
		deleteBillingHead(uF);
		return SUCCESS;

	}

	
	private void deleteBillingHead(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from porject_billing_heads_details where pro_billing_head_id=?");
			pst.setInt(1, uF.parseToInt(getBillingHeadId()));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public String getBillingHeadId() {
		return billingHeadId;
	}

	public void setBillingHeadId(String billingHeadId) {
		this.billingHeadId = billingHeadId;
	}


	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
}