package com.konnect.jpms.recruitment;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.employee.AddEmployeeMode;
import com.konnect.jpms.reports.MyProfile;
import com.konnect.jpms.salary.EmpSalaryApproval;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class OnboardingOnline extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static Logger log = Logger.getLogger(Onboarding.class);
	private static final long serialVersionUID = 1L;
	CommonFunctions CF ;
	HttpSession session;
//	String strUserType = null;
//	String strSessionEmpId = null;
	
	String candidateId;
	String recruitId;
	

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}

		
	public String execute() {
		
		session = request.getSession();
		CF = new CommonFunctions();
		CF.setRequest(request);
		if (CF == null) return LOGIN;
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		
//		strUserType = (String)session.getAttribute(USERTYPE);
//		strSessionEmpId = (String)session.getAttribute(EMPID);
		
//		System.out.println("Cadidate ID ===> "+candidateId);
		checkCandidateAsEmp();
//		setCandidatePersonaldetails();
//		setEmployeeOtherDetails();

	//	insertEmployeeData();

		return SUCCESS;
			
	}
	
	


	private void checkCandidateAsEmp() {
	
		UtilityFunctions uF=new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst =	null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			Map<String,String> hmEmpPersonal_details=new HashMap<String, String>(); 
			boolean flag = false;
			pst = con.prepareStatement("SELECT * FROM candidate_personal_details WHERE emp_per_id = ?");
			pst.setInt(1,uF.parseToInt(getCandidateId()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			if (rst.next()) {
				hmEmpPersonal_details.put("emp_email",rst.getString("emp_email"));
				hmEmpPersonal_details.put("emp_fname",rst.getString("emp_fname"));
				hmEmpPersonal_details.put("emp_lname",rst.getString("emp_lname"));
			}
			rst.close();
			pst.close();
			
			// Fetching employee id from epd*******
			pst=con.prepareStatement("select emp_per_id from employee_personal_details where emp_fname=? and emp_lname=? and emp_email=? order by emp_per_id");
			pst.setString(1, hmEmpPersonal_details.get("emp_fname"));
			pst.setString(2, hmEmpPersonal_details.get("emp_lname"));
			pst.setString(3, hmEmpPersonal_details.get("emp_email"));
//			System.out.println("printing employee_personal_details query==="+pst);
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
//				System.out.println("EMp ID ===> " + rs.getString("emp_per_id"));
				flag = true;
//			 setCandEmpId(rs.getString("emp_per_id"));
			}
			rst.close();
			pst.close();
			
			if(!flag) {
				
				pst=con.prepareStatement("select added_by,designation_id,grade_id,wlocation,org_id,dept_id,level_id,type_of_employment from recruitment_details where recruitment_id = ?");
				pst.setInt(1, uF.parseToInt(getRecruitId()));
				rst=pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
				if(rst.next()) {
				setSupervisor_emp_id(rst.getString("added_by"));
				setWlocation_id(rst.getString("wlocation"));
				setGrade_id(rst.getString("grade_id"));
				setDesignation_id(rst.getString("designation_id"));
				setOrg_id(rst.getString("org_id"));
				setDepart_id(rst.getString("dept_id"));
				setLevel_id(rst.getString("level_id"));
				setType_of_emlpoyment(rst.getString("type_of_employment"));
				}
				rst.close();
				pst.close();
				
				setCandidatePersonaldetails();
				setEmployeeOtherDetails();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void setEmployeeOtherDetails() {
	
		UtilityFunctions uF=new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst =	null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			
			// for multiple prev employment........FETCHING 
			
            Map<String,Map<String,String>> hmPrevEmp=new HashMap<String, Map<String,String>>();
            List<String> comList=new ArrayList<String>();
			pst=con.prepareStatement("select * from candidate_prev_employment where emp_id=?");
			pst.setInt(1,uF.parseToInt(getCandidateId()));

			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				
				Map<String,String> innermap=new HashMap<String, String>();
				innermap.put("company_name", rst.getString("company_name"));
				innermap.put("company_location", rst.getString("company_location"));
				innermap.put("company_city", rst.getString("company_city"));
				innermap.put("company_state", rst.getString("company_state"));
				innermap.put("company_country", rst.getString("company_country"));
				innermap.put("company_contact_no", rst.getString("company_contact_no"));
				innermap.put("reporting_to", rst.getString("reporting_to"));
				innermap.put("report_manager_ph_no", rst.getString("report_manager_ph_no"));
				innermap.put("hr_manager", rst.getString("hr_manager"));
				innermap.put("hr_manager_ph_no", rst.getString("hr_manager_ph_no"));
				innermap.put("from_date", rst.getString("from_date"));
				innermap.put("to_date", rst.getString("to_date"));	
				innermap.put("designation", rst.getString("designation"));
				innermap.put("responsibilities", rst.getString("responsibilities"));
				innermap.put("skills", rst.getString("skills"));
				
				hmPrevEmp.put(rst.getString("company_id"), innermap);
				comList.add(rst.getString("company_id"));

			}
			rst.close();
			pst.close();
			
			
		// inserting emp prev data************
			
//			System.out.println("printin prev employment=="+hmPrevEmp);
			
				for(int i=0; i<comList.size(); i++) {					
					
				Map<String,String>	hminner=hmPrevEmp.get(comList.get(i));
					
				pst = con.prepareStatement("INSERT INTO emp_prev_employment(company_name, company_location, company_city, company_state, " +
						"company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, emp_id, " +
						"report_manager_ph_no, hr_manager, hr_manager_ph_no)" +
						"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
				
				pst.setString(1, hminner.get("company_name"));
				pst.setString(2, hminner.get("company_location"));
				pst.setString(3, hminner.get("company_city"));
				pst.setString(4,  hminner.get("company_state"));
				pst.setString(5, hminner.get("company_country"));
				pst.setString(6, hminner.get("company_contact_no"));
				pst.setString(7, hminner.get("reporting_to"));
				pst.setDate(8, uF.getDateFormat(hminner.get("from_date"), DBDATE));
				pst.setDate(9, uF.getDateFormat(hminner.get("to_date"), DBDATE));
				pst.setString(10, hminner.get("designation"));
				pst.setString(11, hminner.get("responsibilities"));
				pst.setString(12, hminner.get("skills"));
				pst.setInt(13,uF.parseToInt(getCandEmpId()));
				pst.setString(14, hminner.get("report_manager_ph_no"));
				pst.setString(15, hminner.get("hr_manager"));
				pst.setString(16, hminner.get("hr_manager_ph_no"));
				log.debug("pst=>"+pst);
//				System.out.println("printing query==="+pst);
				pst.execute();
				pst.close();
				}
			
			
			// for Family INFO.......FETCHING 
			
		    Map<String,Map<String,String>> hmFamily=new HashMap<String, Map<String,String>>();
			pst=con.prepareStatement("select * from candidate_family_members where emp_id=?");
			pst.setInt(1,uF.parseToInt(getCandidateId()));
			int cnt=1;
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				
				Map<String,String> innermap=new HashMap<String, String>();
				innermap.put("member_name", rst.getString("member_name"));
				innermap.put("member_dob", rst.getString("member_dob"));
				innermap.put("member_education", rst.getString("member_education"));
				innermap.put("member_occupation", rst.getString("member_occupation"));
				innermap.put("member_contact_no", rst.getString("member_contact_no"));
				innermap.put("member_email_id", rst.getString("member_email_id"));
				innermap.put("member_gender", rst.getString("member_gender"));
				innermap.put("member_type", rst.getString("member_type"));

				if(rst.getString("member_type").equalsIgnoreCase("MOTHER"))
				hmFamily.put("MOTHER", innermap);
				else if(rst.getString("member_type").equalsIgnoreCase("FATHER"))
					hmFamily.put("FATHER", innermap);
				else if(rst.getString("member_type").equalsIgnoreCase("SPOUSE"))
					hmFamily.put("SPOUSE", innermap);
				else{
					
					hmFamily.put("SIBLING"+cnt, innermap);
					cnt++;
				}
				}
			rst.close();
			pst.close();
			
//			System.out.println("printing hm family.........."+hmFamily);
			
			// inserting Family members INFORMATION ***********************
			

				List<String> alFamilyKey=new ArrayList<String>();
				Iterator<String> itr=hmFamily.keySet().iterator();
				while(itr.hasNext()){
					alFamilyKey.add(itr.next());
				}
				for(int i=0; i<hmFamily.size();i++) {
					
					Map<String,String> innerFamily=hmFamily.get(alFamilyKey.get(i));
					
					pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_dob, member_education, " +
							"member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" +
								"VALUES (?,?,?,?,?,?,?,?,?)");
	
			        if(alFamilyKey.get(i).indexOf("SIBLING")==0)
			        	pst.setString(1, alFamilyKey.get(i).substring(0,alFamilyKey.get(i).length()-1));
			        else
			        	pst.setString(1, alFamilyKey.get(i));
					
			        pst.setString(2, innerFamily.get("member_name"));
					pst.setDate(3, uF.getDateFormat(innerFamily.get("member_dob"), DBDATE));
					pst.setString(4, innerFamily.get("member_education"));
					pst.setString(5,innerFamily.get("member_occupation"));
					pst.setString(6, innerFamily.get("member_contact_no"));
					pst.setString(7, innerFamily.get("member_email_id"));
					pst.setString(8,innerFamily.get("member_gender"));
					pst.setInt(9,uF.parseToInt(getCandEmpId()));
					log.debug("pst=>"+pst);
//					System.out.println("printing query==="+pst);
					pst.execute();
					pst.close();
				}
				

			// for multiple Skills........FETCHING 
			
		    Map<String,Map<String,String>> hmSkills=new HashMap<String, Map<String,String>>();
		    List<String> skillsList=new ArrayList<String>();
			pst=con.prepareStatement("select * from candidate_skills_description where emp_id=?");
			pst.setInt(1,uF.parseToInt(getCandidateId()));

			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				
				Map<String,String> innermap=new HashMap<String, String>();
//				innermap.put("skills_name", rs.getString("skills_name"));
				innermap.put("skills_value", rst.getString("skills_value"));
				innermap.put("skill_id", rst.getString("skill_id"));
				hmSkills.put(rst.getString("skills_id"), innermap);
				skillsList.add(rst.getString("skills_id"));
			}
			rst.close();
			pst.close();
			
//			System.out.println("printing hmSkills ===> "+hmSkills);
			// INSERTING EDUCATION  *************************************
			
			for(int i=0; i<skillsList.size(); i++) {
					Map<String,String> hminner=hmSkills.get(skillsList.get(i));
					pst = con.prepareStatement("INSERT INTO skills_description(skill_id, skills_value, emp_id)" +
										"VALUES (?,?,?)");
					pst.setInt(1, uF.parseToInt(hminner.get("skill_id")));
					pst.setInt(2, uF.parseToInt(hminner.get("skills_value")));
					pst.setInt(3,uF.parseToInt(getCandEmpId()));
					log.debug("pst=>"+pst);
//					System.out.println("printing skills_description==="+pst);
					pst.execute();
					pst.close();
			}
			
			
	// for multiple Hobbies........FETCHING 
			
		    Map<String,Map<String,String>> hmHobbies=new HashMap<String, Map<String,String>>();
		    List<String> hobbiesList=new ArrayList<String>();
			pst=con.prepareStatement("select * from candidate_hobbies_details where emp_id=?");
			pst.setInt(1,uF.parseToInt(getCandidateId()));

			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				Map<String,String> innermap=new HashMap<String, String>();
				innermap.put("hobbies_name", rst.getString("hobbies_name"));
				hmHobbies.put(rst.getString("hobbies_id"), innermap);
				hobbiesList.add(rst.getString("hobbies_id"));
			}
			rst.close();
			pst.close();
			
//			System.out.println("printing hmHobbies ===> "+hmHobbies);
			// INSERTING EDUCATION  *************************************
			
			for(int i=0; i<hobbiesList.size(); i++) {
					Map<String,String> hminner=hmHobbies.get(hobbiesList.get(i));
					pst = con.prepareStatement("INSERT INTO hobbies_details(hobbies_name, emp_id)" +
										"VALUES (?,?)");
					pst.setString(1, hminner.get("hobbies_name"));
					pst.setInt(2,uF.parseToInt(getCandEmpId()));
					log.debug("pst=>"+pst);
//					System.out.println("printing hobbies_details==="+pst);
					pst.execute();
					pst.close();
			}

	
	// for multiple Educatin........FETCHING 
	
    Map<String,Map<String,String>> hmEducation=new HashMap<String, Map<String,String>>();
    List<String> eduList=new ArrayList<String>();
	pst=con.prepareStatement("select * from candidate_education_details where emp_id=?");
	pst.setInt(1,uF.parseToInt(getCandidateId()));

	rst=pst.executeQuery();
//	System.out.println("new Date ===> " + new Date());
	while(rst.next()){
		
		Map<String,String> innermap=new HashMap<String, String>();
//		innermap.put("degree_name", rs.getString("degree_name"));
		innermap.put("degree_duration", rst.getString("degree_duration"));
		innermap.put("completion_year", rst.getString("completion_year"));
		innermap.put("education_id", rst.getString("education_id"));
		innermap.put("grade", rst.getString("grade"));
		
		hmEducation.put(rst.getString("degree_id"), innermap);
		eduList.add(rst.getString("degree_id"));
	}
	rst.close();
	pst.close();
	
//	System.out.println("printing education"+hmEducation);
	// INSERTING EDUCATION  *************************************
	
	for(int i=0; i<eduList.size(); i++) {
		
			Map<String,String> hminnerEdu=hmEducation.get(eduList.get(i));
			pst = con.prepareStatement("INSERT INTO education_details(education_id, degree_duration, completion_year, grade, emp_id)" +
								"VALUES (?,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(hminnerEdu.get("education_id")));
			pst.setInt(2, uF.parseToInt(hminnerEdu.get("degree_duration")));
			pst.setInt(3, uF.parseToInt(hminnerEdu.get("completion_year")));
			pst.setString(4, hminnerEdu.get("grade"));
			pst.setInt(5,uF.parseToInt(getCandEmpId()));
			log.debug("pst=>"+pst);
//			System.out.println("printing query==="+pst);
			pst.execute();
			pst.close();
	}
	
	
	// for multiple Language........FETCHING 
	
    Map<String,Map<String,String>> hmLanguage=new HashMap<String, Map<String,String>>();
	pst=con.prepareStatement("select * from candidate_languages_details where emp_id=?");
	pst.setInt(1,uF.parseToInt(getCandidateId()));
    List<String> langList=new ArrayList<String>();
	rst=pst.executeQuery();
//	System.out.println("new Date ===> " + new Date());
	while(rst.next()){
		
		Map<String,String> innermap=new HashMap<String, String>();
		innermap.put("language_name", rst.getString("language_name"));
		innermap.put("language_read", rst.getString("language_read"));
		innermap.put("language_write", rst.getString("language_write"));
		innermap.put("language_speak", rst.getString("language_speak"));
		
		hmLanguage.put(rst.getString("language_id"), innermap);
	langList.add(rst.getString("language_id"));
	}
	rst.close();
	pst.close();
	
//	System.out.println("printing language=="+hmLanguage);
	// INSERTING LANGUAGE  *************************************
	
	
	for(int i=0; i<langList.size(); i++) {
		
			Map<String,String> hminnerlang=hmLanguage.get(langList.get(i));
			
			pst = con.prepareStatement("INSERT INTO languages_details(language_name, language_read, language_write, language_speak, emp_id)" +
									"VALUES (?,?,?,?,?)");
			pst.setString(1, hminnerlang.get("language_name"));
			pst.setInt(2, uF.parseToInt(hminnerlang.get("language_read")));
			pst.setInt(3, uF.parseToInt(hminnerlang.get("language_write")));
			pst.setInt(4, uF.parseToInt(hminnerlang.get("language_speak")));
			pst.setInt(5, uF.parseToInt(getCandEmpId()));
			log.debug("pst=>"+pst);
//			System.out.println("printing query==="+pst);
			pst.execute();
			pst.close();
	}
	
	
	// for Refrances........FETCHING 
	
    Map<String,Map<String,String>> hmRefrances=new HashMap<String, Map<String,String>>();
	pst=con.prepareStatement("select * from candidate_references where emp_id=?");
	pst.setInt(1,uF.parseToInt(getCandidateId()));
	   List<String> refList=new ArrayList<String>();
	rst=pst.executeQuery();
//	System.out.println("new Date ===> " + new Date());
	while(rst.next()){
		
		Map<String,String> innermap=new HashMap<String, String>();
		innermap.put("ref_name", rst.getString("ref_name"));
		innermap.put("ref_company", rst.getString("ref_company"));
		innermap.put("ref_designation", rst.getString("ref_designation"));
		innermap.put("ref_contact_no", rst.getString("ref_contact_no"));
		innermap.put("ref_email_id", rst.getString("ref_email_id"));

		hmRefrances.put(rst.getString("ref_id"), innermap);
	refList.add(rst.getString("ref_id"));
	}
	rst.close();
	pst.close();
	
//	System.out.println("printing refrances======"+hmRefrances);
	
	// INSERTING EMPLOYEE REFRANCES  *************************************	
		
	for(int i=0;i<refList.size(); i++) {
		
			Map<String,String> hminnerRef=hmRefrances.get(refList.get(i));
			
			pst = con.prepareStatement("INSERT INTO emp_references (ref_name, ref_company, ref_designation, ref_contact_no, ref_email_id, emp_id) " +
				"values(?,?,?,?,?,?)");
			pst.setString(1, hminnerRef.get("ref_name"));
			pst.setString(2, hminnerRef.get("ref_company"));
			pst.setString(3, hminnerRef.get("ref_designation"));
			pst.setString(4, hminnerRef.get("ref_contact_no"));
			pst.setString(5, hminnerRef.get("ref_email_id"));
			pst.setInt(6, uF.parseToInt(getCandEmpId()));
			
			log.debug("pst==>"+pst);
	//		System.out.println("printing query==="+pst);
			pst.execute();
			pst.close();
	
		}
		
		
	// for Refrances........FETCHING 
	
    Map<String,Map<String,String>> hmMedicalQues=new HashMap<String, Map<String,String>>();
	pst=con.prepareStatement("select * from candidate_medical_details where emp_id=?");
	pst.setInt(1,uF.parseToInt(getCandidateId()));
	   List<String> medList=new ArrayList<String>();
	rst=pst.executeQuery();
//	System.out.println("new Date ===> " + new Date());
	while(rst.next()){
		
		Map<String,String> innermap=new HashMap<String, String>();
		innermap.put("question_id", rst.getString("question_id"));
		innermap.put("yes_no", rst.getString("yes_no"));
		innermap.put("description", rst.getString("description"));


		hmMedicalQues.put(rst.getString("medical_id"), innermap);
		medList.add(rst.getString("medical_id"));
		
	}
	rst.close();
	pst.close();
	
//	System.out.println("printing medical questions===="+hmMedicalQues);
	
		// INSERTING MEDICAL DETAILS  *************************************
	
	for(int i=0;  i<medList.size(); i++) {

			Map<String,String> innerMed=hmMedicalQues.get(medList.get(i));
			
			pst = con.prepareStatement("INSERT INTO emp_medical_details (question_id, emp_id, yes_no, description) values (?,?,?,?)");
			pst.setInt(1, uF.parseToInt(innerMed.get("")));
			pst.setInt(2, uF.parseToInt(getCandEmpId()));
			pst.setBoolean(3,uF.parseToBoolean(innerMed.get("yes_no")));
			pst.setString(4, innerMed.get("description"));
			log.debug("pst ==>"+pst);
	//		System.out.println("printing query==="+pst);
			pst.execute();
			pst.close();
		}
				
		
	
	//fetching data for employee_official_details
	
			pst = con.prepareStatement("select is_disable_sal_calculate from candidate_application_details join recruitment_details using (recruitment_id) where candidate_id= ? and recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rst = pst.executeQuery();
			boolean disableSalaryStructure = false;
			if (rst.next()) {
				disableSalaryStructure = uF.parseToBoolean(rst.getString("is_disable_sal_calculate"));
			}
			rst.close();
			pst.close();
				
		// updating employee_official_details ******************
		
			pst=con.prepareStatement("INSERT INTO employee_official_details(depart_id, supervisor_emp_id, wlocation_id, grade_id, emp_id ,org_id, " +
				"emptype,is_disable_sal_calculate) values(?,?,?,?, ?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getDepart_id()));
			pst.setInt(2, uF.parseToInt(getSupervisor_emp_id()));
			pst.setInt(3, uF.parseToInt(getWlocation_id()));
			pst.setInt(4, uF.parseToInt(getGrade_id()));
			pst.setInt(5, uF.parseToInt(getCandEmpId()));
			pst.setInt(6, uF.parseToInt(getOrg_id()));
			pst.setString(7, getType_of_emlpoyment());
			pst.setBoolean(8, disableSalaryStructure);
			pst.execute();
			pst.close();	
		
		} catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	String depart_id;
	public String getDepart_id() {
		return depart_id;
	}

	public void setDepart_id(String depart_id) {
		this.depart_id = depart_id;
	}

	public String getSupervisor_emp_id() {
		return supervisor_emp_id;
	}

	public void setSupervisor_emp_id(String supervisor_emp_id) {
		this.supervisor_emp_id = supervisor_emp_id;
	}

	public String getWlocation_id() {
		return wlocation_id;
	}



	public void setWlocation_id(String wlocation_id) {
		this.wlocation_id = wlocation_id;
	}

	public String getGrade_id() {
		return grade_id;
	}

	public void setGrade_id(String grade_id) {
		this.grade_id = grade_id;
	}

	public String getDesignation_id() {
		return designation_id;
	}

	public void setDesignation_id(String designation_id) {
		this.designation_id = designation_id;
	}

	public String getOrg_id() {
		return org_id;
	}

	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}

	public String getDept_id() {
		return dept_id;
	}

	public void setDept_id(String dept_id) {
		this.dept_id = dept_id;
	}

	public String getLevel_id() {
		return level_id;
	}

	public void setLevel_id(String level_id) {
		this.level_id = level_id;
	}

	public String getType_of_emlpoyment() {
		return type_of_emlpoyment;
	}

	public void setType_of_emlpoyment(String type_of_emlpoyment) {
		this.type_of_emlpoyment = type_of_emlpoyment;
	}

	String supervisor_emp_id;
	String wlocation_id;
	String grade_id;
	String designation_id;
	String org_id;
	String dept_id;
	String level_id;
	String type_of_emlpoyment;


	private void setCandidatePersonaldetails() {
	
		UtilityFunctions uF=new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst =	null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String,String> hmEmpPersonal_details=new HashMap<String, String>(); 

			// selecting candidate all personal detailss.............
			
//			pst = con.prepareStatement("SELECT * FROM candidate_personal_details WHERE emp_per_id = ?");
			pst = con.prepareStatement("SELECT cad.candidate_joining_date as join_date,* FROM candidate_personal_details cpd, candidate_application_details cad WHERE cpd.emp_per_id = ? and cad.recruitment_id = ? and cpd.emp_per_id = cad.candidate_id");
			pst.setInt(1,uF.parseToInt(getCandidateId()));
			pst.setInt(2,uF.parseToInt(getRecruitId()));
			log.debug("pst selectEmployeeR1V==>"+pst);
//			System.out.println("pst candidate_personal_details==>"+pst);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			if (rst.next()) {
				hmEmpPersonal_details.put("salutation", rst.getString("salutation"));
				hmEmpPersonal_details.put("emp_email", rst.getString("emp_email"));
				hmEmpPersonal_details.put("emp_fname", rst.getString("emp_fname"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
						strEmpMName = rst.getString("emp_mname");
					}
				}
				
				hmEmpPersonal_details.put("emp_mname",strEmpMName);
				hmEmpPersonal_details.put("emp_lname", rst.getString("emp_lname"));
				hmEmpPersonal_details.put("emp_image", rst.getString("emp_image"));
				hmEmpPersonal_details.put("emp_address1", rst.getString("emp_address1"));
				hmEmpPersonal_details.put("emp_address2", rst.getString("emp_address2"));
				hmEmpPersonal_details.put("emp_city_id", rst.getString("emp_city_id"));
				hmEmpPersonal_details.put("emp_state_id", rst.getString("emp_state_id"));
				hmEmpPersonal_details.put("emp_country_id", rst.getString("emp_country_id"));
				hmEmpPersonal_details.put("emp_pincode", rst.getString("emp_pincode"));
				hmEmpPersonal_details.put("emp_address1_tmp", rst.getString("emp_address1_tmp"));
				hmEmpPersonal_details.put("emp_address2_tmp", rst.getString("emp_address2_tmp"));
				hmEmpPersonal_details.put("emp_city_id_tmp", rst.getString("emp_city_id_tmp"));
				hmEmpPersonal_details.put("emp_state_id_tmp", rst.getString("emp_state_id_tmp"));
				hmEmpPersonal_details.put("emp_country_id_tmp", rst.getString("emp_country_id_tmp"));
				hmEmpPersonal_details.put("emp_pincode_tmp", rst.getString("emp_pincode_tmp"));
				hmEmpPersonal_details.put("emp_contactno", rst.getString("emp_contactno"));
				hmEmpPersonal_details.put("emp_pan_no", rst.getString("emp_pan_no"));
				hmEmpPersonal_details.put("emp_pf_no", rst.getString("emp_pf_no"));
				hmEmpPersonal_details.put("emp_gender", rst.getString("emp_gender"));
				hmEmpPersonal_details.put("emp_date_of_birth", rst.getString("emp_date_of_birth"));
				hmEmpPersonal_details.put("emp_date_of_marriage", rst.getString("emp_date_of_marriage"));
				hmEmpPersonal_details.put("emp_bank_name", rst.getString("emp_bank_name"));
				hmEmpPersonal_details.put("emp_bank_acct_nbr", rst.getString("emp_bank_acct_nbr"));
				hmEmpPersonal_details.put("emp_email_sec", rst.getString("emp_email_sec"));
				hmEmpPersonal_details.put("skype_id", rst.getString("skype_id"));
				hmEmpPersonal_details.put("emp_contactno_mob", rst.getString("emp_contactno_mob"));
				hmEmpPersonal_details.put("emergency_contact_name", rst.getString("emergency_contact_name"));
				hmEmpPersonal_details.put("emergency_contact_no", rst.getString("emergency_contact_no"));
				hmEmpPersonal_details.put("passport_no", rst.getString("passport_no"));
				hmEmpPersonal_details.put("passport_expiry_date", rst.getString("passport_expiry_date"));
				hmEmpPersonal_details.put("blood_group", rst.getString("blood_group"));
				hmEmpPersonal_details.put("marital_status", rst.getString("marital_status"));
				hmEmpPersonal_details.put("joining_date", rst.getString("join_date"));

			}
			rst.close();
			pst.close();
			
//			System.out.println("Joining Date Online===> " + hmEmpPersonal_details.get("joining_date"));
			
			String query="insert into employee_personal_details(emp_fname,emp_lname,emp_address1,emp_address2,emp_state_id,emp_country_id," +
				"emp_pincode,emp_contactno,emp_image,emp_email,joining_date,emp_city_id,emp_pan_no,emp_gender,emp_date_of_birth," +
				"emp_bank_name,emp_bank_acct_nbr,emp_email_sec,skype_id ,emp_contactno_mob,emergency_contact_name,emergency_contact_no," +
				"passport_no,passport_expiry_date,blood_group,marital_status,emp_pf_no,emp_gpf_no,emp_date_of_marriage,emp_address1_tmp," +
				"emp_address2_tmp,emp_city_id_tmp,emp_state_id_tmp,emp_country_id_tmp,emp_pincode_tmp,empcode,approved_flag,emp_mname," +
				"salutation,emp_status)"+ //added_by, 
				"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)";
			pst=con.prepareStatement(query);
						
			pst.setString(1,hmEmpPersonal_details.get("emp_fname"));
			pst.setString(2,hmEmpPersonal_details.get("emp_lname"));
			pst.setString(3,hmEmpPersonal_details.get("emp_address1"));
			pst.setString(4,hmEmpPersonal_details.get("emp_address2"));
			
			pst.setInt(5,uF.parseToInt(hmEmpPersonal_details.get("emp_state_id")));
			pst.setInt(6,uF.parseToInt(hmEmpPersonal_details.get("emp_country_id")));
			
			pst.setString(7,hmEmpPersonal_details.get("emp_pincode"));
			pst.setString(8,hmEmpPersonal_details.get("emp_contactno"));
			
			pst.setString(9,hmEmpPersonal_details.get("emp_image"));
			pst.setString(10,hmEmpPersonal_details.get("emp_email"));
			
			pst.setDate(11,uF.getDateFormat(hmEmpPersonal_details.get("joining_date"), DBDATE));
			
			pst.setString(12,hmEmpPersonal_details.get("emp_city_id"));
			
			pst.setString(13,hmEmpPersonal_details.get("emp_pan_no"));
			pst.setString(14,hmEmpPersonal_details.get("emp_gender"));
			
			pst.setDate(15,uF.getDateFormat(hmEmpPersonal_details.get("emp_date_of_birth"), DBDATE));
			
			pst.setString(16,hmEmpPersonal_details.get("emp_bank_name"));
			pst.setString(17,hmEmpPersonal_details.get("emp_bank_acct_nbr"));
			pst.setString(18,hmEmpPersonal_details.get("emp_email_sec"));
			pst.setString(19,hmEmpPersonal_details.get("skype_id"));
			pst.setString(20,hmEmpPersonal_details.get("emp_contactno_mob"));
			pst.setString(21,hmEmpPersonal_details.get("emergency_contact_name"));
			pst.setString(22,hmEmpPersonal_details.get("emergency_contact_no"));
			pst.setString(23,hmEmpPersonal_details.get("passport_no"));
			
			pst.setDate(24,uF.getDateFormat(hmEmpPersonal_details.get("passport_expiry_date"), DBDATE));

			pst.setString(25,hmEmpPersonal_details.get("blood_group"));
			pst.setString(26,hmEmpPersonal_details.get("marital_status"));
			pst.setString(27,hmEmpPersonal_details.get("emp_pf_no"));
			pst.setString(28,hmEmpPersonal_details.get("emp_gpf_no"));
			
			pst.setDate(29,uF.getDateFormat(hmEmpPersonal_details.get("emp_date_of_marriage"), DBDATE));

			pst.setString(30,hmEmpPersonal_details.get("emp_address1_tmp"));
			pst.setString(31,hmEmpPersonal_details.get("emp_address2_tmp"));
			pst.setString(32,hmEmpPersonal_details.get("emp_city_id_tmp"));
			pst.setInt(33,uF.parseToInt(hmEmpPersonal_details.get("emp_state_id_tmp")));
			pst.setInt(34,uF.parseToInt(hmEmpPersonal_details.get("emp_country_id_tmp")));
			pst.setString(35,hmEmpPersonal_details.get("emp_pincode_tmp"));
//			pst.setInt(36,uF.parseToInt(strSessionEmpId));
			pst.setString(36,"");
			pst.setBoolean(37, false);
			pst.setString(38,hmEmpPersonal_details.get("emp_mname"));
			pst.setString(39,hmEmpPersonal_details.get("salutation"));
			pst.setString(40, PROBATION);
//			System.out.println("insert pst ===="+pst);
			pst.execute();
			pst.close();
			
			// Fetching employee id from epd*******
			
			pst=con.prepareStatement("select emp_per_id from employee_personal_details where emp_fname=? and emp_lname=? and emp_email=? order by emp_per_id");
			pst.setString(1, hmEmpPersonal_details.get("emp_fname"));
			pst.setString(2, hmEmpPersonal_details.get("emp_lname"));
			pst.setString(3, hmEmpPersonal_details.get("emp_email"));
//			System.out.println("printing employee_personal_details query==="+pst);
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
//				System.out.println("EMp ID ===> " + rs.getString("emp_per_id"));
			 setCandEmpId(rst.getString("emp_per_id"));
			}
			rst.close();
			pst.close();
			
			insertUser(con, uF, uF.parseToInt(getCandEmpId()), hmEmpPersonal_details.get("emp_fname"), hmEmpPersonal_details.get("emp_lname"));
			
			insertSalaryDetails(con, uF, uF.parseToInt(getCandEmpId()), hmEmpPersonal_details.get("joining_date"));
			
			// Inserting candidate employee id into candidate personal details**********
			
			pst=con.prepareStatement("update candidate_personal_details set candididate_emp_id=? where emp_per_id=?");
			pst.setInt(1,uF.parseToInt(getCandEmpId()));
			pst.setInt(2,uF.parseToInt(getCandidateId()));
			pst.execute();
			pst.close();
			
			pst=con.prepareStatement("update candidate_application_details set candididate_emp_id=? where candidate_id=? and recruitment_id=?");
			pst.setInt(1,uF.parseToInt(getCandEmpId()));
			pst.setInt(2,uF.parseToInt(getCandidateId()));
			pst.setInt(3,uF.parseToInt(getRecruitId()));
//			System.out.println("candidate_application_details query==="+pst);
			pst.execute();
			pst.close();
			
			pst=con.prepareStatement("select added_by,approved_by from recruitment_details where recruitment_id=?");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			rst=pst.executeQuery();
			List<String> empIdList = new ArrayList<String>();
			while(rst.next()){
			 empIdList.add(rst.getString("added_by"));
			 empIdList.add(rst.getString("approved_by"));
			}
			rst.close();
			pst.close();
			
			for (int i = 0; empIdList != null && !empIdList.isEmpty() && i < empIdList.size(); i++) {
				if(empIdList.get(i) != null && !empIdList.get(i).equals("")) {
					String strDomain = request.getServerName().split("\\.")[0];
					Map<String, String> hmEmpUsertypeId = CF.getEmployeeIdUserTypeIdMap(con);
					String alertData = "<div style=\"float: left;\"> <b>"+CF.getEmpNameMapByEmpId(con, getCandEmpId()+"")+"</b> New Joinee Pending.</div>";
					String alertAction = "EmployeeActivity.action?pType=WR";
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(empIdList.get(i));
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(hmEmpUsertypeId.get(empIdList.get(i)));
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
//					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//					userAlerts.setStrDomain(strDomain);
//					userAlerts.setStrEmpId(empIdList.get(i));
//					userAlerts.set_type(NEW_JOINEE_PENDING_ALERT);
//					userAlerts.setStatus(INSERT_ALERT);
//					Thread t = new Thread(userAlerts);
//					t.run();
				}
			}
			
			pst=con.prepareStatement("delete from candidate_activity_details where recruitment_id=? and candi_id=? and user_id=? and " +
					"activity_id = ?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getCandidateId()));
			pst.setInt(3, 0);
			pst.setInt(4, CANDI_ACTIVITY_ONBOARDING_ID);
			pst.executeUpdate();
			pst.close();
			
			pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			pst.setInt(2,uF.parseToInt(getCandidateId()));
			pst.setString(3, "Onboarding");
			pst.setInt(4,0);
			pst.setDate(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setInt(6, CANDI_ACTIVITY_ONBOARDING_ID);
			pst.execute();
			pst.close();
	
	
			addEmployeeInLearningPlan(con, uF, getCandEmpId());
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void addEmployeeInLearningPlan(Connection con, UtilityFunctions uF, String empId) {
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		try {
			pst=con.prepareStatement("select learning_plan_id from learning_plan_details where desig_id like '%,"+getDesignation_id()+",%'");
			List<List<String>> lPlanIdAndLearnerList = new ArrayList<List<String>>();
			rst=pst.executeQuery();
//			System.out.println("pst learning_plan_details ===> " + pst);
			while(rst.next()){
				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("learning_plan_id"));
				innerList.add(rst.getString("learner_ids"));
				lPlanIdAndLearnerList.add(innerList);
			}
			rst.close();
			pst.close();
			
			for(int i=0; lPlanIdAndLearnerList != null && i<lPlanIdAndLearnerList.size(); i++) {
				List<String> innerList = lPlanIdAndLearnerList.get(i);
				String learnersIds = "";
				if (innerList.get(1) != null && !innerList.get(1).equals("")) {
					learnersIds = innerList.get(1) + empId + ","; 
				} else {
					learnersIds = "," + empId + ",";
				}
				pst = con.prepareStatement("update learning_plan_details set learner_ids = ? where learning_plan_id = ?");
				pst.setString(1, learnersIds);
				pst.setInt(2, uF.parseToInt(innerList.get(0)));
				pst.executeUpdate();
				pst.close();
			}
			
		} catch (Exception e) {
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
	}

	
	private void insertSalaryDetails(Connection con, UtilityFunctions uF, int empID, String joinDate) {
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		try {
			Map<String,Map<String,String>> hmSalary = new HashMap<String, Map<String,String>>();
			pst=con.prepareStatement("select * from candidate_salary_details where emp_id=?");
			pst.setInt(1,uF.parseToInt(getCandidateId()));
		    List<String> salaryHeadList=new ArrayList<String>();
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				
				Map<String,String> innerMap = new HashMap<String, String>();
				innerMap.put("salary_head_id", rst.getString("salary_head_id"));
				innerMap.put("amount", rst.getString("amount"));
				innerMap.put("pay_type", rst.getString("pay_type"));
				innerMap.put("isdisplay", rst.getString("isdisplay"));
				innerMap.put("is_approved", rst.getString("is_approved"));
				innerMap.put("user_id", rst.getString("user_id"));
				innerMap.put("earning_deduction", rst.getString("earning_deduction"));
//				innerMap.put("salary_type", rs.getString("salary_type"));
				
				hmSalary.put(rst.getString("salary_head_id"), innerMap);
				salaryHeadList.add(rst.getString("salary_head_id"));
			}
			rst.close();
			pst.close();
			
//			System.out.println("printing language=="+hmLanguage);
			// INSERTING LANGUAGE  *************************************
			
			for(int i=0; salaryHeadList != null && i<salaryHeadList.size(); i++) {
				Map<String,String> hminnerSalary=hmSalary.get(salaryHeadList.get(i));
				pst = con.prepareStatement("INSERT INTO emp_salary_details(salary_head_id, amount, pay_type, isdisplay, is_approved, user_id, " +
						"earning_deduction, effective_date, entry_date, emp_id, service_id, approved_date, approved_by)" +
						"VALUES (?,?,?,? ,?,?,?,? ,?,?,?,? ,?)");
				pst.setInt(1, uF.parseToInt(hminnerSalary.get("salary_head_id")));
				pst.setDouble(2, uF.parseToDouble(hminnerSalary.get("amount")));
				pst.setString(3, hminnerSalary.get("pay_type"));
				pst.setBoolean(4, uF.parseToBoolean(hminnerSalary.get("isdisplay")));
				pst.setBoolean(5, true);
				pst.setInt(6, uF.parseToInt(hminnerSalary.get("user_id")));
				pst.setString(7, hminnerSalary.get("earning_deduction"));
				pst.setDate(8,uF.getDateFormat(joinDate, DBDATE));
				pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(10, empID);
				pst.setInt(11, 0);
				pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(13, empID);
				log.debug("pst=>"+pst);
//					System.out.println("printing query==="+pst);
				pst.execute();
				pst.close();
			}
			
			/**
			 * Calaculate CTC
			 * */
			Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, null, ""+empID);
			
			MyProfile myProfile = new MyProfile();
			myProfile.session = session;
			myProfile.request = request;
			myProfile.CF = CF;
			int intEmpIdReq = empID;
			myProfile.getSalaryHeadsforEmployee(con, uF, intEmpIdReq, hmEmpProfile);
			
			double grossAmount = 0.0d;
			double grossYearAmount = 0.0d;
			double deductAmount = 0.0d;
			double deductYearAmount = 0.0d;
			double netAmount = 0.0d;
			double netYearAmount = 0.0d;
			
			List<List<String>> salaryHeadDetailsList = (List<List<String>>) request.getAttribute("salaryHeadDetailsList");
			for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
				List<String> innerList = salaryHeadDetailsList.get(i);
				if(innerList.get(1).equals("E")) {
					grossAmount +=uF.parseToDouble(innerList.get(2));
					grossYearAmount +=uF.parseToDouble(innerList.get(3));
				} else if(innerList.get(1).equals("D")) {
					double dblDeductMonth = 0.0d;
					double dblDeductAnnual = 0.0d;
					if(uF.parseToInt(innerList.get(4)) == EMPLOYEE_ESI){
						dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
						dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
					} else if(uF.parseToInt(innerList.get(4)) == EMPLOYER_ESI){
						dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
						dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
					} else {
						dblDeductMonth += Math.round(uF.parseToDouble(innerList.get(2)));
						dblDeductAnnual += Math.round(uF.parseToDouble(innerList.get(3)));
					}
					deductAmount += dblDeductMonth;
					deductYearAmount += dblDeductAnnual;
				}
			}
			
			Map<String,String> hmContribution = (Map<String,String>) request.getAttribute("hmContribution");
			if(hmContribution == null) hmContribution = new HashMap<String, String>();
			double dblMonthContri = 0.0d;
			double dblAnnualContri = 0.0d;
			boolean isEPF = uF.parseToBoolean((String)request.getAttribute("isEPF"));
			boolean isESIC = uF.parseToBoolean((String)request.getAttribute("isESIC"));
			boolean isLWF = uF.parseToBoolean((String)request.getAttribute("isLWF"));
			if(isEPF || isESIC || isLWF){
				if(isEPF){
					double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
					double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
					dblMonthContri += dblEPFMonth;
					dblAnnualContri += dblEPFAnnual;
				}
				if(isESIC){
					double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
					double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
					dblMonthContri += dblESIMonth;
					dblAnnualContri += dblESIAnnual;
				}
				if(isLWF){
					double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
					double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
					dblMonthContri += dblLWFMonth;
					dblAnnualContri += dblLWFAnnual;
				}
			}
			
			double dblCTCMonthly = grossAmount + dblMonthContri;
			double dblCTCAnnualy = grossYearAmount + dblAnnualContri;
			
			List<List<String>> salaryAnnualVariableDetailsList = (List<List<String>>)request.getAttribute("salaryAnnualVariableDetailsList");
			if(salaryAnnualVariableDetailsList == null) salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
			int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
			if(nAnnualVariSize > 0){
				double grossAnnualAmount = 0.0d;
				double grossAnnualYearAmount = 0.0d;
				for(int i = 0; i < nAnnualVariSize; i++){
					List<String> innerList = salaryAnnualVariableDetailsList.get(i);
					double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
					double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
					grossAnnualAmount += dblEarnMonth;
					grossAnnualYearAmount += dblEarnAnnual;
				}
				dblCTCMonthly += grossAnnualAmount;
				dblCTCAnnualy += grossAnnualYearAmount;
			}
			
			netAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCMonthly));							 
			netYearAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCAnnualy));
            
			EmpSalaryApproval salaryApproval = new EmpSalaryApproval();
			salaryApproval.request = request;
			salaryApproval.session = session;
			salaryApproval.CF = CF;
			Map<String, String> hmPrevCTC = salaryApproval.getPrevCTCDetails(con, uF, ""+empID);
			if(hmPrevCTC == null) hmPrevCTC = new HashMap<String, String>();
			double dblIncrementMonthAmt = netAmount - uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC"));
			double dblIncrementAnnualAmt = netYearAmount - uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC"));
            
			pst = con.prepareStatement("update employee_official_details set month_ctc=?,annual_ctc=?,prev_month_ctc=?," +
					"prev_annual_ctc=?,incre_month_amount=?,incre_annual_amount=? where emp_id=?");
			pst.setDouble(1, netAmount);
			pst.setDouble(2, netYearAmount);
			pst.setDouble(3, uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC")));
			pst.setDouble(4, uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC")));
			pst.setDouble(5, dblIncrementMonthAmt);
			pst.setDouble(6, dblIncrementAnnualAmt);
			pst.setInt(7, empID);
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
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
	}

	
public void insertUser(Connection con, UtilityFunctions uF, int empId, String EmpFname, String EmpLname) {
		
		PreparedStatement pst = null;
		
		try {
			Map<String,String> userPresent= CF.getUsersMap(con);
			AddEmployeeMode aE = new AddEmployeeMode();
			aE.request = request;
			aE.session = session;
			aE.CF = CF;
			aE.setFname(EmpFname);
//			aE.setMname(getEmpMname());
			aE.setLname(EmpLname);
			String username = aE.getUserName(userPresent);
			
			SecureRandom random = new SecureRandom();
			String password = new BigInteger(130, random).toString(32).substring(5, 13);
			
			pst = con.prepareStatement(insertUser);
			
			pst.setString(1, username);
			pst.setString(2, password);
			pst.setInt(3, 3);
			pst.setInt(4, empId);
			pst.setString(5, "ACTIVE");
			pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally {
			
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
	}

	String candEmpId;

public String getCandEmpId() {
		return candEmpId;
	}

	public void setCandEmpId(String candEmpId) {
		this.candEmpId = candEmpId;
	}

	
HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		
     this.request=request;
		
	}

}
