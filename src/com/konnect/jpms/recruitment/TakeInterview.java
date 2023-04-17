package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TakeInterview extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

/*	String recruitId;
	
	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	String jobid;
	public String getJobid() {
		return jobid;
	}

	public void setJobid(String jobid) {
		this.jobid = jobid;
	}*/

	CommonFunctions CF = null;
	String strAction = null;
	
	private static Logger log = Logger.getLogger(TakeInterview.class);

	String roundID;
	
	
	public String execute() throws Exception {
	
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(PAGE, "/jsp/recruitment/TakeInterview.jsp");		
		request.setAttribute(TITLE, "Interview Candidate Database");
		UtilityFunctions uF = new UtilityFunctions();
		
		checkEmployee(uF);
		//viewEmployee(uF);
		return SUCCESS;

	}

	
	
	private String checkEmployee(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);

		Map<String, String> hmpanelname=new HashMap<String, String>();
		Map<String, String> hmreq_designation_name=new HashMap<String, String>();
		Map<String, String> hmreq_job_location=new HashMap<String, String>();
		
		try {
			
			con=db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst=con.prepareStatement("Select panel_employee_id,designation_name,wlocation_name,recruitment_id from recruitment_details join designation_details using(designation_id) join work_location_info on(wlocation=wlocation_id) where job_approval_status=1");	
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				hmpanelname.put(rst.getString("recruitment_id"),uF.showData(getAppendData(con,rst.getString("panel_employee_id"), "empname"),""));
				hmreq_designation_name.put(rst.getString("recruitment_id"),rst.getString("designation_name"));
				hmreq_job_location.put(rst.getString("recruitment_id"),rst.getString("wlocation_name"));
			}
			rst.close();
			pst.close();
		

		Map<String,String> hmScheduledBy=new HashMap<String, String>();
		pst=con.prepareStatement("Select candidate_id,interview_date from  candidate_interview_panel join recruitment_details" +
				 " using (recruitment_id) where panel_round_id=? and close_job_status='f' ");
		pst.setInt(1,uF.parseToInt(getRoundID()));
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		while(rst.next()){
			hmScheduledBy.put(rst.getString("candidate_id"), uF.getDateFormat(rst.getString("interview_date"), DBDATE, DATE_FORMAT));
			
		}
		
		rst.close();
		pst.close();
		List<String> alInner;
		List<List<String>> al = new ArrayList<List<String>>();
		
			pst=con.prepareStatement("select * from candidate_personal_details join recruitment_details " +
					"using (recruitment_id) where panel_employee_id like '%,"+strSessionEmpId+",%' ");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				alInner = new ArrayList<String>();
//				setJobcode(rst.getString("job_code"));
				alInner.add(rst.getString("recruitment_id"));
				alInner.add(rst.getString("job_code"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rst.getString("emp_mname");
					}
				}
				
				alInner.add(rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
				alInner.add(hmreq_designation_name.get(rst.getString("recruitment_id")));
				alInner.add(hmreq_job_location.get(rst.getString("recruitment_id")));
			    alInner.add(uF.getDateFormat(rst.getString("entry_date"), DBDATE, DATE_FORMAT));
			    alInner.add(hmpanelname.get(rst.getString("recruitment_id")));
				alInner.add("<a class=\"factsheet\" href=\"CandidateMyProfile.action?CandID=" +rst.getString("emp_per_id")+ "&recruitId="+rst.getString("recruitment_id")+"\" > </a>");
				
				if(rst.getInt("application_status")==0)
				alInner.add("Application Pending");
				else{
					if(!hmScheduledBy.keySet().contains(rst.getString("emp_per_id")))
				alInner.add("<a  href=\"CandidateInterviewSchedule.action?recruitID="+rst.getString("recruitment_id")+"&candidateID="+rst.getString("emp_per_id")+"&panelEmpID="+strSessionEmpId+"&roundID="+getRoundID()+ "\" >Approve Date </a>");
					else
						alInner.add(hmScheduledBy.get(rst.getString("emp_per_id")));
				}
				al.add(alInner);
			}
			rst.close();
			pst.close();
			
			request.setAttribute("reportList", al);
		
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}

	String empName;

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}


	private String getAppendData(Connection con, String strID, String name) {
		StringBuilder sb = new StringBuilder();

		if (strID != null && !strID.equals("")) {
	
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);

			// location
			if (name.equals("empname")) {
				String[] temp = strID.split(",");

				for (int i = 1; i < temp.length; i++) {
					if (i == 1) {
						sb.append(hmEmpName.get(temp[i].trim()));
					} else {
						sb.append("," + hmEmpName.get(temp[i].trim()));
					}
				}
			}

			
		}
		return sb.toString();
	}

	String checkStatus_reportfilter;
	
	public String getCheckStatus_reportfilter() {
		return checkStatus_reportfilter;
	}

	public void setCheckStatus_reportfilter(String checkStatus_reportfilter) {
		this.checkStatus_reportfilter = checkStatus_reportfilter;
	}

	String jobcode;

	public String getJobcode() {
		return jobcode;
	}

	public void setJobcode(String jobcode) {
		this.jobcode = jobcode;
	}

	public String getRoundID() {
		return roundID;
	}

	public void setRoundID(String roundID) {
		this.roundID = roundID;
	}
	
}
