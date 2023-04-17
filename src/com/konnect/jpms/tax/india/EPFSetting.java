package com.konnect.jpms.tax.india;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EPFSetting extends ActionSupport implements ServletRequestAware, IStatements {
	 
		private static final long serialVersionUID = 1L;
		HttpSession session;
		String strUserType = null;
		String strSessionEmpId = null;
		CommonFunctions CF = null; 
		
		private String strOrg;
		private String strLevel;
		private List<FillOrganisation> orgList;
		private List<FillLevel> levelList; 
		
		private List<FillFinancialYears> financialYearList;
		private String financialYear;
		
		private List<FillSalaryHeads> salaryHeadList;
		private String[] strSalaryHeadId;
		private String eepfContribution;
		private String erpfContribution;
		private String epfMaxLimit;
		private String erpfMaxLimit;
		private String erpsContribution;
		private String epsMaxLimit;
		private String erdliContribution;
		private String edliMaxLimit;
		private String pfAdminCharges;
		private String edliAdminCharges;
		private String epfUpdate;
		
		private boolean erpfContributionchbox;
		private boolean erpsContributionchbox;
		private boolean pfAdminChargeschbox;
		private boolean edliAdminChargeschbox;
		private boolean erdliContributionchbox;
		
		public String execute() throws Exception {
			UtilityFunctions uF = new UtilityFunctions();
			session = request.getSession();
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if(CF==null)return LOGIN;
			
			strUserType = (String) session.getAttribute(USERTYPE);
			strSessionEmpId = (String) session.getAttribute(EMPID);
			
			request.setAttribute(PAGE, PEPFSetting);
			request.setAttribute(TITLE, TEPFSetting);
			

			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
				orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
				if(uF.parseToInt(getStrOrg()) == 0  && orgList!=null && orgList.size()>0){
					setStrOrg(orgList.get(0).getOrgId());
				}
			}else{
				if(uF.parseToInt(getStrOrg()) == 0){
					setStrOrg((String) session.getAttribute(ORGID));
				}
				orgList = new FillOrganisation(request).fillOrganisation();
			}
			
			levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
			
			if(uF.parseToInt(getStrLevel()) == 0 && levelList.size() > 0){
				setStrLevel(levelList.get(0).getLevelId());
			}
			request.setAttribute("isLevels", levelList.size());
		
			String []strPayCycleDates = null;
			if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equals("null")) {
				strPayCycleDates = getFinancialYear().split("-");
//				System.out.println("=================>> 0 == 0");
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
//				System.out.println("=================>> 0 == 1");
			} else {
//				System.out.println("=================>> 0 == else");
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
//				System.out.println("=================>> 0 == else 0");
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
//				System.out.println("=================>> 0 == else 1");
			}
//			System.out.println("=================>> 1");
			if(getEpfUpdate()!=null){
				updateEPFSetting(strPayCycleDates);
			}
			
//			System.out.println("=================>> 2");
			viewEPFSetting(uF, strPayCycleDates);
//			System.out.println("=================>> 3");
			salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsWithoutCTC("-1");
//			salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsWithoutCTC(getStrLevel());
			financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
//			System.out.println("=================>> 4");
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
			
			
			alFilter.add("ORG");
			if(getStrOrg()!=null) {
				String strOrg="";
				for(int i=0;orgList!=null && i<orgList.size();i++) {
					if(getStrOrg().equals(orgList.get(i).getOrgId())) {
						strOrg=orgList.get(i).getOrgName();
					}
				}
				if(strOrg!=null && !strOrg.equals("")) {
					hmFilter.put("ORG", strOrg);
				} else {
					hmFilter.put("ORG", "All Organizations");
				}
			} else {
				hmFilter.put("ORG", "All Organizations");
			}
			
			
			alFilter.add("LEVEL");
			if(getStrLevel()!=null) {
				String strLevel="";
				for(int i=0;levelList!=null && i<levelList.size();i++) {
					if(getStrLevel().equals(levelList.get(i).getLevelId())) {
						strLevel=levelList.get(i).getLevelCodeName();
					}
				}
				if(strLevel!=null && !strLevel.equals("")) {
					hmFilter.put("LEVEL", strLevel);
				} else {
					hmFilter.put("LEVEL", "-");
				}
			} else {
				hmFilter.put("LEVEL", "-");
			}
			
			String selectedFilter = getSelectedFilter(uF, alFilter, hmFilter);
			request.setAttribute("selectedFilter", selectedFilter);
		}
		

		public String getSelectedFilter(UtilityFunctions uF, List<String> alFilter, Map<String, String> hmFilter) {
			StringBuilder sbFilter=new StringBuilder("<strong>EPF Administration for FY:&nbsp;&nbsp; </strong>");
			
			for(int i=0;alFilter!=null && i<alFilter.size();i++) {
				if(i>0) {
					sbFilter.append(", ");
				}
				
				if(alFilter.get(i).equals("FINANCIALYEAR")) {
					sbFilter.append("<strong>FINANCIAL YEAR:</strong> ");
					sbFilter.append(hmFilter.get("FINANCIALYEAR"));
				
				} else if(alFilter.get(i).equals("ORG")) {
					sbFilter.append("<strong>ORG:</strong> ");
					sbFilter.append(hmFilter.get("ORG"));
//				 
				} else if(alFilter.get(i).equals("LEVEL")) {
					sbFilter.append("<strong>LEVEL:</strong> ");
					sbFilter.append(hmFilter.get("LEVEL"));
//				 
				}
				
			}
			return sbFilter.toString();
		}
		
		public void updateEPFSetting(String []strPayCycleDates){
			
			Connection con = null;
			PreparedStatement pst=null;
			ResultSet rs = null;
			Database db = new Database();
			db.setRequest(request);
			UtilityFunctions uF = new UtilityFunctions();
					
			try {
				
				con = db.makeConnection(con);
				
				pst = con.prepareStatement("update epf_details set eepf_contribution=?, erpf_contribution=?,epf_max_limit=?, erps_contribution=?,eps_max_limit=?, " +
						"erdli_contribution=?,edli_max_limit=?, pf_admin_charges=?, edli_admin_charges=?, user_id=?, entry_timestamp=?, salary_head_id=?, " +
						"is_erpf_contribution=?, is_erps_contribution=?, is_erdli_contribution=?, is_pf_admin_charges=?, is_edli_admin_charges=?, erpf_max_limit=? " +
						"where financial_year_start =? and financial_year_end =? and org_id=? and level_id=?");
				
				
				pst.setDouble(1, uF.parseToDouble(getEepfContribution()));
				pst.setDouble(2, uF.parseToDouble(getErpfContribution()));
				pst.setDouble(3, uF.parseToDouble(getEpfMaxLimit()));
				pst.setDouble(4, uF.parseToDouble(getErpsContribution()));
				pst.setDouble(5, uF.parseToDouble(getEpsMaxLimit()));
				pst.setDouble(6, uF.parseToDouble(getErdliContribution()));
				pst.setDouble(7, uF.parseToDouble(getEdliMaxLimit()));
				pst.setDouble(8, uF.parseToDouble(getPfAdminCharges()));
				pst.setDouble(9, uF.parseToDouble(getEdliAdminCharges()));
				pst.setInt(10, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(11, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				
				
				StringBuilder sb = new StringBuilder();
				List<String> alSalaryHeads = new ArrayList<String>();
				for(int i=0; getStrSalaryHeadId()!=null && i<getStrSalaryHeadId().length; i++){
					if(!alSalaryHeads.contains(getStrSalaryHeadId()[i])){
						sb.append(getStrSalaryHeadId()[i]+",");
						alSalaryHeads.add(getStrSalaryHeadId()[i]);
					}
				}
				
				pst.setString(12, sb.toString());
				pst.setBoolean(13, isErpfContributionchbox());
				pst.setBoolean(14, isErpsContributionchbox());
				pst.setBoolean(15, isErdliContributionchbox());
				pst.setBoolean(16, isPfAdminChargeschbox());
				pst.setBoolean(17, isEdliAdminChargeschbox());
				
				pst.setDouble(18, uF.parseToDouble(getErpfMaxLimit()));
				pst.setDate(19, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(20, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(21, uF.parseToInt(getStrOrg()));
				pst.setInt(22, uF.parseToInt(getStrLevel()));
				
				int nRow = pst.executeUpdate();	
				pst.close();
				
				if(nRow==0){
					
					pst = con.prepareStatement("insert into epf_details (eepf_contribution, erpf_contribution,epf_max_limit, erps_contribution,eps_max_limit, " +
							"erdli_contribution,edli_max_limit, pf_admin_charges, edli_admin_charges, financial_year_start, financial_year_end, user_id, " +
							"entry_timestamp, salary_head_id, is_erpf_contribution, is_erps_contribution, is_erdli_contribution, is_pf_admin_charges, " +
							"is_edli_admin_charges, erpf_max_limit,org_id,level_id) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					
					pst.setDouble(1, uF.parseToDouble(getEepfContribution()));
					pst.setDouble(2, uF.parseToDouble(getErpfContribution()));
					pst.setDouble(3, uF.parseToDouble(getEpfMaxLimit()));
					pst.setDouble(4, uF.parseToDouble(getErpsContribution()));
					pst.setDouble(5, uF.parseToDouble(getEpsMaxLimit()));
					pst.setDouble(6, uF.parseToDouble(getErdliContribution()));
					pst.setDouble(7, uF.parseToDouble(getEdliMaxLimit()));
					pst.setDouble(8, uF.parseToDouble(getPfAdminCharges()));
					pst.setDouble(9, uF.parseToDouble(getEdliAdminCharges()));
					pst.setDate(10, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
					pst.setDate(11, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
					pst.setInt(12, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(13, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
					pst.setString(14, sb.toString());
					pst.setBoolean(15, isErpfContributionchbox());
					pst.setBoolean(16, isErpsContributionchbox());
					pst.setBoolean(17, isErdliContributionchbox());
					pst.setBoolean(18, isPfAdminChargeschbox());
					pst.setBoolean(19, isEdliAdminChargeschbox());
					pst.setDouble(20, uF.parseToDouble(getErpfMaxLimit()));
					pst.setInt(21, uF.parseToInt(getStrOrg()));
					pst.setInt(22, uF.parseToInt(getStrLevel()));
					pst.execute();	
					pst.close();
					
				}
				
				
				session.setAttribute(MESSAGE, SUCCESSM+"EPF policy saved successfully."+END);
			
			}catch (Exception e) {
				e.printStackTrace();
			} finally {  
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
		}
		
		public void viewEPFSetting(UtilityFunctions uF, String []strPayCycleDates){
			
			Connection con = null;
			PreparedStatement pst=null;
			ResultSet rs = null;
			Database db = new Database();
			db.setRequest(request);
			Map<String, String> hmEPFSettings = new HashMap<String, String>();
			List<List<String>> alEPFSettings = new ArrayList<List<String>> ();
			
		try {
			   
			con = db.makeConnection(con); 
//			System.out.println("In EPFSettings =============>> ");
			Map<String, String> hmSalaryHeads  = CF.getSalaryHeadsMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetailsForPDF(con);
			String currId = CF.getOrgCurrencyIdByOrg(con, getStrOrg());
			if(uF.parseToInt(currId) > 0){
				Map<String, String> hmCurr = hmCurrencyDetails.get(currId);
				if (hmCurr == null) hmCurr = new HashMap<String, String>();
				String currency = hmCurr.get("SHORT_CURR")!=null && !hmCurr.get("SHORT_CURR").trim().equals("") ? hmCurr.get("SHORT_CURR") : "";
				request.setAttribute("currency", currency);
			}
			
			pst = con.prepareStatement("select * from epf_details where financial_year_start =? and financial_year_end =? and org_id=? and level_id=?");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrOrg()));
			pst.setInt(4, uF.parseToInt(getStrLevel()));
			rs = pst.executeQuery();
			while(rs.next()){

				setEepfContribution(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("eepf_contribution"))));
				setErpfContribution(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("erpf_contribution"))));
				setErpfMaxLimit(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("erpf_max_limit"))));
				setEpfMaxLimit(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("epf_max_limit"))));
				setErpsContribution(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("erps_contribution"))));
				setEpsMaxLimit(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("eps_max_limit"))));
				setErdliContribution(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("erdli_contribution"))));
				setEdliMaxLimit(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("edli_max_limit"))));
				setPfAdminCharges(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("pf_admin_charges"))));
				setEdliAdminCharges(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("edli_admin_charges"))));
				
				setStrSalaryHeadId(rs.getString("salary_head_id").split(","));
				
				setErpfContributionchbox(rs.getBoolean("is_erpf_contribution"));
				setErpsContributionchbox(rs.getBoolean("is_erps_contribution"));
				setErdliContributionchbox(rs.getBoolean("is_erdli_contribution"));
				setPfAdminChargeschbox(rs.getBoolean("is_pf_admin_charges"));
				setEdliAdminChargeschbox(rs.getBoolean("is_edli_admin_charges"));
				
				request.setAttribute("UPDATED_NAME", hmEmpName.get(rs.getString("user_id")));
				request.setAttribute("UPDATED_DATE", uF.getDateFormat(rs.getString("entry_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()));
			}	
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select * from epf_details where org_id=? and level_id=? order by financial_year_start desc");
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			pst.setInt(2, uF.parseToInt(getStrLevel()));
			rs = pst.executeQuery();
			while(rs.next()){
				
				List<String> alInner = new ArrayList<String>();
				
				alInner.add(uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, "yyyy")+"-"+uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, "yy"));
				alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("eepf_contribution"))));
				alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("epf_max_limit"))));
				alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("erpf_contribution"))));
				alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("erpf_max_limit"))));				
				alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("erps_contribution"))));
				alInner.add(rs.getString("eps_max_limit"));
				alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("erdli_contribution"))));
				alInner.add(rs.getString("edli_max_limit"));
				alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("pf_admin_charges"))));
				alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("edli_admin_charges"))));
				
				
				StringBuilder sb = new StringBuilder();
				if(rs.getString("salary_head_id")!=null){
					String []arr = rs.getString("salary_head_id").split(",");
					for(int i=0; i<arr.length; i++){
						sb.append((String)hmSalaryHeads.get(arr[i])+",");
					}
				}
				alInner.add(sb.toString());
				
				
				alEPFSettings.add(alInner);
				
			}	
			rs.close();
			pst.close();
			
			
			request.setAttribute("alEPFSettings", alEPFSettings);
			request.setAttribute("hmEPFSettings", hmEPFSettings);
			
			
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
		
		public String getEepfContribution() {
			return eepfContribution;
		}
		
		public void setEepfContribution(String eepfContribution) {
			this.eepfContribution = eepfContribution;
		}
		
		public String getErpfContribution() {
			return erpfContribution;
		}
		
		public void setErpfContribution(String erpfContribution) {
			this.erpfContribution = erpfContribution;
		}
		
		public String getErpsContribution() {
			return erpsContribution;
		}
		
		public void setErpsContribution(String erpsContribution) {
			this.erpsContribution = erpsContribution;
		}
		
		public String getErdliContribution() {
			return erdliContribution;
		}
		
		public void setErdliContribution(String erdliContribution) {
			this.erdliContribution = erdliContribution;
		}
		
		public String getPfAdminCharges() {
			return pfAdminCharges;
		}
		
		public void setPfAdminCharges(String pfAdminCharges) {
			this.pfAdminCharges = pfAdminCharges;
		}
		
		public String getEdliAdminCharges() {
			return edliAdminCharges;
		}
		public void setEdliAdminCharges(String edliAdminCharges) {
			this.edliAdminCharges = edliAdminCharges;
		}
		public String getEpfUpdate() {
			return epfUpdate;
		}
		
		public void setEpfUpdate(String epfUpdate) {
			this.epfUpdate = epfUpdate;
		}

		public String getEpfMaxLimit() {
			return epfMaxLimit;
		}

		public void setEpfMaxLimit(String epfMaxLimit) {
			this.epfMaxLimit = epfMaxLimit;
		}

		public String getEpsMaxLimit() {
			return epsMaxLimit;
		}

		public void setEpsMaxLimit(String epsMaxLimit) {
			this.epsMaxLimit = epsMaxLimit;
		}

		public String getEdliMaxLimit() {
			return edliMaxLimit;
		}

		public void setEdliMaxLimit(String edliMaxLimit) {
			this.edliMaxLimit = edliMaxLimit;
		}

		public List<FillSalaryHeads> getSalaryHeadList() {
			return salaryHeadList;
		}

		public String[] getStrSalaryHeadId() {
			return strSalaryHeadId;
		}

		public void setStrSalaryHeadId(String []strSalaryHeadId) {
			this.strSalaryHeadId = strSalaryHeadId;
		}

		public boolean isErpfContributionchbox() {
			return erpfContributionchbox;
		}

		public void setErpfContributionchbox(boolean erpfContributionchbox) {
			this.erpfContributionchbox = erpfContributionchbox;
		}

		public boolean isErpsContributionchbox() {
			return erpsContributionchbox;
		}

		public void setErpsContributionchbox(boolean erpsContributionchbox) {
			this.erpsContributionchbox = erpsContributionchbox;
		}

		public boolean isPfAdminChargeschbox() {
			return pfAdminChargeschbox;
		}

		public void setPfAdminChargeschbox(boolean pfAdminChargeschbox) {
			this.pfAdminChargeschbox = pfAdminChargeschbox;
		}

		public boolean isEdliAdminChargeschbox() {
			return edliAdminChargeschbox;
		}

		public void setEdliAdminChargeschbox(boolean edliAdminChargeschbox) {
			this.edliAdminChargeschbox = edliAdminChargeschbox;
		}

		public boolean isErdliContributionchbox() {
			return erdliContributionchbox;
		}

		public void setErdliContributionchbox(boolean erdliContributionchbox) {
			this.erdliContributionchbox = erdliContributionchbox;
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

		public String getErpfMaxLimit() {
			return erpfMaxLimit;
		}

		public void setErpfMaxLimit(String erpfMaxLimit) {
			this.erpfMaxLimit = erpfMaxLimit;
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

		public String getStrLevel() {
			return strLevel;
		}

		public void setStrLevel(String strLevel) {
			this.strLevel = strLevel;
		}

		public List<FillLevel> getLevelList() {
			return levelList;
		}

		public void setLevelList(List<FillLevel> levelList) {
			this.levelList = levelList;
		}
}
