package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateTargetRemark extends ActionSupport implements ServletRequestAware,IStatements{

	HttpSession session;
	
	String targetID;
	
	String strSessionEmpId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF;
	
	private String targetRemark;
//	String empid;
//	String form;
	
	private static final long serialVersionUID = 1L;

	public String execute() {
		 
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		UtilityFunctions uF=new UtilityFunctions();
		String update = request.getParameter("submit");
//		System.out.println("targetRemark ===> "+getTargetRemark());
//		System.out.println("targetID ===> "+getTargetID());
//		System.out.println("update ===> "+update);
//		if(update != null && update.equals("Save")){
			updatetargetRemark(uF);
			/*if(form != null && form.equals("GTarget")){
				return "GSUCCESS";
			}else{
				return "KSUCCESS";
			}*/
			return "SUCCESS";
//		}else {
//		 return LOAD;
//		}
//		return LOAD;
	}
	
	
	private void updatetargetRemark(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
//		String targetRemark = request.getParameter("targetRemark");
//		System.out.println("targetRemark 11 ===> "+targetRemark);
//		System.out.println("getEmptmptarget() ===> "+getEmptmptarget());
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update target_details set target_remark = ? where target_id = ?");
			pst.setString(1, targetRemark);
			pst.setInt(2, uF.parseToInt(getTargetID()));
			pst.execute();
			pst.close();
//			System.out.println("pst =====> "+pst);
			request.setAttribute("LastRemark", targetRemark);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	/*
	public String getEmpid() {
		return empid;
	}
	public void setEmpid(String empid) {
		this.empid = empid;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}*/

	public String getTargetID() {
		return targetID;
	}
	public void setTargetID(String targetID) {
		this.targetID = targetID;
	}

	public String getTargetRemark() {
		return targetRemark;
	}

	public void setTargetRemark(String targetRemark) {
		this.targetRemark = targetRemark;
	}



	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}	

}
