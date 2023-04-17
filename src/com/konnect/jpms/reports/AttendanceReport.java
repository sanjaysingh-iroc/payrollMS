package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillAttendanceFilter;
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

public class AttendanceReport extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType;
	String strBaseUserType;
	String strBaseUserTypeId;
	String strUserTypeId;
	String strSessionEmpId;
	boolean isEmpUserType = false; 
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(AttendanceReport.class);
 
	private String strD1 = null; 
	private String strD2 = null;
	
	private String D2;
	private String D1;
	
	private String strLocation;
	private String strDepartment;
	private String strSbu;
	
	private String[] f_strWLocation;
	private String[] f_department;
	private String[] f_service;
	private String attendanceFilter;
	private String f_org;
	
	private String[] f_level;
	private String[] f_employeType;
	private String[] f_grade;
	
	private List<FillOrganisation> organisationList;
	private List<FillWLocation> wLocationList;
	private List<FillDepartment> departmentList;
	private List<FillServices> serviceList;
	private List<FillAttendanceFilter> attendanceFilterList;
	
	List<FillLevel> levelList;
	List<FillEmploymentType> employementTypeList;
	List<FillGrade> gradeList;
	
	private String currUserType;
	
	private String proPage;
	private String minLimit;
	private String strSearch;
	
	List<FillPayCycles> paycycleList;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(TITLE, TAttendanceReport);
		request.setAttribute(PAGE, PReportAttendance);
		
		UtilityFunctions uF = new UtilityFunctions();
		strD1 = request.getParameter("D1");
		strD2 = request.getParameter("D2");

		if(strD1 != null && strD1.equals("")) {
			strD1 = null;
		}
		if(strD2 != null && strD2.equals("")) {
			strD2 = null;
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
			setF_department(null);
		}
		if(getStrSbu() != null && !getStrSbu().equals("")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		
		if(uF.parseToInt(getProPage()) == 0) {
			setProPage("1");
		}
		
		paycycleList = new FillPayCycles("M", request).fillPayCycles(CF, getF_org());
		
		viewTimeAppraovalDataMonthwise(uF);
		
		getSearchAutoCompleteData(uF);
		attendanceReport(CF, uF, strUserType, session);
		attendanceChartReport(CF, uF, strUserType, session);
		
		return loadAttendanceReport(uF);
	}

	private void getSearchAutoCompleteData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			List<String> alDates = new ArrayList<String>();
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			if((strD1==null && strD2==null) || (strD1!=null && strD2!=null && strD1.length()==0 && strD2.length()==0)) {
				for(int i=0; i<14; i++) {
					alDates.add(uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), i)+"", DBDATE, DATE_FORMAT) );
				}
			}
			if(strD1!=null && strD1.length()>0) {
				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
				for(int i=0; i<14; i++) {
					cal.add(Calendar.DATE, -1);
					alDates.add(i, uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT) );
				}
				setD2(null);
			} else if(strD2!=null && strD2.length()>0) {
				for(int i=13; i>=0; i--) { 
					
					cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "dd")));
					cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"))-1);
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "yyyy")));
					
					cal.add(Calendar.DATE, i+1);
					
					
					alDates.add( uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT) );
				}
				setD1(null);
			}
			
			SortedSet<String> setSearchList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("SELECT * FROM employee_official_details eod, employee_personal_details epd WHERE eod.emp_id > 0 " +
					"and epd.emp_per_id=eod.emp_id and epd.joining_date <= ? and (employment_end_date is null or "+
					" (employment_end_date >= ? or employment_end_date between ? and ?)) ");
			if(strUserType!=null && strUserType.equals(MANAGER) && !getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append("and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
			} else {
				if(getF_department()!=null && getF_department().length>0) {
					sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
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
	            } else if((String)session.getAttribute(WLOCATION_ACCESS)!=null && !((String)session.getAttribute(WLOCATION_ACCESS)).trim().equals("") && !((String)session.getAttribute(WLOCATION_ACCESS)).trim().equalsIgnoreCase("NULL")) {
	            	sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            
	            if(uF.parseToInt(getF_org())>0) {
	            	sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if((String)session.getAttribute(ORG_ACCESS)!=null && !((String)session.getAttribute(ORG_ACCESS)).trim().equals("") && !((String)session.getAttribute(ORG_ACCESS)).trim().equalsIgnoreCase("NULL")) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
			}
			sbQuery.append(" and eod.emp_id in (select emp_per_id from employee_personal_details epd join attendance_details ad " +
			"on ad.emp_id = epd.emp_per_id and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? AND ?)");
			sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
//			System.out.println("pst=Search=="+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				setSearchList.add(rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbData = null;
			Iterator<String> it = setSearchList.iterator();
			while (it.hasNext()){
				String strData = it.next();
				if(sbData == null){
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null){
				sbData = new StringBuilder();
			}
			request.setAttribute("sbData", sbData.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
}

	public String loadAttendanceReport(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		attendanceFilterList = new FillAttendanceFilter().fillAttendanceFilter();
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
 
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if((strUserType != null && !strUserType.equals(MANAGER)) || (getCurrUserType() != null && getCurrUserType().equals(strBaseUserType))) {
			alFilter.add("ORGANISATION");
			if(getF_org()!=null) {
				String strOrg="";
				for(int i=0;organisationList!=null && i<organisationList.size();i++) {
					if(getF_org().equals(organisationList.get(i).getOrgId())) {
						strOrg=organisationList.get(i).getOrgName();
					}
				}
				if(strOrg!=null && !strOrg.equals("")) {
					hmFilter.put("ORGANISATION", strOrg);
				} else {
					hmFilter.put("ORGANISATION", "All Organisations");
				}
			} else {
				hmFilter.put("ORGANISATION", "All Organisations");
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
				String strSbu="";
				int k=0;
				for(int i=0;departmentList!=null && i<departmentList.size();i++) {
					for(int j=0;j<getF_department().length;j++) {
						if(getF_department()[j].equals(departmentList.get(i).getDeptId())) {
							if(k==0) {
								strSbu=departmentList.get(i).getDeptName();
							} else {
								strSbu+=", "+departmentList.get(i).getDeptName();
							}
							k++;
						}
					}
				}
				if(strSbu!=null && !strSbu.equals("")) {
					hmFilter.put("DEPARTMENT", strSbu);
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
					hmFilter.put("SERVICE", "All Services");
				}
			} else {
				hmFilter.put("SERVICE", "All Services");
			}
		}
		
		if((strD2 != null && !strD2.trim().equals("") && !strD2.trim().equalsIgnoreCase("NULL"))){
			alFilter.add("DATE");
			hmFilter.put("DATE", uF.getDateFormat(strD2, DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	
	private void viewTimeAppraovalDataMonthwise(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			List<List<String>> alLeaveData = new ArrayList<List<String>>();
			List<List<String>> alAttendanceData = new ArrayList<List<String>>();

			for (int i = 0; paycycleList != null && ((i < paycycleList.size() && paycycleList.size()>3 && i< 3) || (i < paycycleList.size() && paycycleList.size()<=3)); i++) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(paycycleList.get(i).getPaycycleId());
				innerList.add(paycycleList.get(i).getPaycycleName());
				
				String[] strTmp = paycycleList.get(i).getPaycycleId().split("-");
				System.out.println("strTmp[0]"+strTmp[0]);
				System.out.println("strTmp[1]"+strTmp[1]);
			
				List<String> alList = getLeaveDataCountMonthwise(uF, strTmp[0], strTmp[1]);
				innerList.add(alList.get(0));
				innerList.add(alList.get(1));
				alLeaveData.add(innerList);
				
				List<String> innerList1 = new ArrayList<String>();
				innerList1.add(paycycleList.get(i).getPaycycleId());
				innerList1.add(paycycleList.get(i).getPaycycleName());
				List<String> alAttendance = getAttendanceStatusWithCount(uF, strTmp[0], strTmp[1]);
				innerList1.add(alAttendance.get(0));
				innerList1.add(alAttendance.get(1));
				alAttendanceData.add(innerList1);
			}
			request.setAttribute("alLeaveData", alLeaveData);
			request.setAttribute("alAttendanceData", alAttendanceData);
			
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
				System.out.println("pst====>"+pst);
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
	
	
	
	public String attendanceReport(CommonFunctions CF, UtilityFunctions uF, String strUserType,HttpSession session) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmExistingEmpNameMap =  new LinkedHashMap<String, String>();
			Map<String, String> hmServicesMap =  CF.getServicesMap(con, true);
			List<String> alDates = new ArrayList<String>();
			
//			System.out.println("strD1==>"+strD1+"--strD2==>"+strD2);
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			if((strD1==null && strD2==null) || (strD1!=null && strD2!=null && strD1.length()==0 && strD2.length()==0)) {
				for(int i=0; i<15; i++) {
					alDates.add(uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), i)+"", DBDATE, DATE_FORMAT) );
				}
			}
			if(strD1!=null && strD1.length()>0) {
				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
				for(int i=0; i<15; i++) {
					cal.add(Calendar.DATE, -1);
					alDates.add(i, uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT) );
				}
				setD2(null);
			} else if(strD2!=null && strD2.length()>0) {
				for(int i=14; i>=0; i--) { 
					
					cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "dd")));
					cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"))-1);
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "yyyy")));
					
					cal.add(Calendar.DATE, i+1);
					
					
					alDates.add( uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT) );
				}
				setD1(null);
			}
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("SELECT count(eod.emp_id) as empCount FROM employee_official_details eod, employee_personal_details epd WHERE eod.emp_id > 0 " +
					"and epd.emp_per_id=eod.emp_id and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) ");
			if(strUserType!=null && strUserType.equals(MANAGER) && !getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append(" and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
			} else {
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
	            } else if((String)session.getAttribute(WLOCATION_ACCESS)!=null && !((String)session.getAttribute(WLOCATION_ACCESS)).trim().equals("") && !((String)session.getAttribute(WLOCATION_ACCESS)).trim().equalsIgnoreCase("NULL")) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            
	            if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
	            } else if((String)session.getAttribute(ORG_ACCESS)!=null && !((String)session.getAttribute(ORG_ACCESS)).trim().equals("") && !((String)session.getAttribute(ORG_ACCESS)).trim().equalsIgnoreCase("NULL")) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
			}
			if(getStrSearch()!=null && !getStrSearch().trim().equals("") && !getStrSearch().trim().equalsIgnoreCase("NULL")){
				if(flagMiddleName) {
					sbQuery.append(" and (upper(emp_fname)||' '||upper(emp_mname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%' or upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%')");
				} else {
					sbQuery.append(" and upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%'");
				}
			}
			sbQuery.append(" and eod.emp_id in (select emp_per_id from employee_personal_details epd join attendance_details ad " +
						"on ad.emp_id = epd.emp_per_id and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? AND ?)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
//			System.out.println("pst2=====+>"+pst);
			rs = pst.executeQuery();
			int recCnt = 0;
			int pageCount = 0;
			while(rs.next()){
				recCnt = rs.getInt("empCount");
				pageCount = rs.getInt("empCount")/10;
				if(rs.getInt("empCount")%10 != 0) {
					pageCount++;
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("pageCount", pageCount+"");
			request.setAttribute("recCnt", recCnt+"");
			
			sbQuery = new StringBuilder();
			sbQuery.append("SELECT * FROM employee_official_details eod, employee_personal_details epd WHERE eod.emp_id > 0 " +
					"and epd.emp_per_id=eod.emp_id and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) ");
			if(strUserType!=null && strUserType.equals(MANAGER) && !getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append(" and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
			} else {
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
	            } else if((String)session.getAttribute(WLOCATION_ACCESS)!=null && !((String)session.getAttribute(WLOCATION_ACCESS)).trim().equals("") && !((String)session.getAttribute(WLOCATION_ACCESS)).trim().equalsIgnoreCase("NULL")) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            
	            if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
	            } else if((String)session.getAttribute(ORG_ACCESS)!=null && !((String)session.getAttribute(ORG_ACCESS)).trim().equals("") && !((String)session.getAttribute(ORG_ACCESS)).trim().equalsIgnoreCase("NULL")) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
			}
			if(getStrSearch()!=null && !getStrSearch().trim().equals("") && !getStrSearch().trim().equalsIgnoreCase("NULL")){
				if(flagMiddleName) {
					sbQuery.append(" and (upper(emp_fname)||' '||upper(emp_mname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%' or upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%')");
				} else {
					sbQuery.append(" and upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%'");
				}
			}
			sbQuery.append(" and eod.emp_id in (select emp_per_id from employee_personal_details epd join attendance_details ad " +
			"on ad.emp_id = epd.emp_per_id and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? AND ?)");
			sbQuery.append(" order by emp_fname, emp_lname");
			int intOffset = uF.parseToInt(getMinLimit());
			sbQuery.append(" limit 10 offset "+intOffset+"");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
//			System.out.println("pst3=====+>"+pst);
			rs = pst.executeQuery();
			List<String> alEmp = new ArrayList<String>();
			while(rs.next()) {
				hmExistingEmpNameMap.put(rs.getString("emp_id"), rs.getString("emp_fname") +" "+rs.getString("emp_lname"));
				
				alEmp.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			int nEmpSize = alEmp.size();
			if(nEmpSize > 0){
				String strEmpIds = StringUtils.join(alEmp.toArray(),",");
	
				Map<String, String> hmBreakTypeCode = new HashMap<String, String>();
				pst = con.prepareStatement("select * from leave_break_type");
				rs = pst.executeQuery();
				while(rs.next()) {
					hmBreakTypeCode.put(rs.getString("break_type_id"), rs.getString("break_type_code"));
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmBreakPolicy = new HashMap<String, String>();
				pst = con.prepareStatement("select * from break_application_register where emp_id in ("+strEmpIds+") and _date between ? and ?");
				pst.setDate(2, uF.getDateFormat((String)alDates.get(0), DATE_FORMAT));
				pst.setDate(1, uF.getDateFormat((String)alDates.get(alDates.size()-1), DATE_FORMAT));
				rs = pst.executeQuery();
				while(rs.next()) {
					hmBreakPolicy.put(rs.getString("emp_id")+"_"+uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_"+rs.getString("_type"), hmBreakTypeCode.get(rs.getString("break_type_id")));
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
				Map<String, Map<String, String>> hmWLocationHolidaysColour = new HashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmWLocationHolidaysName = new HashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmWLocationHolidaysWeekEnd = new HashMap<String, Map<String, String>>();
				
				CF.getWLocationHolidayList(con, uF, alDates.get(alDates.size()-1), alDates.get(0), CF, hmWLocationHolidaysColour, hmWLocationHolidaysName, hmWLocationHolidaysWeekEnd);
				
				Map<String, Map<String, String>> hmLeaveDatesType = new HashMap<String, Map<String, String>>();
	//			Map<String, Map<String, String>> hmLeaveDatesMap =  CF.getLeaveDates(con, (String)alDates.get(alDates.size()-1), (String)alDates.get(0), CF, hmLeaveDatesType, false, null);
				Map<String, Map<String, String>> hmLeaveDatesMap =  CF.getActualLeaveDates(con, CF, uF, (String)alDates.get(alDates.size()-1), (String)alDates.get(0), hmLeaveDatesType, false, null);
				
				Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
				Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
				Map<String, Set<String>> hmWLocationHolidaysWeekEndDates = CF.getWeekEndDateList(con, alDates.get(alDates.size()-1), alDates.get(0), CF, uF,hmWeekEndHalfDates,null);
				Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
				List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
				Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
				CF.getEmpRosterWeekOffTypeByDate(con,uF,CF,alDates.get(0), alDates.get(alDates.size()-1),alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWLocationHolidaysWeekEndDates,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
				
//				if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))) {
//					pst = con.prepareStatement(selectAttendanceRoster);
//				} else if(strUserType!=null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT))) {
//					pst = con.prepareStatement(selectAttendanceRoster);
//				} else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
//					pst = con.prepareStatement(selectAttendanceRoster);
//				}
				pst = con.prepareStatement("select * from employee_personal_details epd left join roster_details rd " +
						"on rd.emp_id = epd.emp_per_id and epd.emp_per_id in ("+strEmpIds+") and _date between ? AND ? order by emp_id");
				pst.setDate(1, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
//				System.out.println("pst======>"+pst);
				rs = pst.executeQuery();
				Map<String, Map<String, String>> hmEmployeeRosterHours = new HashMap<String, Map<String, String>>();
				Map<String, String> hm = new HashMap<String, String>();
				List<String> alServicesTotal  = new ArrayList<String>();
				Map<String, String> hmCount = new HashMap<String, String>();
				Map<String, String> hmServices = new HashMap<String, String>();
				Map<String, List<String>> hmServicesCount = new HashMap<String, List<String>>();
				Map<String, Map<String, String>> hmEmployeesInvolvedInServices = new HashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmEmployeesIssues = new HashMap<String, Map<String, String>>();
				String strEmpIdNew = null;
				String strEmpIdOld = null;
				while(rs.next()) {
					strEmpIdNew = rs.getString("emp_id");
					
					if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
						hm = new HashMap<String, String>();
					}
					
					if(rs.getString("_date")!=null) {
						hm.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("actual_hours"));
						hm.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_S", rs.getString("_from"));
						hm.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_E", rs.getString("_to"));
					}
					
					if(hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
						hmEmployeeRosterHours.put(strEmpIdNew, hm);
					}
					
					strEmpIdOld = strEmpIdNew;
				}
				rs.close();
				pst.close();
	//			System.out.println("hmEmployeeRosterHours======>"+hmEmployeeRosterHours);
				
//				if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) ||
//						strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))) {
//					pst = con.prepareStatement(selectAttendanceActual);
//				} else if(strUserType!=null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT))) {
//					pst = con.prepareStatement(selectAttendanceActual);
//				} else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
//					pst = con.prepareStatement(selectAttendanceActual);
//				}
				pst = con.prepareStatement("select * from employee_personal_details epd left join attendance_details ad on ad.emp_id = epd.emp_per_id " +
						"and epd.emp_per_id in ("+strEmpIds+") and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? AND ? " +
						"order by emp_id,to_date(in_out_timestamp::text, 'YYYY-MM-DD'),in_out");
				pst.setDate(1, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
//				System.out.println("pst======>"+pst);
				rs = pst.executeQuery();
				Map<String, Map<String, String>> hmEmployeeCount = new HashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmEmployeeActualHours = new HashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmEmployeeService = new HashMap<String, Map<String, String>>();
				Map<String, String> hmInActual = new HashMap<String, String>();
				hm = new HashMap<String, String>();
				strEmpIdNew = null;
				strEmpIdOld = null;
				while(rs.next()) {
					strEmpIdNew = rs.getString("emp_per_id");
					String strServiceId = rs.getString("service_id");
					
					if(hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
						
						Map<String, String> hmTemp = hmEmployeesInvolvedInServices.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
						if(hmTemp==null)hmTemp=new HashMap<String, String>();
						int nEmployeeInServices = uF.parseToInt((String)hmTemp.get(strServiceId));
						nEmployeeInServices++;
						hmTemp.put(strServiceId, nEmployeeInServices+"");
						
						hmEmployeesInvolvedInServices.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), hmTemp);
						
					}
					
					if(hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
						Map<String, String> hmTemp = hmEmployeesIssues.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
						if(hmTemp==null)hmTemp=new HashMap<String, String>();
						
						if(rs.getInt("approved")==-2) {
							int nPendingCount = uF.parseToInt((String)hmTemp.get("PENDING"));
							nPendingCount++;
							hmTemp.put("PENDING", nPendingCount+"");
							
						} else if(rs.getInt("approved")==1) {
							int nApprovedCount = uF.parseToInt((String)hmTemp.get("APPROVED"));
							nApprovedCount++;
							hmTemp.put("APPROVED", nApprovedCount+"");
						}
						
						hmEmployeesIssues.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), hmTemp);
					}
					
					if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
						hm = new HashMap<String, String>();
						hmServices = new HashMap<String, String>();
					}
					
					if(!alServicesTotal.contains(strServiceId)) {
						alServicesTotal.add(strServiceId);
					}
					
					hmCount = hmEmployeeCount.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
					if(hmCount==null) {
						hmCount = new HashMap<String, String>();
					}
					
					if("IN".equalsIgnoreCase(rs.getString("in_out")) && hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
						hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_IN_"+strServiceId, rs.getString("in_out_timestamp"));
						hmInActual.put(strEmpIdNew+"_"+uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_IN_"+strServiceId, ""+(uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime()));
						
						if(rs.getDouble("early_late")<0) {
							hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_EARLY_IN_"+strServiceId, rs.getString("early_late"));
							
							int nCount = uF.parseToInt((String)hmCount.get("EARLY_IN"));
							nCount = nCount+1;
							hmCount.put("EARLY_IN_"+strServiceId, nCount+"");
							
						} else if(rs.getDouble("early_late")>0) {
							hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_LATE_IN_"+strServiceId, rs.getString("early_late"));
							
							int nCount = uF.parseToInt((String)hmCount.get("LATE_IN_"+strServiceId));
							nCount = nCount+1;
							hmCount.put("LATE_IN_"+strServiceId, nCount+"");
						} else if(rs.getDouble("early_late")==0) {
							hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_ONTIME_IN_"+strServiceId, rs.getString("early_late"));
							
							int nCount = uF.parseToInt((String)hmCount.get("ONTIME_IN_"+strServiceId));
							nCount = nCount+1;
							hmCount.put("ONTIME_IN_"+strServiceId, nCount+"");
						}
						
						hmServices.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), rs.getString("service_id"));
						
						List<String> alServicesCount =hmServicesCount.get(strEmpIdNew);
						if(alServicesCount==null) {
							alServicesCount = new ArrayList<String>();
						}
						if(!alServicesCount.contains(strServiceId)) {
							alServicesCount.add(strServiceId);
						}
						hmServicesCount.put(strEmpIdNew, alServicesCount);
						
					} else if("OUT".equalsIgnoreCase(rs.getString("in_out")) && hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
						hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat())+"_OUT_"+strServiceId, rs.getString("in_out_timestamp"));
						
						long inActual = uF.parseToLong(hmInActual.get(strEmpIdNew+"_"+uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_IN_"+strServiceId));
						long outActual = uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
						double dblHoursWorkedActual = uF.parseToDouble(uF.getTimeDiffInHoursMins(inActual, outActual));
//						System.out.println("strEmpIdNew==>"+strEmpIdNew+"--strDate==>"+uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
//						System.out.println("inActual==>"+inActual+"--outActual==>"+outActual+"--dblHoursWorkedActual==>"+dblHoursWorkedActual);
						
						if(hm.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_WH_"+strServiceId)) {
							double dbl = uF.parseToDouble((String)hm.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat())+"_WH"));
//							dbl += rs.getDouble("hours_worked");
							dbl += dblHoursWorkedActual;
							hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_WH_"+strServiceId, uF.formatIntoTwoDecimal(dbl)); 
						} else {
//							hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_WH_"+strServiceId, uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("hours_worked"))));
							hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_WH_"+strServiceId, uF.formatIntoTwoDecimal(dblHoursWorkedActual));
						}
						
						if(rs.getDouble("early_late")<0) {
							hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_EARLY_OUT_"+strServiceId, rs.getString("early_late"));
							
							int nCount = uF.parseToInt((String)hmCount.get("EARLY_OUT_"+strServiceId));
							nCount = nCount+1;
							hmCount.put("EARLY_OUT_"+strServiceId, nCount+"");
							
						} else if(rs.getDouble("early_late")>0) {
							hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_LATE_OUT_"+strServiceId, rs.getString("early_late"));
							
							int nCount = uF.parseToInt((String)hmCount.get("LATE_OUT_"+strServiceId));
							nCount = nCount+1;
							hmCount.put("LATE_OUT_"+strServiceId, nCount+"");
							
						} else if(rs.getDouble("early_late")==0) {
							hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_ONTIME_OUT_"+strServiceId, rs.getString("early_late"));
							
							int nCount = uF.parseToInt((String)hmCount.get("ONTIME_OUT_"+strServiceId));
							nCount = nCount+1;
							hmCount.put("ONTIME_OUT_"+strServiceId, nCount+"");
						}
						
						hmServices.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), rs.getString("service_id"));
						List<String> alServicesCount = hmServicesCount.get(strEmpIdNew);
						if(alServicesCount==null) {
							alServicesCount = new ArrayList<String>();
						}
						if(!alServicesCount.contains(rs.getString("service_id"))) {
							alServicesCount.add(rs.getString("service_id"));
						}
						hmServicesCount.put(strEmpIdNew, alServicesCount);
					}
					
					if(hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
						hmEmployeeActualHours.put(strEmpIdNew, hm);
						hmEmployeeService.put(strEmpIdNew, hmServices);
					}
					
					strEmpIdOld = strEmpIdNew;
					
					if(rs.getString("in_out_timestamp")!=null && hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
						hmEmployeeCount.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), hmCount);
					}
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmLeaveTypes = new HashMap<String, String>();
				CF.getLeavesAttributes(con, uF, hmLeaveTypes, null);
			
				
				request.setAttribute("hmEmployeeService", hmEmployeeService);
				request.setAttribute("hmServicesMap", hmServicesMap);
				request.setAttribute("hmExistingEmpNameMap", hmExistingEmpNameMap);
				request.setAttribute("hmEmployeeRosterHours", hmEmployeeRosterHours);
				request.setAttribute("hmEmployeeActualHours", hmEmployeeActualHours);
				request.setAttribute("hmLeaveDatesMap", hmLeaveDatesMap);
				
				request.setAttribute("hmServicesCount", hmServicesCount);
				request.setAttribute("alServicesTotal", alServicesTotal);
				
				request.setAttribute("hmEmployeeAttendanceCount", hmEmployeeCount);
				request.setAttribute("hmEmployeesInvolvedInServices", hmEmployeesInvolvedInServices);
				request.setAttribute("hmEmployeesIssues", hmEmployeesIssues);
				
				request.setAttribute("hmWLocationHolidaysColour", hmWLocationHolidaysColour);
				request.setAttribute("hmWLocationHolidaysName", hmWLocationHolidaysName);
				request.setAttribute("hmWLocationHolidaysWeekEnd", hmWLocationHolidaysWeekEnd);
				request.setAttribute("hmWLocationHolidaysWeekEndDates", hmWLocationHolidaysWeekEndDates);
				request.setAttribute("alEmpCheckRosterWeektype", alEmpCheckRosterWeektype);
				request.setAttribute("hmRosterWeekEndDates", hmRosterWeekEndDates);
				
				request.setAttribute("hmEmpWLocation", hmEmpWLocation);
				request.setAttribute("hmLeaveTypes", hmLeaveTypes);
				request.setAttribute("hmBreakPolicy", hmBreakPolicy);
			
			}
			
			request.setAttribute("alDates", alDates);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	
	public String attendanceChartReport(CommonFunctions CF, UtilityFunctions uF, String strUserType,HttpSession session) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmExistingEmpNameMap =  new LinkedHashMap<String, String>();
//			Map<String, String> hmServicesMap =  CF.getServicesMap(con, true);
			List<String> alDates = new ArrayList<String>();
			
			System.out.println("strD1==>"+strD1+"--strD2==>"+strD2);
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			if((strD1==null && strD2==null) || (strD1!=null && strD2!=null && strD1.length()==0 && strD2.length()==0)) {
				for(int i=0; i<3; i++) {
					alDates.add(uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), i)+"", DBDATE, DATE_FORMAT) );
				}
			}
			if(strD1!=null && strD1.length()>0) {
				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
				for(int i=0; i<15; i++) {
					cal.add(Calendar.DATE, -1);
					alDates.add(i, uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT) );
				}
				setD2(null);
			} else if(strD2!=null && strD2.length()>0) {
				for(int i=2; i>=0; i--) { 
					
					cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "dd")));
					cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"))-1);
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "yyyy")));
					
					cal.add(Calendar.DATE, i+1);
					
					
					alDates.add( uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT) );
				}
				setD1(null);
			}
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("SELECT * FROM employee_official_details eod, employee_personal_details epd WHERE eod.emp_id > 0 " +
				"and epd.emp_per_id=eod.emp_id and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) ");
			if(strUserType!=null && strUserType.equals(MANAGER) && !getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append(" and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
			} else {
				if(getF_department()!=null && getF_department().length>0) {
					sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
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
	            } else if((String)session.getAttribute(WLOCATION_ACCESS)!=null && !((String)session.getAttribute(WLOCATION_ACCESS)).trim().equals("") && !((String)session.getAttribute(WLOCATION_ACCESS)).trim().equalsIgnoreCase("NULL")) {
	            	sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            
	            if(uF.parseToInt(getF_org())>0) {
	            	sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if((String)session.getAttribute(ORG_ACCESS)!=null && !((String)session.getAttribute(ORG_ACCESS)).trim().equals("") && !((String)session.getAttribute(ORG_ACCESS)).trim().equalsIgnoreCase("NULL")) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
			}
			if(getStrSearch()!=null && !getStrSearch().trim().equals("") && !getStrSearch().trim().equalsIgnoreCase("NULL")){
				if(flagMiddleName) {
					sbQuery.append(" and (upper(emp_fname)||' '||upper(emp_mname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%' or upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%')");
				} else {
					sbQuery.append(" and upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%'");
				}
			}
			sbQuery.append(" and eod.emp_id in (select emp_per_id from employee_personal_details epd join attendance_details ad " +
			"on ad.emp_id = epd.emp_per_id and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? AND ?)");
			sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
//			System.out.println("pst3=====+>"+pst);
			rs = pst.executeQuery();
			List<String> alEmp = new ArrayList<String>();
			while(rs.next()) {
				hmExistingEmpNameMap.put(rs.getString("emp_id"), rs.getString("emp_fname") +" "+rs.getString("emp_lname"));
				alEmp.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			int nEmpSize = alEmp.size();
			Map<String, Map<String, String>> hmEmployeeCount = new HashMap<String, Map<String, String>>();
			if(nEmpSize > 0) {
				String strEmpIds = StringUtils.join(alEmp.toArray(),",");
	
	//			System.out.println("hmEmployeeRosterHours======>"+hmEmployeeRosterHours);
				
				Map<String, String> hmCount = new HashMap<String, String>();
				String strEmpIdNew = null;
				String strEmpIdOld = null;
				pst = con.prepareStatement("select * from employee_personal_details epd left join attendance_details ad on ad.emp_id = epd.emp_per_id " +
					"and epd.emp_per_id in ("+strEmpIds+") and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? AND ? " +
					"order by emp_id,to_date(in_out_timestamp::text, 'YYYY-MM-DD'),in_out");
				pst.setDate(1, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
//				System.out.println("pst======>"+pst);
				rs = pst.executeQuery();
				
				Map<String, String> hmInActual = new HashMap<String, String>();
				strEmpIdNew = null;
				strEmpIdOld = null;
				while(rs.next()) {
					strEmpIdNew = rs.getString("emp_per_id");
					String strServiceId = rs.getString("service_id");
					hmCount = hmEmployeeCount.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
					if(hmCount == null) {
						hmCount = new HashMap<String, String>();
					}
					
					if("IN".equalsIgnoreCase(rs.getString("in_out")) && hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
						hmInActual.put(strEmpIdNew+"_"+uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_IN", ""+(uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime()));
						
						if(rs.getDouble("early_late")<0) {
							int nCount = uF.parseToInt((String)hmCount.get("EARLY_IN"));
							nCount = nCount+1;
							hmCount.put("EARLY_IN", nCount+"");
							
						} else if(rs.getDouble("early_late")>0) {
							int nCount = uF.parseToInt((String)hmCount.get("LATE_IN"));
							nCount = nCount+1;
							hmCount.put("LATE_IN", nCount+"");
						} else if(rs.getDouble("early_late")==0) {
							int nCount = uF.parseToInt((String)hmCount.get("ONTIME_IN"));
							nCount = nCount+1;
							hmCount.put("ONTIME_IN", nCount+"");
						}
						
					} else if("OUT".equalsIgnoreCase(rs.getString("in_out")) && hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
						long inActual = uF.parseToLong(hmInActual.get(strEmpIdNew+"_"+uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_IN"));
						long outActual = uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
						double dblHoursWorkedActual = uF.parseToDouble(uF.getTimeDiffInHoursMins(inActual, outActual));
						
						if(rs.getDouble("early_late")<0) {
							int nCount = uF.parseToInt((String)hmCount.get("EARLY_OUT"));
							nCount = nCount+1;
							hmCount.put("EARLY_OUT", nCount+"");
							
						} else if(rs.getDouble("early_late")>0) {
							int nCount = uF.parseToInt((String)hmCount.get("LATE_OUT"));
							nCount = nCount+1;
							hmCount.put("LATE_OUT", nCount+"");
							
						} else if(rs.getDouble("early_late")==0) {
							int nCount = uF.parseToInt((String)hmCount.get("ONTIME_OUT"));
							nCount = nCount+1;
							hmCount.put("ONTIME_OUT", nCount+"");
						}
						
					}
					strEmpIdOld = strEmpIdNew;
					
					if(rs.getString("in_out_timestamp")!=null && hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
						hmEmployeeCount.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), hmCount);
					}
				}
				rs.close();
				pst.close();
				
			}
			
			StringBuilder sbDatesAttendance = new StringBuilder();
			StringBuilder sbDatesAttendanceDate = new StringBuilder();
			
			sbDatesAttendance.append("{name: 'Came Early',data: [");
			for(int k=0; k<alDates.size(); k++){
				Map hm = (Map)hmEmployeeCount.get((String)alDates.get(k));
				if(hm==null)hm=new HashMap();
				int nCount = uF.parseToInt((String)hm.get("EARLY_IN"));
				sbDatesAttendance.append(nCount);
				sbDatesAttendanceDate.append("'"+uF.getDateFormat((String)alDates.get(k), DATE_FORMAT, "dd MMM")+"'");
				if(k<alDates.size()-1) {
					sbDatesAttendance.append(",");
					sbDatesAttendanceDate.append(",");
				}
			}
			sbDatesAttendance.append("]},");
			
			sbDatesAttendance.append("{name: 'Left Early',data: [");
			for(int k=0; k<alDates.size(); k++){
				Map hm = (Map)hmEmployeeCount.get((String)alDates.get(k));
				if(hm==null)hm=new HashMap();
				int nCount = uF.parseToInt((String)hm.get("EARLY_OUT"));
				sbDatesAttendance.append(nCount);
				if(k<alDates.size()-1){
					sbDatesAttendance.append(",");
				}
			}
			sbDatesAttendance.append("]},");
			
			sbDatesAttendance.append("{name: 'Came Late',data: [");
			for(int k=0; k<alDates.size(); k++){
				Map hm = (Map)hmEmployeeCount.get((String)alDates.get(k));
				if(hm==null)hm=new HashMap();
				int nCount = uF.parseToInt((String)hm.get("LATE_IN"));
				sbDatesAttendance.append(nCount);
				if(k<alDates.size()-1){
					sbDatesAttendance.append(",");
				}
			}
			sbDatesAttendance.append("]},");
			
			sbDatesAttendance.append("{name: 'Left Late',data: [");
			for(int k=0; k<alDates.size(); k++){
				Map hm = (Map)hmEmployeeCount.get((String)alDates.get(k));
				if(hm==null)hm=new HashMap();
				int nCount = uF.parseToInt((String)hm.get("LATE_OUT"));
				sbDatesAttendance.append(nCount);
				if(k<alDates.size()-1){
					sbDatesAttendance.append(",");
				}
			}
			sbDatesAttendance.append("]},");
			
			sbDatesAttendance.append("{name: 'Came Ontime',data: [");
			for(int k=0; k<alDates.size(); k++){
				Map hm = (Map)hmEmployeeCount.get((String)alDates.get(k));
				if(hm==null)hm=new HashMap();
				int nCount = uF.parseToInt((String)hm.get("ONTIME_IN"));
				sbDatesAttendance.append(nCount);
				if(k<alDates.size()-1){
					sbDatesAttendance.append(",");
				}
			}
			sbDatesAttendance.append("]},");
			
			sbDatesAttendance.append("{name: 'Left Ontime',data: [");
			for(int k=0; k<alDates.size(); k++){
				Map hm = (Map)hmEmployeeCount.get((String)alDates.get(k));
				if(hm==null)hm=new HashMap();
				int nCount = uF.parseToInt((String)hm.get("ONTIME_OUT"));
				sbDatesAttendance.append(nCount);
				if(k<alDates.size()-1){
					sbDatesAttendance.append(",");
				}
			}
			
			sbDatesAttendance.append("]}");
			
			request.setAttribute("sbDatesAttendance", sbDatesAttendance);
			request.setAttribute("sbDatesAttendanceDate", sbDatesAttendanceDate);
			
			
			Map<String, String> hmLeaveTypes = new HashMap<String, String>();
			CF.getLeavesAttributes(con, uF, hmLeaveTypes, null);
			Map<String, Map<String, String>> hmLeaveDates = getActualLeaveDates(con, uF, (String)alDates.get(alDates.size()-1), (String)alDates.get(0), true);
			
			StringBuilder sbDatesLeaves = new StringBuilder();
			StringBuilder sbDatesLeavesDate = new StringBuilder();
			int rowCnt = 0;
			int rowCnt1 = 0;
			Iterator<String> it = hmLeaveTypes.keySet().iterator();
			while (it.hasNext()) {
				String leaveCode = it.next();
				sbDatesLeaves.append("{name: '"+leaveCode+"',data: [");
				Map<String, String> hmLeaveTypeCodewiseCnt = hmLeaveDates.get(leaveCode);
				if(hmLeaveTypeCodewiseCnt != null) {
					for(int k=0; k<alDates.size(); k++) {
						int nCount = uF.parseToInt((String)hmLeaveTypeCodewiseCnt.get(alDates.get(k)));
						sbDatesLeaves.append(nCount);
						if(rowCnt1 == 0) {
							sbDatesLeavesDate.append("'"+uF.getDateFormat(alDates.get(k), DATE_FORMAT, "dd MMM")+"'");
						}
						if(k<alDates.size()-1) {
							sbDatesLeaves.append(",");
							if(rowCnt1 == 0) {
								sbDatesLeavesDate.append(",");
							}
						}
					}
					rowCnt1++;
				}
				sbDatesLeaves.append("]}");
				if(rowCnt <= hmLeaveTypes.size()-1) {
					sbDatesLeaves.append(",");
				}
				rowCnt++;
			}

			request.setAttribute("sbDatesLeaves", sbDatesLeaves);
			request.setAttribute("sbDatesLeavesDate", sbDatesLeavesDate);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	
	public Map<String, Map<String, String>> getActualLeaveDates(Connection con, UtilityFunctions uF, String strD1, String strD2, boolean isPaid) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, Map<String, String>> hmLeaveDates = new HashMap<String, Map<String, String>>();
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,_date,leave_no,lt.leave_type_id,lt.leave_type_code from leave_application_register lar, leave_type lt "
				+ "where lar.leave_type_id = lt.leave_type_id and _date between ? and ? and _type = true ");
			if (isPaid) {
				sbQuery.append(" and is_paid = true ");
			}
			sbQuery.append("and is_modify= false and lar.leave_type_id not in (select leave_type_id from leave_type where is_compensatory = true) "
				+ "and lar.leave_id in (select leave_id from emp_leave_entry) order by lt.leave_type_code,_date,emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
//			double dblLeaveCount = 0;
			while (rs.next()) {
//				String strEmpId = rs.getString("emp_id");
				String strLeaveDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
//				String strLeaveNo = rs.getString("leave_no");
//				String strLeaveTypeId = rs.getString("leave_type_id");
				String strLeaveTypeCode = rs.getString("leave_type_code");

				/**
				 * Leave Dates
				 * */
				Map<String, String> hmLeaveDateTemp = (Map<String, String>) hmLeaveDates.get(strLeaveTypeCode);
				if (hmLeaveDateTemp == null) hmLeaveDateTemp = new HashMap<String, String>();
				int intLeaveCnt = uF.parseToInt(hmLeaveDateTemp.get(strLeaveDate));
				intLeaveCnt++;
				hmLeaveDateTemp.put(strLeaveDate, intLeaveCnt+"");
				hmLeaveDates.put(strLeaveTypeCode, hmLeaveDateTemp);

			}
			rs.close();
			pst.close();
			

			sbQuery = new StringBuilder();
			sbQuery.append("select * from travel_application_register where _date between ? and ? and is_modify= false ");
			if (isPaid) {
				sbQuery.append(" and is_paid = true ");
			}
			sbQuery.append("order by _date,emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
//			dblLeaveCount = 0;
			while (rs.next()) {
//				String strEmpId = rs.getString("emp_id");
				String strTravelDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
//				String strTravelNo = rs.getString("travel_no");

				/**
				 * Leave Dates
				 * */
				Map<String, String> hmLeaveDateTemp = (Map<String, String>) hmLeaveDates.get("T");
				if (hmLeaveDateTemp == null) hmLeaveDateTemp = new HashMap<String, String>();
				int intLeaveCnt = uF.parseToInt(hmLeaveDateTemp.get(strTravelDate));
				intLeaveCnt++;
				hmLeaveDateTemp.put(strTravelDate, intLeaveCnt+"");
				hmLeaveDates.put("T", hmLeaveDateTemp);
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmLeaveDates;
	}
	
	
	
	
	
//	public String attendanceReport(CommonFunctions CF, UtilityFunctions uF, String strUserType,HttpSession session) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//
//		try {
//			con = db.makeConnection(con);
//			
//			Map<String, String> hmExistingEmpNameMap =  new LinkedHashMap<String, String>();
//			Map<String, String> hmServicesMap =  CF.getServicesMap(con, true);
//			List<String> alDates = new ArrayList<String>();
//			
//			
//			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
//			
//			
//			StringBuilder strQuery = new StringBuilder();
//			strQuery.append("SELECT * FROM employee_official_details eod, employee_personal_details epd WHERE epd.emp_per_id=eod.emp_id and is_alive=true ");
//			if(strUserType!=null && strUserType.equals(MANAGER) && !getCurrUserType().equals(strBaseUserType)) {
//				strQuery.append("and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
//			} else {
//				if(getF_department()!=null && getF_department().length>0) {
//					strQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
//	            }
//	            if(getF_service()!=null && getF_service().length>0) {
//	            	strQuery.append(" and (");
//	                for(int i=0; i<getF_service().length; i++) {
//	                	strQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
//	                    
//	                    if(i<getF_service().length-1) {
//	                    	strQuery.append(" OR "); 
//	                    }
//	                }
//	                strQuery.append(" ) ");
//	            } 
//	            
//	            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
//	            	strQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//	            } else if((String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//	            	strQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//				}
//	            
//	            if(uF.parseToInt(getF_org())>0) {
//	            	strQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
//				} else if((String)session.getAttribute(ORG_ACCESS)!=null) {
//					strQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//				}
//			}
//			strQuery.append(" order by emp_fname, emp_lname");
//			
//			pst = con.prepareStatement(strQuery.toString());
////			System.out.println("pst=====+>"+pst);
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				if(rs.getInt("emp_id")<0) {
//					continue;
//				}
//				hmExistingEmpNameMap.put(rs.getString("emp_id"), rs.getString("emp_fname") +" "+rs.getString("emp_lname"));
//			}
//			rs.close();
//			pst.close();
//			
////			System.out.println(" strD1===>"+strD1);
////			System.out.println(" strD2===>"+strD2);
////			System.out.println(" hmExistingEmpNameMap===>"+hmExistingEmpNameMap); 
//			
//			if((strD1==null && strD2==null) || (strD1!=null && strD2!=null && strD1.length()==0 && strD2.length()==0)) {
//				for(int i=0; i<14; i++) {
//					alDates.add(uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), i)+"", DBDATE, DATE_FORMAT) );
//				}
//			}
////			System.out.println(" 1 alDates===>"+alDates.toString());
////			if(strD1!=null && strD2==null) {
//			if(strD1!=null && strD1.length()>0) {
//				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
//				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
//				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
//				for(int i=0; i<14; i++) {
//					cal.add(Calendar.DATE, -1);
//					alDates.add(i, uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT) );
//				}
//				setD2(null);
////				System.out.println(" 2 alDates===>"+alDates.toString());
//			} else			
////			if(strD1==null && strD2!=null) {
//			if(strD2!=null && strD2.length()>0) {
//				for(int i=13; i>=0; i--) {
//					
//					cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "dd")));
//					cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"))-1);
//					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "yyyy")));
//					
//					cal.add(Calendar.DATE, i+1);
//					
//					
//					alDates.add( uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT) );
//				}
//				setD1(null);
////				System.out.println(" 3 alDates===>"+alDates.toString());
//			}
//
//			Map<String, String> hmBreakTypeCode = new HashMap<String, String>();
//			pst = con.prepareStatement("select * from leave_break_type");
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				hmBreakTypeCode.put(rs.getString("break_type_id"), rs.getString("break_type_code"));
//			}
//			rs.close();
//			pst.close();
//			
//			
//			
//			Map<String, String> hmBreakPolicy = new HashMap<String, String>();
//			pst = con.prepareStatement("select * from break_application_register where _date between ? and ?");
//			pst.setDate(2, uF.getDateFormat((String)alDates.get(0), DATE_FORMAT));
//			pst.setDate(1, uF.getDateFormat((String)alDates.get(alDates.size()-1), DATE_FORMAT));
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				hmBreakPolicy.put(rs.getString("emp_id")+"_"+uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_"+rs.getString("_type"), hmBreakTypeCode.get(rs.getString("break_type_id")));
//			}
//			rs.close();
//			pst.close();
//			
//			Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
//			Map<String, Map<String, String>> hmWLocationHolidaysColour = new HashMap<String, Map<String, String>>();
//			Map<String, Map<String, String>> hmWLocationHolidaysName = new HashMap<String, Map<String, String>>();
//			Map<String, Map<String, String>> hmWLocationHolidaysWeekEnd = new HashMap<String, Map<String, String>>();
//			
//			CF.getWLocationHolidayList(con, uF, alDates.get(alDates.size()-1), alDates.get(0), CF, hmWLocationHolidaysColour, hmWLocationHolidaysName, hmWLocationHolidaysWeekEnd);
////			Map hmWeekEndMap = CF.getWeekEndList(con);
//			
//			Map<String, Map<String, String>> hmLeaveDatesType = new HashMap<String, Map<String, String>>();
////			Map<String, Map<String, String>> hmLeaveDatesMap =  CF.getLeaveDates(con, (String)alDates.get(alDates.size()-1), (String)alDates.get(0), CF, hmLeaveDatesType, false, null);
//			Map<String, Map<String, String>> hmLeaveDatesMap =  CF.getActualLeaveDates(con, CF, uF, (String)alDates.get(alDates.size()-1), (String)alDates.get(0), hmLeaveDatesType, false, null);
////			System.out.println("hmWLocationHolidaysColour======>"+hmWLocationHolidaysColour);
////			System.out.println("hmWLocationHolidaysName======>"+hmWLocationHolidaysName);
////			System.out.println("hmWLocationHolidaysWeekEnd======>"+hmWLocationHolidaysWeekEnd);
////			System.out.println("hmLeaveDatesType======>"+hmLeaveDatesType);
////			System.out.println("hmLeaveDatesMap======>"+hmLeaveDatesMap);
//			
//			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
//			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
//			Map<String, Set<String>> hmWLocationHolidaysWeekEndDates = CF.getWeekEndDateList(con, alDates.get(alDates.size()-1), alDates.get(0), CF, uF,hmWeekEndHalfDates,null);
//			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
//			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
//			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
//			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF,alDates.get(0), alDates.get(alDates.size()-1),alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWLocationHolidaysWeekEndDates,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
//			
//			
//			
//			
//			if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))) {
//				pst = con.prepareStatement(selectAttendanceRoster);
//			} else if(strUserType!=null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT))) {
//				pst = con.prepareStatement(selectAttendanceRoster);
//			} else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
//				pst = con.prepareStatement(selectAttendanceRoster);
//			}
//			pst.setDate(1, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
////			System.out.println("pst======>"+pst);
//			rs = pst.executeQuery();
////			log.debug("pst = Roster"+pst);
//			Map<String, Map<String, String>> hmEmployeeRosterHours = new HashMap<String, Map<String, String>>();
//			Map<String, String> hm = new HashMap<String, String>();
//			List<String> alServicesTotal  = new ArrayList<String>();
//			Map<String, String> hmCount = new HashMap<String, String>();
//			Map<String, String> hmServices = new HashMap<String, String>();
//			Map<String, List<String>> hmServicesCount = new HashMap<String, List<String>>();
//			Map<String, Map<String, String>> hmEmployeesInvolvedInServices = new HashMap<String, Map<String, String>>();
//			Map<String, Map<String, String>> hmEmployeesIssues = new HashMap<String, Map<String, String>>();
//			
//			String strEmpIdNew = null;
//			String strEmpIdOld = null;
//			
//			while(rs.next()) {
//				strEmpIdNew = rs.getString("emp_id");
//				
//				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
//					hm = new HashMap<String, String>();
//				}
//				
//				if(rs.getString("_date")!=null) {
//					hm.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("actual_hours"));
//					hm.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_S", rs.getString("_from"));
//					hm.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_E", rs.getString("_to"));
//				}
//				
//				if(hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
//					hmEmployeeRosterHours.put(strEmpIdNew, hm);
//				}
//				
//				strEmpIdOld = strEmpIdNew;
//			}
//			rs.close();
//			pst.close();
////			System.out.println("hmEmployeeRosterHours======>"+hmEmployeeRosterHours);
//			
//			if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) ||
//					strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))) {
//				pst = con.prepareStatement(selectAttendanceActual);
//			} else if(strUserType!=null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT))) {
//				pst = con.prepareStatement(selectAttendanceActual);
//			} else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
//				pst = con.prepareStatement(selectAttendanceActual);
//			}
//			pst.setDate(1, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
////			System.out.println("pst======>"+pst);
//			rs = pst.executeQuery();
//			Map<String, Map<String, String>> hmEmployeeCount = new HashMap<String, Map<String, String>>();
//			
//			Map<String, Map<String, String>> hmEmployeeActualHours = new HashMap<String, Map<String, String>>();
//			Map<String, Map<String, String>> hmEmployeeService = new HashMap<String, Map<String, String>>();
//			hm = new HashMap<String, String>();
//			strEmpIdNew = null;
//			strEmpIdOld = null;
//			while(rs.next()) {
//				strEmpIdNew = rs.getString("emp_per_id");
//				String strServiceId = rs.getString("service_id");
//				
//				if(hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
//					
//					Map<String, String> hmTemp = hmEmployeesInvolvedInServices.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
//					if(hmTemp==null)hmTemp=new HashMap<String, String>();
//					int nEmployeeInServices = uF.parseToInt((String)hmTemp.get(strServiceId));
//					nEmployeeInServices++;
//					hmTemp.put(strServiceId, nEmployeeInServices+"");
//					
//					hmEmployeesInvolvedInServices.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), hmTemp);
//					
//				}
//				
//				if(hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
//					Map<String, String> hmTemp = hmEmployeesIssues.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
//					if(hmTemp==null)hmTemp=new HashMap<String, String>();
//					
//					if(rs.getInt("approved")==-2) {
//					
//						int nPendingCount = uF.parseToInt((String)hmTemp.get("PENDING"));
//						nPendingCount++;
//						hmTemp.put("PENDING", nPendingCount+"");
//						
//					} else if(rs.getInt("approved")==1) {
//						int nApprovedCount = uF.parseToInt((String)hmTemp.get("APPROVED"));
//						nApprovedCount++;
//						hmTemp.put("APPROVED", nApprovedCount+"");
//					}
//					
//					
//					hmEmployeesIssues.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), hmTemp);
//					
//				}
//				
//				
//				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
//					hm = new HashMap<String, String>();
//					hmServices = new HashMap<String, String>();
//				}
//				
//				if(!alServicesTotal.contains(strServiceId)) {
//					alServicesTotal.add(strServiceId);
//				}
//				
//				
//				
//				hmCount = hmEmployeeCount.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
//				if(hmCount==null) {
//					hmCount = new HashMap<String, String>();
//				}
//				
//				
//				if("IN".equalsIgnoreCase(rs.getString("in_out")) && hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
//					hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_IN_"+strServiceId, rs.getString("in_out_timestamp"));
//					if(rs.getDouble("early_late")<0) {
//						hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_EARLY_IN_"+strServiceId, rs.getString("early_late"));
//						
//						int nCount = uF.parseToInt((String)hmCount.get("EARLY_IN"));
//						nCount = nCount+1;
//						hmCount.put("EARLY_IN_"+strServiceId, nCount+"");
//						
//					} else if(rs.getDouble("early_late")>0) {
//						hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_LATE_IN_"+strServiceId, rs.getString("early_late"));
//						
//						int nCount = uF.parseToInt((String)hmCount.get("LATE_IN_"+strServiceId));
//						nCount = nCount+1;
//						hmCount.put("LATE_IN_"+strServiceId, nCount+"");
//						
//						
//					} else if(rs.getDouble("early_late")==0) {
//						hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_ONTIME_IN_"+strServiceId, rs.getString("early_late"));
//						
//						int nCount = uF.parseToInt((String)hmCount.get("ONTIME_IN_"+strServiceId));
//						nCount = nCount+1;
//						hmCount.put("ONTIME_IN_"+strServiceId, nCount+"");
//					}
//					
//					hmServices.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), rs.getString("service_id"));
//					
//					List<String> alServicesCount =hmServicesCount.get(strEmpIdNew);
//					if(alServicesCount==null) {
//						alServicesCount = new ArrayList<String>();
//					}
//					if(!alServicesCount.contains(strServiceId)) {
//						alServicesCount.add(strServiceId);
//					}
//					hmServicesCount.put(strEmpIdNew, alServicesCount);
//					
//				} else if("OUT".equalsIgnoreCase(rs.getString("in_out")) && hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
//					hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat())+"_OUT_"+strServiceId, rs.getString("in_out_timestamp"));
//					
//					if(hm.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_WH_"+strServiceId)) {
//						double dbl = uF.parseToDouble((String)hm.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat())+"_WH"));
//						dbl += rs.getDouble("hours_worked");
//						hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_WH_"+strServiceId, uF.formatIntoTwoDecimal(dbl)); 
//					} else {
//						hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_WH_"+strServiceId, uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("hours_worked"))));
//					}
//					
//					if(rs.getDouble("early_late")<0) {
//						hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_EARLY_OUT_"+strServiceId, rs.getString("early_late"));
//						
//						int nCount = uF.parseToInt((String)hmCount.get("EARLY_OUT_"+strServiceId));
//						nCount = nCount+1;
//						hmCount.put("EARLY_OUT_"+strServiceId, nCount+"");
//						
//					} else if(rs.getDouble("early_late")>0) {
//						hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_LATE_OUT_"+strServiceId, rs.getString("early_late"));
//						
//						int nCount = uF.parseToInt((String)hmCount.get("LATE_OUT_"+strServiceId));
//						nCount = nCount+1;
//						hmCount.put("LATE_OUT_"+strServiceId, nCount+"");
//						
//					} else if(rs.getDouble("early_late")==0) {
//						hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_ONTIME_OUT_"+strServiceId, rs.getString("early_late"));
//						
//						int nCount = uF.parseToInt((String)hmCount.get("ONTIME_OUT_"+strServiceId));
//						nCount = nCount+1;
//						hmCount.put("ONTIME_OUT_"+strServiceId, nCount+"");
//						
//						
//					}
//					
//					hmServices.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), rs.getString("service_id"));
//					List<String> alServicesCount = hmServicesCount.get(strEmpIdNew);
//					if(alServicesCount==null) {
//						alServicesCount = new ArrayList<String>();
//					}
//					if(!alServicesCount.contains(rs.getString("service_id"))) {
//						alServicesCount.add(rs.getString("service_id"));
//					}
//					hmServicesCount.put(strEmpIdNew, alServicesCount);
//				}
//				
//				
//				if(hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
//					hmEmployeeActualHours.put(strEmpIdNew, hm);
//					hmEmployeeService.put(strEmpIdNew, hmServices);
//				}
//				
//				
//				
//				strEmpIdOld = strEmpIdNew;
//				
//				if(rs.getString("in_out_timestamp")!=null && hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
//					hmEmployeeCount.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), hmCount);
//				}
//				
//				
//			}
//			rs.close();
//			pst.close();
////			System.out.println("hmEmployeesInvolvedInServices======>"+hmEmployeesInvolvedInServices);
////			System.out.println("hmEmployeesIssues======>"+hmEmployeesIssues);
////			System.out.println("hmServices======>"+hmServices);
////			System.out.println("hmServicesCount======>"+hmServicesCount);
////			System.out.println("hmEmployeeCount======>"+hmEmployeeCount);
////			System.out.println("hmEmployeeActualHours======>"+hmEmployeeActualHours);
////			System.out.println("hmEmployeeService======>"+hmEmployeeService);
//			
//			Map<String, String> hmLeaveTypes = new HashMap<String, String>();
//			CF.getLeavesAttributes(con, uF, hmLeaveTypes, null);
//			
//			
////			System.out.println("hmLeaveTypes======>"+hmLeaveTypes);
//			
//		
//			
//			request.setAttribute("hmEmployeeService", hmEmployeeService);
//			request.setAttribute("hmServicesMap", hmServicesMap);
//			request.setAttribute("hmExistingEmpNameMap", hmExistingEmpNameMap);
//			request.setAttribute("hmEmployeeRosterHours", hmEmployeeRosterHours);
//			request.setAttribute("hmEmployeeActualHours", hmEmployeeActualHours);
//			request.setAttribute("hmLeaveDatesMap", hmLeaveDatesMap);
//			request.setAttribute("alDates", alDates);
//			request.setAttribute("hmServicesCount", hmServicesCount);
//			request.setAttribute("alServicesTotal", alServicesTotal);
//			
//			request.setAttribute("hmEmployeeAttendanceCount", hmEmployeeCount);
//			request.setAttribute("hmEmployeesInvolvedInServices", hmEmployeesInvolvedInServices);
//			request.setAttribute("hmEmployeesIssues", hmEmployeesIssues);
//			
//			request.setAttribute("hmWLocationHolidaysColour", hmWLocationHolidaysColour);
//			request.setAttribute("hmWLocationHolidaysName", hmWLocationHolidaysName);
//			request.setAttribute("hmWLocationHolidaysWeekEnd", hmWLocationHolidaysWeekEnd);
//			request.setAttribute("hmWLocationHolidaysWeekEndDates", hmWLocationHolidaysWeekEndDates);
//			request.setAttribute("alEmpCheckRosterWeektype", alEmpCheckRosterWeektype);
//			request.setAttribute("hmRosterWeekEndDates", hmRosterWeekEndDates);
//			
////			request.setAttribute("hmWeekEndMap", hmWeekEndMap);
//			 
//			
//			request.setAttribute("hmEmpWLocation", hmEmpWLocation);
//			request.setAttribute("hmLeaveTypes", hmLeaveTypes);
//			request.setAttribute("hmBreakPolicy", hmBreakPolicy);
//			
////			log.debug("hmServicesCount==>"+hmServicesCount);
//			
//			
////			System.out.println("hmWLocationHolidaysWeekEndDates===>"+hmWLocationHolidaysWeekEndDates);
//			
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return SUCCESS;
//	}
	
	public void attendanceReportForOneWeek(CommonFunctions CF, UtilityFunctions uF, String strUserType,HttpSession session) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			
			con = db.makeConnection(con);
			
			Map<String, String> hmExistingEmpNameMap =  new LinkedHashMap<String, String>();
			Map<String, String> hmServicesMap =  CF.getServicesMap(con, true);
			List<String> alDates = new ArrayList<String>();
			
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			
			
			StringBuilder strQuery = new StringBuilder();
			
			strQuery.append("SELECT * FROM employee_official_details eod, employee_personal_details epd WHERE epd.emp_per_id=eod.emp_id and is_alive=true ");
			if(strUserType!=null && strUserType.equals(MANAGER) && !getCurrUserType().equals(strBaseUserType)) {
				strQuery.append("and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
			}
			
			/*if(uF.parseToInt(getF_strWLocation())>0) {
				strQuery.append("and wlocation_id="+uF.parseToInt(getF_strWLocation()));
			}if(uF.parseToInt(getF_department())>0) {
				strQuery.append("and depart_id="+uF.parseToInt(getF_department()));				
			}if(uF.parseToInt(getF_service())>0) {
				strQuery.append("and service_id like '%"+uF.parseToInt(getF_service())+",%'");
			}*/
			if(getF_department()!=null && getF_department().length>0) {
				strQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            
            if(getF_service()!=null && getF_service().length>0) {
            	strQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                	strQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    if(i<getF_service().length-1) {
                    	strQuery.append(" OR "); 
                    }
                }
                strQuery.append(" ) ");
            } 
            
            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
            	strQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            } else if((String)session.getAttribute(WLOCATION_ACCESS)!=null) {
            	strQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0) {
            	strQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if((String)session.getAttribute(ORG_ACCESS)!=null) {
				strQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(uF.parseToInt(getF_org())>0) {
				strQuery.append(" and org_id ="+uF.parseToInt(getF_org()));
			}
			strQuery.append(" order by emp_fname, emp_lname");
			
			
			pst = con.prepareStatement(strQuery.toString());
			rs = pst.executeQuery();
			
//			System.out.println("pst=====+>"+pst);
			
			while(rs.next()) {
				if(rs.getInt("emp_id")<0) {
					continue;
				}
				hmExistingEmpNameMap.put(rs.getString("emp_id"), rs.getString("emp_fname") +" "+rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();
			
			if((strD1==null && strD2==null) || (strD1!=null && strD2!=null && strD1.length()==0 && strD2.length()==0)) {
				for(int i=0; i<7; i++) {
					alDates.add(uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), i)+"", DBDATE, DATE_FORMAT) );
				}
			}
			
//			if(strD1!=null && strD2==null) {
			if(strD1!=null && strD1.length()>0) {
				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
				for(int i=0; i<7; i++) {
					cal.add(Calendar.DATE, -1);
					alDates.add(i, uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT) );
				}
				setD2(null);
			} else
			
			
//			if(strD1==null && strD2!=null) {
			if(strD2!=null && strD2.length()>0) {
				 
				
				for(int i=13; i>=0; i--) {
					
					cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "dd")));
					cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"))-1);
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "yyyy")));
					
					cal.add(Calendar.DATE, i+1);
					
					
					alDates.add( uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT) );
				}
				setD1(null);
			}

			Map<String, String> hmBreakTypeCode = new HashMap<String, String>();
			pst = con.prepareStatement("select * from leave_break_type");
			rs = pst.executeQuery();
			while(rs.next()) {
				hmBreakTypeCode.put(rs.getString("break_type_id"), rs.getString("break_type_code"));
			}
			rs.close();
			pst.close();
			
			
			
			Map<String, String> hmBreakPolicy = new HashMap<String, String>();
			pst = con.prepareStatement("select * from break_application_register where _date between ? and ?");
			pst.setDate(2, uF.getDateFormat((String)alDates.get(0), DATE_FORMAT));
			pst.setDate(1, uF.getDateFormat((String)alDates.get(alDates.size()-1), DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmBreakPolicy.put(rs.getString("emp_id")+"_"+uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_"+rs.getString("_type"), hmBreakTypeCode.get(rs.getString("break_type_id")));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
			Map<String, Map<String, String>> hmWLocationHolidaysColour = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmWLocationHolidaysName = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmWLocationHolidaysWeekEnd = new HashMap<String, Map<String, String>>();
			
			CF.getWLocationHolidayList(con, uF, alDates.get(alDates.size()-1), alDates.get(0), CF, hmWLocationHolidaysColour, hmWLocationHolidaysName, hmWLocationHolidaysWeekEnd);
//			Map hmWeekEndMap = CF.getWeekEndList(con);
			
			
			Map<String, Map<String, String>> hmLeaveDatesType = new HashMap<String, Map<String, String>>();
//			Map<String, Map<String, String>> hmLeaveDatesMap =  CF.getLeaveDates(con, (String)alDates.get(alDates.size()-1), (String)alDates.get(0), CF, hmLeaveDatesType, false, null);
			Map<String, Map<String, String>> hmLeaveDatesMap =  CF.getActualLeaveDates(con, CF, uF, (String)alDates.get(alDates.size()-1), (String)alDates.get(0), hmLeaveDatesType, false, null);
			
//			System.out.println("hmWLocationHolidaysColour======>"+hmWLocationHolidaysColour);
//			System.out.println("hmWLocationHolidaysName======>"+hmWLocationHolidaysName);
//			System.out.println("hmWLocationHolidaysWeekEnd======>"+hmWLocationHolidaysWeekEnd);
			
			Map<String, Set<String>> hmWLocationHolidaysWeekEndDates = CF.getWeekEndDateList(con, alDates.get(alDates.size()-1), alDates.get(0), CF, uF,null,null);
			
			if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))) {
				pst = con.prepareStatement(selectAttendanceRoster);
			} else if(strUserType!=null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT))) {
				pst = con.prepareStatement(selectAttendanceRoster);
			} else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				pst = con.prepareStatement(selectAttendanceRoster);
			}
			
			pst.setDate(1, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			rs = pst.executeQuery();
//			log.debug("pst = Roster"+pst);
			
			Map<String, Map<String, String>> hmEmployeeRosterHours = new HashMap<String, Map<String, String>>();
			Map<String, String> hm = new HashMap<String, String>();
			List<String> alServicesTotal  = new ArrayList<String>();
			Map<String, String> hmCount = new HashMap<String, String>();
			Map<String, String> hmServices = new HashMap<String, String>();
			
			Map<String, List<String>> hmServicesCount = new HashMap<String, List<String>>();
			Map<String, Map<String, String>> hmEmployeesInvolvedInServices = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeesIssues = new HashMap<String, Map<String, String>>();
			
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			
			while(rs.next()) {
				strEmpIdNew = rs.getString("emp_id");
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					hm = new HashMap<String, String>();
				}
				
				if(rs.getString("_date")!=null) {
					hm.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("actual_hours"));
					hm.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_S", rs.getString("_from"));
					hm.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_E", rs.getString("_to"));
				}
				
				if(hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
					hmEmployeeRosterHours.put(strEmpIdNew, hm);
				}
				
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
			
			if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))) {
				pst = con.prepareStatement(selectAttendanceActual);
			} else if(strUserType!=null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT))) {
				pst = con.prepareStatement(selectAttendanceActual);
			} else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				pst = con.prepareStatement(selectAttendanceActual);
			}
			
			
			pst.setDate(1, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmEmployeeCount = new HashMap<String, Map<String, String>>();
			
			Map<String, Map<String, String>> hmEmployeeActualHours = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeeService = new HashMap<String, Map<String, String>>();
			Map<String, String> hmInActual = new HashMap<String, String>();
			hm = new HashMap<String, String>();
			strEmpIdNew = null;
			strEmpIdOld = null;
			while(rs.next()) {
				strEmpIdNew = rs.getString("emp_per_id");
				String strServiceId = rs.getString("service_id");
				
				
				
				if(hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
					
					Map<String, String> hmTemp = hmEmployeesInvolvedInServices.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
					if(hmTemp==null)hmTemp=new HashMap<String, String>();
					int nEmployeeInServices = uF.parseToInt((String)hmTemp.get(strServiceId));
					nEmployeeInServices++;
					hmTemp.put(strServiceId, nEmployeeInServices+"");
					
					hmEmployeesInvolvedInServices.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), hmTemp);
					
				}
				
				
				
				if(hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
					Map<String, String> hmTemp = hmEmployeesIssues.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
					if(hmTemp==null)hmTemp=new HashMap<String, String>();
					
					if(rs.getInt("approved")==-2) {
					
						int nPendingCount = uF.parseToInt((String)hmTemp.get("PENDING"));
						nPendingCount++;
						hmTemp.put("PENDING", nPendingCount+"");
						
					} else if(rs.getInt("approved")==1) {
						int nApprovedCount = uF.parseToInt((String)hmTemp.get("APPROVED"));
						nApprovedCount++;
						hmTemp.put("APPROVED", nApprovedCount+"");
					}
					
					
					hmEmployeesIssues.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), hmTemp);
					
				}
				
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					hm = new HashMap<String, String>();
					hmServices = new HashMap<String, String>();
				}
				
				if(!alServicesTotal.contains(strServiceId)) {
					alServicesTotal.add(strServiceId);
				}
				
				
				
				hmCount = hmEmployeeCount.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
				if(hmCount==null) {
					hmCount = new HashMap<String, String>();
				}
				
				
				if("IN".equalsIgnoreCase(rs.getString("in_out")) && hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
					hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_IN_"+strServiceId, rs.getString("in_out_timestamp"));
					
					hmInActual.put(strEmpIdNew+"_"+uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_IN_"+strServiceId, ""+(uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime()));
					
					if(rs.getDouble("early_late")<0) {
						hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_EARLY_IN_"+strServiceId, rs.getString("early_late"));
						
						int nCount = uF.parseToInt((String)hmCount.get("EARLY_IN"));
						nCount = nCount+1;
						hmCount.put("EARLY_IN_"+strServiceId, nCount+"");
						
					} else if(rs.getDouble("early_late")>0) {
						hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_LATE_IN_"+strServiceId, rs.getString("early_late"));
						
						int nCount = uF.parseToInt((String)hmCount.get("LATE_IN_"+strServiceId));
						nCount = nCount+1;
						hmCount.put("LATE_IN_"+strServiceId, nCount+"");
						
						
					} else if(rs.getDouble("early_late")==0) {
						hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_ONTIME_IN_"+strServiceId, rs.getString("early_late"));
						
						int nCount = uF.parseToInt((String)hmCount.get("ONTIME_IN_"+strServiceId));
						nCount = nCount+1;
						hmCount.put("ONTIME_IN_"+strServiceId, nCount+"");
					}
					
					hmServices.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), rs.getString("service_id"));
					
					List<String> alServicesCount =hmServicesCount.get(strEmpIdNew);
					if(alServicesCount==null) {
						alServicesCount = new ArrayList<String>();
					}
					if(!alServicesCount.contains(strServiceId)) {
						alServicesCount.add(strServiceId);
					}
					hmServicesCount.put(strEmpIdNew, alServicesCount);
					
				} else if("OUT".equalsIgnoreCase(rs.getString("in_out")) && hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
					hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat())+"_OUT_"+strServiceId, rs.getString("in_out_timestamp"));
					
					long inActual = uF.parseToLong(hmInActual.get(strEmpIdNew+"_"+uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_IN_"+strServiceId));
					long outActual = uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
					double dblHoursWorkedActual = uF.parseToDouble(uF.getTimeDiffInHoursMins(inActual, outActual));
					
					if(hm.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_WH_"+strServiceId)) {
						double dbl = uF.parseToDouble((String)hm.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat())+"_WH"));
//						dbl += rs.getDouble("hours_worked");
						dbl += dblHoursWorkedActual;
						hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_WH_"+strServiceId, uF.formatIntoTwoDecimal(dbl)); 
					} else {
//						hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_WH_"+strServiceId, uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("hours_worked"))));
						hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_WH_"+strServiceId, uF.formatIntoTwoDecimal(dblHoursWorkedActual));
					}
					
					if(rs.getDouble("early_late")<0) {
						hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_EARLY_OUT_"+strServiceId, rs.getString("early_late"));
						
						int nCount = uF.parseToInt((String)hmCount.get("EARLY_OUT_"+strServiceId));
						nCount = nCount+1;
						hmCount.put("EARLY_OUT_"+strServiceId, nCount+"");
						
					} else if(rs.getDouble("early_late")>0) {
						hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_LATE_OUT_"+strServiceId, rs.getString("early_late"));
						
						int nCount = uF.parseToInt((String)hmCount.get("LATE_OUT_"+strServiceId));
						nCount = nCount+1;
						hmCount.put("LATE_OUT_"+strServiceId, nCount+"");
						
					} else if(rs.getDouble("early_late")==0) {
						hm.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_ONTIME_OUT_"+strServiceId, rs.getString("early_late"));
						
						int nCount = uF.parseToInt((String)hmCount.get("ONTIME_OUT_"+strServiceId));
						nCount = nCount+1;
						hmCount.put("ONTIME_OUT_"+strServiceId, nCount+"");
						
						
					}
					
					hmServices.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), rs.getString("service_id"));
					List<String> alServicesCount = hmServicesCount.get(strEmpIdNew);
					if(alServicesCount==null) {
						alServicesCount = new ArrayList<String>();
					}
					if(!alServicesCount.contains(rs.getString("service_id"))) {
						alServicesCount.add(rs.getString("service_id"));
					}
					hmServicesCount.put(strEmpIdNew, alServicesCount);
				}
				
				
				if(hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
					hmEmployeeActualHours.put(strEmpIdNew, hm);
					hmEmployeeService.put(strEmpIdNew, hmServices);
				}
				
				
				
				strEmpIdOld = strEmpIdNew;
				
				if(rs.getString("in_out_timestamp")!=null && hmExistingEmpNameMap.containsKey(strEmpIdNew)) {
					hmEmployeeCount.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), hmCount);
				}
				
				
			}
			rs.close();
			pst.close();
			
			
			Map<String, String> hmLeaveTypes = new HashMap<String, String>();
			CF.getLeavesAttributes(con, uF, hmLeaveTypes, null);
			
			request.setAttribute("hmEmployeeService", hmEmployeeService);
			request.setAttribute("hmServicesMap", hmServicesMap);
			request.setAttribute("hmExistingEmpNameMap", hmExistingEmpNameMap);
			request.setAttribute("hmEmployeeRosterHours", hmEmployeeRosterHours);
			request.setAttribute("hmEmployeeActualHours", hmEmployeeActualHours);
			request.setAttribute("hmLeaveDatesMap", hmLeaveDatesMap);
			request.setAttribute("alDates", alDates);
//			System.out.println("2 alDates"+ alDates);
			request.setAttribute("hmServicesCount", hmServicesCount);
			request.setAttribute("alServicesTotal", alServicesTotal);
			
			request.setAttribute("hmEmployeeAttendanceCount", hmEmployeeCount);
			request.setAttribute("hmEmployeesInvolvedInServices", hmEmployeesInvolvedInServices);
			request.setAttribute("hmEmployeesIssues", hmEmployeesIssues);
			
			request.setAttribute("hmWLocationHolidaysColour", hmWLocationHolidaysColour);
			request.setAttribute("hmWLocationHolidaysName", hmWLocationHolidaysName);
			request.setAttribute("hmWLocationHolidaysWeekEnd", hmWLocationHolidaysWeekEnd);
			request.setAttribute("hmWLocationHolidaysWeekEndDates", hmWLocationHolidaysWeekEndDates);
			
//			request.setAttribute("hmWeekEndMap", hmWeekEndMap);
			 
			
			request.setAttribute("hmEmpWLocation", hmEmpWLocation);
			request.setAttribute("hmLeaveTypes", hmLeaveTypes);
			request.setAttribute("hmBreakPolicy", hmBreakPolicy);
			
			
			
//			log.debug("hmServicesCount==>"+hmServicesCount);
			
			
//			System.out.println("hmWLocationHolidaysWeekEndDates===>"+hmWLocationHolidaysWeekEndDates);
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;


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

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public String getAttendanceFilter() {
		return attendanceFilter;
	}

	public void setAttendanceFilter(String attendanceFilter) {
		this.attendanceFilter = attendanceFilter;
	}

	public List<FillAttendanceFilter> getAttendanceFilterList() {
		return attendanceFilterList;
	}

	public void setAttendanceFilterList(
			List<FillAttendanceFilter> attendanceFilterList) {
		this.attendanceFilterList = attendanceFilterList;
	}

	public String getD2() {
		return D2;
	}

	public void setD2(String d2) {
		D2 = d2;
	}

	public String getD1() {
		return D1;
	}

	public void setD1(String d1) {
		D1 = d1;
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

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getProPage() {
		return proPage;
	}

	public void setProPage(String proPage) {
		this.proPage = proPage;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}

	public String getStrSearch() {
		return strSearch;
	}

	public void setStrSearch(String strSearch) {
		this.strSearch = strSearch;
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

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
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

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
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
	
}
