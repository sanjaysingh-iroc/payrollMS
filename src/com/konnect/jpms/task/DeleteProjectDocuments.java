package com.konnect.jpms.task;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DeleteProjectDocuments extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
//	String folder_name;
	String operation;
	String type;
	String mainPath;
	String proDocID; 
	
	public String execute() throws Exception {
		
		deleteProjectDocuments();
		return SUCCESS;
	}
	 
	
	public void deleteProjectDocuments() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {
			con = db.makeConnection(con);
//			System.out.println("getMainPath ===>> " + getMainPath());
			String strMainPath = URLDecoder.decode(getMainPath(), "UTF-8");
//			System.out.println("strMainPath ===>> " + strMainPath);
			boolean flag = false;
			File directory = new File(strMainPath);
			
			System.out.println("strMainPath --->> " + strMainPath);
			
	    	//make sure directory exists
	    	if(!directory.exists()) {
	           System.out.println("Directory does not exist.");
	        } else {
	        	flag = delete(directory);
	        }
	    	
	    	System.out.println("flag --->> " + flag);
	    	
//			 if(flag) {
				pst = con.prepareStatement("DELETE FROM project_document_details WHERE pro_folder_id in (select pro_document_id from project_document_details WHERE pro_folder_id = ?)");
				pst.setInt(1, uF.parseToInt(getProDocID()));
//				System.out.println("pst ===>> " + pst);
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("DELETE FROM project_document_details WHERE pro_folder_id = ?");
				pst.setInt(1, uF.parseToInt(getProDocID()));
//				System.out.println("pst 1 ===>> " + pst);
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("DELETE FROM project_document_details WHERE pro_document_id = ?");
				pst.setInt(1, uF.parseToInt(getProDocID()));
//				System.out.println("pst 2 ===>> " + pst);
				pst.executeUpdate();
				pst.close();
				
				request.setAttribute("STATUS_MSG", "yes");
//				System.out.println("yes .....!");
//			 }
//				request.setAttribute("STATUS_MSG", "<b><font color=\"blue\">This document is deleted</font></b>");
		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "error");
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	 public static boolean delete(File file) throws IOException{
		 
	    	if(file.isDirectory()) {
	 
	    		//directory is empty, then delete it
	    		if(file.list().length==0) {
	 
	    		   file.delete();
//	    		   System.out.println("Directory is deleted : " + file.getAbsolutePath());
	    		   return true;
	    		} else {
	 
	    		   //list all the directory contents
	        	   String files[] = file.list();
	 
	        	   for (String temp : files) {
	        	      //construct the file structure
	        	      File fileDelete = new File(file, temp);
	 
	        	      //recursive delete
	        	     delete(fileDelete);
	        	   }
	 
	        	   //check the directory again, if empty then delete it
	        	   if(file.list().length==0){
	           	     file.delete();
//	        	     System.out.println("Directory is deleted : " + file.getAbsolutePath());
	        	     return true;
	        	   }
	    		}
	 
	    	} else {
	    		//if file, then delete it
	    		file.delete();
//	    		System.out.println("File is deleted : " + file.getAbsolutePath());
	    		return true;
	    	}
	    	return false;
	    }
	 
	
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMainPath() {
		return mainPath;
	}

	public void setMainPath(String mainPath) {
		this.mainPath = mainPath;
	}

	public String getProDocID() {
		return proDocID;
	}

	public void setProDocID(String proDocID) {
		this.proDocID = proDocID;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	
	
}