package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;

public class PublishAppraisal implements ServletRequestAware, SessionAware, IStatements {
	Map session;
	CommonFunctions CF;
	HttpServletRequest request;

	private String strUserType = null;
	private String strSessionEmpId = null;

	private String id;
	private String dcount;
	private String from;
	private String appFreqId;
	private String fromPage;
	public String execute() {
		strUserType = (String) session.get(USERTYPE);
		strSessionEmpId = (String) session.get(EMPID);

		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return "login";
		
		setAppraisalPublishStatus();
//		getReviewDetails();
		
		return "success";
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
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, String> hmReviewData = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id = ?");
			pst.setInt(1, uF.parseToInt(getId()));
			rst = pst.executeQuery();
			while (rst.next()) {
				hmReviewData.put("REVIEW_NAME", rst.getString("appraisal_name"));
				hmReviewData.put("REVIEW_STARTDATE", rst.getString("from_date"));
				hmReviewData.put("REVIEW_ENDDATE", rst.getString("to_date"));
				
			}
			rst.close();
			pst.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hmReviewData;
	}
	

	private void setAppraisalPublishStatus() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		boolean is_publish = false;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select is_appraisal_publish from appraisal_details_frequency where appraisal_id=? and appraisal_freq_id = ?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rst = pst.executeQuery();

			while (rst.next()) {
				is_publish=rst.getBoolean(1);
			}
			rst.close();
			pst.close();
			
			
			String query="";
			if(is_publish == true) {
				query ="update appraisal_details_frequency set is_appraisal_publish = false where appraisal_id=? and appraisal_freq_id = ?";
			} else {
				query ="update appraisal_details_frequency set is_appraisal_publish=true where appraisal_id=? and appraisal_freq_id = ?";
			}
			pst = con.prepareStatement(query);
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			pst.execute();
			pst.close();
			
			if(getFrom() != null && getFrom().equals("EFF")) {
//				System.out.println("in PublishAppraisal 's setAppraisalPublishStatus() EXIT_FEEDBACK_FORM ");
				query ="update appraisal_details set is_publish = false where form_type = "+ EXIT_FEEDBACK_FORM +" and appraisal_details_id != ?";
				//query ="update appraisal_details_frequency set is_appraisal_publish = false where form_type = "+ EXIT_FEEDBACK_FORM +" and appraisal_details_id != ?";
				pst = con.prepareStatement(query);
				pst.setInt(1, uF.parseToInt(getId()));
				pst.execute();
				pst.close();
			}
			
			request.setAttribute("is_publish", is_publish);

			if(is_publish == false && (getFrom() == null || !getFrom().equals("EFF"))) {
				String strDomain = request.getServerName().split("\\.")[0];
				setDomain(strDomain);
//				Thread th = new Thread(this);
//				th.start();
				
				
				Map<String, String> hmReviewData = getReviewDetails(con);
				Map<String, Map<String, String>> hmRevieweewiseAppraiser = getRevieweewiseAppraiser(con);
				Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
				
				
				Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);//Created By Dattatray Date:11-11-21
				
				Iterator<String> it = hmRevieweewiseAppraiser.keySet().iterator();
				Map<String, String> hmEmpUserId = CF.getEmpUserIdMap(con);
				while(it.hasNext()) {
					String strRevieweeId = it.next();
					Map<String, String> hmRevieweeNameData = hmEmpInfo.get(strRevieweeId);
					
					Map<String, String> hmRevieweeData = hmRevieweewiseAppraiser.get(strRevieweeId);
					List<List<String>> allIdList = new ArrayList<List<String>>();
					
					//Started By Dattatray Date:11-11-21
					if(uF.parseToBoolean(hmFeatureStatus.get(F_REVIEW_PUBLISH_MAIL_SENT_TO_REVIEWEE))) {
						if(hmRevieweeData.get("REVIEW_SELFID") != null && !hmRevieweeData.get("REVIEW_SELFID").equals("")) {
							List<String> selfID = Arrays.asList(hmRevieweeData.get("REVIEW_SELFID").split(",")); 
							for (int i = 0; selfID != null && i < selfID.size(); i++) {
								if(selfID.get(i) != null && !selfID.get(i).equals("")) {
									List<String> innerList = new ArrayList<String>();
									innerList.add(selfID.get(i));
									innerList.add("Self");
									allIdList.add(innerList);
								}
							}
						}
					} //Ended By Dattatray Date:11-11-21
					
					
					
					if(hmRevieweeData.get("REVIEW_PEERID") != null && !hmRevieweeData.get("REVIEW_PEERID").equals("")) {
						List<String> peerID = Arrays.asList(hmRevieweeData.get("REVIEW_PEERID").split(",")); 
						for (int i = 0; peerID != null && i < peerID.size(); i++) {
							if(peerID.get(i) != null && !peerID.get(i).equals("")) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(peerID.get(i));
								innerList.add("Peer");
								allIdList.add(innerList);
							}
						}
					}
					if(hmRevieweeData.get("REVIEW_MANAGERID") != null && !hmRevieweeData.get("REVIEW_MANAGERID").equals("")) {
						List<String> managerID = Arrays.asList(hmRevieweeData.get("REVIEW_MANAGERID").split(",")); 
						for (int i = 0; managerID != null && i < managerID.size(); i++) {
							if(managerID.get(i) != null && !managerID.get(i).equals("")) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(managerID.get(i));
								innerList.add("Manager");
								allIdList.add(innerList);
							}
						}
					}
					if(hmRevieweeData.get("REVIEW_HRID") != null && !hmRevieweeData.get("REVIEW_HRID").equals("")) {
						List<String> hrID = Arrays.asList(hmRevieweeData.get("REVIEW_HRID").split(",")); 
						for (int i = 0; hrID != null && i < hrID.size(); i++) {
							if(hrID.get(i) != null && !hrID.get(i).equals("")) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(hrID.get(i));
								innerList.add("HR");
								allIdList.add(innerList);
							}
						}
					}
					
					if(hmRevieweeData.get("REVIEW_CEOID") != null && !hmRevieweeData.get("REVIEW_CEOID").equals("")) {
						List<String> ceoID = Arrays.asList(hmRevieweeData.get("REVIEW_CEOID").split(",")); 
						for (int i = 0; ceoID != null && i < ceoID.size(); i++) {
							if(ceoID.get(i) != null && !ceoID.get(i).equals("")) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(ceoID.get(i));
								innerList.add("CEO");
								allIdList.add(innerList);
							}
						}
					}
					
					if(hmRevieweeData.get("REVIEW_HODID") != null && !hmRevieweeData.get("REVIEW_HODID").equals("")) {
						List<String> hodID = Arrays.asList(hmRevieweeData.get("REVIEW_HODID").split(",")); 
						for (int i = 0; hodID != null && i < hodID.size(); i++) {
							if(hodID.get(i) != null && !hodID.get(i).equals("")) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(hodID.get(i));
								innerList.add("HOD");
								allIdList.add(innerList);
							}
						}
					}
					
					if(hmRevieweeData.get("REVIEW_OTHERID") != null && !hmRevieweeData.get("REVIEW_OTHERID").equals("")) {
						List<String> otherID = Arrays.asList(hmRevieweeData.get("REVIEW_OTHERID").split(",")); 
						for (int i = 0; otherID != null && i < otherID.size(); i++) {
							if(otherID.get(i) != null && !otherID.get(i).equals("")) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(otherID.get(i));
								innerList.add("Anyone");
								allIdList.add(innerList);
							}
						}
					}
					
					if(hmRevieweeData.get("REVIEW_SUBORDINATEID") != null && !hmRevieweeData.get("REVIEW_SUBORDINATEID").equals("")) {
						List<String> subordinateID = Arrays.asList(hmRevieweeData.get("REVIEW_SUBORDINATEID").split(",")); 
						for (int i = 0; subordinateID != null && i < subordinateID.size(); i++) {
							if(subordinateID.get(i) != null && !subordinateID.get(i).equals("")) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(subordinateID.get(i));
								innerList.add("Sub-ordinate");
								allIdList.add(innerList);
							}
						}
					}
					
					if(hmRevieweeData.get("REVIEW_GRANDSUPERVISORID") != null && !hmRevieweeData.get("REVIEW_GRANDSUPERVISORID").equals("")) {
						List<String> gSupervisorID = Arrays.asList(hmRevieweeData.get("REVIEW_GRANDSUPERVISORID").split(",")); 
						for (int i = 0; gSupervisorID != null && i < gSupervisorID.size(); i++) {
							if(gSupervisorID.get(i) != null && !gSupervisorID.get(i).equals("")) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(gSupervisorID.get(i));
								innerList.add("Group Head");
								allIdList.add(innerList);
							}
						}
					}
					
					if(hmRevieweeData.get("REVIEW_OTHERPEERID") != null && !hmRevieweeData.get("REVIEW_OTHERPEERID").equals("")) {
						List<String> otherPeerID = Arrays.asList(hmRevieweeData.get("REVIEW_OTHERPEERID").split(",")); 
						for (int i = 0; otherPeerID != null && i < otherPeerID.size(); i++) {
							if(otherPeerID.get(i) != null && !otherPeerID.get(i).equals("")) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(otherPeerID.get(i));
								innerList.add("Other Peer");
								allIdList.add(innerList);
							}
						}
					}
					
					System.out.println("Emp List : "+allIdList);
					for (int i = 0; allIdList != null && !allIdList.isEmpty() && i < allIdList.size(); i++) {
						List<String> innerList = allIdList.get(i);
						if(innerList.get(0) != null && !innerList.get(0).equals("")) {
							Map<String, String> hmEmpInner = hmEmpInfo.get(innerList.get(0));
							Notifications nF = new Notifications(N_NEW_REVIW_PUBLISH, CF);
							nF.setDomain(getStrDomain());
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
					
					/* created by seema */
					
					strDomain = request.getServerName().split("\\.")[0];
					String alertData = "<div style=\"float: left;\"> A new Review ("+hmReviewData.get("REVIEW_NAME")+") has published by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
					String alertAction = "Reviews.action?callFrom=Dash";
					
					
					String userTypeId = hmEmpUserId.get(strRevieweeId);
//					if (userTypeId != null) {
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strRevieweeId);
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID(userTypeId);
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
//					}
					
					/* created by seema */
					
				}
				
				
				
				
				/*Map<String, String> hmReviewData = getReviewDetails(con);
				List<String> allIdList = new ArrayList<String>();
				if(hmReviewData.get("REVIEW_SELFID") != null && !hmReviewData.get("REVIEW_SELFID").equals("")){
					List<String> selfID = Arrays.asList(hmReviewData.get("REVIEW_SELFID").split(",")); 
					for (int i = 0; selfID != null && i < selfID.size(); i++) {
						if(selfID.get(i) != null && !selfID.get(i).equals("")){
							allIdList.add(selfID.get(i));
						}
					}
				}
				if(hmReviewData.get("REVIEW_PEERID") != null && !hmReviewData.get("REVIEW_PEERID").equals("")){
					List<String> peerID = Arrays.asList(hmReviewData.get("REVIEW_PEERID").split(",")); 
					for (int i = 0; peerID != null && i < peerID.size(); i++) {
						if(peerID.get(i) != null && !peerID.get(i).equals("")){
							allIdList.add(peerID.get(i));
						}
					}
				}
				if(hmReviewData.get("REVIEW_MANAGERID") != null && !hmReviewData.get("REVIEW_MANAGERID").equals("")){
					List<String> managerID = Arrays.asList(hmReviewData.get("REVIEW_MANAGERID").split(",")); 
					for (int i = 0; managerID != null && i < managerID.size(); i++) {
						if(managerID.get(i) != null && !managerID.get(i).equals("")){
							allIdList.add(managerID.get(i));
						}
					}
				}
				if(hmReviewData.get("REVIEW_HRID") != null && !hmReviewData.get("REVIEW_HRID").equals("")){
					List<String> hrID = Arrays.asList(hmReviewData.get("REVIEW_HRID").split(",")); 
					for (int i = 0; hrID != null && i < hrID.size(); i++) {
						if(hrID.get(i) != null && !hrID.get(i).equals("")){
							allIdList.add(hrID.get(i));
						}
					}
				}
				
				if(hmReviewData.get("REVIEW_CEOID") != null && !hmReviewData.get("REVIEW_CEOID").equals("")){
					List<String> ceoID = Arrays.asList(hmReviewData.get("REVIEW_CEOID").split(",")); 
					for (int i = 0; ceoID != null && i < ceoID.size(); i++) {
						if(ceoID.get(i) != null && !ceoID.get(i).equals("")){
							allIdList.add(ceoID.get(i));
						}
					}
				}
				
				if(hmReviewData.get("REVIEW_HODID") != null && !hmReviewData.get("REVIEW_HODID").equals("")){
					List<String> hodID = Arrays.asList(hmReviewData.get("REVIEW_HODID").split(",")); 
					for (int i = 0; hodID != null && i < hodID.size(); i++) {
						if(hodID.get(i) != null && !hodID.get(i).equals("")){
							allIdList.add(hodID.get(i));
						}
					}
				}
				
				Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
		
				for (int i = 0; allIdList != null && !allIdList.isEmpty() && i < allIdList.size(); i++) {
					if(allIdList.get(i) != null && !allIdList.get(i).trim().equals("")){
						Map<String, String> hmEmpInner = hmEmpInfo.get(allIdList.get(i));
						Notifications nF = new Notifications(N_NEW_REVIW_PUBLISH, CF);
						nF.setDomain(getStrDomain());
						nF.request = request;
						nF.setStrEmpId(allIdList.get(i));
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
			
						nF.setStrReviewName(hmReviewData.get("REVIEW_NAME")); // +"(for "+hmReviewData.get("REVIEW_FREQ")+")"
						nF.setStrReviewStartdate(uF.getDateFormat(hmReviewData.get("REVIEW_STARTDATE"), DBDATE, CF.getStrReportDateFormat()));
						nF.setStrReviewEnddate(uF.getDateFormat(hmReviewData.get("REVIEW_ENDDATE"), DBDATE, CF.getStrReportDateFormat()));
						nF.setStrEmpFname(uF.showData(hmEmpInner.get("FNAME"), ""));
						nF.setStrEmpLname(uF.showData(hmEmpInner.get("LNAME"), ""));
						nF.setEmailTemplate(true);				
						nF.sendNotifications();
					}
				}*/
				
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
//	@Override
//	public void run() {
//	
//		Connection con = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		try {
//			con = db.makeConnection(con);
//			Map<String, String> hmReviewData = getReviewDetails(con);
//			List<String> allIdList = new ArrayList<String>();
//			if(hmReviewData.get("REVIEW_SELFID") != null && !hmReviewData.get("REVIEW_SELFID").equals("")){
//				List<String> selfID = Arrays.asList(hmReviewData.get("REVIEW_SELFID").split(",")); 
//				for (int i = 0; selfID != null && i < selfID.size(); i++) {
//					if(selfID.get(i) != null && !selfID.get(i).equals("")){
//						allIdList.add(selfID.get(i));
//					}
//				}
//			}
//			if(hmReviewData.get("REVIEW_PEERID") != null && !hmReviewData.get("REVIEW_PEERID").equals("")){
//				List<String> peerID = Arrays.asList(hmReviewData.get("REVIEW_PEERID").split(",")); 
//				for (int i = 0; peerID != null && i < peerID.size(); i++) {
//					if(peerID.get(i) != null && !peerID.get(i).equals("")){
//						allIdList.add(peerID.get(i));
//					}
//				}
//			}
//			if(hmReviewData.get("REVIEW_MANAGERID") != null && !hmReviewData.get("REVIEW_MANAGERID").equals("")){
//				List<String> managerID = Arrays.asList(hmReviewData.get("REVIEW_MANAGERID").split(",")); 
//				for (int i = 0; managerID != null && i < managerID.size(); i++) {
//					if(managerID.get(i) != null && !managerID.get(i).equals("")){
//						allIdList.add(managerID.get(i));
//					}
//				}
//			}
//			if(hmReviewData.get("REVIEW_HRID") != null && !hmReviewData.get("REVIEW_HRID").equals("")){
//				List<String> hrID = Arrays.asList(hmReviewData.get("REVIEW_HRID").split(",")); 
//				for (int i = 0; hrID != null && i < hrID.size(); i++) {
//					if(hrID.get(i) != null && !hrID.get(i).equals("")){
//						allIdList.add(hrID.get(i));
//					}
//				}
//			}
//			
//			if(hmReviewData.get("REVIEW_CEOID") != null && !hmReviewData.get("REVIEW_CEOID").equals("")){
//				List<String> ceoID = Arrays.asList(hmReviewData.get("REVIEW_CEOID").split(",")); 
//				for (int i = 0; ceoID != null && i < ceoID.size(); i++) {
//					if(ceoID.get(i) != null && !ceoID.get(i).equals("")){
//						allIdList.add(ceoID.get(i));
//					}
//				}
//			}
//			
//			if(hmReviewData.get("REVIEW_HODID") != null && !hmReviewData.get("REVIEW_HODID").equals("")){
//				List<String> hodID = Arrays.asList(hmReviewData.get("REVIEW_HODID").split(",")); 
//				for (int i = 0; hodID != null && i < hodID.size(); i++) {
//					if(hodID.get(i) != null && !hodID.get(i).equals("")){
//						allIdList.add(hodID.get(i));
//					}
//				}
//			}
//			
//			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
//	
//	//		 System.out.println("allIdList ==> "+ allIdList);
//			// String strDomain = request.getServerName().split("\\.")[0];
//			for (int i = 0; allIdList != null && !allIdList.isEmpty() && i < allIdList.size(); i++) {
//				if(allIdList.get(i) != null && !allIdList.get(i).trim().equals("")){
//					Map<String, String> hmEmpInner = hmEmpInfo.get(allIdList.get(i));
//	//				System.out.println("allIdList.get(i) ======== > " + allIdList.get(i));
//	//				System.out.println("hmEmpInner ======== > " + hmEmpInner);
//	//				 System.out.println(i+" allIdList "+allIdList.get(i));
////					String strDomain = request.getServerName().split("\\.")[0];
//					Notifications nF = new Notifications(N_NEW_REVIW_PUBLISH, CF);
//					nF.setDomain(getStrDomain());
//					nF.request = request;
//					nF.setStrEmpId(allIdList.get(i));
//					nF.setStrHostAddress(CF.getStrEmailLocalHost());
//					nF.setStrHostPort(CF.getStrHostPort());
//					nF.setStrContextPath(request.getContextPath());
//		
//					nF.setStrReviewName(hmReviewData.get("REVIEW_NAME")+"(for "+hmReviewData.get("REVIEW_FREQ")+")");
//					nF.setStrReviewStartdate(uF.getDateFormat(hmReviewData.get("REVIEW_STARTDATE"), DBDATE, CF.getStrReportDateFormat()));
//					nF.setStrReviewEnddate(uF.getDateFormat(hmReviewData.get("REVIEW_ENDDATE"), DBDATE, CF.getStrReportDateFormat()));
//		
//					nF.setStrEmpFname(uF.showData(hmEmpInner.get("FNAME"), ""));
//					nF.setStrEmpLname(uF.showData(hmEmpInner.get("LNAME"), ""));
//					nF.setEmailTemplate(true);				
//					nF.sendNotifications();
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//		}
//	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDcount() {
		return dcount;
	}

	public void setDcount(String dcount) {
		this.dcount = dcount;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	@Override
	public void setSession(Map session) {
		this.session = session;
	}

	String strDomain;
	public void setDomain(String strDomain) {
		this.strDomain=strDomain;
	}

	public String getStrDomain() {
		return strDomain;
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