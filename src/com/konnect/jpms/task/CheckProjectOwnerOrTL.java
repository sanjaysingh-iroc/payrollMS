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
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CheckProjectOwnerOrTL extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	CommonFunctions CF;
	HttpSession session;
	String strSessionEmpId;
	
	String projectId;
	
	public String execute() throws Exception {
		UtilityFunctions uF=new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		if(uF.parseToInt(getProjectId())>0) {
			checkSessionEmpIsProjectOwnerOrTL(uF);
		}
		return SUCCESS; 

	}
	
	
	private void checkSessionEmpIsProjectOwnerOrTL(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String strProOwnerOrTL = "0";
		try {
			con = db.makeConnection(con);
			
			boolean flag = CF.getFeatureManagementStatus(request, uF, F_SHOW_ALL_PRO_DATA_TO_TL);
			pst = con.prepareStatement("select * from project_emp_details where _isteamlead=true and pro_id=? and emp_id=? ");
			pst.setInt(1, uF.parseToInt(getProjectId()));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
				if(!flag) {
					strProOwnerOrTL = "2";
				}
			}
			rs.close();
			pst.close();
			
		//===start parvez date: 12-10-2022===	
//			pst = con.prepareStatement("select * from projectmntnc where pro_id=? and project_owner=? ");
			pst = con.prepareStatement("select * from projectmntnc where pro_id=? and project_owners like '%,"+strSessionEmpId+",%'");
			pst.setInt(1, uF.parseToInt(getProjectId()));
//			pst.setInt(2, uF.parseToInt(strSessionEmpId)); 
		//===end parvez date: 12-10-2022===	
			rs = pst.executeQuery();
			while (rs.next()) {
				strProOwnerOrTL = "1";
			}
			rs.close();
			pst.close();
			
// 		========================================================= End =========================================================			
		
//			String msg = count+"::::"+ownerEmail+"::::"+ownerSign+"::::"+wLocation+"::::"+locationTel+"::::"+locationFax+"::::"+orgPan
//				+"::::"+orgMCARegNo+"::::"+orgSTRegNo; //+"::::"+locationECCNo2
			request.setAttribute("STATUS_MSG", strProOwnerOrTL);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}


	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
