package com.konnect.jpms.recruitment;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSourceTypeAndName;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Induction extends ActionSupport implements ServletRequestAware,IStatements {

	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(RequirementApproval.class);

	String strSessionEmpId = null;

	private String recruitId;
	
	private String dataType;
	
	private String strSearchJob;
	
	private String f_org;
	private String[] location;
	private String[] designation;
	
	private List<FillOrganisation> organisationList;
	private List<FillDesig> desigList;
	private List<FillWLocation> workLocationList;
	
	private List<FillSourceTypeAndName> sourceTypeList;
	private List<FillSourceTypeAndName> sourceNameList;
	
	private String proPage;
	private String minLimit;
	
	private String strVm;
	
	private String appliSourceType;
	private String[] appliSourceName;
	
	private String currRecruitId;
	private String operation;
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		UtilityFunctions uF=new UtilityFunctions();
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView) {
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, "On-board");
		request.setAttribute(PAGE, "/jsp/recruitment/Induction.jsp");
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		removeTemporaryFile(uF);
		sourceTypeList = new FillSourceTypeAndName(request).fillSourceType();
		if(getAppliSourceType() !=null && !getAppliSourceType().equals("")) {
			if(uF.parseToInt(getAppliSourceType()) != SOURCE_WEBSITE) {
				sourceNameList = new FillSourceTypeAndName(request).fillSourceNameOnType(getAppliSourceType());
			}
		} else {
			sourceNameList = new FillSourceTypeAndName(request).fillAllSourceName();
		}
		
		if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")) {
			setDataType("L");
		}
		if(uF.parseToInt(getProPage()) == 0) {
			setProPage("1");
		}
		
//		System.out.println("getDataType() ===>> " + getDataType());
		
		organisationList = new FillOrganisation(request).fillOrganisation();
		workLocationList = new FillWLocation(request).fillWLocation(getF_org());
		desigList = new FillDesig(request).fillDesig(uF.parseToInt(getF_org()));
		
//		getSelectedFilter(uF);
//		getSearchAutoCompleteData(uF);
		getLiveRecruitmentIds(uF, getRecruitId());
		prepareInduction(uF);
		
		return SUCCESS;
			
	}

	
	private void removeTemporaryFile(UtilityFunctions uF) {
		try{
			String directory = CF.getStrDocSaveLocation()+I_TEMP+"/";
			File theDir = new File(directory);
			if(theDir.exists()){
				FileUtils.cleanDirectory(theDir);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null) {
			String strOrg="";
			for(int i=0;organisationList!=null && i<organisationList.size();i++) {
				if(getF_org().equals(organisationList.get(i).getOrgId())) {
					strOrg=organisationList.get(i).getOrgName();
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organizations");
			}
		} else {
			hmFilter.put("ORGANISATION", "All Organizations");
		}
		
		
		alFilter.add("LOCATION");
		if(getLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;workLocationList!=null && i<workLocationList.size();i++) {
				for(int j=0;j<getLocation().length;j++) {
					if(getLocation()[j].equals(workLocationList.get(i).getwLocationId())) {
						if(k==0) {
							strLocation=workLocationList.get(i).getwLocationName();
						} else {
							strLocation+=", "+workLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if(strLocation!=null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
		
		alFilter.add("DESIG");
		if(getDesignation()!=null) {
			String strDesig="";
			int k=0;
			for(int i=0;desigList!=null && i<desigList.size();i++) {
				for(int j=0;j<getDesignation().length;j++) {
					if(getDesignation()[j].equals(desigList.get(i).getDesigId())) {
						if(k==0) {
							strDesig = desigList.get(i).getDesigCodeName();
						} else {
							strDesig += ", " + desigList.get(i).getDesigCodeName();
						}
						k++;
					}
				}
			}
			if(strDesig!=null && !strDesig.equals("")) {
				hmFilter.put("DESIG", strDesig);
			} else {
				hmFilter.put("DESIG", "All Designations");
			}
		} else {
			hmFilter.put("DESIG", "All Designations");
		}
		
		alFilter.add("SOURCE");
		if(getF_org()!=null) {
			String strSourceType="";
			for(int i=0;sourceTypeList!=null && i<sourceTypeList.size();i++) {
				if(getAppliSourceType().equals(sourceTypeList.get(i).getSourceTypeId())) {
					strSourceType = sourceTypeList.get(i).getSourceTypeName();
				}
			}
			if(strSourceType!=null && !strSourceType.equals("")) {
				hmFilter.put("SOURCE", strSourceType);
			} else {
				hmFilter.put("SOURCE", "All");
			}
		} else {
			hmFilter.put("SOURCE", "All");
		}
		
		if(uF.parseToInt(getAppliSourceType()) != SOURCE_WEBSITE) {
			alFilter.add("SOURCE_NAME");
			if(getAppliSourceName()!=null) {
				String strSourceName="";
				int k=0;
				for(int i=0;sourceNameList!=null && i<sourceNameList.size();i++) {
					for(int j=0;j<getAppliSourceName().length;j++) {
						if(getAppliSourceName()[j].equals(sourceNameList.get(i).getSourceTypeId())) {
							if(k==0) {
								strSourceName = sourceNameList.get(i).getSourceTypeName();
							} else {
								strSourceName += ", " + sourceNameList.get(i).getSourceTypeName();
							}
							k++;
						}
					}
				}
				if(strSourceName!=null && !strSourceName.equals("")) {
					hmFilter.put("SOURCE_NAME", strSourceName);
				} else {
					hmFilter.put("SOURCE_NAME", "All");
				}
			} else {
				hmFilter.put("SOURCE_NAME", "All");
			}
		}
		
		
		String selectedFilter= CF.getSelectedFilter(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	
	private void getSearchAutoCompleteData(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			SortedSet<String> setJobList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select job_code,job_title,skills,essential_skills from recruitment_details where job_approval_status=1");
			/*if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and close_job_status = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and close_job_status = true ");
			}*/
			if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
				sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where candidate_final_status=1 " +
						" and candidate_status=1 and source_type="+uF.parseToInt(getAppliSourceType())+" ");
				if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
						sbQuery.append(" and added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
						sbQuery.append(" and source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					}
				}
				sbQuery.append(") ");
			} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
				sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where candidate_id>0 and " +
						" candidate_final_status=1 and candidate_status=1 ");
				sbQuery.append(" and (added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
				sbQuery.append(" or source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
				sbQuery.append(") ");
			}
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(RECRUITER))) {
				sbQuery.append(" and (added_by = "+ uF.parseToInt(strSessionEmpId) +" or hiring_manager like '%"+strSessionEmpId+"%') ");
			}
			if(uF.parseToInt(getF_org()) > 0) {
			 sbQuery.append(" and org_id = "+uF.parseToInt(getF_org())+" ");
	        }
	        if(getLocation()!=null && getLocation().length > 0 && !getLocation()[0].trim().equals("")) {
	        	sbQuery.append(" and wlocation in ("+StringUtils.join(getLocation(), ",")+") ");
			}
	        if(getDesignation()!=null && getDesignation().length > 0 && !getDesignation()[0].trim().equals("")) {
	        	sbQuery.append(" and designation_id in ("+StringUtils.join(getDesignation(), ",")+") ");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst===> "+ pst);
			rs = pst.executeQuery();
			StringBuilder sbSkills = null;
			while (rs.next()) {
				setJobList.add(rs.getString("job_code"));
				if(rs.getString("job_title")!=null && !rs.getString("job_title").trim().equals("")) {
					setJobList.add(rs.getString("job_title"));
				}
				
				if(rs.getString("skills")!=null && !rs.getString("skills").trim().equals("")) {
					if(sbSkills == null) {
						sbSkills = new StringBuilder();
						sbSkills.append(rs.getString("skills"));
					} else {
						sbSkills.append(","+rs.getString("skills"));
					}
				}
				
				if(rs.getString("essential_skills")!=null && !rs.getString("essential_skills").trim().equals("")) {
					if(sbSkills == null) {
						sbSkills = new StringBuilder();
						sbSkills.append(rs.getString("essential_skills"));
					} else {
						sbSkills.append(","+rs.getString("essential_skills"));
					}
				}
			}
			rs.close();
			pst.close();
			
			if(sbSkills != null) {
				List<String> alSkills = Arrays.asList(sbSkills.toString().trim().split(","));
				StringBuilder sbSkillsId = null;
				for(int i=0; alSkills != null && i < alSkills.size(); i++) {
					if(alSkills.get(i)!=null && !alSkills.get(i).trim().equals("") && uF.parseToInt(alSkills.get(i).trim()) > 0) {
						if(sbSkillsId == null) {
							sbSkillsId = new StringBuilder();
							sbSkillsId.append(alSkills.get(i).trim());
						} else {
							sbSkillsId.append(","+alSkills.get(i).trim());
						}
					}
				}
				
				if(sbSkillsId!=null) {
					pst = con.prepareStatement("select skill_name from skills_details where skill_name is not null " +
							"and skill_name != '' and skill_id in("+sbSkillsId.toString()+")");
					rs = pst.executeQuery();
					while (rs.next()) {
						setJobList.add(rs.getString("skill_name"));
					}
					
					rs.close();
					pst.close();
				}
			}
			
			StringBuilder sbData = null;
			Iterator<String> it = setJobList.iterator();
			while (it.hasNext()) {
				String strData = it.next();
				if(sbData == null) {
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null) {
				sbData = new StringBuilder();
			}
			request.setAttribute("sbData", sbData.toString());
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	private void getLiveRecruitmentIds(UtilityFunctions uF, String recruitId) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmCandiImage = CF.getCandidateImageMap(con);
			request.setAttribute("hmCandiImage", hmCandiImage);
			
			List<String> recruitmentIdList = new ArrayList<String>();
			Map<String, String> hmJobCodeName = new HashMap<String, String>();
			Map<String, String> hmJobPriority = new HashMap<String, String>();
			Map<String, String> hmJobStatus = new HashMap<String, String>();
			Map<String, String> hmJobTitle = new HashMap<String, String>();
			
			List<String> alSkillId = null;
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {	
				pst = con.prepareStatement("select skill_id from skills_details where skill_name is not null " +
						"and skill_name != '' and upper(skill_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'");
				rst = pst.executeQuery();
				while(rst.next()) {
					if(alSkillId == null) {
						alSkillId = new ArrayList<String>();
						alSkillId.add(rst.getString("skill_id"));
					} else {
						alSkillId.add(rst.getString("skill_id"));
					}
				}
				
				rst.close();
				pst.close();
			}
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select job_code,recruitment_id,custum_designation,close_job_status,priority_job_int,job_title from recruitment_details " +
				"where job_approval_status=1 ");
			if (recruitId != null && !recruitId.equals("") && uF.parseToInt(recruitId) > 0) {
				sbQuery.append(" and recruitment_id = " + recruitId + " ");
			}
			int intOffset = uF.parseToInt(getMinLimit());
			sbQuery.append(" limit 10 offset "+intOffset+"");
			pst = con.prepareStatement(sbQuery.toString());
			rst = pst.executeQuery();
//			System.out.println("pst===> "+ pst); 
			StringBuilder sbRecruitId = null;
			Map<String, Map<String, String>> hmcandiStarRecruitwise = new HashMap<String, Map<String,String>>();
			while (rst.next()) {
				if(sbRecruitId == null){
					sbRecruitId = new StringBuilder();
					sbRecruitId.append(rst.getString("recruitment_id"));
				} else {
					sbRecruitId.append(","+rst.getString("recruitment_id"));
				}
				hmJobCodeName.put(rst.getString("recruitment_id"), rst.getString("job_code"));
				hmJobPriority.put(rst.getString("recruitment_id"), rst.getString("priority_job_int"));
				recruitmentIdList.add(rst.getString("recruitment_id"));
				
				hmJobStatus.put(rst.getString("recruitment_id"), uF.parseToBoolean(rst.getString("close_job_status"))+"");
				hmJobTitle.put(rst.getString("recruitment_id"), rst.getString("job_title"));
				
				Map<String, String> hmCandiStars = getCandidateList(con, uF, rst.getString("recruitment_id"));
				hmcandiStarRecruitwise.put(rst.getString("recruitment_id"), hmCandiStars);
			}
			rst.close();
			pst.close();
			if(sbRecruitId != null) {
				setCurrRecruitId(sbRecruitId.toString());
				getSourceDetails(con,uF,sbRecruitId.toString());
			}
			
			request.setAttribute("hmcandiStarRecruitwise", hmcandiStarRecruitwise);
			request.setAttribute("hmJobCodeName", hmJobCodeName);
			request.setAttribute("hmJobPriority", hmJobPriority);
			request.setAttribute("recruitmentIdList", recruitmentIdList);
			request.setAttribute("hmJobStatus", hmJobStatus);
			request.setAttribute("hmJobTitle", hmJobTitle);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getSourceDetails(Connection con, UtilityFunctions uF, String strRecruitIds) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			if(hmEmpName == null) hmEmpName = new HashMap<String, String>();
			
			Map<String, Map<String, Map<String, String>>> hmSource = new HashMap<String, Map<String,Map<String,String>>>();
			pst = con.prepareStatement("select * from candidate_application_details where recruitment_id in ("+strRecruitIds+") order by recruitment_id");
			rs = pst.executeQuery();
			while(rs.next()){
				 Map<String, Map<String, String>> hmCandidate = hmSource.get(rs.getString("recruitment_id"));
				 if(hmCandidate == null) hmCandidate = new HashMap<String, Map<String,String>>();
				 
				 Map<String, String> hmSourceDetails = hmCandidate.get(rs.getString("candidate_id"));
				 if(hmSourceDetails == null) hmSourceDetails = new HashMap<String, String>(); 
				 
				 String strSourceType = "";
				 String strSourceName = "";
				 if(uF.parseToInt(rs.getString("source_type")) == SOURCE_HR){
					 strSourceType = "Hr Manager";
					 strSourceName = uF.showData(hmEmpName.get(rs.getString("added_by")), "-");
				 } else if(uF.parseToInt(rs.getString("source_type")) == SOURCE_RECRUITER){
					 strSourceType = "Recruiter";
					 strSourceName = uF.showData(hmEmpName.get(rs.getString("added_by")), "-");
				 } else if(uF.parseToInt(rs.getString("source_type")) == SOURCE_REFERENCE){
					 strSourceType = "References";
					 strSourceName = uF.showData(hmEmpName.get(rs.getString("source_or_ref_code")), "-");
				 } else if(uF.parseToInt(rs.getString("source_type")) == SOURCE_WEBSITE){
					 strSourceType = "Website";
				 }
				 
				 hmSourceDetails.put("SOURCE_TYPE", strSourceType);
				 hmSourceDetails.put("SOURCE_NAME", strSourceName);
				 
				 hmCandidate.put(rs.getString("candidate_id"), hmSourceDetails);
				 hmSource.put(rs.getString("recruitment_id"), hmCandidate);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmSource", hmSource);
			
		}catch(Exception e){
			e.printStackTrace();
		} finally {
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
	
private Map<String, String> getCandidateList(Connection con, UtilityFunctions uF, String recruitID) {
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String, String> hmCandiStars = new HashMap<String, String>();
		try {
			Map<String, List<String>> hmSkills = getCandiSkillsName(con);
			Map<String, List<String>> hmDegrees = getCandiDegreeName(con);
			Map<String, List<String>> hmTotExp = getCandiTotExp(con, uF);
			StringBuilder sbQue = new StringBuilder();
			sbQue.append("select emp_per_id,emp_fname,emp_lname,cad.recruitment_id,emp_gender from candidate_personal_details cpd, " +
				"candidate_application_details cad where cpd.emp_per_id = cad.candidate_id and cad.recruitment_id = ? ");
			if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
				sbQue.append(" and cad.source_type = "+uF.parseToInt(getAppliSourceType())+" ");
				if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
						sbQue.append(" and cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
						sbQue.append(" and cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					}
				}
			} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
				sbQue.append(" and (cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
				sbQue.append(" or cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
			}
			sbQue.append(" order by emp_fname,emp_lname");
			pst=con.prepareStatement(sbQue.toString());
			pst.setInt(1, uF.parseToInt(recruitID));
			rst=pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while(rst.next()){
				String candiID = rst.getString("emp_per_id");
				String candiGender = rst.getString("emp_gender");
				String strStars = calculateCandidateStarRating(con, uF, hmSkills.get(candiID), hmDegrees.get(candiID), hmTotExp.get(candiID), candiGender, recruitID);
				hmCandiStars.put(candiID, strStars);
			}
			rst.close();
			pst.close();
			
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			if(rst!=null){
				try {
					rst.close();
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
		return hmCandiStars;
	}
	
	private String calculateCandidateStarRating(Connection con, UtilityFunctions uF, List<String> skillsList, List<String> educationsList, List<String> totExpList, String candiGender, String recruitID) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		String strStars=null;
		try {
			String desigID = "";
			Map<String, String> hmEduWeightage = getEducationWeightage(con, uF);
			Map<String, String> hmJobDetails = new HashMap<String, String>();
			pst=con.prepareStatement("select * from recruitment_details where recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(recruitID));
			rst=pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while(rst.next()){
				hmJobDetails.put("SKILLS", rst.getString("skills"));
				hmJobDetails.put("EDUCATIONS", rst.getString("min_education"));
				hmJobDetails.put("MIN_EXP", rst.getString("min_exp"));
				hmJobDetails.put("MAX_EXP", rst.getString("max_exp"));
				hmJobDetails.put("DESIG_ID", rst.getString("designation_id"));
				desigID = rst.getString("designation_id");
			}
			rst.close();
			pst.close();
			
			String minExp = hmJobDetails.get("MIN_EXP");
			List<String> recSkillsList =  getListData(hmJobDetails.get("SKILLS"));
			List<String> recEduList = getListData(hmJobDetails.get("EDUCATIONS"));
			Map<String, String> hmDesigAttrib = getDesigAttribute(con, uF);
			List<String> desigSkillList = getListData(hmDesigAttrib.get(desigID+"_SKILLS"));
			List<String> desigEduList = getListData(hmDesigAttrib.get(desigID+"_EDUCATION"));
			String desigTotExp = hmDesigAttrib.get(desigID+"_TOTEXP");
			String desigRelExp = hmDesigAttrib.get(desigID+"_RELEXP");
			String desigExpWithus = hmDesigAttrib.get(desigID+"_EXPWITH_US");
			String desigGender = hmDesigAttrib.get(desigID+"_GENDER");

			int skillMarks=0, skillCount=0;
			if(recSkillsList != null && !recSkillsList.isEmpty()){
				for (int i = 0; i < recSkillsList.size(); i++) {
					for (int j = 0; skillsList != null && !skillsList.isEmpty() && j < skillsList.size(); j++) {
						if(recSkillsList.get(i).equals(skillsList.get(j))){
							skillMarks = 100;
						}
					}
					skillCount=1;
				}
			}else{
				if(desigSkillList != null && !desigSkillList.isEmpty()){
					for (int i = 0; i < desigSkillList.size(); i++) {
						for (int j = 0; skillsList != null && !skillsList.isEmpty() && j < skillsList.size(); j++) {
							if(desigSkillList.get(i).equals(skillsList.get(j))){
								skillMarks = 100;
							}
						}
						skillCount=1;
					}
				}
			}
			
			int eduMarks=0, eduCount=0;
			if(recEduList != null && !recEduList.isEmpty()){
				for (int i = 0; i < recEduList.size(); i++) {
					for (int j = 0; educationsList != null && !educationsList.isEmpty() && j < educationsList.size(); j++) {
						if(recEduList.get(i).equals(educationsList.get(j)) || 
								uF.parseToInt(hmEduWeightage.get(recEduList.get(i))) >= uF.parseToInt(hmEduWeightage.get(educationsList.get(j)))){
							eduMarks = 100;
						}
					}
					eduCount=1;
				}
			}else{
				if(desigEduList != null && !desigEduList.isEmpty()){
					for (int i = 0; i < desigEduList.size(); i++) {
						for (int j = 0; educationsList != null && !educationsList.isEmpty() && j < educationsList.size(); j++) {
							if(desigEduList.get(i).equals(educationsList.get(j)) || 
									uF.parseToInt(hmEduWeightage.get(desigEduList.get(i))) >= uF.parseToInt(hmEduWeightage.get(educationsList.get(j)))){
								eduMarks = 100;
							}
						}
						eduCount=1;
					}
				}
			}
			
			
			int expMarks=0, expCount=0;
			if(minExp != null && !minExp.equals("")){
//				for (int i = 0; i < recEduList.size(); i++) {
				double sumcandiExp=0;
					for (int j = 0; totExpList != null && !totExpList.isEmpty() && j < totExpList.size(); j++) {
//						System.out.println("totExpList "+j+" == "+totExpList.get(j));
						sumcandiExp += uF.parseToDouble(totExpList.get(j));
					}
//					System.out.println("sumcandiExp == "+sumcandiExp);
					
					if(uF.parseToDouble(minExp)>0 && uF.parseToDouble(minExp) <= sumcandiExp){
						expMarks = 100;
					}
					expCount=1;
//				}
			}else{
				if(desigTotExp != null && !desigTotExp.equals("")){
					double sumcandiExp=0;
						for (int j = 0; totExpList != null && !totExpList.isEmpty() && j < totExpList.size(); j++) {
							sumcandiExp += uF.parseToDouble(totExpList.get(j));
						}
						if(uF.parseToDouble(desigTotExp)>0 && uF.parseToDouble(desigTotExp) <= sumcandiExp){
							expMarks = 100;
						}
						expCount=1;
				}
			}
//			System.out.println("desigGender == "+desigGender);
//			System.out.println("candiGender == "+candiGender);
			int genderMarks=0, genderCount=0;
			if(desigGender != null && !desigGender.equals("")){
				if(desigGender.equals(candiGender)){
					genderMarks = 100;
				}
				genderCount=1;
			}
			
//			System.out.println("skillMarks == " + skillMarks + " eduMarks == " +  eduMarks + " expMarks == " + expMarks + " genderMarks == " + genderMarks);
			int allMarks = skillMarks + eduMarks + expMarks + genderMarks;
			int allCount = skillCount + eduCount + expCount + genderCount;
		
			int avgMarks = allMarks / allCount;
//			System.out.println("avgMarks == "+avgMarks);
			double starrts = uF.parseToDouble(""+avgMarks) / 20;
//			strStars = uF.formatIntoOneDecimalWithOutComma(starrts);
			int intstars = (int) starrts;
			if(starrts>uF.parseToDouble(""+intstars)){
				strStars = uF.formatIntoOneDecimalWithOutComma(starrts);
			}else{
				strStars = intstars+"";
			}
//			System.out.println("strStars == "+strStars);
		
		}catch(Exception e){
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
		
		return strStars;
	}
	
	public List<String> getListData(String strData){
		List<String> dataList = new ArrayList<String>();
		if(strData != null && !strData.equals("")){
			dataList = Arrays.asList(strData.split(","));
		}
		
		return dataList;
	}
	
private Map<String, List<String>> getCandiTotExp(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		Map<String, List<String>> hmTotExp = new HashMap<String, List<String>>();
		try {
			List<String> expList = new ArrayList<String>();
			pst=con.prepareStatement("select emp_id,from_date,to_date from candidate_prev_employment");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while(rst.next()){
				expList =hmTotExp.get(rst.getString("emp_id"));
				if(expList== null)expList = new ArrayList<String>();

				String frmdt = rst.getString("from_date");
				String todt = rst.getString("to_date");
				String candidateExp = "";
				if(frmdt != null && todt != null){
					candidateExp = getTimeDurationBetweenDates(frmdt, DBDATE, todt, DBDATE, uF);
				}
				expList.add(candidateExp);
				hmTotExp.put(rst.getString("emp_id"), expList);
			}
			rst.close();
			pst.close();
			
		}catch(Exception e){
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
		return hmTotExp;
	}

public String getTimeDurationBetweenDates(String strStartDate, String strStartFormat, String strEndDate, String strEndFormat, UtilityFunctions uF){
	
	StringBuilder sbTimeDuration = new StringBuilder();
	try {
		LocalDate joiningDate = new LocalDate(
				uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "yyyy")), 
				uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "MM")), 
				uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "dd")));
	    LocalDate currentDate = new LocalDate(
	    		uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "yyyy")), 
				uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "MM")), 
				uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "dd")));

	    Period period = new Period(joiningDate, currentDate, PeriodType.yearMonthDay());
		
		if(period.getYears()>0){
			sbTimeDuration.append(period.getYears());
		}
		
		if(period.getMonths()>0){
			sbTimeDuration.append("."+period.getMonths());
		}
					
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	return sbTimeDuration.toString();
}


private Map<String, String> getDesigAttribute(Connection con, UtilityFunctions uF){
	
	PreparedStatement pst = null;
	ResultSet rst = null;
	
	Map<String, String> hmDesigAttrib = new HashMap<String, String>();
	try {
		pst=con.prepareStatement("select * from desig_attribute");
		rst=pst.executeQuery();
//		System.out.println("new Date ===> "+ new Date());
		while(rst.next()){
			if(rst.getString("_type").equals("1")){
				hmDesigAttrib.put(rst.getString("desig_id")+"_EDUCATION", rst.getString("desig_value"));
			} else if(rst.getString("_type").equals("2")){
				hmDesigAttrib.put(rst.getString("desig_id")+"_TOTEXP", rst.getString("desig_value"));
			} else if(rst.getString("_type").equals("3")){
				hmDesigAttrib.put(rst.getString("desig_id")+"_RELEXP", rst.getString("desig_value"));
			} else if(rst.getString("_type").equals("4")){
				hmDesigAttrib.put(rst.getString("desig_id")+"_EXPWITH_US", rst.getString("desig_value"));
			} else if(rst.getString("_type").equals("5")){
				hmDesigAttrib.put(rst.getString("desig_id")+"_SKILLS", rst.getString("desig_value"));
			} else if(rst.getString("_type").equals("6")){
				hmDesigAttrib.put(rst.getString("desig_id")+"_GENDER", rst.getString("desig_value"));
			}  
		}
		rst.close();
		pst.close();
		
	}catch(Exception e){
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
	return hmDesigAttrib;
}
	
private Map<String, List<String>> getCandiDegreeName(Connection con){
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String, List<String>> hmDegreeName = new HashMap<String, List<String>>();
		try {
			List<String> degreeList = new ArrayList<String>();
			pst=con.prepareStatement("select emp_id,education_id from candidate_education_details");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while(rst.next()){
				degreeList =hmDegreeName.get(rst.getString("emp_id"));
				if(degreeList== null)degreeList = new ArrayList<String>();
				degreeList.add(rst.getString("education_id"));
				hmDegreeName.put(rst.getString("emp_id"), degreeList);
			}
			rst.close();
			pst.close();
			
		}catch(Exception e){
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
		return hmDegreeName;
	}
	
private Map<String, List<String>> getCandiSkillsName(Connection con){
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String, List<String>> hmSkills = new HashMap<String, List<String>>();
		try {
			List<String> skillList = new ArrayList<String>();
			Map<String, String> hmSkillsName = CF.getSkillNameMap(con);
			pst=con.prepareStatement("select emp_id,skill_id from candidate_skills_description");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while(rst.next()){
				skillList =hmSkills.get(rst.getString("emp_id"));
				if(skillList== null)skillList = new ArrayList<String>();
				skillList.add(hmSkillsName.get(rst.getString("skill_id")));
				hmSkills.put(rst.getString("emp_id"), skillList);
			}
			rst.close();
			pst.close();
			
		}catch(Exception e){
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
		return hmSkills;
	}
	
	private Map<String, String> getEducationWeightage(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		Map<String, String> hmEduWeightage = new HashMap<String, String>();
		try {
			pst=con.prepareStatement("select education_name,weightage from educational_details");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while(rst.next()){
				hmEduWeightage.put(rst.getString("education_name"), rst.getString("weightage"));
			}
			rst.close();
			pst.close();
			
		}catch(Exception e){
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
		return hmEduWeightage;
	}
	
	private void prepareInduction(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst =null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con=db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			// designation map
			if(getCurrRecruitId() !=null && !getCurrRecruitId().equals("")) {
				
				Map<String, String> hmDesignation = new HashMap<String, String>();
				pst=con.prepareStatement("select designation_id,designation_name from designation_details");
				rst=pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
				while(rst.next()) {
				hmDesignation.put(rst.getString("designation_id"), rst.getString("designation_name"));	
				}
				rst.close();
				pst.close();
				
				List<String> alSkillId = null;
				if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
					pst = con.prepareStatement("select skill_id from skills_details where skill_name is not null " +
							"and skill_name != '' and upper(skill_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'");
					rst = pst.executeQuery();
					while(rst.next()) {
						if(alSkillId == null) {
							alSkillId = new ArrayList<String>();
							alSkillId.add(rst.getString("skill_id"));
						} else {
							alSkillId.add(rst.getString("skill_id"));
						}
					}
				}
				
	            // department map
				Map<String, String> hmDepart = new HashMap<String, String>();
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select emp_id,dept_name from employee_official_details eod join recruitment_details on emp_id=added_by join " +
					" department_info di on eod.depart_id=di.dept_id where recruitment_id in ("+getCurrRecruitId()+") ");
				if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
					sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where candidate_final_status=1 " +
						" and candidate_status=1 and source_type="+uF.parseToInt(getAppliSourceType())+" ");
					if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
						if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
							sbQuery.append(" and added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
							sbQuery.append(" and source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						}
					}
					sbQuery.append(") ");
				} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where candidate_id>0 and " +
						" candidate_final_status=1 and candidate_status=1 ");
					sbQuery.append(" and (added_by in ("+StringUtils.join(getAppliSourceName(), ",")+") ");
					sbQuery.append(" or source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
					sbQuery.append(") ");
				}
				pst=con.prepareStatement(sbQuery.toString());
				rst=pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
				while(rst.next()) {
					hmDepart.put(rst.getString("emp_id"), rst.getString("dept_name"));	
				}
				rst.close();
				pst.close();
				
				Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
				if(hmEmpCodeDesig == null) hmEmpCodeDesig = new HashMap<String, String>();
				
				sbQuery = new StringBuilder();
				
				pst = con.prepareStatement("select * from candidate_personal_details where candididate_emp_id > 0 ");
				rst = pst.executeQuery();
				Map<String, String> hmCandToEmp = new HashMap<String, String>();
				while(rst.next()) {
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rst.getString("emp_mname");
						}
					}
					
					hmCandToEmp.put(rst.getString("emp_per_id"), rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname")+" is selected for "+uF.showData(hmEmpCodeDesig.get(rst.getString("candididate_emp_id")), "")+" already!");
				}
				rst.close();
				pst.close();
				
				Map<String, String> hmWorkLocation = CF.getWLocationMap(con, null, null);
				Map<String, String> hmEmpname = CF.getEmpNameMap(con, null, null);
				Map<String,String> hmDepartment= CF.getDepartmentMap(con, null, null);
				
				//===start parvez date: 20-10-2021===
				List<String> onBoardedEmpList = new ArrayList<String>();
				pst=con.prepareStatement("select emp_per_id from candidate_personal_details where emp_email in (select emp_email from employee_personal_details where is_alive='true') " +
						"or emp_email in (select emp_email_sec from employee_personal_details where is_alive='true')");
				rst = pst.executeQuery();
				while(rst.next()) {
					onBoardedEmpList.add(rst.getString("emp_per_id"));
				}
				rst.close();
				pst.close();
				//===end parvez date: 20-10-2021===
				
				Map<String, String> hmTodayIndCount = new HashMap<String, String>();
				Map<String, List<List<String>>> hmTodayInduction = new HashMap<String, List<List<String>>>();
				StringBuilder sbQue = new StringBuilder();
				/*sbQue.append("select cad.candididate_emp_id,cpd.is_emp_live,cad.candidate_joining_date,emp_fname,emp_mname,emp_lname,wlocation,designation_id," +
					" cad.candidate_hr_comments,rd.added_by,cad.recruitment_id,emp_per_id,rd.close_job_status from candidate_personal_details cpd, " +
					" candidate_application_details cad inner join recruitment_details rd on cad.recruitment_id=rd.recruitment_id where " +
					" cpd.emp_per_id = cad.candidate_id and (cad.offer_backout_status=0 or cad.offer_backout_status is null) and cad.candidate_status=1 and cad.candidate_final_status=1 and cad.recruitment_id " +
					"in ("+getCurrRecruitId()+") and cad.candidate_joining_date = ? ");*/
				sbQue.append("select cad.candididate_emp_id,cad.candidate_id,cpd.is_emp_live,cad.candidate_joining_date,emp_fname,emp_mname,emp_lname,wlocation,designation_id," +
						" cad.candidate_hr_comments,rd.added_by,cad.recruitment_id,emp_per_id,rd.close_job_status from candidate_personal_details cpd, " +
						" candidate_application_details cad "
						+ " inner join recruitment_details rd on cad.recruitment_id=rd.recruitment_id "
						+ " where cpd.emp_per_id = cad.candidate_id and (cad.offer_backout_status=0 or cad.offer_backout_status is null) and cad.candidate_status=1 and cad.candidate_final_status=1 and cad.recruitment_id " +
						"in ("+getCurrRecruitId()+") and cad.candidate_joining_date = ? and cad.candididate_emp_id is null");
				if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
					sbQuery.append(" and cad.source_type = "+uF.parseToInt(getAppliSourceType())+" ");
					if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
						if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
							sbQuery.append(" and cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
							sbQuery.append(" and cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						}
					}
				} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					sbQuery.append(" and (cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					sbQuery.append(" or cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
				}
				/*if(strUserType != null && strUserType.equals(RECRUITER)) {
					sbQue.append(" and cad.added_by = "+uF.parseToInt(strSessionEmpId)+" ");
				}*/
				
				pst=con.prepareStatement(sbQue.toString());
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				rst=pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
	//			System.out.println("pst today ===> "+pst);
				int todayCnt=0;
				while(rst.next()) {
					List<List<String>> alexportInductionToday = hmTodayInduction.get(rst.getString("recruitment_id"));
					if (alexportInductionToday == null)alexportInductionToday =new ArrayList<List<String>>();
					
					boolean closeFlag = false;
					if(uF.parseToBoolean(rst.getString("close_job_status"))) { 
						closeFlag = true;
					}
					
					List<String> alinner=new ArrayList<String>();
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rst.getString("emp_mname");
						}
					}
					
					alinner.add(rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
					alinner.add(hmDesignation.get(rst.getString("designation_id")));
					alinner.add( hmWorkLocation.get(rst.getString("wlocation")));
					alinner.add(hmDepart.get(rst.getString("added_by")));
					alinner.add(hmEmpname.get(rst.getString("added_by")));
					alinner.add(uF.getDateFormat(rst.getString("candidate_joining_date"), DBDATE, CF.getStrReportDateFormat()));
//					if(!closeFlag) {
						if(rst.getInt("candididate_emp_id")>0 && rst.getBoolean("is_emp_live") == true) {
							alinner.add("Joined on " + rst.getString("candidate_joining_date"));
						} else if(rst.getInt("candididate_emp_id")>0) {
							Date date1 = uF.getDateFormat(uF.getDateFormat(rst.getString("candidate_joining_date"), DBDATE, DBDATE), DBDATE);
							Date date2 = uF.getDateFormat(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DBDATE), DBDATE);
							if(rst.getDate("candidate_joining_date")!=null && (date1.equals(date2))) {
//								alinner.add("Onboarding form filled up <br/> <a href=\"javascript:void(0)\" name=\"addclick\" onclick=\" window.location='AddEmployee.action?mode=onboard&step=7&operation=EO&empId="+rst.getString("candididate_emp_id")+"';\">Please Fill Up Official Details</a>");
								
						//===start parvez date: 21-10-2021===
								if(onBoardedEmpList.contains(rst.getString("emp_per_id"))){
									alinner.add("Onboarding form filled up <br/> <a href=\"javascript:void(0)\" name=\"addclick\" onclick=\"alert('"+rst.getString("emp_fname")+" "+rst.getString("emp_lname")+" is already onboarded');\">Please Fill Up Official Details</a>");
								} else {
									alinner.add("Onboarding form filled up <br/> <a href=\"javascript:void(0)\" name=\"addclick\" onclick=\" window.location='AddEmployee.action?mode=onboard&step=7&operation=EO&empId="+rst.getString("candididate_emp_id")+"';\">Please Fill Up Official Details</a>");
								}
						//===start parvez date: 21-10-2021===
								
							} else {
								alinner.add("Onboarding form filled up");
							}
						} else {
							if(strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(ADMIN))) {
								if(hmCandToEmp.containsKey(rst.getString("emp_per_id"))) {
									alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"alert('"+hmCandToEmp.get(rst.getString("emp_per_id"))+"');\">Resend onboarding form to candidate</a></span> " +
											"<span style=\"float: right;\"><a href=\"javascript:void(0)\" name=\"addclick\" onclick=\"alert('"+hmCandToEmp.get(rst.getString("emp_per_id"))+"');\">Onboarding Form</a></span>");
								} else {
//									alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"resendOnboardFormToCandidate('"+hmDepartment.get(rst.getString("added_by"))+"','"+rst.getString("emp_per_id")+"','"+rst.getString("recruitment_id")+"');\">Resend onboarding form to candidate</a></span> " +
//											"<span style=\"float: right;\"><a href=\"javascript:void(0)\" name=\"addclick\" onclick=\" window.location='Onboard.action?depart_id="+hmDepartment.get(rst.getString("added_by"))+"&candidateId="+rst.getString("emp_per_id")+"&recruitId="+rst.getString("recruitment_id")+"';\">Onboarding Form</a></span>");
							
						//===start parvez date: 21-10-2021===
									if(onBoardedEmpList.contains(rst.getString("emp_per_id"))){
										alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"alert('"+rst.getString("emp_fname")+" "+rst.getString("emp_lname")+" is already onboarded'); \">Resend onboarding form to candidate</a></span> " +
												"<span style=\"float: right;\"><a href=\"javascript:void(0)\" name=\"addclick\" onclick=\" alert('"+rst.getString("emp_fname")+" "+rst.getString("emp_lname")+" is already onboarded');\">Onboarding Form</a></span>");
									} else {
										alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"resendOnboardFormToCandidate('"+hmDepartment.get(rst.getString("added_by"))+"','"+rst.getString("emp_per_id")+"','"+rst.getString("recruitment_id")+"');\">Resend onboarding form to candidate</a></span> " +
												"<span style=\"float: right;\"><a href=\"javascript:void(0)\" name=\"addclick\" onclick=\" window.location='Onboard.action?depart_id="+hmDepartment.get(rst.getString("added_by"))+"&candidateId="+rst.getString("emp_per_id")+"&recruitId="+rst.getString("recruitment_id")+"';\">Onboarding Form</a></span>");
									}
						//===end parvez date: 21-10-2021===
									
								}
							} else {
								alinner.add("");
							}
						}
					/*} else {
						if(rst.getInt("candididate_emp_id")>0 && rst.getBoolean("is_emp_live") == true) {
							alinner.add("Joined on " + rst.getString("candidate_joining_date"));
						} else if(rst.getInt("candididate_emp_id")>0) {
							alinner.add("Onboarding form filled up <br/> Please Fill Up Official Details");
						} else {
							alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> Resend onboarding form to candidate</span> " +
									"<span style=\"float: right;\">Onboarding Form</span>");  
						}
					}*/
					alinner.add(rst.getString("emp_per_id"));
					
					alexportInductionToday.add(alinner);
	
					todayCnt = uF.parseToInt(hmTodayIndCount.get(rst.getString("recruitment_id")));
					todayCnt++;
					hmTodayIndCount.put(rst.getString("recruitment_id"), String.valueOf(todayCnt));
					
					hmTodayInduction.put(rst.getString("recruitment_id"), alexportInductionToday);
				}
				rst.close();
				pst.close();
				
				
				Map<String, String> hmTomorrowIndCount = new HashMap<String, String>();
				Map<String, List<List<String>>> hmTomorrowInduction = new HashMap<String, List<List<String>>>();
				sbQue = new StringBuilder();
				/*sbQue.append("select cad.candididate_emp_id,cpd.is_emp_live,cad.candidate_joining_date,emp_fname,emp_mname,emp_lname,wlocation,designation_id," +
					"cad.candidate_hr_comments,rd.added_by,cad.recruitment_id,emp_per_id,rd.close_job_status from candidate_personal_details cpd, " +
					"candidate_application_details cad inner join recruitment_details rd on cad.recruitment_id=rd.recruitment_id where " +
					"cpd.emp_per_id = cad.candidate_id and (cad.offer_backout_status=0 or cad.offer_backout_status is null) and cad.candidate_status=1 and cad.candidate_final_status=1 and cad.recruitment_id " +
					"in ("+getCurrRecruitId()+") and cad.candidate_joining_date = ?");*/
				sbQue.append("select cad.candididate_emp_id,cad.candidate_id,cpd.is_emp_live,cad.candidate_joining_date,emp_fname,emp_mname,emp_lname,wlocation,designation_id," +
						"cad.candidate_hr_comments,rd.added_by,cad.recruitment_id,emp_per_id,rd.close_job_status from candidate_personal_details cpd, " +
						"candidate_application_details cad inner join recruitment_details rd on cad.recruitment_id=rd.recruitment_id "
						+ "where cpd.emp_per_id = cad.candidate_id and (cad.offer_backout_status=0 or cad.offer_backout_status is null) and cad.candidate_status=1 and cad.candidate_final_status=1 and cad.recruitment_id " +
						"in ("+getCurrRecruitId()+") and cad.candidate_joining_date = ? and cad.candididate_emp_id is null");
				if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
					sbQuery.append(" and cad.source_type = "+uF.parseToInt(getAppliSourceType())+" ");
					if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
						if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
							sbQuery.append(" and cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
							sbQuery.append(" and cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						}
					}
				} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					sbQuery.append(" and (cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					sbQuery.append(" or cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
				}
				/*if(strUserType != null && strUserType.equals(RECRUITER)) {
					sbQue.append(" and cad.added_by = "+uF.parseToInt(strSessionEmpId)+" ");
				}*/
				pst=con.prepareStatement(sbQue.toString());
				pst.setDate(1, uF.getFutureDate(CF.getStrTimeZone(), 1));
				rst=pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
	//			System.out.println("pst 2morrow ===> "+pst);
				int tomorrowCnt=0;
				while(rst.next()) {
					List<List<String>> alexportInductionTomorrow = hmTomorrowInduction.get(rst.getString("recruitment_id"));
					if (alexportInductionTomorrow == null)alexportInductionTomorrow =new ArrayList<List<String>>();
					
					boolean closeFlag = false;
					if(uF.parseToBoolean(rst.getString("close_job_status"))) { 
						closeFlag = true;
					}
					
					List<String> alinner=new ArrayList<String>();
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rst.getString("emp_mname");
						}
					}
					
					alinner.add(rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
					alinner.add(hmDesignation.get(rst.getString("designation_id")));
					alinner.add(uF.showData(hmWorkLocation.get(rst.getString("wlocation")),""));
					alinner.add(uF.showData(hmDepart.get(rst.getString("added_by")), ""));
					alinner.add(hmEmpname.get(rst.getString("added_by")));
					alinner.add(uF.getDateFormat(rst.getString("candidate_joining_date"), DBDATE, CF.getStrReportDateFormat()));
//					if(!closeFlag) {
						if(rst.getInt("candididate_emp_id")>0 && rst.getBoolean("is_emp_live") == true) {
							alinner.add("Joined on " + rst.getString("candidate_joining_date"));
						} else if(rst.getInt("candididate_emp_id")>0) {
							alinner.add("Onboarding form filled up");
						} else {
							if(strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(ADMIN))) {
								if(hmCandToEmp.containsKey(rst.getString("emp_per_id"))) {
									alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"alert('"+hmCandToEmp.get(rst.getString("emp_per_id"))+"');\">Resend onboarding form to candidate</a></span> " +
										"<span style=\"float: right;\"> <a href=\"javascript:void(0)\" name=\"addclick\" onclick=\"alert('"+hmCandToEmp.get(rst.getString("emp_per_id"))+"');\">Onboarding Form</a></span>");
								} else {
//									alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"resendOnboardFormToCandidate('"+hmDepartment.get(rst.getString("added_by"))+"','"+rst.getString("emp_per_id")+"','"+rst.getString("recruitment_id")+"');\">Resend onboarding form to candidate</a></span> " +
//											"<span style=\"float: right;\"> <a href=\"javascript:void(0)\" name=\"addclick\" onclick=\" window.location='Onboard.action?depart_id="+hmDepartment.get(rst.getString("added_by"))+"&candidateId="+rst.getString("emp_per_id")+"&recruitId="+rst.getString("recruitment_id")+"';\">Onboarding Form</a></span>");
							
							//===start parvez date: 21-10-2021===
									if(onBoardedEmpList.contains(rst.getString("emp_per_id"))){
										alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"alert('"+rst.getString("emp_fname")+" "+rst.getString("emp_lname")+" is already onboarded');\">Resend onboarding form to candidate</a></span> " +
												"<span style=\"float: right;\"> <a href=\"javascript:void(0)\" name=\"addclick\" onclick=\"alert('"+rst.getString("emp_fname")+" "+rst.getString("emp_lname")+" is already onboarded');\">Onboarding Form</a></span>");
									}else{
										alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"resendOnboardFormToCandidate('"+hmDepartment.get(rst.getString("added_by"))+"','"+rst.getString("emp_per_id")+"','"+rst.getString("recruitment_id")+"');\">Resend onboarding form to candidate</a></span> " +
												"<span style=\"float: right;\"> <a href=\"javascript:void(0)\" name=\"addclick\" onclick=\" window.location='Onboard.action?depart_id="+hmDepartment.get(rst.getString("added_by"))+"&candidateId="+rst.getString("emp_per_id")+"&recruitId="+rst.getString("recruitment_id")+"';\">Onboarding Form</a></span>");
									}
							//===end parvez date: 21-10-2021===
									
								}
							} else {
								alinner.add("");
							}
						}
					/*} else {
						if(rst.getInt("candididate_emp_id")>0 && rst.getBoolean("is_emp_live") == true) {
							alinner.add("Joined on " + rst.getString("candidate_joining_date"));
						} else if(rst.getInt("candididate_emp_id")>0) {
							alinner.add("Onboarding form filled up <br/>Please Fill Up Official Details");
						} else {
							alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\">Resend onboarding form to candidate</span> " +
									"<span style=\"float: right;\">Onboarding Form</span>");  
						}
					}*/
					alinner.add(rst.getString("emp_per_id"));
					
					alexportInductionTomorrow.add(alinner);
					
					tomorrowCnt = uF.parseToInt(hmTomorrowIndCount.get(rst.getString("recruitment_id")));
					tomorrowCnt++;
					hmTomorrowIndCount.put(rst.getString("recruitment_id"), String.valueOf(tomorrowCnt));
					
					hmTomorrowInduction.put(rst.getString("recruitment_id"), alexportInductionTomorrow);
				}
				rst.close();
				pst.close();
				
				
				Map<String, String> hmDayAfterTomorrowIndCount = new HashMap<String, String>();
				Map<String, List<List<String>>> hmDayAfterTomorrowInduction = new HashMap<String, List<List<String>>>();
	//			pst=con.prepareStatement("select candididate_emp_id,candidate_joining_date,emp_fname, emp_lname,wlocation,designation_id,candidate_hr_comments,rd.added_by,cpd.recruitment_id,emp_per_id from candidate_personal_details cpd inner join recruitment_details rd on cpd.recruitment_id=rd.recruitment_id where candidate_status=1 and candidate_final_status=1 and candidate_joining_date >= ?");
				sbQue = new StringBuilder();
				/*sbQue.append("select cad.candididate_emp_id,cpd.is_emp_live,cad.candidate_joining_date,emp_fname,emp_mname,emp_lname,wlocation,designation_id," +
					"cad.candidate_hr_comments,rd.added_by,cad.recruitment_id,emp_per_id,rd.close_job_status from candidate_personal_details cpd, " +
					"candidate_application_details cad inner join recruitment_details rd on cad.recruitment_id=rd.recruitment_id " +
					"where cpd.emp_per_id = cad.candidate_id and (cad.offer_backout_status=0 or cad.offer_backout_status is null) and cad.candidate_status=1 and cad.candidate_final_status=1 and cad.recruitment_id " +
					"in ("+getCurrRecruitId()+") and cad.candidate_joining_date >= ? ");*/
				sbQue.append("select cad.candididate_emp_id,cad.candidate_id,cpd.is_emp_live,cad.candidate_joining_date,emp_fname,emp_mname,emp_lname,wlocation,designation_id," +
						"cad.candidate_hr_comments,rd.added_by,cad.recruitment_id,emp_per_id,rd.close_job_status from candidate_personal_details cpd, " +
						"candidate_application_details cad inner join recruitment_details rd on cad.recruitment_id=rd.recruitment_id "+
						"where cpd.emp_per_id = cad.candidate_id and (cad.offer_backout_status=0 or cad.offer_backout_status is null) and cad.candidate_status=1 and cad.candidate_final_status=1 and cad.recruitment_id " +
						"in ("+getCurrRecruitId()+") and cad.candidate_joining_date >= ? and cad.candididate_emp_id is null");
				if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
					sbQuery.append(" and cad.source_type = "+uF.parseToInt(getAppliSourceType())+" ");
					if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
						if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
							sbQuery.append(" and cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
							sbQuery.append(" and cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						}
					}
				} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					sbQuery.append(" and (cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					sbQuery.append(" or cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
				}
				/*if(strUserType != null && strUserType.equals(RECRUITER)) {
					sbQue.append(" and cad.added_by = "+uF.parseToInt(strSessionEmpId)+" ");
				}*/
				pst=con.prepareStatement(sbQue.toString());
				pst.setDate(1, uF.getFutureDate(CF.getStrTimeZone(), 2));
				
				rst=pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
	//			System.out.println("pst day after 2morrow ===> "+pst);
				int dayAfterTomorrowCnt=0;
				while(rst.next()) {
					List<List<String>> alexportInductionRest = hmDayAfterTomorrowInduction.get(rst.getString("recruitment_id"));
					if (alexportInductionRest == null)alexportInductionRest =new ArrayList<List<String>>();
					
					boolean closeFlag = false;
					if(uF.parseToBoolean(rst.getString("close_job_status"))) { 
						closeFlag = true;
					}
					
					List<String> alinner=new ArrayList<String>();
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rst.getString("emp_mname");
						}
					}
					
					alinner.add(rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
					alinner.add(hmDesignation.get(rst.getString("designation_id")));
					alinner.add(uF.showData(hmWorkLocation.get(rst.getString("wlocation")),""));
					alinner.add(uF.showData(hmDepart.get(rst.getString("added_by")),""));
					alinner.add(hmEmpname.get(rst.getString("added_by")));
					alinner.add(uF.getDateFormat(rst.getString("candidate_joining_date"), DBDATE, CF.getStrReportDateFormat()));
					
//					if(!closeFlag) {
						if(rst.getInt("candididate_emp_id")>0 && rst.getBoolean("is_emp_live") == true) {
							alinner.add("Joined on " + rst.getString("candidate_joining_date"));
						} else if(rst.getInt("candididate_emp_id")>0) {
							alinner.add("Onboarding form filled up");
						} else {
							if(strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(ADMIN))) {
							if(hmCandToEmp.containsKey(rst.getString("emp_per_id"))) {
								alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"alert('"+hmCandToEmp.get(rst.getString("emp_per_id"))+"');\">Resend onboarding form to candidate</a></span> " +
									"<span style=\"float: right;\"><a href=\"javascript:void(0)\" name=\"addclick\" onclick=\"alert('"+hmCandToEmp.get(rst.getString("emp_per_id"))+"');\">Onboarding Form</a></span>");
							} else {
//								alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"resendOnboardFormToCandidate('"+hmDepartment.get(rst.getString("added_by"))+"','"+rst.getString("emp_per_id")+"','"+rst.getString("recruitment_id")+"');\">Resend onboarding form to candidate</a></span> " +
//										"<span style=\"float: right;\"><a href=\"javascript:void(0)\" name=\"addclick\" onclick=\" window.location='Onboard.action?depart_id="+hmDepartment.get(rst.getString("added_by"))+"&candidateId="+rst.getString("emp_per_id")+"&recruitId="+rst.getString("recruitment_id")+"';\">Onboarding Form</a></span>");
								
					//===start parvez date: 21-10-2021===
								if(onBoardedEmpList.contains(rst.getString("emp_per_id"))){
									alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"alert('"+rst.getString("emp_fname")+" "+rst.getString("emp_lname")+" is already onboarded');\">Resend onboarding form to candidate</a></span> " +
											"<span style=\"float: right;\"><a href=\"javascript:void(0)\" name=\"addclick\" onclick=\" alert('"+rst.getString("emp_fname")+" "+rst.getString("emp_lname")+" is already onboarded');\">Onboarding Form</a></span>");
								} else {
									alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"resendOnboardFormToCandidate('"+hmDepartment.get(rst.getString("added_by"))+"','"+rst.getString("emp_per_id")+"','"+rst.getString("recruitment_id")+"');\">Resend onboarding form to candidate</a></span> " +
											"<span style=\"float: right;\"><a href=\"javascript:void(0)\" name=\"addclick\" onclick=\" window.location='Onboard.action?depart_id="+hmDepartment.get(rst.getString("added_by"))+"&candidateId="+rst.getString("emp_per_id")+"&recruitId="+rst.getString("recruitment_id")+"';\">Onboarding Form</a></span>");
								}
					//===end parvez date: 21-10-2021===
							}
							} else {
								alinner.add("");
							}
						}
					/*} else {
						if(rst.getInt("candididate_emp_id")>0 && rst.getBoolean("is_emp_live") == true) {
							alinner.add("Joined on " + rst.getString("candidate_joining_date"));
						} else if(rst.getInt("candididate_emp_id")>0) {
							alinner.add("Onboarding form filled up <br/>Please Fill Up Official Details");
						} else {
							alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\">Resend onboarding form to candidate</span> " +
									"<span style=\"float: right;\">Onboarding Form</span>");
						}
					}*/
					alinner.add(rst.getString("emp_per_id"));
					
					alexportInductionRest.add(alinner);
					
					dayAfterTomorrowCnt = uF.parseToInt(hmDayAfterTomorrowIndCount.get(rst.getString("recruitment_id")));
					dayAfterTomorrowCnt++;
					hmDayAfterTomorrowIndCount.put(rst.getString("recruitment_id"), String.valueOf(dayAfterTomorrowCnt));
					
					hmDayAfterTomorrowInduction.put(rst.getString("recruitment_id"), alexportInductionRest);
				}
				rst.close();
				pst.close();
				
				Map<String, String> hmPendingIndCount = new HashMap<String, String>();
				Map<String, String> hmOnBoardedCount = new HashMap<String, String>();
				Map<String, List<List<String>>> hmPendingInduction = new HashMap<String, List<List<String>>>();
	//			pst=con.prepareStatement("select candididate_emp_id,candidate_joining_date,emp_fname, emp_lname,wlocation,designation_id,candidate_hr_comments,rd.added_by,cpd.recruitment_id,emp_per_id from candidate_personal_details cpd inner join recruitment_details rd on cpd.recruitment_id=rd.recruitment_id where candidate_status=1 and candidate_final_status=1 and candidate_joining_date < ?");
				sbQue = new StringBuilder();
				/*sbQue.append("select cad.candididate_emp_id,cpd.is_emp_live,cad.candidate_joining_date,emp_fname,emp_mname,emp_lname,wlocation,designation_id," +
					" cad.candidate_hr_comments,rd.added_by,cad.recruitment_id,emp_per_id,rd.close_job_status,eod.*  from candidate_personal_details cpd," +
					" candidate_application_details cad "+
					" inner join recruitment_details rd on cad.recruitment_id=rd.recruitment_id "+ 
					" inner join employee_official_details eod on eod.emp_id = cad.candididate_emp_id " +
					" where cpd.emp_per_id = cad.candidate_id and (cad.offer_backout_status=0 or cad.offer_backout_status is null) and cad.candidate_status=1 and cad.candidate_final_status=1 and cad.recruitment_id " +
					" in ("+getCurrRecruitId()+") and cad.candidate_joining_date < ? ");*/
				sbQue.append("select cad.candididate_emp_id,cad.candidate_id,cpd.is_emp_live,cad.candidate_joining_date,emp_fname,emp_mname,emp_lname,wlocation,designation_id,grade_id," +
						" cad.candidate_hr_comments,rd.added_by,cad.recruitment_id,emp_per_id,rd.org_id,rd.close_job_status  from candidate_personal_details cpd," +
						" candidate_application_details cad "+
						" inner join recruitment_details rd on cad.recruitment_id=rd.recruitment_id "+ 
						" where cpd.emp_per_id = cad.candidate_id and (cad.offer_backout_status=0 or cad.offer_backout_status is null) and cad.candidate_status=1 and cad.candidate_final_status=1 and cad.recruitment_id " +
						" in ("+getCurrRecruitId()+") and cad.candidate_joining_date < ? and cad.candididate_emp_id is null");
				if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
					sbQuery.append(" and cad.source_type = "+uF.parseToInt(getAppliSourceType())+" ");
					if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
						if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
							sbQuery.append(" and cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
							sbQuery.append(" and cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						}
					}
				} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					sbQuery.append(" and (cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					sbQuery.append(" or cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
				}
				/*if(strUserType != null && strUserType.equals(RECRUITER)) {
					sbQue.append(" and cad.added_by = "+uF.parseToInt(strSessionEmpId)+" ");
				}*/
				pst = con.prepareStatement(sbQue.toString());
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
//				System.out.println("pst pending ===> "+pst);
				int pendingCnt=0;
				while(rst.next()) {
					
					if(!rst.getBoolean("is_emp_live")) {
						List<List<String>> alexportInductionPending = hmPendingInduction.get(rst.getString("recruitment_id"));
						if (alexportInductionPending == null)alexportInductionPending =new ArrayList<List<String>>();
						
						boolean closeFlag = false;
						if(uF.parseToBoolean(rst.getString("close_job_status"))) { 
							closeFlag = true;
						}
						
						List<String> alinner=new ArrayList<String>();
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rst.getString("emp_mname");
							}
						}
						
						alinner.add(rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
						alinner.add(hmDesignation.get(rst.getString("designation_id")));
						alinner.add(uF.showData(hmWorkLocation.get(rst.getString("wlocation")),""));
						alinner.add(uF.showData(hmDepart.get(rst.getString("added_by")),""));
						alinner.add(hmEmpname.get(rst.getString("added_by")));
						alinner.add(uF.getDateFormat(rst.getString("candidate_joining_date"), DBDATE, CF.getStrReportDateFormat()));
//						if(!closeFlag) {
							if(rst.getInt("candididate_emp_id")>0 && rst.getBoolean("is_emp_live") == true) {
								alinner.add("Joined on " + rst.getString("candidate_joining_date"));
							} else if(rst.getInt("candididate_emp_id")>0) {
//								alinner.add("Onboarding form filled up <br/> <a href=\"javascript:void(0)\" name=\"addclick\" onclick=\" window.location='AddEmployee.action?mode=onboard&step=7&operation=EO&empId="+rst.getString("candididate_emp_id")+"';\">Please Fill Up Official Details</a>");
								
					//===start parvez date: 21-10-2021===
								if(onBoardedEmpList.contains(rst.getString("emp_per_id"))){
									alinner.add("Onboarding form filled up <br/> <a href=\"javascript:void(0)\" name=\"addclick\" onclick=\" alert('"+rst.getString("emp_fname")+" "+rst.getString("emp_lname")+" is already onboarded');\">Please Fill Up Official Details</a>");
								} else {
									alinner.add("Onboarding form filled up <br/> <a href=\"javascript:void(0)\" name=\"addclick\" onclick=\" window.location='AddEmployee.action?mode=onboard&step=7&operation=EO&empId="+rst.getString("candididate_emp_id")+"';\">Please Fill Up Official Details</a>");
								}
								
				   //===end parvez date: 21-10-2021===
								
							} else {
								if(strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(ADMIN))) {
									if(hmCandToEmp.containsKey(rst.getString("emp_per_id"))) {
										alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"alert('"+hmCandToEmp.get(rst.getString("emp_per_id"))+"');\">Resend onboarding form to candidate</a></span> " +
											"<span style=\"float: right;\"><a href=\"javascript:void(0)\" name=\"addclick\"  onclick=\"alert('"+hmCandToEmp.get(rst.getString("emp_per_id"))+"');\">Onboarding Form</a></span>");
									} else {
//										alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"resendOnboardFormToCandidate('"+hmDepartment.get(rst.getString("added_by"))+"','"+rst.getString("emp_per_id")+"','"+rst.getString("recruitment_id")+"');\">Resend onboarding form to candidate</a></span> " +
//											"<span style=\"float: right;\"><a href=\"javascript:void(0)\" name=\"addclick\"  onclick=\" window.location='Onboard.action?depart_id="+hmDepartment.get(rst.getString("added_by"))+"&candidateId="+rst.getString("emp_per_id")+"&recruitId="+rst.getString("recruitment_id")+"';\">Onboarding Form</a></span>");
										
								//===start parvez date: 21-10-2021===
										if(onBoardedEmpList.contains(rst.getString("emp_per_id"))){
											alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"alert('"+rst.getString("emp_fname")+" "+rst.getString("emp_lname")+" is already onboarded');\">Resend onboarding form to candidate</a></span> " +
													"<span style=\"float: right;\"><a href=\"javascript:void(0)\" name=\"addclick\"  onclick=\" alert('"+rst.getString("emp_fname")+" "+rst.getString("emp_lname")+" is already onboarded');\">Onboarding Form</a></span>");
										} else {
											alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"resendOnboardFormToCandidate('"+hmDepartment.get(rst.getString("added_by"))+"','"+rst.getString("emp_per_id")+"','"+rst.getString("recruitment_id")+"');\">Resend onboarding form to candidate</a></span> " +
													"<span style=\"float: right;\"><a href=\"javascript:void(0)\" name=\"addclick\"  onclick=\" window.location='Onboard.action?depart_id="+hmDepartment.get(rst.getString("added_by"))+"&candidateId="+rst.getString("emp_per_id")+"&recruitId="+rst.getString("recruitment_id")+"';\">Onboarding Form</a></span>");
										}
								//===end parvez date: 21-10-2021===
									}
								} else {
									alinner.add("");
								}
							}
						/*} else {
							if(rst.getInt("candididate_emp_id")>0 && rst.getBoolean("is_emp_live") == true) {
								alinner.add("Joined on " + rst.getString("candidate_joining_date"));
							} else if(rst.getInt("candididate_emp_id")>0) {
								alinner.add("Onboarding form filled up <br/>Please Fill Up Official Details");
							} else {
								alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\">Resend onboarding form to candidate</span> " +
									"<span style=\"float: right;\">Onboarding Form</span>");
							}
						}*/
						alinner.add(rst.getString("emp_per_id"));
						alinner.add(rst.getString("grade_id"));
						alinner.add(rst.getString("org_id"));
						alinner.add(rst.getString("wlocation"));
						
						alexportInductionPending.add(alinner);
						
						pendingCnt = uF.parseToInt(hmPendingIndCount.get(rst.getString("recruitment_id")));
						pendingCnt++;
						if(rst.getString("grade_id") ==null && rst.getString("org_id") ==null && rst.getString("wlocation") ==null) {
							hmPendingIndCount.put(rst.getString("recruitment_id"), String.valueOf(pendingCnt));
						}
						
						
						if(uF.parseToInt(rst.getString("grade_id")) > 0 && uF.parseToInt(rst.getString("org_id")) > 0 && uF.parseToInt(rst.getString("wlocation")) > 0) {
							hmOnBoardedCount.put(rst.getString("recruitment_id"), String.valueOf(pendingCnt));
						}
						
						hmPendingInduction.put(rst.getString("recruitment_id"), alexportInductionPending);
					}
				}
				rst.close();
				pst.close();
				
				Map<String, List<List<String>>> hmOnBoardedInduction = new HashMap<String, List<List<String>>>();
	//			pst=con.prepareStatement("select candididate_emp_id,candidate_joining_date,emp_fname, emp_lname,wlocation,designation_id,candidate_hr_comments,rd.added_by,cpd.recruitment_id,emp_per_id from candidate_personal_details cpd inner join recruitment_details rd on cpd.recruitment_id=rd.recruitment_id where candidate_status=1 and candidate_final_status=1 and candidate_joining_date < ?");
				sbQue = new StringBuilder();
//				sbQue.append("select cad.candididate_emp_id,cpd.is_emp_live,cad.candidate_joining_date,emp_fname,emp_mname,emp_lname,wlocation,designation_id," +
//					" cad.candidate_hr_comments,rd.added_by,cad.recruitment_id,emp_per_id,rd.close_job_status,eod.*  from candidate_personal_details cpd," +
//					" candidate_application_details cad "+
//					" inner join recruitment_details rd on cad.recruitment_id=rd.recruitment_id "+ 
//					" inner join employee_official_details eod on eod.emp_id = cad.candididate_emp_id " +
//					" where cpd.emp_per_id = cad.candidate_id and (cad.offer_backout_status=0 or cad.offer_backout_status is null) and cad.candidate_status=1 and cad.candidate_final_status=1 and cad.recruitment_id " +
//					" in ("+getCurrRecruitId()+") and cad.candidate_joining_date < ? ");
				
				// Start Dattatray Date : 05-July-21
				//======start parvez on 12-07-2021 added candidate_id======
				sbQue.append("select cad.candididate_emp_id,cad.candidate_id,cpd.is_emp_live,cad.candidate_joining_date,epd.emp_fname,epd.emp_mname,epd.emp_lname,wlocation,designation_id," +
						" cad.candidate_hr_comments,rd.added_by,cad.recruitment_id,epd.emp_per_id,rd.close_job_status,epd.*  from candidate_personal_details cpd," +
						" candidate_application_details cad "+
						" inner join recruitment_details rd on cad.recruitment_id=rd.recruitment_id "+ 
						" inner join employee_personal_details epd on epd.emp_per_id = cad.candididate_emp_id " +
						" where cpd.emp_per_id = cad.candidate_id and (cad.offer_backout_status=0 or cad.offer_backout_status is null) and cad.candidate_status=1 and cad.candidate_final_status=1 and cad.recruitment_id " +
						" in ("+getCurrRecruitId()+") and epd.joining_date IS NOT NULL and epd.is_alive = true and epd.emp_filled_flag = true ");
				//=====end parvez on 12-07-2021=====
				// End Dattatray Date : 05-July-21
				if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
					sbQuery.append(" and cad.source_type = "+uF.parseToInt(getAppliSourceType())+" ");
					if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
						if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
							sbQuery.append(" and cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
							sbQuery.append(" and cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						}
					}
				} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					sbQuery.append(" and (cad.added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					sbQuery.append(" or cad.source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
				}
				/*if(strUserType != null && strUserType.equals(RECRUITER)) {
					sbQue.append(" and cad.added_by = "+uF.parseToInt(strSessionEmpId)+" ");
				}*/
				pst = con.prepareStatement(sbQue.toString());
//				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
//				System.out.println("Induction/1722--pst OnBoardedInduction ===> "+pst);
				int onBoardedCnt=0;
				while(rst.next()) {
					
					if(!rst.getBoolean("is_emp_live")) {
						List<List<String>> alexportOnBoardedInduction = hmOnBoardedInduction.get(rst.getString("recruitment_id"));
						if (alexportOnBoardedInduction == null)alexportOnBoardedInduction =new ArrayList<List<String>>();
						
						boolean closeFlag = false;
						if(uF.parseToBoolean(rst.getString("close_job_status"))) { 
							closeFlag = true;
						}
						
						List<String> alinner=new ArrayList<String>();
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rst.getString("emp_mname");
							}
						}
						
						alinner.add(rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
						alinner.add(hmDesignation.get(rst.getString("designation_id")));
						alinner.add(uF.showData(hmWorkLocation.get(rst.getString("wlocation")),""));
						alinner.add(uF.showData(hmDepart.get(rst.getString("added_by")),""));
						alinner.add(hmEmpname.get(rst.getString("added_by")));
						alinner.add(uF.getDateFormat(rst.getString("candidate_joining_date"), DBDATE, CF.getStrReportDateFormat()));
						if(!closeFlag) {
							if(rst.getInt("candididate_emp_id")>0 && rst.getBoolean("is_emp_live") == true) {
								alinner.add("Joined on " + rst.getString("candidate_joining_date"));
							} else if(rst.getInt("candididate_emp_id")>0) {
//								alinner.add("Onboarding form in progress <br/> <a href=\"javascript:void(0)\" name=\"addclick\" onclick=\" window.location='AddEmployee.action?mode=onboard&step=7&operation=EO&empId="+rst.getString("candididate_emp_id")+"';\">Please Fill Up Official Details</a>");
								
						//===start parvez date: 21-10-2021===
								if(onBoardedEmpList.contains(rst.getString("emp_per_id"))){
									alinner.add("Onboarding form in progress <br/> <a href=\"javascript:void(0)\" name=\"addclick\" onclick=\" alert('"+rst.getString("emp_fname")+" "+rst.getString("emp_lname")+" is already onboarded');\">Please Fill Up Official Details</a>");
								} else {
									alinner.add("Onboarding form in progress <br/> <a href=\"javascript:void(0)\" name=\"addclick\" onclick=\" window.location='AddEmployee.action?mode=onboard&step=7&operation=EO&empId="+rst.getString("candididate_emp_id")+"';\">Please Fill Up Official Details</a>");
								}
						//===end parvez date: 21-10-2021===
								
							} else {
								if(strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(ADMIN))) {
									if(hmCandToEmp.containsKey(rst.getString("emp_per_id"))) {
										alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"alert('"+hmCandToEmp.get(rst.getString("emp_per_id"))+"');\">Resend onboarding form to candidate</a></span> " +
											"<span style=\"float: right;\"><a href=\"javascript:void(0)\" name=\"addclick\"  onclick=\"alert('"+hmCandToEmp.get(rst.getString("emp_per_id"))+"');\">Onboarding Form</a></span>");
									} else {
//										alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"resendOnboardFormToCandidate('"+hmDepartment.get(rst.getString("added_by"))+"','"+rst.getString("emp_per_id")+"','"+rst.getString("recruitment_id")+"');\">Resend onboarding form to candidate</a></span> " +
//											"<span style=\"float: right;\"><a href=\"javascript:void(0)\" name=\"addclick\"  onclick=\" window.location='Onboard.action?depart_id="+hmDepartment.get(rst.getString("added_by"))+"&candidateId="+rst.getString("emp_per_id")+"&recruitId="+rst.getString("recruitment_id")+"';\">Onboarding Form</a></span>");
										
								//===start parvez date: 21-10-2021===
										if(onBoardedEmpList.contains(rst.getString("emp_per_id"))){
											alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"alert('"+rst.getString("emp_fname")+" "+rst.getString("emp_lname")+" is already onboarded');\">Resend onboarding form to candidate</a></span> " +
													"<span style=\"float: right;\"><a href=\"javascript:void(0)\" name=\"addclick\"  onclick=\" alert('"+rst.getString("emp_fname")+" "+rst.getString("emp_lname")+" is already onboarded');\">Onboarding Form</a></span>");
										} else {
											alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\"> <a href=\"javascript:void(0)\" onclick=\"resendOnboardFormToCandidate('"+hmDepartment.get(rst.getString("added_by"))+"','"+rst.getString("emp_per_id")+"','"+rst.getString("recruitment_id")+"');\">Resend onboarding form to candidate</a></span> " +
													"<span style=\"float: right;\"><a href=\"javascript:void(0)\" name=\"addclick\"  onclick=\" window.location='Onboard.action?depart_id="+hmDepartment.get(rst.getString("added_by"))+"&candidateId="+rst.getString("emp_per_id")+"&recruitId="+rst.getString("recruitment_id")+"';\">Onboarding Form</a></span>");
										}
										
								//===end parvez date: 21-10-2021===
										
									}
								} else {
									alinner.add("");
								}
							}
						} else {
							if(rst.getInt("candididate_emp_id")>0 && rst.getBoolean("is_emp_live") == true) {
								alinner.add("Joined on " + rst.getString("candidate_joining_date"));
							} else if(rst.getInt("candididate_emp_id")>0) {
								alinner.add("Onboarding form filled up <br/>Please Fill Up Official Details");
							} else {
								alinner.add("<span id=\"resendOnboardSpan_"+rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")+"\" style=\"float: left;\">Resend onboarding form to candidate</span> " +
									"<span style=\"float: right;\">Onboarding Form</span>");
							}
						}
//						System.out.println("Induction/1777--emp_per_id="+rst.getString("emp_per_id"));
//						alinner.add(rst.getString("emp_per_id"));
						//====start parvez on 12-07-2021====
						alinner.add(rst.getString("candidate_id"));
						//====end parvez on 12-07-2021====
						alexportOnBoardedInduction.add(alinner);
						
						onBoardedCnt = uF.parseToInt(hmOnBoardedCount.get(rst.getString("recruitment_id")));
						onBoardedCnt++;
						hmOnBoardedCount.put(rst.getString("recruitment_id"), String.valueOf(onBoardedCnt));
						
						hmOnBoardedInduction.put(rst.getString("recruitment_id"), alexportOnBoardedInduction);
					}
				}
				rst.close();
				pst.close();
				
	//			System.out.println("hmPendingInduction ===> "+ hmPendingInduction);
				
				request.setAttribute("hmTodayInduction", hmTodayInduction);
				request.setAttribute("hmTomorrowInduction", hmTomorrowInduction);
				request.setAttribute("hmDayAfterTomorrowInduction", hmDayAfterTomorrowInduction);
				request.setAttribute("hmPendingInduction", hmPendingInduction);
				request.setAttribute("hmTodayIndCount", hmTodayIndCount);
				request.setAttribute("hmTomorrowIndCount", hmTomorrowIndCount);
				request.setAttribute("hmDayAfterTomorrowIndCount", hmDayAfterTomorrowIndCount);
				request.setAttribute("hmPendingIndCount", hmPendingIndCount);
				request.setAttribute("hmOnBoardedInduction", hmOnBoardedInduction);// Created Dattatray Date : 05-July-21
				request.setAttribute("hmOnBoardedCount", hmOnBoardedCount);
				
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	public String getRecruitId() {
		return recruitId;
	}


	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}


	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getLocation() {
		return location;
	}

	public void setLocation(String[] location) {
		this.location = location;
	}

	public String[] getDesignation() {
		return designation;
	}

	public void setDesignation(String[] designation) {
		this.designation = designation;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public List<FillWLocation> getWorkLocationList() {
		return workLocationList;
	}

	public void setWorkLocationList(List<FillWLocation> workLocationList) {
		this.workLocationList = workLocationList;
	}

	public String getProPage() {
		return proPage;
	}

	public void setProPage(String proPage) {
		this.proPage = proPage;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}

	public String getStrVm() {
		return strVm;
	}

	public void setStrVm(String strVm) {
		this.strVm = strVm;
	}

	public List<FillSourceTypeAndName> getSourceTypeList() {
		return sourceTypeList;
	}

	public void setSourceTypeList(List<FillSourceTypeAndName> sourceTypeList) {
		this.sourceTypeList = sourceTypeList;
	}

	public List<FillSourceTypeAndName> getSourceNameList() {
		return sourceNameList;
	}

	public void setSourceNameList(List<FillSourceTypeAndName> sourceNameList) {
		this.sourceNameList = sourceNameList;
	}

	public String getAppliSourceType() {
		return appliSourceType;
	}

	public void setAppliSourceType(String appliSourceType) {
		this.appliSourceType = appliSourceType;
	}

	public String[] getAppliSourceName() {
		return appliSourceName;
	}

	public void setAppliSourceName(String[] appliSourceName) {
		this.appliSourceName = appliSourceName;
	}

	public String getCurrRecruitId() {
		return currRecruitId;
	}

	public void setCurrRecruitId(String currRecruitId) {
		this.currRecruitId = currRecruitId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	
}