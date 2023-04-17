package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmailValidation extends ActionSupport implements IStatements, ServletRequestAware{

	private static final long serialVersionUID = 1L;
	
	public HttpSession session;
	public CommonFunctions CF = null;
	
	private String email;
	private String empCodeAlphabet;
	private String cemail;
	private String candiEmail;
	private String trainerEmail;
	private String empId;
	private String biometricId;
	private String strEmpOrContractor;
	private String empCode;
	private String strOrientName;
	

	public String getBiometricId() {
		return biometricId;
	}

	public void setBiometricId(String biometricId) {
		this.biometricId = biometricId;
	}

	public String getEmpCodeAlphabet() {
		return empCodeAlphabet;
	}

	public void setEmpCodeAlphabet(String empCodeAlphabet) {
		this.empCodeAlphabet = empCodeAlphabet;
	}

	public String execute() throws Exception {
		 
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) {
			CF = new CommonFunctions();
			CF.setRequest(request); 
		} 
		
		if(email!=null) {
			checkEmailValidator();
			
		} else if(empCodeAlphabet!=null) {
			empCodeValidator();
			
		} else if(cemail!=null) {
			checkCEmailValidator();
			
		} else if(candiEmail!=null) {
			checkCandidateEmailValidator();
			
		} else if(trainerEmail!=null) {
			checkTrainerEmailValidator();
			
		} else if(biometricId!=null) {
			checkMachineCodeValidator();
			
		} else if(strEmpOrContractor!=null) {
			checkEmpOrContractorCode();
			
		} else if(empCode!=null) {
			checkEmployeeCode();
			
		} else if(strOrientName!=null) {
			checkOrientName();
		}
		
		return SUCCESS;
	}

	
	private void checkOrientName() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			boolean flag = false;
			pst = con.prepareStatement("select apparisal_orientation_id from apparisal_orientation where orientation_name=?");
			pst.setString(1, getStrOrientName());
			rs=pst.executeQuery();
			if(rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			if(flag) {
				request.setAttribute("STATUS_MSG", "<b><font color=\"red\">This Name Exists.Kindly type different Name.</font></b>");
			} else {
				request.setAttribute("STATUS_MSG", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void checkEmployeeCode() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			boolean flag = false;
			String strEmpId = null;
			pst = con.prepareStatement("select emp_per_id from employee_personal_details where empcode=?");
			pst.setString(1, getEmpCode().trim());
			rs=pst.executeQuery();
			if(rs.next()) {
				flag = true;
				strEmpId = rs.getString("emp_per_id");
			}
			rs.close();
			pst.close();
			if(flag) {
				/*request.setAttribute("STATUS_MSG", flag+"::::"+strEmpId+"::::"+"<span style=\"float: left; width: 100%; line-height: 3px;\">&nbsp;</span><img src=\"images1/tick.png\">");*/
				request.setAttribute("STATUS_MSG", flag+"::::"+strEmpId+"::::"+"<span style=\"float: left; width: 100%; line-height: 3px;\">&nbsp;</span><i class=\"fa fa-check checknew\" aria-hidden=\"true\"></i>");
			} else {
				/*request.setAttribute("STATUS_MSG", flag+"::::"+strEmpId+"::::"+"<span style=\"float: left; width: 100%; line-height: 3px;\">&nbsp;</span><img src=\"images1/cross.png\">");*/
				request.setAttribute("STATUS_MSG", flag+"::::"+strEmpId+"::::"+"<span style=\"float: left; width: 100%; line-height: 3px;\">&nbsp;</span><i class=\"fa fa-times cross\" aria-hidden=\"true\"></i>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void checkEmpOrContractorCode() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			com.konnect.jpms.util.CommonFunctions CF = new com.konnect.jpms.util.CommonFunctions();
			Map<String, String> hmSettings = CF.getSettingsMap(con);
			String empCodeAlpha = hmSettings.get(O_EMP_CODE_ALPHA);
			if(uF.parseToInt(getStrEmpOrContractor()) == 2) {
				empCodeAlpha = hmSettings.get(O_CONTRACTOR_CODE_ALPHA);
			}
			String empCodeNum = hmSettings.get(O_EMP_CODE_NUM);
			
			boolean isAutoGeneration = uF.parseToBoolean(hmSettings.get(O_EMP_CODE_AUTO_GENERATION));
			if(isAutoGeneration) {
				pst = con.prepareStatement("SELECT empcode FROM employee_personal_details where empcode like ? order by emp_per_id desc limit 1");
				pst.setString(1, empCodeAlpha+"%");
				rs = pst.executeQuery();
				boolean empcodeFlag = false;
				while(rs.next()) {
					empcodeFlag = true;
					String strEmpCode = rs.getString("empcode");
					String strEmpCodeNum = "";
					strEmpCodeNum = strEmpCode.substring(empCodeAlpha.length(), strEmpCode.length());
					empCodeNum = (uF.parseToInt(strEmpCodeNum)+1) + "";

					getLatestEmpCode(con, uF, empCodeAlpha, empCodeNum);
				}
				rs.close();
				pst.close();
				
				if(!empcodeFlag) {
					String strEmpLbl = "Employee";
					if(uF.parseToInt(getStrEmpOrContractor()) == 2) {
						strEmpLbl = "Contractor";
					}
					request.setAttribute("STATUS_MSG", strEmpLbl + "::::" + empCodeAlpha + "::::" + empCodeNum);
				}
				
			} else {
				request.setAttribute("STATUS_MSG", "");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	private void getLatestEmpCode(Connection con, UtilityFunctions uF, String empCodeAlpha, String empCodeNum) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			boolean flag = false;
				pst = con.prepareStatement("SELECT empcode FROM employee_personal_details where empcode=? and is_delete=false");
				pst.setString(1, empCodeAlpha+empCodeNum);
				rs = pst.executeQuery();
				
				while(rs.next()) {
					flag = true;
					String strEmpCode = rs.getString("empcode");
					String strEmpCodeNum = "";
					strEmpCodeNum = strEmpCode.substring(empCodeAlpha.length(), strEmpCode.length());
//					String strEmpCodeNum = strEmpCode.substring(empCodeAlpha.length(), strEmpCode.length());
					empCodeNum = (uF.parseToInt(strEmpCodeNum)+1) + "";
//					System.out.println("generateEmpCode empCodeNum ===>> " + empCodeNum);
					getLatestEmpCode(con, uF, empCodeAlpha, empCodeNum);
				}
				rs.close();
				pst.close();
				
				if(!flag) {
//					setEmpCodeAlphabet(empCodeAlpha);
//					setEmpCodeNumber(empCodeNum);
						String strEmpLbl = "Employee";
					if(uF.parseToInt(getStrEmpOrContractor()) == 2) {
						strEmpLbl = "Contractor";
					}
					request.setAttribute("STATUS_MSG", strEmpLbl + "::::" + empCodeAlpha + "::::" + empCodeNum);
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

	public void checkMachineCodeValidator(){

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select biometrix_id from employee_official_details where biometrix_id=?");
			pst.setInt(1,uF.parseToInt(biometricId));
			rs=pst.executeQuery();
			if(rs.next()){
				request.setAttribute("STATUS_MSG", "<b><font color=\"red\">This Boimetric ID Exists.Kindly type Different Boimetric ID .</font></b>");
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	
	private void checkTrainerEmailValidator() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select trainer_email from trainer_personal_details where trainer_email=?");
			pst.setString(1,trainerEmail);
			rs=pst.executeQuery();
			if(rs.next()){
				request.setAttribute("STATUS_MSG", "<b><font color=\"red\">This Email Exists.Kindly type Different Email.</font></b>");
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public void checkCandidateEmailValidator(){

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);

			if(CF.getStrTimeZone()==null) {
				String strTimezone = "Asia/Calcutta";
				CF.setStrTimeZone(strTimezone);
			}
//			System.out.println("CF.getStrTimeZone() ===>> " + CF.getStrTimeZone());
			pst = con.prepareStatement("select emp_email from candidate_personal_details where upper(emp_email)=? and emp_entry_date > ?");
			pst.setString(1, candiEmail!=null ? candiEmail.toUpperCase(): "");
			pst.setTimestamp(2, uF.getTimeStamp(uF.getPrevDate(CF.getStrTimeZone(), 180)+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			if(rs.next()) {
				request.setAttribute("STATUS_MSG", "<b><font color=\"red\">This Email Exists.Kindly type Different Email.</font></b>");
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	public void checkEmailValidator() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			StringBuilder sb = new StringBuilder();
			sb.append("select emp_email from employee_personal_details where emp_email=? and is_alive=true and is_delete=false");
			if(empId != null && uF.parseToInt(empId) > 0) {
				sb.append(" and emp_per_id != "+uF.parseToInt(empId)+"");
			}
			pst = con.prepareStatement(sb.toString());
			pst.setString(1, email);
			rs=pst.executeQuery();
			if(rs.next()) {
				request.setAttribute("STATUS_MSG", "<b><font color=\"red\">This Email Exists. Kindly type Different Email.</font></b>");
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	
	public void checkCEmailValidator(){

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			StringBuilder sb = new StringBuilder();
			sb.append("select emp_email_sec from employee_personal_details where emp_email_sec=? and is_alive=true and is_delete=false");
			if(empId != null && uF.parseToInt(empId) > 0) {
				sb.append(" and emp_per_id != "+uF.parseToInt(empId)+"");
			}
			pst = con.prepareStatement(sb.toString());
			pst.setString(1, cemail);
			rs=pst.executeQuery();
			if(rs.next()){
				request.setAttribute("STATUS_MSG", "<b><font color=\"red\">This Email Exists.Kindly type different Email.</font></b>");
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	

	public String getCemail() {
		return cemail;
	}

	public void setCemail(String cemail) {
		this.cemail = cemail;
	}

	public void empCodeValidator() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select empcode from employee_personal_details where empcode=? and is_delete=false");
			pst.setString(1,empCodeAlphabet);
//			System.out.println("pst ===>> " + pst);
			rs=pst.executeQuery();
			if(rs.next()) {
				request.setAttribute("STATUS_MSG", "<b><font color=\"red\">This Code Exists.Kindly type Different Code.</font></b>");
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCandiEmail() {
		return candiEmail;
	}

	public void setCandiEmail(String candiEmail) {
		this.candiEmail = candiEmail;
	}

	public String getTrainerEmail() {
		return trainerEmail;
	}

	public void setTrainerEmail(String trainerEmail) {
		this.trainerEmail = trainerEmail;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getStrEmpOrContractor() {
		return strEmpOrContractor;
	}

	public void setStrEmpOrContractor(String strEmpOrContractor) {
		this.strEmpOrContractor = strEmpOrContractor;
	}

	public String getEmpCode() {
		return empCode;
	}

	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrOrientName() {
		return strOrientName;
	}

	public void setStrOrientName(String strOrientName) {
		this.strOrientName = strOrientName;
	}
	

}
