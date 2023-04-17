package com.konnect.jpms.reports;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ManagerLeaveApprovalReport extends ActionSupport implements ServletRequestAware, IStatements {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF=null;
	String strUserType = null;
	String strUserTypeId = null; 
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null;
	String strSessionOrgId = null;
	
	String leaveStatus;
	String strpaycycle1;

	String alertStatus;
	String alert_type;
	
	String strStartDate;
	String strEndDate;
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	String strGrade;
	String strEmployeType;
	String fromPage;
	String extraWorkingFromTime; 
	String extraWorkingToTime; 
	String f_org;
	String[] f_strWLocation; 
	String[] f_level;
	String[] f_department;
	String[] f_service;
	String[] f_employeType;
	String[] f_grade;
	
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillEmploymentType> employementTypeList;
	List<FillGrade> gradeList;
	List<FillPayCycles> paycycleList;
	
	String currUserType;
	String alertID;
	
	String strOrgId = null;  
	String strLocationId = null;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
			  
		UtilityFunctions uF = new UtilityFunctions(); 
		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionOrgId = (String) session.getAttribute(ORGID);
		
		strOrgId = (String)session.getAttribute(ORGID);
		strLocationId = (String)session.getAttribute(WLOCATIONID);
		
		request.setAttribute(PAGE, PManagerLeaveApprovalReport);
		request.setAttribute(TITLE, TViewManagerLeaveApproval);
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView) {
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
//		System.out.println("app leave strUserType====>"+strUserType);
		
		if(getStrpaycycle1() !=null && !getStrpaycycle1().equals("")) {
			String array_getStrpaycycle1[]=strpaycycle1.split("-");
			strStartDate=array_getStrpaycycle1[0];
			strEndDate=array_getStrpaycycle1[1];
			
		}
		
		if(getF_org()==null || getF_org().trim().equals("")) {
			setF_org((String)session.getAttribute(ORGID));
		}

		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			if(strUserType != null && strUserType.equalsIgnoreCase(OTHER_HR) && session.getAttribute(DEPARTMENTID) != null){
				String[] deptArr = {(String)session.getAttribute(DEPARTMENTID)};
				setF_department(deptArr);
			} else {
				setF_department(null);
			}
		}
		if(getStrSbu() != null && !getStrSbu().equals("")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		
		if(getF_level()!=null && getF_level().length > 0){
			String level_id ="";
			for (int i = 0; i < getF_level().length; i++) {
				if(i==0){
					level_id = getF_level()[i];
					level_id.concat(getF_level()[i]);
				}else{
					level_id =level_id+","+getF_level()[i];
				}
			}			
			gradeList = new FillGrade(request).fillGrade(level_id,getF_org());
		}else{
			gradeList = new FillGrade(request).fillGradebyorganisation(getF_org());
		}
		
		if(getStrGrade() != null && !getStrGrade().equals("")) {
			setF_grade(getStrGrade().split(","));
		} else {
			setF_grade(null);
		}
		
		if(getStrEmployeType() != null && !getStrEmployeType().equals("")) {
			setF_employeType(getStrEmployeType().split(","));
		} else {
			setF_employeType(null);
		}
		
		//System.out.println("getCurrUserType() ===>> " + getCurrUserType());
		if((getCurrUserType()==null || getCurrUserType().equalsIgnoreCase("NULL")) && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		
		if(getLeaveStatus()==null) {
			setLeaveStatus("2");
		}
		
		if((getStrStartDate() == null || getStrStartDate().trim().equals("") || getStrStartDate().trim().equalsIgnoreCase("NULL"))
				|| (getStrEndDate() == null || getStrEndDate().trim().equals("") || getStrEndDate().trim().equalsIgnoreCase("NULL"))) {
			setStrStartDate(null);
			setStrEndDate(null);
		} else {
			String strSDate = URLDecoder.decode(getStrStartDate());
			String strEDate = URLDecoder.decode(getStrEndDate());
			setStrStartDate(strSDate);
			setStrEndDate(strEDate);
		}
		
		if(getStrStartDate() == null || getStrEndDate() == null) {
			String[] arrDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
			if(arrDates != null) {
				String[] arrPrevDates =	getPrevPaycycle(uF, arrDates);
				setStrStartDate(arrPrevDates[0]);
				setStrEndDate(arrDates[1]);
			} else {
				setStrStartDate("");
				setStrEndDate("");
			}
		}
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		
//		if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(LEAVE_REQUEST_ALERT)) {
//			updateUserAlerts();
//		}
		paycycleList = new FillPayCycles("M", request).fillPayCycles(CF, getF_org());
		
		viewLeaveAppraoval1(uF);
		return loadManagerLeaveApproval(uF);

	}
	
	private String[] getPrevPaycycle(UtilityFunctions uF, String[] arrDates) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		
		String[] arrPrevDates = null;
		try {
			con = db.makeConnection(con);
			
			String strPrevDate = uF.getDateFormatUtil(uF.getPrevDate(uF.getDateFormatUtil(arrDates[1], DATE_FORMAT), 180), DATE_FORMAT);
			arrPrevDates = CF.getPayCycleFromDate(con, strPrevDate, CF.getStrTimeZone(), CF, getF_org());
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeConnection(con);
		}
		return arrPrevDates;
	}

//	private void updateUserAlerts() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
//		try {
//			con = db.makeConnection(con);
//			
//			String strDomain = request.getServerName().split("\\.")[0];
//			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//			userAlerts.setStrDomain(strDomain);
//			userAlerts.setStrEmpId(""+nEmpId);
//			userAlerts.set_type(LEAVE_REQUEST_ALERT);
//			userAlerts.setStatus(UPDATE_ALERT);
//			Thread t = new Thread(userAlerts);
//			t.run();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con); 
//		}
//	}
	 
	private void viewLeaveAppraoval1(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		
		try {
			int nCurrentYear = uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"));

			con=db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			
//			String[] strCurrPaycycle = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, getF_org());
//			String[] strPrevPaycycle = CF.getPrevPayCycle(strDate, strTimeZone, CF);
			//Start Dattatray Date :14-09-21
			Map<String, String> hmlevel = CF.getEmpLevelMap(con);
			if(hmlevel == null) hmlevel = new HashMap<String, String>();
			
			List<List<String>> alPaycycleList = new ArrayList<List<String>>();
			for (int i = 0; paycycleList != null && ((i < paycycleList.size() && paycycleList.size()>6 && i< 6) || (i < paycycleList.size() && paycycleList.size()<=6)); i++) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(paycycleList.get(i).getPaycycleId());
				innerList.add(paycycleList.get(i).getPaycycleName());
				String[] strTmp = paycycleList.get(i).getPaycycleId().split("-");
				
				List<String> alList = getLeaveDataCountMonthwise(uF, strTmp[0], strTmp[1]);
				
					innerList.add(alList.get(0));
					innerList.add(alList.get(1));
					alPaycycleList.add(innerList);
			}
			request.setAttribute("alPaycycleList", alPaycycleList);
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select e.emp_id from (select * from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0" +
				" and is_approved > -2 and encashment_status=false ");
			if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
				sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
				sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
			}
			if(uF.parseToInt(getLeaveStatus())==1) { 
				sbQuery.append(" and is_approved=1");
			} else if(uF.parseToInt(getLeaveStatus())==2) {
				sbQuery.append(" and is_approved=0");
			} else if(uF.parseToInt(getLeaveStatus())==3) {
				sbQuery.append(" and is_approved=-1");
			}
			sbQuery.append(" and ele.emp_id in(select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id  ");
			/*if(getF_level()!=null && getF_level().length>0) {
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }*/
			 if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0)
	            {
	            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
	            	//sbQuery.append(" and grade_id in ( " +StringUtils.join(getF_grade(), ",")+" ) and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");	
	            }else {
	            	 if(getF_level()!=null && getF_level().length>0){
	                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	                 }
	            	 if(getF_grade()!=null && getF_grade().length>0){
	                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
	                 }
				}
            if(getF_department()!=null && getF_department().length>0) {
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
            }
            if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                	 sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            } 
            
            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
//            System.out.print("org_id:"+getF_org());
            if(uF.parseToInt(getF_org())>0 && !uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_GLOBAL_MANAGER_ACCESS))) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if((uF.parseToInt(getF_org()) != uF.parseToInt(strSessionOrgId) || (strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(OTHER_HR)))) && uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_GLOBAL_MANAGER_ACCESS))) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(")) e, work_flow_details wfd where e.leave_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_LEAVE+"' ");
			
	//===start parvez date: 08-09-2022===		
			/*if(strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
//					sbQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
			}*/
			if(strUserType != null && !strUserType.equals(ADMIN) && !strUserType.equals(HRMANAGER) && !strUserType.equals(OTHER_HR)) {
				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
//					sbQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
			}
	//===end parvez date: 08-09-2022===		
			sbQuery.append(" order by e.entrydate desc");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("MLAR/396---pst22====>"+pst);
			rs=pst.executeQuery();
			List<String> alEmp = new ArrayList<String>();
			while(rs.next()) {
				alEmp.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(getLeaveStatus())==1 && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
				sbQuery=new StringBuilder();
				sbQuery.append("select ele.emp_id from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0" +
					" and encashment_status=false and is_approved=1 ");
				if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
					sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
				}
				
				sbQuery.append(" and ele.emp_id in(select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id  ");
				/*if(getF_level()!=null && getF_level().length>0) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }*/
				 if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0)
		            {
		            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
		            	//sbQuery.append(" and grade_id in ( " +StringUtils.join(getF_grade(), ",")+" ) and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");	
		            }else {
		            	 if(getF_level()!=null && getF_level().length>0){
		                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
		                 }
		            	 if(getF_grade()!=null && getF_grade().length>0){
		                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
		                 }
					}
	            if(getF_department()!=null && getF_department().length>0) {
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            if (getF_employeType() != null && getF_employeType().length > 0) {
					sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
	            }
	            if(getF_service()!=null && getF_service().length>0) {
	                sbQuery.append(" and (");
	                for(int i=0; i<getF_service().length; i++) {
	                	 sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
	                    
	                    if(i<getF_service().length-1) {
	                        sbQuery.append(" OR "); 
	                    }
	                }
	                sbQuery.append(" ) ");
	            } 
	            
	            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            
	            if(uF.parseToInt(getF_org())>0 && !uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_GLOBAL_MANAGER_ACCESS))) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if((uF.parseToInt(getF_org()) != uF.parseToInt(strSessionOrgId) || (strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(OTHER_HR)))) && uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_GLOBAL_MANAGER_ACCESS))) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				sbQuery.append(") and ele.leave_id not in (select effective_id from work_flow_details where effective_type = '"+WORK_FLOW_LEAVE+"')");
				sbQuery.append(" and ele.leave_id in (select leave_id from leave_application_register where leave_id > 0 ");
				if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
					sbQuery.append(" and _date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' " +
							"and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
				}
				sbQuery.append(") order by entrydate desc");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("other pst111====>"+pst); 
				rs=pst.executeQuery();
				while(rs.next()) {

					if (!alEmp.contains(rs.getString("emp_id"))) {
						alEmp.add(rs.getString("emp_id"));
						//System.out.println("	alEmp.add(rs.getString('emp_id')):"+rs.getString("emp_id"));
						alEmp.add(rs.getString("emp_id"));
					}
				}
			}
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			
//			System.out.println("MLAR/483---alEmp="+alEmp);
			if(alEmp!=null && alEmp.size() > 0) {
				String strEmpIds = StringUtils.join(alEmp.toArray(),",");
				//System.out.println("strEmpIds:"+strEmpIds);
				
				Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
				if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
				
				Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
				if(hmUserTypeIdMap == null) hmUserTypeIdMap = new HashMap<String, String>();
				
				Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
				String locationID=hmEmpWlocationMap.get(strSessionEmpId);
				
				Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
				Map<String, String> hmLevelMap = CF.getEmpLevelMap(con);
				
				sbQuery=new StringBuilder();
				sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
						"and effective_type='"+WORK_FLOW_LEAVE+"' and effective_id in (select ele.leave_id from emp_leave_entry ele,leave_type lt " +
						"where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0 and ele.emp_id in("+strEmpIds+") ");
				if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
					sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
				}
				sbQuery.append(") group by effective_id");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("MLAR/510--pst:==>"+pst);
				rs = pst.executeQuery();
				Map<String, String> hmNextApproval = new HashMap<String, String>();
				while(rs.next()) {
					hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
				}
				
				rs.close();
				pst.close();
				
	//			pst = con.prepareStatement("select effective_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
	//			" and is_approved=0 and effective_type='"+WORK_FLOW_LEAVE+"' group by effective_id ");
				sbQuery=new StringBuilder();
				sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
						" and is_approved=0 and effective_type='"+WORK_FLOW_LEAVE+"' and effective_id in (select ele.leave_id from emp_leave_entry ele,leave_type lt " +
						"where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0 and ele.emp_id in("+strEmpIds+") ");
				if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
					sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
				}
				sbQuery.append(")");
				if(strUserType != null && strUserType.equals(ADMIN)) {
					sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
				} else {
					sbQuery.append(" and user_type_id=? ");
				}
				sbQuery.append("group by effective_id,user_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1,uF.parseToInt(strSessionEmpId));
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
					pst.setInt(2, uF.parseToInt(strBaseUserTypeId));
				} else {
					pst.setInt(2, uF.parseToInt(strUserTypeId));
				}
				if(strUserType != null && strUserType.equals(ADMIN)) {
					pst.setInt(3, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
				}
				rs = pst.executeQuery();
				Map<String, String> hmMemNextApproval = new HashMap<String, String>();
				while(rs.next()) {
					hmMemNextApproval.put(rs.getString("effective_id")+"_"+rs.getString("user_type_id"), rs.getString("member_position"));
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select leave_id from emp_leave_entry where is_approved=-1 and approval_from >=? and approval_from <=? and emp_id in ("+strEmpIds+") ");
				pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
//				System.out.println("MLAR/558--pst="+pst);
				rs = pst.executeQuery();	
				List<String> deniedList=new ArrayList<String>();
				while(rs.next()) {
					if(!deniedList.contains(rs.getString("leave_id"))) {
						deniedList.add(rs.getString("leave_id"));
					}
				}
				rs.close();
				pst.close();
				
	//			pst = con.prepareStatement("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_LEAVE+"' group by effective_id");
				sbQuery=new StringBuilder();
				sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_LEAVE+"' " +
						"and effective_id in (select ele.leave_id from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0 and ele.emp_id in("+strEmpIds+") ");
				if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
					sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
				}
				sbQuery.append(") group by effective_id");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("MLAR/579---pst="+pst);
				rs = pst.executeQuery();			
				while(rs.next()) {
					if(!deniedList.contains(rs.getString("effective_id"))) {
						deniedList.add(rs.getString("effective_id"));
					}
				}
				rs.close();
				pst.close();			
				
	//			pst = con.prepareStatement("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
	//			" and effective_type='"+WORK_FLOW_LEAVE+"' group by effective_id,is_approved");
				sbQuery=new StringBuilder();
				sbQuery.append("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
						" and effective_type='"+WORK_FLOW_LEAVE+"' and effective_id in (select ele.leave_id from emp_leave_entry ele,leave_type lt " +
								"where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0 and ele.emp_id in("+strEmpIds+") ");
				if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
					sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
				}
				sbQuery.append(") group by effective_id,is_approved");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("MLAR/601---pst="+pst);
				rs = pst.executeQuery();			
				Map<String, String> hmAnyOneApproval = new HashMap<String, String>();			
				while(rs.next()) {
					hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
				}
				rs.close();
				pst.close();
				
	//			pst = con.prepareStatement("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
	//					" and effective_type='"+WORK_FLOW_LEAVE+"' group by effective_id,emp_id,user_type_id");
				sbQuery=new StringBuilder();
				sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
						" and effective_type='"+WORK_FLOW_LEAVE+"' and effective_id in (select ele.leave_id from emp_leave_entry ele,leave_type lt " +
								"where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0 and ele.emp_id in("+strEmpIds+") ");
				if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
					sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
				}
				sbQuery.append(") group by effective_id,emp_id,user_type_id");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("MLAR/622---pst="+pst);
				rs = pst.executeQuery();			
				Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();	
				Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();			
				while(rs.next()) {
					hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
					hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
				}
				rs.close();
				pst.close();
				
	//			pst = con.prepareStatement("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
	//				" and effective_type='"+WORK_FLOW_LEAVE+"' group by effective_id,emp_id,user_type_id");
				sbQuery=new StringBuilder();
				sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
					" and effective_type='"+WORK_FLOW_LEAVE+"' and effective_id in (select ele.leave_id from emp_leave_entry ele,leave_type lt " +
							"where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0 and ele.emp_id in("+strEmpIds+") ");
				if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
					sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
				}
				sbQuery.append(") group by effective_id,emp_id,user_type_id");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("MLAR/645---pst="+pst);
				rs = pst.executeQuery();			
				Map<String, String> hmotherApproveBy = new HashMap<String, String>();			
				while(rs.next()) {
					hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
					hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
				}
				rs.close();
				pst.close();
				
				
	//			pst = con.prepareStatement("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_LEAVE+"' order by effective_id,member_position");
				sbQuery=new StringBuilder();
				sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where effective_type='"+WORK_FLOW_LEAVE+"'" +
						" and effective_id in (select ele.leave_id from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id " +
						"and ele.leave_type_id > 0 and ele.emp_id in("+strEmpIds+") ");
				if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
					sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
				}
				sbQuery.append(")");
				if(strUserType != null && strUserType.equals(ADMIN)) {
					sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
				} else {
					sbQuery.append(" and user_type_id=? ");
				}
				sbQuery.append(" order by effective_id,member_position");
				pst = con.prepareStatement(sbQuery.toString());
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
					pst.setInt(1, uF.parseToInt(strBaseUserTypeId));
				} else {
					pst.setInt(1, uF.parseToInt(strUserTypeId));
				}
				if(strUserType != null && strUserType.equals(ADMIN)) {
					pst.setInt(2, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
				}
//				System.out.println("MLAR/673---pst="+pst);
				rs = pst.executeQuery();			
				Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();
				Map<String, List<String>> hmCheckEmpUserType = new HashMap<String, List<String>>();
				while(rs.next()) {
					List<String> checkEmpList = hmCheckEmp.get(rs.getString("effective_id"));
					if(checkEmpList == null)checkEmpList = new ArrayList<String>();				
					checkEmpList.add(rs.getString("emp_id"));
					
					List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("effective_id")+"_"+rs.getString("emp_id"));
					if(checkEmpUserTypeList == null)checkEmpUserTypeList = new ArrayList<String>();				
					checkEmpUserTypeList.add(rs.getString("user_type_id"));
					
					hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
					hmCheckEmpUserType.put(rs.getString("effective_id")+"_"+rs.getString("emp_id"), checkEmpUserTypeList);
				}
				rs.close();
				pst.close();
				
	//			pst = con.prepareStatement("select ud.emp_id from user_details ud,employee_official_details eod,employee_personal_details epd where " +
	//					" ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'");
	//			pst.setInt(1, uF.parseToInt(locationID));
	//			rs = pst.executeQuery();			
	//			Map<String, String> hmEmpByLocation = new HashMap<String, String>();			
	//			while(rs.next()) {
	//				hmEmpByLocation.put(rs.getString("emp_id"), rs.getString("emp_id"));
	//			}
	//			rs.close();
	//			pst.close();
				
				pst = con.prepareStatement("select leave_type_id from leave_type where is_compensatory=true");
				rs = pst.executeQuery();
				List<String> compLeaveTypeList=new ArrayList<String>();
				while (rs.next()) {
					compLeaveTypeList.add(rs.getString("leave_type_id"));
				}
				rs.close();
				pst.close();
				 
				Map<String, String> hmApproveLeave = new HashMap<String, String>();
				if(uF.parseToInt(getLeaveStatus()) == 0 || uF.parseToInt(getLeaveStatus())==1) { 
					sbQuery = new StringBuilder();			
					sbQuery.append("select sum(leave_no) as leave_no, leave_id from leave_application_register where leave_id in (select leave_id " +
							"from emp_leave_entry where entrydate is not null and (istravel is null or istravel=false) and emp_id in("+strEmpIds+") ) group by leave_id");
					pst = con.prepareStatement(sbQuery.toString());
//					System.out.println("PST : "+pst.toString());
					rs = pst.executeQuery();
					while(rs.next()){
						hmApproveLeave.put(rs.getString("leave_id"), rs.getString("leave_no"));
					}
					rs.close();
					pst.close();
				}
//				System.out.println("hmApproveLeave : "+hmApproveLeave);
				List<String> alEmployeeList = new ArrayList<String>();	
				List<String> alList = new ArrayList<String>();	
				sbQuery=new StringBuilder();
				sbQuery.append("select e.*,wfd.user_type_id as user_type from (select * from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0" +
					" and is_approved > -2 and encashment_status=false and ele.emp_id in("+strEmpIds+")  ");
				if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
					sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
				}
				if(uF.parseToInt(getLeaveStatus())==1) { 
					sbQuery.append(" and is_approved=1");
				} else if(uF.parseToInt(getLeaveStatus())==2) {
					sbQuery.append(" and is_approved=0");
				} else if(uF.parseToInt(getLeaveStatus())==3) {
					sbQuery.append(" and is_approved=-1");
				}
				sbQuery.append(" and ele.emp_id in(select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id  ");
				/*if(getF_level()!=null && getF_level().length>0) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }*/
				 if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0)
		            {
		            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
		            	//sbQuery.append(" and grade_id in ( " +StringUtils.join(getF_grade(), ",")+" ) and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");	
		            }else {
		            	 if(getF_level()!=null && getF_level().length>0){
		                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
		                 }
		            	 if(getF_grade()!=null && getF_grade().length>0){
		                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
		                 }
					}
	            if(getF_department()!=null && getF_department().length>0) {
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            if (getF_employeType() != null && getF_employeType().length > 0) {
					sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
	            }
	            if(getF_service()!=null && getF_service().length>0) {
	                sbQuery.append(" and (");
	                for(int i=0; i<getF_service().length; i++) {
	                	 sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
	                    
	                    if(i<getF_service().length-1) {
	                        sbQuery.append(" OR "); 
	                    }
	                }
	                sbQuery.append(" ) ");
	            } 
	            
	            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            
	            if(uF.parseToInt(getF_org())>0 && !uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_GLOBAL_MANAGER_ACCESS))) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if((uF.parseToInt(getF_org()) != uF.parseToInt(strSessionOrgId) || (strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(OTHER_HR)))) && uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_GLOBAL_MANAGER_ACCESS))) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				sbQuery.append(")) e, work_flow_details wfd where e.leave_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_LEAVE+"' ");
		//===start parvez date: 09-06-2022===
				
				/*if(strUserType != null && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
					if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
	//					sbQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
						sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
					} else {
						sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
					}
				}*/
				if(strUserType != null && !strUserType.equals(ADMIN) && !strUserType.equals(HRMANAGER) && !strUserType.equals(OTHER_HR)) {
					sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
					if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
	//					sbQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
						sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
					} else {
						sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
					}
				}
		//===end parvez date: 09-06-2022===		
				
				sbQuery.append(" order by e.entrydate desc");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("MLAR/810--pst query1===>"+pst);
				
				rs=pst.executeQuery();
				while(rs.next()) {
					
					List<String> checkEmpList = hmCheckEmp.get(rs.getString("leave_id"));
					if (checkEmpList == null)
						checkEmpList = new ArrayList<String>();
	
					List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("leave_id") + "_" + strSessionEmpId);
					if (checkEmpUserTypeList == null)
						checkEmpUserTypeList = new ArrayList<String>();
//					System.out.println("checkEmpUserTypeList:::"+checkEmpUserTypeList);
					boolean checkGHRInWorkflow = true;
					/*if (checkEmpUserTypeList.contains(hmUserTypeIdMap.get(HRMANAGER)) && !checkEmpUserTypeList.contains(hmUserTypeIdMap.get(ADMIN))
							&& strUserType != null && strUserType.equals(ADMIN)) {
						checkGHRInWorkflow = false;
					}*/
					if ((checkEmpUserTypeList.contains(hmUserTypeIdMap.get(HRMANAGER)) || checkEmpUserTypeList.contains(hmUserTypeIdMap.get(OTHER_HR))) && !checkEmpUserTypeList.contains(hmUserTypeIdMap.get(ADMIN))
							&& strUserType != null && strUserType.equals(ADMIN)) {
						checkGHRInWorkflow = false;
					}
					
			//===start parvez date: 09-06-2022===		
//					System.out.println("MLAR/851--userType="+(!strUserType.equalsIgnoreCase(HRMANAGER) || !strUserType.equalsIgnoreCase(OTHER_HR)));
					if (!checkEmpList.contains(strSessionEmpId) && strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(HRMANAGER) && !strUserType.equalsIgnoreCase(OTHER_HR)) {
						continue;
					}
			//===end parvez date: 09-06-2022===		
	
					// if(strUserType.equalsIgnoreCase(HRMANAGER) &&
					// hmEmpByLocation.get(rs.getString("emp_id"))==null) {
					// continue;
					// }
	
					String userType = rs.getString("user_type");
//					System.out.println("MLAR/851--userType="+userType);
					if (!checkEmpList.contains(strSessionEmpId) && strUserType != null && strUserType.equalsIgnoreCase(ADMIN)
							&& alList.contains(rs.getString("leave_id"))) {
						continue;
			//===start parvez date: 09-06-2022===			
					} else if (!checkEmpList.contains(strSessionEmpId) && strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(OTHER_HR))
							&& !alList.contains(rs.getString("leave_id"))) {
			//===end parvez date: 09-06-2022===				
						userType = strUserTypeId;
						alList.add(rs.getString("leave_id"));
						
					} else if (!checkEmpUserTypeList.contains(userType)) {
//					} else if (!checkEmpUserTypeList.contains(userType) && strUserType != null && !strUserType.equalsIgnoreCase(HRMANAGER)) {
						// System.out.println("4 RID ===>> "+rs.getString("recruitment_id")+" -- USRTYPE ===>> "+rs.getString("user_type"));
						continue;
					}
					
	
					if (!alEmployeeList.contains(rs.getString("emp_id"))) {
						alEmployeeList.add(rs.getString("emp_id"));
					}
					
//					System.out.println("--alEmployeeList="+alEmployeeList);
	
					List<String> alInner = new ArrayList<String>();
					
					alInner.add(uF.showData(hmEmployeeNameMap.get(rs.getString("emp_id")), "")); //0
					alInner.add(rs.getString("leave_type_name")); //1
					alInner.add(uF.getDateFormat(rs.getString("entrydate"), DBDATE, CF.getStrReportDateFormat())); //2
	
					if (rs.getInt("is_approved") == 1) {
						alInner.add(uF.getDateFormat(rs.getString("approval_from"), DBDATE, CF.getStrReportDateFormat())); //3
						alInner.add(uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, CF.getStrReportDateFormat())); //4
					} else {
						alInner.add(uF.getDateFormat(rs.getString("leave_from"), DBDATE, CF.getStrReportDateFormat()));//3
						alInner.add(uF.getDateFormat(rs.getString("leave_to"), DBDATE, CF.getStrReportDateFormat())); //4
					}
	
					String strApproveLeaveDays = "";
					double leaveCnt = 0;// Created By Dattatray Date:20-09-21
					if (!rs.getBoolean("istravel") && rs.getInt("is_approved") == 1 
							&& !uF.parseToBoolean(rs.getString("ishalfday"))&& uF.parseToDouble(hmApproveLeave.get(rs.getString("leave_id"))) > 0.0d) {
						strApproveLeaveDays = "" + uF.parseToDouble(hmApproveLeave.get(rs.getString("leave_id")));
//						System.out.println("if condition "+strApproveLeaveDays);
						
					} else {
//						System.out.println("else condition ");
//
//						System.out.println("hmlevel : "+hmlevel);
//						System.out.println("hmlevel1 : "+hmlevel.get(strSessionEmpId));
//						//End Dattatray Date :14-09-21
//						String sandwitchType = CF.getSandwitchType(con, hmlevel.get(strSessionEmpId), rs.getString("leave_type_id"), strLocationId, strOrgId);

//						if(uF.parseToInt(sandwitchType) == 0) {
//							strApproveLeaveDays = "" + uF.parseToDouble(hmApproveLeave.get(rs.getString("leave_id")));
//						} else {
//							strApproveLeaveDays = ""+uF.parseToDouble(rs.getString("emp_no_of_leave"));
//						}
						
						// Start Dattatray Date:20-09-21
						String strDateDiff = uF.dateDifference(rs.getString("leave_from"), DBDATE, rs.getString("leave_to"), DBDATE,CF.getStrTimeZone());
	                 	strDateDiff = strDateDiff !=null && !strDateDiff.trim().equals("") && !strDateDiff.trim().equalsIgnoreCase("NULL") ? strDateDiff.trim() : "0"; 
	                    leaveCnt = uF.parseToDouble(strDateDiff);
//	                    System.out.println("dblLeavesApproved : "+leaveCnt);
	                    Calendar cal = GregorianCalendar.getInstance();
	                    cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(rs.getString("leave_from"), DBDATE, "dd")));
	                    cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(rs.getString("leave_from"), DBDATE, "MM")) - 1);
	                    cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(rs.getString("leave_from"), DBDATE, "yyyy")));
	                    double count=0;
	                    for (int i1 = 0; i1 < leaveCnt; i1++) {
							String strDate = uF.zero(cal.get(Calendar.DATE)) + "/" + uF.zero((cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
							cal.add(Calendar.DATE, 1);
//							System.out.println("strDate 1 : "+strDate);
							boolean isHoliday = CF.checkHoliday(con, uF, strDate, ""+strLocationId, ""+strOrgId); 
							if(isHoliday){
								count++;
							}
							
							boolean isEmpRosterWeekOff = CF.checkEmpRosterWeeklyOff(con, CF, uF, strSessionEmpId, strDate, hmlevel.get(strSessionEmpId), ""+strLocationId, ""+strOrgId);
							if(isEmpRosterWeekOff){
								count++;
							}
							
	                    }
//	                    System.out.println("Count : "+count);
//	                    
//						
//						
						String sandwitchType = CF.getSandwitchType(con, hmlevel.get(strSessionEmpId), rs.getString("leave_type_id"), strLocationId, strOrgId);
//						System.out.println("Sandwitch type ==> "+sandwitchType);

				//===start parvez date: 04-08-2022===		
//						if(uF.parseToInt(sandwitchType) == SANDWITCH_TYPE_NONE) {
						if(uF.parseToInt(sandwitchType) == SANDWITCH_TYPE_NONE && !uF.parseToBoolean(rs.getString("ishalfday"))) {
				//===end parvez date: 04-08-2022===			
							strApproveLeaveDays = ""+(leaveCnt - count);
							
						} else {
							strApproveLeaveDays = ""+uF.parseToDouble(rs.getString("emp_no_of_leave"));
							
						}
						
						//End Dattatray Date:20-09-21
//						strApproveLeaveDays = ""+uF.parseToDouble(rs.getString("emp_no_of_leave"));
						
					}
//					System.out.println("rs.getString(\"emp_no_of_leave\") : "+rs.getString("emp_no_of_leave"));
					// alInner.add(uF.showData(rs.getString("emp_no_of_leave")+
					// ((uF.parseToBoolean(rs.getString("is_modify")))?"<div title=\"Canceled\" class=\"leftearly\">&nbsp;</div>":""),""));
					alInner.add(strApproveLeaveDays + ((uF.parseToBoolean(rs.getString("is_modify"))) ? "<div title=\"Canceled\" class=\"leftearly\">&nbsp;</div>" : "")); //5
					
					alInner.add(uF.showData(rs.getString("reason"), "-")); //6
					alInner.add(uF.showData(rs.getString("manager_reason"), "-")); //7
	
					StringBuilder sbCheckApproveby = new StringBuilder();
//					System.out.println("hmAnyOneApproeBy::"+hmAnyOneApproeBy);
	
					if (hmAnyOneApproeBy != null && hmAnyOneApproeBy.get(rs.getString("leave_id")) != null) {
						// String
						// approvedby=hmAnyOneApproeBy.get(rs.getString("leave_id"));
						// String strUserTypeName =
						// uF.parseToInt(hmWorkFlowUserTypeId.get(rs.getString("leave_id")))
						// > 0 ?
						// " ("+uF.showData(hmUserTypeMap.get(hmWorkFlowUserTypeId.get(rs.getString("leave_id"))),
						// "")+")" : "";
						// sbCheckApproveby.append(hmEmployeeNameMap.get(approvedby.trim())+strUserTypeName);
					
						sbCheckApproveby.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('" + rs.getString("leave_id") + "','"
								+ hmEmployeeNameMap.get(rs.getString("emp_id")) + "');\" style=\"margin-left: 10px;\">View</a>");
					} else if (hmotherApproveBy != null && hmotherApproveBy.get(rs.getString("leave_id")) != null) {
						sbCheckApproveby.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('" + rs.getString("leave_id") + "','"
								+ hmEmployeeNameMap.get(rs.getString("emp_id")) + "');\" style=\"margin-left: 10px;\">View</a>");
					} else {
						sbCheckApproveby.append("");
					}
//					System.out.println("sbCheckApproveby::"+sbCheckApproveby);
					if (deniedList.contains(rs.getString("leave_id"))) {
						 /*alInner.add("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>"); //8
						alInner.add(sbCheckApproveby.toString()); //9
					} else if (rs.getInt("is_approved") == 1) {
						/*alInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>"); //8
						alInner.add(sbCheckApproveby.toString()); //9
					} else if (uF.parseToInt(hmAnyOneApproval.get(rs.getString("leave_id"))) == 1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("leave_id"))) == rs.getInt("is_approved")) {
						/*alInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>"); //8
						alInner.add(sbCheckApproveby.toString()); //9
					} else if (uF.parseToInt(hmNextApproval.get(rs.getString("leave_id"))) == uF.parseToInt(hmMemNextApproval.get(rs.getString("leave_id") + "_" + userType)) && uF.parseToInt(hmNextApproval.get(rs.getString("leave_id"))) > 0) {
//						System.out.println("In else::");
						// if(ArrayUtils.contains(arrEnabledModules,
						// MODULE_PROJECT_MANAGEMENT+"")>=0 &&
						// compLeaveTypeList.contains(rs.getString("leave_type_id")))
						// {
						// alInner.add("Extra Working can be approved from timesheet.");
						// } else {
						// alInner.add("<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to approve this request?')) window.location='ManagerLeaveApproval.action?apType=auto&apStatus=1&E="+rs.getString("leave_id")+"&LID="+hmLevelMap.get(rs.getString("emp_id"))+"&strCompensatory="+rs.getString("is_compensatory")+"&leaveStatus="+getLeaveStatus()+"&strStartDate="+getStrStartDate()+"&strEndDate="+getStrEndDate()+"';\"><img title=\"Approved\" src=\"images1/icons/icons/approve_icon.png\" border=\"0\" /></a> "
						// +
						// " <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to denied this request?')) window.location='ManagerLeaveApproval.action?apType=auto&apStatus=-1&E="+rs.getString("leave_id")+"&LID="+hmLevelMap.get(rs.getString("emp_id"))+"&strCompensatory="+rs.getString("is_compensatory")+"&leaveStatus="+getLeaveStatus()+"&strStartDate="+getStrStartDate()+"&strEndDate="+getStrEndDate()+"';\"><img title=\"Denied\" src=\"images1/icons/icons/close_button_icon.png\" border=\"0\" /></a> ");
						alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveDeny('1','" + rs.getString("leave_id") + "','"
								+ hmLevelMap.get(rs.getString("emp_id")) + "','" + rs.getString("is_compensatory") + "','" + getLeaveStatus() + "','"
								+ getStrStartDate() + "','" + getStrEndDate() + "','" + userType
								+ "');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve Leave11\"></i></a> "
								+ "&nbsp;<a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','" + rs.getString("leave_id") + "','"
								+ hmLevelMap.get(rs.getString("emp_id")) + "','" + rs.getString("is_compensatory") + "','" + getLeaveStatus() + "','"
								+ getStrStartDate() + "','" + getStrEndDate() + "','" + userType
								+ "');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Deny Leave\"></i></a> "); //8
						// }
						//System.out.println("sbCheckApproveby::"+sbCheckApproveby);
						alInner.add(sbCheckApproveby.toString()); //9
					} else if (uF.parseToInt(hmNextApproval.get(rs.getString("leave_id"))) < uF.parseToInt(hmMemNextApproval.get(rs.getString("leave_id") + "_" + userType))
						|| (uF.parseToInt(hmNextApproval.get(rs.getString("leave_id"))) == 0 && uF.parseToInt(hmNextApproval.get(rs.getString("leave_id"))) == uF.parseToInt(hmMemNextApproval.get(rs.getString("leave_id") + "_" + userType)))) {
						
//						System.out.println("in else if");
						// if(!checkEmpList.contains(strSessionEmpId) &&
						// (strUserType.equalsIgnoreCase(ADMIN) ||
						// strUserType.equalsIgnoreCase(HRMANAGER))) {
						if (rs.getInt("is_approved") == 0) {
//							System.out.println("strUserType-===>"+strUserType);
							if (strUserType.equalsIgnoreCase(ADMIN) && uF.parseToInt(strUserTypeId) == uF.parseToInt(userType)) {
								alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveDeny('1','" + rs.getString("leave_id") + "','"
										+ hmLevelMap.get(rs.getString("emp_id")) + "','" + rs.getString("is_compensatory") + "','" + getLeaveStatus() + "','"
										+ getStrStartDate() + "','" + getStrEndDate() + "','" + userType
										+ "');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve Leave22\"></i></a> "
										+ "&nbsp;<a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','" + rs.getString("leave_id") + "','"
										+ hmLevelMap.get(rs.getString("emp_id")) + "','" + rs.getString("is_compensatory") + "','" + getLeaveStatus() + "','"
										+ getStrStartDate() + "','" + getStrEndDate() + "','" + userType
										+ "');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Deny Leave\"></i></a> "); //8
							} else {
								
								StringBuffer sbWork = new StringBuffer();
								/*sbWork.append("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
								sbWork.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Waiting for workflow\"></i>");
								
//								System.out.println("checkGHRInWorkflow-===>"+checkGHRInWorkflow);
								if (!checkGHRInWorkflow) {
									sbWork.append("<a href=\"javascript:void(0);\" onclick=\"approveDeny('1','" + rs.getString("leave_id") + "','"
											+ hmLevelMap.get(rs.getString("emp_id")) + "','" + rs.getString("is_compensatory") + "','" + getLeaveStatus() + "','"
											+ getStrStartDate() + "','" + getStrEndDate() + "','');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve Leave (" + ADMIN+ ")\"></i></a> "
											+ "&nbsp;<a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','" + rs.getString("leave_id") + "','"
											+ hmLevelMap.get(rs.getString("emp_id")) + "','" + rs.getString("is_compensatory") + "','" + getLeaveStatus() + "','"
											+ getStrStartDate() + "','" + getStrEndDate() + "','');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Deny Leave (" + ADMIN+ ")\"></i></a> ");
								}
								alInner.add(sbWork.toString()); //8
							}
							alInner.add(sbCheckApproveby.toString()); //9
							
						} else if (rs.getInt("is_approved") == 1) {
							alInner.add("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved \"></i>"); //8
							alInner.add(sbCheckApproveby.toString()); //9
						} else {
							alInner.add("<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i>"); //8
							alInner.add(sbCheckApproveby.toString()); //9
						}
						/*
						 * } else {
						 * 
						 * alInner.add(
						 * "<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />"
						 * ); alInner.add(sbCheckApproveby.toString()); }
						 */
					} else {
						// alInner.add("<img title=\"Cancel Leave\" src=\"images1/icons/pullout.png\" border=\"0\" />");
						
						
						if (strUserType.equalsIgnoreCase(ADMIN)) {	
//							System.out.println("MLAR/1075--strUserType="+strUserType);
							alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveDeny('1','" + rs.getString("leave_id") + "','"
									+ hmLevelMap.get(rs.getString("emp_id")) + "','" + rs.getString("is_compensatory") + "','" + getLeaveStatus() + "','"
									+ getStrStartDate() + "','" + getStrEndDate() + "','" + userType
									+ "');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve Leave33\"></i></a> "
									+ "&nbsp;<a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','" + rs.getString("leave_id") + "','"
									+ hmLevelMap.get(rs.getString("emp_id")) + "','" + rs.getString("is_compensatory") + "','" + getLeaveStatus() + "','"
									+ getStrStartDate() + "','" + getStrEndDate() + "','" + userType
									+ "');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Deny Leave\"></i></a> "); //8
						} else {
							/*alInner.add("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
						//===start parvez date: 01-10-2022===
//							alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Waiting for workflow\" style=\"color:#f7ee1d\"></i>"); //8
							if(strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(OTHER_HR)){
								StringBuilder strComment = new StringBuilder();
								if(rs.getInt("is_approved") == 0){
//									alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Pending\" style=\"color:#b71cc5\"></i>"); //8
									strComment.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Pending\" style=\"color:#b71cc5\"></i>");
								} else if(rs.getInt("is_approved") == 1){
//									alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Approved\" style=\"color:#54aa0d\"></i>"); //8
									strComment.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Approved\" style=\"color:#54aa0d\"></i>");
								} else if(rs.getInt("is_approved") == -1){
//									alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Denied\" style=\"color:#e22d25\"></i>"); //8
									strComment.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Denied\" style=\"color:#e22d25\"></i>");
								}
								
								if(uF.parseToBoolean(hmFeatureStatus.get(F_HR_GHR_ONLY_COMMENT_FOR_LEAVE))){
									strComment.append("<a href=\"javascript:void(0);\" onclick=\"addComment('"+rs.getString("leave_id")+"','"
											+hmLevelMap.get(rs.getString("emp_id"))+"','"+getLeaveStatus()+"','"+getStrStartDate()+"','" + getStrEndDate() + "','" + userType
											+"');\"><i class=\"fa fa-comment\" aria-hidden=\"true\"  title=\"Add Comment\"></i></a>");
								}
								
								alInner.add(strComment.toString());	//8
								
							} else{
								alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Waiting for workflow\" style=\"color:#f7ee1d\"></i>"); //8
							}
						//===end parvez date: 01-10-2022===	
						}
						alInner.add(sbCheckApproveby.toString()); //9
					}
	
					alInner.add(rs.getString("leave_id")); //10
					alInner.add(rs.getString("leave_type_id")); //11
					
					if (uF.parseToBoolean(rs.getString("is_modify"))) {
						alInner.add("Last Modified on " + uF.getDateFormat(rs.getString("modify_date"), DBDATE, CF.getStrReportDateFormat()) + " by "
								+ hmEmployeeNameMap.get(rs.getString("modify_by"))); //12
					} else {
						alInner.add("Modify"); //12
					}
					alInner.add(rs.getString("is_approved")); //13
					alInner.add(uF.showData(hmUserTypeMap.get(userType), "")); //14
					String strEmpName = CF.getEmpNameMapByEmpId(con, rs.getString("emp_id"));
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"viewLeaveBalance(" + rs.getString("emp_id") + ",'"+strEmpName+"',"+nCurrentYear+");\" >view</a>"); //15
					alInner.add(rs.getString("is_modify")); //16
					if(rs.getString("from_time") !=null && rs.getString("to_time")!= null) {
						alInner.add(uF.getDateFormat(rs.getString("from_time"), DBTIME, TIME_FORMAT)); //17
						alInner.add(uF.getDateFormat(rs.getString("to_time"), DBTIME, TIME_FORMAT)); //18
					} else {
						alInner.add(""); //17
						alInner.add(""); //18
					}
					
			//===start parvez date: 29-09-2022===
					if(rs.getString("document_attached")!=null){
//						alInner.add("<div style=\"float:right\"><a href=\""+CF.getStrDocRetriveLocation()+rs.getString("document_attached")+"\" target=\"_blank\" class=\"viewattach\" title=\"View Attachment\"></a></div>");//<i class=\"fa fa-file-o\" aria-hidden=\"true\" style=\"padding-top:2px\"></i>
						if(CF.getStrDocRetriveLocation()==null) {
							alInner.add("<div style=\"float:right\"><a href=\""+request.getContextPath()+DOCUMENT_LOCATION +rs.getString("document_attached")+"\" target=\"_blank\" class=\"viewattach\" title=\"View Attachment\"></a></div>");	//19
						} else {
							alInner.add("<div style=\"float:right\"><a href=\""+CF.getStrDocRetriveLocation()+rs.getString("document_attached")+"\" target=\"_blank\" class=\"viewattach\" title=\"View Attachment\"></a></div>");//<i class=\"fa fa-file-o\" aria-hidden=\"true\" style=\"padding-top:2px\"></i>
						}
					} else{
						alInner.add("");	//19
					}
					
					alInner.add(uF.showData(rs.getString("emp_family_relation"), ""));		//20
			//===end parvez date: 29-09-2022===		
					
				//===start parvez date:18-03-2023===
					if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_BACKUP_EMPLOYEE_FOR_LEAVE))){
						alInner.add(uF.showData(rs.getString("backup_emp_name"), ""));		//21
					} else{
						alInner.add("");		//21
					}
				//===end parvez date:18-03-2023===		
					
	
					reportList.add(alInner);
				}
				rs.close();
				pst.close();
				
				if(uF.parseToInt(getLeaveStatus())==1 && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
					sbQuery=new StringBuilder();
					sbQuery.append("select * from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0" +
						" and encashment_status=false and is_approved=1 and ele.emp_id in("+strEmpIds+") ");
					if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
						sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
						sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
					}
					
					sbQuery.append(" and ele.emp_id in(select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id  ");
					/*if(getF_level()!=null && getF_level().length>0) {
		                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
		            }*/
					 if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0)
			            {
			            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
			            	//sbQuery.append(" and grade_id in ( " +StringUtils.join(getF_grade(), ",")+" ) and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");	
			            }else {
			            	 if(getF_level()!=null && getF_level().length>0){
			                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
			                 }
			            	 if(getF_grade()!=null && getF_grade().length>0){
			                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
			                 }
						}
		            if(getF_department()!=null && getF_department().length>0) {
		                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
		            }
		            if (getF_employeType() != null && getF_employeType().length > 0) {
						sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
		            }
		            if(getF_service()!=null && getF_service().length>0) {
		                sbQuery.append(" and (");
		                for(int i=0; i<getF_service().length; i++) {
		                	 sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
		                    
		                    if(i<getF_service().length-1) {
		                        sbQuery.append(" OR "); 
		                    }
		                }
		                sbQuery.append(" ) ");
		            }
		            
		            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
						sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					}
		            
		            if(uF.parseToInt(getF_org())>0 && !uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_GLOBAL_MANAGER_ACCESS))) {
						sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
					} else if((uF.parseToInt(getF_org()) != uF.parseToInt(strSessionOrgId) || (strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(OTHER_HR)))) && uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_GLOBAL_MANAGER_ACCESS))) {
						sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
					} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					}
					sbQuery.append(") and ele.leave_id not in (select effective_id from work_flow_details where effective_type = '"+WORK_FLOW_LEAVE+"')");
					sbQuery.append(" and ele.leave_id in (select leave_id from leave_application_register where leave_id > 0 ");
					if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
						sbQuery.append(" and _date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' " +
								"and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
					}
					sbQuery.append(") order by entrydate desc");
					pst = con.prepareStatement(sbQuery.toString());
//					System.out.println("other pst====>"+pst); 
					rs=pst.executeQuery();
					while(rs.next()) {
//						System.out.println("while loop");
						if (!alEmployeeList.contains(rs.getString("emp_id"))) {
							alEmployeeList.add(rs.getString("emp_id"));
						}
	
						List<String> alInner = new ArrayList<String>();
						alInner.add(uF.showData(hmEmployeeNameMap.get(rs.getString("emp_id")), "")); //0
//						System.out.println("Index 1 : "+uF.showData(hmEmployeeNameMap.get(rs.getString("emp_id")), ""));
						alInner.add(rs.getString("leave_type_name")); //1
//						System.out.println("Index 2 : "+rs.getString("leave_type_name"));
						alInner.add(uF.getDateFormat(rs.getString("entrydate"), DBDATE, CF.getStrReportDateFormat())); //2
//						System.out.println("Index 3 : "+uF.getDateFormat(rs.getString("entrydate"), DBDATE, CF.getStrReportDateFormat()));
						alInner.add(uF.getDateFormat(rs.getString("approval_from"), DBDATE, CF.getStrReportDateFormat())); //3
//						System.out.println("Index 4 : "+uF.getDateFormat(rs.getString("approval_from"), DBDATE, CF.getStrReportDateFormat()));
						alInner.add(uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, CF.getStrReportDateFormat())); //4
//						System.out.println("Index 5 : "+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, CF.getStrReportDateFormat()));
						String strApproveLeaveDays = "";
						if (!rs.getBoolean("istravel") && rs.getInt("is_approved") == 1 && !uF.parseToBoolean(rs.getString("ishalfday"))) {
							strApproveLeaveDays = "" + uF.parseToDouble(hmApproveLeave.get(rs.getString("leave_id")));
							
						} else {
							strApproveLeaveDays = rs.getString("emp_no_of_leave");
							
						}
//						System.out.println("strApproveLeaveDays : "+strApproveLeaveDays);
						// alInner.add(uF.showData(rs.getString("emp_no_of_leave")+
						// ((uF.parseToBoolean(rs.getString("is_modify")))?"<div title=\"Canceled\" class=\"leftearly\">&nbsp;</div>":""),""));
						alInner.add(strApproveLeaveDays + ((uF.parseToBoolean(rs.getString("is_modify"))) ? "<div title=\"Canceled\" class=\"leftearly\">&nbsp;</div>" : "")); //5
						
						alInner.add(uF.showData(rs.getString("reason"), "-")); //6
						alInner.add(uF.showData(rs.getString("manager_reason"), "-")); //7
	
						StringBuilder sbCheckApproveby = new StringBuilder();
						sbCheckApproveby.append("<a href=\"javascript:void(0)\" onclick=\"alert('This leave is system approved.');\" style=\"margin-left: 10px;\">View</a>");
	
						if (rs.getInt("is_approved") == 1) {
							/*alInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
							alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>"); //8
							alInner.add(sbCheckApproveby.toString()); //9
						}
	
						alInner.add(rs.getString("leave_id")); //10
						alInner.add(rs.getString("leave_type_id")); //11
						if (uF.parseToBoolean(rs.getString("is_modify"))) {
							alInner.add("Last Modified on " + uF.getDateFormat(rs.getString("modify_date"), DBDATE, CF.getStrReportDateFormat()) + " by " + hmEmployeeNameMap.get(rs.getString("modify_by"))); //12
						} else {
							alInner.add("Modify"); //12
						}
						alInner.add(rs.getString("is_approved")); //13
						alInner.add(uF.showData(hmUserTypeMap.get("1"), "")); //14
						alInner.add("<a href=\"#?w=600\" rel=\"popup_name" + rs.getString("emp_id") + "\" class=\"poplight\" >view</a>"); //15
						alInner.add(rs.getString("is_modify")); //16
						if(rs.getString("from_time") !=null && rs.getString("to_time")!= null) {
							alInner.add(uF.getDateFormat(rs.getString("from_time"), DBTIME, TIME_FORMAT)); //17
							alInner.add(uF.getDateFormat(rs.getString("to_time"), DBTIME, TIME_FORMAT)); //18
						} else {
							alInner.add(""); //17
							alInner.add(""); //18
						}
//						System.out.println("1110 alInnerList::"+alInner);
						
						reportList.add(alInner);
					}
					rs.close();
					pst.close();
				}
			}
//			System.out.println("1159 reportList::"+reportList);
			request.setAttribute("reportList", reportList);
			
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private List<String> getLeaveDataCountMonthwise(UtilityFunctions uF, String strStartDate, String strEndDate) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		List<String> leaveCountList = new ArrayList<String>();
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select e.emp_id from (select * from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0" +
				" and is_approved > -2 and encashment_status=false ");
			if((strStartDate!=null && !strStartDate.equals(""))  && (strEndDate!=null && !strEndDate.equals(""))) {
				sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(strStartDate, DATE_FORMAT, DBDATE)+"' ");
				sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE)+"'  ");	
			}
//			if(uF.parseToInt(getLeaveStatus())==1) { 
//				sbQuery.append(" and is_approved=1");
//			} else if(uF.parseToInt(getLeaveStatus())==2) {
//				sbQuery.append(" and is_approved=0");
//			} else if(uF.parseToInt(getLeaveStatus())==3) {
//				sbQuery.append(" and is_approved=-1");
//			}
			sbQuery.append(" and ele.emp_id in(select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id ");
			 if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0) {
	            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
	            } else {
	            	 if(getF_level()!=null && getF_level().length>0) {
	                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	                 }
	            	 if(getF_grade()!=null && getF_grade().length>0) {
	                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
	                 }
				}
            if(getF_department()!=null && getF_department().length>0) {
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
            }
            if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                	 sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            } 
            
            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0 && !uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_GLOBAL_MANAGER_ACCESS))) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
		//===start parvez date: 09-02-2023===	
            } else if((uF.parseToInt(getF_org()) != uF.parseToInt(strSessionOrgId) || (strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(OTHER_HR)))) && uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_GLOBAL_MANAGER_ACCESS))) {
		//===end parvez date: 09-02-2023===		
            	sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(")) e, work_flow_details wfd where e.leave_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_LEAVE+"' ");
		//===start parvez date: 09-02-2023===	
			if(strUserType != null && !strUserType.equals(ADMIN) && !strUserType.equals(OTHER_HR)) {
		//===end parvez date: 09-02-2023===		
				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
			}
			sbQuery.append(" order by e.entrydate desc");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst111123====>"+pst);
			rs=pst.executeQuery();
			List<String> alEmp = new ArrayList<String>();
			while(rs.next()) {
				if(!alEmp.contains(rs.getString("emp_id"))) {
					alEmp.add(rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
			
		//===start parvez date: 09-02-2023===	
			if(uF.parseToInt(getLeaveStatus())==1 && strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(OTHER_HR))) {
		//===end parvez date: 09-02-2023===		
				sbQuery=new StringBuilder();
				sbQuery.append("select ele.emp_id from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0" +
					" and encashment_status=false and is_approved=1 ");
				if((strStartDate!=null && !strStartDate.equals(""))  && (strEndDate!=null && !strEndDate.equals(""))) {
					sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(strStartDate, DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE)+"'  ");	
				}
				
				sbQuery.append(" and ele.emp_id in(select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id  ");
				if(getF_level()!=null && getF_level().length>0){
					sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
				}
				if(getF_grade()!=null && getF_grade().length>0){
					sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
				}
	            if(getF_department()!=null && getF_department().length>0) {
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            if (getF_employeType() != null && getF_employeType().length > 0) {
					sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
	            }
	            if(getF_service()!=null && getF_service().length>0) {
	                sbQuery.append(" and (");
	                for(int i=0; i<getF_service().length; i++) {
	                	sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
	                    if(i<getF_service().length-1) {
	                        sbQuery.append(" OR "); 
	                    }
	                }
	                sbQuery.append(" ) ");
	            }
	            
	            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            
	            if(uF.parseToInt(getF_org())>0 && !uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_GLOBAL_MANAGER_ACCESS))) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			//===start parvez date: 09-02-2023===	
	            } else if((uF.parseToInt(getF_org()) != uF.parseToInt(strSessionOrgId) || (strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(OTHER_HR)))) && uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_GLOBAL_MANAGER_ACCESS))) {
			//===end parvez date: 09-02-2023===		
	            	sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				sbQuery.append(") and ele.leave_id not in (select effective_id from work_flow_details where effective_type = '"+WORK_FLOW_LEAVE+"')");
				sbQuery.append(" and ele.leave_id in (select leave_id from leave_application_register where leave_id > 0 ");
				if((strStartDate!=null && !strStartDate.equals(""))  && (strEndDate!=null && !strEndDate.equals(""))) {
					sbQuery.append(" and _date between '"+uF.getDateFormat(strStartDate, DATE_FORMAT, DBDATE)+"' " + "and '"+uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE)+"'  ");	
				}
				sbQuery.append(") order by entrydate desc");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("other pst====>"+pst); 
				rs=pst.executeQuery();
				while(rs.next()) {
					if (!alEmp.contains(rs.getString("emp_id"))) {
						alEmp.add(rs.getString("emp_id"));
					}
				}
			}
			
//			List<List<String>> reportList = new ArrayList<List<String>>();
			
			if(alEmp!=null && alEmp.size() > 0) {
				String strEmpIds = StringUtils.join(alEmp.toArray(),",");
				
				Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
				if(hmUserTypeIdMap == null) hmUserTypeIdMap = new HashMap<String, String>();
				
				Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
				
				sbQuery=new StringBuilder();
				sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where effective_type='"+WORK_FLOW_LEAVE+"'" +
					" and effective_id in (select ele.leave_id from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id " +
					"and ele.leave_type_id > 0 and ele.emp_id in("+strEmpIds+") ");
				if((strStartDate!=null && !strStartDate.equals(""))  && (strEndDate!=null && !strEndDate.equals(""))) {
					sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(strStartDate, DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE)+"'  ");	
				}
				sbQuery.append(")");
				if(strUserType != null && strUserType.equals(ADMIN)) {
					sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
				} else {
					sbQuery.append(" and user_type_id=? ");
				}
				sbQuery.append(" order by effective_id,member_position");
				pst = con.prepareStatement(sbQuery.toString());
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
					pst.setInt(1, uF.parseToInt(strBaseUserTypeId));
				} else {
					pst.setInt(1, uF.parseToInt(strUserTypeId));
				}
				if(strUserType != null && strUserType.equals(ADMIN)) {
					pst.setInt(2, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
				}
				rs = pst.executeQuery();			
				Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();
				Map<String, List<String>> hmCheckEmpUserType = new HashMap<String, List<String>>();
				while(rs.next()) {
					List<String> checkEmpList = hmCheckEmp.get(rs.getString("effective_id"));
					if(checkEmpList == null)checkEmpList = new ArrayList<String>();				
					checkEmpList.add(rs.getString("emp_id"));
					
					List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("effective_id")+"_"+rs.getString("emp_id"));
					if(checkEmpUserTypeList == null)checkEmpUserTypeList = new ArrayList<String>();				
					checkEmpUserTypeList.add(rs.getString("user_type_id"));
					
					hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
					hmCheckEmpUserType.put(rs.getString("effective_id")+"_"+rs.getString("emp_id"), checkEmpUserTypeList);
				}
				rs.close();
				pst.close();
				
				
				List<String> alList = new ArrayList<String>();	
				sbQuery=new StringBuilder();
				sbQuery.append("select e.*,wfd.user_type_id as user_type from (select * from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0" +
					" and is_approved > -2 and encashment_status=false and ele.emp_id in("+strEmpIds+")  ");
				if((strStartDate!=null && !strStartDate.equals(""))  && (strEndDate!=null && !strEndDate.equals(""))) {
					sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(strStartDate, DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE)+"'  ");	
				}
//				if(uF.parseToInt(getLeaveStatus())==1) { 
//					sbQuery.append(" and is_approved=1");
//				} else if(uF.parseToInt(getLeaveStatus())==2) {
//					sbQuery.append(" and is_approved=0");
//				} else if(uF.parseToInt(getLeaveStatus())==3) {
//					sbQuery.append(" and is_approved=-1");
//				}
				sbQuery.append(") e, work_flow_details wfd where e.leave_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_LEAVE+"' ");
			//===start parvez date: 09-02-2023===	
				if(strUserType != null && !strUserType.equals(ADMIN) && !strUserType.equals(OTHER_HR)) {
			//===end parvez date: 09-02-2023===		
					sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
					if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
						sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
					} else {
						sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
					}
				}
				sbQuery.append(" order by e.entrydate desc");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("MLA/1557---pst====>"+pst);
				int totAppliedLeave = 0;
				int totApproveDenyLeave = 0;
				rs=pst.executeQuery();
				while(rs.next()) {
	
					List<String> checkEmpList = hmCheckEmp.get(rs.getString("leave_id"));
					if (checkEmpList == null) checkEmpList = new ArrayList<String>();
	
					List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("leave_id") + "_" + strSessionEmpId);
					if (checkEmpUserTypeList == null) checkEmpUserTypeList = new ArrayList<String>();
	
//					System.out.println("MLA/1569----1----");
				//===start parvez date: 09-02-2023===	
					if (!checkEmpList.contains(strSessionEmpId) && strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(OTHER_HR)) {
						continue;
					}

//					System.out.println("MLA/1569----2----");
					String userType = rs.getString("user_type");
					if (!checkEmpList.contains(strSessionEmpId) && strUserType != null && strUserType.equalsIgnoreCase(ADMIN) && alList.contains(rs.getString("leave_id"))) {
//						System.out.println("MLA/1569----2.1----");
						continue;
					} else if (!checkEmpList.contains(strSessionEmpId) && strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(OTHER_HR)) 
							&& !alList.contains(rs.getString("leave_id"))) {
						alList.add(rs.getString("leave_id"));
					} else if (!checkEmpUserTypeList.contains(userType)) {
//						System.out.println("MLA/1569----2.2----");
						continue;
					}
				//===end parvez date: 09-02-2023===	
	
					List<String> alInner = new ArrayList<String>();
					alInner.add(uF.showData(hmEmployeeNameMap.get(rs.getString("emp_id")), ""));
	
					if (rs.getInt("is_approved") == 0) {
						
					} else if (rs.getInt("is_approved") == 1) {
						totApproveDenyLeave++;
					} else {
						totApproveDenyLeave++;
					}
					totAppliedLeave++;
				}
				rs.close();
				pst.close();
				
//				if(uF.parseToInt(getLeaveStatus())==1 && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
//					sbQuery=new StringBuilder();
//					sbQuery.append("select * from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0" +
//						" and encashment_status=false and is_approved=1 and ele.emp_id in("+strEmpIds+") ");
//					if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
//						sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
//						sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
//					}
//					sbQuery.append(" and ele.leave_id not in (select effective_id from work_flow_details where effective_type = '"+WORK_FLOW_LEAVE+"')");
//					sbQuery.append(" and ele.leave_id in (select leave_id from leave_application_register where leave_id > 0 ");
//					if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
//						sbQuery.append(" and _date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' " +
//						"and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");	
//					}
//					sbQuery.append(") order by entrydate desc");
//					pst = con.prepareStatement(sbQuery.toString());
//	//				System.out.println("other pst====>"+pst); 
//					rs=pst.executeQuery();
//					while(rs.next()) {
//						List<String> alInner = new ArrayList<String>();
//						alInner.add(uF.showData(hmEmployeeNameMap.get(rs.getString("emp_id")), ""));
//						alInner.add(rs.getString("leave_type_name"));
//						alInner.add(uF.getDateFormat(rs.getString("entrydate"), DBDATE, CF.getStrReportDateFormat()));
//	
//						alInner.add(uF.getDateFormat(rs.getString("approval_from"), DBDATE, CF.getStrReportDateFormat()));
//						alInner.add(uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, CF.getStrReportDateFormat()));
//	
//						String strApproveLeaveDays = "";
//						if (!rs.getBoolean("istravel") && rs.getInt("is_approved") == 1 && !uF.parseToBoolean(rs.getString("ishalfday"))) {
//							strApproveLeaveDays = "" + uF.parseToDouble(hmApproveLeave.get(rs.getString("leave_id")));
//						} else {
//							strApproveLeaveDays = rs.getString("emp_no_of_leave");
//						}
//						alInner.add(strApproveLeaveDays + ((uF.parseToBoolean(rs.getString("is_modify"))) ? "<div title=\"Canceled\" class=\"leftearly\">&nbsp;</div>" : ""));
//						alInner.add(uF.showData(rs.getString("reason"), "-"));
//						alInner.add(uF.showData(rs.getString("manager_reason"), "-"));
//	
//						StringBuilder sbCheckApproveby = new StringBuilder();
//						sbCheckApproveby.append("<a href=\"javascript:void(0)\" onclick=\"alert('This leave is system approved.');\" style=\"margin-left: 10px;\">View</a>");
//	
//						if (rs.getInt("is_approved") == 1) {
//							alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
//							alInner.add(sbCheckApproveby.toString());
//						}
//	
//						alInner.add(rs.getString("leave_id"));
//						alInner.add(rs.getString("leave_type_id"));
//	
//						if (uF.parseToBoolean(rs.getString("is_modify"))) {
//							alInner.add("Last Modified on " + uF.getDateFormat(rs.getString("modify_date"), DBDATE, CF.getStrReportDateFormat()) + " by " + hmEmployeeNameMap.get(rs.getString("modify_by")));
//						} else {
//							alInner.add("Modify");
//						}
//						alInner.add(rs.getString("is_approved"));
//						alInner.add(uF.showData(hmUserTypeMap.get("1"), ""));
//						alInner.add("<a href=\"#?w=600\" rel=\"popup_name" + rs.getString("emp_id") + "\" class=\"poplight\">view</a>");
//						alInner.add(rs.getString("is_modify"));
//	
//						reportList.add(alInner);
//					}
//					rs.close();
//					pst.close();
//				}
				leaveCountList.add(totAppliedLeave+"");
				leaveCountList.add(totApproveDenyLeave+"");
			}
			if(leaveCountList.size() == 0) {
				leaveCountList.add("0");
				leaveCountList.add("0");
			}
			
//			System.out.println("in MLA -- "+strStartDate+" :: "+strEndDate+" -- leaveCountList ===>> " + leaveCountList);
			
//			request.setAttribute("leaveCountList", leaveCountList);
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return leaveCountList;
	}
	
	
	public String loadManagerLeaveApproval(UtilityFunctions uF) {	
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		employementTypeList = new FillEmploymentType().fillEmploymentType(request);
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("LEAVE_STATUS");
		if(uF.parseToInt(getLeaveStatus())==1) { 
			hmFilter.put("LEAVE_STATUS", "Approved");
		} else if(uF.parseToInt(getLeaveStatus())==2) {
			hmFilter.put("LEAVE_STATUS", "Pending");
		} else if(uF.parseToInt(getLeaveStatus())==3) {
			hmFilter.put("LEAVE_STATUS", "Denied");
		} else {
			hmFilter.put("LEAVE_STATUS", "All");
		}
		
		if((strUserType != null && !strUserType.equals(MANAGER)) || (getCurrUserType() != null && getCurrUserType().equals(strBaseUserType))) {
			alFilter.add("ORGANISATION");
//			System.out.print("ORGANISATION"+organisationList);
			if(getF_org()!=null) {
				String strOrg="";
				int k=0;
				for(int i=0;organisationList!=null && i<organisationList.size();i++) {
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
		}
		if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
			alFilter.add("PERIOD");
			hmFilter.put("PERIOD", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}

		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
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


	public String getLeaveStatus() {
		return leaveStatus;
	}


	public void setLeaveStatus(String leaveStatus) {
		this.leaveStatus = leaveStatus;
	}


	public String getAlertStatus() {
		return alertStatus;
	}


	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}


	public String getAlert_type() {
		return alert_type;
	}


	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
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

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
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

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrSbu() {
		return strSbu;
	}

	public void setStrSbu(String strSbu) {
		this.strSbu = strSbu;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}
	public String[] getF_grade() {
		return f_grade;
	}

	public void setF_grade(String[] f_grade) {
		this.f_grade = f_grade;
	}
	
	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}
	
	public String[] getF_employeType() {
		return f_employeType;
	}

	public void setF_employeType(String[] f_employeType) {
		this.f_employeType = f_employeType;
	}

	public List<FillEmploymentType> getEmployementTypeList() {
		return employementTypeList;
	}

	public void setEmployementTypeList(List<FillEmploymentType> employementTypeList) {
		this.employementTypeList = employementTypeList;
	}

	public String getStrGrade() {
		return strGrade;
	}

	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}

	public String getStrEmployeType() {
		return strEmployeType;
	}

	public void setStrEmployeType(String strEmployeType) {
		this.strEmployeType = strEmployeType;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	
	public String getStrpaycycle1() {
		return strpaycycle1;
	}

	public void setStrpaycycle1(String strpaycycle1) {
		this.strpaycycle1 = strpaycycle1;
	}
}