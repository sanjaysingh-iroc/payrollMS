package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class AppraisalScheduler  implements IStatements,ServletRequestAware {
	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;
	UtilityFunctions uF;
	String strEmpId;
	String strEmpOrgId;
	String strSessionUserType;
	
	public AppraisalScheduler(HttpServletRequest request, HttpSession session, CommonFunctions CF, UtilityFunctions uF, String strEmpId) {
		super();
		this.request = request;
		this.session = session;
		this.CF = CF;
		this.uF = uF;
		this.strEmpId = strEmpId;
	}
	
	
	public void checkAndCreateNewAppraisal() {
		Connection con = null;
		PreparedStatement  pst1 = null;
		ResultSet  rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			String strUesrTypeId = (String)session.getAttribute(BASEUSERTYPEID);
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select * from appraisal_details where to_date >= ? and frequency > 1 order by appraisal_details_id ");
			pst1 = con.prepareStatement(strQuery.toString());
			pst1.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs1 = pst1.executeQuery();
			Map<String, List<String>> hmAppraisalWiseData = new LinkedHashMap<String, List<String>>();
			while (rs1.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.getDateFormat(rs1.getString("from_date"), DBTIMESTAMP, DATE_FORMAT));//0
				innerList.add(uF.getDateFormat(rs1.getString("to_date"), DBDATE, DATE_FORMAT));//1
				innerList.add(rs1.getString("frequency"));//2
				innerList.add(rs1.getString("usertype_member"));//3
				innerList.add(rs1.getString("appraisal_day")); //4
				innerList.add(rs1.getString("weekday"));//5
				hmAppraisalWiseData.put(rs1.getString("appraisal_details_id"), innerList);//6
				updateAppraisalInfo(hmAppraisalWiseData, uF.getDateFormat(rs1.getString("to_date"), DBDATE, DATE_FORMAT));
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
	
	
   public void updateAppraisalDetails(String appraisalId) {
		Connection con = null;
		PreparedStatement pst1 = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
//			String strUesrTypeId = (String)session.getAttribute(BASEUSERTYPEID);
			
			String deadlineDate = ""; 
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select * from appraisal_details where appraisal_details_id = ?");
			pst1 = con.prepareStatement(strQuery.toString());
			pst1.setInt(1, uF.parseToInt(appraisalId));
//			System.out.println("pst==>"+pst1);
			rs1 = pst1.executeQuery();
			Map<String, List<String>> hmAppraisalWiseData = new LinkedHashMap<String, List<String>>();
			while (rs1.next()) {
				List<String> innerList = new ArrayList<String>();
				deadlineDate = uF.getDateFormat(rs1.getString("to_date"), DBDATE, DATE_FORMAT);
				innerList.add(uF.getDateFormat(rs1.getString("from_date"), DBDATE, DATE_FORMAT));//0
				innerList.add(uF.getDateFormat(rs1.getString("to_date"), DBDATE, DATE_FORMAT));//1
				innerList.add(rs1.getString("frequency"));//2
				innerList.add(rs1.getString("usertype_member"));//3
				innerList.add(rs1.getString("appraisal_day")); //4
				innerList.add(rs1.getString("weekday"));//5
				hmAppraisalWiseData.put(rs1.getString("appraisal_details_id"), innerList);
			}
			rs1.close();
			pst1.close();
//			System.out.println("hmAppraisalWiseData ===>> " + hmAppraisalWiseData);
			
//			int freqCount = getGoalFreqIds(goalId);
////			System.out.println("\nfreqCount:\t"+freqCount);
//			if(freqCount>0) {
				deleteAppraisalFreq(appraisalId);
//			}
				updateAppraisalInfo(hmAppraisalWiseData, deadlineDate);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}
   }
   
   
   public void updateAppraisalInfo(Map<String,List<String>> hmAppraisalWiseData, String dueDate) {
//		System.out.println("in current date updates");
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
//			String strUesrTypeId = (String)session.getAttribute(USERTYPE);
			Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
			Date duedate = uF.getDateFormatUtil(dueDate, DATE_FORMAT);
			
//			System.out.println("curr date==>"+currDate);
//			String strDomain = request.getServerName().split("\\.")[0];
			Iterator<String> it = hmAppraisalWiseData.keySet().iterator();
			while(it.hasNext()) {
				String appraisalId = it.next();
				List<String> innerList = hmAppraisalWiseData.get(appraisalId);
				String newStartDate = getNewAppraisalStartDate(con, uF, appraisalId, innerList.get(0));
//				System.out.println("frequency ==>"+ innerList.get(2));
				int intMonths =0;
				if(currDate.after(duedate)) {
					intMonths = uF.getMonthsDifference(uF.getDateFormat(newStartDate, DATE_FORMAT), duedate);
				} else {
					intMonths = uF.getMonthsDifference(uF.getDateFormat(newStartDate, DATE_FORMAT), uF.getCurrentDate(CF.getStrTimeZone()));
				}
				int intCount = 1;
			
				if(uF.parseToInt(innerList.get(2)) == 3) { //  Monthly
					intCount = intMonths;
				} else if(uF.parseToInt(innerList.get(2)) == 2) { //Weekly
					String strDays ="";
					if(currDate.after(duedate)){
						strDays = uF.dateDifference(newStartDate, DATE_FORMAT,dueDate,DATE_FORMAT);
					}else {
						
						strDays = uF.dateDifference(newStartDate, DATE_FORMAT, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
					}
					intCount = (uF.parseToInt(strDays) / 7);
					intCount++;
				}else if(uF.parseToInt(innerList.get(2)) == 4) { // Quarterly
					intCount = intMonths / 3;
				}else if(uF.parseToInt(innerList.get(2)) == 5) { // Half Yearly
					intCount = intMonths / 6;
				}else if(uF.parseToInt(innerList.get(2)) == 6) { // Yearly
					intCount = intMonths / 12;
				} else if(uF.parseToInt(innerList.get(2)) == 1) { //One Time
					intCount = 0;
				} 
				
//				System.out.println("intcount ==> " + intCount);
				for(int j=0; j<=intCount; j++) {
					
					String newStDate = getNewAppraisalStartDate(con, uF, appraisalId, innerList.get(0));
//					System.out.println("newStartDate1:\t"+newStDate);
					if(newStDate == null || newStDate.equals("")) {
						newStDate = innerList.get(0);
					}
//					System.out.println("newStartDate2:\t"+newStDate);
					Date stDate = uF.getDateFormatUtil(newStDate, DATE_FORMAT);
					Date endDate = uF.getDateFormatUtil(innerList.get(1), DATE_FORMAT);
					boolean frqFlag = false;
					String freqEndDate = innerList.get(1);
				
					if(innerList.get(4) != null && uF.parseToInt(innerList.get(4)) > 0) {
						
						freqEndDate = innerList.get(4) + "/" + uF.getDateFormat(newStDate, DATE_FORMAT, "MM")+ "/" +uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy");
//						System.out.println("freq1:\t"+freqEndDate);		
						
						freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
//						System.out.println("freq2:\t"+freqEndDate);	
						
						Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
//						System.out.println("freqDate ===>> " + freqDate + "\t stDate ===>> " + stDate+"\tendDate==>"+endDate);
						
						if(freqDate.after(stDate)) {
							frqFlag = true;
						}
						
//						System.out.println("freqFlag ===>> " + frqFlag);
						
						if(frqFlag) {
							if(uF.parseToInt(innerList.get(2)) == 3) { // Monthly
								freqEndDate = freqEndDate;
							} 
							
						} else {
							if(uF.parseToInt(innerList.get(2)) == 3) { // Monthly
								if(uF.parseToInt(innerList.get(4)) == 1) {
	
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
										
								} else {
									freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(freqEndDate, 1)+"", DBDATE, DATE_FORMAT);
								}
								String freqEndDate1 = innerList.get(4) + "/" + uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM")+ "/" +uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy");
								freqEndDate1 = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate1, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
								
								Date newfreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
								Date freqDate1 = uF.getDateFormatUtil(freqEndDate1, DATE_FORMAT);
								
								if(freqDate1.after(stDate) && (freqDate1.before(newfreqDate) || freqDate1.after(newfreqDate))) {
									freqEndDate = freqEndDate1;
								}
							}
						
						}
						Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
						if(newFreqDate.after(endDate)) {
							freqEndDate = innerList.get(1);
						}
					}
					
					
					if(innerList.get(5) != null && !innerList.get(5).equals("") && uF.parseToInt(innerList.get(2)) == 2) { // Weekly
						
						freqEndDate = uF.getDateFormat(uF.getDateOfPassedDay(newStDate, innerList.get(5))+"", DBDATE, DATE_FORMAT);
						freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
						Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
						if(newFreqDate.after(endDate)) {
							freqEndDate = innerList.get(1);
						}
					}
					if(uF.parseToInt(innerList.get(2)) == 4) { // Quarterly
						freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(newStDate, 3)+"", DBDATE, DATE_FORMAT);
						freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
						Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
						if(newFreqDate.after(endDate)) {
							freqEndDate = innerList.get(1);
						}
					}
					
					if(uF.parseToInt(innerList.get(2)) == 5) { //Half Yearly
						freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(newStDate, 6)+"", DBDATE, DATE_FORMAT);
						freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
						Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
						if(newFreqDate.after(endDate)) {
							freqEndDate = innerList.get(1);
						}
					}
					if(uF.parseToInt(innerList.get(2)) == 6) { // Yearly
						freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(newStDate, 12)+"", DBDATE, DATE_FORMAT);
						freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
						Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
						if(newFreqDate.after(endDate)) {
							freqEndDate = innerList.get(1);
						}
					}	
				
					
					if(innerList.get(2) != null && uF.parseToInt(innerList.get(2)) == 1) {
						freqEndDate = innerList.get(1);
						freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
						Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
						if(newFreqDate.after(endDate)) {
							freqEndDate = innerList.get(1);
						}
					}
					
					Date newStDt = uF.getDateFormatUtil(newStDate, DATE_FORMAT);
					Date currDt = uF.getCurrentDate(CF.getStrTimeZone());
					String appraisalFreqName = null;
					if(uF.parseToInt(innerList.get(2)) == 3) { //Monthly
						String freqMonth = uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM");
						String freqYear = uF.getDateFormat(freqEndDate, DATE_FORMAT, "yyyy");
						String strMonth = uF.getMonth(uF.parseToInt(freqMonth));
						appraisalFreqName = freqYear +" "+strMonth;
					} 
					
					else if(uF.parseToInt(innerList.get(2)) == 2) { // Weekly
						String freqMonth = uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM");
						String freqYear = uF.getDateFormat(freqEndDate, DATE_FORMAT, "yyyy");
						String strMonth = uF.getMonth(uF.parseToInt(freqMonth));
						String strWeekName = uF.getWeekOfMonthOnPassedDate(freqEndDate);
						appraisalFreqName = freqYear +" "+strMonth+" Week-" +strWeekName ;
					} else if(uF.parseToInt(innerList.get(2)) == 4) { // Quaterly
						String freqMonth = uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM");
						String freqYear = uF.getDateFormat(freqEndDate, DATE_FORMAT, "yyyy");
//						System.out.println("freqMonth q==>"+freqMonth);							
						if(freqMonth.equals("01") || freqMonth.equals("02") || freqMonth.equals("03")) {
							appraisalFreqName = "First Quarter "+freqYear;
						} else if(freqMonth.equals("04") || freqMonth.equals("05") || freqMonth.equals("06")) {
							appraisalFreqName = "Second Quarter "+freqYear;
						} else if(freqMonth.equals("07") || freqMonth.equals("08") || freqMonth.equals("09")) {
							appraisalFreqName = "Third Quarter "+freqYear;
						} else if(freqMonth.equals("10") || freqMonth.equals("11") || freqMonth.equals("12")) {
							appraisalFreqName = "Fourth Quarter "+freqYear;
						}
					} else if(uF.parseToInt(innerList.get(2)) == 5) { // Half Yearly
						String freqMonth = uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM");
						String freqYear = uF.getDateFormat(freqEndDate, DATE_FORMAT, "yyyy");
//						System.out.println("freqMonth H==>"+freqMonth);								
						if(freqMonth.equals("01") || freqMonth.equals("02") || freqMonth.equals("03") || freqMonth.equals("04") || freqMonth.equals("05") || freqMonth.equals("06") ) {
							appraisalFreqName = "First Half "+freqYear;
						} else if(freqMonth.equals("07") || freqMonth.equals("08") || freqMonth.equals("09") || freqMonth.equals("10") || freqMonth.equals("11") || freqMonth.equals("12")) {
							appraisalFreqName = "Second Half "+freqYear;
						}
					} else if(uF.parseToInt(innerList.get(2)) == 6) { // Yearly
						String freqYear = uF.getDateFormat(freqEndDate, DATE_FORMAT, "yyyy");
						appraisalFreqName = "Annum "+freqYear;
					} else if(uF.parseToInt(innerList.get(2)) == 1) { // one time
						
						appraisalFreqName = "One time";
					}
				
//					System.out.println("newStDate ===>> " + newStDate+"==>freqEndDate==>"+freqEndDate);
					
//					System.out.println("goalFreqName ===>> " + appraisalFreqName);
//					System.out.println("endDate ===>>"+ endDate + "\t stDate ===>> "+stDate +"\t newStDt ===>> " + newStDt + "\t currDt ===>> "  +currDt);
					if((endDate.after(stDate) || endDate.equals(stDate)) && ((currDt.before(newStDt) && j==0) || (currDt.after(newStDt) || currDt.equals(newStDt)))) {
						pst = con.prepareStatement("insert into appraisal_details_frequency (appraisal_id, appraisal_start_date, appraisal_due_date, freq_start_date, freq_end_date, added_by, entry_date, appraisal_freq_name, is_delete) " +
						"values (?,?,?,?, ?,?,?,?,?)"); 
						pst.setInt(1, uF.parseToInt(appraisalId));
						pst.setDate(2, uF.getDateFormat(innerList.get(0), DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(innerList.get(1), DATE_FORMAT));
						pst.setDate(4, uF.getDateFormat(newStDate, DATE_FORMAT));
						if(frqFlag || (innerList.get(2) != null )) {
							pst.setDate(5, uF.getDateFormat(freqEndDate, DATE_FORMAT));
						} else {
							pst.setDate(5, uF.getDateFormat(freqEndDate, DATE_FORMAT));
						}
						pst.setInt(6, uF.parseToInt(strEmpId));
						pst.setDate(7, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
						pst.setString(8, appraisalFreqName);
						pst.setBoolean(9,false);
//						System.out.println("pst frequency set ====> " + pst);
						pst.executeUpdate();
						pst.close();

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
   
   
    public void deleteAppraisalFreq(String appraisalId) {
    	Connection con = null;
		PreparedStatement pst = null;
		
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
//			pst = con.prepareStatement("delete from appraisal_details_frequency where appraisal_freq_id = ?");
			pst = con.prepareStatement("update appraisal_details_frequency set is_delete=? where appraisal_id=?");
			pst.setBoolean(1, true);
			pst.setInt(2, uF.parseToInt(appraisalId));
//			System.out.println("delete freq=="+pst);
			pst.executeQuery();
		    pst.close();
		} catch (SQLException e) {
			
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
    

    public String getNewAppraisalStartDate(Connection con, UtilityFunctions uF, String appraisalId, String appStartDate) {
		PreparedStatement pst1 = null;
		ResultSet rs1 = null;
		String newStDate = null;
		try {
			String freqEndDate = null;
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select freq_end_date from appraisal_details_frequency where appraisal_id=? and is_delete=?");
			pst1 = con.prepareStatement(strQuery.toString());
			pst1.setInt(1, uF.parseToInt(appraisalId));
			pst1.setBoolean(2,false);
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
				newStDate = appStartDate;
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
    
    
	 public int getAppraisalFreqIds(String appraisalId) {
	    	Connection con = null;
			PreparedStatement pst1 = null;
			ResultSet  rs1 = null;
			Database db = new Database();
			db.setRequest(request);
			int count = 0;
			try {
				con = db.makeConnection(con);
				
				StringBuilder strQuery = new StringBuilder();
				strQuery.append("select * from appraisal_details_frequency where appraisal_freq_id =? and is_delete = ?");
				pst1 = con.prepareStatement(strQuery.toString());
				pst1.setInt(1, uF.parseToInt(appraisalId));
				pst1.setBoolean(2,false);
				rs1 = pst1.executeQuery();
				while (rs1.next()) {
					count = 1;
				}
				pst1.close();
				rs1.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				db.closeResultSet(rs1);
				db.closeStatements(pst1);
				db.closeConnection(con);
			}
			return count;
	    }
	 
	 
	 
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}
