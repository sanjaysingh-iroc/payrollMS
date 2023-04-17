package com.konnect.jpms.recruitment;

import java.io.File;
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
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Offers extends ActionSupport implements ServletRequestAware,IStatements{

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId = null;
	String strUserType = null;
	CommonFunctions CF = null;
	String recruitId;
	
	String alertStatus;
	String alert_type;
	
	String dataType;
	
	String strSearchJob;
	
	String f_org;
	String[] location;
	String[] designation;
	
	List<FillOrganisation> organisationList;
	List<FillDesig> desigList;
	List<FillWLocation> workLocationList;
	
	List<FillSourceTypeAndName> sourceTypeList;
	List<FillSourceTypeAndName> sourceNameList;
	
	String proPage;
	String minLimit;
	
	String strVm;
	
	String appliSourceType;
	String[] appliSourceName;
	
	String currRecruitId;
	
	public String execute() throws Exception {
     
		session = request.getSession(); 
		CF= (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(USERTYPE);
		UtilityFunctions uF=new UtilityFunctions();
//		System.out.println("getRecruitId() 00 ===>> " + getRecruitId());
		
		request.setAttribute(PAGE, "/jsp/recruitment/Offers.jsp");
		request.setAttribute(TITLE,"Offers");
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		removeTemporaryFile(uF);
		
		if(strUserType != null && strUserType.equals(HRMANAGER) && getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(CANDIDATE_OFFER_ACCEPTREJECT_ALERT)){
			updateUserAlerts();
		}
		
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
		
		
		
		organisationList = new FillOrganisation(request).fillOrganisation();
		workLocationList = new FillWLocation(request).fillWLocation(getF_org());
		desigList = new FillDesig(request).fillDesig(uF.parseToInt(getF_org()));
		
//		getSelectedFilter(uF);
//		getSearchAutoCompleteData(uF);
        allApplication(uF, getRecruitId());
        getRecruitmentwiseAssessmentScoreCard(uF);
        
//        System.out.println("getRecruitId() ===>> " + getRecruitId());
        
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
	

	
	private void getRecruitmentwiseAssessmentScoreCard(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			if (getCurrRecruitId() != null && !getCurrRecruitId().equals("")) {
				StringBuilder sbQue = new StringBuilder();
				sbQue.append("select * from(select sum(marks) as marks ,sum(weightage) as weightage,assessment_details_id,round_id,recruitment_id," +
					"candidate_id from assessment_question_answer where candidate_id>0 and recruitment_id in ("+getCurrRecruitId()+") ");
				if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
					sbQue.append(" and candidate_id in (select candidate_id from candidate_application_details where candidate_final_status=1 and " +
						" source_type= "+uF.parseToInt(getAppliSourceType())+" ");
					if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
						if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
							sbQue.append(" and added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
							sbQue.append(" and source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						}
					}
					sbQue.append(") ");
				} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					sbQue.append(" and candidate_id in (select candidate_id from candidate_application_details where candidate_id>0 and " +
							" candidate_final_status=1 ");
					sbQue.append(" and (added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					sbQue.append(" or source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
					sbQue.append(") ");
				}
				sbQue.append(" group by recruitment_id,candidate_id,round_id,assessment_details_id)as a");
				
				pst = con.prepareStatement(sbQue.toString());
//				"select * from(select sum(marks) as marks ,sum(weightage) as weightage,assessment_details_id,round_id " +
//				",recruitment_id,candidate_id from assessment_question_answer group by recruitment_id,candidate_id,round_id,assessment_details_id)as a"
				rs = pst.executeQuery();
				Map<String, String> hmAssessRateRecruitAndRoundIdWise = new HashMap<String, String>();
				while (rs.next()) {
					double dblMarks = uF.parseToDouble(rs.getString("marks"));
					double dblWeightage = uF.parseToDouble(rs.getString("weightage"));
					String aggregate = "0";
					if(dblWeightage>0) {
						aggregate = uF.formatIntoTwoDecimal((dblMarks * 100)/dblWeightage);
					}
					hmAssessRateRecruitAndRoundIdWise.put(rs.getString("recruitment_id")+"_"+rs.getString("candidate_id")+"_"+rs.getString("round_id")+"_"+rs.getString("assessment_details_id"), aggregate);
				}
				rs.close();
				pst.close();
				
				request.setAttribute("hmAssessRateRecruitAndRoundIdWise", hmAssessRateRecruitAndRoundIdWise);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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
			sbQuery.append("select job_code,job_title,skills,essential_skills from recruitment_details where job_approval_status=1 ");
			/*if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and close_job_status = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and close_job_status = true ");
			}*/
			if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
				sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where candidate_final_status=1 " +
						" and source_type="+uF.parseToInt(getAppliSourceType())+" ");
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
						" candidate_final_status=1 ");
				sbQuery.append(" and (added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
				sbQuery.append(" or source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
				sbQuery.append(") ");
			}
			
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(RECRUITER))){
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
				if(rs.getString("job_title")!=null && !rs.getString("job_title").trim().equals("")){
					setJobList.add(rs.getString("job_title"));
				}
				
				if(rs.getString("skills")!=null && !rs.getString("skills").trim().equals("")){
					if(sbSkills == null){
						sbSkills = new StringBuilder();
						sbSkills.append(rs.getString("skills"));
					} else {
						sbSkills.append(","+rs.getString("skills"));
					}
				}
				
				if(rs.getString("essential_skills")!=null && !rs.getString("essential_skills").trim().equals("")){
					if(sbSkills == null){
						sbSkills = new StringBuilder();
						sbSkills.append(rs.getString("essential_skills"));
					} else {
						sbSkills.append(","+rs.getString("essential_skills"));
					}
				}
			}
			rs.close();
			pst.close();
			
			if(sbSkills != null){
				List<String> alSkills = Arrays.asList(sbSkills.toString().trim().split(","));
				StringBuilder sbSkillsId = null;
				for(int i=0; alSkills != null && i < alSkills.size(); i++){
					if(alSkills.get(i)!=null && !alSkills.get(i).trim().equals("") && uF.parseToInt(alSkills.get(i).trim()) > 0){
						if(sbSkillsId == null){
							sbSkillsId = new StringBuilder();
							sbSkillsId.append(alSkills.get(i).trim());
						} else {
							sbSkillsId.append(","+alSkills.get(i).trim());
						}
					}
				}
				
				if(sbSkillsId!=null){
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
			while (it.hasNext()){
				String strData = it.next();
				if(sbData == null){
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null){
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
	
	private void updateUserAlerts() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
		try {
			con = db.makeConnection(con);
			
			String strDomain = request.getServerName().split("\\.")[0];
			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
			userAlerts.setStrDomain(strDomain);
			userAlerts.setStrEmpId(""+nEmpId);
			userAlerts.set_type(CANDIDATE_OFFER_ACCEPTREJECT_ALERT);
			userAlerts.setStatus(UPDATE_ALERT);
			Thread t = new Thread(userAlerts);
			t.run();
//			System.out.println("in Appraisal UserAlerts ...");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
	
	
	
	private void allApplication(UtilityFunctions uF, String recruitId) {
		
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;	
		ResultSet rs = null;
		
		try {
			con=db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String,List<String>> hmHeaderInfo=new HashMap<String, List<String>>();
			Map<String, String> hmCandiImage = CF.getCandidateImageMap(con);
			request.setAttribute("hmCandiImage", hmCandiImage);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			request.setAttribute("hmEmpName", hmEmpName);
			
			Map<String, String> hmJobStatus = new HashMap<String, String>();
			
			List<String> alSkillId = null;
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){	
				pst = con.prepareStatement("select skill_id from skills_details where skill_name is not null " +
						"and skill_name != '' and upper(skill_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'");
				rs = pst.executeQuery();
				while(rs.next()){
					if(alSkillId == null){
						alSkillId = new ArrayList<String>();
						alSkillId.add(rs.getString("skill_id"));
					} else {
						alSkillId.add(rs.getString("skill_id"));
					}
				}
				
				rs.close();
				pst.close();
			}
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select job_code,recruitment_id,no_position,priority_job_int,close_job_status,job_title from recruitment_details where job_approval_status = 1 ");
			if (recruitId != null && !recruitId.equals("") && uF.parseToInt(recruitId) > 0){
				sbQuery.append(" and recruitment_id = " + recruitId + " ");
			}
			pst=con.prepareStatement(sbQuery.toString());
			rs=pst.executeQuery();
//			System.out.println("pst===> "+ pst);
			StringBuilder sbRecruitId = null;
			Map<String, Map<String, String>> hmcandiStarRecruitwise = new HashMap<String, Map<String,String>>();
			while(rs.next()){
				if(sbRecruitId == null){
					sbRecruitId = new StringBuilder();
					sbRecruitId.append(rs.getString("recruitment_id"));
				} else {
					sbRecruitId.append(","+rs.getString("recruitment_id"));
				}
				
				hmJobStatus.put(rs.getString("recruitment_id"), uF.parseToBoolean(rs.getString("close_job_status"))+"");
				
				List<String> alInner=new ArrayList<String>();
				alInner.add(rs.getString("job_code"));
				alInner.add(rs.getString("no_position"));
				alInner.add(rs.getString("priority_job_int"));
				alInner.add(rs.getString("job_title"));
				hmHeaderInfo.put(rs.getString("recruitment_id"), alInner);
				
				Map<String, String> hmCandiStars = getCandidateList(con, uF, rs.getString("recruitment_id"));
				hmcandiStarRecruitwise.put(rs.getString("recruitment_id"), hmCandiStars);
			}
			rs.close();
			pst.close();
			if(sbRecruitId != null) {
				setCurrRecruitId(sbRecruitId.toString());
				getSourceDetails(con,uF,sbRecruitId.toString());
			}
			
			request.setAttribute("hmcandiStarRecruitwise", hmcandiStarRecruitwise);
			request.setAttribute("hmHeaderInfo", hmHeaderInfo);
			request.setAttribute("hmJobStatus", hmJobStatus);
			
			Map<String,Map<String,List<String>>> hmAcceptedName=new HashMap<String, Map<String,List<String>>>();
			Map<String,Map<String,List<String>>> hmRejectedName=new HashMap<String, Map<String,List<String>>>();
			Map<String,Map<String,List<String>>> hmOfferedName=new HashMap<String, Map<String,List<String>>>();
			
			if(getCurrRecruitId() !=null && !getCurrRecruitId().equals("")) {
				sbQuery = new StringBuilder();
				sbQuery.append("select cad.ctc_offered,cad.candidate_joining_date,emp_fname,emp_mname,emp_lname,cad.recruitment_id,emp_per_id,cad.candidate_status, " +
					"cad.offer_backout_status from candidate_personal_details cpd, candidate_application_details cad where cpd.emp_per_id = cad.candidate_id and " +
					" cad.application_status=2 and cad.candidate_final_status=1 and cad.recruitment_id in ("+getCurrRecruitId()+") ");
				/*if(strUserType != null && strUserType.equals(RECRUITER)) {
					sbQuery.append(" and cad.added_by = "+uF.parseToInt(strSessionEmpId)+" ");
				}*/
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
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
//				System.out.println("pst ===>> " + pst);
	//			System.out.println("new Date ===> " + new Date());
				List<String> alCandidate = new ArrayList<String>();
				while(rs.next()){
					Map<String,List<String>> hmInner=hmOfferedName.get(rs.getString("recruitment_id"));
					if(hmInner==null)hmInner=new HashMap<String, List<String>>();
					
					Map<String,List<String>> hmInner1=hmAcceptedName.get(rs.getString("recruitment_id"));
					if(hmInner1==null)hmInner1=new HashMap<String, List<String>>();
					
					Map<String,List<String>> hmInner2=hmRejectedName.get(rs.getString("recruitment_id"));
					if(hmInner2==null)hmInner2=new HashMap<String, List<String>>();
					
					List<String> alinner=new ArrayList<String>();
					
					alinner.add(rs.getString("emp_per_id"));
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					alinner.add(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));   		 
		
					alinner.add(uF.showData(rs.getString("ctc_offered"), ""));
			    	alinner.add(uF.getDateFormat(rs.getString("candidate_joining_date"), DBDATE, CF.getStrReportDateFormat()));
			    		 
			    	
			    	if(rs.getInt("candidate_status")==1 && rs.getInt("offer_backout_status")==0) {
			    		hmInner1.put(rs.getString("emp_per_id"),alinner);
			    		hmAcceptedName.put(rs.getString("recruitment_id"), hmInner1);
			    	} else if(rs.getInt("candidate_status")==-1 || rs.getInt("offer_backout_status")==-1) {
			    		hmInner2.put(rs.getString("emp_per_id"), alinner);
			    		hmRejectedName.put(rs.getString("recruitment_id"), hmInner2);
			    	} else if(rs.getInt("candidate_status")==0) {
			    		hmInner.put(rs.getString("emp_per_id"),alinner);
			    		hmOfferedName.put(rs.getString("recruitment_id"), hmInner);
			    		
			    		alCandidate.add(rs.getString("emp_per_id"));
			    	}	
				}
				rs.close();
				pst.close();
				
				if(alCandidate!=null && alCandidate.size()>0) {
					Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
					if(hmEmpCodeDesig == null) hmEmpCodeDesig = new HashMap<String, String>();
					pst = con.prepareStatement("select * from candidate_personal_details where candididate_emp_id > 0 and emp_per_id in ("+StringUtils.join(alCandidate.toArray(),",")+")");
					rs = pst.executeQuery();
					Map<String, String> hmCandToEmp = new HashMap<String, String>();
					while(rs.next()){
						
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						
						hmCandToEmp.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")+" is selected for "+uF.showData(hmEmpCodeDesig.get(rs.getString("candididate_emp_id")), "")+" already!");
					}
					rs.close();
					pst.close();
					request.setAttribute("hmCandToEmp", hmCandToEmp);
				}
			
			 /*LOGIC  FOR MULTIPLE EXPERIENCE *******************************/
			
				String strCandidateIdOld = null;
				String strCandidateIdNew = null;
				int noyear = 0,nomonth = 0,nodays = 0;
				Map<String,String> hmCandidateExperience=new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select candidate_status,to_date,from_date,recruitment_id,candidate_id from candidate_prev_employment cpe join " +
					"candidate_application_details cad on(cad.candidate_id=cpe.emp_id) where application_status=2 and candidate_final_status=1 " +
					"and recruitment_id in ("+getCurrRecruitId()+") ");
				if(strUserType != null && strUserType.equals(RECRUITER)) {
					sbQuery.append(" and cad.added_by = "+uF.parseToInt(strSessionEmpId)+" ");
				}
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
				sbQuery.append(" order by candidate_id ");
				pst = con.prepareStatement(sbQuery.toString());
				rs=pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
				while(rs.next()) {
					
					strCandidateIdNew = rs.getString("candidate_id");
					if(strCandidateIdNew!=null && !strCandidateIdNew.equals(strCandidateIdOld)){
					
						noyear=0;
						nomonth=0;
						nodays=0;
					}
						
						String datedif=uF.dateDifference(rs.getString("from_date"),DBDATE , rs.getString("to_date"), DBDATE);
			    		
						long datediff=uF.parseToLong(datedif);		    		 
					
				    	noyear+=(int) (datediff/365);
				    	nomonth+=(int) ((datediff%365)/30);
				    	nodays+=(int) ((datediff%365)%30);
				     
				    	if(nodays>30){
				    		nomonth=nomonth+1;
				    	}
				    	if(nomonth>12){
				    		nomonth=nomonth-12;
				    		noyear=noyear+1;
				    	}
	
				    		
				    	hmCandidateExperience.put(rs.getString("candidate_id"),""+noyear+" Year "+nomonth+" months "); 			    	
				    	strCandidateIdOld = strCandidateIdNew;
	
				}
				rs.close();
				pst.close();
				
				Map<String,String> hmSkillsName = CF.getSkillNameMap(con);
				Map<String,String> hmCandidateSkill=new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select recruitment_id,emp_id,skill_id,candidate_status from candidate_skills_description csd join " +
					"candidate_application_details cad on(cad.candidate_id=csd.emp_id) where application_status=2 and candidate_final_status=1 " +
					" and recruitment_id in ("+getCurrRecruitId()+") ");
				if(strUserType != null && strUserType.equals(RECRUITER)) {
					sbQuery.append(" and cad.added_by = "+uF.parseToInt(strSessionEmpId)+" ");
				}
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
				pst=con.prepareStatement(sbQuery.toString());
				rs=pst.executeQuery(); 
	//			System.out.println("new Date ===> " + new Date());
				while(rs.next()){
	
						String temp=hmCandidateSkill.get(rs.getString("emp_id"));
					    
						if(temp!=null)
			    		 temp+=","+hmSkillsName.get(rs.getString("skill_id"));
					    else
					    	temp=hmSkillsName.get(rs.getString("skill_id"));
					   
						hmCandidateSkill.put(rs.getString("emp_id"),temp); 
				    
	
				}
				rs.close();
				pst.close();
	
				
				Map<String,String> hmCandidateEducation=new HashMap<String, String>();
				Map<String, String> hmDegreeName = CF.getDegreeNameMap(con);
				sbQuery = new StringBuilder();
				sbQuery.append("select recruitment_id,emp_id,education_id,candidate_status from candidate_education_details ced join " +
					" candidate_application_details cad on(cad.candidate_id = ced.emp_id) where application_status=2 and candidate_final_status=1 " +
					" and recruitment_id in ("+getCurrRecruitId()+") ");
				if(strUserType != null && strUserType.equals(RECRUITER)) {
					sbQuery.append(" and cad.added_by = "+uF.parseToInt(strSessionEmpId)+" ");
				}
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
				pst=con.prepareStatement(sbQuery.toString());
				rs=pst.executeQuery();	
	//			System.out.println("new Date ===> " + new Date());
				while(rs.next()){
					
					 String temp=hmCandidateEducation.get(rs.getString("emp_id"));
					 if(temp!=null)
					 temp+=", "+hmDegreeName.get(rs.getString("education_id"));
					 else
						 temp=hmDegreeName.get(rs.getString("education_id"));
		    		
					 hmCandidateEducation.put(rs.getString("emp_id"), temp);
				}
				rs.close();
				pst.close();
	
				
				Map<String, String> hmCommentsHR = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select cad.recruitment_id,cad.candidate_hr_comments,emp_per_id from candidate_personal_details cpd, " +
					" candidate_application_details cad where cpd.emp_per_id = cad.candidate_id and cad.application_status=2 and " +
					" not cad.candidate_final_status=-1 and cad.recruitment_id in ("+getCurrRecruitId()+") ");
				if(strUserType != null && strUserType.equals(RECRUITER)) {
					sbQuery.append(" and cad.added_by = "+uF.parseToInt(strSessionEmpId)+" ");
				}
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
				pst=con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				while (rs.next()) {
					hmCommentsHR.put(rs.getString("recruitment_id")+"_"+rs.getString("emp_per_id"), rs.getString("candidate_hr_comments"));
				}
				rs.close();
				pst.close();
				
				Map<String, Map<String, String>> hmPanelRatingAndComments = new HashMap<String, Map<String, String>>();
				sbQuery = new StringBuilder();
				sbQuery.append("select cip.recruitment_id,comments,panel_rating,panel_round_id,cip.candidate_id,panel_user_id from " +
					" candidate_interview_panel cip, candidate_application_details cad where cip.candidate_id = cad.candidate_id and " +
					" cip.recruitment_id in ("+getCurrRecruitId()+") ");
				if(strUserType != null && strUserType.equals(RECRUITER)) {
					sbQuery.append(" and cad.added_by = "+uF.parseToInt(strSessionEmpId)+" ");
				}
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
				pst=con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
	//			System.out.println("pst ===> "+ pst);
				Map<String, String> hmInner = new HashMap<String, String>();
				while (rs.next()) {
					hmInner = hmPanelRatingAndComments.get(rs.getString("recruitment_id")+"_"+rs.getString("candidate_id"));
					if (hmInner == null) hmInner = new HashMap<String, String>();
					
					hmInner.put(rs.getString("panel_round_id")+"_"+rs.getString("panel_user_id")+"_RATING", uF.showData(rs.getString("panel_rating"), ""));
					hmInner.put(rs.getString("panel_round_id")+"_"+rs.getString("panel_user_id")+"_COMMENT", uF.showData(rs.getString("comments"), ""));
					hmPanelRatingAndComments.put(rs.getString("recruitment_id")+"_"+rs.getString("candidate_id"), hmInner);
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmAssessmentName = CF.getAssessmentNameMap(con, uF);
				Map<String, String> hmRoundAssessment = new HashMap<String, String>();
				Map<String, Map<String, Map<String, String>>> hmRecruitWiseRoundId = new HashMap<String, Map<String, Map<String, String>>>();
				sbQuery = new StringBuilder();
				sbQuery.append("select * from panel_interview_details where recruitment_id in ("+getCurrRecruitId()+") ");
				if(getAppliSourceType() != null && !getAppliSourceType().equals("")) {
					sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where source_type="+uF.parseToInt(getAppliSourceType())+" ");
					if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
						if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
							sbQuery.append(" and added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
							sbQuery.append(" and source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
						}
					}
					sbQuery.append(") ");
				} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where candidate_id>0 ");
					sbQuery.append(" and (added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					sbQuery.append(" or source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
					sbQuery.append(") ");
				}
				pst=con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				while(rs.next()) {
					Map<String,Map<String,String>> hmRoundId = hmRecruitWiseRoundId.get(rs.getString("recruitment_id"));  
					if(hmRoundId==null) hmRoundId =new HashMap<String, Map<String,String>>();
					if(rs.getString("panel_emp_id") != null && !rs.getString("panel_emp_id").equals("")) {
						Map<String,String> hmEmpId = hmRoundId.get(rs.getString("round_id"));
						if(hmEmpId==null) hmEmpId =new HashMap<String, String>();
						
						hmEmpId.put(rs.getString("panel_emp_id"), rs.getString("panel_emp_id"));
						
						hmRoundId.put(rs.getString("round_id"), hmEmpId);
					}
					hmRecruitWiseRoundId.put(rs.getString("recruitment_id"), hmRoundId);
					
					if(uF.parseToInt(rs.getString("assessment_id")) > 0) {
						hmRoundAssessment.put(rs.getString("recruitment_id")+"_"+rs.getString("round_id")+"_NAME", hmAssessmentName.get(rs.getString("assessment_id")));
						hmRoundAssessment.put(rs.getString("recruitment_id")+"_"+rs.getString("round_id")+"_ID", rs.getString("assessment_id"));
					}
				}
				rs.close();
				pst.close();
				
	//			System.out.println("hmPanelRatingAndComments ===> " + hmPanelRatingAndComments);
	//			System.out.println("hmRecruitWiseRoundId ===> " + hmRecruitWiseRoundId);
				request.setAttribute("hmRoundAssessment", hmRoundAssessment);
				
				request.setAttribute("hmPanelRatingAndComments", hmPanelRatingAndComments);
				request.setAttribute("hmRecruitWiseRoundId", hmRecruitWiseRoundId);
				request.setAttribute("hmCommentsHR", hmCommentsHR);
	
				request.setAttribute("hmAcceptedName", hmAcceptedName);
				request.setAttribute("hmOfferedName", hmOfferedName);
				request.setAttribute("hmRejectedName", hmRejectedName);
	
				
	//			request.setAttribute("hmCandidateRating",hmCandidateRating);
				request.setAttribute("hmCandidateEducation",hmCandidateEducation);
				request.setAttribute("hmCandidateSkill",hmCandidateSkill);
				request.setAttribute("hmCandidateExperience",hmCandidateExperience);
			}
			
			//===start parvez date: 20-10-2021===
			List<String> onBoardedEmpList = new ArrayList<String>();
			pst=con.prepareStatement("select emp_per_id from candidate_personal_details where emp_email in (select emp_email from employee_personal_details where is_alive='true') " +
					"or emp_email in (select emp_email_sec from employee_personal_details where is_alive='true')");
			rs = pst.executeQuery();
			while(rs.next()) {
				onBoardedEmpList.add(rs.getString("emp_per_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("onBoardedEmpList",onBoardedEmpList);
			//===end parvez date: 20-10-2021===
			
			
	     } catch(Exception e) {
	    	 e.printStackTrace();
	     } finally {
				db.closeResultSet(rs);
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
	
//	private String panelName(Connection con, String panelid){
//		String name=null;
//		Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
//		if(panelid != null){
//			name=hmEmpName.get(panelid.trim());
//		}
//		return name;
//	}

	
	HttpServletRequest request;
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

	String strDashboardRequest;
	public String getStrDashboardRequest() {
		return strDashboardRequest;
	}

	public void setStrDashboardRequest(String strDashboardRequest) {
		this.strDashboardRequest = strDashboardRequest.trim();
	}

	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
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


	
}