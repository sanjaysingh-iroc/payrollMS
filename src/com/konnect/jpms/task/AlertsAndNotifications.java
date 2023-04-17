package com.konnect.jpms.task;

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

public class AlertsAndNotifications extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	HttpSession session;
	String strUserType;
	String strSessionEmpId; 
	
	String operation;
	
	public String execute() throws Exception {
		session = request.getSession();		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/task/AlertsAndNotifications.jsp");
		
		UtilityFunctions uF = new UtilityFunctions();
		if(getOperation() != null && getOperation().equals("NA")){
			getNewsAndAlerts(uF);
			request.setAttribute(TITLE, "News And Alerts");
		}else{
			request.setAttribute(TITLE, "Notifications");
		}
		
//		System.out.println("operation==>"+ operation);
		getAllNotifications(uF);
		
		if(getOperation() != null && getOperation().equals("RAN")){
			deleteAllNotifications(uF,operation);
		}
		return SUCCESS; 

	}

	
	private void deleteAllNotifications(UtilityFunctions uF,String operation) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			//if(operation.equals("RAN")){
				sbQuery.append("delete from taskrig_user_alerts ");
				if(strUserType != null && strUserType.equalsIgnoreCase(CUSTOMER)){
					sbQuery.append("where customer_id=?");
				} else {
					sbQuery.append("where resource_id=?");
				}
				pst = con.prepareStatement(sbQuery.toString());			
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
			//}
			/*else if(operation.equals("DNA")){
				sbQuery.append("delete from taskrig_user_alerts where type= ? and resource_id =? ");
				pst = con.prepareStatement(sbQuery.toString());			
				pst.setString(1, "NA");
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
			}*/
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getAllNotifications(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from taskrig_user_alerts where type is null");
			if(strUserType != null && strUserType.equalsIgnoreCase(CUSTOMER)){
				sbQuery.append(" and customer_id=?");
			} else {
				sbQuery.append(" and resource_id=?");
			}
			sbQuery.append(" order by alerts_id desc");
			 pst = con.prepareStatement(sbQuery.toString());			
			 pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			 System.out.println("pst ===> " +pst);
			 rs = pst.executeQuery();
			 StringBuilder sbAllNoti = new StringBuilder();
			 int notiCnt = 0;
			 while (rs.next()) {
				 if(rs.getString("alert_data") != null && !rs.getString("alert_data").equals("null") && !rs.getString("alert_data").equals("") && 
					rs.getString("alert_action") != null && !rs.getString("alert_action").equals("null") && !rs.getString("alert_action").equals("")) {
						sbAllNoti.append("<div style=\"float: left; width: 99%; border: 1px solid #CCCCCC; border-radius: 4px; margin: 2px 0px; padding: 5px;\"");
						sbAllNoti.append("onmouseover=\"javascript:this.style.backgroundColor='#D4D4D4'\" onmouseout=\"javascript:this.style.backgroundColor=''\" >");
						sbAllNoti.append("<a href=\""+rs.getString("alert_action")+"&alertID="+rs.getString("alerts_id")+"\" style=\"color: black; font-weight: normal; width: 100%;\">");
						sbAllNoti.append(rs.getString("alert_data"));
						sbAllNoti.append("</a>");
						sbAllNoti.append("</div>");
						notiCnt++;
				 }
			 }
			rs.close();
			pst.close();
//			 System.out.println("notiCnt ===>> " + notiCnt);
			 request.setAttribute("notiCnt", notiCnt+"");
			 request.setAttribute("sbAllNoti", sbAllNoti.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getNewsAndAlerts(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from taskrig_user_alerts where resource_id = ? and type = ? order by alerts_id desc");
			 pst = con.prepareStatement(sbQuery.toString());			
			 pst.setInt(1, uF.parseToInt(strSessionEmpId));
			 pst.setString(2,"NA");
			 rs = pst.executeQuery();
			 StringBuilder sbAllNoti = new StringBuilder();
			 int notiCnt = 0;
			 while (rs.next()) {
				 if(rs.getString("alert_data") != null && !rs.getString("alert_data").equals("null") && !rs.getString("alert_data").equals("") && 
					rs.getString("alert_action") != null && !rs.getString("alert_action").equals("null") && !rs.getString("alert_action").equals("")) {
						sbAllNoti.append("<div style=\"float: left; width: 99%; border: 1px solid #CCCCCC; border-radius: 4px; margin: 2px 0px; padding: 5px;\"");
						sbAllNoti.append("onmouseover=\"javascript:this.style.backgroundColor='#D4D4D4'\" onmouseout=\"javascript:this.style.backgroundColor=''\" >");
						sbAllNoti.append("<a href=\""+rs.getString("alert_action")+"?alertID="+rs.getString("alerts_id")+"\" style=\"color: black; font-weight: normal; width: 100%;\">");
						sbAllNoti.append(rs.getString("alert_data"));
						sbAllNoti.append("</a>");
						sbAllNoti.append("</div>");
						notiCnt++;
				 }
			 }
			rs.close();
			pst.close();
			System.out.println("newsCount");
			request.setAttribute("newsCount", notiCnt);
			request.setAttribute("newsAndalerts", sbAllNoti.toString());
			
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


	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}


	
}