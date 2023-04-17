package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillExpYears;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillStatus;
import com.konnect.jpms.select.FillText;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CandidateReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF = null; 
	String strAction = null;

	private static Logger log = Logger.getLogger(CandidateReport.class);

	private String operation;
	private String candidateId;
	private String currCTC;
	private String expectedCTC;
	private String noticePeriod;
	private String minCurrCTC;
	private String minExpectedCTC;
	private String minNoticePeriod;
	private String maxCurrCTC;
	private String maxExpectedCTC;
	private String maxNoticePeriod;
	private List<FillMonth> monthList;
	private List<FillYears> yearList;
	private String strMonth;
	private String strYear;
	
	private List<FillText> alEmail = new ArrayList<FillText>();
	private List<FillText> alPanCard = new ArrayList<FillText>();
	private String f_pancard;
	private String f_email;
	
	private String recruitId;
	private String jobcode;
	private String empName;
	private String[] checkStatus_reportfilter;
	
	private String f_org;
	private List<FillOrganisation> organisationList;

	private String[] f_wlocation;
	private List<FillWLocation> workList;
    
	private String[] strMinEducation;
	private List<FillEducational> eduList;
    
	private String[] strSkills;
	private List<FillSkills> skillsList;
	
	private String[] strExperience;
	private List<FillExpYears> expYearsList;
	private List<FillStatus> statusList;
	String strBaseUserType;

	public String execute() throws Exception {
		
		session = request.getSession();
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		
		UtilityFunctions uF = new UtilityFunctions();
//		EncryptionUtility eU = new EncryptionUtility();
		
		/*if(getCandidateId() != null && uF.parseToInt(getCandidateId()) == 0) {
			String decodeCandidateId = eU.decode(getCandidateId());
			setCandidateId(decodeCandidateId);
		}
		
		if(getRecruitId() != null && uF.parseToInt(getRecruitId()) == 0) {
			String decodeRecruitId = eU.decode(getRecruitId());
			setRecruitId(decodeRecruitId);
		}*/
		
		request.setAttribute(PAGE, "/jsp/recruitment/CandidateReport.jsp");
		request.setAttribute(TITLE, "Candidate Database");

		if(strUserType==null || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(RECRUITER)
			&& !strUserType.equalsIgnoreCase(HRMANAGER) && !strUserType.equalsIgnoreCase(CEO))) {
		 	
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-user-circle-o\"></i><a href=\"RecruitmentDashboard.action\" style=\"color: #3c8dbc;\"> Recruitment</a></li>" +
				"<li class=\"active\">Candidates</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		if(strUserType != null && (strUserType.equals(ADMIN) || strUserType.equals(HRMANAGER))) {
			updateUserAlerts(uF, NEW_CANDIDATE_FILL_ALERT);
		}
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		workList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
		
		eduList=new FillEducational(request).fillEducationalQual();
		skillsList=new FillSkills(request).fillSkillsWithId();
		monthList = new FillMonth().fillMonth();
		expYearsList = new FillExpYears(request).fillExpYears();
		statusList = new FillStatus(request).fillStatus();
		
		if(getOperation() != null && getOperation().equals("D")) {
			deleteCandidate(uF);
		}
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		//Created By Dattatray 15-06-2022
		strAction = request.getServletPath();
		if(strAction!=null) {
			strAction = strAction.replace("/","");
		}
				
		viewEmployee(uF);
		getSelectedFilter(uF);
		loadPageVisitAuditTrail();//Created By Dattatray 15-06-2022
		return SUCCESS;

	}

	//Created By Dattatray 15-06-2022
	private void loadPageVisitAuditTrail() {
		Connection con=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpProfile = CF.getEmpNameMap(con,null,strSessionEmpId);
			StringBuilder builder = new StringBuilder();
			builder.append(hmEmpProfile.get(strSessionEmpId) +"  accessed "+strAction);
			
			
			
			CF.pageVisitAuditTrail(con,CF,uF, strSessionEmpId, strAction, strBaseUserType, builder.toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			db.closeConnection(con);
		}
	}
	
	private void updateUserAlerts(UtilityFunctions uF, String alertType) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
		try {
			con = db.makeConnection(con);
			String strDomain = request.getServerName().split("\\.")[0];
			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
			userAlerts.setStrDomain(strDomain);
			userAlerts.setStrEmpId(""+nEmpId);
			userAlerts.set_type(alertType);
			userAlerts.setStatus(UPDATE_ALERT);
			Thread t = new Thread(userAlerts);
			t.run();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
	
	
	private void deleteCandidate(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from candidate_application_details where candidate_id = ?");
		
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("delete from candidate_activity_details where candi_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.executeUpdate();
			pst.close();
			//**
			pst = con.prepareStatement("delete from candidate_documents_details where emp_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("delete from candidate_education_details where emp_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("delete from candidate_family_members where emp_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("delete from candidate_hobbies_details where emp_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("delete from candidate_interview_availability where emp_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("delete from candidate_interview_panel where candidate_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("delete from candidate_interview_panel_availability where candidate_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("delete from candidate_languages_details where emp_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("delete from candidate_medical_details where emp_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("delete from candidate_personal_details where emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("delete from candidate_prev_employment where emp_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("delete from candidate_references where emp_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("delete from candidate_salary_details where emp_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.executeUpdate();
			pst.close();
			//**
			pst = con.prepareStatement("delete from candidate_skills_description where emp_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("delete from candidate_skills_rating_details where candidate_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.executeUpdate();
			pst.close();
			
//			System.out.println("pst ===> " + pst);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public Map<String,String> getCandidateExpMap(Connection con,UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String,String> hmCandidateExperience = new HashMap<String,String>();
		try {
			pst = con.prepareStatement("select * from candidate_prev_employment order by emp_id");
//			System.out.println("exp pst==>"+pst);
			rst=pst.executeQuery();
			String strCandidateIdOld = null;
			String strCandidateIdNew = null;
			int noyear = 0,nomonth = 0,nodays = 0;
			while(rst.next()) {
				
					if(rst.getString("from_date") != null && !rst.getString("from_date").equals("") && rst.getString("to_date") != null && !rst.getString("to_date").equals("")) {
//						System.out.println("empId==>"+rst.getString("emp_id")+"==>fromDate==>"+rst.getString("from_date")+"==>todate==>"+rst.getString("to_date"));
						strCandidateIdNew = rst.getString("emp_id");
						if(strCandidateIdNew!=null && !strCandidateIdNew.equals(strCandidateIdOld)) {
						
							noyear=0;
							nomonth=0;
							nodays=0;
						}
						
						String datedif=uF.dateDifference(rst.getString("from_date"),DBDATE , rst.getString("to_date"), DBDATE);
			    		long datediff=uF.parseToLong(datedif);		
			    		
				    	noyear+=(int) (datediff/365);
				    	nomonth+=(int) ((datediff%365)/30);
				    	nodays+=(int) ((datediff%365)%30);
				     
				    	if(nodays>30){
				    		nomonth=nomonth+1;
				    	}
				    	if(nomonth>12){
				    		nomonth=nomonth-12;
				    		noyear=noyear+1;
				    	}
				    	
//				    	System.out.println("CR/315--hmCandidateExperience="+hmCandidateExperience);
				    	
				    	hmCandidateExperience.put(rst.getString("emp_id")+"_YEAR",""+noyear); 
				    	hmCandidateExperience.put(rst.getString("emp_id")+"_MONTH",""+nomonth); 
				    	hmCandidateExperience.put(rst.getString("emp_id")+"_DAY",""+nodays); 
				    	strCandidateIdOld = strCandidateIdNew;
				}
			}			    	
			rst.close();
			pst.close();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
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
		return hmCandidateExperience;
	}
	
	
	public String viewEmployee(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null; 
		Database db = new Database();
		db.setRequest(request);
//		EncryptionUtility eU = new EncryptionUtility();
		
		try {
			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			
			Map<String,String> hmCandidateExperience =  getCandidateExpMap(con, uF);
			int nCount = 0;
//			int currentYear = uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"));
			pst = con.prepareStatement("select financial_year_from FROM financial_year_details order by financial_year_from limit 1");
//			System.out.println("1 pst===> "+pst);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String fYear = "";
			while (rst.next()) {
				fYear = rst.getString("financial_year_from");
			}
			rst.close();
			pst.close();
			
			int fStartYear = uF.parseToInt(uF.getDateFormat(fYear, DBDATE, "yyyy"));
			yearList = new FillYears().fillYears(uF.getCurrentDate(CF.getStrTimeZone()), ""+fStartYear); 
			
			Map<String, String> hmreq_designation_name = new HashMap<String, String>();

			Map<String, String> hmreq_job_location = new HashMap<String, String>();
			String strRequirement = null;
			pst = con.prepareStatement("Select job_code, no_position,designation_name,wlocation_name,recruitment_id from recruitment_details join designation_details using(designation_id) join work_location_info on(wlocation=wlocation_id) where job_approval_status=1 and recruitment_id=? ");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
//			System.out.println("2 pst===> "+pst);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {

				hmreq_designation_name.put(rst.getString("recruitment_id"), rst.getString("designation_name"));
				hmreq_job_location.put(rst.getString("recruitment_id"), rst.getString("wlocation_name"));
				setJobcode(rst.getString("job_code"));  	// setting job code
				strRequirement = rst.getString("no_position");
			}
			rst.close();
			pst.close();
			
			request.setAttribute("recruitId", getRecruitId());
			request.setAttribute("hmreq_designation_name", hmreq_designation_name);
			request.setAttribute("hmreq_job_location", hmreq_job_location);
			request.setAttribute("strRequirement", strRequirement);
			
			StringBuilder sbQuery1 = new StringBuilder();
			List<String> lstSkills = new ArrayList<String>();
			List<String> lstEducation = new ArrayList<String>();
			StringBuilder sbRecruitID = new StringBuilder();
			
			if (strSkills != null) {
				for (int i = 0; i < strSkills.length; i++) {
					if (!strSkills[i].trim().equals("")) {
							lstSkills.add(strSkills[i].trim());
					}
				}
			}
//			System.out.println("sbSkills ===> "+sbSkills);
			
			
			
			if((uF.parseToInt(getF_org())>0) ||(getF_wlocation() != null && getF_wlocation().length > 0)) {
				sbQuery1.append("select recruitment_id from recruitment_details where org_id > 0 ");
				if(getF_org() != null && !getF_org().equals("")){
					sbQuery1.append(" and org_id = "+uF.parseToInt(getF_org()));	
				}
				if(getF_wlocation() != null && !getF_wlocation().equals("")){
//					sbQuery1.append(" and wlocation = "+getF_wlocation()+" ");
					sbQuery1.append(" and wlocation in ("+StringUtils.join(getF_wlocation(), ",")+") ");
				}
				pst = con.prepareStatement(sbQuery1.toString());
//				System.out.println("3 pst===> "+pst);
				rst = pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
				while (rst.next()) {
					if(sbRecruitID == null || sbRecruitID.toString().equals("")){
						sbRecruitID.append(rst.getString("recruitment_id"));
					}else{
						sbRecruitID.append(","+rst.getString("recruitment_id"));
					}
				}
				rst.close();
				pst.close();
				
				if(sbRecruitID == null || sbRecruitID.toString().equals("")){
					sbRecruitID.append("0");
				}
			}
//			System.out.println("sbRecruitID ===> "+sbRecruitID);
			
			List<String> allFilterCandidate = new ArrayList<String>();
			List<String> orgLocCandidate = new ArrayList<String>();
			List<String> skillCandidate = new ArrayList<String>();
			List<String> eduCandidate = new ArrayList<String>();
			
//			if((getF_org() != null && !getF_org().equals("")) && (getF_wlocation() != null && getF_wlocation().length > 0) && (getCheckStatus_reportfilter() == null || getCheckStatus_reportfilter().length == 0)) {
			if((getF_org() != null && !getF_org().equals("")) && (getF_wlocation() == null || getF_wlocation().length == 0) && (getCheckStatus_reportfilter() == null || getCheckStatus_reportfilter().length == 0)) {
				StringBuilder sbQuery = new StringBuilder(); 
				sbQuery.append("select emp_per_id from candidate_personal_details where org_id = ? ");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getF_org()));
//				System.out.println("4 pst===> "+pst);
				rst = pst.executeQuery();
				while (rst.next()) {
					if(!allFilterCandidate.contains(rst.getString("emp_per_id"))) {
						allFilterCandidate.add(rst.getString("emp_per_id"));
					}
					if(!orgLocCandidate.contains(rst.getString("emp_per_id"))) {
						orgLocCandidate.add(rst.getString("emp_per_id"));
					}
				}
				rst.close();
				pst.close();
			}
			
			
			
			if((sbRecruitID != null && !sbRecruitID.toString().equals("")) ||(getCheckStatus_reportfilter() != null && getCheckStatus_reportfilter().length > 0)){
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select distinct(candidate_id) from candidate_application_details where recruitment_id > 0 ");
				if(sbRecruitID != null && !sbRecruitID.toString().equals("")){
					sbQuery.append(" and recruitment_id in("+sbRecruitID.toString()+") ");
				}
				
				/*if(getCheckStatus_reportfilter()!= null && getCheckStatus_reportfilter().length>0) {
					System.out.println("getCheckStatus_reportfilter()==>"+Arrays.asList(getCheckStatus_reportfilter()));
				}*/
				
				if(getCheckStatus_reportfilter() != null && getCheckStatus_reportfilter().length > 0){
//					sbQuery.append(" and application_status = "+getCheckStatus_reportfilter()+" ");
					sbQuery.append(" and application_status in ("+StringUtils.join(getCheckStatus_reportfilter(), ",")+") ");
				}
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("5 pst===> "+pst);
				rst = pst.executeQuery();
				while (rst.next()) {
					if(allFilterCandidate != null && !allFilterCandidate.contains(rst.getString("candidate_id"))) {
						allFilterCandidate.add(rst.getString("candidate_id"));
					}
					if(orgLocCandidate!=null && !orgLocCandidate.contains(rst.getString("candidate_id"))) {
						orgLocCandidate.add(rst.getString("candidate_id"));
					}
				}
				rst.close();
				pst.close();
			}
			
//			System.out.println("allFilterCandidate==>"+allFilterCandidate);
//			System.out.println("orgLocCandidate==>"+orgLocCandidate);
//			int skillCnt =0;
			for(int i=0; lstSkills != null && !lstSkills.isEmpty() && i < lstSkills.size(); i++) {
				pst = con.prepareStatement("select skill_id,emp_id from candidate_skills_description where skill_id in("+lstSkills.get(i)+")");
//				System.out.println("6 pst===> "+pst);
				rst = pst.executeQuery();
				while (rst.next()) {
					if(!allFilterCandidate.contains(rst.getString("emp_id"))) {
						allFilterCandidate.add(rst.getString("emp_id"));
					}
					if(!skillCandidate.contains(rst.getString("emp_id"))) {
						skillCandidate.add(rst.getString("emp_id"));
					}
				}
				rst.close();
				pst.close();
			}
			
			if (strMinEducation != null) {
				for (int i = 0; i < strMinEducation.length; i++) {
					if (!strMinEducation[i].trim().equals("")) {
							lstEducation.add(strMinEducation[i].trim());
					}
				}
			}
//			System.out.println("sbEducation ===> "+sbEducation);
//			int eduCnt=0;
			for(int i=0; lstEducation != null && !lstEducation.isEmpty() && i < lstEducation.size(); i++) {
				pst = con.prepareStatement("select education_id,emp_id from candidate_education_details where education_id in("+lstEducation.get(i)+")");
//				System.out.println("7 pst===> "+pst);
				rst = pst.executeQuery();
				while (rst.next()) {
					if(!allFilterCandidate.contains(rst.getString("emp_id"))) {
						allFilterCandidate.add(rst.getString("emp_id"));
					}
					if(!eduCandidate.contains(rst.getString("emp_id"))) {
						eduCandidate.add(rst.getString("emp_id"));
					}
				}
				rst.close();
				pst.close();
			}
			
//			System.out.println("allFilterCandidate ===>> " + allFilterCandidate);
//			System.out.println("orgLocCandidate ===>> " + orgLocCandidate);
//			System.out.println("skillCandidate ===>> " + skillCandidate);
//			System.out.println("eduCandidate ===>> " + eduCandidate); 
			
			List<String> finalCandiId = new ArrayList<String>();
			for(int i=0; allFilterCandidate!=null && !allFilterCandidate.isEmpty() && i<allFilterCandidate.size(); i++) {
				if(orgLocCandidate != null && !orgLocCandidate.isEmpty() && lstSkills != null && !lstSkills.isEmpty() && lstEducation != null && !lstEducation.isEmpty()) {
					if(orgLocCandidate.contains(allFilterCandidate.get(i)) && skillCandidate.contains(allFilterCandidate.get(i)) && eduCandidate.contains(allFilterCandidate.get(i))) {
						finalCandiId.add(allFilterCandidate.get(i));
					}
				} else if(orgLocCandidate != null && !orgLocCandidate.isEmpty() && lstSkills != null && !lstSkills.isEmpty()) {
					if(orgLocCandidate.contains(allFilterCandidate.get(i)) && skillCandidate.contains(allFilterCandidate.get(i))) {
						finalCandiId.add(allFilterCandidate.get(i));
					}
				} else if(orgLocCandidate != null && !orgLocCandidate.isEmpty() && lstEducation != null && !lstEducation.isEmpty()) {
					if(orgLocCandidate.contains(allFilterCandidate.get(i)) && eduCandidate.contains(allFilterCandidate.get(i))) {
						finalCandiId.add(allFilterCandidate.get(i));
					}
				} else if(lstSkills != null && !lstSkills.isEmpty() && lstEducation != null && !lstEducation.isEmpty()) {
					if(skillCandidate.contains(allFilterCandidate.get(i)) && eduCandidate.contains(allFilterCandidate.get(i))) {
						finalCandiId.add(allFilterCandidate.get(i));
					}
				} else {
					finalCandiId.add(allFilterCandidate.get(i));
				}
			}
			
			
			StringBuilder sbFinalCadiIDS = null;
//			System.out.println("finalCandiId 1111===>> " + finalCandiId);
			for(int a=0; finalCandiId != null && !finalCandiId.isEmpty() && a<finalCandiId.size(); a++) {
				if(sbFinalCadiIDS == null) {
					sbFinalCadiIDS = new StringBuilder();
					sbFinalCadiIDS.append(finalCandiId.get(a));
				} else {
					sbFinalCadiIDS.append(","+finalCandiId.get(a));
				}
			}
//			System.out.println("sbFinalCadiIDS ===> " + sbFinalCadiIDS.toString());
			List<String> alInner;
			List<List<String>> al = new ArrayList<List<String>>();
			
//			System.out.println("getF_org() ===>> " + getF_org());
//			System.out.println("getF_wlocation() ===>> " + getF_wlocation());
//			System.out.println("allFilterCandidate ===>> " + allFilterCandidate);
//			System.out.println("lstSkills ===>> " + lstSkills);
//			System.out.println("lstEducation ===>> " + lstEducation);
			if(getCurrCTC() == null) {
				setCurrCTC("0 - 10000000");
			}
			if(getExpectedCTC() == null) {
				setExpectedCTC("0 - 10000000");
			}
			if(getNoticePeriod() == null) {
				setNoticePeriod("0 - 365");
			}
			String[] arrCurrCTC = getCurrCTC().split("-");
			String[] arrExpectedCTC = getExpectedCTC().split("-");
			String[] arrNoticePeriod = getNoticePeriod().split("-");
			
			setMinCurrCTC(arrCurrCTC[0].trim());
			setMinExpectedCTC(arrExpectedCTC[0].trim());
			setMinNoticePeriod(arrNoticePeriod[0].trim());
			
			setMaxCurrCTC(arrCurrCTC[1].trim());
			setMaxExpectedCTC(arrExpectedCTC[1].trim());
			setMaxNoticePeriod(arrNoticePeriod[1].trim());
			
			String minDate = "";
			String maxDate = "";
			if(getStrMonth() != null && !getStrMonth().equals("") && getStrYear() != null && !getStrYear().equals("")) {
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth()) -1 );
				cal.set(Calendar.YEAR, uF.parseToInt(getStrYear()));
				
				int maxDays = cal.getActualMaximum(Calendar.DATE);
				int minDays = cal.getActualMinimum(Calendar.DATE);
				cal.set(Calendar.DAY_OF_MONTH, minDays);
				minDate = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
				
				cal.set(Calendar.DAY_OF_MONTH, maxDays);
				maxDate = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
			}
			
			StringBuilder query=new StringBuilder();
			if((getF_org()!=null && !getF_org().equals("")) || (getF_wlocation()!=null && getF_wlocation().length > 0) || (allFilterCandidate != null && !allFilterCandidate.isEmpty()) || (lstSkills != null && !lstSkills.isEmpty()) || (lstEducation!= null && !lstEducation.isEmpty())) {
//				System.out.println("if ===> ");
				//Started By Dattatray Date:08-10-21 Note : is_rejected added
				query.append("select years,emp_per_id,emp_fname,emp_mname,emp_lname,emp_date_of_birth,emp_city_id,emp_email,emp_pan_no,current_ctc," +
						"expected_ctc,notice_period,emp_image,availability_for_interview,emp_entry_date,is_rejected " +
						"from candidate_personal_details cpd left join (select sum(to_date-from_date)/365 as years ,emp_id from " +
						"candidate_prev_employment cpe group by emp_id) as a on cpd.emp_per_id = a.emp_id where cpd.emp_per_id>0 and " +
						"cpd.candididate_emp_id is null ");
				if(minDate != null && !minDate.equals("") && maxDate != null && !maxDate.equals("")){
					query.append(" and emp_per_id in(select candidate_id from candidate_application_details where candidate_id in ("+sbFinalCadiIDS.toString()+") and " +
						"application_date between '"+uF.getDateFormat(minDate, DATE_FORMAT)+"' and '" +uF.getDateFormat(maxDate, DATE_FORMAT)+"') ");
				} else if(sbFinalCadiIDS != null && !sbFinalCadiIDS.toString().equals("")){
					query.append(" and emp_per_id in("+sbFinalCadiIDS.toString()+") ");
				} else {
					query.append(" and emp_per_id in(0) ");
				}
				if(uF.parseToDouble(arrCurrCTC[0].trim()) == 0) {
					query.append(" and (cpd.current_ctc is null or (cpd.current_ctc >= "+uF.parseToDouble(arrCurrCTC[0].trim())+" and cpd.current_ctc <= "+ uF.parseToDouble(arrCurrCTC[1].trim())+"))");
				} else {
					query.append(" and cpd.current_ctc >= "+uF.parseToDouble(arrCurrCTC[0].trim())+" and cpd.current_ctc <= "+ uF.parseToDouble(arrCurrCTC[1].trim()));
				}
				if(uF.parseToDouble(arrExpectedCTC[0].trim()) == 0) {
					query.append(" and (cpd.expected_ctc is null or (cpd.expected_ctc >= "+uF.parseToDouble(arrExpectedCTC[0].trim())+" and cpd.expected_ctc <= "+ uF.parseToDouble(arrExpectedCTC[1].trim())+"))");
				} else {
					query.append(" and cpd.expected_ctc >= "+uF.parseToDouble(arrExpectedCTC[0].trim())+" and cpd.expected_ctc <= "+ uF.parseToDouble(arrExpectedCTC[1].trim()));
				}
				if(uF.parseToInt(arrNoticePeriod[0].trim()) == 0) {
					query.append(" and (cpd.notice_period is null or (cpd.notice_period >= "+uF.parseToInt(arrNoticePeriod[0].trim())+" and cpd.notice_period <= "+ uF.parseToInt(arrNoticePeriod[1].trim())+"))");
				} else {
					query.append(" and cpd.notice_period >= "+uF.parseToInt(arrNoticePeriod[0].trim())+" and cpd.notice_period <= "+ uF.parseToInt(arrNoticePeriod[1].trim()));
				}
				
				if (getStrExperience() != null && getStrExperience().length > 0) {

					StringBuilder sbExp = null; 
					for (int i = 0; i < getStrExperience().length; i++) {
						if (getStrExperience()[i] != null && !getStrExperience()[i].trim().equals("")) {
							if(uF.parseToInt(getStrExperience()[i].trim())==1){
								if(sbExp == null){
									sbExp = new StringBuilder();
									sbExp.append(" (years<=1)");
								} else {
									sbExp.append(" or (years<=1)");
								}
							} else if(uF.parseToInt(getStrExperience()[i].trim())==2) {
								if(sbExp == null){
									sbExp = new StringBuilder();
									sbExp.append(" (years<=2)");
								} else {
									sbExp.append(" or (years<=2)");
								}
							} else if(uF.parseToInt(getStrExperience()[i].trim())==3) {
								if(sbExp == null){
									sbExp = new StringBuilder();
									sbExp.append(" (years<=5)");
								} else {
									sbExp.append(" or (years<=5)");
								}
							} else if(uF.parseToInt(getStrExperience()[i].trim())==4) {
								if(sbExp == null){
									sbExp = new StringBuilder();
									sbExp.append(" (years<=10)");
								} else {
									sbExp.append(" or (years<=10)");
								}
							} else if(uF.parseToInt(getStrExperience()[i].trim())==5){
								if(sbExp == null){
									sbExp = new StringBuilder(); 
									sbExp.append(" (years>=10)");
								} else {
									sbExp.append(" or (years>=10)");
								}
							}
						}
					}
					if(sbExp!=null){
						query.append(" and ("+sbExp.toString()+")");
					}
				}
			} else {
//				System.out.println("else ===> ");
				//===start parvez date: 01-11-2021 Note: add is_rejected in select statement===	
				query.append("select years,emp_per_id,emp_fname,emp_mname,emp_lname,emp_date_of_birth,emp_city_id,emp_email,emp_pan_no,current_ctc," +
						"expected_ctc,notice_period,emp_image,availability_for_interview,emp_entry_date,is_rejected " +
						"from candidate_personal_details cpd left join (select sum(to_date-from_date)/365 as years ,emp_id from " +
						"candidate_prev_employment cpe group by emp_id) as a on cpd.emp_per_id = a.emp_id where cpd.emp_per_id>0 and " +
						"cpd.candididate_emp_id is null ");
				if(minDate != null && !minDate.equals("") && maxDate != null && !maxDate.equals("")){
					query.append(" and emp_per_id in(select candidate_id from candidate_application_details where " +
						"application_date between '"+uF.getDateFormat(minDate, DATE_FORMAT)+"' and '" +uF.getDateFormat(maxDate, DATE_FORMAT)+"') ");
				} 
				if(uF.parseToDouble(arrCurrCTC[0].trim()) == 0) {
					query.append(" and (cpd.current_ctc is null or (cpd.current_ctc >= "+uF.parseToDouble(arrCurrCTC[0].trim())+" and cpd.current_ctc <= "+ uF.parseToDouble(arrCurrCTC[1].trim())+"))");
				} else {
					query.append(" and cpd.current_ctc >= "+uF.parseToDouble(arrCurrCTC[0].trim())+" and cpd.current_ctc <= "+ uF.parseToDouble(arrCurrCTC[1].trim()));
				}
				if(uF.parseToDouble(arrExpectedCTC[0].trim()) == 0) {
					query.append(" and (cpd.expected_ctc is null or (cpd.expected_ctc >= "+uF.parseToDouble(arrExpectedCTC[0].trim())+" and cpd.expected_ctc <= "+ uF.parseToDouble(arrExpectedCTC[1].trim())+"))");
				} else {
					query.append(" and cpd.expected_ctc >= "+uF.parseToDouble(arrExpectedCTC[0].trim())+" and cpd.expected_ctc <= "+ uF.parseToDouble(arrExpectedCTC[1].trim()));
				}
				if(uF.parseToInt(arrNoticePeriod[0].trim()) == 0) {
					query.append(" and (cpd.notice_period is null or (cpd.notice_period >= "+uF.parseToInt(arrNoticePeriod[0].trim())+" and cpd.notice_period <= "+ uF.parseToInt(arrNoticePeriod[1].trim())+"))");
				} else {
					query.append(" and cpd.notice_period >= "+uF.parseToInt(arrNoticePeriod[0].trim())+" and cpd.notice_period <= "+ uF.parseToInt(arrNoticePeriod[1].trim()));
				}
				
					if (getStrExperience() != null && getStrExperience().length > 0) {

						StringBuilder sbExp = null; 
						for (int i = 0; i < getStrExperience().length; i++) {
							if (getStrExperience()[i] != null && !getStrExperience()[i].trim().equals("")) {
								if(uF.parseToInt(getStrExperience()[i].trim())==1){
									if(sbExp == null){
										sbExp = new StringBuilder();
										sbExp.append(" (years<=1)");
									} else {
										sbExp.append(" or (years<=1)");
									}
								} else if(uF.parseToInt(getStrExperience()[i].trim())==2) {
									if(sbExp == null){
										sbExp = new StringBuilder();
										sbExp.append(" (years<=2 )");
									} else {
										sbExp.append(" or (years<=2)");
									}
								} else if(uF.parseToInt(getStrExperience()[i].trim())==3) {
									if(sbExp == null){
										sbExp = new StringBuilder();
										sbExp.append(" (years<=5)");
									} else {
										sbExp.append(" or (years<=5 )");
									}
								} else if(uF.parseToInt(getStrExperience()[i].trim())==4) {
									if(sbExp == null){
										sbExp = new StringBuilder();
										sbExp.append(" (years <=10)");
									} else {
										sbExp.append(" or (years <=10)");
									}
								} else if(uF.parseToInt(getStrExperience()[i].trim())==5){
									if(sbExp == null){
										sbExp = new StringBuilder(); 
										sbExp.append(" (years>=10)");
									} else {
										sbExp.append(" or (years>=10)");
									}
								}
							}
						}
						if(sbExp!=null){
							query.append(" and ("+sbExp.toString()+")");
						}
					}
			}

			query.append(" order by emp_entry_date desc");
			pst = con.prepareStatement(query.toString());
//			System.out.println("CR/791--Candidate pst ===> "+pst);
			rst = pst.executeQuery();
			
			StringBuilder sbCandidateIds = new StringBuilder(); 
			String oldEmp =null;
			while (rst.next()) {
				
				FillText objFillTextEmail = new FillText(rst.getString("emp_email"), rst.getString("emp_email"));
				FillText objFillTextPanCard = new FillText(rst.getString("emp_pan_no"), rst.getString("emp_pan_no"));
				
				alEmail.add(objFillTextEmail);
				alPanCard.add(objFillTextPanCard);
				

				if(oldEmp==null || (oldEmp!=null && !oldEmp.equals(rst.getString("emp_per_id")))){
					 alInner=new ArrayList<String>();	

					if(sbCandidateIds.length()==0){
						sbCandidateIds.append(rst.getString("emp_per_id"));
					}else{
						sbCandidateIds.append(","+rst.getString("emp_per_id"));
					}
					alInner.add("");
					alInner.add("");
					alInner.add(rst.getString("emp_per_id"));
					
					alInner.add(rst.getString("emp_image")); 
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rst.getString("emp_mname");
						}
					}
					
					String empFullName = rst.getString("emp_fname") +strEmpMName+ " " + rst.getString("emp_lname");
					alInner.add(rst.getString("emp_fname") +strEmpMName+ " " + rst.getString("emp_lname"));
					
					alInner.add(""); 
					alInner.add(""); 
//					String encodeEmpId = eU.encode(rst.getString("emp_per_id"));
//					String encodeRecruitId = eU.encode(getRecruitId());
					String encodeEmpId = rst.getString("emp_per_id");
					String encodeRecruitId = getRecruitId();
					alInner.add("<a class=\"factsheet\" href=\"CandidateMyProfile.action?CandID="+encodeEmpId+"&recruitId="+encodeRecruitId+"\"> </a>");
					alInner.add("");//8
					
					if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))) {
						StringBuilder sbApproveDeny = new StringBuilder();
						sbApproveDeny.append("<div id=\"myDivM" + nCount + "\" > ");
//						System.out.println(" Candi ID ===> "+rst.getString("emp_per_id"));
						sbApproveDeny.append("<a href=\"javascript:void(0)\" style=\"float: left;\" onclick=\"getCandiApplicationsDetailsPopup('" + rst.getString("emp_per_id") + "', '"+empFullName+"');\">" + " <img src=\"images1/setting.png\" title=\"Application Tracker\" /></a> ");
						
						boolean flag = getCandidateStatus(con, uF, rst.getString("emp_per_id"));
						if(flag) {
							sbApproveDeny.append("<a href=\"javascript:void(0)\" style=\"float: left; margin-left: 5px;color:#DB3D3D;\" class=\"del\" title=\"Delete\" onclick=\"alert('You can not delete this candidate, this candidate is shortlisted for any job?')\"><i class=\"fa fa-trash\"></i></a>");
						} else {
							sbApproveDeny.append("<a href=\"CandidateReport.action?operation=D&candidateId="+encodeEmpId+"\" style=\"float: left; margin-left: 5px;color:#DB3D3D;\" class=\"del\" title=\"Delete\" onclick=\"return confirm('Are you sure you wish to delete candidate?')\"><i class=\"fa fa-trash\"></i></a>");
						}
						sbApproveDeny.append("<a href=\"javascript:void(0)\" style=\"float: left;\" onclick=\"sendOffer('" + rst.getString("emp_per_id") + "');\">" + " <i class=\"fa fa-plus-circle\"></i></a> ");

						sbApproveDeny.append("</div>");
						alInner.add(sbApproveDeny.toString()); //9
						
					} else {
						//MANAGERempId
						StringBuilder sbApproveDeny = new StringBuilder();
						sbApproveDeny.append("<div id=\"myDivM" + nCount + "\" > ");
						sbApproveDeny.append("<a href=\"javascript:void(0)\" style=\"float: left;\" onclick=\"getCandiApplicationsDetailsPopup('" + rst.getString("emp_per_id") + "', '"+empFullName+"');\">" + " <img src=\"images1/setting.png\" title=\"Application Tracker\" /></a> ");
						boolean flag = getCandidateStatus(con, uF, rst.getString("emp_per_id"));
						if(flag) {
							sbApproveDeny.append("<a href=\"javascript:void(0)\" style=\"float: left; margin-left: 5px;color:#DB3D3D;\" class=\"del\" title=\"Delete\" onclick=\"alert('You can not delete this candidate, this candidate is shortlisted for any job?')\"><i class=\"fa fa-trash\"></i></a>");
						} else {
							sbApproveDeny.append("<a href=\"CandidateReport.action?operation=D&candidateId="+encodeEmpId+"\" style=\"float: left; margin-left: 5px;color:#DB3D3D;\" class=\"del\" title=\"Delete\" onclick=\"return confirm('Are you sure you wish to delete candidate?')\"><i class=\"fa fa-trash\"></i></a>");
						}
						sbApproveDeny.append("<a href=\"javascript:void(0)\" style=\"float: left;\" onclick=\"sendOffer('" + rst.getString("emp_per_id") + "', '"+empFullName+"');\">" + " <i class=\"fa fa-plus-circle\"></i></a> ");

						sbApproveDeny.append("</div>");
						alInner.add(sbApproveDeny.toString()); //9
					}
					
					if(rst.getString("emp_date_of_birth") != null && !rst.getString("emp_date_of_birth").equals("")){
						alInner.add(uF.getTimeDurationBetweenDates(rst.getString("emp_date_of_birth"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF, uF, request, true, true, false)); //10
					}else{
						alInner.add("-"); //10
					}
					alInner.add(uF.showData(rst.getString("emp_city_id"),"-")); //11
					
					Map<String, String> hmLastJobData = getLastJobData(con, uF, rst.getString("emp_per_id"));
					alInner.add(uF.showData(hmLastJobData.get("JOB_NAME"), "-")); //12
					if(hmLastJobData!=null && !hmLastJobData.isEmpty()) {
						alInner.add(uF.showData(hmLastJobData.get("LAST_APPLIED_DATE"), "-")); //13
					} else {
						alInner.add(uF.showData(uF.getDateFormat(rst.getString("emp_entry_date"), DBDATE, CF.getStrReportDateFormat()), "-")); //13
					}
					alInner.add(uF.showData(rst.getString("current_ctc"), "N/A")); //14
					alInner.add(uF.showData(rst.getString("expected_ctc"), "N/A")); //15
					if(rst.getString("notice_period") != null && !rst.getString("notice_period").equals("")) {
						alInner.add(rst.getString("notice_period")+" days"); //16
					} else {
						alInner.add("N/A"); //16
					}
					alInner.add(rst.getString("availability_for_interview")); //17
					//Started By Dattatray Date:08-10-21
					alInner.add(uF.showData(rst.getString("is_rejected"), ""));//18
					//Ended By Dattatray Date:08-10-21
					al.add(alInner);
					oldEmp=rst.getString("emp_per_id");
				}
				
			}
			rst.close();
			pst.close();
			
			StringBuilder sbNewCandidate = null;
			for(int i=0;al!=null && i<al.size(); i++){
//				System.out.println("CR/903--al"+al.get(i));
				List<String> innerList = (List<String>) al.get(i);
				if(sbNewCandidate == null){
					sbNewCandidate = new StringBuilder();
					sbNewCandidate.append(innerList.get(2));
				} else {
					sbNewCandidate.append(","+innerList.get(2));
				}
			}
			if(sbNewCandidate!=null){
				List<String> alNewCandidate = new ArrayList<String>();
				pst=con.prepareStatement("select emp_per_id from candidate_personal_details where emp_entry_date>=? and emp_per_id in ("+sbNewCandidate.toString()+")");
				pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 10));
//				System.out.println("CR/913--new Candidate pst======>"+pst);
				rst = pst.executeQuery();
				while(rst.next()){
					alNewCandidate.add(rst.getString("emp_per_id"));
				}
				rst.close();
				pst.close();
				request.setAttribute("alNewCandidate", alNewCandidate);
			}

			request.setAttribute("jobcode", getJobcode());
			request.setAttribute("reportList", al);
			
			Map<String,String> hmCandidateExperience1=new HashMap<String, String>();
			Map<String, String> hmEducationDetails = new HashMap<String, String>();
			Map<String, String> hmSkillDetails = new HashMap<String, String>();
			
			
			if(sbCandidateIds.length()>=1) {
			
			pst = con.prepareStatement("select * from candidate_education_details where emp_id in ("+sbCandidateIds.toString()+") order by emp_id");
//			System.out.println("9 pst===> "+pst);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String strCandidateIdOld = null;
			String strCandidateIdNew = null;
			StringBuilder sbContainer = new StringBuilder();

			Map<String, String> hmDegreeName = CF.getDegreeNameMap(con);
			
			while(rst.next()) {
//				String strEducations = 	hmEducationDetails.get(rst.getString("emp_id"));
//				if(strEducations == null) {
//					strEducations = "";
//					strEducations = strEducations + hmDegreeName.get(rst.getString("education_id"));
//				} else {
//					strEducations = strEducations + ", " + hmDegreeName.get(rst.getString("education_id"));
//				}
//				hmEducationDetails.put(strCandidateIdNew, strEducations);
				
				strCandidateIdNew = rst.getString("emp_id");
				if (strCandidateIdNew != null && !strCandidateIdNew.equals(strCandidateIdOld)) {
					sbContainer.replace(0, sbContainer.length(), "");
				}
				if (strCandidateIdNew != null && strCandidateIdNew.equals(strCandidateIdOld))
					sbContainer.append(", " + hmDegreeName.get(rst.getString("education_id")));
				else
					sbContainer.append(hmDegreeName.get(rst.getString("education_id")));

				hmEducationDetails.put(strCandidateIdNew,sbContainer.toString());
				strCandidateIdOld = strCandidateIdNew;
			}
			rst.close();
			pst.close();
//			System.out.println("hmEducationDetails ===>> " + hmEducationDetails);
			

			pst = con.prepareStatement("select * from candidate_skills_description where emp_id in ("+sbCandidateIds.toString()+") order by emp_id");
//			System.out.println("10 pst===> "+pst);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			strCandidateIdOld = null;
			strCandidateIdNew = null;
			sbContainer.replace(0, sbContainer.length(), "");
			Map<String, String> hmSkillsName = CF.getSkillNameMap(con);
			while(rst.next()){
				strCandidateIdNew = rst.getString("emp_id");
				if (strCandidateIdNew != null && !strCandidateIdNew.equals(strCandidateIdOld)) {
					sbContainer.replace(0, sbContainer.length(), "");
				}
				
				if (strCandidateIdNew != null && strCandidateIdNew.equals(strCandidateIdOld)) {
					sbContainer.append(", " + hmSkillsName.get(rst.getString("skill_id")));
				} else {
					sbContainer.append(hmSkillsName.get(rst.getString("skill_id")));
				}
				hmSkillDetails.put(strCandidateIdNew, sbContainer.toString());
				strCandidateIdOld = strCandidateIdNew;
			}
			rst.close();
			pst.close();

			// Logic for multiple experience
			
			pst = con.prepareStatement("select * from candidate_prev_employment where emp_id in ("+sbCandidateIds.toString()+") order by emp_id");
//			System.out.println("11 pst===> "+pst);
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			strCandidateIdOld = null;
			strCandidateIdNew = null;
			//====start parvez===
			StringBuilder strCandidateIdNew1 = new StringBuilder();
			//====end parvez====
			int noyear = 0,nomonth = 0,nodays = 0;
			while(rst.next()) {
				strCandidateIdNew = rst.getString("emp_id");
				if(strCandidateIdNew!=null && !strCandidateIdNew.equals(strCandidateIdOld)){
				
					noyear=0;
					nomonth=0;
					nodays=0;
				}
					
					String datedif=uF.dateDifference(rst.getString("from_date"),DBDATE , rst.getString("to_date"), DBDATE);
		    		
					long datediff=uF.parseToLong(datedif);		    		 
				
			    	noyear+=(int) (datediff/365);
			    	nomonth+=(int) ((datediff%365)/30);
			    	nodays+=(int) ((datediff%365)%30);
			     
			    	if(nodays>30){
			    		nomonth=nomonth+1;
			    	}
			    	if(nomonth>12){
			    		nomonth=nomonth-12;
			    		noyear=noyear+1;
			    	}
			    		
			    	hmCandidateExperience1.put(rst.getString("emp_id"),""+noyear+" Y "+nomonth+" M"); 
			    	
			    	strCandidateIdOld = strCandidateIdNew;
			    	
			    	//====start parvez====
			    	if(noyear>0 || nomonth>0) {
				    	if(strCandidateIdNew1.length()==0){
				    		strCandidateIdNew1.append(strCandidateIdNew);
				    	}else{
				    		strCandidateIdNew1.append(","+strCandidateIdNew);
				    	}
			    	}
			    	//====end parvez====
			}
			rst.close();
			pst.close();
			
			/*System.out.println("CR/1036--hmCandidateExperience1"+hmCandidateExperience1);
			System.out.println("CR/1040--sbCandidateIds="+sbCandidateIds);
			System.out.println("CR/1040--strCandidateIdOld="+strCandidateIdOld);
			System.out.println("CR/1040--strCandidateIdNew1="+strCandidateIdNew1);*/
			//=====start parvedz====
			if(strCandidateIdNew1.length()==0){
				pst = con.prepareStatement("select * from candidate_personal_details where emp_per_id in ("+sbCandidateIds.toString()+") order by emp_per_id");
				rst=pst.executeQuery();
				while(rst.next()){
					hmCandidateExperience1.put(rst.getString("emp_per_id"), rst.getString("total_experience"));
				}
			}else{
				pst = con.prepareStatement("select * from candidate_personal_details where emp_per_id in ("+sbCandidateIds.toString()+") and emp_per_id not in ( "+strCandidateIdNew1+") order by emp_per_id");
				rst=pst.executeQuery();
				while(rst.next()){
					hmCandidateExperience1.put(rst.getString("emp_per_id"), rst.getString("total_experience"));
				}
			}
			
			//====end parvez====
			
			}
			
			request.setAttribute("hmExperienceDetails", hmCandidateExperience1);
			
			request.setAttribute("hmEducationDetails", hmEducationDetails);
			request.setAttribute("hmSkillDetails", hmSkillDetails);
//			request.setAttribute("hmExperienceDetails", hmExperienceDetails);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	
	private boolean getCandidateStatus(Connection con, UtilityFunctions uF, String candidateId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		boolean flag = false;
		try {
			pst = con.prepareStatement("select * from candidate_application_details where candidate_id = ? and application_status != 0");
			pst.setInt(1, uF.parseToInt(candidateId));
			rst=pst.executeQuery();
//			System.out.println("pst ===> " + pst);
			while(rst.next()) {
				flag = true;
			}
			rst.close();
			pst.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
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
		return flag;
	}
	

	private Map<String, String> getLastJobData(Connection con, UtilityFunctions uF, String candidateId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String, String> hmLastJobData = new HashMap<String, String>();
		try {
//			pst=con.prepareStatement("select cad.application_date,rd.job_code,rd.job_title from candidate_application_details cad, recruitment_details rd " +
//				" where cad.recruitment_id = rd.recruitment_id and cad.recruitment_id = (select max(recruitment_id) from candidate_application_details " +
//				" where candidate_id = ?) and candidate_id = ?");
			pst=con.prepareStatement("select cad.application_date,rd.job_code,rd.job_title from candidate_application_details cad, recruitment_details rd " +
					" where cad.recruitment_id = rd.recruitment_id and candidate_id = ? order by candi_application_deatils_id desc limit 1");
			pst.setInt(1, uF.parseToInt(candidateId));
//			pst.setInt(2, uF.parseToInt(candidateId));
			rst=pst.executeQuery();
//			System.out.println("pst ===> " + pst);
			while(rst.next()){
				hmLastJobData.put("JOB_NAME", rst.getString("job_title"));
				hmLastJobData.put("LAST_APPLIED_DATE", uF.getDateFormat(rst.getString("application_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rst.close();
			pst.close();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
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
		return hmLastJobData;
	}

private void getSelectedFilter(UtilityFunctions uF) {
		
	Map<String,String> hmFilter=new HashMap<String, String>();
	List<String> alFilter = new ArrayList<String>();

//	System.out.println("getF_service()----"+getF_service());
	alFilter.add("ORGANISATION");
	if(getF_org()!=null)  {
		String strOrg="";
		int k=0;
		for(int i=0;organisationList!=null && i<organisationList.size();i++){
			if(getF_org().equals(organisationList.get(i).getOrgId())) {
				if(k==0) {
					strOrg=organisationList.get(i).getOrgName();
				} else {
					strOrg+=", "+organisationList.get(i).getOrgName();
				}
				k++;
			}
		}
		if(strOrg!=null && !strOrg.equals("")) {
			hmFilter.put("ORGANISATION", strOrg);
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
	} else {
		hmFilter.put("ORGANISATION", "All Organisation");
	}
	
	

	alFilter.add("LOCATION");
	if(getF_wlocation()!=null) {
		String strLocation="";
		int k=0;
		for(int i=0;workList!=null && i<workList.size();i++) {
			for(int j=0;j<getF_wlocation().length;j++) {
				if(getF_wlocation()[j].equals(workList.get(i).getwLocationId())) {
					if(k==0) {
						strLocation=workList.get(i).getwLocationName();
					} else {
						strLocation+=", "+workList.get(i).getwLocationName();
					}
					k++;
				}
			}
		}
		if(strLocation!=null && !strLocation.equals("")) {
			hmFilter.put("LOCATION", strLocation);
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
	} else {
		hmFilter.put("LOCATION", "All Locations");
	}
	
	alFilter.add("EDUCATION");
	if(getStrMinEducation()!=null) {
		String strEdu="";
		int k=0;
		for(int i=0;eduList!=null && i<eduList.size();i++) {
			for(int j=0;j<getStrMinEducation().length;j++) {
				if(getStrMinEducation()[j].equals(eduList.get(i).getEduId())) {
					if(k==0) {
						strEdu=eduList.get(i).getEduName();
					} else {
						strEdu+=", "+eduList.get(i).getEduName();
					}
					k++;
				}
			}
		}
		if(strEdu!=null && !strEdu.equals("")) {
			hmFilter.put("EDUCATION", strEdu);
		} else {
			hmFilter.put("EDUCATION", "All Educations");
		}
	} else {
		hmFilter.put("EDUCATION", "All Educations");
	}
	
	alFilter.add("SKILL");
	if(getStrSkills()!=null) {
		String strSkills="";
		int k=0;
		for(int i=0;skillsList!=null && i<skillsList.size();i++) {
			for(int j=0;j<getStrSkills().length;j++) {
				if(getStrSkills()[j].equals(skillsList.get(i).getSkillsId())) {
					if(k==0) {
						strSkills=skillsList.get(i).getSkillsName();
					} else {
						strSkills+=", "+skillsList.get(i).getSkillsName();
					}
					k++;
				}
			}
		}
		if(strSkills!=null && !strSkills.equals("")) {
			hmFilter.put("SKILL", strSkills);
		} else {
			hmFilter.put("SKILL", "All Skills");
		}
	} else {
		hmFilter.put("SKILL", "All Skills");
	}
	
	alFilter.add("EXPERIENCE");
	if(getStrExperience()!=null) {
		String strExp="";
		int k=0;
		for(int i=0;expYearsList!=null && i<expYearsList.size();i++) {
			for(int j=0;j<getStrExperience().length;j++) {
				if(getStrExperience()[j].equals(expYearsList.get(i).getExpYearsId())) {
					if(k==0) {
						strExp=expYearsList.get(i).getExpYearsName();
					} else {
						strExp+=", "+expYearsList.get(i).getExpYearsName();
					}
					k++;
				}
			}
		}
		if(strExp!=null && !strExp.equals("")) {
			hmFilter.put("EXPERIENCE", strExp);
		} else {
			hmFilter.put("EXPERIENCE", "All EXPERIENCE");
		}
	} else {
		hmFilter.put("EXPERIENCE", "All EXPERIENCE");
	}
	
	alFilter.add("STATUS");
	if(getCheckStatus_reportfilter()!=null){
		String strStatus="";
		int k=0;
		for(int j=0;j<getCheckStatus_reportfilter().length;j++) {
			if(getCheckStatus_reportfilter()[j].equals("1")) {
				if(k==0) {
					strStatus="Approved";
				} else {
					strStatus+=", "+"Approved";
				}
				k++;
			} else if(getCheckStatus_reportfilter()[j].equals("0")) {
				if(k==0) {
					strStatus="Pending";
				} else {
					strStatus+=", "+"Pending";
				}
				k++;
			} else if(getCheckStatus_reportfilter()[j].equals("-1")) {
				if(k==0) {
					strStatus="Rejected";
				} else {
					strStatus+=", "+"Rejected";
				}
				k++;
			}
		}
		if(strStatus!=null && !strStatus.equals("")){
			hmFilter.put("STATUS", strStatus);
		}else{
			hmFilter.put("STATUS", "All Status");
		}
	}else{
		hmFilter.put("STATUS", "All Status");
	}
	
	alFilter.add("MONTH");
	if(getStrMonth()!=null)  {
		String strMonth="";
		int k=0;
		for(int i=0;monthList!=null && i<monthList.size();i++){
			if(getStrMonth().equals(monthList.get(i).getMonthId())) {
				if(k==0) {
					strMonth=monthList.get(i).getMonthName();
				} else {
					strMonth+=", "+monthList.get(i).getMonthName();
				}
				k++;
			}
		}
		if(strMonth!=null && !strMonth.equals("")) {
			hmFilter.put("MONTH", strMonth);
		} else {
			hmFilter.put("MONTH", "All Months");
		}
		
	} else {
		hmFilter.put("MONTH", "All Months");
	}
	
	alFilter.add("YEAR");
	if(getStrYear()!=null)  {
		String strYear="";
		int k=0;
		for(int i=0;yearList!=null && i<yearList.size();i++){
			if(getStrYear().equals(yearList.get(i).getYearsID())) {
				if(k==0) {
					strYear=yearList.get(i).getYearsName();
				} else {
					strYear+=", "+yearList.get(i).getYearsName();
				}
				k++;
			}
		}
		if(strYear!=null && !strYear.equals("")) {
			hmFilter.put("YEAR", strYear);
		} else {
			hmFilter.put("YEAR", "All Years");
		}
		
	} else {
		hmFilter.put("YEAR", "All Years");
	}
	
	String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
	request.setAttribute("selectedFilter", selectedFilter);
}

	

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	

	public String[] getCheckStatus_reportfilter() {
		return checkStatus_reportfilter;
	}

	public void setCheckStatus_reportfilter(String[] checkStatus_reportfilter) {
		this.checkStatus_reportfilter = checkStatus_reportfilter;
	}

	public String getJobcode() {
		return jobcode;
	}

	public void setJobcode(String jobcode) {
		this.jobcode = jobcode;
	}

	 

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}
	
	
	//  Filter variables **************
	
	

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String[] getF_wlocation() {
		return f_wlocation;
	}

	public void setF_wlocation(String[] f_wlocation) {
		this.f_wlocation = f_wlocation;
	}

	public List<FillWLocation> getWorkList() {
		return workList;
	}

	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
	}


	public List<FillEducational> getEduList() {
		return eduList;
	}

	public void setEduList(List<FillEducational> eduList) {
		this.eduList = eduList;
	}

	public List<FillSkills> getSkillsList() {
		return skillsList;
	}

	public void setSkillsList(List<FillSkills> skillsList) {
		this.skillsList = skillsList;
	}

	public String[] getStrExperience() {
		return strExperience;
	}

	public void setStrExperience(String[] strExperience) {
		this.strExperience = strExperience;
	}

	public String[] getStrMinEducation() {
		return strMinEducation;
	}

	public void setStrMinEducation(String[] strMinEducation) {
		this.strMinEducation = strMinEducation;
	}

	public String[] getStrSkills() {
		return strSkills;
	}

	public void setStrSkills(String[] strSkills) {
		this.strSkills = strSkills;
	}

	public List<FillText> getAlEmail() {
		return alEmail;
	}

	public void setAlEmail(List<FillText> alEmail) {
		this.alEmail = alEmail;
	}

	public List<FillText> getAlPanCard() {
		return alPanCard;
	}

	public void setAlPanCard(List<FillText> alPanCard) {
		this.alPanCard = alPanCard;
	}

	public String getF_pancard() {
		return f_pancard;
	}

	public void setF_pancard(String f_pancard) {
		this.f_pancard = f_pancard;
	}

	public String getF_email() {
		return f_email;
	}

	public void setF_email(String f_email) {
		this.f_email = f_email;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}


	public String getCurrCTC() {
		return currCTC;
	}

	public void setCurrCTC(String currCTC) {
		this.currCTC = currCTC;
	}

	public String getExpectedCTC() {
		return expectedCTC;
	}

	public void setExpectedCTC(String expectedCTC) {
		this.expectedCTC = expectedCTC;
	}

	public String getNoticePeriod() {
		return noticePeriod;
	}

	public void setNoticePeriod(String noticePeriod) {
		this.noticePeriod = noticePeriod;
	}

	public String getMinCurrCTC() {
		return minCurrCTC;
	}

	public void setMinCurrCTC(String minCurrCTC) {
		this.minCurrCTC = minCurrCTC;
	}

	public String getMinExpectedCTC() {
		return minExpectedCTC;
	}

	public void setMinExpectedCTC(String minExpectedCTC) {
		this.minExpectedCTC = minExpectedCTC;
	}

	public String getMinNoticePeriod() {
		return minNoticePeriod;
	}

	public void setMinNoticePeriod(String minNoticePeriod) {
		this.minNoticePeriod = minNoticePeriod;
	}

	public String getMaxCurrCTC() {
		return maxCurrCTC;
	}

	public void setMaxCurrCTC(String maxCurrCTC) {
		this.maxCurrCTC = maxCurrCTC;
	}

	public String getMaxExpectedCTC() {
		return maxExpectedCTC;
	}

	public void setMaxExpectedCTC(String maxExpectedCTC) {
		this.maxExpectedCTC = maxExpectedCTC;
	}

	public String getMaxNoticePeriod() {
		return maxNoticePeriod;
	}

	public void setMaxNoticePeriod(String maxNoticePeriod) {
		this.maxNoticePeriod = maxNoticePeriod;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public List<FillYears> getYearList() {
		return yearList;
	}

	public void setYearList(List<FillYears> yearList) {
		this.yearList = yearList;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrYear() {
		return strYear;
	}

	public void setStrYear(String strYear) {
		this.strYear = strYear;
	}


	public List<FillExpYears> getExpYearsList() {
		return expYearsList;
	}


	public void setExpYearsList(List<FillExpYears> expYearsList) {
		this.expYearsList = expYearsList;
	}


	public List<FillStatus> getStatusList() {
		return statusList;
	}


	public void setStatusList(List<FillStatus> statusList) {
		this.statusList = statusList;
	}

	
}