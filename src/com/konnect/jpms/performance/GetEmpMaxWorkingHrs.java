package com.konnect.jpms.performance;

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
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetEmpMaxWorkingHrs extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -4581306410273940249L;
	HttpSession session;
	String strSessionEmpId;
	String strEmpOrgId;
//	String strSessionUserType;
	String strUserTypeId;
	
	CommonFunctions CF;
	
	String strUserType;
	private String hrs;
	private String empselected;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
//		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strUserType = (String) session.getAttribute(USERTYPE);
		
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return "login";
		
//		System.out.println("getSelectedEmps==>"+getEmpselected()+"==>hrs==>"+getHrs());
				
		UtilityFunctions uF = new UtilityFunctions();
		getEmpMaxWorkingHrs(uF);
		return LOAD;
	}
	
	private void getEmpMaxWorkingHrs(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			double maxHrs = 0;
			Map<String, String> hmWorkingHrsByLocation = CF.getWorkLocationWorkingHrs(con);
			Map<String, String> hmEmpWorkingHrs = new HashMap<String, String>();
			
			if(getEmpselected() != null && !getEmpselected().equals("")) {
				char lastChar = getEmpselected().charAt(getEmpselected().length()-1);
				if(lastChar == ',') {
					setEmpselected(getEmpselected().substring(0,getEmpselected().lastIndexOf(',')));
				}
				StringBuilder sbQuery=new StringBuilder();
				sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
					" and eod.emp_id in ("+getEmpselected()+") ");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
						String empWorkingHrs = hmWorkingHrsByLocation.get(rs.getString("wlocation_id"));
						if(empWorkingHrs != null && !empWorkingHrs.equals("") && uF.parseToDouble(empWorkingHrs) > maxHrs) {
							maxHrs = uF.parseToDouble(empWorkingHrs);
						}
				}
				rs.close();
				pst.close();
		    }
			
			if(maxHrs == 0) {
				maxHrs = 8.0;
			}

			int flag = 0;
			
			if(maxHrs <= uF.parseToDouble(getHrs())) {
				flag = 1;
			}
			
			request.setAttribute("flag", ""+flag);

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
		this.request = request;
	}

	public String getHrs() {
		return hrs;
	}

	public void setHrs(String hrs) {
		this.hrs = hrs;
	}

	public String getEmpselected() {
		return empselected;
	}

	public void setEmpselected(String empselected) {
		this.empselected = empselected;
	}
	
}
