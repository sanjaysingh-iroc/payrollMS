package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CandidateNotifications;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddCandidateBackgroundDetails extends ActionSupport implements ServletRequestAware, IStatements  {
	String strSessionEmpId;
	HttpSession session;
	CommonFunctions CF;
	private String candidateId;
	private String recruitId;
	private String email;
	private String fname;
	private String lname;
	private String message;

	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			CF = new CommonFunctions();
			CF.setRequest(request);
			CF.getCommonFunctionsDetails(CF,request);
		}
		request.setAttribute(PAGE, "/jsp/recruitment/AddCandidateBackgroundDetails.jsp");
		request.setAttribute(TITLE, "Add Candidate Background Details");
		
		sendEmailToCandidate();
		return SUCCESS;
	}
	
	private void sendEmailToCandidate() {
//		System.out.println("sendEmailToCandidate::");
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
	//	int empId = 0;
		String username = "";
		String password = "";
		String updateMsg = null;
		StringBuilder sbAllData = new StringBuilder();
		
		try {
			con = db.makeConnection(con);
			//pst = con.prepareStatement("select * from candidate_personal_details where emp_email = ?");
			pst = con.prepareStatement("select * from candidate_personal_details where emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
//			System.out.println("pstt=====>"+pst);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			boolean isEmailExist = false;
			while(rs.next()){
				email = rs.getString("emp_email");
				fname = rs.getString("emp_fname");
				lname = rs.getString("emp_lname");
			}
			if(getEmail() != null) {
				isEmailExist = true;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from recruitment_details where recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("pstt=====>"+pst);
			String jobCode = null;
			while(rs.next()){
				jobCode = rs.getString("job_code");
			}
			rs.close();
			pst.close();
			
//			String reqSessionId = request.getSession().getId();
			String strSessionId = session.getId();
			pst = con.prepareStatement("update candidate_personal_details set session_id=? where emp_per_id=?");
			pst.setString(1, strSessionId);
			pst.setInt(2, uF.parseToInt(getCandidateId()));
			System.out.println("pstt=====>"+pst);
			pst.executeUpdate();
			
//			System.out.println("Send notifications=====>");
			String strDomain = request.getServerName().split("\\.")[0];	
			CandidateNotifications nF = new CandidateNotifications(N_NEW_CADIDATE_BACKGROUND_VERIFICATION, CF); 
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrEmpId(candidateId+"");
			nF.setStrRecruitmentId(getRecruitId());
			nF.setStrAddCandiLink("?CandidateId="+getCandidateId()+"&sessionId="+strSessionId+"&recruitId="+getRecruitId()+"&operation=U&step=4&mode=profile&type=type&frombgverify=1&org_id="+session.getAttribute(ORGID));
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			
			
//			System.out.println("getEmail() ===> "+getEmail());
			nF.setStrEmailTo(getEmail());
			nF.setStrCandiFname(getFname());
			nF.setStrCandiLname(getLname());
		//	nF.setStrLegalEntityName(CF.getOrgNameById(con, (String)session.getAttribute(ORGID)));
			nF.setStrEmailSubject("Please fill up the Employee Form.");
//			System.out.println("send notifications");
			nF.sendNotifications();
			setMessage("Email has been sent to the Employee with induction form link.");
			session.setAttribute(MESSAGE, SUCCESSM +"Email has been sent to the Employee with induction form link"+ END );

			setFname("");
			setLname("");
			setEmail("");
			
			request.setAttribute("STATUS_MSG", "Mail has been sent to the candidate with verification form link.");
			//request.setAttribute("data", sbAllData.toString());	

		//	N_NEW_CADIDATE_BACKGROUND_VERIFICATION
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
				
	}
	
	private HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	public String getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
}