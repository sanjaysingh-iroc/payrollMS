package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PayrollPolicyReport  extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(PayrollPolicyReport.class);	
	HttpSession session;
	CommonFunctions CF = null;
	
	public String execute() throws Exception {
		
		session = request.getSession();if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		
		request.setAttribute(PAGE, PPayrollPolicyReport);
		request.setAttribute(TITLE, TViewPayrollPolicy);
		
		
			viewPayrollPolicy();			
			return loadPayrollPolicy();

	}
	
	
	public String loadPayrollPolicy(){
		
		
		return LOAD;
	}
	
	public String viewPayrollPolicy(){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectPayrollPolicy);
			rs = pst.executeQuery();
			while(rs.next()){
				alInner = new ArrayList<String>();
				
				alInner.add(uF.stringMapping(rs.getString("emptype")));
				alInner.add(rs.getString("desig_name"));
				alInner.add(rs.getString("service_name"));
				alInner.add(uF.charMapping(rs.getString("paymode")));
				alInner.add(uF.charMapping(rs.getString("frequency")));
				alInner.add(rs.getString("fxdamount"));
				alInner.add(rs.getString("monamount"));
				alInner.add(rs.getString("tuesamount"));
				alInner.add(rs.getString("wedamount"));
				alInner.add(rs.getString("thursamount"));
				alInner.add(rs.getString("friamount"));
				alInner.add(rs.getString("satamount"));
				alInner.add(rs.getString("sunamount"));
				alInner.add(rs.getDouble("loading")+"%");				
				alInner.add("<a href="+request.getContextPath()+"/PayrollPolicy.action?E="+rs.getString("payroll_policy_id")+">Edit</a> <a onclick=\"return confirm('"+ConfirmDelete+"');\" href="+request.getContextPath()+"/PayrollPolicy.action?D="+rs.getString("payroll_policy_id")+">Delete</a>");
				al.add(alInner);
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", al);
			
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
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}
