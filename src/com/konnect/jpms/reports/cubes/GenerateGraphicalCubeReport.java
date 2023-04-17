package com.konnect.jpms.reports.cubes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
 
public class GenerateGraphicalCubeReport implements ServletRequestAware, IStatements, ICubeReports {

	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	Map<String, String> hmEmpWlocationMap;
	Map<String, String> hmEmpLevelMap;
	String strUserType = null;
	String strSessionEmpId = null;
	private static Logger log = Logger.getLogger(GenerateGraphicalCubeReport.class);
	
	public GenerateGraphicalCubeReport(	CommonFunctions CF, HttpServletRequest request,
										String cubeReportMeasure,
										String reportType,
										String []cubeReportSubMeasure,
										String []cubeReportParaC,
										String []cubeReportParaE) {

				this.CF = CF;
				setCubeReportMeasure(cubeReportMeasure);
				setReportType(reportType);
				setCubeReportSubMeasure(cubeReportSubMeasure);
				setCubeReportParaC(cubeReportParaC);
				setCubeReportParaE(cubeReportParaE);
				setServletRequest(request);
				session = request.getSession();
	}
	
	String cubeReportMeasure;
	String reportType;
	String []cubeReportSubMeasure;
	String []cubeReportParaC;
	String []cubeReportParaE;
	
	

	
	final static public String selectCRRosterHoursEmp = "select sum(actual_hours) as roster_hours, emp_id from roster_details where _date between ? and ? group by emp_id";
	final static public String selectCRActualHoursEmp = "select sum(hours_worked) as hours_worked, emp_id from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? group by emp_id";
	final static public String selectCREmployeeLeaveEntryR   = "select * from (SELECT * FROM emp_leave_entry ee, leave_type lt where lt.leave_type_id = ee.leave_type_id order by emp_id) a, employee_personal_details epd where epd.emp_per_id=a.emp_id and (approval_from, approval_to_date) overlaps (to_date(?::text, 'YYYY-MM-DD')-1,to_date(?::text, 'YYYY-MM-DD') +1)order by emp_fname,emp_lname, emp_id";
	
	public String generateReport(){
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			con = db.makeConnection(con);
			
			hmEmpWlocationMap = CF.getEmpWlocationMap(con);
			hmEmpLevelMap = CF.getEmpLevelMap(con);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		if(getCubeReportMeasure()!=null && getCubeReportMeasure().equalsIgnoreCase("Time")){
			viewCubeTimeReport();
			return "chart_time";
		}else if(getCubeReportMeasure()!=null && getCubeReportMeasure().equalsIgnoreCase("Leaves")){
			viewCubeLeaveReport();
			return "frame";
		}else if(getCubeReportMeasure()!=null && getCubeReportMeasure().equalsIgnoreCase("Salary")){
			viewSalaryReport();
			return "chart_salary";
		}else{
			return "noreports";
		}
		
	}


	public void viewCubeLeaveReport() {
		

		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();
		con = db.makeConnection(con);
		Map hmEmployeeNameMap = CF.getEmpNameMap(con,strUserType, strSessionEmpId);
		
		try {
			
			
			String []currentPayCycle = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
			String []prev1PayCycle = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
			String []prev2PayCycle = CF.getPrevPayCycle(con,prev1PayCycle[0], CF.getStrTimeZone(), CF,request);
			
			
			
			
			
				
				pst = con.prepareStatement(selectCREmployeeLeaveEntryR);
				pst.setDate(1, uF.getDateFormat(currentPayCycle[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(currentPayCycle[1], DATE_FORMAT));
				
				rs = pst.executeQuery();
				Map<String, Map<String, String>> hmOuter = new LinkedHashMap<String, Map<String, String>>();
				Map<String, String> hmInner = new HashMap<String, String>();
				
				List<String> alLeaveType = new ArrayList<String>();
				
				String strEmpIdNew = null;
				
				
				log.debug("pst===>"+pst);
				String arrLeaveType[] = getCubeReportSubMeasure(); 
				String []arrWLocation = getCubeReportParaC();
				String []arrLevel = getCubeReportParaE();
				
				while(rs.next()){
					strEmpIdNew = rs.getString("emp_id");
					
					String strWLocationId = hmEmpWlocationMap.get(strEmpIdNew);
					String strLevelId = hmEmpLevelMap.get(strEmpIdNew);
					
					
					
					if(arrWLocation!=null && arrWLocation.length>0 && strWLocationId!=null && ArrayUtils.contains(arrWLocation, strWLocationId) < 0 )continue;
					if(arrLevel!=null && arrLevel.length>0 && strLevelId!=null && ArrayUtils.contains(arrLevel, strLevelId) < 0 )continue;

					if(!alLeaveType.contains(rs.getString("leave_type_name")) &&  ArrayUtils.contains(arrLeaveType, rs.getString("leave_type_id"))>=0 ){
						alLeaveType.add(rs.getString("leave_type_name"));
					}
					
					
					hmInner =  hmOuter.get(strEmpIdNew);
					if(hmInner==null){
						hmInner = new HashMap<String, String>();
					}
					
					if(rs.getInt("is_approved")==1){
						int noOfLeaves = uF.parseToInt((String)hmInner.get(rs.getString("leave_type_name")+"_APPROVED"));
						noOfLeaves +=  rs.getInt("emp_no_of_leave");
						hmInner.put(rs.getString("leave_type_name")+"_APPROVED", noOfLeaves+"");
					}else if(rs.getInt("is_approved")==-1){
						int noOfLeaves = uF.parseToInt((String)hmInner.get(rs.getString("leave_type_name")+"_DENIED"));
						noOfLeaves +=  rs.getInt("emp_no_of_leave");
						hmInner.put(rs.getString("leave_type_name")+"_DENIED", noOfLeaves+"");
					}else if(rs.getInt("is_approved")==0){
						int noOfLeaves = uF.parseToInt((String)hmInner.get(rs.getString("leave_type_name")+"_WAITING"));
						noOfLeaves +=  rs.getInt("emp_no_of_leave");
						hmInner.put(rs.getString("leave_type_name")+"_WAITING", noOfLeaves+"");
					}
					hmOuter.put(strEmpIdNew, hmInner);
				}
				rs.close();
				pst.close();
				
				log.debug("hmOuter===>>"+hmOuter);
				
				Set set = hmOuter.keySet();
			    Iterator it = set.iterator();
			    int approvedCnt = 0;
			    int deniedCnt = 0;
			    int waitingCnt = 0;
			    while(it.hasNext()){
			    	
			    	String strEmpId = (String)it.next();
			    	Map hmTemp = (Map)hmOuter.get(strEmpId);
			    	
			    	for(int i=0; i<alLeaveType.size(); i++){
			    		approvedCnt += uF.parseToInt((String)hmTemp.get(alLeaveType.get(i)+"_APPROVED"));
			    		deniedCnt 	+= uF.parseToInt((String)hmTemp.get(alLeaveType.get(i)+"_DENIED"));
			    		waitingCnt	+= uF.parseToInt((String)hmTemp.get(alLeaveType.get(i)+"_WAITING"));
			    	}
			    	
			    }
				
			    session.setAttribute("approvedCnt", approvedCnt+"");
			    session.setAttribute("deniedCnt", deniedCnt+"");
			    session.setAttribute("waitingCnt", waitingCnt+"");
				
//			    session.setAttribute("hmOuter", hmOuter);
				session.setAttribute("alLeaveType", alLeaveType);
//				session.setAttribute("hmEmployeeNameMap", hmEmployeeNameMap);
				session.setAttribute("currentPayCycle", currentPayCycle);
				session.setAttribute("prev1PayCycle", prev1PayCycle);
				session.setAttribute("prev2PayCycle", prev2PayCycle);
				
				request.setAttribute(PAGE, "jsp/reports/cubes/CubeReportLeaveChart.jsp");
				
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	final String SelectPayrollGeneration = "select * from payroll_generation where paycycle=? order by pay_date, emp_id";
	
	public void viewSalaryReport(){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();
		String strColspan = null;
		con = db.makeConnection(con);
		try {
			Map hmEmployeeNameMap = CF.getEmpNameMap(con,strUserType, strSessionEmpId);
			Map hmSalaryHeadsMap = CF.getSalaryHeadsMap(con);
			Map<String, String> hmSalaryHeadsSelectedMap = new LinkedHashMap<String, String>();
			String []currentPayCycle = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
					
			String []arrSalaryHeads = getCubeReportSubMeasure(); 
			String []arrWLocation = getCubeReportParaC();
			String []arrLevel = getCubeReportParaE();

			
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
				hmSalaryHeadsSelectedMap.put(arrSalaryHeads[i], (String)hmSalaryHeadsMap.get(arrSalaryHeads[i]));
			}
			
			
			
			
			
			
			pst = con.prepareStatement(SelectPayrollGeneration);
			pst.setInt(1, uF.parseToInt(currentPayCycle[2]));
			rs = pst.executeQuery();
			
			Map<String, Map<String, String>> hmSalary = new HashMap<String, Map<String, String>>();
			Map<String, String> hmInner = new HashMap<String, String>();
			
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			while(rs.next()){
				strEmpIdNew = rs.getString("emp_id");
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					hmInner = new HashMap<String, String>();
				}
				
				String strWLocationId = hmEmpWlocationMap.get(strEmpIdNew);
				String strLevelId = hmEmpLevelMap.get(strEmpIdNew);
				
				for(int i=0; i<arrLevel.length; i++){
					log.debug(strLevelId+" === arrLevel=="+arrLevel[i]);
				}
				
				
				if(arrWLocation!=null && arrWLocation.length>0 && strWLocationId!=null && ArrayUtils.contains(arrWLocation, strWLocationId) < 0 )continue;
				if(arrLevel!=null && arrLevel.length>0 && strLevelId!=null && ArrayUtils.contains(arrLevel, strLevelId) < 0 )continue;

				if(arrSalaryHeads!=null && arrSalaryHeads.length>0 && ArrayUtils.contains(arrSalaryHeads, rs.getString("salary_head_id"))>=0 ){
					hmInner.put(rs.getString("salary_head_id"), rs.getString("amount"));
					
					
				}
				
				hmSalary.put(strEmpIdNew, hmInner);
				
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmSalary", hmSalary);
			request.setAttribute("hmEmployeeNameMap", hmEmployeeNameMap);
			request.setAttribute("currentPayCycle", currentPayCycle);
			request.setAttribute("hmSalaryHeadsMap", hmSalaryHeadsSelectedMap);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void viewCubeTimeReport(){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();
		String strColspan = null;
		con = db.makeConnection(con);
		try {
			Map hmEmployeeName = CF.getEmpNameMap(con,strUserType, strSessionEmpId);
			
			String []currentPayCycle = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
			String []prevPayCycle = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
					

			Map hmRosterCurrent = new HashMap();
			Map hmRosterPrev = new HashMap();
			
		
			
			
			
			if(ArrayUtils.containsString(getCubeReportSubMeasure(), "R")>=0){
				
				pst = con.prepareStatement(selectCRRosterHoursEmp);
				pst.setDate(1, uF.getDateFormat(currentPayCycle[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(currentPayCycle[1], DATE_FORMAT));
				rs = pst.executeQuery();
				while(rs.next()){
					hmRosterCurrent.put(rs.getString("emp_id"), rs.getString("roster_hours"));
				}
				rs.close();
				pst.close();

				
				pst = con.prepareStatement(selectCRRosterHoursEmp);
				pst.setDate(1, uF.getDateFormat(prevPayCycle[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(prevPayCycle[1], DATE_FORMAT));
				rs = pst.executeQuery();
				while(rs.next()){
					hmRosterPrev.put(rs.getString("emp_id"), rs.getString("roster_hours"));
				}
				rs.close();
				pst.close();
				
			}
			
			Map hmActualCurrent = new HashMap();
			Map hmActualPrev = new HashMap();
			
			if(ArrayUtils.containsString(getCubeReportSubMeasure(), "A")>=0){
				
				pst = con.prepareStatement(selectCRActualHoursEmp);
				pst.setDate(1, uF.getDateFormat(currentPayCycle[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(currentPayCycle[1], DATE_FORMAT));
				rs = pst.executeQuery();
				while(rs.next()){
					hmActualCurrent.put(rs.getString("emp_id"), rs.getString("hours_worked"));
				}
				rs.close();
				pst.close();
				
				
				pst = con.prepareStatement(selectCRActualHoursEmp);
				pst.setDate(1, uF.getDateFormat(prevPayCycle[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(prevPayCycle[1], DATE_FORMAT));
				rs = pst.executeQuery();
				while(rs.next()){
					hmActualPrev.put(rs.getString("emp_id"), rs.getString("hours_worked"));
				}
				rs.close();
				pst.close();
			}
			
			
			List<String> alReportLabel = new ArrayList<String>();
			List<List<String>> alReport = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			
			String []arrWLocation = getCubeReportParaC();
			String []arrLevel = getCubeReportParaE();
			
			
			Set set = hmEmployeeName.keySet();
			Iterator it = set.iterator();
			
			while(it.hasNext()){
				String strEmpIdNew = (String)it.next();
				alInner = new ArrayList<String>();

				
				String strWLocationId = hmEmpWlocationMap.get(strEmpIdNew);
				String strLevelId = hmEmpLevelMap.get(strEmpIdNew);
				if(arrWLocation!=null && arrWLocation.length>0 && strWLocationId!=null && ArrayUtils.contains(arrWLocation, strWLocationId) < 0 )continue;
				if(arrLevel!=null && arrLevel.length>0 && strLevelId!=null &&ArrayUtils.contains(arrLevel, strLevelId) < 0 )continue;

				
				
				alInner.add((String)hmEmployeeName.get(strEmpIdNew));
				
				if(ArrayUtils.containsString(getCubeReportSubMeasure(), "A")>=0 && 
						ArrayUtils.containsString(getCubeReportSubMeasure(), "R")>=0){
					
					alInner.add(uF.showData((String)hmActualCurrent.get(strEmpIdNew), "0"));
					alInner.add(uF.showData((String)hmRosterCurrent.get(strEmpIdNew), "0"));
					alInner.add(uF.showData((String)hmActualPrev.get(strEmpIdNew), "0"));
					alInner.add(uF.showData((String)hmRosterPrev.get(strEmpIdNew), "0"));
					strColspan = "2";
				}else if(ArrayUtils.containsString(getCubeReportSubMeasure(), "A")>=0){
					alInner.add(uF.showData((String)hmActualCurrent.get(strEmpIdNew), "0"));
					alInner.add(uF.showData((String)hmActualPrev.get(strEmpIdNew), "0"));
				}else if(ArrayUtils.containsString(getCubeReportSubMeasure(), "R")>=0){
					alInner.add(uF.showData((String)hmRosterCurrent.get(strEmpIdNew), "0"));
					alInner.add(uF.showData((String)hmRosterPrev.get(strEmpIdNew), "0"));
				}
				
				
				alReport.add(alInner);
				
			}
			
			alReportLabel.add("Employee");
			if(ArrayUtils.containsString(getCubeReportSubMeasure(), "A")>=0 && 
					ArrayUtils.containsString(getCubeReportSubMeasure(), "R")>=0){
				alReportLabel.add("Actual");
				alReportLabel.add("Roster");
				alReportLabel.add("Actual");
				alReportLabel.add("Roster");
			}else if(ArrayUtils.containsString(getCubeReportSubMeasure(), "A")>=0){
				alReportLabel.add("Actual");
				alReportLabel.add("Actual");
			}else if(ArrayUtils.containsString(getCubeReportSubMeasure(), "R")>=0){
				alReportLabel.add("Roster");
				alReportLabel.add("Roster");
			}
			
			request.setAttribute("alReport", alReport);
			request.setAttribute("alReportLabel", alReportLabel);
			request.setAttribute("currentPayCycle", currentPayCycle);
			request.setAttribute("prevPayCycle", prevPayCycle);
			request.setAttribute("strColspan", strColspan);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
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

	public String getCubeReportMeasure() {
		return cubeReportMeasure;
	}

	public void setCubeReportMeasure(String cubeReportMeasure) {
		this.cubeReportMeasure = cubeReportMeasure;
	}

	public String[] getCubeReportParaC() {
		return cubeReportParaC;
	}

	public void setCubeReportParaC(String []cubeReportParaC) {
		this.cubeReportParaC = cubeReportParaC;
	}

	public String[] getCubeReportParaE() {
		return cubeReportParaE;
	}

	public void setCubeReportParaE(String []cubeReportParaE) {
		this.cubeReportParaE = cubeReportParaE;
	}

	public String[] getCubeReportSubMeasure() {
		return cubeReportSubMeasure;
	}

	public void setCubeReportSubMeasure(String []cubeReportSubMeasure) {
		this.cubeReportSubMeasure = cubeReportSubMeasure;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

}
