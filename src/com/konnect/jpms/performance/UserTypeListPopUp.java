package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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

public class UserTypeListPopUp extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8137873618020825511L;
	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	
	CommonFunctions CF;
	
	private String id;
	private String empId;
	private String sectionId;
	private String memberIds;
	private String appFreqId;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("memberIds ==== > "+memberIds);
//		request.setAttribute(PAGE, "/jsp/performance/Appraisal.jsp");
//		request.setAttribute(TITLE, "Appraisal");
//		getOrientationMember();
//		getExistUsersInAQA(uF);
//		getOrientTypeWiseRemainIds(uF);
		getAppraisalSectionWorkFlow(uF);
		return "success";
	}

	private List<String> getAppraisalSubSectionAndWorkFlowOrder(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		List<String> workFlowOrderList = new ArrayList<String>();
		Map<String, List<String>> hmWorkFlowOrder = new HashMap<String, List<String>>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_main_level_details ald, appraisal_details ad where ald.appraisal_id = ? and " +
					"ald.main_level_id =? and ald.appraisal_id = ad.appraisal_details_id limit 1");
			pst.setInt(1, uF.parseToInt(id));
			pst.setInt(2, uF.parseToInt(sectionId));
			rs = pst.executeQuery();
			Map<String, String> hmOrientMemberID = CF.getOrientMemberID(con);
			while (rs.next()) {
				List<String> memberList= CF.getOrientationMemberDetails(con,rs.getInt("oriented_type"));
				
				if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Self") != null   && memberList.contains(hmOrientMemberID.get("Self"))) {
					if(!workFlowOrderList.contains(rs.getString("self"))) {
						workFlowOrderList.add(rs.getString("self"));
					}
					List<String> innerList = hmWorkFlowOrder.get(rs.getString("self"));
					if(innerList==null) innerList = new ArrayList<String>();
					innerList.add(EMPLOYEE);
					hmWorkFlowOrder.put(rs.getString("self"), innerList);
				}
				
				if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Sub-ordinate") != null  && memberList.contains(hmOrientMemberID.get("Sub-ordinate"))) {
					if(!workFlowOrderList.contains(rs.getString("subordinate"))) {
						workFlowOrderList.add(rs.getString("subordinate"));
					}
					List<String> innerList = hmWorkFlowOrder.get(rs.getString("subordinate"));
					if(innerList==null) innerList = new ArrayList<String>();
					innerList.add("Sub-ordinate");
					hmWorkFlowOrder.put(rs.getString("subordinate"), innerList);
				}
				
				if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Peer") != null    && memberList.contains(hmOrientMemberID.get("Peer"))) {
					if(!workFlowOrderList.contains(rs.getString("peer"))) {
						workFlowOrderList.add(rs.getString("peer"));
					}
					List<String> innerList = hmWorkFlowOrder.get(rs.getString("peer"));
					if(innerList==null) innerList = new ArrayList<String>();
					innerList.add("Peer");
					hmWorkFlowOrder.put(rs.getString("peer"), innerList);
				}
				
				if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Manager") != null  && memberList.contains(hmOrientMemberID.get("Manager"))) {
					if(!workFlowOrderList.contains(rs.getString("manager"))) {
						workFlowOrderList.add(rs.getString("manager"));
					}
					List<String> innerList = hmWorkFlowOrder.get(rs.getString("manager"));
					if(innerList==null) innerList = new ArrayList<String>();
					innerList.add(MANAGER);
					hmWorkFlowOrder.put(rs.getString("manager"), innerList);
				}
				
				if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("CEO") != null  && memberList.contains(hmOrientMemberID.get("CEO"))) {
					if(!workFlowOrderList.contains(rs.getString("ceo"))) {
						workFlowOrderList.add(rs.getString("ceo"));
					}
					List<String> innerList = hmWorkFlowOrder.get(rs.getString("ceo"));
					if(innerList==null) innerList = new ArrayList<String>();
					innerList.add(CEO);
					hmWorkFlowOrder.put(rs.getString("ceo"), innerList);
				}
				
				if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HOD") != null  && memberList.contains(hmOrientMemberID.get("HOD"))) {
					if(!workFlowOrderList.contains(rs.getString("hod"))) {
						workFlowOrderList.add(rs.getString("hod"));
					}
					List<String> innerList = hmWorkFlowOrder.get(rs.getString("hod"));
					if(innerList==null) innerList = new ArrayList<String>();
					innerList.add(HOD);
					hmWorkFlowOrder.put(rs.getString("hod"), innerList);
				}
				
				if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HR") != null && memberList.contains(hmOrientMemberID.get("HR"))) {
					if(!workFlowOrderList.contains(rs.getString("hr"))) {
						workFlowOrderList.add(rs.getString("hr"));
					}
					List<String> innerList = hmWorkFlowOrder.get(rs.getString("hr"));
					if(innerList==null) innerList = new ArrayList<String>();
					innerList.add(HRMANAGER);
					hmWorkFlowOrder.put(rs.getString("hr"), innerList);
				}
				
				if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("GroupHead") != null  && memberList.contains(hmOrientMemberID.get("GroupHead"))) {
					if(!workFlowOrderList.contains(rs.getString("grouphead"))) {
						workFlowOrderList.add(rs.getString("grouphead"));
					}
					List<String> innerList = hmWorkFlowOrder.get(rs.getString("grouphead"));
					if(innerList==null) innerList = new ArrayList<String>();
					innerList.add("GroupHead");
					hmWorkFlowOrder.put(rs.getString("grouphead"), innerList);
				}
				
				if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Other Peer") != null  && memberList.contains(hmOrientMemberID.get("Other Peer"))) {
					if(!workFlowOrderList.contains(rs.getString("other_peer"))) {
						workFlowOrderList.add(rs.getString("other_peer"));
					}
					List<String> innerList = hmWorkFlowOrder.get(rs.getString("other_peer"));
					if(innerList==null) innerList = new ArrayList<String>();
					innerList.add("Other Peer");
					hmWorkFlowOrder.put(rs.getString("other_peer"), innerList);
				}
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmWorkFlowOrder", hmWorkFlowOrder);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return workFlowOrderList;
	
	}
	
	
		
	private void getAppraisalSectionWorkFlow(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			String hrIDS = null,subordinateIDS=null, managerIDS = null,peerIDS = null,otherPeerIDS=null, grandSupervisorIDS=null, orientType=null, ceoIds=null, hodIds=null, otherIds=null;
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst = con.prepareStatement("select * from appraisal_reviewee_details where appraisal_id=? and reviewee_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				subordinateIDS = rs.getString("subordinate_ids");
				peerIDS = rs.getString("peer_ids");
				otherPeerIDS = rs.getString("other_peer_ids"); 
				managerIDS = rs.getString("supervisor_ids");
				grandSupervisorIDS = rs.getString("grand_supervisor_ids");
				hodIds = rs.getString("hod_ids");
				ceoIds = rs.getString("ceo_ids");
				hrIDS = rs.getString("hr_ids");
				otherIds = rs.getString("other_ids");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select hr_ids,peer_ids,supervisor_id,oriented_type,ceo_ids,hod_ids from appraisal_details where appraisal_details_id=? ");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			while (rs.next()) {
				orientType = rs.getString("oriented_type");
			}
			rs.close();
			pst.close();
			
			
			
//			pst = con.prepareStatement("select supervisor_emp_id from employee_official_details where emp_id = ?");
//			pst.setInt(1, uF.parseToInt(empId));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				managerID = rs.getString("supervisor_emp_id");
//			}
			
			List<String> memberList= CF.getOrientationMemberDetails(con,uF.parseToInt(orientType));
			Map<String, String> hmUserName = new HashMap<String, String>();
			pst = con.prepareStatement("select emp_per_id,emp_fname,emp_mname, emp_lname from employee_personal_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmUserName.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();

			boolean empFlag=false,subordinateFlag=false,hrFlag=false,managerFlag=false,grandManagerFlag=false,peerFlag=false,otherPeerFlag=false,ceoFlag=false,hodFlag=false,otherFlag=false;
			List<String> subordinateIDList = getListData1(subordinateIDS);
			List<String> ansGivenSubordinateIDList = new ArrayList<String>();
			List<String> peerIDList = getListData1(peerIDS);
			List<String> ansGivenPeerIDList = new ArrayList<String>();
			List<String> otherPeerIDList = getListData1(otherPeerIDS);
			List<String> ansGivenOtherPeerIDList = new ArrayList<String>();
			List<String> managerIDList = getListData1(managerIDS);
			List<String> ansGivenManagerIDList = new ArrayList<String>();
			List<String> grandManagerIDList = getListData1(grandSupervisorIDS);
			List<String> ansGivenGrandManagerIDList = new ArrayList<String>();
			List<String> ceoIDList = getListData1(ceoIds);
			List<String> ansGivenCEOIDList = new ArrayList<String>();
			List<String> hodIDList = getListData1(hodIds);
			List<String> ansGivenHODIDList = new ArrayList<String>();
			List<String> hrIDList = getListData1(hrIDS);
			List<String> ansGivenHRIDList = new ArrayList<String>();
			List<String> otherIDList = getListData1(otherIds);
			List<String> ansGivenOtherIDList = new ArrayList<String>();
			
			pst = con.prepareStatement("select user_type_id,user_id from appraisal_question_answer where is_submit=true and appraisal_id=? and section_id=? and emp_id=? and appraisal_freq_id=? group by user_type_id,user_id");
			pst.setInt(1, uF.parseToInt(id));
			pst.setInt(2, uF.parseToInt(sectionId));
			pst.setInt(3, uF.parseToInt(empId));
			pst.setInt(4, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				if(rs.getString("user_id").equals(empId) && rs.getString("user_type_id").equals("3")) {
					empFlag = true;
				}
				
				for(int a=0; subordinateIDList!=null && a<subordinateIDList.size(); a++) {
					if(rs.getString("user_id").equals(subordinateIDList.get(a)) && rs.getString("user_type_id").equals("6")) {
						ansGivenSubordinateIDList.add(rs.getString("user_id"));
					}
				}
				if(subordinateIDList.size()==ansGivenSubordinateIDList.size()) {
					subordinateFlag = true;
				}
				
				for(int a=0; peerIDList!=null && a< peerIDList.size(); a++) {
					if(rs.getString("user_id").equals(peerIDList.get(a)) && rs.getString("user_type_id").equals("4")) {
						ansGivenPeerIDList.add(rs.getString("user_id"));
					}
				}
				if(peerIDList.size()==ansGivenPeerIDList.size()) {
					peerFlag = true;
				}
				
				for(int a=0; otherPeerIDList != null && a< otherPeerIDList.size(); a++) {
					if(rs.getString("user_id").equals(otherPeerIDList.get(a)) && rs.getString("user_type_id").equals("14")) {
						ansGivenOtherPeerIDList.add(rs.getString("user_id"));
					}
				}
				if(otherPeerIDList.size()==ansGivenOtherPeerIDList.size()) {
					otherPeerFlag = true;
				}
				
				for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
					if(rs.getString("user_id").equals(managerIDList.get(a)) && rs.getString("user_type_id").equals("2")) {
						ansGivenManagerIDList.add(rs.getString("user_id"));
					}
				}
				if(managerIDList.size()==ansGivenManagerIDList.size()) {
					managerFlag = true;
				}
				
				for(int a=0; grandManagerIDList!=null && a< grandManagerIDList.size(); a++) {
					if(rs.getString("user_id").equals(grandManagerIDList.get(a)) && rs.getString("user_type_id").equals("8")) {
						ansGivenGrandManagerIDList.add(rs.getString("user_id"));
					}
				}
				if(grandManagerIDList.size()==ansGivenGrandManagerIDList.size()) {
					grandManagerFlag = true;
				}

				for(int a=0; ceoIDList!=null && a< ceoIDList.size(); a++) {
					if(rs.getString("user_id").equals(ceoIDList.get(a)) && rs.getString("user_type_id").equals("5")) {
						ansGivenCEOIDList.add(rs.getString("user_id"));
					}
				}
				if(ceoIDList.size()==ansGivenCEOIDList.size()) {
					ceoFlag = true;
				}
				
				for(int a=0; hodIDList != null && a< hodIDList.size(); a++) {
					if(rs.getString("user_id").equals(hodIDList.get(a)) && rs.getString("user_type_id").equals("13")) {
						ansGivenHODIDList.add(rs.getString("user_id"));
					}
				}
				if(hodIDList.size()==ansGivenHODIDList.size()) {
					hodFlag = true;
				}
				
				for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
					if(rs.getString("user_id").equals(hrIDList.get(a)) && rs.getString("user_type_id").equals("7")) {
						ansGivenHRIDList.add(rs.getString("user_id"));
					}
				}
				if(hrIDList.size()==ansGivenHRIDList.size()) {
					hrFlag = true;
				}
				
				for(int a=0; otherIDList != null && a< otherIDList.size(); a++) {
					if(rs.getString("user_id").equals(otherIDList.get(a)) && rs.getString("user_type_id").equals("10")) {
						ansGivenOtherIDList.add(rs.getString("user_id"));
					}
				}
				if(otherIDList.size()==ansGivenOtherIDList.size()) {
					otherFlag = true;
				}
//				workFlowOrderList.add(rs.getString("hr"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("empFlag ===> "+ empFlag);
//			System.out.println("hrFlag ===> "+ hrFlag);
//			System.out.println("managerFlag ===> "+ managerFlag);
//			System.out.println("peerFlag ===> "+ peerFlag);
			
		 Map<String, String> hmOrientMemberID = CF.getOrientMemberID(con);
		 List<String> workFlowOrderList = getAppraisalSubSectionAndWorkFlowOrder(uF);
//		 System.out.println("workFlowOrderList ===>> " + workFlowOrderList);
		 Collections.sort(workFlowOrderList);
		 List<String> sortWorkFlowOrderList = workFlowOrderList;
//		 System.out.println("sortWorkFlowOrderList ===>> " + sortWorkFlowOrderList);
		 List<String> unsortWorkFlowOrderList = getAppraisalSubSectionAndWorkFlowOrder(uF);
//		 System.out.println("unsortWorkFlowOrderList ===>> " + unsortWorkFlowOrderList);
		 List<String> workFlowOrderNameList = new ArrayList<String>();
//		 List<String> workFlowOrderNameList1 = new ArrayList<String>();
		 
		 Map<String, List<String>> hmWorkFlowOrder = (Map<String, List<String>>) request.getAttribute("hmWorkFlowOrder");
		 if(hmWorkFlowOrder == null) hmWorkFlowOrder = new HashMap<String, List<String>>();
//		 System.out.println("hmWorkFlowOrder ===>> " + hmWorkFlowOrder);
		 
		 StringBuilder allMemberNames= new StringBuilder();
		 allMemberNames.append("<table><tr>"); 
		 String memberStep=null,strMemberPosition=null;
		 
		 for(int i=0; sortWorkFlowOrderList != null && i< sortWorkFlowOrderList.size(); i++) {
			 allMemberNames.append("<td style=\"vertical-align: top;\">");
			 allMemberNames.append("<div style=\"font-size: 14px;\"><label>STEP- "+sortWorkFlowOrderList.get(i)+" </label></div>");
			 List<String> innerList = hmWorkFlowOrder.get(sortWorkFlowOrderList.get(i));
			 if(innerList==null) innerList = new ArrayList<String>();
//			 for(int j=0; unsortWorkFlowOrderList != null && j< unsortWorkFlowOrderList.size(); j++) {
//				 if(sortWorkFlowOrderList.get(i).equals(unsortWorkFlowOrderList.get(j))) {
			 for(int j=0; innerList != null && j<innerList.size(); j++) {
				 if(!workFlowOrderNameList.contains("Self") && innerList.get(j).equals(EMPLOYEE)) {
					 workFlowOrderNameList.add("Self");
					if(empFlag == true) {
						allMemberNames.append("<div><label>SELF</label> <i class=\"fa fa-long-arrow-right\" aria-hidden=\"true\" style=\"color:#28db35;font-size: 18px;\"></i></div>");
					} else {
						allMemberNames.append("<div><label>SELF</label> <i class=\"fa fa-long-arrow-right\" aria-hidden=\"true\" style=\"font-size: 18px;\"></i></div>");
					}
					allMemberNames.append("<div>"+ hmUserName.get(empId)+"");
					if(empFlag) {
						allMemberNames.append("<i class=\"fa fa-check\" aria-hidden=\"true\" title=\"Completed\"></i>");
					} else {
						allMemberNames.append("<i class=\"fa fa-minus-circle\" aria-hidden=\"true\" title=\"Pending\"></i>");
					}
					allMemberNames.append("</div>");
				} else if(!workFlowOrderNameList.contains("Sub-ordinate") && innerList.get(j).equals("Sub-ordinate")) {
					workFlowOrderNameList.add("Sub-ordinate");
					if(subordinateFlag == true) {
						allMemberNames.append("<div><label>SUBORDINATE</label> <i class=\"fa fa-long-arrow-right\" aria-hidden=\"true\" style=\"color:#28db35;font-size: 18px;\"></i></div>");
					} else {
						allMemberNames.append("<div><label>SUBORDINATE</label> <i class=\"fa fa-long-arrow-right\" aria-hidden=\"true\" style=\"font-size: 18px;\"></i></div>");
					}
					for(int a=0; subordinateIDList != null && a< subordinateIDList.size(); a++) {
						allMemberNames.append("<div>"+ hmUserName.get(subordinateIDList.get(a))+"");
						if(ansGivenSubordinateIDList.contains(subordinateIDList.get(a))) {
							allMemberNames.append("<i class=\"fa fa-check\" aria-hidden=\"true\" title=\"Completed\"></i>");
						} else {
							allMemberNames.append("<i class=\"fa fa-minus-circle\" aria-hidden=\"true\" title=\"Pending\"></i>");
						}
						allMemberNames.append("</div>");
					}
				} else if(!workFlowOrderNameList.contains("Peer") && innerList.get(j).equals("Peer")) {
					workFlowOrderNameList.add("Peer");
					if(peerFlag == true) {
						allMemberNames.append("<div><label>PEER</label> <i class=\"fa fa-long-arrow-right\" aria-hidden=\"true\" style=\"color:#28db35;font-size: 18px;\"></i></div>");
					} else {
						allMemberNames.append("<div><label>PEER</label> <i class=\"fa fa-long-arrow-right\" aria-hidden=\"true\" style=\"font-size: 18px;\"></i></div>");
					}
					for(int a=0; peerIDList != null && a< peerIDList.size(); a++) {
						allMemberNames.append("<div>"+ hmUserName.get(peerIDList.get(a))+"");
						if(ansGivenPeerIDList.contains(peerIDList.get(a))) {
							allMemberNames.append("<i class=\"fa fa-check\" aria-hidden=\"true\" title=\"Completed\"></i>");
						} else {
							allMemberNames.append("<i class=\"fa fa-minus-circle\" aria-hidden=\"true\" title=\"Pending\"></i>");
						}
						allMemberNames.append("</div>");
					}
				} else if(!workFlowOrderNameList.contains("Other Peer") && innerList.get(j).equals("Other Peer")) {
					workFlowOrderNameList.add("Other Peer");
					if(otherPeerFlag == true) {
						allMemberNames.append("<div><label>OTHER PEER</label> <i class=\"fa fa-long-arrow-right\" aria-hidden=\"true\" style=\"color:#28db35;font-size: 18px;\"></i></div>");
					} else {
						allMemberNames.append("<div><label>OTHER PEER</label> <i class=\"fa fa-long-arrow-right\" aria-hidden=\"true\" style=\"font-size: 18px;\"></i></div>");
					}
					for(int a=0; otherPeerIDList != null && a< otherPeerIDList.size(); a++) {
						allMemberNames.append("<div>"+ hmUserName.get(otherPeerIDList.get(a))+"");
						if(ansGivenOtherPeerIDList.contains(otherPeerIDList.get(a))) {
							allMemberNames.append("<i class=\"fa fa-check\" aria-hidden=\"true\" title=\"Completed\"></i>");
						} else {
							allMemberNames.append("<i class=\"fa fa-minus-circle\" aria-hidden=\"true\" title=\"Pending\"></i>");
						}
						allMemberNames.append("</div>");
					 }
				} else if(!workFlowOrderNameList.contains("Manager") && innerList.get(j).equals(MANAGER)) {
					workFlowOrderNameList.add("Manager");
					if(managerFlag == true) {
						allMemberNames.append("<div><label>MANAGER</label> <i class=\"fa fa-long-arrow-right\" aria-hidden=\"true\" style=\"color:#28db35;font-size: 18px;\"></i></div>");
					} else {
						allMemberNames.append("<div><label>MANAGER</label> <i class=\"fa fa-long-arrow-right\" aria-hidden=\"true\" style=\"font-size: 18px;\"></i></div>");
					}
					for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
						allMemberNames.append("<div>"+ hmUserName.get(managerIDList.get(a))+"");
						if(ansGivenManagerIDList.contains(managerIDList.get(a))) {
							allMemberNames.append("<i class=\"fa fa-check\" aria-hidden=\"true\" title=\"Completed\"></i>");
						} else {
							allMemberNames.append("<i class=\"fa fa-minus-circle\" aria-hidden=\"true\" title=\"Pending\"></i>");
						}
						allMemberNames.append("</div>");
					}
				} else if(!workFlowOrderNameList.contains("GroupHead") && innerList.get(j).equals("GroupHead")) {
					workFlowOrderNameList.add("GroupHead");
					if(grandManagerFlag == true) {
						allMemberNames.append("<div><label>GRAND MANAGER</label> <i class=\"fa fa-long-arrow-right\" aria-hidden=\"true\" style=\"color:#28db35;font-size: 18px;\"></i></div>");
					} else {
						allMemberNames.append("<div><label>GRAND MANAGER</label> <i class=\"fa fa-long-arrow-right\" aria-hidden=\"true\" style=\"font-size: 18px;\"></i></div>");
					}
					for(int a=0; grandManagerIDList != null && a< grandManagerIDList.size(); a++) {
						allMemberNames.append("<div>"+ hmUserName.get(grandManagerIDList.get(a))+"");
						if(ansGivenGrandManagerIDList.contains(grandManagerIDList.get(a))) {
							allMemberNames.append("<i class=\"fa fa-check\" aria-hidden=\"true\" title=\"Completed\"></i>");
						} else {
							allMemberNames.append("<i class=\"fa fa-minus-circle\" aria-hidden=\"true\" title=\"Pending\"></i>");
						}
						allMemberNames.append("</div>");
					}
				} else if(!workFlowOrderNameList.contains(CEO) && innerList.get(j).equals(CEO)) {
					workFlowOrderNameList.add(CEO);
					if(ceoFlag == true) {
						allMemberNames.append("<div><label>"+CEO+"</label> <i class=\"fa fa-long-arrow-right\" aria-hidden=\"true\" style=\"color:#28db35;font-size: 18px;\"></i></div>");
					} else {
						allMemberNames.append("<div><label>"+CEO+"</label> <i class=\"fa fa-long-arrow-right\" aria-hidden=\"true\" style=\"font-size: 18px;\"></i></div>");
					}
					for(int a=0; ceoIDList != null && a< ceoIDList.size(); a++) {
						allMemberNames.append("<div>"+ hmUserName.get(ceoIDList.get(a))+"");
						if(ansGivenCEOIDList.contains(ceoIDList.get(a))) {
							allMemberNames.append("<i class=\"fa fa-check\" aria-hidden=\"true\" title=\"Completed\"></i>");
						} else {
							allMemberNames.append("<i class=\"fa fa-minus-circle\" aria-hidden=\"true\" title=\"Pending\"></i>");
						}
						allMemberNames.append("</div>");
					}
				} else if(!workFlowOrderNameList.contains(HOD) && innerList.get(j).equals(HOD)) {
					workFlowOrderNameList.add(HOD);
					if(hodFlag == true) {
						allMemberNames.append("<div><label>"+HOD+"</label> <i class=\"fa fa-long-arrow-right\" aria-hidden=\"true\" style=\"color:#28db35;font-size: 18px;\"></i></div>");
					} else {
						allMemberNames.append("<div><label>"+HOD+"</label> <i class=\"fa fa-long-arrow-right\" aria-hidden=\"true\" style=\"font-size: 18px;\"></i></div>");
					}
					for(int a=0; hodIDList != null && a< hodIDList.size(); a++) {
						allMemberNames.append("<div>"+ hmUserName.get(hodIDList.get(a))+"");
						if(ansGivenHODIDList.contains(hodIDList.get(a))) {
							allMemberNames.append("<i class=\"fa fa-check\" aria-hidden=\"true\" title=\"Completed\"></i>");
						} else {
							allMemberNames.append("<i class=\"fa fa-minus-circle\" aria-hidden=\"true\" title=\"Pending\"></i>");
						}
						allMemberNames.append("</div>");
					}
				} else if(!workFlowOrderNameList.contains(HRMANAGER) && innerList.get(j).equals(HRMANAGER)) {
					workFlowOrderNameList.add(HRMANAGER);
					if(hrFlag == true) {
						allMemberNames.append("<div><label>"+HRMANAGER+"</label> <i class=\"fa fa-long-arrow-right\" aria-hidden=\"true\" style=\"color:#28db35;font-size: 18px;\"></i></div>");
					} else {
						allMemberNames.append("<div><label>"+HRMANAGER+"</label> <i class=\"fa fa-long-arrow-right\" aria-hidden=\"true\" style=\"font-size: 18px;\"></i></div>");
					}
					for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
						allMemberNames.append("<div>"+ hmUserName.get(hrIDList.get(a))+"");
						if(ansGivenHRIDList.contains(hrIDList.get(a))) {
							allMemberNames.append("<i class=\"fa fa-check\" aria-hidden=\"true\" title=\"Completed\"></i>");
						} else {
							allMemberNames.append("<i class=\"fa fa-minus-circle\" aria-hidden=\"true\" title=\"Pending\"></i>");
						}
						allMemberNames.append("</div>");
					}
				}
			 }
			 allMemberNames.append("</td>");
//			 System.out.println("workFlowOrderNameList ===>" + workFlowOrderNameList);
		 }
		 
//		 	allMemberNames.append("<td>");
			/*for(int i=0; sortWorkFlowOrderList != null && i< sortWorkFlowOrderList.size(); i++) {
				 allMemberNames.append("<td>");
				 List<String> innerList = hmWorkFlowOrder.get(sortWorkFlowOrderList.get(i));
				 if(innerList==null) innerList = new ArrayList<String>();
//				 for(int j=0; unsortWorkFlowOrderList != null && j< unsortWorkFlowOrderList.size(); j++) {
//					 if(sortWorkFlowOrderList.get(i).equals(unsortWorkFlowOrderList.get(j))) {
						 if(!workFlowOrderNameList.contains("HR") && innerList.contains(HRMANAGER)) {
//							 List<String> hrIDList = getListData1(hrIDS);
	//						 System.out.println("hrIDList 3 ===> "+hrIDList);
							 allMemberNames.append("<table>");
							 workFlowOrderNameList.add("HR");
							 if(memberStep==null) {
								 for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
									 memberStep = "<label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
								 }
								if(hrFlag == true) {
									allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_green_big.png\" style=\"height: 10px;\"/></td></tr>");
								} else {
									allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/></td></tr>");
								}
								strMemberPosition=sortWorkFlowOrderList.get(i);
							 } else {
								if(strMemberPosition!=null && strMemberPosition.equals(sortWorkFlowOrderList.get(i))) {
									for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
										memberStep+=" or "+" <label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
									 }
									if(hrFlag == true) {
										allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_green_big.png\" style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/></td></tr>");
									}
								} else if(i == sortWorkFlowOrderList.size()-1) {
									for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
										memberStep+=" or "+" <label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
									 }
									if(hrFlag == true) {
										allMemberNames.append("<tr><td>HRMANAGER");
									} else {
										allMemberNames.append("<tr><td>HRMANAGER");
									}
								} else {
									strMemberPosition=sortWorkFlowOrderList.get(i);
									for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
										memberStep+=" <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/> <label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
									 }
									if(hrFlag == true) {
										allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_green_big.png\" style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/></td></tr>");
									}
								}
							}
							 allMemberNames.append("</table>");
						 } else if(!workFlowOrderNameList.contains("Manager") && innerList.contains(MANAGER)) {
//							 List<String> managerIDList = getListData1(managerIDS);
							 System.out.println("managerIDList 3 ===> "+managerIDList);
							 allMemberNames.append("<table>");
							 workFlowOrderNameList.add("Manager");
							 if(memberStep==null) {
								 for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
									 memberStep ="<label>"+ hmUserName.get(managerIDList.get(a))+"</label> ";
								 }
								if(managerFlag == true) {
									allMemberNames.append("<tr><td>MANAGER <img src=\"images1/arrow_green_big.png\" style=\"height: 10px;\"/></td></tr>");
								} else {
									allMemberNames.append("<tr><td>MANAGER <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/></td></tr>");
								}
								strMemberPosition=sortWorkFlowOrderList.get(i);
							 } else {
								if(strMemberPosition!=null && strMemberPosition.equals(sortWorkFlowOrderList.get(i))) {
									for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
										memberStep+=" or "+" <label>"+ hmUserName.get(managerIDList.get(a))+"</label> ";
									 }
									if(managerFlag == true) {
										allMemberNames.append("<tr><td>MANAGER <img src=\"images1/arrow_green_big.png\" style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>MANAGER <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/></td></tr>");
									}
								} else if(i == sortWorkFlowOrderList.size()-1) {
									for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
										memberStep+=" or "+" <label>"+ hmUserName.get(managerIDList.get(a))+"</label> ";
									 }
									if(managerFlag == true) {
										allMemberNames.append("<tr><td>MANAGER");
									} else {
										allMemberNames.append("<tr><td>MANAGER");
									}
								} else {
									strMemberPosition=sortWorkFlowOrderList.get(i);
									for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
										memberStep+=" <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/> <label>"+ hmUserName.get(managerIDList.get(a))+"</label> ";
									 }
									if(managerFlag == true) {
										allMemberNames.append("<tr><td>MANAGER <img src=\"images1/arrow_green_big.png\" style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>MANAGER <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/></td></tr>");
									}
								}
							}
							 allMemberNames.append("</table>");
						 } else if(!workFlowOrderNameList.contains("Peer") && innerList.contains("PEER")) {
							 
							 allMemberNames.append("<table>");
							 workFlowOrderNameList.add("Peer");
							 if(memberStep==null) {
								 if(peerFlag == true) {
									 allMemberNames.append("<tr><td>PEER <img src=\"images1/arrow_green_big.png\" style=\"height: 10px;\"/></td></tr>");
								 } else {
									allMemberNames.append("<tr><td>PEER <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/></td></tr>");
								}
								memberStep="Peer";
								strMemberPosition=sortWorkFlowOrderList.get(i);
							 } else {
								if(strMemberPosition!=null && strMemberPosition.equals(sortWorkFlowOrderList.get(i))) {
									memberStep+=" or "+ "Peer";
									if(peerFlag == true) {
										allMemberNames.append("<tr><td>PEER <img src=\"images1/arrow_green_big.png\" style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>PEER <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/></td></tr>");
									}
								} else if(i == sortWorkFlowOrderList.size()-1) {
									memberStep+=" or "+" <label>"+ hmUserName.get(empId)+"</label> ";
									if(peerFlag == true) {
										allMemberNames.append("<tr><td>PEER");
									} else {
										allMemberNames.append("<tr><td>PEER");
									}
								} else {
									strMemberPosition=sortWorkFlowOrderList.get(i);
									memberStep+=" <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/> "+ "Peer";
									if(peerFlag == true) {
										allMemberNames.append("<tr><td>PEER <img src=\"images1/arrow_green_big.png\" style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>PEER <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/></td></tr>");
									}
								}
							}
							 allMemberNames.append("</table>");
						 } else if(!workFlowOrderNameList.contains("Self") && innerList.contains(EMPLOYEE)) {
							 System.out.println("hmUserName.get(empId) ===>> " + hmUserName.get(empId));
							 allMemberNames.append("<table>");
							 workFlowOrderNameList.add("Self");
							 if(memberStep==null) {
									memberStep ="<label>"+ hmUserName.get(empId)+"</label> ";
									if(empFlag == true) {
										allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_green_big.png\" style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/></td></tr>");
									}
								strMemberPosition=sortWorkFlowOrderList.get(i);
							 } else {
								if(strMemberPosition!=null && strMemberPosition.equals(sortWorkFlowOrderList.get(i))) {
									memberStep+=" or "+" <label>"+ hmUserName.get(empId)+"</label> ";
									if(empFlag == true) {
										allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_green_big.png\" style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/></td></tr>");
									}
								} else if(i == sortWorkFlowOrderList.size()-1) {
									memberStep+=" or "+" <label>"+ hmUserName.get(empId)+"</label> ";
									if(empFlag == true) {
										allMemberNames.append("<tr><td>SELF");
									} else {
										allMemberNames.append("<tr><td>SELF");
									}
								} else {
									strMemberPosition=sortWorkFlowOrderList.get(i);
									memberStep+=" <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/> <label>"+ hmUserName.get(empId)+"</label>";
									if(empFlag == true) {
										allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_green_big.png\" style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/></td></tr>");
									}
								}
							}
							 allMemberNames.append("</table>");
							 
						 } else if(!workFlowOrderNameList.contains("HOD") && innerList.contains(HOD)) {
//							 List<String> hodIDList = getListData1(hodIds);
//							 System.out.println("managerIDList 1 ===> "+managerIDList);
							 allMemberNames.append("<table>");
							 workFlowOrderNameList.add("HOD");
							 if(memberStep==null) {
								 for(int a=0; hodIDList != null && a< hodIDList.size(); a++) {
									 memberStep ="<label>"+ hmUserName.get(hodIDList.get(a))+"</label> ";
								 }
//								memberStep ="<label>"+ hmUserName.get(managerID)+"</label> ";
								if(managerFlag == true) {
									allMemberNames.append("<tr><td>HOD <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
								} else {
									allMemberNames.append("<tr><td>HOD <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
								}
								strMemberPosition=sortWorkFlowOrderList.get(i);
							} else {
								if(strMemberPosition!=null && strMemberPosition.equals(sortWorkFlowOrderList.get(i))) {
									for(int a=0; hodIDList != null && a< hodIDList.size(); a++) {
										memberStep+=" or "+" <label>"+ hmUserName.get(hodIDList.get(a))+"</label> ";
									 }
//									memberStep+=" or "+" <label>"+ hmUserName.get(managerID)+"</label> ";
									if(managerFlag == true) {
										allMemberNames.append("<tr><td>HOD <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>HOD <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
									}
								} else if(i == sortWorkFlowOrderList.size()-1) {
									for(int a=0; hodIDList != null && a< hodIDList.size(); a++) {
										memberStep+=" or "+" <label>"+ hmUserName.get(hodIDList.get(a))+"</label> ";
									 }
//									memberStep+=" or "+" <label>"+ hmUserName.get(managerID)+"</label> ";
									if(managerFlag == true) {
										allMemberNames.append("<tr><td>HOD");
									} else {
										allMemberNames.append("<tr><td>HOD");
									}
								} else {
									strMemberPosition=sortWorkFlowOrderList.get(i);
									for(int a=0; hodIDList != null && a< hodIDList.size(); a++) {
										memberStep+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(hodIDList.get(a))+"</label> ";
									 }
//									memberStep+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(managerID)+"</label>";
									if(managerFlag == true) {
										allMemberNames.append("<tr><td>HOD <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>HOD <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
									}
								}
							}
							allMemberNames.append("</table>");
							 
						 } else if(!workFlowOrderNameList.contains("CEO") && innerList.contains(CEO)) {
//							 List<String> ceoIDList = getListData1(ceoIds);
//							 System.out.println("managerIDList 1 ===> "+managerIDList);
							 allMemberNames.append("<table>");
							 workFlowOrderNameList.add("CEO");
							 if(memberStep==null) {
								 for(int a=0; ceoIDList != null && a< ceoIDList.size(); a++) {
									 memberStep ="<label>"+ hmUserName.get(ceoIDList.get(a))+"</label> ";
								 }
//								memberStep ="<label>"+ hmUserName.get(managerID)+"</label> ";
								if(managerFlag == true) {
									allMemberNames.append("<tr><td>CEO <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
								} else {
									allMemberNames.append("<tr><td>CEO <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
								}
								strMemberPosition=sortWorkFlowOrderList.get(i);
							} else {
								if(strMemberPosition!=null && strMemberPosition.equals(sortWorkFlowOrderList.get(i))) {
									for(int a=0; ceoIDList != null && a< ceoIDList.size(); a++) {
										memberStep+=" or "+" <label>"+ hmUserName.get(ceoIDList.get(a))+"</label> ";
									 }
//									memberStep+=" or "+" <label>"+ hmUserName.get(managerID)+"</label> ";
									if(managerFlag == true) {
										allMemberNames.append("<tr><td>CEO <img src=\"images1/arrow_green_big.png\" style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>CEO <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/></td></tr>");
									}
								} else if(i == sortWorkFlowOrderList.size()-1) {
									for(int a=0; ceoIDList != null && a< ceoIDList.size(); a++) {
										memberStep+=" or "+" <label>"+ hmUserName.get(ceoIDList.get(a))+"</label> ";
									 }
//									memberStep+=" or "+" <label>"+ hmUserName.get(managerID)+"</label> ";
									if(managerFlag == true) {
										allMemberNames.append("<tr><td>CEO");
									} else {
										allMemberNames.append("<tr><td>CEO");
									}
								} else {
									strMemberPosition=sortWorkFlowOrderList.get(i);
									for(int a=0; ceoIDList != null && a< ceoIDList.size(); a++) {
										memberStep+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(ceoIDList.get(a))+"</label> ";
									 }
//									memberStep+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(managerID)+"</label>";
									if(managerFlag == true) {
										allMemberNames.append("<tr><td>CEO <img src=\"images1/arrow_green_big.png\" style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>CEO <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/></td></tr>");
									}
								}
							}
							 allMemberNames.append("</table>");
						 }
				 allMemberNames.append("</td>");
//				 System.out.println("workFlowOrderNameList ===>" + workFlowOrderNameList);
			 }*/
			
			 /*allMemberNames.append("</tr><tr>");
			 String memberStep1=null,strMemberPosition1=null;
//			 allMemberNames.append("<td>");
			 for(int i=0; sortWorkFlowOrderList != null && i< sortWorkFlowOrderList.size(); i++) {
				 allMemberNames.append("<td>");
			 		if(!workFlowOrderNameList1.contains("HR") && hmWorkFlowOrder.get(sortWorkFlowOrderList.get(i)).equals(HRMANAGER)) {
						 List<String> hrIDList = getListData1(hrIDS);
//						 System.out.println("hrIDList 33 ===> "+hrIDList);
						 allMemberNames.append("<table>");
						 workFlowOrderNameList1.add("HR");
						 if(memberStep1==null) {
							 for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
								 memberStep1 = "<label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
								 allMemberNames.append("<tr><td>"+hmUserName.get(hrIDList.get(a))+"</td></tr>");
							 }
							strMemberPosition1=sortWorkFlowOrderList.get(i);
						 } else {
							if(strMemberPosition1!=null && strMemberPosition1.equals(sortWorkFlowOrderList.get(i))) {
								for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
									memberStep1+=" or "+" <label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
									 allMemberNames.append("<tr><td>"+hmUserName.get(hrIDList.get(a))+"</td></tr>");
								 }
							} else {
								strMemberPosition1=sortWorkFlowOrderList.get(i);
								for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
									memberStep1+=" <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/> <label>"+ hmUserName.get(hrIDList.get(a))+"</label>";
									allMemberNames.append("<tr><td>"+hmUserName.get(hrIDList.get(a))+"</td></tr>");
								 }
							}
						}
						 allMemberNames.append("</table>");
						 
					 } else if(!workFlowOrderNameList1.contains("Manager") && hmWorkFlowOrder.get(sortWorkFlowOrderList.get(i)).equals(MANAGER)) {
						 List<String> managerIDList = getListData1(managerIDS);
//						 System.out.println("managerIDList 33 ===> "+managerIDList);
						 allMemberNames.append("<table>");
						 workFlowOrderNameList1.add("Manager");
						 if(memberStep1==null) {
							 for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
								 memberStep1 ="<label>"+ hmUserName.get(managerIDList.get(a))+"</label> ";
								 allMemberNames.append("<tr><td>"+hmUserName.get(managerIDList.get(a))+"</td></tr>");
							 }
							strMemberPosition1=sortWorkFlowOrderList.get(i);
						 } else {
							if(strMemberPosition1!=null && strMemberPosition1.equals(sortWorkFlowOrderList.get(i))) {
								for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
									memberStep1+=" or "+" <label>"+ hmUserName.get(managerIDList.get(a))+"</label> ";
									 allMemberNames.append("<tr><td>"+hmUserName.get(managerIDList.get(a))+"</td></tr>");
								 }
							} else {
								strMemberPosition1=sortWorkFlowOrderList.get(i);
								for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
									memberStep1+=" <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/> <label>"+ hmUserName.get(managerIDList.get(a))+"</label>";
									 allMemberNames.append("<tr><td>"+hmUserName.get(managerIDList.get(a))+"</td></tr>");
								 }
							}
						}
						 allMemberNames.append("</table>");
						 
					 } else if(!workFlowOrderNameList1.contains("Peer") && hmWorkFlowOrder.get(sortWorkFlowOrderList.get(i)).equals("PEER")) {
						 List<String> peerIDList = getListData1(peerIDS);
//						 System.out.println("peerIDList 33 ===> "+peerIDList);
						 allMemberNames.append("<table>");
						 workFlowOrderNameList1.add("Peer");
						 if(memberStep1==null) {
							 for(int a=0; peerIDList != null && a< peerIDList.size(); a++) {
								 allMemberNames.append("<tr><td>"+hmUserName.get(peerIDList.get(a))+"</td></tr>");
							 }
							 memberStep1="Peer";
							 strMemberPosition1=sortWorkFlowOrderList.get(i);
						 } else {
							if(strMemberPosition1!=null && strMemberPosition1.equals(sortWorkFlowOrderList.get(i))) {
								memberStep1+=" or "+ "Peer";
								for(int a=0; peerIDList != null && a< peerIDList.size(); a++) {
									 allMemberNames.append("<tr><td>"+hmUserName.get(peerIDList.get(a))+"</td></tr>");
								 }
							} else {
								strMemberPosition1=sortWorkFlowOrderList.get(i);
								memberStep1+=" <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/> "+ "Peer";
								for(int a=0; peerIDList != null && a< peerIDList.size(); a++) {
									 allMemberNames.append("<tr><td>"+hmUserName.get(peerIDList.get(a))+"</td></tr>");
								 }
							}
						}
						 allMemberNames.append("</table>");
						 
					 } else if(!workFlowOrderNameList1.contains("Self") && hmWorkFlowOrder.get(sortWorkFlowOrderList.get(i)).equals(EMPLOYEE)) {
						 allMemberNames.append("<table>");
						 workFlowOrderNameList1.add("Self");
						 if(memberStep1==null) {
							 memberStep1 ="<label>"+ hmUserName.get(empId)+"</label> ";
								allMemberNames.append("<tr><td><table><tr><td>"+hmUserName.get(empId)+"</td></tr></table></td></tr>");
								strMemberPosition1=sortWorkFlowOrderList.get(i);
						 } else {
							if(strMemberPosition1!=null && strMemberPosition1.equals(sortWorkFlowOrderList.get(i))) {
								memberStep1+=" or "+" <label>"+ hmUserName.get(empId)+"</label> ";
								allMemberNames.append("<tr><td><table><tr><td>"+hmUserName.get(empId)+"</td></tr></table></td></tr>");
							} else {
								strMemberPosition1=sortWorkFlowOrderList.get(i);
								memberStep1+=" <img src=\"images1/arrow_black_big.png\" style=\"height: 10px;\"/> <label>"+ hmUserName.get(empId)+"</label>";
									allMemberNames.append("<tr><td><table><tr><td>"+hmUserName.get(empId)+"</td></tr></table></td></tr>");
							}
						}
						 allMemberNames.append("</table>");
						 
					 } else if(!workFlowOrderNameList.contains("HOD") && hmWorkFlowOrder.get(sortWorkFlowOrderList.get(i)).equals(HOD)) {
						 List<String> hodIDList = getListData1(hodIds);
						 allMemberNames.append("<table>");
						 workFlowOrderNameList1.add("HOD");
						 if(memberStep1==null) {
							 for(int a=0; hodIDList != null && a< hodIDList.size(); a++) {
								 memberStep1 ="<label>"+ hmUserName.get(hodIDList.get(a))+"</label> ";
								 allMemberNames.append("<tr><td>"+hmUserName.get(hodIDList.get(a))+"</td></tr>");
							 }
							strMemberPosition1=sortWorkFlowOrderList.get(i);
						 } else {
							if(strMemberPosition1!=null && strMemberPosition1.equals(sortWorkFlowOrderList.get(i))) {
								for(int a=0; hodIDList != null && a< hodIDList.size(); a++) {
									memberStep1+=" or "+" <label>"+ hmUserName.get(hodIDList.get(a))+"</label> ";
									 allMemberNames.append("<tr><td>"+hmUserName.get(hodIDList.get(a))+"</td></tr>");
								 }
							} else {
								strMemberPosition1=sortWorkFlowOrderList.get(i);
								for(int a=0; hodIDList != null && a< hodIDList.size(); a++) {
									memberStep1+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(hodIDList.get(a))+"</label> ";
									 allMemberNames.append("<tr><td>"+hmUserName.get(hodIDList.get(a))+"</td></tr>");
								 }
							}
						}
						 allMemberNames.append("</table>");
						 
					 } else if(!workFlowOrderNameList.contains("CEO") && hmWorkFlowOrder.get(sortWorkFlowOrderList.get(i)).equals(CEO)) {
						 List<String> ceoIDList = getListData1(ceoIds);
						 allMemberNames.append("<table>");
						 workFlowOrderNameList1.add("CEO");
						 if(memberStep1==null) {
							 for(int a=0; ceoIDList != null && a< ceoIDList.size(); a++) {
								 memberStep1 ="<label>"+ hmUserName.get(ceoIDList.get(a))+"</label> ";
								 allMemberNames.append("<tr><td>"+hmUserName.get(ceoIDList.get(a))+"</td></tr>");
							 }
							strMemberPosition1=sortWorkFlowOrderList.get(i);
						 } else {
							if(strMemberPosition1!=null && strMemberPosition1.equals(sortWorkFlowOrderList.get(i))) {
								for(int a=0; ceoIDList != null && a< ceoIDList.size(); a++) {
									memberStep1+=" or "+" <label>"+ hmUserName.get(ceoIDList.get(a))+"</label> ";
									 allMemberNames.append("<tr><td>"+hmUserName.get(ceoIDList.get(a))+"</td></tr>");
								 }
							} else {
								strMemberPosition1=sortWorkFlowOrderList.get(i);
								for(int a=0; ceoIDList != null && a< ceoIDList.size(); a++) {
									memberStep1+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(ceoIDList.get(a))+"</label> ";
									 allMemberNames.append("<tr><td>"+hmUserName.get(ceoIDList.get(a))+"</td></tr>");
								 }
							}
						}
						 allMemberNames.append("</table>");
						 
					 }
			 		allMemberNames.append("</td>");
//				 System.out.println("workFlowOrderNameList1 ===>" + workFlowOrderNameList1);
			 	}*/
		

			 
		 
		 /*if(orientType.equals("1")) {
			 for(int i=0; sortWorkFlowOrderList != null && i< sortWorkFlowOrderList.size(); i++) {
				 allMemberNames.append("<td>");
				 for(int j=0; unsortWorkFlowOrderList != null && j< unsortWorkFlowOrderList.size(); j++) {
				 if(sortWorkFlowOrderList.get(i).equals(unsortWorkFlowOrderList.get(j))) {
					 if(j==0 && !workFlowOrderNameList.contains("HR")) {
						 List<String> hrIDList = getListData1(hrIDS);
//						 System.out.println("hrID ===> "+hrIDS);
						 allMemberNames.append("<table>");
						 workFlowOrderNameList.add("HR");
						 if(memberStep==null) {
							 for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
								 memberStep = "<label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
							 }
//							memberStep ="<label>"+ hmUserName.get(hrID.substring(1, hrID.length()-1))+"</label> ";
							if(hrFlag == true) {
								allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
							} else {
								allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
							}
							strMemberPosition=sortWorkFlowOrderList.get(i);
						 } else {
							if(strMemberPosition!=null && strMemberPosition.equals(sortWorkFlowOrderList.get(i))) {
								for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
									memberStep+=" or "+" <label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
								 }
//								memberStep+=" or "+" <label>"+ hmUserName.get(hrID.substring(1, hrID.length()-1))+"</label> ";
								if(hrFlag == true) {
									allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
								} else {
									allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
								}
							} else if(i == sortWorkFlowOrderList.size()-1) {
								for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
									memberStep+=" or "+" <label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
								 }
//								memberStep+=" or "+" <label>"+ hmUserName.get(hrID.substring(1, hrID.length()-1))+"</label> ";
								if(hrFlag == true) {
									allMemberNames.append("<tr><td>HRMANAGER");
								} else {
									allMemberNames.append("<tr><td>HRMANAGER");
								}
							} else {
								strMemberPosition=sortWorkFlowOrderList.get(i);
								for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
									memberStep+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
								 }
//								memberStep+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(hrID.substring(1, hrID.length()-1))+"</label>";
								if(hrFlag == true) {
									allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
								} else {
									allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
								}
							}
						}
						 allMemberNames.append("</table>");
					 } else if(j==1 && !workFlowOrderNameList.contains("Self")) {
//						 System.out.println("empId ===> "+empId);
						 allMemberNames.append("<table>");
						 workFlowOrderNameList.add("Self");
						 if(memberStep==null) {
								memberStep ="<label>"+ hmUserName.get(empId)+"</label> ";
								if(empFlag == true) {
									allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
								} else {
									allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
								}
							strMemberPosition=sortWorkFlowOrderList.get(i);
						 } else {
							if(strMemberPosition!=null && strMemberPosition.equals(sortWorkFlowOrderList.get(i))) {
								memberStep+=" or "+" <label>"+ hmUserName.get(empId)+"</label> ";
								if(empFlag == true) {
									allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
								} else {
									allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
								}
							} else if(i == sortWorkFlowOrderList.size()-1) {
								memberStep+=" or "+" <label>"+ hmUserName.get(empId)+"</label> ";
								if(empFlag == true) {
									allMemberNames.append("<tr><td>SELF");
								} else {
									allMemberNames.append("<tr><td>SELF");
								}
							} else {
								strMemberPosition=sortWorkFlowOrderList.get(i);
									memberStep+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(empId)+"</label>";
									if(empFlag == true) {
										allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
									}
							}
						}
						 allMemberNames.append("</table>");
					 }
				 }
				 }
				 allMemberNames.append("</td>");
//				 System.out.println("workFlowOrderNameList ===>" + workFlowOrderNameList);
			 }
			 allMemberNames.append("</tr><tr>");
			 String memberStep1=null,strMemberPosition1=null;
			 for(int i=0; sortWorkFlowOrderList != null && i< sortWorkFlowOrderList.size(); i++) {
				 
				 allMemberNames.append("<td>");
				 for(int j=0; unsortWorkFlowOrderList != null && j< unsortWorkFlowOrderList.size(); j++) {
				 if(sortWorkFlowOrderList.get(i).equals(unsortWorkFlowOrderList.get(j))) {
					 if(j==0 && !workFlowOrderNameList1.contains("HR")) {
						 List<String> hrIDList = getListData1(hrIDS);
//						 System.out.println("hrIDList 1 ===> "+hrIDList);
						 allMemberNames.append("<table>");
						 workFlowOrderNameList1.add("HR");
						 if(memberStep1==null) {
							 for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
								 memberStep1 = "<label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
								 allMemberNames.append("<tr><td>"+hmUserName.get(hrIDList.get(a))+"</td></tr>");
							 }
//							 memberStep1 ="<label>"+ hmUserName.get(hrID.substring(1, hrID.length()-1))+"</label> ";
//							allMemberNames.append("<tr><td>"+hmUserName.get(hrID.substring(1, hrID.length()-1))+"</td></tr>");
							strMemberPosition1=sortWorkFlowOrderList.get(i);
						 } else {
							if(strMemberPosition1!=null && strMemberPosition1.equals(sortWorkFlowOrderList.get(i))) {
								for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
									memberStep1+=" or "+" <label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
									 allMemberNames.append("<tr><td>"+hmUserName.get(hrIDList.get(a))+"</td></tr>");
								 }
//								memberStep1+=" or "+" <label>"+ hmUserName.get(hrID.substring(1, hrID.length()-1))+"</label> ";
//								allMemberNames.append("<tr><td>"+hmUserName.get(hrID.substring(1, hrID.length()-1))+"</td></tr>");
							} else {
								strMemberPosition1=sortWorkFlowOrderList.get(i);
								for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
									memberStep1+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
									 allMemberNames.append("<tr><td>"+hmUserName.get(hrIDList.get(a))+"</td></tr>");
								 }
//								memberStep1+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(hrID.substring(1, hrID.length()-1))+"</label>";
//								allMemberNames.append("<tr><td>"+hmUserName.get(hrID.substring(1, hrID.length()-1))+"</td></tr>");
							}
						}
						 allMemberNames.append("</table>");
					 } else if(j==1 && !workFlowOrderNameList1.contains("Self")) {
						 allMemberNames.append("<table>");
						 workFlowOrderNameList1.add("Self");
						 if(memberStep1==null) {
							 memberStep1 ="<label>"+ hmUserName.get(empId)+"</label> ";
								allMemberNames.append("<tr><td><table><tr><td>"+hmUserName.get(empId)+"</td></tr></table></td></tr>");
								strMemberPosition1=sortWorkFlowOrderList.get(i);
						 } else {
							if(strMemberPosition1!=null && strMemberPosition1.equals(sortWorkFlowOrderList.get(i))) {
								memberStep1+=" or "+" <label>"+ hmUserName.get(empId)+"</label> ";
								allMemberNames.append("<tr><td><table><tr><td>"+hmUserName.get(empId)+"</td></tr></table></td></tr>");
							} else {
								strMemberPosition1=sortWorkFlowOrderList.get(i);
								memberStep1+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(empId)+"</label>";
									allMemberNames.append("<tr><td><table><tr><td>"+hmUserName.get(empId)+"</td></tr></table></td></tr>");
							}
						}
						 allMemberNames.append("</table>");
					 }
				 }
				 }
				 allMemberNames.append("</td>");
//				 System.out.println("workFlowOrderNameList1 ===>" + workFlowOrderNameList1);
			 }
			} else if(orientType.equals("2")) {
//				System.out.println("sortWorkFlowOrderList ===>> "  + sortWorkFlowOrderList);
//				System.out.println("unsortWorkFlowOrderList ===>> "  + unsortWorkFlowOrderList);
//				System.out.println("workFlowOrderNameList ===>> "  + workFlowOrderNameList);
				
				for(int i=0; sortWorkFlowOrderList != null && i< sortWorkFlowOrderList.size(); i++) {
					 allMemberNames.append("<td>");
					 for(int j=0; unsortWorkFlowOrderList != null && j< unsortWorkFlowOrderList.size(); j++) {
					 if(sortWorkFlowOrderList.get(i).equals(unsortWorkFlowOrderList.get(j))) {
						 System.out.println("unsortWorkFlowOrderList.get(j) ===>> "  + unsortWorkFlowOrderList.get(j));
						 if(j==0 && !workFlowOrderNameList.contains("HR")) {
							 List<String> hrIDList = getListData1(hrIDS);
//							 System.out.println("hrIDList ===>> "  + hrIDList);
							 allMemberNames.append("<table>");
							 workFlowOrderNameList.add("HR");
							 if(memberStep==null) {
								 for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
									 memberStep = "<label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
								 }
//								memberStep ="<label>"+ hmUserName.get(hrID.substring(1, hrID.length()-1))+"</label> ";
								if(hrFlag == true) {
									allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
								} else {
									allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
								}
								strMemberPosition=sortWorkFlowOrderList.get(i);
							} else {
								if(strMemberPosition!=null && strMemberPosition.equals(sortWorkFlowOrderList.get(i))) {
									for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
										memberStep+=" or "+" <label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
									 }
//									memberStep+=" or "+" <label>"+ hmUserName.get(hrID.substring(1, hrID.length()-1))+"</label> ";
									if(hrFlag == true) {
										allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
									}
								} else if(i == sortWorkFlowOrderList.size()-1) {
									for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
										memberStep+=" or "+" <label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
									 }
//									memberStep+=" or "+" <label>"+ hmUserName.get(hrID.substring(1, hrID.length()-1))+"</label> ";
									if(hrFlag == true) {
										allMemberNames.append("<tr><td>HRMANAGER");
									} else {
										allMemberNames.append("<tr><td>HRMANAGER");
									}
								} else {
									strMemberPosition=sortWorkFlowOrderList.get(i);
									for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
										memberStep+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
									 }
//									memberStep+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(hrID.substring(1, hrID.length()-1))+"</label>";
									if(hrFlag == true) {
										allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
									}
								}
							}
							 allMemberNames.append("</table>");
						 } else if(j==1 && !workFlowOrderNameList.contains("Manager")) {
							 List<String> managerIDList = getListData1(managerIDS);
//							 System.out.println("managerIDList 1 ===> "+managerIDList);
							 allMemberNames.append("<table>");
							 workFlowOrderNameList.add("Manager");
							 if(memberStep==null) {
								 for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
									 memberStep ="<label>"+ hmUserName.get(managerIDList.get(a))+"</label> ";
								 }
//								memberStep ="<label>"+ hmUserName.get(managerID)+"</label> ";
								if(managerFlag == true) {
									allMemberNames.append("<tr><td>MANAGER <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
								} else {
									allMemberNames.append("<tr><td>MANAGER <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
								}
								strMemberPosition=sortWorkFlowOrderList.get(i);
							} else {
								if(strMemberPosition!=null && strMemberPosition.equals(sortWorkFlowOrderList.get(i))) {
									for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
										memberStep+=" or "+" <label>"+ hmUserName.get(managerIDList.get(a))+"</label> ";
									 }
//									memberStep+=" or "+" <label>"+ hmUserName.get(managerID)+"</label> ";
									if(managerFlag == true) {
										allMemberNames.append("<tr><td>MANAGER <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>MANAGER <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
									}
								} else if(i == sortWorkFlowOrderList.size()-1) {
									for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
										memberStep+=" or "+" <label>"+ hmUserName.get(managerIDList.get(a))+"</label> ";
									 }
//									memberStep+=" or "+" <label>"+ hmUserName.get(managerID)+"</label> ";
									if(managerFlag == true) {
										allMemberNames.append("<tr><td>MANAGER");
									} else {
										allMemberNames.append("<tr><td>MANAGER");
									}
								} else {
									strMemberPosition=sortWorkFlowOrderList.get(i);
									for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
										memberStep+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(managerIDList.get(a))+"</label> ";
									 }
//									memberStep+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(managerID)+"</label>";
									if(managerFlag == true) {
										allMemberNames.append("<tr><td>MANAGER <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>MANAGER <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
									}
								}
							}
							 allMemberNames.append("</table>");
						 } else if(j==2 && !workFlowOrderNameList.contains("Self")) {

							 allMemberNames.append("<table>");
							 workFlowOrderNameList.add("Self");
							 if(memberStep==null) {
									memberStep ="<label>"+ hmUserName.get(empId)+"</label> ";
									if(empFlag == true) {
										allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
									}
								strMemberPosition=sortWorkFlowOrderList.get(i);
							 } else {
								if(strMemberPosition!=null && strMemberPosition.equals(sortWorkFlowOrderList.get(i))) {
									memberStep+=" or "+" <label>"+ hmUserName.get(empId)+"</label> ";
									if(empFlag == true) {
										allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
									}
								} else if(i == sortWorkFlowOrderList.size()-1) {
									memberStep+=" or "+" <label>"+ hmUserName.get(empId)+"</label> ";
									if(empFlag == true) {
										allMemberNames.append("<tr><td>SELF");
									} else {
										allMemberNames.append("<tr><td>SELF");
									}
								} else {
									strMemberPosition=sortWorkFlowOrderList.get(i);
										memberStep+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(empId)+"</label>";
										if(empFlag == true) {
											allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
										} else {
											allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
										}
								}
							}
							 allMemberNames.append("</table>");
						 }
					 }
					 }
					 allMemberNames.append("</td>");
//					 System.out.println("workFlowOrderNameList ===>" + workFlowOrderNameList);
				 }
				 allMemberNames.append("</tr><tr>");
				 String memberStep1=null,strMemberPosition1=null;
				 for(int i=0; sortWorkFlowOrderList != null && i< sortWorkFlowOrderList.size(); i++) {
					 
					 allMemberNames.append("<td>");
					 for(int j=0; unsortWorkFlowOrderList != null && j< unsortWorkFlowOrderList.size(); j++) {
					 if(sortWorkFlowOrderList.get(i).equals(unsortWorkFlowOrderList.get(j))) {
						 if(j==0 && !workFlowOrderNameList1.contains("HR")) {
							 List<String> hrIDList = getListData1(hrIDS);
//							 System.out.println("hrIDList 2 ===> "+hrIDList);
							 allMemberNames.append("<table>");
							 workFlowOrderNameList1.add("HR");
							 if(memberStep1==null) {
								 for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
									 memberStep1 = "<label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
									 allMemberNames.append("<tr><td>"+hmUserName.get(hrIDList.get(a))+"</td></tr>");
								 }
//								 memberStep1 ="<label>"+ hmUserName.get(hrID.substring(1, hrID.length()-1))+"</label> ";
//								allMemberNames.append("<tr><td>"+hmUserName.get(hrID.substring(1, hrID.length()-1))+"</td></tr>");
								strMemberPosition1=sortWorkFlowOrderList.get(i);
							 } else {
								if(strMemberPosition1!=null && strMemberPosition1.equals(sortWorkFlowOrderList.get(i))) {
									for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
										memberStep1+=" or "+" <label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
										 allMemberNames.append("<tr><td>"+hmUserName.get(hrIDList.get(a))+"</td></tr>");
									 }
//									memberStep1+=" or "+" <label>"+ hmUserName.get(hrID.substring(1, hrID.length()-1))+"</label> ";
//									allMemberNames.append("<tr><td>"+hmUserName.get(hrID.substring(1, hrID.length()-1))+"</td></tr>");
								} else {
									strMemberPosition1=sortWorkFlowOrderList.get(i);
									for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
										memberStep1+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
										 allMemberNames.append("<tr><td>"+hmUserName.get(hrIDList.get(a))+"</td></tr>");
									 }
//									memberStep1+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(hrID.substring(1, hrID.length()-1))+"</label>";
//									allMemberNames.append("<tr><td>"+hmUserName.get(hrID.substring(1, hrID.length()-1))+"</td></tr>");
								}
							}
							 allMemberNames.append("</table>");
						 } else if(j==1 && !workFlowOrderNameList1.contains("Manager")) {
							 List<String> managerIDList = getListData1(managerIDS);
//							 System.out.println("managerIDList 2 ===> "+managerIDList);
							 allMemberNames.append("<table>");
							 workFlowOrderNameList1.add("Manager");
							 if(memberStep1==null) {
								 for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
									 memberStep1 ="<label>"+ hmUserName.get(managerIDList.get(a))+"</label> ";
									 allMemberNames.append("<tr><td>"+hmUserName.get(managerIDList.get(a))+"</td></tr>");
								 }
//								 memberStep1 ="<label>"+ hmUserName.get(managerID)+"</label> ";
//								allMemberNames.append("<tr><td>"+hmUserName.get(managerID)+"</td></tr>");
								strMemberPosition1=sortWorkFlowOrderList.get(i);
							 } else {
								if(strMemberPosition1!=null && strMemberPosition1.equals(sortWorkFlowOrderList.get(i))) {
									for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
										memberStep1+=" or "+" <label>"+ hmUserName.get(managerIDList.get(a))+"</label> ";
										 allMemberNames.append("<tr><td>"+hmUserName.get(managerIDList.get(a))+"</td></tr>");
									 }
//									memberStep1+=" or "+" <label>"+ hmUserName.get(managerID)+"</label> ";
//									allMemberNames.append("<tr><td>"+hmUserName.get(managerID)+"</td></tr>");
								} else {
									strMemberPosition1=sortWorkFlowOrderList.get(i);
									for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
										memberStep1+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(managerIDList.get(a))+"</label> ";
										 allMemberNames.append("<tr><td>"+hmUserName.get(managerIDList.get(a))+"</td></tr>");
									 }
//									memberStep1+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(managerID)+"</label>";
//									allMemberNames.append("<tr><td>"+hmUserName.get(managerID)+"</td></tr>");
								}
							}
							 allMemberNames.append("</table>");
						 } else if(j==2 && !workFlowOrderNameList1.contains("Self")) {
							 allMemberNames.append("<table>");
							 workFlowOrderNameList1.add("Self");
							 if(memberStep1==null) {
								 memberStep1 ="<label>"+ hmUserName.get(empId)+"</label> ";
									allMemberNames.append("<tr><td><table><tr><td>"+hmUserName.get(empId)+"</td></tr></table></td></tr>");
									strMemberPosition1=sortWorkFlowOrderList.get(i);
							 } else {
								if(strMemberPosition1!=null && strMemberPosition1.equals(sortWorkFlowOrderList.get(i))) {
									memberStep1+=" or "+" <label>"+ hmUserName.get(empId)+"</label> ";
									allMemberNames.append("<tr><td><table><tr><td>"+hmUserName.get(empId)+"</td></tr></table></td></tr>");
								} else {
									strMemberPosition1=sortWorkFlowOrderList.get(i);
									memberStep1+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(empId)+"</label>";
										allMemberNames.append("<tr><td><table><tr><td>"+hmUserName.get(empId)+"</td></tr></table></td></tr>");
								}
							}
							 allMemberNames.append("</table>");
						 }
					 }
					 }
					 allMemberNames.append("</td>");
//					 System.out.println("workFlowOrderNameList1 ===>" + workFlowOrderNameList1);
				 }
			} else if(orientType.equals("3")) {
				for(int i=0; sortWorkFlowOrderList != null && i< sortWorkFlowOrderList.size(); i++) {
					 allMemberNames.append("<td>");
					 for(int j=0; unsortWorkFlowOrderList != null && j< unsortWorkFlowOrderList.size(); j++) {
					 if(sortWorkFlowOrderList.get(i).equals(unsortWorkFlowOrderList.get(j))) {
						 if(j==0 && !workFlowOrderNameList.contains("HR")) {
							 List<String> hrIDList = getListData1(hrIDS);
//							 System.out.println("hrIDList 3 ===> "+hrIDList);
							 allMemberNames.append("<table>");
							 workFlowOrderNameList.add("HR");
							 if(memberStep==null) {
								 for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
									 memberStep = "<label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
								 }
								if(hrFlag == true) {
									allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
								} else {
									allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
								}
								strMemberPosition=sortWorkFlowOrderList.get(i);
							 } else {
								if(strMemberPosition!=null && strMemberPosition.equals(sortWorkFlowOrderList.get(i))) {
									for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
										memberStep+=" or "+" <label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
									 }
									if(hrFlag == true) {
										allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
									}
								} else if(i == sortWorkFlowOrderList.size()-1) {
									for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
										memberStep+=" or "+" <label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
									 }
									if(hrFlag == true) {
										allMemberNames.append("<tr><td>HRMANAGER");
									} else {
										allMemberNames.append("<tr><td>HRMANAGER");
									}
								} else {
									strMemberPosition=sortWorkFlowOrderList.get(i);
									for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
										memberStep+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
									 }
									if(hrFlag == true) {
										allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>HRMANAGER <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
									}
								}
							}
							 allMemberNames.append("</table>");
						 } else if(j==1 && !workFlowOrderNameList.contains("Manager")) {
							 List<String> managerIDList = getListData1(managerIDS);
//							 System.out.println("managerIDList 3 ===> "+managerIDList);
							 allMemberNames.append("<table>");
							 workFlowOrderNameList.add("Manager");
							 if(memberStep==null) {
								 for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
									 memberStep ="<label>"+ hmUserName.get(managerIDList.get(a))+"</label> ";
								 }
								if(managerFlag == true) {
									allMemberNames.append("<tr><td>MANAGER <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
								} else {
									allMemberNames.append("<tr><td>MANAGER <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
								}
								strMemberPosition=sortWorkFlowOrderList.get(i);
							 } else {
								if(strMemberPosition!=null && strMemberPosition.equals(sortWorkFlowOrderList.get(i))) {
									for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
										memberStep+=" or "+" <label>"+ hmUserName.get(managerIDList.get(a))+"</label> ";
									 }
									if(managerFlag == true) {
										allMemberNames.append("<tr><td>MANAGER <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>MANAGER <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
									}
								} else if(i == sortWorkFlowOrderList.size()-1) {
									for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
										memberStep+=" or "+" <label>"+ hmUserName.get(managerIDList.get(a))+"</label> ";
									 }
									if(managerFlag == true) {
										allMemberNames.append("<tr><td>MANAGER");
									} else {
										allMemberNames.append("<tr><td>MANAGER");
									}
								} else {
									strMemberPosition=sortWorkFlowOrderList.get(i);
									for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
										memberStep+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(managerIDList.get(a))+"</label> ";
									 }
									if(managerFlag == true) {
										allMemberNames.append("<tr><td>MANAGER <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>MANAGER <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
									}
								}
							}
							 allMemberNames.append("</table>");
						 } else if(j==2 && !workFlowOrderNameList.contains("Peer")) {
							 
							 allMemberNames.append("<table>");
							 workFlowOrderNameList.add("Peer");
							 if(memberStep==null) {
								 if(peerFlag == true) {
									 allMemberNames.append("<tr><td>PEER <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
								 } else {
									allMemberNames.append("<tr><td>PEER <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
								}
								memberStep="Peer";
								strMemberPosition=sortWorkFlowOrderList.get(i);
							 } else {
								if(strMemberPosition!=null && strMemberPosition.equals(sortWorkFlowOrderList.get(i))) {
									memberStep+=" or "+ "Peer";
									if(peerFlag == true) {
										allMemberNames.append("<tr><td>PEER <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>PEER <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
									}
								} else if(i == sortWorkFlowOrderList.size()-1) {
									memberStep+=" or "+" <label>"+ hmUserName.get(empId)+"</label> ";
									if(peerFlag == true) {
										allMemberNames.append("<tr><td>PEER");
									} else {
										allMemberNames.append("<tr><td>PEER");
									}
								} else {
									strMemberPosition=sortWorkFlowOrderList.get(i);
									memberStep+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> "+ "Peer";
									if(peerFlag == true) {
										allMemberNames.append("<tr><td>PEER <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>PEER <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
									}
								}
							}
							 allMemberNames.append("</table>");
						 } else if(j==3 && !workFlowOrderNameList.contains("Self")) {

							 allMemberNames.append("<table>");
							 workFlowOrderNameList.add("Self");
							 if(memberStep==null) {
									memberStep ="<label>"+ hmUserName.get(empId)+"</label> ";
									if(empFlag == true) {
										allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
									}
								strMemberPosition=sortWorkFlowOrderList.get(i);
							 } else {
								if(strMemberPosition!=null && strMemberPosition.equals(sortWorkFlowOrderList.get(i))) {
									memberStep+=" or "+" <label>"+ hmUserName.get(empId)+"</label> ";
									if(empFlag == true) {
										allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
									} else {
										allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
									}
								} else if(i == sortWorkFlowOrderList.size()-1) {
									memberStep+=" or "+" <label>"+ hmUserName.get(empId)+"</label> ";
									if(empFlag == true) {
										allMemberNames.append("<tr><td>SELF");
									} else {
										allMemberNames.append("<tr><td>SELF");
									}
								} else {
									strMemberPosition=sortWorkFlowOrderList.get(i);
										memberStep+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(empId)+"</label>";
										if(empFlag == true) {
											allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_green_big.png\"  style=\"height: 10px;\"/></td></tr>");
										} else {
											allMemberNames.append("<tr><td>SELF <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/></td></tr>");
										}
								}
							}
							 allMemberNames.append("</table>");
						 }
					 }
					 }
					 allMemberNames.append("</td>");
//					 System.out.println("workFlowOrderNameList ===>" + workFlowOrderNameList);
				 }
				 allMemberNames.append("</tr><tr>");
				 String memberStep1=null,strMemberPosition1=null;
				 for(int i=0; sortWorkFlowOrderList != null && i< sortWorkFlowOrderList.size(); i++) {
					 
					 allMemberNames.append("<td>");
					 for(int j=0; unsortWorkFlowOrderList != null && j< unsortWorkFlowOrderList.size(); j++) {
					 if(sortWorkFlowOrderList.get(i).equals(unsortWorkFlowOrderList.get(j))) {
						 if(j==0 && !workFlowOrderNameList1.contains("HR")) {
							 List<String> hrIDList = getListData1(hrIDS);
//							 System.out.println("hrIDList 33 ===> "+hrIDList);
							 allMemberNames.append("<table>");
							 workFlowOrderNameList1.add("HR");
							 if(memberStep1==null) {
								 for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
									 memberStep1 = "<label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
									 allMemberNames.append("<tr><td>"+hmUserName.get(hrIDList.get(a))+"</td></tr>");
								 }
								strMemberPosition1=sortWorkFlowOrderList.get(i);
							 } else {
								if(strMemberPosition1!=null && strMemberPosition1.equals(sortWorkFlowOrderList.get(i))) {
									for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
										memberStep1+=" or "+" <label>"+ hmUserName.get(hrIDList.get(a))+"</label> ";
										 allMemberNames.append("<tr><td>"+hmUserName.get(hrIDList.get(a))+"</td></tr>");
									 }
								} else {
									strMemberPosition1=sortWorkFlowOrderList.get(i);
									for(int a=0; hrIDList != null && a< hrIDList.size(); a++) {
										memberStep1+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(hrIDList.get(a))+"</label>";
										allMemberNames.append("<tr><td>"+hmUserName.get(hrIDList.get(a))+"</td></tr>");
									 }
								}
							}
							 allMemberNames.append("</table>");
						 } else if(j==1 && !workFlowOrderNameList1.contains("Manager")) {
							 List<String> managerIDList = getListData1(managerIDS);
//							 System.out.println("managerIDList 33 ===> "+managerIDList);
							 allMemberNames.append("<table>");
							 workFlowOrderNameList1.add("Manager");
							 if(memberStep1==null) {
								 for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
									 memberStep1 ="<label>"+ hmUserName.get(managerIDList.get(a))+"</label> ";
									 allMemberNames.append("<tr><td>"+hmUserName.get(managerIDList.get(a))+"</td></tr>");
								 }
								strMemberPosition1=sortWorkFlowOrderList.get(i);
							 } else {
								if(strMemberPosition1!=null && strMemberPosition1.equals(sortWorkFlowOrderList.get(i))) {
									for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
										memberStep1+=" or "+" <label>"+ hmUserName.get(managerIDList.get(a))+"</label> ";
										 allMemberNames.append("<tr><td>"+hmUserName.get(managerIDList.get(a))+"</td></tr>");
									 }
								} else {
									strMemberPosition1=sortWorkFlowOrderList.get(i);
									for(int a=0; managerIDList != null && a< managerIDList.size(); a++) {
										memberStep1+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(managerIDList.get(a))+"</label>";
										 allMemberNames.append("<tr><td>"+hmUserName.get(managerIDList.get(a))+"</td></tr>");
									 }
								}
							}
							 allMemberNames.append("</table>");
						 } else if(j==2 && !workFlowOrderNameList1.contains("Peer")) {
							 
							 List<String> peerIDList = getListData1(peerIDS);
//							 System.out.println("peerIDList 33 ===> "+peerIDList);
							 allMemberNames.append("<table>");
							 workFlowOrderNameList1.add("Peer");
							 if(memberStep1==null) {
								 for(int a=0; peerIDList != null && a< peerIDList.size(); a++) {
									 allMemberNames.append("<tr><td>"+hmUserName.get(peerIDList.get(a))+"</td></tr>");
								 }
								 memberStep1="Peer";
								 strMemberPosition1=sortWorkFlowOrderList.get(i);
							 } else {
								if(strMemberPosition1!=null && strMemberPosition1.equals(sortWorkFlowOrderList.get(i))) {
									memberStep1+=" or "+ "Peer";
									for(int a=0; peerIDList != null && a< peerIDList.size(); a++) {
										 allMemberNames.append("<tr><td>"+hmUserName.get(peerIDList.get(a))+"</td></tr>");
									 }
								} else {
									strMemberPosition1=sortWorkFlowOrderList.get(i);
									memberStep1+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> "+ "Peer";
									for(int a=0; peerIDList != null && a< peerIDList.size(); a++) {
										 allMemberNames.append("<tr><td>"+hmUserName.get(peerIDList.get(a))+"</td></tr>");
									 }
								}
							}
							 allMemberNames.append("</table>");
						 } else if(j==3 && !workFlowOrderNameList1.contains("Self")) {
							 allMemberNames.append("<table>");
							 workFlowOrderNameList1.add("Self");
							 if(memberStep1==null) {
								 memberStep1 ="<label>"+ hmUserName.get(empId)+"</label> ";
									allMemberNames.append("<tr><td><table><tr><td>"+hmUserName.get(empId)+"</td></tr></table></td></tr>");
									strMemberPosition1=sortWorkFlowOrderList.get(i);
							 } else {
								if(strMemberPosition1!=null && strMemberPosition1.equals(sortWorkFlowOrderList.get(i))) {
									memberStep1+=" or "+" <label>"+ hmUserName.get(empId)+"</label> ";
									allMemberNames.append("<tr><td><table><tr><td>"+hmUserName.get(empId)+"</td></tr></table></td></tr>");
								} else {
									strMemberPosition1=sortWorkFlowOrderList.get(i);
									memberStep1+=" <img src=\"images1/arrow_black_big.png\"  style=\"height: 10px;\"/> <label>"+ hmUserName.get(empId)+"</label>";
										allMemberNames.append("<tr><td><table><tr><td>"+hmUserName.get(empId)+"</td></tr></table></td></tr>");
								}
							}
							 allMemberNames.append("</table>");
						 }
					 }
					 }
					 allMemberNames.append("</td>");
//					 System.out.println("workFlowOrderNameList1 ===>" + workFlowOrderNameList1);
				 }
			} else if(orientType.equals("4")) {
				
			}*/
		 
		 allMemberNames.append("</tr></table>");
//		 System.out.println("allMemberNames ===>" + allMemberNames);
//		 request.setAttribute("memberStep", memberStep);
		 request.setAttribute("allMemberNames", allMemberNames);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	/*private void getOrientTypeWiseRemainIds(UtilityFunctions uF) {
		List<String> listMemberType = getListData(getMemberIds());
		Map<String, List<String>> hmOrientTypeAndUsers = new LinkedHashMap<String, List<String>>();
		Map<String, String> hmUserName = getUserName(uF);
//		System.out.println("listMemberType :: "+listMemberType);
		for(int i = 0; listMemberType != null && i < listMemberType.size(); i++) {
//			System.out.println("listMemberType.get(i) :: "+listMemberType.get(i));
			Map<String,List<String>> hmExistUsersAQA = getExistUsersInAQA(uF);
			List<String> existUsersAQAList = hmExistUsersAQA.get(listMemberType.get(i));
			Map<String,List<String>> hmOrientTypewiseID = getOrientTypeWiseIds();
			List<String> listOrientTypewiseID = hmOrientTypewiseID.get(getId()+"_"+listMemberType.get(i));
//			System.out.println("listOrientTypewiseID :: "+listOrientTypewiseID);
//			System.out.println("existUsersAQAList :: "+existUsersAQAList);
			List<String> listMemberName = new ArrayList<String>();
			List<String> listMemberIds = new ArrayList<String>();
			for(int j=0; listOrientTypewiseID != null && j< listOrientTypewiseID.size();j++) {
				if(existUsersAQAList != null) {
					if(!listOrientTypewiseID.get(j).trim().equals("") && !existUsersAQAList.contains(listOrientTypewiseID.get(j).trim())) {
						if(!listOrientTypewiseID.get(j).trim().equals("") && !listMemberIds.contains(listOrientTypewiseID.get(j).trim())) {
							listMemberIds.add(listOrientTypewiseID.get(j));
							listMemberName.add(hmUserName.get(listOrientTypewiseID.get(j).trim()));							
						}
					} else {
//						listRemainOrientType.add(listOrientTypewiseID.get(j));
					}
				} else {
					if(!listOrientTypewiseID.get(j).trim().equals("") && !listMemberIds.contains(listOrientTypewiseID.get(j).trim())) {
						listMemberIds.add(listOrientTypewiseID.get(j));
						listMemberName.add(hmUserName.get(listOrientTypewiseID.get(j).trim()));							
					}
				}
			}
			hmOrientTypeAndUsers.put(listMemberType.get(i), listMemberName);
		}
//		System.out.println("hmOrientTypeAndUsers ::: "+hmOrientTypeAndUsers);
		request.setAttribute("hmOrientTypeAndUsers", hmOrientTypeAndUsers);
	}*/
	
	private Map<String, String> getUserName(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, String> hmUserName = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst = con.prepareStatement("select emp_per_id,emp_fname,emp_mname,emp_lname from employee_personal_details");
//			pst.setInt(1, uF.parseToInt(userId));
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmUserName.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")); 
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
		return hmUserName;
	}
	
	
	private Map<String,List<String>> getOrientTypeWiseIds() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		Map<String,List<String>> hmOrientTypewiseID = new HashMap<String, List<String>>();
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmManagerId = new HashMap<String, String>();
			pst = con.prepareStatement("select emp_id,supervisor_emp_id from employee_official_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmManagerId.put(rs.getString("emp_id"), rs.getString("supervisor_emp_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select appraisal_details_id,supervisor_id,peer_ids,self_ids,hr_ids,ceo_ids,hod_ids from appraisal_details");
			rs = pst.executeQuery();
			while (rs.next()) {
//				System.out.println("getEmpId() ========== > "+getEmpId());
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_7", getListData1(rs.getString("hr_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_4", getListData1(rs.getString("peer_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_3", getListData1(","+getEmpId()+","));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_2", getListData1(","+hmManagerId.get(getEmpId())+","));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_5", getListData(rs.getString("ceo_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_13", getListData(rs.getString("hod_ids")));
			}
			rs.close();
			pst.close();
//			System.out.println("hmOrientTypewiseID ========== > "+hmOrientTypewiseID);
			request.setAttribute("hmOrientTypewiseID", hmOrientTypewiseID);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmOrientTypewiseID;
	}
	
	
	private List<String> getListData(String memberTypeIds) {
		List<String> listMemberTypeIds = new ArrayList<String>();
		if(memberTypeIds != null && !memberTypeIds.equals("") && memberTypeIds.length()>0) {
			memberTypeIds = memberTypeIds.substring(0, memberTypeIds.length()-1);
			listMemberTypeIds = Arrays.asList(memberTypeIds.split(","));
		}
			
//			if (memberTypeIds.contains(",")) {
//				String[] temp = memberTypeIds.split(",");
//				for (int i = 0; i < temp.length; i++) {
//					listMemberTypeIds.add(temp[i]);
//				}
//			} else {
//				listMemberTypeIds.add(memberTypeIds);
//			}
//		}
		return listMemberTypeIds;
	}
	
	private List<String> getListData1(String Ids) {
		List<String> listIds = new ArrayList<String>();
		if(Ids != null && !Ids.equals("") && Ids.length()>1) {
			Ids = Ids.substring(1, Ids.trim().length()-1);
		listIds = Arrays.asList(Ids.split(","));
		}
//		System.out.println("Ids ::: "+Ids);
//		if(Ids != null && !Ids.equals("") && Ids.length()>1) {
//			Ids = Ids.substring(1, Ids.length());
//			System.out.println("Ids 1 ::: "+Ids);
//			if (Ids.contains(",")) {
//				System.out.println("Ids 2 ::: "+Ids);
//				String[] temp = Ids.split(",");
//				for (int i = 0; i < temp.length; i++) {
//					listIds.add(temp[i]);
//				}
//			} else {
//				System.out.println("Ids else ::: "+Ids);
//				listIds.add(Ids);
//			}
//		}
//		System.out.println("listIds ::: "+listIds);
//		System.out.println("listIds.size() ::: "+ listIds != null ? listIds.size() : "-");
		return listIds;
	}
	
	/*private Map<String,List<String>> getExistUsersInAQA(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		Map<String,List<String>> hmExistUsersAQA = new HashMap<String, List<String>>();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select distinct(user_type_id),appraisal_id,section_id,user_id from appraisal_question_answer " +
					" where is_submit=true and appraisal_id=? and section_id=? and emp_id=? and appraisal_freq_id=?");
			pst.setInt(1, uF.parseToInt(id));
			pst.setInt(2, uF.parseToInt(sectionId));
			pst.setInt(3, uF.parseToInt(empId));
			pst.setInt(4, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> existUsersAQAList = hmExistUsersAQA.get(rs.getString("user_type_id"));
				if(existUsersAQAList==null) existUsersAQAList = new ArrayList<String>();				
				existUsersAQAList.add(rs.getString("user_id"));
				hmExistUsersAQA.put(rs.getString("user_type_id"), existUsersAQAList);
			}
			rs.close();
			pst.close();
//			System.out.println("hmExistUsersAQA ========== > "+hmExistUsersAQA);
			request.setAttribute("hmExistUsersAQA", hmExistUsersAQA);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmExistUsersAQA;
	}*/
	
	
	/*private void getOrientationMember() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			Map<String, String> orientMemberMp = new HashMap<String, String>();
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				//orientationMemberMp.put(rs.getString("orientation_member_id"), rs.getString("member_name"));
				orientMemberMp.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("orientMemberMp", orientMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}*/
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getMemberIds() {
		return memberIds;
	}

	public void setMemberIds(String memberIds) {
		this.memberIds = memberIds;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

	
}
