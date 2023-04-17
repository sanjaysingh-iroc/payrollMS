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

public class StatutoryIDAndRegistrationInfoReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	List<FillCountry> countryList;
	List<FillState> stateList;
	List<FillTimezones> timezoneList;
	List<FillWlocationType> wlocationTypeList;
	List<FillWeekDays> weeklyOffList;
//	List<FillCity> cityList; 
	List<FillBank> bankList;
	List<FillCurrency> currencyList;
	 
	HttpSession session; 
	CommonFunctions CF;
	String strUserType;
	
	private static Logger log = Logger.getLogger(StatutoryIDAndRegistrationInfoReport.class);
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		strUserType = (String) session.getAttribute(USERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, PReportStatutaryIdAndRegInfo);
		request.setAttribute(TITLE, TReportStatutaryIdAndRegInfo);
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		viewWLocation();			
		return loadWLocation(uF);
	}
	
	public String loadWLocation(UtilityFunctions uF) {	
		
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
			
//			pst = con.prepareStatement("select * from org_details");
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from org_details where org_id>0 ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && strOrgAccess != null && !strOrgAccess.trim().equals("") && !strOrgAccess.trim().equalsIgnoreCase("NULL")){
				sbQuery.append(" and org_id in( "+strOrgAccess+") ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while(rs.next()){
				
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("org_id"));
				alInner.add(rs.getString("org_code")); 
				alInner.add(rs.getString("org_name"));
				alInner.add(CF.getStrDocRetriveLocation()+rs.getString("org_logo"));
				alInner.add(rs.getString("org_name"));
				
				hmOrganistaionMap.put(rs.getString("org_id"), alInner); 
				
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from work_location_type");
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
			
			
//			pst = con.prepareStatement("SELECT * FROM( SELECT *,wt.wlocation_type_id as type_id FROM(SELECT * FROM ( SELECT * FROM ( SELECT * FROM work_location_info wl, timezones tz WHERE tz.timezone_id=wl.timezone_id ) wl left join state s on wl.wlocation_state_id=s.state_id ) wl left join country co on wl.wlocation_country_id=co.country_id ) awt LEFT JOIN work_location_type wt ON awt.wlocation_type_id = wt.wlocation_type_id order by wlocation_name) a left join bank_details bd on bd.bank_id=a.wlocation_bank_id order by a.type_id");
			sbQuery = new StringBuilder();
			sbQuery.append("SELECT * FROM ( SELECT *,wt.wlocation_type_id as type_id  FROM (SELECT * FROM ( SELECT * FROM (SELECT * " +
					"FROM work_location_info wl, timezones tz  WHERE tz.timezone_id=wl.timezone_id ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && strOrgAccess != null && !strOrgAccess.trim().equals("") && !strOrgAccess.trim().equalsIgnoreCase("NULL")){
				sbQuery.append(" and wl.org_id in ( "+strOrgAccess+") ");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && strWLocationAccess != null && !strWLocationAccess.trim().equals("") && !strWLocationAccess.trim().equalsIgnoreCase("NULL")){
				sbQuery.append(" and wl.wlocation_id in ("+strWLocationAccess+")");
			}
			sbQuery.append(") wl " +
					"left join state s on wl.wlocation_state_id=s.state_id ) wl " +
					"left join country co on wl.wlocation_country_id=co.country_id ) awt " +
					"LEFT JOIN work_location_type wt ON awt.wlocation_type_id = wt.wlocation_type_id order by wlocation_name) a " +
					"left join bank_details bd on bd.bank_id=a.wlocation_bank_id order by a.type_id");
			pst = con.prepareStatement(sbQuery.toString());
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
			
			pst = con.prepareStatement("select count(*) as count from org_details");
			rs = pst.executeQuery();
			int count = 0;
			while(rs.next()){
				count = rs.getInt("count");
			}
			rs.close();
			pst.close();
			
			
//			if(count>=uF.parseToInt(CF.getStrMaxOrganisation())){
//				session.setAttribute(MESSAGE, ERRORM+"You have reached maximum organisation limit. Please contact <a target=\"_blank\" href=\"http://dailyhrz.com/\">Dailyhrz </a> to increase you organisations limit."+END);
//			}else{
//				pst = con.prepareStatement("select count(*) as count from work_location_info");
//				rs = pst.executeQuery();
//				count = 0;
//				while(rs.next()){
//					count = rs.getInt("count");
//				}
//				
//				if(count>=uF.parseToInt(CF.getStrMaxLocations())){
//					session.setAttribute(MESSAGE, ERRORM+"You have reached maximum work location limit. Please contact <a target=\"_blank\" href=\"http://dailyhrz.com/\">Dailyhrz </a> to increase you work location limit."+END);
//				}
//			}
			
			request.setAttribute("hmOfficeLocationMap", hmOfficeLocationMap);
			request.setAttribute("hmOfficeTypeMap", hmOfficeTypeMap);
			request.setAttribute("hmEmpCount", hmEmpCount);
			request.setAttribute("hmOrgEmpCount", hmOrgEmpCount);
			request.setAttribute("hmOrganistaionMap", hmOrganistaionMap);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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