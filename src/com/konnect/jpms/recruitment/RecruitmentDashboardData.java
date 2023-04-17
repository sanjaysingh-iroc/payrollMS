package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSourceTypeAndName;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class RecruitmentDashboardData implements ServletRequestAware, IStatements {
	HttpSession session;
	CommonFunctions CF;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null;
	
	private String dataType;
	private String f_org;
	private String strLocation;
	private String strDesignation;
	private String strAppliSourceName;
	
	private String[] location;
	private String[] designation;
	
	private List<FillOrganisation> organisationList;
	private List<FillDesig> desigList;
	private List<FillWLocation> workLocationList;
	
	private List<FillSourceTypeAndName> sourceTypeList;
	private List<FillSourceTypeAndName> sourceNameList;
	
	private String strSearchJob;
	
	private String appliSourceType;
	private String[] appliSourceName;
	
	private String recruitId;
	private String fromPage;
	private String callFrom;
	public String execute() {
		session = request.getSession();
	
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		
		if (CF == null)
			return "login";
			
		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		
		strSessionEmpId = (String) session.getAttribute(EMPID);

		if (CF == null) 
			return "login";
		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setLocation(getStrLocation().split(","));
		} else {
			setLocation(null);
		}
		if(getStrDesignation() != null && !getStrDesignation().equals("")) {
			setDesignation(getStrDesignation().split(","));
		} else {
			setDesignation(null);
		}
		
		if(getStrAppliSourceName() != null && !getStrAppliSourceName().equals("")) {
			setAppliSourceName(getStrAppliSourceName().split(","));
		} else {
			setAppliSourceName(null);
		}
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(strUserType!=null && (strUserType.equalsIgnoreCase(HRMANAGER) || strBaseUserType.equalsIgnoreCase(CEO))) {
			if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
				organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
				if(uF.parseToInt(getF_org()) == 0  && organisationList!=null && organisationList.size()>0) {
					setF_org(organisationList.get(0).getOrgId());
				}
				workLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
				desigList = new FillDesig(request).fillDesigByOrgOrAccessOrg(0,(String)session.getAttribute(ORG_ACCESS));
			} else {
				organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORGID));
				 if(uF.parseToInt(getF_org()) == 0) {
					setF_org((String) session.getAttribute(ORGID));
				}
				 workLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATIONID));
				 desigList = new FillDesig(request).fillDesig(uF.parseToInt(getF_org()));
			 }
		} else if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
			organisationList = new FillOrganisation(request).fillOrganisation();
			workLocationList = new FillWLocation(request).fillWLocation(getF_org());
			desigList = new FillDesig(request).fillDesig(uF.parseToInt(getF_org()));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORGID));
			if(uF.parseToInt(getF_org()) == 0) {
				setF_org((String) session.getAttribute(ORGID));
			}
			workLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATIONID));
			desigList = new FillDesig(request).fillDesig(uF.parseToInt(getF_org()));
		}
		/*organisationList = new FillOrganisation(request).fillOrganisation();
		workLocationList = new FillWLocation(request).fillWLocation(getF_org());
		desigList = new FillDesig(request).fillDesig(uF.parseToInt(getF_org()));	*/
		
//		System.out.println("workLocationList=>"+workLocationList.size());
		sourceTypeList = new FillSourceTypeAndName(request).fillSourceType();
		if(getAppliSourceType() !=null && !getAppliSourceType().equals("")) {
			if(uF.parseToInt(getAppliSourceType()) != SOURCE_WEBSITE) {
				sourceNameList = new FillSourceTypeAndName(request).fillSourceNameOnType(getAppliSourceType());
			}
		} else {
			sourceNameList = new FillSourceTypeAndName(request).fillAllSourceName();
		}
		
		getSelectedFilter(uF);
		if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")){
			setDataType("L");
		}
		getSearchAutoCompleteData(uF);
		
		return LOAD;
	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null) {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++) {
//				for(int j=0;j<getF_sbu().length;j++) {
					if(getF_org().equals(organisationList.get(i).getOrgId())) {
						strOrg=organisationList.get(i).getOrgName();
					}
//				}
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
					if(getLocation() != null && getLocation()[j].equals(workLocationList.get(i).getwLocationId())) {
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
					if(getDesignation()!= null && getDesignation()[j].equals(desigList.get(i).getDesigId())) {
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
				if(getAppliSourceType()!= null && getAppliSourceType().equals(sourceTypeList.get(i).getSourceTypeId())) {
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
						if(getAppliSourceName() != null && getAppliSourceName()[j].equals(sourceNameList.get(i).getSourceTypeId())) {
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
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and close_job_status = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and close_job_status = true ");
			}
			if(getAppliSourceType() != null && !getAppliSourceType().equals("") && !getAppliSourceType().equalsIgnoreCase("null")) {
				sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where source_type="+uF.parseToInt(getAppliSourceType())+" ");
				if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
					if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
						sbQuery.append(" and added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
						sbQuery.append(" and source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")");
					}
				} /*else if(getStrAppliSourceName()!=null && !getStrAppliSourceName().equals("") && !getStrAppliSourceName().equalsIgnoreCase("null")){
					if(uF.parseToInt(getAppliSourceType()) == SOURCE_HR || uF.parseToInt(getAppliSourceType()) == SOURCE_RECRUITER) {
						sbQuery.append(" and added_by in ("+getStrAppliSourceName()+")");
					} else if(uF.parseToInt(getAppliSourceType()) == SOURCE_REFERENCE) {
						sbQuery.append(" and source_or_ref_code in ("+getStrAppliSourceName()+")");
					}
				}*/
				sbQuery.append(") ");
			} else if(getAppliSourceName()!=null && getAppliSourceName().length > 0 && !getAppliSourceName()[0].trim().equals("")) {
				sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where candidate_id>0 ");
				sbQuery.append(" and (added_by in ("+StringUtils.join(getAppliSourceName(), ",")+")");
				sbQuery.append(" or source_or_ref_code in ("+StringUtils.join(getAppliSourceName(), ",")+")) ");
				sbQuery.append(") ");
			}/* else if(getStrAppliSourceName()!=null && !getStrAppliSourceName().equals("") && !getStrAppliSourceName().equalsIgnoreCase("null")) {
				sbQuery.append(" and recruitment_id in (select recruitment_id from candidate_application_details where candidate_id>0 ");
				sbQuery.append(" and (added_by in ("+getStrAppliSourceName()+")");
				sbQuery.append(" or source_or_ref_code in ("+getStrAppliSourceName()+")) ");
				sbQuery.append(") ");
			}*/
			
			if(strUserType != null && (strUserType.equals(MANAGER) || strUserType.equals(RECRUITER))) {
				sbQuery.append(" and added_by = "+ uF.parseToInt(strSessionEmpId) +" ");
			}
			if(uF.parseToInt(getF_org()) > 0) {
			 sbQuery.append(" and org_id = "+uF.parseToInt(getF_org())+" ");
	        }
	        if(getLocation()!=null && getLocation().length > 0 && !getLocation()[0].trim().equals("")) {
	        	sbQuery.append(" and wlocation in ("+StringUtils.join(getLocation(), ",")+") ");
			}else if(getStrLocation()!=null && !getStrLocation().equals("") && !getStrLocation().equalsIgnoreCase("null")) {
				sbQuery.append(" and wlocation in ("+getLocation()+") ");
			}
	        
	        if(getDesignation()!=null && getDesignation().length > 0 && !getDesignation()[0].trim().equals("")) {
	        	sbQuery.append(" and designation_id in ("+StringUtils.join(getDesignation(), ",")+") ");
			}else if(getStrDesignation()!=null && !getStrDesignation().equals("") && !getStrDesignation().equalsIgnoreCase("null")) {
				sbQuery.append(" and designation_id in ("+getStrDesignation()+") ");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
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
							"and skill_name != '' and skill_id in ("+sbSkillsId.toString()+") ");
					rs = pst.executeQuery();
					while (rs.next()) {
						setJobList.add(rs.getString("skill_name").trim());
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
	
	public HttpServletRequest request;

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

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
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

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrDesignation() {
		return strDesignation;
	}

	public void setStrDesignation(String strDesignation) {
		this.strDesignation = strDesignation;
	}

	public String getStrAppliSourceName() {
		return strAppliSourceName;
	}

	public void setStrAppliSourceName(String strAppliSourceName) {
		this.strAppliSourceName = strAppliSourceName;
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

	
}