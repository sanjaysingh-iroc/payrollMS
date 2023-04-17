package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Timesheet  extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5167383523996336618L;
	HttpSession session;
	CommonFunctions CF;
	String strSessionEmpId;
	String strOrgId;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN; 
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strOrgId = (String)session.getAttribute(ORGID);
		
		getTimesheet();
		
		return SUCCESS;
	}

	
	
	public void getTimesheet() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
		
			con = db.makeConnection(con);
			
			Map<String, String> hmTimesheetSubmittedDate = new HashMap<String, String>();
			pst = con.prepareStatement("select * from project_timesheet");
			rs = pst.executeQuery();
			while(rs.next()){
				hmTimesheetSubmittedDate.put(rs.getString("timesheet_paycycle"), uF.getDateFormat(rs.getString("timesheet_generated_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();
			
		
			List<FillPayCycles> paycycleList = new FillPayCycles(request).fillPayCycles(CF, strOrgId);
			List<String> alReport = new ArrayList<String>();
			String arr[] = null;
			for(int i=0; i<paycycleList.size(); i++){
				 arr = paycycleList.get(i).getPaycycleId().split("-");
				
				
				alReport.add(paycycleList.get(i).getPaycycleName());
				alReport.add("" +
						"<a href=\"javascript:void(0)\" onclick=\"sendTimesheet("+strSessionEmpId+", '"+arr[0]+"','"+arr[1]+"', 0)\">Donwload</a>" +
						" | " +
//						"<div id=\"myDiv_"+i+"\"><a href=\"javascript:void(0)\" onclick=\"(confirm('Are you sure you want to submit your timesheet?')?getContent('myDiv_"+i+"', sendTimesheet("+strSessionEmpId+", '"+arr[0]+"','"+arr[1]+"', 1)'):'')\">Submit</a>" +
						((hmTimesheetSubmittedDate.containsKey(arr[2]))?
								"<div id=\"myDiv_"+i+"\" style=\"float: right; width: 200px;\">Timesheet sent on "+hmTimesheetSubmittedDate.get(arr[2])+"</div>"
								:
								"<div id=\"myDiv_"+i+"\" style=\"float: right; width: 200px;\"><a href=\"javascript:void(0)\" onclick=\"(confirm('Are you sure you want to submit your timesheet?')?getContent('myDiv_"+i+"', 'GenerateTimeSheet1.action?mailAction=sendMail&empid="+ strSessionEmpId+"&datefrom="+arr[0]+"&dateto="+arr[1]+"&strPC="+arr[2]+"&downloadSubmit="+1+"'):'')\">Submit Timesheet</a></div>" 
						)
						
						);
			} 
			
			request.setAttribute("alReport", alReport);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	HttpServletRequest request;
	HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
		
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}
}
