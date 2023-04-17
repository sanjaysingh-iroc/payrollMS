package com.konnect.jpms.reports;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.select.FillColour;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class HolidayReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**     
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType;
	String strSessionEmpId;
	CommonFunctions CF=null;
	List<FillDepartment> deptList;
	List<FillWLocation> wLocationList;
	List<FillColour> colourCodeList;
	 
	
	List<FillOrganisation> orgList;
	String strOrg;
	
	Map<String, String> hm_DeptId_DeptName;
	Map<String, String> hm_DeptId_WlocationName;
	private static Logger log = Logger.getLogger(HolidayReport.class);
	
	String calendarYear;
	List<FillCalendarYears> calendarYearList;  
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(uF.parseToInt(getStrOrg()) == 0  && orgList!=null && orgList.size()>0){
				setStrOrg(orgList.get(0).getOrgId());
			}
			wLocationList = new FillWLocation(request).fillWLocation(getStrOrg(), (String)session.getAttribute(WLOCATION_ACCESS));
		}else{
			if(uF.parseToInt(getStrOrg()) == 0){
				setStrOrg((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getStrOrg());
		}
		
		calendarYearList = new FillCalendarYears(request).fillCalendarYears(CF);
			
		if(strUserType!=null&& strUserType.equalsIgnoreCase(EMPLOYEE)) {
			request.setAttribute(PAGE, PReportHolidayE);
			request.setAttribute(TITLE, TViewHolidays);
			getHolidayReport(uF); //Need to change
			viewHolidayReport(uF);
		} else if(strUserType!=null) {
			request.setAttribute(PAGE, PReportHoliday);
			request.setAttribute(TITLE, TViewHolidays);
			
			getHolidayReport(uF);
		}
		
		getSelectedFilter(uF);
		
		return LOAD;

	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("CALENDARYEAR");
		if(getCalendarYear()!=null) {
			String str = URLDecoder.decode(getCalendarYear());
			setCalendarYear(str);
			String strCal = "";
			for(int i=0;calendarYearList!=null && i<calendarYearList.size();i++) {
				if(getCalendarYear().equals(calendarYearList.get(i).getCalendarYearId())) {
					strCal = calendarYearList.get(i).getCalendarYearName();
				}
			}
			if(strCal!=null && !strCal.equals("")) {
				hmFilter.put("CALENDARYEAR", strCal);
			} else {
				hmFilter.put("CALENDARYEAR", "-");
			}
		} else {
			hmFilter.put("CALENDARYEAR", "-");
		}
		
		alFilter.add("ORGANISATION");
		if(getStrOrg()!=null) {
			String strOrg="";
			for(int i=0;orgList!=null && i<orgList.size();i++) {
				if(getStrOrg().equals(orgList.get(i).getOrgId())) {
					strOrg=orgList.get(i).getOrgName();
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organizations");
			}
		} else {
			hmFilter.put("ORGANISATION", "All Organizations");
		}
		
		String selectedFilter = CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	
	private void getHolidayReport(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;

			System.out.println("getCalendarYear() ===>> " + getCalendarYear());
			if (getCalendarYear() != null && !getCalendarYear().equals("") && !getCalendarYear().equals("null")) {
				
				String str = URLDecoder.decode(getCalendarYear());
				setCalendarYear(str);
				strCalendarYearDates = getCalendarYear().split("-");
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			} else {
				strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
				setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);

				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			}
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			Map<String,List<Map<String,String>>> hmHolidayList = new HashMap<String,List<Map<String,String>>>();
			Map<String,List<Map<String,String>>> hmOptionalHolidayList = new HashMap<String,List<Map<String,String>>>();
//			pst = con.prepareStatement(selectHolidaysR3);
//			pst = con.prepareStatement("SELECT * FROM holidays where org_id=? and _date between ? and ? order by _date desc ");
//			pst.setInt(1, uF.parseToInt(getStrOrg()));
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("SELECT * FROM holidays where _date between ? and ? ");
			if(uF.parseToInt(getStrOrg())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getStrOrg()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(uF.parseToBoolean(rs.getString("is_optional_holiday"))){
					List<Map<String,String>> outerList = hmOptionalHolidayList.get(rs.getString("wlocation_id"));
					if(outerList==null)outerList = new ArrayList<Map<String,String>>();
					
					Map<String,String> hmInner = new HashMap<String, String>();
					hmInner.put("HOLIDAY_ID",rs.getString("holiday_id"));
					hmInner.put("HOLIDAY_DATE",uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
					hmInner.put("HOLIDAY_YEAR",rs.getString("_year"));
					hmInner.put("HOLIDAY_DESCRIPTION",rs.getString("description"));
					hmInner.put("COLOUR_CODE",rs.getString("colour_code"));
					hmInner.put("WLOCATION_ID",rs.getString("wlocation_id"));
					hmInner.put("ORG_ID",rs.getString("org_id"));
					
					String holiday_type="Full Day";
					if(rs.getString("holiday_type").equals("HD")){
						holiday_type="Half Day";
					}
					hmInner.put("HOLIDAY_TYPE",holiday_type);
					
					outerList.add(hmInner);
					
					hmOptionalHolidayList.put(rs.getString("wlocation_id"), outerList);
				} else {
					List<Map<String,String>> outerList = hmHolidayList.get(rs.getString("wlocation_id"));
					if(outerList==null)outerList = new ArrayList<Map<String,String>>();
					
					Map<String,String> hmInner = new HashMap<String, String>();
					hmInner.put("HOLIDAY_ID",rs.getString("holiday_id"));
					hmInner.put("HOLIDAY_DATE",uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
					hmInner.put("HOLIDAY_YEAR",rs.getString("_year"));
					hmInner.put("HOLIDAY_DESCRIPTION",rs.getString("description"));
					hmInner.put("COLOUR_CODE",rs.getString("colour_code"));
					hmInner.put("WLOCATION_ID",rs.getString("wlocation_id"));
					hmInner.put("ORG_ID",rs.getString("org_id"));
					
					String holiday_type="Full Day";
					if(rs.getString("holiday_type")!= null && rs.getString("holiday_type").equals("HD")){
						holiday_type="Half Day";
					}
					hmInner.put("HOLIDAY_TYPE",holiday_type);
					
					outerList.add(hmInner);
					
					hmHolidayList.put(rs.getString("wlocation_id"), outerList);
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmHolidayList", hmHolidayList);
			request.setAttribute("hmOptionalHolidayList", hmOptionalHolidayList);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}


	public String viewHolidayReport(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			if(session==null) {
				session = request.getSession();
				CF = (CommonFunctions)session.getAttribute(CommonFunctions);
			}

			Calendar cal = GregorianCalendar.getInstance();
	        String strDate = (cal.getActualMinimum(Calendar.DATE) < 10 ? "0"+cal.getActualMinimum(Calendar.DATE) : cal.getActualMinimum(Calendar.DATE)) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
	        String strendDate = (cal.getActualMaximum(Calendar.DATE) < 10 ? "0"+cal.getActualMaximum(Calendar.DATE) : cal.getActualMaximum(Calendar.DATE)) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
	        
	        Date startDate = uF.getDateFormat(strDate, DATE_FORMAT);
	        Date endDate = uF.getDateFormat(strendDate, DATE_FORMAT);
			
			
			pst = con.prepareStatement(selectHolidaysE);
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			List<String> holidayList = new ArrayList<String>();
			Map<String, Map<String, String>> hmHolidayDates = new LinkedHashMap<String, Map<String,String>>();
			rs = pst.executeQuery();
			while(rs.next()) {
				holidayList.add("{color:'"+uF.showData(rs.getString("colour_code"), "")+"',title: '"+uF.strDecoding(rs.getString("description"))+".'," +
						"start: new Date("+uF.getDateFormat(rs.getString("_date"), DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(rs.getString("_date"), DBDATE, "M"))-1)+", "+uF.getDateFormat(rs.getString("_date"), DBDATE, "dd")+")}");
				
				if(startDate.before(rs.getDate("_date")) && endDate.after(rs.getDate("_date"))){
					Map<String, String> hmInner = new LinkedHashMap<String, String>();
					hmInner.put("HOLIDAY", uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+" "+uF.strDecoding(rs.getString("description")));
					hmInner.put("HLIDAY_COLOR", uF.showData(rs.getString("colour_code"), ""));
					
					hmHolidayDates.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), hmInner);
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("holidayList", holidayList);
			request.setAttribute("hmHolidayDates", hmHolidayDates);
			
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

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public String getCalendarYear() {
		return calendarYear;
	}

	public void setCalendarYear(String calendarYear) {
		this.calendarYear = calendarYear;
	}

	public List<FillCalendarYears> getCalendarYearList() {
		return calendarYearList;
	}

	public void setCalendarYearList(List<FillCalendarYears> calendarYearList) {
		this.calendarYearList = calendarYearList;
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
