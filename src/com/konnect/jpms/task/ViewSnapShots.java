package com.konnect.jpms.task;


import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class ViewSnapShots extends ActionSupport implements IStatements, ServletRequestAware,ServletResponseAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String divId;
	String proId;
	String strSessionEmpId;
	String strSessionOrgId;
	String strTaskId; 
	String strEmpId;
	String strScreenShotId; 
	String complete;
	
	public String execute() {
		System.out.println("got hereee");
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		 String taskId = request.getParameter("ID");
		 String percent = request.getParameter("percent");
		 strTaskId = request.getParameter("taskId");
		 strEmpId=request.getParameter("empID");
		 strScreenShotId=request.getParameter("screenShotId");
		 String mainpath=CF.getStrDocRetriveLocation();
		 request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		System.out.println("====Emp ID-->>>>"+strSessionEmpId);
		System.out.println("===orgid-->>>>"+strSessionOrgId);
		System.out.println("==taskiid--"+strTaskId);
		System.out.println("=== Project Id===>"+proId);
		
		System.out.println("percent-"+percent);
//		System.out.println("mainpath-"+mainpath);
		System.out.println("getComplete ==>> " + getComplete());
		getSnaphots(taskId, strSessionEmpId);

		if(taskId!=null) {
			if(getComplete() != null && getComplete().equals("complete")) {
				return "mysuccess";
			} else {
				return SUCCESS;
			}
		} else {
			getTaskStatus(strTaskId, proId); 
			return LOAD;
		}
	}
	
	
	public void getSnaphots(String taskId1, String empId) {
		
		 String taskId = request.getParameter("ID");
		 strTaskId = request.getParameter("taskId");
		 strEmpId=request.getParameter("empID");
		 System.out.println("strEmpId=>"+strEmpId);
		 strScreenShotId=request.getParameter("screenShotId");
		 System.out.println("**********************************************************");
		 System.out.println("====Emp ID-->>>>"+strSessionEmpId);
		 System.out.println("===orgid-->>>>"+strSessionOrgId);
		 System.out.println("==taskiid--"+strTaskId);
		 System.out.println("=== Project Id===>"+proId);
		 System.out.println("**********************************************************");
		
		String mainpath=CF.getStrDocSaveLocation();
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			System.out.println("Project ID--"+proId);
			System.out.println("main-"+mainpath);
//			/var/www/html/Logo/Tracker/101/1137/3437/448/images
			
			mainpath=mainpath+"Tracker"+"/"+"strSessionOrgId"+"/"+proId+"/"+strTaskId+"/"+strSessionEmpId+"/"+"images";
			System.out.println("mainpath-Tracker->>>>"+mainpath);
			File[] listOfFiles;
			File folder = new File(mainpath);
			
			listOfFiles = folder.listFiles();

			if (listOfFiles != null) {
				System.out.println("in loop");
				List<String> filenames = new ArrayList<String>();
				for (int i = 0; i < listOfFiles.length; i++) {
					if (listOfFiles[i].isFile()) {
						try {
							filenames.add("" + mainpath + "/" + "" + listOfFiles[i].getName());
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (listOfFiles[i].isDirectory()) {
					}
				}
				System.out.println("Filename-"+filenames.toString());
				request.setAttribute("filenames", filenames);
			}else{
				System.out.println("files not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	public void getTaskStatus(String taskId, String proId) {
		
	}
	
	
	public String getDivId() {
		return divId;
	}

	public void setDivId(String divId) {
		this.divId = divId;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getComplete() {
		return complete;
	}

	public void setComplete(String complete) {
		this.complete = complete;
	}
	
	public String getStrEmpId() {
		return strEmpId;
	}
	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}
	public String getStrScreenShotId() {
		return strScreenShotId;
	}

	public void setStrScreenShotId(String strScreenShotId) {
		this.strScreenShotId = strScreenShotId;
	}

	HttpServletRequest request;
	
	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
		
	}
}
