package com.konnect.jpms.task.tax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCountry;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TaxSetting extends ActionSupport implements ServletRequestAware, IStatements {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	 
	CommonFunctions CF = null; 
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		if(session==null)
			return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)
			return LOGIN;
		
		strUserType = (String) session.getAttribute(BASEUSERTYPE); 
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, PTaxSetting);
		request.setAttribute(TITLE, TTaxSetting);
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		
		String []strPayCycleDates = null;
		if (getFinancialYear() != null) {
			strPayCycleDates = getFinancialYear().split("-");
			setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
		} else {
			strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
			setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
		}
		
		if(getMiscUpdate()!=null){
			updateMiscSetting(strPayCycleDates);
		}
		viewMiscSetting(uF, strPayCycleDates);
		
		stateList = new FillState(request).fillState("1"); // 1 is for India
		countryList = new FillCountry(request).fillCountry();
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
		return LOAD;
	}
	

	List<FillState> stateList;
	List<FillCountry> countryList;
	List<FillFinancialYears> financialYearList;
	String financialYear;
	String state;
	String flatTds;
	String serviceTax;
	String standardCess;
	String educationCess;
	String miscUpdate;
	String strMiscId;
	
	
	
	
	
	public void updateMiscSetting(String []strPayCycleDates){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
	try {
		
		
		
		
		con = db.makeConnection(con);
		
		
		pst = con.prepareStatement("select * from deduction_tax_misc_details where deduction_tax_misc_id=?");
		pst.setInt(1, uF.parseToInt(getStrMiscId()));
		rs = pst.executeQuery();
		int nTrailStatus = 0;
		while(rs.next()){
			nTrailStatus = uF.parseToInt(rs.getString("trail_status"));
		}	
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("update deduction_tax_misc_details set trail_status = ? where deduction_tax_misc_id=?");
		pst.setInt(1, 2);
		pst.setInt(2, uF.parseToInt(getStrMiscId()));
		pst.executeUpdate();	
		pst.close();

		if(nTrailStatus!=2){	
			
			pst = con.prepareStatement("insert into deduction_tax_misc_details (standard_tax, education_tax, flat_tds, service_tax, deduction_type, financial_year_from,financial_year_to, _year, entry_timestamp, user_id, trail_status,  state_id) values (?,?,?,?,?,?,?,?,?,?,?,?)");
			pst.setDouble(1, uF.parseToDouble(getStandardCess()));
			pst.setDouble(2, uF.parseToDouble(getEducationCess()));
			pst.setDouble(3, uF.parseToDouble(getFlatTds()));
			pst.setDouble(4, uF.parseToDouble(getServiceTax()));
			pst.setString(5, "P");
			pst.setDate(6, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(7, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(8, uF.parseToInt(uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, "yyyy")));
			pst.setTimestamp(9, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(10, uF.parseToInt(strSessionEmpId));
			pst.setInt(11, 1);
			pst.setInt(12, uF.parseToInt(getState()));
			pst.execute();	
			pst.close();
		
			
			session.setAttribute(MESSAGE, SUCCESSM+"Other tax policy saved successfully."+END);
		}
		
		
		
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void viewMiscSetting(UtilityFunctions uF, String []strPayCycleDates){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		LinkedHashMap hmMiscSettings = new LinkedHashMap();
		List<List<String>> alMiscSettings = new ArrayList<List<String>> ();
		
		
	try {
		
		con = db.makeConnection(con);
		
		Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
		Map<String, String> hmStateMap = CF.getStateMap(con);
		
		
		
		pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from =? and financial_year_to =? and state_id =? and trail_status=1");
		pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
		pst.setInt(3, uF.parseToInt(getState()));
		
		rs = pst.executeQuery();
		if(rs.next()){
			setFlatTds(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("flat_tds"))));
			setServiceTax(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("service_tax"))));
			setStandardCess(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("standard_tax"))));
			setEducationCess(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("education_tax"))));
			setStrMiscId(rs.getString("deduction_tax_misc_id"));
		}else{
			setFlatTds("0");
			setServiceTax("0");
			setStandardCess("0");
			setEducationCess("0");
			setStrMiscId(null);
		}	
		rs.close();
		pst.close();
		
		
		pst = con.prepareStatement("select * from deduction_tax_misc_details where trail_status=1 order by state_id, financial_year_from desc");
		
		rs = pst.executeQuery();
		
		
		String strStateNew = null;
		String strStateOld = null;
		List<String> alInner = new ArrayList<String>();
		
		while(rs.next()){
			
			strStateNew  = rs.getString("state_id");
		
			alInner = new ArrayList<String>();
			if(strStateNew!=null && !strStateNew.equalsIgnoreCase(strStateOld)){
				alMiscSettings = new ArrayList<List<String>> ();
			}
			
			
			
			alInner.add(uF.getDateFormat(rs.getString("financial_year_from"), DBDATE, "yyyy")+"-"+uF.getDateFormat(rs.getString("financial_year_to"), DBDATE, "yy"));
			alInner.add(rs.getString("flat_tds"));
			alInner.add(rs.getString("service_tax"));
			alInner.add(rs.getString("standard_tax"));
			alInner.add(rs.getString("education_tax"));
			
			
			alMiscSettings.add(alInner);
			
			hmMiscSettings.put(hmStateMap.get(strStateNew), alMiscSettings);
			
			
			strStateOld = strStateNew;
			
			request.setAttribute("UPDATED_NAME", hmEmpName.get(rs.getString("user_id")));
			request.setAttribute("UPDATED_DATE", uF.getDateFormat(rs.getString("entry_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()));
		}	
		rs.close();
		pst.close();
		
		
		request.setAttribute("alMiscSettings", alMiscSettings);
		request.setAttribute("hmMiscSettings", hmMiscSettings);
		
		
		}catch (Exception e) {
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

	public String getStrSessionEmpId() {
		return strSessionEmpId;
	}

	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getFlatTds() {
		return flatTds;
	}

	public void setFlatTds(String flatTds) {
		this.flatTds = flatTds;
	}

	public String getServiceTax() {
		return serviceTax;
	}

	public void setServiceTax(String serviceTax) {
		this.serviceTax = serviceTax;
	}

	public String getStandardCess() {
		return standardCess;
	}

	public void setStandardCess(String standardCess) {
		this.standardCess = standardCess;
	}

	public String getEducationCess() {
		return educationCess;
	}

	public void setEducationCess(String educationCess) {
		this.educationCess = educationCess;
	}

	public String getMiscUpdate() {
		return miscUpdate;
	}

	public void setMiscUpdate(String miscUpdate) {
		this.miscUpdate = miscUpdate;
	}

	public String getStrMiscId() {
		return strMiscId;
	}
	public void setStrMiscId(String strMiscId) {
		this.strMiscId = strMiscId;
	}
	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}
	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}
	public String getFinancialYear() {
		return financialYear;
	}
	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}
}
