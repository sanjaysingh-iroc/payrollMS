package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.recruitment.FillEducational;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PeopleUser extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF=null;
	String strUserType = null;
	
	String strSessionEmpId = null;
	
	String strLocation;
	String strSkill;
	String strEdu;
	String strExp;
	
	String f_org;
	String[] f_strWLocation;
	String[] strSkills;
	String[] strEducation;
	String[] strExperience;
	
	List<FillOrganisation> orgList; 
	List<FillWLocation> wLocationList;
	List<FillSkills> skillsList;
	List<FillEducational> eduList;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		
		strUserType = (String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId= (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/task/PeopleUser.jsp");
		request.setAttribute(TITLE, "User Database");
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}

		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrSkill() != null && !getStrSkill().equals("")) {
			setStrSkills(getStrSkill().split(","));
		} else {
			setStrSkills(null);
		}
		if(getStrEdu() != null && !getStrEdu().equals("")) {
			setStrEducation(getStrEdu().split(","));
		} else {
			setStrEducation(null);
		}
		if(getStrExp() != null && !getStrExp().equals("")) {
			setStrExperience(getStrExp().split(","));
		} else {
			setStrExperience(null);
		}
		
		getPeopleData(uF);
		
		return loadPeople(uF);
		
	}


	private void getPeopleData(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmEmpEducations = CF.getEmpEducations(con);
			if(hmEmpEducations == null) hmEmpEducations = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from settings where options=?");
			pst.setString(1, O_SHOW_PASSWORD);
			rs = pst.executeQuery();
			boolean isShowPassword = false;
			while(rs.next()){
				isShowPassword = uF.parseToBoolean(rs.getString("value"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbEmp = null;
			if(getStrEducation()!=null && getStrEducation().length>0){
				StringBuilder sbQuery1 = new StringBuilder();
				sbQuery1.append("select emp_id from education_details where education_id in ("+StringUtils.join(getStrEducation(), ",")+") and emp_id > 0 ");
				pst = con.prepareStatement(sbQuery1.toString()); 
				rs = pst.executeQuery();
				while (rs.next()) {
					if(sbEmp == null){
						sbEmp = new StringBuilder();
						sbEmp.append(rs.getString("emp_id"));
					} else {
						sbEmp.append(","+rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
			}
			
			if(getStrSkills()!=null && getStrSkills().length>0){
				StringBuilder sbQuery1 = new StringBuilder();
				sbQuery1.append("select emp_id from skills_description where skill_id in ("+StringUtils.join(getStrSkills(), ",")+") and emp_id > 0 ");
				pst = con.prepareStatement(sbQuery1.toString()); 
				rs = pst.executeQuery();
				while (rs.next()) {
					if(sbEmp == null){
						sbEmp = new StringBuilder();
						sbEmp.append(rs.getString("emp_id"));
					} else {
						sbEmp.append(","+rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
			}
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("SELECT * FROM user_details ud, user_type ut, employee_personal_details epd, employee_official_details eod WHERE ut.user_type_id=ud.usertype_id " +
					"and epd.emp_per_id = ud.emp_id  and epd.emp_per_id = eod.emp_id and eod.emp_id = ud.emp_id and is_delete=false ");
			if(sbEmp !=null) {
				sbQuery.append(" and eod.emp_id in("+sbEmp.toString()+") ");
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(strUserType!=null && strUserType.equals(MANAGER)){
	            sbQuery.append(" and eod.supervisor_emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
	        }
			sbQuery.append(" order by epd.emp_fname,epd.emp_lname");
			
			pst = con.prepareStatement(sbQuery.toString());
			System.out.println("sbQuery ===>> " + sbQuery.toString()); 
			rs = pst.executeQuery();
			List<Map<String, String>> alPeopleList = new ArrayList<Map<String,String>>();
			int nCount=0;
			while(rs.next()) {
				if(rs.getInt("emp_id")<0) {
					continue;
				}
				
				Map<String, String> hmPeople = new HashMap<String, String>();
				hmPeople.put("EMP_ID", rs.getString("emp_id"));
			
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmPeople.put("EMP_NAME", uF.showData(rs.getString("emp_fname"), "")+strEmpMName+" "+uF.showData(rs.getString("emp_lname"), ""));
				hmPeople.put("EMP_IMAGE", uF.showData(rs.getString("emp_image"), ""));
				hmPeople.put("JOINING_DATE", uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));
				hmPeople.put("EMP_EDUCATION", uF.showData(hmEmpEducations.get(rs.getString("emp_id")), ""));
				
				String username=rs.getString("username");
				username+=" <a href=\"javascript:void(0)\" class=\"edit\" " +
				"onclick=\"changeUserName('"+rs.getString("emp_id")+"','"+rs.getString("user_id")+"','"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"')\" title=\"Edit Exist\"></a>";
				hmPeople.put("USER_NAME", username);
				
				if(isShowPassword){
					hmPeople.put("PASSWORD", rs.getString("password")); 
				}else{
					hmPeople.put("PASSWORD", uF.getPasswordString(rs.getString("password")));
				}
				
				if(rs.getString("added_timestamp")!=null){
					hmPeople.put("CREATED_ON", uF.getDateFormat(rs.getString("added_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()));
				}else{
					hmPeople.put("CREATED_ON", "-");
				}
				
				hmPeople.put("USER_TYPE", rs.getString("user_type"));
				hmPeople.put("EMP_STATUS", rs.getString("emp_status")); 
				
				
				hmPeople.put("RESET_PASSWORD", "<div id=\"myDiv_"+nCount+"\"><a href=\"javascript:void(0)\" onclick=\"(confirm('Are You sure you want to reset the password?')?getContent('myDiv_"+nCount+"','ResetPassword.action?user_id="+rs.getString("user_id")+"'):'')\" >Reset Password</a></div>");
				if(rs.getString("reset_timestamp")!=null){
					hmPeople.put("RESET_TIMESTAMP", uF.getDateFormat(rs.getString("reset_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) +" at " +uF.getDateFormat(rs.getString("reset_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
				}else{
					hmPeople.put("RESET_TIMESTAMP", "-");
				}
				   
				String changeUsertype= "<a href=\"javascript:void(0)\" " +
				"onclick=\"changeUserType('"+rs.getString("emp_id")+"','"+rs.getString("user_id")+"','"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"')\" title=\"Change User Type\"> Change User Type</a>";
				hmPeople.put("CHANGE_USERTYPE", changeUsertype);
				
				alPeopleList.add(hmPeople); 
				
				nCount++;
			}
			rs.close();
			pst.close();
			request.setAttribute("alPeopleList", alPeopleList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private String loadPeople(UtilityFunctions uF) {
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		skillsList=new FillSkills(request).fillSkillsWithId();
		eduList=new FillEducational(request).fillEducationalQual();
		
		getSelectedFilter(uF);    
		
		return LOAD;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=orgList.get(i).getOrgName();
					} else {
						strOrg+=", "+orgList.get(i).getOrgName();
					}
					k++;
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
			
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
		alFilter.add("LOCATION");
		if(getF_strWLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getF_strWLocation().length;j++) {
					if(getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if(k==0) {
							strLocation=wLocationList.get(i).getwLocationName();
						} else {
							strLocation+=", "+wLocationList.get(i).getwLocationName();
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
		
		alFilter.add("SKILL");
		if(getStrSkills()!=null) {
			String strSkill="";
			int k=0;
			for(int i=0;skillsList!=null && i<skillsList.size();i++) {
				for(int j=0;j<getStrSkills().length;j++) {
					if(getStrSkills()[j].equals(skillsList.get(i).getSkillsId())) {
						if(k==0) {
							strSkill=skillsList.get(i).getSkillsName();
						} else {
							strSkill+=", "+skillsList.get(i).getSkillsName();
						}
						k++;
					}
				}
			}
			if(strSkill!=null && !strSkill.equals("")) {
				hmFilter.put("SKILL", strSkill);
			} else {
				hmFilter.put("SKILL", "All Skills");
			}
		} else {
			hmFilter.put("SKILL", "All Skills");
		}
		
		alFilter.add("EDUCATION");
		if(getStrEducation()!=null) {
			String strEducation="";
			int k=0;
			for(int i=0;eduList!=null && i<eduList.size();i++) {
				for(int j=0;j<getStrEducation().length;j++) {
					if(getStrEducation()[j].equals(eduList.get(i).getEduId())) {
						if(k==0) {
							strEducation=eduList.get(i).getEduName();
						} else {
							strEducation+=", "+eduList.get(i).getEduName();
						}
						k++;
					}
				}
			}
			if(strEducation!=null && !strEducation.equals("")) {
				hmFilter.put("EDUCATION", strEducation);
			} else {
				hmFilter.put("EDUCATION", "All Educations");
			}
		} else {
			hmFilter.put("EDUCATION", "All Educations");
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public String[] getStrSkills() {
		return strSkills;
	}

	public void setStrSkills(String[] strSkills) {
		this.strSkills = strSkills;
	}

	public String[] getStrEducation() {
		return strEducation;
	}

	public void setStrEducation(String[] strEducation) {
		this.strEducation = strEducation;
	}

	public String[] getStrExperience() {
		return strExperience;
	}

	public void setStrExperience(String[] strExperience) {
		this.strExperience = strExperience;
	}

	public List<FillSkills> getSkillsList() {
		return skillsList;
	}

	public void setSkillsList(List<FillSkills> skillsList) {
		this.skillsList = skillsList;
	}

	public List<FillEducational> getEduList() {
		return eduList;
	}

	public void setEduList(List<FillEducational> eduList) {
		this.eduList = eduList;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrSkill() {
		return strSkill;
	}

	public void setStrSkill(String strSkill) {
		this.strSkill = strSkill;
	}

	public String getStrEdu() {
		return strEdu;
	}

	public void setStrEdu(String strEdu) {
		this.strEdu = strEdu;
	}

	public String getStrExp() {
		return strExp;
	}

	public void setStrExp(String strExp) {
		this.strExp = strExp;
	}

}
