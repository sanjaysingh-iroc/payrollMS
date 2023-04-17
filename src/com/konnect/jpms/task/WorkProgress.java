package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class WorkProgress extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session; 
	String strSessionEmpId;
	String strUserType =  null;
	CommonFunctions CF; 
	
	String strProId;
	String strMonth;
	String strYear;
	
	List<FillProjectList> projectdetailslist;
	List<FillMonth> monthList; 
	
	String proPage;
	String minLimit;
	String proType;
	
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_service;
	
	List<FillOrganisation> organisationList; 
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	
	String calendarYear;
	List<FillCalendarYears> calendarYearList;
	
	String strProType;
	boolean poFlag;

	String btnSubmit;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}

		strSessionEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		request.setAttribute(PAGE, "/jsp/task/WorkProgress.jsp");
		request.setAttribute(TITLE, "Work Progress");
		
		request.setAttribute("arrEnabledModules", CF.getArrEnabledModules());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		request.setAttribute("IS_DEVICE_INTEGRATION", CF.getIsDeviceIntegration());
		
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		checkProjectOwner(uF);
		
		if(uF.parseToInt(getProPage()) == 0) {
			setProPage("1");
		}
		if(getProType() == null || getProType().trim().equals("") || getProType().trim().equalsIgnoreCase("null")){
			setProType("P");
		}
		if(getProType()!=null && getProType().trim().equalsIgnoreCase("P")){
			getProjectWorkProgressDetails(uF);
		} else if(getProType()!=null && getProType().trim().equalsIgnoreCase("R")){
			getResourceWorkProgressDetails(uF);
		}
		return loadWorkProgress(uF);

	}
	
	private void checkProjectOwner(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean poFlag = false;
		try{
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 13-10-2022===	
//			sbQuery.append("select * from projectmntnc pmc where project_owner=?");
			sbQuery.append("select * from projectmntnc pmc where project_owners like '%,"+strSessionEmpId+",%'");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
		//===end parvez date: 13-10-2022===	
			rs = pst.executeQuery();
			if(rs.next()) {
				poFlag = true;
			}
			rs.close();
			pst.close();
			
			setPoFlag(poFlag);
//			System.out.println("isPoFlag() ===>> "+ isPoFlag() + " == poFlag ===>> " + poFlag);
			if(poFlag && uF.parseToInt(getStrProType()) == 0) {
				setStrProType("2");
			}
		} catch (Exception e) {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getResourceWorkProgressDetails(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		try { 
			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;

			if (getCalendarYear() != null) {
				strCalendarYearDates = getCalendarYear().split("-");
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			} else {
				strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
				setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			}
			
			if(getStrMonth() == null){
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			}
			setStrYear(uF.parseToInt(uF.getDateFormat(strCalendarYearEnd+"", DATE_FORMAT, "yyyy"))+"");
			
			List<List<String>> weekdates = new ArrayList<List<String>>();
			uF.getMonthWeeksDate(weekdates,getStrMonth(),getStrYear(),DATE_FORMAT);
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as cnt from employee_personal_details epd,employee_official_details eod " +
					"where epd.emp_per_id=eod.emp_id and eod.emp_id in(select distinct(emp_id) from project_emp_details ");
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
			//===start parvez date: 13-10-2022===	
//				sbQuery.append(" where pro_id in (select pro_id from projectmntnc where pro_id > 0 and project_owner="+uF.parseToInt(strSessionEmpId)+")");
				sbQuery.append(" where pro_id in (select pro_id from projectmntnc where pro_id > 0 and project_owners like '%,"+strSessionEmpId+",%')");
			//===start parvez date: 13-10-2022===	
			}
			sbQuery.append(")");
			if(uF.parseToInt(getF_org())>0){
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	        if(getF_department()!=null && getF_department().length>0){
	            sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
	        if(getF_service()!=null && getF_service().length>0){
	            sbQuery.append(" and (");
	            for(int i=0; i<getF_service().length; i++){
	            	sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
	                
	                if(i<getF_service().length-1){
	                    sbQuery.append(" OR "); 
	                }
	            }
	            sbQuery.append(" ) ");
	        } 
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			int proCount = 0;
			int proCnt = 0;
			while(rs.next()) {
				proCnt = rs.getInt("cnt");
				proCount = rs.getInt("cnt")/20;
				if(rs.getInt("cnt")%20 != 0) {
					proCount++;
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("proCount", proCount+"");
			request.setAttribute("proCnt", proCnt+"");
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select eod.emp_id,epd.emp_fname,epd.emp_mname,epd.emp_lname,epd.emp_image from employee_personal_details epd,employee_official_details eod " +
					"where epd.emp_per_id=eod.emp_id and eod.emp_id in(select distinct(emp_id) from project_emp_details ");
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
			//===start parvez date: 13-10-2022===	
//				sbQuery.append(" where pro_id in (select pro_id from projectmntnc where pro_id > 0 and project_owner="+uF.parseToInt(strSessionEmpId)+")");
				sbQuery.append(" where pro_id in (select pro_id from projectmntnc where pro_id > 0 and project_owners like '%,"+strSessionEmpId+",%')");
			//===end parvez date: 13-10-2022===	
			}
			sbQuery.append(")");
			if(uF.parseToInt(getF_org())>0){
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	        if(getF_department()!=null && getF_department().length>0){
	            sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
	        if(getF_service()!=null && getF_service().length>0){
	            sbQuery.append(" and (");
	            for(int i=0; i<getF_service().length; i++){
	            	sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
	                
	                if(i<getF_service().length-1){
	                    sbQuery.append(" OR "); 
	                }
	            }
	            sbQuery.append(" ) ");
	        } 
			sbQuery.append(" order by epd.emp_fname,epd.emp_lname");
			int intOffset = uF.parseToInt(getMinLimit());
			sbQuery.append(" limit 20 offset "+intOffset+"");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alPeople = new ArrayList<Map<String,String>>();
			while(rs.next()) {
				Map<String, String> hmPeople = new HashMap<String, String>();
				hmPeople.put("EMP_ID", rs.getString("emp_id"));	
				
				/*String strMiddleName = "";
				if(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("") && !rs.getString("emp_mname").trim().equalsIgnoreCase("NULL")){
					strMiddleName = rs.getString("emp_mname")+" ";
				}*/
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmPeople.put("EMP_NAME", uF.showData(rs.getString("emp_fname"), "")+strEmpMName+" "+uF.showData(rs.getString("emp_lname"), ""));
				hmPeople.put("EMP_IMAGE", uF.showData(rs.getString("emp_image"), ""));
				
				alPeople.add(hmPeople); 
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmCompleteTask = new HashMap<String, String>();
            Map<String, String> hmActiveTask = new HashMap<String, String>();
            Map<String, String> hmOverdueTask = new HashMap<String, String>();
            
            Map<String, String> hmCompleteTotalCnt = new HashMap<String, String>();
	        Map<String, String> hmActiveTotalCnt = new HashMap<String, String>();
	        Map<String, String> hmOverdueTotalCnt = new HashMap<String, String>();
            
			if(alPeople != null && alPeople.size() > 0){
				for(int j =0; j<alPeople.size(); j++){
					Map<String, String> hmPeople = alPeople.get(j);
					String strEmpId = hmPeople.get("EMP_ID");
					StringBuilder sbCompleteCount = null;
					StringBuilder sbActiveCount = null;
					StringBuilder sbOverDueCount = null;
					int nCompleteCnt=0;
					int nActiveCnt=0;
					int nOverdueCnt=0;
					int x = 0;
					
					for(int i = 0; weekdates!=null && i < weekdates.size();i++){
						List<String> week = weekdates.get(i);
						x++;
						/**Complete Task Count
						 * */
						pst = con.prepareStatement("select count(*) as cnt from activity_info where resource_ids like '%,"+strEmpId+",%' " +
								"and approve_status = 'approved' and end_date between ? and ?");
						pst.setDate(1, uF.getDateFormat(week.get(0), DATE_FORMAT));
						pst.setDate(2, uF.getDateFormat(week.get(1), DATE_FORMAT));
//						System.out.println("pst======>"+pst);
						rs = pst.executeQuery();
						int nComplete = 0;
						while(rs.next()){
							nComplete = rs.getInt("cnt");
						}
						rs.close();
						pst.close();
						
						if(sbCompleteCount == null){
							sbCompleteCount = new StringBuilder();
							sbCompleteCount.append(""+nComplete);
						} else {
							sbCompleteCount.append(","+nComplete);
						}
						nCompleteCnt +=nComplete;
						
						int nCompleteTotalCnt = uF.parseToInt(hmCompleteTotalCnt.get(x+"week"));
						nCompleteTotalCnt +=nComplete;
						hmCompleteTotalCnt.put(x+"week", ""+nCompleteTotalCnt);
						/**Complete Task Count end
						 * */
						
						/**Active Task Count
						 * */
						pst = con.prepareStatement("select count(*) as cnt from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
								"and resource_ids like '%,"+strEmpId+",%' and ai.approve_status='n' and ai.deadline >= ? and ai.start_date < ?");
						pst.setDate(1, uF.getDateFormat(week.get(1), DATE_FORMAT));
						pst.setDate(2, uF.getDateFormat(week.get(1), DATE_FORMAT));
						rs = pst.executeQuery();
						int nActive = 0;
						while(rs.next()){
							nActive = rs.getInt("cnt");
						}
						rs.close();
						pst.close();
						
						if(sbActiveCount == null){
							sbActiveCount = new StringBuilder();
							sbActiveCount.append(""+nActive);
						} else {
							sbActiveCount.append(","+nActive);
						}
						nActiveCnt +=nActive;
						
						int nActiveTotalCnt = uF.parseToInt(hmActiveTotalCnt.get(x+"week"));
						nActiveTotalCnt +=nActive;
						hmActiveTotalCnt.put(x+"week", ""+nActiveTotalCnt);
						
						/**Active Task Count
						 * */
						/**Overdue Task Count
						 * */
						pst = con.prepareStatement("select count(*) as cnt from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
								"and resource_ids like '%,"+strEmpId+",%' and ai.approve_status='n' and ai.deadline < ?");
						pst.setDate(1, uF.getDateFormat(week.get(1), DATE_FORMAT));
						rs = pst.executeQuery();
						int nOverdue = 0;
						while(rs.next()){
							nOverdue = rs.getInt("cnt");
						}
						rs.close();
						pst.close();
						
						if(sbOverDueCount == null){
							sbOverDueCount = new StringBuilder();
							sbOverDueCount.append(""+nOverdue);
						} else {
							sbOverDueCount.append(","+nOverdue);
						}
						nOverdueCnt +=nOverdue;
						
						int nOverdueTotalCnt = uF.parseToInt(hmOverdueTotalCnt.get(x+"week"));
						nOverdueTotalCnt +=nOverdue;
						hmOverdueTotalCnt.put(x+"week", ""+nOverdueTotalCnt);
						
						/**Overdue Task Count
						 * */
						
					}
					
					hmCompleteTask.put(strEmpId+"_COMPLETE", sbCompleteCount.toString());
					hmCompleteTask.put(strEmpId+"_COMPLETE_COUNT", ""+nCompleteCnt);
					
					hmActiveTask.put(strEmpId+"_ACTIVE", sbActiveCount.toString());
					hmActiveTask.put(strEmpId+"_ACTIVE_COUNT", ""+nActiveCnt);
					
					hmOverdueTask.put(strEmpId+"_OVERDUE", sbOverDueCount.toString());
					hmOverdueTask.put(strEmpId+"_OVERDUE_COUNT", ""+nOverdueCnt);
				}
			}
			
			StringBuilder sbWork 	= new StringBuilder();
			int x = 0;
			for(int i = 0; weekdates!=null && i < weekdates.size();i++){
				x++;
				sbWork.append("{'week':'"+x+"wk', " +
						"'completed': "+uF.parseToInt(hmCompleteTotalCnt.get(x+"week"))+"," +
						"'active': "+uF.parseToInt(hmActiveTotalCnt.get(x+"week"))+"," +
						"'overdue': "+uF.parseToInt(hmOverdueTotalCnt.get(x+"week"))+"},");
				
            }
            if(sbWork.length()>1) {
				sbWork.replace(0, sbWork.length(), sbWork.substring(0, sbWork.length()-1));
            }
			
			request.setAttribute("alPeople", alPeople);
			request.setAttribute("hmCompleteTask", hmCompleteTask);
			request.setAttribute("hmActiveTask", hmActiveTask);
			request.setAttribute("hmOverdueTask", hmOverdueTask);
			
			request.setAttribute("sbWork", sbWork.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getProjectWorkProgressDetails(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		try {
			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;

			if (getCalendarYear() != null) {
				strCalendarYearDates = getCalendarYear().split("-");
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			} else {
				strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
				setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			}
			
			if(getStrMonth() == null){
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			}
			setStrYear(uF.parseToInt(uF.getDateFormat(strCalendarYearEnd+"", DATE_FORMAT, "yyyy"))+"");
			
			List<List<String>> weekdates = new ArrayList<List<String>>();
			uF.getMonthWeeksDate(weekdates,getStrMonth(),getStrYear(),DATE_FORMAT);
			
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as proCount from projectmntnc where pro_id > 0 ");
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
			//===start parvez date: 13-10-2022===	
//				sbQuery.append(" and project_owner="+uF.parseToInt(strSessionEmpId));
				sbQuery.append(" and project_owners like '%,"+strSessionEmpId+",%'");
			//===end parvez date: 13-10-2022===	
			}
			if(uF.parseToInt(getStrProId()) > 0){
				sbQuery.append(" and pro_id ="+uF.parseToInt(getStrProId()));
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
			//===start parvez date: 13-10-2022===	
				/*sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");*/
				sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
						+" or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%' ) ");
			//===end parvez date: 13-10-2022===	
			}
			if(strUserType!=null && strUserType.equalsIgnoreCase(CUSTOMER)) {
				sbQuery.append(" and poc="+uF.parseToInt((String)session.getAttribute(EMPID))+" ");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			int proCount = 0;
			int proCnt = 0;
			while(rs.next()) {
				proCnt = rs.getInt("proCount");
				proCount = rs.getInt("proCount")/20;
				if(rs.getInt("proCount")%20 != 0) {
					proCount++;
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("proCount", proCount+"");
			request.setAttribute("proCnt", proCnt+"");
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from projectmntnc where pro_id > 0 ");
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
			//===start parvez date: 13-10-2022===	
//				sbQuery.append(" and project_owner="+uF.parseToInt(strSessionEmpId));
				sbQuery.append(" and project_owners like '%,"+strSessionEmpId+",%'");
			//===end parvez date: 13-10-2022===	
			}
			if(uF.parseToInt(getStrProId()) > 0){
				sbQuery.append(" and pro_id ="+uF.parseToInt(getStrProId()));
			}
			if(uF.parseToInt(getStrProId()) > 0){
				sbQuery.append(" and pro_id ="+uF.parseToInt(getStrProId()));
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
			//===start parvez date: 13-10-2022===	
				/*sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");*/
				sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
						+" or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%' ) ");
			//===start parvez date: 13-10-2022===	
			}
			if(strUserType!=null && strUserType.equalsIgnoreCase(CUSTOMER)) {
				sbQuery.append(" and poc="+uF.parseToInt((String)session.getAttribute(EMPID))+" ");
			}
			sbQuery.append(" order by pro_name");
			int intOffset = uF.parseToInt(getMinLimit());
			sbQuery.append(" limit 20 offset "+intOffset+"");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmProject = new LinkedHashMap<String, String>();
			while(rs.next()){
				hmProject.put(rs.getString("pro_id"), uF.showData(rs.getString("pro_name"), ""));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmCompleteTask = new HashMap<String, String>();
            Map<String, String> hmActiveTask = new HashMap<String, String>();
            Map<String, String> hmOverdueTask = new HashMap<String, String>();
            
            Map<String, String> hmCompleteTotalCnt = new HashMap<String, String>();
	        Map<String, String> hmActiveTotalCnt = new HashMap<String, String>();
	        Map<String, String> hmOverdueTotalCnt = new HashMap<String, String>();
            
			if(hmProject != null && hmProject.size() > 0) {
                Iterator<String> it = hmProject.keySet().iterator();
				while(it.hasNext()) {
					String strProId = it.next();
					StringBuilder sbCompleteCount = null;
					StringBuilder sbActiveCount = null;
					StringBuilder sbOverDueCount = null;  
					int nCompleteCnt=0;
					int nActiveCnt=0;
					int nOverdueCnt=0;
					int x = 0;
					for(int i = 0; weekdates!=null && i < weekdates.size();i++) {
						List<String> week = weekdates.get(i);
						x++;
						/**Complete Task Count
						 * */
						pst = con.prepareStatement("select count(*) as cnt,ai.pro_id from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
								"and ai.pro_id=? and end_date between ? and ? and ai.approve_status='approved' " +
								"group by ai.pro_id,ai.end_date order by ai.pro_id,ai.end_date");
						pst.setInt(1, uF.parseToInt(strProId));
						pst.setDate(2, uF.getDateFormat(week.get(0), DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(week.get(1), DATE_FORMAT));
//						System.out.println("pst nComplete ===>> " + pst);
						rs = pst.executeQuery();
						int nComplete = 0;
						while(rs.next()) {
							nComplete = rs.getInt("cnt");
						}
						rs.close();
						pst.close();
						
						if(sbCompleteCount == null){
							sbCompleteCount = new StringBuilder();
							sbCompleteCount.append(""+nComplete);
						} else {
							sbCompleteCount.append(","+nComplete);
						}
						nCompleteCnt +=nComplete;
						
						int nCompleteTotalCnt = uF.parseToInt(hmCompleteTotalCnt.get(x+"week"));
						nCompleteTotalCnt +=nComplete;
						hmCompleteTotalCnt.put(x+"week", ""+nCompleteTotalCnt);
						/**Complete Task Count end
						 * */
						
						/**Active Task Count
						 * */
						pst = con.prepareStatement("select count(*) as cnt from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
								"and ai.approve_status='n' and ai.pro_id=? and ai.deadline >= ? and ai.start_date <= ?");
						pst.setInt(1, uF.parseToInt(strProId));
						pst.setDate(2, uF.getDateFormat(week.get(1), DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(week.get(1), DATE_FORMAT));
//						System.out.println("pst nActive ===>> " + pst);
						rs = pst.executeQuery();
						int nActive = 0;
						while(rs.next()) {
							nActive = rs.getInt("cnt");
						}
						rs.close();
						pst.close();
						
						if(sbActiveCount == null) {
							sbActiveCount = new StringBuilder();
							sbActiveCount.append(""+nActive);
						} else {
							sbActiveCount.append(","+nActive);
						}
						nActiveCnt +=nActive;
						
						int nActiveTotalCnt = uF.parseToInt(hmActiveTotalCnt.get(x+"week"));
						nActiveTotalCnt +=nActive;
						hmActiveTotalCnt.put(x+"week", ""+nActiveTotalCnt);
						
						/**Active Task Count
						 * */
						
						/**Overdue Task Count
						 * */
						pst = con.prepareStatement("select count(*) as cnt from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
								"and ai.approve_status='n' and ai.pro_id=? and ai.deadline < ?");
						pst.setInt(1, uF.parseToInt(strProId));
						pst.setDate(2, uF.getDateFormat(week.get(1), DATE_FORMAT));
//						System.out.println("pst nOverdue ===>> " + pst);
						rs = pst.executeQuery();
						int nOverdue = 0;
						while(rs.next()) {
							nOverdue = rs.getInt("cnt");
						}
						rs.close();
						pst.close();
						
						if(sbOverDueCount == null) {
							sbOverDueCount = new StringBuilder();
							sbOverDueCount.append(""+nOverdue);
						} else {
							sbOverDueCount.append(","+nOverdue);
						}
						nOverdueCnt +=nOverdue;
						
						int nOverdueTotalCnt = uF.parseToInt(hmOverdueTotalCnt.get(x+"week"));
						nOverdueTotalCnt +=nOverdue;
						hmOverdueTotalCnt.put(x+"week", ""+nOverdueTotalCnt);
						
						/**Overdue Task Count
						 * */
					}
					
					hmCompleteTask.put(strProId+"_COMPLETE", sbCompleteCount.toString());
					hmCompleteTask.put(strProId+"_COMPLETE_COUNT", ""+nCompleteCnt);
					
					hmActiveTask.put(strProId+"_ACTIVE", sbActiveCount.toString());
					hmActiveTask.put(strProId+"_ACTIVE_COUNT", ""+nActiveCnt);
					
					hmOverdueTask.put(strProId+"_OVERDUE", sbOverDueCount.toString());
					hmOverdueTask.put(strProId+"_OVERDUE_COUNT", ""+nOverdueCnt);
                }
                
			}
			
			StringBuilder sbWork 	= new StringBuilder();
			int x = 0;
			for(int i = 0; weekdates!=null && i < weekdates.size();i++){
				x++;
				sbWork.append("{'week':'"+x+"wk', " +
						"'completed': "+uF.parseToInt(hmCompleteTotalCnt.get(x+"week"))+"," +
						"'active': "+uF.parseToInt(hmActiveTotalCnt.get(x+"week"))+"," +
						"'overdue': "+uF.parseToInt(hmOverdueTotalCnt.get(x+"week"))+"},");
				
            }
            if(sbWork.length()>1) {
				sbWork.replace(0, sbWork.length(), sbWork.substring(0, sbWork.length()-1));
            }
            
			request.setAttribute("hmProject", hmProject);
			request.setAttribute("hmCompleteTask", hmCompleteTask);
			request.setAttribute("hmActiveTask", hmActiveTask);
			request.setAttribute("hmOverdueTask", hmOverdueTask);
			
			request.setAttribute("sbWork", sbWork.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	private String loadWorkProgress(UtilityFunctions uF) {
		monthList = new FillMonth().fillMonth();
		calendarYearList = new FillCalendarYears(request).fillCalendarYears(CF);
		
		if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(CEO))) {
			projectdetailslist = new FillProjectList(request).fillAllProjectDetails(false, false);
		} else if (strUserType != null && strUserType.equalsIgnoreCase(CUSTOMER)) {
			projectdetailslist = new FillProjectList(request).fillProjectDetailsByCustomer(uF.parseToInt(strSessionEmpId), true, false, false);
		} else {
			projectdetailslist = new FillProjectList(request).fillProjectDetailsByManager(uF.parseToInt(strSessionEmpId), false, false);
		}
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		
		getSelectedFilter(uF);
		
		if(getBtnSubmit() != null) {
			return SUCCESS;
		} else {
			return LOAD;
		}
	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String, String> hmFilter = new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
//		System.out.println("isPoFlag =====>> " + isPoFlag()+" -- strUserType =====>> " + strUserType);
		if(isPoFlag() && strUserType != null && (strUserType.equals(ADMIN) || strUserType.equals(HRMANAGER)) ) {
			alFilter.add("PROJECT_TYPE");
			if(getStrProType()!=null) {
				String strProType="";
				if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
					strProType = "My Projects";
				}
				if(strProType!=null && !strProType.equals("")) {
					hmFilter.put("PROJECT_TYPE", strProType);
				} else {
					hmFilter.put("PROJECT_TYPE", "All Projects");
				}
			} else {
				hmFilter.put("PROJECT_TYPE", "All Projects");
			}
		}
		
		if(getProType()!=null && getProType().trim().equalsIgnoreCase("P")){
			alFilter.add("PROJECT");
			if (getStrProId() != null) {
				String strProjects = "";
				int k = 0;
				for (int i = 0; projectdetailslist != null && i < projectdetailslist.size(); i++) {
					if (getStrProId().equals(projectdetailslist.get(i).getProjectID())) {
						if (k == 0) {
							strProjects = projectdetailslist.get(i).getProjectName();
						} else {
							strProjects += ", " + projectdetailslist.get(i).getProjectName();
						}
						k++;
					}
				}
				if (strProjects != null && !strProjects.equals("")) {
					hmFilter.put("PROJECT", strProjects);
				} else {
					hmFilter.put("PROJECT", "All Projects");
				}
			} else {
				hmFilter.put("PROJECT", "All Projects");
			}
		}else if(getProType()!=null && getProType().trim().equalsIgnoreCase("R")){
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
		}
		
		alFilter.add("CALENDARYEAR");
		String[] strCalendarYearDates = null;
		if (getCalendarYear() != null) {
			strCalendarYearDates = getCalendarYear().split("-");
			setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
		} else {
			strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
			setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
		}
		hmFilter.put("CALENDARYEAR", uF.getDateFormat(strCalendarYearDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strCalendarYearDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		alFilter.add("MONTH");
		int nselectedMonth = uF.parseToInt(getStrMonth());
		String strMonth = uF.getMonth(nselectedMonth);
		hmFilter.put("MONTH", strMonth);

		String selectedFilter = CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrProId() {
		return strProId;
	}

	public void setStrProId(String strProId) {
		this.strProId = strProId;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrYear() {
		return strYear;
	}

	public void setStrYear(String strYear) {
		this.strYear = strYear;
	}

	public List<FillProjectList> getProjectdetailslist() {
		return projectdetailslist;
	}

	public void setProjectdetailslist(List<FillProjectList> projectdetailslist) {
		this.projectdetailslist = projectdetailslist;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
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

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
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

	public String getCalendarYear() {
		return calendarYear;
	}

	public void setCalendarYear(String calendarYear) {
		this.calendarYear = calendarYear;
	}

	public List<FillCalendarYears> getCalendarYearList() {
		return calendarYearList;
	}

	public void setCalendarYearList(List<FillCalendarYears> calendarYearList) {
		this.calendarYearList = calendarYearList;
	}

	public String getStrProType() {
		return strProType;
	}

	public void setStrProType(String strProType) {
		this.strProType = strProType;
	}

	public boolean isPoFlag() {
		return poFlag;
	}

	public void setPoFlag(boolean poFlag) {
		this.poFlag = poFlag;
	}

	public String getBtnSubmit() {
		return btnSubmit;
	}

	public void setBtnSubmit(String btnSubmit) {
		this.btnSubmit = btnSubmit;
	}
	
}
