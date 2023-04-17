package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPeriod;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class TeamPerformance implements IStatements, ServletRequestAware {

	
//	Map session;
	private HttpServletRequest request;
	HttpSession session;
	String strSessionEmpId;
	String strSessionOrgId;
	String strUserType;
	String strUserTypeId;
	CommonFunctions CF;
	
	private String search;
	private String fdate;
	private String tdate;
	private String strOrg;
	private List<FillOrganisation> orgList;
	private List<FillWLocation> workList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	
	private List<FillAttribute> attributeList;
	private List<FillPeriod> periodList;
	
	private String checkOrg;
	private String checkDepart;
	private String checkLocation;
	private String checkLevel;
	
	private String dateParam;
	private String period;
	private String strStartDate;
	private String strEndDate;
	
	private String strBaseUserType;
	private String dataType;

	public String execute() throws Exception {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID); 
		strSessionOrgId = (String) session.getAttribute(ORGID);
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) 
			return "login";

		UtilityFunctions uF=new UtilityFunctions();
		/*	request.setAttribute(PAGE, "/jsp/performance/TeamPerformance.jsp");
		request.setAttribute(TITLE, "Team Performance");
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-cubes\"></i><a href=\"TeamPerformance.action\" style=\"color: #3c8dbc;\"> Analytics</a></li>");
		if(strUserType != null && strUserType.equals(MANAGER)) {
			sbpageTitleNaviTrail.append("<li class=\"active\">Team Performance</li>");
		} else {
			sbpageTitleNaviTrail.append("<li class=\"active\">Performance</li>");
		}
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());*/
		
		String org = null;
		if(getStrOrg() == null) {
			org = strSessionOrgId;
			setStrOrg(strSessionOrgId);
//			System.out.println("orgList.get(0).getOrgId() ======== "+orgList.get(0).getOrgId());
		} else {
			org = getStrOrg();
		}
		if(getDataType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setDataType("MYTEAM");
		}
//		System.out.println("org ===>> " + org);
		
		if(strUserType != null && strUserType.equals(ADMIN)) {
			orgList = new FillOrganisation(request).fillOrganisation();
			workList = new FillWLocation(request).fillWLocation(org);
		} else {
			if(session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
				orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
				workList = new FillWLocation(request).fillWLocation((String)session.getAttribute(ORG_ACCESS), (String)session.getAttribute(WLOCATION_ACCESS));
			} else {
				orgList = new FillOrganisation(request).fillOrganisation(org);
				workList = new FillWLocation(request).fillWLocation(org, (String)session.getAttribute(WLOCATION_ACCESS));
			}
		}
//		System.out.println("getStrOrg() ======== "+getStrOrg());
		
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(org));
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(org));
		//System.out.println("getCheckLevel() ======== "+getCheckLevel());
//		StringBuilder lvlBuilder = new StringBuilder("");
//		for(int i = 0; levelList!= null && i<levelList.size(); i++) {
//			if(i==0)
//				lvlBuilder.append(levelList.get(i).getLevelId());
//			else
//				lvlBuilder.append(","+levelList.get(i).getLevelId());
//		}
//		if(lvlBuilder.toString().equals("")) {
//			lvlBuilder.append("0");
//			System.out.println("lvlBuilder append 0 ========"+lvlBuilder.toString());
//		}
//		System.out.println("lvlBuilder ========"+lvlBuilder.toString());
//		attributeList = new FillAttribute().fillAttribute(lvlBuilder.toString());
		attributeList = new FillAttribute(request).fillAttribute();
//		getEmployeeCount();
//		getEmployeeCount1();
		getElementList();
//		getAnalysisSummary();
		getAttributeList1();
//		getData();
		if(strUserType != null && strUserType.equals(MANAGER) && getDataType()!= null && !getDataType().equals(strBaseUserType)) {
			getManagerData();
		}
		periodList = new FillPeriod().fillPeriod(1);
//		if(search!=null) {
//			getReportData();
//		}
		
		
		return "success";

	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if((strUserType != null && !strUserType.equals(MANAGER)) || (getDataType() != null && getDataType().equals(strBaseUserType))) {
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
					hmFilter.put("ORGANISATION", "All Organisations");
				}
			} else {
				hmFilter.put("ORGANISATION", "All Organisations");
			}
		}
		
		alFilter.add("PERIOD");
		if(getDateParam()!=null && uF.parseToInt(getDateParam())==1) {
			if(getPeriod()!=null && getPeriod().equalsIgnoreCase("T")) {
				hmFilter.put("PERIOD", "Today");
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("Y")) {
				hmFilter.put("PERIOD", "Yesterday");
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1W")) {
				hmFilter.put("PERIOD", "Last 1 Week");
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1M")) {
				hmFilter.put("PERIOD", "Last 1 Month");
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L3M")) {
				hmFilter.put("PERIOD", "Last 3 Months");
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L6M")) {
				hmFilter.put("PERIOD", "Last 6 Months");
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1Y")) {
				hmFilter.put("PERIOD", "Last 1 Year");
			}
		} else if(getDateParam()!=null && uF.parseToInt(getDateParam())==2) {
			hmFilter.put("PERIOD", "From: " + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DATE_FORMAT_STR) + "  To: " + uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DATE_FORMAT_STR));
		}
		
		String selectedFilter= CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	
	private void getManagerData() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		ResultSet rs = null;
		try {
//			System.out.println("CHECK PARAM  =================  > "+getCheckParam());
			con = db.makeConnection(con);
			List<String> wLocList = new ArrayList<String>();
			List<String> dprtList = new ArrayList<String>();
			List<String> lvlList = new ArrayList<String>();
			List<String> orgnList = new ArrayList<String>();
			pst=con.prepareStatement("select od.org_id,eod.wlocation_id,depart_id,supervisor_emp_id,emp_id,dd.designation_id,ld.level_id from employee_personal_details epd join employee_official_details eod on (" +
					"epd.emp_per_id = eod.emp_id) join department_info di on (depart_id=dept_id) join grades_details gd on eod.grade_id=gd.grade_id " +
					"join designation_details dd on gd.designation_id=dd.designation_id join level_details ld on dd.level_id=ld.level_id join " +
					"org_details od  on ld.org_id=od.org_id where  is_alive= true and emp_per_id >0 and supervisor_emp_id = ? " +
					"union all " +
					"select od.org_id,eod.wlocation_id,depart_id,supervisor_emp_id,emp_id,dd.designation_id,ld.level_id from employee_personal_details epd join employee_official_details eod on (" +
					"epd.emp_per_id = eod.emp_id) join department_info di on (depart_id=dept_id) join grades_details gd on eod.grade_id=gd.grade_id " +
					"join designation_details dd on gd.designation_id=dd.designation_id join level_details ld on dd.level_id=ld.level_id join " +
					"org_details od  on ld.org_id=od.org_id where  is_alive= true and emp_per_id = ? order by supervisor_emp_id,emp_id"); //appraisal_attribute in ("+getCheckParam()+") and 
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			rs=pst.executeQuery();
			while(rs.next()) {
				if(!wLocList.contains(rs.getString("wlocation_id"))) {
					wLocList.add(rs.getString("wlocation_id"));
				}
				if(!orgnList.contains(rs.getString("org_id"))) {
					orgnList.add(rs.getString("org_id"));
				}
				if(!dprtList.contains(rs.getString("depart_id"))) {
					dprtList.add(rs.getString("depart_id"));
				}
				if(!lvlList.contains(rs.getString("level_id"))) {
					lvlList.add(rs.getString("level_id"));
				}
			}
			rs.close();
			pst.close();
			
			String wlocIds = getAppendData(wLocList);
			String departIds = getAppendData(dprtList);
			String levelIds = getAppendData(lvlList);
			String orgIds = getAppendData(orgnList);

			levelList = new FillLevel(request).fillLevelOrgIdAndLevelIds(orgIds, levelIds);
			workList = new FillWLocation(request).fillWLocationOrgIdAndWLocationIds(orgIds, wlocIds);
			departmentList = new FillDepartment(request).fillDepartmentOrgIdAndDepartIds(orgIds, departIds);
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public String getAppendData(List<String> strID) {
		StringBuilder sb = new StringBuilder();
		if (strID != null) {
			for (int i = 0; i < strID.size(); i++) {
				if (i == 0) {
					sb.append(strID.get(i));
				} else {
					sb.append("," + strID.get(i));
				}
			}
		} else {
			return null;
		}
		return sb.toString();
	}
	
	
	private void getEmployeeCount1() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		ResultSet rs = null;
		try {
//			System.out.println("CHECK PARAM  =================  > "+getCheckParam());
			con = db.makeConnection(con);
			pst=con.prepareStatement("select count(*)as count from (select emp_id from appraisal_question_answer" +
					" where attempted_on between ? and ? group by emp_id) as a"); //appraisal_attribute in ("+getCheckParam()+") and 
			pst.setDate(1, uF.getDateFormat(strStartDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strEndDate, DATE_FORMAT));
			rs=pst.executeQuery();
			String empCount="0";
			while(rs.next()) {
				empCount=rs.getString("count");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("empCount1",empCount);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getEmployeeCount() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst=con.prepareStatement("select count(*)as count from (select emp_id from appraisal_question_answer group by emp_id) as a");
			rs=pst.executeQuery();
			String empCount="0";
			while(rs.next()) {
				empCount=rs.getString("count");
			}
			rs.close();
			pst.close();
			request.setAttribute("empCount",empCount);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
//	public void getData() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rs = null;
//
//		try {
//			Map<String,String> mp=new HashMap<String,String>();
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from appraisal_element_attribute");
//			rs=pst.executeQuery();
//			while(rs.next()) {
//				mp.put(rs.getString("appraisal_attribute")+"element"+rs.getString("appraisal_element"), "");
//			}
//			request.setAttribute("mp",mp);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	public void getElementList() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_element order by appraisal_element_id");
			rs = pst.executeQuery();
			List<List<String>> elementouterList=new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("appraisal_element_id"));
				innerList.add(rs.getString("appraisal_element_name"));
				elementouterList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("elementouterList",elementouterList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void getAnalysisSummary() {
		UtilityFunctions uF = new UtilityFunctions();
		String strD1 = "", strD2 = "";
		
		if(getDateParam()!=null && uF.parseToInt(getDateParam())==1) {
			if(getPeriod()!=null && getPeriod().equalsIgnoreCase("T")) {
				strD2 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("Y")) {
				strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DATE_FORMAT);
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1W")) {
				strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 7)+"", DBDATE, DATE_FORMAT);
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1M")) {
				strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 30)+"", DBDATE, DATE_FORMAT);
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L3M")) {
				strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 90)+"", DBDATE, DATE_FORMAT);
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L6M")) {
				strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 180)+"", DBDATE, DATE_FORMAT);
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1Y")) {
				strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 365)+"", DBDATE, DATE_FORMAT);
			}
		} else if(getDateParam()!=null && uF.parseToInt(getDateParam())==2) {
			strD1 = getStrStartDate();
			strD2 = getStrEndDate();
		} else if(getDateParam()==null) {
			strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 181)+"", DBDATE, DATE_FORMAT);
			strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
			
			setStrStartDate(strD1);
			setStrEndDate(strD2);
			setDateParam("2");
		}
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		double totAverage=0;
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmAnalysisSummaryMap = new HashMap<String, String>();

			pst = con.prepareStatement("select *,((marks*100/weightage)/10) as average from(select sum(marks) as marks, sum(weightage) " +
					"as weightage,a.appraisal_element from (select appraisal_element,appraisal_attribute from appraisal_element_attribute " +
					"group by appraisal_element,appraisal_attribute) as a,appraisal_question_answer aqw where " +
					"a.appraisal_attribute=aqw.appraisal_attribute and weightage>0 group by a.appraisal_element ) as aa order by aa.appraisal_element");
			rs = pst.executeQuery();
			while (rs.next()) {
				totAverage += uF.parseToDouble(rs.getString("average"));
				hmAnalysisSummaryMap.put(rs.getString("appraisal_element"),rs.getString("average"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAnalysisSummaryMap",hmAnalysisSummaryMap);
			request.setAttribute("totAverage",""+totAverage);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	} 
	
	
	public void getAttributeList1() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			UtilityFunctions uF = new UtilityFunctions();
			String strD1 = "", strD2 = "";
			if(getDateParam()!=null && uF.parseToInt(getDateParam())==1) {
				if(getPeriod()!=null && getPeriod().equalsIgnoreCase("T")) {
					strD2 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
					strD1 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
				} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("Y")) {
					strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DATE_FORMAT);
					strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DATE_FORMAT);
				} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1W")) {
					strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
					strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 7)+"", DBDATE, DATE_FORMAT);
				} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1M")) {
					strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
					strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 30)+"", DBDATE, DATE_FORMAT);
				} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L3M")) {
					strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
					strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 90)+"", DBDATE, DATE_FORMAT);
				} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L6M")) {
					strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
					strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 180)+"", DBDATE, DATE_FORMAT);
				} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1Y")) {
					strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
					strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 365)+"", DBDATE, DATE_FORMAT);
				}
			} else if(getDateParam()!=null && uF.parseToInt(getDateParam())==2) {
				strD1 = getStrStartDate();
				strD2 = getStrEndDate();
			} else if(getDateParam()==null) {
				strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 181)+"", DBDATE, DATE_FORMAT);
				strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
				setDateParam("2");
			}
			setStrStartDate(strD1);
			setStrEndDate(strD2);
			
			getSelectedFilter(uF);
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select a.appraisal_element,a.appraisal_attribute,aa.attribute_name from " +
					" (select appraisal_element,appraisal_attribute from appraisal_element_attribute " +
					" group by appraisal_element,appraisal_attribute order by appraisal_element) as a, appraisal_attribute aa " +
					" where a.appraisal_attribute=aa.arribute_id");
			rs = pst.executeQuery();
			Map<String,List<List<String>>> hmElementAttribute=new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("appraisal_attribute"));
				innerList.add(rs.getString("attribute_name"));
				List<List<String>> attributeouterList=hmElementAttribute.get(rs.getString("appraisal_element"));
				if(attributeouterList==null) attributeouterList=new ArrayList<List<String>>();
				attributeouterList.add(innerList);
				hmElementAttribute.put(rs.getString("appraisal_element"), attributeouterList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmElementAttribute",hmElementAttribute);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String getSearch() {
		return search;
	}
	
	public void setSearch(String search) {
		this.search = search;
	}
	
	public List<FillOrganisation> getOrgList() {
		return orgList;
	}
	
	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}
	
	public String getFdate() {
		return fdate;
	}
	
	public void setFdate(String fdate) {
		this.fdate = fdate;
	}
	
	public String getTdate() {
		return tdate;
	}
	
	public void setTdate(String tdate) {
		this.tdate = tdate;
	}
	
	public String getStrOrg() {
		return strOrg;
	}
	
	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}
	
	public List<FillWLocation> getWorkList() {
		return workList;
	}
	
	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
	}
	
	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}
	
	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}
	
	public List<FillLevel> getLevelList() {
		return levelList;
	}
	
	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}
	
	public String getCheckOrg() {
		return checkOrg;
	}
	
	public void setCheckOrg(String checkOrg) {
		this.checkOrg = checkOrg;
	}
	
	public String getCheckDepart() {
		return checkDepart;
	}
	
	public void setCheckDepart(String checkDepart) {
		this.checkDepart = checkDepart;
	}
	
	public String getCheckLocation() {
		return checkLocation;
	}
	
	public void setCheckLocation(String checkLocation) {
		this.checkLocation = checkLocation;
	}
	
	public String getCheckLevel() {
		return checkLevel;
	}
	
	public void setCheckLevel(String checkLevel) {
		this.checkLevel = checkLevel;
	}
	
	public List<FillAttribute> getAttributeList() {
		return attributeList;
	}
	
	public void setAttributeList(List<FillAttribute> attributeList) {
		this.attributeList = attributeList;
	}
	
	public List<FillPeriod> getPeriodList() {
		return periodList;
	}
	
	public void setPeriodList(List<FillPeriod> periodList) {
		this.periodList = periodList;
	}
	
	public String getDateParam() {
		return dateParam;
	}
	
	public void setDateParam(String dateParam) {
		this.dateParam = dateParam;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
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

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	
}
