package com.konnect.jpms.recruitment;

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

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.task.FillProjectList;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectResourceRequests extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;

	String strSessionEmpId = null;
	String strEmpOrgId = null; 
	
	private String f_financialYearStart; 
	private String f_financialYearEnd;
	
	private List<FillFinancialYears> financialYearList;
	private List<FillOrganisation> orgList;
	private List<FillSkills> skillsList;
	private List<FillProjectList> projectList;
	private List<FillDesig> desigList;
	private String f_org;
	private String f_strFinancialYear;
	String strSkill;
	String strDesig;
	String strProject;
	String[] strSkills;
	String[] strProjects;
	private String adStatus;
	private String proResReqId;
	
	private String alertStatus;
	private String alert_type;
	
	private String alertID;
	private String currUserType;

	private String fromPage;
	private String type;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);

		orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
		if(getF_org()==null || getF_org().equals("") || getF_org().equalsIgnoreCase("null")) {
			setF_org(strEmpOrgId);
		}
		if(getStrSkill() != null && !getStrSkill().equals("")) {
			setStrSkills(getStrSkill().split(","));
		} else {
			setStrSkills(null);
		}
		if(getStrProject() != null && !getStrProject().equals("")) {
			setStrProjects(getStrProject().split(","));
		} else {
			setStrProjects(null);
		}
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		if(getType()!=null && getType().equals("ApproveDeny")) {
			alignResourceReqWithDesig(uF);
			return "ajax";
		} else {
			if(getType()!=null && getType().equals("BulkApproveDeny")) {
				alignAllResourceReqWithDesig(uF);
			}
			viewAllProjectResourceRequestList(uF);
			getSelectedFilter(uF);
//			System.out.println("getFromPage() ========>> " + getFromPage());
			return LOAD;
		}
	}

	
	private void alignAllResourceReqWithDesig(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
//			String[] strSendLogin = request.getParameterValues("strSendLogin");
//			String[] strDesig = request.getParameterValues("strDesig");
			String[] proResReqId = request.getParameterValues("proResReqId");
			setStrDesig(request.getParameter("strAllDesig"));
			for(int i=0; proResReqId!=null && i<proResReqId.length; i++) {
				setProResReqId(proResReqId[i]);
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("update resource_plan_request_details set desig_id=?, approve_status=?, approved_by=?,approve_date=? where resource_plan_request_id=?");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getStrDesig()));
				pst.setInt(2, uF.parseToInt(getAdStatus()));
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.setInt(5, uF.parseToInt(getProResReqId()));
				pst.executeUpdate();
				pst.close();
				System.out.println("pst ===>> " + pst);
				
				if(uF.parseToInt(getAdStatus())==1) {
					List<String> alProResReqData = new ArrayList<String>();
					sbQuery = new StringBuilder();
					sbQuery.append("select * from resource_plan_request_details where resource_plan_request_id=?");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(getProResReqId()));
					System.out.println("pst ===>> " + pst);
					rs = pst.executeQuery();
					while(rs.next()) {
						alProResReqData.add(rs.getString("req_month"));
						alProResReqData.add(rs.getString("req_year"));
						alProResReqData.add(rs.getString("fy_start"));
						alProResReqData.add(rs.getString("fy_end"));
					}
					rs.close();
					pst.close();
					
					
					sbQuery = new StringBuilder();
					sbQuery.append("select pro_resource_req from resource_planner_details where designation_id=? and rmonth=? and ryear=? and financial_year_from=? and financial_year_to=? ");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(getStrDesig()));
					pst.setInt(2, uF.parseToInt(alProResReqData.get(0)));
					pst.setInt(3, uF.parseToInt(alProResReqData.get(1)));
					pst.setDate(4, uF.getDateFormat(alProResReqData.get(2), DBDATE));
					pst.setDate(5, uF.getDateFormat(alProResReqData.get(3), DBDATE));
					System.out.println("pst ===>> " + pst);
					rs = pst.executeQuery();
					int intProResReq=0;
					boolean flag = false;
					while(rs.next()) {
						intProResReq = rs.getInt("pro_resource_req");
						flag = true;
					}
					rs.close();
					pst.close();
					intProResReq++;
					
					if(flag) {	
						sbQuery = new StringBuilder();
						sbQuery.append("update resource_planner_details set pro_resource_req=? where designation_id=? and rmonth=? and ryear=? and financial_year_from=? and financial_year_to=? ");
						pst = con.prepareStatement(sbQuery.toString());
						pst.setInt(1, intProResReq);
						pst.setInt(2, uF.parseToInt(getStrDesig()));
						pst.setInt(3, uF.parseToInt(alProResReqData.get(0)));
						pst.setInt(4, uF.parseToInt(alProResReqData.get(1)));
						pst.setDate(5, uF.getDateFormat(alProResReqData.get(2), DBDATE));
						pst.setDate(6, uF.getDateFormat(alProResReqData.get(3), DBDATE));
						pst.executeUpdate();
						pst.close();
					} else {
						sbQuery = new StringBuilder();
						sbQuery.append("insert into resource_planner_details (designation_id,financial_year_from,financial_year_to," +
						 	"rmonth,ryear,added_by,added_date,pro_resource_req) values (?,?,?,?,?,?,?,?)"); //wlocation_id, ,?
						pst=con.prepareStatement(sbQuery.toString());
						pst.setInt(1, uF.parseToInt(getStrDesig()));
						pst.setDate(2, uF.getDateFormat(alProResReqData.get(2), DBDATE));
						pst.setDate(3, uF.getDateFormat(alProResReqData.get(3), DBDATE)); 
						pst.setInt(4, uF.parseToInt(alProResReqData.get(0)));
						pst.setInt(5, uF.parseToInt(alProResReqData.get(1)));
						pst.setInt(6, uF.parseToInt(strSessionEmpId));
						pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
		//				pst.setInt(8, uF.parseToInt(userLocation));
						pst.setInt(8, intProResReq);
						pst.execute();
						pst.close();
					}
				}
			}
			session.setAttribute(MESSAGE, SUCCESSM+"Project resource request approved successfully."+END);
		} catch (Exception e) {
			session.setAttribute(MESSAGE, SUCCESSM+"Project resource request approved failed."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void alignResourceReqWithDesig(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("update resource_plan_request_details set desig_id=?, approve_status=?, approved_by=?,approve_date=? where resource_plan_request_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrDesig()));
			pst.setInt(2, uF.parseToInt(getAdStatus()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(5, uF.parseToInt(getProResReqId()));
			pst.executeUpdate();
			pst.close();
			System.out.println("pst ===>> " + pst);
//			request.setAttribute("STATUS_MSG", "Aligned");
			
			if(uF.parseToInt(getAdStatus())==1) {
				List<String> alProResReqData = new ArrayList<String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select * from resource_plan_request_details where resource_plan_request_id=?");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getProResReqId()));
				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					alProResReqData.add(rs.getString("req_month"));
					alProResReqData.add(rs.getString("req_year"));
					alProResReqData.add(rs.getString("fy_start"));
					alProResReqData.add(rs.getString("fy_end"));
				}
				rs.close();
				pst.close();
				
				
				sbQuery = new StringBuilder();
				sbQuery.append("select pro_resource_req from resource_planner_details where designation_id=? and rmonth=? and ryear=? and financial_year_from=? and financial_year_to=? ");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getStrDesig()));
				pst.setInt(2, uF.parseToInt(alProResReqData.get(0)));
				pst.setInt(3, uF.parseToInt(alProResReqData.get(1)));
				pst.setDate(4, uF.getDateFormat(alProResReqData.get(2), DBDATE));
				pst.setDate(5, uF.getDateFormat(alProResReqData.get(3), DBDATE));
				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				int intProResReq=0;
				boolean flag = false;
				while(rs.next()) {
					intProResReq = rs.getInt("pro_resource_req");
					flag = true;
				}
				rs.close();
				pst.close();
				intProResReq++;
				
				if(flag) {	
					sbQuery = new StringBuilder();
					sbQuery.append("update resource_planner_details set pro_resource_req=? where designation_id=? and rmonth=? and ryear=? and financial_year_from=? and financial_year_to=? ");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, intProResReq);
					pst.setInt(2, uF.parseToInt(getStrDesig()));
					pst.setInt(3, uF.parseToInt(alProResReqData.get(0)));
					pst.setInt(4, uF.parseToInt(alProResReqData.get(1)));
					pst.setDate(5, uF.getDateFormat(alProResReqData.get(2), DBDATE));
					pst.setDate(6, uF.getDateFormat(alProResReqData.get(3), DBDATE));
					pst.executeUpdate();
					pst.close();
				} else {
					sbQuery = new StringBuilder();
					sbQuery.append("insert into resource_planner_details (designation_id,financial_year_from,financial_year_to," +
					 	"rmonth,ryear,added_by,added_date,pro_resource_req) values (?,?,?,?,?,?,?,?)"); //wlocation_id, ,?
					pst=con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(getStrDesig()));
					pst.setDate(2, uF.getDateFormat(alProResReqData.get(2), DBDATE));
					pst.setDate(3, uF.getDateFormat(alProResReqData.get(3), DBDATE)); 
					pst.setInt(4, uF.parseToInt(alProResReqData.get(0)));
					pst.setInt(5, uF.parseToInt(alProResReqData.get(1)));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
	//				pst.setInt(8, uF.parseToInt(userLocation));
					pst.setInt(8, intProResReq);
					pst.execute();
					pst.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if(strUserType != null && !strUserType.equals(MANAGER) && !strUserType.equals(EMPLOYEE)) {
			alFilter.add("ORGANISATION");
			if(getF_org()!=null) {
				String strOrg="";
				for(int i=0;orgList!=null && i<orgList.size();i++) {
					if(getF_org().equals(orgList.get(i).getOrgId())) {
						strOrg=orgList.get(i).getOrgName();
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
		}
		
		alFilter.add("SKILL");
		if(getStrSkills()!=null) {
			String strCal = "";
			for(int i=0;skillsList!=null && i<skillsList.size();i++) {
				if(getStrSkills().equals(skillsList.get(i).getSkillsId())) {
					strCal = skillsList.get(i).getSkillsName();
				}
			}
			if(strCal!=null && !strCal.equals("")) {
				hmFilter.put("SKILL", strCal);
			} else {
				hmFilter.put("SKILL", "All Skills");
			}
		} else {
			hmFilter.put("SKILL", "All Skills");
		}
		
		alFilter.add("PROJECT");
		if(getStrProjects()!=null) {
			String strCal = "";
			for(int i=0;projectList!=null && i<projectList.size();i++) {
				if(getStrProjects().equals(projectList.get(i).getProjectID())) {
					strCal = projectList.get(i).getProjectName();
				}
			}
			if(strCal!=null && !strCal.equals("")) {
				hmFilter.put("PROJECT", strCal);
			} else {
				hmFilter.put("PROJECT", "All Projects");
			}
		} else {
			hmFilter.put("PROJECT", "All Projects");
		}
		
		alFilter.add("FINANCIALYEAR");
		if(getF_strFinancialYear()!=null) {
			String strCal = "";
			for(int i=0;financialYearList!=null && i<financialYearList.size();i++) {
				if(getF_strFinancialYear().equals(financialYearList.get(i).getFinancialYearId())) {
					strCal = financialYearList.get(i).getFinancialYearName();
				}
			}
			if(strCal!=null && !strCal.equals("")) {
				hmFilter.put("FINANCIALYEAR", strCal);
			} else {
				hmFilter.put("FINANCIALYEAR", "-");
			}
		} else {
			hmFilter.put("FINANCIALYEAR", "-");
		}
		
		

		String selectedFilter= CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	
	private void viewAllProjectResourceRequestList(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);

		// List<List<String>> requestList = new ArrayList<List<String>>();
		List<String> requestList = new ArrayList<String>();
		try {

			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));

			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getF_strFinancialYear() != null) {
				strFinancialYearDates = getF_strFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setF_strFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];			 
			}
			
			setF_financialYearStart(strFinancialYearStart);
			setF_financialYearEnd(strFinancialYearEnd);

			
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select * from resource_plan_request_details where fy_start=? and fy_end=? and pro_id in (select pro_id from projectmntnc where org_id=?) and (approved_by=0 or approved_by is null)");
			pst = con.prepareStatement(strQuery.toString());
			pst.setDate(1, uF.getDateFormat(getF_financialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getF_financialYearEnd(), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_org()));
			System.out.println("pst ====>> " + pst);
			rst = pst.executeQuery(); 
			StringBuilder sbProIds = null;
			StringBuilder sbSkillIds = null;
			List<String> alProId = new ArrayList<String>();
			List<String> alSkillId = new ArrayList<String>();
			while (rst.next()) {
				if(!alProId.contains(rst.getString("pro_id"))) {
					if(sbProIds == null) {
						sbProIds= new StringBuilder();
						sbProIds.append(rst.getString("pro_id"));
					} else {
						sbProIds.append(","+rst.getString("pro_id"));
					}
					alProId.add(rst.getString("pro_id"));
				}
				if(!alSkillId.contains(rst.getString("skill_id"))) {
					if(sbSkillIds == null) {
						sbSkillIds= new StringBuilder();
						sbSkillIds.append(rst.getString("skill_id"));
					} else {
						sbSkillIds.append(","+rst.getString("skill_id"));
					}
					alSkillId.add(rst.getString("skill_id"));
				}
			}
			rst.close();
			pst.close();
			if(sbProIds == null) {
				sbProIds= new StringBuilder();
			}
			if(sbSkillIds == null) {
				sbSkillIds= new StringBuilder();
			}
			
			skillsList = new FillSkills(request).fillSkillNameByIds(sbSkillIds.toString());
			projectList = new FillProjectList(request).fillProjectDetailsByProjectIds(sbProIds.toString());
			desigList = new FillDesig(request).fillDesig(uF.parseToInt(getF_org()));
			
			StringBuilder sbDesigList = new StringBuilder();
			for(int i=0; desigList!=null && i<desigList.size(); i++) {
				sbDesigList.append("<option value=\""+desigList.get(i).getDesigId()+"\">"+desigList.get(i).getDesigCodeName()+"</option>");
			}
			request.setAttribute("sbDesigList", sbDesigList.toString());
			
			Map<String, String> hmSkillName = CF.getSkillNameMap(con);
			Map<String, String> hmProjectName = CF.getProjectNameMap(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			strQuery = new StringBuilder();
			strQuery.append("select * from resource_plan_request_details where fy_start=? and fy_end=? and pro_id in (select pro_id from projectmntnc where org_id=?) and (approve_status=0 or approve_status is null) ");
			if (getStrSkills() != null && getStrSkills().length > 0) {
				strQuery.append(" and skill_id in (" + StringUtils.join(getStrSkills(), ",") + ") ");
			}
			if (getStrProjects() != null && getStrProjects().length > 0) {
				strQuery.append(" and pro_id in (" + StringUtils.join(getStrProjects(), ",") + ") ");
			}
			strQuery.append(" order by pro_start_date,skill_id");
			int nCount = 0;
			pst = con.prepareStatement(strQuery.toString());
			pst.setDate(1, uF.getDateFormat(getF_financialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getF_financialYearEnd(), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_org()));
			System.out.println("pst ====>> " + pst);
			rst = pst.executeQuery();
			StringBuilder sbRequirements = new StringBuilder();
			while (rst.next()) {
				sbRequirements.replace(0, sbRequirements.length(), "");
				StringBuilder sbApproveDeny = new StringBuilder();
				if (rst.getInt("approve_status")==0) {
					sbApproveDeny.append("<div style=\"float:right;\" id=\"myDivM" + nCount + "\" > ");
//					if (uF.parseToInt(rst.getString("job_profile_updated_by")) > 0) {
					sbApproveDeny.append("<select name=\"strDesig\" id=\"strDesig"+rst.getString("resource_plan_request_id")+"\" style=\"width:150px !important;\" >" +
							"<option value=\"\">Select Designation</option>"+sbDesigList.toString()+"</select>");
					sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"approveDenyProResReq('"+nCount+"','1','" + rst.getString("resource_plan_request_id")
						+ "');\" ><i class=\"fa fa-check-circle checknew\" style=\"padding-top: 0px !important;\" aria-hidden=\"true\" title=\"Approve\"></i></a> ");
					sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"approveDenyProResReq('"+nCount+"','-1','" + rst.getString("resource_plan_request_id")
						+ "');\" ><i class=\"fa fa-times-circle cross\" style=\"padding-top: 0px !important;\" aria-hidden=\"true\" title=\"Deny\"></i></a> ");
//					}
					sbApproveDeny.append("</div>");
				}
				
//				StringBuilder sbDesignations = new StringBuilder();
//				sbDesignations.append("<select name=\"strDesig"+rst.getString("resource_plan_request_id")+"\" id=\"strDesig"+rst.getString("resource_plan_request_id")+"\" style=\"width:150px !important;\" >" +
//					"<option value=\"\">Select Designation</option>"+sbDesigList.toString()+"</select>");
				
//				<<Skilll>> for <<Min Exp>> to <<Max Exp>> has been raised by <<Initiated by>> for <<Project/Account Name>>on <<Date>> to be fulfilled by <<Date>>
				sbRequirements.append("<div><span style=\"float:left; width: 80%;\"> <input type=\"checkbox\" name=\"proResReqId\" id=\"proResReqId\" value=\""+rst.getString("resource_plan_request_id")+"\" onclick=\"checkAll();\"> A request <b>"+uF.showData(hmSkillName.get(rst.getString("skill_id")), "-")+"</b> for <b>"+uF.showData(rst.getString("min_exp"), "0")
					+"</b> to <b>"+uF.showData(rst.getString("max_exp"), "0")+"</b> years has been raised by <b>"+uF.showData(hmEmpNames.get(rst.getString("requested_by")), "-")
					+"</b> for <b>"+uF.showData(hmProjectName.get(rst.getString("pro_id")), "-")+"</b> on <b>"+uF.getDateFormat(rst.getString("request_date"), DBTIMESTAMP, DATE_FORMAT_STR)
					+"</b> to be fulfilled by <b>"+uF.getDateFormat(rst.getString("pro_start_date"), DBDATE, DATE_FORMAT_STR)+"</b>.</span> "+sbApproveDeny.toString()+"</div>"); //<span style=\"float: right;\">"+ sbDesignations.toString()+"</span>
				requestList.add(sbRequirements.toString());

				nCount++;
			}
			rst.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);

		}
	    
//	    System.out.println("requestList ==>"+requestList.size());
//		System.out.println("requestList ==>"+requestList);
  
		request.setAttribute("requestList", requestList);
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getF_financialYearStart() {
		return f_financialYearStart;
	}

	public void setF_financialYearStart(String f_financialYearStart) {
		this.f_financialYearStart = f_financialYearStart;
	}

	public String getF_financialYearEnd() {
		return f_financialYearEnd;
	}

	public void setF_financialYearEnd(String f_financialYearEnd) {
		this.f_financialYearEnd = f_financialYearEnd;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public String getF_strFinancialYear() {
		return f_strFinancialYear;
	}

	public void setF_strFinancialYear(String f_strFinancialYear) {
		this.f_strFinancialYear = f_strFinancialYear;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
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

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public List<FillSkills> getSkillsList() {
		return skillsList;
	}

	public void setSkillsList(List<FillSkills> skillsList) {
		this.skillsList = skillsList;
	}

	public List<FillProjectList> getProjectList() {
		return projectList;
	}

	public void setProjectList(List<FillProjectList> projectList) {
		this.projectList = projectList;
	}

	public String getStrSkill() {
		return strSkill;
	}

	public void setStrSkill(String strSkill) {
		this.strSkill = strSkill;
	}

	public String getStrProject() {
		return strProject;
	}

	public void setStrProject(String strProject) {
		this.strProject = strProject;
	}

	public String[] getStrSkills() {
		return strSkills;
	}

	public void setStrSkills(String[] strSkills) {
		this.strSkills = strSkills;
	}

	public String[] getStrProjects() {
		return strProjects;
	}

	public void setStrProjects(String[] strProjects) {
		this.strProjects = strProjects;
	}

	public String getAdStatus() {
		return adStatus;
	}

	public void setAdStatus(String adStatus) {
		this.adStatus = adStatus;
	}

	public String getProResReqId() {
		return proResReqId;
	}

	public void setProResReqId(String proResReqId) {
		this.proResReqId = proResReqId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStrDesig() {
		return strDesig;
	}

	public void setStrDesig(String strDesig) {
		this.strDesig = strDesig;
	}

}
