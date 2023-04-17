package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
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

public class ProjectCommitmentStatusReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strEmpId;
	String strUserType;

	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_service;
	String[] f_level;
	String[] pro_id;
	String[] client;
	
	String paycycle;
	String strStartDate;
	String strEndDate;

	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillLevel> levelList;
	List<FillPayCycles> paycycleList;
	String freqENDDATE;
	String qfreqENDDATE;
	public String execute() throws Exception {
		session = request.getSession();

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/task/ProjectCommitmentStatusReport.jsp");
		request.setAttribute(TITLE, "Project Commitment Status Report");
		strEmpId = (String) session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(BASEUSERTYPE);

		if (getF_org() == null) {
			setF_org((String) session.getAttribute(ORGID));
		}

		viewProjectWiseEmployeeProgress(uF);

		return loadEmployeeWiseProjectProgress(uF);

	}
//	private void viewEmployeeWiseProjectProgress(UtilityFunctions uF) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//
//		try {
//
//			con = db.makeConnection(con);
//
//			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
//			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
//
//			System.out.println("EWPP/98--strStartDate=" + getStrStartDate());
//			if (getStrStartDate() != null && getStrStartDate().equalsIgnoreCase("NULL")) {
//				setStrStartDate(null);
//				setStrEndDate(null);
//			}
//			if (getStrEndDate() != null && getStrEndDate().equalsIgnoreCase("NULL")) {
//				setStrStartDate(null);
//				setStrEndDate(null);
//			}
//			if (getStrStartDate() == null && getStrEndDate() == null) {
//				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
//				int nMaxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//				int nMinDate = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
//
//				setStrStartDate(uF.getDateFormat(nMinDate + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
//				setStrEndDate(uF.getDateFormat(nMaxDate + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
//			}
//
//			Map<String, String> hmGradeDesigId = CF.getGradeDesig(con);
//			Map<String, String> hmDesigName = CF.getDesigMap(con);
//			Map<String, String> hmDepartName = CF.getDeptMap(con);
//
//			StringBuilder sbQuery = new StringBuilder();
//
//			sbQuery.append(
//					"select emp_per_id, empcode, emp_fname, emp_mname, emp_lname,depart_id,grade_id from employee_personal_details epd, employee_official_details eod WHERE epd.is_alive=true and epd.emp_per_id=eod.emp_id and approved_flag=true ");
//			if (strUserType != null && strUserType.equals(MANAGER)) {
//				sbQuery.append(" and (supervisor_emp_id = " + uF.parseToInt((String) session.getAttribute(EMPID)) + " or eod.emp_id = "
//						+ uF.parseToInt((String) session.getAttribute(EMPID)) + ")");
//			}
//			if (uF.parseToInt(getF_org()) > 0) {
//				sbQuery.append(" and org_id in (" + getF_org() + ")");
//			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(ORG_ACCESS) != null) {
//				sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
//			}
//			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
//				sbQuery.append(" and wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ")");
//			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(WLOCATION_ACCESS) != null) {
//				sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
//			}
//			if (getF_department() != null && getF_department().length > 0) {
//				sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
//			}
//			if (getF_service() != null && getF_service().length > 0) {
//				sbQuery.append(" and (");
//				for (int i = 0; i < getF_service().length; i++) {
//					sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
//
//					if (i < getF_service().length - 1) {
//						sbQuery.append(" OR ");
//					}
//				}
//				sbQuery.append(" ) ");
//			}
//			if (getF_level() != null && getF_level().length > 0) {
//				sbQuery.append(
//						" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "
//								+ StringUtils.join(getF_level(), ",") + ") ) ");
//			}
//
//			pst = con.prepareStatement(sbQuery.toString());
//			rs = pst.executeQuery();
//			System.out.println("ESR/170--pst=" + pst);
//			List<String> alEmpIds = new ArrayList<String>();
//
//			Map<String, String> hmEmpPersonalDetails = new HashMap<String, String>();
//			while (rs.next()) {
//				alEmpIds.add(rs.getString("emp_per_id"));
//				String strMiddleName = "";
//				if (flagMiddleName) {
//					if (rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length() > 0) {
//						strMiddleName = " " + rs.getString("emp_mname");
//					}
//				}
//				String strEmpName = rs.getString("emp_fname") + uF.showData(strMiddleName, "") + " " + rs.getString("emp_lname");
//				hmEmpPersonalDetails.put(rs.getString("emp_per_id") + "_EMPNAME", strEmpName);
//				hmEmpPersonalDetails.put(rs.getString("emp_per_id") + "_EMPDESIG", hmDesigName.get(hmGradeDesigId.get(rs.getString("grade_id"))));
//				hmEmpPersonalDetails.put(rs.getString("emp_per_id") + "_EMPDEPT", hmDepartName.get(rs.getString("depart_id")));
//
//			}
//			rs.close();
//			pst.close();
//
//			Map<String, String> hmEmpWiseTaskInfo = new HashMap<String, String>();
//			Map<String, String> hmProName = CF.getProjectNameMap(con);
//			List<List<String>> reportList = new ArrayList<List<String>>();
//			Map<String, String> hmEmpGrossSalary = CF.getEmpGrossSalary(uF, CF, con, getStrStartDate(), "H");
//			System.out.println("EWPP/182--strStartDate1=" + getStrStartDate());
//			for (int i = 0; alEmpIds != null && !alEmpIds.isEmpty() && i < alEmpIds.size(); i++) {
//
//				pst = con.prepareStatement(
//						"select task_id, activity_name, approve_status, pro_id from activity_info where resource_ids like '%," + alEmpIds.get(i) + ",%' ");
//
//				// System.out.println("pst ==>> " + pst);
//				rs = pst.executeQuery();
//
//				StringBuilder sbTaskIds = null;
//				while (rs.next()) {
//					if (sbTaskIds == null) {
//						sbTaskIds = new StringBuilder();
//						sbTaskIds.append(rs.getString("task_id"));
//					} else {
//						sbTaskIds.append("," + rs.getString("task_id"));
//					}
//					hmEmpWiseTaskInfo.put(rs.getString("task_id") + "_TASKNAME", rs.getString("activity_name"));
//
//					if (rs.getString("approve_status").equalsIgnoreCase("approved")) {
//						hmEmpWiseTaskInfo.put(rs.getString("task_id") + "_TASKSTATUS", "Close");
//					} else {
//						hmEmpWiseTaskInfo.put(rs.getString("task_id") + "_TASKSTATUS", "Open");
//					}
//
//					hmEmpWiseTaskInfo.put(rs.getString("task_id") + "_PROJECTNAME", hmProName.get(rs.getString("pro_id")));
//					String clientId = CF.getClientIdByProjectTaskId(con, uF, rs.getString("task_id"), alEmpIds.get(i));
//					String clientName = CF.getClientNameById(con, clientId);
//					hmEmpWiseTaskInfo.put(rs.getString("task_id") + "_CLIENTNAME", clientName);
//				}
//				rs.close();
//				pst.close();
//
//				if (sbTaskIds != null && !sbTaskIds.equals("")) {
//					StringBuilder sbQue = new StringBuilder();
//					sbQue.append("select activity_id,sum(actual_hrs) as hrs from task_activity where emp_id = ? and activity_id  in (" + sbTaskIds.toString()
//							+ ") ");
//					if (getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null
//							&& !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
//						sbQue.append(" and task_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"
//								+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
//					}
//					sbQue.append("group by activity_id");
//
//					pst = con.prepareStatement(sbQue.toString());
//					pst.setInt(1, uF.parseToInt(alEmpIds.get(i)));
//					rs = pst.executeQuery();
//					while (rs.next()) {
//						List<String> innerList = new ArrayList<String>();
//						innerList.add(hmEmpPersonalDetails.get(alEmpIds.get(i) + "_EMPDEPT")); // 0
//						innerList.add(hmEmpPersonalDetails.get(alEmpIds.get(i) + "_EMPNAME")); // 1
//						innerList.add(hmEmpPersonalDetails.get(alEmpIds.get(i) + "_EMPDESIG")); // 2
//						innerList.add(hmEmpWiseTaskInfo.get(rs.getString("activity_id") + "_CLIENTNAME")); // 3
//						innerList.add(hmEmpWiseTaskInfo.get(rs.getString("activity_id") + "_PROJECTNAME")); // 4
//						innerList.add(hmEmpWiseTaskInfo.get(rs.getString("activity_id") + "_TASKNAME")); // 5
//						innerList.add(hmEmpWiseTaskInfo.get(rs.getString("activity_id") + "_TASKSTATUS")); // 6
//						innerList.add(rs.getString("hrs")); // 7
//						innerList.add(""); // 8 Billing
//						double cost = uF.parseToDouble(rs.getString("hrs")) * uF.parseToDouble(hmEmpGrossSalary.get(alEmpIds.get(i)));
//						innerList.add(uF.formatIntoTwoDecimal(cost)); // 9
//						innerList.add(uF.formatIntoTwoDecimal(-cost)); // 10
//
//						reportList.add(innerList);
//					}
//
//				}
//			}
//			request.setAttribute("reportList", reportList);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//
//	}
	
	private void viewProjectWiseEmployeeProgress(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs  = null;
		
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmDeptName = CF.getEmpDepartmentNameMap(con);
//			Map<String, String> hmEmpGrossSalary = CF.getEmpGrossSalary(uF, CF, con, getStrStartDate(), "H");
			
//			System.out.println("hmEmpNetHourlySalary ===>> " + hmEmpNetHourlySalary);
			if(getStrStartDate()!=null && getStrStartDate().equalsIgnoreCase("NULL")) {
				setStrStartDate(null);
				setStrEndDate(null);
			}		
			if(getStrEndDate()!=null && getStrEndDate().equalsIgnoreCase("NULL")) {
				setStrStartDate(null);
				setStrEndDate(null);
			}
			if(getStrStartDate()==null && getStrEndDate()==null) {
				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
				int nMaxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				int nMinDate = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
				
				setStrStartDate(uF.getDateFormat(nMinDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
				setStrEndDate(uF.getDateFormat(nMaxDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
			}
			
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con, getStrStartDate(), getStrEndDate(), CF, uF, hmWeekEndHalfDates, null);
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con, uF, CF, getStrStartDate(), getStrEndDate(), alEmpCheckRosterWeektype, hmRosterWeekEndDates, hmWeekEnds, hmEmpLevelMap, hmEmpWlocation, hmWeekEndHalfDates);
			
			Map<String,String> hmWorkDays = new HashMap<String, String>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_per_id,wlocation_id from employee_personal_details epd, employee_official_details eod WHERE epd.is_alive=true and epd.emp_per_id=eod.emp_id and approved_flag=true ");
			if(strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery.append(" and (supervisor_emp_id = "+uF.parseToInt((String)session.getAttribute(EMPID))+" or eod.emp_id = "+uF.parseToInt((String)session.getAttribute(EMPID))+")");
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
			sbQuery.append(" order by emp_fname ");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while(rs.next()) {
				String strWLocationId = rs.getString("wlocation_id");
				Set<String> weeklyOffEndDate = hmWeekEnds.get(strWLocationId);
				if (weeklyOffEndDate == null) weeklyOffEndDate = new HashSet<String>();

				Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(rs.getString("emp_per_id"));
				if (rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();

				Map<String, String> hmHolidaysCnt = new HashMap<String, String>();
				Map<String, String> hmHolidayDates = new HashMap<String, String>();
				
				if (alEmpCheckRosterWeektype != null && alEmpCheckRosterWeektype.contains(rs.getString("emp_per_id"))) {
					CF.getHolidayListCount(con, request, getStrStartDate(), getStrEndDate(), CF, hmHolidayDates, hmHolidaysCnt, rosterWeeklyOffSet, true);
				} else {
					CF.getHolidayListCount(con, request, getStrStartDate(), getStrEndDate(), CF, hmHolidayDates, hmHolidaysCnt, weeklyOffEndDate, true);
				}
				
				String diffInDays = uF.dateDifference(getStrStartDate(), DATE_FORMAT, getStrEndDate(), DATE_FORMAT, CF.getStrTimeZone());

				int nWeekEnd = (alEmpCheckRosterWeektype != null && alEmpCheckRosterWeektype.contains(rs.getString("emp_per_id"))) ? rosterWeeklyOffSet.size() : weeklyOffEndDate.size();
				int nHolidayCnt = uF.parseToInt(hmHolidaysCnt.get(strWLocationId));
				double nWorkDay = (uF.parseToDouble(diffInDays) - nWeekEnd) - nHolidayCnt;
				
				double avgMonthDays = uF.parseToDouble(diffInDays)/30;
				double avgWorkDay = nWorkDay/avgMonthDays;
				
				hmWorkDays.put(rs.getString("emp_per_id"), avgWorkDay+"");
			}
			rs.close();
			pst.close();
				
			Map<String, String> hmEmpNetHourlySalary = CF.getEmpNetSalary(uF, CF, con, uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE), "H", hmWorkDays);
			
			sbQuery = new StringBuilder();
			
			Map<String, String> hmProjectData =  new HashMap<String, String>();
			Map<String, List<String>> hmClientProId = new HashMap<String, List<String>>();
		
		//===start parvez date: 12-10-2022===	
			/*sbQuery.append("select cd.client_id, cd.client_name, p.pro_id, p.pro_name,p.curr_id,p.actual_calculation_type,di.dept_name,p.project_owner, p.start_date,p.deadline, billing_type, billing_kind, billing_cycle_day, billing_cycle_weekday, billing_amount"
				+ " from client_details cd,projectmntnc p,department_info di where cd.client_id = p.client_id and p.department_id = di.dept_id ");*/
			sbQuery.append("select cd.client_id, cd.client_name, p.pro_id, p.pro_name,p.curr_id,p.actual_calculation_type,di.dept_name,p.project_owners, p.start_date,p.deadline, billing_type, billing_kind, billing_cycle_day, billing_cycle_weekday, billing_amount"
					+ " from client_details cd,projectmntnc p,department_info di where cd.client_id = p.client_id and p.department_id = di.dept_id ");
		//===end parvez date: 12-10-2022===	
			//and p.pro_id = pbad.pro_id and p.approve_status='n'
			if(getClient() != null && getClient().length>0) {
				sbQuery.append(" and p.client_id in ("+StringUtils.join(getClient(), ",")+") ");
			}
			if(getPro_id()!=null && getPro_id().length>0 ){
				sbQuery.append(" and p.pro_id in ("+StringUtils.join(getPro_id(), ",")+") ");
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and p.org_id in ("+getF_org()+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and p.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation() != null && getF_strWLocation().length>0) {
				sbQuery.append(" and p.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
			}
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and p.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_service() != null && getF_service().length>0) {
				sbQuery.append(" and p.sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and ((start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and start_date <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (deadline >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "')) ");
				}
			sbQuery.append(" order by client_name, pro_id,dept_name desc ");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			List<String> alInner1 = new ArrayList<String>();
			StringBuilder sbProIds = null;
			
			while(rs.next()) {
//				alInner1 = hmClientProId.get(rs.getString("client_id"));
				if(sbProIds==null) {
					sbProIds = new StringBuilder();
					sbProIds.append(rs.getString("pro_id"));
				} else {
					sbProIds.append(","+rs.getString("pro_id"));
				}
				
				if(alInner1 == null) alInner1 = new ArrayList<String>();
				alInner1.add(rs.getString("pro_id"));			
				hmClientProId.put(rs.getString("client_id"), alInner1);
				hmProjectData.put(rs.getString("pro_id")+"_CLIENT", rs.getString("client_name"));
				hmProjectData.put(rs.getString("pro_id")+"_PRO_NAME", rs.getString("pro_name"));
				hmProjectData.put(rs.getString("pro_id")+"_DEPT_NAME", rs.getString("dept_name"));
				
			//===start parvez date: 14-10-2022===
				StringBuilder sbOwners = null;
				if(rs.getString("project_owners")!=null){
					List<String> tempList = Arrays.asList(rs.getString("project_owners").split(","));
					
					for(int j=1; j<tempList.size();j++){
						if(sbOwners==null){
							sbOwners = new StringBuilder();
							sbOwners.append(hmEmpName.get(tempList.get(j)));
						}else{
							sbOwners.append(", "+hmEmpName.get(tempList.get(j)));
						}
					}
				}
//				hmProjectData.put(rs.getString("pro_id")+"_MANAGER", hmEmpName.get(rs.getString("project_owner")));
				hmProjectData.put(rs.getString("pro_id")+"_MANAGER", sbOwners+"");
			//===end parvez date: 12-10-2022===	
				
				hmProjectData.put(rs.getString("pro_id")+"_START_DATE", rs.getString("start_date"));
				hmProjectData.put(rs.getString("pro_id")+"_DEADLINE", rs.getString("deadline"));
				
				hmProjectData.put(rs.getString("pro_id")+"_BILL_FREQ", rs.getString("billing_kind"));
				hmProjectData.put(rs.getString("pro_id")+"_FREQ_DAY", rs.getString("billing_cycle_day"));
				hmProjectData.put(rs.getString("pro_id")+"_WEEKDAY", rs.getString("billing_cycle_weekday"));
				hmProjectData.put(rs.getString("pro_id")+"_BILLING_AMT", rs.getString("billing_amount"));
				hmProjectData.put(rs.getString("pro_id")+"_CURR_ID", rs.getString("curr_id"));
			}
			rs.close();
			pst.close();
			
			List<String> monthYearsList = new ArrayList<String>();
			Map<String, String> hmCommitment = new HashMap<String, String>();
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
				
			    while(monthDiff > 0) {
					monthYearsList.add(String.valueOf(startMonth)+"/"+String.valueOf(startYear));
			    
					startMonth++;
			
					if(startMonth > 12 && endMonth < 12) {
						startMonth = 1;
						startYear++;
					} else if(startMonth > endMonth && startYear == endYear) {
						break;
					}
				}
			    
			    for(int i=0;i<alInner1.size() && alInner1!=null && !alInner1.isEmpty();i++) {
					String proId = alInner1.get(i);
	//				System.out.println("proId ===>> " + proId);
//					List<String> proList = new ArrayList<String>();
//					proList.add(hmProjectData.get(proId+"_PRO_CODE"));			//0
//				    proList.add(hmProjectData.get(proId+"_CLIENT_NAME"));		//1
//				    proList.add(hmProjectData.get(proId+"_PRO_NAME"));			//2
//				    proList.add(hmProjectData.get(proId+"_LOCATION"));			//3
//	//			    proList.add(hmProjectData.get(proId+"_DEPARTMENT"));		//4
//				    proList.add(hmProjectData.get(proId+"_SBU"));				//4
//				    proList.add(hmProjectData.get(proId+"_BILL_TYPE"));			//5
				    
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
						
						String[] dateArr = month.split("/");
						String strFirstDate = null;
						String strEndDate = null;
						
						String strDate = "01/"+dateArr[0]+"/"+dateArr[1];
						String minMaxDate = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
						String[] tmpDate = minMaxDate.split("::::");
						strFirstDate = tmpDate[0];
						strEndDate = tmpDate[1];
				
	//					System.out.println("strFirstDate==>"+ strFirstDate +"strEndDate==>"+ strEndDate);
	//					String newStartDate = strFirstDate;
	//					System.out.println(proId + " --- newStartDate ===>> " + newStartDate);
						
						int intMonths = uF.getMonthsDifference(uF.getDateFormat(strFirstDate, DATE_FORMAT), uF.getDateFormat(strEndDate, DATE_FORMAT));
	//					System.out.println(proId + " --- intMonths ===>> " + intMonths);
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
	//						System.out.println(proId + " --- strDays ===>> " + strDays);
							intCount = (uF.parseToInt(strDays) / 7);
							intCount++;	
				//===start parvez date: 29-01-2022===		
						} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O")) {
							if(milestoneIds.size()>0){
								intCount = milestoneIds.size();
							}
						}	
				//===end parvez date: 29-01-2022===		
						
						
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
							
							for(int j=0; j<intCount; j++) {
								String newStDate = getNewProjectStartDate(con, uF, proId, getFreqENDDATE());
							
								if(newStDate == null || newStDate.equals("")) {
										newStDate = strFirstDate;
								}
	//								System.out.println("newStDate ===>> " + newStDate);
								boolean frqFlag = false;
								String freqEndDate = strEndDate;
								
								if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O")) {
	//								freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
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
	//								System.out.println("newStDate ===>> "+ newStDate + " -- freqEndDate 1 ===>> " + freqEndDate +" -- proId ===>> " + proId);
	//									uF.getDateFormat(uF.getPrevDate(freqEndDate, 1)+"", DBDATE, DATE_FORMAT);
									
									Date stDate = uF.getDateFormatUtil(newStDate, DATE_FORMAT);
	//									Date endDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
									Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
									
	//									System.out.println("innerList.get(4) ===>> " + innerList.get(4));
	//									System.out.println("freqDate ===>> " + freqDate + " stDate ===>> " + stDate);
									
									if(freqDate.after(stDate)) {
										frqFlag = true;
									}
	//								System.out.println("frqFlag ===>> " + frqFlag+" -- proId ===>> " + proId);
									if(frqFlag) {
										if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("M")) {
											freqEndDate = freqEndDate;
										} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("B")) {
											freqEndDate = uF.getDateFormat(uF.getFutureDate(newStDate, 15)+"", DBDATE, DATE_FORMAT);
										}
									} else {
										if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("M")) {
											freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(freqEndDate, 1)+"", DBDATE, DATE_FORMAT);
	//										System.out.println("freqEndDate in else ===>> " + freqEndDate +" -- proId ===>> " + proId);
										} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("B")) {
											freqEndDate = uF.getDateFormat(uF.getFutureDate(strFirstDate, 15)+"", DBDATE, DATE_FORMAT);
										}
									}
								}
								
	//							System.out.println("freqEndDate 2 ===>> " + freqEndDate +" -- proId ===>> " + proId);
								if(hmProjectData.get(proId+"_WEEKDAY") != null && !hmProjectData.get(proId+"_WEEKDAY").equals("") && hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("W")) {
									freqEndDate = uF.getDateFormat(uF.getDateOfPassedDay(newStDate, hmProjectData.get(proId+"_WEEKDAY"))+"", DBDATE, DATE_FORMAT);
									freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
	//								System.out.println("freqEndDate -1 ===>>>>> " + freqEndDate);
								}
								if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("Q")) {
									 Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
	//								 System.out.println("Q -- getQfreqENDDATE() ===>> " + getQfreqENDDATE());
									 if(getQfreqENDDATE() == null) {
										 String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
										 String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
										 String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -3)+"", DBDATE, DATE_FORMAT);
										 freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 2, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
	//									 freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(arrTmpMinMaxDate[1], 2)+"", DBDATE, DATE_FORMAT);
	//										setQfreqENDDATE(freqEndDate);
										 setQfreqENDDATE(freqEndDate);
	//									 System.out.println("Q -- freqEndDate ===>> " + freqEndDate);
										} else {
											freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 3)+"", DBDATE, DATE_FORMAT);
	//										System.out.println("Q -- else freqEndDate ===>> " + freqEndDate );
										}
									 
									 if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
										freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
	//									System.out.println("Q -- _DEADLINE freqEndDate ===>> " + freqEndDate );
									 }
									 if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
										setQfreqENDDATE(freqEndDate);
									 }
	//									System.out.println("Q -- freqEndDate out ===>> " + freqEndDate +" -- proId ===>> " + proId);
								}
								if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("H")) {
									Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
									if(getQfreqENDDATE() == null) {
										 String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
										 String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
										 String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -6)+"", DBDATE, DATE_FORMAT);
										 freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 5, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
	//									 freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(arrTmpMinMaxDate[1], 5)+"", DBDATE, DATE_FORMAT);
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
									
										setQfreqENDDATE(freqEndDate);
										
									} else {
									
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
								
								
								setFreqENDDATE(freqEndDate);
								
								
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
						totalOutStandingAmt += dblBillAmount;
					}
					hmCommitment.put(proId, uF.formatIntoOneDecimalWithOutComma(totalOutStandingAmt));
				}
			}
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			Iterator<String> it = hmClientProId.keySet().iterator();
//			while (it.hasNext()) {
//				String clientId = it.next();
//				List<String> alList = hmClientProId.get(clientId);
			/*Map<String, String> hmInvoiceAmt = new HashMap<String, String>();
//			for(int i=0; alInner1 != null && !alInner1.isEmpty() && i< alInner1.size(); i++) {
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(invoice_amount) as invoice_amount, pro_id from promntc_invoice_details ");
			if(sbProIds!=null) {
				sbQuery.append(" where pro_id in ("+sbProIds.toString()+") ");
			}
			sbQuery.append(" group by pro_id");
			pst = con.prepareStatement(sbQuery.toString()); // where pro_id in ("+alInner1.get(i)+")
//				System.out.println(pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				hmInvoiceAmt.put(rs.getString("pro_id"), rs.getString("invoice_amount"));
//				alInvoiceAmt.add(rs.getString("invoice_amount"));
			}
			rs.close();
			pst.close();*/
			
			
			Map<String, String> hmInvoiceAmt = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select promntc_invoice_id,invoice_amount, pro_ids from promntc_invoice_details where promntc_invoice_id>0 ");
			if(alInner1!=null && !alInner1.isEmpty()){
				sbQuery.append(" and ( ");
				for(int i=0; i<alInner1.size(); i++){
					sbQuery.append("pro_ids like '%,"+alInner1.get(i)+",%'");
					if(i<alInner1.size()-1){
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" )");
			}
			
			
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and invoice_generated_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"'");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst="+pst);	
			rs = pst.executeQuery();
			
			Map<String,List<String>> hmInvoiceProIds = new HashMap<String, List<String>>();
			List<String> alInoviceId = new ArrayList<String>();
			while(rs.next()) {
				
				
				if(rs.getString("pro_ids") != null){
					String[] tempProId = rs.getString("pro_ids").split(",");
					List<String> alInner = new ArrayList<String>();
					for(int i=0; i<tempProId.length; i++){
						if(i>0){
							double amt = uF.parseToDouble(rs.getString("invoice_amount"))+ uF.parseToDouble(hmInvoiceAmt.get(tempProId[i]));
							hmInvoiceAmt.put(tempProId[i], amt+"");
							alInner.add(tempProId[i]);
						}
					}
					alInoviceId.add(rs.getString("promntc_invoice_id"));
					hmInvoiceProIds.put(rs.getString("promntc_invoice_id"), alInner);
				}
				
			}
			rs.close();
			pst.close();
//			System.out.println("hmInvoiceAmt="+hmInvoiceAmt);
			
//			}
			/*Map<String, String> hmRecivedAmt = new HashMap<String, String>();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(received_amount) as received_amount, pro_id from promntc_bill_amt_details ");
			if(sbProIds!=null) {
				sbQuery.append(" where pro_id in ("+sbProIds.toString()+") ");
			}
			sbQuery.append(" group by pro_id");
			pst = con.prepareStatement(sbQuery.toString()); //where pro_id in ("+alInner1.get(i)+")
			rs = pst.executeQuery();
			while(rs.next()) {
				hmRecivedAmt.put(rs.getString("pro_id"), rs.getString("received_amount"));
			}
			rs.close();
			pst.close();*/
			
			Map<String, String> hmRecivedAmt = new HashMap<String, String>();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select received_amount, invoice_ids from promntc_bill_amt_details where bill_id>0 ");
			
			if(alInoviceId !=null && !alInoviceId.isEmpty()){
				sbQuery.append(" and ( ");
				for(int i=0; i<alInoviceId.size(); i++){
					sbQuery.append("invoice_ids like '%,"+alInoviceId.get(i)+",%'");
					if(i<alInoviceId.size()-1){
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" )");
			}
			
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and entry_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"'");
			}
			
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst==="+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				
				if(rs.getString("invoice_ids")!=null){
					String[] invoiceId = rs.getString("invoice_ids").split(",");
					for(int k=0; k<invoiceId.length; k++){
						if(k>0){
							List<String> inner = hmInvoiceProIds.get(invoiceId[k]);
							for(int j=0; j<inner.size(); j++){
								double amt = uF.parseToDouble(rs.getString("received_amount"))+ uF.parseToDouble(hmRecivedAmt.get(inner.get(j)));
								hmRecivedAmt.put(inner.get(j), amt+"");
							}
						}
					}
				}
				
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmRecivedAmt="+hmRecivedAmt);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from project_emp_details ");
			if(sbProIds!=null) {
				sbQuery.append(" where pro_id in ("+sbProIds.toString()+") ");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println(pst);
			rs = pst.executeQuery();
			List<String> alEmpIds = new ArrayList<String>();
			StringBuilder sbEmpIds = null;
			while(rs.next()) {
				if(sbEmpIds== null) {
					sbEmpIds = new StringBuilder();
					sbEmpIds.append(rs.getString("emp_id"));
				} else {
					sbEmpIds.append(","+rs.getString("emp_id"));
				}
				alEmpIds.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
				
			double acHr = 0.0d;
			double grossSalary = 0.0d;
			Map<String, String> hmProjectCostAmt = new HashMap<String, String>(); 
			if(sbEmpIds!=null && !sbEmpIds.equals("")) {
				StringBuilder sbQue = new StringBuilder();
				sbQue.append("select ta.emp_id, sum(ta.actual_hrs) as hrs, ai.pro_id from task_activity ta, activity_info ai where ai.task_id = ta.activity_id and ta.emp_id in ("+sbEmpIds.toString()+") ");
				if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
					sbQue.append(" and task_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
				}
				sbQue.append("group by ai.pro_id, ta.emp_id");
				pst = con.prepareStatement(sbQue.toString());
//				System.out.println("pst ===>> " + pst);
//						pst.setInt(1, uF.parseToInt(alEmpIds.get(j)));
				rs = pst.executeQuery();
				while(rs.next()) {
					double empCost = uF.parseToDouble(hmProjectCostAmt.get(rs.getString("pro_id")));
					acHr = uF.parseToDouble(rs.getString("hrs"));
					grossSalary = uF.parseToDouble(hmEmpNetHourlySalary.get(rs.getString("emp_id")));
//					if(rs.getString("emp_id").equals("508")) {
//						System.out.println(rs.getString("pro_id")+ " -- acHr ===>> " + acHr + " -- grossSalary ===>> " + grossSalary);
//					}
					empCost += (acHr * grossSalary);
					hmProjectCostAmt.put(rs.getString("pro_id"), empCost+"");
				}
				rs.close();
				pst.close();
			}
//			System.out.println("hmProjectCostAmt ===>> " + hmProjectCostAmt);
			
			for(int i=0;i<alInner1.size() && alInner1!=null && !alInner1.isEmpty();i++) {
				
				Map<String, String> hmCurr = hmCurrencyMap.get(hmProjectData.get(alInner1.get(i)+"_CURR_ID"));
				
				List<String> innerList = new ArrayList<String>();					
				innerList.add(hmProjectData.get(alInner1.get(i)+"_CLIENT"));	//0
				innerList.add(hmProjectData.get(alInner1.get(i)+"_PRO_NAME"));	//1
				innerList.add(hmProjectData.get(alInner1.get(i)+"_DEPT_NAME"));	//2
				innerList.add(uF.showData(hmProjectData.get(alInner1.get(i)+"_MANAGER"), "-"));	//3
//				double cost = acHr*grossSalary;
				innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.formatIntoComma(uF.parseToDouble(hmProjectCostAmt.get(alInner1.get(i)))));	//4
				innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.formatIntoComma(uF.parseToDouble(hmCommitment.get(alInner1.get(i)))));	//4
				double billedAmt = uF.parseToDouble(hmInvoiceAmt.get(alInner1.get(i)));
				double recivedAmt = uF.parseToDouble(hmRecivedAmt.get(alInner1.get(i)));
				innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.formatIntoComma(billedAmt));//5
//				System.out.println("billedAmt="+billedAmt+"---proId="+alInner1.get(i));
				innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.formatIntoComma(recivedAmt));//6
				innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.formatIntoComma(billedAmt-recivedAmt));//7
				innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.formatIntoComma(billedAmt-uF.parseToDouble(hmProjectCostAmt.get(alInner1.get(i)))));//8
				reportList.add(innerList);
			}
//			}
			request.setAttribute("reportList", reportList);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs); 
			db.closeStatements(pst);  
			db.closeConnection(con);
		}
	}
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
	public String loadEmployeeWiseProjectProgress(UtilityFunctions uF) {
		if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String) session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());

		getSelectedFilter(uF);

		return LOAD;
	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String, String> hmFilter = new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		alFilter.add("ORGANISATION");
		if (getF_org() != null) {
			String strOrg = "";
			int k = 0;
			for (int i = 0; organisationList != null && i < organisationList.size(); i++) {
				if (getF_org().equals(organisationList.get(i).getOrgId())) {
					if (k == 0) {
						strOrg = organisationList.get(i).getOrgName();
					} else {
						strOrg += ", " + organisationList.get(i).getOrgName();
					}
					k++;
				}
			}
			if (strOrg != null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}

		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}

		alFilter.add("LOCATION");
		if (getF_strWLocation() != null) {
			String strLocation = "";
			int k = 0;
			for (int i = 0; wLocationList != null && i < wLocationList.size(); i++) {
				for (int j = 0; j < getF_strWLocation().length; j++) {
					if (getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if (k == 0) {
							strLocation = wLocationList.get(i).getwLocationName();
						} else {
							strLocation += ", " + wLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if (strLocation != null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}

		alFilter.add("DEPARTMENT");
		if (getF_department() != null) {
			String strDepartment = "";
			int k = 0;
			for (int i = 0; departmentList != null && i < departmentList.size(); i++) {
				for (int j = 0; j < getF_department().length; j++) {
					if (getF_department()[j].equals(departmentList.get(i).getDeptId())) {
						if (k == 0) {
							strDepartment = departmentList.get(i).getDeptName();
						} else {
							strDepartment += ", " + departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if (strDepartment != null && !strDepartment.equals("")) {
				hmFilter.put("DEPARTMENT", strDepartment);
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}

		alFilter.add("SERVICE");
		if (getF_service() != null) {
			String strService = "";
			int k = 0;
			for (int i = 0; serviceList != null && i < serviceList.size(); i++) {
				for (int j = 0; j < getF_service().length; j++) {
					if (getF_service()[j].equals(serviceList.get(i).getServiceId())) {
						if (k == 0) {
							strService = serviceList.get(i).getServiceName();
						} else {
							strService += ", " + serviceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if (strService != null && !strService.equals("")) {
				hmFilter.put("SERVICE", strService);
			} else {
				hmFilter.put("SERVICE", "All SBUs");
			}
		} else {
			hmFilter.put("SERVICE", "All SBUs");
		}

		alFilter.add("LEVEL");
		if (getF_level() != null) {
			String strLevel = "";
			int k = 0;
			for (int i = 0; levelList != null && i < levelList.size(); i++) {
				for (int j = 0; j < getF_level().length; j++) {
					if (getF_level()[j].equals(levelList.get(i).getLevelId())) {
						if (k == 0) {
							strLevel = levelList.get(i).getLevelCodeName();
						} else {
							strLevel += ", " + levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if (strLevel != null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Levels");
			}
		} else {
			hmFilter.put("LEVEL", "All Levels");
		}

		if ((getStrStartDate() != null && !getStrStartDate().equals("")) && (getStrEndDate() != null && !getStrEndDate().equals(""))) {
			alFilter.add("FROMTO");
			hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) + " - "
					+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}

		String selectedFilter = CF.getSelectedFilter1(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpSession getSession() {
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public CommonFunctions getCF() {
		return CF;
	}

	public void setCF(CommonFunctions cF) {
		CF = cF;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
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

	public String[] getF_level() {
		return f_level;
	}
	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}
	public String getPaycycle() {
		return paycycle;
	}
	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
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
	public List<FillServices> getServiceList() {
		return serviceList;
	}
	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}
	public List<FillLevel> getLevelList() {
		return levelList;
	}
	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}
	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}
	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}
	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String[] getPro_id() {
		return pro_id;
	}

	public void setPro_id(String[] pro_id) {
		this.pro_id = pro_id;
	}

	public String[] getClient() {
		return client;
	}

	public void setClient(String[] client) {
		this.client = client;
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
	
	

}
