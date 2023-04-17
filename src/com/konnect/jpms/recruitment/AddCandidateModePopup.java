package com.konnect.jpms.recruitment;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CandidateNotifications;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddCandidateModePopup extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AddCandidateModePopup.class);
	
	String strUserType;
	String strSessionEmpId;
	HttpSession session;
	private String fname;
	private String lname;
	private String email;
	private String message;
	private String notification;
	private int empId;
	String mailID;
	String fromPage;
	String recruitId;
		
	CommonFunctions CF;
	
	public String execute() throws Exception {
		
		session = request.getSession();		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/recruitment/AddCandidateModePopup.jsp");
		request.setAttribute(TITLE, "Add Candidate Mode");
	//	request.setAttribute("jobid", getJobid());
		UtilityFunctions uF = new UtilityFunctions();
		
		if(getMailID() != null && !getMailID().equals("")){
			checkCandiMailId();
			return "checkmail";
		}
		
		if(getNotification()!=null && getNotification().equals("reminder")){
			sendReminderNotif();
			return "preport";
		}
		
		if(getNotification()!=null && getNotification().equals("signup")) {
			sendSingupNotif();
			if(getFromPage() != null && getFromPage().equals("CR")) {
				return "crsuccess";
			} else {
				return SUCCESS;
			}
		}
		
		if(getEmpId()!=0) {
			approveEmployee();
			return REPORT;
		}
		
//		if(getF_org()==null){
//			setF_org((String)session.getAttribute(ORGID));
//		}
//		
//		organisationList=new FillOrganisation(request).fillOrganisation();
//		
//		if(getF_org()!=null){
//			workList=new FillWLocation(request).fillWLocation(getF_org());
//		} else {
//			workList=new FillWLocation(request).fillWLocation();
//		}
//		eduList=new FillEducational(request).fillEducationalQual();
//		skillsList=new FillSkills(request).fillSkills();
		
//		viewEmployee(uF);
		return LOAD;
	}

	
private void checkCandiMailId() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from candidate_personal_details where emp_email = ?");
			pst.setString(1, getMailID());
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("pst ===> "+pst);
			boolean isEmailExist = false;
			while(rs.next()){
				isEmailExist = true;
			}
			rs.close();
			pst.close();
			
			if(isEmailExist){
				setMessage("<p style=\"color: green\">"+"Entered email-id already exists in the system, please use different email."+"</p>");
			}else{
				setMessage("");
			}
//			System.out.println("getMessage()===> "+getMessage());
//			System.out.println("message ===> "+message);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}
	
	
	private void sendReminderNotif() {
		
		Connection con = null;
		PreparedStatement pst =null;
		Database db = new Database();
		db.setRequest(request); 
		UtilityFunctions uF = new UtilityFunctions();
		
		String username = "";
		String password = ""; 
		
		try {
		
			con = db.makeConnection(con);
			Map<String, String> hmUsers = CF.getEmpIdUserNameMap(con);
			if(hmUsers.get(getEmpId()+"")!=null) {
				
				username = ((String)hmUsers.get(getEmpId()+"")).split(" ")[0];
				password = ((String)hmUsers.get(getEmpId()+"")).split(" ")[1];
				
			}
			
			/*Notifications nF = new Notifications(0, CF);
			nF.setStrEmailTo(getEmail());
			nF.setStrEmpFname(getFname());
			nF.setStrEmpLname(getLname());
			
			nF.setStrEmailSubject("Reminder to fill up the Employee induction Form.");
			nF.setStrEmailBody("You have not filled up the employee induction Form completely. Login Crenditials are <br> " +
					"username:"+ username +" & password: "+password );
			nF.sendNotifications();
*/
			pst = con.prepareStatement("INSERT INTO reminder_details(_date, emp_id, reminder_type) values(?,?,?)");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, getEmpId());
			pst.setString(3, "T");
			log.debug("pst===>"+pst);
			pst.execute();
			pst.close();
			
			setMessage("Reminder Sent To the Employee "+getFname()+ " "+getLname());
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void approveEmployee() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE candidate_personal_details SET approved_flag = ? and is_alive = ? WHERE emp_per_id = ?");
			pst.setBoolean(1, true);
			pst.setBoolean(2, true);
			pst.setInt(3, getEmpId());
			log.debug("pst===>"+pst);
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
			
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
			
		}
		
	}

	private void sendSingupNotif() {
		
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		int empId = 0;
		String username = "";
		String password = "";
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from candidate_personal_details where emp_email = ?");
			pst.setString(1, getEmail());
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			boolean isEmailExist = false;
			while(rs.next()){
				isEmailExist = true;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from recruitment_details where recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String jobCode = null;
			while(rs.next()){
				jobCode = rs.getString("job_code");
			}
			rs.close();
			pst.close();
//			if(isEmailExist){
//				setMessage("Entered email-id already exists in the system, please use different email.");
//				return;
//			}
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
			
			AddCandidate aE = new AddCandidate();
			aE.setServletRequest(request);
			aE.CF = CF;
			aE.setEmpFname(getFname());
			aE.setEmpLname(getLname());
			aE.setEmpEmail(getEmail());
			aE.setJobcode(jobCode);
			aE.setEmpAddress1("");
			aE.setRecruitId(getRecruitId());
			aE.setApprovedFlag(false);
			aE.setOrg_id((String)session.getAttribute(ORGID));
//			System.out.println("con ===> "+con);
//			System.out.println("CF ===> "+CF);
//			System.out.println(" Emp ID ===> " + aE.insertCandidatePersonalDetails(con, uF, CF));
			empId = aE.insertCandidatePersonalDetails(con, uF, CF);
//			System.out.println("empId ===> "+empId);
			
			
			String strSessionId = session.getId();
//			strSessionId = URLEncoder.encode(strSessionId);
			pst = con.prepareStatement("update candidate_personal_details set session_id =?, _timestamp=?, added_by=? where emp_per_id=?");
			pst.setString(1, strSessionId);
			pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(3, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(4, empId);
			pst.execute();
			pst.close();
			
//			System.out.println("pst ===> "+pst);
			
			String strDomain = request.getServerName().split("\\.")[0];	
			CandidateNotifications nF = new CandidateNotifications(N_NEW_CADIDATE_ADD, CF); 
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrEmpId(empId+"");
			nF.setStrRecruitmentId(getRecruitId());
			nF.setStrAddCandiLink("?CandidateId="+empId+"&sessionId="+strSessionId+"&recruitId="+getRecruitId()+"&operation=U&step=1&mode=profile&type=type&org_id="+session.getAttribute(ORGID));
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
//			System.out.println("getEmail() ===> "+getEmail());
			nF.setStrEmailTo(getEmail());
			nF.setStrCandiFname(getFname());
			nF.setStrCandiLname(getLname());
			nF.setStrLegalEntityName(CF.getOrgNameById(con, (String)session.getAttribute(ORGID)));
//			nF.setStrEmailSubject("Please fill up the Employee Form.");
			nF.setHmFeatureStatus(hmFeatureStatus);
			nF.setHmFeatureUserTypeId(hmFeatureUserTypeId);
			
			nF.sendNotifications();
			setMessage("Email has been sent to the Employee with induction form link.");
			setFname("");
			setLname("");
			setEmail("");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}


//	public String getUserName() {
//		
//		int size=getFname().length();
//		Map<String,String> userPresent= CF.getUsersMap();
//		
//		String createUserName = getFname().charAt(0) + getLname();
//		
//				
//		if(CF.getStrUserNameFormat()!=null && CF.getStrUserNameFormat().equalsIgnoreCase(FIRSTNAME_DOT_LASTNAME)){
//			createUserName = ((getFname()!=null)?getFname().toLowerCase():"") +"."+((getLname()!=null)?getLname().toLowerCase():"");
//			createUserName = createUserName.replace(" ", "");
//			
//			
//			for(int i=0;i<=userPresent.size();i++){
//				if((userPresent.values()).contains(createUserName)){
//					createUserName = getFname().toLowerCase() +"."+ getLname().toLowerCase()+ (i + 1);
//				}
//			}	
//		}else{
//			for(int i=1;i<=size;i++){
//				if((userPresent.values()).contains(createUserName)){
//					createUserName = getFname().substring(0, i) + getLname();
//				}
//			}
//			
//			for(int i=0;i<=userPresent.size();i++){
//				
//				if((userPresent.values()).contains(createUserName)){
//					if(size<=i) {
//						createUserName = getFname() + getLname() + (i -size + 1);
//					}
//				}
//			}
//		}
//		
//		log.debug("createUserName===>"+createUserName);
//		
//		return createUserName;
//		
//	}

//Filter variables **************

//String f_org;
//String f_wlocation;
//String strExperience;
//String checkStatus_reportfilter;
//
//List<FillOrganisation> organisationList;
//List<FillWLocation> workList;
//List<FillEducational> eduList;
//List<FillSkills> skillsList;
//
//String[] strMinEducation;
//String[] strSkills;


//public String viewEmployee(UtilityFunctions uF) {
//
//	Connection con = null;
//	PreparedStatement pst = null;
//	ResultSet rst = null;
//	Database db = new Database();
//	db.setRequest(request);
//	
//	try {
//		con = db.makeConnection(con);
//
//		int nCount = 0;
//
//		Map<String, String> hmreq_designation_name = new HashMap<String, String>();
//
//		Map<String, String> hmreq_job_location = new HashMap<String, String>();
//		String strRequirement = null;
//		pst = con.prepareStatement("Select job_code, no_position,designation_name,wlocation_name,recruitment_id from recruitment_details join designation_details using(designation_id) join work_location_info on(wlocation=wlocation_id) where job_approval_status=1 and recruitment_id=? ");
//		pst.setInt(1, uF.parseToInt(getRecruitId()));
//		rst = pst.executeQuery();
//
//		while (rst.next()) {
//
//			hmreq_designation_name.put(rst.getString("recruitment_id"), rst.getString("designation_name"));
//			hmreq_job_location.put(rst.getString("recruitment_id"), rst.getString("wlocation_name"));
//			strRequirement = rst.getString("no_position");
//		}
//		
//		request.setAttribute("recruitId", getRecruitId());
//		request.setAttribute("hmreq_designation_name", hmreq_designation_name);
//		request.setAttribute("hmreq_job_location", hmreq_job_location);
//		request.setAttribute("strRequirement", strRequirement);
//		
//		StringBuilder sbSelectCandiIds1 = new StringBuilder();
//		StringBuilder sbSelectCandiIds2 = new StringBuilder();
//		StringBuilder sbSelectCandiIds3 = new StringBuilder();
//		StringBuilder sbQuery1 = new StringBuilder();
//		List<String> lstSkills = new ArrayList<String>();
//		List<String> lstEducation = new ArrayList<String>();
//		StringBuilder sbRecruitID = new StringBuilder();
//		
//		if (strSkills != null) {
//			for (int i = 0; i < strSkills.length; i++) {
//				if (!strSkills[i].equals("")) {
//						lstSkills.add(strSkills[i]);
//				}
//			}
//		}
////		System.out.println("sbSkills ===> "+sbSkills);
//		if((getF_org() != null && !getF_org().equals("")) ||(getF_wlocation() != null && !getF_wlocation().equals(""))){
//			sbQuery1.append("select recruitment_id from recruitment_details where org_id > 0 ");
//			if(getF_org() != null && !getF_org().equals("")){
//			sbQuery1.append(" and org_id = "+getF_org()+" ");	
//			}
//			if(getF_wlocation() != null && !getF_wlocation().equals("")){
//				sbQuery1.append(" and wlocation = "+getF_wlocation()+" ");	
//				}
//			pst = con.prepareStatement(sbQuery1.toString());
//			rst = pst.executeQuery();
////			System.out.println("sbSkills pst===> "+pst);
//			while (rst.next()) {
//				if(sbRecruitID == null || sbRecruitID.toString().equals("")){
//					sbRecruitID.append(rst.getString("recruitment_id"));
//				}else{
//					sbRecruitID.append(","+rst.getString("recruitment_id"));
//				}
//			}
//			if(sbRecruitID == null || sbRecruitID.toString().equals("")){
//				sbRecruitID.append("0");
//			}
//		}
////		System.out.println("sbRecruitID ===> "+sbRecruitID);
//		
//		if((sbRecruitID != null && !sbRecruitID.toString().equals("")) ||(getCheckStatus_reportfilter() != null && !getCheckStatus_reportfilter().equals("-2"))){
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select distinct(candidate_id) from candidate_application_details where recruitment_id > 0 ");
//			if(sbRecruitID != null && !sbRecruitID.toString().equals("")){
//				sbQuery.append(" and recruitment_id in("+sbRecruitID.toString()+") ");
//			}
//			if(getCheckStatus_reportfilter() != null && !getCheckStatus_reportfilter().equals("-2")){
//				sbQuery.append(" and application_status = "+getCheckStatus_reportfilter()+" ");
//			}
////			pst = con.prepareStatement("select distinct(candidate_id) from candidate_application_details where recruitment_id in("+sbRecruitID.toString()+")");
//			pst = con.prepareStatement(sbQuery.toString());
//			rst = pst.executeQuery();
////			System.out.println("RecruitID pst===> "+pst);
//			while (rst.next()) {
//				if(sbSelectCandiIds1 == null || sbSelectCandiIds1.toString().equals("")){
//					sbSelectCandiIds1.append(rst.getString("candidate_id"));
//				}else{
//					sbSelectCandiIds1.append(","+rst.getString("candidate_id"));
//				}
//			}
//				if(sbSelectCandiIds1 == null || sbSelectCandiIds1.toString().equals("")){
//					sbSelectCandiIds1.append("0");
//				}else{
//					sbSelectCandiIds1.append(",0");
//				}
//		}
//		
//		int skillCnt =0;
//		for(int i=0; lstSkills != null && !lstSkills.isEmpty() && i < lstSkills.size(); i++){
//			pst = con.prepareStatement("select skills_name,emp_id from candidate_skills_description where skills_name in('"+lstSkills.get(i)+"')");
//			rst = pst.executeQuery();
////			System.out.println("sbSkills pst===> "+pst);
//			while (rst.next()) {
//				if(sbSelectCandiIds2 == null || sbSelectCandiIds2.toString().equals("")){
//					sbSelectCandiIds2.append(rst.getString("emp_id"));
//				}else{
//					sbSelectCandiIds2.append(","+rst.getString("emp_id"));
//				}
//				skillCnt++;
//			}	
//		}
//		if(lstSkills != null && lstSkills.size() > 0 && skillCnt == 0){
//			if(sbSelectCandiIds2 == null || sbSelectCandiIds2.toString().equals("")){
//				sbSelectCandiIds2.append("0");
//			}else{
//				sbSelectCandiIds2.append(",0");
//			}
//		}
//		
//		if (strMinEducation != null) {
//			for (int i = 0; i < strMinEducation.length; i++) {
//				if (!strMinEducation[i].equals("")) {
//						lstEducation.add(strMinEducation[i]);
//				}
//			}
//		}
////		System.out.println("sbEducation ===> "+sbEducation);
//		int eduCnt=0;
//		for(int i=0; lstEducation != null && !lstEducation.isEmpty() && i < lstEducation.size(); i++){
//			pst = con.prepareStatement("select degree_name,emp_id from candidate_education_details where degree_name in('"+lstEducation.get(i)+"')");
//			rst = pst.executeQuery();
////			System.out.println("sbEducation pst===> "+pst);
//			while (rst.next()) {
//				if(sbSelectCandiIds3 == null || sbSelectCandiIds3.toString().equals("")){
//					sbSelectCandiIds3.append(rst.getString("emp_id"));
//				}else{
//					sbSelectCandiIds3.append(","+rst.getString("emp_id"));
//				}
//				eduCnt++;
//			}
//		}
//		if(lstEducation != null && lstEducation.size() > 0 && eduCnt == 0){
//			if(sbSelectCandiIds3 == null || sbSelectCandiIds3.toString().equals("")){
//				sbSelectCandiIds3.append("0");
//			}else{
//				sbSelectCandiIds3.append(",0");
//			}
//		}
//		
//		StringBuilder sbFinalCadiIDS = new StringBuilder();
//		sbFinalCadiIDS.append(sbSelectCandiIds1);
//		sbFinalCadiIDS.append(sbSelectCandiIds2);
//		sbFinalCadiIDS.append(sbSelectCandiIds3);
////		System.out.println("sbFinalCadiIDS ===> "+sbFinalCadiIDS.toString());
//		List<String> alInner;
//		List<List<String>> al = new ArrayList<List<String>>();
//		
//		StringBuilder query=new StringBuilder("select years,emp_per_id,emp_fname,emp_lname,emp_date_of_birth,emp_city_id," +
//				"emp_email,emp_pan_no from " +
//				"candidate_personal_details cpd left join (select sum(to_date-from_date)/365 as years ,emp_id from " +
//				"candidate_prev_employment cpe group by emp_id) as a on cpd.emp_per_id = a.emp_id where cpd.emp_per_id>0 ");
//				
//		if(sbFinalCadiIDS != null && !sbFinalCadiIDS.toString().equals("")){
//			query.append(" and  emp_per_id in("+sbFinalCadiIDS.toString()+") ");
//		}
//		
//		if(getStrExperience()!=null && uF.parseToInt(getStrExperience())!=0){
//		    if(uF.parseToInt(getStrExperience())==1)
//		    	query.append(" and years<=1 and years>=0");
//		    else if(uF.parseToInt(getStrExperience())==2)
//		    	query.append(" and years<2 and years>=1");
//		    else if(uF.parseToInt(getStrExperience())==3)
//		    	query.append(" and years<5 and years>=2");
//		    	else if(uF.parseToInt(getStrExperience())==4)
//		    		query.append(" and years=10 and years>=5");
//		    		else if(uF.parseToInt(getStrExperience())==5)
//		    			query.append(" and years>=10");
//		}
//		
//		pst = con.prepareStatement(query.toString());
//		rst = pst.executeQuery();
////		System.out.println("pst ===> "+pst);
//		StringBuilder sbCandidateIds = new StringBuilder(); 
//		String oldEmp =null;
//		while (rst.next()) {
//			
//			if(oldEmp==null || (oldEmp!=null && !oldEmp.equals(rst.getString("emp_per_id")))){
//				 alInner=new ArrayList<String>();	
//
//			if(sbCandidateIds.length()==0){
//				sbCandidateIds.append(rst.getString("emp_per_id"));
//			}else{
//				sbCandidateIds.append(","+rst.getString("emp_per_id"));
//			}
//			alInner.add("");
//			alInner.add("");
//			alInner.add(rst.getString("emp_per_id"));
//			
//			alInner.add(""); 
//			alInner.add(rst.getString("emp_fname") + " " + rst.getString("emp_lname"));
//			
//			alInner.add(""); 
//			alInner.add(""); 
//			alInner.add("<a class=\"factsheet\" href=\"CandidateMyProfile.action?CandID=" +rst.getString("emp_per_id")+ "&recruitId=" + getRecruitId() + "\" > </a>");
//			alInner.add("");
//			
//			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))) {
//				
//				StringBuilder sbApproveDeny = new StringBuilder();
//
//					sbApproveDeny.append("<div id=\"myDivM" + nCount + "\" > ");
////					System.out.println(" Candi ID ===> "+rst.getString("emp_per_id"));
//					sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"getCandiApplicationsDetailsPopup('" + rst.getString("emp_per_id") + "');\">" + " <img src=\"images1/edit.png\" title=\"Applications Details\" /></a> ");
//					sbApproveDeny.append("</div>");
//					alInner.add(sbApproveDeny.toString());
//				
//			} else{
//				//MANAGERempId
//				StringBuilder sbApproveDeny = new StringBuilder();
//				
//				sbApproveDeny.append("<div id=\"myDivM" + nCount + "\" > ");
//				sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"getCandiApplicationsDetailsPopup('" + rst.getString("emp_per_id") + "');\">" + " <img src=\"images1/edit.png\" title=\"Applications Details\" /></a> ");
//				sbApproveDeny.append("</div>");
//				alInner.add(sbApproveDeny.toString());
//			}
//			
//			
//			if(rst.getString("emp_date_of_birth") != null && !rst.getString("emp_date_of_birth").equals("")){
//				alInner.add(uF.getTimeDurationBetweenDates(rst.getString("emp_date_of_birth"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF, uF, request, true, true, false));
//			}else{
//				alInner.add("-");
//			}
//			alInner.add(uF.showData(rst.getString("emp_city_id"),"-"));
//					
//			al.add(alInner);
//			oldEmp=rst.getString("emp_per_id");
//			}
//			
//		}
//		  
//		request.setAttribute("reportList", al);
//		
//		Map<String,String> hmCandidateExperience=new HashMap<String, String>();
//		Map<String, String> hmEducationDetails = new HashMap<String, String>();
//		Map<String, String> hmSkillDetails = new HashMap<String, String>();
//		
//		
//		if(sbCandidateIds.length()>1){
//		
//		pst = con.prepareStatement("select * from candidate_education_details where emp_id in ("+sbCandidateIds.toString()+") order by emp_id");
//
//		rst = pst.executeQuery();
//		String strCandidateIdOld = null;
//		String strCandidateIdNew = null;
//		StringBuilder sbContainer = new StringBuilder();
//
//		while(rst.next()){
//				strCandidateIdNew = rst.getString("emp_id");
//				if (strCandidateIdNew != null && !strCandidateIdNew.equals(strCandidateIdOld)) {
//					sbContainer.replace(0, sbContainer.length(), "");
//				}
//
//				if (strCandidateIdNew != null && strCandidateIdNew.equals(strCandidateIdOld))
//					sbContainer.append(", " + rst.getString("degree_name"));
//				else
//					sbContainer.append(rst.getString("degree_name"));
//
//				hmEducationDetails.put(strCandidateIdNew,sbContainer.toString());
//				strCandidateIdOld = strCandidateIdNew;
//		}
//		
//
//		pst = con.prepareStatement("select * from candidate_skills_description where emp_id in ("+sbCandidateIds.toString()+") order by emp_id");
//		rst = pst.executeQuery();
//		strCandidateIdOld = null;
//		strCandidateIdNew = null;
//		sbContainer.replace(0, sbContainer.length(), "");
//	
//		while(rst.next()){
//			strCandidateIdNew = rst.getString("emp_id");
//			if (strCandidateIdNew != null && !strCandidateIdNew.equals(strCandidateIdOld)) {
//				sbContainer.replace(0, sbContainer.length(), "");
//			}
//			
//			if (strCandidateIdNew != null && strCandidateIdNew.equals(strCandidateIdOld))
//				sbContainer.append(", " + rst.getString("skills_name"));
//			else
//				sbContainer.append(rst.getString("skills_name"));
//			
//			hmSkillDetails.put(strCandidateIdNew, sbContainer.toString());
//			strCandidateIdOld = strCandidateIdNew;
//		}
//	
//
//		// Logic for multiple experience
//		
//		pst = con.prepareStatement("select * from candidate_prev_employment where emp_id in ("+sbCandidateIds.toString()+") order by emp_id");
//		rst=pst.executeQuery();
//		strCandidateIdOld = null;
//		strCandidateIdNew = null;
//		int noyear = 0,nomonth = 0,nodays = 0;
//		while(rst.next())
//		{
//			strCandidateIdNew = rst.getString("emp_id");
//			if(strCandidateIdNew!=null && !strCandidateIdNew.equals(strCandidateIdOld)){
//			
//				noyear=0;
//				nomonth=0;
//				nodays=0;
//			}
//				String datedif=uF.dateDifference(rst.getString("from_date"),DBDATE , rst.getString("to_date"), DBDATE);
//				long datediff=uF.parseToLong(datedif);		    		 
//		    	noyear+=(int) (datediff/365);
//		    	nomonth+=(int) ((datediff%365)/30);
//		    	nodays+=(int) ((datediff%365)%30);
//		     
//		    	if(nodays>30){
//		    		nomonth=nomonth+1;
//		    	}
//		    	if(nomonth>12){
//		    		nomonth=nomonth-12;
//		    		noyear=noyear+1;
//		    	}
//		    	hmCandidateExperience.put(rst.getString("emp_id"),""+noyear+" Year "+nomonth+" months "); 
//		    	strCandidateIdOld = strCandidateIdNew;
//		}
//		
//		}
//		request.setAttribute("hmExperienceDetails", hmCandidateExperience);
//		
//		request.setAttribute("hmEducationDetails", hmEducationDetails);
//		request.setAttribute("hmSkillDetails", hmSkillDetails);
//	} catch (Exception e) {
//		e.printStackTrace();
//		log.error(e.getClass() + ": " + e.getMessage(), e);
//	} finally {
//		db.closeConnection(con);
//		db.closeStatements(pst);
//		db.closeResultSet(rst);
//	}
//	return SUCCESS;

//}


	private HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	//  for adding candidate for a particular job
	
	
	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getEmpId() {
		return empId;
	}

	public void setEmpId(int empId) {
		this.empId = empId;
	}

	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}

	public String getMailID() {
		return mailID;
	}

	public void setMailID(String mailID) {
		this.mailID = mailID;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

}
