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

public class AddMobileBillReimbursement extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strUserType;  
	String strSessionEmpId; 
	
	String operation;
	String strOrg;
	String strLevel;
	
	String mobileDefaultPolicyValue;
	String mobileDefaultPolicy;
	String mobileLimitType;
	String mobileLimit;
	String reimbPolicyId;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId =  (String)session.getAttribute(EMPID);		
		
		UtilityFunctions uF = new UtilityFunctions();
		
		String submit = (String) request.getParameter("submit");
		if(getOperation()!=null && getOperation().trim().equalsIgnoreCase("D")){
			deleteReimbursementPolicy(uF);
			return SUCCESS;
		} else if(getOperation()!=null && getOperation().trim().equalsIgnoreCase("A")){
			if(submit!=null){
				insertReimbursementPolicy(uF);
				return SUCCESS;
			}
		} else if(uF.parseToInt(getReimbPolicyId())>0 && getOperation()!=null && getOperation().trim().equalsIgnoreCase("E")){
			setOperation("U");
			viewReimbursementPolicy(uF);
			return LOAD;
		} else if(uF.parseToInt(getReimbPolicyId())>0 && getOperation()!=null && getOperation().trim().equalsIgnoreCase("U")){
			updateReimbursementPolicy(uF);
			return SUCCESS;
		}
		
		setMobileDefaultPolicyValue("true");
	
		return LOAD;
	}


	private void deleteReimbursementPolicy(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from reimbursement_policy where reimbursement_policy_id=?");
			pst.setInt(1, uF.parseToInt(getReimbPolicyId()));
			int x = pst.executeUpdate();
			if(x > 0){
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully deleted mobile reimbursements."+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Colud not delete mobile reimbursements. Please,try again."+END);
			}
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Colud not delete mobile reimbursements. Please,try again."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void updateReimbursementPolicy(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update reimbursement_policy set is_default_policy=?,mobile_limit_type=?,mobile_limit=?,added_by=?,entry_date=? where reimbursement_policy_id=?");
			pst.setBoolean(1, uF.parseToBoolean(getMobileDefaultPolicy()));
			pst.setInt(2, uF.parseToInt(getMobileLimitType()));
			pst.setDouble(3, uF.parseToDouble(getMobileLimit()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt(getReimbPolicyId()));
			int x = pst.executeUpdate();
			if(x > 0){
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully updated mobile reimbursements."+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Colud not update mobile reimbursements. Please,try again."+END);
			}
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Colud not update mobile reimbursements. Please,try again."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void viewReimbursementPolicy(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from reimbursement_policy where reimbursement_policy_id=?");
			pst.setInt(1, uF.parseToInt(getReimbPolicyId()));
			rs = pst.executeQuery();
			while(rs.next()){
				setReimbPolicyId(rs.getString("reimbursement_policy_id"));
				setMobileDefaultPolicyValue(""+uF.parseToBoolean(rs.getString("is_default_policy")));
				setMobileLimitType(rs.getString("mobile_limit_type"));
				setMobileLimit(""+uF.parseToDouble(rs.getString("mobile_limit")));
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


	private void insertReimbursementPolicy(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			//reimbursement_policy_id,reimbursement_policy_type,is_default_policy,mobile_limit,level_id,org_id,added_by,entry_date
			pst = con.prepareStatement("insert into reimbursement_policy(reimbursement_policy_type,is_default_policy,mobile_limit_type,mobile_limit," +
					"level_id,org_id,added_by,entry_date)values(?,?,?,?, ?,?,?,?)");
			pst.setInt(1, REIMBURSEMENTS_MOBILE_BILL);
			pst.setBoolean(2, uF.parseToBoolean(getMobileDefaultPolicy()));
			pst.setInt(3, uF.parseToInt(getMobileLimitType()));
			pst.setDouble(4, uF.parseToDouble(getMobileLimit()));
			pst.setInt(5, uF.parseToInt(getStrLevel()));
			pst.setInt(6, uF.parseToInt(getStrOrg()));
			pst.setInt(7, uF.parseToInt(strSessionEmpId));
			pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
			int x = pst.executeUpdate();
			if(x > 0){
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully inserted mobile reimbursements."+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Colud not insert mobile reimbursements. Please,try again."+END);
			}
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Colud not insert mobile reimbursements. Please,try again."+END);
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

	public String getMobileDefaultPolicy() {
		return mobileDefaultPolicy;
	}

	public void setMobileDefaultPolicy(String mobileDefaultPolicy) {
		this.mobileDefaultPolicy = mobileDefaultPolicy;
	}

	public String getMobileLimit() {
		return mobileLimit;
	}

	public void setMobileLimit(String mobileLimit) {
		this.mobileLimit = mobileLimit;
	}

	public String getMobileDefaultPolicyValue() {
		return mobileDefaultPolicyValue;
	}

	public void setMobileDefaultPolicyValue(String mobileDefaultPolicyValue) {
		this.mobileDefaultPolicyValue = mobileDefaultPolicyValue;
	}


	public String getReimbPolicyId() {
		return reimbPolicyId;
	}


	public void setReimbPolicyId(String reimbPolicyId) {
		this.reimbPolicyId = reimbPolicyId;
	}


	public String getMobileLimitType() {
		return mobileLimitType;
	}


	public void setMobileLimitType(String mobileLimitType) {
		this.mobileLimitType = mobileLimitType;
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