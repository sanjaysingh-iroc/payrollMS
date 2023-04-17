package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillGender;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateJobProfile extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;
	String strEmpWLocId = null;

	private String recruitID ;
	
	private String updateJobProfile;
	private String addInformation;
	private String minEducation;
	private String gender;
	private String minAge;
	private String strMinCTC;
	private String strMaxCTC;
	
	private String maxMonth;
	private String maxYear;
	private String minMonth;
	private String minYear;
	private String candidateProfile;
	private String jobDescription;
	private String essentialQualification;
	private String alternateQualification;
	private String empselected;
	private String sourceofRec;
	private String advertisementMedia;
	private String view;
	
	private String orgID;
	private String wlocID;
	private String desigID;
	private String checkStatus;
	private String fdate;
	private String tdate;
	private String frmPage;
	
	private List<FillEducational> eduList;
	private List<FillEducational> essentialEduList;
	
	private List<FillWLocation> workList;
	private List<FillDepartment> departmentList;
	private List<FillOrganisation> orgList;
	private List<FillLevel> levelList;
	private List<FillDesig> designationList;
	private  List<FillEmployee> empList;
	private List<FillGender> genderList;
	
	private String strOrg;
	private String currUserType;
	public String execute() throws Exception {
		
		request.setAttribute(PAGE, "/jsp/recruitment/UpdateJobProfile.jsp");
		request.setAttribute(TITLE, "Update Job Profile");
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpWLocId = (String) session.getAttribute(WLOCATIONID);
		
		UtilityFunctions uF = new UtilityFunctions();

//		recruitID = request.getParameter("recruitID");
		eduList = new FillEducational(request).fillEducationalQual();
		essentialEduList = new FillEducational(request).fillEducationalQual();
//		String updt = request.getParameter("update");
//		String updtapr = request.getParameter("updateApprove");
//		System.out.println("updt ===> "+updt);
//		System.out.println("updtapr ===> "+updtapr);

		if (updateJobProfile != null && updateJobProfile.equals("update")) {
			updateJobProfiles();
			
			if(getFrmPage() != null && getFrmPage().equals("HRDash")) {
				return "hrupdate";
			} else {
				return UPDATE;
			}
		}
		
		if(uF.parseToInt(getStrOrg()) == 0) {
			setStrOrg((String)session.getAttribute(ORGID));
		}

		orgList = new FillOrganisation(request).fillOrganisation();
		workList = new FillWLocation(request).fillWLocation(getStrOrg());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getStrOrg()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
		designationList = new FillDesig(request).fillDesig(uF.parseToInt(getStrOrg()));
		genderList=new FillGender().fillGender();
		
//		empList = new FillEmployee(request).fillEmployeeNameByLocation(strEmpWLocId);
		empList = new FillEmployee(request).fillEmployeeName(null, null, uF.parseToInt(getStrOrg()),0,session);
		
		getSelectedJobProfile(getRecruitID(), uF);
		getSelectEmployeeList(getRecruitID(), uF);
		return LOAD;
	
	}


	String job_desc_info=null;
	String cand_profile_info=null;

	
private void getSelectEmployeeList(String recruitmentID,UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		ResultSet rst = null;
		try {
			
			con=db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			pst = con.prepareStatement("select consultant_ids from recruitment_details where recruitment_id=?");
			pst.setInt(1, uF.parseToInt(recruitmentID));
			rst=pst.executeQuery();
			String selectEmpIDs=null;
			while(rst.next()) {		
				selectEmpIDs=rst.getString("consultant_ids");
			}
			rst.close();
			pst.close();
			
			List<String> selectEmpList=new ArrayList<String>();
			Map<String,String> hmCheckEmpList=new HashMap<String, String>();
			StringBuilder sb = new StringBuilder();
			if(selectEmpIDs!=null && !selectEmpIDs.equals("")) {
				
				List<String> tmpselectEmpList=Arrays.asList(selectEmpIDs.split(","));
				
				int i=0;
				if(tmpselectEmpList != null && !tmpselectEmpList.isEmpty()) {
					for(String empId:tmpselectEmpList) {
						if(empId.equals("0") || empId.equals("")) {
							continue;
						}
						selectEmpList.add(hmEmpName.get(empId));
						if(i==0) {
							sb.append(empId);
							i++;
						} else {
							sb.append(","+empId); 
						}
						hmCheckEmpList.put(empId.trim(), empId.trim());
					}
				}
			} else {
				selectEmpList=null;
			}
			request.setAttribute("selectEmpList", selectEmpList);
			request.setAttribute("hmCheckEmpList", hmCheckEmpList);
			request.setAttribute("empids", sb.toString());
		}catch(Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	
	private void updateJobProfiles() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			String updtapr = request.getParameter("updateApprove");
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("update recruitment_details set job_description=?, min_exp=?, max_exp=?, min_education=?, "
					+ "candidate_profile=?, additional_info=?,job_profile_updated_by=?,job_profile_updated_date=?, " +
							"essential_qualification=?,alternate_qualification=?,consultant_ids=?,source_of_requirement=?," +
							"advertisement_media=?,sex=?,age=?,min_ctc=?,max_ctc=? ");
			
			if(updtapr != null && !updtapr.equals("")) {
				sbQuery.append(",job_approval_status = 1 ");
			}
			sbQuery.append(" where recruitment_id=?");
//			String query = "update recruitment_details set job_description=?, min_exp=?, max_exp=?, min_education=?, "
//					+ "candidate_profile=?, additional_info=?,job_profile_updated_by=?," +
//					"job_profile_updated_date=? where recruitment_id=?";
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setString(1, getJobDescription());
			double minexp = parseDouble(getMinYear(), getMinMonth());
			pst.setDouble(2, minexp);

			double maxexp = parseDouble(getMaxYear(), getMaxMonth());
			pst.setDouble(3, maxexp);

			pst.setString(4,getMinEducation());
			pst.setString(5, getCandidateProfile());
			pst.setString(6, getAddInformation());
			
			pst.setInt(7,uF.parseToInt(strSessionEmpId));
			pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(9, getEssentialQualification());
			pst.setString(10, getAlternateQualification());
			pst.setString(11, getEmpselected());
			pst.setString(12, getSourceofRec());
			pst.setString(13, getAdvertisementMedia());
//			System.out.println("getGender() ===>>> " + getGender());
			pst.setString(14, getGender());
			pst.setString(15, getMinAge());
			pst.setDouble(16, uF.parseToDouble(getStrMinCTC()));
			pst.setDouble(17, uF.parseToDouble(getStrMaxCTC()));
			pst.setInt(18, uF.parseToInt(getRecruitID()));
			pst.execute();
			pst.close();
			
			sendProfileUpdateMail(getRecruitID());
			
			if(updtapr != null && !updtapr.equals("")) {
				sendMail(getRecruitID());
				sendSendToConsultant(getRecruitID());
			}
			String idealCandi = null;
			String jobProfile = null;
			String skills= null;
			String attributeId = "1, 2, 3, 5, 6, 7, 8";
			String desigID = null;
			String levelID = null;
			pst = con.prepareStatement("select * from recruitment_details where recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitID()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("pst ===> "+pst);
			while (rs.next()) {
				idealCandi = rs.getString("ideal_candidate");
				jobProfile = rs.getString("candidate_profile");
				skills = rs.getString("skills");
				desigID = rs.getString("designation_id");
				levelID = rs.getString("level_id");
			}
			pst.close();
			
			pst = con.prepareStatement("UPDATE designation_details SET attribute_ids=?,ideal_candidate=?,profile=?," +
					"job_description=? WHERE designation_id=?");
			pst.setString(1, attributeId);
			pst.setString(2, idealCandi);
			pst.setString(3, jobProfile);
			pst.setString(4, getJobDescription());
			pst.setInt(5, uF.parseToInt(desigID));			
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("select org_id from level_details where level_id=?");
			pst.setInt(1, uF.parseToInt(levelID.trim()));
			rs=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String orgid = null;
			while(rs.next()) {
				orgid = rs.getString("org_id");
			}
			rs.close();
			pst.close();
			
//			System.out.println("getAttributeid()=====>"+getAttributeid());
			if(attributeId!=null && !attributeId.equals("")) {
				List<String> attList=Arrays.asList(attributeId.split(","));
					for(int i=0; attList!=null && !attList.isEmpty() && i<attList.size(); i++) {
						String id = attList.get(i).trim();
						String desig_value = null;
						String value_type = "";
						boolean flag = false;
						if(id.equals("1")) {
							desig_value = getMinEducation();
							value_type = ",";
							flag=true;
						} else if(id.equals("2")) {
							desig_value = getMinYear()+"."+getMinMonth();
							value_type = ",";
							flag=true;
						} else if(id.equals("3")) {
							desig_value = getMaxYear()+"."+getMaxMonth();
							flag=true;
						} else if(id.equals("4")) {
							desig_value = "0.0";
							flag=true;
						} else if(id.equals("5")) {
							desig_value = skills;
							value_type=",";
							flag=true;
						} else if(id.equals("6")) {
							desig_value = getGender();
							flag=true;
						} else if(id.equals("7")) {
							desig_value = getMinAge();
							flag=true;
						} else if(id.equals("8")) {
							desig_value = uF.showData(getStrMinCTC(), "0")+"-"+uF.showData(getStrMaxCTC(), "0");
							flag=true;
						}
						
						if(flag) {
							pst=con.prepareStatement("update desig_attribute set desig_value=?,value_type=? where desig_id=? and _type=?");
							pst.setString(1, desig_value);
							pst.setString(2, value_type);
							pst.setInt(3, uF.parseToInt(desigID));
							pst.setInt(4, uF.parseToInt(id));
							int x=pst.executeUpdate();
							pst.close();
							
							if(x==0) {
								pst=con.prepareStatement("insert into desig_attribute(desig_id,desig_value,_type,value_type)values(?,?,?,?)");
								pst.setInt(1, uF.parseToInt(desigID));
								pst.setString(2, desig_value);
								pst.setInt(3, uF.parseToInt(id));
								pst.setString(4, value_type);
								pst.execute();
								pst.close();
							}
							
							/*if(id.equals("5")) {
								List<String> skillList=Arrays.asList(skills.split(","));
								for(int j=0;skillList!=null && !skillList.isEmpty() && j<skillList.size();j++) {
									String skill=skillList.get(j).trim();
									pst = con.prepareStatement("select * from skills_details where upper(skill_name) like ?");
									pst.setString(1, skill.toUpperCase());
									rs=pst.executeQuery();
									System.out.println("new Date ===> " + new Date());
									boolean flg=false;
									while(rs.next()) {
										flg=true;
									}
									if(!flg) {
										pst=con.prepareStatement("insert into skills_details(skill_name,org_id)values(?,?)");
										pst.setString(1, skill);
										pst.setInt(2, uF.parseToInt(orgid));
										pst.execute();										
									}
								}
							} else if(id.equals("1")) {
								List<String> eduList=Arrays.asList(getMinEducation().split(","));
								for(int j=0;eduList!=null && !eduList.isEmpty() && j<eduList.size();j++) {
									String education=eduList.get(j).trim();
									pst = con.prepareStatement("select * from educational_details where upper(education_name) like ?");
									pst.setString(1, education.toUpperCase());
									rs=pst.executeQuery();
									System.out.println("new Date ===> " + new Date());
									boolean flg=false;
									while(rs.next()) {
										flg=true;
									}
									if(!flg) {
										pst=con.prepareStatement("insert into educational_details(education_name,org_id)values(?,?)");
										pst.setString(1, education);
										pst.setInt(2, uF.parseToInt(orgid));
										pst.execute();										
									}
								}
							}*/
							
						}
					}
				}
			
			String jobCodeName = getJobCodeNameById(con, uF, getRecruitID());
			if(updtapr != null && !updtapr.equals("")) {
				
				String strAddedBy = getAddedByName(con, uF, getRecruitID());
				if(uF.parseToInt(strAddedBy) > 0) {
					String strDomain = request.getServerName().split("\\.")[0];
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strAddedBy);
					userAlerts.set_type(JOBCODE_APPROVAL_ALERT);
					userAlerts.setStatus(INSERT_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
				}
				
				session.setAttribute(MESSAGE, SUCCESSM+""+jobCodeName+" job profile has been updated and approved successfully."+END);
			} else {
				session.setAttribute(MESSAGE, SUCCESSM+""+jobCodeName+" job profile has been updated successfully."+END);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private String getAddedByName(Connection con, UtilityFunctions uF, String strId) {
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		String strAddedBy = null;
		try{
			String queryy = "select rd.added_by,ud.usertype_id from recruitment_details rd, user_details ud where recruitment_id = ? and rd.added_by = ud.emp_id"; // and ud.usertype_id = 2
			pst = con.prepareStatement(queryy);
			pst.setInt(1, uF.parseToInt(strId));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				strAddedBy = rst.getString("added_by");
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
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
		return strAddedBy;
	}

	
	private String getJobCodeNameById(Connection con, UtilityFunctions uF, String recruitId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		String requirementName = null;
		try {
			pst = con.prepareStatement("select job_code,designation_id from recruitment_details where recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(recruitId));
			rst = pst.executeQuery();
//				Map<String, String> hmDesignation = CF.getDesigMap(con);
			String desigId = null;
			while (rst.next()) {
				desigId = rst.getString("designation_id");
				requirementName = rst.getString("job_code");
			}
			rst.close();
			pst.close();
//				designationName = hmDesignation.get(desigId);
				
		} catch (Exception e) {
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
		return requirementName;
	}
	
	
	public void sendSendToConsultant(String strId) {
//		System.out.println("++++++Thread example UpdateADRRequest++++++");
//		getRecruitmentDetails(strId);

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);

		try{
			con = db.makeConnection(con);
			Map<String, String> hmRecruitmentData = CF.getRecruitmentDetails(con, uF, CF, request, strId);
			
			String consultantIds = null;
			String queryy = "";
			queryy = "select consultant_ids from recruitment_details where recruitment_id = ?";
			pst = con.prepareStatement(queryy);
			pst.setInt(1, uF.parseToInt(strId));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				consultantIds = rst.getString("consultant_ids");
			}
			rst.close();
			pst.close();
			
			List<String> consultantList = Arrays.asList(consultantIds.split(","));
			for(int i=0; consultantList != null && !consultantList.isEmpty() && i < consultantList.size(); i++) {
				if(consultantList.get(i) != null && !consultantList.get(i).equals("")) {
					String strDomain = request.getServerName().split("\\.")[0];
					Notifications nF = new Notifications(N_HIRING_LINK_FOR_CONSULTANT, CF);
					nF.setDomain(strDomain);
					nF.request = request;
					nF.setStrEmpId(consultantList.get(i));
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
//					System.out.println("getDesignation_name() N_HIRING_LINK_FOR_CONSULTANT ===>> " + getDesignation_name());
					
					nF.setStrRecruitmentDesignation(hmRecruitmentData.get("DESIG_NAME"));
//					nF.setStrRecruitmentGrade(getGrade_name());
//					nF.setStrRecruitmentLevel(getLevel_name());
//					nF.setStrRecruitmentPosition(getPositions());
//					nF.setStrRecruitmentWLocation(getLocation_name());
//					nF.setStrRecruitmentProfile(getServices());
//					nF.setStrRecruitmentSkill(getSkills_name());
					nF.setEmailTemplate(true);
			
					nF.sendNotifications();
				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void sendProfileUpdateMail(String strId) {
//		System.out.println("++++++Thread example UpdateADRRequest++++++");
//		getRecruitmentDetails(strId);

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);

		try{
			con = db.makeConnection(con);
			Map<String, String> hmRecruitmentData = CF.getRecruitmentDetails(con, uF, CF, request, strId);
			
			String strAddedBy = null;
			String queryy = "";
			if(strUserType.equals(MANAGER)) {
				queryy = "select rd.approved_by,ud.usertype_id from recruitment_details rd, user_details ud where recruitment_id = ? and ud.usertype_id = 7 and rd.approved_by = ud.emp_id";
			} else if(strUserType.equals(HRMANAGER)) {
				queryy = "select rd.added_by,ud.usertype_id from recruitment_details rd, user_details ud where recruitment_id = ? and ud.usertype_id = 2 and rd.added_by = ud.emp_id";
			} 
			pst = con.prepareStatement(queryy);
			pst.setInt(1, uF.parseToInt(strId));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				strAddedBy = rst.getString(1);
			}
			rst.close();
			pst.close();
			
			if(strAddedBy != null && !strAddedBy.equals("")) {
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_JOB_PROFILE_UPDATE, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrEmpId(strAddedBy);
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
//				System.out.println("getDesignation_name() N_JOB_PROFILE_UPDATE ===>> " + getDesignation_name());
				nF.setStrRecruitmentDesignation(hmRecruitmentData.get("DESIG_NAME"));
				nF.setStrRecruitmentGrade(hmRecruitmentData.get("GRADE_NAME"));
				nF.setStrRecruitmentLevel(hmRecruitmentData.get("LEVEL_NAME"));
				nF.setStrRecruitmentPosition(hmRecruitmentData.get("POSITIONS"));
				nF.setStrRecruitmentWLocation(hmRecruitmentData.get("WLOC_NAME"));
//				nF.setStrRecruitmentProfile(getServices());
				nF.setStrRecruitmentSkill(hmRecruitmentData.get("SKILLS_NAME"));
				nF.setEmailTemplate(true);
				nF.sendNotifications();
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void sendMail(String strId) {
//		System.out.println("++++++Thread example UpdateADRRequest++++++");
//		getRecruitmentDetails(strId);

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);

		try{
			con = db.makeConnection(con);
			Map<String, String> hmRecruitmentData = CF.getRecruitmentDetails(con, uF, CF, request, strId);
			
			String strAddedBy = null;
			String queryy = "select rd.added_by,ud.usertype_id from recruitment_details rd, user_details ud where recruitment_id = ? and ud.usertype_id = 2 and rd.added_by = ud.emp_id";
			pst = con.prepareStatement(queryy);
			pst.setInt(1, uF.parseToInt(strId));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				strAddedBy = rst.getString("added_by");
			}
			rst.close();
			pst.close();
			
			if(strAddedBy != null && !strAddedBy.equals("")) {
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_JOB_PROFILE_APPROVAL, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrEmpId(strAddedBy);
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				
//				System.out.println("strAddedBy getDesignation_name() ===>> " + getDesignation_name());
				nF.setStrRecruitmentDesignation(hmRecruitmentData.get("DESIG_NAME"));
				nF.setStrRecruitmentGrade(hmRecruitmentData.get("GRADE_NAME"));
				nF.setStrRecruitmentLevel(hmRecruitmentData.get("LEVEL_NAME"));
				nF.setStrRecruitmentPosition(hmRecruitmentData.get("POSITIONS"));
				nF.setStrRecruitmentWLocation(hmRecruitmentData.get("WLOC_NAME"));
//				nF.setStrRecruitmentProfile(getServices());
				nF.setStrRecruitmentSkill(hmRecruitmentData.get("SKILLS_NAME"));
				nF.setEmailTemplate(true);
		
				nF.sendNotifications();
			}
			
		Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
		String empWlocation = hmEmpWLocation.get(strSessionEmpId);
//		String panel_employee_id = "";
		pst = con.prepareStatement("select emp_per_id from employee_personal_details epd, employee_official_details eod, user_details ud " +
			"where epd.emp_per_id = eod.emp_id and epd.is_alive=true and epd.emp_per_id = ud.emp_id and ud.usertype_id = 7 " +
			"and eod.wlocation_id = ? and emp_per_id!=?");
		pst.setInt(1, uF.parseToInt(empWlocation));
		pst.setInt(2, uF.parseToInt(strAddedBy));
		rst = pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
//		System.out.println("pst ===> "+pst);
		while (rst.next()) {
			//Map<String, String> hmEmpInner = hmEmpInfo.get(rst.getString("emp_per_id"));

			if(rst.getString("emp_per_id") != null && !rst.getString("emp_per_id").equals("")) {
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_JOB_PROFILE_APPROVAL, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrEmpId(rst.getString("emp_per_id"));
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
		
//				System.out.println("emp_per_id HR getDesignation_name() ===>> " + getDesignation_name());
				nF.setStrRecruitmentDesignation(hmRecruitmentData.get("DESIG_NAME"));
				nF.setStrRecruitmentGrade(hmRecruitmentData.get("GRADE_NAME"));
				nF.setStrRecruitmentLevel(hmRecruitmentData.get("LEVEL_NAME"));
				nF.setStrRecruitmentPosition(hmRecruitmentData.get("POSITIONS"));
				nF.setStrRecruitmentWLocation(hmRecruitmentData.get("WLOC_NAME"));
				nF.setStrRecruitmentSkill(hmRecruitmentData.get("SKILLS_NAME"));
//				nF.setStrRecruitmentDesignation(getDesignation_name());
//				nF.setStrRecruitmentGrade(getGrade_name());
//				nF.setStrRecruitmentLevel(getLevel_name());
//				nF.setStrRecruitmentPosition(getPositions());
//				nF.setStrRecruitmentWLocation(getLocation_name());
//				nF.setStrRecruitmentProfile(getServices());
//				nF.setStrRecruitmentSkill(getSkills_name());
				nF.setEmailTemplate(true);
		
				nF.sendNotifications();
			} 
		}
		rst.close();
		pst.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
//	private void getRecruitmentDetails(String strId) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rst = null;
//		Database db = new Database();
//		db.setRequest(request);
//
//		UtilityFunctions uF = new UtilityFunctions();
//
//		con = db.makeConnection(con);
//
//		try {
////			System.out.println("in getRecruitmentDetails strid " + getStrId());
//
//			StringBuilder strQuery = new StringBuilder();
//
//			strQuery.append("select d.designation_code,d.designation_name,g.grade_code,g.grade_name,w.wlocation_name,r.no_position,"
//					+ "r.job_code,s.service_name,r.added_by,l.level_code,l.level_name,r.skills from recruitment_details r,grades_details g,"
//					+ "work_location_info w,designation_details d,services s,department_info di,employee_personal_details e,level_details l "
//					+ "where r.grade_id=g.grade_id and r.wlocation=w.wlocation_id and r.designation_id=d.designation_id and r.added_by=e.emp_per_id "
//					+ "and r.services=s.service_id and r.dept_id=di.dept_id and r.level_id=l.level_id and r.recruitment_id=?");
//
//			pst = con.prepareStatement(strQuery.toString());
//			pst.setInt(1, uF.parseToInt(strId));
//			rst = pst.executeQuery();
//			System.out.println("recruitment_details  pst ===> " + pst);
//			while (rst.next()) {
////				System.out.println("in rst.next");
//				designation_name = "[" + rst.getString(1) + "] "
//						+ rst.getString(2);
//				grade_name = "[" + rst.getString(3) + "] " + rst.getString(4);
//				location_name = rst.getString(5);
//				positions = rst.getString(6);
//				services = rst.getString(8);
//				Level_name = "[" + rst.getString(10) + "] " + rst.getString(11);
//				skills_name = rst.getString(12);
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rst);
//			db.closeConnection(con);
//		}
//
//	}
	
	
	private double parseDouble(String year, String month) {
		try {

			if ((year != null && year.length() > 0 && !year.equalsIgnoreCase("NULL"))
					&& (month != null && month.length() > 0 && !month.equalsIgnoreCase("NULL"))) {

//				System.out.println("parsedoubr year " + year + " month "+ month);
				year = year.trim();
				year = year.replaceAll(" ", "");

				month = month.trim();
				month = month.replaceAll(" ", "");
//				System.out.println("year " + year + " month " + month);
				if (year.length() == 1) {
					year = "0" + year;
				}

				if (month.length() == 1) {
					month = "0" + month;
				}
				return Double.parseDouble(year + "." + month);
			} else {
				return 0;
			}
		} catch (Exception e) {
			return 0;
		}
	}

	private void getSelectedJobProfile(String recruitID, UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		// List<List<String>> requestList = new ArrayList<List<String>>();
		List<String> jobProfileList = null;

		try {
			// grade_id grade_code grades_details wlocation_id wlocation_name
			// work_location_info
			// job_description min_exp max_exp min_education candidate_profile
			// additional_info

			con = db.makeConnection(con);
			Map<String, String> hmEmpLocation = CF.getEmpWlocationMap(con);
			Map<String, String> hmWLocation = CF.getWLocationMap(con,null, null);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			request.setAttribute("hmEmpLocation", hmEmpLocation);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			request.setAttribute("hmEmpName", hmEmpName);
			
			pst=con.prepareStatement("select * from designation_details");
			rs=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, String> hmDesigJobDesc=new HashMap<String, String>();
			Map<String, String> hmDesigProfile=new HashMap<String, String>();
			while(rs.next()) {
				hmDesigJobDesc.put(rs.getString("designation_id"), uF.showData(rs.getString("job_description"),""));
				hmDesigProfile.put(rs.getString("designation_id"), uF.showData(rs.getString("profile"),""));
			}
			rs.close();
			pst.close();
			
			
			pst=con.prepareStatement("select * from requirement_employment_type");
			rs=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, String> hmRequireEmpType = new HashMap<String, String>();
			while(rs.next()) {
				hmRequireEmpType.put(rs.getString("employment_type_id"), uF.showData(rs.getString("employment_type"),""));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmSkillName = CF.getSkillNameMap(con);
			Map<String, String> hmEduName = CF.getDegreeNameMap(con);
			StringBuilder query = new StringBuilder();
			query.append("select r.recruitment_id,d.designation_name,g.grade_code,w.wlocation_name,r.ideal_candidate,r.no_position,r.comments," +
					"r.job_code,r.job_description,r.min_exp,r.max_exp,r.min_education,r.skills,r.candidate_profile,r.additional_info," +
					"r.job_approval_status,r.custum_designation,org_name,r.designation_id,r.essential_qualification,r.alternate_qualification," +
					"r.essential_skills,r.type_of_employment,r.sex,r.age,r.vacancy_type,r.give_justification,r.replacement_person_ids," +
					"r.reporting_to_person_ids,r.temp_casual_give_jastification,r.consultant_ids,r.source_of_requirement,r.advertisement_media," +
					"r.job_title,r.customer_id,r.added_by,r.hiring_manager,r.min_ctc,r.max_ctc " +
					"from recruitment_details r left join grades_details g using(grade_id) left join work_location_info w on r.wlocation=w.wlocation_id " +
					"left join designation_details d on r.designation_id=d.designation_id left join org_details od on (r.org_id=od.org_id) " +
					"where r.recruitment_id=? ");
			if(getView() !=null && getView().equals("Request")) {
				query.append(" and r.status=0 ");
			} else {
				query.append(" and r.status=1 ");
			}
			pst = con.prepareStatement(query.toString());
			pst.setInt(1, uF.parseToInt(recruitID));
//			System.out.println("pst =====>> " + pst);
			rs = pst.executeQuery();
			int nCount = 0;
			Map<String, String> hmSourceOfReq = new HashMap<String, String>();
			Map<String, String> hmAdvMedia = new HashMap<String, String>();
//			Map<String, String> hmGender = new HashMap<String, String>();
//			hmGender.put("M", "Male");
//			hmGender.put("F", "Female");
//			hmGender.put("0", "Any");
//			hmGender.put("", "Any");
			
//			request.setAttribute("hmGender", hmGender);
			while (rs.next()) {
				jobProfileList = new ArrayList<String>();
				jobProfileList.add(removeNUll(rs.getString("recruitment_id"))); //0
				jobProfileList.add(removeNUll(rs.getString("designation_name"))); //1
				jobProfileList.add(removeNUll(rs.getString("grade_code"))); //2
				jobProfileList.add(removeNUll(rs.getString("wlocation_name"))); //3
				jobProfileList.add(removeNUll(rs.getString("no_position"))); //4
				jobProfileList.add(removeNUll(rs.getString("comments"))); //5
				jobProfileList.add(removeNUll(rs.getString("job_code"))); //6
                
				if(rs.getString("job_description")!=null && !rs.getString("job_description").equals("")) {
					jobProfileList.add(rs.getString("job_description")); //7
				} else {					
					jobProfileList.add(hmDesigJobDesc.get(rs.getString("designation_id"))); //7
				}

				String minex;
				if (rs.getString("min_exp") == null || rs.getString("min_exp").equals("")) {
					minex = removeNUll("0.0");
				} else if (rs.getString("min_exp").contains(".1")) {
					minex = removeNUll(rs.getString("min_exp") + "0");
				} else if (rs.getString("min_exp").contains(".")) {
					minex = removeNUll(rs.getString("min_exp"));
				} else {
					minex = removeNUll(rs.getString("min_exp") + ".0");
				}
				String[] minTemp = splitString(minex);
				jobProfileList.add(removeNUll(minTemp[0])); //8
				jobProfileList.add(removeNUll(minTemp[1])); //9

				String maxex;
				if (rs.getString("max_exp") == null || rs.getString("max_exp").equals("")) {
					maxex = removeNUll("0.0");
				} else if (rs.getString("max_exp").contains(".1")) {
					maxex = removeNUll(rs.getString("max_exp") + "0");
				} else if (rs.getString("max_exp").contains(".")) {
					maxex = removeNUll(rs.getString("max_exp"));
				} else {
					maxex = removeNUll(rs.getString("max_exp") + ".0");
				}
				String[] maxTemp = splitString(maxex);
				jobProfileList.add(removeNUll(maxTemp[0]));//10
				jobProfileList.add(removeNUll(maxTemp[1]));//11


				jobProfileList.add(removeNUll(getAppendData(rs.getString("min_education"), hmEduName)));//12

			     if(rs.getString("min_education")!=null) {
				 List<String> eduListSelected11=Arrays.asList(rs.getString("min_education").split(","));
				
				 for(int i=0;eduListSelected11!=null && i<eduListSelected11.size();i++) {
					 if(eduListSelected11.get(i)!=null && !eduListSelected11.get(i).equals(""))
					 eduListSelected.add(eduListSelected11.get(i).trim());
					 }
			     } else {
			    	 eduListSelected.add("");
			     }
			     
				if(rs.getString("candidate_profile")!=null && !rs.getString("candidate_profile").equals("")) {
					jobProfileList.add(rs.getString("candidate_profile"));//13
				} else {					
					jobProfileList.add(hmDesigProfile.get(rs.getString("designation_id")));//13
				}
				
				jobProfileList.add(removeNUll(rs.getString("additional_info")));//14
				jobProfileList.add(removeNUll(rs.getString("job_approval_status")));//15

				jobProfileList.add(removeNUll(rs.getString("custum_designation")));//16
				jobProfileList.add(removeNUll(rs.getString("org_name")));//17
				
				jobProfileList.add(removeNUll(rs.getString("ideal_candidate")));//18
				
				jobProfileList.add(removeNUll(getAppendData(rs.getString("skills"), hmSkillName)));//19
				setEssentialQualification(removeNUll(rs.getString("essential_qualification")));
				jobProfileList.add(removeNUll(getAppendData(rs.getString("essential_qualification"), hmEduName)));//20
				jobProfileList.add(removeNUll(rs.getString("alternate_qualification"))); //21
				jobProfileList.add(removeNUll(getAppendData(rs.getString("essential_skills"), hmSkillName)));//22
				jobProfileList.add(removeNUll(rs.getString("type_of_employment")));//23
				String typeOfEmployment = uF.stringMapping(rs.getString("type_of_employment"));
				String strSex = null;
//				System.out.println("sex ===>> "+ rs.getString("sex"));
				if(rs.getString("sex") != null && rs.getString("sex").equals("F")) {
					strSex = "Female";
				} else if(rs.getString("sex") != null && rs.getString("sex").equals("M")) {
					strSex = "Male";
				} else if(rs.getString("sex") != null && rs.getString("sex").equals("0")) {
					strSex = "Any";
				} else {
					strSex = "Any";
				}
				jobProfileList.add(removeNUll(rs.getString("sex")));//24
				jobProfileList.add(removeNUll(rs.getString("age")));//25
				String strVacancyType = null;
				if(rs.getString("vacancy_type") != null && rs.getString("vacancy_type").equals("0")) {
					strVacancyType = "Replacement";
				} else if(rs.getString("vacancy_type") != null && rs.getString("vacancy_type").equals("1")) {
					strVacancyType = "Additional";
				}
				jobProfileList.add(removeNUll(rs.getString("vacancy_type")));//26
				jobProfileList.add(removeNUll(rs.getString("give_justification")));//27
				jobProfileList.add(removeNUll(getAppendData(rs.getString("replacement_person_ids"), hmEmpName)));//28
				jobProfileList.add(removeNUll(getAppendData(rs.getString("reporting_to_person_ids"), hmEmpName)));//29
				jobProfileList.add(removeNUll(rs.getString("temp_casual_give_jastification")));//30
				jobProfileList.add(removeNUll(getAppendData(rs.getString("consultant_ids"), hmEmpName)));//31
				jobProfileList.add(removeNUll(strSex));//32
				jobProfileList.add(removeNUll(strVacancyType));//33
				jobProfileList.add(removeNUll(typeOfEmployment));//34
				jobProfileList.add(removeNUll(rs.getString("job_title"))); //35
				jobProfileList.add(uF.showData(getAppendData(rs.getString("hiring_manager"), hmEmpName), "-")); //36
				jobProfileList.add(removeNUll(CF.getClientNameById(con, rs.getString("customer_id")))); //37
				jobProfileList.add(removeNUll(rs.getString("min_ctc"))); //38
				jobProfileList.add(removeNUll(rs.getString("max_ctc"))); //39
				
				
				setStrMinCTC(removeNUll(rs.getString("min_ctc")));
				setStrMaxCTC(removeNUll(rs.getString("max_ctc")));
				
				setEmpselected(rs.getString("consultant_ids"));
				if(rs.getString("source_of_requirement") != null) {
					List<String> sourceOfReqList = Arrays.asList(rs.getString("source_of_requirement").split(","));
					for (int i = 0; sourceOfReqList != null && !sourceOfReqList.isEmpty() && i < sourceOfReqList.size(); i++) {
						if(sourceOfReqList .get(i).trim().equals("Advertisement")) { //
							hmSourceOfReq.put("_ADV", "checked");
						} else if(sourceOfReqList.get(i).trim().equals("Consultant")) {
							hmSourceOfReq.put("_CON", "checked");
						} else if(sourceOfReqList.get(i).trim().equals("Reference")) {
							hmSourceOfReq.put("_REF", "checked");
						} else if(sourceOfReqList.get(i).trim().equals("Colleges")) {
							hmSourceOfReq.put("_COL", "checked");
						} else if(sourceOfReqList.get(i).trim().equals("EmployementExchange")) {
							hmSourceOfReq.put("_EMP", "checked");
						} else if(sourceOfReqList.get(i).trim().equals("Inhouse")) {
							hmSourceOfReq.put("_INH", "checked");
						}
					}
				}
				if(rs.getString("advertisement_media") != null) {
					List<String> advMediaList = Arrays.asList(rs.getString("advertisement_media").split(","));
					for (int i = 0; advMediaList != null && !advMediaList.isEmpty() && i < advMediaList.size(); i++) {
						if(advMediaList .get(i).trim().equals("Periodicals")) { //        
							hmAdvMedia.put("_PERI", "checked");
						} else if(advMediaList.get(i).trim().equals("Magazines")) {
							hmAdvMedia.put("_MAGA", "checked");
						} else if(advMediaList.get(i).trim().equals("Newspaper")) {
							hmAdvMedia.put("_NEWS", "checked");
						} else if(advMediaList.get(i).trim().equals("Websites")) {
							hmAdvMedia.put("_WEBS", "checked");
						} else if(advMediaList.get(i).trim().equals("Anyother")) {
							hmAdvMedia.put("_ANYO", "checked");
						}
					}
				}
				nCount++;
			}
			rs.close();
			pst.close();
			
//			System.out.println("jobProfileList ===>> " + jobProfileList);
			request.setAttribute("hmSourceOfReq", hmSourceOfReq);
			request.setAttribute("hmAdvMedia", hmAdvMedia);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("jobProfileList", jobProfileList);
	}

	
	private String getAppendData(String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
//		System.out.println("strID :: "+strID);
		if (strID != null && !strID.equals("") && !strID.isEmpty()) {
			if(strID.length()>0 && strID.substring(0, 1).equals(",") && strID.substring(strID.length()-1, strID.length()).equals(",")) {
				strID = strID.substring(1, strID.length()-1);
			}
//			System.out.println("strID :::: " + strID);
			if (strID.contains(",")) {
				String[] temp = strID.split(",");
				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim()));
					} else {
						sb.append(", " + mp.get(temp[i].trim()));
					} 
				}
			} else {
//				System.out.println("strID else :::: " + strID);
				return mp.get(strID);
			}
		} else {
			return null;
		}
		return sb.toString();
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

	public String getEssentialQualification() {
		return essentialQualification;
	}

	public void setEssentialQualification(String essentialQualification) {
		this.essentialQualification = essentialQualification;
	}

	public String getAlternateQualification() {
		return alternateQualification;
	}

	public void setAlternateQualification(String alternateQualification) {
		this.alternateQualification = alternateQualification;
	}

	public String getRecruitID() {
		return recruitID;
	}

	public void setRecruitID(String recruitID) {
		this.recruitID = recruitID;
	}

	public String getUpdateJobProfile() {
		return updateJobProfile;
	}

	public void setUpdateJobProfile(String updateJobProfile) {
		this.updateJobProfile = updateJobProfile;
	}

	public String getAddInformation() {
		return addInformation;
	}

	public void setAddInformation(String addInformation) {
		this.addInformation = addInformation;
	}

	public String getMinEducation() {
		return minEducation;
	}

	public void setMinEducation(String minEducation) {
		this.minEducation = minEducation;
	}

	public String getMaxMonth() {
		return maxMonth;
	}

	public void setMaxMonth(String maxMonth) {
		this.maxMonth = maxMonth;
	}

	public String getMaxYear() {
		return maxYear;
	}

	public void setMaxYear(String maxYear) {
		this.maxYear = maxYear;
	}

	public String getMinMonth() {
		return minMonth;
	}	

	public void setMinMonth(String minMonth) {
		this.minMonth = minMonth;
	}

	public String getMinYear() {
		return minYear;
	}

	public void setMinYear(String minYear) {
		this.minYear = minYear;
	}

	public String getCandidateProfile() {
		return candidateProfile;
	}

	public void setCandidateProfile(String candidateProfile) {
		this.candidateProfile = candidateProfile;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}
	

	public List<FillEducational> getEduList() {
		return eduList;
	}

	public void setEduList(List<FillEducational> eduList) {
		this.eduList = eduList;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	List<String> eduListSelected=new ArrayList<String>();

	public List<String> getEduListSelected() {
		return eduListSelected;
	}

	public void setEduListSelected(List<String> eduListSelected) {
		this.eduListSelected = eduListSelected;
	}
	
	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	String pageFrom;

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}

	public String getOrgID() {
		return orgID;
	}

	public void setOrgID(String orgID) {
		this.orgID = orgID;
	}

	public String getWlocID() {
		return wlocID;
	}

	public void setWlocID(String wlocID) {
		this.wlocID = wlocID;
	}

	public String getDesigID() {
		return desigID;
	}

	public void setDesigID(String desigID) {
		this.desigID = desigID;
	}

	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}

	public String getFdate() {
		return fdate;
	}

	public void setFdate(String fdate) {
		this.fdate = fdate;
	}

	public String getTdate() {
		return tdate;
	}

	public void setTdate(String tdate) {
		this.tdate = tdate;
	}
	
	String positions;
	String Level_name;
	String designation_name;
	String grade_name;
	String location_name;
	String skills_name;
	String services;

	public String getPositions() {
		return positions;
	}

	public void setPositions(String positions) {
		this.positions = positions;
	}

	public String getLevel_name() {
		return Level_name;
	}

	public void setLevel_name(String level_name) {
		Level_name = level_name;
	}

	public String getDesignation_name() {
		return designation_name;
	}

	public void setDesignation_name(String designation_name) {
		this.designation_name = designation_name;
	}

	public String getGrade_name() {
		return grade_name;
	}

	public void setGrade_name(String grade_name) {
		this.grade_name = grade_name;
	}

	public String getLocation_name() {
		return location_name;
	}

	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}

	public String getSkills_name() {
		return skills_name;
	}

	public void setSkills_name(String skills_name) {
		this.skills_name = skills_name;
	}

	public String getServices() {
		return services;
	}

	public void setServices(String services) {
		this.services = services;
	}

	public List<FillWLocation> getWorkList() {
		return workList;
	}

	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillDesig> getDesignationList() {
		return designationList;
	}

	public void setDesignationList(List<FillDesig> designationList) {
		this.designationList = designationList;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String getEmpselected() {
		return empselected;
	}

	public void setEmpselected(String empselected) {
		this.empselected = empselected;
	}

	public String getSourceofRec() {
		return sourceofRec;
	}

	public void setSourceofRec(String sourceofRec) {
		this.sourceofRec = sourceofRec;
	}

	public String getAdvertisementMedia() {
		return advertisementMedia;
	}

	public void setAdvertisementMedia(String advertisementMedia) {
		this.advertisementMedia = advertisementMedia;
	}

	public String getFrmPage() {
		return frmPage;
	}

	public void setFrmPage(String frmPage) {
		this.frmPage = frmPage;
	}

	public List<FillGender> getGenderList() {
		return genderList;
	}

	public void setGenderList(List<FillGender> genderList) {
		this.genderList = genderList;
	}

	public List<FillEducational> getEssentialEduList() {
		return essentialEduList;
	}

	public void setEssentialEduList(List<FillEducational> essentialEduList) {
		this.essentialEduList = essentialEduList;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMinAge() {
		return minAge;
	}

	public void setMinAge(String minAge) {
		this.minAge = minAge;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getStrMinCTC() {
		return strMinCTC;
	}

	public void setStrMinCTC(String strMinCTC) {
		this.strMinCTC = strMinCTC;
	}

	public String getStrMaxCTC() {
		return strMaxCTC;
	}

	public void setStrMaxCTC(String strMaxCTC) {
		this.strMaxCTC = strMaxCTC;
	}


	public String getCurrUserType() {
		return currUserType;
	}


	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}
	
}
