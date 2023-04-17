package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillRimbursementType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetTravelPlanDetails extends ActionSupport implements IStatements, ServletRequestAware {

	private static final long serialVersionUID = 6483180990145887248L;


	HttpSession session;
	private CommonFunctions CF;
	
	String empId;	
	String travelId;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		getTravelPlanDetails(uF);
		
		return SUCCESS;		
	}

	private void getTravelPlanDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			double dblNoOfDays = 0.0d;
			pst = con.prepareStatement("select * from emp_leave_entry where emp_id=? and leave_id=? and istravel = true ");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(getTravelId()));
			rs = pst.executeQuery();
			while(rs.next()){
				dblNoOfDays = rs.getDouble("emp_no_of_leave");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("STATUS_MSG",""+dblNoOfDays);
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getTravelId() {
		return travelId;
	}

	public void setTravelId(String travelId) {
		this.travelId = travelId;
	}
	
}
