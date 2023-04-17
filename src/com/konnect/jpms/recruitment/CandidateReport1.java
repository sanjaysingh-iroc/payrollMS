package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import java.util.ArrayList;

import java.util.Date;
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

public class CandidateReport1 extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	String recruitId;
	
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
	}




	CommonFunctions CF = null;
	String strAction = null;
	UtilityFunctions uF = new UtilityFunctions();
	private static Logger log = Logger.getLogger(CandidateReport1.class);

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

		request.setAttribute(PAGE, "/jsp/recruitment/CandidateReport1.jsp");		
		request.setAttribute(TITLE, "Interview Candidate Database");
		
		checkEmployee();
		//viewEmployee(uF);
		return SUCCESS;

	
	
	
	}

	
	
	private String checkEmployee() {

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
				hmpanelname.put(rst.getString("recruitment_id"),uF.showData(getAppendData(con, rst.getString("panel_employee_id"), "empname"),""));
				hmreq_designation_name.put(rst.getString("recruitment_id"),rst.getString("designation_name"));
				hmreq_job_location.put(rst.getString("recruitment_id"),rst.getString("wlocation_name"));
			}
			rst.close();
			pst.close();
		
			 request.setAttribute("recruitId",getRecruitId());
			 request.setAttribute("hmreq_designation_name",hmreq_designation_name);
			 request.setAttribute("hmreq_job_location", hmreq_job_location);
		
			Map<String,Map<String,String>> hmInterviewStatus=new HashMap<String, Map<String,String>>();
		
			pst=con.prepareStatement("select status,panel_emp_id,job_code,candidate_id from candidate_interview_panel where panel_emp_id=? ");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){		
				Map<String,String> hmemp=hmInterviewStatus.get(rst.getString("job_code"));	
				if(hmemp==null)hmemp=new HashMap<String, String>();
				
				hmemp.put(rst.getString("candidate_id"),rst.getString("status"));
				hmInterviewStatus.put(rst.getString("job_code") ,hmemp);
			}
			rst.close();
			pst.close();
 
		pst=con.prepareStatement("select job_code from recruitment_details where recruitment_id=?");
		pst.setInt(1,uF.parseToInt(getRecruitId()));
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		while(rst.next()){
			setJobcode(rst.getString("job_code"));
		}
		rst.close();
		pst.close();
		
		List<String> alInner;
		List<List<String>> al = new ArrayList<List<String>>();
		String strquery="select recruitment_id,job_code,emp_fname,emp_mname,emp_lname,emp_per_id,to_date(emp_entry_date::text,'yyyy-MM-dd') as entry_date from candidate_personal_details where recruitment_id =?";
		
		// filter ***********
/*		
		if(checkStatus_reportfilter==null || checkStatus_reportfilter.equalsIgnoreCase("0")){
			
			strquery+="and application_status=0";
			
		}else if(checkStatus_reportfilter.equalsIgnoreCase("-1")){
			
			strquery+="and application_status=-1";
			
		}else if(checkStatus_reportfilter.equalsIgnoreCase("1")){	
		
			strquery+="and application_status=1";
		}
	 */

			pst=con.prepareStatement(strquery);
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				alInner = new ArrayList<String>();
//				setJobcode(rst.getString("job_code"));
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
				
			    String strEmpId = rst.getString("emp_per_id");			
				 
				if (strUserType != null &&  !strUserType.equalsIgnoreCase(HRMANAGER)) {
					alInner.add("<a class=\"factsheet\" href=\"CandidateMyProfile.action?empId="
							+ strEmpId +"&recruitId="+rst.getString("recruitment_id")+ "\" > </a>");
				}else{
					alInner.add("");
				}
				
				StringBuilder sbStatus = new StringBuilder();
				Map<String, String> hminnermap=hmInterviewStatus.get(rst.getString("job_code"));
				
				if( hminnermap!=null   && hminnermap.get(strEmpId)!=null) { 
					if(hminnermap.get(strEmpId).equalsIgnoreCase("1"))
					 /*sbStatus.append("<img src=\"images1/icons/approved.png\" title=\"Approved\" /> ");*/
						sbStatus.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					else if(hminnermap.get(strEmpId).equalsIgnoreCase("-1"))
					/*sbStatus.append("<img src=\"images1/icons/denied.png\" title=\"Denied\" />");*/
						sbStatus.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\" ></i>");
					else
					/*sbStatus.append(" <img src=\"images1/icons/pending.png\" title=\"Waiting for approval\" /> </a> ");*/
						sbStatus.append(" <i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for approval\"></i> ");
					
					alInner.add(sbStatus.toString());
				} else {
			
					/*sbStatus.append(" <img src=\"images1/icons/pending.png\" title=\"Waiting for approval\" /> </a> ");*/
					sbStatus.append(" <i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for approval\" ></i>");
					alInner.add(sbStatus.toString());
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
	
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);

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
	
}
