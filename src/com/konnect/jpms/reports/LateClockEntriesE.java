package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.Employee;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LateClockEntriesE extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strEmpId = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(LateClockEntries.class);

	public String execute() throws Exception {
 
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strEmpId = (String) session.getAttribute(EMPID);
		

		request.setAttribute(TITLE, TViewExceptions);
		isEmpUserType = false;

		request.setAttribute(PAGE, PReportLateClockEntriesE);

		String strP = (String)request.getParameter("P");
		if(getStrP()!=null){
			strP = getStrP();
		}else{
			setStrP(strP);
		}
		if(strP!=null && (strP.equalsIgnoreCase("EXWL") || strP.equalsIgnoreCase("EXE") 
				|| strP.equalsIgnoreCase("EXS") || strP.equalsIgnoreCase("EXD") 
				|| strP.equalsIgnoreCase("EXUT"))){
		
			employeeLateClockEntries(strP);
			return loadLateClockEntries();
		}else{ 
			
			request.setAttribute(PAGE, PAccessDenied);
			return ACCESS_DENIED;
		}

	}

	public String loadLateClockEntries() {

		paycycleList = new FillPayCycles(request).fillPayCycles(CF);

		return LOAD;
	}

	

	public String employeeLateClockEntries(String strP) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		Map<String, String> hmIdNameMap = new HashMap<String, String>();
		
		
		
		try {

			con = db.makeConnection(con);

			
			if(strP!=null && strP.equalsIgnoreCase("EXWL")){
				hmIdNameMap = CF.getWLocationMap(con,strUserType, request, strEmpId);
			}else if(strP!=null && strP.equalsIgnoreCase("EXE")){
				hmIdNameMap = CF.getEmpNameMap(con, strUserType, strEmpId);
			}else if(strP!=null && strP.equalsIgnoreCase("EXS")){
				hmIdNameMap = CF.getServicesMap(con, true); 
			}else if(strP!=null && strP.equalsIgnoreCase("EXUT")){
				hmIdNameMap = CF.getUserTypeMap(con);
			}else if(strP!=null && strP.equalsIgnoreCase("EXD")){
				hmIdNameMap = CF.getDepartmentMap(con,strUserType,  strEmpId);
			}
			
			Map<String, String> hmHolidays = CF.getHolidayList(con,request);
			List<String> _alHolidays = new ArrayList<String>();
			List<String> _allDates = new ArrayList<String>();
			List<String> alInnerChart = new ArrayList<String>();
			List<Employee> alId = new ArrayList<Employee>();
			List<String> alIdTemp = new ArrayList<String>();
			Map<String, String> _hmHolidaysColour = new HashMap<String, String>();
			Map hmOutLateCount = new HashMap();
			Map hmInLateCount = new HashMap();

			// String []strPayCycleDates = new
			// CommonFunctions().getCurrentPayCycle(strTimeZone);
			String[] strPayCycleDates = null;

			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
			}

			log.debug("Start ===> " + strPayCycleDates[0]);
			log.debug("End ===> " + strPayCycleDates[1]);

			pst = con.prepareStatement(selectDates);
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();

			while (rs.next()) {
				_allDates.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
				alInnerChart.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();

			
			if(strUserType!=null){
				
				if(strP!=null && strP.equalsIgnoreCase("EXWL")){
					pst = con.prepareStatement(selectLateHoursWLocation);
				}else if(strP!=null && strP.equalsIgnoreCase("EXE")){
					pst = con.prepareStatement(selectLateHoursEmp);
				}else if(strP!=null && strP.equalsIgnoreCase("EXS")){
					pst = con.prepareStatement(selectLateHoursService);
				}else if(strP!=null && strP.equalsIgnoreCase("EXUT")){
					pst = con.prepareStatement(selectLateHoursUserType);
				}else if(strP!=null && strP.equalsIgnoreCase("EXD")){
					pst = con.prepareStatement(selectLateHoursDepartment);
				}
				
				
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				rs = pst.executeQuery();
			}
//			else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
//				pst = con.prepareStatement(selectLateHoursEmpManager);
//				pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//				pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//				pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//				rs = pst.executeQuery();
//			}
			

			Map<String, String> hmOut = new HashMap<String, String>();
			Map<String, String> hmIn = new HashMap<String, String>();
			String strId = null;
			String strName = null;
			while(rs.next()){
				
				
				if(strP!=null && strP.equalsIgnoreCase("EXWL")){
					strId = rs.getString("wlocation_id");
					strName = (String)hmIdNameMap.get(strId);
				}else if(strP!=null && strP.equalsIgnoreCase("EXE")){
					strId = rs.getString("emp_id");
					strName = (String)hmIdNameMap.get(strId);
				}else if(strP!=null && strP.equalsIgnoreCase("EXS")){
					strId = rs.getString("serviceid");
					strName = (String)hmIdNameMap.get(strId);
				}else if(strP!=null && strP.equalsIgnoreCase("EXUT")){
					strId = rs.getString("usertype_id");
					strName = (String)hmIdNameMap.get(strId);
				}else if(strP!=null && strP.equalsIgnoreCase("EXD")){
					strId = rs.getString("depart_id");
					strName = (String)hmIdNameMap.get(strId);
				}
				
				hmOut = (Map) hmOutLateCount.get(strId);
				hmIn = (Map) hmInLateCount.get(strId);
				if (hmOut == null) {
					hmOut = new HashMap();
				}
				if (hmIn == null) {
					hmIn = new HashMap();
				}
				
				if(rs.getString("in_out").equalsIgnoreCase("IN") && rs.getInt("latecount")>0){
					hmIn.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), rs.getString("latecount"));
				}else if(rs.getString("in_out").equalsIgnoreCase("OUT") && rs.getInt("latecount")<0){
					hmOut.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), rs.getString("latecount"));
				}
				
				hmInLateCount.put(strId, hmIn);
				hmOutLateCount.put(strId, hmOut);
				
				if(alIdTemp!=null && !alIdTemp.contains(strId)){
					alIdTemp.add(strId);
					if(strName!=null){
						alId.add(new Employee(strName, strId));	
					}
				}
			}
			rs.close();
			pst.close();
			
			Collections.sort(alId, new BeanComparator("strName"));
			
			
			request.setAttribute("_allDates", _allDates);
			request.setAttribute("hmInLateCount", hmInLateCount);
			request.setAttribute("hmOutLateCount", hmOutLateCount);
			request.setAttribute("FROM", strPayCycleDates[0]);
			request.setAttribute("TO", strPayCycleDates[1]);
			request.setAttribute("alId", alId);
			request.setAttribute("hmIDNameMap", hmIdNameMap);
			
			
//Charts ==>
			StringBuilder sbActualHours = new StringBuilder();
			StringBuilder sbRosterHours = new StringBuilder();
			StringBuilder sbActualPC = new StringBuilder();
			
			for (int i = alInnerChart.size()-1 ; i>=0; i--) {
				
				log.debug("alInnerChart.get(i)==>"+alInnerChart.get(i));
				
				sbActualPC.append("'"+uF.getDateFormat(alInnerChart.get(i), CF.getStrReportDateFormat(), "dd/MM")+"'");
				
				if(i>0) 
					sbActualPC.append(",");
			}
			
			for (int j = 0; j < alId.size(); j++) {
				
				Employee objEmp = (Employee) alId.get(j);
				String strEmpId = (String) objEmp.getStrEmpId();
				String strEmpName = (String) objEmp.getStrName();
				
				sbActualHours.append(
						
						"{"+
						"name: '"+strEmpName+"',"+
						"data: ["
						
						);
				
				sbRosterHours.append(
						
						"{"+
						"name: '"+strEmpName+"',"+
						"data: ["
						
						);

				for (int i = alInnerChart.size()-1 ; i>=0; i--) {
					
						String strDate = (String)alInnerChart.get(i);
					
						String strActualInner1 =  hmIn.get(strDate);
						String strRosterInner1 =  hmOut.get(strDate);
						
						sbActualHours.append(
								uF.showData(strActualInner1, 0 + "")
					    );
						
						sbRosterHours.append(
								uF.showData(strRosterInner1, 0 + "")
					    );
					
						if (i>0) {
							sbActualHours.append(",");
							sbRosterHours.append(",");
						}
						
						if (i==0) {
							sbActualHours.append("]}");
							sbRosterHours.append("]}");
						}
						
				}
				
				if(j<alId.size()-1) {
					sbActualHours.append(",");
					sbRosterHours.append(",");
				}
				
			}
			
			request.setAttribute("alPayCyclesChart", alInnerChart);
			request.setAttribute("sbActualHours", sbActualHours);
			request.setAttribute("sbActualPC", sbActualPC.toString());
			request.setAttribute("sbRosterHours", sbRosterHours);
			request.setAttribute("strP", strP);
			
			
			
			

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	String strP;
	String paycycle;
	List<FillPayCycles> paycycleList;

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public String getStrP() {
		return strP;
	}

	public void setStrP(String strP) {
		this.strP = strP;
	}

}
