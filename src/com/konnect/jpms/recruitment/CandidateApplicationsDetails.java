package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CandidateApplicationsDetails extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId = null;
	String strUserType = null;
	CommonFunctions CF = null;

	String candidateId;
	// String jobCode;
	String type;
	String fromPage;
	String recruitId;
	String[] jobCode;
	String candiFinalStatus;
	String isRejected;

	String rejectStatus;//Started By Dattatray Date:08-10-21

	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		// request.setAttribute(PAGE, "/jsp/recruitment/Applications.jsp");
		// request.setAttribute(TITLE, "Applications");
		getCandiApplicationsDetails(uF);
		getCandiJobCodesList(uF);
		getLiveJobs(uF);

		String submit = request.getParameter("submit");
		// System.out.println("getFromPage() ===> " + getFromPage());
		 System.out.println("getCandidateId() ===> " + getCandidateId());

		if ((submit != null && !submit.equals("")) || (getFromPage() != null && getFromPage().equals("AC"))) {
			addCandidateInNewJob(uF);
			if (type != null && type.equals("IFrame")) {
				return "ISUCCESS";
			} else {
				return SUCCESS;
			}

		}
		//Started By Dattatray Date:08-10-21
		if (getRejectStatus() !=null && getRejectStatus().equals("-1")) {
			rejectCandidate(uF);
			return SUCCESS;
		}
		//Ended By Dattatray Date:08-10-21
//		setCandiFinalStatus(true);
		checkCandidateOfferedOrNot(uF);//Created By Dattatray Date:11-10-21
		return LOAD;
	}

	/**
	 * @author Dattatray
	 * @since 08-10-21
	 * @param uF
	 */
	private void rejectCandidate(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from candidate_personal_details where emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(candidateId));
			rs = pst.executeQuery();
			while (rs.next()) {
				pst = con.prepareStatement("UPDATE candidate_personal_details SET is_rejected = ? where emp_per_id = ?");
				pst.setInt(1, uF.parseToInt(getRejectStatus()));
				pst.setInt(2, uF.parseToInt(candidateId));
				pst.executeUpdate();
				
				CF.candidateReject(con, uF, getCandidateId(), getRecruitId(),strSessionEmpId);//Created By Dattatray Date:11-10-21
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
	
	
	
	private void addCandidateInNewJob(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			// String recruitID = request.getParameter("jobCode");
			// System.out.println("recruitID ===> "+getJobCode().length);
			if (getJobCode() != null) {
				// System.out.println("getJobCode().length ===>
				// "+getJobCode().length);
				StringBuilder sbJobCodeName = null;
				for (int a = 0; a < getJobCode().length; a++) {
					if (uF.parseToInt(getJobCode()[a]) > 0) {
						String jobcodeName = null;
						pst = con.prepareStatement("select job_code from recruitment_details where recruitment_id= ? ");
						pst.setInt(1, uF.parseToInt(getJobCode()[a]));
						rs = pst.executeQuery();
						// System.out.println("pst ===>> " + pst);
						// System.out.println("new Date ===> " + new Date());
						while (rs.next()) {
							jobcodeName = rs.getString("job_code");
						}
						rs.close();
						pst.close();

						boolean checkJobCodeFlag = false;
						pst = con.prepareStatement("select job_code from candidate_application_details where recruitment_id= ? and candidate_id =?");
						pst.setInt(1, uF.parseToInt(getJobCode()[a]));
						pst.setInt(2, uF.parseToInt(candidateId));
						rs = pst.executeQuery();
						// System.out.println("new Date ===> " + new Date());
						while (rs.next()) {
							checkJobCodeFlag = true;
						}
						rs.close();
						pst.close();

						// System.out.println("checkJobCodeFlag ===> " +
						// checkJobCodeFlag);

						if (!checkJobCodeFlag) {
							pst = con.prepareStatement("insert into candidate_application_details (candidate_id,recruitment_id,job_code,application_date,"
									+ "added_by,entry_date)values(?,?,?,?,?,?)");
							pst.setInt(1, uF.parseToInt(candidateId));
							pst.setInt(2, uF.parseToInt(getJobCode()[a]));
							pst.setString(3, jobcodeName);
							pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setTimestamp(6,
									uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
							pst.executeUpdate();
							pst.close();
							if (sbJobCodeName == null) {
								sbJobCodeName = new StringBuilder();
								sbJobCodeName.append(jobcodeName);
							} else {
								sbJobCodeName.append(", " + jobcodeName);
							}

							pst = con.prepareStatement(
									"delete from candidate_activity_details where recruitment_id=? and candi_id=? and user_id=? and " + "activity_id = ?");
							pst.setInt(1, uF.parseToInt(getJobCode()[a]));
							pst.setInt(2, uF.parseToInt(candidateId));
							pst.setInt(3, uF.parseToInt(strSessionEmpId));
							pst.setInt(4, CANDI_ACTIVITY_APPLY_ID);
							pst.executeUpdate();
							pst.close();

							pst = con.prepareStatement(
									"insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
							pst.setInt(1, uF.parseToInt(getJobCode()[a]));
							pst.setInt(2, uF.parseToInt(candidateId));
							pst.setString(3, "Apply for Job");
							pst.setInt(4, uF.parseToInt(strSessionEmpId));
							pst.setDate(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE));
							pst.setInt(6, CANDI_ACTIVITY_APPLY_ID);
							pst.execute();
							pst.close();
						}
					}
				}
				if (sbJobCodeName != null && !sbJobCodeName.toString().equals("")) {
					String candiName = CF.getCandiNameByCandiId(con, candidateId);
					session.setAttribute(MESSAGE, SUCCESSM + "" + candiName + " is added in " + sbJobCodeName.toString() + " successfully." + END);
				}
			}
			// System.out.println("pst ===> "+pst);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getLiveJobs(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			StringBuilder sb = new StringBuilder("");
			// int queCnt=1;
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from recruitment_details where close_job_status=false and job_approval_status = 1 and "
					+ "recruitment_id not in (select recruitment_id from candidate_application_details where candidate_id = ?)");
			pst.setInt(1, uF.parseToInt(candidateId));
			rs = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				if (uF.parseToInt(recruitId) == uF.parseToInt(rs.getString("recruitment_id"))) {
					sb.append("<option value=\"" + rs.getString("recruitment_id") + "\" selected=\"selected\">" + rs.getString("job_code") + "</option>");
				} else {
					sb.append("<option value=\"" + rs.getString("recruitment_id") + "\">" + rs.getString("job_code") + "</option>");
				}
				// queCnt++;
			}
			rs.close();
			pst.close();

			request.setAttribute("option", sb.toString());
			// request.setAttribute("queCnt", queCnt);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getCandiJobCodesList(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		List<List<String>> candiJobCodesList = new ArrayList<List<String>>();
		Map<String, String> hmjobCodeChangeDate = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from candidate_application_details where candidate_id = ?");
			pst.setInt(1, uF.parseToInt(candidateId));
			rst = pst.executeQuery();
			// System.out.println("pst ===> " + pst);
			while (rst.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("recruitment_id"));
				innerList.add(rst.getString("job_code"));
				candiJobCodesList.add(innerList);
				hmjobCodeChangeDate.put(rst.getString("recruitment_id"),
						uF.getDateFormat(rst.getString("application_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rst.close();
			pst.close();

			// System.out.println("candiJobCodesList ===> " +
			// candiJobCodesList);

			pst = con.prepareStatement("select * from candidate_application_details where candidate_id = ?");
			pst.setInt(1, uF.parseToInt(candidateId));
			rst = pst.executeQuery();
			Map<String, List<List<String>>> hmCandiAppActivityDetail = new HashMap<String, List<List<String>>>();
			while (rst.next()) {
				List<List<String>> activityList = hmCandiAppActivityDetail.get(rst.getString("recruitment_id"));

				if (activityList == null)
					activityList = new ArrayList<List<String>>();
				activityList.add(getCandiApplication(con, uF, candidateId, rst.getString("recruitment_id")));
				activityList.add(getCandiApplicationStatus(con, uF, candidateId, rst.getString("recruitment_id")));

				List<String> candiRoundList = getCandiRoundIds(con, uF, candidateId, rst.getString("recruitment_id"));
				for (int i = 0; candiRoundList != null && !candiRoundList.isEmpty() && i < candiRoundList.size(); i++) {
					activityList.add(getCandiRoundDetails(con, uF, candidateId, rst.getString("recruitment_id"), candiRoundList.get(i)));
				}
				activityList.add(getCandiFinalizeStatus(con, uF, candidateId, rst.getString("recruitment_id")));
				activityList.add(getCandiOfferStatus(con, uF, candidateId, rst.getString("recruitment_id")));

				activityList.add(getCandiInduction(con, uF, candidateId, rst.getString("recruitment_id")));
				hmCandiAppActivityDetail.put(rst.getString("recruitment_id"), activityList);
			}
			rst.close();
			pst.close();

			// System.out.println("hmCandiAppActivityDetail ===> " +
			// hmCandiAppActivityDetail);

			request.setAttribute("candiJobCodesList", candiJobCodesList);
			request.setAttribute("hmjobCodeChangeDate", hmjobCodeChangeDate);
			request.setAttribute("hmCandiAppActivityDetail", hmCandiAppActivityDetail);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		// return hmDegreeName;
	}

	private List<String> getCandiInduction(Connection con, UtilityFunctions uF, String candidateId, String jobId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		List<String> candiInductionData = new ArrayList<String>();
		try {
			pst = con.prepareStatement(
					"select * from candidate_application_details where candidate_id = ? and recruitment_id = ? and application_status = 2 and candidate_final_status = 1 and candidate_status = 1");
			pst.setInt(1, uF.parseToInt(candidateId));
			pst.setInt(2, uF.parseToInt(jobId));
			rst = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				candiInductionData.add("Induction"); // status
				candiInductionData.add(uF.getDateFormat(rst.getString("candidate_joining_date"), DBDATE, CF.getStrReportDateFormat())); // date
				candiInductionData.add(""); // rating/other
				candiInductionData.add(""); // comments
				candiInductionData.add("No"); // star yes/no
			}
			rst.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return candiInductionData;
	}

	private List<String> getCandiOfferStatus(Connection con, UtilityFunctions uF, String candidateId, String jobId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		List<String> candiOfferData = new ArrayList<String>();
		try {
			pst = con.prepareStatement(
					"select * from candidate_application_details where candidate_id = ? and recruitment_id = ? and application_status = 2 and candidate_final_status = 1");
			pst.setInt(1, uF.parseToInt(candidateId));
			pst.setInt(2, uF.parseToInt(jobId));
			rst = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				candiOfferData.add("Offer Status"); // status
				candiOfferData.add(uF.getDateFormat(rst.getString("offer_accept_date"), DBDATE, CF.getStrReportDateFormat())); // date
				if (uF.parseToInt(rst.getString("candidate_status")) == 0) {
					candiOfferData.add(""); // rating/other
				} else if (uF.parseToInt(rst.getString("candidate_status")) == 1) {
					candiOfferData.add("Accepted"); // rating/other
				} else if (uF.parseToInt(rst.getString("candidate_status")) == -1) {
					candiOfferData.add("Rejected"); // rating/other
				}
				candiOfferData.add(rst.getString("offer_accept_remark")); // comments
				candiOfferData.add("No"); // star yes/no
			}
			rst.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return candiOfferData;
	}

	private List<String> getCandiFinalizeStatus(Connection con, UtilityFunctions uF, String candidateId, String jobId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		List<String> candiFinalData = new ArrayList<String>();
		try {
			pst = con.prepareStatement("select * from candidate_application_details where candidate_id = ? and recruitment_id = ? and application_status = 2");
			pst.setInt(1, uF.parseToInt(candidateId));
			pst.setInt(2, uF.parseToInt(jobId));
			rst = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				if (uF.parseToInt(rst.getString("candidate_final_status")) == 0) {
					candiFinalData.add(""); // status
				} else if (uF.parseToInt(rst.getString("candidate_final_status")) == 1) {
					candiFinalData.add("Finalisation (Offer Letter)"); // status
				} else if (uF.parseToInt(rst.getString("candidate_final_status")) == -1) {
					candiFinalData.add("Rejected"); // status
				}
				candiFinalData.add(uF.getDateFormat(rst.getString("candidate_final_status_date"), DBDATE, CF.getStrReportDateFormat())); // date
				candiFinalData.add("INR " + rst.getString("ctc_offered")); // rating/other
				candiFinalData.add(rst.getString("candidate_hr_comments")); // comments
				candiFinalData.add("No"); // star yes/no
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return candiFinalData;
	}

	private List<String> getCandiRoundDetails(Connection con, UtilityFunctions uF, String candidateId, String jobId, String roundId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		List<String> candiRoundData = new ArrayList<String>();
		try {
			pst = con.prepareStatement("select * from candidate_interview_panel where candidate_id = ? and recruitment_id = ? and panel_round_id = ?");
			pst.setInt(1, uF.parseToInt(candidateId));
			pst.setInt(2, uF.parseToInt(jobId));
			pst.setInt(3, uF.parseToInt(roundId));
			rst = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				candiRoundData.add("Round " + roundId); // status
				candiRoundData.add(uF.getDateFormat(rst.getString("interview_date"), DBDATE, CF.getStrReportDateFormat())); // date
				candiRoundData.add(rst.getString("panel_rating")); // rating/other
				candiRoundData.add(rst.getString("comments")); // comments
				candiRoundData.add("Yes"); // star yes/no
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return candiRoundData;
	}

	private List<String> getCandiRoundIds(Connection con, UtilityFunctions uF, String candidateId, String jobId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		List<String> candiRoundIds = new ArrayList<String>();
		try {
			pst = con.prepareStatement("select round_id,recruitment_id from panel_interview_details where recruitment_id = ? group by recruitment_id,round_id");
			// pst.setInt(1, uF.parseToInt(candidateId));
			pst.setInt(1, uF.parseToInt(jobId));
			rst = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				candiRoundIds.add(rst.getString("round_id")); // rating/other
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return candiRoundIds;
	}

	private List<String> getCandiApplication(Connection con, UtilityFunctions uF, String candidateId, String jobId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		List<String> candiAppliData = new ArrayList<String>();
		try {

			Map<String, List<String>> hmSkills = getCandiSkillsName(con);
			Map<String, List<String>> hmDegrees = getCandiDegreeName(con);
			Map<String, List<String>> hmTotExp = getCandiTotExp(con, uF);

			pst = con.prepareStatement("select application_date,emp_gender from candidate_application_details cad, candidate_personal_details cpd "
					+ "where candidate_id = ? and cad.recruitment_id = ? and cad.candidate_id = cpd.emp_per_id");
			pst.setInt(1, uF.parseToInt(candidateId));
			pst.setInt(2, uF.parseToInt(jobId));
			rst = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				candiAppliData.add("Application"); // status
				candiAppliData.add(uF.getDateFormat(rst.getString("application_date"), DBDATE, CF.getStrReportDateFormat())); // date
				String candiGender = rst.getString("emp_gender");
				String strStars = calculateCandidateStarRating(con, uF, hmSkills.get(candidateId), hmDegrees.get(candidateId), hmTotExp.get(candidateId),
						candiGender, jobId);
				// System.out.println("strStars === > " +strStars);
				candiAppliData.add(strStars); // rating/other
				candiAppliData.add(""); // comments
				candiAppliData.add("Yes"); // star yes/no
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return candiAppliData;
	}

	private List<String> getCandiApplicationStatus(Connection con, UtilityFunctions uF, String candidateId, String jobId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		List<String> candiAppliData = new ArrayList<String>();
		try {
			Map<String, List<String>> hmSkills = getCandiSkillsName(con);
			Map<String, List<String>> hmDegrees = getCandiDegreeName(con);
			Map<String, List<String>> hmTotExp = getCandiTotExp(con, uF);

			pst = con.prepareStatement(
					"select cad.application_status,cad.application_status_date,emp_gender from candidate_application_details cad, candidate_personal_details cpd "
							+ "where candidate_id = ? and cad.recruitment_id = ? and cad.candidate_id = cpd.emp_per_id");
			pst.setInt(1, uF.parseToInt(candidateId));
			pst.setInt(2, uF.parseToInt(jobId));
			rst = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				if (uF.parseToInt(rst.getString("application_status")) == 0) {
					candiAppliData.add(""); // status
				} else if (uF.parseToInt(rst.getString("application_status")) == 2) {
					candiAppliData.add("Shortlisted"); // status
				} else if (uF.parseToInt(rst.getString("application_status")) == -1) {
					candiAppliData.add("Rejected"); // status
				}
				candiAppliData.add(uF.getDateFormat(rst.getString("application_status_date"), DBDATE, CF.getStrReportDateFormat())); // date
				String candiGender = rst.getString("emp_gender");
				String strStars = calculateCandidateStarRating(con, uF, hmSkills.get(candidateId), hmDegrees.get(candidateId), hmTotExp.get(candidateId),
						candiGender, jobId);
				// System.out.println("strStars 11111 === > " +strStars);
				candiAppliData.add(strStars); // rating/other
				candiAppliData.add(""); // comments
				candiAppliData.add("Yes"); // star yes/no
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return candiAppliData;
	}

	private Map<String, List<String>> getCandiDegreeName(Connection con) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String, List<String>> hmDegreeName = new HashMap<String, List<String>>();
		try {
			List<String> degreeList = new ArrayList<String>();
			pst = con.prepareStatement("select emp_id,education_id from candidate_education_details");
			rst = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				degreeList = hmDegreeName.get(rst.getString("emp_id"));
				if (degreeList == null)
					degreeList = new ArrayList<String>();
				degreeList.add(rst.getString("education_id"));
				hmDegreeName.put(rst.getString("emp_id"), degreeList);
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmDegreeName;
	}

	private Map<String, List<String>> getCandiSkillsName(Connection con) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String, List<String>> hmSkills = new HashMap<String, List<String>>();
		try {
			List<String> skillList = new ArrayList<String>();
			pst = con.prepareStatement("select emp_id,skill_id from candidate_skills_description");
			rst = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				skillList = hmSkills.get(rst.getString("emp_id"));
				if (skillList == null)
					skillList = new ArrayList<String>();
				skillList.add(rst.getString("skill_id"));
				hmSkills.put(rst.getString("emp_id"), skillList);
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmSkills;
	}

	public String getTimeDurationBetweenDates(String strStartDate, String strStartFormat, String strEndDate, String strEndFormat, UtilityFunctions uF) {

		StringBuilder sbTimeDuration = new StringBuilder();
		try {
			LocalDate joiningDate = new LocalDate(uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "yyyy")),
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "MM")), uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "dd")));
			LocalDate currentDate = new LocalDate(uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "yyyy")),
					uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "MM")), uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "dd")));

			Period period = new Period(joiningDate, currentDate, PeriodType.yearMonthDay());

			if (period.getYears() > 0) {
				sbTimeDuration.append(period.getYears());
			}

			if (period.getMonths() > 0) {
				sbTimeDuration.append("." + period.getMonths());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sbTimeDuration.toString();
	}

	private Map<String, List<String>> getCandiTotExp(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rst = null;

		Map<String, List<String>> hmTotExp = new HashMap<String, List<String>>();
		try {
			List<String> expList = new ArrayList<String>();
			pst = con.prepareStatement("select emp_id,from_date,to_date from candidate_prev_employment");
			rst = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				expList = hmTotExp.get(rst.getString("emp_id"));
				if (expList == null)
					expList = new ArrayList<String>();

				String frmdt = rst.getString("from_date");
				String todt = rst.getString("to_date");
				String candidateExp = "";
				if (frmdt != null && todt != null) {
					candidateExp = getTimeDurationBetweenDates(frmdt, DBDATE, todt, DBDATE, uF);
				}
				expList.add(candidateExp);
				hmTotExp.put(rst.getString("emp_id"), expList);
			}
			rst.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmTotExp;
	}

	private Map<String, String> getDesigAttribute(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rst = null;

		Map<String, String> hmDesigAttrib = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from desig_attribute");
			rst = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				if (rst.getString("_type").equals("1")) {
					hmDesigAttrib.put(rst.getString("desig_id") + "_EDUCATION", rst.getString("desig_value"));
				} else if (rst.getString("_type").equals("2")) {
					hmDesigAttrib.put(rst.getString("desig_id") + "_TOTEXP", rst.getString("desig_value"));
				} else if (rst.getString("_type").equals("3")) {
					hmDesigAttrib.put(rst.getString("desig_id") + "_RELEXP", rst.getString("desig_value"));
				} else if (rst.getString("_type").equals("4")) {
					hmDesigAttrib.put(rst.getString("desig_id") + "_EXPWITH_US", rst.getString("desig_value"));
				} else if (rst.getString("_type").equals("5")) {
					hmDesigAttrib.put(rst.getString("desig_id") + "_SKILLS", rst.getString("desig_value"));
				} else if (rst.getString("_type").equals("6")) {
					hmDesigAttrib.put(rst.getString("desig_id") + "_GENDER", rst.getString("desig_value"));
				}
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmDesigAttrib;
	}

	private Map<String, String> getEducationWeightage(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rst = null;

		Map<String, String> hmEduWeightage = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select education_name,weightage from educational_details");
			rst = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				hmEduWeightage.put(rst.getString("education_name"), rst.getString("weightage"));
			}
			rst.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmEduWeightage;
	}

	private String calculateCandidateStarRating(Connection con, UtilityFunctions uF, List<String> skillsList, List<String> educationsList,
			List<String> totExpList, String candiGender, String jobId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		String strStars = null;
		try {
			String desigID = "";
			Map<String, String> hmEduWeightage = getEducationWeightage(con, uF);
			Map<String, String> hmJobDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from recruitment_details where recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(jobId));
			rst = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				hmJobDetails.put("SKILLS", rst.getString("skills"));
				hmJobDetails.put("EDUCATIONS", rst.getString("min_education"));
				hmJobDetails.put("MIN_EXP", rst.getString("min_exp"));
				hmJobDetails.put("MAX_EXP", rst.getString("max_exp"));
				hmJobDetails.put("DESIG_ID", rst.getString("designation_id"));
				desigID = rst.getString("designation_id");
			}
			rst.close();
			pst.close();

			String minExp = hmJobDetails.get("MIN_EXP");
			List<String> recSkillsList = getListData(hmJobDetails.get("SKILLS"));
			List<String> recEduList = getListData(hmJobDetails.get("EDUCATIONS"));
			Map<String, String> hmDesigAttrib = getDesigAttribute(con, uF);
			List<String> desigSkillList = getListData(hmDesigAttrib.get(desigID + "_SKILLS"));
			List<String> desigEduList = getListData(hmDesigAttrib.get(desigID + "_EDUCATION"));
			String desigTotExp = hmDesigAttrib.get(desigID + "_TOTEXP");
			String desigRelExp = hmDesigAttrib.get(desigID + "_RELEXP");
			String desigExpWithus = hmDesigAttrib.get(desigID + "_EXPWITH_US");
			String desigGender = hmDesigAttrib.get(desigID + "_GENDER");

			int skillMarks = 0, skillCount = 0;
			if (recSkillsList != null && !recSkillsList.isEmpty()) {
				for (int i = 0; i < recSkillsList.size(); i++) {
					for (int j = 0; skillsList != null && !skillsList.isEmpty() && j < skillsList.size(); j++) {
						if (recSkillsList.get(i).equals(skillsList.get(j))) {
							skillMarks = 100;
						}
					}
					skillCount = 1;
				}
			} else {
				if (desigSkillList != null && !desigSkillList.isEmpty()) {
					for (int i = 0; i < desigSkillList.size(); i++) {
						for (int j = 0; skillsList != null && !skillsList.isEmpty() && j < skillsList.size(); j++) {
							if (desigSkillList.get(i).equals(skillsList.get(j))) {
								skillMarks = 100;
							}
						}
						skillCount = 1;
					}
				}
			}

			int eduMarks = 0, eduCount = 0;
			if (recEduList != null && !recEduList.isEmpty()) {
				for (int i = 0; i < recEduList.size(); i++) {
					for (int j = 0; educationsList != null && !educationsList.isEmpty() && j < educationsList.size(); j++) {
						if (recEduList.get(i).equals(educationsList.get(j))
								|| uF.parseToInt(hmEduWeightage.get(recEduList.get(i))) >= uF.parseToInt(hmEduWeightage.get(educationsList.get(j)))) {
							eduMarks = 100;
						}
					}
					eduCount = 1;
				}
			} else {
				if (desigEduList != null && !desigEduList.isEmpty()) {
					for (int i = 0; i < desigEduList.size(); i++) {
						for (int j = 0; educationsList != null && !educationsList.isEmpty() && j < educationsList.size(); j++) {
							if (desigEduList.get(i).equals(educationsList.get(j))
									|| uF.parseToInt(hmEduWeightage.get(desigEduList.get(i))) >= uF.parseToInt(hmEduWeightage.get(educationsList.get(j)))) {
								eduMarks = 100;
							}
						}
						eduCount = 1;
					}
				}
			}

			int expMarks = 0, expCount = 0;
			if (minExp != null && !minExp.equals("")) {
				// for (int i = 0; i < recEduList.size(); i++) {
				double sumcandiExp = 0;
				for (int j = 0; totExpList != null && !totExpList.isEmpty() && j < totExpList.size(); j++) {
					// System.out.println("totExpList "+j+" ==
					// "+totExpList.get(j));
					sumcandiExp += uF.parseToDouble(totExpList.get(j));
				}
				// System.out.println("sumcandiExp == "+sumcandiExp);

				if (uF.parseToDouble(minExp) <= sumcandiExp) {
					expMarks = 100;
				}
				expCount = 1;
				// }
			} else {
				if (desigTotExp != null && !desigTotExp.equals("")) {
					double sumcandiExp = 0;
					for (int j = 0; totExpList != null && !totExpList.isEmpty() && j < totExpList.size(); j++) {
						sumcandiExp += uF.parseToDouble(totExpList.get(j));
					}
					if (uF.parseToDouble(desigTotExp) <= sumcandiExp) {
						expMarks = 100;
					}
					expCount = 1;
				}
			}
			// System.out.println("desigGender == "+desigGender);
			// System.out.println("candiGender == "+candiGender);
			int genderMarks = 0, genderCount = 0;
			if (desigGender != null && !desigGender.equals("")) {
				if (desigGender.equals(candiGender)) {
					genderMarks = 100;
				}
				genderCount = 1;
			}

			// System.out.println("skillMarks == " + skillMarks + " eduMarks ==
			// " + eduMarks + " expMarks == " + expMarks + " genderMarks == " +
			// genderMarks);
			int allMarks = skillMarks + eduMarks + expMarks + genderMarks;
			int allCount = skillCount + eduCount + expCount + genderCount;

			int avgMarks = 0;
			if (allCount > 0) {
				avgMarks = allMarks / allCount;
			}
			// System.out.println("avgMarks == "+avgMarks);
			double starrts = uF.parseToDouble("" + avgMarks) / 20;
			// strStars = uF.formatIntoOneDecimalWithOutComma(starrts);
			int intstars = (int) starrts;
			if (starrts > uF.parseToDouble("" + intstars)) {
				strStars = uF.formatIntoOneDecimalWithOutComma(starrts);
			} else {
				strStars = intstars + "";
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return strStars;
	}

	public List<String> getListData(String strData) {
		List<String> dataList = new ArrayList<String>();
		if (strData != null && !strData.equals("")) {
			dataList = Arrays.asList(strData.split(","));
		}

		return dataList;
	}

	private void getCandiApplicationsDetails(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		List<List<String>> candiAppDetailsList = new ArrayList<List<String>>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from candidate_application_details where candidate_id = ? ");
			pst.setInt(1, uF.parseToInt(candidateId));
			rst = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			// System.out.println("pst ===> "+pst);
			while (rst.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("candi_application_deatils_id"));
				innerList.add(rst.getString("job_code"));
				innerList.add(uF.getDateFormat(rst.getString("application_date"), DBDATE, CF.getStrReportDateFormat()));
				if (rst.getString("application_status").equals("0")) {
					innerList.add("-");
				} else if (rst.getString("application_status").equals("2")) {
					innerList.add("Shortlist");
				} else if (rst.getString("application_status").equals("-1")) {
					innerList.add("Reject");
				}
				if (rst.getString("candidate_final_status").equals("0")) {
					innerList.add("Not Taken");
				} else if (rst.getString("candidate_final_status").equals("1")) {
					innerList.add("Candidate Final");
				} else if (rst.getString("candidate_final_status").equals("-1")) {
					innerList.add("Candidate Reject");
				}
				innerList.add(uF.showData(getInterviewPanelRating(rst.getString("recruitment_id"), candidateId, uF), "0"));
				candiAppDetailsList.add(innerList);
			}
			rst.close();
			pst.close();
			request.setAttribute("candiAppDetailsList", candiAppDetailsList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		// return hmDegreeName;
	}

	private String getInterviewPanelRating(String recruitId, String candidateId, UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		String avgRating = null;
		try {
			StringBuilder sb = new StringBuilder("");
			// int queCnt=1;
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from candidate_interview_panel where candidate_id = ? and recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(candidateId));
			pst.setInt(2, uF.parseToInt(recruitId));
			rs = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			// System.out.println("pst ===> "+pst);
			double totRating = 0, dblAvgRating = 0;
			int count = 0;
			while (rs.next()) {
				count++;
				totRating += rs.getDouble("panel_rating");
			}
			rs.close();
			pst.close();
			// System.out.println("totRating ===> " + totRating);
			if (totRating > 0) {
				dblAvgRating = totRating / count;
			} else {
				dblAvgRating = 0;
			}
			// System.out.println("dblAvgRating ===> " + dblAvgRating);
			avgRating = "" + dblAvgRating;
			// request.setAttribute("option", sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return avgRating;
	}

	/**
	 * 
	 * Note : Checking if Candidate Offered or not
	 * 
	 * @author Dattatray
	 * @since 11-Oct-2021
	 * @param uF
	 */
	private void checkCandidateOfferedOrNot(UtilityFunctions uF) {
		setCandiFinalStatus("true");
		setIsRejected("true");
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM candidate_application_details WHERE candidate_id = ? AND (candidate_final_status = 1 or candidate_final_status = -1)");
			pst.setInt(1, uF.parseToInt(candidateId));
			System.out.println("pst --> "+pst.toString());
			rst = pst.executeQuery();
			while (rst.next()) {
				setCandiFinalStatus("false");
			}
			rst.close();
			pst.close();

			pst = con.prepareStatement("SELECT * FROM candidate_personal_details WHERE emp_per_id = ? AND is_rejected = -1");
			pst.setInt(1, uF.parseToInt(candidateId));
			System.out.println("pst --> "+pst.toString());
			rst = pst.executeQuery();
			while (rst.next()) {
				setIsRejected("false");
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
		// return hmDegreeName;
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

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getRejectStatus() {
		return rejectStatus;
	}

	public void setRejectStatus(String rejectStatus) {
		this.rejectStatus = rejectStatus;
	}

	public String getCandiFinalStatus() {
		return candiFinalStatus;
	}

	public void setCandiFinalStatus(String candiFinalStatus) {
		this.candiFinalStatus = candiFinalStatus;
	}

	public String getIsRejected() {
		return isRejected;
	}

	public void setIsRejected(String isRejected) {
		this.isRejected = isRejected;
	}

	

}