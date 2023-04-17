package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LeaveValidation extends ActionSupport implements IConstants, ServletRequestAware {

	CommonFunctions CF;
	String strUserType;
	
	public String execute() throws Exception {

		String strEmpId = (String)request.getParameter("EMPID");
		String strLeaveTypeId = (String)request.getParameter("LTID");
		String strD1 = (String)request.getParameter("D1");
		String strD2 = (String)request.getParameter("D2");
		
		HttpSession session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String)session.getAttribute(USERTYPE);
		
		if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
			strEmpId = (String)session.getAttribute(EMPID);
		}
		
//		System.out.println("LV/44---");
		
		return getLeaveStatus(strEmpId, strLeaveTypeId, strD1, strD2);
		
	}

	
	public String getLeaveStatus(String strEmpId, String strLeaveTypeId, String strD1, String strD2) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			java.util.Date d1=uF.getDateFormatUtil(strD1, DATE_FORMAT);
			java.util.Date d2=uF.getDateFormatUtil(strD2, DATE_FORMAT);
			if(d1.compareTo(d2)>0) {
				request.setAttribute("STATUS_MSG","Sorry From Date can not be graeater than to date");
				return SUCCESS;
			}
//			pst = con.prepareStatement("select * from leave_register1 where emp_id = ? and leave_type_id=? and _date <= ? order by register_id desc limit 1");
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setInt(2, uF.parseToInt(strLeaveTypeId));
//			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
//			double dblBalanceLeaves = 0;
//			while(rs.next()){
//				dblBalanceLeaves = rs.getDouble("balance");
//			}
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);		//added by parvez date: 05-10-2022
			if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
			
			double dblBalanceLeaves = 0;
			 pst = con.prepareStatement("select balance,_date from leave_register1 where emp_id =? and leave_type_id=? " +
             		"and register_id >=(select max(register_id) as register_id from leave_register1 where _type='C' and emp_id =? " +
             		"and leave_type_id=?) and _date = (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
             		"and leave_type_id=? and  _type='C') and _type='C' ");
             pst.setInt(1, uF.parseToInt(strEmpId));
             pst.setInt(2, uF.parseToInt(strLeaveTypeId));
             pst.setInt(3, uF.parseToInt(strEmpId));
             pst.setInt(4, uF.parseToInt(strLeaveTypeId));
             pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
             pst.setInt(6, uF.parseToInt(strEmpId));
             pst.setInt(7, uF.parseToInt(strLeaveTypeId));
//           System.out.println("LV/87--pst 0 ===>> " + pst);
             rs = pst.executeQuery();
             double dblBalance = 0;
             String balanceDate=null;
             while (rs.next()) {
                 dblBalance = uF.parseToDouble(rs.getString("balance"));
                 balanceDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
             }
             rs.close();
             pst.close();
             
             pst = con.prepareStatement("select sum(accrued) as accrued from leave_register1 where emp_id =? and leave_type_id=? " +
             		"and register_id >(select max(register_id) as register_id from leave_register1 where _type='C' and emp_id =? " +
             		"and leave_type_id=?) and _date >= (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
             		"and leave_type_id=? and  _type='C') ");
             pst.setInt(1, uF.parseToInt(strEmpId));
             pst.setInt(2, uF.parseToInt(strLeaveTypeId));
             pst.setInt(3, uF.parseToInt(strEmpId));
             pst.setInt(4, uF.parseToInt(strLeaveTypeId));
       //===start parvez date: 06-01-2022===      
//             pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
             
             int nCurrentYear = uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"));
             int lApplyYear = uF.parseToInt(uF.getDateFormat(strD2+"", DATE_FORMAT, "yyyy"));
             if(lApplyYear == nCurrentYear-1){
            	 pst.setDate(5, uF.getDateFormat(strD2, DATE_FORMAT));
             } else {
            	 pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
             }
//             pst.setDate(5, uF.getDateFormat(strD2, DATE_FORMAT));
      //===end parvez date: 06-01-2022===     
             
             pst.setInt(6, uF.parseToInt(strEmpId));
             pst.setInt(7, uF.parseToInt(strLeaveTypeId));
//             System.out.println("LV/109--pst 1 ===>> " + pst);
             rs = pst.executeQuery();
             while (rs.next()) {
                 dblBalance += uF.parseToDouble(rs.getString("accrued"));
             }
             rs.close();
             pst.close();
             
             pst = con.prepareStatement("select sum(leave_no) as count from leave_application_register where is_paid=true and (is_modify is null or is_modify=false) and emp_id=? and leave_type_id=? and _date>=?");
             pst.setInt(1, uF.parseToInt(strEmpId));
             pst.setInt(2, uF.parseToInt(strLeaveTypeId));
             pst.setDate(3, uF.getDateFormat(balanceDate, DATE_FORMAT));
//             System.out.println("LV/121--pst 2 ===>> " + pst);
             rs = pst.executeQuery();
             double dblPaidBalance = 0;
             while (rs.next()) {
             	dblPaidBalance = uF.parseToDouble(rs.getString("count"));
             }
             rs.close();
             pst.close();
             
             if(dblBalance > 0 && dblBalance >= dblPaidBalance){
            	 dblBalanceLeaves = dblBalance - dblPaidBalance;
             }
             
            
            String strEmpOrgId = CF.getEmpOrgId(con, uF, strEmpId);
            String strEmpWlocationId = CF.getEmpWlocationId(con, uF, strEmpId);
			String strEmpLevelId = CF.getEmpLevelId(con, strEmpId);
//			pst = con.prepareStatement("select * from emp_leave_type where leave_type_id = ? and level_id=(select a.level_id from level_details ld " +
//					"right join (select * from designation_details dd " +
//					"right join (select *, gd.designation_id as designationid from employee_official_details eod, grades_details gd " +
//					"where emp_id=? and  gd.grade_id=eod.grade_id) a on a.designationid=dd.designation_id) a on a.level_id=ld.level_id)");
            pst = con.prepareStatement("select * from emp_leave_type where leave_type_id = ? and level_id=? and org_id=? and wlocation_id=?"); 
            pst.setInt(1,uF.parseToInt(strLeaveTypeId));
			pst.setInt(2,uF.parseToInt(strEmpLevelId));
			pst.setInt(3,uF.parseToInt(strEmpOrgId));
			pst.setInt(4,uF.parseToInt(strEmpWlocationId));
//			System.out.println("LV/146--pst==>>>"+pst);
			rs = pst.executeQuery();
			Map<String,String> leaveTypeValid=new HashMap<String,String>();
			List<String> sandwichleavetype = new ArrayList<String>();
			
			while(rs.next()){
				leaveTypeValid.put("BALANCE", rs.getString("balance_validation"));
				leaveTypeValid.put("SUFFIX", rs.getString("leave_suffix"));
				leaveTypeValid.put("PREFIX", rs.getString("leave_prefix"));
				leaveTypeValid.put("PRIOR_DAYS", rs.getString("prior_days"));
				leaveTypeValid.put("FUTURE_DAYS", rs.getString("future_days"));
				leaveTypeValid.put("FUTURE_DAYS_MAX", rs.getString("future_days_max"));
				leaveTypeValid.put("MATERNITY", rs.getString("maternity_type_frequency"));
				leaveTypeValid.put("VALIDATION_DAYS", rs.getString("validation_days"));
				leaveTypeValid.put("SANDWITCH_TYPE", rs.getString("sandwich_type"));
				if (rs.getString("sandwich_leave_type") != null){
                    sandwichleavetype = Arrays.asList(rs.getString("sandwich_leave_type").split(","));
                }	
				leaveTypeValid.put("PRIOR_DAYS_FOR_ONE_DAY_LEAVE", rs.getString("prior_days_for_one_day_leave"));
				
				StringBuilder sbCombinationLeave = null;
				if(rs.getString("combination_leave") !=null && !rs.getString("combination_leave").trim().equals("") && !rs.getString("combination_leave").trim().equalsIgnoreCase("NULL")){
					List<String> al = Arrays.asList(rs.getString("combination_leave").split(","));	
					for(int i=0; al!=null && i<al.size();i++){
						if(uF.parseToInt(al.get(i).trim()) > 0){
							if(sbCombinationLeave == null){
								sbCombinationLeave = new StringBuilder();
								sbCombinationLeave.append(al.get(i).trim());
							} else {
								sbCombinationLeave.append(","+al.get(i).trim());
							}
						}
					}
				}
				
				leaveTypeValid.put("COMBINATION", (sbCombinationLeave != null ? sbCombinationLeave.toString() : null));
				leaveTypeValid.put("EFFECTIVE_DATE_TYPE", rs.getString("effective_date_type"));
				leaveTypeValid.put("CARRYFORWARD", rs.getString("is_carryforward"));
				
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_LEAVE_PRIOR_DAYS_NOTIFICATION))){
					leaveTypeValid.put("FUTURE_DAY1", rs.getString("future_days_1"));
					leaveTypeValid.put("NO_OF_LEAVES1", rs.getString("no_of_leaves1"));
					leaveTypeValid.put("NO_OF_LEAVES2", rs.getString("no_of_leaves2"));
					leaveTypeValid.put("NO_OF_LEAVES3", rs.getString("no_of_leaves3"));
				} else{
					leaveTypeValid.put("FUTURE_DAY1", "0");
					leaveTypeValid.put("NO_OF_LEAVES1", "0");
					leaveTypeValid.put("NO_OF_LEAVES2", "0");
					leaveTypeValid.put("NO_OF_LEAVES3", "0");
				}
				
				leaveTypeValid.put("IS_LONG_LEAVE", rs.getString("is_long_leave"));
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GAP_BETWEEN_TWO_APPLIED_LONG_LEAVE))){
					leaveTypeValid.put("LONG_LEAVE_GAP", rs.getString("long_leave_gap"));
				}else{
					leaveTypeValid.put("LONG_LEAVE_GAP", "0");
				}
				
				leaveTypeValid.put("MAX_LONG_LEAVE_LIMIT", rs.getString("long_leave_limit"));
				leaveTypeValid.put("MIN_LONG_LEAVE_LIMIT", rs.getString("min_long_leave_limit"));
				
				leaveTypeValid.put("MONTHLY_APPLY_LEAVE_LIMIT", rs.getString("monthly_apply_leave_limit"));

//				strMonthlyLimit = rs.getString("monthly_limit");
//				strConsecutiveLimit = rs.getString("consecutive_limit");
			}
            rs.close();
            pst.close();
            
       //===start parvez date: 05-11-2022===
            if(uF.parseToBoolean(hmFeatureStatus.get(F_EXTRA_WORKING_LAPS_DAYS_LIMIT_FOR_COMPOFF_LEAVE))){
	            pst = con.prepareStatement("select * from emp_leave_type where compensate_with = ? and level_id=? and org_id=? and wlocation_id=?"); 
	            pst.setInt(1,uF.parseToInt(strLeaveTypeId));
				pst.setInt(2,uF.parseToInt(strEmpLevelId));
				pst.setInt(3,uF.parseToInt(strEmpOrgId));
				pst.setInt(4,uF.parseToInt(strEmpWlocationId));
				rs = pst.executeQuery();
				while(rs.next()){
					leaveTypeValid.put("EXTRA_WORKING_LAPS_DAYS", rs.getString("laps_days"));
				}
				rs.close();
	            pst.close();
            }
       //===end parvez date: 05-11-2022===     
			
			if(leaveTypeValid.get("COMBINATION")!=null && leaveTypeValid.get("COMBINATION").trim().length()!=0) {
				pst=con.prepareStatement(" select * from emp_leave_entry where emp_id=? and leave_type_id not in(-1,"+leaveTypeValid.get("COMBINATION")+") and is_approved in (0,1) and (is_modify is null or is_modify=false)");
			} else {
				pst=con.prepareStatement(" select * from emp_leave_entry where emp_id=? and leave_type_id not in(-1) and is_approved in (0,1) and (is_modify is null or is_modify=false)");
			}
			pst.setInt(1, uF.parseToInt(strEmpId));
//			System.out.println("LV/216---pst==>>>"+pst);
			rs=pst.executeQuery();
			while(rs.next()) {
//				System.out.println("LV/219----"+strD2+"------"+rs.getString("approval_from")+"==>>>"+uF.parseToDouble(uF.dateDifference(strD2, DATE_FORMAT, rs.getString("approval_from"), DBDATE)));
				if(uF.parseToDouble(uF.dateDifference(strD2, DATE_FORMAT, rs.getString("approval_from"), DBDATE,CF.getStrTimeZone()))==2){
//					System.out.println("first==>>>");
					request.setAttribute("STATUS_MSG", "Sorry You can't combine this Leave");
					return SUCCESS;
				}
//				System.out.println("LV/225----"+strD1+"------"+rs.getString("approval_to_date")+"==>>>"+uF.dateDifference(rs.getString("approval_to_date"), DBDATE,strD1, DATE_FORMAT,CF.getStrTimeZone()));

				if(uF.parseToDouble(uF.dateDifference(rs.getString("approval_to_date"), DBDATE,strD1, DATE_FORMAT,CF.getStrTimeZone()))==2){
//					System.out.println("second==>>>");
					request.setAttribute("STATUS_MSG", "Sorry You can't combine this Leave");
					return SUCCESS;
				}
			}
            rs.close();
            pst.close();
			
//            System.out.println("LV/208--BALANCE ===>> " + leaveTypeValid.get("BALANCE"));
//            System.out.println("LV/209--dblBalanceLeaves ===>> " + dblBalanceLeaves);
			if(uF.parseToBoolean(leaveTypeValid.get("BALANCE")) && dblBalanceLeaves<=0) {
				request.setAttribute("STATUS_MSG", "Sorry You can't apply for more leave.");
				return SUCCESS;
			}else{
				
				if(uF.parseToDouble(leaveTypeValid.get("VALIDATION_DAYS"))==0) {
					
				}else{
					String strDiff = uF.dateDifference(strD1,DATE_FORMAT, strD2, DATE_FORMAT,CF.getStrTimeZone());
					if(uF.parseToDouble(leaveTypeValid.get("VALIDATION_DAYS"))<(uF.parseToDouble(strDiff)-dblBalanceLeaves)){
						request.setAttribute("STATUS_MSG","Sorry You can't apply for more negative leave limit.");
						return SUCCESS;
					}
				}
			}
			
		//===start parvez date: 08-03-2022===	
			
//			System.out.println("LV/255--leaveTypeValid="+leaveTypeValid.get("SANDWITCH_TYPE"));
//			System.out.println("LV/256--sandwichleavetype="+sandwichleavetype);
			if((uF.parseToDouble(leaveTypeValid.get("SANDWITCH_TYPE"))==1 || uF.parseToDouble(leaveTypeValid.get("SANDWITCH_TYPE"))==2)){
				
				boolean isSandwichDate = false;
				double strDateDiff1 = uF.parseToDouble(uF.dateDifference(strD1,DATE_FORMAT, strD2, DATE_FORMAT,CF.getStrTimeZone()));
				int cnt = 0;
//				System.out.println("LV/261--strDateDiff1="+strDateDiff1);
				
				for(int i=0; i<strDateDiff1; i++){
					String strApplyDate = uF.getDateFormat(uF.getFutureDate(d1, i)+"", DBDATE, DATE_FORMAT);
//					System.out.println("LV/266--strApplyDate="+strApplyDate);
					if(sandwichleavetype != null && sandwichleavetype.contains("-2")) {
						boolean isHoliday = CF.checkHoliday(con, uF, strApplyDate, ""+strEmpWlocationId, ""+strEmpOrgId); 
						if(isHoliday){
							isSandwichDate = true;
							cnt++;
						}
					}
					
					if(sandwichleavetype != null && sandwichleavetype.contains("-1") ){
						boolean isEmpRosterWeekOff = CF.checkEmpRosterWeeklyOff(con, CF, uF, strEmpId, strApplyDate, strEmpLevelId, ""+strEmpWlocationId, ""+strEmpOrgId);
						if(isEmpRosterWeekOff){
							isSandwichDate = true;
							cnt++;
//							System.out.println("LV/280--cnt="+cnt+"---strApplyDate="+strApplyDate);
						}
					}
					
					if(sandwichleavetype != null && sandwichleavetype.contains("0") ){
						
						boolean isHoliday = CF.checkHoliday(con, uF, strApplyDate, ""+strEmpWlocationId, ""+strEmpOrgId); 
						if(isHoliday){
							isSandwichDate = true;
							cnt++;
						}
						
						boolean isEmpRosterWeekOff = CF.checkEmpRosterWeeklyOff(con, CF, uF, strEmpId, strApplyDate, strEmpLevelId, ""+strEmpWlocationId, ""+strEmpOrgId);
						if(isEmpRosterWeekOff){
							isSandwichDate = true;
							cnt++;
//							System.out.println("LV/296--cnt="+cnt+"---strApplyDate="+strApplyDate);
						}
						
					}
					
				}
				
				
				double aplyDayCnt = strDateDiff1-cnt;
//				System.out.println("LV/284---aplyDayCnt="+aplyDayCnt+"====>cnt="+cnt);
				
		//===start parvez date: 02-07-2022===		
				if(uF.parseToBoolean(leaveTypeValid.get("BALANCE")) && isSandwichDate && dblBalanceLeaves <= aplyDayCnt){
		//===end parvez date: 02-07-2022===			
//					System.out.println("LV/285--isSandwichDate----if");
					request.setAttribute("STATUS_MSG", "Sorry You can't apply for more leave.");
					return SUCCESS;
				}
				/*if(isSandwichDate && dblBalanceLeaves < strDateDiff1){
					System.out.println("LV/285--isSandwichDate----if");
					request.setAttribute("STATUS_MSG", "Sorry You can't apply for more leave.");
					return SUCCESS;
				}*/
			}
		//===end parvez date: 08-03-2022===	
			
			SimpleDateFormat smft = new SimpleDateFormat(DATE_FORMAT);
			java.util.Date currentDate = new java.util.Date();
			smft.format(currentDate);
			uF.getCurrentDate(CF.getStrTimeZone());
			String strFutureDaysDiff = uF.dateDifference(smft.format(currentDate), DATE_FORMAT, strD1, DATE_FORMAT, CF.getStrTimeZone());
			String strPriorDaysDiff = uF.dateDifference(strD1, DATE_FORMAT, smft.format(currentDate), DATE_FORMAT, CF.getStrTimeZone());
//			System.out.println("LV/329---strPriorDaysDiff="+strPriorDaysDiff);
			
			double priorDays = uF.parseToDouble(leaveTypeValid.get("PRIOR_DAYS"));
			double futureDays = uF.parseToDouble(leaveTypeValid.get("FUTURE_DAYS"));
			double futureDaysMax = uF.parseToDouble(leaveTypeValid.get("FUTURE_DAYS_MAX"));
			double leavePriorDays = uF.parseToDouble(strPriorDaysDiff)-1;
			double leaveFutureDays = uF.parseToDouble(strFutureDaysDiff);
			
			double futureOneDay = uF.parseToDouble(leaveTypeValid.get("PRIOR_DAYS_FOR_ONE_DAY_LEAVE"));
			double nAplyDayCnt = uF.parseToDouble(uF.dateDifference(strD1,DATE_FORMAT, strD2, DATE_FORMAT,CF.getStrTimeZone()));
			
			double futureOneDay1 = uF.parseToDouble(leaveTypeValid.get("FUTURE_DAY1"));
			double nOfLeave1 = uF.parseToDouble(leaveTypeValid.get("NO_OF_LEAVES1"));
			double nOfLeave2 = uF.parseToDouble(leaveTypeValid.get("NO_OF_LEAVES2"));
			double nOfLeave3 = uF.parseToDouble(leaveTypeValid.get("NO_OF_LEAVES3"));
			
			if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_LEAVE_PRIOR_DAYS_NOTIFICATION))){
				if(priorDays>0 && priorDays <= leavePriorDays) {
					if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
						request.setAttribute("STATUS_MSG", "You can only apply leave for "+Math.round(priorDays)+" days before today"); //past
					} else {
						request.setAttribute("STATUS_MSG", "You are applying beyond the date range, should we proceed?"); //past
					}
	//				System.out.println("else if in prior notice==");
					return SUCCESS;
				}
				
				if((nAplyDayCnt >= nOfLeave1 && nAplyDayCnt < nOfLeave2) && leaveFutureDays>=0 && ((futureOneDay >= leaveFutureDays && futureOneDay>0) || (futureDaysMax <= leaveFutureDays && futureDaysMax>0))) { //(futureDays>0 || futureDaysMax>0) && 
					if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
						StringBuilder sbMsg = new StringBuilder();
						sbMsg.append("You can apply for ");
						if(nAplyDayCnt>0) {
							sbMsg.append(Math.round(nAplyDayCnt)+" day leave ");
						}
						if(futureOneDay>0) {
							sbMsg.append("minimum "+Math.round(futureOneDay)+" days ");
						}
						sbMsg.append("prior to the leave date");
						request.setAttribute("STATUS_MSG", sbMsg.toString()); //future
					} else {
						request.setAttribute("STATUS_MSG", "You are applying beyond the date range, should we proceed?"); //future
					}
					return SUCCESS;
				} else if(nAplyDayCnt >= nOfLeave2 && nAplyDayCnt < nOfLeave3 && leaveFutureDays>=0 && ((futureDays >= leaveFutureDays && futureDays>0) || (futureDaysMax <= leaveFutureDays && futureDaysMax>0))) { //(futureDays>0 || futureDaysMax>0) && 
					
					if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
						StringBuilder sbMsg = new StringBuilder();
						sbMsg.append("You can apply for Short leaves ");
						if(futureDays>0) {
							sbMsg.append(" minimum "+Math.round(futureDays)+" days ");
						}
						sbMsg.append(" prior to the leave date");
						request.setAttribute("STATUS_MSG", sbMsg.toString()); //future
					} else {
						request.setAttribute("STATUS_MSG", "You are applying beyond the date range, should we proceed?"); //future
					}
					return SUCCESS;
				} else if(nAplyDayCnt >= nOfLeave3 && leaveFutureDays>=0 && ((futureOneDay1 >= leaveFutureDays && futureOneDay1>0) || (futureDaysMax <= leaveFutureDays && futureDaysMax>0))) { //(futureDays>0 || futureDaysMax>0) && 
//					System.out.println("LV/434--else if-2-nOfLeave3=="+nOfLeave3);
					if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
						StringBuilder sbMsg = new StringBuilder();
						sbMsg.append("You can apply for Long leaves ");
						if(futureOneDay1>0) {
							sbMsg.append(" minimum "+Math.round(futureOneDay1)+" days ");
						}
						sbMsg.append(" prior to the leave date");
						request.setAttribute("STATUS_MSG", sbMsg.toString()); //future
					} else {
						request.setAttribute("STATUS_MSG", "You are applying beyond the date range, should we proceed?"); //future
					}
					return SUCCESS;
				}
			} else{
				if(priorDays>0 && priorDays <= leavePriorDays) {
					if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
						request.setAttribute("STATUS_MSG", "You can only apply leave for "+Math.round(priorDays)+" days before today"); //past
					} else {
						request.setAttribute("STATUS_MSG", "You are applying beyond the date range, should we proceed?"); //past
					}
	//				System.out.println("else if in prior notice==");
					return SUCCESS;
				} else if(nAplyDayCnt == 1 && leaveFutureDays>=0 && ((futureOneDay >= leaveFutureDays && futureOneDay>0) || (futureDaysMax <= leaveFutureDays && futureDaysMax>0))) { //(futureDays>0 || futureDaysMax>0) && 
					if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
						StringBuilder sbMsg = new StringBuilder();
						sbMsg.append("You can only apply leave ");
						if(futureOneDay>0) {
							sbMsg.append("after "+Math.round(futureOneDay)+" days ");
						}
						if(futureOneDay>0 && futureDaysMax>0) {
							sbMsg.append("and ");
						}
						if(futureDaysMax>0) {
							sbMsg.append("before "+Math.round(futureDaysMax)+" days ");
						}
						sbMsg.append("from today");
						request.setAttribute("STATUS_MSG", sbMsg.toString()); //future
					} else {
						request.setAttribute("STATUS_MSG", "You are applying beyond the date range, should we proceed?"); //future
					}
	//				System.out.println("in prior notice==");
					return SUCCESS;
				} else if(nAplyDayCnt > 1 && leaveFutureDays>=0 && ((futureDays >= leaveFutureDays && futureDays>0) || (futureDaysMax <= leaveFutureDays && futureDaysMax>0))) { //(futureDays>0 || futureDaysMax>0) && 
					if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
						StringBuilder sbMsg = new StringBuilder();
						sbMsg.append("You can only apply leave ");
						if(futureDays>0) {
							sbMsg.append("after "+Math.round(futureDays)+" days ");
						}
						if(futureDays>0 && futureDaysMax>0) {
							sbMsg.append("and ");
						}
						if(futureDaysMax>0) {
							sbMsg.append("before "+Math.round(futureDaysMax)+" days ");
						}
						sbMsg.append("from today");
						request.setAttribute("STATUS_MSG", sbMsg.toString()); //future
					} else {
						request.setAttribute("STATUS_MSG", "You are applying beyond the date range, should we proceed?"); //future
					}
	//				System.out.println("in prior notice==");
					return SUCCESS;
				}
			}
			
		//===end parvez date: 15-09-2022===
			
			String strEmpJoiningDate = CF.getEmpJoiningDate(con, uF, uF.parseToInt(strEmpId));
			if(uF.getDateFormatUtil(strEmpJoiningDate, DATE_FORMAT).after(d1)
					|| uF.getDateFormatUtil(strEmpJoiningDate, DATE_FORMAT).after(d2)){
				
				request.setAttribute("STATUS_MSG","You can not apply leaves before your joining date");
				return SUCCESS;
			}
			
			if(uF.parseToBoolean(hmFeatureStatus.get(F_EXTRA_WORKING_LAPS_DAYS_LIMIT_FOR_COMPOFF_LEAVE))){
				
				pst = con.prepareStatement("select max(_date) as _date from leave_register1 where emp_id =? and leave_type_id=? and compensate_id>0"); 
				pst.setInt(1,uF.parseToInt(strEmpId));
				pst.setInt(2,uF.parseToInt(strLeaveTypeId));
				rs = pst.executeQuery();
				String lapsDate = null;
				while(rs.next()){
					if(rs.getString("_date")!=null){
						lapsDate = uF.getFutureDate(uF.getDateFormatUtil(rs.getString("_date"), DBDATE), uF.parseToInt(leaveTypeValid.get("EXTRA_WORKING_LAPS_DAYS")))+"";
					}
					
				}
	            rs.close();
	            pst.close();
//	            System.out.println("lapsDate="+uF.getDateFormatUtil(lapsDate, DBDATE)+"---strD2=="+uF.getDateFormatUtil(strD2, DATE_FORMAT));
				if(uF.parseToBoolean(leaveTypeValid.get("BALANCE")) && lapsDate!=null && uF.getDateFormatUtil(strD2, DATE_FORMAT).after(uF.getDateFormatUtil(lapsDate, DBDATE))){
					request.setAttribute("STATUS_MSG", "Sorry You can't apply for more leave.");
					return SUCCESS;
				}
			}
			
		//===start parvez date: 06-12-2022===
			String[] strFinancialYear = CF.getFinancialYear(con, smft.format(currentDate), CF, uF);	////added by parvez date: 06-12-2022
			String financialYearStartDate = null;
			String financialYearEndDate = null;
			if(strFinancialYear!=null && strFinancialYear.length>0){
				financialYearStartDate = strFinancialYear[0];
				financialYearEndDate = strFinancialYear[1];
			}
			int nMonth = uF.parseToInt(uF.getDateFormat(smft.format(currentDate), DATE_FORMAT, "MM"));
			int nYear = uF.parseToInt(uF.getDateFormat(smft.format(currentDate), DATE_FORMAT, "yyyy"));
			String yearEndDate = null;
			if(leaveTypeValid.get("EFFECTIVE_DATE_TYPE").equals("FY")){
				yearEndDate = financialYearEndDate;
			}else if(leaveTypeValid.get("EFFECTIVE_DATE_TYPE").equals("CY")){
				yearEndDate = "31/12/" + nYear;
			} else if(leaveTypeValid.get("EFFECTIVE_DATE_TYPE").equals("CMY")){
				if(nMonth > 0 && nMonth <= 6){
					yearEndDate = "30/06/" + nYear;
				} else{
					yearEndDate = "31/12/" + nYear;
				}
			} else if(leaveTypeValid.get("EFFECTIVE_DATE_TYPE").equals("FMY")){
				if(nMonth >= 4 && nMonth <=10){
					yearEndDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormatUtil(financialYearStartDate, DATE_FORMAT), 183)+"", DBDATE, DATE_FORMAT);
				}else{
					yearEndDate = financialYearEndDate;
				}
			}
			
			
			if(dblBalanceLeaves > 0 && !uF.parseToBoolean(leaveTypeValid.get("CARRYFORWARD"))){
				if(uF.getDateFormatUtil(strD1, DATE_FORMAT).after(uF.getDateFormatUtil(yearEndDate, DATE_FORMAT)) ||uF.getDateFormatUtil(strD2, DATE_FORMAT).after(uF.getDateFormatUtil(yearEndDate, DATE_FORMAT))){
					request.setAttribute("STATUS_MSG", "Sorry You can't apply leave Since leave isn't carryforward to next year.");
					return SUCCESS;
				}
			}
		//===end parvez date: 06-12-2022===	
			if(uF.parseToBoolean(hmFeatureStatus.get(F_GAP_BETWEEN_TWO_APPLIED_LONG_LEAVE))){
				String lastAppliedDate = null;
				double nlastAppliedLeaveCnt = 0;
				pst=con.prepareStatement("select max(approval_from) as approval_from, max(approval_to_date) as approval_to_date" +
						" from emp_leave_entry where is_approved>=0 and emp_id=? and leave_type_id=? and (is_modify is null or is_modify=false) and emp_no_of_leave>?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setInt(2, uF.parseToInt(strLeaveTypeId));
				pst.setDouble(3, uF.parseToDouble(leaveTypeValid.get("MIN_LONG_LEAVE_LIMIT")));
				rs = pst.executeQuery();
				while(rs.next()){
					lastAppliedDate = uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, DATE_FORMAT);
					nlastAppliedLeaveCnt = uF.parseToDouble(uF.dateDifference(rs.getString("approval_from"),DBDATE,rs.getString("approval_to_date"),DBDATE,CF.getStrTimeZone()));
//					System.out.println("LV/580---nlastAppliedLeave=="+nlastAppliedLeaveCnt);
				}
				rs.close();
	            pst.close();
//	            System.out.println("lastAppliedDate=="+lastAppliedDate+"---leaveTypeValid.get(LONG_LEAVE_GAP)==="+leaveTypeValid.get("LONG_LEAVE_GAP"));
	            
	            if(nlastAppliedLeaveCnt > 0 && nAplyDayCnt > uF.parseToDouble(leaveTypeValid.get("MIN_LONG_LEAVE_LIMIT")) && uF.parseToInt(leaveTypeValid.get("LONG_LEAVE_GAP"))>0 ){
	            	double strAppliedDateDiff = uF.parseToDouble(uF.dateDifference(lastAppliedDate,DATE_FORMAT, strD1, DATE_FORMAT,CF.getStrTimeZone()));
	            	
	            	if(strAppliedDateDiff<=uF.parseToInt(leaveTypeValid.get("LONG_LEAVE_GAP"))){
	            		request.setAttribute("STATUS_MSG", "You can only apply long leave after 30 days.");
						return SUCCESS;
	            	}
	            }
			}
			
			if(leaveTypeValid.get("MONTHLY_APPLY_LEAVE_LIMIT") != null && !leaveTypeValid.get("MONTHLY_APPLY_LEAVE_LIMIT").equals("") && uF.parseToDouble(leaveTypeValid.get("MONTHLY_APPLY_LEAVE_LIMIT")) > 0){
				String tempDates = uF.getCurrentMonthMinMaxDate(strD1, DATE_FORMAT);
				String[] arrDates = tempDates.split("::::");
				String monthStartDate = arrDates[0];
				String monthEndDate = arrDates[1];
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from emp_leave_entry where emp_id = ? and leave_type_id=? and is_approved>=0 and (is_modify is null or is_modify=false) ");
				sbQuery.append("and ((approval_from <= '"+uF.getDateFormat(monthStartDate, DATE_FORMAT,DBDATE)+"' and approval_to_date >= '"+uF.getDateFormat(monthStartDate, DATE_FORMAT,DBDATE)+"')");
				sbQuery.append("or (approval_from >= '"+uF.getDateFormat(monthStartDate, DATE_FORMAT,DBDATE)+"' and approval_from <= '"+uF.getDateFormat(monthEndDate, DATE_FORMAT,DBDATE)+"')");
				sbQuery.append("or (approval_from >= '"+uF.getDateFormat(monthStartDate, DATE_FORMAT,DBDATE)+"' and approval_to_date <= '"+uF.getDateFormat(monthEndDate, DATE_FORMAT,DBDATE)+"')");
				sbQuery.append("or (approval_from <= '"+uF.getDateFormat(monthStartDate, DATE_FORMAT,DBDATE)+"' and approval_to_date >= '"+uF.getDateFormat(monthEndDate, DATE_FORMAT,DBDATE)+"')");
				sbQuery.append("or (approval_to_date >= '"+uF.getDateFormat(monthStartDate, DATE_FORMAT,DBDATE)+"' and approval_to_date <= '"+uF.getDateFormat(monthEndDate, DATE_FORMAT,DBDATE)+"'))");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setInt(2, uF.parseToInt(strLeaveTypeId));
				rs = pst.executeQuery();
//				System.out.println("LV/606---pst===>"+pst);
				int count = 0;
				while(rs.next()){
					int strDateDifference = uF.parseToInt(uF.dateDifference(rs.getString("approval_from"), DBDATE, rs.getString("approval_to_date"), DBDATE));
//					System.out.println("strDateDifference=="+strDateDifference);
					for(int i=0; i < strDateDifference; i++){
						String dbAppliedDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormatUtil(rs.getString("approval_from"), DBDATE), i)+"", DBDATE, DATE_FORMAT);
//						System.out.println("dbAppliedDate=="+dbAppliedDate);
						if((uF.getDateFormatUtil(dbAppliedDate,DATE_FORMAT).after(uF.getDateFormatUtil(monthStartDate,DATE_FORMAT)) || uF.getDateFormatUtil(dbAppliedDate,DATE_FORMAT).equals(uF.getDateFormatUtil(monthStartDate,DATE_FORMAT)))
								&& (uF.getDateFormatUtil(dbAppliedDate,DATE_FORMAT).before(uF.getDateFormatUtil(monthEndDate,DATE_FORMAT)) || uF.getDateFormatUtil(dbAppliedDate,DATE_FORMAT).equals(uF.getDateFormatUtil(monthEndDate,DATE_FORMAT)))){
							count++;
						}
					}
				}
				rs.close();
				pst.close();
//				System.out.println("count=="+count);
				
				double leaveApplyDayCnt = nAplyDayCnt + count;
//				System.out.println("LV/627--leaveApplyDayCnt=="+leaveApplyDayCnt+"---MONTHLY_APPLY_LEAVE_LIMIT==="+uF.parseToDouble(leaveTypeValid.get("MONTHLY_APPLY_LEAVE_LIMIT")));
				if(leaveApplyDayCnt > uF.parseToDouble(leaveTypeValid.get("MONTHLY_APPLY_LEAVE_LIMIT"))){
					request.setAttribute("STATUS_MSG", "You can only apply "+Math.round(uF.parseToDouble(leaveTypeValid.get("MONTHLY_APPLY_LEAVE_LIMIT")))+" leaves in a month.");
					return SUCCESS;
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
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
	
}