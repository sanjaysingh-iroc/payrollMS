package com.konnect.jpms.recruitment;

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

public class CloseJob extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;
	private String recruitmentId;
	private String fromPage;
	private String operation;
	
	private String closeReason;
	private String from;
	
//===start parvez date: 18-01-2022===
	private String dataType;
	private String reopenReason;
//===end parvez date: 18-01-2022===	
	
	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
	
		UtilityFunctions uF = new UtilityFunctions();
		System.out.println("CJ/45--getFromPage="+getFromPage());
		
	//=====start parvez date: 18-01-2022===	
		/*if(getOperation() != null && getOperation().equals("update")) {
			
			closeJob(uF);
			
			if(getFromPage() != null && getFromPage().equals("Application")) {
				return "ASUCCESS";
			} else if(getFrom() != null && getFrom().equals("WF")){
				return VIEW;
			}else {
				return SUCCESS;
			}
		} else {
			getCloseJobReason(uF);
			return LOAD;
		}*/
		
		if(getDataType() != null && getDataType().equals("reopen")){
			System.out.println("CJ/70--rReason="+getReopenReason());
			if(getOperation() != null && getOperation().equals("update")){
				reOpenJob(uF);
				return SUCCESS;
			}else{
				return LOAD;
			}
			
		} else if(getDataType() != null && !getDataType().equals("reopen") && getOperation() != null && getOperation().equals("update")) {
			System.out.println("CJ/74--closeJob()");
			closeJob(uF);
			
			if(getFromPage() != null && getFromPage().equals("Application")) {
				System.out.println("CJ/78");
				return "ASUCCESS";
			} else if(getFrom() != null && getFrom().equals("WF")){
				System.out.println("CJ/81");
				return VIEW;
			}else {
				System.out.println("CJ/84");
				return SUCCESS;
			}
		} else {
			System.out.println("CJ/88");
			getCloseJobReason(uF);
			return LOAD;
		}
	//===start parvez date: 18-01-2022===	
		
	}
	
	
	private void getCloseJobReason(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select close_job_reason from recruitment_details where recruitment_id = ? and close_job_status = true");
			pst.setInt(1, uF.parseToInt(getRecruitmentId()));
			rst = pst.executeQuery();
			while(rst.next()) {
				setCloseReason(uF.showData(rst.getString("close_job_reason"), "-"));
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void closeJob(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update recruitment_details set close_job_status = true, close_job_reason = ? where recruitment_id = ?");
			pst.setString(1, getCloseReason());
			pst.setInt(2, uF.parseToInt(getRecruitmentId()));
			pst.executeUpdate();
			pst.close();
			
			String recruitName = CF.getRecruitmentNameById(con, uF, getRecruitmentId());
			session.setAttribute(MESSAGE, SUCCESSM + "" + uF.showData(recruitName, "") + " job has been closed successfully." + END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void reOpenJob(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update recruitment_details set close_job_status = false, reopen_job_reason = ? where recruitment_id = ?");
			pst.setString(1, getReopenReason());
			pst.setInt(2, uF.parseToInt(getRecruitmentId()));
			pst.executeUpdate();
			pst.close();
			System.out.println("CJ/162--pst="+pst);
			String recruitName = CF.getRecruitmentNameById(con, uF, getRecruitmentId());
			session.setAttribute(MESSAGE, SUCCESSM + "" + uF.showData(recruitName, "") + " job has been re-opened successfully." + END);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String getRecruitmentId() {
		return recruitmentId;
	}

	public void setRecruitmentId(String recruitmentId) {
		this.recruitmentId = recruitmentId;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getCloseReason() {
		return closeReason;
	}

	public void setCloseReason(String closeReason) {
		this.closeReason = closeReason;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}


	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}


	public String getFrom() {
		return from;
	}


	public void setFrom(String from) {
		this.from = from;
	}

//===start parvez date: 18-01-2022===
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public String getReopenReason() {
		return reopenReason;
	}

	public void setReopenReason(String reopenReason) {
		this.reopenReason = reopenReason;
	}
//===end parvez date: 18-01-2022===	


}
