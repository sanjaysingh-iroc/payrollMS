package com.konnect.jpms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>
 * Validate a user login.
 * </p>
 */
public class SubmitTicket extends ActionSupport implements IStatements,ServletRequestAware 
{
  
	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	
	private static Logger log = Logger.getLogger(SubmitTicket.class);
	  
	
	public String execute() throws Exception {
		session = request.getSession(true);
		
		
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
			
		
		
		if(getStrName()!=null){
			
			request.setAttribute(PAGE, "/jsp/common/SubmitTicketThankYou.jsp");
			request.setAttribute(TITLE, "Thank you");
			
			UtilityFunctions uF = new UtilityFunctions();
			submitTicket(uF);
			return SUCCESS;
		}
		return LOAD;
  
	}
	
	String strContactNo;
	String strName;
	String ticketTopic;
	String strQuery;
	
	

	public void submitTicket(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		
		try{
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("insert into support_ticket (name, contact_no, topic, query, entry_time, product_id) values (?,?,?,?,?,?)");
			pst.setString(1, getStrName());
			pst.setString(2, getStrContactNo());
			pst.setString(3, getTicketTopic());
			pst.setString(4, getStrQuery());
			pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+" "+uF.getCurrentTime(CF.getStrTimeZone()), DBTIMESTAMP));
			pst.setInt(6, 6); // 6 = Kirtane & Pandit
		
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Your ticket has been submitted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}
	
	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	public String getStrContactNo() {
		return strContactNo;
	}
	public void setStrContactNo(String strContactNo) {
		this.strContactNo = strContactNo;
	}
	public String getStrName() {
		return strName;
	}
	public void setStrName(String strName) {
		this.strName = strName;
	}
	public String getTicketTopic() {
		return ticketTopic;
	}
	public void setTicketTopic(String ticketTopic) {
		this.ticketTopic = ticketTopic;
	}
	public String getStrQuery() {
		return strQuery;
	}
	public void setStrQuery(String strQuery) {
		this.strQuery = strQuery;
	}
}