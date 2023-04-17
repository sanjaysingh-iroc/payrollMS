package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CandidateNotifications;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ResendOnboardingFormToCandidate extends ActionSupport implements ServletRequestAware,IConstants {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(ResendOnboardingFormToCandidate.class);

	String strSessionEmpId = null;

	String depart_id;
	String candidateId;
	String recruitId;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, "Resend Onboarding Form To Candidate");
		request.setAttribute(PAGE, "/jsp/recruitment/ResendOnboardingFormToCandidate.jsp");
		
		sendMail();
		
		return SUCCESS;

	}
	
	
	public void sendMail() {

		Connection con = null;
		ResultSet rst = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmCandiInfo = getCandiInfoMap(con, false);
			System.out.println("hmCandiInfo ===>>>> " +hmCandiInfo);
				Map<String, String> hmCandiInner = hmCandiInfo.get(getCandidateId());
				System.out.println("getCandidateId() =====>> " + getCandidateId() + "  hmCandiInner =====>> " +hmCandiInner);
				Map<String, String> hmCandiDesig = new HashMap<String, String>();
				
				pst = con.prepareStatement("select e.emp_per_id,d.designation_code,d.designation_name from recruitment_details r," +
						"designation_details d,candidate_personal_details e where r.recruitment_id = e.recruitment_id and " +
						"r.designation_id=d.designation_id and emp_per_id = ?");
				pst.setInt(1, uF.parseToInt(getCandidateId()));
				System.out.println("pst : "+pst.toString());
				rst = pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
				while(rst.next()) {
					hmCandiDesig.put(rst.getString("emp_per_id"), rst.getString("designation_name"));
				}
				rst.close();
				pst.close();
				
				//Start Dattatray Date:10-08-21
				String joining_date = "";
				String job_title = "";
				pst = con.prepareStatement("select cad.candidate_joining_date,job_title from candidate_application_details cad,recruitment_details rd where rd.recruitment_id =cad.recruitment_id and  cad.candidate_id = ? and cad.recruitment_id = ?");
				pst.setInt(1, uF.parseToInt(getCandidateId()));
				pst.setInt(2, uF.parseToInt(getRecruitId()));
				rst = pst.executeQuery();
				while(rst.next()) {
					joining_date = rst.getString("candidate_joining_date");
					job_title = rst.getString("job_title");
				}
				rst.close();
				pst.close();
				//End Dattatray Date:10-08-21
//				System.out.println("candidate_joining_date : "+joining_date);
//				System.out.println("getCandidateId() ========= "+getCandidateId());
				String strDomain = request.getServerName().split("\\.")[0];
				CandidateNotifications nF = new CandidateNotifications(N_CANDI_ONBOARDING_CTC, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrEmpId(getCandidateId());
				nF.setStrRecruitmentId(getRecruitId());
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				 
				nF.setStrCandiFname(hmCandiInner.get("FNAME"));
				nF.setStrCandiLname(hmCandiInner.get("LNAME"));
				nF.setStrRecruitmentDesignation(hmCandiDesig.get(getCandidateId()));
				//Start Dattatray Date:10-08-21
				nF.setStrJobTitle(job_title);//Created Dattatray Date:10-08-21
				SimpleDateFormat sdf = new SimpleDateFormat(DBDATE);
				java.util.Date parse = sdf.parse(joining_date);
//				System.out.println("joining_date : "+ joining_date +"formatted : "+uF.getDateFormat(uF.getPrevDate(parse, 1).toString(), DBDATE, DATE_FORMAT));
				nF.setStrCandiJoiningDate(uF.getDateFormat(uF.getPrevDate(parse, 1).toString(), DBDATE, DATE_FORMAT));//Created Dattatray Date:10-08-21
				//End Dattatray Date:10-08-21
				nF.setOnboardingData("?depart_id="+getDepart_id()+"&candidateId="+getCandidateId()+"&recruitId="+getRecruitId());
				nF.sendNotifications();
			 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public Map<String, Map<String, String>> getCandiInfoMap(Connection con, boolean isFamilyInfo) {
		Map<String, Map<String, String>> hmCandiInfo = new HashMap<String, Map<String, String>>();
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			Map<String, String> hmCandiInner = new HashMap<String, String>();
			if(isFamilyInfo) {
				pst = con.prepareStatement("select * from candidate_family_members order by emp_id");
				rs = pst.executeQuery();
				while(rs.next()) {
					hmCandiInner = hmCandiInfo.get(rs.getString("emp_id"));
					if(hmCandiInner==null)hmCandiInner=new HashMap<String, String>();
					hmCandiInner.put(rs.getString("member_type"), rs.getString("member_name"));
					hmCandiInfo.put(rs.getString("emp_id"), hmCandiInner);
				}
				rs.close();
				pst.close();
			}
			
			pst = con.prepareStatement("SELECT cpd.emp_per_id, cpd.emp_fname, cpd.emp_lname, cpd.empcode, cpd.emp_image, cpd.emp_email, " +
					"cpd.emp_date_of_birth, cad.candidate_joining_date, cpd.emp_gender, cpd.marital_status, cad.ctc_offered FROM " +
					"candidate_personal_details cpd, candidate_application_details cad where cpd.emp_per_id = cad.candidate_id order by emp_per_id");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				if (rs.getInt("emp_per_id") < 0) {
					continue;
				}
				hmCandiInner = hmCandiInfo.get(rs.getString("emp_per_id"));
				if(hmCandiInner==null)hmCandiInner=new HashMap<String, String>();

				hmCandiInner.put("FNAME", rs.getString("emp_fname"));
				hmCandiInner.put("LNAME", rs.getString("emp_lname"));
				hmCandiInner.put("FULLNAME", rs.getString("emp_lname")+" "+rs.getString("emp_lname"));
				hmCandiInner.put("EMPCODE", rs.getString("empcode"));
				hmCandiInner.put("IMAGE", rs.getString("emp_image"));
				hmCandiInner.put("EMAIL", rs.getString("emp_email"));
				hmCandiInner.put("DOB", rs.getString("emp_date_of_birth"));
				if(rs.getString("candidate_joining_date") != null) {
				hmCandiInner.put("JOINING_DATE", uF.getDateFormat(rs.getString("candidate_joining_date"), DBDATE, CF.getStrReportDateFormat()));
				} else {
					hmCandiInner.put("JOINING_DATE", "-");
				}
				hmCandiInner.put("GENDER", rs.getString("emp_gender"));
				hmCandiInner.put("MARITAL_STATUS", rs.getString("marital_status"));
				hmCandiInner.put("OFFERED_CTC", rs.getString("ctc_offered"));
				
				hmCandiInfo.put(rs.getString("emp_per_id"), hmCandiInner);
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
		return hmCandiInfo;
	}
	
	
	
	public String getDepart_id() {
		return depart_id;
	}

	public void setDepart_id(String depart_id) {
		this.depart_id = depart_id;
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


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}