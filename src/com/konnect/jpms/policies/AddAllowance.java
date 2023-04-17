package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.reports.SearchByServices;
import com.konnect.jpms.select.FillDesignation;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddAllowance extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	String strReqEmpId = null;

	private static Logger log = Logger.getLogger(AddAllowance.class);
	public String execute() {
		
		session = request.getSession();
		request.setAttribute(PAGE, PAddAllowance);
		strEmpId = (String)session.getAttribute("EMPID");
		strReqEmpId = (String)request.getParameter("EMPID");
		
		String referer = request.getHeader("Referer");

		if (referer != null) {
			int index1 = referer.indexOf(request.getContextPath());
			int index2 = request.getContextPath().length();
			referer = referer.substring(index1 + index2 + 1);
		}
		setRedirectUrl(referer);
		
		String operation = request.getParameter("operation");
		
		if(operation.equals("A"))
		{
			insertAllowance();
			if(getEmpID()!=null && getEmpID().length()>0){
				session.setAttribute("ALLOWANCE", "---");
				return "previous";
			}
		}
		else if (operation.equals("U"))
		{
			return updateAllowance();
		}
		else if (operation.equals("D"))
		{
			return deleteAllowance();
		}
		return SUCCESS;
		
	}

	public String loadValidateAllowance() {
		request.setAttribute(PAGE, PAddAllowance);
		request.setAttribute(TITLE, TAddAllowance);
		
		desigList = new FillDesignation(request).fillDesignation();

		if(strReqEmpId!=null){
			setDesignation(strReqEmpId);
			setEmpID(strReqEmpId);	
			
			log.debug("getEmpID 1 ="+getEmpID());
			log.debug("getEmpID 1 ="+getEmpID());
			
		}else if(getEmpID()!=null && getEmpID().length()>0){
			setDesignation(getEmpID());
			setEmpID(getEmpID());
			
			log.debug("getEmpID 2 ="+getEmpID());
			log.debug("getEmpID 2 ="+getEmpID());
			
		}else{
			
			log.debug("getEmpID 3 ="+getEmpID());
			log.debug("getEmpID 3 ="+getEmpID());
			
//			setDesignation(null);
//			setEmpID(null);
		}
		
		return LOAD;
	}
	

	public String updateAllowance() {

		Connection con = null;
		PreparedStatement pst =null;;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		setAllowanceId(request.getParameter("id"));
		int columnId = Integer.parseInt(request.getParameter("columnId"));
		String columnName=null;
		
		switch(columnId) {
			case 0 : columnName = "hours_completed"; break;
			case 1 : columnName = "allowance_value"; break;
			case 2 : columnName = "allowance_type"; break;
			case 3 : columnName = "desig_id"; break;
		}
		String updateAllowance = "UPDATE allowance SET "+columnName+"=? WHERE allowance_id=?";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateAllowance);
			if(columnId==1)
				pst.setDouble(1, uF.parseToDouble(request.getParameter("value")));
			else if(columnId==2)
				pst.setString(1, request.getParameter("value"));
			else if (columnId==0 || columnId==3)
				pst.setInt(1, uF.parseToInt(request.getParameter("value")));
			
			pst.setInt(2, uF.parseToInt(request.getParameter("id")));
			log.debug("pst to update the allowance"+pst);
			int cnt = pst.executeUpdate();
			pst.close();
			log.debug("update cnt="+cnt);
			log.debug("updateAllowance ="+pst);

		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;

	}
	
	public String insertAllowance() {

		Connection con = null;
		PreparedStatement pst =null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(insertAllowance);
			pst.setInt(1, uF.parseToInt(getAllowanceHours()));
			pst.setDouble(2, uF.parseToDouble(getAllowanceAmount()));
			pst.setString(3," ");
			pst.setInt(4, uF.parseToInt(getDesignation()));
			log.debug("to insert the allowance"+pst);
			pst.execute();
			pst.close();
			request.setAttribute(MESSAGE, "Allowance added successfully!");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String deleteAllowance() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteAllowance);
			pst.setInt(1, uF.parseToInt(request.getParameter("id")));
			pst.execute();
			pst.close();
			request.setAttribute(MESSAGE, "Allowance deleted successfully!");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	public void validate() {
		UtilityFunctions uF = new UtilityFunctions();

		if (getDesignation() != null && uF.parseToInt(getDesignation()) == 0) {
			addFieldError("empId", "Please select employee from the list");
		}

		if (getAllowanceHours() != null && getAllowanceHours().length() == 0) {
			addFieldError("allowanceHours", "Allowance hours is required");
		} else if (getAllowanceHours() != null && !uF.isNumber(getAllowanceHours())) {
			addFieldError("allowanceHours", "Allowance hours should be in numbers only.");
		}
		if (getAllowanceAmount() != null && getAllowanceAmount().length() == 0) {
			addFieldError("allowanceAmount", "Allowance amount is required");
		} else if (getAllowanceAmount() != null && !uF.isNumber(getAllowanceAmount())) {
			addFieldError("allowanceAmount", "Allowance amount should be in numbers only.");
		}
		loadValidateAllowance();
	}
	
	String allowanceId;
	String allowanceHours;
	String allowanceAmount;
	String designation;
	String redirectUrl;
	String empID;
	List<FillDesignation> desigList;
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getAllowanceId() {
		return allowanceId;
	}

	public void setAllowanceId(String allowanceId) {
		this.allowanceId = allowanceId;
	}

	public String getAllowanceHours() {
		return allowanceHours;
	}

	public void setAllowanceHours(String allowanceHours) {
		this.allowanceHours = allowanceHours;
	}

	public String getAllowanceAmount() {
		return allowanceAmount;
	}

	public void setAllowanceAmount(String allowanceAmount) {
		this.allowanceAmount = allowanceAmount;
	}



	public List<FillDesignation> getDesigList() {
		return desigList;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

}
