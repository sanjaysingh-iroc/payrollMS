package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class JobNamesList extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null;
	
	CommonFunctions CF = null;

	private String f_org;
	private String location;
	private String designation;

	private String dataType;
	private String proPage;
	private String minLimit;
	private String recruitId;
	
	private String appliSourceType;
	private String appliSourceName;
	private String strSearchJob;
	private String fromPage;
	private String callFrom;
	private String strRecruitId;
	
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
		
		if(getDataType() == null || getDataType().equals("") || getDataType().equalsIgnoreCase("Null")) {
			setDataType("L");
		}
		
		if(uF.parseToInt(getProPage()) == 0) {
			setProPage("1");
		}
//		System.out.println("getJobNamesList callfrom==>"+callFrom);
		getJobNamesList(uF);
		return LOAD;
	}
	
	private void getJobNamesList(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
				
	    try {
	    	con = db.makeConnection(con);
	    	//preparing acceptance from candidate********************
	    	
	    	List<String> alSkillIds = new ArrayList<String>();
	    	if(getStrSearchJob() != null && !getStrSearchJob().equals("")) {
		    	pst = con.prepareStatement("select skill_id from skills_details where upper(skill_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'");
		    	rst = pst.executeQuery();
//		    	System.out.println("pst ===>> " + pst); 
		    	while (rst.next()) {
		    		alSkillIds.add(rst.getString("skill_id"));
		    	}
		    	rst.close(); 
		    	pst.close();
	    	}
//	    	System.out.println("alSkillIds ===>> " + alSkillIds);
	    
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(recruitment_id) as recCount from recruitment_details where job_approval_status=1 ");
			if(strBaseUserType != null && (strBaseUserType.equals(MANAGER) || strBaseUserType.equals(RECRUITER))){
				sbQuery.append(" and (added_by = "+ uF.parseToInt(strSessionEmpId) +" or hiring_manager like '%,"+strSessionEmpId+",%' )");
			}
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and close_job_status = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and close_job_status = true ");
			}
			if(getAppliSourceType() != null && !getAppliSourceType().equals("") && !getAppliSourceType().equalsIgnoreCase("null")) {
				sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where source_type="+uF.parseToInt(getAppliSourceType())+" ");
				if(getAppliSourceName()!=null && !getAppliSourceName().equals("") &&  !getAppliSourceName().equalsIgnoreCase("null")) {
					if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
						sbQuery.append(" and added_by in ("+getAppliSourceName()+")");
					} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
						sbQuery.append(" and source_or_ref_code in ("+getAppliSourceName()+")");
					}
				}
				sbQuery.append(") ");
			} else if(getAppliSourceName()!=null && !getAppliSourceName().equals("") &&  !getAppliSourceName().equalsIgnoreCase("null")) {
				sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where candidate_id>0 ");
				sbQuery.append(" and (added_by in ("+getAppliSourceName()+")");
				sbQuery.append(" or source_or_ref_code in ("+getAppliSourceName()+")) ");
				sbQuery.append(") ");
			}
			
			if(uF.parseToInt(getF_org()) > 0) {
	        	sbQuery.append(" and org_id = "+uF.parseToInt(getF_org())+" ");
	        }
	        if(getLocation()!=null && !getLocation().equals("") &&  !getLocation().equalsIgnoreCase("null")) {
				sbQuery.append(" and wlocation in ("+getLocation()+") ");
			}
	        if(getDesignation()!=null && !getDesignation().equals("") &&  !getDesignation().equalsIgnoreCase("null")) {
				sbQuery.append(" and designation_id in ("+getDesignation()+") ");
			}
	        
	        if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
				sbQuery.append(" and (upper(job_code) like '%"+getStrSearchJob().trim().toUpperCase()+"%' " +
				" or upper(job_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' ");
				for(int i=0; i<alSkillIds.size(); i++) {
                    sbQuery.append(" or skills like '%,"+alSkillIds.get(i)+",%'");
                }
				for(int i=0; i<alSkillIds.size(); i++) {
                    sbQuery.append(" or essential_skills like '%,"+alSkillIds.get(i)+",%'");
                }
                sbQuery.append(" ) ");
            }
			pst=con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1==>"+pst);
			rst=pst.executeQuery();
			int proCount = 0;
			int proCnt = 0;
			while(rst.next()) {
				proCnt = rst.getInt("recCount");
				proCount = rst.getInt("recCount")/15;
				if(rst.getInt("recCount")%15 != 0) {
					proCount++;
				}
			}
			rst.close();
			pst.close();
			request.setAttribute("proCount", proCount+"");
			request.setAttribute("proCnt", proCnt+"");
			
			/*System.out.println("proCount==>"+proCount);
			System.out.println("proCnt==>"+proCnt);*/
			
	    	StringBuilder sbQuery1 = new StringBuilder();
			sbQuery1.append("select a.*,cpd.recruitment_id as p_recruitment_id from (select a.*,cpd.recruitment_id as r_recruitment_id from ( " +
				" select designation_name,job_code,job_title,recruitment_id,custum_designation,close_job_status,no_position from recruitment_details " +
				" left join designation_details using(designation_id) where job_approval_status=1 ");
			if(strBaseUserType != null && (strBaseUserType.equals(MANAGER) || strBaseUserType.equals(RECRUITER))) {
				sbQuery1.append(" and (added_by = "+ uF.parseToInt(strSessionEmpId) +" or hiring_manager like '%,"+strSessionEmpId+",%' )");
			}
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery1.append(" and close_job_status = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery1.append(" and close_job_status = true ");
			}
			if(getAppliSourceType() != null && !getAppliSourceType().equals("") &&  !getAppliSourceType().equalsIgnoreCase("null")) {
				sbQuery1.append(" and recruitment_id in (select recruitment_id from candidate_application_details where source_type="+uF.parseToInt(getAppliSourceType())+" ");
				if(getAppliSourceName()!=null && !getAppliSourceName().equals("") &&  !getAppliSourceName().equalsIgnoreCase("null")) {
					if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
						sbQuery1.append(" and added_by in ("+getAppliSourceName()+")");
					} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
						sbQuery1.append(" and source_or_ref_code in ("+getAppliSourceName()+")");
					}
				}
				sbQuery1.append(") ");
			} else if(getAppliSourceName()!=null && !getAppliSourceName().equals("") &&  !getAppliSourceName().equalsIgnoreCase("null")) {
				sbQuery1.append(" and recruitment_id in (select recruitment_id from candidate_application_details where candidate_id>0 ");
				sbQuery1.append(" and (added_by in ("+getAppliSourceName()+") ");
				sbQuery1.append(" or source_or_ref_code in ("+getAppliSourceName()+")) ");
				sbQuery1.append(") ");
			}
			
			if(uF.parseToInt(getF_org()) > 0) {
				sbQuery1.append(" and org_id = "+uF.parseToInt(getF_org())+" ");
	        }
	        if(getLocation()!=null && !getLocation().equals("") &&  !getLocation().equalsIgnoreCase("null")) {
	        	sbQuery1.append(" and wlocation in ("+getLocation()+") ");
			}
	        if(getDesignation()!=null && !getDesignation().equals("") &&  !getDesignation().equalsIgnoreCase("null")) {
	        	sbQuery1.append(" and designation_id in ("+getDesignation()+") ");
			}
	        
	        if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
				sbQuery1.append(" and (upper(job_code) like '%"+getStrSearchJob().trim().toUpperCase()+"%' " +
				" or upper(job_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' ");
				for(int i=0; i<alSkillIds.size(); i++) {
                    sbQuery1.append(" or skills like '%,"+alSkillIds.get(i)+",%'");
                }
				for(int i=0; i<alSkillIds.size(); i++) {
                    sbQuery1.append(" or essential_skills like '%,"+alSkillIds.get(i)+",%'");
                }
                sbQuery1.append(" ) ");
            }
				
		    sbQuery1.append(")a LEFT JOIN (select distinct(recruitment_id) from candidate_application_details) cpd on(cpd.recruitment_id=a.recruitment_id) " +
				"order by close_job_status,cpd.recruitment_id desc) a LEFT JOIN (select distinct(recruitment_id) from panel_interview_details " +
				"where panel_emp_id is not null) cpd on(cpd.recruitment_id=a.recruitment_id) " +
				"order by close_job_status,r_recruitment_id desc,cpd.recruitment_id desc");
		    
			int intOffset = uF.parseToInt(minLimit);
			sbQuery1.append(" limit 15 offset "+intOffset+"");
			pst=con.prepareStatement(sbQuery1.toString());
			System.out.println("pst==>"+pst);
			rst=pst.executeQuery();
			
			Map<String, List<String>> hmJobNames = new LinkedHashMap<String, List<String>>();
			int i =0;
			while(rst.next()){
				List<String> innerList = new ArrayList<String>();
				if((rst.getString("p_recruitment_id") == null || rst.getString("p_recruitment_id").equals("")) && (rst.getString("r_recruitment_id") == null || rst.getString("r_recruitment_id").equals(""))  && uF.parseToBoolean(rst.getString("close_job_status")) == false){
					innerList.add(rst.getString("job_code").trim());
					innerList.add(rst.getString("job_title").trim());
					if(i == 0) setRecruitId(rst.getString("recruitment_id").trim());
				} else if(rst.getString("p_recruitment_id") != null && rst.getString("r_recruitment_id") == null && uF.parseToBoolean(rst.getString("close_job_status")) == false){
					innerList.add(rst.getString("job_code").trim());
					innerList.add(rst.getString("job_title").trim());
					if(i == 0) setRecruitId(rst.getString("recruitment_id").trim());
				} else if(rst.getString("p_recruitment_id") == null && rst.getString("r_recruitment_id") != null && uF.parseToBoolean(rst.getString("close_job_status")) == false){
					innerList.add(rst.getString("job_code").trim());
					innerList.add(rst.getString("job_title").trim());
					if(i == 0) setRecruitId(rst.getString("recruitment_id").trim());
				} else if(rst.getString("p_recruitment_id") != null && rst.getString("r_recruitment_id") != null && uF.parseToBoolean(rst.getString("close_job_status")) == false){
					innerList.add(rst.getString("job_code").trim());
					innerList.add(rst.getString("job_title").trim());
					if(i == 0) setRecruitId(rst.getString("recruitment_id").trim());
				} else if(uF.parseToBoolean(rst.getString("close_job_status")) == true) {
					innerList.add(rst.getString("job_code").trim());
					innerList.add(rst.getString("job_title").trim());
					if(i == 0) setRecruitId(rst.getString("recruitment_id").trim());
				}
				i++;
				hmJobNames.put(rst.getString("recruitment_id").trim(), innerList);
			}
			rst.close();
			pst.close();
			
//			System.out.println("setRecruitId ===="+getRecruitId());			
//			System.out.println("hmJobNames ===="+hmJobNames.size());

			request.setAttribute("hmJobNames", hmJobNames);

		} catch (Exception e) {
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
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
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

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getAppliSourceType() {
		return appliSourceType;
	}

	public void setAppliSourceType(String appliSourceType) {
		this.appliSourceType = appliSourceType;
	}

	public String getAppliSourceName() {
		return appliSourceName;
	}

	public void setAppliSourceName(String appliSourceName) {
		this.appliSourceName = appliSourceName;
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getCallFrom() {
		return callFrom;
	}

	public void setCallFrom(String callFrom) {
		this.callFrom = callFrom;
	}

	public String getStrRecruitId() {
		return strRecruitId;
	}

	public void setStrRecruitId(String strRecruitId) {
		this.strRecruitId = strRecruitId;
	}

}
