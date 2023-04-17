package com.konnect.jpms.requsitions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetLTAAmount extends ActionSupport  implements ServletRequestAware, IStatements {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	 
	CommonFunctions CF;
	private static Logger log = Logger.getLogger(GetLTAAmount.class);
	
	String strEmpId;
	String strTypeId;
	
	
	public String execute() {
	
		session = request.getSession();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
				
		getLTAAmount(); 
		
		return SUCCESS;
	}
	
	
	public void getLTAAmount(){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select sum(amount) as amount from payroll_generation_lta where emp_id=? and salary_head_id=? " +
					"and is_paid=true group by emp_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrTypeId()));
			rs=pst.executeQuery();
			double dblLTAAmount = 0.0d;  
			while(rs.next()) {
				dblLTAAmount = rs.getDouble("amount");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select sum(applied_amount) as applied_amount from emp_lta_details where emp_id=? and salary_head_id=? " +
					"and is_approved in (0,1) group by emp_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrTypeId()));
			rs=pst.executeQuery();
			double dblAppliedAmount = 0.0d;
			while(rs.next()) {
				dblAppliedAmount = rs.getDouble("applied_amount");
			}
			rs.close();
			pst.close();
			
			double amt = dblLTAAmount - dblAppliedAmount;
			if(amt < 0.0d) {
				amt = 0.0d;
			}
			StringBuilder sb = new StringBuilder();
			sb.append("<input type=\"text\" name=\"strActualAmount\" id=\"strActualAmount\" value=\""+uF.formatIntoTwoDecimalWithOutComma(amt)+"\" readonly=\"true\"/>");
			
			request.setAttribute("STATUS_MSG", sb.toString());
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}


	public String getStrEmpId() {
		return strEmpId;
	}


	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}


	public String getStrTypeId() {
		return strTypeId;
	}


	public void setStrTypeId(String strTypeId) {
		this.strTypeId = strTypeId;
	}
	
}
