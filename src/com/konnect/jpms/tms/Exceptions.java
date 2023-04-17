package com.konnect.jpms.tms;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import ChartDirector.GetSessionImage;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.Employee;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Exceptions extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId;
	String strUserType;
	String strD1 = null;
	String strD2 = null;
	 
	String strSelectedEmpId;
	private String level;
	List<FillLevel> levelList; 
	List<FillEmployee> empNamesList;
	
	CommonFunctions CF = null;
	String paycycle;
	List<FillPayCycles> payCycleList;
	//List<FillEmployee> empList;
	
	
	private static Logger log = Logger.getLogger(Exceptions.class);
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);

		strD1 = (String) request.getParameter("D1");
		strD2 = (String) request.getParameter("D2");

		if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)){
			request.setAttribute(TITLE, "My Issues");
		}else{
			request.setAttribute(TITLE, "Clock On/Off Exceptions");
		}
		
		
		request.setAttribute(PAGE, "/jsp/tms/Exceptions.jsp");

		String[] strPayCycleDates = null;
		if(strD1==null) {
			
			loadClockEntries();
			String firstEmp = null;
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)){
				setStrSelectedEmpId(strSessionEmpId);
			}else{
				if(getStrSelectedEmpId()==null && empNamesList!=null && empNamesList.size()>0){
					firstEmp = empNamesList.get(0).getEmployeeId();
					
					setStrSelectedEmpId(firstEmp);
				}else{
					setStrSelectedEmpId(getStrSelectedEmpId());
				}
			}
			
			
			
			
			
			if (getPaycycle() != null) {
				
				strPayCycleDates = getPaycycle().split("-");
				strD1 = strPayCycleDates[0];
				strD2 = strPayCycleDates[1];
			
			} else {
				
				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+"-"+strPayCycleDates[2]);
				strD1 = strPayCycleDates[0];
				strD2 = strPayCycleDates[1];
				
			}
			
			
			
			
		}
	
		viewClockEntries(strD1, strD2);
		
		

		return loadClockEntries();
	}

	public String loadClockEntries() {
		UtilityFunctions uF = new UtilityFunctions();
		payCycleList = new FillPayCycles(request).fillPayCycles(CF);
		levelList = new FillLevel(request).fillLevel();
		empNamesList = new FillEmployee(request).fillEmployeeName(strD1, uF.parseToInt(getLevel()));
		return LOAD;
	}

	public String viewClockEntries(String strD1, String strD2) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			
			
						
			
			
			
			
			
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace(); 
			log.error(e.getClass() + ": " +  e.getMessage(), e);
			return ERROR;
		} finally {
			db.closeConnection(con);
		}
		

		return SUCCESS;
	}

	
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

	public List<FillPayCycles> getPayCycleList() {
		return payCycleList;
	}

	public void setPayCycleList(List<FillPayCycles> payCycleList) {
		this.payCycleList = payCycleList;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillEmployee> getEmpNamesList() {
		return empNamesList;
	}

	public void setEmpNamesList(List<FillEmployee> empNamesList) {
		this.empNamesList = empNamesList;
	}

	public String getStrSelectedEmpId() {
		return strSelectedEmpId;
	}

	public void setStrSelectedEmpId(String strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}

}
