package com.konnect.jpms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
  
public class Database implements IConstants {
	static private Connection conn; 
 
/**
 * Cloud Connection 
 * */ 
//public Connection makeConnection(Connection con) {
//	Connection con1 = null;
//	PreparedStatement pst = null;
//	ResultSet rs = null;
//		try {
//			String strDomain = request.getServerName().split("\\.")[0];
//			if(strDomain!=null && !strDomain.trim().equals("") && !strDomain.trim().equalsIgnoreCase("")){
//				con = null; 
//				String strAlais 		= null;
//				String H_ALAIS	 		= null;
//				String H_DBNAME	 		= null;
//				String H_HOST	 		= null;
//				String H_PORT	 		= null;
//				String H_DBUSERNAME	 	= null;
//				String H_DBPASSWORD	 	= null;
//				
//				con1 = makeConnection(con1, "base_db");
//				pst = con1.prepareStatement("select * from database_details where subdomain=?");
//				pst.setString(1, strDomain.trim());
////				System.out.println("pst======>"+pst);
//				rs = pst.executeQuery();
//				while(rs.next()){
//					strAlais = rs.getString("db_alias");
//					H_ALAIS = strAlais;
//					H_DBNAME = rs.getString("db_name");
//					
//					H_HOST = rs.getString("db_host");
//					H_PORT = rs.getString("db_port");
//					H_DBUSERNAME = rs.getString("db_username");
//					H_DBPASSWORD = rs.getString("db_password");
//				}
//				rs.close();
//				pst.close(); 
//				
//				Class.forName("org.postgresql.Driver");
//				
//				con = DriverManager.getConnection("jdbc:postgresql://" + H_HOST + ":"+H_PORT+"/"+ H_DBNAME, H_DBUSERNAME, H_DBPASSWORD );
//			}
//			 
//		} catch (Exception e) { 
//			e.printStackTrace();
//		} finally {
//			closeResultSet(rs);
//			closeStatements(pst);
//			closeConnectionDb(con1);
//		}
//		
//		return con;   
//	}

/**
 * Cloud Connection end
 * */
	

/**
 * Single Connection
 * */
	
	  public Connection makeConnection(Connection con) {
		  try {
		  
			  Class.forName("org.postgresql.Driver"); 
			  // conn = DriverManager.getConnection("jdbc:postgresql://" + HOST + ":"+PORT+"/" // + DBNAME, DBUSERNAME, DBPASSWORD ); 
			  
			  if(con==null || (con!=null && con.isClosed())) {
				  Class.forName("org.postgresql.Driver"); 
				  con = DriverManager.getConnection("jdbc:postgresql://" + HOST + ":"+PORT+"/" + DBNAME, DBUSERNAME, DBPASSWORD); //
			  }
		  
		  } catch (Exception e) { 
			  e.printStackTrace(); 
		  } 
		  
		  return con; 
	  }
	 

/**
 * Single Connection End
 * */
		 
	
	public static String H_ALAIS	 	= null;
	public static String H_DBNAME	 	= null;
	public static String H_HOST	 	= null;
	public static String H_PORT	 	= null;
	public static String H_DBUSERNAME	 	= null;
	public static String H_DBPASSWORD	 	= null;
	
//	public String H_ALAIS	 	= null;
//	public String H_DBNAME	 	= null;
//	public String H_HOST	 	= null;
//	public String H_PORT	 	= null;
//	public String H_DBUSERNAME	= null;
//	public String H_DBPASSWORD	= null;
	
	public Connection makeConnection(Connection con, String strDB) { 
		try { 
			
			if(con==null || (con!=null && con.isClosed())){
				Class.forName("org.postgresql.Driver");
				con = DriverManager.getConnection("jdbc:postgresql://" + HOST + ":"+PORT+"/"
						+ strDB, DBUSERNAME, DBPASSWORD );
				
//				System.out.println("====================    Creating New Instance   ======================");
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;    
	}
	
	/**
	 * Cloud
	 * */

//	public void closeConnection(Connection con) {
//		try {
//			if(con!=null){
//				if(!con.getAutoCommit()){
//					con.commit();
//				}
//				con.close();  
//				con = null; 
//				
//			}
//		} catch (Exception e) {   
//			e.printStackTrace();
//		}
//	}
	/**
	 * Cloud end 
	 * */
	
	
	/**
	 * Single Instance  
	 * */
	public void closeConnection(Connection con) {
		try {
			if(con!=null){
				if(!con.getAutoCommit()){
					con.commit();
				} 
				con.close();  
				con = null; 
			}
		} catch (Exception e) {   
			e.printStackTrace();
		}
	} 
	/**
	 * Single Instance End
	 * */
	
public void closeConnectionDb(Connection con) {
		try {
			if(con!=null){
				if(!con.getAutoCommit()){
					con.commit();
				}
				con.close();
				con = null;
			}
		} catch (Exception e) {   
			e.printStackTrace();
		}

	}
	
	public void closeStatements(PreparedStatement pst) {

		try {
			if(pst!=null){
				pst.close();
				pst=null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeResultSet(ResultSet rst) {

		try {
			if(rst!=null){
				rst.close();
				rst=null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public Connection makeConnection() {
		Connection con = null;
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://" + "dailyhrsproduct.db.4270658.hostedresource.com" +"/"
					+ "dailyhrsproduct", "dailyhrsproduct", "K0nnectDa1ly" );
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;

	}
	
	public static void main(String[] args) {
		new Database();
	}


	HttpServletRequest request;
	public void setRequest(HttpServletRequest request){
		this.request=request;
	}
	
	String strDomain;
	public void setDomain(String strDomain){
		this.strDomain=strDomain;
	}

}
