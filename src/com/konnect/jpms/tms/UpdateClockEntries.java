package com.konnect.jpms.tms;
 
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateClockEntries extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	public HttpSession session;
	public String strSessionEmpId;
	public String strUserType; 
	public String strBaseUserType;
//	String EMPID;
	String strReqServiceId = null;
	String strPay = null;
//	String strType = null;
//	String strPC = null;
	public String strD1 = null;
	public String strD2 = null;
	 
	private String strSelectedEmpId;
	private List<FillEmployee> empNamesList; 
	
	public CommonFunctions CF = null;
	String paycycle;
	private List<FillPayCycles> payCycleList;
	private String divid;
	
	private String strLocation;
	private String strDepartment;
	private String strLevel;
	
	private String f_org;
	private String[] f_strWLocation;
	private String[] f_department;
	private String[] f_level;
	
	private List<FillOrganisation> organisationList;
	private List<FillWLocation> wLocationList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList; 
	
	private String currUserType;
	String fromPage;
	
	


	public String execute() throws Exception {
		
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();		
		strUserType = (String) session.getAttribute(USERTYPE);
		
//		System.out.println("UCE/88--==UserType=="+strUserType);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		strPay = (String) request.getParameter("PAY");
//		strType = (String) request.getParameter("T");
//		strPC = (String) request.getParameter("PC");
		strD1 = (String) request.getParameter("D1");
		strD2 = (String) request.getParameter("D2");
		
//		System.out.println("strpaycycle1---in updateclock entries "+paycycle+"fromPage---"+fromPage);
		strReqServiceId = (String) request.getParameter("SID");

		if(strUserType!=null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))) {
			request.setAttribute(TITLE, "My Issues");
		} else {
			request.setAttribute(TITLE, "Clock On/Off Exceptions");
		}
		
		if(getF_org()==null || getF_org().equals("")) {
			setF_org((String)session.getAttribute(ORGID));
		}
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		
		String date = getStrDATE();
		if (date == null) {
			date = request.getParameter("DATE");
			setStrDATE(date);
		}

		String[] strPayCycleDates = null;
		
		if(strD1==null) {
			
			loadClockEntries(uF);
			String firstEmp = null;
			
			if(strUserType!=null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))) {
				setStrSelectedEmpId(strSessionEmpId);
			} else {
				
				if(getStrSelectedEmpId()==null && empNamesList!=null && empNamesList.size()>0) {
					firstEmp = empNamesList.get(0).getEmployeeId();
					setStrSelectedEmpId(firstEmp);
				} else {
					setStrSelectedEmpId(getStrSelectedEmpId());
				}
			}
			
			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
				String str = URLDecoder.decode(getPaycycle());
				setPaycycle(str);
				strPayCycleDates = getPaycycle().split("-");
				strD1 = strPayCycleDates[0];
				strD2 = strPayCycleDates[1];
			
			} else {
				    
//				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
				strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+"-"+strPayCycleDates[2]);
				strD1 = strPayCycleDates[0];
				strD2 = strPayCycleDates[1];
			}
		}
		
		String referer = request.getHeader("Referer");
		if (referer != null) {
			int index1 = referer.indexOf(request.getContextPath());
			int index2 = request.getContextPath().length();
			referer = referer.substring(index1 + index2 + 1);
		}
		
		if(getRedirectUrl()==null) {
			setRedirectUrl(referer);
		}
		
		String strDate = request.getParameter("strDate");
//		String strReqEmpID = request.getParameter("EMPID");
		
		String strReqEmpID = getStrSelectedEmpId();
		
		String strReqServiceId = request.getParameter("SERVICEID");
		
//		System.out.println("======= "+strPay+ " --- strReqEmpID ===>> " + strReqEmpID);
		getSalaryPaidEmployee(uF,strReqEmpID);
		if (strPay == null) {
			if(strUserType!=null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))) {
				request.setAttribute(PAGE, PUpdateClockEntries1Emp);
			} else {
				request.setAttribute(PAGE, PUpdateClockEntries1);
			}
			viewClockEntries(strReqEmpID, strReqServiceId, uF);
//			System.out.println("strPay viewClockEntries ======>>");
		} else {
			request.setAttribute(PAGE, PUpdateClockEntries);
			viewClockEntries(date, strDate, strReqEmpID, strReqServiceId);
//			System.out.println("viewClockEntries else ======>>");
		}
//		System.out.println("UCE/189--getStrEmpId()======>"+getStrEmpId());
		if (getStrEmpId() != null && getStrEmpId().length() > 0) {
			// updateClockEntries(strDate);
//			System.out.println("in update clock entries");
			updateClockEntries();
			if (strReqEmpID != null && !strReqEmpID.equalsIgnoreCase("NULL")) {
				
				// return APPROVE_PAYROLL;
			}
			
			//return UPDATE_CLOCK_ENTRIES;
			return "ajax";
		}
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
//		setEmpIds(strReqEmpID);

		return loadClockEntries(uF);
	}

	
	private void getSalaryPaidEmployee(UtilityFunctions uF, String strReqEmpID) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id from payroll_generation where emp_id>0 and paid_from=? and paid_to=? ");
			if(strUserType!=null && (strUserType.equalsIgnoreCase(MANAGER))) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
			}
			if(uF.parseToInt(strReqEmpID)>0) {
				sbQuery.append(" and emp_id = "+uF.parseToInt(strReqEmpID));
			}     
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where org_id = "+uF.parseToInt(getF_org())+") ");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and emp_id in (select emp_id from employee_official_details where org_id in ("+(String)session.getAttribute(ORG_ACCESS)+"))");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and emp_id in (select emp_id from employee_official_details where wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")) ");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+"))");
			}			
            sbQuery.append(" group by emp_id order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("1 pst======>"+pst); 
			rs = pst.executeQuery();
			List<String> alSalPaidEmpList = new ArrayList<String>();
			while (rs.next()) {
				if(!uF.parseToBoolean(hmFeatureStatus.get(F_AFTER_PAYROLL_PROCESS_ACTIVE_ABSENCE_AND_TIME))) {
					alSalPaidEmpList.add(rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("alSalPaidEmpList", alSalPaidEmpList);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from approve_attendance where approve_from>=? and approve_to<=?");
			if(strUserType!=null && (strUserType.equalsIgnoreCase(MANAGER))) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
			}
			if(uF.parseToInt(strReqEmpID)>0) {
				sbQuery.append(" and emp_id = "+uF.parseToInt(strReqEmpID));
			}     
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where org_id = "+uF.parseToInt(getF_org())+") ");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and emp_id in (select emp_id from employee_official_details where org_id in ("+(String)session.getAttribute(ORG_ACCESS)+"))");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and emp_id in (select emp_id from employee_official_details where wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")) ");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+"))");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
//			System.out.println("rs==>"+rs);
			List<String> alApproveClockEntrieEmp = new ArrayList<String>();
			while(rs.next()){
				if(!uF.parseToBoolean(hmFeatureStatus.get(F_AFTER_PAYROLL_PROCESS_ACTIVE_ABSENCE_AND_TIME))) {
					alApproveClockEntrieEmp.add(rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("alApproveClockEntrieEmp", alApproveClockEntrieEmp);
//			System.out.println("alApproveClockEntrieEmp=====>"+alApproveClockEntrieEmp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadClockEntries(UtilityFunctions uF) {
		
		/*payCycleList = new FillPayCycles(request).fillPayCycles(CF);
		levelList = new FillLevel(request).fillLevel();
		if (strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
			empNamesList = getEmployeeList(strD1);
		} else {
			empNamesList = new FillEmployee(request).fillEmployeeName(strD1, uF.parseToInt(getLevel()));
		}*/
		
		payCycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		
		empNamesList = getEmployeeList();
		
		getSelectedFilter(uF);
		
		if (strPay == null) {
			if(strUserType!=null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))) {
				return LOAD;
			} else {
				return "ghrload";
			}
		} else {
			return LOAD;
		}
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
					hmFilter.put("ORGANISATION", "All Organisations");
				}
			} else {
				hmFilter.put("ORGANISATION", "All Organisations");
			}
			
			alFilter.add("LOCATION");
			if(getStrLocation()!=null) {
				String strLocation="";
				int k=0;
				for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
						if(getStrLocation().equals(wLocationList.get(i).getwLocationId())) {
							if(k==0) {
								strLocation=wLocationList.get(i).getwLocationName();
							} else {
								strLocation+=", "+wLocationList.get(i).getwLocationName();
							}
							k++;
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
			if(getStrDepartment()!=null) {
				String strSbu="";
				int k=0;
				for(int i=0;departmentList!=null && i<departmentList.size();i++) {
					
						if(getStrDepartment().equals(departmentList.get(i).getDeptId())) {
							if(k==0) {
								strSbu=departmentList.get(i).getDeptName();
							} else {
								strSbu+=", "+departmentList.get(i).getDeptName();
							}
							k++;
						}
					
				}
				if(strSbu!=null && !strSbu.equals("")) {
					hmFilter.put("DEPARTMENT", strSbu);
				} else {
					hmFilter.put("DEPARTMENT", "All Departments");
				}
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
			
			alFilter.add("LEVEL");
			if(getStrLevel()!=null) {
				String strLevel="";
				int k=0;
				for(int i=0;levelList!=null && i<levelList.size();i++) {
						if(getStrLevel().equals(levelList.get(i).getLevelId())) {
							if(k==0) {
								strLevel = levelList.get(i).getLevelCodeName();
							} else {
								strLevel+=", "+levelList.get(i).getLevelCodeName();
							}
							k++;
						}
				}
				if(strLevel!=null && !strLevel.equals("")) {
					hmFilter.put("LEVEL", strLevel);
				} else {
					hmFilter.put("LEVEL", "All Levels");
				}
			} else {
				hmFilter.put("LEVEL", "All Levels");
			}
		}
		
		alFilter.add("PAYCYCLE");
		if(getPaycycle()!=null) {
			String strPaycycle = "";
			for(int i=0;payCycleList!=null && i<payCycleList.size();i++) {
				if(getPaycycle().equals(payCycleList.get(i).getPaycycleId())) {
					strPaycycle = payCycleList.get(i).getPaycycleName();
				}
			}
			if(strPaycycle!=null && !strPaycycle.equals("")) {
				hmFilter.put("PAYCYCLE", strPaycycle);
			} else {
				hmFilter.put("PAYCYCLE", "-");
			}
		} else {
			hmFilter.put("PAYCYCLE", "-");
		}
		if(strUserType != null && !strUserType.equals(EMPLOYEE)) {
			alFilter.add("EMP");
			if(getStrSelectedEmpId()!=null) {
				String strEmpName = "";
				for(int i=0;empNamesList!=null && i<empNamesList.size();i++) {
					if(getStrSelectedEmpId().equals(empNamesList.get(i).getEmployeeId())) {
						strEmpName = empNamesList.get(i).getEmployeeName();
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
		
		String selectedFilter= CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	
	public List<FillEmployee> getEmployeeList() {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus1 = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus1.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			//===start parvez on 26-07-2021===
			Map<String, String> hmFeaturesStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeaturesStatus);
			//===end parvez on 26-07-2021===
			
			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					" and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) ");
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && (getCurrUserType() == null || !getCurrUserType().equals(strBaseUserType))) {
				sbQuery.append("and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
			} else {
	//			if(uF.parseToInt(getF_org())>0) {
	//				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
	//			} else if((String)session.getAttribute(ORG_ACCESS)!=null) {
	//				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	//			}
	//            
	//            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	//            	sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	//            } else if((String)session.getAttribute(WLOCATION_ACCESS)!=null) {
	//            	sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	//			}
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(uF.parseToInt(getStrLocation())>0) {
		            sbQuery.append(" and wlocation_id in ("+getStrLocation()+") ");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            
	            if(uF.parseToInt(getStrDepartment())>0) {
	            	sbQuery.append(" and depart_id in ("+getStrDepartment()+") ");
	            }
	            if(uF.parseToInt(getStrLevel())>0) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+getStrLevel()+") ) ");
	            }            
			}
            sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("2 pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strEmpMName = "";
				
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rs.getString("emp_id"), rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"), ""));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	public String viewClockEntries(String strReqEmpID, String strServiceId, UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		StringBuilder sb = new StringBuilder();
		
		Map hmTimeSheet = new HashMap();
		List alTimeSheet = new ArrayList();
		List alService = new ArrayList();
		List alDt = new ArrayList();
		
		Map hmEmployeeNameMap = null;
		Map hmServicesMap = null;
		
		try {
//			System.out.println("in ===>> viewClockEntries");
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus1 = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus1.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			hmServicesMap = CF.getServicesMap(con, true);
			hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
			Map hmTardyType = CF.getTardyType(con);
			
			Map hmLeaves = CF.getActualLeaveDates(con, CF, uF, strD1, strD2, null, false, null);
			Map hmRowColour = CF.getTardyType(con);
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEndDates = CF.getWeekEndDateList(con, strD1, strD2, CF, uF,hmWeekEndHalfDates,null);
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF,strD1, strD2,alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEndDates,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			
			Map<String,String> hmHolidays = new HashMap<String,String>();
			Map<String,String> hmHolidayDates = new HashMap<String,String>();
			CF.getHolidayList(con, request,strD1, strD2, CF, hmHolidayDates, hmHolidays, true);
			
			request.setAttribute("hmWeekEndHalfDates", hmWeekEndHalfDates);
			request.setAttribute("hmWeekEndDates", hmWeekEndDates);
			request.setAttribute("hmEmpWlocation", hmEmpWlocation);
			request.setAttribute("alEmpCheckRosterWeektype", alEmpCheckRosterWeektype);
			request.setAttribute("hmRosterWeekEndDates", hmRosterWeekEndDates);
			request.setAttribute("hmHolidays", hmHolidays);
			request.setAttribute("hmHolidayDates", hmHolidayDates);
			
//			System.out.println("hmWeekEndHalfDates1===>"+ hmWeekEndHalfDates);
//			System.out.println("hmWeekEndDates1===>"+ hmWeekEndDates);
//			System.out.println("hmEmpWlocation1===>"+ hmEmpWlocation);
//			System.out.println("alEmpCheckRosterWeektype1===>"+ alEmpCheckRosterWeektype);
//			System.out.println("hmRosterWeekEndDates1===>"+ hmRosterWeekEndDates);
//			System.out.println("hmHolidays1===>"+ hmHolidays);
//			System.out.println("hmHolidayDates1===>"+ hmHolidayDates);

//			System.out.println("strReqServiceId===>"+ strReqServiceId);
//			System.out.println("strReqEmpID===>"+ strReqEmpID);
			
			if (strReqEmpID != null && strReqServiceId!=null) {
				
//				System.out.println("pst===> 1");
				
				pst = con.prepareStatement(selectClockEntriesR1_ES);
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strReqEmpID));
				pst.setDate(4, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(6, uF.parseToInt(strReqEmpID));
				pst.setInt(7, uF.parseToInt(strReqServiceId));
				 
//				System.out.println("UCE/646--3 pst==1====>"+pst);
			} else if (strReqEmpID != null && uF.parseToInt(strReqEmpID)>0) {
//				System.out.println("pst===> 2");
				pst = con.prepareStatement(selectClockEntriesR1_E);
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strReqEmpID));
				pst.setDate(4, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(6, uF.parseToInt(strReqEmpID));
//				System.out.println("UCE/656--3 pst===2===>"+pst);
		//===end parvez date: 02-12-2021===
			} else if(strUserType!=null && strUserType.equals(MANAGER) && (getCurrUserType() == null || !getCurrUserType().equals(strBaseUserType))) {
				
//				System.out.println("pst===> 3");	
				
				pst = con.prepareStatement("select * from (SELECT * FROM  (Select *,rd.emp_id as empid, rd.service_id as serviceid from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad WHERE TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? ORDER BY in_out_timestamp desc) a FULL JOIN roster_details rd ON a.emp_id=rd.emp_id and a.service_id=rd.service_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date ) t WHERE t._date BETWEEN ? AND ?  order by empid, serviceid, in_out ) ab, employee_personal_details epd  where ab.empid=epd.emp_per_id and ab.empid in (select emp_id from employee_official_details where is_roster=true and supervisor_emp_id  = ? ) order by _date desc, empid, serviceid, in_out");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt((String)session.getAttribute(EMPID)));
//				System.out.println("UCE/668--pst===>  " + pst);
			} else {
//				pst = con.prepareStatement(selectClockEntriesR1_E2);
//				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//				pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));

//				StringBuilder sbQuery = new StringBuilder(); 
//				sbQuery.append("select * from (select serviceid as service_id,serviceid,approved,notify_time,new_time,_date,empid,_to,_from,empid as emp_id,reason," +
//						"in_out,early_late,emp_fname,emp_lname,atten_id,in_out_timestamp from (SELECT * FROM (Select *,rd.emp_id as empid, " +
//						"rd.service_id as serviceid from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad WHERE " +
//						"TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and ad.emp_id in (select emp_id from employee_official_details " +
//						"where is_roster=true and org_id=?) and ad.approved=-2 ORDER BY in_out_timestamp desc) a FULL JOIN (select * from roster_details where " +
//						"roster_id in (select rd1.roster_id from roster_details rd1,attendance_details ad1 WHERE " +
//						"TO_DATE(ad1.in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and ad1.emp_id in (select emp_id from employee_official_details " +
//						"where is_roster=true and org_id=?) and ad1.approved=-2 and ad1.emp_id=rd1.emp_id and ad1.service_id=rd1.service_id " +
//						"and TO_DATE(ad1.in_out_timestamp::text, 'YYYY-MM-DD')=rd1._date)) rd ON a.emp_id=rd.emp_id and a.service_id=rd.service_id " +
//						"and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date) t WHERE t._date BETWEEN ? AND ?  order by empid, serviceid, in_out ) ab," +
//						" employee_personal_details epd  where ab.empid=epd.emp_per_id and ab.empid in (select emp_id from employee_official_details " +
//						"where is_roster=true)");
//				sbQuery.append(" union ");
//				sbQuery.append("select serviceid as service_id,serviceid,approved,notify_time,new_time,_date,empid,_to,_from,empid as emp_id,reason,in_out,early_late," +
//						"emp_fname,emp_lname,atten_id,in_out_timestamp from (SELECT * FROM  (Select *,rd.emp_id as empid, rd.service_id as serviceid " +
//						"from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad WHERE TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') " +
//						"BETWEEN ? AND ? and ad.emp_id in (select emp_id from employee_official_details where is_roster=true and org_id=?) " +
//						"ORDER BY in_out_timestamp desc) a FULL JOIN (select * from roster_details rd1,(select rd.roster_id,ad.atten_id as attenid " +
//						"from roster_details rd left join attendance_details ad ON ad.emp_id=rd.emp_id and ad.service_id=rd.service_id " +
//						"and TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date and TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD') " +
//						"BETWEEN ? AND ? and TO_DATE(rd._date::text, 'YYYY-MM-DD') BETWEEN ? AND ? and rd.emp_id in (select emp_id from employee_official_details " +
//						" where is_roster=true and org_id=?) group by rd.roster_id,ad.atten_id having (atten_id is null or atten_id =0)) aa where rd1.roster_id=aa.roster_id and " +
//						"TO_DATE(rd1._date::text, 'YYYY-MM-DD') BETWEEN ? AND ? and rd1.emp_id in (select emp_id from employee_official_details  " +
//						"where is_roster=true and org_id=?)) rd ON a.emp_id=rd.emp_id and a.service_id=rd.service_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date) t " +
//						"WHERE t._date BETWEEN ? AND ? order by empid, serviceid, in_out) ab," +
//						" employee_personal_details epd  where ab.empid=epd.emp_per_id and ab.empid in (select emp_id from employee_official_details " +
//						"where is_roster=true)) as a order by _date desc, empid,service_id, in_out");
//				pst = con.prepareStatement(sbQuery.toString());
//				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//				pst.setInt(3, uF.parseToInt(getF_org()));
//				pst.setDate(4, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(5, uF.getDateFormat(strD2, DATE_FORMAT));
//				pst.setInt(6, uF.parseToInt(getF_org()));
//				pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
//				pst.setDate(9, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(10, uF.getDateFormat(strD2, DATE_FORMAT)); 
//				pst.setInt(11, uF.parseToInt(getF_org()));
//				pst.setDate(12, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(13, uF.getDateFormat(strD2, DATE_FORMAT));
//				pst.setDate(14, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(15, uF.getDateFormat(strD2, DATE_FORMAT));
//				pst.setInt(16, uF.parseToInt(getF_org()));
//				pst.setDate(17, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(18, uF.getDateFormat(strD2, DATE_FORMAT));
//				pst.setInt(19, uF.parseToInt(getF_org()));
//				pst.setDate(20, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(21, uF.getDateFormat(strD2, DATE_FORMAT));
//				System.out.println("3 pst====4==>"+pst);
				
				/*StringBuilder sbQuery = new StringBuilder(); 
				sbQuery.append("select * from (select serviceid as service_id,serviceid,approved,notify_time,new_time,_date,empid,_to,_from,empid as emp_id,reason," +
						"in_out,early_late,emp_fname,emp_mname, emp_lname,atten_id,in_out_timestamp from (SELECT * FROM (Select *,rd.emp_id as empid, " +
						"rd.service_id as serviceid from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad WHERE " +
						"TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and ad.emp_id in (select emp_id from employee_official_details " +
						"where is_roster=true ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            if(getF_department()!=null && getF_department().length>0) {
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            if(getF_level()!=null && getF_level().length>0) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }
				sbQuery.append(") and ad.approved=-2 ORDER BY in_out_timestamp desc) a FULL JOIN (select * from roster_details where " +
						"roster_id in (select rd1.roster_id from roster_details rd1,attendance_details ad1 WHERE " +
						"TO_DATE(ad1.in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and ad1.emp_id in (select emp_id from employee_official_details " +
						"where is_roster=true ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            if(getF_department()!=null && getF_department().length>0) {
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            if(getF_level()!=null && getF_level().length>0) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }
				sbQuery.append(") and ad1.approved=-2 and ad1.emp_id=rd1.emp_id and ad1.service_id=rd1.service_id " +
						"and TO_DATE(ad1.in_out_timestamp::text, 'YYYY-MM-DD')=rd1._date)) rd ON a.emp_id=rd.emp_id and a.service_id=rd.service_id " +
						"and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date) t WHERE t._date BETWEEN ? AND ?  order by empid, serviceid, in_out ) ab," +
						" employee_personal_details epd  where ab.empid=epd.emp_per_id and ab.empid in (select emp_id from employee_official_details " +
						"where is_roster=true ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            if(getF_department()!=null && getF_department().length>0) {
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            if(getF_level()!=null && getF_level().length>0) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }
				sbQuery.append(")");
				sbQuery.append(" union ");
				sbQuery.append("select serviceid as service_id,serviceid,approved,notify_time,new_time,_date,empid,_to,_from,empid as emp_id,reason,in_out,early_late," +
						"emp_fname,emp_mname,emp_lname,atten_id,in_out_timestamp from (SELECT * FROM  (Select *,rd.emp_id as empid, rd.service_id as serviceid " +
						"from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad WHERE TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') " +
						"BETWEEN ? AND ? and ad.emp_id in (select emp_id from employee_official_details where is_roster=true ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            if(getF_department()!=null && getF_department().length>0) {
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            if(getF_level()!=null && getF_level().length>0) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }
				sbQuery.append(") ORDER BY in_out_timestamp desc) a FULL JOIN (select * from roster_details rd1,(select rd.roster_id,ad.atten_id as attenid " +
						"from roster_details rd left join attendance_details ad ON ad.emp_id=rd.emp_id and ad.service_id=rd.service_id " +
						"and TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date and TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD') " +
						"BETWEEN ? AND ? and TO_DATE(rd._date::text, 'YYYY-MM-DD') BETWEEN ? AND ? and rd.emp_id in (select emp_id from employee_official_details " +
						" where is_roster=true ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            if(getF_department()!=null && getF_department().length>0) {
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            System.out.println("LeveId="+StringUtils.join(getF_level(), ",")+"---"+getStrLevel());
	            if(getF_level()!=null && getF_level().length>0) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }
				sbQuery.append(") group by rd.roster_id,ad.atten_id having (atten_id is null or atten_id =0)) aa where rd1.roster_id=aa.roster_id and " +
						"TO_DATE(rd1._date::text, 'YYYY-MM-DD') BETWEEN ? AND ? and rd1.emp_id in (select emp_id from employee_official_details  " +
						"where is_roster=true ");
	                  
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            if(getF_department()!=null && getF_department().length>0) {
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            if(getF_level()!=null && getF_level().length>0) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }
				sbQuery.append(")) rd ON a.emp_id=rd.emp_id and a.service_id=rd.service_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date) t " +
					"WHERE t._date BETWEEN ? AND ? order by empid, serviceid, in_out) ab, employee_personal_details epd where ab.empid=epd.emp_per_id " +
					"and ab.empid in (select emp_id from employee_official_details where is_roster = true ");
	                  
	            
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            if(getF_department()!=null && getF_department().length>0) {
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            if(getF_level()!=null && getF_level().length>0) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }
				sbQuery.append(")) as a order by _date desc, empid,service_id, in_out");*/
				
		//===start parvez date: 27-06-2022===		
				StringBuilder sbQuery = new StringBuilder(); 
				sbQuery.append("select * from (select serviceid as service_id,serviceid,approved,notify_time,new_time,_date,empid,_to,_from,empid as emp_id,reason," +
						"in_out,early_late,emp_fname,emp_mname, emp_lname,atten_id,in_out_timestamp from (SELECT * FROM (Select *,rd.emp_id as empid, " +
						"rd.service_id as serviceid from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad WHERE " +
						"TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and ad.emp_id in (select emp_id from employee_official_details " +
						"where is_roster=true ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getStrLocation()!=null && !getStrLocation().isEmpty() && !getStrLocation().equals("")) {
		            sbQuery.append(" and wlocation_id= "+getStrLocation());
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            if(getStrDepartment()!=null && !getStrDepartment().isEmpty() && !getStrDepartment().equals("")) {
	                sbQuery.append(" and depart_id="+getStrDepartment());
	            }
	            if(getStrLevel()!=null && !getStrLevel().isEmpty() && !getStrLevel().equals("")) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id="+getStrLevel()+" ) ");
	            }
				sbQuery.append(") and ad.approved=-2 ORDER BY in_out_timestamp desc) a FULL JOIN (select * from roster_details where " +
						"roster_id in (select rd1.roster_id from roster_details rd1,attendance_details ad1 WHERE " +
						"TO_DATE(ad1.in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and ad1.emp_id in (select emp_id from employee_official_details " +
						"where is_roster=true ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getStrLocation()!=null && !getStrLocation().isEmpty() && !getStrLocation().equals("")) {
		            sbQuery.append(" and wlocation_id="+getStrLocation());
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
				if(getStrDepartment()!=null && !getStrDepartment().isEmpty() && !getStrDepartment().equals("")) {
	                sbQuery.append(" and depart_id="+getStrDepartment());
	            }
	            if(getStrLevel()!=null && !getStrLevel().isEmpty() && !getStrLevel().equals("")) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id="+getStrLevel()+" ) ");
	            }
				sbQuery.append(") and ad1.approved=-2 and ad1.emp_id=rd1.emp_id and ad1.service_id=rd1.service_id " +
						"and TO_DATE(ad1.in_out_timestamp::text, 'YYYY-MM-DD')=rd1._date)) rd ON a.emp_id=rd.emp_id and a.service_id=rd.service_id " +
						"and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date) t WHERE t._date BETWEEN ? AND ?  order by empid, serviceid, in_out ) ab," +
						" employee_personal_details epd  where ab.empid=epd.emp_per_id and ab.empid in (select emp_id from employee_official_details " +
						"where is_roster=true ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getStrLocation()!=null && !getStrLocation().isEmpty() && !getStrLocation().equals("")) {
		            sbQuery.append(" and wlocation_id="+getStrLocation());
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
				if(getStrDepartment()!=null && !getStrDepartment().isEmpty() && !getStrDepartment().equals("")) {
	                sbQuery.append(" and depart_id="+getStrDepartment());
	            }
	            if(getStrLevel()!=null && !getStrLevel().isEmpty() && !getStrLevel().equals("")) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id="+getStrLevel()+" ) ");
	            }
				sbQuery.append(")");
				sbQuery.append(" union ");
				sbQuery.append("select serviceid as service_id,serviceid,approved,notify_time,new_time,_date,empid,_to,_from,empid as emp_id,reason,in_out,early_late," +
						"emp_fname,emp_mname,emp_lname,atten_id,in_out_timestamp from (SELECT * FROM  (Select *,rd.emp_id as empid, rd.service_id as serviceid " +
						"from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad WHERE TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') " +
						"BETWEEN ? AND ? and ad.emp_id in (select emp_id from employee_official_details where is_roster=true ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getStrLocation()!=null && !getStrLocation().isEmpty() && !getStrLocation().equals("")) {
		            sbQuery.append(" and wlocation_id="+getStrLocation());
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
				if(getStrDepartment()!=null && !getStrDepartment().isEmpty() && !getStrDepartment().equals("")) {
	                sbQuery.append(" and depart_id="+getStrDepartment());
	            }
	            if(getStrLevel()!=null && !getStrLevel().isEmpty() && !getStrLevel().equals("")) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id="+getStrLevel()+" ) ");
	            }
				sbQuery.append(") ORDER BY in_out_timestamp desc) a FULL JOIN (select * from roster_details rd1,(select rd.roster_id,ad.atten_id as attenid " +
						"from roster_details rd left join attendance_details ad ON ad.emp_id=rd.emp_id and ad.service_id=rd.service_id " +
						"and TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date and TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD') " +
						"BETWEEN ? AND ? and TO_DATE(rd._date::text, 'YYYY-MM-DD') BETWEEN ? AND ? and rd.emp_id in (select emp_id from employee_official_details " +
						" where is_roster=true ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getStrLocation()!=null && !getStrLocation().isEmpty() && !getStrLocation().equals("")) {
		            sbQuery.append(" and wlocation_id="+getStrLocation());
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
				if(getStrDepartment()!=null && !getStrDepartment().isEmpty() && !getStrDepartment().equals("")) {
	                sbQuery.append(" and depart_id="+getStrDepartment());
	            }
	            
	            if(getStrLevel()!=null && !getStrLevel().isEmpty() && !getStrLevel().equals("")) {
	            	System.out.println("LeveId="+getStrLevel()+"----lenght="+getStrLevel().length());
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id="+getStrLevel()+" ) ");
	            }      
	            sbQuery.append(") group by rd.roster_id,ad.atten_id ) aa where rd1.roster_id=aa.roster_id and " +
						"TO_DATE(rd1._date::text, 'YYYY-MM-DD') BETWEEN ? AND ? and rd1.emp_id in (select emp_id from employee_official_details  " +
						"where is_roster=true ");
	            
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getStrLocation()!=null && !getStrLocation().isEmpty() && !getStrLocation().equals("")) {
		            sbQuery.append(" and wlocation_id="+getStrLocation());
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
				if(getStrDepartment()!=null && !getStrDepartment().isEmpty() && !getStrDepartment().equals("")) {
	                sbQuery.append(" and depart_id="+getStrDepartment());
	            }
	            if(getStrLevel()!=null && !getStrLevel().isEmpty() && !getStrLevel().equals("")) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id="+getStrLevel()+" ) ");
	            }
				sbQuery.append(")) rd ON a.emp_id=rd.emp_id and a.service_id=rd.service_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date) t " +
					"WHERE t._date BETWEEN ? AND ? order by empid, serviceid, in_out) ab, employee_personal_details epd where ab.empid=epd.emp_per_id " +
					"and ab.empid in (select emp_id from employee_official_details where is_roster = true ");
	                  
	            
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getStrLocation()!=null && !getStrLocation().isEmpty() && !getStrLocation().equals("")) {
		            sbQuery.append(" and wlocation_id="+getStrLocation());
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
				if(getStrDepartment()!=null && !getStrDepartment().isEmpty() && !getStrDepartment().equals("")) {
	                sbQuery.append(" and depart_id="+getStrDepartment());
	            }
	            if(getStrLevel()!=null && !getStrLevel().isEmpty() && !getStrLevel().equals("")) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id="+getStrLevel()+" ) ");
	            }
				sbQuery.append(")) as a order by _date desc, empid,service_id, in_out");
		//===end parvez date: 27-06-2022====
				
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT)); 
				pst.setDate(9, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(10, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(11, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(12, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(13, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(14, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(15, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(16, uF.getDateFormat(strD2, DATE_FORMAT));
			}
			if(uF.parseToInt(strReqEmpID)>0 && CF.isRosterDependency(con,strReqEmpID)) {
				System.out.println("UCE/885--3 pst==5====>"+pst);
				rs = pst.executeQuery();
			} else {
				System.out.println("UCE/888--3 pst==6====>"+pst); 
				rs = pst.executeQuery();
			}
			sb.append("<table style=\"table-layout:fixed;width:920px\" cellpadding=\"0\" cellspacing=\"1\">");
			// StringBuilder str = new StringBuilder();
			String strNew = "";
			String strOld = "";
			int i = 1;
			String strEmpId = "";

			String strDateNew = null;
			String strDateOld = null;
			String strEmpIdRowOld = null;
			String strEmpIdOld = null;
			String strEmpIdNew = null;
			String strRosterFromNew = null;
			String strRosterFromOld = null;
			String strRosterToNew = null;
			String strRosterToOld = null;

			boolean isIn = false;
			boolean isOut = false;
			boolean isData = false;
			int nApproved = 0;
			int nNotify_Time = 0;
			String strNotify_Time = null;
			int nServiceIdNew = 0;
			int nServiceIdOld = 0;
			int empRowCount=0;
			boolean isChange = false;
			
			
			while (rs!=null && rs.next()) {
//				System.out.println("====="+rs.getString("in_out_timestamp"));
				
//				nServiceIdNew = rs.getInt("service_id");
				nServiceIdNew = rs.getInt("serviceid"); 
				nApproved = rs.getInt("approved");
				nNotify_Time = rs.getInt("notify_time");
				if(rs.getString("new_time")!=null) {
					strNotify_Time = uF.getDateFormat(rs.getString("new_time"), DBTIME, CF.getStrReportTimeFormat());
				}
				i++;   

				strDateNew = rs.getString("_date");
				
				
//				strEmpIdNew = rs.getString("emp_id");
//				strEmpIdNew = rs.getString("empl_id");
				strEmpIdNew = rs.getString("empid");
				strRosterToNew = rs.getString("_to");
				strRosterFromNew = rs.getString("_from");
				
				if(strUserType!=null && (strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(CONSULTANT)) && !CF.isRosterDependency(con,strEmpIdNew))continue;
				if(!alService.contains(nServiceIdNew+"")) {
					alService.add(nServiceIdNew+"");
				}
				
				Map hmEmpLeave = (Map)hmLeaves.get(strEmpIdNew);
				if(hmEmpLeave==null)hmEmpLeave=new HashMap();
				
				
//				System.out.println("strEmpIdNew===="+strEmpIdNew+" -- strDateOld ===>> "+strDateOld+" -- strDateNew ===>> "+strDateNew+" -- alDt ===>> "+alDt+" -- hmHolidayDates ===>> "+hmHolidayDates+" -- hmEmpLeave ===>> "+hmEmpLeave);
				if(!alDt.contains(strDateNew) && !hmHolidayDates.containsKey(uF.getDateFormat(strDateNew, DBDATE, DATE_FORMAT)) && !hmEmpLeave.containsKey(uF.getDateFormat(strDateNew, DBDATE, DATE_FORMAT))) {
					alDt.add(strDateNew);

				}
				

				if (((strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) || (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld)) || (nServiceIdNew != nServiceIdOld)) && i > 1) {
					long frmTime = 0;
					long toTime = 0;
					long currentTime = 0;
					if (strRosterFromOld != null) {
						frmTime = uF.getTimeStamp(strDateOld + strRosterFromOld, DBDATE + DBTIME).getTime();
					}

					if (strRosterToOld != null) {
						toTime = uF.getTimeStamp(strDateOld + strRosterToOld, DBDATE + DBTIME).getTime();
					}

					currentTime = uF.getTimeStamp("" + uF.getCurrentDate(CF.getStrTimeZone()) + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME).getTime();
//					System.out.println("------------->> 2");
			
			//===created by parvez date: 06-12-2021===
			//===start===		
					if(rs.isLast() && rs.getString("in_out") != null && rs.getString("in_out").equalsIgnoreCase("IN")){
						isIn = true;
						isOut = false;
						strDateOld = strDateNew;
					}
			//===end===
					
					if (!isIn && isOut) {
					
						
						if ((frmTime == 0) || (frmTime > 0 && frmTime < currentTime)) {
//							System.out.println("------------->> (frmTime == 0) || (frmTime > 0 && frmTime < currentTime)");
							isData = true;
							Map hmRosterStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_RS");
							Map hmRosterEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_RE");
							Map hmActualStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_AS");
							Map hmActualEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_AE");
							//System.out.println("UCE/985--hmTimeSheet="+hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_AE"));
							if(hmRosterStart==null) {
								hmRosterStart = new HashMap();
							}
							if(hmRosterEnd==null) {
								hmRosterEnd = new HashMap();
							}
							if(hmActualStart==null) {
								hmActualStart = new HashMap();
							}
							if(hmActualEnd==null) {
								hmActualEnd = new HashMap();
							}
							
							hmRosterStart.put(strEmpIdOld, uF.showData(uF.getDateFormat(strRosterFromOld, DBTIME, CF.getStrReportTimeFormat()), "N/A"));
							hmActualStart.put(strEmpIdOld, "");
//							hmActualReason.put(strEmpIdOld, rs.getString("reason"));
							//System.out.println("UCE/1002--strDateOld="+strDateOld);
									
							hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_RS", hmRosterStart);
							hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_RE", hmRosterEnd);
							hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_AS", hmActualStart);
							hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_AE", hmActualEnd);
							
							if(strEmpIdOld!=null && !alTimeSheet.contains(strEmpIdOld)) {
								alTimeSheet.add(strEmpIdOld);
//								System.out.println(" strEmpIdOld 1 ===>> " + strEmpIdOld);
							}
							
//							if (isDate && (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdRowOld)) || (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld))) {
//								strEmpIdRowOld = strEmpIdNew;
//								if (strNew.length() > 0) {
//									strNew = new String();
//								} else {
//									strNew = "1";
//								}
//							}
							
							sb.append("<tr>");
							sb.append("<td colspan=\"6\">");
							sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");

							sb.append("<td nowrap height=\"25\" width=\"100\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDayFormat()) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat()) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + hmEmployeeNameMap.get(strEmpIdOld) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdOld + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdOld + "\" ></td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">ROSTERED START TIME</td>");
							sb.append("<td style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.showData(uF.getDateFormat(strRosterFromOld, DBTIME, CF.getStrReportTimeFormat()), "N/A") + "</td>");

							sb.append("<td style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">&nbsp;</td>");
							sb.append("</tr></table>");
							sb.append("</td>");
							sb.append("<td rowspan=\"2\" style=\"width:50px\" class=\"reportLabel" + strOld + " alignLeft\">" +
							// "<a href=\"#?w=500\" rel=\"popup_name" + i +
							// "\" class=\"poplight\" >View Reason</a>" +
							// "<a href=\"javascript:$.modaldialog.success('" +
							// (new
							// CommonFunctions().converIntoHtml(rs.getString("reason"),
							// "No reason provided")) +
							// "', { title: 'Reason given!' });\">View Reason</a>"
							// + "</td>");
								"&nbsp;</td>");

							sb.append("<td style=\"width:100px\" rowspan=\"2\" class=\"reportLabel" + strOld + " alignLeft\">&nbsp;</td>"
							// "+|<a href=\"ApproveClockEntries.action?DID="+
							// rs.getString("atten_id") + "&P=U\">Deny</a></td>"
							);

							// if ((rs.getInt("approved") == 0 ||
							// rs.getInt("approved") == -2) && strUserType !=
							// null && (strUserType.equalsIgnoreCase(ADMIN) ||
							// strUserType.equalsIgnoreCase(MANAGER))) {
							// sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel"
							// + str +
							// " alignLeft\"><a onclick=\"return confirm('Are you sure you want to approve?')\" href=\"ApproveClockEntries.action?AID="
							// + rs.getString("atten_id") + "&P=U\">Approve</a>"
							// //"+|<a href=\"ApproveClockEntries.action?DID="+
							// rs.getString("atten_id") + "&P=U\">Deny</a></td>"
							// );
							// } else if (rs.getInt("approved") == 0 ||
							// rs.getInt("approved") == -2) {
							// sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel"
							// + str +
							// " alignLeft\">Waiting For Approval</td>");
							// } else if (rs.getInt("approved") == -1) {
							// sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel"
							// + str + " alignLeft\">Denied</td>");
							// } else if (rs.getInt("approved") == 1) {
							// sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel"
							// + str + " alignLeft\">Approved</td>");
							// }

							sb.append("</tr>");
							sb.append("<tr>");

							sb.append("<td colspan=\"6\">");
							sb.append("<form style=\"margin:0\" action=\"UpdateClockEntries.action\" >");
							sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");

							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDayFormat()) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input type=\"hidden\" value=\"" + strDateOld + "\" name=\"strCurrentDate\" >"
									+ uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat()) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + hmEmployeeNameMap.get(strEmpIdOld) + "<input type=\"hidden\" name=\"strEmpId\" value=\"" + rs.getString("emp_id") + "\" ></td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdOld + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdOld + "\" ></td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">ACTUAL START TIME</td>");
							sb.append("<td style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input type=\"text\"  style=\"width:65px\" name=\"strEmpIN\" value=\" \" ></td>");

							sb.append("<td>" + "<div id=\"popup_name" + i + "\" class=\"popup_block\">" + "<h2 class=\"textcolorWhite\">Reasons </h2>" + "<table width=\"90%\" cellSpacing=\"1\"  cellPadding=\"1\">" + "<tr><td class=\"reportLabel\">"
									+ ((rs.getString("reason") == null || (rs.getString("reason") != null && rs.getString("reason").equalsIgnoreCase("")) ? "No reason provided" : rs.getString("reason"))) + "</td></tr>" + "</table>" + "</div>" + "</td>");
							sb.append("<td style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input style=\"width:65px\" class=\"input_button\" type=\"submit\" value=\"Update\" ></td>");

							sb.append("</tr></table>");
							sb.append("</form>");
							sb.append("</td>");
							sb.append("</tr>");
						}

					}

					
					if (isIn && !isOut) {
						
//						System.out.println("UCE/1150-->>"+"strDateOld="+strDateOld+"---strDateNew="+strDateNew);
						
						if ((toTime == 0) || (toTime > 0 && toTime < currentTime)) {
//							System.out.println("UCE/1150-->>"+"strDateOld="+strDateOld+"---strDateNew="+strDateNew);
//							System.out.println("------------->> (toTime == 0) || (toTime > 0 && toTime < currentTime)");
//							if (isDate && (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdRowOld)) || (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld))) {
//								strEmpIdRowOld = strEmpIdNew;
//								if (strNew.length() > 0) {
//									strNew = new String();
//								} else {
//									strNew = "1";
//								}
//							}
							
							Map hmRosterStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_RS");
							Map hmRosterEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_RE");
							Map hmActualStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_AS");
							Map hmActualEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_AE");
							
							if(hmRosterStart==null) {
								hmRosterStart = new HashMap();
							}
							if(hmRosterEnd==null) {
								hmRosterEnd = new HashMap();
							}
							if(hmActualStart==null) {
								hmActualStart = new HashMap();
							}
							if(hmActualEnd==null) {
								hmActualEnd = new HashMap();
							}
							
							hmRosterEnd.put(strEmpIdOld, uF.showData(uF.getDateFormat(strRosterToOld, DBTIME, CF.getStrReportTimeFormat()), "N/A"));
							hmActualEnd.put(strEmpIdOld, "");
//							hmActualReason.put(strEmpIdOld, rs.getString("reason"));
									
							hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_RS", hmRosterStart);
							hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_RE", hmRosterEnd);
							hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_AS", hmActualStart);
							hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_AE", hmActualEnd);
							if(!alTimeSheet.contains(strEmpIdOld)) {
								alTimeSheet.add(strEmpIdOld);
//								System.out.println(" strEmpIdOld 2 ===>> " + strEmpIdOld);
							}
							
//							System.out.println(" 222  strDateNew==>" + strDateNew + " strEmpIdNew==>" + hmEmployeeNameMap.get(strEmpIdNew) + " strEmpIdOld==>" + hmEmployeeNameMap.get(strEmpIdOld) + " str===>" + " str===>" + strNew.toString() + "   " + strOld.toString()+" empRowCount="+empRowCount);
							
							isData = true;
							sb.append("<tr>");
							sb.append("<td colspan=\"6\">");
							sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");

							sb.append("<td height=\"25\" width=\"100\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDayFormat()) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat()) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + hmEmployeeNameMap.get(strEmpIdOld) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdOld + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdOld + "\" ></td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">ROSTERED END TIME</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.showData(uF.getDateFormat(strRosterToOld, DBTIME, CF.getStrReportTimeFormat()), "N/A") + "</td>");
							sb.append("<td style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">&nbsp;</td>");

							sb.append("</tr></table>");
							sb.append("</td>");

							sb.append("<td rowspan=\"2\" style=\"width:50px\" class=\"reportLabel" + strOld + " alignLeft\">" +
							// "<a href=\"#?w=500\" rel=\"popup_name" + i +
							// "\" class=\"poplight\" >View Reason</a>" +
							// "<a href=\"javascript:$.modaldialog.success('" +
							// (new
							// CommonFunctions().converIntoHtml(rs.getString("reason"),
							// "No reason provided")) +
							// "', { title: 'Reason given!' });\">View Reason</a>"
							// + "</td>");
								"&nbsp;</td>");

							sb.append("<td style=\"width:100px\" rowspan=\"2\" class=\"reportLabel" + strOld + " alignLeft\">"
							// +"|<a href=\"ApproveClockEntries.action?DID="+
							// rs.getString("atten_id") + "&P=U\">Deny</a></td>"
								+ "&nbsp;</td>");

							// if ((rs.getInt("approved") == 0 ||
							// rs.getInt("approved") == -2) && strUserType !=
							// null && (strUserType.equalsIgnoreCase(ADMIN) ||
							// strUserType.equalsIgnoreCase(MANAGER))) {
							// sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel"
							// + str +
							// " alignLeft\"><a onclick=\"return confirm('Are you sure you want to approve?')\"  href=\"ApproveClockEntries.action?AID="
							// + rs.getString("atten_id") + "&P=U\">Approve</a>"
							// //+"|<a href=\"ApproveClockEntries.action?DID="+
							// rs.getString("atten_id") + "&P=U\">Deny</a></td>"
							// );
							// } else if (rs.getInt("approved") == 0 ||
							// rs.getInt("approved") == -2) {
							// sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel"
							// + str +
							// " alignLeft\">Waiting For Approval</td>");
							// } else if (rs.getInt("approved") == -1) {
							// sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel"
							// + str + " alignLeft\">Denied</td>");
							// } else if (rs.getInt("approved") == 1) {
							// sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel"
							// + str + " alignLeft\">Approved</td>");
							// }
							sb.append("</tr>");

							sb.append("<tr>");

							sb.append("<td colspan=\"6\">");
							sb.append("<form style=\"margin:0\" action=\"UpdateClockEntries.action\" >");
							sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");

							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDayFormat()) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input type=\"hidden\" value=\"" + strDateOld + "\" name=\"strCurrentDate\" >"
									+ uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat()) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + hmEmployeeNameMap.get(strEmpIdOld) + "<input type=\"hidden\" name=\"strEmpId\" value=\"" + rs.getString("emp_id") + "\" ></td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdOld + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdOld + "\" ></td>");
							sb.append("<td nowrap style=\"width:150px\" class=\"reportLabel" + strOld + " alignLeft\">ACTUAL END TIME</td>");
							sb.append("<td style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input style=\"width:65px\" type=\"text\" name=\"strEmpOUT\" value=\" \" ></td>");

							sb.append("<td>" + "<div id=\"popup_name" + i + "\" class=\"popup_block\">" + "<h2 class=\"textcolorWhite\">Reasons </h2>" + "<table width=\"90%\" cellSpacing=\"0\"  cellPadding=\"1\">" + "<tr><td class=\"reportLabel\">"
									+ ((rs.getString("reason") == null || (rs.getString("reason") != null && rs.getString("reason").equalsIgnoreCase("")) ? "No reason provided" : rs.getString("reason"))) + "</td></tr>" + "</table>" + "</div>" + "</td>");
							sb.append("<td style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input class=\"input_button\" style=\"width:65px\" type=\"submit\" value=\"Update\" ></td>");

							sb.append("</tr></table>");
							sb.append("</form>");
							sb.append("</td>");
							sb.append("</tr>");
						}
					}
					isIn = false;
					isOut = false;

					// if(str.length()==1) {
					// str = new StringBuilder("");
					// } else {
					// str = new StringBuilder("1");
					// }
				}

//				strEmpId = rs.getString("emp_id");
				strEmpId = rs.getString("empid");
				if ("IN".equalsIgnoreCase(rs.getString("in_out"))) {
					
					double dblTardyIn = uF.parseToDouble((String) hmTardyType.get("TARDY_IN"));

					isIn = true;
//					 if(rs.getDouble("early_late")>0 || uF.convertMinutesIntoHours(dblTardyIn)>rs.getDouble("early_late")) {
					if (nApproved == -2) {
//						System.out.println(" 333  strDateNew==>" + strDateNew + " strEmpIdNew==>" + hmEmployeeNameMap.get(strEmpId) + " str===>" + strNew.toString() + "   " + strOld.toString());
						String strNewTimeReason = ((nNotify_Time == 0) ? "" : "<br/><br/>New Start Time :" + strNotify_Time);
						
						Map hmRosterStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS");
						Map hmRosterEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE");
						Map hmActualStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS");
						Map hmActualEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE");
						Map hmActualReason = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_IN_REASON");
						Map hmApprove = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_IN_APPROVE");
						
						if(hmRosterStart==null) {
							hmRosterStart = new HashMap();
						}
						if(hmRosterEnd==null) {
							hmRosterEnd = new HashMap();
						}
						if(hmActualStart==null) {
							hmActualStart = new HashMap();
						}
						if(hmActualEnd==null) {
							hmActualEnd = new HashMap();
						}
						if(hmActualReason==null) {
							hmActualReason = new HashMap();
						}
						if(hmApprove==null) {
							hmApprove = new HashMap();
						}
						
						hmRosterStart.put(strEmpId, uF.showData(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()), "N/A"));
						hmActualStart.put(strEmpId, uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
						hmActualReason.put(strEmpId, CF.converIntoHtml(rs.getString("reason") + strNewTimeReason, "No reason provided"));
						
						if ((rs.getInt("approved") == 0 || rs.getInt("approved") == -2) && strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(MANAGER))) {
							hmApprove.put(strEmpId,  ((nNotify_Time == 0) ? "" : "<span style=\"font-size:11px\">Notification for revised timings</span><br/>")
									+ "<a onclick=\"return confirm('Are you sure you want to approve?')\" href=\"ApproveClockEntries.action?AID=" + rs.getString("atten_id") + "&P=U\">Approve</a>");
						} else if (rs.getInt("approved") == 0 || rs.getInt("approved") == -2) {
							hmApprove.put(strEmpId,  "Waiting For Approval");
						} else if (rs.getInt("approved") == -1) {
							hmApprove.put(strEmpId,  "Denied");
						} else if (rs.getInt("approved") == 1) {
							hmApprove.put(strEmpId,  "Approved");
						}
								
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS", hmRosterStart);
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS", hmActualStart);
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_IN_REASON", hmActualReason);
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_IN_APPROVE", hmApprove);
						
						if(!alTimeSheet.contains(strEmpId)) {
							alTimeSheet.add(strEmpId);
//							System.out.println(" strEmpId 1 ===>> " + strEmpId);
						}
//						if (isDate && (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdRowOld)) || (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld))) {
//							strEmpIdRowOld = strEmpIdNew;
//							if (strNew.length() > 0) {
//								strNew = new String();
//							} else {
//								strNew = "1";
//							}
//						}
						
						
						isData = true;
						sb.append("<tr>");
						sb.append("<td colspan=\"6\">");
						sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");

						sb.append("<td nowrap height=\"25\" width=\"105\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
						
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname") + "</td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">ROSTERED START TIME</td>");
						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.showData(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()), "N/A") + "</td>");

						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">&nbsp;</td>");

						sb.append("</tr></table>");
						sb.append("</td>");

						sb.append("<td rowspan=\"2\" style=\"width:50px\" class=\"reportLabel" + strNew + " alignLeft\">" +
						// "<a href=\"#?w=500\" rel=\"popup_name" + i +
						// "\" class=\"poplight\" >View Reason</a>" +
								"<a href=\"javascript:$.modaldialog.success('" + (CF.converIntoHtml(rs.getString("reason") + strNewTimeReason, "No reason provided")) + "', { title: 'Reason given!' });\"><img src=\""+request.getContextPath()+"/images1/Exclamation.png"+"\"></a>" + "</td>");
						if ((rs.getInt("approved") == 0 || rs.getInt("approved") == -2) && strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(MANAGER))) {
							sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">" + ((nNotify_Time == 0) ? "" : "<span style=\"font-size:11px\">Notification for revised timings</span><br/>")
									+ "<a onclick=\"return confirm('Are you sure you want to approve?')\" href=\"ApproveClockEntries.action?AID=" + rs.getString("atten_id") + "&P=U\">Approve</a>"
							// "+|<a href=\"ApproveClockEntries.action?DID="+
							// rs.getString("atten_id") + "&P=U\">Deny</a></td>"
							);
						} else if (rs.getInt("approved") == 0 || rs.getInt("approved") == -2) {
							sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">Waiting For Approval</td>");
						} else if (rs.getInt("approved") == -1) {
							sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">Denied</td>");
						} else if (rs.getInt("approved") == 1) {
							sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">Approved</td>");
						}

						sb.append("</tr>");
						sb.append("<tr>");

						sb.append("<td colspan=\"6\">");
						sb.append("<form style=\"margin:0\" action=\"UpdateClockEntries.action\" >");
						sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");

						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input type=\"hidden\" value=\"" + rs.getString("_date") + "\" name=\"strCurrentDate\" >"
								+ uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname") + "<input type=\"hidden\" name=\"strEmpId\" value=\"" + rs.getString("emp_id")
								+ "\" ></td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">ACTUAL START TIME</td>");
						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input type=\"text\"  style=\"width:65px\" name=\"strEmpIN\" value=\""
								+ uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()) + "\" ></td>");

						sb.append("<td>" + "<div id=\"popup_name" + i + "\" class=\"popup_block\">" + "<h2 class=\"textcolorWhite\">Reasons </h2>" + "<table width=\"90%\" cellSpacing=\"1\"  cellPadding=\"1\">" + "<tr><td class=\"reportLabel\">"
								+ ((rs.getString("reason") == null || (rs.getString("reason") != null && rs.getString("reason").equalsIgnoreCase("")) ? "No reason provided" : rs.getString("reason"))) + "</td></tr>" + "</table>" + "</div>" + "</td>");
						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input style=\"width:65px\" class=\"input_button\" type=\"submit\" value=\"Update\" ></td>");

						sb.append("</tr></table>");
						sb.append("</form>");
						sb.append("</td>");
						sb.append("</tr>");
					}
//					 }

				} else if ("OUT".equalsIgnoreCase(rs.getString("in_out"))) {
					
					if (!isIn && !isOut) {
//						System.out.println(" 444  strDateNew==>" + strDateNew + " strEmpIdNew==>" + hmEmployeeNameMap.get(strEmpId) + " str===>" + strNew.toString() + "   " + strOld.toString());
						
//						if (isDate && (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdRowOld)) || (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld))) {
//							strEmpIdRowOld = strEmpIdNew;
//							if (strNew.length() > 0) {
//								strNew = new String();
//							} else {
//								strNew = "1";
//							}
//						}
						
						Map hmRosterStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS");
						Map hmRosterEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE");
						Map hmActualStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS");
						Map hmActualEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE");
//						Map hmActualReason = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_REASON");
						
						if(hmRosterStart==null) {
							hmRosterStart = new HashMap();
						}
						if(hmRosterEnd==null) {
							hmRosterEnd = new HashMap();
						}
						if(hmActualStart==null) {
							hmActualStart = new HashMap();
						}
						if(hmActualEnd==null) {
							hmActualEnd = new HashMap();
						}
//						if(hmActualReason==null) {
//							hmActualReason = new HashMap();
//						}
						
						hmRosterStart.put(strEmpId, uF.showData(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()), "N/A"));
						hmActualStart.put(strEmpId, "");
//						hmActualReason.put(strEmpId, new CommonFunctions().converIntoHtml(rs.getString("reason") + strNewTimeReason, "No reason provided"));
						
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS", hmRosterStart);
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS", hmActualStart);
						
						if(!alTimeSheet.contains(strEmpId)) {
							alTimeSheet.add(strEmpId);
						}
						
						isData = true;
						isIn = true;
						sb.append("<tr>");
						sb.append("<td colspan=\"6\">");
						sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");

						sb.append("<td nowrap height=\"25\" width=\"105\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
					
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname") + "</td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">ROSTERED START TIME</td>");
						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.showData(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()), "N/A") + "</td>");

						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">&nbsp;</td>");

						sb.append("</tr></table>");
						sb.append("</td>");

						sb.append("<td rowspan=\"2\" style=\"width:50px\" class=\"reportLabel" + strNew + " alignLeft\">" +
						// "<a href=\"#?w=500\" rel=\"popup_name" + i +
						// "\" class=\"poplight\" >View Reason</a>" +
								"&nbsp;</td>");

						sb.append("<td rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">&nbsp;</td>"
						// "+|<a href=\"ApproveClockEntries.action?DID="+
						// rs.getString("atten_id") + "&P=U\">Deny</a></td>"
						);

						sb.append("</tr>");
						sb.append("<tr>");

						sb.append("<td colspan=\"6\">");
						sb.append("<form style=\"margin:0\" action=\"UpdateClockEntries.action\" >");
						sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");

						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input type=\"hidden\" value=\"" + rs.getString("_date") + "\" name=\"strCurrentDate\" >"
								+ uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname") + "<input type=\"hidden\" name=\"strEmpId\" value=\"" + rs.getString("emp_id")
								+ "\" ></td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">ACTUAL START TIME</td>");
						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input type=\"text\"  style=\"width:65px\" name=\"strEmpIN\" value=\" \" ></td>");

						sb.append("<td>" + "<div id=\"popup_name" + i + "\" class=\"popup_block\">" + "<h2 class=\"textcolorWhite\">Reasons </h2>" + "<table width=\"90%\" cellSpacing=\"1\"  cellPadding=\"1\">" + "<tr><td class=\"reportLabel\">"
								+ ((rs.getString("reason") == null || (rs.getString("reason") != null && rs.getString("reason").equalsIgnoreCase("")) ? "No reason provided" : rs.getString("reason"))) + "</td></tr>" + "</table>" + "</div>" + "</td>");
						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input style=\"width:65px\" class=\"input_button\" type=\"submit\" value=\"Update\" ></td>");

						sb.append("</tr></table>");
						sb.append("</form>");
						sb.append("</td>");
						sb.append("</tr>");

					}

					double dblTardyOut = uF.parseToDouble((String) hmTardyType.get("TARDY_OUT"));
					isOut = true;
					
//					 if(rs.getDouble("early_late")<0 || uF.convertMinutesIntoHours(Math.abs(dblTardyOut))<rs.getDouble("early_late")) {
					if (nApproved == -2) {
						isData = true;

//						System.out.println(" 555  strDateNew==>" + strDateNew + " strEmpIdNew==>" + hmEmployeeNameMap.get(strEmpIdNew) + " strEmpIdOld==>" + hmEmployeeNameMap.get(strEmpIdOld) + " str===>" + strNew.toString() + "   " + strOld.toString()+"  empRowCount="+empRowCount);
						
//						if (isDate && (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdRowOld)) || (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld))) {
//							strEmpIdRowOld = strEmpIdNew;
//							if (strNew.length() > 0) {
//								strNew = new String();
//							} else {
//								strNew = "1";
//							}
//						}
						
						String strNewTimeReason = ((nNotify_Time == 0) ? "" : "<br/><br/>New End Time :" + strNotify_Time);
						
						Map hmRosterStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS");
						Map hmRosterEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE");
						Map hmActualStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS");
						Map hmActualEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE");
						Map hmActualReason = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_OUT_REASON");
						Map hmApprove = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_OUT_APPROVE");
						
						if(hmRosterStart==null) {
							hmRosterStart = new HashMap();
						}
						if(hmRosterEnd==null) {
							hmRosterEnd = new HashMap();
						}
						if(hmActualStart==null) {
							hmActualStart = new HashMap();
						}
						if(hmActualEnd==null) {
							hmActualEnd = new HashMap();
						}
						if(hmActualReason==null) {
							hmActualReason = new HashMap();
						}
						if(hmApprove==null) {
							hmApprove = new HashMap();
						}
						
						hmRosterEnd.put(strEmpId, uF.showData(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()), "N/A"));
						hmActualEnd.put(strEmpId, uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
						hmActualReason.put(strEmpId, CF.converIntoHtml(rs.getString("reason") + strNewTimeReason, "No reason provided"));
						
						if ((rs.getInt("approved") == 0 || rs.getInt("approved") == -2) && strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(MANAGER))) {
							hmApprove.put(strEmpId,  ((nNotify_Time == 0) ? "" : "<span style=\"font-size:11px\">Notification for revised timings</span><br/>")
									+ "<a onclick=\"return confirm('Are you sure you want to approve?')\" href=\"ApproveClockEntries.action?AID=" + rs.getString("atten_id") + "&P=U\">Approve</a>");
						} else if (rs.getInt("approved") == 0 || rs.getInt("approved") == -2) {
							hmApprove.put(strEmpId,  "Waiting For Approval");
						} else if (rs.getInt("approved") == -1) {
							hmApprove.put(strEmpId,  "Denied");
						} else if (rs.getInt("approved") == 1) {
							hmApprove.put(strEmpId,  "Approved");
						}
								
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE", hmRosterEnd);
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE", hmActualEnd);
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_OUT_REASON", hmActualReason);
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_OUT_APPROVE", hmApprove);
						
						if(!alTimeSheet.contains(strEmpId)) {
							alTimeSheet.add(strEmpId);
						}
						
						sb.append("<tr>");

						sb.append("<td colspan=\"6\">");
						sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");

						sb.append("<td height=\"25\" width=\"105\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
					
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname") + "</td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">ROSTERED END TIME</td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.showData(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()), "N/A") + "</td>");
						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">&nbsp;</td>");

						sb.append("</tr></table>");
						sb.append("</td>");

						sb.append("<td rowspan=\"2\" style=\"width:50px\" class=\"reportLabel" + strNew + " alignLeft\">" +
						// "<a href=\"#?w=500\" rel=\"popup_name" + i +
						// "\" class=\"poplight\" >View Reason</a>" +
								"<a href=\"javascript:$.modaldialog.success('" + (CF.converIntoHtml(rs.getString("reason") + strNewTimeReason, "No reason provided")) + "', { title: 'Reason given!' });\"><img src=\""+request.getContextPath()+"/images1/Exclamation.png"+"\"></a>" + "</td>");
						if ((rs.getInt("approved") == 0 || rs.getInt("approved") == -2) && strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(MANAGER))) {
							sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">" + ((nNotify_Time == 0) ? "" : "<span style=\"font-size:11px\">Notification for revised timings</span><br/>")
									+ "<a onclick=\"return confirm('Are you sure you want to approve?')\"  href=\"ApproveClockEntries.action?AID=" + rs.getString("atten_id") + "&P=U\">Approve</a>"
							// +"|<a href=\"ApproveClockEntries.action?DID="+
							// rs.getString("atten_id") + "&P=U\">Deny</a></td>"
							);
						} else if (rs.getInt("approved") == 0 || rs.getInt("approved") == -2) {
							sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">Waiting For Approval</td>");
						} else if (rs.getInt("approved") == -1) {
							sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">Denied</td>");
						} else if (rs.getInt("approved") == 1) {
							sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">Approved</td>");
						}
						sb.append("</tr>");

						sb.append("<tr>");

						sb.append("<td colspan=\"6\">");
						sb.append("<form style=\"margin:0\" action=\"UpdateClockEntries.action\" >");
						sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");

						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input type=\"hidden\" value=\"" + rs.getString("_date") + "\" name=\"strCurrentDate\" >"
								+ uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname") + "<input type=\"hidden\" name=\"strEmpId\" value=\"" + rs.getString("emp_id")
								+ "\" ></td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
						sb.append("<td nowrap style=\"width:150px\" class=\"reportLabel" + strNew + " alignLeft\">ACTUAL END TIME</td>");
						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input style=\"width:65px\" type=\"text\" name=\"strEmpOUT\" value=\""
								+ uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()) + "\" ></td>");

						sb.append("<td>" + "<div id=\"popup_name" + i + "\" class=\"popup_block\">" + "<h2 class=\"textcolorWhite\">Reasons </h2>" + "<table width=\"90%\" cellSpacing=\"0\"  cellPadding=\"1\">" + "<tr><td class=\"reportLabel\">"
								+ ((rs.getString("reason") == null || (rs.getString("reason") != null && rs.getString("reason").equalsIgnoreCase("")) ? "No reason provided" : rs.getString("reason"))) + "</td></tr>" + "</table>" + "</div>" + "</td>");
						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input class=\"input_button\" style=\"width:65px\" type=\"submit\" value=\"Update\" ></td>");

						sb.append("</tr></table>");
						sb.append("</form>");
						sb.append("</td>");
						sb.append("</tr>");
					}
//					 }

				} else {
					if(uF.isThisDateValid(rs.getString("_date"), DBDATE) && rs.getString("_from")!=null && !rs.getString("_from").trim().equals("") && !rs.getString("_from").trim().equalsIgnoreCase("NULL") && rs.getString("_to")!=null && !rs.getString("_to").trim().equals("") && !rs.getString("_to").trim().equalsIgnoreCase("NULL")) {
						long frmTime = uF.getTimeStamp(rs.getString("_date") + rs.getString("_from"), DBDATE + DBTIME).getTime();
						long toTime = uF.getTimeStamp(rs.getString("_date") + rs.getString("_to"), DBDATE + DBTIME).getTime();
						long currentTime = uF.getTimeStamp("" + uF.getCurrentDate(CF.getStrTimeZone()) + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME).getTime();

//						System.out.println("UCE/1703--else="+strDateNew);
						if (frmTime < currentTime) {
	//						if (isDate && (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdRowOld)) || (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld))) {
	//							strEmpIdRowOld = strEmpIdNew;
	//							if (strNew.length() > 0) {
	//								strNew = new String();
	//							} else {
	//								strNew = "1";
	//							}
	//						}
							
							Map hmRosterStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS");
							Map hmRosterEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE");
							Map hmActualStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS");
							Map hmActualEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE");
							
							
							if(hmRosterStart==null) {
								hmRosterStart = new HashMap();
							}
							if(hmRosterEnd==null) {
								hmRosterEnd = new HashMap();
							}
							if(hmActualStart==null) {
								hmActualStart = new HashMap();
							}
							if(hmActualEnd==null) {
								hmActualEnd = new HashMap();
							}
							
							hmRosterStart.put(strEmpId, uF.showData(uF.getDateFormat(strRosterFromNew, DBTIME, CF.getStrReportTimeFormat()), "N/A"));
							hmActualStart.put(strEmpId, "");
	//						hmActualReason.put(strEmpIdOld, rs.getString("reason"));
							
							hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS", hmRosterStart);
							hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS", hmActualStart);
							
							if(!alTimeSheet.contains(strEmpId)) {
								alTimeSheet.add(strEmpId);
//								System.out.println(" strEmpId 666 ===>> " + strEmpId);
							}
							
//							System.out.println(" 666  strDateNew==>" + strDateNew + " strEmpIdNew==>" + hmEmployeeNameMap.get(strEmpId) + " str===>" + strNew.toString() + "   " + strOld.toString());
							isData = true;
							sb.append("<tr>");
							sb.append("<td colspan=\"6\">");
							sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
	
							sb.append("<td nowrap height=\"25\" width=\"105\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + hmEmployeeNameMap.get(strEmpId) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">ROSTERED START TIME</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.showData(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()), "N/A") + "</td>");
	
							sb.append("<td width=\"124\" class=\"reportLabel" + strNew + " alignLeft\">&nbsp;</td>");
	
							sb.append("</tr></table>");
							sb.append("</td>");
	
							sb.append("<td rowspan=\"2\" style=\"width:50px\" class=\"reportLabel" + strNew + " alignLeft\">" +
							// "<a href=\"#?w=500\" rel=\"popup_name" + i +
							// "\" class=\"poplight\" >View Reason</a>" +
									"&nbsp;</td>");
	
							sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">&nbsp;</td>"
							// "+|<a href=\"ApproveClockEntries.action?DID="+
							// rs.getString("atten_id") + "&P=U\">Deny</a></td>"
							);
	
							sb.append("</tr>");
							sb.append("<tr>");
	
							sb.append("<td colspan=\"6\">");
							sb.append("<form style=\"margin:0\" action=\"UpdateClockEntries.action\" >");
							sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
	
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input type=\"hidden\" value=\"" + rs.getString("_date") + "\" name=\"strCurrentDate\" >"
									+ uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + hmEmployeeNameMap.get(strEmpId) + "<input type=\"hidden\" name=\"strEmpId\" value=\"" + rs.getString("emp_id") + "\" ></td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">ACTUAL START TIME</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input type=\"text\"  style=\"width:65px\" name=\"strEmpIN\" value=\" \" ></td>");
	
							sb.append("<td>" + "<div id=\"popup_name" + i + "\" class=\"popup_block\">" + "<h2 class=\"textcolorWhite\">Reasons </h2>" + "<table width=\"90%\" cellSpacing=\"0\"  cellPadding=\"1\">" + "<tr><td class=\"reportLabel\">"
									+ ((rs.getString("reason") == null || (rs.getString("reason") != null && rs.getString("reason").equalsIgnoreCase("")) ? "No reason provided" : rs.getString("reason"))) + "</td></tr>" + "</table>" + "</div>" + "</td>");
							sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input style=\"width:65px\" class=\"input_button\" type=\"submit\" value=\"Update\" ></td>");
	
							sb.append("</tr></table>");
							sb.append("</form>");
							sb.append("</td>");
							sb.append("</tr>");
						}
	
						if (toTime < currentTime) {
	
//							System.out.println(" 777  strDateNew==>" + strDateNew + " strEmpIdNew==>" + hmEmployeeNameMap.get(strEmpId) + " str===>" + strNew.toString() + "   " + strOld.toString());
							Map hmRosterStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS");
							Map hmRosterEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE");
							Map hmActualStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS");
							Map hmActualEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE");
							
							if(hmRosterStart==null) {
								hmRosterStart = new HashMap();
							}
							if(hmRosterEnd==null) {
								hmRosterEnd = new HashMap();
							}
							if(hmActualStart==null) {
								hmActualStart = new HashMap();
							}
							if(hmActualEnd==null) {
								hmActualEnd = new HashMap();
							}
							
							hmRosterEnd.put(strEmpId, uF.showData(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()), "N/A"));
							hmActualEnd.put(strEmpId, "");
	//						hmActualReason.put(strEmpIdOld, rs.getString("reason"));
									
							hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE", hmRosterEnd);
							hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE", hmActualEnd);
							
							if(!alTimeSheet.contains(strEmpId)) {
								alTimeSheet.add(strEmpId);
//								System.out.println("UCE/1818--- strEmpId 777 ===>> " + strEmpId);
							}
							
							isData = true;
	
							sb.append("<tr>");
	
							sb.append("<td colspan=\"6\">");
							sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
	
							sb.append("<td nowrap height=\"25\" width=\"105\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + hmEmployeeNameMap.get(strEmpId) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">ROSTERED END TIME</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.showData(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()), "N/A") + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">&nbsp;</td>");
	
							sb.append("</tr></table>");
							sb.append("</td>");
	
							sb.append("<td rowspan=\"2\" style=\"width:50px\" class=\"reportLabel" + strNew + " alignLeft\">" + "&nbsp;</td>");
	
							sb.append("<td style=\"width:100px\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">&nbsp;</td>"
							// "+|<a href=\"ApproveClockEntries.action?DID="+
							// rs.getString("atten_id") + "&P=U\">Deny</a></td>"
							);
	
							sb.append("</tr>");
	
							sb.append("<tr>");
	
							sb.append("<td colspan=\"6\">");
							sb.append("<form style=\"margin:0\" action=\"UpdateClockEntries.action\" >");
							sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
	
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input type=\"hidden\" value=\"" + rs.getString("_date") + "\" name=\"strCurrentDate\" >"
									+ uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + hmEmployeeNameMap.get(strEmpId) + "<input type=\"hidden\" name=\"strEmpId\" value=\"" + rs.getString("emp_id") + "\" ></td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
							sb.append("<td nowrap style=\"width:150px\" class=\"reportLabel" + strNew + " alignLeft\">ACTUAL END TIME</td>");
							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input style=\"width:65px\" type=\"text\" name=\"strEmpOUT\" value=\" \" ></td>");
	
							sb.append("<td>" + "<div id=\"popup_name" + i + "\" class=\"popup_block\">" + "<h2 class=\"textcolorWhite\">Reasons </h2>" + "<table width=\"90%\" cellSpacing=\"0\"  cellPadding=\"0\">" + "<tr><td class=\"reportLabel\">"
									+ ((rs.getString("reason") == null || (rs.getString("reason") != null && rs.getString("reason").equalsIgnoreCase("")) ? "No reason provided" : rs.getString("reason"))) + "</td></tr>" + "</table>" + "</div>" + "</td>");
							sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input class=\"input_button\" style=\"width:65px\" type=\"submit\" value=\"Update\" ></td>");
	
							sb.append("</tr></table>");
							sb.append("</form>");
							sb.append("</td>");
							sb.append("</tr>");
						}
					}
				}
			
		
				
				strDateOld = strDateNew;
				strEmpIdOld = strEmpIdNew;
				strRosterFromOld = strRosterFromNew;
				strRosterToOld = strRosterToNew;
				nServiceIdOld = nServiceIdNew;
				strOld = strNew;
//				isOut = false;
			}
			rs.close();
			pst.close();
			
			
			StringBuilder sbQuery = new StringBuilder(); 
			sbQuery.append("select * from exception_reason where status=0 and (in_out_type='HD' or in_out_type='FD') and _date between ? and ? and emp_id in (select emp_id from employee_official_details " +
				"where is_roster=true ");
			if(strUserType!=null && strUserType.equals(MANAGER) && (getCurrUserType() == null || !getCurrUserType().equals(strBaseUserType))) {
			sbQuery.append("and supervisor_emp_id = "+uF.parseToInt(strSessionEmpId));	
			}
			if(uF.parseToInt(strReqEmpID)>0) {
				sbQuery.append(" and emp_id = "+ strReqEmpID);
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
	        	sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(getF_department()!=null && getF_department().length>0) {
            	sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if(getF_level()!=null && getF_level().length>0) {
            	sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id " +
            		"and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+")) ");
            }
			sbQuery.append(") order by emp_id,_date desc, service_id, in_out_type");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			if(uF.parseToInt(strReqEmpID)>0 && CF.isRosterDependency(con, strReqEmpID)) {
//				System.out.println("UCE/1879---3 pst==5====> " + pst);
				rs = pst.executeQuery();
			} else {
//				System.out.println("UCE/1882---3 pst==6====> " + pst);  
				rs = pst.executeQuery();
			}
			Map<String, List<List<String>>> hmEmpHDFDException = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<List<String>> alData = hmEmpHDFDException.get(rs.getString("emp_id"));
				if(alData == null) alData = new ArrayList<List<String>>();
				if(!alTimeSheet.contains(rs.getString("emp_id"))) {
					alTimeSheet.add(rs.getString("emp_id"));
				}
				if(!alDt.contains(rs.getString("_date"))) {
					alDt.add(rs.getString("_date"));
				}
				if(!alService.contains(rs.getString("service_id"))) {
					alService.add(rs.getString("service_id"));
				}
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("emp_id"));
				innerList.add(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT_STR));
				innerList.add(rs.getString("in_out_type"));
				innerList.add(uF.getRoundOffValue(2, rs.getDouble("hours_worked")));
				innerList.add(rs.getString("given_reason")); //4
				innerList.add(rs.getString("service_id")); //5
				innerList.add(rs.getString("exception_id")); //6
//				System.out.println("emp_id ===>> " + rs.getString("emp_id") + " -- SupervisorId ===>> " + hmEmpSupervisorId.get(rs.getString("emp_id")));
				
				alData.add(innerList);
				
				hmEmpHDFDException.put(rs.getString("emp_id")+"_"+rs.getString("_date"), alData);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmEmpHDFDException ===>> " + hmEmpHDFDException);
			
			request.setAttribute("hmEmpHDFDException", hmEmpHDFDException);
			
//			System.out.println(" alTimeSheet out of RS ===>> " + alTimeSheet);

			/*if (isIn && !isOut) {
				long frmTime = uF.getTimeStamp(strDateNew + strRosterFromNew, DBDATE + DBTIME).getTime();
				long toTime = uF.getTimeStamp(strDateNew + strRosterToNew, DBDATE + DBTIME).getTime();
				long currentTime = uF.getTimeStamp("" + uF.getCurrentDate(CF.getStrTimeZone()) + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME).getTime();

				if (toTime < currentTime) {

//					System.out.println(" 888  strDateNew==>" + strDateNew + " strEmpIdNew==>" + hmEmployeeNameMap.get(strEmpIdOld) + " str===>" + strNew.toString() + "   " + strOld.toString());

					
					Map hmRosterStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS");
					Map hmRosterEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE");
					Map hmActualStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS");
					Map hmActualEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE");
					
					
					if(hmRosterStart==null) {
						hmRosterStart = new HashMap();
					}
					if(hmRosterEnd==null) {
						hmRosterEnd = new HashMap();
					}
					if(hmActualStart==null) {
						hmActualStart = new HashMap();
					}
					if(hmActualEnd==null) {
						hmActualEnd = new HashMap();
					}
					
					
					hmRosterEnd.put(strEmpIdOld, uF.showData(uF.getDateFormat(strRosterToOld, DBTIME, CF.getStrReportTimeFormat()), "N/A"));
					hmActualEnd.put(strEmpIdOld, "");
//					hmActualReason.put(strEmpIdOld, rs.getString("reason"));
					
							
					hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE", hmRosterEnd);
					hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE", hmActualEnd);
					
					if(!alTimeSheet.contains(strEmpIdOld)) {
						alTimeSheet.add(strEmpIdOld);
//						System.out.println(" strEmpIdOld 3 ===>> " + strEmpIdOld);
					}
					
					isData = true;

					sb.append("<tr>");

					sb.append("<td colspan=\"6\">");
					sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");

					sb.append("<td nowrap height=\"25\" width=\"105\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDayFormat()) + "</td>");
					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat()) + "</td>");
					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + hmEmployeeNameMap.get(strEmpIdOld) + "</td>");
					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">ROSTERED END TIME</td>");
					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.showData(uF.getDateFormat(strRosterToOld, DBTIME, CF.getStrReportTimeFormat()), "N/A") + "</td>");
					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">&nbsp;</td>");

					sb.append("</tr></table>");
					sb.append("</td>");

					sb.append("<td rowspan=\"2\" style=\"width:50px\" class=\"reportLabel" + strOld + " alignLeft\">" + "&nbsp;</td>");

					sb.append("<td style=\"width:100px\" rowspan=\"2\" class=\"reportLabel" + strOld + " alignLeft\">" + "&nbsp;</td>");

					sb.append("</tr>");

					sb.append("<tr>");

					sb.append("<td colspan=\"6\">");
					sb.append("<form style=\"margin:0\" action=\"UpdateClockEntries.action\" >");
					sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");

					sb.append("<td nowrap width=\"100\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDayFormat()) + "</td>");
					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input type=\"hidden\" value=\"" + strDateOld + "\" name=\"strCurrentDate\" >" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())
							+ "</td>");
					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + hmEmployeeNameMap.get(strEmpIdOld) + "<input type=\"hidden\" name=\"strEmpId\" value=\"" + strEmpIdOld + "\" ></td>");
					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
					sb.append("<td nowrap style=\"width:150px\" class=\"reportLabel" + strOld + " alignLeft\">ACTUAL END TIME</td>");
					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input style=\"width:65px\" type=\"text\" name=\"strEmpOUT\" value=\"\" ></td>");

					sb.append("<td>&nbsp;</td>");
					sb.append("<td width=\"77\" class=\"reportLabel" + strOld + " alignLeft\"><input class=\"input_button\" style=\"width:65px\" type=\"submit\" value=\"Update\" ></td>");

					sb.append("</tr></table>");
					sb.append("</form>");
					sb.append("</td>");
					sb.append("</tr>");
				}

			} else if (!isIn && isOut) {

				isData = true;

//				System.out.println(" 999  strDateNew==>" + strDateNew + " strEmpIdNew==>" + hmEmployeeNameMap.get(strEmpIdOld) + " str===>" + strNew.toString() + "   " + strOld.toString());
				Map hmRosterStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS");
				Map hmRosterEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE");
				Map hmActualStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS");
				Map hmActualEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE");
				
				if(hmRosterStart==null) {
					hmRosterStart = new HashMap();
				}
				if(hmRosterEnd==null) {
					hmRosterEnd = new HashMap();
				}
				if(hmActualStart==null) {
					hmActualStart = new HashMap();
				}
				if(hmActualEnd==null) {
					hmActualEnd = new HashMap();
				}
				
				hmRosterStart.put(strEmpIdOld, uF.showData(uF.getDateFormat(strRosterFromOld, DBTIME, CF.getStrReportTimeFormat()), "N/A"));
				hmActualStart.put(strEmpIdOld, "");
//				hmActualReason.put(strEmpIdOld, rs.getString("reason"));
				
				hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS", hmRosterStart);
				hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS", hmActualStart);
				
				if(!alTimeSheet.contains(strEmpIdOld)) {
					alTimeSheet.add(strEmpIdOld);
//					System.out.println(" strEmpIdOld 4 ===>> " + strEmpIdOld);
				}

				sb.append("<tr>");
				sb.append("<td colspan=\"6\">");
				sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");

				sb.append("<td nowrap height=\"25\" width=\"105\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDayFormat()) + "</td>");
				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat()) + "</td>");
				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + hmEmployeeNameMap.get(strEmpIdOld) + "</td>");
				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">ROSTERED START TIME</td>");
				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.showData(uF.getDateFormat(strRosterFromOld, DBTIME, CF.getStrReportTimeFormat()), "N/A") + "</td>");

				sb.append("<td width=\"116\" class=\"reportLabel" + strOld + " alignLeft\">&nbsp;</td>");

				sb.append("</tr></table>");
				sb.append("</td>");

				sb.append("<td rowspan=\"2\" style=\"width:50px\" class=\"reportLabel" + strOld + " alignLeft\">&nbsp;</td>");

				sb.append("<td style=\"width:100px\" rowspan=\"2\" class=\"reportLabel" + strOld + " alignLeft\">&nbsp;</td>");

				sb.append("</tr>");
				sb.append("<tr>");

				sb.append("<td colspan=\"6\">");
				sb.append("<form style=\"margin:0\" action=\"UpdateClockEntries.action\" >");
				sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");

				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDayFormat()) + "</td>");
				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input type=\"hidden\" value=\"" + strDateOld + "\" name=\"strCurrentDate\" >" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())
						+ "</td>");
				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + hmEmployeeNameMap.get(strEmpIdOld) + "<input type=\"hidden\" name=\"strEmpId\" value=\"" + strEmpIdOld + "\" ></td>");
				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">ACTUAL START TIME</td>");
				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input type=\"text\"  style=\"width:65px\" name=\"strEmpIN\" value=\" \" ></td>");

				sb.append("<td>&nbsp;</td>");
				sb.append("<td style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input style=\"width:65px\" class=\"input_button\" type=\"submit\" value=\"Update\" ></td>");

				sb.append("</tr></table>");
				sb.append("</form>");
				sb.append("</td>");
				sb.append("</tr>");
			}

			if (i == 1 || !isData) {
				sb.append("<tr><td class=\"label alignCenter\">No exceptions found for this employee.</td></tr>");
			}

			sb.append("</table>");*/
			
			Map<String, String> hmEmpData = new HashMap<String, String>();
			pst = con.prepareStatement(selectEmployee1Details);
			pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
//			System.out.println("4 pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				hmEmpData.put("NAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpData", hmEmpData);
		
			
			pst = con.prepareStatement("select * from exception_reason where _date between ? and ? order by emp_id");
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("UCE/2118--5 pst======>"+pst);
			rs = pst.executeQuery();
			Map hmExceptionReason = new HashMap();
			Map hmInner = null;
			
			strEmpIdNew = null;
			strEmpIdOld = null;
			String appliedExceptionCount = "";		//===added by parvez on 26-07-2021====
			while(rs.next()) {
				strEmpIdNew = rs.getString("emp_id");
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					hmInner = new HashMap();
				}
				
				hmInner.put(rs.getString("_date")+"_"+rs.getString("service_id")+"_"+rs.getString("in_out_type"), rs.getString("given_reason"));
				hmInner.put(rs.getString("_date")+"_"+rs.getString("service_id")+"_"+rs.getString("in_out_type")+"_STATUS", rs.getString("status"));
				
				// Started By Dattatray Date:07-10-21
				hmInner.put(rs.getString("_date")+"_"+rs.getString("service_id")+"_"+rs.getString("in_out_type")+"_IN", rs.getString("in_timestamp"));
				hmInner.put(rs.getString("_date")+"_"+rs.getString("service_id")+"_"+rs.getString("in_out_type")+"_IN_TIME", rs.getString("in_timestamp"));
				hmInner.put(rs.getString("_date")+"_"+rs.getString("service_id")+"_"+rs.getString("in_out_type")+"_OUT_TIME", rs.getString("out_timestamp"));
				// Ended By Dattatray Date:07-10-21
				hmInner.put(rs.getString("_date")+"_"+rs.getString("service_id")+"_"+rs.getString("in_out_type")+"_MANAGER_REASON", rs.getString("approve_by_reason"));//Start Dattatray Date:10-11-21 Note:added approve_by_reason
				hmExceptionReason.put(rs.getString("emp_id"), hmInner);
				
				strEmpIdOld = strEmpIdNew ;
				//===start parvez on 26-07-2021===
				if(uF.parseToInt(rs.getString("emp_id")) == uF.parseToInt(strReqEmpID)){
					appliedExceptionCount = (uF.parseToInt(appliedExceptionCount)+1)+"";
				}
				//===end parvez on 26-07-2021====
			}
			rs.close();
			pst.close();
			request.setAttribute("appliedExceptionCount",appliedExceptionCount);
			request.setAttribute("hmExceptionReason", hmExceptionReason);
			
			Map hmGenderMap = CF.getEmpGenderMap(con);
			
			request.setAttribute("hmGenderMap", hmGenderMap);
			
			
			leaveTypeList = new FillLeaveType(request).fillLeave(uF.parseToInt((String)hmEmpLevelMap.get(getStrSelectedEmpId())), uF.parseToInt(getStrSelectedEmpId()));
			
			pst = con.prepareStatement("select * from roster_details where _date between ? and ? and emp_id =?");
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
//			System.out.println("UCE/2166---6 pst======>"+pst);
			rs = pst.executeQuery();
			Map hmRoster = new HashMap();
			
			while(rs.next()) {
				hmRoster.put(rs.getString("_date")+"_"+rs.getString("service_id")+"_IN", rs.getString("_from"));
				hmRoster.put(rs.getString("_date")+"_"+rs.getString("service_id")+"_OUT", rs.getString("_to"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmRoster", hmRoster);
			StringBuilder sbEmp = null;
//			System.out.println("alTimeSheet ===>>>> " + alTimeSheet);
			for(int k = 0; alTimeSheet!=null && k < alTimeSheet.size(); k++ ) {
				if(sbEmp == null) {
					sbEmp = new StringBuilder();
					sbEmp.append(""+alTimeSheet.get(k));
				} else {
					sbEmp.append(","+alTimeSheet.get(k));
				}
			}
			
			
			if(sbEmp !=null) {
//				System.out.println("sbEmp=====>"+sbEmp.toString());
				Map<String, List<String>> hmEmpSbu = new HashMap<String, List<String>>();
				pst = con.prepareStatement("SELECT * FROM employee_official_details eod, employee_personal_details epd WHERE epd.emp_per_id=eod.emp_id " +
						"and eod.emp_id in ("+sbEmp.toString()+") order by emp_id");
				rs = pst.executeQuery();
				while (rs.next()) {
					String strSbu = rs.getString("service_id");
					
					List<String> al = new ArrayList<String>();
					if(strSbu !=null && !strSbu.trim().equals("")) {
						List<String> alTemp = Arrays.asList(strSbu.split(","));
						for(String str : alTemp) {
							if(uF.parseToInt(str) > 0) {
								al.add(str);
							}
						}
					}
					hmEmpSbu.put(rs.getString("emp_id"), al);
				}
				rs.close();
				pst.close();
				
				request.setAttribute("hmEmpSbu", hmEmpSbu);
			}
			
		} catch (Exception e) {
			e.printStackTrace(); 
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
		request.setAttribute("TIMESHEET", sb.toString());
		request.setAttribute("TIMESHEET_", hmTimeSheet);
//		System.out.println("UCE/2209--TIMESHEET="+sb.toString());
		request.setAttribute("TIMESHEET_EMP", alTimeSheet);
		request.setAttribute("TIMESHEET_EMPNAME", hmEmployeeNameMap);
		request.setAttribute("TIMESHEET_SERVICENAME", hmServicesMap);
		request.setAttribute("TIMESHEET_DATE", alDt);
		request.setAttribute("TIMESHEET_SERVICE", alService);
		
		return SUCCESS;
	}
	
	
	public String viewClockEntriesExceptionCount(String strReqEmpID, String strServiceId, UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		Map hmTimeSheet = new HashMap();
		List alTimeSheet = new ArrayList();
		List alService = new ArrayList();
		List alDt = new ArrayList();
		
		Map hmEmployeeNameMap = null;
		Map hmServicesMap = null;
		
		try {

			con = db.makeConnection(con);
			
			hmServicesMap = CF.getServicesMap(con, true);
			hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
			Map hmTardyType = CF.getTardyType(con);
			
			Map hmLeaves = CF.getActualLeaveDates(con, CF, uF, strD1, strD2, null, false, null);
			
			Map hmRowColour = CF.getTardyType(con);
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEndDates = CF.getWeekEndDateList(con, strD1, strD2, CF, uF,hmWeekEndHalfDates,null);
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF,strD1, strD2,alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEndDates,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			
			Map<String,String> hmHolidays = new HashMap<String,String>();
			Map<String,String> hmHolidayDates = new HashMap<String,String>(); 
			
			CF.getHolidayList(con, request,strD1, strD2, CF, hmHolidayDates, hmHolidays, true);
			
			request.setAttribute("hmWeekEndHalfDates1", hmWeekEndHalfDates);
			request.setAttribute("hmWeekEndDates1", hmWeekEndDates);
			request.setAttribute("hmEmpWlocation1", hmEmpWlocation);
			request.setAttribute("alEmpCheckRosterWeektype1", alEmpCheckRosterWeektype);
			request.setAttribute("hmRosterWeekEndDates1", hmRosterWeekEndDates);
			request.setAttribute("hmHolidays1", hmHolidays);
			request.setAttribute("hmHolidayDates1", hmHolidayDates);
			
			if (strReqEmpID != null && strReqServiceId!=null) {
				
//				System.out.println("pst===> 1");
				
				pst = con.prepareStatement(selectClockEntriesR1_ES);
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strReqEmpID));
				pst.setDate(4, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(6, uF.parseToInt(strReqEmpID));
				pst.setInt(7, uF.parseToInt(strReqServiceId));
				
//				System.out.println("3 pst==1====>"+pst);
			} else if (strReqEmpID != null && uF.parseToInt(strReqEmpID)>0) {
//				System.out.println("pst===> 2");
				
				pst = con.prepareStatement(selectClockEntriesR1_E);
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strReqEmpID));
				pst.setDate(4, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(6, uF.parseToInt(strReqEmpID));
//				System.out.println("3 pst===2===>"+pst);
			} else if(strUserType!=null && strUserType.equals(MANAGER) && (getCurrUserType() == null || !getCurrUserType().equals(strBaseUserType))) {
				
//				System.out.println("pst===> 3");	
				
				pst = con.prepareStatement("select * from (SELECT * FROM  (Select *,rd.emp_id as empid, rd.service_id as serviceid from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad WHERE TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? ORDER BY in_out_timestamp desc) a FULL JOIN roster_details rd ON a.emp_id=rd.emp_id and a.service_id=rd.service_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date ) t WHERE t._date BETWEEN ? AND ?  order by empid, serviceid, in_out ) ab, employee_personal_details epd  where ab.empid=epd.emp_per_id and ab.empid in (select emp_id from employee_official_details where is_roster=true and supervisor_emp_id  = ? ) order by _date desc, empid, serviceid, in_out");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt((String)session.getAttribute(EMPID)));
//				System.out.println("3 from cnt pst====3==>"+pst);
			} else {
//				pst = con.prepareStatement(selectClockEntriesR1_E2);

				StringBuilder sbQuery = new StringBuilder(); 
				sbQuery.append("select * from (select serviceid as service_id,serviceid,approved,notify_time,new_time,_date,empid,_to,_from,empid as emp_id,reason," +
						"in_out,early_late,emp_fname,emp_mname,emp_lname,atten_id,in_out_timestamp from (SELECT * FROM (Select *,rd.emp_id as empid, " +
						"rd.service_id as serviceid from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad WHERE " +
						"TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and ad.emp_id in (select emp_id from employee_official_details " +
						"where is_roster=true ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
				sbQuery.append(") and ad.approved=-2 ORDER BY in_out_timestamp desc) a FULL JOIN (select * from roster_details where " +
						"roster_id in (select rd1.roster_id from roster_details rd1,attendance_details ad1 WHERE " +
						"TO_DATE(ad1.in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and ad1.emp_id in (select emp_id from employee_official_details " +
						"where is_roster=true ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
				sbQuery.append(") and ad1.approved=-2 and ad1.emp_id=rd1.emp_id and ad1.service_id=rd1.service_id " +
						"and TO_DATE(ad1.in_out_timestamp::text, 'YYYY-MM-DD')=rd1._date)) rd ON a.emp_id=rd.emp_id and a.service_id=rd.service_id " +
						"and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date) t WHERE t._date BETWEEN ? AND ?  order by empid, serviceid, in_out ) ab," +
						" employee_personal_details epd  where ab.empid=epd.emp_per_id and ab.empid in (select emp_id from employee_official_details " +
						"where is_roster=true ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
				sbQuery.append(")");
				sbQuery.append(" union ");
				sbQuery.append("select serviceid as service_id,serviceid,approved,notify_time,new_time,_date,empid,_to,_from,empid as emp_id,reason,in_out,early_late," +
						"emp_fname,emp_mname,emp_lname,atten_id,in_out_timestamp from (SELECT * FROM  (Select *,rd.emp_id as empid, rd.service_id as serviceid " +
						"from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad WHERE TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') " +
						"BETWEEN ? AND ? and ad.emp_id in (select emp_id from employee_official_details where is_roster=true ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				sbQuery.append(") ORDER BY in_out_timestamp desc) a FULL JOIN (select * from roster_details rd1,(select rd.roster_id,ad.atten_id as attenid " +
						"from roster_details rd left join attendance_details ad ON ad.emp_id=rd.emp_id and ad.service_id=rd.service_id " +
						"and TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date and TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD') " +
						"BETWEEN ? AND ? and TO_DATE(rd._date::text, 'YYYY-MM-DD') BETWEEN ? AND ? and rd.emp_id in (select emp_id from employee_official_details " +
						" where is_roster=true ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
				sbQuery.append(") group by rd.roster_id,ad.atten_id having (atten_id is null or atten_id =0)) aa where rd1.roster_id=aa.roster_id and " +
						"TO_DATE(rd1._date::text, 'YYYY-MM-DD') BETWEEN ? AND ? and rd1.emp_id in (select emp_id from employee_official_details  " +
						"where is_roster=true ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
				sbQuery.append(")) rd ON a.emp_id=rd.emp_id and a.service_id=rd.service_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date) t " +
						"WHERE t._date BETWEEN ? AND ? order by empid, serviceid, in_out) ab," +
						" employee_personal_details epd  where ab.empid=epd.emp_per_id and ab.empid in (select emp_id from employee_official_details " +
						"where is_roster=true ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
				sbQuery.append(")) as a order by _date desc, empid,service_id, in_out");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT)); 
				pst.setDate(9, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(10, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(11, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(12, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(13, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(14, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(15, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(16, uF.getDateFormat(strD2, DATE_FORMAT));
//				System.out.println("3 pst====4==>"+pst);
			}
			
			if(uF.parseToInt(strReqEmpID)>0 && CF.isRosterDependency(con,strReqEmpID)) {
//				System.out.println("3 pst==5====>"+pst);
				rs = pst.executeQuery();
			} else {
//				System.out.println("3 pst==6====>"+pst);
				rs = pst.executeQuery();
			}

//			sb.append("<table style=\"table-layout:fixed;width:920px\" width=\"920px\" cellpadding=\"0\" cellspacing=\"1\">");

			// StringBuilder str = new StringBuilder();
			String strNew = "";
			String strOld = "";
			int i = 1;
			String strEmpId = "";

			String strDateNew = null;
			String strDateOld = null;
			String strEmpIdRowOld = null;
			String strEmpIdOld = null;
			String strEmpIdNew = null;
			String strRosterFromNew = null;
			String strRosterFromOld = null;
			String strRosterToNew = null;
			String strRosterToOld = null;

			boolean isIn = false;
			boolean isOut = false;
			boolean isData = false;
			int nApproved = 0;
			int nNotify_Time = 0;
			String strNotify_Time = null;
			int nServiceIdNew = 0;
			int nServiceIdOld = 0;
			int empRowCount=0;
			boolean isChange = false;
			
			
			while (rs!=null && rs.next()) {

				
//				System.out.println("====="+rs.getString("in_out_timestamp"));
				
				nServiceIdNew = rs.getInt("service_id");
				nApproved = rs.getInt("approved");
				nNotify_Time = rs.getInt("notify_time");
				if(rs.getString("new_time")!=null) {
					strNotify_Time = uF.getDateFormat(rs.getString("new_time"), DBTIME, CF.getStrReportTimeFormat());
				}
				
				i++;   

				strDateNew = rs.getString("_date");
//				strEmpIdNew = rs.getString("emp_id");
//				strEmpIdNew = rs.getString("empl_id");
				strEmpIdNew = rs.getString("empid");
				strRosterToNew = rs.getString("_to");
				strRosterFromNew = rs.getString("_from");

//				System.out.println("strEmpIdNew==>"+strEmpIdNew);
				
				/*System.out.println("strDateNew====="+strDateNew);
				
				System.out.println("strUserType====="+strUserType);
				System.out.println("CF.isRosterDependency(strEmpIdNew)====="+CF.isRosterDependency(strEmpIdNew));
				System.out.println("strEmpIdNew====="+strEmpIdNew);
				*/
				
				if(strUserType!=null && (strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(CONSULTANT)) && !CF.isRosterDependency(con,strEmpIdNew)){
					continue;
				}
				
				if(!alService.contains(nServiceIdNew+"")) {
					alService.add(nServiceIdNew+"");
				}
				
				Map hmEmpLeave = (Map)hmLeaves.get(strEmpIdNew);
				if(hmEmpLeave==null)hmEmpLeave=new HashMap();
				
//				System.out.println("strEmpIdNew===="+strEmpIdNew+" strDateNew="+strDateNew+" alDt==>"+alDt+" hmHolidayDates="+hmHolidayDates+" hmEmpLeave="+hmEmpLeave);
				
				if(!alDt.contains(strDateNew) && !hmHolidayDates.containsKey(uF.getDateFormat(strDateNew, DBDATE, DATE_FORMAT)) && !hmEmpLeave.containsKey(uF.getDateFormat(strDateNew, DBDATE, DATE_FORMAT))) {
					alDt.add(strDateNew);
				}
				
				if (((strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) || (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld)) || (nServiceIdNew != nServiceIdOld)) && i > 1) {

					long frmTime = 0;
					long toTime = 0;
					long currentTime = 0;

					if (strRosterFromOld != null) {
						frmTime = uF.getTimeStamp(strDateOld + strRosterFromOld, DBDATE + DBTIME).getTime();
					}

					if (strRosterToOld != null) {
						toTime = uF.getTimeStamp(strDateOld + strRosterToOld, DBDATE + DBTIME).getTime();
					}

					currentTime = uF.getTimeStamp("" + uF.getCurrentDate(CF.getStrTimeZone()) + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME).getTime();

					if (!isIn && isOut) {

						if ((frmTime == 0) || (frmTime > 0 && frmTime < currentTime)) {

							isData = true;

							Map hmRosterStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_RS");
							Map hmRosterEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_RE");
							Map hmActualStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_AS");
							Map hmActualEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_AE");
							
							if(hmRosterStart==null) {
								hmRosterStart = new HashMap();
							}
							if(hmRosterEnd==null) {
								hmRosterEnd = new HashMap();
							}
							if(hmActualStart==null) {
								hmActualStart = new HashMap();
							}
							if(hmActualEnd==null) {
								hmActualEnd = new HashMap();
							}
							
							hmRosterStart.put(strEmpIdOld, uF.showData(uF.getDateFormat(strRosterFromOld, DBTIME, CF.getStrReportTimeFormat()), "N/A"));
							hmActualStart.put(strEmpIdOld, "");
//							hmActualReason.put(strEmpIdOld, rs.getString("reason"));

							hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_RS", hmRosterStart);
							hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_RE", hmRosterEnd);
							hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_AS", hmActualStart);
							hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_AE", hmActualEnd);
							
							if(strEmpIdOld!=null && !alTimeSheet.contains(strEmpIdOld)) {
								alTimeSheet.add(strEmpIdOld);
//								System.out.println(" strEmpIdOld 5 ===>> " + strEmpIdOld);
							}
							
							
							
//							if (isDate && (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdRowOld)) || (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld))) {
//								strEmpIdRowOld = strEmpIdNew;
//								if (strNew.length() > 0) {
//									strNew = new String();
//								} else {
//									strNew = "1";
//								}
//							}
							
//							sb.append("<tr>");
//							sb.append("<td colspan=\"6\">");
//							sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
//
//							sb.append("<td nowrap height=\"25\" width=\"100\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDayFormat()) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat()) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + hmEmployeeNameMap.get(strEmpIdOld) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdOld + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdOld + "\" ></td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">ROSTERED START TIME</td>");
//							sb.append("<td style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.showData(uF.getDateFormat(strRosterFromOld, DBTIME, CF.getStrReportTimeFormat()), "N/A") + "</td>");
//
//							sb.append("<td style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">&nbsp;</td>");
//
//							sb.append("</tr></table>");
//							sb.append("</td>");
//
//							sb.append("<td rowspan=\"2\" style=\"width:50px\" class=\"reportLabel" + strOld + " alignLeft\">" +
							// "<a href=\"#?w=500\" rel=\"popup_name" + i +
							// "\" class=\"poplight\" >View Reason</a>" +
							// "<a href=\"javascript:$.modaldialog.success('" +
							// (new
							// CommonFunctions().converIntoHtml(rs.getString("reason"),
							// "No reason provided")) +
							// "', { title: 'Reason given!' });\">View Reason</a>"
							// + "</td>");
//									"&nbsp;</td>");

//							sb.append("<td style=\"width:100px\" rowspan=\"2\" class=\"reportLabel" + strOld + " alignLeft\">&nbsp;</td>"
							// "+|<a href=\"ApproveClockEntries.action?DID="+
							// rs.getString("atten_id") + "&P=U\">Deny</a></td>"
//							);

							// if ((rs.getInt("approved") == 0 ||
							// rs.getInt("approved") == -2) && strUserType !=
							// null && (strUserType.equalsIgnoreCase(ADMIN) ||
							// strUserType.equalsIgnoreCase(MANAGER))) {
							// sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel"
							// + str +
							// " alignLeft\"><a onclick=\"return confirm('Are you sure you want to approve?')\" href=\"ApproveClockEntries.action?AID="
							// + rs.getString("atten_id") + "&P=U\">Approve</a>"
							// //"+|<a href=\"ApproveClockEntries.action?DID="+
							// rs.getString("atten_id") + "&P=U\">Deny</a></td>"
							// );
							// } else if (rs.getInt("approved") == 0 ||
							// rs.getInt("approved") == -2) {
							// sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel"
							// + str +
							// " alignLeft\">Waiting For Approval</td>");
							// } else if (rs.getInt("approved") == -1) {
							// sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel"
							// + str + " alignLeft\">Denied</td>");
							// } else if (rs.getInt("approved") == 1) {
							// sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel"
							// + str + " alignLeft\">Approved</td>");
							// }

//							sb.append("</tr>");
//							sb.append("<tr>");
//
//							sb.append("<td colspan=\"6\">");
//							sb.append("<form style=\"margin:0\" action=\"UpdateClockEntries.action\" >");
//							sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
//
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDayFormat()) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input type=\"hidden\" value=\"" + strDateOld + "\" name=\"strCurrentDate\" >"
//									+ uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat()) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + hmEmployeeNameMap.get(strEmpIdOld) + "<input type=\"hidden\" name=\"strEmpId\" value=\"" + rs.getString("emp_id") + "\" ></td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdOld + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdOld + "\" ></td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">ACTUAL START TIME</td>");
//							sb.append("<td style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input type=\"text\"  style=\"width:65px\" name=\"strEmpIN\" value=\" \" ></td>");
//
//							sb.append("<td>" + "<div id=\"popup_name" + i + "\" class=\"popup_block\">" + "<h2 class=\"textcolorWhite\">Reasons </h2>" + "<table width=\"90%\" cellSpacing=\"1\"  cellPadding=\"1\">" + "<tr><td class=\"reportLabel\">"
//									+ ((rs.getString("reason") == null || (rs.getString("reason") != null && rs.getString("reason").equalsIgnoreCase("")) ? "No reason provided" : rs.getString("reason"))) + "</td></tr>" + "</table>" + "</div>" + "</td>");
//							sb.append("<td style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input style=\"width:65px\" class=\"input_button\" type=\"submit\" value=\"Update\" ></td>");
//
//							sb.append("</tr></table>");
//							sb.append("</form>");
//							sb.append("</td>");
//							sb.append("</tr>");
						}

					}

					if (isIn && !isOut) {

						if ((toTime == 0) || (toTime > 0 && toTime < currentTime)) {

							
//							if (isDate && (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdRowOld)) || (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld))) {
//								strEmpIdRowOld = strEmpIdNew;
//								if (strNew.length() > 0) {
//									strNew = new String();
//								} else {
//									strNew = "1";
//								}
//							}
							
							Map hmRosterStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_RS");
							Map hmRosterEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_RE");
							Map hmActualStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_AS");
							Map hmActualEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_AE");
							
							if(hmRosterStart==null) {
								hmRosterStart = new HashMap();
							}
							if(hmRosterEnd==null) {
								hmRosterEnd = new HashMap();
							}
							if(hmActualStart==null) {
								hmActualStart = new HashMap();
							}
							if(hmActualEnd==null) {
								hmActualEnd = new HashMap();
							}
							
							
							hmRosterEnd.put(strEmpIdOld, uF.showData(uF.getDateFormat(strRosterToOld, DBTIME, CF.getStrReportTimeFormat()), "N/A"));
							hmActualEnd.put(strEmpIdOld, "");
//							hmActualReason.put(strEmpIdOld, rs.getString("reason"));
									
							hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_RS", hmRosterStart);
							hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_RE", hmRosterEnd);
							hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_AS", hmActualStart);
							hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_AE", hmActualEnd);
							if(!alTimeSheet.contains(strEmpIdOld)) {
								alTimeSheet.add(strEmpIdOld);
//								System.out.println(" strEmpIdOld 6 ===>> " + strEmpIdOld);
							}
							
//							System.out.println(" 222  strDateNew==>" + strDateNew + " strEmpIdNew==>" + hmEmployeeNameMap.get(strEmpIdNew) + " strEmpIdOld==>" + hmEmployeeNameMap.get(strEmpIdOld) + " str===>" + " str===>" + strNew.toString() + "   " + strOld.toString()+" empRowCount="+empRowCount);
							
							
							isData = true;

//							sb.append("<tr>");
//
//							sb.append("<td colspan=\"6\">");
//							sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
//
//							sb.append("<td height=\"25\" width=\"100\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDayFormat()) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat()) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + hmEmployeeNameMap.get(strEmpIdOld) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdOld + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdOld + "\" ></td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">ROSTERED END TIME</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.showData(uF.getDateFormat(strRosterToOld, DBTIME, CF.getStrReportTimeFormat()), "N/A") + "</td>");
//							sb.append("<td style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">&nbsp;</td>");
//
//							sb.append("</tr></table>");
//							sb.append("</td>");
//
//							sb.append("<td rowspan=\"2\" style=\"width:50px\" class=\"reportLabel" + strOld + " alignLeft\">" +
//							// "<a href=\"#?w=500\" rel=\"popup_name" + i +
//							// "\" class=\"poplight\" >View Reason</a>" +
//							// "<a href=\"javascript:$.modaldialog.success('" +
//							// (new
//							// CommonFunctions().converIntoHtml(rs.getString("reason"),
//							// "No reason provided")) +
//							// "', { title: 'Reason given!' });\">View Reason</a>"
//							// + "</td>");
//									"&nbsp;</td>");

//							sb.append("<td style=\"width:100px\" rowspan=\"2\" class=\"reportLabel" + strOld + " alignLeft\">"
//							// +"|<a href=\"ApproveClockEntries.action?DID="+
//							// rs.getString("atten_id") + "&P=U\">Deny</a></td>"
//									+ "&nbsp;</td>");

							// if ((rs.getInt("approved") == 0 ||
							// rs.getInt("approved") == -2) && strUserType !=
							// null && (strUserType.equalsIgnoreCase(ADMIN) ||
							// strUserType.equalsIgnoreCase(MANAGER))) {
							// sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel"
							// + str +
							// " alignLeft\"><a onclick=\"return confirm('Are you sure you want to approve?')\"  href=\"ApproveClockEntries.action?AID="
							// + rs.getString("atten_id") + "&P=U\">Approve</a>"
							// //+"|<a href=\"ApproveClockEntries.action?DID="+
							// rs.getString("atten_id") + "&P=U\">Deny</a></td>"
							// );
							// } else if (rs.getInt("approved") == 0 ||
							// rs.getInt("approved") == -2) {
							// sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel"
							// + str +
							// " alignLeft\">Waiting For Approval</td>");
							// } else if (rs.getInt("approved") == -1) {
							// sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel"
							// + str + " alignLeft\">Denied</td>");
							// } else if (rs.getInt("approved") == 1) {
							// sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel"
							// + str + " alignLeft\">Approved</td>");
							// }
//							sb.append("</tr>");
//
//							sb.append("<tr>");
//
//							sb.append("<td colspan=\"6\">");
//							sb.append("<form style=\"margin:0\" action=\"UpdateClockEntries.action\" >");
//							sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
//
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDayFormat()) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input type=\"hidden\" value=\"" + strDateOld + "\" name=\"strCurrentDate\" >"
//									+ uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat()) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + hmEmployeeNameMap.get(strEmpIdOld) + "<input type=\"hidden\" name=\"strEmpId\" value=\"" + rs.getString("emp_id") + "\" ></td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdOld + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdOld + "\" ></td>");
//							sb.append("<td nowrap style=\"width:150px\" class=\"reportLabel" + strOld + " alignLeft\">ACTUAL END TIME</td>");
//							sb.append("<td style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input style=\"width:65px\" type=\"text\" name=\"strEmpOUT\" value=\" \" ></td>");
//
//							sb.append("<td>" + "<div id=\"popup_name" + i + "\" class=\"popup_block\">" + "<h2 class=\"textcolorWhite\">Reasons </h2>" + "<table width=\"90%\" cellSpacing=\"0\"  cellPadding=\"1\">" + "<tr><td class=\"reportLabel\">"
//									+ ((rs.getString("reason") == null || (rs.getString("reason") != null && rs.getString("reason").equalsIgnoreCase("")) ? "No reason provided" : rs.getString("reason"))) + "</td></tr>" + "</table>" + "</div>" + "</td>");
//							sb.append("<td style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input class=\"input_button\" style=\"width:65px\" type=\"submit\" value=\"Update\" ></td>");
//
//							sb.append("</tr></table>");
//							sb.append("</form>");
//							sb.append("</td>");
//							sb.append("</tr>");

						}
					}
					isIn = false;
					isOut = false;

					// if(str.length()==1) {
					// str = new StringBuilder("");
					// } else {
					// str = new StringBuilder("1");
					// }
				}

				strEmpId = rs.getString("emp_id");
				if ("IN".equalsIgnoreCase(rs.getString("in_out"))) {

					double dblTardyIn = uF.parseToDouble((String) hmTardyType.get("TARDY_IN"));

					isIn = true;
//					 if(rs.getDouble("early_late")>0 || uF.convertMinutesIntoHours(dblTardyIn)>rs.getDouble("early_late")) {

					if (nApproved == -2) {
//						System.out.println(" 333  strDateNew==>" + strDateNew + " strEmpIdNew==>" + hmEmployeeNameMap.get(strEmpId) + " str===>" + strNew.toString() + "   " + strOld.toString());
						String strNewTimeReason = ((nNotify_Time == 0) ? "" : "<br/><br/>New Start Time :" + strNotify_Time);
						
						Map hmRosterStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS");
						Map hmRosterEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE");
						Map hmActualStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS");
						Map hmActualEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE");
						Map hmActualReason = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_IN_REASON");
						Map hmApprove = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_IN_APPROVE");
						
						if(hmRosterStart==null) {
							hmRosterStart = new HashMap();
						}
						if(hmRosterEnd==null) {
							hmRosterEnd = new HashMap();
						}
						if(hmActualStart==null) {
							hmActualStart = new HashMap();
						}
						if(hmActualEnd==null) {
							hmActualEnd = new HashMap();
						}
						if(hmActualReason==null) {
							hmActualReason = new HashMap();
						}
						if(hmApprove==null) {
							hmApprove = new HashMap();
						}
						
						hmRosterStart.put(strEmpId, uF.showData(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()), "N/A"));
						hmActualStart.put(strEmpId, uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
						hmActualReason.put(strEmpId, CF.converIntoHtml(rs.getString("reason") + strNewTimeReason, "No reason provided"));
						
						if ((rs.getInt("approved") == 0 || rs.getInt("approved") == -2) && strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(MANAGER))) {
							hmApprove.put(strEmpId,  ((nNotify_Time == 0) ? "" : "<span style=\"font-size:11px\">Notification for revised timings</span><br/>")
									+ "<a onclick=\"return confirm('Are you sure you want to approve?')\" href=\"ApproveClockEntries.action?AID=" + rs.getString("atten_id") + "&P=U\">Approve</a>");
						} else if (rs.getInt("approved") == 0 || rs.getInt("approved") == -2) {
							hmApprove.put(strEmpId,  "Waiting For Approval");
						} else if (rs.getInt("approved") == -1) {
							hmApprove.put(strEmpId,  "Denied");
						} else if (rs.getInt("approved") == 1) {
							hmApprove.put(strEmpId,  "Approved");
						}
						
								
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS", hmRosterStart);
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS", hmActualStart);
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_IN_REASON", hmActualReason);
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_IN_APPROVE", hmApprove);
						
						if(!alTimeSheet.contains(strEmpId)) {
							alTimeSheet.add(strEmpId);
						}
						
//						if (isDate && (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdRowOld)) || (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld))) {
//							strEmpIdRowOld = strEmpIdNew;
//							if (strNew.length() > 0) {
//								strNew = new String();
//							} else {
//								strNew = "1";
//							}
//						}
						
						isData = true;
//						sb.append("<tr>");
//						sb.append("<td colspan=\"6\">");
//						sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
//
//						sb.append("<td nowrap height=\"25\" width=\"105\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + rs.getString("emp_fname") + " " + rs.getString("emp_lname") + "</td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">ROSTERED START TIME</td>");
//						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.showData(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()), "N/A") + "</td>");
//
//						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">&nbsp;</td>");
//
//						sb.append("</tr></table>");
//						sb.append("</td>");

//						sb.append("<td rowspan=\"2\" style=\"width:50px\" class=\"reportLabel" + strNew + " alignLeft\">" +
//						// "<a href=\"#?w=500\" rel=\"popup_name" + i +
//						// "\" class=\"poplight\" >View Reason</a>" +
//								"<a href=\"javascript:$.modaldialog.success('" + (CF.converIntoHtml(rs.getString("reason") + strNewTimeReason, "No reason provided")) + "', { title: 'Reason given!' });\"><img src=\""+request.getContextPath()+"/images1/Exclamation.png"+"\"></a>" + "</td>");
//						if ((rs.getInt("approved") == 0 || rs.getInt("approved") == -2) && strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(MANAGER))) {
//							sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">" + ((nNotify_Time == 0) ? "" : "<span style=\"font-size:11px\">Notification for revised timings</span><br/>")
//									+ "<a onclick=\"return confirm('Are you sure you want to approve?')\" href=\"ApproveClockEntries.action?AID=" + rs.getString("atten_id") + "&P=U\">Approve</a>"
//							// "+|<a href=\"ApproveClockEntries.action?DID="+
//							// rs.getString("atten_id") + "&P=U\">Deny</a></td>"
//							);
//						} else if (rs.getInt("approved") == 0 || rs.getInt("approved") == -2) {
//							sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">Waiting For Approval</td>");
//						} else if (rs.getInt("approved") == -1) {
//							sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">Denied</td>");
//						} else if (rs.getInt("approved") == 1) {
//							sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">Approved</td>");
//						}
//
//						sb.append("</tr>");
//						sb.append("<tr>");
//
//						sb.append("<td colspan=\"6\">");
//						sb.append("<form style=\"margin:0\" action=\"UpdateClockEntries.action\" >");
//						sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
//
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input type=\"hidden\" value=\"" + rs.getString("_date") + "\" name=\"strCurrentDate\" >"
//								+ uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + rs.getString("emp_fname") + " " + rs.getString("emp_lname") + "<input type=\"hidden\" name=\"strEmpId\" value=\"" + rs.getString("emp_id")
//								+ "\" ></td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">ACTUAL START TIME</td>");
//						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input type=\"text\"  style=\"width:65px\" name=\"strEmpIN\" value=\""
//								+ uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()) + "\" ></td>");
//
//						sb.append("<td>" + "<div id=\"popup_name" + i + "\" class=\"popup_block\">" + "<h2 class=\"textcolorWhite\">Reasons </h2>" + "<table width=\"90%\" cellSpacing=\"1\"  cellPadding=\"1\">" + "<tr><td class=\"reportLabel\">"
//								+ ((rs.getString("reason") == null || (rs.getString("reason") != null && rs.getString("reason").equalsIgnoreCase("")) ? "No reason provided" : rs.getString("reason"))) + "</td></tr>" + "</table>" + "</div>" + "</td>");
//						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input style=\"width:65px\" class=\"input_button\" type=\"submit\" value=\"Update\" ></td>");
//
//						sb.append("</tr></table>");
//						sb.append("</form>");
//						sb.append("</td>");
//						sb.append("</tr>");
					}
//					 }
				} else if ("OUT".equalsIgnoreCase(rs.getString("in_out"))) {

					if (!isIn && !isOut) {

//						System.out.println(" 444  strDateNew==>" + strDateNew + " strEmpIdNew==>" + hmEmployeeNameMap.get(strEmpId) + " str===>" + strNew.toString() + "   " + strOld.toString());
						
//						if (isDate && (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdRowOld)) || (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld))) {
//							strEmpIdRowOld = strEmpIdNew;
//							if (strNew.length() > 0) {
//								strNew = new String();
//							} else {
//								strNew = "1";
//							}
//						}
						
						Map hmRosterStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS");
						Map hmRosterEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE");
						Map hmActualStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS");
						Map hmActualEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE");
//						Map hmActualReason = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdOld+"_REASON");
						
						if(hmRosterStart==null) {
							hmRosterStart = new HashMap();
						}
						if(hmRosterEnd==null) {
							hmRosterEnd = new HashMap();
						}
						if(hmActualStart==null) {
							hmActualStart = new HashMap();
						}
						if(hmActualEnd==null) {
							hmActualEnd = new HashMap();
						}
//						if(hmActualReason==null) {
//							hmActualReason = new HashMap();
//						}
						
						hmRosterStart.put(strEmpId, uF.showData(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()), "N/A"));
						hmActualStart.put(strEmpId, "");
//						hmActualReason.put(strEmpId, new CommonFunctions().converIntoHtml(rs.getString("reason") + strNewTimeReason, "No reason provided"));
								
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS", hmRosterStart);
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS", hmActualStart);
						
						if(!alTimeSheet.contains(strEmpId)) {
							alTimeSheet.add(strEmpId);
						}
						isData = true;
						isIn = true;
//						sb.append("<tr>");
//						sb.append("<td colspan=\"6\">");
//						sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
//
//						sb.append("<td nowrap height=\"25\" width=\"105\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + rs.getString("emp_fname") + " " + rs.getString("emp_lname") + "</td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">ROSTERED START TIME</td>");
//						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.showData(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()), "N/A") + "</td>");
//
//						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">&nbsp;</td>");
//
//						sb.append("</tr></table>");
//						sb.append("</td>");
//
//						sb.append("<td rowspan=\"2\" style=\"width:50px\" class=\"reportLabel" + strNew + " alignLeft\">" +
//						// "<a href=\"#?w=500\" rel=\"popup_name" + i +
//						// "\" class=\"poplight\" >View Reason</a>" +
//								"&nbsp;</td>");
//
//						sb.append("<td rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">&nbsp;</td>"
//						// "+|<a href=\"ApproveClockEntries.action?DID="+
//						// rs.getString("atten_id") + "&P=U\">Deny</a></td>"
//						);
//
//						sb.append("</tr>");
//						sb.append("<tr>");
//
//						sb.append("<td colspan=\"6\">");
//						sb.append("<form style=\"margin:0\" action=\"UpdateClockEntries.action\" >");
//						sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
//
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input type=\"hidden\" value=\"" + rs.getString("_date") + "\" name=\"strCurrentDate\" >"
//								+ uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + rs.getString("emp_fname") + " " + rs.getString("emp_lname") + "<input type=\"hidden\" name=\"strEmpId\" value=\"" + rs.getString("emp_id")
//								+ "\" ></td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">ACTUAL START TIME</td>");
//						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input type=\"text\"  style=\"width:65px\" name=\"strEmpIN\" value=\" \" ></td>");
//
//						sb.append("<td>" + "<div id=\"popup_name" + i + "\" class=\"popup_block\">" + "<h2 class=\"textcolorWhite\">Reasons </h2>" + "<table width=\"90%\" cellSpacing=\"1\"  cellPadding=\"1\">" + "<tr><td class=\"reportLabel\">"
//								+ ((rs.getString("reason") == null || (rs.getString("reason") != null && rs.getString("reason").equalsIgnoreCase("")) ? "No reason provided" : rs.getString("reason"))) + "</td></tr>" + "</table>" + "</div>" + "</td>");
//						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input style=\"width:65px\" class=\"input_button\" type=\"submit\" value=\"Update\" ></td>");
//
//						sb.append("</tr></table>");
//						sb.append("</form>");
//						sb.append("</td>");
//						sb.append("</tr>");
					}

					double dblTardyOut = uF.parseToDouble((String) hmTardyType.get("TARDY_OUT"));
					isOut = true;
					
//					 if(rs.getDouble("early_late")<0 || uF.convertMinutesIntoHours(Math.abs(dblTardyOut))<rs.getDouble("early_late")) {
					if (nApproved == -2) {
						isData = true;

//						System.out.println(" 555  strDateNew==>" + strDateNew + " strEmpIdNew==>" + hmEmployeeNameMap.get(strEmpIdNew) + " strEmpIdOld==>" + hmEmployeeNameMap.get(strEmpIdOld) + " str===>" + strNew.toString() + "   " + strOld.toString()+"  empRowCount="+empRowCount);
						
//						if (isDate && (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdRowOld)) || (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld))) {
//							strEmpIdRowOld = strEmpIdNew;
//							if (strNew.length() > 0) {
//								strNew = new String();
//							} else {
//								strNew = "1";
//							}
//						}
						
						String strNewTimeReason = ((nNotify_Time == 0) ? "" : "<br/><br/>New End Time :" + strNotify_Time);
						
						Map hmRosterStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS");
						Map hmRosterEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE");
						Map hmActualStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS");
						Map hmActualEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE");
						Map hmActualReason = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_OUT_REASON");
						Map hmApprove = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_OUT_APPROVE");
						
						if(hmRosterStart==null) {
							hmRosterStart = new HashMap();
						}
						if(hmRosterEnd==null) {
							hmRosterEnd = new HashMap();
						}
						if(hmActualStart==null) {
							hmActualStart = new HashMap();
						}
						if(hmActualEnd==null) {
							hmActualEnd = new HashMap();
						}
						if(hmActualReason==null) {
							hmActualReason = new HashMap();
						}
						if(hmApprove==null) {
							hmApprove = new HashMap();
						}
						
						hmRosterEnd.put(strEmpId, uF.showData(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()), "N/A"));
						hmActualEnd.put(strEmpId, uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
						hmActualReason.put(strEmpId, CF.converIntoHtml(rs.getString("reason") + strNewTimeReason, "No reason provided"));
						
						if ((rs.getInt("approved") == 0 || rs.getInt("approved") == -2) && strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(MANAGER))) {
							hmApprove.put(strEmpId,  ((nNotify_Time == 0) ? "" : "<span style=\"font-size:11px\">Notification for revised timings</span><br/>")
									+ "<a onclick=\"return confirm('Are you sure you want to approve?')\" href=\"ApproveClockEntries.action?AID=" + rs.getString("atten_id") + "&P=U\">Approve</a>");
						} else if (rs.getInt("approved") == 0 || rs.getInt("approved") == -2) {
							hmApprove.put(strEmpId,  "Waiting For Approval");
						} else if (rs.getInt("approved") == -1) {
							hmApprove.put(strEmpId,  "Denied");
						} else if (rs.getInt("approved") == 1) {
							hmApprove.put(strEmpId,  "Approved");
						}
								
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE", hmRosterEnd);
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE", hmActualEnd);
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_OUT_REASON", hmActualReason);
						hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_OUT_APPROVE", hmApprove);
						
						if(!alTimeSheet.contains(strEmpId)) {
							alTimeSheet.add(strEmpId);
						}
						
//						sb.append("<tr>");
//
//						sb.append("<td colspan=\"6\">");
//						sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
//
//						sb.append("<td height=\"25\" width=\"105\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + rs.getString("emp_fname") + " " + rs.getString("emp_lname") + "</td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">ROSTERED END TIME</td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.showData(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()), "N/A") + "</td>");
//						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">&nbsp;</td>");
//
//						sb.append("</tr></table>");
//						sb.append("</td>");

//						sb.append("<td rowspan=\"2\" style=\"width:50px\" class=\"reportLabel" + strNew + " alignLeft\">" +
						// "<a href=\"#?w=500\" rel=\"popup_name" + i +
						// "\" class=\"poplight\" >View Reason</a>" +
//								"<a href=\"javascript:$.modaldialog.success('" + (CF.converIntoHtml(rs.getString("reason") + strNewTimeReason, "No reason provided")) + "', { title: 'Reason given!' });\"><img src=\""+request.getContextPath()+"/images1/Exclamation.png"+"\"></a>" + "</td>");
//						if ((rs.getInt("approved") == 0 || rs.getInt("approved") == -2) && strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(MANAGER))) {
//							sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">" + ((nNotify_Time == 0) ? "" : "<span style=\"font-size:11px\">Notification for revised timings</span><br/>")
//									+ "<a onclick=\"return confirm('Are you sure you want to approve?')\"  href=\"ApproveClockEntries.action?AID=" + rs.getString("atten_id") + "&P=U\">Approve</a>"
//							// +"|<a href=\"ApproveClockEntries.action?DID="+
//							// rs.getString("atten_id") + "&P=U\">Deny</a></td>"
//							);
//						} else if (rs.getInt("approved") == 0 || rs.getInt("approved") == -2) {
//							sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">Waiting For Approval</td>");
//						} else if (rs.getInt("approved") == -1) {
//							sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">Denied</td>");
//						} else if (rs.getInt("approved") == 1) {
//							sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">Approved</td>");
//						}
//						sb.append("</tr>");
//
//						sb.append("<tr>");
//
//						sb.append("<td colspan=\"6\">");
//						sb.append("<form style=\"margin:0\" action=\"UpdateClockEntries.action\" >");
//						sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
//
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input type=\"hidden\" value=\"" + rs.getString("_date") + "\" name=\"strCurrentDate\" >"
//								+ uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + rs.getString("emp_fname") + " " + rs.getString("emp_lname") + "<input type=\"hidden\" name=\"strEmpId\" value=\"" + rs.getString("emp_id")
//								+ "\" ></td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
//						sb.append("<td nowrap style=\"width:150px\" class=\"reportLabel" + strNew + " alignLeft\">ACTUAL END TIME</td>");
//						sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input style=\"width:65px\" type=\"text\" name=\"strEmpOUT\" value=\""
//								+ uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()) + "\" ></td>");
//
//						sb.append("<td>" + "<div id=\"popup_name" + i + "\" class=\"popup_block\">" + "<h2 class=\"textcolorWhite\">Reasons </h2>" + "<table width=\"90%\" cellSpacing=\"0\"  cellPadding=\"1\">" + "<tr><td class=\"reportLabel\">"
//								+ ((rs.getString("reason") == null || (rs.getString("reason") != null && rs.getString("reason").equalsIgnoreCase("")) ? "No reason provided" : rs.getString("reason"))) + "</td></tr>" + "</table>" + "</div>" + "</td>");
//						sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input class=\"input_button\" style=\"width:65px\" type=\"submit\" value=\"Update\" ></td>");
//
//						sb.append("</tr></table>");
//						sb.append("</form>");
//						sb.append("</td>");
//						sb.append("</tr>");

					}
//					 }

				} else {
					if(uF.isThisDateValid(rs.getString("_date"), DBDATE) && rs.getString("_from")!=null && !rs.getString("_from").trim().equals("") && !rs.getString("_from").trim().equalsIgnoreCase("NULL") && rs.getString("_to")!=null && !rs.getString("_to").trim().equals("") && !rs.getString("_to").trim().equalsIgnoreCase("NULL")) {
						long frmTime = uF.getTimeStamp(rs.getString("_date") + rs.getString("_from"), DBDATE + DBTIME).getTime();
						long toTime = uF.getTimeStamp(rs.getString("_date") + rs.getString("_to"), DBDATE + DBTIME).getTime();
						long currentTime = uF.getTimeStamp("" + uF.getCurrentDate(CF.getStrTimeZone()) + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME).getTime();
	
						if (frmTime < currentTime) {
	
							
	//						if (isDate && (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdRowOld)) || (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld))) {
	//							strEmpIdRowOld = strEmpIdNew;
	//							if (strNew.length() > 0) {
	//								strNew = new String();
	//							} else {
	//								strNew = "1";
	//							}
	//						}
							
							Map hmRosterStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS");
							Map hmRosterEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE");
							Map hmActualStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS");
							Map hmActualEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE");
							
							if(hmRosterStart==null) {
								hmRosterStart = new HashMap();
							}
							if(hmRosterEnd==null) {
								hmRosterEnd = new HashMap();
							}
							if(hmActualStart==null) {
								hmActualStart = new HashMap();
							}
							if(hmActualEnd==null) {
								hmActualEnd = new HashMap();
							}
							
							hmRosterStart.put(strEmpId, uF.showData(uF.getDateFormat(strRosterFromNew, DBTIME, CF.getStrReportTimeFormat()), "N/A"));
							hmActualStart.put(strEmpId, "");
	//						hmActualReason.put(strEmpIdOld, rs.getString("reason"));
									
							hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS", hmRosterStart);
							hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS", hmActualStart);
							
							if(!alTimeSheet.contains(strEmpId)) {
								alTimeSheet.add(strEmpId);
							}
							
	//						System.out.println(" 666  strDateNew==>" + strDateNew + " strEmpIdNew==>" + hmEmployeeNameMap.get(strEmpId) + " str===>" + strNew.toString() + "   " + strOld.toString());
							isData = true;
//							sb.append("<tr>");
//							sb.append("<td colspan=\"6\">");
//							sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
//	
//							sb.append("<td nowrap height=\"25\" width=\"105\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + hmEmployeeNameMap.get(strEmpId) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">ROSTERED START TIME</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.showData(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()), "N/A") + "</td>");
//	
//							sb.append("<td width=\"124\" class=\"reportLabel" + strNew + " alignLeft\">&nbsp;</td>");
//	
//							sb.append("</tr></table>");
//							sb.append("</td>");
//	
//							sb.append("<td rowspan=\"2\" style=\"width:50px\" class=\"reportLabel" + strNew + " alignLeft\">" +
//							// "<a href=\"#?w=500\" rel=\"popup_name" + i +
//							// "\" class=\"poplight\" >View Reason</a>" +
//									"&nbsp;</td>");
//	
//							sb.append("<td width=\"100\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">&nbsp;</td>"
//							// "+|<a href=\"ApproveClockEntries.action?DID="+
//							// rs.getString("atten_id") + "&P=U\">Deny</a></td>"
//							);
//	
//							sb.append("</tr>");
//							sb.append("<tr>");
//	
//							sb.append("<td colspan=\"6\">");
//							sb.append("<form style=\"margin:0\" action=\"UpdateClockEntries.action\" >");
//							sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
//	
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input type=\"hidden\" value=\"" + rs.getString("_date") + "\" name=\"strCurrentDate\" >"
//									+ uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + hmEmployeeNameMap.get(strEmpId) + "<input type=\"hidden\" name=\"strEmpId\" value=\"" + rs.getString("emp_id") + "\" ></td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">ACTUAL START TIME</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input type=\"text\"  style=\"width:65px\" name=\"strEmpIN\" value=\" \" ></td>");
//	
//							sb.append("<td>" + "<div id=\"popup_name" + i + "\" class=\"popup_block\">" + "<h2 class=\"textcolorWhite\">Reasons </h2>" + "<table width=\"90%\" cellSpacing=\"0\"  cellPadding=\"1\">" + "<tr><td class=\"reportLabel\">"
//									+ ((rs.getString("reason") == null || (rs.getString("reason") != null && rs.getString("reason").equalsIgnoreCase("")) ? "No reason provided" : rs.getString("reason"))) + "</td></tr>" + "</table>" + "</div>" + "</td>");
//							sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input style=\"width:65px\" class=\"input_button\" type=\"submit\" value=\"Update\" ></td>");
//	
//							sb.append("</tr></table>");
//							sb.append("</form>");
//							sb.append("</td>");
//							sb.append("</tr>");
						}
	
						if (toTime < currentTime) {
	
	//						System.out.println(" 777  strDateNew==>" + strDateNew + " strEmpIdNew==>" + hmEmployeeNameMap.get(strEmpId) + " str===>" + strNew.toString() + "   " + strOld.toString());
							
							Map hmRosterStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS");
							Map hmRosterEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE");
							Map hmActualStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS");
							Map hmActualEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE");
							
							if(hmRosterStart==null) {
								hmRosterStart = new HashMap();
							}
							if(hmRosterEnd==null) {
								hmRosterEnd = new HashMap();
							}
							if(hmActualStart==null) {
								hmActualStart = new HashMap();
							}
							if(hmActualEnd==null) {
								hmActualEnd = new HashMap();
							}
							
							hmRosterEnd.put(strEmpId, uF.showData(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()), "N/A"));
							hmActualEnd.put(strEmpId, "");
	//						hmActualReason.put(strEmpIdOld, rs.getString("reason"));
							
							hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE", hmRosterEnd);
							hmTimeSheet.put(uF.getDateFormat(strDateNew, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE", hmActualEnd);
							
							if(!alTimeSheet.contains(strEmpId)) {
								alTimeSheet.add(strEmpId);
							}
							
							isData = true;
	
//							sb.append("<tr>");
//	
//							sb.append("<td colspan=\"6\">");
//							sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
//	
//							sb.append("<td nowrap height=\"25\" width=\"105\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + hmEmployeeNameMap.get(strEmpId) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">ROSTERED END TIME</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.showData(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()), "N/A") + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">&nbsp;</td>");
//	
//							sb.append("</tr></table>");
//							sb.append("</td>");
//	
//							sb.append("<td rowspan=\"2\" style=\"width:50px\" class=\"reportLabel" + strNew + " alignLeft\">" + "&nbsp;</td>");
//	
//							sb.append("<td style=\"width:100px\" rowspan=\"2\" class=\"reportLabel" + strNew + " alignLeft\">&nbsp;</td>"
//							// "+|<a href=\"ApproveClockEntries.action?DID="+
//							// rs.getString("atten_id") + "&P=U\">Deny</a></td>"
//							);
//	
//							sb.append("</tr>");
//	
//							sb.append("<tr>");
//	
//							sb.append("<td colspan=\"6\">");
//							sb.append("<form style=\"margin:0\" action=\"UpdateClockEntries.action\" >");
//							sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
//	
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input type=\"hidden\" value=\"" + rs.getString("_date") + "\" name=\"strCurrentDate\" >"
//									+ uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + hmEmployeeNameMap.get(strEmpId) + "<input type=\"hidden\" name=\"strEmpId\" value=\"" + rs.getString("emp_id") + "\" ></td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
//							sb.append("<td nowrap style=\"width:150px\" class=\"reportLabel" + strNew + " alignLeft\">ACTUAL END TIME</td>");
//							sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input style=\"width:65px\" type=\"text\" name=\"strEmpOUT\" value=\" \" ></td>");
//	
//							sb.append("<td>" + "<div id=\"popup_name" + i + "\" class=\"popup_block\">" + "<h2 class=\"textcolorWhite\">Reasons </h2>" + "<table width=\"90%\" cellSpacing=\"0\"  cellPadding=\"0\">" + "<tr><td class=\"reportLabel\">"
//									+ ((rs.getString("reason") == null || (rs.getString("reason") != null && rs.getString("reason").equalsIgnoreCase("")) ? "No reason provided" : rs.getString("reason"))) + "</td></tr>" + "</table>" + "</div>" + "</td>");
//							sb.append("<td style=\"width:100px\" class=\"reportLabel" + strNew + " alignLeft\"><input class=\"input_button\" style=\"width:65px\" type=\"submit\" value=\"Update\" ></td>");
//	
//							sb.append("</tr></table>");
//							sb.append("</form>");
//							sb.append("</td>");
//							sb.append("</tr>");
						}
					}
				}

				strDateOld = strDateNew;
				strEmpIdOld = strEmpIdNew;
				strRosterFromOld = strRosterFromNew;
				strRosterToOld = strRosterToNew;
				nServiceIdOld = nServiceIdNew;
				strOld = strNew;
				
			}
			rs.close();
			pst.close();


			if (isIn && !isOut) {

				long frmTime = uF.getTimeStamp(strDateNew + strRosterFromNew, DBDATE + DBTIME).getTime();
				long toTime = uF.getTimeStamp(strDateNew + strRosterToNew, DBDATE + DBTIME).getTime();
				long currentTime = uF.getTimeStamp("" + uF.getCurrentDate(CF.getStrTimeZone()) + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME).getTime();

				if (toTime < currentTime) {

//					System.out.println(" 888  strDateNew==>" + strDateNew + " strEmpIdNew==>" + hmEmployeeNameMap.get(strEmpIdOld) + " str===>" + strNew.toString() + "   " + strOld.toString());
					Map hmRosterStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS");
					Map hmRosterEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE");
					Map hmActualStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS");
					Map hmActualEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE");
					
					if(hmRosterStart==null) {
						hmRosterStart = new HashMap();
					}
					if(hmRosterEnd==null) {
						hmRosterEnd = new HashMap();
					}
					if(hmActualStart==null) {
						hmActualStart = new HashMap();
					}
					if(hmActualEnd==null) {
						hmActualEnd = new HashMap();
					}
					
					hmRosterEnd.put(strEmpIdOld, uF.showData(uF.getDateFormat(strRosterToOld, DBTIME, CF.getStrReportTimeFormat()), "N/A"));
					hmActualEnd.put(strEmpIdOld, "");
//					hmActualReason.put(strEmpIdOld, rs.getString("reason"));
							
					hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE", hmRosterEnd);
					hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE", hmActualEnd);
					
					if(!alTimeSheet.contains(strEmpIdOld)) {
						alTimeSheet.add(strEmpIdOld);
//						System.out.println(" strEmpIdOld 7 ===>> " + strEmpIdOld);
					}
					
					isData = true;

//					sb.append("<tr>");
//
//					sb.append("<td colspan=\"6\">");
//					sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
//
//					sb.append("<td nowrap height=\"25\" width=\"105\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDayFormat()) + "</td>");
//					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat()) + "</td>");
//					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + hmEmployeeNameMap.get(strEmpIdOld) + "</td>");
//					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
//					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">ROSTERED END TIME</td>");
//					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.showData(uF.getDateFormat(strRosterToOld, DBTIME, CF.getStrReportTimeFormat()), "N/A") + "</td>");
//					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">&nbsp;</td>");
//
//					sb.append("</tr></table>");
//					sb.append("</td>");
//
//					sb.append("<td rowspan=\"2\" style=\"width:50px\" class=\"reportLabel" + strOld + " alignLeft\">" + "&nbsp;</td>");
//
//					sb.append("<td style=\"width:100px\" rowspan=\"2\" class=\"reportLabel" + strOld + " alignLeft\">" + "&nbsp;</td>");
//
//					sb.append("</tr>");
//
//					sb.append("<tr>");
//
//					sb.append("<td colspan=\"6\">");
//					sb.append("<form style=\"margin:0\" action=\"UpdateClockEntries.action\" >");
//					sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
//
//					sb.append("<td nowrap width=\"100\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDayFormat()) + "</td>");
//					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input type=\"hidden\" value=\"" + strDateOld + "\" name=\"strCurrentDate\" >" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())
//							+ "</td>");
//					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + hmEmployeeNameMap.get(strEmpIdOld) + "<input type=\"hidden\" name=\"strEmpId\" value=\"" + strEmpIdOld + "\" ></td>");
//					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
//					sb.append("<td nowrap style=\"width:150px\" class=\"reportLabel" + strOld + " alignLeft\">ACTUAL END TIME</td>");
//					sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input style=\"width:65px\" type=\"text\" name=\"strEmpOUT\" value=\"\" ></td>");
//
//					sb.append("<td>&nbsp;</td>");
//					sb.append("<td width=\"77\" class=\"reportLabel" + strOld + " alignLeft\"><input class=\"input_button\" style=\"width:65px\" type=\"submit\" value=\"Update\" ></td>");
//
//					sb.append("</tr></table>");
//					sb.append("</form>");
//					sb.append("</td>");
//					sb.append("</tr>");

				}

			} else if (!isIn && isOut) {

				isData = true;

//				System.out.println(" 999  strDateNew==>" + strDateNew + " strEmpIdNew==>" + hmEmployeeNameMap.get(strEmpIdOld) + " str===>" + strNew.toString() + "   " + strOld.toString());
				
				Map hmRosterStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS");
				Map hmRosterEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RE");
				Map hmActualStart = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS");
				Map hmActualEnd = (Map)hmTimeSheet.get(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AE");
				
				if(hmRosterStart==null) {
					hmRosterStart = new HashMap();
				}
				if(hmRosterEnd==null) {
					hmRosterEnd = new HashMap();
				}
				if(hmActualStart==null) {
					hmActualStart = new HashMap();
				}
				if(hmActualEnd==null) {
					hmActualEnd = new HashMap();
				}
				
				hmRosterStart.put(strEmpIdOld, uF.showData(uF.getDateFormat(strRosterFromOld, DBTIME, CF.getStrReportTimeFormat()), "N/A"));
				hmActualStart.put(strEmpIdOld, "");
//				hmActualReason.put(strEmpIdOld, rs.getString("reason"));
				
				hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_RS", hmRosterStart);
				hmTimeSheet.put(uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())+""+nServiceIdNew+"_AS", hmActualStart);
				
				if(!alTimeSheet.contains(strEmpIdOld)) {
					alTimeSheet.add(strEmpIdOld);
//					System.out.println(" strEmpIdOld 8 ===>> " + strEmpIdOld);
				}
				
//				sb.append("<tr>");
//				sb.append("<td colspan=\"6\">");
//				sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
//
//				sb.append("<td nowrap height=\"25\" width=\"105\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDayFormat()) + "</td>");
//				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat()) + "</td>");
//				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + hmEmployeeNameMap.get(strEmpIdOld) + "</td>");
//				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
//				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">ROSTERED START TIME</td>");
//				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.showData(uF.getDateFormat(strRosterFromOld, DBTIME, CF.getStrReportTimeFormat()), "N/A") + "</td>");
//
//				sb.append("<td width=\"116\" class=\"reportLabel" + strOld + " alignLeft\">&nbsp;</td>");
//
//				sb.append("</tr></table>");
//				sb.append("</td>");
//
//				sb.append("<td rowspan=\"2\" style=\"width:50px\" class=\"reportLabel" + strOld + " alignLeft\">&nbsp;</td>");
//
//				sb.append("<td style=\"width:100px\" rowspan=\"2\" class=\"reportLabel" + strOld + " alignLeft\">&nbsp;</td>");
//
//				sb.append("</tr>");
//				sb.append("<tr>");
//
//				sb.append("<td colspan=\"6\">");
//				sb.append("<form style=\"margin:0\" action=\"UpdateClockEntries.action\" >");
//				sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
//
//				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDayFormat()) + "</td>");
//				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input type=\"hidden\" value=\"" + strDateOld + "\" name=\"strCurrentDate\" >" + uF.getDateFormat(strDateOld, DBDATE, CF.getStrReportDateFormat())
//						+ "</td>");
//				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + hmEmployeeNameMap.get(strEmpIdOld) + "<input type=\"hidden\" name=\"strEmpId\" value=\"" + strEmpIdOld + "\" ></td>");
//				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">" + (String) hmServicesMap.get(nServiceIdNew + "") + "<input type=\"hidden\" name=\"strServiceId\" value=\"" + nServiceIdNew + "\" ></td>");
//				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\">ACTUAL START TIME</td>");
//				sb.append("<td nowrap style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input type=\"text\"  style=\"width:65px\" name=\"strEmpIN\" value=\" \" ></td>");
//
//				sb.append("<td>&nbsp;</td>");
//				sb.append("<td style=\"width:100px\" class=\"reportLabel" + strOld + " alignLeft\"><input style=\"width:65px\" class=\"input_button\" type=\"submit\" value=\"Update\" ></td>");
//
//				sb.append("</tr></table>");
//				sb.append("</form>");
//				sb.append("</td>");
//				sb.append("</tr>");

			}

//			if (i == 1 || !isData) {
//
//				sb.append("<tr><td class=\"label alignCenter\">No exceptions found for this employee.</td></tr>");
//			}
//
//			sb.append("</table>");
			
			Map<String, String> hmEmpData = new HashMap<String, String>();
			pst = con.prepareStatement(selectEmployee1Details);
			pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
//			System.out.println("4 pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmEmpData.put("NAME", rs.getString("emp_fname") + " " + rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpData1", hmEmpData);
			
			pst = con.prepareStatement("select * from exception_reason where _date between ? and ? order by emp_id");
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("5 pst======>"+pst);
			rs = pst.executeQuery();
			Map hmExceptionReason = new HashMap();
			Map hmInner = null;
			
			strEmpIdNew = null;
			strEmpIdOld = null;
			
			while(rs.next()) {
				strEmpIdNew = rs.getString("emp_id");
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					hmInner = new HashMap();
				}
				
				hmInner.put(rs.getString("_date")+"_"+rs.getString("service_id")+"_"+rs.getString("in_out_type"), rs.getString("given_reason"));
				hmInner.put(rs.getString("_date")+"_"+rs.getString("service_id")+"_"+rs.getString("in_out_type")+"_STATUS", rs.getString("status"));
//				hmInner.put(rs.getString("_date")+"_"+rs.getString("service_id"), rs.getString("given_reason"));
//				hmInner.put(rs.getString("_date")+"_"+rs.getString("service_id")+"_STATUS", rs.getString("status"));
				hmExceptionReason.put(rs.getString("emp_id"), hmInner);
				
				strEmpIdOld = strEmpIdNew ;
			}
			rs.close();
			pst.close();
			request.setAttribute("hmExceptionReason1", hmExceptionReason);
			
			Map hmGenderMap = CF.getEmpGenderMap(con);
			
			request.setAttribute("hmGenderMap1", hmGenderMap);
			
			
			leaveTypeList = new FillLeaveType(request).fillLeave(uF.parseToInt((String)hmEmpLevelMap.get(getStrSelectedEmpId())), uF.parseToInt(getStrSelectedEmpId()));
			
			pst = con.prepareStatement("select * from roster_details where _date between ? and ? and emp_id =?");
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
//			System.out.println("6 pst======>"+pst);
			rs = pst.executeQuery();
			Map hmRoster = new HashMap();
			while(rs.next()) {
				hmRoster.put(rs.getString("_date")+"_"+rs.getString("service_id")+"_IN", rs.getString("_from"));
				hmRoster.put(rs.getString("_date")+"_"+rs.getString("service_id")+"_OUT", rs.getString("_to"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmRoster1", hmRoster);
			
			StringBuilder sbEmp = null;
			for(int k = 0; alTimeSheet!=null && k < alTimeSheet.size(); k++ ) {
				if(sbEmp == null) {
					sbEmp = new StringBuilder();
					sbEmp.append(""+alTimeSheet.get(k));
				} else {
					sbEmp.append(","+alTimeSheet.get(k));
				}
			}
			
			if(sbEmp !=null) {
				Map<String, List<String>> hmEmpSbu = new HashMap<String, List<String>>();
				pst = con.prepareStatement("SELECT * FROM employee_official_details eod, employee_personal_details epd WHERE epd.emp_per_id=eod.emp_id " +
						"and eod.emp_id in ("+sbEmp.toString()+") order by emp_id");
				rs = pst.executeQuery();
				while (rs.next()) {
					String strSbu = rs.getString("service_id");
					
					List<String> al = new ArrayList<String>();
					if(strSbu !=null && !strSbu.trim().equals("")) {
						List<String> alTemp = Arrays.asList(strSbu.split(","));
						for(String str : alTemp) {
							if(uF.parseToInt(str) > 0) {
								al.add(str);
							}
						}
					}
					hmEmpSbu.put(rs.getString("emp_id"), al);
				}
				rs.close();
				pst.close();
				
				request.setAttribute("hmEmpSbu1", hmEmpSbu);
			}
			
		} catch (Exception e) {
			e.printStackTrace(); 
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
//		request.setAttribute("TIMESHEET1", sb.toString());
		request.setAttribute("TIMESHEET_1", hmTimeSheet);
		request.setAttribute("TIMESHEET_EMP1", alTimeSheet);
		request.setAttribute("TIMESHEET_EMPNAME1", hmEmployeeNameMap);
		request.setAttribute("TIMESHEET_SERVICENAME1", hmServicesMap);
		request.setAttribute("TIMESHEET_DATE1", alDt);
		request.setAttribute("TIMESHEET_SERVICE1", alService);
		
		return SUCCESS;
	}
	

	
	List<FillLeaveType> leaveTypeList;
	
	public String viewClockEntries(String date, String strDate, String strReqEmpID, String strReqServiceId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		StringBuilder sb = new StringBuilder();
		try {

			con = db.makeConnection(con);
			if (strDate != null) {
				date = strDate;
			} else if (date == null || (date != null && date.equalsIgnoreCase(""))) {
				date = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, CF.getStrReportDateFormat());
			}
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEndDates = CF.getWeekEndDateList(con, date, date, CF, uF,hmWeekEndHalfDates,null);
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF,date, date,alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEndDates,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			
			Map<String,String> hmHolidays = new HashMap<String,String>();
			Map<String,String> hmHolidayDates = new HashMap<String,String>();
			CF.getHolidayList(con,request,date, date, CF, hmHolidayDates, hmHolidays, true);
			
			request.setAttribute("hmWeekEndHalfDates", hmWeekEndHalfDates);
			request.setAttribute("hmWeekEndDates", hmWeekEndDates);
			request.setAttribute("hmEmpWlocation", hmEmpWlocation);
			request.setAttribute("alEmpCheckRosterWeektype", alEmpCheckRosterWeektype);
			request.setAttribute("hmRosterWeekEndDates", hmRosterWeekEndDates);
			request.setAttribute("hmHolidays", hmHolidays);
			request.setAttribute("hmHolidayDates", hmHolidayDates);
			
//			System.out.println("or hmWeekEndHalfDates1===>"+ hmWeekEndHalfDates);
//			System.out.println("or hmWeekEndDates1===>"+ hmWeekEndDates);
//			System.out.println("or hmEmpWlocation1===>"+ hmEmpWlocation);
//			System.out.println("or alEmpCheckRosterWeektype1===>"+ alEmpCheckRosterWeektype);
//			System.out.println("or hmRosterWeekEndDates1===>"+ hmRosterWeekEndDates);
//			System.out.println("or hmHolidays1===>"+ hmHolidays);
//			System.out.println("or hmHolidayDates1===>"+ hmHolidayDates);

			if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN) && strReqEmpID != null) {
				if (strPay == null) {
					pst = con.prepareStatement(selectClockEntriesR1_E);
				} else {
					pst = con.prepareStatement(selectClockEntriesR1_E1);
				}
				pst.setDate(1, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setDate(2, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setInt(3, uF.parseToInt(strReqEmpID));
				pst.setDate(4, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setDate(5, uF.getDateFormat(date, CF.getStrReportDateFormat()));

			} else if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN)) {
				pst = con.prepareStatement(selectClockEntriesR1_A);
				pst.setDate(1, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setDate(2, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setDate(3, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setDate(4, uF.getDateFormat(date, CF.getStrReportDateFormat()));

			} else if (strUserType != null && strUserType.equalsIgnoreCase(MANAGER)) {
				pst = con.prepareStatement(selectClockEntriesR1_M);
				pst.setDate(1, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setDate(2, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setDate(3, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setDate(4, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setInt(5, uF.parseToInt(strSessionEmpId));

			} else if (strUserType != null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))) {
				pst = con.prepareStatement(selectClockEntriesR1_E);
				pst.setDate(1, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setDate(2, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.setDate(4, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setDate(5, uF.getDateFormat(date, CF.getStrReportDateFormat()));
//				System.out.println("UCE/3766--pst="+pst);

			} else if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				pst = con.prepareStatement(selectClockEntriesR1_HRM);
				pst.setDate(1, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setDate(2, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setDate(3, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setDate(4, uF.getDateFormat(date, CF.getStrReportDateFormat()));
				pst.setInt(5, uF.parseToInt(strSessionEmpId));

			} 
//			System.out.println("7 pst ======>> " + pst);
			rs = pst.executeQuery();

			String strOldEmpId = "";
			String strNewEmpId = "";

			sb.append("<form action=\"UpdateClockEntries.action\" method=\"POST\" >");
			sb.append("<input type=\"hidden\" name=\"strDate\" value=\"" + uF.getDateFormat(date, CF.getStrReportDateFormat(), CF.getStrReportDateFormat()) + "\" >");
			sb.append("<input type=\"hidden\" name=\"redirectUrl\" value=\"" + getRedirectUrl() + "\" >");

			Map<String, String> hmInner = new HashMap<String, String>();
			Map<String, Map<String, String>> hm = new HashMap<String, Map<String, String>>();

			while (rs.next()) {

				strNewEmpId = rs.getString("empl_id");
				if (strReqServiceId != null && !strReqServiceId.equalsIgnoreCase(rs.getString("service_id"))) {
					continue;
				}

				if (strOldEmpId != null && !strOldEmpId.equalsIgnoreCase(strNewEmpId)) {
					hmInner = new HashMap<String, String>();
				}

				hmInner.put("RS", uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
				hmInner.put("RE", uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));
				hmInner.put("EMPNAME", rs.getString("emp_fname"));
				hmInner.put("SERVICEID", rs.getString("service_id"));

				if ("OUT".equalsIgnoreCase(rs.getString("in_out"))) {
					if (rs.getString("in_out_timestamp_actual") != null) {
						hmInner.put("AE", uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					} else {
						// hmInner.put("AE", uF.getDateFormat("00:00:00",
						// DBTIME, ReportTimeFormat));
						hmInner.put("AE", "No Entry");
					}

					hmInner.put("MANAGER_OC", ((rs.getString("comments") != null) ? rs.getString("comments") : ""));
					hmInner.put("EMP_OR", (rs.getString("reason") != null) ? rs.getString("reason") : "");

					if (rs.getInt("approved") == -1) {
						hmInner.put("STATUS_OUT", "Denied");
					} else if (rs.getInt("approved") == 1) {
						hmInner.put("STATUS_OUT", "Approved");
					} else if (rs.getInt("approved") == -2) {
						hmInner.put("STATUS_OUT", "Waiting for Approval");
					} else {
						hmInner.put("STATUS_OUT", "System Approved");
					}

					if (rs.getInt("early_late") > 0) {
						hmInner.put("EMP_OEL", "Left office late by " + rs.getString("early_late") + "mins");
					} else if (rs.getInt("early_late") < 0) {
						hmInner.put("EMP_OEL", "Left office early by " + rs.getString("early_late") + "mins");
					} else if (rs.getInt("early_late") == 0) {
						hmInner.put("EMP_OEL", "Left office on time.");
					} else {
						hmInner.put("EMP_OEL", "");
					}

				} else if ("IN".equalsIgnoreCase(rs.getString("in_out"))) {

					if (rs.getString("in_out_timestamp_actual") != null) {
						hmInner.put("AS", uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					} else {
						// hmInner.put("AS", uF.getDateFormat("00:00:00",
						// DBTIME, ReportTimeFormat));
						hmInner.put("AS", "No Entry");
					}

					hmInner.put("MANAGER_IC", ((rs.getString("comments") != null) ? rs.getString("comments") : ""));
					hmInner.put("EMP_IR", (rs.getString("reason") != null) ? rs.getString("reason") : "");
					if (rs.getInt("approved") == -1) {
						hmInner.put("STATUS_IN", "Denied");
					} else if (rs.getInt("approved") == 1) {
						hmInner.put("STATUS_IN", "Approved");
					} else if (rs.getInt("approved") == -2) {
						hmInner.put("STATUS_IN", "Waiting for Approval");
					} else {
						hmInner.put("STATUS_IN", "System Approved");
					}

					if (rs.getInt("early_late") > 0) {
						hmInner.put("EMP_IEL", "Came office late by " + rs.getString("early_late") + "hrs");
					} else if (rs.getInt("early_late") < 0) {
						hmInner.put("EMP_IEL", "Came office early by " + rs.getString("early_late") + "hrs");
					} else if (rs.getInt("early_late") == 0) {
						hmInner.put("EMP_IEL", "Came office on time.");
					} else {
						hmInner.put("EMP_IEL", "");
					}

				}

				hm.put(strNewEmpId, hmInner);

				strOldEmpId = strNewEmpId;

			}
			rs.close();
			pst.close();
//			System.out.println("hm ===>> " + hm);
			

			sb.append("<table width=\"50%\" cellSpacing=\"1\"  cellPadding=\"1\" >");
			sb.append("<tr>");

			if (strUserType != null && !strUserType.equalsIgnoreCase(EMPLOYEE) && !strUserType.equalsIgnoreCase(ARTICLE) && !strUserType.equalsIgnoreCase(CONSULTANT)) {
				sb.append("<td class=\"reportHeading\">Emp Name</td>");
			}

			sb.append("<td class=\"reportHeading\">Roster/Actual</td>");

			sb.append("<td class=\"reportHeading\">Start</td><td class=\"reportHeading\">End</td><td class=\"reportHeading\">Total Time</td><td class=\"reportHeading\">&nbsp;</td></tr>");

			Set set = hm.keySet();
			Iterator it = set.iterator();
			Map hmTemp = new HashMap();
			int i = 0;
			while (it.hasNext()) {
				String strEmpId = (String) it.next();

				hmTemp = (HashMap) hm.get(strEmpId);

				if (strEmpId != null) {
					sb.append("<tr>");

					if (strUserType != null && !strUserType.equalsIgnoreCase(EMPLOYEE) && !strUserType.equalsIgnoreCase(ARTICLE) && !strUserType.equalsIgnoreCase(CONSULTANT)) {
						sb.append("<td class=\"reportHeading\">" + (String) hmTemp.get("EMPNAME") + "<input type=\"hidden\" name=\"strEmpId\" value=\"" + strEmpId + "\" ></td>");
					}

					sb.append("<td class=\"reportHeading\">Roster<br/>&nbsp;<br/>Actual</td>");

					sb.append("<td class=\"timeLabel\">" + "<p style=\"margin-left:5px\">" + uF.showTimeFormat((String) hmTemp.get("RS")) + "</p>" + "<input type=\"text\" style=\"width:65px\" size=\"8\" name=\"strEmpIN\" value=\""
							+ uF.showTimeFormat((String) hmTemp.get("AS")) + "\" > <input type=\"hidden\" name=\"strServiceId\" value=\"" + (String) hmTemp.get("SERVICEID") + "\" /> </td>");
					sb.append("<td class=\"timeLabel\">" + "<p style=\"margin-left:5px\">" + uF.showTimeFormat((String) hmTemp.get("RE")) + "</p>" + "<input type=\"text\" style=\"width:65px\" size=\"8\"  name=\"strEmpOUT\" value=\""
							+ uF.showTimeFormat((String) hmTemp.get("AE")) + "\" ></td>");

					if (hmTemp.get("AS") != null && !(hmTemp.get("AS").toString()).equalsIgnoreCase(NO_TIME_RECORD) && hmTemp.get("AE") != null && !(hmTemp.get("AE").toString()).equalsIgnoreCase(NO_TIME_RECORD)) {
						sb.append("<td class=\"timeLabel\">"
								+ uF.getTimeDiffInHoursMins(uF.getTimeFormat(uF.showTimeFormat((String) hmTemp.get("AS")), CF.getStrReportTimeFormat()).getTime(), uF.getTimeFormat(uF.showTimeFormat((String) hmTemp.get("AE")), CF.getStrReportTimeFormat())
										.getTime()) + "</td>");
					} else {
						sb.append("<td class=\"timeLabel\"> - </td>");
					}

					sb.append("<td class=\"reportLabel\"><a href=\"#?w=500\" rel=\"popup_name" + i + "\" class=\"poplight\" >View Details</a></td>");

					sb.append("<td>" + "<div id=\"popup_name" + i++ + "\" class=\"popup_block\">" + "<h2 class=\"textcolorWhite\">Please enter reason for denial.</h2>" + "<table width=\"90%\" cellSpacing=\"1\"  cellPadding=\"1\">"
							+ "<tr><td class=\"reportHeading\">&nbsp;</td><td class=\"reportHeading\">IN</td><td class=\"reportHeading\">OUT</td></tr>" + "<tr><td class=\"reportLabel\">System Comment</td><td class=\"reportLabel\">"
							+ uF.showData((String) hmTemp.get("EMP_IEL"), "-") + "</td><td class=\"reportLabel\">" + uF.showData((String) hmTemp.get("EMP_OEL"), "-") + "</td></tr>" + "<tr><td class=\"reportLabel\">Emp's Reason</td><td class=\"reportLabel\">"
							+ uF.showData((String) hmTemp.get("EMP_IR"), "-") + "</td><td class=\"reportLabel\">" + uF.showData((String) hmTemp.get("EMP_OR"), "-") + "</td></tr>"
							+ "<tr><td class=\"reportLabel\">Manager's Comment</td><td class=\"reportLabel\">" + uF.showData((String) hmTemp.get("MANAGER_IC"), "-") + "</td><td class=\"reportLabel\">" + uF.showData((String) hmTemp.get("MANAGER_OC"), "-")
							+ "</td></tr>" + "<tr><td class=\"reportLabel\">Status</td><td class=\"reportLabel\">" + uF.showData((String) hmTemp.get("STATUS_IN"), "-") + "</td><td class=\"reportLabel\">" + uF.showData((String) hmTemp.get("STATUS_OUT"), "-")
							+ "</td></tr>" + "</table>" + "</div>" + "</td>");
					sb.append("</tr>");
				}

			}

			if (i == 0) {
				sb.append("<tr><td class=\"reportLabel alignCenter\" colspan=\"6\">&nbsp;" + "You have No clock entries for Today" + "</td></tr>");
			}

			if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN)) {
				sb.append("<input name=\"EMPID\" type=\"hidden\" value=\"" + strReqEmpID + "\" >");
				sb.append("<tr><td align=\"center\" colspan=\"4\">&nbsp;" + "<input  class=\"input_button\" type=\"submit\" value=\"Update Entries\" >" + "&nbsp;<input onclick=\"history.go(-1)\" class=\"input_button\" type=\"button\" value=\"Go back\" >"
						+ "</td></tr>");
			}

			sb.append("</table>");
			sb.append("</form>");

		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("TIMESHEET", sb.toString());

		return SUCCESS;

	}
	
	
	private String approve;
	private String deny;
	private String typeOfLeave;
	
	public String updateClockEntries() {

		Connection con = null;
		PreparedStatement pst = null, pst1 = null, pst2 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		StringBuilder sb = new StringBuilder();
		try {

			con = db.makeConnection(con);

			
			
			
			if(getTypeOfLeave()!=null) {
				
				pst = con.prepareStatement("INSERT INTO emp_leave_entry (emp_id,leave_from,leave_to,entrydate,emp_no_of_leave,leave_type_id,reason,approval_from,approval_to_date, is_approved, user_id) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setDate(2, uF.getDateFormat(getStrCurrentDate(), DBDATE));
				pst.setDate(3, uF.getDateFormat(getStrCurrentDate(), DBDATE));
				pst.setDate(4, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE));				
				pst.setInt(5, 1);
				pst.setInt(6, uF.parseToInt(getTypeOfLeave()));
				pst.setString(7, "");
				pst.setDate(8, uF.getDateFormat(getStrCurrentDate(), DBDATE));
				pst.setDate(9, uF.getDateFormat(getStrCurrentDate(), DBDATE));
				pst.setInt(10, 1);
				pst.setInt(11, uF.parseToInt(strSessionEmpId));
				pst.execute();
				pst.close();
				
				return SUCCESS;
				
			}
			
			String strDate1 = null;
			String strDate2 = null;

			pst = con.prepareStatement(selectClockEntries1_N);
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setString(2, "IN");
			pst.setDate(3, ((getStrCurrentDate() != null) ? uF.getDateFormat(getStrCurrentDate(), DBDATE) : uF.getDateFormat(getStrDate(), CF.getStrReportDateFormat())));
			pst.setInt(4, uF.parseToInt(getStrServiceId()));
//			System.out.println("UCE/4048--pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				strDate1 = uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, DBDATE);
				strDate2 = uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, DBDATE);
			}
			rs.close();
			pst.close();

			if (strDate2 == null) {
				strDate2 = ((getStrCurrentDate() != null) ? getStrCurrentDate() : uF.getDateFormat(getStrDate(), CF.getStrReportDateFormat()) + "");
			}
			
			if (getStrEmpIN() != null) {

				long lIn = 0;
				long lOut = 0;

				
				
				if (strDate2 != null && getStrEmpIN() != null && !getStrEmpIN().equalsIgnoreCase(NO_TIME_RECORD)) {
					lIn = uF.getTimeFormat(strDate2 + getStrEmpIN(), DBDATE + CF.getStrReportTimeFormat()).getTime();
				}

				pst = con.prepareStatement(selectClockEntries1_N);
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setString(2, "OUT");
				pst.setDate(3, uF.getDateFormat(strDate2, DBDATE));
				pst.setInt(4, uF.parseToInt(getStrServiceId()));
//				System.out.println("9 pst======>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					lOut = rs.getTimestamp("in_out_timestamp").getTime();
				}
				rs.close();
				pst.close();

				double dblHoursWorked = 0;
				if (lOut > 0) {
					dblHoursWorked = uF.parseToDouble(uF.getTimeDiffInHoursMins(lIn, lOut));
				}

				try {
					pst = con.prepareStatement(updateClockEntries2_N);
					pst.setTimestamp(1, uF.getTimeStamp(strDate2 + getStrEmpIN(), DBDATE + CF.getStrReportTimeFormat()));
					pst.setDouble(2, dblHoursWorked);

					if(getApprove()!=null) {
						pst.setInt(3, 1);
					} else if(getDeny()!=null) {
						pst.setInt(3, -1);
					}
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
					
					pst.setInt(5, uF.parseToInt(getStrEmpId()));
					pst.setString(6, "IN");
					pst.setDate(7, uF.getDateFormat(strDate2, DBDATE));
					pst.setInt(8, uF.parseToInt(getStrServiceId()));
					int xIn = pst.executeUpdate();
					pst.close();

//					if (xIn == 0) {
//						pst = con.prepareStatement(insertClockEntries1_N);
//						pst.setInt(1, uF.parseToInt(getStrEmpId()));
//						pst.setTimestamp(2, uF.getTimeStamp(strDate2 + getStrEmpIN(), DBDATE + CF.getStrReportTimeFormat()));
//						pst.setTimestamp(3, uF.getTimeStamp(strDate2 + getStrEmpIN(), DBDATE + CF.getStrReportTimeFormat()));
//						pst.setDouble(4, 0);
//						pst.setString(5, "IN");
//						pst.setInt(6, uF.parseToInt(getStrServiceId()));
//						pst.execute();
//
//					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				

				try {

//					dblHoursWorked = CF.calculateTimeDeduction(con,dblHoursWorked);

					pst = con.prepareStatement(updateClockEntries3_N);
					pst.setDouble(1, dblHoursWorked);
					pst.setInt(2, uF.parseToInt(getStrEmpId()));
					pst.setString(3, "OUT");
					pst.setDate(4, uF.getDateFormat(strDate2, DBDATE));
					pst.setInt(5, uF.parseToInt(getStrServiceId()));
					pst.execute();
					pst.close();

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			if (getStrEmpOUT() != null) {

				long lIn = 0;
				long lOut = 0;

				if (strDate2 != null && getStrEmpOUT() != null && !getStrEmpOUT().equalsIgnoreCase(NO_TIME_RECORD)) {
					lOut = uF.getTimeFormat(strDate2 + getStrEmpOUT(), DBDATE + CF.getStrReportTimeFormat()).getTime();
				}

				if (strDate2 == null) {
					strDate2 = getStrCurrentDate();
				}


				pst = con.prepareStatement(selectClockEntries1_N);
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setString(2, "IN");
				pst.setDate(3, uF.getDateFormat(strDate2, DBDATE));
				pst.setInt(4, uF.parseToInt(getStrServiceId()));
//				System.out.println("10 pst======>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					lIn = rs.getTimestamp("in_out_timestamp").getTime();
				}
				rs.close();
				pst.close();

				double dblHoursWorked = 0;
				if (lIn > 0) {

					dblHoursWorked = uF.parseToDouble(uF.getTimeDiffInHoursMins(lIn, lOut));

//					dblHoursWorked = CF.calculateTimeDeduction(con,dblHoursWorked);

				}

				try {

					pst = con.prepareStatement(updateClockEntries2_N);
					pst.setTimestamp(1, uF.getTimeStamp(strDate2 + getStrEmpOUT(), DBDATE + CF.getStrReportTimeFormat()));
					// pst.setDouble(2, -2);
					pst.setDouble(2, dblHoursWorked);
					if(getApprove()!=null) {
						pst.setInt(3, 1);
					} else if(getDeny()!=null) {
						pst.setInt(3, -1);
					}
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
					
					pst.setInt(5, uF.parseToInt(getStrEmpId()));
					pst.setString(6, "OUT");
					pst.setDate(7, uF.getDateFormat(strDate2, DBDATE));
					pst.setInt(8, uF.parseToInt(getStrServiceId()));
					int xOut = pst.executeUpdate();
					pst.close();

					if (xOut == 0) {
						pst = con.prepareStatement(insertClockEntries1_N);
						pst.setInt(1, uF.parseToInt(getStrEmpId()));
						pst.setTimestamp(2, uF.getTimeStamp(strDate2 + getStrEmpOUT(), DBDATE + CF.getStrReportTimeFormat()));
						pst.setTimestamp(3, uF.getTimeStamp(strDate2 + getStrEmpOUT(), DBDATE + CF.getStrReportTimeFormat()));
						pst.setDouble(4, dblHoursWorked);
						pst.setString(5, "OUT");
						pst.setInt(6, uF.parseToInt(getStrServiceId()));
						if(getApprove()!=null) {
							pst.setInt(7, 1);
						} else if(getDeny()!=null) {
							pst.setInt(7, -1);
						}
						pst.setInt(8, uF.parseToInt(strSessionEmpId));
						
						pst.execute();
						pst.close();

					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			
			
			if(getApprove()!=null) {
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(APPROVED_DENIED_EXCEPTION, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrEmpId(getStrEmpId());
				nF.setStrDate(uF.getDateFormat(strDate2, DBDATE, CF.getStrReportDateFormat()));
				nF.setStrApprvedDenied("approved");
				nF.setEmailTemplate(true);
				nF.sendNotifications();
			}
			
			if(getDeny()!=null) { 
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(APPROVED_DENIED_EXCEPTION, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrEmpId(getStrEmpId());
				nF.setStrDate(uF.getDateFormat(strDate2, DBDATE, CF.getStrReportDateFormat()));
				nF.setStrApprvedDenied("denied");
				nF.setEmailTemplate(true);
				nF.sendNotifications();
			}
			
			
			


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeStatements(pst2);
			db.closeConnection(con);
		}

		return SUCCESS;

	}

	String strServiceId;
	String strEmpId;
	String strEmpIN;
	String strEmpOUT;
	String strDate;
	String redirectUrl;
	String strDATE;
	String strCurrentDate;

	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStrEmpIN() {
		return strEmpIN;
	}

	public void setStrEmpIN(String strEmpIN) {
		this.strEmpIN = strEmpIN;
	}

	public String getStrEmpOUT() {
		return strEmpOUT;
	}

	public void setStrEmpOUT(String strEmpOUT) {
		this.strEmpOUT = strEmpOUT;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	public String getStrDATE() {
		return strDATE;
	}

	public void setStrDATE(String strDATE) {
		this.strDATE = strDATE;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getStrCurrentDate() {
		return strCurrentDate;
	}

	public void setStrCurrentDate(String strCurrentDate) {
		this.strCurrentDate = strCurrentDate;
	}

	public String getStrServiceId() {
		return strServiceId;
	}

	public void setStrServiceId(String strServiceId) {
		this.strServiceId = strServiceId;
	}

	public void call() {

		CF = new CommonFunctions();
		CF.setRequest(request);
		CF.setStrTimeZone("Australia/Sydney");
		CF.setStrCURRENCY_SHORT("$");
		CF.setStrCURRENCY_FULL("$");
		CF.setStrReportDateFormat("dd/MM/yyyy");
		CF.setStrReportTimeFormat("HH:mm");
		CF.setStrReportDayFormat("EEEE");
		CF.setStrReportTimeAM_PMFormat("hh:mma");

		strD1 = "31/08/2011";
		strD2 = "31/08/2011";
		viewClockEntries(null, null, new UtilityFunctions());
	}

	public static void main(String args[]) {

		UpdateClockEntries up = new UpdateClockEntries();
		up.call();

	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public List<FillPayCycles> getPayCycleList() {
		return payCycleList;
	}

	public void setPayCycleList(List<FillPayCycles> payCycleList) {
		this.payCycleList = payCycleList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillEmployee> getEmpNamesList() {
		return empNamesList;
	}

	public void setEmpNamesList(List<FillEmployee> empNamesList) {
		this.empNamesList = empNamesList;
	}



	public List<FillLeaveType> getLeaveTypeList() {
		return leaveTypeList;
	}

	public void setLeaveTypeList(List<FillLeaveType> leaveTypeList) {
		this.leaveTypeList = leaveTypeList;
	}

	public String getApprove() {
		return approve;
	}

	public void setApprove(String approve) {
		this.approve = approve;
	}

	public String getDeny() {
		return deny;
	}

	public void setDeny(String deny) {
		this.deny = deny;
	}

	public String getTypeOfLeave() {
		return typeOfLeave;
	}

	public void setTypeOfLeave(String typeOfLeave) {
		this.typeOfLeave = typeOfLeave;
	}

	public String getStrSelectedEmpId() {
		return strSelectedEmpId;
	}

	public void setStrSelectedEmpId(String strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}

	public String getDivid() {
		return divid;
	}

	public void setDivid(String divid) {
		this.divid = divid;
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

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
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

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}
	public String getFromPage() {
		return fromPage;
	}
	
	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

}