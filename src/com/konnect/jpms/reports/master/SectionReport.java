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
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.select.FillUnderSection;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SectionReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	CommonFunctions CF= null; 
	HttpSession session;
	List<FillAmountType> amountTypeList; 
	private static Logger log = Logger.getLogger(SectionReport.class);
	private List<FillSalaryHeads> salaryHeadList;
	private List<FillUnderSection> underSectionList;
	
	private String financialYear;
	private List<FillFinancialYears> financialYearList;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		
		session = request.getSession();if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, PSection);
		request.setAttribute(TITLE, TSection);
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		viewSection(uF);			
		return loadSection(uF);

	}
	
	
	public String loadSection(UtilityFunctions uF){
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
		amountTypeList = new FillAmountType().fillAmountType();
		salaryHeadList = new FillSalaryHeads(request).fillFixSalaryHeads();
		underSectionList=new FillUnderSection().fillUnderSection();
		
		request.setAttribute("amountTypeList", amountTypeList);
		request.setAttribute("salaryHeadList", salaryHeadList);
		request.setAttribute("underSectionList", underSectionList);
		
		String amountTypeId;
		String amountTypeName;
		
		//Formatting list data for drop down list
		int i=0;
		if(amountTypeList.size()!=0) {
			StringBuilder sbAmountTypeList = new StringBuilder();
			sbAmountTypeList.append("{");
		    for(; i<amountTypeList.size()-1;i++ ) {
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
		
		String underSectionId;
		String underSectionName;
		i=0;
		if(underSectionList.size()!=0) { 
			StringBuilder sbunderSectionList = new StringBuilder();
			sbunderSectionList.append("{");
		    for(; i<underSectionList.size()-1;i++ ) {
		    	underSectionId = (underSectionList.get(i)).getUnderSectionId();
		    	underSectionName = underSectionList.get(i).getUnderSectionName();
		    	sbunderSectionList.append("\""+ underSectionId+"\":\""+underSectionName+"\",");
		    }
		    underSectionId = (underSectionList.get(i)).getUnderSectionId();
		    underSectionName = underSectionList.get(i).getUnderSectionName();
		    sbunderSectionList.append("\""+ underSectionId+"\":\""+underSectionName+"\"");	
		    sbunderSectionList.append("}");
		    request.setAttribute("sbunderSectionList", sbunderSectionList.toString());
		}
		
		String salaryHeadId;
		String salaryHeadName;
		i=0;
		if(salaryHeadList.size()!=0) { 
			StringBuilder sbsalaryHeadList = new StringBuilder();
			sbsalaryHeadList.append("{");
		    for(; i<salaryHeadList.size()-1;i++ ) {
		    	salaryHeadId = (salaryHeadList.get(i)).getSalaryHeadId();
		    	salaryHeadName = salaryHeadList.get(i).getSalaryHeadName();
		    	sbsalaryHeadList.append("\""+ salaryHeadId+"\":\""+salaryHeadName+"\",");
		    }
		    salaryHeadId = (salaryHeadList.get(i)).getSalaryHeadId();
		    salaryHeadName = salaryHeadList.get(i).getSalaryHeadName();
		    sbsalaryHeadList.append("\""+ salaryHeadId+"\":\""+salaryHeadName+"\"");	
		    sbsalaryHeadList.append("}");
		    request.setAttribute("sbsalaryHeadList", sbsalaryHeadList.toString());
		}
		
		getSelectedFilter(uF);
		
		return "load";
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("FINANCIALYEAR");
		String[] strFinancialYears = null;
		if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equals("null")) {
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
		StringBuilder sbFilter=new StringBuilder("<strong>Filter Summary:&nbsp;&nbsp; </strong>");
		
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
	
	
	public String viewSection(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

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
			
			
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
			if(hmSalaryHeadMap==null) hmSalaryHeadMap = new LinkedHashMap<String, String>();
			//pst = con.prepareStatement(selectSection);
			pst = con.prepareStatement("SELECT * FROM section_details where isdisplay=true and financial_year_start=? and financial_year_end=? order by section_code");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				
				alInner = new ArrayList<String>();
				alInner.add(Integer.toString(rs.getInt("section_id")));
				alInner.add(rs.getString("section_code"));
				alInner.add(rs.getString("section_description"));
				alInner.add(rs.getString("section_exemption_limit"));
				alInner.add(uF.charMappingForAmountType(rs.getString("section_limit_type")));
				alInner.add(CF.getUnderSectionName(uF.parseToInt(rs.getString("under_section"))));
				alInner.add(uF.showData(uF.getAlignedSlabTypeName(uF.parseToInt(rs.getString("slab_type"))), "")); //6
				alInner.add(rs.getString("salary_head_id")!=null && rs.getString("salary_head_id").length()>0 ? getSalaryHeadNames(hmSalaryHeadMap,rs.getString("salary_head_id")) : "");
				al.add(alInner);
				
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("SELECT max(entry_date) as entry_date, user_id FROM section_details where isdisplay=true and entry_date is not null and financial_year_start=? and financial_year_end=? group by user_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()) {
				request.setAttribute("UPDATED_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				request.setAttribute("UPDATED_NAME", hmEmpName.get(rs.getString("user_id")));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", al);
			
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
	
	
//	public String viewSection(UtilityFunctions uF){
//		
//		Connection con = null;
//		PreparedStatement pst=null;
//		ResultSet rs= null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//		try {
//
//			
//			List<List<String>> al = new ArrayList<List<String>>();
//			List<String> alInner = new ArrayList<String>();
//			
//			con = db.makeConnection(con);
//			
//			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
//			Map<String, String> hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
//			if(hmSalaryHeadMap==null) hmSalaryHeadMap = new LinkedHashMap<String, String>();
//			//pst = con.prepareStatement(selectSection);
//			pst = con.prepareStatement("SELECT * FROM section_details where isdisplay=true order by section_code");
//			rs = pst.executeQuery();
//			while(rs.next()){
//				
//				alInner = new ArrayList<String>();
//				alInner.add(Integer.toString(rs.getInt("section_id")));
//				alInner.add(rs.getString("section_code"));
//				alInner.add(rs.getString("section_description"));
//				alInner.add(rs.getString("section_exemption_limit"));
//				alInner.add(uF.charMappingForAmountType(rs.getString("section_limit_type")));
//				alInner.add(CF.getUnderSectionName(uF.parseToInt(rs.getString("under_section"))));
////				alInner.add(uF.showData(hmSalaryHeadMap.get(rs.getString("salary_head_id")),""));
//				alInner.add(rs.getString("salary_head_id")!=null && rs.getString("salary_head_id").length()>0 ? getSalaryHeadNames(hmSalaryHeadMap,rs.getString("salary_head_id")) : "");
//				al.add(alInner);
//				
//			}
//			
//			
//			pst = con.prepareStatement("SELECT max(entry_date) as entry_date, user_id FROM section_details where isdisplay=true group by user_id");
//			rs = pst.executeQuery();
//			while(rs.next()){
//				request.setAttribute("UPDATED_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
//				request.setAttribute("UPDATED_NAME", hmEmpName.get(rs.getString("user_id")));
//			}
//			
//			request.setAttribute("reportList", al);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}finally{
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//			db.closeConnection(con);
//		}
//		return SUCCESS;
//		
//	}
	
	
	private String getSalaryHeadNames(Map<String, String> hmSalaryHeadMap, String salaryHeadIds) {
		String salaryHeadName="";
		salaryHeadIds=salaryHeadIds.substring(1, salaryHeadIds.length()-1);
		String[] temp=salaryHeadIds.split(",");
		for(int i=0;i<temp.length;i++){
			if(i==0){
				salaryHeadName=hmSalaryHeadMap.get(temp[i].trim());
			}else{
				salaryHeadName+=","+hmSalaryHeadMap.get(temp[i].trim());
			}
		}
		return salaryHeadName;
	}



	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
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
