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

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmpServiceRecordReport extends ActionSupport implements IConstants,ServletRequestAware{
	HttpSession session;
	CommonFunctions CF = null; 
	String strUserType;
	private String fromPage;
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;//
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/testsuraj/EmpServiceRecordReport.jsp");
		request.setAttribute(TITLE, "Employee Service Record Report");
		viewBirthDayReport(uF);
		   return LOAD;
	}
	private void viewBirthDayReport(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			List<Map<String, String>> emplist = new ArrayList<Map<String, String>>();
			List<String> coloumvalues = new ArrayList<String>();
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select epd.empcode,epd.emp_fname,epd.emp_lname, epd.joining_date , eob.last_day_date ,di.dept_name ,dd.designation_name ,(select age('2017-06-09',epd.joining_date) as days)from  employee_personal_details epd left join  emp_off_board eob on eob.emp_id=epd.emp_per_id left join employee_official_details eod on eod.emp_id= epd.emp_per_id left join grades_details gd on gd.grade_id = eod.grade_id left join designation_details dd on dd.designation_id =gd.designation_id left join department_info di on di.dept_id =eod.depart_id ");
			
			pst = con.prepareStatement(sbQuery.toString());
			System.out.println("pst====>"+pst);
			rs =pst.executeQuery();
			while (rs.next()) {
				Map< String, String> hmConfirmattionDuedata = new HashMap<String, String>();
				hmConfirmattionDuedata.put("empName",uF.showData(rs.getString("emp_fname")+" "+rs.getString("emp_lname"),""));
				hmConfirmattionDuedata.put("joining_date",uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				hmConfirmattionDuedata.put("last_day_date",uF.getDateFormat(rs.getString("last_day_date"), DBDATE, DATE_FORMAT));
				hmConfirmattionDuedata.put("empDepartmentName",uF.showData(rs.getString("dept_name"),""));
				hmConfirmattionDuedata.put("empDesignationName",uF.showData(rs.getString("designation_name"),""));
				hmConfirmattionDuedata.put("NoDays",uF.showData(rs.getString("days"),""));
				emplist.add(hmConfirmattionDuedata);
			}
			rs.close();
			pst.close();
			request.setAttribute("reportList", emplist);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}
	

	


	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}


	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		
		this.request = request;
	}

}

