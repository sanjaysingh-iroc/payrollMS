package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.charts.SemiCircleMeter;
import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillResourceType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MyProjectDashboard extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType;
	String strSessionEmpId;
	String strSessionOrgId;
	CommonFunctions CF; 

	boolean poFlag;
	boolean tlFlag;
	
	String taskWorking;
	String proWorking;
	String proBusinessSnapshot;
	String freqENDDATE;
	String qfreqENDDATE;
	
	public String execute() throws Exception {
 
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)return LOGIN;

		strSessionOrgId = (String)session.getAttribute(ORGID);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		request.setAttribute(PAGE, "/jsp/task/MyProjectDashboard.jsp");
		request.setAttribute(TITLE, "My Dashboard");
		
		request.setAttribute("arrEnabledModules", CF.getArrEnabledModules());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		request.setAttribute("IS_DEVICE_INTEGRATION", CF.getIsDeviceIntegration());
		UtilityFunctions uF = new UtilityFunctions();
		
		getMyProjectDetails(uF);
 
		return LOAD;
	}
	
	public void getMyProjectDetails(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		try {
			con = db.makeConnection(con);
			
//			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
//			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			getEmpProfileDetail(con, uF, null, strSessionEmpId);
			checkProjectOwner(con,uF);
			checkTeamLead(con,uF);
			if(isPoFlag() || isTlFlag()){
				getProjectPerformanceDetails(con,uF);
				getWeeklyWorkProgress(con, uF);
				getProjectWorkProgressDetails(con,uF);
			}
			getProjectTaskCount(con,uF);
			getTeamTaskDetails(con,uF);
			getRequestAndAlerts(con,uF);
			
			getResourceMoneyWorkKPI(con, uF);
			getResourceMoneyProjectKPI(con, uF);

			getResourceTimeWorkKPI(con, uF);
			getResourceTimeProjectKPI(con, uF);
			
			getProjectandBusinessDetails(con,uF);
			getTeamEffort(con,uF); 
			
			getMyTaskPerformanceDetails(con,uF);
			
			getMyCommitmentAmount(con, uF, poFlag);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
	
	
	public Map<String, String> getEmpProfileDetail(Connection con, UtilityFunctions uF, String strUserType, String strEmpIdReq) {

		PreparedStatement pst = null;
		ResultSet rs = null;

		Map<String, String> hmEmpProfile = new HashMap<String, String>();
		try {
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			request.setAttribute("hmEmpName", hmEmpName);
			
			Map<String, String> hmEmpResource = FillResourceType.getResourceData();
			if(hmEmpResource == null) hmEmpResource = new HashMap<String, String>();
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
//			pst = con.prepareStatement("Select * from ( Select * from ( Select * from employee_personal_details epd left join employee_official_details eod on epd.emp_per_id=eod.emp_id where epd.emp_per_id=?) ast left join state s on ast.emp_state_id = s.state_id ) aco left join country co on aco.emp_country_id = co.country_id");
			pst = con.prepareStatement(" select a.*, org_name from org_details od right join(select a.*,wlocation_name from work_location_info wl right join(" +
					"select a.*,dept_name,dept_code from department_info d right join(" +
					"select a.*,level_code,level_name from level_details ld right join(select a.*,dd.designation_id,designation_code,designation_name," +
					"level_id from designation_details dd right join(select * from grades_details gd right join(select a.*,country_name from " +
					"country co right join(select a.*,state_name from state s right join(select * from employee_personal_details epd, " +
					"employee_official_details eod where epd.emp_per_id= ? and epd.emp_per_id=eod.emp_id) a on a.emp_state_id = s.state_id" +
					") a on a.emp_country_id = co.country_id) a on a.grade_id=gd.grade_id) a on a.designation_id=dd.designation_id" +
					") a on a.level_id=ld.level_id) a on a.depart_id=d.dept_id) a on a.wlocation_id=wl.wlocation_id" +
					") a on a.org_id = od.org_id");
			pst.setInt(1, uF.parseToInt(strEmpIdReq));
			rs = pst.executeQuery();

			while (rs.next()) {
				
				if(strUserType!=null && (!strUserType.equalsIgnoreCase(EMPLOYEE) || !strUserType.equalsIgnoreCase(ARTICLE) || !strUserType.equalsIgnoreCase(CONSULTANT))){
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
	
					
					request.setAttribute(TITLE, rs.getString("emp_fname") +strEmpMName+ " "+ rs.getString("emp_lname")+"'s Profile");
				}
				hmEmpProfile.put("EMP_ID", rs.getString("emp_per_id"));
				hmEmpProfile.put("EMPCODE", rs.getString("empcode"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				hmEmpProfile.put("NAME", uF.showData(rs.getString("salutation"), "")+ " " +rs.getString("emp_fname") +strEmpMName+ " "+ rs.getString("emp_lname")); 
				
				
				hmEmpProfile.put("IMAGE", rs.getString("emp_image"));
				hmEmpProfile.put("EMAIL", rs.getString("emp_email"));
				hmEmpProfile.put("EMAIL_SEC", rs.getString("emp_email_sec"));
				hmEmpProfile.put("ORG_NAME", rs.getString("org_name"));
				
				if(rs.getString("emp_email_sec")!=null && rs.getString("emp_email_sec").indexOf("@")>0) {
					hmEmpProfile.put("EMP_EMAIL", rs.getString("emp_email_sec"));
				} else {
					hmEmpProfile.put("EMP_EMAIL", rs.getString("emp_email"));
				}
				
				hmEmpProfile.put("DESIGNATION_NAME", rs.getString("designation_name"));
				hmEmpProfile.put("LEVEL_NAME", rs.getString("level_name"));
				hmEmpProfile.put("GRADE_NAME", rs.getString("grade_name"));
				hmEmpProfile.put("GENDER", uF.getGender(rs.getString("emp_gender")));
				
				hmEmpProfile.put("DOB", uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, CF.getStrReportDateFormat()));
				
				hmEmpProfile.put("WLOCATION_NAME", rs.getString("wlocation_name"));
				hmEmpProfile.put("DEPARTMENT_NAME", rs.getString("dept_name"));

//				hmEmpProfile.put("SBU_NAME", rs.getString("service_name"));
				String joinDate = "";
				if(rs.getString("joining_date") != null) {
					joinDate = uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat());
				} 
				hmEmpProfile.put("JOINING_DATE", joinDate);
				
				hmEmpProfile.put("EMP_TYPE", uF.stringMapping(rs.getString("emptype")));
				hmEmpProfile.put("EMPLOYMENT_TYPE", rs.getString("emp_status"));

				hmEmpProfile.put("SUPERVISOR_NAME", hmEmpName.get(rs.getString("supervisor_emp_id")));
				hmEmpProfile.put("HOD_NAME", hmEmpName.get(rs.getString("hod_emp_id")));
				hmEmpProfile.put("HR_NAME", hmEmpName.get(rs.getString("emp_hr")));
				
				String strEmpProfile = "";
				if(uF.parseToInt(rs.getString("emprofile")) > 0 ){
					strEmpProfile = uF.showData(hmEmpResource.get(rs.getString("emprofile")), ""); 
				}
				hmEmpProfile.put("PROFILE", strEmpProfile);
				hmEmpProfile.put("PROFILE_ID", rs.getString("emprofile"));
				
				String strEmpContractor = "";
				if(uF.parseToInt(rs.getString("emp_contractor")) == 1){
					strEmpContractor = "Employee";
				} else if(uF.parseToInt(rs.getString("emp_contractor")) == 2){
					strEmpContractor = "Contractor";
				}
				hmEmpProfile.put("EMPLOYEE_CONTRACTOR", strEmpContractor);
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmEmpProfile", hmEmpProfile);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		return hmEmpProfile;
	}
	
	
	private void getMyCommitmentAmount(Connection con, UtilityFunctions uF, boolean poFlag) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			
			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;
			strCalendarYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			strCalendarYearStart = strCalendarYearDates[0];
			strCalendarYearEnd = strCalendarYearDates[1];
			int stFYear = uF.parseToInt(uF.getDateFormat(strCalendarYearStart+"", DATE_FORMAT, "yy"));
			int endFYear = uF.parseToInt(uF.getDateFormat(strCalendarYearEnd+"", DATE_FORMAT, "yy"));
			request.setAttribute("stFYear", stFYear+"");
			request.setAttribute("endFYear", endFYear+"");
			
//			System.out.println("strCalendarYearStart ===>> " + strCalendarYearStart + " -- strCalendarYearEnd ===>> " + strCalendarYearEnd);
			/**
			 * Bills & receipts
			 * */
			StringBuilder sbQuery = new StringBuilder();
			
//			System.out.println("hmReceipts =====> "+hmReceipts);
			
			
			
			Map<String, String> hmProjectData = new HashMap<String,String>();
			sbQuery = new StringBuilder();
			List<String> monthYearsList = new ArrayList<String>();
			List<String> alInner = new ArrayList<String>();
			sbQuery.append("select cd.client_id, cd.client_name, pmt.pro_id, pmt.pro_name, pmt.org_id, pmt.wlocation_id, pmt.department_id, pmt.start_date," +
				" pmt.deadline, billing_kind, billing_cycle_day, billing_cycle_weekday, billing_amount from client_details cd, projectmntnc pmt " +
				" where cd.client_id = pmt.client_id ");
		//===start parvez date: 17-10-2022===	
//			sbQuery.append(" and pmt.project_owner="+uF.parseToInt(strSessionEmpId));
			sbQuery.append(" and pmt.project_owners like '%,"+strSessionEmpId+",%' ");
		//===end parvez date: 17-10-2022===	
			
			sbQuery.append(" and ((start_date <= '" + uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "') ");
			sbQuery.append(" or (start_date >= '" + uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "' and start_date <= '"+ uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, DBDATE) + "') ");
			sbQuery.append(" or (start_date >= '" + uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, DBDATE) + "') ");
			sbQuery.append(" or (start_date <= '" + uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, DBDATE) + "') ");
			sbQuery.append(" or (deadline >= '" + uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, DBDATE) + "')) ");
	        
//			sbQuery.append(" order by pro_id limit 5");
			pst = con.prepareStatement(sbQuery.toString());
//		        System.out.println("pst =====>> "  +pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				
				alInner.add(rs.getString("pro_id"));
				hmProjectData.put(rs.getString("pro_id")+"_START_DATE", rs.getString("start_date"));
				hmProjectData.put(rs.getString("pro_id")+"_DEADLINE", rs.getString("deadline"));
				
				hmProjectData.put(rs.getString("pro_id")+"_BILL_FREQ", rs.getString("billing_kind"));
				hmProjectData.put(rs.getString("pro_id")+"_FREQ_DAY", rs.getString("billing_cycle_day"));
				hmProjectData.put(rs.getString("pro_id")+"_WEEKDAY", rs.getString("billing_cycle_weekday"));
				hmProjectData.put(rs.getString("pro_id")+"_BILLING_AMT", rs.getString("billing_amount"));
			}
			rs.close();
			pst.close();
				
//			System.out.println("alInner ===>> " + alInner);
			
				int startDay = 0;
				int endDay = 0;
				int startMonth = 0;
				int endMonth = 0;
				int startYear = 0;
				int endYear = 0;
				int start_month = 0;
				int start_year = 0;
				Date startDate = uF.getDateFormat(strCalendarYearStart, DATE_FORMAT);
				Date endDate1 = uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT);
//				System.out.println("start date ===>> "+startDate+" --- end date ===>> "+endDate);
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				startDay = cal.get(Calendar.DATE);
			    start_month = cal.get(Calendar.MONTH)+1;
			    startMonth = start_month;
			    
			    start_year = cal.get(Calendar.YEAR);
			    startYear= start_year;
			    
			    Calendar cal2 = Calendar.getInstance();
				cal2.setTime(endDate1);
				endDay = cal2.get(Calendar.DATE);
				endMonth = cal2.get(Calendar.MONTH)+1;
				endYear = cal2.get(Calendar.YEAR);
			
				long monthDiff = uF.getMonthsDifference(startDate, endDate1);
			
//				System.out.println("monthDiff ===>> " + monthDiff);
//				System.out.println("endYear ===>> " + endYear);
				
			    while(monthDiff > 0) {
//			    	System.out.println("monthDiff ===>> " + monthDiff);
			    	
					monthYearsList.add(String.valueOf(startMonth)+"/"+String.valueOf(startYear));
					if(startMonth == 12 && startYear == endYear) {
						break;
					}
					startMonth++;
//					System.out.println("startMonth ===>> " + startMonth);
					if(startMonth > 12) {
						startMonth = 1;
						startYear++;
					} else if(startMonth > endMonth && startYear == endYear) {
						break;
					} 
				}
			    
			    Map<String, String> hmCommitAmt = new LinkedHashMap<String, String>();
				Iterator<String> itr = monthYearsList.iterator();
				double totalCommitmentAmt = 0;
				while(itr.hasNext()) {
					 
					double dblBillAmount = 0;
					String month = itr.next();
					
//					System.out.println("month==>"+month);
					String[] dateArr = month.split("/");
					String strFirstDate = null;
					String strEndDate = null;
					
					String strDate = "01/"+dateArr[0]+"/"+dateArr[1];
					String minMaxDate = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
					String[] tmpDate = minMaxDate.split("::::");
					strFirstDate = tmpDate[0];
					strEndDate = tmpDate[1];
			
//					System.out.println("strFirstDate==>"+ strFirstDate +"strEndDate==>"+ strEndDate);
//							String newStartDate = strFirstDate;
//							System.out.println(proId + " --- newStartDate ===>> " + newStartDate);
					for(int i=0;i<alInner.size() && alInner!=null && !alInner.isEmpty();i++) {
						String proId = alInner.get(i);
						List<String> proList = new ArrayList<String>();
//					    double totalOutStandingAmt = 0;
					    int proStMnth = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, "MM")); 
						int proStYr = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, "yyyy"));
						
					    int proEndMnth = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, "MM")); 
						int proEndYr = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, "yyyy"));
						
						setQfreqENDDATE(null);
						
						int intMonths = uF.getMonthsDifference(uF.getDateFormat(strFirstDate, DATE_FORMAT), uF.getDateFormat(strEndDate, DATE_FORMAT));
	//							System.out.println(proId + " --- intMonths ===>> " + intMonths);
						int intCount = 1;
						if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("A")) {
							intCount = intMonths/12;
							intCount++;
						}else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("H")) {
							intCount = intMonths/6;
							intCount++;
						}else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("Q")) {
							intCount = intMonths/3;
							intCount++;
						} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("M")) {
							intCount = intMonths;
						} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("B")) {
							intCount = (intMonths * 2);
						} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("W")) {
							String strDays = uF.dateDifference(strFirstDate, DATE_FORMAT, strEndDate, DATE_FORMAT);
	//								System.out.println(proId + " --- strDays ===>> " + strDays);
							intCount = (uF.parseToInt(strDays) / 7);
							intCount++;
						} 
	//							System.out.println(proId + " --- intCount ===>> " + intCount);
						dblBillAmount = 0;
						setFreqENDDATE(null);
						
						Date proStDate = uF.getDateFormatUtil(hmProjectData.get(proId+"_START_DATE"), DBDATE);
						Date proEdDate = uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE);
						Date mnthStDate = uF.getDateFormatUtil(strFirstDate, DATE_FORMAT);
						Date mnthEdDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
						
						boolean flag = false;
						if(((proStDate.before(mnthStDate) || proStDate.equals(mnthStDate)) && (proEdDate.after(mnthEdDate) || proEdDate.equals(mnthEdDate))) ) {
							flag = true;
						}
						
						if(flag || (proStMnth == uF.parseToInt(dateArr[0]) && proStYr == uF.parseToInt(dateArr[1])) || (proEndMnth == uF.parseToInt(dateArr[0]) && proEndYr == uF.parseToInt(dateArr[1]))) {
							for(int j=0; j<intCount; j++) {
								String newStDate = getNewProjectStartDate(con, uF, proId, getFreqENDDATE());
							
								if(newStDate == null || newStDate.equals("")) {
										newStDate = strFirstDate;
								}
	//								System.out.println("newStDate ===>> " + newStDate);
								boolean frqFlag = false;
								String freqEndDate = strEndDate;
								
								if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O")) {
									freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
								}
								
								if(uF.parseToInt(hmProjectData.get(proId+"_FREQ_DAY")) > 0) {
									freqEndDate = hmProjectData.get(proId+"_FREQ_DAY") + "/" + uF.getDateFormat(newStDate, DATE_FORMAT, "MM")+ "/" +uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy");
									freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
									
									Date stDate = uF.getDateFormatUtil(newStDate, DATE_FORMAT);
									Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
									
									if(freqDate.after(stDate)) {
										frqFlag = true;
									}
									if(frqFlag) {
										if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("M")) {
											freqEndDate = freqEndDate;
										} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("B")) {
											freqEndDate = uF.getDateFormat(uF.getFutureDate(newStDate, 15)+"", DBDATE, DATE_FORMAT);
										}
									} else {
										if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("M")) {
											freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(freqEndDate, 1)+"", DBDATE, DATE_FORMAT);
	//												System.out.println("freqEndDate in else ===>> " + freqEndDate +" -- proId ===>> " + proId);
										} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("B")) {
											freqEndDate = uF.getDateFormat(uF.getFutureDate(strFirstDate, 15)+"", DBDATE, DATE_FORMAT);
										}
									}
								}
								
	//									System.out.println("freqEndDate 2 ===>> " + freqEndDate +" -- proId ===>> " + proId);
								if(hmProjectData.get(proId+"_WEEKDAY") != null && !hmProjectData.get(proId+"_WEEKDAY").equals("") && hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("W")) {
									freqEndDate = uF.getDateFormat(uF.getDateOfPassedDay(newStDate, hmProjectData.get(proId+"_WEEKDAY"))+"", DBDATE, DATE_FORMAT);
									freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
	//										System.out.println("freqEndDate -1 ===>>>>> " + freqEndDate);
								}
								if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("Q")) {
									Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
									if(getQfreqENDDATE() == null) {
										String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
										String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
										String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -3)+"", DBDATE, DATE_FORMAT);
										freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 2, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
										setQfreqENDDATE(freqEndDate);
//										System.out.println("Q -- freqEndDate ===>> " + freqEndDate);
									} else {
										freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 3)+"", DBDATE, DATE_FORMAT);
//										System.out.println("Q -- else freqEndDate ===>> " + freqEndDate );
									}
									 if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
										freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
									 }
									 if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
										setQfreqENDDATE(freqEndDate);
									 }
	//											System.out.println("freqEndDate in if ===>> " + freqEndDate +" -- proId ===>> " + proId);
								}
								if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("H")) {
									Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
									if(getQfreqENDDATE() == null) {
										String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
										String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
										String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -6)+"", DBDATE, DATE_FORMAT);
										freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 5, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
										setQfreqENDDATE(freqEndDate);
									} else {
										freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 6)+"", DBDATE, DATE_FORMAT);
									}
									if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
										freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
									}
									if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
										setQfreqENDDATE(freqEndDate);
									}
								}	
								if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("A")) {
									Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
									if(getQfreqENDDATE() == null) {
										String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
										String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
										String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -12)+"", DBDATE, DATE_FORMAT);
										freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 11, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
//										System.out.println("arrTmpMinMaxDate[1] ===>> " + arrTmpMinMaxDate[1]);
										setQfreqENDDATE(freqEndDate);
//										System.out.println("arrTmpMinMaxDate[1] freqEndDate ===>> " + freqEndDate);
									} else {
//									System.out.println("freqEndDate ===>> " + freqEndDate+" == getQfreqENDDATE()  ===>> " + getQfreqENDDATE());
										freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 12)+"", DBDATE, DATE_FORMAT);
									}
									if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
										freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
									}
									if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
										setQfreqENDDATE(freqEndDate);
									}
								}	
								Date firstDate = uF.getDateFormatUtil(strFirstDate, DATE_FORMAT);
								Date endDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
								Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
	//									System.out.println("endDate ===>> " + endDate + "  newFreqDate ======>> " + newFreqDate +" -- proId ===>> " + proId);
								
								setFreqENDDATE(freqEndDate);
								
								if((firstDate.before(newFreqDate) || firstDate.equals(newFreqDate)) && (endDate.after(newFreqDate) || endDate.equals(newFreqDate))) {
									dblBillAmount += uF.parseToInt(hmProjectData.get(proId+"_BILLING_AMT"));
								}
							}
						}
						
						totalCommitmentAmt += dblBillAmount;
					}
					
					hmCommitAmt.put(dateArr[0], uF.formatIntoTwoDecimalWithOutComma(totalCommitmentAmt));
				}
				request.setAttribute("totalCommitmentAmt", uF.formatIntoTwoDecimal(totalCommitmentAmt));

//				System.out.println("sbCommitReceipts ===>> " + sbCommitReceipts.toString());
				
				
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	
	private String getNewProjectStartDate(Connection con, UtilityFunctions uF, String proId, String freqEndDate) {

		String newStDate = null;
		try {
			if(freqEndDate != null) {
				newStDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), 1)+"", DBDATE, DATE_FORMAT);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return newStDate;
	}
	
	private void getMyTaskPerformanceDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select * from activity_info where resource_ids like '%,"+strSessionEmpId+",%'  and approve_status='n' " +
					"and task_accept_status = 1  order by deadline limit 5");
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alTask = new ArrayList<Map<String,String>>();
			StringBuilder sbTaskIds = null;
			while(rs.next()) {
				Map<String, String> hmTask = new HashMap<String, String>();
				hmTask.put("TASK_ID", rs.getString("task_id"));
				hmTask.put("PRO_ID", rs.getString("pro_id"));
				hmTask.put("TASK_NAME", uF.showData(rs.getString("activity_name"), ""));
				hmTask.put("TASK_EST_TIME", uF.showData(rs.getString("idealtime"), ""));
				String resourceIds = rs.getString("resource_ids").substring(1, rs.getString("resource_ids").length()-1);
				hmTask.put("TASK_RESOURCE_IDS", resourceIds);
				hmTask.put("TASK_START_DATE", rs.getString("start_date"));
				hmTask.put("TASK_DEADLINE", rs.getString("deadline"));
				hmTask.put("TASK_COMPLETED", rs.getString("completed"));
				alTask.add(hmTask);
				
				if(sbTaskIds == null) {
					sbTaskIds = new StringBuilder();
					sbTaskIds.append(rs.getString("task_id"));
				} else {
					sbTaskIds.append(","+rs.getString("task_id"));
				}
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmHrsActualTime = new HashMap<String, String>();
			if(sbTaskIds != null) {
				pst = con.prepareStatement("select sum(actual_hrs) as actual_hrs, activity_id from task_activity where activity_id in ("+sbTaskIds.toString()+") and emp_id =? group by activity_id");
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
//				System.out.println("pst ======> " + pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					hmHrsActualTime.put(rs.getString("activity_id"), uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("actual_hrs")));
				}
				rs.close();
				pst.close();
			}
			
			List<Map<String, String>> alMyTask = new ArrayList<Map<String,String>>();
			for(int i=0; alTask != null && !alTask.isEmpty() && i< alTask.size(); i++) {
				Map<String, String> hmTask = alTask.get(i);

				Map<String, String> hmMyTask = new HashMap<String, String>();
				hmMyTask.put("TASK_ID", hmTask.get("TASK_ID"));
				hmMyTask.put("PRO_ID", hmTask.get("PRO_ID"));
				hmMyTask.put("TASK_NAME", hmTask.get("TASK_NAME"));
				hmMyTask.put("TASK_COMPLETED", hmTask.get("TASK_COMPLETED"));
				
				List<String> alResIds = new ArrayList<String>();
				if(hmTask.get("TASK_RESOURCE_IDS") != null && !hmTask.get("TASK_RESOURCE_IDS").trim().equals("")) {
					alResIds = Arrays.asList(hmTask.get("TASK_RESOURCE_IDS").split(","));
				}
				
				double idealTime = uF.parseToDouble(hmTask.get("TASK_EST_TIME")) / alResIds.size();
				
				Map<String, String> hmProData = CF.getProjectDetailsByProId(con, hmTask.get("PRO_ID"));
				double hrsIdealTime = 0;
				if(hmProData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProData.get("PRO_BILLING_ACTUAL_TYPE").equals("M")) {
					hrsIdealTime = (idealTime * 30) * 8;
				} else if(hmProData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProData.get("PRO_BILLING_ACTUAL_TYPE").equals("D")) {
					hrsIdealTime = idealTime * 8;
				} else if(hmProData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProData.get("PRO_BILLING_ACTUAL_TYPE").equals("H")) {
					hrsIdealTime = idealTime;
				}
				hmMyTask.put("TASK_EST_TIME", uF.formatIntoTwoDecimalWithOutComma(hrsIdealTime));
				
				hmMyTask.put("TASK_SPENT_TIME", uF.showData(hmHrsActualTime.get(hmTask.get("TASK_ID")), "0.00"));
				 
				Date dtDeadline = uF.getDateFormat(hmTask.get("TASK_DEADLINE"), DBDATE);
				Date dtCurrentDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
				
				if(dtDeadline!=null && dtCurrentDate!=null && dtDeadline.after(dtCurrentDate)) {
					if(uF.parseToDouble(hmHrsActualTime.get(hmTask.get("TASK_ID"))) <= hrsIdealTime) {
						  /*hmMyTask.put("TASK_TIME_INDICATOR", "<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
						hmMyTask.put("TASK_TIME_INDICATOR", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Actual Time < Estimated\"></i>");
						
					} else {
						/*hmMyTask.put("TASK_TIME_INDICATOR", "<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
						hmMyTask.put("TASK_TIME_INDICATOR", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Actual Time > Estimated\"></i>");
						
					}
				} else if(dtDeadline!=null && dtCurrentDate!=null && (dtCurrentDate.after(dtDeadline) || dtCurrentDate.equals(dtDeadline))) {
					/*hmMyTask.put("TASK_TIME_INDICATOR", "<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
					hmMyTask.put("TASK_TIME_INDICATOR", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Actual Date > Deadline\"></i>");
					
				} else {
					hmMyTask.put("TASK_TIME_INDICATOR", "");
				}
				alMyTask.add(hmMyTask);
			}
			
			request.setAttribute("alMyTask", alMyTask);
//			System.out.println("alMyTask======>"+alMyTask);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	
	
	private void getTeamEffort(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			String[] strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
			String strCalendarYearStart = strCalendarYearDates[0];
			String strCalendarYearEnd = strCalendarYearDates[1];
			
			String strMonth = ""+uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"));
			String strYear = ""+uF.parseToInt(uF.getDateFormat(strCalendarYearEnd+"", DATE_FORMAT, "yyyy"));
			
			List<List<String>> weekdates = new ArrayList<List<String>>();
			uF.getMonthWeeksDate(weekdates,strMonth,strYear,DATE_FORMAT);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select eod.emp_id,epd.emp_fname,epd.emp_mname,epd.emp_lname,epd.emp_image from employee_personal_details epd,employee_official_details eod " +
					"where epd.emp_per_id=eod.emp_id and eod.emp_id in(select distinct(emp_id) from project_emp_details where emp_id > 0 ");
		//===start parvez date: 17-10-2022===	
			/*sbQuery.append(" and pro_id in (select pro_id from projectmntnc where pro_id > 0 and (project_owner="+uF.parseToInt(strSessionEmpId)+" " +
					"or pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(strSessionEmpId)+"))) ");*/
			sbQuery.append(" and pro_id in (select pro_id from projectmntnc where pro_id > 0 and (project_owners like '%,"+strSessionEmpId+",%' " +
					"or pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(strSessionEmpId)+"))) ");
		//===end parvez date: 17-10-2022===	
			sbQuery.append(")");
			sbQuery.append(" order by epd.emp_fname,epd.emp_lname limit 5 ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alPeople = new ArrayList<Map<String,String>>();
			while(rs.next()) {
				Map<String, String> hmPeople = new HashMap<String, String>();
				hmPeople.put("EMP_ID", rs.getString("emp_id"));	
				/*String strMiddleName = "";
				if(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("") && !rs.getString("emp_mname").trim().equalsIgnoreCase("NULL")) {
					strMiddleName = rs.getString("emp_mname")+" ";
				}*/
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				hmPeople.put("EMP_NAME", uF.showData(rs.getString("emp_fname"), "")+strEmpMName+" "+uF.showData(rs.getString("emp_lname"), ""));
				hmPeople.put("EMP_IMAGE", uF.showData(rs.getString("emp_image"), ""));
				
				alPeople.add(hmPeople); 
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmBillable = new HashMap<String, String>();
            Map<String, String> hmNonBillable = new HashMap<String, String>();
            Map<String, String> hmOther = new HashMap<String, String>();
            
            Map<String, String> hmBillableTotalCnt = new HashMap<String, String>();
	        Map<String, String> hmNonBillableTotalCnt = new HashMap<String, String>();
	        Map<String, String> hmOtherTotalCnt = new HashMap<String, String>();
            
			if(alPeople != null && alPeople.size() > 0) {
				for(int j =0; j<alPeople.size(); j++) {
					Map<String, String> hmPeople = alPeople.get(j);
					String strEmpId = hmPeople.get("EMP_ID");
					StringBuilder sbBillableCount = null;
					StringBuilder sbNonBillableCount = null;
					StringBuilder sbOtherCount = null;
					double dblBillableCnt = 0.0d;
					double dblNonBillableCnt = 0.0d;
					double dblOtherCnt = 0.0d;
					int x = 0;
					
					for(int i = 0; weekdates!=null && i < weekdates.size();i++) {
						List<String> week = weekdates.get(i);
						x++;
						/**Billable Count
						 * */
						pst = con.prepareStatement("select sum(billable_hrs) as billable_hrs from task_activity where emp_id = ? and activity_id > 0 " +
								"and task_date between ? and ? and is_billable =true");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getDateFormat(week.get(0), DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(week.get(1), DATE_FORMAT));
//						System.out.println("pst======>"+pst);
						rs = pst.executeQuery();
						double dblBillable = 0.0d;
						while(rs.next()){
							dblBillable = uF.parseToDouble(rs.getString("billable_hrs"));
						}
						rs.close();
						pst.close();
						
						dblBillable = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblBillable));
						
						if(sbBillableCount == null){
							sbBillableCount = new StringBuilder();
							sbBillableCount.append(""+dblBillable);
						} else {
							sbBillableCount.append(","+dblBillable);
						}
						dblBillableCnt +=dblBillable;
						
						double dblBillableTotalCnt = uF.parseToDouble(hmBillableTotalCnt.get(x+"week"));
						dblBillableTotalCnt +=dblBillable;
						hmBillableTotalCnt.put(x+"week", ""+dblBillableTotalCnt);
						/**Billable Count end
						 * */
						
						/**NonBillable Count
						 * */
						pst = con.prepareStatement("select sum(billable_hrs) as billable_hrs, sum(actual_hrs) as actual_hrs from task_activity " +
								"where emp_id = ? and activity_id > 0 and task_date between ? and ? ");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getDateFormat(week.get(0), DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(week.get(1), DATE_FORMAT));
						rs = pst.executeQuery();
						double dblNonBillable = 0.0d;
						while(rs.next()){
							dblNonBillable = uF.parseToDouble(rs.getString("actual_hrs")) - uF.parseToDouble(rs.getString("billable_hrs"));
						}
						rs.close();
						pst.close();
						
						dblNonBillable = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblNonBillable));
						
						if(sbNonBillableCount == null){
							sbNonBillableCount = new StringBuilder();
							sbNonBillableCount.append(""+dblNonBillable);
						} else {
							sbNonBillableCount.append(","+dblNonBillable);
						}
						dblNonBillableCnt +=dblNonBillable;
						
						double dblNonBillableTotalCnt = uF.parseToDouble(hmNonBillableTotalCnt.get(x+"week"));
						dblNonBillableTotalCnt +=dblNonBillable;
						hmNonBillableTotalCnt.put(x+"week", ""+dblNonBillableTotalCnt);
						
						/**NonBillable Count
						 * */
						/**Other Count
						 * */
						pst = con.prepareStatement("select sum(actual_hrs) as actual_hrs from task_activity where emp_id = ? and activity_id = 0 " +
								"and (activity_id > 0 and activity is not null) and task_date between ? and ?");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getDateFormat(week.get(0), DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(week.get(1), DATE_FORMAT));
						rs = pst.executeQuery();
						double dblOther = 0.0d;
						while(rs.next()){
							dblOther = uF.parseToDouble(rs.getString("actual_hrs"));
						}
						rs.close();
						pst.close();
						
						dblOther = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblOther));
						
						if(sbOtherCount == null){
							sbOtherCount = new StringBuilder();
							sbOtherCount.append(""+dblOther);
						} else {
							sbOtherCount.append(","+dblOther);
						}
						dblOtherCnt +=dblOther;
						
						double dblOtherTotalCnt = uF.parseToDouble(hmOtherTotalCnt.get(x+"week"));
						dblOtherTotalCnt +=dblOther;
						hmOtherTotalCnt.put(x+"week", ""+dblOtherTotalCnt);
						
						/**Other Count
						 * */
					}
					
					hmBillable.put(strEmpId+"_BILLABLE", sbBillableCount.toString());
					hmBillable.put(strEmpId+"_BILLABLE_COUNT", ""+uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblBillableCnt)));
					
					hmNonBillable.put(strEmpId+"_NON_BILLABLE", sbNonBillableCount.toString());
					hmNonBillable.put(strEmpId+"_NON_BILLABLE_COUNT", ""+uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblNonBillableCnt)));
					
					hmOther.put(strEmpId+"_OTHER", sbOtherCount.toString());
					hmOther.put(strEmpId+"_OTHER_COUNT", ""+uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblOtherCnt)));
				}
			}
			
			request.setAttribute("alPeople", alPeople);
			request.setAttribute("hmBillable", hmBillable);
			request.setAttribute("hmNonBillable", hmNonBillable);
			request.setAttribute("hmOther", hmOther);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void getProjectandBusinessDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			if(uF.parseToInt(getProBusinessSnapshot()) == 0) {
				setProBusinessSnapshot("1");
			}
			String strProDate = null;
			if(uF.parseToInt(getProBusinessSnapshot()) == 1) {
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getProBusinessSnapshot()) == 2) {
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 180), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getProBusinessSnapshot()) == 3) {
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 90), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getProBusinessSnapshot()) == 4) {
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 30), DBDATE, DATE_FORMAT);
			} else {
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			}
			
			String strCurr = null;
			String strOrgCurr = CF.getOrgCurrencyIdByOrg(con, strSessionOrgId);
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			Map<String, String> hmCurr = hmCurrencyMap.get(strOrgCurr);
			strCurr = hmCurr != null ? hmCurr.get("SHORT_CURR") : "";
			request.setAttribute("strCurr", strCurr);
			
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 17-10-2022===	
			/*sbQuery.append("select * from projectmntnc where pro_id > 0 and (project_owner="+uF.parseToInt(strSessionEmpId)+" " +
					"or pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(strSessionEmpId)+"))");*/
			sbQuery.append("select * from projectmntnc where pro_id > 0 and (project_owners like '%,"+strSessionEmpId+",%' " +
					"or pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(strSessionEmpId)+"))");
		//===end parvez date: 17-10-2022===	
			if(uF.parseToInt(getProBusinessSnapshot()) < 5) {
				sbQuery.append(" and start_date > ? ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			if(uF.parseToInt(getProBusinessSnapshot()) < 5) {
				pst.setDate(1, uF.getDateFormat(strProDate, DATE_FORMAT));
			}
			rs = pst.executeQuery();
			StringBuilder sbProId = null;
			while(rs.next()){
				if(sbProId == null){
					sbProId = new StringBuilder();
					sbProId.append(rs.getString("pro_id"));
				} else {
					sbProId.append(","+rs.getString("pro_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(sbProId != null && sbProId.length() > 0){
				
				/**
				 * Live Project Count
				 * */
				sbQuery = new StringBuilder();
				sbQuery.append("select count(*) as proCount from projectmntnc where pro_id > 0 and pro_id in ("+sbProId.toString()+") and approve_status='n' ");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				int nLivePro = 0;
				while(rs.next()){
					nLivePro = rs.getInt("proCount");
				}
				rs.close();
				pst.close();
				request.setAttribute("nLivePro", ""+nLivePro);
				/**
				 * Live Project Count end
				 * */
				
				/**
				 * New Project Count
				 * */
				sbQuery = new StringBuilder();
				sbQuery.append("select count(*) as proCount from projectmntnc where pro_id > 0 and pro_id in ("+sbProId.toString()+") and approve_status='n' ");
				sbQuery.append(" and (completed = 0 or completed is null) and deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				int nNewPro = 0;
				while(rs.next()){
					nNewPro = rs.getInt("proCount");
				}
				rs.close();
				pst.close();
				request.setAttribute("nNewPro", ""+nNewPro);
				/**
				 * New Project Count end 
				 * */
				
				/**
				 * On Track Project Count
				 * */
				sbQuery = new StringBuilder();
				sbQuery.append("select count(*) as proCount from projectmntnc where pro_id > 0 and pro_id in ("+sbProId.toString()+") and approve_status='n' ");
				sbQuery.append(" and completed>0 and deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"'");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				int nOnTrackPro = 0;
				while(rs.next()){
					nOnTrackPro = rs.getInt("proCount");
				}
				rs.close();
				pst.close();
				request.setAttribute("nOnTrackPro", ""+nOnTrackPro);
				/**
				 * On Track Count end 
				 * */
				
				/**
				 * Delayed Project Count
				 * */
				sbQuery = new StringBuilder();
				sbQuery.append("select count(*) as proCount from projectmntnc where pro_id > 0 and pro_id in ("+sbProId.toString()+") and approve_status='n' ");
				sbQuery.append(" and deadline < '"+uF.getCurrentDate(CF.getStrTimeZone())+"'");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				int nDelayedPro = 0;
				while(rs.next()){
					nDelayedPro = rs.getInt("proCount");
				}
				rs.close(); 
				pst.close();
				request.setAttribute("nDelayedPro", ""+nDelayedPro);
				/**
				 * On Track Count end 
				 * */
				
				
				/**
				 * Project Profit
				 * */
				
//				Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", false, null, null, uF);
				Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
				Map<String, String> hmPprojectClientMap = CF.getProjectClientMap(con, uF);
				if(hmPprojectClientMap == null) hmPprojectClientMap = new HashMap<String, String>();
				Map<String, String> hmCustName = CF.getCustomerNameMap(con);
				if(hmCustName == null) hmCustName = new HashMap<String, String>();
				Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
				Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
				
//				Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", true, getStrStartDate(), getStrEndDate(), uF);
				Map<String, String> hmReimbursementAmountMap = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(reimbursement_amount) as reimbursement_amount, pro_id as group_type from emp_reimbursement where approval_2 = 1 " +
						"and pro_id in (select pmc.pro_id from projectmntnc pmc, activity_info ai where ai.pro_id = pmc.pro_id ");
					sbQuery.append(" and pmc.approve_status = 'approved'");
				sbQuery.append(" ) and pro_id in("+sbProId.toString()+") and reimbursement_type1 = 'P' group by pro_id");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst===>"+pst);  
				rs = pst.executeQuery();
				while (rs.next()) {
					String arr[] = null;
					if (rs.getString("group_type") != null) {
						arr = rs.getString("group_type").split(",");
						hmReimbursementAmountMap.put(arr[0], rs.getString("reimbursement_amount"));
					}
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from project_emp_details where pro_id>0 and pro_id in("+sbProId.toString()+") order by pro_id");
				rs=pst.executeQuery();
				Map<String, String> hmProEmpRate = new HashMap<String, String>();
				while(rs.next()) {
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_HOUR", rs.getString("emp_actual_rate_per_hour"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_DAY", rs.getString("emp_actual_rate_per_day"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_MONTH", rs.getString("emp_actual_rate_per_month"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_HOUR", rs.getString("emp_rate_per_hour"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_DAY", rs.getString("emp_rate_per_day"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_MONTH", rs.getString("emp_rate_per_month"));
					
				}
//				System.out.println("hmProEmpRate ===>> " + hmProEmpRate);
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select org_id, calculation_type, days from cost_calculation_settings ");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===> " + pst);
				rs = pst.executeQuery();
				Map<String, String> hmOrgCalType = new HashMap<String, String>();
				while (rs.next()) {
					hmOrgCalType.put(rs.getString("org_id"), rs.getString("calculation_type"));
					hmOrgCalType.put(rs.getString("org_id")+"_DAYS", rs.getString("days"));
				}
				
				sbQuery = new StringBuilder();
				sbQuery.append("select * from projectmntnc pmc where pmc.pro_id > 0 and pro_id in ("+sbProId.toString()+")");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst======>"+pst);
				rs = pst.executeQuery();
				double dblBugedtedAmt = 0;
				double dblActualAmt = 0;
				double dblBillableAmt = 0;
				while(rs.next()){
//					Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
					Map<String, String> hmProjectData = new HashMap<String, String>();
					hmProjectData.put("PRO_ID", rs.getString("pro_id"));
					hmProjectData.put("PRO_NAME", rs.getString("pro_name"));
					hmProjectData.put("PRO_CUSTOMER_NAME", uF.showData(hmPprojectClientMap.get(rs.getString("client_id")), ""));
					hmProjectData.put("PRO_CUST_SPOC_ID", rs.getString("poc"));
					hmProjectData.put("PRO_CUST_SPOC_NAME", uF.showData(hmCustName.get(rs.getString("poc")), ""));
					hmProjectData.put("PRO_OWNER_ID", rs.getString("project_owner"));
					hmProjectData.put("PRO_OWNER_NAME", uF.showData(hmEmpCodeName.get(rs.getString("project_owner")), ""));
					hmProjectData.put("PRO_BILLING_TYPE", CF.getBillinType(rs.getString("billing_type")));
					hmProjectData.put("PRO_BILL_TYPE", rs.getString("billing_type"));
					hmProjectData.put("PRO_BILLING_ACTUAL_TYPE", rs.getString("actual_calculation_type"));
					hmProjectData.put("PRO_BILLING_FREQUENCY", CF.getBillinFreq(rs.getString("billing_kind"), rs.getString("billing_type")));
					hmProjectData.put("PRO_BILL_FREQUENCY", rs.getString("billing_kind"));
					hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
					hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
					hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
					hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
					hmProjectData.put("PRO_SERVICE_ID", rs.getString("service"));
					hmProjectData.put("PRO_REPORT_CURR_ID", rs.getString("curr_id"));
					hmProjectData.put("PRO_BILLING_CURR_ID", rs.getString("billing_curr_id"));
					hmProjectData.put("PRO_BILLING_AMOUNT", rs.getString("billing_amount"));
					hmProjectData.put("PRO_ORG_ID", rs.getString("org_id"));
					

					Map<String, String> hmProActualCostTime = new HashMap<String, String>();
					Map<String, String> hmProBillCost = new HashMap<String, String>();
					if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equals("M")) {
//						hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData);
						hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation,hmOrgCalType);
//						hmProBillCost = CF.getMonthlyProjectBillableCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData);
						hmProBillCost = CF.getMonthlyProjectBillableCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation,hmOrgCalType);
					} else {
//						hmProActualCostTime = CF.getProjectActualCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
						hmProActualCostTime = CF.getProjectActualCostAndTime(con,CF, uF, rs.getString("pro_id"), hmProjectData, false, false,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation); //,hmOrgCalType
//						hmProBillCost = CF.getProjectBillableCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
						hmProBillCost = CF.getProjectBillableCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation); //,hmOrgCalType
					}
					
//					Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con, uF, rs.getString("pro_id"), hmProjectData);
					Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con,CF, uF, rs.getString("pro_id"), hmProjectData,hmProEmpRate);
					
					double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
					if("F".equalsIgnoreCase(rs.getString("billing_type"))) { 
						dblBillableAmt += uF.parseToDouble(rs.getString("billing_amount"));
					} else {
						dblBillableAmt += uF.parseToDouble(hmProBillCost.get("proBillableCost"));
					}
					 
					 
					dblBugedtedAmt += uF.parseToDouble(hmProBudgetedCostAndTime.get("proBudgetedCost"));
					dblActualAmt += (uF.parseToDouble(hmProActualCostTime.get("proActualCost")) + dblReimbursement);
					
				}
				rs.close(); 
				pst.close();
				
				double dblProfit = 0.0d;
				double dblProfitMargin = 0.0d;
				if(dblBillableAmt > 0) {
					dblProfit = (dblBillableAmt - dblActualAmt);
					dblProfitMargin = (dblProfit/dblBillableAmt) * 100;
				}
				
				request.setAttribute("dblProfit", uF.formatIntoTwoDecimal(dblProfit));
				request.setAttribute("dblProfitMargin", uF.formatIntoTwoDecimal(dblProfitMargin));
				request.setAttribute("dblBugedtedAmt", uF.formatIntoTwoDecimal(dblBugedtedAmt));
				request.setAttribute("dblActualAmt", uF.formatIntoTwoDecimal(dblActualAmt));
				/**
				 * Project Profit end 
				 * */
				
				/**
				 * Bills & receipts
				 * */
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(oc_invoice_amount) as oc_invoice_amount " +
						"from promntc_invoice_details where pro_id in ("+sbProId.toString()+")");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst=====>"+pst);
				rs = pst.executeQuery();
				double dblBilled = 0.0d;
				while(rs.next()){
					dblBilled = uF.parseToDouble(rs.getString("oc_invoice_amount"));
				}
				rs.close();
				pst.close();
				request.setAttribute("dblBilled", uF.formatIntoTwoDecimal(dblBilled));
				
				
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(received_amount) as received_amount from promntc_bill_amt_details " +
						"where pro_id in ("+sbProId.toString()+") ");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst=====>"+pst);
				rs = pst.executeQuery();
				double dblReceived = 0.0d;
				while(rs.next()){
					dblReceived = uF.parseToDouble(rs.getString("received_amount"));
				}
				rs.close();
				pst.close();
				request.setAttribute("dblReceived", uF.formatIntoTwoDecimal(dblReceived));
				
//				StringBuilder sbBillsDonut 	= new StringBuilder();
//				sbBillsDonut.append("{'protype':'Bills', " +
//						"'amt': "+uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblBilled))+"},");
//				sbBillsDonut.append("{'protype':'Received', " +
//						"'amt': "+uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblReceived))+"}");
				
				StringBuilder sbBillsDonut 	= new StringBuilder();
				double dblReceivePercentage = (uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblReceived))/uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblBilled))) * 100;
				double dblBillPercentage = 100 - dblReceivePercentage;
				sbBillsDonut.append("{'category': 'Received from Billed','Pending': "+uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblBillPercentage))+"," +
						"'Received': "+uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblReceivePercentage))+"}");
				request.setAttribute("sbBillsDonut", sbBillsDonut.toString());
//				System.out.println("sbBillsDonut.toString()=====>"+sbBillsDonut.toString());
				
				/**
				 * Bills & receipts end
				 * */
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void getResourceMoneyWorkKPI(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			if(uF.parseToInt(getTaskWorking()) == 0) {
				setTaskWorking("3");
			}
			String strTaskDate = null;
			if(uF.parseToInt(getTaskWorking()) == 1){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getTaskWorking()) == 2){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 180), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getTaskWorking()) == 3){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 90), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getTaskWorking()) == 4){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 30), DBDATE, DATE_FORMAT);
			} else {
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			}
			
			Map<String, String> hmTaskPro = new HashMap<String, String>();
			List<String> alTaskId = new ArrayList<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select a.* from (select task_id,parent_task_id,pro_id,start_date,idealtime from activity_info where resource_ids like '%,"+strSessionEmpId+",%' and task_id " +
					"not in (select parent_task_id from activity_info where resource_ids like '%,"+strSessionEmpId+",%' and parent_task_id is not null) and " +
					"task_accept_status = 1) a, projectmntnc pmc where pmc.pro_id=a.pro_id and (parent_task_id in (select task_id from activity_info " +
					"where resource_ids like '%,"+strSessionEmpId+",%') or parent_task_id = 0) and a.start_date > ? ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strTaskDate, DATE_FORMAT));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmTaskPro.put("PRO_ID_"+rs.getString("task_id"), rs.getString("pro_id"));
				hmTaskPro.put("TASK_ID_"+rs.getString("task_id"), rs.getString("task_id"));
				hmTaskPro.put("IDEAL_TIME_"+rs.getString("task_id"), rs.getString("idealtime"));
				
				if(!alTaskId.contains(rs.getString("task_id"))) {
					alTaskId.add(rs.getString("task_id"));
				}
			}
			rs.close();
			pst.close();
			
			/**
			 * Work KPI Money
			 * */
			double dblActualCostTotal = 0.0d;
			double dblBudgetedCostTotal = 0.0d;
			double dblBillableAmt = 0.0d;
			List<String> alProId = new ArrayList<String>();
			for(int i = 0; alTaskId != null && i < alTaskId.size(); i++){
				String strProId =  hmTaskPro.get("PRO_ID_"+alTaskId.get(i));
				String strTaskId =  hmTaskPro.get("TASK_ID_"+alTaskId.get(i));
				
				pst = con.prepareStatement("SELECT * FROM projectmntnc where pro_id = ?");
				pst.setInt(1, uF.parseToInt(strProId));
				rs = pst.executeQuery();
				String strActualBilling = null;
				while(rs.next()) {
					strActualBilling = rs.getString("actual_calculation_type");
					Map<String, String> hmProBillCost = new HashMap<String, String>();
					Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
					if("M".equalsIgnoreCase(rs.getString("actual_calculation_type"))) { 
						hmProBillCost = CF.getMonthlyProjectBillableCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData);
					} else {
						hmProBillCost = CF.getProjectBillableCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
					}
					
//					dblBillableAmt = uF.parseToDouble(hmPOActBillAmt.get(strProId+"BILL_AMT"));
					if(!alProId.contains(strProId)) {
						 if("F".equalsIgnoreCase(rs.getString("billing_type"))) { 
							 dblBillableAmt += uF.parseToDouble(rs.getString("billing_amount"));
						 } else {
							 dblBillableAmt += uF.parseToDouble(hmProBillCost.get("proBillableCost"));
						 }
//						 System.out.println(" -- dblBillableAmt ===>> " + dblBillableAmt);
					}
				}
				rs.close();
				pst.close();
				if(!alProId.contains(strProId)) {
					alProId.add(strProId);
				}
				
				Map<String, String> empcostMp = CF.getProjectEmpActualRates(con, uF, strProId, strActualBilling);
				double dblEmpRate = uF.parseToDouble(uF.getTaskResourcesAvgCostOrBillRate(empcostMp, ""+strSessionEmpId));
				double dblIdealTime = uF.parseToDouble(hmTaskPro.get("IDEAL_TIME_"+alTaskId.get(i)));
				
				double dblTaskActualCost = 0.0d;
				if(strActualBilling!=null && strActualBilling.equalsIgnoreCase("M")) {
					pst = con.prepareStatement("SELECT count(distinct(task_date)) as cnt FROM task_activity where activity_id = ? and emp_id=?");
					pst.setInt(1, uF.parseToInt(strTaskId));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					rs = pst.executeQuery();
					int nTaskCount = 0;
					while(rs.next()) {
						nTaskCount = rs.getInt("cnt");
					}
					rs.close();
					pst.close();
					
					dblTaskActualCost = (nTaskCount / 30) * dblEmpRate;
				} else if(strActualBilling!=null && strActualBilling.equalsIgnoreCase("D")) {
					pst = con.prepareStatement("SELECT count(distinct(task_date)) as cnt FROM task_activity where activity_id = ? and emp_id=?");
					pst.setInt(1, uF.parseToInt(strTaskId));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					rs = pst.executeQuery();
					int nTaskCount = 0;
					while(rs.next()) {
						nTaskCount = rs.getInt("cnt");
					}
					rs.close();
					pst.close();
					
					dblTaskActualCost = nTaskCount * dblEmpRate;
				} else if(strActualBilling!=null && strActualBilling.equalsIgnoreCase("H")){
					pst = con.prepareStatement("SELECT sum(actual_hrs) as actual_hrs FROM task_activity where activity_id = ? and emp_id=?");
					pst.setInt(1, uF.parseToInt(strTaskId));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					rs = pst.executeQuery();
					double nTaskCount = 0;
					while(rs.next()) {
						nTaskCount = uF.parseToDouble(rs.getString("actual_hrs"));
					}
					rs.close();
					pst.close();
					
					dblTaskActualCost = nTaskCount * dblEmpRate;
				}
				
				
				double budgetedAmt = 0;
				if(dblIdealTime > 0) {
					budgetedAmt = dblEmpRate * dblIdealTime;
				}
				dblActualCostTotal += dblTaskActualCost;
				dblBudgetedCostTotal += budgetedAmt;
			}
			double[] TASK_MONEY_DATA  = new double[2];
			String[] TASK_MONEY_LABEL  = new String[2];
			
			TASK_MONEY_DATA[0] = uF.parseToDouble(uF.formatIntoZeroWithOutComma(dblActualCostTotal));
			TASK_MONEY_DATA[1] = uF.parseToDouble(uF.formatIntoZeroWithOutComma(dblBudgetedCostTotal));
			
			request.setAttribute("TASK_MONEY_KPI", new SemiCircleMeter().getSemiCircleChart(TASK_MONEY_DATA, TASK_MONEY_LABEL, "Money"));
			request.setAttribute("TASK_ACTUAL_MONEY_KPI", uF.formatIntoZeroWithOutComma(dblActualCostTotal));
			request.setAttribute("TASK_BUDGET_MONEY_KPI", uF.formatIntoZeroWithOutComma(dblBudgetedCostTotal));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void getResourceMoneyProjectKPI(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			if(uF.parseToInt(getProWorking()) == 0) {
				setProWorking("3");
			}
			String strProDate = null;
			if(uF.parseToInt(getProWorking()) == 1) {
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getProWorking()) == 2) {
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 180), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getProWorking()) == 3) {
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 90), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getProWorking()) == 4) {
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 30), DBDATE, DATE_FORMAT);
			} else {
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			}
			
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmPprojectClientMap = CF.getProjectClientMap(con, uF);
			if(hmPprojectClientMap == null) hmPprojectClientMap = new HashMap<String, String>();
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
			if(hmCustName == null) hmCustName = new HashMap<String, String>();
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			
			Map<String, String> hmPro = new HashMap<String, String>();
			List<String> alProjectId = new ArrayList<String>(); 
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 17-10-2022===	
			/*sbQuery.append("select * from projectmntnc pmc where pmc.pro_id > 0  and approve_status !='blocked' " +
					"and project_owner=? and start_date > ?");*/
			sbQuery.append("select * from projectmntnc pmc where pmc.pro_id > 0  and approve_status !='blocked' " +
					"and project_owners like '%,"+strSessionEmpId+",%' and start_date > ?");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setDate(1, uF.getDateFormat(strProDate, DATE_FORMAT));
		//===end parvez date: 17-10-2022===	
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			StringBuilder sbProId = null;
			Map<String, Map<String, String>> hmProjectPro = new HashMap<String, Map<String, String>>();
			while (rs.next()){
				hmPro.put("PRO_ID_"+rs.getString("pro_id"), rs.getString("pro_id"));
				hmPro.put("ACTUAL_CAL_TYPE_"+rs.getString("pro_id"), rs.getString("actual_calculation_type"));
				hmPro.put("BILLING_TYPE_"+rs.getString("pro_id"), rs.getString("billing_type"));
				hmPro.put("BILLING_AMOUNT_"+rs.getString("pro_id"), rs.getString("billing_amount"));
				
				if(!alProjectId.contains(rs.getString("pro_id"))) {
					alProjectId.add(rs.getString("pro_id"));
				}
				if(sbProId==null){
					sbProId=new StringBuilder();
					sbProId.append(rs.getString("pro_id"));
				} else {
					sbProId.append(","+rs.getString("pro_id"));
				}
				
				Map<String, String> hmProjectData = new HashMap<String, String>();
				hmProjectData.put("PRO_ID", rs.getString("pro_id"));
				hmProjectData.put("PRO_NAME", rs.getString("pro_name"));
				hmProjectData.put("PRO_CUSTOMER_NAME", uF.showData(hmPprojectClientMap.get(rs.getString("client_id")), ""));
				hmProjectData.put("PRO_CUST_SPOC_ID", rs.getString("poc"));
				hmProjectData.put("PRO_CUST_SPOC_NAME", uF.showData(hmCustName.get(rs.getString("poc")), ""));
				hmProjectData.put("PRO_OWNER_ID", rs.getString("project_owner"));
				hmProjectData.put("PRO_OWNER_NAME", uF.showData(hmEmpCodeName.get(rs.getString("project_owner")), ""));
				hmProjectData.put("PRO_BILLING_TYPE", CF.getBillinType(rs.getString("billing_type")));
				hmProjectData.put("PRO_BILL_TYPE", rs.getString("billing_type"));
				hmProjectData.put("PRO_BILLING_ACTUAL_TYPE", rs.getString("actual_calculation_type"));
				hmProjectData.put("PRO_BILLING_FREQUENCY", CF.getBillinFreq(rs.getString("billing_kind"), rs.getString("billing_type")));
				hmProjectData.put("PRO_BILL_FREQUENCY", rs.getString("billing_kind"));
				hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
				hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
				hmProjectData.put("PRO_SERVICE_ID", rs.getString("service"));
				hmProjectData.put("PRO_REPORT_CURR_ID", rs.getString("curr_id"));
				hmProjectData.put("PRO_BILLING_CURR_ID", rs.getString("billing_curr_id"));
				hmProjectData.put("PRO_BILLING_AMOUNT", rs.getString("billing_amount"));
				hmProjectData.put("PRO_ORG_ID", rs.getString("org_id"));
				
				hmProjectPro.put(rs.getString("pro_id"), hmProjectData);
				
			}
			rs.close();
			pst.close();
//			System.out.println("hmProjectPro ==========>> " + hmProjectPro);
			
			/**
			 * Project KPI Money
			 * */
//			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", true, null, null, uF);
			if(sbProId!=null && sbProId.length()>0){
//				Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", true, getStrStartDate(), getStrEndDate(), uF);
				Map<String, String> hmReimbursementAmountMap = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(reimbursement_amount) as reimbursement_amount, pro_id as group_type from emp_reimbursement where approval_2 = 1 " +
						"and pro_id in (select pmc.pro_id from projectmntnc pmc, activity_info ai where ai.pro_id = pmc.pro_id ");
					sbQuery.append(" and pmc.approve_status = 'approved'");
				sbQuery.append(" ) and pro_id in("+sbProId.toString()+") and reimbursement_type1 = 'P' group by pro_id");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst===>"+pst);  
				rs = pst.executeQuery();
				while (rs.next()) {
					String arr[] = null;
					if (rs.getString("group_type") != null) {
						arr = rs.getString("group_type").split(",");
						hmReimbursementAmountMap.put(arr[0], rs.getString("reimbursement_amount"));
					}
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from project_emp_details where pro_id>0 and pro_id in("+sbProId.toString()+") order by pro_id");
				rs=pst.executeQuery();
				Map<String, String> hmProEmpRate = new HashMap<String, String>();
				while(rs.next()) {
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_HOUR", rs.getString("emp_actual_rate_per_hour"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_DAY", rs.getString("emp_actual_rate_per_day"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_MONTH", rs.getString("emp_actual_rate_per_month"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_HOUR", rs.getString("emp_rate_per_hour"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_DAY", rs.getString("emp_rate_per_day"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_MONTH", rs.getString("emp_rate_per_month"));
					
				}
//				System.out.println("hmProEmpRate ===>> " + hmProEmpRate);
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select org_id, calculation_type, days from cost_calculation_settings ");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===> " + pst);
				rs = pst.executeQuery();
				Map<String, String> hmOrgCalType = new HashMap<String, String>();
				while (rs.next()) {
					hmOrgCalType.put(rs.getString("org_id"), rs.getString("calculation_type"));
					hmOrgCalType.put(rs.getString("org_id")+"_DAYS", rs.getString("days"));
				}
				rs.close();
				pst.close();
				
				double dblProBudgetAmt = 0.0d;
				double dblProActualAmt = 0.0d;
				double dblBugedtedAmt = 0;
				for(int i = 0; alProjectId != null && i < alProjectId.size(); i++){
//					Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, (String)alProjectId.get(i));
					Map<String, String> hmProjectData = hmProjectPro.get((String)alProjectId.get(i));
	
					Map<String, String> hmProActualCostTime = new HashMap<String, String>();
					
					Map<String, String> hmProBillCost = new HashMap<String, String>();
					if(hmProjectData.get("PRO_BILLING_ACTUAL_TYPE")!=null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("M")) {
						hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con,request, CF, uF, (String)alProjectId.get(i), hmProjectData,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation,hmOrgCalType);
						hmProBillCost = CF.getMonthlyProjectBillableCostAndTime(con,request, CF, uF, (String)alProjectId.get(i), hmProjectData);
					} else {
						hmProActualCostTime = CF.getProjectActualCostAndTime(con, CF, uF, (String)alProjectId.get(i), hmProjectData, false, false,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation); //,hmOrgCalType
						hmProBillCost = CF.getProjectBillableCostAndTime(con,request, CF, uF, (String)alProjectId.get(i), hmProjectData, false, false);
					}
					
//					Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con, uF, (String)alProjectId.get(i), hmProjectData);
//					Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con,CF, uF, (String)alProjectId.get(i), hmProjectData,hmProEmpRate);
					
					if("F".equalsIgnoreCase(hmProjectData.get("PRO_BILL_TYPE"))) { 
						dblBugedtedAmt = uF.parseToDouble(hmProjectData.get("PRO_BILLING_AMOUNT"));
					 } else {
						 dblBugedtedAmt = uF.parseToDouble(hmProBillCost.get("proBillableCost"));
					 }
//					System.out.println((String)alProjectId.get(i)+ " --------- dblBugedtedAmt ===>> " + dblBugedtedAmt);
					
					double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get((String)alProjectId.get(i)));
					
//					double dblBugedtedAmt = uF.parseToDouble(hmProBudgetedCostAndTime.get("proBudgetedCost"));
					double dblActualAmt = uF.parseToDouble(hmProActualCostTime.get("proActualCost"));
					 
					dblProBudgetAmt += uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblBugedtedAmt));
					dblProActualAmt += uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma((dblActualAmt + dblReimbursement)));
				}
				
				double[] PRO_MONEY_DATA  = new double[2];
				String[] PRO_MONEY_LABEL  = new String[2];
				
				PRO_MONEY_DATA[0] = uF.parseToDouble(uF.formatIntoZeroWithOutComma(dblProActualAmt));
				PRO_MONEY_DATA[1] = uF.parseToDouble(uF.formatIntoZeroWithOutComma(dblProBudgetAmt));
				
				request.setAttribute("PRO_MONEY_KPI", new SemiCircleMeter().getSemiCircleChart(PRO_MONEY_DATA, PRO_MONEY_LABEL, "Money"));
				request.setAttribute("PRO_ACTUAL_MONEY_KPI", uF.formatIntoZeroWithOutComma(dblProActualAmt));
				request.setAttribute("PRO_BUDGET_MONEY_KPI", uF.formatIntoZeroWithOutComma(dblProBudgetAmt));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void getResourceTimeProjectKPI(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			if(uF.parseToInt(getProWorking()) == 0) {
				setProWorking("3");
			}
			
			String strProDate = null;
			if(uF.parseToInt(getProWorking()) == 1){
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getProWorking()) == 2){
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 180), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getProWorking()) == 3){
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 90), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getProWorking()) == 4){
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 30), DBDATE, DATE_FORMAT);
			} else {
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			}
			
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmPprojectClientMap = CF.getProjectClientMap(con, uF);
			if(hmPprojectClientMap == null) hmPprojectClientMap = new HashMap<String, String>();
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
			if(hmCustName == null) hmCustName = new HashMap<String, String>();
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			
			
			Map<String, String> hmPro = new HashMap<String, String>();
			List<String> alProjectId = new ArrayList<String>(); 
			
//			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", true, null, null, uF);
			
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 17-10-2022===	
			/*sbQuery.append("select * from projectmntnc pmc where pmc.pro_id > 0  and pmc.approve_status != 'blocked'  " +
					"and project_owner=? and start_date > ?");*/
			sbQuery.append("select * from projectmntnc pmc where pmc.pro_id > 0  and pmc.approve_status != 'blocked'  " +
					"and project_owners like '%,"+strSessionEmpId+",%' and start_date > ?");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setDate(1, uF.getDateFormat(strProDate, DATE_FORMAT));
		//===end parvez date: 17-10-2022===	
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			StringBuilder sbProId = null;
			Map<String, Map<String, String>> hmProjectPro = new HashMap<String, Map<String, String>>();
			while (rs.next()){
				hmPro.put("PRO_ID_"+rs.getString("pro_id"), rs.getString("pro_id"));
				hmPro.put("ACTUAL_CAL_TYPE_"+rs.getString("pro_id"), rs.getString("actual_calculation_type"));
				hmPro.put("BILLING_TYPE_"+rs.getString("pro_id"), rs.getString("billing_type"));
				hmPro.put("BILLING_AMOUNT_"+rs.getString("pro_id"), rs.getString("billing_amount"));
				
				if(!alProjectId.contains(rs.getString("pro_id"))) {
					alProjectId.add(rs.getString("pro_id"));
				}
				
				if(sbProId==null){
					sbProId=new StringBuilder();
					sbProId.append(rs.getString("pro_id"));
				} else {
					sbProId.append(","+rs.getString("pro_id"));
				}
				
				Map<String, String> hmProjectData = new HashMap<String, String>();
				hmProjectData.put("PRO_ID", rs.getString("pro_id"));
				hmProjectData.put("PRO_NAME", rs.getString("pro_name"));
				hmProjectData.put("PRO_CUSTOMER_NAME", uF.showData(hmPprojectClientMap.get(rs.getString("client_id")), ""));
				hmProjectData.put("PRO_CUST_SPOC_ID", rs.getString("poc"));
				hmProjectData.put("PRO_CUST_SPOC_NAME", uF.showData(hmCustName.get(rs.getString("poc")), ""));
				hmProjectData.put("PRO_OWNER_ID", rs.getString("project_owner"));
				hmProjectData.put("PRO_OWNER_NAME", uF.showData(hmEmpCodeName.get(rs.getString("project_owner")), ""));
				hmProjectData.put("PRO_BILLING_TYPE", CF.getBillinType(rs.getString("billing_type")));
				hmProjectData.put("PRO_BILL_TYPE", rs.getString("billing_type"));
				hmProjectData.put("PRO_BILLING_ACTUAL_TYPE", rs.getString("actual_calculation_type"));
				hmProjectData.put("PRO_BILLING_FREQUENCY", CF.getBillinFreq(rs.getString("billing_kind"), rs.getString("billing_type")));
				hmProjectData.put("PRO_BILL_FREQUENCY", rs.getString("billing_kind"));
				hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
				hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
				hmProjectData.put("PRO_SERVICE_ID", rs.getString("service"));
				hmProjectData.put("PRO_REPORT_CURR_ID", rs.getString("curr_id"));
				hmProjectData.put("PRO_BILLING_CURR_ID", rs.getString("billing_curr_id"));
				hmProjectData.put("PRO_BILLING_AMOUNT", rs.getString("billing_amount"));
				hmProjectData.put("PRO_ORG_ID", rs.getString("org_id"));
				
				hmProjectPro.put(rs.getString("pro_id"), hmProjectData);
				
			}
			rs.close();
			pst.close();
			
			/**
			 * Project KPI Time
			 * */
			
			if(sbProId!=null && sbProId.length()>0){
				Map<String, String> hmReimbursementAmountMap = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(reimbursement_amount) as reimbursement_amount, pro_id as group_type from emp_reimbursement where approval_2 = 1 " +
						"and pro_id in (select pmc.pro_id from projectmntnc pmc, activity_info ai where ai.pro_id = pmc.pro_id ");
					sbQuery.append(" and pmc.approve_status = 'approved'");
				sbQuery.append(" ) and pro_id in("+sbProId.toString()+") and reimbursement_type1 = 'P' group by pro_id");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst===>"+pst);  
				rs = pst.executeQuery();
				while (rs.next()) {
					String arr[] = null;
					if (rs.getString("group_type") != null) {
						arr = rs.getString("group_type").split(",");
						hmReimbursementAmountMap.put(arr[0], rs.getString("reimbursement_amount"));
					}
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from project_emp_details where pro_id>0 and pro_id in("+sbProId.toString()+") order by pro_id");
				rs=pst.executeQuery();
				Map<String, String> hmProEmpRate = new HashMap<String, String>();
				while(rs.next()) {
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_HOUR", rs.getString("emp_actual_rate_per_hour"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_DAY", rs.getString("emp_actual_rate_per_day"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_MONTH", rs.getString("emp_actual_rate_per_month"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_HOUR", rs.getString("emp_rate_per_hour"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_DAY", rs.getString("emp_rate_per_day"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_MONTH", rs.getString("emp_rate_per_month"));
					
				}
//				System.out.println("hmProEmpRate ===>> " + hmProEmpRate);
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select org_id, calculation_type, days from cost_calculation_settings ");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===> " + pst);
				rs = pst.executeQuery();
				Map<String, String> hmOrgCalType = new HashMap<String, String>();
				while (rs.next()) {
					hmOrgCalType.put(rs.getString("org_id"), rs.getString("calculation_type"));
					hmOrgCalType.put(rs.getString("org_id")+"_DAYS", rs.getString("days"));
				}
				rs.close();
				pst.close();
				double dblActualTimeTotal = 0.0d;
				double dblBudgetedTimeTotal = 0.0d;
				
//				System.out.println("alProjectId ===>> " + alProjectId);
				for(int i = 0; alProjectId != null && i < alProjectId.size(); i++) {
					
//					Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, (String)alProjectId.get(i));
					Map<String, String> hmProjectData = hmProjectPro.get((String)alProjectId.get(i));
	
					Map<String, String> hmProActualCostTime = new HashMap<String, String>();
					
//					if(hmProjectData.get("PRO_BILLING_ACTUAL_TYPE")!=null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("M")) {
//						hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con,request, CF, uF, (String)alProjectId.get(i), hmProjectData,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation,hmOrgCalType);
//					} else {
						hmProActualCostTime = CF.getProjectActualCostAndTime(con, CF, uF, (String)alProjectId.get(i), hmProjectData, false, false,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation); //,hmOrgCalType
//					}
					
//					Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con, uF, (String)alProjectId.get(i), hmProjectData);
					Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con,CF, uF, (String)alProjectId.get(i), hmProjectData,hmProEmpRate);

					double dblBugedtedTime = uF.parseToDouble(hmProBudgetedCostAndTime.get("proBudgetedTime"));
					double dblActualTime = uF.parseToDouble(hmProActualCostTime.get("proActualTime"));
					 
					dblBudgetedTimeTotal += uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblBugedtedTime));
					dblActualTimeTotal += uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma((dblActualTime)));
				}
//				System.out.println("dblBudgetedTimeTotal ===>> " + dblBudgetedTimeTotal);
//				System.out.println("dblActualTimeTotal ===>> " + dblActualTimeTotal);
				
				double[] PRO_TIME_DATA  = new double[2];
				String[] PRO_TIME_LABEL  = new String[2];
				
				PRO_TIME_DATA[0] = uF.parseToDouble(uF.formatIntoZeroWithOutComma(dblActualTimeTotal));
				PRO_TIME_DATA[1] = uF.parseToDouble(uF.formatIntoZeroWithOutComma(dblBudgetedTimeTotal));
				
				request.setAttribute("PRO_TIME_KPI", new SemiCircleMeter().getSemiCircleChart(PRO_TIME_DATA, PRO_TIME_LABEL, "Time"));
				request.setAttribute("PRO_ACTUAL_TIME_KPI", uF.formatIntoZeroWithOutComma(dblActualTimeTotal));
				request.setAttribute("PRO_BUDGET_TIME_KPI", uF.formatIntoZeroWithOutComma(dblBudgetedTimeTotal));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void getResourceTimeWorkKPI(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			if(uF.parseToInt(getTaskWorking()) == 0) {
				setTaskWorking("3");
			}
			
			String strTaskDate = null;
			if(uF.parseToInt(getTaskWorking()) == 1){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getTaskWorking()) == 2){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 180), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getTaskWorking()) == 3){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 90), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getTaskWorking()) == 4){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 30), DBDATE, DATE_FORMAT);
			} else {
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			}
			
			Map<String, String> hmTaskPro = new HashMap<String, String>();
			List<String> alTaskId = new ArrayList<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select a.* from (select task_id,parent_task_id,pro_id,start_date,idealtime from activity_info where resource_ids like '%,"+strSessionEmpId+",%' and task_id " +
					"not in (select parent_task_id from activity_info where resource_ids like '%,"+strSessionEmpId+",%' and parent_task_id is not null) and " +
					"task_accept_status = 1) a, projectmntnc pmc where pmc.pro_id=a.pro_id and (parent_task_id in (select task_id from activity_info " +
					"where resource_ids like '%,"+strSessionEmpId+",%') or parent_task_id = 0) and a.start_date > ?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strTaskDate, DATE_FORMAT));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()){
				hmTaskPro.put("PRO_ID_"+rs.getString("task_id"), rs.getString("pro_id"));
				hmTaskPro.put("TASK_ID_"+rs.getString("task_id"), rs.getString("task_id"));
				hmTaskPro.put("IDEAL_TIME_"+rs.getString("task_id"), rs.getString("idealtime"));
				
				if(!alTaskId.contains(rs.getString("task_id"))) {
					alTaskId.add(rs.getString("task_id"));
				}
			}
			rs.close();
			pst.close();
			/**
			 * Work KPI Time
			 * */
			double dblActualTimeTotal = 0.0d;
			double dblBudgetedTimeTotal = 0.0d;
//			System.out.println("alTaskId =====>> " + alTaskId);
//			System.out.println("hmTaskPro ===>> " + hmTaskPro);
			for(int i = 0; alTaskId != null && i < alTaskId.size(); i++) {
				String strProId =  hmTaskPro.get("PRO_ID_"+alTaskId.get(i));
				String strTaskId =  hmTaskPro.get("TASK_ID_"+alTaskId.get(i));
				
				pst = con.prepareStatement("SELECT * FROM projectmntnc where pro_id = ?");
				pst.setInt(1, uF.parseToInt(strProId));
				rs = pst.executeQuery();
				String strActualBilling = null;
				while(rs.next()) {
					strActualBilling = rs.getString("actual_calculation_type");
				}
				rs.close();
				pst.close();
				
				Map<String, String> empcostMp = CF.getProjectEmpActualRates(con, uF, strProId, strActualBilling);
				double dblEmpRate = uF.parseToDouble(uF.getTaskResourcesAvgCostOrBillRate(empcostMp, ""+strSessionEmpId));
				double dblIdealTime = uF.parseToDouble(hmTaskPro.get("IDEAL_TIME_"+alTaskId.get(i)));
				
				double empBudgetedTime = 0;
				if(dblIdealTime > 0) {
					if(strActualBilling != null && strActualBilling.equals("M")) {
						empBudgetedTime = ((dblIdealTime *30) * 8);
					} else if(strActualBilling != null && strActualBilling.equals("D")) {
						empBudgetedTime = (dblIdealTime * 8);
					} else if(strActualBilling != null && strActualBilling.equals("H")) {
						empBudgetedTime = dblIdealTime;
					}
				}
				
				pst = con.prepareStatement("SELECT sum(actual_hrs) as actual_hrs FROM task_activity where activity_id = ? and emp_id=?");
				pst.setInt(1, uF.parseToInt(strTaskId));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				rs = pst.executeQuery();
				double empActualTime = 0;
				while(rs.next()) {
					empActualTime = uF.parseToDouble(rs.getString("actual_hrs"));
				}
				rs.close();
				pst.close();
				
				dblActualTimeTotal += empActualTime;
				dblBudgetedTimeTotal += empBudgetedTime;
			}
			
			
			double[] TASK_TIME_DATA  = new double[2];
			String[] TASK_TIME_LABEL  = new String[2];
			
			TASK_TIME_DATA[0] = uF.parseToDouble(uF.formatIntoZeroWithOutComma(dblActualTimeTotal));
			TASK_TIME_DATA[1] = uF.parseToDouble(uF.formatIntoZeroWithOutComma(dblBudgetedTimeTotal));
			
			request.setAttribute("TASK_TIME_KPI", new SemiCircleMeter().getSemiCircleChart(TASK_TIME_DATA, TASK_TIME_LABEL, "Time"));
			request.setAttribute("TASK_ACTUAL_TIME_KPI", uF.formatIntoZeroWithOutComma(dblActualTimeTotal));
			request.setAttribute("TASK_BUDGET_TIME_KPI", uF.formatIntoZeroWithOutComma(dblBudgetedTimeTotal));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void getRequestAndAlerts(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from taskrig_user_alerts ");
			if(strUserType != null && strUserType.equalsIgnoreCase(CUSTOMER)){
				sbQuery.append("where customer_id=?");
			} else {
				sbQuery.append("where resource_id=?");
			}
			sbQuery.append(" order by alerts_id desc limit 25");
			pst = con.prepareStatement(sbQuery.toString());		
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			List<String> alNotifications = new ArrayList<String>();
			while (rs.next()) {
				if(rs.getString("alert_data") != null && !rs.getString("alert_data").equals("null") && !rs.getString("alert_data").equals("") && 
					rs.getString("alert_action") != null && !rs.getString("alert_action").equals("null") && !rs.getString("alert_action").equals("")) {
					
					StringBuilder sbNotifications = new StringBuilder();
					sbNotifications.append("<a href=\""+rs.getString("alert_action")+"?alertID="+rs.getString("alerts_id")+"\">");
					sbNotifications.append(rs.getString("alert_data"));
					sbNotifications.append("</a>");
					
					alNotifications.add(sbNotifications.toString());
				 }
			 }
			 rs.close();
			 pst.close();
			 
			 request.setAttribute("alNotifications", alNotifications);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void getTeamTaskDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 17-10-2022===	
//			sbQuery.append("select pro_id from projectmntnc where project_owner=? and approve_status='n' order by pro_id");
			sbQuery.append("select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%' and approve_status='n' order by pro_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
		//===end parvez date: 17-10-2022===	
			rs = pst.executeQuery();
			Set<String> setProId = new HashSet<String>();
			while(rs.next()){
				setProId.add(rs.getString("pro_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select pmt.pro_id from project_emp_details ped,projectmntnc pmt where ped.pro_id = pmt.pro_id " +
					"and ped.emp_id=? and pmt.approve_status='n' order by pmt.pro_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			while(rs.next()){
				setProId.add(rs.getString("pro_id"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbProId = null;
			for(String strProId : setProId){
				if(sbProId == null){
					sbProId = new StringBuilder();
					sbProId.append(strProId);
				} else {
					sbProId.append(","+strProId);
				}
			}
			
			List<Map<String, String>> alTeamMember = new ArrayList<Map<String, String>>();
			if(sbProId !=null){
				
				sbQuery = new StringBuilder();
				sbQuery.append("select * from activity_info where pro_id in("+sbProId.toString()+") and resource_ids like '%,"+strSessionEmpId+",%'");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				boolean flagTask = false;
				if(rs.next()){
					flagTask = true;
				}
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select epd.* from (select distinct(ped.emp_id) as emp_id from project_emp_details ped,projectmntnc pmt " +
						"where ped.pro_id = pmt.pro_id and ped.pro_id in("+sbProId.toString()+")) a, employee_personal_details epd," +
						"employee_official_details eod where a.emp_id=epd.emp_per_id and a.emp_id=eod.emp_id and epd.emp_per_id=eod.emp_id " +
						"and epd.is_alive=true ");
				if(!flagTask){
					sbQuery.append(" and eod.emp_id!="+uF.parseToInt(strSessionEmpId));
				}
				sbQuery.append(" order by epd.emp_fname,epd.emp_lname");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst=====>"+pst);
				rs = pst.executeQuery();
				List<String> alEmp = new ArrayList<String>();
				while(rs.next()){
					String strShortLName = (rs.getString("emp_lname") !=null && !rs.getString("emp_lname").trim().equals("")) ? String.valueOf(rs.getString("emp_lname").trim().charAt(0)) : "";
					String strEmpShortLName = uF.showData(rs.getString("emp_fname"), "")+" "+strShortLName.toUpperCase();
				
					
				//	String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
				
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
				
					String strEmpName = rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname");
					String strEmpImage = uF.showData(rs.getString("emp_image"), "");
					
					
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("EMP_ID", rs.getString("emp_per_id"));
					hmInner.put("EMP_SHORT_NAME", strEmpShortLName);
					hmInner.put("EMP_NAME", strEmpName); 
					hmInner.put("EMP_IMAGE", strEmpImage);
					
					alTeamMember.add(hmInner);
					
					alEmp.add(rs.getString("emp_per_id"));
				}
				rs.close();
				pst.close();
				
				int nMemAssigned = 0;
				int nMemUnAssigned = 0;
				for(int i = 0; alEmp!=null && i < alEmp.size(); i++){
					sbQuery = new StringBuilder();
					sbQuery.append("select * from activity_info where pro_id in("+sbProId.toString()+") and resource_ids like '%,"+alEmp.get(i)+",%'");
					pst = con.prepareStatement(sbQuery.toString());
					rs = pst.executeQuery();
					boolean flag = false;
					if(rs.next()){
						flag = true;
					}
					rs.close();
					pst.close();
					
					if(flag){
						nMemAssigned++;
					} else {
						nMemUnAssigned++;
					}
				}
				request.setAttribute("nMemAssigned", ""+nMemAssigned);
				request.setAttribute("nMemUnAssigned", ""+nMemUnAssigned);
				
				int nTotalTask = 0;
				sbQuery = new StringBuilder();
				sbQuery.append("select count(task_id) as cnt from activity_info where pro_id in("+sbProId.toString()+")");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				while(rs.next()){
					nTotalTask = rs.getInt("cnt");
				}
				rs.close();
				pst.close();
				request.setAttribute("nTotalTask", ""+nTotalTask);
				
				int nTaskAssigned = 0;
				sbQuery = new StringBuilder();
				sbQuery.append("select count(task_id) as cnt from activity_info where pro_id in("+sbProId.toString()+") " +
						"and (resource_ids is not null and resource_ids !='' and resource_ids!=',,')");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				while(rs.next()){
					nTaskAssigned = rs.getInt("cnt");
				}
				rs.close();
				pst.close();
				request.setAttribute("nTaskAssigned", ""+nTaskAssigned);
				
				int nTaskUnAssigned = 0;
				sbQuery = new StringBuilder();
				sbQuery.append("select count(task_id) as cnt from activity_info where pro_id in("+sbProId.toString()+") " +
						"and (resource_ids is null or resource_ids ='' or resource_ids=',,')");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				while(rs.next()){
					nTaskUnAssigned = rs.getInt("cnt");
				}
				rs.close();
				pst.close();
				request.setAttribute("nTaskUnAssigned", ""+nTaskUnAssigned);
				
				sbQuery = new StringBuilder();
				sbQuery.append("select a.*,pmt.pro_name from (select count(task_id) as cnt,pro_id from activity_info " +
						"where pro_id in("+sbProId.toString()+") group by pro_id) a, projectmntnc pmt where a.pro_id=pmt.pro_id");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				StringBuilder sbProTaskPie	= new StringBuilder();
				while(rs.next()){
					String strcnt = uF.showData(rs.getString("cnt"), "0");
					String strProName = uF.showData(rs.getString("pro_name"), "");
					
					sbProTaskPie.append("{'Project':'"+strProName.replaceAll("[^a-zA-Z0-9]", "")+"', " +
							"'cnt': "+uF.parseToInt(strcnt)+"},");
				}
				rs.close();
				pst.close();
				
				if(sbProTaskPie.length()>1) {
					sbProTaskPie.replace(0, sbProTaskPie.length(), sbProTaskPie.substring(0, sbProTaskPie.length()-1));
		        }
				request.setAttribute("sbProTaskPie", sbProTaskPie.toString());
			}
			request.setAttribute("alTeamMember", alTeamMember);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void getProjectTaskCount(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 17-10-2022===	
			/*sbQuery.append("select count(pro_id) as cnt from projectmntnc where approve_status='n' and (project_owner="+uF.parseToInt(strSessionEmpId)+" " +
				"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead =true)) ");*/
			sbQuery.append("select count(pro_id) as cnt from projectmntnc where approve_status='n' and (project_owners like '%,"+strSessionEmpId+",%' " +
					"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead =true)) ");
		//===end parvez date: 17-10-2022===	
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst My Project Dashboard =====>> " + pst);
			rs = pst.executeQuery();
			int nProject = 0;
			while(rs.next()){
				nProject = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
//			sbQuery.append("select count(task_id) as cnt from activity_info where approve_status='n' and pro_id in (select pro_id from projectmntnc where approve_status='n'");
//			sbQuery.append(" and (project_owner="+uF.parseToInt(strSessionEmpId)+" or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+"))");
//			sbQuery.append(" ) ");
			sbQuery.append("select count(task_id) as cnt from activity_info where approve_status='n' " +
					"and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") and resource_ids like '%,"+strSessionEmpId+",%'");
			
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst My Project Dashboard =====>> " + pst); 
			rs = pst.executeQuery();
			int nTask = 0;
			while(rs.next()){
				nTask = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("nProject", ""+nProject);
			request.setAttribute("nTask", ""+nTask);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	private void getProjectWorkProgressDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(currDate+"", DBDATE, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(currDate+"", DBDATE, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(currDate+"", DBDATE, "yyyy")));
//			
//            int nMonthStart = cal.getActualMinimum(Calendar.DATE);
//			int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
//			int nMonth = cal.get(Calendar.MONTH);
			
//			String strStartDate =  (nMonthStart < 10 ? "0"+nMonthStart : nMonthStart) + "/" + ((cal.get(Calendar.MONTH) + 1) < 10 ? "0"+(cal.get(Calendar.MONTH) + 1):(cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
//			String strEndDate =  (nMonthEnd < 10 ? "0"+nMonthEnd : nMonthEnd) + "/" + ((cal.get(Calendar.MONTH) + 1) < 10 ? "0"+(cal.get(Calendar.MONTH) + 1):(cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
			
			List<List<String>> weekdates = new ArrayList<List<String>>();
			uF.getMonthWeeksDate(weekdates,""+((cal.get(Calendar.MONTH) + 1) < 10 ? "0"+(cal.get(Calendar.MONTH) + 1):(cal.get(Calendar.MONTH) + 1)),""+cal.get(Calendar.YEAR),DATE_FORMAT);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from projectmntnc where pro_id > 0 ");
		//===start parvez date: 17-10-2022===	
			/*sbQuery.append(" and (project_owner="+uF.parseToInt(strSessionEmpId)+" " +
					"or pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(strSessionEmpId)+"))");*/
			sbQuery.append(" and (project_owners like '%,"+strSessionEmpId+",%' " +
					"or pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(strSessionEmpId)+"))");
		//===end parvez date: 17-10-2022===	
			sbQuery.append("order by pro_name, pro_id desc limit 5");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmProject = new LinkedHashMap<String, String>();
			while(rs.next()) {
				hmProject.put(rs.getString("pro_id"), uF.showData(rs.getString("pro_name"), ""));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmCompleteTask = new HashMap<String, String>();
            Map<String, String> hmActiveTask = new HashMap<String, String>();
            Map<String, String> hmOverdueTask = new HashMap<String, String>();
            
			if(hmProject != null && hmProject.size() > 0){
                Iterator<String> it = hmProject.keySet().iterator();
				while(it.hasNext()){
					String strProId = it.next();
					StringBuilder sbCompleteCount = null;
					StringBuilder sbActiveCount = null;
					StringBuilder sbOverDueCount = null;  
					int nCompleteCnt=0;
					int nActiveCnt=0;
					int nOverdueCnt=0;
					int x = 0;
					for(int i = 0; weekdates!=null && i < weekdates.size();i++){
						List<String> week = weekdates.get(i);
						x++;
						/**Complete Task Count
						 * */
						pst = con.prepareStatement("select count(*) as cnt,ai.pro_id from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
								"and ai.pro_id=? and end_date between ? and ? and ai.approve_status='approved' " +
								"group by ai.pro_id,ai.end_date order by ai.pro_id,ai.end_date");
						pst.setInt(1, uF.parseToInt(strProId));
						pst.setDate(2, uF.getDateFormat(week.get(0), DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(week.get(1), DATE_FORMAT));
//						System.out.println("pst My Project Dashboard ======>> " + pst);
						rs = pst.executeQuery();
						int nComplete = 0;
						while(rs.next()){
							nComplete = rs.getInt("cnt");
						}
						rs.close();
						pst.close();
						
						if(sbCompleteCount == null){
							sbCompleteCount = new StringBuilder();
							sbCompleteCount.append(""+nComplete);
						} else {
							sbCompleteCount.append(","+nComplete);
						}
						nCompleteCnt +=nComplete;
						
						/**Complete Task Count end
						 * */
						
						/**Active Task Count
						 * */
						pst = con.prepareStatement("select count(*) as cnt from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
								"and ai.approve_status='n' and ai.pro_id=? and ai.deadline >= ? and ai.start_date < ?");
						pst.setInt(1, uF.parseToInt(strProId));
						pst.setDate(2, uF.getDateFormat(week.get(1), DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(week.get(1), DATE_FORMAT));
						rs = pst.executeQuery();
						int nActive = 0;
						while(rs.next()){
							nActive = rs.getInt("cnt");
						}
						rs.close();
						pst.close();
						
						if(sbActiveCount == null){
							sbActiveCount = new StringBuilder();
							sbActiveCount.append(""+nActive);
						} else {
							sbActiveCount.append(","+nActive);
						}
						nActiveCnt +=nActive;
						
						/**Active Task Count
						 * */
						
						/**Overdue Task Count
						 * */
						pst = con.prepareStatement("select count(*) as cnt from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
								"and ai.approve_status='n' and ai.pro_id=? and ai.deadline < ?");
						pst.setInt(1, uF.parseToInt(strProId));
						pst.setDate(2, uF.getDateFormat(week.get(1), DATE_FORMAT));
						rs = pst.executeQuery();
						int nOverdue = 0;
						while(rs.next()){
							nOverdue = rs.getInt("cnt");
						}
						rs.close();
						pst.close();
						
						if(sbOverDueCount == null){
							sbOverDueCount = new StringBuilder();
							sbOverDueCount.append(""+nOverdue);
						} else {
							sbOverDueCount.append(","+nOverdue);
						}
						nOverdueCnt +=nOverdue;
						
						/**Overdue Task Count
						 * */
					}
					
					hmCompleteTask.put(strProId+"_COMPLETE", sbCompleteCount.toString());
					hmCompleteTask.put(strProId+"_COMPLETE_COUNT", ""+nCompleteCnt);
					
					hmActiveTask.put(strProId+"_ACTIVE", sbActiveCount.toString());
					hmActiveTask.put(strProId+"_ACTIVE_COUNT", ""+nActiveCnt);
					
					hmOverdueTask.put(strProId+"_OVERDUE", sbOverDueCount.toString());
					hmOverdueTask.put(strProId+"_OVERDUE_COUNT", ""+nOverdueCnt);
                }
                
			}
            
			request.setAttribute("hmProject", hmProject);
			request.setAttribute("hmCompleteTask", hmCompleteTask);
			request.setAttribute("hmActiveTask", hmActiveTask);
			request.setAttribute("hmOverdueTask", hmOverdueTask);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void getWeeklyWorkProgress(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(currDate+"", DBDATE, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(currDate+"", DBDATE, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(currDate+"", DBDATE, "yyyy")));
			
            int nMonthStart = cal.getActualMinimum(Calendar.DATE);
			int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
			int nMonth = cal.get(Calendar.MONTH);
			
			String strStartDate =  (nMonthStart < 10 ? "0"+nMonthStart : nMonthStart) + "/" + ((cal.get(Calendar.MONTH) + 1) < 10 ? "0"+(cal.get(Calendar.MONTH) + 1):(cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
			String strEndDate =  (nMonthEnd < 10 ? "0"+nMonthEnd : nMonthEnd) + "/" + ((cal.get(Calendar.MONTH) + 1) < 10 ? "0"+(cal.get(Calendar.MONTH) + 1):(cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
			
			List<List<String>> weekdates = new ArrayList<List<String>>();
			uF.getMonthWeeksDate(weekdates,""+((cal.get(Calendar.MONTH) + 1) < 10 ? "0"+(cal.get(Calendar.MONTH) + 1):(cal.get(Calendar.MONTH) + 1)),""+cal.get(Calendar.YEAR),DATE_FORMAT);
			
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 17-10-2022===	
			sbQuery.append("select * from projectmntnc where pro_id > 0 ");
			/*sbQuery.append(" and (project_owner="+uF.parseToInt(strSessionEmpId)+" " +
					"or pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(strSessionEmpId)+"))");*/
			sbQuery.append(" and (project_owners like '%,"+strSessionEmpId+",%' " +
					"or pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(strSessionEmpId)+"))");
		//===end parvez date: 17-10-2022===	
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
//			Map<String, String> hmProject = new LinkedHashMap<String, String>();
			StringBuilder sbProId = null;
			while(rs.next()){
//				hmProject.put(rs.getString("pro_id"), uF.showData(rs.getString("pro_name"), ""));
				if(sbProId==null){
					sbProId = new StringBuilder();
					sbProId.append(rs.getString("pro_id"));
				} else {
					sbProId.append(","+rs.getString("pro_id"));
				}
			}
			rs.close(); 
			pst.close();
			
			Map<String, String> hmCompleteTotalCnt = new HashMap<String, String>();
	        Map<String, String> hmActiveTotalCnt = new HashMap<String, String>();
	        Map<String, String> hmOverdueTotalCnt = new HashMap<String, String>();
	        
	        if(sbProId != null && sbProId.length() > 0) {
				int x = 0;
				for(int i = 0; weekdates!=null && i < weekdates.size();i++){
					List<String> week = weekdates.get(i);
					x++;
					/**Complete Task Count
					 * */
					pst = con.prepareStatement("select count(*) as cnt,ai.pro_id from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
							"and ai.pro_id in ("+sbProId.toString()+") and end_date between ? and ? and ai.approve_status='approved' " +
							"group by ai.pro_id,ai.end_date order by ai.pro_id,ai.end_date");
					pst.setDate(1, uF.getDateFormat(week.get(0), DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(week.get(1), DATE_FORMAT));
//					System.out.println("pst My Project Dashboard ======>> " + pst);
					rs = pst.executeQuery();
					int nComplete = 0;
					while(rs.next()){
						nComplete += rs.getInt("cnt");
					}
					rs.close();
					pst.close();
					
					int nCompleteTotalCnt = uF.parseToInt(hmCompleteTotalCnt.get(x+"week"));
					nCompleteTotalCnt +=nComplete;
					hmCompleteTotalCnt.put(x+"week", ""+nCompleteTotalCnt);
					
					/**Complete Task Count end
					 * */
					
					/**Active Task Count
					 * */
					pst = con.prepareStatement("select count(*) as cnt from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
							"and ai.approve_status='n' and ai.pro_id in ("+sbProId.toString()+") and ai.deadline >= ? and ai.start_date < ?");
					pst.setDate(1, uF.getDateFormat(week.get(1), DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(week.get(1), DATE_FORMAT));
					rs = pst.executeQuery();
//					System.out.println("pst======>"+pst);
					int nActive = 0;
					while(rs.next()){
						nActive += rs.getInt("cnt");
					}
					rs.close();
					pst.close();
					
					int nActiveTotalCnt = uF.parseToInt(hmActiveTotalCnt.get(x+"week"));
					nActiveTotalCnt +=nActive;
					hmActiveTotalCnt.put(x+"week", ""+nActiveTotalCnt);
					
					/**Active Task Count
					 * */
					
					/**Overdue Task Count
					 * */
					pst = con.prepareStatement("select count(*) as cnt from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
							"and ai.approve_status='n' and ai.pro_id in ("+sbProId.toString()+") and ai.deadline < ?");
					pst.setDate(1, uF.getDateFormat(week.get(1), DATE_FORMAT));
//					System.out.println("pst======>"+pst);
					rs = pst.executeQuery();
					int nOverdue = 0;
					while(rs.next()){
						nOverdue += rs.getInt("cnt");
					}
					rs.close();
					pst.close();
					
					int nOverdueTotalCnt = uF.parseToInt(hmOverdueTotalCnt.get(x+"week"));
					nOverdueTotalCnt +=nOverdue;
					hmOverdueTotalCnt.put(x+"week", ""+nOverdueTotalCnt);
					
					/**Overdue Task Count
					 * */
				}
                
			}
	        
//			if(hmProject != null && hmProject.size() > 0){
//                Iterator<String> it = hmProject.keySet().iterator();
//				while(it.hasNext()){
//					String strProId = it.next();
//					
//					int x = 0;
//					for(int i = 0; weekdates!=null && i < weekdates.size();i++){
//						List<String> week = weekdates.get(i);
//						x++;
//						/**Complete Task Count
//						 * */
//						pst = con.prepareStatement("select count(*) as cnt,ai.pro_id from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
//								"and ai.pro_id=? and end_date between ? and ? and ai.approve_status='approved' " +
//								"group by ai.pro_id,ai.end_date order by ai.pro_id,ai.end_date");
//						pst.setInt(1, uF.parseToInt(strProId));
//						pst.setDate(2, uF.getDateFormat(week.get(0), DATE_FORMAT));
//						pst.setDate(3, uF.getDateFormat(week.get(1), DATE_FORMAT));
////						System.out.println("pst======>"+pst);
//						rs = pst.executeQuery();
//						int nComplete = 0;
//						while(rs.next()){
//							nComplete = rs.getInt("cnt");
//						}
//						rs.close();
//						pst.close();
//						
//						int nCompleteTotalCnt = uF.parseToInt(hmCompleteTotalCnt.get(x+"week"));
//						nCompleteTotalCnt +=nComplete;
//						hmCompleteTotalCnt.put(x+"week", ""+nCompleteTotalCnt);
//						
//						/**Complete Task Count end
//						 * */
//						
//						/**Active Task Count
//						 * */
//						pst = con.prepareStatement("select count(*) as cnt from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
//								"and ai.approve_status='n' and ai.pro_id=? and ai.deadline >= ? and ai.start_date < ?");
//						pst.setInt(1, uF.parseToInt(strProId));
//						pst.setDate(2, uF.getDateFormat(week.get(1), DATE_FORMAT));
//						pst.setDate(3, uF.getDateFormat(week.get(1), DATE_FORMAT));
//						rs = pst.executeQuery();
//						int nActive = 0;
//						while(rs.next()){
//							nActive = rs.getInt("cnt");
//						}
//						rs.close();
//						pst.close();
//						
//						int nActiveTotalCnt = uF.parseToInt(hmActiveTotalCnt.get(x+"week"));
//						nActiveTotalCnt +=nActive;
//						hmActiveTotalCnt.put(x+"week", ""+nActiveTotalCnt);
//						
//						/**Active Task Count
//						 * */
//						
//						/**Overdue Task Count
//						 * */
//						pst = con.prepareStatement("select count(*) as cnt from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
//								"and ai.approve_status='n' and ai.pro_id=? and ai.deadline < ?");
//						pst.setInt(1, uF.parseToInt(strProId));
//						pst.setDate(2, uF.getDateFormat(week.get(1), DATE_FORMAT));
//						rs = pst.executeQuery();
//						int nOverdue = 0;
//						while(rs.next()){
//							nOverdue = rs.getInt("cnt");
//						}
//						rs.close();
//						pst.close();
//						
//						int nOverdueTotalCnt = uF.parseToInt(hmOverdueTotalCnt.get(x+"week"));
//						nOverdueTotalCnt +=nOverdue;
//						hmOverdueTotalCnt.put(x+"week", ""+nOverdueTotalCnt);
//						
//						/**Overdue Task Count
//						 * */
//					}
//                }
//                
//			}
			
			StringBuilder sbWork = new StringBuilder();
			int x = 0;
			for(int i = 0; weekdates!=null && i < weekdates.size();i++){
				x++;
				sbWork.append("{'week':'"+x+"wk', " +
						"'completed': "+uF.parseToInt(hmCompleteTotalCnt.get(x+"week"))+"," +
						"'active': "+uF.parseToInt(hmActiveTotalCnt.get(x+"week"))+"," +
						"'overdue': "+uF.parseToInt(hmOverdueTotalCnt.get(x+"week"))+"},");
            }
            if(sbWork.length()>1) {
				sbWork.replace(0, sbWork.length(), sbWork.substring(0, sbWork.length()-1));
            }
			
			request.setAttribute("sbWork", sbWork.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	private void getProjectPerformanceDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
//			Map<String, String> hmEmpNMap = CF.getEmpNameMap(con, null, null);
			
//			Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
//			Calendar cal = GregorianCalendar.getInstance();
//			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(currDate+"", DBDATE, "dd")));
//			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(currDate+"", DBDATE, "MM")) - 1);
//			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(currDate+"", DBDATE, "yyyy")));
			
//            int nMonthStart = cal.getActualMinimum(Calendar.DATE);
//			int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
//			int nMonth = cal.get(Calendar.MONTH);
			
//			String strStartDate =  (nMonthStart < 10 ? "0"+nMonthStart : nMonthStart) + "/" + ((cal.get(Calendar.MONTH) + 1) < 10 ? "0"+(cal.get(Calendar.MONTH) + 1):(cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
//			String strEndDate =  (nMonthEnd < 10 ? "0"+nMonthEnd : nMonthEnd) + "/" + ((cal.get(Calendar.MONTH) + 1) < 10 ? "0"+(cal.get(Calendar.MONTH) + 1):(cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
			
//			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", false, strStartDate, strEndDate, uF);
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmPprojectClientMap = CF.getProjectClientMap(con, uF);
			if(hmPprojectClientMap == null) hmPprojectClientMap = new HashMap<String, String>();
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
			if(hmCustName == null) hmCustName = new HashMap<String, String>();
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			
			/*Map<String, String> hmReimbursementAmountMap = new HashMap<String, String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(reimbursement_amount) as reimbursement_amount, pro_id as group_type from emp_reimbursement where approval_2 = 1 " +
					"and pro_id in (select pmc.pro_id from projectmntnc pmc, activity_info ai where ai.pro_id = pmc.pro_id ");
				sbQuery.append(" and pmc.approve_status = 'approved'");
			if (strStartDate != null && strEndDate != null && !strStartDate.equalsIgnoreCase(LABEL_FROM_DATE) && !strEndDate.equalsIgnoreCase(LABEL_TO_DATE)) {
				sbQuery.append(" and from_date between '" + uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE) + "' and '"
						+ uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE) + "' ");
			}
			sbQuery.append(" ) and reimbursement_type1 = 'P' group by pro_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst My Project Dashboard ===>> " + pst);  
			rs = pst.executeQuery();
			while (rs.next()) {
				String arr[] = null;
				if (rs.getString("group_type") != null) {
					arr = rs.getString("group_type").split(",");
					hmReimbursementAmountMap.put(arr[0], rs.getString("reimbursement_amount"));
				}
			}
			rs.close();
			pst.close();*/
			
		//===start parvez date: 17-10-2022===	
			/*pst = con.prepareStatement("select * from project_emp_details where pro_id>0 and pro_id in (select pro_id from projectmntnc pmc where pmc.pro_id > 0 " +
				"and pmc.approve_status = 'n' and (pmc.project_owner="+uF.parseToInt(strSessionEmpId)+" or pmc.pro_id in (select pro_id from project_emp_details " +
				"where _isteamlead =true and emp_id="+uF.parseToInt(strSessionEmpId)+")) order by pmc.pro_id desc limit 5) order by pro_id");*/
			pst = con.prepareStatement("select * from project_emp_details where pro_id>0 and pro_id in (select pro_id from projectmntnc pmc where pmc.pro_id > 0 " +
					"and pmc.approve_status = 'n' and (pmc.project_owners like '%,"+strSessionEmpId+",%' or pmc.pro_id in (select pro_id from project_emp_details " +
					"where _isteamlead =true and emp_id="+uF.parseToInt(strSessionEmpId)+")) order by pmc.pro_id desc limit 5) order by pro_id");
		//===end parvez date: 17-10-2022===	
			rs=pst.executeQuery();
			Map<String, String> hmProEmpRate = new HashMap<String, String>();
			while(rs.next()) {
				hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_HOUR", rs.getString("emp_actual_rate_per_hour"));
				hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_DAY", rs.getString("emp_actual_rate_per_day"));
				hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_MONTH", rs.getString("emp_actual_rate_per_month"));
				hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_HOUR", rs.getString("emp_rate_per_hour"));
				hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_DAY", rs.getString("emp_rate_per_day"));
				hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_MONTH", rs.getString("emp_rate_per_month"));
			}
//			System.out.println("hmProEmpRate ===>> " + hmProEmpRate);
			rs.close();
			pst.close();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select org_id, calculation_type, days from cost_calculation_settings ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hmOrgCalType = new HashMap<String, String>();
			while (rs.next()) {
				hmOrgCalType.put(rs.getString("org_id"), rs.getString("calculation_type"));
				hmOrgCalType.put(rs.getString("org_id")+"_DAYS", rs.getString("days"));
			}
			
			sbQuery = new StringBuilder();
		//===start parvez date: 17-10-2022===	
			/*sbQuery.append("select sum(ai.completed) as completed, count(task_id) as taskCnt, ai.pro_id from activity_info ai, projectmntnc pmc " +
				" where ai.pro_id=pmc.pro_id and ai.parent_task_id = 0 and task_accept_status != -1 and ai.pro_id in (select pro_id from activity_info " +
				"where resource_ids like '%"+strSessionEmpId+"%') and ai.pro_id in (select pro_id from projectmntnc pmc where pmc.pro_id > 0  and pmc.approve_status = 'n' and (pmc.project_owner="+uF.parseToInt(strSessionEmpId)+" " +
				" or pmc.pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(strSessionEmpId)+")) order by pmc.pro_id desc limit 5) group by ai.pro_id");*/
			sbQuery.append("select sum(ai.completed) as completed, count(task_id) as taskCnt, ai.pro_id from activity_info ai, projectmntnc pmc " +
					" where ai.pro_id=pmc.pro_id and ai.parent_task_id = 0 and task_accept_status != -1 and ai.pro_id in (select pro_id from activity_info " +
					"where resource_ids like '%"+strSessionEmpId+"%') and ai.pro_id in (select pro_id from projectmntnc pmc where pmc.pro_id > 0  and pmc.approve_status = 'n' and (pmc.project_owners like '%,"+strSessionEmpId+",%' " +
					" or pmc.pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(strSessionEmpId)+")) order by pmc.pro_id desc limit 5) group by ai.pro_id");
		//===end parvez date: 17-10-2022===	
//			sbQuery.append("select ai.* from activity_info ai, projectmntnc pmc where ai.pro_id=pmc.pro_id and " +
//				"ai.parent_task_id = 0 and task_accept_status != -1 and ai.resource_ids like '%"+strSessionEmpId+"%'");
			pst = con.prepareStatement(sbQuery.toString()); 
			rs = pst.executeQuery();
			Map<String, String> hmProCompletedStatus = new HashMap<String, String>();
			while (rs.next()) {
				double taskCompleted = uF.parseToDouble(rs.getString("completed")) / uF.parseToDouble(rs.getString("taskCnt"));
				hmProCompletedStatus.put(rs.getString("pro_id"), taskCompleted>0 ? uF.formatIntoOneDecimal(taskCompleted)+"" : "0");
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery1 = new StringBuilder();
		//===start parvez date: 17-10-2022===	
			/*sbQuery1.append("select * from projectmntnc pmc where pmc.pro_id > 0  and pmc.approve_status = 'n' and (pmc.project_owner="+uF.parseToInt(strSessionEmpId)+" " +
						"or pmc.pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(strSessionEmpId)+"))");*/
			sbQuery1.append("select * from projectmntnc pmc where pmc.pro_id > 0  and pmc.approve_status = 'n' and (pmc.project_owners like '%,"+strSessionEmpId+",%' " +
					"or pmc.pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id="+uF.parseToInt(strSessionEmpId)+"))");
			sbQuery1.append(" order by pmc.pro_id desc limit 5");
		//===end parvez date: 17-10-2022===	
			pst = con.prepareStatement(sbQuery1.toString());
//			System.out.println("pst My Project Dashboard ======>> " + pst); 
			rs = pst.executeQuery();
			double dblBugedtedTime = 0; 
			double dblActualTime = 0;
			double dblIdealTimeHrs = 0; 
			double dblActualTimeHrs = 0;
			
			Map<String, String> hmProPerformaceProjectName = new HashMap<String, String>();
			Map<String, String> hmProOwner = new HashMap<String, String>();
			Map<String, String> hmProPerformaceProjectTimeIndicator = new HashMap<String, String>();
			List<String> alProjectId = new ArrayList<String>(); 
			Map<String, String> hmProActIdealTimeHRS = new HashMap<String, String>();
			while(rs.next()) {
//				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
				Map<String, String> hmProjectData = new HashMap<String, String>();
				hmProjectData.put("PRO_ID", rs.getString("pro_id"));
				hmProjectData.put("PRO_NAME", rs.getString("pro_name"));
				hmProjectData.put("PRO_CUSTOMER_NAME", uF.showData(hmPprojectClientMap.get(rs.getString("client_id")), ""));
				hmProjectData.put("PRO_CUST_SPOC_ID", rs.getString("poc"));
				hmProjectData.put("PRO_CUST_SPOC_NAME", uF.showData(hmCustName.get(rs.getString("poc")), ""));
				hmProjectData.put("PRO_OWNER_ID", rs.getString("project_owner"));
				hmProjectData.put("PRO_OWNER_NAME", uF.showData(hmEmpCodeName.get(rs.getString("project_owner")), ""));
				hmProjectData.put("PRO_BILLING_TYPE", CF.getBillinType(rs.getString("billing_type")));
				hmProjectData.put("PRO_BILL_TYPE", rs.getString("billing_type"));
				hmProjectData.put("PRO_BILLING_ACTUAL_TYPE", rs.getString("actual_calculation_type"));
				hmProjectData.put("PRO_BILLING_FREQUENCY", CF.getBillinFreq(rs.getString("billing_kind"), rs.getString("billing_type")));
				hmProjectData.put("PRO_BILL_FREQUENCY", rs.getString("billing_kind"));
				hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
				hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
				hmProjectData.put("PRO_SERVICE_ID", rs.getString("service"));
				hmProjectData.put("PRO_REPORT_CURR_ID", rs.getString("curr_id"));
				hmProjectData.put("PRO_BILLING_CURR_ID", rs.getString("billing_curr_id"));
				hmProjectData.put("PRO_BILLING_AMOUNT", rs.getString("billing_amount"));
				hmProjectData.put("PRO_ORG_ID", rs.getString("org_id"));

				Map<String, String> hmProActualCostTime = new HashMap<String, String>();
				
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equals("M")) {
//					hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData);
					hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation,hmOrgCalType);
				} else {
//					hmProActualCostTime = CF.getProjectActualCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
					hmProActualCostTime = CF.getProjectActualCostAndTime(con,CF, uF, rs.getString("pro_id"), hmProjectData, false, false,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation); //,hmOrgCalType
				}
				
//				Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con, uF, rs.getString("pro_id"), hmProjectData);
				Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con,CF, uF, rs.getString("pro_id"), hmProjectData,hmProEmpRate);
				
				dblBugedtedTime = uF.parseToDouble(hmProBudgetedCostAndTime.get("proBudgetedTime"));
				 
				 if("H".equalsIgnoreCase(rs.getString("actual_calculation_type"))) {
					 dblActualTime = uF.parseToDouble(hmProActualCostTime.get("proActualTime"));
				 } else if("M".equalsIgnoreCase(rs.getString("actual_calculation_type"))) { 
					 dblActualTime = uF.parseToDouble(hmProActualCostTime.get("proActualTime"));
				 } else {
					 dblActualTime = uF.parseToDouble(hmProActualCostTime.get("proActualTime"));
				 }
				 hmProPerformaceProjectName.put(rs.getString("pro_id"), rs.getString("pro_name"));
				
				 
				Date dtDeadline = uF.getDateFormat(rs.getString("deadline"), DBDATE);
				Date dtCurrentDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
				
				if(dtDeadline!=null && dtCurrentDate!=null && dtDeadline.after(dtCurrentDate)) {
					if(dblActualTime <= dblBugedtedTime) {
						/* hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
						 hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Actual Time < Estimated\"></i>");
					} else {
						/*hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
						hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Actual > Estimated\"></i>");
						
					}
				} else if(dtDeadline!=null && dtCurrentDate!=null && (dtCurrentDate.after(dtDeadline) || dtCurrentDate.equals(dtDeadline))) {
					/*hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
					hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Actual > Deadline\"></i>");
				} else {
					hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "");
				}
			 	
			//===start parvez date: 17-10-2022===	
				StringBuilder sbOwners = null;
				if(rs.getString("project_owners")!=null){
					List<String> tempList = Arrays.asList(rs.getString("project_owners").split(","));
					
					for(int j=1; j<tempList.size();j++){
						if(sbOwners==null){
							sbOwners = new StringBuilder();
							sbOwners.append(hmEmpCodeName.get(tempList.get(j)));
						}else{
							sbOwners.append(", "+hmEmpCodeName.get(tempList.get(j)));
						}
					}
				}
				
//				hmProOwner.put(rs.getString("pro_id"), uF.showData(hmEmpCodeName.get(rs.getString("project_owner")), ""));
				hmProOwner.put(rs.getString("pro_id"), uF.showData(sbOwners+"", ""));
			//===end parvez date: 17-10-2022===	
			 	

//				 ********************************* Time in HRS ***************************************
				 dblActualTimeHrs = uF.parseToDouble(hmProActIdealTimeHRS.get(rs.getString("pro_id")+"ACT_TIME_HRS"));
				 String proActualTimeHRS = getProjectActualTimeHRS(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
				 dblActualTimeHrs += uF.parseToDouble(proActualTimeHRS);
				 
				 dblIdealTimeHrs = uF.parseToDouble(hmProActIdealTimeHRS.get(rs.getString("pro_id")+"IDEAL_TIME_HRS"));
				 String proIdealTimeHRS = getProjectIdealTimeHRS(con, uF, rs.getString("pro_id"), hmProjectData);
				 dblIdealTimeHrs += uF.parseToDouble(proIdealTimeHRS);
				 
				 hmProActIdealTimeHRS.put(rs.getString("pro_id")+"_ACT_TIME_HRS", uF.formatIntoTwoDecimalWithOutComma(dblActualTimeHrs));
				 hmProActIdealTimeHRS.put(rs.getString("pro_id")+"_IDEAL_TIME_HRS", uF.formatIntoTwoDecimalWithOutComma(dblIdealTimeHrs));
				 hmProActIdealTimeHRS.put(rs.getString("pro_id")+"_COMPLETED_SATUS", hmProCompletedStatus.get(rs.getString("pro_id")));
				 
				 if(!alProjectId.contains(rs.getString("pro_id"))) {
					 alProjectId.add(rs.getString("pro_id"));
				 }
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmProPerformaceProjectTimeIndicator", hmProPerformaceProjectTimeIndicator);
			request.setAttribute("hmProPerformaceProjectName", hmProPerformaceProjectName);
			request.setAttribute("alProjectId", alProjectId);
			request.setAttribute("hmProOwner", hmProOwner);
			request.setAttribute("hmProActIdealTimeHRS", hmProActIdealTimeHRS);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public String getProjectIdealTimeHRS(Connection con, UtilityFunctions uF, String proId, Map<String, String> hmProjectData) {
		PreparedStatement pst=null;
		ResultSet rs=null;
		String proIdealTimeHrs = null;
		try {
			
			pst = con.prepareStatement("select task_id, activity_name, resource_ids, idealtime, parent_task_id from activity_info where " +
					" parent_task_id = 0 and pro_id = ? ");
			pst.setInt(1, uF.parseToInt(proId));
//			System.out.println("pst======>"+pst); 
			rs=pst.executeQuery();
			Map<String, Map<String, String>> hmTaskData = new HashMap<String, Map<String,String>>();
			while(rs.next()) {
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put(rs.getString("task_id")+"_IDEAL_TIME", rs.getString("idealtime"));
				hmTaskData.put(rs.getString("task_id"), hmInner);
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmTaskData.keySet().iterator();
			double proBudgetedTime = 0;
//			System.out.println("billType ===>> " + billType);
			while (it.hasNext()) {
				String taskId = it.next();
				Map<String, String> hmInner = hmTaskData.get(taskId);

				if(hmProjectData != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("H")) {
					proBudgetedTime += uF.parseToDouble(hmInner.get(taskId+"_IDEAL_TIME"));
				} else if(hmProjectData != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("D")) {
					proBudgetedTime += (uF.parseToDouble(hmInner.get(taskId+"_IDEAL_TIME"))* 8);
				} else if(hmProjectData != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("M")) {
					proBudgetedTime += ((uF.parseToDouble(hmInner.get(taskId+"_IDEAL_TIME"))* 8) * 30);
				}
//				System.out.println(taskId + "  taskResourceCnt ===>> " + taskResourceCnt + "  taskResourceCost ====>> " + taskResourceCost +" IDEAL_TIME =>>>>> " + hmInner.get(taskId+"_IDEAL_TIME"));
			}
			proIdealTimeHrs = proBudgetedTime+"";
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return proIdealTimeHrs;
	}
	
	
	public String getProjectActualTimeHRS(Connection con, CommonFunctions CF, UtilityFunctions uF, String proId, Map<String, String> hmProjectData, boolean isSubmit, boolean isApprove) {
		PreparedStatement pst=null;
		ResultSet rs=null;
		String proActualTimeHrs = null;
		try {
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(a1.hrs) actual_hrs, sum(a1.days) actual_days, a1.emp_id from (select sum(ta.actual_hrs) hrs, " +
				"count(distinct ta.task_date) days, ta.emp_id, ta.activity_id from task_activity ta where ta.activity_id >0 "); //task_date between ? and ? 
			if(isSubmit && isApprove) {
				sbQuery.append(" and (is_approved = 1 or is_approved = 2)");
			} else if(isSubmit) {
				sbQuery.append(" and is_approved = 1 ");
			} else if(isApprove) {
				sbQuery.append(" and is_approved = 2 ");
			}
			sbQuery.append(" group by ta.activity_id, ta.emp_id) as a1, activity_info ai where ai.task_id = a1.activity_id and ai.pro_id = ? " +
				"group by a1.emp_id");
			
			pst = con.prepareStatement(sbQuery.toString()); // and (is_approved = 1 or is_approved = 2) 
//			pst.setDate(1, uF.getDateFormat(hmProjectData.get("PRO_START_DATE"), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_END_DATE"), DATE_FORMAT));
			pst.setInt(1, uF.parseToInt(proId));
//			System.out.println("pst======> " + pst);
			rs=pst.executeQuery();
			Map<String, String> hmResourceActualTime = new HashMap<String,String>();
			while(rs.next()) {
				hmResourceActualTime.put(rs.getString("emp_id"), rs.getString("actual_hrs"));
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmResourceActualTime.keySet().iterator();
			double proActualTime = 0;
			while (it.hasNext()) {
				String empId = it.next();
				String actualTime = hmResourceActualTime.get(empId);
				proActualTime += uF.parseToDouble(actualTime);
			}
			proActualTimeHrs = ""+proActualTime;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return proActualTimeHrs;
	}
	
	private void checkTeamLead(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean tlFlag = false;
		try{
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from project_emp_details where emp_id=? and _isteamlead =true and pro_id in (select pro_id from projectmntnc)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			if(rs.next()) {
				tlFlag = true;
			}
			rs.close();
			pst.close();
			
			setTlFlag(tlFlag);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void checkProjectOwner(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean poFlag = false;
		try{
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 17-10-2022===	
//			sbQuery.append("select * from projectmntnc pmc where project_owner=?");
			sbQuery.append("select * from projectmntnc pmc where project_owners like '%,"+strSessionEmpId+",%'");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
		//===end parvez date: 17-10-2022===	
			rs = pst.executeQuery();
			if(rs.next()) {
				poFlag = true;
			}
			rs.close();
			pst.close();
			
			setPoFlag(poFlag);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public boolean isPoFlag() {
		return poFlag;
	}

	public void setPoFlag(boolean poFlag) {
		this.poFlag = poFlag;
	}

	public boolean isTlFlag() {
		return tlFlag;
	}

	public void setTlFlag(boolean tlFlag) {
		this.tlFlag = tlFlag;
	}

	public String getTaskWorking() {
		return taskWorking;
	}

	public void setTaskWorking(String taskWorking) {
		this.taskWorking = taskWorking;
	}

	public String getProWorking() {
		return proWorking;
	}

	public void setProWorking(String proWorking) {
		this.proWorking = proWorking;
	}

	public String getProBusinessSnapshot() {
		return proBusinessSnapshot;
	}

	public void setProBusinessSnapshot(String proBusinessSnapshot) {
		this.proBusinessSnapshot = proBusinessSnapshot;
	}

	public String getFreqENDDATE() {
		return freqENDDATE;
	}

	public void setFreqENDDATE(String freqENDDATE) {
		this.freqENDDATE = freqENDDATE;
	}

	public String getQfreqENDDATE() {
		return qfreqENDDATE;
	}

	public void setQfreqENDDATE(String qfreqENDDATE) {
		this.qfreqENDDATE = qfreqENDDATE;
	}

}
