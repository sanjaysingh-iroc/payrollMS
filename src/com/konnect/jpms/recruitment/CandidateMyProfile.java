package com.konnect.jpms.recruitment;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.EncryptionUtility;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CandidateMyProfile extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF;

	private String CandID;
	private String recruitId;
	private String[] salary_head_id;
	private String[] salary_head_value;
	private String[] emp_salary_id;
	private String[] isDisplay;
	
	private String approvedeny;
	private String panelrating;
	private String ctcOffer;
	private String interviewcomment;
	private String roundID;
	private String strinterviewcommentHR;
	private String hrchoice;
	private String joiningdate;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
//		EncryptionUtility eU = new EncryptionUtility();
		if (getHrchoice() == null)
			setHrchoice("0");

		/*if(getCandID() != null && !getCandID().equalsIgnoreCase("null") && uF.parseToInt(getCandID()) == 0) {
			String decodeCandID = eU.decode(getCandID());
			setCandID(decodeCandID);
		}
		
		if(getRecruitId() != null && !getRecruitId().equalsIgnoreCase("null") && uF.parseToInt(getRecruitId()) == 0) {
			String decodeRecruitId = eU.decode(getRecruitId());
			setRecruitId(decodeRecruitId);
		}*/
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(PAGE, "/jsp/recruitment/CandidateMyProfile.jsp");
//		request.setAttribute(TITLE, "My Profile");

		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		Map<String, String> medicalQuest = new HashMap<String, String>();
		medicalQuest.put("1", "Are you now receiving medical attention:");
		medicalQuest.put("2", "Have you had any form of serious illness or operation");
		medicalQuest.put("3", "Have you had any illness in the last two years? YES/NO If YES, please give the details about the same and any absences from work: ");
//		medicalQuest.put("4", "Has any previous post been terminated on medical grounds?");
//		medicalQuest.put("5", "Do you have an allergies?");
		request.setAttribute("medicalQuest", medicalQuest);
		
		viewProfile(getCandID(), uF);

//		if (getRecruitId() != null && uF.parseToInt(getRecruitId())>0){
			loadFilledData(uF);
//		}
			

		String intSubmitComment = request.getParameter("intSubmitComment");

		if (intSubmitComment != null) {
			return insertInterviewComment(uF);
		}

		String hrsubmit = request.getParameter("hrsubmit");

		if (hrsubmit != null) {
			
			insertEmpSalaryDetails(uF);
			return insertHrInterview(uF);
		}

		return SUCCESS;
	}

	private void loadFilledData(UtilityFunctions uF) {

		getPanelComment(uF);
		getHrData(uF);
		getInterviewDates(uF);
		getCandiActivityDetails(uF);
	} 
	

	private void getCandiActivityDetails(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
//		Map<String, List<String>> hmDegreeName = new HashMap<String, List<String>>();
		try {
			con=db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmJobName = CF.getRecruitmentNameMap(con, uF);
			
			List<List<String>> activityList = new ArrayList<List<String>>();
			pst=con.prepareStatement("select * from candidate_activity_details where candi_id = ? order by candi_activity_id desc");
			pst.setInt(1, uF.parseToInt(getCandID()));
//			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("pst ===> "+pst);
			while(rst.next()){
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(rst.getString("activity_name"), ""));
				innerList.add(hmEmpName.get(rst.getString("user_id")));
				innerList.add(uF.getDateFormat(rst.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(uF.showData(hmJobName.get(rst.getString("recruitment_id")), ""));
				innerList.add(uF.showData(rst.getString("round_id"), ""));
				innerList.add(rst.getString("activity_id"));
				activityList.add(innerList);
			}
			rst.close();
			pst.close();
			//System.out.println("activityList ===> "+activityList);
			request.setAttribute("activityList", activityList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
//		return hmDegreeName;
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

			List<String> panelList = new ArrayList<String>();

			con = db.makeConnection(con);

			pst = con.prepareStatement("Select panel_employee_id  from recruitment_details  where  recruitment_id=? ");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				panelList = getPanelList(rs.getString("panel_employee_id"));
			}
			rs.close();
			pst.close();

			request.setAttribute("panelList", panelList);

			Boolean isCandidateRejected=false;
			
			Map<String, List<String>> hmPanelData = new HashMap<String, List<String>>();

			pst = con.prepareStatement("select * from candidate_interview_panel where candidate_id=? and recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getCandID()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("pst=====>"+pst);
			while (rs.next()) {

				List<String> alInner = new ArrayList<String>();
				
				alInner.add(uF.showData(rs.getString("is_interview_taken"), ""));
				alInner.add(uF.showData(rs.getString("comments"), ""));
				alInner.add(uF.showData(rs.getString("status"), ""));
				alInner.add(uF.showData(rs.getString("panel_rating"), ""));
			    alInner.add(uF.showData(rs.getString("status"), ""));
			    
			    if(rs.getInt("status")==-1);
			    isCandidateRejected=true;

				hmPanelData.put(rs.getString("panel_round_id"), alInner);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmPanelData==>"+hmPanelData);
			
			request.setAttribute("isCandidateRejected", isCandidateRejected);
			request.setAttribute("hmPanelData", hmPanelData);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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
			
			pst = con.prepareStatement("select * from candidate_interview_panel where candidate_id=?");
			pst.setInt(1, uF.parseToInt(getCandID()));
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

			pst = con.prepareStatement("select is_interview_taken,panel_round_id,comments,status,panel_rating from candidate_interview_panel where candidate_id=? and recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getCandID()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {

				alInner = new ArrayList<String>();

				alInner.add(uF.showData(rst.getString("comments"), "-"));
				alInner.add(uF.showData(rst.getString("status"), "-"));
				alInner.add(uF.showData(rst.getString("panel_rating"), ""));
				alInner.add(uF.showData(rst.getString("is_interview_taken"), ""));

				hmPanelDataHR.put(rst.getString("panel_round_id"), alInner);
			}
			rst.close();
			pst.close();
			
			// Panel information for hr 
			request.setAttribute("hmPanelDataHR", hmPanelDataHR);
			
			Map<String, List<String>> hmCommentsHr = new HashMap<String, List<String>>();
			pst = con.prepareStatement("select candidate_final_status,candidate_hr_comments,candidate_joining_date,ctc_offered,candidate_status from candidate_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getCandID()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
                 alInner=new ArrayList<String>();
                 alInner.add(uF.showData(rst.getString("candidate_final_status"), ""));
                 alInner.add(uF.showData(rst.getString("candidate_hr_comments"), ""));
                 alInner.add(uF.getDateFormat(rst.getString("candidate_joining_date"), DBDATE, DATE_FORMAT) );
                 alInner.add(uF.showData(rst.getString("ctc_offered"), ""));
                 alInner.add(uF.showData(rst.getString("candidate_status"),""));
                 
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

			pst = con.prepareStatement("update candidate_interview_panel set comments=?,status=?,panel_rating=?,is_interview_taken=? where recruitment_id=? and candidate_id=? and panel_round_id=?");
			pst.setString(1, getInterviewcomment());
			if (approvedeny != null) {
				pst.setInt(2, 1);
			} else {
				pst.setInt(2, -1);
			}
			pst.setDouble(3, uF.parseToDouble(getPanelrating()));
			pst.setBoolean(4,true);
			pst.setInt(5, uF.parseToInt(getRecruitId()));
			pst.setInt(6, uF.parseToInt(getCandID()));
			pst.setInt(7, uF.parseToInt(getRoundID()));
			pst.executeUpdate();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return "takeInterview";
	}

	private List<String> getPanelList(String panelID) {
		List<String> al = new ArrayList<String>();

		if (panelID != null && !panelID.equals("")) {
			String[] temp = panelID.split(",");

			for (int i = 1; i < temp.length; i++) {
				al.add(temp[i].trim());
			}
		}

		return al;
	}
	

	private String insertHrInterview(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);

//			pst = con.prepareStatement("update candidate_personal_details set candidate_final_status=?,candidate_hr_comments=?, candidate_joining_date=?,ctc_offered=? where emp_per_id=? ");
			pst = con.prepareStatement("update candidate_personal_details set candidate_final_status=?,candidate_hr_comments=?, candidate_joining_date=?  where emp_per_id=? ");

			if (hrchoice != null && hrchoice.equals("1")) {
				pst.setInt(1, 1);
			} else {
				pst.setInt(1, 0);
			}
			pst.setString(2, getStrinterviewcommentHR());
			pst.setDate(3, uF.getDateFormat(getJoiningdate(), DATE_FORMAT));
//			pst.setDouble(4, uF.parseToDouble(getCtcOffer()));
			pst.setInt(4, Integer.parseInt(getCandID()));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return "returnhr";

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
			pst = con.prepareStatement("select * from salary_details ");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, String> hmEarningDeductionMap = new HashMap<String, String>();
			while(rs.next()){
				hmEarningDeductionMap.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmEarningDeductionMap==>"+hmEarningDeductionMap);
			
			for(int i=0; i<salary_head_id.length; i++) {
				
				pst = con.prepareStatement("INSERT INTO candidate_salary_details " +
						"(emp_id , salary_head_id, amount, entry_date, user_id, pay_type," +
	/*	isdisplay,*/	"service_id, effective_date, earning_deduction)" +
						" VALUES (?,?,?,?,?,?,?,?,?)");
				
				pst.setInt(1, uF.parseToInt(getCandID()));
				pst.setInt(2, uF.parseToInt(getSalary_head_id()[i]));
				pst.setDouble(3, uF.parseToDouble(getSalary_head_value()[i]));
				pst.setDate	(4, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				pst.setInt(5, 1);
				pst.setString(6, "M");
				pst.setInt(7, uF.parseToInt("0"));
				pst.setDate	(8, uF.getDateFormat(getJoiningdate(), DATE_FORMAT));
				pst.setString(9, hmEarningDeductionMap.get(getSalary_head_id()[i]));
				pst.execute();
				pst.close();
			}
			
			
//			String Amount =null;
//			pst=con.prepareStatement("select sum(amount) from candidate_salary_details where emp_id=? and earning_deduction='E' ");
//			pst.setInt(1, uF.parseToInt(getCandID()));
//
//			rs=pst.executeQuery();
////			System.out.println("new Date ===> " + new Date());
//			if(rs.next()){
//				Amount=rs.getString("sum");
//			}
//			rs.close();
//			pst.close();
			
			CandidateMyProfilePopup candidateMyProfilePopup = new CandidateMyProfilePopup();
			candidateMyProfilePopup.request = request;
			candidateMyProfilePopup.session = session;
			candidateMyProfilePopup.CF = CF;
			candidateMyProfilePopup.setCandID(getCandID());
			candidateMyProfilePopup.setRecruitId(getRecruitId());
			Map<String, String> hmCandiOffered = candidateMyProfilePopup.getCandiOfferedCTC(con);
			if(hmCandiOffered == null) hmCandiOffered = new HashMap<String, String>();
			
			pst = con.prepareStatement("update candidate_personal_details set ctc_offered=?,annual_ctc_offered=? where emp_per_id=? and recruitment_id = ? ");
			pst.setDouble(1,uF.parseToDouble(hmCandiOffered.get("CANDI_CTC")));
			pst.setDouble(2,uF.parseToDouble(hmCandiOffered.get("CANDI_ANNUAL_CTC")));
			pst.setInt(3, uF.parseToInt(getCandID()));
			pst.setInt(4, uF.parseToInt(getRecruitId()));
			pst.execute();
			pst.close();
			
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
	


	public String viewProfile(String strEmpIdReq, UtilityFunctions uF) {

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
		List<List<String>> alCandiReferences;
		List<List<String>> alCertification;
		// List<List<String>> alActivityDetails;
		//===start parvez date: 08-09-2021===
		Map<String, List<String>> hmEducationDocs;
		//===end parvez date: 08-09-2021===

		try {

			Map<String, String> hm = new HashMap<String, String>();

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			List<String> availableExt = CF.getAvailableExtention();
			request.setAttribute("availableExt",availableExt);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmStateMap = CF.getStateMap(con);
			Map<String, String> hmCountryMap = CF.getCountryMap(con);
			request.setAttribute("hmEmpName", hmEmpName);
			pst = con.prepareStatement("Select * from candidate_personal_details where emp_per_id=?");
			if (strEmpIdReq != null) {
				pst.setInt(1, uF.parseToInt(strEmpIdReq));
			} else {
				pst.setInt(1, uF.parseToInt((String) session.getAttribute("CANDID")));
			}
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
//				strEmpId = rs.getString("emp_per_id");

//				if (strUserType != null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {

				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
					request.setAttribute(TITLE, rs.getString("emp_fname") + strEmpMName+" "+ rs.getString("emp_lname") + "'s Profile");
//				}

				hm.put("job_code", rs.getString("job_code"));
				hm.put("CANDI_ID", rs.getString("emp_per_id"));
				
				
				hm.put("NAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				StringBuilder pAddress = new StringBuilder();
				if(rs.getString("emp_address1") != null && !rs.getString("emp_address1").equals("") && !rs.getString("emp_address1").equals("null")) {
					pAddress.append(uF.showData(rs.getString("emp_address1"), "-")+",");
				}
				
				if(rs.getString("emp_address2") != null && !rs.getString("emp_address2").equals("") && !rs.getString("emp_address2").equals("null")) {
					pAddress.append(uF.showData(rs.getString("emp_address2"), "-")+",");
				}
				
				if(rs.getString("emp_city_id") != null && !rs.getString("emp_city_id").equals("") && !rs.getString("emp_city_id").equals("null")) {
					pAddress.append(uF.showData(rs.getString("emp_city_id"), "-")+",");
				}
				
				if(rs.getString("emp_state_id") != null && !rs.getString("emp_state_id").equals("") && !rs.getString("emp_state_id").equals("null")) {
					pAddress.append(uF.showData(hmStateMap.get(rs.getString("emp_state_id")), "-")+",");
				}
				
				if(rs.getString("emp_country_id") != null && !rs.getString("emp_country_id").equals("") && !rs.getString("emp_country_id").equals("null")) {
					pAddress.append(uF.showData(hmCountryMap.get(rs.getString("emp_country_id")), "-")+",");
				}
				
				if(rs.getString("emp_pincode") != null && !rs.getString("emp_pincode").equals("") && !rs.getString("emp_pincode").equals("null")) {
					pAddress.append(uF.showData(rs.getString("emp_pincode"), "-")+".");
				}
				if(pAddress.length() == 0){
					pAddress.append("-");
				}
				hm.put("ADDRESS", pAddress.toString());
				hm.put("CITY", uF.showData(CF.getStateNameById(con, uF, rs.getString("emp_state_id")), "-"));
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
				
				String date_of_marriage = null;
				if(rs.getString("emp_date_of_marriage") != null && !rs.getString("emp_date_of_marriage").equals("")) {
					date_of_marriage = uF.getDateFormat(rs.getString("emp_date_of_marriage"), DBDATE, CF.getStrReportDateFormat());
				}
				hm.put("MARITAL_STATUS", mStatus);
				hm.put("DATE_OF_MARRIAGE", uF.showData(date_of_marriage, "-"));
				hm.put("BLOOD_GROUP", uF.showData(rs.getString("blood_group"), "-"));
				hm.put("isaccepted", rs.getString("application_status"));

				hm.put("SUPER_CODE", uF.showData(rs.getString("empcode"), "-"));
				
				hm.put("SUPER_NAME", uF.showData(rs.getString("emp_fname"), "-") +strEmpMName+ " " + uF.showData(rs.getString("emp_lname"), "-"));
				hm.put("PASSPORT_NO", uF.showData(rs.getString("passport_no"), "-"));
				hm.put("PASSPORT_EXPIRY", uF.getDateFormat(rs.getString("passport_expiry_date"), DBDATE, CF.getStrReportDateFormat()));
				hm.put("AVAILABILITY",  uF.showYesNo(rs.getString("availability_for_interview")));
				hm.put("CURRENT_CTC", uF.formatIntoOneDecimal(rs.getDouble("current_ctc")));
				hm.put("EXPECTED_CTC", uF.formatIntoOneDecimal(rs.getDouble("expected_ctc")));
				hm.put("NOTICE_PERIOD", uF.showData(rs.getString("notice_period"), "0") +" days"); 
				//Start Dattatray Date:23-08-21
				hm.put("CURRENT_LOCATION",rs.getString("current_location") !=null ? uF.showData(rs.getString("current_location"), "-"):CF.getCandidatePrevLocation(con, uF, rs.getString("emp_per_id")));
				hm.put("PREFERRED_LOCATION", CF.getCandidatePreferedLocation(con,rs.getString("applied_location")));
				//End Dattatray Date:23-08-21
				
				StringBuilder cAddress = new StringBuilder();
				if(rs.getString("emp_address1_tmp") != null && !rs.getString("emp_address1_tmp").equals("") && !rs.getString("emp_address1_tmp").equals("null")) {
					cAddress.append(uF.showData(rs.getString("emp_address1_tmp"), "-")+",");
				}
				
				if(rs.getString("emp_address2_tmp") != null && !rs.getString("emp_address2_tmp").equals("") && !rs.getString("emp_address2_tmp").equals("null")) {
					cAddress.append(uF.showData(rs.getString("emp_address2_tmp"), "-")+",");
				}
				
				if(rs.getString("emp_city_id_tmp") != null && !rs.getString("emp_city_id_tmp").equals("") && !rs.getString("emp_city_id_tmp").equals("null")) {
					cAddress.append(uF.showData(rs.getString("emp_city_id_tmp"), "-")+",");
				}
				
				if(rs.getString("emp_state_id_tmp") != null && !rs.getString("emp_state_id_tmp").equals("") && !rs.getString("emp_state_id_tmp").equals("null")) {
					cAddress.append(uF.showData(hmStateMap.get(rs.getString("emp_state_id_tmp")), "-")+",");
				}
				
				if(rs.getString("emp_country_id_tmp") != null && !rs.getString("emp_country_id_tmp").equals("") && !rs.getString("emp_country_id_tmp").equals("null")) {
					cAddress.append(uF.showData(hmCountryMap.get(rs.getString("emp_country_id_tmp")), "-")+",");
				}
				
				if(rs.getString("emp_pincode_tmp") != null && !rs.getString("emp_pincode_tmp").equals("") && !rs.getString("emp_pincode_tmp").equals("null")) {
					cAddress.append(uF.showData(rs.getString("emp_pincode_tmp"), "-")+".");
				}
				
				if(cAddress.length() == 0){
					cAddress.append("-");
				}
				hm.put("TMP_ADDRESS", cAddress.toString());
			}
			rs.close();
			pst.close();

			request.setAttribute("myProfile", hm);

			int intEmpIdReq = uF.parseToInt(strEmpIdReq);

			request.setAttribute("myProfile", hm);

			alSkills = CF.selectCandidateSkills(con, intEmpIdReq);
			alHobbies = CF.selectCandidateHobbies(con, intEmpIdReq);
			alLanguages = CF.selectCandidateLanguages(con, intEmpIdReq);
			alEducation = CF.selectCandidateEducation(con, intEmpIdReq);
			//===start parvez date: 08-09-2021===
			hmEducationDocs = CF.selectCandidateEducationDocument(con, intEmpIdReq);
			//===end parvez date: 08-09-2021===
			alCertification = CF.selectCandidateCertification(con, intEmpIdReq);//Created By Dattatray Date:23-08-21
//			System.out.println("intEmpIdReq ---->>>> "+intEmpIdReq);

			String filePath = request.getRealPath("/userDocuments/");
			alDocuments = selectDocuments(con, intEmpIdReq, filePath, uF);
			alResumes = selectResumes(con, intEmpIdReq, filePath, uF);
			selectMedicalDetails(con, intEmpIdReq);
			alFamilyMembers = selectFamilyMembers(con, intEmpIdReq, uF);
			alPrevEmployment = selectPrevEmploment(con, intEmpIdReq, uF);
			alCandiReferences = selectCandiReferences(con, intEmpIdReq, uF);
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
			request.setAttribute("alCandiReferences", alCandiReferences);
			request.setAttribute("alCertification", alCertification);//Created By Dattatray Date:23-08-21 

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}


	public List<List<String>> selectPrevEmploment(Connection con, int empId, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<String>> alPrevEmployment = new ArrayList<List<String>>();
		
		try {
			Map<String,String> hmCountryMap = CF.getCountryMap(con);
			Map<String,String> hmStateMap = CF.getStateMap(con);
			pst = con.prepareStatement("SELECT * FROM candidate_prev_employment WHERE emp_id = ? order by from_date");
			pst.setInt(1, empId);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("company_id"));//0
				alInner.add(uF.showData(rs.getString("company_name"),"-"));//1
				alInner.add(uF.showData(rs.getString("company_location"),"-"));//2
				alInner.add(uF.showData(rs.getString("company_city"), "-"));//3
				alInner.add(uF.showData(rs.getString("company_state"), "-"));//4
				alInner.add(uF.showData(rs.getString("company_country"), "-"));//5
				//alInner.add(hmStateMap.get(rs.getString("company_state")));//4
				//alInner.add(hmCountryMap.get(rs.getString("company_country")));//5
				alInner.add(uF.showData(rs.getString("company_contact_no"), "-"));//6
				alInner.add(uF.showData(rs.getString("reporting_to"),"-"));//7
				if(rs.getString("from_date") != null && !rs.getString("from_date").equals("")) {
					alInner.add(uF.getDateFormat(rs.getString("from_date"), DBDATE,	CF.getStrReportDateFormat()));//8
				} else {
					alInner.add("-");//8
				}
				
				if(rs.getString("to_date") != null && !rs.getString("to_date").equals("")) {
					alInner.add(uF.getDateFormat(rs.getString("to_date"), DBDATE,CF.getStrReportDateFormat()));//9
				} else {
					alInner.add("-");//9
				}
				
				alInner.add(uF.showData(rs.getString("designation"), "-"));//10
				alInner.add(uF.showData(rs.getString("responsibilities"), "-"));//11
				alInner.add(uF.showData(rs.getString("skills"), "-"));//12
				
				alInner.add(uF.showData(rs.getString("report_manager_ph_no"), "-"));//13
				alInner.add(uF.showData(rs.getString("hr_manager"), "-"));//14
				alInner.add(uF.showData(rs.getString("hr_manager_ph_no"), "-"));//15
				alInner.add(uF.showData(rs.getString("experience_letter"), "-"));//16
			//===start parvez date: 08-08-2022===
				alInner.add(uF.showData(rs.getString("emp_esic_no"), "-"));//17
				alInner.add(uF.showData(rs.getString("uan_no"), "-"));//18
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

	public List<List<String>> selectCandiReferences(Connection con, int empId, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<String>> alCandiReferences = new ArrayList<List<String>>();
		
		try {

			pst = con
					.prepareStatement("SELECT * FROM candidate_references WHERE emp_id = ? ");
			pst.setInt(1, empId);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("ref_id"));//0
				alInner.add(uF.showData(rs.getString("ref_name"), "-"));//1
				alInner.add(uF.showData(rs.getString("ref_company"), "-"));//2
				alInner.add(uF.showData(rs.getString("ref_designation"), "-"));//3
				alInner.add(uF.showData(rs.getString("ref_contact_no"), "-"));//4
				alInner.add(uF.showData(rs.getString("ref_email_id"), "-"));//5
							
				alCandiReferences.add(alInner);
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
		return alCandiReferences;
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
				alInner.add(uF.showData(rs.getString("member_name"), "-"));
				alInner.add(uF.getDateFormat(rs.getString("member_dob"), DBDATE, DATE_FORMAT));
				alInner.add(uF.showData(rs.getString("member_education"), "-"));
				alInner.add(uF.showData(rs.getString("member_occupation"), "-"));
				alInner.add(uF.showData(rs.getString("member_contact_no"), "-"));
				alInner.add(uF.showData(rs.getString("member_email_id"), "-"));
				alInner.add(uF.showData(uF.charMappingMaleFemale(rs.getString("member_gender")), "-"));
				alInner.add(uF.showData(rs.getString("member_type"),"-"));
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

	public List<List<Object>> selectDocuments(Connection con, int empId, String filePath, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<Object>> alDocuments = new ArrayList<List<Object>>();
		
		try {

			pst = con.prepareStatement("SELECT * FROM candidate_documents_details where emp_id = ? and documents_type !=?");
			pst.setInt(1, empId);
			pst.setString(2, "Resume");
			rs = pst.executeQuery();
		
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				
					ArrayList<Object> alInner1 = new ArrayList<Object>();
					alInner1.add(rs.getInt("documents_id") + "");
					alInner1.add(uF.showData(rs.getString("documents_name"), ""));
					alInner1.add(uF.showData(rs.getString("documents_type"), ""));
					alInner1.add(rs.getInt("emp_id") + "");
	
					// File fileName = new
					// File(filePath+rs.getString("documents_file_name"));
	
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
	
	public List<List<Object>> selectResumes(Connection con, int empId, String filePath, UtilityFunctions uF) {

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
					alInner1.add(rs.getInt("documents_id") + "");//0
					alInner1.add(uF.showData(rs.getString("documents_name"), ""));//1
					alInner1.add(uF.showData(rs.getString("documents_type"), ""));//2
					alInner1.add(rs.getInt("emp_id") + "");//3
	
					File fileName = new File(rs.getString("documents_file_name") != null ? rs.getString("documents_file_name") : "");
					alInner1.add(fileName);
					String extenstion = null;
					if(rs.getString("documents_file_name") !=null && !rs.getString("documents_file_name").trim().equals("")){
						extenstion = FilenameUtils.getExtension(rs.getString("documents_file_name").trim());
					}
					alInner1.add(extenstion);//5
					
					alResumes.add(alInner1);
//					System.out.println("alResumes==>"+alResumes);
					
				
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

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}


	public String getCandID() {
		return CandID;
	}

	public void setCandID(String candID) {
		CandID = candID;
	}

	public String getRoundID() {
		return roundID;
	}

	public void setRoundID(String roundID) {
		this.roundID = roundID;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

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

	public String getApprovedeny() {
		return approvedeny;
	}

	public void setApprovedeny(String approvedeny) {
		this.approvedeny = approvedeny;
	}

	
	public String getPanelrating() {
		return panelrating;
	}

	public void setPanelrating(String panelrating) {
		this.panelrating = panelrating;
	}

	

	public String getCtcOffer() {
		return ctcOffer;
	}

	public void setCtcOffer(String ctcOffer) {
		this.ctcOffer = ctcOffer;
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
}