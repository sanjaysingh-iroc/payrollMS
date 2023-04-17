package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class JdReport extends ActionSupport implements ServletRequestAware,IConstants {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(RequirementApproval.class);

	String strSessionEmpId = null;

	public String execute() throws Exception {
		
		UtilityFunctions uF = new UtilityFunctions();
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, "Job Report");
		request.setAttribute(PAGE, "/jsp/recruitment/jdReport.jsp");

		preparejobreport();
		
		return LOAD;

	}
	
	public void preparejobreport(){
			
			
		List<List<String>> alopenjobreport=new ArrayList<List<String>>();
			
		Connection con=null;
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst=null;

		ResultSet rst=null;
		
		try {
			
			con=db.makeConnection(con);
			
			pst=con.prepareStatement("select job_code,recruitment_id,job_description from recruitment_details where job_approval_status=1");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
		while(rst.next()){
			
			List<String> job_code_info =new ArrayList<String>();
			job_code_info.add(rst.getString("recruitment_id"));
			job_code_info.add(rst.getString("job_code"));
			job_code_info.add(rst.getString("job_description"));
			alopenjobreport.add(job_code_info);
			
		}
		rst.close();
		pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
			
			request.setAttribute("job_code_info",alopenjobreport);

		}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}
