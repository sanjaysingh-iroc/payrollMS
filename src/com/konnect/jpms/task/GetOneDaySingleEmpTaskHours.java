package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetOneDaySingleEmpTaskHours extends ActionSupport implements ServletRequestAware, ServletResponseAware,IStatements {
	private static final long serialVersionUID = 1L;
	
	/*Map session;*/
	HttpSession session;
	
	CommonFunctions CF;
	private HttpServletRequest request;
	
	String strDate;
	String strTime;
	String empId;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		empId = (String)session.getAttribute(EMPID);
		UtilityFunctions uF = new UtilityFunctions();
		getOneDaySingleEmpTaskHours(uF);
		
	return SUCCESS;
	}
	
	
	private void getOneDaySingleEmpTaskHours(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
	
		try {
			con = db.makeConnection(con);
				pst = con.prepareStatement("select sum(actual_hrs) as actual_hrs from task_activity WHERE emp_id =? and task_date = ?");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setDate(2, uF.getDateFormat(getStrDate(), DATE_FORMAT));
//				System.out.println("pst ==>>> " + pst);
				String totHrs = "0";
				rs = pst.executeQuery();
				while (rs.next()) {
					totHrs = rs.getString("actual_hrs");
				}
				double dblTotHrs = uF.parseToDouble(getStrTime()) + uF.parseToDouble(totHrs);
				request.setAttribute("TotalHrs", dblTotHrs);
				rs.close();
				pst.close();
//				System.out.println("dblTotHrs ===>> " + dblTotHrs);
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public String getStrDate() {
		return strDate;
	}
	
	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}
	
	public String getStrTime() {
		return strTime;
	}
	
	public void setStrTime(String strTime) {
		this.strTime = strTime;
	}
	
	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}


	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
}
