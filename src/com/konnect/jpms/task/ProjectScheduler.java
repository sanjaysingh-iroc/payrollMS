package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class ProjectScheduler implements IStatements,ServletRequestAware {
	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;
	UtilityFunctions uF;
	String strEmpId;
	String strEmpOrgId;
	String strSessionUserType;
	
	public ProjectScheduler(HttpServletRequest request, HttpSession session, CommonFunctions CF, UtilityFunctions uF, String strEmpId) {
		super();
		this.request = request;
		this.session = session;
		this.CF = CF;
		this.uF = uF;
		this.strEmpId = strEmpId;
	}
	public void checkAndCreateNewProject() {
		Connection con = null;
		PreparedStatement  pst1 = null;
		ResultSet  rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			String strUesrTypeId = (String)session.getAttribute(BASEUSERTYPEID);
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select * from projectmntnc where deadline > ? and billing_kind !='O' order by pro_id");
			pst1 = con.prepareStatement(strQuery.toString());
			pst1.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs1 = pst1.executeQuery();
			Map<String, List<String>> hmProjectwiseData = new LinkedHashMap<String, List<String>>();
			while (rs1.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.getDateFormat(rs1.getString("start_date"), DBDATE, DATE_FORMAT));
				innerList.add(uF.getDateFormat(rs1.getString("deadline"), DBDATE, DATE_FORMAT));
				innerList.add(rs1.getString("billing_kind"));
				innerList.add(rs1.getString("added_by"));
				innerList.add(rs1.getString("billing_cycle_day")); //4
				innerList.add(rs1.getString("billing_cycle_weekday"));
				hmProjectwiseData.put(rs1.getString("pro_id"), innerList);
				updateProjectInfo(hmProjectwiseData,uF.getDateFormat(rs1.getString("deadline"), DBDATE, DATE_FORMAT));
			}
			rs1.close();
			pst1.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}
	}
   public void updateProjectDetails(String pro_Id){
		Connection con = null;
		PreparedStatement pst1 = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			String strUesrTypeId = (String)session.getAttribute(BASEUSERTYPEID);
//			System.out.println("\nProId:\t"+pro_Id);
			String deadlineDate = ""; 
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select * from projectmntnc where pro_id=?");
			pst1 = con.prepareStatement(strQuery.toString());
			pst1.setInt(1, uF.parseToInt(pro_Id));
//			System.out.println("\npst1 in proId====>"+pst1);
			rs1 = pst1.executeQuery();
			Map<String, List<String>> hmProjectwiseData = new LinkedHashMap<String, List<String>>();
			while (rs1.next()) {
				List<String> innerList = new ArrayList<String>();
				deadlineDate = uF.getDateFormat(rs1.getString("deadline"), DBDATE, DATE_FORMAT);
				innerList.add(uF.getDateFormat(rs1.getString("start_date"), DBDATE, DATE_FORMAT));
				innerList.add(uF.getDateFormat(rs1.getString("deadline"), DBDATE, DATE_FORMAT));
				innerList.add(rs1.getString("billing_kind"));
				innerList.add(rs1.getString("added_by"));
				innerList.add(rs1.getString("billing_cycle_day")); //4
				innerList.add(rs1.getString("billing_cycle_weekday"));
				hmProjectwiseData.put(rs1.getString("pro_id"), innerList);
			}
			rs1.close();
			pst1.close();
			
//			System.out.println("deadlineDate==>"+deadlineDate);
			
			Set<String> hmProjectFreqData = getProFreqIds(pro_Id);
			int freqCount = hmProjectFreqData.size();
//			System.out.println("\nfreqCount:\t"+freqCount);
			if(freqCount>0){
				Iterator<String> its =  hmProjectFreqData.iterator();
				while(its.hasNext()){
					String proFreqId = its.next();
					deleteProFreq(pro_Id,proFreqId);
				}
			}
			updateProjectInfo(hmProjectwiseData,deadlineDate);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}
   }
   public void updateProjectInfo(Map<String,List<String>> hmProjectwiseData,String deadlineDate){
//		System.out.println("in current date updates");
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			String strUesrTypeId = (String)session.getAttribute(USERTYPE);
//			System.out.println("\nProId:\t"+pro_Id);
			Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
			Date deadline = uF.getDateFormatUtil(deadlineDate, DATE_FORMAT);
		   
//			System.out.println("curr date==>"+currDate);
			String strDomain = request.getServerName().split("\\.")[0];
			Iterator<String> it = hmProjectwiseData.keySet().iterator();
			while(it.hasNext()) {
				String proId = it.next();
				List<String> innerList = hmProjectwiseData.get(proId);
				String newStartDate = getNewProjectStartDate(con, uF, proId, innerList.get(0));
				int intMonths =0;
				if(currDate.after(deadline)){
					intMonths = uF.getMonthsDifference(uF.getDateFormat(newStartDate, DATE_FORMAT),deadline );
				}else{
					intMonths = uF.getMonthsDifference(uF.getDateFormat(newStartDate, DATE_FORMAT), uF.getCurrentDate(CF.getStrTimeZone()));
				}
				int intCount = 1;
			
				if(innerList.get(2) != null && innerList.get(2).equals("M")) {
					intCount = intMonths;
				} else if(innerList.get(2) != null && innerList.get(2).equals("B")) {
					intCount = intMonths * 2;
				} else if(innerList.get(2) != null && innerList.get(2).equals("W")) {
					String strDays ="";
					if(currDate.after(deadline)){
						strDays = uF.dateDifference(newStartDate, DATE_FORMAT,deadlineDate,DATE_FORMAT);
					}else{
						
						strDays = uF.dateDifference(newStartDate, DATE_FORMAT, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
					}
					intCount = (uF.parseToInt(strDays) / 7);
					intCount++;
				}else if(innerList.get(2) != null && innerList.get(2).equals("Q")) {
					intCount = intMonths / 3;
				}else if(innerList.get(2) != null && innerList.get(2).equals("H")) {
					intCount = intMonths / 6;
				}else if(innerList.get(2) != null && innerList.get(2).equals("A")) {
					intCount = intMonths / 12;
				} else if(innerList.get(2) != null && innerList.get(2).equals("O")) {
					intCount = 0;
				} 
//				System.out.println("intcount==>"+intCount);
				for(int j=0; j<=intCount; j++) {
					String newStDate = getNewProjectStartDate(con, uF, proId, innerList.get(0));
					if(newStDate == null || newStDate.equals("")) {
						newStDate = innerList.get(0);
					}
				
					Date stDate = uF.getDateFormatUtil(newStDate, DATE_FORMAT);
					Date endDate = uF.getDateFormatUtil(innerList.get(1), DATE_FORMAT);
					boolean frqFlag = false;
					String freqEndDate = innerList.get(1);
				
					if(uF.parseToInt(innerList.get(4)) > 0) {
	//					System.out.println("newStDate:\t"+newStDate);
						freqEndDate = innerList.get(4) + "/" + uF.getDateFormat(newStDate, DATE_FORMAT, "MM")+ "/" +uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy");
	//					System.out.println("freq1:\t"+freqEndDate);		
						
						freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
	//					System.out.println("freq2:\t"+freqEndDate);	
						
						Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
	//					System.out.println("freqDate ===>> " + freqDate + "\t stDate ===>> " + stDate+"\tendDate==>"+endDate);
						
						if(freqDate.after(stDate)) {
							frqFlag = true;
						}
	//					System.out.println("freqFlag:\t"+frqFlag);
						
						if(frqFlag) {
							if(innerList.get(2) != null && innerList.get(2).equals("M")) {
								freqEndDate = freqEndDate;
							} else if(innerList.get(2) != null && innerList.get(2).equals("B")) {
								freqEndDate = freqEndDate;
								String freqEndDate1 = freqEndDate;
	
								int startDateMonth = uF.parseToInt(uF.getDateFormat(newStDate, DATE_FORMAT, "MM"));
								int freqDateMonth = uF.parseToInt(uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM"));
								if(startDateMonth==freqDateMonth){
	
									long diff = uF.getDateDiffinDays(stDate,freqDate);
									 if(diff>14){
										freqEndDate = uF.getDateFormat(uF.getBiweeklyDate(newStDate, 14)+"", DBDATE, DATE_FORMAT);
									}
								}
							}
						} else {
							if(innerList.get(2) != null && innerList.get(2).equals("M")) {
								if(uF.parseToInt(innerList.get(4))==1){
	
									int month = uF.parseToInt(uF.getDateFormat(newStDate, DATE_FORMAT, "MM"));
									int year = uF.parseToInt(uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy"));
									int days = uF.getNoOfDaysMonth(year, month-1);
									if(days == 31){
										freqEndDate = uF.getDateFormat(uF.getBiweeklyDate(newStDate, 30)+"", DBDATE, DATE_FORMAT);
									}else if(days == 30){
										freqEndDate = uF.getDateFormat(uF.getBiweeklyDate(newStDate, 29)+"", DBDATE, DATE_FORMAT);
									}else if(days == 29){
										freqEndDate = uF.getDateFormat(uF.getBiweeklyDate(newStDate, 28)+"", DBDATE, DATE_FORMAT);
									}else if(days == 28){
										freqEndDate = uF.getDateFormat(uF.getBiweeklyDate(newStDate, 27)+"", DBDATE, DATE_FORMAT);
									}
										
								}else{
									freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(freqEndDate, 1)+"", DBDATE, DATE_FORMAT);
								}
								String freqEndDate1 = innerList.get(4) + "/" + uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM")+ "/" +uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy");
								freqEndDate1 = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate1, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
								
								Date newfreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
								Date freqDate1 = uF.getDateFormatUtil(freqEndDate1, DATE_FORMAT);
								
								if(freqDate1.after(stDate) && (freqDate1.before(newfreqDate) || freqDate1.after(newfreqDate))) {
									freqEndDate = freqEndDate1;
								}
							} else if(innerList.get(2) != null && innerList.get(2).equals("B")) {
								
								if(freqEndDate.equals(newStDate)){
//									System.out.println("in equal");
									freqEndDate = newStDate;
								}else{
									freqEndDate = uF.getDateFormat(uF.getBiweeklyDate(newStDate, 14)+"", DBDATE, DATE_FORMAT);
									int startDateYear = uF.parseToInt(uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy"));
									int startDateMonth = uF.parseToInt(uF.getDateFormat(newStDate, DATE_FORMAT, "MM"));
									int freqDateMonth = uF.parseToInt(uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM"));
								
									if(startDateMonth!=freqDateMonth){
										int noOfDays = uF.getNoOfDaysMonth(startDateYear, startDateMonth-1);
										if(noOfDays == 31){
											freqEndDate = uF.getDateFormat(uF.getBiweeklyDate(newStDate, 15)+"", DBDATE, DATE_FORMAT);
										}else if(noOfDays == 29){
											freqEndDate = uF.getDateFormat(uF.getBiweeklyDate(newStDate, 13)+"", DBDATE, DATE_FORMAT);
										}else if(noOfDays == 28){
											freqEndDate = uF.getDateFormat(uF.getBiweeklyDate(newStDate, 12)+"", DBDATE, DATE_FORMAT);
										}
									}
								}
								String freqEndDate1 = innerList.get(4) + "/" + uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM")+ "/" +uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy");
								String freqEndDate2 = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate1, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
								
							    Date newfreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
								Date freqDate1 = uF.getDateFormatUtil(freqEndDate2, DATE_FORMAT);
								
							    if((freqDate1.after(stDate))  && (freqDate1.before(newfreqDate) )){
									
							    	freqEndDate = freqEndDate2;
//							    	System.out.println("proflag false freq date==>"+freqEndDate);
								}
							 }
						}
						Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
						if(newFreqDate.after(endDate)) {
							freqEndDate = innerList.get(1);
						}
					}
					
					if(innerList.get(5) != null && !innerList.get(5).equals("") && innerList.get(2) != null && innerList.get(2).equals("W")) {
						
						freqEndDate = uF.getDateFormat(uF.getDateOfPassedDay(newStDate, innerList.get(5))+"", DBDATE, DATE_FORMAT);
						freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
						Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
						if(newFreqDate.after(endDate)) {
							freqEndDate = innerList.get(1);
						}
					}
					if(innerList.get(2) != null && innerList.get(2).equals("Q")){
						freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(newStDate, 3)+"", DBDATE, DATE_FORMAT);
						freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
						Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
						if(newFreqDate.after(endDate)) {
							freqEndDate = innerList.get(1);
						}
					}
					
					if(innerList.get(2) != null && innerList.get(2).equals("H")){
						freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(newStDate, 6)+"", DBDATE, DATE_FORMAT);
						freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
						Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
						if(newFreqDate.after(endDate)) {
							freqEndDate = innerList.get(1);
						}
					}
					if(innerList.get(2) != null && innerList.get(2).equals("A")){
						freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(newStDate, 12)+"", DBDATE, DATE_FORMAT);
						freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
						Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
						if(newFreqDate.after(endDate)) {
							freqEndDate = innerList.get(1);
						}
					}	
					
					/*if(innerList.get(2) != null && innerList.get(2).equals("O")){
						freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(endDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
					}	
					*/
					Date newStDt = uF.getDateFormatUtil(newStDate, DATE_FORMAT);
					Date currDt = uF.getCurrentDate(CF.getStrTimeZone());
					String proFreqName = "";
					if(innerList.get(2) != null && innerList.get(2).equals("M")) {
						String freqMonth = uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM");
						String freqYear = uF.getDateFormat(freqEndDate, DATE_FORMAT, "yyyy");
						String strMonth = uF.getMonth(uF.parseToInt(freqMonth));
						proFreqName = freqYear +" "+strMonth;
					} else if(innerList.get(2) != null && innerList.get(2).equals("B")) {
						String freqMonth = uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM");
						String freqYear = uF.getDateFormat(freqEndDate, DATE_FORMAT, "yyyy");
						String strMonth = uF.getMonth(uF.parseToInt(freqMonth));
						String freqDate = uF.getDateFormat(freqEndDate, DATE_FORMAT, "dd");
						String strHalf = "- First";
						if(uF.parseToInt(freqDate) > 15) {
							strHalf = "- Second";
						}
						proFreqName = freqYear +" "+strMonth+" " + strHalf;
					} else if(innerList.get(2) != null && innerList.get(2).equals("W")) {
						String freqMonth = uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM");
						String freqYear = uF.getDateFormat(freqEndDate, DATE_FORMAT, "yyyy");
						String strMonth = uF.getMonth(uF.parseToInt(freqMonth));
						String strWeekName = uF.getWeekOfMonthOnPassedDate(freqEndDate);
						proFreqName = freqYear +" "+strMonth+" Week-" +strWeekName ;
					}else if(innerList.get(2) != null && innerList.get(2).equals("Q")) {
						String freqMonth = uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM");
//						System.out.println("freqMonth q==>"+freqMonth);							
						if(freqMonth.equals("01") || freqMonth.equals("02") || freqMonth.equals("03") ){
							proFreqName = "First Quarter";
						}else if(freqMonth.equals("04") || freqMonth.equals("05") || freqMonth.equals("06")){
							proFreqName = "Second Quarter";
						}else if(freqMonth.equals("07") || freqMonth.equals("08") || freqMonth.equals("09")){
							proFreqName = "Third Quarter";
						}else if(freqMonth.equals("10") || freqMonth.equals("11") || freqMonth.equals("12")){
							proFreqName = "Fourth Quarter";
						}
					}else if(innerList.get(2) != null && innerList.get(2).equals("H")) {
						String freqMonth = uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM");
//						System.out.println("freqMonth H==>"+freqMonth);								
						if(freqMonth.equals("01") || freqMonth.equals("02") || freqMonth.equals("03") || freqMonth.equals("04") || freqMonth.equals("05") || freqMonth.equals("06") ){
							proFreqName = "First Half";
						}else if(freqMonth.equals("07") || freqMonth.equals("08") || freqMonth.equals("09") || freqMonth.equals("10") || freqMonth.equals("11") || freqMonth.equals("12")){
							proFreqName = "Second Half";
						}
					}else if(innerList.get(2) != null && innerList.get(2).equals("A")) {
						String freqYear = uF.getDateFormat(freqEndDate, DATE_FORMAT, "yyyy");
							proFreqName = "Annum"+freqYear;
					}
				/*	else if(innerList.get(2) != null && innerList.get(2).equals("O")) {
						String freqYear = uF.getDateFormat(freqEndDate, DATE_FORMAT, "yyyy");
							proFreqName = "One Time";
					}*/
//					System.out.println("profreqName==>"+proFreqName);
					
					if((endDate.after(stDate) || endDate.equals(stDate)) && (currDt.after(newStDt) || currDt.equals(newStDt))) {
						pst = con.prepareStatement("insert into projectmntnc_frequency (pro_id, pro_start_date, pro_end_date, freq_start_date, freq_end_date, added_by, entry_date, pro_freq_name) " +
						"values (?,?,?,?, ?,?,?,?)"); 
						pst.setInt(1, uF.parseToInt(proId));
						pst.setDate(2, uF.getDateFormat(innerList.get(0), DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(innerList.get(1), DATE_FORMAT));
						pst.setDate(4, uF.getDateFormat(newStDate, DATE_FORMAT));
						if(frqFlag || (innerList.get(2) != null )) {
							pst.setDate(5, uF.getDateFormat(freqEndDate, DATE_FORMAT));
						}else {
							pst.setDate(5, uF.getDateFormat(freqEndDate, DATE_FORMAT));
						}
						pst.setInt(6, uF.parseToInt((String) innerList.get(3)));
						pst.setDate(7, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
						pst.setString(8, proFreqName);
//						System.out.println("pst ====> " + pst);
						pst.executeUpdate();
						pst.close();
//						System.out.println("\nnewStDate:\t"+newStDate+"\tfreqEndDate:\t"+freqEndDate);
					}
				}
			}
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}
    public void deleteProFreq(String proId,String proFreqId){
    	Connection con = null;
		PreparedStatement pst = null;
		
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from projectmntnc_frequency where pro_id=? and pro_freq_id=? ");
			pst.setInt(1, uF.parseToInt(proId));
			pst.setInt(2,uF.parseToInt(proFreqId) );
		    pst.executeQuery();
		    pst.close();
		} catch (SQLException e) {
		
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
   	private void addRecurringTasks(Connection con, UtilityFunctions uF, String proId, String newStDate, String freqEndDate) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select * from activity_info where pro_id = ? and parent_task_id=0 and recurring_task=1");
			pst = con.prepareStatement(strQuery.toString());
			pst.setInt(1, uF.parseToInt(proId));
//			System.out.println("pst ====> " + pst);
			rs = pst.executeQuery();
			List<List<String>> proTasksData = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				CF.getStrReportDateFormat();
				innerList.add(rs.getString("activity_name")); //+" "+uF.getDateFormat(newStDate, DATE_FORMAT, DATE_FORMAT_STR)+" to "+uF.getDateFormat(freqEndDate, DATE_FORMAT, DATE_FORMAT_STR)
				innerList.add(rs.getString("priority")); //1
				innerList.add(rs.getString("resource_ids")); //2
				innerList.add(rs.getString("deadline")); //freqEndDate
				innerList.add(rs.getString("idealtime")); //4
				innerList.add(rs.getString("start_date")); //newStDate
				innerList.add(rs.getString("dependency_task")); //6
				innerList.add(rs.getString("dependency_type")); //7
				innerList.add(rs.getString("color_code")); //8
				innerList.add(rs.getString("taskstatus")); //9
				innerList.add(rs.getString("pro_id")); //10
				innerList.add(rs.getString("parent_task_id")); //11
				innerList.add(rs.getString("task_skill_id")); //12
				innerList.add(rs.getString("task_description")); //13
				innerList.add(rs.getString("added_by")); //14
				innerList.add(rs.getString("task_id")); //15
				proTasksData.add(innerList);
			}
			rs.close();
			pst.close();
			
			StringBuilder strQue = new StringBuilder();
			strQue.append("select * from activity_info where pro_id = ? and parent_task_id !=0 and recurring_task=1");
			pst = con.prepareStatement(strQue.toString());
			pst.setInt(1, uF.parseToInt(proId));
//			System.out.println("pst ====> " + pst);
			rs = pst.executeQuery();
			Map<String, List<List<String>>> hmProSubTaskList = new HashMap<String, List<List<String>>>();
			List<List<String>> proSubTasksData = new ArrayList<List<String>>();
			while (rs.next()) {
				proSubTasksData = hmProSubTaskList.get(rs.getString("parent_task_id"));
				if(proSubTasksData == null) proSubTasksData = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("activity_name")); //+" "+uF.getDateFormat(newStDate, DATE_FORMAT, DATE_FORMAT_STR)+" to "+uF.getDateFormat(freqEndDate, DATE_FORMAT, DATE_FORMAT_STR)
				innerList.add(rs.getString("priority")); //1
				innerList.add(rs.getString("resource_ids")); //2
				innerList.add(rs.getString("deadline")); //freqEndDate
				innerList.add(rs.getString("idealtime")); //4
				innerList.add(rs.getString("start_date")); //newStDate
				innerList.add(rs.getString("dependency_task")); //6
				innerList.add(rs.getString("dependency_type")); //7
				innerList.add(rs.getString("color_code")); //8
				innerList.add(rs.getString("taskstatus")); //9
				innerList.add(rs.getString("pro_id")); //10
				innerList.add(rs.getString("parent_task_id")); //11
				innerList.add(rs.getString("task_skill_id")); //12
				innerList.add(rs.getString("task_description")); //13
				innerList.add(rs.getString("added_by")); //14
				innerList.add(rs.getString("task_id")); //15
				proSubTasksData.add(innerList);
				hmProSubTaskList.put(rs.getString("parent_task_id"), proSubTasksData);
			}
			rs.close();
			pst.close();
			
			for(int i=0; proTasksData != null && !proTasksData.isEmpty() && i<proTasksData.size(); i++) {
				List<String> innerList = proTasksData.get(i);
				pst = con.prepareStatement("insert into activity_info (activity_name,priority,resource_ids,deadline,idealtime,start_date," +
					"dependency_task,dependency_type,color_code,taskstatus,pro_id,parent_task_id,task_skill_id,task_description,added_by,task_freq_name) " +
					"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
				pst.setString(1, innerList.get(0));
				pst.setString(2, innerList.get(1));
				pst.setString(3, innerList.get(2));						
				pst.setDate(4, uF.getDateFormat(freqEndDate, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(innerList.get(4)));
				pst.setDate(6, uF.getDateFormat(newStDate, DATE_FORMAT));
				pst.setInt(7, uF.parseToInt(innerList.get(6)));
				pst.setString(8, innerList.get(7));
				pst.setString(9, innerList.get(8));
				pst.setString(10, "New Task");
				pst.setInt(11, uF.parseToInt(innerList.get(10)));
				pst.setInt(12, uF.parseToInt(innerList.get(11)));
				pst.setInt(13, uF.parseToInt(innerList.get(12)));
				pst.setString(14, innerList.get(13));
				pst.setInt(15, uF.parseToInt(innerList.get(14)));
				pst.setString(16, uF.getDateFormat(newStDate, DATE_FORMAT, DATE_FORMAT_STR)+" to "+uF.getDateFormat(freqEndDate, DATE_FORMAT, DATE_FORMAT_STR));
				pst.executeUpdate();
				pst.close();
				
				String strTaskId = null;
				pst = con.prepareStatement("select max(task_id) as task_id from activity_info ");
				rs = pst.executeQuery();
				while (rs.next()) {
					strTaskId = rs.getString("task_id");
				}
				rs.close();
				pst.close();
				
				List<List<String>> proSubTaskList = hmProSubTaskList.get(innerList.get(15));
				for (int j = 0; proSubTaskList!=null && !proSubTaskList.isEmpty() && j<proSubTaskList.size(); j++) {
					List<String> innerList1 = proSubTaskList.get(j);
					pst = con.prepareStatement("insert into activity_info (activity_name,priority,resource_ids,deadline,idealtime,start_date," +
						"dependency_task,dependency_type,color_code,taskstatus,pro_id,parent_task_id,task_skill_id,task_description,added_by,task_freq_name) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
					pst.setString(1, innerList1.get(0));
					pst.setString(2, innerList1.get(1));
					pst.setString(3, innerList1.get(2));						
					pst.setDate(4, uF.getDateFormat(freqEndDate, DATE_FORMAT));
					pst.setInt(5, uF.parseToInt(innerList1.get(4)));
					pst.setDate(6, uF.getDateFormat(newStDate, DATE_FORMAT));
					pst.setInt(7, uF.parseToInt(innerList1.get(6)));
					pst.setString(8, innerList1.get(7));
					pst.setString(9, innerList1.get(8));
					pst.setString(10, "New Sub Task");
					pst.setInt(11, uF.parseToInt(innerList1.get(10)));
					pst.setInt(12, uF.parseToInt(strTaskId));
					pst.setInt(13, uF.parseToInt(innerList1.get(12)));
					pst.setString(14, innerList1.get(13));
					pst.setInt(15, uF.parseToInt(innerList1.get(14)));
					pst.setString(16, uF.getDateFormat(newStDate, DATE_FORMAT, DATE_FORMAT_STR)+" to "+uF.getDateFormat(freqEndDate, DATE_FORMAT, DATE_FORMAT_STR));
					pst.executeUpdate();
					pst.close();
				}
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
	public String getNewProjectStartDate(Connection con, UtilityFunctions uF, String proId, String proStartDate) {
		PreparedStatement pst1 = null;
		ResultSet rs1 = null;
		String newStDate = null;
		try {
			String freqEndDate = null;
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select freq_end_date from projectmntnc_frequency where pro_id = ? ");
			pst1 = con.prepareStatement(strQuery.toString());
			pst1.setInt(1, uF.parseToInt(proId));
//			System.out.println("pst1 ====> " + pst1);
			rs1 = pst1.executeQuery();
			while (rs1.next()) {
				freqEndDate = rs1.getString("freq_end_date");
			}
			rs1.close();
			pst1.close();
			
			if(freqEndDate != null) {
				newStDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DBDATE), 1)+"", DBDATE, DATE_FORMAT);
			} else {
				newStDate = proStartDate;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs1 != null){
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if(pst1 != null){
				try {
					pst1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return newStDate;
	}
	 public Set<String> getProFreqIds(String proId){
	    	Connection con = null;
			PreparedStatement pst1 = null;
			ResultSet  rs1 = null;
			Database db = new Database();
			db.setRequest(request);
			Set<String> hmProjectFreqData = new LinkedHashSet<String>();
			try {
				con = db.makeConnection(con);
				
				StringBuilder strQuery = new StringBuilder();
				strQuery.append("select * from projectmntnc_frequency where pro_id=?");

				pst1 = con.prepareStatement(strQuery.toString());
				pst1.setInt(1, uF.parseToInt(proId));
				
				rs1 = pst1.executeQuery();
				while (rs1.next()) {
					hmProjectFreqData.add(rs1.getString("pro_freq_id"));
					
				}
				pst1.close();
				rs1.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				db.closeResultSet(rs1);
				db.closeStatements(pst1);
				db.closeConnection(con);
			}
			return hmProjectFreqData;
	    }
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}
