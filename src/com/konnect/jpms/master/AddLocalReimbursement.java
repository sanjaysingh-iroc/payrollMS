package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillRimbursementType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddLocalReimbursement extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strUserType;  
	String strSessionEmpId; 
	
	String operation;
	String strOrg;
	String strLevel;
	
	List<FillRimbursementType> transportTypeList;	
	String reimbPolicyId;
	
	String localDefaultPolicy;
	String localDefaultPolicyValue;
	String localType;
	String transportType;
	String localLimitType;
	String localLimit;
	String requireApproval;
	String requireApprovalDefaultValue;
	String strMin;
	String strMax;
	
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
		
		transportTypeList = new FillRimbursementType().fillmodeoftravel();
		
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
		
		setLocalDefaultPolicyValue("true");
		setRequireApprovalDefaultValue("false");
	
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
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully deleted local reimbursements."+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Colud not delete local reimbursements. Please,try again."+END);
			}
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Colud not delete local reimbursements. Please,try again."+END);
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
			
			//reimbursement_policy_id,reimbursement_policy_type,is_default_policy,level_id,org_id,added_by,entry_date,local_type,
//			  transport_type,local_limit_type,local_limit,is_require_approval,min_amount,max_amount
			double dblLocalLimit = 0.0d;
			if(uF.parseToInt(getLocalLimitType()) == 2){
				dblLocalLimit = uF.parseToDouble(getLocalLimit());
			}
			
			pst = con.prepareStatement("update reimbursement_policy set is_default_policy=?," +
					"level_id=?,org_id=?,added_by=?,entry_date=?,local_type=?,transport_type=?,local_limit_type=?,local_limit=?," +
					"is_require_approval=?,min_amount=?,max_amount=? where reimbursement_policy_id=?");
			pst.setBoolean(1, uF.parseToBoolean(getLocalDefaultPolicy()));
			pst.setInt(2, uF.parseToInt(getStrLevel()));
			pst.setInt(3, uF.parseToInt(getStrOrg()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt(getLocalType()));
			pst.setString(7, (uF.parseToInt(getLocalType()) == 1 || uF.parseToInt(getLocalType()) == 3) ? getTransportType() : null);
			pst.setInt(8, uF.parseToInt(getLocalLimitType()));
			pst.setDouble(9, dblLocalLimit);
			pst.setBoolean(10, uF.parseToBoolean(getRequireApproval()));
			pst.setDouble(11, uF.parseToBoolean(getRequireApproval()) ? uF.parseToDouble(getStrMin()) : 0.0d);
			pst.setDouble(12, uF.parseToBoolean(getRequireApproval()) ? uF.parseToDouble(getStrMax()) : 0.0d);
			pst.setInt(13, uF.parseToInt(getReimbPolicyId()));
			int x = pst.executeUpdate();
			if(x > 0){
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully updated local reimbursements."+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Colud not update local reimbursements. Please,try again."+END);
			}
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Colud not update local reimbursements. Please,try again."+END);
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
			
			//reimbursement_policy_id,reimbursement_policy_type,is_default_policy,level_id,org_id,added_by,entry_date,local_type,
//			  transport_type,local_limit_type,local_limit,is_require_approval,min_amount,max_amount
			pst = con.prepareStatement("select * from reimbursement_policy where reimbursement_policy_id=?");
			pst.setInt(1, uF.parseToInt(getReimbPolicyId()));
			rs = pst.executeQuery();
			while(rs.next()){
				setReimbPolicyId(rs.getString("reimbursement_policy_id"));
				setLocalDefaultPolicyValue(""+uF.parseToBoolean(rs.getString("is_default_policy")));
				setLocalType(rs.getString("local_type"));
				setTransportType(rs.getString("transport_type"));
				setLocalLimitType(rs.getString("local_limit_type"));
				setLocalLimit(""+uF.parseToDouble(rs.getString("local_limit")));
				setRequireApprovalDefaultValue(""+uF.parseToBoolean(rs.getString("is_require_approval")));
				setStrMin(""+uF.parseToDouble(rs.getString("min_amount")));
				setStrMax(""+uF.parseToDouble(rs.getString("max_amount")));
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
			
			//reimbursement_policy_id,reimbursement_policy_type,is_default_policy,level_id,org_id,added_by,entry_date,local_type,
//			  transport_type,local_limit_type,local_limit,is_require_approval,min_amount,max_amount
			double dblLocalLimit = 0.0d;
			if(uF.parseToInt(getLocalLimitType()) == 2){
				dblLocalLimit = uF.parseToDouble(getLocalLimit());
			}
			pst = con.prepareStatement("insert into reimbursement_policy(reimbursement_policy_type,is_default_policy," +
					"level_id,org_id,added_by,entry_date,local_type,transport_type,local_limit_type,local_limit,is_require_approval," +
					"min_amount,max_amount)values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
			pst.setInt(1, REIMBURSEMENTS_LOCAL);
			pst.setBoolean(2, uF.parseToBoolean(getLocalDefaultPolicy()));
			pst.setInt(3, uF.parseToInt(getStrLevel()));
			pst.setInt(4, uF.parseToInt(getStrOrg()));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(7, uF.parseToInt(getLocalType()));
			pst.setString(8, (uF.parseToInt(getLocalType()) == 1 || uF.parseToInt(getLocalType()) == 3) ? getTransportType() : null);
			pst.setInt(9, uF.parseToInt(getLocalLimitType()));
			pst.setDouble(10, dblLocalLimit);
			pst.setBoolean(11, uF.parseToBoolean(getRequireApproval()));
			pst.setDouble(12, uF.parseToBoolean(getRequireApproval()) ? uF.parseToDouble(getStrMin()) : 0.0d);
			pst.setDouble(13, uF.parseToBoolean(getRequireApproval()) ? uF.parseToDouble(getStrMax()) : 0.0d);
			int x = pst.executeUpdate();
			if(x > 0){
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully inserted local reimbursements."+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Colud not insert local reimbursements. Please,try again."+END);
			}
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Colud not insert local reimbursements. Please,try again."+END);
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


	public String getReimbPolicyId() {
		return reimbPolicyId;
	}


	public void setReimbPolicyId(String reimbPolicyId) {
		this.reimbPolicyId = reimbPolicyId;
	}


	public String getLocalDefaultPolicy() {
		return localDefaultPolicy;
	}


	public void setLocalDefaultPolicy(String localDefaultPolicy) {
		this.localDefaultPolicy = localDefaultPolicy;
	}


	public String getLocalType() {
		return localType;
	}


	public void setLocalType(String localType) {
		this.localType = localType;
	}


	public String getTransportType() {
		return transportType;
	}


	public void setTransportType(String transportType) {
		this.transportType = transportType;
	}


	public String getLocalLimitType() {
		return localLimitType;
	}


	public void setLocalLimitType(String localLimitType) {
		this.localLimitType = localLimitType;
	}


	public String getLocalLimit() {
		return localLimit;
	}


	public void setLocalLimit(String localLimit) {
		this.localLimit = localLimit;
	}


	public String getRequireApproval() {
		return requireApproval;
	}


	public void setRequireApproval(String requireApproval) {
		this.requireApproval = requireApproval;
	}


	public String getStrMin() {
		return strMin;
	}


	public void setStrMin(String strMin) {
		this.strMin = strMin;
	}


	public String getStrMax() {
		return strMax;
	}


	public void setStrMax(String strMax) {
		this.strMax = strMax;
	}


	public List<FillRimbursementType> getTransportTypeList() {
		return transportTypeList;
	}


	public void setTransportTypeList(List<FillRimbursementType> transportTypeList) {
		this.transportTypeList = transportTypeList;
	}


	public String getLocalDefaultPolicyValue() {
		return localDefaultPolicyValue;
	}


	public void setLocalDefaultPolicyValue(String localDefaultPolicyValue) {
		this.localDefaultPolicyValue = localDefaultPolicyValue;
	}


	public String getRequireApprovalDefaultValue() {
		return requireApprovalDefaultValue;
	}


	public void setRequireApprovalDefaultValue(String requireApprovalDefaultValue) {
		this.requireApprovalDefaultValue = requireApprovalDefaultValue;
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