package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCountry;
import com.konnect.jpms.select.FillCurrency;
import com.konnect.jpms.select.FillDateFormats;
import com.konnect.jpms.select.FillPayCycleDuration;
import com.konnect.jpms.select.FillSalaryCalculationTypes;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.select.FillTimeFormats;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddOrganisation extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	String strSessionEmpId = null;
	String strBaseUserType = null;
	
	CommonFunctions CF;
	HttpSession session;
	
	String orgId;
	String orgName;
	String orgAddress;
	String orgCity;
	String orgState;
	String orgCountry;
	String orgPincode;
	String orgContact1;
	String orgEmail;
	String orgCode;
	
	List<FillCountry> countryList;
	List<FillState> stateList;
	
	List<FillPayCycleDuration> paycycleDurationList;
	List<FillSalaryCalculationTypes> salaryCalculationList;
	
	String startPaycycle;
	String displayPaycycle;
	String strPaycycleDuration;
	String strSalaryCalculation;
	

	List<FillCurrency> currencyList;
	String orgSubTitle;
	String orgDescription;
	String orgFaxNo;
	String orgWebsite;
	String orgIndustry;
	String orgCurrency;
	String orgMCARegNo;
	String orgSTRegNo;
	String orgAdditionalNote;
	String officesAt;
	
	boolean isAutoGenerate;
	String strEmpCodeAlpha;
	String strContractorCodeAlpha;
	String strEmpCodeNumber;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
    	
		paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();
		salaryCalculationList = new FillSalaryCalculationTypes().fillSalaryCalculationTypes();
		
		countryList = new FillCountry(request).fillCountry();
//		if(getOrgCountry()==null && countryList!=null && countryList.size()>0){
//			setOrgCountry(countryList.get(0).getCountryId());
//		}
		stateList = new FillState(request).fillState(getOrgCountry());
		currencyList= new FillCurrency(request).fillCurrency();
		
		viewCompanySettings();
		
		if (operation!=null && operation.equals("D")) {
			return deleteOrganisation(strId);
		}
		if (operation!=null && operation.equals("E")) {
			return viewOrganisation(strId);
		} 
		if (getOrgId()!=null && getOrgId().length()>0) { 
			return updateOrganisation();
		}
		if(getOrgName()!=null && getOrgName().length()>0){
			return insertOrganisation();
		}
		
		return LOAD;
	}

	public String loadValidateOrganisation() {
		return LOAD;
	}
	
	public String viewCompanySettings() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con); 

			pst = con.prepareStatement(selectSettings);
			rst = pst.executeQuery();
			
			Map<String, String> hmCompanySetting = new HashMap<String, String>();
 			
			while(rst.next()){

				if(rst.getString("options").equalsIgnoreCase(O_ORG_FULL_NAME)){
					hmCompanySetting.put("COMPANY_NAME", uF.showData(rst.getString("value"), ""));
				}
				if(rst.getString("options").equalsIgnoreCase(O_ORG_SUB_TITLE)){
					hmCompanySetting.put("COMPANY_SUB_TITLE", uF.showData(rst.getString("value"), ""));
				}
				if(rst.getString("options").equalsIgnoreCase(O_ORG_DESCRIPTION)){
					hmCompanySetting.put("COMPANY_DESCRIPTION", uF.showData(rst.getString("value"), ""));
				}
				if(rst.getString("options").equalsIgnoreCase(O_ORG_FULL_ADDRESS)){
					hmCompanySetting.put("COMPANY_ADDRESS", uF.showData(rst.getString("value"), ""));
				}
				if(rst.getString("options").equalsIgnoreCase(O_ORG_CITY)){
					hmCompanySetting.put("COMPANY_CITY", uF.showData(rst.getString("value"), ""));
				}
				if(rst.getString("options").equalsIgnoreCase(O_ORG_COUNTRY)){
					hmCompanySetting.put("COMPANY_COUNTRY", uF.showData(rst.getString("value"), ""));
				}
				if(rst.getString("options").equalsIgnoreCase(O_ORG_STATE)){
					hmCompanySetting.put("COMPANY_STATE", uF.showData(rst.getString("value"), ""));
				}
				if(rst.getString("options").equalsIgnoreCase(O_ORG_PINCODE)){
					hmCompanySetting.put("COMPANY_PINCODE", uF.showData(rst.getString("value"), ""));
				}
				if(rst.getString("options").equalsIgnoreCase(O_ORG_CONTACT_NO)){
					hmCompanySetting.put("COMPANY_CONTACT_NO", uF.showData(rst.getString("value"), ""));
				}
				if(rst.getString("options").equalsIgnoreCase(O_ORG_FAX_NO)){
					hmCompanySetting.put("COMPANY_FAX_NO", uF.showData(rst.getString("value"), ""));
				}
				if(rst.getString("options").equalsIgnoreCase(O_ORG_EMAIL)){
					hmCompanySetting.put("COMPANY_EMAIL", uF.showData(rst.getString("value"), ""));
				}
				if(rst.getString("options").equalsIgnoreCase(O_ORG_WEBSITE)){
					hmCompanySetting.put("COMPANY_WEBSITE", uF.showData(rst.getString("value"), ""));
				}				
				if(rst.getString("options").equalsIgnoreCase(O_ORG_INDUSTRY)){
					hmCompanySetting.put("COMPANY_INDUSTRY", uF.showData(rst.getString("value"), ""));
				}
				if(rst.getString("options").equalsIgnoreCase(O_SELECTED_CURRENCY)){
					hmCompanySetting.put("COMPANY_CURRENCY", uF.showData(rst.getString("value"), ""));
				}
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmCompanySetting", hmCompanySetting);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String insertOrganisation() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select count(*) as count from org_details");
			rs = pst.executeQuery();
			int count = 0;
			while(rs.next()){
				count = rs.getInt("count");
			}
			rs.close();
			pst.close();
			
			if(count>=uF.parseToInt(CF.getStrMaxOrganisation())){
				session.setAttribute(MESSAGE, ERRORM+"Your plan does not allow you to add organisation more than "+count+END);
				return SUCCESS;
			}
			
			pst = con.prepareStatement("insert into org_details (org_name,org_address,org_pincode,org_contact1,org_email,org_state_id,org_country_id," +
				"org_city,org_code,org_subtitle,org_description,org_fax_no,org_website,org_industry,org_currency,org_additional_note,offices_at," +
				"emp_code_auto_generate,emp_code_alpha,contractor_code_alpha,emp_code_numeric) " +
				"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
			pst.setString(1, getOrgName());
			pst.setString(2, getOrgAddress());
			pst.setString(3, getOrgPincode());
			pst.setString(4, getOrgContact1());
			pst.setString(5, getOrgEmail());
			pst.setInt(6, uF.parseToInt(getOrgState()));
			pst.setInt(7, uF.parseToInt(getOrgCountry()));
			pst.setString(8, getOrgCity());
			pst.setString(9, getOrgCode());
			pst.setString(10, getOrgSubTitle());
			pst.setString(11, getOrgDescription());
			pst.setString(12, getOrgFaxNo());
			pst.setString(13, getOrgWebsite());
			pst.setString(14, getOrgIndustry());
			pst.setInt(15, uF.parseToInt(getOrgCurrency()));
			pst.setString(16, getOrgAdditionalNote());
			pst.setString(17, getOfficesAt());
			pst.setBoolean(18, getIsAutoGenerate());
			pst.setString(19, getStrEmpCodeAlpha());
			pst.setString(20, getStrContractorCodeAlpha());
			pst.setString(21, getStrEmpCodeNumber());
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0){
				pst = con.prepareStatement("select max(org_id) as org_id from org_details");
				rs = pst.executeQuery();
				int orgId = 0;
				while(rs.next()){
					orgId = rs.getInt("org_id");
				}
				rs.close();
				pst.close();
				
				if(strBaseUserType != null && (strBaseUserType.equals(HRMANAGER) || strBaseUserType.equals(CEO) || strBaseUserType.equals(ACCOUNTANT))) {
					pst = con.prepareStatement("update user_details set org_id_access = org_id_access||'"+orgId+"'||',' where emp_id=?");
					pst.setInt(1, uF.parseToInt(strSessionEmpId));
					pst.executeUpdate();
					pst.close();
					
					pst = con.prepareStatement("select org_id_access from user_details where emp_id=?");
					pst.setInt(1, uF.parseToInt(strSessionEmpId));
					rs = pst.executeQuery();
					String strOrgAccess = null;
					while(rs.next()){
						strOrgAccess = rs.getString("org_id_access");
						if(strOrgAccess!=null && strOrgAccess.lastIndexOf(",")>=0){
							strOrgAccess = strOrgAccess.substring(1, strOrgAccess.length()-1);
						}
					}
					rs.close();
					pst.close();
					if(strOrgAccess != null) {
						session.setAttribute(ORG_ACCESS, strOrgAccess);
					}
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String updateOrganisation() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		String updateWlocationType = "UPDATE org_details SET org_name=?,org_address=?,org_pincode=?,org_contact1=?,org_email=?, " +
				"org_state_id=?,org_country_id=?, org_city=?,org_code=?,org_subtitle=?,org_description=?,org_fax_no=?,org_website=?," +
				"org_industry=?,org_currency=?,org_additional_note=?,offices_at=?,emp_code_auto_generate=?,emp_code_alpha=?," +
				"contractor_code_alpha=?,emp_code_numeric=? where org_id=?";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateWlocationType);
			pst.setString(1, getOrgName());
			pst.setString(2, getOrgAddress());
			pst.setString(3, getOrgPincode());
			pst.setString(4, getOrgContact1());
			pst.setString(5, getOrgEmail());
			pst.setInt(6, uF.parseToInt(getOrgState()));
			pst.setInt(7, uF.parseToInt(getOrgCountry()));
			pst.setString(8, getOrgCity());
			pst.setString(9, getOrgCode());
			
			pst.setString(10, getOrgSubTitle());
			pst.setString(11, getOrgDescription());
			pst.setString(12, getOrgFaxNo());
			pst.setString(13, getOrgWebsite());
			pst.setString(14, getOrgIndustry());
			pst.setInt(15, uF.parseToInt(getOrgCurrency()));
			pst.setString(16, getOrgAdditionalNote());
			pst.setString(17, getOfficesAt());
			pst.setBoolean(18, getIsAutoGenerate());
			pst.setString(19, getStrEmpCodeAlpha());
			pst.setString(20, getStrContractorCodeAlpha());
			pst.setString(21, getStrEmpCodeNumber());
			pst.setInt(22, uF.parseToInt(getOrgId()));			
			pst.executeUpdate();
			pst.close();
			
			
			StringBuilder sbLocations = null;
			pst = con.prepareStatement("select wlocation_id from work_location_info where org_id =?");
			pst.setInt(1, uF.parseToInt(getOrgId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				if(sbLocations == null) {
					sbLocations = new StringBuilder();
					sbLocations.append(rs.getString("wlocation_id"));
				} else {
					sbLocations.append(","+rs.getString("wlocation_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(sbLocations != null && sbLocations.length() > 0) {
				pst = con.prepareStatement("update level_skill_rates set curr_id = ? where wlocation_id in ("+sbLocations.toString()+")");
				pst.setInt(1, uF.parseToInt(getOrgCurrency()));
				pst.executeUpdate();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String deleteOrganisation(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("DELETE FROM org_details WHERE org_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM work_location_type WHERE org_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM work_location_info WHERE org_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.executeUpdate();
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

	
	public String viewOrganisation(String strId) {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from org_details where org_id =?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			//org_subtitle,org_description,org_fax_no,org_website,org_industry,org_currency
			while(rs.next()) {
				
				setOrgName(rs.getString("org_name"));
				setOrgAddress(rs.getString("org_address"));
				setOrgPincode(rs.getString("org_pincode"));
				setOrgContact1(rs.getString("org_contact1"));
				setOrgEmail(rs.getString("org_email"));
				setOrgState(rs.getString("org_state_id"));
				setOrgCountry(rs.getString("org_country_id"));
				setOrgCity(rs.getString("org_city"));
				setOrgId(rs.getString("org_id"));
				setOrgCode(rs.getString("org_code"));
				
//				setStartPaycycle(uF.showData(uF.getDateFormat(rs.getString("start_paycycle"), DBDATE, DATE_FORMAT),""));				
//				setDisplayPaycycle(rs.getString("display_paycycle"));
//				setStrPaycycleDuration(rs.getString("duration_paycycle"));
//				setStrSalaryCalculation(rs.getString("salary_cal_basis"));
				
				setOrgSubTitle(rs.getString("org_subtitle"));
				setOrgDescription(rs.getString("org_description"));
				setOrgFaxNo(rs.getString("org_fax_no"));
				setOrgWebsite(rs.getString("org_website"));
				setOrgIndustry(rs.getString("org_industry"));
				setOrgCurrency(rs.getString("org_currency"));
//				setOrgMCARegNo(rs.getString("org_reg_no"));
//				setOrgSTRegNo(rs.getString("org_st_reg_no"));
				setOrgAdditionalNote(rs.getString("org_additional_note"));
				setOfficesAt(rs.getString("offices_at"));
				setIsAutoGenerate(uF.parseToBoolean(rs.getString("emp_code_auto_generate")));
				setStrEmpCodeAlpha(rs.getString("emp_code_alpha"));
				setStrContractorCodeAlpha(rs.getString("contractor_code_alpha"));
				setStrEmpCodeNumber(rs.getString("emp_code_numeric"));
			}
			rs.close();
			pst.close();
			
			stateList = new FillState(request).fillState(getOrgCountry());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
	}
	
	
	

	public void validate() {


	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgAddress() {
		return orgAddress;
	}

	public void setOrgAddress(String orgAddress) {
		this.orgAddress = orgAddress;
	}

	public String getOrgCity() {
		return orgCity;
	}

	public void setOrgCity(String orgCity) {
		this.orgCity = orgCity;
	}

	public String getOrgState() {
		return orgState;
	}

	public void setOrgState(String orgState) {
		this.orgState = orgState;
	}

	public String getOrgCountry() {
		return orgCountry;
	}

	public void setOrgCountry(String orgCountry) {
		this.orgCountry = orgCountry;
	}

	public String getOrgPincode() {
		return orgPincode;
	}

	public void setOrgPincode(String orgPincode) {
		this.orgPincode = orgPincode;
	}

	public String getOrgContact1() {
		return orgContact1;
	}

	public void setOrgContact1(String orgContact1) {
		this.orgContact1 = orgContact1;
	}

	public String getOrgEmail() {
		return orgEmail;
	}

	public void setOrgEmail(String orgEmail) {
		this.orgEmail = orgEmail;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public List<FillCountry> getCountryList() {
		return countryList;
	}

	public void setCountryList(List<FillCountry> countryList) {
		this.countryList = countryList;
	}

	public List<FillState> getStateList() {
		return stateList;
	}

	public void setStateList(List<FillState> stateList) {
		this.stateList = stateList;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public List<FillPayCycleDuration> getPaycycleDurationList() {
		return paycycleDurationList;
	}

	public void setPaycycleDurationList(
			List<FillPayCycleDuration> paycycleDurationList) {
		this.paycycleDurationList = paycycleDurationList;
	}

	public List<FillSalaryCalculationTypes> getSalaryCalculationList() {
		return salaryCalculationList;
	}

	public void setSalaryCalculationList(
			List<FillSalaryCalculationTypes> salaryCalculationList) {
		this.salaryCalculationList = salaryCalculationList;
	}

	public String getStartPaycycle() {
		return startPaycycle;
	}

	public void setStartPaycycle(String startPaycycle) {
		this.startPaycycle = startPaycycle;
	}

	public String getDisplayPaycycle() {
		return displayPaycycle;
	}

	public void setDisplayPaycycle(String displayPaycycle) {
		this.displayPaycycle = displayPaycycle;
	}

	public String getStrPaycycleDuration() {
		return strPaycycleDuration;
	}

	public void setStrPaycycleDuration(String strPaycycleDuration) {
		this.strPaycycleDuration = strPaycycleDuration;
	}

	public String getStrSalaryCalculation() {
		return strSalaryCalculation;
	}

	public void setStrSalaryCalculation(String strSalaryCalculation) {
		this.strSalaryCalculation = strSalaryCalculation;
	}

	public List<FillCurrency> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(List<FillCurrency> currencyList) {
		this.currencyList = currencyList;
	}

	public String getOrgSubTitle() {
		return orgSubTitle;
	}

	public void setOrgSubTitle(String orgSubTitle) {
		this.orgSubTitle = orgSubTitle;
	}

	public String getOrgDescription() {
		return orgDescription;
	}

	public void setOrgDescription(String orgDescription) {
		this.orgDescription = orgDescription;
	}

	public String getOrgFaxNo() {
		return orgFaxNo;
	}

	public void setOrgFaxNo(String orgFaxNo) {
		this.orgFaxNo = orgFaxNo;
	}

	public String getOrgWebsite() {
		return orgWebsite;
	}

	public void setOrgWebsite(String orgWebsite) {
		this.orgWebsite = orgWebsite;
	}

	public String getOrgIndustry() {
		return orgIndustry;
	}

	public void setOrgIndustry(String orgIndustry) {
		this.orgIndustry = orgIndustry;
	}

	public String getOrgCurrency() {
		return orgCurrency;
	}

	public void setOrgCurrency(String orgCurrency) {
		this.orgCurrency = orgCurrency;
	}

	public String getOrgMCARegNo() {
		return orgMCARegNo;
	}

	public void setOrgMCARegNo(String orgMCARegNo) {
		this.orgMCARegNo = orgMCARegNo;
	}

	public String getOrgSTRegNo() {
		return orgSTRegNo;
	}

	public void setOrgSTRegNo(String orgSTRegNo) {
		this.orgSTRegNo = orgSTRegNo;
	}

	public String getOrgAdditionalNote() {
		return orgAdditionalNote;
	}

	public void setOrgAdditionalNote(String orgAdditionalNote) {
		this.orgAdditionalNote = orgAdditionalNote;
	}

	public String getOfficesAt() {
		return officesAt;
	}

	public void setOfficesAt(String officesAt) {
		this.officesAt = officesAt;
	}

	public boolean getIsAutoGenerate() {
		return isAutoGenerate;
	}

	public void setIsAutoGenerate(boolean isAutoGenerate) {
		this.isAutoGenerate = isAutoGenerate;
	}

	public String getStrEmpCodeAlpha() {
		return strEmpCodeAlpha;
	}

	public void setStrEmpCodeAlpha(String strEmpCodeAlpha) {
		this.strEmpCodeAlpha = strEmpCodeAlpha;
	}

	public String getStrContractorCodeAlpha() {
		return strContractorCodeAlpha;
	}

	public void setStrContractorCodeAlpha(String strContractorCodeAlpha) {
		this.strContractorCodeAlpha = strContractorCodeAlpha;
	}

	public String getStrEmpCodeNumber() {
		return strEmpCodeNumber;
	}

	public void setStrEmpCodeNumber(String strEmpCodeNumber) {
		this.strEmpCodeNumber = strEmpCodeNumber;
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