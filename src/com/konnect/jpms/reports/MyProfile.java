package com.konnect.jpms.reports;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.payroll.ApprovePayroll;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MyProfile extends ActionSupport implements ServletRequestAware, IStatements {
 
	/** 
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionEmpId;
	String strUserType =  null;
	String strBaseUserType =  null;
	String strWLocationAccess =  null;
	public CommonFunctions CF;
	private String empId;
	String popup;
	String proPopup;
	String strAction = null;
	
	private String empImageFileName;
	private File empImage;
	private String submit;
	private String fromPage;
	
	private String empCoverImageFileName;
	private File empCoverImage;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
//		EncryptionUtility eU = new EncryptionUtility();

		strUserType = (String)session.getAttribute(USERTYPE);
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);
		strWLocationAccess = (String)session.getAttribute(WLOCATION_ACCESS);
		strSessionEmpId = (String)session.getAttribute(EMPID);
//		setEmpId(URLDecoder.decode(getEmpId()));
////		System.out.println("getEmpId() in myprofile ===>> " + getEmpId());
//		if(uF.parseToInt(getEmpId()) > 0) {
//			String encodeEmpId = eU.encode(getEmpId());
//			setEmpId(encodeEmpId);
//		}
//		System.out.println("getEmpId() in myprofile after encode ===>> " + getEmpId());
		
//		if(getEmpId() != null && uF.parseToInt(getEmpId()) == 0) {
//			String decodeEmpId = eU.decode(getEmpId());
//			setEmpId(decodeEmpId);
//		}
//		System.out.println("getEmpId() in myprofile after decode ===>> " + getEmpId());
		List<String> accessEmpList = CF.viewEmployeeIdsList(request, uF, strBaseUserType, strSessionEmpId, strWLocationAccess);
//		if((strBaseUserType != null && strUserType != null && (strBaseUserType.equals(EMPLOYEE) || strUserType.equals(EMPLOYEE))) || !accessEmpList.contains(getEmpId())) {
		// Start : Dattatray Date : 20-July-2021 Note : EncodedEmpId
//		EncryptionUtils encryption = new EncryptionUtils();
		/*if (getEmpId() !=null && (getEmpId().length()==3 || getEmpId().contains("-"))) {
			System.out.println("Emp Id : " + getEmpId() );
//			System.out.println("decryptedEmpId : " + encryption.decrypt(getEmpId()) );
			setEmpId(encryption.decrypt(getEmpId()));
		}// End : Dattatray Date : 20-July-2021 Note : EncodedEmpId
*/		
//		System.out.println("accessEmpList ===>> " + accessEmpList);
//		System.out.println("getEmpId() in myprofile ===>> " + getEmpId());
		//Created By Dattatray 09-6-2022
		strAction = request.getServletPath();
		if(strAction!=null) {
			strAction = strAction.replace("/","");
		}

		if((strBaseUserType != null && strUserType != null && (strBaseUserType.equals(EMPLOYEE) || strUserType.equals(EMPLOYEE))) || !accessEmpList.contains(getEmpId()) || (strUserType.equals(EMPLOYEE) && accessEmpList.contains(getEmpId())) ) {
			setEmpId((String)session.getAttribute(EMPID));
		}
//		System.out.println("getEmpId() in myprofile after empsessionid ===>> " + getEmpId());
		String strTrailName = "Employee ";
		if(uF.parseToInt(strSessionEmpId) == uF.parseToInt(getEmpId())) {
			strTrailName = "My ";
		}
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-group\"></i><a href=\"People.action\" style=\"color: #3c8dbc;\"> People</a></li>"
				+"<li>"+strTrailName+"Profile</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		request.setAttribute(PAGE, PMyProfile);
//		request.setAttribute(TITLE, TViewProfile);
		
		request.setAttribute("arrEnabledModules", CF.getArrEnabledModules());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		request.setAttribute("IS_DEVICE_INTEGRATION", CF.getIsDeviceIntegration());
		
//		System.out.println("empId==>"+getEmpId());
		
		/*System.out.println("empImage==>"+getEmpImageFileName());
		System.out.println("empCoverImage==>"+getEmpCoverImageFileName());*/
		Map<String,String> medicalQuest=new HashMap<String,String>();
		medicalQuest.put("1", "Are you now receiving medical attention:");
		medicalQuest.put("2", "Have you had any form of serious illness or operation");
		medicalQuest.put("3", "Have you had any illness in the last two years? YES/NO If YES, please give the details about the same and any absences from work: ");
//		medicalQuest.put("4", "Has any previous post been terminated on medical grounds?");
//		medicalQuest.put("5", "Do you have an allergies?");
		request.setAttribute("medicalQuest", medicalQuest);
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
    
		String []arrEnabledModules = CF.getArrEnabledModules();
		String strImgType = (String) request.getParameter("strImgType");
//		System.out.println("strImgType==>"+strImgType);
//		if(getSubmit()!=null && getSubmit().equals("Upload")){
		if(strImgType!=null && !strImgType.equals("")){
			uploadEmpImages(strImgType);
			loadPageVisitAuditTrail(uF);//Created By Dattatray 10-6-2022
			if(strImgType.equals("imgcover")) {
				return VIEW;
			}
			return "ajax";
		}
		
		if (getEmpId() != null && getEmpId().length() != 0) {
			viewProfile(getEmpId());
			request.setAttribute("EMPID", getEmpId());
//			getEmpReviewScore(getEmpId());
			if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, MODULE_PERFORMANCE_MANAGEMENT+"")>=0){
//				getElementList();
//				getAttributes(getEmpId());
			}
		} else {
			setEmpId((String) session.getAttribute(EMPID));
			viewProfile((String) session.getAttribute(EMPID));
//			getEmpReviewScore((String) session.getAttribute(EMPID));
			if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, MODULE_PERFORMANCE_MANAGEMENT+"")>=0){

			}
		}
		//Created By Dattatray 09-6-2022
		if(fromPage !=null && fromPage.equals("P")) {
			loadPageVisitAuditTrail(uF);
		}else {
			loadPageVisitAuditTrail(uF);//Created By Dattatray 10-6-2022
		}

		return loadProfile();
	}

	private void loadPageVisitAuditTrail(UtilityFunctions uF) {
		Connection con=null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpProfile = CF.getEmpNameMap(con,null,getEmpId());
			StringBuilder builder = new StringBuilder();
			if(fromPage !=null && fromPage.equals("P")) {//Created By Dattatray 10-6-2022
				builder.append("From page : "+fromPage);
			}
			
			builder.append("\nEmp name : "+hmEmpProfile.get(getEmpId()));
			CF.pageVisitAuditTrail(con,CF,uF, strSessionEmpId, strAction, strBaseUserType, builder.toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			db.closeConnection(con);
		}
	}
	
	private void uploadEmpImages(String strImageType) {
		try {
			if(strImageType.equals("img")) {
				UploadImage uI1 = new UploadImage();
				uI1.setServletRequest(request);
				uI1.setImageType("EMPLOYEE_IMAGE");
				uI1.setEmpImage(getEmpImage());
				uI1.setEmpImageFileName(getEmpImageFileName());
				uI1.setEmpId(getEmpId());
				uI1.setCF(CF);
				uI1.upoadImage();
			}
			
			if(strImageType.equals("imgcover")) {
				UploadImage uI2 = new UploadImage();
				uI2.setServletRequest(request);
				uI2.setImageType("EMPLOYEE_COVER_IMAGE");
				uI2.setEmpImage(getEmpCoverImage());
				uI2.setEmpImageFileName(getEmpCoverImageFileName());
				uI2.setEmpId(getEmpId());
				uI2.setCF(CF);
				uI2.upoadImage();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public String loadProfile() {
		if(popup!=null)
			return "popup";
		if(proPopup!=null)
			return "proPopup";
		return LOAD;
	}

	
	public void viewProfile(String strEmpIdReq) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		List<List<String>> alHobbies;
		List<List<String>> alLanguages;
		List<List<String>> alEducation;
		Map<String, List<String>> hmEducationDocs;
		List<List<Object>> alDocuments;
		List<List<String>> alFamilyMembers;
		List<List<String>> alPrevEmployment;
		Map<String, String> hmEmpPrevEarnDeduct;
		List<List<String>> alActivityDetails;
		List<List<String>> alMedicalDetails;
		List<Map<String,String>> empRefList;
		try {

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, strUserType, strEmpIdReq);
			
//			System.out.println("hmEmpProfile ===> " + hmEmpProfile);
			CF.getElementList(con, request);
			CF.getAttributes(con, request, strEmpIdReq);
			CF.getEmpWorkedHours(con, request, uF, strEmpIdReq);
			
			List<List<String>> alSkills = new ArrayList<List<String>>();
			alSkills = CF.selectSkills(con, uF.parseToInt(strEmpIdReq));
			Map<String, String> hmLeaveTypeName = CF.getLeaveTypeMap(con);
			
//			String levelId = CF.getEmpLevelId(con, getEmpId());f
//			List<List<String>> leaveTypeListWithBalance = CF.getLevelLeaveTypeBalanceForEmp(con, levelId, ""+getEmpId(), CF);
//			Map<String, String> hmEmpLeaveBalance = CF.getEmpExistLeaveBalance(con, ""+getEmpId(), CF);
			/*Leave Balance 
			 * **/
			EmployeeLeaveEntryReport leaveEntryReport = new EmployeeLeaveEntryReport();
			leaveEntryReport.request = request;
			leaveEntryReport.session = session;
			leaveEntryReport.CF = CF;
			leaveEntryReport.setStrEmpId(getEmpId());
			leaveEntryReport.setDataType("L");
			leaveEntryReport.viewEmployeeLeaveEntry1();
			
			int intEmpIdReq = uF.parseToInt(strEmpIdReq);
			
			viewPerkWithSalary(con,uF,getEmpId());
			viewReimbursementPartofCTC(con,uF,getEmpId());
			
//			CF.getSalaryHeadsforEmployee(con, request, CF, uF, intEmpIdReq, hmEmpProfile);
			request.setAttribute("salaryStructure", CF.getStrSalaryStructure());
			
			int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
//			System.out.println("nSalaryStrucuterType ===> " + nSalaryStrucuterType);
			if(nSalaryStrucuterType == S_GRADE_WISE) {
				getSalaryHeadsforEmployeeByGrade(con, uF, intEmpIdReq, hmEmpProfile);
			} else {
				getSalaryHeadsforEmployee(con, uF, intEmpIdReq, hmEmpProfile);
			}  
			alHobbies = CF.selectHobbies(con, intEmpIdReq);
			alLanguages = CF.selectLanguages(con, uF, intEmpIdReq);
			alEducation = CF.selectEducation(con, intEmpIdReq);
			hmEducationDocs = CF.selectEducationDocument(con, intEmpIdReq);
			empRefList = CF.selectReferences(con, intEmpIdReq);
			
			alMedicalDetails = CF.selectMedicalDetails(con, intEmpIdReq);
			
			String filePath = request.getRealPath("/userDocuments/");
			alDocuments = CF.selectDocuments(con, intEmpIdReq, filePath);

			alFamilyMembers = CF.selectFamilyMembers(con, intEmpIdReq);
			alPrevEmployment = CF.selectPrevEmploment(con, intEmpIdReq);
			hmEmpPrevEarnDeduct = CF.selectEmpPrevEarnDeduct(con, intEmpIdReq);
			
			alActivityDetails = CF.selectEmpActivityDetails(con, intEmpIdReq, uF, CF);

			boolean isFilledStatus = CF.getEmpFilledStatus(con, getEmpId());
			
			List<List<String>> alKRADetails = CF.getEmpKRADetails(con, getEmpId(), uF);
			
			request.setAttribute("alKRADetails", alKRADetails);
			request.setAttribute("alSkills", alSkills);
			
			request.setAttribute("alHobbies", alHobbies);
			request.setAttribute("alLanguages", alLanguages);
			request.setAttribute("alEducation", alEducation);
			request.setAttribute("hmEducationDocs", hmEducationDocs);
			request.setAttribute("empRefList", empRefList);
			
			request.setAttribute("alDocuments", alDocuments);
			request.setAttribute("alMedicalDetails", alMedicalDetails);
			request.setAttribute("alFamilyMembers", alFamilyMembers);

			request.setAttribute("alPrevEmployment", alPrevEmployment);
			request.setAttribute("hmEmpPrevEarnDeduct", hmEmpPrevEarnDeduct);
			
			request.setAttribute("alActivityDetails", alActivityDetails);
			request.setAttribute("isFilledStatus", isFilledStatus+"");
			request.setAttribute("alActivityDetails", alActivityDetails);
			
			request.setAttribute("hmLeaveTypeName", hmLeaveTypeName);
//			request.setAttribute("hmEmpLeaveBalance", hmEmpLeaveBalance);
//			request.setAttribute("leaveTypeListWithBalance", leaveTypeListWithBalance);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
//		return SUCCESS;

	}
	
	
	
	private void viewReimbursementPartofCTC(Connection con, UtilityFunctions uF, String strEmpId) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {

			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];

			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			String strOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			String strLevelId = CF.getEmpLevelId(con, strEmpId);

			pst = con.prepareStatement("select * from reimbursement_ctc_details where level_id=? and org_id=? and reimbursement_ctc_id in " +
					"(select reimbursement_ctc_id from reimbursement_head_details where level_id=? and org_id=? and reimbursement_head_id in " +
					"(select reimbursement_head_id from reimbursement_head_amt_details where financial_year_start=? and financial_year_end=?))" +
					" order by reimbursement_name");
			pst.setInt(1, uF.parseToInt(strLevelId));
			pst.setInt(2, uF.parseToInt(strOrgId));
			pst.setInt(3, uF.parseToInt(strLevelId));
			pst.setInt(4, uF.parseToInt(strOrgId));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alReimbursementCTC = new ArrayList<Map<String,String>>();
			while(rs.next()){
				Map<String, String> hmReimbursementCTCInner = new HashMap<String, String>();
				hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_ID", rs.getString("reimbursement_ctc_id"));
				hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_CODE", uF.showData(rs.getString("reimbursement_code"), ""));
				hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_NAME", uF.showData(rs.getString("reimbursement_name"), ""));
				hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_LEVEL_ID", rs.getString("level_id"));
				hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_ORG_ID", rs.getString("org_id"));
				hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_ADDED_BY", uF.showData(hmEmpName.get(rs.getString("added_by")), ""));
				hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_ADDED_DATE", uF.getDateFormat(rs.getString("added_date"), DBDATE, CF.getStrReportDateFormat()));
				hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_UPDATE_BY", uF.showData(hmEmpName.get(rs.getString("update_by")), ""));
				hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_UPDATE_DATE", uF.getDateFormat(rs.getString("update_date"), DBDATE, CF.getStrReportDateFormat()));
				
				alReimbursementCTC.add(hmReimbursementCTCInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("alReimbursementCTC", alReimbursementCTC);
//			System.out.println("alReimbursementCTC===>"+alReimbursementCTC);
			
			pst = con.prepareStatement("select rhd.reimbursement_ctc_id,rhd.reimbursement_head_id,rhd.reimbursement_head_code,rhd.reimbursement_head_name," +
					"rhad.reimbursement_head_amt_id,rhad.amount,rhad.is_attachment,rhad.is_optimal from reimbursement_head_details rhd, " +
					"reimbursement_head_amt_details rhad where rhd.reimbursement_head_id=rhad.reimbursement_head_id and rhd.level_id=? " +
					"and rhd.org_id=? and rhad.financial_year_start=? and rhad.financial_year_end=?");
			pst.setInt(1, uF.parseToInt(strLevelId));
			pst.setInt(2, uF.parseToInt(strOrgId));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, List<Map<String, String>>> hmReimbursementCTCHead = new HashMap<String, List<Map<String,String>>>(); 
			while(rs.next()){
				List<Map<String, String>> alReimCTCHead = hmReimbursementCTCHead.get(rs.getString("reimbursement_ctc_id"));
				if(alReimCTCHead == null) alReimCTCHead = new ArrayList<Map<String,String>>();				
				
				Map<String, String> hmReimCTCHeadInner = new HashMap<String, String>();
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_ID", rs.getString("reimbursement_head_id"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_CODE", rs.getString("reimbursement_head_code"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_NAME", rs.getString("reimbursement_head_name"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_AMT_ID", rs.getString("reimbursement_head_amt_id"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_AMOUNT", rs.getString("amount"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_IS_ATTACHMENT", rs.getString("is_attachment"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_IS_OPTIMAL", rs.getString("is_optimal"));
				
				alReimCTCHead.add(hmReimCTCHeadInner);
				
				hmReimbursementCTCHead.put(rs.getString("reimbursement_ctc_id"),alReimCTCHead);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmReimbursementCTCHead", hmReimbursementCTCHead);
//			System.out.println("hmReimbursementCTCHead===>"+hmReimbursementCTCHead);
			
			String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String[] strPayCycleDate = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrgId);
			if(strPayCycleDate !=null && strPayCycleDate.length > 0){
				String startDate = strPayCycleDate[0];
				String endDate = strPayCycleDate[1];
				String strPC = strPayCycleDate[2];
				
				pst = con.prepareStatement("select * from reimbursement_assign_head_details where emp_id=? and level_id=? and org_id=? " +
						"and financial_year_start=? and financial_year_end=? and trail_status=? and paycycle_from=? and paycycle_to=?" +
						" and paycycle=?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setInt(2, uF.parseToInt(strLevelId));
				pst.setInt(3, uF.parseToInt(strOrgId));
				pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setBoolean(6, true);
				pst.setDate(7, uF.getDateFormat(startDate, DATE_FORMAT));
				pst.setDate(8, uF.getDateFormat(endDate, DATE_FORMAT));
				pst.setInt(9, uF.parseToInt(strPC));
//				System.out.println("pst=====>"+pst);
				rs = pst.executeQuery();
				Map<String, Map<String, String>> hmAssignReimHead = new HashMap<String, Map<String,String>>();
				while(rs.next()){
					Map<String, String> hmAssignReim = new HashMap<String, String>();
					hmAssignReim.put("REIM_ASSIGN_SALARY_ID", rs.getString("reim_assign_head_id"));
					hmAssignReim.put("REIM_EMP_ID", rs.getString("emp_id"));
					hmAssignReim.put("REIM_HEAD_ID", rs.getString("reimbursement_head_id"));
					hmAssignReim.put("REIM_CTC_ID", rs.getString("reimbursement_ctc_id"));
					hmAssignReim.put("LEVEL_ID", rs.getString("level_id"));
					hmAssignReim.put("ORG_ID", rs.getString("org_id"));
					hmAssignReim.put("AMOUNT", rs.getString("amount"));
					hmAssignReim.put("FINANCIAL_YEAR_START", rs.getString("financial_year_start"));
					hmAssignReim.put("FINANCIAL_YEAR_END", rs.getString("financial_year_end"));
					hmAssignReim.put("STATUS", ""+uF.parseToBoolean(rs.getString("status")));
					hmAssignReim.put("TRAIL_STATUS", ""+uF.parseToBoolean(rs.getString("trail_status")));
					hmAssignReim.put("ADDED_BY", rs.getString("added_by"));
					hmAssignReim.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
					hmAssignReim.put("UPDATE_BY", rs.getString("update_by"));
					hmAssignReim.put("UPDATE_DATE", uF.getDateFormat(rs.getString("update_date"), DBDATE, DATE_FORMAT));
					hmAssignReim.put("PAYCYCLE_FROM", uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, DATE_FORMAT));
					hmAssignReim.put("PAYCYCLE_TO", uF.getDateFormat(rs.getString("paycycle_to"), DBDATE, DATE_FORMAT));
					hmAssignReim.put("PAYCYCLE", rs.getString("paycycle"));					
					
					hmAssignReimHead.put(rs.getString("reimbursement_head_id")+"_"+rs.getString("reimbursement_ctc_id"), hmAssignReim);
				}
				rs.close();
				pst.close();
				request.setAttribute("hmAssignReimHead", hmAssignReimHead);
//				System.out.println("hmAssignReimHead=====>"+hmAssignReimHead);	
				
				pst = con.prepareStatement("select reimbursement_head_id from reimbursement_ctc_applied_details where emp_id=? " +
						"and is_approved in (0,1) and financial_year_start=? and financial_year_end =? and reimbursement_head_id in " +
						"(select rhd.reimbursement_head_id from reimbursement_head_details rhd, reimbursement_ctc_details rcd " +
						"where rhd.reimbursement_ctc_id=rcd.reimbursement_ctc_id and rcd.level_id=? and rcd.org_id=?) " +
						"and reim_ctc_applied_id in (select reim_ctc_applied_id from reimbursement_ctc_applied_paycycle where emp_id =? " +
						"and paycycle_from=? and paycycle_to=? and paycycle=? and financial_year_start=? and financial_year_end =?)");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(strLevelId));
				pst.setInt(5, uF.parseToInt(strOrgId));
				pst.setInt(6, uF.parseToInt(strEmpId));
				pst.setDate(7, uF.getDateFormat(startDate, DATE_FORMAT));
				pst.setDate(8, uF.getDateFormat(endDate, DATE_FORMAT));
				pst.setInt(9, uF.parseToInt(strPC));
				pst.setDate(10, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(11, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst=====>"+pst); 
				rs = pst.executeQuery();
				List<String> alReimbursementCTCAppliedId = new ArrayList<String>();
				while(rs.next()){
					alReimbursementCTCAppliedId.add(rs.getString("reimbursement_head_id"));
				}
				rs.close();
				pst.close();
				request.setAttribute("alReimbursementCTCAppliedId", alReimbursementCTCAppliedId);
				
				Map<String, String> hmReimCTC = new HashMap<String, String>();
				CF.getReimbursementCTC(con, uF, uF.parseToInt(strEmpId), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(strLevelId), hmReimCTC);
				request.setAttribute("hmReimCTC", hmReimCTC);
//				System.out.println("hmReimCTC==>"+hmReimCTC);
				
				Map<String, String> hmReimCTCHeadAmount = new HashMap<String, String>();
				CF.getReimbursementCTCHeadAmount(con, uF, uF.parseToInt(strEmpId), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(strLevelId), hmReimCTCHeadAmount);
				request.setAttribute("hmReimCTCHeadAmount", hmReimCTCHeadAmount);
//				System.out.println("hmReimCTCHeadAmount==>"+hmReimCTCHeadAmount);
				request.setAttribute("strFinancialYearStart", strFinancialYearStart);
				request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void viewPerkWithSalary(Connection con, UtilityFunctions uF, String strEmpId) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {

			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];

			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			String strOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			String strLevelId = CF.getEmpLevelId(con, strEmpId);

			pst = con.prepareStatement("select * from salary_details where is_align_with_perk=true and level_id in (select level_id " +
					"from level_details where org_id=? and level_id=?) and (is_delete is null or is_delete=false)");
			pst.setInt(1, uF.parseToInt(strOrgId));
			pst.setInt(2, uF.parseToInt(strLevelId));
			rs = pst.executeQuery();
			Map<String, String> hmPerkAlign = new HashMap<String,String>(); 
			while (rs.next()){
				hmPerkAlign.put(rs.getString("salary_head_id"),rs.getString("salary_head_name"));
			}
			rs.close();
			pst.close();
			
			
			Map<String, List<Map<String, String>>> hmPerkAlignSalary = new HashMap<String, List<Map<String, String>>>();
			pst = con.prepareStatement("SELECT * FROM perk_salary_details where org_id=? and level_id=? and financial_year_start=? " +
					"and financial_year_end=? and salary_head_id in (select salary_head_id from salary_details where org_id=? " +
					"and level_id=? and (is_delete is null or is_delete =false))");
			pst.setInt(1, uF.parseToInt(strOrgId));
			pst.setInt(2, uF.parseToInt(strLevelId));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, uF.parseToInt(strOrgId));
			pst.setInt(6, uF.parseToInt(strLevelId));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<Map<String, String>> outerList = hmPerkAlignSalary.get(rs.getString("salary_head_id"));
				if (outerList == null) outerList = new ArrayList<Map<String, String>>();

				Map<String, String> hmPerkSalary = new HashMap<String, String>();
				hmPerkSalary.put("PERK_SALARY_ID",rs.getString("perk_salary_id"));
				hmPerkSalary.put("PERK_CODE",uF.showData(rs.getString("perk_code"), ""));
				hmPerkSalary.put("PERK_NAME",uF.showData(rs.getString("perk_name"), ""));
				hmPerkSalary.put("PERK_DESCRIPTION",uF.showData(rs.getString("perk_description"), ""));
				hmPerkSalary.put("PERK_AMOUNT",uF.showData(rs.getString("amount"), ""));
				hmPerkSalary.put("PERK_USER",hmEmpName.get(rs.getString("user_id")));
				hmPerkSalary.put("ENTRY_DATE",uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmPerkSalary.put("FINANCIAL_YEAR",uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, CF.getStrReportDateFormat()) + " to "
						+ uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, CF.getStrReportDateFormat()));
				hmPerkSalary.put("PERK_ATTACHMENT",rs.getString("is_attachment"));
				hmPerkSalary.put("PERK_IS_OPTIMAL",rs.getString("is_optimal"));

				outerList.add(hmPerkSalary);

				hmPerkAlignSalary.put(rs.getString("salary_head_id"), outerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmPerkAlign", hmPerkAlign);
			request.setAttribute("hmPerkAlignSalary", hmPerkAlignSalary);
			
			String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String[] strPayCycleDate = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrgId);
			if(strPayCycleDate !=null && strPayCycleDate.length > 0){
				String startDate = strPayCycleDate[0];
				String endDate = strPayCycleDate[1];
				String strPC = strPayCycleDate[2];
				
				pst = con.prepareStatement("select * from perk_assign_salary_details where emp_id=? and level_id=? and org_id=? " +
						"and financial_year_start=? and financial_year_end=? and trail_status=? and paycycle_from=? and paycycle_to=?" +
						" and paycycle=?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setInt(2, uF.parseToInt(strLevelId));
				pst.setInt(3, uF.parseToInt(strOrgId));
				pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setBoolean(6, true);
				pst.setDate(7, uF.getDateFormat(startDate, DATE_FORMAT));
				pst.setDate(8, uF.getDateFormat(endDate, DATE_FORMAT));
				pst.setInt(9, uF.parseToInt(strPC));
//				System.out.println("pst=====>"+pst);
				rs = pst.executeQuery();
				Map<String, Map<String, String>> hmAssignPerkSalary = new HashMap<String, Map<String,String>>();
				while(rs.next()){
					Map<String, String> hmAssignPerk = new HashMap<String, String>();
					hmAssignPerk.put("PERK_ASSIGN_SALARY_ID", rs.getString("perk_assign_salary_id"));
					hmAssignPerk.put("PERK_EMP_ID", rs.getString("emp_id"));
					hmAssignPerk.put("PERK_SALARY_ID", rs.getString("perk_salary_id"));
					hmAssignPerk.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
					hmAssignPerk.put("LEVEL_ID", rs.getString("level_id"));
					hmAssignPerk.put("ORG_ID", rs.getString("org_id"));
					hmAssignPerk.put("AMOUNT", rs.getString("amount"));
					hmAssignPerk.put("FINANCIAL_YEAR_START", rs.getString("financial_year_start"));
					hmAssignPerk.put("FINANCIAL_YEAR_END", rs.getString("financial_year_end"));
					hmAssignPerk.put("STATUS", ""+uF.parseToBoolean(rs.getString("status")));
					hmAssignPerk.put("TRAIL_STATUS", ""+uF.parseToBoolean(rs.getString("trail_status")));
					hmAssignPerk.put("ADDED_BY", rs.getString("added_by"));
					hmAssignPerk.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
					hmAssignPerk.put("UPDATE_BY", rs.getString("update_by"));
					hmAssignPerk.put("UPDATE_DATE", uF.getDateFormat(rs.getString("update_date"), DBDATE, DATE_FORMAT));
					hmAssignPerk.put("PAYCYCLE_FROM", uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, DATE_FORMAT));
					hmAssignPerk.put("PAYCYCLE_TO", uF.getDateFormat(rs.getString("paycycle_to"), DBDATE, DATE_FORMAT));
					hmAssignPerk.put("PAYCYCLE", rs.getString("paycycle"));					
					
					hmAssignPerkSalary.put(rs.getString("salary_head_id")+"_"+rs.getString("perk_salary_id"), hmAssignPerk);
				}
				rs.close();
				pst.close();
				request.setAttribute("hmAssignPerkSalary", hmAssignPerkSalary);
//				System.out.println("hmAssignPerkSalary=====>"+hmAssignPerkSalary);
				
				pst = con.prepareStatement("select * from perk_salary_applied_details where emp_id=? and is_approved in (0,1) " +
						"and financial_year_start=? and financial_year_end = ? and perk_salary_id in (select perk_salary_id " +
						"from perk_salary_details where financial_year_start=? and financial_year_end = ? " +
						"and salary_head_id in (select salary_head_id from salary_details where is_align_with_perk=true " +
						"and level_id in (select level_id from level_details where level_id=? and org_id=?) " +
						"and (is_delete is null or is_delete=false)))");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(6, uF.parseToInt(strLevelId));
				pst.setInt(7, uF.parseToInt(strOrgId));
//				System.out.println("pst=====>"+pst);
				rs = pst.executeQuery();
				List<String> alPerkSalaryAppliedId = new ArrayList<String>();
				while(rs.next()){
					alPerkSalaryAppliedId.add(rs.getString("perk_salary_id"));
				}
				rs.close();
				pst.close();
				request.setAttribute("alPerkSalaryAppliedId", alPerkSalaryAppliedId);
				
				Map<String, String> hmPerkAlignAmount = new HashMap<String, String>();
				CF.getPerkAlignAmount(con, uF, uF.parseToInt(strEmpId), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(strLevelId), hmPerkAlignAmount);
				request.setAttribute("hmPerkAlignAmount", hmPerkAlignAmount);
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getSalaryHeadsforEmployeeByGrade(Connection con, UtilityFunctions uF, int intEmpIdReq, Map<String, String> hmEmpProfile) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];


//			Map<String, String> hmEmpGradeMap = CF.getEmpGradeMap(con); 
//			String gradeId = hmEmpGradeMap.get(getEmpId());
			String gradeId = CF.getEmpGradeId(con, ""+intEmpIdReq);
			
			String strOrg = CF.getEmpOrgId(con, uF, ""+intEmpIdReq);
			
			String currId = CF.getOrgCurrencyIdByOrg(con, strOrg);
			Map<String, Map<String, String>> hmCurrencyDetailsMap =  CF.getCurrencyDetails(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String,String>>();
			Map<String, String> hmCurr = hmCurrencyDetailsMap.get(currId);
			if(hmCurr == null) hmCurr = new HashMap<String, String>();
			String strCurr = hmCurr.get("LONG_CURR")!=null && !hmCurr.get("LONG_CURR").equalsIgnoreCase("null") ? hmCurr.get("LONG_CURR")+" " : "";
			request.setAttribute("strCurr", strCurr);

//			Map<String, String> hmEmpGenderMap = CF.getEmpGenderMap(con);
//			Map<String, String> hmEmpAgeMap = CF.getEmpAgeMap(con,CF);
//			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			
			String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String[] strPayCycleDates = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrg);
			
			if(strPayCycleDates!=null && strPayCycleDates.length > 0){
				String strD1 = strPayCycleDates[0];
				String strD2 = strPayCycleDates[1];
				String strPC = strPayCycleDates[2];
				
				int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
			
				Map hmEmpMertoMap = new HashMap();
				Map hmEmpWlocationMap = new HashMap();
				Map hmEmpStateMap = new HashMap();
				CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
				
				String strStateId = (String)hmEmpStateMap.get(""+intEmpIdReq);
				String strEmpGender = CF.getEmpGender(con, uF, ""+intEmpIdReq);
				
//				Map<String, String> hmEmpServiceTaxMap = CF.getEmpServiceTax(con, uF, CF);
				
				pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
	//			System.out.println(" pst==>"+pst);
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
					
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and section_id=3 order by section_code");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
	//			System.out.println(" pst==>"+pst);
				double dblInvestmentExemption = 0.0d;
				if (rs.next()) {
					dblInvestmentExemption = uF.parseToDouble(rs.getString("section_exemption_limit"));
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmSalaryDetails = new HashMap<String, String>();
				List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
				List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
				pst = con.prepareStatement("select * from salary_details where salary_head_id not in ("+GROSS+","+CTC+","+REIMBURSEMENT_CTC+")" +
					" and grade_id =? order by earning_deduction desc, salary_head_id, weight");
				pst.setInt(1, uF.parseToInt(gradeId));
				rs = pst.executeQuery();  
				List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
				List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
				while(rs.next()){
					
					if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("E")){
						int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
						
						if(index>=0){
							alEmpSalaryDetailsEarning.remove(index);
							alEarningSalaryDuplicationTracer.remove(index);
							alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
						}else{
							alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
						}
						
						alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					}else if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("D")){
						int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
						if(index>=0){
							alEmpSalaryDetailsDeduction.remove(index);
							alDeductionSalaryDuplicationTracer.remove(index);
							alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
						}else{
							alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
						}
						alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					}
					
					hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
				}
				rs.close();
				pst.close();
				
	
				Map<String, Double> hmSalaryTotal = new LinkedHashMap<String, Double>();
				double grossAmount = 0.0d;
				double grossYearAmount = 0.0d;
				double deductAmount = 0.0d;
				double deductYearAmount = 0.0d;
				
				ApprovePayroll objAP = new ApprovePayroll();
				objAP.CF = CF;
				objAP.session = session;
				objAP.request = request; 
				
//				Map<String, Map<String, String>> hmEmpPaidAmountDetails =  objAP.getEmpPaidAmountDetails(con, uF, strFinancialYearStart, strFinancialYearEnd);
//				Map<String, String> hmEmpExemptionsMap = objAP.getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, dblInvestmentExemption);
//				Map<String, String> hmEmpHomeLoanMap = objAP.getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd);
//				Map<String,String> hmFixedExemptions = objAP.getFixedExemption(con, uF, strFinancialYearStart, strFinancialYearEnd);
//				Map<String, String> hmEmpRentPaidMap = objAP.getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
				
//				Map<String, String> hmPrevEmpTdsAmount  = new HashMap<String, String>();
//				Map<String, String> hmPrevEmpGrossAmount  = new HashMap<String, String>();
//				objAP.getPrevEmpTdsAmount(con,uF,strFinancialYearStart,strFinancialYearEnd,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount);
				
//				Map<String, String> hmEmpIncomeOtherSourcesMap = objAP.getEmpIncomeOtherSources(con, uF, strFinancialYearStart, strFinancialYearEnd);
			
				pst = con.prepareStatement("select * from vda_rate_details where desig_id = ? and from_date = (select max(from_date) as from_date from vda_rate_details where desig_id = ? and from_date <=?)");
				pst.setInt(1, uF.parseToInt(hmEmpProfile.get("DESIGNATION_ID")));
				pst.setInt(2, uF.parseToInt(hmEmpProfile.get("DESIGNATION_ID")));
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				double dblVDAAmount = 0.0d;
				while(rs.next()) {
					if(hmEmpProfile.get("EMPLOYMENT_TYPE")!=null && hmEmpProfile.get("EMPLOYMENT_TYPE").equalsIgnoreCase("PROBATION")) {
						dblVDAAmount = rs.getDouble("vda_amount_probation");
					} else if(hmEmpProfile.get("EMPLOYMENT_TYPE")!=null && hmEmpProfile.get("EMPLOYMENT_TYPE").equalsIgnoreCase("PERMANENT")) {
						dblVDAAmount = rs.getDouble("vda_amount_permanent");
					} else if(hmEmpProfile.get("EMPLOYMENT_TYPE")!=null && hmEmpProfile.get("EMPLOYMENT_TYPE").equalsIgnoreCase("TEMPORARY")) {
						dblVDAAmount = rs.getDouble("vda_amount_temporary");
					}
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmPerkAlignAmount = (Map<String, String>) request.getAttribute("hmPerkAlignAmount");
				if(hmPerkAlignAmount == null) hmPerkAlignAmount = new HashMap<String, String>();
				
				pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
					"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true " +
					"and isdisplay=true and grade_id = ?) AND effective_date <= ? and grade_id = ? group by salary_head_id) a, emp_salary_details esd " +
					"WHERE a.emp_salary_id=esd.emp_salary_id and a.salary_head_id=esd.salary_head_id and emp_id = ? " +
					"AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? " +
					"and is_approved = true and isdisplay=true and grade_id = ?) AND esd.effective_date <= ? " +
					"and esd.grade_id = ?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
					"WHERE sd.grade_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) " +
					"order by sd.earning_deduction desc, weight");
				pst.setInt(1, intEmpIdReq);
				pst.setInt(2, intEmpIdReq);
				pst.setInt(3, uF.parseToInt(gradeId));
				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(5, uF.parseToInt(gradeId));
				pst.setInt(6, intEmpIdReq);
				pst.setInt(7, intEmpIdReq);
				pst.setInt(8, uF.parseToInt(gradeId));
				pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(10, uF.parseToInt(gradeId));
				pst.setInt(11, uF.parseToInt(gradeId));
//				System.out.println("pst ===>> " + pst); 
				rs = pst.executeQuery();
				List alSalaryDuplicationTracer = new ArrayList();
				List<List<String>> salaryHeadDetailsList = new ArrayList<List<String>>();
				Map<String, String> hmTotal = new HashMap<String, String>();
				double dblGrossTDS = 0.0d;
				boolean isEPF = false;
				boolean isESIC = false;
				boolean isLWF = false;
				while (rs.next()) {
	
					if(uF.parseToInt(rs.getString("salary_head_id")) == CTC){
						continue;
					}
					
					if(!uF.parseToBoolean(rs.getString("isdisplay"))){
						continue;
					}
					
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("salary_head_name"));
					innerList.add(rs.getString("earning_deduction"));
	//				innerList.add(uF.parseToBoolean(rs.getString("isdisplay"))? rs.getString("amount") : "0");
	//				double dblYearAmount = (uF.parseToBoolean(rs.getString("isdisplay")) ? rs.getDouble("amount") : 0.0d )* 12;
	//				innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
	
					if(rs.getString("earning_deduction").equals("E")) {
						if(uF.parseToBoolean(rs.getString("isdisplay"))) {
							double dblAmount = rs.getDouble("amount");
							if(hmPerkAlignAmount.containsKey(rs.getString("salary_head_id"))){
								dblAmount = uF.parseToDouble(hmPerkAlignAmount.get(rs.getString("salary_head_id")));
							}
							
							if(rs.getInt("salary_head_id") == VDA && !uF.parseToBoolean(hmEmpProfile.get("EMP_IS_DISABLE_SAL_CALCULATE"))) {
								dblAmount = dblVDAAmount;
							}
							double dblYearAmount = dblAmount * 12;
							
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
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
							
							hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount);
						} else {
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							hmTotal.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
						}
					} else if(rs.getString("earning_deduction").equals("D")) {
						if(uF.parseToBoolean(rs.getString("isdisplay"))){
	//						int nPayMonth = uF.parseToInt(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, "MM"));
//							System.out.println("sal Head Id ===>> " + rs.getInt("salary_head_id"));
							switch(rs.getInt("salary_head_id")) {
														
								case PROFESSIONAL_TAX :
									  
									double dblAmount = calculateProfessionalTax(con, uF, grossAmount,strFinancialYearStart, strFinancialYearEnd, nPayMonth, strStateId,strEmpGender);
									double dblYearAmount =  getAnnualProfessionalTax(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, strStateId, strEmpGender);
									
									deductAmount += dblAmount;
									deductYearAmount += dblYearAmount;
									
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
									innerList.add(rs.getString("salary_head_id"));
									innerList.add(rs.getString("is_variable"));
									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount);
									
									break;
								
								case EMPLOYEE_EPF :
									isEPF = true;	
									double dblAmount1 = objAP.calculateEEPF(con, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmTotal, ""+intEmpIdReq, null, null, false, null);
									double dblYearAmount1 = dblAmount1 * 12;
									
									deductAmount += dblAmount1;
									deductYearAmount += dblYearAmount1;
									
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount1));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount1));
									innerList.add(rs.getString("salary_head_id"));
									innerList.add(rs.getString("is_variable"));
									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount1);
									
									break;
								
//								case EMPLOYER_EPF :
//									
//									double dblAmount2 = objAP.calculateERPF(con, CF, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, ""+intEmpIdReq, null, null, false, null);
//									double dblYearAmount2 = dblAmount2 * 12;
//									
//									deductAmount += dblAmount2;
//									deductYearAmount += dblYearAmount2;
//									
//									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount2));
//									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount2));
//									innerList.add(rs.getString("salary_head_id"));
//									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount2);
//									
//									break;  
								
//								case EMPLOYER_ESI :
//									
//									double dblAmount3 = objAP.calculateERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId,""+intEmpIdReq);
//									double dblYearAmount3 = dblAmount3 * 12;
//									
//									deductAmount += dblAmount3;
//									deductYearAmount += dblYearAmount3;
//									
//									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount3));
//									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount3));
//									innerList.add(rs.getString("salary_head_id"));
//									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount3);
//									
//									break;
								
								case EMPLOYEE_ESI :
									isESIC = true;
//									System.out.println("in EMPLOYEE_ESI ========>> ");
									double dblAmount4 = objAP.calculateEEESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, ""+intEmpIdReq);
									dblAmount4 = Math.ceil(dblAmount4);
									
//									System.out.println("dblAmount4 ===>> " + dblAmount4);
									double dblYearAmount4 = dblAmount4 * 12;
									dblYearAmount4 = Math.ceil(dblYearAmount4);
									
									deductAmount += dblAmount4;
									deductYearAmount += dblYearAmount4;
									
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount4));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount4));
									innerList.add(rs.getString("salary_head_id"));
									innerList.add(rs.getString("is_variable"));
									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount4);
									
									break;
								
//								case EMPLOYER_LWF :
//									
//									double dblAmount5 = objAP.calculateERLWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, nPayMonth);
//									double dblYearAmount5 = dblAmount5 * 12;
//									
//									deductAmount += dblAmount5;
//									deductYearAmount += dblYearAmount5;
//									
//									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount5));
//									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount5));
//									innerList.add(rs.getString("salary_head_id"));
//									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount5);
//									
//									break;
								
								case EMPLOYEE_LWF :
									isLWF = true;
									double dblAmount6 = objAP.calculateEELWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, ""+intEmpIdReq, nPayMonth, strOrg);
									double dblYearAmount6 = dblAmount6 * 12;
									
									deductAmount += dblAmount6;
									deductYearAmount += dblYearAmount6;
									
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount6));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount6));
									innerList.add(rs.getString("salary_head_id"));
									innerList.add(rs.getString("is_variable"));
									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount6);
									
									break;
								
								case TDS :
									
									/*double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
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
									
									Map<String, String> hmPaidSalaryDetails =  hmEmpPaidAmountDetails.get(""+intEmpIdReq);
									if(hmPaidSalaryDetails==null){hmPaidSalaryDetails=new HashMap<String, String>();}
									
									double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(""+intEmpIdReq)+"_EDU_TAX"));
									double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(""+intEmpIdReq)+"_STD_TAX"));
									double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(""+intEmpIdReq)+"_FLAT_TDS"));
									 
									 
									if(hmEmpServiceTaxMap.containsKey(""+intEmpIdReq)){
										dblGrossTDS = grossAmount;
										double  dblServiceTaxAmount = uF.parseToDouble(hmTotal.get(SERVICE_TAX+""));
										dblGrossTDS = dblGrossTDS - dblServiceTaxAmount;
										
										double dblSwachhaBharatCess = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS+""));
										dblGrossTDS = dblGrossTDS - dblSwachhaBharatCess;
										
										double dblKrishiKalyanCess = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS+""));
										dblGrossTDS = dblGrossTDS - dblKrishiKalyanCess;
									}
									
									double dblAmount7 = objAP.calculateTDS(con, uF,strD2,strD1, dblGrossTDS, dblCess1, dblCess2, dblFlatTDS, dblInvestmentExemption, dblHRA, dblHraSalHeadsAmount,
											nPayMonth,
											strD1, strFinancialYearStart, strFinancialYearEnd, ""+intEmpIdReq, hmEmpGenderMap.get(""+intEmpIdReq),  hmEmpAgeMap.get(""+intEmpIdReq), strStateId,
											hmEmpExemptionsMap, hmEmpHomeLoanMap, hmFixedExemptions, hmEmpMertoMap, hmEmpRentPaidMap, hmPaidSalaryDetails,
											hmTotal, hmSalaryDetails, hmEmpLevelMap, CF,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount,hmEmpIncomeOtherSourcesMap,hmOtherTaxDetails,hmEmpStateMap);
									
									double dblYearAmount7 = dblAmount7 * 12;
									
									deductAmount += dblAmount7;
									deductYearAmount += dblYearAmount7;
									
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount7));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount7));
									innerList.add(rs.getString("salary_head_id"));
									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount7);*/
									
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
									innerList.add(rs.getString("salary_head_id"));
									innerList.add(rs.getString("is_variable"));
									hmTotal.put(rs.getString("salary_head_id"), ""+0.0d);
									
									break;
								
								default:
									
									double dblAmount9 = uF.parseToDouble(rs.getString("amount"));
									double dblYearAmount9 = dblAmount9 * 12;
									
									deductAmount += dblAmount9;
									deductYearAmount += dblYearAmount9;
									
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount9));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount9));
									innerList.add(rs.getString("salary_head_id"));
									innerList.add(rs.getString("is_variable"));
									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount9);
									
									break;
							}
						}  else {
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							hmTotal.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
						}
					}
					
					int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
					
					if(index>=0){
						salaryHeadDetailsList.remove(index);
						salaryHeadDetailsList.add(index, innerList);
					}else{
						alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
						salaryHeadDetailsList.add(innerList);
					}
				}
	
				hmSalaryTotal.put("GROSS_AMOUNT", grossAmount);
				hmSalaryTotal.put("GROSS_YEAR_AMOUNT", grossYearAmount);
				hmSalaryTotal.put("DEDUCT_AMOUNT", deductAmount);
				hmSalaryTotal.put("DEDUCT_YEAR_AMOUNT", deductYearAmount);
				
				request.setAttribute("hmSalaryTotal", hmSalaryTotal);
				request.setAttribute("salaryHeadDetailsList", salaryHeadDetailsList);
//				System.out.println("salaryHeadDetailsList======>"+salaryHeadDetailsList);
				/**
				 * Employer Contribution
				 * */
				Map<String,String> hmContribution = new HashMap<String, String>();
//				System.out.println("isEPF======>"+isEPF);
				if(isEPF){
//					double dblAmount = objAP.calculateERPFandEPS(con, CF, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, ""+intEmpIdReq, null, null, false, null);
					double dblAmount = objAP.calculateERPF(con,CF, null, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, ""+intEmpIdReq, null, null, false, null);
//					dblAmount = Math.round(dblAmount);
//					System.out.println("MP.java/1207----dblAmount=="+dblAmount);
					double dblYearAmount = dblAmount * 12;
					hmContribution.put("EPF_MONTHLY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
					hmContribution.put("EPF_ANNUALY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
				}
				if(isESIC){
					double dblAmount = objAP.calculateERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId,""+intEmpIdReq, null, null);
					dblAmount = Math.ceil(dblAmount);
					double dblYearAmount = dblAmount * 12;
					dblYearAmount = Math.ceil(dblYearAmount);
					
					hmContribution.put("ESI_MONTHLY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
					hmContribution.put("ESI_ANNUALY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
				}
				if(isLWF){
					double dblAmount = objAP.calculateERLWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, nPayMonth, strOrg);
//					dblAmount = Math.round(dblAmount);
					double dblYearAmount = dblAmount * 12;
					hmContribution.put("LWF_MONTHLY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
					hmContribution.put("LWF_ANNUALY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));				
				}
				request.setAttribute("isEPF", ""+isEPF);
				request.setAttribute("isESIC", ""+isESIC);
				request.setAttribute("isLWF", ""+isLWF);
				request.setAttribute("hmContribution", hmContribution);
				
				
				pst = con.prepareStatement("select amount from payroll_generation where emp_id = ? and salary_head_id = ? and financial_year_from_date=? and financial_year_to_date=?");
				pst.setInt(1, intEmpIdReq);
				pst.setInt(2, PROFESSIONAL_TAX);
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
	//			System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				double dblAmount = 0;
				int nMonthCount = 0;
				while(rs.next()){
					dblAmount += uF.parseToDouble(rs.getString("amount"));
					nMonthCount++;
				}
				rs.close();
				pst.close(); 
				
				int nTotalCount = 12;
				
				if(((String)hmEmpProfile.get("JOINING_DATE")) != null && !((String)hmEmpProfile.get("JOINING_DATE")).trim().equals("") && !((String)hmEmpProfile.get("JOINING_DATE")).trim().equalsIgnoreCase("NULL")){
					java.util.Date dtJoiningDt = uF.getDateFormatUtil((String)hmEmpProfile.get("JOINING_DATE"), CF.getStrReportDateFormat());
					java.util.Date dtFinancialYearStartDt = uF.getDateFormatUtil(strFinancialYearStart, DATE_FORMAT);
					java.util.Date dtFinancialYearEndDt = uF.getDateFormatUtil(strFinancialYearEnd, DATE_FORMAT);
					if(dtJoiningDt!=null && dtJoiningDt.before(dtFinancialYearStartDt)) {
						nTotalCount = 12;
					} else if(dtJoiningDt != null) {
						int m1 = dtJoiningDt.getYear() * 12 + dtJoiningDt.getMonth();
					    int m2 = dtFinancialYearEndDt.getYear() * 12 + dtFinancialYearEndDt.getMonth();
					    nTotalCount = m2 - m1 + 1;
					}
				}
				int nRemainingCount = nTotalCount - nMonthCount;
				
				
				pst = con.prepareStatement("select amount from emp_salary_details where emp_id = ? " +
						"and earning_deduction = ? and is_approved =true " +
						"and effective_date = (select max(effective_date) from emp_salary_details " +
						"where emp_id = ? and earning_deduction = ? and is_approved = true and grade_id=?) and grade_id=?");
				pst.setInt(1, intEmpIdReq);
				pst.setString(2, "E");
				pst.setInt(3, intEmpIdReq);
				pst.setString(4, "E");
				pst.setInt(5, uF.parseToInt(gradeId));
				pst.setInt(6, uF.parseToInt(gradeId));
				rs = pst.executeQuery();
				double dblGrossAmount = 0;
				
				while(rs.next()){
					dblGrossAmount += uF.parseToDouble(rs.getString("amount"));
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from deduction_details_india where income_from<=? and income_to>=? and state_id=? " +
						"and financial_year_from=? and financial_year_to=? and gender =? limit 1");
				pst.setDouble(1, dblGrossAmount);
				pst.setDouble(2, dblGrossAmount);
				pst.setInt(3, uF.parseToInt((String)hmEmpStateMap.get(intEmpIdReq+"")));
				pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(6, strEmpGender);
				rs = pst.executeQuery();				
	//			System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				double dblDeductionAmount = 0;
				while(rs.next()) {
					dblDeductionAmount = rs.getDouble("deduction_amount");
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
				if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
				String strCurrency = "";
				if(uF.parseToInt(hmEmpCurrency.get(intEmpIdReq+"")) > 0){
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(intEmpIdReq+""));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				} 
				
				
				ApprovePayroll objAppPayroll = new ApprovePayroll();
				objAppPayroll.session = session;
				objAppPayroll.request = request;
				objAppPayroll.CF = CF;
				
				double dblMonthlyAmount = objAppPayroll.calculateProfessionalTax(con, uF, null, dblGrossAmount, strFinancialYearStart, strFinancialYearEnd, 6, (String)hmEmpStateMap.get(intEmpIdReq+""), strEmpGender);
				double dblVar = dblDeductionAmount - (dblMonthlyAmount * 12);
				dblAmount = dblAmount + (dblMonthlyAmount * nRemainingCount) + dblVar;
				
				request.setAttribute("dblAmount", strCurrency+uF.formatIntoOneDecimal(dblAmount));
				request.setAttribute("dblMonthlyAmount", strCurrency+uF.formatIntoOneDecimal(dblMonthlyAmount));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private double getAnnualProfessionalTax(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, 
			String strStateId, String strEmpGender) {
		
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

	
	
	
	public void getSalaryHeadsforEmployee(Connection con, UtilityFunctions uF, int intEmpIdReq, Map<String, String> hmEmpProfile) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
//			System.out.println("getSalaryHeadsforEmployee ===> ");
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];
			
			String levelId = CF.getEmpLevelId(con, ""+intEmpIdReq);			
			String strOrg = CF.getEmpOrgId(con, uF, ""+intEmpIdReq);
			
			String currId = CF.getOrgCurrencyIdByOrg(con, strOrg);
			Map<String, Map<String, String>> hmCurrencyDetailsMap =  CF.getCurrencyDetails(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String,String>>();
			Map<String, String> hmCurr = hmCurrencyDetailsMap.get(currId);
			if(hmCurr == null) hmCurr = new HashMap<String, String>();
			String strCurr = hmCurr.get("LONG_CURR")!=null && !hmCurr.get("LONG_CURR").equalsIgnoreCase("null") ? hmCurr.get("LONG_CURR")+" " : "";
			request.setAttribute("strCurr", strCurr);

//			Map<String, String> hmEmpGenderMap = CF.getEmpGenderMap(con);
//			Map<String, String> hmEmpAgeMap = CF.getEmpAgeMap(con,CF);
			
			String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String[] strPayCycleDates = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrg);
			
			if(strPayCycleDates!=null && strPayCycleDates.length > 0){
				String strD1 = strPayCycleDates[0];
				String strD2 = strPayCycleDates[1];
				String strPC = strPayCycleDates[2];
				
				int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
			
				Map hmEmpMertoMap = new HashMap();
				Map hmEmpWlocationMap = new HashMap();
				Map hmEmpStateMap = new HashMap();
				CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
				
				String strStateId = (String)hmEmpStateMap.get(""+intEmpIdReq);
				String strEmpGender = CF.getEmpGender(con, uF, ""+intEmpIdReq);
				
//				Map<String, String> hmEmpServiceTaxMap = CF.getEmpServiceTax(con, uF, CF);
				
				pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
	//			System.out.println(" pst==>"+pst);
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
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and section_id=3 order by section_code");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
	//			System.out.println(" pst==>"+pst);
				double dblInvestmentExemption = 0.0d;
				if (rs.next()) {
					dblInvestmentExemption = uF.parseToDouble(rs.getString("section_exemption_limit"));
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from annual_variable_details where level_id=? and org_id=? " +
						"and financial_year_start=? and financial_year_end=? and emp_id=? and salary_head_id in (select salary_head_id from salary_details " +
						"where is_annual_variable=true and (is_delete is null or is_delete = false) and (is_contribution is null or is_contribution=false))");
				pst.setInt(1, uF.parseToInt(levelId));
				pst.setInt(2, uF.parseToInt(strOrg));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, intEmpIdReq);
				rs = pst.executeQuery();
				Map<String, String> hmAnnualVariableAmt = new HashMap<String, String>();
				while(rs.next()){
					hmAnnualVariableAmt.put(rs.getString("salary_head_id"), rs.getString("variable_amount"));
				}
				rs.close();
				pst.close();
				request.setAttribute("hmAnnualVariableAmt", hmAnnualVariableAmt);
				
				pst = con.prepareStatement("select * from annual_variable_details where level_id=? and org_id=? " +
						"and financial_year_start=? and financial_year_end=? and emp_id=? and salary_head_id in (select salary_head_id from salary_details " +
						"where is_contribution=true and (is_delete is null or is_delete = false))");
				pst.setInt(1, uF.parseToInt(levelId));
				pst.setInt(2, uF.parseToInt(strOrg));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, intEmpIdReq);
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				Map<String, String> hmContributionSalHeadAmt = new HashMap<String, String>();
				while(rs.next()){
					hmContributionSalHeadAmt.put(rs.getString("salary_head_id"), rs.getString("variable_amount"));
				}
				rs.close();
				pst.close();
				request.setAttribute("hmContributionSalHeadAmt", hmContributionSalHeadAmt);
				
				pst = con.prepareStatement("SELECT * FROM emp_salary_details WHERE emp_id=? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
					"WHERE salary_head_id in ("+CTC+") and emp_id=? and is_approved = true and isdisplay=true and level_id=?) and salary_head_id in ("+CTC+") AND effective_date<=? and level_id=?");
				pst.setInt(1, intEmpIdReq);
				pst.setInt(2, intEmpIdReq);
				pst.setInt(3, uF.parseToInt(levelId));
				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(5, uF.parseToInt(levelId));
				rs = pst.executeQuery();
				String ctcAmt = "0";
				while(rs.next()){
					ctcAmt = rs.getString("amount");
				}
				rs.close();
				pst.close();
				String salaryBandId = CF.getSalaryBandId(con, ctcAmt, levelId);
				
				
				Map<String, String> hmSalaryDetails = new HashMap<String, String>();
				List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
				List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
				pst = con.prepareStatement("select * from salary_details where salary_head_id not in ("+GROSS+","+CTC+") and org_id=? and level_id=? order by earning_deduction desc, salary_head_id, weight");
				pst.setInt(1, uF.parseToInt(strOrg));
				pst.setInt(2, uF.parseToInt(levelId)); 
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();  
				List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
				List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
				while(rs.next()){
					if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("E")){
						int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
						
						if(index>=0){
							alEmpSalaryDetailsEarning.remove(index);
							alEarningSalaryDuplicationTracer.remove(index);
							alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
						}else{
							alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
						}
						
						alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					}else if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("D")){
						int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
						if(index>=0){
							alEmpSalaryDetailsDeduction.remove(index);
							alDeductionSalaryDuplicationTracer.remove(index);
							alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
						}else{
							alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
						}
						alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					}
					
					hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
				}
				rs.close();
				pst.close();				
	
				Map<String, Double> hmSalaryTotal = new LinkedHashMap<String, Double>();
				double grossAmount = 0.0d;
				double grossYearAmount = 0.0d;
				double deductAmount = 0.0d;
				double deductYearAmount = 0.0d;				
				
				ApprovePayroll objAP = new ApprovePayroll();
				objAP.CF = CF;
				objAP.session = session;
				objAP.request = request; 
				
//				Map<String, Map<String, String>> hmEmpPaidAmountDetails =  objAP.getEmpPaidAmountDetails(con, uF, strFinancialYearStart, strFinancialYearEnd);
//				Map<String, String> hmEmpExemptionsMap = objAP.getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, dblInvestmentExemption);
//				Map<String, String> hmEmpHomeLoanMap = objAP.getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd);
//				Map<String,String> hmFixedExemptions = objAP.getFixedExemption(con, uF, strFinancialYearStart, strFinancialYearEnd);
//				Map<String, String> hmEmpRentPaidMap = objAP.getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
				
//				Map<String, String> hmPrevEmpTdsAmount  = new HashMap<String, String>();
//				Map<String, String> hmPrevEmpGrossAmount  = new HashMap<String, String>();
//				objAP.getPrevEmpTdsAmount(con,uF,strFinancialYearStart,strFinancialYearEnd,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount);
				
//				Map<String, String> hmEmpIncomeOtherSourcesMap = objAP.getEmpIncomeOtherSources(con, uF, strFinancialYearStart, strFinancialYearEnd);
				
				pst = con.prepareStatement("SELECT * FROM gratuity_details where org_id=? and effective_date<=? order by effective_date desc limit 1");
				pst.setInt(1, uF.parseToInt(strOrg));
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
//				System.out.println("hmGratuityPolicy ===>> " + hmGratuityPolicy);
				request.setAttribute("hmGratuityPolicy", hmGratuityPolicy);
				
				Map<String, String> hmPerkAlignAmount = (Map<String, String>) request.getAttribute("hmPerkAlignAmount");
				if(hmPerkAlignAmount == null) hmPerkAlignAmount = new HashMap<String, String>();
				Map<String, String> hmPerkAlignTDSAmount = (Map<String, String>) request.getAttribute("hmPerkAlignTDSAmount");
				if(hmPerkAlignTDSAmount == null) hmPerkAlignTDSAmount = new HashMap<String, String>();
				pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id," +
						"salary_head_id FROM emp_salary_details WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) " +
						"FROM emp_salary_details WHERE emp_id = ? and is_approved = true " +
						"and isdisplay=true and level_id = ?) AND effective_date <= ? and level_id = ? group by salary_head_id) a, " +
						"emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
						"and a.salary_head_id=esd.salary_head_id and emp_id = ? " +
						"AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
						"WHERE emp_id = ? and is_approved = true and isdisplay=true and level_id = ?) " +
						"AND effective_date <= ? and esd.level_id = ?) asd RIGHT JOIN salary_details sd " +
						"ON asd.salary_head_id = sd.salary_head_id WHERE sd.level_id = ? and sd.salary_band_id=? " +
						"and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) " +
						"order by sd.earning_deduction desc, weight");
				pst.setInt(1, intEmpIdReq);
				pst.setInt(2, intEmpIdReq);
				pst.setInt(3, uF.parseToInt(levelId));
				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(5, uF.parseToInt(levelId));
				pst.setInt(6, intEmpIdReq);
				pst.setInt(7, intEmpIdReq);
				pst.setInt(8, uF.parseToInt(levelId));
				pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(10, uF.parseToInt(levelId));
				pst.setInt(11, uF.parseToInt(levelId));
				pst.setInt(12, uF.parseToInt(salaryBandId));
//				System.out.println("in level pst ===>> " + pst); 
				rs = pst.executeQuery();
//				List alSalaryDuplicationTracer = new ArrayList();
				List<List<String>> salaryHeadDetailsList = new ArrayList<List<String>>();
				Map<String, String> hmTotal = new HashMap<String, String>();
				double dblGrossTDS = 0.0d;
				boolean isEPF = false;
				boolean isESIC = false;
				boolean isLWF = false;
				List<List<String>> salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
				List<List<String>> salaryContributionDetailsList = new ArrayList<List<String>>();
				while (rs.next()) {
	
					if(uF.parseToInt(rs.getString("salary_head_id")) == CTC){
						continue;
					}
					
					if(!uF.parseToBoolean(rs.getString("isdisplay"))){
						continue;
					}
	
					if(rs.getString("earning_deduction").equals("E")) {
						if(uF.parseToBoolean(rs.getString("is_contribution"))) {
							
							List<String> innerList = new ArrayList<String>();
							innerList.add(rs.getString("salary_head_name"));
							innerList.add(rs.getString("earning_deduction"));
							
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
						
						} else if(!uF.parseToBoolean(rs.getString("is_variable")) && uF.parseToBoolean(rs.getString("is_annual_variable"))) {
							
							List<String> innerList = new ArrayList<String>();
							innerList.add(rs.getString("salary_head_name"));
							innerList.add(rs.getString("earning_deduction"));
							
							if(uF.parseToBoolean(rs.getString("isdisplay"))){
								double dblYearAmount = uF.parseToDouble(hmAnnualVariableAmt.get(rs.getString("salary_head_id")));
								double dblAmount = dblYearAmount/12;
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
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
								
								hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount);
							} else {
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
								innerList.add(rs.getString("salary_head_id"));
								innerList.add(rs.getString("is_variable"));
								hmTotal.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
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
									dblYearAmount = uF.parseToDouble(hmAnnualVariableAmt.get(rs.getString("salary_head_id")));
									dblAmount = dblYearAmount/12;
								} else {
									dblAmount = rs.getDouble("amount");
									if(hmPerkAlignAmount.containsKey(rs.getString("salary_head_id"))){
										dblAmount = uF.parseToDouble(hmPerkAlignAmount.get(rs.getString("salary_head_id")));
									}
									dblYearAmount = dblAmount * 12;
								}
								
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
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
								
								hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount);
							} else {
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
								innerList.add(rs.getString("salary_head_id"));
								innerList.add(rs.getString("is_variable"));
								hmTotal.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
							}
							salaryHeadDetailsList.add(innerList);
						}
					} else if(rs.getString("earning_deduction").equals("D")) {
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("salary_head_name"));
						innerList.add(rs.getString("earning_deduction"));
						if(uF.parseToBoolean(rs.getString("is_contribution"))) {
							
							if(uF.parseToBoolean(rs.getString("isdisplay"))) {
								double dblYearAmount = uF.parseToDouble(hmAnnualVariableAmt.get(rs.getString("salary_head_id")));
								double dblAmount = dblYearAmount/12;
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
						
						} else {
							if(uF.parseToBoolean(rs.getString("isdisplay"))) {
		//						int nPayMonth = uF.parseToInt(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, "MM"));
								switch(rs.getInt("salary_head_id")) {
															
									case PROFESSIONAL_TAX :
										  
										double dblAmount = calculateProfessionalTax(con, uF, grossAmount,strFinancialYearStart, strFinancialYearEnd, nPayMonth, strStateId,strEmpGender);
										double dblYearAmount =  getAnnualProfessionalTax(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, strStateId, strEmpGender);
										
										deductAmount += dblAmount;
										deductYearAmount += dblYearAmount;
										
										innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
										innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
										innerList.add(rs.getString("salary_head_id"));
										innerList.add(rs.getString("is_variable"));
										hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount);
										
										break;
									
									case EMPLOYEE_EPF :
										isEPF = true;
										double dblAmount1 = objAP.calculateEEPF(con, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmTotal, ""+intEmpIdReq, null, null, false, null);
//										System.out.println("dblAmount1 ===>> " + dblAmount1);
										double dblYearAmount1 = dblAmount1 * 12;
										
										deductAmount += dblAmount1;
										deductYearAmount += dblYearAmount1;
										
										innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount1));
										innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount1));
										innerList.add(rs.getString("salary_head_id"));
										innerList.add(rs.getString("is_variable"));
										hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount1);
										
										break;
									
									case EMPLOYEE_ESI :
										isESIC = true;
										double dblAmount4 = objAP.calculateEEESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, ""+intEmpIdReq);
										dblAmount4 = Math.ceil(dblAmount4);
										
										double dblYearAmount4 = dblAmount4 * 12;
										dblYearAmount4 = Math.ceil(dblYearAmount4);
										
										deductAmount += dblAmount4;
										deductYearAmount += dblYearAmount4;
	//									System.out.println("dblAmount4====>"+dblAmount4);
										innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount4));
										innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount4));
										innerList.add(rs.getString("salary_head_id"));
										innerList.add(rs.getString("is_variable"));
										hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount4);
										
										break;
									
									case EMPLOYEE_LWF :
										isLWF = true;
										double dblAmount6 = objAP.calculateEELWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, ""+intEmpIdReq, nPayMonth, strOrg);
										double dblYearAmount6 = dblAmount6 * 12;
										
										deductAmount += dblAmount6;
										deductYearAmount += dblYearAmount6;
										
										innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount6));
										innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount6));
										innerList.add(rs.getString("salary_head_id"));
										innerList.add(rs.getString("is_variable"));
										hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount6);
										
										break;
									
									case TDS :
										
										innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
										innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
										innerList.add(rs.getString("salary_head_id"));
										innerList.add(rs.getString("is_variable"));
										hmTotal.put(rs.getString("salary_head_id"), ""+0.0d);
										
										break;
									
									default:
										
										double dblAmount9 = uF.parseToDouble(rs.getString("amount"));
										double dblYearAmount9 = dblAmount9 * 12;
										
										deductAmount += dblAmount9;
										deductYearAmount += dblYearAmount9;
										
										innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount9));
										innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount9));
										innerList.add(rs.getString("salary_head_id"));
										innerList.add(rs.getString("is_variable"));
										hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount9);
										
										break;
								}
								
							}  else {
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
								innerList.add(rs.getString("salary_head_id"));
								innerList.add(rs.getString("is_variable"));
								hmTotal.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
							}
							salaryHeadDetailsList.add(innerList);
						}
					}
					
				}
				rs.close();
				pst.close();
	
				hmSalaryTotal.put("GROSS_AMOUNT", grossAmount);
				hmSalaryTotal.put("GROSS_YEAR_AMOUNT", grossYearAmount);
				hmSalaryTotal.put("DEDUCT_AMOUNT", deductAmount);
				hmSalaryTotal.put("DEDUCT_YEAR_AMOUNT", deductYearAmount);
				
				request.setAttribute("hmSalaryTotal", hmSalaryTotal);
				request.setAttribute("salaryHeadDetailsList", salaryHeadDetailsList);
				request.setAttribute("salaryAnnualVariableDetailsList", salaryAnnualVariableDetailsList);
				request.setAttribute("salaryContributionDetailsList", salaryContributionDetailsList);
//				System.out.println("salaryHeadDetailsList 1======>"+salaryHeadDetailsList); 
//				System.out.println("salaryAnnualVariableDetailsList 1======>"+salaryAnnualVariableDetailsList);
				/**
				 * Employer Contribution
				 * */ 
				Map<String,String> hmContribution = new HashMap<String, String>();
				if(isEPF){
//					double dblAmount = objAP.calculateERPFandEPS(con, CF, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, ""+intEmpIdReq, null, null, false, null);
					double dblAmount = objAP.calculateERPF(con,CF, null, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, ""+intEmpIdReq, null, null, false, null);
//					dblAmount = Math.round(dblAmount);
//					System.out.println("MP.java/1931----dblAmount=="+dblAmount);
					double dblYearAmount = dblAmount * 12;
					hmContribution.put("EPF_MONTHLY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
					hmContribution.put("EPF_ANNUALY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
				}
				if(isESIC){
					double dblAmount = objAP.calculateERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId,""+intEmpIdReq, null, null);
					dblAmount = Math.ceil(dblAmount);
					double dblYearAmount = dblAmount * 12;
					dblYearAmount = Math.ceil(dblYearAmount);
					
					hmContribution.put("ESI_MONTHLY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
					hmContribution.put("ESI_ANNUALY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
				}
				if(isLWF){
					double dblAmount = objAP.calculateERLWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, nPayMonth, strOrg);
//					dblAmount = Math.round(dblAmount); 
					double dblYearAmount = dblAmount * 12;
					hmContribution.put("LWF_MONTHLY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
					hmContribution.put("LWF_ANNUALY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));				
				}
				request.setAttribute("isEPF", ""+isEPF);
				request.setAttribute("isESIC", ""+isESIC);
				request.setAttribute("isLWF", ""+isLWF);
				request.setAttribute("hmContribution", hmContribution);
				
				/**
				 * Employer Contribution End
				 * */ 
				
				
				pst = con.prepareStatement("select amount from payroll_generation where emp_id = ? and salary_head_id = ? and financial_year_from_date=? and financial_year_to_date=?");
				pst.setInt(1, intEmpIdReq);
				pst.setInt(2, PROFESSIONAL_TAX);
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
	//			System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				double dblAmount = 0;
				int nMonthCount = 0;
				while(rs.next()){
					dblAmount += uF.parseToDouble(rs.getString("amount"));
					nMonthCount++;
				}
				rs.close();
				pst.close(); 
				
				int nTotalCount = 12;
				if(((String)hmEmpProfile.get("JOINING_DATE")) != null && !((String)hmEmpProfile.get("JOINING_DATE")).trim().equals("") && !((String)hmEmpProfile.get("JOINING_DATE")).trim().equalsIgnoreCase("NULL")){
					java.util.Date dtJoiningDt = uF.getDateFormatUtil((String)hmEmpProfile.get("JOINING_DATE"), CF.getStrReportDateFormat());
					java.util.Date dtFinancialYearStartDt = uF.getDateFormatUtil(strFinancialYearStart, DATE_FORMAT);
					java.util.Date dtFinancialYearEndDt = uF.getDateFormatUtil(strFinancialYearEnd, DATE_FORMAT);
					if(dtJoiningDt!=null && dtJoiningDt.before(dtFinancialYearStartDt)) {
						nTotalCount = 12;
					} else if(dtJoiningDt != null) {
						int m1 = dtJoiningDt.getYear() * 12 + dtJoiningDt.getMonth();
					    int m2 = dtFinancialYearEndDt.getYear() * 12 + dtFinancialYearEndDt.getMonth();
					    nTotalCount = m2 - m1 + 1;
					}
				}
				int nRemainingCount = nTotalCount - nMonthCount;
				
				
				pst = con.prepareStatement("select amount from emp_salary_details where emp_id = ? " +
						"and earning_deduction = ? and is_approved =true " +
						"and effective_date = (select max(effective_date) from emp_salary_details " +
						"where emp_id = ? and earning_deduction = ? and is_approved = true " +
						"and level_id=?) and level_id=?");
				pst.setInt(1, intEmpIdReq);
				pst.setString(2, "E");
				pst.setInt(3, intEmpIdReq);
				pst.setString(4, "E");
				pst.setInt(5, uF.parseToInt(levelId));
				pst.setInt(6, uF.parseToInt(levelId));
	//			System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				double dblGrossAmount = 0;
				while(rs.next()){
					dblGrossAmount += uF.parseToDouble(rs.getString("amount"));
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from deduction_details_india where income_from<=? and income_to>=? and state_id=? " +
						"and financial_year_from=? and financial_year_to=? and gender =? limit 1");
				pst.setDouble(1, dblGrossAmount);
				pst.setDouble(2, dblGrossAmount);
				pst.setInt(3, uF.parseToInt((String)hmEmpStateMap.get(intEmpIdReq+"")));
				pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(6, strEmpGender);
	//			System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				double dblDeductionAmount = 0;
				while(rs.next()) {
					dblDeductionAmount = rs.getDouble("deduction_amount");
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
				if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
				String strCurrency = "";
				if(uF.parseToInt(hmEmpCurrency.get(intEmpIdReq+"")) > 0){
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(intEmpIdReq+""));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				} 
				
				
				ApprovePayroll objAppPayroll = new ApprovePayroll();
				objAppPayroll.session = session;
				objAppPayroll.request = request;
				objAppPayroll.CF = CF;
				
				double dblMonthlyAmount = objAppPayroll.calculateProfessionalTax(con, uF, null, dblGrossAmount, strFinancialYearStart, strFinancialYearEnd, 6, (String)hmEmpStateMap.get(intEmpIdReq+""), strEmpGender);
				double dblVar = dblDeductionAmount - (dblMonthlyAmount * 12);
				dblAmount = dblAmount + (dblMonthlyAmount * nRemainingCount) + dblVar;
				
				request.setAttribute("dblAmount", strCurrency+uF.formatIntoOneDecimal(dblAmount));
				request.setAttribute("dblMonthlyAmount", strCurrency+uF.formatIntoOneDecimal(dblMonthlyAmount));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	
	
	public void getPrevSalaryHeadsforEmployee(Connection con, UtilityFunctions uF, int intEmpIdReq, Map<String, String> hmEmpProfile) {
		PreparedStatement pst = null;
		ResultSet rs = null;
	
		try {
	//		System.out.println("getSalaryHeadsforEmployee ===> ");
			
			String levelId = CF.getEmpLevelId(con, ""+intEmpIdReq);			
			String strOrg = CF.getEmpOrgId(con, uF, ""+intEmpIdReq);
			
			String strPrevEffectiveDate = null;
			pst = con.prepareStatement("SELECT MAX(effective_date) as effective_date FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
				"and isdisplay=true and level_id=? AND effective_date < (SELECT MAX(effective_date) as effective_date FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
				"and isdisplay=true and level_id=? AND effective_date<=?) ");
			pst.setInt(1, intEmpIdReq);
			pst.setInt(2, uF.parseToInt(levelId));
			pst.setInt(3, intEmpIdReq);
			pst.setInt(4, uF.parseToInt(levelId));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while (rs.next()) {
				strPrevEffectiveDate = uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT);
			}
			rs.close();
			pst.close();
			
		//===start parvez date: 01-07-2022===	
			pst = con.prepareStatement("SELECT * FROM emp_salary_details WHERE emp_id=? AND effective_date=? and level_id=? and salary_head_id="+CTC+"");
		//===end parvez date: 01-07-2022===	
			pst.setInt(1, intEmpIdReq);
			pst.setDate(2, uF.getDateFormat(strPrevEffectiveDate, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(levelId));
			rs = pst.executeQuery();
			String ctcAmt = "0";
			while(rs.next()){
				ctcAmt = rs.getString("amount");
			}
			rs.close();
			pst.close();
			String salaryBandId = CF.getSalaryBandId(con, ctcAmt, levelId);
				
//			System.out.println("strPrevEffectiveDate ===>>>> " + strPrevEffectiveDate);
			if(strPrevEffectiveDate == null || strPrevEffectiveDate.equals("-")) {
				strPrevEffectiveDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
			}
			String[] strFinancialYearDates = CF.getFinancialYear(con, strPrevEffectiveDate, CF, uF);
//			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];
			
			
			
			String currId = CF.getOrgCurrencyIdByOrg(con, strOrg);
			Map<String, Map<String, String>> hmCurrencyDetailsMap =  CF.getCurrencyDetails(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String,String>>();
			Map<String, String> hmCurr = hmCurrencyDetailsMap.get(currId);
			if(hmCurr == null) hmCurr = new HashMap<String, String>();
			String strCurr = hmCurr.get("LONG_CURR")!=null && !hmCurr.get("LONG_CURR").equalsIgnoreCase("null") ? hmCurr.get("LONG_CURR")+" " : "";
			request.setAttribute("strCurr", strCurr);
	
	//		Map<String, String> hmEmpGenderMap = CF.getEmpGenderMap(con);
	//		Map<String, String> hmEmpAgeMap = CF.getEmpAgeMap(con,CF);
			
			String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String[] strPayCycleDates = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrg);
			
			if(strPayCycleDates!=null && strPayCycleDates.length > 0){
				String strD1 = strPayCycleDates[0];
				String strD2 = strPayCycleDates[1];
				String strPC = strPayCycleDates[2];
				
				int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
			
				Map hmEmpMertoMap = new HashMap();
				Map hmEmpWlocationMap = new HashMap();
				Map hmEmpStateMap = new HashMap();
				CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
				
				String strStateId = (String)hmEmpStateMap.get(""+intEmpIdReq);
				String strEmpGender = CF.getEmpGender(con, uF, ""+intEmpIdReq);
				
	//			Map<String, String> hmEmpServiceTaxMap = CF.getEmpServiceTax(con, uF, CF);
				
				pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
	//			System.out.println(" pst==>"+pst);
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
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and section_id=3 order by section_code");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
	//			System.out.println(" pst==>"+pst);
				double dblInvestmentExemption = 0.0d;
				if (rs.next()) {
					dblInvestmentExemption = uF.parseToDouble(rs.getString("section_exemption_limit"));
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from annual_variable_details where level_id=? and org_id=? " +
						"and financial_year_start=? and financial_year_end=? and emp_id=? and salary_head_id in (select salary_head_id from salary_details " +
						"where is_annual_variable=true and (is_delete is null or is_delete = false))");
				pst.setInt(1, uF.parseToInt(levelId));
				pst.setInt(2, uF.parseToInt(strOrg));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, intEmpIdReq);
				rs = pst.executeQuery();
				Map<String, String> hmAnnualVariableAmt = new HashMap<String, String>();
				while(rs.next()){
					hmAnnualVariableAmt.put(rs.getString("salary_head_id"), rs.getString("variable_amount"));
				}
				rs.close();
				pst.close();
				request.setAttribute("hmPrevAnnualVariableAmt", hmAnnualVariableAmt);
				
				Map<String, String> hmSalaryDetails = new HashMap<String, String>();
				List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
				List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
				pst = con.prepareStatement("select * from salary_details where salary_head_id not in ("+GROSS+","+CTC+") and org_id=? and level_id=? order by earning_deduction desc, salary_head_id, weight");
				pst.setInt(1, uF.parseToInt(strOrg));
				pst.setInt(2, uF.parseToInt(levelId)); 
	//			System.out.println("pst==>"+pst);
				rs = pst.executeQuery();  
				List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
				List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
				while(rs.next()){
					if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("E")){
						int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
						
						if(index>=0){
							alEmpSalaryDetailsEarning.remove(index);
							alEarningSalaryDuplicationTracer.remove(index);
							alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
						}else{
							alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
						}
						
						alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					}else if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("D")){
						int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
						if(index>=0){
							alEmpSalaryDetailsDeduction.remove(index);
							alDeductionSalaryDuplicationTracer.remove(index);
							alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
						}else{
							alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
						}
						alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					}
					
					hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
				}
				rs.close();
				pst.close();				
	
				Map<String, Double> hmSalaryTotal = new LinkedHashMap<String, Double>();
				double grossAmount = 0.0d;
				double grossYearAmount = 0.0d;
				double deductAmount = 0.0d;
				double deductYearAmount = 0.0d;				
				
				ApprovePayroll objAP = new ApprovePayroll();
				objAP.CF = CF;
				objAP.session = session;
				objAP.request = request; 
				
				Map<String, String> hmPerkAlignAmount = (Map<String, String>) request.getAttribute("hmPerkAlignAmount");
				if(hmPerkAlignAmount == null) hmPerkAlignAmount = new HashMap<String, String>();
				Map<String, String> hmPerkAlignTDSAmount = (Map<String, String>) request.getAttribute("hmPerkAlignTDSAmount");
				if(hmPerkAlignTDSAmount == null) hmPerkAlignTDSAmount = new HashMap<String, String>();
				pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id," +
						"salary_head_id FROM emp_salary_details WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) " +
						"FROM emp_salary_details WHERE emp_id = ? and is_approved = true " +
						"and isdisplay=true and level_id = ? AND effective_date <= ?) and level_id = ? group by salary_head_id) a, " +
						"emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
						"and a.salary_head_id=esd.salary_head_id and emp_id = ? " +
						"AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
						"WHERE emp_id = ? and is_approved = true and isdisplay=true and level_id = ? " +
						"AND effective_date <= ?) and esd.level_id = ?) asd RIGHT JOIN salary_details sd " +
						"ON asd.salary_head_id = sd.salary_head_id WHERE sd.level_id = ? and sd.salary_band_id=? " +
						"and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) " +
						"order by sd.earning_deduction desc, weight");
				pst.setInt(1, intEmpIdReq);
				pst.setInt(2, intEmpIdReq);
				pst.setInt(3, uF.parseToInt(levelId));
				pst.setDate(4, uF.getDateFormat(strPrevEffectiveDate, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(levelId));
				pst.setInt(6, intEmpIdReq);
				pst.setInt(7, intEmpIdReq);
				pst.setInt(8, uF.parseToInt(levelId));
				pst.setDate(9, uF.getDateFormat(strPrevEffectiveDate, DATE_FORMAT));
				pst.setInt(10, uF.parseToInt(levelId));
				pst.setInt(11, uF.parseToInt(levelId));
				pst.setInt(12, uF.parseToInt(salaryBandId));
//				System.out.println("in level pst ================= ===>> " + pst); 
				rs = pst.executeQuery();
	//			List alSalaryDuplicationTracer = new ArrayList();
				List<List<String>> salaryHeadDetailsList = new ArrayList<List<String>>();
				Map<String, String> hmTotal = new HashMap<String, String>();
				double dblGrossTDS = 0.0d;
				boolean isEPF = false;
				boolean isESIC = false;
				boolean isLWF = false;
				List<List<String>> salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
				while (rs.next()) {
	
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
								double dblYearAmount = uF.parseToDouble(hmAnnualVariableAmt.get(rs.getString("salary_head_id")));
								
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
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
								
								hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount);
							} else {
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
								innerList.add(rs.getString("salary_head_id"));
								innerList.add(rs.getString("is_variable"));
								hmTotal.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
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
									dblYearAmount = uF.parseToDouble(hmAnnualVariableAmt.get(rs.getString("salary_head_id")));
								} else {
									dblAmount = rs.getDouble("amount");
									if(hmPerkAlignAmount.containsKey(rs.getString("salary_head_id"))){
										dblAmount = uF.parseToDouble(hmPerkAlignAmount.get(rs.getString("salary_head_id")));
									}
									dblYearAmount = dblAmount * 12;
								}
								
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
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
								
								hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount);
							} else {
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
								innerList.add(rs.getString("salary_head_id"));
								innerList.add(rs.getString("is_variable"));
								hmTotal.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
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
									  
									double dblAmount = calculateProfessionalTax(con, uF, grossAmount,strFinancialYearStart, strFinancialYearEnd, nPayMonth, strStateId,strEmpGender);
									double dblYearAmount =  getAnnualProfessionalTax(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, strStateId, strEmpGender);
									
									deductAmount += dblAmount;
									deductYearAmount += dblYearAmount;
									
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
									innerList.add(rs.getString("salary_head_id"));
									innerList.add(rs.getString("is_variable"));
									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount);
									
									break;
								
								case EMPLOYEE_EPF :
									isEPF = true;	
									double dblAmount1 = objAP.calculateEEPF(con, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmTotal, ""+intEmpIdReq, null, null, false, null);
									double dblYearAmount1 = dblAmount1 * 12;
									
									deductAmount += dblAmount1;
									deductYearAmount += dblYearAmount1;
									
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount1));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount1));
									innerList.add(rs.getString("salary_head_id"));
									innerList.add(rs.getString("is_variable"));
									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount1);
									
									break;
								
								case EMPLOYEE_ESI :
									isESIC = true;
									double dblAmount4 = objAP.calculateEEESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, ""+intEmpIdReq);
									dblAmount4 = Math.ceil(dblAmount4);
									
									double dblYearAmount4 = dblAmount4 * 12;
									dblYearAmount4 = Math.ceil(dblYearAmount4);
									
									deductAmount += dblAmount4;
									deductYearAmount += dblYearAmount4;
	//								System.out.println("dblAmount4====>"+dblAmount4);
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount4));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount4));
									innerList.add(rs.getString("salary_head_id"));
									innerList.add(rs.getString("is_variable"));
									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount4);
									
									break;
								
								case EMPLOYEE_LWF :
									isLWF = true;
									double dblAmount6 = objAP.calculateEELWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, ""+intEmpIdReq, nPayMonth, strOrg);
									double dblYearAmount6 = dblAmount6 * 12;
									
									deductAmount += dblAmount6;
									deductYearAmount += dblYearAmount6;
									
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount6));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount6));
									innerList.add(rs.getString("salary_head_id"));
									innerList.add(rs.getString("is_variable"));
									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount6);
									
									break;
								
								case TDS :
									
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
									innerList.add(rs.getString("salary_head_id"));
									innerList.add(rs.getString("is_variable"));
									hmTotal.put(rs.getString("salary_head_id"), ""+0.0d);
									
									break;
								
								default:
									
									double dblAmount9 = uF.parseToDouble(rs.getString("amount"));
									double dblYearAmount9 = dblAmount9 * 12;
									
									deductAmount += dblAmount9;
									deductYearAmount += dblYearAmount9;
									
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount9));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount9));
									innerList.add(rs.getString("salary_head_id"));
									innerList.add(rs.getString("is_variable"));
									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount9);
									
									break;
							}
						}  else {
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							hmTotal.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
						}
						
						salaryHeadDetailsList.add(innerList);
					}
					
				}
				rs.close();
				pst.close();
	
				hmSalaryTotal.put("PREV_GROSS_AMOUNT", grossAmount);
				hmSalaryTotal.put("PREV_GROSS_YEAR_AMOUNT", grossYearAmount);
				hmSalaryTotal.put("PREV_DEDUCT_AMOUNT", deductAmount);
				hmSalaryTotal.put("PREV_DEDUCT_YEAR_AMOUNT", deductYearAmount);
				
				request.setAttribute("hmPrevSalaryTotal", hmSalaryTotal);
				request.setAttribute("prevSalaryHeadDetailsList", salaryHeadDetailsList);
				request.setAttribute("prevSalaryAnnualVariableDetailsList", salaryAnnualVariableDetailsList);
	//			System.out.println("salaryHeadDetailsList======>"+salaryHeadDetailsList); 
//				System.out.println("strFinancialYearStart ====>>> " + strFinancialYearStart + " --- strFinancialYearEnd ====>>> " + strFinancialYearEnd);
				/**
				 * Employer Contribution
				 * */ 
				Map<String,String> hmContribution = new HashMap<String, String>();
				if(isEPF){
	//				double dblAmount = objAP.calculateERPFandEPS(con, CF, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, ""+intEmpIdReq, null, null, false, null);
					double dblAmount = objAP.calculateERPF(con,CF, null, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, ""+intEmpIdReq, null, null, false, null);
	//				dblAmount = Math.round(dblAmount);
					double dblYearAmount = dblAmount * 12;
					hmContribution.put("EPF_MONTHLY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
					hmContribution.put("EPF_ANNUALY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
				}
				if(isESIC){
					double dblAmount = objAP.calculateERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId,""+intEmpIdReq, null, null);
					dblAmount = Math.ceil(dblAmount);
					double dblYearAmount = dblAmount * 12;
					dblYearAmount = Math.ceil(dblYearAmount);
					
					hmContribution.put("ESI_MONTHLY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
					hmContribution.put("ESI_ANNUALY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
				}
				if(isLWF){
					double dblAmount = objAP.calculateERLWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, nPayMonth, strOrg);
	//				dblAmount = Math.round(dblAmount); 
					double dblYearAmount = dblAmount * 12;
					hmContribution.put("LWF_MONTHLY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
					hmContribution.put("LWF_ANNUALY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));				
				}
				request.setAttribute("isPrevEPF", ""+isEPF);
				request.setAttribute("isPrevESIC", ""+isESIC);
				request.setAttribute("isPrevLWF", ""+isLWF);
				request.setAttribute("hmPrevContribution", hmContribution);
				
				/**
				 * Employer Contribution End
				 * */ 
				
				
				pst = con.prepareStatement("select amount from payroll_generation where emp_id = ? and salary_head_id = ? and financial_year_from_date=? and financial_year_to_date=?");
				pst.setInt(1, intEmpIdReq);
				pst.setInt(2, PROFESSIONAL_TAX);
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
	//			System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				double dblAmount = 0;
				int nMonthCount = 0;
				while(rs.next()){
					dblAmount += uF.parseToDouble(rs.getString("amount"));
					nMonthCount++;
				}
				rs.close();
				pst.close(); 
				
				int nTotalCount = 12;
				if(((String)hmEmpProfile.get("JOINING_DATE")) != null && !((String)hmEmpProfile.get("JOINING_DATE")).trim().equals("") && !((String)hmEmpProfile.get("JOINING_DATE")).trim().equalsIgnoreCase("NULL")){
					java.util.Date dtJoiningDt = uF.getDateFormatUtil((String)hmEmpProfile.get("JOINING_DATE"), CF.getStrReportDateFormat());
					java.util.Date dtFinancialYearStartDt = uF.getDateFormatUtil(strFinancialYearStart, DATE_FORMAT);
					java.util.Date dtFinancialYearEndDt = uF.getDateFormatUtil(strFinancialYearEnd, DATE_FORMAT);
					if(dtJoiningDt!=null && dtJoiningDt.before(dtFinancialYearStartDt)) {
						nTotalCount = 12;
					} else if(dtJoiningDt != null) {
						int m1 = dtJoiningDt.getYear() * 12 + dtJoiningDt.getMonth();
					    int m2 = dtFinancialYearEndDt.getYear() * 12 + dtFinancialYearEndDt.getMonth();
					    nTotalCount = m2 - m1 + 1;
					}
				}
				int nRemainingCount = nTotalCount - nMonthCount;
				
				
				pst = con.prepareStatement("select amount from emp_salary_details where emp_id = ? " +
						"and earning_deduction = ? and is_approved =true " +
						"and effective_date = (select max(effective_date) from emp_salary_details " +
						"where emp_id = ? and earning_deduction = ? and is_approved = true " +
						"and level_id=?) and level_id=?");
				pst.setInt(1, intEmpIdReq);
				pst.setString(2, "E");
				pst.setInt(3, intEmpIdReq);
				pst.setString(4, "E");
				pst.setInt(5, uF.parseToInt(levelId));
				pst.setInt(6, uF.parseToInt(levelId));
	//			System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				double dblGrossAmount = 0;
				while(rs.next()){
					dblGrossAmount += uF.parseToDouble(rs.getString("amount"));
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from deduction_details_india where income_from<=? and income_to>=? and state_id=? " +
						"and financial_year_from=? and financial_year_to=? and gender =? limit 1");
				pst.setDouble(1, dblGrossAmount);
				pst.setDouble(2, dblGrossAmount);
				pst.setInt(3, uF.parseToInt((String)hmEmpStateMap.get(intEmpIdReq+"")));
				pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(6, strEmpGender);
	//			System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				double dblDeductionAmount = 0;
				while(rs.next()) {
					dblDeductionAmount = rs.getDouble("deduction_amount");
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
				if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
				String strCurrency = "";
				if(uF.parseToInt(hmEmpCurrency.get(intEmpIdReq+"")) > 0){
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(intEmpIdReq+""));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				} 
				
				
				ApprovePayroll objAppPayroll = new ApprovePayroll();
				objAppPayroll.session = session;
				objAppPayroll.request = request;
				objAppPayroll.CF = CF;
				
				double dblMonthlyAmount = objAppPayroll.calculateProfessionalTax(con, uF, null, dblGrossAmount, strFinancialYearStart, strFinancialYearEnd, 6, (String)hmEmpStateMap.get(intEmpIdReq+""), strEmpGender);
				double dblVar = dblDeductionAmount - (dblMonthlyAmount * 12);
				dblAmount = dblAmount + (dblMonthlyAmount * nRemainingCount) + dblVar;
				
				request.setAttribute("dblPrevAmount", strCurrency+uF.formatIntoOneDecimal(dblAmount));
				request.setAttribute("dblPrevMonthlyAmount", strCurrency+uF.formatIntoOneDecimal(dblMonthlyAmount));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	
	
	private double calculateProfessionalTax(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd,
			int nPayMonth, String strStateId, String strEmpGender) {
		
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
			pst.setString(6, strEmpGender);
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

	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getProPopup() { 
		return proPopup;
	}

	public void setProPopup(String proPopup) {
		this.proPopup = proPopup;
	}

	public String getPopup() {
		return popup;
	}

	public void setPopup(String popup) {
		this.popup = popup; 
	}

	public String getEmpImageFileName() {
		return empImageFileName;
	}

	public void setEmpImageFileName(String empImageFileName) {
		this.empImageFileName = empImageFileName;
	}

	public File getEmpImage() {
		return empImage;
	}

	public void setEmpImage(File empImage) {
		this.empImage = empImage;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getEmpCoverImageFileName() {
		return empCoverImageFileName;
	}

	public void setEmpCoverImageFileName(String empCoverImageFileName) {
		this.empCoverImageFileName = empCoverImageFileName;
	}

	public File getEmpCoverImage() {
		return empCoverImage;
	}

	public void setEmpCoverImage(File empCoverImage) {
		this.empCoverImage = empCoverImage;
	}

	
}
