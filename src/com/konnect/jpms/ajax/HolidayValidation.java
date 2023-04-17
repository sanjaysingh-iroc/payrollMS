package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class HolidayValidation extends ActionSupport implements ServletRequestAware, IStatements {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	private String holidayDate;
	private String orgId;
	private String strWlocation;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		  
		if (getHolidayDate() == null || getHolidayDate().trim().equals("") || getHolidayDate().trim().equalsIgnoreCase("NULL")) {
			request.setAttribute("STATUS_MSG","Please select the proper date.");
			return SUCCESS;
		} 
		
		validateCode();
		
		return SUCCESS;
	}


	public void validateCode(){
		
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		boolean isExist = false;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM holidays WHERE _date=? and org_id=? and wlocation_id=?");			
			pst.setDate(1, uF.getDateFormat(getHolidayDate(), DATE_FORMAT));
			pst.setInt(2, uF.parseToInt(getOrgId()));
			pst.setInt(3, uF.parseToInt(getStrWlocation()));
			rs = pst.executeQuery();
//			System.out.println("pst=="+pst);
			while(rs.next()){
				isExist = true;
			}
            rs.close();
            pst.close();
			
            if(isExist){
            	request.setAttribute("STATUS_MSG","Sorry You have already added holiday for this date.");
            }
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String getHolidayDate() {
		return holidayDate;
	}

	public void setHolidayDate(String holidayDate) {
		this.holidayDate = holidayDate;
	}
	
	public String getOrgId() {
		return orgId;
	}


	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}


	public String getStrWlocation() {
		return strWlocation;
	}


	public void setStrWlocation(String strWlocation) {
		this.strWlocation = strWlocation;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
}
