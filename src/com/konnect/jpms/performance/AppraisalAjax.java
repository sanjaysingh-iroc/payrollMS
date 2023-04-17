package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class AppraisalAjax implements IStatements,ServletRequestAware, SessionAware {

	private int id;
	private int empId;
	public int getEmpId() {
		return empId;
	}

	public void setEmpId(int empId) {
		this.empId = empId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	String operation;
	Map session;
	private HttpServletRequest request;

	public String execute() throws Exception {
		UtilityFunctions uF=new UtilityFunctions();

		if (getOperation().equals("A"))
			approvedAppraisal(uF);
		
		
		return "success";

	}
	
	public void approvedAppraisal(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);

			pst = con.prepareStatement("insert into appraisal_approval(appraisal_id,emp_id,user_id,user_type_id,status) values(?,?,?,?,?)");
			pst.setInt(1, getId());
			pst.setInt(2, getEmpId());
			pst.setInt(3,uF.parseToInt((String)session.get(EMPID)));
			pst.setInt(4,uF.parseToInt((String)session.get(USERTYPEID)));
			pst.setBoolean(5,true);
			pst.execute();
			pst.close();
			
			request.setAttribute("STATUS_MSG","Approved");

		} catch (Exception e) {
			request.setAttribute("STATUS_MSG","Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	
	}

	@Override
	public void setSession(Map session) {
		this.session=session;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
		// TODO Auto-generated method stub
		
	}


}
