package com.konnect.jpms.payroll;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
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

public class ProccessingDashboard_1 extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null;
	
	String paycycle;
	String strPaycycleDuration;
	String f_paymentMode;
	String f_org;
	String[] bankBranch;
	String[] f_strWLocation;
	String[] f_level;
	String[] f_department;
	String[] f_service;
	String[] f_employeType;
	String[] f_grade;
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	String strGrade;
	String strEmployeType;
	
	String strStartDate;
	String strEndDate;
	
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillEmploymentType> employementTypeList;
	List<FillGrade> gradeList;
	List<FillPayCycles> paycycleList;
	
	String currUserType;
	
	public String execute() throws Exception {
//		System.out.println("in processing dashboard method");
      
		session = request.getSession(); 
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(TITLE, PProcessing_Dashboard);
		request.setAttribute(PAGE, ProccessingDashboard_1);
		
		if(strUserType==null || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(ACCOUNTANT)
			&& !strUserType.equalsIgnoreCase(MANAGER) && !strUserType.equalsIgnoreCase(HRMANAGER) && !strUserType.equalsIgnoreCase(CEO))) {
			
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied); 
			return ACCESS_DENIED;
		}
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-money\"></i><a href=\"ApplyPay.action\" style=\"color: #3c8dbc;\"> Pay</a></li>" +
			"<li class=\"active\">Proccessing Dashboard</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		if(getF_org()==null || getF_org().trim().equals("")){
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
			setF_department(null);
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
			
		String[] strPayCycleDates = null;
		//System.out.println("getPaycycle==>"+getPaycycle());
		
		if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		} else {
			strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}
		
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		
		viewLeaveAppraovalDataMonthwise(uF);
		viewApproveAttendance(uF);
		viewApprovePayByGrade(uF);
		viewApporvedPayroll(uF);	
		loadProcessingDashboard1(uF);
		
		return SUCCESS;
	}
	
	//********code for creating the list of all*********//
	public String loadProcessingDashboard1(UtilityFunctions uF) {
		
		//System.out.println("hii im in loadProcessingDashboard1");
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
	
	//***code for getting the all the data which is selected by the user******//
	private void getSelectedFilter(UtilityFunctions uF) {
		
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		if((strUserType != null && !strUserType.equals(MANAGER)) || (getCurrUserType() != null && getCurrUserType().equals(strBaseUserType))) {
			
			alFilter.add("ORGANISATION");
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
				for(int i=0;wLocationList!=null && i<wLocationList.size();i++) 
				{
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
			
		
			alFilter.add("PAYCYCLE");
			if (getPaycycle() != null) {
				String strPayCycle = "";
				int k = 0;
				for (int i = 0; paycycleList != null && i < paycycleList.size(); i++) {
					if (getPaycycle().equals(paycycleList.get(i).getPaycycleId())) {
						if (k == 0) {
							strPayCycle = paycycleList.get(i).getPaycycleName();
						} else {
							strPayCycle += ", " + paycycleList.get(i).getPaycycleName();
						}
						k++;
					}
				}
				if (strPayCycle != null && !strPayCycle.equals("")) {
					hmFilter.put("PAYCYCLE", strPayCycle);
				} else {
					hmFilter.put("PAYCYCLE", "All Paycycle");
				}

			}
			
			
		/*	System.out.println("hiii above date check function");
			System.out.println("getStrStartDate"+getStrStartDate());
			
			if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
			System.out.println("hiii im in date check function");
			
			alFilter.add("PERIOD");
			System.out.println("getStrStartDate"+getStrStartDate());
			
			hmFilter.put("PERIOD", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		*/
			
		}

		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	private void viewLeaveAppraovalDataMonthwise(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
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
			
			con=db.makeConnection(con);
			
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
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select e.emp_id from (select * from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0" +
				" and is_approved > -2 and encashment_status=false ");
			if((strStartDate!=null && !strStartDate.equals(""))  && (strEndDate!=null && !strEndDate.equals(""))) {
				sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(strStartDate, DATE_FORMAT, DBDATE)+"' ");
				sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE)+"'  ");	
			}
			sbQuery.append(" and ele.emp_id in(select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id ");
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
        	if(getF_level()!=null && getF_level().length>0) {
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
        	if(getF_grade()!=null && getF_grade().length>0) {
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
            
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(")) e, work_flow_details wfd where e.leave_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_LEAVE+"' ");
			if(strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
			}
			
			sbQuery.append(" order by e.entrydate desc");
			pst = con.prepareStatement(sbQuery.toString());
		//	System.out.println("pst====>"+pst);
			rs=pst.executeQuery();
			List<String> alEmp = new ArrayList<String>();
			while(rs.next()) {
				if(!alEmp.contains(rs.getString("emp_id"))) {
					alEmp.add(rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
			
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
				if(strUserType != null && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
					if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
						sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
					} else {
						sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
					}
				}
				sbQuery.append(" order by e.entrydate desc");
				pst = con.prepareStatement(sbQuery.toString());
			//	System.out.println("pst====>"+pst);
				int totAppliedLeave = 0;
				int totApproveDenyLeave = 0;
				rs=pst.executeQuery();
				while(rs.next()) {
	
					List<String> checkEmpList = hmCheckEmp.get(rs.getString("leave_id"));
					if (checkEmpList == null) checkEmpList = new ArrayList<String>();
	
					List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("leave_id") + "_" + strSessionEmpId);
					if (checkEmpUserTypeList == null) checkEmpUserTypeList = new ArrayList<String>();
	
					if (!checkEmpList.contains(strSessionEmpId) && strUserType != null && !strUserType.equalsIgnoreCase(ADMIN)) {
						continue;
					}
	
					String userType = rs.getString("user_type");
					if (!checkEmpList.contains(strSessionEmpId) && strUserType != null && strUserType.equalsIgnoreCase(ADMIN) && alList.contains(rs.getString("leave_id"))) {
						continue;
					} else if (!checkEmpList.contains(strSessionEmpId) && strUserType != null && strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("leave_id"))) {
						alList.add(rs.getString("leave_id"));
					} else if (!checkEmpUserTypeList.contains(userType)) {
						continue;
					}
	
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
				
				leaveCountList.add(totAppliedLeave+"");
				leaveCountList.add(totApproveDenyLeave+"");
			}
			if(leaveCountList.size() == 0) {
				leaveCountList.add("0");
				leaveCountList.add("0");
			}
			
//			System.out.println(strStartDate+" :: "+strEndDate+" -- leaveCountList ===>> " + leaveCountList);
			
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return leaveCountList;
	}

//******for ApproveAttendance-->clock functionality***********//
	
	
private void viewApproveAttendance(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			List<List<String>> alPaycycleList_ApproveAttendance = new ArrayList<List<String>>();
			for (int i = 0; paycycleList != null && ((i < paycycleList.size() && paycycleList.size()>6 && i< 6) || (i < paycycleList.size() && paycycleList.size()<=6)); i++) {
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(paycycleList.get(i).getPaycycleId());
				innerList.add(paycycleList.get(i).getPaycycleName());
				
				String[] strTmp = paycycleList.get(i).getPaycycleId().split("-");
				
				List<String> alList_Approve= getAttendanceStatusWithCount(uF, strTmp[0], strTmp[1]);
	
				innerList.add(alList_Approve.get(0));
				innerList.add(alList_Approve.get(1));
					
				alPaycycleList_ApproveAttendance.add(innerList);
				
			}
			request.setAttribute("alPaycycleList_ApproveAttendance", alPaycycleList_ApproveAttendance);
			
			con=db.makeConnection(con);
			
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}	

private List<String> getAttendanceStatusWithCount(UtilityFunctions uF, String strD1, String strD2) {
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	List<String> alAttendaneStatus = new ArrayList<String>();
	try {
		Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
		cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
		cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM")) - 1);
		cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
		int nTotalNumberOfDays = cal.getActualMaximum(Calendar.DATE);
		List<String> alActualDates = new ArrayList<String>();
		for (int i = 0; i < nTotalNumberOfDays; i++) {
			String strDate = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/"
					+ (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
			alActualDates.add(uF.getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT));
			cal.add(Calendar.DATE, 1);
		}
		// System.out.println("alActualDates==>"+alActualDates);
		con = db.makeConnection(con);

		String strFinancialYearEnd = null;
		String strFinancialYearStart = null;
		String[] strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
		
		if (strFinancialYear != null) {
			strFinancialYearStart = strFinancialYear[0];
			strFinancialYearEnd = strFinancialYear[1];
		}

		StringBuilder sbQuery = new StringBuilder();
		sbQuery.append("select count(*) as emp_ids from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " + // and epd.is_alive=true
			"and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) and emp_per_id > 0 ");
		if (getF_employeType() != null && getF_employeType().length > 0) {
			sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
		}
		if (getF_level() != null && getF_level().length > 0) {
			sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( " + StringUtils.join(getF_level(), ",") + ") ) ");
		}
		if (getF_grade() != null && getF_grade().length > 0) {
			sbQuery.append(" and grade_id in (" + StringUtils.join(getF_grade(), ",") + " ) ");
		}
		if (getF_department() != null && getF_department().length > 0) {
			sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
		}

		if (getF_service() != null && getF_service().length > 0) {
			sbQuery.append(" and (");
			for (int i = 0; i < getF_service().length; i++) {
				sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
				if (i < getF_service().length - 1) {
					sbQuery.append(" OR ");
				}
			}
			sbQuery.append(" ) ");
		}

		if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
			sbQuery.append(" and wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
		} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS) != null) {
			sbQuery.append(" and wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
		}

		if (uF.parseToInt(getF_org()) > 0) {
			sbQuery.append(" and org_id = " + uF.parseToInt(getF_org()));
		} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS) != null) {
			sbQuery.append(" and org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
		}
		sbQuery.append(" and emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
				+ "and paid_from = ? and paid_to=? group by emp_id) and emp_id not in (select emp_id from approve_attendance where "
				+ "approve_from=? and approve_to=?)");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
		pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
		pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
		pst.setDate(9, uF.getDateFormat(strD1, DATE_FORMAT));
		pst.setDate(10, uF.getDateFormat(strD2, DATE_FORMAT));
		// System.out.println("pst=====>"+pst);
		rs = pst.executeQuery();
		String strPendingEmpCount = "0";
		while (rs.next()) {
			strPendingEmpCount = rs.getString("emp_ids");
		}
		rs.close();
		pst.close();

		sbQuery = new StringBuilder();
		sbQuery.append("select count(*) as emp_ids from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " + // and epd.is_alive=true
			"and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) and emp_per_id > 0 ");
		if (getF_employeType() != null && getF_employeType().length > 0) {
			sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
		}
		if (getF_level() != null && getF_level().length > 0) {
			sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "
			+ StringUtils.join(getF_level(), ",") + ") ) ");
		}
		if (getF_grade() != null && getF_grade().length > 0) {
			sbQuery.append(" and grade_id in (" + StringUtils.join(getF_grade(), ",") + " ) ");
		}
		if (getF_department() != null && getF_department().length > 0) {
			sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
		}
		if (getF_service() != null && getF_service().length > 0) {
			sbQuery.append(" and (");
			for (int i = 0; i < getF_service().length; i++) {
				sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
				if (i < getF_service().length - 1) {
					sbQuery.append(" OR ");
				}
			}
			sbQuery.append(" ) ");
		}

		if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
			sbQuery.append(" and wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
		} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS) != null) {
			sbQuery.append(" and wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
		}

		if (uF.parseToInt(getF_org()) > 0) {
			sbQuery.append(" and org_id = " + uF.parseToInt(getF_org()));
		} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS) != null) {
			sbQuery.append(" and org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
		}
		sbQuery.append(" and (emp_id in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
			+ "and paid_from = ? and paid_to=? group by emp_id) or emp_id in (select emp_id from approve_attendance where approve_from=? and approve_to=?)) ");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
		pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
		pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
		pst.setDate(9, uF.getDateFormat(strD1, DATE_FORMAT));
		pst.setDate(10, uF.getDateFormat(strD2, DATE_FORMAT));
		// System.out.println("pst=====>"+pst);
		rs = pst.executeQuery();
		String strApprovedEmpCount = "0";
		while (rs.next()) {
			strApprovedEmpCount = rs.getString("emp_ids");
		}
		rs.close();
		pst.close();

		alAttendaneStatus.add(strApprovedEmpCount);
		alAttendaneStatus.add(strPendingEmpCount);

	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	return alAttendaneStatus;
}

	
//*******code for Approve_pay_check*******************//
	
private void viewApprovePayByGrade(UtilityFunctions uF) {
	
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		List<List<String>> alPaycycleList_ApprovePay = new ArrayList<List<String>>();
		for (int i = 0; paycycleList != null && ((i < paycycleList.size() && paycycleList.size()>6 && i< 6) || (i < paycycleList.size() && paycycleList.size()<=6)); i++) {
			List<String> innerList = new ArrayList<String>();
			innerList.add(paycycleList.get(i).getPaycycleId());
			innerList.add(paycycleList.get(i).getPaycycleName());
			String[] strTmp = paycycleList.get(i).getPaycycleId().split("-");
			
			List<String> alList = getApprovedEmpCount(uF, strTmp[0], strTmp[1]);

				innerList.add(alList.get(0));
				innerList.add(alList.get(1));
				
				alPaycycleList_ApprovePay.add(innerList);
			
		}
		request.setAttribute("alPaycycleList_ApprovePay", alPaycycleList_ApprovePay);
		
		con=db.makeConnection(con);
		
	} catch (Exception e) {
		e.printStackTrace(); 
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}	


private List<String> getApprovedEmpCount(UtilityFunctions uF, String strD1, String strD2) {
	
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	List<String> alApprovePayStatus = new ArrayList<String>();
	
	try {
		con = db.makeConnection(con);
		
		String strFinancialYearEnd = null;
		String strFinancialYearStart = null;
		String[] strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
		
		if (strFinancialYear != null) {
			strFinancialYearStart = strFinancialYear[0];
			strFinancialYearEnd = strFinancialYear[1];
			
		//	System.out.println("strFinancialYearStart==>"+strFinancialYearStart+" ---"+"strFinancialYearEnd"+strFinancialYearEnd);
		}

		
		StringBuilder sbQuery = new StringBuilder();
		sbQuery.append("select count (*) as emp_ids from approve_attendance at, employee_personal_details epd, employee_official_details eod where at.emp_id > 0 "
			+ "and epd.emp_per_id = eod.emp_id and epd.emp_per_id = at.emp_id and at.emp_id = eod.emp_id and at.approve_from>=? "
			+ "and at.approve_to<=? and (epd.employment_end_date is null OR epd.employment_end_date >= ?) and epd.joining_date<=? ");
    	if(getF_level()!=null && getF_level().length>0) {
            sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
        }
    	if(getF_grade()!=null && getF_grade().length>0) {
            sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
        }
		if (getF_employeType() != null && getF_employeType().length > 0) {
			sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "','") + "') ");
		}
		if (getF_department() != null && getF_department().length > 0) {
			sbQuery.append(" and eod.depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
		}

		if (getF_service() != null && getF_service().length > 0) {
			sbQuery.append(" and (");
			for (int i = 0; i < getF_service().length; i++) {
				sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
				if (i < getF_service().length - 1) {
					sbQuery.append(" OR ");
				}
			}
			sbQuery.append(" ) ");
		}
		if (getStrPaycycleDuration() != null) {
			sbQuery.append(" and eod.paycycle_duration ='" + getStrPaycycleDuration() + "'");
		}
		if (uF.parseToInt(getF_paymentMode()) > 0) {
			sbQuery.append(" and eod.payment_mode =" + uF.parseToInt(getF_paymentMode()));
		}
		if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
			sbQuery.append(" and eod.wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
		} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS) != null) {
			sbQuery.append(" and eod.wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
		}
		if (uF.parseToInt(getF_org()) > 0) {
			sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
		} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS) != null) {
			sbQuery.append(" and eod.org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
		}
		sbQuery.append(" and eod.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
			+ "and paid_from = ? and paid_to=? group by emp_id)");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
		pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
		pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
		rs = pst.executeQuery();
		
	//	System.out.println("pst getApprovedEmpCount 1==>"+pst);

		String strPendingEmpCount = "0";
		while (rs.next()) {
			
			strPendingEmpCount = rs.getString("emp_ids");
		}
		rs.close();
		pst.close();

		sbQuery = new StringBuilder();
		sbQuery.append("select count (*) as emp_ids from approve_attendance at, employee_personal_details epd, employee_official_details eod where at.emp_id > 0 "
			+ "and epd.emp_per_id = eod.emp_id and epd.emp_per_id = at.emp_id and at.emp_id = eod.emp_id and at.approve_from>=? "
			+ "and at.approve_to<=? and (epd.employment_end_date is null OR epd.employment_end_date >= ?) and epd.joining_date<=? ");
    	if(getF_level()!=null && getF_level().length>0) {
            sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
        }
    	if(getF_grade()!=null && getF_grade().length>0) {
            sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
        }
		if (getF_employeType() != null && getF_employeType().length > 0) {
			sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "','") + "') ");
		}
		if (getF_department() != null && getF_department().length > 0) {
			sbQuery.append(" and eod.depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
		}

		if (getF_service() != null && getF_service().length > 0) {
			sbQuery.append(" and (");
			for (int i = 0; i < getF_service().length; i++) {
				sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
				if (i < getF_service().length - 1) {
					sbQuery.append(" OR ");
				}
			}
			sbQuery.append(" ) ");
		}
		if (getStrPaycycleDuration() != null) {
			sbQuery.append(" and eod.paycycle_duration ='" + getStrPaycycleDuration() + "'");
		}
		if (uF.parseToInt(getF_paymentMode()) > 0) {
			sbQuery.append(" and eod.payment_mode =" + uF.parseToInt(getF_paymentMode()));
		}
		if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
			sbQuery.append(" and eod.wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
		} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS) != null) {
			sbQuery.append(" and eod.wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
		}
		if (uF.parseToInt(getF_org()) > 0) {
			sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
		} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS) != null) {
			sbQuery.append(" and eod.org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
		}
		sbQuery.append(" and eod.emp_id in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
			+ "and paid_from = ? and paid_to=? group by emp_id)");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
		pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
		pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
		
	//	System.out.println("pst getApprovedEmpCount 2==>"+pst);
		
		rs = pst.executeQuery();
		String strApprovedEmpCount = "0";
		while (rs.next()) {
			strApprovedEmpCount = rs.getString("emp_ids");
		}
		rs.close();
		pst.close();
		
		alApprovePayStatus.add(strApprovedEmpCount);
		alApprovePayStatus.add(strPendingEmpCount);
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	return alApprovePayStatus;
}	
//***************code for paypayroll_money************//

private void viewApporvedPayroll(UtilityFunctions uF) {
	
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		List<List<String>> alPaycycleList_approvePayroll = new ArrayList<List<String>>();
		for (int i = 0; paycycleList != null && ((i < paycycleList.size() && paycycleList.size()>6 && i< 6) || (i < paycycleList.size() && paycycleList.size()<=6)); i++) {
			List<String> innerList = new ArrayList<String>();
			innerList.add(paycycleList.get(i).getPaycycleId());
			innerList.add(paycycleList.get(i).getPaycycleName());
			String[] strTmp = paycycleList.get(i).getPaycycleId().split("-");
		
			List<String> alList = getPaidAndUnpaidEmpCount(uF, strTmp[0], strTmp[1], strTmp[2]);

				innerList.add(alList.get(0));
				innerList.add(alList.get(1));
			
				alPaycycleList_approvePayroll.add(innerList);
		
		}
		request.setAttribute("alPaycycleList_approvePayroll", alPaycycleList_approvePayroll);
		
		con=db.makeConnection(con);
		
	} catch (Exception e) {
		e.printStackTrace(); 
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}
	
private List<String> getPaidAndUnpaidEmpCount(UtilityFunctions uF, String strD1, String strD2, String strPC) {

	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	List<String> alPaidPayStatus = new ArrayList<String>();
	
	try {
		
		con = db.makeConnection(con);

		StringBuilder sbQuery = new StringBuilder();
		sbQuery.append("select count(distinct(eod.emp_id)) as emp_cnt,is_paid from payroll_generation pg, employee_official_details eod where pg.emp_id = eod.emp_id and paycycle=? and paid_from =? and paid_to=? ");
    	if(getF_level()!=null && getF_level().length>0) {
    		sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
        }
    	if(getF_grade()!=null && getF_grade().length>0) {
    		sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
        }
		if (getF_employeType() != null && getF_employeType().length > 0) {
			sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "','") + "') ");
		}
		if (getF_department() != null && getF_department().length > 0) {
			sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
		}
		if (getF_service() != null && getF_service().length > 0) {
			sbQuery.append(" and (");
			for (int i = 0; i < getF_service().length; i++) {
				sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
				if (i < getF_service().length - 1) {
					sbQuery.append(" OR ");
				}
			}
			sbQuery.append(" ) ");
		}
		if (getStrPaycycleDuration() != null) {
			sbQuery.append(" and pay_mode ='" + getStrPaycycleDuration() + "'");
		}
		if (uF.parseToInt(getF_paymentMode()) > 0) {
			sbQuery.append(" and pg.payment_mode =" + uF.parseToInt(getF_paymentMode()));
		}
		if (getBankBranch() != null && getBankBranch().length > 0) {
			sbQuery.append(" and eod.emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
				"and (CAST(epd.emp_bank_name AS integer) in ("+StringUtils.join(getBankBranch(), ",")+") or CAST (epd.emp_bank_name2 AS integer) in ("+StringUtils.join(getBankBranch(), ",")+")))");
		}

		if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
			sbQuery.append(" and eod.wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
		} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(WLOCATION_ACCESS) != null) {
			sbQuery.append(" and eod.wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
		}

		if (uF.parseToInt(getF_org()) > 0) {
			sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
		} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(ORG_ACCESS) != null) {
			sbQuery.append(" and eod.org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
		}
		sbQuery.append(" group by is_paid");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setInt(1, uF.parseToInt(strPC));
		pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
//		System.out.println("pst1====>"+pst);
		rs = pst.executeQuery();
		String strUnpaidEmpCount = "0";
		String strPaidEmpCount = "0";
		while (rs.next()) {
			if (rs.getBoolean("is_paid")) {
				strPaidEmpCount = rs.getString("emp_cnt");
			} else if (!rs.getBoolean("is_paid")) {
				strUnpaidEmpCount = rs.getString("emp_cnt");
			}
		}
		rs.close();
		pst.close();
		alPaidPayStatus.add(strPaidEmpCount);
		alPaidPayStatus.add(strUnpaidEmpCount);
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	return alPaidPayStatus;
}	
	
	
	
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
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

	public String[] getF_employeType() {
		return f_employeType;
	}

	public void setF_employeType(String[] f_employeType) {
		this.f_employeType = f_employeType;
	}

	public String[] getF_grade() {
		return f_grade;
	}

	public void setF_grade(String[] f_grade) {
		this.f_grade = f_grade;
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

	public List<FillEmploymentType> getEmployementTypeList() {
		return employementTypeList;
	}

	public void setEmployementTypeList(List<FillEmploymentType> employementTypeList) {
		this.employementTypeList = employementTypeList;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}
	public String getStrPaycycleDuration() {
		return strPaycycleDuration;
	}

	public void setStrPaycycleDuration(String strPaycycleDuration) {
		this.strPaycycleDuration = strPaycycleDuration;
	}

	public String getF_paymentMode() {
		return f_paymentMode;
	}

	public void setF_paymentMode(String f_paymentMode) {
		this.f_paymentMode = f_paymentMode;
	}

	public String[] getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String[] bankBranch) {
		this.bankBranch = bankBranch;
	}
}
