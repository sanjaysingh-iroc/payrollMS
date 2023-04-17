package com.konnect.jpms.common;

import java.rmi.server.Operation;
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
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateCompanyManualStatus extends ActionSupport  implements ServletRequestAware, IStatements {
		
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	 
	private CommonFunctions CF;
	
	String orgId;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() {
	
		session = request.getSession();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(USERTYPE);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
	
		String strManualId = (String)request.getParameter("MID");
		String orgId = (String)request.getParameter("orgId");
		String pageFrom =  (String)request.getParameter("pageFrom");
		String operation =  (String)request.getParameter("operation");
		
		updateStatus(strManualId, orgId, operation);
		
		if(pageFrom!=null && pageFrom.equals("MyHub")) {
			return SUCCESS;
		} else {
			return LOAD; 
		}
	}
	
	
	public int updateStatus(String strManualId,String orgId, String operation) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int nRequisitionId=0;
		
		try {
			con = db.makeConnection(con);
//			pst = con.prepareStatement("update company_manual set status = 2 where status = 1  and org_id=?");
//			pst.setInt(1, uF.parseToInt(orgId));
//			pst.execute();
//			pst.close();
			
			pst = con.prepareStatement("update company_manual set status=?, _date=? where manual_id=?");
			if(operation != null && operation.equals("UP")) {
				pst.setInt(1, 2);
			} else {
				pst.setInt(1, 1);
			}
			pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()),DBDATE+DBTIME) );
			pst.setInt(3, uF.parseToInt(strManualId));
		//	System.out.println("pst2==>"+pst);
			pst.execute();
			pst.close();
			
			
			if(operation == null || !operation.equals("UP")) {
				pst = con.prepareStatement("select org_id from company_manual where manual_id=?");
				pst.setInt(1, uF.parseToInt(strManualId));
				rs = pst.executeQuery();
				String strOrgId = null;
				while(rs.next()) {
					strOrgId = rs.getString("org_id");
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select emp_per_id from employee_official_details eod, user_details ud, employee_personal_details epd " +
					" where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and eod.org_id=?");
				pst.setInt(1, uF.parseToInt(strOrgId));
				rs = pst.executeQuery();
				List<String> empList = new ArrayList<String>();
				while(rs.next()) {
					if(!empList.contains(rs.getString("emp_per_id").trim())) {
						empList.add(rs.getString("emp_per_id").trim());	
					}
				}
				rs.close();
				pst.close();
				
				String strDomain = request.getServerName().split("\\.")[0];
				for(int i=0; empList!= null && !empList.isEmpty() && i<empList.size(); i++) {
					if(!empList.get(i).equals("") && uF.parseToInt(empList.get(i)) > 0) {
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(empList.get(i));
						userAlerts.set_type(NEW_MANUAL_ALERT);
						userAlerts.setStatus(INSERT_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
					}
				}
			}
			request.setAttribute("STATUS_MSG", "Published");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return nRequisitionId;
	}
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}


	public String getUserscreen() {
		return userscreen;
	}


	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}


	public String getNavigationId() {
		return navigationId;
	}


	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}


	public String getToPage() {
		return toPage;
	}


	public void setToPage(String toPage) {
		this.toPage = toPage;
	}
	
}