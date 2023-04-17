package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCountry;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddBank extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	
	private String param;
	
	private String bankId;
	private String bankCode;
	private String bankName;
	private String bankDesc;
	private String bankAddress;
	private String bankCity;
	private String bankState;
	private String bankCountry;
	private String bankPincode;
	private String bankBranch;
	private String bankEmail;
	private String bankFax;
	private String bankContactNo;
	private String bankIFSCCode;
	private String bankAccNo;
	private List<FillState> stateList;
	private List<FillCountry> countryList;
			
	public String execute() throws Exception {

		session = request.getSession();
		
		String operation = request.getParameter("operation");
		
		String strId = request.getParameter("ID");
		
		if (operation!=null && operation.equals("D")) {
			return deleteBank(strId);
		}
		if (operation!=null && operation.equals("E")) {
			return viewBank(strId);
		}
		
		if (getBankId()!=null && getBankId().length()>0) {
				return updateBank();
		}
		if (getBankCode()!=null && getBankCode().length()>0) {
				return insertBank();
		}
		
		
		if(getParam()!=null && getParam().length()>0){
			return viewBank(getParam());
		}
		
		return LOAD;
	}

	public String loadValidateBank() {
		
//		stateList = new FillState(request).fillState();
		countryList = new FillCountry(request).fillCountry();
		stateList = new FillState(request).fillState(getBankCountry());
		
		return LOAD;
	}

	public String insertBank() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(insertBank);
			pst.setString(1, getBankCode());
			pst.setString(2, getBankName());
			pst.setString(3, getBankDesc());
			pst.setString(4, getBankAddress());
			pst.setString(5, getBankCity());
			pst.setInt(6, uF.parseToInt(getBankState()));
			pst.setInt(7, uF.parseToInt(getBankCountry()));
			pst.setString(8, getBankBranch());
			pst.setString(9, getBankEmail());
			pst.setString(10, getBankFax());
			pst.setString(11, getBankContactNo());
			pst.setString(12, getBankIFSCCode());
			pst.setString(13, getBankAccNo());
			pst.setString(14, getBankPincode());
			
			pst.execute();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String viewBank(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectBankV);
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				
				setBankCode(rs.getString("bank_code"));
				setBankName(rs.getString("bank_name"));
				setBankDesc(rs.getString("bank_description"));
				setBankAddress(rs.getString("bank_address"));
				
				setBankCity(rs.getString("bank_city"));
				setBankState(rs.getString("bank_state_id"));
				setBankCountry(rs.getString("bank_country_id"));
				
				setBankEmail(rs.getString("bank_email"));
				setBankContactNo(rs.getString("bank_contact"));
				setBankFax(rs.getString("bank_fax"));
				setBankIFSCCode(rs.getString("bank_ifsc_code"));
				setBankAccNo(rs.getString("bank_account_no"));
				setBankPincode(rs.getString("bank_pincode"));
				
				if(getParam()!=null && getParam().length()>0){
					setBankBranch(null);
					setBankId(null);
				}else{
					setBankBranch(rs.getString("bank_branch"));
					setBankId(rs.getString("bank_id"));
				}
				
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
		return LOAD;
	}

	public String updateBank() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		String updateLevel = "UPDATE bank_details SET bank_code=?,bank_name=?, bank_description=?, bank_address=?,bank_city=?, bank_state_id=?,bank_country_id=?,bank_branch=?,bank_email=?,bank_fax=?,bank_contact=?,bank_ifsc_code=?,bank_account_no=?, bank_pincode=?  WHERE bank_id=?";
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateLevel);
	
			pst.setString(1, getBankCode());
			pst.setString(2, getBankName());
			pst.setString(3, getBankDesc());
			pst.setString(4, getBankAddress());
			pst.setString(5, getBankCity());
			pst.setInt(6, uF.parseToInt(getBankState()));
			pst.setInt(7, uF.parseToInt(getBankCountry()));
			pst.setString(8, getBankBranch());
			pst.setString(9, getBankEmail());
			pst.setString(10, getBankFax());
			pst.setString(11, getBankContactNo());
			pst.setString(12, getBankIFSCCode());
			pst.setString(13, getBankAccNo());
			pst.setString(14, getBankPincode());
			pst.setInt(15, uF.parseToInt(getBankId()));
			
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String deleteBank(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteBank);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	

	public void validate() {
		loadValidateBank();
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankDesc() {
		return bankDesc;
	}

	public void setBankDesc(String bankDesc) {
		this.bankDesc = bankDesc;
	}

	public String getBankAddress() {
		return bankAddress;
	}

	public void setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
	}

	public String getBankCity() {
		return bankCity;
	}

	public void setBankCity(String bankCity) {
		this.bankCity = bankCity;
	}

	public String getBankState() {
		return bankState;
	}

	public void setBankState(String bankState) {
		this.bankState = bankState;
	}

	public String getBankCountry() {
		return bankCountry;
	}

	public void setBankCountry(String bankCountry) {
		this.bankCountry = bankCountry;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public String getBankEmail() {
		return bankEmail;
	}

	public void setBankEmail(String bankEmail) {
		this.bankEmail = bankEmail;
	}

	public String getBankFax() {
		return bankFax;
	}

	public void setBankFax(String bankFax) {
		this.bankFax = bankFax;
	}

	public String getBankContactNo() {
		return bankContactNo;
	}

	public void setBankContactNo(String bankContactNo) {
		this.bankContactNo = bankContactNo;
	}

	public String getBankIFSCCode() {
		return bankIFSCCode;
	}

	public void setBankIFSCCode(String bankIFSCCode) {
		this.bankIFSCCode = bankIFSCCode;
	}

	public String getBankAccNo() {
		return bankAccNo;
	}

	public void setBankAccNo(String bankAccNo) {
		this.bankAccNo = bankAccNo;
	}

	public List<FillState> getStateList() {
		return stateList;
	}

	public void setStateList(List<FillState> stateList) {
		this.stateList = stateList;
	}

	public List<FillCountry> getCountryList() {
		return countryList;
	}

	public void setCountryList(List<FillCountry> countryList) {
		this.countryList = countryList;
	}

	public String getBankPincode() {
		return bankPincode;
	}

	public void setBankPincode(String bankPincode) {
		this.bankPincode = bankPincode;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}
}