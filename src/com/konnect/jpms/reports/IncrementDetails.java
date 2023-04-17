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

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class IncrementDetails extends ActionSupport implements ServletRequestAware, IStatements {

	/**     
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType;
	String strSessionEmpId;
	CommonFunctions CF=null;

	private static Logger log = Logger.getLogger(IncrementDetails.class);
	
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, PApproveIncrement);
		request.setAttribute(TITLE, TApproveIncrement);
		
		
		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		
		
		viewIncrementDetails(uF);			
		return loadIncrementDetails();

	}
	
	
	public String loadIncrementDetails() {
		return LOAD;
	}
	
	public String viewIncrementDetails(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			
			
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			
			
			
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con, null, null);
			Map<String, Map<String, String>> hmCurrentSalaryDetails = CF.getCurrentSalary(con);
			
			pst = con.prepareStatement("select * from employee_personal_details epd left join (select *, ead.emp_id as empid from employee_activity_details ead, (select max(effective_date) as effective_date, emp_id from employee_activity_details group by emp_id) a where activity_id in (1,2) and ead.effective_date = a.effective_date and ead.emp_id = a.emp_id) a on epd.emp_per_id = a.empid");
			rs = pst.executeQuery();
			while(rs.next()) {
				
				Map<String, String> hmEmpSalaryDetails = hmCurrentSalaryDetails.get(rs.getString("emp_per_id"));
				if(hmEmpSalaryDetails==null)hmEmpSalaryDetails=new HashMap<String, String>();
				double dblBasic = uF.parseToDouble(hmEmpSalaryDetails.get(BASIC+""));
				Map<String, String> hmIncrementDetails = CF.getIncrementAmount(con, dblBasic);
				if(hmIncrementDetails==null)hmIncrementDetails=new HashMap<String, String>();
				double dblIncrement = uF.parseToDouble(hmIncrementDetails.get("INCREMENT_AMOUNT"));
				int nMonth = uF.parseToInt(hmIncrementDetails.get("DUE_MONTH"));
				int nCurrentMonth = uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"));
				int nCurrentYear = uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"));
				java.sql.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
				java.sql.Date dtEffectiveDate = null;
				
				if(rs.getString("effective_date")!=null){
					dtEffectiveDate = uF.getDateFormat(rs.getString("effective_date"), DBDATE);
				}
				
				
				if(nMonth<=nCurrentMonth){
					nCurrentYear++;
				}
				
//				log.debug(hmIncrementDetails+"=hmIncrementDetails");
//				log.debug(nCurrentYear+"=nCurrentYear");
//				log.debug(nMonth+"=nMonth");
//				log.debug(nCurrentMonth+"=nCurrentMonth");
//				log.debug("nMonth="+uF.getDateFormat(nMonth+"", "MM", "MMM"));
//				log.debug("nCurrentMonth="+uF.getDateFormat(nCurrentMonth+"", "MM", "MMM"));

				if(hmEmpNameMap.containsKey(rs.getString("emp_per_id"))){
					
					alInner = new ArrayList<String>();
					alInner.add(rs.getString("activity_id"));
					alInner.add(hmEmpNameMap.get(rs.getString("emp_per_id")));
					alInner.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));
					alInner.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()));
					if(nMonth>0){
						alInner.add(uF.getDateFormat(nMonth+"-"+nCurrentYear, "MM-yyyy", "MMM, yyyy"));
					}else{
						alInner.add("-");
					}
					
//					alInner.add(nMonth+"-"+nCurrentYear);
					alInner.add(uF.formatIntoComma(dblBasic));
					alInner.add(uF.formatIntoComma(dblIncrement));
					
					
					log.debug("dtCurrentDate===="+dtCurrentDate);
					log.debug("dtEffectiveDate===="+dtEffectiveDate);
					
					
					
					
					if(dtEffectiveDate!=null && dtCurrentDate.after(dtEffectiveDate)){
						alInner.add("<div id=\"myDiv"+rs.getString("emp_per_id")+"\"><a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+rs.getString("emp_per_id")+"','ApproveIncrement.action?EMPID="+rs.getString("emp_per_id")+"&ACTIVITYID=1&MONTH="+nMonth+"&YEAR="+nCurrentYear+"');\">Approve</a></div>");
					}else if(dtEffectiveDate!=null){
						alInner.add("Approved on "+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
					}else{
						alInner.add("");
					}
					
					
					
					
					al.add(alInner);
					
					
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", al);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
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

}
