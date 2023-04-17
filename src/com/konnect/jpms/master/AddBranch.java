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
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddBranch extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	CommonFunctions CF;
	HttpSession session;
	
	private String bankId;
	private String branchId;
	private String branchCode;
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
	private String swiftCode;
	private String bankClearingCode;
	private String bankAccNo;
	private String otherInformation;
	
	private List<FillState> stateList;
	private List<FillCountry> countryList;
	
	private String isIFSC;
	private String isSWIFT;
	private String isClearingCode;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	public String execute() throws Exception {
		
		request.setAttribute(PAGE, "/jsp/master/AddBranch.jsp");
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
			
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");

		loadValidateBranch();
		
		if (operation!=null && operation.equals("D")) {
			return deleteBranch(strId);
		} 
		if (operation!=null && operation.equals("E")) {
			return viewBranch(strId);
		}
		
		if (getBranchId()!=null && getBranchId().length()>0 && !getBranchId().equalsIgnoreCase("NULL")) {
				return updateBranch();
		}
		if (getBankBranch()!=null && getBankBranch().length()>0) {
				return insertBranch();
		}
		return LOAD;
		
	}

	public String insertBranch() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("INSERT INTO branch_details (branch_code, bank_name, bank_description, bank_address,bank_city, bank_state_id, " +
				"bank_country_id, bank_branch, bank_email, bank_fax, bank_contact, bank_ifsc_code, bank_account_no, bank_pincode, bank_id, swift_code, " +
				"bank_clearing_code, other_information,is_ifsc,is_swift,is_clearing_code) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
			pst.setString(1, getBranchCode());
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
			pst.setString(16, getSwiftCode());
			pst.setString(17, getBankClearingCode());
			pst.setString(18, getOtherInformation());
			pst.setBoolean(19, uF.parseToBoolean(getIsIFSC()));
			pst.setBoolean(20, uF.parseToBoolean(getIsSWIFT()));
			pst.setBoolean(21, uF.parseToBoolean(getIsClearingCode()));
			pst.execute();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;
	}

	
	
	public String updateBranch() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		String updateLevel = "UPDATE branch_details SET branch_code=?, bank_name=?, bank_description=?, bank_address=?, bank_city=?, bank_state_id=?, " +
			"bank_country_id=?, bank_branch=?, bank_email=?, bank_fax=?, bank_contact=?, bank_ifsc_code=?, bank_account_no=?, bank_pincode=?, " +
			"swift_code=?, bank_clearing_code=?, other_information=?,is_ifsc=?,is_swift=?,is_clearing_code=? WHERE branch_id=?";
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateLevel);
	
			pst.setString(1, getBranchCode());
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
			pst.setString(15, getSwiftCode());
			pst.setString(16, getBankClearingCode());
			pst.setString(17, getOtherInformation());
			pst.setBoolean(18, uF.parseToBoolean(getIsIFSC()));
			pst.setBoolean(19, uF.parseToBoolean(getIsSWIFT()));
			pst.setBoolean(20, uF.parseToBoolean(getIsClearingCode()));
			pst.setInt(21, uF.parseToInt(getBranchId()));
			
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
	
	
	public String viewBranch(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM branch_details where branch_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				
				setBranchCode(rs.getString("branch_code"));
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
				
				setSwiftCode(rs.getString("swift_code"));
				setBankClearingCode(rs.getString("bank_clearing_code"));
				setOtherInformation(rs.getString("other_information"));
				
				setBankBranch(rs.getString("bank_branch"));
				setBranchId(rs.getString("branch_id"));
				
				setIsIFSC(uF.parseToBoolean(rs.getString("is_ifsc")) ? "true" : "false");
				setIsSWIFT(uF.parseToBoolean(rs.getString("is_swift")) ? "true" : "false");
				setIsClearingCode(uF.parseToBoolean(rs.getString("is_clearing_code")) ? "true" : "false");
				
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
	
	public String deleteBranch(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("DELETE FROM branch_details WHERE branch_id = ?");
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
		countryList = new FillCountry(request).fillCountry();
		stateList = new FillState(request).fillState(getBankCountry());
//		stateList = new FillState(request).fillState();
     
        loadValidateBranch();
    }
	
	public String loadValidateBranch() {
		
		request.setAttribute(PAGE, PAddWLocation);  
		request.setAttribute(TITLE, TAddWLocation);
			
		countryList = new FillCountry(request).fillCountry();
		stateList = new FillState(request).fillState(getBankCountry());
//		stateList = new FillState(request).fillState();
		return LOAD;
	}
	
	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

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

	public String getBankPincode() {
		return bankPincode;
	}

	public void setBankPincode(String bankPincode) {
		this.bankPincode = bankPincode;
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

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
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

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getSwiftCode() {
		return swiftCode;
	}

	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}

	public String getBankClearingCode() {
		return bankClearingCode;
	}

	public void setBankClearingCode(String bankClearingCode) {
		this.bankClearingCode = bankClearingCode;
	}

	public String getOtherInformation() {
		return otherInformation;
	}

	public void setOtherInformation(String otherInformation) {
		this.otherInformation = otherInformation;
	}

	public String getIsIFSC() {
		return isIFSC;
	}

	public void setIsIFSC(String isIFSC) {
		this.isIFSC = isIFSC;
	}

	public String getIsSWIFT() {
		return isSWIFT;
	}

	public void setIsSWIFT(String isSWIFT) {
		this.isSWIFT = isSWIFT;
	}

	public String getIsClearingCode() {
		return isClearingCode;
	}

	public void setIsClearingCode(String isClearingCode) {
		this.isClearingCode = isClearingCode;
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