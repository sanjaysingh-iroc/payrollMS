package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClients;
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

public class ManDaysUtilizationReport extends ActionSupport implements ServletRequestAware, IStatements {
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
	
	String paycycle;
	String strStartDate;
	String strEndDate;
	
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillLevel> levelList;
	List<FillPayCycles> paycycleList;
	
	List<FillProjectList> projectdetailslist;
	List<FillClients> clientList;
	String[] pro_id;
	String[] client;
	
	public String execute() throws Exception {
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/task/ManDaysUtilizationReport.jsp");
		request.setAttribute(TITLE, "Man Days Based Utilization Report");
		strEmpId =(String) session.getAttribute(EMPID);
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
		
		if(getF_org() == null){
			setF_org((String)session.getAttribute(ORGID));
		}
		getManDaysUtilizationReport(uF);
		
//		System.out.println("Man Days Based Utilization ===>> ");
		return loadManDaysUtilizationReport(uF);

	}
	
	private void getManDaysUtilizationReport(UtilityFunctions uF) {
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

			StringBuilder sbQuery = new StringBuilder();
			/*sbQuery.append("select task_date,ta.emp_id,sum(ta.actual_hrs) as actual_hrs,ta.approved_by,client_name,pro_name,p.pro_id from task_activity ta ");
			sbQuery.append("left join client_details cd on cd.client_id = ta.client_id left join projectmntnc p on p.client_id = cd.client_id where is_approved > 1");
			sbQuery.append(" and ta.emp_id in (select emp_per_id from employee_personal_details epd, employee_official_details eod WHERE epd.is_alive=true and " +
					" epd.emp_per_id=eod.emp_id and approved_flag=true and activity_id>0");
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
			sbQuery.append(" order by emp_fname) ");
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and task_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"'");
			}
			if(getPro_id() != null && getPro_id().length > 0){
				sbQuery.append(" and p.pro_id in("+StringUtils.join(getPro_id(), ",")+") ");
			}
			if(getClient() != null && getClient().length > 0){
				sbQuery.append(" and ta.client_id in("+StringUtils.join(getClient(), ",")+") ");
			}
			sbQuery.append(" group by ta.emp_id,task_date,ta.approved_by,client_name,pro_name,p.pro_id order by ta.emp_id,task_date ");
			pst = con.prepareStatement(sbQuery.toString());
			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			StringBuilder sbEmpIds = null;
			Map<String, Map<String, String>> hmManagerwiseTaskDayCount = new HashMap<String, Map<String,String>>();
			Map<String, Map<String,String>> hmEmpwiseTaskDayCount = new HashMap<String, Map<String,String>>();
			Map<String, String> hmProjectDetails = new HashMap<String,String>();
			while(rs.next()) {
				Map<String, String> hmInner = hmManagerwiseTaskDayCount.get(rs.getString("approved_by")+"_"+rs.getString("pro_id"));
				if(hmInner==null) hmInner = new HashMap<String, String>();
				
				Map<String, String> hmInner1 = hmEmpwiseTaskDayCount.get(rs.getString("emp_id")+"_"+rs.getString("pro_id"));
				if(hmInner1==null) hmInner1 = new HashMap<String, String>();
				
				double dblProjectTaskDaysCnt = uF.parseToDouble(hmInner.get(rs.getString("emp_id")+"_"+rs.getString("pro_id")));
				double dblProjectTaskDaysCnt1 = uF.parseToDouble(hmInner1.get(rs.getString("emp_id")+"_"+rs.getString("pro_id")+"_MAN_DAYS"));
				
				if(rs.getDouble("actual_hrs")<=5 && rs.getDouble("actual_hrs")>1) {
					dblProjectTaskDaysCnt += 0.5;
					dblProjectTaskDaysCnt1 += 0.5;
				} else if(rs.getDouble("actual_hrs")>5) {
					dblProjectTaskDaysCnt += 1;
					dblProjectTaskDaysCnt1 += 1;
				}
				
				hmInner.put(rs.getString("emp_id")+"_"+rs.getString("pro_id"), dblProjectTaskDaysCnt+"");
				hmManagerwiseTaskDayCount.put(rs.getString("approved_by")+"_"+rs.getString("pro_id"), hmInner);
				hmProjectDetails.put(rs.getString("approved_by")+"_"+rs.getString("pro_id")+"_"+"PRO_NAME", rs.getString("pro_name"));
				hmProjectDetails.put(rs.getString("approved_by")+"_"+rs.getString("pro_id")+"_"+"CLIENT_NAME", rs.getString("client_name"));
				
				hmInner1.put(rs.getString("emp_id")+"_"+rs.getString("pro_id")+"_MAN_DAYS", dblProjectTaskDaysCnt1+"");
				hmInner1.put(rs.getString("emp_id")+"_"+rs.getString("pro_id")+"_PRO_NAME", rs.getString("pro_name"));
				hmInner1.put(rs.getString("emp_id")+"_"+rs.getString("pro_id")+"_CLIENT_NAME", rs.getString("client_name"));
				hmInner1.put(rs.getString("emp_id")+"_"+rs.getString("pro_id")+"_APPROVED_BY", rs.getString("approved_by"));
				hmEmpwiseTaskDayCount.put(rs.getString("emp_id")+"_"+rs.getString("pro_id"), hmInner1);
			}
			rs.close();
			pst.close();*/
			
			Map<String, String> hmProClientMap = CF.getProjectClientMap(con, uF);
			Map<String,String> hmProjectNameMap = CF.getProjectNameMap(con);
			
			sbQuery.append("select task_date,ta.emp_id,sum(ta.actual_hrs) as actual_hrs,ta.approved_by,ai.pro_id,ta.client_id from task_activity ta,activity_info ai where ta.activity_id=ai.task_id and ta.is_approved > 1 ");
			sbQuery.append(" and ta.emp_id in (select emp_per_id from employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id=eod.emp_id and approved_flag=true ");
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
			sbQuery.append(" order by emp_fname) ");
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and task_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"'");
			}
			if(getPro_id() != null && getPro_id().length > 0){
				sbQuery.append(" and ai.pro_id in("+StringUtils.join(getPro_id(), ",")+") ");
			}
			if(getClient() != null && getClient().length > 0){
				sbQuery.append(" and ta.client_id in("+StringUtils.join(getClient(), ",")+") ");
			}
			sbQuery.append(" group by ta.emp_id,task_date,ta.approved_by,ai.pro_id,ta.client_id order by ta.emp_id,task_date ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			StringBuilder sbEmpIds = null;
			Map<String, Map<String, String>> hmManagerwiseTaskDayCount = new HashMap<String, Map<String,String>>();
			Map<String, Map<String,String>> hmEmpwiseTaskDayCount = new HashMap<String, Map<String,String>>();
			Map<String, String> hmProjectDetails = new HashMap<String,String>();
			while(rs.next()) {
				Map<String, String> hmInner = hmManagerwiseTaskDayCount.get(rs.getString("approved_by")+"_"+rs.getString("pro_id"));
				if(hmInner==null) hmInner = new HashMap<String, String>();
				
				Map<String, String> hmInner1 = hmEmpwiseTaskDayCount.get(rs.getString("emp_id")+"_"+rs.getString("pro_id"));
				if(hmInner1==null) hmInner1 = new HashMap<String, String>();
				
				double dblProjectTaskDaysCnt = uF.parseToDouble(hmInner.get(rs.getString("emp_id")+"_"+rs.getString("pro_id")));
				double dblProjectTaskDaysCnt1 = uF.parseToDouble(hmInner1.get(rs.getString("emp_id")+"_"+rs.getString("pro_id")+"_MAN_DAYS"));
				
				if(rs.getDouble("actual_hrs")<=5 && rs.getDouble("actual_hrs")>1) {
					dblProjectTaskDaysCnt += 0.5;
					dblProjectTaskDaysCnt1 += 0.5;
				} else if(rs.getDouble("actual_hrs")>5) {
					dblProjectTaskDaysCnt += 1;
					dblProjectTaskDaysCnt1 += 1;
				}
				
				hmInner.put(rs.getString("emp_id")+"_"+rs.getString("pro_id"), dblProjectTaskDaysCnt+"");
				hmManagerwiseTaskDayCount.put(rs.getString("approved_by")+"_"+rs.getString("pro_id"), hmInner);
				hmProjectDetails.put(rs.getString("approved_by")+"_"+rs.getString("pro_id")+"_"+"PRO_NAME", hmProjectNameMap.get(rs.getString("pro_id")));
				hmProjectDetails.put(rs.getString("approved_by")+"_"+rs.getString("pro_id")+"_"+"CLIENT_NAME", hmProClientMap.get(rs.getString("client_id")));
				
				hmInner1.put(rs.getString("emp_id")+"_"+rs.getString("pro_id")+"_MAN_DAYS", dblProjectTaskDaysCnt1+"");
				hmInner1.put(rs.getString("emp_id")+"_"+rs.getString("pro_id")+"_PRO_NAME", hmProjectNameMap.get(rs.getString("pro_id")));
				hmInner1.put(rs.getString("emp_id")+"_"+rs.getString("pro_id")+"_CLIENT_NAME", hmProClientMap.get(rs.getString("client_id")));
				hmInner1.put(rs.getString("emp_id")+"_"+rs.getString("pro_id")+"_APPROVED_BY", rs.getString("approved_by"));
				hmEmpwiseTaskDayCount.put(rs.getString("emp_id")+"_"+rs.getString("pro_id"), hmInner1);
			}
			rs.close();
			pst.close();
			
			List<List<String>> alOuter = new ArrayList<List<String>>();
			
			
			/*Iterator<String> it = hmManagerwiseTaskDayCount.keySet().iterator();
			int cnt = 1;
			while(it.hasNext()){
				List<String> innerList = new ArrayList<String>();
				Map<String, String> hmProMap = new HashMap<String, String>();
				String key = it.next();
				String[] tempKey = key.split("_");
				String managerId = tempKey[0];
				String proId = tempKey[1];
				hmProMap = hmManagerwiseTaskDayCount.get(key);
				
				Iterator<String> it1 = hmProMap.keySet().iterator();
				double totalDays = 0;
				StringBuilder sbEmpName = new StringBuilder();
				int x =0;
				while(it1.hasNext()){
					String strKey = it1.next();
					String[] arr = strKey.split("_");
					String empId = arr[0];
					totalDays += uF.parseToDouble(hmProMap.get(strKey));
					if(x == 0){
						sbEmpName.append(hmEmpName.get(empId));
					} else{
						sbEmpName.append(","+hmEmpName.get(empId));
					}
					x++;
				}
				innerList.add(cnt+"");
				innerList.add(hmEmpName.get(managerId));
				innerList.add(hmProjectDetails.get(key+"_"+"CLIENT_NAME"));
				innerList.add(hmProjectDetails.get(key+"_"+"PRO_NAME"));
				innerList.add(sbEmpName.toString());
				innerList.add(totalDays+"");
				alOuter.add(innerList);
				
				cnt++;
			}*/
			
			Iterator<String> it = hmEmpwiseTaskDayCount.keySet().iterator();
			int cnt = 1;
			while(it.hasNext()){
				List<String> innerList = new ArrayList<String>();
				Map<String, String> hmProMap = new HashMap<String, String>();
				String key = it.next();
				String[] tempKey = key.split("_");
				String empId = tempKey[0];
				String proId = tempKey[1];
				hmProMap = hmEmpwiseTaskDayCount.get(key);
				innerList.add(cnt+"");
				innerList.add(hmEmpName.get(hmProMap.get(empId+"_"+proId+"_APPROVED_BY")));
				innerList.add(hmProMap.get(empId+"_"+proId+"_"+"CLIENT_NAME"));
				innerList.add(hmProMap.get(empId+"_"+proId+"_"+"PRO_NAME"));
				innerList.add(hmEmpName.get(empId));
				innerList.add(hmProMap.get(empId+"_"+proId+"_MAN_DAYS"));
				alOuter.add(innerList);
				
				cnt++;
			}
			
			request.setAttribute("alOuter", alOuter);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs); 
			db.closeStatements(pst);  
			db.closeConnection(con);
		}
	}
	
	public String loadManDaysUtilizationReport(UtilityFunctions uF) {
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
		
		projectdetailslist = new FillProjectList(request).fillProjectAllDetails();
		clientList = new FillClients(request).fillClients(false);
		
		getSelectedFilter(uF);
		 
		return SUCCESS;
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
		
		
		alFilter.add("PROJECT");
		if(getPro_id()!=null) {
			String strProjects="";
			int k=0;
			for(int i=0;projectdetailslist!=null && i<projectdetailslist.size();i++) {
				for(int j=0;j<getPro_id().length;j++) {
					if(getPro_id()[j].equals(projectdetailslist.get(i).getProjectID())) {
						if(k==0) {
							strProjects=projectdetailslist.get(i).getProjectName();
						} else {
							strProjects+=", "+projectdetailslist.get(i).getProjectName();
						}
						k++;
					}
				}
			}
			if(strProjects!=null && !strProjects.equals("")) {
				hmFilter.put("PROJECT", strProjects);
			} else {
				hmFilter.put("PROJECT", "All Projects");
			}
		} else {
			hmFilter.put("PROJECT", "All Projects");
		}
		
		alFilter.add("CLIENT");
		if(getClient()!=null) {
			String strClient="";
			int k=0;
			for(int i=0; clientList!=null && i<clientList.size();i++) {
				for(int j=0;j<getClient().length;j++) {
					if(getClient()[j].equals(clientList.get(i).getClientId())) {
						if(k==0) {
							strClient=clientList.get(i).getClientName();
						} else {
							strClient+=", "+clientList.get(i).getClientName();
						}
						k++;
					}
				}
			}
			if(strClient!=null && !strClient.equals("")) {
				hmFilter.put("CLIENT", strClient);
			} else {
				hmFilter.put("CLIENT", "All Clients");
			}
		} else {
			hmFilter.put("CLIENT", "All Clients");
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

	public List<FillProjectList> getProjectdetailslist() {
		return projectdetailslist;
	}

	public void setProjectdetailslist(List<FillProjectList> projectdetailslist) {
		this.projectdetailslist = projectdetailslist;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
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
}
