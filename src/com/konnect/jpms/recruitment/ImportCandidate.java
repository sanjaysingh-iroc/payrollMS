package com.konnect.jpms.recruitment;

import java.io.File;
import java.io.FileInputStream;
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

public class ImportCandidate extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId;
	String strUserType;
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
		strUserType = (String) session.getAttribute(USERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
		orgList = new FillOrganisation(request).fillOrganisation();

		if(fileUpload != null) {
			upload8StepCandidate(fileUpload, uF);
			
			return SUCCESS;
		}
		return LOAD;
	} 

	
	private void upload8StepCandidate(File path, UtilityFunctions uF) {
		
		System.out.println("upload8StepCandidate ===>> ");
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
			
			List<List<List<String>>> allOuterList = new ArrayList<List<List<String>>>();
			for(int i=0; i<7; i++) {
				XSSFSheet sheet = workbook.getSheetAt(i);
				List<List<String>> outerList = new ArrayList<List<String>>();
				int maxNumOfCells = sheet.getRow(1).getLastCellNum(); // The the maximum number of columns
				System.out.println(i + " -- maxNumOfCells ===>> " + maxNumOfCells);
	            Iterator rows = sheet.rowIterator();
	            int rowCnt=0;
	            while (rows.hasNext()) {
//	            	if(rowCnt==0) {
//	            		continue;
//	            	}
	            	rowCnt++;
	                XSSFRow row = (XSSFRow) rows.next();
	                List<String> data = new ArrayList<String>();
	                for(int cellCounter=0; cellCounter < maxNumOfCells; cellCounter++) { // Loop through cells
	                    XSSFCell cell;
	                    if(row.getCell(cellCounter) == null) {
	                        cell = row.createCell(cellCounter);
	                    } else {
	                        cell = row.getCell(cellCounter);
	                    }
	                    data.add(uF.getExcelImportDataString(cell, workbook));
	                }
	//				System.out.println("data ===> "+data);
	                if(rowCnt>1) {
	                	outerList.add(data);
	                }
	            }
				System.out.println("outerList ======>> " + outerList);
				allOuterList.add(outerList);
			}
			System.out.println("allOuterList ======>> " + allOuterList);
			
			
			
			Map<String,String> hmCandiIds = new HashMap<String,String>();
			Map<String,String> hmCandiIdsWOJD = new HashMap<String,String>();
			// Created by Dattatray Date : 07-July-21 Note : spelling mistake flase to false
			pst = con.prepareStatement("select distinct(emp_per_id),upper(empcode) as empcode,emp_fname,emp_lname,upper(rd.job_code) as job_code from candidate_personal_details cpd, candidate_application_details cad, " +
				"recruitment_details rd where cpd.emp_per_id = cad.candidate_id and cad.recruitment_id = rd.recruitment_id and rd.close_job_status=false and (empcode !='') order by emp_per_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmCandiIds.put(rs.getString("empcode")+"_"+rs.getString("job_code"), rs.getString("emp_per_id"));
				hmCandiIdsWOJD.put(rs.getString("empcode"), rs.getString("emp_per_id"));
			}
			rs.close();
			pst.close();
			
			Map<String,String> hmJobProfileIds = new HashMap<String,String>();
			// Created By Dattatray Date : 07-July-2021 Note : rd.job_code to job_code and flase to false
			pst = con.prepareStatement("select upper(job_code) as job_code,recruitment_id from recruitment_details where close_job_status=false");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmJobProfileIds.put(rs.getString("job_code"), rs.getString("recruitment_id"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmState = new HashMap<String,String>();
			pst = con.prepareStatement("select upper(state_name) as state_name,state_id from state");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmState.put(rs.getString("state_name"), rs.getString("state_id"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmCountry = new HashMap<String,String>();
			// Created By Dattatray Date : 07-July-21 from SELECT * FROM country to from country
//			pst = con.prepareStatement("select upper(country_name) as country_name,country_id from SELECT * FROM country");
			pst = con.prepareStatement("select upper(country_name) as country_name,country_id from country");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmCountry.put(rs.getString("country_name"), rs.getString("country_id"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmDegreeId = new HashMap<String,String>();
			pst = con.prepareStatement("select upper(education_name) as education_name,edu_id from educational_details where education_name != ''");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmDegreeId.put(rs.getString("education_name"), rs.getString("edu_id"));
			}
			rs.close();
			pst.close();
			
			Map<String,String> hmJDOrgId = new HashMap<String,String>();
			Map<String,String> hmJDId = new HashMap<String,String>();
			pst = con.prepareStatement("select upper(job_code) as job_code,org_id from recruitment_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmJDOrgId.put(rs.getString("job_code"), rs.getString("org_id"));
				hmJDId.put(rs.getString("job_code"), rs.getString("org_id"));
			}
			rs.close();
			pst.close();
			
			Map<String,String> hmSkillIds = new HashMap<String,String>();
			pst = con.prepareStatement("Select skill_id,org_id,upper(skill_name) as skill_name from skills_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmSkillIds.put(rs.getString("skill_name"), rs.getString("skill_id")); //+"_"+rs.getString("org_id")
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmDegreeDuration = new HashMap<String,String>();
			hmDegreeDuration.put("1", "1");
			hmDegreeDuration.put("2", "2");
			hmDegreeDuration.put("3", "3");
			hmDegreeDuration.put("4", "4");
			hmDegreeDuration.put("5", "5");
			
			Map<String, String> hmDegreeCompletionYear = new HashMap<String,String>();			
			int currentYear = uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"));
			for(int i=currentYear; i > currentYear-50; i--) {
				hmDegreeCompletionYear.put(i+"", i+"");
			}
			
			List<String> alGenderList = new ArrayList<String>();
			alGenderList.add("M");
			alGenderList.add("F");
			alGenderList.add("O");
			
			Map<String, String> hmSourceIds = uF.getSourcesIdMap();
			boolean flag = false;
			List<String> alCandiAndJDCode = new ArrayList<String>();
			List<String> alCandiCode = new ArrayList<String>();
			List<List<String>> alCandiInfo = new ArrayList<List<String>>();
			List<List<String>> alCandiExpInfo = new ArrayList<List<String>>();
			List<List<String>> alCandiEduInfo = new ArrayList<List<String>>();
			List<List<String>> alCandiSkillInfo = new ArrayList<List<String>>();
			List<List<String>> alCandiLangInfo = new ArrayList<List<String>>();
			List<List<String>> alCandiHobiesInfo = new ArrayList<List<String>>();
			List<List<String>> alCandiFamilyInfo = new ArrayList<List<String>>();
			
			for(int i=0; i<allOuterList.size(); i++) {
				List<List<String>> outerList = allOuterList.get(i);
				if (i==0 && outerList.size() <= 1) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">No data available in Sheet.</li>");
					flag = false;
					break;
				} else {
					if(i==0) {
						for (int k=1; k<outerList.size(); k++) {
							List<String> innerList = outerList.get(k);
							
							if(innerList != null && innerList.size() > 0) {
								
								if(innerList.get(0)!=null && innerList.get(0).trim().length()>0 && uF.parseToInt(hmJobProfileIds.get(innerList.get(0).trim().toUpperCase()))==0) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Job Profile Id for Candidate ('"+innerList.get(3)+"_"+innerList.get(4)+"_"+innerList.get(5)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(1)==null || innerList.get(1).length()==0) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate Id  for Candidate ('"+innerList.get(3)+"_"+innerList.get(4)+"_"+innerList.get(5)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(1)!=null && innerList.get(0)!=null && alCandiAndJDCode.contains(innerList.get(1).trim().toUpperCase()+"_"+innerList.get(0).trim().toUpperCase()) && hmCandiIds.containsKey(innerList.get(1).trim().toUpperCase()+"_"+innerList.get(0).trim().toUpperCase())) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate Id for Candidate ('"+innerList.get(3)+"_"+innerList.get(4)+"_"+innerList.get(5)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(1)!=null && innerList.get(0)==null && alCandiCode.contains(innerList.get(1).trim().toUpperCase()) && hmCandiIdsWOJD.containsKey(innerList.get(1).trim().toUpperCase())) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate Id for Candidate ('"+innerList.get(3)+"_"+innerList.get(4)+"_"+innerList.get(5)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(6)!=null && innerList.get(6).length()>0 && !innerList.get(6).contains("@")) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Mail Id for Candidate ('"+innerList.get(3)+"_"+innerList.get(4)+"_"+innerList.get(5)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(10)!=null && innerList.get(10).trim().length()>0 && uF.parseToInt(hmCountry.get(innerList.get(10).trim().toUpperCase()))==0) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Permanent Address Country Name for Candidate ('"+innerList.get(3)+"_"+innerList.get(4)+"_"+innerList.get(5)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(11)!=null && innerList.get(11).trim().length()>0 && uF.parseToInt(hmState.get(innerList.get(11).trim().toUpperCase()))==0) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Permanent Address State Name for Candidate ('"+innerList.get(3)+"_"+innerList.get(4)+"_"+innerList.get(5)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(16)!=null && innerList.get(16).trim().length()>0 && uF.parseToInt(hmCountry.get(innerList.get(16).trim().toUpperCase()))==0) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Temporary Address Country Name for Candidate ('"+innerList.get(3)+"_"+innerList.get(4)+"_"+innerList.get(5)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(17)!=null && innerList.get(17).trim().length()>0 && uF.parseToInt(hmState.get(innerList.get(17).trim().toUpperCase()))==0) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Temporary Address State Name for Candidate ('"+innerList.get(3)+"_"+innerList.get(4)+"_"+innerList.get(5)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(22)!=null && innerList.get(22).length()>0 && !uF.isThisDatePatternMatch(innerList.get(22))) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Passport Expiry Date for Candidate ('"+innerList.get(3)+"_"+innerList.get(4)+"_"+innerList.get(5)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(24)!=null && innerList.get(24).length()>0 && !uF.isThisDatePatternMatch(innerList.get(24))) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Date of Birth for Candidate ('"+innerList.get(3)+"_"+innerList.get(4)+"_"+innerList.get(5)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(25)==null || !alGenderList.contains(uF.getGenderCode(innerList.get(25).trim()))) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Gender for Candidate ('"+innerList.get(3)+"_"+innerList.get(4)+"_"+innerList.get(5)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(27)!=null && innerList.get(27).length()>0 && !uF.isThisDatePatternMatch(innerList.get(27))) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Date of Marriage for Candidate ('"+innerList.get(3)+"_"+innerList.get(4)+"_"+innerList.get(5)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(29)!=null && innerList.get(29).length()>0 && hmSourceIds.get(innerList.get(29))==null) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Source Name (HR Manager, Recruiter, Website, Online Job Portal, Social Sites, Consultant, Walk-in, Other) for Candidate ('"+innerList.get(3)+"_"+innerList.get(4)+"_"+innerList.get(5)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(32)!=null && innerList.get(32).length()>0 && !uF.isNumeric(innerList.get(32))) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Current CTC for Candidate ('"+innerList.get(3)+"_"+innerList.get(4)+"_"+innerList.get(5)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(33)!=null && innerList.get(33).length()>0 && !uF.isNumeric(innerList.get(33))) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Expected CTC for Candidate ('"+innerList.get(3)+"_"+innerList.get(4)+"_"+innerList.get(5)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(34)!=null && innerList.get(34).length()>0 && !uF.isNumeric(innerList.get(34))) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Notice Period for Candidate ('"+innerList.get(3)+"_"+innerList.get(4)+"_"+innerList.get(5)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(39)!=null && innerList.get(39).length()>0 && !innerList.get(39).contains("@")) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the First Candidate Reference Mail Id for Candidate ('"+innerList.get(3)+"_"+innerList.get(4)+"_"+innerList.get(5)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(44)!=null && innerList.get(44).length()>0 && !innerList.get(44).contains("@")) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Second Candidate Reference Mail Id for Candidate ('"+innerList.get(3)+"_"+innerList.get(4)+"_"+innerList.get(5)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(48)!=null && !innerList.get(48).trim().equals("") && !uF.isThisDateTimePatternMatch(innerList.get(48))) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate's Availability option 1 Date Time (dd/mm/yyyy hh:mm) for Candidate Id ('"+innerList.get(0)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(49)!=null && !innerList.get(49).trim().equals("") && !uF.isThisDateTimePatternMatch(innerList.get(49))) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate's  Availability option 2 Date Time (dd/mm/yyyy hh:mm) for Candidate Id ('"+innerList.get(0)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(50)!=null && !innerList.get(50).trim().equals("") && !uF.isThisDateTimePatternMatch(innerList.get(50))) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate's  Availability option 3 Date Time (dd/mm/yyyy hh:mm) for Candidate Id ('"+innerList.get(0)+"') and try again.</li>");
									flag = false;
									break;
								}
								//flag = true;
								alCandiAndJDCode.add(innerList.get(1)+"_"+innerList.get(0));
								alCandiCode.add(innerList.get(1));
							}
							
						}
						alCandiInfo = outerList;
						//System.out.println("alCandiInfo :  " +alCandiInfo);
					} else if(i==1) {
						for (int k=1; k<outerList.size(); k++) {
							List<String> innerList = outerList.get(k);
							
							if(innerList != null && innerList.size() > 0) {
								// Created By dattatray Date : 08-July-2021 Note : !alCandiCode.contains(innerList.get(0).trim().toUpperCase()) checked
								if(innerList.get(0)!=null && innerList.get(0).trim().length()>0 && !alCandiCode.contains(innerList.get(0).trim().toUpperCase())) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate Id ('"+innerList.get(0)+"') in sheet Candidate Experience Info, this candidate id does not exist.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(5)!=null && innerList.get(5).trim().length()>0 && uF.parseToInt(hmState.get(innerList.get(5).trim().toUpperCase()))==0) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate's Previous Employment State Name for Candidate Id ('"+innerList.get(0)+"') & Company Name ('"+innerList.get(2)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(6)!=null && innerList.get(6).trim().length()>0 && uF.parseToInt(hmCountry.get(innerList.get(6).trim().toUpperCase()))==0) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate's Previous Employment Country Name for Candidate Id ('"+innerList.get(0)+"') & Company Name ('"+innerList.get(2)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(12)==null || !uF.isThisDatePatternMatch(innerList.get(12))) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate's Previous Employment Start Date for Candidate Id ('"+innerList.get(0)+"') & Company Name ('"+innerList.get(2)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(13)==null || !uF.isThisDatePatternMatch(innerList.get(13))) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate's Previous Employment End Date for Candidate Id ('"+innerList.get(0)+"') & Company Name ('"+innerList.get(2)+"') and try again.</li>");
									flag = false;
									break;
								}
								//flag = true;
							}
						}
						alCandiExpInfo = outerList;
						//System.out.println("alCandiExpInfo :  " +alCandiExpInfo);
					} else if(i==2) {
						for (int k=1; k<outerList.size(); k++) {
							List<String> innerList = outerList.get(k);
							if(innerList != null && innerList.size() > 0) {
								// Created By dattatray Date : 08-July-2021 Note : !alCandiCode.contains(innerList.get(0).trim().toUpperCase()) checked
								if(innerList.get(0)!=null && innerList.get(0).trim().length()>0 && !alCandiCode.contains(innerList.get(0).trim().toUpperCase())) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate Id ('"+innerList.get(0)+"') in sheet Candidate Education Info, this candidate id does not exist.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(1)!=null && innerList.get(1).trim().length()>0 && uF.parseToInt(hmDegreeId.get(innerList.get(1).trim().toUpperCase()))==0) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Degree Name for Candidate Id ('"+innerList.get(0)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(5)!=null && innerList.get(5).length()>0 && uF.parseToInt(hmDegreeDuration.get(innerList.get(5)))==0) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Degree Duration for Candidate Id ('"+innerList.get(0)+"') & Degree Name ('"+innerList.get(1)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(6)!=null && innerList.get(6).length()>0 && uF.parseToInt(hmDegreeCompletionYear.get(innerList.get(6)))==0) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Degree Completion Year for Candidate Id ('"+innerList.get(0)+"') & Degree Name ('"+innerList.get(1)+"') and try again.</li>");
									flag = false;
									break;
								}
								//flag = true;
							}
						}
						alCandiEduInfo = outerList;
						//System.out.println("alCandiEduInfo :  " +alCandiEduInfo);
					} else if(i==3) {
						for (int k=1; k<outerList.size(); k++) {
							List<String> innerList = outerList.get(k);
							if(innerList != null && innerList.size() > 0) {
								// Created By dattatray Date : 08-July-2021 Note : !alCandiCode.contains(innerList.get(0).trim().toUpperCase()) checked
								if(innerList.get(0)!=null && innerList.get(0).trim().length()>0 && !alCandiCode.contains(innerList.get(0).trim().toUpperCase())) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate Id ('"+innerList.get(0)+"') in sheet Skills, this candidate id does not exist.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(1)==null || uF.parseToInt(hmSkillIds.get(innerList.get(1).trim().toUpperCase()))==0) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Skill Name for Candidate Id ('"+innerList.get(0)+"') and try again.</li>");
									flag = false;
									break;
								}
								//flag = true;
							}
						}
						alCandiSkillInfo = outerList;
						//System.out.println("alCandiSkillInfo :  " +alCandiSkillInfo);
					} else if(i==4) {
						for (int k=1; k<outerList.size(); k++) {
							List<String> innerList = outerList.get(k);
							if(innerList != null && innerList.size() > 0) {
								// Created By dattatray Date : 08-July-2021 Note : !alCandiCode.contains(innerList.get(0).trim().toUpperCase()) checked
								if(innerList.get(0)!=null && innerList.get(0).trim().length()>0 && !alCandiCode.contains(innerList.get(0).trim().toUpperCase())) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate Id ('"+innerList.get(0)+"') in sheet Language, this candidate id does not exist.</li>");
									flag = false;
									break;
								}
								//flag = true;
							}
						}
						alCandiLangInfo = outerList;
						//System.out.println("alCandiLangInfo :  " +alCandiLangInfo);
					} else if(i==5) {
						for (int k=1; k<outerList.size(); k++) {
							List<String> innerList = outerList.get(k);
							if(innerList != null && innerList.size() > 0) {
								// Created by Dattatray Date : 08-July-2021 Note: !alCandiCode.contains(innerList.get(0).trim().toUpperCase()) checked
								if(innerList.get(0)!=null && innerList.get(0).trim().length()>0 && !alCandiCode.contains(innerList.get(0).trim().toUpperCase())) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate Id ('"+innerList.get(0)+"') in sheet Hobbies, this candidate id does not exist.</li>");
									flag = false;
									break;
								}
								//flag = true;
							}
						}
						alCandiHobiesInfo = outerList;// Created By Dattatray Date : 07-July-2021 Note : alCandiLangInfo to alCandiHobiesInfo
						System.out.println("alCandiHobiesInfo :  " +alCandiHobiesInfo);
					} else if(i==6) {
						for (int k=1; k<outerList.size(); k++) {
							List<String> innerList = outerList.get(k);
							if(innerList != null && innerList.size() > 0) {
								System.out.println("alCandiCode.contains(innerList.get(0).trim().toUpperCase()) : "+ (!alCandiCode.contains(innerList.get(0).trim().toUpperCase())));
								// Created by Dattatray Date : 08-July-2021 Note: !alCandiCode.contains(innerList.get(0).trim().toUpperCase()) checked
								if(innerList.get(0)!=null && innerList.get(0).trim().length()>0 && !alCandiCode.contains(innerList.get(0).trim().toUpperCase())) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate Id ('"+innerList.get(0)+"') in sheet Candidate Family Info, this candidate id does not exist.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(2)!=null && !uF.isThisDatePatternMatch(innerList.get(2))) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate's Father's Date of Birth for Candidate Id ('"+innerList.get(0)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(6)!=null && innerList.get(6).length()>0 && !innerList.get(6).contains("@")) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate's Father's Mail Id for Candidate Id ('"+innerList.get(0)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(8)!=null && !uF.isThisDatePatternMatch(innerList.get(8))) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate's Mother's Date of Birth for Candidate Id ('"+innerList.get(0)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(12)!=null && innerList.get(12).length()>0 && !innerList.get(12).contains("@")) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate's Mother's Mail Id for Candidate Id ('"+innerList.get(0)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(14)!=null && !uF.isThisDatePatternMatch(innerList.get(14))) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate's Spouse's Date of Birth for Candidate Id ('"+innerList.get(0)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(18)!=null && innerList.get(18).length()>0 && !innerList.get(18).contains("@")) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate's Spouse's Mail Id for Candidate Id ('"+innerList.get(0)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(19)==null || !alGenderList.contains(uF.getGenderCode(innerList.get(19).trim()))) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate's Spouse's Gender for Candidate Id ('"+innerList.get(0)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(21)!=null && !uF.isThisDatePatternMatch(innerList.get(21))) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate's Sibling's Date of Birth for Candidate Id ('"+innerList.get(0)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(25)!=null && innerList.get(25).length()>0 && !innerList.get(25).contains("@")) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate's Sibling's Mail Id for Candidate Id ('"+innerList.get(0)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								if(innerList.get(26)==null || !alGenderList.contains(uF.getGenderCode(innerList.get(26).trim()))) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Please check the Candidate's Sibling's Gender for Candidate Id ('"+innerList.get(0)+"') and try again.</li>");
									flag = false;
									break;
								}
								
								//flag = true;
							}
						}
						alCandiFamilyInfo = outerList;
					}
				}
			}

			
			Map<String, String> hmCandidateId = new HashMap<String, String>();
			//Created by dattatray Date : 08-July-2021 Note : i = 0 to i = 1
			for(int i=1; alCandiInfo!=null && i<alCandiInfo.size(); i++) {
				List<String> innerList = alCandiInfo.get(i);
				pst = con.prepareStatement("INSERT INTO candidate_personal_details (empcode, emp_fname, emp_lname, emp_email, emp_address1, emp_address2, " +
					"emp_city_id, emp_state_id, emp_country_id, emp_pincode, emp_address1_tmp, emp_address2_tmp, emp_city_id_tmp, emp_state_id_tmp, " +
					"emp_country_id_tmp, emp_pincode_tmp, emp_contactno, emp_gender, emp_date_of_birth, emp_contactno_mob, passport_no, passport_expiry_date, " +
					"blood_group, marital_status, emp_date_of_marriage, emp_entry_date, emp_mname, salutation, current_ctc,expected_ctc,notice_period," +
					"availability_for_interview,source_type,source_or_ref_code,other_ref_src) " +
					"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setString(1, innerList.get(1));
				pst.setString(2, innerList.get(3));
				pst.setString(3, innerList.get(5));
				pst.setString(4, innerList.get(6));

				pst.setString(5, innerList.get(7));
				pst.setString(6, innerList.get(8));
				pst.setString(7, innerList.get(9));
				pst.setInt(8, uF.parseToInt(hmState.get(innerList.get(11).toUpperCase())));
				pst.setInt(9, uF.parseToInt(hmCountry.get(innerList.get(10).toUpperCase())));
				pst.setString(10, innerList.get(12));

				pst.setString(11, innerList.get(13));
				pst.setString(12, innerList.get(14));
				pst.setString(13, innerList.get(15));
				pst.setInt(14, uF.parseToInt(hmState.get(innerList.get(17).toUpperCase())));
				pst.setInt(15, uF.parseToInt(hmCountry.get(innerList.get(16).toUpperCase())));
				pst.setString(16, innerList.get(18));

				pst.setString(17, innerList.get(18));
				pst.setString(18, innerList.get(25));
				pst.setDate(19, uF.getDateFormat(innerList.get(24), DATE_FORMAT));
				pst.setString(20, innerList.get(20));
				pst.setString(21, innerList.get(21));
				pst.setDate(22, uF.getDateFormat(innerList.get(22), DATE_FORMAT));
				pst.setString(23, innerList.get(23));
				pst.setString(24, innerList.get(26));
				pst.setDate(25, uF.getDateFormat(innerList.get(27), DATE_FORMAT));
				pst.setTimestamp(26, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
				pst.setString(27, innerList.get(4));
				pst.setString(28, innerList.get(2));
//				pst.setInt(39, uF.parseToInt(getOrg_id()));
//				pst.setInt(40, uF.parseToInt(orgCurrId));
				
				pst.setDouble(29, uF.parseToDouble(innerList.get(32)));
				pst.setDouble(30, uF.parseToDouble(innerList.get(33)));
				pst.setInt(31, uF.parseToInt(innerList.get(34)));
				pst.setBoolean(32, uF.parseToBoolean(innerList.get(28)));
				
				pst.setInt(33, uF.parseToInt(innerList.get(29)));
				pst.setInt(34, uF.parseToInt(innerList.get(30)));
				pst.setString(35, innerList.get(30));
				System.out.println("pst ===>> " + pst);
				int x= pst.executeUpdate();
				pst.close();
				System.out.println("x ===>> " + x);
				
				String candidateId = null;
				pst = con.prepareStatement("SELECT max(emp_per_id) as emp_per_id from candidate_personal_details");
				rs = pst.executeQuery();
//				System.out.println("pst candi max id ===> "+pst);
				while (rs.next()) {
					candidateId = rs.getString("emp_per_id");
				}
				rs.close();
				pst.close();
				
				String recruitId = hmJDId.get(innerList.get(0));
//				System.out.println("empPerId ===> "+empPerId);
				if(uF.parseToInt(recruitId) > 0) {
					pst = con.prepareStatement("INSERT INTO candidate_application_details (candidate_id,recruitment_id,job_code,application_date," +
						"added_by,entry_date,source_type) VALUES (?,?,?,?, ?,?,?)");
					pst.setInt(1, uF.parseToInt(candidateId));
					pst.setInt(2, uF.parseToInt(recruitId));
					pst.setString(3, innerList.get(0));
					pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(5, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
					if(strUserType != null && strUserType.equals(RECRUITER)) {
						pst.setInt(7, SOURCE_RECRUITER);
					} else {
						pst.setInt(7, SOURCE_HR);
					}
					pst.executeUpdate();
					pst.close();
					
					pst=con.prepareStatement("delete from candidate_activity_details where recruitment_id=? and candi_id=? and user_id=? and " +
						"activity_id = ?");
					pst.setInt(1, uF.parseToInt(recruitId));
					pst.setInt(2, uF.parseToInt(candidateId));
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
					pst.setInt(4, CANDI_ACTIVITY_APPLY_ID);
					pst.executeUpdate();
					pst.close();
					
					pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?, ?,?)");
					pst.setInt(1, uF.parseToInt(recruitId));
					pst.setInt(2, uF.parseToInt(candidateId));
					pst.setString(3, "Apply for Job");
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
					pst.setDate(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
					pst.setInt(6, CANDI_ACTIVITY_APPLY_ID);
					pst.executeUpdate();
					pst.close();
					
					if (innerList.get(48) != null && !innerList.get(48).equals("")) {
						pst = con.prepareStatement("insert into candidate_interview_availability (emp_id, ip_address, _timestamp, _date, _time,recruitment_id) values (?,?,?,?, ?,?)");
						pst.setInt(1, uF.parseToInt(candidateId));
						pst.setString(2, "");
						pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
						pst.setDate(4, uF.getDateFormat(uF.getDateFormat(innerList.get(48), DATE_FORMAT+" "+TIME_FORMAT, DATE_FORMAT), DATE_FORMAT));
						pst.setTime(5, uF.getTimeFormat(uF.getDateFormat(innerList.get(48), DATE_FORMAT+" "+TIME_FORMAT, TIME_FORMAT), TIME_FORMAT));
						pst.setInt(6, uF.parseToInt(recruitId));
						pst.execute();
//						System.out.println("in step 8 pst ===>> " + pst);
						pst.close();
					}
					
					if (innerList.get(49) != null && !innerList.get(49).equals("")) {
						pst = con.prepareStatement("insert into candidate_interview_availability (emp_id, ip_address, _timestamp, _date, _time,recruitment_id) values (?,?,?,?, ?,?)");
						pst.setInt(1, uF.parseToInt(candidateId));
						pst.setString(2, "");
						pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
						pst.setDate(4, uF.getDateFormat(uF.getDateFormat(innerList.get(49), DATE_FORMAT+" "+TIME_FORMAT, DATE_FORMAT), DATE_FORMAT));
						pst.setTime(5, uF.getTimeFormat(uF.getDateFormat(innerList.get(49), DATE_FORMAT+" "+TIME_FORMAT, TIME_FORMAT), TIME_FORMAT));
						pst.setInt(6, uF.parseToInt(recruitId));
						pst.execute();
//						System.out.println("in step 8 pst ===>> " + pst);
						pst.close();
					}
					
					if (innerList.get(50) != null && !innerList.get(50).equals("")) {
						pst = con.prepareStatement("insert into candidate_interview_availability (emp_id, ip_address, _timestamp, _date, _time,recruitment_id) values (?,?,?,?, ?,?)");
						pst.setInt(1, uF.parseToInt(candidateId));
						pst.setString(2, "");
						pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
						pst.setDate(4, uF.getDateFormat(uF.getDateFormat(innerList.get(50), DATE_FORMAT+" "+TIME_FORMAT, DATE_FORMAT), DATE_FORMAT));
						pst.setTime(5, uF.getTimeFormat(uF.getDateFormat(innerList.get(50), DATE_FORMAT+" "+TIME_FORMAT, TIME_FORMAT), TIME_FORMAT));
						pst.setInt(6, uF.parseToInt(recruitId));
						pst.execute();
//						System.out.println("in step 8 pst ===>> " + pst);
						pst.close();
					}
				}
				
				pst = con.prepareStatement("INSERT INTO candidate_references (ref_name, ref_company, ref_designation, ref_contact_no, ref_email_id, emp_id) " + "values(?,?,?,?, ?,?)");
				pst.setString(1, innerList.get(35));
				pst.setString(2, innerList.get(36));
				pst.setString(3, innerList.get(37));
				pst.setString(4, innerList.get(38));
				pst.setString(5, innerList.get(39));
				pst.setInt(6, uF.parseToInt(candidateId));
//				log.debug("pst==>" + pst);
				pst.executeUpdate();
				pst.close();

				pst = con.prepareStatement("INSERT INTO candidate_references (ref_name, ref_company, ref_designation, ref_contact_no, ref_email_id, emp_id) " + "values(?,?,?,?, ?,?)");
				pst.setString(1, innerList.get(40));
				pst.setString(2, innerList.get(41));
				pst.setString(3, innerList.get(42));
				pst.setString(4, innerList.get(43));
				pst.setString(5, innerList.get(44));
				pst.setInt(6, uF.parseToInt(candidateId));
//				log.debug("pst==>" + pst);
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("INSERT INTO candidate_medical_details (question_id, emp_id, yes_no, description,filepath) values(?,?,?,?, ?)");
				pst.setInt(1, 1);
				pst.setInt(2, uF.parseToInt(candidateId));
				pst.setBoolean(3, (innerList.get(45)!=null && !innerList.get(45).equals("")) ? true : false);
				pst.setString(4, innerList.get(45));
				pst.setString(5, null);
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("INSERT INTO candidate_medical_details (question_id, emp_id, yes_no, description,filepath) values(?,?,?,?, ?)");
				pst.setInt(1, 2);
				pst.setInt(2, uF.parseToInt(candidateId));
				pst.setBoolean(3, (innerList.get(46)!=null && !innerList.get(46).equals("")) ? true : false);
				pst.setString(4, innerList.get(46));
				pst.setString(5, null);
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("INSERT INTO candidate_medical_details (question_id, emp_id, yes_no, description,filepath) values(?,?,?,?, ?)");
				pst.setInt(1, 3);
				pst.setInt(2, uF.parseToInt(candidateId));
				pst.setBoolean(3, (innerList.get(47)!=null && !innerList.get(47).equals("")) ? true : false);
				pst.setString(4, innerList.get(47));
				pst.setString(5, null);
				pst.executeUpdate();
				pst.close();
				hmCandidateId.put(innerList.get(1).trim().toUpperCase(), candidateId);
			}
			//Created by dattatray Date : 08-July-2021 Note : i = 0 to i = 1
			for(int i=1; alCandiExpInfo!=null && i<alCandiExpInfo.size(); i++) {
				List<String> innerList = alCandiExpInfo.get(i);
				if (innerList.get(2)!=null && !innerList.get(2).trim().equals("")) {

					pst = con.prepareStatement("INSERT INTO candidate_prev_employment(company_name, company_location, company_city, company_state, "
						+ "company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, emp_id, " +
						"report_manager_ph_no, hr_manager, hr_manager_ph_no) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
					pst.setString(1, innerList.get(2).trim());
					pst.setString(2, innerList.get(3).trim());
					pst.setString(3, innerList.get(4).trim());
					pst.setString(4, innerList.get(5).trim());
					pst.setString(5, innerList.get(6).trim());
					pst.setString(6, innerList.get(7).trim());
					pst.setString(7, innerList.get(8).trim());
					pst.setDate(8, uF.getDateFormat(innerList.get(12).trim(), DATE_FORMAT));
					pst.setDate(9, uF.getDateFormat(innerList.get(13).trim(), DATE_FORMAT));
					pst.setString(10, innerList.get(14).trim());
					pst.setString(11, innerList.get(15).trim());
					pst.setString(12, innerList.get(16).trim());
					pst.setInt(13, uF.parseToInt(hmCandidateId.get(innerList.get(0).trim().toUpperCase())));
					pst.setString(14, innerList.get(9).trim());
					pst.setString(15, innerList.get(10).trim());
					pst.setString(16, innerList.get(11).trim());
//					log.debug("pst=>" + pst);
					pst.execute();
					pst.close();
				}
			}
			//Created by dattatray Date : 08-July-2021 Note : i = 0 to i = 1
			for(int i=1; alCandiEduInfo!=null && i<alCandiEduInfo.size(); i++) {
				List<String> innerList = alCandiEduInfo.get(i);
				if (innerList.get(1)!=null && !innerList.get(1).trim().equals("")) {

					pst = con.prepareStatement("INSERT INTO candidate_education_details(education_id, degree_duration, completion_year, grade, emp_id, degree_type,institute_name,university_name)" 
						+ "VALUES (?,?,?,?, ?,?,?,?)");
					pst.setInt(1, uF.parseToInt(hmDegreeId.get(innerList.get(1).trim().toUpperCase())));
					pst.setInt(2, uF.parseToInt(hmDegreeDuration.get(innerList.get(5).trim())));
					pst.setInt(3, uF.parseToInt(hmDegreeDuration.get(innerList.get(6).trim())));
					pst.setString(4, innerList.get(7).trim());
					pst.setInt(5, uF.parseToInt(hmCandidateId.get(innerList.get(0).trim().toUpperCase())));
					pst.setString(6, innerList.get(2).trim());
					pst.setString(7, innerList.get(3).trim());
					pst.setString(8, innerList.get(4).trim());
//					System.out.println("pst =========>> " + pst);
					pst.executeUpdate();
					pst.close();
				}
			}
			//Created by dattatray Date : 08-July-2021 Note : i = 0 to i = 1
			for(int i=1; alCandiSkillInfo!=null && i<alCandiSkillInfo.size(); i++) {
				List<String> innerList = alCandiSkillInfo.get(i);
				if (innerList.get(1)!=null && !innerList.get(1).trim().equals("")) {
					pst = con.prepareStatement("INSERT INTO candidate_skills_description (skill_id,skills_value,emp_id,last_used_year) VALUES (?,?,?,?)");
					pst.setInt(1, uF.parseToInt(hmSkillIds.get(innerList.get(1).trim().toUpperCase())));
					pst.setString(2, innerList.get(2).trim());
					pst.setInt(3, uF.parseToInt(hmCandidateId.get(innerList.get(0).trim().toUpperCase())));
					pst.setInt(4, uF.parseToInt(innerList.get(3)));// Created by Dattatray Date : 07-July-21 Note : Index changed 2 to 4
					pst.executeUpdate();
					pst.close();
				}
			}
			//Created by dattatray Date : 08-July-2021 Note : i = 0 to i = 1
			for(int i=1; alCandiLangInfo!=null && i<alCandiLangInfo.size(); i++) {
				List<String> innerList = alCandiLangInfo.get(i);
				if (innerList.get(1)!=null && !innerList.get(1).trim().equals("")) {
					pst = con.prepareStatement("INSERT INTO candidate_languages_details(language_name, language_read, language_write, language_speak, emp_id)" +
						"VALUES (?,?,?,?, ?)");
					pst.setString(1, innerList.get(1).trim());
					pst.setInt(2, uF.parseToInt(innerList.get(2).trim()));
					pst.setInt(3, uF.parseToInt(innerList.get(3).trim()));
					pst.setInt(4, uF.parseToInt(innerList.get(4).trim()));
					pst.setInt(5, uF.parseToInt(hmCandidateId.get(innerList.get(0).trim().toUpperCase())));
					pst.executeUpdate();
					pst.close();
				}
			}
			//Created by dattatray Date : 08-July-2021 Note : i = 0 to i = 1
			for(int i=1; alCandiHobiesInfo!=null && i<alCandiHobiesInfo.size(); i++) {
				List<String> innerList = alCandiHobiesInfo.get(i);
				if (innerList.get(1)!=null && !innerList.get(1).trim().equals("")) {
					pst = con.prepareStatement("INSERT INTO candidate_hobbies_details (hobbies_name, emp_id) VALUES (?,?)");
					pst.setString(1, innerList.get(1).trim());
					pst.setInt(2, uF.parseToInt(hmCandidateId.get(innerList.get(0).trim().toUpperCase())));
					pst.executeUpdate();
					pst.close();
				}
			}
			
			//Created by dattatray Date : 08-July-2021 Note : i = 0 to i = 1
			for(int i=1; alCandiFamilyInfo!=null && i<alCandiFamilyInfo.size(); i++) {
				List<String> innerList = alCandiFamilyInfo.get(i);
				if (innerList.get(7)!=null && !innerList.get(7).trim().equals("")) {
					pst = con.prepareStatement("INSERT INTO candidate_family_members(member_type, member_name, member_dob, member_education, "
						+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?, ?,?,?,?, ?)");
					pst.setString(1, "MOTHER");
					pst.setString(2, innerList.get(7).trim());
					pst.setDate(3, uF.getDateFormat(innerList.get(8).trim(), DATE_FORMAT));
					pst.setString(4, innerList.get(9).trim());
					pst.setString(5, innerList.get(10).trim());
					pst.setString(6, innerList.get(11).trim());
					pst.setString(7, innerList.get(12).trim());
					pst.setString(8, "F");
					pst.setInt(9, uF.parseToInt(hmCandidateId.get(innerList.get(0).trim().toUpperCase())));
					pst.executeUpdate();
					pst.close();
				}

				if (innerList.get(1)!=null && !innerList.get(1).trim().equals("")) {
					pst = con.prepareStatement("INSERT INTO candidate_family_members(member_type, member_name, member_dob, member_education, "
							+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?, ?,?,?,?, ?)");
					pst.setString(1, "FATHER");
					pst.setString(2, innerList.get(1).trim());
					pst.setDate(3, uF.getDateFormat(innerList.get(2).trim(), DATE_FORMAT));
					pst.setString(4, innerList.get(3).trim());
					pst.setString(5, innerList.get(4).trim());
					pst.setString(6, innerList.get(5).trim());
					pst.setString(7, innerList.get(6).trim());
					pst.setString(8, "M");
					pst.setInt(9, uF.parseToInt(hmCandidateId.get(innerList.get(0).trim().toUpperCase())));
					pst.executeUpdate();
					pst.close();
				}

				if (innerList.get(13)!=null && !innerList.get(13).trim().equals("")) {
					pst = con.prepareStatement("INSERT INTO candidate_family_members(member_type, member_name, member_dob, member_education, "
							+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?, ?,?,?,?, ?)");
					pst.setString(1, "SPOUSE");
					pst.setString(2, innerList.get(13).trim());
					pst.setDate(3, uF.getDateFormat(innerList.get(14).trim(), DATE_FORMAT));
					pst.setString(4, innerList.get(15).trim());
					pst.setString(5, innerList.get(16).trim());
					pst.setString(6, innerList.get(17).trim());
					pst.setString(7, innerList.get(18).trim());
					pst.setString(8, uF.getGenderCode(innerList.get(19).trim()));
					pst.setInt(9, uF.parseToInt(hmCandidateId.get(innerList.get(0).trim().toUpperCase())));
					pst.executeUpdate();
					pst.close();
				}

				if (innerList.get(20)!=null && !innerList.get(20).trim().equals("")) {
					pst = con.prepareStatement("INSERT INTO candidate_family_members(member_type, member_name, member_dob, member_education, "
						+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?, ?,?,?,?, ?)");
					pst.setString(1, "SIBLING");
					pst.setString(2, innerList.get(20).trim());
					pst.setDate(3, uF.getDateFormat(innerList.get(21).trim(), DATE_FORMAT));
					pst.setString(4, innerList.get(22).trim());
					pst.setString(5, innerList.get(23).trim());
					pst.setString(6, innerList.get(24).trim());
					pst.setString(7, innerList.get(25).trim());
					pst.setString(8, uF.getGenderCode(innerList.get(26).trim()));
					pst.setInt(9, uF.parseToInt(hmCandidateId.get(innerList.get(0).trim().toUpperCase())));
					pst.executeUpdate();
					pst.close();
				}
			}
			
			//Created by dattatray Date : 08-July-2021 Note : Condtion checked
			if(alErrorList.size() == 0) {
				flag = true;
			}
			System.out.println("flag ===>> " + flag);
			System.out.println("alErrorList size : "+alErrorList.size());
			
			if (flag) {
				con.commit();
//				sbMessage.append("<li class=\"msg savesuccess\" style=\"margin:0px\">Job Profiles Imported Successfully!</li>");
//				sbMessage.append("</ul>");
//				System.out.println("sbMessage in commit else if ===>> " + sbMessage.toString());
//				request.setAttribute("sbMessage", sbMessage.toString());
				session.setAttribute(MESSAGE, SUCCESSM+ "Job Profiles Imported Successfully!" + END);
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
					//session.setAttribute(MESSAGE,ERRORM+ "Job Profiles not imported. Please check imported file."+ END);
				
			}
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
//			System.out.println("in catch rollback");
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