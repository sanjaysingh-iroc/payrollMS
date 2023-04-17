package com.konnect.jpms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;


public class DBScript {
	
	public static void main(String args[]) { 
	
		List<String> alQueries = new ArrayList<String>();
		DBScript db = new DBScript();
//		db.fillQueriesEarth(alQueries);  
//		db.fillQueriesJupiter(alQueries);
		
		/*alQueries.add("ALTER TABLE appraisal_question_details DROP COLUMN ceo;" +
				"ALTER TABLE appraisal_question_details DROP COLUMN hod;" +
				"ALTER TABLE appraisal_question_details DROP COLUMN client;" +
				"ALTER TABLE appraisal_question_details DROP COLUMN vendor;" +
				"ALTER TABLE appraisal_question_details DROP COLUMN grouphead;" +
				"ALTER TABLE appraisal_question_details DROP COLUMN subordinate;" +
				"ALTER TABLE appraisal_question_details DROP COLUMN self;" +
				"ALTER TABLE appraisal_question_details DROP COLUMN peer;" +
				"ALTER TABLE appraisal_question_details DROP COLUMN manager;" +
				"ALTER TABLE appraisal_question_details DROP COLUMN hr;");
		
		alQueries.add("ALTER TABLE appraisal_level_details DROP COLUMN ceo;" +
				"ALTER TABLE appraisal_level_details DROP COLUMN hod;" +
				"ALTER TABLE appraisal_level_details DROP COLUMN client;" +
				"ALTER TABLE appraisal_level_details DROP COLUMN vendor;" +
				"ALTER TABLE appraisal_level_details DROP COLUMN grouphead;" +
				"ALTER TABLE appraisal_level_details DROP COLUMN subordinate;" +
				"ALTER TABLE appraisal_level_details DROP COLUMN self;" +
				"ALTER TABLE appraisal_level_details DROP COLUMN peer;" +
				"ALTER TABLE appraisal_level_details DROP COLUMN manager;" +
				"ALTER TABLE appraisal_level_details DROP COLUMN hr;");*/
		
//		***************** AWS SES ************************
//		alQueries.add("update settings set value = 'email-smtp.us-east-1.amazonaws.com' where options = 'EMAIL_HOST';" +
//				"update settings set value = 'no-reply-support@workrig.com' where options = 'EMAIL_FROM';" +
//				"update settings set value = '579$AeDrFtG@246' where options = 'HOST_PASSWORD';" +
//				"update settings set value = 'AKIAV7WWKQM377TRI67Y' where options = 'EMAIL_AUTHENTICATION_USER';" +
//				"update settings set value = 'BPauwh8evw4RW0LMiIf7Sjf/kzmlAQOp0pOs8LPY9Zty' where options = 'EMAIL_AUTHENTICATION_PASSWORD';");
		
//		***************** Mail Jet ***********************
//		alQueries.add("update settings set value = 'in-v3.mailjet.com' where options = 'EMAIL_HOST';" +
//				"update settings set value = 'no-reply@workrig.com' where options = 'EMAIL_FROM';" +
//				"update settings set value = '' where options = 'HOST_PASSWORD';" +
//				"update settings set value = 'cc02f56078c9e2af900ca860eb07d071' where options = 'EMAIL_AUTHENTICATION_USER';" +
//				"update settings set value = 'b8c35ebef26e9e4bf0835d208dd25c96' where options = 'EMAIL_AUTHENTICATION_PASSWORD';");
		
//		alQueries.add("insert into notifications (notification_code) values (1024);" +
//				"insert into notifications (notification_code) values (1025);" +
//				"insert into notifications (notification_code) values (1026);" +
//				"insert into notifications (notification_code) values (1027);");
		
//		alQueries.add("update salary_details set salary_band_id = 0 where salary_band_id is null and level_id>0");		
		
		
		alQueries.add("ALTER TABLE emp_one_one_discussion_details ADD COLUMN start_time time without time zone;\n" + 
				"ALTER TABLE emp_one_one_discussion_details ADD COLUMN end_time time without time zone;\n" + 
				"ALTER TABLE emp_one_one_discussion_details ADD COLUMN discussion_date date;\n" + 
				"ALTER TABLE emp_one_one_discussion_details ADD COLUMN total_time_spent double precision;");
		
		alQueries.add("");
		
//		alQueries.add("update deduction_tax_details set slab_type=0 where slab_type is null");

//		alQueries.add("update section_details set slab_type=0 where slab_type is null");
//
//		alQueries.add("update section_details set slab_type=2 where financial_year_start='2020-04-01' and financial_year_end='2021-03-31' and under_section=0");
		
//		alQueries.add("insert into feature_management (feature_id,feature_name,feature_status,user_type_id,emp_ids) values (41,'SHOW_SALARY_IN_PROFILE_USERWISE',TRUE,',1,7,3,2,','');");
		
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
//			String []arrDBNAME = {"kpca_payroll_trial"};  
			
//			String HOST 		= "223.30.29.18";
//			String PORT 		= "5432";
//			String DBUSERNAME	= "postgres";
//			String DBPASSWORD 	= "dbPassword";
//			String []arrDBNAME = {"rajgurunagar_payroll"};
			
//			String HOST 		= "192.168.1.6";
//			String HOST 		= "localhost";
//			String PORT 		= "5432";
//			String DBUSERNAME	= "postgres";
//			String DBPASSWORD 	= "dbPassword";
//			String []arrDBNAME = {"clairvoyant_test_payroll_3"};
//			String []arrDBNAME = { "development_payroll", "development_payroll_NewUI", "workrig_newui"};
//			String []arrDBNAME = {"xanadu_payroll1"};
//			
//			String HOST = "184.107.247.242";
//			String PORT = "5432";
//			String DBUSERNAME = "postgres";
//			String DBPASSWORD = "dbPassword";
			
//			String HOST 		= "3.7.209.244";
//			String PORT 		= "5432";
//			String DBUSERNAME	= "postgres";
//			String DBPASSWORD 	= "Pune123!";
//			String []arrDBNAME	= {"test_payroll","bct_sandbox","workrig_dev","bct_rmg","demo_payroll","bct_sandbox1","client_demo", "trial_v2","bct_rmg_test"};
			
//			String HOST 		= "108.163.186.66";
//			String PORT 		= "5432";
//			String DBUSERNAME	= "postgres";
//			String DBPASSWORD 	= "2Bt4Re7Va1";
//			String []arrDBNAME = {"test_payroll"};
//			String []arrDBNAME = {"rajgurunagar_test_payroll", "rajgurunagar_payroll"};			
//			String []arrDBNAME = {"kp_payroll", "kp_payroll_olddata", "testing_payroll", "impactinfotech_payroll", 
//					"seinumero_payroll", "latururban_payroll", "workrig_newui", "indianoxide_payroll", "rajgurunagar_payroll"};
			
			
//			//String HOST 		= "54.152.10.99"; 
			
//			String HOST 		= "52.204.34.153";
			String HOST 		= "3.7.78.190";
			String PORT 		= "5432";
			String DBUSERNAME	= "postgres";
			String DBPASSWORD 	= "Pune123!";
//			String []arrDBNAME = {"workrig_payroll"};
//			String []arrDBNAME = {"kpca_payroll", "hbfuller_payroll"};
			String []arrDBNAME = {"kcs_payroll", "kpca_payroll", "kpca_payroll_olddata", "craveinfotech_payroll", "quloi_payroll", "hbfuller_payroll", 
				"quloi_sandbox_1", "client_demo_payroll", "exusia_payroll", "exusia_sandbox", "omgajanan_payroll", "pace_sandbox", "workrig_payroll",
				"prarambhika_learning", "protogon_payroll", "stratizant_payroll", "test_payroll", "justo_payroll", "decipher_payroll", "kpca_old"};
			
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
//				"client_trial_inteliment","quloi_payroll", "quloi_sandbox", "quloi_test","bct_sandbox" ,"bct_settings", "kpca_payroll_trial", "stratizant_payroll",
//				"impact_payroll","hrace_payroll","spb_payroll","demo_pms_payroll", "quloi_sandbox_1"};
			
			/*String []arrDBNAME = {"kcs_payroll", "testing_payroll", "nichesoft_payroll", "medinfi_payroll", "fxkart_payroll", "demo_level_payroll", 
					"demo_grade_payroll", "connectivitysolutions_payroll", "eurosteel_payroll", "motoitaliaa_payroll", "tabcapital_payroll", 
					"default_payroll", "baptist_payroll", "hitech_payroll", "vittalsmedicare_payroll", "lifepoint_payroll", "nehaconsultancy_payroll", 
					"mahabeej_payroll", "jfk_payroll", "chinapearl_payroll", "chavangroup_payroll", "rlfine_payroll", "nexapp_payroll", 
					"konnecttech_payroll", "workrig_newui", "craveinfotech_payroll", "jantaurban_payroll", "sonyindia_payroll", "xanadu_payroll", 
					"srp_payroll", "gmbiocides_payroll", "workrig_newui1", "booksntaxes_payroll", "hbfuller_payroll", "ibsbpo_payroll", 
					"chandantech_payroll", "rawalwasia_payroll", "testing_newui", "egovtech_payroll", "jebi_payroll", "xanadu_test_payroll", 
					"fernandez_hospital", "coolberg_payroll", "nimdex_payroll", "vishnu_payroll", "kpca_payroll", "jmbaxi_payroll", "shrosystems_payroll", 
					"olympia_elevators_payroll", "datacaliper_payroll", "clairvoyant_demo_payroll", "clairvoyant_payroll", "cuembux_payroll"};*/
			
//			String []arrDBNAME = {"srp_sandbox_payroll"};
//			String []arrDBNAME = {"demo_payroll", "kcs_payroll", "sesame_payroll", "advisional_payroll"};
//			String []arrDBNAME = {"kp1_payroll"}; 
//			String []arrDBNAME = {"kcs_payroll"};
//			String []arrDBNAME = {"demo_payroll"};
//			String []arrDBNAME = {"sesame_payroll"};
//			String []arrDBNAME = {"demo_payroll", "sesame_payroll"};
//			String []arrDBNAME = {"demo_payroll_new", "kcs_payroll"};

//			String HOST = "192.168.1.5";
//			String PORT = "5432";
//			String DBUSERNAME = "postgres";
//			String DBPASSWORD = "konnect1";
//			
//			String []arrDBNAME = {"demo_payroll", "demo_rajdeep", "kcs_payroll", "sesame_payroll", "solar_payroll", "lift_shift_payroll", "kp1_payroll", "premmotors_payroll", "kp3_test_payroll"};
//			
//			String []arrDBNAME = {"demo_payroll_new1"}; 
//			String []arrDBNAME = {"kcs_payroll"};
//			String []arrDBNAME = {"taskrig_project"};
//			String []arrDBNAME = {"lift_shift_payroll"};
//			String []arrDBNAME = {"premmotors_payroll"};
//			String []arrDBNAME = {"kp3_test_payroll"};
//			String []arrDBNAME = {"lift_shift_payroll"};
//			String []arrDBNAME = {"wai_payroll"}; 
//			String []arrDBNAME = {"kp1_demo_payroll"};
//			String []arrDBNAME = {"lift_shift_payroll27092014"};
//			String []arrDBNAME = {"kp1_new_payroll"};
//			String []arrDBNAME = {"techchef_payroll"};   
//			String []arrDBNAME = {"demo_payroll", "kp_payroll", "uja_payroll"};
//			String []arrDBNAME = {"dskdigital_payroll"};
//			String []arrDBNAME = {"demo2_payroll", "freightbazaar_payroll", "hbfuller_payroll", "techchef_payroll"};
//			String []arrDBNAME = {"demo_payroll_new1", "impactinfotech_payroll"};
//			String []arrDBNAME = {"demo2_hcm", "demo_payroll_new1", "evolve_payroll", "freightbazaar_payroll", "hbfuller_payroll", "intsemi_payroll", "kcs_payroll", "kp_payroll", "seinumero_payroll","fxkart_payroll","dskdigital_payroll","medinfi_payroll"};
			
//			String []arrDBNAME = {"demo2_hcm", "demo_hcm", "demo_payroll_new1", "kcs_payroll", "fxkart_payroll", "dskdigital_payroll",
//				"medinfi_payroll","bubhandari_payroll", "ghost_tracker_app", "faasos_payroll", "testing_payroll", "apptask_payroll", "goqii_payroll",
//				"infinite1_hcm","nichesoft_payroll", "ipvpeople_payroll", "connectivitysolutions_payroll", "procure_payroll", "impactinfotech_payroll",
//				"rlfinechem_payroll" }; 
			
			 
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
	
	
	public void fillQueriesEarth(List<String> alQueries) {
		

		alQueries.add(" CREATE TABLE page_visit_audit_trail"
				+ "("
				+ "page_visit_audit_trail_id serial NOT NULL,"
				+ "emp_id integer,"
				+ "entry_date_time timestamp without time zone,"
				+ "action_name character varying,"
				+ "base_user_type character varying,"
				+ "remark character varying,"
				+ "CONSTRAINT page_visit_audit_trail_pkey PRIMARY KEY (page_visit_audit_trail_id)"
				+ ");"
				+ "ALTER TABLE page_visit_audit_trail OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE public.skill_category (" + 
				"	skill_category_id serial4 NOT NULL," + 
				"	skill_category_title varchar NULL," + 
				"	added_by int4 NULL," + 
				"	entry_date timestamp NULL," + 
				"	CONSTRAINT skill_category_pkey PRIMARY KEY (skill_category_id)" + 
				");");
		
		alQueries.add("ALTER TABLE public.skills_details ADD skill_category_id int4 NULL;");
		
		alQueries.add("CREATE TABLE public.skill_rating_details (" + 
				"	skill_rating_id serial4 NOT NULL," + 
				"	skill_rating_value int4 NULL," + 
				"	skill_rating_label varchar NULL," + 
				"	CONSTRAINT skill_rating_details_pk PRIMARY KEY (skill_rating_id)" + 
				");");
		
		alQueries.add("CREATE TABLE skill_family" + 
				"(" + 
				"  skill_family_id serial NOT NULL," + 
				"  skill_family_title character varying," + 
				"  status integer," + 
				"  CONSTRAINT skill_family_pkey PRIMARY KEY (skill_family_id)" + 
				");" + 
				"ALTER TABLE skill_family OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE skill_rating_details ("
				+ "skill_rating_id serial NOT NULL,"
				+ "skill_rating_value integer NULL,"
				+ "skill_rating_label character varying NULL,"
				+ "CONSTRAINT skill_rating_details_pk PRIMARY KEY (skill_rating_id)"
				+ ");"
				+ "ALTER TABLE skill_rating_details OWNER TO postgres;");
		
		
		alQueries.add("CREATE TABLE partner_budget_details (" +
				"  partner_budget_id serial NOT NULL," + 
				"  emp_id integer," + 
				"  financial_year_start date," + 
				"  financial_year_end date," + 
				"  month_apr_amount double precision," + 
				"  month_may_amount double precision," + 
				"  month_jun_amount double precision," + 
				"  month_jul_amount double precision," + 
				"  month_aug_amount double precision," + 
				"  month_sep_amount double precision," + 
				"  month_oct_amount double precision," + 
				"  month_nov_amount double precision," + 
				"  month_dec_amount double precision," + 
				"  month_jan_amount double precision," + 
				"  month_feb_amount double precision," + 
				"  month_mar_amount double precision," + 
				"  total_amount double precision," + 
				"  added_by integer," + 
				"  entry_date timestamp without time zone," + 
				"  updated_by integer," + 
				"  update_date timestamp without time zone," + 
				"  CONSTRAINT partner_budget_details_pkey PRIMARY KEY (partner_budget_id)" + 
				");" + 
				"ALTER TABLE partner_budget_details OWNER TO postgres;");

		
		alQueries.add("CREATE TABLE user_type_group_details" +
				"(" +
				"user_type_group_id serial NOT NULL," +
				"user_type_group_name character varying," +
				"access_module_codes character varying," +
				"user_type_ids character varying," +
				"CONSTRAINT user_type_group_details_pkey PRIMARY KEY (user_type_group_id)" +
				");" +
				"ALTER TABLE user_type_group_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE salary_band_details" +
				"(" +
				"salary_band_id serial NOT NULL," +
				"salary_band_name character varying," +
				"band_min_amount double precision," +
				"band_max_amount double precision," +
				"level_id integer," +
				"org_id integer," +
				"added_by integer," +
				"entry_date timestamp without time zone," +
				"updated_by integer," +
				"update_date timestamp without time zone," +
				"CONSTRAINT salary_band_details_pkey PRIMARY KEY (salary_band_id)" +
				");" +
				"ALTER TABLE salary_band_details OWNER TO postgres;");
		
		
		alQueries.add("CREATE TABLE service_tasks_details" +
				"(" +
				"service_task_id serial NOT NULL," +
				"task_name character varying," +
				"task_description character varying," +
				"service_id integer," +
				"added_by integer," +
				"entry_date timestamp without time zone," +
				"updated_by integer," +
				"update_date timestamp without time zone," +
				"CONSTRAINT service_tasks_details_pkey PRIMARY KEY (service_task_id)" +
				");" +
				"ALTER TABLE service_tasks_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE emp_it_slab_access_details" +
				"(" +
				"emp_it_slab_access_id serial NOT NULL," +
				"emp_id integer," +
				"slab_type integer," +
				"fyear_start date," +
				"fyear_end date," +
				"added_by integer," +
				"entry_time timestamp without time zone," +
				"updated_by integer," +
				"update_date timestamp without time zone," +
				"CONSTRAINT emp_it_slab_access_details_pkey PRIMARY KEY (emp_it_slab_access_id)" +
				");" +
				"ALTER TABLE emp_it_slab_access_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE client_brand_details" +
				"(" +
				"client_brand_id serial NOT NULL," +
				"client_brand_name text," +
				"client_brand_industry text," +
				"client_brand_address character varying," +
				"client_brand_type integer DEFAULT 0," +
				"brand_tds_percent double precision DEFAULT 0," +
				"brand_registration_no character varying," +
				"client_brand_logo character varying," +
				"brand_country integer," +
				"brand_state integer," +
				"brand_pin_code character varying," +
				"client_brand_description character varying," +
				"brand_website character varying," +
				"client_brand_city character varying," +
				"isdisabled boolean NOT NULL DEFAULT false," +
				"client_brand_email character varying(100)," +
				"client_brand_phone character varying(30)," +
				"client_brand_fax character varying(30)," +
				"client_brand_pan character varying(30)," +
				"client_brand_tin character varying(30)," +
				"org_id integer," +
				"client_id integer," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"CONSTRAINT client_brand_details_pkey PRIMARY KEY (client_brand_id)" +
				");" +
				"ALTER TABLE client_brand_details OWNER TO postgres;");
		
				
		alQueries.add("CREATE TABLE document_labels_for_revised_salary_details" +
				"(" +
				"revised_salary_details_id serial NOT NULL," +
				"label_name text," +
				"CONSTRAINT document_labels_for_revised_salary_details_pkey PRIMARY KEY (revised_salary_details_id)" +
				");" +
				"ALTER TABLE document_labels_for_revised_salary_details OWNER TO postgres;");
		
		
		alQueries.add("CREATE TABLE assign_shift_dates (" +
				"assign_shift_date_id serial NOT NULL," +
				"emp_id integer," +
				"paycycle_from_date date," +
				"paycycle_to_date date," +
				"paycycle_no integer," +
				"org_id integer," +
				"last_assigned_date date," +
				"added_by integer," +
				"entry_date timestamp without time zone," +
				"CONSTRAINT assign_shift_dates_pkey PRIMARY KEY (assign_shift_date_id)" +
				");" +
				"ALTER TABLE assign_shift_dates OWNER TO postgres;");
		
		
		alQueries.add("CREATE TABLE roster_policy_rules (" +
				"roster_policy_rule_id serial NOT NULL," +
				"rule_type_id integer," +
				"shift_id integer," +
				"shift_ids character varying," +
				"no_of_days integer," +
				"gender character varying," +
				"min_no_of_member_in_shift integer," +
				"min_no_of_member_in_shift_at_weekend integer," +
				"no_of_leads_from_levels_for_no_of_member character varying," +
				"min_weekend_off_per_month double precision," +
				"max_no_of_shifts_per_member_per_month integer," +
				"min_days_off_between_shifts double precision," +
				"member_location_associated_locations character varying," +
				"min_male_member_in_shift integer," +
				"min_break_days_in_stretch_shift double precision," +
				"org_id integer," +
				"added_by integer," +
				"entry_date timestamp without time zone," +
				"updated_by integer," +
				"update_date timestamp without time zone," +
				"roster_policy_rule_name character varying," +
				"CONSTRAINT roster_policy_rules_pkey PRIMARY KEY (roster_policy_rule_id)" +
				"); ALTER TABLE roster_policy_rules OWNER TO postgres;");
		
		
		alQueries.add("CREATE TABLE roster_halfday_fullday_hrs_policy" +
				"(" +
				"roster_halfday_fullday_hrs_id serial NOT NULL," +
				"exception_type character varying," +
				"min_hrs double precision," +
				"effective_date date," +
				"org_id integer," +
				"wlocation_id integer," +
				"entry_date timestamp without time zone," +
				"added_by integer," +
				"update_date timestamp without time zone," +
				"updated_by integer," +
				"policy_status integer DEFAULT 1" +
				");" +
				"ALTER TABLE roster_halfday_fullday_hrs_policy OWNER TO postgres;");
		
		
		alQueries.add("CREATE TABLE emp_degree_certificate_details " +
				"(" +
				"emp_degree_certificate_id serial NOT NULL," +
				"degree_id integer," +
				"emp_id integer," +
				"degree_certificate_name character varying" +
				");" +
				"ALTER TABLE emp_degree_certificate_details OWNER TO postgres;");
		
		
		alQueries.add("CREATE TABLE vda_rate_details" +
				"(" +
				"vda_rate_id serial NOT NULL," +
				"org_id integer," +
				"paycycle integer," +
				"from_date date," +
				"to_date date," +
				"vda_rate double precision," +
				"desig_id integer," +
				"vda_amount_probation double precision," +
				"vda_amount_permanent double precision," +
				"vda_amount_temporary double precision," +
				"vda_index_probation double precision," +
				"vda_index_permanent double precision," +
				"vda_index_temporary double precision," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"CONSTRAINT vda_rate_details_pkey PRIMARY KEY (vda_rate_id)" +
				");" +
				"ALTER TABLE vda_rate_details OWNER TO postgres;");
		
		
		alQueries.add("CREATE TABLE attendance_punch_in_out_details" +
				"(" +
				"attendance_punch_in_out_id serial NOT NULL," +
				"emp_id integer," +
				"punch_date_time timestamp without time zone," +
				"punch_date date," +
				"punch_time time without time zone," +
				"punch_mode character varying," +
				"hours_worked double precision," +
				"user_location character varying," +
				"latitude double precision," +
				"longitude double precision," +
				"punch_entry_mode character varying," +
				"CONSTRAINT attendance_punch_in_out_details_pkey PRIMARY KEY (attendance_punch_in_out_id)" +
				");" +
				"ALTER TABLE attendance_punch_in_out_details OWNER TO postgres;");
		
		
		alQueries.add("CREATE TABLE emp_reimbursement_paid_trans_details" +
				"(" +
				"emp_reimb_paid_trans_id serial NOT NULL," +
				"emp_id integer," +
				"reimbursement_curr integer," +
				"reimbursement_amount double precision," +
				"exchange_rate double precision," +
				"exchange_curr integer," +
				"exchange_amount double precision," +
				"reimbursement_ids character varying," +
				"parent_id integer," +
				"paid_by integer," +
				"paid_date_time timestamp without time zone," +
				"CONSTRAINT emp_reimbursement_paid_trans_details_pkey PRIMARY KEY (emp_reimb_paid_trans_id)" +
				");" +
				"ALTER TABLE emp_reimbursement_paid_trans_details OWNER TO postgres;");
		
		
		alQueries.add("CREATE TABLE arrear_headwise_details" +
				"(" +
				"arrear_headwise_id serial NOT NULL," +
				"emp_id integer," +
				"month integer," +
				"year integer," +
				"entry_date date," +
				"salary_head_id integer," +
				"amount double precision," +
				"paycycle integer DEFAULT 0," +
				"financial_year_from_date date," +
				"financial_year_to_date date," +
				"currency_id integer," +
				"service_id integer," +
				"earning_deduction character varying(1)," +
				"paid_from date," +
				"paid_to date," +
				"paid_days integer," +
				"paid_leaves integer," +
				"total_days integer," +
				"present_days double precision," +
				"arear_id integer," +
				"approve_by integer," +
				"approve_date date," +
				"CONSTRAINT arrear_headwise_details_pkey PRIMARY KEY (arrear_headwise_id)" +
				");" +
				"ALTER TABLE arrear_headwise_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE emp_reimbursement_draft (" +
				"reimbursement_id serial NOT NULL," +
				"from_date date," +
				"to_date date," +
				"reimbursement_type text," +
				"reimbursement_purpose text," +
				"reimbursement_amount double precision," +
				"emp_id integer," +
				"approval_1 integer DEFAULT 0," +
				"approval_2 integer DEFAULT 0," +
				"ispaid boolean DEFAULT false," +
				"paid_date date," +
				"entry_date date," +
				"approval_1_emp_id integer," +
				"approval_2_emp_id integer," +
				"approval_1_date date," +
				"approval_2_date date," +
				"paid_by integer," +
				"ref_document character varying," +
				"reimbursement_type1 character varying(2)," +
				"reimbursement_info text," +
				"travel_mode text," +
				"no_days integer," +
				"no_person integer," +
				"is_billable boolean DEFAULT true," +
				"travel_from character varying," +
				"travel_to character varying," +
				"travel_distance double precision," +
				"travel_rate double precision," +
				"is_fullandfinal boolean DEFAULT false," +
				"client_id integer," +
				"pro_id integer," +
				"statement_id integer," +
				"vendor character varying," +
				"receipt_no character varying," +
				"cancel_by integer," +
				"cancel_date date," +
				"approve_reason text," +
				"transport_type integer," +
				"transport_mode integer," +
				"transport_amount double precision," +
				"lodging_type integer," +
				"lodging_amount double precision," +
				"local_conveyance_type character varying," +
				"local_conveyance_km double precision," +
				"local_conveyance_rate double precision," +
				"local_conveyance_amount double precision," +
				"food_beverage_amount double precision," +
				"laundry_amount double precision," +
				"sundry_amount double precision," +
				"is_paid_with_salary boolean," +
				"reimb_from_date date," +
				"reimb_to_date date," +
				"parent_id integer DEFAULT 0," +
				"submited_by integer," +
				"submit_date date," +
				"submit_status integer DEFAULT 0," +
				"CONSTRAINT emp_reimbursement_draft_pkey PRIMARY KEY (reimbursement_id)" +
				");");
		
		alQueries.add("CREATE TABLE project_milestone_details" +
				"(" +
				"project_milestone_id serial NOT NULL," +
				"pro_milestone_name character varying," +
				"pro_milestone_description character varying," +
				"pro_completion_percent double precision," +
				"pro_task_id integer," +
				"pro_milestone_amount double precision," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"pro_id integer," +
				"client_id integer," +
				"CONSTRAINT project_milestone_details_pkey PRIMARY KEY (project_milestone_id)" +
				");" +
				"ALTER TABLE project_milestone_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN hod_emp_id integer;");
		alQueries.add("alter table emp_family_members add member_marital text;");
		alQueries.add("alter table documents_details add entry_date date;");
		alQueries.add("alter table documents_details add added_by int;");
		alQueries.add("alter table emp_medical_details add filepath text;");
		alQueries.add("alter table employee_personal_details add emp_mname text;"); 
		alQueries.add("ALTER TABLE holidays ADD COLUMN holiday_type character varying;");
		alQueries.add("ALTER TABLE overtime_details ADD COLUMN calculation_basis character varying;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN compensate_with integer;");
		alQueries.add("ALTER TABLE leave_application_register ADD COLUMN prefix_suffix character varying;");
		alQueries.add("ALTER TABLE leave_application_register ADD COLUMN prefix_suffix_type character varying;");
		alQueries.add("ALTER TABLE salary_details ADD COLUMN salary_type character varying;");
		alQueries.add("ALTER TABLE roster_policy ADD COLUMN wlocation_id integer;");
		alQueries.add("ALTER TABLE roster_halfday_policy ADD COLUMN wlocation_id integer;");

		alQueries.add("CREATE TABLE emp_lwf_details(" +
				"emp_lwf_id serial NOT NULL,financial_year_start date,financial_year_end date,salary_head_id character varying," +
				"lwf_max_limit double precision,eelwf_contribution double precision,erlwf_contribution double precision,user_id integer," +
				"entry_timestamp timestamp without time zone,emp_id integer,paycycle integer,_month integer," +
				"CONSTRAINT emp_lwf_details_pkey PRIMARY KEY (emp_lwf_id));ALTER TABLE emp_lwf_details OWNER TO postgres;");
		alQueries.add("ALTER TABLE leave_type ADD COLUMN is_maternity boolean;");

		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN wlocation_id integer;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN balance_validation boolean;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN validation_days integer;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN leave_suffix text;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN leave_prefix text;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN prior_days integer;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN leave_limit integer;");
		
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN maternity_type_frequency integer;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN combination_leave text;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN is_compensatory boolean;");

		alQueries.add("ALTER TABLE overtime_details ADD COLUMN org_id integer;");

		alQueries.add("ALTER TABLE user_details ADD COLUMN wlocation_id_access character varying;");

		alQueries.add("ALTER TABLE org_details ADD COLUMN display_paycycle character varying;");
		alQueries.add("ALTER TABLE org_details ADD COLUMN duration_paycycle character varying;");
		alQueries.add("ALTER TABLE org_details ADD COLUMN salary_cal_basis character varying;");
		alQueries.add("ALTER TABLE org_details ADD COLUMN start_paycycle date;");

		alQueries.add("ALTER TABLE navigation_1 ADD COLUMN nav_visibility boolean;");

		alQueries.add("CREATE TABLE overtime_hours(overtime_hours_id serial NOT NULL,emp_id integer,actual_ot_hours character varying," +
				"approved_ot_hours character varying,approved_by integer,approve_date date,paycle integer,paycycle_from date,paycycle_to date," +
				"financialyear_from date,financialyear_to date,_date date,CONSTRAINT overtime_hours_pkey PRIMARY KEY (overtime_hours_id));" +
				"ALTER TABLE overtime_hours OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE overtime_details ADD COLUMN day_calculation character varying;");
		alQueries.add("ALTER TABLE overtime_details ADD COLUMN fixed_day_calculation character varying;");
		alQueries.add("ALTER TABLE overtime_details ADD COLUMN standard_wkg_hours character varying;");
		alQueries.add("ALTER TABLE overtime_details ADD COLUMN fixed_stwkg_hrs character varying;");
		alQueries.add("ALTER TABLE overtime_details ADD COLUMN standard_time character varying;");
		alQueries.add("ALTER TABLE overtime_details ADD COLUMN buffer_standard_time character varying;");
		alQueries.add("ALTER TABLE overtime_details ADD COLUMN over_time_hrs character varying;");
		alQueries.add("ALTER TABLE overtime_details ADD COLUMN fixed_overtime_hrs character varying;");
		alQueries.add("ALTER TABLE overtime_details ADD COLUMN min_over_time character varying;");

		alQueries.add("ALTER TABLE loan_payments ADD COLUMN paycycle_start date;");
		alQueries.add("ALTER TABLE loan_payments ADD COLUMN paycycle_end date;");

		alQueries.add("ALTER TABLE activity_details ADD COLUMN is_achievements boolean;");
		alQueries.add("ALTER TABLE salary_details ADD COLUMN is_variable boolean;");

		alQueries.add("CREATE TABLE otherearning_individual_details(otherearning_id serial NOT NULL,emp_id integer,pay_paycycle integer," +
				"percent double precision,salary_head_id integer,amount double precision,pay_amount double precision,added_by integer," +
				"entry_date date,paid_from date,paid_to date,is_approved integer,approved_by integer,approved_date date," +
				"CONSTRAINT otherearning_individual_details_pkey PRIMARY KEY (otherearning_id));" +
				"ALTER TABLE otherearning_individual_details OWNER TO postgres;");

		alQueries.add("ALTER TABLE emp_leave_entry ADD COLUMN is_compensate boolean;");
		alQueries.add("ALTER TABLE level_details ADD COLUMN level_parent integer;");
		alQueries.add("ALTER TABLE level_details ADD COLUMN user_id integer;");
		alQueries.add("ALTER TABLE level_details ADD COLUMN ins_no character varying;");
		alQueries.add("ALTER TABLE level_details ADD COLUMN ins_date date;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN wlocation_id integer;");
		alQueries.add("ALTER TABLE overtime_details ADD COLUMN salaryhead_id character varying;");

		alQueries.add("CREATE TABLE specific_emp(specific_emp_id serial NOT NULL,emp_id integer," +
				"CONSTRAINT specific_emp_pkey PRIMARY KEY (specific_emp_id));ALTER TABLE specific_emp OWNER TO postgres;");

		alQueries.add("CREATE TABLE kra_details(kra_id serial NOT NULL,kra text,kra_desc text,attribute_id integer,level_id integer," +
				"measurable integer,CONSTRAINT kra_details_pkey PRIMARY KEY (kra_id));ALTER TABLE kra_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE goal_details ADD COLUMN priority integer;");

		alQueries.add("ALTER TABLE appraisal_attribute ADD COLUMN attribute_info text;");
		alQueries.add("CREATE TABLE appraisal_attribute_level(arribute_level_id serial NOT NULL,level_id integer," +
				"threshhold double precision,attribute_id integer,element_id integer," +
				"CONSTRAINT appraisal_attribute_level_pkey PRIMARY KEY (arribute_level_id));" +
				"ALTER TABLE appraisal_attribute_level OWNER TO postgres;");

		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN appraisal_instruction text;");

		alQueries.add("ALTER TABLE appraisal_level_details ADD COLUMN main_level_id integer;");

		alQueries.add("CREATE TABLE appraisal_main_level_details(main_level_id serial NOT NULL,level_title character varying(200)," +
				"short_description text,long_description text,appraisal_id integer,attribute_id integer," +
				"CONSTRAINT appraisal_main_level_details_pkey PRIMARY KEY (main_level_id));" +
				"ALTER TABLE appraisal_main_level_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE work_flow_details(work_flow_id serial NOT NULL,emp_id integer,effective_id integer," +
				"effective_type character varying,member_type integer,member_position integer,work_flow_mem_id integer," +
				"is_approved integer,approve_date date,reason text,CONSTRAINT work_flow_details_pkey PRIMARY KEY (work_flow_id));" +
				"ALTER TABLE work_flow_details OWNER TO postgres;");

		alQueries.add("CREATE TABLE work_flow_member(work_flow_member_id serial NOT NULL,work_flow_mem character varying," +
				"member_type integer,group_name character varying,group_id integer,member_id integer,reason text," +
				"CONSTRAINT work_flow_member_pkey PRIMARY KEY (work_flow_member_id));" +
				"ALTER TABLE work_flow_member OWNER TO postgres;");

		alQueries.add("CREATE TABLE work_flow_policy(work_flow_policy_id serial NOT NULL,work_flow_member_id integer," +
				"member_position double precision,policy_type character varying,trial_status integer,added_by integer," +
				"added_date date,policy_count integer,policy_name character varying,effective_date date,org_id character varying," +
				"location_id character varying,policy_status integer,group_id integer," +
				"CONSTRAINT work_flow_policy_pkey PRIMARY KEY (work_flow_policy_id));" +
				"ALTER TABLE work_flow_policy OWNER TO postgres;");

		alQueries.add("CREATE TABLE work_flow_policy_details(work_flow_policy_details_id serial NOT NULL,policy_id integer," +
				"type character varying,level_id integer,CONSTRAINT work_flow_policy_details_pkey PRIMARY KEY (work_flow_policy_details_id));" +
				"ALTER TABLE work_flow_policy_details OWNER TO postgres;");
		alQueries.add("ALTER TABLE work_flow_details ADD COLUMN status integer;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN policy_id integer;");		
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN hod_emp_id integer;");
		alQueries.add("alter table emp_family_members add member_marital text");
		alQueries.add("alter table documents_details add entry_date date");
		alQueries.add("alter table documents_details add added_by int");
		alQueries.add("alter table emp_medical_details add filepath text");
		alQueries.add("ALTER TABLE candidate_personal_details ADD COLUMN renegotiate_remark character varying");
		alQueries.add("ALTER TABLE candidate_personal_details ADD COLUMN offer_accept_remark character varying");
		alQueries.add("ALTER TABLE candidate_personal_details ADD COLUMN send_notification_status integer");
		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN finalization_ids character varying");
		alQueries.add("ALTER TABLE panel_interview_details ADD COLUMN added_by integer;");
		alQueries.add("ALTER TABLE panel_interview_details ADD COLUMN added_date timestamp without time zone;");
		
		alQueries.add("CREATE TABLE panel_interview_details (" +
				"panel_interview_id serial NOT NULL, " +
				"recruitment_id integer, " +
				"panel_emp_id integer, " +
				"round_id integer, " +
				"CONSTRAINT panel_interview_details_pkey PRIMARY KEY (panel_interview_id)); " +
				"ALTER TABLE panel_interview_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE document_activities ADD COLUMN document_header text;");
		alQueries.add("ALTER TABLE document_activities ADD COLUMN document_footer text;");
		alQueries.add("ALTER TABLE epf_details ADD COLUMN erpf_max_limit double precision;");
		
		alQueries.add("ALTER TABLE document_comm_details ADD COLUMN entry_time time without time zone;");
		alQueries.add("ALTER TABLE document_comm_details ADD COLUMN status integer;");
		
		alQueries.add("ALTER TABLE overtime_details ADD COLUMN calculation_basis character varying;");
		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN emp_mname character varying;");
		
		
		alQueries.add("alter table navigation_1 add column _label_selected character varying;");
		alQueries.add("alter table navigation_1 add column _label_unselected character varying;");
		alQueries.add("alter table deduction_tax_misc_details add column trail_status integer;");
		
		alQueries.add("alter table emp_leave_entry add column is_modify  boolean default false;");
		alQueries.add("alter table emp_leave_entry add column modify_date  date;");
		alQueries.add("alter table emp_leave_entry add column modify_by  integer;");



		alQueries.add("alter table leave_application_register add column is_modify  boolean  default false;");
		alQueries.add("alter table leave_application_register add column modify_date  date;");
		alQueries.add("alter table leave_application_register add column modify_by  integer;");

//		alQueries.add("update leave_application_register set is_modify= false;");
		
		alQueries.add("alter table emp_epf_details add column evpf_contribution double precision;");

		alQueries.add("alter table leave_type add column is_sandwich boolean default false ");
		alQueries.add("alter table user_details add column org_id_access character varying");
		
		alQueries.add("CREATE TABLE client_address"+
			"(client_address_id serial NOT NULL,"+
			  "client_id integer,"+
			  "client_address character varying,"+
			  "client_city character varying,"+
			  "client_state integer,"+
			  "client_country integer,"+
			  "CONSTRAINT client_address_pkey PRIMARY KEY (client_address_id)"+
			");ALTER TABLE client_address OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE loan_applied_details add column is_paid boolean;");
		alQueries.add("ALTER TABLE loan_applied_details add column paid_date date;");
		alQueries.add("ALTER TABLE loan_applied_details add column paid_by integer;");
		alQueries.add("ALTER TABLE loan_applied_details add column paid_description text;");
		alQueries.add("ALTER TABLE loan_applied_details add column pay_mode character varying;");
		alQueries.add("ALTER TABLE loan_applied_details add column pay_amount double precision;");
		alQueries.add("ALTER TABLE loan_applied_details add column ins_no character varying;");
		alQueries.add("ALTER TABLE loan_applied_details add column ins_date date;");
		alQueries.add("ALTER TABLE loan_applied_details add column tds_amount double precision;");
		
		alQueries.add("ALTER TABLE loan_payments add column payclycle_start date;");
		alQueries.add("ALTER TABLE loan_payments add column payclycle_end date;");
		
		alQueries.add("ALTER TABLE employee_official_details add column emp_hr integer;");
		alQueries.add("ALTER TABLE employee_official_details add column is_service_tax boolean default false;");
		
		alQueries.add("CREATE TABLE emp_tds_details(emp_tds_id serial NOT NULL,financial_year_start date," +
				"financial_year_end date,tds_amount double precision,edu_tax_amount double precision,std_tax_amount double precision," +
				"user_id integer,entry_timestamp timestamp without time zone,emp_id integer,paycycle integer,_month integer," +
				"flat_tds_amount double precision,actual_tds_amount double precision,CONSTRAINT emp_tds_details_pkey PRIMARY KEY (emp_tds_id));" +
				"ALTER TABLE emp_tds_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN is_constant_balance boolean;" +
				"ALTER TABLE emp_leave_type ALTER COLUMN is_constant_balance SET STORAGE PLAIN;" +
				"ALTER TABLE emp_leave_type ALTER COLUMN is_constant_balance SET DEFAULT false;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN is_sandwich boolean;" +
				"ALTER TABLE emp_leave_type ALTER COLUMN is_sandwich SET STORAGE PLAIN;" +
				"ALTER TABLE emp_leave_type ALTER COLUMN is_sandwich SET DEFAULT false;");
		
		alQueries.add("update emp_leave_type set is_compensatory = false  where is_compensatory is null;");
		alQueries.add("update leave_type set is_maternity = false  where is_maternity is null;");
		alQueries.add("ALTER TABLE projectmntnc ADD COLUMN project_owner integer;");
		alQueries.add("ALTER TABLE projectmntnc ADD COLUMN curr_id integer;"); 
		
		alQueries.add("CREATE TABLE promntc_invoice_details(promntc_invoice_id serial NOT NULL,invoice_generated_date date," +
				"invoice_generated_by integer,invoice_from_date date,invoice_to_date date,invoice_paycycle integer,pro_id integer," +
				"invoice_code character varying,project_description character varying,other_description character varying,spoc_id integer," +
				"address_id integer,pro_owner_id integer,financial_start_date date,financial_end_date date,wlocation_id integer,depart_id integer," +
				"CONSTRAINT promntc_invoice_details_pkey PRIMARY KEY (promntc_invoice_id));" +
				"ALTER TABLE promntc_invoice_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE promntc_invoice_amt_details(promntc_invoice_amt_id serial NOT NULL,invoice_particulars character varying," +
				"invoice_particulars_amount double precision,promntc_invoice_id integer," +
				"CONSTRAINT promntc_invoice_amt_details_pkey PRIMARY KEY (promntc_invoice_amt_id));" +
				"ALTER TABLE promntc_invoice_amt_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN particulars_total_amount double precision;");
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN invoice_amount double precision;");
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN curr_id integer;");
		
		alQueries.add("CREATE TABLE promntc_bill_amt_details" +
						"(" +
						  "bill_id serial NOT NULL," +
						  "invoice_id integer," +
						  "invoice_amount double precision," +
						  "received_amount double precision," +
						  "tds_deducted double precision," + 
						  "is_tds_deducted boolean," +
						  "payment_description text," +
						  "payment_mode character varying," +
						  "ins_no character varying," +
						  "ins_date date," +
						  "received_by integer," +
						  "write_off_amount double precision," +
						  "balance_amount double precision," +
						  "is_write_off boolean," +
						  "curr_id integer," +
						  "exchange_rate double precision," +
						  "entry_date date," +
						  "CONSTRAINT promntc_bill_amt_details_pkey PRIMARY KEY (bill_id)" +
						");ALTER TABLE promntc_bill_amt_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE promntc_bill_amt_details ADD COLUMN bill_no character varying;");
		alQueries.add("ALTER TABLE promntc_bill_amt_details ADD COLUMN pro_id integer;");
		alQueries.add("ALTER TABLE promntc_bill_amt_details ADD COLUMN service_tax double precision;");
		alQueries.add("ALTER TABLE promntc_bill_amt_details ADD COLUMN cess1 double precision;");
		alQueries.add("ALTER TABLE promntc_bill_amt_details ADD COLUMN cess2 double precision;");
		alQueries.add("ALTER TABLE promntc_bill_amt_details ADD COLUMN other_deduction double precision;");
		
		alQueries.add("ALTER TABLE promntc_bill_amt_details ADD COLUMN write_off_prof_ex double precision;");
		alQueries.add("ALTER TABLE promntc_bill_amt_details ADD COLUMN write_off_op_ex double precision;");
		alQueries.add("ALTER TABLE promntc_bill_amt_details ADD COLUMN write_off_service_tax double precision;");
		alQueries.add("ALTER TABLE promntc_bill_amt_details ADD COLUMN write_off_desc text;");
		
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN is_cancel boolean;" +
				"ALTER TABLE promntc_invoice_details ALTER COLUMN is_cancel SET DEFAULT false;");
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN other_amount double precision;");		
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN other_particular character varying;");
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN bank_branch_id integer;");
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN invoice_type integer;");
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN client_id integer;");
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN service_id integer;");
		
		alQueries.add("ALTER TABLE promntc_invoice_amt_details ADD COLUMN emp_id integer;");
		alQueries.add("ALTER TABLE promntc_invoice_amt_details ADD COLUMN days_hours double precision;");
		alQueries.add("ALTER TABLE promntc_invoice_amt_details ADD COLUMN _rate double precision;");
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN bill_type character varying;");
		
		alQueries.add("CREATE TABLE tds_projections(tds_id serial NOT NULL,break_register_id serial NOT NULL,salary_head_id integer," +
				"fy_year_from date,fy_year_end date,month integer,amount double precision,emp_id integer,_date date," +
				"CONSTRAINT tds_projections_pkey PRIMARY KEY (tds_id));ALTER TABLE tds_projections OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN standard_tax double precision;");
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN education_tax double precision;");
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN service_tax double precision;");
		
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN sandwich_leave_type character varying(50);");
		
		alQueries.add("ALTER TABLE emp_leave_entry ADD COLUMN is_view boolean;" +
				"ALTER TABLE emp_leave_entry ALTER COLUMN is_view SET STORAGE PLAIN;" +
				"ALTER TABLE emp_leave_entry ALTER COLUMN is_view SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE payroll_generation ADD COLUMN is_view boolean;" +
				"ALTER TABLE payroll_generation ALTER COLUMN is_view SET STORAGE PLAIN;" +
				"ALTER TABLE payroll_generation ALTER COLUMN is_view SET DEFAULT false;");
		
		alQueries.add("CREATE TABLE nodes(" +
				  "node_id serial NOT NULL," +
				  "node_name character varying," +
				  "node_type character varying(1)," +
				  "module_id integer," +
				  "mapped_activity_id integer,"+
				  "CONSTRAINT nodes_pkey PRIMARY KEY (node_id)" +
				");" +
				"ALTER TABLE nodes OWNER TO postgres;" );
		
		alQueries.add("ALTER TABLE document_comm_details ADD COLUMN trigger_nodes character varying;");
		alQueries.add("ALTER TABLE document_comm_details ADD COLUMN doc_id integer;");

		alQueries.add("ALTER TABLE employee_activity_details ADD COLUMN appraisal_id integer;");
		alQueries.add("ALTER TABLE employee_activity_details ALTER COLUMN appraisal_id SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE task_activity ADD COLUMN unblock_by integer;");
		alQueries.add("ALTER TABLE task_activity ADD COLUMN unblock_time timestamp without time zone;");
		
		alQueries.add("CREATE TABLE document_collateral(collateral_id serial NOT NULL," +
				"collateral_name text,_type character varying(1),collateral_image character varying," +
				"CONSTRAINT document_collateral_pkey PRIMARY KEY (collateral_id));" +
				"ALTER TABLE document_collateral OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE document_comm_details ADD COLUMN collateral_header integer;");
		alQueries.add("ALTER TABLE document_comm_details ADD COLUMN collateral_footer integer;");
		alQueries.add("ALTER TABLE org_details ADD COLUMN org_code character varying;");
		
		
		alQueries.add("ALTER TABLE investment_details ADD COLUMN trail_status integer;");
		alQueries.add("ALTER TABLE investment_details ADD COLUMN denied_by integer;");
		alQueries.add("ALTER TABLE investment_details ADD COLUMN denied_date timestamp without time zone;");
		alQueries.add("ALTER TABLE investment_documents ADD COLUMN entry_date timestamp without time zone;");
		
		alQueries.add("CREATE TABLE emp_arrear_details(emp_arrear_details_id serial NOT NULL,emp_id integer,is_arrear_paid boolean DEFAULT false," +
				"start_date date,end_date date,effective_date date,month_count integer,CONSTRAINT emp_arrear_details_pkey PRIMARY KEY (emp_arrear_details_id))" +
				"ALTER TABLE emp_arrear_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE arrear_generation(generation_id serial NOT NULL,emp_id integer,\"month\" integer,\"year\" integer," +
				"pay_date date,entry_date date,salary_head_id integer,amount double precision,paycycle integer DEFAULT 0,financial_year_from_date date," +
				"financial_year_to_date date,pay_mode character varying(10) DEFAULT 'X'::character varying,currency_id integer,service_id integer," +
				"is_paid boolean DEFAULT false,earning_deduction character varying(1),paid_from date,paid_to date,payment_mode integer," +
				"statement_id integer,paid_days integer,paid_leaves integer,total_days integer,present_days double precision,is_fullfinal boolean DEFAULT false," +
				"is_view boolean DEFAULT false,CONSTRAINT arrear_generation_pkey PRIMARY KEY (generation_id));ALTER TABLE arrear_generation OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE document_collateral ADD COLUMN image_align character varying(1);");
		alQueries.add("ALTER TABLE document_collateral ADD COLUMN collateral_text text;");
		
		alQueries.add("CREATE TABLE prev_earn_deduct_details(prev_earn_deduct_id serial NOT NULL,emp_id integer,gross_amount double precision," +
				"tds_amount double precision,financial_start date,financial_end date,document_name character varying,added_by integer," +
				"added_on timestamp without time zone,CONSTRAINT prev_earn_deduct_details_pkey PRIMARY KEY (prev_earn_deduct_id));" +
				"ALTER TABLE prev_earn_deduct_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE leave_register1 ADD COLUMN update_balance integer;" +
				"ALTER TABLE leave_register1 ALTER COLUMN update_balance SET STORAGE PLAIN;" +
				"ALTER TABLE leave_register1 ALTER COLUMN update_balance SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN sandwich_type integer;" +
				"ALTER TABLE emp_leave_type ALTER COLUMN sandwich_type SET STORAGE PLAIN;" +
				"ALTER TABLE emp_leave_type ALTER COLUMN sandwich_type SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE login_timestamp ADD COLUMN session_id character varying(500);");
		alQueries.add("ALTER TABLE login_timestamp ADD COLUMN logout_timestamp timestamp without time zone;");
		
		alQueries.add("CREATE TABLE holiday_count(holiday_count_id serial NOT NULL," +
				"emp_id integer," +
				"_date date," +
				"approved_by integer," +
				"approved_date date," +
				"CONSTRAINT holiday_count_pkey PRIMARY KEY (holiday_count_id));" +
				"ALTER TABLE holiday_count OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE section_details ADD COLUMN under_section integer;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN salary_head_id character varying;");
		alQueries.add("ALTER TABLE payroll_generation ADD COLUMN approve_by integer;");
		alQueries.add("ALTER TABLE payroll_generation ADD COLUMN approve_date date;");
		alQueries.add("ALTER TABLE payroll_generation ADD COLUMN paid_by integer;");
		alQueries.add("ALTER TABLE payroll_generation ADD COLUMN paid_date date;");
		alQueries.add("ALTER TABLE investment_details ADD COLUMN parent_section integer;" +
				"ALTER TABLE investment_details ALTER COLUMN parent_section SET STORAGE PLAIN;" +
				"ALTER TABLE investment_details ALTER COLUMN parent_section SET DEFAULT 0;");
		alQueries.add("ALTER TABLE investment_details ADD COLUMN child_section character varying;");
		
		alQueries.add("ALTER TABLE leave_type ADD COLUMN encashment_applicable integer;");
		alQueries.add("ALTER TABLE leave_type ADD COLUMN encashment_times integer;");
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN bio_device_id integer;");
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN biometrix_id integer;");
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN biometrix_access character varying;");
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN biometrix_isenable boolean;"); 
		alQueries.add("ALTER TABLE section_details ADD COLUMN isdisplay boolean;" +
				"ALTER TABLE section_details ALTER COLUMN isdisplay SET STORAGE PLAIN;" +
				"ALTER TABLE section_details ALTER COLUMN isdisplay SET DEFAULT true;");
		alQueries.add("ALTER TABLE epf_details ADD COLUMN org_id integer;");
		alQueries.add("ALTER TABLE attendance_details ADD COLUMN approval_reason text;");
		alQueries.add("ALTER TABLE navigation_1 ADD COLUMN _type character varying(1);" +
				"ALTER TABLE navigation_1 ALTER COLUMN _type SET STORAGE EXTENDED;" +
				"ALTER TABLE navigation_1 ALTER COLUMN _type SET DEFAULT 'N'::character varying;");
		alQueries.add("ALTER TABLE work_location_info ADD COLUMN biometric_info text;");
		alQueries.add("ALTER TABLE navigation_1 ADD COLUMN _type character varying(1);" +
				"ALTER TABLE navigation_1 ALTER COLUMN _type SET DEFAULT 'N'::character varying;");
		
		alQueries.add("CREATE TABLE user_alerts(user_alerts_id serial NOT NULL," + 
				"emp_id integer," +
				"mypay integer," +
				"leave_request integer," +
				"leave_approval integer," +
				"reimbursement_request integer," +
				"reimbursement_approval integer," +
				"travel_request integer," +
				"travel_approval integer," +
				"CONSTRAINT user_alerts_pkey PRIMARY KEY (user_alerts_id));" +
				"ALTER TABLE user_alerts OWNER TO postgres;"); 
		
		alQueries.add("ALTER TABLE esi_details ADD COLUMN org_id integer;");
		
		alQueries.add("ALTER TABLE education_details ADD COLUMN education_id integer;" +
		"ALTER TABLE skills_description ADD COLUMN skill_id integer;");

		alQueries.add("ALTER TABLE user_alerts ADD COLUMN my_personal_goals integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN my_targets integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN requirement_request integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN requirement_approval integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN jobcode_request integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN jobcode_approval integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN my_reviews integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN hr_reviews integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN manager_reviews integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN peer_reviews integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN my_goals integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN my_kras integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN my_learning_plans integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN reviews integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN interviews integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN new_joinees integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN hr_learning_gaps integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN manager_learning_gaps integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN my_interviews_scheduled integer;" +
				"ALTER TABLE user_alerts ADD COLUMN candidate_finalization integer;" +
				"ALTER TABLE user_alerts ADD COLUMN candidate_offer_accept_reject integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN hr_learning_finalization integer;" +
				"ALTER TABLE user_alerts ADD COLUMN review_finalization integer;" + 
				"ALTER TABLE user_alerts ADD COLUMN manager_goals integer;");
		
		alQueries.add("alter table salary_details add is_ctc_variable boolean");
		alQueries.add("ALTER TABLE salary_details ALTER COLUMN is_ctc_variable SET DEFAULT true;");
		alQueries.add("ALTER TABLE leave_register1 ADD COLUMN _type character varying(1);");
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN is_form16 boolean;");
		
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN perk_request integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN perk_approval integer;"); 
		alQueries.add("ALTER TABLE gratuity_details ADD COLUMN salary_cal_basis character varying;"); 
		
		alQueries.add("CREATE TABLE emp_gratuity_details(" +
				"emp_gratuity_id serial NOT NULL," +
				"emp_id integer," +
				"gratuity_amount double precision," +
				"paid_from date," +
				"paid_to date," +
				"paycycle integer," +
				"added_by integer," +
				"entry_date date," +
				"currency_id integer," +
				"CONSTRAINT emp_gratuity_details_pkey PRIMARY KEY (emp_gratuity_id )" +
				");" +
				"ALTER TABLE emp_gratuity_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE ex_gratia_slab_details(" +
				"gratia_slab_id serial NOT NULL," +
				"ex_gratia_slab character varying," +
				"slab_from double precision," +
				"slab_to double precision," +
				"slab_percentage double precision," +
				"entry_date date," +
				"added_by integer," +
				"CONSTRAINT ex_gratia_slab_details_pkey PRIMARY KEY (gratia_slab_id )" +
				");" +
				"ALTER TABLE ex_gratia_slab_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE ex_gratia_details(" +
				"ex_gratia_id serial NOT NULL," +
				"financial_year_from date," +
				"financial_year_to date," +
				"net_profit double precision," +
				"paycycle_from date," +
				"paycycle_to date," +
				"paycycle integer," +
				"added_by integer," +
				"entry_date date," +
				"org_id integer," +
				"CONSTRAINT ex_gratia_details_pkey PRIMARY KEY (ex_gratia_id )" +
				");" +
				"ALTER TABLE ex_gratia_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE emp_exgratia_details(" +
				"emp_exgratia_id serial NOT NULL," +
				"emp_id integer," +
				"pay_paycycle integer," +
				"percent double precision," +
				"salary_head_id integer," +
				"amount double precision," +
				"pay_amount double precision," +
				"added_by integer," +
				"entry_date date," +
				"paid_from date," +
				"paid_to date," +
				"is_approved integer," +
				"approved_by integer," +
				"approved_date date," +
				"CONSTRAINT emp_exgratia_details_pkey PRIMARY KEY (emp_exgratia_id)" +
				");" +
				"ALTER TABLE emp_exgratia_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE loan_details ADD COLUMN level_id integer;");
		
		alQueries.add("CREATE TABLE emp_leave_encashment(" +
				"leave_encash_id serial NOT NULL," +
				"emp_id integer," +
				"leave_type_id integer," +
				"no_days double precision," +
				"encash_reason character varying," +
				"entry_date date," +
				"is_approved integer DEFAULT 0," +
				"approved_by integer," +
				"approved_date date," +
				"is_paid boolean DEFAULT false," +
				"paid_from date," +
				"paid_to date," +
				"paycycle integer," +
				"CONSTRAINT emp_leave_encashment_pkey PRIMARY KEY (leave_encash_id )" +
				");" +
				"ALTER TABLE emp_leave_encashment OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE emp_lta_details(" +
				"emp_lta_id serial NOT NULL," +
				"emp_id integer," +
				"actual_amount double precision," +
				"applied_amount double precision," +
				"is_approved integer DEFAULT 0," +
				"entry_date date," +
				"approved_by integer," +
				"approved_date date," +
				"is_paid boolean DEFAULT false," +
				"paid_by integer," +
				"paid_date date," +
				"ref_document character varying," +
				"lta_purpose text," +
				"salary_head_id integer," +
				"CONSTRAINT emp_lta_details_pkey PRIMARY KEY (emp_lta_id )" +
				");" +
				"ALTER TABLE emp_lta_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE perk_details ADD COLUMN financial_year_start date;");
		alQueries.add("ALTER TABLE perk_details ADD COLUMN financial_year_end date;");
		alQueries.add("ALTER TABLE emp_perks ADD COLUMN financial_year_start date;");
		alQueries.add("ALTER TABLE emp_perks ADD COLUMN financial_year_end date;"); 
		
		alQueries.add("CREATE TABLE roster_weeklyoff_policy(" +
				"roster_weeklyoff_id serial NOT NULL," +
				"weeklyoff_name character varying," +
				"weeklyoff_type character varying," +
				"weeklyoff_day character varying," +
				"weeklyoff_weekno character varying," +
				"entry_date date," +
				"added_by integer," +
				"CONSTRAINT roster_weeklyoff_policy_pkey PRIMARY KEY (roster_weeklyoff_id )" +
				");ALTER TABLE roster_weeklyoff_policy OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE roster_weekly_off(" +
				"roster_weekoff_id serial NOT NULL," +
				"emp_id integer," +
				"weekoff_date date," +
				"service_id integer," +
				"roster_weeklyoff_id integer," +
				"shift_id integer," +
				"CONSTRAINT roster_weekly_off_pkey PRIMARY KEY (roster_weekoff_id )" +
				"); ALTER TABLE roster_weekly_off OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE org_details ADD COLUMN org_pan_no character varying;" +
			"ALTER TABLE org_details ADD COLUMN org_tan_no character varying;");

		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN doctor_name character varying;" +
				"ALTER TABLE employee_personal_details ADD COLUMN doctor_contact_no character varying;" +
			"ALTER TABLE employee_personal_details ADD COLUMN uid_no character varying;");
		alQueries.add("ALTER TABLE roster_details ADD COLUMN roster_weeklyoff_id integer;");
		alQueries.add("ALTER TABLE epf_details ADD COLUMN level_id integer;");
		alQueries.add("ALTER TABLE statutory_id_registration_info_history ADD COLUMN statutory_type character varying;");
		alQueries.add("ALTER TABLE statutory_id_registration_info_history ADD COLUMN statutory_value character varying;");
		alQueries.add("ALTER TABLE org_details ADD COLUMN org_ain_code character varying;");
		alQueries.add("ALTER TABLE org_details ADD COLUMN org_esic_no character varying;");
		alQueries.add("ALTER TABLE org_details ADD COLUMN trrn_epf character varying;");
		alQueries.add("ALTER TABLE org_details ADD COLUMN epf_account_no character varying;");
		alQueries.add("ALTER TABLE org_details ADD COLUMN establish_code_no character varying;");
		alQueries.add("ALTER TABLE org_details ADD COLUMN tds_payment_code character varying;");
		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN emp_esic_no character varying;"); 
		
		alQueries.add("ALTER TABLE challan_details ADD COLUMN amt_tax double precision;");
		alQueries.add("ALTER TABLE challan_details ADD COLUMN interest_amt double precision;");
		alQueries.add("ALTER TABLE challan_details ADD COLUMN penalty_amt double precision;");
		alQueries.add("ALTER TABLE challan_details ADD COLUMN composition_money double precision;");
		alQueries.add("ALTER TABLE challan_details ADD COLUMN fine_amt double precision;");
		alQueries.add("ALTER TABLE challan_details ADD COLUMN fees_amt double precision;");
		alQueries.add("ALTER TABLE challan_details ADD COLUMN advance_amt double precision;");
		alQueries.add("ALTER TABLE challan_details ADD COLUMN bank_name character varying;");
		alQueries.add("ALTER TABLE challan_details ADD COLUMN bank_branch character varying;");
		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN uan_no character varying;");
		alQueries.add("ALTER TABLE challan_details ADD COLUMN under_section234 double precision;");
		alQueries.add("ALTER TABLE challan_details ADD COLUMN surcharge double precision;");
		alQueries.add("ALTER TABLE challan_details ADD COLUMN edu_cess double precision;");
		alQueries.add("ALTER TABLE challan_details ADD COLUMN income_tax double precision;");
		alQueries.add("ALTER TABLE challan_details ADD COLUMN mode_tds_deposit character varying;");
		
		alQueries.add("CREATE TABLE bonus_details(" +
				"bonus_id serial NOT NULL," +
				"level_id integer," +
				"date_from date," +
				"date_to date," +
				"bonus_minimum double precision," +
				"bonus_maximum double precision," +
				"bonus_type character varying(10) NOT NULL DEFAULT 'A'::character varying," +
				"bonus_minimum_days integer," +
				"bonus_amount double precision," +
				"bonus_period character varying(20)," +
				"entry_date date," +
				"user_id integer," +
				"org_id integer," +
				"salary_head_id character varying," +
				"salary_calculation integer," +
				"salary_effective_year integer," +
				"wlocation_id integer," +
				"CONSTRAINT bonus_details_pkey PRIMARY KEY (bonus_id)" +
				"); ALTER TABLE bonus_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN min_leave_encashment double precision;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN encashment_applicable integer;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN encashment_times integer;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN max_leave_encash double precision;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN salary_head_id character varying;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN is_long_leave boolean;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN long_leave_limit double precision;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN leave_available character varying;");
		
		alQueries.add("ALTER TABLE bonus_details ADD COLUMN limit_amount double precision;");
		alQueries.add("ALTER TABLE bonus_details ADD COLUMN limit_salary_head_id integer;");
		
		alQueries.add("ALTER TABLE work_location_info ADD COLUMN wlocation_ptreg_no character varying;");
		alQueries.add("ALTER TABLE emp_lta_details ADD COLUMN statement_id integer;");
		alQueries.add("ALTER TABLE emp_perks ADD COLUMN statement_id integer;"); 
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN statement_id integer;");
		alQueries.add("ALTER TABLE leave_application_register ADD COLUMN is_long_leave boolean;" +
				"ALTER TABLE leave_application_register ALTER COLUMN is_long_leave SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN percentage double precision;");
		
		alQueries.add("CREATE TABLE roster_fullday_policy(" +
				"roster_full_policy_id serial NOT NULL," +
				"time_value double precision," +
				"_mode character varying," +
				"days integer," +
				"months integer," +
				"desig_id integer," +
				"user_id integer," +
				"policy_status integer DEFAULT 1," +
				"effective_date date," +
				"entry_date date," +
				"org_id integer," +
				"wlocation_id integer," +
				"CONSTRAINT roster_fullday_policy_pkey PRIMARY KEY (roster_full_policy_id)" +
				");" +
				"ALTER TABLE roster_fullday_policy OWNER TO postgres;");
		
		
	// *************************************  ********************************  **************************
		
		alQueries.add("CREATE TABLE validation_details" +
				"(" +
				"validation_details_id serial NOT NULL," +
				"field_name character varying," +
				"required_field boolean," +
				"optional_field boolean," +
				"optional_field_value character varying," +
				"form_name character varying," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"CONSTRAINT validation_details_pkey PRIMARY KEY (validation_details_id)" +
				");" +
				"ALTER TABLE validation_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE promntc_bill_amt_details ADD COLUMN write_off_cess1 double precision;" +
				"ALTER TABLE promntc_bill_amt_details ADD COLUMN write_off_cess2 double precision;" +
				"ALTER TABLE promntc_bill_amt_details ADD COLUMN ope_amount double precision;");
		
		alQueries.add("ALTER TABLE navigation_1 ADD COLUMN product_type character varying;");
		
		alQueries.add("ALTER TABLE navigation_1 ADD COLUMN _label_code character varying;");

		alQueries.add("ALTER TABLE client_details ADD COLUMN website character varying;" +
				"ALTER TABLE client_details ADD COLUMN client_comp_description character varying;" +
				"ALTER TABLE client_details ADD COLUMN pin_code character varying;" +
				"ALTER TABLE client_details ADD COLUMN state integer;" +
				"ALTER TABLE client_details ADD COLUMN country integer;" +
				"ALTER TABLE client_details ADD COLUMN client_logo character varying;");
		
		alQueries.add("ALTER TABLE client_poc RENAME COLUMN contact_name TO contact_fname");;
		
		alQueries.add("ALTER TABLE client_poc ADD COLUMN contact_mname character varying(200);" +
				"ALTER TABLE client_poc ALTER COLUMN contact_mname SET DEFAULT 'N/A'::character varying;");
		
		alQueries.add("ALTER TABLE client_poc ADD COLUMN contact_lname character varying(200);" +
				"ALTER TABLE client_poc ALTER COLUMN contact_lname SET DEFAULT 'N/A'::character varying;");
		
		alQueries.add("ALTER TABLE client_poc ADD COLUMN contact_location character varying(200);" +
				"ALTER TABLE client_poc ALTER COLUMN contact_location SET DEFAULT 'N/A'::character varying;");
		
		alQueries.add("ALTER TABLE client_poc ADD COLUMN contact_photo character varying;");
		
		alQueries.add("ALTER TABLE designation_details ADD COLUMN ideal_candidate text;" +
				"ALTER TABLE designation_details ADD COLUMN job_description text;" +
				"ALTER TABLE designation_details ADD COLUMN profile text;");
		
		alQueries.add("ALTER TABLE work_location_info ADD COLUMN wlocation_billing_address character varying;" +
				"ALTER TABLE work_location_info ADD COLUMN wlocation_billing_city character varying;" +
				"ALTER TABLE work_location_info ADD COLUMN wlocation_billing_country_id integer;" +
				"ALTER TABLE work_location_info ADD COLUMN wlocation_billing_state_id integer;" +
				"ALTER TABLE work_location_info ADD COLUMN wlocation_billing_pincode character varying;" +
				"ALTER TABLE work_location_info ADD COLUMN wlocation_billing_contactno character varying;" +
				"ALTER TABLE work_location_info ADD COLUMN wlocation_billing_faxno character varying;" +
				"ALTER TABLE work_location_info ADD COLUMN wlocation_billing_email character varying;");
		
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN corporate_mobile_no character varying;" +
				"ALTER TABLE employee_official_details ADD COLUMN corporate_desk character varying;");
		
		alQueries.add("ALTER TABLE work_location_info ADD COLUMN wlocation_billing_address character varying;" +
				"ALTER TABLE work_location_info ADD COLUMN wlocation_billing_city character varying;" +
				"ALTER TABLE work_location_info ADD COLUMN wlocation_billing_country_id integer;" +
				"ALTER TABLE work_location_info ADD COLUMN wlocation_billing_state_id integer;" +
				"ALTER TABLE work_location_info ADD COLUMN wlocation_billing_pincode character varying;" +
				"ALTER TABLE work_location_info ADD COLUMN wlocation_billing_contactno character varying;" +
				"ALTER TABLE work_location_info ADD COLUMN wlocation_billing_faxno character varying;" +
				"ALTER TABLE work_location_info ADD COLUMN wlocation_billing_email character varying;");
		       
		alQueries.add("CREATE TABLE calendar_year_details" +
				"(" +
				"calendar_year serial NOT NULL," +
				"calendar_year_from date," +
				"calendar_year_to date," +
				"CONSTRAINT calendar_year_details_pkey PRIMARY KEY (calendar_year)" +
				");" +
				"ALTER TABLE calendar_year_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE exemption_details ADD COLUMN salary_head_id integer;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN financial_year_start date;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN financial_year_end date;");
		
		alQueries.add("ALTER TABLE investment_details ADD COLUMN salary_head_id integer;");
		alQueries.add("ALTER TABLE investment_documents ADD COLUMN salary_head_id integer;");
		alQueries.add("ALTER TABLE exemption_details ADD COLUMN under_section integer;");
		alQueries.add("ALTER TABLE exemption_details ADD COLUMN investment_form boolean;" +
				"ALTER TABLE exemption_details ALTER COLUMN investment_form SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE deduction_tax_misc_details ADD COLUMN max_net_tax_income double precision;" +
				"ALTER TABLE deduction_tax_misc_details ALTER COLUMN max_net_tax_income SET DEFAULT 0;"); 
		alQueries.add("ALTER TABLE deduction_tax_misc_details ADD COLUMN rebate_amt double precision;" +
				"ALTER TABLE deduction_tax_misc_details ALTER COLUMN rebate_amt SET DEFAULT 0;");
		alQueries.add("ALTER TABLE work_location_info ADD COLUMN wlocation_cit_address text;");
		
		alQueries.add("ALTER TABLE bonus_details ADD COLUMN salary_head_id character varying;" +
				"ALTER TABLE bonus_details ADD COLUMN salary_calculation integer;" +
				"ALTER TABLE bonus_details ADD COLUMN salary_effective_year integer;");
		
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN pay_perk integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN lta_request integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN lta_approval integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN pay_lta integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN leave_encash_request integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN leave_encash_approval integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN pay_gratuity integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN pay_reimbursement integer;");
		
		alQueries.add("ALTER TABLE salary_details ADD COLUMN is_tax_exemption boolean;");
		
		alQueries.add("ALTER TABLE org_details ADD COLUMN org_subtitle character varying;");
		alQueries.add("ALTER TABLE org_details ADD COLUMN org_description text;");
		alQueries.add("ALTER TABLE org_details ADD COLUMN org_fax_no character varying;");
		alQueries.add("ALTER TABLE org_details ADD COLUMN org_website character varying;");
		alQueries.add("ALTER TABLE org_details ADD COLUMN org_industry character varying;");
		alQueries.add("ALTER TABLE org_details ADD COLUMN org_currency integer;"); 
		alQueries.add("ALTER TABLE org_details ADD COLUMN salary_fix_days integer;"); 
		
		alQueries.add("CREATE TABLE office_type(" +
				"office_type_id serial NOT NULL," +
				"office_type character varying," +
				"office_type_code character varying," +
				"location_office_type_id integer," +
				"CONSTRAINT office_type_pkey PRIMARY KEY (office_type_id)" +
				");" +
				"ALTER TABLE office_type OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE work_location_type ADD COLUMN wlocation_office_type_id integer;");
		
//		alQueries.add("insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements)" +
//				"values(26,'BF','Bonafide',FALSE,FALSE)");
//		alQueries.add("insert into nodes(node_id,node_name,node_type,module_id,mapped_activity_id)" +
//				"values(9,'Bonafide','D',1,26)");
		
		alQueries.add("CREATE TABLE infrastructure_type" +
				"(" +
				"infra_type_id serial NOT NULL," +
				"infra_type character varying," +
				"CONSTRAINT infrastructure_type_pkey PRIMARY KEY (infra_type_id)" +
				");" +
				"ALTER TABLE infrastructure_type OWNER TO postgres;");
//		alQueries.add("insert into infrastructure_type (infra_type_id,infra_type) values(1,'Room')");
//		alQueries.add("insert into infrastructure_type (infra_type_id,infra_type) values(2,'Laptop')");
//		alQueries.add("insert into infrastructure_type (infra_type_id,infra_type) values(3,'Projector')");
		
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN requisition_request integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN requisition_approval integer;");
		 
		alQueries.add("ALTER TABLE requisition_details ADD COLUMN requi_type integer;");
		alQueries.add("ALTER TABLE requisition_details ADD COLUMN document_id integer;");
		alQueries.add("ALTER TABLE requisition_details ADD COLUMN requi_from date;");
		alQueries.add("ALTER TABLE requisition_details ADD COLUMN requi_to date;");
		alQueries.add("ALTER TABLE requisition_details ADD COLUMN infra_type integer;");
		alQueries.add("ALTER TABLE requisition_details ADD COLUMN purpose text;");
		alQueries.add("ALTER TABLE requisition_details ADD COLUMN is_approved integer;");
		alQueries.add("ALTER TABLE requisition_details ADD COLUMN is_received boolean;");
		alQueries.add("ALTER TABLE requisition_details ADD COLUMN approved_by integer;");
		alQueries.add("ALTER TABLE requisition_details ADD COLUMN approved_date date;");
		
		alQueries.add("ALTER TABLE break_application_register ADD COLUMN is_modify boolean;"+
		"ALTER TABLE break_application_register ALTER COLUMN is_modify SET DEFAULT false;");
		alQueries.add("ALTER TABLE break_application_register ADD COLUMN modify_date date;");
		alQueries.add("ALTER TABLE break_application_register ADD COLUMN modify_by integer;");
		alQueries.add("ALTER TABLE hra_exemption_details ADD COLUMN salary_head_id character varying;");
		
		alQueries.add("ALTER TABLE salary_details ADD COLUMN added_by integer;");
		alQueries.add("ALTER TABLE salary_details ADD COLUMN added_date date;"); 
		
		alQueries.add("ALTER TABLE employee_activity_details ADD COLUMN transfer_type character varying;"); 
		
		alQueries.add("ALTER TABLE arear_details ADD COLUMN basic_amount double precision;");
		
		alQueries.add("CREATE TABLE payroll_history" +
				"(" +
				"payroll_history_id serial NOT NULL," +
				"emp_id integer," +
				"org_id integer," +
				"wlocation_id integer," +
				"depart_id integer," +
				"grade_id integer," +
				"paycycle_from date," +
				"paycycle_to date," +
				"paycycle integer," +
				"service_id character varying," +
				"financial_year_start date," +
				"financial_year_end date," +
				"paid_month integer," +
				"paid_year integer," +
				"paid_date date," +
				"CONSTRAINT payroll_history_pkey PRIMARY KEY (payroll_history_id)" +
				");ALTER TABLE payroll_history OWNER TO postgres;");
		 
		alQueries.add("ALTER TABLE employee_activity_details ADD COLUMN learning_plan_id integer;");
		alQueries.add("ALTER TABLE employee_activity_details ADD COLUMN assessment_id integer;");
		alQueries.add("ALTER TABLE employee_activity_details ADD COLUMN training_id integer;");
		alQueries.add("ALTER TABLE training_gap_details ADD COLUMN assessment_id integer;");
		alQueries.add("ALTER TABLE training_gap_details ADD COLUMN training_id integer;");
		
		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN corporate_mobile character varying;");
		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN corporate_desk character varying;");
		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN house_no character varying;");
		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN suburb character varying;");
		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN relevant_experience double precision;");
		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN total_experience double precision;");
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN corporate_mobile_no character varying;");
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN corporate_desk character varying;");
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN emp_contractor integer;");
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN emprofile integer;");
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN month_ctc double precision;");
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN annual_ctc double precision;");
		
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN customer_id integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN pro_request integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN pro_created integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN pro_completed integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN pro_new_resource integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN task_allocate integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN task_new_request integer;");
		
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN task_accept integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN task_request_reschedule integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN task_request_reassign integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN task_reassign integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN task_reschedule integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN task_completed integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN timesheet_received integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN invoice_generated integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN share_document integer;");
		
		alQueries.add("ALTER TABLE project_milestone_details ADD COLUMN send_milestone_mail integer;");
		
		alQueries.add("ALTER TABLE currency_details ADD COLUMN sub_division character varying;");
		alQueries.add("ALTER TABLE work_location_info ADD COLUMN wlocation_pt_rcec character varying;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN is_leave_accrual boolean;");
		 
		alQueries.add("ALTER TABLE goal_kras ADD COLUMN is_close boolean;" +
				"ALTER TABLE goal_kras ALTER COLUMN is_close SET DEFAULT false;" +
				"ALTER TABLE goal_kras ADD COLUMN close_reason character varying;");
		
		alQueries.add("CREATE TABLE travel_application_register(" +
				"travel_register_id serial NOT NULL," +
				"_date date," +
				"emp_id integer," +
				"travel_id integer," +
				"travel_no double precision," +
				"is_paid boolean," +
				"CONSTRAINT travel_application_register_pkey PRIMARY KEY (travel_register_id)" +
				");ALTER TABLE travel_application_register OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE deduction_tax_misc_details ADD COLUMN swachha_bharat_cess double precision;" +
				"ALTER TABLE deduction_tax_misc_details ALTER COLUMN swachha_bharat_cess SET DEFAULT 0;");
		alQueries.add("ALTER TABLE training_gap_details ADD COLUMN assign_learning_plan_id integer;");
		
		alQueries.add("CREATE TABLE assessment_emp_remain_time(" +
				"assessment_emp_time_id serial NOT NULL," +
				"emp_id integer," +
				"learning_plan_id integer," +
				"assessment_id integer," +
				"remaining_time character varying," +
				"entry_date timestamp without time zone," +
				"CONSTRAINT assessment_emp_remain_time_pkey PRIMARY KEY (assessment_emp_time_id)" +
				");ALTER TABLE assessment_emp_remain_time OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE notices ADD COLUMN display_end_date date;");
		alQueries.add("ALTER TABLE notices ADD COLUMN ispublish boolean;");
		alQueries.add("ALTER TABLE salary_details ADD COLUMN is_incentive boolean;");
		
		alQueries.add("CREATE TABLE ideal_time_details(" +
				"ideal_id serial NOT NULL," +
				"task_id integer," +
				"pro_id integer," +
				"emp_id integer," +
				"org_id integer," +
				"screen_shot_id integer," +
				"status integer," +
				"entry_time timestamp without time zone," +
				"CONSTRAINT idea PRIMARY KEY (ideal_id)" +
				");ALTER TABLE ideal_time_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN vendor character varying;");
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN receipt_no character varying;");
		
		alQueries.add("CREATE TABLE task_screenshot_details(" +
				"screenshot_id serial NOT NULL," +
				"task_id integer," +
				"project_id integer," +
				"emp_id integer," +
				"org_id integer," +
				"screenshot_name character(100)," +
				"entry_time timestamp without time zone," +
				"CONSTRAINT task_screenshot_details_pkey PRIMARY KEY (screenshot_id)" +
				");ALTER TABLE task_screenshot_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE ideal_time_details ADD COLUMN key_stroke_count integer;");
		alQueries.add("ALTER TABLE ideal_time_details ADD COLUMN mouse_stroke_count integer;");
		alQueries.add("ALTER TABLE ideal_time_details ADD COLUMN top_application_name character(100);");
		
		alQueries.add("ALTER TABLE leave_type ADD COLUMN is_leave_opt_holiday boolean;");
		
		alQueries.add("CREATE TABLE leave_opt_holiday_details(" +
				"leave_opt_holiday_id serial NOT NULL," +
				"leave_type_id integer," +
				"level_id integer," +
				"wlocation_id integer," +
				"org_id integer," +
				"calendar_year_from date," +
				"calendar_year_to date," +
				"leave_limit integer," +
				"added_by integer," +
				"entry_date date," +
				"CONSTRAINT leave_opt_holiday_details_pkey PRIMARY KEY (leave_opt_holiday_id)" +
				");ALTER TABLE leave_opt_holiday_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE salary_details ADD COLUMN is_delete boolean;" +
				"ALTER TABLE salary_details ALTER COLUMN is_delete SET DEFAULT false;");
		alQueries.add("ALTER TABLE document_activities ADD COLUMN emp_activity_id integer;");
		
		alQueries.add("ALTER TABLE emp_gratuity_details ADD COLUMN is_fullandfinal boolean;");
		alQueries.add("ALTER TABLE emp_lta_details ADD COLUMN is_fullandfinal boolean;");
		alQueries.add("ALTER TABLE emp_perks ADD COLUMN is_fullandfinal boolean;");
		
		alQueries.add("ALTER TABLE emp_offboard_status ADD COLUMN deduct_amount double precision;");
		
		alQueries.add("ALTER TABLE travel_application_register ADD COLUMN is_modify boolean;" +
				"ALTER TABLE travel_application_register ALTER COLUMN is_modify SET DEFAULT false;");
		alQueries.add("ALTER TABLE travel_application_register ADD COLUMN modify_date date;");
		alQueries.add("ALTER TABLE travel_application_register ADD COLUMN modify_by integer;");
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN cancel_by integer;");
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN cancel_date date;");
		alQueries.add("ALTER TABLE salary_details ADD COLUMN is_allowance boolean;");
		
		alQueries.add("CREATE TABLE allowance_condition_details(" +
				"allowance_condition_id serial NOT NULL," +
				"allowance_condition integer," +
				"min_condition double precision," +
				"max_condition double precision," +
				"salary_head_id integer," +
				"level_id integer," +
				"org_id integer," +
				"added_by integer," +
				"entry_date date," +
				"custom_type character varying," +
				"custom_amt_percentage double precision," +
				"condition_slab character varying," +
				"CONSTRAINT allowance_condition_details_pkey PRIMARY KEY (allowance_condition_id)" +
				");ALTER TABLE allowance_condition_details OWNER TO postgres;"); 
		
		alQueries.add("CREATE TABLE allowance_payment_logic(" +
				"payment_logic_id serial NOT NULL," +
				"payment_logic_slab character varying," +
				"allowance_condition_id integer," +
				"payment_logic integer," +
				"added_by integer," +
				"entry_date date," +
				"salary_head_id integer," +
				"level_id integer," +
				"org_id integer," +
				"fixed_amount double precision," +
				"cal_salary_head_id integer," +
				"CONSTRAINT allowance_payment_logic_pkey PRIMARY KEY (payment_logic_id)" +
				");ALTER TABLE allowance_payment_logic OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE allowance_individual_details(" +
				"allowance_id serial NOT NULL," +
				"emp_id integer," +
				"pay_paycycle integer," +
				"salary_head_id integer," +
				"amount double precision," +
				"pay_amount double precision," +
				"added_by integer," +
				"entry_date date," +
				"paid_from date," +
				"paid_to date," +
				"is_approved integer," +
				"approved_by integer," +
				"approved_date date," +
				"CONSTRAINT allowance_individual_details_pkey PRIMARY KEY (allowance_id)" +
				");ALTER TABLE allowance_individual_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE allowance_pay_details(" +
				"allowance_pay_id serial NOT NULL," +
				"allowance_id integer," +
				"condition_id integer," +
				"payment_logic_id integer," +
				"amount double precision," +
				"CONSTRAINT allowance_pay_details_pkey PRIMARY KEY (allowance_pay_id)" +
				");ALTER TABLE allowance_pay_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE deduction_tax_misc_details ADD COLUMN krishi_kalyan_cess double precision;" +
				"ALTER TABLE deduction_tax_misc_details ALTER COLUMN krishi_kalyan_cess SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE salary_details ADD COLUMN multiple_calculation character varying;");  
		
		alQueries.add("CREATE TABLE form_management_details(" +
				"form_id serial NOT NULL," +
				"form_name character varying," +
				"node_id integer," +
				"org_id integer," +
				"added_by integer," +
				"entry_date date," +
				"CONSTRAINT form_management_details_pkey PRIMARY KEY (form_id)" +
				");ALTER TABLE form_management_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE form_question_details(" +
				"form_question_id serial NOT NULL," +
				"form_id integer," +
				"section_id integer," +
				"question_bank_id integer," +
				"answer_type integer," +
				"weightage double precision," +
				"added_by integer," +
				"entry_date date," +
				"CONSTRAINT form_question_details_pkey PRIMARY KEY (form_question_id)" +
				");ALTER TABLE form_question_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE form_section_details(" +
				"form_section_id serial NOT NULL," +
				"section_name character varying," +
				"short_description text," +
				"long_description text," +
				"weightage double precision," +
				"answer_type integer," +
				"form_id integer," +
				"added_by integer," +
				"entry_date date," +
				"CONSTRAINT form_section_details_pkey PRIMARY KEY (form_section_id)" +
				");ALTER TABLE form_section_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN is_prorata boolean;");
		alQueries.add("ALTER TABLE work_flow_details ADD COLUMN user_type_id integer;"); 
		alQueries.add("ALTER TABLE education_details ADD COLUMN inst_name character varying(200);");
		
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN loan_request integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN loan_approval integer;");
		alQueries.add("ALTER TABLE shift_details ADD COLUMN org_id integer;");
		alQueries.add("ALTER TABLE roster_weeklyoff_policy ADD COLUMN org_id integer;"); 
		alQueries.add("ALTER TABLE work_flow_policy_details ADD COLUMN wlocation_id integer;");
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN approve_reason text;");
		alQueries.add("ALTER TABLE emp_perks ADD COLUMN approve_reason text;");
		alQueries.add("ALTER TABLE emp_lta_details ADD COLUMN approve_reason text;");
		alQueries.add("ALTER TABLE loan_applied_details ADD COLUMN approve_reason text;");
		alQueries.add("ALTER TABLE requisition_details ADD COLUMN approve_reason text;");
		alQueries.add("ALTER TABLE emp_leave_encashment ADD COLUMN approve_reason text;");
		
		alQueries.add("CREATE TABLE form_question_answer(" +
				"form_question_answer_id serial NOT NULL," +
				"emp_id integer," +
				"answer text," +
				"form_id integer," +
				"section_id integer," +
				"question_id integer," +
				"user_id integer," +
				"attempted_on date," +
				"remark text," +
				"marks double precision," +
				"weightage double precision," +
				"read_status integer DEFAULT 0," +
				"read_status_comment text," +
				"answers_comment character varying," +
				"resign_id integer," +
				"CONSTRAINT form_question_answer_pkey PRIMARY KEY (form_question_answer_id)" +
				");ALTER TABLE form_question_answer OWNER TO postgres;"); 
		
		alQueries.add("ALTER TABLE work_flow_member ADD COLUMN org_id integer;");
		alQueries.add("ALTER TABLE work_flow_member ADD COLUMN wlocation_id integer;");
		alQueries.add("ALTER TABLE work_flow_member ADD COLUMN is_default boolean;" +
				"ALTER TABLE work_flow_member ALTER COLUMN is_default SET DEFAULT false;");
		alQueries.add("ALTER TABLE specific_emp ADD COLUMN policy_id integer;");
		
		alQueries.add("CREATE TABLE authorised_details(" +
				"auth_id serial NOT NULL," +
				"wlocation_id integer," +
				"financial_year_start date," +
				"financial_year_end date," +
				"emp_id integer," +
				"CONSTRAINT authorised_details_pkey PRIMARY KEY (auth_id)" +
				");ALTER TABLE authorised_details OWNER TO postgres;"); 
		
		alQueries.add("ALTER TABLE emp_leave_entry ADD COLUMN is_concierge boolean;" +
				"ALTER TABLE emp_leave_entry ALTER COLUMN is_concierge SET DEFAULT false;");
		alQueries.add("ALTER TABLE emp_leave_entry ADD COLUMN travel_mode character varying;");
		alQueries.add("ALTER TABLE emp_leave_entry ADD COLUMN is_booking boolean;" +
				"ALTER TABLE emp_leave_entry ALTER COLUMN is_booking SET DEFAULT false;");
		alQueries.add("ALTER TABLE emp_leave_entry ADD COLUMN booking_info text;");
		alQueries.add("ALTER TABLE emp_leave_entry ADD COLUMN is_accommodation boolean;" +
				"ALTER TABLE emp_leave_entry ALTER COLUMN is_accommodation SET DEFAULT false;");
		alQueries.add("ALTER TABLE emp_leave_entry ADD COLUMN accommodation_info text;");
		
		alQueries.add("CREATE TABLE travel_booking_documents(" +
		  "travel_booking_id serial NOT NULL," +
		  "travel_id integer," +
		  "emp_id integer," +
		  "document_name character varying," +
		  "added_by integer," +
		  "added_date date," +
		  "CONSTRAINT travel_booking_docments_pkey PRIMARY KEY (travel_booking_id)" +
		  ");ALTER TABLE travel_booking_documents OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN travelbooking integer;");
		alQueries.add("ALTER TABLE allowance_payment_logic ADD COLUMN effective_date date;");
		alQueries.add("ALTER TABLE allowance_payment_logic ADD COLUMN per_hour_day double precision;");
		alQueries.add("ALTER TABLE allowance_condition_details ADD COLUMN calculation_from double precision;");
		alQueries.add("ALTER TABLE bonus_details ADD COLUMN is_bonus_condition boolean;" +
				"ALTER TABLE bonus_details ALTER COLUMN is_bonus_condition SET DEFAULT false;");
		alQueries.add("ALTER TABLE bonus_details ADD COLUMN min1 double precision;");
		alQueries.add("ALTER TABLE bonus_details ADD COLUMN max1 double precision;");
		alQueries.add("ALTER TABLE bonus_details ADD COLUMN percentage1 double precision;");
		alQueries.add("ALTER TABLE bonus_details ADD COLUMN max2 double precision;");
		alQueries.add("ALTER TABLE bonus_details ADD COLUMN percentage2 double precision;"); 
		
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN is_period boolean;" +
				"ALTER TABLE emp_leave_type ALTER COLUMN is_period SET DEFAULT false;");
		
		alQueries.add("CREATE TABLE workflow_policy_period(" +
				"workflow_policy_period_id serial NOT NULL," +
				"min_value integer," +
				"max_value integer," +
				"policy_id integer," +
				"policy_type character varying," +
				"level_id integer," +
				"org_id integer," +
				"wlocation_id integer," +
				"leave_type_id integer," +
				"added_by integer," +
				"entry_date date," +
				"CONSTRAINT workflow_policy_period_pkey PRIMARY KEY (workflow_policy_period_id)" +
				");ALTER TABLE workflow_policy_period OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN accrual_type integer;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN accrual_days integer;");
		
		alQueries.add("ALTER TABLE salary_details ADD COLUMN grade_id integer;");
		alQueries.add("ALTER TABLE org_details ADD COLUMN payslip_format integer;");
		alQueries.add("ALTER TABLE org_details ADD COLUMN increment_type integer;"); 
		alQueries.add("ALTER TABLE org_details ADD COLUMN increment_month integer;");
		
		alQueries.add("CREATE TABLE basic_fitment_details(" +
				"fitment_id serial NOT NULL," +
				"grade_id integer," +
				"increment_year integer," +
				"amount double precision," +
				"base_amount double precision," +
				"increment_amount double precision," +
				"entry_date date," +
				"update_by integer," +
				"trail_status integer," +
				"CONSTRAINT basic_fitment_details_pkey PRIMARY KEY (fitment_id)" +
				");ALTER TABLE basic_fitment_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE emp_basic_fitment_details(" +
				"emp_fitment_id serial NOT NULL," +
				"emp_id integer," +
				"grade_from integer," +
				"grade_to integer," +
				"fitment_month integer," +
				"fitment_year integer," +
				"entry_date date," +
				"approve_by integer," +
				"approve_status integer," +
				"increment_type integer," +
				"CONSTRAINT emp_basic_fitment_details_pkey PRIMARY KEY (emp_fitment_id)" +
				");ALTER TABLE emp_basic_fitment_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE emp_defer_basic_fitment(" +
				"emp_defer_id serial NOT NULL," +
				"emp_id integer," +
				"defer_date date," +
				"grade_from integer," +
				"grade_to integer," +
				"fitment_month integer," +
				"fitment_year integer," +
				"entry_date date," +
				"approve_by integer," +
				"defer_status boolean," +
				"CONSTRAINT emp_defer_basic_fitment_pkey PRIMARY KEY (emp_defer_id)" +
				");ALTER TABLE emp_defer_basic_fitment OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE grades_details ADD COLUMN is_fitment boolean;");
		alQueries.add("ALTER TABLE grades_details ADD COLUMN weightage integer;");
		
		alQueries.add("CREATE TABLE reimbursement_policy(" +
				"reimbursement_policy_id serial NOT NULL," +
				"reimbursement_policy_type integer," +
				"is_default_policy boolean," +
				"mobile_limit_type integer," +
				"mobile_limit double precision," +
				"level_id integer," +
				"org_id integer," +
				"added_by integer," +
				"entry_date date," +
				"local_type integer," +
				"transport_type character varying," +
				"local_limit_type integer," +
				"local_limit double precision," +
				"is_require_approval boolean," +
				"min_amount double precision," +
				"max_amount double precision," +
				"country_id integer," +
				"city character varying," +
				"eligible_amount double precision," +
				"eligible_type integer," +
				"travel_transport_type integer," +
				"train_type integer," +
				"bus_type integer," +
				"flight_type integer," +
				"car_type integer," +
				"travel_limit_type integer," +
				"travel_limit double precision," +
				"lodging_type integer," +
				"lodging_limit_type integer," +
				"lodging_limit double precision," +
				"local_conveyance_tran_type character varying," +
				"local_conveyance_limit double precision," +
				"food_limit_type integer," +
				"food_limit double precision," +
				"laundry_limit_type integer," +
				"laundry_limit double precision," +
				"sundry_limit_type integer," +
				"sundry_limit double precision," +
				"CONSTRAINT reimbursement_policy_pkey PRIMARY KEY (reimbursement_policy_id)" +
				");ALTER TABLE reimbursement_policy OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE leave_type ADD COLUMN leave_category integer;" +
				"ALTER TABLE leave_type ALTER COLUMN leave_category SET DEFAULT 0;");
		
		alQueries.add("CREATE TABLE form16_documents(" +
				"form16_document_id serial NOT NULL," +
				"emp_id integer," +
				"financial_year_start date," +
				"financial_year_end date," +
				"form16_name character varying," +
				"approved_by integer," +
				"approved_date date," +
				"CONSTRAINT form16_documents_pkey PRIMARY KEY (form16_document_id)" +
				");ALTER TABLE form16_documents OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN form16_release integer;"); 
		
		alQueries.add("CREATE TABLE approve_attendance(" +
				"approve_attendance_id serial NOT NULL," +
				"emp_id integer," +
				"financial_year_start date," +
				"financial_year_end date," +
				"approve_from date," +
				"approve_to date," +
				"paycycle integer," +
				"total_days double precision," +
				"paid_days double precision," +
				"present_days double precision," +
				"paid_leaves double precision," +
				"absent_days double precision," +
				"approve_by integer," +
				"approve_date date," +
				"CONSTRAINT approve_attendance_pkey PRIMARY KEY (approve_attendance_id)" +
				");ALTER TABLE approve_attendance OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE company_manual ADD COLUMN manual_doc character varying;"); 
		alQueries.add("ALTER TABLE attendance_details ADD COLUMN user_location character varying;");
		alQueries.add("ALTER TABLE attendance_details ADD COLUMN latitude double precision;");
		alQueries.add("ALTER TABLE attendance_details ADD COLUMN longitude double precision;");
		
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN transport_type integer;");
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN transport_mode integer;");
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN transport_amount double precision;");
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN lodging_type integer;");
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN lodging_amount double precision;");
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN local_conveyance_type character varying;");
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN local_conveyance_km double precision;");
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN local_conveyance_rate double precision;");
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN local_conveyance_amount double precision;");
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN food_beverage_amount double precision;");
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN laundry_amount double precision;");
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN sundry_amount double precision;");
		
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN is_paid_with_salary boolean;");
		alQueries.add("ALTER TABLE gratuity_details ADD COLUMN salary_head_id character varying;");
		alQueries.add("ALTER TABLE gratuity_details ADD COLUMN org_id integer;");
		alQueries.add("ALTER TABLE gratuity_details ADD COLUMN fixed_days double precision;"); 
		alQueries.add("ALTER TABLE emp_gratuity_details ADD COLUMN statement_id integer;"); 
		
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN is_carryforward_limit boolean;" +
			"ALTER TABLE emp_leave_type ALTER COLUMN is_carryforward_limit SET DEFAULT false;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN carryforward_limit double precision;");
		
		alQueries.add("ALTER TABLE salary_details ADD COLUMN is_align_with_perk boolean;" +
			"ALTER TABLE salary_details ALTER COLUMN is_align_with_perk SET DEFAULT false;");
		
		alQueries.add("CREATE TABLE perk_assign_salary_details(" +
				"perk_assign_salary_id serial NOT NULL," +
				"emp_id integer," +
				"perk_salary_id integer," +
				"salary_head_id integer," +
				"level_id integer," +
				"org_id integer," +
				"amount double precision," +
				"financial_year_start date," +
				"financial_year_end date," +
				"status boolean," +
				"trail_status boolean," +
				"added_by integer," +
				"entry_date date," +
				"update_by integer," +
				"update_date date," +
				"paycycle_from date," +
				"paycycle_to date," +
				"paycycle integer," +
				"CONSTRAINT perk_assign_salary_details_pkey PRIMARY KEY (perk_assign_salary_id)" +
				"); ALTER TABLE perk_assign_salary_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE perk_salary_applied_details(" +
				"perk_salary_applied_id serial NOT NULL," +
				"emp_id integer," +
				"is_approved integer," +
				"entry_date date," +
				"perk_salary_id integer," +
				"description text," +
				"ref_document character varying," +
				"financial_year_start date," +
				"financial_year_end date," +
				"approved_by integer," +
				"approved_date date," +
				"is_nontaxable boolean," +
				"approver_user_type_id integer," +
				"approve_reason character varying," +
				"CONSTRAINT perk_salary_applied_details_pkey PRIMARY KEY (perk_salary_applied_id)" +
				"); ALTER TABLE perk_salary_applied_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE perk_salary_details(" +
				"perk_salary_id serial NOT NULL," +
				"perk_code character varying," +
				"perk_name character varying," +
				"perk_description text," +
				"amount double precision," +
				"entry_date date," +
				"user_id integer," +
				"salary_head_id integer," +
				"level_id integer," +
				"org_id integer," +
				"financial_year_start date," +
				"financial_year_end date," +
				"is_attachment boolean," +
				"CONSTRAINT perk_salary_details_pkey PRIMARY KEY (perk_salary_id)" +
				"); ALTER TABLE perk_salary_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE emp_leave_entry ADD COLUMN approve_date date;");
		alQueries.add("ALTER TABLE perk_salary_applied_details ADD COLUMN applied_amount double precision;");
		alQueries.add("CREATE TABLE perk_salary_applied_paycycle(" +
				"salary_applied_paycycle_id serial NOT NULL," +
				"perk_salary_applied_id integer," +
				"emp_id integer," +
				"paycycle_from date," +
				"paycycle_to date," +
				"paycycle integer," +
				"financial_year_start date," +
				"financial_year_end date," +
				"CONSTRAINT perk_salary_applied_paycycle_pkey PRIMARY KEY (salary_applied_paycycle_id)" +
				"); ALTER TABLE perk_salary_applied_paycycle OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE payroll_bank_statement ADD COLUMN bank_pay_type integer;");
		
		alQueries.add("CREATE TABLE log_details(" +
				"log_id serial NOT NULL," +
				"process_id integer," +
				"process_type character varying," +
				"process_activity integer," +
				"process_msg character varying," +
				"process_step integer," +
				"process_by integer," +
				"process_time timestamp without time zone," +
				"CONSTRAINT log_details_pkey PRIMARY KEY (log_id)" +
				"); ALTER TABLE log_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE payroll_generation ADD COLUMN bank_pay_type integer;");
		alQueries.add("ALTER TABLE esi_details ALTER COLUMN salary_head_id TYPE character varying;");
		alQueries.add("ALTER TABLE epf_details ALTER COLUMN salary_head_id TYPE character varying;");
		alQueries.add("ALTER TABLE user_details ADD COLUMN mob_device_id text;");
		
		alQueries.add("ALTER TABLE salary_details ADD COLUMN is_annual_variable boolean;");
		alQueries.add("CREATE TABLE annual_variable_details(" +
				"annual_variable_id serial NOT NULL," +
				"salary_head_id integer," +
				"variable_amount double precision," +
				"level_id integer," +
				"org_id integer," +
				"financial_year_start date," +
				"financial_year_end date," +
				"added_by integer," +
				"added_date date," +
				"updated_by integer," +
				"updated_date date," +
				"CONSTRAINT annual_variable_details_pkey PRIMARY KEY (annual_variable_id)" +
				"); ALTER TABLE annual_variable_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE annual_variable_individual_details(" +
				"annual_vari_ind_id serial NOT NULL," +
				"emp_id integer," +
				"pay_paycycle integer," +
				"percent double precision," +
				"salary_head_id integer," +
				"amount double precision," +
				"pay_amount double precision," +
				"added_by integer," +
				"entry_date date," +
				"paid_from date," +
				"paid_to date," +
				"is_approved integer," +
				"approved_by integer," +
				"approved_date date," +
				"earning_deduction character varying(1)," +
				"CONSTRAINT annual_variable_individual_details_pkey PRIMARY KEY (annual_vari_ind_id)" +
				"); ALTER TABLE annual_variable_individual_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE user_details ADD COLUMN is_mobile_authorized boolean;");
		alQueries.add("ALTER TABLE annual_variable_details ADD COLUMN emp_id integer;");
		
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN prev_month_ctc double precision;");
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN prev_annual_ctc double precision;");
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN incre_month_amount double precision;");
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN incre_annual_amount double precision;");
		
		alQueries.add("ALTER TABLE perk_salary_details ADD COLUMN is_optimal boolean;");
		alQueries.add("ALTER TABLE emp_leave_entry ADD COLUMN place_from character varying;");
		
		alQueries.add("ALTER TABLE overtime_details ADD COLUMN round_off_time integer;");
		alQueries.add("ALTER TABLE work_location_info ADD COLUMN lunch_break_deduct double precision;");
		alQueries.add("ALTER TABLE work_location_info ADD COLUMN is_break_time_policy boolean;");
		
		alQueries.add("CREATE TABLE reimbursement_ctc_details(" +
				"reimbursement_ctc_id serial NOT NULL," +
				"reimbursement_code character varying," +
				"reimbursement_name character varying," +
				"level_id integer," +
				"org_id integer," +
				"added_by integer," +
				"added_date date," +
				"update_by integer," +
				"update_date date," +
				"CONSTRAINT reimbursement_ctc_details_pkey PRIMARY KEY (reimbursement_ctc_id)" +
				");ALTER TABLE reimbursement_ctc_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE reimbursement_head_details(" +
				"reimbursement_head_id serial NOT NULL," +
				"reimbursement_head_code character varying," +
				"reimbursement_head_name character varying," +
				"reimbursement_head_description text," +
				"added_by integer," +
				"entry_date date," +
				"reimbursement_ctc_id integer," +
				"level_id integer," +
				"org_id integer," +
				"CONSTRAINT reimbursement_head_details_pkey PRIMARY KEY (reimbursement_head_id)" +
				");ALTER TABLE reimbursement_head_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE reimbursement_head_amt_details(" +
				"reimbursement_head_amt_id serial NOT NULL," +
				"amount double precision," +
				"reimbursement_head_id integer," +
				"financial_year_start date," +
				"financial_year_end date," +
				"is_attachment boolean," +
				"is_optimal boolean," +
				"CONSTRAINT reimbursement_head_amt_details_pkey PRIMARY KEY (reimbursement_head_amt_id)" +
				");ALTER TABLE reimbursement_head_amt_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE reimbursement_assign_head_details(" +
				"reim_assign_head_id serial NOT NULL," +
				"emp_id integer," +
				"reimbursement_head_id integer," +
				"reimbursement_ctc_id integer," +
				"level_id integer," +
				"org_id integer," +
				"amount double precision," +
				"financial_year_start date," +
				"financial_year_end date," +
				"status boolean," +
				"trail_status boolean," +
				"added_by integer," +
				"entry_date date," +
				"update_by integer," +
				"update_date date," +
				"paycycle_from date," +
				"paycycle_to date," +
				"paycycle integer," +
				"CONSTRAINT reimbursement_assign_head_details_pkey PRIMARY KEY (reim_assign_head_id)" +
				");ALTER TABLE reimbursement_assign_head_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE reimbursement_ctc_applied_details(" +
				"reim_ctc_applied_id serial NOT NULL," +
				"emp_id integer," +
				"is_approved integer," +
				"entry_date date," +
				"reimbursement_head_id integer," +
				"description text," +
				"ref_document character varying," +
				"financial_year_start date," +
				"financial_year_end date," +
				"approved_by integer," +
				"approved_date date," +
				"is_nontaxable boolean," +
				"approver_user_type_id integer," +
				"approve_reason character varying," +
				"applied_amount double precision," +
				"reimbursement_ctc_id integer,"+
				"CONSTRAINT reimbursement_ctc_applied_details_pkey PRIMARY KEY (reim_ctc_applied_id)" +
				"); ALTER TABLE reimbursement_ctc_applied_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE reimbursement_ctc_applied_paycycle(" +
				"reimctcapply_paycycle_id serial NOT NULL," +
				"reim_ctc_applied_id integer," +
				"emp_id integer," +
				"paycycle_from date," +
				"paycycle_to date," +
				"paycycle integer," +
				"financial_year_start date," +
				"financial_year_end date," +
				"CONSTRAINT reimbursement_ctc_applied_paycycle_pkey PRIMARY KEY (reimctcapply_paycycle_id)" +
				"); ALTER TABLE reimbursement_ctc_applied_paycycle OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE reimbursement_ctc_pay(" +
				"reimbursement_ctc_pay_id serial NOT NULL," +
				"emp_id integer," +
				"pay_month integer," +
				"pay_year integer," +
				"reimbursement_ctc_id integer," +
				"reimbursement_head_id integer," +
				"amount double precision," +
				"paid_from date," +
				"paid_to date," +
				"paycycle integer DEFAULT 0," +
				"financial_year_from date," +
				"financial_year_to date," +
				"currency_id integer," +
				"statement_id integer," +
				"paid_by integer," +
				"paid_date date," +
				"bank_pay_type integer," +
				"payment_mode integer," +
				"CONSTRAINT reimbursement_ctc_pay_pkey PRIMARY KEY (reimbursement_ctc_pay_id)" +
				"); ALTER TABLE reimbursement_ctc_pay OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE reimbursement_ctc_tax_pay(" +
				"reim_ctc_tax_pay_id serial NOT NULL," +
				"emp_id integer," +
				"reimbursement_ctc_id integer," +
				"amount double precision," +
				"financial_year_start date," +
				"financial_year_end date," +
				"approve_by integer," +
				"approve_date date," +
				"CONSTRAINT reimbursement_ctc_tax_pay_pkey PRIMARY KEY (reim_ctc_tax_pay_id)" +
				"); ALTER TABLE reimbursement_ctc_tax_pay OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE work_location_info ADD COLUMN shift_base_type integer;");
		alQueries.add("ALTER TABLE salary_details ADD COLUMN is_reimbursement_ctc boolean;" +
				"ALTER TABLE salary_details ALTER COLUMN is_reimbursement_ctc SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE work_location_info ADD COLUMN shift_base_buffer_time double precision;");
	
		alQueries.add("CREATE TABLE allowance_hours_details(" +
				"allowance_hours_id serial NOT NULL," +
				"emp_id integer," +
				"allowance_hours double precision," +
				"paycycle_from date," +
				"paycycle_to date," +
				"paycycle integer," +
				"added_by integer," +
				"added_date date," +
				"CONSTRAINT allowance_hours_details_pkey PRIMARY KEY (allowance_hours_id)" +
				"); ALTER TABLE allowance_hours_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE allowance_condition_details ADD COLUMN is_add_days_attendance boolean;");
		
		alQueries.add("ALTER TABLE reimbursement_ctc_pay ADD COLUMN total_days double precision;");
		alQueries.add("ALTER TABLE reimbursement_ctc_pay ADD COLUMN paid_days double precision;");
		alQueries.add("ALTER TABLE reimbursement_ctc_pay ADD COLUMN present_days double precision;");
		alQueries.add("ALTER TABLE reimbursement_ctc_pay ADD COLUMN paid_leaves double precision;");
		alQueries.add("ALTER TABLE reimbursement_ctc_pay ADD COLUMN absent_days double precision;");
		
		alQueries.add("ALTER TABLE allowance_individual_details ADD COLUMN production_line_id integer;");
		
		alQueries.add("CREATE TABLE production_line_details(" +
				"production_line_id serial NOT NULL," +
				"production_line_code character varying," +
				"production_line_name character varying," +
				"org_id integer," +
				"added_by integer," +
				"added_date date," +
				"CONSTRAINT production_line_details_pkey PRIMARY KEY (production_line_id)" +
				"); ALTER TABLE production_line_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE production_line_heads(" +
				"production_line_head_id serial NOT NULL," +
				"production_line_id integer," +
				"level_id integer," +
				"salary_heads character varying," +
				"added_by integer," +
				"added_date date," +
				"CONSTRAINT production_line_heads_pkey PRIMARY KEY (production_line_head_id)" +
				"); ALTER TABLE production_line_heads OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE emp_leave_entry ADD COLUMN cancel_reason text;");
		alQueries.add("ALTER TABLE esi_details ADD COLUMN eligible_salary_head_ids character varying;");
		alQueries.add("ALTER TABLE salary_details ADD COLUMN salary_calculate_amount double precision;");
		alQueries.add("ALTER TABLE deduction_details_india ADD COLUMN gender character varying;");
		
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN is_apply_leave_limit boolean;" +
				"ALTER TABLE emp_leave_type ALTER COLUMN is_apply_leave_limit SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE arear_details ADD COLUMN arrear_type integer;" +
				"ALTER TABLE arear_details ALTER COLUMN arrear_type SET DEFAULT 0;");
		alQueries.add("ALTER TABLE arear_details ADD COLUMN arrear_days double precision;");
		alQueries.add("ALTER TABLE arear_details ADD COLUMN paycycle_from date;");
		alQueries.add("ALTER TABLE arear_details ADD COLUMN paycycle_to date;");
		alQueries.add("ALTER TABLE arear_details ADD COLUMN paycycle integer;");
		
		alQueries.add("ALTER TABLE emp_salary_details ADD COLUMN level_id integer;");
		alQueries.add("ALTER TABLE emp_salary_details ADD COLUMN grade_id integer;");
		
		alQueries.add("ALTER TABLE arrear_generation ADD COLUMN arear_id integer;");
		alQueries.add("ALTER TABLE arrear_generation ADD COLUMN approve_by integer;");
		alQueries.add("ALTER TABLE arrear_generation ADD COLUMN approve_date date;");
		alQueries.add("ALTER TABLE arrear_generation ADD COLUMN paid_by integer;");
		alQueries.add("ALTER TABLE arrear_generation ADD COLUMN paid_date date;");
		
		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN emp_mrd_no character varying;");
		
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN parent_id integer;" +
				"ALTER TABLE emp_reimbursement ALTER COLUMN parent_id SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN is_carryforward_accrual_monthly boolean;" +
				"ALTER TABLE emp_leave_type ALTER COLUMN is_carryforward_accrual_monthly SET DEFAULT true;");
		alQueries.add("update emp_leave_type set is_carryforward_accrual_monthly=true where is_carryforward_accrual_monthly is null");
		
		alQueries.add("update emp_reimbursement set parent_id = 0 where parent_id is null;");
		
		alQueries.add("ALTER TABLE loan_applied_details ADD COLUMN effective_date date;");
		alQueries.add("ALTER TABLE loan_details ADD COLUMN is_check_previous_loan boolean;");
		
		
		alQueries.add("ALTER TABLE arear_details ADD COLUMN is_approved integer;" +
				"ALTER TABLE arear_details ALTER COLUMN is_approved SET DEFAULT 0;");

		alQueries.add("ALTER TABLE arear_details ADD COLUMN approved_by integer;");
		alQueries.add("ALTER TABLE arear_details ADD COLUMN approve_date date;");
		
		alQueries.add("ALTER TABLE arear_details ADD COLUMN approve_deny_comment character varying;");
		
		alQueries.add("ALTER TABLE arear_details ADD COLUMN paycycles character varying;");

		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN cancel_reason character varying;");
		
		alQueries.add("ALTER TABLE section_details ADD COLUMN is_pf_applicable boolean;" +
				"ALTER TABLE section_details ALTER COLUMN is_pf_applicable SET DEFAULT false;");

		alQueries.add("ALTER TABLE work_location_info ADD COLUMN wlocation_weightage integer;");
		alQueries.add("ALTER TABLE designation_details ADD COLUMN designation_weightage integer;");
		
		alQueries.add("ALTER TABLE user_details ADD COLUMN is_geofence boolean;" +
				"ALTER TABLE user_details ALTER COLUMN is_geofence SET DEFAULT false;");
		alQueries.add("ALTER TABLE user_details ADD COLUMN geofence_locations character varying;");
		alQueries.add("ALTER TABLE work_location_info ADD COLUMN wlocation_lat double precision;");
		alQueries.add("ALTER TABLE work_location_info ADD COLUMN wlocation_long double precision;");
		alQueries.add("ALTER TABLE work_location_info ADD COLUMN geofence_distance double precision;");
		
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN reimb_currency integer;");
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN reimb_payment_mode integer;");
		alQueries.add("ALTER TABLE emp_reimbursement_draft ADD COLUMN reimb_currency integer;");
		alQueries.add("ALTER TABLE emp_reimbursement_draft ADD COLUMN reimb_payment_mode integer;");
		
		alQueries.add("ALTER TABLE user_details ADD COLUMN is_authorized_user_for_api boolean;" +
			"ALTER TABLE user_details ALTER COLUMN is_authorized_user_for_api SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE user_details ADD COLUMN is_roster_required boolean;" +
			"ALTER TABLE user_details ALTER COLUMN is_roster_required SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE communication_1 ADD COLUMN manniversary_emp_id integer;");
		alQueries.add("ALTER TABLE communication_1 ADD COLUMN wanniversary_emp_id integer;");
		
		alQueries.add("ALTER TABLE user_details ADD COLUMN is_show_clock_entries boolean;" +
			"ALTER TABLE user_details ALTER COLUMN is_show_clock_entries SET DEFAULT false;");
		alQueries.add("ALTER TABLE user_details ADD COLUMN is_single_button_clock_on_off boolean;" +
			"ALTER TABLE user_details ALTER COLUMN is_single_button_clock_on_off SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE appraisal_main_level_details ADD COLUMN other_peer integer;" +
			"ALTER TABLE appraisal_main_level_details ALTER COLUMN other_peer SET DEFAULT 0;");

		alQueries.add("ALTER TABLE approve_attendance ADD COLUMN sal_effective_date date;");
		alQueries.add("ALTER TABLE approve_attendance ADD COLUMN effective_date_total_days double precision;");
		alQueries.add("ALTER TABLE payroll_generation ADD COLUMN sal_effective_date date;");
		alQueries.add("ALTER TABLE payroll_generation_lta ADD COLUMN sal_effective_date date;");
		alQueries.add("ALTER TABLE arrear_generation ADD COLUMN sal_effective_date date;");

		alQueries.add("ALTER TABLE designation_details ADD COLUMN vda_index_probation double precision;");
		alQueries.add("ALTER TABLE designation_details ADD COLUMN vda_index_permanent double precision;");
		alQueries.add("ALTER TABLE designation_details ADD COLUMN vda_index_temporary double precision;");
		
		alQueries.add("ALTER TABLE org_details ADD COLUMN retirement_age double precision;");
		
		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN emp_other_bank_acct_ifsc_code character varying;");
		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN emp_other_bank_acct_ifsc_code_2 character varying;");
		
		alQueries.add("ALTER TABLE loan_payments ADD COLUMN sal_effective_date date;");
		
		alQueries.add("ALTER TABLE section_details ADD COLUMN is_ceiling_applicable boolean;" +
				"ALTER TABLE section_details ALTER COLUMN is_ceiling_applicable SET DEFAULT false;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN ceiling_amount double precision;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_1 character varying;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_1_amt double precision;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_1_description character varying;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_2 character varying;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_2_amt double precision;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_2_description character varying;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_3 character varying;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_3_amt double precision;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_3_description character varying;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_4 character varying;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_4_amt double precision;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_4_description character varying;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_5 character varying;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_5_amt double precision;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_5_description character varying;");
		
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_1_limit_type character varying;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_2_limit_type character varying;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_3_limit_type character varying;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_4_limit_type character varying;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_5_limit_type character varying;");
		
		alQueries.add("ALTER TABLE investment_details ADD COLUMN sub_section_limit_type character varying;");
		
		alQueries.add("ALTER TABLE investment_details ADD COLUMN sub_section_no integer;");
		alQueries.add("ALTER TABLE investment_details ADD COLUMN sub_section_amt double precision;");
		
		alQueries.add("ALTER TABLE exception_reason ADD COLUMN hours_worked double precision;");
		alQueries.add("ALTER TABLE exception_reason ADD COLUMN generated_date timestamp without time zone;");
		alQueries.add("ALTER TABLE exception_reason ADD COLUMN approved_date date;");
		
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_1_is_adjust_gross_income_limit boolean;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_2_is_adjust_gross_income_limit boolean;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_3_is_adjust_gross_income_limit boolean;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_4_is_adjust_gross_income_limit boolean;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN sub_section_5_is_adjust_gross_income_limit boolean;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN is_adjusted_gross_income_limit boolean;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN include_sub_section character varying;");
		alQueries.add("ALTER TABLE section_details ADD COLUMN combine_sub_section character varying;");
		
		alQueries.add("ALTER TABLE exception_reason ADD COLUMN approve_by integer;");
		alQueries.add("ALTER TABLE exception_reason ADD COLUMN approved_user_type integer;");
		
		alQueries.add("ALTER TABLE roster_policy_rules ADD COLUMN rotation_of_shift character varying;");
		
		alQueries.add("ALTER TABLE employee_activity_details ADD COLUMN emptype character varying;");
		
		alQueries.add("ALTER TABLE shift_details ADD COLUMN shift_name character varying;");
		alQueries.add("ALTER TABLE roster_policy_rules ADD COLUMN remaining_emp_shift character varying;");
		alQueries.add("ALTER TABLE roster_policy_rules ADD COLUMN depart_id character varying;");
		
		alQueries.add("ALTER TABLE roster_policy_rules ADD COLUMN no_of_days_for_stretch_shift integer;");
		alQueries.add("ALTER TABLE roster_policy_rules ADD COLUMN no_of_days_for_normal_weekoff integer;");
		
		alQueries.add("ALTER TABLE assign_shift_dates ADD COLUMN is_mail_sent boolean;" +
				"ALTER TABLE assign_shift_dates ALTER COLUMN is_mail_sent SET DEFAULT false;");
		alQueries.add("ALTER TABLE assign_shift_dates ADD COLUMN mail_sent_date timestamp without time zone;");
		
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN future_days integer;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN future_days_max integer;");
		
		alQueries.add("ALTER TABLE emp_leave_entry ADD COLUMN is_work_from_home boolean;" +
				"ALTER TABLE emp_leave_entry ALTER COLUMN is_work_from_home SET DEFAULT false;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN is_work_from_home boolean;" +
				"ALTER TABLE emp_leave_type ALTER COLUMN is_work_from_home SET DEFAULT false;");
		alQueries.add("ALTER TABLE leave_type ADD COLUMN is_work_from_home boolean;" +
				"ALTER TABLE leave_type ALTER COLUMN is_work_from_home SET DEFAULT false;");

		alQueries.add("ALTER TABLE client_poc ADD COLUMN client_brand_id integer;" +
				"ALTER TABLE client_poc ALTER COLUMN client_brand_id SET DEFAULT 0;");
		alQueries.add("ALTER TABLE client_poc ADD COLUMN entry_date date;");
		alQueries.add("ALTER TABLE client_poc ADD COLUMN updated_by integer;");
		alQueries.add("ALTER TABLE client_poc ADD COLUMN update_date date;");

		alQueries.add("ALTER TABLE payroll_bank_statement ADD COLUMN statement_body_excel character varying;");
		
		alQueries.add("ALTER TABLE task_type_setting ADD COLUMN task_request_autoapproved boolean;");
		
		alQueries.add("ALTER TABLE deduction_tax_details ADD COLUMN slab_type integer;" +
				"ALTER TABLE deduction_tax_details ALTER COLUMN slab_type SET DEFAULT 0;");

//		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN slab_type integer;" +
//				"ALTER TABLE employee_official_details ALTER COLUMN slab_type SET DEFAULT 0;");

		alQueries.add("ALTER TABLE section_details ADD COLUMN slab_type integer;" +
				"ALTER TABLE section_details ALTER COLUMN slab_type SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE exemption_details ADD COLUMN slab_type integer;" +
				"ALTER TABLE exemption_details ALTER COLUMN slab_type SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE emp_leave_entry ADD COLUMN from_time time without time zone;");
		alQueries.add("ALTER TABLE emp_leave_entry ADD COLUMN to_time time without time zone;");
		
		alQueries.add("ALTER TABLE projectmntnc ADD COLUMN client_brand_id integer;");
		
		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN pf_start_date date;");
		
		alQueries.add("ALTER TABLE payroll_bank_statement ADD COLUMN bank_uploader_excel character varying;");
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN flat_deduct_percent double precision;");
		
		alQueries.add("ALTER TABLE lwf_details ADD COLUMN org_id integer;");
		
		alQueries.add("ALTER TABLE salary_details ADD COLUMN is_contribution boolean;" +
		"ALTER TABLE salary_details ALTER COLUMN is_contribution SET DEFAULT false;");

		alQueries.add("ALTER TABLE gratuity_details ADD COLUMN calculate_percent double precision;");
		alQueries.add("ALTER TABLE gratuity_details ADD COLUMN effective_date date;");

		alQueries.add("ALTER TABLE salary_details ADD COLUMN salary_band_id integer;" +
		"ALTER TABLE salary_details ALTER COLUMN salary_band_id SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE leave_type ADD COLUMN is_short_leave boolean;");
		
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN distributed_month integer;");
		
		alQueries.add("ALTER TABLE salary_details ADD COLUMN max_cap_amount double precision;");
		
		alQueries.add("﻿ALTER TABLE notifications ADD COLUMN background_image character varying;");
		
		alQueries.add("ALTER TABLE exception_reason ADD COLUMN in_timestamp timestamp without time zone;");
		alQueries.add("ALTER TABLE exception_reason ADD COLUMN out_timestamp timestamp without time zone;");
		
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN joining_bonus_amount_details text;");
		alQueries.add("ALTER TABLE salary_details ADD COLUMN joining_bonus_component boolean;"
				+ "ALTER TABLE salary_details ALTER COLUMN joining_bonus_component SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE project_resource_req_details ADD request_read_status integer NULL DEFAULT 0;");
		alQueries.add("ALTER TABLE task_activity ADD activity_date_time timestamp without time zone NULL;");
		
		alQueries.add("ALTER TABLE project_resource_req_details ADD desig_id integer NULL;");
		
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN pro_ids character varying;");
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN sgst_percent double precision;");
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN sgst_amount double precision;");
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN cgst_percent double precision;");
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN cgst_amount double precision;");
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN igst_percent double precision;");
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN igst_amount double precision;");
		
		alQueries.add("ALTER TABLE promntc_invoice_amt_details ADD COLUMN pro_id integer;");
		
		alQueries.add("ALTER TABLE promntc_bill_amt_details ADD COLUMN invoice_ids character varying;"
				+ "ALTER TABLE promntc_bill_amt_details ADD COLUMN sgst_amount double precision;"
				+ "ALTER TABLE promntc_bill_amt_details ADD COLUMN cgst_amount double precision;"
				+ "ALTER TABLE promntc_bill_amt_details ADD COLUMN igst_amount double precision;"
				+ "ALTER TABLE promntc_bill_amt_details ADD COLUMN bank_id integer;");
		
		alQueries.add("ALTER TABLE promntc_bill_parti_amt_details ADD COLUMN invoice_id integer;");

		alQueries.add("ALTER TABLE public.cost_calculation_settings ALTER COLUMN days TYPE double precision USING days::double precision;");
		
		alQueries.add("ALTER TABLE public.cost_calculation_settings ADD artical_days double precision NULL;");
		
		alQueries.add("ALTER TABLE public.employee_personal_details ADD zoho_id varchar(500) NULL;");
		
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD eligible_resource_ids character varying NULL;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD available_resource_ids character varying NULL;");
		
		alQueries.add("ALTER TABLE public.employee_official_details ADD ust_id integer NULL;");
		
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD skill_family_id integer NULL;");
		
		alQueries.add("ALTER TABLE prev_earn_deduct_details ADD COLUMN org_pan_no character varying;" + 
				"ALTER TABLE prev_earn_deduct_details ADD COLUMN org_tan_no character varying;");
			
		alQueries.add("ALTER TABLE public.state ADD state_code character varying NULL;");
		alQueries.add("ALTER TABLE public.project_emp_details ADD resource_request_id integer NULL;");
		alQueries.add("ALTER TABLE public.project_emp_details ADD approval_read_status integer NOT NULL DEFAULT 0;");
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD additional_notes character varying NULL;");
		
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD resource_request_title varchar NULL;" + 
				"ALTER TABLE public.project_resource_req_details ADD resource_request_id varchar NULL;" + 
				"ALTER TABLE public.project_resource_req_details ADD allocation_start_date date NULL;" + 
				"ALTER TABLE public.project_resource_req_details ADD allocation_end_date date NULL;" + 
				"ALTER TABLE public.project_resource_req_details ADD billing_start_date date NULL;" + 
				"ALTER TABLE public.project_resource_req_details ADD billing_end_date date NULL;" + 
				"ALTER TABLE public.project_resource_req_details ADD jd_id integer NULL;" + 
				"ALTER TABLE public.project_resource_req_details ADD type_of_vacancy integer NULL;" + 
				"ALTER TABLE public.project_resource_req_details ADD type_of_employment character varying NULL;" + 
				"ALTER TABLE public.project_resource_req_details ADD org_id int4 NULL;" + 
				"ALTER TABLE public.project_resource_req_details ADD department_id int4 NULL;" + 
				"ALTER TABLE public.project_resource_req_details ADD level_id int4 NULL;" + 
				"ALTER TABLE public.project_resource_req_details ADD grade_id int4 NULL;" + 
				"ALTER TABLE public.project_resource_req_details ADD resource_req_priority int4 NULL;" + 
				"ALTER TABLE public.project_resource_req_details ADD must_have_skills varchar NULL;" + 
				"ALTER TABLE public.project_resource_req_details ADD nice_to_have_skills varchar NULL;" + 
				"ALTER TABLE public.project_resource_req_details ADD benchmark_resource_id int4 NULL;" + 
				"ALTER TABLE public.project_resource_req_details ADD resource_allocation_type int4 NULL;" + 
				"ALTER TABLE public.project_resource_req_details ADD allocation_percent int4 NULL;" + 
				"ALTER TABLE public.project_resource_req_details ADD is_notes_add bool NOT NULL DEFAULT false;" + 
				"ALTER TABLE public.project_resource_req_details ADD status int4 NULL DEFAULT 0;");
		
		alQueries.add("ALTER TABLE public.project_emp_details ADD allocation_comment varchar NULL;" + 
				"ALTER TABLE public.project_emp_details ADD resource_request_id int4 NULL;" + 
				"ALTER TABLE public.project_emp_details ADD allocation_type int4 NULL;" + 
				"ALTER TABLE public.project_emp_details ADD resource_role int4 NULL;");
		
		alQueries.add("ALTER TABLE public.bonus_details ADD bonus_slab_type integer NULL;");
		
		alQueries.add("ALTER TABLE employee_activity_details ADD COLUMN document_file_name character varying;");

		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN emergency_contact_relation character varying;");

		alQueries.add("ALTER TABLE emp_prev_employment ADD COLUMN emp_esic_no character varying; " + 
				"ALTER TABLE emp_prev_employment ADD COLUMN uan_no character varying; " + 
				"ALTER TABLE candidate_prev_employment ADD COLUMN emp_esic_no character varying; " + 
				"ALTER TABLE candidate_prev_employment ADD COLUMN uan_no character varying;");
		
		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN emp_other_bank_name character varying; " + 
				"ALTER TABLE employee_personal_details ADD COLUMN emp_other_bank_name2 character varying;");
	
		alQueries.add("ALTER TABLE public.emp_leave_type ADD prior_days_for_one_day_leave integer NULL;" + 
				"ALTER TABLE public.emp_leave_type ADD laps_days integer NULL;");
		
		alQueries.add("ALTER TABLE leave_type ADD COLUMN is_document_mandatory boolean;");
		
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN joining_month_day_date integer;\n" + 
				"ALTER TABLE emp_leave_type ADD COLUMN joining_month_balance double precision;\n" + 
				"ALTER TABLE emp_leave_type ADD COLUMN future_days_1 integer;\n" + 
				"ALTER TABLE emp_leave_type ADD COLUMN no_of_leaves1 integer;\n" + 
				"ALTER TABLE emp_leave_type ADD COLUMN no_of_leaves2 integer;\n" + 
				"ALTER TABLE emp_leave_type ADD COLUMN no_of_leaves3 integer;\n" + 
				"ALTER TABLE emp_leave_type ADD COLUMN min_long_leave_limit double precision;");
		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN long_leave_gap integer;");
		
		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN emp_other_bank_branch character varying;\n" + 
				"ALTER TABLE employee_personal_details ADD COLUMN emp_other_bank_branch2 character varying;");

		alQueries.add("ALTER TABLE emp_leave_type ADD COLUMN monthly_apply_leave_limit double precision;");

		alQueries.add("ALTER TABLE emp_lta_details ADD COLUMN paycycle integer;\n" + 
				"ALTER TABLE emp_leave_type ADD COLUMN monthly_apply_leave_limit double precision;");

		alQueries.add("ALTER TABLE emp_leave_entry ADD COLUMN backup_emp_name character varying;");
		
	}
	
	
	public void fillQueriesJupiter(List<String> alQueries) {
		
		alQueries.add("CREATE TABLE emp_one_one_discussion_details " + 
				"( " + 
				"  emp_one_one_discussion_details_id serial NOT NULL, " + 
				"  emp_id integer, " + 
				"  user_id integer, " + 
				"  user_type_id integer, " + 
				"  appraisal_id integer, " + 
				"  appraisal_freq_id integer, " + 
				"  user_rating double precision, " + 
				"  user_remark character varying, " + 
				"  user_entry_date timestamp without time zone, " + 
				"  emp_rating double precision, " + 
				"  emp_remark character varying, " + 
				"  emp_entry_date timestamp without time zone, " + 
				"  emp_sign_off boolean DEFAULT false, " + 
				"  user_approval boolean DEFAULT false, " + 
				"  CONSTRAINT emp_one_one_discussion_details_pkey PRIMARY KEY (emp_one_one_discussion_details_id) " + 
				"); " + 
				"ALTER TABLE emp_one_one_discussion_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE reviewer_feedback_details" + 
				"(" + 
				"reviewer_feedback_details_id serial NOT NULL," + 
				"emp_id integer," + 
				"appraisal_id integer, " + 
				"user_id integer, " + 
				"user_type_id integer, " + 
				"appraisal_freq_id integer, " + 
				"reviewer_comment character varying, " + 
				"reviewer_marks double precision, " + 
				"entry_date timestamp without time zone, " + 
				"is_submit boolean DEFAULT false, " + 
				"CONSTRAINT reviewer_feedback_details_pkey PRIMARY KEY (reviewer_feedback_details_id) " + 
				");"
				+ "ALTER TABLE reviewer_feedback_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE public.job_description_details ( " + 
				"	job_description_id serial NOT NULL, " + 
				"	job_description_code character varying NULL, " + 
				"	job_description_title character varying NULL, " + 
				"	job_description_data character varying NULL, " + 
				"	added_by integer NULL, " + 
				"	entry_date timestamp NULL, " + 
				"	updated_by integer NULL, " + 
				"	update_date timestamp NULL, " + 
				"	parent_jd_id integer NULL, " + 
				"	pro_resource_req_id integer NULL, " + 
				"	CONSTRAINT job_description_details_pkey PRIMARY KEY (job_description_id)"
				+ ");"
				+ "ALTER TABLE job_description_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE learning_nominee_details" +
				"(" +
				"nominated_details_id serial NOT NULL," +
				"learning_plan_id integer," +
				"requested_by integer," +
				"request_date timestamp without time zone," +
				"approve_status integer DEFAULT 0," +
				"approved_by integer," +
				"approve_date timestamp without time zone," +
				"approve_reason character varying," +
				"CONSTRAINT learning_nominee_details_pkey PRIMARY KEY (nominated_details_id)" +
				");" +
				"ALTER TABLE learning_nominee_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE learning_subvideo_details" +
				"(" +
				"learning_subvideo_id serial NOT NULL," +
				"learning_subvideo_title character varying," +
				"learning_subvideo_description character varying," +
				"video_link_id integer," +
				"learning_video_id integer," +
				"added_by integer," +
				"entry_date timestamp without time zone," +
				"updated_by integer," +
				"update_date timestamp without time zone," +
				"CONSTRAINT learning_subvideo_details_pkey PRIMARY KEY (learning_subvideo_id)" +
				");" +
				"ALTER TABLE learning_subvideo_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE learning_video_details" +
				"(" +
				"learning_video_id serial NOT NULL," +
				"learning_video_title character varying," +
				"learning_video_description character varying," +
				"video_link_id integer," +
				"entry_date timestamp without time zone," +
				"updated_by integer," +
				"added_by integer," +
				"update_date timestamp without time zone," +
				"CONSTRAINT learning_video_details_pkey PRIMARY KEY (learning_video_id)" +
				");" +
				"ALTER TABLE learning_video_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE learning_video_seen_details" +
				"(" +
				"learning_video_seen_id serial NOT NULL," +
				"emp_id integer," +
				"learning_plan_id integer," +
				"learning_video_id integer," +
				"learning_video_seen_status integer," +
				"added_by integer," +
				"entry_date timestamp without time zone," +
				"CONSTRAINT learning_video_seen_details_pkey PRIMARY KEY (learning_video_seen_id)" +
				");" +
				"ALTER TABLE learning_video_seen_details 	OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE video_link_details" +
				"(" +
				"video_link_id serial NOT NULL," +
				"video_name character varying," +
				"video_link_file character varying," +
				"CONSTRAINT video_link_details_pkey PRIMARY KEY (video_link_id)" +
				");" +
				"ALTER TABLE video_link_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE candidate_degree_certificate_details" +
				"(" +
				"candi_degree_certificate_id serial NOT NULL," +
				"degree_id integer," +
				"emp_id integer," +
				"degree_certificate_name character varying," +
				"CONSTRAINT candidate_degree_certificate_details_pkey PRIMARY KEY (candi_degree_certificate_id)" +
				");" +
				"ALTER TABLE candidate_degree_certificate_details OWNER TO postgres; ");
		
		alQueries.add("CREATE TABLE recruitment_technology" +
				"(" +
				"recruitment_technology_id serial NOT NULL," +
				"technology_name character varying," +
				"CONSTRAINT recruitment_technology_pkey PRIMARY KEY (recruitment_technology_id)" +
				");" +
				"ALTER TABLE recruitment_technology OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE candidate_certification_details" +
				"(" +
				"candidate_certification_details_id serial NOT NULL," +
				"certification_title character varying," +
				"certification_completion_year integer," +
				"location character varying," +
				"candidate_id integer," +
				"CONSTRAINT candidate_certification_details_pkey PRIMARY KEY (candidate_certification_details_id)" +
				");" +
				"ALTER TABLE candidate_certification_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE common_tasks" +
				"(" +
				"common_task_id serial NOT NULL," +
				"task_name character varying," +
				"added_by integer," +
				"entry_date timestamp without time zone," +
				"CONSTRAINT common_tasks_pkey PRIMARY KEY (common_task_id)" +
				");" +
				"ALTER TABLE common_tasks OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE resource_plan_request_details" +
				"(" +
				"resource_plan_request_id serial NOT NULL," +
				"pro_id integer," +
				"pro_start_date date," +
				"skill_id integer," +
				"wloc_ids character varying," +
				"min_exp double precision," +
				"max_exp double precision," +
				"req_res double precision," +
				"res_gap double precision," +
				"req_month integer," +
				"req_year integer," +
				"fy_start date," +
				"fy_end date," +
				"added_by integer," +
				"entry_date timestamp without time zone," +
				"updated_by integer," +
				"update_date timestamp without time zone," +
				"CONSTRAINT resource_plan_request_details_pkey PRIMARY KEY (resource_plan_request_id)" +
				");" +
				"ALTER TABLE resource_plan_request_details OWNER TO postgres;");
		
		
		alQueries.add("CREATE TABLE project_resource_req_details" +
				"(" +
				"project_resource_req_id serial NOT NULL," +
				"pro_id integer," +
				"skill_id integer," +
				"job_profile character varying," +
				"min_exp double precision," +
				"max_exp double precision," +
				"req_resource double precision," +
				"resource_gap double precision," +
				"added_by integer," +
				"entry_date timestamp without time zone," +
				"updated_by integer," +
				"update_date timestamp without time zone," +
				"CONSTRAINT project_resource_req_details_pkey PRIMARY KEY (project_resource_req_id)" +
				");" +
				"ALTER TABLE project_resource_req_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE reviewee_strength_improvements" +
				"(" +
				"reviewee_strength_improvements_id serial NOT NULL," +
				"emp_id integer," +
				"review_id integer," +
				"review_freq_id integer," +
				"user_id integer," +
				"user_type_id integer," +
				"entry_date timestamp without time zone," +
				"areas_of_strength character varying," +
				"areas_of_improvement character varying," +
				"CONSTRAINT reviewee_strength_improvements_pkey PRIMARY KEY (reviewee_strength_improvements_id)" +
				");" +
				"ALTER TABLE reviewee_strength_improvements OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE review_feedback_reopen_details" +
				"(" +
				"review_feedback_reopen_id serial NOT NULL," +
				"review_id integer," +
				"review_freq_id integer," +
				"emp_id integer," +
				"user_id integer," +
				"user_type_id integer," +
				"reviewer_or_appraiser integer," +
				"reopen_comment character varying," +
				"reopened_by integer," +
				"entry_date timestamp without time zone," +
				"CONSTRAINT review_feedback_reopen_details_pkey PRIMARY KEY (review_feedback_reopen_id)" +
				");" +
				"ALTER TABLE review_feedback_reopen_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE review_final_recommendation" +
				"(" +
				"review_final_recommendation_id serial NOT NULL," +
				"emp_id integer," +
				"review_id integer," +
				"review_freq_id integer," +
				"recommendation_comment text," +
				"user_id integer," +
				"entry_date date," +
				"areas_of_strength character varying," +
				"areas_of_development character varying," +
				"CONSTRAINT review_final_recommendation_pkey PRIMARY KEY (review_final_recommendation_id)" +
				");" +
				"ALTER TABLE review_final_recommendation OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE emp_shift_trans_details" +
				"(" +
				"emp_shift_trans_id serial NOT NULL," +
				"emp_id integer," +
				"shift_trans_date date," +
				"shift_id integer," +
				"next_shift_id integer," +
				"paycycle_no integer," +
				"added_by integer," +
				"entry_date timestamp without time zone," +
				"CONSTRAINT emp_shift_trans_details_pkey PRIMARY KEY (emp_shift_trans_id)" +
				");" +
				"ALTER TABLE emp_shift_trans_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE hr_client_visit_details" +
				"(" +
				"visit_id serial NOT NULL," +
				"hr_name character varying," +
				"client_name character varying," +
				"description character varying," +
				"time time without time zone," +
				"date date," +
				"entry_date timestamp without time zone," +
				"added_by integer," +
				"CONSTRAINT hr_client_visit_details_pkey PRIMARY KEY (visit_id)" +
				");" +
				"ALTER TABLE hr_client_visit_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE parameter_details" +
				"(" +
				"parameter_id serial NOT NULL," +
				"parameter_name character varying," +
				"parameter_desc character varying," +
				"org_id integer," +
				"added_by integer," +
				"entry_date timestamp without time zone," +
				"updated_by integer," +
				"update_date timestamp without time zone," +
				"CONSTRAINT parameter_details_pkey PRIMARY KEY (parameter_id)" +
				");" +
				"ALTER TABLE parameter_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE faq_details" +
				"(" +
				"faq_id serial NOT NULL," +
				"faq_question character varying," +
				"faq_answer character varying," +
				"added_by integer," +
				"entry_date timestamp without time zone," +
				"updated_by integer," +
				"update_date timestamp without time zone," +
				"org_id character varying," +
				"section_id integer," +
				"section_name character varying," +
				"CONSTRAINT faq_details_pkey PRIMARY KEY (faq_id)" +
				");" +
				"ALTER TABLE faq_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE bsc_details (" +
				"bsc_id serial NOT NULL," +
				"bsc_name character varying," +
				"bsc_vision character varying," +
				"bsc_mission character varying," +
				"bsc_perspective_ids character varying," +
				"CONSTRAINT bsc_details_pkey PRIMARY KEY (bsc_id)" +
				");" +
				"ALTER TABLE bsc_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE bsc_perspective_details" +
				"(" +
				"bsc_perspective_id serial NOT NULL," +
				"bsc_perspective_name character varying," +
				"weightage integer," +
				"CONSTRAINT bsc_perspective_details_pkey PRIMARY KEY (bsc_perspective_id)" +
				");" +
				"ALTER TABLE bsc_perspective_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE appraisal_reviewee_details" +
			"(" +
			"appraisal_reviewee_details_id serial NOT NULL," +
			"appraisal_id integer," +
			"appraisal_freq_id integer," +
			"reviewee_id integer," +
			"subordinate_ids character varying," +
			"peer_ids character varying," +
			"other_peer_ids character varying," +
			"supervisor_ids character varying," +
			"grand_supervisor_ids character varying," +
			"hod_ids character varying," +
			"ceo_ids character varying," +
			"hr_ids character varying," +
			"ghr_ids character varying," +
			"recruiter_ids character varying," +
			"other_ids character varying," +
			"added_by integer,"+
			"entry_date date,"+
			"CONSTRAINT appraisal_reviewee_details_pkey PRIMARY KEY (appraisal_reviewee_details_id)" +
			");" +
			"ALTER TABLE appraisal_reviewee_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE document_signature (" +
			"signature_id serial NOT NULL," +
			"signature_type integer," +
			"signature_image character varying," +
			"user_id integer," +
			"org_id integer," +
			"added_by integer," +
			"added_date date," +
			"updated_by integer," +
			"updated_date date," +
			"CONSTRAINT document_signature_pkey PRIMARY KEY (signature_id)" +
			");" +
			"ALTER TABLE document_signature OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE client_departments (" +
			"client_depart_id serial NOT NULL," +
			"client_depart_name character varying," +
			"CONSTRAINT client_departments_pkey PRIMARY KEY (client_depart_id)" +
			");" +
			"ALTER TABLE client_departments OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE client_designations (" +
			"client_desig_id serial NOT NULL," +
			"client_desig_name character varying," +
			"CONSTRAINT client_designations_pkey PRIMARY KEY (client_desig_id)" +
			");" +
			"ALTER TABLE client_designations OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE client_locations (" +
			"client_loc_id serial NOT NULL," +
			"client_loc_name character varying," +
			"CONSTRAINT client_locations_pkey PRIMARY KEY (client_loc_id)" +
			"); " +
			"ALTER TABLE client_locations OWNER TO postgres;");

			
		alQueries.add("CREATE TABLE emp_clock_on_off_access" +
				"(" +
				"clock_on_off_id serial NOT NULL," +
				"emp_id integer," +
				"is_web_access boolean," +
				"is_mobile_access boolean," +
				"is_biomatric_access boolean," +
				"added_by integer," +
				"entry_date date," +
				"updated_by date," +
				"update_date date," +
				"CONSTRAINT emp_clock_on_off_access_pkey PRIMARY KEY (clock_on_off_id)" +
				");" +
				"ALTER TABLE emp_clock_on_off_access OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE workrig_user_alerts" +
				"(" +
				"alerts_id serial NOT NULL," +
				"emp_id integer," +
				"alert_data character varying," +
				"alert_action character varying," +
				"emp_user_type integer," +
				"type character varying," +
				"from_cron boolean DEFAULT false," +
				"CONSTRAINT workrig_user_alerts_pkey PRIMARY KEY (alerts_id)" +
				");" +
				"ALTER TABLE workrig_user_alerts OWNER TO postgres");
		
		alQueries.add("CREATE TABLE goal_kra_target_finalization" +
				"(" +
				"gkt_finalization_id serial NOT NULL," +
				"emp_id integer," +
				"goal_id integer," +
				"goal_weightage double precision," +
				"goal_achieve_share double precision," +
				"goal_actual_achieved double precision," +
				"remark_comment character varying," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"CONSTRAINT goal_kra_target_finalization_pkey PRIMARY KEY (gkt_finalization_id)" +
				");" +
				"ALTER TABLE goal_kra_target_finalization   OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE meeting_room_details" +
				"(" +
				"meeting_room_id serial NOT NULL," +
				"meeting_room_name character varying," +
				"room_length character varying," +
				"room_width character varying," +
				"seating_capacity integer," +
				"room_color_code character varying," +
				"org_id integer," +
				"wlocation_id integer," +
				"added_by integer," +
				"entry_date date," +
				"update_by integer," +
				"updated_date date," +
				"CONSTRAINT meeting_room_details_pkey PRIMARY KEY (meeting_room_id)" +
				");" +
				"ALTER TABLE meeting_room_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE meeting_room_booking_details" +
				"(" +
				"booking_id serial NOT NULL," +
				"meeting_room_id integer," +
				"booking_from date," +
				"booking_to date," +
				"from_time time without time zone," +
				"to_time time without time zone," +
				"booked_by integer," +
				"request_date date," +
				"participants character varying," +
				"request_status integer," +
				"food_service_id integer," +
				"food_service_details character varying," +
				"meeting_status integer," +
				"approved_by integer," +
				"approved_date date," +
				"no_of_people integer," +
				"food_service_location character varying," +
				"guests character varying," +
				"booking_request_comment character varying," +
				"booking_purpose character varying," +
				"is_food_required boolean," +
				"CONSTRAINT meeting_room_booking_details_pkey PRIMARY KEY (booking_id)" +
				");" +
				"ALTER TABLE meeting_room_booking_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE book_details" +
				"(" +
				"book_id serial NOT NULL," +
				"book_image character varying," +
				"book_title character varying," +
				"book_author character varying," +
				"book_publisher character varying," +
				"book_year_published integer," +
				"book_isbn_no character varying," +
				"book_category character varying," +
				"book_short_description character varying," +
				"org_id integer," +
				"wlocation_id integer," +
				"book_quantity integer," +
				"added_by integer," +
				"entry_date date," +
				"last_updated_by integer," +
				"last_updated_date date," +
				"issued_quantity integer," +
				"CONSTRAINT book_details_pkey PRIMARY KEY (book_id)" +
				");" +
				"ALTER TABLE book_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE book_issues_returns" +
				"(" +
				"book_issued_id serial NOT NULL," +
				"book_id integer," +
				"request_quantity integer," +
				"approved_by integer," +
				"issue_status integer," +
				"book_issued_quantity integer," +
				"from_date date," +
				"to_date date," +
				"return_date date," +
				"returned_quantity integer," +
				"received_by integer," +
				"requested_by integer," +
				"return_status integer," +
				"issued_date date," +
				"request_comment character varying," +
				"request_date date," +
				"issue_comment character varying," +
				"return_comment character varying," +
				"CONSTRAINT book_issues_returns_pkey PRIMARY KEY (book_issued_id)" +
				");" +
				"ALTER TABLE book_issues_returns OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE book_purchases" +
				"(" +
				"book_purchase_id serial NOT NULL," +
				"book_id integer," +
				"purchased_by integer," +
				"approved_by integer," +
				"book_amount double precision," +
				"purchased_date date," +
				"requested_quantity integer," +
				"approved_quantity integer," +
				"requested_date date," +
				"request_status integer," +
				"emp_reason character varying," +
				"CONSTRAINT book_purchases_pkey PRIMARY KEY (book_purchase_id)" +
				");" +
				"ALTER TABLE book_purchases OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE book_reviews" +
				"(" +
				"book_review_id serial NOT NULL," +
				"book_id integer," +
				"emp_id integer," +
				"emp_rate integer," +
				"emp_comment character varying," +
				"entry_date date," +
				"last_updated_date date," +
				"CONSTRAINT book_reviews_pkey PRIMARY KEY (book_review_id)" +
				");" +
				"ALTER TABLE book_reviews OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE dish_details" +
				"(" +
				"dish_id serial NOT NULL," +
				"dish_name character varying," +
				"dish_image character varying," +
				"dish_type character varying," +
				"dish_from_date date," +
				"dish_to_date date," +
				"added_by integer," +
				"org_id integer," +
				"wlocation_id integer," +
				"dish_price integer," +
				"updated_by integer," +
				"last_updated_date date," +
				"dish_comment character varying," +
				"dish_from_time time without time zone," +
				"dish_to_time time without time zone," +
				"entry_date date," +
				"CONSTRAINT dish_details_pkey PRIMARY KEY (dish_id)" +
				");" +
				"ALTER TABLE dish_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE dish_order_details" +
				"(" +
				"order_id serial NOT NULL," +
				"dish_id integer," +
				"emp_id integer," +
				"order_status integer," +
				"dish_quantity integer," +
				"price integer," +
				"order_date date," +
				"confirmed_by integer," +
				"confirmed_date date," +
				"CONSTRAINT dish_order_details_pkey PRIMARY KEY (order_id)" +
				");" +
				"ALTER TABLE dish_order_details OWNER TO postgres;");
		
		
		alQueries.add("CREATE TABLE goal_kra_emp_status_rating_details" +
				"(" +
				"goal_kra_emp_status_rating_id serial NOT NULL," +
				"goal_id integer," +
				"kra_id integer," +
				"kra_task_id integer," +
				"emp_id integer," +
				"user_id integer," +
				"user_rating double precision," +
				"user_comment character varying," +
				"user_type character varying," +
				"entry_date date," +
				"goal_freq_id integer," +
				"CONSTRAINT goal_kra_emp_status_rating_details_pkey PRIMARY KEY (goal_kra_emp_status_rating_id)" +
				");" +
				"ALTER TABLE goal_kra_emp_status_rating_details OWNER TO postgres;");

		alQueries.add("CREATE TABLE goal_details_frequency" +
				"(" +
				"goal_freq_id serial NOT NULL," +
				"goal_id integer," +
				"goal_start_date date," +
				"goal_due_date date," +
				"freq_start_date date," +
				"freq_end_date date," +
				"goal_freq_name character varying," +
				"is_delete boolean DEFAULT false," +
				"added_by integer," +
				"entry_date date," +
				"CONSTRAINT goal_details_frequency_pkey PRIMARY KEY (goal_freq_id)" +
				");" +
				"ALTER TABLE goal_details_frequency OWNER TO postgres;");

		alQueries.add("CREATE TABLE goal_kra_tasks" +
				"(" +
				"goal_kra_task_id serial NOT NULL," +
				"goal_id integer," +
				"kra_id integer," +
				"task_name character varying," +
				"emp_ids character varying," +
				"entry_date date," +
				"added_by integer," +
				"update_date date," +
				"updated_by integer," +
				"CONSTRAINT goal_kra_tasks_pkey PRIMARY KEY (goal_kra_task_id)" +
				");" +
				"ALTER TABLE goal_kra_tasks OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE goal_kra_status_rating_details" +
				"(" +
				"kra_task_status_rating_id serial NOT NULL," +
				"goal_id integer," +
				"kra_id integer," +
				"kra_task_id integer," +
				"emp_id integer," +
				"complete_percent double precision," +
				"manager_id integer," +
				"manager_rating double precision," +
				"manager_comment character varying," +
				"hr_id integer," +
				"hr_rating double precision," +
				"hr_comment character varying," +
				"CONSTRAINT goal_kra_status_rating_details_pkey PRIMARY KEY (kra_task_status_rating_id)" +
				");" +
				"ALTER TABLE goal_kra_status_rating_details OWNER TO postgres;");

		alQueries.add("CREATE TABLE feature_management" +
				"(" +
				"feature_id serial NOT NULL," +
				"feature_name character varying," +
				"feature_status boolean DEFAULT false," +
				"user_type_id character varying," +
				"CONSTRAINT feature_management_pkey PRIMARY KEY (feature_id)" +
				");" +
				"ALTER TABLE feature_management OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE communication_1" +
				"(" +
				"communication_id serial NOT NULL," +
				"parent_id integer DEFAULT 0," +
				"communication character varying," +
				"align_with integer," +
				"align_with_id integer," +
				"tagged_with character varying," +
				"doc_shared_with_id character varying," +
				"doc_id character varying," +
				"visibility integer," +
				"visibility_with_id character varying," +
				"likes integer," +
				"like_ids character varying," +
				"created_by integer," +
				"time_zone character varying," +
				"create_time timestamp without time zone," +
				"CONSTRAINT communication_1_pkey PRIMARY KEY (communication_id)" +
				");" +
				"ALTER TABLE communication_1 OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE taskrig_user_alerts" +
				"(" +
				"alerts_id serial NOT NULL," +
				"resource_id integer," +
				"customer_id integer," +
				"alert_data character varying," +
				"alert_action character varying," +
				"CONSTRAINT taskrig_user_alerts_pkey PRIMARY KEY (alerts_id)" +
				");" +
				"ALTER TABLE taskrig_user_alerts OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE project_information_display" +
				"(" +
				"project_info_display_id serial NOT NULL," +
				"only_team boolean," +
				"is_cost boolean," +
				"is_rate boolean," +
				"updated_by integer," +
				"update_date date," +
				"CONSTRAINT project_information_display_pkey PRIMARY KEY (project_info_display_id)" +
				");" +
				"ALTER TABLE project_information_display OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE communications" +
				"(" +
				"communications_id serial NOT NULL," +
				"module_type character varying," +
				"pro_id integer," +
				"pro_freq_id integer," +
				"doc_id integer," +
				"bill_id integer," +
				"receipt_id integer," +
				"emp_id integer," +
				"cust_id integer," +
				"comments character varying," +
				"entry_time timestamp without time zone," +
				"CONSTRAINT communications_pkey PRIMARY KEY (communications_id)" +
				");" +
				"ALTER TABLE communications OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE user_details_customer" +
				"(" +
				"user_id serial NOT NULL," +
				"username character varying(45) NOT NULL," +
				"\"password\" character varying(45) NOT NULL," +
				"usertype_id integer," +
				"emp_id integer," +
				"status character varying(45)," +
				"start_date date," +
				"end_date date," +
				"is_termscondition boolean DEFAULT true," +
				"added_timestamp timestamp without time zone," +
				"reset_timestamp timestamp without time zone," +
				"is_forcepassword boolean DEFAULT false," +
				"wlocation_id_access character varying," +
				"org_id_access character varying," +
				"CONSTRAINT user_details_customer_pkey PRIMARY KEY (user_id)," +
				"CONSTRAINT user_details_customer_username_key UNIQUE (username)" +
				");" +
				"ALTER TABLE user_details_customer OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE task_type_setting" +
				"(" +
				"task_type_setting_id serial NOT NULL," +
				"org_id integer," +
				"forced_task boolean," +
				"description character varying," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"CONSTRAINT task_type_setting_pkey PRIMARY KEY (task_type_setting_id)" +
				");" +
				"ALTER TABLE task_type_setting OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE cost_calculation_settings" +
				"(" +
				"cost_calculation_id serial NOT NULL," +
				"org_id integer," +
				"calculation_type integer," +
				"calculation_type_label character varying," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"CONSTRAINT cost_calculation_settings_pkey PRIMARY KEY (cost_calculation_id)" +
				");" +
				"ALTER TABLE cost_calculation_settings OWNER TO postgres;");
		
		
		alQueries.add("CREATE TABLE tax_setting_history" +
				"(" +
				"tax_setting_history_id serial NOT NULL," +
				"tax_setting_id integer," +
				"tax_percent double precision," +
				"added_by integer," +
				"entry_date date," +
				"invoice_or_customer integer," +
				"effective_date date," +
				"CONSTRAINT tax_setting_history_pkey PRIMARY KEY (tax_setting_history_id)" +
				");" +
				"ALTER TABLE tax_setting_history OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE services_project_sbu" +
				"(" +
				"services_pro_sbu_id serial NOT NULL," +
				"service_pro_id integer," +
				"sbu_id integer," +
				"CONSTRAINT services_project_sbu_pkey PRIMARY KEY (services_pro_sbu_id)" +
				");" +
				"ALTER TABLE services_project_sbu OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE project_document_details(" +
				"pro_document_id serial NOT NULL," +
				"client_id integer," +
				"pro_id integer," +
				"folder_name character varying," +
				"document_name character varying," +
				"added_by integer," +
				"entry_date timestamp without time zone," +
				"folder_file_type character varying," +
				"pro_folder_id integer," +
				"file_size character varying," +
				"file_type character varying," +
				"align_with integer," +
				"sharing_type integer," +
				"sharing_resources character varying," +
				"size_in_bytes character varying," +
				"project_category integer," +
				"doc_parent_id integer," +
				"scope_document character varying," +
				"description text," +
				"doc_version integer," +
				"CONSTRAINT project_document_details_pkey PRIMARY KEY (pro_document_id)" +
				");ALTER TABLE project_document_details OWNER TO postgres;");
		
		
		alQueries.add("CREATE TABLE projectmntnc_frequency" +
				"(" +
				"pro_freq_id serial NOT NULL," +
				"pro_id integer," +
				"pro_start_date date," +
				"pro_end_date date," +
				"freq_start_date date," +
				"freq_end_date date," +
				"added_by integer," +
				"entry_date date," +
				"CONSTRAINT projectmntnc_frequency_pkey PRIMARY KEY (pro_freq_id)" +
				");" +
				"ALTER TABLE projectmntnc_frequency OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE promntc_bill_parti_amt_details" +
				"(" +
				"promntc_bill_parti_amt_id serial NOT NULL," +
				"bill_particulars character varying," +
				"bill_particulars_amount double precision," +
				"promntc_invoice_bill_id integer," +
				"emp_id integer," +
				"oc_bill_particulars_amount double precision," +
				"head_type character varying," +
				"tax_percent double precision," +
				"CONSTRAINT promntc_bill_parti_amt_details_pkey PRIMARY KEY (promntc_bill_parti_amt_id)" +
				");" +
				"ALTER TABLE promntc_bill_parti_amt_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE additional_info_of_pro_invoice" +
				"(" +
				"pro_additional_info_id serial NOT NULL," +
				"additional_info_text character varying," +
				"pro_id integer," +
				"CONSTRAINT additional_info_of_pro_invoice_pkey PRIMARY KEY (pro_additional_info_id)" +
				");" +
				"ALTER TABLE additional_info_of_pro_invoice OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE additional_info_of_invoice" +
				"(" +
				"additional_info_id serial NOT NULL," +
				"additional_info_text character varying," +
				"org_id integer," +
				"CONSTRAINT additional_info_of_invoice_pkey PRIMARY KEY (additional_info_id)" +
				");" +
				"ALTER TABLE additional_info_of_invoice OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE billing_head_setting" +
				"(" +
				"billing_head_id serial NOT NULL," +
				"head_label character varying," +
				"head_data_type integer," +
				"head_other_variable integer," +
				"org_id integer," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"CONSTRAINT billing_head_setting_pkey PRIMARY KEY (billing_head_id)" +
				");" +
				"ALTER TABLE billing_head_setting OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE project_tax_setting" +
				"(" +
				"pro_tax_setting_id serial NOT NULL," +
				"tax_name character varying," +
				"tax_percent double precision," +
				"pro_id integer," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"invoice_or_customer integer," +
				"CONSTRAINT project_tax_setting_pkey PRIMARY KEY (pro_tax_setting_id)" +
				");" +
				"ALTER TABLE project_tax_setting OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE tax_setting" +
				"(" +
				"tax_setting_id serial NOT NULL," +
				"tax_name character varying," +
				"tax_percent double precision," +
				"org_id integer," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"invoice_or_customer integer," +
				"CONSTRAINT tax_setting_pkey PRIMARY KEY (tax_setting_id)" +
				");" +
				"ALTER TABLE tax_setting OWNER TO postgres;");
		
		
		alQueries.add("CREATE TABLE performance_details_empwise" +
				"(" +
				"performance_empwise_id serial NOT NULL," +
				"performance_id integer," +
				"performance_file_name character varying," +
				"emp_id integer," +
				"emp_performance_file_name character varying," +
				"upload_date date," +
				"added_by integer," +
				"entry_date date," +
				"CONSTRAINT performance_details_empwise_pkey PRIMARY KEY (performance_empwise_id)" +
				");" +
				"ALTER TABLE performance_details_empwise OWNER TO postgres;");
		
		
		alQueries.add("CREATE TABLE performance_details" +
				"(" +
				"performance_id serial NOT NULL," +
				"performance_name character varying," +
				"performance_file_name character varying," +
				"emp_ids character varying," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"CONSTRAINT performance_details_pkey PRIMARY KEY (performance_id)" +
				");" +
				"ALTER TABLE performance_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE invoice_formats" +
				"(" +
				"invoice_formats_id serial NOT NULL," +
				"invoice_format_name character varying," +
				"section_1 character varying," +
				"section_2 character varying," +
				"section_3 character varying," +
				"section_4 character varying," +
				"section_5 character varying," +
				"section_6 character varying," +
				"section_7 character varying," +
				"section_8 character varying," +
				"section_9 character varying," +
				"section_10 character varying," +
				"section_11 character varying," +
				"section_12 character varying," +
				"section_13 character varying," +
				"section_14 character varying," +
				"section_15 character varying," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"CONSTRAINT invoice_formats_pkey PRIMARY KEY (invoice_formats_id)" +
				");ALTER TABLE invoice_formats OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE porject_billing_heads_details" +
				"(" +
				"pro_billing_head_id serial NOT NULL," +
				"head_label character varying," +
				"head_data_type integer," +
				"head_other_variable integer," +
				"pro_id integer," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"CONSTRAINT porject_billing_heads_details_pkey PRIMARY KEY (pro_billing_head_id)" +
				");" +
				"ALTER TABLE porject_billing_heads_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE currencies_details" +
				"(" +
				"currencies_id serial NOT NULL," +
				"curr_from_id integer," +
				"curr_to_id integer," +
				"curr_to_value double precision," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"CONSTRAINT currencies_details_pkey PRIMARY KEY (currencies_id)" +
				");" +
				"ALTER TABLE currencies_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE service_skills_details" +
				"(" +
				"service_skill_id serial NOT NULL," +
				"service_id integer," +
				"skill_id integer," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"skill_description character varying," +
				"CONSTRAINT service_skills_details_pkey PRIMARY KEY (service_skill_id)" +
				");" +
				"ALTER TABLE service_skills_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE statutory_id_registration_info_history" +
				"(" +
				"stat_id_reg_info_id serial NOT NULL," +
				"org_id integer," +
				"wlocation_id integer," +
				"org_tan_no character varying," +
				"org_pan_no character varying," +
				"added_by integer," +
				"entry_date date," +
				"CONSTRAINT statutory_id_registration_info_history_pkey PRIMARY KEY (stat_id_reg_info_id)" +
				");" +
				"ALTER TABLE statutory_id_registration_info_history OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE designation_kra_details" +
				"(" +
				"designation_kra_id serial NOT NULL," +
				"designation_id integer," +
				"kra_name character varying," +
				"entry_date date," +
				"added_by integer," +
				"updated_by integer," +
				"update_date date," +
				"CONSTRAINT designation_kra_details_pkey PRIMARY KEY (designation_kra_id)" +
				");ALTER TABLE designation_kra_details OWNER TO postgres;");

		alQueries.add("CREATE TABLE certificate_details" +
				"(" +
				"certificate_details_id serial NOT NULL," +
				"certificate_name character varying," +
				"certificate_title character varying," +
				"certificate_logo_align integer," +
				"certificate_border integer," +
				"certificate_first_line integer," +
				"certificate_second_line integer," +
				"certificate_third_line integer," +
				"certificate_font_size integer," +
				"certificate_sign_one integer," +
				"certificate_sign_two integer," +
				"certificate_sign_three integer," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"CONSTRAINT certificate_details_pkey PRIMARY KEY (certificate_details_id)" +
				");ALTER TABLE certificate_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE certificate_master_data_details" +
				"(" +
				"certificate_master_data_id serial NOT NULL," +
				"data_text character varying," +
				"data_value integer," +
				"data_type character varying," +
				"data_type_value integer," +
				"CONSTRAINT certificate_master_data_details_pkey PRIMARY KEY (certificate_master_data_id)" +
				");ALTER TABLE certificate_master_data_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE learning_plan_finalize_details" +
				"(" +
				"learning_plan_finalize_id serial NOT NULL," +
				"learning_plan_id integer," +
				"training_id integer," +
				"assessment_id integer," +
				"course_id integer," +
				"emp_id integer," +
				"training_certificate_id integer," +
				"certificate_status boolean," +
				"thumbsup_status boolean," +
				"send_to_gap_status boolean," +
				"added_by integer," +
				"entry_date date," +
				"finalize_remark character varying," +
				"CONSTRAINT learning_plan_finalize_details_pkey PRIMARY KEY (learning_plan_finalize_id)" +
				");ALTER TABLE learning_plan_finalize_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE training_schedule_details" +
				"(" +
				"training_schedule_details_id serial NOT NULL," +
				"day_date date," +
				"day_description character varying," +
				"training_id integer," +
				"added_by integer," +
				"entry_date date," +
				"training_schedule_id integer," +
				"CONSTRAINT training_schedule_details_pkey PRIMARY KEY (training_schedule_details_id)" +
				");");
		
		alQueries.add("CREATE TABLE training_mark_grade_type" +
				"(" +
				"training_mark_grade_type_id serial NOT NULL," +
				"numeric_grade_type character varying," +
				"alphabet_grade_type character varying," +
				"grade_standard integer," +
				"CONSTRAINT training_mark_grade_type_pkey PRIMARY KEY (training_mark_grade_type_id)" +
				");" +
				"ALTER TABLE training_mark_grade_type OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE candidate_skills_rating_details" +
				"(" +
				"candidate_skill_rating_id serial NOT NULL," +
				"candidate_id integer," +
				"emp_id integer," +
				"skill_id integer," +
				"skill_value double precision," +
				"recruitment_id integer," +
				"added_by integer," +
				"entry_date timestamp without time zone," +
				"CONSTRAINT candidate_skills_rating_details_pkey PRIMARY KEY (candidate_skill_rating_id)" +
				");" +
				"ALTER TABLE candidate_skills_rating_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE successionplan_criteria_details" +
				"(" +
				"successionplan_criteria_id serial NOT NULL," +
				"designation_id integer," +
				"qualification_id integer," +
				"qualification_weight integer," +
				"total_exp character varying," +
				"precent_org_exp character varying," +
				"potential_attribute character varying," +
				"performance_attribute character varying," +
				"potential_threshold integer," +
				"performance_threshold integer," +
				"skills character varying," +
				"skills_threshold integer," +
				"department_ids character varying," +
				"sbu_ids character varying," +
				"geography_ids character varying," +
				"level_ids character varying," +
				"added_by integer," +
				"entry_date timestamp without time zone," +
				"updated_by integer," +
				"update_date timestamp without time zone," +
				"CONSTRAINT successionplan_criteria_details_pkey PRIMARY KEY (successionplan_criteria_id)" +
				");" +
				"ALTER TABLE successionplan_criteria_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE plan_status_details" +
				"(" +
				"plan_status_id serial NOT NULL," +
				"emp_id integer," +
				"plan_status character varying," +
				"added_by integer," +
				"entry_date date," +
				"CONSTRAINT plan_status_details_pkey PRIMARY KEY (plan_status_id)" +
				");" +
				"ALTER TABLE plan_status_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE course_read_update_details" +
				"(" +
				"course_read_update_id serial NOT NULL," +
				"emp_id integer," +
				"learning_plan_id integer," +
				"course_id integer," +
				"course_chapter_id integer," +
				"course_subchapter_id integer," +
				"read_status integer," +
				"added_by integer," +
				"entry_date date," +
				"CONSTRAINT course_read_update_details_pkey PRIMARY KEY (course_read_update_id)" +
				");" +
				"ALTER TABLE course_read_update_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE course_read_details" +
				"(" +
				"course_read_id serial NOT NULL," +
				"emp_id integer," +
				"learning_plan_id integer," +
				"course_id integer," +
				"course_read_status integer," +
				"added_by integer," +
				"entry_date date," +
				"CONSTRAINT course_read_details_pkey PRIMARY KEY (course_read_id)" +
				");" +
				"ALTER TABLE course_read_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE training_attend_details" +
				"(" +
				"training_attend_id serial NOT NULL," +
				"emp_id integer," +
				"learning_plan_id integer," +
				"training_id integer," +
				"attend_status integer," +
				"added_by integer," +
				"entry_date date," +
				"CONSTRAINT training_attend_details_pkey PRIMARY KEY (training_attend_id)" +
				");" +
				"ALTER TABLE training_attend_details OWNER TO postgres;");
	
	
		alQueries.add("CREATE TABLE assessment_question_details" +
				"(" +
				"assessment_question_id serial NOT NULL," +
				"question_bank_id integer," +
				"assessment_details_id integer," +
				"assessment_section_id integer," +
				"weightage double precision," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"ref_assessment_question_id integer," +
				"CONSTRAINT assessment_question_details_pkey PRIMARY KEY (assessment_question_id)" +
				");" +
				"ALTER TABLE assessment_question_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE assessment_question_bank" +
				"(" +
				"assessment_question_bank_id serial NOT NULL," +
				"question_text character varying," +
				"option_a character varying," +
				"option_b character varying," +
				"option_c character varying," +
				"option_d character varying," +
				"correct_ans character varying," +
				"is_add boolean," +
				"answer_type integer," +
				"CONSTRAINT assessment_question_bank_pkey PRIMARY KEY (assessment_question_bank_id)" +
				");" +
				"ALTER TABLE assessment_question_bank OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE assessment_section_details" +
				"(" +
				"assessment_section_id serial NOT NULL," +
				"assessment_section_name character varying," +
				"assessment_section_description character varying," +
				"marks_of_section integer," +
				"attempt_questions integer," +
				"assessment_details_id integer," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"CONSTRAINT assessment_section_details_pkey PRIMARY KEY (assessment_section_id)" +
				");" +
				"ALTER TABLE assessment_section_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE assessment_details" +
				"(" +
				"assessment_details_id serial NOT NULL," +
				"assessment_name character varying," +
				"assessment_subject character varying," +
				"assessment_author character varying," +
				"date_of_creation date," +
				"assessment_version character varying," +
				"assessment_description character varying," +
				"assessment_index character varying," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"parent_assessment_id integer," +
				"ref_assessment_id integer," +
				"CONSTRAINT assessment_details_pkey PRIMARY KEY (assessment_details_id)" +
				");" +
				"ALTER TABLE assessment_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE assessment_question_answer" +
				"(" +
				"assess_question_answer_id serial NOT NULL," +
				"assessment_question_id integer," +
				"answer character varying," +
				"remark character varying," +
				"marks double precision," +
				"weightage double precision," +
				"emp_id integer," +
				"user_id integer," +
				"user_type_id integer," +
				"entry_date date," +
				"assessment_details_id integer," +
				"assessment_section_id integer," +
				"assessment_question_bank_id integer," +
				"learning_plan_id integer," +
				"answers_comment character varying," +
				"read_status integer," +
				"read_status_comment character varying," +
				"CONSTRAINT assessment_question_answer_pkey PRIMARY KEY (assess_question_answer_id)" +
				");" +
				"ALTER TABLE assessment_question_answer OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE assessment_take_attempt_details" +
				"(" +
				"assessment_take_attempt_id serial NOT NULL," +
				"assessment_details_id integer," +
				"take_attempt_count integer," +
				"assessment_section_ids character varying," +
				"added_by integer," +
				"entry_date date," +
				"learning_plan_id integer," +
				"CONSTRAINT assessment_take_attempt_details_pkey PRIMARY KEY (assessment_take_attempt_id)" +
				");" +
				"ALTER TABLE assessment_take_attempt_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE course_read_update_details" +
				"(" +
				"course_read_update_id serial NOT NULL," +
				"emp_id integer," +
				"learning_plan_id integer," +
				"course_id integer," +
				"course_chapter_id integer," +
				"course_subchapter_id integer," +
				"read_status integer," +
				"added_by integer," +
				"entry_date date," +
				"CONSTRAINT course_read_update_details_pkey PRIMARY KEY (course_read_update_id)" +
				");" +
				"ALTER TABLE course_read_update_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE course_read_details" +
				"(" +
				"course_read_id serial NOT NULL," +
				"emp_id integer," +
				"learning_plan_id integer," +
				"course_id integer," +
				"course_read_status integer," +
				"added_by integer," +
				"entry_date date," +
				"CONSTRAINT course_read_details_pkey PRIMARY KEY (course_read_id)" +
				");" +
				"ALTER TABLE course_read_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE training_attend_details" +
				"(" +
				"training_attend_id serial NOT NULL," +
				"emp_id integer," +
				"learning_plan_id integer," +
				"training_id integer," +
				"attend_status integer," +
				"added_by integer," +
				"entry_date date," +
				"CONSTRAINT training_attend_details_pkey PRIMARY KEY (training_attend_id)" +
				");" +
				"ALTER TABLE training_attend_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE educational_details" +
				"(" +
				"edu_id serial NOT NULL," +
				"education_name character varying," +
				"education_details text," +
				"org_id integer," +
				"weightage integer," +
				"CONSTRAINT educational_details_pkey PRIMARY KEY (edu_id)" +
				");" +
				"ALTER TABLE educational_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE candidate_salary_details" +
				"(" +
				"emp_salary_id serial NOT NULL," +
				"emp_id integer," +
				"salary_head_id integer," +
				"amount double precision," +
				"entry_date date," +
				"user_id integer," +
				"pay_type character varying(1)," +
				"isdisplay boolean DEFAULT true," +
				"service_id integer," +
				"effective_date date," +
				"approved_date date," +
				"approved_by integer," +
				"is_approved boolean DEFAULT false," +
				"earning_deduction character varying(1)," +
				"recruitment_id integer," +
				"CONSTRAINT candidate_salary_details_pkey PRIMARY KEY (emp_salary_id)" +
				");" +
				"ALTER TABLE candidate_salary_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE candidate_interview_panel_availability" +
				"(" +
				"panel_interview_id serial NOT NULL," +
				"int_avail_id integer," +
				"is_live integer NOT NULL," +
				"panel_emp_id integer," +
				"recruitment_id integer," +
				"candidate_id integer," +
				"CONSTRAINT candidate_interview_panel_availability_pkey PRIMARY KEY (panel_interview_id)" +
				");" +
				"ALTER TABLE candidate_interview_panel_availability OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE requirement_employment_type" +
				"(" +
				"employment_type_id serial NOT NULL," +
				"employment_type character varying," +
				"CONSTRAINT requirement_employment_type_pkey PRIMARY KEY (employment_type_id)" +
				");" +
				"ALTER TABLE requirement_employment_type OWNER TO postgres;");

		alQueries.add("CREATE TABLE learning_plan_stage_details" +
				"(" +
				"learning_plan_stage_id serial NOT NULL," +
				"learning_plan_stage_name character varying," +
				"from_date date," +
				"to_date date," +
				"from_time time without time zone," +
				"to_time time without time zone," +
				"learning_type character varying," +
				"learning_plan_id integer," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"updated_date date," +
				"weekdays character varying," +
				"learning_plan_stage_name_id integer," +
				"CONSTRAINT learning_plan_stage_details_pkey PRIMARY KEY (learning_plan_stage_id)" +
				");" +
				"ALTER TABLE learning_plan_stage_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE learning_plan_question_bank" +
				"(" +
				"learning_plan_question_bank_id serial NOT NULL," +
				"learning_plan_question_text text," +
				"option_a text," +
				"option_b text," +
				"option_c text," +
				"option_d text," +
				"correct_ans text," +
				"answer_type integer," +
				"learning_plan_id integer," +
				"weightage double precision," +
				"added_by integer," +
				"entry_date date," +
				"CONSTRAINT learning_plan_question_bank_pkey PRIMARY KEY (learning_plan_question_bank_id)" +
				");" +
				"ALTER TABLE learning_plan_question_bank OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE learning_plan_details" +
				"(" +
				"learning_plan_id serial NOT NULL," +
				"learning_plan_name character varying," +
				"learning_plan_objective character varying," +
				"group_or_condition integer," +
				"learner_ids character varying," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"updated_date date," +
				"is_publish boolean," +
				"org_id character varying," +
				"location_id character varying," +
				"level_id character varying," +
				"desig_id character varying," +
				"grade_id character varying," +
				"attribute_id character varying," +
				"CONSTRAINT learning_plan_details_pkey PRIMARY KEY (learning_plan_id)" +
				");" +
				"ALTER TABLE learning_plan_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE course_subject_details" +
				"(" +
				"course_subject_id serial NOT NULL," +
				"course_subject_name character varying," +
				"added_by integer," +
				"entry_date date," +
				"CONSTRAINT course_subject_details_pkey PRIMARY KEY (course_subject_id)" +
				");" +
				"ALTER TABLE course_subject_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE course_subchapter_details" +
				"(" +
				"course_subchapter_id serial NOT NULL," +
				"course_subchapter_name character varying," +
				"subchapter_description character varying," +
				"course_chapter_id integer," +
				"course_id integer," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"CONSTRAINT course_subchapter_details_pkey PRIMARY KEY (course_subchapter_id)" +
				");" +
				"ALTER TABLE course_subchapter_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE course_question_bank" +
				"(" +
				"course_question_bank_id serial NOT NULL," +
				"course_question_text text," +
				"option_a text," +
				"option_b text," +
				"option_c text," +
				"option_d text," +
				"correct_ans text," +
				"is_add boolean," +
				"answer_type integer," +
				"CONSTRAINT course_question_bank_pkey PRIMARY KEY (course_question_bank_id)" +
				");" +
				"ALTER TABLE course_question_bank OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE course_details" +
				"(" +
				"course_id serial NOT NULL," +
				"course_name character varying," +
				"course_subject character varying," +
				"author character varying," +
				"date_of_creation date," +
				"course_version character varying," +
				"preface character varying," +
				"course_index character varying," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"CONSTRAINT course_details_pkey PRIMARY KEY (course_id)" +
				");" +
				"ALTER TABLE course_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE course_content_details" +
				"(" +
				"course_content_id serial NOT NULL," +
				"course_content_name character varying," +
				"content_type character varying," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"course_subchapter_id integer," +
				"course_chapter_id integer," +
				"course_id integer," +
				"CONSTRAINT course_content_details_pkey PRIMARY KEY (course_content_id)" +
				");" +
				"ALTER TABLE course_content_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE course_chapter_details" +
				"(" +
				"course_chapter_id serial NOT NULL," +
				"course_chapter_name character varying," +
				"chapter_description character varying," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"course_id integer," +
				"CONSTRAINT course_chapter_details_pkey PRIMARY KEY (course_chapter_id)" +
				");" +
				"ALTER TABLE course_chapter_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE course_assessment_details" +
				"(" +
				"course_assessment_id serial NOT NULL," +
				"assessment_id integer," +
				"course_id integer," +
				"course_chapter_id integer," +
				"course_subchapter_id integer," +
				"added_by integer," +
				"entry_date date," +
				"updated_by integer," +
				"update_date date," +
				"weightage double precision," +
				"CONSTRAINT course_assessment_details_pkey PRIMARY KEY (course_assessment_id)" +
				");" +
				"ALTER TABLE course_assessment_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE trainer_family_members" +
				"(" +
				"member_type character varying(10)," +
				"member_name character varying(100)," +
				"member_dob date," +
				"member_education character varying(50)," +
				"member_occupation character varying(50)," +
				"member_contact_no character varying(50)," +
				"member_email_id character varying(50)," +
				"member_gender character varying(10)," +
				"member_id serial NOT NULL," +
				"emp_id integer," +
				"CONSTRAINT trainer_family_members_pkey PRIMARY KEY (member_id)" +
				");" +
				"ALTER TABLE trainer_family_members OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE candidate_application_details" +
				"(" +
				"candi_application_deatils_id serial NOT NULL," +
				"candidate_id integer," +
				"recruitment_id integer," +
				"job_code character varying(20)," +
				"application_date date," +
				"application_status integer DEFAULT 0," +
				"application_status_reason text," +
				"candidate_final_status integer DEFAULT 0," +
				"candidate_joining_date date," +
				"ctc_offered double precision," +
				"candidate_status integer DEFAULT 0," +
				"candidate_hr_comments text," +
				"candididate_emp_id integer," +
				"send_notification_status integer DEFAULT 0," +
				"renegotiate_remark character varying," +
				"offer_accept_remark character varying," +
				"added_by integer," +
				"entry_date timestamp without time zone," +
				"CONSTRAINT candidate_application_details_pkey PRIMARY KEY (candi_application_deatils_id)" +
				");" +
				"ALTER TABLE candidate_application_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE goal_type_details" +
				"(" +
				"goal_type_id serial NOT NULL," +
				"goal_type_name character varying," +
				"CONSTRAINT goal_type_details_pkey PRIMARY KEY (goal_type_id)" +
				");" +
				"ALTER TABLE goal_type_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE goal_kras_amt_details" +
				"(" +
				"goal_kra_amt_id serial NOT NULL," +
				"goal_id integer," +
				"goal_kra_id integer," +
				"emp_id integer," +
				"amt_percentage double precision," +
				"amt_percentage_type character varying," +
				"user_type_id integer," +
				"added_by integer," +
				"entry_date date," +
				"measure_type character varying," +
				"CONSTRAINT goal_kras_amt_details_pkey PRIMARY KEY (goal_kra_amt_id)" +
				");" +
				"ALTER TABLE goal_kras_amt_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE goal_kras" +
				"(" +
				"goal_kra_id serial NOT NULL," +
				"goal_id integer," +
				"entry_date date," +
				"effective_date date," +
				"is_approved boolean," +
				"approved_by integer," +
				"kra_order integer," +
				"kra_description text," +
				"goal_type integer," +
				"CONSTRAINT goal_kras_pkey PRIMARY KEY (goal_kra_id)" +
				");" +
				"ALTER TABLE goal_kras OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE goal_details" +
				"(" +
				"goal_id serial NOT NULL," +
				"goal_type integer," +
				"goal_parent_id integer," +
				"goal_title character varying," +
				"goal_objective text," +
				"goal_description text," +
				"goal_attribute integer," +
				"measure_type character varying," +
				"measure_currency_value double precision," +
				"measure_currency_id integer," +
				"measure_effort_days double precision," +
				"measure_effort_hrs double precision," +
				"measure_type1 character varying," +
				"measure_kra text," +
				"measure_currency_value1 double precision," +
				"measure_currency1_id integer," +
				"due_date date," +
				"is_feedback boolean," +
				"orientation_id integer," +
				"weightage double precision," +
				"emp_ids text," +
				"entry_date date," +
				"user_id integer," +
				"is_measure_kra boolean," +
				"measure_kra_days double precision," +
				"measure_kra_hrs double precision," +
				"grade_id text," +
				"level_id text," +
				"kra character varying," +
				"frequency integer," +
				"weekday character varying," +
				"frequency_day character varying," +
				"frequency_month character varying," +
				"priority integer," +
				"measure_val double precision," +
				"measure_desc text," +
				"effective_date timestamp without time zone," +
				"goal_creater_id integer DEFAULT 0," +
				"responsible_emp_id integer DEFAULT 0," +
				"goal_creater_type text," +
				"goalalign_with_teamgoal boolean DEFAULT false," +
				"goal_element integer DEFAULT 0," +
				"org_id integer DEFAULT 0," +
				"CONSTRAINT goal_details_pkey PRIMARY KEY (goal_id)" +
				");" +
				"ALTER TABLE goal_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE appraisal_scorecard_details" +
				"(" +
				"scorecard_id serial NOT NULL," +
				"scorecard_section_name character varying," +
				"scorecard_description text," +
				"scorecard_weightage character varying," +
				"level_id integer," +
				"appraisal_id integer," +
				"appraisal_attribute integer," +
				"CONSTRAINT appraisal_scorecard_details_pkey PRIMARY KEY (scorecard_id)" +
				");" +
				"ALTER TABLE appraisal_scorecard_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE appraisal_question_details" +
				"(" +
				"appraisal_question_details_id serial NOT NULL," +
				"question_id integer," +
				"weightage double precision," +
				"appraisal_id integer," +
				"plan_id integer," +
				"measure_id integer," +
				"attribute_id integer," +
				"other_short_description text," +
				"appraisal_level_id integer," +
				"scorecard_id integer," +
				"other_id integer," +
				"answer_type integer," +
				"CONSTRAINT appraisal_question_details_pkey PRIMARY KEY (appraisal_question_details_id)" +
				");" +
				"ALTER TABLE appraisal_question_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE appraisal_question_answer" +
				"(" +
				"emp_id integer," +
				"answer text," +
				"appraisal_id integer," +
				"question_id integer," +
				"user_id integer," +
				"user_type_id integer," +
				"attempted_on date," +
				"remark text," +
				"marks double precision," +
				"weightage double precision," +
				"appraisal_level_id integer," +
				"scorecard_id integer," +
				"appraisal_question_answer_id serial NOT NULL," +
				"other_id integer," +
				"appraisal_attribute integer," +
				"appraisal_question_details_id integer," +
				"read_status integer DEFAULT 0," +
				"read_status_comment text," +
				"section_id integer," +
				"answers_comment character varying," +
				"CONSTRAINT appraisal_question_answer_pkey PRIMARY KEY (appraisal_question_answer_id)" +
				");" +
				"ALTER TABLE appraisal_question_answer OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE appraisal_plan" +
				"(" +
				"appraisal_id integer," +
				"frequency_type character varying(100)," +
				"from_date date," +
				"to_date date," +
				"plan_type integer," +
				"appraisal_plan_id serial NOT NULL," +
				"attribute_id integer," +
				"description text," +
				"monthfrom date," +
				"monthto date," +
				"weekday character varying," +
				"CONSTRAINT appraisal_plan_pkey PRIMARY KEY (appraisal_plan_id)" +
				");" +
				"ALTER TABLE appraisal_plan OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE appraisal_other_question_type_details" +
				"(" +
				"othe_question_type_id serial NOT NULL," +
				"other_answer_type character varying," +
				"other_question_type character varying," +
				"is_weightage boolean," +
				"appraisal_id integer," +
				"level_id integer," +
				"CONSTRAINT appraisal_other_question_type_details_pkey PRIMARY KEY (othe_question_type_id)" +
				");" +
				"ALTER TABLE appraisal_other_question_type_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE appraisal_objective_details" +
				"(" +
				"objective_id serial NOT NULL," +
				"goal_id integer," +
				"objective_section_name character varying," +
				"objective_description text," +
				"objective_weightage character varying," +
				"appraisal_id integer," +
				"CONSTRAINT appraisal_objective_details_pkey PRIMARY KEY (objective_id)" +
				");" +
				"ALTER TABLE appraisal_objective_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE appraisal_measure_details" +
				"(" +
				"measure_id serial NOT NULL," +
				"measure_section_name character varying," +
				"measure_description text," +
				"appraisal_id integer," +
				"weightage text," +
				"scorecard_id integer," +
				"goal_id integer," +
				"objective_id integer," +
				"CONSTRAINT appraisal_measure_details_pkey PRIMARY KEY (measure_id)" +
				");" +
				"ALTER TABLE appraisal_measure_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE appraisal_level_details" +
				"(" +
				"appraisal_level_id serial NOT NULL," +
				"level_title character varying(200)," +
				"short_description text," +
				"long_description text," +
				"appraisal_id integer," +
				"attribute_id integer," +
				"scorecard_type integer," +
				"appraisal_system integer," +
				"main_level_id integer," +
				"subsection_weightage character varying," +
				"added_by integer," +
				"entry_date timestamp without time zone," +
				"CONSTRAINT appraisal_level_details_pkey PRIMARY KEY (appraisal_level_id)" +
				");" +
				"ALTER TABLE appraisal_level_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE appraisal_goal_details" +
				"(" +
				"goal_id serial NOT NULL," +
				"scorecard_id integer," +
				"goal_section_name character varying," +
				"goal_description text," +
				"goal_weightage character varying," +
				"appraisal_id integer," +
				"CONSTRAINT appraisal_goal_details_pkey PRIMARY KEY (goal_id)" +
				");" +
				"ALTER TABLE appraisal_goal_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE appraisal_approval" +
				"(" +
				"appraisal_approval_id serial NOT NULL," +
				"emp_id integer," +
				"user_id integer," +
				"user_type_id integer," +
				"status boolean," +
				"appraisal_id integer," +
				"CONSTRAINT appraisal_approval_pkey PRIMARY KEY (appraisal_approval_id)" +
				");" +
				"ALTER TABLE appraisal_approval OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE appraisal_element(" +
				"appraisal_element_id serial NOT NULL," +
				"appraisal_element_name text," +
				"CONSTRAINT appraisal_element_pkey PRIMARY KEY (appraisal_element_id )" +
				"); ALTER TABLE appraisal_element OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE appraisal_element_attribute(" +
				"appraisal_element integer," +
				"appraisal_attribute integer," +
				"status boolean," +
				"appraisal_element_attribute_id serial NOT NULL," +
				"CONSTRAINT appraisal_element_attribute_pkey PRIMARY KEY (appraisal_element_attribute_id )" +
				");ALTER TABLE appraisal_element_attribute OWNER TO postgres;");
		alQueries.add("CREATE TABLE appraisal_attribute(" +
				"arribute_id serial NOT NULL," +
				"attribute_name text," +
				"status boolean," +
				"level_id integer," +
				"threshhold double precision," +
				"attribute_desc text," +
				"attribute_info text," +
				"CONSTRAINT appraisal_attribute_pkey PRIMARY KEY (arribute_id )" +
				"); ALTER TABLE appraisal_attribute OWNER TO postgres;");

//		alQueries.add("insert into apparisal_orientation(orientation_name)values('90')");
//		alQueries.add("insert into apparisal_orientation(orientation_name)values('180')");
//		alQueries.add("insert into apparisal_orientation(orientation_name)values('270')");
//		alQueries.add("insert into apparisal_orientation(orientation_name)values('360')");
//		alQueries.add("insert into apparisal_orientation(orientation_name)values('Other')");
		
//		alQueries.add("insert into orientation_details(orientation_id,member_id)values(3,2)");
//		alQueries.add("insert into orientation_details(orientation_id,member_id)values(3,7)");
//		alQueries.add("insert into orientation_details(orientation_id,member_id)values(3,4)");
//		alQueries.add("insert into orientation_details(orientation_id,member_id)values(3,3)");
//		alQueries.add("insert into orientation_details(orientation_id,member_id)values(2,2)");
//		alQueries.add("insert into orientation_details(orientation_id,member_id)values(2,7)");
//		alQueries.add("insert into orientation_details(orientation_id,member_id)values(2,3)");
//		alQueries.add("insert into orientation_details(orientation_id,member_id)values(4,2)");
//		alQueries.add("insert into orientation_details(orientation_id,member_id)values(4,7)");
//		alQueries.add("insert into orientation_details(orientation_id,member_id)values(4,4)");
//		alQueries.add("insert into orientation_details(orientation_id,member_id)values(4,5)");
//		alQueries.add("insert into orientation_details(orientation_id,member_id)values(4,3)");
//		alQueries.add("insert into orientation_details(orientation_id,member_id)values(1,7)");
//		alQueries.add("insert into orientation_details(orientation_id,member_id)values(1,3)");
//		alQueries.add("insert into orientation_details(orientation_id,member_id)values(5,10)");
//		alQueries.add("insert into orientation_details(orientation_id,member_id)values(5,3)");
		
//		alQueries.add("insert into orientation_member(member_name,status,weightage,member_id)values('Manager',true,1,2)");
//		alQueries.add("insert into orientation_member(member_name,status,weightage,member_id)values('Self',true,5,3)");
//		alQueries.add("insert into orientation_member(member_name,status,weightage,member_id)values('Peer',true,3,4)");
//		alQueries.add("insert into orientation_member(member_name,status,weightage,member_id)values('Client',true,4,5)");
//		alQueries.add("insert into orientation_member(member_name,status,weightage,member_id)values('Sub-ordinate',true,6,6)");
//		alQueries.add("insert into orientation_member(member_name,status,weightage,member_id)values('HR',true,2,7)");
//		alQueries.add("insert into orientation_member(member_name,status,weightage,member_id)values('GroupHead',true,8,8)");
//		alQueries.add("insert into orientation_member(member_name,status,weightage,member_id)values('Vendor',true,7,9)");
//		alQueries.add("insert into orientation_member(member_name,status,weightage,member_id)values('Anyone',true,9,10)");
		
		
//		alQueries.add("insert into appraisal_answer_type(appraisal_answer_type_name)values('With Remark')");
//		alQueries.add("insert into appraisal_answer_type(appraisal_answer_type_name)values('Without Remark')");
//		alQueries.add("insert into appraisal_answer_type(appraisal_answer_type_name)values('Score')");
//		alQueries.add("insert into appraisal_answer_type(appraisal_answer_type_name)values('Grade with Excellency')");
//		alQueries.add("insert into appraisal_answer_type(appraisal_answer_type_name)values('Yes/No')");
//		alQueries.add("insert into appraisal_answer_type(appraisal_answer_type_name)values('True/False')");
//		alQueries.add("insert into appraisal_answer_type(appraisal_answer_type_name)values('Single Open with Marks')");
//		alQueries.add("insert into appraisal_answer_type(appraisal_answer_type_name)values('Single Choice')");
//		alQueries.add("insert into appraisal_answer_type(appraisal_answer_type_name)values('Multiple Choice')");
//		alQueries.add("insert into appraisal_answer_type(appraisal_answer_type_name)values('Multiple Open')");
//		alQueries.add("insert into appraisal_answer_type(appraisal_answer_type_name)values('Grade with Rating')");
//		alQueries.add("insert into appraisal_answer_type(appraisal_answer_type_name)values('Single Open without Marks')");

//		alQueries.add("insert into appraisal_answer_type_sub(answer_type_id,label,score,score_label)values(5,'Yes/No','1','Yes')");
//		alQueries.add("insert into appraisal_answer_type_sub(answer_type_id,label,score,score_label)values(5,'Yes/No','0','No')");
//		alQueries.add("insert into appraisal_answer_type_sub(answer_type_id,label,score,score_label)values(6,'True/False','1','True')");
//		alQueries.add("insert into appraisal_answer_type_sub(answer_type_id,label,score,score_label)values(6,'True/False','0','False')");
//		alQueries.add("insert into appraisal_answer_type_sub(answer_type_id,label,score,score_label)values(4,'Grade with Excellency','100','Excellent')");
//		alQueries.add("insert into appraisal_answer_type_sub(answer_type_id,label,score,score_label)values(4,'Grade with Excellency','80','Very Good')");
//		alQueries.add("insert into appraisal_answer_type_sub(answer_type_id,label,score,score_label)values(4,'Grade with Excellency','60','Average')");
//		alQueries.add("insert into appraisal_answer_type_sub(answer_type_id,label,score,score_label)values(4,'Grade with Excellency','40','Good')");
//		alQueries.add("insert into appraisal_answer_type_sub(answer_type_id,label,score,score_label)values(4,'Grade with Excellency','20','Poor')");
		/*alQueries.add("insert into appraisal_answer_type_sub(answer_type_id,label,score,score_label)values(4,'Grade with Excellency','0','Bad')");*/
//		
//		alQueries.add("insert into appraisal_attribute(attribute_name,status,level_id,threshhold,attribute_desc)values('Integrity',true,71,75,'Completely internalises the organizations focus of doing business and acts as a role model inspiring others and sets example.')");
//		alQueries.add("insert into appraisal_attribute(attribute_name,status,level_id,threshhold,attribute_desc)values('Personal Touch',true,71,75,'Shows genuine concern and respect and are sensitive to employees needs. Is committed to supporting and protecting staff.')");
//		alQueries.add("insert into appraisal_attribute(attribute_name,status,level_id,threshhold,attribute_desc)values('People Management',true,70,75,'Seeks to improve and optimize man-management processes and the working environment..')");
//		alQueries.add("insert into appraisal_attribute(attribute_name,status,level_id,threshhold,attribute_desc)values('Work Process Orientation',true,70,75,'Plans for effective utilization of resources, optimizes the workflow and ensures effective integration and alignment with other related processes. Sets goals and objectives, monitors progress and responds rapidly when required.')");
		
//		alQueries.add("insert into appraisal_element(appraisal_element_name)values('Potential')");
//		alQueries.add("insert into appraisal_element(appraisal_element_name)values('Performance')");
		
//		alQueries.add("insert into appraisal_frequency(frequency_name)values('One Time')");
//		alQueries.add("insert into appraisal_frequency(frequency_name)values('Weekly')");
//		alQueries.add("insert into appraisal_frequency(frequency_name)values('Monthly')");
//		alQueries.add("insert into appraisal_frequency(frequency_name)values('Quaterly')");
//		alQueries.add("insert into appraisal_frequency(frequency_name)values('Half Yearly')");
//		alQueries.add("insert into appraisal_frequency(frequency_name)values('Annually')");
		
		
		
		alQueries.add("CREATE TABLE appraisal_answer_type_sub" +
				"(" +
				"appraisal_answer_type_sub_id serial NOT NULL," +
				"answer_type_id integer," +
				"label text," +
				"score text," +
				"score_label text," +
				"CONSTRAINT appraisal_answer_type_sub_pkey PRIMARY KEY (appraisal_answer_type_sub_id)" +
				");" +
				"ALTER TABLE appraisal_answer_type_sub OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE orientation_member" +
				"(" +
				"orientation_member_id serial NOT NULL," +
				"member_name text," +
				"status boolean," +
				"weightage integer," +
				"member_id integer," +
				"CONSTRAINT orientation_member_pkey PRIMARY KEY (orientation_member_id)" +
				");" +
				"ALTER TABLE orientation_member OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE appraisal_answer_type" +
				"(" +
				"appraisal_answer_type_id serial NOT NULL," +
				"appraisal_answer_type_name text," +
				"CONSTRAINT appraisal_answer_type_pkey PRIMARY KEY (appraisal_answer_type_id)" +
				");" +
				"ALTER TABLE appraisal_answer_type OWNER TO postgres;");

		alQueries.add("CREATE TABLE orientation_details" +
				"(" +
				"orientation_details_id serial NOT NULL," +
				"orientation_id integer," +
				"member_id integer," +
				"CONSTRAINT orientation_details_pkey PRIMARY KEY (orientation_details_id)" +
				");" +
				"ALTER TABLE orientation_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE appraisal_details" +
				"(" +
				"appraisal_details_id serial NOT NULL," +
				"appraisal_name text," +
				"oriented_type character varying(50)," +
				"employee_id text," +
				"level_id text," +
				"desig_id text," +
				"grade_id text," +
				"frequency character varying(100)," +
				"supervisor_id text," +
				"peer_ids text," +
				"self_ids text," +
				"emp_status character varying," +
				"appraisal_type character varying," +
				"added_by integer," +
				"entry_date date," +
				"wlocation_id text," +
				"department_id text," +
				"from_date date," +
				"to_date date," +
				"monthfrom date," +
				"monthto date," +
				"weekday character varying," +
				"plan_type integer," +
				"hr_ids text," +
				"usertype_member text," +
				"appraisal_description text," +
				"is_publish boolean," +
				"appraisal_day character varying," +
				"appraisal_month character varying," +
				"template_id integer," +
				"appraisal_instruction text," +
				"finalization_ids character varying," +
				"my_review_status integer DEFAULT 0," +
				"other_ids character varying," +
				"CONSTRAINT appraisal_details_pkey PRIMARY KEY (appraisal_details_id)" +
				");" +
				"ALTER TABLE appraisal_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE apparisal_orientation" +
				"(" +
				"apparisal_orientation_id serial NOT NULL," +
				"orientation_name text," +
				"CONSTRAINT apparisal_orientation_pkey PRIMARY KEY (apparisal_orientation_id)" +
				");" +
				"ALTER TABLE apparisal_orientation OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE appraisal_final_sattlement" +
				"(" +
				"appraisal_final_sattlement_id serial NOT NULL," +
				"emp_id integer," +
				"appraisal_id integer," +
				"user_id integer," +
				"if_approved boolean," +
				"sattlement_comment text," +
				"_date date," +
				"activity_id1 integer," +
				"activity_ids character varying," +
				"CONSTRAINT appraisal_final_sattlement_pkey PRIMARY KEY (appraisal_final_sattlement_id)" +
				");" +
				"ALTER TABLE appraisal_final_sattlement OWNER TO postgres;");

		
		alQueries.add("CREATE TABLE question_bank" +
				"(" +
				"question_bank_id serial NOT NULL," +
				"question_text text," +
				"option_a text," +
				"option_b text," +
				"option_c text," +
				"option_d text," +
				"correct_ans text," +
				"is_add boolean," +
				"question_type integer," +
				"CONSTRAINT question_bank_pkey PRIMARY KEY (question_bank_id)" +
				");" +
				"ALTER TABLE question_bank OWNER TO postgres;");

		
		alQueries.add("CREATE TABLE resource_planner_details(" +
				"resource_planner_id serial NOT NULL," +
				"designation_id integer," +
				"financial_year_from date," +
				"financial_year_to date," +
				"rmonth integer," +
				"ryear integer," +
				"added_by integer," +
				"wlocation_id integer," +
				"resource_requirement integer DEFAULT 0," +
				"added_date date," +
				"CONSTRAINT resource_planner_details_pkey PRIMARY KEY (resource_planner_id));" +
				"ALTER TABLE resource_planner_details OWNER TO postgres;");
//		
		alQueries.add("CREATE TABLE recruitment_details(" +
				"recruitment_id serial NOT NULL," +
				"designation_id integer," +
				"grade_id integer," +
				"no_position integer," +
				"effective_date date," +
				"comments text," +
				"skills text," +
				"job_description text," +
				"min_exp double precision," +
				"max_exp double precision," +
				"min_education character varying(50)," +
				"status integer DEFAULT 0," +
				"job_code character varying," +
				"job_approval_status integer DEFAULT 0," +
				"wlocation integer," +
				"candidate_profile text," +
				"additional_info text," +
				"entry_date date," +
				"added_by integer," +
				"approved_by integer," +
				"approved_date date," +
				"req_deny_reason text," +
				"job_deny_reason text," +
				"services integer," +
				"dept_id integer," +
				"level_id integer," +
				"emp_mail_status integer DEFAULT 0," +
				"close_job_status boolean DEFAULT false," +
				"priority_job_int integer," +
				"custum_designation character varying(25)," +
				"target_deadline date," +
				"panel_location text," +
				"panel_level text," +
				"panel_designation text," +
				"panel_grade text," +
				"panel_employee_id text," +
				"org_id integer," +
				"ideal_candidate text," +
				"existing_emp_count integer," +
				"job_profile_updated_by integer," +
				"job_profile_updated_date date," +
				"publish_profile integer DEFAULT 0," +
				"custum_grade text," +
				"requirement_status text," +
				"CONSTRAINT recruitment_details_pkey PRIMARY KEY (recruitment_id)" +
				");ALTER TABLE recruitment_details OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE candidate_prev_employment" +
				"(" +
				"company_id serial NOT NULL," +
				"company_name character varying(50)," +
				"company_location character varying(50)," +
				"company_city character varying(50)," +
				"company_contact_no character varying(50)," +
				"reporting_to character varying(50)," +
				"from_date date," +
				"to_date date," +
				"designation character varying(50)," +
				"responsibilities character varying(100)," +
				"skills character varying(50)," +
				"emp_id integer," +
				"company_country character varying(50)," +
				"company_state character varying(50)," +
				"CONSTRAINT candidate_prev_employment_pkey PRIMARY KEY (company_id)" +
				");ALTER TABLE candidate_prev_employment OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE appraisal_frequency" +
				"(" +
				"appraisal_frequency_id serial NOT NULL," +
				"frequency_name text," +
				"CONSTRAINT appraisal_frequency_pkey PRIMARY KEY (appraisal_frequency_id)" +
				");ALTER TABLE appraisal_frequency OWNER TO postgres;");
		
		alQueries.add("CREATE TABLE candidate_activity_details" +
				"(" +
				"candi_activity_id serial NOT NULL," +
				"reason text," +
				"entry_date date," +
				"user_id integer," +
				"candi_id integer," +
				"activity_name character varying," +
				"activity_status integer," +
				"recruitment_id integer," +
				"round_id integer," +
				"activity_id integer," +
				"CONSTRAINT candidate_activity_details_pkey PRIMARY KEY (candi_activity_id)" +
				");" +
				"ALTER TABLE candidate_activity_details OWNER TO postgres;");
		
		
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN job_approval_date timestamp without time zone;");
		
		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN my_review_status integer;");
		alQueries.add("ALTER TABLE appraisal_details ALTER COLUMN my_review_status SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE goal_details ADD COLUMN goalalign_with_teamgoal boolean;");
		alQueries.add("update appraisal_frequency set frequency_name = 'Half Yearly' where frequency_name = 'HalfYearly'");
		
		alQueries.add("ALTER TABLE goal_details ADD COLUMN goal_creater_id integer;");
		alQueries.add("ALTER TABLE goal_details ALTER COLUMN goal_creater_id SET DEFAULT 0;");
		alQueries.add("ALTER TABLE goal_details ADD COLUMN responsible_emp_id integer;");
		alQueries.add("ALTER TABLE goal_details ALTER COLUMN responsible_emp_id SET DEFAULT 0;");
		alQueries.add("ALTER TABLE goal_details ADD COLUMN goal_creater_type text;");
		
		alQueries.add("ALTER TABLE target_details ADD COLUMN emp_amt_percentage double precision;");
		
		alQueries.add("ALTER TABLE target_details ADD COLUMN approve_status integer;");
		alQueries.add("ALTER TABLE target_details ALTER COLUMN approve_status SET DEFAULT 0;");

		alQueries.add("ALTER TABLE goal_details ADD COLUMN effective_date timestamp without time zone;");
		
		alQueries.add("ALTER TABLE appraisal_main_level_details ADD COLUMN hr integer;");
		alQueries.add("ALTER TABLE appraisal_main_level_details ALTER COLUMN hr SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE appraisal_main_level_details ADD COLUMN manager integer;");
		alQueries.add("ALTER TABLE appraisal_main_level_details ALTER COLUMN manager SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE appraisal_main_level_details ADD COLUMN peer integer;");
		alQueries.add("ALTER TABLE appraisal_main_level_details ALTER COLUMN peer SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE appraisal_main_level_details ADD COLUMN self integer;");
		alQueries.add("ALTER TABLE appraisal_main_level_details ALTER COLUMN self SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE appraisal_main_level_details ADD COLUMN subordinate integer;");
		alQueries.add("ALTER TABLE appraisal_main_level_details ALTER COLUMN subordinate SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE appraisal_main_level_details ADD COLUMN grouphead integer;");
		alQueries.add("ALTER TABLE appraisal_main_level_details ALTER COLUMN grouphead SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE appraisal_main_level_details ADD COLUMN vendor integer;");
		alQueries.add("ALTER TABLE appraisal_main_level_details ALTER COLUMN vendor SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE appraisal_main_level_details ADD COLUMN client integer;");
		alQueries.add("ALTER TABLE appraisal_main_level_details ALTER COLUMN client SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE appraisal_question_answer ADD COLUMN read_status_comment text;");
		alQueries.add("ALTER TABLE appraisal_question_answer ADD COLUMN read_status integer;");
		alQueries.add("ALTER TABLE appraisal_question_answer ALTER COLUMN read_status SET DEFAULT 0;");
		alQueries.add("update appraisal_question_answer set read_status = 0 where read_status is null");
		
		alQueries.add("ALTER TABLE candidate_medical_details ADD COLUMN filepath text;");
		alQueries.add("ALTER TABLE candidate_personal_details ADD COLUMN renegotiate_remark character varying;");
		alQueries.add("ALTER TABLE candidate_personal_details ADD COLUMN offer_accept_remark character varying;");
		
		alQueries.add("CREATE TABLE panel_interview_details ( panel_interview_id serial NOT NULL , " +
				"recruitment_id integer, " +
				"panel_emp_id integer, " +
				"round_id integer, " +
				"CONSTRAINT panel_interview_details_pkey PRIMARY KEY (panel_interview_id));" +
				"ALTER TABLE panel_interview_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE candidate_personal_details ADD COLUMN send_notification_status integer;");
		
		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN finalization_ids character varying;");
		alQueries.add("ALTER TABLE appraisal_main_level_details ADD COLUMN added_by integer;");
		alQueries.add("ALTER TABLE appraisal_main_level_details ADD COLUMN entry_date timestamp without time zone;");
		alQueries.add("ALTER TABLE appraisal_level_details ADD COLUMN added_by integer;");
		alQueries.add("ALTER TABLE appraisal_level_details ADD COLUMN entry_date timestamp without time zone;");
		alQueries.add("ALTER TABLE appraisal_question_answer ADD COLUMN answers_comment character varying;");
		
		alQueries.add("ALTER TABLE appraisal_question_answer ADD COLUMN section_id integer;");
		
		alQueries.add("ALTER TABLE appraisal_main_level_details ADD COLUMN section_weightage character varying;");
		alQueries.add("ALTER TABLE appraisal_level_details ADD COLUMN subsection_weightage character varying;");
		
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN requirement_status text;");
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN custum_grade text;");
		alQueries.add("ALTER TABLE goal_details ADD COLUMN measure_val double precision;");
		alQueries.add("ALTER TABLE goal_details ADD COLUMN measure_desc text;");
		alQueries.add("ALTER TABLE kra_details ADD COLUMN goal_id integer;");
		
		alQueries.add("ALTER TABLE candidate_documents_details ADD COLUMN added_by integer;");
		alQueries.add("ALTER TABLE candidate_documents_details ALTER COLUMN added_by SET DEFAULT 0;");
		alQueries.add("ALTER TABLE candidate_documents_details ADD COLUMN entry_date timestamp without time zone;");
		
		alQueries.add("ALTER TABLE goal_details ADD COLUMN goal_element integer;");
		alQueries.add("ALTER TABLE goal_details ALTER COLUMN goal_element SET DEFAULT 0;");
		alQueries.add("ALTER TABLE educational_details ADD COLUMN weightage integer;");
		
		alQueries.add("ALTER TABLE appraisal_final_sattlement ADD COLUMN activity_ids character varying;");
		alQueries.add("ALTER TABLE goal_details ADD COLUMN org_id integer;");
		alQueries.add("ALTER TABLE goal_details ALTER COLUMN org_id SET DEFAULT 0;");
		
		
		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN other_ids character varying;");
		
		alQueries.add("ALTER TABLE target_details ADD COLUMN target_remark character varying;");
		
		alQueries.add("ALTER TABLE target_details ADD COLUMN entry_time time without time zone;");
		
		alQueries.add("ALTER TABLE candidate_salary_details ADD COLUMN recruitment_id integer;");
		alQueries.add("ALTER TABLE panel_interview_details ADD COLUMN candidate_id integer;");
		
		alQueries.add("ALTER TABLE training_schedule ADD COLUMN training_day character varying;");
		alQueries.add("ALTER TABLE training_schedule ADD COLUMN training_month character varying;");
		alQueries.add("ALTER TABLE training_schedule ADD COLUMN training_weekday character varying;");
		
		alQueries.add("ALTER TABLE training_session ADD COLUMN session_training_day character varying;");
		alQueries.add("ALTER TABLE training_session ADD COLUMN session_trainig_month character varying;");
		
		alQueries.add("ALTER TABLE trainer_medical_details ADD COLUMN filepath text;");
		alQueries.add("ALTER TABLE trainer_documents_details ADD COLUMN added_by integer;");
		alQueries.add("ALTER TABLE trainer_documents_details ADD COLUMN entry_date date;");
		
		alQueries.add("ALTER TABLE training_session ADD COLUMN schedule_type integer;");
		alQueries.add("ALTER TABLE training_session ADD COLUMN week_days character varying;");
		alQueries.add("ALTER TABLE training_schedule ADD COLUMN day_schedule_type integer;");
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN request_updated_by integer;");
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN request_updated_date date;");
		
		alQueries.add("ALTER TABLE training_schedule ADD COLUMN training_duration_type character varying;");

		alQueries.add("ALTER TABLE question_bank ADD COLUMN kra_id integer;");
		alQueries.add("ALTER TABLE appraisal_question_details ADD COLUMN kra_id integer;");
		
		alQueries.add("ALTER TABLE appraisal_question_details ADD COLUMN goal_kra_target_id integer;");
		alQueries.add("ALTER TABLE appraisal_question_details ADD COLUMN app_system_type integer;");
		alQueries.add("ALTER TABLE question_bank ADD COLUMN goal_kra_target_id integer;");

		alQueries.add("ALTER TABLE question_bank ADD COLUMN app_system_type integer;");
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN assessment_threshhold integer;");

		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN essential_qualification character varying;");
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN alternate_qualification character varying;");
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN essential_skills character varying;");
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN consultant_ids character varying;");
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN reportto_type character varying;");
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN source_of_requirement character varying;");
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN advertisement_media character varying;");
		
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN type_of_employment integer;");
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN sex character varying;");
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN age character varying;");
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN vacancy_type integer;");
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN give_justification character varying;");
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN replacement_person_ids character varying;");
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN reporting_to_person_ids character varying;");
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN temp_casual_give_jastification character varying;");
		
		alQueries.add("ALTER TABLE course_subchapter_details ADD COLUMN ref_course_subchapter_id integer;");
		alQueries.add("ALTER TABLE course_content_details ADD COLUMN ref_course_content_id integer;");
		alQueries.add("ALTER TABLE course_chapter_details ADD COLUMN ref_course_chapter_id integer;");
		alQueries.add("ALTER TABLE course_assessment_details ADD COLUMN ref_course_assessment_id integer;");
		alQueries.add("ALTER TABLE course_details ADD COLUMN ref_course_id integer;");
		alQueries.add("ALTER TABLE course_details ADD COLUMN parent_course_id integer;");
		
//		alQueries.add("ALTER TABLE appraisal_level_details ADD COLUMN hr integer;");
//		alQueries.add("ALTER TABLE appraisal_level_details ADD COLUMN manager integer;");
//		alQueries.add("ALTER TABLE appraisal_level_details ADD COLUMN peer integer;");
//		alQueries.add("ALTER TABLE appraisal_level_details ADD COLUMN self integer;");
//		alQueries.add("ALTER TABLE appraisal_level_details ADD COLUMN subordinate integer;");
//		alQueries.add("ALTER TABLE appraisal_level_details ADD COLUMN grouphead integer;");
//		alQueries.add("ALTER TABLE appraisal_level_details ADD COLUMN vendor integer;");
//		alQueries.add("ALTER TABLE appraisal_level_details ADD COLUMN client integer;");

		alQueries.add("ALTER TABLE appraisal_question_details ADD COLUMN answer_type integer;");
		
		alQueries.add("ALTER TABLE candidate_interview_availability ADD COLUMN islive integer;");
		alQueries.add("ALTER TABLE candidate_interview_availability ALTER COLUMN islive SET DEFAULT 0;");
		alQueries.add("ALTER TABLE candidate_interview_availability ADD COLUMN recruitment_id integer;");
		alQueries.add("ALTER TABLE candidate_interview_availability ADD COLUMN rejected_by integer;");
		
		alQueries.add("ALTER TABLE candidate_interview_panel ADD COLUMN is_interview_taken boolean;");
		alQueries.add("ALTER TABLE candidate_interview_panel ALTER COLUMN is_interview_taken SET NOT NULL;");
		alQueries.add("ALTER TABLE candidate_interview_panel ALTER COLUMN is_interview_taken SET DEFAULT false;");

		alQueries.add("ALTER TABLE document_comm_details ADD COLUMN org_id integer;");
		
		alQueries.add("ALTER TABLE training_question_answer ADD COLUMN learning_plan_id integer;");
		
		alQueries.add("ALTER TABLE training_status ADD COLUMN learning_plan_id integer;");
		alQueries.add("ALTER TABLE learning_plan_details ADD COLUMN certificate_id integer;");
		alQueries.add("ALTER TABLE assessment_details ADD COLUMN assessment_take_attempt integer;");
		alQueries.add("ALTER TABLE assessment_details ALTER COLUMN assessment_take_attempt SET DEFAULT 1;");
		alQueries.add("ALTER TABLE assessment_take_attempt_details ADD COLUMN emp_id integer;");
		alQueries.add("ALTER TABLE training_question_answer ADD COLUMN learning_plan_id integer;");
		alQueries.add("ALTER TABLE training_status RENAME COLUMN learning_id TO training_id;");
		alQueries.add("ALTER TABLE learning_plan_details ADD COLUMN skills character varying;");
		alQueries.add("ALTER TABLE assessment_section_details ADD COLUMN ref_assessment_section_id integer;");
		alQueries.add("ALTER TABLE assessment_question_details ADD COLUMN answer_type integer;");
		
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN previous_empment_avg_exp double precision;");
		alQueries.add("ALTER TABLE successionplan_criteria_details ADD COLUMN org_id integer;");
		
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN previous_empment_avg_exp double precision;");
		
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN offer_accept_date date;");
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN candidate_final_status_date date;");
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN application_status_date date;");
		
		alQueries.add("ALTER TABLE assessment_details ADD COLUMN assessment_time_duration character varying;");
		alQueries.add("ALTER TABLE assessment_details ADD COLUMN time_to_attempt_assessment integer;");
		
		alQueries.add("ALTER TABLE candidate_interview_panel ADD COLUMN panel_user_id integer;");
		alQueries.add("ALTER TABLE trainer_personal_details ADD COLUMN trainer_code character varying(45);");
		
		alQueries.add("ALTER TABLE panel_interview_details ADD COLUMN round_name character varying;");
		
		alQueries.add("ALTER TABLE assessment_details ADD COLUMN marks_grade_standard integer;");
		alQueries.add("ALTER TABLE assessment_details ADD COLUMN marks_grade_type integer;");
		alQueries.add("ALTER TABLE training_plan ADD COLUMN marks_grade_standard integer;");
		alQueries.add("ALTER TABLE training_plan ADD COLUMN marks_grade_type integer;");
		
		alQueries.add("ALTER TABLE training_mark_grade_type ADD COLUMN max_value double precision;");
		alQueries.add("ALTER TABLE training_mark_grade_type ADD COLUMN min_value double precision;");
		
		alQueries.add("ALTER TABLE course_details ADD COLUMN root_course_id integer;");
		alQueries.add("ALTER TABLE assessment_details ADD COLUMN root_assessment_id integer;");
		
		alQueries.add("ALTER TABLE training_certificate ADD COLUMN added_by integer;");
		alQueries.add("ALTER TABLE training_certificate ADD COLUMN entry_date date;");
		alQueries.add("ALTER TABLE training_certificate ADD COLUMN updated_by integer;");
		alQueries.add("ALTER TABLE training_certificate ADD COLUMN update_date date;");
		
		alQueries.add("ALTER TABLE training_plan ADD COLUMN added_by integer;");
		alQueries.add("ALTER TABLE training_plan ADD COLUMN entry_date date;");
		alQueries.add("ALTER TABLE training_plan ADD COLUMN updated_by integer;");
		alQueries.add("ALTER TABLE training_plan ADD COLUMN update_date date;");
		
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN my_personal_goals integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN my_targets integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN requirement_request integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN requirement_approval integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN jobcode_request integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN jobcode_approval integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN my_reviews integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN hr_reviews integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN manager_reviews integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN peer_reviews integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN my_goals integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN my_kras integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN my_learning_plans integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN reviews integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN interviews integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN new_joinees integer;");
		
		alQueries.add("ALTER TABLE training_schedule_details ADD COLUMN long_description character varying;");
		alQueries.add("ALTER TABLE learning_plan_details ALTER COLUMN is_publish SET DEFAULT false;");

		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN remove_from_successionplan integer;");
		alQueries.add("ALTER TABLE employee_official_details ALTER COLUMN remove_from_successionplan SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN remove_from_successionplan integer;");
		alQueries.add("ALTER TABLE employee_official_details ALTER COLUMN remove_from_successionplan SET DEFAULT 0;");
		alQueries.add("ALTER TABLE course_content_details ADD COLUMN content_title character varying;");
		alQueries.add("ALTER TABLE course_content_details ADD COLUMN content_url character varying;");
		
		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN publish_expire_status integer;");
		alQueries.add("ALTER TABLE appraisal_details ALTER COLUMN publish_expire_status SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE learning_plan_details ADD COLUMN depart_id character varying;");
		alQueries.add("ALTER TABLE learning_plan_details ADD COLUMN service_id character varying;");
		
		alQueries.add("ALTER TABLE training_gap_details ADD COLUMN learning_id integer;" +
				"ALTER TABLE training_gap_details ADD COLUMN learning_attribute_ids character varying;" +
				"ALTER TABLE training_gap_details ADD COLUMN learning_skill_ids character varying;");
		
		alQueries.add("ALTER TABLE education_details ADD COLUMN education_id integer;" +
				"ALTER TABLE skills_description ADD COLUMN skill_id integer;");
		alQueries.add("ALTER TABLE candidate_education_details ADD COLUMN education_id integer;" +
				"ALTER TABLE candidate_skills_description ADD COLUMN skill_id integer;");
		
		alQueries.add("ALTER TABLE trainer_skills_description ADD COLUMN skill_id integer;" +
				"ALTER TABLE trainer_education_details ADD COLUMN education_id integer;");
		
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN hr_learning_gaps integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN manager_learning_gaps integer;");
		
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN my_interviews_scheduled integer;" +
				"ALTER TABLE user_alerts ADD COLUMN candidate_finalization integer;" +
				"ALTER TABLE user_alerts ADD COLUMN candidate_offer_accept_reject integer;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN hr_learning_finalization integer;" +
				"ALTER TABLE user_alerts ADD COLUMN review_finalization integer;" +
				"ALTER TABLE user_alerts ADD COLUMN manager_goals integer;");
		
		alQueries.add("ALTER TABLE document_collateral ADD COLUMN text_align character varying;");
		
		alQueries.add("ALTER TABLE document_activities ADD COLUMN candi_id integer;");
		
		alQueries.add("ALTER TABLE work_location_info ADD COLUMN wlocation_weeknos1 character varying;" +
				"ALTER TABLE work_location_info ADD COLUMN wlocation_weeknos2 character varying;" +
				"ALTER TABLE work_location_info ADD COLUMN wlocation_start_time_halfday1 time without time zone;" +
				"ALTER TABLE work_location_info ADD COLUMN wlocation_end_time_halfday1 time without time zone;" +
				"ALTER TABLE work_location_info ADD COLUMN wlocation_start_time_halfday2 time without time zone;" +
				"ALTER TABLE work_location_info ADD COLUMN wlocation_end_time_halfday2 time without time zone;");
		
		alQueries.add("ALTER TABLE level_details ADD COLUMN wlocation_weeklyoff1 character varying;" +
				"ALTER TABLE level_details ADD COLUMN wlocation_weeklyofftype1 character varying;" +
				"ALTER TABLE level_details ADD COLUMN wlocation_weeklyoff2 character varying;" +
				"ALTER TABLE level_details ADD COLUMN wlocation_weeklyofftype2 character varying;" +
				"ALTER TABLE level_details ADD COLUMN wlocation_weeklyoff3 character varying;" +
				"ALTER TABLE level_details ADD COLUMN wlocation_weeklyofftype3 character varying;" +
				"ALTER TABLE level_details ADD COLUMN wlocation_weeknos3 character varying;");
		
		alQueries.add("ALTER TABLE candidate_personal_details ADD COLUMN emp_mname character varying;" +
				"ALTER TABLE candidate_personal_details ADD COLUMN salutation character varying;" +
				"ALTER TABLE candidate_prev_employment ADD COLUMN report_manager_ph_no character varying;" +
				"ALTER TABLE candidate_prev_employment ADD COLUMN hr_manager character varying;" +
				"ALTER TABLE candidate_prev_employment ADD COLUMN hr_manager_ph_no character varying;");
		
		alQueries.add("ALTER TABLE emp_prev_employment ADD COLUMN report_manager_ph_no character varying;" +
				"ALTER TABLE emp_prev_employment ADD COLUMN hr_manager character varying;" +
				"ALTER TABLE emp_prev_employment ADD COLUMN hr_manager_ph_no character varying;");
		
		alQueries.add("ALTER TABLE candidate_personal_details ADD COLUMN is_emp_live boolean;");
		
		alQueries.add("ALTER TABLE employee_activity_details ADD COLUMN service_id character varying;");
		
		alQueries.add("ALTER TABLE probation_policy ADD COLUMN extend_probation_duration integer;" +
				"ALTER TABLE employee_activity_details ADD COLUMN org_id integer;");
		
		alQueries.add("ALTER TABLE employee_activity_details ADD COLUMN extend_probation_period integer;");
		
		alQueries.add("ALTER TABLE employee_activity_details ADD COLUMN increment_type integer;" +
				"ALTER TABLE employee_activity_details ADD COLUMN increment_percent double precision;");
		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN is_close boolean;" +
				"ALTER TABLE appraisal_details ALTER COLUMN is_close SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN close_reason character varying;" +
				"ALTER TABLE recruitment_details ADD COLUMN close_job_reason character varying;");
		
		alQueries.add("ALTER TABLE learning_plan_details ADD COLUMN is_close boolean;" +
				"ALTER TABLE learning_plan_details ALTER COLUMN is_close SET DEFAULT false;" +
				"ALTER TABLE learning_plan_details ADD COLUMN close_reason character varying;");
		
		alQueries.add("ALTER TABLE goal_details ADD COLUMN is_close boolean;" +
				"ALTER TABLE goal_details ALTER COLUMN is_close SET DEFAULT false;" +
				"ALTER TABLE goal_details ADD COLUMN close_reason character varying;");
		
		alQueries.add("ALTER TABLE certificate_details ADD COLUMN ref_certificate_id integer;" +
				"ALTER TABLE certificate_details ADD COLUMN parent_certificate_id integer;" +
				"ALTER TABLE certificate_details ADD COLUMN root_certificate_id integer;");
		
		alQueries.add("ALTER TABLE designation_kra_details ADD COLUMN element_id integer;" +
				"ALTER TABLE designation_kra_details ADD COLUMN attribute_id integer;");
		
		alQueries.add("ALTER TABLE goal_kras ADD COLUMN element_id integer;" +
				"ALTER TABLE goal_kras ADD COLUMN attribute_id integer;" +
				"ALTER TABLE goal_kras ADD COLUMN emp_ids character varying;" +
				"ALTER TABLE goal_kras ADD COLUMN is_assign boolean;");
		
		alQueries.add("ALTER TABLE goal_kras ADD COLUMN desig_kra_id integer;" +
				"ALTER TABLE goal_kras ADD COLUMN added_by integer;" +
				"ALTER TABLE goal_kras ADD COLUMN update_date date;" +
				"ALTER TABLE goal_kras ADD COLUMN updated_by integer;");
		
		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN is_form16_a boolean;");
		
		alQueries.add("ALTER TABLE emp_off_board ADD COLUMN approved_1_reason character varying;" +
				"ALTER TABLE emp_off_board ADD COLUMN approved_2_reason character varying;");
		
		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN form_type integer;" +
				"ALTER TABLE appraisal_details ALTER COLUMN form_type SET DEFAULT 0;" +
				"ALTER TABLE emp_offboard_status ADD COLUMN emp_status boolean;");
		
		alQueries.add("ALTER TABLE emp_offboard_status ADD COLUMN approved_remark character varying;" +
				"ALTER TABLE emp_offboard_status ADD COLUMN approved_rating integer;");
		
		alQueries.add("ALTER TABLE client_details ADD COLUMN client_type integer;" +
				"ALTER TABLE client_details ALTER COLUMN client_type SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE projectmntnc ADD COLUMN project_type integer;" +
				"ALTER TABLE projectmntnc ALTER COLUMN project_type SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE client_details ADD COLUMN tds_percent double precision;" +
				"ALTER TABLE client_details ALTER COLUMN tds_percent SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE client_details ADD COLUMN registration_no character varying;");
		
		alQueries.add("ALTER TABLE promntc_bill_amt_details ADD COLUMN write_off_percent double precision;");
		
		alQueries.add("ALTER TABLE project_skill_details ADD COLUMN skill_id integer;");
		
		alQueries.add("ALTER TABLE variable_cost ADD COLUMN emp_id integer;" +
				"ALTER TABLE variable_cost ADD COLUMN amount_type character varying;");
		
		alQueries.add("ALTER TABLE promntc_bill_amt_details ADD COLUMN prev_year_tds_deducted double precision;");
		
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN pro_id integer;");
		
		alQueries.add("ALTER TABLE branch_details ADD COLUMN swift_code character varying;" +
				"ALTER TABLE branch_details ADD COLUMN bank_clearing_code character varying;" +
				"ALTER TABLE branch_details ADD COLUMN other_information character varying;");
		
		alQueries.add("ALTER TABLE promntc_bill_amt_details ADD COLUMN professional_fees double precision;");
		
		alQueries.add("ALTER TABLE currency_details ADD COLUMN inr_value double precision;" +
				"ALTER TABLE currency_details ADD COLUMN updated_by integer;" +
				"ALTER TABLE currency_details ADD COLUMN update_date date;");
		
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN ideal_candidate text;");
		
		alQueries.add("ALTER TABLE designation_details ADD COLUMN ideal_candidate text;");
		
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN reference_no_desc character varying;");
		
		alQueries.add("ALTER TABLE projectmntnc ADD COLUMN sbu_id integer;" +
				"ALTER TABLE projectmntnc ADD COLUMN org_id integer;" +
				"ALTER TABLE projectmntnc ADD COLUMN short_description character varying;" +
				"ALTER TABLE projectmntnc ADD COLUMN billing_curr_id integer;");
		
		alQueries.add("CREATE TABLE project_document_details" +
				"(" +
				"pro_document_id serial NOT NULL," +
				"client_id integer," +
				"pro_id integer," +
				"folder_name character varying," +
				"document_name character varying," +
				"added_by integer," +
				"entry_date date," +
				"folder_file_type character varying," +
				"CONSTRAINT project_document_details_pkey PRIMARY KEY (pro_document_id)" +
				");" +
				"ALTER TABLE project_document_details OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE projectmntnc ADD COLUMN milestone_dependent_on integer;");
		
		alQueries.add("ALTER TABLE task_activity ADD COLUMN billable_hrs double precision;");
		alQueries.add("ALTER TABLE task_activity ADD COLUMN submited_date date;");
		
		alQueries.add("ALTER TABLE promntc_invoice_amt_details ADD COLUMN resource_name character varying;" +
				"ALTER TABLE promntc_invoice_amt_details ADD COLUMN day_or_hour integer;");
		
		alQueries.add("ALTER TABLE task_activity ADD COLUMN is_billable_approved integer;" +
				"ALTER TABLE task_activity ALTER COLUMN is_billable_approved SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE task_activity ADD COLUMN billable_approved_by integer;");
		alQueries.add("ALTER TABLE task_activity ADD COLUMN billable_approve_date date;");
		
		alQueries.add("ALTER TABLE projectmntnc ADD COLUMN bill_days_type integer;");
		alQueries.add("ALTER TABLE projectmntnc ADD COLUMN hours_for_bill_day double precision;");
		
		alQueries.add("ALTER TABLE project_document_details ADD COLUMN pro_folder_id integer;");
		alQueries.add("ALTER TABLE project_document_details ADD COLUMN file_size character varying;");
		alQueries.add("ALTER TABLE project_document_details ADD COLUMN file_type character varying;");
		
		alQueries.add("ALTER TABLE level_skill_rates ADD COLUMN curr_id integer;");
		
		alQueries.add("ALTER TABLE activity_info ADD COLUMN parent_task_id integer;" +
				"ALTER TABLE activity_info ALTER COLUMN parent_task_id SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE activity_info ADD COLUMN task_skill_id integer;");
		alQueries.add("ALTER TABLE activity_info ADD COLUMN resource_ids character varying;");
		
		alQueries.add("ALTER TABLE project_document_details ADD COLUMN align_with integer;");
		alQueries.add("ALTER TABLE project_document_details ADD COLUMN sharing_type integer;");
		alQueries.add("ALTER TABLE project_document_details ADD COLUMN sharing_resources character varying;");
		alQueries.add("ALTER TABLE project_document_details ADD COLUMN size_in_bytes character varying;");
		
		alQueries.add("ALTER TABLE projectmntnc ADD COLUMN invoice_template_type integer;");
		
		alQueries.add("ALTER TABLE promntc_invoice_amt_details ADD COLUMN oc_invoice_particulars_amount double precision;");
		
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN oc_particulars_total_amount double precision;");
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN oc_invoice_amount double precision;");
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN oc_other_amount double precision;");
		
		alQueries.add("ALTER TABLE promntc_invoice_amt_details ADD COLUMN task_id integer;");
		
		alQueries.add("ALTER TABLE activity_info ADD COLUMN task_from_my_self boolean;" +
				"ALTER TABLE activity_info ALTER COLUMN task_from_my_self SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE invoice_formats ADD COLUMN is_delete boolean;" +
				"ALTER TABLE invoice_formats ALTER COLUMN is_delete SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE org_details ADD COLUMN org_reg_no character varying;");
		
		alQueries.add("ALTER TABLE candidate_personal_details ADD COLUMN current_ctc double precision;" +
				"ALTER TABLE candidate_personal_details ADD COLUMN expected_ctc double precision;" +
				"ALTER TABLE candidate_personal_details ADD COLUMN notice_period integer;");
		
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN customer_id integer;" +
				"ALTER TABLE recruitment_details ADD COLUMN job_title character varying;");

		alQueries.add("ALTER TABLE candidate_personal_details ADD COLUMN org_id integer;" +
				"ALTER TABLE candidate_personal_details ADD COLUMN ctc_curr_id integer;");

		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN req_form_type integer;" +
				"ALTER TABLE recruitment_details ALTER COLUMN req_form_type SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE candidate_personal_details ADD COLUMN availability_for_interview boolean;" +
				"ALTER TABLE candidate_personal_details ALTER COLUMN availability_for_interview SET DEFAULT false;");
		
		
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN hiring_manager character varying;");
		
		alQueries.add("ALTER TABLE work_location_info ADD COLUMN weightage integer;" +
				"ALTER TABLE work_location_info ALTER COLUMN weightage SET DEFAULT 1;");
		
		alQueries.add("ALTER TABLE client_details ADD COLUMN added_by integer;" +
				"ALTER TABLE client_details ADD COLUMN entry_date date;");
		
		alQueries.add("ALTER TABLE level_skill_rates ADD COLUMN rate_per_month double precision;" +
				"ALTER TABLE project_emp_details ADD COLUMN emp_rate_per_month double precision;" +
				"ALTER TABLE project_emp_details ADD COLUMN emp_actual_rate_per_month double precision;");
		
		
		alQueries.add("ALTER TABLE project_tax_setting ADD COLUMN status boolean;" +
				"ALTER TABLE project_tax_setting ADD COLUMN tax_setting_id integer;" +
				"ALTER TABLE porject_billing_heads_details ADD COLUMN billing_head_id integer;");
		
		alQueries.add("ALTER TABLE promntc_invoice_amt_details ADD COLUMN head_type character varying;" +
				"ALTER TABLE promntc_invoice_amt_details ADD COLUMN tax_percent double precision;");
		
		alQueries.add("ALTER TABLE level_details ADD COLUMN weightage integer;" +
				"ALTER TABLE level_details ALTER COLUMN weightage SET DEFAULT 0;" +
				"ALTER TABLE successionplan_criteria_details ADD COLUMN levels_below integer;");
		
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN adhoc_billing_type integer;" +
				"ALTER TABLE promntc_invoice_details ALTER COLUMN adhoc_billing_type SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN invoice_update_date date;" +
				"ALTER TABLE promntc_invoice_details ADD COLUMN invoice_updated_by integer;");
		
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN invoice_cancel boolean;" +
				"ALTER TABLE promntc_invoice_details ALTER COLUMN invoice_cancel SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE projectmntnc ADD COLUMN billing_cycle_weekday character varying;" +
				"ALTER TABLE projectmntnc ADD COLUMN billing_cycle_day integer;");
		
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN pro_freq_id integer;" +
				"ALTER TABLE promntc_bill_amt_details ADD COLUMN pro_freq_id integer;");
		
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN pro_recurring_billing integer;");
		
		alQueries.add("ALTER TABLE org_details ADD COLUMN org_st_reg_no character varying;" +
				"ALTER TABLE org_details ADD COLUMN org_additional_note character varying;");
		
		alQueries.add("ALTER TABLE promntc_bill_parti_amt_details ADD COLUMN amt_receive_type integer;");

		alQueries.add("ALTER TABLE projectmntnc_frequency ADD COLUMN pro_freq_name character varying;");
		
		alQueries.add("ALTER TABLE projectmntnc_frequency ADD COLUMN pro_milestone_id integer;" +
				"ALTER TABLE projectmntnc_frequency ALTER COLUMN pro_milestone_id SET DEFAULT 0;");

		alQueries.add("ALTER TABLE promntc_invoice_details ALTER COLUMN pro_freq_id SET DEFAULT 0;" +
				"ALTER TABLE promntc_bill_amt_details ALTER COLUMN pro_freq_id SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE project_document_details ADD COLUMN is_edit boolean;" +
				"ALTER TABLE project_document_details ALTER COLUMN is_edit SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE project_document_details ADD COLUMN is_delete boolean;" +
				"ALTER TABLE project_document_details ALTER COLUMN is_delete SET DEFAULT false;"); 
		
		alQueries.add("ALTER TABLE project_document_details ADD COLUMN is_cust_add boolean;" +
				"ALTER TABLE project_document_details ALTER COLUMN is_cust_add SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE project_document_details ADD COLUMN sharing_poc character varying;");

		alQueries.add("ALTER TABLE tax_setting ADD COLUMN tax_name_label character varying;" +
				"ALTER TABLE project_tax_setting ADD COLUMN tax_name_label character varying;" +
				"ALTER TABLE promntc_invoice_amt_details ADD COLUMN invoice_particulars_label character varying;" +
				"ALTER TABLE promntc_bill_parti_amt_details ADD COLUMN bill_particulars_label character varying;" +
				"ALTER TABLE promntc_invoice_details ADD COLUMN entry_date date;");
		
		alQueries.add("ALTER TABLE org_details ADD COLUMN offices_at character varying;" +
				"ALTER TABLE promntc_invoice_details ADD COLUMN invoice_no integer;");
		
		alQueries.add("ALTER TABLE activity_info ADD COLUMN added_by integer;" +
				"ALTER TABLE activity_info ADD COLUMN task_description character varying;" +
				"ALTER TABLE activity_info ADD COLUMN recurring_task integer;" +
				"ALTER TABLE activity_info ALTER COLUMN recurring_task SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE activity_info ADD COLUMN task_freq_name character varying;");
		
		alQueries.add("ALTER TABLE activity_info ADD COLUMN task_accept_status integer;" +
				"ALTER TABLE activity_info ALTER COLUMN task_accept_status SET DEFAULT 0;" +
				"ALTER TABLE activity_info ADD COLUMN r_start_date date;" +
				"ALTER TABLE activity_info ADD COLUMN r_deadline date;");
		
		alQueries.add("ALTER TABLE activity_info ADD COLUMN task_reassign_reschedule_comment character varying;");
		
		alQueries.add("ALTER TABLE activity_info ADD COLUMN reschedule_reassign_align_by integer;" +
				"ALTER TABLE activity_info ADD COLUMN requested_by integer;");
		
		alQueries.add("ALTER TABLE projectmntnc_frequency ADD COLUMN save_for_later integer;" +
				"ALTER TABLE projectmntnc_frequency ALTER COLUMN save_for_later SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE project_document_details ADD COLUMN is_edit boolean;" +
				"ALTER TABLE project_document_details ALTER COLUMN is_edit SET DEFAULT false;" +
				"ALTER TABLE project_document_details ADD COLUMN is_delete boolean;" +
				"ALTER TABLE project_document_details ALTER COLUMN is_delete SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE client_poc ADD COLUMN contact_location_id integer;" +
				"ALTER TABLE client_poc ADD COLUMN contact_desig_id integer;" +
				"ALTER TABLE client_poc ADD COLUMN contact_department_id integer;");
		
		alQueries.add("ALTER TABLE project_document_details ADD COLUMN is_cust_add boolean;" +
				"ALTER TABLE project_document_details ALTER COLUMN is_cust_add SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE task_activity ADD COLUMN cust_denied_by integer;" +
				"ALTER TABLE task_activity ADD COLUMN cust_denied_date date;");
		
		alQueries.add("ALTER TABLE activity_info ADD COLUMN is_cust_add integer;" +
				"ALTER TABLE activity_info ALTER COLUMN is_cust_add SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE client_details ADD COLUMN org_id integer;");
		
		alQueries.add("ALTER TABLE client_details ADD COLUMN client_city character varying;");
		
		alQueries.add("ALTER TABLE projectmntnc ADD COLUMN bank_id integer;" +
				"ALTER TABLE projectmntnc ADD COLUMN paypal_mail_id character varying;" +
				"ALTER TABLE projectmntnc ADD COLUMN acc_ref character varying;" +
				"ALTER TABLE projectmntnc ADD COLUMN po_no character varying;" +
				"ALTER TABLE projectmntnc ADD COLUMN terms character varying;" +
				"ALTER TABLE projectmntnc ADD COLUMN bill_due_date date;");
		
		alQueries.add("ALTER TABLE promntc_invoice_details ADD COLUMN paypal_mail_id character varying;" +
				"ALTER TABLE promntc_invoice_details ADD COLUMN acc_ref character varying;" +
				"ALTER TABLE promntc_invoice_details ADD COLUMN po_no character varying;" +
				"ALTER TABLE promntc_invoice_details ADD COLUMN terms character varying;" +
				"ALTER TABLE promntc_invoice_details ADD COLUMN bill_due_date date;" +
				"ALTER TABLE promntc_invoice_details ADD COLUMN invoice_template_id integer;");
		
		alQueries.add("ALTER TABLE promntc_invoice_amt_details ADD COLUMN parent_parti_id integer;" +
				"ALTER TABLE promntc_invoice_amt_details ALTER COLUMN parent_parti_id SET DEFAULT 0;" +
				"ALTER TABLE promntc_invoice_amt_details ADD COLUMN milestone_id integer;");
		
		alQueries.add("ALTER TABLE communication_1 ADD COLUMN update_time timestamp without time zone;" +
				"ALTER TABLE communication_1 ADD COLUMN doc_or_image character varying;" +
				"ALTER TABLE communication_1 ADD COLUMN client_tagged_with character varying;" +
				"ALTER TABLE communication_1 ADD COLUMN client_visibility_with_id character varying;" +
				"ALTER TABLE communication_1 ADD COLUMN client_created_by integer;" +
				"ALTER TABLE communication_1 ADD COLUMN feed_type integer;" +
				"ALTER TABLE communication_1 ALTER COLUMN feed_type SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE project_document_details ADD COLUMN feed_id integer;" +
				"ALTER TABLE project_document_details ALTER COLUMN feed_id SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE projectmntnc_frequency ADD COLUMN is_delete boolean;" +
				"ALTER TABLE projectmntnc_frequency ALTER COLUMN is_delete SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE branch_details ADD COLUMN is_ifsc boolean;" +
				"ALTER TABLE branch_details ALTER COLUMN is_ifsc SET DEFAULT false;" +
				"ALTER TABLE branch_details ADD COLUMN is_swift boolean;" +
				"ALTER TABLE branch_details ALTER COLUMN is_swift SET DEFAULT false;" +
				"ALTER TABLE branch_details ADD COLUMN is_clearing_code boolean;" +
				"ALTER TABLE branch_details ALTER COLUMN is_clearing_code SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE communication_1 ADD COLUMN client_like_ids character varying;");
		
		alQueries.add("ALTER TABLE goal_kras ADD COLUMN is_close boolean;" +
				"ALTER TABLE goal_kras ALTER COLUMN is_close SET DEFAULT false;" +
				"ALTER TABLE goal_kras ADD COLUMN close_reason character varying;");
		
		alQueries.add("ALTER TABLE panel_interview_details ADD COLUMN assessment_id integer;");
		
		alQueries.add("ALTER TABLE assessment_question_answer ADD COLUMN recruitment_id integer;");
		alQueries.add("ALTER TABLE assessment_question_answer ADD COLUMN round_id integer;");
		alQueries.add("ALTER TABLE assessment_question_answer ADD COLUMN candidate_id integer;");
		
		alQueries.add("ALTER TABLE candidate_interview_panel ADD COLUMN assessment_id integer;");
		
		alQueries.add("ALTER TABLE assessment_take_attempt_details ADD COLUMN candidate_id integer;" +
				"ALTER TABLE assessment_take_attempt_details ADD COLUMN round_id integer;" +
				"ALTER TABLE assessment_take_attempt_details ADD COLUMN recruitment_id integer;");
		
		alQueries.add("ALTER TABLE assessment_emp_remain_time ADD COLUMN candidate_id integer;" +
				"ALTER TABLE assessment_emp_remain_time ADD COLUMN round_id integer;" +
				"ALTER TABLE assessment_emp_remain_time ADD COLUMN recruitment_id integer;");
		
		alQueries.add("ALTER TABLE project_information_display ADD COLUMN snapshot_time double precision;");
		
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN source_type integer;" +
				"ALTER TABLE candidate_application_details ALTER COLUMN source_type SET DEFAULT 0;" +
				"ALTER TABLE candidate_application_details ADD COLUMN source_or_ref_code integer;");
		
		alQueries.add("ALTER TABLE ideal_time_details ADD COLUMN freq_status integer;" +
				"ALTER TABLE ideal_time_details ALTER COLUMN freq_status SET DEFAULT 0;" +
				"ALTER TABLE ideal_time_details ADD COLUMN entry_date date;");
		
		alQueries.add("ALTER TABLE communication_1 ADD COLUMN bday_emp_id integer;");
		
		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN is_one_step boolean;" +
				"ALTER TABLE employee_personal_details ALTER COLUMN is_one_step SET DEFAULT false;" +
				"ALTER TABLE employee_personal_details ADD COLUMN is_delete boolean;" +
				"ALTER TABLE employee_personal_details ALTER COLUMN is_delete SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE activity_info ADD COLUMN kra_id integer;");
		
		alQueries.add("ALTER TABLE goal_details ADD COLUMN super_id integer;" +
				"ALTER TABLE goal_details ALTER COLUMN super_id SET DEFAULT 0;");

		alQueries.add("ALTER TABLE activity_info ADD COLUMN goal_kra_task_id integer;");
		
		alQueries.add("ALTER TABLE designation_kra_details ADD COLUMN task_name character varying;");
		
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN new_joinee_pending integer;");
		
		alQueries.add("ALTER TABLE cost_calculation_settings ADD COLUMN days integer;" +
				"ALTER TABLE cost_calculation_settings ALTER COLUMN days SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE daythoughts ADD COLUMN entry_date date;");
		
		alQueries.add("ALTER TABLE daythoughts ADD COLUMN added_by integer;");
		
		alQueries.add("ALTER TABLE daythoughts ADD COLUMN posted_date timestamp without time zone;");
		
		alQueries.add("ALTER TABLE events ADD COLUMN location character varying;");
		
		alQueries.add("ALTER TABLE events ADD COLUMN sharing_level character varying;");
		
		alQueries.add("ALTER TABLE events ADD COLUMN event_image character varying;");
		
		alQueries.add("ALTER TABLE events ADD COLUMN event_end_date date;");
		
		alQueries.add("ALTER TABLE events ADD COLUMN posted_date timestamp without time zone;");
		
		alQueries.add("ALTER TABLE events ADD COLUMN from_time time without time zone;");
		
		alQueries.add("ALTER TABLE events ADD COLUMN to_time time without time zone;");
		
		alQueries.add("ALTER TABLE notices ADD COLUMN posted_date timestamp without time zone;");
		
		alQueries.add("ALTER TABLE notices ADD COLUMN added_by integer;");
		
		alQueries.add("ALTER TABLE projectmntnc ADD COLUMN reference_by_id integer;");
		
		alQueries.add("ALTER TABLE daythoughts ADD COLUMN updated_by integer;");
		alQueries.add("ALTER TABLE daythoughts ADD COLUMN last_updated timestamp without time zone;");
		alQueries.add("ALTER TABLE daythoughts ADD COLUMN year integer;");
		alQueries.add("ALTER TABLE events ADD COLUMN updated_by integer;");
		alQueries.add("ALTER TABLE events ADD COLUMN last_updated timestamp without time zone;");
		alQueries.add("ALTER TABLE notices ADD COLUMN updated_by integer;");
		alQueries.add("ALTER TABLE notices ADD COLUMN last_updated timestamp without time zone;");
		alQueries.add("ALTER TABLE taskrig_user_alerts ADD COLUMN type character varying;");
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN news_and_alerts integer;");
		
		alQueries.add("ALTER TABLE org_details ADD COLUMN emp_code_auto_generate boolean;" +
				"ALTER TABLE org_details ALTER COLUMN emp_code_auto_generate SET DEFAULT false;" +
				"ALTER TABLE org_details ADD COLUMN emp_code_alpha character varying;" +
				"ALTER TABLE org_details ADD COLUMN contractor_code_alpha character varying;" +
				"ALTER TABLE org_details ADD COLUMN emp_code_numeric character varying;");
		
		alQueries.add("ALTER TABLE allowance_condition_details ADD COLUMN goal_kra_target_ids character varying;");
		
		alQueries.add("ALTER TABLE allowance_condition_details ADD COLUMN kra_ids character varying;" +
				"ALTER TABLE allowance_condition_details ADD COLUMN is_publish boolean;" +
				"ALTER TABLE allowance_condition_details ALTER COLUMN is_publish SET DEFAULT false;" +
				"ALTER TABLE allowance_condition_details ADD COLUMN updated_by integer;" +
				"ALTER TABLE allowance_condition_details ADD COLUMN update_date date;" +
				"ALTER TABLE allowance_payment_logic ADD COLUMN is_publish boolean;" +
				"ALTER TABLE allowance_payment_logic ALTER COLUMN is_publish SET DEFAULT false;" +
				"ALTER TABLE allowance_payment_logic ADD COLUMN updated_by integer;" +
				"ALTER TABLE allowance_payment_logic ADD COLUMN update_date date;");
		
		alQueries.add("ALTER TABLE education_details ADD COLUMN inst_name character varying(200);");
		
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN min_ctc double precision;" +
				"ALTER TABLE recruitment_details ADD COLUMN max_ctc double precision;");
		
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN self_review_approval integer;");
		
		alQueries.add("ALTER TABLE goal_kras ADD COLUMN kra_weightage double precision;" +
				"ALTER TABLE project_information_display ADD COLUMN attend_from_attend_detail boolean;" +
				"ALTER TABLE project_information_display ALTER COLUMN attend_from_attend_detail SET DEFAULT false;" +
				"ALTER TABLE project_information_display ADD COLUMN attend_from_timesheet_detail boolean;" +
				"ALTER TABLE project_information_display ALTER COLUMN attend_from_timesheet_detail SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN publish_request_to_workflow boolean;" +
				"ALTER TABLE appraisal_details ALTER COLUMN publish_request_to_workflow SET DEFAULT false;" +
				"ALTER TABLE user_alerts ADD COLUMN self_review_request integer;");
		
		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN publish_approve_deny_by integer;" +
				"ALTER TABLE appraisal_details ADD COLUMN publish_approve_deny_reason character varying;" +
				"ALTER TABLE appraisal_details ADD COLUMN publish_is_approved integer;" +
				"ALTER TABLE appraisal_details ALTER COLUMN publish_is_approved SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN emp_confirmations integer;" +
				"ALTER TABLE user_alerts ADD COLUMN emp_resignations integer;" +
				"ALTER TABLE user_alerts ADD COLUMN emp_final_day integer;");
		
		alQueries.add("ALTER TABLE goal_details ADD COLUMN peer_ids character varying;" +
				"ALTER TABLE goal_details ADD COLUMN manager_ids character varying;" +
				"ALTER TABLE goal_details ADD COLUMN hr_ids character varying;" +
				"ALTER TABLE goal_details ADD COLUMN anyone_ids character varying;");
		
		alQueries.add("ALTER TABLE target_details ADD COLUMN goal_freq_id integer;");
		alQueries.add("ALTER TABLE goal_kra_status_rating_details ADD COLUMN goal_freq_id integer;");
		
		alQueries.add("ALTER TABLE training_gap_details ADD COLUMN added_by integer;" +
				"ALTER TABLE training_gap_details ADD COLUMN entry_date date;");
		
		alQueries.add("ALTER TABLE client_details ADD COLUMN isdisabled boolean;" +
				"ALTER TABLE client_details ALTER COLUMN isdisabled SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN library_request integer;" +
				"ALTER TABLE user_alerts ADD COLUMN library_req_approved integer;");
		
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN meeting_room_booking_request integer;" +
				"ALTER TABLE user_alerts ADD COLUMN new_manual integer;" +
				"ALTER TABLE user_alerts ADD COLUMN meeting_room_booking_req_approved integer;" +
				"ALTER TABLE user_alerts ADD COLUMN food_request integer;");
		
		alQueries.add("ALTER TABLE dish_order_details ADD COLUMN guest_names character varying;" +
				"ALTER TABLE dish_order_details ADD COLUMN added_by integer;");
		
		alQueries.add("ALTER TABLE meeting_room_booking_details ADD COLUMN updated_by integer;" +
				"ALTER TABLE meeting_room_booking_details ADD COLUMN updated_date date;" +
				"ALTER TABLE meeting_room_booking_details ADD COLUMN meeting_room_location integer;" +
				"ALTER TABLE meeting_room_booking_details ADD COLUMN food_dish_types character varying;");

		alQueries.add("ALTER TABLE employee_official_details ADD COLUMN is_hod boolean;" +
				"ALTER TABLE employee_official_details ALTER COLUMN is_hod SET DEFAULT false;" +
				"ALTER TABLE employee_official_details ADD COLUMN is_cxo boolean;" +
				"ALTER TABLE employee_official_details ALTER COLUMN is_cxo SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE employee_activity_details ADD COLUMN emp_hod_id integer;");
		
		alQueries.add("ALTER TABLE orientation_details ADD COLUMN view_access boolean;" +
				"ALTER TABLE orientation_details ALTER COLUMN view_access SET DEFAULT false;" +
				"ALTER TABLE orientation_details ADD COLUMN edit_access boolean;" +
				"ALTER TABLE orientation_details ALTER COLUMN edit_access SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN new_canditate_fill integer;");
		
		alQueries.add("ALTER TABLE goal_kra_emp_status_rating_details ADD COLUMN updated_date date;");
		alQueries.add("ALTER TABLE goal_kra_emp_status_rating_details ADD COLUMN updated_by integer;");
		alQueries.add("ALTER TABLE goal_kra_status_rating_details ADD COLUMN updated_by integer;");
		alQueries.add("ALTER TABLE goal_kra_status_rating_details ADD COLUMN updated_date date;");
		
		alQueries.add("ALTER TABLE emp_off_board ADD COLUMN previous_emp_status character varying;");
		alQueries.add("ALTER TABLE goal_kra_target_finalization ADD COLUMN goal_freq_id integer;");
		
		alQueries.add("ALTER TABLE emp_off_board ADD COLUMN previous_emp_status character varying;");
		
		alQueries.add("ALTER TABLE user_alerts ADD COLUMN emp_terminations integer;");
		
		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN emp_bank_name2 character varying;" +
		"ALTER TABLE employee_personal_details ADD COLUMN emp_bank_acct_nbr_2 character varying;");

		alQueries.add("ALTER TABLE user_details ADD COLUMN terms_condition_type character varying;");
		
		alQueries.add(" ALTER TABLE user_alerts ADD COLUMN ceo_reviews integer;" +
				"ALTER TABLE user_alerts ADD COLUMN hod_reviews integer;");
		
		alQueries.add("ALTER TABLE appraisal_main_level_details ADD COLUMN hod integer;" +
				"ALTER TABLE appraisal_main_level_details ALTER COLUMN hod SET DEFAULT 0;" +
				"ALTER TABLE appraisal_main_level_details ADD COLUMN ceo integer;" +
				"ALTER TABLE appraisal_main_level_details ALTER COLUMN ceo SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN ceo_ids text;" +
				"ALTER TABLE appraisal_details ADD COLUMN hod_ids text;");
		
		alQueries.add("ALTER TABLE user_details ADD COLUMN terms_condition_type character varying;");
		
		alQueries.add("ALTER TABLE candidate_references ALTER COLUMN ref_name TYPE character varying;" 
				+"ALTER TABLE candidate_references ALTER COLUMN ref_company TYPE character varying;"
				+"ALTER TABLE candidate_references ALTER COLUMN ref_designation TYPE character varying;"
				+"ALTER TABLE candidate_references ALTER COLUMN ref_contact_no TYPE character varying;"
				+"ALTER TABLE candidate_references ALTER COLUMN ref_email_id TYPE character varying;");
		
		alQueries.add("CREATE TABLE appraisal_details_frequency" +
				"(" +
				"appraisal_freq_id serial NOT NULL," +
				"appraisal_start_date date," +
				"appraisal_due_date date," +
				"freq_start_date date," +
				"freq_end_date date," +
				"appraisal_freq_name character varying," +
				"added_by integer," +
				"entry_date date," +
				"is_delete boolean DEFAULT false," +
				"appraisal_id integer," +
				"is_appraisal_publish boolean DEFAULT false," +
				"freq_publish_expire_status integer DEFAULT 0," +
				"is_appraisal_close boolean DEFAULT false," +
				"close_reason character varying" +
				");" +
				"ALTER TABLE appraisal_details_frequency OWNER TO postgres;");
		
		alQueries.add("ALTER TABLE employee_activity_details ADD COLUMN appraisal_freq_id integer;"
				+" ALTER TABLE appraisal_approval ADD COLUMN appraisal_freq_id integer;"
				+" ALTER TABLE appraisal_question_answer ADD COLUMN appraisal_freq_id integer;"
				+" ALTER TABLE appraisal_final_sattlement ADD COLUMN appraisal_freq_id integer;"
				+" ALTER TABLE kra_rating_details ADD COLUMN appraisal_freq_id integer;"
				+" ALTER TABLE target_details ADD COLUMN appraisal_freq_id integer;"
				+" ALTER TABLE training_gap_details ADD COLUMN appraisal_freq_id integer;");
		
		alQueries.add("ALTER TABLE book_details ADD COLUMN available_book_quantity integer;");
		
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN annual_ctc_offered double precision;");
		
		alQueries.add("ALTER TABLE org_details ADD COLUMN org_logo_small character varying;");
		alQueries.add("ALTER TABLE employee_personal_details ADD COLUMN emp_cover_image character varying;");
//		alQueries.add("insert into settings (options,entry_date) values('ORG_LOGO_SMALL','2017-06-12')");
		
		alQueries.add("ALTER TABLE bonus_details  ALTER COLUMN bonus_period TYPE character varying;");  
		alQueries.add("ALTER TABLE bonus_details  ALTER COLUMN bonus_type TYPE character varying;");
		
		alQueries.add("ALTER TABLE goal_details ADD COLUMN open_reason character varying;");
		alQueries.add("ALTER TABLE goal_details ADD COLUMN opened_by integer;");
		alQueries.add("ALTER TABLE goal_kras ADD COLUMN open_reason character varying;");
		alQueries.add("ALTER TABLE goal_kras ADD COLUMN opened_by integer;");
		
		alQueries.add("ALTER TABLE appraisal_final_sattlement ADD COLUMN areas_of_strength character varying;");
		alQueries.add("ALTER TABLE appraisal_final_sattlement ADD COLUMN areas_of_development character varying;");
		alQueries.add("ALTER TABLE appraisal_final_sattlement ADD COLUMN learning_ids character varying;");
		
		alQueries.add("ALTER TABLE client_poc ADD COLUMN added_by integer;");
		
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN reimb_from_date date;");
		alQueries.add("ALTER TABLE emp_reimbursement ADD COLUMN reimb_to_date date;");
		
		alQueries.add("ALTER TABLE workrig_user_alerts ADD COLUMN employee_id integer;");   
		alQueries.add("ALTER TABLE workrig_user_alerts ADD COLUMN _date date;");
		alQueries.add("ALTER TABLE workrig_user_alerts ADD COLUMN entry_date_time date;");
		
		alQueries.add("ALTER TABLE feature_management ADD COLUMN emp_ids character varying;");
		
		alQueries.add("ALTER TABLE languages_details ADD COLUMN language_mothertounge integer;");
		
		alQueries.add("ALTER TABLE activity_info ADD COLUMN reschedule_reassign_by_comment character varying;");
		alQueries.add("ALTER TABLE activity_info ADD COLUMN reschedule_reassign_request_status integer;" +
				"ALTER TABLE activity_info ALTER COLUMN reschedule_reassign_request_status SET DEFAULT 0;");
		
		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN reviewer_id character varying;");
		alQueries.add("ALTER TABLE appraisal_question_answer ADD COLUMN reviewer_or_appraiser integer;" +
				"ALTER TABLE appraisal_question_answer ALTER COLUMN reviewer_or_appraiser SET DEFAULT 0;");
		alQueries.add("ALTER TABLE appraisal_question_answer ADD COLUMN reviewer_answer character varying;");
		alQueries.add("ALTER TABLE appraisal_question_answer ADD COLUMN reviewer_id integer;");
		alQueries.add("ALTER TABLE appraisal_question_answer ADD COLUMN reviewer_user_type_id integer;");
		alQueries.add("ALTER TABLE appraisal_question_answer ADD COLUMN reviewer_attempted_on date;");
		alQueries.add("ALTER TABLE appraisal_question_answer ADD COLUMN reviewer_marks double precision;");
		alQueries.add("ALTER TABLE appraisal_question_answer ADD COLUMN reviewer_remark character varying;");
		alQueries.add("ALTER TABLE appraisal_question_answer ADD COLUMN reviewer_answers_comment character varying;");
		
		alQueries.add("ALTER TABLE activity_info ADD COLUMN is_billable_task boolean;" +
				"ALTER TABLE activity_info ALTER COLUMN is_billable_task SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE question_bank ADD COLUMN option_e character varying;");
		alQueries.add("ALTER TABLE training_question_bank ADD COLUMN option_e character varying;");
		alQueries.add("ALTER TABLE assessment_question_bank ADD COLUMN option_e character varying;");
		
		alQueries.add("ALTER TABLE question_bank ADD COLUMN rate_option_a integer;");
		alQueries.add("ALTER TABLE question_bank ADD COLUMN rate_option_b integer;");
		alQueries.add("ALTER TABLE question_bank ADD COLUMN rate_option_c integer;");
		alQueries.add("ALTER TABLE question_bank ADD COLUMN rate_option_d integer;");
		alQueries.add("ALTER TABLE question_bank ADD COLUMN rate_option_e integer;");
		
		alQueries.add("ALTER TABLE appraisal_question_answer ADD COLUMN section_comment character varying;");
		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN user_types_for_feedback character varying;");
		
		alQueries.add("ALTER TABLE appraisal_question_answer ADD COLUMN section_comment_file character varying;");
		
		alQueries.add("ALTER TABLE assessment_question_bank ADD COLUMN que_attached_file character varying;");
		alQueries.add("ALTER TABLE assessment_question_bank ADD COLUMN que_matrix_heading character varying;");
		
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN offer_backout_status integer;" +
				"ALTER TABLE candidate_application_details ALTER COLUMN offer_backout_status SET DEFAULT 0;");
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN offer_backout_remark character varying;");
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN offer_backout_date date;");

		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN essential_skills_text character varying;");
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN desirable_skills_text character varying;");
		
		alQueries.add("ALTER TABLE candidate_interview_panel ADD COLUMN interview_attachment character varying;");
		
		alQueries.add("ALTER TABLE goal_details ADD COLUMN align_with_perspective boolean;" +
				"ALTER TABLE goal_details ALTER COLUMN align_with_perspective SET DEFAULT false;");
		alQueries.add("ALTER TABLE goal_details ADD COLUMN perspective_id integer;");
		
		alQueries.add("ALTER TABLE bsc_perspective_details ADD COLUMN perspective_description character varying;");
		alQueries.add("ALTER TABLE bsc_perspective_details ADD COLUMN perspective_color character varying;");

		alQueries.add("ALTER TABLE plan_status_details ADD COLUMN desig_id integer;");
		alQueries.add("ALTER TABLE plan_status_details ADD COLUMN status_color character varying;");
		
		alQueries.add("ALTER TABLE appraisal_final_sattlement ADD COLUMN review_ids character varying;");
		alQueries.add("ALTER TABLE appraisal_final_sattlement ADD COLUMN desig_id integer;");
		alQueries.add("ALTER TABLE appraisal_final_sattlement ADD COLUMN incumbent_emp_id integer;");
		
		alQueries.add("ALTER TABLE employee_activity_details ADD COLUMN review_ids character varying;");
		alQueries.add("ALTER TABLE employee_activity_details ADD COLUMN learning_ids character varying;");
		alQueries.add("ALTER TABLE employee_activity_details ADD COLUMN incumbent_emp_id integer;");
		
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN resume_workflow_policy_id integer;");
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN resume_workflow_aligned_member character varying;");
		
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN application_shortlist_by integer;");
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN application_shortlist_reason character varying;");
		
		alQueries.add("ALTER TABLE candidate_personal_details ADD COLUMN total_experience double precision;");
		
		alQueries.add("ALTER TABLE candidate_prev_employment ADD COLUMN experience_letter character varying;");
		alQueries.add("ALTER TABLE candidate_prev_employment ADD COLUMN relieving_letter character varying;");
		alQueries.add("ALTER TABLE candidate_prev_employment ADD COLUMN fnf_doc character varying;");
		alQueries.add("ALTER TABLE candidate_prev_employment ADD COLUMN bank_statement character varying;");
		
		alQueries.add("ALTER TABLE goal_details ADD COLUMN depart_id integer;");
		alQueries.add("ALTER TABLE goal_details ADD COLUMN freq_year_type integer;");
		alQueries.add("ALTER TABLE goal_details ADD COLUMN recurring_years character varying;");
		
		alQueries.add("ALTER TABLE goal_details ADD COLUMN with_depart_with_team character varying;");
		
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN prev_recruit_id integer;");
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN recruit_id_changed_by integer;");
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN recruit_id_changed_date timestamp without time zone;");
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN recruit_id_changed_reason character varying;");
		
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN offer_negotiation_approval_request_remark character varying;");
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN need_approval_for_offer_negotiation boolean;");
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN offer_negotiation_approver integer;");
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN offer_negotiation_approval_requested_by integer;");
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN offer_negotiation_approval_request_date timestamp without time zone;");
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN offer_negotiation_request_approve_remark character varying;");
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN offer_negotiation_request_approved_by integer;");
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN offer_negotiation_request_approve_date timestamp without time zone;");
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN is_negotiation_approve boolean;");
		
		alQueries.add("ALTER TABLE goal_details ADD COLUMN review_this_goal character varying;");
		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN goal_id integer;");
		
		alQueries.add("ALTER TABLE communication_1 ADD COLUMN org_attendance_approval_status_paycycle_id integer;");
		alQueries.add("ALTER TABLE communication_1 ADD COLUMN org_salary_approval_status_paycycle_id integer;");
		
		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN eligibility_min_daysbefore_start_date double precision;");
		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN eligibility_min_daysbefore_end_date double precision;");
		
		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN is_anonymous_review boolean;" +
				"ALTER TABLE appraisal_details ALTER COLUMN is_anonymous_review SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN managers_level_up integer;" +
				"ALTER TABLE appraisal_details ALTER COLUMN managers_level_up SET DEFAULT 1;");
		
		alQueries.add("ALTER TABLE appraisal_details ADD COLUMN subordinates_level_down integer;" +
				"ALTER TABLE appraisal_details ALTER COLUMN subordinates_level_down SET DEFAULT 1;");

		alQueries.add("ALTER TABLE project_emp_details ADD COLUMN allocation_percent double precision;");
		alQueries.add("ALTER TABLE project_emp_details ADD COLUMN is_billed boolean;" +
				"ALTER TABLE project_emp_details ALTER COLUMN is_billed SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE project_resource_req_details ADD COLUMN wloc_ids_filter character varying;");
		alQueries.add("ALTER TABLE project_resource_req_details ADD COLUMN min_exp_filter double precision;");
		alQueries.add("ALTER TABLE project_resource_req_details ADD COLUMN max_exp_filter double precision;");
		
		alQueries.add("ALTER TABLE resource_plan_request_details ADD COLUMN requested_by integer;");
		alQueries.add("ALTER TABLE resource_plan_request_details ADD COLUMN request_date timestamp without time zone;");
		
		alQueries.add("ALTER TABLE project_resource_req_details ADD COLUMN res_req_create_status boolean;" +
				"ALTER TABLE project_resource_req_details ALTER COLUMN res_req_create_status SET DEFAULT false;");
		alQueries.add("ALTER TABLE project_resource_req_details ADD COLUMN req_created_by integer;");
		alQueries.add("ALTER TABLE project_resource_req_details ADD COLUMN req_create_date timestamp without time zone;");
		
		alQueries.add("ALTER TABLE resource_plan_request_details ADD COLUMN approved_by integer;");
		alQueries.add("ALTER TABLE resource_plan_request_details ADD COLUMN approve_date timestamp without time zone;");
		alQueries.add("ALTER TABLE resource_plan_request_details ADD COLUMN approve_comment character varying;");
		
		alQueries.add("ALTER TABLE resource_plan_request_details ADD COLUMN project_resource_req_id integer;");
		alQueries.add("ALTER TABLE resource_plan_request_details ADD COLUMN desig_id integer;");
		alQueries.add("ALTER TABLE resource_plan_request_details ADD COLUMN approve_status integer;");
		
		alQueries.add("ALTER TABLE resource_planner_details ADD COLUMN pro_resource_req integer;");
		
		alQueries.add("ALTER TABLE project_resource_req_details ADD COLUMN wloc_ids character varying;");
		alQueries.add("ALTER TABLE candidate_application_details ADD COLUMN aligned_pro_id integer;");
		
		alQueries.add("ALTER TABLE appraisal_question_answer ADD COLUMN is_submit boolean;" +
				"ALTER TABLE appraisal_question_answer ALTER COLUMN is_submit SET DEFAULT false;");
		
		alQueries.add("ALTER TABLE candidate_education_details ADD COLUMN degree_type character varying;");
		alQueries.add("ALTER TABLE candidate_education_details ADD COLUMN institute_name character varying;");
		alQueries.add("ALTER TABLE candidate_education_details ADD COLUMN university_name character varying;");
		
		alQueries.add("ALTER TABLE candidate_skills_description ADD COLUMN last_used_year integer;");
		
		alQueries.add("ALTER TABLE candidate_personal_details ADD COLUMN applied_location character varying;");
		
		alQueries.add("ALTER TABLE appraisal_reviewee_details ADD COLUMN subordinate_weightage double precision;" +
				"ALTER TABLE appraisal_reviewee_details ADD COLUMN peer_weightage double precision;" +
				"ALTER TABLE appraisal_reviewee_details ADD COLUMN other_peer_weightage double precision;" +
				"ALTER TABLE appraisal_reviewee_details ADD COLUMN supervisor_weightage double precision;" +
				"ALTER TABLE appraisal_reviewee_details ADD COLUMN grand_supervisor_weightage double precision;" +
				"ALTER TABLE appraisal_reviewee_details ADD COLUMN hod_weightage double precision;" +
				"ALTER TABLE appraisal_reviewee_details ADD COLUMN ceo_weightage double precision;" +
				"ALTER TABLE appraisal_reviewee_details ADD COLUMN hr_weightage double precision;" +
				"ALTER TABLE appraisal_reviewee_details ADD COLUMN ghr_weightage double precision;" +
				"ALTER TABLE appraisal_reviewee_details ADD COLUMN recruiter_weightage double precision;" +
				"ALTER TABLE appraisal_reviewee_details ADD COLUMN other_weightage double precision;");
		
		alQueries.add("ALTER TABLE client_details ADD COLUMN ref_client_id integer;" +
				"ALTER TABLE projectmntnc ADD COLUMN ref_pro_id integer;" +
				"ALTER TABLE activity_info ADD COLUMN ref_task_id integer;");
		
		alQueries.add("ALTER TABLE project_emp_details ADD COLUMN allocation_date date;" +
				"ALTER TABLE project_emp_details ADD COLUMN release_date date;");
		
		alQueries.add("ALTER TABLE candidate_education_details ADD COLUMN subject character varying;" +
				"ALTER TABLE candidate_education_details ADD COLUMN start_date date;" +
				"ALTER TABLE candidate_education_details ADD COLUMN completion_date date;" +
				"ALTER TABLE candidate_education_details ADD COLUMN marks double precision;" +
				"ALTER TABLE candidate_education_details ADD COLUMN city character varying;" +
				"ALTER TABLE educational_details ADD COLUMN education_type integer;" +
				"ALTER TABLE candidate_personal_details ADD COLUMN current_location character varying;" +
				"ALTER TABLE candidate_skills_description ADD COLUMN skill_type integer;");
		
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN jd_category integer;" +
				"ALTER TABLE recruitment_details ADD COLUMN technology_id integer;");
		
		alQueries.add("ALTER TABLE goal_details ADD COLUMN approve_status integer;" +
				"ALTER TABLE goal_details ALTER COLUMN approve_status SET DEFAULT 0;" +
				"ALTER TABLE goal_details ADD COLUMN approved_by integer;" +
				"ALTER TABLE goal_details ADD COLUMN approve_reason character varying;");
		
		alQueries.add("ALTER TABLE candidate_personal_details ADD COLUMN is_rejected integer");
		alQueries.add("ALTER TABLE learning_video_seen_details ADD COLUMN learning_video_seen_count character varying;");
		alQueries.add("ALTER TABLE exception_reason ADD COLUMN approve_by_reason text;");
		
		alQueries.add("ALTER TABLE skills_description ADD COLUMN is_primary_skill boolean;");
		
		alQueries.add("ALTER TABLE public.project_emp_details ADD roles_responsibility character varying NULL;");
		
		alQueries.add("ALTER TABLE public.education_details ADD completion_month integer NULL;");
		alQueries.add("ALTER TABLE public.education_details ADD specialization character varying NULL;");

		alQueries.add("ALTER TABLE public.project_resource_req_details ADD ijp_open_date timestamp without time zone NULL;");

		alQueries.add("ALTER TABLE public.project_resource_req_details ADD ijp_open_date timestamp without time zone NULL;");
		
		alQueries.add("ALTER TABLE public.employee_personal_details ADD experience_summary character varying NULL;" + 
				"ALTER TABLE public.employee_personal_details ADD resume_updated_date timestamp without time zone NULL;");
		
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD ijp_applied_resources character varying NULL;");
		
		alQueries.add("ALTER TABLE recruitment_details ADD COLUMN reopen_job_reason character varying;");
		
		alQueries.add("ALTER TABLE project_milestone_details ADD COLUMN milestone_end_date date;");
		
		
		alQueries.add("ALTER TABLE public.project_resource_req_details ADD requisition_id integer NOT NULL DEFAULT 0;");
		
		alQueries.add("ALTER TABLE public.projectmntnc ADD segment character varying NULL;");
		
		alQueries.add("ALTER TABLE public.user_type ADD access_usertype_ids character varying NULL;");

		alQueries.add("ALTER TABLE appraisal_question_answer ADD COLUMN hr_approval boolean; " + 
				" ALTER TABLE appraisal_question_answer ADD COLUMN hr_approval_comment character varying;");
		
		alQueries.add("ALTER TABLE projectmntnc ADD COLUMN project_owners character varying;");
		
		alQueries.add("ALTER TABLE projectmntnc ADD COLUMN portfolio_manager integer;\n" + 
				"ALTER TABLE projectmntnc ADD COLUMN account_manager integer;\n" + 
				"ALTER TABLE projectmntnc ADD COLUMN delivery_manager integer;\n" + 
				"ALTER TABLE projectmntnc ADD COLUMN pro_domain_id integer;\n" + 
				"ALTER TABLE projectmntnc ADD COLUMN project_acc_code character varying;\n" + 
				"ALTER TABLE projectmntnc ADD COLUMN revenue_target double precision;\n" + 
				"ALTER TABLE projectmntnc ADD COLUMN ust_project_code character varying;");
		
		alQueries.add("ALTER TABLE appraisal_question_details ADD COLUMN score_calculation_basis boolean;\n" + 
				"ALTER TABLE appraisal_question_details ALTER COLUMN score_calculation_basis SET DEFAULT false;");

		alQueries.add("");
		alQueries.add("");
		
		
		
		
		//------------------------ 7-Dec-16 ----------------------
		
		//-------------------------------22 Dec 16 ---------------------------------------------
		// From and to date in the apply reimbursement
		
		
	
		
		//-------------------------30-Mar-2017-----------------------------------------------
		/*alQueries.add("delete from orientation_member where orientation_member_id > 0");
		
		alQueries.add("insert into orientation_member (orientation_member_id,member_name,status,weightage,member_id) " +
				"values(2,'Manager','TRUE',1,2)");
		
		alQueries.add("insert into orientation_member (orientation_member_id,member_name,status,weightage,member_id) " +
		"values(3,'Self','TRUE',5,3)");
		
		alQueries.add("insert into orientation_member (orientation_member_id,member_name,status,weightage,member_id) " +
		"values(4,'Peer','TRUE',3,4)");
		
		alQueries.add("insert into orientation_member (orientation_member_id,member_name,status,weightage,member_id) " +
		"values(5,'CEO','TRUE',4,5)");
		
		alQueries.add("insert into orientation_member (orientation_member_id,member_name,status,weightage,member_id) " +
		"values(6,'Sub-ordinate','FALSE',6,6)");
		
		alQueries.add("insert into orientation_member (orientation_member_id,member_name,status,weightage,member_id) " +
		"values(7,'HR','TRUE',2,7)");
		
		alQueries.add("insert into orientation_member (orientation_member_id,member_name,status,weightage,member_id) " +
		"values(8,'GroupHead','FALSE',8,8)");
		
		alQueries.add("insert into orientation_member (orientation_member_id,member_name,status,weightage,member_id) " +
		"values(9,'Vendor','FALSE',7,9)");
		
		alQueries.add("insert into orientation_member (orientation_member_id,member_name,status,weightage,member_id) " +
		"values(10,'Anyone','TRUE',12,10)");
		
		alQueries.add("insert into orientation_member (orientation_member_id,member_name,status,weightage,member_id) " +
		"values(11,'Client','TRUE',9,11)");
		
		alQueries.add("insert into orientation_member (orientation_member_id,member_name,status,weightage,member_id) " +
		"values(12,'Managers','FALSE',11,12)");
		
		alQueries.add("insert into orientation_member (orientation_member_id,member_name,status,weightage,member_id) " +
		"values(13,'HOD','TRUE',10,13)");*/
	
		//-------------------------12-June-2017-----------------------------------------------
	  
	}

}


// Use the section to manage most of the forms in the system. Be it workflow driven or standard updations, this section will allow you to configure the forms according to your requirement.


//alQueries.add("update navigation_1 set _label='My Hub <span style=\"color: #ff8826; font-size: 10px; font-style: italic;\">(Beta)</span>' where _label='My Hub' and parent > 0");

//alQueries.add("update navigation_1 set _label_selected = 'Hub <span style=\"color: #ff8826; font-size: 10px; font-style: italic;\">(Beta)</span>', _label_unselected = 'Hub <span style=\"color: #ff8826; font-size: 10px; font-style: italic;\">(Beta)</span>' where _label = 'Hub';" +
//"update navigation_1 set _label_selected = 'My Hub <span style=\"color: #ff8826; font-size: 10px; font-style: italic;\">(Beta)</span>', _label_unselected = 'My Hub <span style=\"color: #ff8826; font-size: 10px; font-style: italic;\">(Beta)</span>' where _label = 'My Hub';");

//alQueries.add("update navigation_1 set _label_selected = 'Utility <span style=\"color: #ff8826; font-size: 10px; font-style: italic;\">(Beta)</span>', _label_unselected = 'Utility <span style=\"color: #ff8826; font-size: 10px; font-style: italic;\">(Beta)</span>' where _label = 'Utility';");


//insert into client_address (client_id, client_address) select client_id, client_address from client_details
//alQueries.add("update navigation_1 set _exist = 0 where navigation_id in (1122,722,1123,723);");

//alQueries.add("update navigation_1 set _action = 'MenuNavigationInner.action?NN=1109' where navigation_id in(1108,1109);" +
//		"update navigation_1 set _action = 'MenuNavigationInner.action?NN=1119' where navigation_id in(1119);" +
//		"update navigation_1 set _action = 'MenuNavigationInner.action?NN=1124' where navigation_id in(1124);" +
//		"update navigation_1 set _action = 'MenuNavigationInner.action?NN=1145' where navigation_id in(1145);" +
//		"update navigation_1 set _action = 'MenuNavigationInner.action?NN=1155' where navigation_id in(1155);" +
//		"update navigation_1 set _action = 'MenuNavigationInner.action?NN=1161' where navigation_id in(1161);" +
//		"update navigation_1 set _action = 'MenuNavigationInner.action?NN=1163' where navigation_id in(1163);" +
//		"update navigation_1 set _action = 'MenuNavigationInner.action?NN=2217' where navigation_id in(2216,2217);");


//alQueries.add("update navigation_1 set _label = 'Goals, KRAs, Targets, Reviews' where navigation_id in (304);" +
//		"update navigation_1 set _label = 'Goals, KRAs, Targets' where navigation_id in (793,1193);" +
//		"update navigation_1 set link_desc = 'The section allows you to setup your entire organization. This includes, Legal Entities, if they exist, Work Locations, Organization Structure, Departments and SBUs.' where navigation_id in (102);" +
//		"update navigation_1 set link_desc = 'Strategic Business Units or SBUs that are responsible for their individual profitability. Legal Entities can have multiple SBUs. This section allows for updating SBUs that the company operates out of.' where navigation_id in (105);" +
//		"update navigation_1 set link_desc = 'Functional Departments, are standard practices across all industries. Using this section one can manage such Departments. Multiple layers of Departments can be created and aligned with either SBUs and/or legal entities.' where navigation_id in (106);" +
//		"update navigation_1 set link_desc = 'An organization works with various services that are offered to the customers. These Services can be aligned with SBUs for analysis. These Services are also integrated with the Project Management module, for project alignments.' where navigation_id in (107);");

//alQueries.add("update validation_details set required_field = false where validation_details_id in (22,35,37,38,52,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97);");
//alQueries.add("update user_details set is_mobile_authorized = true");
//alQueries.add("ALTER TABLE user_details ALTER COLUMN is_mobile_authorized SET DEFAULT true;");




//need to check in notifications table which one to insert
/*		alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1002,'FALSE','FALSE','2017-07-10');");  
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1003,'FALSE','FALSE','2017-07-10');");  

alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1004,'FALSE','FALSE','2017-07-10');");  
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1005,'FALSE','FALSE','2017-07-10');"); 
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1000,'FALSE','FALSE','2017-07-10');"); 
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1001,'FALSE','FALSE','2017-07-10');"); 
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1006,'FALSE','FALSE','2017-07-10');"); 
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1007,'FALSE','FALSE','2017-07-10');"); 
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1008,'FALSE','FALSE','2017-07-10');"); 
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1009,'FALSE','FALSE','2017-07-10');"); 
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1100,'FALSE','FALSE','2017-07-10');"); 
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1101,'FALSE','FALSE','2017-07-10');"); 

alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1011,'FALSE','FALSE','2017-07-10');"); 
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1012,'FALSE','FALSE','2017-07-10');"); 
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1013,'FALSE','FALSE','2017-07-10');"); 
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1014,'FALSE','FALSE','2017-07-10');"); 
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1015,'FALSE','FALSE','2017-07-10');"); 
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1016,'FALSE','FALSE','2017-07-10');"); 
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1017,'FALSE','FALSE','2017-07-10');"); 
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1019,'FALSE','FALSE','2017-07-10');"); 
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1020,'FALSE','FALSE','2017-07-10');"); 
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1103,'FALSE','FALSE','2017-07-10');");
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1104,'FALSE','FALSE','2017-07-10');");
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1105,'FALSE','FALSE','2017-07-10');");
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1106,'FALSE','FALSE','2017-07-10');");
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1107,'FALSE','FALSE','2017-07-10');");

alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1108,'FALSE','FALSE','2017-07-10');");
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1109,'FALSE','FALSE','2017-07-10');");
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1018,'FALSE','FALSE','2017-07-10');");

alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1021,'FALSE','FALSE','2017-07-10');");
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (1010,'FALSE','FALSE','2017-07-10');");
alQueries.add("insert into notifications(notification_code,isemail,istext,entry_date)"
+"values (119,'FALSE','FALSE','2017-07-10');");*/



/*alQueries.add("delete from activity_details;");
alQueries.add("insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (1,'O','Offer',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (2,'A','Appointment',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (3,'PR','Probation',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (4,'EPR','Extend Probation',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (5,'CO','Confirmation',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (6,'TEMP','Temporary',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (7,'PERM','Permanent',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (8,'TR','Transfer',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (9,'P','Promotion',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (10,'I','Increment',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (11,'GC','Grade Change',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (12,'TRMN','Terminate',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (13,'NP','Notice Period',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (14,'WR','Withdrawn Resignation',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (15,'FF','Full & Final',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (16,'NJP','New Joinee Pending',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (17,'R','Resigned',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (18,'ESC','Employee Status Change',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (19,'UTC','User Type Change',FALSE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (20,'SA','Salary Approved',FALSE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (21,'EFFS','Exit Feedback Form Submited',FALSE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (22,'EFFA','Exit Feedback Form Approved',FALSE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (23,'HDS','Handover Document Submited',FALSE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (24,'HDA','Handover Document Approved',FALSE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (25,'HRDSA','HR Documents Save & Approved',FALSE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (26,'BF','Bonafide',FALSE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (27,'CO','Candidate Offer',FALSE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (28,'CA','Candidate Appointment',FALSE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (29,'RL','Relieving',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (30,'E','Experience',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (31,'BO','Bank Order',FALSE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (32,'LE','Life Event Increment',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (33,'I-CARD','I-CARD',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (34,'LOU','LOU',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (35,'NDA','NDA',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (36,'OLNDA','Out Location NDA',FALSE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (37,'D','Demotion',FALSE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (38,'RTR','Retirement',FALSE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (39,'EA','Employee Agreement',TRUE,NULL);" +
		"insert into activity_details (activity_id,activity_code,activity_name,isactivity,is_achievements) values (40,'APBL','Address Proof Bank Letter',TRUE,NULL);");


alQueries.add("delete from nodes;");
alQueries.add("insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (1,'Offer','D','1','1');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (2,'Appointment','D','1','2');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (3,'Confirmation','D','1','5');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (4,'Temporary','D','1','6');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (5,'Permanent','D','1','7');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (6,'Promotion','D','1','9');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (7,'Increment','D','1','10');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (8,'Grade Change','D','1','11');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (9,'Bonafide','D','1','26');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (10,'Candidate Offer','D','101','27');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (11,'Candidate Appointment','D','101','28');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (12,'Relieving','D','1','29');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (13,'Experience','D','1','30');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (14,'Bank Order','D','1','31');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (15,'Exit Form','F','1','0');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (16,'Clearance Form','F','1','0');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (17,'Life Event Increment','D','1','32');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (18,'Demotion','D','1','37');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (19,'Retirement','D','1','38');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (20,'Employee Agreement','D','1','39');" +
		"insert into nodes (node_id,node_name,node_type,module_id,mapped_activity_id) values (21,'Address Proof Bank Letter','D','1','40');");*/


/*alQueries.add("update appraisal_answer_type set appraisal_answer_type_name = 'Multiple Choice With Remark' where appraisal_answer_type_id = 1;" +
		"update appraisal_answer_type set appraisal_answer_type_name = 'Multiple Choice Without Remark' where appraisal_answer_type_id = 2;" +
		"update appraisal_answer_type set appraisal_answer_type_name = 'Grade with Excellence' where appraisal_answer_type_id = 4;" +
		"update appraisal_answer_type set appraisal_answer_type_name = 'Remark With Score' where appraisal_answer_type_id = 7;" +
		"update appraisal_answer_type set appraisal_answer_type_name = 'Remark Without Score' where appraisal_answer_type_id = 12;" +
		"update appraisal_answer_type set appraisal_answer_type_name = 'Multiple Remark With Score' where appraisal_answer_type_id = 10;");

alQueries.add("update appraisal_answer_type_sub set label = 'Grade with Excellence' where answer_type_id = 4;" +
		"update appraisal_answer_type_sub set score_label= 'Good' where appraisal_answer_type_sub_id = 7;" +
		"update appraisal_answer_type_sub set score_label= 'Average' where appraisal_answer_type_sub_id = 8;");*/

//alQueries.add("update navigation_1 set _exist=0 where navigation_id in (328,259,538,2135,2235);");


//alQueries.add("update user_type set access_usertype_ids = '1,2,3' where user_type_id =1;\n" + 
//		"update user_type set access_usertype_ids = '2,3' where user_type_id =2;\n" + 
//		"update user_type set access_usertype_ids = '3' where user_type_id =3;\n" + 
//		"update user_type set access_usertype_ids = '2,3,4' where user_type_id =4;\n" + 
//		"update user_type set access_usertype_ids = '2,3,5' where user_type_id =5;\n" + 
//		"update user_type set access_usertype_ids = '2,3,7' where user_type_id =7;\n" + 
//		"update user_type set access_usertype_ids = '2,3,11' where user_type_id =11;\n" + 
//		"update user_type set access_usertype_ids = '2,3,13' where user_type_id =13;");