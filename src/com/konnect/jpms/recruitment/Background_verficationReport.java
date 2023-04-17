package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.opensymphony.xwork2.ActionSupport;


public class Background_verficationReport extends ActionSupport implements ServletRequestAware, IStatements {
	HttpSession session;
	CommonFunctions CF = null; 

	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/recruitment/Background_verficationReport.jsp");
		request.setAttribute(TITLE, "Candidate Database");
		 getBackgroundVerificationDetails(uF);
		return SUCCESS;
	}
	
	private void getBackgroundVerificationDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String,String> hmJobCode = new HashMap<String,String>();
		Map<String,String> hmdesignationDetails = new HashMap<String,String>();
		Map<String,String> hmdesignation = new HashMap<String,String>();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from designation_details");
//			System.out.println("exp pst==>"+pst);
			rs=pst.executeQuery();
			while(rs.next()) {
				hmdesignation.put(rs.getString("designation_id"), rs.getString("designation_name"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from recruitment_details");
//			System.out.println("exp pst==>"+pst);
			rs=pst.executeQuery();
			while(rs.next()) {
				hmJobCode.put(rs.getString("recruitment_id"),rs.getString("job_code"));
				hmdesignationDetails.put(rs.getString("recruitment_id"), hmdesignation.get(rs.getString("designation_id")));
			}
			rs.close();
			pst.close();
			List<List<String>> al = new ArrayList<List<String>>();
			pst = con.prepareStatement("select cad.ctc_offered,cad.candidate_joining_date,emp_fname,emp_mname,emp_lname,cad.recruitment_id," +
				"emp_per_id,cad.candidate_status, cad.offer_backout_status from candidate_personal_details cpd, candidate_application_details cad "+
				" where cpd.emp_per_id = cad.candidate_id and cad.application_status=2 and cad.candidate_final_status=1");
			
			//System.out.println("exp pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmDetails = new HashMap<String,List<String>>();
			while(rs.next()) {
				List<String> alInner=new ArrayList<String>();
				alInner.add(rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
				alInner.add(rs.getString("candidate_joining_date"));
				alInner.add(hmJobCode.get(rs.getString("recruitment_id")));
				alInner.add(hmdesignationDetails.get(rs.getString("recruitment_id")));
				alInner.add(rs.getString("emp_per_id"));
				al.add(alInner);
			}
			rs.close();
			pst.close();
			
			Map<String,List<String>> hmDocumentsDetails = new HashMap<String,List<String>>();
			for(int i =0; i < al.size();i++){
				List<String> alInner1 = al.get(i);
				pst = con.prepareStatement("select * from candidate_documents_details cad where emp_id = ?");
				pst.setInt(1,uF.parseToInt(alInner1.get(4)));
				List<String> documentList = new ArrayList<String>();
//				System.out.println("pstt====>"+pst);
				rs = pst.executeQuery();
				while(rs.next())	{
					documentList.add(rs.getString("documents_name"));
				}
				hmDocumentsDetails.put(alInner1.get(4),documentList);

				
			}
			request.setAttribute("reportList", al);
			request.setAttribute("hmDocumentsDetails", hmDocumentsDetails);
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	

}