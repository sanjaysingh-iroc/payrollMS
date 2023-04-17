package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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

public class AppraisalStatus__1 implements ServletRequestAware, IStatements {

	
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
		
		/*if(strSessionUserType != null && strSessionUserType.equals(HRMANAGER) && getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(REVIEW_FINALIZATION_ALERT)){
			updateUserAlerts();
		}*/
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		
		request.setAttribute(PAGE, "/jsp/performance/AppraisalStatus.jsp");
		request.setAttribute(TITLE, "Review Status");

		if(getType() != null && getType().equalsIgnoreCase("REMINDER")) {
			sendReminderToAllPendingReviewers(getId());
			return MESSAGE;
		}
		getOrientationMember();
		getAppraisalStatus(uF);
		getAppraisalStatusReport(uF);
		getRemarks();
		getEmpSupervisor();
		getOrientationCount(uF);
		//getSingleOpenWithoutMarksQueCount(uF);
		getSingleOpenWithoutMarksQueReadUnreadCount(uF);
		if(getFromPage() != null && getFromPage().equalsIgnoreCase("AD")) {
			return "success";
		} 
		
		return LOAD;
	}
	
	
		
//	private void updateUserAlerts() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
//		try {
//			con = db.makeConnection(con);
//			
//			String strDomain = request.getServerName().split("\\.")[0];
//			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//			userAlerts.setStrDomain(strDomain);
//			userAlerts.setStrEmpId(""+nEmpId);
//			userAlerts.set_type(REVIEW_FINALIZATION_ALERT);
//			userAlerts.setStatus(UPDATE_ALERT);
//			Thread t = new Thread(userAlerts);
//			t.run();
////			System.out.println("in Appraisal UserAlerts ...");
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	
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
			pst = con.prepareStatement("select user_type_id,user_id,emp_id from appraisal_question_answer where appraisal_id=? and is_submit=true group by user_type_id,user_id,emp_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
//			System.out.println("pst ============ > "+ pst);
			while (rs.next()) {
				hmExistQueAns.put(rs.getString("user_type_id")+"_"+rs.getString("user_id")+"_"+rs.getString("emp_id"), rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmExistQueAns ===>> " + hmExistQueAns);
			String strDomain = request.getServerName().split("\\.")[0];
//			setDomain(strDomain);
//			Thread th = new Thread(this);
//			th.start();
			
			Map<String, String> hmReviewData = getReviewDetails(con);
			Map<String, Map<String, String>> hmRevieweewiseAppraiser = getRevieweewiseAppraiser(con);
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
			Map<String, String> hmOrientData = CF.getOrientMemberID(con);
			
			Iterator<String> it = hmRevieweewiseAppraiser.keySet().iterator();
			while(it.hasNext()) {
				String strRevieweeId = it.next();
				Map<String, String> hmRevieweeNameData = hmEmpInfo.get(strRevieweeId);
				
				Map<String, String> hmRevieweeData = hmRevieweewiseAppraiser.get(strRevieweeId);
				List<List<String>> allIdList = new ArrayList<List<String>>();
				/*if(hmRevieweeData.get("REVIEW_SELFID") != null && !hmRevieweeData.get("REVIEW_SELFID").equals("")) {
					List<String> selfID = Arrays.asList(hmRevieweeData.get("REVIEW_SELFID").split(",")); 
					for (int i = 0; selfID != null && i < selfID.size(); i++) {
						if(selfID.get(i) != null && !selfID.get(i).equals("")) {
							List<String> innerList = new ArrayList<String>();
							innerList.add(selfID.get(i));
							innerList.add("Self");
							allIdList.add(innerList);
						}
					}
				}*/
				
				
				if(hmRevieweeData.get("REVIEW_PEERID") != null && !hmRevieweeData.get("REVIEW_PEERID").equals("")) {
					List<String> peerID = Arrays.asList(hmRevieweeData.get("REVIEW_PEERID").split(",")); 
					for (int i = 0; peerID != null && i < peerID.size(); i++) {
						if(peerID.get(i) != null && !peerID.get(i).equals("")) {
							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("Peer")+"_"+peerID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Peer")+"_"+peerID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(peerID.get(i));
								innerList.add("Peer");
								allIdList.add(innerList);
							}
						}
					}
				}
				if(hmRevieweeData.get("REVIEW_MANAGERID") != null && !hmRevieweeData.get("REVIEW_MANAGERID").equals("")) {
					List<String> managerID = Arrays.asList(hmRevieweeData.get("REVIEW_MANAGERID").split(",")); 
					for (int i = 0; managerID != null && i < managerID.size(); i++) {
						if(managerID.get(i) != null && !managerID.get(i).equals("")) {
							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("Manager")+"_"+managerID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Manager")+"_"+managerID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(managerID.get(i));
								innerList.add("Manager");
								allIdList.add(innerList);
							}
						}
					}
				}
				if(hmRevieweeData.get("REVIEW_HRID") != null && !hmRevieweeData.get("REVIEW_HRID").equals("")) {
					List<String> hrID = Arrays.asList(hmRevieweeData.get("REVIEW_HRID").split(",")); 
					for (int i = 0; hrID != null && i < hrID.size(); i++) {
						if(hrID.get(i) != null && !hrID.get(i).equals("")) {
//							System.out.println("hmOrientData.get(HR)===>> " + hmOrientData.get("HR"));
//							System.out.println("hrID.get(i) ===>> " + hrID.get(i));
//							System.out.println("strRevieweeId ===>> " + strRevieweeId);
//							System.out.println("hmExistQueAns.get(hmOrientData.get(HR)-hrID.get(i)-strRevieweeId) ===>> " + hmExistQueAns.get(hmOrientData.get("HR")+"_"+hrID.get(i)+"_"+strRevieweeId));
							
							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("HR")+"_"+hrID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("HR")+"_"+hrID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(hrID.get(i));
								innerList.add("HR");
								allIdList.add(innerList);
							}
						}
					}
				}
				
				if(hmRevieweeData.get("REVIEW_CEOID") != null && !hmRevieweeData.get("REVIEW_CEOID").equals("")) {
					List<String> ceoID = Arrays.asList(hmRevieweeData.get("REVIEW_CEOID").split(",")); 
					for (int i = 0; ceoID != null && i < ceoID.size(); i++) {
						if(ceoID.get(i) != null && !ceoID.get(i).equals("")) {
							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("CEO")+"_"+ceoID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("CEO")+"_"+ceoID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(ceoID.get(i));
								innerList.add("CEO");
								allIdList.add(innerList);
							}
						}
					}
				}
				
				if(hmRevieweeData.get("REVIEW_HODID") != null && !hmRevieweeData.get("REVIEW_HODID").equals("")) {
					List<String> hodID = Arrays.asList(hmRevieweeData.get("REVIEW_HODID").split(",")); 
					for (int i = 0; hodID != null && i < hodID.size(); i++) {
						if(hodID.get(i) != null && !hodID.get(i).equals("")) {
							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("HOD")+"_"+hodID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("HOD")+"_"+hodID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(hodID.get(i));
								innerList.add("HOD");
								allIdList.add(innerList);
							}
						}
					}
				}
				
				if(hmRevieweeData.get("REVIEW_OTHERID") != null && !hmRevieweeData.get("REVIEW_OTHERID").equals("")) {
					List<String> otherID = Arrays.asList(hmRevieweeData.get("REVIEW_OTHERID").split(",")); 
					for (int i = 0; otherID != null && i < otherID.size(); i++) {
						if(otherID.get(i) != null && !otherID.get(i).equals("")) {
							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("Anyone")+"_"+otherID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Anyone")+"_"+otherID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(otherID.get(i));
								innerList.add("Anyone");
								allIdList.add(innerList);
							}
						}
					}
				}
				
				if(hmRevieweeData.get("REVIEW_SUBORDINATEID") != null && !hmRevieweeData.get("REVIEW_SUBORDINATEID").equals("")) {
					List<String> subordinateID = Arrays.asList(hmRevieweeData.get("REVIEW_SUBORDINATEID").split(",")); 
					for (int i = 0; subordinateID != null && i < subordinateID.size(); i++) {
						if(subordinateID.get(i) != null && !subordinateID.get(i).equals("")) {
							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("Sub-ordinate")+"_"+subordinateID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Sub-ordinate")+"_"+subordinateID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(subordinateID.get(i));
								innerList.add("Sub-ordinate");
								allIdList.add(innerList);
							}
						}
					}
				}
				
				if(hmRevieweeData.get("REVIEW_GRANDSUPERVISORID") != null && !hmRevieweeData.get("REVIEW_GRANDSUPERVISORID").equals("")) {
					List<String> gSupervisorID = Arrays.asList(hmRevieweeData.get("REVIEW_GRANDSUPERVISORID").split(",")); 
					for (int i = 0; gSupervisorID != null && i < gSupervisorID.size(); i++) {
						if(gSupervisorID.get(i) != null && !gSupervisorID.get(i).equals("")) {
							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("GroupHead")+"_"+gSupervisorID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("GroupHead")+"_"+gSupervisorID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(gSupervisorID.get(i));
								innerList.add("Group Head");
								allIdList.add(innerList);
							}
						}
					}
				}
				
				if(hmRevieweeData.get("REVIEW_OTHERPEERID") != null && !hmRevieweeData.get("REVIEW_OTHERPEERID").equals("")) {
					List<String> otherPeerID = Arrays.asList(hmRevieweeData.get("REVIEW_OTHERPEERID").split(",")); 
					for (int i = 0; otherPeerID != null && i < otherPeerID.size(); i++) {
						if(otherPeerID.get(i) != null && !otherPeerID.get(i).equals("")) {
							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("Other Peer")+"_"+otherPeerID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Other Peer")+"_"+otherPeerID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
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
					if(innerList.get(0) != null && !innerList.get(0).equals("")) {
						Map<String, String> hmEmpInner = hmEmpInfo.get(innerList.get(0));
						Notifications nF = new Notifications(N_PENDING_REVIEW_REMINDER, CF);
						nF.setDomain(strDomain);
						nF.request = request;
						nF.setStrEmpId(innerList.get(0));
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrRevieweeName(hmRevieweeNameData.get("FNAME")+" " +hmRevieweeNameData.get("LNAME"));
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
				request.setAttribute("STATUS_MSG", SUCCESSM+"Reminder mails sent successfully."+END);
			}
			
			
			
//			System.out.println("hmReadUnreadCount =====> "+ hmReadUnreadCount);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("STATUS_MSG", ERRORM+"Reminder mails not sent, Please try again."+END);
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
			pst = con.prepareStatement("Select count(*) as count,aqa.user_type_id,aqa.emp_id,aqa.read_status from appraisal_question_answer aqa," +
					"appraisal_question_details aqd where aqa.appraisal_id = ? and aqa.appraisal_freq_id= ? and aqa.appraisal_id = aqd.appraisal_id and " +
					"aqa.question_id = aqd.question_id and aqd.answer_type = 12 and aqa.is_submit=true group by aqa.read_status,aqa.user_type_id,aqa.emp_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
//			System.out.println("pst ============ > "+ pst);
			while (rs.next()) {
				hmReadUnreadCount.put(rs.getString("user_type_id")+"_"+rs.getString("emp_id")+"_"+rs.getString("read_status"), rs.getString("count"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmReadUnreadCount", hmReadUnreadCount);
//			System.out.println("hmReadUnreadCount =====> "+ hmReadUnreadCount);
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
			
			Map<String, String> hmEmpWlocationMap=CF.getEmpWlocationMap(con);
			Map<String,String> hmEmpName=CF.getEmpNameMap(con, null,null);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con); 
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			
			Map<String,String> hmMemberMP=new HashMap<String, String>();			
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,supervisor_emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
				" and is_alive= true and emp_per_id >0 order by supervisor_emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("PST ===>> " + pst);
			Map<String, List<String>> hmHireracyLevelEmpIds = new LinkedHashMap<String, List<String>>();
//			List<String> alHireracyLevels = new ArrayList<String>();
			while(rs.next()) {
				List<String> alInner = hmHireracyLevelEmpIds.get(rs.getString("supervisor_emp_id"));
				if(alInner==null) alInner = new ArrayList<String>();
				if(uF.parseToInt(rs.getString("supervisor_emp_id")) == uF.parseToInt(rs.getString("emp_id"))) {
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
			if(strSessionUserType != null && strSessionUserType.equals(MANAGER)) {
				sbQuery = new StringBuilder();
				sbQuery.append("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id  and appraisal_id=? " +
					"and (supervisor_ids like '%,"+strSessionEmpId+",%' or grand_supervisor_ids like '%,"+strSessionEmpId+",%' or hod_ids like '%,"+strSessionEmpId+",%' ");
				if(sbClildEmpIds!=null && !sbClildEmpIds.toString().equals("")) {
					sbQuery.append(" or epd.emp_per_id in ("+sbClildEmpIds.toString()+") ");
				}
				sbQuery.append(") order by emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
//				pst = con.prepareStatement("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id and supervisor_ids like '%,"+strSessionEmpId+",%' and appraisal_id=? order by emp_fname");
				pst.setInt(1, uF.parseToInt(getId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rs.getString("reviewee_id"));
					} else {
						sbEmpId.append(","+rs.getString("reviewee_id"));
					}
				}
				rs.close();
				pst.close();
			}
			
			/*StringBuilder sbEmpId = null;
			if(strSessionUserType != null && strSessionUserType.equals(MANAGER)) {
				pst = con.prepareStatement("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id and supervisor_ids like '%,"+strSessionEmpId+",%' and appraisal_id=? order by emp_fname");
				pst.setInt(1, uF.parseToInt(getId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rs.getString("reviewee_id"));
					} else {
						sbEmpId.append(","+rs.getString("reviewee_id"));
					}
				}
				rs.close();
				pst.close();
			}*/
			
			pst = con.prepareStatement("select oriented_type,self_ids,is_anonymous_review from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			String oriented_type="";
			String self_ids="";
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
			if(sbEmpId!=null) { 
				sbQuery.append(" and reviewee_id in ("+sbEmpId.toString()+") ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			Map<String, List<String>> hmRevieweeUserIds = new HashMap<String, List<String>>();
			List<String> selfList = new ArrayList<String>();
			while (rs.next()) {
				selfList.add(rs.getString("reviewee_id"));
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("subordinate_ids")); //0
				innerList.add(rs.getString("peer_ids")); //1
				innerList.add(rs.getString("other_peer_ids")); //2
				innerList.add(rs.getString("supervisor_ids")); //3
				innerList.add(rs.getString("grand_supervisor_ids")); //4
				innerList.add(rs.getString("hr_ids")); //5
				innerList.add(rs.getString("hod_ids")); //6
				innerList.add(rs.getString("ceo_ids")); //7
				innerList.add(rs.getString("other_ids")); //8
				
				hmRevieweeUserIds.put(rs.getString("reviewee_id"), innerList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("oriented_type ===>> " + oriented_type);
			
			List<String> memberList = CF.getOrientationMemberDetails(con,uF.parseToInt(oriented_type));
//			System.out.println("memberList ===>> " + memberList);
			
			Map<String, String> hmOrientMemberID = CF.getOrientMemberID(con);
			
			//if(oriented_type != null && !oriented_type.equals("5")){
			if(self_ids!=null && !self_ids.equals("")) {
				
				sbQuery = new StringBuilder();
				sbQuery.append("select emp_image,emp_per_id from employee_personal_details where emp_per_id>0");
				if(sbEmpId!=null) { 
					sbQuery.append(" and emp_per_id in ("+sbEmpId.toString()+") ");
				}
				pst=con.prepareStatement(sbQuery.toString());
				rs=pst.executeQuery();
				Map<String,String> empImageMap=new HashMap<String,String>();
				while(rs.next()){
					empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
				}
				rs.close();
				pst.close();
				
				/*List<String> memberList = Arrays.asList(mem.split(","));
				for(int i = 0; i < memberList.size(); i++) {
					hmOrientationCount.put(memberList.get(i), orientationMemberMp.get(memberList.get(i)));
				}*/
				
				getEmpWlocation(self_ids);
//				self_ids=self_ids.substring(1,self_ids.length()-1);
//				List<String> selfList = Arrays.asList(self_ids.split(","));
				
				for(int j=0; selfList!=null && !selfList.isEmpty() && j<selfList.size();j++) {
					
					pst = con.prepareStatement("select emp_id,user_id,user_type_id from appraisal_question_answer where appraisal_id=? and emp_id=? " +
						" and appraisal_freq_id=? and appraisal_question_answer_id in (select max(appraisal_question_answer_id) from appraisal_question_answer " +
						" where appraisal_id=? and emp_id=? and appraisal_freq_id=? and is_submit=true group by user_type_id,user_id) and is_submit=true");
					pst.setInt(1, uF.parseToInt(getId()));
					pst.setInt(2, uF.parseToInt(selfList.get(j).trim()));
					pst.setInt(3, uF.parseToInt(getAppFreqId()));
					pst.setInt(4, uF.parseToInt(getId()));
					pst.setInt(5, uF.parseToInt(selfList.get(j).trim()));
					pst.setInt(6, uF.parseToInt(getAppFreqId()));
					rs = pst.executeQuery();
					
					Map<String, String> hmCheckAppraisal = new HashMap<String, String>();
					Map<String, List<String>> hmCheckUsers = new HashMap<String, List<String>>();
					while(rs.next()) {
						String key = rs.getString("emp_id")+"_"+rs.getString("user_id")+"_"+rs.getString("user_type_id");
						hmCheckAppraisal.put(key, rs.getString("emp_id"));
						
						List<String> innerList = new ArrayList<String>();
						if(rs.getString("user_type_id").equals("7")) {
							innerList = hmCheckUsers.get(HRMANAGER);
							if(innerList == null) innerList = new ArrayList<String>();
							if(!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckUsers.put(HRMANAGER, innerList);	
						} else if(rs.getString("user_type_id").equals("2")) {
							innerList = hmCheckUsers.get(MANAGER);
							if(innerList == null) innerList = new ArrayList<String>();
							if(!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckUsers.put(MANAGER, innerList);
						} else if(rs.getString("user_type_id").equals("8")) {
							innerList = hmCheckUsers.get("GroupHead");
							if(innerList == null) innerList = new ArrayList<String>();
							if(!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckUsers.put("GroupHead", innerList);
						} else if(rs.getString("user_type_id").equals("5")) {
							innerList = hmCheckUsers.get(CEO);
							if(innerList == null) innerList = new ArrayList<String>();
							if(!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckUsers.put(CEO, innerList);
						} else if(rs.getString("user_type_id").equals("13")) {
							innerList = hmCheckUsers.get(HOD);
							if(innerList == null) innerList = new ArrayList<String>();
							if(!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckUsers.put(HOD, innerList);
						} else if(rs.getString("user_type_id").equals("6")) {
							innerList = hmCheckUsers.get("Sub-ordinate");
							if(innerList == null) innerList = new ArrayList<String>();
							if(!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckUsers.put("Sub-ordinate", innerList);
						} else if(rs.getString("user_type_id").equals("4")) {
							innerList = hmCheckUsers.get("Peer");
							if(innerList == null) innerList = new ArrayList<String>();
							if(!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckUsers.put("Peer", innerList);
						} else if(rs.getString("user_type_id").equals("14")) {
							innerList = hmCheckUsers.get("Other Peer");
							if(innerList == null) innerList = new ArrayList<String>();
							if(!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckUsers.put("Other Peer", innerList);
						} else if(rs.getString("user_type_id").equals("10")) {
							innerList = hmCheckUsers.get("Anyone");
							if(innerList == null) innerList = new ArrayList<String>();
							if(!innerList.contains(rs.getString("user_id"))) {
								innerList.add(rs.getString("user_id"));
							}
							hmCheckUsers.put("Anyone", innerList);
						}
					}
					rs.close();
					pst.close();
					
//					System.out.println("hmCheckAppraisal ===>> " + hmCheckAppraisal);
//					System.out.println("hmCheckUsers ===>> " + hmCheckUsers);
					
					StringBuilder sbMemList = new StringBuilder();
					
					//self
					if(hmOrientMemberID.get("Self") != null && memberList!=null && memberList.contains(hmOrientMemberID.get("Self"))) {
						String brdrColor = "red";
						if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+selfList.get(j).trim()+"_3")){
							brdrColor = "green";
						}
						if(isAnonymousReview && strSessionUserType!=null && strSessionUserType.equals(EMPLOYEE)) {
							sbMemList.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "+brdrColor+"\" " +
									" border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-SELF)\"/>");
						} else {
							sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+selfList.get(j)+"','"+hmEmpName.get(selfList.get(j))+"')\" >" +
								"<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "+brdrColor+"\" " +
								"data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+selfList.get(j).trim()+"/"+I_16x16+"/"+empImageMap.get(selfList.get(j).trim())+"\" " +
								"border=\"0\" height=\"16px\" width=\"16px;\" title=\""+hmEmpName.get(selfList.get(j))+"(Role-SELF)\"/></a>");
						}
					}
					
					//HRMANAGER
					List<String> usersList = hmRevieweeUserIds.get(selfList.get(j).trim());
					if(hmOrientMemberID.get("HR") != null && memberList!=null && memberList.contains(hmOrientMemberID.get("HR"))) {
						int cnt=0;
						sbMemList = new StringBuilder();
						/*if(hmCheckUsers.get(HRMANAGER)!=null) {
//							System.out.println("hmCheckHR.get(HRMANAGER) ===>> " + hmCheckHR.get(HRMANAGER));
							List<String> innerList = hmCheckUsers.get(HRMANAGER);
							for(int i=0; i<innerList.size(); i++) {
								cnt++;
								sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+innerList.get(i)+"','"+hmEmpName.get(innerList.get(i))+"')\" ><img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid green\" data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+innerList.get(i)+"/"+I_16x16+"/"+empImageMap.get(innerList.get(i))+"\" border=\"0\" height=\"16px\" " +
									"width=\"16px;\" title=\""+hmEmpName.get(innerList.get(i))+"(Role-HR)\"/></a>");
							}
						} else {*/
							List<String> hrList = new ArrayList<String>();
							if(usersList.get(5) != null && !usersList.get(5).equals("")) {
								hrList = Arrays.asList(usersList.get(5).split(","));
							}
							for (int i = 0; hrList != null && !hrList.isEmpty() && i < hrList.size(); i++) {
//									System.out.println("hrList.get(i).trim() ===>> " + hrList.get(i).trim());
								if(!hrList.get(i).trim().equals("")) {
									if(cnt>7) {
										break;
									} else {
										cnt++;
									}
									String brdrColor = "red";
									if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+hrList.get(i).trim()+"_"+hmOrientMemberID.get("HR"))){
										brdrColor = "green";
									}
									if(isAnonymousReview && strSessionUserType!=null && strSessionUserType.equals(EMPLOYEE)) {
										sbMemList.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "+brdrColor+"\" " +
											" border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-HR)\"/>");
									} else {
										sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+hrList.get(i).trim()+"','"+hmEmpName.get(hrList.get(i).trim())+"')\" >" +
											"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "+brdrColor+"\" data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+hrList.get(i).trim()+"/"+I_16x16+"/"+empImageMap.get(hrList.get(i).trim())+"\" border=\"0\" height=\"16px\" " +
											"width=\"16px;\" title=\""+hmEmpName.get(hrList.get(i).trim())+"(Role-HR)\"/></a>");
									}
								}
							}
//						}
						hmMemberMP.put(selfList.get(j).trim()+"_"+hmOrientMemberID.get("HR"), sbMemList.toString());
					}
//					System.out.println("hmMemberMP ===>> " + hmMemberMP);
					
					//Manager
					if(hmOrientMemberID.get("Manager") != null && memberList!=null && memberList.contains(hmOrientMemberID.get("Manager"))) {
						int cnt=0;
						sbMemList = new StringBuilder();
						/*if(hmCheckUsers.get(MANAGER)!=null) {
							List<String> innerList = hmCheckUsers.get(MANAGER);
							for(int i=0; i<innerList.size(); i++) {
								cnt++;
								sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+innerList.get(i)+"','"+hmEmpName.get(innerList.get(i))+"')\" >" +
									"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\"userImages/"+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+innerList.get(i)+"/"+I_16x16+"/"+empImageMap.get(innerList.get(i))+"\" border=\"0\" height=\"16px\" " +
									"width=\"16px;\" title=\""+hmEmpName.get(innerList.get(i))+"(Role-Manager)\"/></a>");
							}
						} else {*/
							List<String> managerList = new ArrayList<String>();
							if(usersList.get(3) != null && !usersList.get(3).equals("")) {
								managerList = Arrays.asList(usersList.get(3).split(","));
							}
							for (int i = 0; managerList != null && !managerList.isEmpty() && i < managerList.size(); i++) {
								if(!managerList.get(i).trim().equals("")) {
									if(cnt>7) {
//										sbMemList.append("<a href=\"javascript:void(0);\" style=\"margin-right: 4px;\" onclick=\"seeEmpList('"+selfList.get(j)+"','"+id+"','"+getAppFreqId()+"');\" class=\"OR testa\">more..</a>");
										break;
									} else {
										cnt++;
									}
									String brdrColor = "red";
									if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+managerList.get(i).trim()+"_"+hmOrientMemberID.get("Manager"))) {
										brdrColor = "green";
									}
									if(isAnonymousReview && strSessionUserType!=null && strSessionUserType.equals(EMPLOYEE)) {
										sbMemList.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "+brdrColor+"\" " +
											" border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-Manager)\"/>");
									} else {
										sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+managerList.get(i).trim()+"','"+hmEmpName.get(managerList.get(i).trim())+"')\" >" +
											"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "+brdrColor+"\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+managerList.get(i).trim()+"/"+I_16x16+"/"+empImageMap.get(managerList.get(i).trim())+"\" border=\"0\" height=\"16px\" " +
											"width=\"16px;\" title=\""+hmEmpName.get(managerList.get(i).trim())+"(Role-Manager)\"/></a>");
									}
								}
							}
//						}
						hmMemberMP.put(selfList.get(j).trim()+"_"+hmOrientMemberID.get("Manager"), sbMemList.toString());
					}
					
					//Grand Manager
					if(hmOrientMemberID.get("GroupHead") != null && memberList!=null && memberList.contains(hmOrientMemberID.get("GroupHead"))) {
						int cnt=0;
						sbMemList = new StringBuilder();
						List<String> gManagerList = new ArrayList<String>();
						if(usersList.get(4) != null && !usersList.get(4).equals("")) {
							gManagerList = Arrays.asList(usersList.get(4).split(","));
						}
						for (int i = 0; gManagerList != null && !gManagerList.isEmpty() && i < gManagerList.size(); i++) {
							if(!gManagerList.get(i).trim().equals("")) {
								if(cnt>7) {
									break;
								} else {
									cnt++;
								}
								String brdrColor = "red";
								if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+gManagerList.get(i).trim()+"_"+hmOrientMemberID.get("GroupHead"))) {
									brdrColor = "green";
								}
								if(isAnonymousReview && strSessionUserType!=null && strSessionUserType.equals(EMPLOYEE)) {
									sbMemList.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "+brdrColor+"\" " +
										" border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-Grand Manager)\"/>");
								} else {
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+gManagerList.get(i).trim()+"','"+hmEmpName.get(gManagerList.get(i).trim())+"')\" >" +
										"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "+brdrColor+"\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+gManagerList.get(i).trim()+"/"+I_16x16+"/"+empImageMap.get(gManagerList.get(i).trim())+"\" border=\"0\" height=\"16px\" " +
										"width=\"16px;\" title=\""+hmEmpName.get(gManagerList.get(i).trim())+"(Role-Grand Manager)\"/></a>");
								}
							}
						}
						hmMemberMP.put(selfList.get(j).trim()+"_"+hmOrientMemberID.get("GroupHead"), sbMemList.toString());
					}
										
					//peer
					if(hmOrientMemberID.get("Peer") != null && memberList!=null && memberList.contains(hmOrientMemberID.get("Peer"))) {
						int cnt=0;
						sbMemList = new StringBuilder();
						List<String> peerList = new ArrayList<String>();
						if(usersList.get(1) != null && !usersList.get(1).equals("")) {
							peerList = Arrays.asList(usersList.get(1).split(","));
						}
						for (int i = 0; peerList != null && !peerList.isEmpty() && i < peerList.size(); i++) {
							if(!peerList.get(i).trim().equals("") && uF.parseToInt(peerList.get(i).trim()) != uF.parseToInt(selfList.get(j).trim())) {
								if(cnt>7) {
									break;
								} else {
									cnt++;
								}
								String brdrColor = "red";
								if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+peerList.get(i).trim()+"_"+hmOrientMemberID.get("Peer"))) {
									brdrColor = "green";
								}
								if(isAnonymousReview && strSessionUserType!=null && strSessionUserType.equals(EMPLOYEE)) {
									sbMemList.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "+brdrColor+"\" " +
										" border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-Peer)\"/>");
								} else {
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+peerList.get(i).trim()+"','"+hmEmpName.get(peerList.get(i).trim())+"')\" >" +
										"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "+brdrColor+"\"  " +
										"data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+peerList.get(i).trim()+"/"+I_16x16+"/"+empImageMap.get(peerList.get(i).trim())+"\" border=\"0\" height=\"16px\" " +
										"width=\"16px;\" title=\""+hmEmpName.get(peerList.get(i).trim())+"(Role-Peer)\"/></a>");
								}
							}
						}
						hmMemberMP.put(selfList.get(j).trim()+"_"+hmOrientMemberID.get("Peer"), sbMemList.toString());
					}
					
					//other peer
					if(hmOrientMemberID.get("Other Peer") != null && memberList!=null && memberList.contains(hmOrientMemberID.get("Other Peer"))) {
						int cnt=0;
						sbMemList = new StringBuilder();
						List<String> otherPeerList = new ArrayList<String>();
						if(usersList.get(2) != null && !usersList.get(2).equals("")) {
							otherPeerList = Arrays.asList(usersList.get(2).split(","));
						}
						for (int i = 0; otherPeerList != null && !otherPeerList.isEmpty() && i < otherPeerList.size(); i++) {
							if(!otherPeerList.get(i).trim().equals("") && uF.parseToInt(otherPeerList.get(i).trim()) != uF.parseToInt(selfList.get(j).trim())) {
								if(cnt>7) {
									break;
								} else {
									cnt++;
								}
								String brdrColor = "red";
								if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+otherPeerList.get(i).trim()+"_"+hmOrientMemberID.get("Other Peer"))){
									brdrColor = "green";
								}
								if(isAnonymousReview && strSessionUserType!=null && strSessionUserType.equals(EMPLOYEE)) {
									sbMemList.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "+brdrColor+"\" " +
										" border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-Other Peer)\"/>");
								} else {
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+otherPeerList.get(i).trim()+"','"+hmEmpName.get(otherPeerList.get(i).trim())+"')\" >" +
										"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "+brdrColor+"\"  " +
										"data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+otherPeerList.get(i).trim()+"/"+I_16x16+"/"+empImageMap.get(otherPeerList.get(i).trim())+"\" border=\"0\" height=\"16px\" " +
										"width=\"16px;\" title=\""+hmEmpName.get(otherPeerList.get(i).trim())+"(Role-Other Peer)\"/></a>");
								}
							}
						}
						hmMemberMP.put(selfList.get(j).trim()+"_"+hmOrientMemberID.get("Other Peer"), sbMemList.toString());
					}
					
					//CEO
					if(hmOrientMemberID.get("CEO") != null && memberList!=null && memberList.contains(hmOrientMemberID.get("CEO"))) {
						int cnt=0;
						sbMemList = new StringBuilder();
						/*if(hmCheckUsers.get(CEO)!=null) {
							List<String> innerList = hmCheckUsers.get(CEO);
							for(int i=0; i<innerList.size(); i++) {
								cnt++;
								sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+innerList.get(i)+"','"+hmEmpName.get(innerList.get(i))+"')\" >" +
									"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\"userImages/"+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+innerList.get(i)+"/"+I_16x16+"/"+empImageMap.get(innerList.get(i))+"\" border=\"0\" height=\"16px\" " +
									"width=\"16px;\" title=\""+hmEmpName.get(innerList.get(i))+"(Role-CEO)\"/></a>");
							}
						} else {*/
							List<String> ceoList = new ArrayList<String>();
							if(usersList.get(7) != null && !usersList.get(7).equals("")) {
								ceoList = Arrays.asList(usersList.get(7).split(","));
							}
							for (int i = 0; ceoList != null && !ceoList.isEmpty() && i < ceoList.size(); i++) {
								if(!ceoList.get(i).trim().equals("")){
									if(cnt>7) {
										break;
									} else {
										cnt++;
									}
									String brdrColor = "red";
									if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+ceoList.get(i).trim()+"_"+hmOrientMemberID.get("CEO"))){
										brdrColor = "green";
									}
									if(isAnonymousReview && strSessionUserType!=null && strSessionUserType.equals(EMPLOYEE)) {
										sbMemList.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "+brdrColor+"\" " +
											" border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-CEO)\"/>");
									} else {
										sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+ceoList.get(i).trim()+"','"+hmEmpName.get(ceoList.get(i).trim())+"')\" >" +
											"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "+brdrColor+"\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+ceoList.get(i).trim()+"/"+I_16x16+"/"+empImageMap.get(ceoList.get(i).trim())+"\" border=\"0\" height=\"16px\" " +
											"width=\"16px;\" title=\""+hmEmpName.get(ceoList.get(i).trim())+"(Role-CEO)\"/></a>");
									}
								}
							}
//						}
						hmMemberMP.put(selfList.get(j).trim()+"_"+hmOrientMemberID.get("CEO"), sbMemList.toString());	
					}
					
					//HOD
					if(hmOrientMemberID.get("HOD") != null && memberList!=null && memberList.contains(hmOrientMemberID.get("HOD"))) {
						int cnt=0;
						sbMemList = new StringBuilder();
						/*if(hmCheckHod.get(HOD)!=null) {
							List<String> innerList = hmCheckHod.get(HOD);
							for(int i=0; i<innerList.size(); i++) {
								cnt++;
								sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+innerList.get(i)+"','"+hmEmpName.get(innerList.get(i))+"')\" >" +
									"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\"userImages/"+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+innerList.get(i)+"/"+I_16x16+"/"+empImageMap.get(innerList.get(i))+"\" border=\"0\" height=\"16px\" " +
									"width=\"16px;\" title=\""+hmEmpName.get(innerList.get(i))+"(Role-HOD)\"/></a>");
							}
						} else {*/
							List<String> hodList = new ArrayList<String>();
							if(usersList.get(6) != null && !usersList.get(6).equals("")) {
								hodList = Arrays.asList(usersList.get(6).split(","));
							}
							for (int i = 0; hodList != null && !hodList.isEmpty() && i < hodList.size(); i++) {
								if(!hodList.get(i).trim().equals("")) {
									if(cnt>7) {
										break;
									} else {
										cnt++;
									}
									String brdrColor = "red";
									if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+hodList.get(i).trim()+"_"+hmOrientMemberID.get("HOD"))) {
										brdrColor = "green";
									}
									if(isAnonymousReview && strSessionUserType!=null && strSessionUserType.equals(EMPLOYEE)) {
										sbMemList.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "+brdrColor+"\" " +
											" border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-HOD)\"/>");
									} else {
										sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+hodList.get(i).trim()+"','"+hmEmpName.get(hodList.get(i).trim())+"')\" >" +
											"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "+brdrColor+"\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+hodList.get(i).trim()+"/"+I_16x16+"/"+empImageMap.get(hodList.get(i).trim())+"\" border=\"0\" height=\"16px\" " +
											"width=\"16px;\" title=\""+hmEmpName.get(hodList.get(i).trim())+"(Role-HOD)\"/></a>");
									}
								}
							}
//						}
						hmMemberMP.put(selfList.get(j).trim()+"_"+hmOrientMemberID.get("HOD"), sbMemList.toString());
					}
					
					
					//other
					if(hmOrientMemberID.get("Anyone") != null && memberList!=null && memberList.contains(hmOrientMemberID.get("Anyone"))) {
						int cnt=0;
						sbMemList = new StringBuilder();
						List<String> othersList = new ArrayList<String>();
						if(usersList.get(8) != null && !usersList.get(8).equals("")) {
							othersList = Arrays.asList(usersList.get(8).split(","));
						}
						for (int i = 0; othersList != null && !othersList.isEmpty() && i < othersList.size(); i++) {
							if(!othersList.get(i).trim().equals("")) {
								if(cnt>7) {
									break;
								} else {
									cnt++;
								}
								String brdrColor = "red";
								if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+othersList.get(i).trim()+"_"+hmOrientMemberID.get("Anyone"))) {
									brdrColor = "green";
								}
								if(isAnonymousReview && strSessionUserType!=null && strSessionUserType.equals(EMPLOYEE)) {
									sbMemList.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "+brdrColor+"\" " +
										" border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-Anyone)\"/>");
								} else {
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+othersList.get(i).trim()+"','"+hmEmpName.get(othersList.get(i).trim())+"')\" >" +
										"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "+brdrColor+"\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+othersList.get(i).trim()+"/"+I_16x16+"/"+empImageMap.get(othersList.get(i).trim())+"\" border=\"0\" height=\"16px\" " +
										"width=\"16px;\" title=\""+hmEmpName.get(othersList.get(i).trim())+"(Role-Anyone)\"/></a>");
								}
							}
						}
						hmMemberMP.put(selfList.get(j).trim()+"_"+hmOrientMemberID.get("Anyone"), sbMemList.toString());
					}
					
					//Sub-ordinate
					if(hmOrientMemberID.get("Sub-ordinate") != null && memberList!=null && memberList.contains(hmOrientMemberID.get("Sub-ordinate"))) {
						int cnt=0;
						sbMemList = new StringBuilder();
						List<String> othersList = new ArrayList<String>();
						if(usersList.get(0) != null && !usersList.get(0).equals("")) {
							othersList = Arrays.asList(usersList.get(0).split(","));
						}
						for (int i = 0; othersList != null && !othersList.isEmpty() && i < othersList.size(); i++) {
							if(!othersList.get(i).trim().equals("")) {
								if(cnt>7) {
									break;
								} else {
									cnt++;
								}
								String brdrColor = "red";
								if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+othersList.get(i).trim()+"_"+hmOrientMemberID.get("Sub-ordinate"))) {
									brdrColor = "green";
								}
								if(isAnonymousReview && strSessionUserType!=null && strSessionUserType.equals(EMPLOYEE)) {
									sbMemList.append("<img class=\"lazy img-circle img-circle\" src=\"userImages/avatar_photo.png\" style=\"border-bottom:2px solid "+brdrColor+"\" " +
										" border=\"0\" height=\"16px\" width=\"16px;\" title=\"Anonymous User (Role-Sub-ordinate)\"/>");
								} else {
									sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+othersList.get(i).trim()+"','"+hmEmpName.get(othersList.get(i).trim())+"')\" >" +
										"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "+brdrColor+"\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+othersList.get(i).trim()+"/"+I_16x16+"/"+empImageMap.get(othersList.get(i).trim())+"\" border=\"0\" height=\"16px\" " +
										"width=\"16px;\" title=\""+hmEmpName.get(othersList.get(i).trim())+"(Role-Sub-ordinate)\"/></a>");
								}
							}
						}
						hmMemberMP.put(selfList.get(j).trim()+"_"+hmOrientMemberID.get("Sub-ordinate"), sbMemList.toString());
					}
					
					/*if(hmOrientMemberID.get("Anyone") != null && memberList!=null && memberList.contains(hmOrientMemberID.get("Anyone"))) {
						String othrIds = "";
						if(other_ids != null && other_ids.trim().length() > 1){
							othrIds = other_ids.substring(1, other_ids.trim().length()-1);
							pst = con.prepareStatement("select emp_per_id from employee_personal_details where  emp_per_id in("+ othrIds + ")");
							rs = pst.executeQuery();
							while (rs.next()) {
								if(cnt>7) {
									break;
								} else {
									cnt++;
								}
								String brdrColor = "red";
								if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+rs.getString("emp_per_id")+"_10")){
									brdrColor = "green";
								}		
								sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+rs.getString("emp_per_id")+"','"+hmEmpName.get(rs.getString("emp_per_id"))+"')\" >" +
									"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid "+brdrColor+"\" data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_16x16+"/"+empImageMap.get(rs.getString("emp_per_id"))+"\" border=\"0\" height=\"16px\" " +
									"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("emp_per_id"))+"(Role-Anyone)\"/></a>");
//								}
							}
							rs.close();
							pst.close();
							
						}
					}*/
					
//					************************ Here code For more user need to implement ********************
					/*if(cnt>7) {
						sbMemList.append("<a href=\"javascript:void(0);\" style=\"margin-right: 4px;\" onclick=\"seeEmpList('"+selfList.get(j)+"','"+id+"','"+getAppFreqId()+"');\" class=\"OR testa\">more..</a>");
					}
					hmMemberMP.put(selfList.get(j).trim(), sbMemList.toString());*/
				}
			}
			
		/*	} else {
				
				
				if(self_ids!=null && !self_ids.equals("")){
					pst=con.prepareStatement("select emp_image,emp_per_id from employee_personal_details ");
					rs=pst.executeQuery();
					Map<String,String> empImageMap=new HashMap<String,String>();
					while(rs.next()){
						empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
					}
					rs.close();
					pst.close();
					
					
					
					getEmpWlocation(self_ids);
					self_ids=self_ids.substring(1,self_ids.length()-1);
					List<String> selfList = Arrays.asList(self_ids.split(","));
					
					for(int j=0;selfList!=null && !selfList.isEmpty() && j<selfList.size();j++){
						
						
						pst=con.prepareStatement("select emp_id,user_id,user_type_id from appraisal_question_answer where appraisal_id=? and emp_id=? " +
								" and appraisal_question_answer_id in(select max(appraisal_question_answer_id) from appraisal_question_answer " +
								" where appraisal_id=? and emp_id=? group by user_type_id,user_id)");
						pst.setInt(1, uF.parseToInt(id));
						pst.setInt(2, uF.parseToInt(selfList.get(j).trim()));
						pst.setInt(3, uF.parseToInt(id));
						pst.setInt(4, uF.parseToInt(selfList.get(j).trim()));
						rs=pst.executeQuery();
//						System.out.println("pst ===========> "+pst); 
						Map<String,String> hmCheckAppraisal=new HashMap<String, String>();
//						Map<String,String> hmCheckHR=new HashMap<String, String>();
//						Map<String,String> hmCheckMgr=new HashMap<String, String>();
						while(rs.next()){
							String key=rs.getString("emp_id")+"_"+rs.getString("user_id")+"_"+rs.getString("user_type_id");
							hmCheckAppraisal.put(key, rs.getString("emp_id"));
							
						}
						rs.close();
						pst.close();
						
						StringBuilder sbMemList=new StringBuilder();
						int cnt=0;
						
						//self
						if(hmOrientMemberID.get("Self") != null && memberList.contains(hmOrientMemberID.get("Self"))) {
							if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+selfList.get(j).trim()+"_3")){
								sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+selfList.get(j)+"/"+I_16x16+"/"+empImageMap.get(selfList.get(j))+"\" border=\"0\" height=\"16px\" " +
													"width=\"16px;\" title=\""+hmEmpName.get(selfList.get(j))+"(Role-SELF)\"/></span>");
							}else{
								sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+selfList.get(j)+"','"+hmEmpName.get(selfList.get(j))+"')\" >" +
										"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid red\"  data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+selfList.get(j)+"/"+I_16x16+"/"+empImageMap.get(selfList.get(j))+"\" border=\"0\" height=\"16px\" " +
												"width=\"16px;\" title=\""+hmEmpName.get(selfList.get(j))+"(Role-SELF)\"/></a>");
							}
						}
						
						
						
						
						//other
						if(hmOrientMemberID.get("Anyone") != null && memberList.contains(hmOrientMemberID.get("Anyone"))) {
							String othrIds = "";
							if(other_ids.trim().length() > 1){
								othrIds = other_ids.substring(1, other_ids.trim().length()-1);
							}
//							System.out.println("othrIds =====> "+othrIds);
								pst = con.prepareStatement("select emp_per_id from employee_personal_details where  emp_per_id in("+ othrIds + ")");
//								System.out.println("pst=====>"+pst);
								rs = pst.executeQuery();
								while (rs.next()) {
									cnt++;
									if(cnt>7){
										sbMemList.append("<a href=\"javascript:void(0);\" style=\"margin-right: 4px;\" onclick=\"seeEmpList('"+selfList.get(j)+"','"+id+"');\" class=\"OR testa\">more..</a>");
										break;
									}
												
									if(hmCheckAppraisal!=null && hmCheckAppraisal.containsKey(selfList.get(j).trim()+"_"+rs.getString("emp_per_id")+"_10")){
										sbMemList.append("<span style=\"margin-right: 4px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid green\"  " +
												"data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_16x16+"/"+empImageMap.get(rs.getString("emp_per_id"))+"\" border=\"0\" height=\"16px\" " +
														"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("emp_per_id"))+"(Role-Anyone)\"/></span>");
									}else{
										sbMemList.append("<a href=\"javascript:void(0)\" style=\"margin-right: 4px;\" onclick=\"getEmpProfile('"+rs.getString("emp_per_id")+"','"+hmEmpName.get(rs.getString("emp_per_id"))+"')\" >" +
												"<img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\"  style=\"border-bottom:2px solid red\"  " +
												"data-original=\""+CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_16x16+"/"+empImageMap.get(rs.getString("emp_per_id"))+"\" border=\"0\" height=\"16px\" " +
														"width=\"16px;\" title=\""+hmEmpName.get(rs.getString("emp_per_id"))+"(Role-Anyone)\"/></a>");
									}
								}
								rs.close();
								pst.close();
						  }
						  hmMemberMP.put(selfList.get(j).trim(), sbMemList.toString());
					 }
				  }
				} */
			
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
		
		Map<String,String> hmEmpSuperVisor=new HashMap<String, String>();
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
			sbQuery.append("select emp_id,supervisor_emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
				" and is_alive= true and emp_per_id >0 order by supervisor_emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("PST ===>> " + pst);
			Map<String, List<String>> hmHireracyLevelEmpIds = new LinkedHashMap<String, List<String>>();
//			List<String> alHireracyLevels = new ArrayList<String>();
			while(rs.next()) {
				List<String> alInner = hmHireracyLevelEmpIds.get(rs.getString("supervisor_emp_id"));
				if(alInner==null) alInner = new ArrayList<String>();
				if(uF.parseToInt(rs.getString("supervisor_emp_id")) == uF.parseToInt(rs.getString("emp_id"))) {
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
			if(strSessionUserType != null && strSessionUserType.equals(MANAGER)) {
				sbQuery = new StringBuilder();
				sbQuery.append("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id  and appraisal_id=? " +
					"and (supervisor_ids like '%,"+strSessionEmpId+",%' or grand_supervisor_ids like '%,"+strSessionEmpId+",%' or hod_ids like '%,"+strSessionEmpId+",%' ");
				if(sbClildEmpIds!=null && !sbClildEmpIds.toString().equals("")) {
					sbQuery.append(" or epd.emp_per_id in ("+sbClildEmpIds.toString()+") ");
				}
				sbQuery.append(") order by emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
//				pst = con.prepareStatement("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id and supervisor_ids like '%,"+strSessionEmpId+",%' and appraisal_id=? order by emp_fname");
				pst.setInt(1, uF.parseToInt(getId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rs.getString("reviewee_id"));
					} else {
						sbEmpId.append(","+rs.getString("reviewee_id"));
					}
				}
				rs.close();
				pst.close();
			}
			
			/*StringBuilder sbEmpId = null;
			if(strSessionUserType != null && strSessionUserType.equals(MANAGER)) {
				pst = con.prepareStatement("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id and supervisor_ids like '%,"+strSessionEmpId+",%' and appraisal_id=? order by emp_fname");
				pst.setInt(1, uF.parseToInt(getId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rs.getString("reviewee_id"));
					} else {
						sbEmpId.append(","+rs.getString("reviewee_id"));
					}
				}
				rs.close();
				pst.close();
			}*/
			
			sbQuery = new StringBuilder();
			sbQuery.append("select sattlement_comment,if_approved,user_id, emp_fname, emp_mname,emp_lname,activity_ids,afs.emp_id,appraisal_id,_date,appraisal_freq_id " +
				"from appraisal_final_sattlement afs,employee_personal_details epd  where afs.user_id = epd.emp_per_id and appraisal_id=? and appraisal_freq_id=? ");
			if(sbEmpId!=null) {
				sbQuery.append(" and emp_id in ("+sbEmpId.toString()+")");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
//			System.out.println("getRemarks pst==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				remark = rs.getString("sattlement_comment");
				if (remark != null) {
					remark = remark.replace("\n", "<br/>");
				}
				flag = uF.parseToBoolean(rs.getString("if_approved"));
				String strEmpMName = "";
				
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				strApprovedBy = rs.getString("emp_fname") +strEmpMName+" " + rs.getString("emp_lname");
			
				hmRemark.put(rs.getString("appraisal_id")+"_"+rs.getString("emp_id"), strApprovedBy+" on "+ uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmRemark", hmRemark);
			
			
			Map<String, String> hmManagerRecommendation = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select recommendation_comment,user_id,emp_fname,emp_mname,emp_lname,emp_id,review_id,entry_date,review_freq_id " +
				"from review_final_recommendation afs, employee_personal_details epd where afs.user_id = epd.emp_per_id and review_id=? and review_freq_id=? ");
			if(sbEmpId!=null) {
				sbQuery.append(" and emp_id in ("+sbEmpId.toString()+")");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
//			System.out.println("getRemarks pst==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				remark = rs.getString("recommendation_comment");
				if (remark != null) {
					remark = remark.replace("\n", "<br/>");
				}
				flag = true;
				String strEmpMName = "";
				
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				strApprovedBy = rs.getString("emp_fname") +strEmpMName+" " + rs.getString("emp_lname");
			
				hmManagerRecommendation.put(rs.getString("review_id")+"_"+rs.getString("emp_id"), strApprovedBy+" on "+ uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
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
		// TODO Auto-generated method stub
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> orientationMemberMp = getOrientationMember();
		con = db.makeConnection(con);
//		String self_ids = null;
		String oriented_type = null;
		try {
			Map<String, String> hmAttributeThreshhold = new HashMap<String, String>();
			pst = con.prepareStatement("select attribute_id,threshhold from appraisal_attribute_level");
			rs = pst.executeQuery(); 
			while (rs.next()) {
				hmAttributeThreshhold.put(rs.getString("attribute_id"), rs.getString("threshhold"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,supervisor_emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
				" and is_alive= true and emp_per_id >0 order by supervisor_emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("PST ===>> " + pst);
			Map<String, List<String>> hmHireracyLevelEmpIds = new LinkedHashMap<String, List<String>>();
//			List<String> alHireracyLevels = new ArrayList<String>();
			while(rs.next()) {
				List<String> alInner = hmHireracyLevelEmpIds.get(rs.getString("supervisor_emp_id"));
				if(alInner==null) alInner = new ArrayList<String>();
				if(uF.parseToInt(rs.getString("supervisor_emp_id")) == uF.parseToInt(rs.getString("emp_id"))) {
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
			if(strSessionUserType != null && strSessionUserType.equals(MANAGER)) {
				sbQuery = new StringBuilder();
				sbQuery.append("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id  and appraisal_id=? " +
					"and (supervisor_ids like '%,"+strSessionEmpId+",%' or grand_supervisor_ids like '%,"+strSessionEmpId+",%' or hod_ids like '%,"+strSessionEmpId+",%' ");
				if(sbClildEmpIds!=null && !sbClildEmpIds.toString().equals("")) {
					sbQuery.append(" or epd.emp_per_id in ("+sbClildEmpIds.toString()+") ");
				}
				sbQuery.append(") order by emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
//				pst = con.prepareStatement("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id and supervisor_ids like '%,"+strSessionEmpId+",%' and appraisal_id=? order by emp_fname");
				pst.setInt(1, uF.parseToInt(getId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rs.getString("reviewee_id"));
					} else {
						sbEmpId.append(","+rs.getString("reviewee_id"));
					}
				}
				rs.close();
				pst.close();
			}
			
			/*StringBuilder sbEmpId = null;
			if(strSessionUserType != null && strSessionUserType.equals(MANAGER)) {
				pst = con.prepareStatement("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id and supervisor_ids like '%,"+strSessionEmpId+",%' and appraisal_id=? order by emp_fname");
				pst.setInt(1, uF.parseToInt(getId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rs.getString("reviewee_id"));
					} else {
						sbEmpId.append(","+rs.getString("reviewee_id"));
					}
				}
				rs.close();
				pst.close();
			}*/
			
			double dblTotalMarks1 = 0;
			double dblTotalWeightage1 = 0;
			double dblTotalAggregate1 = 0;
			Map<String, String> hmScoreAggregateMap = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(marks) as marks, sum(weightage) as weightage, aqw.appraisal_attribute, aqw.emp_id from appraisal_question_answer aqw where aqw.appraisal_id=? and aqw.appraisal_freq_id=? and aqw.is_submit=true");
			if(sbEmpId!=null) {
				sbQuery.append(" and emp_id in ("+sbEmpId.toString()+")");
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

				if(!attribIdList.contains(rs.getString("appraisal_attribute"))) {
					attribIdList.add(rs.getString("appraisal_attribute"));
				}
				hmScoreAggregateMap.put(rs.getString("emp_id")+"_"+rs.getString("appraisal_attribute"), uF.showData("" + dblTotalAggregate1, "0"));
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
			if(hmRevieweewiseMemberCount == null) hmRevieweewiseMemberCount = new HashMap<String, String>();
			
			Map<String, String> hmDesignation = CF.getDesigMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			Map<String, String> hmLevelMap = getLevelMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			Map<String, String> orientationMp = getOrientationValue(con);
			/*pst = con.prepareStatement("select * from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id " 
					+ " and (is_delete is null or is_delete = false) and appraisal_details_id =?");*/
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
//			System.out.println("==>pstAppraisaldetails"+pst);
			Map<String, String> appraisalMp = new HashMap<String, String>();
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			
//			List<String> alUserTypeForFeedback = new ArrayList<String>();
			String strUserTypesForFeedback = null;
			while (rs.next()) {
				List<String> memberList = new ArrayList<String>();
				if(rs.getString("usertype_member") != null && !rs.getString("usertype_member").equals("")) {
					memberList = Arrays.asList(rs.getString("usertype_member").split(","));
				}
				String memberName = "";
				for (int i=0; memberList!=null && !memberList.isEmpty() && i<memberList.size(); i++) {
					if (i == 0) {
						memberName += orientationMemberMp.get(memberList.get(i));
					} else {
						memberName += ", " + orientationMemberMp.get(memberList.get(i));
					}
				}
				if(memberName == null || memberName.equals("null")) {
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
				if(rs.getString("usertype_member") != null && rs.getString("usertype_member").length()>0) {
					List<String> alAppraiserMember = Arrays.asList(rs.getString("usertype_member").split(","));
					for(int i=0; alAppraiserMember != null && i<alAppraiserMember.size(); i++) {
						if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
							sbAppraisers.append("Managers: " + uF.showData(getAppendData(rs.getString("supervisor_id"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(HOD))) {
							sbAppraisers.append("HODs: " + uF.showData(getAppendData(rs.getString("hod_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(CEO))) {
							sbAppraisers.append("CEOs: " + uF.showData(getAppendData(rs.getString("ceo_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(HRMANAGER))) {
							sbAppraisers.append("HRs: " + uF.showData(getAppendData(rs.getString("hr_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(ADMIN))) {
							sbAppraisers.append("Global HRs: " + uF.showData(getAppendData(rs.getString("hr_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == 4) {
							sbAppraisers.append("Peers: " + uF.showData(getAppendData(rs.getString("peer_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == 10) {
							sbAppraisers.append("Anyone: " + uF.showData(getAppendData(rs.getString("other_ids"), hmEmpName), "N/A")+"</br>");
						}
					}
				}
				appraisalMp.put("APPRAISER", uF.showData(sbAppraisers.toString(), ""));
				appraisalMp.put("REVIEWER", uF.showData(getAppendData(rs.getString("reviewer_id"), hmEmpName), ""));
				if(rs.getString("user_types_for_feedback") != null) {
//					alUserTypeForFeedback = Arrays.asList(rs.getString("user_types_for_feedback").split(","));
					strUserTypesForFeedback = rs.getString("user_types_for_feedback").substring(1, (rs.getString("user_types_for_feedback").length()-1));
				}
				request.setAttribute("memberList", memberList);
			}
			rs.close();
			pst.close();
						
//			request.setAttribute("memberCount", memberCount);			
			pst = con.prepareStatement("select * from appraisal_details_frequency  where (is_delete =false or is_delete is null) and appraisal_id = ? and appraisal_freq_id =?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
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
			String empids = appraisalMp.get("SELFID")!=null && !appraisalMp.get("SELFID").equals("") ? appraisalMp.get("SELFID").substring(1, appraisalMp.get("SELFID").length()-1) : "";
			Map<String, String> empImageMap = new HashMap<String, String>();
			if(empids != null && !empids.equals("") && !empids.equalsIgnoreCase("null")) {
				sbQuery = new StringBuilder();
				sbQuery.append("select emp_image,emp_per_id from employee_personal_details where emp_per_id>0 ");
				if(sbEmpId!=null) {
					sbQuery.append(" and emp_per_id in ("+sbEmpId.toString()+")");
				} else {
					sbQuery.append(" and emp_per_id in ("+empids+")");
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
			
			alInnerExport.add(new DataStyle("Balance Score Card_"+appraisalMp.get("APPRAISAL"), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			alInnerExport = new ArrayList<DataStyle>();	
			alInnerExport.add(new DataStyle(appraisalMp.get("APPRAISAL"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
		
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Review Type:",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(appraisalMp.get("APPRAISALTYPE"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Description:",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(appraisalMp.get("DESCRIPTION"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Frequency:",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(appraisalMp.get("FREQUENCY"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Effective Date: ",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(appraisalMp.get("APP_FREQ_FROM"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Due Date:",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(appraisalMp.get("APP_FREQ_TO"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			
			
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Orientation",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(appraisalMp.get("ORIENT"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			
			
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Score Cards",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			
			
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Department",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Level",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Designation",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Location",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));						
			
			/*pst = con.prepareStatement("select self_ids,appraisal_details_id,oriented_type from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
//			System.out.println("pst =========>> " + pst);
			while (rs.next()) {
				self_ids = rs.getString("self_ids");
				// appraisal_details_id = rs.getInt(2);
				oriented_type = rs.getString(3);
				
			}
			rs.close();
			pst.close();*/
			
			List<String> empList = new ArrayList<String>();
			if(getType() != null && getType().equals("KRATARGET")) {
				empList.add(strSessionEmpId);
			} else if(sbEmpId!=null) {
				empList = Arrays.asList(sbEmpId.toString().split(","));
			} else {
				pst = con.prepareStatement("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id and appraisal_id=? order by emp_fname");
				pst.setInt(1, uF.parseToInt(id));
				rs = pst.executeQuery();
//				System.out.println("pst 1 =========>> " + pst);
				while (rs.next()) {
					empList.add(rs.getString("reviewee_id"));
				}
				rs.close();
				pst.close();
			}
			
//			System.out.println("self_ids =========>> " + self_ids);
			/*self_ids=self_ids!=null && !self_ids.equals("") ? self_ids.substring(1, self_ids.length()-1) :null;
			List<String> empList = new ArrayList<String>();
			if(getType() != null && getType().equals("KRATARGET")) {
				empList.add(strSessionEmpId);
			} else if(sbEmpId!=null) {
				empList = Arrays.asList(sbEmpId.toString().split(","));
			} else {
				if(self_ids != null) {
					empList = Arrays.asList(self_ids.split(","));
				}
			}*/
//			System.out.println("empList report ===>> " + empList);
			// Map<String, String> hmUserTypeID = new HashMap<String, String>();
			// pst = con
			// .prepareStatement("select user_type_id,user_type from user_type");
			// rs = pst.executeQuery();
			// while (rs.next()) {
			// hmUserTypeID.put(rs.getString(2), rs.getString(1));
			// }

//			pst = con.prepareStatement("select *,(marks*100/weightage) as average from(select sum(marks) as marks ,sum(weightage) as weightage," +
//				"user_type_id,emp_id from appraisal_question_answer where appraisal_id=? and appraisal_freq_id=? and weightage>0 " +
//				"and reviewer_or_appraiser = 0 group by user_type_id,emp_id)as a order by emp_id ");
			
			sbQuery = new StringBuilder();
			sbQuery.append("select *,(marks*100/weightage) as average, (reviewer_marks*100/weightage) as reviewer_average from (select sum(marks) as marks, " +
				" sum(weightage) as weightage, sum(reviewer_marks) as reviewer_marks,user_type_id,emp_id from appraisal_question_answer where appraisal_id=? " +
				" and appraisal_freq_id=? and weightage>0 and reviewer_or_appraiser=0 and is_submit=true ");
			if(sbEmpId!=null) {
				sbQuery.append(" and emp_id in ("+sbEmpId.toString()+") ");
			}
			sbQuery.append(" group by user_type_id,emp_id) as a order by emp_id ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
//			System.out.println("pst1 ===>> " + pst);
			rs = pst.executeQuery();
//			System.out.println("pst ===>> "+pst);
			Map<String, Map<String, String>> outerMp = new HashMap<String, Map<String, String>>();
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			double dblTotalMarks = 0; 
			double dblTotalWeightage = 0;
			Map<String, String> hmEmpTotMarksWeightage = new HashMap<String, String>();
			while (rs.next()) {
				strEmpIdNew = rs.getString("emp_id");
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					dblTotalMarks = 0;
					dblTotalWeightage = 0;
				}
				
				if(rs.getDouble("reviewer_marks") > 0) {
					dblTotalMarks += uF.parseToDouble(rs.getString("reviewer_marks"));
				} else {
					dblTotalMarks += uF.parseToDouble(rs.getString("marks"));
				}
//				System.out.println("dblTotalMarks"+dblTotalMarks);
				
				dblTotalWeightage += uF.parseToDouble(rs.getString("weightage"));
				Map<String, String> value = outerMp.get(rs.getString("emp_id"));
				if (value == null)
					value = new HashMap<String, String>();
				value.put(rs.getString("user_type_id"), uF.formatIntoTwoDecimal(rs.getDouble("average")));
				value.put(rs.getString("user_type_id")+"_REVIEWER", uF.formatIntoTwoDecimal(rs.getDouble("reviewer_average")));
				if(dblTotalWeightage>0){
					value.put("AGGREGATE", uF.formatIntoTwoDecimal((dblTotalMarks * 100)/dblTotalWeightage));
				}
				hmEmpTotMarksWeightage.put(rs.getString("emp_id")+"_TOT_MARKS", dblTotalMarks+"");
				hmEmpTotMarksWeightage.put(rs.getString("emp_id")+"_TOT_WEIGHTAGE", dblTotalWeightage+"");
				
				outerMp.put(rs.getString("emp_id"), value);
				
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpTotMarksWeightage =====>> " + hmEmpTotMarksWeightage);
			
			// request.setAttribute("hmUserTypeID", hmUserTypeID);
			request.setAttribute("outerMp", outerMp);
			request.setAttribute("empList", empList);
			request.setAttribute("oriented_type", oriented_type);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select *,(marks*100/weightage) as average from (select sum(marks) as marks ,sum(weightage) as weightage,user_type_id,emp_id " +
				"from appraisal_question_answer where appraisal_id=? and appraisal_freq_id=? and weightage>0 and reviewer_or_appraiser=1 and is_submit=true");
			if(sbEmpId!=null) {
				sbQuery.append(" and emp_id in ("+sbEmpId.toString()+") ");
			}
			sbQuery.append(" group by user_type_id,emp_id) as a order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
//			pst.setInt(3, uF.parseToInt(getEmpID()));
//			System.out.println("pst2==>"+pst);
			rs = pst.executeQuery();
//					System.out.println("pst ========> "+pst);
			Map<String, Map<String, String>> reviewerOutMp = new HashMap<String, Map<String, String>>();
			String strEmpIdNewReviewer = null;
			String strEmpIdOldReviewer = null;
			double dblTotalMarksReviewer = 0; 
			double dblTotalWeightageReviewer = 0;
			while (rs.next()) {
				strEmpIdNewReviewer = rs.getString("emp_id");
				if(strEmpIdNewReviewer!=null && !strEmpIdNewReviewer.equalsIgnoreCase(strEmpIdOldReviewer)) {
					dblTotalMarksReviewer = uF.parseToDouble(hmEmpTotMarksWeightage.get(rs.getString("emp_id")+"_TOT_MARKS"));
					dblTotalWeightageReviewer = uF.parseToDouble(hmEmpTotMarksWeightage.get(rs.getString("emp_id")+"_TOT_WEIGHTAGE"));
				}
				dblTotalMarksReviewer += uF.parseToDouble(rs.getString("marks"));
//						System.out.println("dblTotalMarks"+dblTotalMarks);
				dblTotalWeightageReviewer += uF.parseToDouble(rs.getString("weightage"));
				Map<String, String> value = reviewerOutMp.get(rs.getString("emp_id"));
				if (value == null)
					value = new HashMap<String, String>();
				value.put("REVIEWER", uF.formatIntoTwoDecimal(rs.getDouble("average")));
				value.put("REVIEWER_USERTYPE", rs.getString("user_type_id"));
				if(dblTotalWeightageReviewer>0) {
					value.put("AGGREGATE", uF.formatIntoTwoDecimal((dblTotalMarksReviewer * 100)/dblTotalWeightageReviewer));
				}
				reviewerOutMp.put(rs.getString("emp_id"), value);
				
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
//			System.out.println("reviewerOutMp==>"+reviewerOutMp);
			// request.setAttribute("hmUserTypeID", hmUserTypeID);
			request.setAttribute("reviewerOutMp", reviewerOutMp);
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as count,emp_id,appraisal_id,appraisal_freq_id from (select emp_id,appraisal_id,user_id," +
				"user_type_id,appraisal_freq_id from appraisal_question_answer where appraisal_id=? and appraisal_freq_id=? and is_submit=true ");
			if(sbEmpId!=null) {
				sbQuery.append(" and emp_id in ("+sbEmpId.toString()+") ");
			}
			if(strUserTypesForFeedback != null && strUserTypesForFeedback.length()>0) {
				sbQuery.append(" and user_type_id not in ("+strUserTypesForFeedback+") ");
			}
			sbQuery.append(" and reviewer_or_appraiser=0 group by emp_id,user_id,user_type_id,appraisal_id,appraisal_freq_id) as a group by emp_id,appraisal_id,appraisal_freq_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));		
			pst.setInt(2, uF.parseToInt(getAppFreqId()));		
//			System.out.println("hmEmpCount pst3==>"+pst);
//			pst = con.prepareStatement("select count(*) as count,emp_id,appraisal_id,appraisal_freq_id from (select emp_id,appraisal_id,user_type_id,appraisal_freq_id from appraisal_question_answer "
//					+ "where appraisal_id=? and appraisal_freq_id = ? and reviewer_or_appraiser=0 group by emp_id,user_type_id,appraisal_id,appraisal_freq_id)as a group by emp_id,appraisal_id,appraisal_freq_id");
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getId()));		
//			pst.setInt(2, uF.parseToInt(getAppFreqId()));		
//			System.out.println("hmEmpCount pst3==>"+pst);
			rs = pst.executeQuery();
			Map<String,String> hmEmpCount=new HashMap<String, String>();
			while (rs.next()) {
				hmEmpCount.put(rs.getString("emp_id"), rs.getString("count"));				
			}		
			rs.close();
			pst.close();
//			System.out.println("hmEmpCount ===>> " + hmEmpCount);
			
			request.setAttribute("hmEmpCount", hmEmpCount);
			
			
			sbQuery = new StringBuilder();
//			select * from reviewee_strength_improvements rsi, appraisal_reviewee_details ard where rsi.emp_id= ard.reviewee_id and rsi.review_freq_id=ard.appraisal_freq_id and rsi.review_id=25 and rsi.review_freq_id=24  order by emp_id,user_id,user_type_id
			sbQuery.append("select * from reviewee_strength_improvements rsi, appraisal_reviewee_details ard where rsi.emp_id= ard.reviewee_id and rsi.review_freq_id=ard.appraisal_freq_id and rsi.review_id=? and rsi.review_freq_id=? ");
			sbQuery.append(" order by emp_id,user_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));		
			pst.setInt(2, uF.parseToInt(getAppFreqId()));		
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			Map<String,StringBuilder> hmEmpwiseStrengthAndImprovements = new HashMap<String, StringBuilder>();
			while (rs.next()) {
				if(rs.getString("areas_of_strength")!=null && !rs.getString("areas_of_strength").equals("") && !rs.getString("areas_of_strength").equals("''") && !rs.getString("areas_of_strength").equalsIgnoreCase("N/A")) {
					StringBuilder sbStrengthData = hmEmpwiseStrengthAndImprovements.get(rs.getString("emp_id")+"_STRENGTH");
					if(sbStrengthData==null) {
						sbStrengthData = new StringBuilder();
						sbStrengthData.append(hmEmpName.get(rs.getString("user_id"))+": "+rs.getString("areas_of_strength"));
					} else {
						sbStrengthData.append("\n"+hmEmpName.get(rs.getString("user_id"))+": "+rs.getString("areas_of_strength"));
					}
					hmEmpwiseStrengthAndImprovements.put(rs.getString("emp_id")+"_STRENGTH", sbStrengthData);
				}
				
				if(rs.getString("areas_of_improvement")!=null && !rs.getString("areas_of_improvement").equals("") && !rs.getString("areas_of_improvement").equals("''") && !rs.getString("areas_of_improvement").equalsIgnoreCase("N/A")) {
					StringBuilder sbImprovementsData = hmEmpwiseStrengthAndImprovements.get(rs.getString("emp_id")+"_IMPROVEMENTS");
					if(sbImprovementsData==null) {
						sbImprovementsData = new StringBuilder();
						sbImprovementsData.append(hmEmpName.get(rs.getString("user_id"))+": "+rs.getString("areas_of_improvement"));
					} else {
						sbImprovementsData.append("\n"+hmEmpName.get(rs.getString("user_id"))+": "+rs.getString("areas_of_improvement"));
					}
					hmEmpwiseStrengthAndImprovements.put(rs.getString("emp_id")+"_IMPROVEMENTS", sbImprovementsData);
				}
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,user_id,user_type_id,section_id,section_comment from appraisal_question_answer aqa, appraisal_reviewee_details ard where aqa.emp_id= ard.reviewee_id and " +
				" aqa.appraisal_freq_id=ard.appraisal_freq_id and aqa.user_type_id=2 and aqa.appraisal_id=? and aqa.appraisal_freq_id=? group by aqa.emp_id,aqa.user_id,aqa.user_type_id," +
				" aqa.section_id,aqa.section_comment order by aqa.emp_id,aqa.user_id,aqa.section_id,aqa.user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));		
			pst.setInt(2, uF.parseToInt(getAppFreqId()));		
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			Map<String, StringBuilder> hmEmpwiseManagerCommentsOfSections = new HashMap<String, StringBuilder>();
			Map<String, String> hmEmpwiseUserwiseManagerCommentsOfSections = new HashMap<String, String>();
			while (rs.next()) {
				if(rs.getString("section_comment")!=null && !rs.getString("section_comment").equals("") && !rs.getString("section_comment").equals("''") && !rs.getString("section_comment").equalsIgnoreCase("N/A") && !rs.getString("section_comment").equalsIgnoreCase("-")) {
					String strComment = hmEmpwiseUserwiseManagerCommentsOfSections.get(rs.getString("emp_id")+"_"+rs.getString("user_id"));
					StringBuilder sbCommentsData = hmEmpwiseStrengthAndImprovements.get(rs.getString("emp_id"));
					if(sbCommentsData==null) {
						sbCommentsData = new StringBuilder();
						sbCommentsData.append(hmEmpName.get(rs.getString("user_id"))+": "+rs.getString("section_comment"));
					} else {
						if(strComment==null) {
							sbCommentsData.append("\n"+hmEmpName.get(rs.getString("user_id"))+": "+rs.getString("section_comment"));
						} else {
							sbCommentsData.append("\n"+rs.getString("section_comment"));
						}
					}
					hmEmpwiseUserwiseManagerCommentsOfSections.put(rs.getString("emp_id")+"_"+rs.getString("user_id"), rs.getString("section_comment"));
					hmEmpwiseManagerCommentsOfSections.put(rs.getString("emp_id"), sbCommentsData);
				}
			}		
			rs.close();
			pst.close();
			
			Map<String, String> locationMp = (Map<String, String>) request.getAttribute("locationMp");
			List<String> memberList = (List<String>) request.getAttribute("memberList"); 
			Map<String, String> hmEmpWlocationMap=CF.getEmpWlocationMap(con);
			Map<String, String> hmEmpDepartment = CF.getEmpDepartmentNameMap(con);
			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelName = CF.getLevelMap(con);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			
			for (int i=0; memberList!=null && i<memberList.size(); i++) {
//				System.out.println("MemberList"+orientationMemberMp.get(memberList.get(i)));
				alInnerExport.add(new DataStyle(orientationMemberMp.get(memberList.get(i)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			}
			alInnerExport.add(new DataStyle("Balanced Score", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Overall Comments", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Area of Strength", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Area of Improvement", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			reportListExport.add(alInnerExport);
			for (int i = 0; empList != null && i < empList.size(); i++) {
				int memberCount = uF.parseToInt(hmRevieweewiseMemberCount.get(empList.get(i)));
				alInnerExport = new ArrayList<DataStyle>();
				if(uF.parseToInt(empList.get(i))>0) {
                	Map<String, String> value = outerMp.get(empList.get(i).trim());
             		if (value == null)
             			value = new HashMap<String, String>();
             			alInnerExport.add(new DataStyle(hmEmpCode.get(empList.get(i)),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
           		  		alInnerExport.add(new DataStyle(hmEmpName.get(empList.get(i)),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
           		  		alInnerExport.add(new DataStyle(hmEmpDepartment.get(empList.get(i)),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
           		  		alInnerExport.add(new DataStyle(hmLevelName.get(hmEmpLevelId.get(empList.get(i))),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
           		  		alInnerExport.add(new DataStyle(hmEmpCodeDesig.get(empList.get(i)),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
           		  		alInnerExport.add(new DataStyle(locationMp.get(empList.get(i)),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
             		  
             		  //uF.showData(uF.getRoundOffValue(2,uF.parseToDouble(value.get("AGGREGATE"))), "NA");				   
//				     System.out.println("EmpName==>"+hmEmpName.get(empList.get(i))+"EmpCode==>"+hmEmpCode.get(empList.get(i))+"EmpDsignation==>"+hmEmpCodeDesig.get(empList.get(i))+"EmpLocation"+locationMp.get(empList.get(i))+"value"+uF.showData(uF.getRoundOffValue(2,uF.parseToDouble(value.get("AGGREGATE"))), "NA"));
           		  		int addOneCnt=0;
				        for (int j=0; memberList!=null && j<memberList.size(); j++) {
//				        	if(uF.parseToInt(empList.get(i).trim())==725 || uF.parseToInt(empList.get(i).trim())==607) {
//					        	System.out.println("uF.parseToInt(memberList.get(j)) ===>> " + uF.parseToInt(memberList.get(j)));
//					        }
				        	if(uF.parseToInt(memberList.get(j)) == 3) {
                          		addOneCnt=1;
                          	  }
				        	if(value.get(memberList.get(j).trim())!=null) {
//					        	alInnerExport.add(new DataStyle(uF.showData(value.get(memberList.get(j)),"0")+"%",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				        		double dblRating = uF.parseToDouble(value.get(memberList.get(j))) / 20;
				        		alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoOneDecimalIfDecimalValIsThere(dblRating), "0"), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					        } else {
//					        	alInnerExport.add(new DataStyle("0%",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					        	alInnerExport.add(new DataStyle("0",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					        }
						}
				        
//                        boolean flag = false;
                      /*  StringBuilder attribIds = new StringBuilder();
                        int attribCnt = 0;
                        int aggregateCnt = 0;
                        for(int a=0; attribIdList != null && !attribIdList.isEmpty() && a<attribIdList.size(); a++) {
                        	double aggregate = uF.parseToDouble(hmScoreAggregateMap.get(empList.get(i).trim()+"_"+attribIdList.get(a)));
                      
                        	if(aggregate < uF.parseToDouble(hmAttributeThreshhold.get(attribIdList.get(a)))) {
                        	
                        		attribIds.append(attribIdList.get(a)+"::");
                        		aggregateCnt++;
                        	}
                        	attribCnt++;
                        }*/
                       
//                        if(attribCnt == aggregateCnt) {
//                        	flag = true;
//                        }                                                   
//                        String aggregate="0.0"; 725,607
//				        if(uF.parseToInt(empList.get(i).trim())==725 || uF.parseToInt(empList.get(i).trim())==607) {
//				        	System.out.println("(memberCount+addOneCnt) ===>> " + (memberCount+addOneCnt));
//				        	System.out.println("addOneCnt ===>> " + addOneCnt);
//				        	System.out.println("hmEmpCount.get(empList.get(i).trim()) ===>> " + hmEmpCount.get(empList.get(i).trim()));
//				        }
                        if((memberCount+addOneCnt)==uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))) {
//                        	aggregate = value.get("AGGREGATE") != null ? uF.parseToDouble(value.get("AGGREGATE")) / 20 + "" : "0";
                        	double dblRating = uF.parseToDouble(value.get("AGGREGATE")) / 20;
                        	alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoOneDecimalIfDecimalValIsThere(dblRating), "0"), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
                        } else { 
                        	alInnerExport.add(new DataStyle("NA", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
                        }
                        alInnerExport.add(new DataStyle(hmEmpwiseManagerCommentsOfSections.get(empList.get(i).trim())!=null ? uF.showData(hmEmpwiseManagerCommentsOfSections.get(empList.get(i).trim()).toString(), "NA") : "", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
                        alInnerExport.add(new DataStyle(hmEmpwiseStrengthAndImprovements.get(empList.get(i).trim()+"_STRENGTH")!=null ? uF.showData(hmEmpwiseStrengthAndImprovements.get(empList.get(i).trim()+"_STRENGTH").toString(), "NA") : "", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
                        alInnerExport.add(new DataStyle(hmEmpwiseStrengthAndImprovements.get(empList.get(i).trim()+"_IMPROVEMENTS")!=null ? uF.showData(hmEmpwiseStrengthAndImprovements.get(empList.get(i).trim()+"_IMPROVEMENTS").toString(), "NA") : "", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
                        
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
	
	private void getAppraisalStatus(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> orientationMemberMp = getOrientationMember();
		
//		String self_ids = null;
		String oriented_type = null;
		try {

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			Map<String, String> hmAttributeThreshhold = new HashMap<String, String>();
			pst = con.prepareStatement("select attribute_id,threshhold from appraisal_attribute_level");
//			pst=con.prepareStatement(selectAttribute);
			rs = pst.executeQuery(); 
			while (rs.next()) {
				hmAttributeThreshhold.put(rs.getString("attribute_id"), rs.getString("threshhold"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,supervisor_emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
				" and is_alive= true and emp_per_id >0 order by supervisor_emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("PST ===>> " + pst);
			Map<String, List<String>> hmHireracyLevelEmpIds = new LinkedHashMap<String, List<String>>();
//			List<String> alHireracyLevels = new ArrayList<String>();
			while(rs.next()) {
				List<String> alInner = hmHireracyLevelEmpIds.get(rs.getString("supervisor_emp_id"));
				if(alInner==null) alInner = new ArrayList<String>();
				if(uF.parseToInt(rs.getString("supervisor_emp_id")) == uF.parseToInt(rs.getString("emp_id"))) {
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
			if(strSessionUserType != null && strSessionUserType.equals(MANAGER)) {
				sbQuery = new StringBuilder();
				sbQuery.append("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id  and appraisal_id=? " +
					"and (supervisor_ids like '%,"+strSessionEmpId+",%' or grand_supervisor_ids like '%,"+strSessionEmpId+",%' or hod_ids like '%,"+strSessionEmpId+",%' ");
				if(sbClildEmpIds!=null && !sbClildEmpIds.toString().equals("")) {
					sbQuery.append(" or epd.emp_per_id in ("+sbClildEmpIds.toString()+") ");
				}
				sbQuery.append(") order by emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
//				pst = con.prepareStatement("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id and supervisor_ids like '%,"+strSessionEmpId+",%' and appraisal_id=? order by emp_fname");
				pst.setInt(1, uF.parseToInt(getId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rs.getString("reviewee_id"));
					} else {
						sbEmpId.append(","+rs.getString("reviewee_id"));
					}
				}
				rs.close();
				pst.close();
			}
			

			double dblTotalMarks1 = 0;
			double dblTotalWeightage1 = 0;
			double dblTotalAggregate1 = 0;
			Map<String, String> hmScoreAggregateMap = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(marks) as marks, sum(weightage) as weightage, aqw.appraisal_attribute, aqw.emp_id from appraisal_question_answer aqw " +
				" where aqw.appraisal_id=? and aqw.appraisal_freq_id=? and aqw.is_submit=true ");
			if(sbEmpId!=null) {
				sbQuery.append(" and emp_id in ("+sbEmpId.toString()+")");
			}
			sbQuery.append(" group by aqw.appraisal_attribute,aqw.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(id));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
//			System.out.println("getAppraisalStatus pst1==>"+pst);
			rs = pst.executeQuery();
			List<String> attribIdList = new ArrayList<String>();
			while (rs.next()) {

				dblTotalMarks1 = uF.parseToDouble(rs.getString("marks"));
				dblTotalWeightage1 = uF.parseToDouble(rs.getString("weightage"));
				dblTotalAggregate1 = uF.parseToDouble(uF.formatIntoTwoDecimal(((dblTotalMarks1 / dblTotalWeightage1) * 100)));

				if(!attribIdList.contains(rs.getString("appraisal_attribute"))) {
					attribIdList.add(rs.getString("appraisal_attribute"));
				}
				hmScoreAggregateMap.put(rs.getString("emp_id")+"_"+rs.getString("appraisal_attribute"), uF.showData("" + dblTotalAggregate1, "0"));
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
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			Map<String, String> orientationMp = getOrientationValue(con);
			/*pst = con.prepareStatement("select * from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id " 
					+ " and (is_delete is null or is_delete = false) and appraisal_details_id =?");*/
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
//			System.out.println("==>pstAppraisaldetails"+pst);
			Map<String, String> appraisalMp = new HashMap<String, String>();
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			int memberCount=0;
			List<String> alUserTypeForFeedback = new ArrayList<String>();
			String strUserTypesForFeedback = null;
			while (rs.next()) {

				List<String> memberList = new ArrayList<String>();

				if(rs.getString("usertype_member") != null && !rs.getString("usertype_member").equals("")) {
					memberList = Arrays.asList(rs.getString("usertype_member").split(","));
				}
				String memberName = "";
				
				for (int i = 0; memberList != null && !memberList.isEmpty() && i < memberList.size(); i++) {
					if (i == 0){
						memberName += orientationMemberMp.get(memberList.get(i));
					}else{
						memberName += ", " + orientationMemberMp.get(memberList.get(i));
					}
					memberCount++;
				}
				if(memberName == null || memberName.equals("null")){
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
				if(rs.getString("usertype_member") != null && rs.getString("usertype_member").length()>0) {
					List<String> alAppraiserMember = Arrays.asList(rs.getString("usertype_member").split(","));
					for(int i=0; alAppraiserMember != null && i<alAppraiserMember.size(); i++) {
						if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
							sbAppraisers.append("Managers: " + uF.showData(getAppendData(rs.getString("supervisor_id"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(HOD))) {
							sbAppraisers.append("HODs: " + uF.showData(getAppendData(rs.getString("hod_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(CEO))) {
							sbAppraisers.append("CEOs: " + uF.showData(getAppendData(rs.getString("ceo_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(HRMANAGER))) {
							sbAppraisers.append("HRs: " + uF.showData(getAppendData(rs.getString("hr_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(ADMIN))) {
							sbAppraisers.append("Global HRs: " + uF.showData(getAppendData(rs.getString("hr_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == 4) {
							sbAppraisers.append("Peers: " + uF.showData(getAppendData(rs.getString("peer_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == 10) {
							sbAppraisers.append("Anyone: " + uF.showData(getAppendData(rs.getString("other_ids"), hmEmpName), "N/A")+"</br>");
						}
					}
				}
				appraisalMp.put("APPRAISER", uF.showData(sbAppraisers.toString(), ""));
				appraisalMp.put("REVIEWER", uF.showData(getAppendData(rs.getString("reviewer_id"), hmEmpName), ""));
				appraisalMp.put("ANONYMOUS_REVIEW", uF.showData(rs.getString("is_anonymous_review"), ""));
				
				if(rs.getString("user_types_for_feedback") != null) {
					alUserTypeForFeedback = Arrays.asList(rs.getString("user_types_for_feedback").split(","));
					strUserTypesForFeedback = rs.getString("user_types_for_feedback").substring(1, (rs.getString("user_types_for_feedback").length()-1));
				}
				
				request.setAttribute("memberList", memberList);
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from appraisal_reviewee_details where appraisal_id=? ");
			if(sbEmpId!=null) {
				sbQuery.append(" and reviewee_id in ("+sbEmpId.toString()+")");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
//			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			Map<String, String> hmRevieweewiseMemberCount = new HashMap<String, String>();
			while (rs.next()) {
				StringBuilder sbAllUserIds = new StringBuilder();
				if(!alUserTypeForFeedback.contains("6")) {
					sbAllUserIds.append(rs.getString("subordinate_ids")!=null ? rs.getString("subordinate_ids") : "");
				}
				if(!alUserTypeForFeedback.contains("4")) {
					sbAllUserIds.append(rs.getString("peer_ids")!=null ? rs.getString("peer_ids") : "");
				}
				if(!alUserTypeForFeedback.contains("14")) {
					sbAllUserIds.append(rs.getString("other_peer_ids")!=null ? rs.getString("other_peer_ids") : "");
				}
				if(!alUserTypeForFeedback.contains("2")) {
					sbAllUserIds.append(rs.getString("supervisor_ids")!=null ? rs.getString("supervisor_ids") : "");
				}
				if(!alUserTypeForFeedback.contains("8")) {
					sbAllUserIds.append(rs.getString("grand_supervisor_ids")!=null ? rs.getString("grand_supervisor_ids") : "");
				}
				if(!alUserTypeForFeedback.contains("13")) {
					sbAllUserIds.append(rs.getString("hod_ids")!=null ? rs.getString("hod_ids") : "");
				}
				if(!alUserTypeForFeedback.contains("5")) {
					sbAllUserIds.append(rs.getString("ceo_ids")!=null ? rs.getString("ceo_ids") : "");
				}
				if(!alUserTypeForFeedback.contains("7")) {
					sbAllUserIds.append(rs.getString("hr_ids")!=null ? rs.getString("hr_ids") : "");
				}
				if(!alUserTypeForFeedback.contains("1")) {
					sbAllUserIds.append(rs.getString("ghr_ids")!=null ? rs.getString("ghr_ids") : "");
				}
				if(!alUserTypeForFeedback.contains("15")) {
					sbAllUserIds.append(rs.getString("recruiter_ids")!=null ? rs.getString("recruiter_ids") : "");
				}
				if(!alUserTypeForFeedback.contains("10")) {
					sbAllUserIds.append(rs.getString("other_ids")!=null ? rs.getString("other_ids") : "");
				}
//				sbAllUserIds.append(","+rs.getString("reviewee_id"));
				List<String> al = new ArrayList<String>();
				al = Arrays.asList(sbAllUserIds.toString().split(","));
				int cnt=0;
				for(int i=0; i<al.size(); i++) {
					if(uF.parseToInt(al.get(i).trim())>0) {
						cnt++;
					}
				}
				hmRevieweewiseMemberCount.put(rs.getString("reviewee_id"), cnt+"");
			}
			rs.close();
			pst.close();
//			System.out.println("hmRevieweewiseMemberCount ===>> " + hmRevieweewiseMemberCount);
			
			request.setAttribute("hmRevieweewiseMemberCount", hmRevieweewiseMemberCount);
			request.setAttribute("memberCount", memberCount);
			
			pst = con.prepareStatement("select * from appraisal_details_frequency  where (is_delete =false or is_delete is null) and appraisal_id = ? and appraisal_freq_id =?");
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
			
			String empids = appraisalMp.get("SELFID")!=null && !appraisalMp.get("SELFID").equals("") ? appraisalMp.get("SELFID").substring(1, appraisalMp.get("SELFID").length()-1) : "";
			Map<String, String> empImageMap = new HashMap<String, String>();
			if(empids != null && !empids.equals("") && !empids.equalsIgnoreCase("null")) {
				sbQuery = new StringBuilder();
				sbQuery.append("select emp_image,emp_per_id from employee_personal_details where emp_per_id>0 ");
				if(sbEmpId!=null) {
					sbQuery.append(" and emp_per_id in ("+sbEmpId.toString()+")");
				} else {
					sbQuery.append(" and emp_per_id in ("+empids+")");
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

			/*pst = con.prepareStatement("select self_ids,appraisal_details_id,oriented_type from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			while (rs.next()) {
				self_ids = rs.getString("self_ids");
				// appraisal_details_id = rs.getInt(2);
				oriented_type = rs.getString(3);
			}
			rs.close();
			pst.close();*/
			
			List<String> empList = new ArrayList<String>();
			if(getType() != null && getType().equals("KRATARGET")) {
				empList.add(strSessionEmpId);
			} else if(sbEmpId!=null) {
				empList = Arrays.asList(sbEmpId.toString().split(","));
			} else {
				pst = con.prepareStatement("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id and appraisal_id=? order by emp_fname");
				pst.setInt(1, uF.parseToInt(id));
				rs = pst.executeQuery();
//				System.out.println("pst 1 =========>> " + pst);
				while (rs.next()) {
					empList.add(rs.getString("reviewee_id"));
				}
				rs.close();
				pst.close();
			}
			
			/*self_ids=self_ids!=null && !self_ids.equals("") ? self_ids.substring(1, self_ids.length()-1) : null;
			List<String> empList = new ArrayList<String>();
			if(getType() != null && getType().equals("KRATARGET")) {
				empList.add(strSessionEmpId);
			} else if(sbEmpId!=null) {
				empList = Arrays.asList(sbEmpId.toString().split(","));
			} else {
				if(self_ids != null) {
					empList = Arrays.asList(self_ids.split(","));
				}
			}*/
			
//			System.out.println("empList ===>> " + empList);
			// Map<String, String> hmUserTypeID = new HashMap<String, String>();
			// pst = con
			// .prepareStatement("select user_type_id,user_type from user_type");
			// rs = pst.executeQuery();
			// while (rs.next()) {
			// hmUserTypeID.put(rs.getString(2), rs.getString(1));
			// }

			sbQuery = new StringBuilder();
			sbQuery.append("select *, (marks*100/weightage) as average from (select sum(marks) as marks, sum(weightage) as weightage," +
				"user_type_id,emp_id from appraisal_question_answer where appraisal_id=? and appraisal_freq_id=? and weightage>0 and is_submit=true ");
			if(sbEmpId!=null) {
				sbQuery.append(" and emp_id in ("+sbEmpId.toString()+")");
			}
			sbQuery.append(" group by user_type_id, emp_id) as a order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
//			System.out.println("pst2==>"+pst);
			rs = pst.executeQuery();
//			System.out.println("pst ========> "+pst);
			Map<String, Map<String, String>> outerMp = new HashMap<String, Map<String, String>>();
			
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			
			double dblTotalMarks = 0; 
			double dblTotalWeightage = 0;
			
			while (rs.next()) {
				strEmpIdNew = rs.getString("emp_id");
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					dblTotalMarks = 0;
					dblTotalWeightage = 0;
				}
				
				dblTotalMarks += uF.parseToDouble(rs.getString("marks"));
//				System.out.println("dblTotalMarks"+dblTotalMarks);
				
				dblTotalWeightage += uF.parseToDouble(rs.getString("weightage"));
				Map<String, String> value = outerMp.get(rs.getString("emp_id"));
				if (value == null)
					value = new HashMap<String, String>();
				value.put(rs.getString("user_type_id"), uF.formatIntoTwoDecimal(rs.getDouble("average")));
				if(dblTotalWeightage>0){
					value.put("AGGREGATE", uF.formatIntoTwoDecimal((dblTotalMarks * 100)/dblTotalWeightage));
				}
				outerMp.put(rs.getString("emp_id"), value);
				
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
//			System.out.println("outerMp==>"+outerMp);
			// request.setAttribute("hmUserTypeID", hmUserTypeID);
			request.setAttribute("outerMp", outerMp);
			request.setAttribute("empList", empList);
			request.setAttribute("oriented_type", oriented_type);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as count,emp_id,appraisal_id,appraisal_freq_id from (select emp_id,appraisal_id,user_id," +
				"user_type_id,appraisal_freq_id from appraisal_question_answer where appraisal_id=? and appraisal_freq_id=? and is_submit=true ");
			if(sbEmpId!=null) {
				sbQuery.append(" and emp_id in ("+sbEmpId.toString()+")");
			}
			if(strUserTypesForFeedback != null && strUserTypesForFeedback.length()>0) {
				sbQuery.append(" and user_type_id not in ("+strUserTypesForFeedback+") ");
			}
			sbQuery.append(" group by emp_id,user_id,user_type_id,appraisal_id,appraisal_freq_id) as a group by emp_id,appraisal_id,appraisal_freq_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));		
			pst.setInt(2, uF.parseToInt(getAppFreqId()));		
//			System.out.println("hmEmpCount pst3==>"+pst);
			rs = pst.executeQuery();
			Map<String,String> hmEmpCount=new HashMap<String, String>();
			while (rs.next()) {
				hmEmpCount.put(rs.getString("emp_id"), rs.getString("count"));				
			}		
			rs.close();
			pst.close();
//			System.out.println("hmEmpCount ===>> " + hmEmpCount);
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
		
			empIds=empIds !=null && !empIds.equals("") ? empIds.substring(1, empIds.length()-1) : "";
			
			if(empIds != null && !empIds.equals("") && !empIds.equalsIgnoreCase("null")) {
			pst = con.prepareStatement("select eod.wlocation_id,emp_id,wlocation_name from employee_official_details eod,work_location_info wli where eod.wlocation_id=wli.wlocation_id and emp_id in(" + empIds + ")");
//			System.out.println("pst====> "+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				locationMp.put(rs.getString("emp_id"), rs.getString("wlocation_name"));
			}
			rs.close();
			pst.close();
			}
//			System.out.println("locationMp ====> "+locationMp);
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

		if(empId != null && !empId.trim().equals("")) {
			if(hmHireracyLevelEmpIds == null) hmHireracyLevelEmpIds = new HashMap<String, List<String>>();
			List<String> innerList = (List<String>)hmHireracyLevelEmpIds.get(empId.trim());
	
			for(int i= 0; innerList != null && !innerList.isEmpty() && i<innerList.size(); i++) {
				String empId1 = innerList.get(i);
				if(empId1 != null && !empId1.trim().equals("") && !empIDList.contains(empId1.trim())) {
					empIDList.add(empId1.trim());
					if(sbClildEmpIds==null) {
						sbClildEmpIds = new StringBuilder();
						sbClildEmpIds.append(empId1.trim());
					} else {
						sbClildEmpIds.append(","+empId1.trim());
					}
				}
				if(empId1 != null && !empId1.trim().equals("")) {
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
	// con.prepareStatement("select * from orientation_details where orientation_id=?");
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
				//orientationMemberMp.put(rs.getString("orientation_member_id"), rs.getString("member_name"));
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
			strID=strID.substring(1, strID.length()-1);
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
