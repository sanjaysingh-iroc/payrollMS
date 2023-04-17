package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.charts.BarchartRssource;
import com.konnect.jpms.export.GeneratePdfResource;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.tms.PayCycleList;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.Employee;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeMandaysReport extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpID;
	String strUserType = null;
	String strEmpId = null;
//	String strP;
	String param;
	
	GeneratePdfResource gpr=new GeneratePdfResource();
	BarchartRssource br=new BarchartRssource();
	
	public static	String strActual;
	public static String strRoster;

	String paramSelection;

	String financialYear;
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	
	List<FillOrganisation> orgList;
	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	
	List<FillServices> serviceList;
	
	List<FillFinancialYears> financialYearList;
	
	String exportType;
	
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(EmployeeHoursVar.class);
	
	
	ArrayList<String> data=new ArrayList<String>();
	ArrayList<String> data1=new ArrayList<String>();
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(TITLE, "Employee Mandays");
		
		UtilityFunctions uF = new UtilityFunctions();

		strEmpID = (String) request.getParameter("EMPID");
//		strP = (String) request.getParameter("paramSelection");
		
		if(getF_org()==null || getF_org().trim().equals("")) {
			setF_org((String)session.getAttribute(ORGID));
		}
		if(getParamSelection() == null){
			setParamSelection("EH");
		}
		
		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if(getStrSbu() != null && !getStrSbu().equals("")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		
		request.setAttribute(PAGE, "/jsp/reports/EmployeeMandaysReport.jsp");
		viewPayCycle(uF);
		
		return loadPayCycle(uF);
	}

	public String loadPayCycle(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=orgList.get(i).getOrgName();
					} else {
						strOrg+=", "+orgList.get(i).getOrgName();
					}
					k++;
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
				hmFilter.put("SERVICE", "All SBU's");
			}
		} else {
			hmFilter.put("SERVICE", "All SBU's");
		}
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				for(int j=0;j<getF_level().length;j++) {
					if(getF_level()[j].equals(levelList.get(i).getLevelId())) {
						if(k==0) {
							strLevel=levelList.get(i).getLevelCodeName();
						} else {
							strLevel+=", "+levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Level's");
			}
		} else {
			hmFilter.put("LEVEL", "All Level's");
		}
		
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
		
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	public String viewPayCycle(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		String strUserType=null;

		try {
			
			Map hmWorkLocation = null;
			Map hmDepartment = null;
			Map hmUserType = null;
			Map<String,String> hmServices = null;
			Map hmEmpName = null;
			
			String startDate = null;
			String strDisplayPaycycle = null;
			String strPaycycleDuration = null;
			
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			
			if(getParamSelection()!=null && getParamSelection().equalsIgnoreCase("WLH")) {
				hmWorkLocation = CF.getWLocationMap(con,strUserType, request, strEmpId);
				strUserType="By WorkLocation";
				
			} else if(getParamSelection()!=null && getParamSelection().equalsIgnoreCase("DH")) {
				hmDepartment = CF.getDepartmentMap(con,strUserType,  strEmpId);
				strUserType="By Department";
				
			} else if(getParamSelection()!=null && getParamSelection().equalsIgnoreCase("UTH")) {
				hmUserType = CF.getUserTypeMap(con);
				strUserType="By UserType";
				
			} else if(getParamSelection()!=null && getParamSelection().equalsIgnoreCase("SH")) {
				hmServices = CF.getServicesMap(con, false);
				strUserType="By Services";
				
			} else {
				hmEmpName = CF.getEmpNameMap(con, strUserType, strEmpId);
				strUserType="By Employee";
			}
			
			pst = con.prepareStatement(selectShift);
			pst.setInt(1, uF.parseToInt(getF_org()));
			rs = pst.executeQuery();
			Map<String, String> hmShiftRosterLunch = new HashMap<String, String>();
			while (rs.next()) {
				if(!(rs.getString("shift_code").equalsIgnoreCase("ST"))) {
					long fromTime = uF.getTimeFormat(rs.getString("break_start"), DBTIME ).getTime();
					long toTime = uF.getTimeFormat(rs.getString("break_end"), DBTIME ).getTime();
	
					double timeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(fromTime,toTime)));
					hmShiftRosterLunch.put(rs.getString("shift_id"), ""+timeDiff);
				}
			} 
			rs.close();
			pst.close();
			
			StringBuilder sb = new StringBuilder();
			int nPayCycle = 0;

			int nDurationCount = 0;
			
			gpr.clearList();
			br.clearList();
			List<String> alPayCycles = new ArrayList<String>();
			List<String> alSubTitle = new ArrayList<String>();
			List<String> alInnerChart = new ArrayList<String>();
			List<String> alInnerChart1 = new ArrayList<String>();
			List<String> alInnerChart2 = new ArrayList<String>();
			List<String> alInnerDates = new ArrayList<String>();
			if(getFinancialYear() == null) {
				String str1FY[] = CF.getFinancialYear(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT), CF, uF);
				setFinancialYear(str1FY[0]+"-"+str1FY[1]);
			}
			String strFinancialYR[] = getFinancialYear().split("-");
			String[] arr = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), uF.getDateFormat(strFinancialYR[1], DATE_FORMAT, DBDATE), CF, getF_org(), request);
			request.setAttribute("currentPaycycle", arr[2]);
			
			List<FillPayCycles> paycycleList = new FillPayCycles(request).fillBetweenNoofPayCycles(CF, getF_org(), (uF.parseToInt(arr[2])), (uF.parseToInt(arr[2])-11));
			if(paycycleList !=null && paycycleList.size() > 0){
				Collections.reverse(paycycleList);
			}
			List<String> alPaycycleNo = new ArrayList<String>();
			for(FillPayCycles fillPayCycles : paycycleList) {
				String[] arrDates = fillPayCycles.getPaycycleId().split("-");
				sb = new StringBuilder();
				sb.append("PC " + arrDates[2] + "<br>" + uF.getDateFormat(arrDates[0], DATE_FORMAT, "dd MMM yy") + " - " + uF.getDateFormat(arrDates[1], DATE_FORMAT, "dd MMM yy"));
				alPayCycles.add(sb.toString());
				alInnerChart.add(nPayCycle+"");
				alInnerChart1.add("PayCycle"+" "+arrDates[2]+" "+arrDates[0]+"  "+arrDates[1]);
				alInnerChart2.add(arrDates[0]+"-"+arrDates[1]);
				alInnerDates.add(arrDates[0]);
				alInnerDates.add(arrDates[1]);
				
				alPaycycleNo.add(arrDates[2]);
			}
			if(alPaycycleNo !=null && alPaycycleNo.size() > 0) {
				Collections.reverse(alPaycycleNo);
			}
			request.setAttribute("alPaycycleNo", alPaycycleNo);
				
			Map<String, String> hmTotalA = new HashMap<String, String>();
			Map<String, String> hmTotalR = new HashMap<String, String>();
			Map<String, String> hmTotalV = new HashMap<String, String>();
			
			Map<String, Map<String, String>> hmActual = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmRoster = new HashMap<String, Map<String, String>>();
			Map<String, String> hmActualInner = new HashMap<String, String>();
			Map<String, String> hmRosterInner = new HashMap<String, String>();
			List alId = new ArrayList();
			List<String> alIdTemp = new ArrayList<String>();
			int x = -1;
			for (int i = 0; i < alInnerDates.size();) {
				x++;
				
				String strD1 = (String) alInnerDates.get(i++);
				String strD2 = (String) alInnerDates.get(i++);
				
				if(strUserType!=null){

					if(getParamSelection()!=null && (getParamSelection().equalsIgnoreCase("WLH") || getParamSelection().equalsIgnoreCase("DH"))){
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("SELECT *, ad.emp_id as empl_id FROM attendance_details ad, employee_official_details eod WHERE eod.emp_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and in_out = 'OUT'");

						if(getF_level()!=null && getF_level().length>0){
			                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
			            }
			            if(getF_department()!=null && getF_department().length>0){
			                sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			            }
			            
			            if(getF_service()!=null && getF_service().length>0){
			            	 sbQuery.append(" and ad.service_id in ("+StringUtils.join(getF_service(), ",")+") ");
			            } 
			            
			            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
			                sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
			            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
							sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
						}
			            
			            if(uF.parseToInt(getF_org())>0){
							sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
						}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
							sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
						}
						
						sbQuery.append(" order by empl_id, in_out_timestamp desc");
//						pst = con.prepareStatement(selectClockEntriesR4_E_Actual);
						pst = con.prepareStatement(sbQuery.toString());
					} else if(getParamSelection()!=null && (getParamSelection().equalsIgnoreCase("UTH") || getParamSelection().equalsIgnoreCase("SH"))) {
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("SELECT *, ad.emp_id as empl_id FROM attendance_details ad, user_details ud, employee_official_details eod WHERE eod.emp_id = ud.emp_id and eod.emp_id = ad.emp_id and ud.emp_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and in_out = 'OUT'");

						
						if(getF_level()!=null && getF_level().length>0){
			                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
			            }
			            if(getF_department()!=null && getF_department().length>0){
			                sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			            }
			            
			            if(getF_service()!=null && getF_service().length>0){
			            	 sbQuery.append(" and ad.service_id in ("+StringUtils.join(getF_service(), ",")+") ");
					    } 
			            
			            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
			                sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
			            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
							sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
						}
			            
			            if(uF.parseToInt(getF_org())>0){
							sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
						}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
							sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
						}
						
						sbQuery.append(" order by empl_id, in_out_timestamp desc");
						
//						pst = con.prepareStatement(selectClockEntriesR5_E_Actual);
						pst = con.prepareStatement(sbQuery.toString());
					} else {
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("SELECT *, ad.emp_id as empl_id FROM attendance_details ad, employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id=ad.emp_id AND eod.emp_id = ad.emp_id AND eod.emp_id = epd.emp_per_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and in_out = 'OUT'");
//						sbQuery.append(" and ad.emp_id = 92");
						if(getF_level()!=null && getF_level().length>0){
			                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
			            }
			            if(getF_department()!=null && getF_department().length>0){
			                sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			            }
			            
			            if(getF_service()!=null && getF_service().length>0){
			            	 sbQuery.append(" and ad.service_id in ("+StringUtils.join(getF_service(), ",")+") ");
			            } 
			            
			            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
			                sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
			            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
							sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
						}
			            
			            if(uF.parseToInt(getF_org())>0){
							sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
						}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
							sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
						}	
						
						sbQuery.append(" order by  emp_fname, emp_lname, empl_id, in_out_timestamp desc");
//						pst = con.prepareStatement(selectClockEntriesR3_E_Actual);
						pst = con.prepareStatement(sbQuery.toString());
					}
					pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));

				}
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				String strOldId = null;
				String strNewId = null;
				String strName = null;
				while (rs.next()) {
					if(getParamSelection()!=null && getParamSelection().equalsIgnoreCase("WLH")) {
						strNewId = rs.getString("wlocation_id");
						strName = (String)hmWorkLocation.get(strNewId);
					} else if(getParamSelection()!=null && getParamSelection().equalsIgnoreCase("DH")) {
						strNewId = rs.getString("depart_id");
						strName = (String)hmDepartment.get(strNewId);
					} else if(getParamSelection()!=null && getParamSelection().equalsIgnoreCase("UTH")) {
						strNewId = rs.getString("usertype_id");
						strName = (String)hmUserType.get(strNewId);
					} else if(getParamSelection()!=null && getParamSelection().equalsIgnoreCase("SH")) {
						
						strNewId = rs.getString("service_id");
						strNewId = strNewId.replace(",","");
						strNewId = strNewId.trim();
						strName = (String)hmServices.get(strNewId);
					} else {
						strNewId = rs.getString("emp_id");
						strName = rs.getString("emp_fname")+" "+rs.getString("emp_lname");
					}
					
					hmActualInner = hmActual.get(x+""+strNewId);
					if (hmActualInner == null || (strNewId != null && !strNewId.equalsIgnoreCase(strOldId))) {
						hmActualInner = new HashMap<String, String>();
					}
					
					double dblActual = 0;
					double dblRoster = 0;
//					System.out.println("hmActualInner ===>> " + hmActualInner);
					dblActual = uF.parseToDouble((String) hmActualInner.get(x + ""));
					dblActual++;
//					System.out.println("dblActual ===>> " + dblActual);
					dblRoster = uF.parseToDouble((String) hmRosterInner.get(x + ""));
					if (rs.getString("in_out").equalsIgnoreCase("OUT")) {
						hmActualInner.put(x + "", dblActual+ "");
						/*if(getParamSelection()!=null && getParamSelection().equalsIgnoreCase("SH")) {
							hmActualInner.put(x + "L", "<a href=\"ClockEntries.action?T=T&PAY=Y&EMPID=" + strNewId + "&PC=" + x + "&D1=" + strD1 + "&D2=" + strD2 + "\">" + uF.formatIntoOneDecimal((dblActual + rs.getDouble("hours_worked"))) + "</a>");
						} else {
							hmActualInner.put(x + "L", "<a href=\"javascript:void(0);\">" + uF.formatIntoOneDecimal((dblActual + rs.getDouble("hours_worked"))) + "</a>");
						}*/
					}
					hmActual.put(x+""+strNewId, hmActualInner);
					
					strOldId = strNewId;
					if(alIdTemp!=null && !alIdTemp.contains(strNewId)) {
						alIdTemp.add(strNewId);
						if(strName != null) {
							alId.add(new Employee(strName, strNewId));
						}
					}
				}
				rs.close();
				pst.close();
				
//				System.out.println("hmActual ===>> " + hmActual);
			}
			
			x = -1;
			for (int i = 0; i < alInnerDates.size();) {
				x++;
				
				String strD1 = (String) alInnerDates.get(i++);
				String strD2 = (String) alInnerDates.get(i++);
				
				Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
				Map<String, Set<String>> hmWeekEndDates = CF.getWeekEndDateList(con, strD1, strD2, CF, uF, hmWeekEndHalfDates, null);
				Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
				List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
				Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
				CF.getEmpRosterWeekOffTypeByDate(con, uF, CF, strD1, strD2, alEmpCheckRosterWeektype, hmRosterWeekEndDates, hmWeekEndDates, hmEmpLevelMap, hmEmpWlocation, hmWeekEndHalfDates);

				Map<String,String> hmHolidays = new HashMap<String, String>();
				Map<String,String> hmHolidayDates = new HashMap<String, String>();
				CF.getHolidayList(con,request,strD1, strD2, CF, hmHolidayDates, hmHolidays, true);
				
				if(strUserType != null) {
				
					if(getParamSelection()!=null && (getParamSelection().equalsIgnoreCase("WLH") || getParamSelection().equalsIgnoreCase("DH"))){
						
							StringBuilder sbQuery = new StringBuilder();
							sbQuery.append("SELECT *, rd.emp_id as empl_id FROM roster_details rd, employee_official_details eod  where rd.emp_id=eod.emp_id and _date between ? AND ? ");

							if(getF_level()!=null && getF_level().length>0){
				                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
				            }
				            if(getF_department()!=null && getF_department().length>0){
				                sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
				            }
				            
				            if(getF_service()!=null && getF_service().length>0){
				            	 sbQuery.append(" and rd.service_id in ("+StringUtils.join(getF_service(), ",")+") ");
							} 
				            
				            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
				                sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
				            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
								sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
							}
				            
				            if(uF.parseToInt(getF_org())>0){
								sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
							}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
								sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
							}
							
							sbQuery.append(" order by empl_id, _date desc");
//							pst = con.prepareStatement(selectClockEntriesR4_E_Roster);
							pst = con.prepareStatement(sbQuery.toString());
							
					} else if(getParamSelection()!=null && (getParamSelection().equalsIgnoreCase("UTH") || getParamSelection().equalsIgnoreCase("SH"))) {
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("SELECT *, rd.emp_id as empl_id FROM roster_details rd, user_details ud  where rd.emp_id=ud.emp_id and _date between ? AND ?");

						if(getF_level()!=null && getF_level().length>0){
			                sbQuery.append(" and ud.emp_id in (select emp_id from employee_official_details where grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") )) ");
			            }
			            if(getF_department()!=null && getF_department().length>0){
			                sbQuery.append(" and ud.emp_id in (select emp_id from employee_official_details where depart_id in ("+StringUtils.join(getF_department(), ",")+")) ");
			            }
			            
			            if(getF_service()!=null && getF_service().length>0){
			            	 sbQuery.append(" and rd.service_id in ("+StringUtils.join(getF_service(), ",")+") ");
			            } 
			            
			            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
			                sbQuery.append(" and ud.emp_id in (select emp_id from employee_official_details where wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")) ");
			            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
							sbQuery.append(" and ud.emp_id in (select emp_id from employee_official_details where wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+"))");
						}
			            
			            if(uF.parseToInt(getF_org())>0){
							sbQuery.append(" and ud.emp_id in (select emp_id from employee_official_details where org_id = "+uF.parseToInt(getF_org())+")");
						}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
							sbQuery.append(" and ud.emp_id in (select emp_id from employee_official_details where org_id in ("+(String)session.getAttribute(ORG_ACCESS)+"))");
						}
						
						sbQuery.append(" order by empl_id, _date desc");
//						pst = con.prepareStatement(selectClockEntriesR5_E_Roster);
						pst = con.prepareStatement(sbQuery.toString());
						
					} else {
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("SELECT *, rd.emp_id as empl_id FROM roster_details rd, employee_personal_details epd  where rd.emp_id=epd.emp_per_id  and _date between ? AND ? ");

						if(getF_level()!=null && getF_level().length>0){
			                sbQuery.append(" and epd.emp_per_id in (select emp_id from employee_official_details where grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ) ");
			            }
			            if(getF_department()!=null && getF_department().length>0){
			                sbQuery.append(" and epd.emp_per_id in (select emp_id from employee_official_details where depart_id in ("+StringUtils.join(getF_department(), ",")+") )");
			            }
			            
			            if(getF_service()!=null && getF_service().length>0){
			            	 sbQuery.append(" and rd.service_id in ("+StringUtils.join(getF_service(), ",")+") ");
						} 
			            
			            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
			                sbQuery.append(" and epd.emp_per_id in (select emp_id from employee_official_details where wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")) ");
			            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
							sbQuery.append(" and epd.emp_per_id in (select emp_id from employee_official_details where wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+"))");
						}
			            
			            if(uF.parseToInt(getF_org())>0){
							sbQuery.append(" and epd.emp_per_id in (select emp_id from employee_official_details where org_id = "+uF.parseToInt(getF_org())+")");
						}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
							sbQuery.append(" and epd.emp_per_id in (select emp_id from employee_official_details where org_id in ("+(String)session.getAttribute(ORG_ACCESS)+"))");
						} 
						
						sbQuery.append(" order by  emp_fname, empl_id, _date desc");
//						pst = con.prepareStatement(selectClockEntriesR3_E_Roster);
						pst = con.prepareStatement(sbQuery.toString());
					}	
					
					pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));

				} else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
					if(getParamSelection()!=null && (getParamSelection().equalsIgnoreCase("WLH") || getParamSelection().equalsIgnoreCase("DH"))) {
						pst = con.prepareStatement(selectClockEntriesR4_EManager_Roster);
					} else if(getParamSelection()!=null && (getParamSelection().equalsIgnoreCase("UTH") || getParamSelection().equalsIgnoreCase("SH"))) {
						pst = con.prepareStatement(selectClockEntriesR5_EManager_Roster);
					} else {
						pst = con.prepareStatement(selectClockEntriesR3_EManager_Roster);
					}
					
					pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
					pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
				}
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				String strOldId = null;
				String strNewId = null;

				while (rs.next()) {
					String strWLocationId = hmEmpWlocation.get(rs.getString("emp_id"));
					Set<String> weeklyOffSet= hmWeekEndDates.get(strWLocationId);
					if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
					
					Set<String> halfDayWeeklyOffSet= hmWeekEndHalfDates.get(strWLocationId);
					if(halfDayWeeklyOffSet==null) halfDayWeeklyOffSet=new HashSet<String>();
					
					Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(rs.getString("emp_id"));
					if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
					
					if(alEmpCheckRosterWeektype.contains(rs.getString("emp_id"))) {
						if(rosterWeeklyOffSet.contains(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT))) {
							continue;
						}
					} else if(weeklyOffSet.contains(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT))) {
						continue;
					} else if(hmHolidayDates.containsKey(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())+"_"+strWLocationId)) {
						continue;
					}
					
					if(getParamSelection()!=null && getParamSelection().equalsIgnoreCase("WLH")) {
						strNewId = rs.getString("wlocation_id");
					} else if(getParamSelection()!=null && getParamSelection().equalsIgnoreCase("DH")) {
						strNewId = rs.getString("depart_id");
					} else if(getParamSelection()!=null && getParamSelection().equalsIgnoreCase("UTH")) {
						strNewId = rs.getString("usertype_id");
					} else if(getParamSelection()!=null && getParamSelection().equalsIgnoreCase("SH")) {
						strNewId = rs.getString("service_id");
					} else {
						strNewId = rs.getString("emp_id");
					}
					
					hmRosterInner = (Map) hmRoster.get(x+""+strNewId);
					if ( strNewId != null && !strNewId.equalsIgnoreCase(strOldId)) {
						hmRosterInner = new HashMap();
					}
					
					double dblRoster = 0; 
//					System.out.println("hmRosterInner ===>> " + hmRosterInner);
					dblRoster = uF.parseToDouble((String) hmRosterInner.get(x + ""));
					dblRoster++;
//					System.out.println("dblRoster ===>> " + dblRoster);
					hmRosterInner.put(x + "", dblRoster + "");
					
//					if(getParamSelection()!=null && getParamSelection().equalsIgnoreCase("EH")) {
//						hmRosterInner.put(x + "L", "<a href=\"ClockEntries.action?T=T&PAY=Y&EMPID=" + strNewId + "&PC=" + x + "&D1=" + strD1 + "&D2=" + strD2 + "\">" + uF.formatIntoOneDecimal(dblActualRoster) + "</a>");
//					} else {
//						hmRosterInner.put(x + "L", "<a href=\"javascript:void(0);\">" + uF.formatIntoOneDecimal(dblActualRoster) + "</a>");	
//					}

					hmRoster.put(x+""+strNewId, hmRosterInner);
					strOldId = strNewId;
				}
				rs.close();
				pst.close();
			}
			
			Collections.sort(alId, new BeanComparator("strName"));
			
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			if(hmEmpCode == null) hmEmpCode = new HashMap<String, String>();
			
			List alReport = new ArrayList();
			List<String> alInner = new ArrayList<String>();
			
			alSubTitle.add("");
			for (int i = 0 ; i<alPayCycles.size(); i++) {
				alSubTitle.add("Actual");
				alSubTitle.add("Roster");
				alSubTitle.add("Var");
			}
			
			for (int j = 0; j < alId.size(); j++) {
				 alInner = new ArrayList();
				 
				String strCol = ((j%2==0)?"dark":"light");
				//String strEmpId = (String) alEmpId.get(j);
				Employee objEmp1 = (Employee) alId.get(j);
				String strEmpId1 = (String) objEmp1.getStrEmpId();
				String strEmpName1 = (String) objEmp1.getStrName();
				
//				alInner.add(uF.showData((String)hmEmpCode.get(strEmpId1), ""));
				alInner.add(strEmpName1);
			
				for (int i = 0 ; i<alPayCycles.size(); i++) {
					
					hmActualInner = (Map) hmActual.get(i+strEmpId1);
					if(hmActualInner==null){hmActualInner=new HashMap();}
					hmRosterInner = (Map) hmRoster.get(i+strEmpId1);
					if(hmRosterInner==null){hmRosterInner=new HashMap();}

					double dblActual = uF.parseToDouble((String) hmActualInner.get(i+""));
					double dblRoster = uF.parseToDouble((String) hmRosterInner.get(i+""));
					double dblVariance = dblActual - dblRoster; 
					
					alInner.add(uF.formatIntoOneDecimal(dblActual)) ;
					alInner.add(uF.formatIntoOneDecimal(dblRoster)) ;
					alInner.add(uF.formatIntoOneDecimal(dblVariance)) ;
					
					double nWFCountA = uF.parseToDouble((String)hmTotalA.get(i+""));
					double nWFCountR = uF.parseToDouble((String)hmTotalR.get(i+""));
					double nWFCountV = uF.parseToDouble((String)hmTotalV.get(i+""));
					
					nWFCountA +=uF.parseToDouble(uF.formatIntoOneDecimal(dblActual));
					nWFCountR +=uF.parseToDouble(uF.formatIntoOneDecimal(dblRoster));
					nWFCountV +=uF.parseToDouble(uF.formatIntoOneDecimal(dblVariance));
					
					hmTotalA.put(i+"", uF.formatIntoOneDecimal(nWFCountA));
					hmTotalR.put(i+"", uF.formatIntoOneDecimal(nWFCountR));
					hmTotalV.put(i+"", uF.formatIntoOneDecimal(nWFCountV));
				}
				alReport.add(alInner);
			}
			
			request.setAttribute("alReport", alReport);
			request.setAttribute("hmTotalA", hmTotalA);
			request.setAttribute("hmTotalR", hmTotalR);
			request.setAttribute("hmTotalV", hmTotalV);
			request.setAttribute("hmActual", hmActual);
			request.setAttribute("hmRoster", hmRoster);
			request.setAttribute("alId", alId);
			request.setAttribute("alPayCycles", alPayCycles);
			request.setAttribute("alSubTitle", alSubTitle);
			
			StringBuilder sbActualHours = new StringBuilder();
			StringBuilder sbRosterHours = new StringBuilder();
			StringBuilder sbActualPC = new StringBuilder();
			
			for (int i = alInnerChart.size()-1 ; i>=18; i--) {
				sbActualPC.append("'"+alInnerChart.get(i)+"'");
				if(i>18) 
					sbActualPC.append(",");
			}
			
			for (int j = 0; j < alId.size(); j++) {
				Employee objEmp = (Employee) alId.get(j);
				String strEmpId = (String) objEmp.getStrEmpId();
				String strEmpName = (String) objEmp.getStrName();
				//strEmpNamefull=strEmpName;
				sbActualHours.append(
						"{"+
						"name: '"+strEmpName+"',"+
						"data: ["
				);
				
				sbRosterHours.append(
						"{"+
						"name: '"+strEmpName+"',"+
						"data: ["
				);

				for (int i = alInnerChart.size()-1 ; i>=alInnerChart.size()-6; i--) {
					Map hmActualInner1 = (Map) hmActual.get(i+strEmpId);
					if(hmActualInner1==null) {
						hmActualInner1=new HashMap();
					}
					Map hmRosterInner1 = (Map) hmRoster.get(i+strEmpId);
					if(hmRosterInner1==null) {
						hmRosterInner1=new HashMap();
					}
					strActual=(String) hmActualInner1.get(i+"");
					data1.add(strActual);
					
					strRoster=(String)hmRosterInner1.get(i+"");
					data1.add(strRoster);
						
					sbActualHours.append(uF.showData(uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble((String)hmActualInner1.get(i+""))), 0 + ""));
					sbRosterHours.append(uF.showData(uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble((String)hmRosterInner1.get(i+""))), 0 + ""));
				
					if (i>alInnerChart.size()-6) {
						sbActualHours.append(",");
						sbRosterHours.append(",");
					}
					
					if (i==alInnerChart.size()-6) {
						sbActualHours.append("]}");
						sbRosterHours.append("]}");
					}
				}
				
				if(j<alId.size()-1) {
					sbActualHours.append(",");
					sbRosterHours.append(",");
				}
				
				gpr.callEmpdata(strEmpName, data1);
				br.call2(strEmpName, data1);
			}
			
			gpr.callCycle(alInnerChart1,strUserType);
			br.callCycle(alInnerChart1,alInnerChart,alInnerChart2,strUserType);
			data.clear();
			
			request.setAttribute("alPayCyclesChart", alInnerChart);
			request.setAttribute("sbActualHours", sbActualHours);
			request.setAttribute("sbActualPC", sbActualPC.toString());
			request.setAttribute("sbRosterHours", sbRosterHours);
			
			//System.out.println("sbActualHours===>> " + sbActualHours);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
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
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public static void main(String args[]) {

		try {
			PayCycleList pcl = new PayCycleList();
			pcl.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getParamSelection() {
		return paramSelection;
	}

	public void setParamSelection(String paramSelection) {
		this.paramSelection = paramSelection;
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

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
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
	
}