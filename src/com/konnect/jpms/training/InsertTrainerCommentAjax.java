package com.konnect.jpms.training;

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

public class InsertTrainerCommentAjax extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	private int empId;  

	
	CommonFunctions CF=null;

	private static Logger log = Logger.getLogger(InsertTrainerCommentAjax.class);
	
	public String execute() {
		
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			return LOGIN;
		}
		
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/

		insertComment();

		return SUCCESS;
	}


	private void insertComment() {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;

		UtilityFunctions uF=new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);

			pst=con.prepareStatement("update training_learnings set trainer_comments=? ,is_completed=?" +
					" where plan_id=? and emp_id=? ");
			pst.setString(1, getTrainerCommentHidden());
			if(isCompleted==true)
			pst.setInt(2, 1);
			else
			pst.setInt(2, 0);
			pst.setInt(3,uF.parseToInt(getPlanID()));
			pst.setInt(4,uF.parseToInt(getEmpID()));
			pst.execute();
			pst.close();
			
		}catch(Exception e){
			
			e.printStackTrace();
			
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}


	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
		
	}

		String trainerCommentHidden;
	
		public String getTrainerCommentHidden() {
			return trainerCommentHidden;
		}
	
	
		public void setTrainerCommentHidden(String trainerCommentHidden) {
			this.trainerCommentHidden = trainerCommentHidden;
		}

		String empID;
		String planID;
		
		public String getEmpID() {
			return empID;
		}
		
		
		public String getPlanID() {
			return planID;
		}
		
		public void setEmpID(String empID) {
			this.empID = empID;
		}
		
		public void setPlanID(String planID) {
			this.planID = planID;
		}
		
		public boolean isCompleted;
		
		public boolean isCompleted() {
			return isCompleted;
		}
		
		public void setCompleted(boolean isCompleted) {
			this.isCompleted = isCompleted;
		}


	
}