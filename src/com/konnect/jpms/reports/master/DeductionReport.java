package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class DeductionReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(DeductionReport.class);
	
	public String execute() throws Exception {		
				
		request.setAttribute(PAGE, PReportDeduction);
		request.setAttribute(TITLE, TViewDeduction);	
		viewDeduction();			
		return loadDeduction();
	} 
	
	public String loadDeduction(){
		
		return LOAD;
	}
	
	public String viewDeduction(){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		Database db = new Database();		
		db.setRequest(request);

		try {

			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectDeductionR);
			rs = pst.executeQuery();
			while(rs.next()){
				alInner = new ArrayList<String>();
				alInner.add(Integer.toString(rs.getInt("deduction_id")));
				alInner.add(rs.getString("income_from"));
				alInner.add(rs.getString("income_to"));
				alInner.add(rs.getString("deduction_amount"));
				
//				alInner.add("<a href="+request.getContextPath()+"/AddDeduction.action?E="+rs.getString("deduction_id")+">Edit</a> | <a onclick=\"return confirm('"+ConfirmDelete+"');\" href="+request.getContextPath()+"/AddDeduction.action?D="+rs.getString("deduction_id")+">Delete</a>");
				
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
