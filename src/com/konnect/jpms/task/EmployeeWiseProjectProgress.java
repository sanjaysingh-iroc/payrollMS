package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.payroll.reports.DepartmentwiseReport;
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

public class EmployeeWiseProjectProgress extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId;
	String strUserType; 
	       
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(DepartmentwiseReport.class);
	
	String f_org;
	String[] f_strWLocation; 
	String[] f_department;
	String[] f_service;
	String[] f_level;
	
	String paycycle;
	String strStartDate;
	String strEndDate;
	
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillLevel> levelList;
	List<FillPayCycles> paycycleList;
	
	public String execute() throws Exception {
		
		session = request.getSession();
//		System.out.println("EWPP/62--");
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/task/EmployeeWiseProjectProgress.jsp");
		request.setAttribute(TITLE, "Employee wise Project Progress");
		strEmpId =(String) session.getAttribute(EMPID);
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
		
		if(getF_org() == null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		viewEmployeeWiseProjectProgress(uF);
		
		return loadEmployeeWiseProjectProgress(uF);

	}
	
	private void viewEmployeeWiseProjectProgress(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
//			System.out.println("EWPP/98--strStartDate="+getStrStartDate());
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
			
			
			Map<String, String> hmGradeDesigId = CF.getGradeDesig(con);
			Map<String, String> hmDesigName = CF.getDesigMap(con);
			Map<String, String> hmDepartName = CF.getDeptMap(con);
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con, getStrStartDate(), getStrEndDate(), CF, uF, hmWeekEndHalfDates, null);
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con, uF, CF, getStrStartDate(), getStrEndDate(), alEmpCheckRosterWeektype, hmRosterWeekEndDates, hmWeekEnds, hmEmpLevelMap, hmEmpWlocation, hmWeekEndHalfDates);
			
			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery.append("select emp_per_id, empcode, emp_fname, emp_mname, emp_lname,depart_id,grade_id,wlocation_id from employee_personal_details epd, employee_official_details eod WHERE epd.is_alive=true and epd.emp_per_id=eod.emp_id and approved_flag=true ");
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
			System.out.println("ESR/170--pst="+pst);
			List<String> alEmpIds = new ArrayList<String>();
			StringBuilder sbEmpIds = null;
		//===start parvez date: 25-03-2022===
			Map<String,String> hmWorkDays = new HashMap<String, String>();
			
		//===end parvez date: 25-03-2022===	
			
			Map<String, String> hmEmpPersonalDetails = new HashMap<String, String>();
			while(rs.next()) {
				alEmpIds.add(rs.getString("emp_per_id"));
				String strMiddleName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strMiddleName = " "+rs.getString("emp_mname");
					}
				}
				String strEmpName = rs.getString("emp_fname") + uF.showData(strMiddleName, "")+" " + rs.getString("emp_lname");
				hmEmpPersonalDetails.put(rs.getString("emp_per_id")+"_EMPNAME", strEmpName);
				hmEmpPersonalDetails.put(rs.getString("emp_per_id")+"_EMPDESIG", hmDesigName.get(hmGradeDesigId.get(rs.getString("grade_id"))));
				hmEmpPersonalDetails.put(rs.getString("emp_per_id")+"_EMPDEPT", hmDepartName.get(rs.getString("depart_id")));
				
			//===start parvez date: 25-03-2022===	
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
				System.out.println("EWPP/215--avgWorkDay="+avgWorkDay);
			//===end parvez date: 25-03-2022===
				
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmEmpWiseTaskInfo = new HashMap<String, String>();
			Map<String, String> hmProName = CF.getProjectNameMap(con);
			List<List<String>> reportList = new ArrayList<List<String>>();
//			Map<String, String> hmEmpGrossSalary = CF.getEmpGrossSalary(uF, CF, con, getStrStartDate(), "H");
				
			Map<String, String> hmEmpNetHourlySalary = CF.getEmpNetSalary(uF, CF, con, uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE), "H",hmWorkDays);
//			System.out.println("EWPP/182--strStartDate1="+getStrStartDate());
			StringBuilder sbTaskIds = null;
			Set<String> stEmpIds = new HashSet<String>();
			for(int i=0; alEmpIds != null && !alEmpIds.isEmpty() && i<alEmpIds.size(); i++) {
//				pst = con.prepareStatement("select task_id, activity_name, approve_status, pro_id from activity_info where resource_ids like '%,"+alEmpIds.get(i)+",%' ");
				pst = con.prepareStatement("select ai.task_id, ai.activity_name, ai.approve_status, p.pro_name, p.curr_id, cd.client_name from activity_info ai, projectmntnc p, "
						+ "client_details cd where ai.pro_id =p.pro_id and p.client_id = cd.client_id and resource_ids like '%,"+alEmpIds.get(i)+",%' ");
				
				rs = pst.executeQuery();
				while(rs.next()) {
					if(sbTaskIds == null) {
						sbTaskIds = new StringBuilder();
						sbTaskIds.append(rs.getString("task_id"));
					} else {
						sbTaskIds.append(","+rs.getString("task_id"));
					}
					if(sbEmpIds == null) {
						sbEmpIds = new StringBuilder();
						sbEmpIds.append(alEmpIds.get(i));
					} else {
						sbEmpIds.append(","+alEmpIds.get(i));
					}
					hmEmpWiseTaskInfo.put(rs.getString("task_id")+"_TASKNAME", rs.getString("activity_name"));
					if(rs.getString("approve_status").equalsIgnoreCase("approved")){
						hmEmpWiseTaskInfo.put(rs.getString("task_id")+"_TASKSTATUS", "Close");
					} else{
						hmEmpWiseTaskInfo.put(rs.getString("task_id")+"_TASKSTATUS", "Open");
					}
					hmEmpWiseTaskInfo.put(rs.getString("task_id")+"_PROJECTNAME", rs.getString("pro_name"));
					hmEmpWiseTaskInfo.put(rs.getString("task_id")+"_CLIENTNAME", rs.getString("client_name"));
					hmEmpWiseTaskInfo.put(rs.getString("task_id")+"_CURR_ID", rs.getString("curr_id"));
					
//					hmEmpWiseTaskInfo.put(rs.getString("task_id")+"_PROJECTNAME", hmProName.get(rs.getString("pro_id")));
//					String clientId = CF.getClientIdByProjectTaskId(con, uF, rs.getString("task_id"), alEmpIds.get(i));
//					String clientName = CF.getClientNameById(con, clientId);
//					hmEmpWiseTaskInfo.put(rs.getString("task_id")+"_CLIENTNAME", clientName);
				}
				rs.close();
				pst.close();
			}
			
			if(sbTaskIds != null && !sbTaskIds.equals("") && sbEmpIds != null && !sbEmpIds.equals("")) {
				Map<String,String> hmBillingAmt = new HashMap<String, String>();
				StringBuilder sbQue = new StringBuilder();
				sbQue.append("select sum(piad.invoice_particulars_amount) as invoice_particulars_amount,emp_id,task_id from promntc_invoice_details pid,promntc_invoice_amt_details piad where pid.promntc_invoice_id=piad.promntc_invoice_id and emp_id in ("+sbEmpIds.toString()+") and task_id in ("+sbTaskIds.toString()+")");
				if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
					sbQue.append(" and invoice_generated_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
				}
				sbQue.append(" group by emp_id, task_id");
				pst = con.prepareStatement(sbQue.toString());
				rs = pst.executeQuery();
				while(rs.next()) {
					double billAmt = uF.parseToDouble(rs.getString("invoice_particulars_amount"));
					hmBillingAmt.put(rs.getString("emp_id") + "_" + rs.getString("task_id"), billAmt+"");
				}
				rs.close();
				pst.close();
				
				sbQue = new StringBuilder();
				sbQue.append("select emp_id,activity_id,sum(actual_hrs) as hrs from task_activity where emp_id in ("+sbEmpIds.toString()+") and activity_id  in ("+sbTaskIds.toString()+") ");
				if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
					sbQue.append(" and task_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
				}
				sbQue.append("group by emp_id, activity_id");
				pst = con.prepareStatement(sbQue.toString());
				rs = pst.executeQuery();
				while(rs.next()) {
					
					Map<String, String> hmCurr = hmCurrencyMap.get(hmEmpWiseTaskInfo.get(rs.getString("activity_id")+"_CURR_ID"));
					
					List<String> innerList = new ArrayList<String>();
					innerList.add(hmEmpPersonalDetails.get(rs.getString("emp_id")+"_EMPDEPT"));		//0
					innerList.add(hmEmpPersonalDetails.get(rs.getString("emp_id")+"_EMPNAME"));		//1
					innerList.add(hmEmpPersonalDetails.get(rs.getString("emp_id")+"_EMPDESIG"));		//2
					innerList.add(hmEmpWiseTaskInfo.get(rs.getString("activity_id")+"_CLIENTNAME"));	//3
					innerList.add(hmEmpWiseTaskInfo.get(rs.getString("activity_id")+"_PROJECTNAME"));	//4
					innerList.add(hmEmpWiseTaskInfo.get(rs.getString("activity_id")+"_TASKNAME"));	//5
					innerList.add(hmEmpWiseTaskInfo.get(rs.getString("activity_id")+"_TASKSTATUS"));	//6
					innerList.add(rs.getString("hrs"));	//7
					if(hmBillingAmt != null && !hmBillingAmt.isEmpty() && hmBillingAmt.get(rs.getString("emp_id")+"_"+rs.getString("activity_id")) != null) {
						innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.formatIntoComma(uF.parseToDouble(hmBillingAmt.get(rs.getString("emp_id")+"_"+rs.getString("activity_id")))));	//8   Billing
					} else{
						innerList.add(hmCurr.get("SHORT_CURR")+" "+"0");	//8   Billing
					}
//						innerList.add("");	//8   Billing
					double cost = uF.parseToDouble(rs.getString("hrs"))*uF.parseToDouble(hmEmpNetHourlySalary.get(rs.getString("emp_id")));
					innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.formatIntoComma(cost));	//9
					innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.formatIntoComma((hmBillingAmt!=null ? uF.parseToDouble(hmBillingAmt.get(rs.getString("emp_id")+"_"+rs.getString("activity_id"))) : 0)-cost));	//10
					
					reportList.add(innerList);
				}
			}
			request.setAttribute("reportList", reportList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	public String loadEmployeeWiseProjectProgress(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
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
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
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
		
		
		
		if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))){
			alFilter.add("FROMTO");
			hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		
		String selectedFilter=CF.getSelectedFilter1(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
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
	
}
