package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillBank;
import com.konnect.jpms.select.FillCountry;
import com.konnect.jpms.select.FillCurrency;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.select.FillTimezones;
import com.konnect.jpms.select.FillWeekDays;
import com.konnect.jpms.select.FillWlocationType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class WLocationReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	HttpSession session; 
	CommonFunctions CF;
	String strSessionOrgId;
	String strUsertypeId;
	String strUserType;
	String strBaseUserType;
	
	private List<FillCountry> countryList;
	private List<FillState> stateList;
	private List<FillTimezones> timezoneList;
	private List<FillWlocationType> wlocationTypeList;
	private List<FillWeekDays> weeklyOffList;
//	List<FillCity> cityList; 
	private List<FillBank> bankList;
	private List<FillCurrency> currencyList;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	private static Logger log = Logger.getLogger(WLocationReport.class);
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, PReportWLocation);
		request.setAttribute(TITLE, TViewWLocation);
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strSessionOrgId = (String) session.getAttribute(ORGID);
		strUsertypeId = (String) session.getAttribute(BASEUSERTYPEID);
		
		viewWLocation();			
		return loadWLocation(uF);
	}
	
	public String loadWLocation(UtilityFunctions uF){	
		
		
		countryList = new FillCountry(request).fillCountry();
		stateList = new FillState(request).fillState();
		timezoneList = new FillTimezones(request).fillTimezones();
		wlocationTypeList = new FillWlocationType(request).fillWlocationType();
		weeklyOffList = new FillWeekDays().fillWeekDays();
		bankList = new FillBank(request).fillBankCode();
		currencyList= new FillCurrency(request).fillCurrency();
		
//		cityList = new FillCity().fillCity();
		request.setAttribute("countryList", countryList);
		request.setAttribute("stateList", stateList);
		request.setAttribute("timezoneList", timezoneList);
		request.setAttribute("wlocationTypeList", wlocationTypeList);
		request.setAttribute("weeklyOffList", weeklyOffList);
		request.setAttribute("bankList", bankList);
		request.setAttribute("currencyList", currencyList);
		
		
//		request.setAttribute("cityList", cityList);
		
		int i;
		
		if(countryList.size()!=0) {
			int countryId;
			String countryName;
			StringBuilder sbCountryList = new StringBuilder();
			sbCountryList.append("{");
			for(i=0; i<countryList.size()-1;i++ ) {
	    		countryId = uF.parseToInt((countryList.get(i)).getCountryId());
	    		countryName = countryList.get(i).getCountryName();
	    		sbCountryList.append("\""+ countryId+"\":\""+countryName+"\",");
			}
			countryId = uF.parseToInt((countryList.get(i)).getCountryId());
			countryName = countryList.get(i).getCountryName();
			sbCountryList.append("\""+ countryId+"\":\""+countryName+"\"");	//no comma for last record
			sbCountryList.append("}");
			request.setAttribute("sbCountryList", sbCountryList.toString());
		}
		
		if(stateList.size()!=0) {
			int stateId;
			String stateName;
			StringBuilder sbStateList = new StringBuilder();
			sbStateList.append("{");
		    for(i=0; i<stateList.size()-1;i++ ) {
		    		stateId = uF.parseToInt(stateList.get(i).getStateId());
		    		stateName = stateList.get(i).getStateName();
		    		sbStateList.append("\""+ stateId+"\":\""+stateName+"\",");
		    }
		    stateId = uF.parseToInt(stateList.get(i).getStateId());
			stateName = stateList.get(i).getStateName();
			sbStateList.append("\""+ stateId+"\":\""+stateName+"\"");	
		    sbStateList.append("}");
		    request.setAttribute("sbStateList", sbStateList.toString());
//		    log.debug("sbStateList.toString====>>"+sbStateList.toString());
		}
	    
		/*if(cityList.size()!=0) {
			int cityId;
			String cityName;
			StringBuilder sbCityList = new StringBuilder();
			sbCityList.append("{");
			    for(i=0; i<cityList.size()-1;i++ ) {
			    		cityId = Integer.parseInt((cityList.get(i)).getCityId());
			    		cityName = cityList.get(i).getCityName();
			    		sbCityList.append("\""+ cityId+"\":\""+cityName+"\",");
			    }
			    cityId = Integer.parseInt((cityList.get(i)).getCityId());
	 		cityName = cityList.get(i).getCityId();
	 		sbCityList.append("\""+ cityId+"\":\""+cityName+"\"");	
		    sbCityList.append("}");
		    request.setAttribute("sbCityList", sbCityList.toString());
		}*/
	    
		if(timezoneList.size()!=0) {
			int timezoneId;
			String timezoneName;
			StringBuilder sbTimezoneList = new StringBuilder();
			sbTimezoneList.append("{");
		    for(i=0; i<timezoneList.size()-1;i++ ) {
		    	timezoneId = uF.parseToInt(timezoneList.get(i).getTimezoneId());
		    	timezoneName = timezoneList.get(i).getTimezoneName();
		    	sbTimezoneList.append("\""+ timezoneId+"\":\""+timezoneName+"\",");
		    }
		    timezoneId = uF.parseToInt(timezoneList.get(i).getTimezoneId());
		    timezoneName = timezoneList.get(i).getTimezoneName();
		    sbTimezoneList.append("\""+ timezoneId+"\":\""+timezoneName+"\"}");	
		    request.setAttribute("sbTimezoneList", sbTimezoneList.toString());
//		    log.debug("sbtimezoneList.toString====>>"+sbTimezoneList.toString());
		}
		
		if(wlocationTypeList.size()!=0) {
			int wlocationTypeId;
			String wlocationTypeName;
			StringBuilder sbWlocationTypeList = new StringBuilder();
			sbWlocationTypeList.append("{");
		    for(i=0; i<wlocationTypeList.size()-1;i++ ) {
		    		wlocationTypeId = uF.parseToInt(wlocationTypeList.get(i).getWlocationTypeId());
		    		wlocationTypeName = wlocationTypeList.get(i).getWlocationTypeCodeName();
		    		sbWlocationTypeList.append("\""+ wlocationTypeId+"\":\""+wlocationTypeName+"\",");
		    }
		    wlocationTypeId = uF.parseToInt(wlocationTypeList.get(i).getWlocationTypeId());
    		wlocationTypeName = wlocationTypeList.get(i).getWlocationTypeCodeName();
    		sbWlocationTypeList.append("\""+ wlocationTypeId+"\":\""+wlocationTypeName+"\"");
    		sbWlocationTypeList.append("}");
		    request.setAttribute("sbWlocationTypeList", sbWlocationTypeList.toString());
//		    log.debug("sbWlocationTypeList====>>"+sbWlocationTypeList.toString());
		}
		
		if(weeklyOffList.size()!=0) {
			String weekDayId;
			String weekDayName;
			StringBuilder sbWeeklyOffList = new StringBuilder();
			sbWeeklyOffList.append("{");
		    for(i=0; i<weeklyOffList.size()-1;i++ ) {
		    	weekDayId = weeklyOffList.get(i).getWeekDayId();
		    	weekDayName = weeklyOffList.get(i).getWeekDayName();
		    	sbWeeklyOffList.append("\""+ weekDayId+"\":\""+weekDayName+"\",");
		    }
		    weekDayId = weeklyOffList.get(i).getWeekDayId();
	    	weekDayName = weeklyOffList.get(i).getWeekDayName();
	    	sbWeeklyOffList.append("\""+ weekDayId+"\":\""+weekDayName+"\",");
		    sbWeeklyOffList.append("}");
		    request.setAttribute("sbWeeklyOffList", sbWeeklyOffList.toString());
		    log.debug("sbWeeklyOffList====>>"+sbWeeklyOffList.toString());
		}
		
		if(bankList.size()!=0) {
			String bankId;
			String bankCode;
			String bankName;
			StringBuilder sbBankList = new StringBuilder();
			sbBankList.append("{");
		    for(i=0; i<bankList.size()-1;i++ ) {
		    	bankId = bankList.get(i).getBankId();
		    	bankCode = bankList.get(i).getBankCode();
		    	bankName = bankList.get(i).getBankName();
		    	sbBankList.append("\""+ bankId+"\":\""+bankCode+"\",");
		    }
		    bankId = bankList.get(i).getBankId();
	    	bankCode = bankList.get(i).getBankCode();
	    	bankName = bankList.get(i).getBankName();
	    	sbBankList.append("\""+ bankId+"\":\""+bankCode+"\"");
	    	sbBankList.append("}");
		    request.setAttribute("sbBankList", sbBankList.toString());
		}
		
		if(currencyList.size()!=0) {
			String currencyId;			
			String currencyName;
			StringBuilder sbcurrencyList = new StringBuilder();
			sbcurrencyList.append("{");
		    for(i=0; i<currencyList.size()-1;i++ ) {
		    	currencyId = currencyList.get(i).getCurrencyId();
		    	currencyName = currencyList.get(i).getCurrencyName();
		    	sbcurrencyList.append("\""+ currencyId+"\":\""+currencyName+"\",");
		    	
		    }
		    currencyId = currencyList.get(i).getCurrencyId();
	    	currencyName = currencyList.get(i).getCurrencyName();
	    	sbcurrencyList.append("\""+ currencyId+"\":\""+currencyName+"\"");
	    	sbcurrencyList.append("}");
		    request.setAttribute("sbcurrencyList", sbcurrencyList.toString());
		}
		
		
		StringBuilder sbIsMetro = new StringBuilder();
		sbIsMetro.append("{");
		sbIsMetro.append("\"t\":\"Yes\",");
		sbIsMetro.append("\"f\":\"No\"}");
		request.setAttribute("sbIsMetro", sbIsMetro.toString());
		
		
		return LOAD;
	}
	
	public String viewWLocation(){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			String strOrgAccess = (String)session.getAttribute(ORG_ACCESS);
			String strWLocationAccess = (String)session.getAttribute(WLOCATION_ACCESS);
			
			List<String> alInner = new ArrayList<String>();
			Map<String, List<String>> hmOrganistaionMap = new HashMap<String, List<String>>();
			Map<String, List<String>> hmOfficeTypeMap = new HashMap<String, List<String>>();
			Map<String, List<String>> hmOfficeLocationMap = new HashMap<String, List<String>>();
			 
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from org_details where org_id>0 ");
//			if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_ONLY_LOGIN_USER_ORG)) && hmFeatureUserTypeId.get(IConstants.F_SHOW_ONLY_LOGIN_USER_ORG).contains(strUsertypeId)) {
//				sbQuery.append(" and org_id = "+uF.parseToInt(strSessionOrgId)+" ");
//			}
			if(strBaseUserType!=null && !strBaseUserType.equalsIgnoreCase(ADMIN) && strOrgAccess != null && !strOrgAccess.trim().equals("") && !strOrgAccess.trim().equalsIgnoreCase("NULL")){
				sbQuery.append(" and org_id in( "+strOrgAccess+") ");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst wlocationReport==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("org_id"));//0
				alInner.add(rs.getString("org_code")); //1
				alInner.add(rs.getString("org_name"));//2

				String fileName = "";
				if(rs.getString("org_logo") != null && !rs.getString("org_logo").equals("")) {
					if(CF.getStrDocSaveLocation()==null) {
						fileName = DOCUMENT_LOCATION+rs.getString("org_logo");
					} else {
						fileName = CF.getStrDocRetriveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+rs.getString("org_logo");
					}			
				}
				alInner.add(fileName);//3
				alInner.add(rs.getString("org_name"));//4
				
				String fileName1 = "";
				if(rs.getString("org_logo_small") != null && !rs.getString("org_logo_small").equals("")) {
					if(CF.getStrDocSaveLocation()==null) {
						fileName1 = DOCUMENT_LOCATION+rs.getString("org_logo_small");
					} else {
						fileName1 = CF.getStrDocRetriveLocation()+I_ORGANISATION+"/"+I_IMAGE_SMALL+"/"+rs.getString("org_logo_small");
					}		
				}
				alInner.add(fileName1);//5
				
				hmOrganistaionMap.put(rs.getString("org_id"), alInner); 
				
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmOrganistaionMap==>"+hmOrganistaionMap);
			
			pst = con.prepareStatement("select * from work_location_type order by org_id");
			rs = pst.executeQuery();
			String strOrgIdOld = null;
			String strOrgIdNew = null;
			while(rs.next()){
				
				strOrgIdNew = rs.getString("org_id");
				
				if(strOrgIdNew!=null && !strOrgIdNew.equalsIgnoreCase(strOrgIdOld)){
					alInner = new ArrayList<String>();
				}
				alInner.add(rs.getString("wlocation_type_id"));
				alInner.add(rs.getString("wlocation_type_code"));
				alInner.add(rs.getString("wlocation_type_name"));
				alInner.add(rs.getString("wlocation_type_name"));
				
				hmOfficeTypeMap.put(rs.getString("org_id"), alInner);
				
				strOrgIdOld = strOrgIdNew;
				
			}
			rs.close();
			pst.close();
//			System.out.println("hmOfficeTypeMap ===>> " + hmOfficeTypeMap);
			
			sbQuery = new StringBuilder();
			sbQuery.append("SELECT * FROM ( SELECT *,wt.wlocation_type_id as type_id  FROM (SELECT * FROM ( SELECT * FROM (SELECT * " +
					"FROM work_location_info wl, timezones tz  WHERE tz.timezone_id=wl.timezone_id ");
			if(strBaseUserType!=null && !strBaseUserType.equalsIgnoreCase(ADMIN) && strOrgAccess != null && !strOrgAccess.trim().equals("") && !strOrgAccess.trim().equalsIgnoreCase("NULL")){
				sbQuery.append(" and wl.org_id in ( "+strOrgAccess+") ");
			}
			if(strBaseUserType!=null && !strBaseUserType.equalsIgnoreCase(ADMIN) && strWLocationAccess != null && !strWLocationAccess.trim().equals("") && !strWLocationAccess.trim().equalsIgnoreCase("NULL")){
				sbQuery.append(" and wl.wlocation_id in ("+strWLocationAccess+")");
			}
			sbQuery.append(") wl " +
					"left join state s on wl.wlocation_state_id=s.state_id ) wl " +
					"left join country co on wl.wlocation_country_id=co.country_id ) awt " +
					"LEFT JOIN work_location_type wt ON awt.wlocation_type_id = wt.wlocation_type_id order by wlocation_name) a " +
					"left join bank_details bd on bd.bank_id=a.wlocation_bank_id order by a.type_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
//			pst = con.prepareStatement("SELECT * FROM( SELECT *,wt.wlocation_type_id as type_id  FROM(SELECT * FROM ( SELECT * FROM ( SELECT * FROM work_location_info wl, timezones tz  WHERE tz.timezone_id=wl.timezone_id ) wl left join state s on wl.wlocation_state_id=s.state_id ) wl left join country co on wl.wlocation_country_id=co.country_id ) awt LEFT JOIN work_location_type wt ON awt.wlocation_type_id = wt.wlocation_type_id order by wlocation_name) a left join bank_details bd on bd.bank_id=a.wlocation_bank_id order by a.type_id");
			rs = pst.executeQuery();
			String strWLocationIdOld = null;
			String strWLocationNew = null;
			while(rs.next()){
				strWLocationNew = rs.getString("wlocation_type_id");
				if(strWLocationNew!=null && !strWLocationNew.equalsIgnoreCase(strWLocationIdOld)){
					alInner = new ArrayList<String>();
				}
				
				
				alInner.add(rs.getString("wlocation_id"));
				alInner.add(rs.getString("wlocation_name"));
				alInner.add(rs.getString("state_name"));
				
				alInner.add(rs.getString("country_name"));
				alInner.add(rs.getString("wlocation_pincode"));
				alInner.add(rs.getString("wlocation_contactno"));
				alInner.add(rs.getString("wlocation_faxno"));
				alInner.add(rs.getString("wlocation_city"));
				alInner.add(rs.getString("timezone_id"));
				
				alInner.add(rs.getString("wlocation_email"));
				alInner.add(rs.getString("wloacation_code"));
				alInner.add(rs.getString("wlocation_address"));
				alInner.add(rs.getString("wlocation_start_time"));
				alInner.add(rs.getString("wlocation_end_time"));
				alInner.add(rs.getString("ismetro"));
				
				alInner.add(rs.getString("wlocation_weeklyoff1"));
				alInner.add(rs.getString("wlocation_weeklyofftype1"));
				alInner.add(rs.getString("wlocation_weeklyoff2"));
				alInner.add(rs.getString("wlocation_weeklyofftype2"));
				alInner.add(rs.getString("currency_id"));
				
				hmOfficeLocationMap.put(rs.getString("wlocation_type_id"), alInner);
				
				strWLocationIdOld = strWLocationNew;
			}
			rs.close();
			pst.close();
//			System.out.println("hmOfficeLocationMap ===>> " + hmOfficeLocationMap);
			
			pst = con.prepareStatement("select count(*) as count, wlocation_id,org_id  from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and epd.is_alive=true and eod.emp_id >0 group by wlocation_id,org_id ");
			rs = pst.executeQuery();
			Map<String, String> hmEmpCount = new HashMap<String, String>();
			Map<String, String> hmOrgEmpCount = new HashMap<String, String>();
			while(rs.next()) {
				hmEmpCount.put(rs.getString("wlocation_id"), rs.getString("count"));
				int empCount = uF.parseToInt(hmOrgEmpCount.get(rs.getString("org_id")));
				empCount += uF.parseToInt(rs.getString("count"));
				hmOrgEmpCount.put(rs.getString("org_id"), ""+empCount);			
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(*) as cnt,wlocation_type_id  from work_location_info group by wlocation_type_id");
			rs = pst.executeQuery();
			Map<String, String> hmOfficeTypeWlocCount = new HashMap<String, String>();
			while(rs.next()) {
				hmOfficeTypeWlocCount.put(rs.getString("wlocation_type_id"), rs.getString("cnt"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(*) as count from org_details");
			rs = pst.executeQuery();
			int count = 0;
			while(rs.next()){
				count = rs.getInt("count");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(*) as count from work_location_info");
			rs = pst.executeQuery();
			int nLocCount = 0;
			while(rs.next()){
				nLocCount = rs.getInt("count");
			}
			rs.close();
			pst.close();
			
			StringBuilder sbLimit = new StringBuilder();
			boolean isOrgLimit = false;
			boolean isLocLimit = false;
			if(count>=uF.parseToInt(CF.getStrMaxOrganisation())){
//				session.setAttribute(MESSAGE, ERRORM+"You have reached maximum organisation limit. Please contact <a target=\"_blank\" href=\"http://dailyhrz.com/\">Dailyhrz </a> to increase you organisations limit."+END);
//				session.setAttribute(MESSAGE, ERRORM+"You have reached maximum organisation limit. Please contact the support team to increase your organisations limit."+END);
				sbLimit.append(ERRORM+"You have reached maximum organisation limit. Please contact the support team to increase your organisations limit."+END);
				isOrgLimit = true;
			}
			if(nLocCount>=uF.parseToInt(CF.getStrMaxLocations())){
//				session.setAttribute(MESSAGE, ERRORM+"You have reached maximum work location limit. Please contact the support team to increase your work location limit."+END);
				sbLimit.append("<br/>");
				sbLimit.append(ERRORM+"You have reached maximum work location limit. Please contact the support team to increase your work location limit."+END);
				isLocLimit = true;
			}
			request.setAttribute(MESSAGE, sbLimit.toString());
			
			request.setAttribute("hmOfficeLocationMap", hmOfficeLocationMap);
			request.setAttribute("hmOfficeTypeMap", hmOfficeTypeMap);
			request.setAttribute("hmEmpCount", hmEmpCount);
			request.setAttribute("hmOrgEmpCount", hmOrgEmpCount);
			request.setAttribute("hmOrganistaionMap", hmOrganistaionMap);
			
			request.setAttribute("isOrgLimit", ""+isOrgLimit);
			request.setAttribute("isLocLimit", ""+isLocLimit);
			request.setAttribute("hmOfficeTypeWlocCount", hmOfficeTypeWlocCount);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
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

	public List<FillCurrency> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(List<FillCurrency> currencyList) {
		this.currencyList = currencyList;
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