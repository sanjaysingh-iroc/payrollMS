package com.konnect.jpms.reports.master;

import java.net.URLDecoder;
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

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPerkPaymentCycle;
import com.konnect.jpms.select.FillPerkType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PerkReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<FillPerkType> perkTypeList;
	List<FillPerkPaymentCycle> perkPaymentCycleList;
	private static Logger log = Logger.getLogger(PerkReport.class);
	CommonFunctions CF;
	HttpSession session;
	String strUserType;
	String strBaseUserTypeId;

	String strOrg;
	String financialYear;
	List<FillOrganisation> orgList;
	List<FillFinancialYears> financialYearList;
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, PPerk);
		request.setAttribute(TITLE, "Perk Policy");

		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
//		boolean isView = CF.getAccess(session, request, uF);
//		if (!isView) {
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN)) {
			orgList = new FillOrganisation(request).fillOrganisation((String) session.getAttribute(ORG_ACCESS));
			if (uF.parseToInt(getStrOrg()) == 0 && orgList != null && orgList.size() > 0) {
				setStrOrg(orgList.get(0).getOrgId());
			}
		} else {
			if (uF.parseToInt(getStrOrg()) == 0) {
				setStrOrg((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
		}

		viewPerk(uF);
		viewPerkWithSalary(uF);

		return loadPerk(uF);
	}

	private void viewPerkWithSalary(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			String[] strFinancialYear = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equalsIgnoreCase("NULL")) {
				String str = URLDecoder.decode(getFinancialYear());
				setFinancialYear(str);
				strFinancialYear = getFinancialYear().split("-");
				
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			} else {
				strFinancialYear = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYear[0] + "-" + strFinancialYear[1]);

				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}

			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);

			pst = con.prepareStatement("select * from salary_details where is_align_with_perk=true and level_id in (select level_id " +
					"from level_details where org_id=?) and (is_delete is null or is_delete=false)");
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			Map<String, List<Map<String, String>>> hmPerkAlign = new HashMap<String, List<Map<String,String>>>(); 
			while (rs.next()){
				List<Map<String, String>> alPerkAlign = hmPerkAlign.get(rs.getString("level_id"));
				if(alPerkAlign == null) alPerkAlign = new ArrayList<Map<String,String>>();
				
				Map<String, String> hmAlign = new HashMap<String, String>();
				hmAlign.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
				hmAlign.put("SALARY_HEAD_NAME", rs.getString("salary_head_name"));
				
				alPerkAlign.add(hmAlign);
				
				hmPerkAlign.put(rs.getString("level_id"),alPerkAlign);
			}
			rs.close();
			pst.close();
			
			
			Map<String, List<Map<String, String>>> hmPerkAlignSalary = new HashMap<String, List<Map<String, String>>>();
			pst = con.prepareStatement("SELECT * FROM perk_salary_details where org_id=? and financial_year_start=? and financial_year_end=?");
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {

				List<Map<String, String>> outerList = hmPerkAlignSalary.get(rs.getString("level_id")+"_"+rs.getString("salary_head_id"));
				if (outerList == null) outerList = new ArrayList<Map<String, String>>();

				Map<String, String> hmPerkSalary = new HashMap<String, String>();
				hmPerkSalary.put("PERK_SALARY_ID",rs.getString("perk_salary_id"));
				hmPerkSalary.put("PERK_CODE",uF.showData(rs.getString("perk_code"), ""));
				hmPerkSalary.put("PERK_NAME",uF.showData(rs.getString("perk_name"), ""));
				hmPerkSalary.put("PERK_DESCRIPTION",uF.showData(rs.getString("perk_description"), ""));
				hmPerkSalary.put("PERK_AMOUNT",uF.showData(rs.getString("amount"), ""));
				hmPerkSalary.put("PERK_USER",hmEmpName.get(rs.getString("user_id")));
				hmPerkSalary.put("ENTRY_DATE",uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmPerkSalary.put("FINANCIAL_YEAR",uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, CF.getStrReportDateFormat()) + " to "
						+ uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, CF.getStrReportDateFormat()));
				hmPerkSalary.put("PERK_ATTACHMENT",uF.showYesNo(rs.getString("is_attachment")));
				hmPerkSalary.put("PERK_IS_OPTIMAL",uF.showYesNo(rs.getString("is_optimal")));
				
				outerList.add(hmPerkSalary);

				hmPerkAlignSalary.put(rs.getString("level_id")+"_"+rs.getString("salary_head_id"), outerList);

			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmPerkAlign", hmPerkAlign);
			request.setAttribute("hmPerkAlignSalary", hmPerkAlignSalary);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadPerk(UtilityFunctions uF) {
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		getSelectedFilter(uF);
		return LOAD;
	}

	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
//		Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
//		Map<String, String> hmFeatureUserTypeId = (Map<String, String>) request.getAttribute("hmFeatureUserTypeId");
//		
//		System.out.println("hmFeatureStatus ====>> " + hmFeatureStatus);
//		System.out.println("hmFeatureUserTypeId ====>> " + hmFeatureUserTypeId);
		
		if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equalsIgnoreCase("NULL")) {
			String str = URLDecoder.decode(getFinancialYear());
			setFinancialYear(str);
			String[] strFinancialYear = getFinancialYear().split("-");
			alFilter.add("FINANCIALYEAR");
			hmFilter.put("FINANCIALYEAR", uF.getDateFormat(strFinancialYear[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strFinancialYear[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		
//		if(uF.parseToBoolean(hmFeatureStatus.get(F_LOGIN_USER_ORG_IN_FILTER)) && hmFeatureUserTypeId.get(F_LOGIN_USER_ORG_IN_FILTER).contains(strBaseUserTypeId)) {
			alFilter.add("ORGANISATION");
			if(getStrOrg() !=null) {
				String strOrg="";
				for(int i=0;orgList!=null && i<orgList.size();i++) {
					if(getStrOrg().equals(orgList.get(i).getOrgId())) {
						strOrg=orgList.get(i).getOrgName();
					}
				}
				if(strOrg!=null && !strOrg.equals("")) {
					hmFilter.put("ORGANISATION", strOrg);
				} else {
					hmFilter.put("ORGANISATION", "All Organisation");
				}
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
//		}
			
		String selectedFilter = CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	public void viewPerk(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			String[] strFinancialYear = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equalsIgnoreCase("NULL")) {
				String str = URLDecoder.decode(getFinancialYear());
				setFinancialYear(str);
				
				strFinancialYear = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			} else {
				strFinancialYear = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYear[0] + "-" + strFinancialYear[1]);

				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);

			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, Map<String, String>> hmLevelMap = new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement(selectLevel1);
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("LEVEL_CODE", rs.getString("level_code"));
				hmInner.put("LEVEL_NAME", rs.getString("level_name"));
				hmLevelMap.put(rs.getString("level_id"), hmInner);
			}
			rs.close();
			pst.close();

			Map<String, List<List<String>>> hmPerkReport = new HashMap<String, List<List<String>>>();
			// pst = con.prepareStatement(selectPerk);
			pst = con.prepareStatement("SELECT * FROM perk_details where org_id=? and financial_year_start=? and financial_year_end=?");
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {

				List<List<String>> outerList = hmPerkReport.get(rs.getString("level_id"));
				if (outerList == null) outerList = new ArrayList<List<String>>();

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getInt("perk_id") + "");
				innerList.add(uF.showData(rs.getString("perk_code"), ""));
				innerList.add(uF.showData(rs.getString("perk_name"), ""));
				innerList.add(uF.showData(rs.getString("perk_description"), ""));
				innerList.add(uF.charMappingForPerkType(rs.getString("perk_type")));
				innerList.add(uF.charMappingForPerkPaymentCycle(rs.getString("perk_payment_cycle")));
				innerList.add(uF.showData(rs.getString("max_amount"), ""));

				innerList.add("");
				// innerList.add("["+uF.showData(rs.getString("level_code"),
				// "not selected")+"] "+uF.showData(rs.getString("level_name"),""));
				innerList.add(hmEmpName.get(rs.getString("user_id")));
				innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, CF.getStrReportDateFormat()) + " to "
						+ uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, CF.getStrReportDateFormat()));

				outerList.add(innerList);

				hmPerkReport.put(rs.getString("level_id"), outerList);

			}
			rs.close();
			pst.close();
			request.setAttribute("hmPerkReport", hmPerkReport);
			request.setAttribute("hmLevelMap", hmLevelMap);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
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

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

}
