package com.konnect.jpms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
 
public class ConfigSettings extends ActionSupport implements ServletRequestAware, IStatements {
 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	
	private List<FillCurrency> currencyList;
	private String orgCurrency;
	private String orgDescription;
	private String orgContactNo;
	private String orgFaxNo;
	private String orgWebsite;
	private String orgIndustry;
	
	private String orgFullName;
	private String orgFullAddress;
	private String orgPincode;
	private String orgSubTitle;
	private String orgLogo;
	private String orgLogoSmall;
	
	private List<FillState> stateList;
	private List<FillCountry> countryList;
	
	private String orgCity;
	private String state;
	private String orgCountry;
	private String orgEmailAddress;
	
	
	private String strDateFormat;
	private String strTimeFormat;
	
	private String strSettingsId;
	private String stndFullTimeHrs;
	private String strTimeZone;
	private String startPaycycle;
	private String displayPaycycle;
	private String strLunchBreak;
	private String strLunchDeduct;
	private String strEmailFrom;
	private String strEmailLocalHost;
	private String strEmailHost;
	private String strEmailHostPassword;
	private String strTextFrom;
	private String strSalaryCalculation;
	private boolean isAttendanceIntegrationWithActivities;
	private boolean isShowPassword;
	private String salaryFixedDays;
	
	private String strFlat_TDS;
	private String strFinancialYearStart;
	private String strFinancialYearEnd;
	
	private String strPaycycleDuration;
	private String paycycleDurationId;
	private String paycycleDurationName;
	private List<FillPayCycleDuration> paycycleDurationList;
	private List<FillDateFormats> dateFormatList;
	private List<FillTimeFormats> timeFormatList;
	
	private List<FillSalaryCalculationTypes> salaryCalculationList;
	
	private String strShortCurrency;
	private String strLongCurrency;
	private String strEducationCess;
	private String strStandardCess;
	private String strServicetax;
	
	private boolean isEmail;
	private boolean isText;
	
	private String passTrackerPassword;
	private String textTrackerPassword;
	
	private boolean isRequiredAuthentication;
	private String strEmailAuthUsername;
	private String strEmailAuthPassword;
	private String roundOffCondtion;
	
	private boolean isClockOnOff; 
	private String salaryStructure;
	
	private boolean isHalfDayLeave;
	private boolean isProductionLine;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	private boolean isTerminateWithoutFullAndFinal;
	private boolean isSandwichAbsent;
	private boolean isTDSAutoApprove;
	private boolean isShowTimeVariance;
	
	public String execute() throws Exception {
		session = request.getSession(true);
		UtilityFunctions uF = new UtilityFunctions();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
	
		request.setAttribute(PAGE, PConfigSettings);

		if (getStrSettingsId() != null && getStrSettingsId().length() > 0) {
			updateConfigSettings();
			request.setAttribute(TITLE, TConfigSettings);
			return SUCCESS;
		} 
		viewConfigSettings(uF);
		return loadConfigSettings();
		
	}

	public String loadConfigSettings() {
		request.setAttribute(PAGE, PConfigSettings);
		request.setAttribute(TITLE, TConfigSettings);
		
		paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();
		dateFormatList = new FillDateFormats().fillDateFormats();
		timeFormatList = new FillTimeFormats().fillTimeFormats();
		
		countryList = new FillCountry(request).fillCountry();
//		stateList = new FillState().fillState();
		
		salaryCalculationList = new FillSalaryCalculationTypes().fillSalaryCalculationTypes();
		
		currencyList= new FillCurrency(request).fillCurrency();
		
		return LOAD;
	}

	public String viewConfigSettings(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			
			con = db.makeConnection(con); 
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			

			pst = con.prepareStatement(selectSettings);
			rst = pst.executeQuery();
			
			while(rst.next()){

				if(rst.getString("options").equalsIgnoreCase(O_DATE_FORMAT)){
					setStrDateFormat(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_TIME_FORMAT)){
					setStrTimeFormat(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_LUNCH_DEDUCT_TIME)){
					setStrLunchDeduct(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_LUNCH_DEDUCT)){
					setStrLunchBreak(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_TIME_ZONE)){
					setStrTimeZone(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_FINANCIAL_YEAR_START)){
					setStrFinancialYearStart(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_FINANCIAL_YEAR_END)){
					setStrFinancialYearEnd(rst.getString("value"));
				}
				
				
				if(rst.getString("options").equalsIgnoreCase(O_START_PAY_CLYCLE)){
					setStartPaycycle(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_STANDARD_FULL_TIME_HOURS)){
					setStndFullTimeHrs(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_DISPLAY_PAY_CLYCLE)){
					setDisplayPaycycle(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_EMAIL_NOTIFICATIONS)){
					setIsEmail(uF.parseToBoolean(rst.getString("value")));
				}
				if(rst.getString("options").equalsIgnoreCase(O_TEXT_NOTIFICATIONS)){
					setIsText(uF.parseToBoolean(rst.getString("value")));
				}
				if(rst.getString("options").equalsIgnoreCase(O_EMAIL_HOST)){
					setStrEmailHost(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_EMAIL_LOCAL_HOST)){
					setStrEmailLocalHost(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_EMAIL_FROM)){
					setStrEmailFrom(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_HOST_PASSWORD)){
					setStrEmailHostPassword(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_TEXT_FROM)){
					setStrTextFrom(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_PAYCYCLE_DURATION)){
					setStrPaycycleDuration(rst.getString("value"));
					
				}
				if(rst.getString("options").equalsIgnoreCase(O_ORG_FULL_NAME)){
					setOrgFullName(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_ORG_FULL_ADDRESS)){
					setOrgFullAddress(rst.getString("value"));
				} 
				if(rst.getString("options").equalsIgnoreCase(O_ORG_SUB_TITLE)){
					setOrgSubTitle(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_ORG_LOGO)){
					setOrgLogo(rst.getString("value"));
					request.setAttribute("COMPANY_LOGO", rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_ORG_LOGO_SMALL)){
					setOrgLogoSmall(rst.getString("value"));
					request.setAttribute("COMPANY_LOGO_SMALL", rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_ORG_CITY)){
					setOrgCity(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_ORG_STATE)){
					setState(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_ORG_COUNTRY)){
					setOrgCountry(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_ORG_EMAIL)){
					setOrgEmailAddress(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_SHORT_CURR)){
					setStrShortCurrency(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_LONG_CURR)){
					setStrLongCurrency(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_SERVICE_TAX)){
					setStrServicetax(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_STANDARD_CESS)){
					setStrStandardCess(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_EDUCATION_CESS)){
					setStrEducationCess(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_FLAT_TDS)){
					setStrFlat_TDS(rst.getString("value"));
				}
				
				/*if(rst.getString("options").equalsIgnoreCase(O_EMP_CODE_ALPHA)){
					setStrEmpCodeAlpha(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_CONTRACTOR_CODE_ALPHA)){
					setStrContractorCodeAlpha(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_EMP_CODE_NUM)){
					setStrEmpCodeNumber(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_EMP_CODE_AUTO_GENERATION)){
					setIsAutoGenerate(uF.parseToBoolean(rst.getString("value")));
				}*/
				
				if(rst.getString("options").equalsIgnoreCase(O_ORG_PINCODE)){
					setOrgPincode(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_SALARY_CALCULATION)){
					setStrSalaryCalculation(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_ATTENDANCE_INTEGRATED_WITH_ACTIVITY)){
					setIsAttendanceIntegrationWithActivities(uF.parseToBoolean(rst.getString("value")));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_SHOW_PASSWORD)){
					setIsShowPassword(uF.parseToBoolean(rst.getString("value")));
				}
				if(rst.getString("options").equalsIgnoreCase(FIXED_MONTH_DAYS)){
					setSalaryFixedDays(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_SELECTED_CURRENCY)){
					setOrgCurrency(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_ORG_DESCRIPTION)){
					setOrgDescription(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_ORG_CONTACT_NO)){
					setOrgContactNo(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_ORG_FAX_NO)){
					setOrgFaxNo(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_ORG_WEBSITE)){
					setOrgWebsite(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_ORG_INDUSTRY)){
					setOrgIndustry(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_TRACKER_PASSWORD)){
					setPassTrackerPassword(rst.getString("value"));
					setTextTrackerPassword(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_IS_REQUIRED_AUTHENTICATION)){
					setIsRequiredAuthentication(uF.parseToBoolean(rst.getString("value")));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_EMAIL_AUTHENTICATION_USER)){
					setStrEmailAuthUsername(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_EMAIL_AUTHENTICATION_PASSWORD)){
					setStrEmailAuthPassword(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_IS_CLOCK_ON_OFF)){
					setIsClockOnOff(uF.parseToBoolean(rst.getString("value")));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_SALARY_STRUCTURE)){
					setSalaryStructure(rst.getString("value"));
				}
				/*if(rst.getString("options").equalsIgnoreCase(O_IS_RECEIPT)){
					setIsReceipt(uF.parseToBoolean(rst.getString("value")));
				}*/
				if(rst.getString("options").equalsIgnoreCase(O_IS_HALF_DAY_LEAVE)){
					setIsHalfDayLeave(uF.parseToBoolean(rst.getString("value")));
				}
				if(rst.getString("options").equalsIgnoreCase(O_ROUND_OFF_CONDITION)){
					setRoundOffCondtion(rst.getString("value"));
				}
				if(rst.getString("options").equalsIgnoreCase(O_PRODUCTION_LINE)){
					setIsProductionLine(uF.parseToBoolean(rst.getString("value")));
				}
				if(rst.getString("options").equalsIgnoreCase(O_IS_SANDWICH_ABSENT)){
					setIsSandwichAbsent(uF.parseToBoolean(rst.getString("value")));
				}
				if(rst.getString("options").equalsIgnoreCase(O_IS_TERMINATE_WITHOUT_FULLANDFINAL)){
					setIsTerminateWithoutFullAndFinal(uF.parseToBoolean(rst.getString("value")));
				}
				if(rst.getString("options").equalsIgnoreCase(O_IS_TDS_AUTO_APPROVE)){
					setIsTDSAutoApprove(uF.parseToBoolean(rst.getString("value")));
				}
				if(rst.getString("options").equalsIgnoreCase(O_IS_SHOW_TIME_VARIANCE)){
					setIsShowTimeVariance(uF.parseToBoolean(rst.getString("value")));
				}
				
				setStrSettingsId(rst.getString("id"));
				
				request.setAttribute("UPDATED_NAME", hmEmpName.get(rst.getString("user_id")));
				request.setAttribute("UPDATED_DATE", uF.getDateFormat(rst.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rst.close();
			pst.close();
			
			paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();
			dateFormatList = new FillDateFormats().fillDateFormats();
			timeFormatList = new FillTimeFormats().fillTimeFormats();
			stateList = new FillState(request).fillState(getOrgCountry());
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	public String updateConfigSettings() {

		Connection con = null;
		PreparedStatement pst = null;
		
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		CommonFunctions CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		try {

			con = db.makeConnection(con);
			
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetails(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			
			insertSettings(con,getStrDateFormat(),O_DATE_FORMAT);
			insertSettings(con,getStrTimeFormat(),O_TIME_FORMAT);
			insertSettings(con,getStrLunchBreak(),O_LUNCH_DEDUCT);
			insertSettings(con,getStrLunchDeduct(),O_LUNCH_DEDUCT_TIME);
			insertSettings(con,getStrFinancialYearStart(),O_FINANCIAL_YEAR_START);

			insertSettings(con,getStrFinancialYearEnd(),O_FINANCIAL_YEAR_END);

			insertSettings(con,getStndFullTimeHrs(),O_STANDARD_FULL_TIME_HOURS);

			insertSettings(con,getIsEmail()+"",O_EMAIL_NOTIFICATIONS);

			insertSettings(con,getIsText()+"",O_TEXT_NOTIFICATIONS);

			insertSettings(con,getStrEmailLocalHost(),O_EMAIL_LOCAL_HOST);

//			insertSettings(con,getStrEmailHost(),O_EMAIL_HOST);

//			insertSettings(con,getStrEmailHostPassword(),O_HOST_PASSWORD);

//			insertSettings(con,getStrEmailFrom(),O_EMAIL_FROM);

			insertSettings(con,getStrTextFrom(),O_TEXT_FROM);

			insertSettings(con,getOrgFullName(),O_ORG_FULL_NAME);

			insertSettings(con,getOrgFullAddress(),O_ORG_FULL_ADDRESS);

			insertSettings(con,getOrgPincode(),O_ORG_PINCODE);

			insertSettings(con,getOrgSubTitle(),O_ORG_SUB_TITLE);

			insertSettings(con,getOrgEmailAddress(),O_ORG_EMAIL);

			insertSettings(con,getOrgCity(),O_ORG_CITY);

			insertSettings(con,getState(),O_ORG_STATE);

			insertSettings(con,getOrgCountry(),O_ORG_COUNTRY);
			
			insertSettings(con,getOrgCurrency(),O_SELECTED_CURRENCY);
			
			Map<String, String> hmInnerCurrency = hmCurrencyDetailsMap.get(getOrgCurrency());
			if(hmInnerCurrency == null) hmInnerCurrency = new HashMap<String, String>();
			insertSettings(con,uF.showData(hmInnerCurrency.get("SHORT_CURR"), ""),O_SHORT_CURR);
			insertSettings(con,uF.showData(hmInnerCurrency.get("LONG_CURR"), ""),O_LONG_CURR);

			insertSettings(con,getStrServicetax(),O_SERVICE_TAX);

			insertSettings(con,getStrStandardCess(),O_STANDARD_CESS);

			insertSettings(con,getStrEducationCess(),O_EDUCATION_CESS);

			insertSettings(con,getStrFlat_TDS(),O_FLAT_TDS);

			insertSettings(con,getIsAttendanceIntegrationWithActivities()+"",O_ATTENDANCE_INTEGRATED_WITH_ACTIVITY);

			insertSettings(con,getIsShowPassword()+"",O_SHOW_PASSWORD);

			insertSettings(con,getSalaryFixedDays(),FIXED_MONTH_DAYS);

			insertSettings(con,getOrgDescription(),O_ORG_DESCRIPTION);
			insertSettings(con,getOrgContactNo(),O_ORG_CONTACT_NO);
			insertSettings(con,getOrgFaxNo(),O_ORG_FAX_NO);
			insertSettings(con,getOrgWebsite(),O_ORG_WEBSITE);
			insertSettings(con,getOrgIndustry(),O_ORG_INDUSTRY);
			
			insertSettings(con,getPassTrackerPassword(),O_TRACKER_PASSWORD);
			
//			insertSettings(con,""+getIsRequiredAuthentication(),O_IS_REQUIRED_AUTHENTICATION);
//			insertSettings(con,getStrEmailAuthUsername(),O_EMAIL_AUTHENTICATION_USER);
//			insertSettings(con,getStrEmailAuthPassword(),O_EMAIL_AUTHENTICATION_PASSWORD);
			
//			System.out.println("getIsClockOnOff() ===>> " + getIsClockOnOff());
//			System.out.println("getIsShowPassword() ===>> " + getIsShowPassword());
			
			insertSettings(con, getIsClockOnOff()+"", O_IS_CLOCK_ON_OFF);
			
//			insertSettings(con,getSalaryStructure(),O_SALARY_STRUCTURE);
//			insertSettings(con, getIsReceipt()+"", O_IS_RECEIPT); 
			insertSettings(con, getIsHalfDayLeave()+"", O_IS_HALF_DAY_LEAVE);
			insertSettings(con, getRoundOffCondtion()+"", O_ROUND_OFF_CONDITION);
			insertSettings(con, getIsProductionLine()+"", O_PRODUCTION_LINE);
			insertSettings(con, getIsSandwichAbsent()+"", O_IS_SANDWICH_ABSENT);
			insertSettings(con, getIsTerminateWithoutFullAndFinal()+"", O_IS_TERMINATE_WITHOUT_FULLANDFINAL);
			insertSettings(con, getIsTDSAutoApprove()+"", O_IS_TDS_AUTO_APPROVE);
			insertSettings(con, getIsShowTimeVariance()+"", O_IS_SHOW_TIME_VARIANCE);
			
			pst = con.prepareStatement("update settings set user_id=?, entry_date=?");
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"New Settings have been successfully saved."+END);

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	
	public void insertSettings(Connection con,String value,String option) {
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		try {
			pst = con.prepareStatement("Update settings set value=? where options=?");
			pst.setString(1, value);
			pst.setString(2, option);			
			int x=pst.executeUpdate();
			pst.close();
			if(x==0) {
				pst1 = con.prepareStatement("insert into settings(value,options)values(?,?)");
				pst1.setString(1, value);
				pst1.setString(2, option);	
				pst1.execute();
				pst1.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst1 != null) {
				try {
					pst1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getSalaryFixedDays() {
		return salaryFixedDays;
	}


	public void setSalaryFixedDays(String salaryFixedDays) {
		this.salaryFixedDays = salaryFixedDays;
	}
	public String getStndFullTimeHrs() {
		return stndFullTimeHrs;
	}

	public void setStndFullTimeHrs(String stndFullTimeHrs) {
		this.stndFullTimeHrs = stndFullTimeHrs;
	}

	public String getStrTimeZone() {
		return strTimeZone;
	}

	public void setStrTimeZone(String strTimeZone) {
		this.strTimeZone = strTimeZone;
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

	public String getStrLunchBreak() {
		return strLunchBreak;
	}

	public void setStrLunchBreak(String strLunchBreak) {
		this.strLunchBreak = strLunchBreak;
	}

	public String getStrLunchDeduct() {
		return strLunchDeduct;
	}

	public void setStrLunchDeduct(String strLunchDeduct) {
		this.strLunchDeduct = strLunchDeduct;
	}

	public String getStrEmailFrom() {
		return strEmailFrom;
	}

	public void setStrEmailFrom(String strEmailFrom) {
		this.strEmailFrom = strEmailFrom;
	}

	public String getStrEmailHost() {
		return strEmailHost;
	}

	public void setStrEmailHost(String strEmailHost) {
		this.strEmailHost = strEmailHost;
	}

	public String getStrTextFrom() {
		return strTextFrom;
	}

	public void setStrTextFrom(String strTextFrom) {
		this.strTextFrom = strTextFrom;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}


	public String getStrSettingsId() {
		return strSettingsId;
	}

	public void setStrSettingsId(String strSettingsId) {
		this.strSettingsId = strSettingsId;
	}


	public boolean getIsEmail() {
		return isEmail;
	}


	public void setIsEmail(boolean isEmail) {
		this.isEmail = isEmail;
	}


	public boolean getIsText() {
		return isText;
	}

	public void setIsText(boolean isText) {
		this.isText = isText;
	}

	public boolean getIsClockOnOff() {
		return isClockOnOff;
	}

	public void setIsClockOnOff(boolean isClockOnOff) {
		this.isClockOnOff = isClockOnOff;
	}

	public String getPaycycleDurationId() {
		return paycycleDurationId;
	}


	public void setPaycycleDurationId(String paycycleDurationId) {
		this.paycycleDurationId = paycycleDurationId;
	}


	public String getPaycycleDurationName() {
		return paycycleDurationName;
	}


	public void setPaycycleDurationName(String paycycleDurationName) {
		this.paycycleDurationName = paycycleDurationName;
	}

	

	public String getStrPaycycleDuration() {
		return strPaycycleDuration;
	}


	public void setStrPaycycleDuration(String strPaycycleDuration) {
		this.strPaycycleDuration = strPaycycleDuration;
	}


	public List<FillPayCycleDuration> getPaycycleDurationList() {
		return paycycleDurationList;
	}


	public String getOrgFullName() {
		return orgFullName;
	}


	public void setOrgFullName(String orgFullName) {
		this.orgFullName = orgFullName;
	}


	public String getOrgSubTitle() {
		return orgSubTitle;
	}


	public void setOrgSubTitle(String orgSubTitle) {
		this.orgSubTitle = orgSubTitle;
	}


	public String getOrgLogo() {
		return orgLogo;
	}


	public void setOrgLogo(String orgLogo) {
		this.orgLogo = orgLogo;
	}


	public String getStrDateFormat() {
		return strDateFormat;
	}


	public void setStrDateFormat(String strDateFormat) {
		this.strDateFormat = strDateFormat;
	}


	public String getStrTimeFormat() {
		return strTimeFormat;
	}


	public void setStrTimeFormat(String strTimeFormat) {
		this.strTimeFormat = strTimeFormat;
	}


	public List<FillDateFormats> getDateFormatList() {
		return dateFormatList;
	}


	public String getStrFinancialYearStart() {
		return strFinancialYearStart;
	}


	public void setStrFinancialYearStart(String strFinancialYearStart) {
		this.strFinancialYearStart = strFinancialYearStart;
	}


	public String getStrFinancialYearEnd() {
		return strFinancialYearEnd;
	}


	public void setStrFinancialYearEnd(String strFinancialYearEnd) {
		this.strFinancialYearEnd = strFinancialYearEnd;
	}


	public List<FillTimeFormats> getTimeFormatList() {
		return timeFormatList;
	}


	public String getStrShortCurrency() {
		return strShortCurrency;
	}


	public void setStrShortCurrency(String strShortCurrency) {
		this.strShortCurrency = strShortCurrency;
	}


	public String getStrLongCurrency() {
		return strLongCurrency;
	}


	public void setStrLongCurrency(String strLongCurrency) {
		this.strLongCurrency = strLongCurrency;
	}


	public String getStrEducationCess() {
		return strEducationCess;
	}


	public void setStrEducationCess(String strEducationCess) {
		this.strEducationCess = strEducationCess;
	}


	public String getStrStandardCess() {
		return strStandardCess;
	}


	public void setStrStandardCess(String strStandardCess) {
		this.strStandardCess = strStandardCess;
	}


	/*public String getStrEmpCodeAlpha() {
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


	public boolean getIsAutoGenerate() {
		return isAutoGenerate;
	}


	public void setIsAutoGenerate(boolean isAutoGenerate) {
		this.isAutoGenerate = isAutoGenerate;
	}*/


	public String getOrgFullAddress() {
		return orgFullAddress;
	}


	public void setOrgFullAddress(String orgFullAddress) {
		this.orgFullAddress = orgFullAddress;
	}


	public String getOrgPincode() {
		return orgPincode;
	}


	public void setOrgPincode(String orgPincode) {
		this.orgPincode = orgPincode;
	}


	public String getStrSalaryCalculation() {
		return strSalaryCalculation;
	}


	public void setStrSalaryCalculation(String strSalaryCalculation) {
		this.strSalaryCalculation = strSalaryCalculation;
	}


	public List<FillSalaryCalculationTypes> getSalaryCalculationList() {
		return salaryCalculationList;
	}


	public void setSalaryCalculationList(
			List<FillSalaryCalculationTypes> salaryCalculationList) {
		this.salaryCalculationList = salaryCalculationList;
	}


	public String getStrEmailLocalHost() {
		return strEmailLocalHost;
	}


	public void setStrEmailLocalHost(String strEmailLocalHost) {
		this.strEmailLocalHost = strEmailLocalHost;
	}

	public boolean getIsAttendanceIntegrationWithActivities() {
		return isAttendanceIntegrationWithActivities;
	}


	public void setIsAttendanceIntegrationWithActivities(boolean isAttendanceIntegrationWithActivities) {
		this.isAttendanceIntegrationWithActivities = isAttendanceIntegrationWithActivities;
	}
	public String getStrFlat_TDS() {
		return strFlat_TDS;
	}
	public void setStrFlat_TDS(String strFlat_TDS) {
		this.strFlat_TDS = strFlat_TDS;
	}
	public String getStrServicetax() {
		return strServicetax;
	}
	public void setStrServicetax(String strServicetax) {
		this.strServicetax = strServicetax;
	}
	public boolean getIsShowPassword() {
		return isShowPassword;
	}
	public void setIsShowPassword(boolean isShowPassword) {
		this.isShowPassword = isShowPassword;
	}
	public String getStrEmailHostPassword() {
		return strEmailHostPassword;
	}
	public void setStrEmailHostPassword(String strEmailHostPassword) {
		this.strEmailHostPassword = strEmailHostPassword;
	}
	public String getOrgCity() {
		return orgCity;
	}
	public void setOrgCity(String orgCity) {
		this.orgCity = orgCity;
	}
	public String getOrgCountry() {
		return orgCountry;
	}
	public void setOrgCountry(String orgCountry) {
		this.orgCountry = orgCountry;
	}
	public String getOrgEmailAddress() {
		return orgEmailAddress;
	}
	public void setOrgEmailAddress(String orgEmailAddress) {
		this.orgEmailAddress = orgEmailAddress;
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
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}


	public List<FillCurrency> getCurrencyList() {
		return currencyList;
	}


	public void setCurrencyList(List<FillCurrency> currencyList) {
		this.currencyList = currencyList;
	}


	public String getOrgCurrency() {
		return orgCurrency;
	}


	public void setOrgCurrency(String orgCurrency) {
		this.orgCurrency = orgCurrency;
	}


	public String getOrgDescription() {
		return orgDescription;
	}


	public void setOrgDescription(String orgDescription) {
		this.orgDescription = orgDescription;
	}


	public String getOrgContactNo() {
		return orgContactNo;
	}


	public void setOrgContactNo(String orgContactNo) {
		this.orgContactNo = orgContactNo;
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

	public String getPassTrackerPassword() {
		return passTrackerPassword;
	}

	public void setPassTrackerPassword(String passTrackerPassword) {
		this.passTrackerPassword = passTrackerPassword;
	}

	public String getTextTrackerPassword() {
		return textTrackerPassword;
	}

	public void setTextTrackerPassword(String textTrackerPassword) {
		this.textTrackerPassword = textTrackerPassword;
	}

	public boolean getIsRequiredAuthentication() {
		return isRequiredAuthentication;
	}

	public void setIsRequiredAuthentication(boolean isRequiredAuthentication) {
		this.isRequiredAuthentication = isRequiredAuthentication;
	}

	public String getStrEmailAuthUsername() {
		return strEmailAuthUsername;
	}

	public void setStrEmailAuthUsername(String strEmailAuthUsername) {
		this.strEmailAuthUsername = strEmailAuthUsername;
	}

	public String getStrEmailAuthPassword() {
		return strEmailAuthPassword;
	}

	public void setStrEmailAuthPassword(String strEmailAuthPassword) {
		this.strEmailAuthPassword = strEmailAuthPassword;
	}
	public String getSalaryStructure() {
		return salaryStructure;
	}

	public void setSalaryStructure(String salaryStructure) {
		this.salaryStructure = salaryStructure;
	}
	
	/*public boolean getIsReceipt() {
		return isReceipt;
	}

	public void setIsReceipt(boolean isReceipt) {
		this.isReceipt = isReceipt;
	}*/

	public boolean getIsHalfDayLeave() {
		return isHalfDayLeave;
	}

	public void setIsHalfDayLeave(boolean isHalfDayLeave) {
		this.isHalfDayLeave = isHalfDayLeave;
	}

	public String getRoundOffCondtion() {
		return roundOffCondtion;
	}

	public void setRoundOffCondtion(String roundOffCondtion) {
		this.roundOffCondtion = roundOffCondtion;
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

	public boolean getIsProductionLine() {
		return isProductionLine;
	}

	public void setIsProductionLine(boolean isProductionLine) {
		this.isProductionLine = isProductionLine;
	}

	public String getOrgLogoSmall() {
		return orgLogoSmall;
	}

	public void setOrgLogoSmall(String orgLogoSmall) {
		this.orgLogoSmall = orgLogoSmall;
	}	
	
	public boolean getIsSandwichAbsent() {
		return isSandwichAbsent;
	}

	public void setIsSandwichAbsent(boolean isSandwichAbsent) {
		this.isSandwichAbsent = isSandwichAbsent;
	}

	public boolean getIsTerminateWithoutFullAndFinal() {
		return isTerminateWithoutFullAndFinal;
	}

	public void setIsTerminateWithoutFullAndFinal(boolean isTerminateWithoutFullAndFinal) {
		this.isTerminateWithoutFullAndFinal = isTerminateWithoutFullAndFinal;
	}	
	public boolean getIsTDSAutoApprove() {
		return isTDSAutoApprove;
	}

	public void setIsTDSAutoApprove(boolean isTDSAutoApprove) {
		this.isTDSAutoApprove = isTDSAutoApprove;
	}

	public boolean getIsShowTimeVariance() {
		return isShowTimeVariance;
	}

	public void setIsShowTimeVariance(boolean isShowTimeVariance) {
		this.isShowTimeVariance = isShowTimeVariance;
	}	
}