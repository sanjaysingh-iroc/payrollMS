package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetSelectedCandidateList extends ActionSupport implements ServletRequestAware,IConstants {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	String recruitId;
	CommonFunctions CF = null;
	String type;
	String submit; 
	
	String selectedEmp;
	String chboxReject;
	String chboxShortlist;
	String resetCandi;
	public String execute() {
		
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("chboxReject === > "+chboxReject);
		System.out.println("chboxShortlist === > "+chboxShortlist);
		if (uF.parseToBoolean(getChboxShortlist())){
			//addCandidate(uF);
			candidateShortlist(uF);
		} else{
			//removeCandidate(uF);
		}
		if (uF.parseToBoolean(getChboxReject())){
			//addCandidate(uF);
			candidateReject(uF);
		}
		if (getResetCandi() != null && getResetCandi().equals("reset")){
			//addCandidate(uF);
			candidateReset(uF);
		}
		getSelectCandidateList(uF);
		getRejectCandidateList(uF);
		getCandidateList(uF);
		return LOAD;
	}



	private void candidateReset(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update candidate_application_details set application_status = 0,application_status_date=null, send_notification_status=0 where candidate_id = ? and recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(getSelectedEmp()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			pst.executeUpdate();
			pst.close();
			
			pst=con.prepareStatement("delete from candidate_activity_details where recruitment_id=? and activity_id=? and  candi_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, CANDI_ACTIVITY_APPLI_SHORTLIST_OR_REJECT_ID);
			pst.setInt(3, uF.parseToInt(getSelectedEmp()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("select * from candidate_application_details cad, recruitment_details rd where cad.candidate_id=? " +
				" and cad.recruitment_id=rd.recruitment_id and cad.recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getSelectedEmp()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
			String strCandiApplicationId = null;
			while (rs.next()) {
				strCandiApplicationId = rs.getString("candi_application_deatils_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("delete from work_flow_details where effective_id=? and effective_type=?");
			pst.setInt(1, uF.parseToInt(strCandiApplicationId));
			pst.setString(2, WORK_FLOW_RESUME_SHORTLIST);
			pst.executeUpdate();
			System.out.println("pst ===>> " + pst);
			pst.close();
		
	//		System.out.println("pst Reset ===> "+pst);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}



	private void candidateReject(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update candidate_application_details set application_status = -1,application_status_date=? where candidate_id = ? and recruitment_id = ?");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, uF.parseToInt(getSelectedEmp()));
			pst.setInt(3, uF.parseToInt(getRecruitId()));
			pst.executeUpdate();
			pst.close();
			
			pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			pst.setInt(2,uF.parseToInt(getSelectedEmp()));
	//		pst.setInt(3,uF.parseToInt(getPanelId()));
			pst.setString(3, "Application Rejected");
			pst.setInt(4,uF.parseToInt(strSessionEmpId));
			pst.setDate	(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setInt(6, CANDI_ACTIVITY_APPLI_SHORTLIST_OR_REJECT_ID);
			pst.execute();
			pst.close();
	//		System.out.println("pst Reject ===> "+pst);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void candidateShortlist(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);
			

			pst = con.prepareStatement("select * from candidate_application_details cad, recruitment_details rd where cad.candidate_id=? " +
				" and cad.recruitment_id=rd.recruitment_id and cad.recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getSelectedEmp()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
			System.out.println("pst ===>> " + pst);
			String strCandiApplicationId = null;
			String strWorkflowMember = null;
			String policy_id = null;
			while (rs.next()) {
				strCandiApplicationId = rs.getString("candi_application_deatils_id");
				policy_id = rs.getString("resume_workflow_policy_id");
				strWorkflowMember = rs.getString("resume_workflow_aligned_member");
			}
			rs.close();
			pst.close();
			System.out.println("strWorkflowMember ===>> " + strWorkflowMember);
			
			pst = con.prepareStatement("update candidate_application_details set application_status=?, application_status_date=? where candidate_id = ? and recruitment_id = ?");
			if(uF.parseToInt(policy_id)>0) {
				pst.setInt(1, 1);
			} else {
				pst.setInt(1, 2);
			}
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(3, uF.parseToInt(getSelectedEmp()));
			pst.setInt(4, uF.parseToInt(getRecruitId()));
			pst.executeUpdate();
			pst.close();
			
			pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			pst.setInt(2,uF.parseToInt(getSelectedEmp()));
	//		pst.setInt(3,uF.parseToInt(getPanelId()));
			pst.setString(3, "Application Shortlisted");
			pst.setInt(4,uF.parseToInt(strSessionEmpId));
			pst.setDate	(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setInt(6, CANDI_ACTIVITY_APPLI_SHORTLIST_OR_REJECT_ID);
			pst.execute();
			pst.close();
			
			
			List<String> alMemberList = new ArrayList<String>();
			if(strWorkflowMember!=null && strWorkflowMember.length()>0) {
				alMemberList = Arrays.asList(strWorkflowMember.split(":__:"));
			}
			
			Map<String, String> hmWorkflowData = new HashMap<String, String>();
			for(int i=0; alMemberList!=null && i<alMemberList.size(); i++) {
				String[] strTmp = alMemberList.get(i).split("::");
				if(strTmp.length>1) {
					hmWorkflowData.put(strTmp[0], strTmp[1]);
				}
			}
			
			System.out.println("hmWorkflowData ===>> " + hmWorkflowData);
			Map<String, String> hmEmpUserTypeId = CF.getEmployeeIdUserTypeIdMap(con);
			Map<String, String> hmUserType = CF.getUserTypeMap(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			
			pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
			" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
			pst.setInt(1, uF.parseToInt(policy_id));
			System.out.println("pst ===>> " + pst);
			rs=pst.executeQuery();
			Map<String,List<String>> hmMemberMap=new LinkedHashMap<String, List<String>>();
			while(rs.next()){
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("member_type"));
				innerList.add(rs.getString("member_id"));
				innerList.add(rs.getString("member_position"));
				innerList.add(rs.getString("work_flow_mem"));
				innerList.add(rs.getString("work_flow_member_id"));
				
				hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("delete from work_flow_details where effective_id=? and effective_type=?");
			pst.setInt(1, uF.parseToInt(strCandiApplicationId));
			pst.setString(2, WORK_FLOW_RESUME_SHORTLIST);
			pst.executeUpdate();
			pst.close();
			
			String strDomain = request.getServerName().split("\\.")[0];
			Iterator<String> it=hmMemberMap.keySet().iterator();
			while(it.hasNext()){
				String work_flow_member_id=it.next();
				List<String> innerList= hmMemberMap.get(work_flow_member_id);
				
				int memid=uF.parseToInt(innerList.get(1)); 
				String empid = hmWorkflowData.get(memid+"");
				System.out.println("empid ====>>> " +empid +" -- memid ====>>> "+memid);
				
				if(empid!=null && !empid.equals("")) {
					int userTypeId = memid;
					if(uF.parseToInt(innerList.get(0)) == 3) {
						userTypeId = uF.parseToInt(hmEmpUserTypeId.get(empid));
					}
					System.out.println("approval empid====>"+empid);
					pst = con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
						"work_flow_mem_id,is_approved,status,user_type_id) values(?,?,?,?, ?,?,?,?, ?)");
					pst.setInt(1, uF.parseToInt(empid));
					pst.setInt(2, uF.parseToInt(strCandiApplicationId));
					pst.setString(3, WORK_FLOW_RESUME_SHORTLIST);
					pst.setInt(4, uF.parseToInt(innerList.get(0)));
					pst.setInt(5, (int)uF.parseToDouble(innerList.get(2)));
					pst.setInt(6, uF.parseToInt(innerList.get(4)));
					pst.setInt(7, 0);
					pst.setInt(8, 0);
					pst.setInt(9, userTypeId);
					System.out.println("pst ===>> " + pst);
					pst.execute();
					pst.close();
					
					
					String alertData = "<div style=\"float: left;\"> Received a new Request for resume shortlisting from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. ["+hmUserType.get(userTypeId+"")+"] </div>";
					String strSubAction = "";
					String alertAction = "";
					if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD)) || userTypeId == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
						if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD))) {
							strSubAction = "&currUserType="+hmUserType.get(userTypeId+"");
						}
						alertAction = "RecruitmentDashboard.action?pType=WR&recruitId="+getRecruitId()+strSubAction;
					} else {
						alertAction = "RecruitmentDashboard.action?pType=WR&recruitId="+getRecruitId()+strSubAction;
					}
					
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(empid);
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(userTypeId+"");
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
				}
			}
			
			
	//		System.out.println("pst shortlist ===> "+pst);
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
//		System.out.println("new Date ===> " + new Date());
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
	
	return hmDegreeName;
}


private Map<String, List<String>> getCandiSkillsName(Connection con) {
	
	PreparedStatement pst = null;
	ResultSet rst = null;
	Map<String, List<String>> hmSkills = new HashMap<String, List<String>>();
	try {
		List<String> skillList = new ArrayList<String>();
		pst=con.prepareStatement("select emp_id,skill_id from candidate_skills_description");
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
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
//		System.out.println("new Date ===> " + new Date());
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


private Map<String, String> getDesigAttribute(UtilityFunctions uF){

	Connection con = null;
	PreparedStatement pst = null;
	Database db = new Database();
	ResultSet rst = null;
	db.setRequest(request);
	Map<String, String> hmDesigAttrib = new HashMap<String, String>();
	try {
		con=db.makeConnection(con);
		pst=con.prepareStatement("select * from desig_attribute");
		rst=pst.executeQuery();
	//	System.out.println("new Date ===> " + new Date());
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
	}finally{
		
		db.closeResultSet(rst);
		db.closeStatements(pst);
		db.closeConnection(con);
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
//	System.out.println("new Date ===> " + new Date());
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


private String calculateCandidateStarRating(Connection con, UtilityFunctions uF, List<String> skillsList, List<String> educationsList, List<String> totExpList, String candiGender) {
	
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
//		System.out.println("new Date ===> " + new Date());
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
		Map<String, String> hmDesigAttrib = getDesigAttribute(uF);
		List<String> desigSkillList = getListData(hmDesigAttrib.get(desigID+"_SKILLS"));
		List<String> desigEduList = getListData(hmDesigAttrib.get(desigID+"_EDUCATION"));
		String desigTotExp = hmDesigAttrib.get(desigID+"_TOTEXP");
		String desigRelExp = hmDesigAttrib.get(desigID+"_RELEXP");
		String desigExpWithus = hmDesigAttrib.get(desigID+"_EXPWITH_US");
		String desigGender = hmDesigAttrib.get(desigID+"_GENDER");
//		System.out.println("desigSkillList ===> "+desigSkillList);
//		System.out.println("desigEduList ===> "+ desigEduList);
//		System.out.println("desigTotExp ===> "+ desigTotExp);
//		System.out.println("desigGender ===> "+ desigGender);
		
		
//		System.out.println("recEduList ===> "+recEduList);
//		System.out.println("recSkillsList ===> "+ recSkillsList);
//		System.out.println("minExp ===> "+ minExp);
		
//		System.out.println("educationsList ===> "+educationsList);
//		System.out.println("skillsList ===> "+ skillsList);
//		System.out.println("totExpList ===> "+ totExpList);
		
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
//			for (int i = 0; i < recEduList.size(); i++) {
			double sumcandiExp=0;
				for (int j = 0; totExpList != null && !totExpList.isEmpty() && j < totExpList.size(); j++) {
//					System.out.println("totExpList "+j+" == "+totExpList.get(j));
					sumcandiExp += uF.parseToDouble(totExpList.get(j));
				}
//				System.out.println("sumcandiExp == "+sumcandiExp);
				
				if(uF.parseToDouble(minExp) <= sumcandiExp){
					expMarks = 100;
				}
				expCount=1;
//			}
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
//		System.out.println("desigGender == "+desigGender);
//		System.out.println("candiGender == "+candiGender);
		int genderMarks=0, genderCount=0;
		if(desigGender != null && !desigGender.equals("")){
			if(desigGender.equals(candiGender)){
				genderMarks = 100;
			}
			genderCount=1;
		}
		
//		System.out.println("skillMarks == " + skillMarks + " eduMarks == " +  eduMarks + " expMarks == " + expMarks + " genderMarks == " + genderMarks);
		int allMarks = skillMarks + eduMarks + expMarks + genderMarks;
		int allCount = skillCount + eduCount + expCount + genderCount;
	
		int avgMarks = allMarks / allCount;
//		System.out.println("avgMarks == "+avgMarks);
		double starrts = uF.parseToDouble(""+avgMarks) / 20;
//		strStars = uF.formatIntoOneDecimalWithOutComma(starrts);
		int intstars = (int) starrts;
		if(starrts>uF.parseToDouble(""+intstars)){
			strStars = uF.formatIntoOneDecimalWithOutComma(starrts);
		}else{
			strStars = intstars+"";
		}
//		System.out.println("strStars == "+strStars);
		
//		if(strStars.contains(".0")){
//			
//			strStars = intstars+"";
//			System.out.println("strStars if == "+strStars);
//		}
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
	db.setRequest(request);
	ResultSet rst = null;
	try {
		con=db.makeConnection(con);
		
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
		boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
		Map<String, List<String>> hmSkills = getCandiSkillsName(con);
		Map<String, List<String>> hmDegrees = getCandiDegreeName(con);
		Map<String, List<String>> hmTotExp = getCandiTotExp(con,uF);
		
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
		
		Map<String, String> hmCandiExp = new HashMap<String, String>();
		pst=con.prepareStatement("select emp_id,from_date,to_date from candidate_prev_employment");
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		while(rst.next()){
			String frmdt = rst.getString("from_date");
			String todt = rst.getString("to_date");
			String candidateExp = "";
			if(frmdt != null && todt != null){
				candidateExp = uF.getTimeDurationBetweenDates(frmdt, DBDATE, todt, DBDATE, CF, uF, request);
			}
			hmCandiExp.put(rst.getString("emp_id"), candidateExp);
		}
		rst.close();
		pst.close();
		
//		pst=con.prepareStatement("select emp_per_id,emp_fname,emp_lname,recruitment_id,emp_gender from candidate_personal_details cpd " +
//				"where cpd.candidate_joining_date is null and cpd.recruitment_id = ? and cpd.application_status = 0 " +
//				" and candidate_final_status = 0 and candidate_status = 0 order by emp_fname,emp_lname");
		pst=con.prepareStatement("select emp_per_id,emp_fname,emp_mname, emp_lname,cad.recruitment_id,emp_gender from candidate_personal_details cpd, " +
				"candidate_application_details cad where cpd.emp_per_id = cad.candidate_id and cad.candidate_joining_date is null and " +
				"cad.application_status = 0 and cad.candidate_final_status = 0 and cad.candidate_status = 0 and cad.recruitment_id = ?" +
				" order by emp_fname,emp_lname");
		pst.setInt(1, uF.parseToInt(getRecruitId()));
		rst=pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
		while(rst.next()){
			List<String> innerList = new ArrayList<String>();
			String candiID = rst.getString("emp_per_id");
			String candiGender = rst.getString("emp_gender");
			String strStars = calculateCandidateStarRating(con, uF, hmSkills.get(candiID), hmDegrees.get(candiID), hmTotExp.get(candiID), candiGender);
			innerList.add(rst.getString("emp_per_id"));
		
			String strEmpMName = "";
			if(flagMiddleName) {
				if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
					strEmpMName = " "+rst.getString("emp_mname");
				}
			}
		
			
			innerList.add(rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
			innerList.add(getAppendData(hmDegrees.get(rst.getString("emp_per_id"))));
			innerList.add(hmJobCode.get(rst.getString("recruitment_id")));
			innerList.add(getAppendData(hmSkills.get(rst.getString("emp_per_id"))));
			innerList.add(hmCandiExp.get(rst.getString("emp_per_id")));
			innerList.add(strStars);
			allCandidateList.add(innerList);
		}
		rst.close();
		pst.close();
		
		request.setAttribute("allCandidateList", allCandidateList);
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		
		db.closeResultSet(rst);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}
	

public String getAppendData(List<String> strID) {
	StringBuilder sb = new StringBuilder();
	if (strID != null) {
		for (int i = 0; i < strID.size(); i++) {
			if (i == 0) {
				sb.append(strID.get(i));
			} else {
				sb.append(", "+strID.get(i));
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
		ResultSet rst = null;
		db.setRequest(request);
		Map<String, String> hmCandiName = new HashMap<String, String>();
		Map<String, String> hmCandiProfile = new HashMap<String, String>();
		
		try {
			con=db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmNotificationStatus = new HashMap<String, String>();
			pst=con.prepareStatement("select candidate_id,send_notification_status from candidate_application_details where " +
			"recruitment_id=? and (application_status = 1 or application_status = 2) and candidate_final_status = 0 and candidate_status = 0");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				hmNotificationStatus.put(rst.getString("candidate_id"), rst.getString("send_notification_status"));
			}
			rst.close();
			pst.close();
			
			pst=con.prepareStatement("select emp_per_id,emp_fname,emp_mname,emp_lname,emp_image from candidate_personal_details cpd, " +
				"candidate_application_details cad where cpd.emp_per_id = cad.candidate_id and (cad.application_status = 1 or cad.application_status = 2) " +
				"and cad.candidate_final_status = 0 and cad.candidate_status = 0 and cad.recruitment_id=?");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			System.out.println("pst selected =====>> " + pst);
			String selectCandiIDs="";
			int cnt=0;
			while(rst.next()) {
				
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
//				System.out.println("rst.getString(emp_per_id) ===== > "+rst.getString("emp_per_id"));
			}
			rst.close();
			pst.close();
			
			System.out.println("selectCandiIDs ===== > "+selectCandiIDs);
			
			List<String> selectCandidateList=new ArrayList<String>();
			List<String> selectCandidateIds=new ArrayList<String>();
			if(selectCandiIDs !=null && !selectCandiIDs.equals("")){
				Set<String> candiSet = new HashSet<String>(Arrays.asList(selectCandiIDs.split(",")));
				Iterator<String> itr = candiSet.iterator();
				while (itr.hasNext()) {
					String candiId = (String) itr.next();
					if(candiId!=null && !candiId.equals("")){
//						System.out.println("hmNotificationStatus.get(candiId) ===> "+hmNotificationStatus.get(candiId));
//						if(hmNotificationStatus.get(candiId).equals("0")){
//							selectCandidateList.add("<img style=\"margin-right:5px\" width=\"18\" src=\"userImages/"+hmCandiProfile.get(candiId.trim())+"\">"+hmCandiName.get(candiId.trim())
//							+"<img border=\"0\" style=\"padding: 5px 5px 0pt;\" width=\"21\" src=\""+request.getContextPath()+"/images1/icons/news_icon.gif\"/>");
//						}else{
//							selectCandidateList.add("<img style=\"margin-right:5px\" width=\"18\" src=\"userImages/"+hmCandiProfile.get(candiId.trim())+"\">"+hmCandiName.get(candiId.trim()));
//						}
						if(hmNotificationStatus.get(candiId).equals("0")){
							selectCandidateList.add(""+hmCandiName.get(candiId.trim())
							+"<img border=\"0\" style=\"padding: 5px 5px 0pt;\" width=\"21\" src=\""+request.getContextPath()+"/images1/icons/news_icon.gif\"/>");
						}else{
							selectCandidateList.add(""+hmCandiName.get(candiId.trim()));
						}
						selectCandidateIds.add(candiId.trim());
					}
				}
			}else{
				selectCandidateList=null;
			}
			request.setAttribute("selectCandidateIds", selectCandidateIds);
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
		ResultSet rst = null;
		db.setRequest(request);
		Map<String, String> hmCandiName = new HashMap<String, String>();
		Map<String, String> hmCandiProfile = new HashMap<String, String>();
		try {
			con=db.makeConnection(con);
						
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst=con.prepareStatement("select emp_per_id,emp_fname,emp_mname,emp_lname,emp_image from candidate_personal_details cpd, " +
				"candidate_application_details cad where cpd.emp_per_id = cad.candidate_id and cad.application_status = -1 " +
				"and cad.candidate_final_status = 0 and cad.candidate_status = 0 and cad.recruitment_id=?");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("pst ===== > "+pst);
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
//				System.out.println("rst.getString(emp_per_id) ===== > "+rst.getString("emp_per_id"));
			}
			rst.close();
			pst.close();
			
//			System.out.println("selectCandiIDs ===== > "+selectCandiIDs);
			
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
	
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	public String getChboxReject() {
		return chboxReject;
	}

	public void setChboxReject(String chboxReject) {
		this.chboxReject = chboxReject;
	}

	public String getChboxShortlist() {
		return chboxShortlist;
	}

	public void setChboxShortlist(String chboxShortlist) {
		this.chboxShortlist = chboxShortlist;
	}

	public String getSelectedEmp() {
		return selectedEmp;
	}

	public void setSelectedEmp(String selectedEmp) {
		this.selectedEmp = selectedEmp;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getResetCandi() {
		return resetCandi;
	}

	public void setResetCandi(String resetCandi) {
		this.resetCandi = resetCandi;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

}
