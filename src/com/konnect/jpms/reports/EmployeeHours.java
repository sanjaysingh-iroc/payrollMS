package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.charts.BarchartRssource;
import com.konnect.jpms.export.GeneratePdfResource;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.tms.PayCycleList;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.Employee;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeHours extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpID;
	String strUserType = null;
	String strEmpId = null;
	String strP;
	String param;
	String duration;
	
	String[] f_strWLocation; 
	String[] f_department;
	String[] f_level; 
	String[] f_service;
	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	
	GeneratePdfResource gpr=new GeneratePdfResource();
	BarchartRssource br=new BarchartRssource();
	
	public static	String strActual;
	public static String strRoster;


	List<FillOrganisation> organisationList;
	String f_org;
	
	
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(EmployeeHours.class);
	
	
	ArrayList<String> data=new ArrayList<String>();
	ArrayList<String> data1=new ArrayList<String>();
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(TITLE, TViewActualHours);
		

		strEmpID = (String) request.getParameter("EMPID");
		strP = (String) request.getParameter("param");
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)
				 && !strUserType.equalsIgnoreCase(CEO) && !strUserType.equalsIgnoreCase(CFO)
				 && !strUserType.equalsIgnoreCase(HRMANAGER) && !strUserType.equalsIgnoreCase(ACCOUNTANT)
				 && !strUserType.equalsIgnoreCase(MANAGER)){
			request.setAttribute(PAGE, PAccessDenied);
			return ACCESS_DENIED;
		}else{
			if(strP!=null && strP.equalsIgnoreCase("ESH")){
				request.setAttribute(PAGE, PReportEmployeeHours1);
				viewPayCycle1();
			}else{
				request.setAttribute(PAGE, PReportEmployeeHours);
				viewPayCycle(strP);
			}
		}
		
		
		

		return loadPayCycle();

	}

	public String loadPayCycle() {
		UtilityFunctions uF=new UtilityFunctions();
		
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		
		if(getParam()==null){
			setParam("EH");
		}
		
		
		return LOAD;
	}

	public String viewPayCycle(String strP) {
		
		String strEmpNamefull=null;

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		String strUserType=null;

		try {

			
			
			Map hmWorkLocation = null;
			Map hmDepartment = null;
			Map hmUserType = null;
			Map hmServices = null;
			Map hmEmpName = null;
			
			String startDate = null;
			String strDisplayPaycycle = null;
			String strPaycycleDuration = null;
			
			con = db.makeConnection(con);
			
			if(strP!=null && strP.equalsIgnoreCase("WLH")){
				hmWorkLocation = CF.getWLocationMap(con,strUserType, request, strEmpId);
				
				strUserType="By WorkLocation";
				
			}else if(strP!=null && strP.equalsIgnoreCase("DH")){
				hmDepartment = CF.getDepartmentMap(con,strUserType,  strEmpId);
				strUserType="By Department";
			}else if(strP!=null && strP.equalsIgnoreCase("UTH")){
				hmUserType = CF.getUserTypeMap(con);
				strUserType="By UserType";
			}else if(strP!=null && strP.equalsIgnoreCase("SH")){
				hmServices = CF.getServicesMap(con, true);
				strUserType="By Services";
			}else{
				hmEmpName = CF.getEmpNameMap(con, strUserType, strEmpId);
				strUserType="By Employee";
			}
			
			
			pst = con.prepareStatement(selectSettings);
			rs = pst.executeQuery();

			while (rs.next()) {
				if (rs.getString("options").equalsIgnoreCase(O_START_PAY_CLYCLE)) {
					startDate = rs.getString("value");
				}
				if (rs.getString("options").equalsIgnoreCase(O_DISPLAY_PAY_CLYCLE)) {
					strDisplayPaycycle = rs.getString("value");
				}
				if (rs.getString("options").equalsIgnoreCase(O_PAYCYCLE_DURATION)) {
					strPaycycleDuration = rs.getString("value");
				}
			}
			rs.close();
			pst.close();

			String []arrDisplayPAycycle = null;
			int minCycle = 0;
			int maxCycle = 0;
			if(strDisplayPaycycle!=null){
				arrDisplayPAycycle = strDisplayPaycycle.split("-");
				minCycle = uF.parseToInt(arrDisplayPAycycle[0]);
				maxCycle = uF.parseToInt(arrDisplayPAycycle[1]);
			}
			
			Calendar calCurrent = GregorianCalendar.getInstance(TimeZone.getTimeZone((CF.getStrTimeZone())));
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(startDate, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(startDate, DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(startDate, DATE_FORMAT, "yyyy")));
			

			StringBuilder sb = new StringBuilder();
			int nPayCycle = 0;

			int nDurationCount = 0;
			
			String dt1 = null;
			String dt2 = null;
			gpr.clearList();
			br.clearList();
			List<String> alPayCycles = new ArrayList<String>();
			List<String> alSubTitle = new ArrayList<String>();
			List<String> alInnerChart = new ArrayList<String>();
			List<String> alInnerChart1 = new ArrayList<String>();
			List<String> alInnerChart2 = new ArrayList<String>();
			List<String> alInnerDates = new ArrayList<String>();

			String strCurrent = ((calCurrent.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + calCurrent.get(Calendar.DAY_OF_MONTH) : calCurrent.get(Calendar.DAY_OF_MONTH)) + "/" + (((calCurrent.get(Calendar.MONTH) + 1) < 10) ? "0" + (calCurrent.get(Calendar.MONTH) + 1) : (calCurrent.get(Calendar.MONTH) + 1)) + "/"
								+ calCurrent.get(Calendar.YEAR);
			java.util.Date strCurrentDate = uF.getDateFormatUtil(strCurrent, DATE_FORMAT);

			java.util.Date strCurrentPayCycleD1 = null;
			java.util.Date strCurrentPayCycleD2 = null;

			while (true) {
				
				
				
				
//				System.out.println("nPayCycle====>"+nPayCycle);
				
				
				sb = new StringBuilder();
				nPayCycle++;
				
				
				
				
				

				if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("M")){
					nDurationCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH) -1 ;
				}else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("F")){
					nDurationCount = 15 - 1 ;
				}else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("BW")){
					nDurationCount = 14 - 1 ;
				}else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("W")){
					nDurationCount = 7 - 1 ;
				}else{
					nDurationCount = cal.getMaximum(Calendar.DAY_OF_MONTH) -1 ;
				}
				
				dt1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				cal.add(Calendar.DAY_OF_MONTH, nDurationCount);
				dt2 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);

				
				if(nPayCycle<minCycle){
					cal.add(Calendar.DAY_OF_MONTH, 1);
					continue;
				}
				
				sb.append("PC " + nPayCycle + "<br>" + uF.getDateFormat(dt1, DATE_FORMAT, "dd MMM") + "-" + uF.getDateFormat(dt2, DATE_FORMAT, "dd MMM"));
				
				strCurrentPayCycleD1 = uF.getDateFormatUtil(dt1, DATE_FORMAT);
				strCurrentPayCycleD2 = uF.getDateFormatUtil(dt2, DATE_FORMAT);
				
				
				if (strCurrentDate.equals(strCurrentPayCycleD1) || strCurrentDate.equals(strCurrentPayCycleD2) || (strCurrentDate.after(strCurrentPayCycleD1) && strCurrentDate.before(strCurrentPayCycleD2))) {
					// alInner.add("<a style=\"font-weight:bold;color:blue;\" href="
					// + request.getContextPath() +
					// "/EmployeeReportPayCycle.action?T=" + strPayCycleType +
					// "&PC=" + nPayCycle + "&D1=" + dt1 + "&D2=" + dt2 + " >" +
					// sb.toString() + "</a>");
					alPayCycles.add(sb.toString());
					alInnerChart.add(nPayCycle+"");
					alInnerChart1.add("PayCycle"+" "+nPayCycle+" "+dt1+"  "+dt2);
					alInnerChart2.add(dt1+"-"+dt2);
					alInnerDates.add(dt1);
					alInnerDates.add(dt2);
					
				} else if (strCurrentDate.before(strCurrentPayCycleD2)) {
//					alInner.add(sb.toString());
//					alInnerDates.add(dt1);
//					alInnerDates.add(dt2);
				} else {
					
					
					if(getDuration()!=null && getDuration().equalsIgnoreCase("1M")){
						
					}else if(getDuration()!=null && getDuration().equalsIgnoreCase("3M")){
						
					}else if(getDuration()!=null && getDuration().equalsIgnoreCase("6M")){
						
					}else if(getDuration()!=null && getDuration().equalsIgnoreCase("1Y")){
						
					}else if(getDuration()!=null && getDuration().equalsIgnoreCase("5Y")){
						
					}
					
					
					alPayCycles.add(sb.toString());
					alInnerChart.add(nPayCycle+"");
					alInnerChart1.add("PayCycle"+" "+nPayCycle+" "+dt1+"  "+dt2);
					alInnerChart2.add(dt1+"-"+dt2);
					alInnerDates.add(dt1);
					alInnerDates.add(dt2);
				}

				cal.add(Calendar.DAY_OF_MONTH, 1);

				if(nPayCycle>=maxCycle){
					break;
				}

			}

			
			Map<String, String> hmTotal = new HashMap<String, String>();
			Map hmActual = new HashMap();
			Map hmRoster = new HashMap();
			Map hmActualInner = new HashMap();
			Map hmRosterInner = new HashMap();
			List alId = new ArrayList();
			List alIdTemp = new ArrayList();
			int x = -1;
			for (int i = 0; i < alInnerDates.size();) {
				x++;
				
				String strD1 = (String) alInnerDates.get(i++);
				String strD2 = (String) alInnerDates.get(i++);
				
				if(strUserType!=null){

					if(strP!=null && (strP.equalsIgnoreCase("WLH") || strP.equalsIgnoreCase("DH"))){
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("SELECT *, ad.emp_id as empl_id FROM attendance_details ad, employee_official_details eod WHERE eod.emp_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and in_out = 'OUT'");

						/*if(uF.parseToInt(getF_strWLocation())>0){
							sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation()));
						}
						if(uF.parseToInt(getF_service())>0){
							sbQuery.append(" and ad.service_id = "+uF.parseToInt(getF_service()));
						}
						if(uF.parseToInt(getF_department())>0){
							sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));
						}
						if(uF.parseToInt(getF_level())>0){
							sbQuery.append(" and grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
						}*/
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
//						pst = con.prepareStatement(selectClockEntriesR4_E_Actual);
						pst = con.prepareStatement(sbQuery.toString());
					}else if(strP!=null && (strP.equalsIgnoreCase("UTH") || strP.equalsIgnoreCase("SH"))){
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("SELECT *, ad.emp_id as empl_id FROM attendance_details ad, user_details ud, employee_official_details eod WHERE eod.emp_id = ud.emp_id and eod.emp_id = ad.emp_id and ud.emp_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and in_out = 'OUT'");

						/*if(uF.parseToInt(getF_strWLocation())>0){
							sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation()));
						}
						if(uF.parseToInt(getF_service())>0){
							sbQuery.append(" and ad.service_id = "+uF.parseToInt(getF_service()));
						}
						if(uF.parseToInt(getF_department())>0){
							sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));
						}
						if(uF.parseToInt(getF_level())>0){
							sbQuery.append(" and grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
						}*/
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
					}else{
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("SELECT *, ad.emp_id as empl_id FROM attendance_details ad, employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id=ad.emp_id AND eod.emp_id = ad.emp_id AND eod.emp_id = epd.emp_per_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? and in_out = 'OUT'");

						/*if(uF.parseToInt(getF_strWLocation())>0){
							sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation()));
						}
						if(uF.parseToInt(getF_department())>0){
							sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));
						}
						if(uF.parseToInt(getF_service())>0){
							sbQuery.append(" and ad.service_id = "+uF.parseToInt(getF_service()));
						}
						if(uF.parseToInt(getF_level())>0){
							sbQuery.append(" and grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
						}*/
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
						
						sbQuery.append(" order by  emp_fname, emp_lname, empl_id, in_out_timestamp desc");
//						pst = con.prepareStatement(selectClockEntriesR3_E_Actual);
						pst = con.prepareStatement(sbQuery.toString());
						
						
					}
					
					pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));

				}
				
//				else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
//					
//					if(strP!=null && (strP.equalsIgnoreCase("WLH") || strP.equalsIgnoreCase("DH"))){
//						pst = con.prepareStatement(selectClockEntriesR4_EManager_Actual);
//					}else if(strP!=null && (strP.equalsIgnoreCase("UTH") || strP.equalsIgnoreCase("SH"))){
//						pst = con.prepareStatement(selectClockEntriesR5_EManager_Actual);
//					}else{
//						pst = con.prepareStatement(selectClockEntriesR3_EManager_Actual);
//					}
//					
//					
//					pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
//					pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//					pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
//
//				}
				
//				log.debug("pst====>" + pst);

				
//				System.out.println("pst====>" + pst);
				
				rs = pst.executeQuery();

				String strOldId = null;
				String strNewId = null;
				String strName = null;

				while (rs.next()) {

					if(strP!=null && strP.equalsIgnoreCase("WLH")){
						strNewId = rs.getString("wlocation_id");
						strName = (String)hmWorkLocation.get(strNewId);
					}else if(strP!=null && strP.equalsIgnoreCase("DH")){
						strNewId = rs.getString("depart_id");
						strName = (String)hmDepartment.get(strNewId);
					}else if(strP!=null && strP.equalsIgnoreCase("UTH")){
						strNewId = rs.getString("usertype_id");
						strName = (String)hmUserType.get(strNewId);
					}else if(strP!=null && strP.equalsIgnoreCase("SH")){
						strNewId = rs.getString("service_id");
						strName = (String)hmServices.get(strNewId);
						log.debug("strNewId="+strNewId+" hmServices="+hmServices);
						
					}else{
						strNewId = rs.getString("emp_id");
						strName = rs.getString("emp_fname")+" "+rs.getString("emp_lname");
					}
					
					hmActualInner = (Map) hmActual.get(x+strNewId);
					if (hmActualInner == null || (strNewId != null && !strNewId.equalsIgnoreCase(strOldId))) {
						hmActualInner = new HashMap();
					}
					
					
					
					
					

					double dblActual = 0;
					double dblRoster = 0;
					dblActual = uF.parseToDouble((String) hmActualInner.get(x + ""));
					dblRoster = uF.parseToDouble((String) hmRosterInner.get(x + ""));

					if (rs.getString("in_out").equalsIgnoreCase("OUT")) {
						hmActualInner.put(x + "", (dblActual + rs.getDouble("hours_worked")) + "");
						if(strP!=null && strP.equalsIgnoreCase("SH")){
							hmActualInner.put(x + "L", "<a href=\"ClockEntries.action?T=T&PAY=Y&EMPID=" + strNewId + "&PC=" + x + "&D1=" + strD1 + "&D2=" + strD2 + "\">" + uF.formatIntoOneDecimal((dblActual + rs.getDouble("hours_worked"))) + "</a>");
						}else{
							hmActualInner.put(x + "L", "<a href=\"javascript:void(0);\">" + uF.formatIntoOneDecimal((dblActual + rs.getDouble("hours_worked"))) + "</a>");
						}
						
						
						
						
					}

					
					hmActual.put(x+strNewId, hmActualInner);
					
					
					
					
					
					
					
					
					strOldId = strNewId;
					if(alIdTemp!=null && !alIdTemp.contains(strNewId)){
						alIdTemp.add(strNewId);
						if(strName!=null){
							alId.add(new Employee(strName, strNewId));
						}
					}
				}
				rs.close();
				pst.close();
			}
			
			
			
			
			x = -1;
			for (int i = 0; i < alInnerDates.size();) {
				x++;
				
				String strD1 = (String) alInnerDates.get(i++);
				String strD2 = (String) alInnerDates.get(i++);
				
				if(strUserType!=null){
				
					if(strP!=null && (strP.equalsIgnoreCase("WLH") || strP.equalsIgnoreCase("DH"))){
						
							StringBuilder sbQuery = new StringBuilder();
							sbQuery.append("SELECT *, rd.emp_id as empl_id FROM roster_details rd, employee_official_details eod  where rd.emp_id=eod.emp_id and _date between ? AND ? ");

							/*if(uF.parseToInt(getF_strWLocation())>0){
								sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation()));
							}
							if(uF.parseToInt(getF_service())>0){
								sbQuery.append(" and rd.service_id = "+uF.parseToInt(getF_service()));
							}
							if(uF.parseToInt(getF_department())>0){
								sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));
							}
							if(uF.parseToInt(getF_level())>0){
								sbQuery.append(" and grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
							}*/
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
							
					}else if(strP!=null && (strP.equalsIgnoreCase("UTH") || strP.equalsIgnoreCase("SH"))){
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("SELECT *, rd.emp_id as empl_id FROM roster_details rd, user_details ud  where rd.emp_id=ud.emp_id and _date between ? AND ?");

						/*if(uF.parseToInt(getF_strWLocation())>0){
							sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation()));
						}
						if(uF.parseToInt(getF_service())>0){
							sbQuery.append(" and rd.service_id = "+uF.parseToInt(getF_service()));
						}
						if(uF.parseToInt(getF_department())>0){
							sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));
						}
						if(uF.parseToInt(getF_level())>0){
							sbQuery.append(" and grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
						}*/
						if(getF_level()!=null && getF_level().length>0){
			                sbQuery.append(" and ud.emp_per_id in (select emp_id from employee_official_details where grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) )");
			            }
			            if(getF_department()!=null && getF_department().length>0){
			                sbQuery.append(" and ud.emp_per_id in (select emp_id from employee_official_details where depart_id in ("+StringUtils.join(getF_department(), ",")+")) ");
			            }
			            
			            if(getF_service()!=null && getF_service().length>0){
			            	 sbQuery.append(" and rd.service_id in ("+StringUtils.join(getF_service(), ",")+") ");
			            } 
			            
			            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
			                sbQuery.append(" and ud.emp_per_id in (select emp_id from employee_official_details where wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")) ");
			            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
							sbQuery.append(" and ud.emp_per_id in (select emp_id from employee_official_details where wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+"))");
						}
			            
			            if(uF.parseToInt(getF_org())>0){
							sbQuery.append(" and ud.emp_per_id in (select emp_id from employee_official_details where org_id = "+uF.parseToInt(getF_org())+")");
						}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
							sbQuery.append(" and ud.emp_per_id in (select emp_id from employee_official_details where org_id in ("+(String)session.getAttribute(ORG_ACCESS)+"))");
						}
			            
						
						sbQuery.append(" order by empl_id, _date desc");
//						pst = con.prepareStatement(selectClockEntriesR5_E_Roster);
						pst = con.prepareStatement(sbQuery.toString());
						
					}else{
						
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("SELECT *, rd.emp_id as empl_id FROM roster_details rd, employee_personal_details epd  where rd.emp_id=epd.emp_per_id  and _date between ? AND ? ");

						/*if(uF.parseToInt(getF_strWLocation())>0){
							sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation()));
						}
						if(uF.parseToInt(getF_service())>0){
							sbQuery.append(" and rd.service_id = "+uF.parseToInt(getF_service()));
						}
						if(uF.parseToInt(getF_department())>0){
							sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));
						}
						if(uF.parseToInt(getF_level())>0){
							sbQuery.append(" and grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
						}*/
						if(getF_level()!=null && getF_level().length>0){
			                sbQuery.append(" and epd.emp_per_id in (select emp_id from employee_official_details where grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ) ");
			            }
			            if(getF_department()!=null && getF_department().length>0){
			                sbQuery.append(" and epd.emp_per_id in (select emp_id from employee_official_details where depart_id in ("+StringUtils.join(getF_department(), ",")+")) ");
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

				}else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
					
					if(strP!=null && (strP.equalsIgnoreCase("WLH") || strP.equalsIgnoreCase("DH"))){
						pst = con.prepareStatement(selectClockEntriesR4_EManager_Roster);
					}else if(strP!=null && (strP.equalsIgnoreCase("UTH") || strP.equalsIgnoreCase("SH"))){
						pst = con.prepareStatement(selectClockEntriesR5_EManager_Roster);
					}else{
						pst = con.prepareStatement(selectClockEntriesR3_EManager_Roster);
					}
					
					pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
					pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));

				}
				

				rs = pst.executeQuery();

				String strOldId = null;
				String strNewId = null;

				while (rs.next()) {

					
					if(strP!=null && strP.equalsIgnoreCase("WLH")){
						strNewId = rs.getString("wlocation_id");
					}else if(strP!=null && strP.equalsIgnoreCase("DH")){
						strNewId = rs.getString("depart_id");
					}else if(strP!=null && strP.equalsIgnoreCase("UTH")){
						strNewId = rs.getString("usertype_id");
					}else if(strP!=null && strP.equalsIgnoreCase("SH")){
						strNewId = rs.getString("service_id");
					}else{
						strNewId = rs.getString("emp_id");
					}
					
					
					hmRosterInner = (Map) hmRoster.get(x +strNewId);

					if ( strNewId != null && !strNewId.equalsIgnoreCase(strOldId)) {
						hmRosterInner = new HashMap();
					}
					
					double dblRoster = 0;
					dblRoster = uF.parseToDouble((String) hmRosterInner.get(x + ""));
					hmRosterInner.put(x + "", (dblRoster + rs.getDouble("actual_hours")) + "");
					
					if(strP!=null && strP.equalsIgnoreCase("EH")){
						hmRosterInner.put(x + "L", "<a href=\"ClockEntries.action?T=T&PAY=Y&EMPID=" + strNewId + "&PC=" + x + "&D1=" + strD1 + "&D2=" + strD2 + "\">" + uF.formatIntoOneDecimal((dblRoster + rs.getDouble("actual_hours"))) + "</a>");
					}else{
						hmRosterInner.put(x + "L", "<a href=\"javascript:void(0);\">" + uF.formatIntoOneDecimal((dblRoster + rs.getDouble("actual_hours"))) + "</a>");	
					}

					hmRoster.put(x+strNewId, hmRosterInner);
					
					
					
					
					
					
					strOldId = strNewId;
					
				}
				rs.close();
				pst.close();

			}
			
			Collections.sort(alId, new BeanComparator("strName"));
			
			
			
			
			
			
//			System.out.println("alId=="+alId);
			
			
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
					
					
					
					double nWFCount = uF.parseToDouble((String)hmTotal.get(i+""));
					nWFCount +=uF.parseToDouble(uF.formatIntoOneDecimal(dblActual));
					hmTotal.put(i+"", uF.formatIntoOneDecimal(nWFCount)); 
				}
			
				alReport.add(alInner);
			
			}
			
			
			
//			System.out.println("alReport===>"+alReport);
			
			
			request.setAttribute("alReport", alReport);
			
			request.setAttribute("hmTotal", hmTotal);
			
			
			
			
			
			
			request.setAttribute("hmActual", hmActual);
			request.setAttribute("hmRoster", hmRoster);
			request.setAttribute("alId", alId);
			request.setAttribute("alPayCycles", alPayCycles);
			request.setAttribute("alSubTitle", alSubTitle);
			
			log.debug("alId==>"+alId);
			log.debug("alPayCycles==>"+alPayCycles);
			log.debug("alPayCyclesChart==>"+alInnerChart);
			
			//charts ===>>
			
//		    StringBuilder sb1 = new StringBuilder();        
//			for (int j = 0; j < alId.size(); j++) {
//				
//				sb1.append(
//						
//						"{"+
//						"name: '"+((Employee)alId.get(j)).getStrName()+"Actual"+"',"+
//						"data: [49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4]" +
//			         "}"
//			         
//			       );
//				
//				if(j<alId.size()-1) {
//					sb1.append(",");
//				}
//				
//			}
			
			
			
//			StringBuilder sbActualHours = new StringBuilder();
//			StringBuilder sbActualPC = new StringBuilder();
//			
//			for (int i = alInnerChart.size()-1 ; i>=18; i--) {
//				
//				sbActualPC.append("'"+alInnerChart.get(i)+"'");
//				
//				if(i>18) 
//					sbActualPC.append(",");
//			}
//			
//			for (int j = 0; j < alId.size(); j++) {
//				
//				Employee objEmp = (Employee) alId.get(j);
//				String strEmpId = (String) objEmp.getStrEmpId();
//				String strEmpName = (String) objEmp.getStrName();
//				
//				sbActualHours.append(
//						
//						"{"+
//						"name: '"+strEmpName+"',"+
//						"data: ["
//						
//						);
//				
//				for (int i = alInnerChart.size()-1 ; i>=alInnerChart.size()-6; i--) {
//					
//						Map hmActualInner1 = (Map) hmActual.get(i+strEmpId);
//						if(hmActualInner1==null){hmActualInner1=new HashMap();}
//						
//						sbActualHours.append(
//								
//								uF.showData((String) hmActualInner1.get(i+""), 0 + "")
//					         
//					       );
//					
//						if (i>alInnerChart.size()-6) {
//							
//							sbActualHours.append(",");
//							
//						}
//						
//						if (i==alInnerChart.size()-6) {
//							
//							sbActualHours.append("]}");
//							
//						}
//						
//				}
//				
//				if(j<alId.size()-1) {
//					sbActualHours.append(",");
//				}
//				
//			}
//			
//			StringBuilder sbRosterHours = new StringBuilder();     
//			
//			for (int j = 0; j < alId.size(); j++) {
//				
//				Employee objEmp = (Employee) alId.get(j);
//				String strEmpId = (String) objEmp.getStrEmpId();
//				String strEmpName = (String) objEmp.getStrName();
//				
//				sbRosterHours.append(
//						
//						"{"+
//						"name: '"+strEmpName+"',"+
//						"data: ["
//						
//						);
//				
//				for (int i = alInnerChart.size()-1 ; i>=alInnerChart.size()-6; i--) {
//					
//					Map hmRosterInner1 = (Map) hmRoster.get(i+strEmpId);
//					if(hmRosterInner1==null){hmRosterInner1=new HashMap();}
//					
//					sbRosterHours.append(
//							
//							uF.showData((String) hmRosterInner1.get(i+""), 0 + "")
//				         
//				       );
//				
//					if (i>alInnerChart.size()-6) {
//						
//						sbRosterHours.append(",");
//						
//					}
//					
//					if (i==alInnerChart.size()-6) {
//						
//						sbRosterHours.append("]}");
//						
//					}
//					
//				}
//				
//				if(j<alId.size()-1) {
//					sbRosterHours.append(",");
//				}
//				
//			}
			
			
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
				strEmpNamefull=strEmpName;
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
						if(hmActualInner1==null){hmActualInner1=new HashMap();}
						Map hmRosterInner1 = (Map) hmRoster.get(i+strEmpId);
						if(hmRosterInner1==null){hmRosterInner1=new HashMap();}
						

						strActual=(String) hmActualInner1.get(i+"");
						data1.add(strActual);

						
						strRoster=(String)hmRosterInner1.get(i+"");
					data1.add(strRoster);
						
						
					
						
						sbActualHours.append(
								
								uF.showData(uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble((String)hmActualInner1.get(i+""))), 0 + "")
					         
					    );
						
						sbRosterHours.append(
								
								uF.showData(uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble((String)hmRosterInner1.get(i+""))), 0 + "")
					         
					    );
					
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
			
			
		//	gpr.call2(strEmpNamefull, data);
			
			//String strReportType="Resource Effort Report";
			
			gpr.callCycle(alInnerChart1,strUserType);
			br.callCycle(alInnerChart1,alInnerChart,alInnerChart2,strUserType);
			data.clear();
			
			
			
			request.setAttribute("alPayCyclesChart", alInnerChart);
			request.setAttribute("sbActualHours", sbActualHours);
			request.setAttribute("sbActualPC", sbActualPC.toString());
			request.setAttribute("sbRosterHours", sbRosterHours);
			
			log.debug("==========================charts=============================");
			log.debug("sbActualHours===>>"+sbActualHours);
			log.debug("sbRosterHours===>>"+sbRosterHours);
			log.debug("sbActualPC==>"+sbActualPC);
			

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
	
	
	 

	public String viewPayCycle1() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			String startDate = null;

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectSettings);
			rs = pst.executeQuery();

			while (rs.next()) {
				if (rs.getString("options").equalsIgnoreCase(O_START_PAY_CLYCLE)) {
					startDate = rs.getString("value");
				}
			}
			rs.close();
			pst.close();

			Calendar calCurrent = GregorianCalendar.getInstance(TimeZone.getTimeZone((CF.getStrTimeZone())));
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(startDate, CF.getStrReportDateFormat(), "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(startDate, CF.getStrReportDateFormat(), "MM")) - 1);

			StringBuilder sb = new StringBuilder();
			int nPayCycle = 0;

			String dt1 = null;
			String dt2 = null;

			List<String> alInner = new ArrayList<String>();
			List<String> alInnerDates = new ArrayList<String>();

			String strCurrent = ((calCurrent.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + calCurrent.get(Calendar.DAY_OF_MONTH) : calCurrent.get(Calendar.DAY_OF_MONTH)) + "/" + (((calCurrent.get(Calendar.MONTH) + 1) < 10) ? "0" + (calCurrent.get(Calendar.MONTH) + 1) : (calCurrent.get(Calendar.MONTH) + 1)) + "/"
								+ calCurrent.get(Calendar.YEAR);
			java.util.Date strCurrentDate = uF.getDateFormatUtil(strCurrent, CF.getStrReportDateFormat());


			java.util.Date strCurrentPayCycleD1 = null;
			java.util.Date strCurrentPayCycleD2 = null;

			while (true) {
				sb = new StringBuilder();
				nPayCycle++;

				dt1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				cal.add(Calendar.DAY_OF_MONTH, 13);
				dt2 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);

				sb.append("Pay Cycle " + nPayCycle + "<br>" + dt1 + "-" + dt2);

				strCurrentPayCycleD1 = uF.getDateFormatUtil(dt1, CF.getStrReportDateFormat());
				strCurrentPayCycleD2 = uF.getDateFormatUtil(dt2, CF.getStrReportDateFormat());

				if (strCurrentDate.equals(strCurrentPayCycleD1) || strCurrentDate.equals(strCurrentPayCycleD2) || (strCurrentDate.after(strCurrentPayCycleD1) && strCurrentDate.before(strCurrentPayCycleD2))) {
					// alInner.add("<a style=\"font-weight:bold;color:blue;\" href="
					// + request.getContextPath() +
					// "/EmployeeReportPayCycle.action?T=" + strPayCycleType +
					// "&PC=" + nPayCycle + "&D1=" + dt1 + "&D2=" + dt2 + " >" +
					// sb.toString() + "</a>");
					alInner.add(sb.toString());
					alInnerDates.add(dt1);
					alInnerDates.add(dt2);
				} else if (strCurrentDate.before(strCurrentPayCycleD2)) {
//					alInner.add(sb.toString());
//					alInnerDates.add(dt1);
//					alInnerDates.add(dt2);
				} else {
					alInner.add(sb.toString());
					alInnerDates.add(dt1);
					alInnerDates.add(dt2);
				}

				cal.add(Calendar.DAY_OF_MONTH, 1);

				if (nPayCycle == 26) {
					break;
				}

			}

			Map hm = new HashMap();
			Map hmInner = new HashMap();
			List al = new ArrayList();
			List alServiceId = new ArrayList();
			int x = -1;
			for (int i = 0; i < alInnerDates.size();) {
				x++;
				
				String strD1 = (String) alInnerDates.get(i++);
				String strD2 = (String) alInnerDates.get(i++);
				
				if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)){
					
					pst = con.prepareStatement(selectClockEntriesR3_E);
					pst.setDate(1, uF.getDateFormat(strD1, CF.getStrReportDateFormat()));
					pst.setDate(2, uF.getDateFormat(strD2, CF.getStrReportDateFormat()));
					pst.setDate(3, uF.getDateFormat(strD1, CF.getStrReportDateFormat()));
					pst.setDate(4, uF.getDateFormat(strD2, CF.getStrReportDateFormat()));

				}else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
					pst = con.prepareStatement(selectClockEntriesR3_EManager);
					pst.setDate(1, uF.getDateFormat(strD1, CF.getStrReportDateFormat()));
					pst.setDate(2, uF.getDateFormat(strD2, CF.getStrReportDateFormat()));
					pst.setDate(3, uF.getDateFormat(strD1, CF.getStrReportDateFormat()));
					pst.setDate(4, uF.getDateFormat(strD2, CF.getStrReportDateFormat()));
					pst.setInt(5, uF.parseToInt((String) session.getAttribute("EMPID")));

				}
				

				rs = pst.executeQuery();

				String strEmpOldId = null;
				String strEmpNewId = null;

				while (rs.next()) {

					strEmpNewId = rs.getString("emp_id");
					hmInner = (Map) hm.get(rs.getString("emp_id"));

					if (hmInner == null && strEmpNewId != null && !strEmpNewId.equalsIgnoreCase(strEmpOldId)) {
//						al = new ArrayList();
//						hmInner = new HashMap();
					}

					if (hmInner == null) {
						al = new ArrayList();
						hmInner = new HashMap();
					}
					
					hmInner.put("EMP_NAME", rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
					
					if(!alServiceId.contains(rs.getString("service_id"))){
						alServiceId.add(rs.getString("service_id"));
					}
					
					
					log.debug(x+" hmInner="+hmInner);
					
					double dbl = 0;
					dbl = uF.parseToDouble((String) hmInner.get(x + rs.getString("service_id")+""));

					if (rs.getString("in_out").equalsIgnoreCase("OUT")) {
						al.add(uF.formatIntoTwoDecimal(dbl + rs.getDouble("hours_worked")) + "");
						hmInner.put(x +rs.getString("service_id")+ "", (dbl + rs.getDouble("hours_worked")) + "");
//						hmInner.put(x + rs.getString("service_id")+"L", "<a href=\"ClockEntries.action?T=T&PAY=Y&EMPID=" + strEmpNewId + "&PC=" + x + "&D1=" + strD1 + "&D2=" + strD2 + "\">" + (dbl + rs.getDouble("hours_worked")) + "</a>");
						hmInner.put(x + rs.getString("service_id")+"L", uF.formatIntoTwoDecimal(dbl + rs.getDouble("hours_worked")) +"");

					}

					
					hm.put(rs.getString("emp_id"), hmInner);
					strEmpOldId = strEmpNewId;
				}
				rs.close();
				pst.close();

			}

			request.setAttribute("hmList", hm);
			request.setAttribute("alPayCycles", alInner);
			request.setAttribute("alServiceId", alServiceId);
			request.setAttribute("alServiceName", CF.getServicesMap(con,true)); 

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

	public static void main(String args[]) {

		try {
			PayCycleList pcl = new PayCycleList();
			pcl.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

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

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}
	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
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
	

}

