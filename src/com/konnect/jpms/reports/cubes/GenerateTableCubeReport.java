package com.konnect.jpms.reports.cubes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
 
public class GenerateTableCubeReport implements ServletRequestAware, IStatements, ICubeReports {
 
	
	HttpSession session; 
	CommonFunctions CF;
	Map<String, String> hmEmpWlocationMap;
	Map<String, String> hmEmpLevelMap;
	UtilityFunctions uF = new UtilityFunctions();
	
	String strUserType = null;
	String strSessionEmpId = null;
	private static Logger log = Logger.getLogger(GenerateTableCubeReport.class);
	
	public GenerateTableCubeReport(	CommonFunctions CF, HttpServletRequest request,
									String cubeReportMeasure,
									String cubeReportSubMeasureAll,
									String reportType,
									String []cubeReportSubMeasure,
									String []cubeReportParaC,
									String []cubeReportParaE) {
		
		this.CF = CF;
		session = request.getSession();
		setCubeReportMeasure(cubeReportMeasure);
		setReportType(reportType);
		setCubeReportSubMeasure(cubeReportSubMeasure);
		setCubeReportSubMeasureAll(uF.parseToBoolean(cubeReportSubMeasureAll));
		setCubeReportParaC(cubeReportParaC);
		setCubeReportParaE(cubeReportParaE);
		setServletRequest(request);

	}
	
	String cubeReportMeasure;
	String reportType;
	boolean cubeReportSubMeasureAll;
	String []cubeReportSubMeasure;
	String []cubeReportParaC;
	String []cubeReportParaE;
	
	
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
			return "frame";
		}else if(getCubeReportMeasure()!=null && getCubeReportMeasure().equalsIgnoreCase("Leaves")){
			viewCubeLeaveReport();
			return "frame";
		}else if(getCubeReportMeasure()!=null && getCubeReportMeasure().equalsIgnoreCase("Salary")){
			viewSalaryReport();
			return "frame";
		}else{
			return "noreports";
		}
		
	}

	
	final static public String selectCRRosterHoursEmp = "select sum(actual_hours) as roster_hours, emp_id from roster_details where _date between ? and ? group by emp_id";
	final static public String selectCRActualHoursEmp = "select sum(hours_worked) as hours_worked, emp_id from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? group by emp_id";
	final static public String selectCREmployeeLeaveEntryR   = "select * from (SELECT * FROM emp_leave_entry ee, leave_type lt where lt.leave_type_id = ee.leave_type_id order by emp_id) a, employee_personal_details epd where epd.emp_per_id=a.emp_id and (approval_from, approval_to_date) overlaps (to_date(?::text, 'YYYY-MM-DD')-1,to_date(?::text, 'YYYY-MM-DD') +1)order by emp_fname,emp_lname, emp_id";
	
	final static private String  CONSOLIDATED = "Consolidated";
	final static private String  EARNING = "(E)";
	final static private String  DEDUCTION = "(D)";
	
	public void viewCubeLeaveReport(){
		
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		con = db.makeConnection(con);
		Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con,strUserType, strSessionEmpId);
		
		try {
			
			
			String []currentPayCycle = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
			String []prev1PayCycle = CF.getPrevPayCycle(con,currentPayCycle[0], CF.getStrTimeZone(), CF,request);
			String []prev2PayCycle = CF.getPrevPayCycle(con,prev1PayCycle[0], CF.getStrTimeZone(), CF,request);
				
			List<String[]> alDates = new ArrayList<String[]>();
			alDates.add(currentPayCycle);
			alDates.add(prev1PayCycle);
			alDates.add(prev2PayCycle);
			
			
			
			Map<String, Map<String, String>> hmOuter = new LinkedHashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmpOuter = new LinkedHashMap<String, Map<String, String>>();
			Map<String, String> hmInner = new HashMap<String, String>();
			Map<String, String> hmInnerAll = new HashMap<String, String>();
			
			List<String> alLeaveType = new ArrayList<String>();
			List<String> alLeaveTypeAll = new ArrayList<String>();
			
			String arrLeaveType[] = getCubeReportSubMeasure(); 
			String []arrWLocation = getCubeReportParaC();
			String []arrLevel = getCubeReportParaE();
			
			
			for(int i=0; i<alDates.size(); i++){
				String []arrDates = alDates.get(i);
				pst = con.prepareStatement(selectCREmployeeLeaveEntryR);
				pst.setDate(1, uF.getDateFormat(arrDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(arrDates[1], DATE_FORMAT));
				rs = pst.executeQuery();
				String strEmpIdNew = null;
				while(rs.next()){
					strEmpIdNew = rs.getString("emp_id");
					
					String strWLocationId = hmEmpWlocationMap.get(strEmpIdNew);
					String strLevelId = hmEmpLevelMap.get(strEmpIdNew);
					
					
					
					if(arrWLocation!=null && arrWLocation.length>0 && strWLocationId!=null && ArrayUtils.contains(arrWLocation, strWLocationId) < 0 )continue;
					if(arrLevel!=null && arrLevel.length>0 && strLevelId!=null && ArrayUtils.contains(arrLevel, strLevelId) < 0 )continue;

					
					if(!alLeaveType.contains(rs.getString("leave_type_name")) &&  ArrayUtils.contains(arrLeaveType, rs.getString("leave_type_id"))>=0 ){
						alLeaveType.add(rs.getString("leave_type_name"));
						if(alLeaveTypeAll.size()==0){
							alLeaveTypeAll.add(CONSOLIDATED);
						}
						
					}
					
					
						
					
					
					
					hmInner =  hmOuter.get(strEmpIdNew+arrDates[2]);
					if(hmInner==null){
						hmInner = new HashMap<String, String>();
						hmInnerAll = new HashMap<String, String>();
					}
					
					if(rs.getInt("is_approved")==1){
						int noOfLeaves = uF.parseToInt((String)hmInner.get(rs.getString("leave_type_name")+"_APPROVED"));
						int noOfTotalLeaves = uF.parseToInt((String)hmInnerAll.get(CONSOLIDATED+"_APPROVED")) + rs.getInt("emp_no_of_leave");
						noOfLeaves +=  rs.getInt("emp_no_of_leave");
						hmInner.put(rs.getString("leave_type_name")+"_APPROVED", noOfLeaves+"");
						hmInnerAll.put(CONSOLIDATED+"_APPROVED", noOfTotalLeaves+"");
					}else if(rs.getInt("is_approved")==-1){
						int noOfLeaves = uF.parseToInt((String)hmInner.get(rs.getString("leave_type_name")+"_DENIED"));
						int noOfTotalLeaves = uF.parseToInt((String)hmInnerAll.get(CONSOLIDATED+"_DENIED")) + rs.getInt("emp_no_of_leave");
						noOfLeaves +=  rs.getInt("emp_no_of_leave");
						hmInner.put(rs.getString("leave_type_name")+"_DENIED", noOfLeaves+"");
						hmInnerAll.put(CONSOLIDATED+"_DENIED", noOfTotalLeaves+"");
					}else if(rs.getInt("is_approved")==0){
						int noOfLeaves = uF.parseToInt((String)hmInner.get(rs.getString("leave_type_name")+"_WAITING"));
						int noOfTotalLeaves = uF.parseToInt((String)hmInnerAll.get(CONSOLIDATED+"_WAITING")) + rs.getInt("emp_no_of_leave");
						noOfLeaves +=  rs.getInt("emp_no_of_leave");
						hmInner.put(rs.getString("leave_type_name")+"_WAITING", noOfLeaves+"");
						hmInnerAll.put(CONSOLIDATED+"_WAITING", noOfTotalLeaves+"");
					}
					
					
					
					if(isCubeReportSubMeasureAll() ){
						hmEmpOuter.put(strEmpIdNew, hmInnerAll);
						hmOuter.put(strEmpIdNew+arrDates[2], hmInnerAll);
					}else{
						hmEmpOuter.put(strEmpIdNew, hmInner);
						hmOuter.put(strEmpIdNew+arrDates[2], hmInner);
					}
					
				}
				rs.close();
				pst.close();
			}
				
				

				
				session.setAttribute("hmOuter", hmOuter);
				session.setAttribute("hmEmpOuter", hmEmpOuter);
				
				session.setAttribute("alLeaveType", alLeaveType);
				session.setAttribute("hmEmployeeNameMap", hmEmployeeNameMap);
				session.setAttribute("alDates", alDates);
				
				
				log.debug("hmOuter===>"+hmOuter);
				log.debug("hmEmpOuter===>"+hmEmpOuter);
				
				
				if(isCubeReportSubMeasureAll()){
					session.setAttribute("alLeaveType", alLeaveTypeAll);
				}else{
					session.setAttribute("alLeaveType", alLeaveType);
				}
			
				request.setAttribute(PAGE, PCubeLeaveTable);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	final String SelectPayrollGeneration = "select * from payroll_generation pg, salary_details sd where pg.salary_head_id = sd.salary_head_id and paycycle=? order by pay_date, emp_id";
	
	public void viewSalaryReport(){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		con = db.makeConnection(con);
		try {
			Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con,strUserType, strSessionEmpId);
			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con);
			List<String> alSalaryHeadsSelected = new ArrayList<String>();
			List<String> alSalaryHeadsSelectedAll = new ArrayList<String>();

			String []currentPayCycle = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
			String []prev1PayCycle = CF.getPrevPayCycle(con,currentPayCycle[0], CF.getStrTimeZone(), CF,request);
			String []prev2PayCycle = CF.getPrevPayCycle(con,prev1PayCycle[0], CF.getStrTimeZone(), CF,request);
				
			
			String []arrSalaryHeads = getCubeReportSubMeasure(); 
			String []arrWLocation = getCubeReportParaC();
			String []arrLevel = getCubeReportParaE();
			
			
			
			
			
			List<String[]> alDates = new ArrayList<String[]>();
			alDates.add(currentPayCycle);
			alDates.add(prev1PayCycle);
			alDates.add(prev2PayCycle);
			
					
			
			if(isCubeReportSubMeasureAll()){
				alSalaryHeadsSelectedAll.add(CONSOLIDATED+EARNING);
				alSalaryHeadsSelectedAll.add(CONSOLIDATED+DEDUCTION);
			}else{
				for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
					alSalaryHeadsSelected.add(arrSalaryHeads[i]);
				}
			}
			
			
			
			
			
			
			
			Map<String, Map<String, String>> hmSalary = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmSalaryEmp = new HashMap<String, Map<String, String>>();
			Map<String, String> hmInner = new HashMap<String, String>();
			Map<String, String> hmInnerAll = new HashMap<String, String>();
			
			
			for(int k=0; k<alDates.size(); k++){
				String []arrDates = alDates.get(k);
				pst = con.prepareStatement(SelectPayrollGeneration);
				pst.setInt(1, uF.parseToInt(arrDates[2]));
				rs = pst.executeQuery();
				
				
				
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

					
					
					if(isCubeReportSubMeasureAll()){
						if(rs.getString("earning_deduction").equalsIgnoreCase("E")){
							double dblTotal = uF.parseToDouble((String)hmInnerAll.get(CONSOLIDATED+EARNING));
							hmInnerAll.put(CONSOLIDATED+EARNING, uF.formatIntoTwoDecimal(dblTotal + rs.getDouble("amount")));
							hmSalaryHeadsMap.put(CONSOLIDATED+EARNING, CONSOLIDATED+EARNING);
						}else if(rs.getString("earning_deduction").equalsIgnoreCase("D")){
							double dblTotal = uF.parseToDouble((String)hmInnerAll.get(CONSOLIDATED+DEDUCTION));
							hmInnerAll.put(CONSOLIDATED+DEDUCTION, uF.formatIntoTwoDecimal(dblTotal + rs.getDouble("amount")));
							hmSalaryHeadsMap.put(CONSOLIDATED+DEDUCTION, CONSOLIDATED+DEDUCTION);
						}
						
					}else if(arrSalaryHeads!=null && arrSalaryHeads.length>0 && ArrayUtils.contains(arrSalaryHeads, rs.getString("salary_head_id"))>=0 ){
						hmInner.put(rs.getString("salary_head_id"), rs.getString("amount"));
					}
					
					
					if(isCubeReportSubMeasureAll()){
						hmSalary.put(strEmpIdNew+arrDates[2], hmInnerAll);
						hmSalaryEmp.put(strEmpIdNew, hmInnerAll);
						
						log.debug("hmSalary Inner All===>"+hmInnerAll);
						  
					}else{
						hmSalary.put(strEmpIdNew+arrDates[2], hmInner);
						hmSalaryEmp.put(strEmpIdNew, hmInner);
						
						log.debug("hmSalary Inner ===>"+hmInnerAll);
					}
					
					
					strEmpIdOld = strEmpIdNew;
				}
				rs.close();
				pst.close();
			}
			
			
			
			session.setAttribute("hmSalary", hmSalary);
			session.setAttribute("hmSalaryEmp", hmSalaryEmp);
			session.setAttribute("hmEmployeeNameMap", hmEmployeeNameMap);
			session.setAttribute("alDates", alDates);
			session.setAttribute("hmSalaryHeadsMap", hmSalaryHeadsMap);
			
			
			if(isCubeReportSubMeasureAll()){
				session.setAttribute("alSalaryHeadsSelected", alSalaryHeadsSelectedAll);
			}else{
				session.setAttribute("alSalaryHeadsSelected", alSalaryHeadsSelected);
			}
			
			request.setAttribute(PAGE, PCubeSalaryTable);
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
		con = db.makeConnection(con);
		try {
			Map<String, String> hmEmployeeName = CF.getEmpNameMap(con,strUserType, strSessionEmpId);
			
			String []currentPayCycle = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
			String []prev1PayCycle = CF.getPrevPayCycle(con,currentPayCycle[0], CF.getStrTimeZone(), CF,request);
			String []prev2PayCycle = CF.getPrevPayCycle(con,prev1PayCycle[0], CF.getStrTimeZone(), CF,request);
				
			List<String[]> alDates = new ArrayList<String[]>();
			alDates.add(currentPayCycle);
			alDates.add(prev1PayCycle);
			alDates.add(prev2PayCycle);

			
					

			Map<String, String> hmRosterEmp = new HashMap<String, String>();
			Map<String, String> hmRoster = new HashMap<String, String>();
			Map<String, String> hmActualEmp = new HashMap<String, String>();
			Map<String, String> hmActual = new HashMap<String, String>();
			
			String []arrWLocation = getCubeReportParaC();
			String []arrLevel = getCubeReportParaE();
			
			
			
			for(int i=0; i<alDates.size(); i++){
				String []arrDates = (String[])alDates.get(i);
				
				if(ArrayUtils.containsString(getCubeReportSubMeasure(), "R")>=0){
					
					pst = con.prepareStatement(selectCRRosterHoursEmp);
					pst.setDate(1, uF.getDateFormat(arrDates[0], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(arrDates[1], DATE_FORMAT));
					rs = pst.executeQuery();
					while(rs.next()){
						String strWLocationId = hmEmpWlocationMap.get(rs.getString("emp_id"));
						String strLevelId = hmEmpLevelMap.get(rs.getString("emp_id"));
						if(arrWLocation!=null && arrWLocation.length>0 && strWLocationId!=null && ArrayUtils.contains(arrWLocation, strWLocationId) < 0 )continue;
						if(arrLevel!=null && arrLevel.length>0 && strLevelId!=null &&ArrayUtils.contains(arrLevel, strLevelId) < 0 )continue;
						
						if(hmEmployeeName.containsKey(rs.getString("emp_id"))){
							hmRoster.put(rs.getString("emp_id")+arrDates[2], rs.getString("roster_hours"));
							hmRosterEmp.put(rs.getString("emp_id"), rs.getString("roster_hours"));
						}
					}
					rs.close();
					pst.close();
				}
			}
			
			
			for(int i=0; i<alDates.size(); i++){
				String []arrDates = (String[])alDates.get(i);
				
				if(ArrayUtils.containsString(getCubeReportSubMeasure(), "A")>=0){
					
					pst = con.prepareStatement(selectCRActualHoursEmp);
					pst.setDate(1, uF.getDateFormat(arrDates[0], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(arrDates[1], DATE_FORMAT));
					rs = pst.executeQuery();
					while(rs.next()){
						
						String strWLocationId = hmEmpWlocationMap.get(rs.getString("emp_id"));
						String strLevelId = hmEmpLevelMap.get(rs.getString("emp_id"));
						if(arrWLocation!=null && arrWLocation.length>0 && strWLocationId!=null && ArrayUtils.contains(arrWLocation, strWLocationId) < 0 )continue;
						if(arrLevel!=null && arrLevel.length>0 && strLevelId!=null &&ArrayUtils.contains(arrLevel, strLevelId) < 0 )continue;
						
						if(hmEmployeeName.containsKey(rs.getString("emp_id"))){
							hmActual.put(rs.getString("emp_id")+arrDates[2], rs.getString("hours_worked"));
							hmActualEmp.put(rs.getString("emp_id"), rs.getString("hours_worked"));
						}
					}
					rs.close();
					pst.close();
				}
			}
			
			
			List<String> alReportLabel = new ArrayList<String>();
			
			if(ArrayUtils.containsString(getCubeReportSubMeasure(), "A")>=0 && 
					ArrayUtils.containsString(getCubeReportSubMeasure(), "R")>=0){
				alReportLabel.add("Actual");
				alReportLabel.add("Roster");
				
				
				session.setAttribute("hmActualEmp", hmActualEmp);
				session.setAttribute("hmActual", hmActual);
				session.setAttribute("hmRosterEmp", hmRosterEmp);
				session.setAttribute("hmRoster", hmRoster);
				
			}else if(ArrayUtils.containsString(getCubeReportSubMeasure(), "A")>=0){
				alReportLabel.add("Actual");
				session.setAttribute("hmActualEmp", hmActualEmp);
				session.setAttribute("hmActual", hmActual);
				
			}else if(ArrayUtils.containsString(getCubeReportSubMeasure(), "R")>=0){
				alReportLabel.add("Roster");
				session.setAttribute("hmRosterEmp", hmRosterEmp);
				session.setAttribute("hmRoster", hmRoster);
			}
			
			
			
			session.setAttribute("alDates", alDates);
			session.setAttribute("alReportLabel", alReportLabel);
			session.setAttribute("hmEmployeeName", hmEmployeeName);
			
			request.setAttribute(PAGE, PCubeTimeTable);
			
			
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

	public boolean isCubeReportSubMeasureAll() {
		return cubeReportSubMeasureAll;
	}

	public void setCubeReportSubMeasureAll(boolean cubeReportSubMeasureAll) {
		this.cubeReportSubMeasureAll = cubeReportSubMeasureAll;
	}



}
