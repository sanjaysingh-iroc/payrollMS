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

public class TrainingAttend extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF=null;
	
	private static Logger log = Logger.getLogger(MyLearningPlan.class);
	
	private String trainingId;
	private String lPlanId;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
		return LOGIN;
		}
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/training/TrainingAttend.jsp");
		request.setAttribute(TITLE, "Training Attend");
		
		trainingAttend();
		
		return SUCCESS;

	}

	private void trainingAttend() {
		
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con=db.makeConnection(con);
			
			pst = con.prepareStatement("insert into training_attend_details(emp_id,learning_plan_id,training_id,attend_status,added_by,entry_date) values(?,?,?,?, ?,?)");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(lPlanId));
			pst.setInt(3, uF.parseToInt(trainingId));
			pst.setInt(4, uF.parseToInt("1"));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.executeUpdate();
			pst.close();
//			request.setAttribute("alLiveLearnings", alLiveLearnings);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
			
		}
	}
	
	
	public String getTrainingId() {
		return trainingId;
	}

	public void setTrainingId(String trainingId) {
		this.trainingId = trainingId;
	}

	public String getlPlanId() {
		return lPlanId;
	}

	public void setlPlanId(String lPlanId) {
		this.lPlanId = lPlanId;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request=request;
		
	}

}
