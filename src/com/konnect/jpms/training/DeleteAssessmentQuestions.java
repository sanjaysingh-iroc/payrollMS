package com.konnect.jpms.training;

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

public class DeleteAssessmentQuestions extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request; 
	String strUserType = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;
	private String delId;
	private String type;
	
	
	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("type ====> " + type);
//		System.out.println("delId ====> " + delId);
		if(type != null && type.equals("assess")) {
			deleteAssessmentQuestions(uF);
		}
		return LOAD;
	}
	
	

	public void deleteAssessmentQuestions(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
//			System.out.println("Type ===> "+getType());
//			System.out.println("delID ===> "+getDelId());
				pst = con.prepareStatement("delete from assessment_question_details where assessment_question_id = ?");
				pst.setInt(1, uF.parseToInt(getDelId()));
				pst.executeUpdate();
				pst.close();
				
//				System.out.println("delID pst ===> "+ pst);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public String getDelId() {
		return delId;
	}

	public void setDelId(String delId) {
		this.delId = delId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
