package com.konnect.jpms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.util.HSSFColor.AQUA;

public class DBScript_V2 {
	
	public static void main(String args[]) { 
	
		List<String> alQueries = new ArrayList<String>();
		DBScript_V2 db = new DBScript_V2();
		db.fillQueriesAlterAddColumn(alQueries);
//		db.fillQueriesAlterForeinKey(alQueries);
		
		alQueries.add("");
		alQueries.add("");
		alQueries.add("");
		
		db.getFile(alQueries);
		
	}

	public void getFile(List<String> alQueries) {
		
		try {
			
			PreparedStatement pst = null; 
			Connection conn = null;
			
//			String HOST = "localhost";
//			String PORT = "5432";
//			String DBUSERNAME = "postgres";
//			String DBPASSWORD = "dbPassword";
////		String DBPASSWORD = "postgres"; 
//			
////		String []arrDBNAME = {"demo_payroll", "kp1_payroll", "kp2_payroll", "kp3_payroll"}; 
//			String []arrDBNAME = {"truebuild_payroll"};  
			
//			String HOST 		= "108.163.186.66";
//			String PORT 		= "5432";
//			String DBUSERNAME	= "postgres";
//			String DBPASSWORD 	= "2Bt4Re7Va1";
//			String []arrDBNAME = {"test_payroll"};
			
//			//String HOST 		= "54.152.10.99"; 
			
			String HOST 		= "52.204.34.153";
//			String HOST 		= "localhost";
//			String HOST 		= "3.7.209.244";
			String PORT 		= "5432";
			String DBUSERNAME	= "postgres";
			String DBPASSWORD 	= "dbPassword";
//			String DBPASSWORD 	= "Pune123!";
			String []arrDBNAME = {"bct_sandbox"};
//			String []arrDBNAME = {"postgres"};
			
//			String []arrDBNAME = {"kcs_payroll", "testing_payroll", "test_payroll", "demo_level_payroll", "demo_grade_payroll", "default_payroll", "vittalsmedicare_payroll", 
//				"konnecttech_payroll", "workrig_newui", "craveinfotech_payroll", "xanadu_payroll", "workrig_newui1", "booksntaxes_payroll", "hbfuller_payroll", 
//				"testing_newui", "egovtech_payroll", "fernandez_hospital", "vishnu_payroll", "kpca_payroll", "shrosystems_payroll", 
//				"olympia_elevators_payroll", "omgajanan_payroll", "clairvoyant_demo_payroll", "clairvoyant_payroll", "clairvoyant_dev_payroll", 
//				"cuembux_payroll", "clairvoyant_test2_payroll", "fairpattern_payroll", "sant_sopankaka_payroll", "rrf_demo_payroll", "client_demo_payroll", 
//				"demo_taskrig", "demo_corehr_payroll", "konnect_vaadin_payroll", "ethinos_pm", "egovtech_payroll_test", "adv_sangietaa_asso_payroll", 
//				"iroc_payroll", "exusia_payroll", "clairvoyant_test_payroll", "loopmethods_payroll", "kpca_payroll_olddata", "workrig_demo_us", 
//				"workrig_demo_us_test", "truebuild_payroll", "gamaya_payroll", "vspl_payroll", "vspl_payroll_test", "prarambhika_learning", "test_newjoin_payroll", 
//				"highr_demo", "workrig_shr_pms", "edu_payroll", "xpanxion_demo", "vspl_payroll_1_test", "haqdarshak_payroll", "haqdarshak_sandbox", 
//				"haqdarshak_test", "mfg_payroll", "workrig_plus", "inteliment_payroll", "inteliment_sandbox", "app_payroll", "client_trial_payroll", "client_trial",
//				"client_trial_inteliment","quloi_payroll", "quloi_sandbox", "quloi_test"};
			
			
			for(int i=0; i<arrDBNAME.length; i++) {
				
				try {
					Class.forName("org.postgresql.Driver");
					conn = DriverManager.getConnection("jdbc:postgresql://" + HOST + ":"+PORT+"/" + arrDBNAME[i], DBUSERNAME, DBPASSWORD);
					
					System.out.println("======== EXECUTION STARTED ON "+arrDBNAME[i]);
					
					for(int al=0; al<alQueries.size(); al++) {
						try {
							pst = conn.prepareStatement(alQueries.get(al));
							pst.execute();
							pst.close();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if(pst!=null){
								pst.close();
								pst = null;
							}
						}
					}
					
					System.out.println("========EXECUTION FINISHED ON "+arrDBNAME[i]);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if(conn != null){
							conn.close();
							conn = null;
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void fillQueriesAlterForeinKey(List<String> alQueries) {
		alQueries.add("");
		alQueries.add("");
		alQueries.add("");
		alQueries.add("");
		alQueries.add("");
		alQueries.add("");
		alQueries.add("");
		alQueries.add("");
		alQueries.add("");
		alQueries.add("");
		alQueries.add("");
		alQueries.add("");
		alQueries.add("");
		alQueries.add("ALTER TABLE public.projectmntnc ADD CONSTRAINT projectmntnc_reference_by_id_fkey FOREIGN KEY (reference_by_id) REFERENCES employee_personal_details (emp_per_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.projectmntnc ADD CONSTRAINT projectmntnc_bank_id_fkey FOREIGN KEY (bank_id) REFERENCES bank_details (bank_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.projectmntnc ADD CONSTRAINT projectmntnc_org_id_fkey FOREIGN KEY (org_id) REFERENCES org_details (org_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.projectmntnc ADD CONSTRAINT projectmntnc_sbu_id_fkey FOREIGN KEY (sbu_id) REFERENCES services (service_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.projectmntnc ADD CONSTRAINT projectmntnc_project_owner_fkey FOREIGN KEY (project_owner) REFERENCES employee_personal_details (emp_per_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.projectmntnc ADD CONSTRAINT projectmntnc_department_id_fkey FOREIGN KEY (department_id) REFERENCES department_info (dept_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.projectmntnc ADD CONSTRAINT projectmntnc_wlocation_id_fkey FOREIGN KEY (wlocation_id) REFERENCES work_location_info (wlocation_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.projectmntnc ADD CONSTRAINT projectmntnc_added_by_fkey FOREIGN KEY (added_by) REFERENCES employee_personal_details (emp_per_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("update projectmntnc set poc = null where poc=0;");
		alQueries.add("ALTER TABLE public.projectmntnc ADD CONSTRAINT projectmntnc_poc_fkey FOREIGN KEY (poc) REFERENCES client_poc (poc_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("update projectmntnc set client_id = null where client_id=0;");
		alQueries.add("ALTER TABLE public.projectmntnc ADD CONSTRAINT projectmntnc_client_id_fkey FOREIGN KEY (client_id) REFERENCES client_details (client_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD CONSTRAINT project_resource_req_details_req_benchmark_resource_id_fkey FOREIGN KEY (benchmark_resource_id) REFERENCES employee_personal_details (emp_per_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD CONSTRAINT project_resource_req_details_grade_id_fkey FOREIGN KEY (grade_id) REFERENCES grades_details (grade_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD CONSTRAINT project_resource_req_details_level_id_fkey FOREIGN KEY (level_id) REFERENCES level_details (level_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD CONSTRAINT project_resource_req_details_department_id_fkey FOREIGN KEY (department_id) REFERENCES department_info (dept_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD CONSTRAINT project_resource_req_details_org_id_fkey FOREIGN KEY (org_id) REFERENCES org_details (org_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD CONSTRAINT project_resource_req_details_jd_id_fkey FOREIGN KEY (jd_id) REFERENCES job_description_details (job_description_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD CONSTRAINT project_resource_req_details_req_created_by_fkey FOREIGN KEY (req_created_by) REFERENCES employee_personal_details (emp_per_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD CONSTRAINT project_resource_req_details_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES employee_personal_details (emp_per_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD CONSTRAINT project_resource_req_details_added_by_fkey FOREIGN KEY (added_by) REFERENCES employee_personal_details (emp_per_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD CONSTRAINT project_resource_req_details_pro_id_fkey FOREIGN KEY (pro_id) REFERENCES projectmntnc (pro_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.activity_info ADD CONSTRAINT activity_info_goal_kra_task_id_fkey FOREIGN KEY (goal_kra_task_id) REFERENCES goal_kra_tasks (goal_kra_task_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.activity_info ADD CONSTRAINT activity_info_kra_id_fkey FOREIGN KEY (kra_id) REFERENCES goal_kras (goal_kra_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.activity_info ADD CONSTRAINT activity_info_reschedule_reassign_align_by_fkey FOREIGN KEY (reschedule_reassign_align_by) REFERENCES employee_personal_details (emp_per_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.activity_info ADD CONSTRAINT activity_info_requested_by_fkey FOREIGN KEY (requested_by) REFERENCES employee_personal_details (emp_per_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.activity_info ADD CONSTRAINT activity_info_added_by_fkey FOREIGN KEY (added_by) REFERENCES employee_personal_details (emp_per_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.activity_info ADD CONSTRAINT activity_info_reassign_by_fkey FOREIGN KEY (reassign_by) REFERENCES employee_personal_details (emp_per_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("update activity_info set pro_id = null where pro_id=0;");
		alQueries.add("ALTER TABLE activity_info ALTER COLUMN pro_id TYPE integer;");
		alQueries.add("ALTER TABLE public.activity_info ADD CONSTRAINT activity_info_pro_id_fkey FOREIGN KEY (pro_id) REFERENCES projectmntnc (pro_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.skills_details ADD CONSTRAINT skills_details_org_id_fkey FOREIGN KEY (org_id) REFERENCES org_details (org_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.skills_details ADD CONSTRAINT skills_details_skill_category_id_fkey FOREIGN KEY (skill_category_id) REFERENCES skill_category (skill_category_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.skill_category ADD CONSTRAINT skill_category_added_by_fkey FOREIGN KEY (added_by) REFERENCES employee_personal_details (emp_per_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.resource_project_roles ADD CONSTRAINT resource_project_roles_added_by_fkey FOREIGN KEY (added_by) REFERENCES employee_personal_details (emp_per_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.project_emp_details ADD CONSTRAINT project_emp_details_resource_role_fkey FOREIGN KEY (resource_role) REFERENCES resource_project_roles (resource_project_role_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.project_emp_details ADD CONSTRAINT project_emp_details_emp_id_fkey FOREIGN KEY (emp_id) REFERENCES employee_personal_details (emp_per_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
		alQueries.add("ALTER TABLE public.project_emp_details ADD CONSTRAINT project_emp_details_pro_id_fkey FOREIGN KEY (pro_id) REFERENCES projectmntnc (pro_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;");
	}
	
	
	public void fillQueriesAlterAddColumn(List<String> alQueries) {
		
		alQueries.add("");
		alQueries.add("");
		alQueries.add("");
		
		alQueries.add("CREATE TABLE resource_project_roles\n" + 
				"(\n" + 
				"  resource_project_role_id serial NOT NULL,\n" + 
				"  project_role_name character varying,\n" + 
				"  added_by integer,\n" + 
				"  entry_date time without time zone,\n" + 
				"  CONSTRAINT resource_project_roles_pkey PRIMARY KEY (resource_project_role_id)\n" + 
				");\n" + 
				"ALTER TABLE resource_project_roles  OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE skill_category\n" +
				"(\n" + 
				"  skill_category_id serial NOT NULL,\n" + 
				"  skill_category_title character varying,\n" + 
				"  added_by integer,\n" + 
				"  entry_date time without time zone,\n" + 
				"  CONSTRAINT skill_category_pkey PRIMARY KEY (skill_category_id)\n" + 
				");\n" + 
				"ALTER TABLE skill_category OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE public.job_description_details (\n" + 
				"	job_description_id serial4 NOT NULL,\n" + 
				"	job_description_code varchar NULL,\n" + 
				"	job_description_title varchar NULL,\n" + 
				"	job_description_data varchar NULL,\n" + 
				"	added_by int4 NULL,\n" + 
				"	entry_date timestamp NULL,\n" + 
				"	updated_by int4 NULL,\n" + 
				"	update_date timestamp NULL,\n" + 
				"	CONSTRAINT job_description_details_pkey PRIMARY KEY (job_description_id)\n" + 
				");\n" +
				"ALTER TABLE skill_category OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD resource_request_title character varying NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD resource_request_id character varying NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD allocation_start_date date NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD allocation_end_date date NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD billing_start_date date NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD billing_end_date date NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD jd_id integer NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD type_of_vacancy integer NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD type_of_employment character varying NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD org_id integer NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD department_id integer NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD level_id integer NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD grade_id integer NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD resource_req_priority integer NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD must_have_skills character varying NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD nice_to_have_skills character varying NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD benchmark_resource_id integer NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD resource_allocation_type integer NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD allocation_percent integer NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD is_notes_add boolean NOT NULL DEFAULT false;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD additional_notes character varying NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD status integer NULL DEFAULT 0;");
		alQueries.add("ALTER TABLE public.project_emp_details ADD allocation_comment character varying NULL;");
		alQueries.add("ALTER TABLE public.project_emp_details ADD resource_request_id integer NULL;");
		alQueries.add("ALTER TABLE project_emp_details ADD COLUMN allocation_type integer;");
		alQueries.add("ALTER TABLE project_emp_details ADD COLUMN resource_role integer;");
		alQueries.add("ALTER TABLE public.skills_details ADD COLUMN skill_category_id integer;");
		alQueries.add("");
		alQueries.add("");
		alQueries.add("");
		
	}

}
