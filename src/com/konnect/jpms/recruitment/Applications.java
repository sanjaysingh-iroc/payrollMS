package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Applications extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId = null;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	CommonFunctions CF = null;
 
	private String recruitId;
	private String fromPage;
	
	private String currUserType;
	private String alertStatus;
	private String alert_type;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
 
		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		/*boolean isView = CF.getAccess(session, request, uF); 
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied); 
			return ACCESS_DENIED; 
		}*/
		 
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		if(strUserType != null && strUserType.equals(HRMANAGER) && getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(CANDIDATE_FINALIZATION_ALERT)){
			updateUserAlerts();
		}
		
		request.setAttribute(PAGE, "/jsp/recruitment/Applications.jsp");
		request.setAttribute(TITLE, "Applications");
		
		if(getRecruitId()==null || getRecruitId().equals(null) || getRecruitId().equalsIgnoreCase("null")){
			recruitId = (String) session.getAttribute(EMPID);
		}
//		System.out.println("in Application getRecruitId() ===>> " + getRecruitId());
		getLiveRecruitmentIds(uF, getRecruitId());
		prepareHeaderInfo(uF);
		prepareAllData(uF);
		getRoundStatus(uF);
//		prepareTodayAppl(getStrDashboardRequest()); // Query problem
		newApplicationsRecruitIdwise(uF);
		getRecruitmentwiseAssessmentScoreCard(uF);
	
		return SUCCESS;
	}
	
	
	private void getRecruitmentwiseAssessmentScoreCard(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		try {
			if (getRecruitId() != null && !getRecruitId().equals("")) {
				StringBuilder sbQue = new StringBuilder();
				sbQue.append("select * from(select sum(marks) as marks ,sum(weightage) as weightage,assessment_details_id,round_id" +
						",recruitment_id,candidate_id from assessment_question_answer where candidate_id>0 ");
				if (getRecruitId() != null && !getRecruitId().equals("")) {
					sbQue.append(" and recruitment_id in ("+getRecruitId()+") ");
				}
				sbQue.append(" group by recruitment_id,candidate_id,round_id,assessment_details_id)as a");
				pst = con.prepareStatement(sbQue.toString());
	//			pst.setInt(1, uF.parseToInt(getRecruitId()));
	//			pst.setInt(2, uF.parseToInt(getCandID()));
				rs = pst.executeQuery();
				Map<String, String> hmAssessRateRecruitAndRoundIdWise = new HashMap<String, String>();
				while (rs.next()) {
					
					double dblMarks = uF.parseToDouble(rs.getString("marks"));
	//				System.out.println("dblTotalMarks"+dblTotalMarks);
					double dblWeightage = uF.parseToDouble(rs.getString("weightage"));
					String aggregate = "0";
					if(dblWeightage>0) {
						aggregate = uF.formatIntoTwoDecimal((dblMarks * 100)/dblWeightage);
					}
					hmAssessRateRecruitAndRoundIdWise.put(rs.getString("recruitment_id")+"_"+rs.getString("candidate_id")+"_"+rs.getString("round_id")+"_"+rs.getString("assessment_details_id"), aggregate);
				}
				rs.close();
				pst.close();
				
	//			System.out.println("hmAssessRateRecruitAndRoundIdWise ===>> " + hmAssessRateRecruitAndRoundIdWise);
				// request.setAttribute("hmUserTypeID", hmUserTypeID);
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
	
	private void updateUserAlerts() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
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
			userAlerts.set_type(CANDIDATE_FINALIZATION_ALERT);
			userAlerts.setStatus(UPDATE_ALERT);
			Thread t = new Thread(userAlerts);
			t.run();
//			System.out.println("in Appraisal UserAlerts ...");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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
		StringBuilder sbQue = new StringBuilder();
		sbQue.append("select emp_per_id,emp_fname,emp_lname,cad.recruitment_id,emp_gender from candidate_personal_details cpd, " +
			"candidate_application_details cad where cpd.emp_per_id = cad.candidate_id and cad.recruitment_id = ? ");
		sbQue.append(" order by emp_fname,emp_lname");
		pst=con.prepareStatement(sbQue.toString());
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
		
//		request.setAttribute("hmCandiStars", hmCandiStars);
//		System.out.println("hmCandiStars ===> "+hmCandiStars);
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
	
	
private void getLiveRecruitmentIds(UtilityFunctions uF, String recruitId) {
	Connection con = null;
	Database db = new Database();
	db.setRequest(request);
	PreparedStatement pst = null;
	ResultSet rst = null;
	try {
		con = db.makeConnection(con);
		List<String> recruitmentIdList = new ArrayList<String>();
		Map<String, String> hmJobStatus = new HashMap<String, String>();
		Map<String, Map<String, String>> hmcandiStarRecruitwise = new HashMap<String, Map<String,String>>();
		
		StringBuilder sbQuery = new StringBuilder();
		
		sbQuery = new StringBuilder();
		sbQuery.append("select job_code,recruitment_id,custum_designation,close_job_status from recruitment_details where job_approval_status=1 ");

		if (recruitId != null && !recruitId.equals("") && uF.parseToInt(recruitId) > 0){
			sbQuery.append(" and recruitment_id = " + recruitId + " ");
		}
		sbQuery.append(" order by close_job_status,job_approval_date desc,recruitment_id desc");
		pst = con.prepareStatement(sbQuery.toString());
		rst = pst.executeQuery();
//		System.out.println("pstlive===> "+ pst); 
		StringBuilder sbRecruitIds = null;
		while (rst.next()) {
			hmJobStatus.put(rst.getString("recruitment_id"), uF.parseToBoolean(rst.getString("close_job_status"))+"");
			recruitmentIdList.add(rst.getString("recruitment_id"));
			if(sbRecruitIds == null) {
				sbRecruitIds = new StringBuilder();
				sbRecruitIds.append(rst.getString("recruitment_id"));
			} else {
				sbRecruitIds.append(","+rst.getString("recruitment_id"));
			}
			Map<String, String> hmCandiStars = getCandidateList(con, uF, rst.getString("recruitment_id"));
			hmcandiStarRecruitwise.put(rst.getString("recruitment_id"), hmCandiStars);
		}
		rst.close();
		pst.close();
		if(sbRecruitIds != null) {
			setRecruitId(sbRecruitIds.toString());
			getSourceDetails(con,uF,sbRecruitIds.toString());
		}
//		System.out.println("hmcandiStarRecruitwise ===> "+hmcandiStarRecruitwise);
		request.setAttribute("hmcandiStarRecruitwise", hmcandiStarRecruitwise);
		request.setAttribute("hmJobStatus", hmJobStatus);
		request.setAttribute("recruitmentIdList", recruitmentIdList);
//		System.out.println("recruitmentIdList===>"+recruitmentIdList);
//		System.out.println("recruitmentIdList Size===>"+recruitmentIdList.size());
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
			
			Map<String, Map<String, Map<String, String>>> hmSource = new HashMap<String, Map<String, Map<String, String>>>();
			Map<String, Map<String, Map<String, String>>> hmCandiOnHold = new HashMap<String, Map<String, Map<String, String>>>();
			pst = con.prepareStatement("select * from candidate_application_details where recruitment_id in ("+strRecruitIds+") order by recruitment_id");
			rs = pst.executeQuery();
			while(rs.next()){
				 Map<String, Map<String, String>> hmCandidate = hmSource.get(rs.getString("recruitment_id"));
				 if(hmCandidate == null) hmCandidate = new HashMap<String, Map<String,String>>();
				 
				 Map<String, String> hmSourceDetails = hmCandidate.get(rs.getString("candidate_id"));
				 if(hmSourceDetails == null) hmSourceDetails = new HashMap<String, String>(); 
				 
				 Map<String, Map<String, String>> hmCandiHoldInn = hmCandiOnHold.get(rs.getString("recruitment_id"));
				 if(hmCandiHoldInn == null) hmCandiHoldInn = new HashMap<String, Map<String,String>>();
				 
				 Map<String, String> hmHoldDetails = hmCandiHoldInn.get(rs.getString("candidate_id"));
				 if(hmHoldDetails == null) hmHoldDetails = new HashMap<String, String>(); 
				 
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

				 hmHoldDetails.put("HOLD_STATUS", rs.getString("offer_backout_status"));
				 hmHoldDetails.put("HOLD_REMARK", rs.getString("offer_backout_remark"));
				 hmHoldDetails.put("HOLD_DATE", uF.getDateFormat(rs.getString("offer_backout_date"), DBDATE, DATE_FORMAT_STR));
				 
				 hmCandiHoldInn.put(rs.getString("candidate_id"), hmHoldDetails);
				 hmCandiOnHold.put(rs.getString("recruitment_id"), hmCandiHoldInn);
				 
				 hmCandidate.put(rs.getString("candidate_id"), hmSourceDetails);
				 hmSource.put(rs.getString("recruitment_id"), hmCandidate);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmCandiOnHold", hmCandiOnHold);
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

	private void getRoundStatus(UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String, String> hmRoundStatus = new LinkedHashMap<String, String>();
		try {
			con = db.makeConnection(con);
			if (getRecruitId() != null && !getRecruitId().equals("")) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select candidate_id,recruitment_id,panel_round_id,status,comments from candidate_interview_panel where candidate_id>0 ");
				if (getRecruitId() != null && !getRecruitId().equals("")) {
					sbQuery.append(" and recruitment_id in ("+getRecruitId()+") ");
				}
				sbQuery.append(" order by panel_id desc");
				pst = con.prepareStatement(sbQuery.toString());
				rst = pst.executeQuery();
//				System.out.println("===>pst"+pst);
				//			System.out.println("new Date ===> "+ new Date());
				while (rst.next()) {
					hmRoundStatus.put(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id")+"_"+rst.getString("panel_round_id"), rst.getString("status"));
				}
				rst.close();
				pst.close();
				
	//			System.out.println("hmRoundStatus ===> "+hmRoundStatus);
				request.setAttribute("hmRoundStatus", hmRoundStatus);
			}	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	
	
	private void newApplicationsRecruitIdwise(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String, List<List<String>>> hmNewApplications = new LinkedHashMap<String, List<List<String>>>();
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			if (getRecruitId() != null && !getRecruitId().equals("")) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select cpd.emp_per_id,emp_fname,emp_mname,emp_lname,cad.recruitment_id from candidate_personal_details cpd, " +
						"candidate_application_details cad where cpd.emp_per_id = cad.candidate_id and cad.application_status = 0 ");
				if (getRecruitId() != null && !getRecruitId().equals("")) {
					sbQuery.append(" and cad.recruitment_id in ("+getRecruitId()+") ");
				}
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst===>"+pst);
				rst = pst.executeQuery();
				System.out.println();
	//			System.out.println("new Date ===> "+ new Date());
				while (rst.next()) {
					List<List<String>> newAppList = hmNewApplications.get(rst.getString("recruitment_id"));
					if (newAppList == null)newAppList = new ArrayList<List<String>>();
					List<String> innerList = new ArrayList<String>();
					innerList.add(rst.getString("emp_per_id"));
					
					String strMiddleName = "";
					
					if(flagMiddleName) {
						if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
							strMiddleName = " "+rst.getString("emp_mname");
						}
					}
					
					innerList.add(rst.getString("emp_fname")+strMiddleName+" "+rst.getString("emp_lname"));
					newAppList.add(innerList);
					hmNewApplications.put(rst.getString("recruitment_id"), newAppList);
				}
				rst.close();
				pst.close();
	//			System.out.println("hmNewApplications ===> "+hmNewApplications);
				request.setAttribute("hmNewApplications", hmNewApplications);
				
				
				Map<String, String> hmJobCodeName = new HashMap<String, String>();
				Map<String, String> hmJobPriority = new HashMap<String, String>();
				Map<String, String> hmJobTitle = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select job_code,priority_job_int,recruitment_id,job_title from recruitment_details where recruitment_id > 0 ");
				if (getRecruitId() != null && !getRecruitId().equals("")) {
					sbQuery.append(" and recruitment_id in ("+getRecruitId()+") ");
				}
				pst = con.prepareStatement(sbQuery.toString());
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> "+ new Date());
				while (rst.next()) {
					hmJobCodeName.put(rst.getString("recruitment_id"), rst.getString("job_code"));
					hmJobPriority.put(rst.getString("recruitment_id"), rst.getString("priority_job_int"));
					hmJobTitle.put(rst.getString("recruitment_id"), rst.getString("job_title"));
				}
				rst.close();
				pst.close();
				
				request.setAttribute("hmJobCodeName", hmJobCodeName);
				request.setAttribute("hmJobPriority", hmJobPriority);
				request.setAttribute("hmJobTitle", hmJobTitle);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private String prepareHeaderInfo(UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String, String> applyMp = new LinkedHashMap<String, String>();
		Map<String, String> approveMp = new LinkedHashMap<String, String>();
		Map<String, String> denyMp = new LinkedHashMap<String, String>();
		Map<String, String> finalisedMp = new LinkedHashMap<String, String>();

		try {
			
			con = db.makeConnection(con);
			if (getRecruitId() != null && !getRecruitId().equals("")) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select  count(*) as count,cad.recruitment_id, cad.application_status, cad.candidate_final_status,cad.candidate_status from " +
					"candidate_application_details cad, recruitment_details rd where cad.recruitment_id = rd.recruitment_id and rd.close_job_status = false ");
				if (getRecruitId() != null && !getRecruitId().equals("")) {
					sbQuery.append(" and cad.recruitment_id in ("+getRecruitId()+") ");
				}
				sbQuery.append(" group by cad.recruitment_id, cad.application_status, cad.candidate_final_status,cad.candidate_status order by cad.candidate_final_status,cad.recruitment_id desc");
				pst = con.prepareStatement(sbQuery.toString());
				int dblTotalApplication = 0, approve = 0;
//				System.out.println("pst====>"+pst);
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> "+ new Date());
				while (rst.next()) {
	
					if (applyMp.get(rst.getString("recruitment_id")) != null) {
	
						dblTotalApplication = uF.parseToInt(applyMp.get(rst.getString("recruitment_id")));
						dblTotalApplication += rst.getInt("count");
						applyMp.put(rst.getString("recruitment_id"), String.valueOf(dblTotalApplication));
	
					} else {
	
						applyMp.put(rst.getString("recruitment_id"), rst.getString("count"));
					}
	
					if (uF.parseToInt(rst.getString("candidate_final_status")) == 1) {
	
						finalisedMp.put(rst.getString("recruitment_id"), rst.getString("count"));
					}
					
					if (uF.parseToInt(rst.getString("candidate_final_status")) == 1) {
	
						finalisedMp.put(rst.getString("recruitment_id"), rst.getString("count"));
					}
	
					if (uF.parseToInt(rst.getString("application_status")) == 2 && uF.parseToInt(rst.getString("candidate_final_status")) == 0  && uF.parseToInt(rst.getString("candidate_status")) == 0) {
	
						approve = uF.parseToInt(approveMp.get(rst.getString("recruitment_id")));
						approve += rst.getInt("count");
						approveMp.put(rst.getString("recruitment_id"), String.valueOf(approve));
	
					} else if (uF.parseToInt(rst.getString("application_status")) == -1) {
	
						denyMp.put(rst.getString("recruitment_id"), rst.getString("count"));
					}
	
				}
				rst.close();
				pst.close();
				
				List<Map<String, String>> alheaderfirst = new ArrayList<Map<String, String>>();
	
				alheaderfirst.add(applyMp);
				alheaderfirst.add(approveMp);
				alheaderfirst.add(denyMp);
				alheaderfirst.add(finalisedMp);
	
				request.setAttribute("applicationstatus", alheaderfirst);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	private void prepareAllData(UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			if(getRecruitId() != null && !getRecruitId().equals("")) {
				
				Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
				request.setAttribute("hmEmpName", hmEmpName);
				
				Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
				if(hmUserTypeIdMap == null) hmUserTypeIdMap = new HashMap<String, String>();
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select effective_id, min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
					"and effective_type='"+WORK_FLOW_RESUME_SHORTLIST+"' and effective_id in (select candi_application_deatils_id from " +
					"candidate_application_details where recruitment_id in ("+getRecruitId()+")) group by effective_id");
				pst = con.prepareStatement(sbQuery.toString());
				rst = pst.executeQuery();
				Map<String, String> hmNextApproval = new HashMap<String, String>();
				while(rst.next()) {
					hmNextApproval.put(rst.getString("effective_id"), rst.getString("member_position"));
				}
				rst.close();
				pst.close();
//				System.out.println("hmNextApproval ===>> " + hmNextApproval);
				sbQuery=new StringBuilder();
				sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
					" and is_approved=0 and effective_type='"+WORK_FLOW_RESUME_SHORTLIST+"' and effective_id in (select candi_application_deatils_id from " +
					"candidate_application_details where recruitment_id in ("+getRecruitId()+")");
				if(strUserType != null && strUserType.equals(ADMIN)) {
					sbQuery.append(") and (user_type_id=? or user_type_id=?) ");
				} else if (strUserType != null && strUserType.equals(MANAGER) && strBaseUserType != null && (strBaseUserType.equals(HOD) || strBaseUserType.equals(CEO))) {
					sbQuery.append(") and (user_type_id=? or user_type_id=?) ");
				} else {
					sbQuery.append(") and user_type_id=? ");
				}
				sbQuery.append(" group by effective_id,user_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1,uF.parseToInt(strSessionEmpId));
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
					pst.setInt(2, uF.parseToInt(strBaseUserTypeId));
				} else {
					pst.setInt(2, uF.parseToInt(strUserTypeId));
				}
				if(strUserType != null && strUserType.equals(ADMIN)) {
					pst.setInt(3, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
				} else if (strUserType != null && strUserType.equals(MANAGER) && strBaseUserType != null && (strBaseUserType.equals(HOD) || strBaseUserType.equals(CEO))) {
					pst.setInt(3, uF.parseToInt(hmUserTypeIdMap.get(strBaseUserType)));
				}
//				System.out.println("pst ===>> " + pst);
				rst = pst.executeQuery();
				Map<String, String> hmMemNextApproval = new HashMap<String, String>();
				while(rst.next()) {
					hmMemNextApproval.put(rst.getString("effective_id")+"_"+rst.getString("user_type_id"), rst.getString("member_position"));
				}
				rst.close();
				pst.close();
//				System.out.println("hmMemNextApproval ===>> " + hmMemNextApproval);
				
				sbQuery=new StringBuilder();
				sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_RESUME_SHORTLIST+"' and effective_id in " +
					"(select candi_application_deatils_id from candidate_application_details where recruitment_id in ("+getRecruitId()+") ");
				sbQuery.append(") group by effective_id");
				pst = con.prepareStatement(sbQuery.toString());
				rst = pst.executeQuery();			
				List<String> deniedList=new ArrayList<String>();			
				while(rst.next()) {
					if(!deniedList.contains(rst.getString("effective_id"))) {
						deniedList.add(rst.getString("effective_id"));
					}
				}
				rst.close();
				pst.close();
				
				sbQuery=new StringBuilder();
				sbQuery.append("select candi_application_deatils_id from candidate_application_details where recruitment_id in ("+getRecruitId()+") and application_status=-1 ");
				pst = con.prepareStatement(sbQuery.toString());
				rst = pst.executeQuery();			
				while(rst.next()) {
					if(!deniedList.contains(rst.getString("candi_application_deatils_id"))) {
						deniedList.add(rst.getString("candi_application_deatils_id"));
					}
				}
				rst.close();
				pst.close();
				
				sbQuery=new StringBuilder();
				sbQuery.append("select effective_id,is_approved from work_flow_details where is_approved=2 and member_type=3 and effective_type='"+WORK_FLOW_RESUME_SHORTLIST+"' and " +
					"effective_id in (select candi_application_deatils_id from candidate_application_details where recruitment_id in ("+getRecruitId()+") ");
				sbQuery.append(") group by effective_id,is_approved");
				pst = con.prepareStatement(sbQuery.toString());
				rst = pst.executeQuery();			
				Map<String, String> hmAnyOneApproval = new HashMap<String, String>();
				while(rst.next()) {
					hmAnyOneApproval.put(rst.getString("effective_id"), rst.getString("is_approved"));
				}
				rst.close();
				pst.close();
						
				sbQuery=new StringBuilder();
				sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where effective_type='"+WORK_FLOW_RESUME_SHORTLIST+"' and " +
					"effective_id in (select candi_application_deatils_id from candidate_application_details where recruitment_id in ("+getRecruitId()+") ");
				if(strUserType != null && strUserType.equals(ADMIN)) {
					sbQuery.append(") and (user_type_id=? or user_type_id=?) ");
				} else if (strUserType != null && strUserType.equals(MANAGER) && strBaseUserType != null && (strBaseUserType.equals(HOD) || strBaseUserType.equals(CEO))) {
					sbQuery.append(") and (user_type_id=? or user_type_id=?) ");
				} else {
					sbQuery.append(") and user_type_id=? ");
				}
				sbQuery.append(" order by effective_id,member_position");
//				sbQuery.append(") and user_type_id=? order by effective_id,member_position");
				pst = con.prepareStatement(sbQuery.toString());
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
					pst.setInt(1, uF.parseToInt(strBaseUserTypeId));
				} else {
					pst.setInt(1, uF.parseToInt(strUserTypeId));
				}
				if(strUserType != null && strUserType.equals(ADMIN)) {
					pst.setInt(2, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
				} else if (strUserType != null && strUserType.equals(MANAGER) && strBaseUserType != null && (strBaseUserType.equals(HOD) || strBaseUserType.equals(CEO))) {
					pst.setInt(2, uF.parseToInt(hmUserTypeIdMap.get(strBaseUserType)));
				}
//				System.out.println("pst ===>> " + pst);
				rst = pst.executeQuery();
				Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();
				Map<String, List<String>> hmCheckEmpUserType = new HashMap<String, List<String>>();
				while(rst.next()) {
					List<String> checkEmpList = hmCheckEmp.get(rst.getString("effective_id"));
					if(checkEmpList == null)checkEmpList = new ArrayList<String>();				
					checkEmpList.add(rst.getString("emp_id"));
					
					List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rst.getString("effective_id")+"_"+rst.getString("emp_id"));
					if(checkEmpUserTypeList == null)checkEmpUserTypeList = new ArrayList<String>();				
					checkEmpUserTypeList.add(rst.getString("user_type_id"));
					
					hmCheckEmp.put(rst.getString("effective_id"), checkEmpList);
					hmCheckEmpUserType.put(rst.getString("effective_id")+"_"+rst.getString("emp_id"), checkEmpUserTypeList);
				}
				rst.close();
				pst.close();
				
				
				
				
				
				
				Map<String, String> hmJobStatus = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select job_code,recruitment_id,custum_designation,close_job_status from recruitment_details where job_approval_status=1 ");
				if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(RECRUITER))) {
					sbQuery.append(" and (added_by = "+ uF.parseToInt(strSessionEmpId) +" or hiring_manager like '%,"+strSessionEmpId+",%' )");
				}
				if(getRecruitId() != null && !getRecruitId().equals("")) {
					sbQuery.append(" and recruitment_id in ("+getRecruitId()+") ");
				}
				sbQuery.append(" order by close_job_status,job_approval_date desc,recruitment_id desc");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("====>pst"+pst);
				rst = pst.executeQuery();
				while (rst.next()) {
					hmJobStatus.put(rst.getString("recruitment_id"), uF.parseToBoolean(rst.getString("close_job_status"))+"");
				}
				rst.close();
				pst.close();
				
				Map<String, String> hmPanelInterviewDates = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select * from candidate_interview_panel where candidate_id > 0 ");
				if(getRecruitId() != null && !getRecruitId().equals("")) {
					sbQuery.append(" and recruitment_id in ("+getRecruitId()+") ");
				}
				pst = con.prepareStatement(sbQuery.toString());
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> "+ new Date());
				while (rst.next()) {
					StringBuilder interviewDateTime= new StringBuilder() ;
					interviewDateTime.append("Date: "+uF.getDateFormat(rst.getString("interview_date"), DBDATE, DATE_FORMAT));
					if(rst.getString("interview_time") != null){
						interviewDateTime.append(" Time: "+rst.getString("interview_time").substring(0, 5));
					}else {
						interviewDateTime.append(" Time: "+uF.showData(rst.getString("interview_time"),"-"));
					}
						hmPanelInterviewDates.put(rst.getString("recruitment_id")+"_"+rst.getString("panel_round_id")+"_"+rst.getString("candidate_id"), interviewDateTime.toString());
				}
				rst.close();
				pst.close();
				
	//			System.out.println("hmPanelInterviewDates ===> "+hmPanelInterviewDates);
				request.setAttribute("hmPanelInterviewDates", hmPanelInterviewDates);
				
				Map<String, String> hmCandiRejectFromRound = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				//====start parvez on 07-07-2021=====
//				sbQuery.append("select * from candidate_interview_panel where candidate_id > 0 and status=-1");
				sbQuery.append("select * from candidate_interview_panel cip, candidate_application_details cad where cip.candidate_id > 0 and cip.status=-1");
				if(getRecruitId() != null && !getRecruitId().equals("")) {
//					sbQuery.append(" and recruitment_id in ("+getRecruitId()+") ");
					sbQuery.append(" and cip.recruitment_id in ("+getRecruitId()+") and cad.candidate_final_status!=1 and cip.candidate_id=cad.candidate_id ");
				}
				//=====end parvez on 07-07-2021=====
				pst = con.prepareStatement(sbQuery.toString());
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> "+ new Date());
				while (rst.next()) {
					hmCandiRejectFromRound.put(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"), rst.getString("status"));
				}
				
				Map<String, String> hmClearRoundCnt = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select count (distinct(panel_round_id)) as count,recruitment_id,candidate_id from candidate_interview_panel where status = 1");
				if(getRecruitId() != null && !getRecruitId().equals("")) {
					sbQuery.append(" and recruitment_id in ("+getRecruitId()+") ");
				}
				sbQuery.append(" group by recruitment_id,candidate_id");
				pst = con.prepareStatement(sbQuery.toString());
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> "+ new Date());
				while (rst.next()) {
					hmClearRoundCnt.put(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"), rst.getString("count"));
				}
				rst.close();
				pst.close();
				
	//			System.out.println("hmClearRoundCnt ===> "+hmClearRoundCnt);
				
				Map<String, String> hmRoundCnt = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select count (distinct(round_id))as count,recruitment_id from panel_interview_details where recruitment_id > 0 ");
				if(getRecruitId() != null && !getRecruitId().equals("")) {
					sbQuery.append(" and recruitment_id in ("+getRecruitId()+") ");
				}
				sbQuery.append(" group by recruitment_id");
				pst = con.prepareStatement(sbQuery.toString());
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> "+ new Date());
				while (rst.next()) {
					hmRoundCnt.put(rst.getString("recruitment_id"), rst.getString("count"));
				}
				rst.close();
				pst.close();
				
	//			System.out.println("hmRoundCnt ===> "+hmRoundCnt);
				
				Map<String, List<String>> hmRoundIdsRecruitwise = new HashMap<String, List<String>>();
				List<String> roundIdsRecruitwiseList = new ArrayList<String>(); 
				sbQuery = new StringBuilder();
				sbQuery.append("select distinct(round_id),recruitment_id,candidate_id from panel_interview_details where recruitment_id > 0 ");
				if(getRecruitId() != null && !getRecruitId().equals("")) {
					sbQuery.append(" and recruitment_id in ("+getRecruitId()+") ");
				}
				sbQuery.append(" order by round_id");
				pst = con.prepareStatement(sbQuery.toString());
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> "+ new Date());
				while (rst.next()) {
					roundIdsRecruitwiseList = hmRoundIdsRecruitwise.get(rst.getString("recruitment_id"));
					if(roundIdsRecruitwiseList == null)roundIdsRecruitwiseList = new ArrayList<String>();
					roundIdsRecruitwiseList.add(rst.getString("round_id"));
					hmRoundIdsRecruitwise.put(rst.getString("recruitment_id"), roundIdsRecruitwiseList);
				}
				rst.close();
				pst.close();
				
	//			System.out.println("hmRoundIdsRecruitwise ===> "+hmRoundIdsRecruitwise);
				request.setAttribute("hmRoundIdsRecruitwise", hmRoundIdsRecruitwise);
				
				Map<String, List<String>> hmpanelIDS = new HashMap<String, List<String>>();
				Map<String, List<String>> hmpanelIDSRAndRwise = new HashMap<String, List<String>>();
				List<String> panelEmpIDList = new ArrayList<String>(); 
				List<String> panelEmpIDRAndRwiseList = new ArrayList<String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select recruitment_id,round_id,panel_emp_id from panel_interview_details where recruitment_id > 0 ");
				if(getRecruitId() != null && !getRecruitId().equals("")) {
					sbQuery.append(" and recruitment_id in ("+getRecruitId()+") ");
				}
				pst = con.prepareStatement(sbQuery.toString());
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> "+ new Date());
				while (rst.next()) {
					panelEmpIDList = hmpanelIDS.get(rst.getString("recruitment_id"));
					if(panelEmpIDList == null)panelEmpIDList = new ArrayList<String>();
					if(!panelEmpIDList.contains(rst.getString("panel_emp_id"))) {
						panelEmpIDList.add(rst.getString("panel_emp_id"));
					}
					hmpanelIDS.put(rst.getString("recruitment_id"), panelEmpIDList);
					
					panelEmpIDRAndRwiseList = hmpanelIDSRAndRwise.get(rst.getString("recruitment_id")+"_"+rst.getString("round_id"));
					if(panelEmpIDRAndRwiseList == null)panelEmpIDRAndRwiseList = new ArrayList<String>();
					panelEmpIDRAndRwiseList.add(rst.getString("panel_emp_id"));
					hmpanelIDSRAndRwise.put(rst.getString("recruitment_id")+"_"+rst.getString("round_id"), panelEmpIDRAndRwiseList);
				}
				rst.close();
				pst.close();
				
	
				Map<String, String> hmAssessmentName = CF.getAssessmentNameMap(con, uF);
	//			Map<String, String> hmpanelName = new HashMap<String, String>();
				Map<String, String> hmpanelName1 = new HashMap<String, String>();
				Map<String, String> hmpanelNameRAndRwise = new HashMap<String, String>();
				List<String> panelEmpIDList1 = new ArrayList<String>(); 
				List<String> panelEmpIDList1RAndRwise = new ArrayList<String>();
				Map<String, String> hmRoundAssessment = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select recruitment_id,round_id,panel_emp_id,assessment_id from panel_interview_details where recruitment_id > 0 ");
				if(getRecruitId() != null && !getRecruitId().equals("")) {
					sbQuery.append(" and recruitment_id in ("+getRecruitId()+") ");
				}
				pst = con.prepareStatement(sbQuery.toString());
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> "+ new Date());
				while (rst.next()) {
					boolean closeFlag = false;
					if(uF.parseToBoolean(hmJobStatus.get(rst.getString("recruitment_id")))){ 
						closeFlag = true;
					}
					panelEmpIDList1 = hmpanelIDS.get(rst.getString("recruitment_id"));
					String panelEmpNames = uF.showData(getAppendDataList(con, panelEmpIDList1,closeFlag), "");
	//				hmpanelName.put(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"), panelEmpNames);
					hmpanelName1.put(rst.getString("recruitment_id"), panelEmpNames);
					
					panelEmpIDList1RAndRwise = hmpanelIDSRAndRwise.get(rst.getString("recruitment_id")+"_"+rst.getString("round_id"));
					String panelEmpNamesRAndRwise = uF.showData(getAppendDataList1(con, panelEmpIDList1RAndRwise,closeFlag), "");
					hmpanelNameRAndRwise.put(rst.getString("recruitment_id")+"_"+rst.getString("round_id"), panelEmpNamesRAndRwise);
					
					if(uF.parseToInt(rst.getString("assessment_id")) > 0) {
						hmRoundAssessment.put(rst.getString("recruitment_id")+"_"+rst.getString("round_id")+"_NAME", hmAssessmentName.get(rst.getString("assessment_id")));
						hmRoundAssessment.put(rst.getString("recruitment_id")+"_"+rst.getString("round_id")+"_ID", rst.getString("assessment_id"));
					}
				}
				rst.close();
				pst.close();
	
				request.setAttribute("hmRoundAssessment", hmRoundAssessment);
				request.setAttribute("hmpanelname1", hmpanelName1);
				request.setAttribute("hmpanelNameRAndRwise", hmpanelNameRAndRwise);
				
				Map<String, Map<String, String>> hmshortlistedname = new HashMap<String, Map<String, String>>();
				Map<String, String> hmCandiShortlistStatus = new HashMap<String, String>();
				Map<String, Map<String, String>> hmFinalisedName = new HashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmCandiImage = new HashMap<String, Map<String, String>>();
				Map<String, String> hmSelectCount = new LinkedHashMap<String, String>();
				Map<String, String> hmFinalCount = new LinkedHashMap<String, String>();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select candi_application_deatils_id,wfd.user_type_id as user_type from candidate_application_details cad, work_flow_details wfd " +
					" where cad.candi_application_deatils_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_RESUME_SHORTLIST+"' and cad.application_status>=1 " +
					" and not cad.candidate_final_status=-1 ");
				if(getRecruitId() != null && !getRecruitId().equals("")) {
					sbQuery.append(" and cad.recruitment_id in ("+getRecruitId()+") ");
				}
				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if(strUserType != null && strUserType.equals(ADMIN)) {
					sbQuery.append(" and ( wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER))+") ");
				} else if (strUserType != null && strUserType.equals(MANAGER) && strBaseUserType != null && (strBaseUserType.equals(HOD) || strBaseUserType.equals(CEO))) {
					sbQuery.append(" and ( wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+") ");
				} else {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
				pst = con.prepareStatement(sbQuery.toString());
				rst = pst.executeQuery();
//				System.out.println("pst ===>> " + pst);
				Map<String, List<String>> hmCandiAppWorkflowUsertypeId = new HashMap<String, List<String>>();
				while (rst.next()) {
					List<String> alInner = hmCandiAppWorkflowUsertypeId.get(rst.getString("candi_application_deatils_id"));
					if(alInner==null)alInner = new ArrayList<String>();
					alInner.add(rst.getString("user_type"));
					hmCandiAppWorkflowUsertypeId.put(rst.getString("candi_application_deatils_id"), alInner);
				}
				rst.close();
				pst.close();
//				System.out.println("hmCandiAppWorkflowUsertypeId ===>> " + hmCandiAppWorkflowUsertypeId);
				
				
				sbQuery = new StringBuilder();
				sbQuery.append("select cad.recruitment_id,candi_application_deatils_id,emp_fname,emp_mname,emp_lname,cad.job_code,emp_per_id,cad.candidate_final_status, " +
					"emp_image,cad.application_status from candidate_personal_details cpd, candidate_application_details cad where cpd.emp_per_id = cad.candidate_id and " +
					" cad.application_status>=1 and not cad.candidate_final_status=-1 ");
				if(getRecruitId() != null && !getRecruitId().equals("")) {
					sbQuery.append(" and cad.recruitment_id in ("+getRecruitId()+") ");
				}
				/*if(strUserType != null && strUserType.equals(RECRUITER)) {
					sbQuery.append(" and cad.added_by = "+uF.parseToInt(strSessionEmpId)+" ");
				} LOGIC remove on request of XANADU */ 
				
				pst = con.prepareStatement(sbQuery.toString());
				rst = pst.executeQuery();
//				System.out.println("pst ===>> " + pst);
	//			System.out.println("new Date ===> "+ new Date());
				int selectCnt=0, finalCnt=0;
				List<String> alCandidate = new ArrayList<String>();
				while (rst.next()) {
	
					Map<String, String> hmInnerImage = hmCandiImage.get(rst.getString("recruitment_id"));
					
					if (hmInnerImage == null)hmInnerImage = new HashMap<String, String>();
					hmInnerImage.put(rst.getString("emp_per_id"), rst.getString("emp_image"));
					
					hmCandiImage.put(rst.getString("recruitment_id"), hmInnerImage);
					
					Map<String, String> hmInner = null;
					if (rst.getInt("candidate_final_status") == 0 && (hmCandiRejectFromRound == null || 
						hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")) == null ||
						!hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")).equals("-1"))
						&& hmClearRoundCnt != null && hmRoundCnt != null && 
						hmClearRoundCnt.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")) != null && hmRoundCnt.get(rst.getString("recruitment_id")) != null &&
							hmClearRoundCnt.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")).equals(hmRoundCnt.get(rst.getString("recruitment_id")))){
							hmInner = hmFinalisedName.get(rst.getString("recruitment_id"));
						
					}else if (rst.getInt("candidate_final_status") == 0 && (hmCandiRejectFromRound == null || 
							hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")) == null || !hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")).equals("-1"))){
						hmInner = hmshortlistedname.get(rst.getString("recruitment_id"));
					}
					if (hmInner == null)hmInner = new HashMap<String, String>();
	
					
					String strMiddleName = "";
					
					if(flagMiddleName) {
						if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
							strMiddleName = " "+rst.getString("emp_mname");
						}
					}
					
					String name = rst.getString("emp_fname") +strMiddleName+" " + rst.getString("emp_lname");
					hmInner.put(rst.getString("emp_per_id"), name);
					alCandidate.add(rst.getString("emp_per_id"));
	 
					if (rst.getInt("candidate_final_status") == 0 && (hmCandiRejectFromRound == null || hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")) == null || 
						!hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")).equals("-1")) && hmClearRoundCnt != null && hmRoundCnt != null &&
						hmClearRoundCnt.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")) != null && hmRoundCnt.get(rst.getString("recruitment_id")) != null && 
						hmClearRoundCnt.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")).equals(hmRoundCnt.get(rst.getString("recruitment_id")))) {
						hmFinalisedName.put(rst.getString("recruitment_id"), hmInner);
						finalCnt = uF.parseToInt(hmFinalCount.get(rst.getString("recruitment_id")));
						finalCnt++;
						hmFinalCount.put(rst.getString("recruitment_id"), String.valueOf(finalCnt));
						
					} else if (rst.getInt("candidate_final_status") == 0 && (hmCandiRejectFromRound == null || hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")) == null || !hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")).equals("-1"))){
						hmshortlistedname.put(rst.getString("recruitment_id"), hmInner);
						selectCnt = uF.parseToInt(hmSelectCount.get(rst.getString("recruitment_id")));
						selectCnt++;
						hmSelectCount.put(rst.getString("recruitment_id"), String.valueOf(selectCnt));
						
						
						
						String candiAppId = rst.getString("candi_application_deatils_id");
						List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(candiAppId+"_"+strSessionEmpId);
						if(checkEmpUserTypeList==null) checkEmpUserTypeList = new ArrayList<String>();
						boolean checkGHRInWorkflow = true;
						if(checkEmpUserTypeList.contains(hmUserTypeIdMap.get(HRMANAGER)) && !checkEmpUserTypeList.contains(hmUserTypeIdMap.get(ADMIN)) && strUserType != null && strUserType.equals(ADMIN)) {
							checkGHRInWorkflow = false;
						}
						
						List<String> alWorkflowUsertypeId = hmCandiAppWorkflowUsertypeId.get(candiAppId);
						String userType = "";
						if(alWorkflowUsertypeId !=null) {
							userType = alWorkflowUsertypeId.get(0);
						}
						StringBuilder sbStauts = new StringBuilder();
						StringBuilder sbApproveDeny = new StringBuilder();
						/*if(deniedList.contains(candiAppId)) {
							sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus" + candiAppId + "\" > ");
							sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
							sbStauts.append("</div>");
							hmCandiShortlistStatus.put(rst.getString("emp_per_id"), sbStauts.toString());
						
						} else if(rst.getInt("application_status")==2) {
							sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus" + candiAppId + "\" > ");
							sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
							sbStauts.append("</div>");
							hmCandiShortlistStatus.put(rst.getString("emp_per_id"), sbStauts.toString());
						
						} else 
						if(uF.parseToInt(hmAnyOneApproval.get(candiAppId))==2 && uF.parseToInt(hmAnyOneApproval.get(candiAppId))==rst.getInt("application_status")) {
							sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
							sbStauts.append("</div>");
							hmCandiShortlistStatus.put(rst.getString("emp_per_id"), sbStauts.toString());

						} else*/
						
						
//						System.out.println(candiAppId + " hmNextApproval.get(candiAppId) ===>> " + hmNextApproval.get(candiAppId));
//						System.out.println(userType + " -- hmMemNextApproval.get(candiAppId+_+userType) ===>> " + hmMemNextApproval.get(candiAppId+"_"+userType));
						
						if(rst.getInt("application_status")==2) {
							hmCandiShortlistStatus.put(rst.getString("emp_per_id"), "");
						
						} else if((strUserType != null && strUserType.equals(ADMIN)) || (uF.parseToInt(hmNextApproval.get(candiAppId))==uF.parseToInt(hmMemNextApproval.get(candiAppId+"_"+userType)) && uF.parseToInt(hmNextApproval.get(candiAppId))>0)) {
							
//							sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus" + candiAppId + "\" > ");
//							sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for approval\"></i>");
//							sbStauts.append("</div>");
							
							sbApproveDeny.append("<div style=\"float: left; margin-top: -10px; margin-left: 10px;\" id=\"myDivM" + candiAppId + "\" > ");
							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"shortlistOrRejectResume('2','"+ candiAppId+"','"+userType+"','"+getCurrUserType()+"');\" >" +
								"<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Shortlist Candidate\"></i></a> ");
							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"shortlistOrRejectResume('-1','" + candiAppId + "','"+userType+"','"+getCurrUserType()+"');\">" + " <i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Reject Candidate\"></i></a>  ");
							sbApproveDeny.append("</div>");
							
							hmCandiShortlistStatus.put(rst.getString("emp_per_id"), sbApproveDeny.toString());
//							System.out.println("hmCandiShortlistStatus ===>> 1");
						} else if(uF.parseToInt(hmNextApproval.get(candiAppId))<uF.parseToInt(hmMemNextApproval.get(candiAppId+"_"+userType)) || 
							(uF.parseToInt(hmNextApproval.get(candiAppId))==0 && uF.parseToInt(hmNextApproval.get(candiAppId))==uF.parseToInt(hmMemNextApproval.get(candiAppId+"_"+userType)))) {
								if(rst.getInt("application_status")==1) {
//									System.out.println("===>> in status 0");
									if(strUserType.equalsIgnoreCase(ADMIN) && uF.parseToInt(strUserTypeId) == uF.parseToInt(userType)) { //!checkEmpList.contains(strSessionEmpId) && 
//										sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus" + candiAppId + "\" > ");
//										sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Waiting for approval\"></i>");
//										sbStauts.append("</div>");
										
										sbApproveDeny.append("<div style=\"float: left; margin-top: -10px; margin-left: 10px;\" id=\"myDivM" + candiAppId + "\" > ");
										sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"shortlistOrRejectResume('2','"+ candiAppId+"','"+userType+"','"+getCurrUserType()+"');\" >" +
											"<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Shortlist Candidate\"></i></a> ");
										sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"shortlistOrRejectResume('-1','" + candiAppId + "','"+userType+"','"+getCurrUserType()+"');\">" + " <i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Reject Candidate\"></i></a>  ");
										sbApproveDeny.append("</div>");
										hmCandiShortlistStatus.put(rst.getString("emp_per_id"), sbApproveDeny.toString());
//										System.out.println("hmCandiShortlistStatus ===>> 2");
									} else {
										/*sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus" + candiAppId + "\" > ");
										sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" title=\"Waiting for workflow\" ></i>");
										sbStauts.append("</div>");*/
										
//										****************** Workflow *******************************
										sbApproveDeny.append("<div style=\"float:right;\" id=\"myDivM" + candiAppId + "\" > ");
										sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+candiAppId+"');\" style=\"margin-left: 10px;\">Workflow Status</a>");
										if(!checkGHRInWorkflow) {
											sbApproveDeny.append("&nbsp;|&nbsp;<a href=\"javascript:void(0)\" onclick=\"shortlistOrRejectResume('2','"+ candiAppId+"','','"+getCurrUserType()+"');\" >" +
												"<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Shortlist Candidate ("+ADMIN+")\"></i></a> ");
											sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"shortlistOrRejectResume('-1','" + candiAppId + "','1','"+getCurrUserType()+"');\">" + " <i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Reject Candidate ("+ADMIN+")\"></i></a>  ");
										}
										sbApproveDeny.append("</div>");
//										****************** Workflow *******************************
										hmCandiShortlistStatus.put(rst.getString("emp_per_id"), sbApproveDeny.toString());
//										System.out.println("hmCandiShortlistStatus ===>> 3");
									}
								}
								
						} else {
							/*sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus" + candiAppId + "\" > ");
							sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\"  title=\"Waiting for workflow\" ></i> ");
							sbStauts.append("</div>");*/
							
//							****************** Workflow *******************************
							sbApproveDeny.append("<div style=\"float:right;\" id=\"myDivM" + candiAppId + "\" > ");
							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+candiAppId+"');\" style=\"margin-left: 10px;\">Workflow Status</a>");
							sbApproveDeny.append("</div>");
//							****************** Workflow *******************************
							hmCandiShortlistStatus.put(rst.getString("emp_per_id"), sbApproveDeny.toString());
//							System.out.println("hmCandiShortlistStatus ===>> 4");
						}
					}
				}
				rst.close();
				pst.close();
				
				
				Map<String, Map<String, String>> hmRejectedName = new HashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmRejectCandiImage = new HashMap<String, Map<String, String>>();
				Map<String, String> hmRejectCount = new LinkedHashMap<String, String>();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select cad.recruitment_id,emp_fname,emp_mname,emp_lname,emp_mname,cad.job_code,emp_per_id,cad.candidate_final_status, emp_image, " +
					" cad.application_status from candidate_personal_details cpd, candidate_application_details cad where " +
					" cpd.emp_per_id = cad.candidate_id ");
				if(getRecruitId() != null && !getRecruitId().equals("")) {
					sbQuery.append(" and cad.recruitment_id in ("+getRecruitId()+") ");
				}
				pst = con.prepareStatement(sbQuery.toString());
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> "+ new Date());
				int rejectCnt=0;
				while (rst.next()) {
					if(rst.getString("application_status").equals("-1") || rst.getString("candidate_final_status").equals("-1")
						|| (hmCandiRejectFromRound != null && hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")) != null && hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")).equals("-1"))){
					Map<String, String> hmInnerImage = hmRejectCandiImage.get(rst.getString("recruitment_id"));
					if (hmInnerImage == null)hmInnerImage = new HashMap<String, String>();
					hmInnerImage.put(rst.getString("emp_per_id"), rst.getString("emp_image"));
					hmRejectCandiImage.put(rst.getString("recruitment_id"), hmInnerImage);
					
					Map<String, String> hmInner = null;
						hmInner = hmRejectedName.get(rst.getString("recruitment_id")+"_CNAME");
					if (hmInner == null)hmInner = new HashMap<String, String>();
					

					String strMiddleName = "";
					
					if(flagMiddleName) {
						if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
							strMiddleName = " "+rst.getString("emp_mname");
						}
					}
					
					String name = rst.getString("emp_fname") +strMiddleName+ " " + rst.getString("emp_lname");
					hmInner.put(rst.getString("emp_per_id"), name);
					
					alCandidate.add(rst.getString("emp_per_id"));
					
					Map<String, String> hmInnerStauts = null;
						hmInnerStauts = hmRejectedName.get(rst.getString("recruitment_id")+"_CSTATUS");
					if (hmInnerStauts == null)hmInnerStauts = new HashMap<String, String>();
					String stauts = null;
					if(hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id")) != null){
						stauts = hmCandiRejectFromRound.get(rst.getString("recruitment_id")+"_"+rst.getString("emp_per_id"));
					}else{
						stauts = rst.getString("candidate_final_status");
					}
					hmInnerStauts.put(rst.getString("emp_per_id"), stauts);
					
					
					rejectCnt = uF.parseToInt(hmRejectCount.get(rst.getString("recruitment_id")));
					rejectCnt++;
					hmRejectCount.put(rst.getString("recruitment_id"), String.valueOf(rejectCnt));
					
					hmRejectedName.put(rst.getString("recruitment_id")+"_CNAME", hmInner);
					hmRejectedName.put(rst.getString("recruitment_id")+"_CSTATUS", hmInnerStauts);
					}
				}
				rst.close();
				pst.close();
				
				
				Map<String, String> hmCandidateExperience = new HashMap<String, String>();
				Map<String, String> hmCandidateSkill = new HashMap<String, String>();
				Map<String, String> hmCandidateEducation = new HashMap<String, String>();
				
				if(alCandidate!=null && alCandidate.size()>0){
					Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
					if(hmEmpCodeDesig == null) hmEmpCodeDesig = new HashMap<String, String>();
					
					pst = con.prepareStatement("select * from candidate_personal_details where candididate_emp_id > 0 and emp_per_id in ("+StringUtils.join(alCandidate.toArray(),",")+")");
					rst = pst.executeQuery();
					Map<String, String> hmCandToEmp = new HashMap<String, String>();
					while(rst.next()){
						

						String strMiddleName = "";
						
						if(flagMiddleName) {
							if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
								strMiddleName = " "+rst.getString("emp_mname");
							}
						}
						
						hmCandToEmp.put(rst.getString("emp_per_id"), rst.getString("emp_fname")+strMiddleName+" "+rst.getString("emp_lname")+" is selected for "+uF.showData(hmEmpCodeDesig.get(rst.getString("candididate_emp_id")), "")+" already!");
					}
					rst.close();
					pst.close();
					request.setAttribute("hmCandToEmp", hmCandToEmp);
	//			}
				
				
				/* LOGIC FOR MULTIPLE EXPERIENCE ****************************** */
					String strCandidateIdOld = null;
					String strCandidateIdNew = null;
					//====start parvez===
					StringBuilder strCandidateIdNew1 = new StringBuilder();
					//====end parvez====
					int noyear = 0, nomonth = 0, nodays = 0;
					
					sbQuery = new StringBuilder();
					sbQuery.append("select to_date,from_date,emp_per_id from candidate_prev_employment cpe join candidate_personal_details cpd " +
							" on(cpd.emp_per_id=cpe.emp_id) where emp_per_id > 0 and emp_per_id in ("+StringUtils.join(alCandidate.toArray(),",")+") order by emp_per_id");
					pst = con.prepareStatement(sbQuery.toString());
					rst = pst.executeQuery();
		//			System.out.println("new Date ===> "+ new Date());
					while (rst.next()) {
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
							hmCandidateExperience.put(rst.getString("emp_per_id"), "" + noyear + " Year " + nomonth + " months ");
							strCandidateIdOld = strCandidateIdNew;
							//====start parvez====
					    	if(strCandidateIdNew1.length()==0){
					    		strCandidateIdNew1.append(strCandidateIdNew);
					    	}else{
					    		strCandidateIdNew1.append(","+strCandidateIdNew);
					    	}
					    	//====end parvez====
						}
					}
					rst.close();
					pst.close();
					
					
					//=====start parvez===
					if(strCandidateIdNew1.length()==0){
						pst = con.prepareStatement("select * from candidate_personal_details where emp_per_id > 0 and emp_per_id in ("+StringUtils.join(alCandidate.toArray(),",")+") order by emp_per_id");
						rst=pst.executeQuery();
						while(rst.next()){
							hmCandidateExperience.put(rst.getString("emp_per_id"), rst.getString("total_experience"));
						}
					}else{
						pst = con.prepareStatement("select * from candidate_personal_details where emp_per_id > 0 and emp_per_id in ("+StringUtils.join(alCandidate.toArray(),",")+") and emp_per_id not in ( "+strCandidateIdNew1+") order by emp_per_id");
						rst=pst.executeQuery();
						while(rst.next()){
							hmCandidateExperience.put(rst.getString("emp_per_id"), rst.getString("total_experience"));
						}
					}
					//====end parvez====
	//			}
				
				// for skill multiple***********
	
					Map<String, String> hmSkillsName = CF.getSkillNameMap(con);
					sbQuery = new StringBuilder();
					sbQuery.append("select emp_id,skill_id from candidate_skills_description csd join candidate_personal_details cpd on(cpd.emp_per_id=csd.emp_id) " +
							" where emp_per_id > 0 and emp_per_id in ("+StringUtils.join(alCandidate.toArray(),",")+") order by emp_per_id");
					pst = con.prepareStatement(sbQuery.toString());// where application_status=1 and not candidate_final_status=-1
					rst = pst.executeQuery();
		//			System.out.println("new Date ===> "+ new Date());
					while (rst.next()) {
		
						String temp = hmCandidateSkill.get(rst.getString("emp_id"));
						if (temp != null)
							temp += "," + hmSkillsName.get(rst.getString("skill_id"));
						else
							temp = hmSkillsName.get(rst.getString("skill_id"));
		
						hmCandidateSkill.put(rst.getString("emp_id"), temp);
		
					}
					rst.close();
					pst.close();
					
					// for education multiple***********
		
					Map<String, String> hmDegreeName = CF.getDegreeNameMap(con);
					sbQuery = new StringBuilder();
					sbQuery.append("select emp_id,education_id from candidate_education_details ced join candidate_personal_details cpd on(cpd.emp_per_id=ced.emp_id) " +
							" where emp_per_id > 0 and emp_per_id in ("+StringUtils.join(alCandidate.toArray(),",")+") order by emp_per_id");
					pst = con.prepareStatement(sbQuery.toString()); // where application_status=1 and not candidate_final_status=-1
					rst = pst.executeQuery();
		//			System.out.println("new Date ===> "+ new Date());
					while (rst.next()) {
						String temp = hmCandidateEducation.get(rst.getString("emp_id"));
						if (temp != null){
							temp += "," + hmDegreeName.get(rst.getString("education_id"));
						}else{
							temp = hmDegreeName.get(rst.getString("education_id"));
						}
						hmCandidateEducation.put(rst.getString("emp_id"), temp);
		
					}
					rst.close();
					pst.close();
				}
				
//				System.out.println("hmCandiShortlistStatus ===>> " + hmCandiShortlistStatus);
				request.setAttribute("hmCandiShortlistStatus", hmCandiShortlistStatus);
				request.setAttribute("hmshortlistedname", hmshortlistedname);
				request.setAttribute("hmFinalisedName", hmFinalisedName);
				request.setAttribute("hmCandiImage", hmCandiImage);
				request.setAttribute("hmRejectedName", hmRejectedName);
				request.setAttribute("hmRejectCandiImage", hmRejectCandiImage);
				request.setAttribute("hmSelectCount", hmSelectCount);
				request.setAttribute("hmFinalCount", hmFinalCount);
				request.setAttribute("hmRejectCount", hmRejectCount);
				
	
				request.setAttribute("hmCandidateEducation", hmCandidateEducation);
				request.setAttribute("hmCandidateSkill", hmCandidateSkill);
				request.setAttribute("hmCandidateExperience", hmCandidateExperience);
	
				Map<String, Map<String, String>> hmPanelRatingAndComments = new HashMap<String, Map<String, String>>();
				sbQuery = new StringBuilder();
				sbQuery.append("select cip.recruitment_id,comments,panel_rating,panel_round_id,cip.candidate_id,panel_user_id from candidate_interview_panel cip where candidate_id>0 ");
				if(getRecruitId() != null && !getRecruitId().equals("")) {
					sbQuery.append(" and recruitment_id in ("+getRecruitId()+") ");
				}
				pst = con.prepareStatement(sbQuery.toString());
				rst = pst.executeQuery();
	//			System.out.println("pst ===> "+ pst);
				Map<String, String> hmInner = new HashMap<String, String>();
				while (rst.next()) {
					hmInner = hmPanelRatingAndComments.get(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"));
					if (hmInner == null) hmInner = new HashMap<String, String>();
					
					hmInner.put(rst.getString("panel_round_id")+"_"+rst.getString("panel_user_id")+"_RATING", uF.showData(rst.getString("panel_rating"), ""));
					hmInner.put(rst.getString("panel_round_id")+"_"+rst.getString("panel_user_id")+"_COMMENT", uF.showData(rst.getString("comments"), ""));
					hmPanelRatingAndComments.put(rst.getString("recruitment_id")+"_"+rst.getString("candidate_id"), hmInner);
				}
				rst.close();
				pst.close();
				
				Map<String, Map<String, Map<String, String>>> hmRecruitWiseRoundId = new HashMap<String, Map<String, Map<String, String>>>();
				sbQuery = new StringBuilder();
				sbQuery.append("select * from panel_interview_details where recruitment_id >0 ");
				if(getRecruitId() != null && !getRecruitId().equals("")) {
					sbQuery.append(" and recruitment_id in ("+getRecruitId()+") ");
				}
				pst = con.prepareStatement(sbQuery.toString());
				ResultSet rs1 = pst.executeQuery();
				while(rs1.next()) {
					
					Map<String,Map<String,String>> hmRoundId = hmRecruitWiseRoundId.get(rs1.getString("recruitment_id"));  
					if(hmRoundId==null) hmRoundId =new HashMap<String, Map<String,String>>();
					
					if(rs1.getString("panel_emp_id") != null && !rs1.getString("panel_emp_id").equals("")) {
						Map<String,String> hmEmpId = hmRoundId.get(rs1.getString("round_id"));
						if(hmEmpId==null) hmEmpId =new HashMap<String, String>();
						
						hmEmpId.put(rs1.getString("panel_emp_id"), rs1.getString("panel_emp_id"));
						
						hmRoundId.put(rs1.getString("round_id"), hmEmpId);
					}
					hmRecruitWiseRoundId.put(rs1.getString("recruitment_id"), hmRoundId);
						
				}
				rs1.close();
				pst.close();
	
				request.setAttribute("hmPanelRatingAndComments", hmPanelRatingAndComments);
				request.setAttribute("hmRecruitWiseRoundId", hmRecruitWiseRoundId);
				/*request.setAttribute("hmCommentsHR", hmCommentsHR);*/
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private String getAppendDataList(Connection con, List<String> strIDList, boolean closeFlag) {
		StringBuilder sb = new StringBuilder();
//		EncryptionUtils encryptionUtils = new EncryptionUtils();//Created By Dattatray Date : 21-July-2021 Note : Encryption
		Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);

	//===start parvez date: 17-01-2022===
		UtilityFunctions uF = new UtilityFunctions();
		boolean isEnableHiringInCloseJd = CF.getFeatureManagementStatus(request, uF, F_ENABLE_HIRING_PROCEDURE_IN_CLOSED_JOB_REQUIREMENT);
	//===end parvez date: 17-01-2022===
		
		for (int i =0; strIDList != null && i<strIDList.size(); i++) {

			if(strIDList.get(i)!=null && !strIDList.get(i).equals("")){
				if(i==strIDList.size()-1){ 
					
			//===start parvez date: 17-01-2022===
//					if(closeFlag){
					if(closeFlag && !isEnableHiringInCloseJd){
			//===end parvez date: 17-01-2022===			
						sb.append(hmEmpName.get(strIDList.get(i).trim()));
					} else {
						//Created By Dattatray Date : 21-July-2021 Note :empId Encrypt encryptionUtils.encrypt(strIDList.get(i))
						sb.append("<a href=\"javascript: void(0)\" onclick=\"openPanelEmpProfilePopup('"+strIDList.get(i)+"');\">" + hmEmpName.get(strIDList.get(i).trim())+"</a>");
					}
				} else {	
				
			//===start parvez date: 17-01-2022===		
//					if(closeFlag){
					if(closeFlag && !isEnableHiringInCloseJd){
			//===start parvez date: 17-01-2022===			
						sb.append(hmEmpName.get(strIDList.get(i).trim())+", ");
					} else {
						//Created By Dattatray Date : 21-July-2021 Note : empId Encrypt
						sb.append("<a href=\"javascript: void(0)\" onclick=\"openPanelEmpProfilePopup('"+strIDList.get(i)+"');\">" + hmEmpName.get(strIDList.get(i).trim())+"</a>"+", ");
					}
				}
			}
		}

		return sb.toString();
	}

	
	private String getAppendDataList1(Connection con, List<String> strIDList, boolean closeFlag) {
		StringBuilder sb = null;
		
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);

			for (int i =0; strIDList != null && i<strIDList.size(); i++) {
				if(strIDList.get(i)!=null && !strIDList.get(i).trim().equals("")){
				 if(sb == null) {
					 sb = new StringBuilder();
					 sb.append(hmEmpName.get(strIDList.get(i).trim()));
				 } else {	
					sb.append(", "+hmEmpName.get(strIDList.get(i).trim()));
				 }
				}
		}
			if(sb == null) {
				 sb = new StringBuilder();
			}
		return sb.toString();
	}

	
	
//	private String getAppendData(String strID) {
//		StringBuilder sb = new StringBuilder();
//        
//		if (strID != null && !strID.equals("")) {
//			Map<String, String> hmEmpName = CF.getEmpNameMap(null, null);
//				String[] temp = strID.split(",");
//
//				for (int i =0; i < temp.length; i++) {
//	
//					if(temp[i]!=null && !temp[i].equals("")){
//					 if(i==temp.length-1){ 
//						 sb.append("<a href=\"MyProfile.action?empId="+temp[i]+"\">"+hmEmpName.get(temp[i].trim())+"</a>");
//					 }else{	
//						sb.append("<a href=\"MyProfile.action?empId="+temp[i]+"\">"+ hmEmpName.get(temp[i].trim())+"</a>"+", ");
//					 }
//					}
//			}
//		}
//		return sb.toString();
//	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

//	private Map<String, String> getPanelNameMap(Connection con, String recruitId) {
//
//		PreparedStatement pst = null;
//		ResultSet rst = null;
//		Map<String, String> panelnamemap = new LinkedHashMap<String, String>();
//		UtilityFunctions uF = new UtilityFunctions();
//		try {
//			String panelId = null;
//			pst = con.prepareStatement("select panel_employee_id from recruitment_details where recruitment_id=? ");
//			pst.setInt(1, uF.parseToInt(recruitId));
//			rst = pst.executeQuery();
////			System.out.println("new Date ===> "+ new Date());
//			if (rst.next()) {
//				panelId = rst.getString("panel_employee_id");
//			}
//			rst.close();
//			pst.close();
//			
//			if (panelId != null && !panelId.equals("")) {
//				Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
//				String[] temp = panelId.split(",");
//
//				for (int i = 1; i < temp.length; i++) {
//					panelnamemap.put(temp[i].trim(), hmEmpName.get(temp[i].trim()));
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return panelnamemap;
//	}

	String strDashboardRequest;

	public String getStrDashboardRequest() {
		return strDashboardRequest;
	}

	public void setStrDashboardRequest(String strDashboardRequest) {
		this.strDashboardRequest = strDashboardRequest;
	}

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

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	
}