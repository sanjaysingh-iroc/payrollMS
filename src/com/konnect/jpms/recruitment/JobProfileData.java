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

public class JobProfileData extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	String strSessionEmpId = null;
//	
	
	String recruitId;
	
	String currRecruitId;
	
	public String execute() throws Exception {
	    
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) 
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, TRequirement);
		request.setAttribute(PAGE, "/jsp/recruitment/jobreport.jsp");
		UtilityFunctions uF = new UtilityFunctions();
		
//		System.out.println("dataType ===>> " + getDataType() + " -- pageNumber ===>> " + getPageNumber() + " -- minLimit ===>> " + getMinLimit());
		
		preparejobreport();
		getApplicationCountOfRecruitmentID();
		
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
	    	
	    	System.out.println("getRecruitId() ===>> " + getRecruitId());
				setCurrRecruitId(getRecruitId());
			
			if(getCurrRecruitId() !=null && !getCurrRecruitId().equals("")) {
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
	        		" cad.recruitment_id in ("+getCurrRecruitId()+") ");
		        System.out.println("sbQue ===> " + sbQue.toString());
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
	    		" and cad.recruitment_id in ("+getCurrRecruitId()+") ");
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
	    		"(select candidate_id from candidate_interview_panel) and cad.recruitment_id in ("+getCurrRecruitId()+") group by recruitment_id");
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
			sbQue.append("select * from candidate_interview_panel where status=-1 and recruitment_id in ("+getCurrRecruitId()+") ");
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
				" and recruitment_id in ("+getCurrRecruitId()+") ");
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
				" and recruitment_id in ("+getCurrRecruitId()+") group by recruitment_id ");
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
				" from candidate_application_details cad where candidate_id > 0 and cad.recruitment_id in ("+getCurrRecruitId()+") ");
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
				" and cad.application_status=2 and not cad.candidate_final_status=-1 and cad.recruitment_id in ("+getCurrRecruitId()+") ");
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
					" and cad.recruitment_id in ("+getCurrRecruitId()+") ");
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
					" and recruitment_id in ("+getCurrRecruitId()+") ");
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
					" and recruitment_id in ("+getCurrRecruitId()+") ");
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
	            " recruitment_id in ("+getCurrRecruitId()+") ");
	//		sbQuery.append(" order by recruitment_id desc, close_job_status, job_approval_date desc");
			pst = con.prepareStatement(sbQuery.toString());
			System.out.println("pst  ==== >>>> "+pst);
			rst = pst.executeQuery();
			int count=0; 
	//		List<List<String>> aljobreport=new ArrayList<List<String> >();	
			Map<String, List<String>> hmJobReport = new HashMap<String, List<String>>();
			
			while(rst.next()) {
				List<String> job_code_info =new ArrayList<String>();
				job_code_info.add(rst.getString("recruitment_id"));
				job_code_info.add(rst.getString("job_code"));
				 
				job_code_info.add(hmpanelName.get(rst.getString("recruitment_id")));
				
				job_code_info.add(uF.showData(hmToday.get(rst.getString("recruitment_id")),"0"));
				job_code_info.add(uF.showData(hmDayafterTommorow.get(rst.getString("recruitment_id")),"0"));
				
				job_code_info.add(uF.showData(rst.getString("no_position"),"0"));
				job_code_info.add(uF.showData(hmCandAccepted.get(rst.getString("recruitment_id")),"0"));			
				job_code_info.add(uF.showData(hmCandRejected.get(rst.getString("recruitment_id")),"0"));
				job_code_info.add(uF.showData(hmCandOfferd.get(rst.getString("recruitment_id")),"0"));
				
				job_code_info.add(uF.showData(applyMp.get(rst.getString("recruitment_id")),"0"));
				job_code_info.add(uF.showData(hmSelectCount.get(rst.getString("recruitment_id")),"0"));
				job_code_info.add(uF.showData(hmFinalCount.get(rst.getString("recruitment_id")),"0"));
				job_code_info.add(uF.showData(hmRejectCount.get(rst.getString("recruitment_id")),"0")); //12
				
				job_code_info.add(uF.showData(hmScheduling.get(rst.getString("recruitment_id")),"0")); //13
				 
				List<String> alScheduled=(List<String>)hmScheduledCandidate.get(rst.getString("recruitment_id"));
				if(alScheduled==null)alScheduled=new ArrayList<String>();
				
				List<String> alUnderProcess=(List<String>) hmUnderProcessCandidate.get(rst.getString("recruitment_id"));
				if(alUnderProcess==null)alUnderProcess=new ArrayList<String>();
				
				job_code_info.add(""+alScheduled.size()); //14
				job_code_info.add(""+alUnderProcess.size());
			    job_code_info.add(uF.showData(rst.getString("designation_name"), "-")); //16
			    job_code_info.add(uF.parseToBoolean(rst.getString("close_job_status"))+""); //17
			    job_code_info.add(rst.getString("priority_job_int")); //18
			    boolean flag = getCandidateAddStatus(con, uF, rst.getString("recruitment_id"));
			    job_code_info.add(flag+""); //19
			    job_code_info.add(rst.getString("req_form_type")); //20
			    job_code_info.add(uF.showData(rst.getString("job_title"), "-")); //21
	//		    aljobreport.add(job_code_info);
		    	hmJobReport.put(rst.getString("recruitment_id"), job_code_info);
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmJobReport", hmJobReport);
			System.out.println("hmJobReport ==== >>>> "+hmJobReport);
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
	
	
	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getCurrRecruitId() {
		return currRecruitId;
	}

	public void setCurrRecruitId(String currRecruitId) {
		this.currRecruitId = currRecruitId;
	}

}
