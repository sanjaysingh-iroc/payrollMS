package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SendOpenJobNotification extends ActionSupport implements ServletRequestAware, IStatements  {


	private static final long serialVersionUID = 1L;

	HttpSession session;
	public CommonFunctions CF;
	String strUserType = null;
	String strSessionEmpId = null;
	private String recruitId;
	
	private String emp_id;
	private String positions;
	private String job_code;
	private String Level_name;
	private String designation_name;
	private String grade_name;
	private String location_name;
	private String skills_name;
	private String services;
	private String min_exp;
	private String max_exp;
	private String min_education;
	private String fromPage;
	private String jobTitle;
	public String execute() throws Exception {
		
		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
			sendMail();
			
				
		if(getFromPage() != null && getFromPage().equals("A")) {
			return LOAD;
		}else {
			return SUCCESS;
		}
			
	}

	public void sendMail() {

		System.out.println("sendMail().....");
		Connection con = null;
		ResultSet rst = null, rst1 = null;
		PreparedStatement pst = null, pst1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
//			System.out.println("Req id is ========= "+getStrId());
			pst1 = con.prepareStatement("select panel_emp_id from panel_interview_details where recruitment_id = ? and panel_emp_id is not null");
			pst1.setInt(1, uF.parseToInt(getRecruitId()));
			rst1 = pst1.executeQuery();
			while (rst1.next()) {
				Map<String, String> hmEmpInner = hmEmpInfo.get(rst1.getString("panel_emp_id"));
//				panel_employee_id = rst.getString("panel_employee_id");
//			}
//			
	//			String tmpsltempids = panel_employee_id.substring(1, panel_employee_id.length()-1);
	//			List<String> selectedEmpIdsLst=Arrays.asList(tmpsltempids.split(","));
	//			for(int i=0; selectedEmpIdsLst!= null && i<selectedEmpIdsLst.size(); i++){
				if(rst1.getString("panel_emp_id") != null && !rst1.getString("panel_emp_id").equals("")){
					StringBuilder strQuery = new StringBuilder();
					//Created By Dattatray Date:05-10-21 Note : job title
					strQuery.append("select d.designation_code,d.designation_name,g.grade_code,g.grade_name,w.wlocation_name,r.no_position,"
						+ "r.job_code,r.added_by,l.level_code,l.level_name,r.skills,r.min_exp,r.max_exp,r.min_education,r.job_title from recruitment_details r," +
						"grades_details g,work_location_info w,designation_details d,employee_personal_details e,level_details l where r.grade_id=g.grade_id " +
						"and r.wlocation=w.wlocation_id and r.designation_id=d.designation_id and r.added_by=e.emp_per_id and r.level_id=l.level_id and r.recruitment_id=?");
					pst = con.prepareStatement(strQuery.toString());
					pst.setInt(1, uF.parseToInt(getRecruitId()));
					rst = pst.executeQuery();
					System.out.println("pst ===> " + pst);
					while (rst.next()) {
//						designation_name = "[" + rst.getString("designation_code") + "] "
//								+ rst.getString("designation_name");
						designation_name = rst.getString("designation_name");
						grade_name = "[" + rst.getString("grade_code") + "] " + rst.getString("grade_name");
						location_name = rst.getString("wlocation_name");
						positions = rst.getString("no_position");
						job_code = rst.getString("job_code");
						emp_id = rst.getString("added_by");
						Level_name = "[" + rst.getString("level_code") + "] " + rst.getString("level_name");
						skills_name = rst.getString("skills");
						min_exp = rst.getString("min_exp");
						max_exp = rst.getString("max_exp");
						min_education = rst.getString("min_education");
						jobTitle =  rst.getString("job_title");//Created By Dattatray Date:05-10-21
					}
					rst.close();
					pst.close();
					System.out.println("designation_name ===>> " + designation_name);
					
					String strDomain = request.getServerName().split("\\.")[0];
					Notifications nF = new Notifications(N_RECRUITMENT_APPROVAL, CF);
					nF.setDomain(strDomain);
					nF.request = request;
		//			System.out.println("Emp ID is ========= "+rst1.getString("panel_emp_id"));
					 nF.setStrEmpId(rst1.getString("panel_emp_id"));
					 nF.setStrHostAddress(CF.getStrEmailLocalHost());
					 nF.setStrHostPort(CF.getStrHostPort());
					 nF.setStrContextPath(request.getContextPath());
					
					 nF.setStrRecruitmentDesignation(designation_name);
					 nF.setStrRecruitmentGrade(grade_name);
					 nF.setStrRecruitmentLevel(Level_name);
					 nF.setStrRecruitmentPosition(positions);
					 nF.setStrRecruitmentWLocation(location_name);
					 nF.setStrRecruitmentProfile(services);
					 nF.setStrRecruitmentSkill(skills_name);
					 nF.setStrEmpFname(hmEmpInner.get("FNAME"));
					 nF.setStrEmpLname(hmEmpInner.get("LNAME"));
					 nF.setStrJobTitle(jobTitle);// Start Dattatray Date : 05-10-21
					 nF.setEmailTemplate(true);
					 
					 nF.sendNotifications();
				}
			}
			rst1.close();
			pst1.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeResultSet(rst1);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}
	}
	

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	
}