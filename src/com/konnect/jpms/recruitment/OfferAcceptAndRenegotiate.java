package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CandidateNotifications;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class OfferAcceptAndRenegotiate extends ActionSupport implements ServletRequestAware,IStatements{

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId = null;
	String strUserType = null;
	CommonFunctions CF = null;
	
	
	String MSG;
	
	String rejectStatus;
	String rejectType;
	
	public String execute() throws Exception {
     
		session = request.getSession(); 
		CF= (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(USERTYPE);
//		request.setAttribute(PAGE, "/jsp/recruitment/OfferAcceptAndRenegotiate.jsp");
//		request.setAttribute(TITLE,"Offer Accept");
		
		request.setAttribute(PAGE, "/jsp/common/showMessagePage.jsp");
		request.setAttribute(TITLE,"Offer Accept");
		UtilityFunctions uF=new UtilityFunctions();
//		System.out.println("getRecruitId ===>> " + getRecruitId());
		if(getUpdateRemark() != null && getUpdateRemark().equals("Update")) {
			if(getCandiOfferAccept() != null && getCandiOfferAccept().equals("yes")) {
				offerAccept(getCandidateID(), getRecruitId(), uF);
				return "message";
			} else {
				if(getRejectType() != null && getRejectType().equalsIgnoreCase("CANDIBACKOUT")) {
					offerBackoutAfterAccept(getCandidateID(), getRecruitId(), uF);
				} else if(getRejectType() != null && getRejectType().equalsIgnoreCase("CANDIONHOLD")) {
					offerOnHold(getCandidateID(), getRecruitId(), uF);
				} else {
					offerAccept(getCandidateID(), getRecruitId(), uF);
				}
//			allApplication(getRecruitID());
			return SUCCESS;
			}
		} else {
			if(getRejectType() != null && getRejectType().equalsIgnoreCase("CANDIONHOLD_REASON")) {
				getOfferOnHoldDetils(getCandidateID(), getRecruitId(), uF);
			}
			return LOAD;
		}
	}
	
	
	private void getOfferOnHoldDetils(String candidateID, String recruitID, UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;	
		ResultSet rs = null;
		try {
			
			con = db.makeConnection(con);
//			System.out.println("getRejectType ===>> " + getRejectType());
			pst = con.prepareStatement("select * from candidate_application_details where candidate_id = ? and recruitment_id = ? and offer_backout_status = -2");
			pst.setInt(1, uF.parseToInt(candidateID));
			pst.setInt(2, uF.parseToInt(recruitID));
			rs = pst.executeQuery();
			String offerHoldDate = null;
			while (rs.next()) {
				setOfferOnHoldRemark(rs.getString("offer_backout_remark"));
				offerHoldDate = uF.getDateFormat(rs.getString("offer_backout_date"), DBDATE, DATE_FORMAT_STR); 
			}
			rs.close();
			pst.close();
			
			request.setAttribute("offerHoldDate", offerHoldDate);
			
	     } catch(Exception e) {
	    	 e.printStackTrace();
	     } finally {
	    	 db.closeResultSet(rs);
			 db.closeStatements(pst);	
	    	 db.closeConnection(con);
	     }
	}


	private void offerBackoutAfterAccept(String candidateID, String recruitID, UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;	
		ResultSet rs = null;
		try {
			con=db.makeConnection(con);
			String candiStatus = "0";
			pst=con.prepareStatement("select candidate_status,candidate_joining_date from candidate_application_details where candidate_id=? and recruitment_id = ? ");
			pst.setInt(1, uF.parseToInt(candidateID));
			pst.setInt(2, uF.parseToInt(recruitID));
			rs=pst.executeQuery(); 
//			System.out.println("new Date ===> " + new Date());
			while(rs.next()){
				candiStatus = rs.getString("candidate_status");
			}
			rs.close();
			pst.close();
			
			if(candiStatus.equals("1")) {
//				System.out.println("getRejectStatus ===>> " + getRejectStatus());
				if(getRejectStatus()!=null && getRejectStatus().equalsIgnoreCase("reject")) {
					pst = con.prepareStatement("update candidate_application_details set offer_backout_status = -1, offer_backout_remark = ?, offer_backout_date=? where candidate_id = ? and recruitment_id = ? ");
					pst.setString(1, uF.showData(getOfferAcceptRemark(),""));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(3, uF.parseToInt(candidateID));
					pst.setInt(4, uF.parseToInt(recruitID));
					pst.executeUpdate();
					pst.close();
					
					pst=con.prepareStatement("delete from candidate_activity_details where recruitment_id=? and candi_id=? and user_id=? and " +
							"activity_id = ?");
					pst.setInt(1, uF.parseToInt(recruitID));
					pst.setInt(2, uF.parseToInt(getCandidateID()));
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
					pst.setInt(4, CANDI_ACTIVITY_OFFER_BACKOUT_ID);
					pst.executeUpdate();
					pst.close();
					
					pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
					pst.setInt(1,uF.parseToInt(recruitID));
					pst.setInt(2,uF.parseToInt(getCandidateID()));
					pst.setString(3, "Offer Backout");
					pst.setInt(4,uF.parseToInt(strSessionEmpId));
					pst.setDate(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
					pst.setInt(6, CANDI_ACTIVITY_OFFER_BACKOUT_ID);
					pst.execute();
					pst.close();
					
//					setOfferAcceptRejectAlert(con, recruitID);
//					setMSG("You have rejected your offer.");
				}
			}
	     } catch(Exception e) {
	    	 e.printStackTrace();
	     } finally {
	    	 db.closeResultSet(rs);
			 db.closeStatements(pst);	
	    	 db.closeConnection(con);
	     }
	}
	
	
	private void offerOnHold(String candidateID, String recruitID, UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;	
		ResultSet rs = null;
		try {
			con=db.makeConnection(con);
			String candiStatus = "0";
			pst=con.prepareStatement("select candidate_status,candidate_joining_date from candidate_application_details where candidate_id=? and recruitment_id = ? ");
			pst.setInt(1, uF.parseToInt(candidateID));
			pst.setInt(2, uF.parseToInt(recruitID));
			rs=pst.executeQuery(); 
//			System.out.println("new Date ===> " + new Date());
			while(rs.next()){
				candiStatus = rs.getString("candidate_status");
			}
			rs.close();
			pst.close();
			
			if(candiStatus.equals("0")) {
//				System.out.println("getRejectStatus ===>> " + getRejectStatus());
				if(getRejectStatus()!=null && getRejectStatus().equalsIgnoreCase("hold")) {
					pst = con.prepareStatement("update candidate_application_details set offer_backout_status = -2, offer_backout_remark = ?, offer_backout_date=? where candidate_id = ? and recruitment_id = ? ");
					pst.setString(1, uF.showData(getOfferOnHoldRemark(),""));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(3, uF.parseToInt(candidateID));
					pst.setInt(4, uF.parseToInt(recruitID));
					pst.executeUpdate();
					pst.close();
					
					pst=con.prepareStatement("delete from candidate_activity_details where recruitment_id=? and candi_id=? and user_id=? and " +
							"activity_id = ?");
					pst.setInt(1, uF.parseToInt(recruitID));
					pst.setInt(2, uF.parseToInt(getCandidateID()));
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
					pst.setInt(4, CANDI_ACTIVITY_OFFER_ONHOLD_ID);
					pst.executeUpdate();
					pst.close();
					
					pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
					pst.setInt(1,uF.parseToInt(recruitID));
					pst.setInt(2,uF.parseToInt(getCandidateID()));
					pst.setString(3, "Offer Hold");
					pst.setInt(4,uF.parseToInt(strSessionEmpId));
					pst.setDate(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
					pst.setInt(6, CANDI_ACTIVITY_OFFER_ONHOLD_ID);
					pst.execute();
					pst.close();
				}
			}
	     } catch(Exception e) {
	    	 e.printStackTrace();
	     } finally {
	    	 db.closeResultSet(rs);
			 db.closeStatements(pst);	
	    	 db.closeConnection(con);
	     }
	}


	private void offerAccept(String candidateID, String recruitID, UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;	
		ResultSet rs = null;
		try {
			con=db.makeConnection(con);
			String candiStatus = "0", dateDiff="0";
			pst=con.prepareStatement("select candidate_status,candidate_joining_date from candidate_application_details where candidate_id=? and recruitment_id = ? ");
			pst.setInt(1, uF.parseToInt(candidateID));
			pst.setInt(2, uF.parseToInt(recruitID));
			rs=pst.executeQuery(); 
//			System.out.println("new Date ===> " + new Date());
			while(rs.next()){
				candiStatus = rs.getString("candidate_status");
//				System.out.println("current date == "+uF.getCurrentDate(CF.getStrTimeZone()));
//				System.out.println("joining date == "+rst.getString("candidate_joining_date"));
				
				dateDiff = uF.dateDifference(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, rs.getString("candidate_joining_date"), DBDATE);
//				System.out.println("dateDiff == "+ dateDiff);
			}
			rs.close();
			pst.close();
			
//			System.out.println("getCandiOfferAccept() == "+getCandiOfferAccept());
			if(getCandiOfferAccept() != null && getCandiOfferAccept().equals("yes")) {
				if(uF.parseToInt(dateDiff) > 0) {
					if(candiStatus.equals("0")) {
						pst = con.prepareStatement("update candidate_application_details set candidate_status = 1,offer_accept_remark = ?, offer_accept_date=? where candidate_id = ? and recruitment_id = ? ");
						pst.setString(1, uF.showData(getOfferAcceptRemark(),""));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(3, uF.parseToInt(candidateID));
						pst.setInt(4, uF.parseToInt(recruitID));
						pst.executeUpdate();
						pst.close();
						
						pst=con.prepareStatement("delete from candidate_activity_details where recruitment_id=? and candi_id=? and round_id=? and user_id=? and " +
								"activity_id = ?");
						pst.setInt(1, uF.parseToInt(recruitID));
						pst.setInt(2, uF.parseToInt(getCandidateID()));
						pst.setInt(3, uF.parseToInt(strSessionEmpId));
						pst.setInt(4, CANDI_ACTIVITY_OFFER_ACCEPT_OR_REJECT_ID);
						pst.executeUpdate();
						pst.close();
						
						pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
						pst.setInt(1,uF.parseToInt(recruitID));
						pst.setInt(2,uF.parseToInt(getCandidateID()));
//						pst.setInt(3,uF.parseToInt(getRoundID()));
						pst.setString(3, "Offer Accept");
						pst.setInt(4,uF.parseToInt(strSessionEmpId));
						pst.setDate(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
						pst.setInt(6, CANDI_ACTIVITY_OFFER_ACCEPT_OR_REJECT_ID);
						pst.execute();
						pst.close();
						
						setOfferAcceptRejectAlert(con, recruitID);
						
						sendMail();
						setMSG("You have accept your offer.");
					} else {
						setMSG("You are already accept this offer.");
					}
				} else {
					setMSG("Your offer is expired.");
				}
			} else {
				if(candiStatus.equals("0")) {
//					System.out.println("getRejectStatus ===>> " + getRejectStatus());
					if(getRejectStatus()!=null && getRejectStatus().equalsIgnoreCase("reject")) {
						pst = con.prepareStatement("update candidate_application_details set candidate_status = -1, offer_accept_remark = ?, offer_accept_date=? where candidate_id = ? and recruitment_id = ? ");
						pst.setString(1, uF.showData(getOfferAcceptRemark(),""));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(3, uF.parseToInt(candidateID));
						pst.setInt(4, uF.parseToInt(recruitID));
						pst.executeUpdate();
						pst.close();
						
						pst=con.prepareStatement("delete from candidate_activity_details where recruitment_id=? and candi_id=? and user_id=? and " +
								"activity_id = ?");
						pst.setInt(1, uF.parseToInt(recruitID));
						pst.setInt(2, uF.parseToInt(getCandidateID()));
						pst.setInt(3, uF.parseToInt(strSessionEmpId));
						pst.setInt(4, CANDI_ACTIVITY_OFFER_ACCEPT_OR_REJECT_ID);
						pst.executeUpdate();
						pst.close();
						
						pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
						pst.setInt(1,uF.parseToInt(recruitID));
						pst.setInt(2,uF.parseToInt(getCandidateID()));
						pst.setString(3, "Offer Reject");
						pst.setInt(4,uF.parseToInt(strSessionEmpId));
						pst.setDate(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
						pst.setInt(6, CANDI_ACTIVITY_OFFER_ACCEPT_OR_REJECT_ID);
						pst.execute();
						pst.close();
						
						setOfferAcceptRejectAlert(con, recruitID);
						
//							sendMail();
						setMSG("You have rejected your offer.");
					} else {
						pst = con.prepareStatement("update candidate_application_details set candidate_status = 1,offer_accept_remark = ?, offer_accept_date=? where candidate_id = ? and recruitment_id = ? ");
						pst.setString(1, uF.showData(getOfferAcceptRemark(),""));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(3, uF.parseToInt(candidateID));
						pst.setInt(4, uF.parseToInt(recruitID));
						pst.executeUpdate();
						pst.close();
						
						pst=con.prepareStatement("delete from candidate_activity_details where recruitment_id=? and candi_id=? and user_id=? and " +
								"activity_id = ?");
						pst.setInt(1, uF.parseToInt(recruitID));
						pst.setInt(2, uF.parseToInt(getCandidateID()));
						pst.setInt(3, uF.parseToInt(strSessionEmpId));
						pst.setInt(4, CANDI_ACTIVITY_OFFER_ACCEPT_OR_REJECT_ID);
						pst.executeUpdate();
						pst.close();
						
						pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
						pst.setInt(1,uF.parseToInt(recruitID));
						pst.setInt(2,uF.parseToInt(getCandidateID()));
	//					pst.setInt(3,uF.parseToInt(getRoundID()));
						pst.setString(3, "Offer Accept");
						pst.setInt(4,uF.parseToInt(strSessionEmpId));
						pst.setDate(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
						pst.setInt(6, CANDI_ACTIVITY_OFFER_ACCEPT_OR_REJECT_ID);
						pst.execute();
						pst.close();
						
						setOfferAcceptRejectAlert(con, recruitID);
						
						sendMail();
						setMSG("You have accepted your offer.");
					}
				} else {
					setMSG("You are already accept this offer.");
				}
			}
	     } catch(Exception e) {
	    	 e.printStackTrace();
	     } finally {
	    	 db.closeResultSet(rs);
			 db.closeStatements(pst);	
	    	 db.closeConnection(con);
	     }
	}

	private void setOfferAcceptRejectAlert(Connection con, String recruitID) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF=new UtilityFunctions();
		try {
			pst = con.prepareStatement("select approved_by from recruitment_details where recruitment_id=?");
			pst.setInt(1, uF.parseToInt(recruitID));
			rs = pst.executeQuery();
			String approvedBy = null;
			while (rs.next()) {
				approvedBy = rs.getString("approved_by");
			}
			rs.close();
			pst.close();
			
			if(approvedBy != null) {
				String strDomain = request.getServerName().split("\\.")[0];
				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(approvedBy);
				userAlerts.set_type(CANDIDATE_OFFER_ACCEPTREJECT_ALERT); 
				userAlerts.setStatus(INSERT_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
			}
				
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
	}
	
	public void sendMail() {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmCandiInfo = CF.getCandiInfoMap(con, false);
//			Map<String, String> hmSalaryAmountMap = getCandiSalaryDetails(con);
//			System.out.println("hmCandiInfo ===> " + hmCandiInfo);
			Map<String, String> hmCandiInner = hmCandiInfo.get(getCandidateID());
//			System.out.println("hmCandiInner ===> " + hmCandiInner);
			Map<String, String> hmCandiDesig = new HashMap<String, String>();
			Map<String,String> hmDepartment= CF.getDepartmentMap(con,null,null);
			
			pst = con.prepareStatement("select e.emp_per_id,d.designation_code,d.designation_name from recruitment_details r," +
				"designation_details d,candidate_personal_details e where r.recruitment_id = e.recruitment_id and " +
				"r.designation_id=d.designation_id and emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateID()));
			rs = pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
			while(rs.next()){
				hmCandiDesig.put(rs.getString("emp_per_id"), rs.getString("designation_name"));
			}
			rs.close();
			pst.close();
				
//			System.out.println("getCandidateID() ========= "+getCandidateID());
			String strDomain = request.getServerName().split("\\.")[0];
			CandidateNotifications nF = new CandidateNotifications(N_CANDI_ONBOARDING_CTC, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrRecruitmentId(getRecruitId());
//			System.out.println("CF.getStrEmailLocalHost() is ========= "+CF.getStrEmailLocalHost());
//			System.out.println("request.getContextPath() is ========= "+request.getContextPath());
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			nF.setStrEmpId(getCandidateID());//Created By Dattatray Date : 01-11-21 
			nF.setStrCandiFname(hmCandiInner.get("FNAME"));
			nF.setStrCandiLname(hmCandiInner.get("LNAME"));
			nF.setStrRecruitmentDesignation(hmCandiDesig.get(getCandidateID()));
			nF.setOnboardingData("?depart_id="+hmDepartment.get(strSessionEmpId)+"&candidateId="+getCandidateID()+"&recruitId="+getRecruitId());
			 
			nF.sendNotifications();
			 
			List<String> hrIDList = null; 
			pst = con.prepareStatement("select emp_id from user_details where usertype_id=7");
			rs = pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
			hrIDList = new ArrayList<String>();
			while (rs.next()) {
				hrIDList.add(rs.getString(1));
			}
			rs.close();
			pst.close();
			
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
			
			for (int i = 0; hrIDList != null && i < hrIDList.size(); i++) {
//					System.out.println("hrIDList.get(i) ===> "+hrIDList.get(i));
				Map<String, String> hmEmpInner = hmEmpInfo.get(hrIDList.get(i));
				if(hmEmpInner == null) hmEmpInner = new HashMap<String, String>();
				
//					System.out.println("hmEmpInner ===> "+hmEmpInner);
			 	Notifications nF1 = new Notifications(N_CANDI_OFFER_ACCEPT_REJECT, CF);
			 	nF1.setDomain(strDomain);
				nF1.request = request;
			 	nF1.setStrEmpId(hrIDList.get(i));
//				System.out.println("CF.getStrEmailLocalHost() is ========= "+CF.getStrEmailLocalHost());
//				System.out.println("request.getContextPath() is ========= "+request.getContextPath());
			 	nF1.setStrHostAddress(CF.getStrEmailLocalHost());
				nF1.setStrHostPort(CF.getStrHostPort());
			 	nF1.setStrContextPath(request.getContextPath());
				 
			 	nF1.setStrEmpFname(hmEmpInner.get("FNAME"));
			 	nF1.setStrEmpLname(hmEmpInner.get("LNAME"));//Created by Dattatray Date : 08-July-2021 Note : setStrEmpFname to setStrEmpLname
			 	nF1.setStrCandiFname(hmCandiInner.get("FNAME"));
			 	nF1.setStrCandiLname(hmCandiInner.get("LNAME"));
			 	nF1.setStrRecruitmentDesignation(hmCandiDesig.get(getCandidateID()));
				 //nF.setOnboardingData("?depart_id="+hmDepartment.get(strSessionEmpId)+"&candidateId="+getCandidateID()+"&recruitId="+getRecruitID());
			 	nF1.setEmailTemplate(true);
				 
				nF1.sendNotifications();
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
//	public Map<String, Map<String, String>> getCandiInfoMap(Connection con) {
//		Map<String, Map<String, String>> hmCandiInfo = new HashMap<String, Map<String, String>>();
//		UtilityFunctions uF = new UtilityFunctions();
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		try {
//			Map<String, String> hmCandiInner = new HashMap<String, String>();
//			
//			pst = con.prepareStatement("SELECT emp_per_id, emp_fname, emp_lname, empcode, emp_image, emp_email, emp_date_of_birth, candidate_joining_date, " +
//					"emp_gender, marital_status,ctc_offered FROM candidate_personal_details order by emp_per_id");
//			rs = pst.executeQuery();
////			System.out.println("new Date ===> " + new Date());
//			while (rs.next()) {
//				if (rs.getInt("emp_per_id") < 1) {
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
//				hmCandiInner.put("JOINING_DATE", uF.getDateFormat(rs.getString("candidate_joining_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmCandiInner.put("GENDER", rs.getString("emp_gender"));
//				hmCandiInner.put("MARITAL_STATUS", rs.getString("marital_status"));
//				hmCandiInner.put("OFFERED_CTC", rs.getString("ctc_offered"));
//				 
//				hmCandiInfo.put(rs.getString("emp_per_id"), hmCandiInner);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			//log.error(e.getClass() + ": " + e.getMessage(), e);
//		}
//		return hmCandiInfo;
//	}
	
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;	
	}


	String recruitId;
	String candidateID;
	String updateRemark;
	String offerAcceptRemark;
	String offerOnHoldRemark;
	String candiOfferAccept;
	
	public String getOfferOnHoldRemark() {
		return offerOnHoldRemark;
	}

	public void setOfferOnHoldRemark(String offerOnHoldRemark) {
		this.offerOnHoldRemark = offerOnHoldRemark;
	}

	public String getCandiOfferAccept() {
		return candiOfferAccept;
	}

	public void setCandiOfferAccept(String candiOfferAccept) {
		this.candiOfferAccept = candiOfferAccept;
	}

	public String getOfferAcceptRemark() {
		return offerAcceptRemark;
	}

	public void setOfferAcceptRemark(String offerAcceptRemark) {
		this.offerAcceptRemark = offerAcceptRemark;
	}

	public String getUpdateRemark() {
		return updateRemark;
	}

	public void setUpdateRemark(String updateRemark) {
		this.updateRemark = updateRemark;
	}

	public String getCandidateID() {
		return candidateID;
	}

	public void setCandidateID(String candidateID) {
		this.candidateID = candidateID;
	}

	public String getMSG() {
		return MSG;
	}

	public void setMSG(String mSG) {
		MSG = mSG;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getRejectStatus() {
		return rejectStatus;
	}

	public void setRejectStatus(String rejectStatus) {
		this.rejectStatus = rejectStatus;
	}

	public String getRejectType() {
		return rejectType;
	}

	public void setRejectType(String rejectType) {
		this.rejectType = rejectType;
	}
	

}
