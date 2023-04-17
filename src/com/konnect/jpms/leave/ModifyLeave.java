package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ModifyLeave extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF=null;
	String strUserType = null;
	String strSessionEmpId = null;

	String leaveId;
	String leaveTypeId;
	String leaveRegisterId;
	String isCompensate;
	String modify;
	String cancelReason;
	String type;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		
		UtilityFunctions uF = new UtilityFunctions();
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		if(getModify()!=null){
			modifyLeaveDetails(uF);
			if(getType()!=null && getType().trim().equalsIgnoreCase("emp")) {
				return "updateemp";
			} else {
				return "update";
			}
		}
		viewLeaveDetails(uF);
		
		return SUCCESS;

	}
	
	private void viewLeaveDetails(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con=db.makeConnection(con);
			

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst = con.prepareStatement("select * from leave_type where leave_type_id  = ?");
			pst.setInt(1, uF.parseToInt(getLeaveTypeId()));
			rs = pst.executeQuery();
			String strLeaveType = null;
			boolean isCompensatory = false;
			while(rs.next()){
				strLeaveType = rs.getString("leave_type_name");
				isCompensatory = uF.parseToBoolean(rs.getString("is_compensatory"));
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select sum(leave_no) as leave_no, leave_id from leave_application_register where leave_id in (select leave_id " +
//					"from emp_leave_entry where leave_id=? and entrydate is not null and (istravel is null or istravel=false)) group by leave_id");
			pst = con.prepareStatement("select count(leave_register_id) as leave_register_id, leave_id from leave_application_register where leave_id in (select leave_id " +
				"from emp_leave_entry where leave_id=? and entrydate is not null and (istravel is null or istravel=false)) group by leave_id");
			pst.setInt(1, uF.parseToInt(getLeaveId()));
			rs = pst.executeQuery();
			double dblLeaveDays = 0.0d;
			while(rs.next()){
				dblLeaveDays = uF.parseToDouble(rs.getString("leave_register_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from emp_leave_entry eld left join employee_personal_details epd on eld.modify_by = epd.emp_per_id where eld.leave_id=? order by approval_from");
			pst.setInt(1, uF.parseToInt(getLeaveId()));
			rs = pst.executeQuery();
			List<Map<String,String>> alReport = new ArrayList<Map<String,String>>();
			while(rs.next()){
				
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("LEAVE_ID", rs.getString("leave_id"));
				hmInner.put("LEAVE_TYPE_ID", rs.getString("leave_type_id"));
				hmInner.put("LEAVE_TYPE_NAME", strLeaveType);
				hmInner.put("LEAVE_FROM", uF.getDateFormat(rs.getString("approval_from"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("LEAVE_TO", uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, CF.getStrReportDateFormat()));

				String strApproveLeaveDays = "";
				if(!rs.getBoolean("istravel") && rs.getInt("is_approved")==1  && !uF.parseToBoolean(rs.getString("ishalfday"))&& dblLeaveDays > 0.0d){
					strApproveLeaveDays = ""+dblLeaveDays;
				} else {
					strApproveLeaveDays = ""+uF.parseToDouble(rs.getString("emp_no_of_leave"));
				}
				hmInner.put("LEAVE_NO", strApproveLeaveDays);
				hmInner.put("LEAVE_MODIFY", rs.getString("is_modify"));
				hmInner.put("LEAVE_MODIFY_DATE", uF.getDateFormat(rs.getString("modify_date"), DBDATE, CF.getStrReportDateFormat()));
				
			//	String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
			
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				String strEmpName = rs.getString("emp_fname") + strEmpMName+" "+ rs.getString("emp_lname");
				hmInner.put("LEAVE_MODIFY_BY", strEmpName);
				hmInner.put("LEAVE_MODIFY_REASON", rs.getString("cancel_reason"));
				hmInner.put("LEAVE_IS_COMPENSATORY", isCompensatory+"");
				
				alReport.add(hmInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReport", alReport);
			request.setAttribute("uF", uF);
					
		} catch (Exception e) {
			e.printStackTrace(); 
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void modifyLeaveDetails(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con=db.makeConnection(con);
			con.setAutoCommit(false);

			Map<String, String> hmLeaveType = CF.getLeaveTypeCode(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("update leave_application_register set is_modify = true, modify_date=?, modify_by=? where leave_id= ?");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(3, uF.parseToInt(getLeaveId()));
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0){
				String strLeaveTypeId = "";
				pst = con.prepareStatement("update emp_leave_entry set is_modify = true, modify_date=?, modify_by=?, cancel_reason=? where leave_id=?");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setString(3, getCancelReason());
				pst.setInt(4, uF.parseToInt(getLeaveId()));
				int y = pst.executeUpdate();
				pst.close();
				
				if(y > 0){
					
					pst = con.prepareStatement("select * from emp_leave_entry where leave_id=?");
					pst.setInt(1, uF.parseToInt(getLeaveId()));
					rs = pst.executeQuery();
					Map<String, String> hmEmpLeave = new HashMap<String, String>();
					while(rs.next()){
						hmEmpLeave.put("EMP_ID", rs.getString("emp_id"));
						hmEmpLeave.put("FROM_DATE", uF.getDateFormat(rs.getString("approval_from"),DBDATE, DATE_FORMAT));
						hmEmpLeave.put("TO_DATE", uF.getDateFormat(rs.getString("approval_to_date"),DBDATE, DATE_FORMAT));
						hmEmpLeave.put("LEAVE_TYPE_ID", rs.getString("leave_type_id"));
						hmEmpLeave.put("IS_HALF_DAY", rs.getString("ishalfday"));
						hmEmpLeave.put("IS_TRAVEL", rs.getString("istravel"));
						strLeaveTypeId = rs.getString("leave_type_id");
					}
					rs.close();
					pst.close();
					
//					System.out.println("getType ===>> " + getType());
					if(uF.parseToInt(hmEmpLeave.get("EMP_ID")) > 0 && getType()!=null && getType().trim().equalsIgnoreCase("emp")){
						String strLeaveFrom = hmEmpLeave.get("FROM_DATE");
						String strLeaveTo = hmEmpLeave.get("TO_DATE");
						
						List<String> alManagers = new ArrayList<String>();
						List<String> alManagersUserId = new ArrayList<String>();
						pst=con.prepareStatement("select wfd.emp_id,wfd.user_type_id from emp_leave_entry ele,work_flow_details wfd where ele.leave_id = wfd.effective_id " +
								"and ele.leave_id=? and wfd.effective_type=?");
						pst.setInt(1,uF.parseToInt(getLeaveId()));
						pst.setString(2,WORK_FLOW_LEAVE);
						rs=pst.executeQuery();
						while(rs.next()){
							if(uF.parseToInt(rs.getString("emp_id")) > 0){
								if(!alManagers.contains(rs.getString("emp_id"))){
									alManagers.add(rs.getString("emp_id"));
								}
								if(!alManagersUserId.contains(rs.getString("emp_id")+"_"+rs.getString("user_type_id"))){
									alManagersUserId.add(rs.getString("emp_id")+"_"+rs.getString("user_type_id"));
								}
							}
						}
						rs.close();
						pst.close();
						
						pst = con.prepareStatement("select * from leave_type where leave_type_id=?");
						pst.setInt(1, uF.parseToInt(hmEmpLeave.get("LEAVE_TYPE_ID")));
						rs = pst.executeQuery();
						String strLeaveTypeName = null;
						while(rs.next()){
							strLeaveTypeName = rs.getString("leave_type_name");
						}
						rs.close();
						pst.close();
						
						String strDomain = request.getServerName().split("\\.")[0];
						String strLeaveLbl = "leave";
//						String strLeaveNotiLbl = N_EMPLOYEE_LEAVE_CANCEL+"";
						String strLeaveNotiLbl = N_EMPLOYEE_LEAVE_PULLOUT+"";
						if(uF.parseToBoolean(getIsCompensate())) {
							strLeaveLbl = "extra working";
//							strLeaveNotiLbl = N_EMPLOYEE_EXTRA_WORK_CANCEL+"";
							strLeaveNotiLbl = N_EMPLOYEE_EXTRA_WORK_PULLOUT+"";
						}
						if(TRAVEL_LEAVE == uF.parseToInt(hmEmpLeave.get("LEAVE_TYPE_ID"))) {
							strLeaveLbl = "travel";
//							strLeaveNotiLbl = N_EMPLOYEE_TRAVEL_CANCEL+"";
							strLeaveNotiLbl = N_EMPLOYEE_TRAVEL_PULLOUT+"";
						}
						for(int i=0; alManagersUserId!=null && i<alManagersUserId.size();i++){
							String[] temp = alManagersUserId.get(i).split("_");
							String strManagerId = temp[0];
							String strUserTypeId = temp[1];
							
							String alertData = "<div style=\"float: left;\"> <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>'s "+strLeaveLbl+" '<strong>"+strLeaveTypeName+"</strong>' "+uF.getDateFormat(hmEmpLeave.get("FROM_DATE"), DATE_FORMAT, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(hmEmpLeave.get("TO_DATE"), DATE_FORMAT, CF.getStrReportDateFormat())+" have pulled out. </div>";
							String alertAction = "ManagerLeaveApprovalReport.action?pType=WR";
							
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strManagerId);
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(strUserTypeId);
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();							
							
						}
						
						for(int i=0; alManagers!=null && i<alManagers.size();i++){
							Notifications nF = new Notifications(uF.parseToInt(strLeaveNotiLbl), CF);
							nF.setDomain(strDomain);
							nF.request = request;
							nF.session = session;
							nF.setStrHostAddress(CF.getStrEmailLocalHost());
							nF.setStrHostPort(CF.getStrHostPort());
							nF.setStrContextPath(request.getContextPath());
							nF.setStrEmpId(hmEmpLeave.get("EMP_ID"));
							nF.setSupervisor(false);
							nF.setEmailTemplate(true);
							
							pst = con.prepareStatement(selectEmpDetails1);
							pst.setInt(1, uF.parseToInt((String)alManagers.get(i)));
							rs = pst.executeQuery();
							boolean flg=false;
							while(rs.next()){
								nF.setStrSupervisorEmail(rs.getString("emp_email"));					
							
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								nF.setStrSupervisorName(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
								nF.setStrSupervisorContactNo(rs.getString("emp_contactno"));
								if(rs.getString("emp_email_sec")!=null && rs.getString("emp_email_sec").indexOf("@")>0){
									nF.setStrEmpEmail(rs.getString("emp_email_sec"));
									nF.setStrEmailTo(rs.getString("emp_email_sec"));
								} else {
									nF.setStrEmpEmail(rs.getString("emp_email"));
									nF.setStrEmailTo(rs.getString("emp_email"));
								}
								flg=true;
							}
							rs.close();
							pst.close();
							
							if(flg){
								if(uF.parseToBoolean(hmEmpLeave.get("IS_HALF_DAY"))){
									nF.setStrEmpLeaveFrom(uF.getDateFormat(strLeaveFrom, DATE_FORMAT, CF.getStrReportDateFormat()));
									nF.setStrEmpLeaveTo(uF.getDateFormat(strLeaveFrom, DATE_FORMAT, CF.getStrReportDateFormat()));
									nF.setStrEmpLeaveNoOfDays("0.5");	
								}else{
									nF.setStrEmpLeaveFrom(uF.getDateFormat(strLeaveFrom, DATE_FORMAT, CF.getStrReportDateFormat()));
									nF.setStrEmpLeaveTo(uF.getDateFormat(strLeaveTo, DATE_FORMAT, CF.getStrReportDateFormat()));
									nF.setStrEmpLeaveNoOfDays(uF.dateDifference(strLeaveFrom,DATE_FORMAT, strLeaveTo, DATE_FORMAT,CF.getStrTimeZone()));
								}
								nF.setStrLeaveCancelReason(uF.showData(getCancelReason(), ""));	
								nF.setStrLeaveTypeName(uF.showData(hmLeaveType.get(strLeaveTypeId), ""));
								nF.setStrEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT_STR));
								nF.sendNotifications();
							}
						}
						
					} else {	
						String strSupervisorName = CF.getEmpNameMapByEmpId(con, (String)session.getAttribute(EMPID));
						pst = con.prepareStatement("select * from leave_type where leave_type_id=?");
						pst.setInt(1, uF.parseToInt(hmEmpLeave.get("LEAVE_TYPE_ID")));
						rs = pst.executeQuery();
						String strLeaveTypeName = null;
						while(rs.next()){
							strLeaveTypeName = rs.getString("leave_type_name");
						}
						rs.close();
						pst.close();
						
						Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
						String strLeaveLbl = "leave";
						String strLeaveNotiLbl = N_EMPLOYEE_LEAVE_CANCEL+"";
						if(uF.parseToBoolean(getIsCompensate())) {
							strLeaveLbl = "extra working";
							strLeaveNotiLbl = N_EMPLOYEE_EXTRA_WORK_CANCEL+"";
						}
						if(TRAVEL_LEAVE == uF.parseToInt(hmEmpLeave.get("LEAVE_TYPE_ID"))) {
							strLeaveLbl = "travel";
							strLeaveNotiLbl = N_EMPLOYEE_TRAVEL_CANCEL+"";
						}
						String alertData = "<div style=\"float: left;\"> Your "+strLeaveLbl+" '<strong>"+strLeaveTypeName+"</strong>' "+uF.getDateFormat(hmEmpLeave.get("FROM_DATE"), DATE_FORMAT, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(hmEmpLeave.get("TO_DATE"), DATE_FORMAT, CF.getStrReportDateFormat())+" has been canceled by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
						String alertAction = "MyPay.action?pType=WR&callFrom=MyDashLeaveSummary";
						
						String strDomain = request.getServerName().split("\\.")[0];
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(hmEmpLeave.get("EMP_ID"));
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
					
						
						Notifications nF = new Notifications(uF.parseToInt(strLeaveNotiLbl), CF); 
		                nF.setDomain(strDomain);
						nF.request = request;
						nF.session = session;
//		                nF.setSupervisor(true);
//						System.out.println("====>EMPID"+hmEmpLeave.get("EMP_ID"));
						hmEmpLeave.get("EMP_ID");
		                nF.setStrEmpId(hmEmpLeave.get("EMP_ID"));
//		                nF.setStrHostAddress(request.getRemoteHost());
		                nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
		                nF.setStrContextPath(request.getContextPath());
		                nF.setStrLeaveTypeName(uF.showData(strLeaveTypeName, ""));
		                nF.setStrEmpLeaveFrom(uF.getDateFormat(hmEmpLeave.get("FROM_DATE"), DATE_FORMAT, CF.getStrReportDateFormat()));
		                nF.setStrEmpLeaveTo(uF.getDateFormat(hmEmpLeave.get("TO_DATE"), DATE_FORMAT, CF.getStrReportDateFormat()));
		                nF.setStrEmpLeaveNoOfDays(uF.dateDifference(hmEmpLeave.get("FROM_DATE"),DATE_FORMAT, hmEmpLeave.get("TO_DATE"), DATE_FORMAT,CF.getStrTimeZone()));					
						nF.setStrManagerName(uF.showData(strSupervisorName, ""));
		                nF.setStrLeaveCancelReason(uF.showData(getCancelReason(), ""));	
						nF.setStrEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT_STR));
						nF.setEmailTemplate(true);
						nF.setSupervisor(false);
		                nF.sendNotifications();	
					
					}
				}
			}	
			con.commit();
			request.setAttribute("STATUS_MSG", "Canceled");
					
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace(); 
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
//	private void modifyLeaveDetails(UtilityFunctions uF) {
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//		try {
//			con=db.makeConnection(con);
//			
//			pst = con.prepareStatement("select * from leave_application_register where leave_register_id  = ?");
//			pst.setInt(1, uF.parseToInt(getLeaveRegisterId()));
//			rs = pst.executeQuery();
//			boolean isPaid = false;
//			int nLeaveTypeId = 0;
//			int nLeaveId = 0;
//			double dblLeaveNo = 0;
//			String currDate=null;
//			String empId=null;
//			while(rs.next()){
//				isPaid = uF.parseToBoolean(rs.getString("is_paid"));
//				nLeaveTypeId = uF.parseToInt(rs.getString("leave_type_id"));
//				dblLeaveNo = uF.parseToDouble(rs.getString("leave_no"));
//				nLeaveId = uF.parseToInt(rs.getString("leave_id"));
//				currDate=rs.getString("_date");
//				empId=rs.getString("emp_id");
//			}
//			
//			pst = con.prepareStatement("update leave_application_register set is_modify = true, modify_date=?, modify_by=? where leave_register_id  = ?");
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
//			pst.setInt(3, uF.parseToInt(getLeaveRegisterId()));
//			pst.execute();
//			
//			pst = con.prepareStatement("update emp_leave_entry set is_modify = true, modify_date=?, modify_by=? where leave_id  = ?");
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
//			pst.setInt(3, nLeaveId);
//			pst.execute();
//					
//
//			request.setAttribute("STATUS_MSG", "Cancelled");
//					
//		} catch (Exception e) {
//			e.printStackTrace(); 
//		}finally{
//			db.closeConnection(con);
//		}
//	}
	
//	private void modifyLeaveDetails(UtilityFunctions uF) {
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//		try {
//			con=db.makeConnection(con);
//			
//			pst = con.prepareStatement("select * from leave_application_register where leave_register_id  = ?");
//			pst.setInt(1, uF.parseToInt(getLeaveRegisterId()));
//			rs = pst.executeQuery();
//			boolean isPaid = false;
//			int nLeaveTypeId = 0;
//			int nLeaveId = 0;
//			double dblLeaveNo = 0;
//			String currDate=null;
//			String empId=null;
//			while(rs.next()){
//				isPaid = uF.parseToBoolean(rs.getString("is_paid"));
//				nLeaveTypeId = uF.parseToInt(rs.getString("leave_type_id"));
//				dblLeaveNo = uF.parseToDouble(rs.getString("leave_no"));
//				nLeaveId = uF.parseToInt(rs.getString("leave_id"));
//				currDate=rs.getString("_date");
//				empId=rs.getString("emp_id");
//			}
//			
//			pst = con.prepareStatement("select * from emp_leave_type where leave_type_id = ? and level_id=(select a.level_id from level_details ld right join (select * from designation_details dd right join (select *, gd.designation_id as designationid from employee_official_details eod, grades_details gd where emp_id=? and   gd.grade_id=eod.grade_id) a on a.designationid=dd.designation_id) a on a.level_id=ld.level_id)");
//			pst.setInt(2,uF.parseToInt(empId));
//			pst.setInt(1,nLeaveTypeId);
//			rs = pst.executeQuery();
//			List<String> prefix=new ArrayList<String>();
//			List<String> suffix=new ArrayList<String>();
////			Map<String,String> leaveTypeValid=new HashMap<String,String>();
//			while(rs.next()){
//				if(rs.getString("leave_suffix")!=null)
//				suffix=Arrays.asList(rs.getString("leave_suffix").split(","));
//				if(rs.getString("leave_prefix")!=null)
//				prefix=Arrays.asList( rs.getString("leave_prefix").split(","));
//			}
//			
//			pst=con.prepareStatement("select * from work_location_info where wlocation_id=(select wlocation_id from employee_official_details where emp_id=?)");
//			pst.setInt(1, uF.parseToInt(empId));
//			rs=pst.executeQuery();
//			
//			String weeklyoff1=null;
//			String weeklyoff2=null;
//			String weeklyoff3=null;
//			
//
//			while(rs.next()){
//				
//				weeklyoff1=rs.getString("wlocation_weeklyoff1");
//				weeklyoff2=rs.getString("wlocation_weeklyoff2");
//				weeklyoff3=rs.getString("wlocation_weeklyoff3");
//
//
////				flag=true;
//			}
//			
//			Calendar cal = GregorianCalendar.getInstance();
//			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(currDate, DBDATE, "dd")));
//			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(currDate, DBDATE, "MM")) - 1);
//			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(currDate, DBDATE, "yyyy")));
//			
//			Calendar cal1=(Calendar)cal.clone();
//			
//			while(true){
//				cal1.add(Calendar.DATE,-1);
//				String strDate=cal1.get(Calendar.DATE)+"/"+(cal1.get(Calendar.MONTH) + 1)+"/"+cal1.get(Calendar.YEAR);
//				java.sql.Date dtCurrent1 = uF.getDateFormat(cal1.get(Calendar.DATE)+"/"+(cal1.get(Calendar.MONTH) + 1)+"/"+cal1.get(Calendar.YEAR), DATE_FORMAT);
//				String day=uF.getDateFormat(strDate, DATE_FORMAT, "EEEE");
//				
//			if(!prefix.contains("-1")){
//				boolean flag=false;
//				
//				
//				if(weeklyoff1!=null && weeklyoff1.equalsIgnoreCase(day)){
//					flag=true;
//				}
//				if(weeklyoff2!=null && weeklyoff2.equalsIgnoreCase(day)){
//					
//					flag=true;
//				}
//				if(weeklyoff3!=null && weeklyoff3.equalsIgnoreCase(day)){
//					
//					flag=true;
//				}
//				
//				if(flag){
//					
//					if(isPaid){
//						String strDate1 = null;
//						double dblBalance = 0;
//						pst = con.prepareStatement("select * from leave_register1 where leave_type_id=? order by _date desc limit 1");
//						pst.setInt(1, nLeaveTypeId);
//						rs = pst.executeQuery();
//						while(rs.next()){
//							strDate1 = rs.getString("_date");
//							dblBalance = uF.parseToDouble(rs.getString("balance"));
//						}
//						
//						pst = con.prepareStatement("update leave_register1 set balance =? where leave_type_id=? and _date=? ");
//						if(uF.parseToBoolean(getIsCompensate())){
//							pst.setDouble(1, (dblBalance - dblLeaveNo));
//						}else{
//							pst.setDouble(1, (dblBalance + dblLeaveNo));
//						}
//						pst.setInt(2, nLeaveTypeId);
//						pst.setDate(3, uF.getDateFormat(strDate1, DBDATE));
//						pst.execute();
//					}
//					
//					
//					pst = con.prepareStatement("update leave_application_register set is_modify = true, modify_date=?, modify_by=? where " +
//							"_date  = ? and emp_id=?");
//					pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
//					pst.setDate(3, dtCurrent1);
//					pst.setInt(4, uF.parseToInt(empId));
//					pst.executeUpdate();
//				}else{
//					break;
//				}
//			}else{
//				break;
//			}
//			}
//			
//			
//			
//			 cal1=(Calendar)cal.clone();
//			while(true){
//				cal1.add(Calendar.DATE,-1);
//				java.sql.Date dtCurrent1 = uF.getDateFormat(cal1.get(Calendar.DATE)+"/"+(cal1.get(Calendar.MONTH) + 1)+"/"+cal1.get(Calendar.YEAR), DATE_FORMAT);
//
//			if(!prefix.contains("-2")){
////				Date dtCurrent1 = uF.getDateFormat((cal.get(Calendar.DATE)+1)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT);
//				pst=con.prepareStatement("select * from holidays where _date=?");
//				pst.setDate(1, dtCurrent1);
//				
//				rs=pst.executeQuery();
//				boolean flag=false;
//				while(rs.next()){
//					flag=true;
//				}
//				
//				pst=con.prepareStatement("select * from leave_application_register where _date=?  and emp_id=?");
//				
//				pst.setDate(1, dtCurrent1);
//				pst.setInt(2, uF.parseToInt(empId));
//				rs=pst.executeQuery();
//				while(rs.next()){
//					flag=false;
//				}
//				if(flag){
//					
//					if(isPaid){
//						String strDate1 = null;
//						double dblBalance = 0;
//						pst = con.prepareStatement("select * from leave_register1 where leave_type_id=? order by _date desc limit 1");
//						pst.setInt(1, nLeaveTypeId);
//						rs = pst.executeQuery();
//						while(rs.next()){
//							strDate1 = rs.getString("_date");
//							dblBalance = uF.parseToDouble(rs.getString("balance"));
//						}
//						
//						pst = con.prepareStatement("update leave_register1 set balance =? where leave_type_id=? and _date=? ");
//						if(uF.parseToBoolean(getIsCompensate())){
//							pst.setDouble(1, (dblBalance - dblLeaveNo));
//						}else{
//							pst.setDouble(1, (dblBalance + dblLeaveNo));
//						}
//						pst.setInt(2, nLeaveTypeId);
//						pst.setDate(3, uF.getDateFormat(strDate1, DBDATE));
//						pst.execute();
//
//					}
//					
//					pst = con.prepareStatement("update leave_application_register set is_modify = true, modify_date=?, modify_by=? where " +
//					"_date  = ? and emp_id=?");
//					pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
//					pst.setDate(3, dtCurrent1);
//					pst.setInt(4, uF.parseToInt(empId));
//					pst.executeUpdate();
//				}else{
//					break;
//				}
//			}else{
//				break;
//			}
//			
//			}
//			
//			
//			
//			 cal1=(Calendar)cal.clone();
//				while(true){
//					cal1.add(Calendar.DATE,1);
//					String strDate=cal1.get(Calendar.DATE)+"/"+(cal1.get(Calendar.MONTH) + 1)+"/"+cal1.get(Calendar.YEAR);
//					java.sql.Date dtCurrent1 = uF.getDateFormat(cal1.get(Calendar.DATE)+"/"+(cal1.get(Calendar.MONTH) + 1)+"/"+cal1.get(Calendar.YEAR), DATE_FORMAT);
//					String day=uF.getDateFormat(strDate, DATE_FORMAT, "EEEE");
//					
//				if(!suffix.contains("-1")){
//					boolean flag=false;
//					
//					
//					if(weeklyoff1!=null && weeklyoff1.equalsIgnoreCase(day)){
//						
//						flag=true;
//					}
//					if(weeklyoff2!=null && weeklyoff2.equalsIgnoreCase(day)){
//						
//						flag=true;
//					}
//					if(weeklyoff3!=null && weeklyoff3.equalsIgnoreCase(day)){
//						
//						flag=true;
//					}
//					
//					if(flag){
//						
//						if(isPaid){
//							String strDate1 = null;
//							double dblBalance = 0;
//							pst = con.prepareStatement("select * from leave_register1 where leave_type_id=? order by _date desc limit 1");
//							pst.setInt(1, nLeaveTypeId);
//							rs = pst.executeQuery();
//							while(rs.next()){
//								strDate1 = rs.getString("_date");
//								dblBalance = uF.parseToDouble(rs.getString("balance"));
//							}
//							
//							pst = con.prepareStatement("update leave_register1 set balance =? where leave_type_id=? and _date=? ");
//							if(uF.parseToBoolean(getIsCompensate())){
//								pst.setDouble(1, (dblBalance - dblLeaveNo));
//							}else{
//								pst.setDouble(1, (dblBalance + dblLeaveNo));
//							}
//							pst.setInt(2, nLeaveTypeId);
//							pst.setDate(3, uF.getDateFormat(strDate1, DBDATE));
//							pst.execute();
//
//						}
//						
//						pst = con.prepareStatement("update leave_application_register set is_modify = true, modify_date=?, modify_by=? where " +
//						"_date  = ? and emp_id=?");
//						pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//						pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
//						pst.setDate(3, dtCurrent1);
//						pst.setInt(4, uF.parseToInt(empId));
//						pst.executeUpdate();
//					}else{
//						break;
//					}
//				}else{
//					break;
//				}
//				}
//				
//				
//				
//				 cal1=(Calendar)cal.clone();
//				while(true){
//					cal1.add(Calendar.DATE,1);
//					java.sql.Date dtCurrent1 = uF.getDateFormat(cal1.get(Calendar.DATE)+"/"+(cal1.get(Calendar.MONTH) + 1)+"/"+cal1.get(Calendar.YEAR), DATE_FORMAT);
//
//				if(!suffix.contains("-2")){
////					Date dtCurrent1 = uF.getDateFormat((cal.get(Calendar.DATE)+1)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT);
//					pst=con.prepareStatement("select * from holidays where _date=?");
//					pst.setDate(1, dtCurrent1);
//					rs=pst.executeQuery();
//					boolean flag=false;
//					while(rs.next()){
//						flag=true;
//					}
//					pst=con.prepareStatement("select * from leave_application_register where _date=?  and emp_id=?");
//					
//					pst.setDate(1, dtCurrent1);
//					pst.setInt(2, uF.parseToInt(empId));
//					rs=pst.executeQuery();
//					while(rs.next()){
//						flag=false;
//					}
//					if(flag){
//						
//						if(isPaid){
//							String strDate1 = null;
//							double dblBalance = 0;
//							pst = con.prepareStatement("select * from leave_register1 where leave_type_id=? order by _date desc limit 1");
//							pst.setInt(1, nLeaveTypeId);
//							rs = pst.executeQuery();
//							while(rs.next()){
//								strDate1 = rs.getString("_date");
//								dblBalance = uF.parseToDouble(rs.getString("balance"));
//							}
//							
//							pst = con.prepareStatement("update leave_register1 set balance =? where leave_type_id=? and _date=? ");
//							if(uF.parseToBoolean(getIsCompensate())){
//								pst.setDouble(1, (dblBalance - dblLeaveNo));
//							}else{
//								pst.setDouble(1, (dblBalance + dblLeaveNo));
//							}
//							pst.setInt(2, nLeaveTypeId);
//							pst.setDate(3, uF.getDateFormat(strDate1, DBDATE));
//							pst.execute();
//
//						}
//						
//						pst = con.prepareStatement("update leave_application_register set is_modify = true, modify_date=?, modify_by=? where " +
//						"_date  = ? and emp_id=?");
//						pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//						pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
//						pst.setDate(3, dtCurrent1);
//						pst.setInt(4, uF.parseToInt(empId));
//						pst.executeUpdate();
//					}else{
//						break;
//					}
//				}else{
//					break;
//				}
//				
//				}
//			
//			
//			
//			
//			
//			pst = con.prepareStatement("update leave_application_register set is_modify = true, modify_date=?, modify_by=? where leave_register_id  = ?");
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
//			pst.setInt(3, uF.parseToInt(getLeaveRegisterId()));
//			pst.execute();
//			
//			pst = con.prepareStatement("update emp_leave_entry set is_modify = true, modify_date=?, modify_by=? where leave_id  = ?");
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
//			pst.setInt(3, nLeaveId);
//			pst.execute();
//			
//			
//			
//			
//			
//			
//			if(isPaid){
//				String strDate = null;
//				double dblBalance = 0;
//				pst = con.prepareStatement("select * from leave_register1 where leave_type_id=? order by _date desc limit 1");
//				pst.setInt(1, nLeaveTypeId);
//				rs = pst.executeQuery();
//				while(rs.next()){
//					strDate = rs.getString("_date");
//					dblBalance = uF.parseToDouble(rs.getString("balance"));
//				}
//				
//				pst = con.prepareStatement("update leave_register1 set balance =? where leave_type_id=? and _date=? ");
//				if(uF.parseToBoolean(getIsCompensate())){
//					pst.setDouble(1, (dblBalance - dblLeaveNo));
//				}else{
//					pst.setDouble(1, (dblBalance + dblLeaveNo));
//				}
//				pst.setInt(2, nLeaveTypeId);
//				pst.setDate(3, uF.getDateFormat(strDate, DBDATE));
//				pst.execute();
//
//			}
//
//			request.setAttribute("STATUS_MSG", "Cancelled");
//					
//		} catch (Exception e) {
//			e.printStackTrace(); 
//		}finally{
//			db.closeConnection(con);
//		}
//	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}
	public String getLeaveId() {
		return leaveId;
	}
	public void setLeaveId(String leaveId) {
		this.leaveId = leaveId;
	}


	public String getModify() {
		return modify;
	}


	public void setModify(String modify) {
		this.modify = modify;
	}


	public String getLeaveRegisterId() {
		return leaveRegisterId;
	}


	public void setLeaveRegisterId(String leaveRegisterId) {
		this.leaveRegisterId = leaveRegisterId;
	}


	public String getLeaveTypeId() {
		return leaveTypeId;
	}


	public void setLeaveTypeId(String leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}


	public String getIsCompensate() {
		return isCompensate;
	}


	public void setIsCompensate(String isCompensate) {
		this.isCompensate = isCompensate;
	}
	
	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}