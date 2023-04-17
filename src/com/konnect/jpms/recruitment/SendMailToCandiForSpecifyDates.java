package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CandidateNotifications;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SendMailToCandiForSpecifyDates extends ActionSupport implements ServletRequestAware, IStatements  {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	HttpSession session;
	public CommonFunctions CF;
	String strUserType = null;
	String strSessionEmpId = null;
	String empId;
	String recruitId;
	String roundId;
	String assessmentId;
	String type;
	
	public String execute() throws Exception {
		
//		emp_id = (String) request.getParameter("EMPID");
		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
//			System.out.println("emp_id === > "+emp_id);
		if(getType() != null && getType().equals("SpecifyDates")) {
			updateNotiStatus();
			sendMail();
			return SUCCESS;
		} else if(getType() != null && getType().equals("Assessment")) {
			sendAssessmentOnCandidateMail();
			return "mailsent";
		}
		return SUCCESS;
	}

	

//	String emp_id;
	String positions;
	String job_code;
	String Level_name;
	String designation_name;
	String grade_name;
	String location_name;
	String skills_name;
	String services;
	String min_exp;
	String max_exp;
	String min_education;
	
	public void updateNotiStatus() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update candidate_personal_details set send_notification_status = 1 where emp_per_id = ? ");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.executeUpdate();
			pst.close();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void sendAssessmentOnCandidateMail() {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmCandiInfo = CF.getCandiInfoMap(con, false);

			//System.out.println("hmCandiInfo ===>> " + hmCandiInfo);
			Map<String, String> hmCandiInner = hmCandiInfo.get(getEmpId());
			//System.out.println("hmCandiInner ===>> " + hmCandiInner);
			
			String strDomain = request.getServerName().split("\\.")[0];	
			CandidateNotifications nF = new CandidateNotifications(N_CANDI_TAKE_ASSESSMENT, CF);
			nF.setDomain(strDomain);
			nF.request = request;
//			System.out.println("applicationIdsLst.get(i) is ========= "+applicationIdsLst.get(i));
			nF.setStrEmpId(getEmpId());
			nF.setStrRecruitmentId(getRecruitId());
//			System.out.println("CF.getStrEmailLocalHost() is ========= "+CF.getStrEmailLocalHost());
//			System.out.println("request.getContextPath() is ========= "+request.getContextPath());
			 nF.setStrHostAddress(CF.getStrEmailLocalHost());
			 nF.setStrHostPort(CF.getStrHostPort());
			 nF.setStrContextPath(request.getContextPath());
			
			 nF.setStrCandiFname(hmCandiInner.get("FNAME"));
			 nF.setStrCandiLname(hmCandiInner.get("LNAME"));
//			 nF.setStrRecruitmentDesignation(hmCandiDesig.get(getEmp_id()));
			 nF.setStrCandiTakeAssessLink("?candidateId="+getEmpId()+"&recruitId="+getRecruitId()+"&roundId="+getRoundId()+"&assessmentId="+getAssessmentId());
			 
			 nF.sendNotifications();
			 request.setAttribute("STATUS_MSG", "mail sent successfully!");
			 System.out.println("sendNotifications ===>> ");
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeConnection(con);
		}
	}
	
	
	public void sendMail() {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmCandiInfo = CF.getCandiInfoMap(con, false);

				Map<String, String> hmCandiInner = hmCandiInfo.get(getEmpId());
				
//				Map<String, String> hmCandiDesig = new HashMap<String, String>();
//			
//				pst = con.prepareStatement("select e.emp_per_id,d.designation_code,d.designation_name from recruitment_details r," +
//						"designation_details d,candidate_personal_details e where r.recruitment_id = e.recruitment_id and " +
//						"r.designation_id=d.designation_id and emp_per_id = ?");
//				pst.setInt(1, uF.parseToInt(getEmp_id()));
//				rst = pst.executeQuery();
//				while(rst.next()){
//					
//					hmCandiDesig.put(rst.getString("emp_per_id"), rst.getString("designation_name"));
//				}
			
			String strDomain = request.getServerName().split("\\.")[0];	
			CandidateNotifications nF = new CandidateNotifications(N_CANDI_SPECIFY_OTHER_DATE, CF);
			nF.setDomain(strDomain);
			nF.request = request;
//			System.out.println("applicationIdsLst.get(i) is ========= "+applicationIdsLst.get(i));
			nF.setStrEmpId(getEmpId());
//			System.out.println("CF.getStrEmailLocalHost() is ========= "+CF.getStrEmailLocalHost());
//			System.out.println("request.getContextPath() is ========= "+request.getContextPath());
			 nF.setStrHostAddress(CF.getStrEmailLocalHost());
			 nF.setStrHostPort(CF.getStrHostPort());
			 nF.setStrContextPath(request.getContextPath());
			
			 nF.setStrCandiFname(hmCandiInner.get("FNAME"));
			 nF.setStrCandiLname(hmCandiInner.get("LNAME"));
//			 nF.setStrRecruitmentDesignation(hmCandiDesig.get(getEmp_id()));
			 nF.setStrAddCandidateStep8("?CandidateId="+getEmpId()+"&step=8&operation=U&mode=profile&type=type&candibymail=yes");
			 
			 nF.sendNotifications();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeConnection(con);
		}
	}
	
	
//	public Map<String, Map<String, String>> getCandiInfoMap(Connection con, boolean isFamilyInfo) {
//		Map<String, Map<String, String>> hmCandiInfo = new HashMap<String, Map<String, String>>();
//
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		try {
//			Map<String, String> hmCandiInner = new HashMap<String, String>();
//			if(isFamilyInfo){
//				pst = con.prepareStatement("select * from candidate_family_members order by emp_id");
//				rs = pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
//				while(rs.next()){
//					
//					hmCandiInner = hmCandiInfo.get(rs.getString("emp_id"));
//					if(hmCandiInner==null)hmCandiInner=new HashMap<String, String>();
//					
//					hmCandiInner.put(rs.getString("member_type"), rs.getString("member_name"));
//					hmCandiInfo.put(rs.getString("emp_id"), hmCandiInner);
//				}
//			}
//			
//			
//			pst = con.prepareStatement("SELECT emp_per_id, emp_fname, emp_lname, empcode, emp_image, emp_email, emp_date_of_birth, candidate_joining_date, " +
//					"emp_gender, marital_status FROM candidate_personal_details order by emp_per_id");
//			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			while (rs.next()) {
//				if (rs.getInt("emp_per_id") < 0) {
//					continue;
//				}
//				hmCandiInner = hmCandiInfo.get(rs.getString("emp_per_id"));
//				if(hmCandiInner==null)hmCandiInner=new HashMap<String, String>();
//
//				hmCandiInner.put("FNAME", rs.getString("emp_fname"));
//				hmCandiInner.put("LNAME", rs.getString("emp_lname"));
//				hmCandiInner.put("FULLNAME", rs.getString("emp_lname")+" "+rs.getString("emp_lname"));
//				hmCandiInner.put("EMPCODE", rs.getString("empcode"));
//				hmCandiInner.put("IMAGE", rs.getString("emp_image"));
//				hmCandiInner.put("EMAIL", rs.getString("emp_email"));
//				hmCandiInner.put("DOB", rs.getString("emp_date_of_birth"));
//				hmCandiInner.put("JOINING_DATE", rs.getString("candidate_joining_date"));
//				hmCandiInner.put("GENDER", rs.getString("emp_gender"));
//				hmCandiInner.put("MARITAL_STATUS", rs.getString("marital_status"));
//				 
//				hmCandiInfo.put(rs.getString("emp_per_id"), hmCandiInner);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			//log.error(e.getClass() + ": " + e.getMessage(), e);
//		}
//		return hmCandiInfo;
//	}
	
	public String getRecruitId() {
		return recruitId;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getRoundId() {
		return roundId;
	}

	public void setRoundId(String roundId) {
		this.roundId = roundId;
	}

	public String getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}
