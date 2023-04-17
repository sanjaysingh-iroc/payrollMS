package com.konnect.jpms.employee;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ReJoinEmployee extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	CommonFunctions CF=null;
	HttpSession session;
	
	String strEmpId;
	String strEmpNewId;
	public String strUserType = null;
	String strBaseUserType =  null;
	String strWLocationAccess =  null;
	public String strSessionEmpId = null;
	
	String mode;
	String empId;
	String step;
	String strAction = null;
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);
		strWLocationAccess = (String)session.getAttribute(WLOCATION_ACCESS);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		/*EncryptionUtility eU = new EncryptionUtility();
		if(getStrEmpId() != null && uF.parseToInt(getStrEmpId()) == 0) {
			String decodeEmpId = eU.decode(getStrEmpId());
			setStrEmpId(decodeEmpId);
		}*/
		
		//Created By Dattatray 10-6-2022
				strAction = request.getServletPath();
				if(strAction!=null) {
					strAction = strAction.replace("/","");
				}
				
		List<String> accessEmpList = CF.viewEmployeeIdsList(request, uF, strBaseUserType, strSessionEmpId, strWLocationAccess);
		if((strBaseUserType != null && strUserType != null && (strBaseUserType.equals(EMPLOYEE) || strUserType.equals(EMPLOYEE))) || !accessEmpList.contains(getEmpId())) {
			setEmpId((String)session.getAttribute(EMPID));
		}
		
		insertRejoinEmpData(uF);
		
		if(uF.parseToInt(getStrEmpNewId()) > 0) {
			setMode("onboard");
			setEmpId(getStrEmpNewId());
//			String encodeStep = eU.encode("8");
			setStep("8");
//			System.out.println("getEmpId() ===>> " + getEmpId());
//			if(uF.parseToInt(getEmpId()) > 0) {
//				String encodeEmpId = eU.encode(getEmpId());
//				setEmpId(encodeEmpId);
//			}
//			System.out.println("getEmpId() encode ===>> " + getEmpId());
			return SUCCESS;
		}
		loadPageVisitAuditTrail(CF, uF);//Created By Dattatray 10-6-2022
		return LOAD; 
		
	} 
	//Created By Dattatray 10-6-2022
		private void loadPageVisitAuditTrail(CommonFunctions CF,UtilityFunctions uF) {
			Connection con=null;
			Database db = new Database();
			db.setRequest(request);
			try {
				
				
				con = db.makeConnection(con);
				Map<String, String> hmEmpProfile = CF.getEmpNameMap(con,null,getEmpId());
				StringBuilder builder = new StringBuilder();
				
				builder.append("\nEmp name : "+hmEmpProfile.get(getEmpId()));
				
				CF.pageVisitAuditTrail(con,CF,uF, strSessionEmpId, strAction, strBaseUserType, builder.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				db.closeConnection(con);
			}
		}
	
	private void insertRejoinEmpData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			

			con.setAutoCommit(false);
			
			pst = con.prepareStatement("insert into employee_personal_details(empcode,emp_fname,emp_mname,emp_lname,emp_address1,emp_address2,emp_state_id,emp_country_id,emp_pincode," +
					"emp_contactno,emp_image,emp_email,emp_status,joining_date,emp_city_id,emp_pan_no,emp_gender,emp_date_of_birth,emp_bank_name," +
					"emp_bank_acct_nbr,emp_email_sec,skype_id,emp_contactno_mob,employment_end_date,emergency_contact_name,emergency_contact_no," +
					"passport_no,passport_expiry_date,blood_group,marital_status,approved_flag,is_alive,emp_filled_flag,emp_filled_flag_date,emp_entry_date," +
					"emp_pf_no,emp_gpf_no,session_id,_timestamp,added_by,emp_date_of_marriage,emp_address1_tmp,emp_address2_tmp,emp_city_id_tmp," +
					"emp_state_id_tmp,emp_country_id_tmp,emp_pincode_tmp,salutation,doctor_name,doctor_contact_no,uid_no,emp_esic_no,uan_no)" +
					
					"select '',emp_fname,emp_mname,emp_lname,emp_address1,emp_address2,emp_state_id,emp_country_id,emp_pincode," +
					"emp_contactno,emp_image,emp_email,'"+PROBATION+"','"+uF.getCurrentDate(CF.getStrTimeZone())+"',emp_city_id,emp_pan_no,emp_gender," +
					"emp_date_of_birth,emp_bank_name,emp_bank_acct_nbr,emp_email_sec,skype_id,emp_contactno_mob,null,emergency_contact_name," +
					"emergency_contact_no,passport_no,passport_expiry_date,blood_group,marital_status,true,true,true,null,'"+uF.getCurrentDate(CF.getStrTimeZone())+"'," +
					"emp_pf_no,emp_gpf_no,null,null,'"+uF.parseToInt((String) session.getAttribute(EMPID))+"',emp_date_of_marriage,emp_address1_tmp," +
					"emp_address2_tmp,emp_city_id_tmp,emp_state_id_tmp,emp_country_id_tmp,emp_pincode_tmp,salutation,doctor_name,doctor_contact_no," +
					"uid_no,emp_esic_no,uan_no from employee_personal_details where emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			System.out.println("pst=======>"+pst);
			int x = pst.executeUpdate();
			pst.close();
			if(x > 0){
//				System.out.println("insert");
				pst = con.prepareStatement("select max(emp_per_id) as emp_per_id from employee_personal_details");
				rs = pst.executeQuery();
				if(rs.next()){
					setStrEmpNewId(rs.getString("emp_per_id"));
				}
				rs.close();
				pst.close();
				
				if(uF.parseToInt(getStrEmpNewId()) > 0){
					pst = con.prepareStatement("INSERT INTO employee_official_details(emp_id) VALUES (?)");
					pst.setInt(1, uF.parseToInt(getStrEmpNewId()));
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("insert into skills_description(skills_name,skills_value,emp_id,skill_id)" +
							"select skills_name,skills_value,'"+uF.parseToInt(getStrEmpNewId())+"',skill_id from skills_description where emp_id = ?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
//					System.out.println("pst=======>"+pst);
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("insert into education_details(degree_name,degree_duration,completion_year,grade,emp_id,education_id)" +
							"select degree_name,degree_duration,completion_year,grade,'"+uF.parseToInt(getStrEmpNewId())+"',education_id " +
							" from education_details where emp_id = ?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
//					System.out.println("pst=======>"+pst);
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("insert into languages_details(language_name,language_read,language_write,language_speak,language_mothertounge,emp_id)" +
							"select language_name,language_read,language_write,language_speak,language_mothertounge,'"+uF.parseToInt(getStrEmpNewId())+"' " +
							" from languages_details where emp_id = ?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
//					System.out.println("pst=======>"+pst);
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("insert into hobbies_details(hobbies_name,emp_id) select hobbies_name,'"+uF.parseToInt(getStrEmpNewId())+"' from hobbies_details where emp_id = ?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
//					System.out.println("pst=======>"+pst);
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("insert into emp_prev_employment(company_name,company_location,company_city,company_contact_no,reporting_to," +
							"from_date,to_date,designation,responsibilities,skills,emp_id,company_country,company_state,report_manager_ph_no,hr_manager,hr_manager_ph_no)" +
							"select company_name,company_location,company_city,company_contact_no,reporting_to,from_date,to_date,designation,responsibilities," +
							"skills,'"+uF.parseToInt(getStrEmpNewId())+"',company_country,company_state,report_manager_ph_no,hr_manager,hr_manager_ph_no " +
							" from emp_prev_employment where emp_id = ?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
//					System.out.println("pst=======>"+pst);
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("insert into prev_earn_deduct_details(emp_id,gross_amount,tds_amount,financial_start,financial_end,document_name,added_by,added_on)" +
							"select '"+uF.parseToInt(getStrEmpNewId())+"',gross_amount,tds_amount,financial_start,financial_end,document_name,added_by,added_on " +
							" from prev_earn_deduct_details where emp_id = ?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
//					System.out.println("pst=======>"+pst);
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("insert into emp_references(ref_name,ref_company,ref_designation,ref_contact_no,ref_email_id,emp_id)" +
							"select ref_name,ref_company,ref_designation,ref_contact_no,ref_email_id,'"+uF.parseToInt(getStrEmpNewId())+"' " +
							" from emp_references where emp_id = ?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
//					System.out.println("pst=======>"+pst);
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("insert into emp_family_members(member_type,member_name,member_dob,member_education,member_occupation,member_contact_no,member_email_id,member_gender,emp_id,member_marital)" +
							"select member_type,member_name,member_dob,member_education,member_occupation,member_contact_no,member_email_id,member_gender,'"+uF.parseToInt(getStrEmpNewId())+"',member_marital " +
							" from emp_family_members where emp_id = ?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
//					System.out.println("pst=======>"+pst);
					pst.execute();
					pst.close();
					 
					pst = con.prepareStatement("insert into emp_medical_details(question_id,emp_id,yes_no,description,filepath)" +
						"select question_id,'"+uF.parseToInt(getStrEmpNewId())+"',yes_no,description,filepath from emp_medical_details where emp_id = ?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
//					System.out.println("pst=======>"+pst);
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("insert into documents_details(documents_name,documents_type,emp_id,documents_file_name,entry_date,added_by)" +
							"select documents_name,documents_type,'"+uF.parseToInt(getStrEmpNewId())+"',documents_file_name,entry_date,added_by " +
							" from documents_details where emp_id = ?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
//					System.out.println("pst=======>"+pst);
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("select * from employee_personal_details where emp_per_id=?");
					pst.setInt(1, uF.parseToInt(getStrEmpNewId()));
					rs = pst.executeQuery();
					String strEmpFname = null;
					String strEmpMname = null;
					String strEmpLname = null;
					if(rs.next()){
						strEmpFname = rs.getString("emp_fname");
						
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = rs.getString("emp_mname");
							}
						}
					
						
						strEmpMname = strEmpMName;
						strEmpLname = rs.getString("emp_lname");
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select * from user_details where emp_id=?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
					rs = pst.executeQuery();
					Map<String,String> hmUser = new HashMap<String, String>();
					if(rs.next()){
						hmUser.put("USER_NAME", rs.getString("username"));
						hmUser.put("PASSWORD", rs.getString("password"));
						hmUser.put("USER_TYPE_ID", rs.getString("usertype_id"));
						hmUser.put("EMP_ID", rs.getString("emp_id"));
						hmUser.put("STATUS", rs.getString("status"));
						hmUser.put("IS_TERMSCONDITION", rs.getString("is_termscondition"));
						hmUser.put("IS_FORCEPASSWORD", rs.getString("is_forcepassword"));
						hmUser.put("WLOCATION_ID_ACCESS", rs.getString("wlocation_id_access"));
						hmUser.put("ORG_ID_ACCESS", rs.getString("org_id_access"));
					}
					rs.close();
					pst.close();
					
					Map<String,String> userPresent= CF.getUsersMap(con);
					AddEmployeeMode aE = new AddEmployeeMode();
					aE.request = request;
					aE.session = session;
					aE.CF = CF;
					aE.setFname(strEmpFname);
					aE.setMname(strEmpMname);
					aE.setLname(strEmpLname);
					String username = aE.getUserName(userPresent);
					
					SecureRandom random = new SecureRandom();
					String password = new BigInteger(130, random).toString(32).substring(5, 13);
					
					pst = con.prepareStatement("update user_details set username=? where emp_id=?");
					pst.setString(1, hmUser.get("USER_NAME")+"_1");
					pst.setInt(2, uF.parseToInt(getStrEmpId()));
					pst.executeUpdate();
					pst.close();
					
					pst = con.prepareStatement("INSERT INTO user_details (username, password, usertype_id, emp_id, status,added_timestamp,wlocation_id_access,org_id_access) VALUES (?,?,?,?,?,?,?,?)");
					/*pst.setString(1, username);
					pst.setString(2, password);*/
					pst.setString(1, hmUser.get("USER_NAME"));
					pst.setString(2, hmUser.get("PASSWORD"));
					pst.setInt(3, uF.parseToInt(hmUser.get("USER_TYPE_ID")));
					pst.setInt(4, uF.parseToInt(getStrEmpNewId()));
					pst.setString(5, "ACTIVE");
					pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.setString(7, hmUser.get("WLOCATION_ID_ACCESS"));
					pst.setString(8, hmUser.get("ORG_ID_ACCESS"));
					pst.execute();
					pst.close();
					
				} 
				 
			} 
			
			con.commit();
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Rejoin Failed!"+END);
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}


	public String getStrEmpId() {
		return strEmpId;
	}


	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}


	public String getStrEmpNewId() {
		return strEmpNewId;
	}


	public void setStrEmpNewId(String strEmpNewId) {
		this.strEmpNewId = strEmpNewId;
	}


	public String getMode() {
		return mode;
	}


	public void setMode(String mode) {
		this.mode = mode;
	}


	public String getEmpId() {
		return empId;
	}


	public void setEmpId(String empId) {
		this.empId = empId;
	}


	public String getStep() {
		return step;
	}


	public void setStep(String step) {
		this.step = step;
	}

}