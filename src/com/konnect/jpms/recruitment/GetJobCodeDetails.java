package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetJobCodeDetails extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId = null;
	String strUserType = null;
	CommonFunctions CF = null;
	
 
	String recruitid;
	String candidateId;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
 
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(PAGE, "/jsp/recruitment/GetJobCodeDetails.jsp");
		request.setAttribute(TITLE, "Job Code Details");
//		System.out.println("recruitid ===> "+recruitid);
		
//		fillupdateinfo();
//		System.out.println("getRecruitid() ===>> " + getRecruitid());
		
		if(getRecruitid() != null && !getRecruitid().equals("")) {
			getSelectedJobProfile();
			getCandidateRatingWithJobCode(uF);
		}
		return LOAD;
	}
	
	
	String job_desc_info = null;
	String cand_profile_info = null;
	// auto fill of candidate profile data...
//	public void fillupdateinfo() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet resultSet = null;
//		
//		try {
//			con = db.makeConnection(con);
//			String infoquery = "select job_description,candidate_profile from recruitment_details where designation_id=(select designation_id from recruitment_details where recruitment_id=? ) and recruitment_id!=? and job_description is not null order by effective_date desc limit 1 ";
//			pst = con.prepareStatement(infoquery);
//			pst.setInt(1, recruitid);
//			pst.setInt(2, recruitid);
//			resultSet = pst.executeQuery();
////			System.out.println("new Date ===> " + new Date());
//			while (resultSet.next()) {
//				job_desc_info = resultSet.getString("job_description");
//				cand_profile_info = resultSet.getString("candidate_profile");
//			}
//		} catch (SQLException e) {
//
//			e.printStackTrace();
//		} finally {
//			
//			db.closeResultSet(resultSet);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	
	private void getSelectedJobProfile() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		List<List<String>> jobProfileLists = new ArrayList<List<String>>();
		try {
			con = db.makeConnection(con);

			List<String> recruitIds = Arrays.asList(getRecruitid().split(","));
			for(int i=0; recruitIds != null && i<recruitIds.size(); i++) {
				String query = "select r.recruitment_id,d.designation_name,g.grade_code,w.wlocation_name,r.no_position,r.comments,r.job_code," +
					"r.job_description,r.min_exp,r.max_exp,r.min_education,r.candidate_profile,r.additional_info,r.job_approval_status," +
					"r.custum_designation,l.level_name,o.org_name,r.skills,r.ideal_candidate,r.priority_job_int from recruitment_details r " +
					" left join grades_details g using(grade_id) left join work_location_info w on r.wlocation=w.wlocation_id left join " +
					" designation_details d on r.designation_id=d.designation_id left join org_details o on r.org_id=o.org_id left join " +
					" level_details l on r.level_id=l.level_id where r.status=1 and r.recruitment_id=? ";
				pst = con.prepareStatement(query);
				pst.setInt(1, uF.parseToInt(recruitIds.get(i)));
				rs = pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
				int nCount = 0;
				while (rs.next()) {
					List<String> jobProfileList = new ArrayList<String>();
					jobProfileList.add(removeNUll(rs.getString(1)));
					if(rs.getString("designation_name")!=null ) {
						jobProfileList.add(removeNUll(rs.getString(2)));
					} else {
						jobProfileList.add(rs.getString("custum_designation"));
					}
					jobProfileList.add(removeNUll(rs.getString(3)));
					jobProfileList.add(removeNUll(rs.getString(4)));
					jobProfileList.add(removeNUll(rs.getString(5)));
					jobProfileList.add(removeNUll(rs.getString(6)));
					jobProfileList.add(removeNUll(rs.getString(7)));
	
					if (job_desc_info != null) {
						jobProfileList.add(job_desc_info);
					} else {
						jobProfileList.add(removeNUll(rs.getString(8)));
					}
					String minex;
					if (rs.getString(9) == null || rs.getString(9).equals("")) {
						minex = removeNUll("0.0");
					} else if (rs.getString(9).contains(".1")) {
						minex = removeNUll(rs.getString(9) + "0");
					} else if (rs.getString(9).contains(".")) {
						minex = removeNUll(rs.getString(9));
					} else {
						minex = removeNUll(rs.getString(9) + ".0");
					}
					String[] minTemp = splitString(minex);
					jobProfileList.add(removeNUll(minTemp[0]));
					jobProfileList.add(removeNUll(minTemp[1]));
	
					String maxex;
					if (rs.getString(10) == null || rs.getString(10).equals("")) {
						maxex = removeNUll("0.0");
					} else if (rs.getString(10).contains(".1")) {
						maxex = removeNUll(rs.getString(10) + "0");
					} else if (rs.getString(10).contains(".")) {
						maxex = removeNUll(rs.getString(10));
					} else {
						maxex = removeNUll(rs.getString(10) + ".0");
					}
					String[] maxTemp = splitString(maxex);
					jobProfileList.add(removeNUll(maxTemp[0]));
					jobProfileList.add(removeNUll(maxTemp[1]));
	//				CF.getDegreeNameByDegreeId(con, degreeId);
					List<String> eduIdList = new ArrayList<String>();
					Map<String, String> hmDegreeName = CF.getDegreeNameMap(con);
					if(rs.getString(11) != null && !rs.getString(11).equals("")) {
						eduIdList = Arrays.asList(rs.getString(11).split(","));
					}
					jobProfileList.add(removeNUll(CF.getAppendData(eduIdList, hmDegreeName))); //educations
	
					if (job_desc_info != null) {
						jobProfileList.add(cand_profile_info);
					} else {
						jobProfileList.add(removeNUll(rs.getString(12)));
					}
					jobProfileList.add(removeNUll(rs.getString(13)));
					jobProfileList.add(removeNUll(rs.getString(14)));
					jobProfileList.add(removeNUll(rs.getString("level_name")));
					jobProfileList.add(removeNUll(rs.getString("org_name")));
					
					List<String> skillsIdList = new ArrayList<String>();
					Map<String, String> hmSkillsName = CF.getSkillNameMap(con);
					if(rs.getString("skills") != null && !rs.getString("skills").equals("")) {
						skillsIdList = Arrays.asList(rs.getString("skills").split(","));
					}
					
					jobProfileList.add(removeNUll(CF.getAppendData(skillsIdList, hmSkillsName))); //skills
					jobProfileList.add(removeNUll(rs.getString("ideal_candidate")));
					if(rs.getInt("priority_job_int")==0){
						jobProfileList.add("Low");
					} else if(rs.getInt("priority_job_int")==1){
						jobProfileList.add("High");
					} else if(rs.getInt("priority_job_int")==2){
						jobProfileList.add("Medium");
					}
					nCount++;
					jobProfileLists.add(jobProfileList);
				}
				rs.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("jobProfileLists", jobProfileLists);
	}
	
	
	private String[] splitString(String st) {
		if (st.equals("") || st.equals("0")) {
			st = "0.0";
		}
		st = st.replace('.', '_');
		String str[] = st.split("_");
		return str;
	}
	
	
	private String removeNUll(String strNull) {

		if (strNull == null) {
			strNull = "";
		}
		return strNull;
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
	
	
	private Map<String, List<String>> getCandiSkillsName(Connection con){
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String, List<String>> hmSkills = new HashMap<String, List<String>>();
		try {
			List<String> skillList = new ArrayList<String>();
			pst=con.prepareStatement("select emp_id,skill_id from candidate_skills_description");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				skillList =hmSkills.get(rst.getString("emp_id"));
				if(skillList== null)skillList = new ArrayList<String>();
				skillList.add(rst.getString("skill_id"));
				hmSkills.put(rst.getString("emp_id"), skillList);
			}
			rst.close();
			pst.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(rst!= null) {
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
	
	private String calculateCandidateStarRating(Connection con, UtilityFunctions uF, List<String> skillsList, List<String> educationsList, List<String> totExpList, String candiGender, String recruitId) {
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		String strStars=null;
		try {
			String desigID = "";
			Map<String, String> hmEduWeightage = getEducationWeightage(con, uF);
			Map<String, String> hmJobDetails = new HashMap<String, String>();
			pst=con.prepareStatement("select * from recruitment_details where recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(recruitId));
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
//			
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
					
					if(uF.parseToDouble(minExp) <= sumcandiExp){
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
		
			int avgMarks = 0;
			if(allCount > 0) {
				avgMarks = allMarks / allCount;
			}
//			System.out.println("avgMarks == "+avgMarks);
			double starrts = uF.parseToDouble(""+avgMarks) / 20;
//			strStars = uF.formatIntoOneDecimalWithOutComma(starrts);
			int intstars = (int) starrts;
			if(starrts>uF.parseToDouble(""+intstars)) {
				strStars = uF.formatIntoOneDecimalWithOutComma(starrts);
			} else {
				strStars = intstars+"";
			}

		} catch(Exception e) {
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
		return strStars;
	}

	
	public List<String> getListData(String strData){
		List<String> dataList = new ArrayList<String>();
		if(strData != null && !strData.equals("")){
			dataList = Arrays.asList(strData.split(","));
		}
		
		return dataList;
	}

private void getCandidateRatingWithJobCode(UtilityFunctions uF) {
	
	Connection con = null;
	PreparedStatement pst = null;
	Database db = new Database();
	db.setRequest(request);
	ResultSet rst = null;
	try {
		con=db.makeConnection(con);
		Map<String, List<String>> hmSkills = getCandiSkillsName(con);
		Map<String, List<String>> hmDegrees = getCandiDegreeName(con);
		Map<String, List<String>> hmTotExp = getCandiTotExp(con,uF);
		
		pst=con.prepareStatement("select emp_per_id,emp_fname,emp_lname,emp_gender from candidate_personal_details cpd " +
				"where cpd.emp_per_id = ? ");
		pst.setInt(1, uF.parseToInt(candidateId));
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		String candiGender = "";
		while(rst.next()){
			candiGender = rst.getString("emp_gender");
		}
		rst.close();
		pst.close();
		
		Map<String, String> hmRecruitIdStar = new HashMap<String, String>();
		List<String> recruitIds = Arrays.asList(getRecruitid().split(","));
		for(int i=0; recruitIds != null && i<recruitIds.size(); i++) {
			String strStars = calculateCandidateStarRating(con, uF, hmSkills.get(candidateId), hmDegrees.get(candidateId), hmTotExp.get(candidateId), candiGender, recruitIds.get(i));
			hmRecruitIdStar.put(recruitIds.get(i), strStars);
		}
//		System.out.println("strStars ===> "+strStars);
//		System.out.println("candidateId ===> "+candidateId);
		request.setAttribute("hmRecruitIdStar", hmRecruitIdStar);
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		
		db.closeResultSet(rst);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}

	public String getCandidateId() {
		return candidateId;
	}
	
	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}

	public String getRecruitid() {
		return recruitid;
	}

	public void setRecruitid(String recruitid) {
		this.recruitid = recruitid;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
