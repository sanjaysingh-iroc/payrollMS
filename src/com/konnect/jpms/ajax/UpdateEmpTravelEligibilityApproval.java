package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateEmpTravelEligibilityApproval extends ActionSupport implements IStatements, ServletRequestAware{

	String strSessionEmpId;
	HttpSession session;
	CommonFunctions CF;
	
	public String execute() throws Exception {
		 
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		
		
		updateEmpTravelEligibilityApproval();
		
		return SUCCESS;
	
	}

	
	String strEmpId;
	String strEffectiveDate;
	String strEntryDate;
	String status;
	
	public void updateEmpTravelEligibilityApproval(){

		Connection con = null;
		PreparedStatement pst = null, pst1 = null, pst2 = null;		
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			
			
			String count = request.getParameter("count");
			String emp_id = request.getParameter("emp_id");
			
			
			pst = con.prepareStatement("update travel_advance_eligibility set is_eligible = ?, approved_by=?, approved_date=? where emp_id=?");
			pst.setBoolean(1, uF.parseToBoolean(request.getParameter("status")));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt(request.getParameter("emp_id")));
			int x = pst.executeUpdate();
            pst.close();
			
			if(x==0){
				pst = con.prepareStatement("insert into travel_advance_eligibility (is_eligible, approved_by, approved_date, emp_id) values (?,?,?,?)");
				pst.setBoolean(1, uF.parseToBoolean(request.getParameter("status")));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4, uF.parseToInt(request.getParameter("emp_id")));
				pst.execute();
	            pst.close();
			}
			
			
			
			if(uF.parseToBoolean(request.getParameter("status"))){
				/*request.setAttribute("STATUS_MSG", "<div id=\"myDiv_"+count+"\"><a href=\"javascript:void(0)\" onclick=\"confirm('Are you sure you want to deny this?')?getContent('myDiv_"+count+"','UpdateEmpTravelEligibilityApproval.action?status=0&emp_id="+emp_id+"&count="+count+"'):''\"><img src=\"images1/tick.png\" ></div>");*/
				request.setAttribute("STATUS_MSG", "<div id=\"myDiv_"+count+"\"><a href=\"javascript:void(0)\" onclick=\"confirm('Are you sure you want to deny this?')?getContent('myDiv_"+count+"','UpdateEmpTravelEligibilityApproval.action?status=0&emp_id="+emp_id+"&count="+count+"'):''\"><i class=\"fa fa-check checknew\" aria-hidden=\"true\"></i></div>");
			}else{
				/*request.setAttribute("STATUS_MSG", "<div id=\"myDiv_"+count+"\"><a href=\"javascript:void(0)\" onclick=\"confirm('Are you sure you want to approve this?')?getContent('myDiv_"+count+"','UpdateEmpTravelEligibilityApproval.action?status=1&emp_id="+emp_id+"&count="+count+"'):''\"><img src=\"images1/cross.png\" ></div>");*/
				request.setAttribute("STATUS_MSG", "<div id=\"myDiv_"+count+"\"><a href=\"javascript:void(0)\" onclick=\"confirm('Are you sure you want to approve this?')?getContent('myDiv_"+count+"','UpdateEmpTravelEligibilityApproval.action?status=1&emp_id="+emp_id+"&count="+count+"'):''\"><i class=\"fa fa-times cross\" aria-hidden=\"true\"></i></div>");
			}
			
    

		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeStatements(pst2);
			db.closeConnection(con);
		}

		
		
	}
	
	
	
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStrEffectiveDate() {
		return strEffectiveDate;
	}

	public void setStrEffectiveDate(String strEffectiveDate) {
		this.strEffectiveDate = strEffectiveDate;
	}

	public String getStrEntryDate() {
		return strEntryDate;
	}

	public void setStrEntryDate(String strEntryDate) {
		this.strEntryDate = strEntryDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}