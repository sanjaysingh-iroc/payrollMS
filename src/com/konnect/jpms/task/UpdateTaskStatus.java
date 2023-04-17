package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateTaskStatus extends ActionSupport implements IStatements, ServletRequestAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String strSessionEmpId;
	HttpSession session;
	CommonFunctions CF; 
	
	public String execute() throws Exception {
		 
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		String strProjectId = (String)request.getParameter("pro_id");
		String strStatus = (String)request.getParameter("status");
		
		updateProjectStatus(strProjectId, strStatus);
		
		return SUCCESS;
	
	}
	public void updateProjectStatus(String strProjectId, String strStatus){

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			
			if(strStatus!=null && strStatus.equals("n")){
				pst = con.prepareStatement("update projectmntnc set approve_status = 'n' where pro_id=?");
				pst.setInt(1, uF.parseToInt(strProjectId));
				pst.execute();
				pst.close();
				request.setAttribute("STATUS_MSG", "Status Updated.");
				
//				System.out.println("pst==>"+pst);
			}
			
		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}