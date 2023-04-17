package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.export.GeneratePdfReports;
import com.konnect.jpms.charts.*;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.Employee;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeAmountPaidReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strEmpId = null;
	CommonFunctions CF = null; 
	
	private String strEmpID;
	private String strP;
	private String param;
	
	private String f_strWLocation;
	private String f_department;
	private String f_level;
		
	private List<FillWLocation> wLocationList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	
	public static String strDetails;

	ArrayList<String> employeedata=new ArrayList<String>();
	ArrayList<String> employeedatapdf =new ArrayList<String>();
	private static Logger log = Logger.getLogger(EmployeeAmountPaidReport.class);
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		request.setAttribute(TITLE, TViewConsolitdatedCompensationReport);

		strUserType = (String) session.getAttribute(USERTYPE);
		strEmpId = (String) session.getAttribute(EMPID);
		
		strEmpID = (String) request.getParameter("EMPID");
		strP = (String) request.getParameter("param");
		

		request.setAttribute(PAGE, PReportEmployeeAmountPaid);
		
		
		wLocationList = new FillWLocation(request).fillWLocation();
		departmentList = new FillDepartment(request).fillDepartment();
		levelList = new FillLevel(request).fillLevel();
		
		if(getParam()==null){
			setParam("APE");
			strP = getParam();
		}
		

		if(strP!=null && (strP.equalsIgnoreCase("APWL") 
				|| strP.equalsIgnoreCase("APD") 
				|| strP.equalsIgnoreCase("UTH")
				|| strP.equalsIgnoreCase("APS")
				|| strP.equalsIgnoreCase("APE")
				|| strP.equalsIgnoreCase("APUT"))){
			
			viewPayCycle(strP);
			return LOAD;
			
		}else{
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		
		

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
		Map hmWorkLocation = null;
		Map hmDepartment = null;
		Map hmUserType = null;
		Map hmServices = null;
		Map hmEmpName = null;
		
		
//		System.out.println("strUserType===>"+strUserType);
		
		
		
		try {

			String startDate = null;
			String strDisplayPaycycle = null;
			String strPaycycleDuration = null;
			
			con = db.makeConnection(con);
			
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			
			
			if(strP!=null && strP.equalsIgnoreCase("APWL")){
				hmWorkLocation = CF.getWLocationMap(con,strUserType, request, strEmpId);
				
				strUserType="By WorkLocation";
			}else if(strP!=null && strP.equalsIgnoreCase("APD")){
				hmDepartment = CF.getDepartmentMap(con,strUserType,  strEmpId);
				strUserType="By Department";
			}else if(strP!=null && strP.equalsIgnoreCase("APUT")){
				hmUserType = CF.getUserTypeMap(con);
				strUserType="By UserType";
			}else if(strP!=null && strP.equalsIgnoreCase("APS")){
				hmServices = CF.getServicesMap(con, true);
				strUserType="By Services";
			}else if(strP!=null && strP.equalsIgnoreCase("APE")){
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

			List<String> alPayCycles = new ArrayList<String>();
			List<String> alInnerChart = new ArrayList<String>();
			List<String> alInnerChart1 = new ArrayList<String>();
			List<String> alInnerChart2 =new  ArrayList<String>();
			List<String> alInnerChart3 =new  ArrayList<String>();
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

				log.debug("dt1="+dt1);
				log.debug("dt1="+dt1);
				log.debug("strCurrentPayCycleD1="+strCurrentPayCycleD1);
				log.debug("strCurrentPayCycleD2="+strCurrentPayCycleD2);
				log.debug("strCurrentPayCycleD1="+strCurrentPayCycleD1);
				log.debug("strCurrentDate="+strCurrentDate);
				
				
				if (strCurrentDate.equals(strCurrentPayCycleD1) || strCurrentDate.equals(strCurrentPayCycleD2) || (strCurrentDate.after(strCurrentPayCycleD1) && strCurrentDate.before(strCurrentPayCycleD2))) {
					alPayCycles.add(sb.toString());
					alInnerChart.add("Pay Cycle "+nPayCycle+"<br>"+dt1 + "-" + dt2 );
					alInnerChart1.add("Pay Cycle "+nPayCycle);
					alInnerChart2.add("Pay Cycle "+nPayCycle+" "+dt1+" "+dt2);
					alInnerChart3.add(dt1+"-"+dt2);
					
					alInnerDates.add(dt1);
					alInnerDates.add(dt2);
					
					log.debug("== 11  =="+dt1);
					
				} else if (strCurrentDate.before(strCurrentPayCycleD2)) {
//					alInner.add(sb.toString());
//					alInnerDates.add(dt1);
//					alInnerDates.add(dt2);
				} else {
					alPayCycles.add(sb.toString());
					alInnerChart.add("Pay Cycle "+nPayCycle+"<br>"+dt1 + "-" + dt2 );
					alInnerChart1.add("Pay Cycle "+nPayCycle);
					alInnerChart2.add("Pay Cycle "+nPayCycle+" "+dt1+" "+dt2);
					alInnerChart3.add(dt1+"-"+dt2);
					
					alInnerDates.add(dt1);
					alInnerDates.add(dt2);
					
					log.debug("== 22  =="+dt1);
				}

				cal.add(Calendar.DAY_OF_MONTH, 1);

				if(nPayCycle>=maxCycle){
					break;
				}

			}

			Map<String, Map<String, String>> hmAmountPaid = new HashMap<String, Map<String, String>>();
			//Map<String, String> hmAmountPaidInner = new HashMap<String, String>();
			Map<String, String> hmAmountPaidInner = null;
			List<Employee> alId = new ArrayList<Employee>();
			List<String> alIdTemp = new ArrayList<String>();
			int x = -1;
			for (int i = 0; i < alInnerDates.size();) {
				x++;
				
				String strD1 = (String) alInnerDates.get(i++);
				String strD2 = (String) alInnerDates.get(i++);
				
				if(strP!=null && strP.equalsIgnoreCase("APWL")){
					pst = con.prepareStatement(selectPayrollWLocationId); 
				}else if(strP!=null && strP.equalsIgnoreCase("APE")){
					pst = con.prepareStatement(selectPayrollEmpId); 
				}else if(strP!=null && strP.equalsIgnoreCase("APD")){
					pst = con.prepareStatement(selectPayrollDeptId);
				}else if(strP!=null && strP.equalsIgnoreCase("APS")){
					pst = con.prepareStatement(selectPayrollServiceId);
				}else if(strP!=null && strP.equalsIgnoreCase("APUT")){
					pst = con.prepareStatement(selectPayrollUserTypeId);
				}
					
				
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//                System.out.println("pst=======>"+pst);
				rs = pst.executeQuery();
				String strOldId = null;
				String strNewId = null;
				String strName = null;
				hmAmountPaidInner = new HashMap<String, String>();
				while (rs.next()) {

					if(rs.getString("earning_deduction")==null || rs.getString("earning_deduction").trim().equals("")){
						continue;
					}
					
					String strPayMode = rs.getString("pay_mode");
					if(strP!=null && strP.equalsIgnoreCase("APWL")){
						strNewId = rs.getString("wlocation_id");
						strName = (String)hmWorkLocation.get(strNewId);
					}else if(strP!=null && strP.equalsIgnoreCase("APD")){
						strNewId = rs.getString("depart_id");
						strName = (String)hmDepartment.get(strNewId);
					}else if(strP!=null && strP.equalsIgnoreCase("APUT")){
						strNewId = rs.getString("usertype_id");
						strName = (String)hmUserType.get(strNewId);
					}else if(strP!=null && strP.equalsIgnoreCase("APS")){
						strNewId = rs.getString("servid");
						strName = (String)hmServices.get(strNewId);
						
					}else if(strP!=null && strP.equalsIgnoreCase("APE")){
						strNewId = rs.getString("emp_id");
						strName = rs.getString("emp_fname")+" "+rs.getString("emp_lname");
					}
					
					
					hmAmountPaidInner = hmAmountPaid.get(strNewId);

					log.debug("strNewId==>"+strNewId);
					
					if (strNewId != null && !strNewId.equalsIgnoreCase(strOldId)) {
						hmAmountPaidInner = new HashMap<String, String>();
					}else if(strNewId == null){
						hmAmountPaidInner = new HashMap<String, String>();
					}

					double dblAmount = 0,dblTotAmount = 0, dblTotalTime = 0;
					dblAmount = uF.parseToDouble((String) hmAmountPaidInner.get(x + "P"));
					double dblRowAmount = rs.getDouble("amount");
					Map<String, String> hmCurrency = hmCurrencyDetails.get(rs.getString("currency_id"));
					if(hmCurrency==null)hmCurrency=new HashMap();
					double dblConversion = uF.parseToDouble(hmCurrency.get("CURR_CONVERSION_USD"));
					if(dblConversion>0){
						dblRowAmount = dblRowAmount / dblConversion;
					}
					
					if(rs.getString("earning_deduction").equalsIgnoreCase("E")){
						dblTotAmount = dblAmount + dblRowAmount;
					}else{
						dblTotAmount = dblAmount - dblRowAmount;
					}
					
					dblTotalTime = uF.parseToDouble((String) hmAmountPaidInner.get(x + "H"));

					
					
					/*System.out.println("Emp Id ==>"+strNewId);
					System.out.println("Paid amount ==>"+dblAmount);
					System.out.println("Total amount ==>"+dblTotAmount);
					System.out.println("New amount ==>"+rs.getDouble("amount"));
					
					System.out.println("");*/
					
					if(strPayMode!=null && strPayMode.equalsIgnoreCase("X")){
						hmAmountPaidInner.put(x + "P", (dblTotAmount) + "");
					}else{
						hmAmountPaidInner.put(x + "P", (dblAmount + rs.getDouble("amount")) + "");
					}
					
//					hmAmountPaidInner.put(x + "H", (dblAmount + rs.getDouble("total_time")) + "");
//					hmAmountPaidInner.put(x + "L", "<a href=\"ClockEntries.action?T=T&PAY=Y&EMPID=" + strNewId + "&PC=" + x + "&D1=" + strD1 + "&D2=" + strD2 + "\">" + uF.formatIntoTwoDecimal(dblTotAmount) + "</a>");
					hmAmountPaidInner.put(x + "L", uF.formatIntoTwoDecimal(dblTotAmount));
					hmAmountPaidInner.put("D1",strD1);
					hmAmountPaidInner.put("D2",strD2);
					hmAmountPaidInner.put("PC",x+"");

					hmAmountPaid.put(strNewId, hmAmountPaidInner);

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

			
			log.debug("hmAmountPaid===========>"+hmAmountPaid);
			
//			System.out.println("hmAmountPaid===========>"+hmAmountPaid);
			
			Map<String, String> hmAmountPaidC = new HashMap<String, String>();
			
			Set setAmountPaid = hmAmountPaid.keySet();
			Iterator it = setAmountPaid.iterator();
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				Map hmAmountPaidTemp = (Map)hmAmountPaid.get(strEmpId);
				
				Set sTemp = hmAmountPaidTemp.keySet();
				Iterator itTemp = sTemp.iterator();
				double income = 0;
				double totalHrs = 0;
				double amountPaid = 0;
				double allowance = 0;
				String strD1= null;
				String strD2= null;
				String strPC= null;
				
				while(itTemp.hasNext()){
					String key = (String)itTemp.next();
					
					if(key!=null && key.indexOf("H")>0){
						totalHrs += uF.parseToDouble((String)hmAmountPaidTemp.get(key));
					}
					if(key!=null && key.indexOf("P")>0){
						income += uF.parseToDouble((String)hmAmountPaidTemp.get(key));
					}
					if(key!=null && key.indexOf("D1")>=0){
						strD1 = (String)hmAmountPaidTemp.get(key);
					}
					if(key!=null && key.indexOf("D2")>=0){
						strD2 = (String)hmAmountPaidTemp.get(key);
					}
					if(key!=null && key.indexOf("PC")>=0){
						strPC = (String)hmAmountPaidTemp.get(key);
					}
					
				} 
				
//				allowance = CF.getAllowanceValue(totalHrs, uF.parseToInt(strEmpId)) * income / 100;
//				amountPaid =  income + allowance  - (CF.getDeductionAmountMap(income));
				
//				log.debug("totalHrs=" + totalHrs);
//				log.debug("income=" + income);
//				log.debug("allowance=" + allowance);
//				log.debug("deduction=" + CF.getDeductionAmountMap(income));
				hmAmountPaidC.put(strEmpId+"PCCHART"+strPC, uF.formatIntoTwoDecimalWithOutComma(income));
//				hmAmountPaidC.put(strEmpId+"PC"+strPC, "<a href=\"ClockEntries.action?T=T&PAY=Y&EMPID=" + strEmpId + "&PC=" +strPC + "&D1=" + strD1 + "&D2=" + strD2 + "\">" + uF.formatIntoTwoDecimal(income) + "</a>");
				hmAmountPaidC.put(strEmpId+"PC"+strPC, uF.formatIntoTwoDecimal(income));
			}
			
			
			
			pst=con.prepareStatement(selectEmployeeR4);
			rs = pst.executeQuery();
			List<String> alEmpIdN = new ArrayList<String>();
			while(rs.next()){
				
				if(alIdTemp!=null && alIdTemp.contains(rs.getString("emp_per_id"))){
					alEmpIdN.add(rs.getString("emp_per_id"));
				}
				
			}
			rs.close();
			pst.close();
			
//			log.debug("hm=" + hmAmountPaid);
//			log.debug("hm C=" + hmAmountPaidC);

			
			
			
			List alReport = new ArrayList();
			List<String> alInner = new ArrayList<String>();
			
			for (int j = 0; j < alId.size(); j++) {
				String strCol = ((j%2==0)?"dark":"light");
				alInner = new ArrayList<String>();
				Employee objEmp = (Employee) alId.get(j);
				String strEmpId = (String) objEmp.getStrEmpId();
				String strEmpName = (String) objEmp.getStrName();
				
				alInner.add(strEmpName);
				for (int i = 0; i < alPayCycles.size() ; i++) {
					alInner.add(uF.showData((String) hmAmountPaidC.get(strEmpId+"PC"+i), ""+0));
				}
				
				alReport.add(alInner);
			}
			
			
			
			
			
			request.setAttribute("alReport", alReport);
			request.setAttribute("hmAmountPaid", hmAmountPaid);
			request.setAttribute("hmAmountPaidC", hmAmountPaidC);
			request.setAttribute("alId", alId);
			request.setAttribute("alPayCycles", alPayCycles);
			
			
			gprs.clearList();
			bc.clearList();
			//Charts ==>
			
			StringBuilder sbHours = new StringBuilder();
			StringBuilder sbPC = new StringBuilder();
			
			for (int i = alInnerChart.size()-1 ; i>=18; i--) {
				
				sbPC.append("'"+alInnerChart.get(i)+"'");
				
				log.debug(alInnerChart.get(i)+"===================>>>>>");
				
				
				if(i>18) 
					sbPC.append(",");
			}
			
			for (int j = 0; j < alId.size(); j++) {
				
				Employee objEmp = (Employee) alId.get(j);
				String strEmpId = (String) objEmp.getStrEmpId();
				String strEmpName = (String) objEmp.getStrName();
				
				
				
				ArrayList<String> data = new ArrayList<String>();
			    ArrayList<String> data2 = new ArrayList<String>();

			    
			    for (int k = alInnerChart.size()-1 ; k>=alInnerChart.size()-6; k--) {
					
								
			    	employeedatapdf.add(uF.showData((String) hmAmountPaidC.get(strEmpId+"PCCHART"+k), 0 + ""));
			    	
				}
			    
			//   gprs.call2(strEmpName,data);
			   

			   for (int k = alInnerChart.size()-1 ; k>=-1; k--) {
					
					
			    	data2.add(uF.showData((String) hmAmountPaidC.get(strEmpId+"PCCHART"+k), 0 + ""));
					  	
				}

         
               
               data2.clear();
			   
			   
			   data.clear();
			   
				
				
				
				sbHours.append(
						
						"{"+
						"name: '"+strEmpName+"',"+
						"data: ["
						
						);
				

				for (int i = alInnerChart.size()-1 ; i>=alInnerChart.size()-6; i--) {
					
						sbHours.append(
								
								uF.showData((String) hmAmountPaidC.get(strEmpId+"PCCHART"+i), 0 + "")
					         
					    );
						
						strDetails=(String) hmAmountPaidC.get(i+"");
						employeedata.add(strDetails);
						
						
						if (i>alInnerChart.size()-6) {
							
							sbHours.append(",");
						}
						
						if (i==alInnerChart.size()-6) {
							
							sbHours.append("]," +
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
						
						
						String strReporttypeName="Amount paid";
						gprs.callPdfChartData(strReporttypeName,strEmpName, employeedata);
						bc.callPdfChartData(strEmpName, employeedata);
				}
				
				

				
				
				//wfr.callPdfChartData(strReporttypeName,strEmpName, employeedata);
				
			/*	gprs.callPdfChartData(strReporttypeName,strEmpName, employeedata);
				bc.callPdfChartData(strEmpName, employeedata);*/
			
				gprs.callCycle(alInnerChart2, strUserType);
				bc.callCycle(alInnerChart1);
				employeedata.clear();
				
				
				
				if(j<alId.size()-1) {
					sbHours.append(",");
				}
				
			}
			
			request.setAttribute("alPayCyclesChart", alInnerChart);
			request.setAttribute("sbHours", sbHours);
			request.setAttribute("sbPC", sbPC.toString());
			
			log.debug("==========================charts=============================");
			log.debug("sbHours===>>"+sbHours);
			log.debug("sbPC==>"+sbPC);
			
		//	bcd.callCycle(alInnerChart1,alInnerChart2,alInnerChart3,strP);	

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


	public String getParam() {
		return param;
	}


	public void setParam(String param) {
		this.param = param;
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


	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

}
