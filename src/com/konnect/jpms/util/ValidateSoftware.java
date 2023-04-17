package com.konnect.jpms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.konnect.jpms.employee.AddEmployee;

public class ValidateSoftware implements Filter, IStatements {

	private String username;
	private String password;
	HttpServletRequest httpReq;
	
	private static Logger log = Logger.getLogger(ValidateSoftware.class);
	
	@Override
	public void destroy() {

	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
 
		httpReq = (HttpServletRequest) req;

		HttpServletResponse httpRes = (HttpServletResponse) res;
		boolean isTrue = false;
		if(req!=null && res!=null){
			isTrue = getPath(req, res);
		}
		
		if(isTrue){
			chain.doFilter(req, res);
		}else{
			servletContext.getRequestDispatcher("/jsp/errorPages/AccessDeniedSoftware.jsp").forward(req, res); 
		}
		
	}
	
	private ServletContext servletContext;
	@Override
	public void init(FilterConfig config) throws ServletException {
		servletContext = config.getServletContext();
	}
	
	public boolean getPath(ServletRequest req, ServletResponse res){
		
//		try {
//			
//			
//			String macID = ClientMacAddress.getMacAddress();
//			if(req!=null){
//				req.setAttribute("MAC_ID", macID);
//			}
//	    	
//	    	StringBuilder text = new StringBuilder();
//	    	Scanner scanner = null;
//	    	
//	    	try {
//	    		
//			
//		    String NL = System.getProperty("line.separator");
//		    File file = new File(macID);
//		    FileInputStream fis = new FileInputStream(file);
//		    scanner = new Scanner(fis, "UTF8");
//		    long datetime = file.lastModified();
//		    Date d = new Date(datetime);
//		    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ssa");
//		    String dateString = sdf.format(d);
//		    req.setAttribute("CREATED_ON", dateString);
//		    
//		    
//		      while (scanner.hasNextLine()){
//		        text.append(scanner.nextLine() + NL);
//		      }
//		    }catch(FileNotFoundException fx){
////		    	fx.printStackTrace();
//		    	log.error(fx.getClass() + ": " +  fx.getMessage(), fx);
//		    }finally{
//		    	if(scanner!=null)scanner.close();
//		    }
//			
//	    	
//		    if(text.length()<=0){
//		    	
//		    	String strStatus = null;
//		    	Database db = new Database();
//		    	
//		    	Connection con = db.makeConnection();
//		    	PreparedStatement pst = con.prepareStatement("select * from product_info where mac_id = ?");
//		    	pst.setString(1, macID);
//		    	ResultSet rs = pst.executeQuery();
//		    	
//		    	
//		    	
//		    	while(rs.next()){
//		    		strStatus = rs.getString("status");
//		    	}
//		    	
//		    	Writer out = new OutputStreamWriter(new FileOutputStream(macID), "UTF8");
//		    	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ssa");
//			    String dateString = sdf.format(new Date());
//		    	req.setAttribute("CREATED_ON", dateString);
//				try {
//				    out.write(strStatus);
//				}finally {
//					out.close();
//					db.closeConnection(con);
//				}  
//		    }else{
//		    	if((text.toString().trim()).equalsIgnoreCase("TRUE")){
//		    		return true;
//		    	}else{ 
//		    		return false;
//		    	}
//		    }
//			    
//		} catch (Exception e) {
////			e.printStackTrace();
//		} 
		return true;
	}
}
