package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SearchCandidate extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(SearchCandidate.class);

	String strSessionEmpId = null;
	UtilityFunctions uF = new UtilityFunctions();

	List<FillEducational> eduList;
	List<FillSkills> skillsList ;
	List<FillWLocation> workList;
	String f_wlocation;
	List<FillOrganisation> organisationList;
	String f_org;
	String recruitId;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		/*
		 * boolean isView = CF.getAccess(session, request, uF); if(!isView){
		 * request.setAttribute(PAGE, PAccessDenied);
		 * request.setAttribute(TITLE, TAccessDenied); return ACCESS_DENIED; }
		 */
		
		if(getF_wlocation()==null){
			setF_wlocation((String)session.getAttribute(WLOCATIONID));
		}
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getStrSkills()==null)
		{
			setStrSkills(new String[1]);
			strSkills[0]="";
			
		}
		if(getStrMinEducation()==null)
		{
			setStrMinEducation(new String[1]);
			strMinEducation[0]="";
			
		}
		
		organisationList = new FillOrganisation(request).fillOrganisation();
		workList = new FillWLocation(request).fillWLocation(getF_org());
		skillsList = new FillSkills(request).fillSkills();
		eduList = new FillEducational(request).fillEducationalQual();
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, "Candidate Search");
		request.setAttribute(PAGE, "/jsp/recruitment/SearchCandidate.jsp");


		prepareCandidateStats();
		return LOAD;

	}

	private void prepareCandidateStats() {
		
		List<List<String>> alCandidateReport = new ArrayList<List<String>>();

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		try {
			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			/*LOGIC LEFT FOR MULTIPLE EXPERIENCE *******************************/

			Map<String,String> hmCandidateExperience=new HashMap<String, String>();
		
			pst=con.prepareStatement("select candidate_final_status,to_date,from_date,recruitment_id,emp_per_id from candidate_prev_employment cpe" +
					" join candidate_personal_details cpd on(cpd.emp_per_id=cpe.emp_id) " +
					"where  application_status=2 and not candidate_final_status=-1  order by emp_per_id");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String	strCandidateIdOld = null;
			String	strCandidateIdNew = null;
			int noyear = 0,nomonth = 0,nodays = 0;
			while(rst.next()) {
				strCandidateIdNew = rst.getString("emp_per_id");
				if(strCandidateIdNew!=null && !strCandidateIdNew.equals(strCandidateIdOld)){
				
					noyear=0;
					nomonth=0;
					nodays=0;
				}
					String datedif=uF.dateDifference(rst.getString("from_date"),DBDATE , rst.getString("to_date"), DBDATE);
		    		
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
			    		
			    	hmCandidateExperience.put(rst.getString("emp_per_id"),""+noyear+" Year "+nomonth+" months "); 
			    	strCandidateIdOld = strCandidateIdNew;
			}
			rst.close();
			pst.close();

			// for skill multiple***********
			
			Map<String,String> hmCandidateSkill=new HashMap<String, String>();
			Map<String, String> hmSkillsName = CF.getSkillNameMap(con);
			
			pst=con.prepareStatement("select recruitment_id,job_code,emp_id,skill_id,candidate_final_status from candidate_skills_description csd join candidate_personal_details cpd on(cpd.emp_per_id=csd.emp_id) where application_status=2 and not candidate_final_status=-1");
		
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				    String temp=hmCandidateSkill.get(rst.getString("emp_id"));
				    if(temp!=null)		    		 
				    	temp+=", "+hmSkillsName.get(rst.getString("skill_id"));		   
				    else
				    	temp=hmSkillsName.get(rst.getString("skill_id"));
				    hmCandidateSkill.put(rst.getString("emp_id"),temp); 
			}
			rst.close();
			pst.close();
			
			// for education multiple***********
	
			Map<String,String> hmCandidateEducation=new HashMap<String, String>();
			Map<String, String> hmDegreeName = CF.getDegreeNameMap(con);
			pst=con.prepareStatement("select recruitment_id,job_code,emp_id,education_id,candidate_final_status from candidate_education_details ced join candidate_personal_details cpd on(cpd.emp_per_id=ced.emp_id) where application_status=2 and not candidate_final_status=-1");
	
			rst=pst.executeQuery();	
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				 String temp=hmCandidateEducation.get(rst.getString("emp_id"));
				 if(temp!=null)
					 temp+= ", " + hmDegreeName.get(rst.getString("education_id"));
				 else
				 temp = hmDegreeName.get(rst.getString("education_id"));
	    	
				 hmCandidateEducation.put(rst.getString("emp_id"), temp);

			}
			rst.close();
			pst.close();
		
			request.setAttribute("hmCandidateEducation", hmCandidateEducation);			
			request.setAttribute("hmCandidateSkill", hmCandidateSkill);
			request.setAttribute("hmCandidateExperience", hmCandidateExperience);

			StringBuilder query=new StringBuilder("select years,* from candidate_personal_details join recruitment_details r using (recruitment_id) " +
					"join work_location_info wli on (wlocation=wlocation_id) left join (select sum(to_date-from_date)/365 as years ,emp_id from " +
					"candidate_prev_employment  group by emp_id) as b ON (emp_per_id=b.emp_id)  join (select * from (select ced.emp_id from " +
					"candidate_education_details  ced left JOIN candidate_skills_description csd ON(ced.emp_id=csd.emp_id)  where ced.emp_id>0 ");
			if (strSkills != null) {
				int dataFlag = 0;
				for (int i = 0; i < strSkills.length; i++) {
					if (!strSkills[i].equals("")) {
						dataFlag = 1;
						if (i == 0)
							query.append(" and  skill_id in(");
						if (i > 0)
							query.append(",");
						query.append("" + strSkills[i] +"");
					}
				}
				if (dataFlag == 1)
					query.append(") ");
			}
			if (strMinEducation != null) {
				int dataFlag = 0;
				for (int i = 0; i < strMinEducation.length; i++) {
					if (!strMinEducation[i].equals("")) {
						dataFlag = 1;
						if (i == 0)
							query.append(" and education_id in(");
						if (i > 0)
							query.append(",");
						query.append("" + strMinEducation[i] + "");
					}
				}
				if (dataFlag == 1)
					query.append(") ");
			}
			query.append(")as a) as d on (d.emp_id=emp_per_id) where emp_per_id>0");
	
		//	query.append("where  application_status=1 and candidate_final_status=1 and candidate_status=-1");
			if (uF.parseToInt(getF_wlocation()) > 0) {
				query.append(" and r.wlocation="
						+ uF.parseToInt(getF_wlocation()));
			}
			if (uF.parseToInt(getF_org())> 0) {
				query.append(" and wli.org_id="
						+uF.parseToInt(getF_org()));
			}
			if(getStrExperience()!=null && uF.parseToInt(getStrExperience())!=0){
			    if(uF.parseToInt(getStrExperience())==1)
			    	query.append(" and years<=1 and years>=0");
			    else if(uF.parseToInt(getStrExperience())==2)
			    	query.append(" and years<=2 and years>=1");
			    else if(uF.parseToInt(getStrExperience())==3)
			    	query.append(" and years<=5 and years>=3");
			    	else if(uF.parseToInt(getStrExperience())==4)
			    		query.append(" and years<=10 and years>=5");
			    		else if(uF.parseToInt(getStrExperience())==5)
			    			query.append(" and years>=10");
			}
			
			if(uF.parseToInt(getRecruitId())!=0)
			{
			query.append("and recruitment_id="+getRecruitId());	
			}
			query.append(" order by emp_per_id");
			
			pst=con.prepareStatement(query.toString());
			
			String oldEmp ="";
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
			if(!oldEmp.equals(rst.getString("emp_per_id"))){
			List<String> alInner=new ArrayList<String>();	
			
			alInner.add(rst.getString("emp_per_id"));
			alInner.add(rst.getString("job_code"));
			
			String strEmpMName = "";
			if(flagMiddleName) {
				if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
					strEmpMName = " "+rst.getString("emp_mname");
				}
			}
			
			alInner.add(rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
			alInner.add(rst.getString("emp_email"));
			alInner.add("<a class=\"factsheet\" href=\"CandidateMyProfile.action?CandID="
					+ rst.getString("emp_per_id")   /* +"&recruitId="+rst.getString("recruitment_id")*/
					+ "\"></a>");
		
			alCandidateReport.add(alInner);
			oldEmp=rst.getString("emp_per_id");
				}
			}
			rst.close();
			pst.close();
			
			request.setAttribute("alCandidateReport", alCandidateReport);
	
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


/*
	private String getAppendData(String strID) {
		StringBuilder sb = new StringBuilder();
        
		if (strID != null && !strID.equals("")) {
      int flag=0;
			Map<String, String> hmEmpName = CF.getEmpNameMap(null, null);

				String[] temp = strID.split(",");

				for (int i =0; i < temp.length; i++) {
	
					if(temp[i]!=null && !temp[i].equals("")){
					 if(flag==0)
					sb.append(hmEmpName.get(temp[i].trim()));
					else	
					sb.append("," + hmEmpName.get(temp[i].trim()));
					 flag=1;
					}
					
			}

		}

		return sb.toString();
	}
*/
	private HttpServletRequest request;

	String reportStatus;

	public String getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public List<FillEducational> getEduList() {
		return eduList;
	}

	public void setEduList(List<FillEducational> eduList) {
		this.eduList = eduList;
	}

	public List<FillSkills> getSkillsList() {
		return skillsList;
	}

	public void setSkillsList(List<FillSkills> skillsList) {
		this.skillsList = skillsList;
	}
	

		String	strExperience;
		String[]	strSkills;
		String[]	strMinEducation;
		
		public String getStrExperience() {
			return strExperience;
		}
		
		public void setStrExperience(String strExperience) {
			this.strExperience = strExperience;
		}

		
		public String[] getStrSkills() {
			return strSkills;
		}

		public void setStrSkills(String[] strSkills) {
			this.strSkills = strSkills;
		}

		public String[] getStrMinEducation() {
			return strMinEducation;
		}

		public void setStrMinEducation(String[] strMinEducation) {
			this.strMinEducation = strMinEducation;
		}

		public List<FillWLocation> getWorkList() {
			return workList;
		}

		public void setWorkList(List<FillWLocation> workList) {
			this.workList = workList;
		}

		public String getF_wlocation() {
			return f_wlocation;
		}

		public void setF_wlocation(String f_wlocation) {
			this.f_wlocation = f_wlocation;
		}

		public List<FillOrganisation> getOrganisationList() {
			return organisationList;
		}

		public void setOrganisationList(List<FillOrganisation> organisationList) {
			this.organisationList = organisationList;
		}

		public String getF_org() {
			return f_org;
		}

		public void setF_org(String f_org) {
			this.f_org = f_org;
		}

		public String getRecruitId() {
			return recruitId;
		}

		public void setRecruitId(String recruitId) {
			this.recruitId = recruitId;
		}


		
}
