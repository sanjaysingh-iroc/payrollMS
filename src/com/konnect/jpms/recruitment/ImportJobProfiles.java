package com.konnect.jpms.recruitment;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.record.formula.functions.IsNa;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ImportJobProfiles extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId;
	String strEmpID;
	CommonFunctions CF = null;
	
	private File fileUpload;
	private String fileUploadFileName;
	String f_org;
	List<FillOrganisation> orgList;
	
	StringBuilder sbMessage = new StringBuilder("<ul style=\"margin:0px\">");
	public String execute() throws Exception {
		session = request.getSession();
		if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF == null)return LOGIN;
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		orgList = new FillOrganisation(request).fillOrganisation();

		if(fileUpload != null) {
			uploadRoster(fileUpload, uF);
			
			return SUCCESS;
		}
		return LOAD;
	} 

	
	private void uploadRoster(File path, UtilityFunctions uF) {
		
		//System.out.println("format2Attendance ===>> ");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		List<String> alReport = new ArrayList<String>();
		// ImportAttendance importAttendance = new ImportAttendance();

		FileInputStream fis = null;
		List<String> alErrorList = new ArrayList<String>();
		try {
			//System.out.println("in try block====");
			con = db.makeConnection(con);
			con.setAutoCommit(false);

			fis = new FileInputStream(path);
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(0);
			
			List<List<String>> outerList = new ArrayList<List<String>>();
			int maxNumOfCells = sheet.getRow(0).getLastCellNum(); // The the maximum number of columns
//			System.out.println("maxNumOfCells ===>> " + maxNumOfCells);
            Iterator rows = sheet.rowIterator();
            while (rows.hasNext()) {
                XSSFRow row = (XSSFRow) rows.next();
                Iterator cells = row.cellIterator();
                List<String> data = new ArrayList<String>();
                for(int cellCounter=0; cellCounter < maxNumOfCells; cellCounter++) { // Loop through cells
                    XSSFCell cell;
                    if(row.getCell(cellCounter) == null) {
                        cell = row.createCell(cellCounter);
                    } else {
                        cell = row.getCell(cellCounter);
                    }
                    data.add(uF.getExcelImportDataString(cell, workbook));
//                    System.out.println(cellCounter + " -- Cell val ==> "+cell.toString());
//                    data.add(cell.toString());
                }
//                System.out.println("data ===> "+data);
                outerList.add(data);
            }
            
//			System.out.println("outerList======>"+outerList);
			
			boolean flag = false;
			if (outerList.size() <= 1) {
				alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">No data available in Sheet.</li>");
				flag = false;
			} else {
				
				int depart_id=0;
				String query1 = "select depart_id from employee_official_details where emp_id=?";
				pst = con.prepareStatement(query1);
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				rs = pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
				while (rs.next()) {
					depart_id = rs.getInt(1);
				}
				rs.close();
				pst.close();
				
				Map<String,String> hmOrgId = new HashMap<String,String>();
				pst = con.prepareStatement("Select org_id,upper(org_code) as org_code from org_details ");
				rs = pst.executeQuery();
				while (rs.next()) {		
					hmOrgId.put(rs.getString("org_code"), rs.getString("org_id"));
				}
				rs.close();
				pst.close();
				
				Map<String,String> hmWLocationIds = new HashMap<String,String>();
				pst = con.prepareStatement("Select wlocation_id,upper(wlocation_name) as wlocation_name,org_id from work_location_info");
				rs = pst.executeQuery();
				while (rs.next()) {
					hmWLocationIds.put(rs.getString("wlocation_name")+"_"+rs.getString("org_id"), rs.getString("wlocation_id"));
				}
				rs.close();
				pst.close();
				
				Map<String,String> hmLevelIds = new HashMap<String,String>();
				pst = con.prepareStatement("Select level_id,upper(level_code) as level_code, org_id from level_details");
				rs = pst.executeQuery();
				while (rs.next()) {
					hmLevelIds.put(rs.getString("level_code")+"_"+rs.getString("org_id"), rs.getString("level_id"));
				}
				rs.close();
				pst.close();
//				System.out.println("hmLevelIds ===>> " + hmLevelIds);
				
				Map<String,String> hmDesignationIds = new HashMap<String,String>();
				pst = con.prepareStatement("Select designation_id,upper(designation_code) as designation_code,level_id from designation_details");
				rs = pst.executeQuery();
				while (rs.next()) {
					hmDesignationIds.put(rs.getString("designation_code")+"_"+rs.getString("level_id"), rs.getString("designation_id"));
				}
				rs.close();
				pst.close();
//				System.out.println("hmDesignationIds ===>> " + hmDesignationIds);

				Map<String,String> hmGradeIds = new HashMap<String,String>();
				pst = con.prepareStatement("Select grade_id,designation_id,upper(grade_code) as grade_code from grades_details");
				rs = pst.executeQuery();
				while (rs.next()) {
					hmGradeIds.put(rs.getString("grade_code")+"_"+rs.getString("designation_id"), rs.getString("grade_id"));
				}
				rs.close();
				pst.close();
				
				Map<String,String> hmSkillIds = new HashMap<String,String>();
				pst = con.prepareStatement("Select skill_id,org_id,upper(skill_name) as skill_name from skills_details");
				rs = pst.executeQuery();
				while (rs.next()) {
					hmSkillIds.put(rs.getString("skill_name")+"_"+rs.getString("org_id"), rs.getString("skill_id"));
				}
				rs.close();
				pst.close();
				
				Map<String,String> hmClientIds = new HashMap<String,String>();
				pst = con.prepareStatement("Select client_id,org_id,upper(client_name) as client_name from client_details");
				rs = pst.executeQuery();
				while (rs.next()) {
					hmClientIds.put(rs.getString("client_name")+"_"+rs.getString("org_id"), rs.getString("client_id"));
				}
				rs.close();
				pst.close();
				
				Map<String,String> hmEmployeeIds = new HashMap<String,String>();
				pst = con.prepareStatement("select emp_per_id,empcode,org_id from employee_personal_details epd, employee_official_details eod " +
					" where epd.emp_per_id=eod.	emp_id and eod.org_id>0 and epd.is_alive=true");
				rs = pst.executeQuery();
				while (rs.next()) {
					hmEmployeeIds.put(rs.getString("empcode")+"_"+rs.getString("org_id"), rs.getString("emp_per_id"));
				}
				rs.close();
				pst.close();
				
				List<String> alPriority = new ArrayList<String>();
				alPriority.add("LOW");
				alPriority.add("MEDIUM");
				alPriority.add("HIGH");
				
				//System.out.println("outerList======>"+outerList.toString());
				for (int k=1; k<outerList.size(); k++) {
					List<String> innerList = outerList.get(k);
					if(innerList != null && innerList.size() > 0) {
						String jobCode = innerList.get(0);
						String jobTitle = innerList.get(1);
						String org = innerList.get(2);
						String location = innerList.get(3);
						String level = innerList.get(4);
						String desig = innerList.get(5);
						String grade = innerList.get(6);
						String priority = innerList.get(7);
						String jobDescription = innerList.get(8);
						String essentialSkills = innerList.get(9);
						String desirableSkills = innerList.get(10);
						String idealCandidate = innerList.get(11);
						String minExp = innerList.get(12);
						String maxExp = innerList.get(13);
						String noOfPosition = innerList.get(14);
						String gender = innerList.get(15);
						String minAge = innerList.get(16);
						String minCTC = innerList.get(17);
						String maxCTC = innerList.get(18);
						String typeOfEmployment = innerList.get(19);
						String vacancy = innerList.get(20);
						String giveJustification = innerList.get(21);
						String reportingTo = innerList.get(22);
						String positionOpenDate = innerList.get(23);
						String targetDeadline = innerList.get(24);
						String businessBenefits = innerList.get(25);
						String hiringManagerOrRecruiter = innerList.get(26);
						String customerName = innerList.get(27);
						
						String orgId = hmOrgId.get(org.toUpperCase());
						String levelId = hmLevelIds.get(level.toUpperCase()+"_"+orgId);
//						System.out.println("levelId ===>> " + levelId);
						String desigId = hmDesignationIds.get(desig.toUpperCase()+"_"+levelId);
						
//						System.out.println("hmLevelIds ===>> " + hmLevelIds);
//						System.out.println("level ===>> " + level.toUpperCase());
//						System.out.println("desig.toUpperCase() ===>> " + desig.toUpperCase());
//						System.out.println("grade ===>> " + grade.toUpperCase());
//						System.out.println("desigId ===>> " + desigId);
						
						if(jobCode==null || jobCode.equals("")) {
							jobCode = location+""+desig;
							pst = con.prepareStatement("select count(*) as count from recruitment_details where job_code like '"+jobCode+"%'");
							rs = pst.executeQuery();
							while (rs.next()) {
								int count=uF.parseToInt(rs.getString("count"));
								count++;
								DecimalFormat decimalFormat = new DecimalFormat();
								decimalFormat.setMinimumIntegerDigits(3);
								jobCode+="-"+decimalFormat.format(count);
							}
							rs.close();
							pst.close();
						}
						
						if(uF.parseToInt(orgId)==0) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Organization Code for job title ('"+jobTitle+"') and try again.</li>");
							flag = false;
							break;
						}
	
//						System.out.println("location ===>> " + location + " -- orgId ===>> " + orgId);
//						System.out.println("hmWLocationIds ===>> " + hmWLocationIds);
						if(uF.parseToInt(hmWLocationIds.get(location.toUpperCase()+"_"+orgId))==0) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Work Location Name for job title ('"+jobTitle+"') and try again.</li>");
							flag = false;
							break;
						}
						
						if(uF.parseToInt(levelId)==0) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Level Code for job title ('"+jobTitle+"') and try again.</li>");
							flag = false;
							break;
						}
						
						if(uF.parseToInt(desigId)==0) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Designation Code for job title ('"+jobTitle+"') and try again.</li>");
							flag = false;
							break;
						}
						
//						System.out.println(grade + " -- desigId ===>> " + desigId + " -- hmGradeIds ===>> " + hmGradeIds);
						if(uF.parseToInt(hmGradeIds.get(grade+"_"+desigId))==0) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Grade Code for job title ('"+jobTitle+"') and try again.</li>");
							flag = false;
							break;
						}
						
						if(!alPriority.contains(priority)) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Priority (HIGH, MEDIUM, LOW) for job title ('"+jobTitle+"') and try again.</li>");
							flag = false;
							break;
						}
						
						List<String> alEssentialSkills = new ArrayList<String>();
						if(essentialSkills!=null) {
							alEssentialSkills = Arrays.asList(essentialSkills.split("::"));
						}
						StringBuilder sbEssentialSkills = null;
						for(int i=0; i<alEssentialSkills.size(); i++) {
							if(uF.parseToInt(hmSkillIds.get(alEssentialSkills.get(i).toUpperCase()+"_"+orgId))==0) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Essential Skills for job title ('"+jobTitle+"') and try again.</li>");
								flag = false;
								break;
							}
							if(sbEssentialSkills==null) {
								sbEssentialSkills = new StringBuilder();
								sbEssentialSkills.append(","+hmSkillIds.get(alEssentialSkills.get(i).toUpperCase()+"_"+orgId)+",");
							} else {
								sbEssentialSkills.append(hmSkillIds.get(alEssentialSkills.get(i).toUpperCase()+"_"+orgId)+",");
							}
						}
						
						List<String> alDesirableSkills = new ArrayList<String>();
						if(desirableSkills!=null) {
							alDesirableSkills = Arrays.asList(desirableSkills.split("::"));
						}
						StringBuilder sbDesirableSkills = null;
						for(int i=0; i<alDesirableSkills.size(); i++) {
							if(uF.parseToInt(hmSkillIds.get(alDesirableSkills.get(i).toUpperCase()+"_"+orgId))==0) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Desirable Skills for job title ('"+jobTitle+"') and try again.</li>");
								flag = false;
								break;
							}
							if(sbDesirableSkills==null) {
								sbDesirableSkills = new StringBuilder();
								sbDesirableSkills.append(","+hmSkillIds.get(alDesirableSkills.get(i).toUpperCase()+"_"+orgId)+",");
							} else {
								sbDesirableSkills.append(hmSkillIds.get(alDesirableSkills.get(i).toUpperCase()+"_"+orgId)+",");
							}
						}
						
						if(minExp!=null && minExp.length()>0 && !uF.isNumeric(minExp)) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Min Experience for job title ('"+jobTitle+"') and try again.</li>");
							flag = false;
							break;
						}
						
						if(maxExp!=null && maxExp.length()>0 && !uF.isNumeric(maxExp)) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Max Experience for job title ('"+jobTitle+"') and try again.</li>");
							flag = false;
							break;
						}
						
						if(uF.parseToInt(noOfPosition)==0) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the No of Position for job title ('"+jobTitle+"') and try again.</li>");
							flag = false;
							break;
						}
						
						String strGender = uF.getGender(gender);
						if(gender!=null && gender.length()>0 && strGender.equals("-")) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Gender (Any, Male, Female) for job title ('"+jobTitle+"') and try again.</li>");
							flag = false;
							break;
						}
						
						if(minAge!=null && minAge.length()>0 && !uF.isNumeric(minAge)) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Min Age for job title ('"+jobTitle+"') and try again.</li>");
							flag = false;
							break;
						}
						
						if(minCTC!=null && minCTC.length()>0 && !uF.isNumeric(minCTC)) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Min CTC for job title ('"+jobTitle+"') and try again.</li>");
							flag = false;
							break;
						}
						
						if(maxCTC!=null && maxCTC.length()>0 && !uF.isNumeric(maxCTC)) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Max CTC for job title ('"+jobTitle+"') and try again.</li>");
							flag = false;
							break;
						}
						
						String strEmpTypeCode = uF.getEmploymentTypeCode(typeOfEmployment);
						if(strEmpTypeCode==null || strEmpTypeCode.length()==0 || strEmpTypeCode.equals("-")) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Employment Type for job title ('"+jobTitle+"') and try again.</li>");
							flag = false;
							break;
						}
						
						String strVacancy = uF.getVacancyType(vacancy);
						if(strVacancy!=null && strVacancy.length()>0 && uF.parseToInt(strVacancy)==0) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Vacancy for job title ('"+jobTitle+"') and try again.</li>");
							flag = false;
							break;
						}
						
						List<String> alEmployeeCodes = new ArrayList<String>();
						if(reportingTo!=null && reportingTo.trim().length()>0) {
							alEmployeeCodes = Arrays.asList(reportingTo.split("::"));
						}
//						System.out.println("alEmployeeCodes ===>> " + alEmployeeCodes);
						StringBuilder sbReportingTo = null;
						for(int i=0; i<alEmployeeCodes.size(); i++) {
							if(uF.parseToInt(hmEmployeeIds.get(alEmployeeCodes.get(i).toUpperCase()+"_"+orgId))==0) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the reporting Employee Code for job title ('"+jobTitle+"') and try again.</li>");
								flag = false;
								break;
							}
							if(sbReportingTo==null) {
								sbReportingTo = new StringBuilder();
								sbReportingTo.append(","+hmEmployeeIds.get(alEmployeeCodes.get(i).toUpperCase()+"_"+orgId)+",");
							} else {
								sbReportingTo.append(hmEmployeeIds.get(alEmployeeCodes.get(i).toUpperCase()+"_"+orgId)+",");
							}
						}
						
//						System.out.println("positionOpenDate ===>> " + positionOpenDate);
//						System.out.println("targetDeadline ===>> " + targetDeadline);
						if(positionOpenDate==null || (positionOpenDate.length()>0 && !uF.isThisDatePatternMatch(positionOpenDate))) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Position Open Date format (dd/mm/yyyy) for job title ('"+jobTitle+"') and try again.</li>");
							flag = false;
							break;
						}
						
						if(targetDeadline==null || (targetDeadline.length()>0 && !uF.isThisDatePatternMatch(targetDeadline))) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Target Deadline format (dd/mm/yyyy) for job title ('"+jobTitle+"') and try again.</li>");
							flag = false;
							break;
						}

						List<String> alHiringManagerCodes = new ArrayList<String>();
						if(hiringManagerOrRecruiter!=null && hiringManagerOrRecruiter.length()>0) {
							alHiringManagerCodes = Arrays.asList(hiringManagerOrRecruiter.split("::"));
						}
						StringBuilder sbHiringMgr = null;
						for(int i=0; i<alHiringManagerCodes.size(); i++) {
							if(uF.parseToInt(hmEmployeeIds.get(alHiringManagerCodes.get(i).toUpperCase()+"_"+orgId))==0) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Hiring Manager Or Recruiter Employee Code for job title ('"+jobTitle+"') and try again.</li>");
								flag = false;
								break;
							}
							if(sbHiringMgr==null) {
								sbHiringMgr = new StringBuilder();
								sbHiringMgr.append(","+hmEmployeeIds.get(alHiringManagerCodes.get(i).toUpperCase()+"_"+orgId)+",");
							} else {
								sbHiringMgr.append(hmEmployeeIds.get(alHiringManagerCodes.get(i).toUpperCase()+"_"+orgId)+",");
							}
						}
						
						if(customerName!=null && customerName.length()>0 && uF.parseToInt(hmClientIds.get(customerName.toUpperCase()+"_"+orgId))==0) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Client Name for job title ('"+jobTitle+"') and try again.</li>");
							flag = false;
							break;
						}

						String query = "insert into recruitment_details(designation_id,grade_id,no_position,effective_date,comments,wlocation," +
							"entry_date,added_by,skills,services,dept_id,level_id,priority_job_int,target_deadline,org_id,ideal_candidate," +
							"requirement_status,type_of_employment,vacancy_type,give_justification,replacement_person_ids,temp_casual_give_jastification," +
							"essential_skills,reporting_to_person_ids,reportto_type,sex,age,job_title,customer_id,hiring_manager,job_description," +
							"min_exp,max_exp,min_ctc,max_ctc,job_code,job_approval_status,status) " + //
							" values(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?)"; //
						pst = con.prepareStatement(query);
						pst.setInt(1, uF.parseToInt(desigId));
						pst.setInt(2, uF.parseToInt(hmGradeIds.get(grade)+"_"+desigId));
						pst.setInt(3, uF.parseToInt(noOfPosition));
						pst.setDate(4, uF.getDateFormat(positionOpenDate, DATE_FORMAT));
						pst.setString(5, businessBenefits);
						pst.setInt(6, uF.parseToInt(hmWLocationIds.get(location.toUpperCase()+"_"+orgId)));
						pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(8, uF.parseToInt(strSessionEmpId));
	//					System.out.println("IN insert getSkillName ===> "+getSkillsName(getSkills()));
						if(sbDesirableSkills!=null) {
							pst.setString(9, sbDesirableSkills.toString());
						} else {
							pst.setString(9, null);
						}
						pst.setInt(10, uF.parseToInt(""));
						pst.setInt(11, depart_id);
						pst.setInt(12, uF.parseToInt(levelId));
						pst.setInt(13, uF.parseToInt(priority));
						pst.setDate(14,uF.getDateFormat(targetDeadline, DATE_FORMAT));
						pst.setInt(15, uF.parseToInt(orgId));
						pst.setString(16, idealCandidate);
						pst.setString(17, "generate");
						pst.setString(18, strEmpTypeCode);
						pst.setInt(19, uF.parseToInt(strVacancy));
						pst.setString(20, giveJustification);
						pst.setString(21, "");
						pst.setString(22, null);
						if(sbEssentialSkills!=null) {
							pst.setString(23, sbEssentialSkills.toString());
						} else {
							pst.setString(23, null);
						}
						if(sbReportingTo!= null) {
							pst.setString(24, sbReportingTo.toString());
						} else {
							pst.setString(24, null);
						}
						pst.setString(25, "Other");
						pst.setString(26, strGender);
						pst.setString(27, minAge);
						pst.setString(28, jobTitle);
						pst.setInt(29, uF.parseToInt(hmClientIds.get(customerName.toUpperCase()+"_"+orgId)));
						if(sbHiringMgr==null) {
							pst.setString(30, null);
						} else {
							pst.setString(30, sbHiringMgr.toString());
						}
						pst.setString(31, jobDescription);
						pst.setDouble(32, uF.parseToDouble(minExp));
						pst.setDouble(33, uF.parseToDouble(maxExp));
						pst.setDouble(34, uF.parseToDouble(minCTC));
						pst.setDouble(35, uF.parseToDouble(maxCTC));
						pst.setString(36, jobCode);
						pst.setInt(37, 1);
						pst.setInt(38, 1);
//						System.out.println("pst insert recruitment ==> " + pst);
						int x = pst.executeUpdate();
						pst.close();
						
						if(x>0) {
							flag = true;
						}
//						System.out.println("emp_per_id ===>> " + emp_per_id);
					} else {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the excel sheet.</li>");
//						System.out.println("flag 9 ===>> " + flag);
						flag = false;
						break;
					}
					//System.out.println("inner loop ends ");
				}// end main for loop
				
			}
			System.out.println("flag ===>> " + flag);
			if (flag) {
				con.commit();
				sbMessage.append("<li class=\"msg savesuccess\" style=\"margin:0px\">Job Profiles Imported Successfully!</li>");
				sbMessage.append("</ul>");
				System.out.println("sbMessage in commit else if ===>> " + sbMessage.toString());
				request.setAttribute("sbMessage", sbMessage.toString());
//				session.setAttribute(MESSAGE, SUCCESSM+ "Job Profiles Imported Successfully!" + END);
			} else {
				System.out.println("in rollback");
				con.rollback();
//				if (alErrorList.size() > 0) {
//					alReport.add(alErrorList.get(0));
//				}
				System.out.println("alReport ===>> " + alReport);
//				request.setAttribute("alReport", alReport);
				if(alErrorList.size()>0){
					sbMessage.append(alErrorList.get(alErrorList.size()-1));
				}
				sbMessage.append("</ul>");
				session.setAttribute("sbMessage", sbMessage.toString());
				
//				session.setAttribute(MESSAGE,ERRORM+ "Job Profiles not imported. Please check imported file."+ END);
			}
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			System.out.println("in catch rollback");
			/*if (alErrorList.size() > 0) {
				alReport.add(alErrorList.get(0));
			}*/
//			request.setAttribute("alReport", alReport);
			sbMessage.append("<li class=\"msg_error\" style=\"margin:0px\">Job Profiles not imported. Please check imported file.!</li>");
			sbMessage.append("</ul>");
			System.out.println("sbMessage in commit else if ===>> " + sbMessage.toString());
			request.setAttribute("sbMessage", sbMessage.toString());
//			session.setAttribute(MESSAGE, ERRORM+ "Roster not imported. Please check imported file."+ END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
			try {
				fis.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			System.gc();
		}
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public File getFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(File fileUpload) {
		this.fileUpload = fileUpload;
	}

	public String getFileUploadFileName() {
		return fileUploadFileName;
	}

	public void setFileUploadFileName(String fileUploadFileName) {
		this.fileUploadFileName = fileUploadFileName;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

}