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
import com.opensymphony.xwork2.ActionSupport;

public class ReportJobProfilePopUp extends ActionSupport implements
		ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;

	String recruitId = null;
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
	public String execute() throws Exception {

	//	request.setAttribute(PAGE, "/jsp/recruitment/ReportJobProfilePopUp.jsp");
		request.setAttribute(TITLE, "Job Stats");
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);


		// for charts data
		fillJobreportStatsData(uF.parseToInt(recruitId));
		fillOpenJobreportStatsData();

		fillupdateinfo(uF.parseToInt(recruitId)); // /priting data report job
													// desc
		getSelectedJobProfile(recruitId); // printing data report

		if(getView() != null && getView().equals("jobreport")) {
			preparejobreport();
			getApplicationCountOfRecruitmentID();
		}
		
//		request.setAttribute("recruitID", recruitId);

		return LOAD;

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
			pst=con.prepareStatement("select count(*) as count,recruitment_id from candidate_application_details group by recruitment_id");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				hmAppCount.put(rst.getString("recruitment_id"), rst.getString("count"));
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
	    	//preparing acceptance from candidate********************
	    	
//	    	System.out.println("getRecruitId() ===>> " + getRecruitId());
			
			if(uF.parseToInt(getRecruitId()) > 0) {
		    	Map<String,String> hmCandAccepted=new HashMap<String, String>();
		    	Map<String,String> hmCandRejected=new HashMap<String, String>();
		    	Map<String,String> hmCandRequired=new HashMap<String, String>();
		    	Map<String,String> hmCandOfferd=new HashMap<String, String>();
		    	Map<String,String> hmToday=new HashMap<String, String>();
		    	Map<String,String> hmDayafterTommorow=new HashMap<String, String>();
		    	
		    	int  candAccepted = 0;
		    	int candRejected = 0;
		    	int candOffred = 0;
		    	int cnt_today = 0;
		    	int cnt_dayaftertommorow = 0;
		    	
		    	Calendar cal=GregorianCalendar.getInstance();
		        SimpleDateFormat dateFormat=new  SimpleDateFormat(DBDATE);
		        String strCurrentDate=dateFormat.format(cal.getTime());
		        Date currentday=cal.getTime();
		        cal.add(Calendar.DATE,2);
		        Date dayAfterTommorow=cal.getTime();
		        
		        StringBuilder sbQue = new StringBuilder();
		        sbQue.append("select candidate_joining_date,no_position,recruitment_id,candidate_id,candidate_status,candidate_final_status from " +
	        		" candidate_application_details cad join recruitment_details rd using(recruitment_id) where rd.recruitment_id > 0 and " +
	        		" cad.recruitment_id in ("+getRecruitId()+") ");
//		        System.out.println("sbQue ===> " + sbQue.toString());
		    	pst=con.prepareStatement(sbQue.toString());
		    	rst=pst.executeQuery();
	//	    	System.out.println("new Date ===> " + new Date());
		    	while(rst.next()) {
		    		if(rst.getString("candidate_final_status").equals("1") && rst.getString("candidate_status").equals("1")) {
		    			if(hmCandAccepted.keySet().contains(rst.getString("recruitment_id")))
		    				candAccepted= uF.parseToInt(hmCandAccepted.get(rst.getString("recruitment_id"))) ;
		    			else candAccepted=0;
		    				candAccepted++;
		                
		    			hmCandAccepted.put(rst.getString("recruitment_id"),""+candAccepted);
		                 
		    		}else if(rst.getString("candidate_final_status").equals("1") && rst.getString("candidate_status").equals("-1") ){
		    
		    			if(hmCandRejected.keySet().contains(rst.getString("recruitment_id")))
		    			candRejected=uF.parseToInt(hmCandRejected.get(rst.getString("recruitment_id"))) ;
		    			else candRejected=0;
		    			candRejected++;
		    			
		    			hmCandRejected.put(rst.getString("recruitment_id"),""+candRejected);
		    		}else if(rst.getString("candidate_final_status").equals("1") && rst.getString("candidate_status").equals("0") ){
		    
		    			if(hmCandOfferd.keySet().contains(rst.getString("recruitment_id")))
		    				candOffred=uF.parseToInt(hmCandOfferd.get(rst.getString("recruitment_id"))) ;
		    			else candOffred=0;
		    			candOffred++;
		    			
		    			hmCandOfferd.put(rst.getString("recruitment_id"),""+candOffred);
		    		}
		    		
		    		hmCandRequired.put(rst.getString("recruitment_id"),""+rst.getInt("no_position"));
	//	    		System.out.println("strCurrentDate ===> "+strCurrentDate);
	//	    		System.out.println("rst.getString(candidate_joining_date) ===> "+rst.getString("candidate_joining_date"));
		    		if(strCurrentDate.equals(rst.getString("candidate_joining_date"))) {
		    			if(hmToday.keySet().contains(rst.getString("recruitment_id"))) {
		    				cnt_today=uF.parseToInt(hmToday.get(rst.getString("recruitment_id")));
		    			} else {
		    				cnt_today=0;
	    				}
		    			cnt_today++;
		    			hmToday.put(rst.getString("recruitment_id"),""+cnt_today);
		    		} else {
		    			
		    			if(rst.getDate("candidate_joining_date")!=null) {
		    		
		    				if(uF.isDateBetween(currentday, dayAfterTommorow, rst.getDate("candidate_joining_date"))) {
		    					if(hmDayafterTommorow.keySet().contains(rst.getString("recruitment_id"))) {	
		    						cnt_dayaftertommorow=uF.parseToInt(hmDayafterTommorow.get(rst.getString("recruitment_id"))) ;		    			
		    					} else {
		    						cnt_dayaftertommorow=0;	    				
		    					}
		    					cnt_dayaftertommorow++;
		    					hmDayafterTommorow.put(rst.getString("recruitment_id"), ""+cnt_dayaftertommorow);	    					
		    				} 
		    				}	    			
		    		}	    		
		    	}
		    	rst.close();
				pst.close();
	
		    	
			// preparing  interview Status **************
			
	    	Map hmScheduledCandidate = new HashMap();
	    	Map hmUnderProcessCandidate = new HashMap();	
		    
	    	sbQue = new StringBuilder();
	    	sbQue.append("select cip.candidate_id, panel_round_id, cip.recruitment_id, status from candidate_interview_panel cip join " +
	    		"candidate_application_details cad on(cip.candidate_id=cad.candidate_id) where candidate_final_status=0 and is_selected = 0 " +
	    		" and cad.recruitment_id in ("+getRecruitId()+") ");
	    	sbQue.append(" group by cip.candidate_id, panel_round_id, cip.recruitment_id, status order by cip.candidate_id, status");
		    pst = con.prepareStatement(sbQue.toString());
		    rst= pst.executeQuery();
	//	    System.out.println("new Date ===> " + new Date());
		    int nCount = 0;	    
		    String strCandidateNew = null;
		    String strCandidateOld = null;
		    
		    List alCandidateIdUP = new ArrayList();
		    List alCandidateIdS = new ArrayList();
	
		    while(rst.next()){
		    	strCandidateNew = rst.getString("recruitment_id");
		    	if(strCandidateNew!=null && !strCandidateNew.equalsIgnoreCase(strCandidateOld)){
		    		alCandidateIdS = new ArrayList();
		    		alCandidateIdUP = new ArrayList();
		    	}
		    	
		    	if(uF.parseToInt(rst.getString("status"))==0){
		    		
		    		if(!alCandidateIdS.contains(rst.getString("candidate_id"))){
		    			alCandidateIdS.add(rst.getString("candidate_id"));
		    		}
		    		hmScheduledCandidate.put(rst.getString("recruitment_id"), alCandidateIdS);
		    	}
		    	
		    	if(uF.parseToInt(rst.getString("status"))==1){
		    		
		    		if(alCandidateIdS.contains(rst.getString("candidate_id"))){
		    			alCandidateIdS.remove(rst.getString("candidate_id"));
		    		}
		    		
		    		if(!alCandidateIdUP.contains(rst.getString("candidate_id"))){
		    			alCandidateIdUP.add(rst.getString("candidate_id"));
		    		}	    		
		    		hmUnderProcessCandidate.put(rst.getString("recruitment_id"), alCandidateIdUP);
		    	}
		    	strCandidateOld = strCandidateNew;
		    }
		    rst.close();
			pst.close();
			
		    
		    Map<String,String> hmScheduling = new HashMap<String,String>();
		    sbQue = new StringBuilder();
		    sbQue.append("select recruitment_id,count(*) as count from candidate_application_details cad where application_status=2 and candidate_id not in " +
	    		"(select candidate_id from candidate_interview_panel) and cad.recruitment_id in ("+getRecruitId()+") group by recruitment_id");
		    pst=con.prepareStatement(sbQue.toString());
		    rst=pst.executeQuery();
	//	    System.out.println("new Date ===> " + new Date());
		    while(rst.next()) {
		    	hmScheduling.put(rst.getString("recruitment_id"), rst.getString("count"));
		    }
		    rst.close();
			pst.close();
			
	        ///Preparing application statuss***************
		
			Map<String ,String> applyMp=new HashMap<String,String>();
	      	
			Map<String, String> hmCandiRejectFromRound = new HashMap<String, String>();
			sbQue = new StringBuilder();
			sbQue.append("select * from candidate_interview_panel where status=-1 and recruitment_id in ("+getRecruitId()+") ");
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
			sbQue.append("select count (distinct(panel_round_id)) as count,recruitment_id,candidate_id from candidate_interview_panel where status = 1 " +
				" and recruitment_id in ("+getRecruitId()+") ");
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
				" and recruitment_id in ("+getRecruitId()+") group by recruitment_id ");
			pst = con.prepareStatement(sbQue.toString());
			rst = pst.executeQuery();
	//		System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				hmRoundCnt.put(rst.getString("recruitment_id"), rst.getString("count"));
			}
			rst.close();
			pst.close();
			
			sbQue = new StringBuilder();
			sbQue.append("select  count(*) as count,recruitment_id, application_status, candidate_final_status, candidate_id" +
				" from candidate_application_details cad where candidate_id > 0 and cad.recruitment_id in ("+getRecruitId()+") ");
			sbQue.append(" group by recruitment_id, application_status, candidate_final_status, candidate_id");
			pst = con.prepareStatement(sbQue.toString());				
			int dblTotalApplication = 0;
			rst = pst.executeQuery();
	//		System.out.println("new Date ===> " + new Date());
//			System.out.println("pst ===> "+pst);
			while (rst.next()) {
				if(applyMp.get(rst.getString("recruitment_id"))!=null)
				dblTotalApplication=uF.parseToInt(applyMp.get(rst.getString("recruitment_id")));
				else dblTotalApplication=0;
				
				dblTotalApplication+=rst.getInt("count");		            	 
	            applyMp.put(rst.getString("recruitment_id"),String.valueOf(dblTotalApplication));
							
			}
			rst.close();
			pst.close();
			
			
			Map<String, String> hmSelectCount = new LinkedHashMap<String, String>();
			Map<String, String> hmFinalCount = new LinkedHashMap<String, String>();
			
			sbQue = new StringBuilder();
			sbQue.append("select cad.recruitment_id,emp_fname,emp_lname,cad.job_code,emp_per_id,cad.candidate_final_status," +
				"emp_image from candidate_personal_details cpd, candidate_application_details cad where cpd.emp_per_id = cad.candidate_id " +
				" and cad.application_status=2 and not cad.candidate_final_status=-1 and cad.recruitment_id in ("+getRecruitId()+") ");
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
						hmClearRoundCnt.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")).equals(hmRoundCnt.get(rst.getString("recruitment_id")))){
						finalCnt = uF.parseToInt(hmFinalCount.get(rst.getString("recruitment_id")));
						finalCnt++;
						hmFinalCount.put(rst.getString("recruitment_id"), String.valueOf(finalCnt));
						
					} else if (rst.getInt("candidate_final_status") == 0 && (hmCandiRejectFromRound == null || 
						hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")) == null || !hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")).equals("-1"))){
						selectCnt = uF.parseToInt(hmSelectCount.get(rst.getString("recruitment_id")));
						selectCnt++;
						hmSelectCount.put(rst.getString("recruitment_id"), String.valueOf(selectCnt));
					}
				}
				rst.close();
				pst.close();
				
				Map<String, String> hmRejectCount = new LinkedHashMap<String, String>();
				sbQue = new StringBuilder();
				sbQue.append("select cad.recruitment_id,emp_fname,emp_lname,cad.job_code,emp_per_id, cad.candidate_final_status, emp_image, " +
					" cad.application_status from candidate_personal_details cpd,candidate_application_details cad where cpd.emp_per_id = cad.candidate_id " +
					" and cad.recruitment_id in ("+getRecruitId()+") ");
				pst = con.prepareStatement(sbQue.toString());
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
				int rejectCnt=0;
				while (rst.next()) {
					if(rst.getString("application_status").equals("-1") || rst.getString("candidate_final_status").equals("-1")
						|| (hmCandiRejectFromRound != null && hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")) != null && hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")).equals("-1"))){
					
					rejectCnt = uF.parseToInt(hmRejectCount.get(rst.getString("recruitment_id")));
					rejectCnt++;
					hmRejectCount.put(rst.getString("recruitment_id"), String.valueOf(rejectCnt));
					
					}
				}
				rst.close();
				pst.close();
		
				request.setAttribute("hmSelectCount", hmSelectCount);
				request.setAttribute("hmFinalCount", hmFinalCount);
				request.setAttribute("hmRejectCount", hmRejectCount);
			
				Map<String, List<String>> hmpanelIDS = new HashMap<String, List<String>>();
				
				List<String> panelEmpIDList = new ArrayList<String>();
				sbQue = new StringBuilder();
				sbQue.append("select recruitment_id, round_id, panel_emp_id from panel_interview_details where recruitment_id>0  " +
					" and recruitment_id in ("+getRecruitId()+") ");
				pst = con.prepareStatement(sbQue.toString());
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
				while (rst.next()) {
					panelEmpIDList = hmpanelIDS.get(rst.getString("recruitment_id"));
					if(panelEmpIDList == null)panelEmpIDList = new ArrayList<String>();
					panelEmpIDList.add(rst.getString("panel_emp_id"));
					hmpanelIDS.put(rst.getString("recruitment_id"), panelEmpIDList);
				}
				rst.close();
				pst.close();
				
				Map<String, String> hmpanelName = new HashMap<String, String>();
				List<String> panelEmpIDList1 = new ArrayList<String>(); 
				sbQue = new StringBuilder();
				sbQue.append("select recruitment_id, round_id, panel_emp_id from panel_interview_details where recruitment_id>0  " +
					" and recruitment_id in ("+getRecruitId()+") ");
				pst = con.prepareStatement(sbQue.toString());
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
				while (rst.next()) {
					panelEmpIDList1 = hmpanelIDS.get(rst.getString("recruitment_id"));
					String panelEmpNames = uF.showData(getAppendDataList(con, panelEmpIDList1), "");
					hmpanelName.put(rst.getString("recruitment_id"), panelEmpNames);
				}
				rst.close();
				pst.close();
				
				
	//			System.out.println("getF_org() ===>> " + getF_org());
				
	//			System.out.println("panel map===="+hmpanelName);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select designation_name,job_code,recruitment_id,custum_designation,close_job_status,no_position,priority_job_int,req_form_type,"+ 
	            " job_title from recruitment_details left join designation_details using(designation_id) where job_approval_status=1 and " +
	            " recruitment_id in ("+getRecruitId()+") ");
	//		sbQuery.append(" order by recruitment_id desc, close_job_status, job_approval_date desc");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst  ==== >>>> "+pst);
			rst = pst.executeQuery();
			int count=0; 
	//		List<List<String>> aljobreport=new ArrayList<List<String> >();	
			Map<String, List<String>> hmJobReport = new HashMap<String, List<String>>();
			
			while(rst.next()) {
				List<String> job_code_info =new ArrayList<String>();
				job_code_info.add(rst.getString("recruitment_id")); //0
				job_code_info.add(rst.getString("job_code")); //1
				 
				job_code_info.add(hmpanelName.get(rst.getString("recruitment_id"))); //2
				
				job_code_info.add(uF.showData(hmToday.get(rst.getString("recruitment_id")), "0")); //3
				job_code_info.add(uF.showData(hmDayafterTommorow.get(rst.getString("recruitment_id")), "0")); //4
				
				job_code_info.add(uF.showData(rst.getString("no_position"), "0")); //5
				job_code_info.add(uF.showData(hmCandAccepted.get(rst.getString("recruitment_id")), "0")); //6		
				job_code_info.add(uF.showData(hmCandRejected.get(rst.getString("recruitment_id")), "0")); //7
				job_code_info.add(uF.showData(hmCandOfferd.get(rst.getString("recruitment_id")), "0")); //8
				
				job_code_info.add(uF.showData(applyMp.get(rst.getString("recruitment_id")), "0")); //9
				job_code_info.add(uF.showData(hmSelectCount.get(rst.getString("recruitment_id")), "0")); //10
				job_code_info.add(uF.showData(hmFinalCount.get(rst.getString("recruitment_id")), "0")); //11
				job_code_info.add(uF.showData(hmRejectCount.get(rst.getString("recruitment_id")), "0")); //12
				
				job_code_info.add(uF.showData(hmScheduling.get(rst.getString("recruitment_id")), "0")); //13
				 
				List<String> alScheduled=(List<String>)hmScheduledCandidate.get(rst.getString("recruitment_id"));
				if(alScheduled==null)alScheduled=new ArrayList<String>();
				
				List<String> alUnderProcess=(List<String>) hmUnderProcessCandidate.get(rst.getString("recruitment_id"));
				if(alUnderProcess==null)alUnderProcess=new ArrayList<String>();
				
				job_code_info.add(""+alScheduled.size()); //14
				job_code_info.add(""+alUnderProcess.size()); //15
			    job_code_info.add(uF.showData(rst.getString("designation_name"), "-")); //16
			    job_code_info.add(uF.parseToBoolean(rst.getString("close_job_status"))+""); //17
			    job_code_info.add(rst.getString("priority_job_int")); //18
			    boolean flag = getCandidateAddStatus(con, uF, rst.getString("recruitment_id")); //18
			    job_code_info.add(flag+""); //19
			    job_code_info.add(rst.getString("req_form_type")); //20
			    job_code_info.add(uF.showData(rst.getString("job_title"), "-")); //21
			    
			    int intNewApplications = uF.parseToInt(applyMp.get(rst.getString("recruitment_id"))) - (uF.parseToInt(hmSelectCount.get(rst.getString("recruitment_id")))
			    		+ uF.parseToInt(hmFinalCount.get(rst.getString("recruitment_id"))) + uF.parseToInt(hmRejectCount.get(rst.getString("recruitment_id"))));
			    job_code_info.add(""+intNewApplications); //22
	//		    aljobreport.add(job_code_info);
		    	hmJobReport.put(rst.getString("recruitment_id"), job_code_info);
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmJobReport", hmJobReport);
//			System.out.println("hmJobReport ==== >>>> "+hmJobReport);
	    }
//		request.setAttribute("recruitmentIDList", getRecruitId());
//		request.setAttribute("job_code_info", aljobreport);
		} catch (Exception e) {
				e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private boolean getCandidateAddStatus(Connection con, UtilityFunctions uF, String recruitId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		boolean flag = false;
		try {
			pst = con.prepareStatement("select recruitment_id from candidate_application_details where recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(recruitId));
			rst=pst.executeQuery();
//			System.out.println("pst ===> " + pst);
			while(rst.next()) {
				flag = true;
			}
			rst.close();
			pst.close();
			
		} catch(Exception e) {
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
		return flag;
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
	
	
	private void fillJobreportStatsData(int recruitid) {

		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;

		Map<String, String> hmCandAccepted = new HashMap<String, String>();
		Map<String, String> hmCandRejected = new HashMap<String, String>();
		Map<String, String> hmCandRequired = new HashMap<String, String>();

		int candAccepted = 0, candRejected = 0;
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select candidate_joining_date,no_position,rd.recruitment_id,candidate_id,candidate_status,candidate_final_status from candidate_application_details join recruitment_details rd using(recruitment_id)");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {

				if (rst.getString("candidate_final_status").equals("1") && rst.getString("candidate_status").equals("1")) {
					if (hmCandAccepted.keySet().contains(rst.getString("recruitment_id")))
						candAccepted = uF.parseToInt(hmCandAccepted.get(rst.getString("recruitment_id")));

					else
						candAccepted = 0;
					candAccepted++;
					hmCandAccepted.put(rst.getString("recruitment_id"), ""+ candAccepted);

				} else if (rst.getString("candidate_final_status").equals("1") && rst.getString("candidate_status").equals("-1")) {

					if (hmCandRejected.keySet().contains(rst.getString("recruitment_id"))) {
						candRejected = uF.parseToInt(hmCandRejected.get(rst.getString("recruitment_id")));
					} else {
						candRejected = 0;
					}
					candRejected++;
					hmCandRejected.put(rst.getString("recruitment_id"), ""+ candRejected);
				}
				hmCandRequired.put(rst.getString("recruitment_id"),""+rst.getInt("no_position"));
			}
			rst.close();
			pst.close();

			int existing = 0,planned=0;
			// for exixting employee*****************

//			pst = con.prepareStatement("Select count(*)as count from employee_official_details eod join recruitment_details rd on(eod.depart_id=rd.dept_id and eod.wlocation_id=rd.wlocation) where recruitment_id=? ");
			
			pst = con.prepareStatement("select existing_emp_count from recruitment_details where recruitment_id=? ");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				existing = rst.getInt("existing_emp_count");
			}
			rst.close();
			pst.close();
			
			
			//querying for planned records in company 
			pst = con.prepareStatement("select recruitment_id,resource_requirement from recruitment_details rd, resource_planner_details rpd " +
				" where rpd.designation_id=rd.designation_id and date_part('year', effective_date)=ryear and date_part('month', effective_date)=rmonth " +
				" and recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				planned=rst.getInt("resource_requirement");
			}
			rst.close();
			pst.close();
			
			request.setAttribute("planned", planned);
			request.setAttribute("existing", existing);
			
			request.setAttribute("hmCandAccepted", hmCandAccepted);
			request.setAttribute("hmCandRejected", hmCandRejected);
			request.setAttribute("hmCandRequired", hmCandRequired);

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
					"candidate_application_details join recruitment_details rd using(recruitment_id) group by rd.recruitment_id , " +
					"application_status, candidate_final_status, candidate_id");
			int dblTotalApplication = 0;
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				if (applyMp.get(rst.getString("recruitment_id")) != null) {

					dblTotalApplication = uF.parseToInt(applyMp.get(rst.getString("recruitment_id")));
					dblTotalApplication += uF.parseToInt(rst.getString("count"));
				
					applyMp.put(rst.getString("recruitment_id"),String.valueOf(dblTotalApplication));

				} else {
					
					applyMp.put(rst.getString("recruitment_id"),rst.getString("count"));
				}

				if (uF.parseToInt(rst.getString("application_status")) == 2 && uF.parseToInt(rst.getString("candidate_final_status")) == 0 && (hmCandiRejectFromRound == null || 
						hmCandiRejectFromRound.get(rst.getString("candidate_id")) == null || !hmCandiRejectFromRound.get(rst.getString("candidate_id")).equals("-1"))) {
					
					int approvecount = uF.parseToInt(uF.showData(approveMp.get(rst.getString("recruitment_id")), "0"));
					approvecount += uF.parseToInt(rst.getString("count"));
					approveMp.put(rst.getString("recruitment_id"), ""+ approvecount);
//					System.out.println("approveMp in ===> "+approveMp);

				} 
				if (uF.parseToInt(rst.getString("application_status")) == -1 || uF.parseToInt(rst.getString("candidate_final_status")) == -1 
						|| (hmCandiRejectFromRound != null && hmCandiRejectFromRound.get(rst.getString("candidate_id")) != null && hmCandiRejectFromRound.get(rst.getString("candidate_id")).equals("-1"))) {
//					System.out.println("hmCandiRejectFromRound ===> "+hmCandiRejectFromRound);
//					System.out.println("hmCandiRejectFromRound ===> "+rst.getString("emp_per_id")+" == "+hmCandiRejectFromRound.get(rst.getString("emp_per_id")));
					int denycount = uF.parseToInt(uF.showData(denyMp.get(rst.getString("recruitment_id")), "0"));
					denycount += uF.parseToInt(rst.getString("count"));
					denyMp.put(rst.getString("recruitment_id"), ""+ denycount);
//					denyMp.put(rst.getString("recruitment_id"),rst.getString("count"));

				}
				if (uF.parseToInt(rst.getString("candidate_final_status")) == 1 && uF.parseToInt(rst.getString("application_status")) == 2) 
				{
					int finalcount = uF.parseToInt(uF.showData(finalisedMp.get(rst.getString("recruitment_id")), "0"));
					finalcount += uF.parseToInt(rst.getString("count"));
					finalisedMp.put(rst.getString("recruitment_id"), ""+ finalcount);
					
//					finalisedMp.put(rst.getString("recruitment_id"),rst.getString("count"));

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

	private String job_desc_info = null;
	private String cand_profile_info = null;

	// auto fill of candidate profile data...
	public void fillupdateinfo(int recruitid) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;

		try {
			con = db.makeConnection(con);
			String infoquery = "select job_description,candidate_profile from recruitment_details where designation_id=(select designation_id from recruitment_details where recruitment_id=? ) and recruitment_id!=? and job_description is not null order by effective_date desc limit 1 ";
			pst = con.prepareStatement(infoquery);
			pst.setInt(1, recruitid);
			pst.setInt(2, recruitid);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {

				job_desc_info = rst.getString("job_description");
				cand_profile_info = rst.getString("candidate_profile");

			}
			rst.close();
			pst.close();

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void getSelectedJobProfile(String recruitID2) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		// List<List<String>> requestList = new ArrayList<List<String>>();
		List<String> jobProfileList = null;

		try {

			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
//			Map<String, String> hmGender = new HashMap<String, String>();
//			hmGender.put("M", "Male");
//			hmGender.put("F", "Female");
//			hmGender.put("0", "Any");
//			hmGender.put("", "Any");
//			request.setAttribute("hmGender", hmGender);
			
			pst=con.prepareStatement("select * from requirement_employment_type");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, String> hmRequireEmpType = new HashMap<String, String>();
			while(rst.next()){
				hmRequireEmpType.put(rst.getString("employment_type_id"), uF.showData(rst.getString("employment_type"),""));
			}
			rst.close();
			pst.close();
			
			Map<String, String> hmSkillName = CF.getSkillNameMap(con);
			Map<String, String> hmEduName = CF.getDegreeNameMap(con);
			String query = "select r.recruitment_id,d.designation_name,g.grade_code,w.wlocation_name,r.no_position,r.comments,r.job_code," +
					"r.job_description,r.min_exp,r.max_exp,r.min_education,r.candidate_profile,r.additional_info,r.job_approval_status," +
					"r.custum_designation,l.level_name,o.org_name,r.skills,r.ideal_candidate,r.priority_job_int,r.essential_qualification," +
					"r.alternate_qualification,r.essential_skills,r.type_of_employment,r.sex,r.age,r.vacancy_type,r.give_justification," +
					"r.replacement_person_ids,r.reporting_to_person_ids,r.temp_casual_give_jastification,r.consultant_ids,r.source_of_requirement," +
					"r.advertisement_media,r.effective_date,r.target_deadline,r.job_title,r.customer_id,r.added_by,r.hiring_manager,r.min_ctc," +
					"r.max_ctc from recruitment_details r left join grades_details g using(grade_id) " +
					"left join work_location_info w on r.wlocation=w.wlocation_id left join designation_details d on r.designation_id=d.designation_id " +
					"left join org_details o on r.org_id=o.org_id  left join level_details l on r.level_id=l.level_id where r.status=1 and r.recruitment_id=?";
			
			pst = con.prepareStatement(query);
			pst.setInt(1, uF.parseToInt(recruitID2));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			int nCount = 0;
			System.out.println("pst ===> "+pst);
			while (rst.next()) {

				jobProfileList = new ArrayList<String>();
				jobProfileList.add(removeNUll(rst.getString(1)));
				
				if(rst.getString("designation_name")!=null )
					jobProfileList.add(removeNUll(rst.getString(2)));
					else
						jobProfileList.add(rst.getString("custum_designation"));
				
				jobProfileList.add(removeNUll(rst.getString(3)));
				jobProfileList.add(removeNUll(rst.getString(4)));
				jobProfileList.add(removeNUll(rst.getString(5)));
				jobProfileList.add(removeNUll(rst.getString(6)));
				jobProfileList.add(removeNUll(rst.getString(7)));

				if (job_desc_info != null && !job_desc_info.equals("")) {
					jobProfileList.add(job_desc_info); //7
				} else {
					jobProfileList.add(removeNUll(rst.getString(8))); //7
				}

				String minex;
				if (rst.getString(9) == null || rst.getString(9).equals("")) {
					minex = removeNUll("0.0");
				} else if (rst.getString(9).contains(".1")) {
					minex = removeNUll(rst.getString(9) + "0");
				} else if (rst.getString(9).contains(".")) {
					minex = removeNUll(rst.getString(9));
				} else {
					minex = removeNUll(rst.getString(9) + ".0");
				}
				String[] minTemp = splitString(minex);
				jobProfileList.add(removeNUll(minTemp[0]));
				jobProfileList.add(removeNUll(minTemp[1])); //9

				String maxex;
				if (rst.getString(10) == null || rst.getString(10).equals("")) {
					maxex = removeNUll("0.0");
				} else if (rst.getString(10).contains(".1")) {
					maxex = removeNUll(rst.getString(10) + "0");
				} else if (rst.getString(10).contains(".")) {
					maxex = removeNUll(rst.getString(10));
				} else {
					maxex = removeNUll(rst.getString(10) + ".0");
				}
				String[] maxTemp = splitString(maxex);
				jobProfileList.add(removeNUll(maxTemp[0])); //10
				jobProfileList.add(removeNUll(maxTemp[1])); //11

				jobProfileList.add(removeNUll(getAppendData(rst.getString("min_education"), hmEduName))); //12
//				jobProfileList.add(removeNUll((rs.getString("min_education") != null && rs.getString("min_education").length()>0 && rs.getString("min_education").substring(0, 1).equals(",")) ? rs.getString("min_education").substring(1, rs.getString("min_education").length()) : rs.getString("min_education")));

				if (cand_profile_info != null && !cand_profile_info.equals("")) {
					jobProfileList.add(cand_profile_info); //13
				} else {
					jobProfileList.add(removeNUll(rst.getString(12)));//13
				}

				jobProfileList.add(removeNUll(rst.getString(13))); //14
				jobProfileList.add(removeNUll(rst.getString(14))); //15
				jobProfileList.add(removeNUll(rst.getString("level_name"))); //16
				jobProfileList.add(removeNUll(rst.getString("org_name"))); //17
				jobProfileList.add(removeNUll(getAppendData(rst.getString("skills"), hmSkillName))); //18
				jobProfileList.add(removeNUll(rst.getString("ideal_candidate"))); //19
				if(rst.getInt("priority_job_int")==0) {
					jobProfileList.add("Low"); //20
				} else if(rst.getInt("priority_job_int")==1) {
					jobProfileList.add("High"); //20
				} else if(rst.getInt("priority_job_int")==2) {
					jobProfileList.add("Medium"); //20
				}
				jobProfileList.add(removeNUll(getAppendData(rst.getString("essential_qualification"), hmEduName))); //21
				jobProfileList.add(removeNUll(rst.getString("alternate_qualification"))); //22
				jobProfileList.add(removeNUll(getAppendData(rst.getString("essential_skills"), hmSkillName))); //23
				String typeOfEmployment = uF.stringMapping(rst.getString("type_of_employment"));
				String strSex = null;
				if(rst.getString("sex") != null && rst.getString("sex").equals("F")) {
					strSex = "Female";
				} else if(rst.getString("sex") != null && rst.getString("sex").equals("M")) {
					strSex = "Male";
				} else if(rst.getString("sex") != null && rst.getString("sex").equals("0")) {
					strSex = "Any";
				} else {
					strSex = "Any";
				}
				jobProfileList.add(removeNUll(rst.getString("sex"))); //24
				jobProfileList.add(removeNUll(rst.getString("age"))); //25
				String strVacancyType = null;
				if(rst.getString("vacancy_type") != null && rst.getString("vacancy_type").equals("0")) {
					strVacancyType = "Replacement";
				} else if(rst.getString("vacancy_type") != null && rst.getString("vacancy_type").equals("1")) {
					strVacancyType = "Additional";
				}
				jobProfileList.add(removeNUll(rst.getString("vacancy_type"))); //26
				jobProfileList.add(removeNUll(rst.getString("give_justification"))); //27
				jobProfileList.add(removeNUll(getAppendData(rst.getString("replacement_person_ids"), hmEmpName))); //28
				jobProfileList.add(removeNUll(getAppendData(rst.getString("reporting_to_person_ids"), hmEmpName))); //29
				jobProfileList.add(removeNUll(rst.getString("temp_casual_give_jastification"))); //30
				jobProfileList.add(removeNUll(strSex)); //31
				jobProfileList.add(removeNUll(strVacancyType)); //32
				jobProfileList.add(removeNUll(typeOfEmployment)); //33
				jobProfileList.add(removeNUll(rst.getString("type_of_employment"))); //34
				jobProfileList.add(removeNUll(getAppendData(rst.getString("consultant_ids"), hmEmpName))); //35
				jobProfileList.add(removeNUll(rst.getString("comments"))); //36
				jobProfileList.add(uF.getDateFormat(rst.getString("effective_date"), DBDATE, CF.getStrReportDateFormat())); //37
				jobProfileList.add(uF.getDateFormat(rst.getString("target_deadline"), DBDATE, CF.getStrReportDateFormat())); //38
				jobProfileList.add(uF.showData(rst.getString("job_title"), "-")); //39
				jobProfileList.add(uF.showData(getAppendData(rst.getString("hiring_manager"), hmEmpName), "-")); //40
				jobProfileList.add(removeNUll(CF.getClientNameById(con, rst.getString("customer_id")))); //41
				jobProfileList.add(uF.showData(rst.getString("min_ctc"), "0")); //42
				jobProfileList.add(uF.showData(rst.getString("max_ctc"), "0")); //43
				nCount++;
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
		request.setAttribute("jobProfileList", jobProfileList);
//		System.out.println("jobProfileList ===> " + jobProfileList);
	}
	
	
	private String getAppendData(String strID, Map<String, String> mp) {
		StringBuilder sb = null;
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("strID :: "+strID);
		if (strID != null && !strID.equals("") && !strID.isEmpty()) {
			if(strID.length()>0 && strID.substring(0, 1).equals(",") && strID.substring(strID.length()-1, strID.length()).equals(",")){
			strID = strID.substring(1, strID.length()-1);
			}
			if (strID.contains(",")) {
				String[] temp = strID.split(",");
				for (int i = 0; i < temp.length; i++) {
					if(uF.parseToInt(temp[i].trim()) > 0) {
						if (sb == null) {
							sb = new StringBuilder();
							sb.append(mp.get(temp[i].trim()));
						} else {
							sb.append(", " + mp.get(temp[i].trim()));
						}
					}
				}
			} else {
				return mp.get(strID);
			}
		} else {
			return null;
		}
		if (sb == null) {
			sb = new StringBuilder();
		}
		return sb.toString();
	}
	

	private String[] splitString(String st) {
		if (st.equals("") || st.equals("0")) {
			st = "0.0";
		}
		st = st.replace('.', '_');
		String str[] = st.split("_");
		return str;
	}

	private String removeNUll(String strNull) {

		if (strNull == null) {
			strNull = "";
		}
		return strNull;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

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
