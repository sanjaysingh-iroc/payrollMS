package com.konnect.jpms.recruitment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.document.HeaderFooterPageEvent;
import com.konnect.jpms.payroll.ApprovePayroll;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillJD;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.task.FillProjectList;
import com.konnect.jpms.util.CandidateNotifications;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CandidateMyProfilePopup extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	String strUserType = null;

	String strSessionEmpId = null;
	String strEmpOrgId = null;
	
	CommonFunctions CF;
	private String CandID;
	private String userId;
	private String recruitId;
	private String[] salary_head_id;
	private String[] salary_head_value;
	private String[] emp_salary_id;
	private String[] isDisplay;
	private String form;
	private String apptype;
	private String callType;
	
	private String offerReleaseCommunication;
	private String needApprovalForOfferRelease;
	private String needApproveRequestForOfferRelease;
	private String strOCApprover;
	private String offerNegotiationApproval;
	private List<FillEmployee> empList;
	private List<FillProjectList> projectList;

//	start parvez date: 10-01-2022===
	List<FillSalaryHeads> salaryHeadList;
	String[] strJoiningBonus;
	String strJoiningBonusAmount;
	String strAdditionalComment;
//===end parvez date: 10-01-2022	
	
	private String operation;
	private List<FillJD> liveJDList;
	
	String candiApplicationId;
	
	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}
	
	private boolean disableSalaryStructure;

	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		if (getHrchoice() == null)
			setHrchoice("0");

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);

		request.setAttribute(PAGE, "/jsp/recruitment/CandidateMyProfile.jsp");
//		request.setAttribute(TITLE, "My Profile");

		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		Map<String,String> medicalQuest=new HashMap<String,String>();
		medicalQuest.put("1", "Are you now receiving medical attention:");
		medicalQuest.put("2", "Have you had any form of serious illness or operation");
		medicalQuest.put("3", "Have you had any illness in the last two years? YES/NO If YES, please give the details about the same and any absences from work: ");
		medicalQuest.put("4", "Has any previous post been terminated on medical grounds?");
		medicalQuest.put("5", "Do you have an allergies?");
		request.setAttribute("medicalQuest",medicalQuest);
		
		/*if(getStrJoiningBonus()!=null){
			System.out.println("CMPPU/131--strJoiningBonus="+getStrJoiningBonus()[0]);
			System.out.println("CMPPU/132--strJoiningBonusAmount="+getStrJoiningBonusAmount());
			System.out.println("CMPPU/133--strAdditionalComment="+getStrAdditionalComment());
		}*/
		
		
		// Start : Dattatray Date : 20-July-2021
//		EncryptionUtils encryption = new EncryptionUtils();
		if (getCandID().contains("-") && getRecruitId().contains("-")) {
			setCandID(getCandID()); //encryption.decrypt(getCandID())
			setRecruitId(getRecruitId()); //encryption.decrypt(getRecruitId())
		}// End : Dattatray Date : 20-July-2021
		
		liveJDList = new FillJD(request).fillLiveJobs(uF, getCandID());
		empList = new FillEmployee(request).fillGhrHrCeoAndCfo();
		viewProfile(getCandID(), uF);

		if (getRecruitId() != null && uF.parseToInt(getRecruitId())>0) {
			loadFilledData(uF);
		}
			
		String ofcApprove = request.getParameter("ofcApprove");
		if (ofcApprove != null && !ofcApprove.equalsIgnoreCase("null")) {
			return insertOfferFinalizationCommunicationApproval(uF);
		}
		
		
		String revokeNegotiationReq = request.getParameter("revokeNegotiationReq");
		if (revokeNegotiationReq != null && !revokeNegotiationReq.equalsIgnoreCase("null")) {
			return revokeOfferFinalizationCommunication(uF);
		}
		
		String ofcSubmit = request.getParameter("ofcSubmit");
		if (ofcSubmit != null && !ofcSubmit.equalsIgnoreCase("null")) {
			return insertOfferFinalizationCommunication(uF);
		}
		
		String transferToNewJD = request.getParameter("transferToNewJD");
		if (transferToNewJD != null && !transferToNewJD.equalsIgnoreCase("null")) {
			return transferCandidateToNewJD(uF);
		}
		
		String intSubmitComment = request.getParameter("intSubmitComment");
		if (intSubmitComment != null && !intSubmitComment.equalsIgnoreCase("null")) {
			return insertInterviewComment(uF);
		}

		String hrsubmit = request.getParameter("hrsubmit");
		if (hrsubmit != null && !hrsubmit.equalsIgnoreCase("null")) {
			insertEmpSalaryDetails(uF);
			return insertHrInterview(uF);
		}
		String preveiwOffer = request.getParameter("preveiwOffer");
		System.out.println("CMPPU/172--preveiwOffer="+preveiwOffer);
		if (preveiwOffer != null && !preveiwOffer.equalsIgnoreCase("null")) {
			insertEmpSalaryDetailsForPreview(uF);
		}
		if (getOperation() != null && getOperation().equals("PREVIEW")) {
			viewOfferLetter();
			return null;
		}
		return SUCCESS;
	}

	private String revokeOfferFinalizationCommunication(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update candidate_application_details set offer_negotiation_approval_request_remark=null,need_approval_for_offer_negotiation=null," +
				" offer_negotiation_approver=null,offer_negotiation_approval_requested_by=null,offer_negotiation_approval_request_date=null where candidate_id=? and recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getCandID()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "";

	}

	private String insertOfferFinalizationCommunicationApproval(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;// Created By Dattatray Date:02-July-2021
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpUserTypeId = CF.getEmployeeIdUserTypeIdMap(con);
			
			pst = con.prepareStatement("update candidate_application_details set offer_negotiation_request_approve_remark=?,is_negotiation_approve=?," +
				"offer_negotiation_request_approved_by=?,offer_negotiation_request_approve_date=? where candidate_id=? and recruitment_id=?");
			pst.setString(1, getOfferNegotiationApproval());
			pst.setBoolean(2, uF.parseToBoolean(getNeedApproveRequestForOfferRelease()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(5, uF.parseToInt(getCandID()));
			pst.setInt(6, uF.parseToInt(getRecruitId()));
			pst.executeUpdate();
//			System.out.println("pst ===>>" + pst);
			pst.close();
			
			// Start : Dattatray Date:02-July-2021
			pst = con.prepareStatement("select * from candidate_application_details where candidate_id=? and recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(getCandID()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				String candiName = CF.getCandiNameByCandiId(con, rs.getString("candidate_id"));
				String alertData = "<div style=\"float: left;\"> " + candiName + "'s offer negotiation approved for job code " + rs.getString("job_code")
						+ " by <b> " + hmEmpName.get(strSessionEmpId) + " </b>  </div>";
				String alertAction = "RecruitmentDashboard.action?pType=WR&recruitId=" + rs.getString("recruitment_id");

				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(request.getServerName().split("\\.")[0]);
				userAlerts.setStrEmpId(rs.getString("offer_negotiation_approval_requested_by"));
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
				userAlerts.setCurrUserTypeID(hmEmpUserTypeId.get(rs.getString("offer_negotiation_approval_requested_by")));
				userAlerts.setStatus(INSERT_WR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
				

			}
			rs.close();
			pst.close();
			// end dattatray
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);// Created By Dattatray Date:02-July-2021
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "";

	}

	private String insertOfferFinalizationCommunication(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
//			System.out.println("getStrOCApprover() ===>> "+getStrOCApprover()+" -- getNeedApprovalForOfferRelease() ===>> " + getNeedApprovalForOfferRelease()+" -- offerReleaseCommunication ===>> " + getOfferReleaseCommunication());
			
			pst = con.prepareStatement("update candidate_application_details set offer_negotiation_approval_request_remark=?,need_approval_for_offer_negotiation=?," +
				" offer_negotiation_approver=?,offer_negotiation_approval_requested_by=?,offer_negotiation_approval_request_date=? where candidate_id=? and recruitment_id=?");
			pst.setString(1, getOfferReleaseCommunication());
			pst.setBoolean(2, uF.parseToBoolean(getNeedApprovalForOfferRelease()));
			pst.setInt(3, uF.parseToInt(getStrOCApprover()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(6, uF.parseToInt(getCandID()));
			pst.setInt(7, uF.parseToInt(getRecruitId()));
			pst.executeUpdate();
			pst.close();
			
			
			/*pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getCandID()));
			pst.setString(3, "Job Profile Change");
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setDate	(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setInt(6, CANDI_ACTIVITY_CANDI_JD_CHANGE_ID);
			pst.execute();
			pst.close();*/
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "";

	}

	
	private String transferCandidateToNewJD(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			String newRecruitId = request.getParameter("newRecruitId");
			String transferReason = request.getParameter("transferReason");
			
			String jobCode = CF.getRecruitmentNameById(con, uF, newRecruitId);
//			System.out.println("in JD change newRecruitId ===>> " + newRecruitId+ " -- transferReason ===>> " + transferReason + " -- getCandID() ===>> " + getCandID());
			pst = con.prepareStatement("update candidate_application_details set recruitment_id=?,job_code=?,recruit_id_changed_reason=?," +
				"recruit_id_changed_date=?,recruit_id_changed_by=?,prev_recruit_id=? where candidate_id=? and recruitment_id=?");
			pst.setInt(1, uF.parseToInt(newRecruitId));
			pst.setString(2, jobCode);
			pst.setString(3, transferReason);
			pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setInt(6, uF.parseToInt(getRecruitId()));
			pst.setInt(7, uF.parseToInt(getCandID()));
			pst.setInt(8, uF.parseToInt(getRecruitId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("update candidate_interview_panel set recruitment_id=? where candidate_id=? and recruitment_id=?");
			pst.setInt(1, uF.parseToInt(newRecruitId));
			pst.setInt(2, uF.parseToInt(getCandID()));
			pst.setInt(3, uF.parseToInt(getRecruitId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("update candidate_skills_rating_details set recruitment_id=? where candidate_id=? and recruitment_id=?");
			pst.setInt(1, uF.parseToInt(newRecruitId));
			pst.setInt(2, uF.parseToInt(getCandID()));
			pst.setInt(3, uF.parseToInt(getRecruitId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("update candidate_interview_availability set recruitment_id=? where emp_id=? and recruitment_id=?");
			pst.setInt(1, uF.parseToInt(newRecruitId));
			pst.setInt(2, uF.parseToInt(getCandID()));
			pst.setInt(3, uF.parseToInt(getRecruitId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("update candidate_interview_panel_availability set recruitment_id=? where candidate_id=? and recruitment_id=?");
			pst.setInt(1, uF.parseToInt(newRecruitId));
			pst.setInt(2, uF.parseToInt(getCandID()));
			pst.setInt(3, uF.parseToInt(getRecruitId()));
			pst.executeUpdate();
			pst.close();
			
			pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(newRecruitId));
			pst.setInt(2, uF.parseToInt(getCandID()));
//			pst.setInt(3,uF.parseToInt(getRoundID()));
			pst.setString(3, "Job Profile Change");
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setDate	(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setInt(6, CANDI_ACTIVITY_CANDI_JD_CHANGE_ID);
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "transferToNewJD";

	}

	private void loadFilledData(UtilityFunctions uF) {
//		getInterviewDateforHr();
//		getCommentsHr();
//		getPanelScheduleInfo();
//		getPanelComment(getCandID());
		getPanelComment(uF);
		getHrData(uF);
		getInterviewDates(uF);
		getCandiActivityDetails(uF);
		getAssessmentScoreCard(uF);
	} 

	
	
	public void viewOfferLetter() {
//		System.out.println("viewOfferLetter()");
		Connection con = null;
		ResultSet rst = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmCandiInfo = CF.getCandiInfoMap(con, false);

			
//			StringBuilder sbCandiSalTable = getCandiSalaryDetails(con);	
//			if(sbCandiSalTable == null) sbCandiSalTable = new StringBuilder();
//			System.out.println("sbCandiSalTable ===>> " + sbCandiSalTable.toString());
			
			Map<String, String> hmCandiInner = hmCandiInfo.get(getCandID());
			
	//===start parvez date: 11-01-2022===		
			/*pst = con.prepareStatement("select e.candi_application_deatils_id,e.candidate_id,r.org_id,d.designation_code,d.designation_name,r.reporting_to_person_ids,r.job_title" +
				" from recruitment_details r, designation_details d, candidate_application_details e where r.recruitment_id = e.recruitment_id " +
				" and r.designation_id = d.designation_id and candidate_id=?");*/
			
			boolean isJoiningBonus = CF.getFeatureManagementStatus(request, uF, F_ENABLE_JOINING_BONUS_DETAILS);
			
			if(isJoiningBonus){
				pst = con.prepareStatement("select e.candi_application_deatils_id,e.candidate_id,e.joining_bonus_amount_details,r.org_id,d.designation_code,d.designation_name,r.reporting_to_person_ids,r.job_title" +
						" from recruitment_details r, designation_details d, candidate_application_details e where r.recruitment_id = e.recruitment_id " +
						" and r.designation_id = d.designation_id and candidate_id=?");
			} else{
				pst = con.prepareStatement("select e.candi_application_deatils_id,e.candidate_id,r.org_id,d.designation_code,d.designation_name,r.reporting_to_person_ids,r.job_title" +
						" from recruitment_details r, designation_details d, candidate_application_details e where r.recruitment_id = e.recruitment_id " +
						" and r.designation_id = d.designation_id and candidate_id=?");
			}
			
	//===end parvez date: 11-01-2022===		
			pst.setInt(1, uF.parseToInt(getCandID()));
			rst = pst.executeQuery();
//			System.out.println("pst1222 ===> " + pst);
			Map<String, String> hmCandiDesig = new HashMap<String, String>();
			Map<String, String> hmCandiOrg = new HashMap<String, String>();
			Map<String, String> hmCandiJobTitle = new HashMap<String, String>();//Created By Dattatray Date : 10-08-2021
			
			Map<String, String> hmCandiJoiningBonusAmountD = new HashMap<String, String>();//Created By Parvez Date : 11-01-2022
			
			String reportingToEmpIds = null;
			while(rst.next()) {
				hmCandiDesig.put(rst.getString("candidate_id"), rst.getString("designation_name"));
				hmCandiOrg.put(rst.getString("candidate_id"), rst.getString("org_id"));
				hmCandiJobTitle.put(rst.getString("candidate_id"), rst.getString("job_title"));//Created By Dattatray Date : 10-08-2021
				setCandiApplicationId(rst.getString("candi_application_deatils_id"));
				reportingToEmpIds = rst.getString("reporting_to_person_ids");
		//===start parvez date: 11-01-2022===		
				if(isJoiningBonus){
					hmCandiJoiningBonusAmountD.put(rst.getString("candidate_id"), rst.getString("joining_bonus_amount_details"));
				}
		//===end parvez date: 11-01-2022===
				
			}
			rst.close();
			pst.close();
//			System.out.println("hmCandiJobTitle ===>> " + hmCandiJobTitle);
			String reportMgrDesig = "";
			if (reportingToEmpIds !=null) { // Created by Dattatray Date : 26-June-21 Note : if Condition checked beacuse nullpointerexception occur
				String[] strTmp = reportingToEmpIds.split(",");
				for(int i=0; strTmp!=null && i<strTmp.length; i++) {
					if(uF.parseToInt(strTmp[i])>0) {
						reportMgrDesig = CF.getEmpDesigMapByEmpId(con, strTmp[i]);
					}
				}
			}
			
			pst = con.prepareStatement("select * from document_signature where org_id =?");
			pst.setInt(1, uF.parseToInt(hmCandiOrg.get(getCandID())));
			rst = pst.executeQuery();
			String strAuthSign = null;
			String strHrSign = null;
			String strRecruiterSign = null;
			while (rst.next()) {
				if(rst.getInt("signature_type") == 1) {
					strAuthSign = rst.getString("signature_image");
				}
				if(rst.getInt("signature_type") == 2) {
					strHrSign = rst.getString("signature_image");
				}
				if(rst.getInt("signature_type") == 3) {
					if(rst.getInt("user_id") == uF.parseToInt(strSessionEmpId)) {
						strRecruiterSign = rst.getString("signature_image");
					}
				}
			}
			rst.close();
			pst.close();
//			System.out.println("strHrSign =========>> " + strHrSign);
			
			Map<String, Map<String, String>> hmHeader=new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmFooter=new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select * from document_collateral");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()) {
				if(rst.getString("_type").equals("H")) {
					Map<String, String> hmInner=new HashMap<String, String>();
					hmInner.put("COLLATERAL_ID", rst.getString("collateral_id"));
					hmInner.put("COLLATERAL_PATH", rst.getString("collateral_image"));
					hmInner.put("COLLATERAL_IMG_ALIGN", rst.getString("image_align"));
					hmInner.put("COLLATERAL_TEXT", uF.showData(rst.getString("collateral_text"),""));
					
					hmHeader.put(rst.getString("collateral_id"), hmInner);
				} else {
					Map<String, String> hmInner=new HashMap<String, String>();
					hmInner.put("COLLATERAL_ID", rst.getString("collateral_id"));
					hmInner.put("COLLATERAL_PATH", rst.getString("collateral_image"));
					hmInner.put("COLLATERAL_IMG_ALIGN", rst.getString("image_align"));
					hmInner.put("COLLATERAL_TEXT", uF.showData(rst.getString("collateral_text"),""));
					
					hmFooter.put(rst.getString("collateral_id"), hmInner);
				}
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select * from document_comm_details where trigger_nodes like '%,"+NODE_CANDIDATE_OFFER_ID+",%' and status=1 and org_id=? order by document_id desc limit 1");
			pst.setInt(1, uF.parseToInt(hmCandiOrg.get(getCandID())));
			rst = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
//			System.out.println("new Date ===> " + new Date());
			String strDocumentName = null;
			String strDocumentContent = null;
			String strDocumentHeader = null;
			String strDocumentFooter = null;
			String strHeader = null;
			String strFooter = null;
			String strHeaderImageAlign="";
			String strHeaderCollateralText="";
			String strHeaderTextAlign="";
			String strFooterImageAlign="";
			String strFooterCollateralText="";
			String strFooterTextAlign="";
			
			while (rst.next()) {  
				strDocumentName = rst.getString("document_name");
				strDocumentContent = rst.getString("document_text");
				
				if(rst.getString("collateral_header")!=null && !rst.getString("collateral_header").equals("") && hmHeader.get(rst.getString("collateral_header"))!=null){
					Map<String, String> hmInner=hmHeader.get(rst.getString("collateral_header"));
					strHeader = uF.showData(hmInner.get("COLLATERAL_PATH"),"");
					strHeaderImageAlign=uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"),"");
					strHeaderCollateralText=uF.showData(hmInner.get("COLLATERAL_TEXT"),"");
					strHeaderTextAlign=uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"),"");
				}
				if(rst.getString("collateral_footer")!=null && !rst.getString("collateral_footer").equals("") && hmFooter.get(rst.getString("collateral_footer"))!=null){
					Map<String, String> hmInner=hmFooter.get(rst.getString("collateral_footer"));
					strFooter = uF.showData(hmInner.get("COLLATERAL_PATH"),"");
					strFooterImageAlign=uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"),"");
					strFooterCollateralText=uF.showData(hmInner.get("COLLATERAL_TEXT"),"");
					strFooterTextAlign=uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"),"");
				}
			}
			rst.close();
			pst.close();
			
			if(strDocumentName!=null){
				strDocumentName = strDocumentName.replace(" ", "");
			}
			
			String strDomain = request.getServerName().split("\\.")[0];
			
			CandidateNotifications nF = new CandidateNotifications(N_CANDI_JOINING_OFFER_CTC, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrRecruitmentId(getRecruitId());
			nF.setStrHostAddress(CF.getStrEmailLocalHost()); 
			nF.setStrHostPort(CF.getStrHostPort()); 
//			nF.setStrContextPath(request.getServletPath());
			nF.setStrContextPath(request.getContextPath());
			
			nF.setStrCandiSalutation(hmCandiInner.get("SALUTATION"));
			nF.setStrCandiFname(hmCandiInner.get("FNAME"));
			nF.setStrCandiLname(hmCandiInner.get("LNAME"));
			nF.setStrReportEmpDesignation(reportMgrDesig);
			nF.setStrCandiJoiningDate(hmCandiInner.get("JOINING_DATE"));
			nF.setStrEmpId(getCandID());//Created By Dattatray Date : 01-11-21
			
			//Started by Dattatray Date:29-09-21
			String name = uF.showData(nF.getStrCandiSalutation(), "")+" "+uF.showData(nF.getStrCandiFname(),"")+" "+uF.showData(nF.getStrCandiLname(),"");
			Map<String, String> hmCandiSalDetails = getCandiSalaryDetails(con, name, nF.getStrRecruitmentDesignation(), nF.getStrRecruitmentWLocation(), nF.getStrCandiJoiningDate(), nF.getStrRecruitmentLevel(), nF.getStrRecruitmentGrade(),nF.getStrLegalEntityName());
			//Ended by Dattatray Date:29-09-21
			nF.setStrCandiCTC(hmCandiSalDetails.get("CANDI_CTC"));
			nF.setStrCandiCTCInWords(hmCandiSalDetails.get("CANDI_CTC_WORDS"));
			nF.setStrCandiAnnualCTC(hmCandiSalDetails.get("CANDI_ANNUAL_CTC"));
			nF.setStrOfferedSalaryStructure(hmCandiSalDetails.get("OFFERED_SALARY_STRUCTURE"));
			nF.setStrCandidateId(hmCandiInner.get("CANDI_ID"));//Created By Dattatray Date : 05-10-21
			nF.setIsEmailIdCC(true);//Created By Dattatray Date : 02-July-2021
//			System.out.println("hmCandiDesig.get(getCandID()) ===>> " + hmCandiDesig.get(getCandID()));
			nF.setStrRecruitmentDesignation(hmCandiDesig.get(getCandID()));
			nF.setStrJobTitle(hmCandiJobTitle.get(getCandID()));//Created By Dattatray Date : 10-08-2021
			nF.setOfferAcceptData("?candidateID="+getCandID()+"&recruitID="+getRecruitId()+"&candiOfferAccept=yes&updateRemark=Update");
	
	//===start parvez date: 11-01-2022===
			
			String[] strJoiningbonusAmountDetails = hmCandiJoiningBonusAmountD.get(getCandID()).split("::");
			System.out.println("CMPPU/605--joiningBonusAmount="+strJoiningbonusAmountDetails[1]);
			System.out.println("CMPPU/606--AdditionalComment="+strJoiningbonusAmountDetails[2]);
			nF.setStrJoiningBonusAmount(strJoiningbonusAmountDetails[1].trim());
			nF.setStrAdditionalComment(strJoiningbonusAmountDetails[2].trim());
	//===end parvez date: 11-01-2022===
			
			Map<String, String> hmParsedContent = null;
//			Document document = new Document(PageSize.A4);
			Document document = new Document(PageSize.A4,40, 40, 10, 60); 

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			StringBuilder sbHeader = new StringBuilder();
			StringBuilder sbFooter = new StringBuilder();
			
			String strDocName = null;
			String strDocContent = null;
			
			if(strDocumentContent!=null) {
//				hmParsedContent  = nF.parseContent(strDocumentContent, "", "");
				hmParsedContent  = nF.parseContent(strDocumentContent, "", "");
				strDocName = strDocumentName;
				strDocContent = hmParsedContent.get("MAIL_BODY");
				String strDocument = hmParsedContent.get("MAIL_BODY");
				//Start By Dattatray Date:05-10-21
				String brTag="";
//				System.out.println("cAdd : "+nF.getStrCandiAddress().trim().isEmpty());
				//Start By Dattatray Date:06-10-21
				if(nF.getStrCandiAddress() ==null || nF.getStrCandiAddress().trim().isEmpty()) {
					brTag = "<br/><br/><br/>";
				}//End By Dattatray Date:06-10-21
				if (nF.getIntAddressLineCnt() == 1) {
					brTag = "<br/><br/>";
				}else if (nF.getIntAddressLineCnt() == 2) {
					brTag = "<br/>";
				}
				//Ended By Dattatray Date:05-10-21
//				System.out.println("test1 : "+brTag);
				if(strDocument!=null) {
//					strDocument = strDocument.replaceAll("<br/>", "");
					
					//Satrt Dattatray Date : 31-07-21  
					if (strDocument.contains("<pre style=\"text-align:justify\">") || strDocument.contains("<pre style=\"text-align:justify;\">") || strDocument.contains("<pre style=\"text-align: justify;\">") || strDocument.contains("<pre style=\"text-align: justify\">")) {
//						System.out.println("if");
						if (strDocument.contains("<pre ")) {
							strDocument = strDocument.replaceAll("<pre ", "<p ");
						}
						 if(strDocument.contains("<pre>") ){
							 strDocument = strDocument.replaceAll("<pre>", "<p>");
						 }
						//
						/*if (strDocument.contains("><span")) {
							strDocument = strDocument.replaceAll("><span ", "><p style=\"text-align: justify\"><span ");
						}
						if (strDocument.contains("</span>")) {
							strDocument = strDocument.replaceAll("</span>", "</span></p>");
						}*/

						if (strDocument.contains("</pre>")) {
							strDocument = strDocument.replaceAll("</pre>", "</p>");
						}
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p>", "<br/><p style=\"text-align: justify\">", true, true, "<p>");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<li>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
//						strDocument = replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>", "<p style=\"text-align: justify\">", true, true, "<p style=\"text-align: justify\">");
//						strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
//						strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</p>", true, true, "<p>");
//						strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
						//Created By Dattatray Date:06-10-21
						if(strDocument.contains("Director of People")) {
							strDocument = uF.replaceBetweenTwoString(strDocument, "Director of ", "People", true, true,"Director of People"+brTag);//Created By Dattatray Date:05-10-21
						}
						
					}else {
						System.out.println("Else");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<li>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<p>", true, true, "<p>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", " </li>	", true, true, "</li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<pre>", true, true, "<pre>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<pre>", "<br/>", true, true, "<pre>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<span style=\"color:rgb(255, 255, 255)\"><span style=\"font-size:8px\">aaaaa <br/></span>----------------------------------------------------------------------</span><strong>", "SALARY STRUCTURE", true, true, "<span style=\"color:rgb(255, 255, 255)\"><span style=\"font-size:8px\">aaaaa <br/></span>"+brTag+"----------------------------------------------------------------------</span><strong>SALARY STRUCTURE");//Created By Dattatray Date:05-10-21
						//Created By Dattatray Date:06-10-21
						if(strDocument.contains("Director of People")) {
							strDocument = uF.replaceBetweenTwoString(strDocument, "Director of ", "People", true, true,"Director of People"+brTag);//Created By Dattatray Date:05-10-21
						}
					}
					//End Dattatray Date : 31-07-21 
				}
//				System.out.println("strDocument : "+strDocument);
				String headerPath="";
				if(strHeader!=null && !strHeader.equals("")) {
//					headerPath=CF.getStrDocRetriveLocation()+strHeader;
					if(CF.getStrDocRetriveLocation()==null) { 
						headerPath =  DOCUMENT_LOCATION + strHeader;
					} else { 
						headerPath = CF.getStrDocSaveLocation() +I_COLLATERAL+"/"+I_IMAGE+"/"+strHeader;
					}
				}
				
				if(headerPath != null && !headerPath.equals("")) {
					sbHeader.append("<table style=\"width: 100%;\"><tr>");
					sbHeader.append("<td>");
					if(strHeader!=null && !strHeader.equals("")) {
						sbHeader.append("<img src=\""+headerPath+"\">");
					}
					sbHeader.append("</td>");
					sbHeader.append("</tr></table>");
				}
				
				
				PdfWriter writer = PdfWriter.getInstance(document, buffer);
				HeaderFooterPageEvent event = new HeaderFooterPageEvent(sbHeader.toString(), strFooterCollateralText);
			    writer.setPageEvent(event);
				document.open();
				HTMLWorker hw = new HTMLWorker(document);
				if(strDocument.contains(HR_SIGNATURE)) {
//					System.out.println("strHrSign ===> "+strHrSign);
					//Created By Dattatray Date:27-09-21 Note : if condition check
					if (strHrSign !=null) {
						String imageUrl=CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+hmCandiOrg.get(getCandID())+"/"+I_DOC_SIGN+"/"+strHrSign;
						String 	strSignature ="<img src=\""+imageUrl+"\">";
						strDocument = strDocument.replace(HR_SIGNATURE, strSignature);
						System.out.println("CMPPU/734--strSignature =========>> " + strSignature);
					}
					
				}
			
				if(strDocument.contains(AUTHORITY_SIGNATURE)) {
				    String imageUrl=CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+hmCandiOrg.get(getCandID())+"/"+I_DOC_SIGN+"/"+strAuthSign;
					String 	strSignature ="<img src=\""+imageUrl+"\">";
					strDocument = strDocument.replace(AUTHORITY_SIGNATURE, strSignature);
				}
				
//				System.out.println("strDocument ===>> " + strDocument);
				if(strDocument.contains(RECRUITER_SIGNATURE)) {
				    String imageUrl=CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+hmCandiOrg.get(getCandID())+"/"+I_DOC_SIGN+"/"+strSessionEmpId+"/"+strRecruiterSign;
					String 	strSignature ="<img src=\""+imageUrl+"\">";
//					System.out.println("strSignature ===>> " + strSignature);
					strDocument = strDocument.replace(RECRUITER_SIGNATURE, strSignature);
				}
				hw.parse(new StringReader(strDocument));
				document.close();
				
				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition","attachment; filename=OfferPreview.pdf");
				ServletOutputStream out = response.getOutputStream();              
				buffer.writeTo(out);
				out.flush();
				out.close();
				buffer.close();
				out.close();
				
				return;
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	
	private void getAssessmentScoreCard(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		try {
//			pst = con.prepareStatement("select *,(marks*100/weightage) as average from(select sum(marks) as marks ,sum(weightage) as weightage," +
//					"emp_id,plan_id from training_question_answer where learning_plan_id=? group by emp_id,plan_id)as a");
			pst = con.prepareStatement("select * from(select sum(marks) as marks ,sum(weightage) as weightage,assessment_details_id,round_id" +
				" from assessment_question_answer where recruitment_id=? and candidate_id=? group by round_id,assessment_details_id)as a");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getCandID()));
			rs = pst.executeQuery();
			Map<String, String> hmAssessRateRoundIdWise = new HashMap<String, String>();
			while (rs.next()) {
				
				double dblMarks = uF.parseToDouble(rs.getString("marks"));
//				System.out.println("dblTotalMarks"+dblTotalMarks);
				double dblWeightage = uF.parseToDouble(rs.getString("weightage"));
				String aggregate = "0";
				if(dblWeightage>0) {
					aggregate = uF.formatIntoTwoDecimal((dblMarks * 100)/dblWeightage);
				}
				hmAssessRateRoundIdWise.put(rs.getString("round_id")+"_"+rs.getString("assessment_details_id"), aggregate);
			}
			rs.close();
			pst.close();
			
			// request.setAttribute("hmUserTypeID", hmUserTypeID);
			request.setAttribute("hmAssessRateRoundIdWise", hmAssessRateRoundIdWise);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
private void getCandiActivityDetails(UtilityFunctions uF) {
	
	Connection con = null;
	PreparedStatement pst = null;
	Database db = new Database();
	db.setRequest(request);
	ResultSet rst = null;
//	Map<String, List<String>> hmDegreeName = new HashMap<String, List<String>>();
	try {
		con=db.makeConnection(con);
		
		Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
		Map<String, String> hmJobName = CF.getRecruitmentNameMap(con, uF);
		
		List<List<String>> activityList = new ArrayList<List<String>>();
		pst=con.prepareStatement("select * from candidate_activity_details where candi_id = ? and recruitment_id = ? order by candi_activity_id desc");
		pst.setInt(1, uF.parseToInt(getCandID()));
		pst.setInt(2, uF.parseToInt(getRecruitId()));
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
//		System.out.println("pst ===> "+pst);
		while(rst.next()){
			List<String> innerList = new ArrayList<String>();
			innerList.add(rst.getString("activity_name"));
			innerList.add(hmEmpName.get(rst.getString("user_id")));
			innerList.add(uF.getDateFormat(rst.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
			innerList.add(hmJobName.get(rst.getString("recruitment_id")));
			innerList.add(rst.getString("round_id"));
			innerList.add(rst.getString("activity_id"));
			activityList.add(innerList);
		}
		rst.close();
		pst.close();
//		System.out.println("activityList ===> "+activityList);
		request.setAttribute("activityList", activityList);
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		db.closeResultSet(rst);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
//	return hmDegreeName;
}



private Map<String, List<String>> getCandiDegreeName(Connection con){
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String, List<String>> hmDegreeName = new HashMap<String, List<String>>();
		try {
			List<String> degreeList = new ArrayList<String>();
			pst=con.prepareStatement("select emp_id,education_id from candidate_education_details");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				degreeList =hmDegreeName.get(rst.getString("emp_id"));
				if(degreeList== null)degreeList = new ArrayList<String>();
				degreeList.add(rst.getString("education_id"));
				hmDegreeName.put(rst.getString("emp_id"), degreeList);
			}
			rst.close();
			pst.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(rst != null) {
				try {
					rst.close();
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
		return hmDegreeName;
	}
	
	
	private Map<String, List<String>> getCandiSkillsName(Connection con){
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String, List<String>> hmSkills = new HashMap<String, List<String>>();
		try {
			List<String> skillList = new ArrayList<String>();
			pst=con.prepareStatement("select emp_id,skill_id from candidate_skills_description");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				skillList =hmSkills.get(rst.getString("emp_id"));
				if(skillList== null)skillList = new ArrayList<String>();
				skillList.add(rst.getString("skill_id"));
				hmSkills.put(rst.getString("emp_id"), skillList);
			}
			rst.close();
			pst.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(rst != null) {
				try {
					rst.close();
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
		return hmSkills;
	}
	
	
public String getTimeDurationBetweenDates(String strStartDate, String strStartFormat, String strEndDate, String strEndFormat, UtilityFunctions uF){
		
		StringBuilder sbTimeDuration = new StringBuilder();
		try {
			LocalDate joiningDate = new LocalDate(
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "MM")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "dd")));
		    LocalDate currentDate = new LocalDate(
		    		uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "MM")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "dd")));

		    Period period = new Period(joiningDate, currentDate, PeriodType.yearMonthDay());
			
			if(period.getYears()>0){
				sbTimeDuration.append(period.getYears());
			}
			
			if(period.getMonths()>0){
				sbTimeDuration.append("."+period.getMonths());
			}
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sbTimeDuration.toString();
	}
	
	
private Map<String, List<String>> getCandiTotExp(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		Map<String, List<String>> hmTotExp = new HashMap<String, List<String>>();
		try {
			List<String> expList = new ArrayList<String>();
			pst=con.prepareStatement("select emp_id,from_date,to_date from candidate_prev_employment");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				expList =hmTotExp.get(rst.getString("emp_id"));
				if(expList== null)expList = new ArrayList<String>();

				String frmdt = rst.getString("from_date");
				String todt = rst.getString("to_date");
				String candidateExp = "";
				if(frmdt != null && todt != null){
					candidateExp = getTimeDurationBetweenDates(frmdt, DBDATE, todt, DBDATE, uF);
				}
				expList.add(candidateExp);
				hmTotExp.put(rst.getString("emp_id"), expList);
			}
			rst.close();
			pst.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(rst != null) {
				try {
					rst.close();
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
		return hmTotExp;
	}


private Map<String, String> getDesigAttribute(Connection con, UtilityFunctions uF){
	
	PreparedStatement pst = null;
	ResultSet rst = null;
	
	Map<String, String> hmDesigAttrib = new HashMap<String, String>();
	try {
		pst=con.prepareStatement("select * from desig_attribute");
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		while(rst.next()){
			if(rst.getString("_type").equals("1")){
				hmDesigAttrib.put(rst.getString("desig_id")+"_EDUCATION", rst.getString("desig_value"));
			} else if(rst.getString("_type").equals("2")){
				hmDesigAttrib.put(rst.getString("desig_id")+"_TOTEXP", rst.getString("desig_value"));
			} else if(rst.getString("_type").equals("3")){
				hmDesigAttrib.put(rst.getString("desig_id")+"_RELEXP", rst.getString("desig_value"));
			} else if(rst.getString("_type").equals("4")){
				hmDesigAttrib.put(rst.getString("desig_id")+"_EXPWITH_US", rst.getString("desig_value"));
			} else if(rst.getString("_type").equals("5")){
				hmDesigAttrib.put(rst.getString("desig_id")+"_SKILLS", rst.getString("desig_value"));
			} else if(rst.getString("_type").equals("6")){
				hmDesigAttrib.put(rst.getString("desig_id")+"_GENDER", rst.getString("desig_value"));
			}  
		}
		rst.close();
		pst.close();
		
	}catch(Exception e){
		e.printStackTrace();
	}finally {
		if(rst != null) {
			try {
				rst.close();
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
	return hmDesigAttrib;
}


private Map<String, String> getEducationWeightage(Connection con, UtilityFunctions uF){
	
	PreparedStatement pst = null;
	ResultSet rst = null;
	
	Map<String, String> hmEduWeightage = new HashMap<String, String>();
	try {
		pst=con.prepareStatement("select education_name,weightage from educational_details");
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		while(rst.next()){
			hmEduWeightage.put(rst.getString("education_name"), rst.getString("weightage"));
		}
		rst.close();
		pst.close();
		
	}catch(Exception e){
		e.printStackTrace();
	}finally {
		if(rst != null) {
			try {
				rst.close();
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
	return hmEduWeightage;
}
	

	private String calculateCandidateStarRating(Connection con, UtilityFunctions uF, List<String> skillsList, List<String> educationsList, List<String> totExpList, String candiGender) {
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		String strStars=null;
		try {
			
			String desigID = "";
			Map<String, String> hmEduWeightage = getEducationWeightage(con, uF);
			Map<String, String> hmJobDetails = new HashMap<String, String>();
			pst=con.prepareStatement("select * from recruitment_details where recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(recruitId));
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("pst ===> "+pst);
			while(rst.next()){
				hmJobDetails.put("SKILLS", rst.getString("skills"));
				hmJobDetails.put("EDUCATIONS", rst.getString("min_education"));
				hmJobDetails.put("MIN_EXP", rst.getString("min_exp"));
				hmJobDetails.put("MAX_EXP", rst.getString("max_exp"));
				hmJobDetails.put("DESIG_ID", rst.getString("designation_id"));
				desigID = rst.getString("designation_id");
			}
			rst.close();
			pst.close();
//			System.out.println("hmJobDetails ===>> " + hmJobDetails);
			
			String minExp = hmJobDetails.get("MIN_EXP");
			List<String> recSkillsList =  getListData(hmJobDetails.get("SKILLS"));
			List<String> recEduList = getListData(hmJobDetails.get("EDUCATIONS"));
			Map<String, String> hmDesigAttrib = getDesigAttribute(con, uF);
			List<String> desigSkillList = getListData(hmDesigAttrib.get(desigID+"_SKILLS"));
			List<String> desigEduList = getListData(hmDesigAttrib.get(desigID+"_EDUCATION"));
			String desigTotExp = hmDesigAttrib.get(desigID+"_TOTEXP");
			String desigRelExp = hmDesigAttrib.get(desigID+"_RELEXP");
			String desigExpWithus = hmDesigAttrib.get(desigID+"_EXPWITH_US");
			String desigGender = hmDesigAttrib.get(desigID+"_GENDER");
//			System.out.println("desigSkillList ===> "+desigSkillList);
//			System.out.println("desigEduList ===> "+ desigEduList);
//			System.out.println("desigTotExp ===> "+ desigTotExp);
//			System.out.println("desigGender ===> "+ desigGender);
			
			
//			System.out.println("recEduList ===> "+recEduList);
//			System.out.println("recSkillsList ===> "+ recSkillsList);
//			System.out.println("minExp ===> "+ minExp);
			
//			System.out.println("educationsList ===> "+educationsList);
//			System.out.println("skillsList ===> "+ skillsList);
//			System.out.println("totExpList ===> "+ totExpList);
			
			int skillMarks=0, skillCount=0;
			if(recSkillsList != null && !recSkillsList.isEmpty()){
				for (int i = 0; i < recSkillsList.size(); i++) {
					for (int j = 0; skillsList != null && !skillsList.isEmpty() && j < skillsList.size(); j++) {
						if(recSkillsList.get(i).equals(skillsList.get(j))){
							skillMarks = 100;
						}
					}
					skillCount=1;
				}
			}else{
				if(desigSkillList != null && !desigSkillList.isEmpty()){
					for (int i = 0; i < desigSkillList.size(); i++) {
						for (int j = 0; skillsList != null && !skillsList.isEmpty() && j < skillsList.size(); j++) {
							if(desigSkillList.get(i).equals(skillsList.get(j))){
								skillMarks = 100;
							}
						}
						skillCount=1;
					}
				}
			}
			
			int eduMarks=0, eduCount=0;
			if(recEduList != null && !recEduList.isEmpty()){
				for (int i = 0; i < recEduList.size(); i++) {
					for (int j = 0; educationsList != null && !educationsList.isEmpty() && j < educationsList.size(); j++) {
						if(recEduList.get(i).equals(educationsList.get(j)) || 
								uF.parseToInt(hmEduWeightage.get(recEduList.get(i))) >= uF.parseToInt(hmEduWeightage.get(educationsList.get(j)))){
							eduMarks = 100;
						}
					}
					eduCount=1;
				}
			}else{
				if(desigEduList != null && !desigEduList.isEmpty()){
					for (int i = 0; i < desigEduList.size(); i++) {
						for (int j = 0; educationsList != null && !educationsList.isEmpty() && j < educationsList.size(); j++) {
							if(desigEduList.get(i).equals(educationsList.get(j)) || 
									uF.parseToInt(hmEduWeightage.get(desigEduList.get(i))) >= uF.parseToInt(hmEduWeightage.get(educationsList.get(j)))){
								eduMarks = 100;
							}
						}
						eduCount=1;
					}
				}
			}
			
			
			int expMarks=0, expCount=0;
			if(minExp != null && !minExp.equals("")){
				double sumcandiExp=0;
					for (int j = 0; totExpList != null && !totExpList.isEmpty() && j < totExpList.size(); j++) {
//						System.out.println("totExpList "+j+" == "+totExpList.get(j));
						sumcandiExp += uF.parseToDouble(totExpList.get(j));
					}
//					System.out.println("sumcandiExp == "+sumcandiExp);
					
					if(uF.parseToDouble(minExp)>0 && uF.parseToDouble(minExp) <= sumcandiExp){
						expMarks = 100;
					}
					expCount=1;
			}else{
				if(desigTotExp != null && !desigTotExp.equals("")){
					double sumcandiExp=0;
						for (int j = 0; totExpList != null && !totExpList.isEmpty() && j < totExpList.size(); j++) {
							sumcandiExp += uF.parseToDouble(totExpList.get(j));
						}
						if(uF.parseToDouble(desigTotExp)>0 && uF.parseToDouble(desigTotExp) <= sumcandiExp){
							expMarks = 100;
						}
						expCount=1;
				}
			}
//			System.out.println("desigGender == "+desigGender);
//			System.out.println("candiGender == "+candiGender);
			int genderMarks=0, genderCount=0;
			if(desigGender != null && !desigGender.equals("")){
				if(desigGender.equals(candiGender)){
					genderMarks = 100;
				}
				genderCount=1;
			}
			
//			System.out.println("skillMarks ===>> " + skillMarks +" -- eduMarks ===>> " + eduMarks + " -- expMarks ===>> " + expMarks +" -- genderMarks ===>> " + genderMarks);
			int allMarks = skillMarks + eduMarks + expMarks + genderMarks;
			int allCount = skillCount + eduCount + expCount + genderCount;
		
			int avgMarks = 0;
			if(allCount > 0){
				avgMarks = allMarks / allCount;
			}
//			System.out.println("avgMarks == "+avgMarks);
			double starrts = uF.parseToDouble(""+avgMarks) / 20;
//			System.out.println("starrts == "+starrts);
			int intstars = (int) starrts;
//			System.out.println("intstars == "+intstars);
			if(starrts>uF.parseToDouble(""+intstars)){
				strStars = uF.formatIntoOneDecimalWithOutComma(starrts);
			}else{
				strStars = intstars+"";
			}

		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(rst != null) {
				try {
					rst.close();
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
		return strStars;
	}


	public List<String> getListData(String strData){
		List<String> dataList = new ArrayList<String>();
		if(strData != null && !strData.equals("")){
			dataList = Arrays.asList(strData.split(","));
		}
		return dataList;
	}

	
	
	public void sendMail() {
//System.out.println("sendMail()>>>>>>");
		Connection con = null;
		ResultSet rst = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmCandiInfo = CF.getCandiInfoMap(con, false);

			
//			StringBuilder sbCandiSalTable = getCandiSalaryDetails(con);	
//			if(sbCandiSalTable == null) sbCandiSalTable = new StringBuilder();
			
			Map<String, String> hmCandiInner = hmCandiInfo.get(getCandID());
			
			pst = con.prepareStatement("select e.candi_application_deatils_id,e.candidate_id,r.org_id,d.designation_code,d.designation_name," +
				" r.reporting_to_person_ids,r.job_title from recruitment_details r, designation_details d,candidate_application_details e " +
				" where r.recruitment_id = e.recruitment_id and r.designation_id=d.designation_id and candidate_id=?");
			pst.setInt(1, uF.parseToInt(getCandID()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, String> hmCandiDesig = new HashMap<String, String>();
			Map<String, String> hmCandiOrg = new HashMap<String, String>();
			Map<String, String> hmCandiJobTitle = new HashMap<String, String>();//Created By Dattatray Date : 01-11-21
			String reportingToEmpIds = null;
			while(rst.next()){				
				hmCandiDesig.put(rst.getString("candidate_id"), rst.getString("designation_name"));
				hmCandiOrg.put(rst.getString("candidate_id"), rst.getString("org_id"));
				hmCandiJobTitle.put(rst.getString("candidate_id"), rst.getString("job_title"));//Created By Dattatray Date : 01-11-21
				
				setCandiApplicationId(rst.getString("candi_application_deatils_id"));
				reportingToEmpIds = rst.getString("reporting_to_person_ids");
			}
			rst.close();
			pst.close();
			String[] strTmp = reportingToEmpIds.split(",");
			String reportMgrDesig = "";
			for(int i=0; strTmp!=null && i<strTmp.length; i++) {
				if(uF.parseToInt(strTmp[i])>0) {
					reportMgrDesig = CF.getEmpDesigMapByEmpId(con, strTmp[i]);
				}
			}
			
			Map<String, Map<String, String>> hmHeader=new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmFooter=new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select * from document_collateral");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				if(rst.getString("_type").equals("H")){
					Map<String, String> hmInner=new HashMap<String, String>();
					hmInner.put("COLLATERAL_ID", rst.getString("collateral_id"));
					hmInner.put("COLLATERAL_PATH", rst.getString("collateral_image"));
					hmInner.put("COLLATERAL_IMG_ALIGN", rst.getString("image_align"));
					hmInner.put("COLLATERAL_TEXT", uF.showData(rst.getString("collateral_text"),""));
					
					hmHeader.put(rst.getString("collateral_id"), hmInner);
				}else{
					Map<String, String> hmInner=new HashMap<String, String>();
					hmInner.put("COLLATERAL_ID", rst.getString("collateral_id"));
					hmInner.put("COLLATERAL_PATH", rst.getString("collateral_image"));
					hmInner.put("COLLATERAL_IMG_ALIGN", rst.getString("image_align"));
					hmInner.put("COLLATERAL_TEXT", uF.showData(rst.getString("collateral_text"),""));
					
					hmFooter.put(rst.getString("collateral_id"), hmInner);
				}
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select * from document_comm_details where trigger_nodes like '%,"+NODE_CANDIDATE_OFFER_ID+",%' and status=1 and org_id=? order by document_id desc limit 1");
			pst.setInt(1, uF.parseToInt(hmCandiOrg.get(getCandID())));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String strDocumentName = null;
			String strDocumentContent = null;
			String strDocumentHeader = null;
			String strDocumentFooter = null;
			String strHeader = null;
			String strFooter = null;
			String strHeaderImageAlign="";
			String strHeaderCollateralText="";
			String strHeaderTextAlign="";
			String strFooterImageAlign="";
			String strFooterCollateralText="";
			String strFooterTextAlign="";
			
			while (rst.next()) {  
				strDocumentName = rst.getString("document_name");
				strDocumentContent = rst.getString("document_text");
				
				if(rst.getString("collateral_header")!=null && !rst.getString("collateral_header").equals("") && hmHeader.get(rst.getString("collateral_header"))!=null){
					Map<String, String> hmInner=hmHeader.get(rst.getString("collateral_header"));
					strHeader = uF.showData(hmInner.get("COLLATERAL_PATH"),"");
					strHeaderImageAlign=uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"),"");
					strHeaderCollateralText=uF.showData(hmInner.get("COLLATERAL_TEXT"),"");
					strHeaderTextAlign=uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"),"");
				}
				if(rst.getString("collateral_footer")!=null && !rst.getString("collateral_footer").equals("") && hmFooter.get(rst.getString("collateral_footer"))!=null){
					Map<String, String> hmInner=hmFooter.get(rst.getString("collateral_footer"));
					strFooter = uF.showData(hmInner.get("COLLATERAL_PATH"),"");
					strFooterImageAlign=uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"),"");
					strFooterCollateralText=uF.showData(hmInner.get("COLLATERAL_TEXT"),"");
					strFooterTextAlign=uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"),"");
				}
			}
			rst.close();
			pst.close();
			
			if(strDocumentName!=null){
				strDocumentName = strDocumentName.replace(" ", "");
			}
			
			String strDomain = request.getServerName().split("\\.")[0];
			
			CandidateNotifications nF = new CandidateNotifications(N_CANDI_JOINING_OFFER_CTC, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrRecruitmentId(getRecruitId());
			nF.setStrHostAddress(CF.getStrEmailLocalHost()); 
			nF.setStrHostPort(CF.getStrHostPort()); 
//			nF.setStrContextPath(request.getServletPath());
			nF.setStrContextPath(request.getContextPath());
			nF.setStrEmpId(getCandID());
			 
			nF.setStrCandiFname(hmCandiInner.get("FNAME"));
			nF.setStrCandiLname(hmCandiInner.get("LNAME"));
			nF.setStrReportEmpDesignation(reportMgrDesig);
			nF.setStrCandiJoiningDate(hmCandiInner.get("JOINING_DATE"));
			nF.setStrCandidateId(hmCandiInner.get("CANDI_ID"));//Created By Dattatray Date : 05-10-21
			nF.setStrJobTitle(hmCandiJobTitle.get(getCandID()));//Created By Dattatray Date : 01-11-21
			//Started By Dattatray Date:29-09-21
			String name = uF.showData(nF.getStrCandiSalutation(), "")+" "+uF.showData(nF.getStrCandiFname(),"")+" "+uF.showData(nF.getStrCandiLname(),"");
			Map<String, String> hmCandiSalDetails = getCandiSalaryDetails(con, name, nF.getStrRecruitmentDesignation(), nF.getStrRecruitmentWLocation(), nF.getStrCandiJoiningDate(), nF.getStrRecruitmentLevel(), nF.getStrRecruitmentGrade(),nF.getStrLegalEntityName());
			//Ended By Dattatray Date:29-09-21
			nF.setStrOfferedSalaryStructure(hmCandiSalDetails.get("OFFERED_SALARY_STRUCTURE"));
			
			nF.setStrRecruitmentDesignation(hmCandiDesig.get(getCandID()));
			nF.setStrCandiCTC(hmCandiSalDetails.get("CANDI_CTC"));
			nF.setStrCandiCTCInWords(hmCandiSalDetails.get("CANDI_CTC_WORDS"));
			nF.setStrCandiAnnualCTC(hmCandiSalDetails.get("CANDI_ANNUAL_CTC"));
			
			nF.setOfferAcceptData("?candidateID="+getCandID()+"&recruitID="+getRecruitId()+"&candiOfferAccept=yes&updateRemark=Update");
			
			Map<String, String> hmParsedContent = null;

//			Document document = new Document(PageSize.A4);
			Document document = new Document(PageSize.A4,40, 40, 10, 60); 

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			StringBuilder sbHeader = new StringBuilder();
			StringBuilder sbFooter = new StringBuilder();
			String strDocName = null;
			String strDocContent = null;
			if(strDocumentContent!=null){
				
//				hmParsedContent  = nF.parseContent(strDocumentContent, "", "");
				hmParsedContent  = nF.parseContent(strDocumentContent, "", "");
				
				
				strDocName = strDocumentName;
				strDocContent = hmParsedContent.get("MAIL_BODY");
				String strDocument = hmParsedContent.get("MAIL_BODY");
				
				if(strDocument!=null) {
//					strDocument = strDocument.replaceAll("<br/>", "");
					//Satrt Dattatray Date : 31-07-21  
					if (strDocument.contains("<pre style=\"text-align:justify\">") || strDocument.contains("<pre style=\"text-align:justify;\">") || strDocument.contains("<pre style=\"text-align: justify;\">") || strDocument.contains("<pre style=\"text-align: justify\">")) {
//						System.out.println("if");
						if (strDocument.contains("<pre ")) {
							strDocument = strDocument.replaceAll("<pre ", "<p ");
						}
						 if(strDocument.contains("<pre>") ){
							 strDocument = strDocument.replaceAll("<pre>", "<p>");
						 }
						//
						/*if (strDocument.contains("><span")) {
							strDocument = strDocument.replaceAll("><span ", "><p style=\"text-align: justify\"><span ");
						}
						if (strDocument.contains("</span>")) {
							strDocument = strDocument.replaceAll("</span>", "</span></p>");
						}*/

						if (strDocument.contains("</pre>")) {
							strDocument = strDocument.replaceAll("</pre>", "</p>");
						}
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p>", "<br/><p style=\"text-align: justify\">", true, true, "<p>");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<li>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
//						strDocument = replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>", "<p style=\"text-align: justify\">", true, true, "<p style=\"text-align: justify\">");
//						strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
//						strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</p>", true, true, "<p>");
//						strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<span style=\"color:rgb(255, 255, 255)\"><span style=\"font-size:8px\">aaaaa <br/></span>----------------------------------------------------------------------</span><strong>", "SALARY STRUCTURE", true, true, "<span style=\"color:rgb(255, 255, 255)\"><span style=\"font-size:8px\">aaaaa <br/></span>----------------------------------------------------------------------</span><strong>SALARY STRUCTURE");
					}else {
//						System.out.println("Else");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<li>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<p>", true, true, "<p>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", " </li>	", true, true, "</li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<pre>", true, true, "<pre>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<pre>", "<br/>", true, true, "<pre>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<span style=\"color:rgb(255, 255, 255)\"><span style=\"font-size:8px\">aaaaa <br/></span>----------------------------------------------------------------------</span><strong>", "SALARY STRUCTURE", true, true, "<span style=\"color:rgb(255, 255, 255)\"><span style=\"font-size:8px\">aaaaa <br/></span>----------------------------------------------------------------------</span><strong>SALARY STRUCTURE");
					}
					//End Dattatray Date : 31-07-21 
				}
				
				String headerPath="";
				if(strHeader!=null && !strHeader.equals("")){
//					headerPath=CF.getStrDocRetriveLocation()+strHeader;
					if(CF.getStrDocRetriveLocation()==null) { 
						headerPath =  DOCUMENT_LOCATION + strHeader;
					} else { 
						headerPath = CF.getStrDocSaveLocation() +I_COLLATERAL+"/"+I_IMAGE+"/"+strHeader;
					}
				}
				
				
				/*if(headerPath != null && !headerPath.equals("")) {
					//sbHeader.append("<table><tr><td><img height=\"60\" src=\""+strDocumentHeader+"\"></td></tr></table>");
					sbHeader.append("<table style=\"width: 100%;\"><tr>");
					if(strHeaderImageAlign!=null && strHeaderImageAlign.equals("R")) { 
						sbHeader.append("<td width=\"70%\" valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>" +
								"<td align=\"right\">");
						if(headerPath != null && !headerPath.equals("")) {
							sbHeader.append("<img height=\"30\" src=\""+headerPath+"\">");
						}
						sbHeader.append("</td>");						
					
					} else if(strHeaderImageAlign !=null && strHeaderImageAlign.equals("C")) { 
						sbHeader.append("<td colspan=\"2\" align=\"Center\">");
						if(headerPath != null && !headerPath.equals("")) {
							sbHeader.append("<img height=\"30\" src=\""+headerPath+"\"><br/>");
						}
						sbHeader.append(""+strHeaderCollateralText+"</td>");
					} else {
						sbHeader.append("<td>");
						if(headerPath != null && !headerPath.equals("")) {
							sbHeader.append("<img height=\"30\" src=\""+headerPath+"\">");
						}
						sbHeader.append("</td> <td valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>");
					}
					sbHeader.append("</tr></table>");
					
				} else {
					
					sbHeader.append("<table style=\"width: 100%;\"><tr>");
					if(strHeaderTextAlign!=null && strHeaderTextAlign.equals("R")) { 
						sbHeader.append("<td colspan=\"2\" align=\"right\" valign=\"middle\" style=\"padding-right: 50px;\">"+strHeaderCollateralText+"</td>");
						
					} else if(strHeaderTextAlign!=null && strHeaderTextAlign.equals("C")) { 
						sbHeader.append("<td colspan=\"2\" align=\"center\" valign=\"middle\">"+strHeaderCollateralText+"</td>");
					} else { 
						sbHeader.append("<td colspan=\"2\" valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>");
					}
					sbHeader.append("</tr></table>");
				
				}*/
				
				if(headerPath != null && !headerPath.equals("")) {
					sbHeader.append("<table style=\"width: 100%;\"><tr>");
					sbHeader.append("<td>");
					if(strHeader!=null && !strHeader.equals("")) {
						sbHeader.append("<img src=\""+headerPath+"\">");
					}
					sbHeader.append("</td>");	
					sbHeader.append("</tr></table>");
				}
				
				
//				String footerPath="";   
//				if(strFooter!=null && !strFooter.equals("")){
////					footerPath=CF.getStrDocRetriveLocation()+strFooter;
//					if(CF.getStrDocRetriveLocation()==null) { 
//						footerPath =  DOCUMENT_LOCATION + strFooter;
//					} else { 
//						footerPath = CF.getStrDocRetriveLocation() +I_COLLATERAL+"/"+I_IMAGE+"/"+strFooter;
//					}
//				}
//				
//				
//				if(footerPath != null && !footerPath.equals("")) {
//					//sbFooter.append("<table><tr><td><img height=\"60\" src=\""+strDocumentFooter+"\"></td></tr></table>");
//					
//					sbFooter.append("<table><tr>");
//					if(strFooterImageAlign!=null && strFooterImageAlign.equals("R")) { 
//						sbFooter.append("<td width=\"70%\" valign=\"middle\" style=\"padding-left: 50px;\">"+strFooterCollateralText+"</td> <td align=\"right\">");
//						if(footerPath != null && !footerPath.equals("")) {
//							sbFooter.append("<img height=\"60\" src=\""+footerPath+"\">");
//						}
//						sbFooter.append("</td>");						
//					
//					} else if(strFooterImageAlign!=null && strFooterImageAlign.equals("C")) { 
//						sbFooter.append("<td align=\"Center\">");
//						if(footerPath != null && !footerPath.equals("")) {
//							sbFooter.append("<img height=\"60\" src=\""+footerPath+"\"><br/>");
//						}
//						sbFooter.append(""+strFooterCollateralText+"</td>");
//					} else { 
//						sbFooter.append("<td>");
//						if(footerPath != null && !footerPath.equals("")) {
//							sbFooter.append("<img height=\"60\" src=\""+footerPath+"\">");
//						}
//						sbFooter.append("</td> <td valign=\"middle\" style=\"padding-left: 50px;\">"+strFooterCollateralText+"</td>");
//					}
//					sbFooter.append("</tr></table>");
//				} else {
//
//					sbFooter.append("<table><tr>");
//					if(strFooterTextAlign!=null && strFooterTextAlign.equals("R")) { 
//						sbFooter.append("<td colspan=\"2\" align=\"right\" valign=\"middle\" style=\"padding-right: 50px;\">"+strFooterCollateralText+"</td>");
//					
//					} else if(strFooterTextAlign!=null && strFooterTextAlign.equals("C")) { 
//						sbFooter.append("<td colspan=\"2\" align=\"center\" valign=\"middle\">"+strFooterCollateralText+"</td>");
//					} else { 
//						sbFooter.append("<td colspan=\"2\" valign=\"middle\" style=\"padding-right: 50px;\">"+strFooterCollateralText+"</td>");
//					}
//					sbFooter.append("</tr></table>");
//				}
				
				PdfWriter writer = PdfWriter.getInstance(document, buffer);
				HeaderFooterPageEvent event = new HeaderFooterPageEvent(sbHeader.toString(),strFooterCollateralText);
			    writer.setPageEvent(event);
				document.open();
				HTMLWorker hw = new HTMLWorker(document);
//				hw.parse(new StringReader(sbHeader.toString())); 
				hw.parse(new StringReader(strDocument));
//				hw.parse(new StringReader(sbFooter.toString()));
				document.close();  
			}
			 
			/*nF.setStrCandiSalBasic(hmSalaryAmountMap.get("BASIC"));
			nF.setStrCandiSalHRA(hmSalaryAmountMap.get("HRA"));
			nF.setStrCandiSalConvAllow(hmSalaryAmountMap.get("CONVEYANCE_ALLOWANCE"));
			nF.setStrCandiSalOverTime(hmSalaryAmountMap.get("OVER_TIME"));
			nF.setStrCandiSalGratuity(hmSalaryAmountMap.get("GRATUITY"));
			nF.setStrCandiSalBonus(hmSalaryAmountMap.get("BONUS"));
			nF.setStrCandiSalMobExpenses(hmSalaryAmountMap.get("MOBILE_EXPENSES"));
			nF.setStrCandiSalMedicalAllow(hmSalaryAmountMap.get("MEDICAL_ALLOW"));
			nF.setStrCandiSalSpecialAllow(hmSalaryAmountMap.get("SPECIAL_ALLOW"));
			nF.setStrCandiSalArrearsAndOtherAllow(hmSalaryAmountMap.get("ARREARS_AND_OTHER_ALLOW"));
			nF.setStrCandiTotGrossSalary(hmSalaryAmountMap.get("TOT_GROSS_SALARY"));
			
			nF.setStrCandiSalDeductProftax(hmSalaryAmountMap.get("PROFESSIONAL_TAX"));
			nF.setStrCandiSalDeductTDS(hmSalaryAmountMap.get("TDS"));
			nF.setStrCandiSalDeductPFEmpCont(hmSalaryAmountMap.get("EMPLOYEE_EPF"));
			nF.setStrCandiSalDeductPFEmprCont(hmSalaryAmountMap.get("EMPLOYER_EPF"));
			nF.setStrCandiSalDeductESIEmpr(hmSalaryAmountMap.get("EMPLOYER_ESI"));
			nF.setStrCandiSalDeductESIEmp(hmSalaryAmountMap.get("EMPLOYEE_ESI"));
			nF.setStrCandiSalDeductLoan(hmSalaryAmountMap.get("LOAN"));
			nF.setStrCandiTotDeduction(hmSalaryAmountMap.get("TOT_DEDUCTION"));*/
			
			byte[] bytes = buffer.toByteArray();			
			
			if(strDocumentContent!=null) {
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName(strDocumentName+".pdf");
			}
			String strMailSubject = nF.getStrEmailSubject();
			String strMailBody = nF.getStrNewEmailBody();
			nF.sendNotifications();
			
			saveDocumentActivity(con, uF, CF, strDocName, sbHeader.toString(), strDocContent, strFooterCollateralText, strMailSubject, strMailBody);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
private void saveDocumentActivity(Connection con, UtilityFunctions uF,CommonFunctions CF, String strDocumentName, String strDocumentHeader, String strDocumentContent, String strDocumentFooter, String strMailSubject, String strMailBody){
		
		PreparedStatement pst = null;
		
		try {
			pst = con.prepareStatement("insert into document_activities (document_name, document_content, effective_date, entry_date, user_id, candi_id, mail_subject, mail_body, document_header, document_footer) values (?,?,?,?,?,?,?,?,?,?)");
			pst.setString(1, strDocumentName);
			pst.setString(2, strDocumentContent);
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(6, uF.parseToInt(getCandID()));
			pst.setString(7, strMailSubject);
			pst.setString(8, strMailBody);
			pst.setString(9, strDocumentHeader);
			pst.setString(10, strDocumentFooter);
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
	}

//Created by Dattatray Date:29-09-21 Note : Method parameter added
public Map<String, String> getCandiSalaryDetails(Connection con,String name,String designation,String wlocation,String joingDate,String level,String grade,String orgName) {
	
	PreparedStatement pst=null;
	ResultSet rs = null;
	UtilityFunctions uF= new UtilityFunctions();
	Map<String, String> hmCandiSalDetails = new HashMap<String, String>();
	
	try {
		
		String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
		String strFinancialYearStart = strFinancialYearDates[0];
		String strFinancialYearEnd = strFinancialYearDates[1];
		
		pst = con.prepareStatement("select level_id from candidate_application_details join recruitment_details using (recruitment_id) where candidate_id= ? and recruitment_id = ?");
//		pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
		pst.setInt(1, uF.parseToInt(getCandID()));
		pst.setInt(2, uF.parseToInt(getRecruitId()));
		rs = pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		int nLevelId = 0;
		while(rs.next()){
			nLevelId = rs.getInt("level_id");
		}
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("select state_id,rd.org_id,ismetro from state sd, work_location_info wd, recruitment_details rd " +
				"where rd.wlocation=wd.wlocation_id and wd.wlocation_state_id=sd.state_id and rd.recruitment_id=?");
		pst.setInt(1, uF.parseToInt(getRecruitId()));
		rs = pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		String strStateId = null;
		int nOrgId = 0;
		boolean isMetro = false;
		while(rs.next()){
			strStateId = rs.getString("state_id");
			nOrgId = rs.getInt("org_id");
			isMetro = uF.parseToBoolean(rs.getString("ismetro"));
		}
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("select * from candidate_personal_details where emp_per_id=?");
//		pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
		pst.setInt(1, uF.parseToInt(getCandID()));
		rs = pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		String strGender = null;
		double dblCandAge = 0.0d;
		while(rs.next()){
			strGender = rs.getString("emp_gender");
			String strDays = uF.dateDifference(rs.getString("emp_date_of_birth"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE);
			dblCandAge = uF.parseToDouble(strDays) / 365;
		}
		rs.close();
		pst.close();
		
		String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
		String[] strPayCycleDates = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, ""+nOrgId);
		String strD1 = strPayCycleDates[0];
		String strD2 = strPayCycleDates[1];
		String strPC = strPayCycleDates[2];
		
		int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
		
		pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		rs = pst.executeQuery();
//		System.out.println(" pst==>"+pst);
		Map hmHRAExemption = new HashMap();
		while(rs.next()){
			hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
			hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
			hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
			hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
		}
		rs.close();
		pst.close();
		
		Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
		pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		rs = pst.executeQuery();
		while(rs.next()){
			hmOtherTaxDetails.put(rs.getString("state_id")+"_SERVICE_TAX", rs.getString("service_tax"));
			hmOtherTaxDetails.put(rs.getString("state_id")+"_EDU_TAX", rs.getString("education_tax"));
			hmOtherTaxDetails.put(rs.getString("state_id")+"_STD_TAX", rs.getString("standard_tax"));
			hmOtherTaxDetails.put(rs.getString("state_id")+"_FLAT_TDS", rs.getString("flat_tds"));
			
			hmOtherTaxDetails.put(rs.getString("state_id")+"_MAX_TAX_INCOME", rs.getString("max_net_tax_income"));
			hmOtherTaxDetails.put(rs.getString("state_id")+"_REBATE_AMOUNT", rs.getString("rebate_amt"));
			hmOtherTaxDetails.put(rs.getString("state_id")+"_SWACHHA_BHARAT_CESS", rs.getString("swachha_bharat_cess"));
			hmOtherTaxDetails.put(rs.getString("state_id")+"_KRISHI_KALYAN_CESS", rs.getString("krishi_kalyan_cess"));
			
			hmOtherTaxDetails.put(rs.getString("state_id")+"_CGST", rs.getString("cgst"));
			hmOtherTaxDetails.put(rs.getString("state_id")+"_SGST", rs.getString("sgst"));
			
//			dblInvestmentExemption = 100000;
		}
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and section_id=3 order by section_code");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		rs = pst.executeQuery();
//		System.out.println(" pst==>"+pst);
		double dblInvestmentExemption = 0.0d;
		if (rs.next()) {
			dblInvestmentExemption = uF.parseToDouble(rs.getString("section_exemption_limit"));
		}
		rs.close();
		pst.close();
		
		ApprovePayroll objAP = new ApprovePayroll();
		objAP.CF = CF;
		objAP.session = session;
		objAP.request = request; 
		
//		Map<String, String> hmEmpExemptionsMap = objAP.getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, dblInvestmentExemption);
//		Map<String, String> hmEmpHomeLoanMap = objAP.getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd);
		Map<String,String> hmFixedExemptions = objAP.getFixedExemption(con, uF, strFinancialYearStart, strFinancialYearEnd);
//		Map<String, String> hmEmpRentPaidMap = objAP.getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
		
		Map<String, String> hmSalaryDetails = new HashMap<String, String>();
		pst = con.prepareStatement("select * from salary_details where salary_head_id not in ("+GROSS+","+CTC+") and level_id=? and (is_delete is null or is_delete=false) order by earning_deduction desc, salary_head_id, weight");
		pst.setInt(1, nLevelId);
		rs = pst.executeQuery();  
		while(rs.next()){
			hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
		}
		rs.close();
		pst.close();
		
//		pst = con.prepareStatement("select * from annual_variable_details where level_id=? and org_id=? " +
//				"and financial_year_start=? and financial_year_end=? and salary_head_id in (select salary_head_id from salary_details where is_annual_variable=true and (is_delete is null or is_delete = false))");
//		pst.setInt(1, nLevelId);
//		pst.setInt(2, nOrgId);
//		pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//		pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//		rs = pst.executeQuery();
//		Map<String, String> hmAnnualVariableAmt = new HashMap<String, String>();
//		while(rs.next()){
//			hmAnnualVariableAmt.put(rs.getString("salary_head_id"), rs.getString("variable_amount"));
//		}
//		rs.close();
//		pst.close();
//		request.setAttribute("hmAnnualVariableAmt", hmAnnualVariableAmt);
		
		//Started By Dattatray Date:11-10-21
		pst = con.prepareStatement("select * from annual_variable_details where level_id=? and org_id=? " +
				"and financial_year_start=? and financial_year_end=? and emp_id=? and salary_head_id in (select salary_head_id from salary_details " +
				"where is_contribution=true and (is_delete is null or is_delete = false))");
		pst.setInt(1, nLevelId);
		pst.setInt(2, nOrgId);
		pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(5, uF.parseToInt(getCandID()));
//		System.out.println("pst ===>> " + pst);
		rs = pst.executeQuery();
		Map<String, String> hmContributionSalHeadAmt = new HashMap<String, String>();
		while(rs.next()){
			hmContributionSalHeadAmt.put(rs.getString("salary_head_id"), rs.getString("variable_amount"));
		}
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("SELECT * FROM gratuity_details where org_id=? and effective_date<=? order by effective_date desc limit 1");
		pst.setInt(1, nOrgId);
		pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
		rs = pst.executeQuery();
		Map<String, String> hmGratuityPolicy = new HashMap<String,String>();
		while(rs.next()){
			hmGratuityPolicy.put("SALARY_HEAD", rs.getString("salary_head_id"));
			hmGratuityPolicy.put("EFFECTIVE_DATE", uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
			hmGratuityPolicy.put("CALCULATE_PERCENT", rs.getString("calculate_percent"));
		}
		rs.close();
		pst.close();
		
		 double gratuitySalHeadAmt=0.0d;
         List<String> alGratuitySlaHeadId = new ArrayList<String>();
         if(hmGratuityPolicy !=null && hmGratuityPolicy.get("SALARY_HEAD")!=null) {
       	  alGratuitySlaHeadId = Arrays.asList(hmGratuityPolicy.get("SALARY_HEAD").split(","));
         }
       //Ended By Dattatray Date:11-10-21
         
		pst = con.prepareStatement("SELECT * FROM (select csd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id " +
				"FROM candidate_salary_details WHERE emp_id=? AND effective_date = (SELECT MAX(effective_date) FROM candidate_salary_details " +
				"WHERE emp_id=?) group by salary_head_id) a, candidate_salary_details csd WHERE a.emp_salary_id=csd.emp_salary_id " +
				"and a.salary_head_id=csd.salary_head_id and emp_id=? AND effective_date= (SELECT MAX(effective_date) " +
				"FROM candidate_salary_details WHERE emp_id=?)) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
				"WHERE level_id=? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) " +
				"order by sd.earning_deduction desc, weight");
		pst.setInt(1, uF.parseToInt(getCandID()));
		pst.setInt(2, uF.parseToInt(getCandID()));
		pst.setInt(3, uF.parseToInt(getCandID()));
		pst.setInt(4, uF.parseToInt(getCandID()));
		pst.setInt(5, nLevelId);
//		System.out.println("pst =====> " + pst); 
		rs = pst.executeQuery();
		List<List<String>> salaryHeadDetailsList = new ArrayList<List<String>>();
		Map<String, String> hmTotal = new HashMap<String, String>();
		double dblGrossTDS = 0.0d;
		boolean isEPF = false;
		boolean isESIC = false;
		boolean isLWF = false;
		double grossAmount = 0.0d;
		double grossYearAmount = 0.0d;
		double deductAmount = 0.0d;
		double deductYearAmount = 0.0d;
		List<List<String>> salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
		List<List<String>> salaryContributionDetailsList = new ArrayList<List<String>>();//Created By Dattatray Date:11-10-21
		while(rs.next()) {
			
			if(uF.parseToInt(rs.getString("salary_head_id")) == CTC){
				continue;
			}
			
			if(!uF.parseToBoolean(rs.getString("isdisplay"))){
				continue;
			}

			if(rs.getString("earning_deduction").equals("E")) {
				//Started By Dattatray Date:11-10-21
				if(uF.parseToBoolean(rs.getString("is_contribution"))) {
					
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("salary_head_name"));
					innerList.add(rs.getString("earning_deduction"));
					
//					System.out.println("uF.parseToDouble(hmContributionSalHeadAmt.get(rs.getString(\"salary_head_id\")) : "+uF.parseToDouble(hmContributionSalHeadAmt.get(rs.getString("salary_head_id"))));
					if(uF.parseToBoolean(rs.getString("isdisplay"))) {
						double dblAmount = uF.parseToDouble(hmContributionSalHeadAmt.get(rs.getString("salary_head_id")));
						double dblYearAmount = dblAmount*12;
						innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
						innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
						innerList.add(rs.getString("salary_head_id"));
						innerList.add(rs.getString("is_contribution"));
					} else {
						innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
						innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
						innerList.add(rs.getString("salary_head_id"));
						innerList.add(rs.getString("is_contribution"));
					}
					salaryContributionDetailsList.add(innerList);
//					System.out.println("salaryContributionDetailsList : "+salaryContributionDetailsList);
					//Ended By Dattatray Date:11-10-21
				} else if(!uF.parseToBoolean(rs.getString("is_variable")) && uF.parseToBoolean(rs.getString("is_annual_variable"))){
					
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("salary_head_name"));
					innerList.add(rs.getString("earning_deduction"));
					
//					System.out.println("salary_head_id ===>> " + rs.getString("salary_head_id") + " -- isdisplay ===>> " + rs.getString("isdisplay"));
					if(uF.parseToBoolean(rs.getString("isdisplay"))){
						double dblAmount = rs.getDouble("amount");
						double dblYearAmount = dblAmount*12;
						innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
						innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
						/*double dblAmount = 0.0d;
						double dblYearAmount = 0.0d;
						innerList.add(""+dblAmount);
						innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));*/
						innerList.add(rs.getString("salary_head_id"));
						innerList.add(rs.getString("is_variable"));
						
						grossAmount += dblAmount;
						grossYearAmount += dblYearAmount;
						
						if(uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						} else if(uF.parseToInt(rs.getString("salary_head_id")) != TRAVEL_REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						} else if(uF.parseToInt(rs.getString("salary_head_id")) != MOBILE_REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						} else if(uF.parseToInt(rs.getString("salary_head_id")) != OTHER_REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						}
						
						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
					} else {
						innerList.add("0.0");
						innerList.add("0.0");
						innerList.add(rs.getString("salary_head_id"));
						innerList.add(rs.getString("is_variable"));
						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
					}
					salaryAnnualVariableDetailsList.add(innerList);
				
				} else {	
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("salary_head_name"));
					innerList.add(rs.getString("earning_deduction"));
					
					if(uF.parseToBoolean(rs.getString("isdisplay"))){
						double dblAmount = 0.0d;
						double dblYearAmount = 0.0d;
						if(uF.parseToBoolean(rs.getString("is_variable")) && uF.parseToBoolean(rs.getString("is_annual_variable"))){
//							dblAmount = rs.getDouble("amount");
//							dblYearAmount = uF.parseToDouble(hmAnnualVariableAmt.get(rs.getString("salary_head_id")));
//							dblYearAmount = dblAmount * 12;
						} else {
							dblAmount = rs.getDouble("amount");
//							if(hmPerkAlignAmount.containsKey(rs.getString("salary_head_id"))){
//								dblAmount = uF.parseToDouble(hmPerkAlignAmount.get(rs.getString("salary_head_id")));
//							}
							dblYearAmount = dblAmount * 12;
						}
						
						innerList.add(""+dblAmount);
						innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
						innerList.add(rs.getString("salary_head_id"));
						innerList.add(rs.getString("is_variable"));
						
						grossAmount += dblAmount;
						grossYearAmount += dblYearAmount;
						
						if(uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						} else if(uF.parseToInt(rs.getString("salary_head_id")) != TRAVEL_REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						} else if(uF.parseToInt(rs.getString("salary_head_id")) != MOBILE_REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						} else if(uF.parseToInt(rs.getString("salary_head_id")) != OTHER_REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						}
						
						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
					} else {
						innerList.add("0.0");
						innerList.add("0.0");
						innerList.add(rs.getString("salary_head_id"));
						innerList.add(rs.getString("is_variable"));
						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
					}
					salaryHeadDetailsList.add(innerList);
				}
			} else if(rs.getString("earning_deduction").equals("D")) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("salary_head_name"));
				innerList.add(rs.getString("earning_deduction"));
				if(uF.parseToBoolean(rs.getString("isdisplay"))){
//						int nPayMonth = uF.parseToInt(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, "MM"));
					switch(rs.getInt("salary_head_id")){
												
						case PROFESSIONAL_TAX :
							  
							double dblAmount = calculateProfessionalTax(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, nPayMonth, strStateId,strGender);
							dblAmount = Math.round(dblAmount);
//							double dblYearAmount =  dblAmount * 12;
							double dblYearAmount =  getAnnualProfessionalTax(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, strStateId, strGender);
							
							deductAmount += dblAmount;
//							deductYearAmount += dblYearAmount > 0.0d ? dblYearAmount + 100 : 0.0d;
							deductYearAmount += dblYearAmount;
							
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount));
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
							
							break;
						
						case EMPLOYEE_EPF :
							isEPF = true;	
							double dblAmount1 = objAP.calculateCandiEEPF(con, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, getCandID(),nLevelId,nOrgId);
							dblAmount1 = Math.round(dblAmount1);
							double dblYearAmount1 = dblAmount1 * 12;
							
							deductAmount += dblAmount1;
							deductYearAmount += dblYearAmount1;
							
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount1));
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount1));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount1));
							
							break;
						
//						case EMPLOYER_EPF :
//							
//							double dblAmount2 = objAP.calculateERPF(con, CF, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, getCandID(), null, null, false, null);
//							dblAmount2 = Math.round(dblAmount2);
//							double dblYearAmount2 = dblAmount2 * 12;
//							
//							deductAmount += dblAmount2;
//							deductYearAmount += dblYearAmount2;
//							
//							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount2));
//							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount2));
//							innerList.add(rs.getString("salary_head_id"));
//							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount2));
//							
//							break;  
						
//						case EMPLOYER_ESI :
//							
//							double dblAmount3 = objAP.calculateERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId,getCandID());
//							dblAmount3 = Math.round(dblAmount3);
//							double dblYearAmount3 = dblAmount3 * 12;
//							
//							deductAmount += dblAmount3;
//							deductYearAmount += dblYearAmount3;
//							
//							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount3));
//							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount3));
//							innerList.add(rs.getString("salary_head_id"));
//							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount3));
//							
//							break;
						
						case EMPLOYEE_ESI :
							isESIC = true;
							double dblAmount4 = objAP.calculateCandiEEESI(con, uF, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, getCandID(),nLevelId,nOrgId);
							dblAmount4 = Math.ceil(dblAmount4);
							double dblYearAmount4 = dblAmount4 * 12;
							
							deductAmount += dblAmount4;
							deductYearAmount += dblYearAmount4;
//							System.out.println("dblAmount4====>"+dblAmount4);
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount4));
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount4));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount4));
							
							break;
						
//						case EMPLOYER_LWF :
//							
//							double dblAmount5 = objAP.calculateERLWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, nPayMonth);
//							dblAmount5 = Math.round(dblAmount5);
//							double dblYearAmount5 = dblAmount5 * 12;
//							
//							deductAmount += dblAmount5;
//							deductYearAmount += dblYearAmount5;
//							
//							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount5));
//							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount5));
//							innerList.add(rs.getString("salary_head_id"));
//							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount5));
//							
//							break;
						
						case EMPLOYEE_LWF :
							isLWF = true;
							double dblAmount6 = objAP.calculateEELWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, getCandID(), nPayMonth, ""+nOrgId);
							dblAmount6 = Math.round(dblAmount6);
							double dblYearAmount6 = dblAmount6 * 12;
							
							deductAmount += dblAmount6;
							deductYearAmount += dblYearAmount6;
							
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount6));
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount6));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount6));
							
							break;
						
						case TDS :
							
							double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
							double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
							double dblHRA = uF.parseToDouble(hmTotal.get(HRA+""));
							
							String[] hraSalaryHeads = null;
							if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null){
								hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
							}
							
							double dblHraSalHeadsAmount = 0;
							for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++){
								dblHraSalHeadsAmount += uF.parseToDouble((String)hmTotal.get(hraSalaryHeads[i]));
							}
							
							
							double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_EDU_TAX"));
							double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_STD_TAX"));
							double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_FLAT_TDS"));
							 
							double dblAmount7 = objAP.calculateCandidateTDS(con, uF,strD2,strD1, dblGrossTDS, dblCess1, dblCess2, dblFlatTDS, dblInvestmentExemption, dblHRA, dblHraSalHeadsAmount,
									nPayMonth,strD1, strFinancialYearStart, strFinancialYearEnd, uF.parseToInt(getCandID()), strGender,  dblCandAge, strStateId,
									hmFixedExemptions, isMetro, hmTotal, hmSalaryDetails, nLevelId, CF,hmOtherTaxDetails,nOrgId);
							dblAmount7 = Math.round(dblAmount7);
							double dblYearAmount7 = dblAmount7 * 12;
							
							deductAmount += dblAmount7;
							deductYearAmount += dblYearAmount7;
							
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount7));
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount7));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount7));
							
							break;
						
						default:
							
							double dblAmount9 = uF.parseToDouble(rs.getString("amount"));
							double dblYearAmount9 = dblAmount9 * 12;
							
							deductAmount += dblAmount9;
							deductYearAmount += dblYearAmount9;
							
							innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblAmount9));
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount9));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount9));
							
							break;
					}
				}  else {
					innerList.add("0.0");
					innerList.add("0.0");
					innerList.add(rs.getString("salary_head_id"));
					innerList.add(rs.getString("is_variable"));
					hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
				}
				
				salaryHeadDetailsList.add(innerList);
			}
			
		}
		rs.close();
		pst.close();
		
		/**
		 * Employer Contribution
		 * */ 
		Map<String,String> hmContribution = new HashMap<String, String>();
		if(isEPF){
			double dblAmount = objAP.calculateCandiERPF(con,CF, null, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, getCandID(), nLevelId, nOrgId);
			dblAmount = Math.round(dblAmount);
			double dblYearAmount = dblAmount * 12;
			hmContribution.put("EPF_MONTHLY", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
			hmContribution.put("EPF_ANNUALY", uF.formatIntoTwoDecimalWithOutComma(dblYearAmount));
		}
		if(isESIC){
			double dblAmount = objAP.calculateCandiERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId,getCandID(), nOrgId, nLevelId);
			dblAmount = Math.ceil(dblAmount);
			double dblYearAmount = dblAmount * 12;
			dblYearAmount = Math.ceil(dblYearAmount);
			
			hmContribution.put("ESI_MONTHLY", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
			hmContribution.put("ESI_ANNUALY", uF.formatIntoTwoDecimalWithOutComma(dblYearAmount));
		}
		if(isLWF){
			double dblAmount = objAP.calculateERLWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, nPayMonth, ""+nOrgId);
			dblAmount = Math.round(dblAmount);
			double dblYearAmount = dblAmount * 12;
			hmContribution.put("LWF_MONTHLY", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
			hmContribution.put("LWF_ANNUALY", uF.formatIntoTwoDecimalWithOutComma(dblYearAmount));				
		}
		
		/**
		 * Employer Contribution End
		 * */ 
		
		
		/**
		 * Salary Structure Table
		 * */
		StringBuilder sbCandiSalTable = new StringBuilder();
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
		
//		Map<String, String> hmFeatureUserTypeId1 = CF.getFeatureStatusMap(request);
		//Start Dattatray Date:27-09-21
		Map<String, String> hmFeatureUserTypeId2 = CF.getFeatureStatusMap(con, request);
		Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
		//End Dattatray Date:27-09-21
//		System.out.println("hmFeatureUserTypeId: "+hmFeatureUserTypeId);
//		System.out.println("hmFeatureUserTypeId2 : "+hmFeatureUserTypeId2);
		boolean flagDisableNetTakeHomeSal = uF.parseToBoolean(hmFeatureStatus.get(F_DISABLE_NET_TAKE_HOME_SALARY));
		boolean flagShowMonthlySal = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_MONTHLY_SALARY_IN_OFFER_LETTER));
		
		if(salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && salaryHeadDetailsList.size() > 0) {
			
			String strColspan = "colspan=\"2\"";
			if(flagShowMonthlySal) {
				strColspan = "colspan=\"3\"";
			}
//			System.out.println("11 : "+hmFeatureStatus.get(F_INSTANCE_NAMEWISE_FEATURE));
//			System.out.println("22 : "+hmFeatureUserTypeId.get(F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS"));
//			System.out.println("33 : "+hmFeatureUserTypeId.get(F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS").contains(QULOI));
			//Start Dattatray Date:27-09-21
			if(uF.parseToBoolean(hmFeatureStatus.get(F_INSTANCE_NAMEWISE_FEATURE)) && hmFeatureUserTypeId.get(F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS")!=null && hmFeatureUserTypeId.get(F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS").contains(QULOI)) {
				CandidateNotifications nF = new CandidateNotifications(N_CANDI_JOINING_OFFER_CTC, CF);
				nF.getStrCandiFname();
				// TODO test
				sbCandiSalTable.append("<table border=\"0.2\" width=\"100%\">");//Created by Dattatray Date:21-09-21
				sbCandiSalTable.append("<tr bgcolor=\"#dae3f3\">");
				//Started By Dattatray Date:29-09-21
				sbCandiSalTable.append("<td align=\"left\" style=\"font-size:10px;\"><b>"+uF.showData(name, "")+"<br/> "+uF.showData(designation, "")+", "+uF.showData(wlocation, "")+", "+uF.showData(orgName, "")+"</b></td>");
				sbCandiSalTable.append("<td align=\"left\" style=\"font-size:10px;\" colspan=\"2\"><b>Date of Joining: "+uF.showData(joingDate, "-")+"<br /> Band & Grade: "+uF.showData(level, "")+" - "+uF.showData(grade, "")+"</b></td>");
				//Ended By Dattatray Date:29-09-21
				sbCandiSalTable.append("</tr>");
				sbCandiSalTable.append("<tr bgcolor=\"#dae3f3\">");
				sbCandiSalTable.append("<td colspan=\"1\"></td>");
				if(flagShowMonthlySal) {
					sbCandiSalTable.append("<td align=\"right\" style=\"font-size:10px;\"><b>Monthly</b></td>");
				}
				sbCandiSalTable.append("<td align=\"right\" style=\"font-size:10px;\"><b>Annual</b></td>");
				sbCandiSalTable.append("</tr>");
//				sbCandiSalTable.append("</table>");
				grossAmount = 0.0d;
				grossYearAmount = 0.0d;
				double netTakeHome = 0.0d;
				double netTakeHomeAnnual = 0.0d;
				boolean veriableFlag = false;
				for (int i = 0; i < salaryHeadDetailsList.size(); i++) {
					List<String> innerList = salaryHeadDetailsList.get(i);
//					System.out.println("innerList 12: "+innerList);
					if (innerList.get(1).equals("E") && !uF.parseToBoolean(innerList.get(5))) {
						double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
						double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
						grossAmount += dblEarnMonth;
						grossYearAmount += dblEarnAnnual;

						netTakeHome += dblEarnMonth;
						netTakeHomeAnnual += dblEarnAnnual;
						
						//Started By Dattatray Date:11-10-21
						if(alGratuitySlaHeadId.contains(innerList.get(4))) {
							gratuitySalHeadAmt += dblEarnMonth;
						}//Ended By Dattatray Date:11-10-21
						
						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>" + uF.showData(innerList.get(0), "-") + "</td>");
						if(flagShowMonthlySal) {
							sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEarnMonth) + "</td>");
						}
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEarnAnnual) + "</td>");
						sbCandiSalTable.append("</tr>");
					} else if (innerList.get(1).equals("E")) {
						veriableFlag = true;
					}
				}
				
//				System.out.println("grossAmount 1 ===>> " + grossAmount);
				sbCandiSalTable.append("<tr bgcolor=\"#dae3f3\">");
				sbCandiSalTable.append("<td align=\"right\"><strong>Total Monthly Direct</strong></td>");
				if(flagShowMonthlySal) {
					sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\"><strong>" + uF.formatIntoTwoDecimal(grossAmount) + "</strong></td>");
				}
				sbCandiSalTable.append("<td width=\"30%\" align=\"right\"><strong>" + uF.formatIntoTwoDecimal(grossYearAmount) + "</strong></td>");
				sbCandiSalTable.append("</tr>");
				
//				if(veriableFlag) {
//					sbCandiSalTable.append("<tr>");
//					sbCandiSalTable.append("<td "+strColspan+"><b>Variable Component- B</b></td>");
//					sbCandiSalTable.append("</tr>");
//				}
				
//				for (int i = 0; i < salaryHeadDetailsList.size(); i++) {
//					List<String> innerList = salaryHeadDetailsList.get(i);
//					if (innerList.get(1).equals("E") && uF.parseToBoolean(innerList.get(5))) {
//						double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
//						double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
//						grossAmount += dblEarnMonth;
//						grossYearAmount += dblEarnAnnual;
//
//						netTakeHome += dblEarnMonth;
//						netTakeHomeAnnual += dblEarnAnnual;
//
//						sbCandiSalTable.append("<tr>");
//						sbCandiSalTable.append("<td>" + uF.showData(innerList.get(0), "-") + "</td>");
//						if(flagShowMonthlySal) {
//							sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEarnMonth) + "</td>");
//						}
//						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEarnAnnual) + "</td>");
//						sbCandiSalTable.append("</tr>");
//					}
//				}

				

//				hmContribution = (Map<String, String>) request.getAttribute("hmContribution");
//				if (hmContribution == null)
//					hmContribution = new HashMap<String, String>();
				double dblMonthContri = 0.0d;
				double dblAnnualContri = 0.0d;
//				boolean isEPF = uF.parseToBoolean((String) request.getAttribute("isEPF"));
//				boolean isESIC = uF.parseToBoolean((String) request.getAttribute("isESIC"));
//				boolean isLWF = uF.parseToBoolean((String) request.getAttribute("isLWF"));

				if (isEPF || isESIC || isLWF) {
					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td "+strColspan+" nowrap=\"nowrap\" align=\"center\"><b>CONTRIBUTION DETAILS</b></td>");
					sbCandiSalTable.append("</tr>");

					if (isEPF) {
						double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
						double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
						dblMonthContri += dblEPFMonth;
						dblAnnualContri += dblEPFAnnual;

						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>Employer PF</td>");
						if(flagShowMonthlySal) {
							sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEPFMonth) + "</td>");
						}
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEPFAnnual) + "</td>");
						sbCandiSalTable.append("</tr>");
					}
					if (isESIC) {
						double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
						double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
						dblMonthContri += dblESIMonth;
						dblAnnualContri += dblESIAnnual;

						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>Employer ESI</td>");
						if(flagShowMonthlySal) {
							sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblESIMonth) + "</td>");
						}
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblESIAnnual) + "</td>");
						sbCandiSalTable.append("</tr>");
					}
					if (isLWF) {
						double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
						double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
						dblMonthContri += dblLWFMonth;
						dblAnnualContri += dblLWFAnnual;

						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>Employer LWF</td>");
						if(flagShowMonthlySal) {
							sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblLWFMonth) + "</td>");
						}
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblLWFAnnual) + "</td>");
						sbCandiSalTable.append("</tr>");
					}
					
//					Started By Dattatray Date:11-10-21
					for (int i = 0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i < salaryHeadDetailsList.size(); i++) {
						List<String> innerList = salaryHeadDetailsList.get(i);
						if (innerList.get(1).equals("D")) {
							double dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
							if(alGratuitySlaHeadId.contains(innerList.get(4))) {
								gratuitySalHeadAmt += dblDeductMonth;
							}
						}
					}
					
					for (int i = 0; i < salaryContributionDetailsList.size(); i++) {
						List<String> innerList = salaryContributionDetailsList.get(i);
							double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
							double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));

							if(innerList.get(4).equals(IConstants.GRATUITY+"")) {
								dblEarnMonth = (gratuitySalHeadAmt * uF.parseToDouble(hmGratuityPolicy.get("CALCULATE_PERCENT"))) / 100;
								String strEarnMonth = uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblEarnMonth);
								dblEarnMonth = uF.parseToDouble(strEarnMonth);
								dblEarnAnnual = dblEarnMonth * 12;
							}
							
							sbCandiSalTable.append("<tr>");
							sbCandiSalTable.append("<td>" + uF.showData(innerList.get(0), "-") + "</td>");
							if(flagShowMonthlySal) {
								sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEarnMonth) + "</td>");
							}
							sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEarnAnnual) + "</td>");
							sbCandiSalTable.append("</tr>");
							
							dblMonthContri += dblEarnMonth;
							dblAnnualContri += dblEarnAnnual;
					}

//					Ended By Dattatray Date:11-10-21
					sbCandiSalTable.append("<tr bgcolor=\"#dae3f3\">");
					sbCandiSalTable.append("<td align=\"right\"><strong>Total Monthly Indirect</strong></td>");
					if(flagShowMonthlySal) {
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\"><strong>" + uF.formatIntoTwoDecimal(dblMonthContri) + "</strong></td>");
					}
					sbCandiSalTable.append("<td width=\"30%\" align=\"right\"><strong>" + uF.formatIntoTwoDecimal(dblAnnualContri) + "</strong></td>");

					sbCandiSalTable.append("</tr>");
					
				}
				double dblCTCMonthly = grossAmount + dblMonthContri;
				double dblCTCAnnualy = grossYearAmount + dblAnnualContri;

//				System.out.println("dblCTCMonthly 2 ===>> " + dblCTCMonthly);
				sbCandiSalTable.append("<tr bgcolor=\"#dae3f3\">");
				sbCandiSalTable.append("<td align=\"right\"><strong>Total CTC (Direct & Indirect)</strong></td>");
				if(flagShowMonthlySal) {
					sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\"><strong>" + uF.formatIntoTwoDecimal(dblCTCMonthly) + "</strong></td>");
				}
				sbCandiSalTable.append("<td width=\"30%\" align=\"right\"><strong>" + uF.formatIntoTwoDecimal(dblCTCAnnualy) + "</strong></td>");
				sbCandiSalTable.append("</tr>");
				
//				sbCandiSalTable.append("<tr  bgcolor=\"#dae3f3\">");
//				if(flagShowMonthlySal) {
//					sbCandiSalTable.append("<td align=\"right\"><strong>Annual CTC</strong></td>");
//					sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\"><strong>" + uF.formatIntoTwoDecimal(dblCTCMonthly) + "</strong></td>");
//				} else {
//					sbCandiSalTable.append("<td><strong>Cost To Company (Annually):</strong></td>");
//				}
//				sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\"> <strong>" + uF.formatIntoTwoDecimal(dblCTCAnnualy) + "</strong></td>");
//				sbCandiSalTable.append("</tr>");
//				System.out.println("ex : "+salaryAnnualVariableDetailsList);
//				System.out.println("ex1 : "+salaryHeadDetailsList);
				int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
				int n = salaryHeadDetailsList.size();
//				if (nAnnualVariSize > 0) {

					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td "+strColspan+" nowrap=\"nowrap\" align=\"center\"><b>OTHER BENEFITS</b></td>");
					sbCandiSalTable.append("</tr>");

					double grossAnnualAmount = 0.0d;
					double grossAnnualYearAmount = 0.0d;
					
					if (nAnnualVariSize > 0) {
					for (int i = 0; i < nAnnualVariSize; i++) {
						List<String> innerList = salaryAnnualVariableDetailsList.get(i);
						double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
						double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
						grossAnnualAmount += dblEarnMonth;
						grossAnnualYearAmount += dblEarnAnnual;

						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>" + uF.showData(innerList.get(0), "-") + "</td>");
						if(flagShowMonthlySal) {
							sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(grossAnnualAmount) + "</td>");
						}
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(grossAnnualYearAmount) + "</td>");
						sbCandiSalTable.append("</tr>");
					}
					}
					dblCTCMonthly += grossAnnualAmount;
					dblCTCAnnualy += grossAnnualYearAmount;
//					System.out.println("dblCTCMonthly 3 ===>> " + dblCTCMonthly);
					double totalOtherAmount = 0.0d;
					double totalOtherAnnualAmount = 0.0d;
					if (n > 0) {
						
					for (int i = 0; i < salaryHeadDetailsList.size(); i++) {
//						System.out.println("salaryHeadDetailsList : "+salaryHeadDetailsList);
						List<String> innerList = salaryHeadDetailsList.get(i);
//						System.out.println("innerList : "+innerList);
						if (innerList.get(1).equals("E") && uF.parseToBoolean(innerList.get(5))) {
							double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
							double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
							grossAmount += dblEarnMonth;
							grossYearAmount += dblEarnAnnual;

							totalOtherAmount += dblEarnMonth;
							totalOtherAnnualAmount += dblEarnAnnual;
//							System.out.println("innerList11 : "+uF.showData(innerList.get(0), "-"));
							sbCandiSalTable.append("<tr>");
							sbCandiSalTable.append("<td>" + uF.showData(innerList.get(0), "-") + "</td>");
							if(flagShowMonthlySal) {
								sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEarnMonth) + "</td>");
							}
							sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEarnAnnual) + "</td>");
							sbCandiSalTable.append("</tr>");
						}
					}
					}
//					totalOtherAmount = grossAnnualAmount + netTakeHome;
//					totalOtherAnnualAmount = grossAnnualYearAmount + netTakeHomeAnnual;
					if (nAnnualVariSize > 0 || n > 0) {
						sbCandiSalTable.append("<tr  bgcolor=\"#dae3f3\">");
						sbCandiSalTable.append("<td  align=\"right\"><strong>Total CTC Amount </strong></td>");
						if(flagShowMonthlySal) {
							sbCandiSalTable.append("<td width=\"30%\" align=\"right\"><strong>" + uF.formatIntoTwoDecimal(totalOtherAmount+dblCTCMonthly) + "</strong></td>");
						}
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\"><strong>" + uF.formatIntoTwoDecimal(totalOtherAnnualAmount+dblCTCAnnualy) + "</strong></td>");
						sbCandiSalTable.append("</tr>");
					}
					
//				}
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td "+strColspan+" nowrap=\"nowrap\" align=\"center\"><b>DEDUCTION DETAILS</b></td>");
				sbCandiSalTable.append("</tr>");

				deductAmount = 0.0d;
				deductYearAmount = 0.0d;

				for (int i = 0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i < salaryHeadDetailsList.size(); i++) {
					List<String> innerList = salaryHeadDetailsList.get(i);
					if (innerList.get(1).equals("D")) {
						double dblDeductMonth = 0.0d;;
						double dblDeductAnnual = 0.0d;
						if (uF.parseToInt(innerList.get(4)) == EMPLOYEE_ESI) {
							dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
							dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
						} else if (uF.parseToInt(innerList.get(4)) == EMPLOYER_ESI) {
							dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
							dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
						} else {
							dblDeductMonth = Math.round(uF.parseToDouble(innerList.get(2)));
							dblDeductAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
						}

						deductAmount += dblDeductMonth;
						deductYearAmount += dblDeductAnnual;

						netTakeHome -= dblDeductMonth;
						netTakeHomeAnnual -= dblDeductAnnual;

						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>" + uF.showData(innerList.get(0), "-") + "</td>");
						if(flagShowMonthlySal) {
							sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblDeductMonth) + "</td>");
						}
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblDeductAnnual) + "</td>");
						sbCandiSalTable.append("</tr>");
					}
				}

//				sbCandiSalTable.append("<tr bgcolor=\"#dae3f3\">");
//				sbCandiSalTable.append("<td align=\"center\"><strong>Total Deduction</strong></td>");
//				if(flagShowMonthlySal) {
//					sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\"><strong>" + uF.formatIntoTwoDecimal(deductAmount) + "</strong></td>");
//				}
//				sbCandiSalTable.append("<td width=\"30%\" align=\"right\"><strong>" + uF.formatIntoTwoDecimal(deductYearAmount) + "</strong></td>");
//				sbCandiSalTable.append("</tr>");
				sbCandiSalTable.append("</table>");

				sbCandiSalTable.append("<table border=\"0.2\" width=\"100%\" >");//Created by Dattatray Date:21-09-21
				if(!flagDisableNetTakeHomeSal) {
					sbCandiSalTable.append("<tr bgcolor=\"#dae3f3\">");
					if(flagShowMonthlySal) {
						sbCandiSalTable.append("<td align=\"right\"><strong>Net Take Home Salary</strong></td>");
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\"><strong>" + uF.formatIntoTwoDecimal(netTakeHome) + "</strong></td>");
					} else {
				//===start parvez date: 12-08-2022===		
//						sbCandiSalTable.append("<td><strong>Net Take Home Per Month:</strong></td>");
						sbCandiSalTable.append("<td><strong>Net Earning:</strong></td>");
				//===end parvez date: 12-08-2022===		
					}
					sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\"> <strong>" + uF.formatIntoTwoDecimal(netTakeHomeAnnual) + "</strong></td>");
					sbCandiSalTable.append("</tr>");
				}
				
				sbCandiSalTable.append("</table>");
				
				hmCandiSalDetails.put("OFFERED_SALARY_STRUCTURE", sbCandiSalTable.toString());
				hmCandiSalDetails.put("CANDI_CTC", uF.formatIntoTwoDecimal(dblCTCMonthly));
//				System.out.println("dblCTCMonthly ===>> " + dblCTCMonthly + " -- int ===>> "+ (int)dblCTCMonthly);
				hmCandiSalDetails.put("CANDI_CTC_WORDS", uF.digitsToWords((int)dblCTCMonthly));
				hmCandiSalDetails.put("CANDI_ANNUAL_CTC", uF.formatIntoTwoDecimal(dblCTCAnnualy));
				hmCandiSalDetails.put("CANDI_ANNUAL_CTC_WORDS", uF.digitsToWords(uF.parseToInt(dblCTCAnnualy+"")));
			}else {
				sbCandiSalTable.append("<table border=\"0.2\" width=\"100%\">");//Created by Dattatray Date:21-09-21
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td bgcolor=\"#dae3f3\" align=\"center\"><b>Salary Head<br/></b></td>");
				if(flagShowMonthlySal) {
					sbCandiSalTable.append("<td bgcolor=\"#dae3f3\" width=\"30%\" align=\"right\"><b>Monthly</b></td>");
				}
				sbCandiSalTable.append("<td bgcolor=\"#dae3f3\" width=\"30%\" align=\"right\"><b>Annual</b></td>");
				sbCandiSalTable.append("</tr>");
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td "+strColspan+" nowrap=\"nowrap\" align=\"center\"><b>EARNING DETAILS</b></td>");
				sbCandiSalTable.append("</tr>");
				
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td "+strColspan+"><b>Fixed Component- A</b></td>");
				sbCandiSalTable.append("</tr>");
				
				
				
				grossAmount = 0.0d;
				grossYearAmount = 0.0d;
				double netTakeHome = 0.0d;
				double netTakeHomeAnnual = 0.0d;
				boolean veriableFlag = false;
				for (int i = 0; i < salaryHeadDetailsList.size(); i++) {
					List<String> innerList = salaryHeadDetailsList.get(i);
					if (innerList.get(1).equals("E") && !uF.parseToBoolean(innerList.get(5))) {
						double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
						double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
						grossAmount += dblEarnMonth;
						grossYearAmount += dblEarnAnnual;

						netTakeHome += dblEarnMonth;
						netTakeHomeAnnual += dblEarnAnnual;
						
						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>" + uF.showData(innerList.get(0), "-") + "</td>");
						if(flagShowMonthlySal) {
							sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEarnMonth) + "</td>");
						}
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEarnAnnual) + "</td>");
						sbCandiSalTable.append("</tr>");
					} else if (innerList.get(1).equals("E")) {
						veriableFlag = true;
					}
				}
				
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td><strong>Sub Total</strong></td>");
				if(flagShowMonthlySal) {
					sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\"><strong>" + uF.formatIntoTwoDecimal(grossAmount) + "</strong></td>");
				}
				sbCandiSalTable.append("<td width=\"30%\" align=\"right\"><strong>" + uF.formatIntoTwoDecimal(grossYearAmount) + "</strong></td>");
				sbCandiSalTable.append("</tr>");
				
				if(veriableFlag) {
					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td "+strColspan+"><b>Variable Component- B</b></td>");
					sbCandiSalTable.append("</tr>");
				}
				
				for (int i = 0; i < salaryHeadDetailsList.size(); i++) {
					List<String> innerList = salaryHeadDetailsList.get(i);
					if (innerList.get(1).equals("E") && uF.parseToBoolean(innerList.get(5))) {
						double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
						double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
						grossAmount += dblEarnMonth;
						grossYearAmount += dblEarnAnnual;

						netTakeHome += dblEarnMonth;
						netTakeHomeAnnual += dblEarnAnnual;

						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>" + uF.showData(innerList.get(0), "-") + "</td>");
						if(flagShowMonthlySal) {
							sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEarnMonth) + "</td>");
						}
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEarnAnnual) + "</td>");
						sbCandiSalTable.append("</tr>");
					}
				}

				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td><strong>Gross Salary</strong></td>");
				if(flagShowMonthlySal) {
					sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\"><strong>" + uF.formatIntoTwoDecimal(grossAmount) + "</strong></td>");
				}
				sbCandiSalTable.append("<td width=\"30%\" align=\"right\"><strong>" + uF.formatIntoTwoDecimal(grossYearAmount) + "</strong></td>");
				sbCandiSalTable.append("</tr>");

				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td "+strColspan+" nowrap=\"nowrap\" align=\"center\"><b>DEDUCTION DETAILS</b></td>");
				sbCandiSalTable.append("</tr>");

				deductAmount = 0.0d;
				deductYearAmount = 0.0d;

				for (int i = 0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i < salaryHeadDetailsList.size(); i++) {
					List<String> innerList = salaryHeadDetailsList.get(i);
					if (innerList.get(1).equals("D")) {
						double dblDeductMonth = 0.0d;;
						double dblDeductAnnual = 0.0d;
						if (uF.parseToInt(innerList.get(4)) == EMPLOYEE_ESI) {
							dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
							dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
						} else if (uF.parseToInt(innerList.get(4)) == EMPLOYER_ESI) {
							dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
							dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
						} else {
							dblDeductMonth = Math.round(uF.parseToDouble(innerList.get(2)));
							dblDeductAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
						}

						deductAmount += dblDeductMonth;
						deductYearAmount += dblDeductAnnual;

						netTakeHome -= dblDeductMonth;
						netTakeHomeAnnual -= dblDeductAnnual;

						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>" + uF.showData(innerList.get(0), "-") + "</td>");
						if(flagShowMonthlySal) {
							sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblDeductMonth) + "</td>");
						}
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblDeductAnnual) + "</td>");
						sbCandiSalTable.append("</tr>");
					}
				}

				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td align=\"center\"><strong>Total Deduction</strong></td>");
				if(flagShowMonthlySal) {
					sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\"><strong>" + uF.formatIntoTwoDecimal(deductAmount) + "</strong></td>");
				}
				sbCandiSalTable.append("<td width=\"30%\" align=\"right\"><strong>" + uF.formatIntoTwoDecimal(deductYearAmount) + "</strong></td>");
				sbCandiSalTable.append("</tr>");

//				hmContribution = (Map<String, String>) request.getAttribute("hmContribution");
//				if (hmContribution == null)
//					hmContribution = new HashMap<String, String>();
				double dblMonthContri = 0.0d;
				double dblAnnualContri = 0.0d;
//				boolean isEPF = uF.parseToBoolean((String) request.getAttribute("isEPF"));
//				boolean isESIC = uF.parseToBoolean((String) request.getAttribute("isESIC"));
//				boolean isLWF = uF.parseToBoolean((String) request.getAttribute("isLWF"));

				if (isEPF || isESIC || isLWF) {
					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td "+strColspan+" nowrap=\"nowrap\" align=\"center\"><b>CONTRIBUTION DETAILS</b></td>");
					sbCandiSalTable.append("</tr>");

					if (isEPF) {
						double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
						double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
						dblMonthContri += dblEPFMonth;
						dblAnnualContri += dblEPFAnnual;

						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>Employer PF</td>");
						if(flagShowMonthlySal) {
							sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEPFMonth) + "</td>");
						}
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEPFAnnual) + "</td>");
						sbCandiSalTable.append("</tr>");
					}
					if (isESIC) {
						double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
						double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
						dblMonthContri += dblESIMonth;
						dblAnnualContri += dblESIAnnual;

						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>Employer ESI</td>");
						if(flagShowMonthlySal) {
							sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblESIMonth) + "</td>");
						}
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblESIAnnual) + "</td>");
						sbCandiSalTable.append("</tr>");
					}
					if (isLWF) {
						double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
						double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
						dblMonthContri += dblLWFMonth;
						dblAnnualContri += dblLWFAnnual;

						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>Employer LWF</td>");
						if(flagShowMonthlySal) {
							sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblLWFMonth) + "</td>");
						}
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblLWFAnnual) + "</td>");
						sbCandiSalTable.append("</tr>");
					}

					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td align=\"right\"><strong>Contribution Total</strong></td>");
					if(flagShowMonthlySal) {
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\"><strong>" + uF.formatIntoTwoDecimal(dblMonthContri) + "</strong></td>");
					}
					sbCandiSalTable.append("<td width=\"30%\" align=\"right\"><strong>" + uF.formatIntoTwoDecimal(dblAnnualContri) + "</strong></td>");

					sbCandiSalTable.append("</tr>");
				}

				double dblCTCMonthly = grossAmount + dblMonthContri;
				double dblCTCAnnualy = grossYearAmount + dblAnnualContri;

//				List<List<String>> salaryAnnualVariableDetailsList = (List<List<String>>) request.getAttribute("salaryAnnualVariableDetailsList");
//				if (salaryAnnualVariableDetailsList == null)
//					salaryAnnualVariableDetailsList = new ArrayList<List<String>>();

				int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
				if (nAnnualVariSize > 0) {

					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td "+strColspan+" nowrap=\"nowrap\" align=\"center\"><b>ANNUAL EARNING DETAILS</b></td>");
					sbCandiSalTable.append("</tr>");

					double grossAnnualAmount = 0.0d;
					double grossAnnualYearAmount = 0.0d;
					for (int i = 0; i < nAnnualVariSize; i++) {
						List<String> innerList = salaryAnnualVariableDetailsList.get(i);
						double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
						double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
						grossAnnualAmount += dblEarnMonth;
						grossAnnualYearAmount += dblEarnAnnual;

						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>" + uF.showData(innerList.get(0), "-") + "</td>");
						if(flagShowMonthlySal) {
							sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEarnMonth) + "</td>");
						}
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEarnAnnual) + "</td>");
						sbCandiSalTable.append("</tr>");
					}
					dblCTCMonthly += grossAnnualAmount;
					dblCTCAnnualy += grossAnnualYearAmount;

					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td><strong>Total</strong></td>");
					if(flagShowMonthlySal) {
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\"><strong>" + uF.formatIntoTwoDecimal(grossAnnualAmount) + "</strong></td>");
					}
					sbCandiSalTable.append("<td width=\"30%\" align=\"right\"><strong>" + uF.formatIntoTwoDecimal(grossAnnualYearAmount) + "</strong></td>");
					sbCandiSalTable.append("</tr>");
//					sbEmpSalTable.append("</table>");
//					sbEmpSalTable.append("</td>");
//					sbEmpSalTable.append("<td>&nbsp;</td>");
//					sbEmpSalTable.append("</tr>");
				}
				sbCandiSalTable.append("</table>");

				sbCandiSalTable.append("<table border=\"0.2\" width=\"100%\" >");//Created by Dattatray Date:21-09-21
				if(!flagDisableNetTakeHomeSal) {
					sbCandiSalTable.append("<tr>");
					if(flagShowMonthlySal) {
						sbCandiSalTable.append("<td><strong>Net Salary in Hand:</strong></td>");
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\"><strong>" + uF.formatIntoTwoDecimal(netTakeHome) + "</strong></td>");
					} else {
				//===start parvez date: 12-08-2022===	
//						sbCandiSalTable.append("<td><strong>Net Take Home Per Month:</strong></td>");
						sbCandiSalTable.append("<td><strong>Net Earning:</strong></td>");
				//===end parvez date: 12-08-2022===
					}
					sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\"> <strong>" + uF.formatIntoTwoDecimal(netTakeHomeAnnual) + "</strong></td>");
					sbCandiSalTable.append("</tr>");
				}
//				sbEmpSalTable.append("<tr>");
//				sbEmpSalTable.append("<td>Cost To Company (Monthly):</td>");
//				sbEmpSalTable.append("<td valign=\"bottom\"> " + uF.formatIntoTwoDecimal(dblCTCMonthly) + "</td>");
//				sbEmpSalTable.append("</tr>");
//				sbEmpSalTable.append("<tr>");
				
				if(flagShowMonthlySal) {
					sbCandiSalTable.append("<td><strong>CTC (Gross Salary+ Employer’s Share):</strong></td>");
					sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\"><strong>" + uF.formatIntoTwoDecimal(dblCTCMonthly) + "</strong></td>");
				} else {
			//===start parvez date: 12-08-2022===		
//					sbCandiSalTable.append("<td><strong>Cost To Company (Annually):</strong></td>");
					sbCandiSalTable.append("<td><strong>Annual Gross Earning:</strong></td>");
			//===end parvez date: 12-08-2022===
				}
				sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\"> <strong>" + uF.formatIntoTwoDecimal(dblCTCAnnualy) + "</strong></td>");
				sbCandiSalTable.append("</tr>");
				sbCandiSalTable.append("</table>");
				
				
				hmCandiSalDetails.put("OFFERED_SALARY_STRUCTURE", sbCandiSalTable.toString());
				hmCandiSalDetails.put("CANDI_CTC", uF.formatIntoTwoDecimal(dblCTCMonthly));
//				System.out.println("dblCTCMonthly ===>> " + dblCTCMonthly + " -- int ===>> "+ (int)dblCTCMonthly);
				hmCandiSalDetails.put("CANDI_CTC_WORDS", uF.digitsToWords((int)dblCTCMonthly));
				hmCandiSalDetails.put("CANDI_ANNUAL_CTC", uF.formatIntoTwoDecimal(dblCTCAnnualy));
				hmCandiSalDetails.put("CANDI_ANNUAL_CTC_WORDS", uF.digitsToWords(uF.parseToInt(dblCTCAnnualy+"")));
			}
			
		
			
			
			
			
			
			
			
			/*sbCandiSalTable.append("<table>");  
			sbCandiSalTable.append("<tr>");
			sbCandiSalTable.append("<td valign=\"top\">");
			sbCandiSalTable.append("<table>");
			sbCandiSalTable.append("<tr>");
			sbCandiSalTable.append("<td colspan=\"3\" nowrap=\"nowrap\" align=\"center\"><h5>EARNING DETAILS</h5></td>");
			sbCandiSalTable.append("</tr>");
			sbCandiSalTable.append("<tr>");
			sbCandiSalTable.append("<td align=\"right\">Salary Head</td>");
			sbCandiSalTable.append("<td width=\"30%\" align=\"right\">Monthly</td>");
			sbCandiSalTable.append("<td width=\"30%\" align=\"right\">Annual</td>");
			sbCandiSalTable.append("</tr>");
						
			grossAmount = 0.0d;
			grossYearAmount = 0.0d;
			double netTakeHome = 0.0d;
			
			sbCandiSalTable.append("<tr>");
			sbCandiSalTable.append("<td colspan=\"3\" align=\"center\"><b>Fixed Component- A</b></td>");
//			sbEmpSalTable.append("<td align=\"right\" valign=\"bottom\"></td>");
//			sbEmpSalTable.append("<td align=\"right\" valign=\"bottom\"></td>");
			sbCandiSalTable.append("</tr>");
			
			boolean veriableFlag = false;
			for(int i=0; i<salaryHeadDetailsList.size(); i++) {
				List<String> innerList = salaryHeadDetailsList.get(i);
					if(innerList.get(1).equals("E") && !uF.parseToBoolean(innerList.get(5))) {
						double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
						double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
						grossAmount += dblEarnMonth;
						grossYearAmount += dblEarnAnnual;
						
						netTakeHome += dblEarnMonth;
						
						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td align=\"right\">"+uF.showData(innerList.get(0), "-")+"</td>");
						sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblEarnMonth +"</td>");
						sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblEarnAnnual+"</td>");
						sbCandiSalTable.append("</tr>");
				} 
			}
			
			if(veriableFlag) {
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td colspan=\"3\" align=\"center\"><b>Variable Component- B</b></td>");
//				sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\"></td>");
//				sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\"></td>");
				sbCandiSalTable.append("</tr>");
			}
			
			for (int i = 0; i < salaryHeadDetailsList.size(); i++) {
				List<String> innerList = salaryHeadDetailsList.get(i);
				if (innerList.get(1).equals("E") && uF.parseToBoolean(innerList.get(5))) {
					double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
					double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
					grossAmount += dblEarnMonth;
					grossYearAmount += dblEarnAnnual;

					netTakeHome += dblEarnMonth;

					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td align=\"right\">" + uF.showData(innerList.get(0), "-") + "</td>");
					sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">" + dblEarnMonth + "</td>");
					sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">" + dblEarnAnnual + "</td>");
					sbCandiSalTable.append("</tr>");
				}
			}
			
			sbCandiSalTable.append("<tr>");
			sbCandiSalTable.append("<td align=\"right\"><strong>Gross Salary</strong></td>");
			sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(grossAmount)+"</strong></td>");
			sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(grossYearAmount)+"</strong></td>");
			sbCandiSalTable.append("</tr>");
			sbCandiSalTable.append("</table>");
			sbCandiSalTable.append("</td>");
		
			sbCandiSalTable.append("<td valign=\"top\">");
			sbCandiSalTable.append("<table>");
			
			sbCandiSalTable.append("<tr>");
			sbCandiSalTable.append("<td colspan=\"3\" nowrap=\"nowrap\" align=\"center\"><h5>DEDUCTION DETAILS</h5></td>");
			sbCandiSalTable.append("</tr>");
			
			sbCandiSalTable.append("<tr>");
			sbCandiSalTable.append("<td align=\"right\">Salary Head</td>");
			sbCandiSalTable.append("<td width=\"30%\" align=\"right\">Monthly</td>");
			sbCandiSalTable.append("<td width=\"30%\" align=\"right\">Annual</td>");
			sbCandiSalTable.append("</tr>");
						
						
	
			deductAmount = 0.0d;
			deductYearAmount = 0.0d;
		
			for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
				List<String> innerList = salaryHeadDetailsList.get(i);
				if(innerList.get(1).equals("D")) {
					double dblDeductMonth = 0.0d;
					double dblDeductAnnual = 0.0d;
					if(uF.parseToInt(innerList.get(4)) == EMPLOYEE_ESI){
						dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
						dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
					} else if(uF.parseToInt(innerList.get(4)) == EMPLOYER_ESI){
						dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
						dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
					} else {
						dblDeductMonth += Math.round(uF.parseToDouble(innerList.get(2)));
						dblDeductAnnual += Math.round(uF.parseToDouble(innerList.get(3)));
					}
					deductAmount += dblDeductMonth;
					deductYearAmount += dblDeductAnnual;
					
					netTakeHome -= dblDeductMonth;
		
					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td align=\"right\">"+uF.showData(innerList.get(0), "-")+"</td>");
					sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblDeductMonth +"</td>");
					sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblDeductAnnual +"</td>");
					sbCandiSalTable.append("</tr>");
				}
			}
			
			sbCandiSalTable.append("<tr>");
			sbCandiSalTable.append("<td align=\"right\"><strong>Deduction</strong></td>");
			sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(deductAmount)+"</strong></td>");
			sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(deductYearAmount)+"</strong></td>");
			sbCandiSalTable.append("</tr>");
			sbCandiSalTable.append("</table>");
			sbCandiSalTable.append("</td>");
			sbCandiSalTable.append("</tr>");
		
			double dblMonthContri = 0.0d;
			double dblAnnualContri = 0.0d;
			if(isEPF || isESIC || isLWF){
		
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td valign=\"top\">");
				sbCandiSalTable.append("<table>");
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td colspan=\"3\" nowrap=\"nowrap\" align=\"center\"><h5>CONTRIBUTION DETAILS</h5></td>");
				sbCandiSalTable.append("</tr>");
						
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td align=\"right\">Contribution Head</td>");
				sbCandiSalTable.append("<td width=\"30%\" align=\"right\">Monthly</td>");
				sbCandiSalTable.append("<td width=\"30%\" align=\"right\">Annual</td>");
				sbCandiSalTable.append("</tr>");
				if(isEPF){
					double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
					double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
					dblMonthContri += dblEPFMonth;
					dblAnnualContri += dblEPFAnnual;
				
					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td align=\"right\">Employer PF</td>");
					sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblEPFMonth +"</td>");
					sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblEPFAnnual +"</td>");
					sbCandiSalTable.append("</tr>");
				} 
				if(isESIC){
					
					double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
					double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
					dblMonthContri += dblESIMonth;
					dblAnnualContri += dblESIAnnual;
				
					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td align=\"right\">Employer ESI</td>");
					sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblESIMonth +"</td>");
					sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblESIAnnual +"</td>");
					sbCandiSalTable.append("</tr>");
				}
				if(isLWF){
					double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
					double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
					dblMonthContri += dblLWFMonth;
					dblAnnualContri += dblLWFAnnual;
				
					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td align=\"right\">Employer LWF</td>");
					sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblLWFMonth +"</td>");
					sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblLWFAnnual +"</td>");
					sbCandiSalTable.append("</tr>");
				}
				
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td align=\"right\"><strong>Contribution Total</strong></td>");
				sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(dblMonthContri)+"</strong></td>");
				sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(dblAnnualContri)+"</strong></td>");
					
				sbCandiSalTable.append("</tr>");
				sbCandiSalTable.append("</table>");
				sbCandiSalTable.append("</td>");
				sbCandiSalTable.append("<td>&nbsp;</td>");
				sbCandiSalTable.append("</tr>");
			}
			
			double dblCTCMonthly = grossAmount + dblMonthContri;
			double dblCTCAnnualy = grossYearAmount + dblAnnualContri;
			
			int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
			if(nAnnualVariSize > 0){
			
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td valign=\"top\">");
				sbCandiSalTable.append("<table>");
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td colspan=\"3\" nowrap=\"nowrap\" align=\"center\"><h5>ANNUAL EARNING DETAILS</h5></td>");
				sbCandiSalTable.append("</tr>");
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td align=\"right\">Salary Head</td>");
				sbCandiSalTable.append("<td width=\"30%\" align=\"right\">Monthly</td>");
				sbCandiSalTable.append("<td width=\"30%\" align=\"right\">Annual</td>");
				sbCandiSalTable.append("</tr>");
						
				double grossAnnualAmount = 0.0d;
				double grossAnnualYearAmount = 0.0d;
				for(int i = 0; i < nAnnualVariSize; i++){
					List<String> innerList = salaryAnnualVariableDetailsList.get(i);
					double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
					double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
					grossAnnualAmount += dblEarnMonth;
					grossAnnualYearAmount += dblEarnAnnual;
		
					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td align=\"right\">"+uF.showData(innerList.get(0), "-")+"</td>");
					sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblEarnMonth +"</td>");
					sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblEarnAnnual+"</td>");
					sbCandiSalTable.append("</tr>");
				} 
				dblCTCMonthly += grossAnnualAmount;
				dblCTCAnnualy += grossAnnualYearAmount;
			
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td align=\"right\"><strong>Total</strong></td>");
				sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(grossAnnualAmount)+"</strong></td>");
				sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(grossAnnualYearAmount)+"</strong></td>");
				sbCandiSalTable.append("</tr>");
				sbCandiSalTable.append("</table>");
				sbCandiSalTable.append("</td>");
				sbCandiSalTable.append("<td>&nbsp;</td>");
				sbCandiSalTable.append("</tr>");
			}
			sbCandiSalTable.append("</table>");
			
			sbCandiSalTable.append("<table>");
			sbCandiSalTable.append("<tr>");
			sbCandiSalTable.append("<td align=\"right\">Net Take Home Per Month:</td>");
			sbCandiSalTable.append("<td valign=\"bottom\"> "+uF.formatIntoTwoDecimal(netTakeHome)+"</td>");
			sbCandiSalTable.append("</tr>");                  
			sbCandiSalTable.append("<tr>");
			sbCandiSalTable.append("<td align=\"right\">Cost To Company (Monthly):</td>");
			sbCandiSalTable.append("<td valign=\"bottom\"> "+uF.formatIntoTwoDecimal(dblCTCMonthly)+"</td>");
			sbCandiSalTable.append("</tr>");
			sbCandiSalTable.append("<tr>");
			sbCandiSalTable.append("<td align=\"right\">Cost To Company (Annually):</td>");
			sbCandiSalTable.append("<td valign=\"bottom\"> "+uF.formatIntoTwoDecimal(dblCTCAnnualy)+"</td>");
			sbCandiSalTable.append("</tr>");
			sbCandiSalTable.append("</table>");
		*/
			
		}
		
		/**
		 * Salary Structure End
		 * */
		
		
	} catch (Exception e) {
		e.printStackTrace();
		
	}
	return hmCandiSalDetails;
}




public Map<String, String> getCandiOfferedCTC(Connection con) {
	
	PreparedStatement pst=null;
	ResultSet rs = null;
	UtilityFunctions uF= new UtilityFunctions();
	Map<String, String> hmCandiOffered = new HashMap<String, String>();
	try {
		
		String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
		String strFinancialYearStart = strFinancialYearDates[0];
		String strFinancialYearEnd = strFinancialYearDates[1];
		
		pst = con.prepareStatement("select level_id from candidate_application_details join recruitment_details using (recruitment_id) where candidate_id= ? and recruitment_id = ?");
//		pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
		pst.setInt(1, uF.parseToInt(getCandID()));
		pst.setInt(2, uF.parseToInt(getRecruitId()));
		rs = pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		int nLevelId = 0;
		while(rs.next()){
			nLevelId = rs.getInt("level_id");
		}
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("select state_id,rd.org_id,ismetro from state sd, work_location_info wd, recruitment_details rd " +
				"where rd.wlocation=wd.wlocation_id and wd.wlocation_state_id=sd.state_id and rd.recruitment_id=?");
		pst.setInt(1, uF.parseToInt(getRecruitId()));
		rs = pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		String strStateId = null;
		int nOrgId = 0;
		boolean isMetro = false;
		while(rs.next()){
			strStateId = rs.getString("state_id");
			nOrgId = rs.getInt("org_id");
			isMetro = uF.parseToBoolean(rs.getString("ismetro"));
		}
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("select * from candidate_personal_details where emp_per_id=?");
//		pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
		pst.setInt(1, uF.parseToInt(getCandID()));
		rs = pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		String strGender = null;
		double dblCandAge = 0.0d;
		while(rs.next()){
			strGender = rs.getString("emp_gender");
			String strDays = uF.dateDifference(rs.getString("emp_date_of_birth"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE);
			dblCandAge = uF.parseToDouble(strDays) / 365;
		}
		rs.close();
		pst.close();
		
		String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
		String[] strPayCycleDates = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, ""+nOrgId);
		String strD1 = strPayCycleDates[0];
		String strD2 = strPayCycleDates[1];
		String strPC = strPayCycleDates[2];
		
		int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
		
		pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		rs = pst.executeQuery();
//		System.out.println(" pst==>"+pst);
		Map hmHRAExemption = new HashMap();
		while(rs.next()){
			hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
			hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
			hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
			hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
		}
		rs.close();
		pst.close();
		
		Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
		pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		rs = pst.executeQuery();
		while(rs.next()){
			hmOtherTaxDetails.put(rs.getString("state_id")+"_SERVICE_TAX", rs.getString("service_tax"));
			hmOtherTaxDetails.put(rs.getString("state_id")+"_EDU_TAX", rs.getString("education_tax"));
			hmOtherTaxDetails.put(rs.getString("state_id")+"_STD_TAX", rs.getString("standard_tax"));
			hmOtherTaxDetails.put(rs.getString("state_id")+"_FLAT_TDS", rs.getString("flat_tds"));
			
			hmOtherTaxDetails.put(rs.getString("state_id")+"_MAX_TAX_INCOME", rs.getString("max_net_tax_income"));
			hmOtherTaxDetails.put(rs.getString("state_id")+"_REBATE_AMOUNT", rs.getString("rebate_amt"));
			hmOtherTaxDetails.put(rs.getString("state_id")+"_SWACHHA_BHARAT_CESS", rs.getString("swachha_bharat_cess"));
			hmOtherTaxDetails.put(rs.getString("state_id")+"_KRISHI_KALYAN_CESS", rs.getString("krishi_kalyan_cess"));
			
			hmOtherTaxDetails.put(rs.getString("state_id")+"_CGST", rs.getString("cgst"));
			hmOtherTaxDetails.put(rs.getString("state_id")+"_SGST", rs.getString("sgst"));
			
//			dblInvestmentExemption = 100000;
		}
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and section_id=3 order by section_code");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		rs = pst.executeQuery();
//		System.out.println(" pst==>"+pst);
		double dblInvestmentExemption = 0.0d;
		if (rs.next()) {
			dblInvestmentExemption = uF.parseToDouble(rs.getString("section_exemption_limit"));
		}
		rs.close();
		pst.close();
		
		ApprovePayroll objAP = new ApprovePayroll();
		objAP.CF = CF;
		objAP.session = session;
		objAP.request = request; 
		
//		Map<String, String> hmEmpExemptionsMap = objAP.getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, dblInvestmentExemption);
//		Map<String, String> hmEmpHomeLoanMap = objAP.getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd);
		Map<String,String> hmFixedExemptions = objAP.getFixedExemption(con, uF, strFinancialYearStart, strFinancialYearEnd);
//		Map<String, String> hmEmpRentPaidMap = objAP.getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
		
		Map<String, String> hmSalaryDetails = new HashMap<String, String>();
		pst = con.prepareStatement("select * from salary_details where salary_head_id not in ("+GROSS+","+CTC+") and level_id=? and (is_delete is null or is_delete=false) order by earning_deduction desc, salary_head_id, weight");
		pst.setInt(1, nLevelId);
		rs = pst.executeQuery();  
		while(rs.next()){
			hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
		}
		rs.close();
		pst.close();
		
//		pst = con.prepareStatement("select * from annual_variable_details where level_id=? and org_id=? " +
//				"and financial_year_start=? and financial_year_end=? and salary_head_id in (select salary_head_id from salary_details where is_annual_variable=true and (is_delete is null or is_delete = false))");
//		pst.setInt(1, nLevelId);
//		pst.setInt(2, nOrgId);
//		pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//		pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//		rs = pst.executeQuery();
//		Map<String, String> hmAnnualVariableAmt = new HashMap<String, String>();
//		while(rs.next()){
//			hmAnnualVariableAmt.put(rs.getString("salary_head_id"), rs.getString("variable_amount"));
//		}
//		rs.close();
//		pst.close();
//		request.setAttribute("hmAnnualVariableAmt", hmAnnualVariableAmt);
		
		pst = con.prepareStatement("SELECT * FROM (select csd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id " +
				"FROM candidate_salary_details WHERE emp_id=? AND effective_date = (SELECT MAX(effective_date) FROM candidate_salary_details " +
				"WHERE emp_id=?) group by salary_head_id) a, candidate_salary_details csd WHERE a.emp_salary_id=csd.emp_salary_id " +
				"and a.salary_head_id=csd.salary_head_id and emp_id=? AND effective_date= (SELECT MAX(effective_date) " +
				"FROM candidate_salary_details WHERE emp_id=?)) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
				"WHERE level_id=? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) " +
				"order by sd.earning_deduction desc, weight");
		pst.setInt(1, uF.parseToInt(getCandID()));
		pst.setInt(2, uF.parseToInt(getCandID()));
		pst.setInt(3, uF.parseToInt(getCandID()));
		pst.setInt(4, uF.parseToInt(getCandID()));
		pst.setInt(5, nLevelId);
//		System.out.println("pst =====> " + pst);
		rs = pst.executeQuery();
		List<List<String>> salaryHeadDetailsList = new ArrayList<List<String>>();
		Map<String, String> hmTotal = new HashMap<String, String>();
		double dblGrossTDS = 0.0d;
		boolean isEPF = false;
		boolean isESIC = false;
		boolean isLWF = false;
		double grossAmount = 0.0d;
		double grossYearAmount = 0.0d;
		double deductAmount = 0.0d;
		double deductYearAmount = 0.0d;
		List<List<String>> salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
		while(rs.next()) {
			
			if(uF.parseToInt(rs.getString("salary_head_id")) == CTC){
				continue;
			}
			
			if(!uF.parseToBoolean(rs.getString("isdisplay"))){
				continue;
			}

			if(rs.getString("earning_deduction").equals("E")) {
				if(!uF.parseToBoolean(rs.getString("is_variable")) && uF.parseToBoolean(rs.getString("is_annual_variable"))){
					
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("salary_head_name"));
					innerList.add(rs.getString("earning_deduction"));
					
					if(uF.parseToBoolean(rs.getString("isdisplay"))){
						double dblAmount = 0.0d;
//						double dblYearAmount = uF.parseToDouble(hmAnnualVariableAmt.get(rs.getString("salary_head_id")));
						double dblYearAmount = 0.0d;
						
						innerList.add(""+dblAmount);
						innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
						innerList.add(rs.getString("salary_head_id"));
						innerList.add(rs.getString("is_variable"));
						
						grossAmount += dblAmount;
						grossYearAmount += dblYearAmount;
						
						if(uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						} else if(uF.parseToInt(rs.getString("salary_head_id")) != TRAVEL_REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						} else if(uF.parseToInt(rs.getString("salary_head_id")) != MOBILE_REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						} else if(uF.parseToInt(rs.getString("salary_head_id")) != OTHER_REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						}
						
						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
					} else {
						innerList.add("0.0");
						innerList.add("0.0");
						innerList.add(rs.getString("salary_head_id"));
						innerList.add(rs.getString("is_variable"));
						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
					}
					salaryAnnualVariableDetailsList.add(innerList);
				
				} else {	
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("salary_head_name"));
					innerList.add(rs.getString("earning_deduction"));
					
					if(uF.parseToBoolean(rs.getString("isdisplay"))){
						double dblAmount = 0.0d;
						double dblYearAmount = 0.0d;
						if(uF.parseToBoolean(rs.getString("is_variable")) && uF.parseToBoolean(rs.getString("is_annual_variable"))){
							dblAmount = 0.0d;
//							dblYearAmount = uF.parseToDouble(hmAnnualVariableAmt.get(rs.getString("salary_head_id")));
							dblYearAmount = 0.0d;
						} else {
							dblAmount = rs.getDouble("amount");
//							if(hmPerkAlignAmount.containsKey(rs.getString("salary_head_id"))){
//								dblAmount = uF.parseToDouble(hmPerkAlignAmount.get(rs.getString("salary_head_id")));
//							}
							dblYearAmount = dblAmount * 12;
						}
						
						innerList.add(""+dblAmount);
						innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
						innerList.add(rs.getString("salary_head_id"));
						innerList.add(rs.getString("is_variable"));
						
						grossAmount += dblAmount;
						grossYearAmount += dblYearAmount;
						
						if(uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						} else if(uF.parseToInt(rs.getString("salary_head_id")) != TRAVEL_REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						} else if(uF.parseToInt(rs.getString("salary_head_id")) != MOBILE_REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						} else if(uF.parseToInt(rs.getString("salary_head_id")) != OTHER_REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						}
						
						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
					} else {
						innerList.add("0.0");
						innerList.add("0.0");
						innerList.add(rs.getString("salary_head_id"));
						innerList.add(rs.getString("is_variable"));
						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
					}
					salaryHeadDetailsList.add(innerList);
				}
			} else if(rs.getString("earning_deduction").equals("D")) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("salary_head_name"));
				innerList.add(rs.getString("earning_deduction"));
				if(uF.parseToBoolean(rs.getString("isdisplay"))){
//						int nPayMonth = uF.parseToInt(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, "MM"));
					switch(rs.getInt("salary_head_id")){
												
						case PROFESSIONAL_TAX :
							  
							double dblAmount = calculateProfessionalTax(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, nPayMonth, strStateId,strGender);
							dblAmount = Math.round(dblAmount);
//							double dblYearAmount =  dblAmount * 12;
							double dblYearAmount =  getAnnualProfessionalTax(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, strStateId, strGender);
							
							deductAmount += dblAmount;
//							deductYearAmount += dblYearAmount > 0.0d ? dblYearAmount + 100 : 0.0d;
							deductYearAmount += dblYearAmount;
							
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount));
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
							
							break;
						
						case EMPLOYEE_EPF :
							isEPF = true;	
							double dblAmount1 = objAP.calculateCandiEEPF(con, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, getCandID(),nLevelId,nOrgId);
							dblAmount1 = Math.round(dblAmount1);
							double dblYearAmount1 = dblAmount1 * 12;
							
							deductAmount += dblAmount1;
							deductYearAmount += dblYearAmount1;
							
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount1));
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount1));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount1));
							
							break;
						
						case EMPLOYEE_ESI :
							isESIC = true;
							double dblAmount4 = objAP.calculateCandiEEESI(con, uF, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, getCandID(),nLevelId,nOrgId);
							dblAmount4 = Math.ceil(dblAmount4);
							double dblYearAmount4 = dblAmount4 * 12;
							dblYearAmount4 = Math.ceil(dblYearAmount4);
							
							deductAmount += dblAmount4;
							deductYearAmount += dblYearAmount4;
//							System.out.println("dblAmount4====>"+dblAmount4);
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount4));
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount4));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount4));
							
							break;
						
						case EMPLOYEE_LWF :
							isLWF = true;
							double dblAmount6 = objAP.calculateEELWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, getCandID(), nPayMonth, ""+nOrgId);
							dblAmount6 = Math.round(dblAmount6);
							double dblYearAmount6 = dblAmount6 * 12;
							
							deductAmount += dblAmount6;
							deductYearAmount += dblYearAmount6;
							
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount6));
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount6));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount6));
							
							break;
						
						case TDS :
							
							double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
							double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
							double dblHRA = uF.parseToDouble(hmTotal.get(HRA+""));
							
							String[] hraSalaryHeads = null;
							if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null){
								hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
							}
							
							double dblHraSalHeadsAmount = 0;
							for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++){
								dblHraSalHeadsAmount += uF.parseToDouble((String)hmTotal.get(hraSalaryHeads[i]));
							}
							
							
							double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_EDU_TAX"));
							double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_STD_TAX"));
							double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_FLAT_TDS"));
							 
							double dblAmount7 = objAP.calculateCandidateTDS(con, uF,strD2,strD1, dblGrossTDS, dblCess1, dblCess2, dblFlatTDS, dblInvestmentExemption, dblHRA, dblHraSalHeadsAmount,
									nPayMonth,strD1, strFinancialYearStart, strFinancialYearEnd, uF.parseToInt(getCandID()), strGender,  dblCandAge, strStateId,
									hmFixedExemptions, isMetro, hmTotal, hmSalaryDetails, nLevelId, CF,hmOtherTaxDetails,nOrgId);
							dblAmount7 = Math.round(dblAmount7);
							double dblYearAmount7 = dblAmount7 * 12;
							
							deductAmount += dblAmount7;
							deductYearAmount += dblYearAmount7;
							
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount7));
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount7));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount7));
							
							break;
						
						default:
							
							double dblAmount9 = uF.parseToDouble(rs.getString("amount"));
							double dblYearAmount9 = dblAmount9 * 12;
							
							deductAmount += dblAmount9;
							deductYearAmount += dblYearAmount9;
							
							innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblAmount9));
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount9));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount9));
							
							break;
					}
				}  else {
					innerList.add("0.0");
					innerList.add("0.0");
					innerList.add(rs.getString("salary_head_id"));
					innerList.add(rs.getString("is_variable"));
					hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
				}
				
				salaryHeadDetailsList.add(innerList);
			}
			
		}
		rs.close();
		pst.close();
		
		/**
		 * Employer Contribution
		 * */ 
		Map<String,String> hmContribution = new HashMap<String, String>();
		if(isEPF){
			double dblAmount = objAP.calculateCandiERPF(con,CF, null, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, getCandID(), nLevelId, nOrgId);
			dblAmount = Math.round(dblAmount);
			double dblYearAmount = dblAmount * 12;
			hmContribution.put("EPF_MONTHLY", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
			hmContribution.put("EPF_ANNUALY", uF.formatIntoTwoDecimalWithOutComma(dblYearAmount));
		}
		if(isESIC){
			double dblAmount = objAP.calculateCandiERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId,getCandID(), nOrgId, nLevelId);
			dblAmount = Math.ceil(dblAmount);
			double dblYearAmount = dblAmount * 12;
			dblYearAmount = Math.ceil(dblYearAmount);
			
			hmContribution.put("ESI_MONTHLY", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
			hmContribution.put("ESI_ANNUALY", uF.formatIntoTwoDecimalWithOutComma(dblYearAmount));
		}
		if(isLWF){
			double dblAmount = objAP.calculateERLWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, nPayMonth, ""+nOrgId);
			dblAmount = Math.round(dblAmount);
			double dblYearAmount = dblAmount * 12;
			hmContribution.put("LWF_MONTHLY", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
			hmContribution.put("LWF_ANNUALY", uF.formatIntoTwoDecimalWithOutComma(dblYearAmount));				
		}
		
		/**
		 * Employer Contribution End
		 * */ 
		
		
		/**
		 * Salary Structure Table
		 * */
		if(salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && salaryHeadDetailsList.size() > 0){
			grossAmount = 0.0d;
			grossYearAmount = 0.0d;
			double netTakeHome = 0.0d;
			for(int i=0; i<salaryHeadDetailsList.size(); i++) {
				List<String> innerList = salaryHeadDetailsList.get(i);
					if(innerList.get(1).equals("E")) {
						double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
						double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
						grossAmount += dblEarnMonth;
						grossYearAmount += dblEarnAnnual;
						
						netTakeHome += dblEarnMonth;
				} 
			}
	
//			deductAmount = 0.0d;
//			deductYearAmount = 0.0d;
//		
//			for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
//				List<String> innerList = salaryHeadDetailsList.get(i);
//				if(innerList.get(1).equals("D")) {
//					double dblDeductMonth = Math.round(uF.parseToDouble(innerList.get(2)));
//					double dblDeductAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
//					deductAmount += dblDeductMonth;
//					deductYearAmount += dblDeductAnnual;
//					
//					netTakeHome -= dblDeductMonth;
//				}
//			}
//			
//			sbCandiSalTable.append("<tr>");
//			sbCandiSalTable.append("<td align=\"right\"><strong>Deduction</strong></td>");
//			sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(deductAmount)+"</strong></td>");
//			sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(deductYearAmount)+"</strong></td>");
//			sbCandiSalTable.append("</tr>");
//			sbCandiSalTable.append("</table>");
//			sbCandiSalTable.append("</td>");
//			sbCandiSalTable.append("</tr>");
		
			double dblMonthContri = 0.0d;
			double dblAnnualContri = 0.0d;
			if(isEPF || isESIC || isLWF){
				if(isEPF){
					double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
					double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
					dblMonthContri += dblEPFMonth;
					dblAnnualContri += dblEPFAnnual;
				} 
				if(isESIC){
					
					double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
					double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
					dblMonthContri += dblESIMonth;
					dblAnnualContri += dblESIAnnual;
				}
				if(isLWF){
					double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
					double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
					dblMonthContri += dblLWFMonth;
					dblAnnualContri += dblLWFAnnual;
				}
			}
			
			double dblCTCMonthly = grossAmount + dblMonthContri;
			double dblCTCAnnualy = grossYearAmount + dblAnnualContri;
			
			int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
			if(nAnnualVariSize > 0){
				double grossAnnualAmount = 0.0d;
				double grossAnnualYearAmount = 0.0d;
				for(int i = 0; i < nAnnualVariSize; i++){
					List<String> innerList = salaryAnnualVariableDetailsList.get(i);
					double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
					double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
					grossAnnualAmount += dblEarnMonth;
					grossAnnualYearAmount += dblEarnAnnual;
				} 
				dblCTCMonthly += grossAnnualAmount;
				dblCTCAnnualy += grossAnnualYearAmount;
			}
			
			hmCandiOffered.put("CANDI_CTC", uF.formatIntoTwoDecimal(dblCTCMonthly));
			hmCandiOffered.put("CANDI_ANNUAL_CTC", uF.formatIntoTwoDecimal(dblCTCAnnualy));
		}
		/**
		 * Salary Structure End
		 * */
		
		
	} catch (Exception e) {
		e.printStackTrace();
		
	}
	return hmCandiOffered;
}
	
private double calculateProfessionalTax(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd,
		int nPayMonth, String strStateId,String strGender) {
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	double dblAmount= 0;
	
	
	try {
		pst = con.prepareStatement("select * from deduction_details_india where income_from<=? and income_to>=? and state_id=? " +
				"and financial_year_from=? and financial_year_to=? and gender =? limit 1");
		pst.setDouble(1, dblGross);
		pst.setDouble(2, dblGross);
		pst.setInt(3, uF.parseToInt(strStateId));
		pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setString(6, strGender);
		rs = pst.executeQuery();  
		while(rs.next()){
			dblAmount = rs.getDouble("deduction_paycycle");
		}
		rs.close();
		pst.close();
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(rs !=null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(pst !=null){
			try {
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	return dblAmount;
}
	
	private double getAnnualProfessionalTax(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd,String strStateId, String strEmpGender) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblDeductionAnnual= 0;
		
		
		try {
			pst = con.prepareStatement("select * from deduction_details_india where income_from<=? and income_to>=? and state_id=? " +
					"and financial_year_from=? and financial_year_to=? and gender =? limit 1");
			pst.setDouble(1, dblGross);
			pst.setDouble(2, dblGross);
			pst.setInt(3, uF.parseToInt(strStateId));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(6, strEmpGender);	
			rs = pst.executeQuery();  
			while(rs.next()){
				dblDeductionAnnual = rs.getDouble("deduction_amount");
			}
			rs.close();
			pst.close();
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblDeductionAnnual;
	}

	private void getInterviewDates(UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		

		// List<String> alRejectedDateList=new ArrayList<String>();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmDateMap =new LinkedHashMap<String, String>();
			Map<String, String> hmTimeMap =new LinkedHashMap<String, String>();
			pst = con.prepareStatement("select * from candidate_interview_availability where emp_id=?"); //recruitment_id=? and 
//			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(1, uF.parseToInt(getCandID()));

			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("pst=====>"+pst);
			while (rst.next()) {

				hmDateMap.put(rst.getString("int_avail_id"), uF.getDateFormat(
						rst.getString("_date"), DBDATE, DATE_FORMAT));

				hmTimeMap.put(rst.getString("int_avail_id"),
						getTimeFormat(rst.getString("_time")));
			}	
			rst.close();
			pst.close();
			request.setAttribute("hmDateMap", hmDateMap);
			request.setAttribute("hmTimeMap", hmTimeMap);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	private String getTimeFormat(String time) {

		if (time != null && !time.equals(""))
			return time.substring(0, 5);
		else
			return "";
	}
     
	public void getPanelComment(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

//			List<String> panelList = new ArrayList<String>();
			String notiStatus = null;
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			request.setAttribute("hmEmpName", hmEmpName);
			pst = con.prepareStatement("Select send_notification_status from candidate_application_details  where  candidate_id = ? and recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(getCandID()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("pst noti status ===> "+pst);
			while (rs.next()) {
				notiStatus = rs.getString("send_notification_status");
			}
			rs.close();
			pst.close();
			
//			System.out.println("notiStatus ===> "+notiStatus);
			request.setAttribute("notiStatus", notiStatus);
			
			List<String> roundIdsRecruitwiseList = new ArrayList<String>(); 
			Map<String, String> hmAssessmentName = CF.getAssessmentNameMap(con, uF);
			Map<String, String> hmRoundAssessment = new HashMap<String, String>();
			pst = con.prepareStatement("select distinct(round_id),recruitment_id,assessment_id from panel_interview_details where recruitment_id =? and panel_emp_id is null order by round_id");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				roundIdsRecruitwiseList.add(rs.getString("round_id"));
				if(uF.parseToInt(rs.getString("assessment_id"))>0) {
					hmRoundAssessment.put(rs.getString("round_id")+"_ID", rs.getString("assessment_id"));
					hmRoundAssessment.put(rs.getString("round_id")+"_NAME", hmAssessmentName.get(rs.getString("assessment_id")));
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmRoundAssessment : "+hmRoundAssessment);
			request.setAttribute("roundIdsRecruitwiseList", roundIdsRecruitwiseList);
			request.setAttribute("hmRoundAssessment", hmRoundAssessment);
			
			
			Map<String, List<String>> hmpanelIDSRAndRwise = new HashMap<String, List<String>>();
			List<String> panelEmpIDRAndRwiseList = new ArrayList<String>();
			pst = con.prepareStatement("select recruitment_id,round_id,panel_emp_id from panel_interview_details where recruitment_id =? and panel_emp_id is not null");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				panelEmpIDRAndRwiseList = hmpanelIDSRAndRwise.get(rs.getString("round_id"));
				if(panelEmpIDRAndRwiseList == null)panelEmpIDRAndRwiseList = new ArrayList<String>();
				panelEmpIDRAndRwiseList.add(rs.getString("panel_emp_id"));
				hmpanelIDSRAndRwise.put(rs.getString("round_id"), panelEmpIDRAndRwiseList);
			}
			rs.close();
			pst.close();
			
			
			Map<String, String> hmpanelNameRAndRwise = new HashMap<String, String>();
			List<String> panelEmpIDList1RAndRwise = new ArrayList<String>();
			Iterator<String> it = hmpanelIDSRAndRwise.keySet().iterator();
			while (it.hasNext()) {
				String roundId = it.next();
				panelEmpIDList1RAndRwise = hmpanelIDSRAndRwise.get(roundId);
				String panelEmpNamesRAndRwise = uF.showData(getAppendDataList1(con, panelEmpIDList1RAndRwise), "");
				hmpanelNameRAndRwise.put(roundId, panelEmpNamesRAndRwise);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmpanelNameRAndRwise", hmpanelNameRAndRwise);

			Map<String, List<List<String>>> hmPanelData = new HashMap<String, List<List<String>>>();

			pst = con.prepareStatement("select * from candidate_interview_panel where candidate_id=? and recruitment_id=? and panel_user_id > 0");
			pst.setInt(1, uF.parseToInt(getCandID()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("pst=====>"+pst);
			List<List<String>> roundDataList = new ArrayList<List<String>>();
			while (rs.next()) {
				roundDataList = hmPanelData.get(rs.getString("panel_round_id"));
				if(roundDataList == null) roundDataList = new ArrayList<List<String>>();
					
					List<String> alInner = new ArrayList<String>();
					alInner.add(rs.getString("is_interview_taken"));
					alInner.add(rs.getString("comments"));
					alInner.add(rs.getString("status"));
					alInner.add(rs.getString("panel_rating"));
				    alInner.add(rs.getString("status"));
				    alInner.add(rs.getString("panel_user_id"));
				    alInner.add(rs.getString("interview_attachment"));
				    roundDataList.add(alInner);
				    hmPanelData.put(rs.getString("panel_round_id"), roundDataList);
				
					if(uF.parseToBoolean(rs.getString("is_interview_taken"))) {
						
					}
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmPanelData", hmPanelData);
			
			
			pst = con.prepareStatement("select application_status,candidate_final_status from candidate_application_details where candidate_id=? and recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getCandID()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			String strInterviewFinalStatus = null;
			String strCandiShortlistStatus = null;
			while (rs.next()) {
				strInterviewFinalStatus = rs.getString("candidate_final_status");
				strCandiShortlistStatus = rs.getString("application_status");
			}
			rs.close();
			pst.close();
//			System.out.println("strCandiShortlistStatus ===>> " + strCandiShortlistStatus);
			request.setAttribute("strInterviewFinalStatus", strInterviewFinalStatus);
			request.setAttribute("strCandiShortlistStatus", strCandiShortlistStatus);
			
			
			pst = con.prepareStatement("select min(panel_round_id) as round_id from candidate_interview_panel where candidate_id=? and recruitment_id=? and panel_user_id>0 and is_interview_taken=false");
			pst.setInt(1, uF.parseToInt(getCandID()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			String strMinRoundId = null;
			while (rs.next()) {
				strMinRoundId = rs.getString("round_id");
			}
			rs.close();
			pst.close();
			request.setAttribute("strMinRoundId", strMinRoundId);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private String getAppendDataList1(Connection con, List<String> strIDList) {
		StringBuilder sb = new StringBuilder();
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
//			System.out.println("strIDList ===>> " + strIDList + " --- SIZE ===>> " + strIDList.size());
			for (int i =0; strIDList != null && i<strIDList.size(); i++) {

				if(strIDList.get(i)!=null && !strIDList.get(i).equals("") && !strIDList.get(i).equals("null")){
				 if(i==strIDList.size()-1){
					 sb.append(hmEmpName.get(strIDList.get(i).trim()));
				 }else{	
					sb.append(hmEmpName.get(strIDList.get(i).trim())+", <br/> ");
				 }
				}
			}
		return sb.toString();
	}
	
	
	private void getHrData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		
		
		List<String> alInner=null;
		List<String> panelList = new ArrayList<String>();
		Map<String, List<String>> hmPanelScheduleInfo = new HashMap<String, List<String>>();
		Map<String, List<String>> hmPanelInterviewTaken = new HashMap<String, List<String>>();
		
		try{
			
			con=db.makeConnection(con);
			
		
		pst = con.prepareStatement("Select panel_employee_id  from recruitment_details  where  recruitment_id=? ");
		pst.setInt(1, uF.parseToInt(getRecruitId()));
//      System.out.println("pst==="+pst);
		rst = pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				panelList = getPanelList(rst.getString("panel_employee_id"));
			}
			rst.close();
			pst.close();
			
			request.setAttribute("panelList", panelList);
			
			pst = con.prepareStatement("select * from candidate_interview_panel where candidate_id=? and recruitment_id=? and panel_user_id>0");
			pst.setInt(1, uF.parseToInt(getCandID()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				alInner=new ArrayList<String>();

				alInner.add(uF.getDateFormat(rst.getString("interview_date"), DBDATE, DATE_FORMAT) );
				if(rst.getString("interview_time") != null){
					alInner.add(rst.getString("interview_time").substring(0, 5));
				}else {
					alInner.add(uF.showData(rst.getString("interview_time"),"-"));
				}
				
				if(rst.getBoolean("is_interview_taken"))
				    hmPanelInterviewTaken.put(rst.getString("panel_round_id"), alInner);
					else if(!rst.getBoolean("is_interview_taken"))
						hmPanelScheduleInfo.put(rst.getString("panel_round_id"), alInner);
			}
			rst.close();
			pst.close();

			request.setAttribute("hmPanelScheduleInfo", hmPanelScheduleInfo);
			request.setAttribute("hmPanelInterviewTaken", hmPanelInterviewTaken);

			Map<String, List<String>> hmPanelDataHR = new HashMap<String, List<String>>();

			pst = con.prepareStatement("select is_interview_taken,panel_round_id,comments,status,panel_rating from candidate_interview_panel where candidate_id=? and recruitment_id=? and panel_user_id>0");
			pst.setInt(1, uF.parseToInt(getCandID()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));

			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("hmpaneldatahr pst ===> "+pst);
			while (rst.next()) {

				alInner = new ArrayList<String>();

				alInner.add(rst.getString("comments"));
				alInner.add(rst.getString("status"));
				alInner.add(rst.getString("panel_rating"));
				alInner.add(rst.getString("is_interview_taken"));

				hmPanelDataHR.put(rst.getString("panel_round_id"), alInner);
			}	
			
			rst.close();
			pst.close();
//			System.out.println("hmPanelDataHR ===> "+hmPanelDataHR);
			// Panel information for hr 
			request.setAttribute("hmPanelDataHR", hmPanelDataHR);
			
			Map<String, List<String>> hmCommentsHr = new HashMap<String, List<String>>();
			pst = con.prepareStatement("select candidate_final_status,candidate_hr_comments,candidate_joining_date,ctc_offered," +
					"candidate_status,application_status,aligned_pro_id from candidate_application_details where candidate_id = ? and recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(getCandID()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
                 alInner=new ArrayList<String>();
                 alInner.add(rst.getString("candidate_final_status"));//0
                 alInner.add(rst.getString("candidate_hr_comments"));//1
                 alInner.add(uF.getDateFormat(rst.getString("candidate_joining_date"), DBDATE, DATE_FORMAT));//2
                 alInner.add(rst.getString("ctc_offered"));//3
                 alInner.add(rst.getString("candidate_status"));
                 alInner.add(rst.getString("application_status"));
                 alInner.add(uF.showData(CF.getProjectNameById(con, rst.getString("aligned_pro_id")), ""));
                 hmCommentsHr.put(getCandID(), alInner);
			}
			rst.close();
			pst.close();
			
			/// HR Comment for Selected Candidate 
			
			request.setAttribute("hmCommentsHr", hmCommentsHr);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private String insertInterviewComment(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
//			System.out.println("approvedeny ====>> " + approvedeny);
			String strInterAttach = null;
//			System.out.println("getStrInterviewDocument() ===>> " + getStrInterviewDocument());
			if(getStrInterviewDocument() !=null) {
				if(CF.getStrDocSaveLocation()==null) {
					strInterAttach = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getStrInterviewDocument(), getStrInterviewDocumentFileName(), getStrInterviewDocumentFileName(), CF);
				} else {
					strInterAttach = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_RECRUITMENT+"/"+I_DOCUMENT+"/"+getRecruitId()+"/"+getCandID()+"/"+getRoundID(), getStrInterviewDocument(), getStrInterviewDocumentFileName(), getStrInterviewDocumentFileName(), CF);
				} 
			}
			pst = con.prepareStatement("update candidate_interview_panel set comments=?,status=?,panel_rating=?,is_interview_taken=?,interview_attachment=? " +
				"where panel_user_id=? and recruitment_id=? and candidate_id=? and panel_round_id=?");
			pst.setString(1, getInterviewcomment());
			if (approvedeny != null) {
				pst.setInt(2, 1);
			} else {
				pst.setInt(2, -1);
			}
			pst.setDouble(3, uF.parseToDouble(getPanelrating()));
			pst.setBoolean(4, true);
			pst.setString(5, strInterAttach);
			if(strUserType != null && strUserType.equals(EMPLOYEE)) {
				pst.setInt(6, uF.parseToInt(strSessionEmpId));
			} else {
				pst.setInt(6, uF.parseToInt(getUserId()));
			}
			pst.setInt(7, uF.parseToInt(getRecruitId()));
			pst.setInt(8, uF.parseToInt(getCandID()));
			pst.setInt(9, uF.parseToInt(getRoundID()));
			pst.executeUpdate();
//			System.out.println("pst ===========>> " + pst);
			pst.close();
			
			
			Map<String, String> hmCandiRejectFromRound = new HashMap<String, String>();
			pst = con.prepareStatement("Select * from candidate_interview_panel where status=-1 and recruitment_id = ? and candidate_id = ? and panel_user_id>0");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getCandID()));
			rst = pst.executeQuery();
			while (rst.next()) {
				hmCandiRejectFromRound.put("REJECT_STATUS", rst.getString("status"));
			}
			rst.close();
			pst.close();
			
			Map<String, String> hmClearRoundCnt = new HashMap<String, String>();
			pst = con.prepareStatement("select count (distinct(panel_round_id)) as count,recruitment_id,candidate_id from candidate_interview_panel " +
					"where status = 1 and recruitment_id = ? and candidate_id = ? and panel_user_id>0 group by recruitment_id,candidate_id ");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getCandID()));
			rst = pst.executeQuery();
			while (rst.next()) {
				hmClearRoundCnt.put("CLEAR_ROUND_COUNT", rst.getString("count"));
			}
			rst.close();
			pst.close();
			
			Map<String, String> hmRoundCnt = new HashMap<String, String>();
			pst = con.prepareStatement("select count (distinct(round_id))as count,recruitment_id from panel_interview_details where recruitment_id=? group by recruitment_id");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rst = pst.executeQuery();
			while (rst.next()) {
				hmRoundCnt.put("ROUND_COUNT", rst.getString("count"));
			}
			rst.close();
			pst.close();
//			System.out.println("hmCandiRejectFromRound ===>> " + hmCandiRejectFromRound);
//			System.out.println("hmClearRoundCnt ===>> " + hmClearRoundCnt);
//			System.out.println("hmRoundCnt ===>> " + hmRoundCnt);
			
			if ((hmCandiRejectFromRound == null || hmCandiRejectFromRound.isEmpty() || !hmCandiRejectFromRound.get("REJECT_STATUS").equals("-1")) && hmClearRoundCnt != null && 
				!hmClearRoundCnt.isEmpty() && hmRoundCnt != null && hmClearRoundCnt.get("CLEAR_ROUND_COUNT").equals(hmRoundCnt.get("ROUND_COUNT"))) {
//			if(panelEmpCnt == intrvwTakenEmpCnt) {
				pst = con.prepareStatement("select approved_by from recruitment_details where recruitment_id=?");
				pst.setInt(1, uF.parseToInt(getRecruitId()));
				rst = pst.executeQuery();
				String approvedBy = null;
				while (rst.next()) {
					approvedBy = rst.getString("approved_by");
				}
				rst.close();
				pst.close();
				
				if(approvedBy != null) {
					Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
					String candiName = CF.getCandiNameByCandiId(con, getCandID());
					String recruitName = CF.getRecruitmentNameById(con, uF, getRecruitId());
					
					String strDomain = request.getServerName().split("\\.")[0];
					String alertData = "<div style=\"float: left;\"> "+candiName+" is finalized for job code ("+recruitName+") by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
					String alertAction = "Applications.action?pType=WR";
					
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(approvedBy);
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(hmUserTypeId.get(ADMIN));
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
					sendMailForFinalization(con, approvedBy);
				}
			}
		
			pst=con.prepareStatement("delete from candidate_activity_details where recruitment_id=? and candi_id=? and round_id=? and user_id=? and " +
					"activity_id = ?");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			pst.setInt(2,uF.parseToInt(getCandID()));
			pst.setInt(3,uF.parseToInt(getRoundID()));
			pst.setInt(4,uF.parseToInt(strSessionEmpId));
			pst.setInt(5, CANDI_ACTIVITY_ROUND_SHORTLIST_OR_REJECT_ID);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,round_id,activity_name,user_id,entry_date," +
				"activity_id) values(?,?,?,?, ?,?,?)");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			pst.setInt(2,uF.parseToInt(getCandID()));
			pst.setInt(3,uF.parseToInt(getRoundID()));
			if (approvedeny != null) {
				pst.setString(4, "Round Cleared");
			} else {
				pst.setString(4, "Round Rejected");
			}
			pst.setInt(5,uF.parseToInt(strSessionEmpId));
			pst.setDate	(6, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setInt(7, CANDI_ACTIVITY_ROUND_SHORTLIST_OR_REJECT_ID);
			pst.execute();
			pst.close();

			//Start Dattatray Date : 11-08-21
			if (approvedeny != null) {
				sendMailRound(N_SELECTED_ROUND);
			} else {
				sendMailRound(N_REJECTED_ROUND);
			}//End Dattatray Date : 11-08-21
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		if(getForm() != null && getForm().equals("C")) {
			return "takeInterviewCal";
		} else {
			return "takeInterview";
		}
	}

	
	
	public void sendMailForFinalization(Connection con, String approvedBy) {

		try {
			Map<String, Map<String, String>> hmCandiInfo = CF.getCandiInfoMap(con, false);
			Map<String, String> hmCandiInner = hmCandiInfo.get(getCandID());
//				System.out.println("hmCandiInner ===> " + hmCandiInner);
			
				Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
					Map<String, String> hmEmpInner = hmEmpInfo.get(approvedBy);
//					System.out.println("hmEmpInner ===> "+hmEmpInner);
					String strDomain = request.getServerName().split("\\.")[0];
					Notifications nF1 = new Notifications(N_CANDI_FINALIZATION_FROM_EMP, CF);
				 	nF1.setDomain(strDomain);
					nF1.request = request;
				 	nF1.setStrEmpId(approvedBy);
	//				System.out.println("CF.getStrEmailLocalHost() is ========= "+CF.getStrEmailLocalHost());
	//				System.out.println("request.getContextPath() is ========= "+request.getContextPath());
				 	nF1.setStrHostAddress(CF.getStrEmailLocalHost());
					nF1.setStrHostPort(CF.getStrHostPort());
				 	nF1.setStrContextPath(request.getContextPath());
					 
				 	nF1.setStrEmpFname(hmEmpInner.get("FNAME"));
				 	nF1.setStrEmpLname(hmEmpInner.get("LNAME"));// Created by Dattatray Date : 30-June-2021
				 	nF1.setStrCandiFname(hmCandiInner.get("FNAME"));
				 	nF1.setStrCandiLname(hmCandiInner.get("LNAME"));
					nF1.setEmailTemplate(true);
				 	nF1.sendNotifications();
				
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	private List<String> getPanelList(String panelID) {
		List<String> al = new ArrayList<String>();

		if (panelID != null && !panelID.equals("")) {
			String[] temp = panelID.split(",");
			// al.add(strSessionEmpId);
			for (int i = 1; i < temp.length; i++) {
				// if(temp[i].contains(strSessionEmpId))
				/*
				 * if(temp[i].trim().equals(strSessionEmpId)){
				 * 
				 * } else{
				 */
				al.add(temp[i].trim());
				// }
			}
		}
		return al;
	}


	private String insertHrInterview(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
//			pst = con.prepareStatement("update candidate_personal_details set candidate_final_status=?,candidate_hr_comments=?, candidate_joining_date=?,ctc_offered=? where emp_per_id=? ");
			pst = con.prepareStatement("update candidate_application_details set candidate_final_status=?,candidate_hr_comments=?, candidate_joining_date=?,candidate_final_status_date=?,aligned_pro_id=? where candidate_id=? and recruitment_id = ?");
			if (hrchoice != null && hrchoice.equals("1")) {
				pst.setInt(1, 1);
			} else {
				pst.setInt(1, -1);
			}
			pst.setString(2, getStrinterviewcommentHR());
			pst.setDate(3, uF.getDateFormat(getJoiningdate(), DATE_FORMAT));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setDouble(4, uF.parseToDouble(getCtcOffer()));
			pst.setInt(5, uF.parseToInt(getStrDesigProjects()));
			pst.setInt(6, uF.parseToInt(getCandID()));
			pst.setInt(7, uF.parseToInt(getRecruitId()));
			pst.executeUpdate();
			pst.close();
			
			pst=con.prepareStatement("delete from candidate_activity_details where recruitment_id=? and candi_id=? and user_id=? and " +
					"activity_id = ?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getCandID()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, CANDI_ACTIVITY_FINALIZE_AND_OFFER_ID);
			pst.executeUpdate();
			pst.close();
			
			pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getCandID()));
//			pst.setInt(3,uF.parseToInt(getRoundID()));
			pst.setString(3, "Finalisation & Offer");
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setDate	(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setInt(6, CANDI_ACTIVITY_FINALIZE_AND_OFFER_ID);
			pst.execute();
			pst.close();
			
			if (hrchoice != null && hrchoice.equals("1")) {
				sendMail();
			}
			
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "returnhr";

	}
	
	
	private void insertEmpSalaryDetailsForPreview(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			//code for getting userid
			
			con = db.makeConnection(con);
			
	//===start parvez date: 11-01-2022===
			/*pst = con.prepareStatement("update candidate_application_details set candidate_joining_date=? where candidate_id=? and recruitment_id = ?");
			pst.setDate(1, uF.getDateFormat(getJoiningdate(), DATE_FORMAT));
			pst.setInt(2, uF.parseToInt(getCandID()));
			pst.setInt(3, uF.parseToInt(getRecruitId()));
			pst.executeUpdate();
			pst.close();*/
			
			boolean isJoiningBonus = CF.getFeatureManagementStatus(request, uF, F_ENABLE_JOINING_BONUS_DETAILS);
			String joiningBonusAmountDetails = "";
			if(isJoiningBonus){
				pst = con.prepareStatement("update candidate_application_details set candidate_joining_date=?,joining_bonus_amount_details=? where candidate_id=? and recruitment_id = ?");
				pst.setDate(1, uF.getDateFormat(getJoiningdate(), DATE_FORMAT));
				if(getStrJoiningBonus() != null && getStrJoiningBonus().length>0){
					joiningBonusAmountDetails = getStrJoiningBonus()[0]+" :: "+getStrJoiningBonusAmount()+" :: "+getStrAdditionalComment();	
				}
					
				pst.setString(2, joiningBonusAmountDetails);
				pst.setInt(3, uF.parseToInt(getCandID()));
				pst.setInt(4, uF.parseToInt(getRecruitId()));
				pst.executeUpdate();
				pst.close();
			} else{
				pst = con.prepareStatement("update candidate_application_details set candidate_joining_date=? where candidate_id=? and recruitment_id = ?");
				pst.setDate(1, uF.getDateFormat(getJoiningdate(), DATE_FORMAT));
				pst.setInt(2, uF.parseToInt(getCandID()));
				pst.setInt(3, uF.parseToInt(getRecruitId()));
				pst.executeUpdate();
				pst.close();
			}
			
			
	//===end parvez date: 11-01-2022===		
			
			
			pst = con.prepareStatement("select level_id from candidate_application_details join recruitment_details using (recruitment_id) where candidate_id= ? and recruitment_id = ?");
//			pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setInt(1, uF.parseToInt(getCandID()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			boolean isCurrentDateExist = false;
			int level_id = 0;
			while(rs.next()){
				isCurrentDateExist = true;
				level_id = rs.getInt("level_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id = ? and (is_delete is null or is_delete=false) order by weight");
			pst.setInt(1, level_id);
//			System.out.println("pst ===> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hmEarningDeductionMap = new HashMap<String, String>();
			while(rs.next()){
				hmEarningDeductionMap.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
			}
			rs.close();
			pst.close();
			
//			if(isCurrentDateExist){
				pst = con.prepareStatement("delete from candidate_salary_details where emp_id = ? and recruitment_id = ? "); //entry_date = ? and 
//				pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				pst.setInt(1, uF.parseToInt(getCandID()));
				pst.setInt(2, uF.parseToInt(getRecruitId()));
				pst.execute();
				pst.close();
//			}
			
			String total_earning_value = request.getParameter("hide_total_earning_value");
			String total_earning_value1 = request.getParameter("total_earning_value");
//			System.out.println("salary_head_id==>"+Arrays.asList(salary_head_id));
			for(int i=0; i<salary_head_id.length; i++) {
				String isDisplay = (String) request.getParameter("isDisplay_"+getSalary_head_id()[i]);
				pst = con.prepareStatement("INSERT INTO candidate_salary_details (emp_id , salary_head_id, amount, entry_date, user_id, pay_type," +
						"service_id, effective_date, earning_deduction,recruitment_id,isdisplay) VALUES (?,?,?,?, ?,?,?,?, ?,?,?)");
				
				pst.setInt(1, uF.parseToInt(getCandID()));
				pst.setInt(2, uF.parseToInt(getSalary_head_id()[i]));
				pst.setDouble(3, uF.parseToDouble(getSalary_head_value()[i]));
				pst.setDate	(4, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				pst.setInt(5, 1);
				pst.setString(6, "M");
				pst.setInt(7, uF.parseToInt("0"));
				pst.setDate	(8, uF.getDateFormat(getJoiningdate(), DATE_FORMAT));
				pst.setString(9, hmEarningDeductionMap.get(getSalary_head_id()[i]));
				pst.setInt(10, uF.parseToInt(getRecruitId()));
				pst.setBoolean(11, uF.parseToBoolean(isDisplay));
//				System.out.println("pst salary heads ==>"+i+"==>"+pst);
				pst.execute();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	private void insertEmpSalaryDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			//code for getting userid
			con = db.makeConnection(con);
			pst = con.prepareStatement("select level_id from candidate_application_details join recruitment_details using (recruitment_id) where candidate_id= ? and recruitment_id = ?");
//			pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setInt(1, uF.parseToInt(getCandID()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			boolean isCurrentDateExist = false;
			int level_id = 0;
			while(rs.next()){
				isCurrentDateExist = true;
				level_id = rs.getInt("level_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id = ? and (is_delete is null or is_delete=false) order by weight");
			pst.setInt(1, level_id);
//			System.out.println("pst ===> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hmEarningDeductionMap = new HashMap<String, String>();
			while(rs.next()){
				hmEarningDeductionMap.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
			}
			rs.close();
			pst.close();
			
//			if(isCurrentDateExist){
				pst = con.prepareStatement("delete from candidate_salary_details where emp_id = ? and recruitment_id = ? "); //entry_date = ? and 
//				pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				pst.setInt(1, uF.parseToInt(getCandID()));
				pst.setInt(2, uF.parseToInt(getRecruitId()));
				pst.execute();
				pst.close();
//			}
			
			String total_earning_value = request.getParameter("hide_total_earning_value");
			String total_earning_value1 = request.getParameter("total_earning_value");
//			System.out.println("salary_head_id==>"+Arrays.asList(salary_head_id));
			for(int i=0; i<salary_head_id.length; i++) {
				String isDisplay = (String) request.getParameter("isDisplay_"+getSalary_head_id()[i]);
				pst = con.prepareStatement("INSERT INTO candidate_salary_details (emp_id , salary_head_id, amount, entry_date, user_id, pay_type," +
						"service_id, effective_date, earning_deduction,recruitment_id,isdisplay) VALUES (?,?,?,?, ?,?,?,?, ?,?,?)");
				
				pst.setInt(1, uF.parseToInt(getCandID()));
				pst.setInt(2, uF.parseToInt(getSalary_head_id()[i]));
				pst.setDouble(3, uF.parseToDouble(getSalary_head_value()[i]));
				pst.setDate	(4, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				pst.setInt(5, 1);
				pst.setString(6, "M");
				pst.setInt(7, uF.parseToInt("0"));
				pst.setDate	(8, uF.getDateFormat(getJoiningdate(), DATE_FORMAT));
				pst.setString(9, hmEarningDeductionMap.get(getSalary_head_id()[i]));
				pst.setInt(10, uF.parseToInt(getRecruitId()));
				pst.setBoolean(11, uF.parseToBoolean(isDisplay));
//				System.out.println("CMPPU/4701--pst="+pst);
				pst.execute();
				pst.close();
			}
			
			pst = con.prepareStatement("update candidate_application_details set is_disable_sal_calculate=? where candidate_id = ? and recruitment_id=? ");
			pst.setBoolean(1, getDisableSalaryStructure());
			pst.setInt(2, uF.parseToInt(getCandID()));
			pst.setInt(3, uF.parseToInt(getRecruitId()));
			pst.execute();
			pst.close();
			
//			String Amount =null;
//			pst=con.prepareStatement("select sum(amount) as amt from candidate_salary_details where emp_id=? and recruitment_id =? and entry_date = ? and earning_deduction='E' ");
//			pst.setInt(1, uF.parseToInt(getCandID()));
//			pst.setInt(2, uF.parseToInt(getRecruitId()));
//			pst.setDate(3, uF.getDateFormat(getJoiningdate(), DATE_FORMAT));
//			System.out.println("pst salary heads ==>"+i+"==>"+pst);
//			rs=pst.executeQuery();
////			System.out.println("new Date ===> " + new Date());
//			if(rs.next()){
//				Amount=rs.getString("amt");
//			}
//			rs.close();
//			pst.close();
			
//			System.out.println("total_earning_value ===> " + total_earning_value);
			
			Map<String, String> hmCandiOffered = getCandiOfferedCTC(con);
			if(hmCandiOffered == null) hmCandiOffered = new HashMap<String, String>();
			pst = con.prepareStatement("update candidate_application_details set ctc_offered=?, annual_ctc_offered=? where candidate_id = ? and recruitment_id = ? ");
			pst.setDouble(1,uF.parseToDouble(hmCandiOffered.get("CANDI_CTC")));
			pst.setDouble(2,uF.parseToDouble(hmCandiOffered.get("CANDI_ANNUAL_CTC")));
			pst.setInt(3, uF.parseToInt(getCandID()));
			pst.setInt(4, uF.parseToInt(getRecruitId()));
//			System.out.println("pst ===> " + pst);
			pst.execute();
			pst.close();
			
		//===start parvez date: 11-01-2022===	
			boolean isJoiningBonus = CF.getFeatureManagementStatus(request, uF, F_ENABLE_JOINING_BONUS_DETAILS);
			String joiningBonusAmountDetails = "";
			if(isJoiningBonus && getStrJoiningBonus() != null && getStrJoiningBonus().length>0){
				pst = con.prepareStatement("update candidate_application_details set ctc_offered=?, annual_ctc_offered=?, joining_bonus_amount_details=? where candidate_id = ? and recruitment_id = ? ");
				pst.setDouble(1,uF.parseToDouble(hmCandiOffered.get("CANDI_CTC")));
				pst.setDouble(2,uF.parseToDouble(hmCandiOffered.get("CANDI_ANNUAL_CTC")));
				joiningBonusAmountDetails = getStrJoiningBonus()[0]+" :: "+getStrJoiningBonusAmount()+" :: "+getStrAdditionalComment();	
				
				pst.setString(3, joiningBonusAmountDetails);
				pst.setInt(4, uF.parseToInt(getCandID()));
				pst.setInt(5, uF.parseToInt(getRecruitId()));
//				System.out.println("pst ===> " + pst);
				pst.execute();
				pst.close();
			} else{
				pst = con.prepareStatement("update candidate_application_details set ctc_offered=?, annual_ctc_offered=? where candidate_id = ? and recruitment_id = ? ");
				pst.setDouble(1,uF.parseToDouble(hmCandiOffered.get("CANDI_CTC")));
				pst.setDouble(2,uF.parseToDouble(hmCandiOffered.get("CANDI_ANNUAL_CTC")));
				pst.setInt(3, uF.parseToInt(getCandID()));
				pst.setInt(4, uF.parseToInt(getRecruitId()));
//				System.out.println("pst ===> " + pst);
				pst.execute();
				pst.close();
			}
		//===end parvez date: 11-01-2022===	
			
			int ServiceNo = uF.parseToInt((String)session.getAttribute("ServicesLinkNo"));
//			session.setAttribute("ServicesLinkNo", (ServiceNo-1)+"");    // Uncomment this code if you wish to use salary cost center wise.
			session.setAttribute("ServicesLinkNo", 1+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	public String[] getEmp_salary_id() {
	return emp_salary_id;
}

public void setEmp_salary_id(String[] emp_salary_id) {
	this.emp_salary_id = emp_salary_id;
}

public String[] getIsDisplay() {
	return isDisplay;
}

public void setIsDisplay(String[] isDisplay) {
	this.isDisplay = isDisplay;
}

	public String[] getSalary_head_id() {
	return salary_head_id;
}

public void setSalary_head_id(String[] salary_head_id) {
	this.salary_head_id = salary_head_id;
}

public String[] getSalary_head_value() {
	return salary_head_value;
}

public void setSalary_head_value(String[] salary_head_value) {
	this.salary_head_value = salary_head_value;
}

	public String viewProfile(String strEmpIdReq,UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		List<List<String>> alSkills = new ArrayList<List<String>>();
		List<List<String>> alHobbies;
		List<List<String>> alLanguages;
		List<List<String>> alEducation;
		List<List<Object>> alDocuments;
		List<List<String>> alFamilyMembers;
		List<List<String>> alPrevEmployment;
		List<List<Object>> alResumes;
		List<List<String>> alCertification;
		//===start parvez date: 08-09-2021===
		Map<String, List<String>> hmEducationDocs;
		//===end parvez date: 08-09-2021===
		// List<List<String>> alActivityDetails;

		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);//Created By Dattatray Date:18-10-21
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			List<String> availableExt = CF.getAvailableExtention();
			request.setAttribute("availableExt",availableExt);
			
			Map<String, List<String>> hmSkills = getCandiSkillsName(con);
			Map<String, List<String>> hmDegrees = getCandiDegreeName(con);
			Map<String, List<String>> hmTotExp = getCandiTotExp(con,uF);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			request.setAttribute("hmEmpName", hmEmpName);
			Map<String, String> hm = new HashMap<String, String>();

			pst = con.prepareStatement("select * from recruitment_details where recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				hm.put("JOB_CODE", rs.getString("job_code"));
				hm.put("DESIG_ID", rs.getString("designation_id"));
			}
			rs.close();
			pst.close();

			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select * from resource_plan_request_details where desig_id=? ");
			pst = con.prepareStatement(strQuery.toString());
			pst.setInt(1, uF.parseToInt(hm.get("DESIG_ID")));
			rs = pst.executeQuery(); 
			StringBuilder sbProIds = null;
			List<String> alProId = new ArrayList<String>();
			while (rs.next()) {
				if(!alProId.contains(rs.getString("pro_id"))) {
					if(sbProIds == null) {
						sbProIds= new StringBuilder();
						sbProIds.append(rs.getString("pro_id"));
					} else {
						sbProIds.append(","+rs.getString("pro_id"));
					}
					alProId.add(rs.getString("pro_id"));
				}
			}
			rs.close();
			pst.close();
			if(sbProIds == null) {
				sbProIds= new StringBuilder();
			}
			
			projectList = new FillProjectList(request).fillProjectDetailsByProjectIds(sbProIds.toString());
			StringBuilder sbDesigProjectList = new StringBuilder();
			for(int i=0; projectList!=null && i<projectList.size(); i++) {
				sbDesigProjectList.append("<option value=\""+projectList.get(i).getProjectID()+"\">"+projectList.get(i).getProjectName()+"</option>");
			}
			request.setAttribute("sbDesigProjectList", sbDesigProjectList.toString());
			
//			offer_negotiation_approval_request_remark,need_approval_for_offer_negotiation,offer_negotiation_approver,offer_negotiation_approval_requested_by,offer_negotiation_approval_request_date,offer_negotiation_request_approve_remark,offer_negotiation_request_approved_by,offer_negotiation_request_approve_date
			Map<String, String> hmOfferNegoReqData = new HashMap<String, String>();
			pst = con.prepareStatement("select * from candidate_application_details where candidate_id=? and recruitment_id=? and (offer_negotiation_approval_requested_by is not null or offer_negotiation_approval_requested_by>0)");
			pst.setInt(1, uF.parseToInt(getCandID()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmOfferNegoReqData.put("REQUESTED_REMARK", rs.getString("offer_negotiation_approval_request_remark"));
				hmOfferNegoReqData.put("NEED_APPROVAL_STATUS", rs.getString("need_approval_for_offer_negotiation"));
				hmOfferNegoReqData.put("NEGOTIATION_APPROVER", hmEmpName.get(rs.getString("offer_negotiation_approver")));
				hmOfferNegoReqData.put("REQUESTED_BY", hmEmpName.get(rs.getString("offer_negotiation_approval_requested_by")));
				hmOfferNegoReqData.put("REQUEST_DATE", rs.getString("offer_negotiation_approval_request_date")!=null ? uF.getDateFormat(rs.getString("offer_negotiation_approval_request_date"), DBDATE, DATE_FORMAT_STR) : null);
				hmOfferNegoReqData.put("APPROVED_REMARK", rs.getString("offer_negotiation_request_approve_remark"));
				hmOfferNegoReqData.put("NEGOTIATION_APPROVE_STATUS", rs.getString("is_negotiation_approve"));
				hmOfferNegoReqData.put("APPROVED_BY", hmEmpName.get(rs.getString("offer_negotiation_request_approved_by")));
				hmOfferNegoReqData.put("APPROVE_DATE", rs.getString("offer_negotiation_request_approve_date")!=null ? uF.getDateFormat(rs.getString("offer_negotiation_request_approve_date"), DBDATE, DATE_FORMAT_STR) : null);
				hmOfferNegoReqData.put("NEGOTIATION_APPROVER_ID", rs.getString("offer_negotiation_approver"));// Created By Dattatray Date : 03-July-21
				hmOfferNegoReqData.put("REQUESTED_BY_ID", rs.getString("offer_negotiation_approval_requested_by"));// Created By Dattatray Date : 08-July-21
			}
			rs.close();
			pst.close();
			request.setAttribute("hmOfferNegoReqData", hmOfferNegoReqData);

			pst = con.prepareStatement("Select * from candidate_personal_details where emp_per_id=?");
			if (strEmpIdReq != null) {
				pst.setInt(1, uF.parseToInt(strEmpIdReq));
			} else {
				pst.setInt(1, uF.parseToInt(getCandID()));
			}
			rs = pst.executeQuery();
			while (rs.next()) {
			
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				
				request.setAttribute(TITLE, rs.getString("emp_fname") +strEmpMName+ " "+ rs.getString("emp_lname") + "'s Profile");
				
				hm.put("CANDI_ID", rs.getString("emp_per_id"));
				hm.put("NAME", uF.showData(rs.getString("emp_fname"),"") +strEmpMName+ " " + uF.showData(rs.getString("emp_lname"),""));
				hm.put("ADDRESS", uF.showData(rs.getString("emp_address1"),"") + " " +uF.showData(rs.getString("emp_address2"),""));
//				hm.put("CITY", rs.getString("emp_state_id").equals("0") ? "-" : rs.getString("emp_state_id"));
				hm.put("STATE", uF.showData(CF.getStateNameById(con, uF, rs.getString("emp_state_id")), "-"));
				hm.put("COUNTRY", uF.showData(CF.getCountryNameById(con, uF, rs.getString("emp_country_id")), "-"));
				hm.put("TMP_ADDRESS", uF.showData(rs.getString("emp_address1_tmp"),"") + " " +uF.showData(rs.getString("emp_address2_tmp"),""));
//				hm.put("TMP_CITY", rs.getString("emp_state_id").equals("0") ? "-" : rs.getString("emp_state_id"));
				hm.put("TMP_STATE", uF.showData(CF.getStateNameById(con, uF, rs.getString("emp_state_id_tmp")), "-"));
				hm.put("TMP_COUNTRY", uF.showData(CF.getCountryNameById(con, uF, rs.getString("emp_country_id_tmp")), "-"));
				hm.put("PINCODE", rs.getString("emp_pincode"));
				hm.put("CONTACT", rs.getString("emp_contactno"));
				hm.put("CONTACT_MOB", rs.getString("emp_contactno_mob"));
				hm.put("IMAGE", rs.getString("emp_image"));
				hm.put("EMAIL", rs.getString("emp_email"));
				String gStatus = "";
				if(rs.getString("emp_gender") != null && rs.getString("emp_gender").equals("M")){
					gStatus = "Male";
				}else if(rs.getString("emp_gender") != null && rs.getString("emp_gender").equals("F")){
					gStatus = "Female";
				}else{
					gStatus = "-";
				}
				hm.put("GENDER", gStatus);
				hm.put("GENDER_FORSTAR", rs.getString("emp_gender"));
				hm.put("DOB", uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, CF.getStrReportDateFormat()));
				String mStatus = "";
				if(rs.getString("marital_status") != null && rs.getString("marital_status").equals("U")){
					mStatus = "Unmarried";
				}else if(rs.getString("marital_status") != null && rs.getString("marital_status").equals("M")){
					mStatus = "Married";
				}else if(rs.getString("marital_status") != null && rs.getString("marital_status").equals("D")){
					mStatus = "Divorced";
				}else if(rs.getString("marital_status") != null && rs.getString("marital_status").equals("W")){
					mStatus = "Widow";
				}else{
					mStatus = "-";
				}
				hm.put("MARITAL_STATUS", mStatus);
				
				hm.put("isaccepted", rs.getString("application_status"));

				hm.put("SUPER_CODE", rs.getString("empcode"));
				hm.put("SUPER_NAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				hm.put("PASSPORT_NO", rs.getString("passport_no"));
				hm.put("PASSPORT_EXPIRY", uF.getDateFormat(rs.getString("passport_expiry_date"), DBDATE, CF.getStrReportDateFormat()));
				hm.put("AVAILABILITY",  uF.showYesNo(rs.getString("availability_for_interview")));
				
				
				hm.put("TOT_EXPERIENCE", uF.showData(rs.getString("total_experience"), ""));
				hm.put("CURRENT_CTC", uF.formatIntoOneDecimal(rs.getDouble("current_ctc")));
				hm.put("EXPECTED_CTC", uF.formatIntoOneDecimal(rs.getDouble("expected_ctc")));
				hm.put("NOTICE_PERIOD", uF.showData(rs.getString("notice_period"), "0") +" days");
				//Start Dattatray Date:23-08-21
				hm.put("CURRENT_LOCATION",rs.getString("current_location") !=null ? uF.showData(rs.getString("current_location"), "-"):CF.getCandidatePrevLocation(con, uF, rs.getString("emp_per_id")));
				hm.put("PREFERRED_LOCATION", CF.getCandidatePreferedLocation(con,rs.getString("applied_location")));
				//End Dattatray Date:23-08-21
			}
			rs.close();
			pst.close();
			
			int intEmpIdReq = uF.parseToInt(strEmpIdReq);

			String candiID = intEmpIdReq+"";
			String candiGender = hm.get("GENDER_FORSTAR");
			String strStars = calculateCandidateStarRating(con, uF, hmSkills.get(candiID), hmDegrees.get(candiID), hmTotExp.get(candiID), candiGender);
			
			request.setAttribute("myProfile", hm);
			request.setAttribute("EmpStars", strStars);

			alSkills = CF.selectCandidateSkills(con, intEmpIdReq);
			alHobbies = CF.selectCandidateHobbies(con, intEmpIdReq);
			alLanguages = CF.selectCandidateLanguages(con, intEmpIdReq);
			alEducation = CF.selectCandidateEducation(con, intEmpIdReq);
			//===start parvez date: 08-09-2021===
			hmEducationDocs = CF.selectCandidateEducationDocument(con, intEmpIdReq);
			//===end parvez date: 08-09-2021===
			alCertification = CF.selectCandidateCertification(con, intEmpIdReq);//Created By Dattatray Date:23-08-21
			selectMedicalDetails(con, intEmpIdReq);

			String filePath = request.getRealPath("/userDocuments/");
			alDocuments = selectDocuments(con, intEmpIdReq, filePath);
			alResumes = selectResumes(con, intEmpIdReq, filePath);
			alFamilyMembers = selectFamilyMembers(con, intEmpIdReq, uF);
			alPrevEmployment = selectPrevEmploment(con, intEmpIdReq, uF);

			/* alActivityDetails = CF.selectEmpActivityDetails(intEmpIdReq, CF); */

			request.setAttribute("alSkills", alSkills);
			request.setAttribute("alHobbies", alHobbies);
			request.setAttribute("alLanguages", alLanguages);
			request.setAttribute("alEducation", alEducation);
			//===start parvez date: 08-09-2021===
			request.setAttribute("hmEducationDocs", hmEducationDocs);
			//===end parvez date: 08-09-2021===
			request.setAttribute("alDocuments", alDocuments);
			request.setAttribute("alResumes", alResumes);
			request.setAttribute("alFamilyMembers", alFamilyMembers);

			request.setAttribute("alPrevEmployment", alPrevEmployment);
			request.setAttribute("alCertification", alCertification);//Created By Dattatray Date:23-08-21
			
		//===start parvez date: 10-01-2022===
			salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads(getCandID(),getRecruitId()); 
		//===end parvez date: 10-01-2022===	
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	String approvedeny;

	public String getApprovedeny() {
		return approvedeny;
	}

	public void setApprovedeny(String approvedeny) {
		this.approvedeny = approvedeny;
	}

	private String panelrating;

	public String getPanelrating() {
		return panelrating;
	}

	public void setPanelrating(String panelrating) {
		this.panelrating = panelrating;
	}

	private String ctcOffer;

	public String getCtcOffer() {
		return ctcOffer;
	}

	public void setCtcOffer(String ctcOffer) {
		this.ctcOffer = ctcOffer;
	}

	private String interviewcomment;
	private File strInterviewDocument;
	private String strInterviewDocumentFileName;
	private String roundID;
	private String strinterviewcommentHR;
	private String hrchoice;
	private String joiningdate;
	private String strDesigProjects;

	
	public String getRoundID() {
		return roundID;
	}

	public void setRoundID(String roundID) {
		this.roundID = roundID;
	}

	public String getHrchoice() {
		return hrchoice;
	}

	public void setHrchoice(String hrchoice) {
		this.hrchoice = hrchoice;

	}

	public String getJoiningdate() {
		return joiningdate;
	}

	public void setJoiningdate(String joiningdate) {
		this.joiningdate = joiningdate;

	}

	public String getStrinterviewcommentHR() {
		return strinterviewcommentHR;
	}

	public void setStrinterviewcommentHR(String strinterviewcommentHR) {
		this.strinterviewcommentHR = strinterviewcommentHR;
	}

	public String getInterviewcomment() {
		return interviewcomment;
	}

	public void setInterviewcomment(String interviewcomment) {
		this.interviewcomment = interviewcomment;
	}

	
	public List<List<String>> selectPrevEmploment(Connection con, int empId, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<String>> alPrevEmployment = new ArrayList<List<String>>();
		
		try {

			pst = con.prepareStatement("SELECT * FROM candidate_prev_employment WHERE emp_id = ? order by from_date");
			pst.setInt(1, empId);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("company_id"));
				alInner.add(rs.getString("company_name"));
				alInner.add(rs.getString("company_location"));
				alInner.add(rs.getString("company_city"));
				alInner.add(rs.getString("company_state"));
				alInner.add(rs.getString("company_country"));
				alInner.add(rs.getString("company_contact_no"));
				alInner.add(rs.getString("reporting_to"));
				alInner.add(uF.getDateFormat(rs.getString("from_date"), DBDATE,DATE_FORMAT));
				alInner.add(uF.getDateFormat(rs.getString("to_date"), DBDATE,DATE_FORMAT));
				alInner.add(rs.getString("designation"));
				alInner.add(rs.getString("responsibilities"));
				alInner.add(rs.getString("skills"));
			//===start parvez date: 08-08-2022===	
				alInner.add(rs.getString("emp_esic_no"));
				alInner.add(rs.getString("uan_no"));
			//===end parvez date: 08-08-2022===	
				alPrevEmployment.add(alInner);
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

		return alPrevEmployment;
	}

	public List<List<String>> selectFamilyMembers(Connection con, int empId, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<String>> alFamilyMembers = new ArrayList<List<String>>();
		
		try {
			pst = con.prepareStatement("SELECT * FROM candidate_family_members WHERE emp_id = ? order by member_type");
			pst.setInt(1, empId);

			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {

				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("member_id"));
				alInner.add(rs.getString("member_name"));
				alInner.add(uF.getDateFormat(rs.getString("member_dob"),DBDATE, DATE_FORMAT));
				alInner.add(rs.getString("member_education"));
				alInner.add(rs.getString("member_occupation"));
				alInner.add(rs.getString("member_contact_no"));
				alInner.add(rs.getString("member_email_id"));
				alInner.add(uF.charMappingMaleFemale(rs.getString("member_gender")));
				alInner.add(rs.getString("member_type"));
				alFamilyMembers.add(alInner);
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

		return alFamilyMembers;

	}

	public List<List<Object>> selectDocuments(Connection con, int empId, String filePath) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<Object>> alDocuments = new ArrayList<List<Object>>();

		try {

			pst = con.prepareStatement("SELECT * FROM candidate_documents_details where emp_id = ?");
			pst.setInt(1, empId);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				ArrayList<Object> alInner1 = new ArrayList<Object>();
				alInner1.add(rs.getInt("documents_id") + "");
				alInner1.add(rs.getString("documents_name"));
				alInner1.add(rs.getString("documents_type"));
				alInner1.add(rs.getInt("emp_id") + "");
				File fileName = new File(rs.getString("documents_file_name") != null ? rs.getString("documents_file_name") : "");

				alInner1.add(fileName);
				alDocuments.add(alInner1);
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

		return alDocuments;

	}

	public List<List<Object>> selectResumes(Connection con, int empId, String filePath) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> hmCandNameMap = CF.getCandNameMap(con, null,null); 
		List<List<Object>> alResumes = new ArrayList<List<Object>>();
		try {

			pst = con.prepareStatement("SELECT * FROM candidate_documents_details where emp_id = ? and documents_type =? ");
			pst.setInt(1, empId);
			pst.setString(2, "Resume");
			rs = pst.executeQuery();
			
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				
					ArrayList<Object> alInner1 = new ArrayList<Object>();
					alInner1.add(rs.getInt("documents_id") + "");
					alInner1.add(rs.getString("documents_name"));
					alInner1.add(rs.getString("documents_type"));
					alInner1.add(rs.getInt("emp_id") + "");
		
					File fileName = new File(rs.getString("documents_file_name") != null ? rs.getString("documents_file_name") : "");
					alInner1.add(fileName);
					String extenstion = null;
					if(rs.getString("documents_file_name") !=null && !rs.getString("documents_file_name").trim().equals("")){
						extenstion = FilenameUtils.getExtension(rs.getString("documents_file_name").trim());
					}
					alInner1.add(extenstion);//5
					
					alResumes.add(alInner1);
				
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmCandNameMap", hmCandNameMap);

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
		return alResumes;
	}
	
	public List<List<String>> selectMedicalDetails(Connection con, int empId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<String>> alAMedicalDetails=new ArrayList<List<String>>();
		try {
			pst = con.prepareStatement("select * from candidate_medical_details where emp_id = ?");
			pst.setInt(1, empId);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rs.next()){
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("yes_no"));
				innerList.add(rs.getString("description"));
				innerList.add(rs.getString("filepath"));
				alAMedicalDetails.add(innerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alAMedicalDetails",alAMedicalDetails);
			
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

		return alAMedicalDetails;

	}
	
	
	/**
	 * Created By Dattatray
	 * @since 11-08-21
	 * @param notificationCode
	 */
	public void sendMailRound(int notificationCode) {

		Connection con = null;
		ResultSet rst = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmCandiInfo = getCandiInfoMap(con, false);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
//			System.out.println("hmCandiInfo ===>>>> " +hmCandiInfo);
				Map<String, String> hmCandiInner = hmCandiInfo.get(getCandID());
//				System.out.println("getCandidateId() =====>> " + getCandID() + "  hmCandiInner =====>> " +hmCandiInner);
				Map<String, String> hmCandiDesig = new HashMap<String, String>();
				
				pst = con.prepareStatement("select e.emp_per_id,d.designation_code,d.designation_name from recruitment_details r," +
						"designation_details d,candidate_personal_details e where r.recruitment_id = e.recruitment_id and " +
						"r.designation_id=d.designation_id and emp_per_id = ?");
				pst.setInt(1, uF.parseToInt(getCandID()));
//				System.out.println("pst : "+pst.toString());
				rst = pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
				while(rst.next()) {
					hmCandiDesig.put(rst.getString("emp_per_id"), rst.getString("designation_name"));
				}
				rst.close();
				pst.close();
				
				String joining_date = "";
				String job_title = "";
				String recruiter_name = "";
				pst = con.prepareStatement("select cad.candidate_joining_date,job_title,hiring_manager from candidate_application_details cad,recruitment_details rd where rd.recruitment_id =cad.recruitment_id and  cad.candidate_id = ? and cad.recruitment_id = ?");
				pst.setInt(1, uF.parseToInt(getCandID()));
				pst.setInt(2, uF.parseToInt(getRecruitId()));
				rst = pst.executeQuery();
				while(rst.next()) {
					joining_date = rst.getString("candidate_joining_date");
					job_title = rst.getString("job_title");
					recruiter_name = uF.showData(getAppendData(rst.getString("hiring_manager"), hmEmpName), "");
				}
				rst.close();
				pst.close();
				
				pst = con.prepareStatement("select * from document_signature where org_id =?");
				pst.setInt(1, uF.parseToInt(strEmpOrgId));
				rst = pst.executeQuery();
				String strAuthSign = null;
				String strHrSign = null;
				String strRecruiterSign = null;
				while (rst.next()) {
					if(rst.getInt("signature_type") == 1) {
						strAuthSign = rst.getString("signature_image");
					}
					if(rst.getInt("signature_type") == 2) {
						strHrSign = rst.getString("signature_image");
					}
					if(rst.getInt("signature_type") == 3) {
						if(rst.getInt("user_id") == uF.parseToInt(strEmpOrgId)) {
							strRecruiterSign = rst.getString("signature_image");
						}
					}
				}
				rst.close();
				pst.close();
//				System.out.println("candidate_joining_date : "+joining_date);
//				System.out.println("getCandidateId() ========= "+getCandID());
				List<String> roundIdsRecruitwiseList = (List<String>)request.getAttribute("roundIdsRecruitwiseList");
				String strDomain = request.getServerName().split("\\.")[0];
				CandidateNotifications nF = new CandidateNotifications(notificationCode, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrEmpId(getCandID());
				nF.setStrRecruitmentId(getRecruitId());
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				 
				nF.setStrCandiFname(hmCandiInner.get("FNAME"));
				nF.setStrCandiLname(hmCandiInner.get("LNAME"));
				nF.setStrRecruitmentDesignation(hmCandiDesig.get(getCandID()));
				nF.setStrJobTitle(job_title);
				nF.setStrRecruiterName(recruiter_name);
//				System.out.println("getRoundID : "+getRoundID());
				nF.setStrPreRoundNo(uF.parseToInt(getRoundID())+"");
				if(uF.parseToInt(getRoundID()) < roundIdsRecruitwiseList.size()) {
					int nextRound = uF.parseToInt(getRoundID())+1;
					nF.setStrNextRoundNo(nextRound+"");
					nF.setStrInterviewTime(getInterviewTime(nextRound+"", getCandID(), getRecruitId(),uF));
				}
				String imageUrl=CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+strEmpOrgId+"/"+I_DOC_SIGN+"/"+strSessionEmpId+"/"+strRecruiterSign;
				String strSignature ="<img src=\""+imageUrl+"\">";
				nF.setStrRecruiterSignature(strSignature);
				nF.sendNotifications();
			 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	/**
	 * Created By Dattatray
	 * @since 11-08-21
	 * @param roundId
	 * @param candiId
	 * @param recruitID
	 * @param uF
	 * @return
	 */
	private String getInterviewTime(String roundId, String candiId,String recruitID,UtilityFunctions uF){
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		String str = "";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select DISTINCT(panel_round_id),recruitment_id,interview_time from candidate_interview_panel where candidate_id = ? and recruitment_id =? order by panel_round_id");
			pst.setInt(1, uF.parseToInt(candiId));
			pst.setInt(2, uF.parseToInt(recruitID));
			rs = pst.executeQuery();
			while (rs.next()) {
				str = uF.getDateFormat(rs.getString("interview_time"), DBTIME, TIME_FORMAT);
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
		return str;
	}
	/**
	 * Created By Dattatray
	 * @since 11-08-21
	 * @param strID
	 * @param mp
	 * @return
	 */
	private String getAppendData( String strID,  Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
//		System.out.println("strID :: "+strID);
		if (strID != null && !strID.equals("") && !strID.isEmpty()) {
			if(strID.length()>0 && strID.substring(0, 1).equals(",") && strID.substring(strID.length()-1, strID.length()).equals(",")){
			strID = strID.substring(1, strID.length()-1);
			}
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
	
	/**
	 * Created By Dattatray
	 * @since 11-08-21
	 * @param con
	 * @param isFamilyInfo
	 * @return
	 */
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
	
	public String getCandID() {
		return CandID;
	}

	public void setCandID(String candID) {
		CandID = candID;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getApptype() {
		return apptype;
	}

	public void setApptype(String apptype) {
		this.apptype = apptype;
	}
	
	public boolean getDisableSalaryStructure() {
		return disableSalaryStructure;
	}

	public void setDisableSalaryStructure(boolean disableSalaryStructure) {
		this.disableSalaryStructure = disableSalaryStructure;
	}

	public String getCandiApplicationId() {
		return candiApplicationId;
	}

	public void setCandiApplicationId(String candiApplicationId) {
		this.candiApplicationId = candiApplicationId;
	}

	public File getStrInterviewDocument() {
		return strInterviewDocument;
	}

	public void setStrInterviewDocument(File strInterviewDocument) {
		this.strInterviewDocument = strInterviewDocument;
	}

	public String getStrInterviewDocumentFileName() {
		return strInterviewDocumentFileName;
	}

	public void setStrInterviewDocumentFileName(String strInterviewDocumentFileName) {
		this.strInterviewDocumentFileName = strInterviewDocumentFileName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
	}

	public List<FillJD> getLiveJDList() {
		return liveJDList;
	}

	public void setLiveJDList(List<FillJD> liveJDList) {
		this.liveJDList = liveJDList;
	}

	public String getOfferReleaseCommunication() {
		return offerReleaseCommunication;
	}

	public void setOfferReleaseCommunication(String offerReleaseCommunication) {
		this.offerReleaseCommunication = offerReleaseCommunication;
	}

	public String getNeedApprovalForOfferRelease() {
		return needApprovalForOfferRelease;
	}

	public void setNeedApprovalForOfferRelease(String needApprovalForOfferRelease) {
		this.needApprovalForOfferRelease = needApprovalForOfferRelease;
	}

	public String getStrOCApprover() {
		return strOCApprover;
	}

	public void setStrOCApprover(String strOCApprover) {
		this.strOCApprover = strOCApprover;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String getOfferNegotiationApproval() {
		return offerNegotiationApproval;
	}

	public void setOfferNegotiationApproval(String offerNegotiationApproval) {
		this.offerNegotiationApproval = offerNegotiationApproval;
	}

	public String getNeedApproveRequestForOfferRelease() {
		return needApproveRequestForOfferRelease;
	}

	public void setNeedApproveRequestForOfferRelease(String needApproveRequestForOfferRelease) {
		this.needApproveRequestForOfferRelease = needApproveRequestForOfferRelease;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<FillProjectList> getProjectList() {
		return projectList;
	}

	public void setProjectList(List<FillProjectList> projectList) {
		this.projectList = projectList;
	}

	public String getStrDesigProjects() {
		return strDesigProjects;
	}

	public void setStrDesigProjects(String strDesigProjects) {
		this.strDesigProjects = strDesigProjects;
	}

	HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

//===start parvez date: 10-01-2022===	
	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}
	
	public String[] getStrJoiningBonus() {
		return strJoiningBonus;
	}

	public void setStrJoiningBonus(String[] strJoiningBonus) {
		this.strJoiningBonus = strJoiningBonus;
	}

	public String getStrJoiningBonusAmount() {
		return strJoiningBonusAmount;
	}

	public void setStrJoiningBonusAmount(String strJoiningBonusAmount) {
		this.strJoiningBonusAmount = strJoiningBonusAmount;
	}

	public String getStrAdditionalComment() {
		return strAdditionalComment;
	}

	public void setStrAdditionalComment(String strAdditionalComment) {
		this.strAdditionalComment = strAdditionalComment;
	}
//===end parvez date: 10-01-2022===	

}