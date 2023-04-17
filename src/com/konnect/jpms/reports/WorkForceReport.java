package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.tms.PayCycleList;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class WorkForceReport extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 * 
	 */
	
	public static	String strDetails;

	ArrayList<String> employeedata=new ArrayList<String>();
	ArrayList<String> employeedatapdf =new ArrayList<String>();
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpID; 
	String strUserType = null;
	String strEmpId = null;
	String strP;  
	
	String param;
	
	String f_strWLocation;
	String f_department;
	String f_level;
	
	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(WorkForceReport.class);
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(TITLE, "Workforce Report");
		request.setAttribute(PAGE, "/jsp/reports/WorkForceReport.jsp");
		

		strEmpID = (String) request.getParameter("EMPID");
		strP = (String) request.getParameter("param");
		
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		*/
		
			
		viewWorkforceReport();

		return loadWorkforceReport();

	}

	public String loadWorkforceReport() {
		
		wLocationList = new FillWLocation(request).fillWLocation();
		departmentList = new FillDepartment(request).fillDepartment();
		levelList = new FillLevel(request).fillLevel();
		
		return LOAD;
	}

	public String viewWorkforceReport() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			
			
			if(getF_strWLocation()==null){
				setF_strWLocation((String)session.getAttribute(WLOCATIONID));
			}
			
			
			con = db.makeConnection(con);
			
			List<String> alInner = new ArrayList<String>();
			List<List<String>> alReport = new ArrayList<List<String>>();
			
			Map<String, String> hmEmpNames = new HashMap<String, String>();
			Map<String, String> hmEmpCode = new HashMap<String, String>();
			Map<String, String> hmEmpPerEmail = new HashMap<String, String>();
			Map<String, String> hmEmpSecEmail = new HashMap<String, String>();
			
			Map<String, String> hmDepart = CF.getDepartmentMap(con,null, null);
			Map<String, String> hmWLocation = CF.getWLocationMap(con,null, null);
			Map<String, String> hmEmpDesig = CF.getEmpDesigMap(con);
			
			
			pst = con.prepareStatement("select * from employee_personal_details where is_alive = true");
			rs = pst.executeQuery();
			while(rs.next()){
				hmEmpNames.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
				hmEmpCode.put(rs.getString("emp_per_id"), rs.getString("empcode"));
				hmEmpPerEmail.put(rs.getString("emp_per_id"), rs.getString("emp_email"));
				hmEmpSecEmail.put(rs.getString("emp_per_id"), rs.getString("emp_email_sec"));
			}
			rs.close();
			pst.close();
			
			
			
			
			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery.append("select * from employee_activity_details ead, (select min(emp_activity_id) as emp_activity_id from employee_activity_details group by emp_id) eads where ead.emp_activity_id = eads.emp_activity_id");
			
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and department_id = "+uF.parseToInt(getF_department()));
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and level_id = "+uF.parseToInt(getF_level()));			
			}
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			
			while(rs.next()){
				alInner = new ArrayList<String>();
				
				if(hmEmpNames.get(rs.getString("emp_id"))!=null){
					alInner.add(uF.showData(hmEmpCode.get(rs.getString("emp_id")), ""));
					alInner.add(uF.showData(hmEmpNames.get(rs.getString("emp_id")), ""));
					alInner.add(uF.showData(hmWLocation.get(rs.getString("wlocation_id")), ""));
					alInner.add(uF.showData(hmDepart.get(rs.getString("department_id")), ""));
					alInner.add(uF.showData(hmEmpDesig.get(rs.getString("emp_id")), ""));
					
					alInner.add(uF.showData(hmEmpPerEmail.get(rs.getString("emp_id")), ""));
					alInner.add(uF.showData(hmEmpSecEmail.get(rs.getString("emp_id")), ""));
					
					
					alReport.add(alInner);
				}
				
				
				
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("alReport", alReport);
			
			
			
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
	
	
	 

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public static void main(String args[]) {

		try {
			PayCycleList pcl = new PayCycleList();
			pcl.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
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

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

}
