package com.konnect.jpms.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

public class CandidateNotifications extends Thread implements IStatements, ServletRequestAware {

	public CandidateNotifications(int N_NEW_EMPLOYEE) {
		setnNotificationCode(N_NEW_EMPLOYEE);
	}
	public CommonFunctions CF;
	public CandidateNotifications(int N_NEW_EMPLOYEE, CommonFunctions CF) {
		setnNotificationCode(N_NEW_EMPLOYEE);
		this.CF = CF;
	}

	public void sendNotifications() {
		if (!isAlive()) {
			start();
		}
	}

	boolean isTextNotifications = false;
	boolean isEmailNotifications = false;

	boolean isRequiredAuthentication;
	String strEmailAuthUsername;
	String strEmailAuthPassword;

	// Start Dattatray Date : 02-July-2021
	private boolean isEmailIdCC;
	String strEmailIdCC;
	
	//===start parvez date: 30-10-2021===
	boolean isEmailIdHireTeam;
	
	public void setIsEmailIdHireTeam(boolean isEmailIdHireTeam) {
		this.isEmailIdHireTeam = isEmailIdHireTeam;
	}

	public boolean getIsEmailIdHireTeam() {
		return isEmailIdHireTeam;
	}
	
	public boolean getIsEmailIdCC() {
		return isEmailIdCC;
	}

	public void setIsEmailIdCC(boolean isEmailIdCC) {
//		System.out.println("CN/65--isEmailIdCC"+isEmailIdCC);
		this.isEmailIdCC = isEmailIdCC;
		
	}
	//===end parvez date: 30-10-2021===
	
	//===start parvez date: 02-11-2021===
	String strHiringTeamEmailId;
	
	public String getStrHiringTeamEmailId() {
		return strHiringTeamEmailId;
	}

	public void setStrHiringTeamEmailId(String strHiringTeamEmailId) {
		this.strHiringTeamEmailId = strHiringTeamEmailId;
	}
	//===end parvez date: 02-11-2021===


	public String getStrEmailIdCC() {
		return strEmailIdCC;
	}

	public void setStrEmailIdCC(String strEmailIdCC) {
		this.strEmailIdCC = strEmailIdCC;
	}
	// End Dattatray Date : 02-July-2021

	public void run() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		db.setDomain(strDomain);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			// System.out.println("IN sendNotifications Run");
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectSettings);
			rst = pst.executeQuery();
			// System.out.println("IN sendNotifications run pst =====> "+pst);
			while (rst.next()) {

				if (rst.getString("options").equalsIgnoreCase(O_EMAIL_NOTIFICATIONS)) {
					isEmailNotifications = uF.parseToBoolean(rst.getString("value"));
				} else if (rst.getString("options").equalsIgnoreCase(O_TEXT_NOTIFICATIONS)) {
					isTextNotifications = uF.parseToBoolean(rst.getString("value"));
				} else if (rst.getString("options").equalsIgnoreCase(O_EMAIL_HOST)) {
					setStrHost(rst.getString("value"));
				} else if (rst.getString("options").equalsIgnoreCase(O_EMAIL_FROM)) {
					setStrEmailFrom(rst.getString("value"));
				} else if (rst.getString("options").equalsIgnoreCase(O_TEXT_FROM)) {
					setStrTextFrom(rst.getString("value"));
				} else if (rst.getString("options").equalsIgnoreCase(O_HOST_PASSWORD)) {
					setStrHostPassword(rst.getString("value"));
				} else if (rst.getString("options").equalsIgnoreCase(O_IS_REQUIRED_AUTHENTICATION)) {
					setIsRequiredAuthentication(uF.parseToBoolean(rst.getString("value")));
				} else if (rst.getString("options").equalsIgnoreCase(O_EMAIL_AUTHENTICATION_USER)) {
					setStrEmailAuthUsername(rst.getString("value"));
				} else if (rst.getString("options").equalsIgnoreCase(O_EMAIL_AUTHENTICATION_PASSWORD)) {
					setStrEmailAuthPassword(rst.getString("value"));
				} else if (rst.getString("options").equalsIgnoreCase(O_EMAIL_ID_CC)) {// Created By Dattatray Date : 02-July-2021
					setStrEmailIdCC(rst.getString("value"));
//					System.out.println("CN/122--setStrEmailIdCC="+rst.getString("value"));
				//===start parvez date: 02-11-2021===		
				} else if (rst.getString("options").equalsIgnoreCase(O_HIRING_TEAM_EMAIL_ID)) {
					setStrHiringTeamEmailId(rst.getString("value"));
				//===end parvez date: 02-11-2021===
				}
			}
			rst.close();
			pst.close();
//			System.out.println("getStrHiringTeamEmailId() ===>> " + getStrHiringTeamEmailId());
			
//			getNotificationsMessage1(con);
			
			
//			System.out.println("isEmailNotifications =====> " + isEmailNotifications);
//			System.out.println("isTextNotifications =====> " + isTextNotifications);
			int x = 0;
			if (isEmailNotifications || isTextNotifications) {
				x = getNotificationsMessage(con, uF);
				// System.out.println("X == ===> " + x);
			}

			if (isEmailNotifications && x > 0) {
				// System.out.println("X > 0 ===> " + x);
				sendEmailNotifications();
			}

			if (isTextNotifications) {
				Way2Sms sms = new Way2Sms();
				sms.sendSMS(getStrCandiMobileNo(), getStrTextContent());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	/*private String getNotificationsMessage1( Connection con) {
		String docFileName=null;
//		 Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		 Database db = new Database();
		 db.setRequest(request);
		// db.setDomain(strDomain);
		 UtilityFunctions uF = new UtilityFunctions();
		int x = 0;
		try {

//			 con = db.makeConnection(con);
			
			pst = con.prepareStatement("SELECT * FROM candidate_documents_details WHERE emp_id = ? and documents_name='Resume'");
			pst.setInt(1, uF.parseToInt(getStrCandidateId()));
//			pst.setString(2, getIdDocName0());
			System.out.println("pst : "+pst);
			rst = pst.executeQuery();
			
			while(rst.next()) {
				docFileName = rst.getString("documents_file_name");
			}
			rst.close();
			pst.close();
			
			
			if(CF.getStrDocRetriveLocation() == null){
				System.out.println("CN/195--If Condition");
				setStrAttachmentFileSource(DOCUMENT_LOCATION+docFileName.trim());
			} else {
				System.out.println("CN/198--else Condition");
				setStrAttachmentFileSource(CF.getStrDocRetriveLocation() + I_CANDIDATE + "/" + I_DOCUMENT + "/" + I_ATTACHMENT + "/"+ getStrCandidateId()+ "/" + docFileName.trim());
			}
			
			System.out.println("x ===>> " +getStrAttachmentFileSource());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

		return docFileName;
	}*/
	
	private int getNotificationsMessage(Connection con, UtilityFunctions uF) {

		// Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		// Database db = new Database();
		// db.setRequest(request);
		// db.setDomain(strDomain);
		// UtilityFunctions uF = new UtilityFunctions();
		int x = 0;
		try {

			// con = db.makeConnection(con);
			pst = con.prepareStatement(selectNotifications);
			pst.setInt(1, getnNotificationCode());
			rst = pst.executeQuery();
			// System.out.println("pst candi notification
			// getNotificationsMessage ====> " + pst);
			while (rst.next()) {

				setStrEmailBody(rst.getString("email_notification"));
				setStrEmailSubject(rst.getString("email_subject"));
				setStrTextContent(rst.getString("text_notification"));
				isEmailNotifications = uF.parseToBoolean(rst.getString("isemail"));
				isTextNotifications = uF.parseToBoolean(rst.getString("istext"));
				x++;
			}
			rst.close();
			pst.close();
			// System.out.println("x ===>> " + x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		/*
			 * finally { db.closeResultSet(rst); db.closeStatements(pst);
			 * db.closeConnection(con); }
			 */
		if (x == 0) {
//			System.out.println("Notification constant not defined..");
			return x;
		}

		parseContent(uF.showData(getStrEmailBody(), ""), uF.showData(getStrTextContent(), ""), uF.showData(getStrEmailSubject(), ""));
		return x;
	}

	public Map<String, String> parseContent(String strEmailBody, String strTextContent, String strSubject) {

		UtilityFunctions uF = new UtilityFunctions();
		/*
		 * Connection con = null; Database db = new Database();
		 * db.setRequest(request); db.setDomain(strDomain); Map<String, String>
		 * hmFeatureStatus = new HashMap<String, String>(); try { con =
		 * db.makeConnection(con); if (CF == null) CF = new
		 * CommonFunctions(request); hmFeatureStatus =
		 * CF.getFeatureStatusMap(con, request); } catch (Exception e) { //
		 * TODO: handle exception e.printStackTrace(); } finally {
		 * db.closeConnection(con); }
		 */
		
		if (strEmailBody != null) {

			// String strLink = "http://"+strHostAddress+":8080"+strContextPath;
			// String strLink = "http://localhost:8080"+strContextPath;
			String strPort = strHostPort != null && !strHostPort.equals("") ? ":" + strHostPort : "";
			String strLink = "https://" + strHostAddress + strPort; // +strContextPath
			String strLoginAction = "/Login.action";
			String strAddCandidateAction = "/AddCandidate.action";
			String strOfferAcceptAction = "/OfferAcceptAndRenegotiate.action";
			String strOnboardingAction = "/OnboardingFromCandidate.action";
			String strAddCandiByCandiAction = "/AddCandidateByCadi.action";
//			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
//			if (hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();//Created By Dattatray Date:01-11-21
//			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
//			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
			if (hmFeatureUserTypeId == null)
				hmFeatureUserTypeId = new HashMap<String, List<String>>();
			if (hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_INSTANCE_NAMEWISE_FEATURE))
					&& hmFeatureUserTypeId.get(F_INSTANCE_NAMEWISE_FEATURE + "_USER_IDS") != null
					&& hmFeatureUserTypeId.get(F_INSTANCE_NAMEWISE_FEATURE + "_USER_IDS").contains(INTELIMENT)) {
				strAddCandiByCandiAction = "/AddCandidateByCadiNew.action";
			}
			String strCandiTakeAssessmentAction = "/CandidateTakeAssessment.action";

			strEmailBody = strEmailBody.replace(R_LOGIN_LINK, strLink + strLoginAction);

			if (getStrCandiCode() != null && !getStrCandiCode().equals("")) {
				strEmailBody = strEmailBody.replace(R_CANDICODE, uF.showData(getStrCandiCode(), ""));
				strSubject = strSubject.replace(R_CANDICODE, uF.showData(getStrCandiCode(), ""));
				// strTextContent = strTextContent.replace(R_CANDICODE,
				// uF.showData(getStrCandiCode(), ""));
			}

			// if(getStrCandiSalutation()!=null){
			strEmailBody = strEmailBody.replace(R_CANDISALUTATION, uF.showData(getStrCandiSalutation(), ""));
			strSubject = strSubject.replace(R_CANDISALUTATION, uF.showData(getStrCandiSalutation(), ""));
			strTextContent = strTextContent.replace(R_CANDISALUTATION, uF.showData(getStrCandiSalutation(), ""));
			// }
			//Started By Dattatray Date : 05-10-21
			if (getStrCandidateId() != null) {
				strEmailBody = strEmailBody.replace(R_CANDIDATE_ID, uF.showData(getStrCandidateId(), ""));
				strSubject = strSubject.replace(R_CANDIDATE_ID, uF.showData(getStrCandidateId(), ""));
				strTextContent = strTextContent.replace(R_CANDIDATE_ID, uF.showData(getStrCandidateId(), ""));
			}//End By Dattatray Date : 05-10-21
			if (getStrCandiFname() != null) {
				strEmailBody = strEmailBody.replace(R_CANDIFNAME, uF.showData(getStrCandiFname(), ""));
				strSubject = strSubject.replace(R_CANDIFNAME, uF.showData(getStrCandiFname(), ""));
				strTextContent = strTextContent.replace(R_CANDIFNAME, uF.showData(getStrCandiFname(), ""));
			}
			if (getStrCandiLname() != null) {
				strEmailBody = strEmailBody.replace(R_CANDILNAME, uF.showData(getStrCandiLname(), ""));
				strSubject = strSubject.replace(R_CANDILNAME, uF.showData(getStrCandiLname(), ""));
				strTextContent = strTextContent.replace(R_CANDILNAME, uF.showData(getStrCandiLname(), ""));
			}
			if (getStrCandiEmail() != null) {
				strEmailBody = strEmailBody.replace(R_CANDI_EMAIL_ID, uF.showData(getStrCandiEmail(), ""));
				strSubject = strSubject.replace(R_CANDI_EMAIL_ID, uF.showData(getStrCandiEmail(), ""));
				strTextContent = strTextContent.replace(R_CANDI_EMAIL_ID, uF.showData(getStrCandiEmail(), ""));
			}
			if (getStrCandiMobileNo() != null) {
				strEmailBody = strEmailBody.replace(R_CANDI_CONTACT_NO, uF.showData(getStrCandiMobileNo(), ""));
				strSubject = strSubject.replace(R_CANDI_CONTACT_NO, uF.showData(getStrCandiMobileNo(), ""));
				strTextContent = strTextContent.replace(R_CANDI_CONTACT_NO, uF.showData(getStrCandiMobileNo(), ""));
			}
			// if(getStrCandiAddress()!=null){
			strEmailBody = strEmailBody.replace(R_CANDIADDRESS, uF.showData(getStrCandiAddress(), ""));
			strSubject = strSubject.replace(R_CANDIADDRESS, uF.showData(getStrCandiAddress(), ""));
			strTextContent = strTextContent.replace(R_CANDIADDRESS, uF.showData(getStrCandiAddress(), ""));
			// }
			if (getStrCandiDesignation() != null) {
				strEmailBody = strEmailBody.replace(R_DESIGNATION, uF.showData(getStrCandiDesignation(), ""));
				strSubject = strSubject.replace(R_DESIGNATION, uF.showData(getStrCandiDesignation(), ""));
				strTextContent = strTextContent.replace(R_DESIGNATION, uF.showData(getStrCandiDesignation(), ""));
			}

			if (getStrUserName() != null) {
				strEmailBody = strEmailBody.replace(R_USERNAME, uF.showData(getStrUserName(), ""));
				strSubject = strSubject.replace(R_USERNAME, uF.showData(getStrUserName(), ""));
				strTextContent = strTextContent.replace(R_USERNAME, uF.showData(getStrUserName(), ""));
			}
			if (getStrPassword() != null) {
				strEmailBody = strEmailBody.replace(R_PASSWORD, uF.showData(getStrPassword(), ""));
				strSubject = strSubject.replace(R_PASSWORD, uF.showData(getStrPassword(), ""));
				strTextContent = strTextContent.replace(R_PASSWORD, uF.showData(getStrPassword(), ""));
			}

			if (getStrNewPassword() != null) {
				strEmailBody = strEmailBody.replace(R_NEW_PASSWORD, uF.showData(getStrNewPassword(), ""));
				strSubject = strSubject.replace(R_NEW_PASSWORD, uF.showData(getStrNewPassword(), ""));
				strTextContent = strTextContent.replace(R_NEW_PASSWORD, uF.showData(getStrNewPassword(), ""));
			}

			if (getStrNewEmailFrom() != null) {
				strEmailBody = strEmailBody.replace(MAIL_FROM, uF.showData(getStrNewEmailFrom(), ""));
				strSubject = strSubject.replace(MAIL_FROM, uF.showData(getStrNewEmailFrom(), ""));
				strTextContent = strTextContent.replace(MAIL_FROM, uF.showData(getStrNewEmailFrom(), ""));
			}
			if (getStrNewEmailTo() != null) {
				strEmailBody = strEmailBody.replace(MAIL_TO, uF.showData(getStrNewEmailTo(), ""));
				strSubject = strSubject.replace(MAIL_TO, uF.showData(getStrNewEmailTo(), ""));
				strTextContent = strTextContent.replace(MAIL_TO, uF.showData(getStrNewEmailTo(), ""));
			}
			if (getStrNewEmailSubject() != null) {
				strEmailBody = strEmailBody.replace(MAIL_SUBJECT, uF.showData(getStrNewEmailSubject(), ""));
				strSubject = strSubject.replace(MAIL_SUBJECT, uF.showData(getStrNewEmailSubject(), ""));
				strTextContent = strTextContent.replace(MAIL_SUBJECT, uF.showData(getStrNewEmailSubject(), ""));
			}
			if (getStrNewEmailBody() != null) {
				strEmailBody = strEmailBody.replace(MAIL_BODY, uF.showData(getStrNewEmailBody(), ""));
				strSubject = strSubject.replace(MAIL_BODY, uF.showData(getStrNewEmailBody(), ""));
				strTextContent = strTextContent.replace(MAIL_BODY, uF.showData(getStrNewEmailBody(), ""));
			}

			if (getStrDate() != null) {
				strEmailBody = strEmailBody.replace(DATE, uF.showData(getStrDate(), ""));
				strSubject = strSubject.replace(DATE, uF.showData(getStrDate(), ""));
				strTextContent = strTextContent.replace(DATE, uF.showData(getStrDate(), ""));
			}

			if (getStrCandiJoiningDate() != null) {
				strEmailBody = strEmailBody.replace(CANDI_JOINING_DATE, uF.showData(getStrCandiJoiningDate(), ""));
				strSubject = strSubject.replace(CANDI_JOINING_DATE, uF.showData(getStrCandiJoiningDate(), ""));
				strTextContent = strTextContent.replace(CANDI_JOINING_DATE, uF.showData(getStrCandiJoiningDate(), ""));
			}

			if (getStrOfferAcceptanceLastDate() != null) {
				strEmailBody = strEmailBody.replace(OFFER_ACCEPTANCE_LAST_DATE, uF.showData(getStrOfferAcceptanceLastDate(), ""));
				strSubject = strSubject.replace(OFFER_ACCEPTANCE_LAST_DATE, uF.showData(getStrOfferAcceptanceLastDate(), ""));
				strTextContent = strTextContent.replace(OFFER_ACCEPTANCE_LAST_DATE, uF.showData(getStrOfferAcceptanceLastDate(), ""));
			}

			// System.out.println("getStrCandiCTC() ===>>>> " +
			// getStrCandiCTC());
			if (getStrCandiCTC() != null) {
				// System.out.println("getStrCandiCTC() in if ===>>>> " +
				// getStrCandiCTC());
				strEmailBody = strEmailBody.replace(CANDI_CTC, uF.showData(getStrCandiCTC(), ""));
				strSubject = strSubject.replace(CANDI_CTC, uF.showData(getStrCandiCTC(), ""));
				strTextContent = strTextContent.replace(CANDI_CTC, uF.showData(getStrCandiCTC(), ""));
			}

			if (getStrCandiCTCInWords() != null) {
				// System.out.println("getStrCandiCTCInWords() in if ===>>>> " +
				// getStrCandiCTCInWords());
				strEmailBody = strEmailBody.replace(CANDI_CTC_WORDS, uF.showData(getStrCandiCTCInWords(), ""));
				strSubject = strSubject.replace(CANDI_CTC_WORDS, uF.showData(getStrCandiCTCInWords(), ""));
				strTextContent = strTextContent.replace(CANDI_CTC_WORDS, uF.showData(getStrCandiCTCInWords(), ""));
			}

			if (getStrCandiAnnualCTC() != null) {
				// System.out.println("getStrCandiCTC() in if ===>>>> " +
				// getStrCandiCTC());
				strEmailBody = strEmailBody.replace(CANDI_ANNUAL_CTC, uF.showData(getStrCandiAnnualCTC(), ""));
				strSubject = strSubject.replace(CANDI_ANNUAL_CTC, uF.showData(getStrCandiAnnualCTC(), ""));
				strTextContent = strTextContent.replace(CANDI_ANNUAL_CTC, uF.showData(getStrCandiAnnualCTC(), ""));
			}

			// ---- Recruitment ----
			// System.out.println("getStrRecruitmentDesignation ===>> " +
			// getStrRecruitmentDesignation());
			if (getStrRecruitmentDesignation() != null) {
				strEmailBody = strEmailBody.replace(RECRUITMENT_DESIG, uF.showData(getStrRecruitmentDesignation(), ""));
				// System.out.println("getStrRecruitmentDesignation in body
				// ===>> " + getStrRecruitmentDesignation());
				strSubject = strSubject.replace(RECRUITMENT_DESIG, uF.showData(getStrRecruitmentDesignation(), ""));
				strTextContent = strTextContent.replace(RECRUITMENT_DESIG, uF.showData(getStrRecruitmentDesignation(), ""));
			}
			if (getStrRecruitmentGrade() != null) {
				strEmailBody = strEmailBody.replace(RECRUITMENT_GRADE, uF.showData(getStrRecruitmentGrade(), ""));
				strSubject = strSubject.replace(RECRUITMENT_GRADE, uF.showData(getStrRecruitmentGrade(), ""));
				strTextContent = strTextContent.replace(RECRUITMENT_GRADE, uF.showData(getStrRecruitmentGrade(), ""));
			}
			if (getStrRecruitmentLevel() != null) {
				strEmailBody = strEmailBody.replace(RECRUITMENT_LEVEL, uF.showData(getStrRecruitmentLevel(), ""));
				strSubject = strSubject.replace(RECRUITMENT_LEVEL, uF.showData(getStrRecruitmentLevel(), ""));
				strTextContent = strTextContent.replace(RECRUITMENT_LEVEL, uF.showData(getStrRecruitmentLevel(), ""));
			}
			if (getStrRecruitmentPosition() != null) {
				strEmailBody = strEmailBody.replace(RECRUITMENT_POSITIONS, uF.showData(getStrRecruitmentPosition(), ""));
				strSubject = strSubject.replace(RECRUITMENT_POSITIONS, uF.showData(getStrRecruitmentPosition(), ""));
				strTextContent = strTextContent.replace(RECRUITMENT_POSITIONS, uF.showData(getStrRecruitmentPosition(), ""));
			}
			if (getStrRecruitmentProfile() != null) {
				strEmailBody = strEmailBody.replace(RECRUITMENT_PROFILE, uF.showData(getStrRecruitmentProfile(), ""));
				strSubject = strSubject.replace(RECRUITMENT_PROFILE, uF.showData(getStrRecruitmentProfile(), ""));
				strTextContent = strTextContent.replace(RECRUITMENT_PROFILE, uF.showData(getStrRecruitmentProfile(), ""));
			}
			if (getStrRecruitmentSkill() != null) {
				strEmailBody = strEmailBody.replace(RECRUITMENT_SKILL, uF.showData(getStrRecruitmentSkill(), ""));
				strSubject = strSubject.replace(RECRUITMENT_SKILL, uF.showData(getStrRecruitmentSkill(), ""));
				strTextContent = strTextContent.replace(RECRUITMENT_SKILL, uF.showData(getStrRecruitmentSkill(), ""));
			}
			if (getStrRecruitmentWLocation() != null) {
				strEmailBody = strEmailBody.replace(RECRUITMENT_WLOCATION, uF.showData(getStrRecruitmentWLocation(), ""));
				strSubject = strSubject.replace(RECRUITMENT_WLOCATION, uF.showData(getStrRecruitmentWLocation(), ""));
				strTextContent = strTextContent.replace(RECRUITMENT_WLOCATION, uF.showData(getStrRecruitmentWLocation(), ""));
			}

			// System.out.println("getStrOfferedSalaryStructure() ====> "
			// +getStrOfferedSalaryStructure());
			if (getStrOfferedSalaryStructure() != null) {
				// System.out.println("getStrOfferedSalaryStructure() in if
				// ====> " +getStrOfferedSalaryStructure());
				strEmailBody = strEmailBody.replace(OFFERED_SALARY_STRUCTURE, uF.showData(getStrOfferedSalaryStructure(), ""));
				strSubject = strSubject.replace(OFFERED_SALARY_STRUCTURE, uF.showData(getStrOfferedSalaryStructure(), ""));
				strTextContent = strTextContent.replace(OFFERED_SALARY_STRUCTURE, uF.showData(getStrOfferedSalaryStructure(), ""));
			}

			if (getStrCandiSalBasic() != null) {
				strEmailBody = strEmailBody.replace(CANDI_SALARY_BASIC, uF.showData(getStrCandiSalBasic(), ""));
				strSubject = strSubject.replace(CANDI_SALARY_BASIC, uF.showData(getStrCandiSalBasic(), ""));
				strTextContent = strTextContent.replace(CANDI_SALARY_BASIC, uF.showData(getStrCandiSalBasic(), ""));
			}
			if (getStrCandiSalHRA() != null) {
				strEmailBody = strEmailBody.replace(CANDI_SALARY_HRA, uF.showData(getStrCandiSalHRA(), ""));
				strSubject = strSubject.replace(CANDI_SALARY_HRA, uF.showData(getStrCandiSalHRA(), ""));
				strTextContent = strTextContent.replace(CANDI_SALARY_HRA, uF.showData(getStrCandiSalHRA(), ""));
			}
			if (getStrCandiSalConvAllow() != null) {
				strEmailBody = strEmailBody.replace(CANDI_SALARY_CONVALLOW, uF.showData(getStrCandiSalConvAllow(), ""));
				strSubject = strSubject.replace(CANDI_SALARY_CONVALLOW, uF.showData(getStrCandiSalConvAllow(), ""));
				strTextContent = strTextContent.replace(CANDI_SALARY_CONVALLOW, uF.showData(getStrCandiSalConvAllow(), ""));
			}
			if (getStrCandiSalOverTime() != null) {
				strEmailBody = strEmailBody.replace(CANDI_SALARY_OVERTIME, uF.showData(getStrCandiSalOverTime(), ""));
				strSubject = strSubject.replace(CANDI_SALARY_OVERTIME, uF.showData(getStrCandiSalOverTime(), ""));
				strTextContent = strTextContent.replace(CANDI_SALARY_OVERTIME, uF.showData(getStrCandiSalOverTime(), ""));
			}
			if (getStrCandiSalGratuity() != null) {
				strEmailBody = strEmailBody.replace(CANDI_SALARY_GRATUITY, uF.showData(getStrCandiSalGratuity(), ""));
				strSubject = strSubject.replace(CANDI_SALARY_GRATUITY, uF.showData(getStrCandiSalGratuity(), ""));
				strTextContent = strTextContent.replace(CANDI_SALARY_GRATUITY, uF.showData(getStrCandiSalGratuity(), ""));
			}
			if (getStrCandiSalBonus() != null) {
				strEmailBody = strEmailBody.replace(CANDI_SALARY_BONUS, uF.showData(getStrCandiSalBonus(), ""));
				strSubject = strSubject.replace(CANDI_SALARY_BONUS, uF.showData(getStrCandiSalBonus(), ""));
				strTextContent = strTextContent.replace(CANDI_SALARY_BONUS, uF.showData(getStrCandiSalBonus(), ""));
			}
			if (getStrCandiSalMobExpenses() != null) {
				strEmailBody = strEmailBody.replace(CANDI_SALARY_MOBILEEXPENSES, uF.showData(getStrCandiSalMobExpenses(), ""));
				strSubject = strSubject.replace(CANDI_SALARY_MOBILEEXPENSES, uF.showData(getStrCandiSalMobExpenses(), ""));
				strTextContent = strTextContent.replace(CANDI_SALARY_MOBILEEXPENSES, uF.showData(getStrCandiSalMobExpenses(), ""));
			}
			if (getStrCandiSalMedicalAllow() != null) {
				strEmailBody = strEmailBody.replace(CANDI_SALARY_MEDICALALLOW, uF.showData(getStrCandiSalMedicalAllow(), ""));
				strSubject = strSubject.replace(CANDI_SALARY_MEDICALALLOW, uF.showData(getStrCandiSalMedicalAllow(), ""));
				strTextContent = strTextContent.replace(CANDI_SALARY_MEDICALALLOW, uF.showData(getStrCandiSalMedicalAllow(), ""));
			}
			if (getStrCandiSalSpecialAllow() != null) {
				strEmailBody = strEmailBody.replace(CANDI_SALARY_SPECIALALLOW, uF.showData(getStrCandiSalSpecialAllow(), ""));
				strSubject = strSubject.replace(CANDI_SALARY_SPECIALALLOW, uF.showData(getStrCandiSalSpecialAllow(), ""));
				strTextContent = strTextContent.replace(CANDI_SALARY_SPECIALALLOW, uF.showData(getStrCandiSalSpecialAllow(), ""));
			}
			if (getStrCandiSalArrearsAndOtherAllow() != null) {
				strEmailBody = strEmailBody.replace(CANDI_SALARY_ARREAR_AND_OTHERALLOW, uF.showData(getStrCandiSalSpecialAllow(), ""));
				strSubject = strSubject.replace(CANDI_SALARY_ARREAR_AND_OTHERALLOW, uF.showData(getStrCandiSalSpecialAllow(), ""));
				strTextContent = strTextContent.replace(CANDI_SALARY_ARREAR_AND_OTHERALLOW, uF.showData(getStrCandiSalSpecialAllow(), ""));
			}
			if (getStrCandiTotGrossSalary() != null) {
				strEmailBody = strEmailBody.replace(CANDI_TOTAL_GROSS_SALARY, uF.showData(getStrCandiTotGrossSalary(), ""));
				strSubject = strSubject.replace(CANDI_TOTAL_GROSS_SALARY, uF.showData(getStrCandiTotGrossSalary(), ""));
				strTextContent = strTextContent.replace(CANDI_TOTAL_GROSS_SALARY, uF.showData(getStrCandiTotGrossSalary(), ""));
			}

			if (getStrCandiSalDeductProftax() != null) {
				strEmailBody = strEmailBody.replace(CANDI_SALARY_DEDUCT_PROFTAX, uF.showData(getStrCandiSalDeductProftax(), ""));
				strSubject = strSubject.replace(CANDI_SALARY_DEDUCT_PROFTAX, uF.showData(getStrCandiSalDeductProftax(), ""));
				strTextContent = strTextContent.replace(CANDI_SALARY_DEDUCT_PROFTAX, uF.showData(getStrCandiSalDeductProftax(), ""));
			}
			if (getStrCandiSalDeductTDS() != null) {
				strEmailBody = strEmailBody.replace(CANDI_SALARY_DEDUCT_TDS, uF.showData(getStrCandiSalDeductTDS(), ""));
				strSubject = strSubject.replace(CANDI_SALARY_DEDUCT_TDS, uF.showData(getStrCandiSalDeductTDS(), ""));
				strTextContent = strTextContent.replace(CANDI_SALARY_DEDUCT_TDS, uF.showData(getStrCandiSalDeductTDS(), ""));
			}
			if (getStrCandiSalDeductPFEmpCont() != null) {
				strEmailBody = strEmailBody.replace(CANDI_SALARY_DEDUCT_PFEMPCONT, uF.showData(getStrCandiSalDeductPFEmpCont(), ""));
				strSubject = strSubject.replace(CANDI_SALARY_DEDUCT_PROFTAX, uF.showData(getStrCandiSalDeductPFEmpCont(), ""));
				strTextContent = strTextContent.replace(CANDI_SALARY_DEDUCT_PROFTAX, uF.showData(getStrCandiSalDeductPFEmpCont(), ""));
			}
			if (getStrCandiSalDeductPFEmprCont() != null) {
				strEmailBody = strEmailBody.replace(CANDI_SALARY_DEDUCT_PFEMPRCONT, uF.showData(getStrCandiSalDeductPFEmprCont(), ""));
				strSubject = strSubject.replace(CANDI_SALARY_DEDUCT_PFEMPRCONT, uF.showData(getStrCandiSalDeductPFEmprCont(), ""));
				strTextContent = strTextContent.replace(CANDI_SALARY_DEDUCT_PFEMPRCONT, uF.showData(getStrCandiSalDeductPFEmprCont(), ""));
			}
			if (getStrCandiSalDeductESIEmpr() != null) {
				strEmailBody = strEmailBody.replace(CANDI_SALARY_DEDUCT_ESIEMPR, uF.showData(getStrCandiSalDeductESIEmpr(), ""));
				strSubject = strSubject.replace(CANDI_SALARY_DEDUCT_ESIEMPR, uF.showData(getStrCandiSalDeductESIEmpr(), ""));
				strTextContent = strTextContent.replace(CANDI_SALARY_DEDUCT_ESIEMPR, uF.showData(getStrCandiSalDeductESIEmpr(), ""));
			}
			if (getStrCandiSalDeductESIEmp() != null) {
				strEmailBody = strEmailBody.replace(CANDI_SALARY_DEDUCT_ESIEMP, uF.showData(getStrCandiSalDeductESIEmp(), ""));
				strSubject = strSubject.replace(CANDI_SALARY_DEDUCT_ESIEMP, uF.showData(getStrCandiSalDeductESIEmp(), ""));
				strTextContent = strTextContent.replace(CANDI_SALARY_DEDUCT_ESIEMP, uF.showData(getStrCandiSalDeductESIEmp(), ""));
			}
			if (getStrCandiSalDeductLoan() != null) {
				strEmailBody = strEmailBody.replace(CANDI_SALARY_DEDUCT_LOAN, uF.showData(getStrCandiSalDeductLoan(), ""));
				strSubject = strSubject.replace(CANDI_SALARY_DEDUCT_LOAN, uF.showData(getStrCandiSalDeductLoan(), ""));
				strTextContent = strTextContent.replace(CANDI_SALARY_DEDUCT_LOAN, uF.showData(getStrCandiSalDeductLoan(), ""));
			}
			if (getStrCandiTotDeduction() != null) {
				strEmailBody = strEmailBody.replace(CANDI_TOTAL_DEDUCTION, uF.showData(getStrCandiTotDeduction(), ""));
				strSubject = strSubject.replace(CANDI_TOTAL_DEDUCTION, uF.showData(getStrCandiTotDeduction(), ""));
				strTextContent = strTextContent.replace(CANDI_TOTAL_DEDUCTION, uF.showData(getStrCandiTotDeduction(), ""));
			}

			// System.out.println(" ADD EMP STEP 8 ===>
			// "+getStrAddCandidateStep8());

			if (getStrAddCandidateStep8() != null) {
				strEmailBody = strEmailBody.replace(ADD_CANDIDATE_SPECIFY_OTHER_DATE, strLink + strAddCandidateAction + getStrAddCandidateStep8());
				strSubject = strSubject.replace(ADD_CANDIDATE_SPECIFY_OTHER_DATE, strLink + strAddCandidateAction + getStrAddCandidateStep8());
				strTextContent = strTextContent.replace(ADD_CANDIDATE_SPECIFY_OTHER_DATE, strLink + strAddCandidateAction + getStrAddCandidateStep8());
			}

			if (getOfferAcceptData() != null) {
				strEmailBody = strEmailBody.replace(CANDI_OFFER_ACCEPT_LINK, strLink + strOfferAcceptAction + getOfferAcceptData());
				strSubject = strSubject.replace(CANDI_OFFER_ACCEPT_LINK, strLink + strOfferAcceptAction + getOfferAcceptData());
				strTextContent = strTextContent.replace(CANDI_OFFER_ACCEPT_LINK, strLink + strOfferAcceptAction + getOfferAcceptData());
			}

			if (getStrCandiInterviewDateTime() != null) {
				strEmailBody = strEmailBody.replace(CANDI_INTRVIEW_DATE, uF.showData(getStrCandiInterviewDateTime(), ""));
				strSubject = strSubject.replace(CANDI_INTRVIEW_DATE, uF.showData(getStrCandiInterviewDateTime(), ""));
				strTextContent = strTextContent.replace(CANDI_INTRVIEW_DATE, uF.showData(getStrCandiInterviewDateTime(), ""));
			}
			// System.out.println("getOnboardingData() ===>
			// "+getOnboardingData());
			if (getOnboardingData() != null) {
				strEmailBody = strEmailBody.replace(CANDI_ONBOARDING_LINK, strLink + strOnboardingAction + getOnboardingData());
				strSubject = strSubject.replace(CANDI_ONBOARDING_LINK, strLink + strOnboardingAction + getOnboardingData());
				strTextContent = strTextContent.replace(CANDI_ONBOARDING_LINK, strLink + strOnboardingAction + getOnboardingData());
			}

			if (getStrAddCandiLink() != null) {
				strEmailBody = strEmailBody.replace(CANDI_ADD_LINK, strLink + strAddCandiByCandiAction + getStrAddCandiLink());
				strSubject = strSubject.replace(CANDI_ADD_LINK, strLink + strAddCandiByCandiAction + getStrAddCandiLink());
				strTextContent = strTextContent.replace(CANDI_ADD_LINK, strLink + strAddCandiByCandiAction + getStrAddCandiLink());
			}
			//
			if (getStrCandiBGVerificationLink() != null) {
				strEmailBody = strEmailBody.replace(CANDI_BACKGROUND_VERIFICATION_LINK, strLink + strAddCandiByCandiAction + getStrCandiBGVerificationLink());
				strSubject = strSubject.replace(CANDI_BACKGROUND_VERIFICATION_LINK, strLink + strAddCandiByCandiAction + getStrCandiBGVerificationLink());
				strTextContent = strTextContent.replace(CANDI_BACKGROUND_VERIFICATION_LINK,
						strLink + strAddCandiByCandiAction + getStrCandiBGVerificationLink());
			}

			if (getStrCandiTakeAssessLink() != null) {
				strEmailBody = strEmailBody.replace(CANDI_TAKE_ASSESSMENT_LINK, strLink + strCandiTakeAssessmentAction + getStrCandiTakeAssessLink());
				strSubject = strSubject.replace(CANDI_TAKE_ASSESSMENT_LINK, strLink + strCandiTakeAssessmentAction + getStrCandiTakeAssessLink());
				strTextContent = strTextContent.replace(CANDI_TAKE_ASSESSMENT_LINK, strLink + strCandiTakeAssessmentAction + getStrCandiTakeAssessLink());
			}

			// if(getStrCandiPassword()!=null){
			// strEmailBody = strEmailBody.replace(CANDI_PASSWORD,
			// getStrCandiPassword());
			// strSubject = strSubject.replace(CANDI_PASSWORD,
			// getStrCandiPassword());
			// strTextContent = strTextContent.replace(CANDI_PASSWORD,
			// getStrCandiPassword());
			// }

			/**
			 * new tag
			 */
			if (getStrOrganisationAddress() != null) {
				strEmailBody = strEmailBody.replace(ORGANISATION_ADDRESS, uF.showData(getStrOrganisationAddress(), ""));
				strSubject = strSubject.replace(ORGANISATION_ADDRESS, uF.showData(getStrOrganisationAddress(), ""));
				strTextContent = strTextContent.replace(ORGANISATION_ADDRESS, uF.showData(getStrOrganisationAddress(), ""));
			}
			if (getStrLegalEntityName() != null) {
				strEmailBody = strEmailBody.replace(LEGAL_ENTITY_NAME, uF.showData(getStrLegalEntityName(), ""));
				strSubject = strSubject.replace(LEGAL_ENTITY_NAME, uF.showData(getStrLegalEntityName(), ""));
				strTextContent = strTextContent.replace(LEGAL_ENTITY_NAME, uF.showData(getStrLegalEntityName(), ""));
			}
			if (getStrLegalEntityAddress() != null) {
				strEmailBody = strEmailBody.replace(LEGAL_ENTITY_ADDRESS, uF.showData(getStrLegalEntityAddress(), ""));
				strSubject = strSubject.replace(LEGAL_ENTITY_ADDRESS, uF.showData(getStrLegalEntityAddress(), ""));
				strTextContent = strTextContent.replace(LEGAL_ENTITY_ADDRESS, uF.showData(getStrLegalEntityAddress(), ""));
			}
			if (getStrDepartmentName() != null) {
				strEmailBody = strEmailBody.replace(DEPARTMENT_NAME, uF.showData(getStrDepartmentName(), ""));
				strSubject = strSubject.replace(DEPARTMENT_NAME, uF.showData(getStrDepartmentName(), ""));
				strTextContent = strTextContent.replace(DEPARTMENT_NAME, uF.showData(getStrDepartmentName(), ""));
			}
			if (getStrSbuName() != null) {
				strEmailBody = strEmailBody.replace(SBU_NAME, uF.showData(getStrSbuName(), ""));
				strSubject = strSubject.replace(SBU_NAME, uF.showData(getStrSbuName(), ""));
				strTextContent = strTextContent.replace(SBU_NAME, uF.showData(getStrSbuName(), ""));
			}
			if (getStrReportEmpDesignation() != null) {
				strEmailBody = strEmailBody.replace(R_REPORT_EMP_DESIGNATION, uF.showData(getStrReportEmpDesignation(), ""));
				strSubject = strSubject.replace(R_REPORT_EMP_DESIGNATION, uF.showData(getStrReportEmpDesignation(), ""));
				strTextContent = strTextContent.replace(R_REPORT_EMP_DESIGNATION, uF.showData(getStrReportEmpDesignation(), ""));
			}
			if (getStrSkills() != null) {
				strEmailBody = strEmailBody.replace(SKILLS, uF.showData(getStrSkills(), ""));
				strSubject = strSubject.replace(SKILLS, uF.showData(getStrSkills(), ""));
				strTextContent = strTextContent.replace(SKILLS, uF.showData(getStrSkills(), ""));
			}
			if (getStrEffectiveDate() != null) {
				strEmailBody = strEmailBody.replace(EFFECTIVE_DATE, uF.showData(getStrEffectiveDate(), ""));
				strSubject = strSubject.replace(EFFECTIVE_DATE, uF.showData(getStrEffectiveDate(), ""));
				strTextContent = strTextContent.replace(EFFECTIVE_DATE, uF.showData(getStrEffectiveDate(), ""));
			}
			if (getStrPromotionGrade() != null) {
				strEmailBody = strEmailBody.replace(PROMOTION_GRADE, uF.showData(getStrPromotionGrade(), ""));
				strSubject = strSubject.replace(PROMOTION_GRADE, uF.showData(getStrPromotionGrade(), ""));
				strTextContent = strTextContent.replace(PROMOTION_GRADE, uF.showData(getStrPromotionGrade(), ""));
			}
			if (getStrPromotionLevel() != null) {
				strEmailBody = strEmailBody.replace(PROMOTION_LEVEL, uF.showData(getStrPromotionLevel(), ""));
				strSubject = strSubject.replace(PROMOTION_LEVEL, uF.showData(getStrPromotionLevel(), ""));
				strTextContent = strTextContent.replace(PROMOTION_LEVEL, uF.showData(getStrPromotionLevel(), ""));
			}
			if (getStrPromotionDesignation() != null) {
				strEmailBody = strEmailBody.replace(PROMOTION_DESIGNATION, uF.showData(getStrPromotionDesignation(), ""));
				strSubject = strSubject.replace(PROMOTION_DESIGNATION, uF.showData(getStrPromotionDesignation(), ""));
				strTextContent = strTextContent.replace(PROMOTION_DESIGNATION, uF.showData(getStrPromotionDesignation(), ""));
			}
			if (getStrPromotionDate() != null) {
				strEmailBody = strEmailBody.replace(PROMOTION_DATE, uF.showData(getStrPromotionDate(), ""));
				strSubject = strSubject.replace(PROMOTION_DATE, uF.showData(getStrPromotionDate(), ""));
				strTextContent = strTextContent.replace(PROMOTION_DATE, uF.showData(getStrPromotionDate(), ""));
			}
			// if(getStrIncrementAmount()!=null){
			// strEmailBody = strEmailBody.replace(INCREMENT_AMOUNT,
			// uF.showData(getStrIncrementAmount(),""));
			// strSubject = strSubject.replace(INCREMENT_AMOUNT,
			// uF.showData(getStrIncrementAmount(),""));
			// strTextContent = strTextContent.replace(INCREMENT_AMOUNT,
			// uF.showData(getStrIncrementAmount(),""));
			// }
			if (getStrIncrementPercent() != null) {
				strEmailBody = strEmailBody.replace(INCREMENT_PERCENT, uF.showData(getStrIncrementPercent(), ""));
				strSubject = strSubject.replace(INCREMENT_PERCENT, uF.showData(getStrIncrementPercent(), ""));
				strTextContent = strTextContent.replace(INCREMENT_PERCENT, uF.showData(getStrIncrementPercent(), ""));
			}
			if (getStrTerminateDate() != null) {
				strEmailBody = strEmailBody.replace(TERMINATION_DATE, uF.showData(getStrTerminateDate(), ""));
				strSubject = strSubject.replace(TERMINATION_DATE, uF.showData(getStrTerminateDate(), ""));
				strTextContent = strTextContent.replace(TERMINATION_DATE, uF.showData(getStrTerminateDate(), ""));
			}
			if (getStrLastDateAtOffice() != null) {
				strEmailBody = strEmailBody.replace(LAST_DATE_AT_OFFICE, uF.showData(getStrLastDateAtOffice(), ""));
				strSubject = strSubject.replace(LAST_DATE_AT_OFFICE, uF.showData(getStrLastDateAtOffice(), ""));
				strTextContent = strTextContent.replace(LAST_DATE_AT_OFFICE, uF.showData(getStrLastDateAtOffice(), ""));
			}
			if (getStrSalaryStructure() != null) {
				strEmailBody = strEmailBody.replace(SALARY_STRUCTURE, uF.showData(getStrSalaryStructure(), ""));
				strSubject = strSubject.replace(SALARY_STRUCTURE, uF.showData(getStrSalaryStructure(), ""));
				strTextContent = strTextContent.replace(SALARY_STRUCTURE, uF.showData(getStrSalaryStructure(), ""));
			}
			if (getStrPayStructure() != null) {
				strEmailBody = strEmailBody.replace(PAY_STRUCTURE, uF.showData(getStrPayStructure(), ""));
				strSubject = strSubject.replace(PAY_STRUCTURE, uF.showData(getStrPayStructure(), ""));
				strTextContent = strTextContent.replace(PAY_STRUCTURE, uF.showData(getStrPayStructure(), ""));
			}
			if (getStrPayrollAmount() != null) {
				strEmailBody = strEmailBody.replace(PAYROLL_AMOUNT, uF.showData(getStrPayrollAmount(), ""));
				strSubject = strSubject.replace(PAYROLL_AMOUNT, uF.showData(getStrPayrollAmount(), ""));
				strTextContent = strTextContent.replace(PAYROLL_AMOUNT, uF.showData(getStrPayrollAmount(), ""));
			}
			if (getStrPayrollAmountWords() != null) {
				strEmailBody = strEmailBody.replace(PAYROLL_AMOUNT_WORDS, uF.showData(getStrPayrollAmountWords(), ""));
				strSubject = strSubject.replace(PAYROLL_AMOUNT_WORDS, uF.showData(getStrPayrollAmountWords(), ""));
				strTextContent = strTextContent.replace(PAYROLL_AMOUNT_WORDS, uF.showData(getStrPayrollAmountWords(), ""));
			}
			if (getStrPayYear() != null) {
				strEmailBody = strEmailBody.replace(PAY_YEAR, uF.showData(getStrPayYear(), ""));
				strSubject = strSubject.replace(PAY_YEAR, uF.showData(getStrPayYear(), ""));
				strTextContent = strTextContent.replace(PAY_YEAR, uF.showData(getStrPayYear(), ""));
			}
			if (getStrPayMonth() != null) {
				strEmailBody = strEmailBody.replace(PAY_MONTH, uF.showData(getStrPayMonth(), ""));
				strSubject = strSubject.replace(PAY_MONTH, uF.showData(getStrPayMonth(), ""));
				strTextContent = strTextContent.replace(PAY_MONTH, uF.showData(getStrPayMonth(), ""));
			}
			if (getStrAllowanceAmount() != null) {
				strEmailBody = strEmailBody.replace(ALLOWANCE_AMOUNT, uF.showData(getStrAllowanceAmount(), ""));
				strSubject = strSubject.replace(ALLOWANCE_AMOUNT, uF.showData(getStrAllowanceAmount(), ""));
				strTextContent = strTextContent.replace(ALLOWANCE_AMOUNT, uF.showData(getStrAllowanceAmount(), ""));
			}
			if (getStrAllowanceAmountWords() != null) {
				strEmailBody = strEmailBody.replace(ALLOWANCE_AMOUNT_WORDS, uF.showData(getStrAllowanceAmountWords(), ""));
				strSubject = strSubject.replace(ALLOWANCE_AMOUNT_WORDS, uF.showData(getStrAllowanceAmountWords(), ""));
				strTextContent = strTextContent.replace(ALLOWANCE_AMOUNT_WORDS, uF.showData(getStrAllowanceAmountWords(), ""));
			}
			if (getStrBanks() != null) {
				strEmailBody = strEmailBody.replace(BANKS, uF.showData(getStrBanks(), ""));
				strSubject = strSubject.replace(BANKS, uF.showData(getStrBanks(), ""));
				strTextContent = strTextContent.replace(BANKS, uF.showData(getStrBanks(), ""));
			}
			if (getStrBankCode() != null) {
				strEmailBody = strEmailBody.replace(BANK_CODE, uF.showData(getStrBankCode(), ""));
				strSubject = strSubject.replace(BANK_CODE, uF.showData(getStrBankCode(), ""));
				strTextContent = strTextContent.replace(BANK_CODE, uF.showData(getStrBankCode(), ""));
			}
//			if (getStrCandiAddress() != null) {
				strEmailBody = strEmailBody.replace(R_CANDIADDRESS, uF.showData(getStrCandiAddress(), ""));
				strSubject = strSubject.replace(R_CANDIADDRESS, uF.showData(getStrCandiAddress(), ""));
				strTextContent = strTextContent.replace(R_CANDIADDRESS, uF.showData(getStrCandiAddress(), ""));
//			}

			// Start Dattatray Date : 09-08-21
			if (getStrJobTitle() != null) {
				strEmailBody = strEmailBody.replace(CANDI_JOB_TITLE, uF.showData(getStrJobTitle(), ""));
				strSubject = strSubject.replace(CANDI_JOB_TITLE, uF.showData(getStrJobTitle(), ""));
				strTextContent = strTextContent.replace(CANDI_JOB_TITLE, uF.showData(getStrJobTitle(), ""));
			}
			
			//===start parvez date: 30-10-2021===
//			if (getStrCandiNoticePeriod() != null) {
				strEmailBody = strEmailBody.replace(CANDI_NOTICE_PERIOD, uF.showData(getStrCandiNoticePeriod(), ""));
				strSubject = strSubject.replace(CANDI_NOTICE_PERIOD, uF.showData(getStrCandiNoticePeriod(), ""));
				strTextContent = strTextContent.replace(CANDI_NOTICE_PERIOD, uF.showData(getStrCandiNoticePeriod(), ""));
//			}
			
//			if (getStrCandiExpectedCTC() != null) {
				strEmailBody = strEmailBody.replace(CANDI_EXPECTED_CTC, uF.showData(getStrCandiExpectedCTC(), ""));
				strSubject = strSubject.replace(CANDI_EXPECTED_CTC, uF.showData(getStrCandiExpectedCTC(), ""));
				strTextContent = strTextContent.replace(CANDI_EXPECTED_CTC, uF.showData(getStrCandiExpectedCTC(), ""));
//			}
			
//			if (getStrCandiExperience() != null) {
				strEmailBody = strEmailBody.replace(CANDI_TOTAL_EXPERIENCE, uF.showData(getStrCandiExperience(), ""));
				strSubject = strSubject.replace(CANDI_TOTAL_EXPERIENCE, uF.showData(getStrCandiExperience(), ""));
				strTextContent = strTextContent.replace(CANDI_TOTAL_EXPERIENCE, uF.showData(getStrCandiExperience(), ""));
//			}
			//===end parvez date: 30-10-2021===
			
			//===start parvez date: 02-11-2021===
//			if (getStrCandiCurrentLocation() != null) {
				strEmailBody = strEmailBody.replace(CANDI_Current_LOCATION, uF.showData(getStrCandiCurrentLocation(), ""));
				strSubject = strSubject.replace(CANDI_Current_LOCATION, uF.showData(getStrCandiCurrentLocation(), ""));
				strTextContent = strTextContent.replace(CANDI_Current_LOCATION, uF.showData(getStrCandiCurrentLocation(), ""));
//			}
			
//			if (getStrCandiPreferredLocation() != null) {
				strEmailBody = strEmailBody.replace(CANDI_PREFERRED_LOCATION, uF.showData(getStrCandiPreferredLocation(), ""));
				strSubject = strSubject.replace(CANDI_PREFERRED_LOCATION, uF.showData(getStrCandiPreferredLocation(), ""));
				strTextContent = strTextContent.replace(CANDI_PREFERRED_LOCATION, uF.showData(getStrCandiPreferredLocation(), ""));
//			}
			
//			if (getStrCandiCurrentOrg() != null) {
				strEmailBody = strEmailBody.replace(CANDI_Current_ORG, uF.showData(getStrCandiCurrentOrg(), ""));
				strSubject = strSubject.replace(CANDI_Current_ORG, uF.showData(getStrCandiCurrentOrg(), ""));
				strTextContent = strTextContent.replace(CANDI_Current_ORG, uF.showData(getStrCandiCurrentOrg(), ""));
//			}
			
//			if (getStrCandiPrimarySkill() != null) {
				strEmailBody = strEmailBody.replace(CANDI_PRIMARY_SKILL, uF.showData(getStrCandiPrimarySkill(), ""));
				strSubject = strSubject.replace(CANDI_PRIMARY_SKILL, uF.showData(getStrCandiPrimarySkill(), ""));
				strTextContent = strTextContent.replace(CANDI_PRIMARY_SKILL, uF.showData(getStrCandiPrimarySkill(), ""));
//			}
			
//			if (getStrCandiSecondarySkill() != null) {
				strEmailBody = strEmailBody.replace(CANDI_SECONDARY_SKILL, uF.showData(getStrCandiSecondarySkill(), ""));
				strSubject = strSubject.replace(CANDI_SECONDARY_SKILL, uF.showData(getStrCandiSecondarySkill(), ""));
				strTextContent = strTextContent.replace(CANDI_SECONDARY_SKILL, uF.showData(getStrCandiSecondarySkill(), ""));
//			}
			
			//===end parvez date: 02-11-2021===


			if (getStrRecruiterName() != null) {
				strEmailBody = strEmailBody.replace(CANDI_RECRUITER_NAME, uF.showData(getStrRecruiterName(), ""));
				strSubject = strSubject.replace(CANDI_RECRUITER_NAME, uF.showData(getStrRecruiterName(), ""));
				strTextContent = strTextContent.replace(CANDI_RECRUITER_NAME, uF.showData(getStrRecruiterName(), ""));
			}

			if (getStrRoundNo() != null) {
				strEmailBody = strEmailBody.replace(CANDI_ROUND_NUMBER, uF.showData(getStrRoundNo(), ""));
				strSubject = strSubject.replace(CANDI_ROUND_NUMBER, uF.showData(getStrRoundNo(), ""));
				strTextContent = strTextContent.replace(CANDI_ROUND_NUMBER, uF.showData(getStrRoundNo(), ""));
			}

			if (getStrJobPosition() != null) {
				strEmailBody = strEmailBody.replace(CANDI_JOB_POSITION, uF.showData(getStrJobPosition(), ""));
				strSubject = strSubject.replace(CANDI_JOB_POSITION, uF.showData(getStrJobPosition(), ""));
				strTextContent = strTextContent.replace(CANDI_JOB_POSITION, uF.showData(getStrJobPosition(), ""));
			}
			if (getStrRecruiterSignature() != null) {
				strEmailBody = strEmailBody.replace(RECRUITER_SIGNATURE, uF.showData(getStrRecruiterSignature(), ""));
			}

			// End Dattatray Date : 09-08-21
			// Start Dattatray Date : 11-08-21
			if (getStrPreRoundNo() != null) {
				strEmailBody = strEmailBody.replace(PREVIOUS_ROUND, uF.showData(getStrPreRoundNo(), ""));
				strSubject = strSubject.replace(PREVIOUS_ROUND, uF.showData(getStrPreRoundNo(), ""));
				strTextContent = strTextContent.replace(PREVIOUS_ROUND, uF.showData(getStrPreRoundNo(), ""));
			}
			if (getStrNextRoundNo() != null) {
				strEmailBody = strEmailBody.replace(NEXT_ROUND, uF.showData(getStrNextRoundNo(), ""));
				strSubject = strSubject.replace(NEXT_ROUND, uF.showData(getStrNextRoundNo(), ""));
				strTextContent = strTextContent.replace(NEXT_ROUND, uF.showData(getStrNextRoundNo(), ""));
			} else {
				strEmailBody = strEmailBody.replace(NEXT_ROUND, uF.showData(getStrNextRoundNo(), "No more round"));
			}

			if (getStrInterviewTime() != null) {
				strEmailBody = strEmailBody.replace(TIME_OF_INTERVIEW, uF.showData(getStrInterviewTime(), ""));
				strSubject = strSubject.replace(TIME_OF_INTERVIEW, uF.showData(getStrInterviewTime(), ""));
				strTextContent = strTextContent.replace(TIME_OF_INTERVIEW, uF.showData(getStrInterviewTime(), ""));
			} else {
				strEmailBody = strEmailBody.replace(TIME_OF_INTERVIEW, uF.showData(getStrInterviewTime(), ""));
			} // End Dattatray Date : 11-08-21
			/**
			 * end
			 */
			//===start parvez date: 11-01-2022===
			if(getStrAdditionalComment() != null){
				strEmailBody = strEmailBody.replace(ADDITIONAL_COMMENT, uF.showData(getStrAdditionalComment(), ""));
				strSubject = strSubject.replace(ADDITIONAL_COMMENT, uF.showData(getStrAdditionalComment(), ""));
				strTextContent = strTextContent.replace(ADDITIONAL_COMMENT, uF.showData(getStrAdditionalComment(), ""));
			}
			if(getStrJoiningBonusAmount() != null){
				strEmailBody = strEmailBody.replace(JOINING_BONUS, uF.showData(getStrJoiningBonusAmount(), ""));
				strSubject = strSubject.replace(JOINING_BONUS, uF.showData(getStrJoiningBonusAmount(), ""));
				strTextContent = strTextContent.replace(JOINING_BONUS, uF.showData(getStrJoiningBonusAmount(), ""));
			}
	//===end parvez date: 11-01-2022===		

			
			strEmailBody = strEmailBody.replace("\n", "<br/>");

			setStrEmailBody(strEmailBody);
			setStrTextContent(strTextContent);
			setStrEmailSubject(strSubject);
		}

		// System.out.println("strEmailBody ====> " +strEmailBody);
		Map<String, String> hmParsedContent = new HashMap<String, String>();
		hmParsedContent.put("MAIL_BODY", strEmailBody);
		hmParsedContent.put("TEXT_BODY", strTextContent);
		hmParsedContent.put("MAIL_SUBJECT", strSubject);

		return hmParsedContent;
	}

	public void sendEmailNotifications() {
		// System.out.println("getStrHostAddress==>"+getStrHostAddress()+"-----getIsRequiredAuthentication=====>"+getIsRequiredAuthentication());
		if (CF != null && getStrHostAddress() != null && getStrHostAddress().indexOf("gmail") > 0) {
//			System.out.println(" in sendEmailNotificationsFromGmail.....");
			sendEmailNotificationsFromGmail();
		} else if (CF != null && getIsRequiredAuthentication()) {
//			System.out.println(" in sendEmailAuthenticationFromServer.....");
			sendEmailAuthenticationFromServer();
		} else {
//			System.out.println(" in sendEmailNotificationsFromServer.....");
			sendEmailNotificationsFromServer();
		}

		// sendEmailNotificationsFromGmail();

	}

	private void sendEmailAuthenticationFromServer() {
		Transport t = null;

		try {
			Properties props = System.getProperties();
			props.put("mail.transport.protocol", "smtps");
			props.put("mail.smtp.port", 25);
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.starttls.required", "true");

			Session session = Session.getDefaultInstance(props);

			// Instantiatee a message
			MimeMessage msg = new MimeMessage(session);
			Multipart multipart = new MimeMultipart();
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			MimeBodyPart attachmentPart = new MimeBodyPart();

			// Set message attributes
			msg.setFrom(new InternetAddress(getStrEmailFrom()));
			
			InternetAddress[] address = null;
			address = new InternetAddress[1];
			
			//===start parvez date: 29-10-2021===
			if(getIsEmailIdHireTeam()){
				System.out.println("IF Condition");
				address[0] = new InternetAddress(getStrHiringTeamEmailId().trim());
			}else{
				System.out.println("Else Condition");
				address[0] = new InternetAddress(getStrEmailTo().trim());
			}
			//===end parvez date: 29-10-2021===

			msg.setRecipients(Message.RecipientType.TO, address);
			// Start Dattatray Date : 02-July-2021
			if (getIsEmailIdCC()) {
				
				InternetAddress[] addressCC = new InternetAddress[1];
				addressCC[0] = new InternetAddress(getStrEmailIdCC().trim());
				msg.setRecipients(Message.RecipientType.CC, addressCC);
			} 
			// End Dattatray Date : 02-July-2021

			msg.setSubject(getStrEmailSubject());
			msg.setSentDate(new Date());

			// Set message content
			messageBodyPart.setContent(getStrEmailBody(), "text/html");
			multipart.addBodyPart(messageBodyPart);

			if (getStrAttachmentFileSource() != null) {
				
				DataSource source = new FileDataSource(getStrAttachmentFileSource());
				attachmentPart.setDataHandler(new DataHandler(source));
				attachmentPart.setFileName(source.getName());
				if (getStrAttachmentFileName() != null) {
					attachmentPart.setFileName(getStrAttachmentFileName());
				}
				multipart.addBodyPart(attachmentPart);
			}

			if (pdf_data != null) {

				DataSource dataSource = new ByteArrayDataSource(pdf_data, "application/pdf");
				MimeBodyPart pdfBodyPart = new MimeBodyPart();
				pdfBodyPart.setDataHandler(new DataHandler(dataSource));
				pdfBodyPart.setFileName(getStrAttachmentFileName());

				multipart.addBodyPart(pdfBodyPart);
			}
			if (xls_data != null) {

				DataSource dataSource = new ByteArrayDataSource(xls_data, "application/xls");
				MimeBodyPart pdfBodyPart = new MimeBodyPart();
				pdfBodyPart.setDataHandler(new DataHandler(dataSource));
				pdfBodyPart.setFileName(getStrAttachmentFileName());

				multipart.addBodyPart(pdfBodyPart);
			}

			// Associate multi-part with message
			msg.setContent(multipart);

			t = session.getTransport();
			t.connect(getStrHost(), getStrEmailAuthUsername(), getStrEmailAuthPassword());
			t.sendMessage(msg, msg.getAllRecipients());

			session = null;
		} catch (MessagingException mex) {
			// Prints all nested (chained) exceptions as well
			mex.printStackTrace();
		} finally {
			if (t != null) {
				try {
					t.close();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// private void sendEmailAuthenticationFromServer() {
	//
	// Database db=new Database();
	// db.setRequest(request);
	// Connection con=null;
	// try {
	// con=db.makeConnection(con);
	// Properties props = new Properties();
	//
	// props.put("mail.smtp.host", getStrHost());
	// props.put("mail.debug", "true");
	// props.put("mail.smtp.auth", "true");
	//
	// System.out.println("getStrEmailAuthUsername ===>> " +
	// getStrEmailAuthUsername() + " -- getStrEmailAuthPassword ===>> " +
	// getStrEmailAuthPassword());
	// Session session = Session.getInstance(props,
	// new javax.mail.Authenticator() {
	// protected PasswordAuthentication getPasswordAuthentication() {
	// // return new
	// PasswordAuthentication("rahul.patil@konnecttechnologies.com","123temple");
	// return new PasswordAuthentication(getStrEmailAuthUsername(),
	// getStrEmailAuthPassword());
	// }
	// });
	//
	// // Instantiatee a message
	// MimeMessage msg = new MimeMessage(session);
	// Multipart multipart = new MimeMultipart();
	// MimeBodyPart messageBodyPart = new MimeBodyPart();
	// MimeBodyPart attachmentPart = new MimeBodyPart();
	//// System.out.println("getStrEmailFrom() ===> " + getStrEmailFrom());
	//// System.out.println("getStrEmailTo() ===> " + getStrEmailTo());
	// //Set message attributes
	// msg.setFrom(new InternetAddress(getStrEmailFrom()));
	//
	// InternetAddress[] address = null;
	// if(getStrEmailTo()!=null && !getStrEmailTo().equalsIgnoreCase(EVERYONE)){
	// address = new InternetAddress[1];
	// address[0] = new InternetAddress(getStrEmailTo());
	// }else if(getStrEmailTo()!=null &&
	// getStrEmailTo().equalsIgnoreCase(EVERYONE)){
	//
	// Map<String, String> hmCandiEmail = getCandiEmailMap();
	// if(hmCandiEmail==null){
	// hmCandiEmail = new HashMap<String, String>();
	// }
	//
	//
	// address = new InternetAddress[hmCandiEmail.size()];
	//
	// Set set = hmCandiEmail.keySet();
	// Iterator it = set.iterator();
	// int count=0;
	// while(it.hasNext()){
	// String strEmail = (String)hmCandiEmail.get((String)it.next());
	// if(strEmail!=null){
	// address[count++] = new InternetAddress(strEmail);
	// }
	// }
	//
	// }
	//
	// msg.setRecipients(Message.RecipientType.TO, address);
	// msg.setSubject(getStrEmailSubject());
	// msg.setSentDate(new Date());
	//
	//// System.out.println("getStrEmailBody() ===> " + getStrEmailBody());
	// // Set message content
	// messageBodyPart.setContent(getStrEmailBody(), "text/html");
	// multipart.addBodyPart(messageBodyPart);
	//
	//
	// if(getStrAttachmentFileSource()!=null){
	// DataSource source =
	// new FileDataSource(getStrAttachmentFileSource());
	// attachmentPart.setDataHandler(new DataHandler(source));
	// attachmentPart.setFileName(source.getName());
	// if(getStrAttachmentFileName()!=null){
	// attachmentPart.setFileName(getStrAttachmentFileName());
	// }
	// multipart.addBodyPart(attachmentPart);
	// }
	//
	//
	// if(pdf_data!=null){
	//
	// DataSource dataSource = new ByteArrayDataSource(pdf_data,
	// "application/pdf");
	// MimeBodyPart pdfBodyPart = new MimeBodyPart();
	// pdfBodyPart.setDataHandler(new DataHandler(dataSource));
	// pdfBodyPart.setFileName(getStrAttachmentFileName());
	//
	// multipart.addBodyPart(pdfBodyPart);
	// }
	// if(xls_data!=null){
	//
	// DataSource dataSource = new ByteArrayDataSource(xls_data,
	// "application/xls");
	// MimeBodyPart pdfBodyPart = new MimeBodyPart();
	// pdfBodyPart.setDataHandler(new DataHandler(dataSource));
	// pdfBodyPart.setFileName(getStrAttachmentFileName());
	//
	// multipart.addBodyPart(pdfBodyPart);
	// }
	//
	// // Associate multi-part with message
	// msg.setContent(multipart);
	//
	//
	// // Send message
	//// Transport.send(msg);
	// Transport t = session.getTransport("smtp");
	// t.connect(getStrHost(), getStrEmailFrom(), getStrHostPassword());
	// t.sendMessage(msg, msg.getAllRecipients());
	//
	// db.closeConnection(con);
	// }
	// catch (MessagingException mex) {
	// // Prints all nested (chained) exceptions as well
	// mex.printStackTrace();
	// }finally{
	// db.closeConnection(con);
	// }
	//
	// }

	private void sendEmailNotificationsFromServer() {

		// Create properties, get Session
		Properties props = new Properties();
		// String PORT = "80";
		// If using static Transport.send(),
		// need to specify which host to send it to
		props.put("mail.smtp.host", getStrHostAddress());
		// To see what is going on behind the scene
		props.put("mail.debug", "true");
		// props.put("mail.smtp.port", PORT);
		// props.put("mail.smtp.ssl.enable", "true");

		Session session = Session.getInstance(props);
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		con = db.makeConnection(con);
		try {
			// Instantiatee a message
			MimeMessage msg = new MimeMessage(session);
			Multipart multipart = new MimeMultipart();
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			MimeBodyPart attachmentPart = new MimeBodyPart();
			// System.out.println("getStrEmailFrom() ===> " +
			// getStrEmailFrom());
			// System.out.println("getStrEmailTo() ===> " + getStrEmailTo());
			// Set message attributes
			msg.setFrom(new InternetAddress(getStrEmailFrom()));

			InternetAddress[] address = null;
			if (getStrEmailTo() != null && !getStrEmailTo().equalsIgnoreCase(EVERYONE)) {
				address = new InternetAddress[1];
				address[0] = new InternetAddress(getStrEmailTo());
			} else if (getStrEmailTo() != null && getStrEmailTo().equalsIgnoreCase(EVERYONE)) {

				Map<String, String> hmCandiEmail = getCandiEmailMap();
				if (hmCandiEmail == null) {
					hmCandiEmail = new HashMap<String, String>();
				}

				address = new InternetAddress[hmCandiEmail.size()];

				Set set = hmCandiEmail.keySet();
				Iterator it = set.iterator();
				int count = 0;
				while (it.hasNext()) {
					String strEmail = (String) hmCandiEmail.get((String) it.next());
					if (strEmail != null) {
						address[count++] = new InternetAddress(strEmail);
					}
				}

				// Map<String, String> hmEmpEmail = CF.getEmpEmailMap(con);
				// if(hmEmpEmail==null){
				// hmEmpEmail = new HashMap<String, String>();
				// }
				//
				// address = new InternetAddress[hmEmpEmail.size()];
				// Set set = hmEmpEmail.keySet();
				// Iterator it = set.iterator();
				// int count=0;
				// while(it.hasNext()){
				// String strEmail = (String)hmEmpEmail.get((String)it.next());
				// if(strEmail!=null){
				// address[count++] = new InternetAddress(strEmail);
				// }
				// }
			}

			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject(getStrEmailSubject());
			msg.setSentDate(new Date());

			// System.out.println("getStrEmailBody() ===> " +
			// getStrEmailBody());
			// Set message content
			messageBodyPart.setContent(getStrEmailBody(), "text/html");
			multipart.addBodyPart(messageBodyPart);

			if (getStrAttachmentFileSource() != null) {
				DataSource source = new FileDataSource(getStrAttachmentFileSource());
				attachmentPart.setDataHandler(new DataHandler(source));
				attachmentPart.setFileName(source.getName());
				if (getStrAttachmentFileName() != null) {
					attachmentPart.setFileName(getStrAttachmentFileName());
				}
				multipart.addBodyPart(attachmentPart);
			}

			if (pdf_data != null) {

				DataSource dataSource = new ByteArrayDataSource(pdf_data, "application/pdf");
				MimeBodyPart pdfBodyPart = new MimeBodyPart();
				pdfBodyPart.setDataHandler(new DataHandler(dataSource));
				pdfBodyPart.setFileName(getStrAttachmentFileName());

				multipart.addBodyPart(pdfBodyPart);
			}
			if (xls_data != null) {

				DataSource dataSource = new ByteArrayDataSource(xls_data, "application/xls");
				MimeBodyPart pdfBodyPart = new MimeBodyPart();
				pdfBodyPart.setDataHandler(new DataHandler(dataSource));
				pdfBodyPart.setFileName(getStrAttachmentFileName());

				multipart.addBodyPart(pdfBodyPart);
			}

			// Associate multi-part with message
			msg.setContent(multipart);

			// Send message
			// Transport.send(msg);
			Transport t = session.getTransport("smtp");
			t.connect(getStrHost(), getStrEmailFrom(), getStrHostPassword());
			t.sendMessage(msg, msg.getAllRecipients());

			db.closeConnection(con);
		} catch (MessagingException mex) {
			// Prints all nested (chained) exceptions as well
			mex.printStackTrace();
		} finally {
			db.closeConnection(con);
		}

	}

	private void sendEmailNotificationsFromGmail() {

		// Create properties, get Session
		Properties props = new Properties();
		// String PORT = "587";
		// If using static Transport.send(),
		// need to specify which host to send it to
		props.put("mail.smtp.host", getStrHost());
		// To see what is going on behind the scene
		props.put("mail.debug", "true");
		// props.put("mail.smtp.port", PORT);

		/*
		 * Properties props = new Properties(); props.put("mail.smtp.host",
		 * "smtp.gmail.com"); // props.put("mail.smtp.port", "587");
		 * props.put("mail.smtp.port", "465"); props.put("mail.debug", "true");
		 * props.put("mail.smtp.auth", "true");
		 * props.put("mail.smtp.starttls.enable", "true");
		 * //props.put("mail.smtp.localhost", "http://www.mycatchapp.com");
		 * 
		 * props.put("mail.smtp.socketFactory.port", "465");
		 * props.put("mail.smtp.socketFactory.class",
		 * "javax.net.ssl.SSLSocketFactory");
		 */

		Session session = Session.getInstance(props);

		try {
			// Instantiatee a message
			MimeMessage msg = new MimeMessage(session);
			Multipart multipart = new MimeMultipart();
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			MimeBodyPart attachmentPart = new MimeBodyPart();

			// Set message attributes
			msg.setFrom(new InternetAddress(getStrEmailFrom()));

			InternetAddress[] address = null;

			// System.out.println("getStrEmailTo()===>"+getStrEmailTo());

			if (getStrEmailTo() != null && !getStrEmailTo().equalsIgnoreCase(EVERYONE)) {
				address = new InternetAddress[1];
				address[0] = new InternetAddress(getStrEmailTo());
			} else if (getStrEmailTo() != null && getStrEmailTo().equalsIgnoreCase(EVERYONE)) {
				Map<String, String> hmCandiEmail = getCandiEmailMap();
				if (hmCandiEmail == null) {
					hmCandiEmail = new HashMap<String, String>();
				}

				address = new InternetAddress[hmCandiEmail.size()];

				Set set = hmCandiEmail.keySet();
				Iterator it = set.iterator();
				int count = 0;
				while (it.hasNext()) {
					String strEmail = (String) hmCandiEmail.get((String) it.next());
					if (strEmail != null) {
						address[count++] = new InternetAddress(strEmail);
					}
				}
			}

			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject(getStrEmailSubject());
			msg.setSentDate(new Date());

			// Set message content
			messageBodyPart.setContent(getStrEmailBody(), "text/html");
			multipart.addBodyPart(messageBodyPart);

			// System.out.println("messageBodyPart===>"+getStrEmailBody());

			if (getStrAttachmentFileSource() != null) {

				DataSource source = new FileDataSource(getStrAttachmentFileSource());
				attachmentPart.setDataHandler(new DataHandler(source));
				attachmentPart.setFileName(source.getName());
				if (getStrAttachmentFileName() != null) {
					attachmentPart.setFileName(getStrAttachmentFileName());
				}

				multipart.addBodyPart(attachmentPart);
			}

			if (pdf_data != null) {

				DataSource dataSource = new ByteArrayDataSource(pdf_data, "application/pdf");
				MimeBodyPart pdfBodyPart = new MimeBodyPart();
				pdfBodyPart.setDataHandler(new DataHandler(dataSource));
				pdfBodyPart.setFileName(getStrAttachmentFileName());
				multipart.addBodyPart(pdfBodyPart);
			}
			if (xls_data != null) {

				DataSource dataSource = new ByteArrayDataSource(xls_data, "application/xls");
				MimeBodyPart pdfBodyPart = new MimeBodyPart();
				pdfBodyPart.setDataHandler(new DataHandler(dataSource));
				pdfBodyPart.setFileName(getStrAttachmentFileName());
				multipart.addBodyPart(pdfBodyPart);
			}

			// Associate multi-part with message
			msg.setContent(multipart);

			// Send message
			// Transport.send(msg);
			// System.out.println("getStrHost() ===> "+getStrHost());
			// System.out.println("getStrEmailFrom() ===> "+getStrEmailFrom());
			// System.out.println("getStrHostPassword() ===>
			// "+getStrHostPassword());
			Transport t = session.getTransport("smtp");
			t.connect(getStrHost(), getStrEmailFrom(), getStrHostPassword());
			t.sendMessage(msg, msg.getAllRecipients());

		} catch (MessagingException mex) {
			// Prints all nested (chained) exceptions as well
			mex.printStackTrace();
		}

	}

	byte[] pdf_data;
	public void setPdfData(byte[] pdf_data) {
		this.pdf_data = pdf_data;
	}

	byte[] xls_data;
	public void setXlsData(byte[] xls_data) {
		this.xls_data = xls_data;
	}

	public Map<String, String> getCandiEmailMap() {
		Map<String, String> hmCandiCodeEmail = new HashMap<String, String>();

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		db.setDomain(strDomain);

		try {

			con = db.makeConnection(con);

			pst = con.prepareStatement("SELECT emp_per_id, FROM candidate_personal_details where application_status>=1 and "
					+ "candidate_final_status=0 and send_notification_status=1 order by emp_per_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmCandiCodeEmail.put(rs.getString("emp_per_id"), rs.getString("emp_email"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
			// log.error(e.getClass() + ": " + e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmCandiCodeEmail;
	}

	private void setCandidateDetails() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		db.setDomain(strDomain);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;

		try {

			// System.out.println("setCandidateDetails ===>> ");
			con = db.makeConnection(con);
			if (CF == null)
				CF = new CommonFunctions(request);

			if (CF.getStrReportDateFormat() != null)
				setStrDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, CF.getStrReportDateFormat()));

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));

			Map<String, String> hmOrgMap = CF.getOrgName(con);
			Map<String, String> hmWLocMap = CF.getWLocationMap(con, null, null);
			Map<String, String> hmDepartMap = CF.getDeptMap(con);
			Map<String, String> hmServMap = CF.getServicesMap(con, false);
			// Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap();
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			Map<String, String> hmDesigMap = CF.getDesigMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			Map<String, String> hmCandiDesigMap = new HashMap<String, String>();
			// select * from grades_details gd, designation_details dd,
			// level_details ld, employee_official_details eod where
			// dd.designation_id = gd.designation_id and ld.level_id =
			// dd.level_id and gd.grade_id = eod.grade_id

			// Map hmWLocationMap = CF.getWorkLocationMap();

			pst = con.prepareStatement("select * from candidate_personal_details cpd, candidate_application_details cad where cpd.emp_per_id = cad.candidate_id and cad.candi_application_deatils_id= ?");// candi_application_deatils_id
																																										// cad.candidate_id
			// pst = con.prepareStatement("select * from (select * from
			// employee_personal_details epd, user_details ud where ud.emp_id =
			// epd.emp_per_id " +
			// "and emp_per_id = ?) a left join employee_official_details eod on
			// a.emp_per_id = eod.emp_id");
			pst.setInt(1, uF.parseToInt(getStrCandiId()));
//			 System.out.println("pst1 ==================>> " + pst);
			rs = pst.executeQuery();
			int nCount = 0;
			while (rs.next()) {
				setStrCandiCode(rs.getString("empcode"));
//				setStrRecruitmentId(rs.getString("recruitment_id"));

				if (rs.getString("emp_email_sec") != null && rs.getString("emp_email_sec").indexOf("@") > 0) {
					setStrCandiEmail(rs.getString("emp_email_sec"));
					setStrEmailTo(rs.getString("emp_email_sec"));
				} else {
					setStrCandiEmail(rs.getString("emp_email"));
					setStrEmailTo(rs.getString("emp_email"));
				}

				setStrCandiFname(rs.getString("emp_fname"));
				setStrCandiLname(rs.getString("emp_lname"));

				// setStrCandiAddress(uF.showData(rs.getString("emp_address1"),
				// "") + (!uF.showData(rs.getString("emp_address1"),
				// "").equals("") ? ",<br>" : "") +
				// uF.showData(rs.getString("emp_address2"),
				// "")+(!uF.showData(rs.getString("emp_address2"),
				// "").equals("") ? ",<br>" : "")
				// +uF.showData(rs.getString("emp_city_id"), ""));
				setStrCandiAddress(uF.getAddress(rs.getString("emp_address1"), rs.getString("emp_address2"), rs.getString("emp_city_id"), "<br>", uF));// Created By Dattatray Date:13-09-21
				//Started By Dattatray Date:05-10-21
				int cnt = StringUtils.countMatches(getStrCandiAddress(), "<br>");
				setIntAddressLineCnt(cnt);
				//Started By Dattatray Date:05-10-21
				String strEmpMName = "";
				if (flagMiddleName) {
					if (rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length() > 0) {
						strEmpMName = " " + rs.getString("emp_mname");
					}
				}

				setStrCandiFullName(rs.getString("emp_fname") + strEmpMName + " " + rs.getString("emp_lname"));
				setStrCandiMobileNo(rs.getString("emp_contactno_mob"));
				// setStrAccountNo(rs.getString("emp_bank_acct_nbr"));
				// setStrUserName(rs.getString("username"));
				// setStrPassword(rs.getString("password"));
				if (rs.getString("candidate_joining_date") != null) {
					setStrCandiJoiningDate(uF.getDateFormat(rs.getString("candidate_joining_date"), DBDATE, CF.getStrReportDateFormat()));
					setStrOfferAcceptanceLastDate(uF.getDateFormat(uF.getPrevDate(uF.getDateFormatUtil(rs.getString("candidate_joining_date"), DBDATE), 5) + "",
							DBDATE, CF.getStrReportDateFormat()));
				} else {
					setStrCandiJoiningDate("-");
					setStrOfferAcceptanceLastDate("-");
				}

				setStrCandiDesignation(uF.showData(hmCandiDesigMap.get(rs.getString("recruitment_id")), ""));

				nCount++;
			}
			rs.close();
			pst.close();
			// System.out.println("nCount++ : "+nCount);
			// Start Dattatray Date:12-08-21
			if (nCount == 0) {

				pst = con.prepareStatement("select * from candidate_personal_details cpd where cpd.emp_per_id= ?");
				pst.setInt(1, uF.parseToInt(getStrCandiId()));
				// System.out.println("pst new ==================>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					setStrCandiCode(rs.getString("empcode"));

					if (rs.getString("emp_email_sec") != null && rs.getString("emp_email_sec").indexOf("@") > 0) {
						setStrCandiEmail(rs.getString("emp_email_sec"));
						setStrEmailTo(rs.getString("emp_email_sec"));
					} else {
						setStrCandiEmail(rs.getString("emp_email"));
						setStrEmailTo(rs.getString("emp_email"));
					}

					setStrCandiFname(rs.getString("emp_fname"));
					setStrCandiLname(rs.getString("emp_lname"));

					// setStrCandiAddress(uF.showData(rs.getString("emp_address1"),
					// "") + (!uF.showData(rs.getString("emp_address1"),
					// "").equals("") ? ",<br>" : "") +
					// uF.showData(rs.getString("emp_address2"),
					// "")+(!uF.showData(rs.getString("emp_address2"),
					// "").equals("") ? ",<br>" : "")
					// +uF.showData(rs.getString("emp_city_id"), ""));
					setStrCandiAddress(uF.getAddress(rs.getString("emp_address1"), rs.getString("emp_address2"), rs.getString("emp_city_id"), "<br>", uF));// Created
																																							// By
																																							// Dattatray
																																							// Date:13-09-21
					String strEmpMName = "";
					if (flagMiddleName) {
						if (rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length() > 0) {
							strEmpMName = " " + rs.getString("emp_mname");
						}
					}

					setStrCandiFullName(rs.getString("emp_fname") + strEmpMName + " " + rs.getString("emp_lname"));
					setStrCandiMobileNo(rs.getString("emp_contactno_mob"));
					if (rs.getString("candidate_joining_date") != null) {
						setStrCandiJoiningDate(uF.getDateFormat(rs.getString("candidate_joining_date"), DBDATE, CF.getStrReportDateFormat()));
						setStrOfferAcceptanceLastDate(
								uF.getDateFormat(uF.getPrevDate(uF.getDateFormatUtil(rs.getString("candidate_joining_date"), DBDATE), 5) + "", DBDATE,
										CF.getStrReportDateFormat()));
					} else {
						setStrCandiJoiningDate("-");
						setStrOfferAcceptanceLastDate("-");
					}
				}
				rs.close();
				pst.close();
			}

			// End Dattatray Date:12-08-21

			pst = con.prepareStatement("select * from recruitment_details where recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getStrRecruitmentId()));
//			 System.out.println("pst =================>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				setStrLegalEntityName(hmOrgMap.get(rs.getString("org_id")));
				setStrRecruitmentWLocation(hmWLocMap.get(rs.getString("wlocation")));
				setStrRecruitmentLevel(hmLevelMap.get(rs.getString("level_id")));
				setStrRecruitmentDesignation(hmDesigMap.get(rs.getString("designation_id")));
				setStrRecruitmentGrade(hmGradeMap.get(rs.getString("grade_id")));
				// hmCandiDesigMap.put(rs.getString("recruitment_id"),
				// rs.getString("designation_name"));

			}
			rs.close();
			pst.close();
			// System.out.println("getStrLegalEntityName() ===>> "+
			// getStrLegalEntityName());
			// System.out.println("getStrRecruitmentWLocation() ===>> "+
			// getStrRecruitmentWLocation());
			// System.out.println("getStrRecruitmentLevel() ===>> "+
			// getStrRecruitmentLevel());
			// System.out.println("getStrRecruitmentDesignation() ===>> "+
			// getStrRecruitmentDesignation());

			// System.out.println("getStrOfferAcceptanceLastDate() ===>> "+
			// getStrOfferAcceptanceLastDate());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	int nNotificationCode;
	String strHostAddress;
	String strContextPath;
	String strHostPort;

	String strDate;

	boolean isSupervisor;
	String strCandidateId;
	String strCandiId;
	String strCandiCode;
	String strCandiFullName;
	String strCandiSalutation;
	String strCandiFname;
	String strCandiLname;
	String strCandiAddress;
	String strCandiJoiningDate;

	String strOfferAcceptanceLastDate;

	String strCandiCTC;
	String strCandiCTCInWords;
	String strCandiAnnualCTC;

	String strCandiDesignation;

	String strCandiEmail;
	String strCandiMobileNo;
	String strAddCandiLink;
	String strCandiBGVerificationLink;
	String strCandiTakeAssessLink;

	String strOfferedSalaryStructure;
	String strCandiSalBasic;
	String strCandiSalHRA;
	String strCandiSalConvAllow;
	String strCandiSalOverTime;
	String strCandiSalGratuity;
	String strCandiSalBonus;
	String strCandiSalMobExpenses;
	String strCandiSalMedicalAllow;
	String strCandiSalSpecialAllow;
	String strCandiSalArrearsAndOtherAllow;
	String strCandiTotGrossSalary;

	String strCandiSalDeductProftax;
	String strCandiSalDeductTDS;
	String strCandiSalDeductPFEmpCont;
	String strCandiSalDeductPFEmprCont;
	String strCandiSalDeductESIEmpr;
	String strCandiSalDeductESIEmp;
	String strCandiSalDeductLoan;
	String strCandiTotDeduction;

	String strNewEmailSubject;
	String strNewEmailTo;
	String strNewEmailFrom;
	String strNewEmailBody;

	String strEmailSubject;
	String strEmailTo;
	String strEmailBody;

	String strTextFrom;
	String strTextContent;

	String strUserName;
	String strPassword;
	String strNewPassword;

	String strHost;
	String strEmailFrom;
	String strHostPassword;
	String strAttachmentFileSource;
	String strAttachmentFileName;

	String strRecruitmentId;
	String strRecruitmentPosition;
	String strRecruitmentNotificationCOde;
	String strRecruitmentLevel;
	String strRecruitmentDesignation;
	String strRecruitmentGrade;
	String strRecruitmentWLocation;
	String strRecruitmentProfile;
	String strRecruitmentSkill;
	String strAddCandidateStep8;
	String offerAcceptData;
	String onboardingData;

	String strOrganisationAddress;
	String strLegalEntityName;
	String strLegalEntityAddress;
	String strDepartmentName;
	String strSbuName;
	String strReportEmpDesignation;
	String strSkills;
	String strEffectiveDate;
	String strPromotionGrade;
	String strPromotionLevel;
	String strPromotionDesignation;
	String strPromotionDate;
	String strIncrementAmount;
	String strIncrementPercent;
	String strTerminateDate;
	String strLastDateAtOffice;
	String strSalaryStructure;
	String strPayStructure;
	String strPayrollAmount;
	String strPayrollAmountWords;
	String strPayYear;
	String strPayMonth;
	String strAllowanceAmount;
	String strAllowanceAmountWords;
	String strBanks;
	String strBankCode;

	// Start Dattatray Date:10-08-21
	String strJobTitle;
	String strRecruiterName;
	String strJobPosition;
	String strRoundNo;
	String strRecruiterSignature;
	// End Dattatray Date:10-08-21

	// Start Dattatray Date:11-08-21
	String strPreRoundNo;
	String strNextRoundNo;
	String strInterviewTime;
	// End Dattatray Date:11-08-21
	
	
	//===start parvez date: 30-10-2021===
	String strCandiExpectedCTC;
	String strCandiNoticePeriod;
	String strCandiExperience;
	String strCandiCurrentOrg;
	String strCandiPrimarySkill;
	String strCandiSecondarySkill;
	String strCandiCurrentLocation;
	String strCandiPreferredLocation;
	//===end parvez date: 30-10-2021===

	//===start parvez date: 10-01-2022===
		String strAdditionalComment;
		String strJoiningBonusAmount;
	//===end parvez date: 10-01-2022===

	
	Map<String, String> hmFeatureStatus;
	Map<String, List<String>> hmFeatureUserTypeId;
	
	public Map<String, String> getHmFeatureStatus() {
		return hmFeatureStatus;
	}

	public void setHmFeatureStatus(Map<String, String> hmFeatureStatus) {
		this.hmFeatureStatus = hmFeatureStatus;
	}

	public Map<String, List<String>> getHmFeatureUserTypeId() {
		return hmFeatureUserTypeId;
	}

	public void setHmFeatureUserTypeId(Map<String, List<String>> hmFeatureUserTypeId) {
		this.hmFeatureUserTypeId = hmFeatureUserTypeId;
	}
	
	int intAddressLineCnt;
	public String getStrOfferedSalaryStructure() {
		return strOfferedSalaryStructure;
	}

	public void setStrOfferedSalaryStructure(String strOfferedSalaryStructure) {
		this.strOfferedSalaryStructure = strOfferedSalaryStructure;
	}

	public String getOnboardingData() {
		return onboardingData;
	}

	public void setOnboardingData(String onboardingData) {
		this.onboardingData = onboardingData;
	}
	String strCandiInterviewDateTime;

	public String getStrCandiInterviewDateTime() {
		return strCandiInterviewDateTime;
	}

	public void setStrCandiInterviewDateTime(String strCandiInterviewDateTime) {
		this.strCandiInterviewDateTime = strCandiInterviewDateTime;
	}

	public String getOfferAcceptData() {
		return offerAcceptData;
	}

	public void setOfferAcceptData(String offerAcceptData) {
		this.offerAcceptData = offerAcceptData;
	}

	public String getStrCandiTotGrossSalary() {
		return strCandiTotGrossSalary;
	}

	public void setStrCandiTotGrossSalary(String strCandiTotGrossSalary) {
		this.strCandiTotGrossSalary = strCandiTotGrossSalary;
	}

	public String getStrCandiTotDeduction() {
		return strCandiTotDeduction;
	}

	public void setStrCandiTotDeduction(String strCandiTotDeduction) {
		this.strCandiTotDeduction = strCandiTotDeduction;
	}

	public String getStrCandiSalBasic() {
		return strCandiSalBasic;
	}

	public void setStrCandiSalBasic(String strCandiSalBasic) {
		this.strCandiSalBasic = strCandiSalBasic;
	}

	public String getStrCandiSalHRA() {
		return strCandiSalHRA;
	}

	public void setStrCandiSalHRA(String strCandiSalHRA) {
		this.strCandiSalHRA = strCandiSalHRA;
	}

	public String getStrCandiSalConvAllow() {
		return strCandiSalConvAllow;
	}

	public void setStrCandiSalConvAllow(String strCandiSalConvAllow) {
		this.strCandiSalConvAllow = strCandiSalConvAllow;
	}

	public String getStrCandiSalOverTime() {
		return strCandiSalOverTime;
	}

	public void setStrCandiSalOverTime(String strCandiSalOverTime) {
		this.strCandiSalOverTime = strCandiSalOverTime;
	}

	public String getStrCandiSalGratuity() {
		return strCandiSalGratuity;
	}

	public void setStrCandiSalGratuity(String strCandiSalGratuity) {
		this.strCandiSalGratuity = strCandiSalGratuity;
	}

	public String getStrCandiSalBonus() {
		return strCandiSalBonus;
	}

	public void setStrCandiSalBonus(String strCandiSalBonus) {
		this.strCandiSalBonus = strCandiSalBonus;
	}

	public String getStrCandiSalMobExpenses() {
		return strCandiSalMobExpenses;
	}

	public void setStrCandiSalMobExpenses(String strCandiSalMobExpenses) {
		this.strCandiSalMobExpenses = strCandiSalMobExpenses;
	}

	public String getStrCandiSalMedicalAllow() {
		return strCandiSalMedicalAllow;
	}

	public void setStrCandiSalMedicalAllow(String strCandiSalMedicalAllow) {
		this.strCandiSalMedicalAllow = strCandiSalMedicalAllow;
	}

	public String getStrCandiSalSpecialAllow() {
		return strCandiSalSpecialAllow;
	}

	public void setStrCandiSalSpecialAllow(String strCandiSalSpecialAllow) {
		this.strCandiSalSpecialAllow = strCandiSalSpecialAllow;
	}

	public String getStrCandiSalArrearsAndOtherAllow() {
		return strCandiSalArrearsAndOtherAllow;
	}

	public void setStrCandiSalArrearsAndOtherAllow(String strCandiSalArrearsAndOtherAllow) {
		this.strCandiSalArrearsAndOtherAllow = strCandiSalArrearsAndOtherAllow;
	}

	public String getStrCandiSalDeductProftax() {
		return strCandiSalDeductProftax;
	}

	public void setStrCandiSalDeductProftax(String strCandiSalDeductProftax) {
		this.strCandiSalDeductProftax = strCandiSalDeductProftax;
	}

	public String getStrCandiSalDeductTDS() {
		return strCandiSalDeductTDS;
	}

	public void setStrCandiSalDeductTDS(String strCandiSalDeductTDS) {
		this.strCandiSalDeductTDS = strCandiSalDeductTDS;
	}

	public String getStrCandiSalDeductPFEmpCont() {
		return strCandiSalDeductPFEmpCont;
	}

	public void setStrCandiSalDeductPFEmpCont(String strCandiSalDeductPFEmpCont) {
		this.strCandiSalDeductPFEmpCont = strCandiSalDeductPFEmpCont;
	}

	public String getStrCandiSalDeductPFEmprCont() {
		return strCandiSalDeductPFEmprCont;
	}

	public void setStrCandiSalDeductPFEmprCont(String strCandiSalDeductPFEmprCont) {
		this.strCandiSalDeductPFEmprCont = strCandiSalDeductPFEmprCont;
	}

	public String getStrCandiSalDeductESIEmpr() {
		return strCandiSalDeductESIEmpr;
	}

	public void setStrCandiSalDeductESIEmpr(String strCandiSalDeductESIEmpr) {
		this.strCandiSalDeductESIEmpr = strCandiSalDeductESIEmpr;
	}

	public String getStrCandiSalDeductESIEmp() {
		return strCandiSalDeductESIEmp;
	}

	public void setStrCandiSalDeductESIEmp(String strCandiSalDeductESIEmp) {
		this.strCandiSalDeductESIEmp = strCandiSalDeductESIEmp;
	}

	public String getStrCandiSalDeductLoan() {
		return strCandiSalDeductLoan;
	}

	public void setStrCandiSalDeductLoan(String strCandiSalDeductLoan) {
		this.strCandiSalDeductLoan = strCandiSalDeductLoan;
	}

	public String getStrEmailSubject() {
		return strEmailSubject;
	}

	public void setStrEmailSubject(String strEmailSubject) {
		this.strEmailSubject = strEmailSubject;
	}

	public String getStrEmailTo() {
		return strEmailTo;
	}

	public void setStrEmailTo(String strEmailTo) {
		this.strEmailTo = strEmailTo;
	}

	private String getStrEmailBody() {
		return strEmailBody;
	}

	public void setStrEmailBody(String strEmailBody) {
		this.strEmailBody = strEmailBody;
	}

	private String getStrTextFrom() {
		return strTextFrom;
	}

	private void setStrTextFrom(String strTextFrom) {
		this.strTextFrom = strTextFrom;
	}

	private String getStrTextContent() {
		return strTextContent;
	}

	private void setStrTextContent(String strTextContent) {
		this.strTextContent = strTextContent;
	}

	private String getStrUserName() {
		return strUserName;
	}

	public void setStrUserName(String strUserName) {
		this.strUserName = strUserName;
	}

	public String getStrPassword() {
		return strPassword;
	}

	public void setStrPassword(String strPassword) {
		this.strPassword = strPassword;
	}

	private String getStrHost() {
		return strHost;
	}

	private void setStrHost(String strHost) {
		this.strHost = strHost;
	}

	private String getStrEmailFrom() {
		return strEmailFrom;
	}

	public void setStrEmailFrom(String strEmailFrom) {
		this.strEmailFrom = strEmailFrom;
	}

	public String getStrAttachmentFileName() {
		return strAttachmentFileName;
	}

	public void setStrAttachmentFileName(String strAttachmentFileName) {
		this.strAttachmentFileName = strAttachmentFileName;
	}

	public int getnNotificationCode() {
		return nNotificationCode;
	}

	public void setnNotificationCode(int nNotificationCode) {
		this.nNotificationCode = nNotificationCode;
	}

	public void setStrEmpId(String strCandiId) {
		// System.out.println("strCandiId ===> " + strCandiId);
		this.strCandiId = strCandiId;
		if (strCandiId != null && strCandiId.equalsIgnoreCase(EVERYONE)) {
			setStrEmailTo(EVERYONE);
		} else {
			setCandidateDetails();
		}
	}

	public String getStrCandiId() {
		return strCandiId;
	}

	public void setStrCandiId(String strCandiId) {
		this.strCandiId = strCandiId;
	}

	public String getStrCandiCode() {
		return strCandiCode;
	}

	public void setStrCandiCode(String strCandiCode) {
		this.strCandiCode = strCandiCode;
	}

	public String getStrCandiFullName() {
		return strCandiFullName;
	}

	public void setStrCandiFullName(String strCandiFullName) {
		this.strCandiFullName = strCandiFullName;
	}

	public String getStrCandiFname() {
		return strCandiFname;
	}

	public void setStrCandiFname(String strCandiFname) {
		this.strCandiFname = strCandiFname;
	}

	public String getStrCandiLname() {
		return strCandiLname;
	}

	public void setStrCandiLname(String strCandiLname) {
		this.strCandiLname = strCandiLname;
	}

	public String getStrCandiAddress() {
		return strCandiAddress;
	}

	public void setStrCandiAddress(String strCandiAddress) {
		this.strCandiAddress = strCandiAddress;
	}

	public String getStrCandiCTC() {
		return strCandiCTC;
	}

	public void setStrCandiCTC(String strCandiCTC) {
		this.strCandiCTC = strCandiCTC;
	}

	public String getStrCandiCTCInWords() {
		return strCandiCTCInWords;
	}

	public void setStrCandiCTCInWords(String strCandiCTCInWords) {
		this.strCandiCTCInWords = strCandiCTCInWords;
	}

	public String getStrCandiDesignation() {
		return strCandiDesignation;
	}

	public void setStrCandiDesignation(String strCandiDesignation) {
		this.strCandiDesignation = strCandiDesignation;
	}

	public String getStrCandiEmail() {
		return strCandiEmail;
	}

	public void setStrCandiEmail(String strCandiEmail) {
		this.strCandiEmail = strCandiEmail;
	}

	public String getStrCandiMobileNo() {
		return strCandiMobileNo;
	}

	public void setStrCandiMobileNo(String strCandiMobileNo) {
		this.strCandiMobileNo = strCandiMobileNo;
	}

	public String getStrAddCandiLink() {
		return strAddCandiLink;
	}

	public void setStrAddCandiLink(String strAddCandiLink) {
		this.strAddCandiLink = strAddCandiLink;
	}

	public String getStrCandiBGVerificationLink() {
		return strCandiBGVerificationLink;
	}

	public void setStrCandiBGVerificationLink(String strCandiBGVerificationLink) {
		this.strCandiBGVerificationLink = strCandiBGVerificationLink;
	}

	public boolean isSupervisor() {
		return isSupervisor;
	}

	public void setSupervisor(boolean isSupervisor) {
		this.isSupervisor = isSupervisor;
	}

	public String getStrHostAddress() {
		return strHostAddress;
	}

	public void setStrHostAddress(String strHostAddress) {
		this.strHostAddress = strHostAddress;
	}

	public String getStrContextPath() {
		return strContextPath;
	}

	public void setStrContextPath(String strContextPath) {
		this.strContextPath = strContextPath;
	}

	public String getStrAttachmentFileSource() {
		return strAttachmentFileSource;
	}

	public void setStrAttachmentFileSource(String strAttachmentFileSource) {
		this.strAttachmentFileSource = strAttachmentFileSource;
	}

	public String getStrNewEmailSubject() {
		return strNewEmailSubject;
	}

	public void setStrNewEmailSubject(String strNewEmailSubject) {
		this.strNewEmailSubject = strNewEmailSubject;
	}

	public String getStrNewEmailTo() {
		return strNewEmailTo;
	}

	public void setStrNewEmailTo(String strNewEmailTo) {
		this.strNewEmailTo = strNewEmailTo;
	}

	public String getStrNewEmailFrom() {
		return strNewEmailFrom;
	}

	public void setStrNewEmailFrom(String strNewEmailFrom) {
		this.strNewEmailFrom = strNewEmailFrom;
	}

	public String getStrNewEmailBody() {
		return strNewEmailBody;
	}

	public void setStrNewEmailBody(String strNewEmailBody) {
		this.strNewEmailBody = strNewEmailBody;
	}

	public String getStrNewPassword() {
		return strNewPassword;
	}

	public void setStrNewPassword(String strNewPassword) {
		this.strNewPassword = strNewPassword;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	public String getStrHostPassword() {
		return strHostPassword;
	}

	public void setStrHostPassword(String strHostPassword) {
		this.strHostPassword = strHostPassword;
	}

	public String getStrCandiJoiningDate() {
		return strCandiJoiningDate;
	}

	public void setStrCandiJoiningDate(String strCandiJoiningDate) {
		this.strCandiJoiningDate = strCandiJoiningDate;
	}

	public String getStrRecruitmentPosition() {
		return strRecruitmentPosition;
	}

	public void setStrRecruitmentPosition(String strRecruitmentPosition) {
		this.strRecruitmentPosition = strRecruitmentPosition;
	}

	public String getStrRecruitmentLevel() {
		return strRecruitmentLevel;
	}

	public void setStrRecruitmentLevel(String strRecruitmentLevel) {
		this.strRecruitmentLevel = strRecruitmentLevel;
	}

	public String getStrRecruitmentDesignation() {
		return strRecruitmentDesignation;
	}

	public void setStrRecruitmentDesignation(String strRecruitmentDesignation) {
		this.strRecruitmentDesignation = strRecruitmentDesignation;
	}

	public String getStrRecruitmentGrade() {
		return strRecruitmentGrade;
	}

	public void setStrRecruitmentGrade(String strRecruitmentGrade) {
		this.strRecruitmentGrade = strRecruitmentGrade;
	}

	public String getStrRecruitmentWLocation() {
		return strRecruitmentWLocation;
	}

	public void setStrRecruitmentWLocation(String strRecruitmentWLocation) {
		this.strRecruitmentWLocation = strRecruitmentWLocation;
	}

	public String getStrRecruitmentProfile() {
		return strRecruitmentProfile;
	}

	public void setStrRecruitmentProfile(String strRecruitmentProfile) {
		this.strRecruitmentProfile = strRecruitmentProfile;
	}

	public String getStrRecruitmentSkill() {
		return strRecruitmentSkill;
	}

	public void setStrRecruitmentSkill(String strRecruitmentSkill) {
		this.strRecruitmentSkill = strRecruitmentSkill;
	}

	public String getStrAddCandidateStep8() {
		return strAddCandidateStep8;
	}

	public void setStrAddCandidateStep8(String strAddCandidateStep8) {
		this.strAddCandidateStep8 = strAddCandidateStep8;
	}

	public String getStrCandiTakeAssessLink() {
		return strCandiTakeAssessLink;
	}

	public void setStrCandiTakeAssessLink(String strCandiTakeAssessLink) {
		this.strCandiTakeAssessLink = strCandiTakeAssessLink;
	}

	public HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	String strDomain;
	public void setDomain(String strDomain) {
		this.strDomain = strDomain;
	}

	public String getStrHostPort() {
		return strHostPort;
	}

	public void setStrHostPort(String strHostPort) {
		this.strHostPort = strHostPort;
	}

	public String getStrRecruitmentNotificationCOde() {
		return strRecruitmentNotificationCOde;
	}

	public void setStrRecruitmentNotificationCOde(String strRecruitmentNotificationCOde) {
		this.strRecruitmentNotificationCOde = strRecruitmentNotificationCOde;
	}

	public String getStrOrganisationAddress() {
		return strOrganisationAddress;
	}

	public void setStrOrganisationAddress(String strOrganisationAddress) {
		this.strOrganisationAddress = strOrganisationAddress;
	}

	public String getStrLegalEntityName() {
		return strLegalEntityName;
	}

	public void setStrLegalEntityName(String strLegalEntityName) {
		this.strLegalEntityName = strLegalEntityName;
	}

	public String getStrLegalEntityAddress() {
		return strLegalEntityAddress;
	}

	public void setStrLegalEntityAddress(String strLegalEntityAddress) {
		this.strLegalEntityAddress = strLegalEntityAddress;
	}

	public String getStrDepartmentName() {
		return strDepartmentName;
	}

	public void setStrDepartmentName(String strDepartmentName) {
		this.strDepartmentName = strDepartmentName;
	}

	public String getStrSbuName() {
		return strSbuName;
	}

	public void setStrSbuName(String strSbuName) {
		this.strSbuName = strSbuName;
	}

	public String getStrReportEmpDesignation() {
		return strReportEmpDesignation;
	}

	public void setStrReportEmpDesignation(String strReportEmpDesignation) {
		this.strReportEmpDesignation = strReportEmpDesignation;
	}

	public String getStrSkills() {
		return strSkills;
	}

	public void setStrSkills(String strSkills) {
		this.strSkills = strSkills;
	}

	public String getStrEffectiveDate() {
		return strEffectiveDate;
	}

	public void setStrEffectiveDate(String strEffectiveDate) {
		this.strEffectiveDate = strEffectiveDate;
	}

	public String getStrPromotionGrade() {
		return strPromotionGrade;
	}

	public void setStrPromotionGrade(String strPromotionGrade) {
		this.strPromotionGrade = strPromotionGrade;
	}

	public String getStrPromotionLevel() {
		return strPromotionLevel;
	}

	public void setStrPromotionLevel(String strPromotionLevel) {
		this.strPromotionLevel = strPromotionLevel;
	}

	public String getStrPromotionDesignation() {
		return strPromotionDesignation;
	}

	public void setStrPromotionDesignation(String strPromotionDesignation) {
		this.strPromotionDesignation = strPromotionDesignation;
	}

	public String getStrPromotionDate() {
		return strPromotionDate;
	}

	public void setStrPromotionDate(String strPromotionDate) {
		this.strPromotionDate = strPromotionDate;
	}

	public String getStrIncrementAmount() {
		return strIncrementAmount;
	}

	public void setStrIncrementAmount(String strIncrementAmount) {
		this.strIncrementAmount = strIncrementAmount;
	}

	public String getStrIncrementPercent() {
		return strIncrementPercent;
	}

	public void setStrIncrementPercent(String strIncrementPercent) {
		this.strIncrementPercent = strIncrementPercent;
	}

	public String getStrTerminateDate() {
		return strTerminateDate;
	}

	public void setStrTerminateDate(String strTerminateDate) {
		this.strTerminateDate = strTerminateDate;
	}

	public String getStrLastDateAtOffice() {
		return strLastDateAtOffice;
	}

	public void setStrLastDateAtOffice(String strLastDateAtOffice) {
		this.strLastDateAtOffice = strLastDateAtOffice;
	}

	public String getStrSalaryStructure() {
		return strSalaryStructure;
	}

	public void setStrSalaryStructure(String strSalaryStructure) {
		this.strSalaryStructure = strSalaryStructure;
	}

	public String getStrPayStructure() {
		return strPayStructure;
	}

	public void setStrPayStructure(String strPayStructure) {
		this.strPayStructure = strPayStructure;
	}

	public String getStrPayrollAmount() {
		return strPayrollAmount;
	}

	public void setStrPayrollAmount(String strPayrollAmount) {
		this.strPayrollAmount = strPayrollAmount;
	}

	public String getStrPayrollAmountWords() {
		return strPayrollAmountWords;
	}

	public void setStrPayrollAmountWords(String strPayrollAmountWords) {
		this.strPayrollAmountWords = strPayrollAmountWords;
	}

	public String getStrPayYear() {
		return strPayYear;
	}

	public void setStrPayYear(String strPayYear) {
		this.strPayYear = strPayYear;
	}

	public String getStrPayMonth() {
		return strPayMonth;
	}

	public void setStrPayMonth(String strPayMonth) {
		this.strPayMonth = strPayMonth;
	}

	public String getStrAllowanceAmount() {
		return strAllowanceAmount;
	}

	public void setStrAllowanceAmount(String strAllowanceAmount) {
		this.strAllowanceAmount = strAllowanceAmount;
	}

	public String getStrAllowanceAmountWords() {
		return strAllowanceAmountWords;
	}

	public void setStrAllowanceAmountWords(String strAllowanceAmountWords) {
		this.strAllowanceAmountWords = strAllowanceAmountWords;
	}

	public String getStrBanks() {
		return strBanks;
	}

	public void setStrBanks(String strBanks) {
		this.strBanks = strBanks;
	}

	public String getStrBankCode() {
		return strBankCode;
	}

	public void setStrBankCode(String strBankCode) {
		this.strBankCode = strBankCode;
	}

	public boolean getIsRequiredAuthentication() {
		return isRequiredAuthentication;
	}

	public void setIsRequiredAuthentication(boolean isRequiredAuthentication) {
		this.isRequiredAuthentication = isRequiredAuthentication;
	}

	public String getStrEmailAuthUsername() {
		return strEmailAuthUsername;
	}

	public void setStrEmailAuthUsername(String strEmailAuthUsername) {
		this.strEmailAuthUsername = strEmailAuthUsername;
	}

	public String getStrEmailAuthPassword() {
		return strEmailAuthPassword;
	}

	public void setStrEmailAuthPassword(String strEmailAuthPassword) {
		this.strEmailAuthPassword = strEmailAuthPassword;
	}

	public String getStrCandiAnnualCTC() {
		return strCandiAnnualCTC;
	}

	public void setStrCandiAnnualCTC(String strCandiAnnualCTC) {
		this.strCandiAnnualCTC = strCandiAnnualCTC;
	}

	public String getStrOfferAcceptanceLastDate() {
		return strOfferAcceptanceLastDate;
	}

	public void setStrOfferAcceptanceLastDate(String strOfferAcceptanceLastDate) {
		this.strOfferAcceptanceLastDate = strOfferAcceptanceLastDate;
	}

	public String getStrRecruitmentId() {
		return strRecruitmentId;
	}

	public void setStrRecruitmentId(String strRecruitmentId) {
		this.strRecruitmentId = strRecruitmentId;
	}

	public String getStrCandiSalutation() {
		return strCandiSalutation;
	}

	public void setStrCandiSalutation(String strCandiSalutation) {
		this.strCandiSalutation = strCandiSalutation;
	}

	public String getStrJobTitle() {
		return strJobTitle;
	}

	public void setStrJobTitle(String strJobTitle) {
		this.strJobTitle = strJobTitle;
	}

	public String getStrRecruiterName() {
		return strRecruiterName;
	}

	public void setStrRecruiterName(String strRecruiterName) {
		this.strRecruiterName = strRecruiterName;
	}

	public String getStrJobPosition() {
		return strJobPosition;
	}

	public void setStrJobPosition(String strJobPosition) {
		this.strJobPosition = strJobPosition;
	}

	public String getStrRoundNo() {
		return strRoundNo;
	}

	public void setStrRoundNo(String strRoundNo) {
		this.strRoundNo = strRoundNo;
	}

	public String getStrRecruiterSignature() {
		return strRecruiterSignature;
	}

	public void setStrRecruiterSignature(String strRecruiterSignature) {
		this.strRecruiterSignature = strRecruiterSignature;
	}

	public String getStrPreRoundNo() {
		return strPreRoundNo;
	}

	public void setStrPreRoundNo(String strPreRoundNo) {
		this.strPreRoundNo = strPreRoundNo;
	}

	public String getStrNextRoundNo() {
		return strNextRoundNo;
	}

	public void setStrNextRoundNo(String strNextRoundNo) {
		this.strNextRoundNo = strNextRoundNo;
	}

	public String getStrInterviewTime() {
		return strInterviewTime;
	}

	public void setStrInterviewTime(String strInterviewTime) {
		this.strInterviewTime = strInterviewTime;
	}

	public String getStrCandidateId() {
		return strCandidateId;
	}

	public void setStrCandidateId(String strCandidateId) {
		this.strCandidateId = strCandidateId;
	}

	public int getIntAddressLineCnt() {
		return intAddressLineCnt;
	}

	public void setIntAddressLineCnt(int intAddressLineCnt) {
		this.intAddressLineCnt = intAddressLineCnt;
	}

//===start parvez date: 30-10-2021===
	public String getStrCandiNoticePeriod() {
		return strCandiNoticePeriod;
	}

	public void setStrCandiNoticePeriod(String strCandiNoticePeriod) {
		this.strCandiNoticePeriod = strCandiNoticePeriod;
	}

	public String getStrCandiExperience() {
		return strCandiExperience;
	}

	public void setStrCandiExperience(String strCandiExperience) {
		this.strCandiExperience = strCandiExperience;
	}

	public String getStrCandiCurrentOrg() {
		return strCandiCurrentOrg;
	}

	public void setStrCandiCurrentOrg(String strCandiCurrentOrg) {
		this.strCandiCurrentOrg = strCandiCurrentOrg;
	}

	public String getStrCandiPrimarySkill() {
		return strCandiPrimarySkill;
	}

	public void setStrCandiPrimarySkill(String strCandiPrimarySkill) {
		this.strCandiPrimarySkill = strCandiPrimarySkill;
	}

	public String getStrCandiSecondarySkill() {
		return strCandiSecondarySkill;
	}

	public void setStrCandiSecondarySkill(String strCandiSecondarySkill) {
		this.strCandiSecondarySkill = strCandiSecondarySkill;
	}

	public String getStrCandiCurrentLocation() {
		return strCandiCurrentLocation;
	}

	public void setStrCandiCurrentLocation(String strCandiCurrentLocation) {
		this.strCandiCurrentLocation = strCandiCurrentLocation;
	}

	public String getStrCandiPreferredLocation() {
		return strCandiPreferredLocation;
	}

	public void setStrCandiPreferredLocation(String strCandiPreferredLocation) {
		this.strCandiPreferredLocation = strCandiPreferredLocation;
	}

	public String getStrCandiExpectedCTC() {
		return strCandiExpectedCTC;
	}

	public void setStrCandiExpectedCTC(String strCandiExpectedCTC) {
		this.strCandiExpectedCTC = strCandiExpectedCTC;
	}
//===end parvez date: 30-10-2021===		


	//===start parvez date: 10-01-2022===	
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