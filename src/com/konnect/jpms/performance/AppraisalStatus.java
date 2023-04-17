package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;

public class AppraisalStatus implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType; 
	String strUserTypeId;
	CommonFunctions CF;
	private String id;

	private String alertStatus;
	private String alert_type;

	private String type;
	private String strMessage;

	private String appFreqId;

	private String fromPage;
	public String execute() {

		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		UtilityFunctions uF = new UtilityFunctions();

		/*
		 * if(strSessionUserType != null && strSessionUserType.equals(HRMANAGER)
		 * && getAlertStatus()!=null && getAlert_type()!=null &&
		 * getAlert_type().equals(REVIEW_FINALIZATION_ALERT)){
		 * updateUserAlerts(); }
		 */
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());

		request.setAttribute(PAGE, "/jsp/performance/AppraisalStatus.jsp");
		request.setAttribute(TITLE, "Review Status");

		if (getType() != null && getType().equalsIgnoreCase("REMINDER")) {
			sendReminderToAllPendingReviewers(getId());
			return MESSAGE;
		}
		getOrientationMember();
		getAppraisalStatus(uF);
		getAppraisalStatusReport(uF);
		getRemarks();
		getEmpSupervisor();
		getOrientationCount(uF);
		// getSingleOpenWithoutMarksQueCount(uF);
		getSingleOpenWithoutMarksQueReadUnreadCount(uF);
		
		getAppraisalQuestionAnswersExport(uF);
		
		if (getFromPage() != null && getFromPage().equalsIgnoreCase("AD")) {
			return "success";
		}

		return LOAD;
	}

	// private void updateUserAlerts() {
	// Connection con = null;
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	// Database db = new Database();
	// db.setRequest(request);
	// UtilityFunctions uF = new UtilityFunctions();
	// int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
	// try {
	// con = db.makeConnection(con);
	//
	// String strDomain = request.getServerName().split("\\.")[0];
	// UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
	// userAlerts.setStrDomain(strDomain);
	// userAlerts.setStrEmpId(""+nEmpId);
	// userAlerts.set_type(REVIEW_FINALIZATION_ALERT);
	// userAlerts.setStatus(UPDATE_ALERT);
	// Thread t = new Thread(userAlerts);
	// t.run();
	// // System.out.println("in Appraisal UserAlerts ...");
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// db.closeResultSet(rs);
	// db.closeStatements(pst);
	// db.closeConnection(con);
	// }
	// }

	private void sendReminderToAllPendingReviewers(String appId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmExistQueAns = new HashMap<String, String>();
			pst = con
					.prepareStatement("select user_type_id,user_id,emp_id from appraisal_question_answer where appraisal_id=? and is_submit=true group by user_type_id,user_id,emp_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			// System.out.println("pst ============ > "+ pst);
			while (rs.next()) {
				hmExistQueAns.put(rs.getString("user_type_id") + "_" + rs.getString("user_id") + "_" + rs.getString("emp_id"), rs.getString("emp_id"));
			}
			rs.close();
			pst.close();

			// System.out.println("hmExistQueAns ===>> " + hmExistQueAns);
			String strDomain = request.getServerName().split("\\.")[0];
			// setDomain(strDomain);
			// Thread th = new Thread(this);
			// th.start();

			Map<String, String> hmReviewData = getReviewDetails(con);
			Map<String, Map<String, String>> hmRevieweewiseAppraiser = getRevieweewiseAppraiser(con);
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
			Map<String, String> hmOrientData = CF.getOrientMemberID(con);

			Iterator<String> it = hmRevieweewiseAppraiser.keySet().iterator();
			while (it.hasNext()) {
				String strRevieweeId = it.next();
				Map<String, String> hmRevieweeNameData = hmEmpInfo.get(strRevieweeId);

				Map<String, String> hmRevieweeData = hmRevieweewiseAppraiser.get(strRevieweeId);
				List<List<String>> allIdList = new ArrayList<List<String>>();
				/*
				 * if(hmRevieweeData.get("REVIEW_SELFID") != null &&
				 * !hmRevieweeData.get("REVIEW_SELFID").equals("")) {
				 * List<String> selfID =
				 * Arrays.asList(hmRevieweeData.get("REVIEW_SELFID").split(","))
				 * ; for (int i = 0; selfID != null && i < selfID.size(); i++) {
				 * if(selfID.get(i) != null && !selfID.get(i).equals("")) {
				 * List<String> innerList = new ArrayList<String>();
				 * innerList.add(selfID.get(i)); innerList.add("Self");
				 * allIdList.add(innerList); } } }
				 */

				if (hmRevieweeData.get("REVIEW_PEERID") != null && !hmRevieweeData.get("REVIEW_PEERID").equals("")) {
					List<String> peerID = Arrays.asList(hmRevieweeData.get("REVIEW_PEERID").split(","));
					for (int i = 0; peerID != null && i < peerID.size(); i++) {
						if (peerID.get(i) != null && !peerID.get(i).equals("")) {
							if (hmExistQueAns == null
									|| hmExistQueAns.get(hmOrientData.get("Peer") + "_" + peerID.get(i) + "_" + strRevieweeId) == null
									|| uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Peer") + "_" + peerID.get(i) + "_" + strRevieweeId)) != uF
											.parseToInt(strRevieweeId)) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(peerID.get(i));
								innerList.add("Peer");
								allIdList.add(innerList);
							}
						}
					}
				}
				if (hmRevieweeData.get("REVIEW_MANAGERID") != null && !hmRevieweeData.get("REVIEW_MANAGERID").equals("")) {
					List<String> managerID = Arrays.asList(hmRevieweeData.get("REVIEW_MANAGERID").split(","));
					for (int i = 0; managerID != null && i < managerID.size(); i++) {
						if (managerID.get(i) != null && !managerID.get(i).equals("")) {
							if (hmExistQueAns == null
									|| hmExistQueAns.get(hmOrientData.get("Manager") + "_" + managerID.get(i) + "_" + strRevieweeId) == null
									|| uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Manager") + "_" + managerID.get(i) + "_" + strRevieweeId)) != uF
											.parseToInt(strRevieweeId)) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(managerID.get(i));
								innerList.add("Manager");
								allIdList.add(innerList);
							}
						}
					}
				}
				if (hmRevieweeData.get("REVIEW_HRID") != null && !hmRevieweeData.get("REVIEW_HRID").equals("")) {
					List<String> hrID = Arrays.asList(hmRevieweeData.get("REVIEW_HRID").split(","));
					for (int i = 0; hrID != null && i < hrID.size(); i++) {
						if (hrID.get(i) != null && !hrID.get(i).equals("")) {
							// System.out.println("hmOrientData.get(HR)===>> " +
							// hmOrientData.get("HR"));
							// System.out.println("hrID.get(i) ===>> " +
							// hrID.get(i));
							// System.out.println("strRevieweeId ===>> " +
							// strRevieweeId);
							// System.out.println("hmExistQueAns.get(hmOrientData.get(HR)-hrID.get(i)-strRevieweeId)
							// ===>> " +
							// hmExistQueAns.get(hmOrientData.get("HR")+"_"+hrID.get(i)+"_"+strRevieweeId));

							if (hmExistQueAns == null
									|| hmExistQueAns.get(hmOrientData.get("HR") + "_" + hrID.get(i) + "_" + strRevieweeId) == null
									|| uF.parseToInt(hmExistQueAns.get(hmOrientData.get("HR") + "_" + hrID.get(i) + "_" + strRevieweeId)) != uF
											.parseToInt(strRevieweeId)) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(hrID.get(i));
								innerList.add("HR");
								allIdList.add(innerList);
							}
						}
					}
				}

				if (hmRevieweeData.get("REVIEW_CEOID") != null && !hmRevieweeData.get("REVIEW_CEOID").equals("")) {
					List<String> ceoID = Arrays.asList(hmRevieweeData.get("REVIEW_CEOID").split(","));
					for (int i = 0; ceoID != null && i < ceoID.size(); i++) {
						if (ceoID.get(i) != null && !ceoID.get(i).equals("")) {
							if (hmExistQueAns == null
									|| hmExistQueAns.get(hmOrientData.get("CEO") + "_" + ceoID.get(i) + "_" + strRevieweeId) == null
									|| uF.parseToInt(hmExistQueAns.get(hmOrientData.get("CEO") + "_" + ceoID.get(i) + "_" + strRevieweeId)) != uF
											.parseToInt(strRevieweeId)) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(ceoID.get(i));
								innerList.add("CEO");
								allIdList.add(innerList);
							}
						}
					}
				}

				if (hmRevieweeData.get("REVIEW_HODID") != null && !hmRevieweeData.get("REVIEW_HODID").equals("")) {
					List<String> hodID = Arrays.asList(hmRevieweeData.get("REVIEW_HODID").split(","));
					for (int i = 0; hodID != null && i < hodID.size(); i++) {
						if (hodID.get(i) != null && !hodID.get(i).equals("")) {
							if (hmExistQueAns == null
									|| hmExistQueAns.get(hmOrientData.get("HOD") + "_" + hodID.get(i) + "_" + strRevieweeId) == null
									|| uF.parseToInt(hmExistQueAns.get(hmOrientData.get("HOD") + "_" + hodID.get(i) + "_" + strRevieweeId)) != uF
											.parseToInt(strRevieweeId)) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(hodID.get(i));
								innerList.add("HOD");
								allIdList.add(innerList);
							}
						}
					}
				}

				if (hmRevieweeData.get("REVIEW_OTHERID") != null && !hmRevieweeData.get("REVIEW_OTHERID").equals("")) {
					List<String> otherID = Arrays.asList(hmRevieweeData.get("REVIEW_OTHERID").split(","));
					for (int i = 0; otherID != null && i < otherID.size(); i++) {
						if (otherID.get(i) != null && !otherID.get(i).equals("")) {
							if (hmExistQueAns == null
									|| hmExistQueAns.get(hmOrientData.get("Anyone") + "_" + otherID.get(i) + "_" + strRevieweeId) == null
									|| uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Anyone") + "_" + otherID.get(i) + "_" + strRevieweeId)) != uF
											.parseToInt(strRevieweeId)) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(otherID.get(i));
								innerList.add("Anyone");
								allIdList.add(innerList);
							}
						}
					}
				}

				if (hmRevieweeData.get("REVIEW_SUBORDINATEID") != null && !hmRevieweeData.get("REVIEW_SUBORDINATEID").equals("")) {
					List<String> subordinateID = Arrays.asList(hmRevieweeData.get("REVIEW_SUBORDINATEID").split(","));
					for (int i = 0; subordinateID != null && i < subordinateID.size(); i++) {
						if (subordinateID.get(i) != null && !subordinateID.get(i).equals("")) {
							if (hmExistQueAns == null
									|| hmExistQueAns.get(hmOrientData.get("Sub-ordinate") + "_" + subordinateID.get(i) + "_" + strRevieweeId) == null
									|| uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Sub-ordinate") + "_" + subordinateID.get(i) + "_" + strRevieweeId)) != uF
											.parseToInt(strRevieweeId)) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(subordinateID.get(i));
								innerList.add("Sub-ordinate");
								allIdList.add(innerList);
							}
						}
					}
				}

				if (hmRevieweeData.get("REVIEW_GRANDSUPERVISORID") != null && !hmRevieweeData.get("REVIEW_GRANDSUPERVISORID").equals("")) {
					List<String> gSupervisorID = Arrays.asList(hmRevieweeData.get("REVIEW_GRANDSUPERVISORID").split(","));
					for (int i = 0; gSupervisorID != null && i < gSupervisorID.size(); i++) {
						if (gSupervisorID.get(i) != null && !gSupervisorID.get(i).equals("")) {
							if (hmExistQueAns == null
									|| hmExistQueAns.get(hmOrientData.get("GroupHead") + "_" + gSupervisorID.get(i) + "_" + strRevieweeId) == null
									|| uF.parseToInt(hmExistQueAns.get(hmOrientData.get("GroupHead") + "_" + gSupervisorID.get(i) + "_" + strRevieweeId)) != uF
											.parseToInt(strRevieweeId)) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(gSupervisorID.get(i));
								innerList.add("Group Head");
								allIdList.add(innerList);
							}
						}
					}
				}

				if (hmRevieweeData.get("REVIEW_OTHERPEERID") != null && !hmRevieweeData.get("REVIEW_OTHERPEERID").equals("")) {
					List<String> otherPeerID = Arrays.asList(hmRevieweeData.get("REVIEW_OTHERPEERID").split(","));
					for (int i = 0; otherPeerID != null && i < otherPeerID.size(); i++) {
						if (otherPeerID.get(i) != null && !otherPeerID.get(i).equals("")) {
							if (hmExistQueAns == null
									|| hmExistQueAns.get(hmOrientData.get("Other Peer") + "_" + otherPeerID.get(i) + "_" + strRevieweeId) == null
									|| uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Other Peer") + "_" + otherPeerID.get(i) + "_" + strRevieweeId)) != uF
											.parseToInt(strRevieweeId)) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(otherPeerID.get(i));
								innerList.add("Other Peer");
								allIdList.add(innerList);
							}
						}
					}
				}

				for (int i = 0; allIdList != null && !allIdList.isEmpty() && i < allIdList.size(); i++) {
					List<String> innerList = allIdList.get(i);
					if (innerList.get(0) != null && !innerList.get(0).equals("")) {
						Map<String, String> hmEmpInner = hmEmpInfo.get(innerList.get(0));
						Notifications nF = new Notifications(N_PENDING_REVIEW_REMINDER, CF);
						nF.setDomain(strDomain);
						nF.request = request;
						nF.setStrEmpId(innerList.get(0));
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrRevieweeName(hmRevieweeNameData.get("FNAME") + " " + hmRevieweeNameData.get("LNAME"));
						nF.setStrRoleType(innerList.get(1));
						nF.setStrReviewName(hmReviewData.get("REVIEW_NAME"));
						nF.setStrReviewStartdate(uF.getDateFormat(hmReviewData.get("REVIEW_STARTDATE"), DBDATE, CF.getStrReportDateFormat()));
						nF.setStrReviewEnddate(uF.getDateFormat(hmReviewData.get("REVIEW_ENDDATE"), DBDATE, CF.getStrReportDateFormat()));

						nF.setStrEmpFname(hmEmpInner.get("FNAME"));
						nF.setStrEmpLname(hmEmpInner.get("LNAME"));
						nF.setEmailTemplate(true);
						nF.sendNotifications();
					}
				}
				request.setAttribute("STATUS_MSG", SUCCESSM + "Reminder mails sent successfully." + END);
			}

			// System.out.println("hmReadUnreadCount =====> "+
			// hmReadUnreadCount);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("STATUS_MSG", ERRORM + "Reminder mails not sent, Please try again." + END);
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private Map<String, Map<String, String>> getRevieweewiseAppraiser(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, Map<String, String>> hmRevieweewiseAppraiser = new HashMap<String, Map<String, String>>();
		try {
			pst = con.prepareStatement("select * from appraisal_reviewee_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hmRevieweeData = new HashMap<String, String>();

				hmRevieweeData.put("REVIEW_SELFID", rs.getString("reviewee_id"));
				hmRevieweeData.put("REVIEW_PEERID", rs.getString("peer_ids"));
				hmRevieweeData.put("REVIEW_MANAGERID", rs.getString("supervisor_ids"));
				hmRevieweeData.put("REVIEW_HRID", rs.getString("hr_ids"));
				hmRevieweeData.put("REVIEW_OTHERID", rs.getString("other_ids"));
				hmRevieweeData.put("REVIEW_CEOID", rs.getString("ceo_ids"));
				hmRevieweeData.put("REVIEW_HODID", rs.getString("hod_ids"));
				hmRevieweeData.put("REVIEW_SUBORDINATEID", rs.getString("subordinate_ids"));
				hmRevieweeData.put("REVIEW_GRANDSUPERVISORID", rs.getString("grand_supervisor_ids"));
				hmRevieweeData.put("REVIEW_OTHERPEERID", rs.getString("other_peer_ids"));

				hmRevieweewiseAppraiser.put(rs.getString("reviewee_id"), hmRevieweeData);
			}
			rs.close();
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hmRevieweewiseAppraiser;
	}

	private Map<String, String> getReviewDetails(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, String> hmReviewData = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmReviewData.put("REVIEW_NAME", rs.getString("appraisal_name"));
				hmReviewData.put("REVIEW_STARTDATE", rs.getString("from_date"));
				hmReviewData.put("REVIEW_ENDDATE", rs.getString("to_date"));
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hmReviewData;
	}

	private void getSingleOpenWithoutMarksQueReadUnreadCount(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmReadUnreadCount = new HashMap<String, String>();
			pst = con.prepareStatement("Select count(*) as count,aqa.user_type_id,aqa.emp_id,aqa.read_status from appraisal_question_answer aqa,"
					+ "appraisal_question_details aqd where aqa.appraisal_id = ? and aqa.appraisal_freq_id= ? and aqa.appraisal_id = aqd.appraisal_id and "
					+ "aqa.question_id = aqd.question_id and aqd.answer_type = 12 and aqa.is_submit=true group by aqa.read_status,aqa.user_type_id,aqa.emp_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			// System.out.println("pst ============ > "+ pst);
			while (rs.next()) {
				hmReadUnreadCount.put(rs.getString("user_type_id") + "_" + rs.getString("emp_id") + "_" + rs.getString("read_status"), rs.getString("count"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmReadUnreadCount", hmReadUnreadCount);
			// System.out.println("hmReadUnreadCount =====> "+
			// hmReadUnreadCount);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getOrientationCount(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		Database db = new Database();
		db.setRequest(request);
		Map<String, String> orientationMemberMp = getOrientationMember();
		try {
			con = db.makeConnection(con);

			Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);

			Map<String, String> hmMemberMP = new HashMap<String, String>();

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,supervisor_emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id "
					+ " and is_alive= true and emp_per_id >0 order by supervisor_emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			// System.out.println("PST ===>> " + pst);
			Map<String, List<String>> hmHireracyLevelEmpIds = new LinkedHashMap<String, List<String>>();
			// List<String> alHireracyLevels = new ArrayList<String>();
			while (rs.next()) {
				List<String> alInner = hmHireracyLevelEmpIds.get(rs.getString("supervisor_emp_id"));
				if (alInner == null)
					alInner = new ArrayList<String>();
				if (uF.parseToInt(rs.getString("supervisor_emp_id")) == uF.parseToInt(rs.getString("emp_id"))) {
					continue;
				}
				alInner.add(rs.getString("emp_id"));
				hmHireracyLevelEmpIds.put(rs.getString("supervisor_emp_id"), alInner);
			}
			rs.close();
			pst.close();

			StringBuilder sbClildEmpIds = null;
			List<String> empIDList = new ArrayList<String>();
			sbClildEmpIds = getChildEmpIds(hmHireracyLevelEmpIds, strSessionEmpId, empIDList, sbClildEmpIds);

			StringBuilder sbEmpId = null;
			if (strSessionUserType != null && strSessionUserType.equals(MANAGER)) {
				sbQuery = new StringBuilder();
				sbQuery.append("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id  and appraisal_id=? "
						+ "and (supervisor_ids like '%,"
						+ strSessionEmpId
						+ ",%' or grand_supervisor_ids like '%,"
						+ strSessionEmpId
						+ ",%' or hod_ids like '%," + strSessionEmpId + ",%' ");
				if (sbClildEmpIds != null && !sbClildEmpIds.toString().equals("")) {
					sbQuery.append(" or epd.emp_per_id in (" + sbClildEmpIds.toString() + ") ");
				}
				sbQuery.append(") order by emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
				// pst = con.prepareStatement("select reviewee_id from
				// appraisal_reviewee_details ard, employee_personal_details epd
				// where epd.emp_per_id=ard.reviewee_id and supervisor_ids like
				// '%,"+strSessionEmpId+",%' and appraisal_id=? order by
				// emp_fname");
				pst.setInt(1, uF.parseToInt(getId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					if (sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rs.getString("reviewee_id"));
					} else {
						sbEmpId.append("," + rs.getString("reviewee_id"));
					}
				}
				rs.close();
				pst.close();
			}

			/*
			 * StringBuilder sbEmpId = null; if(strSessionUserType != null &&
			 * strSessionUserType.equals(MANAGER)) { pst = con.
			 * prepareStatement(
			 * "select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id and supervisor_ids like '%,"
			 * +strSessionEmpId+",%' and appraisal_id=? order by emp_fname");
			 * pst.setInt(1, uF.parseToInt(getId())); rs = pst.executeQuery();
			 * while (rs.next()) { if(sbEmpId == null) { sbEmpId = new
			 * StringBuilder(); sbEmpId.append(rs.getString("reviewee_id")); }
			 * else { sbEmpId.append(","+rs.getString("reviewee_id")); } }
			 * rs.close(); pst.close(); }
			 */

			pst = con.prepareStatement("select oriented_type,self_ids,is_anonymous_review from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			String oriented_type = "";
			String self_ids = "";
			boolean isAnonymousReview = false;
			while (rs.next()) {
				oriented_type = rs.getString("oriented_type");
				self_ids = rs.getString("self_ids");
				isAnonymousReview = rs.getBoolean("is_anonymous_review");
			}
			rs.close();
			pst.close();

			sbQuery = new StringBuilder();
			sbQuery.append("select * from appraisal_reviewee_details where appraisal_id=? ");
			if (sbEmpId != null) {
				sbQuery.append(" and reviewee_id in (" + sbEmpId.toString() + ") ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			Map<String, List<String>> hmRevieweeUserIds = new HashMap<String, List<String>>();
			List<String> selfList = new ArrayList<String>();
			while (rs.next()) {
				selfList.add(rs.getString("reviewee_id"));

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("subordinate_ids")); // 0
				innerList.add(rs.getString("peer_ids")); // 1
				innerList.add(rs.getString("other_peer_ids")); // 2
				innerList.add(rs.getString("supervisor_ids")); // 3
				innerList.add(rs.getString("grand_supervisor_ids")); // 4
				innerList.add(rs.getString("hr_ids")); // 5
				innerList.add(rs.getString("hod_ids")); // 6
				innerList.add(rs.getString("ceo_ids")); // 7
				innerList.add(rs.getString("other_ids")); // 8

				hmRevieweeUserIds.put(rs.getString("reviewee_id"), innerList);
			}
			rs.close();
			pst.close();

			// System.out.println("oriented_type ===>> " + oriented_type);

			List<String> memberList = CF.getOrientationMemberDetails(con, uF.parseToInt(oriented_type));
			// System.out.println("memberList ===>> " + memberList);

			Map<String, String> hmOrientMemberID = CF.getOrientMemberID(con);

			// if(oriented_type != null && !oriented_type.equals("5")){
			if (self_ids != null && !self_ids.equals("")) {

				sbQuery = new StringBuilder();
				sbQuery.append("select emp_image,emp_per_id from employee_personal_details where emp_per_id>0");
				if (sbEmpId != null) {
					sbQuery.append(" and emp_per_id in (" + sbEmpId.toString() + ") ");
				}
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				Map<String, String> empImageMap = new HashMap<String, String>();
				while (rs.next()) {
					empImageMap.put(rs.getString("emp_per_id"), rs.getString("emp_image"));
				}
				rs.close();
				pst.close();

				/*
				 * List<String> memberList = Arrays.asList(mem.split(","));
				 * for(int i = 0; i < memberList.size(); i++) {
				 * hmOrientationCount.put(memberList.get(i),
				 * orientationMemberMp.get(memberList.get(i))); }
				 */

				getEmpWlocation(self_ids);
				// self_ids=self_ids.substring(1,self_ids.length()-1);
				// List<String> selfList = Arrays.asList(self_ids.split(","));

				for (int j = 0; selfList != null && !selfList.isEmpty() && j < selfList.size(); j++) {

					pst = con
							.prepareStatement("select emp_id,user_id,user_type_id from appraisal_question_answer where appraisal_id=? and emp_id=? "
									+ " and appraisal_freq_id=? and appraisal_question_answer_id in (select max(appraisal_question_answer_id) from appraisal_question_answer "
									+ " where appraisal_id=? and emp_id=? and appraisal_freq_id=? and is_submit=true group by user_type_id,user_id) and is_submit=true");
					pst.setInt(1, uF.parseToInt(getId()));
					pst.setInt(2, uF.parseToInt(selfList.get(j).trim()));
					pst.setInt(3, uF.parseToInt(getAppFreqId()));
					pst.setInt(4, uF.parseToInt(getId()));
					pst.setInt(5, uF.parseToInt(selfList.get(j).trim()));
					pst.setInt(6, uF.parseToInt(getAppFreqId()));
					rs = pst.executeQuery();

					Map<String, String> hmCheckAppraisal = new HashMap<String, String>();
					Map<String, List<String>> hmCheckUsers = new HashMap<String, List<String>>();
					while (rs.next()) {
						String key = rs.getString("emp_id") + "_" + rs.getString("user_id") + "_" + rs.getString("user_type_id");
						hmCheckAppraisal.put(key, rs.getString("emp_id"));

						List<String> innerList = new ArrayList<String>();
						if (rs.getString("user_type_id").equals("7")) {
							innerList = hmCheckUsers.get(HRMANAGER);
							if (innerList == null)
								innerList = new ArrayList<String>();
							if (!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckUsers.put(HRMANAGER, innerList);
						} else if (rs.getString("user_type_id").equals("2")) {
							innerList = hmCheckUsers.get(MANAGER);
							if (innerList == null)
								innerList = new ArrayList<String>();
							if (!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckUsers.put(MANAGER, innerList);
						} else if (rs.getString("user_type_id").equals("8")) {
							innerList = hmCheckUsers.get("GroupHead");
							if (innerList == null)
								innerList = new ArrayList<String>();
							if (!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckUsers.put("GroupHead", innerList);
						} else if (rs.getString("user_type_id").equals("5")) {
							innerList = hmCheckUsers.get(CEO);
							if (innerList == null)
								innerList = new ArrayList<String>();
							if (!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckUsers.put(CEO, innerList);
						} else if (rs.getString("user_type_id").equals("13")) {
							innerList = hmCheckUsers.get(HOD);
							if (innerList == null)
								innerList = new ArrayList<String>();
							if (!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckUsers.put(HOD, innerList);
						} else if (rs.getString("user_type_id").equals("6")) {
							innerList = hmCheckUsers.get("Sub-ordinate");
							if (innerList == null)
								innerList = new ArrayList<String>();
							if (!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckUsers.put("Sub-ordinate", innerList);
						} else if (rs.getString("user_type_id").equals("4")) {
							innerList = hmCheckUsers.get("Peer");
							if (innerList == null)
								innerList = new ArrayList<String>();
							if (!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckUsers.put("Peer", innerList);
						} else if (rs.getString("user_type_id").equals("14")) {
							innerList = hmCheckUsers.get("Other Peer");
							if (innerList == null)
								innerList = new ArrayList<String>();
							if (!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckUsers.put("Other Peer", innerList);
						} else if (rs.getString("user_type_id").equals("10")) {
							innerList = hmCheckUsers.get("Anyone");
							if (innerList == null)
								innerList = new ArrayList<String>();
							if (!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckUsers.put("Anyone", innerList);
						}
					}
					rs.close();
					pst.close();

					// System.out.println("hmCheckAppraisal ===>> " +
					// hmCheckAppraisal);
					// System.out.println("hmCheckUsers ===>> " + hmCheckUsers);

					StringBuilder sbMemList = new StringBuilder();

					// self
					if (hmOrientMemberID.get("Self") != null && memberList != null && memberList.contains(hmOrientMemberID.get("Self"))) {
						String brdrColor = "red";
						if (hmCheckAppraisal != null && hmCheckAppraisal.containsKey(selfList.get(j).trim() + "_" + selfList.get(j).trim() + "_3")) {
							brdrColor = "green";
						}
						if (isAnonymousReview && strSessionUserType != null && strSessionUserType.equals(EMPLOYEE)) {
							sbMemList.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "
									+ brdrColor + "\" " + " border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-SELF)\"/>");
						} else {
							sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('" + selfList.get(j) + "','"
									+ hmEmpName.get(selfList.get(j)) + "')\" >"
									+ "<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "
									+ brdrColor + "\" " + "data-original=\"" + CF.getStrDocRetriveLocation() + I_PEOPLE + "/" + I_IMAGE + "/"
									+ selfList.get(j).trim() + "/" + I_16x16 + "/" + empImageMap.get(selfList.get(j).trim()) + "\" "
									+ "border=\"0\" height=\"16px\" width=\"16px;\" title=\"" + hmEmpName.get(selfList.get(j)) + "(Role-SELF)\"/></a>");
						}
					}

					// HRMANAGER
					List<String> usersList = hmRevieweeUserIds.get(selfList.get(j).trim());
					if (hmOrientMemberID.get("HR") != null && memberList != null && memberList.contains(hmOrientMemberID.get("HR"))) {
						int cnt = 0;
						sbMemList = new StringBuilder();
						/*
						 * if(hmCheckUsers.get(HRMANAGER)!=null) { //
						 * System.out.println("hmCheckHR.get(HRMANAGER) ===>> "
						 * + hmCheckHR.get(HRMANAGER)); List<String> innerList =
						 * hmCheckUsers.get(HRMANAGER); for(int i=0;
						 * i<innerList.size(); i++) { cnt++; sbMemList. append(
						 * "<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"
						 * +innerList.get(i)+"','"+hmEmpName.get(innerList.get(i
						 * )) +
						 * "')\" ><img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid green\" data-original=\""
						 * +CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+
						 * "/"+innerList.get(i)+"/"+I_16x16+"/"+empImageMap.get(
						 * innerList.get(i))+"\" border=\"0\" height=\"16px\" "
						 * + "width=\"16px;\" title=\""+hmEmpName.get(innerList.
						 * get(i))+"(Role-HR)\"/></a>"); } } else {
						 */
						List<String> hrList = new ArrayList<String>();
						if (usersList.get(5) != null && !usersList.get(5).equals("")) {
							hrList = Arrays.asList(usersList.get(5).split(","));
						}
						for (int i = 0; hrList != null && !hrList.isEmpty() && i < hrList.size(); i++) {
							// System.out.println("hrList.get(i).trim() ===>> "
							// + hrList.get(i).trim());
							if (!hrList.get(i).trim().equals("")) {
								/*
								 * if (cnt > 7) { break; } else { cnt++; }
								 */
								String brdrColor = "red";
								if (hmCheckAppraisal != null
										&& hmCheckAppraisal.containsKey(selfList.get(j).trim() + "_" + hrList.get(i).trim() + "_" + hmOrientMemberID.get("HR"))) {
									brdrColor = "green";
								}
								if (isAnonymousReview && strSessionUserType != null && strSessionUserType.equals(EMPLOYEE)) {
									sbMemList
											.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "
													+ brdrColor + "\" " + " border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-HR)\"/>");
								} else {
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"
											+ hrList.get(i).trim() + "','" + hmEmpName.get(hrList.get(i).trim()) + "')\" >"
											+ "<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid " + brdrColor
											+ "\" data-original=\"" + CF.getStrDocRetriveLocation() + I_PEOPLE + "/" + I_IMAGE + "/" + hrList.get(i).trim()
											+ "/" + I_16x16 + "/" + empImageMap.get(hrList.get(i).trim()) + "\" border=\"0\" height=\"16px\" "
											+ "width=\"16px;\" title=\"" + hmEmpName.get(hrList.get(i).trim()) + "(Role-HR)\"/></a>");
								}
							}
						}
						// }
						hmMemberMP.put(selfList.get(j).trim() + "_" + hmOrientMemberID.get("HR"), sbMemList.toString());
					}
					// System.out.println("hmMemberMP ===>> " + hmMemberMP);

					// Manager
					if (hmOrientMemberID.get("Manager") != null && memberList != null && memberList.contains(hmOrientMemberID.get("Manager"))) {
						int cnt = 0;
						sbMemList = new StringBuilder();
						/*
						 * if(hmCheckUsers.get(MANAGER)!=null) { List<String>
						 * innerList = hmCheckUsers.get(MANAGER); for(int i=0;
						 * i<innerList.size(); i++) { cnt++; sbMemList. append(
						 * "<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"
						 * +innerList.get(i)+"','"+hmEmpName.get(innerList.get(i
						 * ))+"')\" >" +
						 * "<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\"userImages/"
						 * +CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+
						 * "/"+innerList.get(i)+"/"+I_16x16+"/"+empImageMap.get(
						 * innerList.get(i))+"\" border=\"0\" height=\"16px\" "
						 * + "width=\"16px;\" title=\""+hmEmpName.get(innerList.
						 * get(i))+"(Role-Manager)\"/></a>"); } } else {
						 */
						List<String> managerList = new ArrayList<String>();
						if (usersList.get(3) != null && !usersList.get(3).equals("")) {
							managerList = Arrays.asList(usersList.get(3).split(","));
						}
						for (int i = 0; managerList != null && !managerList.isEmpty() && i < managerList.size(); i++) {
							if (!managerList.get(i).trim().equals("")) {
								/*
								 * if (cnt > 7) { // sbMemList.append("<a //
								 * href=\"javascript:void(0);\" //
								 * style=\"margin-right: 4px;\" //
								 * onclick=\"seeEmpList('"
								 * +selfList.get(j)+"','"+
								 * id+"','"+getAppFreqId()+"');\" //
								 * class=\"OR testa\">more..</a>"); break; }
								 * else { cnt++; }
								 */
								String brdrColor = "red";
								if (hmCheckAppraisal != null
										&& hmCheckAppraisal.containsKey(selfList.get(j).trim() + "_" + managerList.get(i).trim() + "_"
												+ hmOrientMemberID.get("Manager"))) {
									brdrColor = "green";
								}
								if (isAnonymousReview && strSessionUserType != null && strSessionUserType.equals(EMPLOYEE)) {
									sbMemList
											.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "
													+ brdrColor
													+ "\" "
													+ " border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-Manager)\"/>");
								} else {
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"
											+ managerList.get(i).trim() + "','" + hmEmpName.get(managerList.get(i).trim()) + "')\" >"
											+ "<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "
											+ brdrColor + "\"  data-original=\"" + CF.getStrDocRetriveLocation() + I_PEOPLE + "/" + I_IMAGE + "/"
											+ managerList.get(i).trim() + "/" + I_16x16 + "/" + empImageMap.get(managerList.get(i).trim())
											+ "\" border=\"0\" height=\"16px\" " + "width=\"16px;\" title=\"" + hmEmpName.get(managerList.get(i).trim())
											+ "(Role-Manager)\"/></a>");
								}
							}
						}
						// }
						hmMemberMP.put(selfList.get(j).trim() + "_" + hmOrientMemberID.get("Manager"), sbMemList.toString());
					}

					// Grand Manager
					if (hmOrientMemberID.get("GroupHead") != null && memberList != null && memberList.contains(hmOrientMemberID.get("GroupHead"))) {
						int cnt = 0;
						sbMemList = new StringBuilder();
						List<String> gManagerList = new ArrayList<String>();
						if (usersList.get(4) != null && !usersList.get(4).equals("")) {
							gManagerList = Arrays.asList(usersList.get(4).split(","));
						}
						for (int i = 0; gManagerList != null && !gManagerList.isEmpty() && i < gManagerList.size(); i++) {
							if (!gManagerList.get(i).trim().equals("")) {
								/*
								 * if (cnt > 7) { break; } else { cnt++; }
								 */
								String brdrColor = "red";
								if (hmCheckAppraisal != null
										&& hmCheckAppraisal.containsKey(selfList.get(j).trim() + "_" + gManagerList.get(i).trim() + "_"
												+ hmOrientMemberID.get("GroupHead"))) {
									brdrColor = "green";
								}
								if (isAnonymousReview && strSessionUserType != null && strSessionUserType.equals(EMPLOYEE)) {
									sbMemList
											.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "
													+ brdrColor
													+ "\" "
													+ " border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-Grand Manager)\"/>");
								} else {
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"
											+ gManagerList.get(i).trim() + "','" + hmEmpName.get(gManagerList.get(i).trim()) + "')\" >"
											+ "<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "
											+ brdrColor + "\"  data-original=\"" + CF.getStrDocRetriveLocation() + I_PEOPLE + "/" + I_IMAGE + "/"
											+ gManagerList.get(i).trim() + "/" + I_16x16 + "/" + empImageMap.get(gManagerList.get(i).trim())
											+ "\" border=\"0\" height=\"16px\" " + "width=\"16px;\" title=\"" + hmEmpName.get(gManagerList.get(i).trim())
											+ "(Role-Grand Manager)\"/></a>");
								}
							}
						}
						hmMemberMP.put(selfList.get(j).trim() + "_" + hmOrientMemberID.get("GroupHead"), sbMemList.toString());
					}

					// peer
					if (hmOrientMemberID.get("Peer") != null && memberList != null && memberList.contains(hmOrientMemberID.get("Peer"))) {
						int cnt = 0;
						sbMemList = new StringBuilder();
						List<String> peerList = new ArrayList<String>();
						if (usersList.get(1) != null && !usersList.get(1).equals("")) {
							peerList = Arrays.asList(usersList.get(1).split(","));
						}
						for (int i = 0; peerList != null && !peerList.isEmpty() && i < peerList.size(); i++) {
							if (!peerList.get(i).trim().equals("") && uF.parseToInt(peerList.get(i).trim()) != uF.parseToInt(selfList.get(j).trim())) {
								/*
								 * if (cnt > 7) { break; } else { cnt++; }
								 */
								String brdrColor = "red";
								if (hmCheckAppraisal != null
										&& hmCheckAppraisal.containsKey(selfList.get(j).trim() + "_" + peerList.get(i).trim() + "_"
												+ hmOrientMemberID.get("Peer"))) {
									brdrColor = "green";
								}
								if (isAnonymousReview && strSessionUserType != null && strSessionUserType.equals(EMPLOYEE)) {
									sbMemList
											.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "
													+ brdrColor
													+ "\" "
													+ " border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-Peer)\"/>");
								} else {
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"
											+ peerList.get(i).trim() + "','" + hmEmpName.get(peerList.get(i).trim()) + "')\" >"
											+ "<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "
											+ brdrColor + "\"  " + "data-original=\"" + CF.getStrDocRetriveLocation() + I_PEOPLE + "/" + I_IMAGE + "/"
											+ peerList.get(i).trim() + "/" + I_16x16 + "/" + empImageMap.get(peerList.get(i).trim())
											+ "\" border=\"0\" height=\"16px\" " + "width=\"16px;\" title=\"" + hmEmpName.get(peerList.get(i).trim())
											+ "(Role-Peer)\"/></a>");
								}
							}
						}
						hmMemberMP.put(selfList.get(j).trim() + "_" + hmOrientMemberID.get("Peer"), sbMemList.toString());
					}

					// other peer
					if (hmOrientMemberID.get("Other Peer") != null && memberList != null && memberList.contains(hmOrientMemberID.get("Other Peer"))) {
						int cnt = 0;
						sbMemList = new StringBuilder();
						List<String> otherPeerList = new ArrayList<String>();
						if (usersList.get(2) != null && !usersList.get(2).equals("")) {
							otherPeerList = Arrays.asList(usersList.get(2).split(","));
						}
						for (int i = 0; otherPeerList != null && !otherPeerList.isEmpty() && i < otherPeerList.size(); i++) {
							if (!otherPeerList.get(i).trim().equals("") && uF.parseToInt(otherPeerList.get(i).trim()) != uF.parseToInt(selfList.get(j).trim())) {
								/*
								 * if (cnt > 7) { break; } else { cnt++; }
								 */
								String brdrColor = "red";
								if (hmCheckAppraisal != null
										&& hmCheckAppraisal.containsKey(selfList.get(j).trim() + "_" + otherPeerList.get(i).trim() + "_"
												+ hmOrientMemberID.get("Other Peer"))) {
									brdrColor = "green";
								}
								if (isAnonymousReview && strSessionUserType != null && strSessionUserType.equals(EMPLOYEE)) {
									sbMemList
											.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "
													+ brdrColor
													+ "\" "
													+ " border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-Other Peer)\"/>");
								} else {
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"
											+ otherPeerList.get(i).trim() + "','" + hmEmpName.get(otherPeerList.get(i).trim()) + "')\" >"
											+ "<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "
											+ brdrColor + "\"  " + "data-original=\"" + CF.getStrDocRetriveLocation() + I_PEOPLE + "/" + I_IMAGE + "/"
											+ otherPeerList.get(i).trim() + "/" + I_16x16 + "/" + empImageMap.get(otherPeerList.get(i).trim())
											+ "\" border=\"0\" height=\"16px\" " + "width=\"16px;\" title=\"" + hmEmpName.get(otherPeerList.get(i).trim())
											+ "(Role-Other Peer)\"/></a>");
								}
							}
						}
						hmMemberMP.put(selfList.get(j).trim() + "_" + hmOrientMemberID.get("Other Peer"), sbMemList.toString());
					}

					// CEO
					if (hmOrientMemberID.get("CEO") != null && memberList != null && memberList.contains(hmOrientMemberID.get("CEO"))) {
						int cnt = 0;
						sbMemList = new StringBuilder();
						/*
						 * if(hmCheckUsers.get(CEO)!=null) { List<String>
						 * innerList = hmCheckUsers.get(CEO); for(int i=0;
						 * i<innerList.size(); i++) { cnt++; sbMemList. append(
						 * "<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"
						 * +innerList.get(i)+"','"+hmEmpName.get(innerList.get(i
						 * ))+"')\" >" +
						 * "<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\"userImages/"
						 * +CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+
						 * "/"+innerList.get(i)+"/"+I_16x16+"/"+empImageMap.get(
						 * innerList.get(i))+"\" border=\"0\" height=\"16px\" "
						 * + "width=\"16px;\" title=\""+hmEmpName.get(innerList.
						 * get(i))+"(Role-CEO)\"/></a>"); } } else {
						 */
						List<String> ceoList = new ArrayList<String>();
						if (usersList.get(7) != null && !usersList.get(7).equals("")) {
							ceoList = Arrays.asList(usersList.get(7).split(","));
						}
						for (int i = 0; ceoList != null && !ceoList.isEmpty() && i < ceoList.size(); i++) {
							if (!ceoList.get(i).trim().equals("")) {
								/*
								 * if (cnt > 7) { break; } else { cnt++; }
								 */
								String brdrColor = "red";
								if (hmCheckAppraisal != null
										&& hmCheckAppraisal.containsKey(selfList.get(j).trim() + "_" + ceoList.get(i).trim() + "_"
												+ hmOrientMemberID.get("CEO"))) {
									brdrColor = "green";
								}
								if (isAnonymousReview && strSessionUserType != null && strSessionUserType.equals(EMPLOYEE)) {
									sbMemList
											.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "
													+ brdrColor + "\" " + " border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-CEO)\"/>");
								} else {
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"
											+ ceoList.get(i).trim() + "','" + hmEmpName.get(ceoList.get(i).trim()) + "')\" >"
											+ "<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "
											+ brdrColor + "\"  data-original=\"" + CF.getStrDocRetriveLocation() + I_PEOPLE + "/" + I_IMAGE + "/"
											+ ceoList.get(i).trim() + "/" + I_16x16 + "/" + empImageMap.get(ceoList.get(i).trim())
											+ "\" border=\"0\" height=\"16px\" " + "width=\"16px;\" title=\"" + hmEmpName.get(ceoList.get(i).trim())
											+ "(Role-CEO)\"/></a>");
								}
							}
						}
						// }
						hmMemberMP.put(selfList.get(j).trim() + "_" + hmOrientMemberID.get("CEO"), sbMemList.toString());
					}

					// HOD
					if (hmOrientMemberID.get("HOD") != null && memberList != null && memberList.contains(hmOrientMemberID.get("HOD"))) {
						int cnt = 0;
						sbMemList = new StringBuilder();
						/*
						 * if(hmCheckHod.get(HOD)!=null) { List<String>
						 * innerList = hmCheckHod.get(HOD); for(int i=0;
						 * i<innerList.size(); i++) { cnt++; sbMemList. append(
						 * "<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"
						 * +innerList.get(i)+"','"+hmEmpName.get(innerList.get(i
						 * ))+"')\" >" +
						 * "<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\"userImages/"
						 * +CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+
						 * "/"+innerList.get(i)+"/"+I_16x16+"/"+empImageMap.get(
						 * innerList.get(i))+"\" border=\"0\" height=\"16px\" "
						 * + "width=\"16px;\" title=\""+hmEmpName.get(innerList.
						 * get(i))+"(Role-HOD)\"/></a>"); } } else {
						 */
						List<String> hodList = new ArrayList<String>();
						if (usersList.get(6) != null && !usersList.get(6).equals("")) {
							hodList = Arrays.asList(usersList.get(6).split(","));
						}
						for (int i = 0; hodList != null && !hodList.isEmpty() && i < hodList.size(); i++) {
							if (!hodList.get(i).trim().equals("")) {
								/*
								 * if (cnt > 7) { break; } else { cnt++; }
								 */
								String brdrColor = "red";
								if (hmCheckAppraisal != null
										&& hmCheckAppraisal.containsKey(selfList.get(j).trim() + "_" + hodList.get(i).trim() + "_"
												+ hmOrientMemberID.get("HOD"))) {
									brdrColor = "green";
								}
								if (isAnonymousReview && strSessionUserType != null && strSessionUserType.equals(EMPLOYEE)) {
									sbMemList
											.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "
													+ brdrColor + "\" " + " border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-HOD)\"/>");
								} else {
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"
											+ hodList.get(i).trim() + "','" + hmEmpName.get(hodList.get(i).trim()) + "')\" >"
											+ "<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "
											+ brdrColor + "\"  data-original=\"" + CF.getStrDocRetriveLocation() + I_PEOPLE + "/" + I_IMAGE + "/"
											+ hodList.get(i).trim() + "/" + I_16x16 + "/" + empImageMap.get(hodList.get(i).trim())
											+ "\" border=\"0\" height=\"16px\" " + "width=\"16px;\" title=\"" + hmEmpName.get(hodList.get(i).trim())
											+ "(Role-HOD)\"/></a>");
								}
							}
						}
						// }
						hmMemberMP.put(selfList.get(j).trim() + "_" + hmOrientMemberID.get("HOD"), sbMemList.toString());
					}

					// other
					if (hmOrientMemberID.get("Anyone") != null && memberList != null && memberList.contains(hmOrientMemberID.get("Anyone"))) {
						int cnt = 0;
						sbMemList = new StringBuilder();
						List<String> othersList = new ArrayList<String>();
						if (usersList.get(8) != null && !usersList.get(8).equals("")) {
							othersList = Arrays.asList(usersList.get(8).split(","));
						}
						for (int i = 0; othersList != null && !othersList.isEmpty() && i < othersList.size(); i++) {
							if (!othersList.get(i).trim().equals("")) {
								if (cnt > 7) {
									break;
								} else {
									cnt++;
								}
								String brdrColor = "red";
								if (hmCheckAppraisal != null
										&& hmCheckAppraisal.containsKey(selfList.get(j).trim() + "_" + othersList.get(i).trim() + "_"
												+ hmOrientMemberID.get("Anyone"))) {
									brdrColor = "green";
								}
								if (isAnonymousReview && strSessionUserType != null && strSessionUserType.equals(EMPLOYEE)) {
									sbMemList
											.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "
													+ brdrColor
													+ "\" "
													+ " border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-Anyone)\"/>");
								} else {
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"
											+ othersList.get(i).trim() + "','" + hmEmpName.get(othersList.get(i).trim()) + "')\" >"
											+ "<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "
											+ brdrColor + "\"  data-original=\"" + CF.getStrDocRetriveLocation() + I_PEOPLE + "/" + I_IMAGE + "/"
											+ othersList.get(i).trim() + "/" + I_16x16 + "/" + empImageMap.get(othersList.get(i).trim())
											+ "\" border=\"0\" height=\"16px\" " + "width=\"16px;\" title=\"" + hmEmpName.get(othersList.get(i).trim())
											+ "(Role-Anyone)\"/></a>");
								}
							}
						}
						hmMemberMP.put(selfList.get(j).trim() + "_" + hmOrientMemberID.get("Anyone"), sbMemList.toString());
					}

					// Sub-ordinate
					if (hmOrientMemberID.get("Sub-ordinate") != null && memberList != null && memberList.contains(hmOrientMemberID.get("Sub-ordinate"))) {
						int cnt = 0;
						sbMemList = new StringBuilder();
						List<String> othersList = new ArrayList<String>();
						if (usersList.get(0) != null && !usersList.get(0).equals("")) {
							othersList = Arrays.asList(usersList.get(0).split(","));
						}
						for (int i = 0; othersList != null && !othersList.isEmpty() && i < othersList.size(); i++) {
							if (!othersList.get(i).trim().equals("")) {
								/*
								 * if (cnt > 7) { break; } else { cnt++; }
								 */
								String brdrColor = "red";
								if (hmCheckAppraisal != null
										&& hmCheckAppraisal.containsKey(selfList.get(j).trim() + "_" + othersList.get(i).trim() + "_"
												+ hmOrientMemberID.get("Sub-ordinate"))) {
									brdrColor = "green";
								}
								if (isAnonymousReview && strSessionUserType != null && strSessionUserType.equals(EMPLOYEE)) {
									sbMemList
											.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "
													+ brdrColor
													+ "\" "
													+ " border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-Sub-ordinate)\"/>");
								} else {
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"
											+ othersList.get(i).trim() + "','" + hmEmpName.get(othersList.get(i).trim()) + "')\" >"
											+ "<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "
											+ brdrColor + "\"  data-original=\"" + CF.getStrDocRetriveLocation() + I_PEOPLE + "/" + I_IMAGE + "/"
											+ othersList.get(i).trim() + "/" + I_16x16 + "/" + empImageMap.get(othersList.get(i).trim())
											+ "\" border=\"0\" height=\"16px\" " + "width=\"16px;\" title=\"" + hmEmpName.get(othersList.get(i).trim())
											+ "(Role-Sub-ordinate)\"/></a>");
								}
							}
						}
						hmMemberMP.put(selfList.get(j).trim() + "_" + hmOrientMemberID.get("Sub-ordinate"), sbMemList.toString());
					}

					/*
					 * if(hmOrientMemberID.get("Anyone") != null &&
					 * memberList!=null &&
					 * memberList.contains(hmOrientMemberID.get("Anyone"))) {
					 * String othrIds = ""; if(other_ids != null &&
					 * other_ids.trim().length() > 1){ othrIds =
					 * other_ids.substring(1, other_ids.trim().length()-1); pst
					 * = con. prepareStatement(
					 * "select emp_per_id from employee_personal_details where  emp_per_id in("
					 * + othrIds + ")"); rs = pst.executeQuery(); while
					 * (rs.next()) { if(cnt>7) { break; } else { cnt++; } String
					 * brdrColor = "red"; if(hmCheckAppraisal!=null &&
					 * hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+
					 * rs.getString("emp_per_id")+"_10")){ brdrColor = "green";
					 * } sbMemList. append(
					 * "<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"
					 * +rs.getString("emp_per_id")+"','"+hmEmpName.get(rs.
					 * getString("emp_per_id"))+"')\" >" +
					 * "<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "
					 * +brdrColor+"\" data-original=\""+CF.
					 * getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.
					 * getString("emp_per_id")+"/"+I_16x16+"/"+empImageMap.get(
					 * rs.getString("emp_per_id"))
					 * +"\" border=\"0\" height=\"16px\" " +
					 * "width=\"16px;\" title=\""+hmEmpName.get(rs.getString(
					 * "emp_per_id"))+"(Role-Anyone)\"/></a>"); // } }
					 * rs.close(); pst.close();
					 * 
					 * } }
					 */

					// ************************ Here code For more user need to
					// implement ********************
					/*
					 * if(cnt>7) { sbMemList. append(
					 * "<a href=\"javascript:void(0);\" style=\"margin-right: 4px;\" onclick=\"seeEmpList('"
					 * +selfList.get(j)+"','"+id+"','"+getAppFreqId()
					 * +"');\" class=\"OR testa\">more..</a>"); }
					 * hmMemberMP.put(selfList.get(j).trim(),
					 * sbMemList.toString());
					 */
				}
			}

			/*
			 * } else {
			 * 
			 * 
			 * if(self_ids!=null && !self_ids.equals("")){ pst=con.
			 * prepareStatement
			 * ("select emp_image,emp_per_id from employee_personal_details " );
			 * rs=pst.executeQuery(); Map<String,String> empImageMap=new
			 * HashMap<String,String>(); while(rs.next()){
			 * empImageMap.put(rs.getString("emp_per_id"),rs.getString(
			 * "emp_image")); } rs.close(); pst.close();
			 * 
			 * 
			 * 
			 * getEmpWlocation(self_ids);
			 * self_ids=self_ids.substring(1,self_ids.length()-1); List<String>
			 * selfList = Arrays.asList(self_ids.split(","));
			 * 
			 * for(int j=0;selfList!=null && !selfList.isEmpty() &&
			 * j<selfList.size();j++){
			 * 
			 * 
			 * pst=con. prepareStatement(
			 * "select emp_id,user_id,user_type_id from appraisal_question_answer where appraisal_id=? and emp_id=? "
			 * +
			 * " and appraisal_question_answer_id in(select max(appraisal_question_answer_id) from appraisal_question_answer "
			 * +
			 * " where appraisal_id=? and emp_id=? group by user_type_id,user_id)"
			 * ); pst.setInt(1, uF.parseToInt(id)); pst.setInt(2,
			 * uF.parseToInt(selfList.get(j).trim())); pst.setInt(3,
			 * uF.parseToInt(id)); pst.setInt(4,
			 * uF.parseToInt(selfList.get(j).trim())); rs=pst.executeQuery(); //
			 * System.out.println("pst ===========> "+pst); Map<String,String>
			 * hmCheckAppraisal=new HashMap<String, String>(); //
			 * Map<String,String> hmCheckHR=new HashMap<String, String>(); //
			 * Map<String,String> hmCheckMgr=new HashMap<String, String>();
			 * while(rs.next()){ String
			 * key=rs.getString("emp_id")+"_"+rs.getString("user_id")+"_"+rs.
			 * getString("user_type_id"); hmCheckAppraisal.put(key,
			 * rs.getString("emp_id"));
			 * 
			 * } rs.close(); pst.close();
			 * 
			 * StringBuilder sbMemList=new StringBuilder(); int cnt=0;
			 * 
			 * //self if(hmOrientMemberID.get("Self") != null &&
			 * memberList.contains(hmOrientMemberID.get("Self"))) {
			 * if(hmCheckAppraisal!=null &&
			 * hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+selfList.
			 * get(j).trim()+"_3")){ sbMemList. append(
			 * "<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\""
			 * +CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+selfList.
			 * get(j)+"/"+I_16x16+"/"+empImageMap.get(selfList.get(j))
			 * +"\" border=\"0\" height=\"16px\" " +
			 * "width=\"16px;\" title=\""+hmEmpName.get(selfList.get(j))+
			 * "(Role-SELF)\"/></span>"); }else{ sbMemList. append(
			 * "<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"
			 * +selfList.get(j)+"','"+hmEmpName.get(selfList.get(j))+"')\" >" +
			 * "<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid red\"  data-original=\""
			 * +CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+selfList.
			 * get(j)+"/"+I_16x16+"/"+empImageMap.get(selfList.get(j))
			 * +"\" border=\"0\" height=\"16px\" " +
			 * "width=\"16px;\" title=\""+hmEmpName.get(selfList.get(j))+
			 * "(Role-SELF)\"/></a>"); } }
			 * 
			 * 
			 * 
			 * 
			 * //other if(hmOrientMemberID.get("Anyone") != null &&
			 * memberList.contains(hmOrientMemberID.get("Anyone"))) { String
			 * othrIds = ""; if(other_ids.trim().length() > 1){ othrIds =
			 * other_ids.substring(1, other_ids.trim().length()-1); } //
			 * System.out.println("othrIds =====> "+othrIds); pst = con.
			 * prepareStatement(
			 * "select emp_per_id from employee_personal_details where  emp_per_id in("
			 * + othrIds + ")"); // System.out.println("pst=====>"+pst); rs =
			 * pst.executeQuery(); while (rs.next()) { cnt++; if(cnt>7){
			 * sbMemList. append(
			 * "<a href=\"javascript:void(0);\" style=\"margin-right: 4px;\" onclick=\"seeEmpList('"
			 * +selfList.get(j)+"','"+id+"');\" class=\"OR testa\">more..</a>");
			 * break; }
			 * 
			 * if(hmCheckAppraisal!=null &&
			 * hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+rs.
			 * getString("emp_per_id")+"_10")){ sbMemList. append(
			 * "<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  "
			 * + "data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+
			 * I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_16x16+"/"+
			 * empImageMap.get(rs.getString("emp_per_id"))
			 * +"\" border=\"0\" height=\"16px\" " +
			 * "width=\"16px;\" title=\""+hmEmpName.get(rs.getString(
			 * "emp_per_id"))+"(Role-Anyone)\"/></span>"); }else{ sbMemList.
			 * append(
			 * "<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"
			 * +rs.getString("emp_per_id")+"','"+hmEmpName.get(rs.getString(
			 * "emp_per_id"))+"')\" >" +
			 * "<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid red\"  "
			 * + "data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+
			 * I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_16x16+"/"+
			 * empImageMap.get(rs.getString("emp_per_id"))
			 * +"\" border=\"0\" height=\"16px\" " +
			 * "width=\"16px;\" title=\""+hmEmpName.get(rs.getString(
			 * "emp_per_id"))+"(Role-Anyone)\"/></a>"); } } rs.close();
			 * pst.close(); } hmMemberMP.put(selfList.get(j).trim(),
			 * sbMemList.toString()); } } }
			 */

			request.setAttribute("hmMemberMP", hmMemberMP);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void getEmpSupervisor() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		Map<String, String> hmEmpSuperVisor = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("Select emp_id,supervisor_emp_id from employee_official_details");
			rs = pst.executeQuery();

			while (rs.next()) {
				hmEmpSuperVisor.put(rs.getString("emp_id"), rs.getString("supervisor_emp_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpSuperVisor", hmEmpSuperVisor);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void getRemarks() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		Database db = new Database();
		db.setRequest(request);
		String remark = null;
		String strApprovedBy = null;
		Map<String, String> hmRemark = new HashMap<String, String>();
		boolean flag = false;
		try {
			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,supervisor_emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id "
					+ " and is_alive= true and emp_per_id >0 order by supervisor_emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			// System.out.println("PST ===>> " + pst);
			Map<String, List<String>> hmHireracyLevelEmpIds = new LinkedHashMap<String, List<String>>();
			// List<String> alHireracyLevels = new ArrayList<String>();
			while (rs.next()) {
				List<String> alInner = hmHireracyLevelEmpIds.get(rs.getString("supervisor_emp_id"));
				if (alInner == null)
					alInner = new ArrayList<String>();
				if (uF.parseToInt(rs.getString("supervisor_emp_id")) == uF.parseToInt(rs.getString("emp_id"))) {
					continue;
				}
				alInner.add(rs.getString("emp_id"));
				hmHireracyLevelEmpIds.put(rs.getString("supervisor_emp_id"), alInner);
			}
			rs.close();
			pst.close();

			StringBuilder sbClildEmpIds = null;
			List<String> empIDList = new ArrayList<String>();
			sbClildEmpIds = getChildEmpIds(hmHireracyLevelEmpIds, strSessionEmpId, empIDList, sbClildEmpIds);

			StringBuilder sbEmpId = null;
			if (strSessionUserType != null && strSessionUserType.equals(MANAGER)) {
				sbQuery = new StringBuilder();
				sbQuery.append("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id  and appraisal_id=? "
						+ "and (supervisor_ids like '%,"
						+ strSessionEmpId
						+ ",%' or grand_supervisor_ids like '%,"
						+ strSessionEmpId
						+ ",%' or hod_ids like '%," + strSessionEmpId + ",%' ");
				if (sbClildEmpIds != null && !sbClildEmpIds.toString().equals("")) {
					sbQuery.append(" or epd.emp_per_id in (" + sbClildEmpIds.toString() + ") ");
				}
				sbQuery.append(") order by emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
				// pst = con.prepareStatement("select reviewee_id from
				// appraisal_reviewee_details ard, employee_personal_details epd
				// where epd.emp_per_id=ard.reviewee_id and supervisor_ids like
				// '%,"+strSessionEmpId+",%' and appraisal_id=? order by
				// emp_fname");
				pst.setInt(1, uF.parseToInt(getId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					if (sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rs.getString("reviewee_id"));
					} else {
						sbEmpId.append("," + rs.getString("reviewee_id"));
					}
				}
				rs.close();
				pst.close();
			}

			/*
			 * StringBuilder sbEmpId = null; if(strSessionUserType != null &&
			 * strSessionUserType.equals(MANAGER)) { pst = con.
			 * prepareStatement(
			 * "select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id and supervisor_ids like '%,"
			 * +strSessionEmpId+",%' and appraisal_id=? order by emp_fname");
			 * pst.setInt(1, uF.parseToInt(getId())); rs = pst.executeQuery();
			 * while (rs.next()) { if(sbEmpId == null) { sbEmpId = new
			 * StringBuilder(); sbEmpId.append(rs.getString("reviewee_id")); }
			 * else { sbEmpId.append(","+rs.getString("reviewee_id")); } }
			 * rs.close(); pst.close(); }
			 */

			sbQuery = new StringBuilder();
			sbQuery.append("select sattlement_comment,if_approved,user_id, emp_fname, emp_mname,emp_lname,activity_ids,afs.emp_id,appraisal_id,_date,appraisal_freq_id "
					+ "from appraisal_final_sattlement afs,employee_personal_details epd  where afs.user_id = epd.emp_per_id and appraisal_id=? and appraisal_freq_id=? ");
			if (sbEmpId != null) {
				sbQuery.append(" and emp_id in (" + sbEmpId.toString() + ")");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			// System.out.println("getRemarks pst==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				remark = rs.getString("sattlement_comment");
				if (remark != null) {
					remark = remark.replace("\n", "<br/>");
				}
				flag = uF.parseToBoolean(rs.getString("if_approved"));
				String strEmpMName = "";

				if (flagMiddleName) {
					if (rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length() > 0) {
						strEmpMName = " " + rs.getString("emp_mname");
					}
				}
				strApprovedBy = rs.getString("emp_fname") + strEmpMName + " " + rs.getString("emp_lname");

				hmRemark.put(rs.getString("appraisal_id") + "_" + rs.getString("emp_id"),
						strApprovedBy + " on " + uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmRemark", hmRemark);

			Map<String, String> hmManagerRecommendation = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select recommendation_comment,user_id,emp_fname,emp_mname,emp_lname,emp_id,review_id,entry_date,review_freq_id "
					+ "from review_final_recommendation afs, employee_personal_details epd where afs.user_id = epd.emp_per_id and review_id=? and review_freq_id=? ");
			if (sbEmpId != null) {
				sbQuery.append(" and emp_id in (" + sbEmpId.toString() + ")");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			// System.out.println("getRemarks pst==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				remark = rs.getString("recommendation_comment");
				if (remark != null) {
					remark = remark.replace("\n", "<br/>");
				}
				flag = true;
				String strEmpMName = "";

				if (flagMiddleName) {
					if (rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length() > 0) {
						strEmpMName = " " + rs.getString("emp_mname");
					}
				}
				strApprovedBy = rs.getString("emp_fname") + strEmpMName + " " + rs.getString("emp_lname");

				hmManagerRecommendation.put(rs.getString("review_id") + "_" + rs.getString("emp_id"),
						strApprovedBy + " on " + uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmManagerRecommendation", hmManagerRecommendation);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void getAppraisalStatusReport(UtilityFunctions uF) {
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> orientationMemberMp = getOrientationMember();
		con = db.makeConnection(con);
		// String self_ids = null;
		String oriented_type = null;
		try {

			// ===start parvez date: 10-12-2021===
			boolean isSelfRating = CF.getFeatureManagementStatus(request, uF, F_DISABLE_SELF_APPRAISAL_RATING_DURING_FINAL_RATING_CALCULATION);
			// ===end parvez date: 10-12-2021===

			// ===start parvez date: 21-03-2022===
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			if (hmFeatureStatus == null)
				hmFeatureStatus = new HashMap<String, String>();

			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
			List<String> alFeatureUserTypeId = hmFeatureUserTypeId.get(F_ENABLE_BALANCE_SCORECARD_CALCULATION_USERTYPE);

			boolean isUserTypeRating = uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_BALANCE_SCORECARD_CALCULATION_USERTYPE));
			Map<String, String> hmRevieweeId = CF.getAppraisalRevieweesId(con, uF);
			if (hmRevieweeId == null)
				hmRevieweeId = new HashMap<String, String>();
			// ===end parvez date: 21-03-2022===

			Map<String, String> hmAttributeThreshhold = new HashMap<String, String>();
			pst = con.prepareStatement("select attribute_id,threshhold from appraisal_attribute_level");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmAttributeThreshhold.put(rs.getString("attribute_id"), rs.getString("threshhold"));
			}
			rs.close();
			pst.close();

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,supervisor_emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id "
					+ " and is_alive= true and emp_per_id >0 order by supervisor_emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			// System.out.println("PST ===>> " + pst);
			Map<String, List<String>> hmHireracyLevelEmpIds = new LinkedHashMap<String, List<String>>();
			// List<String> alHireracyLevels = new ArrayList<String>();
			while (rs.next()) {
				List<String> alInner = hmHireracyLevelEmpIds.get(rs.getString("supervisor_emp_id"));
				if (alInner == null)
					alInner = new ArrayList<String>();
				if (uF.parseToInt(rs.getString("supervisor_emp_id")) == uF.parseToInt(rs.getString("emp_id"))) {
					continue;
				}
				alInner.add(rs.getString("emp_id"));
				hmHireracyLevelEmpIds.put(rs.getString("supervisor_emp_id"), alInner);
			}
			rs.close();
			pst.close();

			StringBuilder sbClildEmpIds = null;
			List<String> empIDList = new ArrayList<String>();
			sbClildEmpIds = getChildEmpIds(hmHireracyLevelEmpIds, strSessionEmpId, empIDList, sbClildEmpIds);

			StringBuilder sbEmpId = null;
			if (strSessionUserType != null && strSessionUserType.equals(MANAGER)) {
				sbQuery = new StringBuilder();
				sbQuery.append("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id  and appraisal_id=? "
						+ "and (supervisor_ids like '%,"
						+ strSessionEmpId
						+ ",%' or grand_supervisor_ids like '%,"
						+ strSessionEmpId
						+ ",%' or hod_ids like '%," + strSessionEmpId + ",%' ");
				if (sbClildEmpIds != null && !sbClildEmpIds.toString().equals("")) {
					sbQuery.append(" or epd.emp_per_id in (" + sbClildEmpIds.toString() + ") ");
				}
				sbQuery.append(") order by emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
				// pst = con.prepareStatement("select reviewee_id from
				// appraisal_reviewee_details ard, employee_personal_details epd
				// where epd.emp_per_id=ard.reviewee_id and supervisor_ids like
				// '%,"+strSessionEmpId+",%' and appraisal_id=? order by
				// emp_fname");
				pst.setInt(1, uF.parseToInt(getId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					if (sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rs.getString("reviewee_id"));
					} else {
						sbEmpId.append("," + rs.getString("reviewee_id"));
					}
				}
				rs.close();
				pst.close();
			}

			// sbEmpId = new StringBuilder("719");
			/*
			 * StringBuilder sbEmpId = null; if(strSessionUserType != null &&
			 * strSessionUserType.equals(MANAGER)) { pst = con.
			 * prepareStatement(
			 * "select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id and supervisor_ids like '%,"
			 * +strSessionEmpId+",%' and appraisal_id=? order by emp_fname");
			 * pst.setInt(1, uF.parseToInt(getId())); rs = pst.executeQuery();
			 * while (rs.next()) { if(sbEmpId == null) { sbEmpId = new
			 * StringBuilder(); sbEmpId.append(rs.getString("reviewee_id")); }
			 * else { sbEmpId.append(","+rs.getString("reviewee_id")); } }
			 * rs.close(); pst.close(); }
			 */

			double dblTotalMarks1 = 0;
			double dblTotalWeightage1 = 0;
			double dblTotalAggregate1 = 0;
			Map<String, String> hmScoreAggregateMap = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(marks) as marks, sum(weightage) as weightage, aqw.appraisal_attribute, aqw.emp_id from appraisal_question_answer aqw where aqw.appraisal_id=? and aqw.appraisal_freq_id=? and aqw.is_submit=true");
			if (sbEmpId != null) {
				sbQuery.append(" and emp_id in (" + sbEmpId.toString() + ")");
			}
			sbQuery.append(" group by aqw.appraisal_attribute,aqw.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(id));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			List<String> attribIdList = new ArrayList<String>();
			while (rs.next()) {
				dblTotalMarks1 = uF.parseToDouble(rs.getString("marks"));
				dblTotalWeightage1 = uF.parseToDouble(rs.getString("weightage"));
				dblTotalAggregate1 = uF.parseToDouble(uF.formatIntoTwoDecimal(((dblTotalMarks1 / dblTotalWeightage1) * 100)));

				if (!attribIdList.contains(rs.getString("appraisal_attribute"))) {
					attribIdList.add(rs.getString("appraisal_attribute"));
				}
				hmScoreAggregateMap.put(rs.getString("emp_id") + "_" + rs.getString("appraisal_attribute"), uF.showData("" + dblTotalAggregate1, "0"));
			}
			rs.close();
			pst.close();

			request.setAttribute("hmAttributeThreshhold", hmAttributeThreshhold);
			request.setAttribute("attribIdList", attribIdList);
			request.setAttribute("hmScoreAggregateMap", hmScoreAggregateMap);
			Map<String, String> hmFrequency = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();

			Map<String, String> hmRevieweewiseMemberCount = (Map<String, String>) request.getAttribute("hmRevieweewiseMemberCount");
			if (hmRevieweewiseMemberCount == null)
				hmRevieweewiseMemberCount = new HashMap<String, String>();

			Map<String, String> hmDesignation = CF.getDesigMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			Map<String, String> hmLevelMap = getLevelMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			Map<String, String> orientationMp = getOrientationValue(con);
			/*
			 * pst = con. prepareStatement(
			 * "select * from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id "
			 * +
			 * " and (is_delete is null or is_delete = false) and appraisal_details_id =?"
			 * );
			 */
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			// System.out.println("==>pstAppraisaldetails"+pst);
			Map<String, String> appraisalMp = new HashMap<String, String>();
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);

			// List<String> alUserTypeForFeedback = new ArrayList<String>();
			String strUserTypesForFeedback = null;
			while (rs.next()) {
				List<String> memberList = new ArrayList<String>();
				if (rs.getString("usertype_member") != null && !rs.getString("usertype_member").equals("")) {
					memberList = Arrays.asList(rs.getString("usertype_member").split(","));
				}
				
				String memberName = "";
				for (int i = 0; memberList != null && !memberList.isEmpty() && i < memberList.size(); i++) {
					if (i == 0) {
						memberName += orientationMemberMp.get(memberList.get(i));
					} else {
						memberName += ", " + orientationMemberMp.get(memberList.get(i));
					}
				}
				if (memberName == null || memberName.equals("null")) {
					memberName = "Anyone";
				}
				appraisalMp.put("ID", rs.getString("appraisal_details_id"));
				appraisalMp.put("APPRAISAL", rs.getString("appraisal_name"));
				appraisalMp.put("APPRAISALTYPE", uF.showData(rs.getString("appraisal_type"), ""));
				appraisalMp.put("DESCRIPTION", uF.showData(rs.getString("appraisal_description"), ""));
				appraisalMp.put("INSTRUCTION", uF.showData(rs.getString("appraisal_instruction"), ""));
				appraisalMp.put("ORIENT", orientationMp.get(rs.getString("oriented_type")) + " (" + memberName + ")");
				appraisalMp.put("EMPLOYEE", uF.showData(getAppendData(rs.getString("self_ids"), hmEmpName), ""));
				appraisalMp.put("LEVEL", uF.showData(hmLevelMap.get(rs.getString("level_id")), ""));
				appraisalMp.put("DESIG", hmDesignation.get(rs.getString("desig_id")));
				appraisalMp.put("GRADE", hmGradeMap.get(rs.getString("grade_id")));
				appraisalMp.put("WLOCATION", rs.getString("wlocation_id"));
				appraisalMp.put("PEER", rs.getString("peer_ids"));
				appraisalMp.put("SELFID", rs.getString("self_ids"));
				appraisalMp.put("APPRAISEE", uF.showData(getAppendData(rs.getString("self_ids"), hmEmpName), ""));
				appraisalMp.put("SUPERVISORID", rs.getString("supervisor_id"));
				appraisalMp.put("FREQUENCY", uF.showData(hmFrequency.get(rs.getString("frequency")), ""));
				appraisalMp.put("FROM", uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalMp.put("TO", uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalMp.put("IS_CLOSE", rs.getString("is_close"));
				appraisalMp.put("CEO", rs.getString("ceo_ids"));
				appraisalMp.put("HOD", rs.getString("hod_ids"));

				StringBuilder sbAppraisers = new StringBuilder();
				if (rs.getString("usertype_member") != null && rs.getString("usertype_member").length() > 0) {
					List<String> alAppraiserMember = Arrays.asList(rs.getString("usertype_member").split(","));
					for (int i = 0; alAppraiserMember != null && i < alAppraiserMember.size(); i++) {
						if (uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
							sbAppraisers.append("Managers: " + uF.showData(getAppendData(rs.getString("supervisor_id"), hmEmpName), "N/A") + "</br>");
						} else if (uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(HOD))) {
							sbAppraisers.append("HODs: " + uF.showData(getAppendData(rs.getString("hod_ids"), hmEmpName), "N/A") + "</br>");
						} else if (uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(CEO))) {
							sbAppraisers.append("CEOs: " + uF.showData(getAppendData(rs.getString("ceo_ids"), hmEmpName), "N/A") + "</br>");
						} else if (uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(HRMANAGER))) {
							sbAppraisers.append("HRs: " + uF.showData(getAppendData(rs.getString("hr_ids"), hmEmpName), "N/A") + "</br>");
						} else if (uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(ADMIN))) {
							sbAppraisers.append("Global HRs: " + uF.showData(getAppendData(rs.getString("hr_ids"), hmEmpName), "N/A") + "</br>");
						} else if (uF.parseToInt(alAppraiserMember.get(i)) == 4) {
							sbAppraisers.append("Peers: " + uF.showData(getAppendData(rs.getString("peer_ids"), hmEmpName), "N/A") + "</br>");
						} else if (uF.parseToInt(alAppraiserMember.get(i)) == 10) {
							sbAppraisers.append("Anyone: " + uF.showData(getAppendData(rs.getString("other_ids"), hmEmpName), "N/A") + "</br>");
						}
					}
				}
				appraisalMp.put("APPRAISER", uF.showData(sbAppraisers.toString(), ""));
				appraisalMp.put("REVIEWER", uF.showData(getAppendData(rs.getString("reviewer_id"), hmEmpName), ""));
				if (rs.getString("user_types_for_feedback") != null) {
					// alUserTypeForFeedback =
					// Arrays.asList(rs.getString("user_types_for_feedback").split(","));
					strUserTypesForFeedback = rs.getString("user_types_for_feedback").substring(1, (rs.getString("user_types_for_feedback").length() - 1));
				}
				request.setAttribute("memberList", memberList);
			}
			rs.close();
			pst.close();

			// request.setAttribute("memberCount", memberCount);
			pst = con
					.prepareStatement("select * from appraisal_details_frequency  where (is_delete =false or is_delete is null) and appraisal_id = ? and appraisal_freq_id =?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			// System.out.println("pst ===>> " + pst);
			while (rs.next()) {
				appraisalMp.put("APP_FREQ_ID", rs.getString("appraisal_freq_id"));
				appraisalMp.put("APP_FREQ_CLOSE", rs.getString("is_appraisal_close"));
				appraisalMp.put("APP_FREQ_PUBLISH", rs.getString("is_appraisal_publish"));
				appraisalMp.put("APP_FREQ_EXPIRE", rs.getString("freq_publish_expire_status"));
				appraisalMp.put("APP_FREQ_CLOSE_REASON", rs.getString("close_reason"));
				appraisalMp.put("APP_FREQ_FROM", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalMp.put("APP_FREQ_TO", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, CF.getStrReportDateFormat()));

			}
			rs.close();
			pst.close();
			getEmpWlocation(appraisalMp.get("SELFID"));
			String empids = appraisalMp.get("SELFID") != null && !appraisalMp.get("SELFID").equals("") ? appraisalMp.get("SELFID").substring(1,
					appraisalMp.get("SELFID").length() - 1) : "";
			Map<String, String> empImageMap = new HashMap<String, String>();
			if (empids != null && !empids.equals("") && !empids.equalsIgnoreCase("null")) {
				sbQuery = new StringBuilder();
				sbQuery.append("select emp_image,emp_per_id from employee_personal_details where emp_per_id>0 ");
				if (sbEmpId != null) {
					sbQuery.append(" and emp_per_id in (" + sbEmpId.toString() + ")");
				} else {
					sbQuery.append(" and emp_per_id in (" + empids + ")");
				}
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				while (rs.next()) {
					empImageMap.put(rs.getString("emp_per_id"), rs.getString("emp_image"));
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("empImageMap", empImageMap);
			request.setAttribute("appraisalMp", appraisalMp);

			alInnerExport.add(new DataStyle("Balance Score Card_" + appraisalMp.get("APPRAISAL"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",
					BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle(appraisalMp.get("APPRAISAL"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);

			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Review Type:", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(appraisalMp.get("APPRAISALTYPE"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);

			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Description:", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(appraisalMp.get("DESCRIPTION"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);

			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Frequency:", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(appraisalMp.get("FREQUENCY"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);

			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Effective Date: ", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(appraisalMp.get("APP_FREQ_FROM"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);

			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Due Date:", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(appraisalMp.get("APP_FREQ_TO"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);

			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Orientation", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(appraisalMp.get("ORIENT"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);

			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Score Cards", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);

			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Employee Code", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employee Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Department", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Level", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Designation", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Location", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));

			/*
			 * pst = con. prepareStatement(
			 * "select self_ids,appraisal_details_id,oriented_type from appraisal_details where appraisal_details_id=?"
			 * ); pst.setInt(1, uF.parseToInt(id)); rs = pst.executeQuery(); //
			 * System.out.println("pst =========>> " + pst); while (rs.next()) {
			 * self_ids = rs.getString("self_ids"); // appraisal_details_id =
			 * rs.getInt(2); oriented_type = rs.getString(3);
			 * 
			 * } rs.close(); pst.close();
			 */

			List<String> empList = new ArrayList<String>();
			// Start Dattatray Date : 02-July-21
			List<Integer> peerIdsList = new ArrayList<Integer>();
			List<Integer> supervisorIdsList = new ArrayList<Integer>();
			List<Integer> grandSupervisorIdsList = new ArrayList<Integer>();
			List<String> revieweeIdsList = new ArrayList<String>();
			List<Integer> subordinateIdsList = new ArrayList<Integer>();
			List<Integer> otherPeerIdsList = new ArrayList<Integer>();
			List<Integer> hodIdsList = new ArrayList<Integer>();
			List<Integer> ceoIdsList = new ArrayList<Integer>();
			List<Integer> hrIdsList = new ArrayList<Integer>();
			List<Integer> otherIdsList = new ArrayList<Integer>();
			// End Dattatray Date : 02-July-21
			Map<String, String> hmUserTypeValue = new HashMap<String, String>();
			if (getType() != null && getType().equals("KRATARGET")) {
				// empList.add(strSessionEmpId);
				if (sbEmpId == null) {
					sbEmpId = new StringBuilder(strSessionEmpId);
				}
			} /*
			 * else if (sbEmpId != null) { empList =
			 * Arrays.asList(sbEmpId.toString().split(",")); } else {
			 */

			sbQuery = new StringBuilder();
			sbQuery.append("select reviewee_id,peer_ids,reviewee_id,supervisor_ids,grand_supervisor_ids,subordinate_ids,other_peer_ids,hod_ids,ceo_ids,"
					+ "hr_ids,other_ids from appraisal_reviewee_details ard,employee_personal_details epd where epd.emp_per_id=ard.reviewee_id and is_alive= true and appraisal_id=? ");
			// if (sbEmpId != null) {
			// sbQuery.append(" and reviewee_id in (" + sbEmpId.toString() +
			// ")");
			// }
			// sbQuery.append(" order by emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			// System.out.println("pst 1 count =========>> " + pst);
			// Start Dattatray Date : 02-July-21
			while (rs.next()) {

				if (rs.getString("peer_ids") != null && !rs.getString("peer_ids").contains("''") && rs.getString("peer_ids").length() > 0) {
					String strPeersIds = rs.getString("peer_ids").substring(1);
					peerIdsList.add(Arrays.asList(strPeersIds.split(",")).size());
				}

				if (rs.getString("reviewee_id") != null && !rs.getString("reviewee_id").contains("''") && rs.getString("reviewee_id").length() > 0) {
					revieweeIdsList = Arrays.asList(rs.getString("reviewee_id"));
				}

				if (rs.getString("supervisor_ids") != null && !rs.getString("supervisor_ids").contains("''") && rs.getString("supervisor_ids").length() > 0) {
					String strSupervisorIds = rs.getString("supervisor_ids").substring(1);
					supervisorIdsList.add(Arrays.asList(strSupervisorIds.split(",")).size());
				}

				if (rs.getString("grand_supervisor_ids") != null && !rs.getString("grand_supervisor_ids").contains("''")
						&& rs.getString("grand_supervisor_ids").length() > 0) {
					String strGrandSupervisorIds = rs.getString("grand_supervisor_ids").substring(1);
					grandSupervisorIdsList.add(Arrays.asList(strGrandSupervisorIds.split(",")).size());
				}

				if (rs.getString("subordinate_ids") != null && !rs.getString("subordinate_ids").contains("''") && rs.getString("subordinate_ids").length() > 0) {
					String strSubordinateIds = rs.getString("subordinate_ids").substring(1);
					subordinateIdsList.add(Arrays.asList(strSubordinateIds.split(",")).size());
				}

				if (rs.getString("other_peer_ids") != null && !rs.getString("other_peer_ids").contains("''") && rs.getString("other_peer_ids").length() > 0) {
					String strOtherIds = rs.getString("other_peer_ids").substring(1);
					otherPeerIdsList.add(Arrays.asList(strOtherIds.split(",")).size());
				}

				if (rs.getString("hod_ids") != null && !rs.getString("hod_ids").contains("''") && rs.getString("hod_ids").length() > 0) {
					String strHODIds = rs.getString("hod_ids").substring(1);
					hodIdsList.add(Arrays.asList(strHODIds.split(",")).size());
				}

				if (rs.getString("ceo_ids") != null && !rs.getString("ceo_ids").contains("''") && rs.getString("ceo_ids").length() > 0) {
					String strCEOIds = rs.getString("ceo_ids").substring(1);
					ceoIdsList.add(Arrays.asList(strCEOIds.split(",")).size());
				}

				if (rs.getString("hr_ids") != null && !rs.getString("hr_ids").contains("''") && rs.getString("hr_ids").length() > 0) {
					String strHRIds = rs.getString("hr_ids").substring(1);
					hrIdsList.add(Arrays.asList(strHRIds.split(",")).size());
				}

				if (rs.getString("other_ids") != null && !rs.getString("other_ids").contains("''") && rs.getString("other_ids").length() > 0) {
					String strOtherIds = rs.getString("other_ids").substring(1);
					otherIdsList.add(Arrays.asList(strOtherIds.split(",")).size());
				}
				empList.add(rs.getString("reviewee_id"));
			}
			rs.close();
			pst.close();

			int peerMaxValue = peerIdsList.size() > 0 ? Collections.max(peerIdsList) : 0;
			int grandSupervisorMaxValue = grandSupervisorIdsList.size() > 0 ? Collections.max(grandSupervisorIdsList) : 0;
			int subordinateMaxValue = subordinateIdsList.size() > 0 ? Collections.max(subordinateIdsList) : 0;
			int supervisorMaxValue = supervisorIdsList.size() > 0 ? Collections.max(supervisorIdsList) : 0;
			int otherPeerMaxValue = otherPeerIdsList.size() > 0 ? Collections.max(otherPeerIdsList) : 0;
			int ceoMaxValue = ceoIdsList.size() > 0 ? Collections.max(ceoIdsList) : 0;
			int hrMaxValue = hrIdsList.size() > 0 ? Collections.max(hrIdsList) : 0;
			int hodMaxValue = hodIdsList.size() > 0 ? Collections.max(hodIdsList) : 0;
			int otherMaxValue = otherIdsList.size() > 0 ? Collections.max(otherIdsList) : 0;

			hmUserTypeValue.put("2", supervisorMaxValue + "");
			hmUserTypeValue.put("3", revieweeIdsList.size() + "");
			hmUserTypeValue.put("4", peerMaxValue + "");
			hmUserTypeValue.put("5", ceoMaxValue + "");
			hmUserTypeValue.put("6", subordinateMaxValue + "");
			hmUserTypeValue.put("7", hrMaxValue + "");
			hmUserTypeValue.put("8", grandSupervisorMaxValue + "");
			hmUserTypeValue.put("10", otherMaxValue + "");
			hmUserTypeValue.put("13", hodMaxValue + "");
			hmUserTypeValue.put("14", otherPeerMaxValue + "");
			// End Dattatray Date : 02-July-21
			// System.out.println("hmUserUserTypeValue ==> "+hmUserTypeValue);

			// System.out.println("Peer Max value : " + peerMaxValue +
			// " Peer list : " + peerIdsList);
			// System.out.println("Self Max value : " + revieweeIdsList.size() +
			// " Grand Supervisor list : " + revieweeIdsList);
			// System.out.println("Grand Supervisor Max value : " +
			// grandSupervisorMaxValue + " Grand Supervisor list : " +
			// grandSupervisorIdsList);
			// System.out.println("subordinate Max value : " +
			// subordinateMaxValue + " subordinate list : " +
			// subordinateIdsList);
			// System.out.println("supervisor Max value : " + supervisorMaxValue
			// + " supervisor list : " + supervisorIdsList);
			// System.out.println("otherPeer Max value : " + otherPeerMaxValue +
			// " otherPeer list : " + otherPeerIdsList);
			// System.out.println("ceo Max value : " + ceoMaxValue +
			// " ceo list : " + ceoIdsList);
			// System.out.println("hr Max value : " + hrMaxValue + " hr list : "
			// + hrIdsList);
			// System.out.println("other Max value : " + otherMaxValue +
			// " other list : " + otherIdsList);
			//
			// System.out.println("empList : " + empList);
			// }

			// System.out.println("self_ids =========>> " + self_ids);
			/*
			 * self_ids=self_ids!=null && !self_ids.equals("") ?
			 * self_ids.substring(1, self_ids.length()-1) :null; List<String>
			 * empList = new ArrayList<String>(); if(getType() != null &&
			 * getType().equals("KRATARGET")) { empList.add(strSessionEmpId); }
			 * else if(sbEmpId!=null) { empList =
			 * Arrays.asList(sbEmpId.toString().split(",")); } else {
			 * if(self_ids != null) { empList =
			 * Arrays.asList(self_ids.split(",")); } }
			 */
			// System.out.println("empList report ===>> " + empList);
			// Map<String, String> hmUserTypeID = new HashMap<String, String>();
			// pst = con
			// .prepareStatement("select user_type_id,user_type from
			// user_type");
			// rs = pst.executeQuery();
			// while (rs.next()) {
			// hmUserTypeID.put(rs.getString(2), rs.getString(1));
			// }

			// pst = con.prepareStatement("select *,(marks*100/weightage) as
			// average from(select sum(marks) as marks ,sum(weightage) as
			// weightage," +
			// "user_type_id,emp_id from appraisal_question_answer where
			// appraisal_id=? and appraisal_freq_id=? and weightage>0 " +
			// "and reviewer_or_appraiser = 0 group by user_type_id,emp_id)as a
			// order by emp_id ");

			sbQuery = new StringBuilder();
			sbQuery.append("select *,(marks*100/weightage) as average, (reviewer_marks*100/weightage) as reviewer_average from (select sum(marks) as marks, "
					+ "sum(weightage) as weightage, sum(reviewer_marks) as reviewer_marks,user_type_id,emp_id,user_id from appraisal_question_answer where appraisal_id=? "
					+ "and appraisal_freq_id=? and weightage>0 and reviewer_or_appraiser=0 and is_submit=true ");
			if (sbEmpId != null) {
				sbQuery.append(" and emp_id in (" + sbEmpId.toString() + ") ");
			}
			sbQuery.append(" group by user_type_id,emp_id,user_id) as a order by emp_id,user_type_id,user_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			// System.out.println("pst1 ===>> " + pst);
			rs = pst.executeQuery();
			// System.out.println("pst ========> "+pst);
			Map<String, Map<String, Map<String, String>>> outerMp2 = new HashMap<String, Map<String, Map<String, String>>>();
			Map<String, String> hmEmpBalancedScore1 = new HashMap<String, String>();
			String strEmpIdNew1 = null;
			String strEmpIdOld1 = null;
			double dblTotalMarks11 = 0;
			double dblTotalWeightage11 = 0;
			Map<String, String> hmEmpTotMarksWeightage1 = new HashMap<String, String>();
			while (rs.next()) {
				strEmpIdNew1 = rs.getString("emp_id");
				if (strEmpIdNew1 != null && !strEmpIdNew1.equalsIgnoreCase(strEmpIdOld1)) {
					dblTotalMarks11 = 0;
					dblTotalWeightage11 = 0;
				}

				if (rs.getDouble("reviewer_marks") > 0) {
					dblTotalMarks11 += uF.parseToDouble(rs.getString("reviewer_marks"));
				} else {
					dblTotalMarks11 += uF.parseToDouble(rs.getString("marks"));
				}

				dblTotalWeightage11 += uF.parseToDouble(rs.getString("weightage"));
				Map<String, Map<String, String>> value = outerMp2.get(rs.getString("emp_id"));
				if (value == null)
					value = new HashMap<String, Map<String, String>>();

				Map<String, String> hmEmpUserRating = value.get(rs.getString("user_type_id"));
				if (hmEmpUserRating == null)
					hmEmpUserRating = new HashMap<String, String>();
				// TODO : user_id wise mapfetch data
				hmEmpUserRating.put(rs.getString("user_id"), uF.formatIntoTwoDecimal(rs.getDouble("average")));

				value.put(rs.getString("user_type_id"), hmEmpUserRating);
				value.put(rs.getString("user_type_id") + "_REVIEWER", hmEmpUserRating);

				if (dblTotalWeightage11 > 0) {
					hmEmpBalancedScore1.put(rs.getString("emp_id"), uF.formatIntoTwoDecimal((dblTotalMarks11 * 100) / dblTotalWeightage11));
				}
				hmEmpTotMarksWeightage1.put(rs.getString("emp_id") + "_TOT_MARKS", dblTotalMarks11 + "");
				hmEmpTotMarksWeightage1.put(rs.getString("emp_id") + "_TOT_WEIGHTAGE", dblTotalWeightage11 + "");

				outerMp2.put(rs.getString("emp_id"), value);

				strEmpIdOld1 = strEmpIdNew1;
			}
			rs.close();
			pst.close();
			// System.out.println("hmEmpTotMarksWeightage =====>> "
			// +hmEmpTotMarksWeightage);
			// System.out.println("outerMp123455 =====>> " + outerMp2);
			
			Map<String, List<String>> hmAppraisalLeave = new HashMap<String, List<String>>();
			pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innLevelIds = hmAppraisalLeave.get(rs.getString("appraisal_id")+"_"+rs.getString("main_level_id"));
				if(innLevelIds == null) innLevelIds = new ArrayList<String>();
				
				innLevelIds.add(rs.getString("main_level_id"));
				hmAppraisalLeave.put(rs.getString("appraisal_id")+"_"+rs.getString("main_level_id"), innLevelIds);
			}
			rs.close();
			pst.close();
			

			sbQuery = new StringBuilder();
			// sbQuery.append("select *,(marks*100/weightage) as average,(reviewer_marks*100/weightage) as reviewer_average "
			// +
			// " from (select sum(aqa.marks) as marks, sum(aqa.weightage) as weightage,sum(reviewer_marks) as reviewer_marks, "
			// +
			// " aqa.user_type_id,aqa.emp_id,aqa.user_id,amld.main_level_id,section_weightage "
			// +
			// " from appraisal_question_answer aqa ,appraisal_main_level_details amld "
			// +
			// " where aqa.appraisal_id=? and aqa.appraisal_freq_id=? and aqa.weightage>0 and aqa.is_submit=true and aqa.section_id=amld.main_level_id ");
			sbQuery.append("select *,(marks*100/weightage) as average,(reviewer_marks*100/weightage) as reviewer_average "
					+ " from (select sum(aqa.marks) as marks, sum(aqa.weightage) as weightage,sum(reviewer_marks) as reviewer_marks,aqa.appraisal_id, "
					+ " aqa.user_type_id,aqa.emp_id,aqa.user_id,amld.main_level_id,section_weightage,amld.hr,amld.manager,amld.peer,amld.hod,amld.ceo,amld.subordinate,amld.other_peer "
					+ " from appraisal_question_answer aqa ,appraisal_main_level_details amld "
					+ " where aqa.appraisal_id=? and aqa.appraisal_freq_id=? and aqa.weightage>0 and aqa.is_submit=true and aqa.section_id=amld.main_level_id ");
			if (sbEmpId != null) {
				sbQuery.append(" and emp_id in (" + sbEmpId.toString() + ") ");
			}
			sbQuery.append(" group by aqa.user_type_id,aqa.emp_id,aqa.appraisal_id,aqa.user_id,amld.main_level_id) as a order by emp_id,main_level_id,user_type_id,user_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			// System.out.println("pst1 ===>> " + pst);
			rs = pst.executeQuery();
			// System.out.println("ApSt/2101--pst ========> "+pst);
			// Map<String, Map<String, Map<String, String>>> outerMp = new
			// HashMap<String, Map<String, Map<String, String>>>();
			// Map<String, String> hmEmpBalancedScore = new HashMap<String,
			// String>();
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			double dblTotalMarks = 0;
			double dblTotalWeightage = 0;
			Map<String, String> hmEmpTotMarksWeightage = new HashMap<String, String>();
			Map<String, Map<String, Map<String, Map<String, List<String>>>>> outerMp1 = new HashMap<String, Map<String, Map<String, Map<String, List<String>>>>>();
			Map<String, String> hmPriorUserId = new HashMap<String, String>(); // created
																				// by
																				// parvez
																				// date:11-04-2022===
			while (rs.next()) {
				strEmpIdNew = rs.getString("emp_id");
				if (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					dblTotalMarks = 0;
					dblTotalWeightage = 0;
				}

				// ===start parvez date: 17-12-2021===
				/*
				 * if (rs.getDouble("reviewer_marks") > 0) { dblTotalMarks +=
				 * uF.parseToDouble(rs.getString("reviewer_marks"));
				 * 
				 * } else {
				 * 
				 * dblTotalMarks += uF.parseToDouble(rs.getString("marks")); }
				 */

				/*
				 * if (rs.getDouble("reviewer_marks") > 0) {
				 * if(uF.parseToInt(rs.getString("user_type_id")) != 3 ||
				 * !isSelfRating) { dblTotalMarks +=
				 * uF.parseToDouble(rs.getString("reviewer_marks"));
				 * System.out.println
				 * ("ApS/2140--user_type_id="+rs.getString("user_type_id"
				 * )+"--dblTotalMarks="+dblTotalMarks); } } else {
				 * if((uF.parseToInt(rs.getString("user_type_id")) != 3 ||
				 * !isSelfRating) &&
				 * uF.parseToDouble(rs.getString("section_weightage")) != 0) {
				 * dblTotalMarks += uF.parseToDouble(rs.getString("marks"));
				 * System.out.println("ApS/2145--user_type_id="+rs.getString(
				 * "user_type_id")+"--dblTotalMarks="+dblTotalMarks); } //===end
				 * parvez date: 17-12-2021=== }
				 * 
				 * // dblTotalWeightage +=
				 * uF.parseToDouble(rs.getString("weightage"));
				 * if((uF.parseToInt(rs.getString("user_type_id")) != 3 ||
				 * !isSelfRating) &&
				 * uF.parseToDouble(rs.getString("section_weightage")) != 0) {
				 * dblTotalWeightage +=
				 * uF.parseToDouble(rs.getString("weightage")); //
				 * System.out.println
				 * ("ApS/2134--user_type_id="+rs.getString("user_type_id"
				 * )+"--dblTotalWeightage="+dblTotalWeightage); }
				 */

				// ===start parvez date: 21-03-2022===

				List<String> alPriorUserList = new ArrayList<String>();
				if (hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_SUPERVISOR") != null
						&& !hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_SUPERVISOR").equals("")) {
					alPriorUserList.add(rs.getString("manager"));
				}
				if (hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_HOD") != null
						&& !hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_HOD").equals("")) {
					alPriorUserList.add(rs.getString("hod"));
				}
				if (hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_CEO") != null
						&& !hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_CEO").equals("")) {
					alPriorUserList.add(rs.getString("ceo"));
				}
				if (hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_HR") != null
						&& !hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_HR").equals("")) {
					alPriorUserList.add(rs.getString("hr"));
				}
				if (hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_PEER") != null
						&& !hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_PEER").equals("")) {
					alPriorUserList.add(rs.getString("peer"));
				}
				if (hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_OTHER_PEER") != null
						&& !hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_OTHER_PEER").equals("")) {
					alPriorUserList.add(rs.getString("other_peer"));
				}
				if (hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_SUBORDINATE") != null
						&& !hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_SUBORDINATE").equals("")) {
					alPriorUserList.add(rs.getString("subordinate"));
				}

				// System.out.println("ApS/2172--alPriorUserList="+alPriorUserList);
				Collections.sort(alPriorUserList, Collections.reverseOrder());
				// System.out.println("ApS/2174--alPriorUserList="+alPriorUserList);

				int priorUserTypeId = 0;

				// ===start parvez date: 23-03-2022===
				if (alPriorUserList != null && !alPriorUserList.isEmpty()) {
					if (uF.parseToInt(rs.getString("manager")) == uF.parseToInt(alPriorUserList.get(0))) {
						priorUserTypeId = 2;
					} else if (uF.parseToInt(rs.getString("hod")) == uF.parseToInt(alPriorUserList.get(0))) {
						priorUserTypeId = 13;
					} else if (uF.parseToInt(rs.getString("ceo")) == uF.parseToInt(alPriorUserList.get(0))) {
						priorUserTypeId = 5;
					} else if (uF.parseToInt(rs.getString("hr")) == uF.parseToInt(alPriorUserList.get(0))) {
						priorUserTypeId = 7;
					} else if (uF.parseToInt(rs.getString("peer")) == uF.parseToInt(alPriorUserList.get(0))) {
						priorUserTypeId = 4;
					} else if (uF.parseToInt(rs.getString("other_peer")) == uF.parseToInt(alPriorUserList.get(0))) {
						priorUserTypeId = 14;
					} else if (uF.parseToInt(rs.getString("subordinate")) == uF.parseToInt(alPriorUserList.get(0))) {
						priorUserTypeId = 6;
					}
				}
				// ===end parvez date: 23-03-2022===

				hmPriorUserId.put(rs.getString("emp_id"), priorUserTypeId + "");

				if (rs.getDouble("reviewer_marks") > 0) {

					if (uF.parseToInt(rs.getString("user_type_id")) != 3 || !isSelfRating) {
						// if(uF.parseToInt(rs.getString("user_type_id")) != 3
						// || !isSelfRating ||
						// (isUserTypeRating &&
						// alFeatureUserTypeId.contains(rs.getString("user_type_id"))
						// && uF.parseToInt(rs.getString("user_type_id")) ==
						// priorUserTypeId)) {

						if (!isUserTypeRating
								|| (isUserTypeRating && alFeatureUserTypeId.contains(rs.getString("user_type_id")) && uF.parseToInt(rs
										.getString("user_type_id")) == priorUserTypeId)) {
							dblTotalMarks += uF.parseToDouble(rs.getString("reviewer_marks"));
							// System.out.println("ApS/2191--user_type_id="+rs.getString("user_type_id")+"---priorUserTypeId="+priorUserTypeId+"--dblTotalMarks="+dblTotalMarks);
						}

					}
				} else {

					if (uF.parseToInt(rs.getString("user_type_id")) != 3 || !isSelfRating && uF.parseToDouble(rs.getString("section_weightage")) != 0) {
						// if(uF.parseToInt(rs.getString("user_type_id")) != 3
						// || !isSelfRating ||
						// isUserTypeRating &&
						// alFeatureUserTypeId.contains(rs.getString("user_type_id"))
						// && uF.parseToInt(rs.getString("user_type_id")) ==
						// priorUserTypeId &&
						// uF.parseToDouble(rs.getString("section_weightage"))
						// != 0) {

						if (!isUserTypeRating || isUserTypeRating && alFeatureUserTypeId.contains(rs.getString("user_type_id"))
								&& uF.parseToInt(rs.getString("user_type_id")) == priorUserTypeId) {
							dblTotalMarks += uF.parseToDouble(rs.getString("marks"));
							// System.out.println("ApS/2201--user_type_id="+rs.getString("user_type_id")+"---priorUserTypeId="+priorUserTypeId+"--dblTotalMarks="+dblTotalMarks);
						}

					}
				}

				if ((uF.parseToInt(rs.getString("user_type_id")) != 3 || !isSelfRating) && uF.parseToDouble(rs.getString("section_weightage")) != 0) {
					// if((uF.parseToInt(rs.getString("user_type_id")) != 3 ||
					// !isSelfRating) || (isUserTypeRating &&
					// alFeatureUserTypeId.contains(rs.getString("user_type_id")))
					// && uF.parseToInt(rs.getString("user_type_id")) ==
					// priorUserTypeId &&
					// uF.parseToDouble(rs.getString("section_weightage")) != 0)
					// {

					if (!isUserTypeRating
							|| ((isUserTypeRating && alFeatureUserTypeId.contains(rs.getString("user_type_id"))) && uF.parseToInt(rs.getString("user_type_id")) == priorUserTypeId)) {
						dblTotalWeightage += uF.parseToDouble(rs.getString("weightage"));
						// System.out.println("ApS/2208--user_type_id="+rs.getString("user_type_id")+"---priorUserTypeId="+priorUserTypeId+"--dblTotalWeightage="+dblTotalWeightage);
					}

				}

				// ===end parvez date: 21-03-2022===

				// Map<String, Map<String, String>> value =
				// outerMp.get(rs.getString("emp_id"));
				// if (value == null) value = new HashMap<String, Map<String,
				// String>>();

				Map<String, Map<String, Map<String, List<String>>>> value = outerMp1.get(rs.getString("emp_id"));
				if (value == null)
					value = new HashMap<String, Map<String, Map<String, List<String>>>>();

				Map<String, Map<String, List<String>>> hmMainLevel = value.get(rs.getString("user_type_id"));
				if (hmMainLevel == null)
					hmMainLevel = new HashMap<String, Map<String, List<String>>>();

				Map<String, List<String>> hmUserId = hmMainLevel.get(rs.getString("user_id"));
				if (hmUserId == null)
					hmUserId = new HashMap<String, List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("average"));
//				innerList.add(rs.getString("section_weightage"));
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) 
						&&(uF.parseToInt(rs.getString("user_type_id"))==4 || uF.parseToInt(rs.getString("user_type_id"))==14 || uF.parseToInt(rs.getString("user_type_id"))==13) && uF.parseToInt(rs.getString("section_weightage"))<100){
					innerList.add(100+"");
				} else{
					innerList.add(rs.getString("section_weightage"));
				}
				hmUserId.put(rs.getString("main_level_id"), innerList);

				hmMainLevel.put(rs.getString("user_id"), hmUserId);

				value.put(rs.getString("user_type_id"), hmMainLevel);

				// System.out.println("value ===>> " + value);

				// Map<String, String> hmEmpUserRating =
				// value.get(rs.getString("user_type_id"));
				// if(hmEmpUserRating==null) hmEmpUserRating = new
				// HashMap<String, String>();
				// hmEmpUserRating.put(rs.getString("user_id"),
				// uF.formatIntoTwoDecimal(rs.getDouble("average")));
				//
				// value.put(rs.getString("user_type_id"), hmEmpUserRating);
				// value.put(rs.getString("user_type_id") + "_REVIEWER",
				// hmEmpUserRating);

				// if (dblTotalWeightage > 0) {
				// hmEmpBalancedScore.put(rs.getString("emp_id"),
				// uF.formatIntoTwoDecimal((dblTotalMarks * 100) /
				// dblTotalWeightage));
				// }
				hmEmpTotMarksWeightage.put(rs.getString("emp_id") + "_TOT_MARKS", dblTotalMarks + "");
				hmEmpTotMarksWeightage.put(rs.getString("emp_id") + "_TOT_WEIGHTAGE", dblTotalWeightage + "");

				outerMp1.put(rs.getString("emp_id"), value);
				// outerMp.put(rs.getString("emp_id"), value);

				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
			// System.out.println("hmEmpTotMarksWeightage =====>> "
			// +hmEmpTotMarksWeightage);
			// System.out.println("outerMp xls =====>> " + outerMp1);
			
			Map<String, String> hmFinalScore = new HashMap<String, String>();
			if(uF.parseToBoolean(hmFeatureStatus.get(F_HR_GHR_APPROVAL_FOR_FINAL_RATING_AND_COMMENT))){
				sbQuery = new StringBuilder();
				sbQuery.append("select emp_id,reviewer_comment,reviewer_marks from reviewer_feedback_details where appraisal_id=? and appraisal_freq_id=?");
				if (sbEmpId != null) {
					sbQuery.append(" and emp_id in (" + sbEmpId.toString() + ") ");
				}
				
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getId()));
				pst.setInt(2, uF.parseToInt(getAppFreqId()));
//				System.out.println("hmEmpCount pst3==>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					hmFinalScore.put(rs.getString("emp_id"), rs.getString("reviewer_marks"));
				}
				rs.close();
				pst.close();
			}

			pst = con
					.prepareStatement("select reviewee_id,subordinate_weightage,peer_weightage,other_peer_weightage,supervisor_weightage,grand_supervisor_weightage,hod_weightage,ceo_weightage,hr_weightage,other_weightage "
							+ " from appraisal_reviewee_details where appraisal_id = ?");
			pst.setInt(1, uF.parseToInt(getId()));
			// System.out.println("PST === > "+pst.toString());
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmEmpUsertypeW = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				Map<String, String> hmUsertypeW = new HashMap<String, String>();
				if (uF.parseToInt(rs.getString("supervisor_weightage")) > 0) {
					hmUsertypeW.put("2", rs.getString("supervisor_weightage"));
				}
				if (uF.parseToInt(rs.getString("peer_weightage")) > 0) {
					hmUsertypeW.put("4", rs.getString("peer_weightage"));
				}
				if (uF.parseToInt(rs.getString("ceo_weightage")) > 0) {
					hmUsertypeW.put("5", rs.getString("ceo_weightage"));
				}
				if (uF.parseToInt(rs.getString("subordinate_weightage")) > 0) {
					hmUsertypeW.put("6", rs.getString("subordinate_weightage"));
				}
				if (uF.parseToInt(rs.getString("hr_weightage")) > 0) {
					hmUsertypeW.put("7", rs.getString("hr_weightage"));
				}
				if (uF.parseToInt(rs.getString("grand_supervisor_weightage")) > 0) {
					hmUsertypeW.put("8", rs.getString("grand_supervisor_weightage"));
				}
				if (uF.parseToInt(rs.getString("other_weightage")) > 0) {
					hmUsertypeW.put("10", rs.getString("other_weightage"));
				}
				if (uF.parseToInt(rs.getString("hod_weightage")) > 0) {
					hmUsertypeW.put("13", rs.getString("hod_weightage"));
				}
				if (uF.parseToInt(rs.getString("other_peer_weightage")) > 0) {
					hmUsertypeW.put("14", rs.getString("other_peer_weightage"));
				}
				hmEmpUsertypeW.put(rs.getString("reviewee_id"), hmUsertypeW);
			}
			rs.close();
			pst.close();
			// System.out.println("hmKRA90W ===>> " + hmKRA90W);

			Map<String, Map<String, Map<String, String>>> outerMp = new HashMap<String, Map<String, Map<String, String>>>();
			Map<String, String> hmEmpBalancedScore11 = new HashMap<String, String>();
			Iterator<String> itEmp = outerMp1.keySet().iterator();
			while (itEmp.hasNext()) {
				String strEmpId = itEmp.next();
				Map<String, String> hmUsertypeW = hmEmpUsertypeW.get(strEmpId);
				if (hmUsertypeW == null)
					hmUsertypeW = new HashMap<String, String>();

				Map<String, Map<String, String>> value1 = outerMp.get(strEmpId);
				if (value1 == null)
					value1 = new HashMap<String, Map<String, String>>();

				Map<String, Map<String, Map<String, List<String>>>> value = outerMp1.get(strEmpId);
				if (value == null)
					value = new HashMap<String, Map<String, Map<String, List<String>>>>();
				// System.out.println("value111 : "+value);
				double dblAllUsertypeAvg = 0;
				int intUsertypeCnt = 0;
				Iterator<String> itUserTypeId = value.keySet().iterator();
				while (itUserTypeId.hasNext()) {
					String strUsertypeId = itUserTypeId.next();
					// System.out.println("strUsertypeId : "+strUsertypeId);

					Map<String, String> hmUserId1 = value1.get(strUsertypeId);
					if (hmUserId1 == null)
						hmUserId1 = new HashMap<String, String>();

					Map<String, Map<String, List<String>>> hmUserId = value.get(strUsertypeId);
					if (hmUserId == null)
						hmUserId = new HashMap<String, Map<String, List<String>>>();
					// System.out.println("hmUserId : "+hmUserId);
					Iterator<String> itUserId = hmUserId.keySet().iterator();
					double dblAllUserAvg = 0;
					int intUserCnt = 0;
					while (itUserId.hasNext()) {
						String strUserId = itUserId.next();
						// System.out.println("strUserId : "+strUserId);

						Map<String, List<String>> hmMainLevel = hmUserId.get(strUserId);
						if (hmMainLevel == null)
							hmMainLevel = new HashMap<String, List<String>>();
						// System.out.println("hmMainLevel : "+hmMainLevel);

						Iterator<String> itMainLevel = hmMainLevel.keySet().iterator();
						double dblUserAvg = 0;
						while (itMainLevel.hasNext()) {
							String strLevelId = itMainLevel.next();
							List<String> innList = hmMainLevel.get(strLevelId);
							// System.out.println("innList : "+innList);
							String strAvg = innList.get(0);
							String strWtg = innList.get(1);
							double dblLevelAvg = (uF.parseToDouble(strAvg) * uF.parseToDouble(strWtg)) / 100;
							dblUserAvg += dblLevelAvg;
							/*if(uF.parseToInt(strEmpId)==54 && uF.parseToInt(strUsertypeId) == 4){
								System.out.println("ApSt/2478--strUserId="+strUserId+"--strAvg ===>> " +strAvg + " -- strWtg ===>> " + strWtg + "dblUserAvg=="+dblUserAvg);
							}*/
							
						}
						hmUserId1.put(strUserId, uF.formatIntoOneDecimalWithOutComma(dblUserAvg));
						if (uF.parseToInt(strUsertypeId) != 3) {
							// ===start parvez date: 11-04-2022===

							/*
							 * if(hmUsertypeW!=null &&
							 * uF.parseToDouble(hmUsertypeW.get(strUsertypeId))
							 * > 0) { dblAllUserAvg += (dblUserAvg *
							 * uF.parseToDouble(hmUsertypeW.get(strUsertypeId)))
							 * / 100; } else { dblAllUserAvg += dblUserAvg;
							 * intUserCnt++; }
							 */

							if (!isUserTypeRating
									|| ((isUserTypeRating && alFeatureUserTypeId.contains(strUsertypeId)) && uF.parseToInt(strUsertypeId) == uF
											.parseToInt(hmPriorUserId.get(strEmpId)))) {
								if (hmUsertypeW != null && uF.parseToDouble(hmUsertypeW.get(strUsertypeId)) > 0) {
									// System.out.println("hmKRA90W123 : "+hmKRA90W);
									dblAllUserAvg += (dblUserAvg * uF.parseToDouble(hmUsertypeW.get(strUsertypeId))) / 100;
								}
								/*
								 * if(uF.parseToDouble(hmKRA90W.get(strUsertypeId
								 * )) > 0) { dblAllUserAvg += (dblUserAvg *
								 * uF.parseToDouble
								 * (hmKRA90W.get(strUsertypeId))) / 100; }
								 */else {
									dblAllUserAvg += dblUserAvg;
									intUserCnt++;
								}
							}
							// ===end parvez date: 11-04-2022===
						}
						// System.out.println(strUserId + " dblUserAvg =====>> "
						// + dblUserAvg);
					}
					value1.put(strUsertypeId, hmUserId1);
					// System.out.println("value1 xls new =====>> " + value1);
					if (uF.parseToInt(strUsertypeId) != 3) {

						// ===start parvez date: 11-04-2022===
						if (!isUserTypeRating
								|| ((isUserTypeRating && alFeatureUserTypeId.contains(strUsertypeId)) && uF.parseToInt(strUsertypeId) == uF
										.parseToInt(hmPriorUserId.get(strEmpId)))) {
							if (intUserCnt == 0) {
								// System.out.println(strUsertypeId+
								// " --- dblAllUserAvg ===>> " + dblAllUserAvg +
								// "-- intUserCnt ===>> " + intUserCnt);
								dblAllUsertypeAvg += dblAllUserAvg;
							} else {
								// System.out.println(strUsertypeId+
								// " --- dblAllUserAvg ===>> " + dblAllUserAvg +
								// "-- intUserCnt ===>> " + intUserCnt);
								double dblBlancedScore = dblAllUserAvg / intUserCnt;
								// System.out.println(strUsertypeId+
								// " --- dblBlancedScore ===>> " +
								// dblBlancedScore);
								dblAllUsertypeAvg += dblBlancedScore;

								intUsertypeCnt++;
							}
						}
						// ===end parvez date: 11-04-2022===
					}
				}
				// System.out.println("dblAllUsertypeAvg xls new =====>> " +
				// dblAllUsertypeAvg+ " -- intUsertypeCnt ===>> " +
				// intUsertypeCnt);
				
			//===start parvez date: 27-03-2023===	
				/*if (intUsertypeCnt == 0) {
					hmEmpBalancedScore11.put(strEmpId, uF.formatIntoTwoDecimal(dblAllUsertypeAvg));
				} else {
					double dblBlancedScore = dblAllUsertypeAvg / intUsertypeCnt;
					hmEmpBalancedScore11.put(strEmpId, uF.formatIntoTwoDecimal(dblBlancedScore));
				}*/
				
				if(hmFinalScore!=null && uF.parseToInt(hmFinalScore.get(strEmpId))>0){
					hmEmpBalancedScore11.put(strEmpId, uF.formatIntoTwoDecimal(uF.parseToDouble(hmFinalScore.get(strEmpId))));
				} else{
					if (intUsertypeCnt == 0) {
						hmEmpBalancedScore11.put(strEmpId, uF.formatIntoTwoDecimal(dblAllUsertypeAvg));
					} else {
						double dblBlancedScore = dblAllUsertypeAvg / intUsertypeCnt;
						hmEmpBalancedScore11.put(strEmpId, uF.formatIntoTwoDecimal(dblBlancedScore));
					}
				}
			//===end parvez date: 27-03-2023===	
				
				// System.out.println("dblBlancedScore xls new =====>> " +
				// hmEmpBalancedScore11);
				outerMp.put(strEmpId, value1);
			}
			// System.out.println("outerMp xls new =====>> " + outerMp);

			sbQuery = new StringBuilder();

	// ===start parvez date: 02-03-2023===
			/*
			 * sbQuery.append(
			 * "select *,(marks*100/weightage) as average from (select sum(marks) as marks ,sum(weightage) as weightage,user_type_id,emp_id "
			 * +
			 * "from appraisal_question_answer where appraisal_id=? and appraisal_freq_id=? and weightage>0 and reviewer_or_appraiser=1 and is_submit=true"
			 * );
			 */
			/*sbQuery.append("select *,(marks*100/weightage) as average from (select sum(aqa.marks) as marks ,sum(aqa.weightage) as weightage,aqa.user_type_id,aqa.emp_id, section_weightage "
					+ "from appraisal_question_answer aqa,appraisal_main_level_details amld where aqa.appraisal_id=? and aqa.appraisal_freq_id=? and aqa.weightage>0 and "
					+ "aqa.reviewer_or_appraiser=1 and aqa.is_submit=true and aqa.section_id=amld.main_level_id");*/
			sbQuery.append("select *,(marks*100/weightage) as average from (select sum(aqa.marks) as marks ,sum(aqa.weightage) as weightage,aqa.user_type_id,aqa.emp_id, " +
					" section_weightage, score_calculation_basis from appraisal_question_answer aqa,appraisal_main_level_details amld, appraisal_question_details aqd " +
					" where aqa.appraisal_id=? and aqa.appraisal_freq_id=? and aqa.weightage>0 and aqa.reviewer_or_appraiser=1 and aqa.is_submit=true " +
					" and aqa.section_id=amld.main_level_id and aqa.appraisal_question_details_id=aqd.appraisal_question_details_id ");
			
			if (sbEmpId != null) {
				sbQuery.append(" and emp_id in (" + sbEmpId.toString() + ") ");
			}
			// sbQuery.append(" group by user_type_id,emp_id) as a order by emp_id");
//			sbQuery.append(" group by user_type_id,emp_id,amld.main_level_id) as a order by emp_id");
			sbQuery.append(" group by user_type_id,emp_id,amld.main_level_id,score_calculation_basis) as a order by emp_id");
	// ===end parvez date: 02-03-2023===	
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			// pst.setInt(3, uF.parseToInt(getEmpID()));
			// System.out.println("pst2==>"+pst);
			rs = pst.executeQuery();
//			 System.out.println("ApS/2335--pst ========> "+pst);
			Map<String, Map<String, String>> reviewerOutMp = new HashMap<String, Map<String, String>>();
			String strEmpIdNewReviewer = null;
			String strEmpIdOldReviewer = null;
			double dblTotalMarksReviewer = 0;
			double dblTotalWeightageReviewer = 0;
			double dblUsertypeAvg = 0; // ===created by parvez date:
										// 16-12-2021===
			while (rs.next()) {
				strEmpIdNewReviewer = rs.getString("emp_id");
				if (strEmpIdNewReviewer != null && !strEmpIdNewReviewer.equalsIgnoreCase(strEmpIdOldReviewer)) {
					dblTotalMarksReviewer = uF.parseToDouble(hmEmpTotMarksWeightage.get(rs.getString("emp_id") + "_TOT_MARKS"));
					dblTotalWeightageReviewer = uF.parseToDouble(hmEmpTotMarksWeightage.get(rs.getString("emp_id") + "_TOT_WEIGHTAGE"));
					// System.out.println("ApS/2354--dblTotalMarksReviewer="+dblTotalMarksReviewer+"---user_type_id="+rs.getString("user_type_id"));
					// System.out.println("ApS/2355--dblTotalWeightageReviewer="+dblTotalWeightageReviewer+"---user_type_id="+rs.getString("user_type_id"));
				} else {
					dblTotalMarksReviewer += uF.parseToDouble(rs.getString("marks"));
					dblTotalWeightageReviewer += uF.parseToDouble(rs.getString("weightage"));
					// System.out.println("ApSt/2358--dblTotalMarksReviewer="+dblTotalMarksReviewer+"--dblTotalWeightageReviewer="+dblTotalWeightageReviewer);
				}
				// dblTotalMarksReviewer +=
				// uF.parseToDouble(rs.getString("marks"));
				// System.out.println("dblTotalMarks"+dblTotalMarks);
				// dblTotalWeightageReviewer +=
				// uF.parseToDouble(rs.getString("weightage"));
				Map<String, String> value = reviewerOutMp.get(rs.getString("emp_id"));
				if (value == null)
					value = new HashMap<String, String>();

				// value.put("REVIEWER",
				// uF.formatIntoTwoDecimal(rs.getDouble("average")));
				// value.put("REVIEWER_USERTYPE", rs.getString("user_type_id"));
				// System.out.println("ApS/2325--user_type_id="+rs.getString("user_type_id"));
				// System.out.println("ApS/2328--emp_id="+rs.getString("emp_id")+"--dblTotalWeightageReviewer="+dblTotalWeightageReviewer+"---dblTotalMarksReviewer="+dblTotalMarksReviewer);

				// ===start parvez date: 16-12-2021====
				double dblLevelAvg = (rs.getDouble("average") * uF.parseToDouble(rs.getString("section_weightage"))) / 100;
				dblUsertypeAvg += dblLevelAvg;
				value.put("REVIEWER", uF.formatIntoTwoDecimal(dblUsertypeAvg));
				value.put("REVIEWER_USERTYPE", rs.getString("user_type_id"));

				// ===end parvez date: 16-12-2021===
				// System.out.println("ApSt/2372--dblUsertypeAvg="+dblUsertypeAvg);

				if (dblTotalWeightageReviewer > 0) {
					// System.out.println("ApSt/2384---dblTotalMarksReviewer="+dblTotalMarksReviewer+"---dblTotalWeightageReviewer="+dblTotalWeightageReviewer);
					value.put("AGGREGATE", uF.formatIntoTwoDecimal((dblTotalMarksReviewer * 100) / dblTotalWeightageReviewer));
				}
			//===start parvez date: 02-03-2023===	
				value.put("ACTUAL_CAL_BASIS", rs.getString("score_calculation_basis"));
			//===end parvez date: 02-03-2023===	
				reviewerOutMp.put(rs.getString("emp_id"), value);

				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
			// System.out.println("reviewerOutMp==>"+reviewerOutMp);
			// request.setAttribute("hmUserTypeID", hmUserTypeID);
			request.setAttribute("reviewerOutMp", reviewerOutMp);

			sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as count,emp_id,appraisal_id,appraisal_freq_id from (select emp_id,appraisal_id,user_id,"
					+ "user_type_id,appraisal_freq_id from appraisal_question_answer where appraisal_id=? and appraisal_freq_id=? and is_submit=true ");
			if (sbEmpId != null) {
				sbQuery.append(" and emp_id in (" + sbEmpId.toString() + ") ");
			}
			if (strUserTypesForFeedback != null && strUserTypesForFeedback.length() > 0) {
				sbQuery.append(" and user_type_id not in (" + strUserTypesForFeedback + ") ");
			}
			sbQuery.append(" and reviewer_or_appraiser=0 group by emp_id,user_id,user_type_id,appraisal_id,appraisal_freq_id) as a group by emp_id,appraisal_id,appraisal_freq_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			// System.out.println("hmEmpCount pst3==>"+pst);
			// pst = con.prepareStatement("select count(*) as
			// count,emp_id,appraisal_id,appraisal_freq_id from (select
			// emp_id,appraisal_id,user_type_id,appraisal_freq_id from
			// appraisal_question_answer "
			// + "where appraisal_id=? and appraisal_freq_id = ? and
			// reviewer_or_appraiser=0 group by
			// emp_id,user_type_id,appraisal_id,appraisal_freq_id)as a group by
			// emp_id,appraisal_id,appraisal_freq_id");
			// pst = con.prepareStatement(sbQuery.toString());
			// pst.setInt(1, uF.parseToInt(getId()));
			// pst.setInt(2, uF.parseToInt(getAppFreqId()));
			// System.out.println("hmEmpCount pst3==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmEmpCount = new HashMap<String, String>();
			while (rs.next()) {
				hmEmpCount.put(rs.getString("emp_id"), rs.getString("count"));
			}
			rs.close();
			pst.close();
			
			//===start parvez date: 18-07-2022===
			if(uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){
				sbQuery = new StringBuilder();
				sbQuery.append("select count(*) as count,emp_id,appraisal_id,appraisal_freq_id from reviewer_feedback_details where appraisal_id=? and appraisal_freq_id=? and is_submit=true ");
				if (sbEmpId != null) {
					sbQuery.append(" and emp_id in (" + sbEmpId.toString() + ")");
				}
				if (strUserTypesForFeedback != null && strUserTypesForFeedback.length() > 0) {
					sbQuery.append(" and user_type_id not in (" + strUserTypesForFeedback + ") ");
				}
				sbQuery.append(" group by emp_id,user_id,user_type_id,appraisal_id,appraisal_freq_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getId()));
				pst.setInt(2, uF.parseToInt(getAppFreqId()));
//				System.out.println("hmEmpCount pst3==>"+pst);
				rs = pst.executeQuery();
				
				while (rs.next()) {
					int count = uF.parseToInt(hmEmpCount.get(rs.getString("emp_id")))+uF.parseToInt(rs.getString("count"));
					hmEmpCount.put(rs.getString("emp_id"), count+"");
				}
				rs.close();
				pst.close();
			}
		//===end parvez date: 18-07-2022===
			// System.out.println("hmEmpCount ===>> " + hmEmpCount);

			request.setAttribute("hmEmpCount", hmEmpCount);

			sbQuery = new StringBuilder();
			// select * from reviewee_strength_improvements rsi,
			// appraisal_reviewee_details ard where rsi.emp_id= ard.reviewee_id
			// and rsi.review_freq_id=ard.appraisal_freq_id and rsi.review_id=25
			// and rsi.review_freq_id=24 order by emp_id,user_id,user_type_id
			
	//===start parvez date: 18-04-2022===		
			
//			sbQuery.append("select * from reviewee_strength_improvements rsi, appraisal_reviewee_details ard where rsi.emp_id= ard.reviewee_id and rsi.review_freq_id=ard.appraisal_freq_id and rsi.review_id=? and rsi.review_freq_id=? ");
//			sbQuery.append(" order by emp_id,user_id,user_type_id");
			sbQuery.append("select * from reviewee_strength_improvements rsi, appraisal_reviewee_details ard where rsi.emp_id= ard.reviewee_id and rsi.review_id=? and rsi.review_freq_id=? ");
			sbQuery.append(" order by emp_id,user_id,user_type_id");
	//===end parvez date: 18-04-2022===
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
//			 System.out.println("pst ===>> " + pst);
			Map<String, StringBuilder> hmEmpwiseStrengthAndImprovements = new HashMap<String, StringBuilder>();
			while (rs.next()) {
				if (rs.getString("areas_of_strength") != null && !rs.getString("areas_of_strength").equals("")
						&& !rs.getString("areas_of_strength").equals("''") && !rs.getString("areas_of_strength").equalsIgnoreCase("N/A")) {
					StringBuilder sbStrengthData = hmEmpwiseStrengthAndImprovements.get(rs.getString("emp_id") + "_STRENGTH");
					if (sbStrengthData == null) {
						sbStrengthData = new StringBuilder();
						sbStrengthData.append(hmEmpName.get(rs.getString("user_id")) + ": " + rs.getString("areas_of_strength"));
					} else {
						sbStrengthData.append("\n" + hmEmpName.get(rs.getString("user_id")) + ": " + rs.getString("areas_of_strength"));
					}
					hmEmpwiseStrengthAndImprovements.put(rs.getString("emp_id") + "_STRENGTH", sbStrengthData);
				}

				if (rs.getString("areas_of_improvement") != null && !rs.getString("areas_of_improvement").equals("")
						&& !rs.getString("areas_of_improvement").equals("''") && !rs.getString("areas_of_improvement").equalsIgnoreCase("N/A")) {
					StringBuilder sbImprovementsData = hmEmpwiseStrengthAndImprovements.get(rs.getString("emp_id") + "_IMPROVEMENTS");
					if (sbImprovementsData == null) {
						sbImprovementsData = new StringBuilder();
						sbImprovementsData.append(hmEmpName.get(rs.getString("user_id")) + ": " + rs.getString("areas_of_improvement"));
					} else {
						sbImprovementsData.append("\n" + hmEmpName.get(rs.getString("user_id")) + ": " + rs.getString("areas_of_improvement"));
					}
					hmEmpwiseStrengthAndImprovements.put(rs.getString("emp_id") + "_IMPROVEMENTS", sbImprovementsData);
				}
			}
			rs.close();
			pst.close();

			sbQuery = new StringBuilder();
			
	//===start parvez date: 18-04-2022===		
//			sbQuery.append("select emp_id,user_id,user_type_id,section_id,section_comment from appraisal_question_answer aqa, appraisal_reviewee_details ard where aqa.emp_id= ard.reviewee_id and "
//					+ " aqa.appraisal_freq_id=ard.appraisal_freq_id and aqa.user_type_id=2 and aqa.appraisal_id=? and aqa.appraisal_freq_id=? group by aqa.emp_id,aqa.user_id,aqa.user_type_id,"
//					+ " aqa.section_id,aqa.section_comment order by aqa.emp_id,aqa.user_id,aqa.section_id,aqa.user_type_id");
			
			sbQuery.append("select emp_id,user_id,user_type_id,section_id,section_comment from appraisal_question_answer aqa, appraisal_reviewee_details ard where aqa.emp_id= ard.reviewee_id and "
					+ " aqa.user_type_id=2 and aqa.appraisal_id=? and aqa.appraisal_freq_id=? group by aqa.emp_id,aqa.user_id,aqa.user_type_id,"
					+ " aqa.section_id,aqa.section_comment order by aqa.emp_id,aqa.user_id,aqa.section_id,aqa.user_type_id");
	//===end parvez date: 18-04-2022===		
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
//			 System.out.println("pst ===>> " + pst);
			Map<String, StringBuilder> hmEmpwiseManagerCommentsOfSections = new HashMap<String, StringBuilder>();
			
			Map<String, String> hmEmpwiseUserwiseManagerCommentsOfSections = new HashMap<String, String>();
			while (rs.next()) {
				if (rs.getString("section_comment") != null && !rs.getString("section_comment").equals("") && !rs.getString("section_comment").equals("''")
						&& !rs.getString("section_comment").equalsIgnoreCase("N/A") && !rs.getString("section_comment").equalsIgnoreCase("-")) {
					String strComment = hmEmpwiseUserwiseManagerCommentsOfSections.get(rs.getString("emp_id") + "_" + rs.getString("user_id"));
					StringBuilder sbCommentsData = hmEmpwiseStrengthAndImprovements.get(rs.getString("emp_id"));
					if (sbCommentsData == null) {
						sbCommentsData = new StringBuilder();
						sbCommentsData.append(hmEmpName.get(rs.getString("user_id")) + ": " + rs.getString("section_comment"));
					} else {
						if (strComment == null) {
							sbCommentsData.append("\n" + hmEmpName.get(rs.getString("user_id")) + ": " + rs.getString("section_comment"));
						} else {
							sbCommentsData.append("\n" + rs.getString("section_comment"));
						}
					}
					hmEmpwiseUserwiseManagerCommentsOfSections.put(rs.getString("emp_id") + "_" + rs.getString("user_id"), rs.getString("section_comment"));
					hmEmpwiseManagerCommentsOfSections.put(rs.getString("emp_id"), sbCommentsData);
				}
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpwiseStrengthAndImprovements="+hmEmpwiseStrengthAndImprovements);

			Map<String, String> locationMp = (Map<String, String>) request.getAttribute("locationMp");
			List<String> memberList = (List<String>) request.getAttribute("memberList");
			Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);
			Map<String, String> hmEmpDepartment = CF.getEmpDepartmentNameMap(con);
			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelName = CF.getLevelMap(con);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);

			for (int i = 0; memberList != null && i < memberList.size(); i++) {
				// System.out.println("MemberList"+orientationMemberMp.get(memberList.get(i)));
				// TODO : step 2
				// System.out.println("Member : "+orientationMemberMp.get(memberList.get(i))+"-"+hmUserTypeValue.get(memberList.get(i)));
				alInnerExport.add(new DataStyle(orientationMemberMp.get(memberList.get(i)) + "::" + hmUserTypeValue.get(memberList.get(i)), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(orientationMemberMp.get(memberList.get(i)) + " Avg Score", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				
		//===start parvez date: 18-04-2022===		
				alInnerExport.add(new DataStyle(orientationMemberMp.get(memberList.get(i)) + " Avg Percent", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		//===end parvez date: 18-04-2022===		
				
			}
			alInnerExport.add(new DataStyle("Balanced Score", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		//===start parvez date: 18-04-2022===	
			alInnerExport.add(new DataStyle("Balanced Score Percentage", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		//===end parvez date: 18-04-2022===	
			alInnerExport.add(new DataStyle("Overall Comments", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Area of Strength", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Area of Improvement", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));

			reportListExport.add(alInnerExport);

			for (int i = 0; empList != null && i < empList.size(); i++) {
				int memberCount = uF.parseToInt(hmRevieweewiseMemberCount.get(empList.get(i)));
				alInnerExport = new ArrayList<DataStyle>();
				if (uF.parseToInt(empList.get(i)) > 0) {
					Map<String, Map<String, String>> value = outerMp.get(empList.get(i).trim());
					if (value == null)
						value = new HashMap<String, Map<String, String>>();
					alInnerExport.add(new DataStyle(hmEmpCode.get(empList.get(i)), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
					alInnerExport.add(new DataStyle(hmEmpName.get(empList.get(i)), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
					alInnerExport.add(new DataStyle(hmEmpDepartment.get(empList.get(i)), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
					alInnerExport.add(new DataStyle(hmLevelName.get(hmEmpLevelId.get(empList.get(i))), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0",
							BaseColor.WHITE));
					alInnerExport.add(new DataStyle(hmEmpCodeDesig.get(empList.get(i)), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
					alInnerExport.add(new DataStyle(locationMp.get(empList.get(i)), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));

					// uF.showData(uF.getRoundOffValue(2,uF.parseToDouble(value.get("AGGREGATE"))),
					// "NA");
					// System.out.println("EmpName==>"+hmEmpName.get(empList.get(i))+"EmpCode==>"+hmEmpCode.get(empList.get(i))+"EmpDsignation==>"+hmEmpCodeDesig.get(empList.get(i))+"EmpLocation"+locationMp.get(empList.get(i))+"value"+uF.showData(uF.getRoundOffValue(2,uF.parseToDouble(value.get("AGGREGATE"))),
					// "NA"));
					int addOneCnt = 0;
					for (int j = 0; memberList != null && j < memberList.size(); j++) {
						// if(uF.parseToInt(empList.get(i).trim())==725 ||
						// uF.parseToInt(empList.get(i).trim())==607) {
						// System.out.println("uF.parseToInt(memberList.get(j))===>> "
						// + uF.parseToInt(memberList.get(j)));
						// }
						if (uF.parseToInt(memberList.get(j)) == 3) {
							addOneCnt = 1;
						}

						String count = hmUserTypeValue.get(memberList.get(j));
						// System.out.println("count : "+count);

						if (value.get(memberList.get(j).trim()) != null) {
							
							// alInnerExport.add(new
							// DataStyle(uF.showData(value.get(memberList.get(j)),"0")+"%",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
							double dblTotScore = 0;
							double dblScorePer = 0;
							Map<String, String> hmEmpUSerData = value.get(memberList.get(j));
							if (hmEmpUSerData == null)
								hmEmpUSerData = new HashMap<String, String>();
							 
							Iterator<String> it = hmEmpUSerData.keySet().iterator();
							while (it.hasNext()) {
								String userId = it.next();
							//===start parvez date: 27-03-2023===	
//								double dblRating = uF.parseToDouble(hmEmpUSerData.get(userId)) / 20;
								double dblRating = 0;
								if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
									dblRating = uF.parseToDouble(hmEmpUSerData.get(userId)) / 10;
								}else{
									dblRating = uF.parseToDouble(hmEmpUSerData.get(userId)) / 20;
								}
							//===end parvez date: 27-03-2023===	
								dblTotScore += dblRating;
								
						//===start parvez date: 18-04-2022===		
								dblScorePer += uF.parseToDouble(hmEmpUSerData.get(userId));
						//===end parvez date: 18-04-2022===		
								
								// TODO : step 3
								

//								if(uF.parseToInt(empList.get(i))==54 && uF.parseToInt(userId) == 58){
//									System.out.println("ApSt/2899---marks=="+hmEmpUSerData.get(userId));
//								}
								alInnerExport.add(new DataStyle(
										hmEmpName.get(userId) + ": " + uF.showData(uF.formatIntoOneDecimalWithOutComma(dblRating), "0"), Element.ALIGN_LEFT,
										"NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
							}
							for (int k = hmEmpUSerData.size(); k < uF.parseToInt(count); k++) {
								alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
							}
							
							if (hmEmpUSerData.size() > 0) {
								dblTotScore = dblTotScore / hmEmpUSerData.size();
						//===start parvez date: 18-04-2022===		
								dblScorePer = dblScorePer / hmEmpUSerData.size();
//								System.out.println("ApSt/2910---dblScorePer=="+dblScorePer+"---hmEmpUSerData.size()=="+hmEmpUSerData.size());
						//===end parvez date: 18-04-2022===		
							}
							 
							alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoOneDecimalWithOutComma(dblTotScore), "0"), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
					
					//===start parvez date: 18-04-2022===
							alInnerExport.add(new DataStyle(uF.showData(dblScorePer+"", "0")+"%", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
					//===end parvez date: 18-04-2022===
							
						} else {
							// alInnerExport.add(new
							// DataStyle("0%",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
							// for(int k=0; k<uF.parseToInt(count); k++) {
							// alInnerExport.add(new DataStyle("",
							// Element.ALIGN_LEFT,"NEW_ROMAN", 10, "0", "0",
							// BaseColor.WHITE));
							// }//Created by dattatray Date:03-July-21 Note ;
							// For loop committed
							alInnerExport.add(new DataStyle("0", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
							alInnerExport.add(new DataStyle("0", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));// Created by Dattatray Date :02-July-21
							
					//===start parvez date: 18-04-2022===		
							alInnerExport.add(new DataStyle("0%", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
					//===end parvez date: 18-04-2022===		
						}
					}

					// boolean flag = false;
					/*
					 * StringBuilder attribIds = new StringBuilder(); int
					 * attribCnt = 0; int aggregateCnt = 0; for(int a=0;
					 * attribIdList != null && !attribIdList.isEmpty() &&
					 * a<attribIdList.size(); a++) { double aggregate =
					 * uF.parseToDouble(hmScoreAggregateMap.get(empList.get(i).
					 * trim()+"_"+attribIdList.get(a)));
					 * 
					 * if(aggregate <
					 * uF.parseToDouble(hmAttributeThreshhold.get(attribIdList.
					 * get(a)))) {
					 * 
					 * attribIds.append(attribIdList.get(a)+"::");
					 * aggregateCnt++; } attribCnt++; }
					 */

					// if(attribCnt == aggregateCnt) {
					// flag = true;
					// }
					// String aggregate="0.0"; 725,607
					// if(uF.parseToInt(empList.get(i).trim())==725 ||
					// uF.parseToInt(empList.get(i).trim())==607) {
					// System.out.println("(memberCount+addOneCnt) ===>> " +
					// (memberCount+addOneCnt));
					// System.out.println("addOneCnt ===>> " + addOneCnt);
					// System.out.println("hmEmpCount.get(empList.get(i).trim())===>> "
					// + hmEmpCount.get(empList.get(i).trim()));
					// }
					if ((memberCount + addOneCnt) == uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))) {
						// aggregate = value.get("AGGREGATE") != null ?
						// uF.parseToDouble(value.get("AGGREGATE")) / 20 + "" :
						// "0";
						
					//===start parvez date: 27-03-2023===	
//						double dblRating = uF.parseToDouble(hmEmpBalancedScore11.get(empList.get(i).trim())) / 20;
						double dblRating = 0;
						if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
							dblRating = uF.parseToDouble(hmEmpBalancedScore11.get(empList.get(i).trim())) / 10;
						}else{
							dblRating = uF.parseToDouble(hmEmpBalancedScore11.get(empList.get(i).trim())) / 20;
						}
					//===end parvez date: 27-03-2023===	
						alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoOneDecimalWithOutComma(dblRating), "0"), Element.ALIGN_LEFT, "NEW_ROMAN", 10,
								"0", "0", BaseColor.WHITE));
						
				//===start parvez date: 18-04-2022===
						alInnerExport.add(new DataStyle(uF.showData(hmEmpBalancedScore11.get(empList.get(i).trim()), "0")+"%", Element.ALIGN_LEFT, "NEW_ROMAN", 10,
								"0", "0", BaseColor.WHITE));
				//===end parvez date: 18-04-2022===		
						
					} else {
						alInnerExport.add(new DataStyle("NA", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				//===start parvez date: 18-04-2022===		
						alInnerExport.add(new DataStyle("NA", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				//===end parvez date: 18-04-2022===		
					}
					
					alInnerExport.add(new DataStyle(hmEmpwiseManagerCommentsOfSections.get(empList.get(i).trim()) != null ? uF.showData(
							hmEmpwiseManagerCommentsOfSections.get(empList.get(i).trim()).toString(), "NA") : "", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0",
							"0", BaseColor.WHITE));
//					System.out.println("ASt/2943--hmEmpwiseStrengthAndImprovements="+hmEmpwiseStrengthAndImprovements.get(empList.get(i).trim() + "_STRENGTH"));
					alInnerExport.add(new DataStyle(hmEmpwiseStrengthAndImprovements.get(empList.get(i).trim() + "_STRENGTH") != null ? uF.showData(
							hmEmpwiseStrengthAndImprovements.get(empList.get(i).trim() + "_STRENGTH").toString(), "NA") : "", Element.ALIGN_LEFT, "NEW_ROMAN",
							10, "0", "0", BaseColor.WHITE));
					alInnerExport.add(new DataStyle(hmEmpwiseStrengthAndImprovements.get(empList.get(i).trim() + "_IMPROVEMENTS") != null ? uF.showData(
							hmEmpwiseStrengthAndImprovements.get(empList.get(i).trim() + "_IMPROVEMENTS").toString(), "NA") : "", Element.ALIGN_LEFT,
							"NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));

					reportListExport.add(alInnerExport);
				}
			}
			session.setAttribute("reportListExportScoreCard", reportListExport);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	public static Integer findMax(List<Integer> list) {

		// check list is empty or not
		if (list == null || list.size() == 0) {
			return Integer.MIN_VALUE;
		}

		// create a new list to avoid modification
		// in the original list
		List<Integer> sortedlist = new ArrayList<Integer>(list);

		// sort list in natural order
		Collections.sort(sortedlist);

		// last element in the sorted list would be maximum
		return sortedlist.get(sortedlist.size() - 1);
	}
	
	
	private void getAppraisalStatus(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> orientationMemberMp = getOrientationMember();

		// String self_ids = null;
		String oriented_type = null;
		try {

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);

			// ===start parvez date: 21-03-2022===
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
			List<String> alFeatureUserTypeId = hmFeatureUserTypeId.get(F_ENABLE_BALANCE_SCORECARD_CALCULATION_USERTYPE);

			boolean isUserTypeRating = uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_BALANCE_SCORECARD_CALCULATION_USERTYPE));
			Map<String, String> hmRevieweeId = CF.getAppraisalRevieweesId(con, uF);
			if (hmRevieweeId == null)
				hmRevieweeId = new HashMap<String, String>();
			// ===end parvez date: 21-03-2022===

			Map<String, String> hmAttributeThreshhold = new HashMap<String, String>();
			pst = con.prepareStatement("select attribute_id,threshhold from appraisal_attribute_level");
			// pst=con.prepareStatement(selectAttribute);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmAttributeThreshhold.put(rs.getString("attribute_id"), rs.getString("threshhold"));
			}
			rs.close();
			pst.close();

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,supervisor_emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id "
					+ " and is_alive= true and emp_per_id >0 order by supervisor_emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			// System.out.println("PST ===>> " + pst);
			Map<String, List<String>> hmHireracyLevelEmpIds = new LinkedHashMap<String, List<String>>();
			// List<String> alHireracyLevels = new ArrayList<String>();
			while (rs.next()) {
				List<String> alInner = hmHireracyLevelEmpIds.get(rs.getString("supervisor_emp_id"));
				if (alInner == null)
					alInner = new ArrayList<String>();
				if (uF.parseToInt(rs.getString("supervisor_emp_id")) == uF.parseToInt(rs.getString("emp_id"))) {
					continue;
				}
				alInner.add(rs.getString("emp_id"));
				hmHireracyLevelEmpIds.put(rs.getString("supervisor_emp_id"), alInner);
			}
			rs.close();
			pst.close();

			StringBuilder sbClildEmpIds = null;
			List<String> empIDList = new ArrayList<String>();
			sbClildEmpIds = getChildEmpIds(hmHireracyLevelEmpIds, strSessionEmpId, empIDList, sbClildEmpIds);

			StringBuilder sbEmpId = null;
			if (strSessionUserType != null && strSessionUserType.equals(MANAGER)) {
				sbQuery = new StringBuilder();
				sbQuery.append("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id  and appraisal_id=? "
						+ "and (supervisor_ids like '%,"
						+ strSessionEmpId
						+ ",%' or grand_supervisor_ids like '%,"
						+ strSessionEmpId
						+ ",%' or hod_ids like '%," + strSessionEmpId + ",%' ");
				if (sbClildEmpIds != null && !sbClildEmpIds.toString().equals("")) {
					sbQuery.append(" or epd.emp_per_id in (" + sbClildEmpIds.toString() + ") ");
				}
				sbQuery.append(") order by emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
				// pst = con.prepareStatement("select reviewee_id from
				// appraisal_reviewee_details ard, employee_personal_details epd
				// where epd.emp_per_id=ard.reviewee_id and supervisor_ids like
				// '%,"+strSessionEmpId+",%' and appraisal_id=? order by
				// emp_fname");
				pst.setInt(1, uF.parseToInt(getId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					if (sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rs.getString("reviewee_id"));
					} else {
						sbEmpId.append("," + rs.getString("reviewee_id"));
					}
				}
				rs.close();
				pst.close();
			}

			// sbEmpId = new StringBuilder("719");

			double dblTotalMarks1 = 0;
			double dblTotalWeightage1 = 0;
			double dblTotalAggregate1 = 0;
			Map<String, String> hmScoreAggregateMap = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(marks) as marks, sum(weightage) as weightage, aqw.appraisal_attribute, aqw.emp_id from appraisal_question_answer aqw "
					+ " where aqw.appraisal_id=? and aqw.appraisal_freq_id=? and aqw.is_submit=true ");
			if (sbEmpId != null) {
				sbQuery.append(" and emp_id in (" + sbEmpId.toString() + ")");
			}
			sbQuery.append(" group by aqw.appraisal_attribute,aqw.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(id));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			// System.out.println("getAppraisalStatus pst1==>"+pst);
			rs = pst.executeQuery();
			List<String> attribIdList = new ArrayList<String>();
			while (rs.next()) {

				dblTotalMarks1 = uF.parseToDouble(rs.getString("marks"));
				dblTotalWeightage1 = uF.parseToDouble(rs.getString("weightage"));
				dblTotalAggregate1 = uF.parseToDouble(uF.formatIntoTwoDecimal(((dblTotalMarks1 / dblTotalWeightage1) * 100)));

				if (!attribIdList.contains(rs.getString("appraisal_attribute"))) {
					attribIdList.add(rs.getString("appraisal_attribute"));
				}
				hmScoreAggregateMap.put(rs.getString("emp_id") + "_" + rs.getString("appraisal_attribute"), uF.showData("" + dblTotalAggregate1, "0"));
			}
			rs.close();
			pst.close();

			request.setAttribute("hmAttributeThreshhold", hmAttributeThreshhold);
			request.setAttribute("attribIdList", attribIdList);
			request.setAttribute("hmScoreAggregateMap", hmScoreAggregateMap);

			Map<String, String> hmFrequency = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();

			Map<String, String> hmDesignation = CF.getDesigMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			Map<String, String> hmLevelMap = getLevelMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> orientationMp = getOrientationValue(con);
			/*
			 * pst = con. prepareStatement(
			 * "select * from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id "
			 * +
			 * " and (is_delete is null or is_delete = false) and appraisal_details_id =?"
			 * );
			 */
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			// System.out.println("==>pstAppraisaldetails"+pst);
			Map<String, String> appraisalMp = new HashMap<String, String>();
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			int memberCount = 0;
			List<String> alUserTypeForFeedback = new ArrayList<String>();
			String strUserTypesForFeedback = null;
			while (rs.next()) {

				List<String> memberList = new ArrayList<String>();

				if (rs.getString("usertype_member") != null && !rs.getString("usertype_member").equals("")) {
					memberList = Arrays.asList(rs.getString("usertype_member").split(","));
				}
				
				String memberName = "";

				for (int i = 0; memberList != null && !memberList.isEmpty() && i < memberList.size(); i++) {
					if (i == 0) {
						memberName += orientationMemberMp.get(memberList.get(i));
					} else {
						memberName += ", " + orientationMemberMp.get(memberList.get(i));
					}
					memberCount++;
				}
				if (memberName == null || memberName.equals("null")) {
					memberName = "Anyone";
				}
				appraisalMp.put("ID", rs.getString("appraisal_details_id"));
				appraisalMp.put("APPRAISAL", rs.getString("appraisal_name"));
				appraisalMp.put("APPRAISALTYPE", uF.showData(rs.getString("appraisal_type"), ""));
				appraisalMp.put("DESCRIPTION", uF.showData(rs.getString("appraisal_description"), ""));
				appraisalMp.put("INSTRUCTION", uF.showData(rs.getString("appraisal_instruction"), ""));
				appraisalMp.put("ORIENT", orientationMp.get(rs.getString("oriented_type")) + "&deg( " + memberName + " )");
				appraisalMp.put("EMPLOYEE", uF.showData(getAppendData(rs.getString("self_ids"), hmEmpName), ""));
				appraisalMp.put("LEVEL", uF.showData(hmLevelMap.get(rs.getString("level_id")), ""));
				appraisalMp.put("DESIG", hmDesignation.get(rs.getString("desig_id")));
				appraisalMp.put("GRADE", hmGradeMap.get(rs.getString("grade_id")));
				appraisalMp.put("WLOCATION", rs.getString("wlocation_id"));
				appraisalMp.put("PEER", rs.getString("peer_ids"));
				appraisalMp.put("SELFID", rs.getString("self_ids"));
				appraisalMp.put("APPRAISEE", uF.showData(getAppendData(rs.getString("self_ids"), hmEmpName), ""));
				appraisalMp.put("SUPERVISORID", rs.getString("supervisor_id"));
				appraisalMp.put("FREQUENCY", uF.showData(hmFrequency.get(rs.getString("frequency")), ""));
				appraisalMp.put("FROM", uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalMp.put("TO", uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalMp.put("IS_CLOSE", rs.getString("is_close"));
				appraisalMp.put("CEO", rs.getString("ceo_ids"));
				appraisalMp.put("HOD", rs.getString("hod_ids"));

				StringBuilder sbAppraisers = new StringBuilder();
				if (rs.getString("usertype_member") != null && rs.getString("usertype_member").length() > 0) {
					List<String> alAppraiserMember = Arrays.asList(rs.getString("usertype_member").split(","));
					for (int i = 0; alAppraiserMember != null && i < alAppraiserMember.size(); i++) {
						if (uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
							sbAppraisers.append("Managers: " + uF.showData(getAppendData(rs.getString("supervisor_id"), hmEmpName), "N/A") + "</br>");
						} else if (uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(HOD))) {
							sbAppraisers.append("HODs: " + uF.showData(getAppendData(rs.getString("hod_ids"), hmEmpName), "N/A") + "</br>");
						} else if (uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(CEO))) {
							sbAppraisers.append("CEOs: " + uF.showData(getAppendData(rs.getString("ceo_ids"), hmEmpName), "N/A") + "</br>");
						} else if (uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(HRMANAGER))) {
							sbAppraisers.append("HRs: " + uF.showData(getAppendData(rs.getString("hr_ids"), hmEmpName), "N/A") + "</br>");
						} else if (uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(ADMIN))) {
							sbAppraisers.append("Global HRs: " + uF.showData(getAppendData(rs.getString("hr_ids"), hmEmpName), "N/A") + "</br>");
						} else if (uF.parseToInt(alAppraiserMember.get(i)) == 4) {
							sbAppraisers.append("Peers: " + uF.showData(getAppendData(rs.getString("peer_ids"), hmEmpName), "N/A") + "</br>");
						} else if (uF.parseToInt(alAppraiserMember.get(i)) == 10) {
							sbAppraisers.append("Anyone: " + uF.showData(getAppendData(rs.getString("other_ids"), hmEmpName), "N/A") + "</br>");
						}
					}
				}
				appraisalMp.put("APPRAISER", uF.showData(sbAppraisers.toString(), ""));
				appraisalMp.put("REVIEWER", uF.showData(getAppendData(rs.getString("reviewer_id"), hmEmpName), ""));
				appraisalMp.put("ANONYMOUS_REVIEW", uF.showData(rs.getString("is_anonymous_review"), ""));

				if (rs.getString("user_types_for_feedback") != null) {
					alUserTypeForFeedback = Arrays.asList(rs.getString("user_types_for_feedback").split(","));
					strUserTypesForFeedback = rs.getString("user_types_for_feedback").substring(1, (rs.getString("user_types_for_feedback").length() - 1));
				}

				request.setAttribute("memberList", memberList);
			}
			rs.close();
			pst.close();

			sbQuery = new StringBuilder();
			sbQuery.append("select * from appraisal_reviewee_details where appraisal_id=? ");
			if (sbEmpId != null) {
				sbQuery.append(" and reviewee_id in (" + sbEmpId.toString() + ")");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			// pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
//			System.out.println("ApS/3261---pst="+pst);
			Map<String, String> hmRevieweewiseMemberCount = new HashMap<String, String>();
			while (rs.next()) {
				StringBuilder sbAllUserIds = new StringBuilder();
				if (!alUserTypeForFeedback.contains("6")) {
					sbAllUserIds.append(rs.getString("subordinate_ids") != null ? rs.getString("subordinate_ids") : "");
				}
				if (!alUserTypeForFeedback.contains("4")) {
					sbAllUserIds.append(rs.getString("peer_ids") != null ? rs.getString("peer_ids") : "");
				}
				if (!alUserTypeForFeedback.contains("14")) {
					sbAllUserIds.append(rs.getString("other_peer_ids") != null ? rs.getString("other_peer_ids") : "");
				}
				if (!alUserTypeForFeedback.contains("2")) {
					sbAllUserIds.append(rs.getString("supervisor_ids") != null ? rs.getString("supervisor_ids") : "");
				}
				if (!alUserTypeForFeedback.contains("8")) {
					sbAllUserIds.append(rs.getString("grand_supervisor_ids") != null ? rs.getString("grand_supervisor_ids") : "");
				}
				if (!alUserTypeForFeedback.contains("13")) {
					sbAllUserIds.append(rs.getString("hod_ids") != null ? rs.getString("hod_ids") : "");
				}
				if (!alUserTypeForFeedback.contains("5")) {
					sbAllUserIds.append(rs.getString("ceo_ids") != null ? rs.getString("ceo_ids") : "");
				}
				if (!alUserTypeForFeedback.contains("7")) {
					sbAllUserIds.append(rs.getString("hr_ids") != null ? rs.getString("hr_ids") : "");
				}
				if (!alUserTypeForFeedback.contains("1")) {
					sbAllUserIds.append(rs.getString("ghr_ids") != null ? rs.getString("ghr_ids") : "");
				}
				if (!alUserTypeForFeedback.contains("15")) {
					sbAllUserIds.append(rs.getString("recruiter_ids") != null ? rs.getString("recruiter_ids") : "");
				}
				if (!alUserTypeForFeedback.contains("10")) {
					sbAllUserIds.append(rs.getString("other_ids") != null ? rs.getString("other_ids") : "");
				}
				// sbAllUserIds.append(","+rs.getString("reviewee_id"));
				List<String> al = new ArrayList<String>();
				al = Arrays.asList(sbAllUserIds.toString().split(","));
				int cnt = 0;
				for (int i = 0; i < al.size(); i++) {
					if (uF.parseToInt(al.get(i).trim()) > 0) {
						cnt++;
					}
				}
				hmRevieweewiseMemberCount.put(rs.getString("reviewee_id"), cnt + "");
			}
			rs.close();
			pst.close();
			// System.out.println("hmRevieweewiseMemberCount ===>> " +
			// hmRevieweewiseMemberCount);

			request.setAttribute("hmRevieweewiseMemberCount", hmRevieweewiseMemberCount);
			request.setAttribute("memberCount", memberCount);

			pst = con
					.prepareStatement("select * from appraisal_details_frequency  where (is_delete =false or is_delete is null) and appraisal_id = ? and appraisal_freq_id =?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				appraisalMp.put("APP_FREQ_ID", rs.getString("appraisal_freq_id"));
				appraisalMp.put("APP_FREQ_CLOSE", rs.getString("is_appraisal_close"));
				appraisalMp.put("APP_FREQ_PUBLISH", rs.getString("is_appraisal_publish"));
				appraisalMp.put("APP_FREQ_EXPIRE", rs.getString("freq_publish_expire_status"));
				appraisalMp.put("APP_FREQ_CLOSE_REASON", rs.getString("close_reason"));

				appraisalMp.put("APP_FREQ_FROM", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalMp.put("APP_FREQ_TO", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();

			getEmpWlocation(appraisalMp.get("SELFID"));

			String empids = appraisalMp.get("SELFID") != null && !appraisalMp.get("SELFID").equals("") ? appraisalMp.get("SELFID").substring(1,
					appraisalMp.get("SELFID").length() - 1) : "";
			Map<String, String> empImageMap = new HashMap<String, String>();
			if (empids != null && !empids.equals("") && !empids.equalsIgnoreCase("null")) {
				sbQuery = new StringBuilder();
				sbQuery.append("select emp_image,emp_per_id from employee_personal_details where emp_per_id>0 ");
				if (sbEmpId != null) {
					sbQuery.append(" and emp_per_id in (" + sbEmpId.toString() + ")");
				} else {
					sbQuery.append(" and emp_per_id in (" + empids + ")");
				}
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				while (rs.next()) {
					empImageMap.put(rs.getString("emp_per_id"), rs.getString("emp_image"));
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("empImageMap", empImageMap);

			request.setAttribute("appraisalMp", appraisalMp);

			/*
			 * pst = con. prepareStatement(
			 * "select self_ids,appraisal_details_id,oriented_type from appraisal_details where appraisal_details_id=?"
			 * ); pst.setInt(1, uF.parseToInt(id)); rs = pst.executeQuery();
			 * while (rs.next()) { self_ids = rs.getString("self_ids"); //
			 * appraisal_details_id = rs.getInt(2); oriented_type =
			 * rs.getString(3); } rs.close(); pst.close();
			 */

			List<String> empList = new ArrayList<String>();
			if (getType() != null && getType().equals("KRATARGET")) {
				empList.add(strSessionEmpId);
			} else if (sbEmpId != null) {
				empList = Arrays.asList(sbEmpId.toString().split(","));
			} else {
				pst = con
						.prepareStatement("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id and appraisal_id=? order by emp_fname");
				pst.setInt(1, uF.parseToInt(id));
				rs = pst.executeQuery();
				// System.out.println("pst 1 =========>> " + pst);
				while (rs.next()) {
					empList.add(rs.getString("reviewee_id"));
				}
				rs.close();
				pst.close();
			}

			/*
			 * self_ids=self_ids!=null && !self_ids.equals("") ?
			 * self_ids.substring(1, self_ids.length()-1) : null; List<String>
			 * empList = new ArrayList<String>(); if(getType() != null &&
			 * getType().equals("KRATARGET")) { empList.add(strSessionEmpId); }
			 * else if(sbEmpId!=null) { empList =
			 * Arrays.asList(sbEmpId.toString().split(",")); } else {
			 * if(self_ids != null) { empList =
			 * Arrays.asList(self_ids.split(",")); } }
			 */

			// System.out.println("empList ===>> " + empList);
			// Map<String, String> hmUserTypeID = new HashMap<String, String>();
			// pst = con
			// .prepareStatement("select user_type_id,user_type from
			// user_type");
			// rs = pst.executeQuery();
			// while (rs.next()) {
			// hmUserTypeID.put(rs.getString(2), rs.getString(1));
			// }

			// sbQuery = new StringBuilder();
			// sbQuery.append("select *, (marks*100/weightage) as average from (select sum(marks) as marks, sum(weightage) as weightage,"
			// +
			// "user_type_id,emp_id from appraisal_question_answer where appraisal_id=? and appraisal_freq_id=? and weightage>0 and is_submit=true ");
			// if (sbEmpId != null) {
			// sbQuery.append(" and emp_id in (" + sbEmpId.toString() + ")");
			// }
			// sbQuery.append(" group by user_type_id, emp_id) as a order by emp_id");
			// pst = con.prepareStatement(sbQuery.toString());
			// pst.setInt(1, uF.parseToInt(getId()));
			// pst.setInt(2, uF.parseToInt(getAppFreqId()));
			// System.out.println("pst2==>"+pst);
			// rs = pst.executeQuery();
			// // System.out.println("pst ========> "+pst);
			// Map<String, Map<String, String>> outerMp = new HashMap<String,
			// Map<String, String>>();
			//
			// String strEmpIdNew = null;
			// String strEmpIdOld = null;
			//
			// double dblTotalMarks = 0;
			// double dblTotalWeightage = 0;
			//
			// while (rs.next()) {
			// strEmpIdNew = rs.getString("emp_id");
			// if (strEmpIdNew != null &&
			// !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
			// dblTotalMarks = 0;
			// dblTotalWeightage = 0;
			// }
			//
			// dblTotalMarks += uF.parseToDouble(rs.getString("marks"));
			// // System.out.println("dblTotalMarks"+dblTotalMarks);
			//
			// dblTotalWeightage += uF.parseToDouble(rs.getString("weightage"));
			// Map<String, String> value = outerMp.get(rs.getString("emp_id"));
			// if (value == null) value = new HashMap<String, String>();
			// value.put(rs.getString("user_type_id"),
			// uF.formatIntoTwoDecimal(rs.getDouble("average")));
			// if (dblTotalWeightage > 0) {
			// value.put("AGGREGATE", uF.formatIntoTwoDecimal((dblTotalMarks *
			// 100) / dblTotalWeightage));
			// }
			// outerMp.put(rs.getString("emp_id"), value);
			//
			// strEmpIdOld = strEmpIdNew;
			// }
			// rs.close();
			// pst.close();
			
		//===start parvez date: 10-03-2023===	
			Map<String, String> hmScoreCalBasis = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_question_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmScoreCalBasis.put(rs.getString("appraisal_id"), rs.getString("score_calculation_basis"));
			}
			rs.close();
			pst.close();
		//===end parvez date: 10-03-2023===	

			sbQuery = new StringBuilder();
			
			/*
			 * sbQuery.append(
			 * "select *,(marks*100/weightage) as average from (select sum(aqa.marks) as marks, sum(aqa.weightage) as weightage,aqa.user_type_id,"
			 * + " aqa.emp_id,amld.main_level_id,section_weightage " +
			 * " from appraisal_question_answer aqa ,appraisal_main_level_details amld where aqa.appraisal_id=? and aqa.appraisal_freq_id=? and aqa.weightage>0 and "
			 * + " aqa.is_submit=true and aqa.section_id=amld.main_level_id");
			 * if (sbEmpId != null) { sbQuery.append(" and emp_id in (" +
			 * sbEmpId.toString() + ")"); } sbQuery.append(
			 * " group by aqa.user_type_id,aqa.emp_id,amld.main_level_id) as a order by emp_id,main_level_id,user_type_id"
			 * );
			 */
	//===start parvez date: 10-03-2023===
			/*sbQuery.append("select *,(marks*100/weightage) as average from (select sum(aqa.marks) as marks, sum(aqa.weightage) as weightage,aqa.user_type_id,"
					+ " aqa.emp_id,aqa.appraisal_id,amld.main_level_id,section_weightage,amld.hr,amld.manager,amld.peer,amld.hod,amld.ceo,amld.subordinate,amld.other_peer "
					+ " from appraisal_question_answer aqa ,appraisal_main_level_details amld where aqa.appraisal_id=? and aqa.appraisal_freq_id=? and aqa.weightage>0 and "
					+ " aqa.is_submit=true and aqa.section_id=amld.main_level_id");*/
			/*sbQuery.append("select *,(marks*100/weightage) as average from (select sum(aqa.marks) as marks, sum(aqa.weightage) as weightage,aqa.user_type_id,"
					+ " aqa.emp_id,aqa.appraisal_id,amld.main_level_id,section_weightage,amld.hr,amld.manager,amld.peer,amld.hod,amld.ceo,amld.subordinate,amld.other_peer,aqd.score_calculation_basis "
					+ " from appraisal_question_answer aqa ,appraisal_main_level_details amld, appraisal_question_details aqd where aqa.appraisal_id=? and aqa.appraisal_freq_id=? and aqa.weightage>0 and "
					+ " aqa.is_submit=true and aqa.section_id=amld.main_level_id and aqa.appraisal_question_details_id=aqd.appraisal_question_details_id");*/
			
				sbQuery.append("select *,(marks*100/weightage) as average from (select sum(aqa.marks) as marks, sum(aqa.weightage) as weightage,aqa.user_type_id,aqa.user_id,"
						+ " aqa.emp_id,aqa.appraisal_id,amld.main_level_id,section_weightage,amld.hr,amld.manager,amld.peer,amld.hod,amld.ceo,amld.subordinate,amld.other_peer "
						+ " from appraisal_question_answer aqa ,appraisal_main_level_details amld where aqa.appraisal_id=? and aqa.appraisal_freq_id=? and aqa.weightage>0 and "
						+ " aqa.is_submit=true and aqa.section_id=amld.main_level_id");
			if (sbEmpId != null) {
				sbQuery.append(" and emp_id in (" + sbEmpId.toString() + ")");
			}
			sbQuery.append(" group by aqa.user_type_id,aqa.user_id,aqa.emp_id,aqa.appraisal_id,amld.main_level_id) as a order by emp_id,main_level_id,user_type_id");
//			sbQuery.append(" group by aqa.user_type_id,aqa.emp_id,aqa.appraisal_id,amld.main_level_id,aqd.appraisal_question_details_id) as a order by emp_id,main_level_id,user_type_id");
	// ===end parvez date: 10-03-2023===
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
//			 System.out.println("ApSt/3233--pst2 ====> " + pst);
			rs = pst.executeQuery();
			// System.out.println("pst ========> "+pst);

			// Map<String, Map<String, String>> outerMp1 = new HashMap<String,
			// Map<String, String>>();

//			Map<String, Map<String, Map<String, List<String>>>> outerMp1 = new HashMap<String, Map<String, Map<String, List<String>>>>();
			Map<String, Map<String, Map<String, Map<String, List<String>>>>> outerMp1 = new HashMap<String, Map<String, Map<String, Map<String, List<String>>>>>();

			String strEmpIdNew = null;
			String strEmpIdOld = null;

			double dblTotalMarks = 0;
			double dblTotalWeightage = 0;

			Map<String, String> hmPriorUser = new HashMap<String, String>();
			while (rs.next()) {
				strEmpIdNew = rs.getString("emp_id");
				if (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					dblTotalMarks = 0;
					dblTotalWeightage = 0;
				}

				dblTotalMarks += uF.parseToDouble(rs.getString("marks"));
				// System.out.println("dblTotalMarks"+dblTotalMarks);

				dblTotalWeightage += uF.parseToDouble(rs.getString("weightage"));

				// Map<String, String> value =
				// outerMp.get(rs.getString("emp_id"));
				// if (value == null) value = new HashMap<String, String>();

			//===start parvez date: 18-03-2023===	
//				Map<String, Map<String, List<String>>> value = outerMp1.get(rs.getString("emp_id"));
				Map<String, Map<String, Map<String, List<String>>>> value = outerMp1.get(rs.getString("emp_id"));
//				if (value == null)
//					value = new HashMap<String, Map<String, List<String>>>();
				if (value == null)
					value = new HashMap<String, Map<String, Map<String, List<String>>>>();

				Map<String, Map<String, List<String>>> hmTempLevel = value.get(rs.getString("user_type_id"));
				if (hmTempLevel == null)
					hmTempLevel = new HashMap<String, Map<String, List<String>>>();
				
//				Map<String, List<String>> hmMainLevel = value.get(rs.getString("user_type_id"));
				Map<String, List<String>> hmMainLevel = hmTempLevel.get(rs.getString("user_id"));
				if (hmMainLevel == null)
					hmMainLevel = new HashMap<String, List<String>>();
			
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("average"));
				
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) 
						&&(uF.parseToInt(rs.getString("user_type_id"))==4 || uF.parseToInt(rs.getString("user_type_id"))==14 || uF.parseToInt(rs.getString("user_type_id"))==13)){
					
					if(uF.parseToInt(rs.getString("section_weightage"))<100){
						innerList.add(100+"");
					}else{
						innerList.add(rs.getString("section_weightage"));
					}
				} else{
					innerList.add(rs.getString("section_weightage"));
				}
			//===end parvez date: 18-03-2023===	
			//===start parvez date: 28-02-2023===	
//				innerList.add(rs.getString("score_calculation_basis"));
				innerList.add(hmScoreCalBasis.get(rs.getString("appraisal_id")));
			//===end parvez date: 28-02-2023===	
				
				hmMainLevel.put(rs.getString("main_level_id"), innerList);
			
			//===start parvez date: 18-03-2023===	
				hmTempLevel.put(rs.getString("user_id"), hmMainLevel);

//				value.put(rs.getString("user_type_id"), hmMainLevel);
				value.put(rs.getString("user_type_id"), hmTempLevel);
			//===end parvez date: 18-03-2023===
				
//				System.out.println("hmMainLevel=="+hmMainLevel);

				// value.put(rs.getString("user_type_id"),
				// uF.formatIntoTwoDecimal(rs.getDouble("average")));
				// if (dblTotalWeightage > 0) {
				// value.put("AGGREGATE", uF.formatIntoTwoDecimal((dblTotalMarks
				// * 100) / dblTotalWeightage));
				// }

				outerMp1.put(rs.getString("emp_id"), value);

				// outerMp1.put(rs.getString("emp_id"), value1);

				// ===start parvez date: 21-03-2022===

				List<String> alPriorUserList = new ArrayList<String>();
				if (hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_SUPERVISOR") != null
						&& !hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_SUPERVISOR").equals("")) {
					alPriorUserList.add(rs.getString("manager"));
				}
				if (hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_HOD") != null
						&& !hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_HOD").equals("")) {
					alPriorUserList.add(rs.getString("hod"));
				}
				if (hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_CEO") != null
						&& !hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_CEO").equals("")) {
					alPriorUserList.add(rs.getString("ceo"));
				}
				if (hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_HR") != null
						&& !hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_HR").equals("")) {
					alPriorUserList.add(rs.getString("hr"));
				}
				if (hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_PEER") != null
						&& !hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_PEER").equals("")) {
					alPriorUserList.add(rs.getString("peer"));
				}
				if (hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_OTHER_PEER") != null
						&& !hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_OTHER_PEER").equals("")) {
					alPriorUserList.add(rs.getString("other_peer"));
				}
				if (hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_SUBORDINATE") != null
						&& !hmRevieweeId.get(rs.getString("appraisal_id") + "_" + rs.getString("emp_id") + "_SUBORDINATE").equals("")) {
					alPriorUserList.add(rs.getString("subordinate"));
				}

				Collections.sort(alPriorUserList, Collections.reverseOrder());

				int priorUserTypeId = 0;
				// System.out.println("ApS/3316--isEmpty="+alPriorUserList.isEmpty()+"---alPriorUserList.size="+alPriorUserList.size());

				// ===start parvez date: 23-03-2022===
				if (alPriorUserList != null && !alPriorUserList.isEmpty()) {
					if (uF.parseToInt(rs.getString("manager")) == uF.parseToInt(alPriorUserList.get(0))) {
						priorUserTypeId = 2;
					} else if (uF.parseToInt(rs.getString("hod")) == uF.parseToInt(alPriorUserList.get(0))) {
						priorUserTypeId = 13;
					} else if (uF.parseToInt(rs.getString("ceo")) == uF.parseToInt(alPriorUserList.get(0))) {
						priorUserTypeId = 5;
					} else if (uF.parseToInt(rs.getString("hr")) == uF.parseToInt(alPriorUserList.get(0))) {
						priorUserTypeId = 7;
					} else if (uF.parseToInt(rs.getString("peer")) == uF.parseToInt(alPriorUserList.get(0))) {
						priorUserTypeId = 4;
					}
				}
				// ===end parvez date: 23-03-2022===

				// ===start parvez date: 11-04-2022===
				hmPriorUser.put(rs.getString("emp_id"), priorUserTypeId + "");
				// ===end parvez date: 11-04-2022===

				strEmpIdOld = strEmpIdNew;

			}
			rs.close();
			pst.close();
			// System.out.println(" JSP outerMp1 ===>> " + outerMp1);
				
		//===start parvez date: 18-03-2023===
			Map<String, String> hmFinalScore = new HashMap<String, String>();
			if(uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) || uF.parseToBoolean(hmFeatureStatus.get(F_HR_GHR_APPROVAL_FOR_FINAL_RATING_AND_COMMENT))){
				sbQuery = new StringBuilder();
				sbQuery.append("select distinct(user_type_id) as user_type_id,user_id,rfd.emp_id,reviewer_comment,reviewer_marks,section_weightage,main_level_id from " +
						" reviewer_feedback_details rfd,appraisal_main_level_details amld where rfd.appraisal_id=amld.appraisal_id and rfd.appraisal_id=? and rfd.appraisal_freq_id=? ");
				if (sbEmpId != null) {
					sbQuery.append(" and emp_id in (" + sbEmpId.toString() + ")");
				}
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getId()));
				pst.setInt(2, uF.parseToInt(getAppFreqId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					/*Map<String, Map<String, List<String>>> value = outerMp1.get(rs.getString("emp_id"));
					
					if (value == null)
						value = new HashMap<String, Map<String, List<String>>>();*/
					
					Map<String, Map<String, Map<String, List<String>>>> value = outerMp1.get(rs.getString("emp_id"));
					if (value == null)
						value = new HashMap<String, Map<String, Map<String, List<String>>>>();
					
					
					Map<String, Map<String, List<String>>> hmTempLevel = value.get(rs.getString("user_type_id"));
					if (hmTempLevel == null)
						hmTempLevel = new HashMap<String, Map<String, List<String>>>();

//					Map<String, List<String>> hmMainLevel = value.get(rs.getString("user_type_id"));
					Map<String, List<String>> hmMainLevel = hmTempLevel.get(rs.getString("user_id"));
					if (hmMainLevel == null)
						hmMainLevel = new HashMap<String, List<String>>();

					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("reviewer_marks"));
					innerList.add(rs.getString("section_weightage"));
					// System.out.println("ApS/3146--userTypeId="+rs.getString("user_type_id")+"---average="+rs.getString("average")+"---section_weightage="+rs.getString("section_weightage"));
					hmMainLevel.put(rs.getString("main_level_id"), innerList);

					hmTempLevel.put(rs.getString("user_id"), hmMainLevel);
					
//					value.put(rs.getString("user_type_id"), hmMainLevel);
					value.put(rs.getString("user_type_id"), hmTempLevel);
					outerMp1.put(rs.getString("emp_id"), value);
					hmFinalScore.put(rs.getString("emp_id"), rs.getString("reviewer_marks"));
					
				}
				rs.close();
				pst.close();
			}
	//===end parvez date: 18-03-2023===

			pst = con
					.prepareStatement("select reviewee_id,subordinate_weightage,peer_weightage,other_peer_weightage,supervisor_weightage,grand_supervisor_weightage,hod_weightage,ceo_weightage,hr_weightage,other_weightage "
							+ " from appraisal_reviewee_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
//			 System.out.println("PST === > "+pst.toString());
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmEmpUsertypeW = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				Map<String, String> hmUsertypeW = new HashMap<String, String>();
				if (uF.parseToInt(rs.getString("supervisor_weightage")) > 0) {
					hmUsertypeW.put("2", rs.getString("supervisor_weightage"));
				}
				if (uF.parseToInt(rs.getString("peer_weightage")) > 0) {
					hmUsertypeW.put("4", rs.getString("peer_weightage"));
				}
				if (uF.parseToInt(rs.getString("ceo_weightage")) > 0) {
					hmUsertypeW.put("5", rs.getString("ceo_weightage"));
				}
				if (uF.parseToInt(rs.getString("subordinate_weightage")) > 0) {
					hmUsertypeW.put("6", rs.getString("subordinate_weightage"));
				}
				if (uF.parseToInt(rs.getString("hr_weightage")) > 0) {
					hmUsertypeW.put("7", rs.getString("hr_weightage"));
				}
				if (uF.parseToInt(rs.getString("grand_supervisor_weightage")) > 0) {
					hmUsertypeW.put("8", rs.getString("grand_supervisor_weightage"));
				}
				if (uF.parseToInt(rs.getString("other_weightage")) > 0) {
					hmUsertypeW.put("10", rs.getString("other_weightage"));
				}
				if (uF.parseToInt(rs.getString("hod_weightage")) > 0) {
					hmUsertypeW.put("13", rs.getString("hod_weightage"));
				}
				if (uF.parseToInt(rs.getString("other_peer_weightage")) > 0) {
					hmUsertypeW.put("14", rs.getString("other_peer_weightage"));
				}
				hmEmpUsertypeW.put(rs.getString("reviewee_id"), hmUsertypeW);
			}
			rs.close();
			pst.close();

			boolean isSelfRating = CF.getFeatureManagementStatus(request, uF, F_DISABLE_SELF_APPRAISAL_RATING_DURING_FINAL_RATING_CALCULATION);
			// System.out.println("ApS/3151--isSelfRating="+isSelfRating);

		//===start parvez date: 18-03-2023===	
			/*Map<String, Map<String, String>> outerMp = new HashMap<String, Map<String, String>>();
			Iterator<String> it = outerMp1.keySet().iterator();
			while (it.hasNext()) { 
				String strEmpId = it.next();
				Map<String, String> hmUsertypeW = hmEmpUsertypeW.get(strEmpId);
				if (hmUsertypeW == null)
					hmUsertypeW = new HashMap<String, String>();

				Map<String, String> value1 = outerMp.get(strEmpId);
				if (value1 == null)
					value1 = new HashMap<String, String>();
				Map<String, Map<String, List<String>>> value = outerMp1.get(strEmpId);
				Iterator<String> it1 = value.keySet().iterator();
				double dblAllUsertypeAvg = 0;
				int intUsertypeCnt = 0;
				boolean calculationBasisFlag = false;
				while (it1.hasNext()) {
					String strUsertypeId = it1.next();
					// System.out.println("ApS/3163--strUsertypeId="+strUsertypeId);
					Map<String, List<String>> hmMainLevel = value.get(strUsertypeId);
					Iterator<String> it2 = hmMainLevel.keySet().iterator();
					double dblUsertypeAvg = 0;

					while (it2.hasNext()) {
						String strLevelId = it2.next();
						List<String> innList = hmMainLevel.get(strLevelId);
						String strAvg = innList.get(0);
						String strWtg = innList.get(1);
						
						String actualCaluculationBasis = innList.size()>2 ? innList.get(2) : null;
					//===start parvez date: 28-02-2023===	
						double dblLevelAvg = (uF.parseToDouble(strAvg) * uF.parseToDouble(strWtg)) / 100;
//						double dblLevelAvg = 0;
						if(uF.parseToBoolean(actualCaluculationBasis)){
//							dblLevelAvg = ((uF.parseToDouble(strAvg) * uF.parseToDouble(strWtg)) / 100)/20;
							calculationBasisFlag = true;
						} else{
							calculationBasisFlag = false;
//							dblLevelAvg = (uF.parseToDouble(strAvg) * uF.parseToDouble(strWtg)) / 100;
						}
					//===end parvez date: 28-02-2023===	
						
						
						dblUsertypeAvg += dblLevelAvg;
						
					}
					value1.put(strUsertypeId, uF.formatIntoTwoDecimal(dblUsertypeAvg));

					// ===start parvez date: 09-12-2021===
					

					if (uF.parseToInt(strUsertypeId) != 3 || !isSelfRating) {

						
						if (!isUserTypeRating
								|| (isUserTypeRating && alFeatureUserTypeId.contains(strUsertypeId) && uF.parseToInt(strUsertypeId) == uF
										.parseToInt(hmPriorUser.get(strEmpId)))) {
							
							if (hmUsertypeW != null && uF.parseToDouble(hmUsertypeW.get(strUsertypeId)) > 0) {
							//===start parvez date: 28-02-2023===	
								dblAllUsertypeAvg += (dblUsertypeAvg * uF.parseToDouble(hmUsertypeW.get(strUsertypeId))) / 100;
								
							//===end parvez date: 28-02-2023===	
//								 System.out.println("ApS/3474--strUsertypeId="+strUsertypeId+"--dblUsertypeAvg="+dblUsertypeAvg+"--uF.parseToDouble(hmUsertypeW.get(strUsertypeId)) : "+uF.parseToDouble(hmUsertypeW.get(strUsertypeId)));
							} else {

								dblAllUsertypeAvg += dblUsertypeAvg;
								intUsertypeCnt++;
							}
						}

					}
					// ===end parvez date: 09-12-2021===
				}
				if (intUsertypeCnt == 0) {
					value1.put("AGGREGATE", uF.formatIntoTwoDecimal(dblAllUsertypeAvg));
				} else {
					double dblAggregate = dblAllUsertypeAvg / intUsertypeCnt;
					value1.put("AGGREGATE", uF.formatIntoTwoDecimal(dblAggregate));
				}
			//===start parvez date: 28-02-2023===	
				value1.put("CalculationBasisOn", calculationBasisFlag+"");
			//===end parvez date: 28-02-2023===	
				outerMp.put(strEmpId, value1);
			}*/
			
			Map<String, Map<String, String>> outerMp = new HashMap<String, Map<String, String>>();
			Iterator<String> it = outerMp1.keySet().iterator();
			while (it.hasNext()) { 
				String strEmpId = it.next();
				Map<String, String> hmUsertypeW = hmEmpUsertypeW.get(strEmpId);
				if (hmUsertypeW == null)
					hmUsertypeW = new HashMap<String, String>();

				Map<String, String> value1 = outerMp.get(strEmpId);
				if (value1 == null)
					value1 = new HashMap<String, String>();
				Map<String, Map<String, Map<String, List<String>>>> value = outerMp1.get(strEmpId);
				Iterator<String> it1 = value.keySet().iterator();
				double dblAllUsertypeAvg = 0;
				int intUsertypeCnt = 0;
				boolean calculationBasisFlag = false;
				while (it1.hasNext()) {
					String strUsertypeId = it1.next();
					// System.out.println("ApS/3163--strUsertypeId="+strUsertypeId);
					Map<String, Map<String, List<String>>> hmTempLevel = value.get(strUsertypeId);
					Iterator<String> it2 = hmTempLevel.keySet().iterator();
					
					double dblUsertypeAvg = 0;
					int intUserCnt = 0;
					while (it2.hasNext()) {
						String strUserId = it2.next();
						Map<String, List<String>> hmMainLevel = hmTempLevel.get(strUserId);
						Iterator<String> it3 = hmMainLevel.keySet().iterator();
						
						while (it3.hasNext()) {
							String strLevelId = it3.next();
							List<String> innList = hmMainLevel.get(strLevelId);
							String strAvg = innList.get(0);
							String strWtg = innList.get(1);
							
							String actualCaluculationBasis = innList.size()>2 ? innList.get(2) : null;
						//===start parvez date: 28-02-2023===	
							double dblLevelAvg = (uF.parseToDouble(strAvg) * uF.parseToDouble(strWtg)) / 100;
//							double dblLevelAvg = 0;
							if(uF.parseToBoolean(actualCaluculationBasis)){
//								dblLevelAvg = ((uF.parseToDouble(strAvg) * uF.parseToDouble(strWtg)) / 100)/20;
								calculationBasisFlag = true;
							} else{
								calculationBasisFlag = false;
//								dblLevelAvg = (uF.parseToDouble(strAvg) * uF.parseToDouble(strWtg)) / 100;
							}
						//===end parvez date: 28-02-2023===	
							
							dblUsertypeAvg += dblLevelAvg;
							
						}
						intUserCnt++;
						if (uF.parseToInt(strUsertypeId) != 3 || !isSelfRating) {
							if (!isUserTypeRating
									|| (isUserTypeRating && alFeatureUserTypeId.contains(strUsertypeId) && uF.parseToInt(strUsertypeId) == uF
											.parseToInt(hmPriorUser.get(strEmpId)))) {
								intUsertypeCnt++;
							}
						}	
					}
					
					if(intUserCnt==0){
						value1.put(strUsertypeId, uF.formatIntoTwoDecimal(dblUsertypeAvg));
					} else{
						double agDblUsertypeAvg = dblUsertypeAvg / intUserCnt;
						value1.put(strUsertypeId, uF.formatIntoTwoDecimal(agDblUsertypeAvg));
					}

					if (uF.parseToInt(strUsertypeId) != 3 || !isSelfRating) {
						if (!isUserTypeRating
								|| (isUserTypeRating && alFeatureUserTypeId.contains(strUsertypeId) && uF.parseToInt(strUsertypeId) == uF
										.parseToInt(hmPriorUser.get(strEmpId)))) {
							
							if (hmUsertypeW != null && uF.parseToDouble(hmUsertypeW.get(strUsertypeId)) > 0) {
							//===start parvez date: 28-02-2023===	
								dblAllUsertypeAvg += (dblUsertypeAvg * uF.parseToDouble(hmUsertypeW.get(strUsertypeId))) / 100;
								
							//===end parvez date: 28-02-2023===	
//								 System.out.println("ApS/3474--strUsertypeId="+strUsertypeId+"--dblUsertypeAvg="+dblUsertypeAvg+"--uF.parseToDouble(hmUsertypeW.get(strUsertypeId)) : "+uF.parseToDouble(hmUsertypeW.get(strUsertypeId)));
							} else {

								dblAllUsertypeAvg += dblUsertypeAvg;
								
//								intUsertypeCnt++;
							}
						}

					}
				}
				/*if (intUsertypeCnt == 0) {
					value1.put("AGGREGATE", uF.formatIntoTwoDecimal(dblAllUsertypeAvg));
				} else {
					double dblAggregate = dblAllUsertypeAvg / intUsertypeCnt;
					
					value1.put("AGGREGATE", uF.formatIntoTwoDecimal(dblAggregate));
				}*/
				
				if(hmFinalScore!=null && uF.parseToInt(hmFinalScore.get(strEmpId))>0){
					value1.put("AGGREGATE", uF.formatIntoTwoDecimal(uF.parseToDouble(hmFinalScore.get(strEmpId))));
				} else{
					if (intUsertypeCnt == 0) {
						value1.put("AGGREGATE", uF.formatIntoTwoDecimal(dblAllUsertypeAvg));
					} else {
						double dblAggregate = dblAllUsertypeAvg / intUsertypeCnt;
						
						value1.put("AGGREGATE", uF.formatIntoTwoDecimal(dblAggregate));
					}
				}
				
			//===start parvez date: 28-02-2023===	
				value1.put("CalculationBasisOn", calculationBasisFlag+"");
			//===end parvez date: 28-02-2023===	
				outerMp.put(strEmpId, value1);
			}
//===end parvez date: 18-03-2023===
			// System.out.println("outerMp ===>> " + outerMp);
			// System.out.println("outerMp 123 ===>> " + outerMp1);
			// System.out.println("hmMainLevel ===>> " + hmMainLevel);
			// request.setAttribute("hmUserTypeID", hmUserTypeID);

			request.setAttribute("outerMp", outerMp);
			request.setAttribute("empList", empList);
			request.setAttribute("oriented_type", oriented_type);

			sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as count,emp_id,appraisal_id,appraisal_freq_id from (select emp_id,appraisal_id,user_id,"
					+ "user_type_id,appraisal_freq_id from appraisal_question_answer where appraisal_id=? and appraisal_freq_id=? and is_submit=true ");
			if (sbEmpId != null) {
				sbQuery.append(" and emp_id in (" + sbEmpId.toString() + ")");
			}
			if (strUserTypesForFeedback != null && strUserTypesForFeedback.length() > 0) {
				sbQuery.append(" and user_type_id not in (" + strUserTypesForFeedback + ") ");
			}
			sbQuery.append(" group by emp_id,user_id,user_type_id,appraisal_id,appraisal_freq_id) as a group by emp_id,appraisal_id,appraisal_freq_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			// System.out.println("hmEmpCount pst3==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmEmpCount = new HashMap<String, String>();
			while (rs.next()) {
				hmEmpCount.put(rs.getString("emp_id"), rs.getString("count"));
			}
			rs.close();
			pst.close();
			
		//===start parvez date: 18-07-2022===
			if(uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){
				sbQuery = new StringBuilder();
				sbQuery.append("select count(*) as count,emp_id,appraisal_id,appraisal_freq_id from reviewer_feedback_details where appraisal_id=? and appraisal_freq_id=? and is_submit=true ");
				if (sbEmpId != null) {
					sbQuery.append(" and emp_id in (" + sbEmpId.toString() + ")");
				}
				if (strUserTypesForFeedback != null && strUserTypesForFeedback.length() > 0) {
					sbQuery.append(" and user_type_id not in (" + strUserTypesForFeedback + ") ");
				}
				sbQuery.append(" group by emp_id,user_id,user_type_id,appraisal_id,appraisal_freq_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getId()));
				pst.setInt(2, uF.parseToInt(getAppFreqId()));
//				System.out.println("hmEmpCount pst3==>"+pst);
				rs = pst.executeQuery();
				//System.out.println(hmEmpCount);
				while (rs.next()) {
					int count = uF.parseToInt(hmEmpCount.get(rs.getString("emp_id")))+uF.parseToInt(rs.getString("count"));
//					System.out.println("count="+count+"-----hmEmpCount="+hmEmpCount.get(rs.getString("emp_id")));
					hmEmpCount.put(rs.getString("emp_id"), count+"");
				}
				rs.close();
				pst.close();
			}
		//===end parvez date: 18-07-2022===
			
			// System.out.println("hmEmpCount ===>> " + hmEmpCount);
			request.setAttribute("hmEmpCount", hmEmpCount);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getEmpWlocation(String empIds) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			Map<String, String> locationMp = new HashMap<String, String>();
			con = db.makeConnection(con);

			empIds = empIds != null && !empIds.equals("") ? empIds.substring(1, empIds.length() - 1) : "";

			if (empIds != null && !empIds.equals("") && !empIds.equalsIgnoreCase("null")) {
				pst = con
						.prepareStatement("select eod.wlocation_id,emp_id,wlocation_name from employee_official_details eod,work_location_info wli where eod.wlocation_id=wli.wlocation_id and emp_id in("
								+ empIds + ")");
				// System.out.println("pst====> "+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					locationMp.put(rs.getString("emp_id"), rs.getString("wlocation_name"));
				}
				rs.close();
				pst.close();
			}
			// System.out.println("locationMp ====> "+locationMp);
			request.setAttribute("locationMp", locationMp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public StringBuilder getChildEmpIds(Map<String, List<String>> hmHireracyLevelEmpIds, String empId, List<String> empIDList, StringBuilder sbClildEmpIds) {

		if (empId != null && !empId.trim().equals("")) {
			if (hmHireracyLevelEmpIds == null)
				hmHireracyLevelEmpIds = new HashMap<String, List<String>>();
			List<String> innerList = (List<String>) hmHireracyLevelEmpIds.get(empId.trim());

			for (int i = 0; innerList != null && !innerList.isEmpty() && i < innerList.size(); i++) {
				String empId1 = innerList.get(i);
				if (empId1 != null && !empId1.trim().equals("") && !empIDList.contains(empId1.trim())) {
					empIDList.add(empId1.trim());
					if (sbClildEmpIds == null) {
						sbClildEmpIds = new StringBuilder();
						sbClildEmpIds.append(empId1.trim());
					} else {
						sbClildEmpIds.append("," + empId1.trim());
					}
				}
				if (empId1 != null && !empId1.trim().equals("")) {
					getChildEmpIds(hmHireracyLevelEmpIds, empId1, empIDList, sbClildEmpIds);
				}
			}
		}
		return sbClildEmpIds;
	}

	// private String getOrientationMemberDetails(int id) {
	// Connection con = null;
	// PreparedStatement pst = null;
	// Database db = new Database();
	// ResultSet rs = null;
	// Map<String,String>
	// orientationMp=(Map<String,String>)request.getAttribute("orientationMemberMp");
	// StringBuilder sb=new StringBuilder();
	// try {
	// List<String> memberList=new ArrayList<String>();
	// con = db.makeConnection(con);
	//
	// pst =
	// con.prepareStatement("select * from orientation_details where
	// orientation_id=?");
	// pst.setInt(1,id);
	// rs=pst.executeQuery();
	//
	// while(rs.next()){
	// sb.append(orientationMp.get(rs.getString("member_id"))+",");
	// memberList.add(rs.getString("member_id"));
	// }
	//
	// request.setAttribute("memberList", memberList);
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// db.closeConnection(con);
	// db.closeStatements(pst);
	// }
	// return sb.toString();
	// }

	private Map<String, String> getOrientationValue(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		Map<String, String> orientationMp = new HashMap<String, String>();
		try {

			pst = con.prepareStatement("select * from apparisal_orientation");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMp.put(rs.getString("apparisal_orientation_id"), rs.getString("orientation_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("orientationMp", orientationMp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMp;
	}

	public Map<String, String> getLevelMap(Connection con) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new HashMap<String, String>();
		try {
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();

			while (rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name") + "[" + rs.getString("level_code") + "]");
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmLevelMap;
	}

	private Map<String, String> getOrientationMember() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> orientationMemberMp = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				// orientationMemberMp.put(rs.getString("orientation_member_id"),
				// rs.getString("member_name"));
				orientationMemberMp.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("orientationMemberMp", orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return orientationMemberMp;
	}

	// private List<String> getEmpList(String self_ids) {
	// List<String> empList = new ArrayList<String>();
	//
	// if (self_ids != null && !self_ids.equals("")) {
	//
	// if (self_ids.contains(",")) {
	//
	// String[] temp = self_ids.split(",");
	//
	// for (int i = 1; i < temp.length; i++) {
	//
	// empList.add(temp[i].trim());
	//
	// }
	// } else {
	// empList.add(self_ids);
	// }
	//
	// } else {
	// return null;
	// }
	//
	// return empList;
	// }

	private String getAppendData(String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
		if (strID != null && !strID.equals("")) {
			strID = strID.substring(1, strID.length() - 1);
			if (strID.contains(",")) {
				String[] temp = strID.split(",");
				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim()));
					} else {
						sb.append(", " + mp.get(temp[i].trim()));
					}
				}
			} else {
				return mp.get(strID);
			}
		} else {
			return null;
		}
		return sb.toString();
	}

	public List<Integer> employeeIdsCount(UtilityFunctions uF, String appId, String empId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		List<Integer> totalList = new ArrayList<Integer>();
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();

			sbQuery.append("select * from appraisal_reviewee_details where appraisal_id = ? and reviewee_id = ?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(appId));
			pst.setInt(2, uF.parseToInt(empId));
			// System.out.println("employeeIdsCount PST Count : " +
			// pst.toString());
			rs = pst.executeQuery();
			List<String> peerIdsList = new ArrayList<String>();
			List<String> supervisorIdsList = new ArrayList<String>();
			List<String> grandSupervisorIdsList = new ArrayList<String>();
			List<String> revieweeIdsList = new ArrayList<String>();
			List<String> subordinateIdsList = new ArrayList<String>();
			List<String> otherPeerIdsList = new ArrayList<String>();
			List<String> hodIdsList = new ArrayList<String>();
			List<String> ceoIdsList = new ArrayList<String>();
			List<String> hrIdsList = new ArrayList<String>();
			List<String> recruiterIdsList = new ArrayList<String>();
			List<String> otherIdsList = new ArrayList<String>();

			// Map<String, String> value = new HashMap<String, String>();

			while (rs.next()) {
				if (rs.getString("peer_ids") != null && !rs.getString("peer_ids").contains("''") && rs.getString("peer_ids").length() > 0) {
					String strPeersIds = rs.getString("peer_ids").substring(1);
					peerIdsList = Arrays.asList(strPeersIds.split(","));
				}

				if (rs.getString("reviewee_id") != null && !rs.getString("reviewee_id").contains("''") && rs.getString("reviewee_id").length() > 0) {
					revieweeIdsList = Arrays.asList(rs.getString("reviewee_id"));
				}

				if (rs.getString("supervisor_ids") != null && !rs.getString("supervisor_ids").contains("''") && rs.getString("supervisor_ids").length() > 0) {
					String strSupervisorIds = rs.getString("supervisor_ids").substring(1);
					supervisorIdsList = Arrays.asList(strSupervisorIds.split(","));
				}

				if (rs.getString("grand_supervisor_ids") != null && !rs.getString("grand_supervisor_ids").contains("''")
						&& rs.getString("grand_supervisor_ids").length() > 0) {
					String strGrandSupervisorIds = rs.getString("grand_supervisor_ids").substring(1);
					grandSupervisorIdsList = Arrays.asList(strGrandSupervisorIds.split(","));
				}

				if (rs.getString("subordinate_ids") != null && !rs.getString("subordinate_ids").contains("''") && rs.getString("subordinate_ids").length() > 0) {
					String strSubordinateIds = rs.getString("subordinate_ids").substring(1);
					subordinateIdsList = Arrays.asList(strSubordinateIds.split(","));
				}

				if (rs.getString("other_peer_ids") != null && !rs.getString("other_peer_ids").contains("''") && rs.getString("other_peer_ids").length() > 0) {
					String strOtherIds = rs.getString("other_peer_ids").substring(1);
					otherPeerIdsList = Arrays.asList(strOtherIds.split(","));
				}

				if (rs.getString("hod_ids") != null && !rs.getString("hod_ids").contains("''") && rs.getString("hod_ids").length() > 0) {
					String strHODIds = rs.getString("hod_ids").substring(1);
					hodIdsList = Arrays.asList(strHODIds.split(","));

				}

				if (rs.getString("ceo_ids") != null && !rs.getString("ceo_ids").contains("''") && rs.getString("ceo_ids").length() > 0) {
					String strCEOIds = rs.getString("ceo_ids").substring(1);
					ceoIdsList = Arrays.asList(strCEOIds.split(","));
				}

				if (rs.getString("hr_ids") != null && !rs.getString("hr_ids").contains("''") && rs.getString("hr_ids").length() > 0) {
					String strHRIds = rs.getString("hr_ids").substring(1);
					hrIdsList = Arrays.asList(strHRIds.split(","));
				}

				if (rs.getString("other_ids") != null && !rs.getString("other_ids").contains("''") && rs.getString("other_ids").length() > 0) {
					String strOtherIds = rs.getString("other_ids").substring(1);
					otherIdsList = Arrays.asList(strOtherIds.split(","));
				}

			}
			rs.close();
			pst.close();
			totalList.add(peerIdsList.size());
			// System.out.println("grandSupervisorIdsList : " +
			// grandSupervisorIdsList.size());
			// System.out.println("revieweeIdsCount : " +
			// revieweeIdsList.size());
			// System.out.println("subordinateIdsCount : " +
			// subordinateIdsList.size());
			// System.out.println("supervisorIdsList : " +
			// supervisorIdsList.size());
			// System.out.println("otherPeerIdsList : " +
			// otherPeerIdsList.size());
			// System.out.println("hodIdsList : " + hodIdsList.size());
			// System.out.println("ceoIdsList : " + ceoIdsList.size());
			// System.out.println("hrIdsList : " + hrIdsList.size());
			// System.out.println("recruiterIdsList : " +
			// recruiterIdsList.size());
			// System.out.println("otherIdsList : " + otherIdsList.size());

			/*
			 * value.put("2", supervisorIdsList.size() + ""); value.put("3",
			 * revieweeIdsList.size() + ""); value.put("4", peerIdsList.size() +
			 * ""); value.put("5", ceoIdsList.size() + ""); value.put("6",
			 * subordinateIdsList.size() + ""); value.put("7", hrIdsList.size()
			 * + ""); value.put("8", grandSupervisorIdsList.size() + "");
			 * value.put("10", otherIdsList.size() + ""); value.put("13",
			 * hodIdsList.size() + ""); value.put("14", otherPeerIdsList.size()
			 * + "");
			 */

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return totalList;
	}

// ===start parvez date: 20-04-2022===
	private void getAppraisalQuestionAnswersExport(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String apprasialName = "";
		List<String> empList = new ArrayList<String>();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);

			Map<String, String> hmorientationMembers = new HashMap<String, String>();
			List alRoles = new ArrayList();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,supervisor_emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id "
					+ " and is_alive= true and emp_per_id >0 order by supervisor_emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			// System.out.println("PST ===>> " + pst);
			Map<String, List<String>> hmHireracyLevelEmpIds = new LinkedHashMap<String, List<String>>();
			// List<String> alHireracyLevels = new ArrayList<String>();
			while (rs.next()) {
				List<String> alInner = hmHireracyLevelEmpIds.get(rs.getString("supervisor_emp_id"));
				if (alInner == null)
					alInner = new ArrayList<String>();
				if (uF.parseToInt(rs.getString("supervisor_emp_id")) == uF.parseToInt(rs.getString("emp_id"))) {
					continue;
				}
				alInner.add(rs.getString("emp_id"));
				hmHireracyLevelEmpIds.put(rs.getString("supervisor_emp_id"), alInner);
			}
			rs.close();
			pst.close();
			
			StringBuilder sbClildEmpIds = null;
			List<String> empIDList = new ArrayList<String>();
			sbClildEmpIds = getChildEmpIds(hmHireracyLevelEmpIds, strSessionEmpId, empIDList, sbClildEmpIds);

			StringBuilder sbEmpId = null;
			
			if (strSessionUserType != null && strSessionUserType.equals(MANAGER)) {
				sbQuery = new StringBuilder();
				sbQuery.append("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id  and appraisal_id=? "
						+ "and (supervisor_ids like '%,"
						+ strSessionEmpId
						+ ",%' or grand_supervisor_ids like '%,"
						+ strSessionEmpId
						+ ",%' or hod_ids like '%," + strSessionEmpId + ",%' ");
				if (sbClildEmpIds != null && !sbClildEmpIds.toString().equals("")) {
					sbQuery.append(" or epd.emp_per_id in (" + sbClildEmpIds.toString() + ") ");
				}
				sbQuery.append(") order by emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					if (sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rs.getString("reviewee_id"));
					} else {
						sbEmpId.append("," + rs.getString("reviewee_id"));
					}
				}
				rs.close();
				pst.close();
			}

			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id = ? ");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				apprasialName = rs.getString("appraisal_name");
				/*String[] userTypeMember = rs.getString("usertype_member").split(",");
				for(int i=0; i<userTypeMember.length; i++){
					alRoles.add(userTypeMember[i]);
				}*/
				if (rs.getString("usertype_member") != null && !rs.getString("usertype_member").equals("")) {
					alRoles = Arrays.asList(rs.getString("usertype_member").split(","));
				}
				
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from orientation_member");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmorientationMembers.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("hmorientationMembers", hmorientationMembers);
			request.setAttribute("hmEmpName", hmEmpName);
			List<String> sectionIdsList = new ArrayList<String>();
			pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id = ? ");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				sectionIdsList.add(rs.getString("main_level_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("sectionIdsList", sectionIdsList);

			pst = con.prepareStatement("select distinct aqd.appraisal_level_id,main_level_id,other_id,scorecard_id,app_system_type from "
					+ "appraisal_question_details aqd, appraisal_level_details ald where aqd.appraisal_level_id = ald.appraisal_level_id "
					+ "and aqd.appraisal_id = ? order by aqd.appraisal_level_id");

			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, String> hmAppSystemType = new HashMap<String, String>();

			Map<String, List<String>> hmSubsectionIds = new LinkedHashMap<String, List<String>>();
			while (rs.next()) {
				hmAppSystemType.put(rs.getString("appraisal_level_id"), rs.getString("app_system_type"));

				List<String> alLevelScore = hmSubsectionIds.get(rs.getString("main_level_id") + "SCR");
				if (alLevelScore == null)
					alLevelScore = new ArrayList<String>();

				List<String> alLevelOther = hmSubsectionIds.get(rs.getString("main_level_id") + "OTHR");
				if (alLevelOther == null)
					alLevelOther = new ArrayList<String>();

				List<String> alLevelGoalTargetKRA = hmSubsectionIds.get(rs.getString("main_level_id") + "GTK");
				if (alLevelGoalTargetKRA == null)
					alLevelGoalTargetKRA = new ArrayList<String>();

				if (uF.parseToInt(rs.getString("other_id")) > 0 && !alLevelOther.contains(rs.getString("appraisal_level_id"))) {
					alLevelOther.add(rs.getString("appraisal_level_id"));
				} else if (uF.parseToInt(rs.getString("scorecard_id")) > 0 && !alLevelScore.contains(rs.getString("appraisal_level_id"))) {
					alLevelScore.add(rs.getString("appraisal_level_id"));
				} else if (!alLevelGoalTargetKRA.contains(rs.getString("appraisal_level_id"))) {
					alLevelGoalTargetKRA.add(rs.getString("appraisal_level_id"));
				} else {
				}
				hmSubsectionIds.put(rs.getString("main_level_id") + "SCR", alLevelScore);
				hmSubsectionIds.put(rs.getString("main_level_id") + "OTHR", alLevelOther);
				hmSubsectionIds.put(rs.getString("main_level_id") + "GTK", alLevelGoalTargetKRA);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmSubsectionIds", hmSubsectionIds);
			request.setAttribute("hmAppSystemType", hmAppSystemType);

			pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id = ?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, String> hmSectionDetails = new HashMap<String, String>();
			while (rs.next()) {
				hmSectionDetails.put(rs.getString("main_level_id"), rs.getString("level_title"));
				hmSectionDetails.put(rs.getString("main_level_id") + "_SD", rs.getString("short_description"));
				hmSectionDetails.put(rs.getString("main_level_id") + "_LD", rs.getString("long_description"));
			}
			rs.close();
			pst.close();

			request.setAttribute("hmSectionDetails", hmSectionDetails);

			Map hmScoreQuestionsMap = new HashMap();
			Map hmOtherQuestionsMap = new HashMap();
			Map hmGoalTargetKraQuestionsMap = new HashMap();
			Map hmLevelScoreMap = new HashMap();

			Map hmQuestionMarks = new HashMap();
			Map hmQuestionWeightage = new HashMap();

//			List alRoles = new ArrayList();
//			List rolesUserIds = new ArrayList();
			Map<String, List<String>> hmRolesUserIds = new HashMap<String, List<String>>();
			Map<String, List<String>> hmRoles = new HashMap<String, List<String>>();
			Map<String, List<String>> hmOuterpeerAppraisalDetails = new HashMap<String, List<String>>();

			Map hmQuestionAnswerReport = new HashMap();
			Map hmQuestionRemak = new HashMap();

			List<String> alKRAIds = (List<String>) request.getAttribute("alKRAIds");
			List<String> alGoarTargetIds = (List<String>) request.getAttribute("alGoarTargetIds");

			Map<String, String> hmSectionComment = new HashMap<String, String>();
			Map<String, String> hmQuestionWiseGoalTargetKra = new HashMap<String, String>();
			
			List alQuestion = new ArrayList();
			pst = con.prepareStatement("select * from appraisal_question_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getInt("app_system_type") == 4 && alKRAIds != null && rs.getString("kra_id") != null && !alKRAIds.contains(rs.getString("kra_id"))) {
					continue;
				}
				if ((rs.getInt("app_system_type") == 3 || rs.getInt("app_system_type") == 5) && alGoarTargetIds != null
						&& rs.getString("goal_kra_target_id") != null && !alGoarTargetIds.contains(rs.getString("goal_kra_target_id"))) {
					continue;
				}
				if (uF.parseToInt(rs.getString("other_id")) > 0) {
					alQuestion = (List) hmOtherQuestionsMap.get(rs.getString("appraisal_level_id"));
					if (alQuestion == null)
						alQuestion = new ArrayList();
					if (!alQuestion.contains(rs.getString("question_id"))) {
						alQuestion.add(rs.getString("question_id"));
					}
					hmOtherQuestionsMap.put(rs.getString("appraisal_level_id"), alQuestion);
				} else if (uF.parseToInt(rs.getString("scorecard_id")) > 0) {
					alQuestion = (List) hmScoreQuestionsMap.get(rs.getString("scorecard_id"));
					if (alQuestion == null)
						alQuestion = new ArrayList();
					if (!alQuestion.contains(rs.getString("question_id"))) {
						alQuestion.add(rs.getString("question_id"));
					}
					hmScoreQuestionsMap.put(rs.getString("scorecard_id"), alQuestion);

					List alScore = (List) hmLevelScoreMap.get(rs.getString("appraisal_level_id"));
					if (alScore == null)
						alScore = new ArrayList();
					if (!alScore.contains(rs.getString("scorecard_id"))) {
						alScore.add(rs.getString("scorecard_id"));
					}
					hmLevelScoreMap.put(rs.getString("appraisal_level_id"), alScore);
				} else {
					alQuestion = (List) hmGoalTargetKraQuestionsMap.get(rs.getString("appraisal_level_id"));
					if (alQuestion == null)
						alQuestion = new ArrayList();
					if (!alQuestion.contains(rs.getString("question_id"))) {
						alQuestion.add(rs.getString("question_id"));
					}
					hmGoalTargetKraQuestionsMap.put(rs.getString("appraisal_level_id"), alQuestion);
					hmQuestionWiseGoalTargetKra.put(rs.getString("question_id"), rs.getString("kra_id")+"_"+rs.getString("goal_kra_target_id"));
				}
				hmQuestionWeightage.put(rs.getString("question_id"), rs.getString("weightage"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from question_bank");
			rs = pst.executeQuery();
			Map<String, String> hmQueAnsType = new HashMap<String, String>();
			while (rs.next()) {
				hmQueAnsType.put(rs.getString("question_bank_id"), rs.getString("question_type"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from appraisal_answer_type_sub");
			rs = pst.executeQuery();
			Map<String, String> hmAnsTypeAnswer = new HashMap<String, String>();
			while (rs.next()) {
				hmAnsTypeAnswer.put(rs.getString("answer_type_id") + "_" + rs.getString("score"), rs.getString("score_label"));
			}
			rs.close();
			pst.close();
			
			String avgMarks = "", avgWeightage = "";
			String avgMarksReviewer = "";
			String allAns = "", allRemark = "";
			int cnt = 0;
			String remark = "", ans = "";
			
			sbQuery = new StringBuilder();
	//===start parvez date: 06-07-2022===		
			/*sbQuery.append("select *,(marks/weightcnt) as avgmarks,(reviewer_marks/weightcnt) as avgreviewer_marks FROM "
					+ "(select user_type_id,user_id,question_id,sum(marks) as marks,sum(reviewer_marks) as reviewer_marks,sum(weightage) as weightage,"
					+ "(sum(weightage)/COUNT(weightage)) as avgweightage,COUNT(weightage) as weightcnt from appraisal_question_answer where "
					+ "appraisal_id=? ");
			if(sbEmpId != null){
				sbQuery.append(" and emp_id in(" + sbEmpId.toString() + ") ");
			}
			sbQuery.append("and appraisal_freq_id=? group by user_type_id,user_id,question_id order by question_id)  as a");*/
			sbQuery.append("select *,(marks/weightcnt) as avgmarks,(reviewer_marks/weightcnt) as avgreviewer_marks FROM "
					+ "(select user_type_id,user_id,emp_id,question_id,sum(marks) as marks,sum(reviewer_marks) as reviewer_marks,sum(weightage) as weightage,"
					+ "(sum(weightage)/COUNT(weightage)) as avgweightage,COUNT(weightage) as weightcnt from appraisal_question_answer where "
					+ "appraisal_id=? ");
			if(sbEmpId != null){
				sbQuery.append(" and emp_id in(" + sbEmpId.toString() + ") ");
			}
			sbQuery.append("and appraisal_freq_id=? group by user_type_id,user_id,emp_id,question_id order by question_id)  as a");
	//===end parvez date: 06-07-2022===		
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			
			rs = pst.executeQuery();
//			System.out.println("ApSt/4370---pst marks == " + pst);
			while (rs.next()) {
				if(uF.parseToInt(rs.getString("user_type_id")) == 4 || uF.parseToInt(rs.getString("user_type_id")) == 10){
					List<String> peerAppraisalDetails = new ArrayList<String>();
					avgMarks = uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("avgmarks"));
					avgMarksReviewer = uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("avgreviewer_marks"));
					avgWeightage = uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("avgweightage"));

					/*List<String> alRoles = hmRoles.get(rs.getString("emp_id"));
					if(alRoles == null)
						alRoles = new ArrayList<String>();
					
					if (!alRoles.contains(rs.getString("user_type_id"))) {
						alRoles.add(rs.getString("user_type_id"));
					}*/

					List<String> rolesUserIds = hmRolesUserIds.get(rs.getString("emp_id"));
					if(rolesUserIds == null)
						rolesUserIds = new ArrayList<String>();
					
					if (!rolesUserIds.contains(rs.getString("user_id"))) {
						rolesUserIds.add(rs.getString("user_id"));
					}
					
					peerAppraisalDetails.add(avgMarks);
					peerAppraisalDetails.add(avgWeightage);
					peerAppraisalDetails.add(avgMarksReviewer);
//					hmOuterpeerAppraisalDetails.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id")+"_"+rs.getString("emp_id"), peerAppraisalDetails);
					hmOuterpeerAppraisalDetails.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") +"_"+ rs.getString("emp_id"), peerAppraisalDetails);
//					hmRoles.put(rs.getString("emp_id"), alRoles);
					hmRolesUserIds.put(rs.getString("emp_id"), rolesUserIds);
				}
				
			}
			rs.close();
			pst.close();

			String que_id = "";
			sbQuery = new StringBuilder();
			sbQuery.append("select aqa.*,score_calculation_basis from appraisal_question_answer aqa, appraisal_question_details aqd where aqa.appraisal_id = ? ");
			if(sbEmpId != null){
				sbQuery.append("and emp_id in(" + sbEmpId.toString() + ") ");
			}
			sbQuery.append("and aqa.appraisal_freq_id = ? and aqa.appraisal_question_details_id=aqd.appraisal_question_details_id order by question_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
//			System.out.println("ApSt/4416---pst="+pst);
			rs = pst.executeQuery();
			Map<String, String> hmOuterpeerAnsDetailsreport = new HashMap<String, String>();
			while (rs.next()) {
				
				if(uF.parseToInt(rs.getString("user_type_id")) == 4 || uF.parseToInt(rs.getString("user_type_id")) == 10){
					
					String ansType = hmQueAnsType.get(rs.getString("question_id"));
					String answer = null;
					if (uF.parseToInt(ansType) == 4) {
						answer = hmAnsTypeAnswer.get(ansType + "_" + rs.getString("answer")) + " (" + rs.getString("answer") + ")";
					} else if (uF.parseToInt(ansType) == 5 || uF.parseToInt(ansType) == 6) {
						String getans = rs.getString("answer").length() > 0 ? rs.getString("answer").substring(0, rs.getString("answer").length() - 1) : "";
						answer = hmAnsTypeAnswer.get(ansType + "_" + getans);
					} else if (uF.parseToInt(ansType) == 3) {
						String getans = rs.getString("marks");
						answer = getans;
					} else if (uF.parseToInt(ansType) == 11) {
					//===start parvez date: 17-03-2023===	
//						double getans = (rs.getDouble("marks") * 5) / rs.getDouble("weightage");
//						answer = uF.formatIntoOneDecimal(getans) + " / 5";
						double getans = 0;
						if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
							getans = (rs.getDouble("marks") * 10) / rs.getDouble("weightage");
							answer = uF.formatIntoOneDecimal(getans) + " / 10";
						} else{
							getans = (rs.getDouble("marks") * 5) / rs.getDouble("weightage");
							answer = uF.formatIntoOneDecimal(getans) + " / 5";
						}
					//===end parvez date: 17-03-2023===	
					} else {
						answer = rs.getString("answer");
					}
//					String QuestionAns = hmOuterpeerAnsDetailsreport.get(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id") + "_" + rs.getString("emp_id"));
					String QuestionAns = hmOuterpeerAnsDetailsreport.get(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("emp_id"));
					if (QuestionAns == null) {
						QuestionAns = uF.showData(answer, "No answer") + "-by " + hmEmpName.get(rs.getString("user_id"))
								+ uF.showData(" Comment-" + rs.getString("answers_comment"), "No comment");
					} else {
						
						QuestionAns += uF.showData(answer, "No answer") + "by " + hmEmpName.get(rs.getString("user_id"))
								+ uF.showData(" Comment-" + rs.getString("answers_comment"), "No comment");
					}

					if (rs.getString("remark") != null) {
						remark += rs.getString("remark");
					}
//					hmOuterpeerAnsDetailsreport.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id")+ "_" + rs.getString("emp_id"), QuestionAns);
					hmOuterpeerAnsDetailsreport.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("emp_id"), QuestionAns);
					
//					hmSectionComment.put(rs.getString("section_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id")+ "_" + rs.getString("emp_id"), rs.getString("section_comment"));
					hmSectionComment.put(rs.getString("section_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("emp_id"), rs.getString("section_comment"));
					
				} else{
					
					String ansType = hmQueAnsType.get(rs.getString("question_id"));
					String answer = null, answerWithUser = null;
					if (uF.parseToInt(ansType) == 4) {
						answer = hmAnsTypeAnswer.get(ansType + "_" + rs.getString("answer")) + " (" + rs.getString("answer") + ")";
					} else if (uF.parseToInt(ansType) == 5 || uF.parseToInt(ansType) == 6) {
						String getans = rs.getString("answer").length() > 0 ? rs.getString("answer").substring(0, rs.getString("answer").length() - 1) : "";
						answer = hmAnsTypeAnswer.get(ansType + "_" + getans);
					} else if (uF.parseToInt(ansType) == 3) {
						String getans = rs.getString("marks");
						answer = getans;
					} else if (uF.parseToInt(ansType) == 11) {
					//===start parvez date: 17-03-2023===	
//						double getans = (rs.getDouble("marks") * 5) / rs.getDouble("weightage");
//						answer = uF.formatIntoOneDecimal(getans) + " / 5";
						double getans = 0;
						if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
							getans = (rs.getDouble("marks") * 10) / rs.getDouble("weightage");
							answer = uF.formatIntoOneDecimal(getans) + " / 10";
						} else{
							getans = (rs.getDouble("marks") * 5) / rs.getDouble("weightage");
							answer = uF.formatIntoOneDecimal(getans) + " / 5";
						}
					//===end parvez date: 17-03-2023===	
					} else {
						answer = rs.getString("answer");
					}

				//===start parvez date: 02-03-2023===	
//					hmQuestionMarks.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id")+ "_" + rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("marks")));
//					hmQuestionMarks.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("marks")));
					if(rs.getString("score_calculation_basis")!=null && uF.parseToBoolean(rs.getString("score_calculation_basis"))){
						hmQuestionMarks.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma((rs.getDouble("marks") * 5) / rs.getDouble("weightage")));
					} else{
						hmQuestionMarks.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("marks")));
					}
				//===end parvez date: 02-03-2023===	
					
					hmQuestionWeightage.put(rs.getString("question_id"), rs.getString("weightage"));

					if (rs.getString("answer") != null || rs.getString("marks") != null) {
						
						answerWithUser = uF.showData(answer, "No answer") + "- by " + hmEmpName.get(rs.getString("user_id"))
								+ uF.showData(" Comment-" + rs.getString("answers_comment"), "No comment");
//						hmQuestionAnswerReport.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id")+"_"+rs.getString("emp_id"), answerWithUser);
						hmQuestionAnswerReport.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") +"_" + rs.getString("emp_id"), answerWithUser);
					}
					if (rs.getString("remark") != null) {
//						hmQuestionRemak.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id")+"_"+rs.getString("emp_id"), rs.getString("remark"));
						hmQuestionRemak.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") +"_"+ rs.getString("emp_id"), rs.getString("remark"));
					}
					
					/*List<String> alRoles = hmRoles.get(rs.getString("emp_id"));
					if(alRoles == null)
						alRoles = new ArrayList<String>();
					
					if (!alRoles.contains(rs.getString("user_type_id"))) {
						alRoles.add(rs.getString("user_type_id"));
					}*/

					List<String> rolesUserIds = hmRolesUserIds.get(rs.getString("emp_id"));
					if(rolesUserIds == null)
						rolesUserIds = new ArrayList<String>();
					if (!rolesUserIds.contains(rs.getString("user_id"))) {
						rolesUserIds.add(rs.getString("user_id"));
					}
					
//					hmSectionComment.put(rs.getString("section_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id")+ "_" + rs.getString("emp_id"), rs.getString("section_comment"));
					hmSectionComment.put(rs.getString("section_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("emp_id"), rs.getString("section_comment"));
//					hmRoles.put(rs.getString("emp_id"), alRoles);
					hmRolesUserIds.put(rs.getString("emp_id"), rolesUserIds);
				}
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmOuterpeerAnsDetailsreport", hmOuterpeerAnsDetailsreport);
			
			pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id = ?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, String> hmLevel = new HashMap<String, String>();
			while (rs.next()) {
				hmLevel.put(rs.getString("appraisal_level_id"), rs.getString("level_title"));
				hmLevel.put(rs.getString("appraisal_level_id") + "_SD", rs.getString("short_description"));
				hmLevel.put(rs.getString("appraisal_level_id") + "_LD", rs.getString("long_description"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from appraisal_scorecard_details where appraisal_id = ?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, String> hmScoreCard = new HashMap<String, String>();
			while (rs.next()) {
				hmScoreCard.put(rs.getString("scorecard_id"), rs.getString("scorecard_section_name"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from question_bank");
			rs = pst.executeQuery();
			Map<String, String> hmQuestions = new HashMap<String, String>();
			Map<String, List<String>> hmOptions = new HashMap<String, List<String>>();

			while (rs.next()) {
				hmQuestions.put(rs.getString("question_bank_id"), rs.getString("question_text"));

				List alOptions = new ArrayList();
				alOptions.add(rs.getString("option_a"));// 0
				alOptions.add(rs.getString("option_b"));// 1
				alOptions.add(rs.getString("option_c"));// 2
				alOptions.add(rs.getString("option_d"));// 3
				alOptions.add(rs.getString("question_type"));// 4

				hmOptions.put(rs.getString("question_bank_id"), alOptions);
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select reviewee_id from appraisal_reviewee_details ard,employee_personal_details epd where epd.emp_per_id=ard.reviewee_id and is_alive= true and appraisal_id=? ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				empList.add(rs.getString("reviewee_id"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmKraDetails = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select goal_kra_id, goal_id, emp_ids from goal_kras");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				hmKraDetails.put(rs.getString("goal_kra_id")+"_"+rs.getString("goal_id"), rs.getString("emp_ids"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmStrengthImprovements = new HashMap<String, String>();
			pst = con.prepareStatement("select * from reviewee_strength_improvements where review_id=? and review_freq_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmStrengthImprovements.put(rs.getString("emp_id")+"_"+rs.getString("user_type_id") + "_STRENGTH", rs.getString("areas_of_strength"));
				hmStrengthImprovements.put(rs.getString("emp_id")+"_"+rs.getString("user_type_id") + "_IMPROVEMENT", rs.getString("areas_of_improvement"));
			}
			rs.close();
			pst.close();

			request.setAttribute("hmLevel", hmLevel);
			request.setAttribute("hmScoreCard", hmScoreCard);
			request.setAttribute("hmQuestions", hmQuestions);
			request.setAttribute("hmOptions", hmOptions);

			request.setAttribute("hmQuestionMarks", hmQuestionMarks);
			request.setAttribute("hmQuestionWeightage", hmQuestionWeightage);
			request.setAttribute("hmQuestionAnswerReport", hmQuestionAnswerReport);
			request.setAttribute("hmQuestionRemak", hmQuestionRemak);
			request.setAttribute("alRoles", alRoles);
//			request.setAttribute("rolesUserIds", rolesUserIds);
			request.setAttribute("hmRoles", hmRoles);
			request.setAttribute("hmRolesUserIds", hmRolesUserIds);
			request.setAttribute("hmOuterpeerAppraisalDetails", hmOuterpeerAppraisalDetails);

			request.setAttribute("hmScoreQuestionsMap", hmScoreQuestionsMap);
			request.setAttribute("hmOtherQuestionsMap", hmOtherQuestionsMap);
			request.setAttribute("hmLevelScoreMap", hmLevelScoreMap);
			request.setAttribute("hmGoalTargetKraQuestionsMap", hmGoalTargetKraQuestionsMap);
			
			request.setAttribute("hmQuestionWiseGoalTargetKra", hmQuestionWiseGoalTargetKra);
			request.setAttribute("hmKraDetails", hmKraDetails);
			request.setAttribute("hmSectionComment", hmSectionComment);
			request.setAttribute("hmStrengthImprovements", hmStrengthImprovements);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();

		Map<String, String> hmorientationMembers = (Map) request.getAttribute("hmorientationMembers");
		Map hmScoreQuestionsMap = (Map) request.getAttribute("hmScoreQuestionsMap");
		Map hmOtherQuestionsMap = (Map) request.getAttribute("hmOtherQuestionsMap");
		Map hmGoalTargetKraQuestionsMap = (Map) request.getAttribute("hmGoalTargetKraQuestionsMap");
		Map hmLevelScoreMap = (Map) request.getAttribute("hmLevelScoreMap");
		Map hmLevel = (Map) request.getAttribute("hmLevel");
		Map hmQuestions = (Map) request.getAttribute("hmQuestions");
		Map hmScoreCard = (Map) request.getAttribute("hmScoreCard");
		Map hmOptions = (Map) request.getAttribute("hmOptions");
		String memberId = (String) request.getAttribute("memberId");
		Map hmQuestionMarks = (Map) request.getAttribute("hmQuestionMarks");
		Map hmQuestionWeightage = (Map) request.getAttribute("hmQuestionWeightage");
		Map hmQuestionAnswer = (Map) request.getAttribute("hmQuestionAnswerReport");
//		System.out.println("hmQuestionAnswer="+hmQuestionAnswer);
		Map hmQuestionRemak = (Map) request.getAttribute("hmQuestionRemak");
		List alRoles = (List) request.getAttribute("alRoles");
//		List rolesUserIds = (List) request.getAttribute("rolesUserIds");
		
		Map hmRoles = (Map) request.getAttribute("hmRoles");
		Map hmRolesUserIds = (Map) request.getAttribute("hmRolesUserIds");
		
		Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
		if (hmEmpName == null)
			hmEmpName = new HashMap<String, String>();
		
		Map<String, List<String>> hmOuterpeerAppraisalDetails = (Map<String, List<String>>) request.getAttribute("hmOuterpeerAppraisalDetails");
		Map<String, String> hmOuterpeerAnsDetails = (Map<String, String>) request.getAttribute("hmOuterpeerAnsDetailsreport");
		
		List<String> sectionIdsList = (List<String>) request.getAttribute("sectionIdsList");
		if (sectionIdsList == null)
			sectionIdsList = new ArrayList<String>();
		
		Map<String, List<String>> hmSubsectionIds = (Map<String, List<String>>) request.getAttribute("hmSubsectionIds");
		if (hmSubsectionIds == null)
			hmSubsectionIds = new LinkedHashMap<String, List<String>>();
		
		Map<String, String> hmSectionDetails = (Map<String, String>) request.getAttribute("hmSectionDetails");
		if (hmSectionDetails == null)
			hmSectionDetails = new HashMap<String, String>();
		
		Map<String,String> hmQuestionWiseGoalTargetKra = (Map<String, String>)request.getAttribute("hmQuestionWiseGoalTargetKra");
		if(hmQuestionWiseGoalTargetKra == null)
			hmQuestionWiseGoalTargetKra = new HashMap<String, String>();
		
		Map<String,String> hmKraDetails = (Map<String, String>)request.getAttribute("hmKraDetails");
		if(hmKraDetails == null)
			hmKraDetails = new HashMap<String, String>();
		
		Map<String,String> hmSectionComment = (Map<String, String>)request.getAttribute("hmSectionComment");
		if(hmSectionComment == null)
			hmSectionComment = new HashMap<String, String>();
		
		Map<String, String> hmStrengthImprovements = (Map<String, String>) request.getAttribute("hmStrengthImprovements");
		if (hmStrengthImprovements == null)
			hmStrengthImprovements = new HashMap<String, String>();
		
		alInnerExport.add(new DataStyle(apprasialName, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		reportListExport.add(alInnerExport);
		
		for (int e = 0; empList != null && e < empList.size(); e++) {
			
			alInnerExport = new ArrayList<DataStyle>();
//			List alRoles = (List) hmRoles.get(empList.get(e));
//			List rolesUserIds = (List) hmRolesUserIds.get(empList.get(e));
			
//			if (rolesUserIds != null && !rolesUserIds.isEmpty() && rolesUserIds.size() > 0) {
//				for (int m = 0; rolesUserIds != null && m < rolesUserIds.size(); m++) {
//					if(uF.parseToInt(empList.get(e)) == uF.parseToInt(rolesUserIds.get(m)+"")){
					
//						alInnerExport.add(new DataStyle(hmEmpName.get(rolesUserIds.get(m)) + " (" + hmorientationMembers.get(memberId) + ")", Element.ALIGN_CENTER,
//							"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
//						reportListExport.add(alInnerExport);
						for (int a = 0; sectionIdsList != null && !sectionIdsList.isEmpty() && a < sectionIdsList.size(); a++) {
							List<String> alLevelScore = hmSubsectionIds.get(sectionIdsList.get(a) + "SCR");
							int cnt = 0;
							alInnerExport = new ArrayList<DataStyle>();
							if(a == 0){
								alInnerExport.add(new DataStyle(hmEmpName.get(empList.get(e)), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							}else{
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							}
							alInnerExport.add(new DataStyle(a + 1 + ") " + hmSectionDetails.get(sectionIdsList.get(a)), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							reportListExport.add(alInnerExport);
							
							for (int i = 0; alLevelScore != null && i < alLevelScore.size(); i++) {
								cnt++;
								List alScore = (List) hmLevelScoreMap.get((String) alLevelScore.get(i));
								if (alScore != null && !alScore.isEmpty()) {
	
									alInnerExport = new ArrayList<DataStyle>();
									alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle(a + 1 + ") " + hmSectionDetails.get(sectionIdsList.get(a)), Element.ALIGN_CENTER, "NEW_ROMAN", 6,
											"0", "0", BaseColor.LIGHT_GRAY));
									reportListExport.add(alInnerExport);
									
									alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle(uF.showData(hmSectionDetails.get(sectionIdsList.get(a) + "_SD"), ""), Element.ALIGN_CENTER,
											"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									
									alInnerExport = new ArrayList<DataStyle>();
									alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle(uF.showData(hmSectionDetails.get(sectionIdsList.get(a) + "_LD"), ""), Element.ALIGN_CENTER,
											"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									reportListExport.add(alInnerExport);
									
									alInnerExport = new ArrayList<DataStyle>();
									alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle("Competencies", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle("Question", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle("Weightage %", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									
									for (int r = 0; alRoles != null && r < alRoles.size(); r++) {
	//									System.out.print("Marks Role" + hmorientationMembers.get((String) alRoles.get(r)));
										alInnerExport.add(new DataStyle("Marks Role" + hmorientationMembers.get((String) alRoles.get(r)), Element.ALIGN_CENTER,
												"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
//										alInnerExport.add(new DataStyle("Weightage %", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									}
//									alInnerExport.add(new DataStyle("Weightage %", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									reportListExport.add(alInnerExport);
									for (int s = 0; alScore != null && s < alScore.size(); s++) {
										// single row
										List alQuestions = (List) hmScoreQuestionsMap.get(alScore.get(s));
										for (int q = 0; alQuestions != null && q < alQuestions.size(); q++) {
											List alOptions = (List) hmOptions.get((String) alQuestions.get(q));
											alInnerExport = new ArrayList<DataStyle>();
											alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
											alInnerExport.add(new DataStyle(hmScoreCard.get((String) alScore.get(s)).toString(), Element.ALIGN_CENTER, "NEW_ROMAN", 6,
													"0", "0", BaseColor.LIGHT_GRAY));
	
											alInnerExport.add(new DataStyle(q + 1 + ")" + hmQuestions.get((String) alQuestions.get(q)), Element.ALIGN_CENTER,
													"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
	
											for (int rr = 0; alRoles != null && rr < alRoles.size(); rr++) {
												if (uF.parseToInt(alRoles.get(rr).toString()) == 4 || uF.parseToInt(alRoles.get(rr).toString()) == 10) {
//													List<String> innList = hmOuterpeerAppraisalDetails.get(alQuestions.get(q) + "_" + alRoles.get(rr) + "_" + rolesUserIds.get(m) + "_" + empList.get(e));
													List<String> innList = hmOuterpeerAppraisalDetails.get(alQuestions.get(q) + "_" + alRoles.get(rr) + "_" + empList.get(e));
													if (innList == null)
														innList = new ArrayList<String>();
													if (innList != null && !innList.isEmpty()) {
	
														alInnerExport.add(new DataStyle(uF.showData((String) innList.get(0), "Not Rated"), Element.ALIGN_CENTER,
																"NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
														alInnerExport.add(new DataStyle(uF.showData((String) innList.get(1), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6,
																"0", "0", BaseColor.WHITE));
													} else {
														alInnerExport.add(new DataStyle("Not Rated", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
														 
													}
												} else {
//													alInnerExport.add(new DataStyle(uF.showData(
//															(String) hmQuestionMarks.get((String) alQuestions.get(q) + "_" + alRoles.get(rr) + "_"
//																	+ rolesUserIds.get(m) + "_" + empList.get(e)), "Not Rated"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
													alInnerExport.add(new DataStyle(uF.showData(
															(String) hmQuestionMarks.get((String) alQuestions.get(q) + "_" + alRoles.get(rr) + "_" + empList.get(e)), "Not Rated"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
													
													alInnerExport.add(new DataStyle("" + hmQuestionWeightage.get((String) alQuestions.get(q)), Element.ALIGN_CENTER,
															"NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
												}
											}
											reportListExport.add(alInnerExport);
											
											for (int r = 0; alRoles != null && r < alRoles.size(); r++) {
												StringBuilder sb = new StringBuilder();
												alInnerExport = new ArrayList<DataStyle>();
												alInnerExport.add(new DataStyle(" ", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
												alInnerExport.add(new DataStyle(" ", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
												if (alOptions != null) {
		
													int nOptionType = uF.parseToInt((String) alOptions.get(4));
													switch (nOptionType) {
														case 1 :
															sb.append(" " + "a)" + (String) alOptions.get(0));
															sb.append(" " + "b)" + (String) alOptions.get(1));
															sb.append(" " + "c)" + (String) alOptions.get(2));
															sb.append(" " + "d)" + (String) alOptions.get(3));
															break;
														case 2 :
															sb.append(" " + "a)" + (String) alOptions.get(0));
															sb.append(" " + "b)" + (String) alOptions.get(1));
															sb.append(" " + "c)" + (String) alOptions.get(2));
															sb.append(" " + "d)" + (String) alOptions.get(3));
															break;
														case 3 :
															break;
														case 4 :
															break;
														case 5 :
															sb.append(" " + "a)" + (String) alOptions.get(0));
															sb.append(" " + "b)" + (String) alOptions.get(1));
															break;
		
														case 6 :
															sb.append(" " + "a)" + (String) alOptions.get(0));
															sb.append(" " + "b)" + (String) alOptions.get(1));
															break;
														case 7 :
															break;
														case 8 :
															sb.append(" " + "a)" + (String) alOptions.get(0));
															sb.append(" " + "b)" + (String) alOptions.get(1));
															sb.append(" " + "c)" + (String) alOptions.get(2));
															sb.append(" " + "d)" + (String) alOptions.get(3));
															
															break;
		
														case 9 :
															sb.append(" " + "a)" + (String) alOptions.get(0));
															sb.append(" " + "b)" + (String) alOptions.get(1));
															sb.append(" " + "c)" + (String) alOptions.get(2));
															sb.append(" " + "d)" + (String) alOptions.get(3));
															
															break;
		
														case 10 :
															break;
		
														case 11 :
															break;
														case 12 :
															break;
													}
												}
		
	//											for (int r = 0; alRoles != null && r < alRoles.size(); r++) {
													if (r == 0) {
		
//														sb = sb.append(" Answer/Comments:");
														alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
														
													}
													sb = sb.append(" Answer/Comments:");
													if (uF.parseToInt(alRoles.get(0).toString()) == 4 || uF.parseToInt(alRoles.get(0).toString()) == 10) {
	//													String queAns = hmOuterpeerAnsDetails.get(alQuestions.get(q) + "_" + alRoles.get(r) + "_" + rolesUserIds.get(m) + "_" + empList.get(e));
														String queAns = hmOuterpeerAnsDetails.get(alQuestions.get(q) + "_" + alRoles.get(r) + "_" + empList.get(e));
														
														alInnerExport.add(new DataStyle(uF.showData(queAns, ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",
																BaseColor.WHITE));
		
													} else {
	//													if (hmQuestionAnswer.containsKey((String) alQuestions.get(q) + "_" + alRoles.get(r) + "_" + rolesUserIds.get(m) + "_" + empList.get(e))) {
														if (hmQuestionAnswer.containsKey((String) alQuestions.get(q) + "_" + alRoles.get(r) + "_" + empList.get(e))) {
	//														String strAns = ((String) hmQuestionAnswer.get((String) alQuestions.get(q) + "_" + alRoles.get(r) + "_" + rolesUserIds.get(m) + "_" + empList.get(e)));
															String strAns = ((String) hmQuestionAnswer.get((String) alQuestions.get(q) + "_" + alRoles.get(r) + "_" + empList.get(e)));
															if (strAns != null) {
																strAns = strAns.replace(":_:", "<br/>");
																
																sb.append(uF.showData(strAns, ""));
																
																alInnerExport.add(new DataStyle(uF.showData(sb.toString(), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
//																reportListExport.add(alInnerExport);
															}
														}
													}
											}
											reportListExport.add(alInnerExport);
	
										}
	
										// single row end
									}
									
									alInnerExport = new ArrayList<DataStyle>();
									alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
									alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
									alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
									for (int r1 = 0; alRoles != null && r1 < alRoles.size(); r1++) {
										StringBuilder strCSb = new StringBuilder();
										strCSb.append("Section Comment: ");
										strCSb.append(uF.showData((String) hmSectionComment.get(sectionIdsList.get(a) + "_" + alRoles.get(r1) + "_" + empList.get(e) ), "") + "");
										
										alInnerExport.add(new DataStyle(uF.showData(strCSb.toString(), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
									}
									reportListExport.add(alInnerExport);
								}
							}
	
							// ---------------
							List<String> alLevelOther = hmSubsectionIds.get(sectionIdsList.get(a) + "OTHR");
							for (int i = 0; alLevelOther != null && i < alLevelOther.size(); i++) {
								cnt++;
								List alQuestions = (List) hmOtherQuestionsMap.get((String) alLevelOther.get(i));
								alInnerExport = new ArrayList<DataStyle>();
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle(a + 1 + "." + cnt + " " + hmLevel.get((String) alLevelOther.get(i)), Element.ALIGN_CENTER, "NEW_ROMAN",
										6, "0", "0", BaseColor.WHITE));
								reportListExport.add(alInnerExport);
								
								alInnerExport = new ArrayList<DataStyle>();
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle(uF.showData((String) hmLevel.get((String) alLevelOther.get(i) + "_SD"), ""), Element.ALIGN_CENTER,
										"NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
								reportListExport.add(alInnerExport);
								
								alInnerExport = new ArrayList<DataStyle>();
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle(uF.showData((String) hmLevel.get((String) alLevelOther.get(i) + "_LD"), ""), Element.ALIGN_CENTER,
										"NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
								reportListExport.add(alInnerExport);
								
								alInnerExport = new ArrayList<DataStyle>();
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle("Question", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
								alInnerExport.add(new DataStyle("Weightage %", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
	
								for (int r = 0; alRoles != null && r < alRoles.size(); r++) {
									
									alInnerExport.add(new DataStyle("Marks Role:" + hmorientationMembers.get((String) alRoles.get(r)), Element.ALIGN_CENTER,
											"NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
//									alInnerExport.add(new DataStyle("Weightage %", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
								}
								
								
								reportListExport.add(alInnerExport);
								
								for (int q = 0; alQuestions != null && q < alQuestions.size(); q++) {
									
									alInnerExport = new ArrayList<DataStyle>();
									alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle(q + 1 + ")" + hmQuestions.get((String) alQuestions.get(q)), Element.ALIGN_CENTER, "NEW_ROMAN", 6,
											"0", "0", BaseColor.WHITE));
									for (int ii = 0; alRoles != null && ii < alRoles.size(); ii++) {
										if (uF.parseToInt(alRoles.get(0).toString()) == 4 || uF.parseToInt(alRoles.get(0).toString()) == 10) {
//											List<String> innList = hmOuterpeerAppraisalDetails.get(alQuestions.get(q) + "_" + alRoles.get(ii) + "_" + rolesUserIds.get(m) + "_" + empList.get(e));
											List<String> innList = hmOuterpeerAppraisalDetails.get(alQuestions.get(q) + "_" + alRoles.get(ii) + "_" + empList.get(e));
											if (innList == null)
												innList = new ArrayList<String>();
											if (innList != null && !innList.isEmpty()) {
												
											} else {
												
												if(ii == 0){
													alInnerExport.add(new DataStyle(uF.showData(hmQuestionWeightage.get((String) alQuestions.get(q)).toString(), ""),
															Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
												}
												alInnerExport.add(new DataStyle("Not Rated", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
												
												
											}
										} else {
											
											if(ii == 0){
												alInnerExport.add(new DataStyle(hmQuestionWeightage.get((String) alQuestions.get(q)).toString(), Element.ALIGN_CENTER,
														"NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
											}
											
//											alInnerExport.add(new DataStyle(uF.showData(
//													(String) hmQuestionMarks.get((String) alQuestions.get(q) + "_" + alRoles.get(ii) + "_" + rolesUserIds.get(m) + "_" + empList.get(e)),
//													"Not Rated"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
											
											alInnerExport.add(new DataStyle(uF.showData(
													(String) hmQuestionMarks.get((String) alQuestions.get(q) + "_" + alRoles.get(ii) + "_" + empList.get(e)),
													"Not Rated"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
											
										}
									}
	
									reportListExport.add(alInnerExport);
//									String sb = "";
									alInnerExport = new ArrayList<DataStyle>();
									alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
									alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
									alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
									for (int r = 0; alRoles != null && r < alRoles.size(); r++) {
										String sb = "";
										if (r == 0) {
//											alInnerExport = new ArrayList<DataStyle>();
//											sb = "Answer/Comments:";
//											alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
											
										}
										sb = "Answer/Comments:";
//										alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
										
										if (uF.parseToInt(alRoles.get(0).toString()) == 4 || uF.parseToInt(alRoles.get(0).toString()) == 10) {
//											String queAns = hmOuterpeerAnsDetails.get(alQuestions.get(q) + "_" + memberId + "_" + rolesUserIds.get(m) + "_" + empList.get(e));
											String queAns = hmOuterpeerAnsDetails.get(alQuestions.get(q) + "_" + alRoles.get(r) + "_" + empList.get(e));
											
											alInnerExport.add(new DataStyle(uF.showData(queAns, ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
											
										} else {
//											if (hmQuestionAnswer.containsKey((String) alQuestions.get(q) + "_" + memberId + "_" + rolesUserIds.get(m) + "_" + empList.get(e))) {
											if (hmQuestionAnswer.containsKey((String) alQuestions.get(q) + "_" + alRoles.get(r) + "_" + empList.get(e))) {
//												String strAns = ((String) hmQuestionAnswer.get((String) alQuestions.get(q) + "_" + memberId + "_" + rolesUserIds.get(m) + "_" + empList.get(e)));
												String strAns = ((String) hmQuestionAnswer.get((String) alQuestions.get(q) + "_" + alRoles.get(r) + "_" + empList.get(e)));
												if (strAns != null) {
													strAns = strAns.replace(":_:", "<br/>");
													
													sb = sb + uF.showData(strAns, "");
													alInnerExport.add(new DataStyle(uF.showData(sb, ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",
															BaseColor.WHITE));
//													reportListExport.add(alInnerExport);
												}
											}
										}
									}
									reportListExport.add(alInnerExport);
								}
								// reportListExport.add(alInnerExport);
								alInnerExport = new ArrayList<DataStyle>();
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
								for (int r1 = 0; alRoles != null && r1 < alRoles.size(); r1++) {
									StringBuilder strCSb = new StringBuilder();
									strCSb.append("Section Comment: ");
									strCSb.append(uF.showData((String) hmSectionComment.get(sectionIdsList.get(a) + "_" + alRoles.get(r1) + "_" + empList.get(e) ), "") + "");
									
									alInnerExport.add(new DataStyle(uF.showData(strCSb.toString(), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
								}
								reportListExport.add(alInnerExport);
	
							}
	
							Map<String, String> hmAppSystemType = (Map<String, String>) request.getAttribute("hmAppSystemType");
							List<String> alLevelGoalTargetKRA = hmSubsectionIds.get(sectionIdsList.get(a) + "GTK");
							
							for (int i = 0; alLevelGoalTargetKRA != null && i < alLevelGoalTargetKRA.size(); i++) {
								cnt++;
								String appSystemType = hmAppSystemType.get((String) alLevelGoalTargetKRA.get(i));
								
								List alQuestions = (List) hmGoalTargetKraQuestionsMap.get((String) alLevelGoalTargetKRA.get(i));
								String strSystemType = "";
								if (appSystemType != null && appSystemType.equals("3")) {
									strSystemType = "Goal";
								} else if (appSystemType != null && appSystemType.equals("4")) {
									strSystemType = "KRA";
								} else if (appSystemType != null && appSystemType.equals("5")) {
									strSystemType = "Target";
								}
	
								alInnerExport = new ArrayList<DataStyle>();
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle(a + 1 + "." + cnt + ") " + hmLevel.get((String) alLevelGoalTargetKRA.get(i)), Element.ALIGN_CENTER,
										"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								reportListExport.add(alInnerExport);
								
								alInnerExport = new ArrayList<DataStyle>();
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle(uF.showData((String) hmLevel.get((String) alLevelGoalTargetKRA.get(i) + "_SD"), ""),
										Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								reportListExport.add(alInnerExport);
	
								alInnerExport = new ArrayList<DataStyle>();
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle(uF.showData((String) hmLevel.get((String) alLevelGoalTargetKRA.get(i) + "_LD"), ""),
										Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								reportListExport.add(alInnerExport);
	
								
								alInnerExport = new ArrayList<DataStyle>();
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle(strSystemType, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle("Weightage %", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
	
								for (int r = 0; alRoles != null && r < alRoles.size(); r++) {
									alInnerExport.add(new DataStyle("Marks Role: " + hmorientationMembers.get((String) alRoles.get(r)), Element.ALIGN_CENTER,
											"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
//									alInnerExport.add(new DataStyle("Weightage %", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								}
								
								reportListExport.add(alInnerExport);
	
								int qCount = 1;
								for (int q = 0; alQuestions != null && q < alQuestions.size(); q++) {
									
									String goalTargetKraId =  hmQuestionWiseGoalTargetKra.get(alQuestions.get(q));
//									System.out.println("hmKraDetails=="+hmKraDetails.get(goalTargetKraId));
									if(hmKraDetails.get(goalTargetKraId).contains(empList.get(e))){
										
										alInnerExport = new ArrayList<DataStyle>();
										alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
										alInnerExport.add(new DataStyle(qCount + ") " + hmQuestions.get((String) alQuestions.get(q)), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
										for (int ii = 0; alRoles != null && ii < alRoles.size(); ii++) {
	
											if (uF.parseToInt(alRoles.get(0).toString()) == 4 || uF.parseToInt(alRoles.get(0).toString()) == 10) {
	//											List<String> innList = hmOuterpeerAppraisalDetails.get(alQuestions.get(q) + "_" + alRoles.get(ii) + "_"
	//													+ rolesUserIds.get(m) + "_" + empList.get(e));
												List<String> innList = hmOuterpeerAppraisalDetails.get(alQuestions.get(q) + "_" + alRoles.get(ii) + "_" + empList.get(e));
												if (innList == null)
													innList = new ArrayList<String>();
												if (innList != null && !innList.isEmpty()) {
													alInnerExport = new ArrayList<DataStyle>();
													
													alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
													if(ii == 0){
														alInnerExport.add(new DataStyle(uF.showData((String) innList.get(1), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
													}
													
													alInnerExport.add(new DataStyle(uF.showData((String) innList.get(0), "Not Rated"), Element.ALIGN_CENTER, "NEW_ROMAN",
															6, "0", "0", BaseColor.WHITE));
													
													
												} else {
													if(ii == 0){
														alInnerExport.add(new DataStyle("" + hmQuestionWeightage.get((String) alQuestions.get(q)), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
													}
													
												}
											} else {
												
												if( ii == 0){
													alInnerExport.add(new DataStyle("" + hmQuestionWeightage.get((String) alQuestions.get(q)), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
												}
												
	//											alInnerExport.add(new DataStyle(uF.showData(
	//													(String) hmQuestionMarks.get((String) alQuestions.get(q) + "_" + alRoles.get(ii) + "_" + rolesUserIds.get(m) + "_" + empList.get(e)),
	//													"Not Rated"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
												alInnerExport.add(new DataStyle(uF.showData(
														(String) hmQuestionMarks.get((String) alQuestions.get(q) + "_" + alRoles.get(ii) + "_" + empList.get(e)),
														"Not Rated"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
												
											}
										}
										
										reportListExport.add(alInnerExport);
										
										alInnerExport = new ArrayList<DataStyle>();
										alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
										alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
										alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
										for (int r = 0; alRoles != null && r < alRoles.size(); r++) {
											String sb = "";
											if (r == 0) {
												
//												alInnerExport = new ArrayList<DataStyle>();
	//											sb = "Answer/Comments:";
//												alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
												
	
											}
											sb = "Answer/Comments:";
											
											if (uF.parseToInt(alRoles.get(0).toString()) == 4 || uF.parseToInt(alRoles.get(0).toString()) == 10) {
	//											String queAns = hmOuterpeerAnsDetails.get(alQuestions.get(q) + "_" + memberId + "_" + rolesUserIds.get(m) + "_" + empList.get(e));
												String queAns = hmOuterpeerAnsDetails.get(alQuestions.get(q) + "_" + alRoles.get(r) + "_" + empList.get(e));
												
												alInnerExport.add(new DataStyle(uF.showData(queAns, ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
											} else {
	
	//											if (hmQuestionAnswer.containsKey((String) alQuestions.get(q) + "_" + memberId + "_" + rolesUserIds.get(m) + "_" + empList.get(e))) {
												if (hmQuestionAnswer.containsKey((String) alQuestions.get(q) + "_" + alRoles.get(r) + "_" + empList.get(e))) {
	//												String strAns = ((String) hmQuestionAnswer.get((String) alQuestions.get(q) + "_" + memberId + "_" + rolesUserIds.get(m) + "_" + empList.get(e)));
													String strAns = ((String) hmQuestionAnswer.get((String) alQuestions.get(q) + "_" + alRoles.get(r) + "_" + empList.get(e)));
													if (strAns != null) {
														strAns = strAns.replace(":_:", "<br/>");
														
														sb = sb + uF.showData(strAns, "");
														alInnerExport.add(new DataStyle(uF.showData(sb, ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
	//													reportListExport.add(alInnerExport);
													}
												}
											}
										}
										reportListExport.add(alInnerExport);
										qCount++;
									}
								}
								
								alInnerExport = new ArrayList<DataStyle>();
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
								for (int r1 = 0; alRoles != null && r1 < alRoles.size(); r1++) {
									StringBuilder strCSb = new StringBuilder();
									strCSb.append("Section Comment: ");
									strCSb.append(uF.showData((String) hmSectionComment.get(sectionIdsList.get(a) + "_" + alRoles.get(r1) + "_" + empList.get(e) ), "") + "");
									
									alInnerExport.add(new DataStyle(uF.showData(strCSb.toString(), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
								}
								reportListExport.add(alInnerExport);
								
							}
						}
						
						alInnerExport = new ArrayList<DataStyle>();
						alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
						alInnerExport.add(new DataStyle("Areas of Strength:", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
						alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
						for (int r1 = 0; alRoles != null && r1 < alRoles.size(); r1++) {
							
							StringBuilder strAs = new StringBuilder();
							strAs.append("Areas of Strength: ");
							strAs.append(uF.showData((String)hmStrengthImprovements.get(empList.get(e)+"_"+alRoles.get(r1)+"_STRENGTH"), "") + "");
							
//							alInnerExport.add(new DataStyle("Areas of Strength:", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
							alInnerExport.add(new DataStyle(uF.showData((String)hmStrengthImprovements.get(empList.get(e)+"_"+alRoles.get(r1)+"_STRENGTH"), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
							
						}
						reportListExport.add(alInnerExport);
						
						alInnerExport = new ArrayList<DataStyle>();
						alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
						alInnerExport.add(new DataStyle("Areas of Improvement:", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
						alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
						for (int r1 = 0; alRoles != null && r1 < alRoles.size(); r1++) {
							StringBuilder strAi = new StringBuilder();
							strAi.append("Areas of Improvement: ");
							strAi.append(uF.showData((String)hmStrengthImprovements.get(empList.get(e)+"_"+alRoles.get(r1)+"_IMPROVEMENT"), "") + "");
//							alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
							alInnerExport.add(new DataStyle(uF.showData((String)hmStrengthImprovements.get(empList.get(e)+"_"+alRoles.get(r1)+"_IMPROVEMENT"), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
							
						}
						reportListExport.add(alInnerExport);
//					}
//				}
//			}
		}

		

		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Appraiser Comments", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		reportListExport.add(alInnerExport);
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle(uF.showData((String) request.getAttribute("strFinalComments"), "Not Commented yet"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		reportListExport.add(alInnerExport);

		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Appraised by -" + uF.showData((String) request.getAttribute("strAppraisedBy"), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		reportListExport.add(alInnerExport);

		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("on -" + uF.showData((String) request.getAttribute("strAppraisedOn"), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		reportListExport.add(alInnerExport);
		session.setAttribute("reportListExport", reportListExport);
	}
// ===end parvez date: 16-12-2022===

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStrMessage() {
		return strMessage;
	}

	public void setStrMessage(String strMessage) {
		this.strMessage = strMessage;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

}