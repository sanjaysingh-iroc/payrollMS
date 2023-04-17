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

public class UpdateInvestment extends ActionSupport implements IStatements, ServletRequestAware{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String strSessionEmpId;
	HttpSession session;
	CommonFunctions CF;
	
	String investment_id;
	String status;
	
	public String execute() throws Exception {
		 
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		updateInvestment();
		return SUCCESS;
	
	}
	
	public void updateInvestment(){

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs=null;
		try {

			con = db.makeConnection(con);
			
			/*if(uF.parseToBoolean(getStatus())){
				pst = con.prepareStatement("update investment_details set status=?, approved_by=?, approved_date=? where investment_id = ?");
				pst.setBoolean(1, uF.parseToBoolean(getStatus()));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.setInt(4, uF.parseToInt(getInvestment_id()));
				pst.execute();
			}else{
				pst = con.prepareStatement("update investment_details set status=?, denied_by=?, denied_date=? where investment_id = ?");
				pst.setBoolean(1, uF.parseToBoolean(getStatus()));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.setInt(4, uF.parseToInt(getInvestment_id()));
				pst.execute();
				
			} */
			
			if(uF.parseToBoolean(getStatus())){
				pst = con.prepareStatement("update investment_details set status=?, approved_by=?, approved_date=? where investment_id in ("+getInvestment_id()+")");
				pst.setBoolean(1, uF.parseToBoolean(getStatus()));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
//				System.out.println("pst=====>"+pst);
				pst.execute();
	            pst.close();
			}else{
				pst = con.prepareStatement("update investment_details set status=?, denied_by=?, denied_date=? where investment_id in ("+getInvestment_id()+")");
				pst.setBoolean(1, uF.parseToBoolean(getStatus()));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
//				System.out.println("pst=====>"+pst);
				pst.execute();
	            pst.close();
			} 
			
			if(uF.parseToBoolean(getStatus())){
				request.setAttribute("STATUS_MSG", "<font size=\"1\" color=\"green\">Approved</font>");
			}else{
				request.setAttribute("STATUS_MSG", "<font size=\"1\" color=\"red\">Disapproved</font>");
			}
			
//			String[] temp=getInvestment_id().split(",");
//			
//			pst = con.prepareStatement("select id.emp_id,id.amount_paid,sd.section_code from investment_details id,section_details sd where id.investment_id = ? and id.section_id=sd.section_id");
//			pst.setInt(1, uF.parseToInt(temp[0]));
//			rs=pst.executeQuery();
//			String empId=null;
//			String strAmount = null;
//			String sectionCode=null;
//			while(rs.next()){
//				empId=rs.getString("emp_id");
//				strAmount=rs.getString("amount_paid");
//				sectionCode=rs.getString("section_code");
//			}
//			
//			String strDomain = request.getServerName().split("\\.")[0];
//			Notifications nF = new Notifications(N_EMPLOYEE_INVESTMENT_APPROVAL, CF); 
//			nF.setDomain(strDomain);
//			nF.request = request;
//			nF.setStrEmpId(empId);			
//			nF.setStrSectionCode(sectionCode);
//			nF.setStrInvestmentAmount(strAmount);
//			if(uF.parseToBoolean(getStatus())){
//				nF.setStrApprvedDenied("approved");
//			}else{
//				nF.setStrApprvedDenied("denied");
//			}
//			nF.setStrHostAddress(request.getRemoteHost());
//			nF.setStrContextPath(request.getContextPath());
//			nF.setStrHostAddress(CF.getStrEmailLocalHost());
//			nF.setStrHostPort(CF.getStrHostPort());
//			nF.setStrContextPath(request.getContextPath());
//			nF.setEmailTemplate(true);
//			nF.sendNotifications();   

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getInvestment_id() {
		return investment_id;
	}

	public void setInvestment_id(String investment_id) {
		this.investment_id = investment_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


}