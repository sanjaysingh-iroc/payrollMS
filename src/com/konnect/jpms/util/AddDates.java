package com.konnect.jpms.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionSupport;

public class AddDates  extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	CommonFunctions CF;
	HttpSession session;
	String strUserType = null;
	
	private static Logger log = Logger.getLogger(AddDates.class);
	public String execute() throws Exception {
		request.setAttribute(PAGE, PAddDates); 
		 
		session = request.getSession();
		strUserType = (String)session.getAttribute(USERTYPE);
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		
		
		if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)){
			
			if(getStrNoOfDays()!=null && getStrNoOfDays().length()>0){
				if(getStrPassword()!=null && getStrPassword().equals("K0nnect")){
					addDate();
				}else{
					request.setAttribute("MESSAGE", "Wrong Security Password!");
				}
			}else{
				getMaxDate();
			}
		}else{
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		
		loadDate();
			
		
		return SUCCESS;
		
	}
	
	
	public void loadDate() {
		setStrNoOfDays(null);
		setStrPassword(null);
	}
	
	String strNoOfDays;
	String strPassword;
	
	
	public String addDate() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			
			
			con = db.makeConnection(con);	
			
			pst = con.prepareStatement(selectAllDates);
			rs = pst.executeQuery();
			String strMaxDate = "2009-12-31";
			
			while(rs.next()){
				if(rs.getString("_date")!=null){
					strMaxDate = rs.getString("_date");
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("strMaxDate==>"+strMaxDate);
			request.setAttribute("MESSAGE", "Existing max date "+strMaxDate);
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strMaxDate, DBDATE, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strMaxDate, DBDATE, "MM"))-1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strMaxDate, DBDATE, "yyyy")));
			
			
			int nNoOfDays = uF.parseToInt(getStrNoOfDays());
			
			
			pst = con.prepareStatement(insertAllDates);
			String strTemp = null;
			for(int i=0; i<nNoOfDays; i++){
				cal.add(Calendar.DATE, 1);
				strTemp = cal.get(Calendar.YEAR)+"-"+((cal.get(Calendar.MONTH)+1<10)?"0"+(cal.get(Calendar.MONTH)+1):cal.get(Calendar.MONTH)+1)+"-"+((cal.get(Calendar.DATE)<10)?"0"+cal.get(Calendar.DATE):cal.get(Calendar.DATE));
				pst.setDate(1, uF.getDateFormat(strTemp, DBDATE));
				pst.addBatch();
				
				
				request.setAttribute("MESSAGE", "Dates added till "+uF.getDateFormat(strTemp, DBDATE, DATE_FORMAT));
			}
			pst.executeBatch();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeConnection(con);
			db.closeStatements(pst);
		}
		return SUCCESS; 
	}
	
	
	public String getMaxDate() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);   
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			con = db.makeConnection(con);	
			pst = con.prepareStatement(selectAllDates);
			rs = pst.executeQuery();
			String strMaxDate = "2010-01-01";
			
			while(rs.next()){
				if(rs.getString("_date")!=null){
					strMaxDate = rs.getString("_date");
				}
			}
			
			request.setAttribute("MESSAGE", "Existing max date "+uF.getDateFormat(strMaxDate, DBDATE, DATE_FORMAT));
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeConnection(con);
			db.closeStatements(pst);
		}
		return SUCCESS; 
	}
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrNoOfDays() {
		return strNoOfDays;
	}

	public void setStrNoOfDays(String strNoOfDays) {
		this.strNoOfDays = strNoOfDays;
	}

	public String getStrPassword() {
		return strPassword;
	}

	public void setStrPassword(String strPassword) {
		this.strPassword = strPassword;
	}
}
