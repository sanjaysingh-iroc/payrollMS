package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ViewCandidatePopUp extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	String organisation;
	String strUserType = null;
	String strSessionEmpId = null;
	String recruitId;
	String type;
	String jobid;
	String selectedEmp;
	
	public String execute() throws Exception {

		request.setAttribute(PAGE, "/jsp/recruitment/ViewCandidatePopUp.jsp");
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		request.setAttribute(TITLE, "Candidates");

		UtilityFunctions uF = new UtilityFunctions();
		/*if (getResetCandi() != null && getResetCandi().equals("reset")){
			//addCandidate(uF);
			candidateReset(uF);
		}*/
		getCandidateList(uF);
//		calculateCandidateStarRating(uF);
		getSelectCandidateList(uF);
		getRejectCandidateList(uF);
			return LOAD;
	}
	

private Map<String, List<String>> getCandiDegreeName(Connection con){
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String, List<String>> hmDegreeName = new HashMap<String, List<String>>();
		try {
			List<String> degreeList = new ArrayList<String>();
			pst=con.prepareStatement("select emp_id,education_id from candidate_education_details");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
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
	
	
	private Map<String, List<List<String>>> getCandiSkillsName(Connection con){
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String, List<List<String>>> hmSkills = new HashMap<String, List<List<String>>>();
		try {
//			List<String> skillList = new ArrayList<String>();
			pst=con.prepareStatement("select emp_id,skill_id,skills_value from candidate_skills_description");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				List<List<String>> skillList = hmSkills.get(rst.getString("emp_id"));
				if(skillList== null)skillList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("skill_id"));
				innerList.add(rst.getString("skills_value"));
				skillList.add(innerList);
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
//			System.out.println("new Date ===> " + new Date());
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
		} finally {
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
//		System.out.println("new Date ===> " + new Date());
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
	} finally {
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
//		System.out.println("new Date ===> " + new Date());
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

	
	
		//===start parvez====
//		private String calculateCandidateStarRating(Connection con, UtilityFunctions uF, List<String> skillsList, List<String> educationsList, List<String> totExpList, String candiGender, String candiTotExperience) {
		private String calculateCandidateStarRating(Connection con, UtilityFunctions uF, List<List<String>> skillsList, List<String> educationsList, List<String> totExpList, String candiGender, String candiTotExperience) {
			
		//===end parvez====
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		String strStars=null;
		try {
			String desigID = "";
			Map<String, String> hmEduWeightage = getEducationWeightage(con, uF);
			Map<String, String> hmJobDetails = new HashMap<String, String>();
			pst=con.prepareStatement("select * from recruitment_details where recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
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
						List<String> innerList = skillsList.get(j);
						if(recSkillsList.get(i).equals(innerList.get(0))) {
							skillMarks = (uF.parseToInt(innerList.get(1))/10)*100;
//							skillMarks = 100;
						}
					}
					skillCount=1;
				}
			}else{
				if(desigSkillList != null && !desigSkillList.isEmpty()){
					for (int i = 0; i < desigSkillList.size(); i++) {
						for (int j = 0; skillsList != null && !skillsList.isEmpty() && j < skillsList.size(); j++) {
							List<String> innerList = skillsList.get(j);
							if(desigSkillList.get(i).equals(innerList.get(j))){
								skillMarks = (uF.parseToInt(innerList.get(1))/10)*100;
//								skillMarks = 100;
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
					if(candiTotExperience!=null && (totExpList==null || totExpList.size()==0)) {
						sumcandiExp = uF.parseToDouble(candiTotExperience);
					} else {
						for (int j = 0; totExpList != null && !totExpList.isEmpty() && j < totExpList.size(); j++) {
	//						System.out.println("totExpList "+j+" == "+totExpList.get(j));
							sumcandiExp += uF.parseToDouble(totExpList.get(j));
						}
					}
//					System.out.println("sumcandiExp == "+sumcandiExp);
					if(uF.parseToDouble(minExp) <= sumcandiExp){
						expMarks = 100;
					}
					expCount=1;
//				}
			}else{
				if(desigTotExp != null && !desigTotExp.equals("")){
					double sumcandiExp=0;
					if(candiTotExperience!=null && (totExpList==null || totExpList.size()==0)) {
						sumcandiExp = uF.parseToDouble(candiTotExperience);
					} else {
						for (int j = 0; totExpList != null && !totExpList.isEmpty() && j < totExpList.size(); j++) {
							sumcandiExp += uF.parseToDouble(totExpList.get(j));
						}
					}
					if(uF.parseToDouble(desigTotExp) <= sumcandiExp){
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
			
//			if(strStars.contains(".0")){
//				
//				strStars = intstars+"";
//				System.out.println("strStars if == "+strStars);
//			}
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

private void getCandidateList(UtilityFunctions uF) {
	
	Connection con = null;
	PreparedStatement pst = null;
	Database db = new Database();
	ResultSet rst = null;
	db.setRequest(request);
	try {
		con=db.makeConnection(con);
		
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
		boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
		
		Map<String, List<List<String>>> hmSkills = getCandiSkillsName(con);
		Map<String, List<String>> hmDegrees = getCandiDegreeName(con);
		Map<String, List<String>> hmTotExp = getCandiTotExp(con, uF);
		
		List<List<String>> allCandidateList = new ArrayList<List<String>>();
		Map<String, String> hmJobCode = new HashMap<String, String>();
		pst=con.prepareStatement("select recruitment_id,job_code from recruitment_details");
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		while(rst.next()){
			hmJobCode.put(rst.getString("recruitment_id"), rst.getString("job_code"));
		}
		rst.close();
		pst.close();
		
		String strCandidateIdOld = null;
		String strCandidateIdNew = null;
		int noyear = 0, nomonth = 0, nodays = 0;
		Map<String, String> hmCandiExp = new HashMap<String, String>();
		//====start parvez on 07-07-2021===
		StringBuilder strCandidateIdNew1 = new StringBuilder();
		//====end parvez on 07-07-2021====
		pst = con.prepareStatement("select to_date,from_date,emp_per_id from candidate_prev_employment cpe join candidate_personal_details cpd " +
			" on(cpd.emp_per_id=cpe.emp_id) where emp_per_id > 0 order by emp_per_id");
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		while(rst.next()){
			
			if(rst.getString("from_date")!=null && rst.getString("to_date")!=null){
				strCandidateIdNew = rst.getString("emp_per_id");
				if (strCandidateIdNew != null && !strCandidateIdNew.equals(strCandidateIdOld)) {
					noyear = 0;
					nomonth = 0;
					nodays = 0;
				}
	
				String datedif = uF.dateDifference(rst.getString("from_date"), DBDATE, rst.getString("to_date"), DBDATE);
				long datediff = uF.parseToLong(datedif);
	
				noyear += (int) (datediff / 365);
				nomonth += (int) ((datediff % 365) / 30);
				nodays += (int) ((datediff % 365) % 30);
	
				if (nodays > 30) {
					nomonth = nomonth + 1;
				}
				if (nomonth > 12) {
					nomonth = nomonth - 12;
					noyear = noyear + 1;
				}
				//====start parvez on 06-08-2021 replace emp_id with emp_per_id====
				hmCandiExp.put(rst.getString("emp_per_id"), "" + noyear + " Year " + nomonth + " months ");
				strCandidateIdOld = strCandidateIdNew;
				//====start parvez on 07-07-2021====
		    	if(strCandidateIdNew1.length()==0){
//		    		strCandidateIdNew1.append(rst.getString("emp_id"));
		    		strCandidateIdNew1.append(rst.getString("emp_per_id"));
		    	}else{
//		    		strCandidateIdNew1.append(","+rst.getString("emp_id"));
		    		strCandidateIdNew1.append(","+rst.getString("emp_per_id"));
		    	}
		    	//====end parvez on 07-07-2021====
			}
//			String frmdt = rst.getString("from_date");
//			String todt = rst.getString("to_date");
//			String candidateExp = "";
//			if(frmdt != null && todt != null){
//				candidateExp = uF.getTimeDurationBetweenDates(frmdt, DBDATE, todt, DBDATE, CF, uF, request);
//			}
//			hmCandiExp.put(rst.getString("emp_id"), candidateExp);
		}
		rst.close();
		pst.close();
		
		
		//=====start parvez on 07-07-2021====
		if(strCandidateIdNew1.length()==0){
			pst = con.prepareStatement("select * from candidate_personal_details");
			rst=pst.executeQuery();
			while(rst.next()){
				hmCandiExp.put(rst.getString("emp_per_id"), rst.getString("total_experience"));
			}
		}else{
			pst = con.prepareStatement("select * from candidate_personal_details where emp_per_id not in ("+strCandidateIdNew1+")");
			rst=pst.executeQuery();
			while(rst.next()){
				hmCandiExp.put(rst.getString("emp_per_id"), rst.getString("total_experience"));
			}
		}
		//====end parvez on 07-07-2021====
		
		
//		pst=con.prepareStatement("select emp_per_id,emp_fname,emp_lname,recruitment_id,emp_gender from candidate_personal_details cpd " +
//				"where cpd.candidate_joining_date is null and cpd.recruitment_id = ? and cpd.application_status = 0 " +
//				" and candidate_final_status = 0 and candidate_status = 0 order by emp_fname,emp_lname");
		pst=con.prepareStatement("select emp_per_id,emp_fname,emp_mname, emp_lname,cad.recruitment_id,emp_gender,total_experience from candidate_personal_details cpd, " +
				"candidate_application_details cad where cpd.emp_per_id = cad.candidate_id and cad.candidate_joining_date is null and " +
				"cad.application_status = 0 and cad.candidate_final_status = 0 and cad.candidate_status = 0 and cad.recruitment_id = ?" +
				" order by emp_fname,emp_lname");
		pst.setInt(1, uF.parseToInt(getRecruitId()));
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		List<String> alCandidate = new ArrayList<String>();
		while(rst.next()){
			List<String> innerList = new ArrayList<String>();
			String candiID = rst.getString("emp_per_id");
			String candiGender = rst.getString("emp_gender");
			String candiTotExperience = rst.getString("total_experience");
			String strStars = calculateCandidateStarRating(con, uF, hmSkills.get(candiID), hmDegrees.get(candiID), hmTotExp.get(candiID), candiGender, candiTotExperience);
			innerList.add(rst.getString("emp_per_id"));
		
			String strEmpMName = "";
			if(flagMiddleName) {
				if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
					strEmpMName = " "+rst.getString("emp_mname");
				}
			}
			
			innerList.add(rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
			innerList.add(getAppendDataDegree(con, hmDegrees.get(rst.getString("emp_per_id"))));
			innerList.add(hmJobCode.get(rst.getString("recruitment_id")));
			innerList.add(getAppendDataSkill(con, hmSkills.get(rst.getString("emp_per_id"))));
			innerList.add(hmCandiExp.get(rst.getString("emp_per_id")));
			innerList.add(strStars);
			allCandidateList.add(innerList);
			
			alCandidate.add(rst.getString("emp_per_id"));
		}
		rst.close();
		pst.close();
		
		if(alCandidate!=null && alCandidate.size()>0){
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			if(hmEmpCodeDesig == null) hmEmpCodeDesig = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from candidate_personal_details where candididate_emp_id > 0 and emp_per_id in ("+StringUtils.join(alCandidate.toArray(),",")+")");
			rst = pst.executeQuery();
			Map<String, String> hmCandToEmp = new HashMap<String, String>();
			while(rst.next()){
				
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
		
		request.setAttribute("allCandidateList", allCandidateList);
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		
		db.closeResultSet(rst);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}


public String getAppendDataDegree(Connection con, List<String> strID) {
	StringBuilder sb = new StringBuilder();
	if (strID != null) {
		for (int i = 0; i < strID.size(); i++) {
			if (i == 0) {
				String degreeName = CF.getDegreeNameByDegreeId(con, strID.get(i));
				sb.append(degreeName);
			} else {
				String degreeName = CF.getDegreeNameByDegreeId(con, strID.get(i));
				sb.append(", "+degreeName);
			}
		}
	} else {
		return null;
	}
	return sb.toString();
}


public String getAppendDataSkill(Connection con, List<List<String>> strID) {
	StringBuilder sb = new StringBuilder();
	if (strID != null) {
		for (int i = 0; i < strID.size(); i++) {
			List<String> innList = strID.get(i);
			if (i == 0) {
				String skillName = CF.getSkillNameBySkillId(con, innList.get(0));
				sb.append(skillName);
			} else {
				String skillName = CF.getSkillNameBySkillId(con, innList.get(0));
				sb.append(", "+skillName);
			}
		}
	} else {
		return null;
	}
	return sb.toString();
}

	
private void getSelectCandidateList(UtilityFunctions uF) {
	
	Connection con = null;
	PreparedStatement pst = null;
	Database db = new Database();
	db.setRequest(request);
	ResultSet rst = null;

	Map<String, String> hmCandiName = new HashMap<String, String>();
	Map<String, String> hmCandiProfile = new HashMap<String, String>();
	
	try {
		con=db.makeConnection(con);
		
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
		boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
		
		Map<String, String> hmNotificationStatus = new HashMap<String, String>();
		pst=con.prepareStatement("select candidate_id,send_notification_status from candidate_application_details where " +
		"recruitment_id=? and (application_status=1 or application_status=2) and candidate_final_status = 0 and candidate_status = 0");
		pst.setInt(1,uF.parseToInt(getRecruitId()));
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		while(rst.next()){
			hmNotificationStatus.put(rst.getString("candidate_id"), rst.getString("send_notification_status"));
		}
		rst.close();
		pst.close();
		
		Map<String, String> hmInterviewSelectOrReject = new HashMap<String, String>();
		pst=con.prepareStatement("select candidate_id,status from candidate_interview_panel where recruitment_id=? and status!=0");
		pst.setInt(1,uF.parseToInt(getRecruitId()));
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		while(rst.next()){
			hmInterviewSelectOrReject.put(rst.getString("candidate_id"), rst.getString("status"));
		}
		rst.close();
		pst.close();
		
		pst=con.prepareStatement("select candidate_id,candidate_final_status from candidate_application_details where recruitment_id=? and candidate_final_status!=0");
		pst.setInt(1,uF.parseToInt(getRecruitId()));
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		while(rst.next()){
			hmInterviewSelectOrReject.put(rst.getString("candidate_id"), rst.getString("candidate_final_status"));
		}
		rst.close();
		pst.close();
		System.out.println("hmInterviewSelectOrReject ===>> " + hmInterviewSelectOrReject);
		
				
//		pst=con.prepareStatement("select emp_per_id,emp_fname,emp_lname,emp_image from candidate_personal_details where " +
//				"recruitment_id=? and application_status = 1 and candidate_final_status = 0 and candidate_status = 0");
		pst = con.prepareStatement("select emp_per_id,emp_fname,emp_mname,emp_lname,emp_image from candidate_personal_details cpd, " +
			"candidate_application_details cad where cpd.emp_per_id = cad.candidate_id and (cad.application_status=1 or cad.application_status=2) " +
			"and cad.candidate_final_status = 0 and cad.candidate_status = 0 and cad.recruitment_id=?");
		pst.setInt(1,uF.parseToInt(getRecruitId()));
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		System.out.println("pst =====>> "+pst);
		String selectCandiIDs="";
		int cnt=0;
		while(rst.next()){
			String strEmpMName = "";
			if(flagMiddleName) {
				if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
					strEmpMName = " "+rst.getString("emp_mname");
				}
			}
			
			hmCandiName.put(rst.getString("emp_per_id"), rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
			hmCandiProfile.put(rst.getString("emp_per_id"), rst.getString("emp_image"));
			if(cnt == 0){
				selectCandiIDs += rst.getString("emp_per_id");
				cnt++;
			} else {
				selectCandiIDs += ","+rst.getString("emp_per_id");
			}
//			System.out.println("rst.getString(emp_per_id) ===== > "+rst.getString("emp_per_id"));
		}
		rst.close();
		pst.close();
		
//		System.out.println("selectCandiIDs ===== > "+selectCandiIDs);
		
		List<List<String>> selectCandidateList = new ArrayList<List<String>>();
//		List<String> selectCandidateIds = new ArrayList<String>();
		if(selectCandiIDs !=null && !selectCandiIDs.equals("")){
			Set<String> candiSet = new HashSet<String>(Arrays.asList(selectCandiIDs.split(",")));
			Iterator<String> itr = candiSet.iterator();
			while (itr.hasNext()) {
				List<String> innerList = new ArrayList<String>();
				String candiId = (String) itr.next();
				if(candiId!=null && !candiId.equals("")) {
					if(hmNotificationStatus.get(candiId).equals("0")) {
						innerList.add(""+hmCandiName.get(candiId.trim())
						+"<img border=\"0\" style=\"padding: 5px 5px 0pt;\" width=\"21\" src=\""+request.getContextPath()+"/images1/icons/news_icon.gif\"/>");
					} else {
						innerList.add(""+hmCandiName.get(candiId.trim()));
					}
					innerList.add(candiId.trim());
					innerList.add(hmInterviewSelectOrReject.get(candiId.trim()));
//					System.out.println("selectCandidateIds 1 ===== > "+selectCandidateIds);
				}
				selectCandidateList.add(innerList);
			}
		} else {
			selectCandidateList=null;
		}
//		request.setAttribute("selectCandidateIds", selectCandidateIds);
		request.setAttribute("selectCandidateList", selectCandidateList);
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		
		db.closeResultSet(rst);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	
}


private void getRejectCandidateList(UtilityFunctions uF) {
	
	Connection con = null;
	PreparedStatement pst = null;
	Database db = new Database();
	db.setRequest(request);
	ResultSet rst = null;

	Map<String, String> hmCandiName = new HashMap<String, String>();
	Map<String, String> hmCandiProfile = new HashMap<String, String>();
	try {
		con=db.makeConnection(con);
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
		boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
//		pst=con.prepareStatement("select emp_per_id,emp_fname,emp_lname,emp_image from candidate_application_details where " +
//				"recruitment_id=? and application_status = -1 and candidate_final_status = 0 and candidate_status = 0");
		pst=con.prepareStatement("select emp_per_id,emp_fname,emp_mname,emp_lname,emp_image from candidate_personal_details cpd, " +
				"candidate_application_details cad where cpd.emp_per_id = cad.candidate_id and cad.application_status = -1 " +
				"and cad.candidate_final_status = 0 and cad.candidate_status = 0 and cad.recruitment_id=?");
		pst.setInt(1,uF.parseToInt(getRecruitId()));
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
//		System.out.println("pst ===== > "+pst);
		String selectCandiIDs="";
		int cnt=0;
		while(rst.next()){
			
			String strEmpMName = "";
			if(flagMiddleName) {
				if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
					strEmpMName = " "+rst.getString("emp_mname");
				}
			}
			
			hmCandiName.put(rst.getString("emp_per_id"), rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
			hmCandiProfile.put(rst.getString("emp_per_id"), rst.getString("emp_image"));
			if(cnt == 0){
				selectCandiIDs += rst.getString("emp_per_id");
				cnt++;
			}else{
				selectCandiIDs += ","+rst.getString("emp_per_id");
			}
//			System.out.println("rst.getString(emp_per_id) ===== > "+rst.getString("emp_per_id"));
		}
		rst.close();
		pst.close();
		
//		System.out.println("selectCandiIDs ===== > "+selectCandiIDs);
		
		List<String> rejectCandidateList=new ArrayList<String>();
		List<String> rejectCandidateIds=new ArrayList<String>();
		if(selectCandiIDs !=null && !selectCandiIDs.equals("")){
			Set<String> candiSet = new HashSet<String>(Arrays.asList(selectCandiIDs.split(",")));
			Iterator<String> itr = candiSet.iterator();
			while (itr.hasNext()) {
				String candiId = (String) itr.next();
				if(candiId!=null && !candiId.equals("")){
					rejectCandidateList.add(""+hmCandiName.get(candiId.trim()));
					rejectCandidateIds.add(candiId.trim());
				}
			}
		}else{
			rejectCandidateList=null;
		}
		request.setAttribute("rejectCandidateIds", rejectCandidateIds);
		request.setAttribute("rejectCandidateList", rejectCandidateList);
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		
		db.closeResultSet(rst);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}


	/*private void getSelectEmployeeList() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		ResultSet rst = null;
		UtilityFunctions uF=new UtilityFunctions();
		Map<String, String> hmEmpName = CF.getEmpNameMap(null, null);
		Map<String, String> hmEmpProfile = CF.getEmpProfileImage();
		
		try {
		
			con=db.makeConnection(con);
						
			pst=con.prepareStatement("select panel_employee_id,effective_date from recruitment_details where recruitment_id=?");
			pst.setInt(1,uF.parseToInt(getRecruitID()));
			rst=pst.executeQuery();
			String selectEmpIDs=null;
			String effectDate=null;
			while(rst.next()){
				selectEmpIDs=rst.getString("panel_employee_id");
				effectDate = rst.getString("effective_date");
			}
			
			boolean comparedate = uF.getCurrentDate(CF.getStrTimeZone()).before(uF.getDateFormatUtil(effectDate,DBDATE));
			Map<String, String> sltEmpDtCmprHm = new HashMap<String, String>();
			if(selectEmpIDs!=null && !selectEmpIDs.equals("")){
			String tmpsltempids = selectEmpIDs.substring(1, selectEmpIDs.length()-1);
			List<String> selectedEmpIdsLst=Arrays.asList(tmpsltempids.split(","));
			
			for(int i=0; selectedEmpIdsLst!= null && i<selectedEmpIdsLst.size(); i++){
				sltEmpDtCmprHm.put(selectedEmpIdsLst.get(i).trim(), ""+comparedate);
			}
			}
//			System.out.println("sltEmpDtCmprHm ========== "+sltEmpDtCmprHm);
			List<String> selectEmpIds=new ArrayList<String>();
			List<String> selectEmpNameList=new ArrayList<String>();
			if(selectEmpIDs!=null && !selectEmpIDs.equals("")){
				Set<String> empSet = new HashSet<String>(Arrays.asList(selectEmpIDs.split(",")));
				Iterator<String> itr = empSet.iterator();
				while (itr.hasNext()) {
					String empId = (String) itr.next();
					if(empId!=null && !empId.equals("")){
//						<img  class=\"lazy\" src=\"userImages/avatar_photo.png\"  data-original=\"userImages/"+uF.showData(empImageMap.get(empList.get(i).trim()), "avatar_photo.png")+"\" border=\"0\" height=\"16px\" width=\"16px\" title=\""+hmEmpName.get(empList.get(i).trim())+"\"/>
						selectEmpNameList.add("<img style=\"margin-right:5px\" class=\"lazy\" width=\"18\" src=\"userImages/avatar_photo.png\" data-original=\"userImages/"+uF.showData(hmEmpProfile.get(empId.trim()),"avatar_photo.png")+"\">"+hmEmpName.get(empId.trim()));
						selectEmpIds.add(empId.trim());
					}
				}
			}else{
				selectEmpNameList=null;
			}
			
			Map<String,String> hmWlocation=new HashMap<String, String>();
          pst=con.prepareStatement("select emp_id,wlocation_name from employee_official_details join work_location_info using (wlocation_id)");
          rst=pst.executeQuery();
          while(rst.next()){
        	  hmWlocation.put(rst.getString("emp_id"), rst.getString("wlocation_name"));
        	  
          }
          request.setAttribute("selectEmpIds", selectEmpIds);
          request.setAttribute("sltEmpDtCmprHm", sltEmpDtCmprHm);
          
			request.setAttribute("hmWlocation", hmWlocation);	
			request.setAttribute("selectEmpNameList", selectEmpNameList);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		
			db.closeConnection(con);
			db.closeResultSet(rst);
			db.closeStatements(pst);
		}
		
	}*/


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	// Variablessssssss=================================

	
	public String getJobid() {
		return jobid;
	}

	public void setJobid(String jobid) {
		this.jobid = jobid;
	}
	
	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSelectedEmp() {
		return selectedEmp;
	}

	public void setSelectedEmp(String selectedEmp) {
		this.selectedEmp = selectedEmp;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	
}
