package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Performance extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;

	String strSessionEmpId = null;
	UtilityFunctions uF = new UtilityFunctions();

	String alertStatus;
	String alert_type;
	
	String dataType;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, "Reviews");
		request.setAttribute(PAGE, "/jsp/reports/master/Performance.jsp");
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
//		System.out.println("strUserType =====>> " +strUserType);
		if(strUserType != null && strUserType.equals(EMPLOYEE)) {
			getAppraisalReport();
		} else {
			getAllEmpAppraisalReport();
		}
		return LOAD;

	}

	
	private void getAllEmpAppraisalReport() {
		
		List<List<String>> allAppraisalreport = new ArrayList<List<String>>();	
		Connection con=null;
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst=null;
		ResultSet rst=null;
	    try {	
	    	
	    	con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select pde.*,pd.performance_name from performance_details_empwise pde, performance_details pd where emp_performance_file_name is not null and pde.performance_id=pd.performance_id order by pd.performance_name");
			pst = con.prepareStatement(sbQuery.toString());
			rst = pst.executeQuery();
			int count = 0;
			while(rst.next()) {
				count++;
				List<String> appraisal_info = new ArrayList<String>(); 
				
				appraisal_info.add(rst.getString("performance_id"));
				//System.out.println("empList ===> "+empList );
				appraisal_info.add(rst.getString("performance_name"));
				appraisal_info.add(rst.getString("performance_file_name"));
				appraisal_info.add(rst.getString("emp_performance_file_name"));
				appraisal_info.add(rst.getString("emp_id"));
				appraisal_info.add(CF.getEmpNameMapByEmpId(con, rst.getString("emp_id")));
				appraisal_info.add(uF.getDateFormat(rst.getString("upload_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisal_info.add(uF.getDateFormat(rst.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));

				allAppraisalreport.add(appraisal_info);
			}
			rst.close();
			pst.close();
		} catch (Exception e){
				e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("allAppraisalreport",allAppraisalreport);
	}

	

	public void getAppraisalReport() {
		
		List<List<String>> allAppraisalreport = new ArrayList<List<String>>();	
		Connection con=null;
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst=null;
		ResultSet rst=null;
	    try {	
	    	
	    	con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select pde.*,pd.performance_name from performance_details_empwise pde, performance_details pd where pde.performance_id=pd.performance_id and emp_id=? order by pd.performance_name");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
			int count = 0;
			while(rst.next()) {
				count++;
				List<String> appraisal_info = new ArrayList<String>(); 
				
				appraisal_info.add(rst.getString("performance_id"));
				//System.out.println("empList ===> "+empList );
				appraisal_info.add(rst.getString("performance_name"));
				appraisal_info.add(rst.getString("performance_file_name"));
				appraisal_info.add(rst.getString("emp_performance_file_name"));
				appraisal_info.add(rst.getString("emp_id"));
				appraisal_info.add(uF.getDateFormat(rst.getString("upload_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisal_info.add(uF.getDateFormat(rst.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				allAppraisalreport.add(appraisal_info);
			}
			rst.close();
			pst.close();
		} catch (Exception e){
				e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("allAppraisalreport",allAppraisalreport);
	}

	
	String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
