//package com.konnect.jpms.payroll;
//
//import java.sql.Connection;
//
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.GregorianCalendar;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.TimeZone;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//import com.konnect.jpms.select.FillEmploymentType;
//import com.konnect.jpms.select.FillLeaveType;
//import com.konnect.jpms.util.CommonFunctions;
//import com.konnect.jpms.util.Database;
//import com.konnect.jpms.util.IStatements;
//import com.konnect.jpms.util.UtilityFunctions;
//
//import org.apache.commons.lang.StringUtils;
//import org.apache.poi.ss.formula.IStabilityClassifier;
//import org.apache.struts2.interceptor.ServletRequestAware;
//import org.apache.struts2.interceptor.ServletResponseAware;
//import com.konnect.jpms.util.IDBConstant;
//import com.opensymphony.xwork2.ActionSupport;
//import com.konnect.jpms.payroll.ProccessingDashboard_1;
//
//public class PayrollDashboard_2 extends ActionSupport implements ServletRequestAware,ServletResponseAware,IDBConstant,IStatements
//{
//	
// public HttpSession session; 
// private HttpServletRequest request;
// HttpServletResponse response;
// CommonFunctions CF = null;
// 
// Connection con;
// 
// String strUserType = null;
// String strPaycycleDuration;
// String f_paymentMode;
// String currUserType;
// String strpaycycle1;
//
// String f_org;	
// String strLocation;
// 
// String strUserTypeId = null; 
// String strBaseUserType = null;
// String strBaseUserTypeId = null;
// String strSessionEmpId = null; 
// String strReqServiceId = null;
// 
// String[] arrOfStrpaycycle;
// String[] f_grade;
// String[] f_strWLocation; 
// String[] f_level;
// String[] f_department;
// String[] f_service;
// String[] f_employeType;
// String[] bankBranch;
// 
// private String strSelectedEmpId;
// String strDepartment;
// String strSbu;
// String strLevel;
// String strGrade;
// String strEmployeType;
// String strStartDate;
// String strEndDate;
//
//	List<FillEmploymentType> employementTypeList;
//	List<FillLeaveType> leaveTypeList;
//
//	public String execute() throws Exception
//	{
//		System.out.println("hii im in execute method of payroll dashboard_2");
//		session = request.getSession(); 
//		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
//		if(CF==null)return LOGIN;
//		
//		strUserType = (String) session.getAttribute(USERTYPE);
//		System.out.println("strUserType in dashboard_2"+strUserType);
//		request.setAttribute(PAGE, PayrollDashboard_2);
//		request.setAttribute(TITLE, PPayroll_Dashboard);
//		
//		if(strUserType==null || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(ACCOUNTANT)
//			&& !strUserType.equalsIgnoreCase(MANAGER) && !strUserType.equalsIgnoreCase(HRMANAGER) && !strUserType.equalsIgnoreCase(CEO))) {
//			
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied); 
//			return ACCESS_DENIED;
//		}
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
//		sbpageTitleNaviTrail.append("<li class=\"active\">Payroll Dashboard</li>");
//		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
//		
//		if(getF_org()==null || getF_org().trim().equals("")){
//			setF_org((String)session.getAttribute(ORGID));
//		}
//		
//		if(getStrLocation() != null && !getStrLocation().equals("")) {
//			setF_strWLocation(getStrLocation().split(","));
//		} else {
//			setF_strWLocation(null);
//		}
//		
//		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
//			setF_department(getStrDepartment().split(","));
//		} else {
//			setF_department(null);
//		}
//		
//		if(getStrLevel() != null && !getStrLevel().equals("")) {
//			setF_level(getStrLevel().split(","));
//		} else {
//			setF_level(null);
//		}
//		
//		if(getStrGrade() != null && !getStrGrade().equals("")) {
//			setF_grade(getStrGrade().split(","));
//		} else {
//			setF_grade(null);
//		}
//		
//		if((getCurrUserType()==null || getCurrUserType().equalsIgnoreCase("NULL")) && strUserType != null && strUserType.equals(MANAGER)) {
//			setCurrUserType("MYTEAM");
//		}
//		
//		System.out.println("strpaycycle1"+strpaycycle1);
//		if(strpaycycle1!=null)
//		{ 
//	      arrOfStrpaycycle = strpaycycle1.split("-"); 
//	  
//	        for (String a : arrOfStrpaycycle) 
//	            System.out.println(a); 
//		}
//		
//		 System.out.println("arrOfStrpaycycle[0]"+arrOfStrpaycycle[0]);
//	     System.out.println("arrOfStrpaycycle[1]"+arrOfStrpaycycle[1]);
//	     System.out.println("arrOfStrpaycycle[2]"+arrOfStrpaycycle[2]);
//	      strStartDate=arrOfStrpaycycle[0];
//	      strEndDate=arrOfStrpaycycle[1];
//
//	     ProccessingDashboard_1 prc=new ProccessingDashboard_1();
//	     
//		List<String> alList= getLeaveDataCountMonthwise(uF, arrOfStrpaycycle[0], arrOfStrpaycycle[1]);
//		request.setAttribute("alList", alList);		
//		request.setAttribute("strpaycycle1", strpaycycle1);
//		
//		List<String> alList_LeaveApprove= getAttendanceStatusWithCount(uF, arrOfStrpaycycle[0], arrOfStrpaycycle[1]);
//		request.setAttribute("alList_LeaveApprove", alList_LeaveApprove);
//		
//		List<String> alList_ApprovePay = getApprovedEmpCount(uF, arrOfStrpaycycle[0], arrOfStrpaycycle[1]);
//		request.setAttribute("alList_ApprovePay", alList_ApprovePay);
//		
//		System.out.println("alList_ApprovePay"+alList_ApprovePay);
//		
//		List<String> alList_ApprovePayroll = getPaidAndUnpaidEmpCount(uF, arrOfStrpaycycle[0], arrOfStrpaycycle[1], arrOfStrpaycycle[2]);
//		request.setAttribute("alList_ApprovePayroll", alList_ApprovePayroll);
//		
//		System.out.println("alList_ApprovePayroll"+alList_ApprovePayroll);
//		
////getExceptionCount(Connection con, HttpServletRequest request, CommonFunctions CF, UtilityFunctions uF, String empType, String empId,HttpSession session)
//		int nExceptionCount = CF.getExceptionCount(con,request,CF,uF,strUserType,strUserTypeId,session);
//		request.setAttribute("exceptionCount",""+nExceptionCount);	
//		
//		
//		int count_array[]=getOvertimeCount(uF);
//		int Overtime_approvedCount=count_array[0];
//		int Overtime_unApprovedCount=count_array[1];
//		request.setAttribute("Overtime_approvedCount",""+Overtime_approvedCount);
//		request.setAttribute("Overtime_unApprovedCount",""+Overtime_unApprovedCount);
//		System.out.println("Overtime_approvedCount==="+Overtime_approvedCount+"Overtime_unApprovedCount==="+Overtime_unApprovedCount);
//		
//		return SUCCESS;
//	}
//	
//	public int[] getOvertimeCount(UtilityFunctions uF)
//	{
//		int count_array[]=new int[2];
//		Connection con=null;
//		PreparedStatement pst=null;
//		ResultSet rs=null;
//		Database db=new Database();
//		db.setRequest(request);
//		List<String> alApproveList = new ArrayList<String>();
//		List<String> alUnApproveList = new ArrayList<String>();
//		int unApprovedCount=0;
//		int approvedCount=0;
//
//		try
//		{
//			con=db.makeConnection(con);
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from overtime_individual_details where is_approved=1 and paid_from=? and paid_to=?");
//			pst = con.prepareStatement(sbQuery.toString());
//			
//			pst.setDate(1, uF.getDateFormat(strStartDate, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strEndDate, DATE_FORMAT));	
//			System.out.println("pst 1==="+pst);
//			rs=pst.executeQuery();
//			while(rs.next())
//			{
//				alApproveList.add(rs.getString("emp_id"));
//				
//				if(!alApproveList.contains(rs.getString("emp_id")))
//				{
//					alUnApproveList.add(rs.getString("emp_id"));
//				}
//			}
//			unApprovedCount=alUnApproveList.size();
//			System.out.println("unApprovedCount1===="+unApprovedCount);
//			rs.close();
//			pst.close();
//			
//			sbQuery = new StringBuilder();
//			sbQuery.append("select distinct emp_id from overtime_hours where  paycycle_from=? and paycycle_to=? ");
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getDateFormat(strStartDate, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strEndDate, DATE_FORMAT));	
//			System.out.println("pst 2==="+pst);
//			rs=pst.executeQuery();
//			while(rs.next())
//			{
//				if(!alApproveList.contains(rs.getString("emp_id")) && !alUnApproveList.add("emp_id"))
//				{
//					alUnApproveList.add(rs.getString("emp_id"));
//				}
//			}
//			unApprovedCount=alUnApproveList.size();
//			System.out.println("unApprovedCount===="+unApprovedCount);
//			approvedCount=alApproveList.size();
//			System.out.println("approvedCount===="+approvedCount);
//			
//			count_array[0]=approvedCount;
//			count_array[1]=unApprovedCount;
//			rs.close();
//			pst.close();
//			
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return count_array;
//	}
//		
//	
//	private List<String> getLeaveDataCountMonthwise(UtilityFunctions uF, String strStartDate, String strEndDate) {
//		System.out.println("hiii im in getLeaveDataCountMonthwise dashboard_2");
//		System.out.println("strStartDate-->"+strStartDate+"  strEndDate-->"+strEndDate);
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		List<String> leaveCountList = new ArrayList<String>();
//		
//		
//		for(int i=0;i<leaveCountList.size();i++)
//		{
//			System.out.println("leaveCountList:--->"+leaveCountList.get(i));
//		}
//		
//		try {
//			con = db.makeConnection(con);
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select e.emp_id from (select * from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0" +
//				" and is_approved > -2 and encashment_status=false ");
//			if((strStartDate!=null && !strStartDate.equals(""))  && (strEndDate!=null && !strEndDate.equals(""))) {
//				sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(strStartDate, DATE_FORMAT, DBDATE)+"' ");
//				sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE)+"'  ");	
//			}
//			sbQuery.append(" and ele.emp_id in(select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id ");
//			 if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0) {
//	            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
//	            } else {
//	            	 if(getF_level()!=null && getF_level().length>0) {
//	                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
//	                 }
//	            	 if(getF_grade()!=null && getF_grade().length>0) {
//	                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
//	                 }
//				}
//            if(getF_department()!=null && getF_department().length>0) {
//                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
//            }
//            if (getF_employeType() != null && getF_employeType().length > 0) {
//				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
//            }
//            if(getF_service()!=null && getF_service().length>0) {
//                sbQuery.append(" and (");
//                for(int i=0; i<getF_service().length; i++) {
//                	 sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
//                    
//                    if(i<getF_service().length-1) {
//                        sbQuery.append(" OR "); 
//                    }
//                }
//                sbQuery.append(" ) ");
//            } 
//            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
//                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//			}
//            
//            if(uF.parseToInt(getF_org())>0) {
//				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
//			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
//				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//			}
//			sbQuery.append(")) e, work_flow_details wfd where e.leave_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_LEAVE+"' ");
//			if(strUserType != null && !strUserType.equals(ADMIN)) {
//				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
//				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
//					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
//				} else {
//					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
//				}
//			}
//			
//			sbQuery.append(" order by e.entrydate desc");
//			pst = con.prepareStatement(sbQuery.toString());
//		//	System.out.println("pst====>"+pst);
//			rs=pst.executeQuery();
//			List<String> alEmp = new ArrayList<String>();
//			while(rs.next()) {
//				if(!alEmp.contains(rs.getString("emp_id"))) {
//					alEmp.add(rs.getString("emp_id"));
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			if(alEmp!=null && alEmp.size() > 0) {
//				String strEmpIds = StringUtils.join(alEmp.toArray(),",");
//				
//				System.out.println("strempid--->"+strEmpIds);
//				
//				Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
//				if(hmUserTypeIdMap == null) hmUserTypeIdMap = new HashMap<String, String>();
//				
//				Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
//				
//				for(Map.Entry<String, String>itr:hmEmployeeNameMap.entrySet())
//					System.out.println("key--->: "+itr.getKey()+"value--->: "+itr.getValue());
//				
//				sbQuery=new StringBuilder();
//				sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where effective_type='"+WORK_FLOW_LEAVE+"'" +
//					" and effective_id in (select ele.leave_id from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id " +
//					"and ele.leave_type_id > 0 and ele.emp_id in("+strEmpIds+") ");
//				if((strStartDate!=null && !strStartDate.equals(""))  && (strEndDate!=null && !strEndDate.equals(""))) {
//					sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(strStartDate, DATE_FORMAT, DBDATE)+"' ");
//					sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE)+"'  ");	
//				}
//				sbQuery.append(")");
//				if(strUserType != null && strUserType.equals(ADMIN)) {
//					sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
//				} else {
//					sbQuery.append(" and user_type_id=? ");
//				}
//				sbQuery.append(" order by effective_id,member_position");
//				pst = con.prepareStatement(sbQuery.toString());
//				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
//					pst.setInt(1, uF.parseToInt(strBaseUserTypeId));
//				} else {
//					pst.setInt(1, uF.parseToInt(strUserTypeId));
//				}
//				if(strUserType != null && strUserType.equals(ADMIN)) {
//					pst.setInt(2, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
//				}
//				rs = pst.executeQuery();			
//				Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();
//				Map<String, List<String>> hmCheckEmpUserType = new HashMap<String, List<String>>();
//				while(rs.next()) {
//					List<String> checkEmpList = hmCheckEmp.get(rs.getString("effective_id"));
//					if(checkEmpList == null)checkEmpList = new ArrayList<String>();				
//					checkEmpList.add(rs.getString("emp_id"));
//					
//					List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("effective_id")+"_"+rs.getString("emp_id"));
//					if(checkEmpUserTypeList == null)checkEmpUserTypeList = new ArrayList<String>();				
//					checkEmpUserTypeList.add(rs.getString("user_type_id"));
//					
//					hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
//					hmCheckEmpUserType.put(rs.getString("effective_id")+"_"+rs.getString("emp_id"), checkEmpUserTypeList);
//				}
//				rs.close();
//				pst.close();
//				
//				
//				List<String> alList = new ArrayList<String>();	
//				sbQuery=new StringBuilder();
//				sbQuery.append("select e.*,wfd.user_type_id as user_type from (select * from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0" +
//					" and is_approved > -2 and encashment_status=false and ele.emp_id in("+strEmpIds+")  ");
//				if((strStartDate!=null && !strStartDate.equals(""))  && (strEndDate!=null && !strEndDate.equals(""))) {
//					sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(strStartDate, DATE_FORMAT, DBDATE)+"' ");
//					sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE)+"'  ");	
//				}
////				if(uF.parseToInt(getLeaveStatus())==1) { 
////					sbQuery.append(" and is_approved=1");
////				} else if(uF.parseToInt(getLeaveStatus())==2) {
////					sbQuery.append(" and is_approved=0");
////				} else if(uF.parseToInt(getLeaveStatus())==3) {
////					sbQuery.append(" and is_approved=-1");
////				}
//				sbQuery.append(") e, work_flow_details wfd where e.leave_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_LEAVE+"' ");
//				if(strUserType != null && !strUserType.equals(ADMIN)) {
//					sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
//					if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
//						sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
//					} else {
//						sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
//					}
//				}
//				sbQuery.append(" order by e.entrydate desc");
//				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst====>"+pst);
//				int totAppliedLeave = 0;
//				int totApproveDenyLeave = 0;
//				rs=pst.executeQuery();
//				while(rs.next()) {
//	
//					List<String> checkEmpList = hmCheckEmp.get(rs.getString("leave_id"));
//					if (checkEmpList == null) checkEmpList = new ArrayList<String>();
//	
//					List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("leave_id") + "_" + strSessionEmpId);
//					if (checkEmpUserTypeList == null) checkEmpUserTypeList = new ArrayList<String>();
//	
//					if (!checkEmpList.contains(strSessionEmpId) && strUserType != null && !strUserType.equalsIgnoreCase(ADMIN)) {
//						continue;
//					}
//	
//					String userType = rs.getString("user_type");
//					if (!checkEmpList.contains(strSessionEmpId) && strUserType != null && strUserType.equalsIgnoreCase(ADMIN) && alList.contains(rs.getString("leave_id"))) {
//						continue;
//					} else if (!checkEmpList.contains(strSessionEmpId) && strUserType != null && strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("leave_id"))) {
//						alList.add(rs.getString("leave_id"));
//					} else if (!checkEmpUserTypeList.contains(userType)) {
//						continue;
//					}
//	
//					List<String> alInner = new ArrayList<String>();
//					alInner.add(uF.showData(hmEmployeeNameMap.get(rs.getString("emp_id")), ""));
//	
//					if (rs.getInt("is_approved") == 0) {
//						
//					} else if (rs.getInt("is_approved") == 1) {
//						totApproveDenyLeave++;
//					} else {
//						totApproveDenyLeave++;
//					}
//					totAppliedLeave++;
//				}
//				rs.close();
//				pst.close();
//				
//				leaveCountList.add(totAppliedLeave+"");
//				leaveCountList.add(totApproveDenyLeave+"");
//			}
//			if(leaveCountList.size() == 0) {
//				leaveCountList.add("0");
//				leaveCountList.add("0");
//			}
//			
//			System.out.println(strStartDate+" :: "+strEndDate+" -- leaveCountList in payroll dashboard_2 ===>> " + leaveCountList);
//			
////			request.setAttribute("leaveCountList", leaveCountList);
//		} catch (Exception e) {
//			e.printStackTrace(); 
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		
//		return leaveCountList;
//	}
//	
//	
//	
//	
//	
//	
//	private List<String> getAttendanceStatusWithCount(UtilityFunctions uF, String strD1, String strD2) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		List<String> alAttendaneStatus = new ArrayList<String>();
//		try {
//			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
//			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
//			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM")) - 1);
//			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
//			int nTotalNumberOfDays = cal.getActualMaximum(Calendar.DATE);
//			List<String> alActualDates = new ArrayList<String>();
//			for (int i = 0; i < nTotalNumberOfDays; i++) {
//				String strDate = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/"
//						+ (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
//				alActualDates.add(uF.getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT));
//				cal.add(Calendar.DATE, 1);
//			}
//			// System.out.println("alActualDates==>"+alActualDates);
//			con = db.makeConnection(con);
//
//			String strFinancialYearEnd = null;
//			String strFinancialYearStart = null;
//			String[] strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
//			if (strFinancialYear != null) {
//				strFinancialYearStart = strFinancialYear[0];
//				strFinancialYearEnd = strFinancialYear[1];
//			}
//
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select count(*) as emp_ids from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " + // and epd.is_alive=true
//				"and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) and emp_per_id > 0 ");
//			if (getF_employeType() != null && getF_employeType().length > 0) {
//				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
//			}
//			if (getF_level() != null && getF_level().length > 0) {
//				sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( " + StringUtils.join(getF_level(), ",") + ") ) ");
//			}
//			if (getF_grade() != null && getF_grade().length > 0) {
//				sbQuery.append(" and grade_id in (" + StringUtils.join(getF_grade(), ",") + " ) ");
//			}
//			if (getF_department() != null && getF_department().length > 0) {
//				sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
//			}
//
//			if (getF_service() != null && getF_service().length > 0) {
//				sbQuery.append(" and (");
//				for (int i = 0; i < getF_service().length; i++) {
//					sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
//					if (i < getF_service().length - 1) {
//						sbQuery.append(" OR ");
//					}
//				}
//				sbQuery.append(" ) ");
//			}
//
//			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
//				sbQuery.append(" and wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
//			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS) != null) {
//				sbQuery.append(" and wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
//			}
//
//			if (uF.parseToInt(getF_org()) > 0) {
//				sbQuery.append(" and org_id = " + uF.parseToInt(getF_org()));
//			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS) != null) {
//				sbQuery.append(" and org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
//			}
//			sbQuery.append(" and emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
//					+ "and paid_from = ? and paid_to=? group by emp_id) and emp_id not in (select emp_id from approve_attendance where "
//					+ "approve_from=? and approve_to=?)");
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
//			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
//			pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
//			pst.setDate(9, uF.getDateFormat(strD1, DATE_FORMAT));
//			pst.setDate(10, uF.getDateFormat(strD2, DATE_FORMAT));
//			// System.out.println("pst=====>"+pst);
//			rs = pst.executeQuery();
//			String strPendingEmpCount = "0";
//			while (rs.next()) {
//				strPendingEmpCount = rs.getString("emp_ids");
//			}
//			rs.close();
//			pst.close();
//
//			sbQuery = new StringBuilder();
//			sbQuery.append("select count(*) as emp_ids from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " + // and epd.is_alive=true
//				"and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) and emp_per_id > 0 ");
//			if (getF_employeType() != null && getF_employeType().length > 0) {
//				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
//			}
//			if (getF_level() != null && getF_level().length > 0) {
//				sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "
//				+ StringUtils.join(getF_level(), ",") + ") ) ");
//			}
//			if (getF_grade() != null && getF_grade().length > 0) {
//				sbQuery.append(" and grade_id in (" + StringUtils.join(getF_grade(), ",") + " ) ");
//			}
//			if (getF_department() != null && getF_department().length > 0) {
//				sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
//			}
//			if (getF_service() != null && getF_service().length > 0) {
//				sbQuery.append(" and (");
//				for (int i = 0; i < getF_service().length; i++) {
//					sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
//					if (i < getF_service().length - 1) {
//						sbQuery.append(" OR ");
//					}
//				}
//				sbQuery.append(" ) ");
//			}
//
//			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
//				sbQuery.append(" and wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
//			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS) != null) {
//				sbQuery.append(" and wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
//			}
//
//			if (uF.parseToInt(getF_org()) > 0) {
//				sbQuery.append(" and org_id = " + uF.parseToInt(getF_org()));
//			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS) != null) {
//				sbQuery.append(" and org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
//			}
//			sbQuery.append(" and (emp_id in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
//				+ "and paid_from = ? and paid_to=? group by emp_id) or emp_id in (select emp_id from approve_attendance where approve_from=? and approve_to=?)) ");
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
//			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
//			pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
//			pst.setDate(9, uF.getDateFormat(strD1, DATE_FORMAT));
//			pst.setDate(10, uF.getDateFormat(strD2, DATE_FORMAT));
//			// System.out.println("pst=====>"+pst);
//			rs = pst.executeQuery();
//			String strApprovedEmpCount = "0";
//			while (rs.next()) {
//				strApprovedEmpCount = rs.getString("emp_ids");
//			}
//			rs.close();
//			pst.close();
//
//			alAttendaneStatus.add(strApprovedEmpCount);
//			alAttendaneStatus.add(strPendingEmpCount);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return alAttendaneStatus;
//	}
//
//	private List<String> getApprovedEmpCount(UtilityFunctions uF, String strD1, String strD2) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		List<String> alApprovePayStatus = new ArrayList<String>();
//		
//		try {
//			con = db.makeConnection(con);
//
//			String strFinancialYearEnd = null;
//			String strFinancialYearStart = null;
//			String[] strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
//			if (strFinancialYear != null) {
//				strFinancialYearStart = strFinancialYear[0];
//				strFinancialYearEnd = strFinancialYear[1];
//			}
//
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select count (*) as emp_ids from approve_attendance at, employee_personal_details epd, employee_official_details eod where at.emp_id > 0 "
//				+ "and epd.emp_per_id = eod.emp_id and epd.emp_per_id = at.emp_id and at.emp_id = eod.emp_id and at.approve_from>=? "
//				+ "and at.approve_to<=? and (epd.employment_end_date is null OR epd.employment_end_date >= ?) and epd.joining_date<=? ");
//	    	if(getF_level()!=null && getF_level().length>0) {
//	            sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
//	        }
//	    	if(getF_grade()!=null && getF_grade().length>0) {
//	            sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
//	        }
//			if (getF_employeType() != null && getF_employeType().length > 0) {
//				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "','") + "') ");
//			}
//			if (getF_department() != null && getF_department().length > 0) {
//				sbQuery.append(" and eod.depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
//			}
//
//			if (getF_service() != null && getF_service().length > 0) {
//				sbQuery.append(" and (");
//				for (int i = 0; i < getF_service().length; i++) {
//					sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
//					if (i < getF_service().length - 1) {
//						sbQuery.append(" OR ");
//					}
//				}
//				sbQuery.append(" ) ");
//			}
//			if (getStrPaycycleDuration() != null) {
//				sbQuery.append(" and eod.paycycle_duration ='" + getStrPaycycleDuration() + "'");
//			}
//			if (uF.parseToInt(getF_paymentMode()) > 0) {
//				sbQuery.append(" and eod.payment_mode =" + uF.parseToInt(getF_paymentMode()));
//			}
//			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
//				sbQuery.append(" and eod.wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
//			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS) != null) {
//				sbQuery.append(" and eod.wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
//			}
//			if (uF.parseToInt(getF_org()) > 0) {
//				sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
//			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS) != null) {
//				sbQuery.append(" and eod.org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
//			}
//			sbQuery.append(" and eod.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
//				+ "and paid_from = ? and paid_to=? group by emp_id)");
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
//			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
//			pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
//			rs = pst.executeQuery();
//			String strPendingEmpCount = "0";
//			while (rs.next()) {
//				strPendingEmpCount = rs.getString("emp_ids");
//			}
//			rs.close();
//			pst.close();
//
//			sbQuery = new StringBuilder();
//			sbQuery.append("select count (*) as emp_ids from approve_attendance at, employee_personal_details epd, employee_official_details eod where at.emp_id > 0 "
//				+ "and epd.emp_per_id = eod.emp_id and epd.emp_per_id = at.emp_id and at.emp_id = eod.emp_id and at.approve_from>=? "
//				+ "and at.approve_to<=? and (epd.employment_end_date is null OR epd.employment_end_date >= ?) and epd.joining_date<=? ");
//	    	if(getF_level()!=null && getF_level().length>0) {
//	            sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
//	        }
//	    	if(getF_grade()!=null && getF_grade().length>0) {
//	            sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
//	        }
//			if (getF_employeType() != null && getF_employeType().length > 0) {
//				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "','") + "') ");
//			}
//			if (getF_department() != null && getF_department().length > 0) {
//				sbQuery.append(" and eod.depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
//			}
//
//			if (getF_service() != null && getF_service().length > 0) {
//				sbQuery.append(" and (");
//				for (int i = 0; i < getF_service().length; i++) {
//					sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
//					if (i < getF_service().length - 1) {
//						sbQuery.append(" OR ");
//					}
//				}
//				sbQuery.append(" ) ");
//			}
//			if (getStrPaycycleDuration() != null) {
//				sbQuery.append(" and eod.paycycle_duration ='" + getStrPaycycleDuration() + "'");
//			}
//			if (uF.parseToInt(getF_paymentMode()) > 0) {
//				sbQuery.append(" and eod.payment_mode =" + uF.parseToInt(getF_paymentMode()));
//			}
//			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
//				sbQuery.append(" and eod.wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
//			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS) != null) {
//				sbQuery.append(" and eod.wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
//			}
//			if (uF.parseToInt(getF_org()) > 0) {
//				sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
//			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS) != null) {
//				sbQuery.append(" and eod.org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
//			}
//			sbQuery.append(" and eod.emp_id in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
//				+ "and paid_from = ? and paid_to=? group by emp_id)");
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
//			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
//			pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
////			System.out.println("pst ===>> " + pst);
//			rs = pst.executeQuery();
//			String strApprovedEmpCount = "0";
//			while (rs.next()) {
//				strApprovedEmpCount = rs.getString("emp_ids");
//			}
//			rs.close();
//			pst.close();
//			
//			alApprovePayStatus.add(strApprovedEmpCount);
//			alApprovePayStatus.add(strPendingEmpCount);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return alApprovePayStatus;
//	}	
//	//***************code for paypayroll_money************//
//
//	private List<String> getPaidAndUnpaidEmpCount(UtilityFunctions uF, String strD1, String strD2, String strPC) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		List<String> alPaidPayStatus = new ArrayList<String>();
//		
//		try {
//			
//			con = db.makeConnection(con);
//
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select count(distinct(eod.emp_id)) as emp_cnt,is_paid from payroll_generation pg, employee_official_details eod where pg.emp_id = eod.emp_id and paycycle=? and paid_from =? and paid_to=? ");
//	    	if(getF_level()!=null && getF_level().length>0) {
//	    		sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
//	        }
//	    	if(getF_grade()!=null && getF_grade().length>0) {
//	    		sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
//	        }
//			
//			if (getF_employeType() != null && getF_employeType().length > 0) {
//				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "','") + "') ");
//			}
//		
//			if (getF_department() != null && getF_department().length > 0) {
//				sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
//			}
//
//			if (getF_service() != null && getF_service().length > 0) {
//				sbQuery.append(" and (");
//				for (int i = 0; i < getF_service().length; i++) {
//					sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
//					if (i < getF_service().length - 1) {
//						sbQuery.append(" OR ");
//					}
//				}
//				sbQuery.append(" ) ");
//			}
//			if (getStrPaycycleDuration() != null) {
//				sbQuery.append(" and pay_mode ='" + getStrPaycycleDuration() + "'");
//			}
//			if (uF.parseToInt(getF_paymentMode()) > 0) {
//				sbQuery.append(" and pg.payment_mode =" + uF.parseToInt(getF_paymentMode()));
//			}
//			if (getBankBranch() != null && getBankBranch().length > 0) {
//				sbQuery.append(" and eod.emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
//					"and (CAST(epd.emp_bank_name AS integer) in ("+StringUtils.join(getBankBranch(), ",")+") or CAST (epd.emp_bank_name2 AS integer) in ("+StringUtils.join(getBankBranch(), ",")+")))");
//			}
//
//			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
//				sbQuery.append(" and eod.wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
//			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(WLOCATION_ACCESS) != null) {
//				sbQuery.append(" and eod.wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
//			}
//
//			if (uF.parseToInt(getF_org()) > 0) {
//				sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
//			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(ORG_ACCESS) != null) {
//				sbQuery.append(" and eod.org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
//			}
//			sbQuery.append(" group by is_paid");
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strPC));
//			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
////			System.out.println("pst1====>"+pst);
//			rs = pst.executeQuery();
//			String strUnpaidEmpCount = "0";
//			String strPaidEmpCount = "0";
//			while (rs.next()) {
//				if (rs.getBoolean("is_paid")) {
//					strPaidEmpCount = rs.getString("emp_cnt");
//				} else if (!rs.getBoolean("is_paid")) {
//					strUnpaidEmpCount = rs.getString("emp_cnt");
//				}
//			}
//			rs.close();
//			pst.close();
//			alPaidPayStatus.add(strPaidEmpCount);
//			alPaidPayStatus.add(strUnpaidEmpCount);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return alPaidPayStatus;
//	}	
//	
//	
//	
////*******************************Approve Exception counter**********************
//	
//	public String getStrPaycycleDuration() {
//		return strPaycycleDuration;
//	}
//
//	public void setStrPaycycleDuration(String strPaycycleDuration) {
//		this.strPaycycleDuration = strPaycycleDuration;
//	}
//
//	public String getF_paymentMode() {
//		return f_paymentMode;
//	}
//
//	public void setF_paymentMode(String f_paymentMode) {
//		this.f_paymentMode = f_paymentMode;
//	}
//
//	
//	public HttpSession getSession() {
//		return session;
//	}
//
//
//	public void setSession(HttpSession session) {
//		this.session = session;
//	}
//
//
//	public String getStrLocation() {
//		return strLocation;
//	}
//
//
//	public void setStrLocation(String strLocation) {
//		this.strLocation = strLocation;
//	}
//
//
//	public String getStrDepartment() {
//		return strDepartment;
//	}
//
//
//	public void setStrDepartment(String strDepartment) {
//		this.strDepartment = strDepartment;
//	}
//
//
//	public String getStrLevel() {
//		return strLevel;
//	}
//
//
//	public void setStrLevel(String strLevel) {
//		this.strLevel = strLevel;
//	}
//
//
//	public String getStrGrade() {
//		return strGrade;
//	}
//
//	public void setStrGrade(String strGrade) {
//		this.strGrade = strGrade;
//	}
//
//
//	public String getStrEmployeType() {
//		return strEmployeType;
//	}
//
//
//	public void setStrEmployeType(String strEmployeType) {
//		this.strEmployeType = strEmployeType;
//	}
//
//	
//	public String getF_org() {
//		return f_org;
//	}
//
//	public void setF_org(String f_org) {
//		this.f_org = f_org;
//	}
//	
//	public String getStrUserTypeId() {
//		return strUserTypeId;
//	}
//
//
//	public void setStrUserTypeId(String strUserTypeId) {
//		this.strUserTypeId = strUserTypeId;
//	}
//
//
//	public String getStrBaseUserType() {
//		return strBaseUserType;
//	}
//
//
//	public void setStrBaseUserType(String strBaseUserType) {
//		this.strBaseUserType = strBaseUserType;
//	}
//
//
//	public String getStrBaseUserTypeId() {
//		return strBaseUserTypeId;
//	}
//
//
//	public void setStrBaseUserTypeId(String strBaseUserTypeId) {
//		this.strBaseUserTypeId = strBaseUserTypeId;
//	}
//
//
//	public String getStrSessionEmpId() {
//		return strSessionEmpId;
//	}
//
//
//	public void setStrSessionEmpId(String strSessionEmpId) {
//		this.strSessionEmpId = strSessionEmpId;
//	}
//
//
//	public String getCurrUserType() {
//		return currUserType;
//	}
//
//
//	public void setCurrUserType(String currUserType) {
//		this.currUserType = currUserType;
//}
// 	public String getStrUserType() {
//		return strUserType;
//	}
//
//	public void setStrUserType(String strUserType) {
//		this.strUserType = strUserType;
//	}
//	
//	public String[] getF_strWLocation() {
//		return f_strWLocation;
//	}
//
//
//	public void setF_strWLocation(String[] f_strWLocation) {
//		this.f_strWLocation = f_strWLocation;
//	}
//
//
//	public String[] getF_level() {
//		return f_level;
//	}
//
//
//	public void setF_level(String[] f_level) {
//		this.f_level = f_level;
//	}
//
//
//	public String[] getF_department() {
//		return f_department;
//	}
//
//
//	public void setF_department(String[] f_department) {
//		this.f_department = f_department;
//	}
//
//
//	public String[] getF_service() {
//		return f_service;
//	}
//
//
//	public void setF_service(String[] f_service) {
//		this.f_service = f_service;
//	}
//
//
//	public String[] getF_employeType() {
//		return f_employeType;
//	}
//
//
//	public void setF_employeType(String[] f_employeType) {
//		this.f_employeType = f_employeType;
//	}
//
//	public String[] getF_grade() {
//	return f_grade;
//	}
//
//	public void setF_grade(String[] f_grade) {
//	this.f_grade = f_grade;
//	}
//	
//	@Override
//	public void setServletResponse(HttpServletResponse response) {
//		this.response=response;
//	}
//	
//	@Override
//	public void setServletRequest(HttpServletRequest request) {
//		
//		this.request=request;
//	}
//
//	 public String getStrpaycycle1() {
//			return strpaycycle1;
//	}
//
//		public void setStrpaycycle1(String strpaycycle1) {
//			this.strpaycycle1 = strpaycycle1;
//	}
//		
//	public String[] getBankBranch() {
//			return bankBranch;
//	}
//
//	public void setBankBranch(String[] bankBranch) {
//			this.bankBranch = bankBranch;
//	}
//	public String getStrSelectedEmpId() {
//		return strSelectedEmpId;
//	}
//
//	public void setStrSelectedEmpId(String strSelectedEmpId) {
//		this.strSelectedEmpId = strSelectedEmpId;
//	}
//	public String getStrStartDate() {
//		return strStartDate;
//	}
//
//	public void setStrStartDate(String strStartDate) {
//		this.strStartDate = strStartDate;
//	}
//
//	public String getStrEndDate() {
//		return strEndDate;
//	}
//
//	public void setStrEndDate(String strEndDate) {
//		this.strEndDate = strEndDate;
//	}
//}
