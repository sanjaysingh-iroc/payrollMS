package com.konnect.jpms.master;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillReimbursementCTCHead;
import com.konnect.jpms.tms.PayCycleList;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddExGratia extends ActionSupport implements ServletRequestAware, IStatements {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	 
	CommonFunctions CF = null; 

	String financialYear;
	String strOrg;
	String netProfit;
	String gratiaUpdate;
	String paycycle;
	
	List<FillFinancialYears> financialYearList;
	List<FillOrganisation> orgList;
	List<FillPayCycles> payCycleList;
	 
	String userscreen;
	String navigationId;
	String toPage;
	String toTab;
	
	public String execute() throws Exception {
		session = request.getSession();if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE); 
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/master/AddExGratia.jsp");
		request.setAttribute(TITLE, "Add Ex Gratia");
		
		UtilityFunctions uF = new UtilityFunctions();
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		
		if (operation!=null && operation.equals("D")) {
			return deleteExGratia(uF,strId);  
		}
		
		if(uF.parseToInt(getStrOrg()) == 0){
			setStrOrg((String)session.getAttribute(ORGID));
		}
		
		if(getGratiaUpdate()!=null){
			updateExGratia(uF);
			return SUCCESS;
		}
		
		viewGratia();
		
		return loadGratia();
	}
	
	private String deleteExGratia(UtilityFunctions uF, String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from EX_GRATIA_DETAILS where EX_GRATIA_ID=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			session.setAttribute(MESSAGE, SUCCESSM+"Deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	
	private String loadGratia() {
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN)) {
			orgList = new FillOrganisation(request).fillOrganisation((String) session.getAttribute(ORG_ACCESS));
		} else {
			orgList = new FillOrganisation(request).fillOrganisation();
		}
		UtilityFunctions uF = new UtilityFunctions();
		getEmpPaycycle(uF);
		//payCycleList = new FillPayCycles(request).fillCurrentNextPayCycleByOrg(CF.getStrTimeZone(), CF,getStrOrg());
//		if(getGratiaUpdate()!=null) {
//			return SUCCESS;
//		} else {
			return LOAD;
//		}
	}

	private void getEmpPaycycle(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equalsIgnoreCase("NULL")) {		
				String str = URLDecoder.decode(getFinancialYear());
				setFinancialYear(str);
				String[] strFinancialYear = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			con = db.makeConnection(con);
			
			String orgId = CF.getEmpOrgId(con, uF, strSessionEmpId);
			String levelId = CF.getEmpLevelId(con, strSessionEmpId);
			
			Calendar cal = GregorianCalendar.getInstance();
            cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "dd")));
            cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM")) - 1);
            cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			
	            List<Date> alDate = new ArrayList<Date>();
				for (int i = 1; i <= 12; i++){
					int nMonthStart = cal.getActualMinimum(Calendar.DATE);
					int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
					int nMonth = (cal.get(Calendar.MONTH) + 1);
					
					String strDateStart =  (nMonthStart <10 ? "0"+nMonthStart : nMonthStart)+"/"+(nMonth <10 ? "0"+nMonth : nMonth)+"/"+cal.get(Calendar.YEAR);
					
					alDate.add(uF.getDateFormat(strDateStart, DATE_FORMAT));
					
					cal.add(Calendar.MONTH, 1);
				}
				Collections.reverse(alDate);
				
		        Date date2 = uF.getCurrentDate(CF.getStrTimeZone());
		        payCycleList = new ArrayList<FillPayCycles>();
				for(Date ad : alDate){
					String strDateStart = uF.getDateFormat(""+ad, DBDATE, DATE_FORMAT);
					String[] strPayCycleDates = CF.getPayCycleFromDate(con, strDateStart, CF.getStrTimeZone(), CF, orgId);
					
					Date date1 = uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT);
					
					if(date1.before(date2) || date1.equals(date2)){						
							payCycleList.add(new FillPayCycles(strPayCycleDates[0]+"-"+strPayCycleDates[1]+"-"+strPayCycleDates[2], "Pay Cycle " + strPayCycleDates[2] + ", " + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat())));	
					} else {
						payCycleList.add(new FillPayCycles(strPayCycleDates[0]+"-"+strPayCycleDates[1]+"-"+strPayCycleDates[2], "Pay Cycle " + strPayCycleDates[2] + ", " + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat())));
					}
				}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	
	
	public void updateExGratia(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		Database db = new Database();
		db.setRequest(request);
		
	try {
		
		String []strFinancialDates = null;
		if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
			String str = URLDecoder.decode(getFinancialYear());
			setFinancialYear(str);
			strFinancialDates = getFinancialYear().split("-");
			setFinancialYear(strFinancialDates[0] + "-" + strFinancialDates[1]);
		} else {
			strFinancialDates = new FillFinancialYears(request).fillLatestFinancialYears();
			setFinancialYear(strFinancialDates[0] + "-" + strFinancialDates[1]);
		}
		
		
		con = db.makeConnection(con);
		
		String []strDate = null;
		if(getPaycycle()!=null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")){
			String str = URLDecoder.decode(getPaycycle());
			setPaycycle(str);
			strDate = getPaycycle().split("-");
		}else{
			strDate = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF,getStrOrg());
		}

		pst = con.prepareStatement("update EX_GRATIA_DETAILS set NET_PROFIT=?,ADDED_BY=?,ENTRY_DATE=? " +
				"where FINANCIAL_YEAR_FROM=? and FINANCIAL_YEAR_TO=? and PAYCYCLE_FROM=? and PAYCYCLE_TO=? and PAYCYCLE=? and ORG_ID=?");
		
		pst.setDouble(1, uF.parseToDouble(getNetProfit()));
		pst.setInt(2, uF.parseToInt(strSessionEmpId));
		pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
		pst.setDate(4, uF.getDateFormat(strFinancialDates[0], DATE_FORMAT));
		pst.setDate(5, uF.getDateFormat(strFinancialDates[1], DATE_FORMAT));
		pst.setDate(6, uF.getDateFormat(strDate[0], DATE_FORMAT));
		pst.setDate(7, uF.getDateFormat(strDate[1], DATE_FORMAT));
		pst.setInt(8, uF.parseToInt(strDate[2]));
		pst.setInt(9, uF.parseToInt(getStrOrg()));
		int nRow = pst.executeUpdate();
		pst.close(); 
		
		if(nRow==0){
			
			pst = con.prepareStatement("insert into EX_GRATIA_DETAILS (FINANCIAL_YEAR_FROM,FINANCIAL_YEAR_TO,NET_PROFIT,PAYCYCLE_FROM," +
					"PAYCYCLE_TO,PAYCYCLE,ADDED_BY,ENTRY_DATE,ORG_ID) values (?,?,?,?,?,?,?,?,?)");
			pst.setDate(1, uF.getDateFormat(strFinancialDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialDates[1], DATE_FORMAT));
			pst.setDouble(3, uF.parseToDouble(getNetProfit()));
			pst.setDate(4, uF.getDateFormat(strDate[0], DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strDate[1], DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(strDate[2]));
			pst.setInt(7, uF.parseToInt(strSessionEmpId));
			pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(9, uF.parseToInt(getStrOrg()));
			pst.execute();
			pst.close();
			
		}
		
		session.setAttribute(MESSAGE, SUCCESSM+"Ex Gratia saved successfully."+END);
		
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void viewGratia(){
		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			String []strFinancialDates = null;
			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
				String str = URLDecoder.decode(getFinancialYear());
				setFinancialYear(str);
				strFinancialDates = getFinancialYear().split("-");
				setFinancialYear(strFinancialDates[0] + "-" + strFinancialDates[1]);
			} else {
				strFinancialDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialDates[0] + "-" + strFinancialDates[1]);
			}			
			
			con = db.makeConnection(con);
			
			String []strDate = null;
			if(getPaycycle()!=null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")){
				String str = URLDecoder.decode(getPaycycle());
				setPaycycle(str);
				strDate = getPaycycle().split("-");
			}else{
				strDate = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF,getStrOrg());
			}
	
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			if(hmEmpCodeName==null) hmEmpCodeName = new HashMap<String, String>();
			
			Map<String, String> hmOrg = CF.getOrgName(con);
			
			pst = con.prepareStatement("select * from EX_GRATIA_DETAILS where FINANCIAL_YEAR_FROM =? and FINANCIAL_YEAR_TO =? and ORG_ID=? and PAYCYCLE_FROM=? and PAYCYCLE_TO=? and PAYCYCLE=?");
			pst.setDate(1, uF.getDateFormat(strFinancialDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrOrg()));
			pst.setDate(4, uF.getDateFormat(strDate[0], DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strDate[1], DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(strDate[2]));
			rs = pst.executeQuery(); 
			while(rs.next()){
				setFinancialYear(uF.getDateFormat(rs.getString("FINANCIAL_YEAR_FROM"), DBDATE, DATE_FORMAT)+"-"+uF.getDateFormat(rs.getString("FINANCIAL_YEAR_TO"), DBDATE, DATE_FORMAT));
				setStrOrg(rs.getString("ORG_ID"));
				setPaycycle(uF.getDateFormat(rs.getString("PAYCYCLE_FROM"), DBDATE, DATE_FORMAT)+"-"+uF.getDateFormat(rs.getString("PAYCYCLE_TO"), DBDATE, DATE_FORMAT)+"-"+rs.getString("PAYCYCLE"));
				setNetProfit(rs.getString("NET_PROFIT"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from EX_GRATIA_DETAILS where FINANCIAL_YEAR_FROM =? and FINANCIAL_YEAR_TO =? and ORG_ID=?");
			pst.setDate(1, uF.getDateFormat(strFinancialDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			List<Map<String, String>> gratiaList = new ArrayList<Map<String,String>>();
			while(rs.next()){
				Map<String,String> hmInner = new HashMap<String, String>();
				hmInner.put("EX_GRATIA_ID",rs.getString("EX_GRATIA_ID"));
				hmInner.put("FINANCIAL_YEAR_FROM",uF.getDateFormat(rs.getString("FINANCIAL_YEAR_FROM"), DBDATE, DATE_FORMAT));
				hmInner.put("FINANCIAL_YEAR_TO",uF.getDateFormat(rs.getString("FINANCIAL_YEAR_TO"), DBDATE, DATE_FORMAT));
				hmInner.put("NET_PROFIT",rs.getString("NET_PROFIT"));
				hmInner.put("PAYCYCLE_FROM",uF.getDateFormat(rs.getString("PAYCYCLE_FROM"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("PAYCYCLE_TO",uF.getDateFormat(rs.getString("PAYCYCLE_TO"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("PAYCYCLE",rs.getString("PAYCYCLE"));
				hmInner.put("ADDED_BY",uF.showData(hmEmpCodeName.get(rs.getString("ADDED_BY")), ""));
				hmInner.put("ENTRY_DATE",uF.getDateFormat(rs.getString("ENTRY_DATE"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("ORG_ID",rs.getString("ORG_ID"));
				hmInner.put("FINANCIAL_YEAR", financialYear);
				gratiaList.add(hmInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("gratiaList", gratiaList);
			request.setAttribute("hmOrg", hmOrg);
		
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

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getNetProfit() {
		return netProfit;
	}

	public void setNetProfit(String netProfit) {
		this.netProfit = netProfit;
	}

	public String getGratiaUpdate() {
		return gratiaUpdate;
	}

	public void setGratiaUpdate(String gratiaUpdate) {
		this.gratiaUpdate = gratiaUpdate;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public List<FillPayCycles> getPayCycleList() {
		return payCycleList;
	}

	public void setPayCycleList(List<FillPayCycles> payCycleList) {
		this.payCycleList = payCycleList;
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

	public String getToTab() {
		return toTab;
	}

	public void setToTab(String toTab) {
		this.toTab = toTab;
	}

}
