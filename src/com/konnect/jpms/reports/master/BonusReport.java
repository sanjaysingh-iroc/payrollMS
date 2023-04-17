package com.konnect.jpms.reports.master;

import java.net.URLDecoder;
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

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillAmountType;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class BonusReport extends ActionSupport implements ServletRequestAware, IStatements {
 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF=null;
	String strUserType;
	
	private List<FillLevel> levelList;
	private List<FillMonth> monthList;
	 
	private List<FillFinancialYears> financialYearList;
	private String financialYear;
	
	private List<FillAmountType> amountTypeList;
	
	private List<FillOrganisation> orgList;
	private String strOrg;
	private static Logger log = Logger.getLogger(BonusReport.class);

	private String userscreen;
	private String navigationId;
	private String toPage;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		request.setAttribute(PAGE, PBonus);
		request.setAttribute(TITLE, TBonus);
		UtilityFunctions uF = new UtilityFunctions();
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(uF.parseToInt(getStrOrg()) == 0  && orgList!=null && orgList.size()>0) {
				setStrOrg(orgList.get(0).getOrgId());
			}
		} else {
			if(uF.parseToInt(getStrOrg()) == 0) {
				setStrOrg((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
		}
		
		viewBonus(uF);
		
		return loadBonus(uF);
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
		
		
		alFilter.add("ORGANISATION");
		if(getStrOrg()!=null) {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++) {
//				for(int j=0;j<getF_sbu().length;j++) {
					if(getStrOrg().equals(orgList.get(i).getOrgId())) {
						strOrg=orgList.get(i).getOrgName();
					}
//				}
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
	

	
	public String loadBonus(UtilityFunctions uF){	
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
		levelList = new FillLevel(request).fillLevel();
		monthList = new FillMonth().fillMonth();
		
		request.setAttribute("levelList", levelList);
		int levelId, i=0;
		String levelName;
		
		StringBuilder sbLevelList = new StringBuilder();
		sbLevelList.append("{");
		for(i=0; i<levelList.size()-1;i++ ) {
    		levelId = Integer.parseInt((levelList.get(i)).getLevelId());
    		levelName = levelList.get(i).getLevelCodeName();
    		sbLevelList.append("\""+ levelId+"\":\""+levelName+"\",");
		}
		
		levelId = Integer.parseInt((levelList.get(i)).getLevelId());
		levelName = levelList.get(i).getLevelCodeName();
		sbLevelList.append("\""+ levelId+"\":\""+levelName+"\"");	//no comma for last record
		sbLevelList.append("}");
		request.setAttribute("sbLevelList", sbLevelList.toString());
		
		amountTypeList = new FillAmountType().fillAmountType();
		
		request.setAttribute("amountTypeList", amountTypeList);
		String amountTypeId;
		String amountTypeName;
		
		//Formatting list data for drop down list
		if(amountTypeList.size()!=0) {
			StringBuilder sbAmountTypeList = new StringBuilder();
			sbAmountTypeList.append("{");
		    for(i=0; i<amountTypeList.size()-1;i++ ) {
		    		amountTypeId = (amountTypeList.get(i)).getAmountTypeId();
		    		amountTypeName = amountTypeList.get(i).getAmountTypeName();
		    		sbAmountTypeList.append("\""+ amountTypeId+"\":\""+amountTypeName+"\",");
		    }
		    amountTypeId = (amountTypeList.get(i)).getAmountTypeId();
		    amountTypeName = amountTypeList.get(i).getAmountTypeName();
		    sbAmountTypeList.append("\""+ amountTypeId+"\":\""+amountTypeName+"\"");	
		    sbAmountTypeList.append("}");
		    request.setAttribute("sbAmountTypeList", sbAmountTypeList.toString());
		}
		
		request.setAttribute("monthList", monthList);
		String monthId;
		String monthName;
		
		//Formatting list data for drop down list
		if(monthList.size()!=0) {
			StringBuilder sbMonthList = new StringBuilder();
			sbMonthList.append("{");
		    for(i=0; i<amountTypeList.size()-1;i++ ) {
		    	monthId = (amountTypeList.get(i)).getAmountTypeId();
		    	monthName = amountTypeList.get(i).getAmountTypeName();
		    		sbMonthList.append("\""+ monthId+"\":\""+monthName+"\",");
		    }
		    monthId = (amountTypeList.get(i)).getAmountTypeId();
		    monthName = amountTypeList.get(i).getAmountTypeName();
		    sbMonthList.append("\""+ monthId+"\":\""+monthName+"\"");	
		    sbMonthList.append("}");
		    request.setAttribute("sbMonthList", sbMonthList.toString());
		}
		
		getSelectedFilter(uF);
		
		return "load";
	}
	
	
	public String viewBonus(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		try {

			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			Map hmBonusReport = new LinkedHashMap();
			
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equals("null")) {
				String str = URLDecoder.decode(getFinancialYear());
				setFinancialYear(str);
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmSalaryHeads = CF.getSalaryHeadsMap(con);
			Map hmLevelMap = new HashMap();
			 
			pst = con.prepareStatement(selectLevel1);
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			while(rs.next()){
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("level_code"));
				alInner.add(rs.getString("level_name"));
				hmLevelMap.put(rs.getString("level_id"), alInner);
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement(selectBonus);
			pst = con.prepareStatement("SELECT  ald.user_id as userid,* FROM(SELECT * FROM bonus_details where org_id=? and date_from =? and date_to=?) ald " +
					"LEFT JOIN  level_details ld ON ald.level_id = ld.level_id order by date_from desc");
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			String strLevelIdNew = null;
			String strLevelIdOld = null;
			StringBuilder sb = new StringBuilder();
			while(rs.next()){
				strLevelIdNew = rs.getString("level_id");
				if(strLevelIdNew!=null && !strLevelIdNew.equalsIgnoreCase(strLevelIdOld) ){
					alInner = new ArrayList<String>();
				}
				
				alInner.add(rs.getInt("bonus_id")+"");				
				alInner.add(uF.showData(rs.getString("bonus_minimum"),""));
				alInner.add(uF.showData(rs.getString("bonus_maximum"),""));
				alInner.add(uF.charMappingForAmountType(rs.getString("bonus_type"))); 
				
				String []arrSalaryHead = null;
				sb.replace(0, sb.length(), "");
				if(rs.getString("salary_head_id")!=null){
					arrSalaryHead = rs.getString("salary_head_id").split(",");
				}
				for(int i=0; arrSalaryHead!=null && i<arrSalaryHead.length; i++){
					sb.append(hmSalaryHeads.get(arrSalaryHead[i]));
					if(i<arrSalaryHead.length-1){
						sb.append(", ");	
					}
				}
				
				if(rs.getString("bonus_type")!=null && rs.getString("bonus_type").equalsIgnoreCase("%")){
					alInner.add(uF.showData(rs.getString("bonus_amount"),"") +" % of "+sb.toString());
				}else{
					alInner.add(uF.showData(rs.getString("bonus_amount"),""));
				}
				
				alInner.add(rs.getInt("bonus_minimum_days")+"");
				alInner.add(uF.getMonthsListSeprated(rs.getString("bonus_period")));
//				System.out.println("user_id==>"+rs.getString("userid"));
				alInner.add(hmEmpName.get(rs.getString("userid")));
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				al.add(alInner);
				
				hmBonusReport.put(strLevelIdNew, alInner);
				
				strLevelIdOld  = strLevelIdNew ;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", al);
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmBonusReport", hmBonusReport);
			request.setAttribute("hmLevelMap", hmLevelMap);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
			return ERROR;
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


//	public String getStrLocation() {
//		return strLocation;
//	}
//
//
//	public void setStrLocation(String strLocation) {
//		this.strLocation = strLocation;
//	}
//
//
//	public List<FillWLocation> getWorkList() {
//		return workList;
//	}
//
//
//	public void setWorkList(List<FillWLocation> workList) {
//		this.workList = workList;
//	}

}
