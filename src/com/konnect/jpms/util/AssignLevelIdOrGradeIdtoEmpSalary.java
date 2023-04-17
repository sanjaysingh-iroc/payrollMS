package com.konnect.jpms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

public class AssignLevelIdOrGradeIdtoEmpSalary implements IStatements{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		assignLevelorGradeId();
	}

	private static void assignLevelorGradeId() { 
		
		try {
			UtilityFunctions uF = new UtilityFunctions();
			
			PreparedStatement pst = null; 
			ResultSet rs = null;
			Connection conn = null;
			
//			String HOST = "localhost";
//			String PORT = "5432";  
//			String DBUSERNAME = "postgres";
//			String DBPASSWORD = "dbPassword";
////			String DBPASSWORD = "postgres"; 
////			
//////			String []arrDBNAME = {"demo_payroll", "kp1_payroll", "kp2_payroll", "kp3_payroll"}; 
//			String []arrDBNAME = {"kp_payroll_newui"};  
			
//			String HOST 		= "192.168.1.6";
//			String PORT 		= "5432";
//			String DBUSERNAME	= "postgres";
//			String DBPASSWORD 	= "postgres";
//			String []arrDBNAME = { "development_payroll", "development_payroll_NewUI", "seinumero_payroll", "workrig_newui"};
//			
//			String HOST = "184.107.247.242";
//			String PORT = "5432";
//			String DBUSERNAME = "postgres";
//			String DBPASSWORD = "dbPassword";
			
			String HOST 		= "108.163.186.66";
			String PORT 		= "5432";
			String DBUSERNAME	= "postgres";
			String DBPASSWORD 	= "dbPassword";
//			
			String []arrDBNAME = {""};			
//			String []arrDBNAME = {"demo_payroll", "kp_payroll", "testing_payroll", "impactinfotech_payroll","seinumero_payroll",
//					"latururban_payroll", "workrig_newui", "indianoxide_payroll", "rajgurunagar_payroll", "pridedistributors_payroll"};
//			
			
//			String HOST 		= "54.152.10.99"; 
			
//			String HOST 		= "52.204.34.153"; 
//			String PORT 		= "5432";
//			String DBUSERNAME	= "postgres";
//			String DBPASSWORD 	= "dbPassword";
//			
//			String []arrDBNAME = {"demo_payroll", "kcs_payroll", "testing_payroll", "nichesoft_payroll", "medinfi_payroll", "fxkart_payroll", 
//				"xanadu_payroll", "demo_level_payroll", "demo_grade_payroll", "sandbox_payroll", "trial_payroll", 
//				"connectivitysolutions_payroll", "eurosteel_payroll", "motoitaliaa_payroll", "tabcapital_payroll", "default_payroll", 
//				"baptist_payroll", "hitech_payroll", "vittalsmedicare_payroll", "lifepoint_payroll", "trialbasic_payroll",
//				"nehaconsultancy_payroll", "mahabeej_payroll", "jfk_payroll", "chinapearl_payroll", "chavangroup_payroll", "rlfine_payroll",
//				"nexapp_payroll", "konnecttech_payroll", "workrig_newui", "craveinfotech_payroll", "jantaurban_payroll",
//				"sonyindia_payroll", "xanadu_payroll2", "srp_payroll", "gmbiocides_payroll", "sandbox_nichesoft_payroll",
//				"workrig_newui1", "booksntaxes_payroll", "hbfuller_payroll", "hbfuller_sand_box_payroll", "hbfuller_payroll_new",
//				"hbfuller_test_payroll", "hbfuller_test_payroll1", "ibsbpo_payroll", "chandantech_payroll", "rawalwasia_payroll", 
//				"testing_newui", "egovtech_payroll", "jebi_payroll", "shriji_payroll", "xanadu_test_payroll"};
//			String []arrDBNAME = {""};

			 
			for(int i=0; i<arrDBNAME.length; i++) {
				
				try {
				
					Class.forName("org.postgresql.Driver");
					conn = DriverManager.getConnection("jdbc:postgresql://" + HOST + ":"+PORT+"/" + arrDBNAME[i], DBUSERNAME, DBPASSWORD);
					
					System.out.println("======== EXECUTION STARTED ON "+arrDBNAME[i]);
					
					pst = conn.prepareStatement(selectSettings);
					rs = pst.executeQuery();
					int nSalaryStrucuterType = 0; 
					while(rs.next()){
						if(rs.getString("options").equalsIgnoreCase(O_SALARY_STRUCTURE)){
							nSalaryStrucuterType = uF.parseToInt(rs.getString("value"));
						}
					}
					rs.close();
					pst.close();
					
					if(nSalaryStrucuterType == S_GRADE_WISE){
						pst = conn.prepareStatement("select grade_id from grades_details gd, " +
								"designation_details dd, level_details ld where dd.designation_id = gd.designation_id " +
								"and ld.level_id = dd.level_id and ld.org_id in (select org_id from org_details);");
						rs = pst.executeQuery();
						List<String> alGrades = new ArrayList<String>();
						while(rs.next()){
							alGrades.add(rs.getString("grade_id"));
						}
						rs.close();
						pst.close();
						
						System.out.println("size==>"+alGrades.size());
						for(String strGradeId : alGrades){
							System.out.println("grade==>"+strGradeId);
							pst = conn.prepareStatement("update emp_salary_details set grade_id=? " +
									"where (grade_id is null or grade_id=0) and emp_id in (select emp_id " +
									"from employee_official_details where grade_id in (select grade_id " +
									"from grades_details gd, designation_details dd, level_details ld " +
									"where dd.designation_id = gd.designation_id and ld.level_id = dd.level_id " +
									"and gd.grade_id=? and ld.org_id in (select org_id from org_details)))");
							pst.setInt(1, uF.parseToInt(strGradeId));
							pst.setInt(2, uF.parseToInt(strGradeId));
							pst.execute();
							pst.close();
							
							pst = conn.prepareStatement("select count(salary_head_id) as cnt,emp_id," +
									"effective_date from emp_salary_details where emp_id in (select emp_id " +
									"from employee_official_details where grade_id in (select grade_id " +
									"from grades_details gd, designation_details dd, level_details ld " +
									"where dd.designation_id = gd.designation_id and ld.level_id = dd.level_id " +
									"and gd.grade_id=? and ld.org_id in (select org_id from org_details))) " +
									"group by emp_id,effective_date having count(salary_head_id)=1 " +
									"order by emp_id,effective_date");
							pst.setInt(1, uF.parseToInt(strGradeId));
							rs = pst.executeQuery();
							List<Map<String, String>> alEmpSalary = new ArrayList<Map<String,String>>();
							while(rs.next()){
								Map<String, String> hm = new HashMap<String, String>();
								hm.put("CNT", rs.getString("cnt"));
								hm.put("EMP_ID", rs.getString("emp_id"));
								hm.put("EFFECTIVE_DATE", uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
								alEmpSalary.add(hm);
							}
							rs.close();
							pst.close();
							
							for(Map<String, String> hm : alEmpSalary){
								pst = conn.prepareStatement("update emp_salary_details set is_approved=false " +
										"where emp_id=? and effective_date=?");
								pst.setInt(1, uF.parseToInt(hm.get("EMP_ID")));
								pst.setDate(2, uF.getDateFormat(hm.get("EFFECTIVE_DATE"), DATE_FORMAT));
								pst.execute();
								pst.close();
							}
						}
						
					} else {
						pst = conn.prepareStatement("select distinct(ld.level_id) as level_id from grades_details gd, " +
								"designation_details dd, level_details ld where dd.designation_id = gd.designation_id " +
								"and ld.level_id = dd.level_id and ld.org_id in (select org_id from org_details) " +
								"order by ld.level_id;");
						System.out.println("pst==>"+pst);
						rs = pst.executeQuery();
						List<String> alLevels = new ArrayList<String>();
						while(rs.next()){
							alLevels.add(rs.getString("level_id"));
						}
						rs.close();
						pst.close();
						
						for(String strLevelId : alLevels){
							System.out.println("strLevelId==>"+strLevelId);
							pst = conn.prepareStatement("update emp_salary_details set level_id=? " +
									"where (level_id is null or level_id=0) and emp_id in (select emp_id " +
									"from employee_official_details where grade_id in (select grade_id " +
									"from grades_details gd, designation_details dd, level_details ld " +
									"where dd.designation_id = gd.designation_id and ld.level_id = dd.level_id " +
									"and ld.level_id=? and ld.org_id in (select org_id from org_details)))");
							pst.setInt(1, uF.parseToInt(strLevelId));
							pst.setInt(2, uF.parseToInt(strLevelId));
							pst.execute();
							pst.close();
							
							pst = conn.prepareStatement("select count(salary_head_id) as cnt,emp_id," +
									"effective_date from emp_salary_details where emp_id in (select emp_id " +
									"from employee_official_details where grade_id in (select grade_id " +
									"from grades_details gd, designation_details dd, level_details ld " +
									"where dd.designation_id = gd.designation_id and ld.level_id = dd.level_id " +
									"and ld.level_id=? and ld.org_id in (select org_id from org_details))) " +
									"group by emp_id,effective_date having count(salary_head_id)=1 " +
									"order by emp_id,effective_date");
							pst.setInt(1, uF.parseToInt(strLevelId));
							rs = pst.executeQuery();
							List<Map<String, String>> alEmpSalary = new ArrayList<Map<String,String>>();
							while(rs.next()){
								Map<String, String> hm = new HashMap<String, String>();
								hm.put("CNT", rs.getString("cnt"));
								hm.put("EMP_ID", rs.getString("emp_id"));
								hm.put("EFFECTIVE_DATE", uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
								alEmpSalary.add(hm);
							}
							rs.close();
							pst.close();
							
							for(Map<String, String> hm : alEmpSalary){
								pst = conn.prepareStatement("update emp_salary_details set is_approved=false " +
										"where emp_id=? and effective_date=?");
								pst.setInt(1, uF.parseToInt(hm.get("EMP_ID")));
								pst.setDate(2, uF.getDateFormat(hm.get("EFFECTIVE_DATE"), DATE_FORMAT));
								pst.execute();
								pst.close();
							}
						}
					}
					
					
					System.out.println("========EXECUTION FINISHED ON "+arrDBNAME[i]);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(rs !=null){
						rs.close();
					}
					if(pst !=null){
						pst.close();
					}
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

}
