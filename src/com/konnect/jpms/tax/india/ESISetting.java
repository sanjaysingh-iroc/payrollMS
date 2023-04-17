package com.konnect.jpms.tax.india;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ESISetting extends ActionSupport implements ServletRequestAware, IStatements {
	 
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		HttpSession session;
		String strUserType = null;
		String strSessionEmpId = null;
		
		CommonFunctions CF = null; 
		
		String strOrg;		
		List<FillOrganisation> orgList;
		List<FillFinancialYears> financialYearList;
		String financialYear;
		
		List<FillSalaryHeads> salaryHeadList;
		List<FillState> stateList;
		String[] state;
		String[] strSalaryHeadId;
		String eesiContribution;
		String ersiContribution;
		String esiMaxLimit;
		String esiUpdate;
		
		String[] salaryHeadEligible;
		List<FillSalaryHeads> salaryHeadEligibleList;
		
		String[] level;
		List<FillLevel> levelList;		
		 
		public String execute() throws Exception {
			session = request.getSession();
			if(session==null)return LOGIN;
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if(CF==null)return LOGIN;
			
			strUserType = (String) session.getAttribute(USERTYPE); 
			strSessionEmpId = (String) session.getAttribute(EMPID);
			UtilityFunctions uF = new UtilityFunctions();
			request.setAttribute(PAGE, PESISetting);
			request.setAttribute(TITLE, TESISetting);
			
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
			} else {
				if(uF.parseToInt(getStrOrg()) == 0){
					setStrOrg((String) session.getAttribute(ORGID));
				}
				orgList = new FillOrganisation(request).fillOrganisation();
			}
			
			String []strPayCycleDates = null;
			if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equals("null")) {
				strPayCycleDates = getFinancialYear().split("-");
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
			} else {
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
			}
			
//			System.out.println("getEsiUpdate() ===>> " + getEsiUpdate());
			if(getEsiUpdate()!=null && !getEsiUpdate().equalsIgnoreCase("null")) {
				updateESISetting(strPayCycleDates);
			}
			
			levelList = new FillLevel(request).fillLevelBYORG(uF.parseToInt(getStrOrg()));
			
			viewESISetting(uF, strPayCycleDates);
			
			/*if(uF.parseToInt(getLevel())>0)
			{
				salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsWithoutCTC(getLevel());
			}else{
				salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsWithoutCTC("-1");
			}*/
			salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsWithoutCTC("-1");			
			
//			stateList = new FillState(request).fillState("1"); // 1 is for India
			stateList = new FillState(request).fillWLocationStates();
			financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
			getSelectedFilter(uF);
			
			return LOAD;
		}
		
		private void getSelectedFilter(UtilityFunctions uF) {
			Map<String,String> hmFilter=new HashMap<String, String>();
			List<String> alFilter = new ArrayList<String>();
			
			alFilter.add("FINANCIALYEAR");
			String[] strFinancialYears = null;
			if (getFinancialYear() != null) {
				strFinancialYears = getFinancialYear().split("-");
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				
			} else {
				strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				
			}
			hmFilter.put("FINANCIALYEAR", uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, CF.getStrReportDateFormat()));
			
			
			String selectedFilter = getSelectedFilter(uF, alFilter, hmFilter);
			request.setAttribute("selectedFilter", selectedFilter);
		}
		

		public String getSelectedFilter(UtilityFunctions uF, List<String> alFilter, Map<String, String> hmFilter) {
			StringBuilder sbFilter=new StringBuilder("<strong>ESI Administration for FY:&nbsp;&nbsp; </strong>");
			
			for(int i=0;alFilter!=null && i<alFilter.size();i++) {
				if(i>0) {
					sbFilter.append(", ");
				}
				
				if(alFilter.get(i).equals("FINANCIALYEAR")) {
					sbFilter.append("<strong>FINANCIAL YEAR:</strong> ");
					sbFilter.append(hmFilter.get("FINANCIALYEAR"));
				
				}
			}
			return sbFilter.toString();
		}
		
	public void updateESISetting(String []strPayCycleDates){
		Connection con = null;
		PreparedStatement pst=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			
			StringBuilder sb = new StringBuilder();
			for(int i=0; getStrSalaryHeadId()!=null && i<getStrSalaryHeadId().length; i++){
				sb.append(getStrSalaryHeadId()[i]+",");
			}
			
			StringBuilder sbEligible = new StringBuilder();
			for(int i = 0; getSalaryHeadEligible() != null && i < getSalaryHeadEligible().length; i++){
				sbEligible.append(getSalaryHeadEligible()[i]+",");
			}
			
			for(int i=0; getState()!=null && i<getState().length; i++) {
				for(int j=0; getLevel()!=null && j<getLevel().length; j++) {
					pst = con.prepareStatement("update esi_details set eesi_contribution=?, ersi_contribution=?, max_limit=?, user_id=?, " +
							"entry_timestamp=?, salary_head_id=?, eligible_salary_head_ids=? where financial_year_start =? and financial_year_end =? " +
							"and state_id=? and org_id=? and level_id=?");
					pst.setDouble(1, uF.parseToDouble(getEesiContribution()));
					pst.setDouble(2, uF.parseToDouble(getErsiContribution()));
					pst.setDouble(3, uF.parseToDouble(getEsiMaxLimit()));
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
					pst.setString(6, sb.toString());
					pst.setString(7, sbEligible.toString());
					pst.setDate(8, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
					pst.setDate(9, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
					pst.setInt(10, uF.parseToInt(getState()[i]));
					pst.setInt(11, uF.parseToInt(getStrOrg()));	
					pst.setInt(12, uF.parseToInt(getLevel()[j]));		
					int nRow = pst.executeUpdate();	
					pst.close();
					
					if(nRow==0) {
						pst = con.prepareStatement("insert into esi_details (eesi_contribution, ersi_contribution, max_limit, financial_year_start, " +
								"financial_year_end, user_id, entry_timestamp, salary_head_id, state_id,org_id,eligible_salary_head_ids,level_id)" +
								" values (?,?,?,?, ?,?,?,?, ?,?,?,?)");
						pst.setDouble(1, uF.parseToDouble(getEesiContribution()));
						pst.setDouble(2, uF.parseToDouble(getErsiContribution()));
						pst.setDouble(3, uF.parseToDouble(getEsiMaxLimit()));
						pst.setDate(4, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
						pst.setDate(5, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
						pst.setInt(6, uF.parseToInt(strSessionEmpId));
						pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
						pst.setString(8, sb.toString());
						pst.setInt(9, uF.parseToInt(getState()[i]));
						pst.setInt(10, uF.parseToInt(getStrOrg()));
						pst.setString(11, sbEligible.toString());
						pst.setInt(12, uF.parseToInt(getLevel()[j]));
						pst.execute();	
						pst.close();
					}
				}
			}
			session.setAttribute(MESSAGE, SUCCESSM+"ESI policy saved successfully."+END);
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
		
	public void viewESISetting(UtilityFunctions uF, String []strPayCycleDates){
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		LinkedHashMap<String, List<List<String>>> hmESISettings = new LinkedHashMap<String, List<List<String>>>();
		List<List<String>> alESISettings = new ArrayList<List<String>> ();
		try {
			
			setEesiContribution("");
			setErsiContribution("");
			setEsiMaxLimit("");
			setStrSalaryHeadId(null);
			
			con = db.makeConnection(con);
			
			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con);
			if(hmSalaryMap == null) hmSalaryMap = new HashMap<String, String>();
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			if(hmEmpName == null) hmEmpName = new HashMap<String, String>();
			Map<String, String> hmStateMap = CF.getStateMap(con);
			if(hmStateMap == null) hmStateMap = new HashMap<String, String>();
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			if(hmLevelMap == null) hmLevelMap = new HashMap<String, String>();
			
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetailsForPDF(con);
			String currId = CF.getOrgCurrencyIdByOrg(con, getStrOrg());
			if(uF.parseToInt(currId) > 0){
				Map<String, String> hmCurr = hmCurrencyDetails.get(currId);
				if (hmCurr == null) hmCurr = new HashMap<String, String>();
				String currency = hmCurr.get("SHORT_CURR")!=null && !hmCurr.get("SHORT_CURR").trim().equals("") ? hmCurr.get("SHORT_CURR") : "";
				request.setAttribute("currency", currency);
			}
			
			StringBuilder sbStates = null;
			for(int i=0; getState()!=null && i<getState().length; i++) {
				if(sbStates == null) {
					sbStates = new StringBuilder();
					sbStates.append(getState()[i]);
				} else {
					sbStates.append(","+getState()[i]);
				}
			}
			if(sbStates == null) {
				sbStates = new StringBuilder("0");
			}
			StringBuilder sbLevels = null;
			for(int j=0; getLevel()!=null && j<getLevel().length; j++) {
				if(sbLevels == null) {
					sbLevels = new StringBuilder();
					sbLevels.append(getLevel()[j]);
				} else {
					sbLevels.append(","+getLevel()[j]);
				}
			}
			if(sbLevels == null) {
				sbLevels = new StringBuilder("0");
			}
			pst = con.prepareStatement("select * from esi_details where financial_year_start=? and financial_year_end=? and state_id in ("+sbStates.toString()+") " +
				"and org_id=? and level_id in ("+sbLevels.toString()+")");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrOrg()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()){
				setEesiContribution(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("eesi_contribution"))));
				setErsiContribution(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("ersi_contribution"))));
				setEsiMaxLimit(rs.getString("max_limit"));
				setStrSalaryHeadId(rs.getString("salary_head_id").split(","));				
				setStrSalaryHeadId(rs.getString("salary_head_id") != null ? rs.getString("salary_head_id").split(",") : null);
				setSalaryHeadEligible(rs.getString("eligible_salary_head_ids") != null ? rs.getString("eligible_salary_head_ids").split(",") : null);
//				setState(rs.getString("state_id"));
			}	
			rs.close();
			pst.close();
			
			if(salaryHeadEligibleList == null) salaryHeadEligibleList = new ArrayList<FillSalaryHeads>();
			for(int i = 0; getStrSalaryHeadId()!= null && i < getStrSalaryHeadId().length; i++){
				if(uF.parseToInt(getStrSalaryHeadId()[i]) > 0){					
					salaryHeadEligibleList.add(new FillSalaryHeads(getStrSalaryHeadId()[i], uF.showData(hmSalaryMap.get(getStrSalaryHeadId()[i]), "")));
				}
			}
			
			pst = con.prepareStatement("select * from esi_details where org_id=? order by state_id, financial_year_start desc");
			pst.setInt(1, uF.parseToInt(getStrOrg()));			
			rs = pst.executeQuery();			
			String strStateNew = null;
			String strStateOld = null;
			List<String> alInner = new ArrayList<String>();
			while(rs.next()){
				strStateNew  = rs.getString("state_id");
			
				alInner = new ArrayList<String>();
				if(strStateNew!=null && !strStateNew.equalsIgnoreCase(strStateOld)){
					alESISettings = new ArrayList<List<String>> ();
				}
				
				alInner.add(uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, "yyyy")+"-"+uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, "yy"));
				alInner.add(rs.getString("eesi_contribution"));
				alInner.add(rs.getString("ersi_contribution"));
				alInner.add(rs.getString("max_limit"));
				
				StringBuilder sb = null;
				if(rs.getString("salary_head_id") != null){
					String []arr = rs.getString("salary_head_id").split(",");
					for(int i=0; i<arr.length; i++) {
						if(uF.parseToInt(arr[i].trim()) > 0) {
							if(sb == null) {
								sb = new StringBuilder();
								sb.append(uF.showData(hmSalaryMap.get(arr[i]), ""));
							} else {
								sb.append(", "+uF.showData(hmSalaryMap.get(arr[i]), ""));
							}
						}
					}
				}
				if(sb == null) sb = new StringBuilder();
				alInner.add(sb.toString());
				
				StringBuilder sbEligible = null;
				if(rs.getString("eligible_salary_head_ids") != null){
					String []arr = rs.getString("eligible_salary_head_ids").split(",");
					for(int i = 0; i < arr.length; i++) {
						if(uF.parseToInt(arr[i].trim()) > 0) {
							if(sbEligible == null) {
								sbEligible = new StringBuilder();
								sbEligible.append(uF.showData(hmSalaryMap.get(arr[i]), ""));
							} else {
								sbEligible.append(", "+uF.showData(hmSalaryMap.get(arr[i]), ""));
							}
						}
					}
				}
				if(sbEligible == null) sbEligible = new StringBuilder();
				alInner.add(sbEligible.toString());
				
				alInner.add(uF.showData(hmLevelMap.get(rs.getString("level_id")), ""));
				
				alESISettings.add(alInner);
				
				hmESISettings.put(hmStateMap.get(strStateNew), alESISettings);
				
				strStateOld = strStateNew;
				
				request.setAttribute("UPDATED_NAME", hmEmpName.get(rs.getString("user_id")));
				request.setAttribute("UPDATED_DATE", uF.getDateFormat(rs.getString("entry_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()));
			}	
			rs.close();
			pst.close();
			
			
			request.setAttribute("alESISettings", alESISettings);
			request.setAttribute("hmESISettings", hmESISettings);
			
			
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

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}

	public String[] getStrSalaryHeadId() {
		return strSalaryHeadId;
	}

	public void setStrSalaryHeadId(String[] strSalaryHeadId) {
		this.strSalaryHeadId = strSalaryHeadId;
	}

	public String getEesiContribution() {
		return eesiContribution;
	}

	public void setEesiContribution(String eesiContribution) {
		this.eesiContribution = eesiContribution;
	}

	public String getErsiContribution() {
		return ersiContribution;
	}

	public void setErsiContribution(String ersiContribution) {
		this.ersiContribution = ersiContribution;
	}

	public String getEsiUpdate() {
		return esiUpdate;
	}

	public void setEsiUpdate(String esiUpdate) {
		this.esiUpdate = esiUpdate;
	}

	public String getEsiMaxLimit() {
		return esiMaxLimit;
	}

	public void setEsiMaxLimit(String esiMaxLimit) {
		this.esiMaxLimit = esiMaxLimit;
	}

	public List<FillState> getStateList() {
		return stateList;
	}

	public void setStateList(List<FillState> stateList) {
		this.stateList = stateList;
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

	public String[] getSalaryHeadEligible() {
		return salaryHeadEligible;
	}

	public void setSalaryHeadEligible(String[] salaryHeadEligible) {
		this.salaryHeadEligible = salaryHeadEligible;
	}

	public List<FillSalaryHeads> getSalaryHeadEligibleList() {
		return salaryHeadEligibleList;
	}

	public void setSalaryHeadEligibleList(List<FillSalaryHeads> salaryHeadEligibleList) {
		this.salaryHeadEligibleList = salaryHeadEligibleList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public String[] getState() {
		return state;
	}

	public void setState(String[] state) {
		this.state = state;
	}

	public String[] getLevel() {
		return level;
	}

	public void setLevel(String[] level) {
		this.level = level;
	}
	
}