package com.konnect.jpms.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class CheckDomain implements Filter, IStatements {

private static Logger log = Logger.getLogger(CheckDomain.class);
	
	@Override
	public void destroy() {

	} 
	
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
 
		HttpServletRequest httpReq = (HttpServletRequest) req;

		HttpServletResponse httpRes = (HttpServletResponse) res;

		// check if session already exists
		HttpSession session = httpReq.getSession(true);
		
		
		String strDomain = httpReq.getServerName().split("\\.")[0];
//		System.out.println("httpReq.getServerName()=="+httpReq.getServerName());
//		System.out.println("strDomain=="+strDomain);
//		System.out.println("getContextPath=="+httpReq.getContextPath());
//		System.out.println("getAuthType=="+httpReq.getAuthType());
//		System.out.println("getCharacterEncoding=="+httpReq.getCharacterEncoding());
//		System.out.println("getContentType=="+httpReq.getContentType());
//		System.out.println("getLocalAddr=="+httpReq.getLocalAddr());
//		System.out.println("getLocalName=="+httpReq.getLocalName());
//		System.out.println("getLocalPort=="+httpReq.getLocalPort());
//		System.out.println("getMethod=="+httpReq.getMethod());
//		System.out.println("getPathInfo=="+httpReq.getPathInfo());
//		System.out.println("getPathTranslated=="+httpReq.getPathTranslated());
//		System.out.println("getProtocol=="+httpReq.getProtocol());
//		System.out.println("getParameterMap=="+httpReq.getParameterMap());
//		System.out.println("getParameterNames=="+httpReq.getParameterNames());
//		System.out.println("getQueryString=="+httpReq.getQueryString());
//		System.out.println("getRemoteAddr=="+httpReq.getRemoteAddr());
//		System.out.println("getRemoteHost=="+httpReq.getRemoteHost());
//		System.out.println("getRemotePort=="+httpReq.getRemotePort());
//		System.out.println("getRemoteUser=="+httpReq.getRemoteUser());
//		System.out.println("getRequestedSessionId=="+httpReq.getRequestedSessionId());
//		System.out.println("getRequestURI=="+httpReq.getRequestURI());
//		System.out.println("getScheme=="+httpReq.getScheme());
//		System.out.println("getServerName=="+httpReq.getServerName());
//		System.out.println("getServerPort=="+httpReq.getServerPort());
//		System.out.println("getServletPath=="+httpReq.getServletPath());
//		System.out.println("getUserPrincipal=="+httpReq.getUserPrincipal());
		
//		String strAlais = (String)session.getAttribute("ALAIS_SUBDOMAIN");
		String strAlais = null; 
		if(strAlais==null){
			Database db = new Database();
			db.setRequest(httpReq);
			Connection con = null;
			PreparedStatement pst =null;
			ResultSet rs =null;
			try {
				con = db.makeConnection(con, "base_db");
				pst = con.prepareStatement("select * from database_details where subdomain=?");
				pst.setString(1, strDomain);
//				System.out.println("pst======>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					strAlais = rs.getString("db_alias");
					session.setAttribute("ALAIS_SUBDOMAIN", strAlais);
					
//					Database.H_ALAIS = strAlais;
//					Database.H_DBNAME = rs.getString("db_name");
//					
//					Database.H_HOST = rs.getString("db_host");
//					Database.H_PORT = rs.getString("db_port");
//					Database.H_DBUSERNAME = rs.getString("db_username");
//					Database.H_DBPASSWORD = rs.getString("db_password"); 
				}
				rs.close(); 
				pst.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
		}
		 
		System.out.println("strAlais----->"+strAlais);
		
		
		
		if(strAlais==null){
			//RequestDispatcher dispatcher=httpReq.getRequestDispatcher("AddEmployee.action");
			//dispatcher.include(httpReq, httpRes);
		}else{
			chain.doFilter(httpReq, httpRes);
		}
		
		
		
//		System.out.println("==========Checking Domain==========");
//		chain.doFilter(httpReq, httpRes);
		
	}
	
//	HttpServletRequest httpReq;
//	public void setHttpReq(HttpServletRequest httpReq) {
//		this.httpReq = httpReq;
//	}
	private ServletContext servletContext;
	@Override
	public void init(FilterConfig config) throws ServletException {
		servletContext = config.getServletContext();
	}
}
