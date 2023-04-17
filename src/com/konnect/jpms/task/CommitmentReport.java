package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillProject;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CommitmentReport extends ActionSupport implements ServletRequestAware, IStatements
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strEmpId;
	String strUserType;
	HttpServletRequest request;
	 
	String f_org;
	String strStartDate;
	String strEndDate;
	String[] f_strWLocation; 
	String[] f_department;
	String[] f_service;
	String[] f_project_service;
	String[] f_project;
	String[] f_client;
	
	List<FillOrganisation> organisationList;
	List<FillClients> clientList;
	List<FillProject> projectList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillServices> projectServiceList;
	
	
	String freqENDDATE;
	String qfreqENDDATE;
	
	String strProType;
	boolean poFlag;
	
	public String execute() throws Exception {
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/task/CommitmentReport.jsp");
		request.setAttribute(TITLE, "Commitment Report");
		strEmpId =(String) session.getAttribute(EMPID);
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
		
		if(getF_org() == null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		loadCommitmentReport(uF);
		getCommitmentReport(uF);
		return "load";
	}
	
	private boolean checkProjectOwner(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean poFlag = false;
		try {
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 12-10-2022===	
//			sbQuery.append("select * from projectmntnc pmc where project_owner=?");
			sbQuery.append("select * from projectmntnc pmc where project_owners like '%,"+strEmpId+",%' ");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strEmpId));
		//===end parvez date: 12-10-2022===		
			rs = pst.executeQuery();
			if(rs.next()) {
				poFlag = true;
			}
			rs.close();
			pst.close();
			
			setPoFlag(poFlag);
			
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
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return poFlag;
	}
	
	/*public void getCommitmentReport(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		List<List<String>> alOuter = new ArrayList<List<String>>();
		PreparedStatement pst = null;
		ResultSet rs  = null;

		try {
				con = db.makeConnection(con);
				
//				System.out.println("in getCommitmentReport ----!");
				boolean poFlag = checkProjectOwner(con, uF);
//				System.out.println("poFlag ---->> " + poFlag);
				Map<String, String> hmProjectData = new HashMap<String,String>();
				StringBuilder sbQuery = new StringBuilder();
				
				List<String> monthYearsList = new ArrayList<String>();
				List<String> alInner = new ArrayList<String>();
				
				Map<String, String> hmProWLocation = CF.getWLocationMap(con, null, request, null);
				Map<String, String> hmDepartment = CF.getDeptMap(con);
			
				sbQuery.append("select cd.client_id, cd.client_name, p.pro_id, p.pro_name, p.org_id, p.wlocation_id, p.department_id, p.start_date," +
						" p.deadline, billing_kind, billing_cycle_day, billing_cycle_weekday, billing_amount from client_details cd, projectmntnc p " +
						" where cd.client_id = p.client_id ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and p.org_id in ("+getF_org()+")");
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and p.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		            sbQuery.append(" and p.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and p.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
				if(getF_department() != null && getF_department().length>0) {
					sbQuery.append(" and p.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
				}
				if(getF_service() != null && getF_service().length>0) {
					sbQuery.append(" and p.sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
				}
				if(getF_project_service() != null && getF_project_service().length>0) {
					String services = uF.getConcateData(getF_project_service());
					sbQuery.append(" and p.service in ("+services+") ");
				}
				if(getF_client() != null && getF_client().length>0) {
					sbQuery.append(" and cd.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
				}
				if(getF_project()!=null && getF_project().length>0 ){
					sbQuery.append(" and p.pro_id in ("+StringUtils.join(getF_project(), ",")+") ");
				} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
					sbQuery.append(" and p.project_owner="+uF.parseToInt(strEmpId));
				}
//				sbQuery.append(" and p.pro_id in (1379) ");
				
				if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
					sbQuery.append(" and ((start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "') ");
					sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and start_date <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
					sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
					sbQuery.append(" or (start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
					sbQuery.append(" or (deadline >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "')) ");
				}
				sbQuery.append(" order by cd.client_name ");
		        pst = con.prepareStatement(sbQuery.toString());
//		        System.out.println("pst =====>> "  +pst);
				rs = pst.executeQuery();
		
				while(rs.next()) {
					
					alInner.add(rs.getString("pro_id"));
					hmProjectData.put(rs.getString("pro_id")+"_CLIENT_NAME", rs.getString("client_name"));
					hmProjectData.put(rs.getString("pro_id")+"_PRO_NAME", rs.getString("pro_name"));
					hmProjectData.put(rs.getString("pro_id")+"_LOCATION", hmProWLocation.get(rs.getString("wlocation_id")));
					hmProjectData.put(rs.getString("pro_id")+"_DEPARTMENT", hmDepartment.get(rs.getString("department_id")));

					hmProjectData.put(rs.getString("pro_id")+"_START_DATE", rs.getString("start_date"));
					hmProjectData.put(rs.getString("pro_id")+"_DEADLINE", rs.getString("deadline"));
					
					hmProjectData.put(rs.getString("pro_id")+"_BILL_FREQ", rs.getString("billing_kind"));
					hmProjectData.put(rs.getString("pro_id")+"_FREQ_DAY", rs.getString("billing_cycle_day"));
					hmProjectData.put(rs.getString("pro_id")+"_WEEKDAY", rs.getString("billing_cycle_weekday"));
					hmProjectData.put(rs.getString("pro_id")+"_BILLING_AMT", rs.getString("billing_amount"));
					
				}
				rs.close();
				pst.close();
				
//				System.out.println("hmProjectData ===>> " + hmProjectData);
				
//				System.out.println("getStrStartDate ===>> " + getStrStartDate());
//				System.out.println("getStrEndDate ===>> " + getStrEndDate());
				
				if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
						int startDay = 0;
						int endDay = 0;
						int startMonth = 0;
						int endMonth = 0;
						int startYear = 0;
						int endYear = 0;
						int start_month = 0;
						int start_year = 0;
						Date startDate = uF.getDateFormat(getStrStartDate(), DATE_FORMAT);
						Date endDate1 = uF.getDateFormat(getStrEndDate(), DATE_FORMAT);
//						System.out.println("start date ===>> "+startDate+" --- end date ===>> "+endDate);
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
//						System.out.println("monthDiff ===>> " + monthDiff);
//						System.out.println("startMonth ===>> " + startMonth + " -- endMonth ===>> " + endMonth + " -- startYear ===>> " + startYear + " -- endYear ===>> " + endYear);
					    while(monthDiff > 0) {
							monthYearsList.add(String.valueOf(startMonth)+"/"+String.valueOf(startYear));
							startMonth++;
//							System.out.println("startMonth ++ ===>> " + startMonth);
							if(startMonth > 12 && endMonth < 12) {
								startMonth = 1;
								startYear++;
							} else if(startMonth > endMonth && startYear == endYear) {
								break;
							}
						}
//					    System.out.println("startMonth ===>> " + startMonth + " -- startYear ===>> " + startYear);
//					    System.out.println("alInner ===>> " + alInner);
					for(int i=0;i<alInner.size() && alInner!=null && !alInner.isEmpty();i++) {
						String proId = alInner.get(i);
//						System.out.println("proId ===>> " + proId);
						List<String> proList = new ArrayList<String>();
					    proList.add(hmProjectData.get(proId+"_CLIENT_NAME"));
					    proList.add(hmProjectData.get(proId+"_PRO_NAME"));
					    proList.add(hmProjectData.get(proId+"_LOCATION"));
					    proList.add(hmProjectData.get(proId+"_DEPARTMENT"));
					    
					    double totalOutStandingAmt = 0;
					    
					    int proStMnth = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, "MM")); 
						int proStYr = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, "yyyy"));
						
					    int proEndMnth = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, "MM")); 
						int proEndYr = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, "yyyy"));
						
						setQfreqENDDATE(null);
						
						Iterator<String> itr = monthYearsList.iterator();
						while(itr.hasNext()) {
							String month = itr.next();
//							System.out.println("month==>"+month);
							String[] dateArr = month.split("/");
							String strFirstDate = null;
							String strEndDate = null;
							
							String strDate = "01/"+dateArr[0]+"/"+dateArr[1];
							String minMaxDate = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
							String[] tmpDate = minMaxDate.split("::::");
							strFirstDate = tmpDate[0];
							strEndDate = tmpDate[1];
					
//							System.out.println("strFirstDate==>"+ strFirstDate +"strEndDate==>"+ strEndDate);
//							String newStartDate = strFirstDate;
//							System.out.println(proId + " --- newStartDate ===>> " + newStartDate);
							
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
							double dblBillAmount = 0;
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
//								System.out.println("month ===>> " + month + " -- intCount ===>> " + intCount);
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
	//									System.out.println("innerList.get(4) ===>> " + innerList.get(4));
										freqEndDate = hmProjectData.get(proId+"_FREQ_DAY") + "/" + uF.getDateFormat(newStDate, DATE_FORMAT, "MM")+ "/" +uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy");
										freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
//										System.out.println("newStDate ===>> "+ newStDate + " -- freqEndDate 1 ===>> " + freqEndDate +" -- proId ===>> " + proId);
	//									uF.getDateFormat(uF.getPrevDate(freqEndDate, 1)+"", DBDATE, DATE_FORMAT);
										
										Date stDate = uF.getDateFormatUtil(newStDate, DATE_FORMAT);
	//									Date endDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
										Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
										
	//									System.out.println("innerList.get(4) ===>> " + innerList.get(4));
	//									System.out.println("freqDate ===>> " + freqDate + " stDate ===>> " + stDate);
										
										if(freqDate.after(stDate)) {
											frqFlag = true;
										}
//										System.out.println("frqFlag ===>> " + frqFlag+" -- proId ===>> " + proId);
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
//										 System.out.println("Q -- getQfreqENDDATE() ===>> " + getQfreqENDDATE());
										 if(getQfreqENDDATE() == null) {
											 String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
											 String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
											 String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -3)+"", DBDATE, DATE_FORMAT);
											 freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 2, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
//											 freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(arrTmpMinMaxDate[1], 2)+"", DBDATE, DATE_FORMAT);
//												setQfreqENDDATE(freqEndDate);
											 setQfreqENDDATE(freqEndDate);
//											 System.out.println("Q -- freqEndDate ===>> " + freqEndDate);
											} else {
												freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 3)+"", DBDATE, DATE_FORMAT);
//												System.out.println("Q -- else freqEndDate ===>> " + freqEndDate );
											}
										 
										 if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
											freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
//											System.out.println("Q -- _DEADLINE freqEndDate ===>> " + freqEndDate );
										 }
										 if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
											setQfreqENDDATE(freqEndDate);
										 }
//											System.out.println("Q -- freqEndDate out ===>> " + freqEndDate +" -- proId ===>> " + proId);
									}
									if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("H")) {
										Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
										if(getQfreqENDDATE() == null) {
											 String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
											 String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
											 String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -6)+"", DBDATE, DATE_FORMAT);
											 freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 5, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
//											 freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(arrTmpMinMaxDate[1], 5)+"", DBDATE, DATE_FORMAT);
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
//											 System.out.println("arrTmpMinMaxDate[1] ===>> " + arrTmpMinMaxDate[1]);
//											 freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(arrTmpMinMaxDate[1], 11)+"", DBDATE, DATE_FORMAT);
											setQfreqENDDATE(freqEndDate);
//											 System.out.println("arrTmpMinMaxDate[1] freqEndDate ===>> " + freqEndDate);
										} else {
//										System.out.println("freqEndDate ===>> " + freqEndDate+" == getQfreqENDDATE()  ===>> " + getQfreqENDDATE());
											freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 12)+"", DBDATE, DATE_FORMAT);
										}
//										System.out.println("freqEndDate 1 ===>> " + freqEndDate);
										if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
											freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
//											System.out.println("freqEndDate in if ===>> " + freqEndDate);
										}
//										System.out.println("freqDate ===>> " + freqDate +" -- freqEndDate ===>> " + freqEndDate);
										if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
											setQfreqENDDATE(freqEndDate);
										}
									}	
//									System.out.println("freqEndDate out ===>> " + freqEndDate);
									Date firstDate = uF.getDateFormatUtil(strFirstDate, DATE_FORMAT);
									Date endDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
									Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
//									System.out.println("firstDate ===>> " + firstDate +"  endDate ===>> " + endDate + "  newFreqDate ======>> " + newFreqDate +" -- proId ===>> " + proId);
									
									setFreqENDDATE(freqEndDate);
									if((firstDate.before(newFreqDate) || firstDate.equals(newFreqDate)) && (endDate.after(newFreqDate) || endDate.equals(newFreqDate))) {
										dblBillAmount += uF.parseToInt(hmProjectData.get(proId+"_BILLING_AMT"));
									}
								}
							}
//					        totalOutStandingAmt += uF.parseToDouble(hmCommitmentAmt.get(proId));
							totalOutStandingAmt += dblBillAmount;
							
//					        if(hmCommitmentAmt.get(proId) == null) {
//					        	proList.add("0.0");
//					        } else {
					        	proList.add(uF.formatIntoOneDecimalWithOutComma(dblBillAmount));
//					        }
						}
//						System.out.println("pro list--"+proList);
						proList.add(uF.formatIntoOneDecimalWithOutComma(totalOutStandingAmt));
						alOuter.add(proList);
						}
					}
					request.setAttribute("monthsYearList", monthYearsList);
					System.out.println("CR/499--monthYearsList="+monthYearsList);
					request.setAttribute("alOuter", alOuter);
					
//					System.out.println("alOuter ===>> " + alOuter);
				
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					db.closeResultSet(rs);
					db.closeStatements(pst);
					db.closeConnection(con);
				}
		}*/
	
//===start parvez date: 28-01-2022===	
	
	public void getCommitmentReport(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		List<List<String>> alOuter = new ArrayList<List<String>>();
		PreparedStatement pst = null;
		ResultSet rs  = null;

		try {
				con = db.makeConnection(con);

				boolean poFlag = checkProjectOwner(con, uF);
				
				Map<String, String> hmProjectData = new HashMap<String,String>();
				StringBuilder sbQuery = new StringBuilder();
				
				List<String> monthYearsList = new ArrayList<String>();
				List<String> alInner = new ArrayList<String>();
				
				Map<String, String> hmProWLocation = CF.getWLocationMap(con, null, request, null);
				Map<String, String> hmDepartment = CF.getDeptMap(con);
				Map<String, String> hmSbuName = CF.getServicesMap(con, false);
				
				sbQuery.append("select cd.client_id, cd.client_name, p.pro_id, p.pro_name, p.org_id, p.project_code, p.wlocation_id, p.department_id, p.sbu_id, p.start_date," +
						" p.deadline, billing_type, billing_kind, billing_cycle_day, billing_cycle_weekday, billing_amount from client_details cd, projectmntnc p " +
						" where cd.client_id = p.client_id ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and p.org_id in ("+getF_org()+")");
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and p.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		            sbQuery.append(" and p.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and p.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
				if(getF_department() != null && getF_department().length>0) {
					sbQuery.append(" and p.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
				}
				if(getF_service() != null && getF_service().length>0) {
					sbQuery.append(" and p.sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
				}
				if(getF_project_service() != null && getF_project_service().length>0) {
					String services = uF.getConcateData(getF_project_service());
					sbQuery.append(" and p.service in ("+services+") ");
				}
				if(getF_client() != null && getF_client().length>0) {
					sbQuery.append(" and cd.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
				}
				if(getF_project()!=null && getF_project().length>0 ){
					sbQuery.append(" and p.pro_id in ("+StringUtils.join(getF_project(), ",")+") ");
				} else if(poFlag && uF.parseToInt(getStrProType()) == 2) {
				//===start parvez date: 12-10-2022===	
//					sbQuery.append(" and p.project_owner="+uF.parseToInt(strEmpId));
					sbQuery.append(" and p.project_owners like '%,"+strEmpId+",%' ");
				//===end parvez date: 12-10-2022===	
				}
				
				if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
					sbQuery.append(" and ((start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "') ");
					sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and start_date <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
					sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
					sbQuery.append(" or (start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
					sbQuery.append(" or (deadline >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "')) ");
				}
				sbQuery.append(" order by cd.client_name ");
		        pst = con.prepareStatement(sbQuery.toString());
//		        System.out.println("pst =====>> "  +pst);
				rs = pst.executeQuery();
		
				while(rs.next()) {
					
					alInner.add(rs.getString("pro_id"));
					hmProjectData.put(rs.getString("pro_id")+"_CLIENT_NAME", rs.getString("client_name"));
					hmProjectData.put(rs.getString("pro_id")+"_PRO_NAME", rs.getString("pro_name"));
					hmProjectData.put(rs.getString("pro_id")+"_LOCATION", hmProWLocation.get(rs.getString("wlocation_id")));
					hmProjectData.put(rs.getString("pro_id")+"_DEPARTMENT", hmDepartment.get(rs.getString("department_id")));

					hmProjectData.put(rs.getString("pro_id")+"_START_DATE", rs.getString("start_date"));
					hmProjectData.put(rs.getString("pro_id")+"_DEADLINE", rs.getString("deadline"));
					
					hmProjectData.put(rs.getString("pro_id")+"_BILL_FREQ", rs.getString("billing_kind"));
					hmProjectData.put(rs.getString("pro_id")+"_FREQ_DAY", rs.getString("billing_cycle_day"));
					hmProjectData.put(rs.getString("pro_id")+"_WEEKDAY", rs.getString("billing_cycle_weekday"));
					hmProjectData.put(rs.getString("pro_id")+"_BILLING_AMT", rs.getString("billing_amount"));
					hmProjectData.put(rs.getString("pro_id")+"_PRO_CODE", rs.getString("project_code"));
					hmProjectData.put(rs.getString("pro_id")+"_SBU", hmSbuName.get(rs.getString("sbu_id")));
					
					if(rs.getString("billing_kind").equals("O")){
						hmProjectData.put(rs.getString("pro_id")+"_BILL_TYPE", "SINGLE");
					} else if(rs.getString("billing_kind").equals("W")){
						hmProjectData.put(rs.getString("pro_id")+"_BILL_TYPE", "WEEKLY");
					} else if(rs.getString("billing_kind").equals("B")){
						hmProjectData.put(rs.getString("pro_id")+"_BILL_TYPE", "BIWEEKLY");
					} else if(rs.getString("billing_kind").equals("M")){
						hmProjectData.put(rs.getString("pro_id")+"_BILL_TYPE", "MONTHLY");
					} else if(rs.getString("billing_kind").equals("Q")){
						hmProjectData.put(rs.getString("pro_id")+"_BILL_TYPE", "QUARTERLY");
					} else if(rs.getString("billing_kind").equals("H")){
						hmProjectData.put(rs.getString("pro_id")+"_BILL_TYPE", "HALF YEAR");
					} else if(rs.getString("billing_kind").equals("A")){
						hmProjectData.put(rs.getString("pro_id")+"_BILL_TYPE", "YEARLY");
					}
					
				}
				rs.close();
				pst.close();
				
//				System.out.println("hmProjectData ===>> " + hmProjectData);
				
//				System.out.println("getStrStartDate ===>> " + getStrStartDate());
//				System.out.println("getStrEndDate ===>> " + getStrEndDate());
				
				if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
						int startDay = 0;
						int endDay = 0;
						int startMonth = 0;
						int endMonth = 0;
						int startYear = 0;
						int endYear = 0;
						int start_month = 0;
						int start_year = 0;
						Date startDate = uF.getDateFormat(getStrStartDate(), DATE_FORMAT);
						Date endDate1 = uF.getDateFormat(getStrEndDate(), DATE_FORMAT);
//						System.out.println("start date ===>> "+startDate+" --- end date ===>> "+endDate);
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
//						System.out.println("monthDiff ===>> " + monthDiff);
//						System.out.println("startMonth ===>> " + startMonth + " -- endMonth ===>> " + endMonth + " -- startYear ===>> " + startYear + " -- endYear ===>> " + endYear);
					    while(monthDiff > 0) {
							monthYearsList.add(String.valueOf(startMonth)+"/"+String.valueOf(startYear));
//					    	monthYearsList.add(uF.getMonth(startMonth)+"/"+String.valueOf(startYear));
							startMonth++;
//							System.out.println("startMonth ++ ===>> " + startMonth);
							if(startMonth > 12 && endMonth < 12) {
								startMonth = 1;
								startYear++;
							} else if(startMonth > endMonth && startYear == endYear) {
								break;
							}
						}
//					    System.out.println("CR/646--monthYearsList="+monthYearsList);
//					    System.out.println("startMonth ===>> " + startMonth + " -- startYear ===>> " + startYear);
//					    System.out.println("alInner ===>> " + alInner);
					for(int i=0;i<alInner.size() && alInner!=null && !alInner.isEmpty();i++) {
						String proId = alInner.get(i);
//						System.out.println("proId ===>> " + proId);
						List<String> proList = new ArrayList<String>();
						
						proList.add(hmProjectData.get(proId+"_PRO_CODE"));			//0
					    proList.add(hmProjectData.get(proId+"_CLIENT_NAME"));		//1
					    proList.add(hmProjectData.get(proId+"_PRO_NAME"));			//2
					    proList.add(hmProjectData.get(proId+"_LOCATION"));			//3
//					    proList.add(hmProjectData.get(proId+"_DEPARTMENT"));		//4
					    proList.add(hmProjectData.get(proId+"_SBU"));				//4
					    proList.add(hmProjectData.get(proId+"_BILL_TYPE"));			//5
					    
			//===start parvez date: 29-01-2022===
					    pst = con.prepareStatement("select project_milestone_id, pro_milestone_amount, milestone_end_date from project_milestone_details where pro_id=?");
					    pst.setInt(1, uF.parseToInt(proId));
					    rs = pst.executeQuery();
					    List<String> milestoneIds = new ArrayList<String>();
					    Map<String, String> hmMileStoneMap = new HashMap<String, String>();
					    int x = 0;
					    while(rs.next()){
					    	milestoneIds.add(rs.getString("project_milestone_id"));
					    	hmMileStoneMap.put(proId+"_MS_AMT_"+x, rs.getString("pro_milestone_amount"));
					    	hmMileStoneMap.put(proId+"_MS_END_DATE_"+x, rs.getString("milestone_end_date"));
					    	x++;
					    }
					    rs.close();
						pst.close();
			//===start parvez date: 29-01-2022===		    
					    
					    double totalOutStandingAmt = 0;
					    
					    int proStMnth = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, "MM")); 
						int proStYr = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, "yyyy"));
						
					    int proEndMnth = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, "MM")); 
						int proEndYr = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, "yyyy"));
						
						setQfreqENDDATE(null);
						
						Iterator<String> itr = monthYearsList.iterator();
						while(itr.hasNext()) {
							String month = itr.next();
//							System.out.println("month==>"+month);
							String[] dateArr = month.split("/");
							String strFirstDate = null;
							String strEndDate = null;
							
							String strDate = "01/"+dateArr[0]+"/"+dateArr[1];
							String minMaxDate = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
							String[] tmpDate = minMaxDate.split("::::");
							strFirstDate = tmpDate[0];
							strEndDate = tmpDate[1];
					
//							System.out.println("strFirstDate==>"+ strFirstDate +"strEndDate==>"+ strEndDate);
//							String newStartDate = strFirstDate;
//							System.out.println(proId + " --- newStartDate ===>> " + newStartDate);
							
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
					//===start parvez date: 29-01-2022===		
							} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O")) {
								if(milestoneIds.size()>0){
									intCount = milestoneIds.size();
								}
							}	
					//===end parvez date: 29-01-2022===		
							
//							System.out.println(proId + " --- intCount ===>> " + intCount);
							double dblBillAmount = 0;
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
//								System.out.println("month ===>> " + month + " -- intCount ===>> " + intCount);
								for(int j=0; j<intCount; j++) {
									String newStDate = getNewProjectStartDate(con, uF, proId, getFreqENDDATE());
								
									if(newStDate == null || newStDate.equals("")) {
											newStDate = strFirstDate;
									}
	//								System.out.println("newStDate ===>> " + newStDate);
									boolean frqFlag = false;
									String freqEndDate = strEndDate;
									
									if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O")) {
//										freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
								//===start parvez date: 29-01-2021===		
										if(hmMileStoneMap!=null && !hmMileStoneMap.isEmpty() && hmMileStoneMap.get(proId+"_MS_END_DATE_"+j) != null){
											freqEndDate = uF.getDateFormat(hmMileStoneMap.get(proId+"_MS_END_DATE_"+j), DBDATE, DATE_FORMAT);
										} else{
											freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
										}
								//===end parvez date: 29-01-2021===
									}
									
									if(uF.parseToInt(hmProjectData.get(proId+"_FREQ_DAY")) > 0) {
	//									System.out.println("innerList.get(4) ===>> " + innerList.get(4));
										freqEndDate = hmProjectData.get(proId+"_FREQ_DAY") + "/" + uF.getDateFormat(newStDate, DATE_FORMAT, "MM")+ "/" +uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy");
										freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
//										System.out.println("newStDate ===>> "+ newStDate + " -- freqEndDate 1 ===>> " + freqEndDate +" -- proId ===>> " + proId);
	//									uF.getDateFormat(uF.getPrevDate(freqEndDate, 1)+"", DBDATE, DATE_FORMAT);
										
										Date stDate = uF.getDateFormatUtil(newStDate, DATE_FORMAT);
	//									Date endDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
										Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
										
	//									System.out.println("innerList.get(4) ===>> " + innerList.get(4));
	//									System.out.println("freqDate ===>> " + freqDate + " stDate ===>> " + stDate);
										
										if(freqDate.after(stDate)) {
											frqFlag = true;
										}
//										System.out.println("frqFlag ===>> " + frqFlag+" -- proId ===>> " + proId);
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
//										 System.out.println("Q -- getQfreqENDDATE() ===>> " + getQfreqENDDATE());
										 if(getQfreqENDDATE() == null) {
											 String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
											 String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
											 String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -3)+"", DBDATE, DATE_FORMAT);
											 freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 2, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
//											 freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(arrTmpMinMaxDate[1], 2)+"", DBDATE, DATE_FORMAT);
//												setQfreqENDDATE(freqEndDate);
											 setQfreqENDDATE(freqEndDate);
//											 System.out.println("Q -- freqEndDate ===>> " + freqEndDate);
											} else {
												freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 3)+"", DBDATE, DATE_FORMAT);
//												System.out.println("Q -- else freqEndDate ===>> " + freqEndDate );
											}
										 
										 if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
											freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
//											System.out.println("Q -- _DEADLINE freqEndDate ===>> " + freqEndDate );
										 }
										 if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
											setQfreqENDDATE(freqEndDate);
										 }
//											System.out.println("Q -- freqEndDate out ===>> " + freqEndDate +" -- proId ===>> " + proId);
									}
									if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("H")) {
										Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
										if(getQfreqENDDATE() == null) {
											 String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
											 String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
											 String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -6)+"", DBDATE, DATE_FORMAT);
											 freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 5, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
//											 freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(arrTmpMinMaxDate[1], 5)+"", DBDATE, DATE_FORMAT);
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
//											 System.out.println("arrTmpMinMaxDate[1] ===>> " + arrTmpMinMaxDate[1]);
//											 freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(arrTmpMinMaxDate[1], 11)+"", DBDATE, DATE_FORMAT);
											setQfreqENDDATE(freqEndDate);
//											 System.out.println("arrTmpMinMaxDate[1] freqEndDate ===>> " + freqEndDate);
										} else {
//										System.out.println("freqEndDate ===>> " + freqEndDate+" == getQfreqENDDATE()  ===>> " + getQfreqENDDATE());
											freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 12)+"", DBDATE, DATE_FORMAT);
										}
//										System.out.println("freqEndDate 1 ===>> " + freqEndDate);
										if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
											freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
//											System.out.println("freqEndDate in if ===>> " + freqEndDate);
										}
//										System.out.println("freqDate ===>> " + freqDate +" -- freqEndDate ===>> " + freqEndDate);
										if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
											setQfreqENDDATE(freqEndDate);
										}
									}	
//									System.out.println("freqEndDate out ===>> " + freqEndDate);
									Date firstDate = uF.getDateFormatUtil(strFirstDate, DATE_FORMAT);
									Date endDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
									Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
//									System.out.println("firstDate ===>> " + firstDate +"  endDate ===>> " + endDate + "  newFreqDate ======>> " + newFreqDate +" -- proId ===>> " + proId);
									
									setFreqENDDATE(freqEndDate);
									/*if((firstDate.before(newFreqDate) || firstDate.equals(newFreqDate)) && (endDate.after(newFreqDate) || endDate.equals(newFreqDate))) {
										dblBillAmount += uF.parseToInt(hmProjectData.get(proId+"_BILLING_AMT"));
									}*/
									
							//===start parvez date: 29-01-2022===
									if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O") && hmMileStoneMap!=null && !hmMileStoneMap.isEmpty()) {
										if((firstDate.before(newFreqDate) || firstDate.equals(newFreqDate)) && (endDate.after(newFreqDate) || endDate.equals(newFreqDate))) {
											dblBillAmount += uF.parseToInt(hmMileStoneMap.get(proId+"_MS_AMT_"+j));
										}
									} else{
										if((firstDate.before(newFreqDate) || firstDate.equals(newFreqDate)) && (endDate.after(newFreqDate) || endDate.equals(newFreqDate))) {
											dblBillAmount += uF.parseToInt(hmProjectData.get(proId+"_BILLING_AMT"));
										}
									}
							//===end parvez date: 29-01-2022===		
									
								}
							}
//					        totalOutStandingAmt += uF.parseToDouble(hmCommitmentAmt.get(proId));
							totalOutStandingAmt += dblBillAmount;
							
//					        if(hmCommitmentAmt.get(proId) == null) {
//					        	proList.add("0.0");
//					        } else {
					        	proList.add(uF.formatIntoComma(dblBillAmount));		//6
//					        }
						}
//						System.out.println("pro list--"+proList);
						proList.add(uF.formatIntoComma(totalOutStandingAmt));			//7
						alOuter.add(proList);
						}
					}
					request.setAttribute("monthsYearList", monthYearsList);
//					System.out.println("CR/499--monthYearsList="+monthYearsList);
					request.setAttribute("alOuter", alOuter);
					
//					System.out.println("alOuter ===>> " + alOuter);
				
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					db.closeResultSet(rs);
					db.closeStatements(pst);
					db.closeConnection(con);
				}
		}
//===end parvez date: 28-01-2022===	
	
	
//	private String getFrequencyEndDate(UtilityFunctions uF, Date freqDate, int intMonths, String freqEndDt, String proStrtDate, Date freqEdDate, int cnt) {
////		String freqEndDt = null; 
//		
////		System.out.println(" Recurtion FUN == freqDate ===>> " + freqDate+ " -- freqEndDt ===>> " + freqEndDt +" -- intMonths ===>> "+intMonths);
//		
//		String strDate = uF.getDateFormatUtil(freqDate, DATE_FORMAT);
//		int strDateMnth = uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "MM")); 
//		int strDateYr = uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "yyyy"));
//		
//	    int freqEndMnth = uF.parseToInt(uF.getDateFormat(freqEndDt, DATE_FORMAT, "MM")); 
//		int freqEndYr = uF.parseToInt(uF.getDateFormat(freqEndDt, DATE_FORMAT, "yyyy"));
//		
//		Date strPrevdt = uF.getDateFormatUtil(uF.getFutureDate(uF.getDateFormatUtil(proStrtDate, DATE_FORMAT), -32)+"", DBDATE);
////		System.out.println("strPrevdt ===>> " + strPrevdt);
//		if(cnt == 0 && (freqEdDate.equals(uF.getDateFormatUtil(proStrtDate, DATE_FORMAT)) || (freqDate.before(uF.getDateFormatUtil(proStrtDate, DATE_FORMAT)) && freqDate.before(strPrevdt))) ) {
//			freqEndDt = uF.getDateFormat(uF.getFutureMonthDate(proStrtDate, -1)+"", DBDATE, DATE_FORMAT);
//			if(intMonths == 2 || intMonths == 5 || intMonths == 11) {
//				intMonths++;
//			}
////			proStartDate = freqEndDt;
//			cnt++;
//			freqEndDt = getFrequencyEndDate(uF, freqDate, intMonths, freqEndDt, proStrtDate, freqEdDate, cnt);
//		} else if(cnt == 0 && freqDate.before(uF.getDateFormatUtil(proStrtDate, DATE_FORMAT)) && freqDate.after(strPrevdt) ) {
//			freqEndDt = uF.getDateFormat(uF.getFutureMonthDate(proStrtDate, intMonths)+"", DBDATE, DATE_FORMAT);
//			if(intMonths == 2 || intMonths == 5 || intMonths == 11) {
//				intMonths++;
//			}
////			proStartDate = freqEndDt;
//			cnt++;
//			freqEndDt = getFrequencyEndDate(uF, freqDate, intMonths, freqEndDt, proStrtDate, freqEdDate, cnt);
//		} else if(cnt == 0 && freqDate.after(uF.getDateFormatUtil(freqEndDt, DATE_FORMAT)) && ((strDateMnth>freqEndMnth && strDateYr == freqEndYr) || (strDateYr > freqEndYr))) {
//			freqEndDt = uF.getDateFormat(uF.getFutureMonthDate(freqEndDt, intMonths)+"", DBDATE, DATE_FORMAT);
//			if(intMonths == 2 || intMonths == 5 || intMonths == 11) {
//				intMonths++;
//			}
////			proStartDate = freqEndDt;
//			freqEndDt = getFrequencyEndDate(uF, freqDate, intMonths, freqEndDt, proStrtDate, freqEdDate, cnt);
//		} else if(cnt == 0 && strDateMnth ==freqEndMnth && strDateYr == freqEndYr) {
//			freqEndDt = uF.getDateFormat(uF.getFutureMonthDate(freqEndDt, intMonths)+"", DBDATE, DATE_FORMAT);
//			if(intMonths == 2 || intMonths == 5 || intMonths == 11) {
//				intMonths++;
//			}
////			proStartDate = freqEndDt;
//			freqEndDt = getFrequencyEndDate(uF, freqDate, intMonths, freqEndDt, proStrtDate, freqEdDate, cnt);
//		}
////		System.out.println("freqEndDt ===>> " + freqEndDt);
//		// TODO Auto-generated method stub
//		return freqEndDt;
//	}
	

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
	
	
	/*public void getCommitmentReport(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		List<List<String>> alOuter = new ArrayList<List<String>>();
		PreparedStatement pst = null;
		ResultSet rs  = null;

		try {
				con = db.makeConnection(con);
				
				Map<String, String> hmProjectData = new HashMap<String,String>();
								
				StringBuilder sbQuery=new StringBuilder();
//				DateFormat dF =null;
				
				List<String> monthYearsList = new ArrayList<String>();
				List<String> alInner = new ArrayList<String>();
				
				Map<String, String> hmProWLocation = CF.getWLocationMap(con, null, request, null);
				Map<String, String> hmDepartment = CF.getDeptMap(con);
			
				sbQuery.append("select cd.client_id, cd.client_name, p.pro_id, p.pro_name, p.org_id, p.wlocation_id, p.department_id from client_details cd, " +
					" projectmntnc p where cd.client_id = p.client_id ");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and p.org_id in ("+getF_org()+")");
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and p.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		            sbQuery.append(" and p.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and p.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
				
				if(getF_department() != null && getF_department().length>0) {
					sbQuery.append(" and p.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
				}
				
				if(getF_client() != null && getF_client().length>0) {
					sbQuery.append(" and cd.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
				}
				if(getF_project()!=null && getF_project().length>0 ){
					sbQuery.append(" and p.pro_id in ("+StringUtils.join(getF_project(), ",")+") ");
				}
				if(getStrStartDate() != null && !getStrStartDate().equals("From Date") && getStrEndDate() != null && !getStrEndDate().equals("To Date")) {
					sbQuery.append(" and ((start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "') ");
					sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and start_date <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
					sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
					sbQuery.append(" or (start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
					sbQuery.append(" or (deadline >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "')) ");
				}
		        pst = con.prepareStatement(sbQuery.toString());
//		        System.out.println("pst =====>> "  +pst);
				rs = pst.executeQuery();
		
				while(rs.next()) {
					
					alInner.add(rs.getString("pro_id"));
					hmProjectData.put(rs.getString("pro_id")+"_CLIENT_NAME", rs.getString("client_name"));
					hmProjectData.put(rs.getString("pro_id")+"_PRO_NAME", rs.getString("pro_name"));
					hmProjectData.put(rs.getString("pro_id")+"_LOCATION", hmProWLocation.get(rs.getString("wlocation_id")));
					hmProjectData.put(rs.getString("pro_id")+"_DEPARTMENT", hmDepartment.get(rs.getString("department_id")));
				}
				rs.close();
				pst.close();
				
			
					if(getStrStartDate() != null && !getStrStartDate().equals("From Date") && getStrEndDate() != null && !getStrEndDate().equals("To Date")) {
						int startDay=0;
						int endDay=0;
						int startMonth =0;
						int endMonth=0;
						int startYear=0;
						int endYear=0;
						int start_month =0;
						int start_year=0;
//						Date date = calendar.getTime();
//						dF = new SimpleDateFormat("dd/MM/yyyy"); 
						Date startDate = uF.getDateFormat(getStrStartDate(), DATE_FORMAT);
						Date endDate = uF.getDateFormat(getStrEndDate(), DATE_FORMAT);
						
//						System.out.println("start date ===>> "+startDate+" --- end date ===>> "+endDate);
							
						Calendar cal = Calendar.getInstance();
						cal.setTime(startDate);
						startDay = cal.get(Calendar.DATE);
					    start_month = cal.get(Calendar.MONTH)+1;
					    startMonth = start_month;
					    
					    start_year = cal.get(Calendar.YEAR);
					    startYear= start_year;
					    
					    Calendar cal2 = Calendar.getInstance();
						cal2.setTime(endDate);
						endDay = cal2.get(Calendar.DATE);
						endMonth = cal2.get(Calendar.MONTH)+1;
						endYear = cal2.get(Calendar.YEAR);
					
						long monthDiff = uF.getMonthsDifference(startDate, endDate);
					
					    while(monthDiff>0) {
							
							monthYearsList.add(String.valueOf(startMonth)+"/"+String.valueOf(startYear));
							startMonth++;	
							if(startMonth>12) {
								startMonth = 1;
								startYear++;
							} else if(startMonth > endMonth && startYear == endYear) {
								break;
							}
						}
					    
					for(int i=0;i<alInner.size() && alInner!=null && !alInner.isEmpty();i++) {
						String proId = alInner.get(i);
						List<String> proList = new ArrayList<String>();
					    proList.add(hmProjectData.get(proId+"_CLIENT_NAME"));
					    proList.add(hmProjectData.get(proId+"_PRO_NAME"));
					    proList.add(hmProjectData.get(proId+"_LOCATION"));
					    proList.add(hmProjectData.get(proId+"_DEPARTMENT"));
					    
					    double totalOutStandingAmt=0;
					    
						Iterator<String> itr = monthYearsList.iterator();
						while(itr.hasNext()) {
							String month = itr.next();
							String[] dateArr = month.split("/");
							String strFirstDate = null;
							String strEndDate = null;
							
//							if(Integer.parseInt(dateArr[0]) == start_month && Integer.parseInt(dateArr[1]) == start_year && Integer.parseInt(dateArr[0]) == endMonth && Integer.parseInt(dateArr[1]) == endYear) {
//								strFirstDate = startDay+"/"+start_month+"/"+start_year;
//								strEndDate = endDay+"/"+endMonth+"/"+endYear;
//							} else if(Integer.parseInt(dateArr[0]) == start_month && Integer.parseInt(dateArr[1]) == start_year) {
//								String strDate = startDay+"/"+start_month+"/"+start_year;
//								String minMaxDate = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
//								String[] tmpDate = minMaxDate.split("::::");
//								strFirstDate = strDate;
//								strEndDate = tmpDate[1];
//							} else if(Integer.parseInt(dateArr[0]) == endMonth && Integer.parseInt(dateArr[1]) == endYear) {
//								String strDate = endDay+"/"+endMonth+"/"+endYear;
//								String minMaxDate = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
//								String[] tmpDate = minMaxDate.split("::::");
//								strFirstDate = tmpDate[0];
//								strEndDate = strDate;
//							} else {
								String strDate = "01/"+dateArr[0]+"/"+dateArr[1];
								String minMaxDate = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
								String[] tmpDate = minMaxDate.split("::::");
								strFirstDate = tmpDate[0];
								strEndDate = tmpDate[1];
//							}
		
							
					
							sbQuery = new StringBuilder();
							sbQuery.append("select sum(billing_amount) as billing_amount, p.pro_id from projectmntnc p, projectmntnc_frequency pf " +
								" where p.pro_id = pf.pro_id and pf.pro_id = ?");
							
							if(strFirstDate != null && !strFirstDate.equals("") && strEndDate != null && !strEndDate.equals("")) {
								sbQuery.append(" and freq_end_date between '" + uF.getDateFormat(strFirstDate, DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE) + "' ");
							}
							sbQuery.append(" group by p.pro_id");
					        pst = con.prepareStatement(sbQuery.toString());
					        pst.setInt(1, uF.parseToInt(proId));
//					        System.out.println("pst ===>> " + pst);
					        rs = pst.executeQuery();
					        Map<String, String> hmCommitmentAmt = new HashMap<String, String>();
					        
					        while(rs.next()) {
					        	
					        	hmCommitmentAmt.put(rs.getString("pro_id"), uF.formatIntoOneDecimalWithOutComma(rs.getDouble("billing_amount")));
					        }
			        
					        rs.close();
					        pst.close();
					        
					        totalOutStandingAmt += uF.parseToDouble(hmCommitmentAmt.get(proId));
					        if(hmCommitmentAmt.get(proId) == null) {
					        	proList.add("0");
					        } else {
					        	proList.add(hmCommitmentAmt.get(proId));
					        }
						}
						proList.add(uF.formatIntoOneDecimalWithOutComma(totalOutStandingAmt));
						alOuter.add(proList);
						}
					}
					request.setAttribute("monthsYearList", monthYearsList);
					request.setAttribute("alOuter", alOuter);
				
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					db.closeResultSet(rs);
					db.closeStatements(pst);
					db.closeConnection(con);
				}
		}*/
	
	
	
	public String loadCommitmentReport(UtilityFunctions uF) {
		if((getStrStartDate() == null || getStrStartDate().equals("") || getStrStartDate().equalsIgnoreCase("null")) && (getStrEndDate() == null || getStrEndDate().equals("") || getStrEndDate().equalsIgnoreCase("null"))) {
			String currDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
			String minMaxDate = uF.getCurrentMonthMinMaxDate(currDate, DATE_FORMAT);
			String[] tmpDate = minMaxDate.split("::::");
			setStrStartDate(tmpDate[0]);
			setStrEndDate(tmpDate[1]);
		}
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		projectServiceList = new FillServices(request).fillProjectServices();
		
		projectList= new ArrayList<FillProject>();
		if(getF_client() != null && getF_client().length>0) {
			projectList = new FillProject(request).fillProjects(getF_client());
		} else {
			projectList= new FillProject(request).fillProjects();
		}
		clientList = new FillClients(request).fillClients(false);
		
		getSelectedFilter(uF);
		
		return SUCCESS;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if(isPoFlag()) {
			alFilter.add("PROJECT_TYPE");
			if(getStrProType()!=null) {
				String strProType="";
				if(isPoFlag() && uF.parseToInt(getStrProType()) == 2) {
					strProType = "My Projects";
				}
				if(strProType!=null && !strProType.equals("")) {
					hmFilter.put("PROJECT_TYPE", strProType);
				} else {
					hmFilter.put("PROJECT_TYPE", "All Projects");
				}
			} else {
				hmFilter.put("PROJECT_TYPE", "All Projects");
			}
		}
		
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
				hmFilter.put("SERVICE", "All SBUs");
			}
		} else {
			hmFilter.put("SERVICE", "All SBUs");
		}
		
		alFilter.add("PROJECT_SERVICE");
		if(getF_project_service()!=null) {
			String strProjectService="";
			int k=0;
			for(int i=0;projectServiceList!=null && i<projectServiceList.size();i++) {
				for(int j=0;j<getF_project_service().length;j++) {
					if(getF_project_service()[j].equals(projectServiceList.get(i).getServiceId())) {
						if(k==0) {
							strProjectService=projectServiceList.get(i).getServiceName();
						} else {
							strProjectService+=", "+projectServiceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strProjectService!=null && !strProjectService.equals("")) {
				hmFilter.put("PROJECT_SERVICE", strProjectService);
			} else {
				hmFilter.put("PROJECT_SERVICE", "All Services");
			}
		} else {
			hmFilter.put("PROJECT_SERVICE", "All Services");
		}
		
		alFilter.add("CLIENT");
		if(getF_client()!=null) {
			String strClient="";
			int k=0;
			for(int i=0; clientList!=null && i<clientList.size();i++) {
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
		
		alFilter.add("PROJECT");
		if(getF_project()!=null) {
			
				String strProject="";
				
				int k=0;
				for(int i=0; projectList!=null && i<projectList.size();i++) {
					for(int j=0;j<getF_project().length;j++) {
						if(getF_project()[j].equals(projectList.get(i).getId())) {
							if(k==0) {
								strProject=projectList.get(i).getName();
							} else {
								strProject+=", "+projectList.get(i).getName();
							}
							k++;
						}
					}
				}
			if(strProject!=null && !strProject.equals("")) {
				hmFilter.put("PROJECT", strProject);
			} else {
				hmFilter.put("PROJECT", "All Projects");
			}
		} else {
			hmFilter.put("PROJECT", "All Project");
		}
		
		
		alFilter.add("FROM_TO");
		String strFdt = "-";
		String strEdt = "-";
		if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null")) {
			strFdt = uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat());
		}
		if(getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
			strEdt = uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat());
		}
		hmFilter.put("FROM_TO",  strFdt+" - "+ strEdt);
		String selectedFilter=CF.getSelectedFilter1(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
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
	
	public String[] getF_project() {
		return f_project;
	}
	
	public void setF_project(String[] f_project) {
		this.f_project = f_project;
	}
	
	public String[] getF_client() {
		return f_client;
	}
	
	public void setF_client(String[] f_client) {
		this.f_client = f_client;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}
	
	public List<FillClients> getClientList() {
		return clientList;
	}
	
	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}
	
	public List<FillProject> getProjectList() {
		return projectList;
	}
	
	public void setProjectList(List<FillProject> projectList) {
		this.projectList = projectList;
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

	public String getStrProType() {
		return strProType;
	}

	public void setStrProType(String strProType) {
		this.strProType = strProType;
	}

	public boolean isPoFlag() {
		return poFlag;
	}

	public void setPoFlag(boolean poFlag) {
		this.poFlag = poFlag;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public String[] getF_project_service() {
		return f_project_service;
	}

	public void setF_project_service(String[] f_project_service) {
		this.f_project_service = f_project_service;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public List<FillServices> getProjectServiceList() {
		return projectServiceList;
	}

	public void setProjectServiceList(List<FillServices> projectServiceList) {
		this.projectServiceList = projectServiceList;
	}
	
}
