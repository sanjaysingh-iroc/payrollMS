package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.payroll.reports.DepartmentwiseReport;
import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ClientReimbursementReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId;
	String strUserType; 
	       
	CommonFunctions CF = null; 
	 
	private static Logger log = Logger.getLogger(DepartmentwiseReport.class);
	
	String f_org;
	String[] f_strWLocation; 
	String[] f_department;
	String[] f_service;
	String[] f_level;
//	String[] f_project_service;
	String[] f_client;
	
	String selectOne;
	String strStartDate;
	String strEndDate;
	String financialYear;
	String monthFinancialYear;
	String paycycle;
	String strMonth;
	
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
//	List<FillServices> projectServiceList;
	List<FillLevel> levelList;
	List<FillFinancialYears> financialYearList;
	List<FillMonth> monthList;
	List<FillPayCycles> paycycleList; 
	
	List<FillClients> clientList;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			session = request.getSession();
			request.setAttribute(PAGE, "/jsp/task/ClientReimbursementReport.jsp");
			request.setAttribute(TITLE, "Client Reimbursement Report");
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if(CF==null)return LOGIN;
			
			strEmpId =(String) session.getAttribute(EMPID);
			strUserType =(String) session.getAttribute(BASEUSERTYPE);
			
			boolean isView  = CF.getAccess(session, request, uF);
			if(!isView){
				request.setAttribute(PAGE, PAccessDenied);
				request.setAttribute(TITLE, TAccessDenied);
				return ACCESS_DENIED;
			}
			
			if(getF_org()==null){
				setF_org((String)session.getAttribute(ORGID));
			}
			
			viewClientReimbursementReport(uF);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return loadPaySlips(uF);

	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++){
				if(getF_org().equals(organisationList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=organisationList.get(i).getOrgName();
					} else {
						strOrg+=", "+organisationList.get(i).getOrgName();
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
				hmFilter.put("SERVICE", "All Services");
			}
		} else {
			hmFilter.put("SERVICE", "All Services");
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
				hmFilter.put("LEVEL", "All Levels");
			}
		} else {
			hmFilter.put("LEVEL", "All Levels");
		}
		
		alFilter.add("CLIENT");
		if(getF_client()!=null) {
			String strClient="";
			int k=0;
			for(int i=0;clientList!=null && i<clientList.size();i++) {
				for(int j=0;j<getF_client().length;j++) {
					if(getF_client()[j].equals(clientList.get(i).getClientId())) {
						if(k==0) {
							strClient=clientList.get(i).getClientName();
						} else {
							strClient+=", "+clientList.get(i).getClientName();
						}
						k++;
					}
				}
			}
			if(strClient!=null && !strClient.equals("")) {
				hmFilter.put("CLIENT", strClient);
			} else {
				hmFilter.put("CLIENT", "All Clients");
			}
		} else {
			hmFilter.put("CLIENT", "All Clients");
		}
		
		
		
		/*if(getSelectOne()!= null && !getSelectOne().equals("")) {
			alFilter.add("PERIOD");
			
			String strSelectOne="";
			if(uF.parseToInt(getSelectOne()) == 1) {
				strSelectOne="From - To";
			} else if(uF.parseToInt(getSelectOne()) == 2) {
				strSelectOne="Financial Year";
			} else if(uF.parseToInt(getSelectOne()) == 3) {
				strSelectOne="Month";
			} else if(uF.parseToInt(getSelectOne()) == 4) {
				strSelectOne="Paycycle";
			}
			if(strSelectOne!=null && !strSelectOne.equals("")) {
				hmFilter.put("PERIOD", strSelectOne);
			}
			
		}
		
		if(uF.parseToInt(getSelectOne()) == 1) {
			alFilter.add("FROMTO");
			String strtDate = "";
			String endDate = "";
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("") && !getStrStartDate().equals("")) {
				strtDate = uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat());
			}
			if(getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("") && !getStrEndDate().equals("")) {
				endDate = uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat());
			}
			hmFilter.put("FROMTO", strtDate +" - "+ endDate);
		} else if(uF.parseToInt(getSelectOne()) == 2) {
			alFilter.add("FINANCIALYEAR");
			String[] strFinancialYears = null;
			if (getFinancialYear() != null) {
				strFinancialYears = getFinancialYear().split("-");
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				
				setStrStartDate(strFinancialYears[0]);
				setStrEndDate(strFinancialYears[1]);
			} else {
				strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				
				setStrStartDate(strFinancialYears[0]);
				setStrEndDate(strFinancialYears[1]);
			}
			hmFilter.put("FINANCIALYEAR", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		} else if(uF.parseToInt(getSelectOne()) == 3) {
			alFilter.add("MONTH");
			int nselectedMonth = uF.parseToInt(getStrMonth());
			String strMonth = uF.getMonth(nselectedMonth);
//			int nFYSMonth = uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "MM"));
			int nFYSMonth = 0;
			int nFYSDay = 0;
			String[] strFinancialYears = null;
//			System.out.println("getMonthFinancialYear() ===>> " + getMonthFinancialYear());
			if (getMonthFinancialYear() != null) {
				strFinancialYears = getMonthFinancialYear().split("-");
				setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
				nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
			} else {
				strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
				setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
				nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
			}
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
			cal.set(Calendar.DATE, nFYSDay);
			if(nselectedMonth>=nFYSMonth){
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "yyyy")));
			}else{
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, "yyyy")));
			}
			
			int nMonthStart = cal.getActualMinimum(Calendar.DATE);
			int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
			
			setStrStartDate(nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
			setStrEndDate(nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
			
			hmFilter.put("MONTH", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + strMonth);
		} else if(uF.parseToInt(getSelectOne()) == 4) {
			alFilter.add("PAYCYCLE");
			String strPaycycle = "";
			String[] strPayCycleDates = null;
			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
				
				strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
				setStrStartDate(strPayCycleDates[0]);
				setStrEndDate(strPayCycleDates[1]);
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
				
				strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
				setStrStartDate(strPayCycleDates[0]);
				setStrEndDate(strPayCycleDates[1]);
			}
			hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}*/
		
	//===start parvez date: 02-02-2022===	
		if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))){
			alFilter.add("FROMTO");
			hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}
	//===end parvez date: 02-02-2022===	
		
		String selectedFilter=CF.getSelectedFilter1(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	private void viewClientReimbursementReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			/*if(getSelectOne() != null && getSelectOne().equals("1") && (getStrStartDate() == null || getStrStartDate().equals("")) && (getStrEndDate() == null || getStrEndDate().equals(""))) {
				
				Date currDate=uF.getCurrentDate(CF.getStrTimeZone());
				
				String startdate="01/"+uF.getDateFormat(""+currDate, DBDATE, "MM")+"/"+uF.getDateFormat(""+currDate, DBDATE, "yyyy");
				
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "MM"))-1);
				calendar.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "yyyy")));
				calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));

				Date date = calendar.getTime();
				DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
				String endDate=DATE_FORMAT.format(date);
				
				setStrStartDate(startdate);
				setStrEndDate(endDate);
			} else if(getSelectOne() != null && getSelectOne().equals("2")) {
				String[] strFinancialYears = null;
				if (getFinancialYear() != null) {
					strFinancialYears = getFinancialYear().split("-");
					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					
					setStrStartDate(strFinancialYears[0]);
					setStrEndDate(strFinancialYears[1]);
				} else {
					strFinancialYears = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					
					setStrStartDate(strFinancialYears[0]);
					setStrEndDate(strFinancialYears[1]);
				}
			} else if(getSelectOne() != null && getSelectOne().equals("3")) {
				
				int nselectedMonth = uF.parseToInt(getStrMonth());
//				int nFYSMonth = uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "MM"));
				int nFYSMonth = 0;
				int nFYSDay = 0;
				String[] strFinancialYears = null;
//				System.out.println("getMonthFinancialYear() ===>> " + getMonthFinancialYear());
				if (getMonthFinancialYear() != null) {
					strFinancialYears = getMonthFinancialYear().split("-");
					setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
					nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
				} else {
					strFinancialYears = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
					setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
					nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
				}
				
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
				cal.set(Calendar.DATE, nFYSDay);
				if(nselectedMonth>=nFYSMonth){
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "yyyy")));
				}else{
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, "yyyy")));
				}
				
				int nMonthStart = cal.getActualMinimum(Calendar.DATE);
				int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
				
				setStrStartDate(nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
				setStrEndDate(nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
				
			} else if(getSelectOne() != null && getSelectOne().equals("4")) {
				String[] strPayCycleDates = null;
				if (getPaycycle() != null) {
					strPayCycleDates = getPaycycle().split("-");
					setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
					
					setStrStartDate(strPayCycleDates[0]);
					setStrEndDate(strPayCycleDates[1]);
				} else {
					strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
					setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
					
					setStrStartDate(strPayCycleDates[0]);
					setStrEndDate(strPayCycleDates[1]);
				}
			}*/
	
	//===start parvez date: 02-02-2022===		
			if(getStrStartDate()!=null && getStrStartDate().equalsIgnoreCase("NULL")) {
				setStrStartDate(null);
				setStrEndDate(null);
			}		
			if(getStrEndDate()!=null && getStrEndDate().equalsIgnoreCase("NULL")) {
				setStrStartDate(null);
				setStrEndDate(null);
			}
			if(getStrStartDate()==null && getStrEndDate()==null) {
				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
				int nMaxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				int nMinDate = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
				
				setStrStartDate(uF.getDateFormat(nMinDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
				setStrEndDate(uF.getDateFormat(nMaxDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
			}
	//===end parvez date: 02-02-2022===		

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_reimbursement er, employee_official_details eod where er.emp_id > 0 and ispaid = true and er.emp_id = eod.emp_id ");
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				sbQuery.append(" and eod.supervisor_emp_id = "+ uF.parseToInt(strEmpId) +" ");
			}
			if(getF_client() != null && getF_client().length>0) {
				sbQuery.append(" and er.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
			if(getF_level()!=null && getF_level().length>0) {
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
			
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and to_date(er.entry_date::text, 'YYYY-MM-DD') between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}
			
			sbQuery.append(" order by reimbursement_id desc"); //, er.emp_id 
			
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("CRR/522--pst ===>> " + pst);
			rs = pst.executeQuery();

			List<List<String>> reportList = new ArrayList<List<String>>();
			while (rs.next()) {
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(CF.getEmpCodeByEmpId(con, rs.getString("emp_id")));	//0
				alInner.add(CF.getEmpNameMapByEmpId(con, rs.getString("emp_id")));	//1
				alInner.add(uF.showData(CF.getClientNameById(con, rs.getString("client_id")), "-"));	//2
				
				alInner.add(uF.showData(rs.getString("reimbursement_info"), "-"));	//3
				if(rs.getString("reimbursement_info") != null && (rs.getString("reimbursement_info").equals("Travel") || rs.getString("reimbursement_info").equals("Conveyance Bill"))) {
					alInner.add(uF.showData(rs.getString("travel_mode"), ""));	//4
				} else {
					alInner.add("-");	//4
				}
				alInner.add(uF.showData(rs.getString("no_person"), "-"));	//5
				if(rs.getBoolean("is_billable")) {
					alInner.add("Yes");		//6
				} else {
					alInner.add("No");		//6
				}
				
				if(rs.getString("reimbursement_info") != null && (rs.getString("reimbursement_info").equals("Travel") || rs.getString("reimbursement_info").equals("Conveyance Bill"))) {
					alInner.add(uF.showData(rs.getString("travel_from"),"-"));		//7
					alInner.add(uF.showData(rs.getString("travel_to"), "-"));		//8
					alInner.add(uF.showData(rs.getString("no_days"), "-"));			//9	
					alInner.add(uF.showData(rs.getString("travel_distance"), "-"));		//10
					alInner.add(uF.showData(rs.getString("travel_rate"), "-"));			//11
				} else {
					alInner.add("-");		//7
					alInner.add("-");		//8
					alInner.add("-");		//9
					alInner.add("-");		//10
					alInner.add("-");		//11
				}
				alInner.add(uF.formatIntoComma(uF.parseToDouble(rs.getString("reimbursement_amount"))));		//12
				
		//===start parvez date: 26-01-2022===
				alInner.add(CF.getEmpDesigMapByEmpId(con, rs.getString("emp_id")));		//13	
				alInner.add(rs.getString("paid_date"));									//14
				alInner.add(uF.showData(CF.getProjectNameById(con, rs.getString("pro_id")),"-"));	//15
				
		//===end parvez date: 26-01-2022===		

				reportList.add(alInner);
			}
			rs.close();
			pst.close();

//			System.out.println("reportList ===>> " + reportList);
			
			request.setAttribute("reportList", reportList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

}


//	private void getExcelFirstHeaderList(List<DataStyle> alInnerExport) {
//		
//		alInnerExport.add(new DataStyle("Client Reimbursement Report", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0", BaseColor.LIGHT_GRAY));
//	   	
//	   	alInnerExport.add(new DataStyle("Employee Code", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0", BaseColor.LIGHT_GRAY));
//	   	alInnerExport.add(new DataStyle("Employee Name", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0", BaseColor.LIGHT_GRAY));
//	   	alInnerExport.add(new DataStyle("Client", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0", BaseColor.LIGHT_GRAY));
//	   	alInnerExport.add(new DataStyle("Reimbursement Type", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0", BaseColor.LIGHT_GRAY));
//	   	alInnerExport.add(new DataStyle("if Travel- mode of Travel", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0", BaseColor.LIGHT_GRAY));
//	   	alInnerExport.add(new DataStyle("No. of Persons", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0", BaseColor.LIGHT_GRAY));
//	   	alInnerExport.add(new DataStyle("Chargeable Y/N", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0", BaseColor.LIGHT_GRAY));
//	   	alInnerExport.add(new DataStyle("Place From", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0", BaseColor.LIGHT_GRAY));
//	   	alInnerExport.add(new DataStyle("Place To", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0", BaseColor.LIGHT_GRAY));
//	   	alInnerExport.add(new DataStyle("Days", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0", BaseColor.LIGHT_GRAY));
//	   	alInnerExport.add(new DataStyle("Km/ Day", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0", BaseColor.LIGHT_GRAY));
//	   	alInnerExport.add(new DataStyle("Rate/ Km", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0", BaseColor.LIGHT_GRAY));
//	   	alInnerExport.add(new DataStyle("Amount", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0", BaseColor.LIGHT_GRAY));
//	   	
//	}


	public String loadPaySlips(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillMonth();
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		clientList = new FillClients(request).fillAllClients(false);
		getSelectedFilter(uF);
		
		return LOAD;
	}
	

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
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

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
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

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String getSelectOne() {
		return selectOne;
	}

	public void setSelectOne(String selectOne) {
		this.selectOne = selectOne;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
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

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public String[] getF_client() {
		return f_client;
	}

	public void setF_client(String[] f_client) {
		this.f_client = f_client;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}

	public String getMonthFinancialYear() {
		return monthFinancialYear;
	}

	public void setMonthFinancialYear(String monthFinancialYear) {
		this.monthFinancialYear = monthFinancialYear;
	}

}