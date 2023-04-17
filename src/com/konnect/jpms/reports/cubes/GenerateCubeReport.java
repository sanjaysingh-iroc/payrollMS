package com.konnect.jpms.reports.cubes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;
 
public class GenerateCubeReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	 
	public String execute() throws Exception {
		
				
		request.setAttribute(PAGE, PCubeReport);
		request.setAttribute(TITLE, TReports);
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return SUCCESS;
			
		
		
		if(getReportType()!=null && getReportType().equalsIgnoreCase("T")){
			
			GenerateTableCubeReport objGTCR = new GenerateTableCubeReport(	CF, request,																			
																			cubeReportMeasure,
																			cubeReportSubMeasureAll,
																			reportType, 
																			cubeReportSubMeasure, 
																			cubeReportParaC, 
																			cubeReportParaE); 
			String strReturnType = objGTCR.generateReport();
			return strReturnType;
		}else if(getReportType()!=null && getReportType().equalsIgnoreCase("C")){
			GenerateGraphicalCubeReport objGGCR = new GenerateGraphicalCubeReport(	CF, request,
																					cubeReportMeasure, 
																					reportType, 
																					cubeReportSubMeasure, 
																					cubeReportParaC, 
																					cubeReportParaE); 
			String strReturnType = objGGCR.generateReport();
			return strReturnType;
		}
		
		
			return SUCCESS;

	}
	
	String cubeReportMeasure;
	String reportType;
	String cubeReportSubMeasureAll;
	String []cubeReportSubMeasure;
	String []cubeReportParaC;
	String []cubeReportParaE;
	
	
//
//	
//	final static public String selectCRRosterHoursEmp = "select sum(actual_hours) as roster_hours, emp_id from roster_details where _date between ? and ? group by emp_id";
//	final static public String selectCRActualHoursEmp = "select sum(hours_worked) as hours_worked, emp_id from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? group by emp_id";
//	final static public String selectCREmployeeLeaveEntryR   = "select * from (SELECT * FROM emp_leave_entry ee, leave_type lt where lt.leave_type_id = ee.leave_type_id order by emp_id) a, employee_personal_details epd where epd.emp_per_id=a.emp_id and (approval_from, approval_to_date) overlaps (to_date(?::text, 'YYYY-MM-DD')-1,to_date(?::text, 'YYYY-MM-DD') +1)order by emp_fname,emp_lname, emp_id";
//	
//	public void viewCubeLeaveReport(){
//		
//
//		
//		Connection con = null;
//		PreparedStatement pst=null;
//		ResultSet rs = null;
//		Database db = new Database();
//		UtilityFunctions uF = new UtilityFunctions();
//
//		Map hmEmployeeNameMap = CF.getEmpNameMap();
//		
//		try {
//			
//			
//			String []currentPayCycle = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF);
//			String []prevPayCycle = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF);
//					
//			con = db.makeConnection(con);
//			
//			
//			
//				
//				pst = con.prepareStatement(selectCREmployeeLeaveEntryR);
//				pst.setDate(1, uF.getDateFormat(currentPayCycle[0], DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(currentPayCycle[1], DATE_FORMAT));
//				
//				rs = pst.executeQuery();
//				Map<String, Map<String, String>> hmOuter = new LinkedHashMap<String, Map<String, String>>();
//				Map<String, String> hmInner = new HashMap<String, String>();
//				
//				List<String> alLeaveType = new ArrayList<String>();
//				
//				String strEmpIdNew = null;
//				
//				
//				System.out.println("pst===>"+pst);
//				String arrLeaveType[] = getCubeReportSubMeasure(); 
//				String []arrWLocation = getCubeReportParaC();
//				String []arrLevel = getCubeReportParaE();
//				
//				while(rs.next()){
//					strEmpIdNew = rs.getString("emp_id");
//					
//					String strWLocationId = hmEmpWlocationMap.get(strEmpIdNew);
//					String strLevelId = hmEmpLevelMap.get(strEmpIdNew);
//					
//					
//					
//					if(arrWLocation!=null && arrWLocation.length>0 && strWLocationId!=null && ArrayUtils.contains(arrWLocation, strWLocationId) < 0 )continue;
//					if(arrLevel!=null && arrLevel.length>0 && strLevelId!=null && ArrayUtils.contains(arrLevel, strLevelId) < 0 )continue;
//
//					if(!alLeaveType.contains(rs.getString("leave_type_name")) &&  ArrayUtils.contains(arrLeaveType, rs.getString("leave_type_id"))>=0 ){
//						alLeaveType.add(rs.getString("leave_type_name"));
//					}
//					
//					
//					hmInner =  hmOuter.get(strEmpIdNew);
//					if(hmInner==null){
//						hmInner = new HashMap<String, String>();
//					}
//					
//					if(rs.getInt("is_approved")==1){
//						int noOfLeaves = uF.parseToInt((String)hmInner.get(rs.getString("leave_type_name")+"_APPROVED"));
//						noOfLeaves +=  rs.getInt("emp_no_of_leave");
//						hmInner.put(rs.getString("leave_type_name")+"_APPROVED", noOfLeaves+"");
//					}else if(rs.getInt("is_approved")==-1){
//						int noOfLeaves = uF.parseToInt((String)hmInner.get(rs.getString("leave_type_name")+"_DENIED"));
//						noOfLeaves +=  rs.getInt("emp_no_of_leave");
//						hmInner.put(rs.getString("leave_type_name")+"_DENIED", noOfLeaves+"");
//					}else if(rs.getInt("is_approved")==0){
//						int noOfLeaves = uF.parseToInt((String)hmInner.get(rs.getString("leave_type_name")+"_WAITING"));
//						noOfLeaves +=  rs.getInt("emp_no_of_leave");
//						hmInner.put(rs.getString("leave_type_name")+"_WAITING", noOfLeaves+"");
//					}
//					hmOuter.put(strEmpIdNew, hmInner);
//				}
//
//				
//				request.setAttribute("hmOuter", hmOuter);
//				request.setAttribute("alLeaveType", alLeaveType);
//				request.setAttribute("hmEmployeeNameMap", hmEmployeeNameMap);
//				request.setAttribute("currentPayCycle", currentPayCycle);
//				
//			
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			db.closeConnection(con);
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//		}
//	}
//	
//	final String SelectPayrollGeneration = "select * from payroll_generation where paycycle=? order by pay_date, emp_id";
//	
//	public void viewSalaryReport(){
//		
//		Connection con = null;
//		PreparedStatement pst=null;
//		ResultSet rs = null;
//		Database db = new Database();
//		UtilityFunctions uF = new UtilityFunctions();
//		String strColspan = null;
//
//		try {
//			Map hmEmployeeNameMap = CF.getEmpNameMap();
//			Map hmSalaryHeadsMap = CF.getSalaryHeadsMap();
//			Map<String, String> hmSalaryHeadsSelectedMap = new LinkedHashMap<String, String>();
//			String []currentPayCycle = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF);
//					
//			String []arrSalaryHeads = getCubeReportSubMeasure(); 
//			String []arrWLocation = getCubeReportParaC();
//			String []arrLevel = getCubeReportParaE();
//
//			
//			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
//				hmSalaryHeadsSelectedMap.put(arrSalaryHeads[i], (String)hmSalaryHeadsMap.get(arrSalaryHeads[i]));
//			}
//			
//			
//			
//			
//			
//			con = db.makeConnection(con);
//			pst = con.prepareStatement(SelectPayrollGeneration);
//			pst.setInt(1, uF.parseToInt(currentPayCycle[2]));
//			rs = pst.executeQuery();
//			
//			Map<String, Map<String, String>> hmSalary = new HashMap<String, Map<String, String>>();
//			Map<String, String> hmInner = new HashMap<String, String>();
//			
//			String strEmpIdNew = null;
//			String strEmpIdOld = null;
//			while(rs.next()){
//				strEmpIdNew = rs.getString("emp_id");
//				
//				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
//					hmInner = new HashMap<String, String>();
//				}
//				
//				String strWLocationId = hmEmpWlocationMap.get(strEmpIdNew);
//				String strLevelId = hmEmpLevelMap.get(strEmpIdNew);
//				
//				for(int i=0; i<arrLevel.length; i++){
//					System.out.println(strLevelId+" === arrLevel=="+arrLevel[i]);
//				}
//				
//				
//				if(arrWLocation!=null && arrWLocation.length>0 && strWLocationId!=null && ArrayUtils.contains(arrWLocation, strWLocationId) < 0 )continue;
//				if(arrLevel!=null && arrLevel.length>0 && strLevelId!=null && ArrayUtils.contains(arrLevel, strLevelId) < 0 )continue;
//
//				if(arrSalaryHeads!=null && arrSalaryHeads.length>0 && ArrayUtils.contains(arrSalaryHeads, rs.getString("salary_head_id"))>=0 ){
//					hmInner.put(rs.getString("salary_head_id"), rs.getString("amount"));
//					
//					
//				}
//				
//				
//				
//				
//				hmSalary.put(strEmpIdNew, hmInner);
//				
//				
//				
//				strEmpIdOld = strEmpIdNew;
//			}
//			
//			request.setAttribute("hmSalary", hmSalary);
//			request.setAttribute("hmEmployeeNameMap", hmEmployeeNameMap);
//			request.setAttribute("currentPayCycle", currentPayCycle);
//			request.setAttribute("hmSalaryHeadsMap", hmSalaryHeadsSelectedMap);
//			
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			db.closeConnection(con);
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//		}
//	}
//	
//	public void viewCubeTimeReport(){
//		
//		Connection con = null;
//		PreparedStatement pst=null;
//		ResultSet rs = null;
//		Database db = new Database();
//		UtilityFunctions uF = new UtilityFunctions();
//		String strColspan = null;
//
//		try {
//			Map hmEmployeeName = CF.getEmpNameMap();
//			
//			String []currentPayCycle = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF);
//			String []prevPayCycle = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF);
//					
//
//			Map hmRosterCurrent = new HashMap();
//			Map hmRosterPrev = new HashMap();
//			
//			con = db.makeConnection(con);
//			
//			
//			
//			if(ArrayUtils.containsString(getCubeReportSubMeasure(), "R")>=0){
//				
//				pst = con.prepareStatement(selectCRRosterHoursEmp);
//				pst.setDate(1, uF.getDateFormat(currentPayCycle[0], DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(currentPayCycle[1], DATE_FORMAT));
//				rs = pst.executeQuery();
//				while(rs.next()){
//					hmRosterCurrent.put(rs.getString("emp_id"), rs.getString("roster_hours"));
//				}
//
//				
//				pst = con.prepareStatement(selectCRRosterHoursEmp);
//				pst.setDate(1, uF.getDateFormat(prevPayCycle[0], DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(prevPayCycle[1], DATE_FORMAT));
//				rs = pst.executeQuery();
//				while(rs.next()){
//					hmRosterPrev.put(rs.getString("emp_id"), rs.getString("roster_hours"));
//				}
//				
//			}
//			
//			Map hmActualCurrent = new HashMap();
//			Map hmActualPrev = new HashMap();
//			
//			if(ArrayUtils.containsString(getCubeReportSubMeasure(), "A")>=0){
//				
//				pst = con.prepareStatement(selectCRActualHoursEmp);
//				pst.setDate(1, uF.getDateFormat(currentPayCycle[0], DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(currentPayCycle[1], DATE_FORMAT));
//				rs = pst.executeQuery();
//				
//				
//				
//				while(rs.next()){
//					hmActualCurrent.put(rs.getString("emp_id"), rs.getString("hours_worked"));
//				}
//				
//				
//				pst = con.prepareStatement(selectCRActualHoursEmp);
//				pst.setDate(1, uF.getDateFormat(prevPayCycle[0], DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(prevPayCycle[1], DATE_FORMAT));
//				rs = pst.executeQuery();
//				
//				
//				
//				while(rs.next()){
//					hmActualPrev.put(rs.getString("emp_id"), rs.getString("hours_worked"));
//				}
//			}
//			
//			
//			List<String> alReportLabel = new ArrayList<String>();
//			List<List<String>> alReport = new ArrayList<List<String>>();
//			List<String> alInner = new ArrayList<String>();
//			
//			
//			String []arrWLocation = getCubeReportParaC();
//			String []arrLevel = getCubeReportParaE();
//			
//			
//			Set set = hmEmployeeName.keySet();
//			Iterator it = set.iterator();
//			
//			while(it.hasNext()){
//				String strEmpIdNew = (String)it.next();
//				alInner = new ArrayList<String>();
//
//				
//				String strWLocationId = hmEmpWlocationMap.get(strEmpIdNew);
//				String strLevelId = hmEmpLevelMap.get(strEmpIdNew);
//				if(arrWLocation!=null && arrWLocation.length>0 && strWLocationId!=null && ArrayUtils.contains(arrWLocation, strWLocationId) < 0 )continue;
//				if(arrLevel!=null && arrLevel.length>0 && strLevelId!=null &&ArrayUtils.contains(arrLevel, strLevelId) < 0 )continue;
//
//				
//				
//				alInner.add((String)hmEmployeeName.get(strEmpIdNew));
//				
//				if(ArrayUtils.containsString(getCubeReportSubMeasure(), "A")>=0 && 
//						ArrayUtils.containsString(getCubeReportSubMeasure(), "R")>=0){
//					
//					alInner.add(uF.showData((String)hmActualCurrent.get(strEmpIdNew), "0"));
//					alInner.add(uF.showData((String)hmRosterCurrent.get(strEmpIdNew), "0"));
//					alInner.add(uF.showData((String)hmActualPrev.get(strEmpIdNew), "0"));
//					alInner.add(uF.showData((String)hmRosterPrev.get(strEmpIdNew), "0"));
//					strColspan = "2";
//				}else if(ArrayUtils.containsString(getCubeReportSubMeasure(), "A")>=0){
//					alInner.add(uF.showData((String)hmActualCurrent.get(strEmpIdNew), "0"));
//					alInner.add(uF.showData((String)hmActualPrev.get(strEmpIdNew), "0"));
//				}else if(ArrayUtils.containsString(getCubeReportSubMeasure(), "R")>=0){
//					alInner.add(uF.showData((String)hmRosterCurrent.get(strEmpIdNew), "0"));
//					alInner.add(uF.showData((String)hmRosterPrev.get(strEmpIdNew), "0"));
//				}
//				
//				
//				alReport.add(alInner);
//				
//			}
//			
//			alReportLabel.add("Employee");
//			if(ArrayUtils.containsString(getCubeReportSubMeasure(), "A")>=0 && 
//					ArrayUtils.containsString(getCubeReportSubMeasure(), "R")>=0){
//				alReportLabel.add("Actual");
//				alReportLabel.add("Roster");
//				alReportLabel.add("Actual");
//				alReportLabel.add("Roster");
//			}else if(ArrayUtils.containsString(getCubeReportSubMeasure(), "A")>=0){
//				alReportLabel.add("Actual");
//				alReportLabel.add("Actual");
//			}else if(ArrayUtils.containsString(getCubeReportSubMeasure(), "R")>=0){
//				alReportLabel.add("Roster");
//				alReportLabel.add("Roster");
//			}
//			
//			request.setAttribute("alReport", alReport);
//			request.setAttribute("alReportLabel", alReportLabel);
//			request.setAttribute("currentPayCycle", currentPayCycle);
//			request.setAttribute("prevPayCycle", prevPayCycle);
//			request.setAttribute("strColspan", strColspan);
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			db.closeConnection(con);
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//		}
//	}
	
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

	public String getCubeReportSubMeasureAll() {
		return cubeReportSubMeasureAll;
	}

	public void setCubeReportSubMeasureAll(String cubeReportSubMeasureAll) {
		this.cubeReportSubMeasureAll = cubeReportSubMeasureAll;
	}

}
