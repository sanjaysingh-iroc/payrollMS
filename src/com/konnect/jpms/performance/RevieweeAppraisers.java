package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class RevieweeAppraisers extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request; 
	String strUserType = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;
	private String reviewId;
	private String revieweeId;
	
	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
	
		UtilityFunctions uF = new UtilityFunctions();
		if(uF.parseToInt(revieweeId)>0) {
			getRevieweeAppraisers(uF);
		} else {
			getAllRevieweeAppraisers(uF);
		}
		return LOAD;
	}
	
	
	private void getAllRevieweeAppraisers(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			request.setAttribute("hmEmpName", hmEmpName);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,supervisor_emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
				" and is_alive= true and emp_per_id >0 order by supervisor_emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			rst = pst.executeQuery();
//			System.out.println("PST ===>> " + pst);
			Map<String, List<String>> hmHireracyLevelEmpIds = new LinkedHashMap<String, List<String>>();
//			List<String> alHireracyLevels = new ArrayList<String>();
			while(rst.next()) {
				List<String> alInner = hmHireracyLevelEmpIds.get(rst.getString("supervisor_emp_id"));
				if(alInner==null) alInner = new ArrayList<String>();
				if(uF.parseToInt(rst.getString("supervisor_emp_id")) == uF.parseToInt(rst.getString("emp_id"))) {
					continue;
				}
				alInner.add(rst.getString("emp_id"));
				hmHireracyLevelEmpIds.put(rst.getString("supervisor_emp_id"), alInner);
			}
			rst.close();
			pst.close();

			StringBuilder sbClildEmpIds = null;
			List<String> empIDList = new ArrayList<String>();
			sbClildEmpIds = getChildEmpIds(hmHireracyLevelEmpIds, strSessionEmpId, empIDList, sbClildEmpIds);
			
			StringBuilder sbEmpId = null;
			if(strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery = new StringBuilder();
				sbQuery.append("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id  and appraisal_id=? " +
					"and (supervisor_ids like '%,"+strSessionEmpId+",%' or grand_supervisor_ids like '%,"+strSessionEmpId+",%' or hod_ids like '%,"+strSessionEmpId+",%' ");
				if(sbClildEmpIds!=null && !sbClildEmpIds.toString().equals("")) {
					sbQuery.append(" or epd.emp_per_id in ("+sbClildEmpIds.toString()+") ");
				}
				sbQuery.append(") order by emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getReviewId()));
				rst = pst.executeQuery();
				while (rst.next()) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rst.getString("reviewee_id"));
					} else {
						sbEmpId.append(","+rst.getString("reviewee_id"));
					}
				}
				rst.close();
				pst.close();
			}
			
			/*StringBuilder sbEmpId = null;
			if(strUserType != null && strUserType.equals(MANAGER)) {
				pst = con.prepareStatement("select reviewee_id from appraisal_reviewee_details where supervisor_ids like '%,"+strSessionEmpId+",%' ");
				rst = pst.executeQuery();
				while (rst.next()) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rst.getString("reviewee_id"));
					} else {
						sbEmpId.append(","+rst.getString("reviewee_id"));
					}
				}
				rst.close();
				pst.close();
			}*/
			
			Map<String, String> orientationMemberMp = new HashMap<String, String>();
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rst = pst.executeQuery();
			while (rst.next()) {
				orientationMemberMp.put(rst.getString("member_id"), rst.getString("member_name"));
			}
			rst.close();
			pst.close();
			
			Map<String, List<String>> hmRevieweeWiseUsers = new HashMap<String, List<String>>();
			sbQuery = new StringBuilder();
			sbQuery.append("select ad.usertype_member,ard.* from appraisal_reviewee_details ard, appraisal_details ad where " +
				" ard.appraisal_id = ad.appraisal_details_id and ard.appraisal_id=? ");
			if(sbEmpId!=null) {
				sbQuery.append(" and reviewee_id in ("+sbEmpId.toString()+")");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getReviewId()));
			rst = pst.executeQuery();
			String strUserTypeIds = null;
			while(rst.next()) {
				strUserTypeIds = rst.getString("usertype_member");
				
				List<String> alUserIds = new ArrayList<String>();
				alUserIds.add(rst.getString("subordinate_ids")); //0
				alUserIds.add(rst.getString("peer_ids")); //1
				alUserIds.add(rst.getString("other_peer_ids")); //2
				alUserIds.add(rst.getString("supervisor_ids")); //3
				alUserIds.add(rst.getString("grand_supervisor_ids")); //4
				alUserIds.add(rst.getString("hod_ids")); //5
				alUserIds.add(rst.getString("ceo_ids")); //6
				alUserIds.add(rst.getString("hr_ids")); //7
				alUserIds.add(rst.getString("other_ids")); //8
				
				hmRevieweeWiseUsers.put(rst.getString("reviewee_id"), alUserIds);
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getReviewId()));
			rst = pst.executeQuery();
			Map<String, List<String>> hmFeedbackGivenUsers = new HashMap<String, List<String>>();
			while(rst.next()) {
				
				List<String> alUserIds = hmFeedbackGivenUsers.get(rst.getString("emp_id")+"_"+rst.getString("user_type_id"));
				if(alUserIds == null) alUserIds = new ArrayList<String>();
				alUserIds.add(rst.getString("user_id")); //0
				
				hmFeedbackGivenUsers.put(rst.getString("emp_id")+"_"+rst.getString("user_type_id"), alUserIds);
			}
			rst.close();
			pst.close();
		
		
			Map<String, Map<String, String>> hmRevieweeUsertypeUsers = new HashMap<String, Map<String, String>>();
			List<String> alUserTypes = new ArrayList<String>();
			if(strUserTypeIds != null) {
				alUserTypes = Arrays.asList(strUserTypeIds.split(","));
				
				Iterator<String> it = hmRevieweeWiseUsers.keySet().iterator();
				while(it.hasNext()) {
					String strRevieweeId = it.next();
					List<String> alUserIds = hmRevieweeWiseUsers.get(strRevieweeId);
					
					Map<String, String> hmUsertypeUsers = new HashMap<String, String>();	
					for(int i=0; alUserTypes != null && i<alUserTypes.size(); i++) {
						List<String> alList =  new ArrayList<String>();
						if(alUserTypes.get(i) != null && alUserTypes.get(i).equals("6")) {
							if(alUserIds.get(0) != null && !alUserIds.get(0).equals("")) { 
								alList = Arrays.asList(alUserIds.get(0).split(","));
							}
						} else if(alUserTypes.get(i) != null && alUserTypes.get(i).equals("4")) {
							if(alUserIds.get(1) != null && !alUserIds.get(1).equals("")) { 
								alList = Arrays.asList(alUserIds.get(1).split(","));
							}
						} else if(alUserTypes.get(i) != null && alUserTypes.get(i).equals("14")) {
							if(alUserIds.get(2) != null && !alUserIds.get(2).equals("")) { 
								alList = Arrays.asList(alUserIds.get(2).split(","));
							}
						} else if(alUserTypes.get(i) != null && alUserTypes.get(i).equals("2")) {
							if(alUserIds.get(3) != null && !alUserIds.get(3).equals("")) { 
								alList = Arrays.asList(alUserIds.get(3).split(","));
							}
						} else if(alUserTypes.get(i) != null && alUserTypes.get(i).equals("8")) {
							if(alUserIds.get(4) != null && !alUserIds.get(4).equals("")) { 
								alList = Arrays.asList(alUserIds.get(4).split(","));
							}
						} else if(alUserTypes.get(i) != null && alUserTypes.get(i).equals("13")) {
							if(alUserIds.get(5) != null && !alUserIds.get(5).equals("")) { 
								alList = Arrays.asList(alUserIds.get(5).split(","));
							}
						} else if(alUserTypes.get(i) != null && alUserTypes.get(i).equals("5")) {
							if(alUserIds.get(6) != null && !alUserIds.get(6).equals("")) { 
								alList = Arrays.asList(alUserIds.get(6).split(","));
							}
						} else if(alUserTypes.get(i) != null && alUserTypes.get(i).equals("7")) {
							if(alUserIds.get(7) != null && !alUserIds.get(7).equals("")) { 
								alList = Arrays.asList(alUserIds.get(7).split(","));
							}
						} else if(alUserTypes.get(i) != null && alUserTypes.get(i).equals("10")) {
							if(alUserIds.get(8) != null && !alUserIds.get(8).equals("")) { 
								alList = Arrays.asList(alUserIds.get(8).split(","));
							}
						}
						
						StringBuilder sbUsers = null;
						for(int j=0; alList!=null && j<alList.size(); j++) {
							if(!alList.get(j).equals("")) {
								if(sbUsers == null) {
									sbUsers = new StringBuilder();
									sbUsers.append(hmEmpName.get(alList.get(j)));
								} else {
									sbUsers.append(", " + hmEmpName.get(alList.get(j)));
								}
							}
						}
						if(sbUsers == null) {
							sbUsers = new StringBuilder();
							sbUsers.append("N/A");
						}
						hmUsertypeUsers.put(alUserTypes.get(i), sbUsers.toString());
					}
					
					hmRevieweeUsertypeUsers.put(strRevieweeId, hmUsertypeUsers);
				}
			}
			
			request.setAttribute("alUserTypes", alUserTypes);
			request.setAttribute("hmRevieweeUsertypeUsers", hmRevieweeUsertypeUsers);
			request.setAttribute("orientationMemberMp", orientationMemberMp);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
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

	private void getRevieweeAppraisers(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			Map<String, String> orientationMemberMp = new HashMap<String, String>();
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rst = pst.executeQuery();
			while (rst.next()) {
				orientationMemberMp.put(rst.getString("member_id"), rst.getString("member_name"));
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select ad.usertype_member,ard.* from appraisal_reviewee_details ard, appraisal_details ad where " +
				" ard.appraisal_id = ad.appraisal_details_id and ard.appraisal_id=? and reviewee_id=? ");
			pst.setInt(1, uF.parseToInt(getReviewId()));
			pst.setInt(2, uF.parseToInt(getRevieweeId()));
			rst = pst.executeQuery();
			List<String> alUserIds = new ArrayList<String>();
			String strUserTypeIds = null;
			while(rst.next()) {
				strUserTypeIds = rst.getString("usertype_member");
				alUserIds.add(rst.getString("subordinate_ids")); //0
				alUserIds.add(rst.getString("peer_ids")); //1
				alUserIds.add(rst.getString("other_peer_ids")); //2
				alUserIds.add(rst.getString("supervisor_ids")); //3
				alUserIds.add(rst.getString("grand_supervisor_ids")); //4
				alUserIds.add(rst.getString("hod_ids")); //5
				alUserIds.add(rst.getString("ceo_ids")); //6
				alUserIds.add(rst.getString("hr_ids")); //7
				alUserIds.add(rst.getString("other_ids")); //8
			}
			rst.close();
			pst.close();
			
			Map<String, String> hmUsertypeUsers = new HashMap<String, String>();
			List<String> alUserTypes = new ArrayList<String>();
			if(strUserTypeIds != null) {
				alUserTypes = Arrays.asList(strUserTypeIds.split(","));
				for(int i=0; alUserTypes != null && i<alUserTypes.size(); i++) {
					List<String> alList =  new ArrayList<String>();
					if(alUserTypes.get(i) != null && alUserTypes.get(i).equals("6")) {
						if(alUserIds.get(0) != null && !alUserIds.get(0).equals("")) { 
							alList = Arrays.asList(alUserIds.get(0).split(","));
						}
					} else if(alUserTypes.get(i) != null && alUserTypes.get(i).equals("4")) {
						if(alUserIds.get(1) != null && !alUserIds.get(1).equals("")) { 
							alList = Arrays.asList(alUserIds.get(1).split(","));
						}
					} else if(alUserTypes.get(i) != null && alUserTypes.get(i).equals("14")) {
						if(alUserIds.get(2) != null && !alUserIds.get(2).equals("")) { 
							alList = Arrays.asList(alUserIds.get(2).split(","));
						}
					} else if(alUserTypes.get(i) != null && alUserTypes.get(i).equals("2")) {
						if(alUserIds.get(3) != null && !alUserIds.get(3).equals("")) { 
							alList = Arrays.asList(alUserIds.get(3).split(","));
						}
					} else if(alUserTypes.get(i) != null && alUserTypes.get(i).equals("8")) {
						if(alUserIds.get(4) != null && !alUserIds.get(4).equals("")) { 
							alList = Arrays.asList(alUserIds.get(4).split(","));
						}
					} else if(alUserTypes.get(i) != null && alUserTypes.get(i).equals("13")) {
						if(alUserIds.get(5) != null && !alUserIds.get(5).equals("")) { 
							alList = Arrays.asList(alUserIds.get(5).split(","));
						}
					} else if(alUserTypes.get(i) != null && alUserTypes.get(i).equals("5")) {
						if(alUserIds.get(6) != null && !alUserIds.get(6).equals("")) { 
							alList = Arrays.asList(alUserIds.get(6).split(","));
						}
					} else if(alUserTypes.get(i) != null && alUserTypes.get(i).equals("7")) {
						if(alUserIds.get(7) != null && !alUserIds.get(7).equals("")) { 
							alList = Arrays.asList(alUserIds.get(7).split(","));
						}
					} else if(alUserTypes.get(i) != null && alUserTypes.get(i).equals("10")) {
						if(alUserIds.get(8) != null && !alUserIds.get(8).equals("")) { 
							alList = Arrays.asList(alUserIds.get(8).split(","));
						}
					}
					
					StringBuilder sbUsers = null;
					for(int j=0; alList!=null && j<alList.size(); j++) {
						if(!alList.get(j).equals("")) {
							if(sbUsers == null) {
								sbUsers = new StringBuilder();
								sbUsers.append(hmEmpName.get(alList.get(j)));
							} else {
								sbUsers.append(", " + hmEmpName.get(alList.get(j)));
							}
						}
					}
					if(sbUsers == null) {
						sbUsers = new StringBuilder();
						sbUsers.append("N/A");
					}
					hmUsertypeUsers.put(alUserTypes.get(i), sbUsers.toString());
				}
			}
			
			request.setAttribute("alUserTypes", alUserTypes);
			request.setAttribute("hmUsertypeUsers", hmUsertypeUsers);
			request.setAttribute("orientationMemberMp", orientationMemberMp);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public String getReviewId() {
		return reviewId;
	}

	public void setReviewId(String reviewId) {
		this.reviewId = reviewId;
	}

	public String getRevieweeId() {
		return revieweeId;
	}

	public void setRevieweeId(String revieweeId) {
		this.revieweeId = revieweeId;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
