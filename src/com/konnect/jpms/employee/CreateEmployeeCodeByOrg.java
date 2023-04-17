package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CreateEmployeeCodeByOrg extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	HttpSession session;
	
	String strOrg;
	
	Boolean autoGenerate = false;
	String empContractor;
	String empCodeAlphabet;
	String empCodeNumber;
	String validReqOpt;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
//		System.out.println("empContractor ===>> " + getEmpContractor());
//		System.out.println("strOrg ===>> " + getStrOrg());
		
		generateEmpCode();
		
		return SUCCESS;
	}

	
	private void generateEmpCode() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String empCodeAlpha = "" , empCodeNum = ""; 
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("SELECT emp_code_auto_generate,emp_code_alpha,contractor_code_alpha,emp_code_numeric FROM org_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(getStrOrg()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				setAutoGenerate(uF.parseToBoolean(rs.getString("emp_code_auto_generate")));
				empCodeAlpha = rs.getString("emp_code_alpha");
				if(uF.parseToInt(getEmpContractor()) == 2) {
					empCodeAlpha = rs.getString("contractor_code_alpha");
				}
				empCodeNum = rs.getString("emp_code_numeric");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT empcode FROM employee_personal_details where empcode like ? order by emp_per_id desc limit 1");
			pst.setString(1, empCodeAlpha+"%");
			rs = pst.executeQuery();
			boolean empcodeFlag = false;
			while(rs.next()) {
				empcodeFlag = true;
				String strEmpCode = rs.getString("empcode");
				String strEmpCodeNum = strEmpCode.substring(empCodeAlpha.length(), strEmpCode.length());
//					log.debug("code Number===>"+strEmpCodeNum);
				empCodeNum = (uF.parseToInt(strEmpCodeNum)+1) + "";
				//System.out.println("empCodeNum ===>> " + empCodeNum);
				getLatestEmpCode(con, uF, empCodeAlpha, empCodeNum);
			}
			rs.close();
			pst.close();
			
			if(!empcodeFlag) {
				setEmpCodeAlphabet(empCodeAlpha);
				setEmpCodeNumber(empCodeNum);
				request.setAttribute("EMP_CODE_ALPHA", uF.showData(getEmpCodeAlphabet(), ""));
				request.setAttribute("EMP_CODE_NUM", uF.showData(getEmpCodeNumber(), ""));
			}
//				System.out.println("generateEmpCode final ----- getEmpCodeAlphabet() ===>> " + getEmpCodeAlphabet() + "  getEmpCodeNumber() ===>> " + getEmpCodeNumber());
//
//			request.setAttribute("EMPLOYEE_CODE", empCodeAlpha+empCodeNum);
//			setAutoGenerate(uF.parseToBoolean(hmSettings.get("EMP_CODE_AUTO_GENERATION")));
			/***
			 * This position of code changed on 26-04-2012 for always displaying the auto generated code
			 */
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}



	private void getLatestEmpCode(Connection con, UtilityFunctions uF, String empCodeAlpha, String empCodeNum) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			boolean flag = false;
			pst = con.prepareStatement("SELECT empcode FROM employee_personal_details where empcode = ? ");
			pst.setString(1, empCodeAlpha+empCodeNum);
			rs = pst.executeQuery();
			
			while(rs.next()) {
				flag = true;
				String strEmpCode = rs.getString("empcode");
				String strEmpCodeNum = strEmpCode.substring(empCodeAlpha.length(), strEmpCode.length());
				
				empCodeNum = (uF.parseToInt(strEmpCodeNum)+1) + "";
//				System.out.println("generateEmpCode empCodeNum ===>> " + empCodeNum);
				getLatestEmpCode(con, uF, empCodeAlpha, empCodeNum);
			}
			rs.close();
			pst.close();
			
			if(!flag) {
//				System.out.println("empCodeNum 111 ===>> " + empCodeNum);
				setEmpCodeAlphabet(empCodeAlpha);
				setEmpCodeNumber(empCodeNum);
				request.setAttribute("EMP_CODE_ALPHA", uF.showData(getEmpCodeAlphabet(), ""));
				request.setAttribute("EMP_CODE_NUM", uF.showData(getEmpCodeNumber(), ""));
			}
		
		/***
		 * This position of code changed on 26-04-2012 for always displaying the auto generated code
		 */
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}


	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public Boolean getAutoGenerate() {
		return autoGenerate;
	}

	public void setAutoGenerate(Boolean autoGenerate) {
		this.autoGenerate = autoGenerate;
	}

	public String getEmpContractor() {
		return empContractor;
	}

	public void setEmpContractor(String empContractor) {
		this.empContractor = empContractor;
	}

	public String getEmpCodeAlphabet() {
		return empCodeAlphabet;
	}

	public void setEmpCodeAlphabet(String empCodeAlphabet) {
		this.empCodeAlphabet = empCodeAlphabet;
	}

	public String getEmpCodeNumber() {
		return empCodeNumber;
	}

	public void setEmpCodeNumber(String empCodeNumber) {
		this.empCodeNumber = empCodeNumber;
	}

	public String getValidReqOpt() {
		return validReqOpt;
	}

	public void setValidReqOpt(String validReqOpt) {
		this.validReqOpt = validReqOpt;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}