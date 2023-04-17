package com.konnect.jpms.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DBBackup extends ActionSupport implements ServletRequestAware, IStatements {

 	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/common/DBBackup.jsp");
		request.setAttribute(TITLE, "Database Backup");

		   
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		 
		if(getSubmit()!=null){
			if(CF.isCloud()){
				takeCloudDBBackup(uF);
			} else {
				takeDBBackup(uF);
			}
		}
		viewBackup(uF);
		
		return loadDBBackup();
		
	}
	
	String submit;
	  
	public String loadValidateNotificationSettings() {
		
		return LOAD;
	}
	

	public String loadDBBackup() {
		
		return LOAD;
	}
	
	
	
//	String backupLocation = "/home/konnect/jboss-4.2.3.GA/server/default/deploy/PayrollMS.war/backup/";
	
	
	public void viewBackup(UtilityFunctions uF){
		List<List<String>> alReport = new ArrayList<List<String>>();
		
		try {
			String backupLocation = CF.getBackUpLocation();	
			if(backupLocation==null)return;
			
			String backupRetriveLocation = CF.getBackUpRetriveLocation();
			if(backupRetriveLocation==null)return;
			
			File folder = new File(backupLocation);
			File []listOfFiles = folder.listFiles();
//			System.out.println("backupLocation=====>"+backupLocation);
			
//			Arrays.sort(listOfFiles, listOfFiles.length-1, 1);   
			if(listOfFiles != null){
				Arrays.sort(listOfFiles,Collections.reverseOrder());
			}
			
			List<String> alInner = new ArrayList<String>();
			
			for(int i=0; listOfFiles!=null && i<listOfFiles.length; i++){
				if(listOfFiles[i].isFile()){
					alInner = new ArrayList<String>();
					alInner.add(listOfFiles[i].getName());
					alInner.add(new Date(listOfFiles[i].lastModified())+"");
					alInner.add(getSizeKBMB(listOfFiles[i].length(), uF));
//					alInner.add("<a href=\""+CF.getStrDocRetriveLocation()+"/dbbackup/"+listOfFiles[i].getName()+"\"><img src=\"images1/payslip.png\" title=\"Download Backup\"></a>");
					alInner.add("<a href=\""+backupRetriveLocation+listOfFiles[i].getName()+"\"><i class=\"fa fa-file-o\" aria-hidden=\"true\" title=\"Download Backup\"></i></a>");
					alReport.add(alInner);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("alReport", alReport);
		
	}
	
	
	public String getSizeKBMB(long lSize, UtilityFunctions uF){
		double dblSize = 0;
		
		long lGB = 1000000000;
		long lMB = 1000000;
		long lKB = 1000;
		
		if(lSize>lGB){
			dblSize = lSize * 1.0 / lGB;
			return uF.formatIntoOneDecimal(dblSize)+"GB";
		}else if(lSize>lMB){
			dblSize = lSize * 1.0 / lMB;
			return uF.formatIntoOneDecimal(dblSize)+"MB";
		}else if(lSize>lKB){
			dblSize = lSize * 1.0 / lKB;
			return uF.formatIntoOneDecimal(dblSize)+"KB";
		}else {
			dblSize = lSize * 1.0;
			return uF.formatIntoOneDecimal(dblSize)+"Bytes";
		}
		
	}
	
	public String takeCloudDBBackup(UtilityFunctions uF) {



		try {
			String pgDumpLocation = CF.getDumpLocation();
			String backupLocation = CF.getBackUpLocation();
			 
			System.out.println("pgDumpLocation=====>"+pgDumpLocation);
			System.out.println("backupLocation=====>"+backupLocation);
			
			if(pgDumpLocation==null || backupLocation==null){
				request.setAttribute(MESSAGE, ERRORM+"Dump or backup location not set."+END);
				return SUCCESS;
			}
			
			File file = new File(pgDumpLocation);

			if (file.exists()) {
				
				Map<String, String> hmDB = getDBDetails(uF);
				if(hmDB == null) hmDB = new HashMap<String, String>();
				//DB_ALIAS DB_NAME DB_HOST DB_PORT DB_USERNAME DB_PASSWORD
				
				FileUtils.forceMkdir(new File(backupLocation));
				
				StringBuffer sbfile = new StringBuffer();
//				String strDate = uF.getDateFormat(uF.getCurrentDate("Asia/Calcutta")+" "+uF.getCurrentTime("Asia/Calcutta"), "yyyy-MM-dd HH:mm:ss", "ddMMyyyyHHmm");
				String strDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+" "+uF.getCurrentTime(CF.getStrTimeZone()), "yyyy-MM-dd HH:mm:ss", "ddMMyyyyHHmm");
				
//				sbfile.append(backupLocation + "Backup_"+Database.H_DBNAME+"_"+strDate+".backup");
				sbfile.append(backupLocation + "Backup_"+hmDB.get("DB_NAME")+"_"+strDate+".backup");
				java.io.File backupfile = new java.io.File(sbfile.toString());
				if (backupfile.exists()) {
					backupfile.delete(); 
				}

//				/usr/bin/pg_dump -i -h 192.168.1.5 -p 5432 -u postgres -F c -b -v -f /opt/jboss/jboss-as-7.1.1.Final/standalone/deployments/Demo.war/backup/BAckup.backup demo_payroll
				
				List<String> cmds = new ArrayList<String>();
				cmds.add(pgDumpLocation);
				cmds.add("-i");
				cmds.add("-h");
				cmds.add(hmDB.get("DB_HOST"));
				cmds.add("-p");
				cmds.add(hmDB.get("DB_PORT"));
				cmds.add("-U");
				cmds.add(hmDB.get("DB_USERNAME"));
				cmds.add("-F");  
				cmds.add("c");
				cmds.add("-b");
				cmds.add("-v");
				cmds.add("-f");
				cmds.add(backupfile.toString());
				cmds.add(hmDB.get("DB_NAME"));
//				System.out.println("cmds=====>"+cmds.toString());
//				System.out.println("HOST=="+HOST+" PORT="+PORT+" DBUSERNAME="+DBUSERNAME+" backupfile"+backupfile.toString()+" Database.H_DBNAME="+Database.H_DBNAME);
				System.out.println("HOST=="+hmDB.get("DB_HOST")+" PORT="+hmDB.get("DB_PORT")+" DBUSERNAME="+hmDB.get("DB_USERNAME")+" backupfile"+backupfile.toString()+" Database.DBNAME="+hmDB.get("DB_NAME")+" DBPASSWORD====>"+hmDB.get("DB_PASSWORD"));
				
				
				
				ProcessBuilder pb = new ProcessBuilder(cmds);
				pb.environment().put("PGPASSWORD", hmDB.get("DB_PASSWORD"));
				Process process = pb.start();

				InputStream objIS = process.getErrorStream();
				InputStreamReader objISR = new InputStreamReader(objIS);
				
				
				BufferedReader objBR = new BufferedReader(objISR);
				
				String strTemp = null;
				while((strTemp = objBR.readLine())!=null){
//					log.info(strTemp);
//					System.out.println(strTemp);
				}
				
				int processComplete = process.waitFor();
				if (processComplete == 0) {
					System.out.println("DatabaseManager.backup: Backup Successfull");
					request.setAttribute(MESSAGE, SUCCESSM+"Backup Successfull"+END);
				} else {
					System.out.println("DatabaseManager.Backup: Backup Failure!");
					request.setAttribute(MESSAGE, ERRORM+"Backup Failure!"+END);
				}
			}else{
				System.out.println("Could not find the pd_dump file..");
				request.setAttribute(MESSAGE, ERRORM+"Could not find the pd_dump file.."+END);
			}
		} catch (Exception e) {
			System.err.println("Could not invoke browser, command=");
			request.setAttribute(MESSAGE, ERRORM+"Could not invoke browser, command"+END);
			e.printStackTrace();
		}
		
		
		return SUCCESS;

	}
	
	
	private Map<String, String> getDBDetails(UtilityFunctions uF) {
		Map<String, String> hmDB = new HashMap<String, String>();
		if(request!=null){
			Connection con = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			Database db = new Database();
			db.setRequest(request);
			
			try {
				
				String strDomain = request.getServerName().split("\\.")[0];
				con = db.makeConnection(con, "base_db");
				pst = con.prepareStatement("select * from database_details where subdomain=?");
				pst.setString(1, strDomain);
				System.out.println("pst======>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					hmDB.put("DB_ALIAS", rs.getString("db_alias"));
					hmDB.put("DB_NAME", rs.getString("db_name"));
					hmDB.put("DB_HOST", rs.getString("db_host"));
					hmDB.put("DB_PORT", rs.getString("db_port"));
					hmDB.put("DB_USERNAME", rs.getString("db_username"));
					hmDB.put("DB_PASSWORD", rs.getString("db_password"));
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
		return hmDB;
		
	}


	public String takeDBBackup(UtilityFunctions uF) {



		try {
			String pgDumpLocation = CF.getDumpLocation();
			String backupLocation = CF.getBackUpLocation();
			 
			System.out.println("pgDumpLocation=====>"+pgDumpLocation);
			System.out.println("backupLocation=====>"+backupLocation);
			
			if(pgDumpLocation==null || backupLocation==null){
				request.setAttribute(MESSAGE, ERRORM+"Dump or backup location not set."+END);
				return SUCCESS;
			}
			
			File file = new File(pgDumpLocation);

			if (file.exists()) {
				FileUtils.forceMkdir(new File(backupLocation));
				
				StringBuffer sbfile = new StringBuffer();
				String strDate = uF.getDateFormat(uF.getCurrentDate("Asia/Calcutta")+" "+uF.getCurrentTime("Asia/Calcutta"), "yyyy-MM-dd HH:mm:ss", "ddMMyyyyHHmm");
				
//				sbfile.append(backupLocation + "Backup_"+Database.H_DBNAME+"_"+strDate+".backup");
				sbfile.append(backupLocation + "Backup_"+Database.DBNAME+"_"+strDate+".backup");
				java.io.File backupfile = new java.io.File(sbfile.toString());
				if (backupfile.exists()) {
					backupfile.delete(); 
				}

//				/usr/bin/pg_dump -i -h 192.168.1.5 -p 5432 -u postgres -F c -b -v -f /opt/jboss/jboss-as-7.1.1.Final/standalone/deployments/Demo.war/backup/BAckup.backup demo_payroll
				
				List<String> cmds = new ArrayList<String>();
				cmds.add(pgDumpLocation);
				cmds.add("-i");
				cmds.add("-h");
				cmds.add(HOST);
				cmds.add("-p");
				cmds.add(PORT);
				cmds.add("-U");
				cmds.add(DBUSERNAME);
				cmds.add("-F");  
				cmds.add("c");
				cmds.add("-b");
				cmds.add("-v");
				cmds.add("-f");
				cmds.add(backupfile.toString());
//				cmds.add(getDBName());     
				cmds.add(DBNAME);
//				System.out.println("getDBName()=====>"+getDBName());
//				System.out.println("cmds=====>"+cmds.toString());
//				System.out.println("HOST=="+HOST+" PORT="+PORT+" DBUSERNAME="+DBUSERNAME+" backupfile"+backupfile.toString()+" Database.H_DBNAME="+Database.H_DBNAME);
				System.out.println("HOST=="+HOST+" PORT="+PORT+" DBUSERNAME="+DBUSERNAME+" backupfile"+backupfile.toString()+" Database.DBNAME="+Database.DBNAME+" DBPASSWORD====>"+DBPASSWORD);
				
				
				
				ProcessBuilder pb = new ProcessBuilder(cmds);
				pb.environment().put("PGPASSWORD", DBPASSWORD);
				Process process = pb.start();

				InputStream objIS = process.getErrorStream();
				InputStreamReader objISR = new InputStreamReader(objIS);
				
				
				BufferedReader objBR = new BufferedReader(objISR);
				
				String strTemp = null;
				while((strTemp = objBR.readLine())!=null){
//					log.info(strTemp);
//					System.out.println(strTemp);
				}
				
				int processComplete = process.waitFor();
				if (processComplete == 0) {
					System.out.println("DatabaseManager.backup: Backup Successfull");
					request.setAttribute(MESSAGE, SUCCESSM+"Backup Successfull"+END);
				} else {
					System.out.println("DatabaseManager.Backup: Backup Failure!");
					request.setAttribute(MESSAGE, ERRORM+"Backup Failure!"+END);
				}
			}else{
				System.out.println("Could not find the pd_dump file..");
				request.setAttribute(MESSAGE, ERRORM+"Could not find the pd_dump file.."+END);
			}
		} catch (Exception e) {
			System.err.println("Could not invoke browser, command=");
			request.setAttribute(MESSAGE, ERRORM+"Could not invoke browser, command"+END);
			e.printStackTrace();
		}
		
		
		return SUCCESS;

	}
	
	
	public String getDBName(){
		String strAlais = null;
		if(request!=null){
			Connection con = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			Database db = new Database();
			db.setRequest(request);
			
			try {
				
				String strDomain = request.getServerName().split("\\.")[0];
				con = db.makeConnection(con, "base_db");
				pst = con.prepareStatement("select * from database_details where subdomain=? and db_name=?");
				pst.setString(1, strDomain);
				pst.setString(2, Database.DBNAME);
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
//					strAlais = rs.getString("db_alias");
					strAlais = rs.getString("db_name");
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
		}else{
//			strAlais = Database.H_DBNAME;
			strAlais = Database.DBNAME;
		}
		return strAlais;
		
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