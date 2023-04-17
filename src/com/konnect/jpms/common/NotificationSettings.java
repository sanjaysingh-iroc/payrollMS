package com.konnect.jpms.common;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class NotificationSettings extends ActionSupport implements ServletRequestAware, IStatements {

 	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	String strProductType;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	private String strNotificationCode;
	
	
//	----------------- Project Management --------------------	
	
//	----------------- Customer -------------------------
	private boolean isEmailNewCustAdded;
	private boolean isTextNewCustAdded;
	private String strTextNewCustAdded;
	private String strSubjectNewCustAdded;
	private String strBodyNewCustAdded;
	
	private boolean isEmailNewCustContactAdded;
	private boolean isTextNewCustContactAdded;
	private String strTextNewCustContactAdded;
	private String strSubjectNewCustContactAdded;
	private String strBodyNewCustContactAdded;
	
//	---------------Project Task -------------------------
	private boolean isEmailNewProWelcomeToCust;
	private boolean isTextNewProWelcomeToCust;
	private String strTextNewProWelcomeToCust;
	private String strSubjectNewProWelcomeToCust;
	private String strBodyNewProWelcomeToCust;
	
	private boolean isEmailProUpdated;
	private boolean isTextProUpdated;
	private String strTextProUpdated;
	private String strSubjectProUpdated;
	private String strBodyProUpdated;
	
	private boolean isEmailNewTask;
	private boolean isTextNewTask;
	private String strTextNewTask;
	private String strSubjectNewTask;
	private String strBodyNewTask;
	
	private boolean isEmailNewDocShared;
	private boolean isTextNewDocShared;
	private String strTextNewDocShared;
	private String strSubjectNewDocShared;
	private String strBodyNewDocShared;
	
	private boolean isEmailTaskCompleted;
	private boolean isTextTaskCompleted;
	private String strTextTaskCompleted;
	private String strSubjectTaskCompleted;
	private String strBodyTaskCompleted;
	
	private boolean isEmailProCompleted;
	private boolean isTextProCompleted;
	private String strTextProCompleted;
	private String strSubjectProCompleted;
	private String strBodyProCompleted;
	
	private boolean isEmailProBlocked;
	private boolean isTextProBlocked;
	private String strTextProBlocked;
	private String strSubjectProBlocked;
	private String strBodyProBlocked;
	
	private boolean isEmailMilestoneCompleted;
	private boolean isTextMilestoneCompleted;
	private String strTextMilestoneCompleted;
	private String strSubjectMilestoneCompleted;
	private String strBodyMilestoneCompleted;
	
	private boolean isEmailProReopened;
	private boolean isTextProReopened;
	private String strTextProReopened;
	private String strSubjectProReopened;
	private String strBodyProReopened;
	
//	---------------------- Timesheet -------------------------
	
	private boolean isEmailTimesheetSubmitted;
	private boolean isTextTimesheetSubmitted;
	private String strTextTimesheetSubmitted;
	private String strSubjectTimesheetSubmitted;
	private String strBodyTimesheetSubmitted;
	
	private boolean isEmailTimesheetReopened;
	boolean isTextTimesheetReopened;
	private String strTextTimesheetReopened;
	private String strSubjectTimesheetReopened;
	private String strBodyTimesheetReopened;
	
	private boolean isEmailTimesheetApproved;
	private boolean isTextTimesheetApproved;
	private String strTextTimesheetApproved;
	private String strSubjectTimesheetApproved;
	private String strBodyTimesheetApproved;
	
	private boolean isEmailTimesheetSubmitToCust;
	boolean isTextTimesheetSubmitToCust;
	private String strTextTimesheetSubmitToCust;
	private String strSubjectTimesheetSubmitToCust;
	private String strBodyTimesheetSubmitToCust;
	
//	---------------------------- Billing --------------------------------
	
	private boolean isEmailMilestoneBilling;
	private boolean isTextMilestoneBilling;
	private String strTextMilestoneBilling;
	private String strSubjectMilestoneBilling;
	private String strBodyMilestoneBilling;
	
	private boolean isEmailRecurringBilling;
	private boolean isTextRecurringBilling;
	private String strTextRecurringBilling;
	private String strSubjectRecurringBilling;
	private String strBodyRecurringBilling;
	
	
//	---------------------------- Auto-Mail on Event --------------------------------
	
	private boolean isEmailBirthday;
	private boolean isTextBirthday;
	private String strTextBirthday;
	private String strSubjectBirthday;
	private String strBodyBirthday;
	private File strBackgroundImage;
	//private String strBackgroundImage;
	private String strBackgroundImageFileName;
	private String strBackgroundImageStatus;
	//private String idDocName0;
	private String strBackgroundBirthdayImageStatus;
	private File strBackgroundBirthdayImage;
	private String strBackgroundBirthdayImageFileName;

	
	
	private boolean isEmailWorkAnniversary;
	private boolean isTextWorkAnniversary;
	private String strTextWorkAnniversary;
	private String strSubjectWorkAnniversary;
	private String strBodyWorkAnniversary;
	private String strBackgroundWorkImageStatus;
	private File strBackgroundWorkImage;
	private String strBackgroundWorkImageFileName;

	
	private boolean isEmailMarriageAnniversary;
	private boolean isTextMarriageAnniversary;
	private String strTextMarriageAnniversary;
	private String strSubjectMarriageAnniversary;
	private String strBodyMarriageAnniversary;
	private String strBackgroundMarriageImageStatus;
	private File strBackgroundMarriageImage;
	private String strBackgroundMarriageImageFileName;
	
	

	private boolean isEmailOnEventCreation;
	private boolean isTextOnEventCreation;
	private String strTextOnEventCreation;
	private String strSubjectOnEventCreation;
	private String strBodyOnEventCreation;
	
	
	private boolean isEmailOnAnnouncementCreation;
	private boolean isTextOnAnnouncementCreation;
	private String strTextOnAnnouncementCreation;
	private String strSubjectOnAnnouncementCreation;
	private String strBodyOnAnnouncementCreation;
	
//===start parvez date: 15-02-2023===	
	private boolean isEmailOnCircularCreation;
	private boolean isTextOnCircularCreation;
	private String strTextOnCircularCreation;
	private String strSubjectOnCircularCreation;
	private String strBodyOnCircularCreation;
//===end parvez date: 15-02-2023===
	
	
	
	
//	---------------------- Payment -------------------------------
	
	private boolean isEmailPaymentAlert;
	private boolean isTextPaymentAlert;
	private String strTextPaymentAlert;
	private String strSubjectPaymentAlert;
	private String strBodyPaymentAlert;
	
	private boolean isEmailPaymentReminder;
	private boolean isTextPaymentReminder;
	private String strTextPaymentReminder;
	private String strSubjectPaymentReminder;
	private String strBodyPaymentReminder;
	
//	----------------- Recruitment --------------------	
	
	private boolean isEmailCandiAssessmentForm;
	private boolean isTextCandiAssessmentForm;
	private String strTextCandiAssessmentForm;
	private String strSubjectCandiAssessmentForm;
	private String strBodyCandiAssessmentForm;
	
	private boolean isEmailCandiFinalize;
	private boolean isTextCandiFinalize;
	private String strTextCandiFinalize;
	private String strSubjectCandiFinalize;
	private String strBodyCandiFinalize;
	
	private boolean isEmailCandiShortlistFromConsultant;
	private boolean isTextCandiShortlistFromConsultant;
	private String strTextCandiShortlistFromConsultant;
	private String strSubjectCandiShortlistFromConsultant;
	private String strBodyCandiShortlistFromConsultant;
	
	private boolean isEmailNewJobToConsultant;
	private boolean isTextNewJobToConsultant;
	private String strTextNewJobToConsultant;
	private String strSubjectNewJobToConsultant;
	private String strBodyNewJobToConsultant;
	
	private boolean isEmailJobProfileUpdate;
	private boolean isTextJobProfileUpdate;
	private String strTextJobProfileUpdate;
	private String strSubjectJobProfileUpdate;
	private String strBodyJobProfileUpdate;
	
	private boolean isEmailJobProfileDeny;
	private boolean isTextJobProfileDeny;
	private String strTextJobProfileDeny;
	private String strSubjectJobProfileDeny;
	private String strBodyJobProfileDeny;
	
	private boolean isEmailJobProfileApproval;
	private boolean isTextJobProfileApproval;
	private String strTextJobProfileApproval;
	private String strSubjectJobProfileApproval;
	private String strBodyJobProfileApproval;
	
	private boolean isEmailRecruitmentRequestDeny;
	private boolean isTextRecruitmentRequestDeny;
	private String strTextRecruitmentRequestDeny;
	private String strSubjectRecruitmentRequestDeny;
	private String strBodyRecruitmentRequestDeny;
	
	private boolean isEmailNewEmpOnboardingToHr;
	private boolean isTextNewEmpOnboardingToHr;
	private String strTextNewEmpOnboardingToHr;
	private String strSubjectNewEmpOnboardingToHr;
	private String strBodyNewEmpOnboardingToHr;
	
	private boolean isEmailCandiOfferAcceptReject;
	private boolean isTextCandiOfferAcceptReject;
	private String strTextCandiOfferAcceptReject;
	private String strSubjectCandiOfferAcceptReject;
	private String strBodyCandiOfferAcceptReject;
	
	private boolean isEmailRecruitmentRequestUpdate;
	private boolean isTextRecruitmentRequestUpdate;
	private String strTextRecruitmentRequestUpdate;
	private String strSubjectRecruitmentRequestUpdate;
	private String strBodyRecruitmentRequestUpdate;
	
	private boolean isEmailFillCandiInfoByCandi;
	private boolean isTextFillCandiInfoByCandi;
	private String strTextFillCandiInfoByCandi;
	private String strSubjectFillCandiInfoByCandi;
	private String strBodyFillCandiInfoByCandi;
	
	private boolean isEmailCandiBackgroudVerification;
	private boolean isTextCandiBackgroudVerification;
	private String strTextCandiBackgroudVerification;
	private String strSubjectCandiBackgroudVerification;
	private String strBodyCandiBackgroudVerification;
	
	
	private boolean isEmailCandiEmpLoginDetail;
	private boolean isTextCandiEmpLoginDetail;
	private String strTextCandiEmpLoginDetail;
	private String strSubjectCandiEmpLoginDetail;
	private String strBodyCandiEmpLoginDetail;
	
	private boolean isEmailCandiOnboarding;
	private boolean isTextCandiOnboarding;
	private String strTextCandiOnboarding;
	private String strSubjectCandiOnboarding;
	private String strBodyCandiOnboarding;
	
	private boolean isEmailPanelistInterviewDate;
	private boolean isTextPanelistInterviewDate;
	private String strTextPanelistInterviewDate;
	private String strSubjectPanelistInterviewDate;
	private String strBodyPanelistInterviewDate;
	
	private boolean isEmailCandiInterviewDate;
	private boolean isTextCandiInterviewDate;
	private String strTextCandiInterviewDate;
	private String strSubjectCandiInterviewDate;
	private String strBodyCandiInterviewDate;
	
	private boolean isEmailCandiJoiningOfferCTC;
	private boolean isTextCandiJoiningOfferCTC;
	private String strTextCandiJoiningOfferCTC;
	private String strSubjectCandiJoiningOfferCTC;
	private String strBodyCandiJoiningOfferCTC;
	
	private boolean isEmailCandiSpecifyOtherDate;
	private boolean isTextCandiSpecifyOtherDate;
	private String strTextCandiSpecifyOtherDate;
	private String strSubjectCandiSpecifyOtherDate;
	private String strBodyCandiSpecifyOtherDate;
	
	private boolean isEmailApplicationShortlist;
	private boolean isTextApplicationShortlist;
	private String strTextApplicationShortlist;
	private String strSubjectApplicationShortlist;
	private String strBodyApplicationShortlist;
	
	private boolean isEmailRecruitmentApproval;
	private boolean isTextRecruitmentApproval;
	private String strTextRecruitmentApproval;
	private String strSubjectRecruitmentApproval;
	private String strBodyRecruitmentApproval;
	
	private boolean isEmailNewRecruitmentToEmp;
	private boolean isTextNewRecruitmentToEmp;
	private String strTextNewRecruitmentToEmp;
	private String strSubjectNewRecruitmentToEmp;
	private String strBodyNewRecruitmentToEmp;
	
	private boolean isEmailRecruitmentRequest;
	private boolean isTextRecruitmentRequest;
	private String strTextRecruitmentRequest;
	private String strSubjectRecruitmentRequest;
	private String strBodyRecruitmentRequest;
	
	private boolean isEmailResumeSubmission;
	private boolean isTextResumeSubmission;;
	private String strTextResumeSubmission;;
	private String strSubjectResumeSubmission;
	private String strBodyResumeSubmission;
	
//===start parvez date: 29-10-2021===
	private boolean isEmailApplicationSubmissionToHT;
	private boolean isTextApplicationSubmissionToHT;
	private String strTextApplicationSubmissionToHT;
	private String strSubjectApplicationSubmissionToHT;
	private String strBodyApplicationSubmissionToHT;
//===end parvez date: 29-10-2021===
	
//===start parvez date: 26-08-2022===
	private boolean isEmailEmpOnboardedBySelf;
	private boolean isTextEmpOnboardedBySelf;
	private String strTextEmpOnboardedBySelf;
	private String strSubjectEmpOnboardedBySelf;
	private String strBodyEmpOnboardedBySelf;
//===end parvez date: 26-08-2022===
	
	private boolean isEmailSelectedRound;
	private boolean isTextSelectedRound;
	private String strTextSelectedRound;
	private String strSubjectSelectedRound;
	private String strBodySelectedRound;
	
	
	private boolean isEmailRejectedRound;
	private boolean isTextRejectedRound;
	private String strTextRejectedRound;
	private String strSubjectRejectedRound;
	private String strBodyRejectedRound;
	
//	----------------- Learning --------------------
	
	private boolean isEmailLTrainingFeedbackLearner;
	private boolean isTextLTrainingFeedbackLearner;
	private String strTextLTrainingFeedbackLearner;
	private String strSubjectLTrainingFeedbackLearner;
	private String strBodyLTrainingFeedbackLearner;
	
	private boolean isEmailLTrainingFeedbackTrainer;
	private boolean isTextLTrainingFeedbackTrainer;
	private String strTextLTrainingFeedbackTrainer;
	private String strSubjectLTrainingFeedbackTrainer;
	private String strBodyLTrainingFeedbackTrainer;
	
	private boolean isEmailLAssessFinalizeForHr;
	private boolean isTextLAssessFinalizeForHr;
	private String strTextLAssessFinalizeForHr;
	private String strSubjectLAssessFinalizeForHr;
	private String strBodyLAssessFinalizeForHr;
	
	private boolean isEmailLTrainingFinalizeForHr;
	private boolean isTextLTrainingFinalizeForHr;
	private String strTextLTrainingFinalizeForHr;
	private String strSubjectLTrainingFinalizeForHr;
	private String strBodyLTrainingFinalizeForHr;
	
	private boolean isEmailLearningPlanForHr;
	private boolean isTextLearningPlanForHr;
	private String strTextLearningPlanForHr;
	private String strSubjectLearningPlanForHr;
	private String strBodyLearningPlanForHr;
	
	private boolean isEmailLearningPlanForLearner;
	private boolean isTextLearningPlanForLearner;
	private String strTextLearningPlanForLearner;
	private String strSubjectLearningPlanForLearner;
	private String strBodyLearningPlanForLearner;
	
	
//	----------------- Performance --------------------
	
	private boolean isEmailExecutiveTarget;
	private boolean isTextExecutiveTarget;
	private String strTextExecutiveTarget;
	private String strSubjectExecutiveTarget;
	private String strBodyExecutiveTarget;
	
	private boolean isEmailExecutiveKRA;
	private boolean isTextExecutiveKRA;
	private String strTextExecutiveKRA;
	private String strSubjectExecutiveKRA;
	private String strBodyExecutiveKRA;
	
	private boolean isEmailExecutiveGoal;
	private boolean isTextExecutiveGoal;
	private String strTextExecutiveGoal;
	private String strSubjectExecutiveGoal;
	private String strBodyExecutiveGoal;
	
	private boolean isEmailManagerGoal;
	private boolean isTextManagerGoal;
	private String strTextManagerGoal;
	private String strSubjectManagerGoal;
	private String strBodyManagerGoal;
	
	private boolean isEmailLearningGapForHr;
	private boolean isTextLearningGapForHr;
	private String strTextLearningGapForHr;
	private String strSubjectLearningGapForHr;
	private String strBodyLearningGapForHr;
	
	private boolean isEmailReviewFinalizationForEmp;
	private boolean isTextReviewFinalizationForEmp;
	private String strTextReviewFinalizationForEmp;
	private String strSubjectReviewFinalizationForEmp;
	private String strBodyReviewFinalizationForEmp;
	
	private boolean isEmailReviewFinalizationForHr;
	private boolean isTextReviewFinalizationForHr;
	private String strTextReviewFinalizationForHr;
	private String strSubjectReviewFinalizationForHr;
	private String strBodyReviewFinalizationForHr;
	
	private boolean isEmailEmpReviewSubmit;
	private boolean isTextEmpReviewSubmit;
	private String strTextEmpReviewSubmit;
	private String strSubjectEmpReviewSubmit;
	private String strBodyEmpReviewSubmit;
	
	private boolean isEmailNewReviewPublish;
	private boolean isTextNewReviewPublish;
	private String strTextNewReviewPublish;
	private String strSubjectNewReviewPublish;
	private String strBodyNewReviewPublish;
	
	private boolean isEmailPendingReviewReminder;
	private boolean isTextPendingReviewReminder;
	private String strTextPendingReviewReminder;
	private String strSubjectPendingReviewReminder;
	private String strBodyPendingReviewReminder;
	
	
//	----------------- Earth --------------------
	
	private boolean isEmailNewEmployee;
	private boolean isTextNewEmployee;
	private String strTextNewEmployee;
	private String strSubjectNewEmployee;
	private String strBodyNewEmployee;
	
	private String strBackgroundNewEmpImageStatus;
	private File strBackgroundNewEmpImage;
	private String strBackgroundNewEmpImageFileName;
	
	
	private boolean isEmailNewEmployeeJoining;
	private boolean isTextNewEmployeeJoining;
	private String strTextNewEmployeeJoining;
	private String strSubjectNewEmployeeJoining;
	private String strBodyNewEmployeeJoining;
	
	private boolean isEmailNewEmployeeStatusChange;
	private boolean isTextNewEmployeeStatusChange;
	private String strTextNewEmployeeStatusChange;
	private String strSubjectNewEmployeeStatusChange;
	private String strBodyNewEmployeeStatusChange;
	
	private boolean isEmailNewEmployeeLeaveRequest;
	private boolean isTextNewEmployeeLeaveRequest;
	private String strTextNewEmployeeLeaveRequest;
	private String strSubjectNewEmployeeLeaveRequest;
	private String strBodyNewEmployeeLeaveRequest;
	
	private boolean isEmailManagerNewLeaveRequest;
	private boolean isTextManagerNewLeaveRequest;
	private String strTextManagerNewLeaveRequest;
	private String strSubjectManagerNewLeaveRequest;
	private String strBodyManagerNewLeaveRequest;
	
	private boolean isEmailManagerNewExtraWorkRequest;
	private boolean isTextManagerNewExtraWorkRequest;
	private String strTextManagerNewExtraWorkRequest;
	private String strSubjectManagerNewExtraWorkRequest;
	private String strBodyManagerNewExtraWorkRequest;
	
	private boolean isEmailManagerNewTravelRequest;
	private boolean isTextManagerNewTravelRequest;
	private String strTextManagerNewTravelRequest;
	private String strSubjectManagerNewTravelRequest;
	private String strBodyManagerNewTravelRequest;
	
	private boolean isEmailNewEmployeeLeaveApproval;
	private boolean isTextNewEmployeeLeaveApproval;
	private String strTextNewEmployeeLeaveApproval;
	private String strSubjectNewEmployeeLeaveApproval;
	private String strBodyNewEmployeeLeaveApproval;

	private boolean isEmailNewEmployeeExtraWorkApproval;
	private boolean isTextNewEmployeeExtraWorkApproval;
	private String strTextNewEmployeeExtraWorkApproval;
	private String strSubjectNewEmployeeExtraWorkApproval;
	private String strBodyNewEmployeeExtraWorkApproval;
	
	private boolean isEmailNewEmployeeTravelApproval;
	private boolean isTextNewEmployeeTravelApproval;
	private String strTextNewEmployeeTravelApproval;
	private String strSubjectNewEmployeeTravelApproval;
	private String strBodyNewEmployeeTravelApproval;
	
	private boolean isEmailNewReimbursementRequest;
	private boolean isTextNewReimbursementRequest;
	private String strTextNewReimbursementRequest;
	private String strSubjectNewReimbursementRequest;
	private String strBodyNewReimbursementRequest;
	
	private boolean isEmailNewManagerReimbursementRequest;
	private boolean isTextNewManagerReimbursementRequest;
	private String strTextNewManagerReimbursementRequest;
	private String strSubjectNewManagerReimbursementRequest;
	private String strBodyNewManagerReimbursementRequest;
	
	private boolean isEmailNewReimbursementApproval;
	private boolean isTextNewReimbursementApproval;
	private String strTextNewReimbursementApproval;
	private String strSubjectNewReimbursementApproval;
	private String strBodyNewReimbursementApproval;
	
	private boolean isEmailNewRequisitionRequest;
	private boolean isTextNewRequisitionRequest;
	private String strTextNewRequisitionRequest;
	private String strSubjectNewRequisitionRequest;
	private String strBodyNewRequisitionRequest;
	
	private boolean isEmailNewRequisitionApproval;
	private boolean isTextNewRequisitionApproval;
	private String strTextNewRequisitionApproval;
	private String strSubjectNewRequisitionApproval;
	private String strBodyNewRequisitionApproval;
	
	private boolean isEmailEmployeeProfileUpdated;
	private boolean isTextEmployeeProfileUpdated;
	private String strTextEmployeeProfileUpdated;
	private String strSubjectEmployeeProfileUpdated;
	private String strBodyEmployeeProfileUpdated;
	
	
	private boolean isEmailPasswordChanged;
	private boolean isTextPasswordChanged;
	private String strTextPasswordChanged;
	private String strSubjectPasswordChanged;
	private String strBodyPasswordChanged;
	
	private boolean isEmailPasswordReset;
	private boolean isTextPasswordReset;
	private String strTextPasswordReset;
	private String strSubjectPasswordReset;
	private String strBodyPasswordReset;
	
	private boolean isEmailForgotPassword;
	private boolean isTextForgotPassword;
	private String strTextForgotPassword;
	private String strSubjectForgotPassword;
	private String strBodyForgotPassword;
	
	
	private boolean isEmailEmployeeSalaryApproved;
	private boolean isTextEmployeeSalaryApproved;
	private String strTextEmployeeSalaryApproved;
	private String strSubjectEmployeeSalaryApproved;
	private String strBodyEmployeeSalaryApproved;
	
	
	private boolean isEmailEmployeePayslipGenerated;
	private boolean isTextEmployeePayslipGenerated;
	private String strTextEmployeePayslipGenerated;
	private String strSubjectEmployeePayslipGenerated;
	private String strBodyEmployeePayslipGenerated;
	
	
	private boolean isEmailSalaryReleased;
	private boolean isTextSalaryReleased;
	private String strTextSalaryReleased;
	private String strSubjectSalaryReleased;
	private String strBodySalaryReleased;
	
	
	private boolean isEmailNewAnnouncement;
	private boolean isTextNewAnnouncement;
	private String strTextNewAnnouncement;
	private String strSubjectNewAnnouncement;
	private String strBodyNewAnnouncement;
	
	
	private boolean isEmailNewRoster;
	private boolean isTextNewRoster;
	private String strTextNewRoster;
	private String strSubjectNewRoster;
	private String strBodyNewRoster;
	
	
	private boolean isEmailRosterChanged;
	private boolean isTextRosterChanged;
	private String strTextRosterChanged;
	private String strSubjectRosterChanged;
	private String strBodyRosterChanged;
	
	private boolean isEmailNewMail;
	private boolean isTextNewMail;
	private String strTextNewMail;
	private String strSubjectNewMail;
	private String strBodyNewMail;
	
	private boolean isEmailNewActivity;
	private boolean isTextNewActivity;
	private String strTextNewActivity;
	private String strSubjectNewActivity;
	private String strBodyNewActivity;
	
	private boolean isEmailEmployeeLeaveCancel;
	private boolean isTextEmployeeLeaveCancel;
	private String strTextEmployeeLeaveCancel;
	private String strSubjectEmployeeLeaveCancel;
	private String strBodyEmployeeLeaveCancel;	
	
	boolean isEmailEmployeeLeavePullout;
	boolean isTextEmployeeLeavePullout;
	String strTextEmployeeLeavePullout;
	String strSubjectEmployeeLeavePullout;
	String strBodyEmployeeLeavePullout;
	
	private boolean isEmailEmployeeExtraWorkCancel;
	private boolean isTextEmployeeExtraWorkCancel;
	private String strTextEmployeeExtraWorkCancel;
	private String strSubjectEmployeeExtraWorkCancel;
	private String strBodyEmployeeExtraWorkCancel;	
	
	private boolean isEmailEmployeeExtraWorkPullout;
	private boolean isTextEmployeeExtraWorkPullout;
	private String strTextEmployeeExtraWorkPullout;
	private String strSubjectEmployeeExtraWorkPullout;
	private String strBodyEmployeeExtraWorkPullout;
	
	private boolean isEmailEmployeeTravelCancel;
	private boolean isTextEmployeeTravelCancel;
	private String strTextEmployeeTravelCancel;
	private String strSubjectEmployeeTravelCancel;
	private String strBodyEmployeeTravelCancel;	
	
	private boolean isEmailEmployeeTravelPullout;
	private boolean isTextEmployeeTravelPullout;
	private String strTextEmployeeTravelPullout;
	private String strSubjectEmployeeTravelPullout;
	private String strBodyEmployeeTravelPullout;
	
	boolean isEmailEmployeeResignationRequest;
	boolean isTextEmployeeResignationRequest;
	String strTextEmployeeResignationRequest;
	String strSubjectEmployeeResignationRequest;
	String strBodyEmployeeResignationRequest;
	
	boolean isEmailEmployeeResignationApproval;
	boolean isTextEmployeeResignationApproval;
	String strTextEmployeeResignationApproval;
	String strSubjectEmployeeResignationApproval;
	String strBodyEmployeeResignationApproval;
	
	//Start Dattatray Date:11-08-21
	boolean isEmailFresherResumeSubmission;
	boolean isTextFresherResumeSubmission;
	String strTextFresherResumeSubmission;
	String strSubjectFresherResumeSubmission;
	String strBodyFresherResumeSubmission;
	//End Dattatray Date:11-08-21
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();

		strProductType = (String)session.getAttribute(PRODUCT_TYPE);
		request.setAttribute(PAGE, PNotificationSettings);
		
		String type = (String) request.getParameter("type");
		String strId = (String) request.getParameter("strId");
		String status = (String) request.getParameter("status");
		String divId = (String) request.getParameter("divId");
		String chId = (String) request.getParameter("chId");
		
//		System.out.println("NS/749---type=="+type);
		if(type != null && type.equalsIgnoreCase("email")) {
			updateEmailNotificationStatus(uF,type,strId,status,divId,chId);
			return "ajax";
		}

		if (getStrNotificationCode() != null && getStrNotificationCode().length() > 0) {
			updateNotificationSettings();
			request.setAttribute(TITLE, TNotificationSettings);
			viewNotificationSettings(uF);
			return SUCCESS;
		}
		
		
		viewNotificationSettings(uF);
		return loadNotificationSettings();
		
	}
	
	  
	private void updateEmailNotificationStatus(UtilityFunctions uF, String type, String strId, String status,String divId,String chId) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update notifications set isemail=? where notification_code = ?");
			pst.setBoolean(1, uF.parseToBoolean(status));
			pst.setInt(2, uF.parseToInt(strId));
			int x = pst.executeUpdate();
			pst.close();
//			System.out.println("update x==>"+x);
			if(x > 0){
				boolean isMail = uF.parseToBoolean(status);
				
				String strEImage = isMail ? "mail_enbl.png" : "mail_disbl.png";
				String strEEnable = isMail ? "Disable Mail Notification" : "Enable Mail Notification" ;
				
				if(isMail){
					isMail = false;
				} else{
					isMail = true;
				}
				
				String strData = status +"::::"+"<img width=20px src=images1/"+strEImage+" title='"+strEEnable+"' " +
						"onclick=updateEmailStatus('"+divId+"',"+strId+","+isMail+",'"+chId+"');>";
				
				request.setAttribute("STATUS_MSG",strData);
			} else {
				request.setAttribute("STATUS_MSG","NA");
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}


	public String loadValidateNotificationSettings() {
		request.setAttribute(PAGE, PNotificationSettings);
		request.setAttribute(TITLE, TNotificationSettings);
		
		return LOAD;
	}
	

	public String loadNotificationSettings() {
		request.setAttribute(PAGE, PNotificationSettings);
		request.setAttribute(TITLE, TNotificationSettings);
		
		return LOAD;
	}

	public String viewNotificationSettings(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);

		Map<String, String> hmNotificationStatus = new HashMap<String, String>();
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("Select * FROM notifications where notification_code > 0 ");
			if(uF.parseToInt(strProductType) == 2) {
				sbQuery.append(" and notification_code < 1300 ");
			} else if(uF.parseToInt(strProductType) == 3) {
				sbQuery.append(" and notification_code >= 1300 or notification_code < 200 ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rst = pst.executeQuery();
			while(rst.next()) {
				
				int nCode = rst.getInt("notification_code");
				
				switch(nCode) {
					
//					---------------------------------- Project Management ----------------------------
					
//					---------------------------------- Customer ----------------------------
					case N_NEW_CLIENT:
						setStrTextNewCustAdded(rst.getString("text_notification"));
						setStrSubjectNewCustAdded(rst.getString("email_subject"));
						setStrBodyNewCustAdded(rst.getString("email_notification"));
						setIsEmailNewCustAdded(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewCustAdded(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_NEW_CLIENT,
							"<span id=\"myDivE"+N_NEW_CLIENT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_NEW_CLIENT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_NEW_CLIENT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_NEW_CLIENT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					case N_NEW_CLIENT_CONTACT:
						setStrTextNewCustContactAdded(rst.getString("text_notification"));
						setStrSubjectNewCustContactAdded(rst.getString("email_subject"));
						setStrBodyNewCustContactAdded(rst.getString("email_notification"));
						setIsEmailNewCustContactAdded(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewCustContactAdded(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_NEW_CLIENT_CONTACT,
							"<span id=\"myDivE"+N_NEW_CLIENT_CONTACT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_NEW_CLIENT_CONTACT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_NEW_CLIENT_CONTACT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_NEW_CLIENT_CONTACT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
//					---------------------------------- Project ----------------------------
					case N_CREATE_NEW_PROJECT:
						setStrTextNewProWelcomeToCust(rst.getString("text_notification"));
						setStrSubjectNewProWelcomeToCust(rst.getString("email_subject"));
						setStrBodyNewProWelcomeToCust(rst.getString("email_notification"));
						setIsEmailNewProWelcomeToCust(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewProWelcomeToCust(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_CREATE_NEW_PROJECT,
							"<span id=\"myDivE"+N_CREATE_NEW_PROJECT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_CREATE_NEW_PROJECT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_CREATE_NEW_PROJECT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_CREATE_NEW_PROJECT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					case N_UPDATE_PROJECT:
						setStrTextProUpdated(rst.getString("text_notification"));
						setStrSubjectProUpdated(rst.getString("email_subject"));
						setStrBodyProUpdated(rst.getString("email_notification"));
						setIsEmailProUpdated(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextProUpdated(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_CREATE_NEW_PROJECT,
							"<span id=\"myDivE"+N_UPDATE_PROJECT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_UPDATE_PROJECT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_UPDATE_PROJECT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_UPDATE_PROJECT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					case N_NEW_TASK_ASSIGN:
						setStrTextNewTask(rst.getString("text_notification"));
						setStrSubjectNewTask(rst.getString("email_subject"));
						setStrBodyNewTask(rst.getString("email_notification"));
						setIsEmailNewTask(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewTask(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_NEW_TASK_ASSIGN,
							"<span id=\"myDivE"+N_NEW_TASK_ASSIGN+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_NEW_TASK_ASSIGN+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_NEW_TASK_ASSIGN+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_NEW_TASK_ASSIGN+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					case N_NEW_DOCUMENT_SHARED:
						setStrTextNewDocShared(rst.getString("text_notification"));
						setStrSubjectNewDocShared(rst.getString("email_subject"));
						setStrBodyNewDocShared(rst.getString("email_notification"));
						setIsEmailNewDocShared(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewDocShared(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_NEW_DOCUMENT_SHARED,
							"<span id=\"myDivE"+N_NEW_DOCUMENT_SHARED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_NEW_DOCUMENT_SHARED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_NEW_DOCUMENT_SHARED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_NEW_DOCUMENT_SHARED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					case N_TASK_COMPLETED:
						setStrTextTaskCompleted(rst.getString("text_notification"));
						setStrSubjectTaskCompleted(rst.getString("email_subject"));
						setStrBodyTaskCompleted(rst.getString("email_notification"));
						setIsEmailTaskCompleted(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextTaskCompleted(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_TASK_COMPLETED,
							"<span id=\"myDivE"+N_TASK_COMPLETED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_TASK_COMPLETED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_TASK_COMPLETED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_TASK_COMPLETED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					case N_PROJECT_COMPLETED:
						setStrTextProCompleted(rst.getString("text_notification"));
						setStrSubjectProCompleted(rst.getString("email_subject"));
						setStrBodyProCompleted(rst.getString("email_notification"));
						setIsEmailProCompleted(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextProCompleted(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_PROJECT_COMPLETED,
							"<span id=\"myDivE"+N_PROJECT_COMPLETED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_PROJECT_COMPLETED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_PROJECT_COMPLETED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_PROJECT_COMPLETED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					case N_PROJECT_BLOCKED:
						setStrTextProBlocked(rst.getString("text_notification"));
						setStrSubjectProBlocked(rst.getString("email_subject"));
						setStrBodyProBlocked(rst.getString("email_notification"));
						setIsEmailProBlocked(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextProBlocked(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_PROJECT_BLOCKED,
							"<span id=\"myDivE"+N_PROJECT_BLOCKED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_PROJECT_BLOCKED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_PROJECT_BLOCKED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_PROJECT_BLOCKED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					case N_MILESTONE_COMPLETED:
						setStrTextMilestoneCompleted(rst.getString("text_notification"));
						setStrSubjectMilestoneCompleted(rst.getString("email_subject"));
						setStrBodyMilestoneCompleted(rst.getString("email_notification"));
						setIsEmailMilestoneCompleted(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextMilestoneCompleted(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_MILESTONE_COMPLETED,
							"<span id=\"myDivE"+N_MILESTONE_COMPLETED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_MILESTONE_COMPLETED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_MILESTONE_COMPLETED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_MILESTONE_COMPLETED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					case N_PROJECT_RE_OPENED:
						setStrTextProReopened(rst.getString("text_notification"));
						setStrSubjectProReopened(rst.getString("email_subject"));
						setStrBodyProReopened(rst.getString("email_notification"));
						setIsEmailProReopened(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextProReopened(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_PROJECT_RE_OPENED,
							"<span id=\"myDivE"+N_PROJECT_RE_OPENED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_PROJECT_RE_OPENED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_PROJECT_RE_OPENED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_PROJECT_RE_OPENED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
//				-------------------------------- Timesheet -----------------------------------
						
					case N_TIMESHEET_SUBMITED:
						setStrTextTimesheetSubmitted(rst.getString("text_notification"));
						setStrSubjectTimesheetSubmitted(rst.getString("email_subject"));
						setStrBodyTimesheetSubmitted(rst.getString("email_notification"));
						setIsEmailTimesheetSubmitted(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextTimesheetSubmitted(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_TIMESHEET_SUBMITED,
							"<span id=\"myDivE"+N_TIMESHEET_SUBMITED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_TIMESHEET_SUBMITED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_TIMESHEET_SUBMITED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_TIMESHEET_SUBMITED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					case N_TIMESHEET_RE_OPENED:
						setStrTextTimesheetReopened(rst.getString("text_notification"));
						setStrSubjectTimesheetReopened(rst.getString("email_subject"));
						setStrBodyTimesheetReopened(rst.getString("email_notification"));
						setIsEmailTimesheetReopened(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextTimesheetReopened(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_TIMESHEET_RE_OPENED,
							"<span id=\"myDivE"+N_TIMESHEET_RE_OPENED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_TIMESHEET_RE_OPENED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_TIMESHEET_RE_OPENED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_TIMESHEET_RE_OPENED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					case N_TIMESHEET_APPROVED:
						setStrTextTimesheetApproved(rst.getString("text_notification"));
						setStrSubjectTimesheetApproved(rst.getString("email_subject"));
						setStrBodyTimesheetApproved(rst.getString("email_notification"));
						setIsEmailTimesheetApproved(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextTimesheetApproved(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_TIMESHEET_APPROVED,
							"<span id=\"myDivE"+N_TIMESHEET_APPROVED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_TIMESHEET_APPROVED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_TIMESHEET_APPROVED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_TIMESHEET_APPROVED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					case N_TIMESHEET_SUBMITED_TO_CUST:
						setStrTextTimesheetSubmitToCust(rst.getString("text_notification"));
						setStrSubjectTimesheetSubmitToCust(rst.getString("email_subject"));
						setStrBodyTimesheetSubmitToCust(rst.getString("email_notification"));
						setIsEmailTimesheetSubmitToCust(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextTimesheetSubmitToCust(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_TIMESHEET_SUBMITED_TO_CUST,
							"<span id=\"myDivE"+N_TIMESHEET_SUBMITED_TO_CUST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_TIMESHEET_SUBMITED_TO_CUST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_TIMESHEET_SUBMITED_TO_CUST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_TIMESHEET_SUBMITED_TO_CUST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
//					----------------------------- Billing -------------------------
						
					case N_MILESTONE_BILLING:
						setStrTextMilestoneBilling(rst.getString("text_notification"));
						setStrSubjectMilestoneBilling(rst.getString("email_subject"));
						setStrBodyMilestoneBilling(rst.getString("email_notification"));
						setIsEmailMilestoneBilling(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextMilestoneBilling(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_MILESTONE_BILLING,
							"<span id=\"myDivE"+N_MILESTONE_BILLING+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_MILESTONE_BILLING+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_MILESTONE_BILLING+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_MILESTONE_BILLING+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					case N_RECURRING_BILLING:
						setStrTextRecurringBilling(rst.getString("text_notification"));
						setStrSubjectRecurringBilling(rst.getString("email_subject"));
						setStrBodyRecurringBilling(rst.getString("email_notification"));
						setIsEmailRecurringBilling(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextRecurringBilling(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_RECURRING_BILLING,
							"<span id=\"myDivE"+N_RECURRING_BILLING+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_RECURRING_BILLING+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_RECURRING_BILLING+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_RECURRING_BILLING+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
//					------------------------- Payment -------------------------	
						
					case N_PAYMENT_ALERT:
						setStrTextPaymentAlert(rst.getString("text_notification"));
						setStrSubjectPaymentAlert(rst.getString("email_subject"));
						setStrBodyPaymentAlert(rst.getString("email_notification"));
						setIsEmailPaymentAlert(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextPaymentAlert(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_PAYMENT_ALERT,
							"<span id=\"myDivE"+N_PAYMENT_ALERT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_PAYMENT_ALERT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_PAYMENT_ALERT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_PAYMENT_ALERT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					case N_PAYMENT_REMINDER:
						setStrTextPaymentReminder(rst.getString("text_notification"));
						setStrSubjectPaymentReminder(rst.getString("email_subject"));
						setStrBodyPaymentReminder(rst.getString("email_notification"));
						setIsEmailPaymentReminder(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextPaymentReminder(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_PAYMENT_REMINDER,
							"<span id=\"myDivE"+N_PAYMENT_REMINDER+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_PAYMENT_REMINDER+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_PAYMENT_REMINDER+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_PAYMENT_REMINDER+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
							

//					---------------------------------- Recruitment ----------------------------
					
					case N_CANDI_FINALIZATION_FROM_EMP:
						setStrTextCandiFinalize(rst.getString("text_notification"));
						setStrSubjectCandiFinalize(rst.getString("email_subject"));
						setStrBodyCandiFinalize(rst.getString("email_notification"));
						setIsEmailCandiFinalize(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextCandiFinalize(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_CANDI_FINALIZATION_FROM_EMP,
							"<span id=\"myDivE"+N_CANDI_FINALIZATION_FROM_EMP+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_CANDI_FINALIZATION_FROM_EMP+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_CANDI_FINALIZATION_FROM_EMP+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_CANDI_FINALIZATION_FROM_EMP+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_CANDI_SHORTLIST_FROM_CONSULTANT:
						setStrTextCandiShortlistFromConsultant(rst.getString("text_notification"));
						setStrSubjectCandiShortlistFromConsultant(rst.getString("email_subject"));
						setStrBodyCandiShortlistFromConsultant(rst.getString("email_notification"));
						setIsEmailCandiShortlistFromConsultant(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextCandiShortlistFromConsultant(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_CANDI_SHORTLIST_FROM_CONSULTANT,
							"<span id=\"myDivE"+N_CANDI_SHORTLIST_FROM_CONSULTANT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_CANDI_SHORTLIST_FROM_CONSULTANT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_CANDI_SHORTLIST_FROM_CONSULTANT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_CANDI_SHORTLIST_FROM_CONSULTANT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_HIRING_LINK_FOR_CONSULTANT:
						setStrTextNewJobToConsultant(rst.getString("text_notification"));
						setStrSubjectNewJobToConsultant(rst.getString("email_subject"));
						setStrBodyNewJobToConsultant(rst.getString("email_notification"));
						setIsEmailNewJobToConsultant(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewJobToConsultant(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_HIRING_LINK_FOR_CONSULTANT,
							"<span id=\"myDivE"+N_HIRING_LINK_FOR_CONSULTANT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_HIRING_LINK_FOR_CONSULTANT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_HIRING_LINK_FOR_CONSULTANT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_HIRING_LINK_FOR_CONSULTANT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_JOB_PROFILE_UPDATE:
						setStrTextJobProfileUpdate(rst.getString("text_notification"));
						setStrSubjectJobProfileUpdate(rst.getString("email_subject"));
						setStrBodyJobProfileUpdate(rst.getString("email_notification"));
						setIsEmailJobProfileUpdate(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextJobProfileUpdate(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_JOB_PROFILE_UPDATE,
							"<span id=\"myDivE"+N_JOB_PROFILE_UPDATE+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_JOB_PROFILE_UPDATE+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_JOB_PROFILE_UPDATE+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_JOB_PROFILE_UPDATE+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_JOB_PROFILE_DENY:
						setStrTextJobProfileDeny(rst.getString("text_notification"));
						setStrSubjectJobProfileDeny(rst.getString("email_subject"));
						setStrBodyJobProfileDeny(rst.getString("email_notification"));
						setIsEmailJobProfileDeny(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextJobProfileDeny(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_JOB_PROFILE_DENY,
							"<span id=\"myDivE"+N_JOB_PROFILE_DENY+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_JOB_PROFILE_DENY+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_JOB_PROFILE_DENY+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_JOB_PROFILE_DENY+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_JOB_PROFILE_APPROVAL:
						setStrTextJobProfileApproval(rst.getString("text_notification"));
						setStrSubjectJobProfileApproval(rst.getString("email_subject"));
						setStrBodyJobProfileApproval(rst.getString("email_notification"));
						setIsEmailJobProfileApproval(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextJobProfileApproval(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_JOB_PROFILE_APPROVAL,
							"<span id=\"myDivE"+N_JOB_PROFILE_APPROVAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_JOB_PROFILE_APPROVAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_JOB_PROFILE_APPROVAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_JOB_PROFILE_APPROVAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_RECRUITMENT_DENY:
						setStrTextRecruitmentRequestDeny(rst.getString("text_notification"));
						setStrSubjectRecruitmentRequestDeny(rst.getString("email_subject"));
						setStrBodyRecruitmentRequestDeny(rst.getString("email_notification"));
						setIsEmailRecruitmentRequestDeny(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextRecruitmentRequestDeny(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_RECRUITMENT_DENY,
							"<span id=\"myDivE"+N_RECRUITMENT_DENY+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_RECRUITMENT_DENY+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_RECRUITMENT_DENY+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_RECRUITMENT_DENY+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_EMP_ONBOARDING_TO_HR:
						setStrTextNewEmpOnboardingToHr(rst.getString("text_notification"));
						setStrSubjectNewEmpOnboardingToHr(rst.getString("email_subject"));
						setStrBodyNewEmpOnboardingToHr(rst.getString("email_notification"));
						setIsEmailNewEmpOnboardingToHr(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewEmpOnboardingToHr(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_EMP_ONBOARDING_TO_HR,
							"<span id=\"myDivE"+N_EMP_ONBOARDING_TO_HR+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMP_ONBOARDING_TO_HR+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_EMP_ONBOARDING_TO_HR+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMP_ONBOARDING_TO_HR+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_CANDI_OFFER_ACCEPT_REJECT:
						setStrTextCandiOfferAcceptReject(rst.getString("text_notification"));
						setStrSubjectCandiOfferAcceptReject(rst.getString("email_subject"));
						setStrBodyCandiOfferAcceptReject(rst.getString("email_notification"));
						setIsEmailCandiOfferAcceptReject(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextCandiOfferAcceptReject(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_CANDI_OFFER_ACCEPT_REJECT,
							"<span id=\"myDivE"+N_CANDI_OFFER_ACCEPT_REJECT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_CANDI_OFFER_ACCEPT_REJECT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_CANDI_OFFER_ACCEPT_REJECT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_CANDI_OFFER_ACCEPT_REJECT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_RECRUITMENT_REQUEST_EDIT:
						setStrTextRecruitmentRequestUpdate(rst.getString("text_notification"));
						setStrSubjectRecruitmentRequestUpdate(rst.getString("email_subject"));
						setStrBodyRecruitmentRequestUpdate(rst.getString("email_notification"));
						setIsEmailRecruitmentRequestUpdate(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextRecruitmentRequestUpdate(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_RECRUITMENT_REQUEST_EDIT,
							"<span id=\"myDivE"+N_RECRUITMENT_REQUEST_EDIT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_RECRUITMENT_REQUEST_EDIT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_RECRUITMENT_REQUEST_EDIT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_RECRUITMENT_REQUEST_EDIT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_NEW_CADIDATE_ADD:
						setStrTextFillCandiInfoByCandi(rst.getString("text_notification"));
						setStrSubjectFillCandiInfoByCandi(rst.getString("email_subject"));
						setStrBodyFillCandiInfoByCandi(rst.getString("email_notification"));
						setIsEmailFillCandiInfoByCandi(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextFillCandiInfoByCandi(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_NEW_CADIDATE_ADD,
							"<span id=\"myDivE"+N_NEW_CADIDATE_ADD+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_NEW_CADIDATE_ADD+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_NEW_CADIDATE_ADD+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_NEW_CADIDATE_ADD+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
						
					case N_NEW_CADIDATE_BACKGROUND_VERIFICATION:
						setStrTextCandiBackgroudVerification(rst.getString("text_notification"));
						setStrSubjectCandiBackgroudVerification(rst.getString("email_subject"));
						setStrBodyCandiBackgroudVerification(rst.getString("email_notification"));
						setIsEmailCandiBackgroudVerification(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextCandiBackgroudVerification(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_NEW_CADIDATE_BACKGROUND_VERIFICATION,
							"<span id=\"myDivE"+N_NEW_CADIDATE_BACKGROUND_VERIFICATION+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_NEW_CADIDATE_BACKGROUND_VERIFICATION+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_NEW_CADIDATE_BACKGROUND_VERIFICATION+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_NEW_CADIDATE_BACKGROUND_VERIFICATION+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_EMP_LOGIN_DETAILS:
						setStrTextCandiEmpLoginDetail(rst.getString("text_notification"));
						setStrSubjectCandiEmpLoginDetail(rst.getString("email_subject"));
						setStrBodyCandiEmpLoginDetail(rst.getString("email_notification"));
						setIsEmailCandiEmpLoginDetail(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextCandiEmpLoginDetail(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_EMP_LOGIN_DETAILS,
							"<span id=\"myDivE"+N_EMP_LOGIN_DETAILS+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMP_LOGIN_DETAILS+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_EMP_LOGIN_DETAILS+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMP_LOGIN_DETAILS+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_CANDI_ONBOARDING_CTC:
						setStrTextCandiOnboarding(rst.getString("text_notification"));
						setStrSubjectCandiOnboarding(rst.getString("email_subject"));
						setStrBodyCandiOnboarding(rst.getString("email_notification"));
						setIsEmailCandiOnboarding(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextCandiOnboarding(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_CANDI_ONBOARDING_CTC,
							"<span id=\"myDivE"+N_CANDI_ONBOARDING_CTC+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_CANDI_ONBOARDING_CTC+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_CANDI_ONBOARDING_CTC+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_CANDI_ONBOARDING_CTC+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_INTERVIEW_DATE_MAIL_FOR_ROUNDEMP:
						setStrTextPanelistInterviewDate(rst.getString("text_notification"));
						setStrSubjectPanelistInterviewDate(rst.getString("email_subject"));
						setStrBodyPanelistInterviewDate(rst.getString("email_notification"));
						setIsEmailPanelistInterviewDate(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextPanelistInterviewDate(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_INTERVIEW_DATE_MAIL_FOR_ROUNDEMP,
							"<span id=\"myDivE"+N_INTERVIEW_DATE_MAIL_FOR_ROUNDEMP+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_INTERVIEW_DATE_MAIL_FOR_ROUNDEMP+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_INTERVIEW_DATE_MAIL_FOR_ROUNDEMP+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_INTERVIEW_DATE_MAIL_FOR_ROUNDEMP+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_INTERVIEW_DATE_MAIL_FOR_CANDI:
						setStrTextCandiInterviewDate(rst.getString("text_notification"));
						setStrSubjectCandiInterviewDate(rst.getString("email_subject"));
						setStrBodyCandiInterviewDate(rst.getString("email_notification"));
						setIsEmailCandiInterviewDate(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextCandiInterviewDate(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_INTERVIEW_DATE_MAIL_FOR_CANDI,
							"<span id=\"myDivE"+N_INTERVIEW_DATE_MAIL_FOR_CANDI+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_INTERVIEW_DATE_MAIL_FOR_CANDI+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_INTERVIEW_DATE_MAIL_FOR_CANDI+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_INTERVIEW_DATE_MAIL_FOR_CANDI+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_CANDI_JOINING_OFFER_CTC:
						setStrTextCandiJoiningOfferCTC(rst.getString("text_notification"));
						setStrSubjectCandiJoiningOfferCTC(rst.getString("email_subject"));
						setStrBodyCandiJoiningOfferCTC(rst.getString("email_notification"));
						setIsEmailCandiJoiningOfferCTC(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextCandiJoiningOfferCTC(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_CANDI_JOINING_OFFER_CTC,
							"<span id=\"myDivE"+N_CANDI_JOINING_OFFER_CTC+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_CANDI_JOINING_OFFER_CTC+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_CANDI_JOINING_OFFER_CTC+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_CANDI_JOINING_OFFER_CTC+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_CANDI_SPECIFY_OTHER_DATE:
						setStrTextCandiSpecifyOtherDate(rst.getString("text_notification"));
						setStrSubjectCandiSpecifyOtherDate(rst.getString("email_subject"));
						setStrBodyCandiSpecifyOtherDate(rst.getString("email_notification"));
						setIsEmailCandiSpecifyOtherDate(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextCandiSpecifyOtherDate(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_CANDI_SPECIFY_OTHER_DATE,
							"<span id=\"myDivE"+N_CANDI_SPECIFY_OTHER_DATE+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_CANDI_SPECIFY_OTHER_DATE+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_CANDI_SPECIFY_OTHER_DATE+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_CANDI_SPECIFY_OTHER_DATE+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_APPLICATION_SHORTLIST:
						setStrTextApplicationShortlist(rst.getString("text_notification"));
						setStrSubjectApplicationShortlist(rst.getString("email_subject"));
						setStrBodyApplicationShortlist(rst.getString("email_notification"));
						setIsEmailApplicationShortlist(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextApplicationShortlist(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_APPLICATION_SHORTLIST,
							"<span id=\"myDivE"+N_APPLICATION_SHORTLIST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_APPLICATION_SHORTLIST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_APPLICATION_SHORTLIST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_APPLICATION_SHORTLIST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_RECRUITMENT_APPROVAL:
						setStrTextRecruitmentApproval(rst.getString("text_notification"));
						setStrSubjectRecruitmentApproval(rst.getString("email_subject"));
						setStrBodyRecruitmentApproval(rst.getString("email_notification"));
						setIsEmailRecruitmentApproval(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextRecruitmentApproval(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_RECRUITMENT_APPROVAL,
							"<span id=\"myDivE"+N_RECRUITMENT_APPROVAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_RECRUITMENT_APPROVAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_RECRUITMENT_APPROVAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_RECRUITMENT_APPROVAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_RECRUITMENT_MAIL_TO_EMP:
						setStrTextNewRecruitmentToEmp(rst.getString("text_notification"));
						setStrSubjectNewRecruitmentToEmp(rst.getString("email_subject"));
						setStrBodyNewRecruitmentToEmp(rst.getString("email_notification"));
						setIsEmailNewRecruitmentToEmp(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewRecruitmentToEmp(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_RECRUITMENT_MAIL_TO_EMP,
							"<span id=\"myDivE"+N_RECRUITMENT_MAIL_TO_EMP+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_RECRUITMENT_MAIL_TO_EMP+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_RECRUITMENT_MAIL_TO_EMP+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_RECRUITMENT_MAIL_TO_EMP+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_RECRUITMENT_REQUEST: 
						setStrTextRecruitmentRequest(rst.getString("text_notification"));
						setStrSubjectRecruitmentRequest(rst.getString("email_subject"));
						setStrBodyRecruitmentRequest(rst.getString("email_notification"));
						setIsEmailRecruitmentRequest(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextRecruitmentRequest(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_RECRUITMENT_REQUEST,
							"<span id=\"myDivE"+N_RECRUITMENT_REQUEST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_RECRUITMENT_REQUEST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_RECRUITMENT_REQUEST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_RECRUITMENT_REQUEST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
						// Start Dattaray Date:30-June-2021
					case N_CANDI_TAKE_ASSESSMENT: 
						setStrTextCandiAssessmentForm(rst.getString("text_notification"));
						setStrSubjectCandiAssessmentForm(rst.getString("email_subject"));
						setStrBodyCandiAssessmentForm(rst.getString("email_notification"));
						setIsEmailCandiAssessmentForm(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextCandiAssessmentForm(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_CANDI_TAKE_ASSESSMENT,
							"<span id=\"myDivE"+N_CANDI_TAKE_ASSESSMENT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_CANDI_TAKE_ASSESSMENT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_CANDI_TAKE_ASSESSMENT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_CANDI_TAKE_ASSESSMENT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						// End Dattaray Date:30-June-2021
					

//					---------------------------------- Learning ----------------------------
					
					case N_L_TRAINING_FEEDBACK_FROM_TRAINER_EMP:
						setStrTextLTrainingFeedbackLearner(rst.getString("text_notification"));
						setStrSubjectLTrainingFeedbackLearner(rst.getString("email_subject"));
						setStrBodyLTrainingFeedbackLearner(rst.getString("email_notification"));
						setIsEmailLTrainingFeedbackLearner(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextLTrainingFeedbackLearner(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_L_TRAINING_FEEDBACK_FROM_TRAINER_EMP,
							"<span id=\"myDivE"+N_L_TRAINING_FEEDBACK_FROM_TRAINER_EMP+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_L_TRAINING_FEEDBACK_FROM_TRAINER_EMP+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_L_TRAINING_FEEDBACK_FROM_TRAINER_EMP+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_L_TRAINING_FEEDBACK_FROM_TRAINER_EMP+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_L_TRAINING_FEEDBACK_FROM_TRAINER_TO_HR: 
						setStrTextLTrainingFeedbackTrainer(rst.getString("text_notification"));
						setStrSubjectLTrainingFeedbackTrainer(rst.getString("email_subject"));
						setStrBodyLTrainingFeedbackTrainer(rst.getString("email_notification"));
						setIsEmailLTrainingFeedbackTrainer(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextLTrainingFeedbackTrainer(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_L_TRAINING_FEEDBACK_FROM_TRAINER_TO_HR,
							"<span id=\"myDivE"+N_L_TRAINING_FEEDBACK_FROM_TRAINER_TO_HR+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_L_TRAINING_FEEDBACK_FROM_TRAINER_TO_HR+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_L_TRAINING_FEEDBACK_FROM_TRAINER_TO_HR+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_L_TRAINING_FEEDBACK_FROM_TRAINER_TO_HR+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_LEARNING_ASSESS_FINALIZATION_TO_EMP:
						setStrTextLAssessFinalizeForHr(rst.getString("text_notification"));
						setStrSubjectLAssessFinalizeForHr(rst.getString("email_subject"));
						setStrBodyLAssessFinalizeForHr(rst.getString("email_notification"));
						setIsEmailLAssessFinalizeForHr(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextLAssessFinalizeForHr(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_LEARNING_ASSESS_FINALIZATION_TO_EMP,
							"<span id=\"myDivE"+N_LEARNING_ASSESS_FINALIZATION_TO_EMP+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_LEARNING_ASSESS_FINALIZATION_TO_EMP+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_LEARNING_ASSESS_FINALIZATION_TO_EMP+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_LEARNING_ASSESS_FINALIZATION_TO_EMP+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_LEARNING_TRAINING_FINALIZATION_TO_EMP: 
						setStrTextLTrainingFinalizeForHr(rst.getString("text_notification"));
						setStrSubjectLTrainingFinalizeForHr(rst.getString("email_subject"));
						setStrBodyLTrainingFinalizeForHr(rst.getString("email_notification"));
						setIsEmailLTrainingFinalizeForHr(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextLTrainingFinalizeForHr(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_LEARNING_TRAINING_FINALIZATION_TO_EMP,
							"<span id=\"myDivE"+N_LEARNING_TRAINING_FINALIZATION_TO_EMP+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_LEARNING_TRAINING_FINALIZATION_TO_EMP+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_LEARNING_TRAINING_FINALIZATION_TO_EMP+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_LEARNING_TRAINING_FINALIZATION_TO_EMP+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_NEW_LEARNING_PLAN_FOR_HR: 
						setStrTextLearningPlanForHr(rst.getString("text_notification"));
						setStrSubjectLearningPlanForHr(rst.getString("email_subject"));
						setStrBodyLearningPlanForHr(rst.getString("email_notification"));
						setIsEmailLearningPlanForHr(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextLearningPlanForHr(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_NEW_LEARNING_PLAN_FOR_HR,
							"<span id=\"myDivE"+N_NEW_LEARNING_PLAN_FOR_HR+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_NEW_LEARNING_PLAN_FOR_HR+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_NEW_LEARNING_PLAN_FOR_HR+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_NEW_LEARNING_PLAN_FOR_HR+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
					
					case N_NEW_LEARNING_PLAN_FOR_LEARNERS: 
						setStrTextLearningPlanForLearner(rst.getString("text_notification"));
						setStrSubjectLearningPlanForLearner(rst.getString("email_subject"));
						setStrBodyLearningPlanForLearner(rst.getString("email_notification"));
						setIsEmailLearningPlanForLearner(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextLearningPlanForLearner(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_NEW_LEARNING_PLAN_FOR_LEARNERS,
							"<span id=\"myDivE"+N_NEW_LEARNING_PLAN_FOR_LEARNERS+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_NEW_LEARNING_PLAN_FOR_LEARNERS+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_NEW_LEARNING_PLAN_FOR_LEARNERS+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_NEW_LEARNING_PLAN_FOR_LEARNERS+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
	//					---------------------------------- Performance ----------------------------
					
					case N_EXECUTIVE_TARGET:
						setStrTextExecutiveTarget(rst.getString("text_notification"));
						setStrSubjectExecutiveTarget(rst.getString("email_subject"));
						setStrBodyExecutiveTarget(rst.getString("email_notification"));
						setIsEmailExecutiveTarget(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextExecutiveTarget(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_EXECUTIVE_TARGET,
							"<span id=\"myDivE"+N_EXECUTIVE_TARGET+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EXECUTIVE_TARGET+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_EXECUTIVE_TARGET+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EXECUTIVE_TARGET+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
						
					case N_EXECUTIVE_KRA: 
						setStrTextExecutiveKRA(rst.getString("text_notification"));
						setStrSubjectExecutiveKRA(rst.getString("email_subject"));
						setStrBodyExecutiveKRA(rst.getString("email_notification"));
						setIsEmailExecutiveKRA(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextExecutiveKRA(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_EXECUTIVE_KRA,
							"<span id=\"myDivE"+N_EXECUTIVE_KRA+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EXECUTIVE_KRA+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_EXECUTIVE_KRA+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EXECUTIVE_KRA+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
						
					case N_EXECUTIVE_GOAL: 
						setStrTextExecutiveGoal(rst.getString("text_notification"));
						setStrSubjectExecutiveGoal(rst.getString("email_subject"));
						setStrBodyExecutiveGoal(rst.getString("email_notification"));
						setIsEmailExecutiveGoal(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextExecutiveGoal(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_EXECUTIVE_GOAL,
							"<span id=\"myDivE"+N_EXECUTIVE_GOAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EXECUTIVE_GOAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_EXECUTIVE_GOAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EXECUTIVE_GOAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
						
					case N_MANAGER_GOAL: 
						setStrTextManagerGoal(rst.getString("text_notification"));
						setStrSubjectManagerGoal(rst.getString("email_subject"));
						setStrBodyManagerGoal(rst.getString("email_notification"));
						setIsEmailManagerGoal(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextManagerGoal(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_MANAGER_GOAL,
							"<span id=\"myDivE"+N_MANAGER_GOAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_MANAGER_GOAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_MANAGER_GOAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_MANAGER_GOAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
						
					case N_LEARNING_GAP_FOR_HR: 
						setStrTextLearningGapForHr(rst.getString("text_notification"));
						setStrSubjectLearningGapForHr(rst.getString("email_subject"));
						setStrBodyLearningGapForHr(rst.getString("email_notification"));
						setIsEmailLearningGapForHr(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextLearningGapForHr(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_LEARNING_GAP_FOR_HR,
							"<span id=\"myDivE"+N_LEARNING_GAP_FOR_HR+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_LEARNING_GAP_FOR_HR+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_LEARNING_GAP_FOR_HR+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_LEARNING_GAP_FOR_HR+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
						
					case N_REVIEW_FINALIZATION_FOR_EMP: 
						setStrTextReviewFinalizationForEmp(rst.getString("text_notification"));
						setStrSubjectReviewFinalizationForEmp(rst.getString("email_subject"));
						setStrBodyReviewFinalizationForEmp(rst.getString("email_notification"));
						setIsEmailReviewFinalizationForEmp(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextReviewFinalizationForEmp(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_REVIEW_FINALIZATION_FOR_EMP,
							"<span id=\"myDivE"+N_REVIEW_FINALIZATION_FOR_EMP+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_REVIEW_FINALIZATION_FOR_EMP+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_REVIEW_FINALIZATION_FOR_EMP+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_REVIEW_FINALIZATION_FOR_EMP+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
						
					/*case N_REVIEW_FINALIZATION_FOR_HR: 
						setStrTextReviewFinalizationForHr(rst.getString("text_notification"));
						setStrSubjectReviewFinalizationForHr(rst.getString("email_subject"));
						setStrBodyReviewFinalizationForHr(rst.getString("email_notification"));
						setIsEmailReviewFinalizationForHr(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextReviewFinalizationForHr(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_REVIEW_FINALIZATION_FOR_HR,
							"<span id=\"myDivE"+N_REVIEW_FINALIZATION_FOR_HR+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_REVIEW_FINALIZATION_FOR_HR+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_REVIEW_FINALIZATION_FOR_HR+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_REVIEW_FINALIZATION_FOR_HR+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;*/
						
						
					/*case N_EMP_REVIW_SUBMITED: 
						setStrTextEmpReviewSubmit(rst.getString("text_notification"));
						setStrSubjectEmpReviewSubmit(rst.getString("email_subject"));
						setStrBodyEmpReviewSubmit(rst.getString("email_notification"));
						setIsEmailEmpReviewSubmit(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextEmpReviewSubmit(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_EMP_REVIW_SUBMITED,
							"<span id=\"myDivE"+N_EMP_REVIW_SUBMITED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMP_REVIW_SUBMITED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_EMP_REVIW_SUBMITED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMP_REVIW_SUBMITED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;*/
						
						
					case N_NEW_REVIW_PUBLISH:
						setStrTextNewReviewPublish(rst.getString("text_notification"));
						setStrSubjectNewReviewPublish(rst.getString("email_subject"));
						setStrBodyNewReviewPublish(rst.getString("email_notification"));
						setIsEmailNewReviewPublish(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewReviewPublish(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_NEW_REVIW_PUBLISH,
							"<span id=\"myDivE"+N_NEW_REVIW_PUBLISH+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_NEW_REVIW_PUBLISH+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_NEW_REVIW_PUBLISH+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_NEW_REVIW_PUBLISH+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
						
					case N_PENDING_REVIEW_REMINDER:
						setStrTextPendingReviewReminder(rst.getString("text_notification"));
						setStrSubjectPendingReviewReminder(rst.getString("email_subject"));
						setStrBodyPendingReviewReminder(rst.getString("email_notification"));
						setIsEmailPendingReviewReminder(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextPendingReviewReminder(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_PENDING_REVIEW_REMINDER,
							"<span id=\"myDivE"+N_PENDING_REVIEW_REMINDER+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_PENDING_REVIEW_REMINDER+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_PENDING_REVIEW_REMINDER+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_PENDING_REVIEW_REMINDER+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;	
					
						
						
	//						-------------------- Earth -------------------------
						
					case N_NEW_EMPLOYEE:
						setStrTextNewEmployee(rst.getString("text_notification"));
						setStrSubjectNewEmployee(rst.getString("email_subject"));
						setStrBodyNewEmployee(rst.getString("email_notification"));
						setIsEmailNewEmployee(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewEmployee(uF.parseToBoolean(rst.getString("istext")));
						setStrBackgroundNewEmpImageFileName(rst.getString("background_image"));

						//set(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_NEW_EMPLOYEE,
							"<span id=\"myDivE"+N_NEW_EMPLOYEE+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_NEW_EMPLOYEE+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_NEW_EMPLOYEE+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_NEW_EMPLOYEE+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
						
					case N_EMPLOYEE_STATUS_CHANGE:
						setStrTextNewEmployeeStatusChange(rst.getString("text_notification"));
						setStrSubjectNewEmployeeStatusChange(rst.getString("email_subject"));
						setStrBodyNewEmployeeStatusChange(rst.getString("email_notification"));
						setIsEmailNewEmployeeStatusChange(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewEmployeeStatusChange(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_STATUS_CHANGE,
							"<span id=\"myDivE"+N_EMPLOYEE_STATUS_CHANGE+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_STATUS_CHANGE+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_EMPLOYEE_STATUS_CHANGE+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_STATUS_CHANGE+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;
						
						
					/*case N_EMPLOYEE_LEAVE_REQUEST:
						setStrTextNewEmployeeLeaveRequest(rst.getString("text_notification"));
						setStrSubjectNewEmployeeLeaveRequest(rst.getString("email_subject"));
						setStrBodyNewEmployeeLeaveRequest(rst.getString("email_notification"));
						setIsEmailNewEmployeeLeaveRequest(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewEmployeeLeaveRequest(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_LEAVE_REQUEST,
							"<span id=\"myDivE"+N_EMPLOYEE_LEAVE_REQUEST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_LEAVE_REQUEST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_EMPLOYEE_LEAVE_REQUEST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_LEAVE_REQUEST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;*/
						
						
					case N_MANAGER_LEAVE_REQUEST:
						setStrTextManagerNewLeaveRequest(rst.getString("text_notification"));
						setStrSubjectManagerNewLeaveRequest(rst.getString("email_subject"));
						setStrBodyManagerNewLeaveRequest(rst.getString("email_notification"));
						setIsEmailManagerNewLeaveRequest(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextManagerNewLeaveRequest(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_MANAGER_LEAVE_REQUEST,
							"<span id=\"myDivE"+N_MANAGER_LEAVE_REQUEST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_MANAGER_LEAVE_REQUEST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_MANAGER_LEAVE_REQUEST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_MANAGER_LEAVE_REQUEST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;	
						
						
					case N_EMPLOYEE_LEAVE_APPROVAL:
						setStrTextNewEmployeeLeaveApproval(rst.getString("text_notification"));
						setStrSubjectNewEmployeeLeaveApproval(rst.getString("email_subject"));
						setStrBodyNewEmployeeLeaveApproval(rst.getString("email_notification"));
						setIsEmailNewEmployeeLeaveApproval(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewEmployeeLeaveApproval(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_LEAVE_APPROVAL,
								"<span id=\"myDivE"+N_EMPLOYEE_LEAVE_APPROVAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_LEAVE_APPROVAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_EMPLOYEE_LEAVE_APPROVAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_LEAVE_APPROVAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						
					case N_EMPLOYEE_LEAVE_CANCEL:
						setStrTextEmployeeLeaveCancel(rst.getString("text_notification"));
						setStrSubjectEmployeeLeaveCancel(rst.getString("email_subject"));
						setStrBodyEmployeeLeaveCancel(rst.getString("email_notification"));
						setIsEmailEmployeeLeaveCancel(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextEmployeeLeaveCancel(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_LEAVE_CANCEL,
								"<span id=\"myDivE"+N_EMPLOYEE_LEAVE_CANCEL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_LEAVE_CANCEL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_EMPLOYEE_LEAVE_CANCEL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_LEAVE_CANCEL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;		

					case N_EMPLOYEE_LEAVE_PULLOUT:
						setStrTextEmployeeLeavePullout(rst.getString("text_notification"));
						setStrSubjectEmployeeLeavePullout(rst.getString("email_subject"));
						setStrBodyEmployeeLeavePullout(rst.getString("email_notification"));
						setIsEmailEmployeeLeavePullout(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextEmployeeLeavePullout(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_LEAVE_PULLOUT,
								"<span id=\"myDivE"+N_EMPLOYEE_LEAVE_PULLOUT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_LEAVE_PULLOUT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_EMPLOYEE_LEAVE_PULLOUT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_LEAVE_PULLOUT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						
					case N_MANAGER_EXTRA_WORK_REQUEST:
						setStrTextManagerNewExtraWorkRequest(rst.getString("text_notification"));
						setStrSubjectManagerNewExtraWorkRequest(rst.getString("email_subject"));
						setStrBodyManagerNewExtraWorkRequest(rst.getString("email_notification"));
						setIsEmailManagerNewExtraWorkRequest(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextManagerNewExtraWorkRequest(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_MANAGER_EXTRA_WORK_REQUEST,
							"<span id=\"myDivE"+N_MANAGER_EXTRA_WORK_REQUEST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_MANAGER_EXTRA_WORK_REQUEST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_MANAGER_EXTRA_WORK_REQUEST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_MANAGER_EXTRA_WORK_REQUEST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;	
						
						
					case N_EMPLOYEE_EXTRA_WORK_APPROVAL:
						setStrTextNewEmployeeExtraWorkApproval(rst.getString("text_notification"));
						setStrSubjectNewEmployeeExtraWorkApproval(rst.getString("email_subject"));
						setStrBodyNewEmployeeExtraWorkApproval(rst.getString("email_notification"));
						setIsEmailNewEmployeeExtraWorkApproval(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewEmployeeExtraWorkApproval(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_EXTRA_WORK_APPROVAL,
								"<span id=\"myDivE"+N_EMPLOYEE_EXTRA_WORK_APPROVAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_EXTRA_WORK_APPROVAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_EMPLOYEE_EXTRA_WORK_APPROVAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_EXTRA_WORK_APPROVAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						
					case N_EMPLOYEE_EXTRA_WORK_CANCEL:
						setStrTextEmployeeExtraWorkCancel(rst.getString("text_notification"));
						setStrSubjectEmployeeExtraWorkCancel(rst.getString("email_subject"));
						setStrBodyEmployeeExtraWorkCancel(rst.getString("email_notification"));
						setIsEmailEmployeeExtraWorkCancel(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextEmployeeExtraWorkCancel(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_EXTRA_WORK_CANCEL,
								"<span id=\"myDivE"+N_EMPLOYEE_EXTRA_WORK_CANCEL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_EXTRA_WORK_CANCEL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_EMPLOYEE_EXTRA_WORK_CANCEL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_EXTRA_WORK_CANCEL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;		

					case N_EMPLOYEE_EXTRA_WORK_PULLOUT:
						setStrTextEmployeeExtraWorkPullout(rst.getString("text_notification"));
						setStrSubjectEmployeeExtraWorkPullout(rst.getString("email_subject"));
						setStrBodyEmployeeExtraWorkPullout(rst.getString("email_notification"));
						setIsEmailEmployeeExtraWorkPullout(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextEmployeeExtraWorkPullout(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_EXTRA_WORK_PULLOUT,
								"<span id=\"myDivE"+N_EMPLOYEE_EXTRA_WORK_PULLOUT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_EXTRA_WORK_PULLOUT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_EMPLOYEE_EXTRA_WORK_PULLOUT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_EXTRA_WORK_PULLOUT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						
					case N_MANAGER_TRAVEL_REQUEST:
						setStrTextManagerNewTravelRequest(rst.getString("text_notification"));
						setStrSubjectManagerNewTravelRequest(rst.getString("email_subject"));
						setStrBodyManagerNewTravelRequest(rst.getString("email_notification"));
						setIsEmailManagerNewTravelRequest(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextManagerNewTravelRequest(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_MANAGER_TRAVEL_REQUEST,
							"<span id=\"myDivE"+N_MANAGER_TRAVEL_REQUEST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_MANAGER_TRAVEL_REQUEST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
							+"<span id=\"myDivT"+N_MANAGER_TRAVEL_REQUEST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_MANAGER_TRAVEL_REQUEST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
							);
						break;	
						
						
					case N_EMPLOYEE_TRAVEL_APPROVAL:
						setStrTextNewEmployeeTravelApproval(rst.getString("text_notification"));
						setStrSubjectNewEmployeeTravelApproval(rst.getString("email_subject"));
						setStrBodyNewEmployeeTravelApproval(rst.getString("email_notification"));
						setIsEmailNewEmployeeTravelApproval(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewEmployeeTravelApproval(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_TRAVEL_APPROVAL,
								"<span id=\"myDivE"+N_EMPLOYEE_TRAVEL_APPROVAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_TRAVEL_APPROVAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_EMPLOYEE_TRAVEL_APPROVAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_TRAVEL_APPROVAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						
					case N_EMPLOYEE_TRAVEL_CANCEL:
						setStrTextEmployeeTravelCancel(rst.getString("text_notification"));
						setStrSubjectEmployeeTravelCancel(rst.getString("email_subject"));
						setStrBodyEmployeeTravelCancel(rst.getString("email_notification"));
						setIsEmailEmployeeTravelCancel(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextEmployeeTravelCancel(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_TRAVEL_CANCEL,
								"<span id=\"myDivE"+N_EMPLOYEE_TRAVEL_CANCEL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_TRAVEL_CANCEL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_EMPLOYEE_TRAVEL_CANCEL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_TRAVEL_CANCEL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;		

					case N_EMPLOYEE_TRAVEL_PULLOUT:
						setStrTextEmployeeTravelPullout(rst.getString("text_notification"));
						setStrSubjectEmployeeTravelPullout(rst.getString("email_subject"));
						setStrBodyEmployeeTravelPullout(rst.getString("email_notification"));
						setIsEmailEmployeeTravelPullout(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextEmployeeTravelPullout(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_TRAVEL_PULLOUT,
								"<span id=\"myDivE"+N_EMPLOYEE_TRAVEL_PULLOUT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_TRAVEL_PULLOUT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_EMPLOYEE_TRAVEL_PULLOUT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_TRAVEL_PULLOUT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
					
						
					/*case N_EMPLOYEE_REIMBURSEMENT_REQUEST:
						setStrTextNewReimbursementRequest(rst.getString("text_notification"));
						setStrSubjectNewReimbursementRequest(rst.getString("email_subject"));
						setStrBodyNewReimbursementRequest(rst.getString("email_notification"));
						setIsEmailNewReimbursementRequest(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewReimbursementRequest(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_REIMBURSEMENT_REQUEST,
								"<span id=\"myDivE"+N_EMPLOYEE_REIMBURSEMENT_REQUEST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_REIMBURSEMENT_REQUEST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_EMPLOYEE_REIMBURSEMENT_REQUEST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_REIMBURSEMENT_REQUEST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;*/
						
					
					case N_MANAGER_REIMBURSEMENT_REQUEST:
						setStrTextNewManagerReimbursementRequest(rst.getString("text_notification"));
						setStrSubjectNewManagerReimbursementRequest(rst.getString("email_subject"));
						setStrBodyNewManagerReimbursementRequest(rst.getString("email_notification"));
						setIsEmailNewManagerReimbursementRequest(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewManagerReimbursementRequest(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_MANAGER_REIMBURSEMENT_REQUEST,
								"<span id=\"myDivE"+N_MANAGER_REIMBURSEMENT_REQUEST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_MANAGER_REIMBURSEMENT_REQUEST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_MANAGER_REIMBURSEMENT_REQUEST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_MANAGER_REIMBURSEMENT_REQUEST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						
						
					case N_EMPLOYEE_REIMBURSEMENT_APPROVAL:
						setStrTextNewReimbursementApproval(rst.getString("text_notification"));
						setStrSubjectNewReimbursementApproval(rst.getString("email_subject"));
						setStrBodyNewReimbursementApproval(rst.getString("email_notification"));
						setIsEmailNewReimbursementApproval(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewReimbursementApproval(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_REIMBURSEMENT_APPROVAL,
								"<span id=\"myDivE"+N_EMPLOYEE_REIMBURSEMENT_APPROVAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_REIMBURSEMENT_APPROVAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_EMPLOYEE_REIMBURSEMENT_APPROVAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_REIMBURSEMENT_APPROVAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						
						
					/*case N_EMPLOYEE_REQUISITION_REQUEST:
						setStrTextNewRequisitionRequest(rst.getString("text_notification"));
						setStrSubjectNewRequisitionRequest(rst.getString("email_subject"));
						setStrBodyNewRequisitionRequest(rst.getString("email_notification"));
						setIsEmailNewRequisitionRequest(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewRequisitionRequest(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_REQUISITION_REQUEST,
								"<span id=\"myDivE"+N_EMPLOYEE_REQUISITION_REQUEST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_REQUISITION_REQUEST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_EMPLOYEE_REQUISITION_REQUEST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_REQUISITION_REQUEST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						
						break;*/
						
						
					case N_EMPLOYEE_REQUISITION_APPROVAL:
						setStrTextNewRequisitionApproval(rst.getString("text_notification"));
						setStrSubjectNewRequisitionApproval(rst.getString("email_subject"));
						setStrBodyNewRequisitionApproval(rst.getString("email_notification"));
						setIsEmailNewRequisitionApproval(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewRequisitionApproval(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_REQUISITION_APPROVAL,
								"<span id=\"myDivE"+N_EMPLOYEE_REQUISITION_APPROVAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_REQUISITION_APPROVAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_EMPLOYEE_REQUISITION_APPROVAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_REQUISITION_APPROVAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						
						
					case N_UPD_EMPLOYEE_PROFILE:
						setStrTextEmployeeProfileUpdated(rst.getString("text_notification"));
						setStrSubjectEmployeeProfileUpdated(rst.getString("email_subject"));
						setStrBodyEmployeeProfileUpdated(rst.getString("email_notification"));
						setIsEmailEmployeeProfileUpdated(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextEmployeeProfileUpdated(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_UPD_EMPLOYEE_PROFILE,
								"<span id=\"myDivE"+N_UPD_EMPLOYEE_PROFILE+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_UPD_EMPLOYEE_PROFILE+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_UPD_EMPLOYEE_PROFILE+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_UPD_EMPLOYEE_PROFILE+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						
						
					case N_UPD_PASSWORD:
						setStrTextPasswordChanged(rst.getString("text_notification"));
						setStrSubjectPasswordChanged(rst.getString("email_subject"));
						setStrBodyPasswordChanged(rst.getString("email_notification"));
						setIsEmailPasswordChanged(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextPasswordChanged(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_UPD_PASSWORD,
								"<span id=\"myDivE"+N_UPD_PASSWORD+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_UPD_PASSWORD+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_UPD_PASSWORD+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_UPD_PASSWORD+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						
					case N_RESET_PASSWORD:
						setStrTextPasswordReset(rst.getString("text_notification"));
						setStrSubjectPasswordReset(rst.getString("email_subject"));
						setStrBodyPasswordReset(rst.getString("email_notification"));
						setIsEmailPasswordReset(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextPasswordReset(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_RESET_PASSWORD,
								"<span id=\"myDivE"+N_RESET_PASSWORD+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_RESET_PASSWORD+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_RESET_PASSWORD+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_RESET_PASSWORD+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;	
						
					case N_FORGOT_PASSWORD:
						setStrTextForgotPassword(rst.getString("text_notification"));
						setStrSubjectForgotPassword(rst.getString("email_subject"));
						setStrBodyForgotPassword(rst.getString("email_notification"));
						setIsEmailForgotPassword(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextForgotPassword(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_FORGOT_PASSWORD,
								"<span id=\"myDivE"+N_FORGOT_PASSWORD+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_FORGOT_PASSWORD+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_FORGOT_PASSWORD+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_FORGOT_PASSWORD+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
					
	
					
					/*case N_NEW_SALARY_APPROVED:
						setStrTextEmployeeSalaryApproved(rst.getString("text_notification"));
						setStrSubjectEmployeeSalaryApproved(rst.getString("email_subject"));
						setStrBodyEmployeeSalaryApproved(rst.getString("email_notification"));
						setIsEmailEmployeeSalaryApproved(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextEmployeeSalaryApproved(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_NEW_SALARY_APPROVED,
								"<span id=\"myDivE"+N_NEW_SALARY_APPROVED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_NEW_SALARY_APPROVED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_NEW_SALARY_APPROVED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_NEW_SALARY_APPROVED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;*/
						
						
						
					/*case N_NEW_PAYSLIP_GENERATED:
						setStrTextEmployeePayslipGenerated(rst.getString("text_notification"));
						setStrSubjectEmployeePayslipGenerated(rst.getString("email_subject"));
						setStrBodyEmployeePayslipGenerated(rst.getString("email_notification"));
						setIsEmailEmployeePayslipGenerated(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextEmployeePayslipGenerated(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_NEW_PAYSLIP_GENERATED,
								"<span id=\"myDivE"+N_NEW_PAYSLIP_GENERATED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_NEW_PAYSLIP_GENERATED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_NEW_PAYSLIP_GENERATED+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_NEW_PAYSLIP_GENERATED+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;*/
							
						
					case N_NEW_SALARY_PAID:
						setStrTextSalaryReleased(rst.getString("text_notification"));
						setStrSubjectSalaryReleased(rst.getString("email_subject"));
						setStrBodySalaryReleased(rst.getString("email_notification"));
						setIsEmailSalaryReleased(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextSalaryReleased(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_NEW_SALARY_PAID,
								"<span id=\"myDivE"+N_NEW_SALARY_PAID+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_NEW_SALARY_PAID+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_NEW_SALARY_PAID+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_NEW_SALARY_PAID+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						
						
					case N_NEW_NOTICE:
						setStrTextNewAnnouncement(rst.getString("text_notification"));
						setStrSubjectNewAnnouncement(rst.getString("email_subject"));
						setStrBodyNewAnnouncement(rst.getString("email_notification"));
						setIsEmailNewAnnouncement(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewAnnouncement(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_NEW_NOTICE,
								"<span id=\"myDivE"+N_NEW_NOTICE+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_NEW_NOTICE+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_NEW_NOTICE+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_NEW_NOTICE+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
					
						
					/*case N_NEW_ROSTER:
						setStrTextNewRoster(rst.getString("text_notification"));
						setStrSubjectNewRoster(rst.getString("email_subject"));
						setStrBodyNewRoster(rst.getString("email_notification"));
						setIsEmailNewRoster(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewRoster(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_NEW_ROSTER,
								"<span id=\"myDivE"+N_NEW_ROSTER+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_NEW_ROSTER+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_NEW_ROSTER+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_NEW_ROSTER+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;*/
						
						
					/*case N_CHANGE_ROSTER:
						setStrTextRosterChanged(rst.getString("text_notification"));
						setStrSubjectRosterChanged(rst.getString("email_subject"));
						setStrBodyRosterChanged(rst.getString("email_notification"));
						setIsEmailRosterChanged(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextRosterChanged(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_CHANGE_ROSTER,
								"<span id=\"myDivE"+N_CHANGE_ROSTER+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_CHANGE_ROSTER+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_CHANGE_ROSTER+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_CHANGE_ROSTER+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;*/
						
						
					/*case N_NEW_MAIL:
						setStrTextNewMail(rst.getString("text_notification"));
						setStrSubjectNewMail(rst.getString("email_subject"));
						setStrBodyNewMail(rst.getString("email_notification"));
						setIsEmailNewMail(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewMail(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_NEW_MAIL,
								"<span id=\"myDivE"+N_NEW_MAIL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_NEW_MAIL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_NEW_MAIL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_NEW_MAIL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;*/
						
						
					case N_NEW_EMPLOYEE_JOINING:
						setStrTextNewEmployeeJoining(rst.getString("text_notification"));
						setStrSubjectNewEmployeeJoining(rst.getString("email_subject"));
						setStrBodyNewEmployeeJoining(rst.getString("email_notification"));
						setIsEmailNewEmployeeJoining(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewEmployeeJoining(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_NEW_EMPLOYEE_JOINING,
								"<span id=\"myDivE"+N_NEW_EMPLOYEE_JOINING+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_NEW_EMPLOYEE_JOINING+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_NEW_EMPLOYEE_JOINING+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_NEW_EMPLOYEE_JOINING+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;	
						
					case N_NEW_ACTIVITY: 
						setStrTextNewActivity(rst.getString("text_notification"));
						setStrSubjectNewActivity(rst.getString("email_subject"));
						setStrBodyNewActivity(rst.getString("email_notification"));
						setIsEmailNewActivity(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextNewActivity(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_NEW_ACTIVITY,
								"<span id=\"myDivE"+N_NEW_ACTIVITY+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_NEW_ACTIVITY+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_NEW_ACTIVITY+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_NEW_ACTIVITY+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						
					case N_EMPLOYEE_RESIGNATION_REQUEST: 
						setStrTextEmployeeResignationRequest(rst.getString("text_notification"));
						setStrSubjectEmployeeResignationRequest(rst.getString("email_subject"));
						setStrBodyEmployeeResignationRequest(rst.getString("email_notification"));
						setIsEmailEmployeeResignationRequest(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextEmployeeResignationRequest(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_RESIGNATION_REQUEST,
								"<span id=\"myDivE"+N_EMPLOYEE_RESIGNATION_REQUEST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_RESIGNATION_REQUEST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_EMPLOYEE_RESIGNATION_REQUEST+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_RESIGNATION_REQUEST+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						
					case N_EMPLOYEE_RESIGNATION_APPROVAL: 
						setStrTextEmployeeResignationApproval(rst.getString("text_notification"));
						setStrSubjectEmployeeResignationApproval(rst.getString("email_subject"));
						setStrBodyEmployeeResignationApproval(rst.getString("email_notification"));
						setIsEmailEmployeeResignationApproval(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextEmployeeResignationApproval(uF.parseToBoolean(rst.getString("istext")));
						
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_RESIGNATION_APPROVAL,
								"<span id=\"myDivE"+N_EMPLOYEE_RESIGNATION_APPROVAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_RESIGNATION_APPROVAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_EMPLOYEE_RESIGNATION_APPROVAL+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_RESIGNATION_APPROVAL+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						
					case N_EMPLOYEE_BIRTHDAY: 
						setStrTextBirthday(rst.getString("text_notification"));
						setStrSubjectBirthday(rst.getString("email_subject"));
						setStrBodyBirthday(rst.getString("email_notification"));
						setIsEmailBirthday(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextBirthday(uF.parseToBoolean(rst.getString("istext")));
						setStrBackgroundBirthdayImageFileName(rst.getString("background_image"));
						
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_BIRTHDAY,
								"<span id=\"myDivE"+N_EMPLOYEE_BIRTHDAY+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_BIRTHDAY+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_EMPLOYEE_BIRTHDAY+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_BIRTHDAY+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						
					case N_EMPLOYEE_MARRIAGE_ANNIVERSARY: 
						setStrTextMarriageAnniversary(rst.getString("text_notification"));
						setStrSubjectMarriageAnniversary(rst.getString("email_subject"));
						setStrBodyMarriageAnniversary(rst.getString("email_notification"));
						setIsEmailMarriageAnniversary(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextMarriageAnniversary(uF.parseToBoolean(rst.getString("istext")));
						setStrBackgroundMarriageImageFileName(rst.getString("background_image"));
						
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_MARRIAGE_ANNIVERSARY,
								"<span id=\"myDivE"+N_EMPLOYEE_MARRIAGE_ANNIVERSARY+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_MARRIAGE_ANNIVERSARY+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_EMPLOYEE_MARRIAGE_ANNIVERSARY+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_MARRIAGE_ANNIVERSARY+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						
					case N_EMPLOYEE_WORK_ANNIVERSARY: 

						setStrTextWorkAnniversary(rst.getString("text_notification"));
						setStrSubjectWorkAnniversary(rst.getString("email_subject"));
						setStrBodyWorkAnniversary(rst.getString("email_notification"));
						setIsEmailWorkAnniversary(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextWorkAnniversary(uF.parseToBoolean(rst.getString("istext")));
						setStrBackgroundWorkImageFileName(rst.getString("background_image"));

						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_WORK_ANNIVERSARY,
								"<span id=\"myDivE"+N_EMPLOYEE_WORK_ANNIVERSARY+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_WORK_ANNIVERSARY+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_EMPLOYEE_WORK_ANNIVERSARY+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_WORK_ANNIVERSARY+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						
					case N_ORG_ANNOUNCEMENT:
						setStrSubjectOnAnnouncementCreation(rst.getString("email_subject"));
						setStrBodyOnAnnouncementCreation(rst.getString("email_notification"));
						setEmailOnAnnouncementCreation(uF.parseToBoolean(rst.getString("isemail")));
						hmNotificationStatus.put("NOT_"+N_ORG_ANNOUNCEMENT,
								"<span id=\"myDivE"+N_ORG_ANNOUNCEMENT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_ORG_ANNOUNCEMENT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_ORG_ANNOUNCEMENT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_ORG_ANNOUNCEMENT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
					
					case N_ORG_EVENT:
						setStrSubjectOnEventCreation(rst.getString("email_subject"));
						setStrBodyOnEventCreation(rst.getString("email_notification"));
						setEmailOnEventCreation(uF.parseToBoolean(rst.getString("isemail")));
						hmNotificationStatus.put("NOT_"+N_ORG_EVENT,
								"<span id=\"myDivE"+N_ORG_EVENT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_ORG_EVENT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_ORG_EVENT+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_ORG_EVENT+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
				
				//===start parvez date: 15-02-2023===			
					case N_ORG_CIRCULAR_PUBLISH:
						setStrSubjectOnCircularCreation(rst.getString("email_subject"));
						setStrBodyOnCircularCreation(rst.getString("email_notification"));
						setIsEmailOnCircularCreation(uF.parseToBoolean(rst.getString("isemail")));
						hmNotificationStatus.put("NOT_"+N_ORG_CIRCULAR_PUBLISH,
								"<span id=\"myDivE"+N_ORG_CIRCULAR_PUBLISH+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_ORG_CIRCULAR_PUBLISH+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_ORG_CIRCULAR_PUBLISH+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_ORG_CIRCULAR_PUBLISH+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;	
				//===end parvez date: 15-02-2023===
						
					case N_RESUME_SUBMISSION:
						setStrSubjectResumeSubmission(rst.getString("email_subject"));
						setStrBodyResumeSubmission(rst.getString("email_notification"));
						setIsEmailResumeSubmission(uF.parseToBoolean(rst.getString("isemail")));
						setTextResumeSubmission(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_RESUME_SUBMISSION,
								"<span id=\"myDivE"+N_RESUME_SUBMISSION+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_RESUME_SUBMISSION+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_RESUME_SUBMISSION+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_RESUME_SUBMISSION+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
			
					case N_APPLICATION_SUBMISSION_TO_HIRING_TEAM:
						setStrSubjectApplicationSubmissionToHT(rst.getString("email_subject"));
						setStrBodyApplicationSubmissionToHT(rst.getString("email_notification"));
						setIsEmailApplicationSubmissionToHT(uF.parseToBoolean(rst.getString("isemail")));
						setTextApplicationSubmissionToHT(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_APPLICATION_SUBMISSION_TO_HIRING_TEAM,
								"<span id=\"myDivE"+N_APPLICATION_SUBMISSION_TO_HIRING_TEAM+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_APPLICATION_SUBMISSION_TO_HIRING_TEAM+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_APPLICATION_SUBMISSION_TO_HIRING_TEAM+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_APPLICATION_SUBMISSION_TO_HIRING_TEAM+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						
					case N_SELECTED_ROUND:
						setStrSubjectSelectedRound(rst.getString("email_subject"));
						setStrBodySelectedRound(rst.getString("email_notification"));
						setIsEmailSelectedRound(uF.parseToBoolean(rst.getString("isemail")));
						setTextSelectedRound(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_SELECTED_ROUND,
								"<span id=\"myDivE"+N_SELECTED_ROUND+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_SELECTED_ROUND+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_SELECTED_ROUND+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_SELECTED_ROUND+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						
					case N_REJECTED_ROUND:
						setStrSubjectRejectedRound(rst.getString("email_subject"));
						setStrBodyRejectedRound(rst.getString("email_notification"));
						setIsEmailRejectedRound(uF.parseToBoolean(rst.getString("isemail")));
						setTextRejectedRound(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_REJECTED_ROUND,
								"<span id=\"myDivE"+N_REJECTED_ROUND+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_REJECTED_ROUND+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_REJECTED_ROUND+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_REJECTED_ROUND+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						// End Dattatray Date:11-08-21
						// Start Dattatray Date:12-08-21
					case N_FRESHER_JOB_SUBMISSION:
						setStrSubjectFresherResumeSubmission(rst.getString("email_subject"));
						setStrBodyFresherResumeSubmission(rst.getString("email_notification"));
						setIsEmailFresherResumeSubmission(uF.parseToBoolean(rst.getString("isemail")));
						setIsTextFresherResumeSubmission(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_FRESHER_JOB_SUBMISSION,
								"<span id=\"myDivE"+N_FRESHER_JOB_SUBMISSION+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_FRESHER_JOB_SUBMISSION+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_FRESHER_JOB_SUBMISSION+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_FRESHER_JOB_SUBMISSION+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
						// End Dattatray Date:12-08-21
						
			//===start parvez date: 26-08-2022===
					case N_EMPLOYEE_ONBOARDED_BY_SELF:
						setStrSubjectEmpOnboardedBySelf(rst.getString("email_subject"));
						setStrBodyEmpOnboardedBySelf(rst.getString("email_notification"));
						setIsEmailEmpOnboardedBySelf(uF.parseToBoolean(rst.getString("isemail")));
						setTextEmpOnboardedBySelf(uF.parseToBoolean(rst.getString("istext")));
						hmNotificationStatus.put("NOT_"+N_EMPLOYEE_ONBOARDED_BY_SELF,
								"<span id=\"myDivE"+N_EMPLOYEE_ONBOARDED_BY_SELF+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("isemail")))?"mail_enbl.png":"mail_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("isemail")))?"Disable":"Enable")+" Mail Notification\" onclick=\"getContent('myDivE"+N_EMPLOYEE_ONBOARDED_BY_SELF+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("isemail")))?0:1)+"&M=EMAIL&T=NOT&RID="+rst.getString("notification_id")+"')\" /></span>"
								+"<span id=\"myDivT"+N_EMPLOYEE_ONBOARDED_BY_SELF+"\" style=\"float:right\"><img width=\"20px\" src=\"images1/"+((uF.parseToBoolean(rst.getString("istext")))?"mob_enbl.png":"mob_disbl.png")+"\" title=\""+((uF.parseToBoolean(rst.getString("istext")))?"Disable":"Enable")+" Text Notification\" onclick=\"getContent('myDivT"+N_EMPLOYEE_ONBOARDED_BY_SELF+"','UpdateRequest.action?S="+((uF.parseToBoolean(rst.getString("istext")))?0:1)+"&M=TEXT&T=NOT&RID="+rst.getString("notification_id")+"')\" />&nbsp;</span>"
								);
						break;
		  //===end parvez date: 26-08-2022===
				}
				
				setStrNotificationCode(nCode+"");
				request.setAttribute("UPDATED_NAME", hmEmpName.get(rst.getString("user_id")));
				request.setAttribute("UPDATED_DATE", uF.getDateFormat(rst.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmNotificationStatus", hmNotificationStatus);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	public String updateNotificationSettings() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			String strFileName = null;
			
			Map<String, String> hmNotiBGImage = new HashMap<String, String>();
			pst = con.prepareStatement("select notification_code,background_image from notifications");
			rs = pst.executeQuery();
			while(rs.next()) {
				hmNotiBGImage.put(rs.getString("notification_code"), rs.getString("background_image"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("NS/2283--StrBodyOnCircularCreation=="+getStrBodyOnCircularCreation());
//			System.out.println("NS/2284--StrSubjectOnCircularCreation=="+getStrSubjectOnCircularCreation());
//			System.out.println("NS/2285--IsEmailOnCircularCreation=="+getIsEmailOnCircularCreation());
			
			pst = con.prepareStatement("update notifications set email_notification=?, email_subject=?, isemail=?, background_image=? where notification_code=?");
			
			String strBirthdayFileName = null;
			if(getStrBackgroundBirthdayImageFileName() != null && !getStrBackgroundBirthdayImageFileName().equals("")){
				//System.out.println("getStrBackgroundImage ===>> " + getStrBackgroundBirthdayImage());
				if(getStrBackgroundBirthdayImage() != null) {
					if (CF.getStrDocSaveLocation() == null) {
						strBirthdayFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getStrBackgroundBirthdayImage(), getStrBackgroundBirthdayImageFileName(), getStrBackgroundBirthdayImageFileName(), CF);
					} else {
						strBirthdayFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation(), getStrBackgroundBirthdayImage(), getStrBackgroundBirthdayImageFileName(), getStrBackgroundBirthdayImageFileName(), CF);
					}
				}
			//	System.out.println("strFileName::"+strBirthdayFileName);
			}
			if((strBirthdayFileName ==null || strBirthdayFileName.equals("")) && hmNotiBGImage.get(""+N_EMPLOYEE_BIRTHDAY) !=null) {
				strBirthdayFileName = hmNotiBGImage.get(""+N_EMPLOYEE_BIRTHDAY);
			}
			pst.setString(1, getStrBodyBirthday());
			pst.setString(2, getStrSubjectBirthday());
//				pst.setString(3, getStrTextBirthday());
			pst.setBoolean(3, getIsEmailBirthday());
//				pst.setBoolean(5, getIsTextBirthday());
			pst.setString(4,strBirthdayFileName);
			pst.setInt(5, N_EMPLOYEE_BIRTHDAY);
			pst.addBatch();
			
			
			String strMarraigeFileName = null;
			if(getStrBackgroundMarriageImageFileName() != null && !getStrBackgroundMarriageImageFileName().equals("")){
				//System.out.println("getStrBackgroundImage ===>> " + getStrBackgroundBirthdayImage());
				if(getStrBackgroundMarriageImage() != null) {
					if (CF.getStrDocSaveLocation() == null) {
						strMarraigeFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getStrBackgroundMarriageImage(), getStrBackgroundMarriageImageFileName(), getStrBackgroundMarriageImageFileName(), CF);
					} else {
						strMarraigeFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation(), getStrBackgroundMarriageImage(), getStrBackgroundMarriageImageFileName(), getStrBackgroundMarriageImageFileName(), CF);
					}
				}
				//System.out.println("strFileName::"+strMarraigeFileName);
			} 
			if((strMarraigeFileName ==null || strMarraigeFileName.equals("")) && hmNotiBGImage.get(""+N_EMPLOYEE_MARRIAGE_ANNIVERSARY) !=null) {
				strMarraigeFileName = hmNotiBGImage.get(""+N_EMPLOYEE_MARRIAGE_ANNIVERSARY);
			}
			
			pst.setString(1, getStrBodyMarriageAnniversary());
			pst.setString(2, getStrSubjectMarriageAnniversary());
//				pst.setString(3, getStrTextMarriageAnniversary());
			pst.setBoolean(3, getIsEmailMarriageAnniversary());
//				pst.setBoolean(5, getIsTextMarriageAnniversary());
			pst.setString(4,strMarraigeFileName);
			pst.setInt(5, N_EMPLOYEE_MARRIAGE_ANNIVERSARY);
			pst.addBatch();
			

			String strWorkAnniversaryFileName = null;
			if(getStrBackgroundWorkImageFileName() != null && !getStrBackgroundWorkImageFileName().equals("")){
				//System.out.println("getStrBackgroundImage ===>> " + getStrBackgroundWorkImageFileName());
				if(getStrBackgroundWorkImage() != null) {
					if (CF.getStrDocSaveLocation() == null) {
						strWorkAnniversaryFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getStrBackgroundWorkImage(),getStrBackgroundWorkImageFileName(), getStrBackgroundWorkImageFileName(), CF);
					} else {
						strWorkAnniversaryFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation(), getStrBackgroundWorkImage(), getStrBackgroundWorkImageFileName(), getStrBackgroundWorkImageFileName(), CF);
					}
				}
				//System.out.println("strFileName::"+strWorkAnniversaryFileName);
			} 
			if((strWorkAnniversaryFileName==null || strWorkAnniversaryFileName.equals("")) && hmNotiBGImage.get(""+N_EMPLOYEE_WORK_ANNIVERSARY) !=null) {
				strWorkAnniversaryFileName = hmNotiBGImage.get(""+N_EMPLOYEE_WORK_ANNIVERSARY);
			}
			pst.setString(1, getStrBodyWorkAnniversary());
			pst.setString(2, getStrSubjectWorkAnniversary());
//				pst.setString(3, getStrTextWorkAnniversary());
			pst.setBoolean(3, getIsEmailWorkAnniversary());
//				pst.setBoolean(5, getIsTextWorkAnniversary());
			pst.setString(4,strWorkAnniversaryFileName);
			pst.setInt(5, N_EMPLOYEE_WORK_ANNIVERSARY);
			pst.addBatch();
			
			
//			----------------------- Project Management ---------------------
			
//			----------------------- Customer ---------------------
			pst.setString(1, getStrBodyNewCustAdded());
			pst.setString(2, getStrSubjectNewCustAdded());
//			pst.setString(3, getStrTextNewCustAdded());
			pst.setBoolean(3, getIsEmailNewCustAdded());
			pst.setString(4, strFileName);
//			pst.setBoolean(5, getIsTextNewCustAdded());
			pst.setInt(5, N_NEW_CLIENT);
			pst.addBatch();
			
			pst.setString(1, getStrBodyNewCustContactAdded());
			pst.setString(2, getStrSubjectNewCustContactAdded());
//			pst.setString(3, getStrTextNewCustContactAdded());
			pst.setBoolean(3, getIsEmailNewCustContactAdded());
//			pst.setBoolean(5, getIsTextNewCustContactAdded());
			pst.setString(4, strFileName);
			pst.setInt(5, N_NEW_CLIENT_CONTACT);
			pst.addBatch();
			
//			----------------------- Project ---------------------
			pst.setString(1, getStrBodyNewProWelcomeToCust());
			pst.setString(2, getStrSubjectNewProWelcomeToCust());
//			pst.setString(3, getStrTextNewProWelcomeToCust());
			pst.setBoolean(3, getIsEmailNewProWelcomeToCust());
//			pst.setBoolean(5, getIsTextNewProWelcomeToCust());
			pst.setString(4, strFileName);
			pst.setInt(5, N_CREATE_NEW_PROJECT);
			pst.addBatch();
			
			pst.setString(1, getStrBodyProUpdated());
			pst.setString(2, getStrSubjectProUpdated());
//			pst.setString(3, getStrTextProUpdated());
			pst.setBoolean(3, getIsEmailProUpdated());
//			pst.setBoolean(5, getIsTextProUpdated());
			pst.setString(4, strFileName);
			pst.setInt(5, N_UPDATE_PROJECT);
			pst.addBatch();
			
			pst.setString(1, getStrBodyNewTask());
			pst.setString(2, getStrSubjectNewTask());
//			pst.setString(3, getStrTextNewTask());
			pst.setBoolean(3, getIsEmailNewTask());
//			pst.setBoolean(5, getIsTextNewTask());
			pst.setString(4, strFileName);
			pst.setInt(5, N_NEW_TASK_ASSIGN);
			pst.addBatch();
			
			pst.setString(1, getStrBodyNewDocShared());
			pst.setString(2, getStrSubjectNewDocShared());
//			pst.setString(3, getStrTextNewDocShared());
			pst.setBoolean(3, getIsEmailNewDocShared());
//			pst.setBoolean(5, getIsTextNewDocShared());
			pst.setString(4, strFileName);
			pst.setInt(5, N_NEW_DOCUMENT_SHARED);
			pst.addBatch();
			
			pst.setString(1, getStrBodyTaskCompleted());
			pst.setString(2, getStrSubjectTaskCompleted());
//			pst.setString(3, getStrTextTaskCompleted());
			pst.setBoolean(3, getIsEmailTaskCompleted());
//			pst.setBoolean(5, getIsTextTaskCompleted());
			pst.setString(4, strFileName);
			pst.setInt(5, N_TASK_COMPLETED);
			pst.addBatch();
			
			pst.setString(1, getStrBodyProCompleted());
			pst.setString(2, getStrSubjectProCompleted());
//			pst.setString(3, getStrTextProCompleted());
			pst.setBoolean(3, getIsEmailProCompleted());
//			pst.setBoolean(5, getIsTextProCompleted());
			pst.setString(4, strFileName);
			pst.setInt(5, N_PROJECT_COMPLETED);
			pst.addBatch();
			
			pst.setString(1, getStrBodyProBlocked());
			pst.setString(2, getStrSubjectProBlocked());
//			pst.setString(3, getStrTextProBlocked());
			pst.setBoolean(3, getIsEmailProBlocked());
//			pst.setBoolean(5, getIsTextProBlocked());
			pst.setString(4, strFileName);
			pst.setInt(5, N_PROJECT_BLOCKED);
			pst.addBatch();
			
			pst.setString(1, getStrBodyMilestoneCompleted());
			pst.setString(2, getStrSubjectMilestoneCompleted());
//			pst.setString(3, getStrTextMilestoneCompleted());
			pst.setBoolean(3, getIsEmailMilestoneCompleted());
//			pst.setBoolean(5, getIsTextMilestoneCompleted());
			pst.setString(4, strFileName);
			pst.setInt(5, N_MILESTONE_COMPLETED);
			pst.addBatch();
			
			pst.setString(1, getStrBodyProReopened());
			pst.setString(2, getStrSubjectProReopened());
//			pst.setString(3, getStrTextProReopened());
			pst.setBoolean(3, getIsEmailProReopened());
//			pst.setBoolean(5, getIsTextProReopened());
			pst.setString(4, strFileName);
			pst.setInt(5, N_PROJECT_RE_OPENED);
			pst.addBatch();
			
//			----------------------- Timesheet --------------------------
			pst.setString(1, getStrBodyTimesheetSubmitted());
			pst.setString(2, getStrSubjectTimesheetSubmitted());
//			pst.setString(3, getStrTextTimesheetSubmitted());
			pst.setBoolean(3, getIsEmailTimesheetSubmitted());
//			pst.setBoolean(5, getIsTextTimesheetSubmitted());
			pst.setString(4, strFileName);
			pst.setInt(5, N_TIMESHEET_SUBMITED);
			pst.addBatch();
			
			pst.setString(1, getStrBodyTimesheetReopened());
			pst.setString(2, getStrSubjectTimesheetReopened());
//			pst.setString(3, getStrTextTimesheetReopened());
			pst.setBoolean(3, getIsEmailTimesheetReopened());
//			pst.setBoolean(5, getIsTextTimesheetReopened());
			pst.setString(4, strFileName);
			pst.setInt(5, N_TIMESHEET_RE_OPENED);
			pst.addBatch();
			
			pst.setString(1, getStrBodyTimesheetApproved());
			pst.setString(2, getStrSubjectTimesheetApproved());
//			pst.setString(3, getStrTextTimesheetApproved());
			pst.setBoolean(3, getIsEmailTimesheetApproved());
//			pst.setBoolean(5, getIsTextTimesheetApproved());
			pst.setString(4, strFileName);
			pst.setInt(5, N_TIMESHEET_APPROVED);
			pst.addBatch();
			
			pst.setString(1, getStrBodyTimesheetSubmitToCust());
			pst.setString(2, getStrSubjectTimesheetSubmitToCust());
//			pst.setString(3, getStrTextTimesheetSubmitToCust());
			pst.setBoolean(3, getIsEmailTimesheetSubmitToCust());
//			pst.setBoolean(5, getIsTextTimesheetSubmitToCust());
			pst.setString(4, strFileName);
			pst.setInt(5, N_TIMESHEET_SUBMITED_TO_CUST);
			pst.addBatch();
			
//			------------------ Billing -----------------------
			pst.setString(1, getStrBodyMilestoneBilling());
			pst.setString(2, getStrSubjectMilestoneBilling());
//			pst.setString(3, getStrTextMilestoneBilling());
			pst.setBoolean(3, getIsEmailMilestoneBilling());
//			pst.setBoolean(5, getIsTextMilestoneBilling());
			
			pst.setString(4, strFileName);
			pst.setInt(5, N_MILESTONE_BILLING);
			pst.addBatch();
			
			pst.setString(1, getStrBodyRecurringBilling());
			pst.setString(2, getStrSubjectRecurringBilling());
//			pst.setString(3, getStrTextRecurringBilling());
			pst.setBoolean(3, getIsEmailRecurringBilling());
//			pst.setBoolean(5, getIsTextRecurringBilling());
			pst.setString(4, strFileName);
			pst.setInt(5, N_RECURRING_BILLING);
			pst.addBatch();
			
//			------------------- Payment ----------------------------
			pst.setString(1, getStrBodyPaymentAlert());
			pst.setString(2, getStrSubjectPaymentAlert());
//			pst.setString(3, getStrTextPaymentAlert());
			pst.setBoolean(3, getIsEmailPaymentAlert());
//			pst.setBoolean(5, getIsTextPaymentAlert());
			pst.setString(4, strFileName);
			pst.setInt(5, N_PAYMENT_ALERT);
			pst.addBatch();
			
			pst.setString(1, getStrBodyPaymentReminder());
			pst.setString(2, getStrSubjectPaymentReminder());
//			pst.setString(3, getStrTextPaymentReminder());
			pst.setBoolean(3, getIsEmailPaymentReminder());
//			pst.setBoolean(5, getIsTextPaymentReminder());
			pst.setString(4, strFileName);
			pst.setInt(5, N_PAYMENT_REMINDER);
			pst.addBatch();
			
//			----------------------- Recruitment ---------------------
			
			pst.setString(1, getStrBodyCandiAssessmentForm());
			pst.setString(2, getStrSubjectCandiAssessmentForm());
//			pst.setString(3, getStrTextCandiAssessmentForm());
			pst.setBoolean(3, getIsEmailCandiAssessmentForm());
//			pst.setBoolean(5, getIsTextCandiAssessmentForm());
			pst.setString(4, strFileName);
			pst.setInt(5, N_CANDI_TAKE_ASSESSMENT);
			pst.addBatch();
			
			pst.setString(1, getStrBodyCandiFinalize());
			pst.setString(2, getStrSubjectCandiFinalize());
//			pst.setString(3, getStrTextCandiFinalize());
			pst.setBoolean(3, getIsEmailCandiFinalize());
//			pst.setBoolean(5, getIsTextCandiFinalize());
			pst.setString(4, strFileName);
			pst.setInt(5, N_CANDI_FINALIZATION_FROM_EMP);
			pst.addBatch();
			
			pst.setString(1, getStrBodyCandiShortlistFromConsultant());
			pst.setString(2, getStrSubjectCandiShortlistFromConsultant());
//			pst.setString(3, getStrTextCandiShortlistFromConsultant());
			pst.setBoolean(3, getIsEmailCandiShortlistFromConsultant());
//			pst.setBoolean(5, getIsTextCandiShortlistFromConsultant());
			pst.setString(4, strFileName);
			pst.setInt(5, N_CANDI_SHORTLIST_FROM_CONSULTANT);
			pst.addBatch();
			
			pst.setString(1, getStrBodyNewJobToConsultant());
			pst.setString(2, getStrSubjectNewJobToConsultant());
//			pst.setString(3, getStrTextNewJobToConsultant());
			pst.setBoolean(3, getIsEmailNewJobToConsultant());
//			pst.setBoolean(5, getIsTextNewJobToConsultant());
			pst.setString(4, strFileName);
			pst.setInt(5, N_HIRING_LINK_FOR_CONSULTANT);
			pst.addBatch();
			
			pst.setString(1, getStrBodyJobProfileUpdate());
			pst.setString(2, getStrSubjectJobProfileUpdate());
//			pst.setString(3, getStrTextJobProfileUpdate());
			pst.setBoolean(3, getIsEmailJobProfileUpdate());
//			pst.setBoolean(5, getIsTextJobProfileUpdate());
			pst.setString(4, strFileName);
			pst.setInt(5, N_JOB_PROFILE_UPDATE);
			pst.addBatch();
			
			pst.setString(1, getStrBodyJobProfileDeny());
			pst.setString(2, getStrSubjectJobProfileDeny());
//			pst.setString(3, getStrTextJobProfileDeny());
			pst.setBoolean(3, getIsEmailJobProfileDeny());
//			pst.setBoolean(5, getIsTextJobProfileDeny());
			pst.setString(4, strFileName);
			pst.setInt(5, N_JOB_PROFILE_DENY);
			pst.addBatch();
			
			pst.setString(1, getStrBodyJobProfileApproval());
			pst.setString(2, getStrSubjectJobProfileApproval());
//			pst.setString(3, getStrTextJobProfileApproval());
			pst.setBoolean(3, getIsEmailJobProfileApproval());
//			pst.setBoolean(5, getIsTextJobProfileApproval());
			pst.setString(4, strFileName);
			pst.setInt(5, N_JOB_PROFILE_APPROVAL);
			pst.addBatch();
			
			pst.setString(1, getStrBodyRecruitmentRequestDeny());
			pst.setString(2, getStrSubjectRecruitmentRequestDeny());
//			pst.setString(3, getStrTextRecruitmentRequestDeny());
			pst.setBoolean(3, getIsEmailRecruitmentRequestDeny());
//			pst.setBoolean(5, getIsTextRecruitmentRequestDeny());
			pst.setString(4, strFileName);
			pst.setInt(5, N_RECRUITMENT_DENY);
			pst.addBatch();
			
			pst.setString(1, getStrBodyNewEmpOnboardingToHr());
			pst.setString(2, getStrSubjectNewEmpOnboardingToHr());
//			pst.setString(3, getStrTextNewEmpOnboardingToHr());
			pst.setBoolean(3, getIsEmailNewEmpOnboardingToHr());
//			pst.setBoolean(5, getIsTextNewEmpOnboardingToHr());
			pst.setString(4, strFileName);
			pst.setInt(5, N_EMP_ONBOARDING_TO_HR);
			pst.addBatch();
			
			pst.setString(1, getStrBodyCandiOfferAcceptReject());
			pst.setString(2, getStrSubjectCandiOfferAcceptReject());
//			pst.setString(3, getStrTextCandiOfferAcceptReject());
			pst.setBoolean(3, getIsEmailCandiOfferAcceptReject());
//			pst.setBoolean(5, getIsTextCandiOfferAcceptReject());
			pst.setString(4, strFileName);
			pst.setInt(5, N_CANDI_OFFER_ACCEPT_REJECT);
			pst.addBatch();
			
			pst.setString(1, getStrBodyRecruitmentRequestUpdate());
			pst.setString(2, getStrSubjectRecruitmentRequestUpdate());
//			pst.setString(3, getStrTextRecruitmentRequestUpdate());
			pst.setBoolean(3, getIsEmailRecruitmentRequestUpdate());
//			pst.setBoolean(5, getIsTextRecruitmentRequestUpdate());
			pst.setString(4, strFileName);
			pst.setInt(5, N_RECRUITMENT_REQUEST_EDIT);
			pst.addBatch();
			
			pst.setString(1, getStrBodyFillCandiInfoByCandi());
			pst.setString(2, getStrSubjectFillCandiInfoByCandi());
//			pst.setString(3, getStrTextFillCandiInfoByCandi());
			pst.setBoolean(3, getIsEmailFillCandiInfoByCandi());
//			pst.setBoolean(5, getIsTextFillCandiInfoByCandi());
			pst.setString(4, strFileName);
			pst.setInt(5, N_NEW_CADIDATE_ADD);
			pst.addBatch();
			
			pst.setString(1, getStrBodyCandiBackgroudVerification());
			pst.setString(2, getStrSubjectCandiBackgroudVerification());
//			pst.setString(3, getStrTextCandiBackgroudVerification());
			pst.setBoolean(3, getIsEmailCandiBackgroudVerification());
//			pst.setBoolean(5, getIsTextCandiBackgroudVerification());
			pst.setString(4, strFileName);
			pst.setInt(5, N_NEW_CADIDATE_BACKGROUND_VERIFICATION);
			pst.addBatch();
			
			pst.setString(1, getStrBodyCandiEmpLoginDetail());
			pst.setString(2, getStrSubjectCandiEmpLoginDetail());
//			pst.setString(3, getStrTextCandiEmpLoginDetail());
			pst.setBoolean(3, getIsEmailCandiEmpLoginDetail());
//			pst.setBoolean(5, getIsTextCandiEmpLoginDetail());
			pst.setString(4, strFileName);
			pst.setInt(5, N_EMP_LOGIN_DETAILS);
			pst.addBatch();
			
			pst.setString(1, getStrBodyCandiOnboarding());
			pst.setString(2, getStrSubjectCandiOnboarding());
//			pst.setString(3, getStrTextCandiOnboarding());
			pst.setBoolean(3, getIsEmailCandiOnboarding());
//			pst.setBoolean(5, getIsTextCandiOnboarding());
			pst.setString(4, strFileName);
			pst.setInt(5, N_CANDI_ONBOARDING_CTC);
			pst.addBatch();
			
			pst.setString(1, getStrBodyPanelistInterviewDate());
			pst.setString(2, getStrSubjectPanelistInterviewDate());
//			pst.setString(3, getStrTextPanelistInterviewDate());
			pst.setBoolean(3, getIsEmailPanelistInterviewDate());
//			pst.setBoolean(5, getIsTextPanelistInterviewDate());
			pst.setString(4, strFileName);
			pst.setInt(5, N_INTERVIEW_DATE_MAIL_FOR_ROUNDEMP);
			pst.addBatch();
			
			pst.setString(1, getStrBodyCandiInterviewDate());
			pst.setString(2, getStrSubjectCandiInterviewDate());
//			pst.setString(3, getStrTextCandiInterviewDate());
			pst.setBoolean(3, getIsEmailCandiInterviewDate());
//			pst.setBoolean(5, getIsTextCandiInterviewDate());
			pst.setString(4, strFileName);
			pst.setInt(5, N_INTERVIEW_DATE_MAIL_FOR_CANDI);
			pst.addBatch();
			
			pst.setString(1, getStrBodyCandiJoiningOfferCTC());
			pst.setString(2, getStrSubjectCandiJoiningOfferCTC());
//			pst.setString(3, getStrTextCandiJoiningOfferCTC());
			pst.setBoolean(3, getIsEmailCandiJoiningOfferCTC());
//			pst.setBoolean(5, getIsTextCandiJoiningOfferCTC());
			pst.setString(4, strFileName);
			pst.setInt(5, N_CANDI_JOINING_OFFER_CTC);
			pst.addBatch();
			
			pst.setString(1, getStrBodyCandiSpecifyOtherDate());
			pst.setString(2, getStrSubjectCandiSpecifyOtherDate());
//			pst.setString(3, getStrTextCandiSpecifyOtherDate());
			pst.setBoolean(3, getIsEmailCandiSpecifyOtherDate());
//			pst.setBoolean(5, getIsTextCandiSpecifyOtherDate());
			pst.setString(4, strFileName);
			pst.setInt(5, N_CANDI_SPECIFY_OTHER_DATE);
			pst.addBatch();
			
			pst.setString(1, getStrBodyApplicationShortlist());
			pst.setString(2, getStrSubjectApplicationShortlist());
//			pst.setString(3, getStrTextApplicationShortlist());
			pst.setBoolean(3, getIsEmailApplicationShortlist());
//			pst.setBoolean(5, getIsTextApplicationShortlist());
			pst.setString(4, strFileName);
			pst.setInt(5, N_APPLICATION_SHORTLIST);
			pst.addBatch();
			
			pst.setString(1, getStrBodyRecruitmentApproval());
			pst.setString(2, getStrSubjectRecruitmentApproval());
//			pst.setString(3, getStrTextRecruitmentApproval());
			pst.setBoolean(3, getIsEmailRecruitmentApproval());
//			pst.setBoolean(5, getIsTextRecruitmentApproval());
			pst.setString(4, strFileName);
			pst.setInt(5, N_RECRUITMENT_APPROVAL);
			pst.addBatch();
			
			pst.setString(1, getStrBodyNewRecruitmentToEmp());
			pst.setString(2, getStrSubjectNewRecruitmentToEmp());
//			pst.setString(3, getStrTextNewRecruitmentToEmp());
			pst.setBoolean(3, getIsEmailNewRecruitmentToEmp());
//			pst.setBoolean(5, getIsTextNewRecruitmentToEmp());
			pst.setString(4, strFileName);
			pst.setInt(5, N_RECRUITMENT_MAIL_TO_EMP);
			pst.addBatch();
			
			pst.setString(1, getStrBodyRecruitmentRequest());
			pst.setString(2, getStrSubjectRecruitmentRequest());
//			pst.setString(3, getStrTextRecruitmentRequest());
			pst.setBoolean(3, getIsEmailRecruitmentRequest());
//			pst.setBoolean(5, getIsTextRecruitmentRequest());
			pst.setString(4, strFileName);
			pst.setInt(5, N_RECRUITMENT_REQUEST);
			pst.addBatch();
			
//			Start Dattatray Date:11-08-21		
			pst.setString(1, getStrBodyResumeSubmission());
			pst.setString(2, getStrSubjectResumeSubmission());
			pst.setBoolean(3, getIsEmailResumeSubmission());
			System.out.println("getIsEmailResumeSubmission() : "+getIsEmailResumeSubmission());
			pst.setString(4, strFileName);
			pst.setInt(5, N_RESUME_SUBMISSION);
			pst.addBatch();
			
	//===start parvez date: 29-10-2021===		
			pst.setString(1, getStrBodyApplicationSubmissionToHT());
			pst.setString(2, getStrSubjectApplicationSubmissionToHT());
			pst.setBoolean(3, getIsEmailApplicationSubmissionToHT());
//			System.out.println("getIsEmailApplicationSubmissionToHT() : "+getIsEmailApplicationSubmissionToHT());
			pst.setString(4, strFileName);
			pst.setInt(5, N_APPLICATION_SUBMISSION_TO_HIRING_TEAM);
			pst.addBatch();
	//===end parvez date: 29-10-2021===
			
			pst.setString(1, getStrBodySelectedRound());
			pst.setString(2, getStrSubjectSelectedRound());
			pst.setBoolean(3, getIsEmailSelectedRound());
			pst.setString(4, strFileName);
			pst.setInt(5, N_SELECTED_ROUND);
			pst.addBatch();
			
			pst.setString(1, getStrBodyRejectedRound());
			pst.setString(2, getStrSubjectRejectedRound());
			pst.setBoolean(3, getIsEmailRejectedRound());
			pst.setString(4, strFileName);
			pst.setInt(5, N_REJECTED_ROUND);
			pst.addBatch();

			//Start Dattatray Date:12-08-21
			pst.setString(1, getStrBodyFresherResumeSubmission());
			pst.setString(2, getStrSubjectFresherResumeSubmission());
			pst.setBoolean(3, getIsEmailFresherResumeSubmission());
			pst.setString(4, strFileName);
			pst.setInt(5, N_FRESHER_JOB_SUBMISSION);
			pst.addBatch();
//			End Dattatray
			
//			----------------------- Learning ---------------------
			
			pst.setString(1, getStrBodyLTrainingFeedbackLearner());
			pst.setString(2, getStrSubjectLTrainingFeedbackLearner());
//			pst.setString(3, getStrTextLTrainingFeedbackLearner());
			pst.setBoolean(3, getIsEmailLTrainingFeedbackLearner());
//			pst.setBoolean(5, getIsTextLTrainingFeedbackLearner());
			pst.setString(4, strFileName);
			pst.setInt(5, N_L_TRAINING_FEEDBACK_FROM_TRAINER_EMP);
			pst.addBatch();
			
			pst.setString(1, getStrBodyLTrainingFeedbackTrainer());
			pst.setString(2, getStrSubjectLTrainingFeedbackTrainer());
//			pst.setString(3, getStrTextLTrainingFeedbackTrainer());
			pst.setBoolean(3, getIsEmailLTrainingFeedbackTrainer());
//			pst.setBoolean(5, getIsTextLTrainingFeedbackTrainer());
			pst.setString(4, strFileName);
			pst.setInt(5, N_L_TRAINING_FEEDBACK_FROM_TRAINER_TO_HR);
			pst.addBatch();
			
			pst.setString(1, getStrBodyLAssessFinalizeForHr());
			pst.setString(2, getStrSubjectLAssessFinalizeForHr());
//			pst.setString(3, getStrTextLAssessFinalizeForHr());
			pst.setBoolean(3, getIsEmailLAssessFinalizeForHr());
//			pst.setBoolean(5, getIsTextLAssessFinalizeForHr());
			pst.setString(4, strFileName);
			pst.setInt(5, N_LEARNING_ASSESS_FINALIZATION_TO_EMP);
			pst.addBatch();
			
			pst.setString(1, getStrBodyLTrainingFinalizeForHr());
			pst.setString(2, getStrSubjectLTrainingFinalizeForHr());
//			pst.setString(3, getStrTextLTrainingFinalizeForHr());
			pst.setBoolean(3, getIsEmailLTrainingFinalizeForHr());
//			pst.setBoolean(5, getIsTextLTrainingFinalizeForHr());
			pst.setString(4, strFileName);
			pst.setInt(5, N_LEARNING_TRAINING_FINALIZATION_TO_EMP);
			pst.addBatch();
			
			pst.setString(1, getStrBodyLearningPlanForHr());
			pst.setString(2, getStrSubjectLearningPlanForHr());
//			pst.setString(3, getStrTextLearningPlanForHr());
			pst.setBoolean(3, getIsEmailLearningPlanForHr());
//			pst.setBoolean(5, getIsTextLearningPlanForHr());
			pst.setString(4, strFileName);
			pst.setInt(5, N_NEW_LEARNING_PLAN_FOR_HR);
			pst.addBatch();
			
			
			
			
			pst.setString(1, getStrBodyLearningPlanForLearner());
			pst.setString(2, getStrSubjectLearningPlanForLearner());
//			pst.setString(3, getStrTextLearningPlanForLearner());
			pst.setBoolean(3, getIsEmailLearningPlanForLearner());
//			pst.setBoolean(5, getIsTextLearningPlanForLearner());
			pst.setString(4, strFileName);
			pst.setInt(5, N_NEW_LEARNING_PLAN_FOR_LEARNERS);
			pst.addBatch();
			
//			----------------------- Performance ---------------------
			
			pst.setString(1, getStrBodyExecutiveTarget());
			System.out.println("getStrBodyExecutiveTarget() : "+getStrBodyExecutiveTarget());
			pst.setString(2, getStrSubjectExecutiveTarget());
//			pst.setString(3, getStrTextExecutiveTarget());
			pst.setBoolean(3, getIsEmailExecutiveTarget());
//			pst.setBoolean(5, getIsTextExecutiveTarget());
			pst.setString(4, strFileName);
			pst.setInt(5, N_EXECUTIVE_TARGET);
			pst.addBatch();
			
			pst.setString(1, getStrBodyExecutiveKRA());
			pst.setString(2, getStrSubjectExecutiveKRA());
//			pst.setString(3, getStrTextExecutiveKRA());
			pst.setBoolean(3, getIsEmailExecutiveKRA());
//			pst.setBoolean(5, getIsTextExecutiveKRA());
			pst.setString(4, strFileName);
			pst.setInt(5, N_EXECUTIVE_KRA);
			pst.addBatch();
			
			pst.setString(1, getStrBodyExecutiveGoal());
			pst.setString(2, getStrSubjectExecutiveGoal());
//			pst.setString(3, getStrTextExecutiveGoal());
			pst.setBoolean(3, getIsEmailExecutiveGoal());
//			pst.setBoolean(5, getIsTextExecutiveGoal());
			pst.setString(4, strFileName);
			pst.setInt(5, N_EXECUTIVE_GOAL);
			pst.addBatch();
			
			pst.setString(1, getStrBodyManagerGoal());
			pst.setString(2, getStrSubjectManagerGoal());
//			pst.setString(3, getStrTextManagerGoal());
			pst.setBoolean(3, getIsEmailManagerGoal());
//			pst.setBoolean(5, getIsTextManagerGoal());
			pst.setString(4, strFileName);
			pst.setInt(5, N_MANAGER_GOAL);
			pst.addBatch();
			
			pst.setString(1, getStrBodyLearningGapForHr());
			pst.setString(2, getStrSubjectLearningGapForHr());
//			pst.setString(3, getStrTextLearningGapForHr());
			pst.setBoolean(3, getIsEmailLearningGapForHr());
//			pst.setBoolean(5, getIsTextLearningGapForHr());
			pst.setString(4, strFileName);
			pst.setInt(5, N_LEARNING_GAP_FOR_HR);
			pst.addBatch();
			
			pst.setString(1, getStrBodyReviewFinalizationForEmp());
			pst.setString(2, getStrSubjectReviewFinalizationForEmp());
//			pst.setString(3, getStrTextReviewFinalizationForEmp());
			pst.setBoolean(3, getIsEmailReviewFinalizationForEmp());
//			pst.setBoolean(5, getIsTextReviewFinalizationForEmp());
			pst.setString(4, strFileName);
			pst.setInt(5, N_REVIEW_FINALIZATION_FOR_EMP);
			pst.addBatch();
			
			/*pst.setString(1, getStrBodyReviewFinalizationForHr());
			pst.setString(2, getStrSubjectReviewFinalizationForHr());
			pst.setString(3, getStrTextReviewFinalizationForHr());
			pst.setBoolean(4, getIsEmailReviewFinalizationForHr());
			pst.setBoolean(5, getIsTextReviewFinalizationForHr());
			pst.setInt(6, N_REVIEW_FINALIZATION_FOR_HR);
			pst.addBatch();*/
			
			/*pst.setString(1, getStrBodyEmpReviewSubmit());
			pst.setString(2, getStrSubjectEmpReviewSubmit());
			pst.setString(3, getStrTextEmpReviewSubmit());
			pst.setBoolean(4, getIsEmailEmpReviewSubmit());
			pst.setBoolean(5, getIsTextEmpReviewSubmit());
			pst.setInt(6, N_EMP_REVIW_SUBMITED);
			pst.addBatch();*/
			
			pst.setString(1, getStrBodyNewReviewPublish());
			pst.setString(2, getStrSubjectNewReviewPublish());
//			pst.setString(3, getStrTextNewReviewPublish());
			pst.setBoolean(3, getIsEmailNewReviewPublish());
//			pst.setBoolean(5, getIsTextNewReviewPublish());
			pst.setString(4, strFileName);
			pst.setInt(5, N_NEW_REVIW_PUBLISH);
			pst.addBatch();
			
			pst.setString(1, getStrBodyPendingReviewReminder());
			pst.setString(2, getStrSubjectPendingReviewReminder());
//			pst.setString(3, getStrTextPendingReviewReminder());
			pst.setBoolean(3, getIsEmailPendingReviewReminder());
//			pst.setBoolean(5, getIsTextPendingReviewReminder());
			pst.setString(4, strFileName);
			pst.setInt(5, N_PENDING_REVIEW_REMINDER);
			pst.addBatch();
			
//			----------------------- Earth ---------------------
			String strNewEmpFileName = null;
			if(getStrBackgroundNewEmpImageFileName() != null && !getStrBackgroundNewEmpImageFileName().equals("")){
				//System.out.println("getStrBackgroundImage ===>> " + getStrBackgroundBirthdayImage());
				if(getStrBackgroundNewEmpImage() != null) {
					if (CF.getStrDocSaveLocation() == null) {
						strNewEmpFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getStrBackgroundNewEmpImage(), getStrBackgroundNewEmpImageFileName(),getStrBackgroundNewEmpImageFileName(), CF);
					} else {
						strNewEmpFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation(), getStrBackgroundNewEmpImage(), getStrBackgroundNewEmpImageFileName(), getStrBackgroundNewEmpImageFileName(), CF);
					}
				}
				//System.out.println("strFileName::"+strMarraigeFileName);
			} 
			if((strNewEmpFileName==null || strNewEmpFileName.equals("")) && hmNotiBGImage.get(""+N_NEW_EMPLOYEE) !=null) {
				strNewEmpFileName = hmNotiBGImage.get(""+N_NEW_EMPLOYEE);
			}
			pst.setString(1, getStrBodyNewEmployee());
			pst.setString(2, getStrSubjectNewEmployee());
//			pst.setString(3, getStrTextNewEmployee());
			pst.setBoolean(3, getIsEmailNewEmployee());
//			pst.setBoolean(5, getIsTextNewEmployee());
			pst.setString(4, strNewEmpFileName);
			pst.setInt(5, N_NEW_EMPLOYEE);
			pst.addBatch();
						
			pst.setString(1, getStrBodyNewEmployeeJoining());
			pst.setString(2, getStrSubjectNewEmployeeJoining());
//			pst.setString(3, getStrTextNewEmployeeJoining());
			pst.setBoolean(3, getIsEmailNewEmployeeJoining());
//			pst.setBoolean(5, getIsTextNewEmployeeJoining());
			pst.setString(4, strFileName);
			pst.setInt(5, N_NEW_EMPLOYEE_JOINING);
			pst.addBatch();
			
			/*pst.setString(1, getStrBodyNewEmployeeStatusChange());
			pst.setString(2, getStrSubjectNewEmployeeStatusChange());
			pst.setString(3, getStrTextNewEmployeeStatusChange());
			pst.setBoolean(4, getIsEmailNewEmployeeStatusChange());
			pst.setBoolean(5, getIsTextNewEmployeeStatusChange());
			pst.setInt(6, N_EMPLOYEE_STATUS_CHANGE);
			pst.addBatch();*/

			
			/*pst.setString(1, getStrBodyNewEmployeeLeaveRequest());
			pst.setString(2, getStrSubjectNewEmployeeLeaveRequest());
			pst.setString(3, getStrTextNewEmployeeLeaveRequest());
			pst.setBoolean(4, getIsEmailNewEmployeeLeaveRequest());
			pst.setBoolean(5, getIsTextNewEmployeeLeaveRequest());
			pst.setInt(6, N_EMPLOYEE_LEAVE_REQUEST);
			pst.addBatch();*/
			
			
			pst.setString(1, getStrBodyManagerNewLeaveRequest());
			pst.setString(2, getStrSubjectManagerNewLeaveRequest());
//			pst.setString(3, getStrTextManagerNewLeaveRequest());
			pst.setBoolean(3, getIsEmailManagerNewLeaveRequest());
//			pst.setBoolean(5, getIsTextManagerNewLeaveRequest());
			pst.setString(4, strFileName);
			pst.setInt(5, N_MANAGER_LEAVE_REQUEST);
			pst.addBatch();

			pst.setString(1, getStrBodyNewEmployeeLeaveApproval());
			pst.setString(2, getStrSubjectNewEmployeeLeaveApproval());
//			pst.setString(3, getStrTextNewEmployeeLeaveApproval());
			pst.setBoolean(3, getIsEmailNewEmployeeLeaveApproval());
//			pst.setBoolean(5, getIsTextNewEmployeeLeaveApproval());
			pst.setString(4, strFileName);
			pst.setInt(5, N_EMPLOYEE_LEAVE_APPROVAL);
			pst.addBatch();
			
			pst.setString(1, getStrBodyEmployeeLeaveCancel());
			pst.setString(2, getStrSubjectEmployeeLeaveCancel());
//			pst.setString(3, getStrTextEmployeeLeaveCancel());
			pst.setBoolean(3, getIsEmailEmployeeLeaveCancel());
//			pst.setBoolean(5, getIsTextEmployeeLeaveCancel());
			pst.setString(4, strFileName);
			pst.setInt(5, N_EMPLOYEE_LEAVE_CANCEL);
			pst.addBatch();
			
			pst.setString(1, getStrBodyEmployeeLeavePullout());
			pst.setString(2, getStrSubjectEmployeeLeavePullout());
//			pst.setString(3, getStrTextEmployeeLeavePullout());
			pst.setBoolean(3, getIsEmailEmployeeLeavePullout());
//			pst.setBoolean(5, getIsTextEmployeeLeavePullout());
			pst.setString(4, strFileName);
			pst.setInt(5, N_EMPLOYEE_LEAVE_PULLOUT);
			pst.addBatch();
			
			pst.setString(1, getStrBodyManagerNewExtraWorkRequest());
			pst.setString(2, getStrSubjectManagerNewExtraWorkRequest());
//			pst.setString(3, getStrTextManagerNewExtraWorkRequest());
			pst.setBoolean(3, getIsEmailManagerNewExtraWorkRequest());
//			pst.setBoolean(5, getIsTextManagerNewExtraWorkRequest());
			pst.setString(4, strFileName);
			pst.setInt(5, N_MANAGER_EXTRA_WORK_REQUEST);
			pst.addBatch();
			
			pst.setString(1, getStrBodyNewEmployeeExtraWorkApproval());
			pst.setString(2, getStrSubjectNewEmployeeExtraWorkApproval());
//			pst.setString(3, getStrTextNewEmployeeExtraWorkApproval());
			pst.setBoolean(3, getIsEmailNewEmployeeExtraWorkApproval());
//			pst.setBoolean(5, getIsTextNewEmployeeExtraWorkApproval());
			pst.setString(4, strFileName);
			pst.setInt(5, N_EMPLOYEE_EXTRA_WORK_APPROVAL);
			pst.addBatch();
			
			pst.setString(1, getStrBodyEmployeeExtraWorkCancel());
			pst.setString(2, getStrSubjectEmployeeExtraWorkCancel());
//			pst.setString(3, getStrTextEmployeeExtraWorkCancel());
			pst.setBoolean(3, getIsEmailEmployeeExtraWorkCancel());
//			pst.setBoolean(5, getIsTextEmployeeExtraWorkCancel());
			pst.setString(4, strFileName);
			pst.setInt(5, N_EMPLOYEE_EXTRA_WORK_CANCEL);
			pst.addBatch();
			
			pst.setString(1, getStrBodyEmployeeExtraWorkPullout());
			pst.setString(2, getStrSubjectEmployeeExtraWorkPullout());
//			pst.setString(3, getStrTextEmployeeExtraWorkPullout());
			pst.setBoolean(3, getIsEmailEmployeeExtraWorkPullout());
//			pst.setBoolean(5, getIsTextEmployeeExtraWorkPullout());
			pst.setString(4, strFileName);
			pst.setInt(5, N_EMPLOYEE_EXTRA_WORK_PULLOUT);
			pst.addBatch();
			
			pst.setString(1, getStrBodyManagerNewTravelRequest());
			pst.setString(2, getStrSubjectManagerNewTravelRequest());
//			pst.setString(3, getStrTextManagerNewTravelRequest());
			pst.setBoolean(3, getIsEmailManagerNewTravelRequest());
//			pst.setBoolean(5, getIsTextManagerNewTravelRequest());
			pst.setString(4, strFileName);
			pst.setInt(5, N_MANAGER_TRAVEL_REQUEST);
			pst.addBatch();
			
			pst.setString(1, getStrBodyNewEmployeeTravelApproval());
			pst.setString(2, getStrSubjectNewEmployeeTravelApproval());
//			pst.setString(3, getStrTextNewEmployeeTravelApproval());
			pst.setBoolean(3, getIsEmailNewEmployeeTravelApproval());
//			pst.setBoolean(5, getIsTextNewEmployeeTravelApproval());
			pst.setString(4, strFileName);
			pst.setInt(5, N_EMPLOYEE_TRAVEL_APPROVAL);
			pst.addBatch();
			
			pst.setString(1, getStrBodyEmployeeTravelCancel());
			pst.setString(2, getStrSubjectEmployeeTravelCancel());
//			pst.setString(3, getStrTextEmployeeTravelCancel());
			pst.setBoolean(3, getIsEmailEmployeeTravelCancel());
//			pst.setBoolean(5, getIsTextEmployeeTravelCancel());
			pst.setString(4, strFileName);
			pst.setInt(5, N_EMPLOYEE_TRAVEL_CANCEL);
			pst.addBatch();
			
			pst.setString(1, getStrBodyEmployeeTravelPullout());
			pst.setString(2, getStrSubjectEmployeeTravelPullout());
//			pst.setString(3, getStrTextEmployeeTravelPullout());
			pst.setBoolean(3, getIsEmailEmployeeTravelPullout());
//			pst.setBoolean(5, getIsTextEmployeeTravelPullout());
			pst.setString(4, strFileName);
			pst.setInt(5, N_EMPLOYEE_TRAVEL_PULLOUT);
			pst.addBatch();
			
			/*pst.setString(1, getStrBodyNewReimbursementRequest());
			pst.setString(2, getStrSubjectNewReimbursementRequest());
			pst.setString(3, getStrTextNewReimbursementRequest());
			pst.setBoolean(4, getIsEmailNewReimbursementRequest());
			pst.setBoolean(5, getIsTextNewReimbursementRequest());
			pst.setInt(6, N_EMPLOYEE_REIMBURSEMENT_REQUEST);
			pst.addBatch();*/
			
			pst.setString(1, getStrBodyNewManagerReimbursementRequest());
			pst.setString(2, getStrSubjectNewManagerReimbursementRequest());
//			pst.setString(3, getStrTextNewManagerReimbursementRequest());
			pst.setBoolean(3, getIsEmailNewManagerReimbursementRequest());
//			pst.setBoolean(5, getIsTextNewManagerReimbursementRequest());
			pst.setString(4, strFileName);
			pst.setInt(5, N_MANAGER_REIMBURSEMENT_REQUEST);
			pst.addBatch();
			
			
			pst.setString(1, getStrBodyNewReimbursementApproval());
			pst.setString(2, getStrSubjectNewReimbursementApproval());
//			pst.setString(3, getStrTextNewReimbursementApproval());
			pst.setBoolean(3, getIsEmailNewReimbursementApproval());
//			pst.setBoolean(5, getIsTextNewReimbursementApproval());
			pst.setString(4, strFileName);
			pst.setInt(5, N_EMPLOYEE_REIMBURSEMENT_APPROVAL);
			pst.addBatch();
			
			
			/*pst.setString(1, getStrBodyNewRequisitionRequest());
			pst.setString(2, getStrSubjectNewRequisitionRequest());
			pst.setString(3, getStrTextNewRequisitionRequest());
			pst.setBoolean(4, getIsEmailNewRequisitionRequest());
			pst.setBoolean(5, getIsTextNewRequisitionRequest());
			pst.setInt(6, N_EMPLOYEE_REQUISITION_REQUEST);
			pst.addBatch();*/
			
			
			pst.setString(1, getStrBodyNewRequisitionApproval());
			pst.setString(2, getStrSubjectNewRequisitionApproval());
//			pst.setString(3, getStrTextNewRequisitionApproval());
			pst.setBoolean(3, getIsEmailNewRequisitionApproval());
//			pst.setBoolean(5, getIsTextNewRequisitionApproval());
			pst.setString(4, strFileName);
			pst.setInt(5, N_EMPLOYEE_REQUISITION_APPROVAL);
			pst.addBatch();
			

			pst.setString(1, getStrBodyEmployeeProfileUpdated());
			pst.setString(2, getStrSubjectEmployeeProfileUpdated());
//			pst.setString(3, getStrTextEmployeeProfileUpdated());
			pst.setBoolean(3, getIsEmailEmployeeProfileUpdated());
//			pst.setBoolean(5, getIsTextEmployeeProfileUpdated());
			pst.setString(4, strFileName);

			pst.setInt(5, N_UPD_EMPLOYEE_PROFILE);
			pst.addBatch();
			
			
			
			pst.setString(1, getStrBodyPasswordChanged());
			pst.setString(2, getStrSubjectPasswordChanged());
//			pst.setString(3, getStrTextPasswordChanged());
			pst.setBoolean(3, getIsEmailPasswordChanged());
//			pst.setBoolean(5, getIsTextPasswordChanged());
			pst.setString(4, strFileName);
			pst.setInt(5, N_UPD_PASSWORD);
			pst.addBatch();
			
			pst.setString(1, getStrBodyPasswordReset());
			pst.setString(2, getStrSubjectPasswordReset());
//			pst.setString(3, getStrTextPasswordReset());
			pst.setBoolean(3, getIsEmailPasswordReset());
//			pst.setBoolean(5, getIsTextPasswordReset());
			pst.setString(4, strFileName);
			pst.setInt(5, N_RESET_PASSWORD);
			pst.addBatch();
			
			
			pst.setString(1, getStrBodyForgotPassword());
			pst.setString(2, getStrSubjectForgotPassword());
//			pst.setString(3, getStrTextForgotPassword());
			pst.setBoolean(3, getIsEmailForgotPassword());
//			pst.setBoolean(5, getIsTextForgotPassword());
			pst.setString(4, strFileName);
			pst.setInt(5, N_FORGOT_PASSWORD);
			pst.addBatch();
			
			
			
			
			
			/*pst.setString(1, getStrBodyEmployeeSalaryApproved());
			pst.setString(2, getStrSubjectEmployeeSalaryApproved());
			pst.setString(3, getStrTextEmployeeSalaryApproved());
			pst.setBoolean(4, getIsEmailEmployeeSalaryApproved());
			pst.setBoolean(5, getIsTextEmployeeSalaryApproved());
			pst.setInt(6, N_NEW_SALARY_APPROVED);
			pst.addBatch();*/
			
			
			
			
			/*pst.setString(1, getStrBodyEmployeePayslipGenerated());
			pst.setString(2, getStrSubjectEmployeePayslipGenerated());
			pst.setString(3, getStrTextEmployeePayslipGenerated());
			pst.setBoolean(4, getIsEmailEmployeePayslipGenerated());
			pst.setBoolean(5, getIsTextEmployeePayslipGenerated());
			pst.setInt(6, N_NEW_PAYSLIP_GENERATED);
			pst.addBatch();*/
			
			
			
			
			pst.setString(1, getStrBodySalaryReleased());
			pst.setString(2, getStrSubjectSalaryReleased());
//			pst.setString(3, getStrTextSalaryReleased());
			pst.setBoolean(3, getIsEmailSalaryReleased());
//			pst.setBoolean(5, getIsTextSalaryReleased());
			pst.setString(4, strFileName);
			pst.setInt(5, N_NEW_SALARY_PAID);
			pst.addBatch();
			
			
			pst.setString(1, getStrBodyNewAnnouncement());
			pst.setString(2, getStrSubjectNewAnnouncement());
//			pst.setString(3, getStrTextNewAnnouncement());
			pst.setBoolean(3, getIsEmailNewAnnouncement());
//			pst.setBoolean(5, getIsTextNewAnnouncement());
			pst.setString(4, strFileName);
			pst.setInt(5, N_NEW_NOTICE);
			pst.addBatch();
			
			/*pst.setString(1, getStrBodyNewRoster());
			pst.setString(2, getStrSubjectNewRoster());
			pst.setString(3, getStrTextNewRoster());
			pst.setBoolean(4, getIsEmailNewRoster());
			pst.setBoolean(5, getIsTextNewRoster());
			pst.setInt(6, N_NEW_ROSTER);
			pst.addBatch();*/
			
			
			/*pst.setString(1, getStrBodyRosterChanged());
			pst.setString(2, getStrSubjectRosterChanged());
			pst.setString(3, getStrTextRosterChanged());
			pst.setBoolean(4, getIsEmailRosterChanged());
			pst.setBoolean(5, getIsTextRosterChanged());
			pst.setInt(6, N_CHANGE_ROSTER);
			pst.addBatch();*/
			
			
			/*pst.setString(1, getStrBodyNewMail());
			pst.setString(2, getStrSubjectNewMail());
			pst.setString(3, getStrTextNewMail());
			pst.setBoolean(4, getIsEmailNewMail());
			pst.setBoolean(5, getIsTextNewMail());
			pst.setInt(6, N_NEW_MAIL);
			pst.addBatch();*/
			
			pst.setString(1, getStrBodyNewActivity());
			pst.setString(2, getStrSubjectNewActivity());
//			pst.setString(3, getStrTextNewActivity());
			pst.setBoolean(3, getIsEmailNewActivity());
//			pst.setBoolean(5, getIsTextNewActivity());
			pst.setString(4, strFileName);
			pst.setInt(5, N_NEW_ACTIVITY);
			pst.addBatch();
			
			pst.setString(1, getStrBodyEmployeeResignationRequest());
			pst.setString(2, getStrSubjectEmployeeResignationRequest());
//			pst.setString(3, getStrTextEmployeeResignationRequest());
			pst.setBoolean(3, getIsEmailEmployeeResignationRequest());
//			pst.setBoolean(5, getIsTextEmployeeResignationRequest());
			pst.setString(4, strFileName);
			pst.setInt(5, N_EMPLOYEE_RESIGNATION_REQUEST);
			pst.addBatch();
			
			pst.setString(1, getStrBodyEmployeeResignationApproval());
			pst.setString(2, getStrSubjectEmployeeResignationApproval());
//			pst.setString(3, getStrTextEmployeeResignationApproval());
			pst.setBoolean(3, getIsEmailEmployeeResignationApproval());
//			pst.setBoolean(5, getIsTextEmployeeResignationApproval());
			pst.setString(4, strFileName);
			pst.setInt(5, N_EMPLOYEE_RESIGNATION_APPROVAL);
			pst.addBatch();
			
		/*	pst.setString(1, getStrBodyBirthday());
			pst.setString(2, getStrSubjectBirthday());
//			pst.setString(3, getStrTextBirthday());
			pst.setBoolean(3, getIsEmailBirthday());
//			pst.setBoolean(5, getIsTextBirthday());
			pst.setInt(4, N_EMPLOYEE_BIRTHDAY);
			pst.addBatch();
			
			pst.setString(1, getStrBodyMarriageAnniversary());
			pst.setString(2, getStrSubjectMarriageAnniversary());
//			pst.setString(3, getStrTextMarriageAnniversary());
			pst.setBoolean(3, getIsEmailMarriageAnniversary());
//			pst.setBoolean(5, getIsTextMarriageAnniversary());
			pst.setInt(4, N_EMPLOYEE_MARRIAGE_ANNIVERSARY);
			pst.addBatch();
			
			pst.setString(1, getStrBodyWorkAnniversary());
			pst.setString(2, getStrSubjectWorkAnniversary());
//			pst.setString(3, getStrTextWorkAnniversary());
			pst.setBoolean(3, getIsEmailWorkAnniversary());
//			pst.setBoolean(5, getIsTextWorkAnniversary());
			pst.setInt(4, N_EMPLOYEE_WORK_ANNIVERSARY);
			pst.addBatch();
			
			*/
			
			
			pst.setString(1, getStrBodyOnEventCreation());
			pst.setString(2, getStrSubjectOnEventCreation());
//			pst.setString(3, getStrTextMarriageAnniversary());
			pst.setBoolean(3, getEmailOnEventCreation());
//			pst.setBoolean(5, getIsTextMarriageAnniversary());
			//pst.setString(4,strFileName);
			//pst.setString(4, backgroundImage);
			pst.setInt(4, N_ORG_EVENT);
			pst.addBatch();
			

			pst.setString(1, getStrBodyOnAnnouncementCreation());
			pst.setString(2, getStrSubjectOnAnnouncementCreation());
//			pst.setString(3, getStrTextMarriageAnniversary());
			pst.setBoolean(3, getEmailOnAnnouncementCreation());
//			pst.setBoolean(5, getIsTextMarriageAnniversary());
			pst.setString(4, strFileName);
			pst.setInt(5, N_ORG_ANNOUNCEMENT);
			pst.addBatch();
			
		//===start parvez date: 15-02-2023===
			pst.setString(1, getStrBodyOnCircularCreation());
			pst.setString(2, getStrSubjectOnCircularCreation());
			pst.setBoolean(3, getIsEmailOnCircularCreation());
			pst.setString(4, strFileName);
			pst.setInt(5, N_ORG_CIRCULAR_PUBLISH);
			pst.addBatch();
//			System.out.println("pst="+pst);
		//===end parvez date: 15-02-2023===	
			
	//===start parvez date: 26-08-2022===		
			pst.setString(1, getStrBodyEmpOnboardedBySelf());
			pst.setString(2, getStrSubjectEmpOnboardedBySelf());
			pst.setBoolean(3, getIsEmailEmpOnboardedBySelf());
			pst.setString(4, strFileName);
			pst.setInt(5, N_EMPLOYEE_ONBOARDED_BY_SELF);
			pst.addBatch();
//			System.out.println("pst="+pst);
	//===end parvez date: 26-08-2022===
			
			
			pst.executeBatch();
			pst.close();
				
			
			pst = con.prepareStatement("Update notifications set user_id=?, entry_date=?");
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			//System.out.println("pst2==>"+pst);
			pst.execute();
			pst.close();
			
			request.setAttribute(MESSAGE, SUCCESSM+"New Settings have been successfully saved."+END);

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public void validate() {
		
		UtilityFunctions uF = new UtilityFunctions();
        loadValidateNotificationSettings();
    }
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public boolean getIsEmailEmployeeLeaveCancel() {
		return isEmailEmployeeLeaveCancel;
	}


	public void setIsEmailEmployeeLeaveCancel(boolean isEmailEmployeeLeaveCancel) {
		this.isEmailEmployeeLeaveCancel = isEmailEmployeeLeaveCancel;
	}


	public boolean getIsTextEmployeeLeaveCancel() {
		return isTextEmployeeLeaveCancel;
	}


	public void setIsTextEmployeeLeaveCancel(boolean isTextEmployeeLeaveCancel) {
		this.isTextEmployeeLeaveCancel = isTextEmployeeLeaveCancel;
	}


	public String getStrTextEmployeeLeaveCancel() {
		return strTextEmployeeLeaveCancel;
	}


	public void setStrTextEmployeeLeaveCancel(String strTextEmployeeLeaveCancel) {
		this.strTextEmployeeLeaveCancel = strTextEmployeeLeaveCancel;
	}


	public String getStrSubjectEmployeeLeaveCancel() {
		return strSubjectEmployeeLeaveCancel;
	}


	public void setStrSubjectEmployeeLeaveCancel(String strSubjectEmployeeLeaveCancel) {
		this.strSubjectEmployeeLeaveCancel = strSubjectEmployeeLeaveCancel;
	}


	public String getStrBodyEmployeeLeaveCancel() {
		return strBodyEmployeeLeaveCancel;
	}


	public void setStrBodyEmployeeLeaveCancel(String strBodyEmployeeLeaveCancel) {
		this.strBodyEmployeeLeaveCancel = strBodyEmployeeLeaveCancel;
	}

	public void setStrTextNewEmployee(String strTextNewEmployee) {
		this.strTextNewEmployee = strTextNewEmployee;
	}

	public String getStrNotificationCode() {
		return strNotificationCode;
	}

	public void setStrNotificationCode(String strNotificationCode) {
		this.strNotificationCode = strNotificationCode;
	}

	public boolean getIsEmailNewEmployee() {
		return isEmailNewEmployee;
	}

	public void setIsEmailNewEmployee(boolean isEmailNewEmployee) {
		this.isEmailNewEmployee = isEmailNewEmployee;
	}

	public boolean getIsTextNewEmployee() {
		return isTextNewEmployee;
	}

	public void setIsTextNewEmployee(boolean isTextNewEmployee) {
		this.isTextNewEmployee = isTextNewEmployee;
	}

	public String getStrSubjectNewEmployee() {
		return strSubjectNewEmployee;
	}

	public void setStrSubjectNewEmployee(String strSubjectNewEmployee) {
		this.strSubjectNewEmployee = strSubjectNewEmployee;
	}

	public String getStrBodyNewEmployee() {
		return strBodyNewEmployee;
	}

	public void setStrBodyNewEmployee(String strBodyNewEmployee) {
		this.strBodyNewEmployee = strBodyNewEmployee;
	}

	public boolean getIsEmailNewEmployeeStatusChange() {
		return isEmailNewEmployeeStatusChange;
	}

	public void setIsEmailNewEmployeeStatusChange(
			boolean isEmailNewEmployeeStatusChange) {
		this.isEmailNewEmployeeStatusChange = isEmailNewEmployeeStatusChange;
	}

	public boolean getIsTextNewEmployeeStatusChange() {
		return isTextNewEmployeeStatusChange;
	}

	public void setIsTextNewEmployeeStatusChange(boolean isTextNewEmployeeStatusChange) {
		this.isTextNewEmployeeStatusChange = isTextNewEmployeeStatusChange;
	}

	public String getStrTextNewEmployeeStatusChange() {
		return strTextNewEmployeeStatusChange;
	}

	public void setStrTextNewEmployeeStatusChange(
			String strTextNewEmployeeStatusChange) {
		this.strTextNewEmployeeStatusChange = strTextNewEmployeeStatusChange;
	}

	public String getStrSubjectNewEmployeeStatusChange() {
		return strSubjectNewEmployeeStatusChange;
	}

	public void setStrSubjectNewEmployeeStatusChange(
			String strSubjectNewEmployeeStatusChange) {
		this.strSubjectNewEmployeeStatusChange = strSubjectNewEmployeeStatusChange;
	}

	public String getStrBodyNewEmployeeStatusChange() {
		return strBodyNewEmployeeStatusChange;
	}

	public void setStrBodyNewEmployeeStatusChange(
			String strBodyNewEmployeeStatusChange) {
		this.strBodyNewEmployeeStatusChange = strBodyNewEmployeeStatusChange;
	}

	public boolean getIsEmailNewEmployeeLeaveRequest() {
		return isEmailNewEmployeeLeaveRequest;
	}

	public void setIsEmailNewEmployeeLeaveRequest(
			boolean isEmailNewEmployeeLeaveRequest) {
		this.isEmailNewEmployeeLeaveRequest = isEmailNewEmployeeLeaveRequest;
	}

	public boolean getIsTextNewEmployeeLeaveRequest() {
		return isTextNewEmployeeLeaveRequest;
	}


	public void setIsTextNewEmployeeLeaveRequest(boolean isTextNewEmployeeLeaveRequest) {
		this.isTextNewEmployeeLeaveRequest = isTextNewEmployeeLeaveRequest;
	}


	public String getStrTextNewEmployeeLeaveRequest() {
		return strTextNewEmployeeLeaveRequest;
	}


	public void setStrTextNewEmployeeLeaveRequest(
			String strTextNewEmployeeLeaveRequest) {
		this.strTextNewEmployeeLeaveRequest = strTextNewEmployeeLeaveRequest;
	}


	public String getStrSubjectNewEmployeeLeaveRequest() {
		return strSubjectNewEmployeeLeaveRequest;
	}


	public void setStrSubjectNewEmployeeLeaveRequest(
			String strSubjectNewEmployeeLeaveRequest) {
		this.strSubjectNewEmployeeLeaveRequest = strSubjectNewEmployeeLeaveRequest;
	}


	public String getStrBodyNewEmployeeLeaveRequest() {
		return strBodyNewEmployeeLeaveRequest;
	}


	public void setStrBodyNewEmployeeLeaveRequest(
			String strBodyNewEmployeeLeaveRequest) {
		this.strBodyNewEmployeeLeaveRequest = strBodyNewEmployeeLeaveRequest;
	}


	public boolean getIsEmailNewEmployeeLeaveApproval() {
		return isEmailNewEmployeeLeaveApproval;
	}


	public void setIsEmailNewEmployeeLeaveApproval(
			boolean isEmailNewEmployeeLeaveApproval) {
		this.isEmailNewEmployeeLeaveApproval = isEmailNewEmployeeLeaveApproval;
	}


	public boolean getIsTextNewEmployeeLeaveApproval() {
		return isTextNewEmployeeLeaveApproval;
	}


	public void setIsTextNewEmployeeLeaveApproval(
			boolean isTextNewEmployeeLeaveApproval) {
		this.isTextNewEmployeeLeaveApproval = isTextNewEmployeeLeaveApproval;
	}


	public String getStrTextNewEmployeeLeaveApproval() {
		return strTextNewEmployeeLeaveApproval;
	}


	public void setStrTextNewEmployeeLeaveApproval(
			String strTextNewEmployeeLeaveApproval) {
		this.strTextNewEmployeeLeaveApproval = strTextNewEmployeeLeaveApproval;
	}


	public String getStrSubjectNewEmployeeLeaveApproval() {
		return strSubjectNewEmployeeLeaveApproval;
	}


	public void setStrSubjectNewEmployeeLeaveApproval(
			String strSubjectNewEmployeeLeaveApproval) {
		this.strSubjectNewEmployeeLeaveApproval = strSubjectNewEmployeeLeaveApproval;
	}


	public String getStrBodyNewEmployeeLeaveApproval() {
		return strBodyNewEmployeeLeaveApproval;
	}


	public void setStrBodyNewEmployeeLeaveApproval(
			String strBodyNewEmployeeLeaveApproval) {
		this.strBodyNewEmployeeLeaveApproval = strBodyNewEmployeeLeaveApproval;
	}


	public boolean getIsEmailNewReimbursementRequest() {
		return isEmailNewReimbursementRequest;
	}


	public void setIsEmailNewReimbursementRequest(
			boolean isEmailNewReimbursementRequest) {
		this.isEmailNewReimbursementRequest = isEmailNewReimbursementRequest;
	}


	public boolean getIsTextNewReimbursementRequest() {
		return isTextNewReimbursementRequest;
	}


	public void setIsTextNewReimbursementRequest(boolean isTextNewReimbursementRequest) {
		this.isTextNewReimbursementRequest = isTextNewReimbursementRequest;
	}


	public String getStrTextNewReimbursementRequest() {
		return strTextNewReimbursementRequest;
	}


	public void setStrTextNewReimbursementRequest(
			String strTextNewReimbursementRequest) {
		this.strTextNewReimbursementRequest = strTextNewReimbursementRequest;
	}


	public String getStrSubjectNewReimbursementRequest() {
		return strSubjectNewReimbursementRequest;
	}


	public void setStrSubjectNewReimbursementRequest(
			String strSubjectNewReimbursementRequest) {
		this.strSubjectNewReimbursementRequest = strSubjectNewReimbursementRequest;
	}


	public String getStrBodyNewReimbursementRequest() {
		return strBodyNewReimbursementRequest;
	}


	public void setStrBodyNewReimbursementRequest(
			String strBodyNewReimbursementRequest) {
		this.strBodyNewReimbursementRequest = strBodyNewReimbursementRequest;
	}


	public boolean getIsEmailNewReimbursementApproval() {
		return isEmailNewReimbursementApproval;
	}


	public void setIsEmailNewReimbursementApproval(
			boolean isEmailNewReimbursementApproval) {
		this.isEmailNewReimbursementApproval = isEmailNewReimbursementApproval;
	}


	public boolean getIsTextNewReimbursementApproval() {
		return isTextNewReimbursementApproval;
	}


	public void setIsTextNewReimbursementApproval(
			boolean isTextNewReimbursementApproval) {
		this.isTextNewReimbursementApproval = isTextNewReimbursementApproval;
	}


	public String getStrTextNewReimbursementApproval() {
		return strTextNewReimbursementApproval;
	}


	public void setStrTextNewReimbursementApproval(
			String strTextNewReimbursementApproval) {
		this.strTextNewReimbursementApproval = strTextNewReimbursementApproval;
	}


	public String getStrSubjectNewReimbursementApproval() {
		return strSubjectNewReimbursementApproval;
	}


	public void setStrSubjectNewReimbursementApproval(
			String strSubjectNewReimbursementApproval) {
		this.strSubjectNewReimbursementApproval = strSubjectNewReimbursementApproval;
	}


	public String getStrBodyNewReimbursementApproval() {
		return strBodyNewReimbursementApproval;
	}


	public void setStrBodyNewReimbursementApproval(
			String strBodyNewReimbursementApproval) {
		this.strBodyNewReimbursementApproval = strBodyNewReimbursementApproval;
	}


	public boolean getIsEmailNewRequisitionRequest() {
		return isEmailNewRequisitionRequest;
	}


	public void setIsEmailNewRequisitionRequest(boolean isEmailNewRequisitionRequest) {
		this.isEmailNewRequisitionRequest = isEmailNewRequisitionRequest;
	}


	public boolean getIsTextNewRequisitionRequest() {
		return isTextNewRequisitionRequest;
	}


	public void setIsTextNewRequisitionRequest(boolean isTextNewRequisitionRequest) {
		this.isTextNewRequisitionRequest = isTextNewRequisitionRequest;
	}


	public String getStrTextNewRequisitionRequest() {
		return strTextNewRequisitionRequest;
	}


	public void setStrTextNewRequisitionRequest(String strTextNewRequisitionRequest) {
		this.strTextNewRequisitionRequest = strTextNewRequisitionRequest;
	}


	public String getStrSubjectNewRequisitionRequest() {
		return strSubjectNewRequisitionRequest;
	}


	public void setStrSubjectNewRequisitionRequest(
			String strSubjectNewRequisitionRequest) {
		this.strSubjectNewRequisitionRequest = strSubjectNewRequisitionRequest;
	}


	public String getStrBodyNewRequisitionRequest() {
		return strBodyNewRequisitionRequest;
	}


	public void setStrBodyNewRequisitionRequest(String strBodyNewRequisitionRequest) {
		this.strBodyNewRequisitionRequest = strBodyNewRequisitionRequest;
	}


	public boolean getIsEmailNewRequisitionApproval() {
		return isEmailNewRequisitionApproval;
	}


	public void setIsEmailNewRequisitionApproval(boolean isEmailNewRequisitionApproval) {
		this.isEmailNewRequisitionApproval = isEmailNewRequisitionApproval;
	}


	public boolean getIsTextNewRequisitionApproval() {
		return isTextNewRequisitionApproval;
	}


	public void setIsTextNewRequisitionApproval(boolean isTextNewRequisitionApproval) {
		this.isTextNewRequisitionApproval = isTextNewRequisitionApproval;
	}


	public String getStrTextNewRequisitionApproval() {
		return strTextNewRequisitionApproval;
	}


	public void setStrTextNewRequisitionApproval(
			String strTextNewRequisitionApproval) {
		this.strTextNewRequisitionApproval = strTextNewRequisitionApproval;
	}


	public String getStrSubjectNewRequisitionApproval() {
		return strSubjectNewRequisitionApproval;
	}


	public void setStrSubjectNewRequisitionApproval(
			String strSubjectNewRequisitionApproval) {
		this.strSubjectNewRequisitionApproval = strSubjectNewRequisitionApproval;
	}


	public String getStrBodyNewRequisitionApproval() {
		return strBodyNewRequisitionApproval;
	}


	public void setStrBodyNewRequisitionApproval(
			String strBodyNewRequisitionApproval) {
		this.strBodyNewRequisitionApproval = strBodyNewRequisitionApproval;
	}


	public boolean getIsEmailEmployeeProfileUpdated() {
		return isEmailEmployeeProfileUpdated;
	}


	public void setIsEmailEmployeeProfileUpdated(boolean isEmailEmployeeProfileUpdated) {
		this.isEmailEmployeeProfileUpdated = isEmailEmployeeProfileUpdated;
	}


	public boolean getIsTextEmployeeProfileUpdated() {
		return isTextEmployeeProfileUpdated;
	}


	public void setIsTextEmployeeProfileUpdated(boolean isTextEmployeeProfileUpdated) {
		this.isTextEmployeeProfileUpdated = isTextEmployeeProfileUpdated;
	}


	public String getStrTextEmployeeProfileUpdated() {
		return strTextEmployeeProfileUpdated;
	}


	public void setStrTextEmployeeProfileUpdated(
			String strTextEmployeeProfileUpdated) {
		this.strTextEmployeeProfileUpdated = strTextEmployeeProfileUpdated;
	}


	public String getStrSubjectEmployeeProfileUpdated() {
		return strSubjectEmployeeProfileUpdated;
	}


	public void setStrSubjectEmployeeProfileUpdated(
			String strSubjectEmployeeProfileUpdated) {
		this.strSubjectEmployeeProfileUpdated = strSubjectEmployeeProfileUpdated;
	}


	public String getStrBodyEmployeeProfileUpdated() {
		return strBodyEmployeeProfileUpdated;
	}


	public void setStrBodyEmployeeProfileUpdated(
			String strBodyEmployeeProfileUpdated) {
		this.strBodyEmployeeProfileUpdated = strBodyEmployeeProfileUpdated;
	}


	public boolean getIsEmailPasswordChanged() {
		return isEmailPasswordChanged;
	}


	public void setIsEmailPasswordChanged(boolean isEmailPasswordChanged) {
		this.isEmailPasswordChanged = isEmailPasswordChanged;
	}


	public boolean getIsTextPasswordChanged() {
		return isTextPasswordChanged;
	}


	public void setIsTextPasswordChanged(boolean isTextPasswordChanged) {
		this.isTextPasswordChanged = isTextPasswordChanged;
	}


	public String getStrTextPasswordChanged() {
		return strTextPasswordChanged;
	}


	public void setStrTextPasswordChanged(String strTextPasswordChanged) {
		this.strTextPasswordChanged = strTextPasswordChanged;
	}


	public String getStrSubjectPasswordChanged() {
		return strSubjectPasswordChanged;
	}


	public void setStrSubjectPasswordChanged(String strSubjectPasswordChanged) {
		this.strSubjectPasswordChanged = strSubjectPasswordChanged;
	}


	public String getStrBodyPasswordChanged() {
		return strBodyPasswordChanged;
	}


	public void setStrBodyPasswordChanged(String strBodyPasswordChanged) {
		this.strBodyPasswordChanged = strBodyPasswordChanged;
	}


	public boolean getIsEmailForgotPassword() {
		return isEmailForgotPassword;
	}


	public void setIsEmailForgotPassword(boolean isEmailForgotPassword) {
		this.isEmailForgotPassword = isEmailForgotPassword;
	}


	public boolean getIsTextForgotPassword() {
		return isTextForgotPassword;
	}


	public void setIsTextForgotPassword(boolean isTextForgotPassword) {
		this.isTextForgotPassword = isTextForgotPassword;
	}


	public String getStrTextForgotPassword() {
		return strTextForgotPassword;
	}


	public void setStrTextForgotPassword(String strTextForgotPassword) {
		this.strTextForgotPassword = strTextForgotPassword;
	}


	public String getStrSubjectForgotPassword() {
		return strSubjectForgotPassword;
	}


	public void setStrSubjectForgotPassword(String strSubjectForgotPassword) {
		this.strSubjectForgotPassword = strSubjectForgotPassword;
	}


	public String getStrBodyForgotPassword() {
		return strBodyForgotPassword;
	}


	public void setStrBodyForgotPassword(String strBodyForgotPassword) {
		this.strBodyForgotPassword = strBodyForgotPassword;
	}


	public boolean getIsEmailEmployeeSalaryApproved() {
		return isEmailEmployeeSalaryApproved;
	}


	public void setIsEmailEmployeeSalaryApproved(boolean isEmailEmployeeSalaryApproved) {
		this.isEmailEmployeeSalaryApproved = isEmailEmployeeSalaryApproved;
	}


	public boolean getIsTextEmployeeSalaryApproved() {
		return isTextEmployeeSalaryApproved;
	}


	public void setIsTextEmployeeSalaryApproved(boolean isTextEmployeeSalaryApproved) {
		this.isTextEmployeeSalaryApproved = isTextEmployeeSalaryApproved;
	}


	public String getStrTextEmployeeSalaryApproved() {
		return strTextEmployeeSalaryApproved;
	}


	public void setStrTextEmployeeSalaryApproved(
			String strTextEmployeeSalaryApproved) {
		this.strTextEmployeeSalaryApproved = strTextEmployeeSalaryApproved;
	}


	public String getStrSubjectEmployeeSalaryApproved() {
		return strSubjectEmployeeSalaryApproved;
	}


	public void setStrSubjectEmployeeSalaryApproved(
			String strSubjectEmployeeSalaryApproved) {
		this.strSubjectEmployeeSalaryApproved = strSubjectEmployeeSalaryApproved;
	}


	public String getStrBodyEmployeeSalaryApproved() {
		return strBodyEmployeeSalaryApproved;
	}


	public void setStrBodyEmployeeSalaryApproved(
			String strBodyEmployeeSalaryApproved) {
		this.strBodyEmployeeSalaryApproved = strBodyEmployeeSalaryApproved;
	}


	public boolean getIsEmailEmployeePayslipGenerated() {
		return isEmailEmployeePayslipGenerated;
	}


	public void setIsEmailEmployeePayslipGenerated(
			boolean isEmailEmployeePayslipGenerated) {
		this.isEmailEmployeePayslipGenerated = isEmailEmployeePayslipGenerated;
	}


	public boolean getIsTextEmployeePayslipGenerated() {
		return isTextEmployeePayslipGenerated;
	}


	public void setIsTextEmployeePayslipGenerated(
			boolean isTextEmployeePayslipGenerated) {
		this.isTextEmployeePayslipGenerated = isTextEmployeePayslipGenerated;
	}


	public String getStrTextEmployeePayslipGenerated() {
		return strTextEmployeePayslipGenerated;
	}


	public void setStrTextEmployeePayslipGenerated(
			String strTextEmployeePayslipGenerated) {
		this.strTextEmployeePayslipGenerated = strTextEmployeePayslipGenerated;
	}


	public String getStrSubjectEmployeePayslipGenerated() {
		return strSubjectEmployeePayslipGenerated;
	}


	public void setStrSubjectEmployeePayslipGenerated(
			String strSubjectEmployeePayslipGenerated) {
		this.strSubjectEmployeePayslipGenerated = strSubjectEmployeePayslipGenerated;
	}


	public String getStrBodyEmployeePayslipGenerated() {
		return strBodyEmployeePayslipGenerated;
	}


	public void setStrBodyEmployeePayslipGenerated(
			String strBodyEmployeePayslipGenerated) {
		this.strBodyEmployeePayslipGenerated = strBodyEmployeePayslipGenerated;
	}


	public boolean getIsEmailSalaryReleased() {
		return isEmailSalaryReleased;
	}


	public void setIsEmailSalaryReleased(boolean isEmailSalaryReleased) {
		this.isEmailSalaryReleased = isEmailSalaryReleased;
	}


	public boolean getIsTextSalaryReleased() {
		return isTextSalaryReleased;
	}


	public void setIsTextSalaryReleased(boolean isTextSalaryReleased) {
		this.isTextSalaryReleased = isTextSalaryReleased;
	}


	public String getStrTextSalaryReleased() {
		return strTextSalaryReleased;
	}


	public void setStrTextSalaryReleased(String strTextSalaryReleased) {
		this.strTextSalaryReleased = strTextSalaryReleased;
	}


	public String getStrSubjectSalaryReleased() {
		return strSubjectSalaryReleased;
	}


	public void setStrSubjectSalaryReleased(String strSubjectSalaryReleased) {
		this.strSubjectSalaryReleased = strSubjectSalaryReleased;
	}


	public String getStrBodySalaryReleased() {
		return strBodySalaryReleased;
	}


	public void setStrBodySalaryReleased(String strBodySalaryReleased) {
		this.strBodySalaryReleased = strBodySalaryReleased;
	}


	public boolean getIsEmailNewAnnouncement() {
		return isEmailNewAnnouncement;
	}


	public void setIsEmailNewAnnouncement(boolean isEmailNewAnnouncement) {
		this.isEmailNewAnnouncement = isEmailNewAnnouncement;
	}


	public boolean getIsTextNewAnnouncement() {
		return isTextNewAnnouncement;
	}


	public void setIsTextNewAnnouncement(boolean isTextNewAnnouncement) {
		this.isTextNewAnnouncement = isTextNewAnnouncement;
	}


	public String getStrTextNewAnnouncement() {
		return strTextNewAnnouncement;
	}


	public void setStrTextNewAnnouncement(String strTextNewAnnouncement) {
		this.strTextNewAnnouncement = strTextNewAnnouncement;
	}


	public String getStrSubjectNewAnnouncement() {
		return strSubjectNewAnnouncement;
	}


	public void setStrSubjectNewAnnouncement(String strSubjectNewAnnouncement) {
		this.strSubjectNewAnnouncement = strSubjectNewAnnouncement;
	}


	public String getStrBodyNewAnnouncement() {
		return strBodyNewAnnouncement;
	}


	public void setStrBodyNewAnnouncement(String strBodyNewAnnouncement) {
		this.strBodyNewAnnouncement = strBodyNewAnnouncement;
	}


	public boolean getIsEmailNewRoster() {
		return isEmailNewRoster;
	}


	public void setIsEmailNewRoster(boolean isEmailNewRoster) {
		this.isEmailNewRoster = isEmailNewRoster;
	}


	public boolean getIsTextNewRoster() {
		return isTextNewRoster;
	}


	public void setIsTextNewRoster(boolean isTextNewRoster) {
		this.isTextNewRoster = isTextNewRoster;
	}

	public String getStrTextNewRoster() {
		return strTextNewRoster;
	}

	public void setStrTextNewRoster(String strTextNewRoster) {
		this.strTextNewRoster = strTextNewRoster;
	}

	public String getStrSubjectNewRoster() {
		return strSubjectNewRoster;
	}

	public void setStrSubjectNewRoster(String strSubjectNewRoster) {
		this.strSubjectNewRoster = strSubjectNewRoster;
	}

	public String getStrBodyNewRoster() {
		return strBodyNewRoster;
	}

	public void setStrBodyNewRoster(String strBodyNewRoster) {
		this.strBodyNewRoster = strBodyNewRoster;
	}

	public boolean getIsEmailRosterChanged() {
		return isEmailRosterChanged;
	}

	public void setIsEmailRosterChanged(boolean isEmailRosterChanged) {
		this.isEmailRosterChanged = isEmailRosterChanged;
	}

	public boolean getIsTextRosterChanged() {
		return isTextRosterChanged;
	}

	public void setIsTextRosterChanged(boolean isTextRosterChanged) {
		this.isTextRosterChanged = isTextRosterChanged;
	}

	public String getStrTextRosterChanged() {
		return strTextRosterChanged;
	}

	public void setStrTextRosterChanged(String strTextRosterChanged) {
		this.strTextRosterChanged = strTextRosterChanged;
	}

	public String getStrSubjectRosterChanged() {
		return strSubjectRosterChanged;
	}

	public void setStrSubjectRosterChanged(String strSubjectRosterChanged) {
		this.strSubjectRosterChanged = strSubjectRosterChanged;
	}

	public String getStrBodyRosterChanged() {
		return strBodyRosterChanged;
	}

	public void setStrBodyRosterChanged(String strBodyRosterChanged) {
		this.strBodyRosterChanged = strBodyRosterChanged;
	}

	public String getStrTextNewEmployee() {
		return strTextNewEmployee;
	}

	public boolean getIsEmailNewMail() {
		return isEmailNewMail;
	}

	public void setIsEmailNewMail(boolean isEmailNewMail) {
		this.isEmailNewMail = isEmailNewMail;
	}

	public boolean getIsTextNewMail() {
		return isTextNewMail;
	}

	public void setIsTextNewMail(boolean isTextNewMail) {
		this.isTextNewMail = isTextNewMail;
	}

	public String getStrTextNewMail() {
		return strTextNewMail;
	}

	public void setStrTextNewMail(String strTextNewMail) {
		this.strTextNewMail = strTextNewMail;
	}

	public String getStrSubjectNewMail() {
		return strSubjectNewMail;
	}

	public void setStrSubjectNewMail(String strSubjectNewMail) {
		this.strSubjectNewMail = strSubjectNewMail;
	}

	public String getStrBodyNewMail() {
		return strBodyNewMail;
	}

	public void setStrBodyNewMail(String strBodyNewMail) {
		this.strBodyNewMail = strBodyNewMail;
	}

	public boolean getIsEmailManagerNewLeaveRequest() {
		return isEmailManagerNewLeaveRequest;
	}

	public void setIsEmailManagerNewLeaveRequest(boolean isEmailManagerNewLeaveRequest) {
		this.isEmailManagerNewLeaveRequest = isEmailManagerNewLeaveRequest;
	}

	public boolean getIsTextManagerNewLeaveRequest() {
		return isTextManagerNewLeaveRequest;
	}

	public void setIsTextManagerNewLeaveRequest(boolean isTextManagerNewLeaveRequest) {
		this.isTextManagerNewLeaveRequest = isTextManagerNewLeaveRequest;
	}

	public String getStrTextManagerNewLeaveRequest() {
		return strTextManagerNewLeaveRequest;
	}

	public void setStrTextManagerNewLeaveRequest(
			String strTextManagerNewLeaveRequest) {
		this.strTextManagerNewLeaveRequest = strTextManagerNewLeaveRequest;
	}

	public String getStrSubjectManagerNewLeaveRequest() {
		return strSubjectManagerNewLeaveRequest;
	}

	public void setStrSubjectManagerNewLeaveRequest(
			String strSubjectManagerNewLeaveRequest) {
		this.strSubjectManagerNewLeaveRequest = strSubjectManagerNewLeaveRequest;
	}

	public String getStrBodyManagerNewLeaveRequest() {
		return strBodyManagerNewLeaveRequest;
	}

	public void setStrBodyManagerNewLeaveRequest(
			String strBodyManagerNewLeaveRequest) {
		this.strBodyManagerNewLeaveRequest = strBodyManagerNewLeaveRequest;
	}

	public boolean getIsEmailNewEmployeeJoining() {
		return isEmailNewEmployeeJoining;
	}

	public void setIsEmailNewEmployeeJoining(boolean isEmailNewEmployeeJoining) {
		this.isEmailNewEmployeeJoining = isEmailNewEmployeeJoining;
	}

	public boolean getIsTextNewEmployeeJoining() {
		return isTextNewEmployeeJoining;
	}
	public void setIsTextNewEmployeeJoining(boolean isTextNewEmployeeJoining) {
		this.isTextNewEmployeeJoining = isTextNewEmployeeJoining;
	}
	public String getStrTextNewEmployeeJoining() {
		return strTextNewEmployeeJoining;
	}
	public void setStrTextNewEmployeeJoining(String strTextNewEmployeeJoining) {
		this.strTextNewEmployeeJoining = strTextNewEmployeeJoining;
	}
	public String getStrSubjectNewEmployeeJoining() {
		return strSubjectNewEmployeeJoining;
	}
	public void setStrSubjectNewEmployeeJoining(String strSubjectNewEmployeeJoining) {
		this.strSubjectNewEmployeeJoining = strSubjectNewEmployeeJoining;
	}
	public String getStrBodyNewEmployeeJoining() {
		return strBodyNewEmployeeJoining;
	}
	public void setStrBodyNewEmployeeJoining(String strBodyNewEmployeeJoining) {
		this.strBodyNewEmployeeJoining = strBodyNewEmployeeJoining;
	}
	public boolean getIsEmailPasswordReset() {
		return isEmailPasswordReset;
	}
	public void setIsEmailPasswordReset(boolean isEmailPasswordReset) {
		this.isEmailPasswordReset = isEmailPasswordReset;
	}
	public boolean getIsTextPasswordReset() {
		return isTextPasswordReset;
	}
	public void setIsTextPasswordReset(boolean isTextPasswordReset) {
		this.isTextPasswordReset = isTextPasswordReset;
	}
	public String getStrTextPasswordReset() {
		return strTextPasswordReset;
	}
	public void setStrTextPasswordReset(String strTextPasswordReset) {
		this.strTextPasswordReset = strTextPasswordReset;
	}
	public String getStrSubjectPasswordReset() {
		return strSubjectPasswordReset;
	}
	public void setStrSubjectPasswordReset(String strSubjectPasswordReset) {
		this.strSubjectPasswordReset = strSubjectPasswordReset;
	}
	public String getStrBodyPasswordReset() {
		return strBodyPasswordReset;
	}
	public void setStrBodyPasswordReset(String strBodyPasswordReset) {
		this.strBodyPasswordReset = strBodyPasswordReset;
	}

	public String getStrTextNewReviewPublish() {
		return strTextNewReviewPublish;
	}

	public void setStrTextNewReviewPublish(String strTextNewReviewPublish) {
		this.strTextNewReviewPublish = strTextNewReviewPublish;
	}

	public String getStrSubjectNewReviewPublish() {
		return strSubjectNewReviewPublish;
	}

	public void setStrSubjectNewReviewPublish(String strSubjectNewReviewPublish) {
		this.strSubjectNewReviewPublish = strSubjectNewReviewPublish;
	}

	public String getStrBodyNewReviewPublish() {
		return strBodyNewReviewPublish;
	}

	public void setStrBodyNewReviewPublish(String strBodyNewReviewPublish) {
		this.strBodyNewReviewPublish = strBodyNewReviewPublish;
	}

	public boolean getIsEmailNewReviewPublish() {
		return isEmailNewReviewPublish;
	}

	public void setIsEmailNewReviewPublish(boolean isEmailNewReviewPublish) {
		this.isEmailNewReviewPublish = isEmailNewReviewPublish;
	}

	public boolean getIsTextNewReviewPublish() {
		return isTextNewReviewPublish;
	}

	public void setIsTextNewReviewPublish(boolean isTextNewReviewPublish) {
		this.isTextNewReviewPublish = isTextNewReviewPublish;
	}

	public boolean getIsEmailPendingReviewReminder() {
		return isEmailPendingReviewReminder;
	}

	public void setIsEmailPendingReviewReminder(boolean isEmailPendingReviewReminder) {
		this.isEmailPendingReviewReminder = isEmailPendingReviewReminder;
	}

	public boolean getIsTextPendingReviewReminder() {
		return isTextPendingReviewReminder;
	}

	public void setIsTextPendingReviewReminder(boolean isTextPendingReviewReminder) {
		this.isTextPendingReviewReminder = isTextPendingReviewReminder;
	}

	public String getStrTextPendingReviewReminder() {
		return strTextPendingReviewReminder;
	}

	public void setStrTextPendingReviewReminder(String strTextPendingReviewReminder) {
		this.strTextPendingReviewReminder = strTextPendingReviewReminder;
	}

	public String getStrSubjectPendingReviewReminder() {
		return strSubjectPendingReviewReminder;
	}

	public void setStrSubjectPendingReviewReminder(String strSubjectPendingReviewReminder) {
		this.strSubjectPendingReviewReminder = strSubjectPendingReviewReminder;
	}

	public String getStrBodyPendingReviewReminder() {
		return strBodyPendingReviewReminder;
	}

	public void setStrBodyPendingReviewReminder(String strBodyPendingReviewReminder) {
		this.strBodyPendingReviewReminder = strBodyPendingReviewReminder;
	}

	public boolean getIsEmailEmpReviewSubmit() {
		return isEmailEmpReviewSubmit;
	}

	public void setIsEmailEmpReviewSubmit(boolean isEmailEmpReviewSubmit) {
		this.isEmailEmpReviewSubmit = isEmailEmpReviewSubmit;
	}

	public boolean getIsTextEmpReviewSubmit() {
		return isTextEmpReviewSubmit;
	}

	public void setIsTextEmpReviewSubmit(boolean isTextEmpReviewSubmit) {
		this.isTextEmpReviewSubmit = isTextEmpReviewSubmit;
	}

	public String getStrTextEmpReviewSubmit() {
		return strTextEmpReviewSubmit;
	}

	public void setStrTextEmpReviewSubmit(String strTextEmpReviewSubmit) {
		this.strTextEmpReviewSubmit = strTextEmpReviewSubmit;
	}

	public String getStrSubjectEmpReviewSubmit() {
		return strSubjectEmpReviewSubmit;
	}

	public void setStrSubjectEmpReviewSubmit(String strSubjectEmpReviewSubmit) {
		this.strSubjectEmpReviewSubmit = strSubjectEmpReviewSubmit;
	}

	public String getStrBodyEmpReviewSubmit() {
		return strBodyEmpReviewSubmit;
	}

	public void setStrBodyEmpReviewSubmit(String strBodyEmpReviewSubmit) {
		this.strBodyEmpReviewSubmit = strBodyEmpReviewSubmit;
	}

	public boolean getIsEmailReviewFinalizationForHr() {
		return isEmailReviewFinalizationForHr;
	}

	public void setIsEmailReviewFinalizationForHr(boolean isEmailReviewFinalizationForHr) {
		this.isEmailReviewFinalizationForHr = isEmailReviewFinalizationForHr;
	}

	public boolean getIsTextReviewFinalizationForHr() {
		return isTextReviewFinalizationForHr;
	}

	public void setIsTextReviewFinalizationForHr(boolean isTextReviewFinalizationForHr) {
		this.isTextReviewFinalizationForHr = isTextReviewFinalizationForHr;
	}

	public String getStrTextReviewFinalizationForHr() {
		return strTextReviewFinalizationForHr;
	}

	public void setStrTextReviewFinalizationForHr(String strTextReviewFinalizationForHr) {
		this.strTextReviewFinalizationForHr = strTextReviewFinalizationForHr;
	}

	public String getStrSubjectReviewFinalizationForHr() {
		return strSubjectReviewFinalizationForHr;
	}

	public void setStrSubjectReviewFinalizationForHr(String strSubjectReviewFinalizationForHr) {
		this.strSubjectReviewFinalizationForHr = strSubjectReviewFinalizationForHr;
	}

	public String getStrBodyReviewFinalizationForHr() {
		return strBodyReviewFinalizationForHr;
	}

	public void setStrBodyReviewFinalizationForHr(String strBodyReviewFinalizationForHr) {
		this.strBodyReviewFinalizationForHr = strBodyReviewFinalizationForHr;
	}

	public boolean getIsEmailReviewFinalizationForEmp() {
		return isEmailReviewFinalizationForEmp;
	}

	public void setIsEmailReviewFinalizationForEmp(boolean isEmailReviewFinalizationForEmp) {
		this.isEmailReviewFinalizationForEmp = isEmailReviewFinalizationForEmp;
	}

	public boolean getIsTextReviewFinalizationForEmp() {
		return isTextReviewFinalizationForEmp;
	}

	public void setIsTextReviewFinalizationForEmp(boolean isTextReviewFinalizationForEmp) {
		this.isTextReviewFinalizationForEmp = isTextReviewFinalizationForEmp;
	}

	public String getStrTextReviewFinalizationForEmp() {
		return strTextReviewFinalizationForEmp;
	}

	public void setStrTextReviewFinalizationForEmp(String strTextReviewFinalizationForEmp) {
		this.strTextReviewFinalizationForEmp = strTextReviewFinalizationForEmp;
	}

	public String getStrSubjectReviewFinalizationForEmp() {
		return strSubjectReviewFinalizationForEmp;
	}

	public void setStrSubjectReviewFinalizationForEmp(String strSubjectReviewFinalizationForEmp) {
		this.strSubjectReviewFinalizationForEmp = strSubjectReviewFinalizationForEmp;
	}

	public String getStrBodyReviewFinalizationForEmp() {
		return strBodyReviewFinalizationForEmp;
	}

	public void setStrBodyReviewFinalizationForEmp(String strBodyReviewFinalizationForEmp) {
		this.strBodyReviewFinalizationForEmp = strBodyReviewFinalizationForEmp;
	}

	public boolean getIsEmailLearningGapForHr() {
		return isEmailLearningGapForHr;
	}

	public void setIsEmailLearningGapForHr(boolean isEmailLearningGapForHr) {
		this.isEmailLearningGapForHr = isEmailLearningGapForHr;
	}

	public boolean getIsTextLearningGapForHr() {
		return isTextLearningGapForHr;
	}

	public void setIsTextLearningGapForHr(boolean isTextLearningGapForHr) {
		this.isTextLearningGapForHr = isTextLearningGapForHr;
	}

	public String getStrTextLearningGapForHr() {
		return strTextLearningGapForHr;
	}

	public void setStrTextLearningGapForHr(String strTextLearningGapForHr) {
		this.strTextLearningGapForHr = strTextLearningGapForHr;
	}

	public String getStrSubjectLearningGapForHr() {
		return strSubjectLearningGapForHr;
	}

	public void setStrSubjectLearningGapForHr(String strSubjectLearningGapForHr) {
		this.strSubjectLearningGapForHr = strSubjectLearningGapForHr;
	}

	public String getStrBodyLearningGapForHr() {
		return strBodyLearningGapForHr;
	}

	public void setStrBodyLearningGapForHr(String strBodyLearningGapForHr) {
		this.strBodyLearningGapForHr = strBodyLearningGapForHr;
	}

	public boolean getIsEmailManagerGoal() {
		return isEmailManagerGoal;
	}

	public void setIsEmailManagerGoal(boolean isEmailManagerGoal) {
		this.isEmailManagerGoal = isEmailManagerGoal;
	}

	public boolean getIsTextManagerGoal() {
		return isTextManagerGoal;
	}

	public void setIsTextManagerGoal(boolean isTextManagerGoal) {
		this.isTextManagerGoal = isTextManagerGoal;
	}

	public String getStrTextManagerGoal() {
		return strTextManagerGoal;
	}

	public void setStrTextManagerGoal(String strTextManagerGoal) {
		this.strTextManagerGoal = strTextManagerGoal;
	}

	public String getStrSubjectManagerGoal() {
		return strSubjectManagerGoal;
	}

	public void setStrSubjectManagerGoal(String strSubjectManagerGoal) {
		this.strSubjectManagerGoal = strSubjectManagerGoal;
	}

	public String getStrBodyManagerGoal() {
		return strBodyManagerGoal;
	}

	public void setStrBodyManagerGoal(String strBodyManagerGoal) {
		this.strBodyManagerGoal = strBodyManagerGoal;
	}

	public boolean getIsEmailExecutiveGoal() {
		return isEmailExecutiveGoal;
	}

	public void setIsEmailExecutiveGoal(boolean isEmailExecutiveGoal) {
		this.isEmailExecutiveGoal = isEmailExecutiveGoal;
	}

	public boolean getIsTextExecutiveGoal() {
		return isTextExecutiveGoal;
	}

	public void setIsTextExecutiveGoal(boolean isTextExecutiveGoal) {
		this.isTextExecutiveGoal = isTextExecutiveGoal;
	}

	public String getStrTextExecutiveGoal() {
		return strTextExecutiveGoal;
	}

	public void setStrTextExecutiveGoal(String strTextExecutiveGoal) {
		this.strTextExecutiveGoal = strTextExecutiveGoal;
	}

	public String getStrSubjectExecutiveGoal() {
		return strSubjectExecutiveGoal;
	}

	public void setStrSubjectExecutiveGoal(String strSubjectExecutiveGoal) {
		this.strSubjectExecutiveGoal = strSubjectExecutiveGoal;
	}

	public String getStrBodyExecutiveGoal() {
		return strBodyExecutiveGoal;
	}

	public void setStrBodyExecutiveGoal(String strBodyExecutiveGoal) {
		this.strBodyExecutiveGoal = strBodyExecutiveGoal;
	}

	public boolean getIsEmailExecutiveKRA() {
		return isEmailExecutiveKRA;
	}

	public void setIsEmailExecutiveKRA(boolean isEmailExecutiveKRA) {
		this.isEmailExecutiveKRA = isEmailExecutiveKRA;
	}

	public boolean getIsTextExecutiveKRA() {
		return isTextExecutiveKRA;
	}

	public void setIsTextExecutiveKRA(boolean isTextExecutiveKRA) {
		this.isTextExecutiveKRA = isTextExecutiveKRA;
	}

	public String getStrTextExecutiveKRA() {
		return strTextExecutiveKRA;
	}

	public void setStrTextExecutiveKRA(String strTextExecutiveKRA) {
		this.strTextExecutiveKRA = strTextExecutiveKRA;
	}

	public String getStrSubjectExecutiveKRA() {
		return strSubjectExecutiveKRA;
	}

	public void setStrSubjectExecutiveKRA(String strSubjectExecutiveKRA) {
		this.strSubjectExecutiveKRA = strSubjectExecutiveKRA;
	}

	public String getStrBodyExecutiveKRA() {
		return strBodyExecutiveKRA;
	}

	public void setStrBodyExecutiveKRA(String strBodyExecutiveKRA) {
		this.strBodyExecutiveKRA = strBodyExecutiveKRA;
	}

	public boolean getIsEmailExecutiveTarget() {
		return isEmailExecutiveTarget;
	}

	public void setIsEmailExecutiveTarget(boolean isEmailExecutiveTarget) {
		this.isEmailExecutiveTarget = isEmailExecutiveTarget;
	}

	public boolean getIsTextExecutiveTarget() {
		return isTextExecutiveTarget;
	}

	public void setIsTextExecutiveTarget(boolean isTextExecutiveTarget) {
		this.isTextExecutiveTarget = isTextExecutiveTarget;
	}

	public String getStrTextExecutiveTarget() {
		return strTextExecutiveTarget;
	}

	public void setStrTextExecutiveTarget(String strTextExecutiveTarget) {
		this.strTextExecutiveTarget = strTextExecutiveTarget;
	}

	public String getStrSubjectExecutiveTarget() {
		return strSubjectExecutiveTarget;
	}

	public void setStrSubjectExecutiveTarget(String strSubjectExecutiveTarget) {
		this.strSubjectExecutiveTarget = strSubjectExecutiveTarget;
	}

	public String getStrBodyExecutiveTarget() {
		return strBodyExecutiveTarget;
	}

	public void setStrBodyExecutiveTarget(String strBodyExecutiveTarget) {
		this.strBodyExecutiveTarget = strBodyExecutiveTarget;
	}

	public boolean getIsEmailLearningPlanForLearner() {
		return isEmailLearningPlanForLearner;
	}

	public void setIsEmailLearningPlanForLearner(boolean isEmailLearningPlanForLearner) {
		this.isEmailLearningPlanForLearner = isEmailLearningPlanForLearner;
	}

	public boolean getIsTextLearningPlanForLearner() {
		return isTextLearningPlanForLearner;
	}

	public void setIsTextLearningPlanForLearner(boolean isTextLearningPlanForLearner) {
		this.isTextLearningPlanForLearner = isTextLearningPlanForLearner;
	}

	public String getStrTextLearningPlanForLearner() {
		return strTextLearningPlanForLearner;
	}

	public void setStrTextLearningPlanForLearner(String strTextLearningPlanForLearner) {
		this.strTextLearningPlanForLearner = strTextLearningPlanForLearner;
	}

	public String getStrSubjectLearningPlanForLearner() {
		return strSubjectLearningPlanForLearner;
	}

	public void setStrSubjectLearningPlanForLearner(String strSubjectLearningPlanForLearner) {
		this.strSubjectLearningPlanForLearner = strSubjectLearningPlanForLearner;
	}

	public String getStrBodyLearningPlanForLearner() {
		return strBodyLearningPlanForLearner;
	}

	public void setStrBodyLearningPlanForLearner(String strBodyLearningPlanForLearner) {
		this.strBodyLearningPlanForLearner = strBodyLearningPlanForLearner;
	}

	public boolean getIsEmailLearningPlanForHr() {
		return isEmailLearningPlanForHr;
	}

	public void setIsEmailLearningPlanForHr(boolean isEmailLearningPlanForHr) {
		this.isEmailLearningPlanForHr = isEmailLearningPlanForHr;
	}

	public boolean getIsTextLearningPlanForHr() {
		return isTextLearningPlanForHr;
	}

	public void setIsTextLearningPlanForHr(boolean isTextLearningPlanForHr) {
		this.isTextLearningPlanForHr = isTextLearningPlanForHr;
	}

	public String getStrTextLearningPlanForHr() {
		return strTextLearningPlanForHr;
	}

	public void setStrTextLearningPlanForHr(String strTextLearningPlanForHr) {
		this.strTextLearningPlanForHr = strTextLearningPlanForHr;
	}

	public String getStrSubjectLearningPlanForHr() {
		return strSubjectLearningPlanForHr;
	}

	public void setStrSubjectLearningPlanForHr(String strSubjectLearningPlanForHr) {
		this.strSubjectLearningPlanForHr = strSubjectLearningPlanForHr;
	}

	public String getStrBodyLearningPlanForHr() {
		return strBodyLearningPlanForHr;
	}

	public void setStrBodyLearningPlanForHr(String strBodyLearningPlanForHr) {
		this.strBodyLearningPlanForHr = strBodyLearningPlanForHr;
	}

	public boolean getIsEmailLTrainingFinalizeForHr() {
		return isEmailLTrainingFinalizeForHr;
	}

	public void setIsEmailLTrainingFinalizeForHr(boolean isEmailLTrainingFinalizeForHr) {
		this.isEmailLTrainingFinalizeForHr = isEmailLTrainingFinalizeForHr;
	}

	public boolean getIsTextLTrainingFinalizeForHr() {
		return isTextLTrainingFinalizeForHr;
	}

	public void setIsTextLTrainingFinalizeForHr(boolean isTextLTrainingFinalizeForHr) {
		this.isTextLTrainingFinalizeForHr = isTextLTrainingFinalizeForHr;
	}

	public String getStrTextLTrainingFinalizeForHr() {
		return strTextLTrainingFinalizeForHr;
	}

	public void setStrTextLTrainingFinalizeForHr(String strTextLTrainingFinalizeForHr) {
		this.strTextLTrainingFinalizeForHr = strTextLTrainingFinalizeForHr;
	}

	public String getStrSubjectLTrainingFinalizeForHr() {
		return strSubjectLTrainingFinalizeForHr;
	}

	public void setStrSubjectLTrainingFinalizeForHr(String strSubjectLTrainingFinalizeForHr) {
		this.strSubjectLTrainingFinalizeForHr = strSubjectLTrainingFinalizeForHr;
	}

	public String getStrBodyLTrainingFinalizeForHr() {
		return strBodyLTrainingFinalizeForHr;
	}

	public void setStrBodyLTrainingFinalizeForHr(String strBodyLTrainingFinalizeForHr) {
		this.strBodyLTrainingFinalizeForHr = strBodyLTrainingFinalizeForHr;
	}

	public boolean getIsEmailLAssessFinalizeForHr() {
		return isEmailLAssessFinalizeForHr;
	}

	public void setIsEmailLAssessFinalizeForHr(boolean isEmailLAssessFinalizeForHr) {
		this.isEmailLAssessFinalizeForHr = isEmailLAssessFinalizeForHr;
	}

	public boolean getIsTextLAssessFinalizeForHr() {
		return isTextLAssessFinalizeForHr;
	}

	public void setIsTextLAssessFinalizeForHr(boolean isTextLAssessFinalizeForHr) {
		this.isTextLAssessFinalizeForHr = isTextLAssessFinalizeForHr;
	}

	public String getStrTextLAssessFinalizeForHr() {
		return strTextLAssessFinalizeForHr;
	}

	public void setStrTextLAssessFinalizeForHr(String strTextLAssessFinalizeForHr) {
		this.strTextLAssessFinalizeForHr = strTextLAssessFinalizeForHr;
	}

	public String getStrSubjectLAssessFinalizeForHr() {
		return strSubjectLAssessFinalizeForHr;
	}

	public void setStrSubjectLAssessFinalizeForHr(String strSubjectLAssessFinalizeForHr) {
		this.strSubjectLAssessFinalizeForHr = strSubjectLAssessFinalizeForHr;
	}

	public String getStrBodyLAssessFinalizeForHr() {
		return strBodyLAssessFinalizeForHr;
	}

	public void setStrBodyLAssessFinalizeForHr(String strBodyLAssessFinalizeForHr) {
		this.strBodyLAssessFinalizeForHr = strBodyLAssessFinalizeForHr;
	}

	public boolean getIsEmailLTrainingFeedbackTrainer() {
		return isEmailLTrainingFeedbackTrainer;
	}

	public void setIsEmailLTrainingFeedbackTrainer(boolean isEmailLTrainingFeedbackTrainer) {
		this.isEmailLTrainingFeedbackTrainer = isEmailLTrainingFeedbackTrainer;
	}

	public boolean getIsTextLTrainingFeedbackTrainer() {
		return isTextLTrainingFeedbackTrainer;
	}

	public void setIsTextLTrainingFeedbackTrainer(boolean isTextLTrainingFeedbackTrainer) {
		this.isTextLTrainingFeedbackTrainer = isTextLTrainingFeedbackTrainer;
	}

	public String getStrTextLTrainingFeedbackTrainer() {
		return strTextLTrainingFeedbackTrainer;
	}

	public void setStrTextLTrainingFeedbackTrainer(String strTextLTrainingFeedbackTrainer) {
		this.strTextLTrainingFeedbackTrainer = strTextLTrainingFeedbackTrainer;
	}

	public String getStrSubjectLTrainingFeedbackTrainer() {
		return strSubjectLTrainingFeedbackTrainer;
	}

	public void setStrSubjectLTrainingFeedbackTrainer(String strSubjectLTrainingFeedbackTrainer) {
		this.strSubjectLTrainingFeedbackTrainer = strSubjectLTrainingFeedbackTrainer;
	}

	public String getStrBodyLTrainingFeedbackTrainer() {
		return strBodyLTrainingFeedbackTrainer;
	}

	public void setStrBodyLTrainingFeedbackTrainer(String strBodyLTrainingFeedbackTrainer) {
		this.strBodyLTrainingFeedbackTrainer = strBodyLTrainingFeedbackTrainer;
	}

	public boolean getIsEmailLTrainingFeedbackLearner() {
		return isEmailLTrainingFeedbackLearner;
	}

	public void setIsEmailLTrainingFeedbackLearner(boolean isEmailLTrainingFeedbackLearner) {
		this.isEmailLTrainingFeedbackLearner = isEmailLTrainingFeedbackLearner;
	}

	public boolean getIsTextLTrainingFeedbackLearner() {
		return isTextLTrainingFeedbackLearner;
	}

	public void setIsTextLTrainingFeedbackLearner(boolean isTextLTrainingFeedbackLearner) {
		this.isTextLTrainingFeedbackLearner = isTextLTrainingFeedbackLearner;
	}

	public String getStrTextLTrainingFeedbackLearner() {
		return strTextLTrainingFeedbackLearner;
	}

	public void setStrTextLTrainingFeedbackLearner(String strTextLTrainingFeedbackLearner) {
		this.strTextLTrainingFeedbackLearner = strTextLTrainingFeedbackLearner;
	}

	public String getStrSubjectLTrainingFeedbackLearner() {
		return strSubjectLTrainingFeedbackLearner;
	}

	public void setStrSubjectLTrainingFeedbackLearner(String strSubjectLTrainingFeedbackLearner) {
		this.strSubjectLTrainingFeedbackLearner = strSubjectLTrainingFeedbackLearner;
	}

	public String getStrBodyLTrainingFeedbackLearner() {
		return strBodyLTrainingFeedbackLearner;
	}

	public void setStrBodyLTrainingFeedbackLearner(String strBodyLTrainingFeedbackLearner) {
		this.strBodyLTrainingFeedbackLearner = strBodyLTrainingFeedbackLearner;
	}

	public boolean getIsEmailRecruitmentRequest() {
		return isEmailRecruitmentRequest;
	}

	public void setIsEmailRecruitmentRequest(boolean isEmailRecruitmentRequest) {
		this.isEmailRecruitmentRequest = isEmailRecruitmentRequest;
	}

	public boolean getIsTextRecruitmentRequest() {
		return isTextRecruitmentRequest;
	}

	public void setIsTextRecruitmentRequest(boolean isTextRecruitmentRequest) {
		this.isTextRecruitmentRequest = isTextRecruitmentRequest;
	}

	public String getStrTextRecruitmentRequest() {
		return strTextRecruitmentRequest;
	}

	public void setStrTextRecruitmentRequest(String strTextRecruitmentRequest) {
		this.strTextRecruitmentRequest = strTextRecruitmentRequest;
	}

	public String getStrSubjectRecruitmentRequest() {
		return strSubjectRecruitmentRequest;
	}

	public void setStrSubjectRecruitmentRequest(String strSubjectRecruitmentRequest) {
		this.strSubjectRecruitmentRequest = strSubjectRecruitmentRequest;
	}

	public String getStrBodyRecruitmentRequest() {
		return strBodyRecruitmentRequest;
	}

	public void setStrBodyRecruitmentRequest(String strBodyRecruitmentRequest) {
		this.strBodyRecruitmentRequest = strBodyRecruitmentRequest;
	}


	public boolean getIsEmailNewRecruitmentToEmp() {
		return isEmailNewRecruitmentToEmp;
	}


	public void setIsEmailNewRecruitmentToEmp(boolean isEmailNewRecruitmentToEmp) {
		this.isEmailNewRecruitmentToEmp = isEmailNewRecruitmentToEmp;
	}


	public boolean getIsTextNewRecruitmentToEmp() {
		return isTextNewRecruitmentToEmp;
	}


	public void setIsTextNewRecruitmentToEmp(boolean isTextNewRecruitmentToEmp) {
		this.isTextNewRecruitmentToEmp = isTextNewRecruitmentToEmp;
	}

	public String getStrTextNewRecruitmentToEmp() {
		return strTextNewRecruitmentToEmp;
	}

	public void setStrTextNewRecruitmentToEmp(String strTextNewRecruitmentToEmp) {
		this.strTextNewRecruitmentToEmp = strTextNewRecruitmentToEmp;
	}

	public String getStrSubjectNewRecruitmentToEmp() {
		return strSubjectNewRecruitmentToEmp;
	}

	public void setStrSubjectNewRecruitmentToEmp(String strSubjectNewRecruitmentToEmp) {
		this.strSubjectNewRecruitmentToEmp = strSubjectNewRecruitmentToEmp;
	}

	public String getStrBodyNewRecruitmentToEmp() {
		return strBodyNewRecruitmentToEmp;
	}

	public void setStrBodyNewRecruitmentToEmp(String strBodyNewRecruitmentToEmp) {
		this.strBodyNewRecruitmentToEmp = strBodyNewRecruitmentToEmp;
	}

	public boolean getIsEmailRecruitmentApproval() {
		return isEmailRecruitmentApproval;
	}

	public void setIsEmailRecruitmentApproval(boolean isEmailRecruitmentApproval) {
		this.isEmailRecruitmentApproval = isEmailRecruitmentApproval;
	}

	public boolean getIsTextRecruitmentApproval() {
		return isTextRecruitmentApproval;
	}

	public void setIsTextRecruitmentApproval(boolean isTextRecruitmentApproval) {
		this.isTextRecruitmentApproval = isTextRecruitmentApproval;
	}

	public String getStrTextRecruitmentApproval() {
		return strTextRecruitmentApproval;
	}

	public void setStrTextRecruitmentApproval(String strTextRecruitmentApproval) {
		this.strTextRecruitmentApproval = strTextRecruitmentApproval;
	}

	public String getStrSubjectRecruitmentApproval() {
		return strSubjectRecruitmentApproval;
	}

	public void setStrSubjectRecruitmentApproval(String strSubjectRecruitmentApproval) {
		this.strSubjectRecruitmentApproval = strSubjectRecruitmentApproval;
	}

	public String getStrBodyRecruitmentApproval() {
		return strBodyRecruitmentApproval;
	}

	public void setStrBodyRecruitmentApproval(String strBodyRecruitmentApproval) {
		this.strBodyRecruitmentApproval = strBodyRecruitmentApproval;
	}

	public boolean getIsEmailApplicationShortlist() {
		return isEmailApplicationShortlist;
	}

	public void setIsEmailApplicationShortlist(boolean isEmailApplicationShortlist) {
		this.isEmailApplicationShortlist = isEmailApplicationShortlist;
	}

	public boolean getIsTextApplicationShortlist() {
		return isTextApplicationShortlist;
	}

	public void setIsTextApplicationShortlist(boolean isTextApplicationShortlist) {
		this.isTextApplicationShortlist = isTextApplicationShortlist;
	}

	public String getStrTextApplicationShortlist() {
		return strTextApplicationShortlist;
	}

	public void setStrTextApplicationShortlist(String strTextApplicationShortlist) {
		this.strTextApplicationShortlist = strTextApplicationShortlist;
	}

	public String getStrSubjectApplicationShortlist() {
		return strSubjectApplicationShortlist;
	}

	public void setStrSubjectApplicationShortlist(String strSubjectApplicationShortlist) {
		this.strSubjectApplicationShortlist = strSubjectApplicationShortlist;
	}

	public String getStrBodyApplicationShortlist() {
		return strBodyApplicationShortlist;
	}

	public void setStrBodyApplicationShortlist(String strBodyApplicationShortlist) {
		this.strBodyApplicationShortlist = strBodyApplicationShortlist;
	}

	public boolean getIsEmailCandiSpecifyOtherDate() {
		return isEmailCandiSpecifyOtherDate;
	}

	public void setIsEmailCandiSpecifyOtherDate(boolean isEmailCandiSpecifyOtherDate) {
		this.isEmailCandiSpecifyOtherDate = isEmailCandiSpecifyOtherDate;
	}

	public boolean getIsTextCandiSpecifyOtherDate() {
		return isTextCandiSpecifyOtherDate;
	}

	public void setIsTextCandiSpecifyOtherDate(boolean isTextCandiSpecifyOtherDate) {
		this.isTextCandiSpecifyOtherDate = isTextCandiSpecifyOtherDate;
	}

	public String getStrTextCandiSpecifyOtherDate() {
		return strTextCandiSpecifyOtherDate;
	}

	public void setStrTextCandiSpecifyOtherDate(String strTextCandiSpecifyOtherDate) {
		this.strTextCandiSpecifyOtherDate = strTextCandiSpecifyOtherDate;
	}

	public String getStrSubjectCandiSpecifyOtherDate() {
		return strSubjectCandiSpecifyOtherDate;
	}

	public void setStrSubjectCandiSpecifyOtherDate(String strSubjectCandiSpecifyOtherDate) {
		this.strSubjectCandiSpecifyOtherDate = strSubjectCandiSpecifyOtherDate;
	}

	public String getStrBodyCandiSpecifyOtherDate() {
		return strBodyCandiSpecifyOtherDate;
	}

	public void setStrBodyCandiSpecifyOtherDate(String strBodyCandiSpecifyOtherDate) {
		this.strBodyCandiSpecifyOtherDate = strBodyCandiSpecifyOtherDate;
	}

	public boolean getIsEmailCandiJoiningOfferCTC() {
		return isEmailCandiJoiningOfferCTC;
	}

	public void setIsEmailCandiJoiningOfferCTC(boolean isEmailCandiJoiningOfferCTC) {
		this.isEmailCandiJoiningOfferCTC = isEmailCandiJoiningOfferCTC;
	}

	public boolean getIsTextCandiJoiningOfferCTC() {
		return isTextCandiJoiningOfferCTC;
	}

	public void setIsTextCandiJoiningOfferCTC(boolean isTextCandiJoiningOfferCTC) {
		this.isTextCandiJoiningOfferCTC = isTextCandiJoiningOfferCTC;
	}

	public String getStrTextCandiJoiningOfferCTC() {
		return strTextCandiJoiningOfferCTC;
	}

	public void setStrTextCandiJoiningOfferCTC(String strTextCandiJoiningOfferCTC) {
		this.strTextCandiJoiningOfferCTC = strTextCandiJoiningOfferCTC;
	}

	public String getStrSubjectCandiJoiningOfferCTC() {
		return strSubjectCandiJoiningOfferCTC;
	}

	public void setStrSubjectCandiJoiningOfferCTC(String strSubjectCandiJoiningOfferCTC) {
		this.strSubjectCandiJoiningOfferCTC = strSubjectCandiJoiningOfferCTC;
	}

	public String getStrBodyCandiJoiningOfferCTC() {
		return strBodyCandiJoiningOfferCTC;
	}

	public void setStrBodyCandiJoiningOfferCTC(String strBodyCandiJoiningOfferCTC) {
		this.strBodyCandiJoiningOfferCTC = strBodyCandiJoiningOfferCTC;
	}

	public boolean getIsEmailCandiInterviewDate() {
		return isEmailCandiInterviewDate;
	}

	public void setIsEmailCandiInterviewDate(boolean isEmailCandiInterviewDate) {
		this.isEmailCandiInterviewDate = isEmailCandiInterviewDate;
	}

	public boolean getIsTextCandiInterviewDate() {
		return isTextCandiInterviewDate;
	}

	public void setIsTextCandiInterviewDate(boolean isTextCandiInterviewDate) {
		this.isTextCandiInterviewDate = isTextCandiInterviewDate;
	}

	public String getStrTextCandiInterviewDate() {
		return strTextCandiInterviewDate;
	}

	public void setStrTextCandiInterviewDate(String strTextCandiInterviewDate) {
		this.strTextCandiInterviewDate = strTextCandiInterviewDate;
	}

	public String getStrSubjectCandiInterviewDate() {
		return strSubjectCandiInterviewDate;
	}

	public void setStrSubjectCandiInterviewDate(String strSubjectCandiInterviewDate) {
		this.strSubjectCandiInterviewDate = strSubjectCandiInterviewDate;
	}

	public String getStrBodyCandiInterviewDate() {
		return strBodyCandiInterviewDate;
	}

	public void setStrBodyCandiInterviewDate(String strBodyCandiInterviewDate) {
		this.strBodyCandiInterviewDate = strBodyCandiInterviewDate;
	}

	public boolean getIsEmailPanelistInterviewDate() {
		return isEmailPanelistInterviewDate;
	}

	public void setIsEmailPanelistInterviewDate(boolean isEmailPanelistInterviewDate) {
		this.isEmailPanelistInterviewDate = isEmailPanelistInterviewDate;
	}

	public boolean getIsTextPanelistInterviewDate() {
		return isTextPanelistInterviewDate;
	}

	public void setIsTextPanelistInterviewDate(boolean isTextPanelistInterviewDate) {
		this.isTextPanelistInterviewDate = isTextPanelistInterviewDate;
	}

	public String getStrTextPanelistInterviewDate() {
		return strTextPanelistInterviewDate;
	}

	public void setStrTextPanelistInterviewDate(String strTextPanelistInterviewDate) {
		this.strTextPanelistInterviewDate = strTextPanelistInterviewDate;
	}

	public String getStrSubjectPanelistInterviewDate() {
		return strSubjectPanelistInterviewDate;
	}

	public void setStrSubjectPanelistInterviewDate(String strSubjectPanelistInterviewDate) {
		this.strSubjectPanelistInterviewDate = strSubjectPanelistInterviewDate;
	}

	public String getStrBodyPanelistInterviewDate() {
		return strBodyPanelistInterviewDate;
	}

	public void setStrBodyPanelistInterviewDate(String strBodyPanelistInterviewDate) {
		this.strBodyPanelistInterviewDate = strBodyPanelistInterviewDate;
	}

	public boolean getIsEmailCandiOnboarding() {
		return isEmailCandiOnboarding;
	}

	public void setIsEmailCandiOnboarding(boolean isEmailCandiOnboarding) {
		this.isEmailCandiOnboarding = isEmailCandiOnboarding;
	}

	public boolean getIsTextCandiOnboarding() {
		return isTextCandiOnboarding;
	}

	public void setIsTextCandiOnboarding(boolean isTextCandiOnboarding) {
		this.isTextCandiOnboarding = isTextCandiOnboarding;
	}

	public String getStrTextCandiOnboarding() {
		return strTextCandiOnboarding;
	}

	public void setStrTextCandiOnboarding(String strTextCandiOnboarding) {
		this.strTextCandiOnboarding = strTextCandiOnboarding;
	}

	public String getStrSubjectCandiOnboarding() {
		return strSubjectCandiOnboarding;
	}

	public void setStrSubjectCandiOnboarding(String strSubjectCandiOnboarding) {
		this.strSubjectCandiOnboarding = strSubjectCandiOnboarding;
	}

	public String getStrBodyCandiOnboarding() {
		return strBodyCandiOnboarding;
	}

	public void setStrBodyCandiOnboarding(String strBodyCandiOnboarding) {
		this.strBodyCandiOnboarding = strBodyCandiOnboarding;
	}

	public boolean getIsEmailCandiEmpLoginDetail() {
		return isEmailCandiEmpLoginDetail;
	}

	public void setIsEmailCandiEmpLoginDetail(boolean isEmailCandiEmpLoginDetail) {
		this.isEmailCandiEmpLoginDetail = isEmailCandiEmpLoginDetail;
	}

	public boolean getIsTextCandiEmpLoginDetail() {
		return isTextCandiEmpLoginDetail;
	}

	public void setIsTextCandiEmpLoginDetail(boolean isTextCandiEmpLoginDetail) {
		this.isTextCandiEmpLoginDetail = isTextCandiEmpLoginDetail;
	}

	public String getStrTextCandiEmpLoginDetail() {
		return strTextCandiEmpLoginDetail;
	}

	public void setStrTextCandiEmpLoginDetail(String strTextCandiEmpLoginDetail) {
		this.strTextCandiEmpLoginDetail = strTextCandiEmpLoginDetail;
	}

	public String getStrSubjectCandiEmpLoginDetail() {
		return strSubjectCandiEmpLoginDetail;
	}

	public void setStrSubjectCandiEmpLoginDetail(String strSubjectCandiEmpLoginDetail) {
		this.strSubjectCandiEmpLoginDetail = strSubjectCandiEmpLoginDetail;
	}

	public String getStrBodyCandiEmpLoginDetail() {
		return strBodyCandiEmpLoginDetail;
	}

	public void setStrBodyCandiEmpLoginDetail(String strBodyCandiEmpLoginDetail) {
		this.strBodyCandiEmpLoginDetail = strBodyCandiEmpLoginDetail;
	}

	public boolean getIsEmailFillCandiInfoByCandi() {
		return isEmailFillCandiInfoByCandi;
	}

	public void setIsEmailFillCandiInfoByCandi(boolean isEmailFillCandiInfoByCandi) {
		this.isEmailFillCandiInfoByCandi = isEmailFillCandiInfoByCandi;
	}

	public boolean getIsTextFillCandiInfoByCandi() {
		return isTextFillCandiInfoByCandi;
	}

	public void setIsTextFillCandiInfoByCandi(boolean isTextFillCandiInfoByCandi) {
		this.isTextFillCandiInfoByCandi = isTextFillCandiInfoByCandi;
	}

	public String getStrTextFillCandiInfoByCandi() {
		return strTextFillCandiInfoByCandi;
	}

	public void setStrTextFillCandiInfoByCandi(String strTextFillCandiInfoByCandi) {
		this.strTextFillCandiInfoByCandi = strTextFillCandiInfoByCandi;
	}

	public String getStrSubjectFillCandiInfoByCandi() {
		return strSubjectFillCandiInfoByCandi;
	}

	public void setStrSubjectFillCandiInfoByCandi(String strSubjectFillCandiInfoByCandi) {
		this.strSubjectFillCandiInfoByCandi = strSubjectFillCandiInfoByCandi;
	}

	public String getStrBodyFillCandiInfoByCandi() {
		return strBodyFillCandiInfoByCandi;
	}

	public void setStrBodyFillCandiInfoByCandi(String strBodyFillCandiInfoByCandi) {
		this.strBodyFillCandiInfoByCandi = strBodyFillCandiInfoByCandi;
	}
	
	
	public boolean getIsEmailCandiBackgroudVerification() {
		return isEmailCandiBackgroudVerification;
	}

	public void setIsEmailCandiBackgroudVerification(boolean isEmailCandiBackgroudVerification) {
		this.isEmailCandiBackgroudVerification = isEmailCandiBackgroudVerification;
	}

	public boolean getIsTextCandiBackgroudVerification() {
		return isTextCandiBackgroudVerification;
	}

	public void setIsTextCandiBackgroudVerification(boolean isTextCandiBackgroudVerification) {
		this.isTextCandiBackgroudVerification = isTextCandiBackgroudVerification;
	}

	public String getStrTextCandiBackgroudVerification() {
		return strTextCandiBackgroudVerification;
	}

	public void setStrTextCandiBackgroudVerification(String strTextCandiBackgroudVerification) {
		this.strTextCandiBackgroudVerification = strTextCandiBackgroudVerification;
	}

	public String getStrSubjectCandiBackgroudVerification() {
		return strSubjectCandiBackgroudVerification;
	}

	public void setStrSubjectCandiBackgroudVerification(String strSubjectCandiBackgroudVerification) {
		this.strSubjectCandiBackgroudVerification = strSubjectCandiBackgroudVerification;
	}

	public String getStrBodyCandiBackgroudVerification() {
		return strBodyCandiBackgroudVerification;
	}

	public void setStrBodyCandiBackgroudVerification(String strBodyCandiBackgroudVerification) {
		this.strBodyCandiBackgroudVerification = strBodyCandiBackgroudVerification;
	}

	public boolean getIsEmailRecruitmentRequestUpdate() {
		return isEmailRecruitmentRequestUpdate;
	}

	public void setIsEmailRecruitmentRequestUpdate(boolean isEmailRecruitmentRequestUpdate) {
		this.isEmailRecruitmentRequestUpdate = isEmailRecruitmentRequestUpdate;
	}

	public boolean getIsTextRecruitmentRequestUpdate() {
		return isTextRecruitmentRequestUpdate;
	}

	public void setIsTextRecruitmentRequestUpdate(boolean isTextRecruitmentRequestUpdate) {
		this.isTextRecruitmentRequestUpdate = isTextRecruitmentRequestUpdate;
	}

	public String getStrTextRecruitmentRequestUpdate() {
		return strTextRecruitmentRequestUpdate;
	}

	public void setStrTextRecruitmentRequestUpdate(String strTextRecruitmentRequestUpdate) {
		this.strTextRecruitmentRequestUpdate = strTextRecruitmentRequestUpdate;
	}

	public String getStrSubjectRecruitmentRequestUpdate() {
		return strSubjectRecruitmentRequestUpdate;
	}

	public void setStrSubjectRecruitmentRequestUpdate(String strSubjectRecruitmentRequestUpdate) {
		this.strSubjectRecruitmentRequestUpdate = strSubjectRecruitmentRequestUpdate;
	}

	public String getStrBodyRecruitmentRequestUpdate() {
		return strBodyRecruitmentRequestUpdate;
	}

	public void setStrBodyRecruitmentRequestUpdate(String strBodyRecruitmentRequestUpdate) {
		this.strBodyRecruitmentRequestUpdate = strBodyRecruitmentRequestUpdate;
	}

	public boolean getIsEmailCandiOfferAcceptReject() {
		return isEmailCandiOfferAcceptReject;
	}

	public void setIsEmailCandiOfferAcceptReject(boolean isEmailCandiOfferAcceptReject) {
		this.isEmailCandiOfferAcceptReject = isEmailCandiOfferAcceptReject;
	}

	public boolean getIsTextCandiOfferAcceptReject() {
		return isTextCandiOfferAcceptReject;
	}

	public void setIsTextCandiOfferAcceptReject(boolean isTextCandiOfferAcceptReject) {
		this.isTextCandiOfferAcceptReject = isTextCandiOfferAcceptReject;
	}

	public String getStrTextCandiOfferAcceptReject() {
		return strTextCandiOfferAcceptReject;
	}

	public void setStrTextCandiOfferAcceptReject(String strTextCandiOfferAcceptReject) {
		this.strTextCandiOfferAcceptReject = strTextCandiOfferAcceptReject;
	}

	public String getStrSubjectCandiOfferAcceptReject() {
		return strSubjectCandiOfferAcceptReject;
	}

	public void setStrSubjectCandiOfferAcceptReject(String strSubjectCandiOfferAcceptReject) {
		this.strSubjectCandiOfferAcceptReject = strSubjectCandiOfferAcceptReject;
	}

	public String getStrBodyCandiOfferAcceptReject() {
		return strBodyCandiOfferAcceptReject;
	}

	public void setStrBodyCandiOfferAcceptReject(String strBodyCandiOfferAcceptReject) {
		this.strBodyCandiOfferAcceptReject = strBodyCandiOfferAcceptReject;
	}

	public boolean getIsEmailNewEmpOnboardingToHr() {
		return isEmailNewEmpOnboardingToHr;
	}

	public void setIsEmailNewEmpOnboardingToHr(boolean isEmailNewEmpOnboardingToHr) {
		this.isEmailNewEmpOnboardingToHr = isEmailNewEmpOnboardingToHr;
	}

	public boolean getIsTextNewEmpOnboardingToHr() {
		return isTextNewEmpOnboardingToHr;
	}

	public void setIsTextNewEmpOnboardingToHr(boolean isTextNewEmpOnboardingToHr) {
		this.isTextNewEmpOnboardingToHr = isTextNewEmpOnboardingToHr;
	}

	public String getStrTextNewEmpOnboardingToHr() {
		return strTextNewEmpOnboardingToHr;
	}

	public void setStrTextNewEmpOnboardingToHr(String strTextNewEmpOnboardingToHr) {
		this.strTextNewEmpOnboardingToHr = strTextNewEmpOnboardingToHr;
	}

	public String getStrSubjectNewEmpOnboardingToHr() {
		return strSubjectNewEmpOnboardingToHr;
	}

	public void setStrSubjectNewEmpOnboardingToHr(String strSubjectNewEmpOnboardingToHr) {
		this.strSubjectNewEmpOnboardingToHr = strSubjectNewEmpOnboardingToHr;
	}

	public String getStrBodyNewEmpOnboardingToHr() {
		return strBodyNewEmpOnboardingToHr;
	}

	public void setStrBodyNewEmpOnboardingToHr(String strBodyNewEmpOnboardingToHr) {
		this.strBodyNewEmpOnboardingToHr = strBodyNewEmpOnboardingToHr;
	}

	public boolean getIsEmailRecruitmentRequestDeny() {
		return isEmailRecruitmentRequestDeny;
	}

	public void setIsEmailRecruitmentRequestDeny(boolean isEmailRecruitmentRequestDeny) {
		this.isEmailRecruitmentRequestDeny = isEmailRecruitmentRequestDeny;
	}

	public boolean getIsTextRecruitmentRequestDeny() {
		return isTextRecruitmentRequestDeny;
	}

	public void setIsTextRecruitmentRequestDeny(boolean isTextRecruitmentRequestDeny) {
		this.isTextRecruitmentRequestDeny = isTextRecruitmentRequestDeny;
	}

	public String getStrTextRecruitmentRequestDeny() {
		return strTextRecruitmentRequestDeny;
	}

	public void setStrTextRecruitmentRequestDeny(String strTextRecruitmentRequestDeny) {
		this.strTextRecruitmentRequestDeny = strTextRecruitmentRequestDeny;
	}

	public String getStrSubjectRecruitmentRequestDeny() {
		return strSubjectRecruitmentRequestDeny;
	}

	public void setStrSubjectRecruitmentRequestDeny(String strSubjectRecruitmentRequestDeny) {
		this.strSubjectRecruitmentRequestDeny = strSubjectRecruitmentRequestDeny;
	}

	public String getStrBodyRecruitmentRequestDeny() {
		return strBodyRecruitmentRequestDeny;
	}

	public void setStrBodyRecruitmentRequestDeny(String strBodyRecruitmentRequestDeny) {
		this.strBodyRecruitmentRequestDeny = strBodyRecruitmentRequestDeny;
	}

	public boolean getIsEmailJobProfileApproval() {
		return isEmailJobProfileApproval;
	}

	public void setIsEmailJobProfileApproval(boolean isEmailJobProfileApproval) {
		this.isEmailJobProfileApproval = isEmailJobProfileApproval;
	}

	public boolean getIsTextJobProfileApproval() {
		return isTextJobProfileApproval;
	}

	public void setIsTextJobProfileApproval(boolean isTextJobProfileApproval) {
		this.isTextJobProfileApproval = isTextJobProfileApproval;
	}

	public String getStrTextJobProfileApproval() {
		return strTextJobProfileApproval;
	}

	public void setStrTextJobProfileApproval(String strTextJobProfileApproval) {
		this.strTextJobProfileApproval = strTextJobProfileApproval;
	}

	public String getStrSubjectJobProfileApproval() {
		return strSubjectJobProfileApproval;
	}

	public void setStrSubjectJobProfileApproval(String strSubjectJobProfileApproval) {
		this.strSubjectJobProfileApproval = strSubjectJobProfileApproval;
	}

	public String getStrBodyJobProfileApproval() {
		return strBodyJobProfileApproval;
	}

	public void setStrBodyJobProfileApproval(String strBodyJobProfileApproval) {
		this.strBodyJobProfileApproval = strBodyJobProfileApproval;
	}

	public boolean getIsEmailJobProfileDeny() {
		return isEmailJobProfileDeny;
	}

	public void setIsEmailJobProfileDeny(boolean isEmailJobProfileDeny) {
		this.isEmailJobProfileDeny = isEmailJobProfileDeny;
	}

	public boolean getIsTextJobProfileDeny() {
		return isTextJobProfileDeny;
	}

	public void setIsTextJobProfileDeny(boolean isTextJobProfileDeny) {
		this.isTextJobProfileDeny = isTextJobProfileDeny;
	}

	public String getStrTextJobProfileDeny() {
		return strTextJobProfileDeny;
	}

	public void setStrTextJobProfileDeny(String strTextJobProfileDeny) {
		this.strTextJobProfileDeny = strTextJobProfileDeny;
	}

	public String getStrSubjectJobProfileDeny() {
		return strSubjectJobProfileDeny;
	}

	public void setStrSubjectJobProfileDeny(String strSubjectJobProfileDeny) {
		this.strSubjectJobProfileDeny = strSubjectJobProfileDeny;
	}

	public String getStrBodyJobProfileDeny() {
		return strBodyJobProfileDeny;
	}

	public void setStrBodyJobProfileDeny(String strBodyJobProfileDeny) {
		this.strBodyJobProfileDeny = strBodyJobProfileDeny;
	}

	public boolean getIsEmailJobProfileUpdate() {
		return isEmailJobProfileUpdate;
	}

	public void setIsEmailJobProfileUpdate(boolean isEmailJobProfileUpdate) {
		this.isEmailJobProfileUpdate = isEmailJobProfileUpdate;
	}

	public boolean getIsTextJobProfileUpdate() {
		return isTextJobProfileUpdate;
	}

	public void setIsTextJobProfileUpdate(boolean isTextJobProfileUpdate) {
		this.isTextJobProfileUpdate = isTextJobProfileUpdate;
	}

	public String getStrTextJobProfileUpdate() {
		return strTextJobProfileUpdate;
	}

	public void setStrTextJobProfileUpdate(String strTextJobProfileUpdate) {
		this.strTextJobProfileUpdate = strTextJobProfileUpdate;
	}

	public String getStrSubjectJobProfileUpdate() {
		return strSubjectJobProfileUpdate;
	}

	public void setStrSubjectJobProfileUpdate(String strSubjectJobProfileUpdate) {
		this.strSubjectJobProfileUpdate = strSubjectJobProfileUpdate;
	}

	public String getStrBodyJobProfileUpdate() {
		return strBodyJobProfileUpdate;
	}

	public void setStrBodyJobProfileUpdate(String strBodyJobProfileUpdate) {
		this.strBodyJobProfileUpdate = strBodyJobProfileUpdate;
	}

	public boolean getIsEmailNewJobToConsultant() {
		return isEmailNewJobToConsultant;
	}

	public void setIsEmailNewJobToConsultant(boolean isEmailNewJobToConsultant) {
		this.isEmailNewJobToConsultant = isEmailNewJobToConsultant;
	}

	public boolean getIsTextNewJobToConsultant() {
		return isTextNewJobToConsultant;
	}

	public void setIsTextNewJobToConsultant(boolean isTextNewJobToConsultant) {
		this.isTextNewJobToConsultant = isTextNewJobToConsultant;
	}

	public String getStrTextNewJobToConsultant() {
		return strTextNewJobToConsultant;
	}

	public void setStrTextNewJobToConsultant(String strTextNewJobToConsultant) {
		this.strTextNewJobToConsultant = strTextNewJobToConsultant;
	}

	public String getStrSubjectNewJobToConsultant() {
		return strSubjectNewJobToConsultant;
	}

	public void setStrSubjectNewJobToConsultant(String strSubjectNewJobToConsultant) {
		this.strSubjectNewJobToConsultant = strSubjectNewJobToConsultant;
	}

	public String getStrBodyNewJobToConsultant() {
		return strBodyNewJobToConsultant;
	}

	public void setStrBodyNewJobToConsultant(String strBodyNewJobToConsultant) {
		this.strBodyNewJobToConsultant = strBodyNewJobToConsultant;
	}

	public boolean getIsEmailCandiShortlistFromConsultant() {
		return isEmailCandiShortlistFromConsultant;
	}

	public void setIsEmailCandiShortlistFromConsultant(boolean isEmailCandiShortlistFromConsultant) {
		this.isEmailCandiShortlistFromConsultant = isEmailCandiShortlistFromConsultant;
	}

	public boolean getIsTextCandiShortlistFromConsultant() {
		return isTextCandiShortlistFromConsultant;
	}

	public void setIsTextCandiShortlistFromConsultant(boolean isTextCandiShortlistFromConsultant) {
		this.isTextCandiShortlistFromConsultant = isTextCandiShortlistFromConsultant;
	}

	public String getStrTextCandiShortlistFromConsultant() {
		return strTextCandiShortlistFromConsultant;
	}

	public void setStrTextCandiShortlistFromConsultant(String strTextCandiShortlistFromConsultant) {
		this.strTextCandiShortlistFromConsultant = strTextCandiShortlistFromConsultant;
	}

	public String getStrSubjectCandiShortlistFromConsultant() {
		return strSubjectCandiShortlistFromConsultant;
	}

	public void setStrSubjectCandiShortlistFromConsultant(String strSubjectCandiShortlistFromConsultant) {
		this.strSubjectCandiShortlistFromConsultant = strSubjectCandiShortlistFromConsultant;
	}

	public String getStrBodyCandiShortlistFromConsultant() {
		return strBodyCandiShortlistFromConsultant;
	}

	public void setStrBodyCandiShortlistFromConsultant(String strBodyCandiShortlistFromConsultant) {
		this.strBodyCandiShortlistFromConsultant = strBodyCandiShortlistFromConsultant;
	}

	public boolean getIsEmailCandiAssessmentForm() {
		return isEmailCandiAssessmentForm;
	}

	public void setIsEmailCandiAssessmentForm(boolean isEmailCandiAssessmentForm) {
		this.isEmailCandiAssessmentForm = isEmailCandiAssessmentForm;
	}

	public boolean getIsTextCandiAssessmentForm() {
		return isTextCandiAssessmentForm;
	}

	public void setIsTextCandiAssessmentForm(boolean isTextCandiAssessmentForm) {
		this.isTextCandiAssessmentForm = isTextCandiAssessmentForm;
	}

	public String getStrTextCandiAssessmentForm() {
		return strTextCandiAssessmentForm;
	}

	public void setStrTextCandiAssessmentForm(String strTextCandiAssessmentForm) {
		this.strTextCandiAssessmentForm = strTextCandiAssessmentForm;
	}

	public String getStrSubjectCandiAssessmentForm() {
		return strSubjectCandiAssessmentForm;
	}

	public void setStrSubjectCandiAssessmentForm(String strSubjectCandiAssessmentForm) {
		this.strSubjectCandiAssessmentForm = strSubjectCandiAssessmentForm;
	}

	public String getStrBodyCandiAssessmentForm() {
		return strBodyCandiAssessmentForm;
	}

	public void setStrBodyCandiAssessmentForm(String strBodyCandiAssessmentForm) {
		this.strBodyCandiAssessmentForm = strBodyCandiAssessmentForm;
	}

	public boolean getIsEmailCandiFinalize() {
		return isEmailCandiFinalize;
	}

	public void setIsEmailCandiFinalize(boolean isEmailCandiFinalize) {
		this.isEmailCandiFinalize = isEmailCandiFinalize;
	}

	public boolean getIsTextCandiFinalize() {
		return isTextCandiFinalize;
	}

	public void setIsTextCandiFinalize(boolean isTextCandiFinalize) {
		this.isTextCandiFinalize = isTextCandiFinalize;
	}

	public String getStrTextCandiFinalize() {
		return strTextCandiFinalize;
	}

	public void setStrTextCandiFinalize(String strTextCandiFinalize) {
		this.strTextCandiFinalize = strTextCandiFinalize;
	}

	public String getStrSubjectCandiFinalize() {
		return strSubjectCandiFinalize;
	}

	public void setStrSubjectCandiFinalize(String strSubjectCandiFinalize) {
		this.strSubjectCandiFinalize = strSubjectCandiFinalize;
	}

	public String getStrBodyCandiFinalize() {
		return strBodyCandiFinalize;
	}

	public void setStrBodyCandiFinalize(String strBodyCandiFinalize) {
		this.strBodyCandiFinalize = strBodyCandiFinalize;
	}


	public String getStrTextNewActivity() {
		return strTextNewActivity;
	}


	public void setStrTextNewActivity(String strTextNewActivity) {
		this.strTextNewActivity = strTextNewActivity;
	}


	public String getStrSubjectNewActivity() {
		return strSubjectNewActivity;
	}


	public void setStrSubjectNewActivity(String strSubjectNewActivity) {
		this.strSubjectNewActivity = strSubjectNewActivity;
	}


	public String getStrBodyNewActivity() {
		return strBodyNewActivity;
	}


	public void setStrBodyNewActivity(String strBodyNewActivity) {
		this.strBodyNewActivity = strBodyNewActivity;
	}


	public boolean isEmailNewActivity() {
		return isEmailNewActivity;
	}


	public void setEmailNewActivity(boolean isEmailNewActivity) {
		this.isEmailNewActivity = isEmailNewActivity;
	}


	public boolean isTextNewActivity() {
		return isTextNewActivity;
	}


	public void setTextNewActivity(boolean isTextNewActivity) {
		this.isTextNewActivity = isTextNewActivity;
	}

	public boolean getIsEmailNewActivity() {
		return isEmailNewActivity;
	}

	public void setIsEmailNewActivity(boolean isEmailNewActivity) {
		this.isEmailNewActivity = isEmailNewActivity;
	}

	public boolean getIsTextNewActivity() {
		return isTextNewActivity;
	}

	public void setIsTextNewActivity(boolean isTextNewActivity) {
		this.isTextNewActivity = isTextNewActivity;
	}

	public boolean getIsEmailNewProWelcomeToCust() {
		return isEmailNewProWelcomeToCust;
	}

	public void setIsEmailNewProWelcomeToCust(boolean isEmailNewProWelcomeToCust) {
		this.isEmailNewProWelcomeToCust = isEmailNewProWelcomeToCust;
	}

	public boolean getIsTextNewProWelcomeToCust() {
		return isTextNewProWelcomeToCust;
	}

	public void setIsTextNewProWelcomeToCust(boolean isTextNewProWelcomeToCust) {
		this.isTextNewProWelcomeToCust = isTextNewProWelcomeToCust;
	}

	public String getStrTextNewProWelcomeToCust() {
		return strTextNewProWelcomeToCust;
	}

	public void setStrTextNewProWelcomeToCust(String strTextNewProWelcomeToCust) {
		this.strTextNewProWelcomeToCust = strTextNewProWelcomeToCust;
	}

	public String getStrSubjectNewProWelcomeToCust() {
		return strSubjectNewProWelcomeToCust;
	}

	public void setStrSubjectNewProWelcomeToCust(String strSubjectNewProWelcomeToCust) {
		this.strSubjectNewProWelcomeToCust = strSubjectNewProWelcomeToCust;
	}

	public String getStrBodyNewProWelcomeToCust() {
		return strBodyNewProWelcomeToCust;
	}

	public void setStrBodyNewProWelcomeToCust(String strBodyNewProWelcomeToCust) {
		this.strBodyNewProWelcomeToCust = strBodyNewProWelcomeToCust;
	}

	public boolean getIsTextNewCustAdded() {
		return isTextNewCustAdded;
	}

	public void setIsTextNewCustAdded(boolean isTextNewCustAdded) {
		this.isTextNewCustAdded = isTextNewCustAdded;
	}

	public boolean getIsTextNewCustContactAdded() {
		return isTextNewCustContactAdded;
	}

	public void setIsTextNewCustContactAdded(boolean isTextNewCustContactAdded) {
		this.isTextNewCustContactAdded = isTextNewCustContactAdded;
	}

	public boolean getIsEmailNewCustAdded() {
		return isEmailNewCustAdded;
	}

	public void setIsEmailNewCustAdded(boolean isEmailNewCustAdded) {
		this.isEmailNewCustAdded = isEmailNewCustAdded;
	}

	public String getStrTextNewCustAdded() {
		return strTextNewCustAdded;
	}

	public void setStrTextNewCustAdded(String strTextNewCustAdded) {
		this.strTextNewCustAdded = strTextNewCustAdded;
	}

	public String getStrSubjectNewCustAdded() {
		return strSubjectNewCustAdded;
	}

	public void setStrSubjectNewCustAdded(String strSubjectNewCustAdded) {
		this.strSubjectNewCustAdded = strSubjectNewCustAdded;
	}

	public String getStrBodyNewCustAdded() {
		return strBodyNewCustAdded;
	}

	public void setStrBodyNewCustAdded(String strBodyNewCustAdded) {
		this.strBodyNewCustAdded = strBodyNewCustAdded;
	}

	public boolean getIsEmailNewCustContactAdded() {
		return isEmailNewCustContactAdded;
	}

	public void setIsEmailNewCustContactAdded(boolean isEmailNewCustContactAdded) {
		this.isEmailNewCustContactAdded = isEmailNewCustContactAdded;
	}

	public String getStrTextNewCustContactAdded() {
		return strTextNewCustContactAdded;
	}

	public void setStrTextNewCustContactAdded(String strTextNewCustContactAdded) {
		this.strTextNewCustContactAdded = strTextNewCustContactAdded;
	}

	public String getStrSubjectNewCustContactAdded() {
		return strSubjectNewCustContactAdded;
	}

	public void setStrSubjectNewCustContactAdded(String strSubjectNewCustContactAdded) {
		this.strSubjectNewCustContactAdded = strSubjectNewCustContactAdded;
	}

	public String getStrBodyNewCustContactAdded() {
		return strBodyNewCustContactAdded;
	}

	public void setStrBodyNewCustContactAdded(String strBodyNewCustContactAdded) {
		this.strBodyNewCustContactAdded = strBodyNewCustContactAdded;
	}

	public String getStrProductType() {
		return strProductType;
	}

	public void setStrProductType(String strProductType) {
		this.strProductType = strProductType;
	}

	public String getStrTextProUpdated() {
		return strTextProUpdated;
	}

	public void setStrTextProUpdated(String strTextProUpdated) {
		this.strTextProUpdated = strTextProUpdated;
	}

	public String getStrSubjectProUpdated() {
		return strSubjectProUpdated;
	}

	public void setStrSubjectProUpdated(String strSubjectProUpdated) {
		this.strSubjectProUpdated = strSubjectProUpdated;
	}

	public String getStrBodyProUpdated() {
		return strBodyProUpdated;
	}

	public void setStrBodyProUpdated(String strBodyProUpdated) {
		this.strBodyProUpdated = strBodyProUpdated;
	}

	public String getStrTextNewTask() {
		return strTextNewTask;
	}

	public void setStrTextNewTask(String strTextNewTask) {
		this.strTextNewTask = strTextNewTask;
	}

	public String getStrSubjectNewTask() {
		return strSubjectNewTask;
	}

	public void setStrSubjectNewTask(String strSubjectNewTask) {
		this.strSubjectNewTask = strSubjectNewTask;
	}

	public String getStrBodyNewTask() {
		return strBodyNewTask;
	}

	public void setStrBodyNewTask(String strBodyNewTask) {
		this.strBodyNewTask = strBodyNewTask;
	}

	public String getStrTextNewDocShared() {
		return strTextNewDocShared;
	}

	public void setStrTextNewDocShared(String strTextNewDocShared) {
		this.strTextNewDocShared = strTextNewDocShared;
	}

	public String getStrSubjectNewDocShared() {
		return strSubjectNewDocShared;
	}

	public void setStrSubjectNewDocShared(String strSubjectNewDocShared) {
		this.strSubjectNewDocShared = strSubjectNewDocShared;
	}

	public String getStrBodyNewDocShared() {
		return strBodyNewDocShared;
	}

	public void setStrBodyNewDocShared(String strBodyNewDocShared) {
		this.strBodyNewDocShared = strBodyNewDocShared;
	}

	public String getStrTextTaskCompleted() {
		return strTextTaskCompleted;
	}

	public void setStrTextTaskCompleted(String strTextTaskCompleted) {
		this.strTextTaskCompleted = strTextTaskCompleted;
	}

	public String getStrSubjectTaskCompleted() {
		return strSubjectTaskCompleted;
	}

	public void setStrSubjectTaskCompleted(String strSubjectTaskCompleted) {
		this.strSubjectTaskCompleted = strSubjectTaskCompleted;
	}

	public String getStrBodyTaskCompleted() {
		return strBodyTaskCompleted;
	}

	public void setStrBodyTaskCompleted(String strBodyTaskCompleted) {
		this.strBodyTaskCompleted = strBodyTaskCompleted;
	}

	public String getStrTextProCompleted() {
		return strTextProCompleted;
	}

	public void setStrTextProCompleted(String strTextProCompleted) {
		this.strTextProCompleted = strTextProCompleted;
	}

	public String getStrSubjectProCompleted() {
		return strSubjectProCompleted;
	}

	public void setStrSubjectProCompleted(String strSubjectProCompleted) {
		this.strSubjectProCompleted = strSubjectProCompleted;
	}

	public String getStrBodyProCompleted() {
		return strBodyProCompleted;
	}

	public void setStrBodyProCompleted(String strBodyProCompleted) {
		this.strBodyProCompleted = strBodyProCompleted;
	}

	public String getStrTextProBlocked() {
		return strTextProBlocked;
	}

	public void setStrTextProBlocked(String strTextProBlocked) {
		this.strTextProBlocked = strTextProBlocked;
	}

	public String getStrSubjectProBlocked() {
		return strSubjectProBlocked;
	}

	public void setStrSubjectProBlocked(String strSubjectProBlocked) {
		this.strSubjectProBlocked = strSubjectProBlocked;
	}

	public String getStrBodyProBlocked() {
		return strBodyProBlocked;
	}

	public void setStrBodyProBlocked(String strBodyProBlocked) {
		this.strBodyProBlocked = strBodyProBlocked;
	}

	public String getStrTextMilestoneCompleted() {
		return strTextMilestoneCompleted;
	}

	public void setStrTextMilestoneCompleted(String strTextMilestoneCompleted) {
		this.strTextMilestoneCompleted = strTextMilestoneCompleted;
	}

	public String getStrSubjectMilestoneCompleted() {
		return strSubjectMilestoneCompleted;
	}

	public void setStrSubjectMilestoneCompleted(String strSubjectMilestoneCompleted) {
		this.strSubjectMilestoneCompleted = strSubjectMilestoneCompleted;
	}

	public String getStrBodyMilestoneCompleted() {
		return strBodyMilestoneCompleted;
	}

	public void setStrBodyMilestoneCompleted(String strBodyMilestoneCompleted) {
		this.strBodyMilestoneCompleted = strBodyMilestoneCompleted;
	}

	public String getStrTextProReopened() {
		return strTextProReopened;
	}

	public void setStrTextProReopened(String strTextProReopened) {
		this.strTextProReopened = strTextProReopened;
	}

	public String getStrSubjectProReopened() {
		return strSubjectProReopened;
	}

	public void setStrSubjectProReopened(String strSubjectProReopened) {
		this.strSubjectProReopened = strSubjectProReopened;
	}

	public String getStrBodyProReopened() {
		return strBodyProReopened;
	}

	public void setStrBodyProReopened(String strBodyProReopened) {
		this.strBodyProReopened = strBodyProReopened;
	}

	public String getStrTextTimesheetSubmitted() {
		return strTextTimesheetSubmitted;
	}

	public void setStrTextTimesheetSubmitted(String strTextTimesheetSubmitted) {
		this.strTextTimesheetSubmitted = strTextTimesheetSubmitted;
	}

	public String getStrSubjectTimesheetSubmitted() {
		return strSubjectTimesheetSubmitted;
	}

	public void setStrSubjectTimesheetSubmitted(String strSubjectTimesheetSubmitted) {
		this.strSubjectTimesheetSubmitted = strSubjectTimesheetSubmitted;
	}

	public String getStrBodyTimesheetSubmitted() {
		return strBodyTimesheetSubmitted;
	}

	public void setStrBodyTimesheetSubmitted(String strBodyTimesheetSubmitted) {
		this.strBodyTimesheetSubmitted = strBodyTimesheetSubmitted;
	}

	public String getStrTextTimesheetReopened() {
		return strTextTimesheetReopened;
	}

	public void setStrTextTimesheetReopened(String strTextTimesheetReopened) {
		this.strTextTimesheetReopened = strTextTimesheetReopened;
	}

	public String getStrSubjectTimesheetReopened() {
		return strSubjectTimesheetReopened;
	}

	public void setStrSubjectTimesheetReopened(String strSubjectTimesheetReopened) {
		this.strSubjectTimesheetReopened = strSubjectTimesheetReopened;
	}

	public String getStrBodyTimesheetReopened() {
		return strBodyTimesheetReopened;
	}

	public void setStrBodyTimesheetReopened(String strBodyTimesheetReopened) {
		this.strBodyTimesheetReopened = strBodyTimesheetReopened;
	}

	public String getStrTextTimesheetApproved() {
		return strTextTimesheetApproved;
	}

	public void setStrTextTimesheetApproved(String strTextTimesheetApproved) {
		this.strTextTimesheetApproved = strTextTimesheetApproved;
	}

	public String getStrSubjectTimesheetApproved() {
		return strSubjectTimesheetApproved;
	}

	public void setStrSubjectTimesheetApproved(String strSubjectTimesheetApproved) {
		this.strSubjectTimesheetApproved = strSubjectTimesheetApproved;
	}

	public String getStrBodyTimesheetApproved() {
		return strBodyTimesheetApproved;
	}

	public void setStrBodyTimesheetApproved(String strBodyTimesheetApproved) {
		this.strBodyTimesheetApproved = strBodyTimesheetApproved;
	}

	public String getStrTextTimesheetSubmitToCust() {
		return strTextTimesheetSubmitToCust;
	}

	public void setStrTextTimesheetSubmitToCust(String strTextTimesheetSubmitToCust) {
		this.strTextTimesheetSubmitToCust = strTextTimesheetSubmitToCust;
	}

	public String getStrSubjectTimesheetSubmitToCust() {
		return strSubjectTimesheetSubmitToCust;
	}

	public void setStrSubjectTimesheetSubmitToCust(String strSubjectTimesheetSubmitToCust) {
		this.strSubjectTimesheetSubmitToCust = strSubjectTimesheetSubmitToCust;
	}

	public String getStrBodyTimesheetSubmitToCust() {
		return strBodyTimesheetSubmitToCust;
	}

	public void setStrBodyTimesheetSubmitToCust(String strBodyTimesheetSubmitToCust) {
		this.strBodyTimesheetSubmitToCust = strBodyTimesheetSubmitToCust;
	}

	public String getStrTextMilestoneBilling() {
		return strTextMilestoneBilling;
	}

	public void setStrTextMilestoneBilling(String strTextMilestoneBilling) {
		this.strTextMilestoneBilling = strTextMilestoneBilling;
	}

	public String getStrSubjectMilestoneBilling() {
		return strSubjectMilestoneBilling;
	}

	public void setStrSubjectMilestoneBilling(String strSubjectMilestoneBilling) {
		this.strSubjectMilestoneBilling = strSubjectMilestoneBilling;
	}

	public String getStrBodyMilestoneBilling() {
		return strBodyMilestoneBilling;
	}

	public void setStrBodyMilestoneBilling(String strBodyMilestoneBilling) {
		this.strBodyMilestoneBilling = strBodyMilestoneBilling;
	}

	public String getStrTextRecurringBilling() {
		return strTextRecurringBilling;
	}

	public void setStrTextRecurringBilling(String strTextRecurringBilling) {
		this.strTextRecurringBilling = strTextRecurringBilling;
	}

	public String getStrSubjectRecurringBilling() {
		return strSubjectRecurringBilling;
	}

	public void setStrSubjectRecurringBilling(String strSubjectRecurringBilling) {
		this.strSubjectRecurringBilling = strSubjectRecurringBilling;
	}

	public String getStrBodyRecurringBilling() {
		return strBodyRecurringBilling;
	}

	public void setStrBodyRecurringBilling(String strBodyRecurringBilling) {
		this.strBodyRecurringBilling = strBodyRecurringBilling;
	}

	public String getStrTextPaymentAlert() {
		return strTextPaymentAlert;
	}

	public void setStrTextPaymentAlert(String strTextPaymentAlert) {
		this.strTextPaymentAlert = strTextPaymentAlert;
	}

	public String getStrSubjectPaymentAlert() {
		return strSubjectPaymentAlert;
	}

	public void setStrSubjectPaymentAlert(String strSubjectPaymentAlert) {
		this.strSubjectPaymentAlert = strSubjectPaymentAlert;
	}

	public String getStrBodyPaymentAlert() {
		return strBodyPaymentAlert;
	}

	public void setStrBodyPaymentAlert(String strBodyPaymentAlert) {
		this.strBodyPaymentAlert = strBodyPaymentAlert;
	}

	public String getStrTextPaymentReminder() {
		return strTextPaymentReminder;
	}

	public void setStrTextPaymentReminder(String strTextPaymentReminder) {
		this.strTextPaymentReminder = strTextPaymentReminder;
	}

	public String getStrSubjectPaymentReminder() {
		return strSubjectPaymentReminder;
	}

	public void setStrSubjectPaymentReminder(String strSubjectPaymentReminder) {
		this.strSubjectPaymentReminder = strSubjectPaymentReminder;
	}

	public String getStrBodyPaymentReminder() {
		return strBodyPaymentReminder;
	}

	public void setStrBodyPaymentReminder(String strBodyPaymentReminder) {
		this.strBodyPaymentReminder = strBodyPaymentReminder;
	}

	public boolean getIsEmailProUpdated() {
		return isEmailProUpdated;
	}

	public void setIsEmailProUpdated(boolean isEmailProUpdated) {
		this.isEmailProUpdated = isEmailProUpdated;
	}

	public boolean getIsTextProUpdated() {
		return isTextProUpdated;
	}

	public void setIsTextProUpdated(boolean isTextProUpdated) {
		this.isTextProUpdated = isTextProUpdated;
	}

	public boolean getIsEmailNewTask() {
		return isEmailNewTask;
	}

	public void setIsEmailNewTask(boolean isEmailNewTask) {
		this.isEmailNewTask = isEmailNewTask;
	}

	public boolean getIsTextNewTask() {
		return isTextNewTask;
	}

	public void setIsTextNewTask(boolean isTextNewTask) {
		this.isTextNewTask = isTextNewTask;
	}

	public boolean getIsEmailNewDocShared() {
		return isEmailNewDocShared;
	}

	public void setIsEmailNewDocShared(boolean isEmailNewDocShared) {
		this.isEmailNewDocShared = isEmailNewDocShared;
	}

	public boolean getIsTextNewDocShared() {
		return isTextNewDocShared;
	}

	public void setIsTextNewDocShared(boolean isTextNewDocShared) {
		this.isTextNewDocShared = isTextNewDocShared;
	}

	public boolean getIsEmailTaskCompleted() {
		return isEmailTaskCompleted;
	}

	public void setIsEmailTaskCompleted(boolean isEmailTaskCompleted) {
		this.isEmailTaskCompleted = isEmailTaskCompleted;
	}

	public boolean getIsTextTaskCompleted() {
		return isTextTaskCompleted;
	}

	public void setIsTextTaskCompleted(boolean isTextTaskCompleted) {
		this.isTextTaskCompleted = isTextTaskCompleted;
	}

	public boolean getIsEmailProCompleted() {
		return isEmailProCompleted;
	}

	public void setIsEmailProCompleted(boolean isEmailProCompleted) {
		this.isEmailProCompleted = isEmailProCompleted;
	}

	public boolean getIsTextProCompleted() {
		return isTextProCompleted;
	}

	public void setIsTextProCompleted(boolean isTextProCompleted) {
		this.isTextProCompleted = isTextProCompleted;
	}

	public boolean getIsEmailProBlocked() {
		return isEmailProBlocked;
	}

	public void setIsEmailProBlocked(boolean isEmailProBlocked) {
		this.isEmailProBlocked = isEmailProBlocked;
	}

	public boolean getIsTextProBlocked() {
		return isTextProBlocked;
	}

	public void setIsTextProBlocked(boolean isTextProBlocked) {
		this.isTextProBlocked = isTextProBlocked;
	}

	public boolean getIsEmailMilestoneCompleted() {
		return isEmailMilestoneCompleted;
	}

	public void setIsEmailMilestoneCompleted(boolean isEmailMilestoneCompleted) {
		this.isEmailMilestoneCompleted = isEmailMilestoneCompleted;
	}

	public boolean getIsTextMilestoneCompleted() {
		return isTextMilestoneCompleted;
	}

	public void setIsTextMilestoneCompleted(boolean isTextMilestoneCompleted) {
		this.isTextMilestoneCompleted = isTextMilestoneCompleted;
	}

	public boolean getIsEmailProReopened() {
		return isEmailProReopened;
	}

	public void setIsEmailProReopened(boolean isEmailProReopened) {
		this.isEmailProReopened = isEmailProReopened;
	}

	public boolean getIsTextProReopened() {
		return isTextProReopened;
	}

	public void setIsTextProReopened(boolean isTextProReopened) {
		this.isTextProReopened = isTextProReopened;
	}

	public boolean getIsEmailTimesheetSubmitted() {
		return isEmailTimesheetSubmitted;
	}

	public void setIsEmailTimesheetSubmitted(boolean isEmailTimesheetSubmitted) {
		this.isEmailTimesheetSubmitted = isEmailTimesheetSubmitted;
	}

	public boolean getIsTextTimesheetSubmitted() {
		return isTextTimesheetSubmitted;
	}

	public void setIsTextTimesheetSubmitted(boolean isTextTimesheetSubmitted) {
		this.isTextTimesheetSubmitted = isTextTimesheetSubmitted;
	}

	public boolean getIsEmailTimesheetReopened() {
		return isEmailTimesheetReopened;
	}

	public void setIsEmailTimesheetReopened(boolean isEmailTimesheetReopened) {
		this.isEmailTimesheetReopened = isEmailTimesheetReopened;
	}

	public boolean getIsTextTimesheetReopened() {
		return isTextTimesheetReopened;
	}

	public void setIsTextTimesheetReopened(boolean isTextTimesheetReopened) {
		this.isTextTimesheetReopened = isTextTimesheetReopened;
	}

	public boolean getIsEmailTimesheetApproved() {
		return isEmailTimesheetApproved;
	}

	public void setIsEmailTimesheetApproved(boolean isEmailTimesheetApproved) {
		this.isEmailTimesheetApproved = isEmailTimesheetApproved;
	}

	public boolean getIsTextTimesheetApproved() {
		return isTextTimesheetApproved;
	}

	public void setIsTextTimesheetApproved(boolean isTextTimesheetApproved) {
		this.isTextTimesheetApproved = isTextTimesheetApproved;
	}

	public boolean getIsEmailTimesheetSubmitToCust() {
		return isEmailTimesheetSubmitToCust;
	}

	public void setIsEmailTimesheetSubmitToCust(boolean isEmailTimesheetSubmitToCust) {
		this.isEmailTimesheetSubmitToCust = isEmailTimesheetSubmitToCust;
	}

	public boolean getIsTextTimesheetSubmitToCust() {
		return isTextTimesheetSubmitToCust;
	}

	public void setIsTextTimesheetSubmitToCust(boolean isTextTimesheetSubmitToCust) {
		this.isTextTimesheetSubmitToCust = isTextTimesheetSubmitToCust;
	}

	public boolean getIsEmailMilestoneBilling() {
		return isEmailMilestoneBilling;
	}

	public void setIsEmailMilestoneBilling(boolean isEmailMilestoneBilling) {
		this.isEmailMilestoneBilling = isEmailMilestoneBilling;
	}

	public boolean getIsTextMilestoneBilling() {
		return isTextMilestoneBilling;
	}

	public void setIsTextMilestoneBilling(boolean isTextMilestoneBilling) {
		this.isTextMilestoneBilling = isTextMilestoneBilling;
	}

	public boolean getIsEmailRecurringBilling() {
		return isEmailRecurringBilling;
	}

	public void setIsEmailRecurringBilling(boolean isEmailRecurringBilling) {
		this.isEmailRecurringBilling = isEmailRecurringBilling;
	}

	public boolean getIsTextRecurringBilling() {
		return isTextRecurringBilling;
	}

	public void setIsTextRecurringBilling(boolean isTextRecurringBilling) {
		this.isTextRecurringBilling = isTextRecurringBilling;
	}

	public boolean getIsEmailPaymentAlert() {
		return isEmailPaymentAlert;
	}

	public void setIsEmailPaymentAlert(boolean isEmailPaymentAlert) {
		this.isEmailPaymentAlert = isEmailPaymentAlert;
	}

	public boolean getIsTextPaymentAlert() {
		return isTextPaymentAlert;
	}

	public void setIsTextPaymentAlert(boolean isTextPaymentAlert) {
		this.isTextPaymentAlert = isTextPaymentAlert;
	}

	public boolean getIsEmailPaymentReminder() {
		return isEmailPaymentReminder;
	}

	public void setIsEmailPaymentReminder(boolean isEmailPaymentReminder) {
		this.isEmailPaymentReminder = isEmailPaymentReminder;
	}

	public boolean getIsTextPaymentReminder() {
		return isTextPaymentReminder;
	}

	public void setIsTextPaymentReminder(boolean isTextPaymentReminder) {
		this.isTextPaymentReminder = isTextPaymentReminder;
	}

	public boolean getIsTextNewManagerReimbursementRequest() {
		return isTextNewManagerReimbursementRequest;
	}

	public void setIsTextNewManagerReimbursementRequest(boolean isTextNewManagerReimbursementRequest) {
		this.isTextNewManagerReimbursementRequest = isTextNewManagerReimbursementRequest;
	}

	public boolean getIsEmailNewManagerReimbursementRequest() {
		return isEmailNewManagerReimbursementRequest;
	}

	public void setIsEmailNewManagerReimbursementRequest(boolean isEmailNewManagerReimbursementRequest) {
		this.isEmailNewManagerReimbursementRequest = isEmailNewManagerReimbursementRequest;
	}

	public String getStrTextNewManagerReimbursementRequest() {
		return strTextNewManagerReimbursementRequest;
	}

	public void setStrTextNewManagerReimbursementRequest(String strTextNewManagerReimbursementRequest) {
		this.strTextNewManagerReimbursementRequest = strTextNewManagerReimbursementRequest;
	}

	public String getStrSubjectNewManagerReimbursementRequest() {
		return strSubjectNewManagerReimbursementRequest;
	}

	public void setStrSubjectNewManagerReimbursementRequest(String strSubjectNewManagerReimbursementRequest) {
		this.strSubjectNewManagerReimbursementRequest = strSubjectNewManagerReimbursementRequest;
	}

	public String getStrBodyNewManagerReimbursementRequest() {
		return strBodyNewManagerReimbursementRequest;
	}

	public void setStrBodyNewManagerReimbursementRequest(String strBodyNewManagerReimbursementRequest) {
		this.strBodyNewManagerReimbursementRequest = strBodyNewManagerReimbursementRequest;
	}


	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}
	public boolean getIsEmailEmployeeLeavePullout() {
		return isEmailEmployeeLeavePullout;
	}

	public void setIsEmailEmployeeLeavePullout(boolean isEmailEmployeeLeavePullout) {
		this.isEmailEmployeeLeavePullout = isEmailEmployeeLeavePullout;
	}

	public boolean getIsTextEmployeeLeavePullout() {
		return isTextEmployeeLeavePullout;
	}

	public void setIsTextEmployeeLeavePullout(boolean isTextEmployeeLeavePullout) {
		this.isTextEmployeeLeavePullout = isTextEmployeeLeavePullout;
	}

	public String getStrTextEmployeeLeavePullout() {
		return strTextEmployeeLeavePullout;
	}

	public void setStrTextEmployeeLeavePullout(String strTextEmployeeLeavePullout) {
		this.strTextEmployeeLeavePullout = strTextEmployeeLeavePullout;
	}

	public String getStrSubjectEmployeeLeavePullout() {
		return strSubjectEmployeeLeavePullout;
	}

	public void setStrSubjectEmployeeLeavePullout(String strSubjectEmployeeLeavePullout) {
		this.strSubjectEmployeeLeavePullout = strSubjectEmployeeLeavePullout;
	}

	public String getStrBodyEmployeeLeavePullout() {
		return strBodyEmployeeLeavePullout;
	}

	public void setStrBodyEmployeeLeavePullout(String strBodyEmployeeLeavePullout) {
		this.strBodyEmployeeLeavePullout = strBodyEmployeeLeavePullout;
	}

	public boolean getIsEmailManagerNewExtraWorkRequest() {
		return isEmailManagerNewExtraWorkRequest;
	}

	public void setIsEmailManagerNewExtraWorkRequest(boolean isEmailManagerNewExtraWorkRequest) {
		this.isEmailManagerNewExtraWorkRequest = isEmailManagerNewExtraWorkRequest;
	}

	public boolean getIsTextManagerNewExtraWorkRequest() {
		return isTextManagerNewExtraWorkRequest;
	}

	public void setIsTextManagerNewExtraWorkRequest(boolean isTextManagerNewExtraWorkRequest) {
		this.isTextManagerNewExtraWorkRequest = isTextManagerNewExtraWorkRequest;
	}

	public String getStrTextManagerNewExtraWorkRequest() {
		return strTextManagerNewExtraWorkRequest;
	}

	public void setStrTextManagerNewExtraWorkRequest(String strTextManagerNewExtraWorkRequest) {
		this.strTextManagerNewExtraWorkRequest = strTextManagerNewExtraWorkRequest;
	}

	public String getStrSubjectManagerNewExtraWorkRequest() {
		return strSubjectManagerNewExtraWorkRequest;
	}

	public void setStrSubjectManagerNewExtraWorkRequest(String strSubjectManagerNewExtraWorkRequest) {
		this.strSubjectManagerNewExtraWorkRequest = strSubjectManagerNewExtraWorkRequest;
	}

	public String getStrBodyManagerNewExtraWorkRequest() {
		return strBodyManagerNewExtraWorkRequest;
	}

	public void setStrBodyManagerNewExtraWorkRequest(String strBodyManagerNewExtraWorkRequest) {
		this.strBodyManagerNewExtraWorkRequest = strBodyManagerNewExtraWorkRequest;
	}

	public boolean getIsEmailNewEmployeeExtraWorkApproval() {
		return isEmailNewEmployeeExtraWorkApproval;
	}

	public void setIsEmailNewEmployeeExtraWorkApproval(boolean isEmailNewEmployeeExtraWorkApproval) {
		this.isEmailNewEmployeeExtraWorkApproval = isEmailNewEmployeeExtraWorkApproval;
	}

	public boolean getIsTextNewEmployeeExtraWorkApproval() {
		return isTextNewEmployeeExtraWorkApproval;
	}

	public void setIsTextNewEmployeeExtraWorkApproval(boolean isTextNewEmployeeExtraWorkApproval) {
		this.isTextNewEmployeeExtraWorkApproval = isTextNewEmployeeExtraWorkApproval;
	}

	public String getStrTextNewEmployeeExtraWorkApproval() {
		return strTextNewEmployeeExtraWorkApproval;
	}

	public void setStrTextNewEmployeeExtraWorkApproval(String strTextNewEmployeeExtraWorkApproval) {
		this.strTextNewEmployeeExtraWorkApproval = strTextNewEmployeeExtraWorkApproval;
	}

	public String getStrSubjectNewEmployeeExtraWorkApproval() {
		return strSubjectNewEmployeeExtraWorkApproval;
	}

	public void setStrSubjectNewEmployeeExtraWorkApproval(String strSubjectNewEmployeeExtraWorkApproval) {
		this.strSubjectNewEmployeeExtraWorkApproval = strSubjectNewEmployeeExtraWorkApproval;
	}

	public String getStrBodyNewEmployeeExtraWorkApproval() {
		return strBodyNewEmployeeExtraWorkApproval;
	}

	public void setStrBodyNewEmployeeExtraWorkApproval(String strBodyNewEmployeeExtraWorkApproval) {
		this.strBodyNewEmployeeExtraWorkApproval = strBodyNewEmployeeExtraWorkApproval;
	}

	public boolean getIsEmailEmployeeExtraWorkCancel() {
		return isEmailEmployeeExtraWorkCancel;
	}

	public void setIsEmailEmployeeExtraWorkCancel(boolean isEmailEmployeeExtraWorkCancel) {
		this.isEmailEmployeeExtraWorkCancel = isEmailEmployeeExtraWorkCancel;
	}

	public boolean getIsTextEmployeeExtraWorkCancel() {
		return isTextEmployeeExtraWorkCancel;
	}

	public void setIsTextEmployeeExtraWorkCancel(boolean isTextEmployeeExtraWorkCancel) {
		this.isTextEmployeeExtraWorkCancel = isTextEmployeeExtraWorkCancel;
	}

	public String getStrTextEmployeeExtraWorkCancel() {
		return strTextEmployeeExtraWorkCancel;
	}

	public void setStrTextEmployeeExtraWorkCancel(String strTextEmployeeExtraWorkCancel) {
		this.strTextEmployeeExtraWorkCancel = strTextEmployeeExtraWorkCancel;
	}

	public String getStrSubjectEmployeeExtraWorkCancel() {
		return strSubjectEmployeeExtraWorkCancel;
	}

	public void setStrSubjectEmployeeExtraWorkCancel(String strSubjectEmployeeExtraWorkCancel) {
		this.strSubjectEmployeeExtraWorkCancel = strSubjectEmployeeExtraWorkCancel;
	}

	public String getStrBodyEmployeeExtraWorkCancel() {
		return strBodyEmployeeExtraWorkCancel;
	}

	public void setStrBodyEmployeeExtraWorkCancel(String strBodyEmployeeExtraWorkCancel) {
		this.strBodyEmployeeExtraWorkCancel = strBodyEmployeeExtraWorkCancel;
	}

	public boolean getIsEmailEmployeeExtraWorkPullout() {
		return isEmailEmployeeExtraWorkPullout;
	}

	public void setIsEmailEmployeeExtraWorkPullout(boolean isEmailEmployeeExtraWorkPullout) {
		this.isEmailEmployeeExtraWorkPullout = isEmailEmployeeExtraWorkPullout;
	}

	public boolean getIsTextEmployeeExtraWorkPullout() {
		return isTextEmployeeExtraWorkPullout;
	}

	public void setIsTextEmployeeExtraWorkPullout(boolean isTextEmployeeExtraWorkPullout) {
		this.isTextEmployeeExtraWorkPullout = isTextEmployeeExtraWorkPullout;
	}

	public String getStrTextEmployeeExtraWorkPullout() {
		return strTextEmployeeExtraWorkPullout;
	}

	public void setStrTextEmployeeExtraWorkPullout(String strTextEmployeeExtraWorkPullout) {
		this.strTextEmployeeExtraWorkPullout = strTextEmployeeExtraWorkPullout;
	}

	public String getStrSubjectEmployeeExtraWorkPullout() {
		return strSubjectEmployeeExtraWorkPullout;
	}

	public void setStrSubjectEmployeeExtraWorkPullout(String strSubjectEmployeeExtraWorkPullout) {
		this.strSubjectEmployeeExtraWorkPullout = strSubjectEmployeeExtraWorkPullout;
	}

	public String getStrBodyEmployeeExtraWorkPullout() {
		return strBodyEmployeeExtraWorkPullout;
	}

	public void setStrBodyEmployeeExtraWorkPullout(String strBodyEmployeeExtraWorkPullout) {
		this.strBodyEmployeeExtraWorkPullout = strBodyEmployeeExtraWorkPullout;
	}

	public boolean getIsEmailBirthday() {
		return isEmailBirthday;
	}

	public void setIsEmailBirthday(boolean isEmailBirthday) {
		this.isEmailBirthday = isEmailBirthday;
	}

	public boolean getIsTextBirthday() {
		return isTextBirthday;
	}

	public void setIsTextBirthday(boolean isTextBirthday) {
		this.isTextBirthday = isTextBirthday;
	}

	public String getStrTextBirthday() {
		return strTextBirthday;
	}

	public void setStrTextBirthday(String strTextBirthday) {
		this.strTextBirthday = strTextBirthday;
	}

	public String getStrSubjectBirthday() {
		return strSubjectBirthday;
	}

	public void setStrSubjectBirthday(String strSubjectBirthday) {
		this.strSubjectBirthday = strSubjectBirthday;
	}

	public String getStrBodyBirthday() {
		return strBodyBirthday;
	}

	public void setStrBodyBirthday(String strBodyBirthday) {
		this.strBodyBirthday = strBodyBirthday;
	}

	public boolean getIsEmailWorkAnniversary() {
		return isEmailWorkAnniversary;
	}

	public void setIsEmailWorkAnniversary(boolean isEmailWorkAnniversary) {
		this.isEmailWorkAnniversary = isEmailWorkAnniversary;
	}

	public boolean getIsTextWorkAnniversary() {
		return isTextWorkAnniversary;
	}

	public void setIsTextWorkAnniversary(boolean isTextWorkAnniversary) {
		this.isTextWorkAnniversary = isTextWorkAnniversary;
	}

	public String getStrTextWorkAnniversary() {
		return strTextWorkAnniversary;
	}

	public void setStrTextWorkAnniversary(String strTextWorkAnniversary) {
		this.strTextWorkAnniversary = strTextWorkAnniversary;
	}

	public String getStrSubjectWorkAnniversary() {
		return strSubjectWorkAnniversary;
	}

	public void setStrSubjectWorkAnniversary(String strSubjectWorkAnniversary) {
		this.strSubjectWorkAnniversary = strSubjectWorkAnniversary;
	}

	public String getStrBodyWorkAnniversary() {
		return strBodyWorkAnniversary;
	}

	public void setStrBodyWorkAnniversary(String strBodyWorkAnniversary) {
		this.strBodyWorkAnniversary = strBodyWorkAnniversary;
	}

	public boolean getIsEmailMarriageAnniversary() {
		return isEmailMarriageAnniversary;
	}

	public void setIsEmailMarriageAnniversary(boolean isEmailMarriageAnniversary) {
		this.isEmailMarriageAnniversary = isEmailMarriageAnniversary;
	}

	public boolean getIsTextMarriageAnniversary() {
		return isTextMarriageAnniversary;
	}

	public void setIsTextMarriageAnniversary(boolean isTextMarriageAnniversary) {
		this.isTextMarriageAnniversary = isTextMarriageAnniversary;
	}

	public String getStrTextMarriageAnniversary() {
		return strTextMarriageAnniversary;
	}

	public void setStrTextMarriageAnniversary(String strTextMarriageAnniversary) {
		this.strTextMarriageAnniversary = strTextMarriageAnniversary;
	}

	public String getStrSubjectMarriageAnniversary() {
		return strSubjectMarriageAnniversary;
	}

	public void setStrSubjectMarriageAnniversary(String strSubjectMarriageAnniversary) {
		this.strSubjectMarriageAnniversary = strSubjectMarriageAnniversary;
	}

	public String getStrBodyMarriageAnniversary() {
		return strBodyMarriageAnniversary;
	}

	public void setStrBodyMarriageAnniversary(String strBodyMarriageAnniversary) {
		this.strBodyMarriageAnniversary = strBodyMarriageAnniversary;
	}

	public boolean getIsEmailEmployeeResignationRequest() {
		return isEmailEmployeeResignationRequest;
	}

	public void setIsEmailEmployeeResignationRequest(boolean isEmailEmployeeResignationRequest) {
		this.isEmailEmployeeResignationRequest = isEmailEmployeeResignationRequest;
	}
	
	public boolean getIsTextEmployeeResignationRequest() {
		return isTextEmployeeResignationRequest;
	}

	public void setIsTextEmployeeResignationRequest(boolean isTextEmployeeResignationRequest) {
		this.isTextEmployeeResignationRequest = isTextEmployeeResignationRequest;
	}

	public String getStrTextEmployeeResignationRequest() {
		return strTextEmployeeResignationRequest;
	}

	public void setStrTextEmployeeResignationRequest(String strTextEmployeeResignationRequest) {
		this.strTextEmployeeResignationRequest = strTextEmployeeResignationRequest;
	}

	public String getStrSubjectEmployeeResignationRequest() {
		return strSubjectEmployeeResignationRequest;
	}

	public void setStrSubjectEmployeeResignationRequest(String strSubjectEmployeeResignationRequest) {
		this.strSubjectEmployeeResignationRequest = strSubjectEmployeeResignationRequest;
	}

	public String getStrBodyEmployeeResignationRequest() {
		return strBodyEmployeeResignationRequest;
	}

	public void setStrBodyEmployeeResignationRequest(String strBodyEmployeeResignationRequest) {
		this.strBodyEmployeeResignationRequest = strBodyEmployeeResignationRequest;
	}
	
	public boolean getIsEmailEmployeeResignationApproval() {
		return isEmailEmployeeResignationApproval;
	}

	public void setIsEmailEmployeeResignationApproval(boolean isEmailEmployeeResignationApproval) {
		this.isEmailEmployeeResignationApproval = isEmailEmployeeResignationApproval;
	}
	
	public boolean getIsTextEmployeeResignationApproval() {
		return isTextEmployeeResignationApproval;
	}

	public void setIsTextEmployeeResignationApproval(boolean isTextEmployeeResignationApproval) {
		this.isTextEmployeeResignationApproval = isTextEmployeeResignationApproval;
	}

	public String getStrTextEmployeeResignationApproval() {
		return strTextEmployeeResignationApproval;
	}

	public void setStrTextEmployeeResignationApproval(String strTextEmployeeResignationApproval) {
		this.strTextEmployeeResignationApproval = strTextEmployeeResignationApproval;
	}

	public String getStrSubjectEmployeeResignationApproval() {
		return strSubjectEmployeeResignationApproval;
	}

	public void setStrSubjectEmployeeResignationApproval(String strSubjectEmployeeResignationApproval) {
		this.strSubjectEmployeeResignationApproval = strSubjectEmployeeResignationApproval;
	}

	public String getStrBodyEmployeeResignationApproval() {
		return strBodyEmployeeResignationApproval;
	}

	public void setStrBodyEmployeeResignationApproval(String strBodyEmployeeResignationApproval) {
		this.strBodyEmployeeResignationApproval = strBodyEmployeeResignationApproval;
	}

	public boolean getIsEmailManagerNewTravelRequest() {
		return isEmailManagerNewTravelRequest;
	}

	public void setIsEmailManagerNewTravelRequest(boolean isEmailManagerNewTravelRequest) {
		this.isEmailManagerNewTravelRequest = isEmailManagerNewTravelRequest;
	}

	public boolean getIsTextManagerNewTravelRequest() {
		return isTextManagerNewTravelRequest;
	}

	public void setIsTextManagerNewTravelRequest(boolean isTextManagerNewTravelRequest) {
		this.isTextManagerNewTravelRequest = isTextManagerNewTravelRequest;
	}

	public String getStrTextManagerNewTravelRequest() {
		return strTextManagerNewTravelRequest;
	}

	public void setStrTextManagerNewTravelRequest(String strTextManagerNewTravelRequest) {
		this.strTextManagerNewTravelRequest = strTextManagerNewTravelRequest;
	}

	public String getStrSubjectManagerNewTravelRequest() {
		return strSubjectManagerNewTravelRequest;
	}

	public void setStrSubjectManagerNewTravelRequest(String strSubjectManagerNewTravelRequest) {
		this.strSubjectManagerNewTravelRequest = strSubjectManagerNewTravelRequest;
	}

	public String getStrBodyManagerNewTravelRequest() {
		return strBodyManagerNewTravelRequest;
	}

	public void setStrBodyManagerNewTravelRequest(String strBodyManagerNewTravelRequest) {
		this.strBodyManagerNewTravelRequest = strBodyManagerNewTravelRequest;
	}

	public boolean getIsEmailNewEmployeeTravelApproval() {
		return isEmailNewEmployeeTravelApproval;
	}

	public void setIsEmailNewEmployeeTravelApproval(boolean isEmailNewEmployeeTravelApproval) {
		this.isEmailNewEmployeeTravelApproval = isEmailNewEmployeeTravelApproval;
	}

	public boolean getIsTextNewEmployeeTravelApproval() {
		return isTextNewEmployeeTravelApproval;
	}

	public void setIsTextNewEmployeeTravelApproval(boolean isTextNewEmployeeTravelApproval) {
		this.isTextNewEmployeeTravelApproval = isTextNewEmployeeTravelApproval;
	}

	public String getStrTextNewEmployeeTravelApproval() {
		return strTextNewEmployeeTravelApproval;
	}

	public void setStrTextNewEmployeeTravelApproval(String strTextNewEmployeeTravelApproval) {
		this.strTextNewEmployeeTravelApproval = strTextNewEmployeeTravelApproval;
	}

	public String getStrSubjectNewEmployeeTravelApproval() {
		return strSubjectNewEmployeeTravelApproval;
	}

	public void setStrSubjectNewEmployeeTravelApproval(String strSubjectNewEmployeeTravelApproval) {
		this.strSubjectNewEmployeeTravelApproval = strSubjectNewEmployeeTravelApproval;
	}

	public String getStrBodyNewEmployeeTravelApproval() {
		return strBodyNewEmployeeTravelApproval;
	}

	public void setStrBodyNewEmployeeTravelApproval(String strBodyNewEmployeeTravelApproval) {
		this.strBodyNewEmployeeTravelApproval = strBodyNewEmployeeTravelApproval;
	}

	public boolean getIsEmailEmployeeTravelCancel() {
		return isEmailEmployeeTravelCancel;
	}

	public void setIsEmailEmployeeTravelCancel(boolean isEmailEmployeeTravelCancel) {
		this.isEmailEmployeeTravelCancel = isEmailEmployeeTravelCancel;
	}

	public boolean getIsTextEmployeeTravelCancel() {
		return isTextEmployeeTravelCancel;
	}

	public void setIsTextEmployeeTravelCancel(boolean isTextEmployeeTravelCancel) {
		this.isTextEmployeeTravelCancel = isTextEmployeeTravelCancel;
	}

	public String getStrTextEmployeeTravelCancel() {
		return strTextEmployeeTravelCancel;
	}

	public void setStrTextEmployeeTravelCancel(String strTextEmployeeTravelCancel) {
		this.strTextEmployeeTravelCancel = strTextEmployeeTravelCancel;
	}

	public String getStrSubjectEmployeeTravelCancel() {
		return strSubjectEmployeeTravelCancel;
	}

	public void setStrSubjectEmployeeTravelCancel(String strSubjectEmployeeTravelCancel) {
		this.strSubjectEmployeeTravelCancel = strSubjectEmployeeTravelCancel;
	}

	public String getStrBodyEmployeeTravelCancel() {
		return strBodyEmployeeTravelCancel;
	}

	public void setStrBodyEmployeeTravelCancel(String strBodyEmployeeTravelCancel) {
		this.strBodyEmployeeTravelCancel = strBodyEmployeeTravelCancel;
	}

	public boolean getIsEmailEmployeeTravelPullout() {
		return isEmailEmployeeTravelPullout;
	}

	public void setIsEmailEmployeeTravelPullout(boolean isEmailEmployeeTravelPullout) {
		this.isEmailEmployeeTravelPullout = isEmailEmployeeTravelPullout;
	}

	public boolean getIsTextEmployeeTravelPullout() {
		return isTextEmployeeTravelPullout;
	}

	public void setIsTextEmployeeTravelPullout(boolean isTextEmployeeTravelPullout) {
		this.isTextEmployeeTravelPullout = isTextEmployeeTravelPullout;
	}

	public String getStrTextEmployeeTravelPullout() {
		return strTextEmployeeTravelPullout;
	}

	public void setStrTextEmployeeTravelPullout(String strTextEmployeeTravelPullout) {
		this.strTextEmployeeTravelPullout = strTextEmployeeTravelPullout;
	}

	public String getStrSubjectEmployeeTravelPullout() {
		return strSubjectEmployeeTravelPullout;
	}

	public void setStrSubjectEmployeeTravelPullout(String strSubjectEmployeeTravelPullout) {
		this.strSubjectEmployeeTravelPullout = strSubjectEmployeeTravelPullout;
	}

	public String getStrBodyEmployeeTravelPullout() {
		return strBodyEmployeeTravelPullout;
	}

	public void setStrBodyEmployeeTravelPullout(String strBodyEmployeeTravelPullout) {
		this.strBodyEmployeeTravelPullout = strBodyEmployeeTravelPullout;
	}
	
	public boolean isEmailOnEventCreation() {
		return isEmailOnEventCreation;
	}


	public String getStrSubjectOnEventCreation() {
		return strSubjectOnEventCreation;
	}


	public String getStrBodyOnEventCreation() {
		return strBodyOnEventCreation;
	}


	public boolean isEmailOnAnnouncementCreation() {
		return isEmailOnAnnouncementCreation;
	}


	public String getStrSubjectOnAnnouncementCreation() {
		return strSubjectOnAnnouncementCreation;
	}


	public String getStrBodyOnAnnouncementCreation() {
		return strBodyOnAnnouncementCreation;
	}


	public void setEmailOnEventCreation(boolean isEmailOnEventCreation) {
		this.isEmailOnEventCreation = isEmailOnEventCreation;
	}
	public boolean getEmailOnEventCreation() {
		return isEmailOnEventCreation;
	}


	public void setStrSubjectOnEventCreation(String strSubjectOnEventCreation) {
		this.strSubjectOnEventCreation = strSubjectOnEventCreation;
	}


	public void setStrBodyOnEventCreation(String strBodyOnEventCreation) {
		this.strBodyOnEventCreation = strBodyOnEventCreation;
	}


	public void setEmailOnAnnouncementCreation(boolean isEmailOnAnnouncementCreation) {
		this.isEmailOnAnnouncementCreation = isEmailOnAnnouncementCreation;
	}
	public boolean getEmailOnAnnouncementCreation() {
		return isEmailOnAnnouncementCreation;
	}

	
	

	public void setStrSubjectOnAnnouncementCreation(String strSubjectOnAnnouncementCreation) {
		this.strSubjectOnAnnouncementCreation = strSubjectOnAnnouncementCreation;
	}


	public void setStrBodyOnAnnouncementCreation(String strBodyOnAnnouncementCreation) {
		this.strBodyOnAnnouncementCreation = strBodyOnAnnouncementCreation;
	}
	public boolean isTextOnEventCreation() {
		return isTextOnEventCreation;
	}


	public void setTextOnEventCreation(boolean isTextOnEventCreation) {
		this.isTextOnEventCreation = isTextOnEventCreation;
	}


	public String getStrTextOnEventCreation() {
		return strTextOnEventCreation;
	}


	public void setStrTextOnEventCreation(String strTextOnEventCreation) {
		this.strTextOnEventCreation = strTextOnEventCreation;
	}


	public boolean isTextOnAnnouncementCreation() {
		return isTextOnAnnouncementCreation;
	}


	public void setTextOnAnnouncementCreation(boolean isTextOnAnnouncementCreation) {
		this.isTextOnAnnouncementCreation = isTextOnAnnouncementCreation;
	}


	public String getStrTextOnAnnouncementCreation() {
		return strTextOnAnnouncementCreation;
	}


	public void setStrTextOnAnnouncementCreation(String strTextOnAnnouncementCreation) {
		this.strTextOnAnnouncementCreation = strTextOnAnnouncementCreation;
	}

	public File getStrBackgroundImage() {
		return strBackgroundImage;
	}


	public void setStrBackgroundImage(File strBackgroundImage) {
		this.strBackgroundImage = strBackgroundImage;
	}
	public String getStrBackgroundImageFileName() {
		return strBackgroundImageFileName;
	}


	public void setStrBackgroundImageFileName(String strBackgroundImageFileName) {
		this.strBackgroundImageFileName = strBackgroundImageFileName;
	}


	public String getStrBackgroundImageStatus() {
		return strBackgroundImageStatus;
	}


	public void setStrBackgroundImageStatus(String strBackgroundImageStatus) {
		this.strBackgroundImageStatus = strBackgroundImageStatus;
	}

	
	
	public String getStrBackgroundBirthdayImageStatus() {
		return strBackgroundBirthdayImageStatus;
	}


	public void setStrBackgroundBirthdayImageStatus(String strBackgroundBirthdayImageStatus) {
		this.strBackgroundBirthdayImageStatus = strBackgroundBirthdayImageStatus;
	}


	public File getStrBackgroundBirthdayImage() {
		return strBackgroundBirthdayImage;
	}


	public void setStrBackgroundBirthdayImage(File strBackgroundBirthdayImage) {
		this.strBackgroundBirthdayImage = strBackgroundBirthdayImage;
	}


	public String getStrBackgroundBirthdayImageFileName() {
		return strBackgroundBirthdayImageFileName;
	}


	public void setStrBackgroundBirthdayImageFileName(String strBackgroundBirthdayImageFileName) {
		this.strBackgroundBirthdayImageFileName = strBackgroundBirthdayImageFileName;
	}


	public String getStrBackgroundWorkImageStatus() {
		return strBackgroundWorkImageStatus;
	}


	public void setStrBackgroundWorkImageStatus(String strBackgroundWorkImageStatus) {
		this.strBackgroundWorkImageStatus = strBackgroundWorkImageStatus;
	}


	public File getStrBackgroundWorkImage() {
		return strBackgroundWorkImage;
	}


	public void setStrBackgroundWorkImage(File strBackgroundWorkImage) {
		this.strBackgroundWorkImage = strBackgroundWorkImage;
	}


	public String getStrBackgroundWorkImageFileName() {
		return strBackgroundWorkImageFileName;
	}


	public void setStrBackgroundWorkImageFileName(String strBackgroundWorkImageFileName) {
		this.strBackgroundWorkImageFileName = strBackgroundWorkImageFileName;
	}


	public String getStrBackgroundMarriageImageStatus() {
		return strBackgroundMarriageImageStatus;
	}


	public void setStrBackgroundMarriageImageStatus(String strBackgroundMarriageImageStatus) {
		this.strBackgroundMarriageImageStatus = strBackgroundMarriageImageStatus;
	}


	public File getStrBackgroundMarriageImage() {
		return strBackgroundMarriageImage;
	}


	public void setStrBackgroundMarriageImage(File strBackgroundMarriageImage) {
		this.strBackgroundMarriageImage = strBackgroundMarriageImage;
	}


	public String getStrBackgroundMarriageImageFileName() {
		return strBackgroundMarriageImageFileName;
	}


	public void setStrBackgroundMarriageImageFileName(String strBackgroundMarriageImageFileName) {
		this.strBackgroundMarriageImageFileName = strBackgroundMarriageImageFileName;
	}
	public String getStrBackgroundNewEmpImageStatus() {
		return strBackgroundNewEmpImageStatus;
	}


	public void setStrBackgroundNewEmpImageStatus(String strBackgroundNewEmpImageStatus) {
		this.strBackgroundNewEmpImageStatus = strBackgroundNewEmpImageStatus;
	}


	public File getStrBackgroundNewEmpImage() {
		return strBackgroundNewEmpImage;
	}


	public void setStrBackgroundNewEmpImage(File strBackgroundNewEmpImage) {
		this.strBackgroundNewEmpImage = strBackgroundNewEmpImage;
	}


	public String getStrBackgroundNewEmpImageFileName() {
		return strBackgroundNewEmpImageFileName;
	}


	public void setStrBackgroundNewEmpImageFileName(String strBackgroundNewEmpImageFileName) {
		this.strBackgroundNewEmpImageFileName = strBackgroundNewEmpImageFileName;
	}


	public String getStrTextResumeSubmission() {
		return strTextResumeSubmission;
	}


	public void setStrTextResumeSubmission(String strTextResumeSubmission) {
		this.strTextResumeSubmission = strTextResumeSubmission;
	}


	public String getStrSubjectResumeSubmission() {
		return strSubjectResumeSubmission;
	}


	public void setStrSubjectResumeSubmission(String strSubjectResumeSubmission) {
		this.strSubjectResumeSubmission = strSubjectResumeSubmission;
	}


	public String getStrBodyResumeSubmission() {
		return strBodyResumeSubmission;
	}


	public void setStrBodyResumeSubmission(String strBodyResumeSubmission) {
		this.strBodyResumeSubmission = strBodyResumeSubmission;
	}


	public boolean getIsEmailResumeSubmission() {
		return isEmailResumeSubmission;
	}


	public void setIsEmailResumeSubmission(boolean isEmailResumeSubmission) {
		this.isEmailResumeSubmission = isEmailResumeSubmission;
	}


	public boolean getIsTextResumeSubmission() {
		return isTextResumeSubmission;
	}


	public void setTextResumeSubmission(boolean isTextResumeSubmission) {
		this.isTextResumeSubmission = isTextResumeSubmission;
	}


	public boolean getIsEmailSelectedRound() {
		return isEmailSelectedRound;
	}


	public void setIsEmailSelectedRound(boolean isEmailSelectedRound) {
		this.isEmailSelectedRound = isEmailSelectedRound;
	}


	public boolean getIsTextSelectedRound() {
		return isTextSelectedRound;
	}


	public void setTextSelectedRound(boolean isTextSelectedRound) {
		this.isTextSelectedRound = isTextSelectedRound;
	}


	public String getStrTextSelectedRound() {
		return strTextSelectedRound;
	}


	public void setStrTextSelectedRound(String strTextSelectedRound) {
		this.strTextSelectedRound = strTextSelectedRound;
	}


	public String getStrSubjectSelectedRound() {
		return strSubjectSelectedRound;
	}


	public void setStrSubjectSelectedRound(String strSubjectSelectedRound) {
		this.strSubjectSelectedRound = strSubjectSelectedRound;
	}


	public String getStrBodySelectedRound() {
		return strBodySelectedRound;
	}


	public void setStrBodySelectedRound(String strBodySelectedRound) {
		this.strBodySelectedRound = strBodySelectedRound;
	}


	public boolean getIsTextRejectedRound() {
		return isTextRejectedRound;
	}


	public void setTextRejectedRound(boolean isTextRejectedRound) {
		this.isTextRejectedRound = isTextRejectedRound;
	}


	public String getStrTextRejectedRound() {
		return strTextRejectedRound;
	}


	public void setStrTextRejectedRound(String strTextRejectedRound) {
		this.strTextRejectedRound = strTextRejectedRound;
	}


	public String getStrSubjectRejectedRound() {
		return strSubjectRejectedRound;
	}


	public void setStrSubjectRejectedRound(String strSubjectRejectedRound) {
		this.strSubjectRejectedRound = strSubjectRejectedRound;
	}


	public String getStrBodyRejectedRound() {
		return strBodyRejectedRound;
	}


	public void setStrBodyRejectedRound(String strBodyRejectedRound) {
		this.strBodyRejectedRound = strBodyRejectedRound;
	}


	public boolean getIsEmailRejectedRound() {
		return isEmailRejectedRound;
	}


	public void setIsEmailRejectedRound(boolean isEmailRejectedRound) {
		this.isEmailRejectedRound = isEmailRejectedRound;
	}


	public boolean getIsEmailFresherResumeSubmission() {
		return isEmailFresherResumeSubmission;
	}


	public void setIsEmailFresherResumeSubmission(boolean isEmailFresherResumeSubmission) {
		this.isEmailFresherResumeSubmission = isEmailFresherResumeSubmission;
	}


	public String getStrTextFresherResumeSubmission() {
		return strTextFresherResumeSubmission;
	}


	public void setStrTextFresherResumeSubmission(String strTextFresherResumeSubmission) {
		this.strTextFresherResumeSubmission = strTextFresherResumeSubmission;
	}


	public String getStrSubjectFresherResumeSubmission() {
		return strSubjectFresherResumeSubmission;
	}


	public void setStrSubjectFresherResumeSubmission(String strSubjectFresherResumeSubmission) {
		this.strSubjectFresherResumeSubmission = strSubjectFresherResumeSubmission;
	}


	public String getStrBodyFresherResumeSubmission() {
		return strBodyFresherResumeSubmission;
	}


	public void setStrBodyFresherResumeSubmission(String strBodyFresherResumeSubmission) {
		this.strBodyFresherResumeSubmission = strBodyFresherResumeSubmission;
	}


	public boolean getIsTextFresherResumeSubmission() {
		return isTextFresherResumeSubmission;
	}


	public void setIsTextFresherResumeSubmission(boolean isTextFresherResumeSubmission) {
		this.isTextFresherResumeSubmission = isTextFresherResumeSubmission;
	}


	public boolean getIsEmailApplicationSubmissionToHT() {
		return isEmailApplicationSubmissionToHT;
	}

	public void setIsEmailApplicationSubmissionToHT(boolean isEmailApplicationSubmissionToHT) {
		this.isEmailApplicationSubmissionToHT = isEmailApplicationSubmissionToHT;
	}

	public String getStrTextApplicationSubmissionToHT() {
		return strTextApplicationSubmissionToHT;
	}

	public void setStrTextApplicationSubmissionToHT(String strTextApplicationSubmissionToHT) {
		this.strTextApplicationSubmissionToHT = strTextApplicationSubmissionToHT;
	}

	public String getStrSubjectApplicationSubmissionToHT() {
		return strSubjectApplicationSubmissionToHT;
	}

	public void setStrSubjectApplicationSubmissionToHT(String strSubjectApplicationSubmissionToHT) {
		this.strSubjectApplicationSubmissionToHT = strSubjectApplicationSubmissionToHT;
	}

	public String getStrBodyApplicationSubmissionToHT() {
		return strBodyApplicationSubmissionToHT;
	}

	public void setStrBodyApplicationSubmissionToHT(String strBodyApplicationSubmissionToHT) {
		this.strBodyApplicationSubmissionToHT = strBodyApplicationSubmissionToHT;
	}

	public void setTextApplicationSubmissionToHT(boolean isTextApplicationSubmissionToHT) {
		this.isTextApplicationSubmissionToHT = isTextApplicationSubmissionToHT;
	}

	public boolean getIsTextApplicationSubmissionToHT() {
		return isTextApplicationSubmissionToHT;
	}
//===start parvez date: 26-08-2022===
	public boolean getIsEmailEmpOnboardedBySelf() {
		return isEmailEmpOnboardedBySelf;
	}

	public void setIsEmailEmpOnboardedBySelf(boolean isEmailEmpOnboardedBySelf) {
		this.isEmailEmpOnboardedBySelf = isEmailEmpOnboardedBySelf;
	}

	public boolean getIsTextEmpOnboardedBySelf() {
		return isTextEmpOnboardedBySelf;
	}

	public void setTextEmpOnboardedBySelf(boolean isTextEmpOnboardedBySelf) {
		this.isTextEmpOnboardedBySelf = isTextEmpOnboardedBySelf;
	}

	public String getStrTextEmpOnboardedBySelf() {
		return strTextEmpOnboardedBySelf;
	}

	public void setStrTextEmpOnboardedBySelf(String strTextEmpOnboardedBySelf) {
		this.strTextEmpOnboardedBySelf = strTextEmpOnboardedBySelf;
	}

	public String getStrSubjectEmpOnboardedBySelf() {
		return strSubjectEmpOnboardedBySelf;
	}

	public void setStrSubjectEmpOnboardedBySelf(String strSubjectEmpOnboardedBySelf) {
		this.strSubjectEmpOnboardedBySelf = strSubjectEmpOnboardedBySelf;
	}

	public String getStrBodyEmpOnboardedBySelf() {
		return strBodyEmpOnboardedBySelf;
	}

	public void setStrBodyEmpOnboardedBySelf(String strBodyEmpOnboardedBySelf) {
		this.strBodyEmpOnboardedBySelf = strBodyEmpOnboardedBySelf;
	}
	
//===start parvez date: 15-02-2023===
	public boolean getIsEmailOnCircularCreation() {
		return isEmailOnCircularCreation;
	}

	public void setIsEmailOnCircularCreation(boolean isEmailOnCircularCreation) {
		this.isEmailOnCircularCreation = isEmailOnCircularCreation;
	}

	public boolean getIsTextOnCircularCreation() {
		return isTextOnCircularCreation;
	}

	public void setIsTextOnCircularCreation(boolean isTextOnCircularCreation) {
		this.isTextOnCircularCreation = isTextOnCircularCreation;
	}

	public String getStrTextOnCircularCreation() {
		return strTextOnCircularCreation;
	}

	public void setStrTextOnCircularCreation(String strTextOnCircularCreation) {
		this.strTextOnCircularCreation = strTextOnCircularCreation;
	}

	public String getStrSubjectOnCircularCreation() {
		return strSubjectOnCircularCreation;
	}

	public void setStrSubjectOnCircularCreation(String strSubjectOnCircularCreation) {
		this.strSubjectOnCircularCreation = strSubjectOnCircularCreation;
	}

	public String getStrBodyOnCircularCreation() {
		return strBodyOnCircularCreation;
	}

	public void setStrBodyOnCircularCreation(String strBodyOnCircularCreation) {
		this.strBodyOnCircularCreation = strBodyOnCircularCreation;
	}
//===end parvez date: 15-02-2023===	
}