package com.konnect.jpms.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class ViewTaskIdealTime extends ActionSupport implements ServletRequestAware,ServletResponseAware ,IStatements,IConstants {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF = null;

	String strSessionEmpId;
	String strEmpOrgId;
	String strUserType;
	String strSessionOrgId;
	String f_org;
	String f_strWLocation;
	String f_department;
	String f_level;
	String strEmpId;
	String dataType;
	String f_start; 
	String f_end;
	String proType; 
	String sortBy;
	
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillEmployee> empList;
	
	private static final String FORMAT = "%2d:%02d:%02d";
	
	public String execute() throws Exception {
		request.setAttribute(PAGE,  "/jsp/task/ViewTaskIdealTime.jsp");
		request.setAttribute(TITLE, "Desktop Activity Report");
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		session = request.getSession();
		
		if (CF == null)
			return LOGIN;
		 
		strSessionEmpId = (String)session.getAttribute(EMPID); 
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		UtilityFunctions uF = new UtilityFunctions();
		if(getProType()!=null && getProType().trim().equalsIgnoreCase("L")){
//			System.out.println("Get ProType==PPPP");
		} else if(getProType()!=null && getProType().trim().equalsIgnoreCase("R")){
//			System.out.println("Get ProType==LW");
		}
		
		if(uF.parseToInt(getF_strWLocation())==0){
			setF_strWLocation((String)session.getAttribute(WLOCATIONID));
		}
		
		if(uF.parseToInt(getF_org()) == 0){
			setF_org(strSessionOrgId);
		}
		
		if(getF_start() == null && getF_end() == null) {
			setF_start(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
			setF_end(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
		}
		loadValidateEmpActivity();
		getSelectedFilter(uF);
		
		if(uF.parseToInt(getSortBy()) == 2) {
			getDeaktopActivityReportByEmployee();
		} else {
			getDeaktopActivityReportByTime();
		}
		getTotalOnlineTime();

		return SUCCESS;
	}

	public String loadValidateEmpActivity() {

		UtilityFunctions uF = new UtilityFunctions();
		
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		String userlocation = getOrgLocationIds(uF, getF_org());
		empList = new FillEmployee(request).fillEmployeeNameByLocation(userlocation);
		organisationList = new FillOrganisation(request).fillOrganisation();
		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		return LOAD;
	}
    
	public String getOrgLocationIds(UtilityFunctions uF, String orgId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder locationIds = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select wlocation_id from work_location_info ");
			
			if(uF.parseToInt(orgId)>0) {
				sbQuery.append(" where org_id= "+uF.parseToInt(orgId)+" ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			
			while (rs.next()) {
				if(locationIds == null) {
					locationIds = new StringBuilder();
					locationIds.append(rs.getString("wlocation_id"));
				} else {
					locationIds.append(","+rs.getString("wlocation_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(locationIds == null) {
				locationIds = new StringBuilder();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return locationIds.toString();
	}
    
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORG");
		if(getF_org() != null) {
			String strOrg="";
			for(int i=0;organisationList!=null && i<organisationList.size();i++) {
				if(getF_org().equals(organisationList.get(i).getOrgId())) {
					strOrg=organisationList.get(i).getOrgName();
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORG", strOrg);
			} else {
				hmFilter.put("ORG", "All Organisations");
			}
		} else {
			hmFilter.put("ORG", "All Organisations");
		}
		
		alFilter.add("LOCATION");
		if(getF_strWLocation() != null) {
			String strWloc="";
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				if(getF_strWLocation().equals(wLocationList.get(i).getwLocationId())) {
					strWloc=wLocationList.get(i).getwLocationName();
				}
			}
			if(strWloc!=null && !strWloc.equals("")) {
				hmFilter.put("LOCATION", strWloc);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
		
		alFilter.add("DEPT");
		if(getF_department() != null) {
			String strDept="";
			for(int i=0;departmentList!=null && i<departmentList.size();i++) {
				if(getF_department().equals(departmentList.get(i).getDeptId())) {
					strDept=departmentList.get(i).getDeptName();
				}
			}
			if(strDept!=null && !strDept.equals("")) {
				hmFilter.put("DEPT", strDept);
			} else {
				hmFilter.put("DEPT", "All Departments");
			}
		} else {
			hmFilter.put("DEPT", "All Departments");
		}
		
		alFilter.add("LEVEL");
		if(getF_level() != null) {
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				if(getF_level().equals(levelList.get(i).getLevelId())) {
					strLevel=levelList.get(i).getLevelCodeName();
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
		
		alFilter.add("EMPLOYEE");
		if(getStrEmpId() != null) {
			String strEmployee="";
			for(int i=0;empList!=null && i<empList.size();i++) {
				if(getStrEmpId().equals(empList.get(i).getEmployeeId())) {
					strEmployee=empList.get(i).getEmployeeCode();
				}
			}
			if(strEmployee!=null && !strEmployee.equals("")) {
				hmFilter.put("EMPLOYEE", strEmployee);
			} else {
				hmFilter.put("EMPLOYEE", "All Employees");
			}
		} else {
			hmFilter.put("EMPLOYEE", "All Employees");
		}
		
		if(getF_start() != null) {
			alFilter.add("FROM_DATE");
			hmFilter.put("FROM_DATE", uF.getDateFormat(getF_start(), DATE_FORMAT, DATE_FORMAT_STR));
		}
		
		if(getF_end() != null) {
			alFilter.add("TO_DATE");
			hmFilter.put("TO_DATE", uF.getDateFormat(getF_end(), DATE_FORMAT, DATE_FORMAT_STR));
		}
		
		String selectedFilter=getSelectedFilter(uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	public String getSelectedFilter(UtilityFunctions uF, List<String> alFilter, Map<String, String> hmFilter) {
		StringBuilder sbFilter=new StringBuilder("<strong>Filter Summary: </strong>");
		
		for(int i=0;alFilter!=null && i<alFilter.size();i++) {
			if(i>0) {
				sbFilter.append(", ");
			} 
			if(alFilter.get(i).equals("ORG")) {
				sbFilter.append("<strong>Organisation:</strong> ");
				sbFilter.append(hmFilter.get("ORG"));
			
			} else if(alFilter.get(i).equals("LOCATION")) {
				sbFilter.append("<strong>Work Location:</strong> ");
				sbFilter.append(hmFilter.get("LOCATION"));
			
			} else if(alFilter.get(i).equals("DEPT")) {
				sbFilter.append("<strong>Department:</strong> ");
				sbFilter.append(hmFilter.get("DEPT"));
			
			} else if(alFilter.get(i).equals("LEVEL")) {
				sbFilter.append("<strong>Level:</strong> ");
				sbFilter.append(hmFilter.get("LEVEL"));
			
			} else if(alFilter.get(i).equals("EMPLOYEE")) {
				sbFilter.append("<strong>Employee:</strong> ");
				sbFilter.append(hmFilter.get("EMPLOYEE"));
			
			} else if(alFilter.get(i).equals("FROM_DATE")) {
				sbFilter.append("<strong>From:</strong> ");
				sbFilter.append(hmFilter.get("FROM_DATE"));
			
			} else if(alFilter.get(i).equals("TO_DATE")) {
				sbFilter.append("<strong>To:</strong> ");
				sbFilter.append(hmFilter.get("TO_DATE"));
			}
		}
		return sbFilter.toString();
	}
    
    public String getDeaktopActivityReportByEmployee() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmProjectName = CF.getProjectNameMap(con);
			Map<String, String> hmTaskName = CF.getTaskNameMap(con);
			
			StringBuilder sbEmpIds = null;
			if(uF.parseToInt(getStrEmpId()) == 0) {
				for(int i=0; empList!=null && i<empList.size();i++) {
					if(sbEmpIds == null) {
						sbEmpIds = new StringBuilder();
						sbEmpIds.append(empList.get(i).getEmployeeId());
					} else {
						sbEmpIds.append(","+empList.get(i).getEmployeeId());
					}
				}
			}
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from ideal_time_details where entry_date between ? AND ? ");
			
			if(uF.parseToInt(getStrEmpId()) > 0) {
				sbQuery.append(" and emp_id = "+uF.parseToInt(getStrEmpId())+" ");
			} else if(sbEmpIds != null) {
				sbQuery.append(" and emp_id in ("+sbEmpIds.toString()+") ");
			}
			sbQuery.append(" order by entry_time desc");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getF_start(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getF_end(), DATE_FORMAT));
			rst = pst.executeQuery();
			
			Map<String, Map<String, Map<String, List<List<String>>>>> hmDatewiseHours = new LinkedHashMap<String, Map<String, Map<String, List<List<String>>>>>();
			Map<String, Map<String, List<List<String>>>> hmHourwiseData = new LinkedHashMap<String, Map<String, List<List<String>>>>();
			Map<String, List<List<String>>> hmEmpwiseData = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alOuterData = new ArrayList<List<String>>();
			String prevStatus = null;
			String status = null;
			String prevEntryTime = null;
			String entryTime = null;
			String prevEmpId = null;
			String empId = null;
			String preWTime=null;
			String currWTime=null;
			
			while (rst.next()) {
				
				hmHourwiseData = hmDatewiseHours.get(rst.getString("entry_date"));
				if(hmHourwiseData == null) hmHourwiseData = new LinkedHashMap<String, Map<String,List<List<String>>>>();
				
				int intHour = rst.getTimestamp("entry_time").getHours();
				hmEmpwiseData = hmHourwiseData.get(intHour+"");
				if(hmEmpwiseData == null) hmEmpwiseData = new LinkedHashMap<String, List<List<String>>>();
				
				alOuterData = hmEmpwiseData.get(rst.getString("emp_id"));
				if(alOuterData == null) alOuterData = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				
				entryTime = uF.getTimeFormatStr(rst.getString("entry_time"), DBTIMESTAMP, DBDATE+" "+DBTIME);
				status = rst.getString("status");
				empId = rst.getString("emp_id");
				
				if(prevEntryTime == null || !entryTime.equals(prevEntryTime) || (entryTime.equals(prevEntryTime) && uF.parseToInt(status) != uF.parseToInt(prevStatus)) || (entryTime.equals(prevEntryTime) && uF.parseToInt(status) == uF.parseToInt(prevStatus) && uF.parseToInt(empId) != uF.parseToInt(prevEmpId))) {
					
					innerList.add(rst.getString("ideal_id"));
					innerList.add(getTaskTime(rst.getTimestamp("entry_time")));
					innerList.add(uF.showData(hmTaskName.get(rst.getString("task_id")), ""));
					innerList.add(uF.showData(hmProjectName.get(rst.getString("pro_id")), ""));
					innerList.add(rst.getString("screen_shot_id")); //4
					if(rst.getInt("status") == 1) {
						innerList.add("<img title=\"Online\" src=\"images1/icons/bullet_green.png\" style=\"margin-top: 4px\">");
					} else {
						innerList.add("<img title=\"Offline\" src=\"images1/icons/bullet_grey.png\" style=\"margin-top: 4px\">");
					}
					
					innerList.add(CF.getEmpNameMapByEmpId(con, rst.getString("emp_id"))); //6
					innerList.add(rst.getString("emp_id")); //7
					innerList.add(rst.getString("freq_status")); //8
					
					if(rst.getString("top_application_name")!=null && !rst.getString("top_application_name").equals("")){
						innerList.add(rst.getString("top_application_name"));
					}else{
						innerList.add("");
					}
					innerList.add(""+rst.getInt("status"));
					currWTime=rst.getString("entry_time");
					 double percentage=0.0;
					if(rst.getInt("status")==1){
						Time frmTime = uF.getTimeFormat(preWTime, DBTIMESTAMP);
						Time toTime = uF.getTimeFormat(currWTime, DBTIMESTAMP);
						String DBTIME1="HH:mm:ss";
						
						if(frmTime!=null && toTime!=null){
							
							long lngTime = uF.getTimeDifference(toTime.getTime(),frmTime.getTime());
							
							int hoursHand=uF.getTimeFormat(getHoursAndMinutes(lngTime), DBTIME1).getHours();
							int minHand=uF.getTimeFormat(getHoursAndMinutes(lngTime), DBTIME1).getMinutes();
							int secHand=uF.getTimeFormat(getHoursAndMinutes(lngTime), DBTIME1).getSeconds();
							int totalStroke=rst.getInt("key_stroke_count")+rst.getInt("mouse_stroke_count");
							int defaultStrokes=50;
							long iPart;
							double fPart;
							if(hoursHand==0 && minHand==0 ){
									 double reaminMin=secHand*defaultStrokes;
									 double willStroke=(double)(reaminMin/60);
									 percentage=(double)(totalStroke/willStroke)*100;
									 iPart = (long) percentage;
									 fPart = percentage - iPart;
									 if(fPart>.50){
										  percentage=iPart+1;
									  }else{
										  percentage=iPart;
									  }
							}else if(hoursHand==0 && minHand!=0 ){
								
									 minHand=minHand*60;
									 secHand=secHand+minHand;
									 double reaminMin=secHand*defaultStrokes;
									 double willStroke=(double)(reaminMin/60);
									 percentage=(double)(totalStroke/willStroke)*100;
									  iPart = (long) percentage;
									  fPart = percentage - iPart;
									  if(fPart>.50){
										  percentage=iPart+1;
									  }else{
										  percentage=iPart;
									  }
							}else if(hoursHand!=0){
	  								 hoursHand=hoursHand*60*60;
									 minHand=minHand*60;
									 secHand=secHand+minHand+hoursHand;
									 double reaminMin=secHand*defaultStrokes;
									 double willStroke=(double)(reaminMin/60);
									 percentage=(double)(totalStroke/willStroke)*100;
									 iPart = (long) percentage;
									  fPart = percentage - iPart;
									  if(fPart>.50){
										  percentage=iPart+1;
									  }else{
										  percentage=iPart;
									  }
							}
						}
						
					}
					innerList.add(""+percentage);
					
					File file= new File(getSnaphots(con,uF,rst.getString("emp_id"),rst.getString("screen_shot_id"))); 
					 	if(file.exists()){
							InputStream stream = new FileInputStream(file);
							byte[] outputArray = IOUtils.toByteArray(stream);
							byte[] outputArray1 = uF.scale(outputArray, 153, 86, "jpg");
							String encodedImage = Base64.encode(outputArray1);
							String imageSrc = "data:image/jpg;base64,"+ encodedImage;
							innerList.add(imageSrc); //12
					 	}else{
					 		innerList.add("null");
					 	}
					alOuterData.add(innerList);
					
					hmEmpwiseData.put(rst.getString("emp_id"), alOuterData);
					hmHourwiseData.put(intHour+"", hmEmpwiseData);
					hmDatewiseHours.put(rst.getString("entry_date"), hmHourwiseData);
				}
				preWTime = rst.getString("entry_time");
				prevEntryTime = uF.getTimeFormatStr(rst.getString("entry_time"), DBTIMESTAMP, DBDATE+" "+DBTIME);
				prevStatus = rst.getString("status");
				prevEmpId = rst.getString("emp_id");
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmDatewiseHours", hmDatewiseHours);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	public String getDeaktopActivityReportByTime() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmProjectName = CF.getProjectNameMap(con);
			Map<String, String> hmTaskName = CF.getTaskNameMap(con);
			
			StringBuilder sbEmpIds = null;
			if(uF.parseToInt(getStrEmpId()) == 0) {
				for(int i=0; empList!=null && i<empList.size();i++) {
					if(sbEmpIds == null) {
						sbEmpIds = new StringBuilder();
						sbEmpIds.append(empList.get(i).getEmployeeId());
					} else {
						sbEmpIds.append(","+empList.get(i).getEmployeeId());
					}
				}
			}
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from ideal_time_details where entry_date between ? AND ? ");
			
			if(uF.parseToInt(getStrEmpId()) > 0) {
				sbQuery.append(" and emp_id = "+uF.parseToInt(getStrEmpId())+" ");
			} else if(sbEmpIds != null) {
				sbQuery.append(" and emp_id in ("+sbEmpIds.toString()+") ");
			}
			sbQuery.append(" order by entry_time desc");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getF_start(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getF_end(), DATE_FORMAT));
			rst = pst.executeQuery();
			
			Map<String, Map<String, List<List<String>>>> hmDatewiseHours = new LinkedHashMap<String, Map<String, List<List<String>>>>();
			Map<String, List<List<String>>> hmHourwiseData = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alOuterData = new ArrayList<List<String>>();
			String prevStatus = null;
			String status = null;
			String prevEntryTime = null;
			String entryTime = null;
			String prevEmpId = null;
			String empId = null;
			String preWTime=null;
			String currWTime=null;
			
			while (rst.next()) {
		
				hmHourwiseData = hmDatewiseHours.get(rst.getString("entry_date"));
				if(hmHourwiseData == null) hmHourwiseData = new LinkedHashMap<String, List<List<String>>>();
				
				int intHour = rst.getTimestamp("entry_time").getHours();
				alOuterData = hmHourwiseData.get(intHour+"");
				if(alOuterData == null) alOuterData = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				
				entryTime = uF.getTimeFormatStr(rst.getString("entry_time"), DBTIMESTAMP, DBDATE+" "+DBTIME);
				status = rst.getString("status");
				empId = rst.getString("emp_id");
				
				if(prevEntryTime == null || !entryTime.equals(prevEntryTime) || (entryTime.equals(prevEntryTime) && uF.parseToInt(status) != uF.parseToInt(prevStatus)) || (entryTime.equals(prevEntryTime) && uF.parseToInt(status) == uF.parseToInt(prevStatus) && uF.parseToInt(empId) != uF.parseToInt(prevEmpId))) {
					
					innerList.add(rst.getString("ideal_id"));
					innerList.add(getTaskTime(rst.getTimestamp("entry_time")));
					innerList.add(uF.showData(hmTaskName.get(rst.getString("task_id")), ""));
					innerList.add(uF.showData(hmProjectName.get(rst.getString("pro_id")), ""));
					innerList.add(rst.getString("screen_shot_id")); //4
					if(rst.getInt("status") == 1) {
						innerList.add("<img title=\"Online\" src=\"images1/icons/bullet_green.png\" style=\"margin-top: 4px\">");
					} else {
						innerList.add("<img title=\"Offline\" src=\"images1/icons/bullet_grey.png\" style=\"margin-top: 4px\">");
					}
					innerList.add(CF.getEmpNameMapByEmpId(con, rst.getString("emp_id"))); //6
					innerList.add(rst.getString("emp_id")); //7
					innerList.add(rst.getString("freq_status")); //8
					
					if(rst.getString("top_application_name")!=null && !rst.getString("top_application_name").equals("")){
						innerList.add(rst.getString("top_application_name"));
					}else{
						innerList.add("");
					}
					innerList.add(""+rst.getInt("status"));//10
					 currWTime=rst.getString("entry_time");
					 double percentage=0.0;
					 
					if(rst.getInt("status")==1){
						Time frmTime = uF.getTimeFormat(preWTime, DBTIMESTAMP);
						Time toTime = uF.getTimeFormat(currWTime, DBTIMESTAMP);
						String DBTIME1="HH:mm:ss";
						if(frmTime!=null && toTime!=null){
							long lngTime = uF.getTimeDifference(toTime.getTime(), frmTime.getTime());
							int hoursHand=uF.getTimeFormat(getHoursAndMinutes(lngTime), DBTIME1).getHours();
							int minHand=uF.getTimeFormat(getHoursAndMinutes(lngTime), DBTIME1).getMinutes();
							int secHand=uF.getTimeFormat(getHoursAndMinutes(lngTime), DBTIME1).getSeconds();
							int totalStroke=rst.getInt("key_stroke_count")+rst.getInt("mouse_stroke_count");
							int defaultStrokes=50;
							  long iPart;
							  double fPart;
							if(hoursHand==0 && minHand==0 ){
								
									 double reaminMin=secHand*defaultStrokes;
									 double willStroke=(double)(reaminMin/60);
									 percentage=(double)(totalStroke/willStroke)*100;
									 iPart = (long) percentage;
									  fPart = percentage - iPart;
									  if(fPart>.50){
										  percentage=iPart+1;
									  }else{
										  percentage=iPart;
									  }
							}else if(hoursHand==0 && minHand!=0 ){
								
									 minHand=minHand*60;
									 secHand=secHand+minHand;
									 double reaminMin=secHand*defaultStrokes;
									 double willStroke=(double)(reaminMin/60);
									 percentage=(double)(totalStroke/willStroke)*100;
									  iPart = (long) percentage;
									  fPart = percentage - iPart;
									  if(fPart>.50){
										  percentage=iPart+1;
									  }else{
										  percentage=iPart;
									  }
							}else if(hoursHand!=0){
	   								 hoursHand=hoursHand*60*60;
									 minHand=minHand*60;
									 secHand=secHand+minHand+hoursHand;
									 double reaminMin=secHand*defaultStrokes;
									 double willStroke=(double)(reaminMin/60);
									 percentage=(double)(totalStroke/willStroke)*100;
									 iPart = (long) percentage;
									  fPart = percentage - iPart;
									  if(fPart>.50){
										  percentage=iPart+1;
									  }else{
										  percentage=iPart;
									  }
							}
						}
					}
				  
				   innerList.add(""+percentage);//11
				   	File file= new File(getSnaphots(con,uF,rst.getString("emp_id"),rst.getString("screen_shot_id"))); 
				 	if(file.exists()){
						InputStream stream = new FileInputStream(file);
						byte[] outputArray = IOUtils.toByteArray(stream);
						byte[] outputArray1 = uF.scale(outputArray, 153, 86, "jpg");
						String encodedImage = Base64.encode(outputArray1);
						String imageSrc = "data:image/jpg;base64,"+ encodedImage;
						
						innerList.add(imageSrc); //12
				 	}else{
				 		innerList.add("null"); //12
				 	}
				   
				   alOuterData.add(innerList);
				   hmHourwiseData.put(intHour+"", alOuterData);
				   hmDatewiseHours.put(rst.getString("entry_date"), hmHourwiseData);
				}
				
				preWTime = rst.getString("entry_time");
				prevEntryTime=uF.getTimeFormatStr(rst.getString("entry_time"), DBTIMESTAMP, DBDATE+DBTIME);
				prevStatus = rst.getString("status");
				prevEmpId = rst.getString("emp_id");
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmDatewiseHours", hmDatewiseHours);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
    
	// total online time
	public void getTotalOnlineTime() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		long totalWorkingHours = 0 ;
		long totalIdealHours = 0;
		long totalHours = 0;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select min(entry_time) as mintm, max(entry_time) as maxtm, entry_date, emp_id from ideal_time_details " +
				" where entry_date between ? and ? ");
			
			if(uF.parseToInt(getStrEmpId()) > 0) {
				sbQuery.append(" and emp_id = "+uF.parseToInt(getStrEmpId())+" ");
			}
			sbQuery.append(" group by emp_id,entry_date");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getF_start(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getF_end(), DATE_FORMAT));
			rst = pst.executeQuery();
			
			Map<String, Map<String, List<String>>> hmDatewiseData = new LinkedHashMap<String, Map<String, List<String>>>();
			Map<String, List<String>> hmEmpwiseData = new LinkedHashMap<String, List<String>>();
			
			while (rst.next()) {
				
				hmEmpwiseData = hmDatewiseData.get(rst.getString("entry_date"));
				if(hmEmpwiseData == null) hmEmpwiseData = new LinkedHashMap<String, List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("mintm"));
				innerList.add(rst.getString("maxtm")); //2
				
				hmEmpwiseData.put(rst.getString("emp_id"), innerList);
				
				hmDatewiseData.put(rst.getString("entry_date"), hmEmpwiseData);
			}
			rst.close();
			pst.close();
			
			Iterator<String> itDate = hmDatewiseData.keySet().iterator();
			while (itDate.hasNext()) {
				String strDate = (String) itDate.next();
				Map<String, List<String>> hmEmpwiseData1 = hmDatewiseData.get(strDate);
				Iterator<String> itEmps = hmEmpwiseData1.keySet().iterator();
				while (itEmps.hasNext()) {
					String strEmpId = (String) itEmps.next();
					List<String> innerList = hmEmpwiseData1.get(strEmpId);
					Time frmTime = uF.getTimeFormat(innerList.get(0), DBDATE+DBTIME);
					Time toTime = uF.getTimeFormat(innerList.get(1), DBDATE+DBTIME);
					long lngTime = uF.getTimeDifference(frmTime.getTime(), toTime.getTime());
					totalHours = totalHours + lngTime;
				}
			}
			String strTotalHrsAndMins = getHoursAndMinutes(totalHours);
			request.setAttribute("strTotalHrsAndMins", strTotalHrsAndMins);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from ideal_time_details where entry_date between ? AND ? ");
			
			if(uF.parseToInt(getStrEmpId()) > 0) {
				sbQuery.append(" and emp_id = "+uF.parseToInt(getStrEmpId())+" ");
			}
			sbQuery.append(" order by emp_id,entry_time");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getF_start(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getF_end(), DATE_FORMAT));
			rst = pst.executeQuery();
			
			Map<String, Map<String, List<List<String>>>> hmDatewiseEmpData = new LinkedHashMap<String, Map<String, List<List<String>>>>();
			Map<String, List<List<String>>> hmEmpwiseStatusData = new LinkedHashMap<String, List<List<String>>>();
			Map<String, List<String>> hmDateEmpwiseLastData = new LinkedHashMap<String, List<String>>();
			List<List<String>> alOuterData = new ArrayList<List<String>>();
			String prevStatus = null;
			String status = null;
			
			while (rst.next()) {
				hmEmpwiseStatusData = hmDatewiseEmpData.get(rst.getString("entry_date"));
				if(hmEmpwiseStatusData == null) { 
					hmEmpwiseStatusData = new LinkedHashMap<String, List<List<String>>>();
					prevStatus = null;
				}
				
				alOuterData = hmEmpwiseStatusData.get(rst.getString("emp_id"));
				if(alOuterData == null) {
					alOuterData = new ArrayList<List<String>>();
					prevStatus = null;
				}
				
				List<String> innerList = new ArrayList<String>();
				status = rst.getString("status");
				
				if(prevStatus == null || uF.parseToInt(status) != uF.parseToInt(prevStatus)) {
					innerList.add(rst.getString("status"));
					innerList.add(rst.getString("entry_time"));
					alOuterData.add(innerList);
					
					hmEmpwiseStatusData.put(rst.getString("emp_id"), alOuterData);
					hmDatewiseEmpData.put(rst.getString("entry_date"), hmEmpwiseStatusData);
				}

				List<String> innerEmpLastList = new ArrayList<String>();
				innerEmpLastList.add((uF.parseToInt(rst.getString("status")) == 1) ? "0" : "1");
				innerEmpLastList.add(rst.getString("entry_time"));

				hmDateEmpwiseLastData.put(rst.getString("entry_date")+"_"+rst.getString("emp_id"), innerEmpLastList);
				prevStatus = rst.getString("status");
				
			}
			rst.close();
			pst.close();
			
			Iterator<String> itDt = hmDatewiseEmpData.keySet().iterator();
			while (itDt.hasNext()) {
				String strDate = (String) itDt.next();
				Map<String, List<List<String>>> hmEmpwiseStatusData1 = hmDatewiseEmpData.get(strDate);
				Iterator<String> itEmps = hmEmpwiseStatusData1.keySet().iterator();
				
				while (itEmps.hasNext()) {
					String strEmpId = (String) itEmps.next();
					List<List<String>> outerList = hmEmpwiseStatusData1.get(strEmpId);
					outerList.add(hmDateEmpwiseLastData.get(strDate+"_"+strEmpId));
					String preWTime = null;
					String currWTime = null;
					String preITime = null;
					String currITime = null;
					for(int i=0; outerList != null && !outerList.isEmpty() && i<outerList.size(); i++) {
						List<String> innerList = outerList.get(i);
						currWTime = innerList.get(1);
						currITime = innerList.get(1);
						if(uF.parseToInt(innerList.get(0)) == 0 && preWTime != null && currWTime != null) {
							Time frmTime = uF.getTimeFormat(preWTime, DBDATE+DBTIME);
							Time toTime = uF.getTimeFormat(currWTime, DBDATE+DBTIME);
							long lngTime = uF.getTimeDifference(frmTime.getTime(), toTime.getTime());
							totalWorkingHours = totalWorkingHours + lngTime;
						} else if(uF.parseToInt(innerList.get(0)) == 1 && preITime != null && currITime != null) {
							Time frmTime = uF.getTimeFormat(preITime, DBDATE+DBTIME);
							Time toTime = uF.getTimeFormat(currITime, DBDATE+DBTIME);
							long lngTime = uF.getTimeDifference(frmTime.getTime(), toTime.getTime());
							totalIdealHours = totalIdealHours + lngTime;
						}
						preWTime = innerList.get(1);
						preITime = innerList.get(1);
					}
				}
			}
			String strWorkHrsAndMins = getHoursAndMinutes(totalWorkingHours);
			String strIdleHrsAndMins = getHoursAndMinutes(totalIdealHours);
			
			request.setAttribute("strWorkHrsAndMins", strWorkHrsAndMins);
			request.setAttribute("strIdleHrsAndMins", strIdleHrsAndMins);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	// Screen Shot Path
	public String getSnaphots(Connection con,UtilityFunctions uF, String empId, String screenShotId) {
		String mainpath=CF.getStrDocSaveLocation();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select * from task_screenshot_details where emp_id=? and screenshot_id=?");
			pst.setInt(1, Integer.parseInt(empId));
			pst.setInt(2, Integer.parseInt(screenShotId));
			rs = pst.executeQuery();
			String screenShotName=null;
			int proId=0;
			int taskId = 0;
			int orgId=0;
			while(rs.next()){
				screenShotName=rs.getString("screenshot_name").trim();
				proId=rs.getInt("project_id");
				taskId=rs.getInt("task_id");
				orgId=rs.getInt("org_id");
			}
			rs.close();
			pst.close();
			
			if(proId==0 && taskId==0){
				mainpath=mainpath+"Tracker"+"/" +orgId+"/"+empId+"/"+"screenshot"+"/"+screenShotName;
			}else{
				mainpath=mainpath+"Tracker"+"/" +orgId+"/"+proId+"/"+taskId+"/"+empId+"/"+"images"+"/"+screenShotName;
			}
			
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return mainpath;
	}
	
	// parsing Time
	 public  String getHoursAndMinutes(long milliseconds) {
	        return String.format(FORMAT, TimeUnit.MILLISECONDS.toHours(milliseconds),
	        	TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
                );
	     }

	public String getTaskTime(Timestamp stamp) {
		Date date = new Date(stamp.getTime());
    	SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_AM_PM);
    	String formattedDate = sdf.format(date);
		return formattedDate;
	}
	
	public String getTimeStampDateFormat(Timestamp stamp){
		Date date = new Date(stamp.getTime());
    	SimpleDateFormat sdf = new SimpleDateFormat(DBDATE);
    	String formattedDate = sdf.format(date);
		return formattedDate;
	}

   HttpServletRequest request;
	
	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub		
	}

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

	public String getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
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

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String getF_start() {
		return f_start;
	}

	public void setF_start(String f_start) {
		this.f_start = f_start;
	}

	public String getF_end() {
		return f_end;
	}

	public void setF_end(String f_end) {
		this.f_end = f_end;
	}

	public String getF_department() {
		return f_department;
	}

	public void setF_department(String f_department) {
		this.f_department = f_department;
	}

	public String getF_level() {
		return f_level;
	}

	public void setF_level(String f_level) {
		this.f_level = f_level;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}
	
}
