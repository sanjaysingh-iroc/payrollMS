package com.konnect.jpms.task;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class ViewSnapShot1 extends ActionSupport implements IStatements, ServletRequestAware {

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
//		System.out.println("got hereee");
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
//		 strSessionEmpId = "101";
//		 System.out.println("------------------lll--- Empid----->"+strEmpId);
//		 System.out.println("---------lllllll--------Screen shot id--------->"+strScreenShotId);
		 
		String mainpath=CF.getStrDocRetriveLocation();
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
//		System.out.println("Emp ID--"+strSessionEmpId);
//		System.out.println("orgid--"+strSessionOrgId);
//		System.out.println("taskiid--"+strTaskId);
//		System.out.println("percent-"+percent);
//		System.out.println("mainpath-"+mainpath);
//		System.out.println("getComplete ==>> " + getComplete());
		getSnaphots(strEmpId, strScreenShotId);

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
	
	
	
	public void getSnaphots(String empId, String screenShotId) {
		String mainpath=CF.getStrDocRetriveLocation();
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from task_screenshot_details where emp_id=? and screenshot_id=?");
			pst.setInt(1, Integer.parseInt(empId));
			pst.setInt(2, Integer.parseInt(screenShotId));
//			System.out.println("pst--"+pst);
			rs = pst.executeQuery();
			String screenShotName=null;
			int proId=0;
			int taskId = 0;
			int orgId=0;
			while(rs.next())
			{
				screenShotName=rs.getString("screenshot_name").trim();
				proId=rs.getInt("project_id");
				taskId=rs.getInt("task_id");
				orgId=rs.getInt("org_id");
				
			}
			rs.close();
			pst.close();
			if(proId==0 && taskId==0){
				
				mainpath=mainpath+"Tracker"+"/" +orgId+"/"+empId+"/"+"screenshot"+"/"+screenShotName;
			}else{
				mainpath=mainpath+"Tracker"+"/" +orgId+"/"+proId+"/"+taskId+"/"+empId+"/"+"images"+"/"+screenShotName;
			}
			
			request.setAttribute("imagepath", mainpath);
//			System.out.println("mainpath--"+mainpath);
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
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
		
	}
}
