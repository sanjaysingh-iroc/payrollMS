package com.konnect.jpms.recruitment;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillNoticeDuration;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSalutation;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillSourceTypeAndName;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CandidateNotifications;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.LogDetails;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddCandidateInOneStep extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF = null;
	
	private String operation;
	private String org_id;
	private String CandidateId;
	private String candibymail;
	private String fromPage;
	private String isEmpCode;
	private String refEmpId;
	private String refEmpCode;
	private String recruitId;
	
	private String salutation;
	private String empFname;
	private String empMname;
	private String empLname;
	private String empEmail;

	private String candiCurrCTC;
	private String candiExpectedCTC;
	private String candiNoticePeriod;
	
	private boolean approvedFlag;

	private Timestamp empFilledFlagDate;

	private String empImageFileName;
	private File empImage;

	private String empEmailSec;
	private String empMobileNo;
	private String availability;
	
	private int noticeDuration;

	private String[] skillName;
	private String[] skillValue;

	
	private String docId0;
	private File[] strResume;
	private String[] strResumeFileName;
	private File idDoc0;
	private String idDoc0FileName;
	private String idDocStatus0;
	private String idDocName0;
	private String idDocType0;
	
	private String docId1;
	private File idDoc1;
	private String idDoc1FileName;
	private String idDocStatus1;
	private String idDocName1;
	private String idDocType1;
	
	private String docId2;
	private File idDoc2;
	private String idDoc2FileName;
	private String idDocStatus2;
	private String idDocName2;
	private String idDocType2;
	
	private String[] documentNames;
	private String[] documentValues;

	// private String empDesignation;

	private List<FillSalutation> salutationList;
	private List<FillNoticeDuration> noticeDurationList;
	private List<FillYears> yearsList;
	private List<FillSkills> skillsList;
	
	private List<FillSourceTypeAndName> sourceTypeList;
	private List<FillOrganisation> orgList; 
	private List<FillWLocation> wLocationList;
	private String[] strWLocation;
	
	private String degreeNameOther;
	private String stepSubmit;
	private String jobcode[];
	private String redirectUrl;
	private String appliSourceType;
	private String candiTotalExperience;
	private String applyType;
	
	
	private StringBuilder sbServicesLink = new StringBuilder();
	private static Logger log = Logger.getLogger(AddCandidate.class);

	public String execute() {

		getJobCodeFromrecID();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			CF = new CommonFunctions();
			CF.setRequest(request);
			CF.getCommonFunctionsDetails(CF,request);
		}

		CF.getFormValidationFields(request, ADD_UPDATE_CANDIDATE);
		
		if (getOrg_id() == null) {
			setOrg_id((String)session.getAttribute(ORGID));
		}

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/recruitment/AddCandidateInOneStep.jsp");
		request.setAttribute(TITLE, "Add New Candidate");
		request.setAttribute(MENU, null);
		 UtilityFunctions uF = new UtilityFunctions();
		 
		loadValidateEmployee();
		getLiveJobs(uF);
		
//		System.out.println("getStepSubmit() ===>> " + getStepSubmit());
		if(getStepSubmit() != null) {
			insertEmployee();
			if(getFromPage() != null && getFromPage().equalsIgnoreCase("CR")) {
				return "crfinish";
			} else if(getFromPage() != null && getFromPage().equalsIgnoreCase("JO")) {
				return "jofinish";
			} else {
				return "finish";
			}
		}
		return LOAD;
	}

	private void getLiveJobs(UtilityFunctions uF){

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			
//			int queCnt=1;
			con = db.makeConnection(con);			
			
			setRefEmpCode(uF.showData(CF.getEmpCodeByEmpId(con, getRefEmpId()), ""));
			if(uF.parseToInt(getRefEmpId()) > 0) {
				setIsEmpCode("true");
			}
			StringBuilder sb = new StringBuilder("");
			pst = con.prepareStatement("select * from recruitment_details where close_job_status=false and job_approval_status = 1");
//			pst.setInt(1, uF.parseToInt(candidateId));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				if(uF.parseToInt(getRecruitId()) == uF.parseToInt(rs.getString("recruitment_id"))) {
					sb.append("<option value=\"" + rs.getString("recruitment_id") + "\" selected=\"selected\">" + rs.getString("job_code") + "</option>");
				} else {
					sb.append("<option value=\"" + rs.getString("recruitment_id") + "\">" + rs.getString("job_code") + "</option>");
				}
//				queCnt++;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("option", sb.toString());
//			request.setAttribute("queCnt", queCnt);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getJobCodeFromrecID() {

		PreparedStatement pst = null;
		Connection con = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select job_code,org_id from recruitment_details where recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rst = pst.executeQuery();
//			k"new Date ===> " + new Date());
			while (rst.next()) {
//				setJobcode(rst.getString("job_code"));
				setOrg_id(rst.getString("org_id"));
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
	}

	private void addCandidateInNewJob(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
//			String recruitID = request.getParameter("jobCode");
//			System.out.println("recruitID ===> "+recruitID);
			Map<String, String> hmCodeDesig = CF.getDesigMap(con);
			if(hmCodeDesig == null) hmCodeDesig = new HashMap<String, String>();
			
			if(getJobcode() != null) {
//				System.out.println("if getJobCode().length ===> "+getJobcode());
				StringBuilder sbJobCodeName = null;
				StringBuilder sbDesigName = null;
				for(int a=0; a<getJobcode().length; a++) {
					if(uF.parseToInt(getJobcode()[a])>0) {
						String jobcodeName = null;
						pst = con.prepareStatement("select job_code,designation_id from recruitment_details where recruitment_id= ? ");
						pst.setInt(1, uF.parseToInt(getJobcode()[a]));
						rs = pst.executeQuery();
			//			System.out.println("new Date ===> " + new Date());
						int nDesigId = 0;
						while (rs.next()) {
							jobcodeName = rs.getString("job_code");
							nDesigId = uF.parseToInt(rs.getString("designation_id"));
						}
						rs.close();
						pst.close();
						
						boolean checkJobCodeFlag = false;
						pst = con.prepareStatement("select job_code from candidate_application_details where recruitment_id= ? and candidate_id =?");
						pst.setInt(1, uF.parseToInt(getJobcode()[a]));
						pst.setInt(2, uF.parseToInt(getCandidateId()));
						rs = pst.executeQuery();
			//			System.out.println("new Date ===> " + new Date());
						while (rs.next()) {
							checkJobCodeFlag = true;
						}
						rs.close();
						pst.close();
						
			//			System.out.println("checkJobCodeFlag ===> " + checkJobCodeFlag);
						
						if(!checkJobCodeFlag) {
							pst=con.prepareStatement("insert into candidate_application_details (candidate_id,recruitment_id,job_code,application_date," +
									"added_by,entry_date,source_type,source_or_ref_code) values(?,?,?,?, ?,?,?,?)");
							pst.setInt(1, uF.parseToInt(getCandidateId()));
							pst.setInt(2, uF.parseToInt(getJobcode()[a]));
							pst.setString(3, jobcodeName);
							pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
							if(getAppliSourceType()!=null && !getAppliSourceType().equals("")) {
								pst.setInt(7, uF.parseToInt(getAppliSourceType()));
							} else {
								if(getFromPage() != null && getFromPage().equals("JO") && uF.parseToBoolean(getIsEmpCode()) && uF.parseToInt(getRefEmpId())>0) {
									pst.setInt(7, SOURCE_REFERENCE);
								} else if(getFromPage() != null && getFromPage().equals("JO") && !uF.parseToBoolean(getIsEmpCode()) && uF.parseToInt(getRefEmpId())==0) {
									pst.setInt(7, SOURCE_WEBSITE);
								} else if(strUserType != null && strUserType.equals(RECRUITER)) {
									pst.setInt(7, SOURCE_RECRUITER);
								} else {
									pst.setInt(7, SOURCE_HR);
								}
							}
							
							if(uF.parseToBoolean(getIsEmpCode()) && uF.parseToInt(getRefEmpId())>0) {
								pst.setInt(8, uF.parseToInt(getRefEmpId()));
							} else {
								pst.setInt(8, 0);
							}
							pst.executeUpdate();
							pst.close();
							
							if(sbJobCodeName == null) {
								sbJobCodeName = new StringBuilder();
								sbJobCodeName.append(jobcodeName);
							} else {
								sbJobCodeName.append(", "+jobcodeName);
							}
							if(sbDesigName == null){
								sbDesigName = new StringBuilder();
								sbDesigName.append(uF.showData(hmCodeDesig.get(""+nDesigId), ""));
							} else {
								sbDesigName.append(","+uF.showData(hmCodeDesig.get(""+nDesigId), ""));
							}
							
							pst=con.prepareStatement("delete from candidate_activity_details where recruitment_id=? and candi_id=? and user_id=? and " +
									"activity_id = ?");
							pst.setInt(1, uF.parseToInt(getJobcode()[a]));
							pst.setInt(2, uF.parseToInt(getCandidateId()));
							pst.setInt(3, uF.parseToInt(strSessionEmpId));
							pst.setInt(4, CANDI_ACTIVITY_APPLY_ID);
							pst.executeUpdate();
							pst.close();
							
							pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
							pst.setInt(1, uF.parseToInt(getJobcode()[a]));
							pst.setInt(2, uF.parseToInt(getCandidateId()));
							pst.setString(3, "Apply for Job");
							pst.setInt(4, uF.parseToInt(strSessionEmpId));
							pst.setDate(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
							pst.setInt(6, CANDI_ACTIVITY_APPLY_ID);
							pst.execute();
							pst.close();
						}
					}
				}
				if(sbDesigName == null){
					sbDesigName = new StringBuilder();
				}
				
				if(getFromPage() != null && getFromPage().equalsIgnoreCase("JO")) {
					session.setAttribute(MESSAGE, SUCCESSM+"Your profile is added to "+sbDesigName.toString()+" position successfully."+END);
//					System.out.println("MESSAGE ===>> " + (String)session.getAttribute(MESSAGE));
				} else {
					if(sbJobCodeName != null && !sbJobCodeName.toString().equals("")) {
						String candiName = CF.getCandiNameByCandiId(con, getCandidateId());
						session.setAttribute(MESSAGE, SUCCESSM+""+candiName+" is added in "+sbJobCodeName.toString()+" successfully."+END);
					}
				}
				// Start Dattatray Date:12-08-2021
				if((getApplyType() != null && getApplyType().equalsIgnoreCase("withoutjob")) || (getFromPage() != null && getFromPage().equalsIgnoreCase("CR")) ) {
					sendMail(N_FRESHER_JOB_SUBMISSION,false);
				}else if(getFromPage() != null && getFromPage().equalsIgnoreCase("A")){
					sendMail(N_RESUME_SUBMISSION,true);// Created By Dattatray Date:11-08-2021
					sendMailToHiringTeam(N_APPLICATION_SUBMISSION_TO_HIRING_TEAM);    //created by parvez date: 29-10-2021
				}// End Dattatray
				
			}
//			System.out.println("pst ===> "+pst);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
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
	
	/**
	 * Created By Dattatray
	 * @since 11-08-21
	 * @param notificationCode
	 */
	public void sendMail(int notificationCode,boolean candiID) {
		System.out.println("notificationCode : "+notificationCode);
		Connection con = null;
		ResultSet rst = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
				con = db.makeConnection(con);
				String job_title = "";
				pst = con.prepareStatement("select cad.candidate_joining_date,job_title,hiring_manager from candidate_application_details cad,recruitment_details rd where rd.recruitment_id =cad.recruitment_id and cad.recruitment_id = ?");
				pst.setInt(1, uF.parseToInt(getRecruitId()));
				rst = pst.executeQuery();
				while(rst.next()) {
					job_title = rst.getString("job_title");
				}
				rst.close();
				pst.close();
				
				Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
				Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
				
				String strDomain = request.getServerName().split("\\.")[0];
				CandidateNotifications nF = new CandidateNotifications(notificationCode, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				if (candiID == true) {
					 nF.setStrEmpId(getCandidateId());
				}else {
					
					nF.setStrEmailTo(getEmpEmail());
					nF.setIsEmailIdCC(true);
			 
				//===start parvez date: 28-10-2021===
					/*if(notificationCode != N_APPLICATION_SUBMISSION_TO_HIRING_TEAM){
						nF.setStrEmailTo(getEmpEmail());
						System.out.println("AC1s/440--getEmpEmail="+getEmpEmail());
						nF.setIsEmailIdCC(true);
					} else{
						pst = con.prepareStatement(selectSettings);
						rst = pst.executeQuery();
						String strHiringEmail=null;
						while(rst.next()) {
							if (rst.getString("options").equalsIgnoreCase(O_EMAIL_ID_CC)) {
								strHiringEmail = rst.getString("value");
							}
						}
						rst.close();
						pst.close();
						System.out.println("AC1s/451--strHiringEmail="+strHiringEmail);
						nF.setStrEmailTo(strHiringEmail);
					}*/
				//===end parvez date: 28-10-2021===
					
				}
				
				nF.setStrRecruitmentId(getRecruitId());
//				System.out.println("AC1s/449--getRecruitId="+getRecruitId());
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				 
				nF.setStrCandiFname(getEmpFname());
				nF.setStrCandiLname(getEmpLname());
				nF.setStrJobTitle(job_title);
				nF.setHmFeatureStatus(hmFeatureStatus);
				nF.setHmFeatureUserTypeId(hmFeatureUserTypeId);
				
				nF.sendNotifications();
				
			//===start parvez date: 28-10-2021===	
				/*if(notificationCode == N_APPLICATION_SUBMISSION_TO_HIRING_TEAM){
					CandidateNotifications nF1 = new CandidateNotifications(notificationCode, CF);
					nF1.setDomain(strDomain);
					nF1.request = request;
					
					pst = con.prepareStatement(selectSettings);
					rst = pst.executeQuery();
					String strHiringEmail=null;
					while(rst.next()) {
						if (rst.getString("options").equalsIgnoreCase(O_EMAIL_ID_CC)) {
							strHiringEmail = rst.getString("value");
						}
					}
					rst.close();
					pst.close();
					System.out.println("AC1s/451--strHiringEmail="+strHiringEmail);
					nF.setStrEmailTo(strHiringEmail);
						
				
					nF.setStrRecruitmentId(getRecruitId());
//					System.out.println("AC1s/449--getRecruitId="+getRecruitId());
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					 
					nF.setStrCandiFname(getEmpFname());
					nF.setStrCandiLname(getEmpLname());
					nF.setStrJobTitle(job_title);
					
					nF.sendNotifications();
				}*/
		//===end parvez date: 28-10-2021===
			 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	//===added parvez date: 29-10-2021===
	//===start===
	public void sendMailToHiringTeam(int notificationCode) {
//		System.out.println("AC1S/522--notificationCode : "+notificationCode);
		Connection con = null;
		ResultSet rst = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
				con = db.makeConnection(con);
				String job_title = "";
				pst = con.prepareStatement("select cad.candidate_joining_date,job_title,hiring_manager from candidate_application_details cad,recruitment_details rd where rd.recruitment_id =cad.recruitment_id and cad.recruitment_id = ?");
				pst.setInt(1, uF.parseToInt(getRecruitId()));
				rst = pst.executeQuery();
				while(rst.next()) {
					job_title = rst.getString("job_title");
				}
				rst.close();
				pst.close();
				
				String strDomain = request.getServerName().split("\\.")[0];
				
				pst = con.prepareStatement("SELECT * FROM candidate_documents_details WHERE emp_id = ? and documents_name=?");
				pst.setInt(1, uF.parseToInt(getCandidateId()));
				pst.setString(2, getIdDocName0());
				rst = pst.executeQuery();
				String docFileName=null;
				while(rst.next()) {
					docFileName = rst.getString("documents_file_name");
				}
				rst.close();
				pst.close();
				
				Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
				Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
				
//				System.out.println("docFileName="+docFileName);
			//===start parvez date: 28-10-2021===	
					CandidateNotifications nF = new CandidateNotifications(notificationCode, CF);
					nF.setDomain(strDomain);
					nF.request = request;
					nF.CF = CF;
					
					nF.setStrRecruitmentId(getRecruitId());
//					System.out.println("AC1s/449--getRecruitId="+getRecruitId());
					nF.setStrCandidateId(getCandidateId());
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					 
					nF.setStrCandiFname(getEmpFname());
					nF.setStrCandiLname(getEmpLname());
					nF.setStrJobTitle(job_title);
					nF.setStrCandiCTC(getCandiCurrCTC());
					nF.setStrCandiExpectedCTC(getCandiExpectedCTC());
					nF.setStrCandiNoticePeriod(getCandiNoticePeriod());
					nF.setStrCandiExperience(getCandiTotalExperience());
					nF.setIsEmailIdHireTeam(true);
					nF.setStrAttachmentFileName(docFileName);
					if(CF.getStrDocSaveLocation() == null){
						nF.setStrAttachmentFileSource(DOCUMENT_LOCATION+docFileName.trim());
					} else {
						nF.setStrAttachmentFileSource(CF.getStrDocSaveLocation() + I_CANDIDATE + "/" + I_DOCUMENT + "/" + I_ATTACHMENT + "/"+ getCandidateId()+ "/" + docFileName.trim());
					}
					nF.setHmFeatureStatus(hmFeatureStatus);
					nF.setHmFeatureUserTypeId(hmFeatureUserTypeId);
					
					nF.sendNotifications();
					
				
		//===end parvez date: 28-10-2021===
			 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	//===end===

	String[] strTime;
	String[] strDate;

//	public boolean insertAvailability(UtilityFunctions uF, String strEmpId) {
//
//		Connection con = null;
//		Database db = new Database();
//		db.setRequest(request);
//		boolean isValidSesseion = false;
//		try {
//
//			con = db.makeConnection(con);
//			PreparedStatement pst = null;
//
//			for (int i = 0; getStrDate() != null && i < getStrDate().length; i++) {
//
//				if (getStrDate()[i] != null && getStrDate()[i].length() > 0) {
//					pst = con.prepareStatement("insert into candidate_interview_availability (emp_id, ip_address, _timestamp, _date, _time,recruitment_id) values (?,?,?,?,?,?)");
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setString(2, "");
//					pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
//					pst.setDate(4, uF.getDateFormat(getStrDate()[i], DATE_FORMAT));
//					pst.setTime(5, uF.getTimeFormat(getStrTime()[i], TIME_FORMAT));
//					pst.setInt(6, uF.parseToInt(getRecruitId()));
//					pst.execute();
//				}
//			}
//
//			pst = con.prepareStatement("update candidate_personal_details set session_id =? where emp_per_id=?");
//			pst.setString(1, "");
//			pst.setInt(2, uF.parseToInt(getCandidateId()));
//			pst.execute();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//		}
//		return isValidSesseion;
//	}

	
	public String loadValidateEmployee() {

		try {
			UtilityFunctions uF = new UtilityFunctions();
			orgList = new FillOrganisation(request).fillOrganisation();
			if (uF.parseToInt(getOrg_id())==0) {
				setOrg_id(orgList.get(0).getOrgId());
			}
			salutationList = new FillSalutation(request).fillSalutation();
			
			sourceTypeList = new FillSourceTypeAndName(request).fillSourceType();
			
			noticeDurationList = new FillNoticeDuration().fillNoticeDuration();
			yearsList = new FillYears().fillYears(uF.getCurrentDate(CF.getStrTimeZone()));
			skillsList = new FillSkills(request).fillSkillsWithIdOnOrg(uF.parseToInt(getOrg_id()));

			
			
			wLocationList = new FillWLocation(request).fillWorkLocationName(getOrg_id());
			
			request.setAttribute("yearsList", yearsList);
			request.setAttribute("currentYear", (uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()).toString(), DBDATE, "yyyy")));
			StringBuilder sbSkills = new StringBuilder();

			sbSkills.append(" <table><tr><td>" + "<select name=skillName style=margin-bottom:7px; class=form-control auto>" + "<option value=>Select Skill Name</option>");
			for (int k = 0; k < skillsList.size(); k++) {
				sbSkills.append("<option value=" + ((FillSkills) skillsList.get(k)).getSkillsId() + "> " + ((FillSkills) skillsList.get(k)).getSkillsName() + "</option>");
			}
			sbSkills.append("</select></td><td>" + "<select name=skillValue style=margin-left:10px;margin-bottom:7px;width:100px!important; class=form-control auto>" + "<option value=>Skill Rating</option>");
			for (int k = 1; k < 11; k++) {
				sbSkills.append("<option value=" + k + ">" + k + "</option>");
			}
			sbSkills.append("</select></td>");
			request.setAttribute("sbSkills", sbSkills);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return LOAD;
	}
	

	public void insertEmployee() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			if (uF.parseToInt(getCandidateId()) == 0) {
				insertCandidatePersonalDetails(con, uF, CF);
				uploadImage(uF.parseToInt(getCandidateId()));
			} else {
				uploadImage(uF.parseToInt(getCandidateId()));
			}
			insertSkills(con, uF);
			updateSupportingDocuments(con, uF);
			addCandidateInNewJob(con, uF);
//			insertAvailability(uF, getCandidateId());
			
			/*String candiName = CF.getCandiNameByCandiId(con, getCandidateId());
			if(uF.parseToInt(getRecruitId()) == 0) {
				session.setAttribute(MESSAGE, SUCCESSM+""+candiName+" is added successfully."+END);
			} else {
				session.setAttribute(MESSAGE, SUCCESSM+""+candiName+" is added in "+ getJobcode() +" successfully."+END);
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
		//	db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void uploadImage(int empId2) {

		try {
			UploadImage uI = new UploadImage();
			uI.setServletRequest(request);
			uI.setImageType("CANDIDATE_IMAGE");
			uI.setEmpImage(getEmpImage());
			uI.setEmpImageFileName(getEmpImageFileName());
			uI.setEmpId(empId2 + "");
			uI.setCF(CF);
//			System.out.println("empId2 ===> "+empId2+" getEmpImage() ===> "+getEmpImage());
			uI.upoadImage();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
public void updateSupportingDocuments(Connection con, UtilityFunctions uF) {
		
		try {
//			System.out.println("getStrResume() ===>> " + getStrResume() !=null ? getStrResume()[0] : "");
//			System.out.println("getStrResumeFileName() ===>> " + getStrResumeFileName()[0]);
//			if(uF.parseToInt(getDocId0())!=0) {
//				if(getIdDoc0() != null) {
//					updateDocuments(con, uF, getDocId0(), getIdDoc0(), getIdDocName0(), getIdDocType0(), getIdDocStatus0(), getIdDoc0FileName());
//				}
//			} else {
				insertDocuments(con, uF, getIdDoc0(), getIdDocName0(), getIdDocType0(), getIdDocStatus0(), getIdDoc0FileName());
//				insertDocuments(con, uF, getStrResume()[0], getIdDocName0(), getIdDocType0(), getIdDocStatus0(), getStrResumeFileName()[0]);
//			}
//			System.out.println("getDocId1() ===>> " + getDocId1());
//			if(uF.parseToInt(getDocId1())!=0) {
//				if(getIdDoc1() != null) {
//					updateDocuments(con, uF, getDocId1(), getIdDoc1(), getIdDocName1(), getIdDocType1(), getIdDocStatus1(), getIdDoc1FileName());
//				}
//			} else {
//				insertDocuments(con, uF, getIdDoc1(), getIdDocName1(), getIdDocType1(), getIdDocStatus1(), getIdDoc1FileName());
//			}
////			System.out.println("getDocId2() ===>> " + getDocId2());
//			if(uF.parseToInt(getDocId2())!=0) {
//				if(getIdDoc2() != null) {
//					updateDocuments(con, uF, getDocId2(), getIdDoc2(), getIdDocName2(), getIdDocType2(), getIdDocStatus2(), getIdDoc2FileName());
//				}
//			} else {
//				insertDocuments(con, uF, getIdDoc2(), getIdDocName2(), getIdDocType2(), getIdDocStatus2(), getIdDoc2FileName());
//			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	public void updateDocuments(Connection con, UtilityFunctions uF, String docId, File idDoc, String idDocName, String idDocType, String idDocStatus, String idDocFileName) {
		PreparedStatement pst = null;
		try {
			String strFileName = null;
//			System.out.println("idDoc ===>> " + idDoc);
			if(idDoc != null) {
				if (CF.getStrDocSaveLocation() == null) {
					strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, idDoc, idDocFileName, idDocFileName, CF);
				} else {
					strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_CANDIDATE + "/" + I_DOCUMENT + "/" + I_ATTACHMENT + "/"+ getCandidateId(), idDoc, idDocFileName, idDocFileName, CF);
				}
			}
			pst = con.prepareStatement("UPDATE candidate_documents_details SET documents_file_name=?,added_by=?,entry_date=? where documents_id = ?");
//			pst.setString(1, idDocName);
			pst.setString(1, strFileName);
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt(docId));
//			System.out.println("pst ===>> " + pst);
			int x = pst.executeUpdate();
			pst.close();
			
			if(idDoc != null) {
				if (x > 0) {
					/**
					 * Log Details
					 * */
					String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
					String strCandiName = CF.getCandiNameByCandiId(con, getCandidateId());
					String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted document (" + uF.showData(idDocName, "")
						+ ") of " + uF.showData(strCandiName, "") + " on " + ""
						+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
						+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
					LogDetails logDetails = new LogDetails();
					logDetails.session = session;
					logDetails.CF = CF;
					logDetails.request = request;
					logDetails.setProcessId(uF.parseToInt(getCandidateId()));
					logDetails.setProcessType(L_CANDIDATE);
					logDetails.setProcessActivity(L_UPDATE);
					logDetails.setProcessMsg(strProcessMsg);
					logDetails.setProcessStep(1);
					logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
					logDetails.insertLog(con, uF);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void insertDocuments(Connection con, UtilityFunctions uF, File idDoc, String idDocName, String idDocType, String idDocStatus, String idDocFileName) {
		PreparedStatement pst = null;
		try {
			String strFileName = null;
//			System.out.println("idDoc ===>> " + idDoc);
			if(idDoc != null) {
				if (CF.getStrDocSaveLocation() == null) {
					strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, idDoc, idDocFileName, idDocFileName, CF);
				} else {
					strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_CANDIDATE + "/" + I_DOCUMENT + "/" + I_ATTACHMENT + "/"+ getCandidateId(), idDoc, idDocFileName, idDocFileName, CF);
				}
			
				pst = con.prepareStatement("INSERT INTO candidate_documents_details(documents_name, documents_type, emp_id, documents_file_name,added_by,entry_date) values (?,?,?,?,?,?)");
				pst.setString(1, idDocName);
				pst.setString(2, idDocType);
				pst.setInt(3, uF.parseToInt(getCandidateId()));
				pst.setString(4, strFileName);
				pst.setInt(5, uF.parseToInt(strSessionEmpId));
				pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
				int x = pst.executeUpdate();
				pst.close();

				if (x > 0) {
					/**
					 * Log Details
					 * */
					String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
					String strCandiName = CF.getCandiNameByCandiId(con, getCandidateId());
					String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted document (" + uF.showData(idDocName, "")
						+ ") of " + uF.showData(strCandiName, "") + " on " + ""
						+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
						+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, TIME_FORMAT_AM_PM);
					LogDetails logDetails = new LogDetails();
					logDetails.session = session;
					logDetails.CF = CF;
					logDetails.request = request;
					logDetails.setProcessId(uF.parseToInt(getCandidateId()));
					logDetails.setProcessType(L_CANDIDATE);
					logDetails.setProcessActivity(L_ADD);
					logDetails.setProcessMsg(strProcessMsg);
					logDetails.setProcessStep(1);
					logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
					logDetails.insertLog(con, uF);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	/*public void insertDocuments(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		try {
			if(getIdDoc() != null && getIdDoc().size() != 0 ) {
				for (int i=0; i<getIdDoc().size(); i++) {
					if(getIdDoc().get(i)!=null & getIdDoc().get(i).length()!= 0) {
						String strFileName = null;
						if(CF.getStrDocSaveLocation()==null) {
							strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getIdDoc().get(i), getIdDocFileName().get(i), getIdDocFileName().get(i), CF);
						} else {
							strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_CANDIDATE+"/"+I_DOCUMENT+"/"+I_ATTACHMENT+"/"+getCandidateId(), getIdDoc().get(i), getIdDocFileName().get(i), getIdDocFileName().get(i), CF);
						}
			            pst = con.prepareStatement("INSERT INTO candidate_documents_details (documents_name, documents_type, emp_id, documents_file_name," +
			            		"added_by,entry_date) values (?,?,?,?,?,?)");
						pst.setString(1, getIdDocName()[i]);
			            pst.setString(2, getIdDocType()[i]);
			            pst.setInt(3, uF.parseToInt(getCandidateId()));
			            pst.setString(4, strFileName);
			            pst.setInt(5, uF.parseToInt(strSessionEmpId));
			            pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
//			            log.debug("pst insertDocuments==>"+pst);
			            pst.execute();
						pst.close();
					}
				}
			}
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
	}*/


	public void insertSkills(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		try {
			for (int i = 0; i < getSkillName().length; i++) {
				if (getSkillName()[i] != null && getSkillName()[i].length() != 0 && !getSkillName()[i].trim().equals("") && !getSkillName()[i].equalsIgnoreCase("null")) {
					pst = con.prepareStatement("INSERT INTO candidate_skills_description (skill_id, skills_value, emp_id) VALUES (?,?,?)");
					pst.setInt(1, uF.parseToInt(getSkillName()[i]));
					pst.setString(2, getSkillValue()[i]);
					pst.setInt(3, uF.parseToInt(getCandidateId()));
					pst.execute();
					pst.close();
				}
			}
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


	public int insertCandidatePersonalDetails(Connection con, UtilityFunctions uF, CommonFunctions CF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		int empPerId = 0;
		try {
			String orgCurrId = CF.getOrgCurrencyIdByOrg(con, getOrg_id());
			pst = con.prepareStatement("INSERT INTO candidate_personal_details (salutation, emp_fname, emp_mname, emp_lname, emp_email, "
				+ "emp_contactno_mob, current_ctc, expected_ctc, notice_period, added_by, emp_entry_date, empcode, emp_address1, org_id," +
				"ctc_curr_id,availability_for_interview,total_experience, applied_location) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
			pst.setString(1, getSalutation());
			pst.setString(2, getEmpFname());
			pst.setString(3, getEmpMname());
			pst.setString(4, getEmpLname());
			pst.setString(5, getEmpEmail());
			pst.setString(6, getEmpMobileNo());
			pst.setDouble(7, uF.parseToDouble(getCandiCurrCTC()));
			pst.setDouble(8, uF.parseToDouble(getCandiExpectedCTC()));
			pst.setInt(9, uF.parseToInt(getCandiNoticePeriod()));
			pst.setInt(10, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(11, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.setString(12, "");
			pst.setString(13, "");
			pst.setInt(14, uF.parseToInt(getOrg_id()));
			pst.setInt(15, uF.parseToInt(orgCurrId));
//			pst.setBoolean(16, uF.parseToBoolean(getAvailability()));
			pst.setBoolean(16, true);
			pst.setDouble(17, uF.parseToDouble(getCandiTotalExperience()));
			pst.setString(18, uF.getAppendData(getStrWLocation()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("SELECT max(emp_per_id) as emp_per_id from candidate_personal_details");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("pst candi max id ===> "+pst);
			while (rs.next()) {
				setCandidateId(rs.getString("emp_per_id"));
				empPerId = uF.parseToInt(rs.getString("emp_per_id"));
			}
			rs.close();
			pst.close();
//			System.out.println("empPerId ===> "+empPerId);
			
			/*if(uF.parseToInt(getRecruitId()) > 0) {
				pst = con.prepareStatement("INSERT INTO candidate_application_details (candidate_id,recruitment_id,job_code,application_date," +
						"added_by,entry_date) VALUES (?,?,?,?, ?,?)");
				pst.setInt(1, empPerId);
				pst.setInt(2, uF.parseToInt(getRecruitId()));
				pst.setString(3, getJobcode());
				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(5, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
				pst.execute();
			}*/
		} catch (Exception e) {
			e.printStackTrace();

		}finally {
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
		return empPerId;
	}

	public String getEmpFname() {
		return empFname;
	}

	public void setEmpFname(String empFname) {
		this.empFname = empFname;
	}

	public String getEmpLname() {
		return empLname;
	}

	public void setEmpLname(String empLname) {
		this.empLname = empLname;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getEmpEmail() {
		return empEmail;
	}

	public void setEmpEmail(String empEmail) {
		this.empEmail = empEmail;
	}

	public void setEmpImage(File empImage) {
		this.empImage = empImage;
	}

	public File getEmpImage() {
		return empImage;
	}

	public String getEmpImageFileName() {
		return empImageFileName;
	}

	public void setEmpImageFileName(String empImageFileName) {
		this.empImageFileName = empImageFileName;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getEmpEmailSec() {
		return empEmailSec;
	}

	public void setEmpEmailSec(String empEmailSec) {
		this.empEmailSec = empEmailSec;
	}

	public String getEmpMobileNo() {
		return empMobileNo;
	}

	public void setEmpMobileNo(String empMobileNo) {
		this.empMobileNo = empMobileNo;
	}

	public String[] getDocumentNames() {
		return documentNames;
	}

	public void setDocumentNames(String[] documentNames) {
		this.documentNames = documentNames;
	}

	public String[] getDocumentValues() {
		return documentValues;
	}

	public void setDocumentValues(String[] documentValues) {
		this.documentValues = documentValues;
	}

	public String[] getSkillName() {
		return skillName;
	}

	public void setSkillName(String[] skillName) {
		this.skillName = skillName;
	}

	public String[] getSkillValue() {
		return skillValue;
	}

	public void setSkillValue(String[] skillValue) {
		this.skillValue = skillValue;
	}

	public List<FillYears> getYearsList() {
		return yearsList;
	}

	public void setYearsList(List<FillYears> yearsList) {
		this.yearsList = yearsList;
	}

	public List<FillSkills> getSkillsList() {
		return skillsList;
	}

	public void setSkillsList(List<FillSkills> skillsList) {
		this.skillsList = skillsList;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public boolean isApprovedFlag() {
		return approvedFlag;
	}

	public void setApprovedFlag(boolean approvedFlag) {
		this.approvedFlag = approvedFlag;
	}

	public Timestamp getEmpFilledFlagDate() {
		return empFilledFlagDate;
	}

	public void setEmpFilledFlagDate(Timestamp empFilledFlagDate) {
		this.empFilledFlagDate = empFilledFlagDate;
	}

	public List<FillNoticeDuration> getNoticeDurationList() {
		return noticeDurationList;
	}

	public void setNoticeDurationList(List<FillNoticeDuration> noticeDurationList) {
		this.noticeDurationList = noticeDurationList;
	}

	public int getNoticeDuration() {
		return noticeDuration;
	}

	public void setNoticeDuration(int noticeDuration) {
		this.noticeDuration = noticeDuration;
	}

	public String[] getStrTime() {
		return strTime;
	}

	public void setStrTime(String[] strTime) {
		this.strTime = strTime;
	}

	public String[] getStrDate() {
		return strDate;
	}

	public void setStrDate(String[] strDate) {
		this.strDate = strDate;
	}

	// for record particular job id
	

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}
	

	public String[] getJobcode() {
		return jobcode;
	}

	public void setJobcode(String[] jobcode) {
		this.jobcode = jobcode;
	}

	public String getDegreeNameOther() {
		return degreeNameOther;
	}

	public void setDegreeNameOther(String degreeNameOther) {
		this.degreeNameOther = degreeNameOther;
	}

	public String getOrg_id() {
		return org_id;
	}

	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}

	public String getCandidateId() {
		return CandidateId;
	}

	public void setCandidateId(String candidateId) {
		CandidateId = candidateId;
	}

	public String getStepSubmit() {
		return stepSubmit;
	}

	public void setStepSubmit(String stepSubmit) {
		this.stepSubmit = stepSubmit;
	}

	public String getCandibymail() {
		return candibymail;
	}

	public void setCandibymail(String candibymail) {
		this.candibymail = candibymail;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getEmpMname() {
		return empMname;
	}

	public void setEmpMname(String empMname) {
		this.empMname = empMname;
	}

	public List<FillSalutation> getSalutationList() {
		return salutationList;
	}

	public void setSalutationList(List<FillSalutation> salutationList) {
		this.salutationList = salutationList;
	}

	public String getCandiCurrCTC() {
		return candiCurrCTC;
	}

	public void setCandiCurrCTC(String candiCurrCTC) {
		this.candiCurrCTC = candiCurrCTC;
	}

	public String getCandiExpectedCTC() {
		return candiExpectedCTC;
	}

	public void setCandiExpectedCTC(String candiExpectedCTC) {
		this.candiExpectedCTC = candiExpectedCTC;
	}

	public String getCandiNoticePeriod() {
		return candiNoticePeriod;
	}

	public void setCandiNoticePeriod(String candiNoticePeriod) {
		this.candiNoticePeriod = candiNoticePeriod;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getAvailability() {
		return availability;
	}

	public void setAvailability(String availability) {
		this.availability = availability;
	}

	public String getApplyType() {
		return applyType;
	}

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}

	public String getIsEmpCode() {
		return isEmpCode;
	}

	public void setIsEmpCode(String isEmpCode) {
		this.isEmpCode = isEmpCode;
	}

	public String getRefEmpId() {
		return refEmpId;
	}

	public void setRefEmpId(String refEmpId) {
		this.refEmpId = refEmpId;
	}

	public String getDocId0() {
		return docId0;
	}

	public void setDocId0(String docId0) {
		this.docId0 = docId0;
	}

	public File getIdDoc0() {
		return idDoc0;
	}

	public void setIdDoc0(File idDoc0) {
		this.idDoc0 = idDoc0;
	}

	public String getIdDoc0FileName() {
		return idDoc0FileName;
	}

	public void setIdDoc0FileName(String idDoc0FileName) {
		this.idDoc0FileName = idDoc0FileName;
	}

	public String getIdDocStatus0() {
		return idDocStatus0;
	}

	public void setIdDocStatus0(String idDocStatus0) {
		this.idDocStatus0 = idDocStatus0;
	}

	public String getIdDocName0() {
		return idDocName0;
	}

	public void setIdDocName0(String idDocName0) {
		this.idDocName0 = idDocName0;
	}

	public String getIdDocType0() {
		return idDocType0;
	}

	public void setIdDocType0(String idDocType0) {
		this.idDocType0 = idDocType0;
	}

	public String getDocId1() {
		return docId1;
	}

	public void setDocId1(String docId1) {
		this.docId1 = docId1;
	}

	public File getIdDoc1() {
		return idDoc1;
	}

	public void setIdDoc1(File idDoc1) {
		this.idDoc1 = idDoc1;
	}

	public String getIdDoc1FileName() {
		return idDoc1FileName;
	}

	public void setIdDoc1FileName(String idDoc1FileName) {
		this.idDoc1FileName = idDoc1FileName;
	}

	public String getIdDocStatus1() {
		return idDocStatus1;
	}

	public void setIdDocStatus1(String idDocStatus1) {
		this.idDocStatus1 = idDocStatus1;
	}

	public String getIdDocName1() {
		return idDocName1;
	}

	public void setIdDocName1(String idDocName1) {
		this.idDocName1 = idDocName1;
	}

	public String getIdDocType1() {
		return idDocType1;
	}

	public void setIdDocType1(String idDocType1) {
		this.idDocType1 = idDocType1;
	}

	public String getDocId2() {
		return docId2;
	}

	public void setDocId2(String docId2) {
		this.docId2 = docId2;
	}

	public File getIdDoc2() {
		return idDoc2;
	}

	public void setIdDoc2(File idDoc2) {
		this.idDoc2 = idDoc2;
	}

	public String getIdDoc2FileName() {
		return idDoc2FileName;
	}

	public void setIdDoc2FileName(String idDoc2FileName) {
		this.idDoc2FileName = idDoc2FileName;
	}

	public String getIdDocStatus2() {
		return idDocStatus2;
	}

	public void setIdDocStatus2(String idDocStatus2) {
		this.idDocStatus2 = idDocStatus2;
	}

	public String getIdDocName2() {
		return idDocName2;
	}

	public void setIdDocName2(String idDocName2) {
		this.idDocName2 = idDocName2;
	}

	public String getIdDocType2() {
		return idDocType2;
	}

	public void setIdDocType2(String idDocType2) {
		this.idDocType2 = idDocType2;
	}

	public String getRefEmpCode() {
		return refEmpCode;
	}

	public void setRefEmpCode(String refEmpCode) {
		this.refEmpCode = refEmpCode;
	}

	public File[] getStrResume() {
		return strResume;
	}

	public void setStrResume(File[] strResume) {
		this.strResume = strResume;
	}

	public String[] getStrResumeFileName() {
		return strResumeFileName;
	}

	public void setStrResumeFileName(String[] strResumeFileName) {
		this.strResumeFileName = strResumeFileName;
	}

	public List<FillSourceTypeAndName> getSourceTypeList() {
		return sourceTypeList;
	}

	public void setSourceTypeList(List<FillSourceTypeAndName> sourceTypeList) {
		this.sourceTypeList = sourceTypeList;
	}

	public String getAppliSourceType() {
		return appliSourceType;
	}

	public void setAppliSourceType(String appliSourceType) {
		this.appliSourceType = appliSourceType;
	}

	public String getCandiTotalExperience() {
		return candiTotalExperience;
	}

	public void setCandiTotalExperience(String candiTotalExperience) {
		this.candiTotalExperience = candiTotalExperience;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public String[] getStrWLocation() {
		return strWLocation;
	}

	public void setStrWLocation(String[] strWLocation) {
		this.strWLocation = strWLocation;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

}