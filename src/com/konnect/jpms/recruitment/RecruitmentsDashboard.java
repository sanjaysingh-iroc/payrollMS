package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

public class RecruitmentsDashboard implements ServletRequestAware, IStatements {
	HttpSession session;
	CommonFunctions CF;
	
	String strUserType = null;
	String strSessionEmpId = null;
	String strBaseUserType = null;

	// String updateJobProfile;
	private String addInformation;
	private String minEducation;
	private String maxMonth;
	private String maxYear;
	private String minMonth;
	private String minYear;
	private String candidateProfile;
	private String jobDescription;
	private String view;
	private String fromPage;
	private String strAction = null;
	public String execute() {
		session = request.getSession();
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);//Created By Dattatray 15-06-2022
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) 
			return "login";
		strUserType = (String) session.getAttribute(USERTYPE);
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/recruitment/RecruitmentsDashboard.jsp");
		request.setAttribute(TITLE, "Recruitment Dashboard");
		//Created By Dattatray 15-06-2022
		strAction = request.getServletPath();
		if(strAction!=null) {
			strAction = strAction.replace("/","");
		}
		
		if(strUserType==null || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(RECRUITER)
			&& !strUserType.equalsIgnoreCase(HRMANAGER) && !strUserType.equalsIgnoreCase(CEO))) {
			loadPageVisitAuditTrail();
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
//		System.out.println("getFromPage==>"+getFromPage());
		if(getFromPage() == null || getFromPage().equals("") || getFromPage().equalsIgnoreCase("null")) {
			StringBuilder sbpageTitleNaviTrail = new StringBuilder();
			sbpageTitleNaviTrail.append("<li><i class=\"fa fa-user-circle-o\"></i><a href=\"RecruitmentsDashboard.action\" style=\"color: #3c8dbc;\"> Recruitment</a></li>" +
				"<li class=\"active\">Recruitment Dashboard</li>");
			request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		}
		
		fillJobreportStatsData();
		fillOpenJobreportStatsData();

		preparejobreport();
		getApplicationCountOfRecruitmentID();
		
		System.out.println("hii im in RecruitmentsDashboard class and in execute method--");
		getLiveJobCounter();
		
		return LOAD;
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
	
	public void getLiveJobCounter() {
		System.out.println("in getLiveJobCounter function");
		Connection con=null;
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst=null;
		ResultSet rst=null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			con = db.makeConnection(con);
			pst = con.prepareStatement("select count(*) as count from recruitment_details where job_approval_status=1 and close_job_status = false");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String strLiveJobs = null;
			while(rst.next()) {
				strLiveJobs = rst.getString("count");
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select count(*) as count from recruitment_details where job_approval_status=0 and status =1 and close_job_status = false");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String strApprovalPendingJobs = null;
			while(rst.next()) {
				strApprovalPendingJobs = rst.getString("count");
			}
			rst.close();
			pst.close();
			System.out.println("strLiveJobs in class"+strLiveJobs);
			System.out.println("strApprovalPendingJobs in class"+strApprovalPendingJobs);

//			System.out.println("hmAppCount ===> "+hmAppCount);
			request.setAttribute("strLiveJobs", strLiveJobs);
			request.setAttribute("strApprovalPendingJobs", strApprovalPendingJobs);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getApplicationCountOfRecruitmentID() {
		Connection con=null;
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst=null;
		ResultSet rst=null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			con=db.makeConnection(con);
			Map<String, String> hmAppCount = new HashMap<String, String>();
			pst=con.prepareStatement("select count(*) as count from candidate_application_details cad, recruitment_details rd where cad.recruitment_id = rd.recruitment_id and close_job_status = false and cad.application_status=0");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				hmAppCount.put("ALL", rst.getString("count"));
			}
			rst.close();
			pst.close();
//			System.out.println("hmAppCount ===> "+hmAppCount);
			request.setAttribute("hmAppCount", hmAppCount);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void preparejobreport() {
		
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		
	    try {
	    	con = db.makeConnection(con);
	    	
	    	Map<String, String> hmScheduledCandidate = new HashMap<String, String>();
	    	Map<String, String> hmUnderProcessCandidate = new HashMap<String, String>();	
		    
	    	StringBuilder sbQue = new StringBuilder();
	    	sbQue.append("select cip.candidate_id, panel_round_id, cip.recruitment_id, status from candidate_interview_panel cip join " +
	    		"candidate_application_details cad on(cip.candidate_id=cad.candidate_id) where candidate_final_status=0 and is_selected = 0 ");
	    	sbQue.append(" group by cip.candidate_id, panel_round_id, cip.recruitment_id, status order by cip.candidate_id, status");
		    pst = con.prepareStatement(sbQue.toString());
		    rst= pst.executeQuery();
	//	    System.out.println("new Date ===> " + new Date());
		    while(rst.next()) {
		    	if(uF.parseToInt(rst.getString("status"))==0) {
		    		int scheduledCandidates = uF.parseToInt(hmScheduledCandidate.get("ALL"));
		    		scheduledCandidates++;
		    		hmScheduledCandidate.put("ALL", scheduledCandidates+"");
		    	}
		    	
		    	if(uF.parseToInt(rst.getString("status"))==1) {
		    		int underProcessCandidates = uF.parseToInt(hmUnderProcessCandidate.get("ALL"));
		    		underProcessCandidates++;
		    		hmUnderProcessCandidate.put("ALL", underProcessCandidates+"");
		    	}
		    }
		    rst.close();
			pst.close();
			request.setAttribute("hmScheduledCandidate", hmScheduledCandidate);
			request.setAttribute("hmUnderProcessCandidate", hmUnderProcessCandidate);
			
		    
		    Map<String,String> hmScheduling = new HashMap<String,String>();
		    sbQue = new StringBuilder();
		    sbQue.append("select count(*) as count from candidate_application_details cad where application_status=2 and candidate_id not in " +
	    		"(select candidate_id from candidate_interview_panel) ");
		    pst=con.prepareStatement(sbQue.toString());
		    rst=pst.executeQuery();
	//	    System.out.println("new Date ===> " + new Date());
		    while(rst.next()) {
		    	hmScheduling.put("ALL", rst.getString("count"));
		    }
		    rst.close();
			pst.close();
			request.setAttribute("hmScheduling", hmScheduling);
			
	        ///Preparing application statuss***************
		
			Map<String, String> applyMp = new HashMap<String, String>();
			Map<String, String> hmCandiRejectFromRound = new HashMap<String, String>();
			sbQue = new StringBuilder();
			sbQue.append("select * from candidate_interview_panel where status=-1 ");
			pst = con.prepareStatement(sbQue.toString());
			rst = pst.executeQuery();
	//		System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				hmCandiRejectFromRound.put(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"), rst.getString("status"));
			}
			rst.close();
			pst.close();
			
			Map<String, String> hmClearRoundCnt = new HashMap<String, String>();
			sbQue = new StringBuilder();
			sbQue.append("select count (distinct(panel_round_id)) as count,recruitment_id,candidate_id from candidate_interview_panel where status=1 ");
//				" and recruitment_id in ("+getRecruitId()+") ");
			sbQue.append(" group by recruitment_id,candidate_id");
			pst = con.prepareStatement(sbQue.toString());
			rst = pst.executeQuery();
	//		System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				hmClearRoundCnt.put(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"), rst.getString("count"));
			}
			rst.close();
			pst.close();
	//		System.out.println("hmClearRoundCnt ===> "+hmClearRoundCnt);
			
			Map<String, String> hmRoundCnt = new HashMap<String, String>();
			sbQue = new StringBuilder();
			sbQue.append("select count (distinct(round_id))as count,recruitment_id from panel_interview_details where recruitment_id>0 " +
				" group by recruitment_id ");
			pst = con.prepareStatement(sbQue.toString());
			rst = pst.executeQuery();
	//		System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				hmRoundCnt.put(rst.getString("recruitment_id"), rst.getString("count"));
			}
			rst.close();
			pst.close();
			
			sbQue = new StringBuilder();
			sbQue.append("select  count(*) as count,recruitment_id,candidate_id from candidate_application_details cad where candidate_id > 0 group by recruitment_id,candidate_id");
			pst = con.prepareStatement(sbQue.toString());				
			int dblTotalApplication = 0;
			rst = pst.executeQuery();
	//		System.out.println("new Date ===> " + new Date());
//			System.out.println("pst ===> "+pst);
			while (rst.next()) {
				dblTotalApplication = uF.parseToInt(applyMp.get("ALL"));
				dblTotalApplication += rst.getInt("count");		            	 
	            applyMp.put("ALL", String.valueOf(dblTotalApplication));
			}
			rst.close();
			pst.close();
			request.setAttribute("applyMp", applyMp);
			
			
			Map<String, String> hmSelectCount = new LinkedHashMap<String, String>();
			Map<String, String> hmFinalCount = new LinkedHashMap<String, String>();
			
			sbQue = new StringBuilder();
			sbQue.append("select cad.recruitment_id,emp_fname,emp_lname,cad.job_code,emp_per_id,cad.candidate_final_status," +
				"emp_image from candidate_personal_details cpd, candidate_application_details cad where cpd.emp_per_id = cad.candidate_id " +
				" and cad.application_status=2 and not cad.candidate_final_status=-1 ");
				pst = con.prepareStatement(sbQue.toString());
//				System.out.println("pst ===>> " + pst);
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
				int selectCnt=0, finalCnt=0;
				while (rst.next()) {
		
					if (rst.getInt("candidate_final_status") == 0 && (hmCandiRejectFromRound == null || 
						hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")) == null || 
						!hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")).equals("-1"))
						&& hmClearRoundCnt != null && hmRoundCnt != null &&
						hmClearRoundCnt.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")) != null && hmRoundCnt.get(rst.getString("recruitment_id")) != null && 
						hmClearRoundCnt.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")).equals(hmRoundCnt.get(rst.getString("recruitment_id")))) {
						finalCnt = uF.parseToInt(hmFinalCount.get("ALL"));
						finalCnt++;
						hmFinalCount.put("ALL", String.valueOf(finalCnt));
						
					} else if (rst.getInt("candidate_final_status") == 0 && (hmCandiRejectFromRound == null || 
						hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")) == null || !hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")).equals("-1"))){
						selectCnt = uF.parseToInt(hmSelectCount.get("ALL"));
						selectCnt++;
						hmSelectCount.put("ALL", String.valueOf(selectCnt));
					}
				}
				rst.close();
				pst.close();
				
				Map<String, String> hmRejectCount = new LinkedHashMap<String, String>();
				sbQue = new StringBuilder();
				sbQue.append("select cad.recruitment_id,emp_fname,emp_lname,cad.job_code,emp_per_id, cad.candidate_final_status, emp_image, " +
					" cad.application_status from candidate_personal_details cpd,candidate_application_details cad where cpd.emp_per_id = cad.candidate_id ");
				pst = con.prepareStatement(sbQue.toString());
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
				int rejectCnt=0;
				while (rst.next()) {
					if(rst.getString("application_status").equals("-1") || rst.getString("candidate_final_status").equals("-1")
						|| (hmCandiRejectFromRound != null && hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")) != null && hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")).equals("-1"))){
					rejectCnt = uF.parseToInt(hmRejectCount.get("ALL"));
					rejectCnt++;
					hmRejectCount.put("ALL", String.valueOf(rejectCnt));
					
					}
				}
				rst.close();
				pst.close();
		
				request.setAttribute("hmSelectCount", hmSelectCount);
				request.setAttribute("hmFinalCount", hmFinalCount);
				request.setAttribute("hmRejectCount", hmRejectCount);
			
				 int intNewApplications = uF.parseToInt(applyMp.get("ALL")) - (uF.parseToInt(hmSelectCount.get("ALL")) + uF.parseToInt(hmFinalCount.get("ALL")) + uF.parseToInt(hmRejectCount.get("ALL")));
				request.setAttribute("NewApplications", ""+intNewApplications);    
				    
				Map<String, List<String>> hmpanelIDS = new HashMap<String, List<String>>();
				
				List<String> panelEmpIDList = new ArrayList<String>();
				sbQue = new StringBuilder();
				sbQue.append("select recruitment_id, round_id, panel_emp_id from panel_interview_details where recruitment_id>0 ");
				pst = con.prepareStatement(sbQue.toString());
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
				while (rst.next()) {
					panelEmpIDList = hmpanelIDS.get("ALL");
					if(panelEmpIDList == null)panelEmpIDList = new ArrayList<String>();
					panelEmpIDList.add(rst.getString("panel_emp_id"));
					hmpanelIDS.put("ALL", panelEmpIDList);
				}
				rst.close();
				pst.close();
				
				Map<String, String> hmpanelName = new HashMap<String, String>();
				List<String> panelEmpIDList1 = new ArrayList<String>(); 
				panelEmpIDList1 = hmpanelIDS.get("ALL");
				String panelEmpNames = uF.showData(getAppendDataList(con, panelEmpIDList1), "");
				hmpanelName.put("ALL", panelEmpNames);
				
	//			System.out.println("getF_org() ===>> " + getF_org());
				
	//			System.out.println("panel map===="+hmpanelName);
			
		} catch (Exception e) {
				e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private String getAppendDataList(Connection con, List<String> strIDList) {
		StringBuilder sb = new StringBuilder();
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);

			for (int i =0; strIDList != null && i<strIDList.size(); i++) {

				if(strIDList.get(i)!=null && !strIDList.get(i).equals("")){
				 if(i==strIDList.size()-1){ 
					 sb.append(hmEmpName.get(strIDList.get(i).trim()));
				 } else {	
					sb.append(hmEmpName.get(strIDList.get(i).trim())+", ");
				 }
				}
		}
		return sb.toString();
	}
	
	
	private void fillJobreportStatsData() {

		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;

		Map<String, String> hmCandAccepted = new HashMap<String, String>();
		Map<String, String> hmCandRejected = new HashMap<String, String>();
//		Map<String, String> hmCandRequired = new HashMap<String, String>();
		Map<String,String> hmCandOffered = new HashMap<String, String>();
		
		Map<String,String> hmToday = new HashMap<String, String>();
    	Map<String,String> hmDayafterTommorow = new HashMap<String, String>();
    	
    	int candAccepted = 0;
    	int candRejected = 0;
    	int candOffered = 0;
    	int cnt_today = 0;
    	int cnt_dayaftertommorow = 0;
//		int noOfPositions=0;
		
		Calendar cal=GregorianCalendar.getInstance();
        SimpleDateFormat dateFormat=new  SimpleDateFormat(DBDATE);
        String strCurrentDate=dateFormat.format(cal.getTime());
        Date currentday=cal.getTime();
        cal.add(Calendar.DATE,2);
        Date dayAfterTommorow=cal.getTime();
        
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select candidate_joining_date,no_position,rd.recruitment_id,candidate_id,candidate_status,candidate_final_status from candidate_application_details join recruitment_details rd using(recruitment_id)");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				if (rst.getString("candidate_final_status").equals("1") && rst.getString("candidate_status").equals("1")) {
					candAccepted = uF.parseToInt(hmCandAccepted.get("ALL"));
					candAccepted++;
					hmCandAccepted.put("ALL", ""+ candAccepted);
				} else if (rst.getString("candidate_final_status").equals("1") && rst.getString("candidate_status").equals("-1")) {
					candRejected = uF.parseToInt(hmCandRejected.get("ALL"));
					candRejected++;
					hmCandRejected.put("ALL", ""+ candRejected);
				}else if(rst.getString("candidate_final_status").equals("1") && rst.getString("candidate_status").equals("0") ){
    				candOffered = uF.parseToInt(hmCandOffered.get("ALL")) ;
	    			candOffered++;
	    			hmCandOffered.put("ALL", ""+candOffered);
	    		}
				
//				noOfPositions = uF.parseToInt(hmCandRequired.get("ALL"));
//				noOfPositions += rst.getInt("no_position");
//				hmCandRequired.put("ALL", ""+noOfPositions);
				
				if(strCurrentDate.equals(rst.getString("candidate_joining_date"))) {
	    			cnt_today = uF.parseToInt(hmToday.get("ALL"));
	    			cnt_today++;
	    			hmToday.put("ALL", ""+cnt_today);
	    		} else {
	    			if(rst.getDate("candidate_joining_date")!=null) {
	    				if(uF.isDateBetween(currentday, dayAfterTommorow, rst.getDate("candidate_joining_date"))) {
    						cnt_dayaftertommorow = uF.parseToInt(hmDayafterTommorow.get("ALL")) ;		    			
	    					cnt_dayaftertommorow++;
	    					hmDayafterTommorow.put("ALL", ""+cnt_dayaftertommorow);
	    				} 
	    			}	    			
	    		}
			}
			rst.close();
			pst.close();
			request.setAttribute("hmToday", hmToday);
			request.setAttribute("hmDayafterTommorow", hmDayafterTommorow);
			

			int existing = 0, planned = 0, required=0;
			// for exixting employee*****************

//			pst = con.prepareStatement("Select count(*)as count from employee_official_details eod join recruitment_details rd on(eod.depart_id=rd.dept_id and eod.wlocation_id=rd.wlocation) where recruitment_id=? ");
			
			pst = con.prepareStatement("select sum(existing_emp_count) as existing_emp_count, sum(no_position) as no_position from recruitment_details where close_job_status = false ");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				existing = rst.getInt("existing_emp_count");
				required = rst.getInt("no_position");
			}
			rst.close();
			pst.close();
			
			//querying for planned records in company 
			pst = con.prepareStatement("select sum(resource_requirement) as resource_requirement from recruitment_details rd, resource_planner_details rpd " +
				" where rpd.designation_id=rd.designation_id and close_job_status = false and date_part('year', effective_date)=ryear and date_part('month', effective_date)=rmonth ");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()) {
				planned = rst.getInt("resource_requirement");
			}
			rst.close();
			pst.close();
			
			request.setAttribute("planned", planned);
			request.setAttribute("existing", existing);
			request.setAttribute("required", required);
			
			request.setAttribute("hmCandAccepted", hmCandAccepted);
			request.setAttribute("hmCandRejected", hmCandRejected);
			request.setAttribute("hmCandOffered", hmCandOffered);
//			request.setAttribute("hmCandRequired", hmCandRequired);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	private void fillOpenJobreportStatsData() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;

		Map<String, String> applyMp = new HashMap<String, String>();
		Map<String, String> approveMp = new HashMap<String, String>();
		Map<String, String> denyMp = new HashMap<String, String>();
		Map<String, String> finalisedMp = new HashMap<String, String>();
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			Map<String, String> hmCandiRejectFromRound = new HashMap<String, String>();
			pst = con.prepareStatement("Select * from candidate_interview_panel where status=-1");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				hmCandiRejectFromRound.put(rst.getString("candidate_id"), rst.getString("status"));
			}
			rst.close();
			pst.close();
			
//			System.out.println("hmCandiRejectFromRound ---> "+hmCandiRejectFromRound);
			pst = con.prepareStatement("select  count(*) as count,rd.recruitment_id, application_status, candidate_final_status, candidate_id from " +
					"candidate_application_details join recruitment_details rd using(recruitment_id) where rd.close_job_status=false group by rd.recruitment_id, " +
					"application_status, candidate_final_status, candidate_id");
			int dblTotalApplication = 0;
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {

					dblTotalApplication = uF.parseToInt(applyMp.get("ALL"));
					dblTotalApplication += uF.parseToInt(rst.getString("count"));
					applyMp.put("ALL", String.valueOf(dblTotalApplication));

				if (uF.parseToInt(rst.getString("application_status")) == 2 && uF.parseToInt(rst.getString("candidate_final_status")) == 0 && (hmCandiRejectFromRound == null || 
						hmCandiRejectFromRound.get(rst.getString("candidate_id")) == null || !hmCandiRejectFromRound.get(rst.getString("candidate_id")).equals("-1"))) {
					
					int approvecount = uF.parseToInt(uF.showData(approveMp.get("ALL"), "0"));
					approvecount += uF.parseToInt(rst.getString("count"));
					approveMp.put("ALL", ""+ approvecount);
//					System.out.println("approveMp in ===> "+approveMp);
				} 
				if (uF.parseToInt(rst.getString("application_status")) == -1 || uF.parseToInt(rst.getString("candidate_final_status")) == -1 
						|| (hmCandiRejectFromRound != null && hmCandiRejectFromRound.get(rst.getString("candidate_id")) != null && hmCandiRejectFromRound.get(rst.getString("candidate_id")).equals("-1"))) {
//					System.out.println("hmCandiRejectFromRound ===> "+hmCandiRejectFromRound);
//					System.out.println("hmCandiRejectFromRound ===> "+rst.getString("emp_per_id")+" == "+hmCandiRejectFromRound.get(rst.getString("emp_per_id")));
					int denycount = uF.parseToInt(uF.showData(denyMp.get("ALL"), "0"));
					denycount += uF.parseToInt(rst.getString("count"));
					denyMp.put("ALL", ""+ denycount);
//					denyMp.put(rst.getString("recruitment_id"),rst.getString("count"));
				}
				if (uF.parseToInt(rst.getString("candidate_final_status")) == 1 && uF.parseToInt(rst.getString("application_status")) == 2) {
					int finalcount = uF.parseToInt(uF.showData(finalisedMp.get("ALL"), "0"));
					finalcount += uF.parseToInt(rst.getString("count"));
					finalisedMp.put("ALL", ""+ finalcount);
				}
			}
			rst.close();
			pst.close();
//			System.out.println("approveMp ===> "+approveMp);
			request.setAttribute("approveMp", approveMp);
			request.setAttribute("finalisedMp", finalisedMp);
			request.setAttribute("denyMp", denyMp);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	// auto fill of candidate profile data...

	public String getAddInformation() {
		return addInformation;
	}

	public void setAddInformation(String addInformation) {
		this.addInformation = addInformation;
	}

	public String getMinEducation() {
		return minEducation;
	}

	public void setMinEducation(String minEducation) {
		this.minEducation = minEducation;
	}

	public String getMaxMonth() {
		return maxMonth;
	}

	public void setMaxMonth(String maxMonth) {
		this.maxMonth = maxMonth;
	}

	public String getMaxYear() {
		return maxYear;
	}

	public void setMaxYear(String maxYear) {
		this.maxYear = maxYear;
	}

	public String getMinMonth() {
		return minMonth;
	}

	public void setMinMonth(String minMonth) {
		this.minMonth = minMonth;
	}

	public String getMinYear() {
		return minYear;
	}

	public void setMinYear(String minYear) {
		this.minYear = minYear;
	}

	public String getCandidateProfile() {
		return candidateProfile;
	}

	public void setCandidateProfile(String candidateProfile) {
		this.candidateProfile = candidateProfile;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getView() {
		return view;
	}
	
	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public void setView(String view) {
		this.view = view;
	}

}

