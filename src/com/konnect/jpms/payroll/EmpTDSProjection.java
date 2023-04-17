package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmpTDSProjection extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	public CommonFunctions CF;
	public String strUserType;
	public String strSessionEmpId;

	String financialYear;
	List<FillFinancialYears> financialYearList;

	List<FillPayCycles> paycycleList;
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;

	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;

	String exportType;

	String proPage;
	String minLimit;

	String strSearch;

	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/payroll/EmpTDSProjection.jsp");
		request.setAttribute(TITLE, "TDS Projection");

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());

		if (uF.parseToInt(getProPage()) == 0) {
			setProPage("1");
		}

		if (getF_org() == null) {
			setF_org((String) session.getAttribute(ORGID));
		}

		getSearchAutoCompleteData(uF);

		viewEmpTDSProjection(CF);

		return loadEmpTDSProjectionFilter(uF);
	}

	private void getSearchAutoCompleteData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			String[] strFianacialDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
				strFianacialDates = getFinancialYear().split("-");
				strFinancialYearStart = strFianacialDates[0];
				strFinancialYearEnd = strFianacialDates[1];
			} else {
				strFianacialDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFianacialDates[0] + "-" + strFianacialDates[1]);
				strFinancialYearStart = strFianacialDates[0];
				strFinancialYearEnd = strFianacialDates[1];
			}

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			

			SortedSet<String> setSearchList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("SELECT eod.emp_id,epd.emp_fname,epd.emp_mname,epd.emp_lname,epd.empcode,eod.depart_id,epd.emp_pan_no,epd.employment_end_date,"
					+ "epd.joining_date,epd.emp_address1,epd.emp_city_id, epd.emp_gender FROM employee_official_details eod, employee_personal_details epd "
					+ "WHERE eod.emp_id > 0 and epd.emp_per_id=eod.emp_id and eod.emp_id in (select distinct emp_id from attendance_details "
					+ "where to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ?) and eod.emp_id in(select emp_id from "
					+ "emp_salary_details where salary_head_id=" + TDS + " and isdisplay=true and is_approved=true) "
					+ "and (epd.employment_end_date is null or epd.employment_end_date<=?) ");
			if (getF_level() != null && getF_level().length > 0) {
				sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "
						+ StringUtils.join(getF_level(), ",") + ") ) ");
			}
			if (getF_department() != null && getF_department().length > 0) {
				sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
			}
			if (getF_service() != null && getF_service().length > 0) {
				sbQuery.append(" and (");
				for (int j = 0; j < getF_service().length; j++) {
					sbQuery.append(" eod.service_id like '%," + getF_service()[j] + ",%'");

					if (j < getF_service().length - 1) {
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" ) ");

			}
			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
				sbQuery.append(" and wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(WLOCATION_ACCESS) != null) {
				sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
			}
			if (uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and org_id = " + uF.parseToInt(getF_org()));
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(ORG_ACCESS) != null) {
				sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
			}
			sbQuery.append(" order by epd.emp_fname,epd.emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst=Search==" + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				setSearchList.add(rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();

			StringBuilder sbData = null;
			Iterator<String> it = setSearchList.iterator();
			while (it.hasNext()) {
				String strData = it.next();
				if (sbData == null) {
					sbData = new StringBuilder();
					sbData.append("\"" + strData + "\"");
				} else {
					sbData.append(",\"" + strData + "\"");
				}
			}

			if (sbData == null) {
				sbData = new StringBuilder();
			}
			request.setAttribute("sbData", sbData.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private String loadEmpTDSProjectionFilter(UtilityFunctions uF) {
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);

		if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String) session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String) session.getAttribute(ORG_ACCESS));
		} else {
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);

		getSelectedFilter(uF);

		return LOAD;
	}

	public void viewEmpTDSProjection(CommonFunctions CF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			String[] strFianacialDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
				strFianacialDates = getFinancialYear().split("-");
				strFinancialYearStart = strFianacialDates[0];
				strFinancialYearEnd = strFianacialDates[1];
			} else {
				strFianacialDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFianacialDates[0] + "-" + strFianacialDates[1]);
				strFinancialYearStart = strFianacialDates[0];
				strFinancialYearEnd = strFianacialDates[1];
			}
			// System.out.println("strFinancialYearStart====>"+strFinancialYearStart+"----strFinancialYearEnd===>"+strFinancialYearEnd);
			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("SELECT count(eod.emp_id) as empCount FROM employee_official_details eod, employee_personal_details epd "
					+ "WHERE eod.emp_id > 0 and epd.emp_per_id=eod.emp_id and eod.emp_id in (select distinct emp_id from attendance_details "
					+ "where to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ?) and eod.emp_id in(select emp_id from "
					+ "emp_salary_details where salary_head_id=" + TDS + " and isdisplay=true and is_approved=true) "
					+ "and (epd.employment_end_date is null or epd.employment_end_date<=?) ");
			if (getF_level() != null && getF_level().length > 0) {
				sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "
						+ StringUtils.join(getF_level(), ",") + ") ) ");
			}
			if (getF_department() != null && getF_department().length > 0) {
				sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
			}
			if (getF_service() != null && getF_service().length > 0) {
				sbQuery.append(" and (");
				for (int j = 0; j < getF_service().length; j++) {
					sbQuery.append(" eod.service_id like '%," + getF_service()[j] + ",%'");

					if (j < getF_service().length - 1) {
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" ) ");

			}
			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
				sbQuery.append(" and wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(WLOCATION_ACCESS) != null) {
				sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
			}
			if (uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and org_id = " + uF.parseToInt(getF_org()));
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(ORG_ACCESS) != null) {
				sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
			}
			if (getStrSearch() != null && !getStrSearch().trim().equals("") && !getStrSearch().trim().equalsIgnoreCase("NULL")) {
				if(flagMiddleName) {
					sbQuery.append(" and (upper(emp_fname)||' '||upper(emp_mname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%' or upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%')");
				} else {
					sbQuery.append(" and upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%'");
				}
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst=0==" + pst);
			rs = pst.executeQuery();
			int recCnt = 0;
			int pageCount = 0;
			while (rs.next()) {
				recCnt = rs.getInt("empCount");
				pageCount = rs.getInt("empCount") / 10;
				if (rs.getInt("empCount") % 10 != 0) {
					pageCount++;
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("pageCount", pageCount + "");
			request.setAttribute("recCnt", recCnt + "");

			sbQuery = new StringBuilder();
			sbQuery.append("SELECT eod.emp_id,epd.emp_fname,epd.emp_mname,epd.emp_lname,epd.empcode,eod.depart_id,epd.emp_pan_no,epd.employment_end_date,"
					+ "epd.joining_date,epd.emp_address1,epd.emp_city_id, epd.emp_gender FROM employee_official_details eod, employee_personal_details epd "
					+ "WHERE eod.emp_id > 0 and epd.emp_per_id=eod.emp_id and eod.emp_id in (select distinct emp_id from attendance_details "
					+ "where to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ?) and eod.emp_id in(select emp_id from "
					+ "emp_salary_details where salary_head_id=" + TDS + " and isdisplay=true and is_approved=true) "
					+ "and (epd.employment_end_date is null or epd.employment_end_date<=?) ");
			if (getF_level() != null && getF_level().length > 0) {
				sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "
						+ StringUtils.join(getF_level(), ",") + ") ) ");
			}
			if (getF_department() != null && getF_department().length > 0) {
				sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
			}
			if (getF_service() != null && getF_service().length > 0) {
				sbQuery.append(" and (");
				for (int j = 0; j < getF_service().length; j++) {
					sbQuery.append(" eod.service_id like '%," + getF_service()[j] + ",%'");

					if (j < getF_service().length - 1) {
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" ) ");
			}
			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
				sbQuery.append(" and wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(WLOCATION_ACCESS) != null) {
				sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
			}
			if (uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and org_id = " + uF.parseToInt(getF_org()));
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(ORG_ACCESS) != null) {
				sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
			}
			if (getStrSearch() != null && !getStrSearch().trim().equals("") && !getStrSearch().trim().equalsIgnoreCase("NULL")) {
				if(flagMiddleName) {
					sbQuery.append(" and (upper(emp_fname)||' '||upper(emp_mname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%' or upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%')");
				} else {
					sbQuery.append(" and upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%'");
				}
			}
			sbQuery.append(" order by epd.emp_fname,epd.emp_lname");
			int intOffset = uF.parseToInt(minLimit);
			sbQuery.append(" limit 10 offset " + intOffset + "");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst=1==" + pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmEmployeeMap = new LinkedHashMap<String, Map<String, String>>();
			while (rs.next()) {
				Map<String, String> hmEmpInner = new HashMap<String, String>();
			//	String strMiddleName = (rs.getString("emp_mname") != null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+ " " : "";
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				String strEmpName = rs.getString("emp_fname") +strEmpMName+" " + rs.getString("emp_lname");
				hmEmpInner.put("EMP_NAME", strEmpName);
				hmEmpInner.put("EMP_CODE", rs.getString("empcode"));
				hmEmpInner.put("EMP_JOINING_DATE", uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				hmEmpInner.put("EMP_PAN_NO", uF.showData(rs.getString("emp_pan_no"), ""));
				hmEmpInner.put("EMP_END_DATE", uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));
				hmEmpInner.put("EMP_GENDER", rs.getString("emp_gender"));

				hmEmployeeMap.put(rs.getString("emp_id"), hmEmpInner);
			}
			rs.close();
			pst.close();

			request.setAttribute("hmEmployeeMap", hmEmployeeMap);
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void getSelectedFilter(UtilityFunctions uF) {

		Map<String, String> hmFilter = new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if (getF_org() != null) {
			String strOrg = "";
			int k = 0;
			for (int i = 0; orgList != null && i < orgList.size(); i++) {
				if (getF_org().equals(orgList.get(i).getOrgId())) {
					if (k == 0) {
						strOrg = orgList.get(i).getOrgName();
					} else {
						strOrg += ", " + orgList.get(i).getOrgName();
					}
					k++;
				}
			}
			if (strOrg != null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}

		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}

		alFilter.add("LOCATION");
		if (getF_strWLocation() != null) {
			String strLocation = "";
			int k = 0;
			for (int i = 0; wLocationList != null && i < wLocationList.size(); i++) {
				for (int j = 0; j < getF_strWLocation().length; j++) {
					if (getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if (k == 0) {
							strLocation = wLocationList.get(i).getwLocationName();
						} else {
							strLocation += ", " + wLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if (strLocation != null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}

		alFilter.add("DEPARTMENT");
		if (getF_department() != null) {
			String strDepartment = "";
			int k = 0;
			for (int i = 0; departmentList != null && i < departmentList.size(); i++) {
				for (int j = 0; j < getF_department().length; j++) {
					if (getF_department()[j].equals(departmentList.get(i).getDeptId())) {
						if (k == 0) {
							strDepartment = departmentList.get(i).getDeptName();
						} else {
							strDepartment += ", " + departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if (strDepartment != null && !strDepartment.equals("")) {
				hmFilter.put("DEPARTMENT", strDepartment);
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}

		alFilter.add("SERVICE");
		if (getF_service() != null) {
			String strService = "";
			int k = 0;
			for (int i = 0; serviceList != null && i < serviceList.size(); i++) {
				for (int j = 0; j < getF_service().length; j++) {
					if (getF_service()[j].equals(serviceList.get(i).getServiceId())) {
						if (k == 0) {
							strService = serviceList.get(i).getServiceName();
						} else {
							strService += ", " + serviceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if (strService != null && !strService.equals("")) {
				hmFilter.put("SERVICE", strService);
			} else {
				hmFilter.put("SERVICE", "All SBUs");
			}
		} else {
			hmFilter.put("SERVICE", "All SBUs");
		}

		alFilter.add("LEVEL");
		if (getF_level() != null) {
			String strLevel = "";
			int k = 0;
			for (int i = 0; levelList != null && i < levelList.size(); i++) {
				for (int j = 0; j < getF_level().length; j++) {
					if (getF_level()[j].equals(levelList.get(i).getLevelId())) {
						if (k == 0) {
							strLevel = levelList.get(i).getLevelCodeName();
						} else {
							strLevel += ", " + levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if (strLevel != null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Level's");
			}
		} else {
			hmFilter.put("LEVEL", "All Level's");
		}

		String selectedFilter = CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);

	}

	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
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

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
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

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
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

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
	}

	public String getProPage() {
		return proPage;
	}

	public void setProPage(String proPage) {
		this.proPage = proPage;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}

	public String getStrSearch() {
		return strSearch;
	}

	public void setStrSearch(String strSearch) {
		this.strSearch = strSearch;
	}
}