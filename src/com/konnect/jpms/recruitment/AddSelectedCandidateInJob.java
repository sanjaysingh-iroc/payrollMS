package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddSelectedCandidateInJob extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId = null;
	String strUserType = null;
	CommonFunctions CF = null;
	
 
	String candidateId;
//	String jobCode;
	String type;
	String[] jobCode;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
  
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

//		System.out.println("getCandidateId() =-===>>> " + getCandidateId());
		getLiveJobs(uF);
		
		String submit = request.getParameter("submit");
		if(submit != null && !submit.equals("")){
			addSelectedCandidateInJob(uF);
			return SUCCESS;
		}
		return LOAD;
	}
	
	
private void addSelectedCandidateInJob(UtilityFunctions uF) {
	
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		con=db.makeConnection(con);
		//String recruitID = request.getParameter("jobCode");
		
		if(getJobCode() != null) {
			//System.out.println("getJobCode().length ===> "+getJobCode().length);
			for(int a=0; a<getJobCode().length; a++) {
				//System.out.println("getJobCode() ===> "+getJobCode());
				if(uF.parseToInt(getJobCode()[a])>0){
					String jobcodeName = null;
					pst = con.prepareStatement("select job_code from recruitment_details where recruitment_id= ? ");
					pst.setInt(1, uF.parseToInt(getJobCode()[a]));
					rs = pst.executeQuery();
		//			System.out.println("new Date ===> " + new Date());
					while (rs.next()) {
						jobcodeName = rs.getString("job_code");
					}
					rs.close();
					pst.close();
					
					StringBuilder sbCandiNames = null;
					List<String> candiIdList = Arrays.asList(getCandidateId().split(","));
					
					for(int i=0; candiIdList != null && !candiIdList.isEmpty() && i<candiIdList.size(); i++) {
						if(!candiIdList.get(i).equals("")) {
							boolean checkJobCodeFlag = false;
							pst = con.prepareStatement("select job_code from candidate_application_details where recruitment_id= ? and candidate_id =?");
							pst.setInt(1, uF.parseToInt(getJobCode()[a]));
							pst.setInt(2, uF.parseToInt(candiIdList.get(i)));
							rs = pst.executeQuery();
				//			System.out.println("new Date ===> " + new Date());
							while (rs.next()) {
								checkJobCodeFlag = true;
							}
							rs.close();
							pst.close();
							
				//			System.out.println("checkJobCodeFlag ===> " + checkJobCodeFlag);
							if(!checkJobCodeFlag) {
								pst=con.prepareStatement("insert into candidate_application_details (candidate_id,recruitment_id,job_code,application_date," +
										"added_by,entry_date,source_type) values(?,?,?,?, ?,?,?)");
								pst.setInt(1, uF.parseToInt(candiIdList.get(i)));
								pst.setInt(2, uF.parseToInt(getJobCode()[a]));
								pst.setString(3, jobcodeName);
								pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(5, uF.parseToInt(strSessionEmpId));
								pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
								if(strUserType != null && strUserType.equals(RECRUITER)) {
									pst.setInt(7, SOURCE_RECRUITER);
								} else {
									pst.setInt(7, SOURCE_HR);
								}
								pst.executeUpdate();
								pst.close();
								
								if(sbCandiNames == null) {
									sbCandiNames = new StringBuilder();
									sbCandiNames.append(CF.getCandiNameByCandiId(con, candiIdList.get(i)));
								} else {
									sbCandiNames.append(", " + CF.getCandiNameByCandiId(con, candiIdList.get(i)));
								}
								
								pst=con.prepareStatement("delete from candidate_activity_details where recruitment_id=? and candi_id=? and user_id=? and " +
										"activity_id = ?");
								pst.setInt(1, uF.parseToInt(getJobCode()[a]));
								pst.setInt(2, uF.parseToInt(candiIdList.get(i)));
								pst.setInt(3, uF.parseToInt(strSessionEmpId));
								pst.setInt(4, CANDI_ACTIVITY_APPLY_ID);
								pst.executeUpdate();
								pst.close();
								
								pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
								pst.setInt(1, uF.parseToInt(getJobCode()[a]));
								pst.setInt(2, uF.parseToInt(candiIdList.get(i)));
								pst.setString(3, "Apply for Job");
								pst.setInt(4, uF.parseToInt(strSessionEmpId));
								pst.setDate(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
								pst.setInt(6, CANDI_ACTIVITY_APPLY_ID);
								pst.execute();
								pst.close();
						
							}
						}
					}
					if(sbCandiNames == null) {
						sbCandiNames = new StringBuilder();
					}
					if(sbCandiNames != null && !sbCandiNames.toString().equals("")) {
						session.setAttribute(MESSAGE, SUCCESSM+""+sbCandiNames.toString()+" are added in "+jobcodeName+" successfully."+END);
					} else {
						session.setAttribute(MESSAGE, ERRORM+"These candidates are already added in "+jobcodeName+"."+END);
					}
				}
			}	
		}
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}


private void getLiveJobs(UtilityFunctions uF){

	Connection con = null;
	PreparedStatement pst = null;
	Database db = new Database();
	db.setRequest(request);
	ResultSet rs = null;
	
	try {
		StringBuilder sb = new StringBuilder("");
//		int queCnt=1;
		con = db.makeConnection(con);
		
		List<String> candiIdList = Arrays.asList(getCandidateId().split(","));
		StringBuilder sbCandiNames = null;
		for(int i=0; candiIdList != null && !candiIdList.isEmpty() && i<candiIdList.size(); i++) {
			if(!candiIdList.get(i).equals("")) {
				if(sbCandiNames == null) {
					sbCandiNames = new StringBuilder();
					sbCandiNames.append(CF.getCandiNameByCandiId(con, candiIdList.get(i)));
				} else {
					sbCandiNames.append(", " + CF.getCandiNameByCandiId(con, candiIdList.get(i)));
				}
			}
		}
		if(sbCandiNames == null) {
			sbCandiNames = new StringBuilder();
		}
		request.setAttribute("sbCandiNames", sbCandiNames.toString());
		
//		pst = con.prepareStatement("select * from recruitment_details where close_job_status=false and job_approval_status = 1 and " +
//				"recruitment_id not in (select recruitment_id from candidate_application_details where candidate_id = ?)");
		pst = con.prepareStatement("select * from recruitment_details where close_job_status=false and job_approval_status = 1 ");
//		pst.setInt(1, uF.parseToInt(candidateId));
		rs = pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		while (rs.next()) {
			sb.append("<option value=\"" + rs.getString("recruitment_id") + "\">" +rs.getString("job_title")+ "["+rs.getString("job_code")+"]" + "</option>");
		}
		rs.close();
		pst.close();
		request.setAttribute("option", sb.toString());
//		request.setAttribute("queCnt", queCnt);

	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}
	


public List<String> getListData(String strData){
	List<String> dataList = new ArrayList<String>();
	if(strData != null && !strData.equals("")){
		dataList = Arrays.asList(strData.split(","));
	}
	
	return dataList;
}


	public String getCandidateId() {
	return candidateId;
	}
	
	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}

	public String[] getJobCode() {
		return jobCode;
	}


	public void setJobCode(String[] jobCode) {
		this.jobCode = jobCode;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
