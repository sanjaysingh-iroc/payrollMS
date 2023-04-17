package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateChequeNo  extends ActionSupport implements IStatements, ServletRequestAware{

	
	String strSessionEmpId;
	HttpSession session;
	CommonFunctions CF;
	
	public String execute() throws Exception {
		 
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		updateChequeDetails();
		
		return SUCCESS;
	
	}

	String chqdt;
	String chqno;
	String stmtId;
	public void updateChequeDetails(){

		Connection con = null;
		PreparedStatement pst = null, pst1 = null, pst2 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from payroll_bank_statement where statement_id=?");
			pst.setInt(1, uF.parseToInt(getStmtId()));
			rs = pst.executeQuery();
			String strStatementBody = null;
			while(rs.next()){
				strStatementBody = rs.getString("statement_body");
			}
            rs.close();
            pst.close();
			
			
			if(strStatementBody!=null && strStatementBody.indexOf(CHEQUE_NO)>0){
				strStatementBody = strStatementBody.replace(CHEQUE_NO, getChqno());
			}
			
			if(strStatementBody!=null && strStatementBody.indexOf(CHEQUE_DATED)>0){
				strStatementBody = strStatementBody.replace(CHEQUE_DATED, getChqno());
			}
			
			pst = con.prepareStatement("update payroll_bank_statement set statement_body = ?, is_cheque=true where statement_id=?");
			pst.setString(1, strStatementBody);
			pst.setInt(2, uF.parseToInt(getStmtId()));
			pst.execute();
            pst.close();
			
			 request.setAttribute("STATUS_MSG", "<img src=\"images1/icons/hd_tick.png\" width=\"20px\" />");

		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeStatements(pst2);
			db.closeConnection(con);
		}

	}
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}


	public String getChqdt() {
		return chqdt;
	}


	public void setChqdt(String chqdt) {
		this.chqdt = chqdt;
	}


	public String getChqno() {
		return chqno;
	}


	public void setChqno(String chqno) {
		this.chqno = chqno;
	}


	public String getStmtId() {
		return stmtId;
	}


	public void setStmtId(String stmtId) {
		this.stmtId = stmtId;
	}

	
}
