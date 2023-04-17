package com.konnect.jpms.common;

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

public class DBMaintenance extends ActionSupport implements ServletRequestAware, IStatements {

 	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	
	String submit;
	
	public String execute() throws Exception {
		
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/common/DBMaintenace.jsp");
		request.setAttribute(TITLE, "Database Maintenance");
		
		UtilityFunctions uF = new UtilityFunctions();
		   
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		 
		if(getSubmit()!=null){
			maintaninDB(uF);
			getFreeMemory();
		}
		
		
		return loadDBBackup();
		
	}
	
	public void getFreeMemory() {
		int mb = 1024 * 1024;
		Runtime r = Runtime.getRuntime();
		
		System.out.println("before Total Memory: " + r.totalMemory() / mb); // available memory
		System.out.println("before Free Memory: " + r.freeMemory() / mb); // free memory
		System.out.println("before Used Memory: " + (r.totalMemory() - r.freeMemory()) / mb); // used memory
		System.out.println("before Max Memory: " + r.maxMemory() / mb); // Maximum available memory
		
		r.gc();
		
		System.out.println("after Total Memory: " + r.totalMemory() / mb); // available memory
		System.out.println("after Free Memory: " + r.freeMemory() / mb); // free memory
		System.out.println("after Used Memory: " + (r.totalMemory() - r.freeMemory()) / mb); // used memory
		System.out.println("after Max Memory: " + r.maxMemory() / mb);  // Maximum available memory
	}
	
	
	  
	public String loadValidateNotificationSettings() {
		
		return LOAD;
	}
	

	public String loadDBBackup() {
		
		return LOAD;
	}
	
	
	
	public String maintaninDB(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		PreparedStatement pst2 = null;
		ResultSet rs = null;
		try {
			
			con = db.makeConnection(con);
			
			System.out.println("======= Maintenance Started  ====");
			
			pst = con.prepareStatement("SELECT n.nspname,c.relname,a.attname,d.adsrc FROM pg_attrdef AS d " +
					"JOIN pg_attribute AS a ON a.attrelid = d.adrelid AND a.attnum = d.adnum " +
					"JOIN pg_class AS c ON c.oid = d.adrelid JOIN pg_namespace AS n ON n.oid = c.relnamespace " +
					"WHERE adsrc LIKE 'nextval(''%' ORDER BY c.relname;");
			rs = pst.executeQuery();
			List<Map<String, String>> al = new ArrayList<Map<String,String>>();
			while(rs.next()){
				Map<String, String> hm = new HashMap<String, String>();
				hm.put("TABLE_NAME", rs.getString("relname"));
				hm.put("TABLE_PRIMARY_ID", rs.getString("attname"));
				String[] temp = rs.getString("adsrc").split("'");
				hm.put("TABLE_SEQUENCE", temp[1]);
				
				al.add(hm);
			}
			rs.close();
			pst.close();
			
			for(int i=0; i < al.size(); i++){
				Map<String, String> hm = al.get(i);
				pst = con.prepareStatement("select max("+hm.get("TABLE_PRIMARY_ID")+") as cnt from "+hm.get("TABLE_NAME")); 
				rs = pst.executeQuery();
				int cnt = 0;
				while(rs.next()){
					cnt = uF.parseToInt(rs.getString("cnt"));
				}
				rs.close();
				pst.close();
				
				cnt++;
				pst = con.prepareStatement("ALTER SEQUENCE "+hm.get("TABLE_SEQUENCE")+" restart with "+cnt);
//				System.out.println("pst====>"+pst);
				pst.execute();
				pst.close();
				
			}
			
			
			pst = con.prepareStatement("SELECT * FROM pg_tables where schemaname = 'public' order by tablename");
			rs = pst.executeQuery();
			List<String> alTables = new ArrayList<String>();
			while(rs.next()){
				alTables.add(rs.getString("tablename"));
			}
			rs.close();
			pst.close();
			 
			for(String tableName : alTables){
				pst1 = con.prepareStatement("vacuum full "+tableName);
//				System.out.println("pst1====>"+pst1);
				pst1.execute();
				pst1.close();
				
				pst2 = con.prepareStatement("reindex table "+tableName);
//				System.out.println("pst2====>"+pst2);
				pst2.execute();
				pst2.close();
			}
			
			System.out.println("===== Maintenance Completed  ====");
			request.setAttribute(MESSAGE, SUCCESSM+"Maintenance successfully completed."+END);
		} catch (Exception e) {
			request.setAttribute(MESSAGE, ERRORM+"Could not invoke browser, command"+END);
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeStatements(pst2);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	

	public void validate() {
        loadValidateNotificationSettings();
    }

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}


	public String getSubmit() {
		return submit;
	}


	public void setSubmit(String submit) {
		this.submit = submit;
	}
}