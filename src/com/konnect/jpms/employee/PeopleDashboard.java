package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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

public class PeopleDashboard extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;

	String strSessionEmpId = null;
	UtilityFunctions uF = new UtilityFunctions();

	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, "People's Dashboard");
		request.setAttribute(PAGE, "/jsp/employee/PeopleDashboard.jsp");

		getPeopleDeshboardReport();
		
		return LOAD;

	}

	
	private void getEmployeeSkill(Connection con, UtilityFunctions uF, CommonFunctions CF) {
		
		ResultSet rs = null;
		PreparedStatement pst=null;
		try {
			
			Map<String, String> hmSkillName = CF.getSkillNameMap(con);
//			pst = con.prepareStatement("select count(emp_id) as count, skill_id from skills_description where skill_id is not null group by skill_id"); //emp_id,
			StringBuilder sbQuery = new StringBuilder();
//			pst = con.prepareStatement("select count(emp_id) as count, skill_id from skills_description where skill_id is not null group by skill_id"); //emp_id,
			sbQuery.append("select count(emp_id) as count, skill_id from skills_description where skill_id is not null and emp_id in " +
				"(select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and epd.is_alive=true and wlocation_id > 0 ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(") group by skill_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			StringBuilder sbSkillwisePie = new StringBuilder();
			int skillCount = 0;
			while(rs.next()) {
				skillCount++;
				sbSkillwisePie.append("{'Skill':'"+uF.showData(hmSkillName.get(rs.getString("skill_id")), "").replaceAll("[^a-zA-Z0-9]", "")+"', " +
						"'cnt': "+rs.getInt("count")+"},");
			}
			rs.close();
			pst.close();
			
//			if(i>8) {
//				innerList = new ArrayList<String>();
//				innerList.add("Others");
//				innerList.add(""+otherCnt);
//				skillwiseEmpCountGraphList.add(innerList);
//			}
			if(sbSkillwisePie != null && sbSkillwisePie.length()>1) {
				sbSkillwisePie.replace(0, sbSkillwisePie.length(), sbSkillwisePie.substring(0, sbSkillwisePie.length()-1));
	        }
			request.setAttribute("skillCount", ""+skillCount);
			request.setAttribute("sbSkillwisePie", sbSkillwisePie.toString());
			
//			request.setAttribute("skillwiseEmpCountGraphList", skillwiseEmpCountGraphList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	public void getDepartmentEmployeeCount(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		try {
			
			Map<String, String> hmDepartOrgName = CF.getOrgNameDepartIdwise(con);
//			pst = con.prepareStatement("select count(*),depart_id from employee_personal_details epd, employee_official_details eod " +
//				"where epd.emp_per_id = eod.emp_id and epd.is_alive=true group by depart_id");
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*),depart_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id and epd.is_alive=true ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" group by depart_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			int departCount = 0;
			Map<String, String> hmDepartmentEmployeeCount = new HashMap<String, String>();
			while (rs.next()) {
				departCount++;
				hmDepartmentEmployeeCount.put(rs.getString("depart_id"), rs.getString("count"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("departCount", ""+departCount);
			request.setAttribute("hmDepartmentEmployeeCount", hmDepartmentEmployeeCount);
			request.setAttribute("hmDepartOrgName", hmDepartOrgName);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void getWlocationEmployeeCount(Connection con, UtilityFunctions uF) {
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			Map<String, String> hmWLocOrgName = CF.getOrgNameWLocationIdwise(con);
//			pst = con.prepareStatement("select count(eod.emp_id) as count, wlocation_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and epd.is_alive=true and wlocation_id > 0 group by wlocation_id");
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(eod.emp_id) as count, wlocation_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id and epd.is_alive=true and wlocation_id > 0 ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" group by wlocation_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			int wLocationCount = 0;
			Map<String, String> hmWLocationEmployeeCount = new HashMap<String, String>();
			while (rs.next()) {
				wLocationCount++;
				hmWLocationEmployeeCount.put(rs.getString("wlocation_id"), uF.formatIntoComma(rs.getDouble("count")) );
			}
			rs.close();
			pst.close();
			request.setAttribute("wLocationCount", ""+wLocationCount);
			request.setAttribute("hmWLocationEmployeeCount", hmWLocationEmployeeCount);
			request.setAttribute("hmWLocOrgName", hmWLocOrgName);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public void getPeopleDeshboardReport() {

		List<List<String>> allWorkingPeopleReport = new ArrayList<List<String>>();
		List<List<String>> allEmploymentTypeReport = new ArrayList<List<String>>();
		List<List<String>> allGenderPeopleReport = new ArrayList<List<String>>();
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			int unassignCandiCnt = getUnassignedCandidateCount(con);
			getEmployeeSkill(con, uF, CF);
			getDepartmentEmployeeCount(con, uF);
			getWlocationEmployeeCount(con, uF);
			Map<String, String> hmDepartmentMap = CF.getDeptMap(con);
			Map<String, Map<String, String>> hmWorkLocationMap = CF.getWorkLocationMap(con);
			
			Map<String, String> hmOrgName = new HashMap<String, String>();
			pst = con.prepareStatement("select org_id,org_name from org_details order by org_id");
			rst = pst.executeQuery();
			while (rst.next()) {
				hmOrgName.put(rst.getString("org_id"), rst.getString("org_name"));
			}
			rst.close();
			pst.close();

			Map<String, String> hmEmpTypewiseEmp = getEmploymentTypewiseEmployeeOrgwise(con);
			Map<String, String> hmEmpStatuswiseEmp = getEmploymentStatuswiseEmployeeOrgwise(con);
			Map<String, String> hmGenderwiseEmp = getGenderwiseEmployeeOrgwise(con);
			
			Map<String, String> hmPendingEmp = getPendingEmployeeOrgwise(con);
			Map<String, String> hmExEmp = getExEmployeeOrgwise(con);
			Map<String, String> hmLiveUserEmp = getLiveUserCountOrgwise(con);
			
			pst = con.prepareStatement("select org_id from org_details order by org_id");
			rst = pst.executeQuery();
			// System.out.println("pst  ==== >>>> "+pst);
			while (rst.next()) {
//				List<String> allEmpreport = new ArrayList<String>();
//				allEmpreport = getEmpReport(con, rst.getString("org_id"));
				
//				int candiCnt = getCandidateOrgwiseCount(con, rst.getString("org_id"));
				
				List<String> innerWPList = new ArrayList<String>();
				innerWPList.add(rst.getString("org_id"));
				innerWPList.add(uF.showData(hmOrgName.get(rst.getString("org_id")), ""));
				innerWPList.add(hmPendingEmp.get(rst.getString("org_id"))); //Pending emp 2
				innerWPList.add(uF.showData(hmEmpStatuswiseEmp.get(rst.getString("org_id")+"_"+PROBATION), "0")); //Probation emp 3
				innerWPList.add(uF.showData(hmEmpStatuswiseEmp.get(rst.getString("org_id")+"_"+PERMANENT), "0")); //Permanent emp 4
				innerWPList.add(uF.showData(hmEmpStatuswiseEmp.get(rst.getString("org_id")+"_"+TEMPORARY), "0")); //Permanent emp 5
				innerWPList.add(uF.showData(hmEmpStatuswiseEmp.get(rst.getString("org_id")+"_"+RESIGNED), "0")); //Resigned emp 6
				innerWPList.add(hmExEmp.get(rst.getString("org_id"))); //Ex emp 7
				innerWPList.add(hmLiveUserEmp.get(rst.getString("org_id"))); //8
//				innerPlpList.add(candiCnt + "");
				allWorkingPeopleReport.add(innerWPList);
				
				List<String> innerEmpTypeList = new ArrayList<String>();
				innerEmpTypeList.add(rst.getString("org_id"));
				innerEmpTypeList.add(uF.showData(hmOrgName.get(rst.getString("org_id")), ""));
				if(hmFeatureStatus != null ) {
					if(hmFeatureStatus.get(F_USERTYPE_GENERAL) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_GENERAL))) {
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_FULLTIME"), "0")); //Full time emp 2
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_PARTTIME"), "0")); //Part time emp 3
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_CONSULTANT"), "0")); //Consultant emp 4
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_CONTRACTUAL"), "0")); //Contractual emp 5
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_INTERN"), "0")); //Intern emp 6
					}
					if(hmFeatureStatus.get(F_USERTYPE_HEALTH_CARE) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_HEALTH_CARE))) {
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_REGULAR"), "0")); //Regular emp 2
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_CONTRACT"), "0")); //Contract emp 3
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_PROFESSIONAL"), "0")); //Professional emp 4
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_STIPEND"), "0")); //Stipend emp 5
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_SCHOLARSHIP"), "0")); //Scholarship emp 6
					}
					if(hmFeatureStatus.get(F_USERTYPE_FINANCE) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_FINANCE))) {
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_FULLTIME"), "0")); //Full time emp 2
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_PARTTIME"), "0")); //Part time emp 3
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_CONSULTANT"), "0")); //Consultant emp 4
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_TEMPORARY"), "0")); //Temporary emp 5
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_ARTICLE"), "0")); //Article emp 6
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_PARTNER"), "0")); //Partner emp 7
					}
				} else {
					innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_FULLTIME"), "0")); //Full time emp 2
					innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_PARTTIME"), "0")); //Part time emp 3
					innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_CONSULTANT"), "0")); //Consultant emp 4
					innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_CONTRACTUAL"), "0")); //Contractual emp 5
					innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_INTERN"), "0")); //Intern emp 6
				}
//									***************** KPCA ************************
				/*innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_ARTICLE"), "0")); //ARTICLE emp 5
				innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_PARTNER"), "0")); //PARTNER emp 6
				innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_TEMPORARY"), "0")); //TEMPORARY emp 7 */
				
				allEmploymentTypeReport.add(innerEmpTypeList);
				
				List<String> innerGenderList = new ArrayList<String>();
				innerGenderList.add(rst.getString("org_id"));
				innerGenderList.add(uF.showData(hmOrgName.get(rst.getString("org_id")), ""));
				innerGenderList.add(uF.showData(hmGenderwiseEmp.get(rst.getString("org_id")+"_MALE"), "0")); //Male emp 2
				innerGenderList.add(uF.showData(hmGenderwiseEmp.get(rst.getString("org_id")+"_FEMALE"), "0")); //Female emp 3
				innerGenderList.add(uF.showData(hmGenderwiseEmp.get(rst.getString("org_id")+"_OTHER"), "0")); //Other emp 4
				allGenderPeopleReport.add(innerGenderList);
				
			}
			rst.close();
			pst.close();

			request.setAttribute("unassignCandiCnt", ""+unassignCandiCnt);
			request.setAttribute("allWorkingPeopleReport", allWorkingPeopleReport);
			request.setAttribute("allEmploymentTypeReport", allEmploymentTypeReport);
			request.setAttribute("allGenderPeopleReport", allGenderPeopleReport);
			
			request.setAttribute("hmDepartmentMap", hmDepartmentMap);  
			request.setAttribute("hmWorkLocationMap", hmWorkLocationMap);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
	private Map<String, String> getGenderwiseEmployeeOrgwise(Connection con) {

		Map<String, String> hmGenderwiseEmps = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
//			pst = con.prepareStatement("select count(*) as count,emp_gender,org_id from employee_official_details eod, employee_personal_details epd where org_id>0 and " +
//				" eod.emp_id = epd.emp_per_id and epd.approved_flag = true and epd.is_alive = true and epd.emp_filled_flag = true group by emp_gender, org_id");
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as count,emp_gender,org_id from employee_official_details eod, employee_personal_details epd where org_id>0 and " +
				"eod.emp_id = epd.emp_per_id and epd.approved_flag = true and epd.is_alive = true and epd.emp_filled_flag = true ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" group by emp_gender, org_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(orgid));
			rst = pst.executeQuery();
			// System.out.println("pst  ==== >>>> "+pst);
			while (rst.next()) {
				
				if (rst.getString("emp_gender") != null && rst.getString("emp_gender").trim().equals("M")) { //Male 
					hmGenderwiseEmps.put(rst.getString("org_id")+"_MALE", rst.getString("count"));
				} else if (rst.getString("emp_gender") != null && rst.getString("emp_gender").trim().equals("F")) { //Female 
					hmGenderwiseEmps.put(rst.getString("org_id")+"_FEMALE", rst.getString("count"));
				} else if (rst.getString("emp_gender") != null && !rst.getString("emp_gender").trim().equals("M") && !rst.getString("emp_gender").trim().equals("F")) { //Other
					hmGenderwiseEmps.put(rst.getString("org_id")+"_OTHER", rst.getString("count"));
				}
			}
			rst.close();
			pst.close();
//			 System.out.println("hmGenderwiseEmps ========= >> " + hmGenderwiseEmps);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null) {
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmGenderwiseEmps;
	}
	
	

	private Map<String, String> getPendingEmployeeOrgwise(Connection con) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String, String> hmPendingEmp = new HashMap<String, String>();
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as count,org_id from employee_official_details eod, employee_personal_details epd where " +
				" eod.emp_id = epd.emp_per_id and epd.approved_flag = false and epd.is_alive = false and epd.emp_filled_flag = true ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" group by org_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(orgid));
			rst = pst.executeQuery();
			// System.out.println("pst  ==== >>>> "+pst);
			while (rst.next()) {
				hmPendingEmp.put(rst.getString("org_id"), rst.getInt("count")+"");
			}
			rst.close();
			pst.close();
//			 System.out.println("Pending count ========= >> " + count);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null) {
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmPendingEmp;
	}
	
	
	private Map<String, String> getEmploymentStatuswiseEmployeeOrgwise(Connection con) {

		Map<String, String> hmEmpStatuswiseEmps = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as count,emp_status,org_id from employee_official_details eod, employee_personal_details epd where org_id>0 and " +
				"eod.emp_id = epd.emp_per_id and epd.approved_flag = true and epd.is_alive = true and epd.emp_filled_flag = true ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" group by emp_status, org_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(orgid));
			rst = pst.executeQuery();
			// System.out.println("pst  ==== >>>> "+pst);
			while (rst.next()) {
				if (rst.getString("emp_status") != null && rst.getString("emp_status").trim().equals(PROBATION)) { 
					hmEmpStatuswiseEmps.put(rst.getString("org_id")+"_"+PROBATION, rst.getString("count"));
				} else if (rst.getString("emp_status") != null && rst.getString("emp_status").trim().equals(PERMANENT)) { 
					hmEmpStatuswiseEmps.put(rst.getString("org_id")+"_"+PERMANENT, rst.getString("count"));
				} else if (rst.getString("emp_status") != null && rst.getString("emp_status").trim().equals(TEMPORARY)) { 
					hmEmpStatuswiseEmps.put(rst.getString("org_id")+"_"+TEMPORARY, rst.getString("count"));
				} else if (rst.getString("emp_status") != null && rst.getString("emp_status").trim().equals(RESIGNED)) { 
					hmEmpStatuswiseEmps.put(rst.getString("org_id")+"_"+RESIGNED, rst.getString("count"));
				}
			}
			rst.close();
			pst.close();
//			 System.out.println("hmEmpStatuswiseEmps ========= >> " + hmEmpStatuswiseEmps);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null) {
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmEmpStatuswiseEmps;
	}

	
	private Map<String, String> getEmploymentTypewiseEmployeeOrgwise(Connection con) {

		Map<String, String> hmEmpTypewiseEmps = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as count,emptype,org_id from employee_official_details eod, employee_personal_details epd where org_id>0 and " +
				"eod.emp_id = epd.emp_per_id and epd.approved_flag = true and epd.is_alive = true and epd.emp_filled_flag = true ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" group by emptype, org_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(orgid));
			rst = pst.executeQuery();
//			System.out.println("pst  ====>>>> "+pst);
			while (rst.next()) {
				if(hmFeatureStatus != null ) {
					if(hmFeatureStatus.get(F_USERTYPE_GENERAL) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_GENERAL))) {
						if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("FT")) { //Full Time
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_FULLTIME", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("PT")) { //Part Time
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_PARTTIME", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("CO")) { //Consultant
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_CONSULTANT", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("CON")) { //Contractual
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_CONTRACTUAL", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("I")) { //Intern
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_INTERN", rst.getString("count"));
						}
					}
					
					if(hmFeatureStatus.get(F_USERTYPE_HEALTH_CARE) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_HEALTH_CARE))) {
						if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("R")) { //Regular
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_REGULAR", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("CT")) { //Contract
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_CONTRACT", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("PF")) { //Professional
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_PROFESSIONAL", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("ST")) { //Stipend
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_STIPEND", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("SCH")) { //Scholarship
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_SCHOLARSHIP", rst.getString("count"));
						}
					}
					
					if(hmFeatureStatus.get(F_USERTYPE_FINANCE) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_FINANCE))) {
						if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("FT")) { //Full Time
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_FULLTIME", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("PT")) { //Part Time
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_PARTTIME", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("CO")) { //Consultant
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_CONSULTANT", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("C")) { //Temporary
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_TEMPORARY", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("AT")) { //Article
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_ARTICLE", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("P")) { //Partner
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_PARTNER", rst.getString("count"));
						}
					}
					
				} else {
					if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("FT")) { //Full Time
						hmEmpTypewiseEmps.put(rst.getString("org_id")+"_FULLTIME", rst.getString("count"));
					} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("PT")) { //Part Time
						hmEmpTypewiseEmps.put(rst.getString("org_id")+"_PARTTIME", rst.getString("count"));
					} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("CO")) { //Consultant
						hmEmpTypewiseEmps.put(rst.getString("org_id")+"_CONSULTANT", rst.getString("count"));
					} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("CON")) { //Contractual
						hmEmpTypewiseEmps.put(rst.getString("org_id")+"_CONTRACTUAL", rst.getString("count"));
					} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("I")) { //Intern
						hmEmpTypewiseEmps.put(rst.getString("org_id")+"_INTERN", rst.getString("count"));
					}
				}
				
				
				/* else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("AT")) { //Article
					hmEmpTypewiseEmps.put(rst.getString("org_id")+"_ARTICLE", rst.getString("count"));
				} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("P")) { //Partner
					hmEmpTypewiseEmps.put(rst.getString("org_id")+"_PARTNER", rst.getString("count"));
				} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("C")) { //Temporary
					hmEmpTypewiseEmps.put(rst.getString("org_id")+"_TEMPORARY", rst.getString("count"));
				}*/
			}
			rst.close();
			pst.close();
//			 System.out.println("allEmpTypewiseEmps ========= >> " + allEmpTypewiseEmps);  CON  CO Intern I
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null) {
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmEmpTypewiseEmps;
	}

	
	private int getCandidateOrgwiseCount(Connection con, String orgid) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		int count = 0;
		try {
			pst = con.prepareStatement("select count(distinct(candidate_id)) as count from candidate_application_details cad where cad.recruitment_id in (" +
					"select recruitment_id from recruitment_details where org_id = ? order by recruitment_id) and candididate_emp_id is null "); 
			pst.setInt(1, uF.parseToInt(orgid));
			rst = pst.executeQuery();
//			System.out.println("pst ====>>>>> " + pst);
			while (rst.next()) {
				count = rst.getInt("count");
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null) {
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return count;
	}
	
	private int getUnassignedCandidateCount(Connection con) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		int count = 0;
		try {
//			pst = con.prepareStatement("select count(emp_per_id) as count from candidate_personal_details cpd where emp_per_id not in (" +
//					"select distinct(candidate_id) from candidate_application_details cad where cad.recruitment_id in (select recruitment_id " +
//					"from recruitment_details order by recruitment_id) order by candidate_id)");
			pst = con.prepareStatement("select count(emp_per_id) as count from candidate_personal_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				count = rst.getInt("count");
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null) {
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return count;
	}

	
	private Map<String, String> getLiveUserCountOrgwise(Connection con) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String, String> hmLiveUserEmp = new HashMap<String, String>();
		try {

//			pst = con.prepareStatement("Select count(*) as count,org_id from employee_official_details eod, user_details ud where " +
//				"eod.emp_id = ud.emp_id and ud.emp_id in (select emp_per_id from employee_personal_details where approved_flag = true " +
//				"and emp_filled_flag = true) group by org_id"); //and status = 'ACTIVE'
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as count,org_id from employee_official_details eod, user_details ud where eod.emp_id = ud.emp_id " +
				"and ud.emp_id in (select emp_per_id from employee_personal_details where approved_flag = true and emp_filled_flag = true) ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" group by org_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(orgid));
			rst = pst.executeQuery();
			while (rst.next()) {
				hmLiveUserEmp.put(rst.getString("org_id"), rst.getInt("count")+"");
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null) {
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmLiveUserEmp;
	}
	
	
	private Map<String, String> getExEmployeeOrgwise(Connection con) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String, String> hmExEmp = new HashMap<String, String>();
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as count,org_id from employee_official_details eod, employee_personal_details epd where " +
				"eod.emp_id = epd.emp_per_id and approved_flag = true and is_alive = false and emp_filled_flag = true ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" group by org_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(orgid));
			rst = pst.executeQuery();

			// System.out.println("pst  ==== >>>> "+pst);
			while (rst.next()) {
				hmExEmp.put(rst.getString("org_id"), rst.getInt("count")+"");
			}
			rst.close();
			pst.close();
			// System.out.println("allEmpreport ========= >> "+allEmpreport.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null) {
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmExEmp;
	}

//	private List<String> getEmpReport(Connection con, String orgid) {
//
//		List<String> allEmpreport = new ArrayList<String>();
//		PreparedStatement pst = null;
//		ResultSet rst = null;
//
//		try {
//
//			pst = con.prepareStatement("Select count(*) as count,approved_flag,is_alive,emp_filled_flag from employee_official_details eod,"
//							+ " employee_personal_details epd where org_id = ? and eod.emp_id = epd.emp_per_id group by approved_flag, "
//							+ "is_alive, emp_filled_flag");
//			pst.setInt(1, uF.parseToInt(orgid));
//			rst = pst.executeQuery();
//
//			// System.out.println("pst  ==== >>>> "+pst);
//			int count = 0;
//			while (rst.next()) {
//				if (rst.getBoolean("approved_flag") == true && rst.getBoolean("is_alive") == true && rst.getBoolean("emp_filled_flag") == true) {
//					
//					allEmpreport.add(rst.getString("count"));
//					
//				} else if (rst.getBoolean("approved_flag") == true && rst.getBoolean("is_alive") == false && rst.getBoolean("emp_filled_flag") == true) {
//					
//					allEmpreport.add(rst.getString("count"));
//					
//				} else if (rst.getBoolean("approved_flag") == false && rst.getBoolean("is_alive") == false && rst.getBoolean("emp_filled_flag") == true) {
//					
//					allEmpreport.add(rst.getString("count"));
//				}
//			}
//			// System.out.println("allEmpreport ========= >> "+allEmpreport.toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return allEmpreport;
//	}

//	public List<String> getEmpReport(String orgid) {
//
//		List<String> allEmpreport = new ArrayList<String>();
//		Connection con = null;
//		Database db = new Database();
//		db.setRequest(request);
//		PreparedStatement pst = null;
//		ResultSet rst = null;
//
//		try {
//			con = db.makeConnection(con);
//
//			pst = con
//					.prepareStatement("Select count(*) as count,approved_flag,is_alive,emp_filled_flag from employee_official_details eod,"
//							+ " employee_personal_details epd where org_id = ? and eod.emp_id = epd.emp_per_id group by approved_flag, "
//							+ "is_alive, emp_filled_flag");
//			pst.setInt(1, uF.parseToInt(orgid));
//			rst = pst.executeQuery();
//
//			// System.out.println("pst  ==== >>>> "+pst);
//			int count = 0;
//			while (rst.next()) {
//				if (rst.getBoolean("approved_flag") == true
//						&& rst.getBoolean("is_alive") == true
//						&& rst.getBoolean("emp_filled_flag") == true) {
//					allEmpreport.add(rst.getString("count"));
//				} else if (rst.getBoolean("approved_flag") == true
//						&& rst.getBoolean("is_alive") == false
//						&& rst.getBoolean("emp_filled_flag") == true) {
//					allEmpreport.add(rst.getString("count"));
//				} else if (rst.getBoolean("approved_flag") == false
//						&& rst.getBoolean("is_alive") == false
//						&& rst.getBoolean("emp_filled_flag") == true) {
//					allEmpreport.add(rst.getString("count"));
//				}
//			}
//			// System.out.println("allEmpreport ========= >> "+allEmpreport.toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeResultSet(rst);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return allEmpreport;
//	}

	public int getUserCount(String orgid) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		int count = 0;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("Select count(*) as count from employee_official_details eod, user_details ud where org_id = ? " +
				"and eod.emp_id = ud.emp_id and status = 'ACTIVE'");
			pst.setInt(1, uF.parseToInt(orgid));
			rst = pst.executeQuery();
			while (rst.next()) {
				count = rst.getInt("count");
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
		return count;
	}

//	public int getCandidateCount(String orgid) {
//
//		Connection con = null;
//		Database db = new Database();
//		db.setRequest(request);
//		PreparedStatement pst = null;
//		ResultSet rst = null;
//		int count = 0;
//		try {
//			con = db.makeConnection(con);
//			pst = con
//					.prepareStatement("Select count(*) as count,rd.recruitment_id from candidate_personal_details cpd, "
//							+ "recruitment_details rd where cpd.recruitment_id = rd.recruitment_id and rd.org_id = ? and "
//							+ "cpd.joining_date is null group by rd.recruitment_id");
//			pst.setInt(1, uF.parseToInt(orgid));
//			rst = pst.executeQuery();
//			while (rst.next()) {
//				count = rst.getInt("count");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeResultSet(rst);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return count;
//	}

	/*
	 * private List<String> getAppendData(String strID) { StringBuilder sb = new
	 * StringBuilder(); List<String> empList = new ArrayList<String>(); if
	 * (strID != null && !strID.equals("")) { int flag=0,empcnt=0; Map<String,
	 * String> hmEmpName = CF.getEmpNameMap(null, null);
	 * 
	 * String[] temp = strID.split(","); empcnt = temp.length-1; for (int i =0;
	 * i < temp.length; i++) {
	 * 
	 * if(temp[i]!=null && !temp[i].equals("")) { if(flag==0) {
	 * sb.append("<a href=\"MyProfile.action?empId="
	 * +temp[i]+"\">"+hmEmpName.get(temp[i].trim())+"</a>"); } else {
	 * sb.append(", " +"<a href=\"MyProfile.action?empId="+temp[i]+"\">"+
	 * hmEmpName.get(temp[i].trim())+"</a>"); } flag=1; } }
	 * empList.add(sb.toString()); empList.add(empcnt+""); //
	 * System.out.println("empList ========== >>>> "+empList.toString()); }
	 * return empList; }
	 */

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
