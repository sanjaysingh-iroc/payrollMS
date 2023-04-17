package com.konnect.jpms.requsitions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PerkIncentive extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId = null;
	String strUserType = null;
	CommonFunctions CF = null;
	
	private String strApprove;
	private String strMonth;
	private String financialYear;
	
	private String strLocation;
	private String strDepartment;
	private String strSbu;
	private String strLevel;
	
	private String f_org;
	private String[] f_strWLocation; 
	private String[] f_level;
	private String[] f_department;
	private String[] f_service;
	
	private List<FillFinancialYears> financialYearList;
	private List<FillOrganisation> organisationList;
	private List<FillWLocation> wLocationList;
	private List<FillDepartment> departmentList;
	private List<FillServices> serviceList;
	private List<FillLevel> levelList;
	private List<String> strIsAssignEmpId;
	private List<FillMonth> monthList;
	
	public String execute() throws Exception {
		session = request.getSession();
		request.setAttribute(PAGE, "/jsp/requisitions/PerkIncentive.jsp");
		request.setAttribute(TITLE, "Perk & Incentive");
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(USERTYPE);
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		if(uF.parseToInt(getStrMonth()) == 0){
			setStrMonth(""+uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM")));
		}
		
		if (getStrLocation() != null && !getStrLocation().equals("") && !getStrLocation().equalsIgnoreCase("null")) {
			setF_strWLocation((getStrLocation().split(",")));
		}
		
		if (getStrDepartment() != null && !getStrDepartment().equals("") && !getStrDepartment().equalsIgnoreCase("null")) {
			setF_department((getStrDepartment().split(",")));
		}
		if (getStrSbu() != null && !getStrSbu().equals("") && !getStrSbu().equalsIgnoreCase("null")) {
			setF_service((getStrSbu().split(",")));
		}
		if (getStrLevel() != null && !getStrLevel().equals("") && !getStrLevel().equalsIgnoreCase("null")) {
			setF_level((getStrLevel().split(",")));
		}
		if(getStrApprove()!=null && !getStrApprove().trim().equals("")){
			approvePerkIncentive(uF);
		}
		
		
		viewPerkIncentive(uF);
	
		return loadPerkIncentive(uF);
	}

	private void approvePerkIncentive(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			String[] strFinancialYear = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {				
				strFinancialYear = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			} else {
				strFinancialYear = new FillFinancialYears(request).fillLatestFinancialYears();
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
				setFinancialYear(strFinancialYear[0] + "-" + strFinancialYear[1]);
			}
			
			con = db.makeConnection(con);
			
			if(getStrIsAssignEmpId()!=null && getStrIsAssignEmpId().size() > 0){
				Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
				if(hmEmpLevelMap == null) hmEmpLevelMap = new HashMap<String, String>();
				
				List<String> alLevel = new ArrayList<String>();
				List<String> alEmp = new ArrayList<String>();
				Map<String, List<String>> hmEmpPerkId = new HashMap<String, List<String>>();
				for(String str : getStrIsAssignEmpId()){
					if(str!=null && !str.equals("")){
						
						String[] strTemp = str.split("_");
						int nLevelId = uF.parseToInt(hmEmpLevelMap.get(strTemp[0].trim()));
						if(nLevelId > 0){
							if(!alLevel.contains(""+nLevelId)){
								alLevel.add(""+nLevelId);
							}
						}
						if(!alEmp.contains(strTemp[0].trim())){
							alEmp.add(strTemp[0].trim());
						}
						
						List<String> alPerkId = (List<String>) hmEmpPerkId.get(strTemp[0].trim());
						if(alPerkId == null) alPerkId = new ArrayList<String>();
						alPerkId.add(strTemp[1].trim());
						
						hmEmpPerkId.put(strTemp[0].trim(), alPerkId);
					}
				}
				
				if(alLevel!=null && alLevel.size() > 0){
					String strLevelIds = StringUtils.join(alLevel.toArray(),",");
					
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select * from perk_details where financial_year_start=? and financial_year_end=? " +
							"and level_id in ("+strLevelIds+") ");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//					System.out.println("pst====>"+pst);
					rs = pst.executeQuery();
					
					Map<String, List<Map<String, String>>> hmPerkLevel = new HashMap<String, List<Map<String,String>>>();
					while(rs.next()){
						List<Map<String, String>> al = (List<Map<String, String>>) hmPerkLevel.get(rs.getString("level_id"));
						if(al == null) al = new ArrayList<Map<String,String>>();
						
						Map<String, String> hmPerk = new HashMap<String, String>();
						hmPerk.put("PERK_ID", rs.getString("perk_id"));
						hmPerk.put("PERK_CODE", uF.showData(rs.getString("perk_code"), ""));
						hmPerk.put("PERK_NAME", uF.showData(rs.getString("perk_name"), ""));
						hmPerk.put("PERK_DESCRIPTION", uF.showData(rs.getString("perk_description"), ""));
						hmPerk.put("PERK_PAYMENT_CYCLE", rs.getString("perk_payment_cycle"));
						hmPerk.put("PERK_LEVEL_ID", rs.getString("level_id"));
						hmPerk.put("PERK_MAX_AMOUNT", rs.getString("max_amount"));
						hmPerk.put("PERK_ORG_ID", rs.getString("org_id"));
						
						al.add(hmPerk);
						
						hmPerkLevel.put(rs.getString("level_id"), al);
					}
					rs.close();
					pst.close();
					
					for(int i = 0; i < alEmp.size(); i++){
						String strEmpId = alEmp.get(i).trim();
						String strLevelId = hmEmpLevelMap.get(strEmpId);
						
			    		List<Map<String, String>> alPerkList = (List<Map<String, String>>) hmPerkLevel.get(strLevelId);
			    		if(alPerkList == null) alPerkList = new ArrayList<Map<String, String>>();
			    		
			    		List<String> alPerkId = (List<String>) hmEmpPerkId.get(strEmpId);
						if(alPerkId == null) alPerkId = new ArrayList<String>();
			    		
			    		for(int j = 0; j < alPerkList.size(); j++){
			    			Map<String, String> hmPerk = (Map<String, String>) alPerkList.get(j);
			    			if(hmPerk == null) hmPerk = new HashMap<String, String>();
			    			
			    			if(!alPerkId.contains(hmPerk.get("PERK_ID"))){
			    				continue;
			    			}
			    			
			    			String strPerkAmount = (String) request.getParameter("strPerkAmount_"+strEmpId+"_"+hmPerk.get("PERK_ID"));
//			    			System.out.println("strEmpId==>"+strEmpId+"==perkId==>"+hmPerk.get("PERK_ID")+"==strPerkAmount==>"+strPerkAmount);
			    			
			    			if(uF.parseToDouble(strPerkAmount) > 0.0d){
				    			pst = con.prepareStatement("insert into emp_perks (financial_year_start, financial_year_end, perk_type_id, " +
				    					"perk_purpose, perk_amount, emp_id, entry_date,approval_1,approval_2,approval_1_emp_id,approval_2_emp_id,approval_1_date,approval_2_date,perk_month) " +
				    					"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?);");
								pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
								pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
								pst.setInt(3, uF.parseToInt(hmPerk.get("PERK_ID")));
								pst.setString(4, "Perk and Incentive");
								pst.setDouble(5, uF.parseToDouble(strPerkAmount));
								pst.setInt(6, uF.parseToInt(strEmpId));
								pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(8, 1);
								pst.setInt(9, 1);
								pst.setInt(10, uF.parseToInt(strSessionEmpId));
								pst.setInt(11, uF.parseToInt(strSessionEmpId));
								pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setDate(13, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(14, uF.parseToInt(getStrMonth()));
//								System.out.println("pst=====>"+pst);
								pst.execute();
								pst.close();
			    			}
			    		}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadPerkIncentive(UtilityFunctions uF){
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillMonth();
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
//		System.out.println("strUserType==>"+strUserType);
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			if((String) session.getAttribute(WLOCATION_ACCESS) != null && !((String) session.getAttribute(WLOCATION_ACCESS)).equals("") && !((String) session.getAttribute(WLOCATION_ACCESS)).equalsIgnoreCase("null")) {
				wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
			}else{
				wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATIONID));
			}
			
			if((String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("") && !((String)session.getAttribute(ORG_ACCESS)).equalsIgnoreCase("null")) {
				organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			}else {
				organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORGID));
			}
						
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
			alFilter.add("ORGANISATION");
			if(getF_org()!=null) {
				String strOrg="";
				for(int i=0;organisationList!=null && i<organisationList.size();i++) {
					if(getF_org().equals(organisationList.get(i).getOrgId())) {
						strOrg=organisationList.get(i).getOrgName();
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
			
			if (getFinancialYear() != null) {	
				alFilter.add("FINANCIALYEAR");
				String[] strFinancialYear = getFinancialYear().split("-");
				hmFilter.put("FINANCIALYEAR", uF.getDateFormat(strFinancialYear[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strFinancialYear[1], DATE_FORMAT, CF.getStrReportDateFormat()));
			}
			
			alFilter.add("MONTH");	
			if(getStrMonth()!=null){
				String strMonth="";
				for(int i=0;monthList!=null && i<monthList.size();i++){
					if(getStrMonth().equals(monthList.get(i).getMonthId())){
						strMonth=monthList.get(i).getMonthName();
					}
				}
				if(strMonth!=null && !strMonth.equals("")){
					hmFilter.put("MONTH", strMonth);
				}else{
					hmFilter.put("MONTH", "Select Month");
				}
			}
			
			alFilter.add("LOCATION");
//			System.out.println("location==>"+getF_strWLocation());
			if(getF_strWLocation()!=null) {
				String strLocation="";
				int k=0;
				for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
					for(int j=0;j<getF_strWLocation().length;j++) {
						if(getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
							if(k==0) {
								strLocation=wLocationList.get(i).getwLocationName();
							} else {
								strLocation+=", "+wLocationList.get(i).getwLocationName();
							}
							k++;
						}
					}
				}
				if(strLocation!=null && !strLocation.equals("")) {
					
					hmFilter.put("LOCATION", strLocation);
				} else {
					hmFilter.put("LOCATION", "All Locations");
				}
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
			
			alFilter.add("DEPARTMENT");
			if(getF_department()!=null) {
				String strDepartment="";
				int k=0;
				for(int i=0;departmentList!=null && i<departmentList.size();i++) {
					for(int j=0;j<getF_department().length;j++) {
						if(getF_department()[j].equals(departmentList.get(i).getDeptId())) {
							if(k==0) {
								strDepartment=departmentList.get(i).getDeptName();
							} else {
								strDepartment+=", "+departmentList.get(i).getDeptName();
							}
							k++;
						}
					}
				}
				if(strDepartment!=null && !strDepartment.equals("")) {
					hmFilter.put("DEPARTMENT", strDepartment);
				} else {
					hmFilter.put("DEPARTMENT", "All Departments");
				}
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
			
			alFilter.add("SERVICE");
			if(getF_service()!=null) {
				String strService="";
				int k=0;
				for(int i=0;serviceList!=null && i<serviceList.size();i++) {
					for(int j=0;j<getF_service().length;j++) {
						if(getF_service()[j].equals(serviceList.get(i).getServiceId())) {
							if(k==0) {
								strService=serviceList.get(i).getServiceName();
							} else {
								strService+=", "+serviceList.get(i).getServiceName();
							}
							k++;
						}
					}
				}
				if(strService!=null && !strService.equals("")) {
					hmFilter.put("SERVICE", strService);
				} else {
					hmFilter.put("SERVICE", "All SBUs");
				}
			} else {
				hmFilter.put("SERVICE", "All SBUs");
			}
		
			alFilter.add("LEVEL");
			if(getF_level()!=null){
				String strLevel="";
				int k=0;
				for(int i=0;levelList!=null && i<levelList.size();i++){
					for(int j=0;j<getF_level().length;j++){
						if(getF_level()[j].equals(levelList.get(i).getLevelId())){
							if(k==0){
								strLevel=levelList.get(i).getLevelCodeName();
							}else{
								strLevel+=", "+levelList.get(i).getLevelCodeName();
							}
							k++;
						}
					}
				}
				if(strLevel!=null && !strLevel.equals("")){
					hmFilter.put("LEVEL", strLevel);
				}else{
					hmFilter.put("LEVEL", "All Levels");
				}
			}else{
				hmFilter.put("LEVEL", "All Levels");
			}
			
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
    }
	
	
	public void viewPerkIncentive(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			
			String[] strFinancialYear = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {				
				strFinancialYear = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			} else {
				strFinancialYear = new FillFinancialYears(request).fillLatestFinancialYears();
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
				setFinancialYear(strFinancialYear[0] + "-" + strFinancialYear[1]);
			}
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			if(hmEmpLevelMap == null) hmEmpLevelMap = new HashMap<String, String>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from perk_details where financial_year_start=? and financial_year_end=? ");
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and level_id in ( "+StringUtils.join(getF_level(), ",")+") ");
            }
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			
			Map<String, List<Map<String, String>>> hmPerkLevel = new HashMap<String, List<Map<String,String>>>();
			while(rs.next()){
				List<Map<String, String>> al = (List<Map<String, String>>) hmPerkLevel.get(rs.getString("level_id"));
				if(al == null) al = new ArrayList<Map<String,String>>();
				
				Map<String, String> hmPerk = new HashMap<String, String>();
				hmPerk.put("PERK_ID", rs.getString("perk_id"));
				hmPerk.put("PERK_CODE", uF.showData(rs.getString("perk_code"), ""));
				hmPerk.put("PERK_NAME", uF.showData(rs.getString("perk_name"), ""));
				hmPerk.put("PERK_DESCRIPTION", uF.showData(rs.getString("perk_description"), ""));
				hmPerk.put("PERK_PAYMENT_CYCLE", rs.getString("perk_payment_cycle"));
				hmPerk.put("PERK_LEVEL_ID", rs.getString("level_id"));
				hmPerk.put("PERK_MAX_AMOUNT", rs.getString("max_amount"));
				hmPerk.put("PERK_ORG_ID", rs.getString("org_id"));
				
				al.add(hmPerk);
				
				hmPerkLevel.put(rs.getString("level_id"), al);
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive = true " +
					"and joining_date<= ? and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd " +
					"where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id in (select level_id from perk_details " +
					"where financial_year_start=? and financial_year_end=?");
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and level_id in ( "+StringUtils.join(getF_level(), ",")+")  ");
            }
			sbQuery.append(")) ");
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
				sbQuery.append(" and eod.supervisor_emp_id="+uF.parseToInt((String)session.getAttribute(EMPID))+" ");
			}
			if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                	 sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            } 
            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
 			sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			
			List<String> alEmp = new ArrayList<String>();
			Map<String, Map<String, String>> hmEmpData = new HashMap<String, Map<String,String>>();
			while(rs.next()){
				alEmp.add(rs.getString("emp_id"));
				
				Map<String, String> hmEmp = new HashMap<String, String>();
				
			//	String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				String strEmpName = rs.getString("emp_fname") +strEmpMName+ " "+ rs.getString("emp_lname");
				hmEmp.put("EMP_NAME", strEmpName);
				hmEmp.put("EMP_LEVEL", ""+uF.parseToInt(hmEmpLevelMap.get(rs.getString("emp_id"))));
				
				hmEmpData.put(rs.getString("emp_id"), hmEmp);
			}
			rs.close();
			pst.close();
			
			if(alEmp.size() > 0){
				String strEmpIds = StringUtils.join(alEmp.toArray(),",");
				/*
				 * Annual Data
				 * **/
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(perk_amount) as perk_amount, emp_id, perk_type_id from emp_perks where " +
						" financial_year_start=? and financial_year_end=? and emp_id in("+strEmpIds+") and perk_type_id in (select perk_id " +
						"from perk_details where financial_year_start=? and financial_year_end=? and perk_payment_cycle='A') group by emp_id, perk_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				
				Map<String, String> hmAppliedPerk = new HashMap<String, String>();
				while(rs.next()){
					hmAppliedPerk.put(rs.getString("emp_id")+"_"+rs.getString("perk_type_id"), rs.getString("perk_amount"));
				}
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(perk_amount) as perk_amount, emp_id, perk_type_id from emp_perks where approval_1=1 and approval_2=1 and ispaid=false " +
						"and financial_year_start=? and financial_year_end=? and emp_id in("+strEmpIds+") and perk_type_id in (select perk_id " +
						"from perk_details where financial_year_start=? and financial_year_end=? and perk_payment_cycle='A') group by emp_id, perk_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				
				Map<String, String> hmApprovedPerk = new HashMap<String, String>();
				while(rs.next()){
					hmApprovedPerk.put(rs.getString("emp_id")+"_"+rs.getString("perk_type_id"), rs.getString("perk_amount"));
				}
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(perk_amount) as perk_amount, emp_id, perk_type_id from emp_perks where approval_1=1 and approval_2=1 and ispaid=true " +
						"and financial_year_start=? and financial_year_end=? and emp_id in("+strEmpIds+") and perk_type_id in (select perk_id " +
						"from perk_details where financial_year_start=? and financial_year_end=? and perk_payment_cycle='A') group by emp_id, perk_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				
				Map<String, String> hmPaidPerk = new HashMap<String, String>();
				while(rs.next()){
					hmPaidPerk.put(rs.getString("emp_id")+"_"+rs.getString("perk_type_id"), rs.getString("perk_amount"));
				}
				rs.close();
				pst.close();
				/*
				 * Annual Data end
				 * **/
				
				/*
				 * Month Data
				 * **/
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(perk_amount) as perk_amount, emp_id, perk_type_id from emp_perks where " +
						" financial_year_start=? and financial_year_end=? and emp_id in("+strEmpIds+") and perk_type_id in (select perk_id " +
						"from perk_details where financial_year_start=? and financial_year_end=? and perk_payment_cycle='M') and perk_month=? group by emp_id, perk_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(getStrMonth()));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				
				while(rs.next()){
					hmAppliedPerk.put(rs.getString("emp_id")+"_"+rs.getString("perk_type_id"), rs.getString("perk_amount"));
				}
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(perk_amount) as perk_amount, emp_id, perk_type_id from emp_perks where approval_1=1 and approval_2=1 and ispaid=false " +
						"and financial_year_start=? and financial_year_end=? and emp_id in("+strEmpIds+") and perk_type_id in (select perk_id " +
						"from perk_details where financial_year_start=? and financial_year_end=? and perk_payment_cycle='M') and perk_month=? group by emp_id, perk_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(getStrMonth()));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				
				while(rs.next()){
					hmApprovedPerk.put(rs.getString("emp_id")+"_"+rs.getString("perk_type_id"), rs.getString("perk_amount"));
				}
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(perk_amount) as perk_amount, emp_id, perk_type_id from emp_perks where approval_1=1 and approval_2=1 and ispaid=true " +
						"and financial_year_start=? and financial_year_end=? and emp_id in("+strEmpIds+") and perk_type_id in (select perk_id " +
						"from perk_details where financial_year_start=? and financial_year_end=? and perk_payment_cycle='M') and perk_month=? group by emp_id, perk_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(getStrMonth()));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				
				while(rs.next()){
					hmPaidPerk.put(rs.getString("emp_id")+"_"+rs.getString("perk_type_id"), rs.getString("perk_amount"));
				}
				rs.close();
				pst.close();
				/*
				 * Month Data end
				 * **/
				request.setAttribute("hmAppliedPerk", hmAppliedPerk);
				request.setAttribute("hmApprovedPerk", hmApprovedPerk);
				request.setAttribute("hmPaidPerk", hmPaidPerk);
			}
			
			request.setAttribute("alEmp", alEmp);
			request.setAttribute("hmEmpData", hmEmpData);
			request.setAttribute("hmPerkLevel", hmPerkLevel);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
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

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public String getStrApprove() {
		return strApprove;
	}

	public void setStrApprove(String strApprove) {
		this.strApprove = strApprove;
	}

	public List<String> getStrIsAssignEmpId() {
		return strIsAssignEmpId;
	}

	public void setStrIsAssignEmpId(List<String> strIsAssignEmpId) {
		this.strIsAssignEmpId = strIsAssignEmpId;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrSbu() {
		return strSbu;
	}

	public void setStrSbu(String strSbu) {
		this.strSbu = strSbu;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

}
