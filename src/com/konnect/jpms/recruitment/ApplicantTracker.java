package com.konnect.jpms.recruitment;

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

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApplicantTracker extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId = null;
	String strUserType = null;
	CommonFunctions CF = null;
 
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
	
	String proPage;
	String minLimit;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
 
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
//		boolean isView = CF.getAccess(session, request, uF); 
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied); 
//			return ACCESS_DENIED; 
//		}
		
//		System.out.println("in AT");
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
//		if(strUserType != null && strUserType.equals(HRMANAGER) && getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(CANDIDATE_FINALIZATION_ALERT)){
//			updateUserAlerts();
//		}
		
		request.setAttribute(PAGE, "/jsp/recruitment/ApplicantTracker.jsp");
		request.setAttribute(TITLE, "Applicant Tracker");
		
		organisationList = new FillOrganisation(request).fillOrganisation();
		workLocationList = new FillWLocation(request).fillWLocation(getF_org());
		desigList = new FillDesig(request).fillDesig(uF.parseToInt(getF_org()));
		
		if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")) {
			setDataType("L");
		}
		
		if(uF.parseToInt(getProPage()) == 0) {
			setProPage("1");
		}
		
		System.out.println("getDataType() ===>> " + getDataType());
		
		getSelectedFilter(uF);
		getSearchAutoCompleteData(uF);
		getLiveRecruitmentIds(uF, getRecruitId());
		prepareAllData(uF, getRecruitId());
		
		getCandidateStageStatus(uF, getRecruitId());

		
		return SUCCESS;
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
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(RECRUITER))){
//				sbQuery.append(" and added_by = "+ uF.parseToInt(strSessionEmpId) +" ");
				sbQuery.append(" and (added_by = "+ uF.parseToInt(strSessionEmpId) +" or hiring_manager like '%,"+strSessionEmpId+",%' )");
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
	
//	private void updateUserAlerts() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
//		try {
//			con = db.makeConnection(con);
//			
//			String strDomain = request.getServerName().split("\\.")[0];
//			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//			userAlerts.setStrDomain(strDomain);
//			userAlerts.setStrEmpId(""+nEmpId);
//			userAlerts.set_type(CANDIDATE_FINALIZATION_ALERT);
//			userAlerts.setStatus(UPDATE_ALERT);
//			//Thread t = new Thread(userAlerts);
//			//t.run();
////			System.out.println("in Appraisal UserAlerts ...");
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	
	
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


private Map<String, String> getEducationWeightage(Connection con, UtilityFunctions uF){
	
	PreparedStatement pst = null;
	ResultSet rst = null;
	
	Map<String, String> hmEduWeightage = new HashMap<String, String>();
	try {
		pst=con.prepareStatement("select education_name,weightage from educational_details");
		rst=pst.executeQuery();
//		System.out.println("new Date ===> "+ new Date());
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
//			System.out.println("desigSkillList ===> "+desigSkillList);
//			System.out.println("desigEduList ===> "+ desigEduList);
//			System.out.println("desigTotExp ===> "+ desigTotExp);
//			System.out.println("desigGender ===> "+ desigGender);
			
			
//			System.out.println("recEduList ===> "+recEduList);
//			System.out.println("recSkillsList ===> "+ recSkillsList);
//			System.out.println("minExp ===> "+ minExp);
			
//			System.out.println("educationsList ===> "+educationsList);
//			System.out.println("skillsList ===> "+ skillsList);
//			System.out.println("totExpList ===> "+ totExpList);
			
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

private Map<String, String> getCandidateList(Connection con, UtilityFunctions uF, String recruitID) {
	
	PreparedStatement pst = null;
	ResultSet rst = null;
	Map<String, String> hmCandiStars = new HashMap<String, String>();
	try {
		Map<String, List<String>> hmSkills = getCandiSkillsName(con);
		Map<String, List<String>> hmDegrees = getCandiDegreeName(con);
		Map<String, List<String>> hmTotExp = getCandiTotExp(con, uF);
		
		pst=con.prepareStatement("select emp_per_id,emp_fname,emp_lname,cad.recruitment_id,emp_gender from candidate_personal_details cpd, " +
				"candidate_application_details cad where cpd.emp_per_id = cad.candidate_id and cad.recruitment_id = ? order by emp_fname,emp_lname");
		pst.setInt(1, uF.parseToInt(recruitID));
		rst=pst.executeQuery();
//		System.out.println("new Date ===> "+ new Date());
		while(rst.next()){
			String candiID = rst.getString("emp_per_id");
			String candiGender = rst.getString("emp_gender");
			String strStars = calculateCandidateStarRating(con, uF, hmSkills.get(candiID), hmDegrees.get(candiID), hmTotExp.get(candiID), candiGender, recruitID);
//			System.out.println("strStars final ===> "+strStars);
			hmCandiStars.put(candiID, strStars);
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
	return hmCandiStars;
}
	
	
	private void getLiveRecruitmentIds(UtilityFunctions uF, String recruitId) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		try {
			con = db.makeConnection(con);
			List<String> recruitmentIdList = new ArrayList<String>();
			Map<String, String> hmRecruitmentData = new HashMap<String, String>();
			
			Map<String, Map<String, String>> hmcandiStarRecruitwise = new HashMap<String, Map<String,String>>();
			
			List<String> alSkillId = null;
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){	
				pst = con.prepareStatement("select skill_id from skills_details where skill_name is not null " +
						"and skill_name != '' and upper(skill_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'");
				rst = pst.executeQuery();
				while(rst.next()){
					if(alSkillId == null){
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
			sbQuery.append("select job_code,priority_job_int,recruitment_id,job_title,custum_designation,close_job_status from recruitment_details " +
				"where job_approval_status=1 ");
			if (recruitId != null && !recruitId.equals("") && uF.parseToInt(recruitId) > 0){
				sbQuery.append(" and recruitment_id = " + recruitId + " ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rst = pst.executeQuery();
//			System.out.println("pst===> "+ pst); 
			StringBuilder sbRecruitId = null;
			while (rst.next()) {
				if(sbRecruitId == null){
					sbRecruitId = new StringBuilder();
					sbRecruitId.append(rst.getString("recruitment_id"));
				} else {
					sbRecruitId.append(","+rst.getString("recruitment_id"));
				}
				
				recruitmentIdList.add(rst.getString("recruitment_id"));
				hmRecruitmentData.put(rst.getString("recruitment_id")+"_JOB_CODE", rst.getString("job_code"));
				hmRecruitmentData.put(rst.getString("recruitment_id")+"_PRIORITY", rst.getString("priority_job_int"));
				hmRecruitmentData.put(rst.getString("recruitment_id")+"_JOB_TITLE", rst.getString("job_title"));
				hmRecruitmentData.put(rst.getString("recruitment_id")+"_JOB_STATUS", uF.parseToBoolean(rst.getString("close_job_status"))+"");
				
				Map<String, String> hmCandiStars = getCandidateList(con, uF, rst.getString("recruitment_id"));
				hmcandiStarRecruitwise.put(rst.getString("recruitment_id"), hmCandiStars);
			}
			rst.close();
			pst.close();
			
			if(sbRecruitId != null) {
				getSourceDetails(con,uF,sbRecruitId.toString());
			}
			
			request.setAttribute("hmRecruitmentData", hmRecruitmentData);
			request.setAttribute("hmcandiStarRecruitwise", hmcandiStarRecruitwise);
			request.setAttribute("recruitmentIdList", recruitmentIdList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private String getCandidateStageStatus(UtilityFunctions uF, String recruitId) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getCandNameMap(con, null, null);
			Map<String, String> hmCandiRejectFromRound = new HashMap<String, String>();
			pst = con.prepareStatement("Select * from candidate_interview_panel where status=-1");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				hmCandiRejectFromRound.put(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"), rst.getString("status"));
			}
			rst.close();
			pst.close();
			
			Map<String, String> hmClearRoundCnt = new HashMap<String, String>();
			pst = con.prepareStatement("select count (distinct(panel_round_id)) as count,recruitment_id,candidate_id from candidate_interview_panel where status = 1 group by recruitment_id,candidate_id");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				hmClearRoundCnt.put(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"), rst.getString("count"));
			}
			rst.close(); 
			pst.close();
			
//			System.out.println("hmClearRoundCnt ===> "+hmClearRoundCnt);
			
			Map<String, String> hmRoundCnt = new HashMap<String, String>();
			pst = con.prepareStatement("select count (distinct(round_id))as count,recruitment_id from panel_interview_details group by recruitment_id");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				hmRoundCnt.put(rst.getString("recruitment_id"), rst.getString("count"));
			}
			rst.close();
			pst.close();
			
//			System.out.println("hmRoundCnt ===> "+hmRoundCnt);
			
			
			Map<String, String> hmRoundName = new HashMap<String, String>();
			pst = con.prepareStatement("select * from panel_interview_details where round_name is not null");
			rst = pst.executeQuery();
		//	System.out.println("new Date ===> "+ new Date());
			while (rst.next()) {
				hmRoundName.put(rst.getString("round_id"), rst.getString("round_name"));
			}
			rst.close();
			pst.close();
	
	
			Map<String, Map<String, String>> hmRecruitAndCandiwiseCurrentRoundDetails = new HashMap<String, Map<String, String>>();
			Map<String, String> hmRecrAndCandiwiseCurrentRoundDetails = new HashMap<String, String>(); 
			pst = con.prepareStatement("select * from candidate_interview_panel order by recruitment_id, candidate_id, panel_round_id, status desc");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
//			Map<String, List<String>> hmCompletedRoundRecrAndCandiwise = new HashMap<String, List<String>>();
//			List<String> alCompletedRound = new ArrayList<String>();
			Map<String, String> hmCompRoundIdRecAndCandiwise = new HashMap<String, String>();
			Map<String, List<List<String>>> hmCandiwiseAllRoundStatusData = new HashMap<String, List<List<String>>>();
			Map<String, String> hmRoundIdRecAndCandiwise = new HashMap<String, String>();
			while (rst.next()) {
				List<List<String>> alRoundStatus = hmCandiwiseAllRoundStatusData.get(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"));
				if(alRoundStatus==null) alRoundStatus = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(hmRoundName.get(rst.getString("panel_round_id")), "Round "+rst.getString("panel_round_id")));
				innerList.add(uF.getDateFormat(rst.getString("interview_date"), DBDATE, DATE_FORMAT_STR));
				innerList.add(hmEmpName.get(rst.getString("panel_user_id")));
				innerList.add(rst.getString("comments"));
				innerList.add(rst.getString("status"));
				innerList.add(rst.getString("panel_rating"));
				alRoundStatus.add(innerList);
				
				hmRecrAndCandiwiseCurrentRoundDetails = hmRecruitAndCandiwiseCurrentRoundDetails.get(rst.getString("recruitment_id"));
				if(hmRecrAndCandiwiseCurrentRoundDetails == null)hmRecrAndCandiwiseCurrentRoundDetails = new HashMap<String, String>();
				
				if(rst.getInt("status") == 1) { // && !alCompletedRound.contains(rst.getString("panel_round_id"))
//					alCompletedRound.add(rst.getString("panel_round_id"));
					hmCompRoundIdRecAndCandiwise.put(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"), rst.getString("panel_round_id"));
				}
				hmRoundIdRecAndCandiwise.put(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"), rst.getString("panel_round_id"));
				
				if(rst.getInt("status") == 0 && (uF.parseToInt((hmCompRoundIdRecAndCandiwise != null && !hmCompRoundIdRecAndCandiwise.isEmpty()) ? hmCompRoundIdRecAndCandiwise.get(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id")) : "0")+1) == uF.parseToInt(rst.getString("panel_round_id"))) {
					hmRecrAndCandiwiseCurrentRoundDetails.put(rst.getString("candidate_id")+"_NEXT_ROUND_DATA", uF.showData(hmRoundName.get(rst.getString("panel_round_id")), "Round "+rst.getString("panel_round_id"))+" on "+
						uF.getDateFormat(rst.getString("interview_date"), DBDATE, DATE_FORMAT_STR) );
				}
				
				hmRecruitAndCandiwiseCurrentRoundDetails.put(rst.getString("recruitment_id"), hmRecrAndCandiwiseCurrentRoundDetails);
//				hmCompletedRoundRecrAndCandiwise.put(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"), alCompletedRound);
			}
			rst.close();
			pst.close();
			
//			System.out.println("hmRoundIdsRecruitwise ===> "+hmRoundIdsRecruitwise);
			request.setAttribute("hmRecruitAndCandiwiseCurrentRoundDetails", hmRecruitAndCandiwiseCurrentRoundDetails);
			
			
			Map<String, Map<String, String>> hmRecruitAndCandiwiseStatusData = new HashMap<String, Map<String, String>>();
//			Map<String, String> hmRecrAndCandiwiseStatusData = new HashMap<String, String>(); 
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from candidate_application_details where recruitment_id>0 ");
			if(uF.parseToInt(recruitId)>0) {
				sbQuery.append(" and recruitment_id= "+ uF.parseToInt(recruitId) + " ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rst = pst.executeQuery();
//			System.out.println("pst ===> "+ pst);
			while (rst.next()) {
            	Map<String, String> hmRecrAndCandiwiseStatusData = hmRecruitAndCandiwiseStatusData.get(rst.getString("recruitment_id"));
				if(hmRecrAndCandiwiseStatusData == null)hmRecrAndCandiwiseStatusData = new HashMap<String, String>();
				
				if(rst.getString("application_date") != null && !rst.getString("application_date").equals("")) {
					hmRecrAndCandiwiseStatusData.put(rst.getString("candidate_id")+"_APPLY_DATE", uF.getDateFormat(rst.getString("application_date"), DBDATE, DATE_FORMAT_STR));
				}	
				if(rst.getString("application_status_date") != null && !rst.getString("application_status_date").equals("")) {
					hmRecrAndCandiwiseStatusData.put(rst.getString("candidate_id")+"_APPLICATION_S_R_DATE", uF.getDateFormat(rst.getString("application_status_date"), DBDATE, DATE_FORMAT_STR));
				}
				if(rst.getString("candidate_final_status_date") != null && !rst.getString("candidate_final_status_date").equals("")) {
					hmRecrAndCandiwiseStatusData.put(rst.getString("candidate_id")+"_FINALIZE_DATE", uF.getDateFormat(rst.getString("candidate_final_status_date"), DBDATE, DATE_FORMAT_STR));
					hmRecrAndCandiwiseStatusData.put(rst.getString("candidate_id")+"_OFFERED_AMOUNT", rst.getString("ctc_offered"));
					hmRecrAndCandiwiseStatusData.put(rst.getString("candidate_id")+"_HR_COMMENT", uF.showData(rst.getString("candidate_hr_comments"), ""));
					hmRecrAndCandiwiseStatusData.put(rst.getString("candidate_id")+"_JOINING_DATE", uF.getDateFormat(rst.getString("candidate_joining_date"), DBDATE, DATE_FORMAT_STR));
				}
				if(rst.getString("offer_accept_date") != null && !rst.getString("offer_accept_date").equals("")) {
					hmRecrAndCandiwiseStatusData.put(rst.getString("candidate_id")+"_OFFER_A_R_DATE", uF.getDateFormat(rst.getString("offer_accept_date"), DBDATE, DATE_FORMAT_STR));
					hmRecrAndCandiwiseStatusData.put(rst.getString("candidate_id")+"_OFFER_A_R_COMMENT", uF.showData(rst.getString("offer_accept_remark"), ""));
				}
				if(rst.getString("offer_backout_date") != null && !rst.getString("offer_backout_date").equals("") && rst.getInt("offer_backout_status")== -1) {
					hmRecrAndCandiwiseStatusData.put(rst.getString("candidate_id")+"_OFFER_BACKOUT_DATE", uF.getDateFormat(rst.getString("offer_backout_date"), DBDATE, DATE_FORMAT_STR));
					hmRecrAndCandiwiseStatusData.put(rst.getString("candidate_id")+"_OFFER_BACKOUT_COMMENT", uF.showData(rst.getString("offer_backout_remark"), ""));
				}
				if(rst.getString("offer_backout_date") != null && !rst.getString("offer_backout_date").equals("") && rst.getInt("offer_backout_status")== -2) {
					hmRecrAndCandiwiseStatusData.put(rst.getString("candidate_id")+"_OFFER_ONHOLD_DATE", uF.getDateFormat(rst.getString("offer_backout_date"), DBDATE, DATE_FORMAT_STR));
					hmRecrAndCandiwiseStatusData.put(rst.getString("candidate_id")+"_OFFER_ONHOLD_COMMENT", uF.showData(rst.getString("offer_backout_remark"), ""));
				}
				
				hmRecruitAndCandiwiseStatusData.put(rst.getString("recruitment_id"), hmRecrAndCandiwiseStatusData);
			}
			rst.close();
			pst.close();
			
//			System.out.println("hmRecruitAndCandiwiseStatusData ===> "+hmRecruitAndCandiwiseStatusData);
			request.setAttribute("hmRecruitAndCandiwiseStatusData", hmRecruitAndCandiwiseStatusData);
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select  cad.candidate_id,cad.recruitment_id, cad.application_status, cad.candidate_final_status,cad.candidate_status," +
				"candididate_emp_id,cad.offer_backout_status from candidate_application_details cad, recruitment_details rd where cad.recruitment_id = rd.recruitment_id ");
			/*if(getDataType() == null || getDataType().equals("") || getDataType().equalsIgnoreCase("null") || getDataType().equals("L")) {
				sbQuery.append(" and rd.close_job_status = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and rd.close_job_status = true ");
			}*/
			if(uF.parseToInt(recruitId)>0) {
				sbQuery.append(" and cad.recruitment_id= "+ uF.parseToInt(recruitId) + " ");
			}
			
			pst = con.prepareStatement(sbQuery.toString());
			System.out.println("pst====>"+pst);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			Map<String, List<String>> hmRecruitwiseCandiStageStatus = new HashMap<String, List<String>>();
			List<String> alCandiApplicationIds = new ArrayList<String>();
			List<String> alCandiAppShortlistIds = new ArrayList<String>();
			List<String> alCandiAppRejectIds = new ArrayList<String>();
			List<String> alCandiUnderInterviewIds = new ArrayList<String>();
			List<String> alCandiRejectInterviewIds = new ArrayList<String>();
			List<String> alCandiOfferOnHoldIds = new ArrayList<String>();
			List<String> alCandiFinalInterviewIds = new ArrayList<String>();
			List<String> alCandiOfferedIds = new ArrayList<String>();
			List<String> alCandiOfferAcceptIds = new ArrayList<String>();
			List<String> alCandiOfferRejectIds = new ArrayList<String>();
			List<String> alCandiOfferBackoutIds = new ArrayList<String>();
			List<String> alCandiOnboardPendingIds = new ArrayList<String>();
			List<String> alCandiOnboardIds = new ArrayList<String>();
			while (rst.next()) {
				alCandiApplicationIds = hmRecruitwiseCandiStageStatus.get(rst.getString("recruitment_id")+"_C_APPLICATIONS");
				if(alCandiApplicationIds == null) alCandiApplicationIds = new ArrayList<String>();
				
				alCandiAppShortlistIds = hmRecruitwiseCandiStageStatus.get(rst.getString("recruitment_id")+"_C_APP_SHORTLIST");
				if(alCandiAppShortlistIds == null) alCandiAppShortlistIds = new ArrayList<String>();
				
				alCandiAppRejectIds = hmRecruitwiseCandiStageStatus.get(rst.getString("recruitment_id")+"_C_APP_REJECT");
				if(alCandiAppRejectIds == null) alCandiAppRejectIds = new ArrayList<String>();
				
				alCandiUnderInterviewIds = hmRecruitwiseCandiStageStatus.get(rst.getString("recruitment_id")+"_C_UNDER_INTERVIEW");
				if(alCandiUnderInterviewIds == null) alCandiUnderInterviewIds = new ArrayList<String>();
				
				alCandiRejectInterviewIds = hmRecruitwiseCandiStageStatus.get(rst.getString("recruitment_id")+"_C_REJECT_INTERVIEW");
				if(alCandiRejectInterviewIds == null) alCandiRejectInterviewIds = new ArrayList<String>();
				
				alCandiOfferOnHoldIds = hmRecruitwiseCandiStageStatus.get(rst.getString("recruitment_id")+"_C_OFFER_ONHOLD");
				if(alCandiOfferOnHoldIds == null) alCandiOfferOnHoldIds = new ArrayList<String>();
				
				alCandiFinalInterviewIds = hmRecruitwiseCandiStageStatus.get(rst.getString("recruitment_id")+"_C_FINAL_INTERVIEW");
				if(alCandiFinalInterviewIds == null) alCandiFinalInterviewIds = new ArrayList<String>();
				
				alCandiOfferedIds = hmRecruitwiseCandiStageStatus.get(rst.getString("recruitment_id")+"_C_OFFERED");
				if(alCandiOfferedIds == null) alCandiOfferedIds = new ArrayList<String>();
				
				alCandiOfferAcceptIds = hmRecruitwiseCandiStageStatus.get(rst.getString("recruitment_id")+"_C_OFFER_ACCEPT");
				if(alCandiOfferAcceptIds == null) alCandiOfferAcceptIds = new ArrayList<String>();
				
				alCandiOfferRejectIds = hmRecruitwiseCandiStageStatus.get(rst.getString("recruitment_id")+"_C_OFFER_REJECT");
				if(alCandiOfferRejectIds == null) alCandiOfferRejectIds = new ArrayList<String>();
				
				alCandiOfferBackoutIds = hmRecruitwiseCandiStageStatus.get(rst.getString("recruitment_id")+"_C_OFFER_BACKOUT");
				if(alCandiOfferBackoutIds == null) alCandiOfferBackoutIds = new ArrayList<String>();
				
				alCandiOnboardPendingIds = hmRecruitwiseCandiStageStatus.get(rst.getString("recruitment_id")+"_C_ONBOARD_PENDING");
				if(alCandiOnboardPendingIds == null) alCandiOnboardPendingIds = new ArrayList<String>();
				
				alCandiOnboardIds = hmRecruitwiseCandiStageStatus.get(rst.getString("recruitment_id")+"_C_ONBOARD");
				if(alCandiOnboardIds == null) alCandiOnboardIds = new ArrayList<String>();
				
				alCandiApplicationIds.add(rst.getString("candidate_id")); // Applications
				
//				uF.parseToInt(rst.getString("application_status")) == 1 && uF.parseToInt(rst.getString("candidate_final_status")) == 0  && uF.parseToInt(rst.getString("candidate_status")) == 0
				if (uF.parseToInt(rst.getString("application_status")) == 2) {
					alCandiAppShortlistIds.add(rst.getString("candidate_id")); // Application Shortlist
				}

				if (uF.parseToInt(rst.getString("application_status")) == -1) {
					alCandiAppRejectIds.add(rst.getString("candidate_id")); // Application Rejected
				}
				
				if (uF.parseToInt(rst.getString("candidate_final_status")) == 0 && uF.parseToInt(hmClearRoundCnt.get(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"))) > 0 && uF.parseToInt(hmClearRoundCnt.get(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"))) < uF.parseToInt(hmRoundCnt.get(rst.getString("recruitment_id")))) {
					alCandiUnderInterviewIds.add(rst.getString("candidate_id")); // Candidate Under Interview
				} else if(uF.parseToInt(rst.getString("candidate_final_status")) == 0 && uF.parseToInt(hmClearRoundCnt.get(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"))) < uF.parseToInt(hmRoundCnt.get(rst.getString("recruitment_id"))) && hmRoundIdRecAndCandiwise != null 
					&& hmRoundIdRecAndCandiwise.get(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id")) != null && uF.parseToInt(hmRoundCnt.get(rst.getString("recruitment_id"))) > 0) {
					alCandiUnderInterviewIds.add(rst.getString("candidate_id")); // Candidate Under Interview
				}
				
				if (uF.parseToInt(rst.getString("candidate_final_status")) == -1 || uF.parseToInt(hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"))) == -1) {
					alCandiRejectInterviewIds.add(rst.getString("candidate_id")); // Candidate Rejected Interview
				}
				
				if (uF.parseToInt(rst.getString("offer_backout_status")) == -2) {
					alCandiOfferOnHoldIds.add(rst.getString("candidate_id")); // Candidate ON Hold
				}
				
				if (uF.parseToInt(rst.getString("candidate_final_status")) == 1 || (uF.parseToInt(hmClearRoundCnt.get(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"))) == uF.parseToInt(hmRoundCnt.get(rst.getString("recruitment_id"))) && uF.parseToInt(hmRoundCnt.get(rst.getString("recruitment_id"))) > 0) ) {
					alCandiFinalInterviewIds.add(rst.getString("candidate_id")); // Candidate Finalize Interview
				}
				
				if (uF.parseToInt(rst.getString("candidate_final_status")) == 1 && uF.parseToInt(rst.getString("candidate_status")) == 0) {
					alCandiOfferedIds.add(rst.getString("candidate_id")); // Candidate Offered
				}
				
				if (uF.parseToInt(rst.getString("candidate_status")) == 1) {
					alCandiOfferAcceptIds.add(rst.getString("candidate_id")); // Candidate Offer Accept
				}
				
				if (uF.parseToInt(rst.getString("candidate_status")) == -1) {
					alCandiOfferRejectIds.add(rst.getString("candidate_id")); // Candidate Offer Reject
				}
				
				if (uF.parseToInt(rst.getString("offer_backout_status")) == -1) {
					alCandiOfferBackoutIds.add(rst.getString("candidate_id")); // Candidate Offer Backout
				}
				
				if (uF.parseToInt(rst.getString("candidate_status")) == 1 && uF.parseToInt(rst.getString("candididate_emp_id")) == 0) {
					alCandiOnboardPendingIds.add(rst.getString("candidate_id")); // Candidate Onboard Pending
				}
				
				if (uF.parseToInt(rst.getString("candidate_status")) == 1 && uF.parseToInt(rst.getString("candididate_emp_id")) > 0) {
					alCandiOnboardIds.add(rst.getString("candidate_id")); // Candidate Onboarding
				}
				
				hmRecruitwiseCandiStageStatus.put(rst.getString("recruitment_id")+"_C_APPLICATIONS", alCandiApplicationIds);
				hmRecruitwiseCandiStageStatus.put(rst.getString("recruitment_id")+"_C_APP_SHORTLIST", alCandiAppShortlistIds);
				hmRecruitwiseCandiStageStatus.put(rst.getString("recruitment_id")+"_C_APP_REJECT", alCandiAppRejectIds);
				hmRecruitwiseCandiStageStatus.put(rst.getString("recruitment_id")+"_C_UNDER_INTERVIEW", alCandiUnderInterviewIds);
				hmRecruitwiseCandiStageStatus.put(rst.getString("recruitment_id")+"_C_REJECT_INTERVIEW", alCandiRejectInterviewIds);
				hmRecruitwiseCandiStageStatus.put(rst.getString("recruitment_id")+"_C_OFFER_ONHOLD", alCandiOfferOnHoldIds);
				hmRecruitwiseCandiStageStatus.put(rst.getString("recruitment_id")+"_C_FINAL_INTERVIEW", alCandiFinalInterviewIds);
				hmRecruitwiseCandiStageStatus.put(rst.getString("recruitment_id")+"_C_OFFERED", alCandiOfferedIds);
				hmRecruitwiseCandiStageStatus.put(rst.getString("recruitment_id")+"_C_OFFER_ACCEPT", alCandiOfferAcceptIds);
				hmRecruitwiseCandiStageStatus.put(rst.getString("recruitment_id")+"_C_OFFER_REJECT", alCandiOfferRejectIds);
				hmRecruitwiseCandiStageStatus.put(rst.getString("recruitment_id")+"_C_OFFER_BACKOUT", alCandiOfferBackoutIds);
				hmRecruitwiseCandiStageStatus.put(rst.getString("recruitment_id")+"_C_ONBOARD_PENDING", alCandiOnboardPendingIds);
				hmRecruitwiseCandiStageStatus.put(rst.getString("recruitment_id")+"_C_ONBOARD", alCandiOnboardIds);
			}
			rst.close();
			pst.close();
			
			
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
			
			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Applicant Tracker ".toUpperCase(), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Sr. No".toUpperCase(), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Applicant Name".toUpperCase(), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Application Date".toUpperCase(), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Shortlist/Reject Date".toUpperCase(), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Status".toUpperCase(), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Interview Date".toUpperCase(), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Status with Remarks".toUpperCase(), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Offer Hold Date".toUpperCase(), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Status with Remarks".toUpperCase(), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Offer Date".toUpperCase(), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Status with Remarks".toUpperCase(), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Offer Accept/Reject Date".toUpperCase(), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Status with Remarks".toUpperCase(), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Offer Backout Date".toUpperCase(), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Status with Remarks".toUpperCase(), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Onboarded Date".toUpperCase(), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Status with Remarks".toUpperCase(), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Stage".toUpperCase(), Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			reportListExport.add(alInnerExport);
			
			Map<String, Map<String, Map<String, String>>> hmRecruitwiseCandiData = (Map<String, Map<String, Map<String, String>>>)request.getAttribute("hmRecruitwiseCandiData");
			
			Map<String, Map<String, String>> hmCandiwiseData = hmRecruitwiseCandiData.get(recruitId);
            //System.out.println("recruitId ===>> "+ recruitId +" -- hmCandiwiseData ===>> " + hmCandiwiseData);
            if(hmCandiwiseData != null && !hmCandiwiseData.isEmpty()) {
            	Iterator<String> it = hmCandiwiseData.keySet().iterator();
            	Map<String, String> hmRecrAndCandiwiseStatusData = hmRecruitAndCandiwiseStatusData.get(recruitId);
            	if(hmRecrAndCandiwiseStatusData ==null) hmRecrAndCandiwiseStatusData = new HashMap<String, String>();
            	int nCount = 0;
            	while(it.hasNext()) {
            		String candidateId = it.next();
            		nCount++;
//            		String strStars = hmCandiStars.get(candidateId);
//            		Map<String, String> hmSourceDetails = hmCandidate.get(candidateId);
//                  if(hmSourceDetails == null) hmSourceDetails = new HashMap<String, String>();
                    
//            		String txtApply = "Applied at "+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_APPLY_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_APPLY_DATE") : "");
                    String dtApply = (hmRecrAndCandiwiseStatusData.get(candidateId+"_APPLY_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_APPLY_DATE") : "");
            		String dtShortlist = "";
            		String txtShortlist = "";
            		String dtInterview = "";
            		String txtInterview = "";
            		String dtOfferOnHold = "";
            		String txtOfferOnHold = "";
            		String dtOffer = "";
            		String txtOffer = "";
            		String dtOfferAcceptReject = "";
            		String txtOfferAcceptReject = "";
            		String dtOfferBackout = "";
            		String txtOfferBackout = "";
            		String dtOnboard = "";
            		String txtOnboard = "";
            		
            		Map<String, String> hmCandiInner = hmCandiwiseData.get(candidateId);
            
            		if(alCandiAppShortlistIds != null && alCandiAppShortlistIds.contains(candidateId)) {
//            			clsShortlist = "status_accept"; //"Shortlisted at "+ 
            			dtShortlist = (hmRecrAndCandiwiseStatusData.get(candidateId+"_APPLICATION_S_R_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_APPLICATION_S_R_DATE") : "");
            			txtShortlist = "Shortlisted";
            		}
            		if(alCandiAppRejectIds != null && alCandiAppRejectIds.contains(candidateId)) {
//            			clsShortlist = "status_reject";
            			dtShortlist = (hmRecrAndCandiwiseStatusData.get(candidateId+"_APPLICATION_S_R_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_APPLICATION_S_R_DATE") : "");
            			txtShortlist = "Rejected";
            		}
            		
            		if(alCandiUnderInterviewIds != null && alCandiUnderInterviewIds.contains(candidateId)) {
//            			clsInterview = "status_pendding";
            			dtInterview = uF.showData(hmRecrAndCandiwiseCurrentRoundDetails.get(candidateId+"_NEXT_ROUND_DATA"), "");
            			txtInterview = "Next Round: " + uF.showData(hmRecrAndCandiwiseCurrentRoundDetails.get(candidateId+"_NEXT_ROUND_DATA"), "");
            		}
            		
            		if(alCandiOfferOnHoldIds != null && alCandiOfferOnHoldIds.contains(candidateId)) {
//            			clsInterview = "status_pendding";
            			dtInterview = (hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_ONHOLD_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_ONHOLD_DATE") : "");
            			txtInterview = "On Hold  "+(hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_ONHOLD_COMMENT") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_ONHOLD_COMMENT") : "-");
            		}
            		
            		if((alCandiOfferOnHoldIds == null || !alCandiOfferOnHoldIds.contains(candidateId)) && alCandiFinalInterviewIds != null && alCandiFinalInterviewIds.contains(candidateId)) {
//            			clsInterview = "status_accept";
            			dtOffer = (hmRecrAndCandiwiseStatusData.get(candidateId+"_FINALIZE_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_FINALIZE_DATE") : "");
            			txtOffer = "Offered  CTC: "+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFERED_AMOUNT") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFERED_AMOUNT") : "0")
            			+""+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_HR_COMMENT") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_HR_COMMENT") : "-"); 
            		}
            		if(alCandiRejectInterviewIds != null && alCandiRejectInterviewIds.contains(candidateId)) {
//            			clsInterview = "status_reject";
            			dtOffer = (hmRecrAndCandiwiseStatusData.get(candidateId+"_FINALIZE_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_FINALIZE_DATE") : "");
            			txtOffer = "Rejected "+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_HR_COMMENT") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_HR_COMMENT") : "-");
            		}
            		
            		if(alCandiOfferedIds != null && alCandiOfferedIds.contains(candidateId)) {
//            			clsOffer = "status_pendding";
            			dtOffer = (hmRecrAndCandiwiseStatusData.get(candidateId+"_FINALIZE_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_FINALIZE_DATE") : "");
            			txtOffer = "Offered  CTC: "+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFERED_AMOUNT") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFERED_AMOUNT") : "0")
            			+""+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_HR_COMMENT") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_HR_COMMENT") : "-");
            		}
            		if(alCandiOfferAcceptIds != null && alCandiOfferAcceptIds.contains(candidateId)) {
//            			clsOffer = "status_accept";
            			dtOfferAcceptReject = (hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_A_R_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_A_R_DATE") : "");
            			txtOfferAcceptReject = "Accepted  CTC: "+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFERED_AMOUNT") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFERED_AMOUNT") : "0")
            			+""+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_A_R_COMMENT") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_A_R_COMMENT") : "-");
            		}
            		if(alCandiOfferRejectIds != null && alCandiOfferRejectIds.contains(candidateId)) {
//            			clsOffer = "status_reject";
            			dtOfferAcceptReject = (hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_A_R_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_A_R_DATE") : "");
            			txtOfferAcceptReject = "Rejected  CTC: "+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFERED_AMOUNT") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFERED_AMOUNT") : "0")
            			+""+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_A_R_COMMENT") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_A_R_COMMENT") : "-");
            		}

            		if(alCandiOfferBackoutIds != null && alCandiOfferBackoutIds.contains(candidateId)) {
//            			clsInterview = "status_pendding";
            			dtInterview = (hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_BACKOUT_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_BACKOUT_DATE") : "");
            			txtInterview = "Offer Backout  "+(hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_BACKOUT_COMMENT") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_BACKOUT_COMMENT") : "-");
            		}
            		
            		if(alCandiOnboardPendingIds != null && alCandiOnboardPendingIds.contains(candidateId)) {
//            			clsOnboard = "status_pendding";
            			dtOnboard = (hmRecrAndCandiwiseStatusData.get(candidateId+"_JOINING_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_JOINING_DATE") : "");
            			txtOnboard = "Ready to Onboard ";
            		}
            		if(alCandiOnboardIds != null && alCandiOnboardIds.contains(candidateId)) {
//            			clsOnboard = "status_accept";
            			dtOnboard = (hmRecrAndCandiwiseStatusData.get(candidateId+"_JOINING_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_JOINING_DATE") : "");
            			txtOnboard = "Onboarded ";
            		}
			
					alInnerExport = new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(uF.showData(String.valueOf(nCount), ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(uF.showData(hmEmpName.get(candidateId), ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(uF.showData(dtApply, ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(uF.showData(dtShortlist, ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(uF.showData(txtShortlist, ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(uF.showData(dtInterview, ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(uF.showData(txtInterview, ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(uF.showData(dtOfferOnHold, ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(uF.showData(txtOfferOnHold, ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(uF.showData(dtOffer, ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(uF.showData(txtOffer, ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(uF.showData(dtOfferAcceptReject, ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(uF.showData(txtOfferAcceptReject, ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(uF.showData(dtOfferBackout, ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(uF.showData(txtOfferBackout, ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(uF.showData(dtOnboard, ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(uF.showData(txtOnboard, ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					reportListExport.add(alInnerExport);
            	}
            }
            
            session.setAttribute("reportListExport", reportListExport);
			request.setAttribute("hmRecruitwiseCandiStageStatus", hmRecruitwiseCandiStageStatus);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

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
	
	private void prepareAllData(UtilityFunctions uF, String recruitId) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			List<String> alCandidate = new ArrayList<String>();
			if (recruitId == null || recruitId.equals("")) {
				pst = con.prepareStatement("select cad.recruitment_id,emp_fname,emp_mname,emp_lname,cad.job_code,emp_per_id,cad.candidate_final_status, " +
					"emp_image from candidate_personal_details cpd, candidate_application_details cad where cpd.emp_per_id = cad.candidate_id");
			} else {
				pst = con.prepareStatement("select cad.recruitment_id,emp_fname,emp_mname,emp_lname,cad.job_code,emp_per_id,cad.candidate_final_status, " +
					"emp_image from candidate_personal_details cpd, candidate_application_details cad where cpd.emp_per_id = cad.candidate_id " +
					" and cad.recruitment_id="+ recruitId + "");
			}
//			System.out.println("pst ===> "+ pst);
			
			rst = pst.executeQuery();
//			System.out.println("new Date ===> "+ new Date());
			Map<String, Map<String, Map<String, String>>> hmRecruitwiseCandiData = new HashMap<String, Map<String, Map<String, String>>>();
			Map<String, Map<String, String>> hmCandiwiseData = new HashMap<String, Map<String, String>>();
			while (rst.next()) {

				hmCandiwiseData = hmRecruitwiseCandiData.get(rst.getString("recruitment_id"));
				if(hmCandiwiseData == null) hmCandiwiseData = new HashMap<String, Map<String,String>>();
				
				Map<String, String> hmCandiInner = new HashMap<String, String>();
				
				String strMiddleName = "";
				
				if(flagMiddleName) {
					if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
						strMiddleName = " "+rst.getString("emp_mname");
					}
				}
				
				
				hmCandiInner.put("CANDI_NAME", rst.getString("emp_fname") +strMiddleName+ " " + rst.getString("emp_lname"));
				hmCandiInner.put("CANDI_IMAGE", rst.getString("emp_image"));
				
				hmCandiwiseData.put(rst.getString("emp_per_id"), hmCandiInner);
				
				hmRecruitwiseCandiData.put(rst.getString("recruitment_id"), hmCandiwiseData);
				
			}
			rst.close();
			pst.close();
			
			
			if(alCandidate!=null && alCandidate.size()>0) {
				Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
				if(hmEmpCodeDesig == null) hmEmpCodeDesig = new HashMap<String, String>();
				pst = con.prepareStatement("select * from candidate_personal_details where candididate_emp_id > 0 and emp_per_id in ("+StringUtils.join(alCandidate.toArray(),",")+")");
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
				request.setAttribute("hmCandToEmp", hmCandToEmp);
			}
			
//			System.out.println("hmRecruitwiseCandiData ===>> " + hmRecruitwiseCandiData);
			request.setAttribute("hmRecruitwiseCandiData", hmRecruitwiseCandiData);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	String recruitId;

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
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

}