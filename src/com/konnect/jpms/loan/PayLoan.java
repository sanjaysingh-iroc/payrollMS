package com.konnect.jpms.loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillColour;
import com.konnect.jpms.select.FillPaymentSource;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class PayLoan extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	CommonFunctions CF=null;
	HttpSession session;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		if(getLoanAmount()!=null){
			updateLoan(); 
			return SUCCESS;
		}
		selectLoan();

		return loadLoan();
	}

	String loanApplId;
	String loanAmount;
	String paymentDescription;
	String paymentSource;
	String strInstrumentNo;
	String strInstrumentDate;
	List<FillPaymentSource> paymentSourceList;
	
	String deductTDS;
	String TDSAmount;
	String loanPaidAmount;
	
	public String loadLoan() {
		
		paymentSourceList = new FillPaymentSource().fillPaymentMode();
		
		return LOAD;
	}

	public void updateLoan(){
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update loan_applied_details set is_paid=?, paid_date=?, paid_by=?, paid_description=?, pay_mode=?, pay_amount=?, ins_no=?, ins_date=?, tds_amount=? where loan_applied_id=?");
			pst.setBoolean(1, true);
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(3, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setString(4, getPaymentDescription());
			pst.setString(5, getPaymentSource());
			pst.setDouble(6, Math.round(uF.parseToDouble(getLoanAmount()) - uF.parseToDouble(getTDSAmount())));
			if(getPaymentSource()!=null && (getPaymentSource().equals("D") || getPaymentSource().equals("Q"))){
				pst.setString(7, getStrInstrumentNo());
				pst.setDate(8, uF.getDateFormat(getStrInstrumentDate(), DATE_FORMAT));
			}else{
				pst.setString(7, null);
				pst.setDate(8, null);
			}
			
			if(uF.parseToBoolean(getDeductTDS())){
				pst.setDouble(9, uF.parseToDouble(getTDSAmount()));
			}else{
				pst.setDouble(9, 0);
			}
//			pst.setDouble(10, uF.parseToDouble(getLoanPaidAmount()));
			pst.setInt(10, uF.parseToInt(getLoanApplId()));
			
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void selectLoan(){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from loan_applied_details where loan_applied_id=?");
			pst.setInt(1, uF.parseToInt(getLoanApplId()));
			rs = pst.executeQuery();
			while(rs.next()){
				setLoanAmount(rs.getString("amount_paid"));
				setLoanPaidAmount(rs.getString("amount_paid"));
			}
			rs.close();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void validate() {
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	public String getLoanAmount() {
		return loanAmount;
	}
	public void setLoanAmount(String loanAmount) {
		this.loanAmount = loanAmount;
	}
	public String getPaymentDescription() {
		return paymentDescription;
	}
	public void setPaymentDescription(String paymentDescription) {
		this.paymentDescription = paymentDescription;
	}
	public String getPaymentSource() {
		return paymentSource;
	}
	public void setPaymentSource(String paymentSource) {
		this.paymentSource = paymentSource;
	}
	public String getStrInstrumentNo() {
		return strInstrumentNo;
	}
	public void setStrInstrumentNo(String strInstrumentNo) {
		this.strInstrumentNo = strInstrumentNo;
	}
	public String getStrInstrumentDate() {
		return strInstrumentDate;
	}
	public void setStrInstrumentDate(String strInstrumentDate) {
		this.strInstrumentDate = strInstrumentDate;
	}
	public String getLoanApplId() {
		return loanApplId;
	}
	public void setLoanApplId(String loanApplId) {
		this.loanApplId = loanApplId;
	}

	public List<FillPaymentSource> getPaymentSourceList() {
		return paymentSourceList;
	}

	public void setPaymentSourceList(List<FillPaymentSource> paymentSourceList) {
		this.paymentSourceList = paymentSourceList;
	}

	public String getDeductTDS() {
		return deductTDS;
	}

	public void setDeductTDS(String deductTDS) {
		this.deductTDS = deductTDS;
	}

	public String getTDSAmount() {
		return TDSAmount;
	}

	public void setTDSAmount(String tDSAmount) {
		TDSAmount = tDSAmount;
	}

	public String getLoanPaidAmount() {
		return loanPaidAmount;
	}

	public void setLoanPaidAmount(String loanPaidAmount) {
		this.loanPaidAmount = loanPaidAmount;
	}

}