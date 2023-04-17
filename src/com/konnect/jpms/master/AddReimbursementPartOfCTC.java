package com.konnect.jpms.master;

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

public class AddReimbursementPartOfCTC extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strUserType;  
	String strSessionEmpId; 
	
	String operation;
	String strOrg;
	String strLevel;
	
	String reimbCTCId;
	String reimbCode;
	String reimbName;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId =  (String)session.getAttribute(EMPID);		
		
		UtilityFunctions uF = new UtilityFunctions();
		String submit = (String) request.getParameter("submit");
		if(getOperation()!=null && getOperation().trim().equalsIgnoreCase("D")){
			deleteReimbursementPartOfCTC(uF);
			return SUCCESS;
		} else if(getOperation()!=null && getOperation().trim().equalsIgnoreCase("A")){
			if(submit!=null){
				insertReimbursementPartOfCTC(uF);
				return SUCCESS;
			}
		} else if(uF.parseToInt(getReimbCTCId())>0 && getOperation()!=null && getOperation().trim().equalsIgnoreCase("E")){
			setOperation("U");
			viewReimbursementPartOfCTC(uF);
			return LOAD;
		} else if(uF.parseToInt(getReimbCTCId())>0 && getOperation()!=null && getOperation().trim().equalsIgnoreCase("U")){
			updateReimbursementPartOfCTC(uF);
			return SUCCESS;
		}
		
	
		return LOAD;
	}


	private void deleteReimbursementPartOfCTC(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from reimbursement_ctc_details where reimbursement_ctc_id=?");
			pst.setInt(1, uF.parseToInt(getReimbCTCId()));
			int x = pst.executeUpdate();
			if(x > 0){
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully deleted reimbursements part of ctc."+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Colud not delete reimbursements part of ctc. Please,try again."+END);
			}
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Colud not delete reimbursements part of ctc. Please,try again."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void updateReimbursementPartOfCTC(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
//			reimbursement_code,reimbursement_name,level_id,org_id,added_by,added_date,update_by,update_date
			
			pst = con.prepareStatement("update reimbursement_ctc_details set reimbursement_code=?,reimbursement_name=?,update_by=?,update_date=? " +
					"where reimbursement_ctc_id=?");
			pst.setString(1, getReimbCode());
			pst.setString(2, getReimbName());
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt(getReimbCTCId()));
			int x = pst.executeUpdate();
			if(x > 0){
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully updated reimbursements part of ctc."+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Colud not update reimbursements part of ctc. Please,try again."+END);
			}
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Colud not update reimbursements part of ctc. Please,try again."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void viewReimbursementPartOfCTC(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from reimbursement_ctc_details where reimbursement_ctc_id=?");
			pst.setInt(1, uF.parseToInt(getReimbCTCId()));
			rs = pst.executeQuery();
			while(rs.next()){
				setReimbCTCId(rs.getString("reimbursement_ctc_id"));
				setReimbCode(rs.getString("reimbursement_code"));
				setReimbName(rs.getString("reimbursement_name"));				
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void insertReimbursementPartOfCTC(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("insert into reimbursement_ctc_details(reimbursement_code,reimbursement_name,level_id,org_id,added_by," +
					"added_date)values(?,?,?,?, ?,?)");
			pst.setString(1, getReimbCode());
			pst.setString(2, getReimbName());
			pst.setInt(3, uF.parseToInt(getStrLevel()));
			pst.setInt(4, uF.parseToInt(getStrOrg()));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			int x = pst.executeUpdate();
			if(x > 0){
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully inserted reimbursements part of ctc."+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Colud not insert reimbursements part of ctc. Please,try again."+END);
			}
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Colud not insert reimbursements part of ctc. Please,try again."+END);
			e.printStackTrace();
		} finally {
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

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}


	public String getReimbCTCId() {
		return reimbCTCId;
	}


	public void setReimbCTCId(String reimbCTCId) {
		this.reimbCTCId = reimbCTCId;
	}


	public String getReimbCode() {
		return reimbCode;
	}


	public void setReimbCode(String reimbCode) {
		this.reimbCode = reimbCode;
	}


	public String getReimbName() {
		return reimbName;
	}


	public void setReimbName(String reimbName) {
		this.reimbName = reimbName;
	}


	public String getUserscreen() {
		return userscreen;
	}


	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}


	public String getNavigationId() {
		return navigationId;
	}


	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}


	public String getToPage() {
		return toPage;
	}


	public void setToPage(String toPage) {
		this.toPage = toPage;
	}
	
}