package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import ChartDirector.AngularMeter;

import com.konnect.jpms.charts.SemiCircleMeter;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillPeriod;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeePerformance extends ActionSupport implements ServletRequestAware, IStatements  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7114035908350012346L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	String strBaseUserType;
	String strSessionEmpId;
	
	private String submit;
	private String[] strEmpId;
	private String dataType;
	private String strEmpIds1;
	private String filterParam1;
	
	private String f_strWLocation;
	private String f_department;
	private String f_service;
	private String f_level;
	private String f_org;
	
	private List<FillOrganisation> organisationList;
	private List<FillWLocation> wLocationList;
	private List<FillServices> serviceList;
	private List<FillDepartment> departmentList;
	
	private String level;
	
	private List<FillLevel> levelList;
	private List<FillPayCycles> payCycleList;
	
	private List<FillEmployee> employeeList;
	
//	private List<List<String>> attriblist;
	
	private String dateParam;
	private String []filterParam;
	private String attribParam;
	
	private String checkedReview;
	private String checkedGoalKRATarget;
	private String checkedGoal;
	private String checkedKRA;
	private String checkedTarget;
	private String checkedAttribute;
	
	private List<FillPeriod> periodList;
	private String period;
	private String strStartDate;
	private String strEndDate;
	private static Logger log = Logger.getLogger(EmployeePerformance.class);
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession(true);
		strUserType = (String)session.getAttribute(USERTYPE);
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, PEmployeePerformance);
		request.setAttribute(TITLE, TEmployeePerformance);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-cubes\"></i><a href=\"TeamPerformance.action\" style=\"color: #3c8dbc;\"> Analytics</a></li>" +
			"<li class=\"active\">Team KPI</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());*/
		
		
		if(getF_org()==null) {
			setF_org((String)session.getAttribute(ORGID));
			setF_strWLocation((String)session.getAttribute(WLOCATIONID));
		} else if(uF.parseToInt(getF_org()) == uF.parseToInt(((String)session.getAttribute(ORGID))) && getF_strWLocation() == null) {
			setF_strWLocation((String)session.getAttribute(WLOCATIONID));
		}
		
		if(getDataType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setDataType("MYTEAM");
		}
		
		if(getStrEmpIds1() != null && !getStrEmpIds1().equals("") && !getStrEmpIds1().equalsIgnoreCase("null")) {
			setStrEmpId(getStrEmpIds1().split(","));
		}
		
		if(getFilterParam1() != null && !getFilterParam1().equals("") && !getFilterParam1().equalsIgnoreCase("null")) {
			setFilterParam(getFilterParam1().split(","));
		}
		
		String []arrEnabledModules = CF.getArrEnabledModules();
		request.setAttribute("arrEnabledModules", arrEnabledModules);
		
		String strD1 = "", strD2 = "";
		for(int i=0; getFilterParam()!=null && i<getFilterParam().length; i++) {
			if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("LH")) {
				request.setAttribute("LH", "LH");
			}
			if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("REVIEW")) {
				request.setAttribute("REVIEW", "REVIEW");
				setCheckedReview("REVIEW");
			}
			if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("GOAL_KRA_TARGET")) {
				request.setAttribute("GOAL_KRA_TARGET", "GOAL_KRA_TARGET");
				setCheckedGoalKRATarget("GOAL_KRA_TARGET");
			}
			/*if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("GOAL")) {
				request.setAttribute("GOAL", "GOAL");
				setCheckedGoal("GOAL");
			}
			if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("KRA")) {
				request.setAttribute("KRA", "KRA");
				setCheckedKRA("KRA");
			}
			if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("TARGET")) {
				request.setAttribute("TARGET", "TARGET");
				setCheckedTarget("TARGET");
			}*/
			if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("AT")) {
				request.setAttribute("AT", "AT");
				setCheckedAttribute("AT");
			}
		}
		
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
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L2Y")) {
				strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 730)+"", DBDATE, DATE_FORMAT);
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L5Y")) {
				strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1825)+"", DBDATE, DATE_FORMAT);
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L10Y")) {
				strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 3650)+"", DBDATE, DATE_FORMAT);
			}
		} else if(getDateParam()!=null && uF.parseToInt(getDateParam())==2) {
			strD1 = getStrStartDate();
			strD2 = getStrEndDate();
		} else if(getDateParam()==null || getDateParam().equalsIgnoreCase("null")) {
			strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
			strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 30)+"", DBDATE, DATE_FORMAT);

			setDateParam("2");
		}
//		System.out.println("getDateParam() ===>> " + getDateParam());
		
		setStrStartDate(strD1);
		setStrEndDate(strD2);
		
		loadLists(uF);
		if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) {
			getElementList();
			getAttributeList1();
		}

		return SUCCESS;
	}
	
	
	public void loadLists(UtilityFunctions uF) {
		
		levelList = new FillLevel(request).fillLevel();
		payCycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		periodList = new FillPeriod().fillPeriod(2);
		if(strUserType!=null && strUserType.equals(ADMIN)) {
			employeeList = new FillEmployee(request).fillEmployeeName(strUserType, strSessionEmpId, session);
		} else if(getDataType()!=null && getDataType().equals(strBaseUserType)) {
			employeeList = new FillEmployee(request).fillEmployeeName(strBaseUserType, strSessionEmpId, uF.parseToInt(getF_org()), uF.parseToInt(getF_strWLocation()), session);
		} else {
			employeeList = new FillEmployee(request).fillEmployeeName(strUserType, strSessionEmpId, uF.parseToInt(getF_org()), uF.parseToInt(getF_strWLocation()), session);
		}
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		getSelectedFilter(uF);
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if((strUserType != null && !strUserType.equals(MANAGER)) || (getDataType() != null && getDataType().equals(strBaseUserType))) {
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
					hmFilter.put("ORGANISATION", "All Organisations");
				}
			} else {
				hmFilter.put("ORGANISATION", "All Organisations");
			}
		}
		
		alFilter.add("PERIOD");
		if(getDateParam()!=null && uF.parseToInt(getDateParam())==1) {
			if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1W")) {
				hmFilter.put("PERIOD", "Last 1 Week");
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1M")) {
				hmFilter.put("PERIOD", "Last 1 Month");
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L3M")) {
				hmFilter.put("PERIOD", "Last 3 Months");
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L6M")) {
				hmFilter.put("PERIOD", "Last 6 Months");
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1Y")) {
				hmFilter.put("PERIOD", "Last 1 Year");
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L2Y")) {
				hmFilter.put("PERIOD", "Last 2 Years");
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L5Y")) {
				hmFilter.put("PERIOD", "Last 5 Years");
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L10Y")) {
				hmFilter.put("PERIOD", "Last 10 Years");
			}
		} else if(getDateParam()!=null && uF.parseToInt(getDateParam())==2) {
			hmFilter.put("PERIOD", "From: " + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DATE_FORMAT_STR) + "  To: " + uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DATE_FORMAT_STR));
		}
		
		String selectedFilter= CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	public List<List<String>> getAttribsList(Connection con) {
		List<List<String>> attriblist = new ArrayList<List<String>>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			if(getAttribParam() != null && !getAttribParam().equals("") && !getAttribParam().equalsIgnoreCase("null")) {
				pst = con.prepareStatement("select arribute_id,attribute_name from appraisal_attribute where arribute_id in("+ getAttribParam() +") order by attribute_name");
			} else {
				pst = con.prepareStatement("select arribute_id,attribute_name from appraisal_attribute order by attribute_name");
			}
			rs = pst.executeQuery();
			List<String> checkAttribute=new ArrayList<String>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString(1));
				innerList.add(rs.getString(2));
				attriblist.add(innerList);
				
				checkAttribute.add(rs.getString("arribute_id"));
			}
			rs.close();
			pst.close();
//			System.out.println("attriblist ::::::::::::::"+attriblist.toString());
			request.setAttribute("checkAttribute", checkAttribute);
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
		return attriblist;
	}
	
	public void getAttributeList1() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
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
	

	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}
	
	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}
	
	public List<FillPayCycles> getPayCycleList() {
		return payCycleList;
	}
	
	public void setPayCycleList(List<FillPayCycles> payCycleList) {
		this.payCycleList = payCycleList;
	}
	
	public String getLevel() {
		return level;
	}
	
	public void setLevel(String level) {
		this.level = level;
	}
	
	public String[] getStrEmpId() {
		return strEmpId;
	}
	
	public void setStrEmpId(String[] strEmpId) {
		this.strEmpId = strEmpId;
	}
	
	public String getF_strWLocation() {
		return f_strWLocation;
	}
	
	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}
	
	public String getF_department() {
		return f_department;
	}
	
	public void setF_department(String f_department) {
		this.f_department = f_department;
	}
	
	public String getF_service() {
		return f_service;
	}
	
	public void setF_service(String f_service) {
		this.f_service = f_service;
	}
	
	public String getF_level() {
		return f_level;
	}

	public void setF_level(String f_level) {
		this.f_level = f_level;
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
	
	public List<FillEmployee> getEmployeeList() {
		return employeeList;
	}
	
	public void setEmployeeList(List<FillEmployee> employeeList) {
		this.employeeList = employeeList;
	}
	
	public String getDateParam() {
		return dateParam;
	}
	
	public void setDateParam(String dateParam) {
		this.dateParam = dateParam;
	}
	
	public String[] getFilterParam() {
		return filterParam;
	}
	
	public void setFilterParam(String[] filterParam) {
		this.filterParam = filterParam;
	}
	
	public List<FillPeriod> getPeriodList() {
		return periodList;
	}
	
	public void setPeriodList(List<FillPeriod> periodList) {
		this.periodList = periodList;
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

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getAttribParam() {
		return attribParam;
	}

	public void setAttribParam(String attribParam) {
		this.attribParam = attribParam;
	}

	public String getCheckedReview() {
		return checkedReview;
	}

	public void setCheckedReview(String checkedReview) {
		this.checkedReview = checkedReview;
	}

	public String getCheckedGoal() {
		return checkedGoal;
	}

	public void setCheckedGoal(String checkedGoal) {
		this.checkedGoal = checkedGoal;
	}

	public String getCheckedKRA() {
		return checkedKRA;
	}

	public void setCheckedKRA(String checkedKRA) {
		this.checkedKRA = checkedKRA;
	}

	public String getCheckedTarget() {
		return checkedTarget;
	}

	public void setCheckedTarget(String checkedTarget) {
		this.checkedTarget = checkedTarget;
	}

	public String getCheckedAttribute() {
		return checkedAttribute;
	}

	public void setCheckedAttribute(String checkedAttribute) {
		this.checkedAttribute = checkedAttribute;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}


	public String getSubmit() {
		return submit;
	}


	public void setSubmit(String submit) {
		this.submit = submit;
	}


	public String getStrEmpIds1() {
		return strEmpIds1;
	}


	public void setStrEmpIds1(String strEmpIds1) {
		this.strEmpIds1 = strEmpIds1;
	}


	public String getFilterParam1() {
		return filterParam1;
	}


	public void setFilterParam1(String filterParam1) {
		this.filterParam1 = filterParam1;
	}


	public String getCheckedGoalKRATarget() {
		return checkedGoalKRATarget;
	}


	public void setCheckedGoalKRATarget(String checkedGoalKRATarget) {
		this.checkedGoalKRATarget = checkedGoalKRATarget;
	}
	
	
}
