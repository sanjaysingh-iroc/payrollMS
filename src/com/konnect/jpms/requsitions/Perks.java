package com.konnect.jpms.requsitions;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPerkType;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Perks extends ActionSupport  implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null; 
	
	private CommonFunctions CF; 
	private static Logger log = Logger.getLogger(Perks.class);
	
	private String strId;
	private String strPurpose;
	private String strAmount;
	private String strFrom;
	private String strTo;
	private String strType;
	private File strDocument;
	private String strDocumentContentType;
	private String strDocumentFileName;
	private String strViewDocument;
	private String policy_id;
	
	private List<FillPerkType> typeList;
	private List<FillWLocation> wLocationList;
	private List<FillOrganisation> organisationList;
	private List<FillFinancialYears> financialYearList; 
	private List<FillEmployee> empNamesList;
	private List<FillMonth> monthList;
	
	private String f_org;
	private String f_strWLocation;
	private String strSelectedEmpId;
	private String strStartDate;
	private String strEndDate; 
	private String financialYear;
	private String f_financialYear;
	private String alertStatus;
	private String alert_type;
	private String strMonth;
	private String currUserType;
	private String alertID; 
	
	public String execute() {
	    
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		request.setAttribute(PAGE, PMyPerks);
		request.setAttribute(TITLE, "Perks");
		
		UtilityFunctions uF = new UtilityFunctions();
		
		String strE = (String)request.getParameter("E");
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
				
		if(getF_org()==null) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		
		if (getFinancialYear() == null) {
			String[]  strFinancialYear = new FillFinancialYears(request).fillLatestFinancialYears();
			setFinancialYear(strFinancialYear[0] + "-" + strFinancialYear[1]);
		}
		
		if (getF_financialYear() == null) {
			String[]  strFinancialYear = new FillFinancialYears(request).fillLatestFinancialYears();
			setF_financialYear(strFinancialYear[0] + "-" + strFinancialYear[1]);
		}
		
		if(uF.parseToInt(getStrMonth()) == 0) {
			setStrMonth(""+uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM")));
		}
		
		String[] arrDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
		if(getStrStartDate()==null) {
			setStrStartDate(arrDates[0]);
		}
		if(getStrEndDate()==null) {
			setStrEndDate(arrDates[1]);
		}
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillMonth();
		
		empNamesList = getEmpList();
		
		if(strE!=null) {
			viewPerk(strE);
		} else if(getStrId()!=null && getStrId().length()>0) {
//			loadReimbursements();
			return updatePerk();
		} else if(getStrAmount()!=null) {
//			loadReimbursements();
			return addPerk(); 
		}
		
		if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))) {
			perkReportAdmin();
		} else if(strUserType!=null && (strUserType.equalsIgnoreCase(MANAGER))) {
			perkReportManager();
		} else if(strUserType!=null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT))) {
			getPerkPolicyMember();
			perkReportHRManager();
		} else if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE) ) {
			getPerkPolicyMember();
			perkReportEmp();
		}
		return loadPerks();
	}
	
	private List<FillEmployee> getEmpList() {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id ");
			 if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			 } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			 }
				
			if(getF_strWLocation()!=null && uF.parseToInt(getF_strWLocation())>0) {
	            sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
           
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst==>"+pst);
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				
				String strEmpMName = "";
				
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") 
						+ " ["+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	public String loadPerks() {
		
		String strE = (String)request.getParameter("E");
		UtilityFunctions uF = new UtilityFunctions();
	
		Connection con= null;
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
			}
			
			con = db.makeConnection(con);
			String empLevel = CF.getEmpLevelId(con, (String)session.getAttribute(EMPID));
			
			typeList = new FillPerkType(request).fillPerkType(uF.parseToInt(empLevel), strFinancialYearStart, strFinancialYearEnd);
			
			if(strE==null) {
				setStrPurpose(null);
				setStrAmount(null);
				setStrType(null);
			}
			getSelectedFilter(uF);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		return LOAD;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if((strUserType != null && !strUserType.equals(MANAGER) && !strUserType.equals(EMPLOYEE)) || (getCurrUserType() != null && getCurrUserType().equals(strBaseUserType))) {
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
			
			alFilter.add("LOCATION");
			if(getF_strWLocation()!=null) {
				String strLocation="";
				for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
					if(getF_strWLocation().equals(wLocationList.get(i).getwLocationId())) {
						strLocation=wLocationList.get(i).getwLocationName();
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
			
			alFilter.add("EMP");
			
			if(getStrSelectedEmpId()!=null) {
				String strEmpName="";
				
				for(int i=0;empNamesList!=null && i<empNamesList.size();i++) {
					
					if(uF.parseToInt(getStrSelectedEmpId())  == uF.parseToInt(empNamesList.get(i).getEmployeeId())) {
						strEmpName=empNamesList.get(i).getEmployeeCode();
					}
				}
				
				if(strEmpName!=null && !strEmpName.equals("")) {
					hmFilter.put("EMP", strEmpName);
				} else {
					hmFilter.put("EMP", "All Employee");
				}
			} else {
				hmFilter.put("EMP", "All Employee");
			}
		}

		if (getF_financialYear() != null) {	
			alFilter.add("FINANCIALYEAR");
			String[] strFinancialYear = getF_financialYear().split("-");
			hmFilter.put("FINANCIALYEAR", uF.getDateFormat(strFinancialYear[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strFinancialYear[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	private void getPerkPolicyMember() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();		
		String policy_id=null;
		
		try {
			int strEmpID = uF.parseToInt(strSessionEmpId);
			con = db.makeConnection(con);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			String empLevelId=hmEmpLevelMap.get(""+strEmpID);
			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
			String locationID=hmEmpWlocationMap.get(""+strEmpID);
			
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			if(hmUserTypeIdMap==null) hmUserTypeIdMap = new HashMap<String, String>();
			
			pst = con.prepareStatement("select policy_id from work_flow_policy_details where type='"+WORK_FLOW_PERK+"' and level_id=? and wlocation_id=?");
			pst.setInt(1, uF.parseToInt(empLevelId));
			pst.setInt(2, uF.parseToInt(locationID));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				policy_id=rs.getString("policy_id");
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(policy_id) == 0) {
				pst = con.prepareStatement("select policy_count from work_flow_member wfm,work_flow_policy wfp where wfp.group_id=wfm.group_id " +
						"and wfp.work_flow_member_id=wfm.work_flow_member_id and wfm.wlocation_id=? and wfm.is_default = true");
				pst.setInt(1, uF.parseToInt(locationID));
				rs = pst.executeQuery();
				while(rs.next()) {
					policy_id=rs.getString("policy_count");
				}
				rs.close();
				pst.close();
			}
			
			if(uF.parseToInt(policy_id)>0) {
				pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
						" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
				pst.setInt(1,uF.parseToInt(policy_id));
				rs=pst.executeQuery();
				
				Map<String,List<String>> hmMemberMap=new LinkedHashMap<String, List<String>>();
				while(rs.next()) {
					List<String> innerList=new ArrayList<String>();
					innerList.add(rs.getString("member_type"));
					innerList.add(rs.getString("member_id"));
					innerList.add(rs.getString("member_position"));
					innerList.add(rs.getString("work_flow_mem"));
					innerList.add(rs.getString("work_flow_member_id"));
					
					hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
				}
				rs.close();
				pst.close();
				
				Map<String,String> hmMemberOption=new LinkedHashMap<String,String>();
				
				Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
				boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
				
				
				Iterator<String> it=hmMemberMap.keySet().iterator();
				while(it.hasNext()) {
					String work_flow_member_id=it.next();
					List<String> innerList=hmMemberMap.get(work_flow_member_id);
					
					if(uF.parseToInt(innerList.get(0))==1) {
						int memid=uF.parseToInt(innerList.get(1));
						
						switch(memid) {
						case 1:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=1 "
												+ " and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
												+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									alList.add(rs.getString("emp_lname"));
									
									outerList.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList!=null && !outerList.isEmpty()) {
									StringBuilder sbComboBox=new StringBuilder();
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList.size();i++) {
										List<String> alList=outerList.get(i);
										sbComboBox.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox.append("</select>");								
									
									String optionTr="<tr><td class=\"label alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr);
								}
								break;
							
						case 2:
								pst = con.prepareStatement("select * from (select supervisor_emp_id from employee_official_details where emp_id=? and supervisor_emp_id!=0) as a," +
										"employee_personal_details epd,user_details ud where a.supervisor_emp_id=epd.emp_per_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" +
										" and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList11=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(hmUserTypeIdMap.get(MANAGER));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									
									alList.add(rs.getString("emp_lname"));
									
									outerList11.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList11!=null && !outerList11.isEmpty()) {
									StringBuilder sbComboBox11=new StringBuilder();
									sbComboBox11.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox11.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList11.size();i++) {
										List<String> alList=outerList11.get(i);
										sbComboBox11.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox11.append("</select>");								
									
									String optionTr11="<tr><td class=\"label alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox11.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr11);
								}
								break;
							
						case 3:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
												+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=3 "
												+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
												+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, uF.parseToInt(locationID));
								pst.setInt(2, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList1=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									
									alList.add(rs.getString("emp_lname"));
									
									outerList1.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList1!=null && !outerList1.isEmpty()) {
									StringBuilder sbComboBox1=new StringBuilder();
									sbComboBox1.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox1.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList1.size();i++) {
										List<String> alList=outerList1.get(i);
										sbComboBox1.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox1.append("</select>");								
									
									String optionTr1="<tr><td class=\"label alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox1.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr1);
								}
								break;
						
						case 4:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=4 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
										+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								
								pst.setInt(1, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList2=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									
									alList.add(rs.getString("emp_lname"));
									
									outerList2.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList2!=null && !outerList2.isEmpty()) {
									StringBuilder sbComboBox2=new StringBuilder();
									sbComboBox2.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox2.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList2.size();i++) {
										List<String> alList=outerList2.get(i);
										sbComboBox2.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox2.append("</select>");								
									
									String optionTr2="<tr><td class=\"label alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox2.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr2);
								}
								break;
						
						case 5:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=5 "
										+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
										+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								
								pst.setInt(1, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList3=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									
									alList.add(rs.getString("emp_lname"));
									
									outerList3.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList3!=null && !outerList3.isEmpty()) {
									StringBuilder sbComboBox3=new StringBuilder();
									sbComboBox3.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox3.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList3.size();i++) {
										List<String> alList=outerList3.get(i);
										sbComboBox3.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox3.append("</select>");								
									
									String optionTr3="<tr><td class=\"label alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox3.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr3);
								}
								break;
							
						case 6:
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=6 "
										+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE'" 
										+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
								pst.setInt(1, uF.parseToInt(locationID));
								pst.setInt(2, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList4=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(rs.getString("usertype_id"));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									
									alList.add(rs.getString("emp_lname"));
									
									outerList4.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList4!=null && !outerList4.isEmpty()) {
									StringBuilder sbComboBox4=new StringBuilder();
									sbComboBox4.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox4.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList4.size();i++) {
										List<String> alList=outerList4.get(i);
										sbComboBox4.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox4.append("</select>");								
									
									String optionTr4="<tr><td class=\"label alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox4.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr4);
								}
								break;
							
						case 7:
							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
									"employee_personal_details epd where ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id " +
									"and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true and ud.emp_id in (select eod.emp_hr from employee_official_details eod," +
									"employee_personal_details epd where epd.emp_per_id=eod.emp_id and eod.emp_id=?)" +
									" union " +
									"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
									"employee_personal_details epd where ud.usertype_id=7 and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' " +
									"and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true " +
									" union " +
									"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
									"employee_personal_details epd where ud.usertype_id=1 and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and " +
									"epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true");
								pst.setInt(1, strEmpID);
								pst.setInt(2, strEmpID);
								pst.setInt(3, strEmpID);
								pst.setInt(4, strEmpID);
								rs = pst.executeQuery();
								List<List<String>> outerList5=new ArrayList<List<String>>();
								while (rs.next()) {
									List<String> alList=new ArrayList<String>();
									alList.add(rs.getString("emp_id"));
									alList.add(hmUserTypeIdMap.get(HRMANAGER));
									alList.add(rs.getString("emp_fname")); 
									
									String strEmpMName = "";
									
									if(flagMiddleName) {
										if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
											strEmpMName = " "+rs.getString("emp_mname");
										}
									}
									alList.add(strEmpMName);
									
									
									alList.add(rs.getString("emp_lname"));
									
									outerList5.add(alList);									
								}
								rs.close();
								pst.close();
								
								if(outerList5!=null && !outerList5.isEmpty()) {
									StringBuilder sbComboBox5=new StringBuilder();
									sbComboBox5.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
									sbComboBox5.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
									for(int i=0;i<outerList5.size();i++) {
										List<String> alList=outerList5.get(i);
										sbComboBox5.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");									
									}
									sbComboBox5.append("</select>");								
									
									String optionTr5="<tr><td class=\"label alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox5.toString()+"</td></tr>";
									
									hmMemberOption.put(innerList.get(4), optionTr5);
								}
								break;							
						
						case 13:
							pst = con.prepareStatement("select * from (select distinct(hod_emp_id) as hod_emp_id from employee_official_details where " +
									"emp_id=? and hod_emp_id!=0) as a,employee_personal_details epd,user_details ud where a.hod_emp_id=epd.emp_per_id " +
									"and ud.emp_id=epd.emp_per_id  and ud.status='ACTIVE' and epd.is_alive=true order by epd.emp_fname");
							pst.setInt(1,strEmpID);
							rs = pst.executeQuery();
							List<List<String>> outerHODList=new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList=new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname")); 
								
								String strEmpMName = "";
								
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);
								
								
								alList.add(rs.getString("emp_lname"));
								
								outerHODList.add(alList);									
							}
							rs.close();
							pst.close();
							
							if(outerHODList!=null && !outerHODList.isEmpty()) {
								StringBuilder sbComboBox11=new StringBuilder();
								sbComboBox11.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								sbComboBox11.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
								for(int i=0;i<outerHODList.size();i++) {
									List<String> alList=outerHODList.get(i);
									sbComboBox11.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
								}
								sbComboBox11.append("</select>");								
								
								String optionTr11="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox11.toString()+"</td></tr>";
								
								hmMemberOption.put(innerList.get(4), optionTr11);
							}
							break;
						
						}						
						
					} else if(uF.parseToInt(innerList.get(0))==3) {
						int memid=uF.parseToInt(innerList.get(1));
						
						List<List<String>> outerList=new ArrayList<List<String>>();
						pst = con.prepareStatement("select emp_id from specific_emp se,employee_personal_details epd where se.emp_id=epd.emp_per_id " +
								"and se.policy_id = ? and se.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname"); 
						pst.setInt(1,uF.parseToInt(policy_id));
						pst.setInt(2, strEmpID);
						rs = pst.executeQuery();
						while (rs.next()) {
							List<String> alList = new ArrayList<String>();
							alList.add(rs.getString("emp_id"));
							outerList.add(alList);
						}
						rs.close();
						pst.close();
						
						if(outerList!=null && !outerList.isEmpty()) {
							StringBuilder sbComboBox=new StringBuilder();
							sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
							sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
							for(int i=0;i<outerList.size();i++) {
								List<String> alList=outerList.get(i);
								sbComboBox.append("<option value=\""+alList.get(0)+"\">"+hmEmpCodeName.get(alList.get(0).trim())+"</option>");									
							}
							sbComboBox.append("</select>");								
							
							String optionTr="<tr><td class=\"label alignRight\">Your work flow:<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
							
							hmMemberOption.put(innerList.get(4), optionTr);
						}
					}
				}
				
				String divpopup="";
				StringBuilder sb = new StringBuilder();
				if(uF.parseToBoolean(CF.getIsWorkFlow())) {
					
					 sb.append("<div id=\"popup_name" + strEmpID + "\" class=\"popup_block\">" + 
							   "<h2 class=\"textcolorWhite\">Perk of "+hmEmpCodeName.get(""+strEmpID)+"</h2>" + 
							   "<table>");
										
					 if(hmMemberOption!=null && !hmMemberOption.isEmpty() ) {
						 Iterator<String> it1=hmMemberOption.keySet().iterator();
						while(it1.hasNext()) {
							String memPosition=it1.next();
							String optiontr=hmMemberOption.get(memPosition);					
							sb.append(optiontr); 
						}
						sb.append("<tr><td>&nbsp;</td><td><input type=\"submit\" name=\"submit\" value=\"Submit\" class=\"input_button\" onclick=\"return checkPerkLimit();\"/></td>" +
								"</tr>");
					 } else {
						 sb.append("<tr><td colspan=\"2\">Your work flow is not defined. Please, speak to your hr for your work flow.</td></tr>");
					 }
					 sb.append("</table></div>");
					
					divpopup="<input type=\"button\" name=\"submit1\" value=\"Submit\" class=\"input_button\" onclick=\"return checkPerkLimit();\"/>";
				} else {
					sb.append("");
					divpopup="<input type=\"submit\" name=\"submit1\" value=\"Submit\" class=\"input_button\" onclick=\"return checkPerkLimit();\"/>";
				}
				request.setAttribute("hmMemberOption",hmMemberOption);
				request.setAttribute("policy_id",policy_id);
				request.setAttribute("divpopup",divpopup);
				request.setAttribute("perksD", sb.toString());
				request.setAttribute("strEmpID", strEmpID);
			}
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String perkReportEmp() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			String[] strFinancialYear = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getF_financialYear() != null) {				
				strFinancialYear = getF_financialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			
			}
			
			con = db.makeConnection(con);
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
			Map<String, String> hmPerkMap = CF.getPerkMap(con);
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and effective_type='"+WORK_FLOW_PERK+"' and effective_id in(select perks_id from emp_perks where emp_id=?");
			sbQuery.append(" and to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();	
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();
			while(rs.next()) {
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and effective_type='"+WORK_FLOW_PERK+"' and effective_id in(select perks_id from emp_perks where emp_id=?");
			sbQuery.append(" and to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?)" +
					" group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();	
			while(rs.next()) {
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_perks where emp_id=?");
			sbQuery.append(" and to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=? ");
			sbQuery.append(" order by entry_date desc ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			List<List<String>> alReport = new ArrayList<List<String>>(); 
			int nCount=0;
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				
				String strCurrId = hmEmpCurrency.get(rs.getString("emp_id"));
				Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
				if(hmCurrencyInner==null)hmCurrencyInner=new HashMap<String, String>();
				String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
				
				StringBuilder sb = new StringBuilder();
				
				sb.append("<div style=\"float:left;width:20px;margin-top:1px\" id=\"myDiv"+nCount+"\">");
							
				
				if(rs.getInt("approval_1")==0 || rs.getInt("approval_2")==0) {
					 /*sb.append("<img src=\""+request.getContextPath()+"/images1/icons/pending.png\" title=\"Waiting for approval, click to pullout\" border=\"0\" onclick=\"getContent('myDiv"+nCount+"', 'UpdateRequest.action?S=2&RID="+rs.getString("perks_id")+"&T=PERK&M=D')\" />");*/
					sb.append("<a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+nCount+"', 'UpdateRequest.action?S=2&RID="+rs.getString("perks_id")+"&T=PERK&M=D')\"  > <i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for approval, click to pullout\" ></i></a>");
					
				} else if(rs.getInt("approval_1")==1 || rs.getInt("approval_2")==1) {
					 /*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					 
				} else if(rs.getInt("approval_1")==-1 || rs.getInt("approval_2")==-1) {
					/*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
				}
				
				sb.append("</div>");
				
				sb.append("<div style=\"float:left;width:80%;\">");
				
				sb.append("Your request for perk reimbursement for "+uF.showData(hmPerkMap.get(rs.getString("perk_type_id")), "N/A")+
						" on "+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat())
						+" for "+ strCurrSymbol+rs.getString("perk_amount")
						+" specifying "
						+"\""+uF.showData(rs.getString("perk_purpose"), "")+"\"");
				
				boolean isApproval1 = false;
				if(rs.getInt("approval_1")== -1) {
					sb.append(" has been denied by "+hmEmpNames.get(rs.getString("approval_1_emp_id")));
					isApproval1 = true;
				} else if(rs.getInt("approval_1")== 0) {
					sb.append(" is waiting for your approval");
					isApproval1 = true;
				} else if(rs.getInt("approval_1")== 1) {
					sb.append(" is approved by "+hmEmpNames.get(rs.getString("approval_1_emp_id")) +" on "+ uF.getDateFormat(rs.getString("approval_1_date"), DBDATE, CF.getStrReportDateFormat()));
					isApproval1 = true;
				} 
				
				if(rs.getInt("approval_1")==0 && rs.getInt("approval_2")==0) {
					sb.append(" <a href=\"javascript:void(0);\" onclick=\"editAppliedPerk('"+rs.getString("perks_id")+"')\">Edit</a> ");
				}
				
				sb.append("</div>");
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>2) {
					if(CF.getStrDocRetriveLocation()==null) {
						sb.append("<a target=\"blank\" href=\"" +request.getContextPath()+ DOCUMENT_LOCATION + rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					} else {
						sb.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_PERKS+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					}
				}
				
				alInner.add(sb.toString());
				
				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("perks_id"))!=null) {
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("perks_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("perks_id"))!=null) {
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("perks_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else {
					alInner.add("");
				}
				
				alReport.add(alInner);
				nCount++;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReport", alReport);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
	}
	
	public String perkReportManager() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			String[] strFinancialYear = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getF_financialYear() != null) {				
				strFinancialYear = getF_financialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			con = db.makeConnection(con);
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
			Map<String, String> hmPerkMap = CF.getPerkMap(con);
			
			StringBuilder sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0  and effective_type='"+WORK_FLOW_PERK+"' ");
			sbQuery.append("and effective_id in (select perks_id from emp_perks where to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) ");
			sbQuery.append(" group by effective_id");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
					" and is_approved=0 and effective_type='"+WORK_FLOW_PERK+"' ");
			sbQuery.append("and effective_id in (select perks_id from emp_perks where to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) ");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append("group by effective_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(4, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(4, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(5, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmMemNextApproval.put(rs.getString("effective_id")+"_"+rs.getString("user_type_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and status=0 and effective_type='"+WORK_FLOW_PERK+"' ");
			sbQuery.append("and effective_id in (select perks_id from emp_perks where to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) ");
			sbQuery.append(" group by effective_id");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("effective_id"))) {
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select perks_id from emp_perks where approval_1=-1 and approval_2=-1 and ");
			sbQuery.append("to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=? ");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("perks_id"))) {
					deniedList.add(rs.getString("perks_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
					" and emp_id=? and status=0 and effective_type='"+WORK_FLOW_PERK+"' ");
			sbQuery.append("and effective_id in (select perks_id from emp_perks where to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) ");
			sbQuery.append(" group by effective_id,is_approved");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproval = new HashMap<String, String>();			
			while(rs.next()) {
				hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and effective_type='"+WORK_FLOW_PERK+"' and effective_id in(select perks_id from emp_perks where ");
			sbQuery.append(" to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();	
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();
			while(rs.next()) {
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and effective_type='"+WORK_FLOW_PERK+"' and effective_id in(select perks_id from emp_perks where ");
			sbQuery.append(" to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?)" +
					" group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();	
			while(rs.next()) {
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where status=0 and effective_type='"+WORK_FLOW_PERK+"' ");
			sbQuery.append("and effective_id in (select perks_id from emp_perks where to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) ");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append(" order by effective_id,member_position");
			
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(3, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(3, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(4, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
			rs = pst.executeQuery();			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
			Map<String, List<String>> hmCheckEmpUserType = new HashMap<String, List<String>>();
			while(rs.next()) {
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("effective_id")+"_"+rs.getString("emp_id"));
				if(checkEmpUserTypeList == null)checkEmpUserTypeList = new ArrayList<String>();				
				checkEmpUserTypeList.add(rs.getString("user_type_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
				hmCheckEmpUserType.put(rs.getString("effective_id")+"_"+rs.getString("emp_id"), checkEmpUserTypeList);
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select status,effective_id from work_flow_details where status=0 and effective_type='"+WORK_FLOW_PERK+"' ");
			sbQuery.append("and effective_id in (select perks_id from emp_perks where to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) ");
			sbQuery.append(" order by effective_id,status");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmCheckStatus = new HashMap<String,String>();	
			while(rs.next()) {
				String status=rs.getString("status");
				hmCheckStatus.put(rs.getString("effective_id"), status);
			}
			rs.close();
			pst.close();
			
			List<String> alList = new ArrayList<String>();	
			sbQuery = new StringBuilder();
			sbQuery.append("select ep.*,wfd.user_type_id as user_type from emp_perks ep, work_flow_details wfd " +
					"where ep.perks_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_PERK+"' and ep.emp_id >0");
			sbQuery.append(" and to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=? ");
			if(uF.parseToInt(getStrSelectedEmpId()) > 0) {
				sbQuery.append(" and ep.emp_id = "+uF.parseToInt(getStrSelectedEmpId())+" ");
			}
			if (uF.parseToInt(getF_strWLocation()) > 0 || uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and ep.emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
				if (uF.parseToInt(getF_strWLocation()) > 0) {
					sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation())+" ");
				}
				if (uF.parseToInt(getF_org()) > 0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org())+" ");
				}
				sbQuery.append(")");
			}
			if(strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
			}
			sbQuery.append(" order by ep.entry_date desc ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			List<List<String>> alReport = new ArrayList<List<String>>(); 
			int nCount=0;
			while(rs.next()) {
				
				if(rs.getInt("emp_id")<0) {
					continue;
				}
				
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("perks_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("perks_id")+"_"+strSessionEmpId);
				if(checkEmpUserTypeList==null) checkEmpUserTypeList = new ArrayList<String>();
				
				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN)) {
					continue;
				}
				String userType = rs.getString("user_type");				
				if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && alList.contains(rs.getString("perks_id"))) {
					continue;
				} else if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("perks_id"))) {
					userType = strUserTypeId;
					alList.add(rs.getString("perks_id"));
				} else if(!checkEmpUserTypeList.contains(userType)) {
					continue;	
				}
				
				List<String> alInner = new ArrayList<String>();
				String strCurrId = hmEmpCurrency.get(rs.getString("emp_id"));
				Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
				if(hmCurrencyInner==null)hmCurrencyInner=new HashMap<String, String>();
				String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
				
				StringBuilder sb = new StringBuilder();
				
				sb.append("<div style=\"float:left;width:130px;margin-top:1px;padding-right:8px\" id=\"myDiv" + nCount + "\" >");
				if(deniedList.contains(rs.getString("perks_id"))) {
					/*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\" ></i>");
				} else if(1==rs.getInt("approval_1") && 1==rs.getInt("approval_2")) {
					/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
				} else if(uF.parseToInt(hmAnyOneApproval.get(rs.getString("perks_id")))==1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("perks_id")))==rs.getInt("approval_1") && uF.parseToInt(hmAnyOneApproval.get(rs.getString("perks_id")))==rs.getInt("approval_2")) {
					/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("perks_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("perks_id")+"_"+userType)) && uF.parseToInt(hmNextApproval.get(rs.getString("perks_id")))>0) {
					/*sb.append("<img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" />&nbsp;");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"click to approve/deny\"></i>&nbsp;");
					
					sb.append("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" onclick=\"approveDeny('1','"+rs.getString("perks_id")+"','"+userType+"');\"></i>&nbsp;");
					sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"approveDeny('-1','"+rs.getString("perks_id")+"','"+userType+"');\"></i>");
					
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("perks_id")))<uF.parseToInt(hmMemNextApproval.get(rs.getString("perks_id")+"_"+userType)) || (uF.parseToInt(hmNextApproval.get(rs.getString("perks_id")))==0 && uF.parseToInt(hmNextApproval.get(rs.getString("perks_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("perks_id")+"_"+userType)))) {
						if(rs.getInt("approval_1")==0 && rs.getInt("approval_2")==0) {		
							if(strUserType.equalsIgnoreCase(ADMIN) && uF.parseToInt(strUserTypeId) == uF.parseToInt(userType)) {
								 /*sb.append("<img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" />&nbsp;");*/
								sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"click to approve/deny\" title=\"click to approve/deny\"></i>&nbsp;");
								
								sb.append("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" onclick=\"approveDeny('1','"+rs.getString("perks_id")+"','"+userType+"');\"></i>&nbsp;");
								sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"approveDeny('-1','"+rs.getString("perks_id")+"','"+userType+"');\"></i>");
							} else {
								/*sb.append("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
								sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Waiting for workflow\" ></i>");
								
							}
						} else if(rs.getInt("approval_1")==1 && rs.getInt("approval_2")==1) {							
							/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
							sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
							
							
						} else {
							/*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
							sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
							
						}
					
				} else {
					if(strUserType.equalsIgnoreCase(ADMIN)) {
						/*sb.append("<img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" />&nbsp;");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"click to approve/deny\"></i>&nbsp;");
						
						
						sb.append("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" onclick=\"approveDeny('1','"+rs.getString("perks_id")+"','"+userType+"');\"></i>&nbsp;");
						sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"approveDeny('-1','"+rs.getString("perks_id")+"','"+userType+"');\"></i>");
					} else {
						/*sb.append("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Waiting for workflow\"></i>");
						
					}
				}
				
				sb.append("</div>");
				
				sb.append("<div style=\"float:left;width:85%\">");
				
				sb.append(hmEmpNames.get(rs.getString("emp_id")));
				sb.append(" has submitted a request for perks for "+uF.showData(hmPerkMap.get(rs.getString("perk_type_id")), "N/A")+
						" on "+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat())
						+" for "+ strCurrSymbol+rs.getString("perk_amount")
						+" specifying "
						+"\""+rs.getString("perk_purpose")+"\"");
				
				boolean isApproval1 = false;
				if(rs.getInt("approval_1")== -1) {
					sb.append(" has been denied by "+hmEmpNames.get(rs.getString("approval_1_emp_id")));
					isApproval1 = true;
				} else if(rs.getInt("approval_1")== 0) {
					sb.append(" is waiting for approval");
					isApproval1 = true;
				} else if(rs.getInt("approval_1")== 1) {
					sb.append(" is approved by "+hmEmpNames.get(rs.getString("approval_1_emp_id")) +" on "+ uF.getDateFormat(rs.getString("approval_1_date"), DBDATE, CF.getStrReportDateFormat()));
					isApproval1 = true;
				} 
				
				sb.append("  ["+uF.showData(hmUserTypeMap.get(userType), "")+"]");
				
				sb.append("</div>");
					
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>2) {
					
					if(CF.getStrDocRetriveLocation()==null) {
						sb.append("<a target=\"blank\" href=\"" +request.getContextPath()+ DOCUMENT_LOCATION + rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					} else {
						sb.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_PERKS+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					}
				}
				 
				alInner.add(sb.toString());
				
				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("perks_id"))!=null) {
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("perks_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("perks_id"))!=null) {
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("perks_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else {
					alInner.add("");
				}
				
				alReport.add(alInner);
				nCount++;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReport", alReport);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
	}
	
	public String perkReportAdmin() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			String[] strFinancialYear = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getF_financialYear() != null) {				
				strFinancialYear = getF_financialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			
			}
			con = db.makeConnection(con);
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
			Map<String, String> hmPerkMap = CF.getPerkMap(con);
			
			StringBuilder sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0  and effective_type='"+WORK_FLOW_PERK+"' ");
			sbQuery.append("and effective_id in (select perks_id from emp_perks where to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) ");
			sbQuery.append(" group by effective_id");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
					" and is_approved=0 and effective_type='"+WORK_FLOW_PERK+"' ");
			sbQuery.append("and effective_id in (select perks_id from emp_perks where to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) ");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append("group by effective_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(4, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(4, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(5, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmMemNextApproval.put(rs.getString("effective_id")+"_"+rs.getString("user_type_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and status=0 and effective_type='"+WORK_FLOW_PERK+"' ");
			sbQuery.append("and effective_id in (select perks_id from emp_perks where to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) ");
			sbQuery.append(" group by effective_id");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("effective_id"))) {
					deniedList.add(rs.getString("effective_id"));
				}
			}	
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select perks_id from emp_perks where approval_1=-1 and approval_2=-1 and ");
			sbQuery.append("to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=? ");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("perks_id"))) {
					deniedList.add(rs.getString("perks_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
					" and emp_id=? and status=0 and effective_type='"+WORK_FLOW_PERK+"' ");
			sbQuery.append("and effective_id in (select perks_id from emp_perks where to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) ");
			sbQuery.append(" group by effective_id,is_approved");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproval = new HashMap<String, String>();			
			while(rs.next()) {
				hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and effective_type='"+WORK_FLOW_PERK+"' and effective_id in(select perks_id from emp_perks where ");
			sbQuery.append(" to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();	
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();
			while(rs.next()) {
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and effective_type='"+WORK_FLOW_PERK+"' and effective_id in(select perks_id from emp_perks where ");
			sbQuery.append(" to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?)" +
					" group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();	
			while(rs.next()) {
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where status=0 and effective_type='"+WORK_FLOW_PERK+"' ");
			sbQuery.append("and effective_id in (select perks_id from emp_perks where to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) ");
			
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append(" order by effective_id,member_position");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(3, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(3, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(4, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
			rs = pst.executeQuery();
			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();
			Map<String, List<String>> hmCheckEmpUserType = new HashMap<String, List<String>>();
			while(rs.next()) {
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("effective_id")+"_"+rs.getString("emp_id"));
				if(checkEmpUserTypeList == null)checkEmpUserTypeList = new ArrayList<String>();				
				checkEmpUserTypeList.add(rs.getString("user_type_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
				hmCheckEmpUserType.put(rs.getString("effective_id")+"_"+rs.getString("emp_id"), checkEmpUserTypeList);
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select status,effective_id from work_flow_details where status=0 and effective_type='"+WORK_FLOW_PERK+"' ");
			sbQuery.append("and effective_id in (select perks_id from emp_perks where to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) ");
			sbQuery.append(" order by effective_id,status");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmCheckStatus = new HashMap<String,String>();	
			while(rs.next()) {
				String status=rs.getString("status");
				hmCheckStatus.put(rs.getString("effective_id"), status);
			}
			rs.close();
			pst.close();
			
			List<String> alList = new ArrayList<String>();	
			sbQuery = new StringBuilder();
			sbQuery.append("select ep.*,wfd.user_type_id as user_type from emp_perks ep, work_flow_details wfd " +
					"where ep.perks_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_PERK+"' and ep.emp_id >0");
			sbQuery.append(" and to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=? ");
			if(uF.parseToInt(getStrSelectedEmpId()) > 0) {
				sbQuery.append(" and ep.emp_id = "+uF.parseToInt(getStrSelectedEmpId())+" ");
			}
			if (uF.parseToInt(getF_strWLocation()) > 0 || uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and ep.emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
				if (uF.parseToInt(getF_strWLocation()) > 0) {
					sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation())+" ");
				}
				if (uF.parseToInt(getF_org()) > 0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org())+" ");
				}
				sbQuery.append(")");
			}
			if(strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
			}
			sbQuery.append(" order by ep.entry_date desc ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			List<List<String>> alReport = new ArrayList<List<String>>(); 
			int nCount=0;
			while(rs.next()) {
				
				if(rs.getInt("emp_id")<0) {
					continue;
				}
				
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("perks_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("perks_id")+"_"+strSessionEmpId);
				if(checkEmpUserTypeList==null) checkEmpUserTypeList = new ArrayList<String>();
				
				boolean checkGHRInWorkflow = true;
				if(checkEmpUserTypeList.contains(hmUserTypeIdMap.get(HRMANAGER)) && !checkEmpUserTypeList.contains(hmUserTypeIdMap.get(ADMIN)) && strUserType != null && strUserType.equals(ADMIN)) {
					checkGHRInWorkflow = false;
				}
				
				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN)) {
					continue;
				}
				String userType = rs.getString("user_type");				
				if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && alList.contains(rs.getString("perks_id"))) {
					continue;
				} else if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("perks_id"))) {
					userType = strUserTypeId;
					alList.add(rs.getString("perks_id"));
				} else if(!checkEmpUserTypeList.contains(userType)) {
					continue;	
				}
				
				List<String> alInner = new ArrayList<String>();
				String strCurrId = hmEmpCurrency.get(rs.getString("emp_id"));
				Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
				if(hmCurrencyInner==null)hmCurrencyInner=new HashMap<String, String>();
				String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
				
				StringBuilder sb = new StringBuilder();
				
				sb.append("<div style=\"float:left;width:130px;margin-top:1px;padding-right:8px\" id=\"myDiv" + nCount + "\" >");
				if(deniedList.contains(rs.getString("perks_id"))) {
					 /*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
					
				} else if(1==rs.getInt("approval_1") && 1==rs.getInt("approval_2")) {
					/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
				} else if(uF.parseToInt(hmAnyOneApproval.get(rs.getString("perks_id")))==1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("perks_id")))==rs.getInt("approval_1") && uF.parseToInt(hmAnyOneApproval.get(rs.getString("perks_id")))==rs.getInt("approval_2")) {
					/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("perks_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("perks_id")+"_"+userType)) && uF.parseToInt(hmNextApproval.get(rs.getString("perks_id")))>0) {
					 /*sb.append("<img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" />&nbsp;");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"click to approve/deny\"></i>&nbsp;");
					
					sb.append("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" onclick=\"approveDeny('1','"+rs.getString("perks_id")+"','"+userType+"');\"></i>&nbsp;");
					sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"approveDeny('-1','"+rs.getString("perks_id")+"','"+userType+"');\"></i>");
					
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("perks_id")))<uF.parseToInt(hmMemNextApproval.get(rs.getString("perks_id")+"_"+userType)) || (uF.parseToInt(hmNextApproval.get(rs.getString("perks_id")))==0 && uF.parseToInt(hmNextApproval.get(rs.getString("perks_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("perks_id")+"_"+userType)))) {
					
						if(rs.getInt("approval_1")==0 && rs.getInt("approval_2")==0) {		
							if(strUserType.equalsIgnoreCase(ADMIN)  && uF.parseToInt(strUserTypeId) == uF.parseToInt(userType)) {
								/*sb.append("<img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" />&nbsp;");*/
								sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"click to approve/deny\"></i>&nbsp;");
								
								sb.append("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" onclick=\"approveDeny('1','"+rs.getString("perks_id")+"','"+userType+"');\"></i>&nbsp;");
								sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"approveDeny('-1','"+rs.getString("perks_id")+"','"+userType+"');\"></i>");
							} else {
								/*sb.append("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
								sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Waiting for workflow\" style=\"color:#f7ee1d\"></i>");
								
								if(!checkGHRInWorkflow) {
									sb.append("&nbsp;|&nbsp;&nbsp;<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" onclick=\"approveDeny('1','"+rs.getString("perks_id")+"','');\" title=\"Approve ("+ADMIN+")\" /></i>&nbsp;");
									sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"approveDeny('-1','"+rs.getString("perks_id")+"','');\" title=\"Deny ("+ADMIN+")\" /></i>");
								}
							}
						} else if(rs.getInt("approval_1")==1 && rs.getInt("approval_2")==1) {							
							/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
							sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
							
						} else {
							/*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
							sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
						}
					
				} else {
					if(strUserType.equalsIgnoreCase(ADMIN)) {
						 /*sb.append("<img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" />&nbsp;");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"click to approve/deny\"></i>&nbsp;");
						sb.append("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" onclick=\"approveDeny('1','"+rs.getString("perks_id")+"','"+userType+"');\"></i>&nbsp;");
						sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"approveDeny('-1','"+rs.getString("perks_id")+"','"+userType+"');\"></i>");
					} else {
						/*sb.append("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Waiting for workflow\" ></i>");
						
					}
				}
				
				sb.append("</div>");
			
				sb.append("<div style=\"float:left;width:85%\">");
				
				sb.append(hmEmpNames.get(rs.getString("emp_id")));
				sb.append(" has submitted a request for perks for "+uF.showData(hmPerkMap.get(rs.getString("perk_type_id")), "N/A")+
						" on "+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat())
						+" for "+ strCurrSymbol+rs.getString("perk_amount")
						+" specifying "
						+"\""+rs.getString("perk_purpose")+"\"");
				
				boolean isApproval1 = false;
				if(rs.getInt("approval_1")== -1) {
					sb.append(" has been denied by "+hmEmpNames.get(rs.getString("approval_1_emp_id")));
					isApproval1 = true;
				} else if(rs.getInt("approval_1")== 0) {
					sb.append(" is waiting for approval");
					isApproval1 = true;
				} else if(rs.getInt("approval_1")== 1) {
					sb.append(" is approved by "+hmEmpNames.get(rs.getString("approval_1_emp_id")) +" on "+ uF.getDateFormat(rs.getString("approval_1_date"), DBDATE, CF.getStrReportDateFormat()));
					isApproval1 = true;
				} 
			
				sb.append("  ["+uF.showData(hmUserTypeMap.get(userType), "")+"]");
				
				sb.append("</div>");
					
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>2) {
					if(CF.getStrDocRetriveLocation()==null) {
						sb.append("<a target=\"blank\" href=\"" +request.getContextPath()+ DOCUMENT_LOCATION + rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					} else {
						sb.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_PERKS+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					}
				}
				 
				alInner.add(sb.toString());
				
				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("perks_id"))!=null) {
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("perks_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("perks_id"))!=null) {
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("perks_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else {
					alInner.add("");
				}
				
				alReport.add(alInner);
				nCount++;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReport", alReport);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
	}
	
	public String perkReportHRManager() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			String[] strFinancialYear = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getF_financialYear() != null) {				
				strFinancialYear = getF_financialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			
			}
			
			con = db.makeConnection(con);
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
			Map<String, String> hmPerkMap = CF.getPerkMap(con);
			
			StringBuilder sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0  and effective_type='"+WORK_FLOW_PERK+"' ");
			sbQuery.append("and effective_id in (select perks_id from emp_perks where to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) ");
			sbQuery.append(" group by effective_id");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
					" and is_approved=0 and effective_type='"+WORK_FLOW_PERK+"' ");
			sbQuery.append("and effective_id in (select perks_id from emp_perks where to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) ");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append("group by effective_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(4, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(4, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(5, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmMemNextApproval.put(rs.getString("effective_id")+"_"+rs.getString("user_type_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and status=0 and effective_type='"+WORK_FLOW_PERK+"' ");
			sbQuery.append("and effective_id in (select perks_id from emp_perks where to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) ");
			sbQuery.append(" group by effective_id");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("effective_id"))) {
					deniedList.add(rs.getString("effective_id"));
				}
			}	
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select perks_id from emp_perks where approval_1=-1 and approval_2=-1 and ");
			sbQuery.append("to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=? ");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("perks_id"))) {
					deniedList.add(rs.getString("perks_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
					" and emp_id=? and status=0 and effective_type='"+WORK_FLOW_PERK+"' ");
			sbQuery.append("and effective_id in (select perks_id from emp_perks where to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) ");
			sbQuery.append(" group by effective_id,is_approved");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproval = new HashMap<String, String>();			
			while(rs.next()) {
				hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and effective_type='"+WORK_FLOW_PERK+"' and effective_id in(select perks_id from emp_perks where ");
			sbQuery.append(" to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();	
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();
			while(rs.next()) {
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and effective_type='"+WORK_FLOW_PERK+"' and effective_id in(select perks_id from emp_perks where ");
			sbQuery.append(" to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?)" +
					" group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();	
			while(rs.next()) {
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where status=0 and effective_type='"+WORK_FLOW_PERK+"' ");
			sbQuery.append("and effective_id in (select perks_id from emp_perks where to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) ");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append(" order by effective_id,member_position");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(3, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(3, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(4, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
			rs = pst.executeQuery();	
			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
			Map<String, List<String>> hmCheckEmpUserType = new HashMap<String, List<String>>();
			while(rs.next()) {
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("effective_id")+"_"+rs.getString("emp_id"));
				if(checkEmpUserTypeList == null)checkEmpUserTypeList = new ArrayList<String>();				
				checkEmpUserTypeList.add(rs.getString("user_type_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
				hmCheckEmpUserType.put(rs.getString("effective_id")+"_"+rs.getString("emp_id"), checkEmpUserTypeList);
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();	
			sbQuery.append("select status,effective_id from work_flow_details where status=0 and effective_type='"+WORK_FLOW_PERK+"' ");
			sbQuery.append("and effective_id in (select perks_id from emp_perks where to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=?) ");
			sbQuery.append(" order by effective_id,status");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmCheckStatus = new HashMap<String,String>();	
			while(rs.next()) {
				String status=rs.getString("status");
				hmCheckStatus.put(rs.getString("effective_id"), status);
			}
			rs.close();
			pst.close();
			
			List<String> alList = new ArrayList<String>();	
			sbQuery = new StringBuilder();
			sbQuery.append("select ep.*,wfd.user_type_id as user_type from emp_perks ep, work_flow_details wfd " +
					"where ep.perks_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_PERK+"' and ep.emp_id >0");
			sbQuery.append(" and to_date(financial_year_start::text,'yyyy-MM-dd')=? and to_date(financial_year_end::text,'yyyy-MM-dd')=? ");
			if(uF.parseToInt(getStrSelectedEmpId()) > 0) {
				sbQuery.append(" and ep.emp_id = "+uF.parseToInt(getStrSelectedEmpId())+" ");
			}
			if (uF.parseToInt(getF_strWLocation()) > 0 || uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and ep.emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
				if (uF.parseToInt(getF_strWLocation()) > 0) {
					sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation())+" ");
				}
				if (uF.parseToInt(getF_org()) > 0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org())+" ");
				}
				sbQuery.append(")");
			}
			if(strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
			}
			sbQuery.append(" order by ep.entry_date desc ");			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			List<List<String>> alReport = new ArrayList<List<String>>(); 
			int nCount=0;
			while(rs.next()) {
				if(rs.getInt("emp_id")<0) {
					continue;
				}
				
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("perks_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("perks_id")+"_"+strSessionEmpId);
				if(checkEmpUserTypeList==null) checkEmpUserTypeList = new ArrayList<String>();
				
				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN)) {
					continue;
				}
				String userType = rs.getString("user_type");				
				if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && alList.contains(rs.getString("perks_id"))) {
					continue;
				} else if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("perks_id"))) {
					userType = strUserTypeId;
					alList.add(rs.getString("perks_id"));
				} else if(!checkEmpUserTypeList.contains(userType)) {
					continue;	
				}
				
				List<String> alInner = new ArrayList<String>();
				String strCurrId = hmEmpCurrency.get(rs.getString("emp_id"));
				Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
				if(hmCurrencyInner==null)hmCurrencyInner=new HashMap<String, String>();
				String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
				
				StringBuilder sb = new StringBuilder();
				
				sb.append("<div style=\"float:left;width:130px;margin-top:1px;padding-right:8px\" id=\"myDiv" + nCount + "\" >");
				if(deniedList.contains(rs.getString("perks_id"))) {
					/*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
					
				} else if(1==rs.getInt("approval_1") && 1==rs.getInt("approval_2")) {
					/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
					
				} else if(uF.parseToInt(hmAnyOneApproval.get(rs.getString("perks_id")))==1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("perks_id")))==rs.getInt("approval_1") && uF.parseToInt(hmAnyOneApproval.get(rs.getString("perks_id")))==rs.getInt("approval_2")) {
					/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("perks_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("perks_id")+"_"+userType)) && uF.parseToInt(hmNextApproval.get(rs.getString("perks_id")))>0) {
					/*sb.append("<img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" />&nbsp;");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"click to approve/deny\"></i>&nbsp;");
					sb.append("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" onclick=\"approveDeny('1','"+rs.getString("perks_id")+"','"+userType+"');\"></i>&nbsp;");
					sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"approveDeny('-1','"+rs.getString("perks_id")+"','"+userType+"');\"></i>");
					
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("perks_id")))<uF.parseToInt(hmMemNextApproval.get(rs.getString("perks_id")+"_"+userType)) || (uF.parseToInt(hmNextApproval.get(rs.getString("perks_id")))==0 && uF.parseToInt(hmNextApproval.get(rs.getString("perks_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("perks_id")+"_"+userType)))) {
					if(rs.getInt("approval_1")==0 && rs.getInt("approval_2")==0) {		
						if(strUserType.equalsIgnoreCase(ADMIN)  && uF.parseToInt(strUserTypeId) == uF.parseToInt(userType)) {
							 /*sb.append("<img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" />&nbsp;");*/
							sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"click to approve/deny\"></i>&nbsp;");
							
							sb.append("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" onclick=\"approveDeny('1','"+rs.getString("perks_id")+"','"+userType+"');\"></i>&nbsp;");
							sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"approveDeny('-1','"+rs.getString("perks_id")+"','"+userType+"');\"></i>");
						} else {
							/*sb.append("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
							sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Waiting for workflow\"></i>");
							
						}
					} else if(rs.getInt("approval_1")==1 && rs.getInt("approval_2")==1) {							
						/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
						
					} else {
						/*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
						
					}
				} else {
					if(strUserType.equalsIgnoreCase(ADMIN)) {
						/*sb.append("<img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" />&nbsp;");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"click to approve/deny\" style=\"color:#b71cc5\"></i>&nbsp;");
						
						sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"approveDeny('1','"+rs.getString("perks_id")+"','"+userType+"');\"></i>&nbsp;");
						sb.append("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"approveDeny('-1','"+rs.getString("perks_id")+"','"+userType+"');\"></i>");
					} else {
						/*sb.append("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Waiting for workflow\"></i>");
					}
				}
				
				sb.append("</div>");
				
				sb.append("<div style=\"float:left;width:85%\">");
				
				sb.append(hmEmpNames.get(rs.getString("emp_id")));
				sb.append(" has submitted a request for perks for "+uF.showData(hmPerkMap.get(rs.getString("perk_type_id")), "N/A")+
						" on "+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat())
						+" for "+ strCurrSymbol+rs.getString("perk_amount")
						+" specifying "
						+"\""+rs.getString("perk_purpose")+"\""
						);
				
				boolean isApproval1 = false;
				if(rs.getInt("approval_1")== -1) {
					sb.append(" has been denied by "+hmEmpNames.get(rs.getString("approval_1_emp_id")));
					isApproval1 = true;
				} else if(rs.getInt("approval_1")== 0) {
					sb.append(" is waiting for approval");
					isApproval1 = true;
				} else if(rs.getInt("approval_1")== 1) {
					sb.append(" is approved by "+hmEmpNames.get(rs.getString("approval_1_emp_id")) +" on "+ uF.getDateFormat(rs.getString("approval_1_date"), DBDATE, CF.getStrReportDateFormat()));
					isApproval1 = true;
				} 
				
				sb.append("  ["+uF.showData(hmUserTypeMap.get(userType), "")+"]");
				
				sb.append("</div>"); 
					
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>2) {
					
					if(CF.getStrDocRetriveLocation()==null) {
						sb.append("<a target=\"blank\" href=\"" +request.getContextPath()+ DOCUMENT_LOCATION + rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					} else {
						sb.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_PERKS+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					}
				}
				 
				alInner.add(sb.toString());
				
				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("perks_id"))!=null) {
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("perks_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("perks_id"))!=null) {
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("perks_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else {
					alInner.add("");
				}
				
				alReport.add(alInner);
				nCount++;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReport", alReport);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
	}
	
	
	public String addPerk() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			String[] strFinancialYear = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {				
				strFinancialYear = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			
				String fileName =null;
				if(getStrDocument()!=null) {
					if(CF.getStrDocSaveLocation()==null) {
						fileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getStrDocument(), getStrDocumentFileName(), getStrDocumentFileName(), CF);
					} else {
						fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_PERKS+"/"+I_DOCUMENT+"/"+strSessionEmpId, getStrDocument(), getStrDocumentFileName(), getStrDocumentFileName(), CF);
					} 
				}
				
				con = db.makeConnection(con);
				pst = con.prepareStatement("insert into emp_perks (financial_year_start, financial_year_end, perk_type_id, perk_purpose, perk_amount, emp_id, entry_date, ref_document,perk_month) values (?,?,?,?, ?,?,?,?, ?);");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getStrType()));
				pst.setString(4, getStrPurpose());
				pst.setDouble(5, uF.parseToDouble(getStrAmount()));
				pst.setInt(6, uF.parseToInt(strSessionEmpId));
				pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(8, fileName);
				pst.setInt(9, uF.parseToInt(getStrMonth()));
				int x = pst.executeUpdate();
				pst.close();
				
				if(x>0) {
					String perks_id=null;
					pst = con.prepareStatement("select max(perks_id)as perks_id from emp_perks");
					rs=pst.executeQuery();
					while(rs.next()) {
						perks_id=rs.getString("perks_id");
					}
					rs.close();
					pst.close();
					List<String> alManagers = null;
					if(uF.parseToBoolean(CF.getIsWorkFlow())) {
						alManagers = insertLeaveApprovalMember(con,pst,rs,perks_id,uF);
					}
					session.setAttribute(MESSAGE, SUCCESSM+"Perks Applied Successfully."+END);
				} else {
					session.setAttribute(MESSAGE, ERRORM+"Perks Applied Failed."+END);
				}
			}
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Perks applied failed."+END);
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
	}
	
	private List<String> insertLeaveApprovalMember(Connection con,PreparedStatement pst, ResultSet rs, String perks_id, UtilityFunctions uF) {
		List<String> alManagers = new ArrayList<String>();
		try {
			Map<String, String> hmEmpUserTypeId = CF.getEmployeeIdUserTypeIdMap(con);
			Map<String, String> hmUserType = CF.getUserTypeMap(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			
			pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
			" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
			pst.setInt(1,uF.parseToInt(getPolicy_id()));
			rs=pst.executeQuery();
			
			Map<String,List<String>> hmMemberMap=new LinkedHashMap<String, List<String>>();
			while(rs.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("member_type"));
				innerList.add(rs.getString("member_id"));
				innerList.add(rs.getString("member_position"));
				innerList.add(rs.getString("work_flow_mem"));
				innerList.add(rs.getString("work_flow_member_id"));
				
				hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
			}
			rs.close();
			pst.close();
			
			String strDomain = request.getServerName().split("\\.")[0];
			Iterator<String> it=hmMemberMap.keySet().iterator();
			while(it.hasNext()) {
				String work_flow_member_id=it.next();
				List<String> innerList=hmMemberMap.get(work_flow_member_id);
				
				int memid=uF.parseToInt(innerList.get(1)); 
				String empid=request.getParameter(innerList.get(3)+memid);
				
				if(empid!=null && !empid.equals("")) {
					int userTypeId = memid;
					if(uF.parseToInt(innerList.get(0)) == 3) {
						userTypeId = uF.parseToInt(hmEmpUserTypeId.get(empid));
					}
					pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
							"work_flow_mem_id,is_approved,status,user_type_id)" +
							"values(?,?,?,?, ?,?,?,?, ?)");
					pst.setInt(1,uF.parseToInt(empid));
					pst.setInt(2,uF.parseToInt(perks_id));
					pst.setString(3,WORK_FLOW_PERK);
					pst.setInt(4,uF.parseToInt(innerList.get(0)));
					pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
					pst.setInt(6,uF.parseToInt(innerList.get(4)));
					pst.setInt(7,0);
					pst.setInt(8,0);
					pst.setInt(9,userTypeId);
					pst.execute();
					pst.close();
					
					String alertData = "<div style=\"float: left;\"> Received a new Perk Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b> amount "+uF.formatIntoTwoDecimal(uF.parseToDouble(getStrAmount()))+". ["+hmUserType.get(userTypeId+"")+"] </div>";
					String strSubAction = "";
					String alertAction = "";
					if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD)) || userTypeId == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
						if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD))) {
							strSubAction = "&currUserType="+hmUserType.get(userTypeId+"");
						}
						alertAction = "TeamRequests.action?pType=WR&callFrom=NotiApplyPerk"+strSubAction;
					} else {
						alertAction = "PayApprovals.action?pType=WR&callFrom=NotiApplyPerk"+strSubAction;
					}
					
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(empid);
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(userTypeId+"");
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
					if(!alManagers.contains(empid)) {
						alManagers.add(empid);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return alManagers;
	}

	public String updatePerk() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			String[] strFinancialYear = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {				
				strFinancialYear = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			
				String fileName =null;
				if(getStrDocument()!=null) {
				
					if(CF.getStrDocSaveLocation()==null) {
						fileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getStrDocument(), getStrDocumentFileName(), getStrDocumentFileName(), CF);
					} else {
						fileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_PERKS+"/"+I_DOCUMENT+"/"+strSessionEmpId, getStrDocument(), getStrDocumentFileName(), getStrDocumentFileName(), CF);
					} 
				}
				con = db.makeConnection(con);
				pst = con.prepareStatement("update emp_perks set financial_year_start=?, financial_year_end=?, perk_type_id=?, perk_purpose=?, perk_amount=?,perk_month=? where perks_id=?");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getStrType()));
				pst.setString(4, getStrPurpose());
				pst.setDouble(5, uF.parseToDouble(getStrAmount()));
				pst.setInt(6, uF.parseToInt(getStrMonth()));
				pst.setInt(7, uF.parseToInt(getStrId()));
				pst.execute();
				pst.close();
				
				if(getStrDocument()!=null) {
					pst = con.prepareStatement("update emp_perks set ref_document=? where perks_id=?");
					pst.setString(1, fileName);
					pst.setInt(2, uF.parseToInt(getStrId()));
					pst.execute();
					pst.close();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
	}
	
	public String viewPerk(String strE) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from emp_perks where perks_id=?");
			pst.setInt(1, uF.parseToInt(strE));
			rs = pst.executeQuery();
			if(rs.next()) {
				setFinancialYear(uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, DATE_FORMAT) + "-" + uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, DATE_FORMAT));
				setStrType(rs.getString("perk_type_id"));
				setStrPurpose(rs.getString("perk_purpose"));
				setStrAmount(rs.getString("perk_amount"));
				setStrId(strE);
				
				StringBuilder sbDoc = new StringBuilder();
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>0) {
					if(CF.getStrDocRetriveLocation()==null) {
						sbDoc.append("<a target=\"blank\" href=\"" +request.getContextPath()+ DOCUMENT_LOCATION + rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					} else {
						sbDoc.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_PERKS+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					}
				}
				setStrViewDocument(sbDoc.toString());
				
				setStrMonth(rs.getString("perk_month"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public String getStrPurpose() {
		return strPurpose;
	}

	public void setStrPurpose(String strPurpose) {
		this.strPurpose = strPurpose;
	}

	public String getStrAmount() {
		return strAmount;
	}

	public void setStrAmount(String strAmount) {
		this.strAmount = strAmount;
	}

	public String getStrFrom() {
		return strFrom;
	}

	public void setStrFrom(String strFrom) {
		this.strFrom = strFrom;
	}

	public String getStrTo() {
		return strTo;
	}

	public void setStrTo(String strTo) {
		this.strTo = strTo;
	}

	public List<FillPerkType> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<FillPerkType> typeList) {
		this.typeList = typeList;
	}

	public String getStrType() {
		return strType;
	}

	public void setStrType(String strType) {
		this.strType = strType;
	}

	public String getStrId() {
		return strId;
	}

	public void setStrId(String strId) {
		this.strId = strId;
	}

	public File getStrDocument() {
		return strDocument;
	}

	public void setStrDocument(File strDocument) {
		this.strDocument = strDocument;
	}

	public String getStrViewDocument() {
		return strViewDocument;
	}

	public void setStrViewDocument(String strViewDocument) {
		this.strViewDocument = strViewDocument;
	}

	public String getStrSessionEmpId() {
		return strSessionEmpId;
	}

	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
	}

	public String getStrDocumentFileName() {
		return strDocumentFileName;
	}

	public void setStrDocumentFileName(String strDocumentFileName) {
		this.strDocumentFileName = strDocumentFileName;
	}

	public String getPolicy_id() {
		return policy_id;
	}

	public void setPolicy_id(String policy_id) {
		this.policy_id = policy_id;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String getStrSelectedEmpId() {
		return strSelectedEmpId;
	}

	public void setStrSelectedEmpId(String strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}

	public List<FillEmployee> getEmpNamesList() {
		return empNamesList;
	}

	public void setEmpNamesList(List<FillEmployee> empNamesList) {
		this.empNamesList = empNamesList;
	}

	public String getStrStartDate() {
		return strStartDate;
	}

	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}

	public String getStrEndDate() {
		return strEndDate;
	}

	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
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

	public String getF_financialYear() {
		return f_financialYear;
	}

	public void setF_financialYear(String f_financialYear) {
		this.f_financialYear = f_financialYear;
	}

	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
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

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

}
