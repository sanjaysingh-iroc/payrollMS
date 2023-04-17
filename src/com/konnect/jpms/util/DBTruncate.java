package com.konnect.jpms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;


public class DBTruncate {
	public static void main(String args[]){
		
		List<String> alQueries = new ArrayList<String>();
		DBTruncate db = new DBTruncate();
		db.truncateTables(alQueries);
		db.insertData(alQueries);
		
		db.getFile(alQueries);   
	}

	public void getFile(List<String> alQueries) {
		try {
			
			PreparedStatement pst = null;
			Connection conn = null;
			
//			String HOST = "184.107.247.242";
//			String PORT = "5432";
//			String DBUSERNAME = "postgres";
//			String DBPASSWORD = "dbPassword"; 
			
//			String HOST = "108.163.186.66";
//			String PORT = "5432";
//			String DBUSERNAME = "postgres";
//			String DBPASSWORD = "dbPassword"; 
			
//			String HOST = "52.204.34.153";
//			String PORT = "5432";
//			String DBUSERNAME = "postgres";
//			String DBPASSWORD = "dbPassword";  
			
//			String HOST 		= "192.168.1.6";  
//			String PORT 		= "5432";
//			String DBUSERNAME	= "postgres"; 
//			String DBPASSWORD 	= "postgres";
			
//			String HOST = "54.152.10.99"; 
//			String HOST = "52.204.34.153";
			
			String HOST = "3.7.78.190";
			String PORT = "5432";
			String DBUSERNAME = "postgres";
			String DBPASSWORD = "Pune123!";  
			
//			String []arrDBNAME = null; 
			String []arrDBNAME = {""};
			
			for(int i=0; arrDBNAME!=null && i<arrDBNAME.length; i++) {
				try {
					Class.forName("org.postgresql.Driver");
					conn = DriverManager.getConnection("jdbc:postgresql://" + HOST + ":"+PORT+"/" + arrDBNAME[i], DBUSERNAME, DBPASSWORD);
					
					System.out.println(" ======== TRUNCATE EXECUTION STARTED ON " + arrDBNAME[i]);
					for(int al=0; al<alQueries.size(); al++) {
						try {
							pst = conn.prepareStatement(alQueries.get(al));
							pst.execute();	
							pst.close();
//							System.out.println("table==>"+alQueries.get(al));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					System.out.println(" ======== TRUNCATE EXECUTION FINISHED ON " + arrDBNAME[i]);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						conn.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void insertData(List<String> alQueries) {
		alQueries.add("ALTER SEQUENCE employee_official_details_emp_off_id_seq restart with 1");
		alQueries.add("ALTER SEQUENCE employee_personal_details_emp_per_id_seq restart with 1");
		alQueries.add("ALTER SEQUENCE employee_personal_details_emp_per_id_seq1 restart with 1");
		alQueries.add("ALTER SEQUENCE user_details_user_id_seq restart with 1");
		
		alQueries.add("delete from salary_details where level_id >-1");
		
		alQueries.add("INSERT INTO employee_personal_details(emp_per_id, empcode, emp_fname, emp_lname, emp_address1, emp_address2, emp_state_id, " +
				"emp_country_id, emp_pincode, emp_contactno, emp_image, emp_email, emp_status, joining_date, emp_city_id, emp_pan_no, emp_gender, " +
				"emp_date_of_birth, emp_bank_name, emp_bank_acct_nbr, emp_email_sec, skype_id, emp_contactno_mob, employment_end_date, " +
				"emergency_contact_name, emergency_contact_no, passport_no, passport_expiry_date, blood_group, marital_status, approved_flag, is_alive, " +
				"emp_filled_flag, emp_filled_flag_date, emp_entry_date, emp_pf_no, emp_gpf_no, session_id, _timestamp, added_by, emp_date_of_marriage, " +
				"emp_address1_tmp, emp_address2_tmp, emp_city_id_tmp, emp_state_id_tmp, emp_country_id_tmp, emp_pincode_tmp, salutation, emp_mname, " +
				"doctor_name, doctor_contact_no, uid_no, emp_esic_no, uan_no, corporate_mobile, corporate_desk, house_no, suburb, relevant_experience, " +
				"total_experience)" +
				"VALUES(1, 'E001', 'System', 'Admin', 'C-12 ', NULL, 1, 1, '411048', '20-40094480', NULL, '', 'PERMANENT', " +
				"TO_DATE('2018-04-01','YYYY-MM-DD'), 'Pune', 'AFP121212', 'M', TO_DATE('1980-09-03','YYYY-MM-DD'), '1', '', 'admin@workrig.com', ''," +
				" '7020174556', NULL, 'Rahul Patil', '121212121', '', NULL, '0', '0', true, true, true, TO_DATE('2014-08-27 00:00:00','YYYY-MM-DD HH24:MI:SS')," +
				" NULL, '', '', NULL, NULL, NULL, NULL, 'C-12', NULL, 'Pune', 1, 1, '411048', 'Mr.', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL);");
		
		alQueries.add("INSERT INTO employee_official_details(emp_off_id, depart_id, supervisor_emp_id, service_id, available_time_from, " +
				"available_time_to, available_days, emp_id, wlocation_id, is_roster, emptype, first_aid_allowance, grade_id, is_attendance, paycycle_duration, " +
				"payment_mode, org_id, hod_emp_id, emp_hr, is_service_tax, biometrix_isenable, biometrix_access, biometrix_id, is_form16, " +
				"previous_empment_avg_exp, remove_from_successionplan, is_form16_a, corporate_mobile_no, corporate_desk, bio_device_id, emp_contractor, " +
				"emprofile, month_ctc, annual_ctc)" +
				"VALUES(1, 5, 0, ',2,', NULL, NULL, '', 1, 2, false, 'FT', false, 12, true, 'M', 1, 1, 0, 0, false, NULL, NULL, 0, false, NULL, 0, false, ''," +
				" '', NULL, NULL, NULL, NULL, NULL);");
		
		alQueries.add("INSERT INTO user_details(user_id, username, password, usertype_id, emp_id, status, start_date, end_date, is_termscondition, " +
				"added_timestamp, reset_timestamp, is_forcepassword, wlocation_id_access, org_id_access)" +
				"VALUES(1, 'admin', 'workrig@123', 1, 1, 'ACTIVE', NULL, NULL, true, NULL, TO_DATE('2014-08-28 00:00:00','YYYY-MM-DD HH24:MI:SS'), false, " +
				"NULL, NULL);");
		
		alQueries.add("INSERT INTO shift_details (shift_id, _from, _to, shift_code, break_start, break_end, colour_code, shift_type) VALUES (1, '10:00:00', '16:00:00', 'ST', '14:00:00', '15:00:00', '#0000FF', 'Regular');");
		
		alQueries.add("INSERT INTO roster_weeklyoff_policy (roster_weeklyoff_id, weeklyoff_name, weeklyoff_type) VALUES (1, 'WO', 'FD');");
	}
	
	
	private void truncateTables(List<String> alQueries) {
		alQueries.add("truncate activity_info");
		alQueries.add("ALTER SEQUENCE activity_info_task_id_seq restart with 1");

		alQueries.add("truncate additional_info_of_invoice");
		alQueries.add("ALTER SEQUENCE additional_info_of_invoice_additional_info_id_seq restart with 1");

		alQueries.add("truncate additional_info_of_pro_invoice");
		alQueries.add("ALTER SEQUENCE additional_info_of_pro_invoice_pro_additional_info_id_seq restart with 1");

		alQueries.add("truncate allowance");
		alQueries.add("ALTER SEQUENCE allowance_allowance_id_seq restart with 1");

		alQueries.add("truncate allowance_condition_details"); 
		alQueries.add("ALTER SEQUENCE allowance_condition_details_allowance_condition_id_seq restart with 1");
		
		alQueries.add("truncate allowance_individual_details");
		alQueries.add("ALTER SEQUENCE allowance_individual_details_allowance_id_seq restart with 1");
		
		alQueries.add("truncate allowance_pay_details");
		alQueries.add("ALTER SEQUENCE allowance_pay_details_allowance_pay_id_seq restart with 1");
		
		alQueries.add("truncate allowance_payment_logic");
		alQueries.add("ALTER SEQUENCE allowance_payment_logic_payment_logic_id_seq restart with 1");
		
		alQueries.add("truncate annual_variable_details");
		alQueries.add("ALTER SEQUENCE annual_variable_details_annual_variable_id_seq restart with 1");
		
		alQueries.add("truncate annual_variable_individual_details");
		alQueries.add("ALTER SEQUENCE annual_variable_individual_details_annual_vari_ind_id_seq restart with 1");
		
		alQueries.add("truncate appraisal_approval");
		alQueries.add("ALTER SEQUENCE appraisal_approval_appraisal_approval_id_seq restart with 1");
		
		alQueries.add("truncate appraisal_attribute");
		alQueries.add("ALTER SEQUENCE appraisal_attribute_arribute_id_seq restart with 1");

		alQueries.add("truncate appraisal_attribute_level");
		alQueries.add("ALTER SEQUENCE appraisal_attribute_level_arribute_level_id_seq restart with 1");

		alQueries.add("truncate appraisal_details");
		alQueries.add("ALTER SEQUENCE appraisal_details_appraisal_details_id_seq restart with 1");

		alQueries.add("truncate appraisal_details_frequency");
		alQueries.add("ALTER SEQUENCE appraisal_details_frequency_appraisal_freq_id_seq restart with 1");
		
		alQueries.add("truncate appraisal_element_attribute");
		alQueries.add("ALTER SEQUENCE appraisal_element_attribute_appraisal_element_attribute_id_seq restart with 1");

		alQueries.add("truncate appraisal_final_sattlement");
		alQueries.add("ALTER SEQUENCE appraisal_final_sattlement_appraisal_final_sattlement_id_seq restart with 1");

		alQueries.add("truncate appraisal_goal_details");
		alQueries.add("ALTER SEQUENCE appraisal_goal_details_goal_id_seq restart with 1");
		
		alQueries.add("truncate appraisal_level_details");
		alQueries.add("ALTER SEQUENCE appraisal_level_details_appraisal_level_id_seq restart with 1");

		alQueries.add("truncate appraisal_main_level_details");
		alQueries.add("ALTER SEQUENCE appraisal_main_level_details_main_level_id_seq restart with 1");

		alQueries.add("truncate appraisal_measure_details");
		alQueries.add("ALTER SEQUENCE appraisal_measure_details_measure_id_seq restart with 1");

		alQueries.add("truncate appraisal_objective_details");
		alQueries.add("ALTER SEQUENCE appraisal_objective_details_objective_id_seq restart with 1");
		
		alQueries.add("truncate appraisal_other_question_type_details");
		alQueries.add("ALTER SEQUENCE appraisal_other_question_type_details_othe_question_type_id_seq restart with 1");
		
		alQueries.add("truncate appraisal_plan");
		alQueries.add("ALTER SEQUENCE appraisal_plan_appraisal_plan_id_seq restart with 1");
		
		alQueries.add("truncate appraisal_question_answer");
		alQueries.add("ALTER SEQUENCE appraisal_question_answer_appraisal_question_answer_id_seq restart with 1");
		
		alQueries.add("truncate appraisal_question_details");
		alQueries.add("ALTER SEQUENCE appraisal_question_details_appraisal_question_details_id_seq restart with 1");
		
		alQueries.add("truncate appraisal_reviewee_details");
		alQueries.add("ALTER SEQUENCE appraisal_reviewee_details_appraisal_reviewee_details_id_seq restart with 1");
		
		alQueries.add("truncate appraisal_scorecard_details");
		alQueries.add("ALTER SEQUENCE appraisal_scorecard_details_scorecard_id_seq restart with 1");
		
		alQueries.add("truncate approve_attendance");
		alQueries.add("ALTER SEQUENCE approve_attendance_approve_attendance_id_seq restart with 1");
		
		alQueries.add("truncate arear_details");
		alQueries.add("ALTER SEQUENCE arear_details_arear_id_seq restart with 1");

		alQueries.add("truncate arrear_generation");
		alQueries.add("ALTER SEQUENCE arrear_generation_generation_id_seq restart with 1");
		
		alQueries.add("truncate arrear_headwise_details");
		alQueries.add("ALTER SEQUENCE arrear_headwise_details_arrear_headwise_id_seq restart with 1");
		
		alQueries.add("truncate assessment_details");
		alQueries.add("ALTER SEQUENCE assessment_details_assessment_details_id_seq restart with 1");

		alQueries.add("truncate assessment_emp_remain_time");
		alQueries.add("ALTER SEQUENCE assessment_details_assessment_details_id_seq restart with 1");

		alQueries.add("truncate assessment_question_answer");
		alQueries.add("ALTER SEQUENCE assessment_question_answer_assess_question_answer_id_seq restart with 1");

		alQueries.add("truncate assessment_question_bank");
		alQueries.add("ALTER SEQUENCE assessment_question_bank_assessment_question_bank_id_seq restart with 1");

		alQueries.add("truncate assessment_question_details");
		alQueries.add("ALTER SEQUENCE assessment_question_details_assessment_question_id_seq restart with 1");

		alQueries.add("truncate assessment_section_details");
		alQueries.add("ALTER SEQUENCE assessment_section_details_assessment_section_id_seq restart with 1");

		alQueries.add("truncate assessment_take_attempt_details");
		alQueries.add("ALTER SEQUENCE assessment_take_attempt_details_assessment_take_attempt_id_seq restart with 1");

		alQueries.add("truncate attendance_details");
		alQueries.add("ALTER SEQUENCE attendance_details_att_id_seq restart with 1");

		alQueries.add("truncate attendance_payroll");
		alQueries.add("ALTER SEQUENCE attendance_payroll_attendance_payroll_id_seq restart with 1");

		alQueries.add("truncate attendance_punch_in_out_details");
		alQueries.add("ALTER SEQUENCE attendance_punch_in_out_details_attendance_punch_in_out_id_seq restart with 1");
		
		alQueries.add("truncate bank_details");
		alQueries.add("ALTER SEQUENCE bank_details_bank_id_seq restart with 1");
		
		alQueries.add("truncate basic_fitment_details");
		alQueries.add("ALTER SEQUENCE basic_fitment_details_fitment_id_seq restart with 1");
		
		alQueries.add("truncate billing_head_setting");
		alQueries.add("ALTER SEQUENCE billing_head_setting_billing_head_id_seq restart with 1");

		alQueries.add("truncate bonus_details");
		alQueries.add("ALTER SEQUENCE bonus_details_bonus_id_seq restart with 1");

		alQueries.add("truncate bonus_individual_details");
		alQueries.add("ALTER SEQUENCE bonus_individual_details_bonus_id_seq restart with 1");

		alQueries.add("truncate book_details");
		alQueries.add("ALTER SEQUENCE book_details_book_id_seq restart with 1");

		alQueries.add("truncate book_issues_returns");
		alQueries.add("ALTER SEQUENCE book_issues_returns_book_issued_id_seq restart with 1");

		alQueries.add("truncate book_purchases");
		alQueries.add("ALTER SEQUENCE book_purchases_book_purchase_id_seq restart with 1");
		
		alQueries.add("truncate book_reviews");
		alQueries.add("ALTER SEQUENCE book_reviews_book_review_id_seq restart with 1");
		
		alQueries.add("truncate branch_details");
		alQueries.add("ALTER SEQUENCE branch_details_branch_id_seq restart with 1");

		alQueries.add("truncate break_application_register");
		alQueries.add("ALTER SEQUENCE break_application_register_break_register_id_seq restart with 1");

		alQueries.add("truncate break_policy");
		alQueries.add("ALTER SEQUENCE break_policy_break_policy_id_seq restart with 1");

		alQueries.add("truncate break_register");
		alQueries.add("ALTER SEQUENCE break_register_register_id_seq restart with 1");

		alQueries.add("truncate candidate_activity_details");
		alQueries.add("ALTER SEQUENCE candidate_activity_details_candi_activity_id_seq restart with 1");
		
		alQueries.add("truncate candidate_application_details");
		alQueries.add("ALTER SEQUENCE candidate_application_details_candi_application_deatils_id_seq restart with 1");
		
		alQueries.add("truncate candidate_comments");
		alQueries.add("ALTER SEQUENCE candidate_comments_comment_id_seq restart with 1");
		
		alQueries.add("truncate candidate_documents_details");
		alQueries.add("ALTER SEQUENCE candidate_documents_details_documents_id_seq restart with 1");
		
		alQueries.add("truncate candidate_education_details");
		alQueries.add("ALTER SEQUENCE candidate_education_details_degree_id_seq restart with 1");

		alQueries.add("truncate candidate_family_members");
		alQueries.add("ALTER SEQUENCE candidate_family_members_member_id_seq restart with 1");

		alQueries.add("truncate candidate_hobbies_details");
		alQueries.add("ALTER SEQUENCE candidate_hobbies_details_hobbies_id_seq restart with 1");

		alQueries.add("truncate candidate_interview_availability");
		alQueries.add("ALTER SEQUENCE candidate_interview_availability_int_avail_id_seq restart with 1");

		alQueries.add("truncate candidate_interview_panel");
		alQueries.add("ALTER SEQUENCE candidate_interview_panel_panel_id_seq restart with 1");

		alQueries.add("truncate candidate_interview_panel_availability");
		alQueries.add("ALTER SEQUENCE candidate_interview_panel_availability_panel_interview_id_seq restart with 1");

		alQueries.add("truncate candidate_languages_details");
		alQueries.add("ALTER SEQUENCE candidate_languages_details_language_id_seq restart with 1");

		alQueries.add("truncate candidate_medical_details");
		alQueries.add("ALTER SEQUENCE candidate_medical_details_medical_id_seq restart with 1");

		alQueries.add("truncate candidate_personal_details");
		alQueries.add("ALTER SEQUENCE candidate_personal_details_emp_per_id_seq restart with 1");

		alQueries.add("truncate candidate_prev_employment");
		alQueries.add("ALTER SEQUENCE candidate_prev_employment_company_id_seq restart with 1");

		alQueries.add("truncate candidate_references");
		alQueries.add("ALTER SEQUENCE candidate_references_ref_id_seq restart with 1");

		alQueries.add("truncate candidate_salary_details");
		alQueries.add("ALTER SEQUENCE candidate_salary_details_emp_salary_id_seq restart with 1");

		alQueries.add("truncate candidate_skills_description");
		alQueries.add("ALTER SEQUENCE candidate_skills_description_skills_id_seq restart with 1");

		alQueries.add("truncate candidate_skills_rating_details");
		alQueries.add("ALTER SEQUENCE candidate_skills_rating_details_candidate_skill_rating_id_seq restart with 1");

		alQueries.add("truncate category_details");
		alQueries.add("ALTER SEQUENCE category_details_category_id_seq restart with 1");

		alQueries.add("truncate certificate_details");
		alQueries.add("ALTER SEQUENCE certificate_details_certificate_details_id_seq restart with 1");

		alQueries.add("truncate challan_details");
		alQueries.add("ALTER SEQUENCE challan_details_tax_details_id_seq restart with 1");

		alQueries.add("truncate client_address");
		alQueries.add("ALTER SEQUENCE client_address_client_address_id_seq restart with 1");

		alQueries.add("truncate client_departments");
		alQueries.add("ALTER SEQUENCE client_departments_client_depart_id_seq restart with 1");

		alQueries.add("truncate client_designations");
		alQueries.add("ALTER SEQUENCE client_designations_client_desig_id_seq restart with 1");

		alQueries.add("truncate client_details");
		alQueries.add("ALTER SEQUENCE client_details_client_id_seq restart with 1");

		alQueries.add("truncate client_locations");
		alQueries.add("ALTER SEQUENCE client_locations_client_loc_id_seq restart with 1");
		
		alQueries.add("truncate client_poc");
		alQueries.add("ALTER SEQUENCE client_poc_poc_id_seq restart with 1");

		alQueries.add("truncate communication_1");
		alQueries.add("ALTER SEQUENCE communication_1_communication_id_seq restart with 1");
		
		alQueries.add("truncate communications");
		alQueries.add("ALTER SEQUENCE communications_communications_id_seq restart with 1");
		
		
		alQueries.add("truncate company_manual");
		alQueries.add("ALTER SEQUENCE company_manual_manual_id_seq restart with 1");

		alQueries.add("truncate compose");
		alQueries.add("ALTER SEQUENCE compose_com_sid_seq restart with 1");

		alQueries.add("truncate cost_calculation_settings");
		alQueries.add("ALTER SEQUENCE cost_calculation_settings_cost_calculation_id_seq restart with 1");

		alQueries.add("truncate course_assessment_details");
		alQueries.add("ALTER SEQUENCE course_assessment_details_course_assessment_id_seq restart with 1");
		
		alQueries.add("truncate course_chapter_details");
		alQueries.add("ALTER SEQUENCE course_chapter_details_course_chapter_id_seq restart with 1");
		
		alQueries.add("truncate course_content_details");
		alQueries.add("ALTER SEQUENCE course_content_details_course_content_id_seq restart with 1");
		
		alQueries.add("truncate course_details");
		alQueries.add("ALTER SEQUENCE course_details_course_id_seq restart with 1");

		alQueries.add("truncate course_question_bank");
		alQueries.add("ALTER SEQUENCE course_question_bank_course_question_bank_id_seq restart with 1");
		
		alQueries.add("truncate course_read_details");
		alQueries.add("ALTER SEQUENCE course_read_details_course_read_id_seq restart with 1");

		alQueries.add("truncate course_read_update_details");
		alQueries.add("ALTER SEQUENCE course_read_update_details_course_read_update_id_seq restart with 1");
		
		alQueries.add("truncate course_subchapter_details");
		alQueries.add("ALTER SEQUENCE course_subchapter_details_course_subchapter_id_seq restart with 1");
		
		alQueries.add("truncate course_subject_details");
		alQueries.add("ALTER SEQUENCE course_subject_details_course_subject_id_seq restart with 1");
		
		alQueries.add("truncate deduction_details");
		alQueries.add("ALTER SEQUENCE deduction_details_deduction_id_seq restart with 1");
		alQueries.add("ALTER SEQUENCE deduction_details_oti_deduction_id_seq restart with 1");

//		alQueries.add("truncate deduction_details_india");
//		alQueries.add("ALTER SEQUENCE deduction_details_deduction_id_seq restart with 1");
		
//		alQueries.add("truncate deduction_tax_details");
//		alQueries.add("ALTER SEQUENCE deduction_tax_details_deduction_tax_id_seq restart with 1");
		
		alQueries.add("truncate deduction_tax_misc_details");
		alQueries.add("ALTER SEQUENCE deduction_tax_misc_details_deduction_tax_misc_id_seq1 restart with 1");
		
		alQueries.add("truncate department_info");
		alQueries.add("ALTER SEQUENCE department_info_dept_id_seq restart with 1");

		alQueries.add("truncate desig_attribute");
		alQueries.add("ALTER SEQUENCE desig_attribute_desig_attribute_id_seq restart with 1");
		
		alQueries.add("truncate designation_details");
		alQueries.add("ALTER SEQUENCE designation_details_designation_id_seq restart with 1");

		alQueries.add("truncate designation_info");
		alQueries.add("ALTER SEQUENCE designation_info_desig_id_seq restart with 1");

		alQueries.add("truncate designation_kra_details");
		alQueries.add("ALTER SEQUENCE designation_kra_details_designation_kra_id_seq restart with 1");
		
		alQueries.add("truncate dish_details");
		alQueries.add("ALTER SEQUENCE dish_details_dish_id_seq restart with 1");
		
		alQueries.add("truncate dish_order_details");
		alQueries.add("ALTER SEQUENCE dish_order_details_order_id_seq restart with 1");
		
		alQueries.add("truncate document_activities");
		alQueries.add("ALTER SEQUENCE document_activities_document_id_seq restart with 1");
		
		alQueries.add("truncate document_collateral");
		alQueries.add("ALTER SEQUENCE document_collateral_collateral_id_seq restart with 1");
		
		alQueries.add("truncate document_comm_details");
		alQueries.add("ALTER SEQUENCE document_comm_details_document_id_seq restart with 1");

		alQueries.add("truncate document_download_history");
		alQueries.add("ALTER SEQUENCE document_download_history_document_history_id_seq restart with 1");

		alQueries.add("truncate document_signature");
		alQueries.add("ALTER SEQUENCE document_signature_signature_id_seq restart with 1");
		
		alQueries.add("truncate documents_details");
		alQueries.add("ALTER SEQUENCE documents_details_documents_id_seq restart with 1");

		alQueries.add("truncate education_details");
		alQueries.add("ALTER SEQUENCE education_details_degree_id_seq restart with 1");

		alQueries.add("truncate educational_details");
		alQueries.add("ALTER SEQUENCE educational_details_edu_id_seq restart with 1");

		alQueries.add("truncate emp_basic_fitment_details");
		alQueries.add("ALTER SEQUENCE emp_basic_fitment_details_emp_fitment_id_seq restart with 1");

		alQueries.add("truncate emp_clock_on_off_access");
		alQueries.add("ALTER SEQUENCE emp_clock_on_off_access_clock_on_off_id_seq restart with 1");

		alQueries.add("truncate emp_defer_basic_fitment");
		alQueries.add("ALTER SEQUENCE emp_defer_basic_fitment_emp_defer_id_seq restart with 1");

		alQueries.add("truncate emp_degree_certificate_details");
		alQueries.add("ALTER SEQUENCE emp_degree_certificate_details_emp_degree_certificate_id_seq restart with 1");

		alQueries.add("truncate emp_epf_details");
		alQueries.add("ALTER SEQUENCE emp_epf_details_emp_epf_id_seq restart with 1");

		alQueries.add("truncate emp_esi_details");
		alQueries.add("ALTER SEQUENCE emp_esi_details_emp_esi_id_seq restart with 1");

		alQueries.add("truncate emp_exgratia_details");
		alQueries.add("ALTER SEQUENCE emp_exgratia_details_emp_exgratia_id_seq restart with 1");

		alQueries.add("truncate emp_family_members");
		alQueries.add("ALTER SEQUENCE emp_family_members_member_id_seq restart with 1");

		alQueries.add("truncate emp_gratuity_details");
		alQueries.add("ALTER SEQUENCE emp_gratuity_details_emp_gratuity_id_seq restart with 1");

		alQueries.add("truncate emp_interview_availability");
		alQueries.add("ALTER SEQUENCE emp_interview_availability_int_avail_id_seq restart with 1");

		alQueries.add("truncate emp_kras");
		alQueries.add("ALTER SEQUENCE emp_kras_emp_kra_id_seq restart with 1");

		alQueries.add("truncate emp_leave_break_type");
		alQueries.add("ALTER SEQUENCE emp_leave_break_type_emp_break_type_id_seq restart with 1");

		alQueries.add("truncate emp_leave_encashment");
		alQueries.add("ALTER SEQUENCE emp_leave_encashment_leave_encash_id_seq restart with 1");

		alQueries.add("truncate emp_leave_entry");
		alQueries.add("ALTER SEQUENCE emp_leave_entry_leave_id_seq restart with 1");

		alQueries.add("truncate emp_leave_type");
		alQueries.add("ALTER SEQUENCE emp_leave_type_emp_leave_type_id_seq restart with 1");

		alQueries.add("truncate emp_lta_details");
		alQueries.add("ALTER SEQUENCE emp_lta_details_emp_lta_id_seq restart with 1");

		alQueries.add("truncate emp_lwf_details");
		alQueries.add("ALTER SEQUENCE emp_lwf_details_emp_lwf_id_seq restart with 1");

		alQueries.add("truncate emp_medical_details");
		alQueries.add("ALTER SEQUENCE emp_medical_details_medical_id_seq restart with 1");

		alQueries.add("truncate emp_off_board");
		alQueries.add("ALTER SEQUENCE emp_off_board_off_board_id_seq restart with 1");

		alQueries.add("truncate emp_off_board_document_comment");
		alQueries.add("ALTER SEQUENCE emp_off_board_document_commen_off_board_document_comment_id_seq restart with 1");

		alQueries.add("truncate emp_off_board_document_details");
		alQueries.add("ALTER SEQUENCE emp_off_board_document_details_off_board_document_id_seq restart with 1");

		alQueries.add("truncate emp_off_board_feedback");
		alQueries.add("ALTER SEQUENCE emp_off_board_feedback_exit_feedback_id_seq restart with 1");

		alQueries.add("truncate emp_offboard_question_list");
		alQueries.add("ALTER SEQUENCE emp_offboard_question_list_question_id_seq restart with 1");

		alQueries.add("truncate emp_offboard_status");
		alQueries.add("ALTER SEQUENCE emp_offboard_status_emp_offboard_status_id_seq restart with 1");

		alQueries.add("truncate emp_perks");
		alQueries.add("ALTER SEQUENCE emp_perks_perks_id_seq restart with 1");

		alQueries.add("truncate emp_prev_employment");
		alQueries.add("ALTER SEQUENCE emp_prev_employment_company_id_seq restart with 1");

		alQueries.add("truncate emp_references");
		alQueries.add("ALTER SEQUENCE emp_references_ref_id_seq restart with 1");

		alQueries.add("truncate emp_reimbursement");
		alQueries.add("ALTER SEQUENCE emp_reimbursement_reimbursement_id_seq restart with 1");

		alQueries.add("truncate emp_reimbursement_draft");
		alQueries.add("ALTER SEQUENCE emp_reimbursement_draft_reimbursement_id_seq restart with 1");

		alQueries.add("truncate emp_reimbursement_paid_trans_details");
		alQueries.add("ALTER SEQUENCE emp_reimbursement_paid_trans_detail_emp_reimb_paid_trans_id_seq restart with 1");
		
		alQueries.add("truncate emp_salary_details");
		alQueries.add("ALTER SEQUENCE emp_salary_details_emp_salary_id_seq restart with 1");

		alQueries.add("truncate emp_tds_details");
		alQueries.add("ALTER SEQUENCE emp_tds_details_emp_tds_id_seq restart with 1");

		alQueries.add("truncate employee_activity_details");
		alQueries.add("ALTER SEQUENCE employee_activity_details_emp_activity_id_seq restart with 1");

		alQueries.add("truncate employee_official_details");
		alQueries.add("ALTER SEQUENCE employee_official_details_emp_off_id_seq restart with 1");

		alQueries.add("truncate employee_personal_details");
		alQueries.add("ALTER SEQUENCE employee_personal_details_emp_per_id_seq restart with 1");

//		alQueries.add("truncate epf_details");//
//		alQueries.add("ALTER SEQUENCE epf_details_epf_id_seq restart with 1");

//		alQueries.add("truncate esi_details");//
//		alQueries.add("ALTER SEQUENCE esi_details_esi_id_seq restart with 1");

		alQueries.add("truncate events");
		alQueries.add("ALTER SEQUENCE events_event_id_seq restart with 1");

		alQueries.add("truncate ex_gratia_details");
		alQueries.add("ALTER SEQUENCE ex_gratia_details_ex_gratia_id_seq restart with 1");

		alQueries.add("truncate ex_gratia_slab_details");
		alQueries.add("ALTER SEQUENCE ex_gratia_slab_details_gratia_slab_id_seq restart with 1");

		alQueries.add("truncate exception_reason");
		alQueries.add("ALTER SEQUENCE exception_reason_exception_id_seq restart with 1");

//		alQueries.add("truncate exemption_details");//
//		alQueries.add("ALTER SEQUENCE exemption_details_exemption_id_seq restart with 1");

		alQueries.add("truncate form16_documents");
		alQueries.add("ALTER SEQUENCE form16_documents_form16_document_id_seq restart with 1");
		
		alQueries.add("truncate form_management_details");
		alQueries.add("ALTER SEQUENCE form_management_details_form_id_seq restart with 1");
		
		alQueries.add("truncate form_question_answer");
		alQueries.add("ALTER SEQUENCE form_question_answer_form_question_answer_id_seq restart with 1");
		
		alQueries.add("truncate form_question_details");
		alQueries.add("ALTER SEQUENCE form_question_details_form_question_id_seq restart with 1");
		
		alQueries.add("truncate form_section_details");
		alQueries.add("ALTER SEQUENCE form_section_details_form_section_id_seq restart with 1");
		
		alQueries.add("truncate goal_details");
		alQueries.add("ALTER SEQUENCE goal_details_goal_id_seq restart with 1");

		alQueries.add("truncate goal_details_frequency");
		alQueries.add("ALTER SEQUENCE goal_details_frequency_goal_freq_id_seq restart with 1");
		
		alQueries.add("truncate goal_kra_emp_status_rating_details");
		alQueries.add("ALTER SEQUENCE goal_kra_emp_status_rating_de_goal_kra_emp_status_rating_id_seq restart with 1");
		
		alQueries.add("truncate goal_kra_status_rating_details");
		alQueries.add("ALTER SEQUENCE goal_kra_status_rating_details_kra_task_status_rating_id_seq restart with 1");
		
		alQueries.add("truncate goal_kra_target_finalization");
		alQueries.add("ALTER SEQUENCE goal_kra_target_finalization_gkt_finalization_id_seq restart with 1");
		
		alQueries.add("truncate goal_kra_tasks");
		alQueries.add("ALTER SEQUENCE goal_kra_tasks_goal_kra_task_id_seq restart with 1");
		
		alQueries.add("truncate goal_kras");
		alQueries.add("ALTER SEQUENCE goal_kras_goal_kra_id_seq restart with 1");

		alQueries.add("truncate goal_kras_amt_details");
		alQueries.add("ALTER SEQUENCE goal_kras_amt_details_goal_kra_amt_id_seq restart with 1");

		alQueries.add("truncate grades_details");
		alQueries.add("ALTER SEQUENCE grades_details_grade_id_seq restart with 1");

		alQueries.add("truncate gratuity_details");
		alQueries.add("ALTER SEQUENCE gratuity_details_gratuity_id_seq restart with 1");
		
		alQueries.add("truncate hobbies_details");
		alQueries.add("ALTER SEQUENCE hobbies_details_hobbies_id_seq restart with 1");

		alQueries.add("truncate holiday_count");
		alQueries.add("ALTER SEQUENCE holiday_count_holiday_count_id_seq restart with 1");

		alQueries.add("truncate holidays");
		alQueries.add("ALTER SEQUENCE holidays_holiday_id_seq restart with 1");

//		alQueries.add("truncate hra_exemption_details");
//		alQueries.add("ALTER SEQUENCE hra_exemption_details_hra_id_seq restart with 1");

		alQueries.add("truncate ideal_time_details");
		alQueries.add("ALTER SEQUENCE ideal_time_details_ideal_id_seq restart with 1");
		
		alQueries.add("truncate incentive_details");
		alQueries.add("ALTER SEQUENCE incentive_details_incentive_id_seq restart with 1");

		alQueries.add("truncate increment_details");
		alQueries.add("ALTER SEQUENCE increment_details_increment_id_seq restart with 1");

		alQueries.add("truncate increment_details_da");
		alQueries.add("ALTER SEQUENCE increment_details_da_increment_id_seq restart with 1");

		alQueries.add("truncate investment_details");
		alQueries.add("ALTER SEQUENCE investment_details_emp_investment_id_seq restart with 1");

		alQueries.add("truncate investment_documents");
		alQueries.add("ALTER SEQUENCE investment_documents_investment_doc_id_seq restart with 1");

		alQueries.add("truncate kra_details");
		alQueries.add("ALTER SEQUENCE kra_details_kra_id_seq restart with 1");

		alQueries.add("truncate kra_rating_details");
		alQueries.add("ALTER SEQUENCE kra_rating_details_kra_rating_id_seq restart with 1");
		
		alQueries.add("truncate languages_details");
		alQueries.add("ALTER SEQUENCE languages_details_language_id_seq restart with 1");

		alQueries.add("truncate learning_plan_details");
		alQueries.add("ALTER SEQUENCE learning_plan_details_learning_plan_id_seq restart with 1");

		alQueries.add("truncate learning_plan_finalize_details");
		alQueries.add("ALTER SEQUENCE learning_plan_finalize_details_learning_plan_finalize_id_seq restart with 1");

		alQueries.add("truncate learning_plan_question_bank");
		alQueries.add("ALTER SEQUENCE learning_plan_question_bank_learning_plan_question_bank_id_seq restart with 1");

		alQueries.add("truncate learning_plan_stage_details");
		alQueries.add("ALTER SEQUENCE learning_plan_stage_details_learning_plan_stage_id_seq restart with 1");

		alQueries.add("truncate leave_application_register");
		alQueries.add("ALTER SEQUENCE leave_application_register_leave_register_id_seq restart with 1");

		alQueries.add("truncate leave_break_type");
		alQueries.add("ALTER SEQUENCE leave_break_type_break_type_id_seq restart with 1");

		alQueries.add("truncate leave_details");
		alQueries.add("ALTER SEQUENCE leave_details_leave_id_seq restart with 1");

		alQueries.add("truncate leave_opt_holiday_details");
		alQueries.add("ALTER SEQUENCE leave_opt_holiday_details_leave_opt_holiday_id_seq restart with 1");
		
		alQueries.add("truncate leave_register");
		alQueries.add("ALTER SEQUENCE leave_register_leave_register_id_seq restart with 1");

		alQueries.add("truncate leave_register1");
		alQueries.add("ALTER SEQUENCE leave_register1_register_id_seq restart with 1");

		alQueries.add("truncate leave_time_period");
		alQueries.add("ALTER SEQUENCE leave_time_period_leave_time_period_id_seq restart with 1");
		
		alQueries.add("truncate leave_type");
		alQueries.add("ALTER SEQUENCE leave_type_leave_type_id_seq restart with 1");

		alQueries.add("truncate leaves");
		alQueries.add("ALTER SEQUENCE leaves_leaves_id_seq restart with 1");

		alQueries.add("truncate level_details");
		alQueries.add("ALTER SEQUENCE level_details_level_id_seq restart with 1");

		alQueries.add("truncate level_skill_rates");
//		alQueries.add("ALTER SEQUENCE level_details_level_id_seq restart with 1");
		
		alQueries.add("truncate loan_applied_details");
		alQueries.add("ALTER SEQUENCE loan_applied_details_loan_applied_id_seq restart with 1");

		alQueries.add("truncate loan_details");
		alQueries.add("ALTER SEQUENCE loan_details_loan_id_seq restart with 1");

		alQueries.add("truncate loan_payments");
		alQueries.add("ALTER SEQUENCE loan_payments_loan_payment_id_seq restart with 1");

		alQueries.add("truncate log_details");
		alQueries.add("ALTER SEQUENCE log_details_log_id_seq restart with 1");
		
		alQueries.add("truncate login_timestamp");
		alQueries.add("ALTER SEQUENCE login_timestamp_login_timestamp_id_seq restart with 1");

		alQueries.add("truncate lta_details");
		alQueries.add("ALTER SEQUENCE lta_details_lta_id_seq restart with 1");

//		alQueries.add("truncate lwf_details");//
//		alQueries.add("ALTER SEQUENCE lwf_details_lwf_id_seq restart with 1");
		
		alQueries.add("truncate mail");
		alQueries.add("ALTER SEQUENCE mail_mail_id_seq restart with 1");

		alQueries.add("truncate meeting_room_booking_details");
		alQueries.add("ALTER SEQUENCE meeting_room_booking_details_booking_id_seq restart with 1");

		alQueries.add("truncate meeting_room_details");
		alQueries.add("ALTER SEQUENCE meeting_room_details_meeting_room_id_seq restart with 1");
		
		alQueries.add("truncate mobile_recovery_individual_details");
		alQueries.add("ALTER SEQUENCE mobile_recovery_individual_details_mobile_recovery_id_seq restart with 1");

		alQueries.add("truncate mobile_reimbursement_individual_details");
		alQueries.add("ALTER SEQUENCE mobile_reimbursement_individual_det_mobile_reimbursement_id_seq restart with 1");

		
		alQueries.add("truncate org_details");
		alQueries.add("ALTER SEQUENCE org_details_org_id_seq restart with 1");

//		alQueries.add("truncate orientation_details");
//		alQueries.add("ALTER SEQUENCE orientation_details_orientation_details_id_seq restart with 1");

		alQueries.add("truncate otherdeduction_individual_details");
		alQueries.add("ALTER SEQUENCE otherdeduction_individual_details_otherdeduction_id_seq restart with 1");

		alQueries.add("truncate otherearning_individual_details");
		alQueries.add("ALTER SEQUENCE otherearning_individual_details_otherearning_id_seq restart with 1");

		alQueries.add("truncate overtime_details");
		alQueries.add("ALTER SEQUENCE overtime_details_overtime_id_seq restart with 1");

		alQueries.add("truncate overtime_emp_minute_status");
		alQueries.add("ALTER SEQUENCE overtime_emp_minute_status_overtime_emp_minute_id_seq restart with 1");

		alQueries.add("truncate overtime_hours");
		alQueries.add("ALTER SEQUENCE overtime_hours_overtime_hours_id_seq restart with 1");

		alQueries.add("truncate overtime_individual_details");
		alQueries.add("ALTER SEQUENCE overtime_individual_details_overtime_id_seq restart with 1");

		alQueries.add("truncate overtime_minute_slab");
		alQueries.add("ALTER SEQUENCE overtime_minute_slab_overtime_minute_id_seq restart with 1");
		
		alQueries.add("truncate panel_interview_details");
		alQueries.add("ALTER SEQUENCE panel_interview_details_panel_interview_id_seq restart with 1");

		alQueries.add("truncate payroll");
		alQueries.add("ALTER SEQUENCE payroll_payroll_id_seq restart with 1");

		alQueries.add("truncate payroll_bank_statement");
		alQueries.add("ALTER SEQUENCE payroll_bank_statement_statement_id_seq restart with 1");

		alQueries.add("truncate payroll_generation");
		alQueries.add("ALTER SEQUENCE payroll_generation_generation_id_seq restart with 1");

		alQueries.add("truncate payroll_generation_lta");
		alQueries.add("ALTER SEQUENCE payroll_generation_lta_generation_id_seq restart with 1");

		alQueries.add("truncate payroll_history");
		alQueries.add("ALTER SEQUENCE payroll_history_payroll_history_id_seq restart with 1");
		
		alQueries.add("truncate payroll_policy");
		alQueries.add("ALTER SEQUENCE payroll_policy_payroll_policy_id_seq restart with 1");

		alQueries.add("truncate performance_details");
		alQueries.add("ALTER SEQUENCE performance_details_performance_id_seq restart with 1");

		alQueries.add("truncate performance_details_empwise");
		alQueries.add("ALTER SEQUENCE performance_details_empwise_performance_empwise_id_seq restart with 1");

		alQueries.add("truncate perk_assign_salary_details");
		alQueries.add("ALTER SEQUENCE perk_assign_salary_details_perk_assign_salary_id_seq restart with 1");

		alQueries.add("truncate perk_details");
		alQueries.add("ALTER SEQUENCE perks_details_perks_id_seq restart with 1");

		alQueries.add("truncate perk_salary_applied_details");
		alQueries.add("ALTER SEQUENCE perk_salary_applied_details_perk_salary_applied_id_seq restart with 1");

		alQueries.add("truncate perk_salary_applied_paycycle");
		alQueries.add("ALTER SEQUENCE perk_salary_applied_paycycle_salary_applied_paycycle_id_seq restart with 1");
		
		alQueries.add("truncate perk_salary_details");
		alQueries.add("ALTER SEQUENCE perk_salary_details_perk_salary_id_seq restart with 1");
		
		alQueries.add("truncate plan_status_details");
		alQueries.add("ALTER SEQUENCE plan_status_details_plan_status_id_seq restart with 1");

		alQueries.add("truncate porject_billing_heads_details");
		alQueries.add("ALTER SEQUENCE porject_billing_heads_details_pro_billing_head_id_seq restart with 1");
		
		alQueries.add("truncate prev_earn_deduct_details");
		alQueries.add("ALTER SEQUENCE prev_earn_deduct_details_prev_earn_deduct_id_seq restart with 1");

		alQueries.add("truncate probation_policy");
		alQueries.add("ALTER SEQUENCE probation_policy_probation_id_seq restart with 1");

		alQueries.add("truncate production_line_details");
		alQueries.add("ALTER SEQUENCE production_line_details_production_line_id_seq restart with 1");

		alQueries.add("truncate production_line_heads");
		alQueries.add("ALTER SEQUENCE production_line_heads_production_line_head_id_seq restart with 1");

		alQueries.add("truncate profession_details");
		alQueries.add("ALTER SEQUENCE profession_profession_id_seq restart with 1");

		alQueries.add("truncate project_category_details");
		alQueries.add("ALTER SEQUENCE project_category_details_project_category_id_seq restart with 1");
		
		alQueries.add("truncate project_document_details");
		alQueries.add("ALTER SEQUENCE project_document_details_pro_document_id_seq restart with 1");

		alQueries.add("truncate project_documents_details");
		alQueries.add("ALTER SEQUENCE project_documents_details_sr_no_seq restart with 1");

		alQueries.add("truncate project_emp_details");
		alQueries.add("ALTER SEQUENCE project_emp_details_sr_no_seq restart with 1");

		alQueries.add("truncate project_information_display");
		alQueries.add("ALTER SEQUENCE project_information_display_project_info_display_id_seq restart with 1");

		alQueries.add("truncate project_invoice_details");
		alQueries.add("ALTER SEQUENCE project_invoice_details_project_invoice_id_seq restart with 1");

		alQueries.add("truncate project_milestone");
		alQueries.add("ALTER SEQUENCE project_milestone_milestone_id_seq restart with 1");

		alQueries.add("truncate project_milestone_details");
		alQueries.add("ALTER SEQUENCE project_milestone_details_project_milestone_id_seq restart with 1");

		alQueries.add("truncate project_skill_details");
		alQueries.add("ALTER SEQUENCE project_skill_details_sr_no_seq restart with 1");

		alQueries.add("truncate project_tax_setting");
		alQueries.add("ALTER SEQUENCE project_tax_setting_pro_tax_setting_id_seq restart with 1");
		
		alQueries.add("truncate project_timesheet");
		alQueries.add("ALTER SEQUENCE project_timesheet_timesheet_id_seq restart with 1");

		alQueries.add("truncate projectmntnc");
		alQueries.add("ALTER SEQUENCE projectmntnc_pro_id_seq restart with 1");

		alQueries.add("truncate projectmntnc_billing");
		alQueries.add("ALTER SEQUENCE projectmntnc_billing_pro_billing_id_seq restart with 1");

		alQueries.add("truncate projectmntnc_frequency");
		alQueries.add("ALTER SEQUENCE projectmntnc_frequency_pro_freq_id_seq restart with 1");
		
		alQueries.add("truncate promntc_bill_amt_details");
		alQueries.add("ALTER SEQUENCE promntc_bill_amt_details_bill_id_seq restart with 1");

		alQueries.add("truncate promntc_bill_parti_amt_details");
		alQueries.add("ALTER SEQUENCE promntc_bill_parti_amt_details_promntc_bill_parti_amt_id_seq restart with 1");
		
		alQueries.add("truncate promntc_invoice_amt_details");
		alQueries.add("ALTER SEQUENCE promntc_invoice_amt_details_promntc_invoice_amt_id_seq restart with 1");

		alQueries.add("truncate promntc_invoice_details");
		alQueries.add("ALTER SEQUENCE promntc_invoice_details_promntc_invoice_id_seq restart with 1");

		alQueries.add("truncate question_bank");
		alQueries.add("ALTER SEQUENCE question_bank_question_bank_id_seq restart with 1");

		alQueries.add("truncate question_list");
		alQueries.add("ALTER SEQUENCE question_list_question_id_seq restart with 1");
		
		alQueries.add("truncate reconciliation_details");
		alQueries.add("ALTER SEQUENCE reconciliation_details_reconciliation_id_seq restart with 1");
		
		alQueries.add("truncate recruitment_details");
		alQueries.add("ALTER SEQUENCE recruitment_details_recruitment_id_seq restart with 1");

		alQueries.add("truncate reimbursement_assign_head_details");
		alQueries.add("ALTER SEQUENCE reimbursement_assign_head_details_reim_assign_head_id_seq restart with 1");

		alQueries.add("truncate reimbursement_ctc_applied_details");
		alQueries.add("ALTER SEQUENCE reimbursement_ctc_applied_details_reim_ctc_applied_id_seq restart with 1");

		alQueries.add("truncate reimbursement_ctc_applied_paycycle");
		alQueries.add("ALTER SEQUENCE reimbursement_ctc_applied_paycycle_reimctcapply_paycycle_id_seq restart with 1");

		alQueries.add("truncate reimbursement_ctc_details");
		alQueries.add("ALTER SEQUENCE reimbursement_ctc_details_reimbursement_ctc_id_seq restart with 1");

		alQueries.add("truncate reimbursement_ctc_pay");
		alQueries.add("ALTER SEQUENCE reimbursement_ctc_pay_reimbursement_ctc_pay_id_seq restart with 1");
		
		alQueries.add("truncate reimbursement_ctc_tax_pay");
		alQueries.add("ALTER SEQUENCE reimbursement_ctc_tax_pay_reim_ctc_tax_pay_id_seq restart with 1");
		
		alQueries.add("truncate reimbursement_head_amt_details");
		alQueries.add("ALTER SEQUENCE reimbursement_head_amt_details_reimbursement_head_amt_id_seq restart with 1");
		
		alQueries.add("truncate reimbursement_head_details");
		alQueries.add("ALTER SEQUENCE reimbursement_head_details_reimbursement_head_id_seq restart with 1");
		
		alQueries.add("truncate reimbursement_policy");
		alQueries.add("ALTER SEQUENCE reimbursement_policy_reimbursement_policy_id_seq restart with 1");
		
		alQueries.add("truncate requisition_bonafide");
		alQueries.add("ALTER SEQUENCE requisition_bonafide_bonafide_id_seq restart with 1");

		alQueries.add("truncate requisition_details");
		alQueries.add("ALTER SEQUENCE requisition_details_requisition_id_seq restart with 1");

		alQueries.add("truncate requisition_infrastructure");
		alQueries.add("ALTER SEQUENCE requisition_infrastructure_infrastructure_id_seq restart with 1");

		alQueries.add("truncate requisition_other");
		alQueries.add("ALTER SEQUENCE requisition_other_other_id_seq restart with 1");

		alQueries.add("truncate resource_planner_details");
		alQueries.add("ALTER SEQUENCE resource_planner_details_resource_planner_id_seq restart with 1");

		alQueries.add("truncate roster_details");
		alQueries.add("ALTER SEQUENCE roster_details_roster_id_seq restart with 1");

		alQueries.add("truncate roster_fullday_policy");
		alQueries.add("ALTER SEQUENCE roster_fullday_policy_roster_full_policy_id_seq restart with 1");

		alQueries.add("truncate roster_halfday_policy");
		alQueries.add("ALTER SEQUENCE roster_halfday_policy_roster_hd_policy_id_seq restart with 1");

		alQueries.add("truncate roster_policy");
		alQueries.add("ALTER SEQUENCE roster_policy_roster_policy_id_seq restart with 1");

		alQueries.add("truncate roster_weekly_off");
		alQueries.add("ALTER SEQUENCE roster_weekly_off_roster_weekoff_id_seq restart with 1");

		alQueries.add("truncate roster_weeklyoff_policy");
		alQueries.add("ALTER SEQUENCE roster_weeklyoff_policy_roster_weeklyoff_id_seq restart with 1");

		alQueries.add("truncate service_skills_details");
		alQueries.add("ALTER SEQUENCE service_skills_details_service_skill_id_seq restart with 1");
		
		alQueries.add("truncate services");
		alQueries.add("ALTER SEQUENCE services_service_id_seq restart with 1");

		alQueries.add("truncate services_project");
		alQueries.add("ALTER SEQUENCE services_project_service_project_id_seq restart with 1");

		alQueries.add("truncate services_project_sbu");
		alQueries.add("ALTER SEQUENCE services_project_sbu_services_pro_sbu_id_seq restart with 1");

		alQueries.add("truncate shift_details");
		alQueries.add("ALTER SEQUENCE shift_details_shift_id_seq restart with 1");

		alQueries.add("truncate skills_description");
		alQueries.add("ALTER SEQUENCE skills_description_skills_id_seq restart with 1");

		alQueries.add("truncate skills_details");
		alQueries.add("ALTER SEQUENCE skills_details_skill_id_seq restart with 1");

		alQueries.add("truncate specific_emp");
		alQueries.add("ALTER SEQUENCE specific_emp_specific_emp_id_seq restart with 1");

		alQueries.add("truncate statutory_id_registration_info_history");
		alQueries.add("ALTER SEQUENCE statutory_id_registration_info_history_stat_id_reg_info_id_seq restart with 1");

		alQueries.add("truncate successionplan_criteria_details");
		alQueries.add("ALTER SEQUENCE successionplan_criteria_details_successionplan_criteria_id_seq restart with 1");
		
		alQueries.add("truncate target_details");
		alQueries.add("ALTER SEQUENCE target_details_target_id_seq restart with 1");

		alQueries.add("truncate task_activity");
		alQueries.add("ALTER SEQUENCE task_activity_task_id_seq restart with 1");

		alQueries.add("truncate task_screenshot_details");
		alQueries.add("ALTER SEQUENCE task_screenshot_details_screenshot_id_seq restart with 1");
		
//		alQueries.add("truncate task_type_setting");
//		alQueries.add("ALTER SEQUENCE task_type_setting_task_type_setting_id_seq restart with 1");
		
		alQueries.add("truncate taskrig_user_alerts");
		alQueries.add("ALTER SEQUENCE taskrig_user_alerts_alerts_id_seq restart with 1");
		
		alQueries.add("truncate tax_setting");
		alQueries.add("ALTER SEQUENCE tax_setting_tax_setting_id_seq restart with 1");
		
		alQueries.add("truncate tax_setting_history");
		alQueries.add("ALTER SEQUENCE tax_setting_history_tax_setting_history_id_seq restart with 1");
		
		alQueries.add("truncate tds_projections");
		alQueries.add("ALTER SEQUENCE tds_projections_tds_id_seq restart with 1");

		alQueries.add("truncate trainer_documents_details");
		alQueries.add("ALTER SEQUENCE trainer_documents_details_documents_id_seq restart with 1");

		alQueries.add("truncate trainer_education_details");
		alQueries.add("ALTER SEQUENCE trainer_education_details_degree_id_seq restart with 1");

		alQueries.add("truncate trainer_family_members");
		alQueries.add("ALTER SEQUENCE trainer_family_members_member_id_seq restart with 1");

		alQueries.add("truncate trainer_hobbies_details");
		alQueries.add("ALTER SEQUENCE trainer_hobbies_details_hobbies_id_seq restart with 1");

		alQueries.add("truncate trainer_languages_details");
		alQueries.add("ALTER SEQUENCE trainer_languages_details_language_id_seq restart with 1");

		alQueries.add("truncate trainer_medical_details");
		alQueries.add("ALTER SEQUENCE trainer_medical_details_medical_id_seq restart with 1");

		alQueries.add("truncate trainer_personal_details");
		alQueries.add("ALTER SEQUENCE trainer_personal_details_trainer_id_seq restart with 1");

		alQueries.add("truncate trainer_prev_employment");
		alQueries.add("ALTER SEQUENCE trainer_prev_employment_company_id_seq restart with 1");

		alQueries.add("truncate trainer_references");
		alQueries.add("ALTER SEQUENCE trainer_references_ref_id_seq restart with 1");

		alQueries.add("truncate trainer_skills_description");
		alQueries.add("ALTER SEQUENCE trainer_skills_description_skills_id_seq restart with 1");

//		alQueries.add("truncate training_answer_type");
//		alQueries.add("ALTER SEQUENCE training_answer_type_answer_type_id_seq restart with 1");
//
//		alQueries.add("truncate training_answer_type_sub");
//		alQueries.add("ALTER SEQUENCE training_answer_type_sub_training_answer_type_sub_id_seq restart with 1");

		alQueries.add("truncate training_attend_details");
		alQueries.add("ALTER SEQUENCE training_attend_details_training_attend_id_seq restart with 1");

		alQueries.add("truncate training_certificate");
		alQueries.add("ALTER SEQUENCE training_certificate_certificate_id_seq restart with 1");

		alQueries.add("truncate training_gap_details");
		alQueries.add("ALTER SEQUENCE training_gap_details_training_gap_id_seq restart with 1");

		alQueries.add("truncate training_learnings");
		alQueries.add("ALTER SEQUENCE training_learnings_learning_id_seq restart with 1");

		alQueries.add("truncate training_mark_grade_type");
		alQueries.add("ALTER SEQUENCE training_mark_grade_type_training_mark_grade_type_id_seq restart with 1");

		alQueries.add("truncate training_plan");
		alQueries.add("ALTER SEQUENCE training_plan_plan_id_seq restart with 1");

		alQueries.add("truncate training_question_answer");
		alQueries.add("ALTER SEQUENCE training_question_answer_training_question_answer_id_seq restart with 1");

		alQueries.add("truncate training_question_bank");
		alQueries.add("ALTER SEQUENCE training_question_bank_training_question_bank_id_seq restart with 1");

		alQueries.add("truncate training_question_details");
		alQueries.add("ALTER SEQUENCE training_question_details_training_question_id_seq restart with 1");

		alQueries.add("truncate training_schedule");
		alQueries.add("ALTER SEQUENCE training_schedule_schedule_id_seq restart with 1");

		alQueries.add("truncate training_schedule_details");
		alQueries.add("ALTER SEQUENCE training_schedule_details_training_schedule_details_id_seq restart with 1");

		alQueries.add("truncate training_session");
		alQueries.add("ALTER SEQUENCE training_session_session_id_seq restart with 1");

		alQueries.add("truncate training_status");
		alQueries.add("ALTER SEQUENCE training_status_status_id_seq restart with 1");

		alQueries.add("truncate training_trainer");
		alQueries.add("ALTER SEQUENCE training_trainer_trainer_id_seq restart with 1");

		alQueries.add("truncate travel_advance");
		alQueries.add("ALTER SEQUENCE travel_advance_advance_id_seq restart with 1");

		alQueries.add("truncate travel_advance_eligibility");
		alQueries.add("ALTER SEQUENCE travel_advance_eligibility_eligibility_id_seq restart with 1");

		alQueries.add("truncate travel_application_register");
		alQueries.add("ALTER SEQUENCE travel_application_register_travel_register_id_seq restart with 1");

		alQueries.add("truncate travel_booking_documents");
		alQueries.add("ALTER SEQUENCE travel_booking_documents_travel_booking_id_seq restart with 1");
		
		alQueries.add("truncate user_alerts");
		alQueries.add("ALTER SEQUENCE user_alerts_user_alerts_id_seq restart with 1");

		alQueries.add("truncate user_details");
		alQueries.add("ALTER SEQUENCE user_details_user_id_seq restart with 1");

		alQueries.add("truncate user_details_customer");
		alQueries.add("ALTER SEQUENCE user_details_customer_user_id_seq restart with 1");

		alQueries.add("truncate variable_cost");
		alQueries.add("ALTER SEQUENCE variable_cost_tbl_variable_cost_id_seq restart with 1");

		alQueries.add("truncate vda_rate_details");
		alQueries.add("ALTER SEQUENCE vda_rate_details_vda_rate_id_seq restart with 1");

		alQueries.add("truncate work_flow_details");
		alQueries.add("ALTER SEQUENCE work_flow_details_work_flow_id_seq restart with 1");

		alQueries.add("truncate work_flow_member");
		alQueries.add("ALTER SEQUENCE work_flow_member_work_flow_member_id_seq restart with 1");

		alQueries.add("truncate work_flow_policy");
		alQueries.add("ALTER SEQUENCE work_flow_policy_work_flow_policy_id_seq restart with 1");

		alQueries.add("truncate work_flow_policy_details");
		alQueries.add("ALTER SEQUENCE work_flow_policy_details_work_flow_policy_details_id_seq restart with 1");

		alQueries.add("truncate work_location_info");
		alQueries.add("ALTER SEQUENCE work_location_info_wlocation_id_seq restart with 1");

		alQueries.add("truncate work_location_type");
		alQueries.add("ALTER SEQUENCE work_location_type_work_location_id_seq restart with 1");
		
		alQueries.add("truncate workflow_policy_period");
		alQueries.add("ALTER SEQUENCE workflow_policy_period_workflow_policy_period_id_seq restart with 1");
		
		alQueries.add("truncate workrig_user_alerts");
		alQueries.add("ALTER SEQUENCE workrig_user_alerts_alerts_id_seq restart with 1");
		
		
		
		alQueries.add("truncate reminder_details");
		alQueries.add("ALTER SEQUENCE reminder_details_reminder_id_seq restart with 1");

		alQueries.add("truncate requirement_employment_type");
		alQueries.add("ALTER SEQUENCE requirement_employment_type_employment_type_id_seq restart with 1");

		alQueries.add("truncate notices");
		alQueries.add("ALTER SEQUENCE notices_notice_id_seq restart with 1");

		alQueries.add("truncate resource_plan_request_details");
		alQueries.add("ALTER SEQUENCE resource_plan_request_details_resource_plan_request_id_seq restart with 1");
		
		alQueries.add("truncate project_resource_req_details");
		alQueries.add("ALTER SEQUENCE project_resource_req_details_project_resource_req_id_seq restart with 1");
		
		alQueries.add("truncate service_tasks_details");
		alQueries.add("ALTER SEQUENCE service_tasks_details_service_task_id_seq restart with 1");
//		
		alQueries.add("truncate emp_it_slab_access_details");
		alQueries.add("ALTER SEQUENCE emp_it_slab_access_details_emp_it_slab_access_id_seq restart with 1");
		
		alQueries.add("truncate client_brand_details");
		alQueries.add("ALTER SEQUENCE client_brand_details_client_brand_id_seq restart with 1");
		
//		alQueries.add("truncate assign_shift_dates");
//		alQueries.add("ALTER SEQUENCE  restart with 1");
		
		alQueries.add("truncate roster_policy_rules");
		alQueries.add("ALTER SEQUENCE roster_policy_rules_roster_policy_rule_id_seq restart with 1");
		
		alQueries.add("truncate roster_halfday_fullday_hrs_policy");
		alQueries.add("ALTER SEQUENCE roster_halfday_fullday_hrs_po_roster_halfday_fullday_hrs_id_seq restart with 1");
		
		alQueries.add("truncate reviewee_strength_improvements");
		alQueries.add("ALTER SEQUENCE reviewee_strength_improvement_reviewee_strength_improvement_seq restart with 1");
		
		alQueries.add("truncate review_feedback_reopen_details");
		alQueries.add("ALTER SEQUENCE review_feedback_reopen_details_review_feedback_reopen_id_seq restart with 1");
		
		alQueries.add("truncate review_final_recommendation");
		alQueries.add("ALTER SEQUENCE review_final_recommendation_review_final_recommendation_id_seq restart with 1");
		
		alQueries.add("truncate emp_shift_trans_details");
		alQueries.add("ALTER SEQUENCE emp_shift_trans_details_emp_shift_trans_id_seq restart with 1");
		
		alQueries.add("truncate hr_client_visit_details");
		alQueries.add("ALTER SEQUENCE hr_client_visit_details_visit_id_seq restart with 1");
		
		alQueries.add("truncate parameter_details");
		alQueries.add("ALTER SEQUENCE parameter_details_parameter_id_seq restart with 1");
		
		alQueries.add("truncate faq_details");
		alQueries.add("ALTER SEQUENCE faq_details_faq_id_seq restart with 1");
		
		alQueries.add("truncate bsc_details");
		alQueries.add("ALTER SEQUENCE bsc_details_bsc_id_seq restart with 1");
		
		alQueries.add("truncate bsc_perspective_details");
		alQueries.add("ALTER SEQUENCE bsc_perspective_details_bsc_perspective_id_seq restart with 1");
		
	}
}