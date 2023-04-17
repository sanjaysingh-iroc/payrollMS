package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.tms.AddReason;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ExtendProbationDays extends ActionSupport implements ServletRequestAware, IStatements {
   
	/**
	 *
	 */ 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF;
	 
	
	String paycycle;
	String f_org;
	String f_strWLocation;
	String f_department; 
	String f_service;
	String empid;
	String empname;
	String divid;
	String strReason;
	String extendDays;
	String operation;
	
	String strStartDate;
	String strEndDate;
	
	private static Logger log = Logger.getLogger(AddReason.class);
	
	public String execute() throws Exception {

		log.debug("ClockEntries: execute()");
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		UtilityFunctions uF=new UtilityFunctions();
		
		String strEmpId = (String)request.getAttribute("empid");
		
		
		
		if(getOperation()!=null && getOperation().equals("U")){
			
			updateExtendDays(uF);
			return SUCCESS;
		}
		
		getProbationDetails(uF, strEmpId);
		return LOAD;

	}	
	
	private void updateExtendDays(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs =null;	
		
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select org_id from employee_official_details where emp_id=?");
			pst.setInt(1, uF.parseToInt(getEmpid().trim()));
			rs = pst.executeQuery();
			String orgId=null;
			while(rs.next()){
				orgId=rs.getString("org_id");
			}
			rs.close();
			pst.close();

			String[] arrDates = null;
			if (getPaycycle() != null) {
				arrDates = getPaycycle().split("-");
				setPaycycle(arrDates[0] + "-" + arrDates[1] + "-" + arrDates[2]);
			} else {
				arrDates = CF.getCurrentPayCycleByOrg(con,CF.getStrTimeZone(), CF,orgId);
				setPaycycle(arrDates[0] + "-" + arrDates[1] + "-" + arrDates[2]);
			}
			
			if(getStrStartDate()==null){
				setStrStartDate(arrDates[0]);
			}
			if(getStrEndDate()==null){
				setStrEndDate(arrDates[1]);
			}
			
			
			
			Date startdate=uF.getDateFormat(getStrStartDate(), DATE_FORMAT);
			Date enddate=uF.getDateFormat(getStrEndDate(), DATE_FORMAT);
			
			String startMonth=uF.getDateFormat(getStrStartDate(), DATE_FORMAT, "MM");
			String startYear=uF.getDateFormat(getStrStartDate(), DATE_FORMAT, "yyyy");
			
			String endMonth=uF.getDateFormat(getStrEndDate(), DATE_FORMAT, "MM");
			String endYear=uF.getDateFormat(getStrEndDate(), DATE_FORMAT, "yyyy");
			
			
			pst = con.prepareStatement("select * from employee_activity_details ead,employee_official_details eod,employee_personal_details epd " +
					" where ead.emp_id=eod.emp_id and ead.emp_id=epd.emp_per_id and eod.emp_id=epd.emp_per_id and ead.activity_id=7 " +
					" and ead.emp_id in (select emp_id from probation_policy where is_probation=true) and ead.probation_period>0 and ead.emp_id=? " +
					" and ead.emp_activity_id in (select max(emp_activity_id) as emp_activity_id from employee_activity_details where activity_id=7 group by emp_id)");
			pst.setInt(1, uF.parseToInt(getEmpid().trim()));
			rs = pst.executeQuery();
			Map<String,String> hmEmpData=new HashMap<String, String>();
			while (rs.next()) {
				Date probationDate=uF.getFutureDate(uF.getDateFormatUtil(rs.getString("joining_date"), DBDATE), rs.getInt("probation_period"));
				String probationMonth=uF.getDateFormat(""+probationDate, DBDATE, "MM");
				String probationYear=uF.getDateFormat(""+probationDate, DBDATE, "yyyy");
				
				if((uF.parseToInt(startMonth)<=uF.parseToInt(probationMonth) && uF.parseToInt(startYear)<=uF.parseToInt(probationYear)) && (uF.parseToInt(endMonth)>=uF.parseToInt(probationMonth) && uF.parseToInt(endYear)>=uF.parseToInt(probationYear))){
					
					hmEmpData.put("EMP_WLOCATION",rs.getString("wlocation_id")); 
					hmEmpData.put("EMP_DEPARTMENT",rs.getString("department_id"));
					hmEmpData.put("EMP_LEVEL",rs.getString("level_id"));
					hmEmpData.put("EMP_DESIGNATION",rs.getString("desig_id"));
					hmEmpData.put("EMP_GRADE",rs.getString("grade_id"));
					hmEmpData.put("EMP_STATUS_CODE",rs.getString("emp_status_code"));
					hmEmpData.put("EMP_EFFECTIVE_DATE",uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
					hmEmpData.put("EMP_NOTICE_PERIOD",rs.getString("notice_period"));
					hmEmpData.put("EMP_PROBATION_PERIOD",rs.getString("probation_period"));
					hmEmpData.put("EMP_PROBATION_END_DATE",uF.getDateFormat(""+probationDate, DBDATE, DATE_FORMAT));
					hmEmpData.put("EMP_JOINING_DATE",uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
					
				}
				
			}
			rs.close();
			pst.close();
		
			if(hmEmpData!=null && !hmEmpData.isEmpty()){	
				int nNewProbationDays =  uF.parseToInt(hmEmpData.get("EMP_PROBATION_PERIOD"))+uF.parseToInt(getExtendDays());
				Date probationDate=uF.getFutureDate(uF.getDateFormatUtil(hmEmpData.get("EMP_JOINING_DATE"), DATE_FORMAT), nNewProbationDays);
				
				String effectiveDate=hmEmpData.get("EMP_EFFECTIVE_DATE");
								
				pst = con.prepareStatement("insert into employee_activity_details (wlocation_id, department_id, level_id, desig_id, grade_id, " +
						"emp_status_code, activity_id, reason, effective_date, entry_date, user_id, emp_id, notice_period, probation_period) " +
						"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(hmEmpData.get("EMP_WLOCATION")));
				pst.setInt(2, uF.parseToInt(hmEmpData.get("EMP_DEPARTMENT")));
				pst.setInt(3, uF.parseToInt(hmEmpData.get("EMP_LEVEL")));
				pst.setInt(4, uF.parseToInt(hmEmpData.get("EMP_DESIGNATION")));
				pst.setInt(5, uF.parseToInt(hmEmpData.get("EMP_GRADE")));
				pst.setString(6, hmEmpData.get("EMP_STATUS_CODE"));
				pst.setInt(7, 7);
				pst.setString(8, "");  
//				pst.setDate(9, uF.getDateFormat(effectiveDate, DATE_FORMAT));
				pst.setDate(9, probationDate);
				pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(11, uF.parseToInt(strSessionEmpId));
				pst.setInt(12, uF.parseToInt(getEmpid().trim()));
				pst.setInt(13, uF.parseToInt(hmEmpData.get("EMP_NOTICE_PERIOD")));
				pst.setInt(14, nNewProbationDays);
				pst.execute();
				pst.close();
				
				
				
				pst = con.prepareStatement("update probation_policy set  probation_duration=? where emp_id=?");
				pst.setInt(1, (uF.parseToInt(hmEmpData.get("EMP_PROBATION_PERIOD"))+uF.parseToInt(getExtendDays())));
				pst.setInt(2, uF.parseToInt(getEmpid().trim()));
				int x = pst.executeUpdate();
				pst.close();
				
				request.setAttribute("STATUS_MSG", "Extended");
				
			}else{
				request.setAttribute("STATUS_MSG", "Extension failed");
			}
						
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("STATUS_MSG", "Extension failed");
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getProbationDetails(UtilityFunctions uF, String strEmpId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs =null;	
		Map<String, String> hmEmpProDetails = new HashMap<String, String>();
		
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select joining_date, probation_duration from employee_personal_details epd, probation_policy pp where pp.emp_id = epd.emp_per_id and emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while(rs.next()){
				
				Date probationDate=uF.getFutureDate(uF.getDateFormatUtil(rs.getString("joining_date"), DBDATE), rs.getInt("probation_duration"));
				
				hmEmpProDetails.put("JOINING_DATE", uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));
				hmEmpProDetails.put("PROBAION_DATE", uF.getDateFormat(probationDate+"", DBDATE, CF.getStrReportDateFormat()));
				
			}
			rs.close();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("STATUS_MSG", "Extension failed");
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		request.setAttribute("hmEmpProDetails", hmEmpProDetails);
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

	public String getF_department() {
		return f_department;
	}

	public void setF_department(String f_department) {
		this.f_department = f_department;
	}

	public String getF_service() {
		return f_service;
	}

	public void setF_service(String f_service) {
		this.f_service = f_service;
	}

	public String getEmpid() {
		return empid;
	}

	public void setEmpid(String empid) {
		this.empid = empid;
	}

	public String getEmpname() {
		return empname;
	}

	public void setEmpname(String empname) {
		this.empname = empname;
	}

	public String getDivid() {
		return divid;
	}

	public void setDivid(String divid) {
		this.divid = divid;
	}

	public String getStrReason() {
		return strReason;
	}

	public void setStrReason(String strReason) {
		this.strReason = strReason;
	}

	public String getExtendDays() {
		return extendDays;
	}

	public void setExtendDays(String extendDays) {
		this.extendDays = extendDays;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
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

}
