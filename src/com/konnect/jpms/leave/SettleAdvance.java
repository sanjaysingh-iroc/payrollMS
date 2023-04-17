package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SettleAdvance extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	String strUserType;
	String strSessionEmpId;
	String destinations;
	
	public String execute() throws Exception {
		request.setAttribute(PAGE, PSettleAdvance);
		request.setAttribute(TITLE, "Settle Advance");
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		if(getStrSettleAmount()!=null && getStrSettleAmount().length()>0){
			updateSettledAdvances();
			return "update";
		}
		viewAdvances();
		
		return loadAdvanceEntry();
	}
	public String loadAdvanceEntry() {
		return LOAD;
	}
	
	String strAdvId;
	String strAdvDate;
	String strAdvAmount;
	String strClaimAmount;
	String strEligibilityAmount;
	String strBalanceAmount;
	String strMgrComment;
	String strSettleAmount;
	String strSettleComment;
	String status;
	
	String strApprovedBy;
	String strApprovedDate;
	String strSettledBy;
	String strSettledDate;
	String strBalanceSettleAmount;
	
	public String viewAdvances() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			
			
			Map<String, String> hmEmployeeName = CF.getEmpNameMap(con, null, null);
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			
		/*	pst = con.prepareStatement("select sum(reimbursement_amount) as reimbursement_amount, reimbursement_type::integer, emp_id from emp_reimbursement where reimbursement_type1 = 'T' group by reimbursement_type::integer, emp_id");
			rs = pst.executeQuery();
			Map<String, String> hmClaimReimbursement = new HashMap<String, String>();
			while(rs.next()){
				hmClaimReimbursement.put(rs.getString("emp_id")+"_"+rs.getString("reimbursement_type"), rs.getString("reimbursement_amount"));
			}*/
			
			pst = con.prepareStatement("select reimbursement_amount, reimbursement_type, emp_id from emp_reimbursement where reimbursement_type1 = 'T' order by emp_id");
			rs = pst.executeQuery();
			Map<String, String> hmClaimReimbursement = new HashMap<String, String>();
			while(rs.next()){
				if(uF.isInteger(rs.getString("reimbursement_type"))){
					double reimAmt=uF.parseToDouble(hmClaimReimbursement.get(rs.getString("emp_id")+"_"+rs.getString("reimbursement_type")));
					reimAmt+=uF.parseToDouble(rs.getString("reimbursement_amount"));
					hmClaimReimbursement.put(rs.getString("emp_id")+"_"+rs.getString("reimbursement_type"), uF.formatIntoTwoDecimalWithOutComma(reimAmt));
				}
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select * from travel_advance where advance_id=?");
			pst.setInt(1, uF.parseToInt(getStrAdvId()));
			rs = pst.executeQuery();
			while(rs.next()){
				double dblEligibilityAmount = 0.0d;
				double dblAdvAmount = uF.parseToDouble(rs.getString("advance_amount"));
				double dblClaimAmount = uF.parseToDouble(hmClaimReimbursement.get(rs.getString("emp_id")+"_"+rs.getString("travel_id")));
				double dblBalanceAmount = 0;
				
				if(dblClaimAmount>0 && dblClaimAmount>dblAdvAmount && dblClaimAmount>dblEligibilityAmount){
					dblBalanceAmount = dblEligibilityAmount - dblAdvAmount - dblClaimAmount;
				}else if(dblClaimAmount>0 && dblClaimAmount<dblEligibilityAmount){
					dblBalanceAmount = dblAdvAmount - dblClaimAmount;
				}
				
				String strCurrency = "";
				if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp_id"))) > 0){
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp_id")));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				} 
				
				setStrAdvAmount(strCurrency+uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("advance_amount"))));
				setStrAdvId(rs.getString("advance_id"));
				setStrMgrComment(rs.getString("comments"));
				setStrAdvDate(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				setStrClaimAmount(strCurrency+uF.formatIntoTwoDecimal(dblClaimAmount));
				setStrEligibilityAmount(strCurrency+uF.formatIntoTwoDecimal(dblEligibilityAmount));
				setStrBalanceAmount(strCurrency+uF.formatIntoTwoDecimal(dblBalanceAmount));
				
				setStrSettleAmount(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("settlement_amount"))));
				setStrSettleComment(rs.getString("settlement_comments"));
				setStatus(request.getParameter("status"));
				
				setStrApprovedBy(hmEmployeeName.get(rs.getString("approved_by")));
				setStrSettledBy(hmEmployeeName.get(rs.getString("settled_by")));
				setStrApprovedDate(uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()));
				setStrSettledDate(uF.getDateFormat(rs.getString("settled_date"), DBDATE, CF.getStrReportDateFormat()));
				
				double dblBalanceSettle = dblBalanceAmount - uF.parseToDouble(rs.getString("settlement_amount"));
				setStrBalanceSettleAmount(strCurrency+uF.formatIntoTwoDecimal(dblBalanceSettle));
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

	
	public String updateSettledAdvances() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update travel_advance set settlement_comments=?, settled_by=?, settled_date=?, settlement_amount=?, settlement_status=? where advance_id=?");
			pst.setString(1, getStrSettleComment());
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDouble(4, uF.parseToDouble(getStrSettleAmount()));
			pst.setBoolean(5, true);
			pst.setInt(6, uF.parseToInt(getStrAdvId()));
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
	public String getStrAdvDate() {
		return strAdvDate;
	}
	public void setStrAdvDate(String strAdvDate) {
		this.strAdvDate = strAdvDate;
	}
	public String getStrAdvAmount() {
		return strAdvAmount;
	}
	public void setStrAdvAmount(String strAdvAmount) {
		this.strAdvAmount = strAdvAmount;
	}
	public String getStrClaimAmount() {
		return strClaimAmount;
	}
	public void setStrClaimAmount(String strClaimAmount) {
		this.strClaimAmount = strClaimAmount;
	}
	public String getStrEligibilityAmount() {
		return strEligibilityAmount;
	}
	public void setStrEligibilityAmount(String strEligibilityAmount) {
		this.strEligibilityAmount = strEligibilityAmount;
	}
	public String getStrBalanceAmount() {
		return strBalanceAmount;
	}
	public void setStrBalanceAmount(String strBalanceAmount) {
		this.strBalanceAmount = strBalanceAmount;
	}
	public String getStrMgrComment() {
		return strMgrComment;
	}
	public void setStrMgrComment(String strMgrComment) {
		this.strMgrComment = strMgrComment;
	}
	public String getStrSettleAmount() {
		return strSettleAmount;
	}
	public void setStrSettleAmount(String strSettleAmount) {
		this.strSettleAmount = strSettleAmount;
	}
	public String getStrSettleComment() {
		return strSettleComment;
	}
	public void setStrSettleComment(String strSettleComment) {
		this.strSettleComment = strSettleComment;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStrApprovedBy() {
		return strApprovedBy;
	}
	public void setStrApprovedBy(String strApprovedBy) {
		this.strApprovedBy = strApprovedBy;
	}
	public String getStrSettledBy() {
		return strSettledBy;
	}
	public void setStrSettledBy(String strSettledBy) {
		this.strSettledBy = strSettledBy;
	}
	public String getStrApprovedDate() {
		return strApprovedDate;
	}
	public void setStrApprovedDate(String strApprovedDate) {
		this.strApprovedDate = strApprovedDate;
	}
	public String getStrSettledDate() {
		return strSettledDate;
	}
	public void setStrSettledDate(String strSettledDate) {
		this.strSettledDate = strSettledDate;
	}
	public String getStrBalanceSettleAmount() {
		return strBalanceSettleAmount;
	}
	public void setStrBalanceSettleAmount(String strBalanceSettleAmount) {
		this.strBalanceSettleAmount = strBalanceSettleAmount;
	}
	
}