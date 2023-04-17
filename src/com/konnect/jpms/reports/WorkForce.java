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
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.charts.BarChart;
import com.konnect.jpms.export.GeneratePdfReports;
import com.konnect.jpms.export.WorkForceReports;
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

public class WorkForce extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 * 
	 */
	
	public static	String strDetails;

	ArrayList<String> employeedata=new ArrayList<String>();
	ArrayList<String> employeedatapdf =new ArrayList<String>();
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpID; 
	String strUserType = null;
	String strEmpId = null;
	String strP;  
	
	String param;
	
	String[] f_strWLocation; 
	String[] f_department;
	String[] f_level; 
	String[] f_service;
	
	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(WorkForce.class);

	List<FillServices> serviceList;
	List<FillOrganisation> organisationList;
	String f_org;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(TITLE, TViewConsolidatedWorkForce);
		

		strEmpID = (String) request.getParameter("EMPID");
		strP = (String) request.getParameter("param");
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) 
				&& !strUserType.equalsIgnoreCase(CEO) && !strUserType.equalsIgnoreCase(CFO)
				&& !strUserType.equalsIgnoreCase(MANAGER) && !strUserType.equalsIgnoreCase(HRMANAGER)){
			
			request.setAttribute(PAGE, PAccessDenied);
			return ACCESS_DENIED;
			
		}else{
			
			if(getParam()==null){
				setParam("WLH");
				strP = getParam();
			}
			
			request.setAttribute(PAGE, PWorkForce);
			viewPayCycle(strP);
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
			setParam("WLE");
		}
		
		return LOAD;
	}

	public String viewPayCycle(String strP) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		GeneratePdfReports gprs=new GeneratePdfReports();
		BarChart bc=new BarChart();
		con = db.makeConnection(con);
		try {

			
			Map hmWorkLocation = null;
			Map hmDepartment = null;
			Map hmUserType = null;
			Map hmServices = null;
			Map hmEmpName = null;
			
			
			
			if(strP!=null && strP.equalsIgnoreCase("WLH")){
				hmWorkLocation = CF.getWLocationMap(con,strUserType, strEmpId);
//				strUserType="By location";
			}else if(strP!=null && strP.equalsIgnoreCase("DH")){
				hmDepartment = CF.getDepartmentMap(con,strUserType, strEmpId);
//				strUserType="By Department";
			}else if(strP!=null && strP.equalsIgnoreCase("UTH")){
				hmUserType = CF.getUserTypeMap(con);
//				strUserType="User type";
			}else if(strP!=null && strP.equalsIgnoreCase("SH")){
				hmServices = CF.getServicesMap(con,true);
//				strUserType="By services";
			}else{
				hmEmpName = CF.getEmpNameMap(con,strUserType, strEmpId);
//				strUserType="By Employee";
			}
			
			
			
			String startDate = null;
			String strDisplayPaycycle = null;
			String strPaycycleDuration = null;
			
			con = db.makeConnection(con);
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

			List<String> alPayCycles = new ArrayList<String>();
			List<String> alInnerChart = new ArrayList<String>();
			List<String> alInnerChart1 = new ArrayList<String>();
			List<String> alInnerChart2=new ArrayList<String>();
			List<String> alInnerChart3=new ArrayList<String>();
			
			List<String> alInnerDates = new ArrayList<String>();

			String strCurrent = ((calCurrent.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + calCurrent.get(Calendar.DAY_OF_MONTH) : calCurrent.get(Calendar.DAY_OF_MONTH)) + "/" + (((calCurrent.get(Calendar.MONTH) + 1) < 10) ? "0" + (calCurrent.get(Calendar.MONTH) + 1) : (calCurrent.get(Calendar.MONTH) + 1)) + "/"
								+ calCurrent.get(Calendar.YEAR);
			java.util.Date strCurrentDate = uF.getDateFormatUtil(strCurrent, DATE_FORMAT);

			java.util.Date strCurrentPayCycleD1 = null;
			java.util.Date strCurrentPayCycleD2 = null;

			while (true) {
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
					alInnerChart.add("Pay Cycle "+nPayCycle+"<br>"+dt1 + "-" + dt2 );
					
					alInnerChart1.add("Pay Cycle "+nPayCycle);
					alInnerChart2.add("Pay Cycle "+nPayCycle+" "+dt1+" "+ dt2);
					alInnerChart3.add(dt1+"-"+dt2);
					
					alInnerDates.add(dt1);
					alInnerDates.add(dt2);
				} else if (strCurrentDate.before(strCurrentPayCycleD2)) {
//					alInner.add(sb.toString());
//					alInnerDates.add(dt1);
//					alInnerDates.add(dt2);
				} else {
					alPayCycles.add(sb.toString());
					alInnerChart.add("Pay Cycle "+nPayCycle+"<br>"+dt1 + "-" + dt2 );
					alInnerChart1.add("Pay Cycle "+nPayCycle);
					alInnerChart2.add("Pay Cycle "+nPayCycle+" "+dt1+" "+ dt2);
					alInnerChart3.add(dt1+"-"+dt2);
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
	
			WorkForceReports wfr=new WorkForceReports(); 
	
			for (int i = 0; i < alInnerDates.size();) {
				x++;
				
				String strD1 = (String) alInnerDates.get(i++);
				String strD2 = (String) alInnerDates.get(i++);
				
				if(strUserType!=null ){ 

					if(strP!=null && strP.equalsIgnoreCase("WLH")){
						pst = con.prepareStatement(selectWorkForceR3_E);
						pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
					}else if(strP!=null && strP.equalsIgnoreCase("DH")){
						pst = con.prepareStatement(selectWorkForceR5_E);
						pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
					}else if(strP!=null && (strP.equalsIgnoreCase("SH"))){
						pst = con.prepareStatement(selectClockEntriesR5_E_Actual);
						pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
						pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
					}else if(strP!=null && (strP.equalsIgnoreCase("UTH"))){
						pst = con.prepareStatement(selectWorkForceR4_E);
						pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
					}else{
						pst = con.prepareStatement(selectWorkForceR3_E);
						pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
					}
					
					

				}else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
					
					if(strP!=null && (strP.equalsIgnoreCase("WLH") || strP.equalsIgnoreCase("DH"))){
						pst = con.prepareStatement(selectClockEntriesR4_EManager_Actual);
					}else if(strP!=null && (strP.equalsIgnoreCase("UTH") || strP.equalsIgnoreCase("SH"))){
						pst = con.prepareStatement(selectClockEntriesR5_EManager_Actual);
					}else{
						pst = con.prepareStatement(selectClockEntriesR3_EManager_Actual);
					}
					
					
					pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
					pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));

				}
				
//				log.debug("pst====>" + pst);

				
				
				System.out.println("pst===>"+pst);
				
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
					

					int nActual = 0;
					nActual = uF.parseToInt((String) hmActualInner.get(x + ""));
					hmActualInner.put(x + "", (nActual + 1) + "");
					hmActualInner.put(x + "L", "<a href=\"ClockEntries.action?T=T&PAY=Y&EMPID=" + strNewId + "&PC=" + x + "&D1=" + strD1 + "&D2=" + strD2 + "\">" + (nActual + 1) + "</a>");
					
					hmActual.put(x+strNewId, hmActualInner);
					
					
					
					int nWFCount = uF.parseToInt((String)hmTotal.get(x+""));
					nWFCount ++;
					hmTotal.put(x+"", nWFCount+"");
					
					
					
					strOldId = strNewId;
					if(alIdTemp!=null && !alIdTemp.contains(strNewId)){
						alIdTemp.add(strNewId);
						
						if(strP!=null && strP.equalsIgnoreCase("WLH")){
							if((String)hmWorkLocation.get(strNewId)!=null)
								alId.add(new Employee(strName, strNewId));
						}else if(strP!=null && strP.equalsIgnoreCase("DH")){
							if((String)hmDepartment.get(strNewId)!=null)
								alId.add(new Employee(strName, strNewId));
						}else if(strP!=null && strP.equalsIgnoreCase("UTH")){
							if((String)hmUserType.get(strNewId)!=null)
								alId.add(new Employee(strName, strNewId));
						}else if(strP!=null && strP.equalsIgnoreCase("SH")){
							if((String)hmServices.get(strNewId)!=null)
								alId.add(new Employee(strName, strNewId));
						}else{
							alId.add(new Employee(strName, strNewId));
						}
					}
				}
				rs.close();
				pst.close();
			}
			
			Collections.sort(alId, new BeanComparator("strName"));
			
			List alReport = new ArrayList();
			List<String> alInner = new ArrayList<String>();
			
			
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
					
//					alInner.add(uF.showData((String) hmActualInner.get(i + "L"), 0 + "") ) ;
					alInner.add(uF.showData((String) hmActualInner.get(i + ""), 0 + "") ) ;
					
				}
			
				alReport.add(alInner);
			
			}
			
			request.setAttribute("alReport", alReport);
			request.setAttribute("hmActual", hmActual);
			request.setAttribute("hmRoster", hmRoster);
			request.setAttribute("alId", alId);
			request.setAttribute("alPayCycles", alPayCycles);
			
			request.setAttribute("hmTotal", hmTotal);			
			
			
		
			
			StringBuilder sbActualHours = new StringBuilder();
			StringBuilder sbActualPC = new StringBuilder();
				
			wfr.clearList();
			wfr.clearList1();
			gprs.clearList();
			bc.clearList();
			for (int i = alInnerChart.size()-1 ; i>=18; i--) {
				
				sbActualPC.append("'"+alInnerChart.get(i)+"'");
				
				if(i>18) 
					sbActualPC.append(",");
				
			}
			
			for (int j = 0; j < alId.size(); j++) {
				
				Employee objEmp = (Employee) alId.get(j);
				String strEmpId = (String) objEmp.getStrEmpId();
				String strEmpName = (String) objEmp.getStrName();
				
				sbActualHours.append(
						
						"{"+
						"name: '"+strEmpName+"',"+
						"data: ["
						
						);
				
				for (int i = alInnerChart.size()-1 ; i>=0; i--) {
					Map hmActualInner1 = (Map) hmActual.get(i+strEmpId);
					if(hmActualInner1==null){hmActualInner1=new HashMap();}
					strDetails=(String) hmActualInner1.get(i+"");
					employeedatapdf.add(strDetails);
				}
				
				
								
				wfr.callXlsdata(employeedatapdf);
				employeedatapdf.clear();
				
				
				
				for (int i = alInnerChart.size()-1 ; i>=alInnerChart.size()-6; i--) {
					
						Map hmActualInner1 = (Map) hmActual.get(i+strEmpId);
						if(hmActualInner1==null){hmActualInner1=new HashMap();}
						
						sbActualHours.append(
								
								uF.showData((String) hmActualInner1.get(i+""), 0 + "")
					         
					    );
						
						

						strDetails=(String) hmActualInner1.get(i+"");
						employeedata.add(strDetails);
					
						
						
					
						if (i>alInnerChart.size()-6) {
							
							sbActualHours.append(",");
						}
						
						if (i==alInnerChart.size()-6) {
							
							sbActualHours.append("]," +
									"dataLabels: {" +
									"enabled: true," +
									"rotation: -90," +
									"color: '#FFFFFF'," +
									"align: 'right'," +
									"x: -3," +
									"y: 10," +
									"formatter: function() {" +
									"return this.y;" +
									"}," +
									"style: {" +
									"font: 'normal 13px Verdana, sans-serif'" +
									"}" +
									"}" +
									"}");
						}
						
				}
				
				String strReporttypeName="Work Force Management";
				
				//wfr.callPdfChartData(strReporttypeName,strEmpName, employeedata);
				
				gprs.callPdfChartData(strReporttypeName,strEmpName, employeedata);
				bc.callPdfChartData(strEmpName, employeedata);
				wfr.callCycle(alInnerChart1,alInnerChart2,alInnerChart3,strUserType);
				
				gprs.callCycle(alInnerChart2, strUserType);
				bc.callCycle(alInnerChart1);
				employeedata.clear();
				
				if(j<alId.size()-1) {
					sbActualHours.append(",");
				}
				
			}
			
			request.setAttribute("alPayCyclesChart", alInnerChart);
			request.setAttribute("sbActualHours", sbActualHours);
			request.setAttribute("sbActualPC", sbActualPC.toString());
			
			log.debug("==========================charts=============================");
			log.debug("sbActualHours===>>"+sbActualHours);
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

				strCurrentPayCycleD1 = uF.getDateFormatUtil(dt1, DATE_FORMAT);
				strCurrentPayCycleD2 = uF.getDateFormatUtil(dt2, DATE_FORMAT);

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
					pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));

				}else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
					pst = con.prepareStatement(selectClockEntriesR3_EManager);
					pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
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
			
/*
			request.setAttribute("hmListpdf", hm);
			request.setAttribute("alPayCyclespdf", alInner);
			request.setAttribute("alServiceIdpdf", alServiceId);
			request.setAttribute("alServiceNamepdf", new CommonFunctions().getServicesMap(true));
			*/
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
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

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

}
