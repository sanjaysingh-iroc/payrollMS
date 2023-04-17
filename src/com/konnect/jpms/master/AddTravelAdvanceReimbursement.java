package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCountry;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddTravelAdvanceReimbursement extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strUserType;  
	String strSessionEmpId; 
	
	String operation;
	String strOrg;
	String strLevel;
	
	String reimbPolicyId;
	
	String strCountry;
	String strCity;
	String eligibleAmount;
	String eligibilityType;
	
	List<FillCountry> countryList;
	
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
			return loadTravelAdvanceReimbursements(uF);
		} else if(uF.parseToInt(getReimbPolicyId())>0 && getOperation()!=null && getOperation().trim().equalsIgnoreCase("U")){
			updateReimbursementPolicy(uF);
			return SUCCESS;
		}
		
		return loadTravelAdvanceReimbursements(uF);
	}


	private String loadTravelAdvanceReimbursements(UtilityFunctions uF) {
		countryList = new FillCountry(request).fillCountry();
		
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
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully deleted travel advance reimbursements."+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Colud not delete travel advance reimbursements. Please,try again."+END);
			}
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Colud not delete travel advance reimbursements. Please,try again."+END);
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

//			reimbursement_policy_id,reimbursement_policy_type,level_id,org_id,added_by,entry_date,country_id,
//			  city,eligible_amount,eligible_type
			pst = con.prepareStatement("update reimbursement_policy set added_by=?,entry_date=?,country_id=?,city=?,eligible_amount=?," +
					"eligible_type=?,is_default_policy=? where reimbursement_policy_id=? ");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(3, uF.parseToInt(getStrCountry()));
			pst.setString(4, getStrCity());
			pst.setDouble(5, uF.parseToDouble(getEligibleAmount()));
			pst.setInt(6, uF.parseToInt(getEligibilityType()));
			pst.setBoolean(7, false);
			pst.setInt(8, uF.parseToInt(getReimbPolicyId()));
			int x = pst.executeUpdate();
			if(x > 0){
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully updated travel advance reimbursements."+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Colud not update travel advance reimbursements. Please,try again."+END);
			}
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Colud not update travel advance reimbursements. Please,try again."+END);
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
			

//			reimbursement_policy_id,reimbursement_policy_type,level_id,org_id,added_by,entry_date,country_id,
//			  city,eligible_amount,eligible_type
			pst = con.prepareStatement("select * from reimbursement_policy where reimbursement_policy_id=?");
			pst.setInt(1, uF.parseToInt(getReimbPolicyId()));
			rs = pst.executeQuery();
			while(rs.next()){
				setReimbPolicyId(rs.getString("reimbursement_policy_id"));
				setStrCountry(rs.getString("country_id"));
				setStrCity(uF.showData(rs.getString("city"), ""));
				setEligibleAmount(""+uF.parseToDouble(rs.getString("eligible_amount")));
				setEligibilityType(rs.getString("eligible_type"));
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
//			reimbursement_policy_id,reimbursement_policy_type,level_id,org_id,added_by,entry_date,country_id,
//			  city,eligible_amount,eligible_type
			pst = con.prepareStatement("insert into reimbursement_policy(reimbursement_policy_type,level_id,org_id," +
					"added_by,entry_date,country_id,city,eligible_amount,eligible_type,is_default_policy)values(?,?,?,?, ?,?,?,?, ?,?)");
			pst.setInt(1, REIMBURSEMENTS_TRAVEL_ADVANCE);
			pst.setInt(2, uF.parseToInt(getStrLevel()));
			pst.setInt(3, uF.parseToInt(getStrOrg()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt(getStrCountry()));
			pst.setString(7, getStrCity());
			pst.setDouble(8, uF.parseToDouble(getEligibleAmount()));
			pst.setInt(9, uF.parseToInt(getEligibilityType()));
			pst.setBoolean(10, false);
			int x = pst.executeUpdate();
			if(x > 0){
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully inserted travel advance reimbursements."+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Colud not insert travel advance reimbursements. Please,try again."+END);
			}
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Colud not insert travel advance reimbursements. Please,try again."+END);
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


	public String getStrCountry() {
		return strCountry;
	}


	public void setStrCountry(String strCountry) {
		this.strCountry = strCountry;
	}


	public String getStrCity() {
		return strCity;
	}


	public void setStrCity(String strCity) {
		this.strCity = strCity;
	}


	public String getEligibleAmount() {
		return eligibleAmount;
	}


	public void setEligibleAmount(String eligibleAmount) {
		this.eligibleAmount = eligibleAmount;
	}


	public String getEligibilityType() {
		return eligibilityType;
	}


	public void setEligibilityType(String eligibilityType) {
		this.eligibilityType = eligibilityType;
	}


	public List<FillCountry> getCountryList() {
		return countryList;
	}


	public void setCountryList(List<FillCountry> countryList) {
		this.countryList = countryList;
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