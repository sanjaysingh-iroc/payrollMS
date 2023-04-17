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

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class JobOpportunity extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	CommonFunctions CF = null;
	String strSessionEmpId = null;
	//===start parvez date: 21-10-2021===
	String strSessionOrgId = null;
	//===end parvez date: 21-10-2021===

	String proPage;
	String minLimit;

	private String strSearchJob;

	private String btnSubmit;
	private String btnReset;

	private String strRecruitId;
	private String refEmpId;

	private List<FillOrganisation> orgList;
	private String orgId;

	private String strCategory;

	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			CF = new CommonFunctions();
			CF.setRequest(request);
		}
		UtilityFunctions uF = new UtilityFunctions();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		//===start parvez date: 21-10-2021===
		strSessionOrgId = (String) session.getAttribute(ORGID);
		//===end parvez date: 21-10-2021===

		request.setAttribute(TITLE, "Job Opportunities");
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
		request.setAttribute("hmFeatureStatus", hmFeatureStatus);
		Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
		if(hmFeatureUserTypeId==null) hmFeatureUserTypeId = new HashMap<String, List<String>>();
		if(uF.parseToBoolean(hmFeatureStatus.get(F_INSTANCE_NAMEWISE_FEATURE)) && hmFeatureUserTypeId.get(F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS")!=null && hmFeatureUserTypeId.get(F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS").contains(INTELIMENT)) {
			request.setAttribute(PAGE, "/jsp/recruitment/JobOpportunityNew.jsp");//Created by Dattatray Date:19-08-21
		} else {
			request.setAttribute(PAGE, "/jsp/recruitment/JobOpportunity.jsp");
		}

//		System.out.println("JO/72--orgId="+session.getAttribute(ORGID));
		orgList = new FillOrganisation(request).fillOrganisation();
		if (uF.parseToInt(getOrgId()) == 0) {
			setOrgId(orgList.get(0).getOrgId());
//			System.out.println("JO/75--orgList.get(0).getOrgId()=="+orgList.get(0).getOrgId());
		}

		if (uF.parseToInt(getProPage()) == 0) {
			setProPage("1");
		}

		if (getBtnReset() != null) {
			setStrSearchJob("");
		}

//		System.out.println("Category : " + getStrCategory());
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"\"></i><a href=\"JobOpportunities.action\" style=\"color: #3c8dbc;\"> Job Opportunities</a></li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());

		// session.setAttribute(MENU, "/jsp/common/PreMenu.jsp");
		getSearchAutoCompleteData(uF);
		if (uF.parseToInt(getStrRecruitId()) > 0) {
			singleJobDetailsWithAllJobs(uF);
//			System.out.println("IF CONDITION");
		} else {
			preparejobreport(uF);
//			System.out.println("ELSE CONDITION");
		}

		if(uF.parseToBoolean(hmFeatureStatus.get(F_INSTANCE_NAMEWISE_FEATURE)) && hmFeatureUserTypeId.get(F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS")!=null && hmFeatureUserTypeId.get(F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS").contains(INTELIMENT)) {
			return "success2";
		} else {
			return "success1";
		}
		// return LOAD;

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
			// pst = con.prepareStatement(selectDesig);
			pst = con.prepareStatement("SELECT * FROM designation_details ald INNER JOIN level_details ld ON ald.level_id = ld.level_id and org_id="
					+ getOrgId() + " order by designation_name");
			rs = pst.executeQuery();
			while (rs.next()) {
				setJobList.add(rs.getString("designation_name"));
			}
			rs.close();
			pst.close();

			StringBuilder sbData = null;
			Iterator<String> it = setJobList.iterator();
			while (it.hasNext()) {
				String strData = it.next();
				if (sbData == null) {
					sbData = new StringBuilder();
					sbData.append("\"" + strData + "\"");
				} else {
					sbData.append(",\"" + strData + "\"");
				}
			}

			if (sbData == null) {
				sbData = new StringBuilder();
			}
			// System.out.println("sbData ===>> " + sbData.toString());
			request.setAttribute("sbData", sbData.toString());

		} catch (Exception e) {

		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void preparejobreport(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
			con = db.makeConnection(con);

			Map<String, Map<String, String>> hmOrgMap = CF.getOrgDetails(con, uF);
			if (hmOrgMap == null)
				hmOrgMap = new HashMap<String, Map<String, String>>();

			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con);
			if (hmWorkLocation == null)
				hmWorkLocation = new HashMap<String, Map<String, String>>();
			Map<String, String> hmSkillName = CF.getSkillNameMap(con);
			if (hmSkillName == null)
				hmSkillName = new HashMap<String, String>();

			StringBuilder sbQuery = new StringBuilder();
			/*sbQuery.append("select a.*,cpd.recruitment_id as p_recruitment_id from (select a.*,cpd.recruitment_id as r_recruitment_id from ("
					+ "select designation_name,job_code,recruitment_id,custum_designation,close_job_status,no_position from recruitment_details"
					+ " left join designation_details using(designation_id) where job_approval_status=1 and org_id =" + getOrgId()
					+ " and close_job_status = false ");*/
			
	//===start parvez date: 21-10-2021===
			sbQuery.append("select a.*,cpd.recruitment_id as p_recruitment_id from (select a.*,cpd.recruitment_id as r_recruitment_id from ("
					+ "select designation_name,job_code,recruitment_id,custum_designation,close_job_status,no_position from recruitment_details"
					+ " left join designation_details using(designation_id) where job_approval_status=1 ");
			if(strSessionOrgId != null){
				sbQuery.append("and org_id ="+strSessionOrgId);
			} else {
				sbQuery.append("and org_id ="+getOrgId());
			}
			sbQuery.append(" and close_job_status = false ");
	//===end parvez date: 21-10-2021===
			
//			System.out.println("JO/181--getOrgId="+getOrgId());
			// if(uF.parseToInt(getStrRecruitId()) > 0) {
			// sbQuery.append(" and
			// recruitment_id="+uF.parseToInt(getStrRecruitId()));
			// }
			// Start Dattatray Date : 21-08-21
			if (uF.parseToInt(getStrCategory()) > 0) {
				sbQuery.append(" and jd_category= " + uF.parseToInt(getStrCategory()) + "");
			} // End
			if (getStrSearchJob() != null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and upper(designation_name) like '%" + getStrSearchJob().trim().toUpperCase() + "%'");
			}
			sbQuery.append(")a LEFT JOIN (select distinct(recruitment_id) from candidate_application_details) cpd on(cpd.recruitment_id=a.recruitment_id) "
					+ "order by close_job_status,cpd.recruitment_id desc) a LEFT JOIN (select distinct(recruitment_id) from panel_interview_details "
					+ "where panel_emp_id is not null) cpd on(cpd.recruitment_id=a.recruitment_id) order by close_job_status,r_recruitment_id desc,cpd.recruitment_id desc");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("JO/196--pst=======>" + pst);
			rst = pst.executeQuery();
			StringBuilder recID1 = new StringBuilder();
			StringBuilder recID2 = new StringBuilder();
			StringBuilder recID3 = new StringBuilder();
			StringBuilder recID4 = new StringBuilder();
			StringBuilder recID5 = new StringBuilder();
			while (rst.next()) {
				if ((rst.getString("p_recruitment_id") == null || rst.getString("p_recruitment_id").equals(""))
						&& (rst.getString("r_recruitment_id") == null || rst.getString("r_recruitment_id").equals(""))
						&& uF.parseToBoolean(rst.getString("close_job_status")) == false) {
					recID1.append(rst.getString("recruitment_id").trim());
					recID1.append(",");
				} else if (rst.getString("p_recruitment_id") != null && rst.getString("r_recruitment_id") == null
						&& uF.parseToBoolean(rst.getString("close_job_status")) == false) {
					recID2.append(rst.getString("recruitment_id").trim());
					recID2.append(",");
				} else if (rst.getString("p_recruitment_id") == null && rst.getString("r_recruitment_id") != null
						&& uF.parseToBoolean(rst.getString("close_job_status")) == false) {
					recID3.append(rst.getString("recruitment_id").trim());
					recID3.append(",");
				} else if (rst.getString("p_recruitment_id") != null && rst.getString("r_recruitment_id") != null
						&& uF.parseToBoolean(rst.getString("close_job_status")) == false) {
					recID4.append(rst.getString("recruitment_id").trim());
					recID4.append(",");
				} else if (uF.parseToBoolean(rst.getString("close_job_status")) == true) {
					recID5.append(rst.getString("recruitment_id").trim());
					recID5.append(",");
				}
			}
			rst.close();
			pst.close();

			StringBuilder appendallID = new StringBuilder();
			appendallID.append(recID1);
			appendallID.append(recID2);
			appendallID.append(recID3);
			appendallID.append(recID4);
			appendallID.append(recID5);

			// System.out.println("appendallID ===>> " +
			// appendallID.toString());

			List<String> recruitmentIDList = Arrays.asList(appendallID.toString().split(","));
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
			if(hmFeatureUserTypeId==null) hmFeatureUserTypeId = new HashMap<String, List<String>>();
			if(uF.parseToBoolean(hmFeatureStatus.get(F_INSTANCE_NAMEWISE_FEATURE)) && hmFeatureUserTypeId.get(F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS")!=null && hmFeatureUserTypeId.get(F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS").contains(INTELIMENT)) {
				
			} else {
				int proCnt = recruitmentIDList.size();
				// System.out.println("proCnt======>"+proCnt);
				request.setAttribute("proCnt", proCnt + "");
				// System.out.println("recruitmentIDList ===>> " +
				// recruitmentIDList);
				if (recruitmentIDList.size() > 0) {
					int proCount = recruitmentIDList.size() / 10;
					if (recruitmentIDList.size() % 10 != 0) {
						proCount++;
					}
					request.setAttribute("proCount", proCount + "");
					if (recruitmentIDList.size() > 10) {
						int nStart = 0;
						int nEnd = 10;
						if (uF.parseToInt(getMinLimit()) > 0) {
							nStart = uF.parseToInt(getMinLimit());
							nEnd = uF.parseToInt(getMinLimit()) + 10;
						}
	
						if (nEnd > recruitmentIDList.size()) {
							nEnd = recruitmentIDList.size();
						}
	
						recruitmentIDList = recruitmentIDList.subList(nStart, nEnd);
					}
				}
			}

			// System.out.println("recruitmentIDList.size() ===>> " +
			// recruitmentIDList.size());

			if (recruitmentIDList.size() > 0) {
				StringBuilder sbRecruitId = null;
				for (String strId : recruitmentIDList) {
					if (strId.trim().length() > 0) {
						if (sbRecruitId == null) {
							sbRecruitId = new StringBuilder();
							sbRecruitId.append(strId);
						} else {
							sbRecruitId.append("," + strId);
						}
					}
				}

				if (sbRecruitId != null) {
					sbQuery = new StringBuilder();
					sbQuery.append(
							"select designation_name,job_code,recruitment_id,custum_designation,close_job_status,no_position,priority_job_int,req_form_type,"
									+ "job_title,recruitment_details.job_description,min_exp,max_exp,recruitment_details.wlocation,recruitment_details.org_id,essential_skills,rt.technology_name "
									+ "from recruitment_details "
									+ "left join designation_details using(designation_id) "
									+ "left join recruitment_technology rt on recruitment_technology_id=technology_id "//Created by Dattatray Date:21-08-21 Note: left join and rt.technology_name
									+ "where job_approval_status=1 and close_job_status = false");
					sbQuery.append(" and recruitment_id in (" + sbRecruitId.toString() + ")");
					pst = con.prepareStatement(sbQuery.toString());
//					System.out.println("JO/302--pst ===>> " + pst);
					rst = pst.executeQuery();
					Map<String, List<String>> hmJobReport = new HashMap<String, List<String>>();
					while (rst.next()) {
						List<String> job_code_info = new ArrayList<String>();
						job_code_info.add(rst.getString("recruitment_id"));// 0
						job_code_info.add(rst.getString("job_code"));// 1
						job_code_info.add(uF.showData(rst.getString("custum_designation"), "-"));// 2
						job_code_info.add(uF.showData(rst.getString("no_position"), "0"));// 3
						job_code_info.add(uF.showData(rst.getString("designation_name"), "-")); // 4
						job_code_info.add(uF.parseToBoolean(rst.getString("close_job_status")) + ""); // 5
						job_code_info.add(rst.getString("priority_job_int")); // 6
						job_code_info.add(rst.getString("req_form_type")); // 7
						job_code_info.add(uF.showData(rst.getString("job_title"), "-")); // 8
						job_code_info.add(uF.showData(rst.getString("job_description"), "-")); // 9

						StringBuilder sbESkills = null;
						if (rst.getString("essential_skills") != null) {
							List<String> alESkills = Arrays.asList(rst.getString("essential_skills").split(","));
							for (int i = 0; alESkills != null && i < alESkills.size(); i++) {
								if (sbESkills == null) {
									sbESkills = new StringBuilder();
									sbESkills.append(uF.showData(hmSkillName.get(alESkills.get(i).trim()), ""));
								} else {
									sbESkills.append("," + uF.showData(hmSkillName.get(alESkills.get(i).trim()), ""));
								}
							}
						}
						if (sbESkills == null) {
							sbESkills = new StringBuilder();
						}
						job_code_info.add(sbESkills.toString());// 10

						job_code_info.add(uF.showData(rst.getString("min_exp"), "-")); // 11
						job_code_info.add(uF.showData(rst.getString("max_exp"), "-")); // 12

						Map<String, String> hmWlocation = hmWorkLocation.get(rst.getString("wlocation"));
						if (hmWlocation == null)
							hmWlocation = new HashMap<String, String>();
						job_code_info.add(uF.showData(hmWlocation.get("WL_NAME"), "-")); // 13

						Map<String, String> hmOrg = hmOrgMap.get(rst.getString("org_id"));
						if (hmOrg == null)
							hmOrg = new HashMap<String, String>();
						job_code_info.add(uF.showData(hmOrg.get("ORG_NAME"), "-")); // 14
						job_code_info.add(uF.showData(rst.getString("technology_name"), "-")); // 15
																								// Created
																								// By
																								// Dattatray
																									// Date:21-08-21

						hmJobReport.put(rst.getString("recruitment_id"), job_code_info);
					}
					rst.close();
					pst.close();

					request.setAttribute("recruitmentIDList", recruitmentIDList);
					request.setAttribute("hmJobReport", hmJobReport);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void singleJobDetailsWithAllJobs(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
			con = db.makeConnection(con);

			Map<String, Map<String, String>> hmOrgMap = CF.getOrgDetails(con, uF);
			if (hmOrgMap == null)
				hmOrgMap = new HashMap<String, Map<String, String>>();

			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con);
			if (hmWorkLocation == null)
				hmWorkLocation = new HashMap<String, Map<String, String>>();
			Map<String, String> hmSkillName = CF.getSkillNameMap(con);
			if (hmSkillName == null)
				hmSkillName = new HashMap<String, String>();

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select a.*,cpd.recruitment_id as p_recruitment_id from (select a.*,cpd.recruitment_id as r_recruitment_id from ("
					+ "select designation_name,job_code,recruitment_id,custum_designation,close_job_status,no_position from recruitment_details"
					+ " left join designation_details using(designation_id) where job_approval_status=1 and close_job_status = false ");
			if (uF.parseToInt(getStrRecruitId()) > 0) {
				sbQuery.append(" and recruitment_id != " + uF.parseToInt(getStrRecruitId()));
			}
			if (getStrSearchJob() != null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and upper(designation_name) like '%" + getStrSearchJob().trim().toUpperCase() + "%'");
			}
			sbQuery.append(")a LEFT JOIN (select distinct(recruitment_id) from candidate_application_details) cpd on(cpd.recruitment_id=a.recruitment_id) "
					+ "order by close_job_status,cpd.recruitment_id desc) a LEFT JOIN (select distinct(recruitment_id) from panel_interview_details "
					+ "where panel_emp_id is not null) cpd on(cpd.recruitment_id=a.recruitment_id) order by close_job_status,r_recruitment_id desc,cpd.recruitment_id desc");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst123=======>" + pst);
			rst = pst.executeQuery();
			StringBuilder recID1 = new StringBuilder();
			StringBuilder recID2 = new StringBuilder();
			StringBuilder recID3 = new StringBuilder();
			StringBuilder recID4 = new StringBuilder();
			StringBuilder recID5 = new StringBuilder();
			while (rst.next()) {
				if ((rst.getString("p_recruitment_id") == null || rst.getString("p_recruitment_id").equals(""))
						&& (rst.getString("r_recruitment_id") == null || rst.getString("r_recruitment_id").equals(""))
						&& uF.parseToBoolean(rst.getString("close_job_status")) == false) {
					recID1.append(rst.getString("recruitment_id").trim());
					recID1.append(",");
				} else if (rst.getString("p_recruitment_id") != null && rst.getString("r_recruitment_id") == null
						&& uF.parseToBoolean(rst.getString("close_job_status")) == false) {
					recID2.append(rst.getString("recruitment_id").trim());
					recID2.append(",");
				} else if (rst.getString("p_recruitment_id") == null && rst.getString("r_recruitment_id") != null
						&& uF.parseToBoolean(rst.getString("close_job_status")) == false) {
					recID3.append(rst.getString("recruitment_id").trim());
					recID3.append(",");
				} else if (rst.getString("p_recruitment_id") != null && rst.getString("r_recruitment_id") != null
						&& uF.parseToBoolean(rst.getString("close_job_status")) == false) {
					recID4.append(rst.getString("recruitment_id").trim());
					recID4.append(",");
				} else if (uF.parseToBoolean(rst.getString("close_job_status")) == true) {
					recID5.append(rst.getString("recruitment_id").trim());
					recID5.append(",");
				}
			}
			rst.close();
			pst.close();

			StringBuilder appendallID = new StringBuilder();
			appendallID.append(recID1);
			appendallID.append(recID2);
			appendallID.append(recID3);
			appendallID.append(recID4);
			appendallID.append(recID5);

			// System.out.println("appendallID ===>> " +
			// appendallID.toString());

			List<String> recruitmentIDList = Arrays.asList(appendallID.toString().split(","));
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
			if(hmFeatureUserTypeId==null) hmFeatureUserTypeId = new HashMap<String, List<String>>();
			if(uF.parseToBoolean(hmFeatureStatus.get(F_INSTANCE_NAMEWISE_FEATURE)) && hmFeatureUserTypeId.get(F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS")!=null && hmFeatureUserTypeId.get(F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS").contains(INTELIMENT)) {
				
			} else {
				int proCnt = recruitmentIDList.size();
				// System.out.println("proCnt======>"+proCnt);
				request.setAttribute("proCnt", proCnt + "");
				// System.out.println("recruitmentIDList ===>> " +
				// recruitmentIDList);
				if (recruitmentIDList.size() > 0) {
					int proCount = recruitmentIDList.size() / 10;
					if (recruitmentIDList.size() % 10 != 0) {
						proCount++;
					}
					request.setAttribute("proCount", proCount + "");
					if (recruitmentIDList.size() > 10) {
						int nStart = 0;
						int nEnd = 10;
						if (uF.parseToInt(getMinLimit()) > 0) {
							nStart = uF.parseToInt(getMinLimit());
							nEnd = uF.parseToInt(getMinLimit()) + 10;
						}
	
						if (nEnd > recruitmentIDList.size()) {
							nEnd = recruitmentIDList.size();
						}
	
						recruitmentIDList = recruitmentIDList.subList(nStart, nEnd);
					}
				}
			}
			
			// System.out.println("recruitmentIDList.size() ===>> " +
			// recruitmentIDList.size());

			if (recruitmentIDList.size() > 0) {
				StringBuilder sbRecruitId = null;
				for (String strId : recruitmentIDList) {
					if (strId.trim().length() > 0) {
						if (sbRecruitId == null) {
							sbRecruitId = new StringBuilder();
							sbRecruitId.append(strId);
						} else {
							sbRecruitId.append("," + strId);
						}
					}
				}

				if (sbRecruitId != null) {
					sbQuery = new StringBuilder();
					//Created by Dattatray Date:24-08-21 Note: Added rt.technology_name
					sbQuery.append(
							"select designation_name,job_code,recruitment_id,custum_designation,close_job_status,no_position,priority_job_int,req_form_type,"
									+ "job_title,recruitment_details.job_description,min_exp,max_exp,recruitment_details.wlocation,recruitment_details.org_id,essential_skills,rt.technology_name "
									+ "from recruitment_details left join designation_details using(designation_id) "
									+ " left join recruitment_technology rt on recruitment_technology_id=technology_id"//Created by Dattatray Date:24-08-21 Note: left join and rt.technology_name
									+ " where job_approval_status=1 and close_job_status = false ");
					sbQuery.append(" and recruitment_id in (" + sbRecruitId.toString() + ")");
					pst = con.prepareStatement(sbQuery.toString());
//					 System.out.println("pst ===>> " + pst);
					rst = pst.executeQuery();
					Map<String, List<String>> hmJobReport = new HashMap<String, List<String>>();
					while (rst.next()) {
						List<String> job_code_info = new ArrayList<String>();
						job_code_info.add(rst.getString("recruitment_id"));// 0
						job_code_info.add(rst.getString("job_code"));// 1
						job_code_info.add(uF.showData(rst.getString("custum_designation"), "-"));// 2
						job_code_info.add(uF.showData(rst.getString("no_position"), "0"));// 3
						job_code_info.add(uF.showData(rst.getString("designation_name"), "-")); // 4
						job_code_info.add(uF.parseToBoolean(rst.getString("close_job_status")) + ""); // 5
						job_code_info.add(rst.getString("priority_job_int")); // 6
						job_code_info.add(rst.getString("req_form_type")); // 7
						job_code_info.add(uF.showData(rst.getString("job_title"), "-")); // 8
						job_code_info.add(uF.showData(rst.getString("job_description"), "-")); // 9

						StringBuilder sbESkills = null;
						if (rst.getString("essential_skills") != null) {
							List<String> alESkills = Arrays.asList(rst.getString("essential_skills").split(","));
							for (int i = 0; alESkills != null && i < alESkills.size(); i++) {
								if (sbESkills == null) {
									sbESkills = new StringBuilder();
									sbESkills.append(uF.showData(hmSkillName.get(alESkills.get(i).trim()), ""));
								} else {
									sbESkills.append(", " + uF.showData(hmSkillName.get(alESkills.get(i).trim()), ""));//<!-- Created By Dattatray Date:24-08-21 Note : spacing between "," -->
								}
							}
						}
						if (sbESkills == null) {
							sbESkills = new StringBuilder();
						}
						job_code_info.add(sbESkills.toString());// 10

						job_code_info.add(uF.showData(rst.getString("min_exp"), "-")); // 11
						job_code_info.add(uF.showData(rst.getString("max_exp"), "-")); // 12

						Map<String, String> hmWlocation = hmWorkLocation.get(rst.getString("wlocation"));
						if (hmWlocation == null)
							hmWlocation = new HashMap<String, String>();
						job_code_info.add(uF.showData(hmWlocation.get("WL_NAME"), "-")); // 13

						Map<String, String> hmOrg = hmOrgMap.get(rst.getString("org_id"));
						if (hmOrg == null)
							hmOrg = new HashMap<String, String>();
						job_code_info.add(uF.showData(hmOrg.get("ORG_NAME"), "-")); // 14
						job_code_info.add(uF.showData(rst.getString("technology_name"), "-")); // 15 Created By Dattatray  Date:24-08-21
						hmJobReport.put(rst.getString("recruitment_id"), job_code_info);
					}
					rst.close();
					pst.close();

					request.setAttribute("recruitmentIDList", recruitmentIDList);
					request.setAttribute("hmJobReport", hmJobReport);
				}

				sbQuery = new StringBuilder();
				//Created by Dattatray Date:24-08-21 Note: Added rt.technology_name
				sbQuery.append("select designation_name,job_code,recruitment_id,custum_designation,close_job_status,no_position,priority_job_int,req_form_type,"
						+ "job_title,recruitment_details.job_description,min_exp,max_exp,recruitment_details.wlocation,recruitment_details.org_id,essential_skills,rt.technology_name "
						+ "from recruitment_details left join designation_details using(designation_id)"
						+ " left join recruitment_technology rt on recruitment_technology_id=technology_id"//Created by Dattatray Date:24-08-21 Note: left join and rt.technology_name
						+ " where job_approval_status=1 and close_job_status = false ");
				sbQuery.append(" and recruitment_id = " + uF.parseToInt(getStrRecruitId()));
				pst = con.prepareStatement(sbQuery.toString());
//				 System.out.println("pst12 ===>> " + pst);
				rst = pst.executeQuery();
				Map<String, List<String>> hmSingleJobReport = new HashMap<String, List<String>>();
				while (rst.next()) {
					List<String> job_code_info = new ArrayList<String>();
					job_code_info.add(rst.getString("recruitment_id"));// 0
					job_code_info.add(rst.getString("job_code"));// 1
					job_code_info.add(uF.showData(rst.getString("custum_designation"), "-"));// 2
					job_code_info.add(uF.showData(rst.getString("no_position"), "0"));// 3
					job_code_info.add(uF.showData(rst.getString("designation_name"), "-")); // 4
					job_code_info.add(uF.parseToBoolean(rst.getString("close_job_status")) + ""); // 5
					job_code_info.add(rst.getString("priority_job_int")); // 6
					job_code_info.add(rst.getString("req_form_type")); // 7
					job_code_info.add(uF.showData(rst.getString("job_title"), "-")); // 8
					job_code_info.add(uF.showData(rst.getString("job_description"), "-")); // 9

					StringBuilder sbESkills = null;
					if (rst.getString("essential_skills") != null) {
						List<String> alESkills = Arrays.asList(rst.getString("essential_skills").split(","));
						for (int i = 0; alESkills != null && i < alESkills.size(); i++) {
							if (sbESkills == null) {
								sbESkills = new StringBuilder();
								sbESkills.append(uF.showData(hmSkillName.get(alESkills.get(i).trim()), ""));
							} else {
								sbESkills.append(", " + uF.showData(hmSkillName.get(alESkills.get(i).trim()), ""));//<!-- Created By Dattatray Date:24-08-21 Note : spacing between "," -->
							}
						}
					}
					if (sbESkills == null) {
						sbESkills = new StringBuilder();
					}
					job_code_info.add(sbESkills.toString());// 10

					job_code_info.add(uF.showData(rst.getString("min_exp"), "-")); // 11
					job_code_info.add(uF.showData(rst.getString("max_exp"), "-")); // 12

					Map<String, String> hmWlocation = hmWorkLocation.get(rst.getString("wlocation"));
					if (hmWlocation == null)
						hmWlocation = new HashMap<String, String>();
					job_code_info.add(uF.showData(hmWlocation.get("WL_NAME"), "-")); // 13

					Map<String, String> hmOrg = hmOrgMap.get(rst.getString("org_id"));
					if (hmOrg == null)
						hmOrg = new HashMap<String, String>();
					job_code_info.add(uF.showData(hmOrg.get("ORG_NAME"), "-")); // 14
					job_code_info.add(uF.showData(rst.getString("technology_name"), "-")); // 15 Created By Dattatray  Date:24-08-21
					
					hmSingleJobReport.put(rst.getString("recruitment_id"), job_code_info);
				}
				rst.close();
				pst.close();
				request.setAttribute("hmSingleJobReport", hmSingleJobReport);

			}

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

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}

	public String getBtnSubmit() {
		return btnSubmit;
	}

	public void setBtnSubmit(String btnSubmit) {
		this.btnSubmit = btnSubmit;
	}

	public String getBtnReset() {
		return btnReset;
	}

	public void setBtnReset(String btnReset) {
		this.btnReset = btnReset;
	}

	public String getStrRecruitId() {
		return strRecruitId;
	}

	public void setStrRecruitId(String strRecruitId) {
		this.strRecruitId = strRecruitId;
	}

	public String getRefEmpId() {
		return refEmpId;
	}

	public void setRefEmpId(String refEmpId) {
		this.refEmpId = refEmpId;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getStrCategory() {
		return strCategory;
	}

	public void setStrCategory(String strCategory) {
		this.strCategory = strCategory;
	}

}