package com.konnect.jpms.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.konnect.jpms.common.Login;
import com.konnect.jpms.employee.Dashboard;

public class CheckLogin implements Filter, IStatements {

//	private String username;
//	private String password;
//	HttpServletRequest httpReq;

	private static Logger log = Logger.getLogger(CheckLogin.class);
	
	@Override
	public void destroy() {

	} 
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
 
//		httpReq = (HttpServletRequest) req;
		HttpServletRequest httpReq = (HttpServletRequest) req;
 
		HttpServletResponse httpRes = (HttpServletResponse) res;

		// check if session already exists
		HttpSession session = httpReq.getSession(false);
//		HttpSession session = httpReq.getSession(); 
		
		if (session == null) {
//			System.out.println("session null");
			log.debug("Session Not Found");
			
			String strAction = httpReq.getServletPath();
			
			if(strAction!=null){
				strAction = strAction.replace("/","");
			}
			
			
			if(strAction.equalsIgnoreCase("AddEmployee.action")) {
//				System.out.println("if");
				Map<String, String[]> mp = httpReq.getParameterMap();
				String[] empId = (String[]) mp.get("empId");

				if(empId!=null && empId.length > 0) {
					
					Login login = new Login();
					login.setServletRequest(httpReq);
					login.setEmpId(empId[0]);
					
					try {
						
						if(login.execute().equals("notapproved")) {
							chain.doFilter(req, res);
						}else if(login.execute().equals("login")) {
							chain.doFilter(req, res);
						}else {
							log.debug("90");
							//chain.doFilter(req, res);
						}
						
						} catch (Exception e) {
							e.printStackTrace();
						}
					
				}
			}else {
//				System.out.println("else");
				Map<String, String> hmUserInfo = new HashMap<String, String>();
				if (isUserRemembered(httpReq, httpRes, hmUserInfo)) {
//					System.out.println("else if ");
					// pass username & password to Login Page!!
					Login login = new Login();
					login.setServletRequest(httpReq);
					login.setUsername(hmUserInfo.get("PAYROLL_USERNAME"));
					login.setPassword(hmUserInfo.get("PAYROLL_PASSWORD"));
					login.setLoginType(hmUserInfo.get("PAYROLL_LOGIN_TYPE"));
//					System.out.println("PAYROLL_USERNAME=====>"+hmUserInfo.get("PAYROLL_USERNAME"));
//					System.out.println("PAYROLL_PASSWORD=====>"+hmUserInfo.get("PAYROLL_PASSWORD"));
//					System.out.println("PAYROLL_LOGIN_TYPE=====>"+hmUserInfo.get("PAYROLL_LOGIN_TYPE"));
					try {
							if(login.execute().equals(DASHBOARD) || login.execute().equals(MYHOME)) {
//								System.out.println("else if if ");
								Dashboard db = new Dashboard();
								db.setServletRequest(httpReq);
								
								if(db.execute().equals(LOAD)) {
//									System.out.println("else if else");
									chain.doFilter(req, res);
								}
							}
						
						} catch (Exception e) {
							e.printStackTrace();
						}
					
					}else {
//						System.out.println("else else ");
					//Show a login page here after creating a new session"
					session = httpReq.getSession(true);
					chain.doFilter(req, res);
				}
				 	
				
				
				/*
				session = httpReq.getSession(true);
				
				System.out.println("session== 1 ====++>"+session);
				
				
				String str = isUserRemembered(httpReq, httpRes, session);
				
				if(str!=null && str.equals(DASHBOARD)) {
					Dashboard db = new Dashboard();
					db.setServletRequest(httpReq);
				}else {
					session = httpReq.getSession(true);
					chain.doFilter(req, res);
				}
				*/
			}
		
		} else {
//			System.out.println("session not null");
			//session already exists..
			
//				new MailCountClass().getMailCount(httpReq);
			if(chain!=null){
				chain.doFilter(httpReq, httpRes);
			}
		}
	}

	
	
	
	public void isUserRemembered() {
		
		
	}
	
	public boolean isUserRemembered(HttpServletRequest req,
			HttpServletResponse res, Map<String, String> hmUserInfo) {

		// Code to check if user is remembered
		Cookie cookies[] = req.getCookies();
		String cookieValue = "";
		Database db = new Database();
		db.setRequest(req);
		PreparedStatement pst = null;
		ResultSet rst = null;
		Connection con = null;
		String str = null;
		if (cookies != null) {

			String strUserName = null;
			String strPassword = null;
			String strLoginType = null;
			
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals("PAYROLL_USERNAME")) {
//					setUsername(cookies[i].getValue());
					strUserName = cookies[i].getValue();
					hmUserInfo.put("PAYROLL_USERNAME", strUserName);
				}
				if (cookies[i].getName().equals("PAYROLL_PASSWORD")) {
//					setPassword(cookies[i].getValue());
					strPassword = cookies[i].getValue();
					hmUserInfo.put("PAYROLL_PASSWORD", strPassword);
				}
				if (cookies[i].getName().equals("PAYROLL_LOGIN_TYPE")) {
//					setPassword(cookies[i].getValue());
					strLoginType = cookies[i].getValue();
					hmUserInfo.put("PAYROLL_LOGIN_TYPE", strLoginType);
				}
			}
			
			//Check username password against database..
			
			
			try {
				
				/*
				Login objLogin = new Login();
				objLogin.setServletRequest(req);
				objLogin.setServletResponse(res);
				objLogin.setUsername(getUsername());
				objLogin.setPassword(getPassword());
				str = objLogin.validateUser(session);
				*/
				
				con = db.makeConnection(con);
//				pst = con.prepareStatement(CheckUserExists);
				if(strLoginType!=null && strLoginType.equals("1")){
					pst = con.prepareStatement(selectUser);
					pst.setString(1, (strUserName!=null)?strUserName.toUpperCase():strUserName);
					pst.setString(2, strPassword);
//					System.out.println("pst=====>"+pst);
					rst = pst.executeQuery();
					while(rst.next()) {
						log.debug("User Exists!!");
						return true;
					}
					rst.close();
					pst.close();
				} else if(strLoginType!=null && strLoginType.equals("2")){
					pst = con.prepareStatement("SELECT * FROM user_details_customer udc, user_type ut, client_poc cp WHERE ut.user_type_id = udc.usertype_id and udc.emp_id = cp.poc_id and upper(username)=? and password=? and udc.status = 'ACTIVE'");
					pst.setString(1, (strUserName!=null)?strUserName.toUpperCase():strUserName);
					pst.setString(2, strPassword);
//					System.out.println("pst=====>"+pst);
					rst = pst.executeQuery();
					while(rst.next()) {
						log.debug("User Exists!!");
						return true;
					}
					rst.close();
					pst.close();
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				db.closeResultSet(rst);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
		} else
			log.debug("No cookies Found!");

		return false;
	}  
	
	
	/*public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}*/
	
//	public void setHttpReq(HttpServletRequest httpReq) {
//		this.httpReq = httpReq;
//	}
	private ServletContext servletContext;
	@Override
	public void init(FilterConfig config) throws ServletException {
		servletContext = config.getServletContext();
	}
}

