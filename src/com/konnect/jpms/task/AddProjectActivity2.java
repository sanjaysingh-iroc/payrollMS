	package com.konnect.jpms.task;

	import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillTask;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

	public class AddProjectActivity2 extends ActionSupport implements ServletRequestAware, IStatements 
	{
		private static final long serialVersionUID = 1L;
		
		
		List<FillProjectList> projectdetailslist;
		List<FillClients> clientlist;
		List<FillTask> tasklist; 
		List<FillPayCycles> paycycleList;
		String strPaycycle; 
		String clientId;
		String clientName;

		CommonFunctions CF;
		
		
		
		String frmDate;
		String toDate;
		String []taskId;
		String []strClient;
		String []strProject;
		String []totalHours;
		
		String strProject1;
		
		String strActivity;
		String type;
		
		int nDateCount = 0;
		
		String save;
		
		HttpSession session;
		String strSessionEmpId;
		
		public String execute(){
			session=request.getSession();
			CF = (CommonFunctions)session.getAttribute(CommonFunctions);
			if(CF==null) return LOGIN;
			strSessionEmpId = (String)session.getAttribute(EMPID);
			UtilityFunctions uF=new UtilityFunctions();
			
			request.setAttribute(PAGE, "/jsp/task/AddProjectActivity2.jsp"); 
			request.setAttribute(TITLE, "Timesheet");
			
			
			if(getStrEmpId()==null || (getStrEmpId()!=null && getStrEmpId().equalsIgnoreCase("NULL"))){
				setStrEmpId(strSessionEmpId);
			}
			
			
//			projectdetailslist=new FillProjectList().fillProjectDetailsByEmp(uF.parseToInt(getStrEmpId()), false);
//			clientlist=new FillClients().fillClients(uF.parseToInt(getStrEmpId()));
//			tasklist = new FillTask().fillTask(uF.parseToInt(getStrEmpId()));
			paycycleList = new FillPayCycles(request).fillPayCycles(CF);
			clientlist=new FillClients(request).fillClientsWithOther(uF.parseToInt(getStrEmpId()));
			projectdetailslist=new FillProjectList(request).fillProjectDetailsByEmpWithOther(uF.parseToInt(getStrEmpId()), false, null);
			
			String[] strPayCycleDates = null;
			if (getStrPaycycle() != null) {
				strPayCycleDates = getStrPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
				setStrPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
			}
			
			if(getSave()!=null){
				saveTaskData();
			}
			

			if(getType()!=null){
				saveTypeData();
			}
			
			
			if(request.getParameter("D")!=null && request.getParameter("D").equalsIgnoreCase("D")){
				removeTaskData(request.getParameter("strTaskId"));
			}
			
			
			if(getFrmDate()!=null && getToDate()!=null && !getFrmDate().equalsIgnoreCase("NULL") && !getToDate().equalsIgnoreCase("NULL")){
				
			}else{
				setFrmDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
				setToDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
			}
			
			
			
			fillTaskRows();
	    	getData();
	    	
	    	   	
	    	
		return SUCCESS;
		}
		
		public void removeTaskData(String strTaksId){
			
			Database db = new Database();
			db.setRequest(request);
			Connection con = null;
			PreparedStatement pst = null;
			UtilityFunctions uF=new UtilityFunctions();
			try {
				
				con = db.makeConnection(con);
				pst = con.prepareStatement("delete from task_activity where task_id = ?");
				pst.setInt(1, uF.parseToInt(strTaksId));
				pst.execute();
				pst.close();
			
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				db.closeStatements(pst);
				db.closeConnection(con);
			}
		}
		
		public void saveTaskData(){
			
			Database db = new Database();
			db.setRequest(request);
			Connection con = null;
			PreparedStatement pst = null;
			UtilityFunctions uF=new UtilityFunctions();
			try {
				
				con = db.makeConnection(con);
				
				for(int i=0; getTaskId()!=null && i<getTaskId().length && uF.parseToInt(getStrEmpId())>0; i++){
					
					if(uF.parseToInt(getTaskId()[i])>0){
						pst = con.prepareStatement("update task_activity set activity_id=?, activity=?, task_date=?, emp_id=?, actual_hrs=?, start_time=?, end_time=?, total_time=?, is_billable=? where task_id=?");
						pst.setInt(1, uF.parseToInt(getStrTask()[i]));
						if(uF.parseToInt(getStrTask()[i])>0){
							pst.setString(2, "");
						}else{
							pst.setString(2, getStrTask()[i]);
						}
						pst.setDate(3, uF.getDateFormat(getStrDate()[i], DATE_FORMAT));
						pst.setInt(4, uF.parseToInt(getStrEmpId()));
						pst.setDouble(5, uF.parseToDouble(getStrTime()[i]));
						pst.setTime(6, uF.getTimeFormat("10:00", DBTIME));
						pst.setTime(7, uF.getTimeFormat("18:00", DBTIME));
						pst.setDouble(8, uF.parseToDouble(getStrTime()[i]));
						pst.setBoolean(9, true);
						pst.setInt(10, uF.parseToInt(getTaskId()[i]));
						pst.execute();
						pst.close();
					}else{ 
						pst = con.prepareStatement("insert into task_activity (activity_id, activity, task_date, emp_id, actual_hrs, start_time, end_time, total_time, is_billable) values (?,?,?,?,?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(getStrTask()[i]));
						if(uF.parseToInt(getStrTask()[i])>0){
							pst.setString(2, "");
						}else{
							pst.setString(2, getStrTask()[i]);
						}
						pst.setDate(3, uF.getDateFormat(getStrDate()[i], DATE_FORMAT));
						pst.setInt(4, uF.parseToInt(getStrEmpId()));
						pst.setDouble(5, uF.parseToDouble(getStrTime()[i]));
						pst.setTime(6, uF.getTimeFormat("10:00", DBTIME));
						pst.setTime(7, uF.getTimeFormat("18:00", DBTIME));
						pst.setDouble(8, uF.parseToDouble(getStrTime()[i]));
						pst.setBoolean(9, true);
						pst.execute();
						pst.close();
						
//						System.out.println("pst===>"+pst);
					}
					
					
					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				db.closeStatements(pst);
				db.closeConnection(con);
			}
		}
		
		public void saveTypeData(){
			
			Database db = new Database();
			db.setRequest(request);
			Connection con = null;
			PreparedStatement pst = null;
			UtilityFunctions uF=new UtilityFunctions();
			try {
				
				con = db.makeConnection(con);
				
				
				String arr[] = null;
				if(getStrPaycycle()!=null){
					arr = getStrPaycycle().split("-");
				}
				
				if(arr!=null && arr.length>2 && getType()!=null && getType().equalsIgnoreCase("submit")){
					
					pst = con.prepareStatement("insert into project_timesheet (timesheet_paycycle, timesheet_from, timesheet_to, emp_id, timesheet_generated_date, is_approved) values (?,?,?,?,?,?)");
					pst.setInt(1, uF.parseToInt(arr[2]));
					pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
					pst.setInt(4, uF.parseToInt(getStrEmpId()));
					pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(6, 1);
					pst.execute();
					pst.close();
				}
				
				if(arr!=null && arr.length>2 && getType()!=null && getType().equalsIgnoreCase("approve")){
					
					pst = con.prepareStatement("update project_timesheet set approved_by =?, is_approved = ? where timesheet_id = ?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
					pst.setInt(2, 2);
					pst.setInt(3, uF.parseToInt(getTimesheetId()));
					pst.execute();
					pst.close();
					
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				db.closeStatements(pst);
				db.closeConnection(con);
			}
		}

		
		String []strDate;
		String []strTask;
		String []strTime;
		
		String timesheetId;
		String strEmpId;
		
		public void fillTaskRows(){
			
			Database db = new Database();
			db.setRequest(request);
			Connection con = null;
			PreparedStatement pst =null;
			ResultSet rs = null;
			UtilityFunctions uF=new UtilityFunctions();
			try {
				
				

				String[] strPayCycleDates = null;
				if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("NULL")) {
					strPayCycleDates = getStrPaycycle().split("-");
				} else {
					strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
					setStrPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
				}
				
				con = db.makeConnection(con);
						
				pst= con.prepareStatement("select * from attendance_details where emp_id =? and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and in_out = 'OUT'");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setDate(2, uF.getDateFormat(getFrmDate(), DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(getToDate(), DATE_FORMAT));
				rs = pst.executeQuery();
				
				Map<String, String> hmAttendance = new HashMap<String, String>();
				
				while(rs.next()){
					hmAttendance.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), rs.getString("hours_worked"));
				}
				rs.close();
				pst.close();
				
				
				
				
				Map<String, String> hmClientMap = new HashMap<String, String>();
				Map<String, String> hmProjectMap = new HashMap<String, String>();
				pst = con.prepareStatement("select pro_id, pro_name, cd.client_id, client_name from projectmntnc pmc, client_details cd where pmc.client_id = cd.client_id");
				rs = pst.executeQuery();
				
				while(rs.next()){
					hmClientMap.put(rs.getString("pro_id"), rs.getString("client_name"));
					hmProjectMap.put(rs.getString("pro_id"), rs.getString("pro_name"));
				}
				rs.close();
				pst.close();
				
				
				
				
				if(uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT).after(uF.getDateFormat(getFrmDate(), DATE_FORMAT))){
					setFrmDate(strPayCycleDates[0]);
				}
				if(uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT).before(uF.getDateFormat(getToDate(), DATE_FORMAT)) && uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT).before(uF.getDateFormat(getFrmDate(), DATE_FORMAT))){
					setToDate(strPayCycleDates[1]);
				}	
					
				nDateCount = uF.parseToInt(uF.dateDifference(getFrmDate(), DATE_FORMAT, getToDate(), DATE_FORMAT));

				
				
				
				
				
				

				/*pst = con.prepareStatement("select actual_hrs,activity_id, pro_id, task_date, activity_name  from task_activity ta, activity_info ai where ta.activity_id = ai.task_id and task_date between ? and ? order by pro_id");
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				rs = pst.executeQuery();
				
				System.out.println("pst==>"+pst);
				
				Map<String, String> hmDate = new HashMap<String, String>();
				Map hmProjects = new HashMap();
				
				Map<String, String> hmTasks = new HashMap<String, String>();
				Map hmProjectTasks = new HashMap();
				
				double dblTotalHrs = 0;
				int nProjectIdNew = 0;
				int nProjectIdOld = 0;
				while(rs.next()){
					
					nProjectIdNew = rs.getInt("pro_id");
					if(nProjectIdNew!=nProjectIdOld){
						dblTotalHrs = 0;
						hmDate = new HashMap<String, String>();
						hmTasks = new HashMap<String, String>();
					}
					
					double dblHrs = uF.parseToDouble((String)hmDate.get(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)));
					dblTotalHrs = dblHrs + uF.parseToDouble(rs.getString("actual_hrs")); 
					
					hmDate.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT), dblTotalHrs+"");
					hmProjects.put(rs.getString("pro_id"), hmDate);
					
					
					hmTasks.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT), rs.getString("activity_name"));
					hmProjectTasks.put(rs.getString("pro_id"), hmTasks);
					
					nProjectIdOld  = nProjectIdNew;
					
				}
				*/
				
				
				
				
				
				

//				pst = con.prepareStatement("select actual_hrs,activity_id, pro_id, task_date, activity_name  from task_activity ta, activity_info ai where ta.activity_id = ai.task_id and ta.emp_id =? and task_date between ? and ? order by activity_id");
				pst = con.prepareStatement("select actual_hrs,activity_id, pro_id, task_date, activity_name, activity  from task_activity ta left join activity_info ai on ta.activity_id = ai.task_id where ta.emp_id =? and task_date between ? and ? order by activity_id");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				
				rs = pst.executeQuery();
				
				
				Map<String, String> hmDate = new HashMap<String, String>();
				Map hmProjects = new HashMap();
				
				Map<String, String> hmTasks = new HashMap<String, String>();
				Map hmProjectTasks = new HashMap();
				
				double dblTotalHrs = 0;
				String strActivityIdNew = null;
				String strActivityIdOld = null;
				
				while(rs.next()){
					
//					nActivityIdNew = rs.getInt("activity_id");
					
					strActivityIdNew = rs.getString("activity_id");
					if(strActivityIdNew==null || uF.parseToInt(strActivityIdNew)==0){
						strActivityIdNew = rs.getString("activity");
					}
					
					
					if(strActivityIdNew!=null && !strActivityIdNew.equalsIgnoreCase(strActivityIdOld)){
						dblTotalHrs = 0;
						hmDate = new HashMap<String, String>();
						hmTasks = new HashMap<String, String>();
					}
					
					
					
					double dblHrs = uF.parseToDouble((String)hmDate.get(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)));
					dblTotalHrs = dblHrs + uF.parseToDouble(rs.getString("actual_hrs")); 
					
					hmDate.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT), uF.formatIntoTwoDecimal(dblTotalHrs));
//					hmProjects.put(rs.getString("activity_id"), hmDate);
//					hmTasks.put(rs.getString("activity_id")+"_T", rs.getString("activity_name"));
//					hmTasks.put(rs.getString("activity_id")+"_P", rs.getString("pro_id"));
//					hmProjectTasks.put(rs.getString("activity_id"), hmTasks);


					hmProjects.put(strActivityIdNew, hmDate);
					if(uF.parseToInt(rs.getString("activity_id"))==0){
						hmTasks.put(strActivityIdNew+"_T", rs.getString("activity"));
					}else{
						hmTasks.put(strActivityIdNew+"_T", rs.getString("activity_name"));
					}
					hmTasks.put(strActivityIdNew+"_P", rs.getString("pro_id"));
					hmProjectTasks.put(strActivityIdNew, hmTasks);

					
					strActivityIdOld  = strActivityIdNew;
					
				}
				rs.close();
				pst.close();
				
				
				pst = con.prepareStatement("select pro_id, ta.task_id from task_activity ta, activity_info ai where ta.activity_id = ai.task_id");
				rs = pst.executeQuery();
				Map<String, String> hmTaskProjectMap = new HashMap<String, String>();
				while(rs.next()){
					hmTaskProjectMap.put(rs.getString("task_id"), rs.getString("pro_id"));
				}
				rs.close();
				pst.close();
				
				
				
//				pst = con.prepareStatement("select actual_hrs,activity_id, ai.pro_id, task_date, activity_name , ta.task_id, activity_id, pcmc.client_id from task_activity ta, activity_info ai, projectmntnc pcmc where  ta.activity_id = ai.task_id and pcmc.pro_id = ai.pro_id and task_date between ? and ? order by pro_id");
//				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				
				

				
				if(uF.parseToInt(getStrActivity())>0){
//					pst = con.prepareStatement("select actual_hrs,activity_id, ai.pro_id, task_date, activity_name , ta.task_id, activity_id, pcmc.client_id from task_activity ta, activity_info ai, projectmntnc pcmc where  ta.activity_id = ai.task_id and pcmc.pro_id = ai.pro_id and task_date between ? and ? and ai.pro_id = ? and activity_id =?");
					pst = con.prepareStatement("select a.actual_hrs, a.pro_id, a.task_date, a.activity_name , a.task_id, a.activity_id, client_id from (select actual_hrs,activity_id, ai.pro_id, task_date, activity_name , ta.task_id from task_activity ta left join activity_info ai on ta.activity_id = ai.task_id ) a left join projectmntnc pcmc on a.pro_id = pcmc.pro_id where task_date between ? and ? and a.pro_id = ? and a.activity_id =? ");
					pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(getStrProject1()));
					pst.setInt(4, uF.parseToInt(getStrActivity()));
				}else if(uF.parseToInt(getStrProject1())>0){
					pst = con.prepareStatement("select actual_hrs,activity_id, ai.pro_id, task_date, activity_name , ta.task_id, activity_id, pcmc.client_id from task_activity ta, activity_info ai, projectmntnc pcmc where  ta.activity_id = ai.task_id and pcmc.pro_id = ai.pro_id and task_date between ? and ? and ai.pro_id = ?");
					pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(getStrProject1()));
				}else{
//					pst = con.prepareStatement("select actual_hrs,activity_id, ai.pro_id, task_date, activity_name , ta.task_id, activity_id, pcmc.client_id from task_activity ta, activity_info ai, projectmntnc pcmc where  ta.activity_id = ai.task_id and pcmc.pro_id = ai.pro_id and task_date between ? and ?  and ai.pro_id is null");
					pst = con.prepareStatement("select actual_hrs,activity_id, ai.pro_id, task_date, activity_name, activity , ta.task_id, activity_id from task_activity ta left join activity_info ai on  ta.activity_id = ai.task_id where task_date between ? and ? and ai.pro_id is null");
					pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				}
				
				
				
//				System.out.println("pst===>"+pst);
				
				rs = pst.executeQuery();
				Map hmProjectDates = new HashMap();
				List alProDates = new ArrayList();
				
				
				Map<String, String> hmTaskProjectId = new HashMap<String, String>();
				Map<String, String> hmTaskActivityId = new HashMap<String, String>();
				Map<String, String> hmTaskClientId = new HashMap<String, String>();
				Map<String, String> hmTaskHoursId = new HashMap<String, String>();
				
				
				String strDateNew = null;
				String strDateOld = null;
				while(rs.next()){
					strDateNew = uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT);
					if(strDateNew!=null && !strDateNew.equalsIgnoreCase(strDateOld)){
						alProDates = new ArrayList();
					}
					
					alProDates = (List)hmProjectDates.get(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT));
					if(alProDates==null)alProDates=new ArrayList();
					alProDates.add(rs.getString("task_id"));
					hmProjectDates.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT), alProDates);
					
					strDateOld = strDateNew;
					
					
					if(uF.parseToInt(getStrProject1())>0){
						hmTaskClientId.put(rs.getString("task_id"), rs.getString("client_id"));
						hmTaskActivityId.put(rs.getString("task_id"), rs.getString("activity_id"));
						hmTaskProjectId.put(rs.getString("task_id"), rs.getString("pro_id"));
					}else{
						hmTaskActivityId.put(rs.getString("task_id"), rs.getString("activity"));
						hmTaskProjectId.put(rs.getString("task_id"), "-1");
						hmTaskClientId.put(rs.getString("task_id"), "-1");
					}
					
					
//					hmTaskActivityId.put(rs.getString("task_id"), rs.getString("activity_id"));
//					hmTaskClientId.put(rs.getString("task_id"), rs.getString("client_id"));
					hmTaskHoursId.put(rs.getString("task_id"), rs.getString("actual_hrs"));
				}
				rs.close();
				pst.close();
				
				
				
				StringBuilder sbTasks = new StringBuilder();
				
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "MM"))-1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "yyyy")));
				
				
				StringBuilder sbTaskList = new StringBuilder("<option>Select Task</option>");
				StringBuilder sbClientList = new StringBuilder("<option>Select Client</option>");
				StringBuilder sbProjectList = new StringBuilder("<option>Select Project</option>");
				for(int i=0; tasklist!=null && i<tasklist.size(); i++){
					sbTaskList.append("<option value=\'"+tasklist.get(i).getTaskId()+"\'>"+tasklist.get(i).getTaskName()+"</option>");
				}
				for(int i=0; i<clientlist.size(); i++){
					sbClientList.append("<option value=\'"+clientlist.get(i).getClientId()+"\'>"+clientlist.get(i).getClientName()+"</option>");
				}
				for(int i=0; i<projectdetailslist.size(); i++){
					sbProjectList.append("<option value=\'"+projectdetailslist.get(i).getProjectID()+"\'>"+projectdetailslist.get(i).getProjectName()+"</option>");
				}
				
				request.setAttribute("strTaskList", sbTaskList.toString());
				request.setAttribute("strClientList", sbClientList.toString());
				request.setAttribute("strProjectList", sbProjectList.toString());
				
				
				System.out.println("sbProjectList.toString()==>"+sbProjectList.toString());
				
				for(int i=0; i<nDateCount; i++){
				
					List<String> alTaskId = (List)hmProjectDates.get(uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
					if(alTaskId==null){
						alTaskId = new ArrayList<String>();
						alTaskId.add("0");
					}
					
					
					for(int j=0; j<alTaskId.size(); j++){
						
						
						
//						tasklist = new FillTask().fillTaskByProjects(uF.parseToInt(hmTaskProjectMap.get(alTaskId.get(j))), uF.parseToInt(getStrEmpId()));
						
						if(uF.parseToInt(hmTaskProjectMap.get(alTaskId.get(j)))>0){
							tasklist = new FillTask(request).fillTaskByProjects(uF.parseToInt(hmTaskProjectMap.get(alTaskId.get(j))), uF.parseToInt(getStrEmpId()));
						}else{
							tasklist = new FillTask(request).fillExtraActivity(CF, uF.parseToInt(getStrEmpId()));
						}
						
						
						double dblVal = uF.parseToDouble(hmTaskHoursId.get(alTaskId.get(j)));
						if(dblVal==0){
							dblVal = uF.parseToDouble(hmAttendance.get(uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT)));
						}

						
						sbTasks.append(""+
					    "<div id=\""+uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT)+"_"+j+"\"><div id=\"row_task_"+i+"_"+j+"\" style=\"float:left;width:1100px;padding:2px;\">"+
						"<div style=\"float:left;width:75px;\"><input type=\"hidden\" name=\"taskId\" value=\""+alTaskId.get(j)+"\"><input class=\"validateRequired\" type=\"text\" style=\"width:62px\" name=\"strDate\" value=\""+uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT)+"\"></div>"+
						"<div style=\"float:left;width:220px;\">"+
						"<select name=\"strClient\" class=\"validateRequired\" onchange=\"getContent('myProject_"+i+"','GetProjectClientTask12.action?client_id='+this.value+'&count="+i+"')\">"+
						"<option>Select Client</option>");
						for(int c=0; c<clientlist.size(); c++){
							sbTasks.append("<option value=\""+clientlist.get(c).getClientId()+"\" "+((uF.parseToInt(clientlist.get(c).getClientId())==uF.parseToInt(hmTaskClientId.get(alTaskId.get(j))))?"selected":"")+">"+clientlist.get(c).getClientName()+"</option>");
						}
						sbTasks.append(""+
						"</select>"+
						"</div>"+
						"<div style=\"float:left;width:220px;\" id=\"myProject_"+i+"\" >"+
						"<select name=\"strProject\" class=\"validateRequired\" onchange=\"getContent('myTask_"+i+"','GetProjectClientTask12.action?project_id='+this.value)\">"+
						"<option>Select Project</option>");
						for(int c=0; c<projectdetailslist.size(); c++){
							sbTasks.append("<option value=\""+projectdetailslist.get(c).getProjectID()+"\" "+((uF.parseToInt(projectdetailslist.get(c).getProjectID())==uF.parseToInt(hmTaskProjectId.get(alTaskId.get(j))))?"selected":"")+">"+projectdetailslist.get(c).getProjectName()+"</option>");
						}
						sbTasks.append(""+
						"</select>"+
						"</div>"+
						"<div style=\"float:left;width:220px;\" id=\"myTask_"+i+"\">"+
							"<select name=\"strTask\" class=\"validateRequired\">"+
							"<option>Select Task</option>");
							for(int c=0; tasklist!=null && c<tasklist.size(); c++){
								sbTasks.append("<option value=\""+tasklist.get(c).getTaskId()+"\"  "+((tasklist.get(c).getTaskId().equalsIgnoreCase(hmTaskActivityId.get(alTaskId.get(j))))?"selected":"")+" >"+tasklist.get(c).getTaskName()+"</option>");
							}
							sbTasks.append(""+
							"</select>"+
						"</div>"+
						"<div style=\"float:left;width:150px;\"><input class=\"validateRequired\" type=\"text\" style=\"width:62px\" name=\"strTime\" value=\""+uF.showData((String)hmAttendance.get(uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT)), uF.formatIntoTwoDecimal(dblVal))+"\"></div>"+
						"<div style=\"float:left;width:100px;\"><a href=\"javascript:void(0)\" onclick=\"addTask('"+uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT)+"_"+j+"')\">Add New Task</a></div>"+
						"<div style=\"float:left;width:100px;\"><a href=\"javascript:void(0)\" onclick=\"removeTask('"+uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT)+"_"+j+"','row_task_"+i+"_"+j+"')\">Remove Task</a></div>"+
					"</div>" +
					"</div>");
					}
					cal.add(Calendar.DATE, 1);
				}
			
				request.setAttribute("sbTasks", sbTasks.toString());
				request.setAttribute("hmClientMap", hmClientMap);
				request.setAttribute("hmProjectMap", hmProjectMap);
				
				int nDateDiff = uF.parseToInt(uF.dateDifference(strPayCycleDates[0], DATE_FORMAT, strPayCycleDates[1], DATE_FORMAT));
				
				
				cal = GregorianCalendar.getInstance();
				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, "MM"))-1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, "yyyy")));
				List<String> alDates = new ArrayList<String>();
				for(int i=0; i<32; i++){
					alDates.add(uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
					cal.add(Calendar.DATE, 1);
					if(uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT).equalsIgnoreCase(strPayCycleDates[1])){
						alDates.add(uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
						break;
					}
				}
				
				
				
				Map  hmLeaveDays = new HashMap();
				Map  hmLeaveDatesType = new HashMap();
				Map  hmMonthlyLeaves = new HashMap();
				Map hmLeavesColour = new HashMap();
				CF.getLeavesColour(con, hmLeavesColour);
//				hmLeaveDays = CF.getLeaveDates(con, getFrmDate(), getToDate(), CF, hmLeaveDatesType, false, hmMonthlyLeaves);
				hmLeaveDays = CF.getActualLeaveDates(con, CF, uF, getFrmDate(), getToDate(), hmLeaveDatesType, false, hmMonthlyLeaves);
				
				
				Map hmLeaves = (Map)hmLeaveDays.get(getStrEmpId());
				if(hmLeaves==null)hmLeaves = new HashMap();
				
				Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
				Map hmWeekendMap = CF.getWeekEndDateList(con, getFrmDate(), getToDate(), CF, uF,null,null);
				String strWLocationId = hmEmpWLocation.get(getStrEmpId()) ;
				
				Map hmHolidays = new HashMap();
				Map hmHolidayDates = new HashMap();
				CF.getHolidayList(con,request, getFrmDate(), getToDate(), CF, hmHolidayDates, hmHolidays, true);


				
				request.setAttribute("hmLeaves", hmLeaves);
				request.setAttribute("hmWeekendMap", hmWeekendMap);
				request.setAttribute("strWLocationId", strWLocationId);
				request.setAttribute("hmHolidayDates", hmHolidayDates);
				request.setAttribute("hmLeavesColour", hmLeavesColour);
				
				
				request.setAttribute("timesheet_title", "Timesheet details from "+strPayCycleDates[0]+" to "+strPayCycleDates[1]);
				request.setAttribute("alDates", alDates);
				request.setAttribute("hmProjects", hmProjects);
				request.setAttribute("hmProjectTasks", hmProjectTasks);
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
			
			
		}
		
		public void getData(){
			Connection con = null;
			PreparedStatement pst = null;
			ResultSet rs =null;
			Database db = new Database();
			db.setRequest(request);
			UtilityFunctions uF = new UtilityFunctions();
			try{
				/*
				con = db.makeConnection(con);
				
				
				
				pst = con.prepareStatement("select * from activity_info ai, projectmntnc pmc where pmc.pro_id = ai.pro_id and ai.start_date<=? and ai.deadline>=? order by ai.start_date");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				ResultSet rs = pst.executeQuery();
				*/
				
				
				

				String[] strPayCycleDates = null;
				if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("NULL")) {
					strPayCycleDates = getStrPaycycle().split("-");
				} else {
					strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
					setStrPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
				}
				
				
				
				con = db.makeConnection(con);
				
				Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
				boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
				
				
				/*
				pst = con.prepareStatement("select * from activity_info ai, projectmntnc pmc where pmc.pro_id = ai.pro_id and ai.start_date<=? and ai.deadline>=? order by ai.start_date");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				ResultSet rs = pst.executeQuery();
				
				if(rs.next()){
					setStrClient(rs.getString("client_id"));
					setStrProject(rs.getString("pro_id"));
				}*/

				
				
				pst = con.prepareStatement("select * from project_timesheet pt left join employee_personal_details epd on epd.emp_per_id = pt.approved_by where emp_id = ? and timesheet_paycycle=?");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
				rs = pst.executeQuery();
				int nApproved = 0;
				int timesheetId = 0;
				while(rs.next()){
					nApproved = uF.parseToInt(rs.getString("is_approved"));
					timesheetId = uF.parseToInt(rs.getString("timesheet_id"));
					
					request.setAttribute("submitted_on", uF.getDateFormat(rs.getString("timesheet_generated_date"), DBDATE, CF.getStrReportDateFormat()));
					if(rs.getString("emp_fname")!=null){
						
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						
						request.setAttribute("approved_by", rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
					}
				}
				rs.close();
				pst.close();
				request.setAttribute("nApproved", nApproved+"");
				request.setAttribute("timesheetId", timesheetId+"");
				
				
				request.setAttribute("datefrom", strPayCycleDates[0]);
				request.setAttribute("dateto", strPayCycleDates[1]);
				request.setAttribute("empid", getStrEmpId());
				
				
				
				
			}catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
			
		}


		public String getFrmDate() {
			return frmDate;
		}


		public void setFrmDate(String frmDate) {
			this.frmDate = frmDate;
		}


		public String getToDate() {
			return toDate;
		}


		public void setToDate(String toDate) {
			this.toDate = toDate;
		}


		public String[] getStrClient() {
			return strClient;
		}


		public void setStrClient(String[] strClient) {
			this.strClient = strClient;
		}


		public String[] getStrProject() {
			return strProject;
		}


		public void setStrProject(String[] strProject) {
			this.strProject = strProject;
		}


		
		HttpServletRequest request;
		@Override
		public void setServletRequest(HttpServletRequest request) {
			this.request = request;
			
		}


		public List<FillProjectList> getProjectdetailslist() {
			return projectdetailslist;
		}


		public void setProjectdetailslist(List<FillProjectList> projectdetailslist) {
			this.projectdetailslist = projectdetailslist;
		}


		public List<FillClients> getClientlist() {
			return clientlist;
		}


		public void setClientlist(List<FillClients> clientlist) {
			this.clientlist = clientlist;
		}


		public List<FillTask> getTasklist() {
			return tasklist;
		}


		public void setTasklist(List<FillTask> tasklist) {
			this.tasklist = tasklist;
		}

		public List<FillPayCycles> getPaycycleList() {
			return paycycleList;
		}

		public void setPaycycleList(List<FillPayCycles> paycycleList) {
			this.paycycleList = paycycleList;
		}

		public String getStrPaycycle() {
			return strPaycycle;
		}

		public void setStrPaycycle(String strPaycycle) {
			this.strPaycycle = strPaycycle;
		}

		public String getSave() {
			return save;
		}

		public void setSave(String save) {
			this.save = save;
		}


		public String[] getStrDate() {
			return strDate;
		}


		public void setStrDate(String[] strDate) {
			this.strDate = strDate;
		}


		public String[] getStrTask() {
			return strTask;
		}


		public void setStrTask(String[] strTask) {
			this.strTask = strTask;
		}


		public String[] getStrTime() {
			return strTime;
		}


		public void setStrTime(String[] strTime) {
			this.strTime = strTime;
		}


		public String[] getTaskId() {
			return taskId;
		}


		public void setTaskId(String[] taskId) {
			this.taskId = taskId;
		}


		public String getStrActivity() {
			return strActivity;
		}


		public void setStrActivity(String strActivity) {
			this.strActivity = strActivity;
		}


		public String getType() {
			return type;
		}


		public void setType(String type) {
			this.type = type;
		}


		public String getTimesheetId() {
			return timesheetId;
		}


		public void setTimesheetId(String timesheetId) {
			this.timesheetId = timesheetId;
		}


		public String getStrEmpId() {
			return strEmpId;
		}


		public void setStrEmpId(String strEmpId) {
			this.strEmpId = strEmpId;
		}


		public String getStrProject1() {
			return strProject1;
		}


		public void setStrProject1(String strProject1) {
			this.strProject1 = strProject1;
		}


	
	}
