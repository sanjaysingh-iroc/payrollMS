package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectDashboard extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType;
	String strWLocId;
	String strSessionEmpId;
	CommonFunctions CF; 

	String strProType;
	
	String freqENDDATE;
	String qfreqENDDATE;
	
	List<FillFinancialYears> financialYearList;
	String collectionFYear;
	String profitabilityFYear;
	String billsReceiptsCommitFYear;
	String projectExpensesFYear;
	String weeklyWorkProgressFYear;
	String outstandingCommitBilledFYear;
	String projectPerformanceFYear;
	
	public String execute() throws Exception {
 
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)return LOGIN;

		strSessionEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		strWLocId = (String)session.getAttribute(WLOCATIONID);
		
		request.setAttribute(PAGE, "/jsp/task/ProjectDashboard.jsp");
		request.setAttribute(TITLE, "Project Dashboard");
		
		request.setAttribute("arrEnabledModules", CF.getArrEnabledModules());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		request.setAttribute("IS_DEVICE_INTEGRATION", CF.getIsDeviceIntegration());
		UtilityFunctions uF = new UtilityFunctions();
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
		getProjectDetails(uF);

		return LOAD;
	}
	
	
	public void getProjectDetails(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			
			boolean poFlag = checkProjectOwner(con,uF);
			
			getBillsReceiptsDetails(con,uF,poFlag);
			getCommitmentAndBillOutstandingDetails(con, uF, poFlag);
			
			getProjectDetailsByProject(con,uF,poFlag);
//			getProjectCosting(con,uF,poFlag);
			getProjectWorkProgressDetails(con,uF,poFlag);
			getProjectPerformanceDetails(con,uF,poFlag);
			getProjectPerformance(con, uF, poFlag);
			
			getProjectSbuWise(con,uF,poFlag);
			getSkillResources(con,uF,poFlag);
			getProjectTaskCount(con,uF,poFlag);
			getWeeklyWorkProgress(con,uF,poFlag);
			//getPerformingProjectOwner(con,uF);
			
//			getCommitmentReport(con, uF, poFlag);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}

	
	private boolean checkProjectOwner(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean poFlag = false;
		try {
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 14-10-2022===	
//			sbQuery.append("select * from projectmntnc pmc where project_owner=?");
			sbQuery.append("select * from projectmntnc pmc where project_owners like '%,"+strSessionEmpId+",%'");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
		//===end parvez date: 14-10-2022===	
			rs = pst.executeQuery();
			if(rs.next()) {
				poFlag = true;
			}
			rs.close();
			pst.close();
			
//			System.out.println("poFlag ===>> " + poFlag+" getStrProType() ===>> " + getStrProType());
			if(poFlag && uF.parseToInt(getStrProType()) == 0) {
				setStrProType("2");
			}
			request.setAttribute("poFlag", poFlag);
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
		return poFlag;
	}

	
	
//	public void getCommitmentReport(Connection con, UtilityFunctions uF, boolean poFlag) {
//		List<List<String>> alOuter = new ArrayList<List<String>>();
//		PreparedStatement pst = null;
//		ResultSet rs  = null;
//
//		try {
//				
//			String[] strCalendarYearDates = null;
//			String strCalendarYearStart = null;
//			String strCalendarYearEnd = null;
//			
//			strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
//			strCalendarYearStart = strCalendarYearDates[0];
//			strCalendarYearEnd = strCalendarYearDates[1];
//			int nYear = uF.parseToInt(uF.getDateFormat(strCalendarYearEnd+"", DATE_FORMAT, "yy"));
//			
//			Map<String, String> hmProjectData = new HashMap<String,String>();
//							
//			StringBuilder sbQuery = new StringBuilder();
//			List<String> monthYearsList = new ArrayList<String>();
//			List<String> alInner = new ArrayList<String>();
//			
//			sbQuery.append("select cd.client_id, cd.client_name, pmt.pro_id, pmt.pro_name, pmt.org_id, pmt.wlocation_id, pmt.department_id, pmt.start_date," +
//					" pmt.deadline, billing_kind, billing_cycle_day, billing_cycle_weekday, billing_amount from client_details cd, projectmntnc pmt " +
//					" where cd.client_id = pmt.client_id ");
//			if(uF.parseToInt(getStrProType())==1 && strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//				sbQuery.append(" and (pmt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
//			} else if(uF.parseToInt(getStrProType())==1 && strUserType != null && strUserType.equals(HRMANAGER)) {
//				sbQuery.append(" and (pmt.wlocation_id="+uF.parseToInt(strWLocId)+" or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
//			} else if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(ACCOUNTANT))) {
//				sbQuery.append(" and (pmt.project_owner="+uF.parseToInt(strSessionEmpId)+" or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
//			} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
//				sbQuery.append(" and pmt.project_owner="+uF.parseToInt(strSessionEmpId));
//			}
//			
//			sbQuery.append(" and ((start_date <= '" + uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "') ");
//			sbQuery.append(" or (start_date >= '" + uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "' and start_date <= '"+ uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, DBDATE) + "') ");
//			sbQuery.append(" or (start_date >= '" + uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, DBDATE) + "') ");
//			sbQuery.append(" or (start_date <= '" + uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, DBDATE) + "') ");
//			sbQuery.append(" or (deadline >= '" + uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, DBDATE) + "')) ");
//	        
////			sbQuery.append(" order by pro_id limit 5");
//			pst = con.prepareStatement(sbQuery.toString());
////		        System.out.println("pst =====>> "  +pst);
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				
//				alInner.add(rs.getString("pro_id"));
//				hmProjectData.put(rs.getString("pro_id")+"_START_DATE", rs.getString("start_date"));
//				hmProjectData.put(rs.getString("pro_id")+"_DEADLINE", rs.getString("deadline"));
//				
//				hmProjectData.put(rs.getString("pro_id")+"_BILL_FREQ", rs.getString("billing_kind"));
//				hmProjectData.put(rs.getString("pro_id")+"_FREQ_DAY", rs.getString("billing_cycle_day"));
//				hmProjectData.put(rs.getString("pro_id")+"_WEEKDAY", rs.getString("billing_cycle_weekday"));
//				hmProjectData.put(rs.getString("pro_id")+"_BILLING_AMT", rs.getString("billing_amount"));
//			}
//			rs.close();
//			pst.close();
//				
////			System.out.println("alInner ===>> " + alInner);
//			
//				int startDay = 0;
//				int endDay = 0;
//				int startMonth = 0;
//				int endMonth = 0;
//				int startYear = 0;
//				int endYear = 0;
//				int start_month = 0;
//				int start_year = 0;
//				Date startDate = uF.getDateFormat(strCalendarYearStart, DATE_FORMAT);
//				Date endDate1 = uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT);
////				System.out.println("start date ===>> "+startDate+" --- end date ===>> "+endDate);
//				Calendar cal = Calendar.getInstance();
//				cal.setTime(startDate);
//				startDay = cal.get(Calendar.DATE);
//			    start_month = cal.get(Calendar.MONTH)+1;
//			    startMonth = start_month;
//			    
//			    start_year = cal.get(Calendar.YEAR);
//			    startYear= start_year;
//			    
//			    Calendar cal2 = Calendar.getInstance();
//				cal2.setTime(endDate1);
//				endDay = cal2.get(Calendar.DATE);
//				endMonth = cal2.get(Calendar.MONTH)+1;
//				endYear = cal2.get(Calendar.YEAR);
//			
//				long monthDiff = uF.getMonthsDifference(startDate, endDate1);
//			
////				System.out.println("monthDiff ===>> " + monthDiff);
////				System.out.println("endYear ===>> " + endYear);
//				
//			    while(monthDiff > 0) {
////			    	System.out.println("monthDiff ===>> " + monthDiff);
//			    	
//					monthYearsList.add(String.valueOf(startMonth)+"/"+String.valueOf(startYear));
//					if(startMonth == 12 && startYear == endYear) {
//						break;
//					}
//					startMonth++;
////					System.out.println("startMonth ===>> " + startMonth);
//					if(startMonth > 12) {
//						startMonth = 1;
//						startYear++;
//					} else if(startMonth > endMonth && startYear == endYear) {
//						break;
//					} 
//				}
//			    
////			for(int i=0;i<alInner.size() && alInner!=null && !alInner.isEmpty();i++) {
////				String proId = alInner.get(i);
////				List<String> proList = new ArrayList<String>();
////			    double totalOutStandingAmt = 0;
////			    int proStMnth = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, "MM")); 
////				int proStYr = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, "yyyy"));
////				
////			    int proEndMnth = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, "MM")); 
////				int proEndYr = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, "yyyy"));
////				
////				setQfreqENDDATE(null);
//				
//			    Map<String, String> hmCommitAmt = new LinkedHashMap<String, String>();
//				Iterator<String> itr = monthYearsList.iterator();
//				while(itr.hasNext()) {
//					 double totalOutStandingAmt = 0;
//					 double dblBillAmount = 0;
//					 
//					String month = itr.next();
//					
////					System.out.println("month==>"+month);
//					String[] dateArr = month.split("/");
//					String strFirstDate = null;
//					String strEndDate = null;
//					
//					String strDate = "01/"+dateArr[0]+"/"+dateArr[1];
//					String minMaxDate = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
//					String[] tmpDate = minMaxDate.split("::::");
//					strFirstDate = tmpDate[0];
//					strEndDate = tmpDate[1];
//			
////					System.out.println("strFirstDate==>"+ strFirstDate +"strEndDate==>"+ strEndDate);
////							String newStartDate = strFirstDate;
////							System.out.println(proId + " --- newStartDate ===>> " + newStartDate);
//					for(int i=0;i<alInner.size() && alInner!=null && !alInner.isEmpty();i++) {
//						String proId = alInner.get(i);
//						List<String> proList = new ArrayList<String>();
////					    double totalOutStandingAmt = 0;
//					    int proStMnth = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, "MM")); 
//						int proStYr = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, "yyyy"));
//						
//					    int proEndMnth = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, "MM")); 
//						int proEndYr = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, "yyyy"));
//						
//						setQfreqENDDATE(null);
//						
//						int intMonths = uF.getMonthsDifference(uF.getDateFormat(strFirstDate, DATE_FORMAT), uF.getDateFormat(strEndDate, DATE_FORMAT));
//	//							System.out.println(proId + " --- intMonths ===>> " + intMonths);
//						int intCount = 1;
//						if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("A")) {
//							intCount = intMonths/12;
//							intCount++;
//						}else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("H")) {
//							intCount = intMonths/6;
//							intCount++;
//						}else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("Q")) {
//							intCount = intMonths/3;
//							intCount++;
//						} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("M")) {
//							intCount = intMonths;
//						} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("B")) {
//							intCount = (intMonths * 2);
//						} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("W")) {
//							String strDays = uF.dateDifference(strFirstDate, DATE_FORMAT, strEndDate, DATE_FORMAT);
//	//								System.out.println(proId + " --- strDays ===>> " + strDays);
//							intCount = (uF.parseToInt(strDays) / 7);
//							intCount++;
//						} 
//	//							System.out.println(proId + " --- intCount ===>> " + intCount);
//						dblBillAmount = 0;
//						setFreqENDDATE(null);
//						
//						Date proStDate = uF.getDateFormatUtil(hmProjectData.get(proId+"_START_DATE"), DBDATE);
//						Date proEdDate = uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE);
//						Date mnthStDate = uF.getDateFormatUtil(strFirstDate, DATE_FORMAT);
//						Date mnthEdDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
//						
//						boolean flag = false;
//						if(((proStDate.before(mnthStDate) || proStDate.equals(mnthStDate)) && (proEdDate.after(mnthEdDate) || proEdDate.equals(mnthEdDate))) ) {
//							flag = true;
//						}
//						
//						if(flag || (proStMnth == uF.parseToInt(dateArr[0]) && proStYr == uF.parseToInt(dateArr[1])) || (proEndMnth == uF.parseToInt(dateArr[0]) && proEndYr == uF.parseToInt(dateArr[1]))) {
//							for(int j=0; j<intCount; j++) {
//								String newStDate = getNewProjectStartDate(con, uF, proId, getFreqENDDATE());
//							
//								if(newStDate == null || newStDate.equals("")) {
//										newStDate = strFirstDate;
//								}
//	//								System.out.println("newStDate ===>> " + newStDate);
//								boolean frqFlag = false;
//								String freqEndDate = strEndDate;
//								
//								if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O")) {
//									freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
//								}
//								
//								if(uF.parseToInt(hmProjectData.get(proId+"_FREQ_DAY")) > 0) {
//	//									System.out.println("innerList.get(4) ===>> " + innerList.get(4));
//									freqEndDate = hmProjectData.get(proId+"_FREQ_DAY") + "/" + uF.getDateFormat(newStDate, DATE_FORMAT, "MM")+ "/" +uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy");
//									freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
//	//										System.out.println("newStDate ===>> "+ newStDate + " -- freqEndDate 1 ===>> " + freqEndDate +" -- proId ===>> " + proId);
//	//									uF.getDateFormat(uF.getPrevDate(freqEndDate, 1)+"", DBDATE, DATE_FORMAT);
//									
//									Date stDate = uF.getDateFormatUtil(newStDate, DATE_FORMAT);
//	//									Date endDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
//									Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
//									
//	//									System.out.println("innerList.get(4) ===>> " + innerList.get(4));
//	//									System.out.println("freqDate ===>> " + freqDate + " stDate ===>> " + stDate);
//									
//									if(freqDate.after(stDate)) {
//										frqFlag = true;
//									}
//	//										System.out.println("frqFlag ===>> " + frqFlag+" -- proId ===>> " + proId);
//									if(frqFlag) {
//										if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("M")) {
//											freqEndDate = freqEndDate;
//										} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("B")) {
//											freqEndDate = uF.getDateFormat(uF.getFutureDate(newStDate, 15)+"", DBDATE, DATE_FORMAT);
//										}
//									} else {
//										if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("M")) {
//											freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(freqEndDate, 1)+"", DBDATE, DATE_FORMAT);
//	//												System.out.println("freqEndDate in else ===>> " + freqEndDate +" -- proId ===>> " + proId);
//										} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("B")) {
//											freqEndDate = uF.getDateFormat(uF.getFutureDate(strFirstDate, 15)+"", DBDATE, DATE_FORMAT);
//										}
//									}
//								}
//								
//	//									System.out.println("freqEndDate 2 ===>> " + freqEndDate +" -- proId ===>> " + proId);
//								if(hmProjectData.get(proId+"_WEEKDAY") != null && !hmProjectData.get(proId+"_WEEKDAY").equals("") && hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("W")) {
//									freqEndDate = uF.getDateFormat(uF.getDateOfPassedDay(newStDate, hmProjectData.get(proId+"_WEEKDAY"))+"", DBDATE, DATE_FORMAT);
//									freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
//	//										System.out.println("freqEndDate -1 ===>>>>> " + freqEndDate);
//								}
//								if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("Q")) {
//									 Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
//									 freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 3)+"", DBDATE, DATE_FORMAT);
//									 if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
//										freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
//									 }
//									 if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
//										setQfreqENDDATE(freqEndDate);
//									 }
//	//											System.out.println("freqEndDate in if ===>> " + freqEndDate +" -- proId ===>> " + proId);
//								}
//								if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("H")) {
//									Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
//									freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 6)+"", DBDATE, DATE_FORMAT);
//									if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
//										freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
//									}
//									if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
//										setQfreqENDDATE(freqEndDate);
//									}
//								}	
//								if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("A")) {
//									Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
//									freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 12)+"", DBDATE, DATE_FORMAT);
//									if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
//										freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
//									}
//									if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
//										setQfreqENDDATE(freqEndDate);
//									}
//								}	
//								Date firstDate = uF.getDateFormatUtil(strFirstDate, DATE_FORMAT);
//								Date endDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
//								Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
//	//									System.out.println("endDate ===>> " + endDate + "  newFreqDate ======>> " + newFreqDate +" -- proId ===>> " + proId);
//								
//								setFreqENDDATE(freqEndDate);
//								
//								if((firstDate.before(newFreqDate) || firstDate.equals(newFreqDate)) && (endDate.after(newFreqDate) || endDate.equals(newFreqDate))) {
//									dblBillAmount += uF.parseToInt(hmProjectData.get(proId+"_BILLING_AMT"));
//								}
//							}
//						}
//						
//						totalOutStandingAmt += dblBillAmount;
//					}
//					
//					
//					hmCommitAmt.put(dateArr[0], uF.formatIntoTwoDecimalWithOutComma(totalOutStandingAmt));
//				}
//
////				proList.add(uF.formatIntoOneDecimalWithOutComma(totalOutStandingAmt));
////				alOuter.add(proList);
////				
////				}
//				
//				System.out.println("hmCommitAmt ===>> " + hmCommitAmt);
//				request.setAttribute("hmCommitAmt", hmCommitAmt);
////				request.setAttribute("alOuter", alOuter);
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
	
	
	
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
	
	
//	private void getPerformingProjectOwner(Connection con, UtilityFunctions uF) {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		try{
//			
//			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
//			Map<String, String> hmPprojectClientMap = CF.getProjectClientMap(con, uF);
//			if(hmPprojectClientMap == null) hmPprojectClientMap = new HashMap<String, String>();
//			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
//			if(hmCustName == null) hmCustName = new HashMap<String, String>();
//			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
//			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
//			
//			pst = con.prepareStatement("select * from project_emp_details where pro_id>0  order by pro_id");
//			rs=pst.executeQuery();
//			Map<String, String> hmProEmpRate = new HashMap<String, String>();
//			while(rs.next()) {
//				hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_HOUR", rs.getString("emp_actual_rate_per_hour"));
//				hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_DAY", rs.getString("emp_actual_rate_per_day"));
//				hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_MONTH", rs.getString("emp_actual_rate_per_month"));
//				hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_HOUR", rs.getString("emp_rate_per_hour"));
//				hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_DAY", rs.getString("emp_rate_per_day"));
//				hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_MONTH", rs.getString("emp_rate_per_month"));
//				
//			}
////			System.out.println("hmProEmpRate ===>> " + hmProEmpRate);
//			rs.close();
//			pst.close();
//			
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select org_id, calculation_type, days from cost_calculation_settings ");
//			pst = con.prepareStatement(sbQuery.toString());
////			System.out.println("pst ===> " + pst);
//			rs = pst.executeQuery();
//			Map<String, String> hmOrgCalType = new HashMap<String, String>();
//			while (rs.next()) {
//				hmOrgCalType.put(rs.getString("org_id"), rs.getString("calculation_type"));
//				hmOrgCalType.put(rs.getString("org_id")+"_DAYS", rs.getString("days"));
//			}
//			
//			sbQuery = new StringBuilder();
//			sbQuery.append("select * from projectmntnc pmc where pro_id>0 and project_owner > 0 ");
//			pst = con.prepareStatement(sbQuery.toString());
////			System.out.println("pst=====>"+pst);
//			rs = pst.executeQuery();
//			double dblActualAmt = 0;
//			double dblBillableAmt = 0;
//			
//			double dblBugedtedTime = 0; 
//			double dblActualTime = 0;
//			
//			double dblIdealTimeHrs = 0; 
//			double dblActualTimeHrs = 0;
//			
//			List<String> alProOwner = new ArrayList<String>();
//			Map<String, String> hmPOActBillAmt = new HashMap<String, String>();
//			Map<String, String> hmPOActIdealTime = new HashMap<String, String>();
//			Map<String, String> hmPOActIdealTimeHRS = new HashMap<String, String>();
//			while(rs.next()) {
//
//				if(!alProOwner.contains(rs.getString("project_owner"))) {
//					alProOwner.add(rs.getString("project_owner"));
//				}
//				
////				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
//				Map<String, String> hmProjectData = new HashMap<String, String>();
//				hmProjectData.put("PRO_ID", rs.getString("pro_id"));
//				hmProjectData.put("PRO_NAME", rs.getString("pro_name"));
//				hmProjectData.put("PRO_CUSTOMER_NAME", uF.showData(hmPprojectClientMap.get(rs.getString("client_id")), ""));
//				hmProjectData.put("PRO_CUST_SPOC_ID", rs.getString("poc"));
//				hmProjectData.put("PRO_CUST_SPOC_NAME", uF.showData(hmCustName.get(rs.getString("poc")), ""));
//				hmProjectData.put("PRO_OWNER_ID", rs.getString("project_owner"));
//				hmProjectData.put("PRO_OWNER_NAME", uF.showData(hmEmpCodeName.get(rs.getString("project_owner")), ""));
//				hmProjectData.put("PRO_BILLING_TYPE", CF.getBillinType(rs.getString("billing_type")));
//				hmProjectData.put("PRO_BILL_TYPE", rs.getString("billing_type"));
//				hmProjectData.put("PRO_BILLING_ACTUAL_TYPE", rs.getString("actual_calculation_type"));
//				hmProjectData.put("PRO_BILLING_FREQUENCY", CF.getBillinFreq(rs.getString("billing_kind"), rs.getString("billing_type")));
//				hmProjectData.put("PRO_BILL_FREQUENCY", rs.getString("billing_kind"));
//				hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
//				hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
//				hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
//				hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
//				hmProjectData.put("PRO_SERVICE_ID", rs.getString("service"));
//				hmProjectData.put("PRO_REPORT_CURR_ID", rs.getString("curr_id"));
//				hmProjectData.put("PRO_BILLING_CURR_ID", rs.getString("billing_curr_id"));
//				hmProjectData.put("PRO_BILLING_AMOUNT", rs.getString("billing_amount"));
//				hmProjectData.put("PRO_ORG_ID", rs.getString("org_id"));
//
//				Map<String, String> hmProActualCostTime = new HashMap<String, String>();
//				Map<String, String> hmProBillCost = new HashMap<String, String>();
//				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equals("M")) {
////					hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData);
//					hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation,hmOrgCalType);
////					hmProBillCost = CF.getMonthlyProjectBillableCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData);
//					hmProBillCost = CF.getMonthlyProjectBillableCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation,hmOrgCalType);
//				} else {
////					hmProActualCostTime = CF.getProjectActualCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
//					hmProActualCostTime = CF.getProjectActualCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation); //,hmOrgCalType
////					hmProBillCost = CF.getProjectBillableCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
//					hmProBillCost = CF.getProjectBillableCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation); //,hmOrgCalType
//				}
//				
////				 ********************************* Money you can convert it in to % ***************************************
//				dblBillableAmt += uF.parseToDouble(hmPOActBillAmt.get(rs.getString("project_owner")+"BILL_AMT"));
//				 if("F".equalsIgnoreCase(rs.getString("billing_type"))) { 
//					 dblBillableAmt += uF.parseToDouble(rs.getString("billing_amount"));
//				 } else {
//					 dblBillableAmt += uF.parseToDouble(hmProBillCost.get("proBillableCost"));
//				 }
//				 
//				 dblActualAmt = uF.parseToDouble(hmPOActBillAmt.get(rs.getString("project_owner")+"ACT_AMT"));
//				 dblActualAmt += uF.parseToDouble(hmProActualCostTime.get("proActualCost"));
//				 hmPOActBillAmt.put(rs.getString("project_owner")+"ACT_AMT", uF.formatIntoTwoDecimalWithOutComma(dblActualAmt));
//				 hmPOActBillAmt.put(rs.getString("project_owner")+"BILL_AMT", uF.formatIntoTwoDecimalWithOutComma(dblBillableAmt));
//				 
//				 
////				 ********************************* Time in actual format you can convert it in to % ***************************************
//				 dblBugedtedTime = uF.parseToDouble(hmPOActIdealTime.get(rs.getString("project_owner")+"IDEAL_TIME"));
//				 Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con, uF, rs.getString("pro_id"), hmProjectData);
//				 dblBugedtedTime += uF.parseToDouble(hmProBudgetedCostAndTime.get("proBudgetedTime"));
//				
//				 dblActualTime = uF.parseToDouble(hmPOActIdealTime.get(rs.getString("project_owner")+"ACT_TIME"));
//				 dblActualTime += uF.parseToDouble(hmProActualCostTime.get("proActualTime"));
//				 hmPOActIdealTime.put(rs.getString("project_owner")+"ACT_TIME", uF.formatIntoTwoDecimalWithOutComma(dblActualTime));
//				 hmPOActIdealTime.put(rs.getString("project_owner")+"IDEAL_TIME", uF.formatIntoTwoDecimalWithOutComma(dblBugedtedTime));
//				 
//				 
////				 ********************************* Time in HRS ***************************************
//				 dblActualTimeHrs = uF.parseToDouble(hmPOActIdealTimeHRS.get(rs.getString("project_owner")+"ACT_TIME_HRS"));
//				 String proActualTimeHRS = getProjectActualTimeHRS(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
//				 dblActualTimeHrs += uF.parseToDouble(proActualTimeHRS);
//				 
//				 dblIdealTimeHrs = uF.parseToDouble(hmPOActIdealTimeHRS.get(rs.getString("project_owner")+"IDEAL_TIME_HRS"));
//				 String proIdealTimeHRS = getProjectIdealTimeHRS(con, uF, rs.getString("pro_id"), hmProjectData);
//				 dblIdealTimeHrs += uF.parseToDouble(proIdealTimeHRS);
//				 
//				 hmPOActIdealTimeHRS.put(rs.getString("project_owner")+"ACT_TIME_HRS", uF.formatIntoTwoDecimalWithOutComma(dblActualTimeHrs));
//				 hmPOActIdealTimeHRS.put(rs.getString("project_owner")+"IDEAL_TIME_HRS", uF.formatIntoTwoDecimalWithOutComma(dblIdealTimeHrs));
//					
//			}
//			rs.close();
//			pst.close();
//			
//			List<Map<String, String>> alPOPerformance = new ArrayList<Map<String, String>>();
//			for(int i = 0; i < alProOwner.size(); i++) {
//				String strEmpId = alProOwner.get(i);
//				
//				/**
//				 * Calculate amt in %
//				 * */
//				double dblAmtPercentage = 0.0d;
//				if(uF.parseToDouble(hmPOActBillAmt.get(strEmpId+"ACT_AMT")) > 0.0d && uF.parseToDouble(hmPOActBillAmt.get(strEmpId+"BILL_AMT")) > 0.0d) {
//					dblAmtPercentage = (uF.parseToDouble(hmPOActBillAmt.get(strEmpId+"ACT_AMT")) / uF.parseToDouble(hmPOActBillAmt.get(strEmpId+"BILL_AMT"))) * 100;
//				}
//				/**
//				 * Calculate amt in % end
//				 * */
//				/**
//				 * Calculate time in %
//				 * */
//				double dblTimePercentage = 0.0d;
//				if(uF.parseToDouble(hmPOActIdealTimeHRS.get(strEmpId+"ACT_TIME_HRS")) > 0.0d && uF.parseToDouble(hmPOActIdealTimeHRS.get(strEmpId+"IDEAL_TIME_HRS")) > 0.0d) {
//					dblTimePercentage = (uF.parseToDouble(hmPOActIdealTimeHRS.get(strEmpId+"ACT_TIME_HRS")) / uF.parseToDouble(hmPOActIdealTimeHRS.get(strEmpId+"IDEAL_TIME_HRS"))) * 100;
//				}
//				/**
//				 * Calculate time in %
//				 * */
//				
//				/**
//				 * Calculate avg in %
//				 * */
//				double dblAvgPercentage = 0.0d; 
//				if(dblAmtPercentage > 0.0d || dblTimePercentage > 0.0d) {
//					dblAvgPercentage = (dblAmtPercentage + dblTimePercentage)/2;
//				}
//				/**
//				 * Calculate avg in %
//				 * */
//				if(dblAvgPercentage > 0.0d) {
//					String strEmpName = "";
//					String strEmpShortLName = "";
//					String strEmpImage = "";
//					pst = con.prepareStatement("select * from employee_personal_details epd,employee_official_details eod where epd.emp_per_id = eod.emp_id" +
//							" and eod.emp_id=?");
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					rs = pst.executeQuery();
//					while(rs.next()) {
//						String strShortLName = (rs.getString("emp_lname") !=null && !rs.getString("emp_lname").trim().equals("")) ? String.valueOf(rs.getString("emp_lname").trim().charAt(0)) : "";
//						strEmpShortLName = uF.showData(rs.getString("emp_fname"), "")+" "+strShortLName.toUpperCase();
//						strEmpName = rs.getString("emp_fname") + " " + uF.showData(rs.getString("emp_mname"), "") + " " + rs.getString("emp_lname");
//						strEmpImage = uF.showData(rs.getString("emp_image"), "");
//					}
//					rs.close();
//					pst.close();
//					
//					Map<String, String> hmInner = new HashMap<String, String>();
//					hmInner.put("EMP_ID", strEmpId);
//					hmInner.put("EMP_SHORT_NAME", strEmpShortLName);
//					hmInner.put("EMP_NAME", strEmpName);
//					hmInner.put("EMP_IMAGE", strEmpImage);
//					hmInner.put("EMP_AVG", uF.formatIntoTwoDecimalWithOutComma(dblAvgPercentage));
//					double dblAvgFive = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAvgPercentage))/20;
//					hmInner.put("EMP_AVG_FIVE", uF.formatIntoTwoDecimalWithOutComma(dblAvgFive));
//					
//					alPOPerformance.add(hmInner);
//				}
//			}
//			Collections.sort(alPOPerformance, Collections.reverseOrder(new Comparator<Map<String, String>>()
//			{
//				@Override
//				public int compare(Map<String, String> hm, Map<String, String> hm1) {
//					return Double.valueOf(hm.get("EMP_AVG")).compareTo(Double.valueOf(hm1.get("EMP_AVG")));
//				}
//			}));
//			
//			if(alPOPerformance.size()>3) {
//				alPOPerformance = alPOPerformance.subList(0, 3);
//			}
//			request.setAttribute("alPOPerformance", alPOPerformance);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if(rs != null) {
//				try {
//					rs.close();
//				} catch (SQLException e1) {
//					e1.printStackTrace();
//				}
//			}
//			if(pst != null) {
//				try {
//					pst.close();
//				} catch (SQLException e1) {
//					e1.printStackTrace();
//				}
//			}
//		}
//	}

	private void getWeeklyWorkProgress(Connection con, UtilityFunctions uF, boolean poFlag) {
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
			sbQuery.append("select * from projectmntnc pmt where pmt.pro_id > 0 and pmt.approve_status = 'n' ");
			if(uF.parseToInt(getStrProType())==1 && strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and (pmt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(uF.parseToInt(getStrProType())==1 && strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and (pmt.wlocation_id="+uF.parseToInt(strWLocId)+")");
			} 
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(ACCOUNTANT))) {
			//===start parvez date: 14-10-2022===	
//				sbQuery.append(" and (pmt.project_owner="+uF.parseToInt(strSessionEmpId)+" or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
				sbQuery.append(" and (pmt.project_owners like '%,"+strSessionEmpId+",%' or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			//===end parvez date: 14-10-2022===	
			} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
			//===start parvez date: 14-10-2022===	
				/*sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owner = "+uF.parseToInt(strSessionEmpId)+")");*/
				sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owners like '%,"+strSessionEmpId+",%')");
			//===end parvez date: 14-10-2022===	
//				sbQuery.append(" and pmt.project_owner="+uF.parseToInt(strSessionEmpId));
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
//			Map<String, String> hmProject = new LinkedHashMap<String, String>();
			StringBuilder sbProId = null;
			while(rs.next()) {
//				hmProject.put(rs.getString("pro_id"), uF.showData(rs.getString("pro_name"), ""));
				if(sbProId==null) {
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
				for(int i = 0; weekdates!=null && i < weekdates.size();i++) {
					List<String> week = weekdates.get(i);
					x++;
					/**Complete Task Count
					 * */
					pst = con.prepareStatement("select count(*) as cnt,ai.pro_id from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
							"and ai.pro_id in ("+sbProId.toString()+") and end_date between ? and ? and ai.approve_status='approved' " +
							"group by ai.pro_id,ai.end_date order by ai.pro_id,ai.end_date");
					pst.setDate(1, uF.getDateFormat(week.get(0), DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(week.get(1), DATE_FORMAT));
//					System.out.println("pst======>"+pst);
					rs = pst.executeQuery();
					int nComplete = 0;
					while(rs.next()) {
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
					while(rs.next()) {
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
					while(rs.next()) {
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
	        
//			if(hmProject != null && hmProject.size() > 0) {
//                Iterator<String> it = hmProject.keySet().iterator();
//				while(it.hasNext()) {
//					String strProId = it.next();
//					
//					int x = 0;
//					for(int i = 0; weekdates!=null && i < weekdates.size();i++) {
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
//						while(rs.next()) {
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
//						while(rs.next()) {
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
//						while(rs.next()) {
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
	        
			
			StringBuilder sbWork 	= new StringBuilder();
			int x = 0;
			for(int i = 0; weekdates!=null && i < weekdates.size();i++) {
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

	
	private void getProjectTaskCount(Connection con, UtilityFunctions uF, boolean poFlag) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(pro_id) as cnt from projectmntnc pmt where pmt.approve_status='n'");
			if(uF.parseToInt(getStrProType())==1 && strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and (pmt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(uF.parseToInt(getStrProType())==1 && strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and (pmt.wlocation_id="+uF.parseToInt(strWLocId)+")");
			} 
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(ACCOUNTANT))) {
			//===start parvez date: 14-10-2022===	
//				sbQuery.append(" and (pmt.project_owner="+uF.parseToInt(strSessionEmpId)+" or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
				sbQuery.append(" and (pmt.project_owners like '%,"+strSessionEmpId+",%' or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			//===end parvez date: 14-10-2022===	
			} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
			//===start parvez date: 14-10-2022===	
				/*sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owner = "+uF.parseToInt(strSessionEmpId)+")");*/
				sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owners like '%,"+strSessionEmpId+",%')");
			//===end parvez date: 14-10-2022===	
//				sbQuery.append(" and pmt.project_owner="+uF.parseToInt(strSessionEmpId));
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=====>"+pst); 
			rs = pst.executeQuery();
			int nProject = 0;
			while(rs.next()) {
				nProject = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select count(task_id) as cnt from activity_info where approve_status='n' " +
					"and pro_id in (select pro_id from projectmntnc pmt where pmt.approve_status='n'");
			if(uF.parseToInt(getStrProType())==1 && strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and (pmt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(uF.parseToInt(getStrProType())==1 && strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and (pmt.wlocation_id="+uF.parseToInt(strWLocId)+")");
			}
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(ACCOUNTANT))) {
		//===start parvez date: 14-10-2022===		
//				sbQuery.append(" and (pmt.project_owner="+uF.parseToInt(strSessionEmpId)+" or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
				sbQuery.append(" and (pmt.project_owners like '%,"+strSessionEmpId+",%' or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
		//===end parvez date: 14-10-2022===		
			} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
		//===start parvez date: 14-10-2022===		
				/*sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owner = "+uF.parseToInt(strSessionEmpId)+")");*/
				sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owners like '%,"+strSessionEmpId+",%')");
		//===end parvez date: 14-10-2022===		
//				sbQuery.append(" and pmt.project_owner="+uF.parseToInt(strSessionEmpId));
			}
			sbQuery.append(")");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=====>"+pst); 
			rs = pst.executeQuery();
			int nTask = 0;
			while(rs.next()) {
				nTask = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("nProject", ""+nProject);
			request.setAttribute("nTask", ""+nTask);
			
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
	

	private void getSkillResources(Connection con, UtilityFunctions uF, boolean poFlag) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(sd.emp_id) as cnt,sd.skill_id from (select min(skills_id) as skills_id,emp_id from skills_description where " +
				"emp_id>0 and emp_id in (select eod.emp_id from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id and epd.is_alive=true) ");
			if(uF.parseToInt(getStrProType())==1 && strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and emp_id in(select emp_id from project_emp_details where pro_id in (select pro_id from projectmntnc pmt " +
					"where pmt.approve_status != 'blocked' and pmt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") or pmt.added_by="+uF.parseToInt(strSessionEmpId)+"))");
			} else if(uF.parseToInt(getStrProType())==1 && strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and emp_id in(select emp_id from project_emp_details where pro_id in (select pro_id from projectmntnc pmt " +
					"where pmt.approve_status != 'blocked' and pmt.wlocation_id="+uF.parseToInt(strWLocId)+"))");
			}
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(ACCOUNTANT))) {
			//===start parvez date: 14-10-2022===	
				/*sbQuery.append(" and emp_id in(select emp_id from project_emp_details where pro_id in (select pro_id from projectmntnc pmt " +
					"where pmt.approve_status != 'blocked' and (pmt.project_owner="+uF.parseToInt(strSessionEmpId)+" or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")))");*/
				sbQuery.append(" and emp_id in(select emp_id from project_emp_details where pro_id in (select pro_id from projectmntnc pmt " +
						"where pmt.approve_status != 'blocked' and (pmt.project_owners like '%,"+strSessionEmpId+",%' or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")))");
			//===end parvez date: 14-10-2022===	
			} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
			//===start parvez date: 14-10-2022===	
				/*sbQuery.append(" and emp_id in(select emp_id from project_emp_details where pro_id in (select pro_id from projectmntnc pmt " +
					"where pmt.approve_status != 'blocked' and pmt.project_owner="+uF.parseToInt(strSessionEmpId)+") or (_isteamlead = true " +
					" and emp_id = " +uF.parseToInt(strSessionEmpId)+")) ");*/
				sbQuery.append(" and emp_id in(select emp_id from project_emp_details where pro_id in (select pro_id from projectmntnc pmt " +
						"where pmt.approve_status != 'blocked' and pmt.project_owners like '%,"+strSessionEmpId+",%') or (_isteamlead = true " +
						" and emp_id = " +uF.parseToInt(strSessionEmpId)+")) ");
			//===end parvez date: 14-10-2022===	
			}
			sbQuery.append(" group by emp_id) a, skills_description sd " +
					"where a.skills_id=sd.skills_id and a.emp_id=sd.emp_id group by sd.skill_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=====>"+pst); 
			rs = pst.executeQuery();
			Map<String, String> hmSkillResource = new HashMap<String, String>();
			while(rs.next()) {
				hmSkillResource.put(rs.getString("skill_id"), rs.getString("cnt"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmSkillName = (Map<String, String>)CF.getSkillNameMap(con);
			if(hmSkillName == null) hmSkillName = new HashMap<String, String>();
			
			Iterator<String> it1 = hmSkillResource.keySet().iterator();
			StringBuilder sbSkillResource 	= new StringBuilder();
			sbSkillResource.append("{'Skills (Resources)': 'Skills (Resources)',");
			int x = 1;
			List<String> alSkillGraph = new ArrayList<String>();
			while(it1.hasNext()) {
				String strSkillId = it1.next();
				String strSkillResource = uF.showData(hmSkillResource.get(strSkillId), "0");
				String strSkillName = uF.showData(hmSkillName.get(strSkillId), "");
			
				sbSkillResource.append("'"+uF.showData(strSkillName.replaceAll("[^a-zA-Z0-9]", ""), "")+"': "+uF.parseToInt(strSkillResource)+",");
				StringBuilder sbGraph = new StringBuilder();
				sbGraph.append("var graphSkill"+x+" = new AmCharts.AmGraph();" +
								"graphSkill"+x+".type = \"column\";" +
								"graphSkill"+x+".title = \""+uF.showData(strSkillName.replaceAll("[^a-zA-Z0-9]", ""), "")+"\";" +
								"graphSkill"+x+".valueField = \""+uF.showData(strSkillName.replaceAll("[^a-zA-Z0-9]", ""), "")+"\";" +
								"graphSkill"+x+".balloonText = \""+uF.showData(strSkillName.replaceAll("[^a-zA-Z0-9]", ""), "")+":[[value]]\";" +
								"graphSkill"+x+".lineAlpha = 0;" +
								"graphSkill"+x+".fillAlphas = 1;" +
								"chart5.addGraph(graphSkill"+x+");");
				alSkillGraph.add(sbGraph.toString());
				x++;
			}
			if(sbSkillResource.length()>1) {
				sbSkillResource.replace(0, sbSkillResource.length(), sbSkillResource.substring(0, sbSkillResource.length()-1));
	        }
			sbSkillResource.append("}");
			
			request.setAttribute("sbSkillResource", sbSkillResource.toString());
			request.setAttribute("alSkillGraph", alSkillGraph);
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

	private void getProjectSbuWise(Connection con, UtilityFunctions uF, boolean poFlag) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sbu_id, count(pro_id) as cnt from projectmntnc pmt where pmt.pro_id>0 and pmt.sbu_id >0 and pmt.approve_status = 'n' ");
			if(uF.parseToInt(getStrProType())==1 && strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and (pmt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(uF.parseToInt(getStrProType())==1 && strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and (pmt.wlocation_id="+uF.parseToInt(strWLocId)+" )");
			}
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(ACCOUNTANT))) {
			//===start parvez date: 14-10-2022===	
//				sbQuery.append(" and (pmt.project_owner="+uF.parseToInt(strSessionEmpId)+" or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
				sbQuery.append(" and (pmt.project_owners like '%,"+strSessionEmpId+",%' or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			//===end parvez date: 14-10-2022===	
			} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
			//===start parvez date: 14-10-2022===	
				/*sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owner = "+uF.parseToInt(strSessionEmpId)+")");*/
				sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owners like '%,"+strSessionEmpId+",%')");
			//===end parvez date: 14-10-2022===	
//				sbQuery.append(" and pmt.project_owner="+uF.parseToInt(strSessionEmpId));
			}
			sbQuery.append("group by pmt.sbu_id order by pmt.sbu_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst Project Dashboard ===>> " + pst); 
			rs = pst.executeQuery();
			Map<String, String> hmProSbu = new HashMap<String, String>();
			while(rs.next()) {
				hmProSbu.put(rs.getString("sbu_id"), rs.getString("cnt"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmSbu = (Map<String, String>)CF.getServicesMap(con, false);
			if(hmSbu == null) hmSbu = new HashMap<String, String>();
			
			Iterator<String> it1 = hmProSbu.keySet().iterator();
			StringBuilder sbProSbu 	= new StringBuilder();
			int nProSbuTotal = 0; 
			while(it1.hasNext()) {
				String strSbuId = it1.next();
				String strProSbu = uF.showData(hmProSbu.get(strSbuId), "0");
				String strSbuName = uF.showData(hmSbu.get(strSbuId), "");
				//.replaceAll("[^a-zA-Z0-9]", "")
				sbProSbu.append("{'sbu':'"+strSbuName+"', " +
						"'project': "+uF.parseToDouble(strProSbu)+"},");
				nProSbuTotal += uF.parseToDouble(strProSbu);
			}
			
			if(sbProSbu.length()>1) {
				sbProSbu.replace(0, sbProSbu.length(), sbProSbu.substring(0, sbProSbu.length()-1));
	        }
			request.setAttribute("sbProSbu", sbProSbu.toString());
			request.setAttribute("nProSbuTotal", ""+nProSbuTotal);
			
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

	private void getProjectPerformanceDetails(Connection con, UtilityFunctions uF, boolean poFlag) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
//			Map<String, String> hmEmpNMap = CF.getEmpNameMap(con, null, null);
			
//			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", false, strStartDate, strEndDate, uF);
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmPprojectClientMap = CF.getProjectClientMap(con, uF);
			if(hmPprojectClientMap == null) hmPprojectClientMap = new HashMap<String, String>();
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
			if(hmCustName == null) hmCustName = new HashMap<String, String>();
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from project_emp_details where pro_id>0 and pro_id in (select pro_id from projectmntnc pmt where pmt.pro_id > 0 and pmt.approve_status = 'n' ");
			if(uF.parseToInt(getStrProType())==1 && strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and (pmt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(uF.parseToInt(getStrProType())==1 && strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and (pmt.wlocation_id="+uF.parseToInt(strWLocId)+")");
			}
		//===start parvez date: 14-10-2022===	
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(ACCOUNTANT))) {
//				sbQuery.append(" and (pmt.project_owner="+uF.parseToInt(strSessionEmpId)+" or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
				sbQuery.append(" and (pmt.project_owners like '%,"+strSessionEmpId+",%' or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
				/*sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owner = "+uF.parseToInt(strSessionEmpId)+")");*/
				sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owners like '%,"+strSessionEmpId+",%')");
//				sbQuery.append(" and pmt.project_owner="+uF.parseToInt(strSessionEmpId));
			}
		//===end parvez date: 14-10-2022===	
			sbQuery.append(") order by pro_id ");
			pst = con.prepareStatement(sbQuery.toString());
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
			
			sbQuery = new StringBuilder();
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
			sbQuery.append("select * from projectmntnc pmt where pmt.pro_id > 0  and pmt.approve_status = 'n' ");
			if(uF.parseToInt(getStrProType())==1 && strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and (pmt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(uF.parseToInt(getStrProType())==1 && strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and (pmt.wlocation_id="+uF.parseToInt(strWLocId)+")");
			}
		//===start parvez date: 14-10-2022===	
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(ACCOUNTANT))) {
//				sbQuery.append(" and (pmt.project_owner="+uF.parseToInt(strSessionEmpId)+" or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
				sbQuery.append(" and (pmt.project_owners like '%,"+strSessionEmpId+",%' or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
				sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owners like '%,"+strSessionEmpId+",%')");
//				sbQuery.append(" and pmt.project_owner="+uF.parseToInt(strSessionEmpId));
			}
		//===end parvez date: 14-10-2022===	
			sbQuery.append(" order by pmt.pro_id limit 5");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst); 
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
					hmProActualCostTime = CF.getProjectActualCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation); //,hmOrgCalType
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
						 /*hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
						hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
					} else {
						/*hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
						hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
					}
				} else if(dtDeadline!=null && dtCurrentDate!=null && dtCurrentDate.after(dtDeadline)) {
					/*hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
					hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
				} else {
					hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "");
				}
				
			//===start parvez date: 14-10-2022===
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
				
//			 	hmProOwner.put(rs.getString("pro_id"), uF.showData(hmEmpCodeName.get(rs.getString("project_owner")), ""));
				hmProOwner.put(rs.getString("pro_id"), uF.showData(sbOwners+"", ""));
			//===end parvez date: 14-10-2022===	
			 	

//				 ********************************* Time in HRS ***************************************
				 dblActualTimeHrs = uF.parseToDouble(hmProActIdealTimeHRS.get(rs.getString("pro_id")+"ACT_TIME_HRS"));
				 String proActualTimeHRS = getProjectActualTimeHRS(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
				 dblActualTimeHrs += uF.parseToDouble(proActualTimeHRS);
				 
				 dblIdealTimeHrs = uF.parseToDouble(hmProActIdealTimeHRS.get(rs.getString("pro_id")+"IDEAL_TIME_HRS"));
				 String proIdealTimeHRS = getProjectIdealTimeHRS(con, uF, rs.getString("pro_id"), hmProjectData);
				 dblIdealTimeHrs += uF.parseToDouble(proIdealTimeHRS);
				 
				 hmProActIdealTimeHRS.put(rs.getString("pro_id")+"_ACT_TIME_HRS", uF.formatIntoTwoDecimalWithOutComma(dblActualTimeHrs));
				 hmProActIdealTimeHRS.put(rs.getString("pro_id")+"_IDEAL_TIME_HRS", uF.formatIntoTwoDecimalWithOutComma(dblIdealTimeHrs));
			 	
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
	
	
	
	
	public void getProjectPerformance(Connection con, UtilityFunctions uF, boolean poFlag) {
		PreparedStatement pst=null;
		ResultSet rs=null;
		try {
			
			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;
//			System.out.println("getCollectionFYear() ===>> " + getCollectionFYear());
//			System.out.println("getBillsReceiptsCommitFYear() ===>> " + getBillsReceiptsCommitFYear());
			
			if(getProjectPerformanceFYear() != null && !getProjectPerformanceFYear().equals("")) {
				strCalendarYearDates = getProjectPerformanceFYear().split("-");
			} else {
				strCalendarYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			}
			strCalendarYearStart = strCalendarYearDates[0];
			strCalendarYearEnd = strCalendarYearDates[1];
			
			Map<String, String> hmEmpNMap = CF.getEmpNameMap(con, null, null);
			
			Map<String, String> hmProjectClient  = new HashMap<String, String>();
			boolean isComplete = false;
//			if(getProType() != null && getProType().equals("C")) {
//				isComplete = true;
//			}
			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", isComplete, strCalendarYearStart, strCalendarYearEnd, uF);
			
			StringBuilder budgeted_cost 	= new StringBuilder();
			StringBuilder billable_amount 	= new StringBuilder();
			StringBuilder actual_amount 	= new StringBuilder();
			StringBuilder pro_name 			= new StringBuilder();

			int proCount = 0;
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from projectmntnc pmt where pmt.pro_id > 0  and pmt.approve_status = 'n' ");
			if(uF.parseToInt(getStrProType())==1 && strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and (pmt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(uF.parseToInt(getStrProType())==1 && strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and (pmt.wlocation_id="+uF.parseToInt(strWLocId)+")");
			}
		//===start parvez date: 14-10-2022===	
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(ACCOUNTANT))) {
//				sbQuery.append(" and (pmt.project_owner="+uF.parseToInt(strSessionEmpId)+" or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
				sbQuery.append(" and (pmt.project_owners like '%,"+strSessionEmpId+",%' or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
				/*sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owner = "+uF.parseToInt(strSessionEmpId)+")");*/
				sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owners like '%,"+strSessionEmpId+",%')");
//				sbQuery.append(" and pmt.project_owner="+uF.parseToInt(strSessionEmpId));
			}
		//===end parvez date: 14-10-2022===	
			sbQuery.append(" order by pmt.pro_id limit 5");
			
//			StringBuilder sbQuery1 = new StringBuilder();
//			sbQuery1.append("select pro_id, pro_name, billing_type, billing_amount, idealtime, start_date, deadline, client_id, actual_calculation_type, " +
//				"bill_days_type, hours_for_bill_day, added_by, curr_id from projectmntnc pmc where pmc.pro_id > 0 ");
//			if(poFlag && uF.parseToInt(getStrProType()) == 2) {
//				sbQuery1.append(" and pmc.project_owner="+uF.parseToInt(strSessionEmpId));
//			}
//			if(getProType() == null || getProType().equals("") || getProType().equals("null") || getProType().equals("L")) {
//				sbQuery1.append(" and pmc.approve_status = 'n' ");
//			} else if(getProType() != null && getProType().equals("C")) {
//				sbQuery1.append(" and pmc.approve_status = 'approved' ");
//			}
//			if(uF.parseToInt(getF_org())>0) {
//				sbQuery1.append(" and pmc.org_id in ("+uF.parseToInt(getF_org())+")");
//			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
//					sbQuery1.append(" and pmc.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//			}
//			if(getF_strWLocation()!=null && getF_strWLocation().length>0 && !getF_strWLocation()[0].trim().equals("")) {
//	            sbQuery1.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
//	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//				sbQuery1.append(" and pmc.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//			}
//
//			if(getF_department() != null && getF_department().length>0 && !getF_department()[0].trim().equals("")) {
//				sbQuery1.append(" and pmc.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
//			}
//			
//			if(getF_service() != null && getF_service().length>0 && !getF_service()[0].trim().equals("")) {
//				sbQuery1.append(" and pmc.sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
//			}
//			
//			if(getF_project_service()!=null && getF_project_service().length > 0 && getLoadMore() == null) {
//				String service = uF.getConcateData(getF_project_service());
//				sbQuery1.append(" and pmc.service in ("+service+") ");
//			} else if(getF_project_service()!=null && getF_project_service().length > 0) {
//				String service = getConcateDataLoadMore(getProject_service());
//				sbQuery1.append(" and pmc.service in ("+service+") ");
//			}
//			
//			if(getF_client() != null && getF_client().length>0 && !getF_client()[0].trim().equals("")) {
//				sbQuery1.append(" and pmc.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
//			}
//			
//			if(getStrStartDate()!=null && getStrEndDate()!=null && !getStrStartDate().equalsIgnoreCase(LABEL_FROM_DATE) && !getStrEndDate().equalsIgnoreCase(LABEL_TO_DATE)) {
//				sbQuery1.append(" and pmc.start_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
//			}
//			if(nManagerId>0) {
//				sbQuery1.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "+nManagerId+" ) or added_by = "+nManagerId+" ) ");
//			}
//			int intOffset = uF.parseToInt(minLimit);
//			sbQuery1.append(" order by pmc.pro_id limit 10 offset "+intOffset+"");

			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("pst==>" + pst);
			double dblBugedtedAmt = 0;
			double dblActualAmt = 0;
			double dblBillableAmt = 0;
			
			double dblBugedtedTime = 0; 
			double dblActualTime = 0;
			
			double dblIdealTimeHrs = 0; 
			double dblActualTimeHrs = 0;
			
			Map<String, String> hmProPerformaceBudget = new HashMap<String, String>();
			Map<String, String> hmProPerformaceActual = new HashMap<String, String>();
			Map<String, String> hmProPerformaceBillable = new HashMap<String, String>();
			
			Map<String, String> hmProPerformaceIdealTime = new HashMap<String, String>();
			Map<String, String> hmProPerformaceActualTime = new HashMap<String, String>();
			Map<String, String> hmProPerformaceProjectName = new HashMap<String, String>();
			Map<String, String> hmProPerformaceCurrency = new HashMap<String, String>();
			
			Map<String, String> hmProPerformaceProjectProfit = new HashMap<String, String>();
			Map<String, String> hmProPerformaceProjectManager = new HashMap<String, String>();
			
			Map<String, String> hmProPerformaceProjectAmountIndicator = new HashMap<String, String>();
			Map<String, String> hmProPerformaceProjectTimeIndicator = new HashMap<String, String>();
			
			Map<String, String> hmProName = new HashMap<String, String>();
			
			List<String> alProjectId = new ArrayList<String>(); 
			
			Map<String, String> hmProActIdealTimeHRS = new HashMap<String, String>();
			
//			double dblEmpRate = 0.0d;
			StringBuilder sb = new StringBuilder();
			StringBuilder sbMonth = new StringBuilder();
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			while(rs.next()) {
				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));

				Map<String, String> hmProActualCostTime = new HashMap<String, String>();
				Map<String, String> hmProBillCost = new HashMap<String, String>();
				if("M".equalsIgnoreCase(rs.getString("actual_calculation_type"))) { 
					hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData);
					hmProBillCost = CF.getMonthlyProjectBillableCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData);
				} else {
					hmProActualCostTime = CF.getProjectActualCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
					hmProBillCost = CF.getProjectBillableCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
				}
				Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con, uF, rs.getString("pro_id"), hmProjectData);
//				strProjectIdNew = rs.getString("pro_id");
				double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
//				System.out.println(rs.getString("pro_id") + "  dblReimbursement ===>> " + dblReimbursement);
				hmProName.put(rs.getString("pro_id"), rs.getString("pro_name"));
				hmProjectClient.put(rs.getString("pro_id"), CF.getClientNameById(con, rs.getString("client_id")));
				
				 if("F".equalsIgnoreCase(rs.getString("billing_type"))) { 
					 dblBillableAmt = uF.parseToDouble(rs.getString("billing_amount"));
				 } else {
					 dblBillableAmt = uF.parseToDouble(hmProBillCost.get("proBillableCost"));
				 }
				 
				 dblBugedtedTime = uF.parseToDouble(hmProBudgetedCostAndTime.get("proBudgetedTime"));
				 
				 if("H".equalsIgnoreCase(rs.getString("actual_calculation_type"))) {
//					 dblEmpRate = uF.parseToDouble(hmEmpGrossAmountMapH.get(rs.getString("a_emp_id")));
					 dblActualTime = uF.parseToDouble(hmProActualCostTime.get("proActualTime"));
					 hmProPerformaceActualTime.put(rs.getString("pro_id"), uF.getTotalTimeMinutes100To60(""+dblActualTime)+" hours");
					 hmProPerformaceIdealTime.put(rs.getString("pro_id"), uF.getTotalTimeMinutes100To60(rs.getString("idealtime"))+" hours");
				 } else if("M".equalsIgnoreCase(rs.getString("actual_calculation_type"))) { 
					 dblActualTime = uF.parseToDouble(hmProActualCostTime.get("proActualTime"));
					 hmProPerformaceActualTime.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(dblActualTime)+" months");
					 hmProPerformaceIdealTime.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("idealtime")))+" months");
				 } else {
//					 dblEmpRate = uF.parseToDouble(hmEmpGrossAmountMapD.get(rs.getString("a_emp_id")));
					 dblActualTime = uF.parseToDouble(hmProActualCostTime.get("proActualTime"));
					 hmProPerformaceActualTime.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(dblActualTime)+" days");
					 hmProPerformaceIdealTime.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("idealtime")))+" days");
				 }
				 
				 dblBugedtedAmt = uF.parseToDouble(hmProBudgetedCostAndTime.get("proBudgetedCost"));
				 dblActualAmt = uF.parseToDouble(hmProActualCostTime.get("proActualCost"));
				 
				 hmProPerformaceBudget.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(dblBugedtedAmt));
				 hmProPerformaceActual.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(dblActualAmt + dblReimbursement));
				 hmProPerformaceBillable.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(dblBillableAmt));
				 
				 hmProPerformaceProjectName.put(rs.getString("pro_id"), rs.getString("pro_name"));
				 Map<String, String> hmCurr = hmCurrencyMap.get(rs.getString("curr_id"));
				 hmProPerformaceCurrency.put(rs.getString("pro_id"), hmCurr != null ? hmCurr.get("SHORT_CURR") : "");
				 
				 double diff = 0;
				 if(dblBillableAmt > 0) {
					 diff = ((dblBillableAmt - (dblActualAmt + dblReimbursement))/dblBillableAmt) * 100;
				 }
				 hmProPerformaceProjectProfit.put(rs.getString("pro_id"), uF.formatIntoOneDecimal(diff)+"%");
					
				if (dblActualAmt > dblBugedtedAmt && dblActualAmt < dblBillableAmt) {
					 /*hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
					hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
					
					
				} else if(dblActualAmt < dblBugedtedAmt) {
					/*hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
					hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
					
				} else if(dblActualAmt > dblBillableAmt) {
					/*hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
					hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
					
				} else {
					hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "&nbsp;");
				}
				 
				Date dtDeadline = uF.getDateFormat(rs.getString("deadline"), DBDATE);
				Date dtCurrentDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
				
				if(dtDeadline!=null && dtCurrentDate!=null && (dtDeadline.after(dtCurrentDate) || dtDeadline.equals(dtCurrentDate))) {
					if(dblActualTime <= dblBugedtedTime) {
						/*hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
						hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
						
					} else {
						/*hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
						hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
						
					}
				} else if(dtDeadline!=null && dtCurrentDate!=null && dtCurrentDate.after(dtDeadline)) {
					/*hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
					hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
				} else {
					hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "");
				}
				
			 	if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
//			 		hmProPerformaceProjectManager.put(rs.getString("pro_id"), uF.showData(hmEmpNMap.get(hmTeamLead.get(rs.getString("pro_id"))), ""));
			 		hmProPerformaceProjectManager.put(rs.getString("pro_id"), uF.showData(hmEmpNMap.get(rs.getString("added_by")), ""));
				} else {
					hmProPerformaceProjectManager.put(rs.getString("pro_id"), uF.showData(hmEmpNMap.get(rs.getString("added_by")), ""));
				}
			 	

//				 ********************************* Time in HRS ***************************************
				 dblActualTimeHrs = uF.parseToDouble(hmProActIdealTimeHRS.get(rs.getString("pro_id")+"ACT_TIME_HRS"));
				 String proActualTimeHRS = getProjectActualTimeHRS(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
				 dblActualTimeHrs += uF.parseToDouble(proActualTimeHRS);
				 
				 dblIdealTimeHrs = uF.parseToDouble(hmProActIdealTimeHRS.get(rs.getString("pro_id")+"IDEAL_TIME_HRS"));
				 String proIdealTimeHRS = getProjectIdealTimeHRS(con, uF, rs.getString("pro_id"), hmProjectData);
				 dblIdealTimeHrs += uF.parseToDouble(proIdealTimeHRS);
				 
				 hmProActIdealTimeHRS.put(rs.getString("pro_id")+"_ACT_TIME_HRS", uF.formatIntoTwoDecimalWithOutComma(dblActualTimeHrs));
				 hmProActIdealTimeHRS.put(rs.getString("pro_id")+"_IDEAL_TIME_HRS", uF.formatIntoTwoDecimalWithOutComma(dblIdealTimeHrs));
				 
				 if(!alProjectId.contains(rs.getString("pro_id"))) {
					 alProjectId.add(rs.getString("pro_id"));
				 }
			}
			rs.close();
			pst.close();
			
//			System.out.println("alProjectId ===>> " + alProjectId);
			
			pro_name= new StringBuilder();
			billable_amount= new StringBuilder();
			budgeted_cost= new StringBuilder();
			actual_amount= new StringBuilder();
//			
			StringBuilder projectPerformance 	= new StringBuilder();
			for(int i=0; i<alProjectId.size(); i++) {
				pro_name.append("'"+hmProPerformaceProjectName.get(alProjectId.get(i))+" "+hmProPerformaceCurrency.get(alProjectId.get(i))+"',");
				billable_amount.append(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProPerformaceBillable.get(alProjectId.get(i))))+",");
				budgeted_cost.append(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProPerformaceBudget.get(alProjectId.get(i))))+",");
				actual_amount.append(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProPerformaceActual.get(alProjectId.get(i))))+",");
				
				projectPerformance.append("{'project':'"+hmProPerformaceProjectName.get(alProjectId.get(i)).replaceAll("[^a-zA-Z0-9]", "")+"', " +
					"'Billable Cost': "+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProPerformaceBillable.get(alProjectId.get(i))))+"," +
					"'Budgeted Cost': "+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProPerformaceBudget.get(alProjectId.get(i))))+"," +
					"'Actual Cost': "+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProPerformaceActual.get(alProjectId.get(i))))+"},");
			}
			
			if(pro_name.length()>1) {
				pro_name.replace(0, pro_name.length(), pro_name.substring(0, pro_name.length()-1));
				billable_amount.replace(0, billable_amount.length(), billable_amount.substring(0, billable_amount.length()-1));
				budgeted_cost.replace(0, budgeted_cost.length(), budgeted_cost.substring(0, budgeted_cost.length()-1));
				actual_amount.replace(0, actual_amount.length(), actual_amount.substring(0, actual_amount.length()-1));
				
				projectPerformance.replace(0, projectPerformance.length(), projectPerformance.substring(0, projectPerformance.length()-1));
			}
			
			
//			request.setAttribute("hmProPerformaceBillable", hmProPerformaceBillable);
//			request.setAttribute("hmProPerformaceActual", hmProPerformaceActual);
//			request.setAttribute("hmProPerformaceBudget", hmProPerformaceBudget);
//			request.setAttribute("hmProPerformaceProjectProfit", hmProPerformaceProjectProfit);
//			request.setAttribute("hmProPerformaceProjectAmountIndicator", hmProPerformaceProjectAmountIndicator);
//			request.setAttribute("hmProPerformaceProjectTimeIndicator", hmProPerformaceProjectTimeIndicator);
			
//			request.setAttribute("hmProPerformaceActualTime", hmProPerformaceActualTime);
//			request.setAttribute("hmProPerformaceIdealTime", hmProPerformaceIdealTime);
//			
//			request.setAttribute("hmProPerformaceProjectName", hmProPerformaceProjectName);
//			request.setAttribute("hmProPerformaceCurrency", hmProPerformaceCurrency);
//			request.setAttribute("hmProPerformaceProjectManager", hmProPerformaceProjectManager);
//			
//			request.setAttribute("hmProjectClient", hmProjectClient);
//			request.setAttribute("alProjectId", alProjectId);
			
//			request.setAttribute("alOuter",alOuter);
//			System.out.println("projectPerformance ===>> " + projectPerformance.toString());
			request.setAttribute("pro_name", pro_name.toString());
//			request.setAttribute("pro_amount",sb.toString());
			request.setAttribute("billable_amount", billable_amount.toString());
			request.setAttribute("budgeted_cost", budgeted_cost.toString());
			request.setAttribute("actual_amount", actual_amount.toString());
			
			request.setAttribute("projectPerformance", projectPerformance.toString());
			
//			request.setAttribute("hmProActIdealTimeHRS", hmProActIdealTimeHRS);
		} catch (Exception e) {
			e.printStackTrace();
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
		return proIdealTimeHrs;
	}
	
	
	public String getProjectActualTimeHRS(Connection con, CommonFunctions CF, UtilityFunctions uF, String proId, Map<String, String> hmProjectData, boolean isSubmit, boolean isApprove) {
		PreparedStatement pst=null;
		ResultSet rs=null;
		String proActualTimeHrs = null;
		try {
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(a1.hrs) actual_hrs, sum(a1.days) actual_days, a1.emp_id from (select sum(ta.actual_hrs) hrs, " +
				"count(distinct ta.task_date) days, ta.emp_id, ta.activity_id from task_activity ta where task_date between ? and ? ");
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
			pst.setDate(1, uF.getDateFormat(hmProjectData.get("PRO_START_DATE"), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_END_DATE"), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(proId));
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
		return proActualTimeHrs;
	}

	private void getProjectWorkProgressDetails(Connection con, UtilityFunctions uF, boolean poFlag) {
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
			sbQuery.append("select * from projectmntnc pmt where pmt.pro_id > 0 and pmt.approve_status = 'n'");
			if(uF.parseToInt(getStrProType())==1 && strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and (pmt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(uF.parseToInt(getStrProType())==1 && strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and (pmt.wlocation_id="+uF.parseToInt(strWLocId)+")");
			}
		//===start parvez date: 14-10-2022===	
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(ACCOUNTANT))) {
//				sbQuery.append(" and (pmt.project_owner="+uF.parseToInt(strSessionEmpId)+" or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
				sbQuery.append(" and (pmt.project_owners like '%,"+strSessionEmpId+",%' or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
				/*sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owner = "+uF.parseToInt(strSessionEmpId)+")");*/
				sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owners like '%,"+strSessionEmpId+",%')");
//				sbQuery.append(" and pmt.project_owner="+uF.parseToInt(strSessionEmpId));
			}
		//===end parvez date: 14-10-2022===	
			sbQuery.append("order by pmt.pro_name, pmt.pro_id desc limit 5");
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
            
			if(hmProject != null && hmProject.size() > 0) {
                Iterator<String> it = hmProject.keySet().iterator();
				while(it.hasNext()) {
					String strProId = it.next();
					StringBuilder sbCompleteCount = null;
					StringBuilder sbActiveCount = null;
					StringBuilder sbOverDueCount = null;  
					int nCompleteCnt=0;
					int nActiveCnt=0;
					int nOverdueCnt=0;
					int x = 0;
					for(int i = 0; weekdates!=null && i < weekdates.size();i++) {
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
//						System.out.println("pst======>"+pst);
						rs = pst.executeQuery();
						int nComplete = 0;
						while(rs.next()) {
							nComplete = rs.getInt("cnt");
						}
						rs.close();
						pst.close();
						
						if(sbCompleteCount == null) {
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
						while(rs.next()) {
							nActive = rs.getInt("cnt");
						}
						rs.close();
						pst.close();
						
						if(sbActiveCount == null) {
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
						while(rs.next()) {
							nOverdue = rs.getInt("cnt");
						}
						rs.close();
						pst.close();
						
						if(sbOverDueCount == null) {
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

//	private void getProjectCosting(Connection con, UtilityFunctions uF, boolean poFlag) {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		try{
//			
//			
//			String strStartDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 30), DBDATE, DATE_FORMAT);
//			String strEndDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
//			
////			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", true, null, null, uF);
//			
//			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
//			Map<String, String> hmPprojectClientMap = CF.getProjectClientMap(con, uF);
//			if(hmPprojectClientMap == null) hmPprojectClientMap = new HashMap<String, String>();
//			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
//			if(hmCustName == null) hmCustName = new HashMap<String, String>();
//			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
//			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
//			
//			Map<String, String> hmReimbursementAmountMap = new HashMap<String, String>();
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select sum(reimbursement_amount) as reimbursement_amount, pro_id as group_type from emp_reimbursement where approval_2 = 1 " +
//					"and pro_id in (select pmc.pro_id from projectmntnc pmc, activity_info ai where ai.pro_id = pmc.pro_id ");
//				sbQuery.append(" and pmc.approve_status = 'approved'");
//			sbQuery.append(" )  and reimbursement_type1 = 'P' group by pro_id");
//			pst = con.prepareStatement(sbQuery.toString());
////			System.out.println("pst===>"+pst);  
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				String arr[] = null;
//				if (rs.getString("group_type") != null) {
//					arr = rs.getString("group_type").split(",");
//					hmReimbursementAmountMap.put(arr[0], rs.getString("reimbursement_amount"));
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select * from project_emp_details where pro_id>0 order by pro_id");
//			rs=pst.executeQuery();
//			Map<String, String> hmProEmpRate = new HashMap<String, String>();
//			while(rs.next()) {
//				hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_HOUR", rs.getString("emp_actual_rate_per_hour"));
//				hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_DAY", rs.getString("emp_actual_rate_per_day"));
//				hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_MONTH", rs.getString("emp_actual_rate_per_month"));
//				hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_HOUR", rs.getString("emp_rate_per_hour"));
//				hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_DAY", rs.getString("emp_rate_per_day"));
//				hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_MONTH", rs.getString("emp_rate_per_month"));
//				
//			}
////			System.out.println("hmProEmpRate ===>> " + hmProEmpRate);
//			rs.close();
//			pst.close();
//			
////			sbQuery = new StringBuilder();
////			sbQuery.append("select org_id, calculation_type, days from cost_calculation_settings ");
////			pst = con.prepareStatement(sbQuery.toString());
//////			System.out.println("pst ===> " + pst);
////			rs = pst.executeQuery();
////			Map<String, String> hmOrgCalType = new HashMap<String, String>();
////			while (rs.next()) {
////				hmOrgCalType.put(rs.getString("org_id"), rs.getString("calculation_type"));
////				hmOrgCalType.put(rs.getString("org_id")+"_DAYS", rs.getString("days"));
////			}
//			
//			sbQuery = new StringBuilder();
//			sbQuery.append("select * from projectmntnc pmc where pmc.approve_status = 'approved' ");
//			if(poFlag && uF.parseToInt(getStrProType()) == 2) {
//				sbQuery.append(" and pmc.project_owner="+uF.parseToInt(strSessionEmpId));
//			}
////			if(strStartDate!=null && strEndDate!=null) {
////				sbQuery.append(" and pmc.start_date between '"+uF.getDateFormat(strStartDate, DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE)+"' ");
////			}
//			sbQuery.append(" order by pro_id limit 5");
//			pst = con.prepareStatement(sbQuery.toString());
////			System.out.println("pst ===> " + pst);  
//			rs=pst.executeQuery();
//			StringBuilder sbProCosting = new StringBuilder();
//			while(rs.next()) {
//				
////				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
//				Map<String, String> hmProjectData = new HashMap<String, String>();
//				hmProjectData.put("PRO_ID", rs.getString("pro_id"));
//				hmProjectData.put("PRO_NAME", rs.getString("pro_name"));
//				hmProjectData.put("PRO_CUSTOMER_NAME", uF.showData(hmPprojectClientMap.get(rs.getString("client_id")), ""));
//				hmProjectData.put("PRO_CUST_SPOC_ID", rs.getString("poc"));
//				hmProjectData.put("PRO_CUST_SPOC_NAME", uF.showData(hmCustName.get(rs.getString("poc")), ""));
//				hmProjectData.put("PRO_OWNER_ID", rs.getString("project_owner"));
//				hmProjectData.put("PRO_OWNER_NAME", uF.showData(hmEmpCodeName.get(rs.getString("project_owner")), ""));
//				hmProjectData.put("PRO_BILLING_TYPE", CF.getBillinType(rs.getString("billing_type")));
//				hmProjectData.put("PRO_BILL_TYPE", rs.getString("billing_type"));
//				hmProjectData.put("PRO_BILLING_ACTUAL_TYPE", rs.getString("actual_calculation_type"));
//				hmProjectData.put("PRO_BILLING_FREQUENCY", CF.getBillinFreq(rs.getString("billing_kind"), rs.getString("billing_type")));
//				hmProjectData.put("PRO_BILL_FREQUENCY", rs.getString("billing_kind"));
//				hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
//				hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
//				hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
//				hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
//				hmProjectData.put("PRO_SERVICE_ID", rs.getString("service"));
//				hmProjectData.put("PRO_REPORT_CURR_ID", rs.getString("curr_id"));
//				hmProjectData.put("PRO_BILLING_CURR_ID", rs.getString("billing_curr_id"));
//				hmProjectData.put("PRO_BILLING_AMOUNT", rs.getString("billing_amount"));
//				hmProjectData.put("PRO_ORG_ID", rs.getString("org_id"));
//				
////				Map<String, String> hmProActualCostTimeAndBillCost = CF.getProjectActualCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
//				Map<String, String> hmProActualCostTimeAndBillCost = CF.getProjectActualCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation); //,hmOrgCalType
//				double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
//				
//				sbProCosting.append("{'project':'"+rs.getString("pro_name").replaceAll("[^a-zA-Z0-9]", "")+"',");
//				sbProCosting.append("'salary': "+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProActualCostTimeAndBillCost.get("proActualCost")))+",");
//				sbProCosting.append("'reimbursement': "+uF.formatIntoTwoDecimalWithOutComma(dblReimbursement)+"");
//				sbProCosting.append("},");
//				
//			}
//			rs.close();
//			pst.close();
//			
//			if(sbProCosting.length()>1) {
//				sbProCosting.replace(0, sbProCosting.length(), sbProCosting.substring(0, sbProCosting.length()-1));
//	        }
//			
//			request.setAttribute("sbProCosting", sbProCosting.toString());
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if(rs != null) {
//				try {
//					rs.close();
//				} catch (SQLException e1) {
//					e1.printStackTrace();
//				}
//			}
//			if(pst != null) {
//				try {
//					pst.close();
//				} catch (SQLException e1) {
//					e1.printStackTrace();
//				}
//			}
//		}
//	}

	private void getProjectDetailsByProject(Connection con, UtilityFunctions uF, boolean poFlag) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			
			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;
//			System.out.println("getprofitabilityFYear() ===>> " + getProfitabilityFYear());
			
			if(getProfitabilityFYear() != null && !getProfitabilityFYear().equals("")) {
				strCalendarYearDates = getProfitabilityFYear().split("-");
			} else {
				strCalendarYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			}
			strCalendarYearStart = strCalendarYearDates[0];
			strCalendarYearEnd = strCalendarYearDates[1];
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select pro_id from projectmntnc pmt where pmt.approve_status = 'approved' and pmt.pro_id > 0 ");
			if(uF.parseToInt(getStrProType())==1 && strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and (pmt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(uF.parseToInt(getStrProType())==1 && strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and (pmt.wlocation_id="+uF.parseToInt(strWLocId)+")");
			}
		//===start parvez date: 14-10-2022===	
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(ACCOUNTANT))) {
//				sbQuery.append(" and (pmt.project_owner="+uF.parseToInt(strSessionEmpId)+" or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
				sbQuery.append(" and (pmt.project_owners like '%,"+strSessionEmpId+",%' or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
				/*sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owner = "+uF.parseToInt(strSessionEmpId)+")");*/
				sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owners like '%,"+strSessionEmpId+",%')");
//				sbQuery.append(" and pmt.project_owner="+uF.parseToInt(strSessionEmpId));
			}
		//===end parvez date: 14-10-2022===	
			sbQuery.append(" and ((start_date <= '" + uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "') ");
			sbQuery.append(" or (start_date >= '" + uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "' and start_date <= '"+ uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, DBDATE) + "') ");
			sbQuery.append(" or (start_date >= '" + uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, DBDATE) + "') ");
			sbQuery.append(" or (start_date <= '" + uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, DBDATE) + "') ");
			sbQuery.append(" or (deadline >= '" + uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, DBDATE) + "')) ");
			
			sbQuery.append(" order by pmt.pro_id limit 5");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst Project Dashboard ===>> " + pst);
			rs=pst.executeQuery();
			List<String> alProIdProWise = new ArrayList<String>();
			StringBuilder sbProId = null;
			while(rs.next()) {
				if(sbProId == null) {
					sbProId = new StringBuilder();
					sbProId.append(rs.getString("pro_id"));
				} else {
					sbProId.append(","+rs.getString("pro_id"));
				}
				alProIdProWise.add(rs.getString("pro_id"));
			}
			rs.close();
			pst.close();
			
			if(sbProId == null) {
				sbProId = new StringBuilder();
			}
			
			getProjectDetails(con, uF, poFlag, sbProId.toString());
			
			Map<String, String> hmProPerformaceBillable = (Map<String, String>)request.getAttribute("hmProPerformaceBillable");
			Map<String, String> hmProPerformaceActual = (Map<String, String>)request.getAttribute("hmProPerformaceActual");
			
			StringBuilder sbProfitChart = new StringBuilder();
			sbProfitChart.append("{'Profitability': 'Profitability',");
			List<String> alProfit = new ArrayList<String>();
			int x = 1;
			for (int i = 0; alProIdProWise != null && i < alProIdProWise.size(); i++) {
				String proId = alProIdProWise.get(i);
				double billedAmount = 0;
				double actualCost = 0;
				double profitAmount = 0;
				double profitPercent = 0;
				 
				billedAmount = uF.parseToDouble(hmProPerformaceBillable.get(alProIdProWise.get(i)));
				actualCost = uF.parseToDouble(hmProPerformaceActual.get(alProIdProWise.get(i)));
				
				profitAmount = billedAmount - actualCost;
				if(billedAmount > 0) {
					profitPercent = (profitAmount / billedAmount) * 100;
				} else {
					profitPercent = profitAmount * 100;
				}
				
				sbProfitChart.append("'"+uF.showData(CF.getProjectNameById(con, proId), "N/A")+"': "+uF.formatIntoTwoDecimalWithOutComma(profitAmount)+",");
				StringBuilder sbGraph = new StringBuilder();
				sbGraph.append("var graph"+x+" = new AmCharts.AmGraph();" +
								"graph"+x+".type = \"column\";" +
								"graph"+x+".title = \""+uF.showData(CF.getProjectNameById(con, proId), "N/A")+"\";" +
								"graph"+x+".valueField = \""+uF.showData(CF.getProjectNameById(con, proId), "N/A")+"\";" +
								"graph"+x+".balloonText = \""+uF.showData(CF.getProjectNameById(con, proId), "N/A")+":[[value]]\";" +
								"graph"+x+".lineAlpha = 0;" +
								"graph"+x+".fillAlphas = 1;" +
								"chart1.addGraph(graph"+x+");");
				alProfit.add(sbGraph.toString());
				x++;
			}
			
			if(sbProfitChart.length() > 1) {
				sbProfitChart.replace(0, sbProfitChart.length(), sbProfitChart.substring(0, sbProfitChart.length()-1));
			}
			sbProfitChart.append("}");
			request.setAttribute("sbProfitChart", sbProfitChart.toString());
			request.setAttribute("alProfit", alProfit);
			
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
	
	public void getProjectDetails(Connection con, UtilityFunctions uF, boolean poFlag, String proIds ) {
		
		PreparedStatement pst=null;
		ResultSet rs=null;
		try {
			
			String[] strCalendarYearDates = null;
			String strStartDate = null;
			String strEndDate = null;
			
			strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
			strStartDate = strCalendarYearDates[0];
			strEndDate = strCalendarYearDates[1];
			
//			String strStartDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 30), DBDATE, DATE_FORMAT);
//			String strEndDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			
//			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", true, strStartDate, strEndDate, uF);
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmPprojectClientMap = CF.getProjectClientMap(con, uF);
			if(hmPprojectClientMap == null) hmPprojectClientMap = new HashMap<String, String>();
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
			if(hmCustName == null) hmCustName = new HashMap<String, String>();
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			
			Map<String, String> hmReimbursementAmountMap = new HashMap<String, String>();
			StringBuilder sbQuery = new StringBuilder();
			if(proIds != null && !proIds.equals("")) {
				sbQuery.append("select sum(reimbursement_amount) as reimbursement_amount, pro_id as group_type from emp_reimbursement where approval_2 = 1 " +
					"and pro_id in ("+proIds+") ");
				if (strStartDate != null && strEndDate != null && !strStartDate.equalsIgnoreCase(LABEL_FROM_DATE) && !strEndDate.equalsIgnoreCase(LABEL_TO_DATE)) {
					sbQuery.append(" and from_date between '" + uF.getDateFormat(strStartDate, DATE_FORMAT, DBDATE) + "' and '" + uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE) + "' ");
				}
				sbQuery.append(" and reimbursement_type1 = 'P' group by pro_id");
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
			}

			Map<String, String> hmProEmpRate = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			if(proIds != null && !proIds.equals("")) {
				sbQuery.append("select * from project_emp_details where pro_id>0 and pro_id in ("+proIds+") order by pro_id ");
				pst = con.prepareStatement(sbQuery.toString());
	//			pst = con.prepareStatement("select * from project_emp_details where pro_id>0 order by pro_id");
				rs=pst.executeQuery();
				
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
			}
			
			
			sbQuery = new StringBuilder();
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
			
			double dblBugedtedAmt = 0;
			double dblActualAmt = 0;
			double dblBillableAmt = 0;
			double dblActualTime = 0;
			
			double dblIdealTimeHrs = 0; 
			double dblActualTimeHrs = 0;
			double dblBugedtedTime = 0; 
			
			Map<String, String> hmProPerformaceBudget = new HashMap<String, String>();
			Map<String, String> hmProPerformaceActual = new HashMap<String, String>();
			Map<String, String> hmProPerformaceBillable = new HashMap<String, String>();
			Map<String, String> hmProPerformaceProjectProfit = new HashMap<String, String>();
			Map<String, String> hmProName = new HashMap<String, String>();
			
			StringBuilder sbProCosting = new StringBuilder();
			if(proIds != null && !proIds.equals("")) {
				sbQuery.append("select * from projectmntnc pmt where pmt.pro_id in("+proIds+") order by pmt.pro_id ");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
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
					Map<String, String> hmProBillCost = new HashMap<String, String>();
					if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equals("M")) {
	//					hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData);
						hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation,hmOrgCalType);
	//					hmProBillCost = CF.getMonthlyProjectBillableCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData);
						hmProBillCost = CF.getMonthlyProjectBillableCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation,hmOrgCalType);
					} else {
	//					hmProActualCostTime = CF.getProjectActualCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
						hmProActualCostTime = CF.getProjectActualCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation); //,hmOrgCalType
	//					hmProBillCost = CF.getProjectBillableCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
						hmProBillCost = CF.getProjectBillableCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation); //,hmOrgCalType
					}
					
	//				Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con, uF, rs.getString("pro_id"), hmProjectData);
					Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con,CF, uF, rs.getString("pro_id"), hmProjectData,hmProEmpRate);
					
					double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
					
					hmProName.put(rs.getString("pro_id"), rs.getString("pro_name"));
					
					if("F".equalsIgnoreCase(rs.getString("billing_type"))) { 
						 dblBillableAmt = uF.parseToDouble(rs.getString("billing_amount"));
					 } else {
						 dblBillableAmt = uF.parseToDouble(hmProBillCost.get("proBillableCost"));
					 }
					 
					 dblBugedtedAmt = uF.parseToDouble(hmProBudgetedCostAndTime.get("proBudgetedCost"));
					 dblActualAmt = uF.parseToDouble(hmProActualCostTime.get("proActualCost"));
					 
					 hmProPerformaceBudget.put(rs.getString("pro_id"), uF.formatIntoOneDecimal(dblBugedtedAmt));
					 hmProPerformaceActual.put(rs.getString("pro_id"), uF.formatIntoOneDecimal(dblActualAmt + dblReimbursement));
					 hmProPerformaceBillable.put(rs.getString("pro_id"), uF.formatIntoOneDecimal(dblBillableAmt));
					 
					 double diff = 0;
					 if(dblBillableAmt > 0) {
						 diff = ((dblBillableAmt-(dblActualAmt + dblReimbursement))/dblBillableAmt) * 100;
					 }
					 hmProPerformaceProjectProfit.put(rs.getString("pro_id"), Math.round(diff)+"%");
					
					// ********************************** Project Expenses start ***********************************
					sbProCosting.append("{'project':'"+rs.getString("pro_name").replaceAll("[^a-zA-Z0-9]", "")+"',");
					sbProCosting.append("'salary': "+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProActualCostTime.get("proActualCost")))+",");
					sbProCosting.append("'reimbursement': "+uF.formatIntoTwoDecimalWithOutComma(dblReimbursement)+"");
					sbProCosting.append("},");
					// ********************************** Project Expenses end ***********************************
					
				}
				rs.close();
				pst.close();
			}
			
			// ********************************** Project Expenses start ***********************************
			if(sbProCosting.length()>1) {
				sbProCosting.replace(0, sbProCosting.length(), sbProCosting.substring(0, sbProCosting.length()-1));
	        }
			request.setAttribute("sbProCosting", sbProCosting.toString());
			// ********************************** Project Expenses end ***********************************
			
			
			request.setAttribute("hmProPerformaceBillable", hmProPerformaceBillable);
			request.setAttribute("hmProPerformaceActual", hmProPerformaceActual);
			request.setAttribute("hmProPerformaceBudget", hmProPerformaceBudget);
			request.setAttribute("hmProPerformaceProjectProfit", hmProPerformaceProjectProfit);
			
			
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

	
	private void getCommitmentAndBillOutstandingDetails(Connection con, UtilityFunctions uF, boolean poFlag) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			
			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;
//			System.out.println("getCollectionFYear() ===>> " + getCollectionFYear());
//			System.out.println("getBillsReceiptsCommitFYear() ===>> " + getBillsReceiptsCommitFYear());
			
			if(getOutstandingCommitBilledFYear() != null && !getOutstandingCommitBilledFYear().equals("")) {
				strCalendarYearDates = getOutstandingCommitBilledFYear().split("-");
			} else {
				strCalendarYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			}
			strCalendarYearStart = strCalendarYearDates[0];
			strCalendarYearEnd = strCalendarYearDates[1];
			int nYear = uF.parseToInt(uF.getDateFormat(strCalendarYearEnd+"", DATE_FORMAT, "yy"));
//			System.out.println("strCalendarYearStart ===>> " + strCalendarYearStart + " -- strCalendarYearEnd ===>> " + strCalendarYearEnd);
			/**
			 * Bills & receipts
			 * */
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(oc_invoice_amount) as oc_invoice_amount,extract(month from invoice_generated_date) as invoice_month from " +
				"promntc_invoice_details where invoice_generated_date between ? and ? and pro_id in (select pro_id from projectmntnc pmt where pmt.pro_id > 0 ");
			if(uF.parseToInt(getStrProType())==1 && strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and (pmt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(uF.parseToInt(getStrProType())==1 && strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and (pmt.wlocation_id="+uF.parseToInt(strWLocId)+")");
			}
		//===start parvez date: 14-10-2022===	
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(ACCOUNTANT))) {
//				sbQuery.append(" and (pmt.project_owner="+uF.parseToInt(strSessionEmpId)+" or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
				sbQuery.append(" and (pmt.project_owners like '%,"+strSessionEmpId+",%' or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
				/*sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owner = "+uF.parseToInt(strSessionEmpId)+")");*/
				sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owners like '%,"+strSessionEmpId+",%')");
//				sbQuery.append(" and pmt.project_owner="+uF.parseToInt(strSessionEmpId));
			}
		//===start parvez date: 14-10-2022===	
			sbQuery.append(") group by extract(month from invoice_generated_date) order by extract(month from invoice_generated_date)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmBills = new HashMap<String, String>();
			while(rs.next()) {
				hmBills.put(rs.getString("invoice_month"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("oc_invoice_amount"))));
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(received_amount) as received_amount,extract(month from entry_date) as entry_month from promntc_bill_amt_details " +
					"where entry_date between ? and ? and pro_id in (select pro_id from projectmntnc pmt where pmt.pro_id > 0 ");
			if(uF.parseToInt(getStrProType())==1 && strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and (pmt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(uF.parseToInt(getStrProType())==1 && strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and (pmt.wlocation_id="+uF.parseToInt(strWLocId)+")");
			}
		//===start parvez date: 14-10-2022===	
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(ACCOUNTANT))) {
//				sbQuery.append(" and (pmt.project_owner="+uF.parseToInt(strSessionEmpId)+" or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
				sbQuery.append(" and (pmt.project_owners like '%,"+strSessionEmpId+",%' or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
				/*sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owner = "+uF.parseToInt(strSessionEmpId)+")");*/
				sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owners like '%,"+strSessionEmpId+",%')");
//				sbQuery.append(" and pmt.project_owner="+uF.parseToInt(strSessionEmpId));
			}
		//===end parvez date: 14-10-2022===	
			sbQuery.append(") group by extract(month from entry_date) order by extract(month from entry_date)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmReceipts = new HashMap<String, String>();
			while(rs.next()) {
				hmReceipts.put(rs.getString("entry_month"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("received_amount"))));
			}
			rs.close();
			pst.close();
//			System.out.println("hmReceipts =====> "+hmReceipts);
			
			
			Map<String, String> hmProjectData = new HashMap<String,String>();
			sbQuery = new StringBuilder();
			List<String> monthYearsList = new ArrayList<String>();
			List<String> alInner = new ArrayList<String>();
			sbQuery.append("select cd.client_id, cd.client_name, pmt.pro_id, pmt.pro_name, pmt.org_id, pmt.wlocation_id, pmt.department_id, pmt.start_date," +
					" pmt.deadline, billing_kind, billing_cycle_day, billing_cycle_weekday, billing_amount from client_details cd, projectmntnc pmt " +
					" where cd.client_id = pmt.client_id ");
			if(uF.parseToInt(getStrProType())==1 && strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and (pmt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(uF.parseToInt(getStrProType())==1 && strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and (pmt.wlocation_id="+uF.parseToInt(strWLocId)+")");
			}
		//===start parvez date: 14-10-2022===	
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(ACCOUNTANT))) {
//				sbQuery.append(" and (pmt.project_owner="+uF.parseToInt(strSessionEmpId)+" or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
				sbQuery.append(" and (pmt.project_owners like '%,"+strSessionEmpId+",%' or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
				/*sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owner = "+uF.parseToInt(strSessionEmpId)+")");*/
				sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owners like '%,"+strSessionEmpId+",%')");
//				sbQuery.append(" and pmt.project_owner="+uF.parseToInt(strSessionEmpId));
			}
		//===end parvez date: 14-10-2022===	
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
				while(itr.hasNext()) {
					 double totalOutStandingAmt = 0;
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
						
						totalOutStandingAmt += dblBillAmount;
					}
					
					
					hmCommitAmt.put(dateArr[0], uF.formatIntoTwoDecimalWithOutComma(totalOutStandingAmt));
				}

				StringBuilder sbOutstandingCommitBilled = new StringBuilder();
				Iterator<String> itCR = hmCommitAmt.keySet().iterator();
				while(itCR.hasNext()) {
					String strMonth = itCR.next();
//					System.out.println("strMonth ===>> " + strMonth);
					
					String strCommit = uF.showData(hmCommitAmt.get(strMonth), "0");
					String strReceipts = uF.showData(hmReceipts.get(strMonth), "0");
					String strBills =  uF.showData(hmBills.get(strMonth), "0");
					double outStandCommit = uF.parseToDouble(strCommit) - uF.parseToDouble(strReceipts);
					double outStandBill = uF.parseToDouble(strBills) - uF.parseToDouble(strReceipts);
					
					sbOutstandingCommitBilled.append("{'month':'"+uF.getShortMonth(uF.parseToInt(strMonth))+" "+nYear+"', " +
						"'commitment': "+outStandCommit+"," +
						"'billed': "+outStandBill+"},");
				}
				
				if(sbOutstandingCommitBilled.length()>1) {
					sbOutstandingCommitBilled.replace(0, sbOutstandingCommitBilled.length(), sbOutstandingCommitBilled.substring(0, sbOutstandingCommitBilled.length()-1));
		        }
//				System.out.println("sbCommitReceipts ===>> " + sbCommitReceipts.toString());
//				System.out.println("hmCommitAmt ===>> " + hmCommitAmt);
				request.setAttribute("sbOutstandingCommitBilled", sbOutstandingCommitBilled.toString());
//				request.setAttribute("alOuter", alOuter);
			
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
	
	
	private void getBillsReceiptsDetails(Connection con, UtilityFunctions uF, boolean poFlag) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			
			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;
//			System.out.println("getCollectionFYear() ===>> " + getCollectionFYear());
//			System.out.println("getBillsReceiptsCommitFYear() ===>> " + getBillsReceiptsCommitFYear());
			
			if(getCollectionFYear() != null && !getCollectionFYear().equals("")) {
				strCalendarYearDates = getCollectionFYear().split("-");
				setBillsReceiptsCommitFYear(getCollectionFYear());
			} else if(getBillsReceiptsCommitFYear() != null && !getBillsReceiptsCommitFYear().equals("")) {
				strCalendarYearDates = getBillsReceiptsCommitFYear().split("-");
				setCollectionFYear(getBillsReceiptsCommitFYear());
			} else {
				strCalendarYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			}
			strCalendarYearStart = strCalendarYearDates[0];
			strCalendarYearEnd = strCalendarYearDates[1];
			int nYear = uF.parseToInt(uF.getDateFormat(strCalendarYearEnd+"", DATE_FORMAT, "yy"));
//			System.out.println("strCalendarYearStart ===>> " + strCalendarYearStart + " -- strCalendarYearEnd ===>> " + strCalendarYearEnd);
			/**
			 * Bills & receipts
			 * */
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(oc_invoice_amount) as oc_invoice_amount,extract(month from invoice_generated_date) as invoice_month from " +
				"promntc_invoice_details where invoice_generated_date between ? and ? and pro_id in (select pro_id from projectmntnc pmt where pmt.pro_id > 0 ");
			if(uF.parseToInt(getStrProType())==1 && strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and (pmt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(uF.parseToInt(getStrProType())==1 && strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and (pmt.wlocation_id="+uF.parseToInt(strWLocId)+")");
			}
		//===start parvez date: 14-10-2022===	
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(ACCOUNTANT))) {
//				sbQuery.append(" and (pmt.project_owner="+uF.parseToInt(strSessionEmpId)+" or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
				sbQuery.append(" and (pmt.project_owners like '%,"+strSessionEmpId+",%' or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
				/*sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owner = "+uF.parseToInt(strSessionEmpId)+")");*/
				sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owners like '%,"+strSessionEmpId+",%')");
			}
		//===end parvez date: 14-10-2022===	
			sbQuery.append(") group by extract(month from invoice_generated_date) order by extract(month from invoice_generated_date)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmBills = new HashMap<String, String>();
			while(rs.next()) {
				hmBills.put(rs.getString("invoice_month"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("oc_invoice_amount"))));
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(received_amount) as received_amount,extract(month from entry_date) as entry_month from promntc_bill_amt_details " +
					"where entry_date between ? and ? and pro_id in (select pro_id from projectmntnc pmt where pmt.pro_id > 0 ");
			if(uF.parseToInt(getStrProType())==1 && strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and (pmt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(uF.parseToInt(getStrProType())==1 && strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and (pmt.wlocation_id="+uF.parseToInt(strWLocId)+")");
			}
		//===start parvez date: 14-10-2022===	
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(ACCOUNTANT))) {
//				sbQuery.append(" and (pmt.project_owner="+uF.parseToInt(strSessionEmpId)+" or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
				sbQuery.append(" and (pmt.project_owners like '%,"+strSessionEmpId+",%' or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
				/*sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owner = "+uF.parseToInt(strSessionEmpId)+")");*/
				sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owners like '%,"+strSessionEmpId+",%')");
//				sbQuery.append(" and pmt.project_owner="+uF.parseToInt(strSessionEmpId));
			}
		//===end parvez date: 14-10-2022===	
			sbQuery.append(") group by extract(month from entry_date) order by extract(month from entry_date)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmReceipts = new HashMap<String, String>();
			while(rs.next()) {
				hmReceipts.put(rs.getString("entry_month"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("received_amount"))));
			}
			rs.close();
			pst.close();
//			System.out.println("hmReceipts =====> "+hmReceipts);
			
			
			StringBuilder sbBillsReceipts = new StringBuilder();
			for(int i = 1; i<=12; i++) {
				String strBills = uF.showData(hmBills.get(""+i), "0");
				String strReceipts = uF.showData(hmReceipts.get(""+i), "0");
				
				sbBillsReceipts.append("{'month':'"+uF.getShortMonth(i)+" "+nYear+"', " +
						"'bills': "+uF.parseToDouble(strBills)+"," +
						"'receipts': "+uF.parseToDouble(strReceipts)+"},");
			}
			
			if(sbBillsReceipts.length()>1) {
				sbBillsReceipts.replace(0, sbBillsReceipts.length(), sbBillsReceipts.substring(0, sbBillsReceipts.length()-1));
	        }
//			System.out.println("sbBillsReceipts =====> "+sbBillsReceipts.toString());
			request.setAttribute("sbBillsReceipts", sbBillsReceipts.toString());
			/**
			 * Bills & receipts end
			 * */
			
			/**
			 * Bills donuts by sbu
			 * */
			sbQuery = new StringBuilder();
			sbQuery.append("select pmt.sbu_id, sum(pid.oc_invoice_amount) as oc_invoice_amount from promntc_invoice_details pid, projectmntnc pmt " +
					"where pid.pro_id>0 and pid.pro_id=pmt.pro_id and pid.invoice_generated_date between ? and ? ");
			if(uF.parseToInt(getStrProType())==1 && strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and (pmt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(uF.parseToInt(getStrProType())==1 && strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and (pmt.wlocation_id="+uF.parseToInt(strWLocId)+")");
			}
		//===start parvez date: 14-10-2022===	
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(ACCOUNTANT))) {
//				sbQuery.append(" and (pmt.project_owner="+uF.parseToInt(strSessionEmpId)+" or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
				sbQuery.append(" and (pmt.project_owners like '%,"+strSessionEmpId+",%' or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
				/*sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owner = "+uF.parseToInt(strSessionEmpId)+")");*/
				sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owners like '%,"+strSessionEmpId+",%')");
//				sbQuery.append(" and pmt.project_owner="+uF.parseToInt(strSessionEmpId));
			}
		//===end parvez date: 14-10-2022===	
			sbQuery.append(" group by pmt.sbu_id order by pmt.sbu_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmBillsDoonut = new HashMap<String, String>();
			while(rs.next()) {
				hmBillsDoonut.put(rs.getString("sbu_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("oc_invoice_amount"))));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmSbu = (Map<String, String>)CF.getServicesMap(con, false);;
			if(hmSbu == null) hmSbu = new HashMap<String, String>();
			
			Iterator<String> it1 = hmBillsDoonut.keySet().iterator();
			StringBuilder sbBillsDonut 	= new StringBuilder();
			double dblTotalAmt = 0.0d; 
			while(it1.hasNext()) {
				String strSbuId = it1.next();
				String strBills = uF.showData(hmBillsDoonut.get(strSbuId), "0");
				String strSbuName = uF.showData(hmSbu.get(strSbuId), "");
				//.replaceAll("[^a-zA-Z0-9]", "")
				sbBillsDonut.append("{'sbu':'"+strSbuName+": ', " +
						"'bills': "+uF.parseToDouble(strBills)+"},");
				dblTotalAmt += uF.parseToDouble(strBills);
			}
			if(sbBillsDonut.length()>1) {
				sbBillsDonut.replace(0, sbBillsDonut.length(), sbBillsDonut.substring(0, sbBillsDonut.length()-1));
	        }
			request.setAttribute("sbBillsDonut", sbBillsDonut.toString());
			request.setAttribute("dblTotalAmt", uF.formatIntoTwoDecimal(dblTotalAmt));
			/**
			 * Bills donuts by end
			 * */
			
			
			Map<String, String> hmProjectData = new HashMap<String,String>();
			sbQuery = new StringBuilder();
			List<String> monthYearsList = new ArrayList<String>();
			List<String> alInner = new ArrayList<String>();
			sbQuery.append("select cd.client_id, cd.client_name, pmt.pro_id, pmt.pro_name, pmt.org_id, pmt.wlocation_id, pmt.department_id, pmt.start_date," +
					" pmt.deadline, billing_kind, billing_cycle_day, billing_cycle_weekday, billing_amount from client_details cd, projectmntnc pmt " +
					" where cd.client_id = pmt.client_id ");
			if(uF.parseToInt(getStrProType())==1 && strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and (pmt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(uF.parseToInt(getStrProType())==1 && strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and (pmt.wlocation_id="+uF.parseToInt(strWLocId)+")");
			}
		//===start parvez date: 14-10-2022===	
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(ACCOUNTANT))) {
//				sbQuery.append(" and (pmt.project_owner="+uF.parseToInt(strSessionEmpId)+" or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
				sbQuery.append(" and (pmt.project_owners like '%,"+strSessionEmpId+",%' or pmt.added_by="+uF.parseToInt(strSessionEmpId)+")");
			} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
				/*sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owner = "+uF.parseToInt(strSessionEmpId)+")");*/
				sbQuery.append(" and ( pmt.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt(strSessionEmpId)+" ) or pmt.project_owners like '%,"+strSessionEmpId+",%')");
//				sbQuery.append(" and pmt.project_owner="+uF.parseToInt(strSessionEmpId));
			}
		//===end parvez date: 14-10-2022===	
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
				while(itr.hasNext()) {
					 double totalOutStandingAmt = 0;
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
						
						totalOutStandingAmt += dblBillAmount;
					}
					
					
					hmCommitAmt.put(dateArr[0], uF.formatIntoTwoDecimalWithOutComma(totalOutStandingAmt));
				}

				StringBuilder sbCommitReceipts = new StringBuilder();
				Iterator<String> itCR = hmCommitAmt.keySet().iterator();
				while(itCR.hasNext()) {
					String strMonth = itCR.next();
//					System.out.println("strMonth ===>> " + strMonth);
					
					String strCommit = uF.showData(hmCommitAmt.get(strMonth), "0");
					String strReceipts = uF.showData(hmReceipts.get(strMonth), "0");
					
					sbCommitReceipts.append("{'month':'"+uF.getShortMonth(uF.parseToInt(strMonth))+" "+nYear+"', " +
							"'commitments': "+uF.parseToDouble(strCommit)+"," +
							"'receipts': "+uF.parseToDouble(strReceipts)+"},");
					
				}
				
				if(sbCommitReceipts.length()>1) {
					sbCommitReceipts.replace(0, sbCommitReceipts.length(), sbCommitReceipts.substring(0, sbCommitReceipts.length()-1));
		        }
//				System.out.println("sbCommitReceipts ===>> " + sbCommitReceipts.toString());
				
//				System.out.println("hmCommitAmt ===>> " + hmCommitAmt);
				request.setAttribute("sbCommitReceipts", sbCommitReceipts.toString());
//				request.setAttribute("alOuter", alOuter);
			
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


	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrProType() {
		return strProType;
	}

	public void setStrProType(String strProType) {
		this.strProType = strProType;
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

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public String getCollectionFYear() {
		return collectionFYear;
	}

	public void setCollectionFYear(String collectionFYear) {
		this.collectionFYear = collectionFYear;
	}

	public String getProfitabilityFYear() {
		return profitabilityFYear;
	}

	public void setProfitabilityFYear(String profitabilityFYear) {
		this.profitabilityFYear = profitabilityFYear;
	}

	public String getBillsReceiptsCommitFYear() {
		return billsReceiptsCommitFYear;
	}

	public void setBillsReceiptsCommitFYear(String billsReceiptsCommitFYear) {
		this.billsReceiptsCommitFYear = billsReceiptsCommitFYear;
	}

	public String getProjectExpensesFYear() {
		return projectExpensesFYear;
	}

	public void setProjectExpensesFYear(String projectExpensesFYear) {
		this.projectExpensesFYear = projectExpensesFYear;
	}

	public String getWeeklyWorkProgressFYear() {
		return weeklyWorkProgressFYear;
	}

	public void setWeeklyWorkProgressFYear(String weeklyWorkProgressFYear) {
		this.weeklyWorkProgressFYear = weeklyWorkProgressFYear;
	}

	public String getOutstandingCommitBilledFYear() {
		return outstandingCommitBilledFYear;
	}

	public void setOutstandingCommitBilledFYear(String outstandingCommitBilledFYear) {
		this.outstandingCommitBilledFYear = outstandingCommitBilledFYear;
	}

	public String getProjectPerformanceFYear() {
		return projectPerformanceFYear;
	}

	public void setProjectPerformanceFYear(String projectPerformanceFYear) {
		this.projectPerformanceFYear = projectPerformanceFYear;
	}
	
}
