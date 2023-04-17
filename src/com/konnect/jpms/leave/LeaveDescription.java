package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LeaveDescription  extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(LeaveDescription.class);
	
	
	public String execute() throws Exception {

		session = request.getSession(); 
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		
		
		String strLeaveId = request.getParameter("LID");
		
		viewLeaveDescription(strLeaveId);

		return loadLeaveDescription();

	}
	
	
	
	
	public String loadLeaveDescription() {
		return LOAD;
	}
	
	
	public String viewLeaveDescription(String strLeaveId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			
			con = db.makeConnection(con);
			
			
			pst = con.prepareStatement("select * from level_details where level_id = ?");
			pst.setInt(1, uF.parseToInt(strLeaveId));
			rs = pst.executeQuery();
			String strLevelName = null;
			while(rs.next()){
				strLevelName = rs.getString("level_name")+"["+rs.getString("level_code")+"]";
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select * from designation_details where level_id = ?");
			pst.setInt(1, uF.parseToInt(strLeaveId));
			rs = pst.executeQuery();
			StringBuilder sb = new StringBuilder();
			sb.append("Designations under "+strLevelName+":<br/><br/>");
			
			int count = 0;
			while(rs.next()){
				sb.append(++count+". "+rs.getString("designation_name")+"<br/>");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("description", sb.toString());
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;


	}

}
