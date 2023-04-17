package com.konnect.jpms.recruitment;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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

public class Onboarding extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static Logger log = Logger.getLogger(Onboarding.class);
	private static final long serialVersionUID = 1L;
	CommonFunctions CF ;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	
	String candidateId;
	String recruitId;
	
	String supervisor_emp_id;
	String wlocation_id;
	String grade_id;
	String type_of_emlpoyment;
	String candEmpId;
	String level_id;
	String depart_id; 
	String designation_id;
	String org_id;
	
	
	public String execute() {
		
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
		
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
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
		ResultSet rs =	null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			Map<String,String> hmEmpPersonal_details=new HashMap<String, String>(); 
			boolean flag = false;
			pst = con.prepareStatement("SELECT * FROM candidate_personal_details WHERE emp_per_id = ?");
			pst.setInt(1,uF.parseToInt(getCandidateId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			if (rs.next()) {
				hmEmpPersonal_details.put("emp_email",rs.getString("emp_email"));
				hmEmpPersonal_details.put("emp_fname",rs.getString("emp_fname"));
				hmEmpPersonal_details.put("emp_lname",rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();
			
			// Fetching employee id from epd*******
			pst=con.prepareStatement("select emp_per_id from employee_personal_details where emp_fname=? and emp_lname=? and emp_email=? and is_alive = true order by emp_per_id");
			pst.setString(1, hmEmpPersonal_details.get("emp_fname"));
			pst.setString(2, hmEmpPersonal_details.get("emp_lname"));
			pst.setString(3, hmEmpPersonal_details.get("emp_email"));
//			System.out.println("printing employee_personal_details query==="+pst);
			rs=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rs.next()){
//				System.out.println("EMp ID ===> " + rs.getString("emp_per_id"));
				flag = true;
//			 setCandEmpId(rs.getString("emp_per_id"));
			}
			rs.close();
			pst.close();
			
			if(!flag) {
				pst=con.prepareStatement("select added_by,designation_id,grade_id,wlocation,org_id,dept_id,level_id,type_of_employment,reporting_to_person_ids from recruitment_details where recruitment_id = ?");
				pst.setInt(1, uF.parseToInt(getRecruitId()));
				rs=pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
				if(rs.next()) {
					String strSuperWiser = null;
					if(rs.getString("reporting_to_person_ids")!=null && rs.getString("reporting_to_person_ids").contains(",")){
						List<String> alList = Arrays.asList(rs.getString("reporting_to_person_ids").split(","));
						for(int i = 0; alList != null && i < alList.size(); i++){
				            if(i == 1){
				            	strSuperWiser = alList.get(i).trim();
				            }
				        }
					}
					setSupervisor_emp_id(strSuperWiser);
					
					setWlocation_id(rs.getString("wlocation"));
					setGrade_id(rs.getString("grade_id"));
					setDesignation_id(rs.getString("designation_id"));
					setOrg_id(rs.getString("org_id"));
					setDepart_id(rs.getString("dept_id"));
					setLevel_id(rs.getString("level_id"));
					setType_of_emlpoyment(rs.getString("type_of_employment"));
				}
				rs.close();
				pst.close();
				
				setCandidatePersonaldetails();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void setEmployeeOtherDetails(Connection con,UtilityFunctions uF) {
	
		PreparedStatement pst = null;
		ResultSet rs =	null;
		
		try {
			// for multiple prev employment........FETCHING 
			
            Map<String,Map<String,String>> hmPrevEmp=new HashMap<String, Map<String,String>>();
            List<String> comList=new ArrayList<String>();
			pst=con.prepareStatement("select * from candidate_prev_employment where emp_id=?");
			pst.setInt(1,uF.parseToInt(getCandidateId()));

			rs=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rs.next()){
				Map<String,String> innermap=new HashMap<String, String>();
				innermap.put("company_name", rs.getString("company_name"));
				innermap.put("company_location", rs.getString("company_location"));
				innermap.put("company_city", rs.getString("company_city"));
				innermap.put("company_state", rs.getString("company_state"));
				innermap.put("company_country", rs.getString("company_country"));
				innermap.put("company_contact_no", rs.getString("company_contact_no"));
				innermap.put("reporting_to", rs.getString("reporting_to"));
				innermap.put("report_manager_ph_no", rs.getString("report_manager_ph_no"));
				innermap.put("hr_manager", rs.getString("hr_manager"));
				innermap.put("hr_manager_ph_no", rs.getString("hr_manager_ph_no"));
				innermap.put("from_date", rs.getString("from_date"));
				innermap.put("to_date", rs.getString("to_date"));	
				innermap.put("designation", rs.getString("designation"));
				innermap.put("responsibilities", rs.getString("responsibilities"));
				innermap.put("skills", rs.getString("skills"));
				
				hmPrevEmp.put(rs.getString("company_id"), innermap);
				comList.add(rs.getString("company_id"));
			}
			rs.close();
			pst.close();
			
			
		// inserting emp prev data************
			
//			System.out.println("printing comList ===> " + comList);
			
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
		//		System.out.println("printing emp_prev_employment === "+pst);
				pst.execute();
				pst.close();
			}
			
			
			// for Family INFO.......FETCHING 
			
		    Map<String,Map<String,String>> hmFamily=new HashMap<String, Map<String,String>>();
			pst=con.prepareStatement("select * from candidate_family_members where emp_id=?");
			pst.setInt(1,uF.parseToInt(getCandidateId()));
			int cnt=1;
			rs=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rs.next()){
				
				Map<String,String> innermap=new HashMap<String, String>();
				innermap.put("member_name", rs.getString("member_name"));
				innermap.put("member_dob", rs.getString("member_dob"));
				innermap.put("member_education", rs.getString("member_education"));
				innermap.put("member_occupation", rs.getString("member_occupation"));
				innermap.put("member_contact_no", rs.getString("member_contact_no"));
				innermap.put("member_email_id", rs.getString("member_email_id"));
				innermap.put("member_gender", rs.getString("member_gender"));
				innermap.put("member_type", rs.getString("member_type"));

				if(rs.getString("member_type").equalsIgnoreCase("MOTHER")){
					hmFamily.put("MOTHER", innermap);
				}else if(rs.getString("member_type").equalsIgnoreCase("FATHER")){
					hmFamily.put("FATHER", innermap);
				}else if(rs.getString("member_type").equalsIgnoreCase("SPOUSE")){
					hmFamily.put("SPOUSE", innermap);
				}else{
					hmFamily.put("SIBLING"+cnt, innermap);
					cnt++;
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("printing hmFamily.........."+hmFamily);
			
			// inserting Family members INFORMATION ***********************
			

				List<String> alFamilyKey=new ArrayList<String>();
				Iterator<String> itr=hmFamily.keySet().iterator();
				while(itr.hasNext()){
					alFamilyKey.add(itr.next());
				}
				for(int i=0; i<hmFamily.size();i++) {
					
					Map<String,String> innerFamily=hmFamily.get(alFamilyKey.get(i));
					
					pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_dob, member_education, " +
							"member_occupation, member_contact_no, member_email_id, member_gender, emp_id)VALUES (?,?,?,?,?,?,?,?,?)");
	
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
//					System.out.println("printing emp_family_members==="+pst);
					pst.execute();
					pst.close();
				}
				

			// for multiple Skills........FETCHING 
			
		    Map<String,Map<String,String>> hmSkills=new HashMap<String, Map<String,String>>();
		    List<String> skillsList=new ArrayList<String>();
			pst=con.prepareStatement("select * from candidate_skills_description where emp_id=?");
			pst.setInt(1,uF.parseToInt(getCandidateId()));
	
			rs=pst.executeQuery();
	//		System.out.println("new Date ===> " + new Date());
			while(rs.next()){
				
				Map<String,String> innermap=new HashMap<String, String>();
//				innermap.put("skills_name", rs.getString("skills_name"));
				innermap.put("skills_value", rs.getString("skills_value"));
				innermap.put("skill_id", rs.getString("skill_id"));
				hmSkills.put(rs.getString("skills_id"), innermap);
				skillsList.add(rs.getString("skills_id"));
			}
			rs.close();
			pst.close();
			
	//		System.out.println("printing hmSkills ===> "+hmSkills);
			// INSERTING EDUCATION  *************************************
			
			for(int i=0; i<skillsList.size(); i++) {
				Map<String,String> hminner=hmSkills.get(skillsList.get(i));
				pst = con.prepareStatement("INSERT INTO skills_description(skill_id, skills_value, emp_id)" +
									"VALUES (?,?,?)");
				pst.setInt(1, uF.parseToInt(hminner.get("skill_id")));
				pst.setInt(2, uF.parseToInt(hminner.get("skills_value")));
				pst.setInt(3,uF.parseToInt(getCandEmpId()));
//				System.out.println("printing skills_description==="+pst);
				pst.execute();
				pst.close();
			}
			
			
	// for multiple Hobbies........FETCHING 
			
		    Map<String,Map<String,String>> hmHobbies=new HashMap<String, Map<String,String>>();
		    List<String> hobbiesList=new ArrayList<String>();
			pst=con.prepareStatement("select * from candidate_hobbies_details where emp_id=?");
			pst.setInt(1,uF.parseToInt(getCandidateId()));
	
			rs=pst.executeQuery();
	//		System.out.println("new Date ===> " + new Date());
			while(rs.next()){
				
				Map<String,String> innermap=new HashMap<String, String>();
				innermap.put("hobbies_name", rs.getString("hobbies_name"));
				hmHobbies.put(rs.getString("hobbies_id"), innermap);
				hobbiesList.add(rs.getString("hobbies_id"));
			}
			rs.close();
			pst.close();
			
	//		System.out.println("printing hmHobbies ===> "+hmHobbies);
			// INSERTING EDUCATION  *************************************
			
			for(int i=0; i<hobbiesList.size(); i++) {
				Map<String,String> hminner=hmHobbies.get(hobbiesList.get(i));
				pst = con.prepareStatement("INSERT INTO hobbies_details(hobbies_name, emp_id)VALUES (?,?)");
				pst.setString(1, hminner.get("hobbies_name"));
				pst.setInt(2,uF.parseToInt(getCandEmpId()));
//				System.out.println("printing hobbies_details==="+pst);
				pst.execute();
				pst.close();
			}
		
		// for multiple Educatin........FETCHING 
		
		    Map<String,Map<String,String>> hmEducation=new HashMap<String, Map<String,String>>();
		    List<String> eduList=new ArrayList<String>();
			pst=con.prepareStatement("select * from candidate_education_details where emp_id=?");
			pst.setInt(1,uF.parseToInt(getCandidateId()));
		
			rs=pst.executeQuery();
		//	System.out.println("new Date ===> " + new Date());
			while(rs.next()){
				
				Map<String,String> innermap=new HashMap<String, String>();
	//			innermap.put("degree_name", rs.getString("degree_name"));
				innermap.put("degree_duration", rs.getString("degree_duration"));
				innermap.put("completion_year", rs.getString("completion_year"));
				innermap.put("education_id", rs.getString("education_id"));
				innermap.put("grade", rs.getString("grade"));
				
				hmEducation.put(rs.getString("degree_id"), innermap);
				eduList.add(rs.getString("degree_id"));
			}
			rs.close();
			pst.close();
			
		//	System.out.println("printing hmEducation ===> "+hmEducation);
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
		//			System.out.println("printing education_details==="+pst);
					pst.execute();
					pst.close();
			}
			
			
			// for multiple Language........FETCHING 
			
		    Map<String,Map<String,String>> hmLanguage=new HashMap<String, Map<String,String>>();
			pst=con.prepareStatement("select * from candidate_languages_details where emp_id=?");
			pst.setInt(1,uF.parseToInt(getCandidateId()));
		    List<String> langList=new ArrayList<String>();
			rs=pst.executeQuery();
		//	System.out.println("new Date ===> " + new Date());
			while(rs.next()){
				
				Map<String,String> innermap=new HashMap<String, String>();
				innermap.put("language_name", rs.getString("language_name"));
				innermap.put("language_read", rs.getString("language_read"));
				innermap.put("language_write", rs.getString("language_write"));
				innermap.put("language_speak", rs.getString("language_speak"));
				
				hmLanguage.put(rs.getString("language_id"), innermap);
			langList.add(rs.getString("language_id"));
			}
			rs.close();
			pst.close();
			
		//	System.out.println("printing hmLanguage ===> "+hmLanguage);
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
		//			System.out.println("printing languages_details==="+pst);
					pst.execute();
					pst.close();
			}
			
			// for Refrances........FETCHING 
			
		    Map<String,Map<String,String>> hmRefrances=new HashMap<String, Map<String,String>>();
			pst=con.prepareStatement("select * from candidate_references where emp_id=?");
			pst.setInt(1,uF.parseToInt(getCandidateId()));
			   List<String> refList=new ArrayList<String>();
			rs=pst.executeQuery();
		//	System.out.println("new Date ===> " + new Date());
			while(rs.next()){
				
				Map<String,String> innermap=new HashMap<String, String>();
				innermap.put("ref_name", rs.getString("ref_name"));
				innermap.put("ref_company", rs.getString("ref_company"));
				innermap.put("ref_designation", rs.getString("ref_designation"));
				innermap.put("ref_contact_no", rs.getString("ref_contact_no"));
				innermap.put("ref_email_id", rs.getString("ref_email_id"));
		
				hmRefrances.put(rs.getString("ref_id"), innermap);
			refList.add(rs.getString("ref_id"));
			}
			rs.close();
			pst.close();
			
		//	System.out.println("printing hmRefrances ======> "+hmRefrances);
			
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
	//				log.debug("pst==>"+pst);
			//		System.out.println("printing emp_references==="+pst);
					pst.executeUpdate();
					pst.close();
					
				}
				
			// for Refrances........FETCHING 
			
		    Map<String,Map<String,String>> hmMedicalQues=new HashMap<String, Map<String,String>>();
			pst=con.prepareStatement("select * from candidate_medical_details where emp_id=?");
			pst.setInt(1,uF.parseToInt(getCandidateId()));
			   List<String> medList=new ArrayList<String>();
			rs=pst.executeQuery();
		//	System.out.println("new Date ===> " + new Date());
			while(rs.next()){
				
				Map<String,String> innermap=new HashMap<String, String>();
				innermap.put("question_id", rs.getString("question_id"));
				innermap.put("yes_no", rs.getString("yes_no"));
				innermap.put("description", rs.getString("description"));
				innermap.put("FILE_PATH", rs.getString("filepath"));
				
				hmMedicalQues.put(rs.getString("medical_id"), innermap);
				medList.add(rs.getString("medical_id"));
				
			}
			rs.close();
			pst.close();
			
		//	System.out.println("printing hmMedicalQues ===> "+hmMedicalQues);
			
				// INSERTING MEDICAL DETAILS  *************************************
			
			for(int i=0;  i<medList.size(); i++) {
		
				Map<String,String> innerMed=hmMedicalQues.get(medList.get(i));
				
				pst = con.prepareStatement("INSERT INTO emp_medical_details (question_id, emp_id, yes_no, description,filepath) values (?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(innerMed.get("question_id")));
				pst.setInt(2, uF.parseToInt(getCandEmpId()));
				pst.setBoolean(3,uF.parseToBoolean(innerMed.get("yes_no")));
				pst.setString(4, innerMed.get("description"));
				pst.setString(5, innerMed.get("FILE_PATH"));
		//		System.out.println("printing emp_medical_details==="+pst);
				int x = pst.executeUpdate();
				pst.close();
				
				if(x > 0){
					if(CF.getStrDocSaveLocation() !=null && (innerMed.get("FILE_PATH") !=null && !innerMed.get("FILE_PATH").equals("") && innerMed.get("FILE_PATH").length() > 0)) {
						String strMedicalDocPath = CF.getStrDocSaveLocation()+I_CANDIDATE+"/"+I_DOCUMENT+"/"+I_MEDICAL+"/"+getCandidateId()+"/"+innerMed.get("FILE_PATH");
						String strMoveMedicalDocPath = CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_DOCUMENT+"/"+I_MEDICAL+"/"+getCandEmpId();
//						System.out.println("====strMedicalDocPath Move Medical Document====>"+strMedicalDocPath);
//						System.out.println("====strMoveMedicalDocPath Move Medical Document====>"+strMoveMedicalDocPath);
						
						File dirPath = new File(strMedicalDocPath);
						if (dirPath.exists()) {
//							System.out.println(getCandEmpId()+"====Move Medical Document====>"+innerMed.get("FILE_PATH"));
	
							File dirMovePath = new File(strMoveMedicalDocPath);
							if (!dirMovePath.exists()) {
								dirMovePath.mkdirs(); 
							}
							
							Runtime.getRuntime().exec("mv "+strMedicalDocPath+" "+strMoveMedicalDocPath+"/"+innerMed.get("FILE_PATH"));
						}
					}
				}
			}
			
			//fetching data for employee_official_details
			
			/**
			 * Candidate document
			 * */
			Map<String,Map<String,String>> hmDoc=new HashMap<String, Map<String,String>>();
			pst=con.prepareStatement("select * from candidate_documents_details where emp_id=?");
			pst.setInt(1,uF.parseToInt(getCandidateId()));
			rs=pst.executeQuery();
		//	System.out.println("new Date ===> " + new Date());
			while(rs.next()){
				Map<String,String> innermap=new HashMap<String, String>();
				innermap.put("DOC_NAME", rs.getString("documents_name"));
				innermap.put("DOC_TYPE", rs.getString("documents_type"));
				innermap.put("FILE_PATH", rs.getString("documents_file_name"));
				hmDoc.put(rs.getString("documents_id"), innermap);
				
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmDoc.keySet().iterator();
			while(it.hasNext()){
				String strCanDocId = it.next();
				Map<String,String> innerDoc=hmDoc.get(strCanDocId);
				
				pst = con.prepareStatement(insertDocuments);
				pst.setString(1, innerDoc.get("DOC_NAME"));
	            pst.setString(2, innerDoc.get("DOC_TYPE"));
	            pst.setInt(3, uF.parseToInt(getCandEmpId()));
	            pst.setString(4, innerDoc.get("FILE_PATH"));
	            pst.setInt(5, uF.parseToInt(strSessionEmpId));
	            pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
		//		System.out.println("printing emp_medical_details==="+pst);
				int x = pst.executeUpdate();
				pst.close();
				
				if(x > 0){
					if(CF.getStrDocSaveLocation() !=null && (innerDoc.get("FILE_PATH") !=null && !innerDoc.get("FILE_PATH").equals("") && innerDoc.get("FILE_PATH").length() > 0)) {
						String strAttachDocPath = CF.getStrDocSaveLocation()+I_CANDIDATE+"/"+I_DOCUMENT+"/"+I_ATTACHMENT+"/"+getCandidateId()+"/"+innerDoc.get("FILE_PATH");
						String strMoveAttachDocPath = CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_DOCUMENT+"/"+I_ATTACHMENT+"/"+getCandEmpId();
//						System.out.println("====strAttachDocPath Move Attachment Document====>"+strAttachDocPath);
//						System.out.println("====strMoveAttachDocPath Move Attachment Document====>"+strMoveAttachDocPath);
						File dirPath = new File(strAttachDocPath);
						if (dirPath.exists()) {
//							System.out.println(getCandEmpId()+"====Move Attachment Document====>"+innerDoc.get("FILE_PATH"));
	
							File dirMovePath = new File(strMoveAttachDocPath);
							if (!dirMovePath.exists()) {
								dirMovePath.mkdirs(); 
							}
							
							Runtime.getRuntime().exec("mv "+strAttachDocPath+" "+strMoveAttachDocPath+"/"+innerDoc.get("FILE_PATH"));
						}
					}
				}
			}
			/**
			 * Candidate document end
			 * */
			
			pst = con.prepareStatement("select is_disable_sal_calculate from candidate_application_details join recruitment_details using (recruitment_id) where candidate_id= ? and recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
			boolean disableSalaryStructure = false;
			if (rs.next()) {
				disableSalaryStructure = uF.parseToBoolean(rs.getString("is_disable_sal_calculate"));
			}
			rs.close();
			pst.close();
			
			// updating employee_official_details ******************
			
			pst=con.prepareStatement("INSERT INTO employee_official_details(depart_id, supervisor_emp_id, wlocation_id, grade_id, emp_id, org_id, " +
					"emptype,is_disable_sal_calculate) values(?,?,?,?, ?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getDepart_id()));
			pst.setInt(2, uF.parseToInt(getSupervisor_emp_id()));
			pst.setInt(3, uF.parseToInt(getWlocation_id()));
			pst.setInt(4, uF.parseToInt(getGrade_id()));
			pst.setInt(5, uF.parseToInt(getCandEmpId()));
			pst.setInt(6, uF.parseToInt(getOrg_id()));
			pst.setString(7, getType_of_emlpoyment());
			pst.setBoolean(8, disableSalaryStructure);
			pst.executeUpdate();
			pst.close();
		
			String levelId = CF.getEmpLevelId(con, getCandEmpId());
			CF.insertLeaveRegisterNewEmployee(con, levelId, getCandEmpId(), CF);
		
		} catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}
			
	}

	private void setCandidatePersonaldetails() {
	
		UtilityFunctions uF=new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =	null;
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
//			log.debug("pst selectEmployeeR1V==>"+pst);
			rs = pst.executeQuery();
//			System.out.println("pst ===> " + pst);
			if (rs.next()) {
				hmEmpPersonal_details.put("salutation", rs.getString("salutation"));
				hmEmpPersonal_details.put("emp_email", rs.getString("emp_email"));
				hmEmpPersonal_details.put("emp_fname", rs.getString("emp_fname"));

				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = rs.getString("emp_mname");
					}
				}
				
				hmEmpPersonal_details.put("emp_mname", strEmpMName);
				hmEmpPersonal_details.put("emp_lname",rs.getString("emp_lname"));
				hmEmpPersonal_details.put("emp_image", rs.getString("emp_image"));
				hmEmpPersonal_details.put("emp_address1",rs.getString("emp_address1"));
				hmEmpPersonal_details.put("emp_address2",rs.getString("emp_address2"));
				hmEmpPersonal_details.put("emp_city_id",rs.getString("emp_city_id"));
				hmEmpPersonal_details.put("emp_state_id",rs.getString("emp_state_id"));
				hmEmpPersonal_details.put("emp_country_id",rs.getString("emp_country_id"));
				hmEmpPersonal_details.put("emp_pincode",rs.getString("emp_pincode"));
				hmEmpPersonal_details.put("emp_address1_tmp",rs.getString("emp_address1_tmp"));
				hmEmpPersonal_details.put("emp_address2_tmp",rs.getString("emp_address2_tmp"));
				hmEmpPersonal_details.put("emp_city_id_tmp",rs.getString("emp_city_id_tmp"));
				hmEmpPersonal_details.put("emp_state_id_tmp",rs.getString("emp_state_id_tmp"));
				hmEmpPersonal_details.put("emp_country_id_tmp",rs.getString("emp_country_id_tmp"));
				hmEmpPersonal_details.put("emp_pincode_tmp",rs.getString("emp_pincode_tmp"));
				hmEmpPersonal_details.put("emp_contactno",rs.getString("emp_contactno"));
				hmEmpPersonal_details.put("emp_pan_no",rs.getString("emp_pan_no"));
				hmEmpPersonal_details.put("emp_pf_no",rs.getString("emp_pf_no"));
				hmEmpPersonal_details.put("emp_gender",rs.getString("emp_gender"));
				hmEmpPersonal_details.put("emp_date_of_birth",rs.getString("emp_date_of_birth"));
				hmEmpPersonal_details.put("emp_date_of_marriage",rs.getString("emp_date_of_marriage"));
				hmEmpPersonal_details.put("emp_bank_name",rs.getString("emp_bank_name"));
				hmEmpPersonal_details.put("emp_bank_acct_nbr",rs.getString("emp_bank_acct_nbr"));
				hmEmpPersonal_details.put("emp_email_sec",rs.getString("emp_email_sec"));
				hmEmpPersonal_details.put("skype_id",rs.getString("skype_id"));
				hmEmpPersonal_details.put("emp_contactno_mob",rs.getString("emp_contactno_mob"));
				hmEmpPersonal_details.put("emergency_contact_name",rs.getString("emergency_contact_name"));
				hmEmpPersonal_details.put("emergency_contact_no",rs.getString("emergency_contact_no"));
				hmEmpPersonal_details.put("passport_no",rs.getString("passport_no"));
				hmEmpPersonal_details.put("passport_expiry_date",rs.getString("passport_expiry_date"));
				hmEmpPersonal_details.put("blood_group",rs.getString("blood_group"));
				hmEmpPersonal_details.put("marital_status",rs.getString("marital_status"));
				hmEmpPersonal_details.put("joining_date",rs.getString("join_date"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("Joining Date ===> " + hmEmpPersonal_details.get("joining_date"));
			
			String query="insert into employee_personal_details (emp_fname,emp_lname ,emp_address1 ,emp_address2 ,emp_state_id,emp_country_id," +
					"emp_pincode,emp_contactno ,emp_image,emp_email,joining_date,emp_city_id,emp_pan_no ,emp_gender,emp_date_of_birth,emp_bank_name," +
					"emp_bank_acct_nbr,emp_email_sec ,skype_id ,emp_contactno_mob,emergency_contact_name,emergency_contact_no,passport_no," +
					"passport_expiry_date,blood_group ,marital_status,emp_pf_no ,emp_gpf_no,emp_date_of_marriage ,emp_address1_tmp,emp_address2_tmp," +
					"emp_city_id_tmp,emp_state_id_tmp,emp_country_id_tmp,emp_pincode_tmp,added_by,empcode,approved_flag,emp_mname,salutation,emp_status)" +
					"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)";
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
			
			pst.setDate(11,uF.getDateFormat(hmEmpPersonal_details.get("joining_date"), DBDATE)  );
			
			pst.setString(12,hmEmpPersonal_details.get("emp_city_id"));
			
			pst.setString(13,hmEmpPersonal_details.get("emp_pan_no"));
			pst.setString(14,hmEmpPersonal_details.get("emp_gender"));
			
			pst.setDate(15,uF.getDateFormat(hmEmpPersonal_details.get("emp_date_of_birth"), DBDATE)  );
			
			pst.setString(16,hmEmpPersonal_details.get("emp_bank_name"));
			pst.setString(17,hmEmpPersonal_details.get("emp_bank_acct_nbr"));
			pst.setString(18,hmEmpPersonal_details.get("emp_email_sec"));
			pst.setString(19,hmEmpPersonal_details.get("skype_id"));
			pst.setString(20,hmEmpPersonal_details.get("emp_contactno_mob"));
			pst.setString(21,hmEmpPersonal_details.get("emergency_contact_name"));
			pst.setString(22,hmEmpPersonal_details.get("emergency_contact_no"));
			pst.setString(23,hmEmpPersonal_details.get("passport_no"));
			
			pst.setDate(24,uF.getDateFormat(hmEmpPersonal_details.get("passport_expiry_date"), DBDATE)  );

			pst.setString(25,hmEmpPersonal_details.get("blood_group"));
			pst.setString(26,hmEmpPersonal_details.get("marital_status"));
			pst.setString(27,hmEmpPersonal_details.get("emp_pf_no"));
			pst.setString(28,hmEmpPersonal_details.get("emp_gpf_no"));
			
			pst.setDate(29,uF.getDateFormat(hmEmpPersonal_details.get("emp_date_of_marriage"), DBDATE)  );

			pst.setString(30,hmEmpPersonal_details.get("emp_address1_tmp"));
			pst.setString(31,hmEmpPersonal_details.get("emp_address2_tmp"));
			pst.setString(32,hmEmpPersonal_details.get("emp_city_id_tmp"));
			pst.setInt(33,uF.parseToInt(hmEmpPersonal_details.get("emp_state_id_tmp")));
			pst.setInt(34,uF.parseToInt(hmEmpPersonal_details.get("emp_country_id_tmp")));
			pst.setString(35,hmEmpPersonal_details.get("emp_pincode_tmp"));
			pst.setInt(36,uF.parseToInt(strSessionEmpId));
			pst.setString(37,"");
			pst.setBoolean(38,false);
			pst.setString(39, hmEmpPersonal_details.get("emp_mname"));
			pst.setString(40, hmEmpPersonal_details.get("salutation"));
			pst.setString(41, PROBATION);
//			System.out.println("insert pst ===="+pst);
			int x = pst.executeUpdate();
			pst.close();
			
			// Fetching employee id from epd*******
			if(x > 0){
//				pst=con.prepareStatement("select emp_per_id from employee_personal_details where emp_fname=? and emp_lname=? and emp_email=?");
//				pst.setString(1, hmEmpPersonal_details.get("emp_fname"));
//				pst.setString(2, hmEmpPersonal_details.get("emp_lname"));
//				pst.setString(3, hmEmpPersonal_details.get("emp_email"));
//				//System.out.println("printing employee_personal_details query==="+pst);
//				rs=pst.executeQuery();
//	//			System.out.println("new Date ===> " + new Date());
//				while(rs.next()){
//					setCandEmpId(rs.getString("emp_per_id"));
//				}
//				rs.close();
//				pst.close();
				pst = con.prepareStatement("select max(emp_per_id) as emp_per_id from employee_personal_details");
				rs = pst.executeQuery();
				while (rs.next()) {
					setCandEmpId(rs.getString("emp_per_id"));
				}
				rs.close();
				pst.close();
				
				if(hmEmpPersonal_details.get("emp_image") !=null && !hmEmpPersonal_details.get("emp_image").equals("") && hmEmpPersonal_details.get("emp_image").length() > 0){
					moveImage(con, uF, uF.parseToInt(getCandEmpId()),hmEmpPersonal_details.get("emp_image"));
				}
				insertUser(con, uF, request, uF.parseToInt(getCandEmpId()), hmEmpPersonal_details.get("emp_fname"), hmEmpPersonal_details.get("emp_lname"));
				
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
				rs=pst.executeQuery();
				List<String> empIdList = new ArrayList<String>();
				while(rs.next()){
				 empIdList.add(rs.getString("added_by"));
				 empIdList.add(rs.getString("approved_by"));
				}
				rs.close();
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
						
//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//						userAlerts.setStrDomain(strDomain);
//						userAlerts.setStrEmpId(empIdList.get(i));
//						userAlerts.set_type(NEW_JOINEE_PENDING_ALERT);
//						userAlerts.setStatus(INSERT_ALERT);
//						Thread t = new Thread(userAlerts);
//						t.run();
					}
				}
				
				pst=con.prepareStatement("delete from candidate_activity_details where recruitment_id=? and candi_id=? and user_id=? and " +
						"activity_id = ?");
				pst.setInt(1, uF.parseToInt(getRecruitId()));
				pst.setInt(2, uF.parseToInt(getCandidateId()));
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.setInt(4, CANDI_ACTIVITY_ONBOARDING_ID);
				pst.executeUpdate();
				pst.close();
				
				pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
				pst.setInt(1,uF.parseToInt(getRecruitId()));
				pst.setInt(2,uF.parseToInt(getCandidateId()));
				pst.setString(3, "Onboarding");
				pst.setInt(4,uF.parseToInt(strSessionEmpId));
				pst.setDate(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				pst.setInt(6, CANDI_ACTIVITY_ONBOARDING_ID);
				pst.execute();
				pst.close();
		
		
				addEmployeeInLearningPlan(con, uF, getCandEmpId());
				setEmployeeOtherDetails(con, uF);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
private void moveImage(Connection con, UtilityFunctions uF,int empId, String strEmpImage) {
	try {
		if(CF.getStrDocSaveLocation() !=null) {
			String strImagePath = CF.getStrDocSaveLocation()+I_CANDIDATE+"/"+I_IMAGE+"/"+getCandidateId()+"/"+strEmpImage;
			String strMoveImagePath = CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+empId;
//			System.out.println(empId+"====strImagePath moveImage====>"+strImagePath);
//			System.out.println(empId+"====strMoveImagePath moveImage====>"+strMoveImagePath);
			
			File dirPath = new File(strImagePath); 
			if (dirPath.exists()) {
//				System.out.println(empId+"====moveImage====>"+strEmpImage);

				File dirMovePath = new File(strMoveImagePath);
				if (!dirMovePath.exists()) {
					dirMovePath.mkdirs(); 
				}
				
				Runtime.getRuntime().exec("mv "+strImagePath+" "+strMoveImagePath+"/"+strEmpImage);
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	} 
	
}

private void addEmployeeInLearningPlan(Connection con, UtilityFunctions uF, String empId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			pst=con.prepareStatement("select learning_plan_id from learning_plan_details where desig_id like '%,"+getDesignation_id()+",%'");
			List<List<String>> lPlanIdAndLearnerList = new ArrayList<List<String>>();
			rs=pst.executeQuery();
//			System.out.println("pst learning_plan_details ===> " + pst);
			while(rs.next()){
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("learning_plan_id"));
				innerList.add(rs.getString("learner_ids"));
				lPlanIdAndLearnerList.add(innerList);
			}
			rs.close();
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
			if(rs != null) {
				try {
					rs.close();
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
		ResultSet rs = null;
		
		try {
			Map<String,Map<String,String>> hmSalary = new HashMap<String, Map<String,String>>();
			List<String> salaryHeadList=new ArrayList<String>();
			
//			pst=con.prepareStatement("select * from candidate_salary_details where emp_id=? and recruitment_id = ?");
//			pst.setInt(1, uF.parseToInt(getCandidateId()));
//			pst.setInt(2, uF.parseToInt(getRecruitId()));
			pst=con.prepareStatement("select * from candidate_salary_details where emp_id=? and recruitment_id = ? and effective_date " +
					"in (select max(effective_date) as effective_date from candidate_salary_details where emp_id=? and recruitment_id = ?)");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			pst.setInt(3, uF.parseToInt(getCandidateId()));
			pst.setInt(4, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("pst candidate-salary-detail ===> " + pst);
			while(rs.next()){
				
				Map<String,String> innerMap = new HashMap<String, String>();
				innerMap.put("salary_head_id", rs.getString("salary_head_id"));
				innerMap.put("amount", rs.getString("amount"));
				innerMap.put("pay_type", rs.getString("pay_type"));
				innerMap.put("isdisplay", rs.getString("isdisplay"));
				innerMap.put("is_approved", rs.getString("is_approved"));
				innerMap.put("user_id", rs.getString("user_id"));
				innerMap.put("earning_deduction", rs.getString("earning_deduction"));
//				innerMap.put("salary_type", rs.getString("salary_type"));
				
				hmSalary.put(rs.getString("salary_head_id"), innerMap);
				salaryHeadList.add(rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("printing language=="+hmLanguage);
			// INSERTING LANGUAGE  *************************************
			
//			System.out.println("salaryHeadList ===>>>> " + salaryHeadList);
//			double dblMonthCTC = 0.0d;
			for(int i=0; salaryHeadList != null && i<salaryHeadList.size(); i++) {
				
					Map<String,String> hminnerSalary=hmSalary.get(salaryHeadList.get(i));
					
					pst = con.prepareStatement("INSERT INTO emp_salary_details(salary_head_id, amount, pay_type, isdisplay, is_approved, user_id, " +
							"earning_deduction, effective_date, entry_date, emp_id, service_id, approved_date, approved_by, level_id)" +
							"VALUES (?,?,?,? ,?,?,?,? ,?,?,?,? ,?,?)");
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
					pst.setInt(13, uF.parseToInt(strSessionEmpId));
					pst.setInt(14, uF.parseToInt(getLevel_id()));
//					System.out.println("printing query==="+pst);
					pst.execute();
					pst.close();
					
//					if(hminnerSalary.get("EARNING_DEDUCTION")!=null && hminnerSalary.get("EARNING_DEDUCTION").equalsIgnoreCase("E")){
//						dblMonthCTC += uF.parseToDouble(hminnerSalary.get("AMOUNT"));
//					}
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
			
		}finally {
			if(rs != null) {
				try {
					rs.close();
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

	
	public void insertUser(Connection con, UtilityFunctions uF, HttpServletRequest request, int empId, String EmpFname, String EmpLname) {
		
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
			
		} finally {
			
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		
     this.request=request;
		
	}
	
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
	public String getCandEmpId() {
		return candEmpId;
	}

	public void setCandEmpId(String candEmpId) {
		this.candEmpId = candEmpId;
	}
	
	public String getLevel_id() {
		return level_id;
	}

	public void setLevel_id(String level_id) {
		this.level_id = level_id;
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

	public String getType_of_emlpoyment() {
		return type_of_emlpoyment;
	}

	public void setType_of_emlpoyment(String type_of_emlpoyment) {
		this.type_of_emlpoyment = type_of_emlpoyment;
	}
	
}
