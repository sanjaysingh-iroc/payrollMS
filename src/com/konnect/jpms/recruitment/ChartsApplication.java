package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
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

public class ChartsApplication extends ActionSupport implements ServletRequestAware,IStatements{

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId = null;
	CommonFunctions CF = null;
	UtilityFunctions uF=new UtilityFunctions();
	
	public String execute() throws Exception {
     
		session = request.getSession(); 
		CF= (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		
		strSessionEmpId = (String) session.getAttribute(EMPID);
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		   
		
		request.setAttribute(PAGE, "/jsp/recruitment/ApplicationPieChart.jsp");
		request.setAttribute(TITLE, "Applications Charts");
		
		
		prepareApplicationsData();
		prepareCandidateData();
   
		request.setAttribute("jobcode", getJobcode());
           
	
		
		return "popup" ;
	
	}
	

private void prepareCandidateData() {

	Connection con = null;
	Database db = new Database();
	db.setRequest(request);
	PreparedStatement pst = null;	
	ResultSet rst = null;
	
	
	Map<String,String> hmCandOfferRejected=new HashMap<String, String>();
	Map<String,String> hmCandOfferAccepted=new HashMap<String, String>();
	Map<String,String> hmCandOfferUP=new HashMap<String, String>();
	
	Map<String,String> hmCandOfferedHr=new HashMap<String, String>();
	Map<String,String> hmCandRejectHr=new HashMap<String, String>();
	Map<String,String> hmCandUPHr=new HashMap<String, String>();
	
	Map<String,String> hmCandRequired=new HashMap<String, String>();	
	Map<String,String> hmCandTotalSelected=new HashMap<String, String>();
	
	try{
		
		con=db.makeConnection(con);
		
		pst=con.prepareStatement("select job_code,candidate_final_status,candidate_status,no_position from  candidate_personal_details join recruitment_details using(job_code) where application_status=1 and job_code=? ");
		pst.setString(1, getJobcode());
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		while(rst.next()){
			
			if(rst.getString("candidate_final_status").equals("0")) {
				int candUnderpro=uF.parseToInt(hmCandUPHr.get(rst.getString("job_code")));
				candUnderpro++;
				hmCandUPHr.put(rst.getString("job_code"),""+candUnderpro);		
                 
    		}else if(rst.getString("candidate_final_status").equals("1")){
    			int candAccepted=uF.parseToInt(hmCandOfferedHr.get(rst.getString("job_code")));
    			candAccepted++;
    			hmCandOfferedHr.put(rst.getString("job_code"),""+candAccepted);
    			   			
    		}else if(rst.getString("candidate_final_status").equals("-1")){
    			int candRejected=uF.parseToInt(hmCandRejectHr.get(rst.getString("job_code")));
    			candRejected++;
    			hmCandRejectHr.put(rst.getString("job_code"),""+candRejected);	
    		}
			
			if(rst.getString("candidate_final_status").equals("1") && rst.getString("candidate_status").equals("1"))
			{
				int candRejected=uF.parseToInt(hmCandOfferAccepted.get(rst.getString("job_code")));
    			candRejected++;
    			hmCandOfferAccepted.put(rst.getString("job_code"),""+candRejected);
				
			}
			else if(rst.getString("candidate_final_status").equals("1") && rst.getString("candidate_status").equals("-1"))
			{
				int candRejected=uF.parseToInt(hmCandOfferRejected.get(rst.getString("job_code")));
    			candRejected++;
    			hmCandOfferRejected.put(rst.getString("job_code"),""+candRejected);
			}
			else if(rst.getString("candidate_final_status").equals("1") && rst.getString("candidate_status").equals("0"))
			{
				int candOfferUP=uF.parseToInt(hmCandOfferUP.get(rst.getString("job_code")));
				candOfferUP++;
    			hmCandOfferUP.put(rst.getString("job_code"),""+candOfferUP);
			}
			
			
			if(!hmCandRequired.keySet().contains(rst.getString("job_code"))){
			
				hmCandRequired.put(rst.getString("job_code"), rst.getString("no_position"));
			}
				
			int candTotal=uF.parseToInt(hmCandTotalSelected.get(rst.getString("job_code")));
			candTotal++;
			hmCandTotalSelected.put(rst.getString("job_code"),""+candTotal);
					
		}
		rst.close();
		pst.close();
	
		request.setAttribute("hmCandOfferRejected", hmCandOfferRejected);
		request.setAttribute("hmCandOfferAccepted", hmCandOfferAccepted);
		request.setAttribute("hmCandOfferUP", hmCandOfferUP);
		request.setAttribute("hmCandTotalSelected", hmCandTotalSelected);
	
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		
		db.closeResultSet(rst);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	}


private void prepareApplicationsData() {
	Connection con = null;
	Database db = new Database();
	db.setRequest(request);
	PreparedStatement pst = null;	
	ResultSet rst = null;
	
	
	Map<String,String> hmApplRejected=new HashMap<String, String>();
	Map<String,String> hmApplSelected=new HashMap<String, String>();
	Map<String,String> hmApplTotal=new HashMap<String, String>();
	Map<String,String> hmRequired=new HashMap<String, String>();
	Map<String,String> hmApplUnderprocess=new HashMap<String, String>();
	
	try{
		con=db.makeConnection(con);
		pst=con.prepareStatement("select job_code,application_status from  candidate_personal_details where job_code=? ");
		pst.setString(1, getJobcode());
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		while(rst.next()){
			
			if(rst.getString("application_status").equals("0")) {
				int candUnderpro=uF.parseToInt(hmApplUnderprocess.get(rst.getString("job_code")));
				candUnderpro++;
    			hmApplUnderprocess.put(rst.getString("job_code"),""+candUnderpro);		
                 
    		}else if(rst.getString("application_status").equals("2")){
    			int candAccepted=uF.parseToInt(hmApplSelected.get(rst.getString("job_code")));
    			candAccepted++;
				hmApplSelected.put(rst.getString("job_code"),""+candAccepted);
    			
    		}else if(rst.getString("application_status").equals("-1")){
    			int candRejected=uF.parseToInt(hmApplRejected.get(rst.getString("job_code")));
    			candRejected++;
				hmApplRejected.put(rst.getString("job_code"),""+candRejected);
    		}
			
			int candTotal=uF.parseToInt(hmApplTotal.get(rst.getString("job_code")));
			candTotal++;
			hmApplTotal.put(rst.getString("job_code"),""+candTotal);
		}
		rst.close();
		pst.close();
		
		request.setAttribute("hmApplTotal", hmApplTotal);
		request.setAttribute("hmApplUnderprocess", hmApplUnderprocess);
		request.setAttribute("hmApplSelected", hmApplSelected);
		request.setAttribute("hmApplRejected", hmApplRejected);
		
	
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		
		db.closeResultSet(rst);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
		
	}

	String jobcode;
	
	public String getJobcode() {
	return jobcode;
	}


	public void setJobcode(String jobcode) {
	this.jobcode = jobcode;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;	
	}
	
}