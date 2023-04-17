package com.konnect.jpms.performance;

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

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class AppraisalPreview implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strSessionUserTypeID;
	CommonFunctions CF;
	private String id;
	private String empID; 
	private String userType;
	private String oreinted;
	private String appFreqId;
	private String fromPage;
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strSessionUserTypeID = (String) session.getAttribute(USERTYPEID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		request.setAttribute(PAGE, "/jsp/performance/AppraisalPreview.jsp");
		request.setAttribute(TITLE, "Appraisal Preview");
		UtilityFunctions uF = new UtilityFunctions();
		
		getEmployeeAssignedKRAAndGoalTarget(uF);
		
		getSelectedOrientationPosition(uF);
		getanswerTypeMap(uF);
		getAppOrientationType();
		getSectionWorkFlow();
		// getLevelStep(uF);
		getQuestionSubType(uF);
		getAppraisalDetail(uF);
		getEmployyDetailsList(uF);
		getAppraisalQuestionMap(uF);
//		getLevelStatus(uF);
//		getCurrentLevelAnswer(uF);
//		checkCurrentLevelExistForCurrentEmp(uF);
		getSummary(uF);
		
		return getLevelQuestion(uF);

	}

	
	public void getSelectedOrientationPosition(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, Map<String, String>> hmOrientPosition = new LinkedHashMap<String, Map<String,String>>();
		Map<String, String> orientPosition = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_main_level_details");
//			pst.setInt(1, uF.parseToInt(getMain_level_id()));
			rs = pst.executeQuery();
			while (rs.next()) {
				orientPosition = hmOrientPosition.get(rs.getString("main_level_id"));
				if(orientPosition == null)orientPosition = new HashMap<String, String>();
				orientPosition.put("HR", rs.getString("hr"));//0
				orientPosition.put("Manager", rs.getString("manager"));//1
				orientPosition.put("Self", rs.getString("self"));//2
				orientPosition.put("Peer", rs.getString("peer"));//3
				orientPosition.put("Client", rs.getString("client"));//4
				orientPosition.put("Sub-ordinate", rs.getString("subordinate"));//5
				orientPosition.put("GroupHead", rs.getString("grouphead"));//6
				orientPosition.put("Vendor", rs.getString("vendor"));//7
				orientPosition.put("CEO", rs.getString("ceo"));//8
				orientPosition.put("HOD", rs.getString("hod"));//9
				orientPosition.put("Other Peer", rs.getString("other_peer"));//10
				hmOrientPosition.put(rs.getString("main_level_id"), orientPosition);
			}
			rs.close();
			pst.close();
//			System.out.println("orientPosition ::: "+orientPosition);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("hmOrientPosition",hmOrientPosition);
	}
	
		
	private void getSectionWorkFlow() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmMemberPosition = new HashMap<String, String>();
			pst = con.prepareStatement("select distinct(main_level_id),hr,manager,peer,self,ceo,hod from appraisal_level_details where appraisal_id = ? order by main_level_id");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
//			System.out.println("pst==========> "+pst);
			while (rs.next()) {
				hmMemberPosition.put(rs.getString("main_level_id")+"_HR", rs.getString("hr"));
				hmMemberPosition.put(rs.getString("main_level_id")+"_Manager", rs.getString("manager"));
				hmMemberPosition.put(rs.getString("main_level_id")+"_Self", rs.getString("self"));
				hmMemberPosition.put(rs.getString("main_level_id")+"_Peer", rs.getString("peer"));
				hmMemberPosition.put(rs.getString("main_level_id")+"_CEO", rs.getString("ceo"));
				hmMemberPosition.put(rs.getString("main_level_id")+"_HOD", rs.getString("hod"));
			} 
			rs.close();
			pst.close();
//			System.out.println("hmMemberPosition ==========> "+hmMemberPosition);
			request.setAttribute("hmMemberPosition", hmMemberPosition);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getAppOrientationType() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			StringBuilder sb = new StringBuilder();
			con = db.makeConnection(con);
			String orient = null;
			pst = con.prepareStatement("select oriented_type from appraisal_details where appraisal_details_id = ?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
//			System.out.println("pst===> "+pst);
			while (rs.next()) {
			orient = rs.getString("oriented_type");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select member_name from orientation_details od,orientation_member om  where orientation_id=? and od.member_id=orientation_member_id");
			pst.setInt(1, uF.parseToInt(orient));
			rs = pst.executeQuery();
//			System.out.println("pst===> "+pst);
			int i = 0;
			while (rs.next()) {
				if (i == 0)
					sb.append(rs.getString("member_name"));
				else
					sb.append("," + rs.getString("member_name"));
				i++; 
			}
			rs.close();
			pst.close();
			
			request.setAttribute("member", sb.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getSummary(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);

			/*pst = con.prepareStatement("select appraisal_level_id from appraisal_question_answer where appraisal_id=? and emp_id=? and user_id=? and user_type_id=? and appraisal_freq_id=? group by appraisal_level_id order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			Map<String, String> innerMp = new HashMap<String, String>();
			while (rs.next()) {
				innerMp.put(rs.getString("appraisal_level_id"), rs.getString("appraisal_level_id"));
			}
			rs.close();
			pst.close();
			
			pst = con
			.prepareStatement("select appraisal_level_id from kra_rating_details  where appraisal_id=? and emp_id=? and added_by=? and user_type_id=? and appraisal_freq_id = ? group by appraisal_level_id order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				innerMp.put(rs.getString("appraisal_level_id"), rs.getString("appraisal_level_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select appraisal_level_id from target_details  where appraisal_id=? and emp_id=? and added_by=? and user_type_id=? and appraisal_freq_id = ? group by appraisal_level_id order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			while (rs.next()) {
			innerMp.put(rs.getString("appraisal_level_id"), rs.getString("appraisal_level_id"));
			}		
			rs.close();
			pst.close();
			
			request.setAttribute("LEVEL_STATUS", innerMp);*/

			List<String> levelList = new ArrayList<String>();
			pst = con.prepareStatement("select appraisal_level_id from appraisal_level_details where appraisal_id=? order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				levelList.add(rs.getString("appraisal_level_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("levelList", levelList);
//			System.out.println("levelList=======+>" + levelList);
			
			List<String> mainLevelList = new ArrayList<String>();
			pst = con.prepareStatement("select main_level_id from appraisal_main_level_details where appraisal_id=? order by main_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				mainLevelList.add(rs.getString("main_level_id"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("mainLevelList", mainLevelList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public boolean getPreviousLevelData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		boolean flag = false;
		try {
			con = db.makeConnection(con);

			StringBuilder sb = new StringBuilder("select * from appraisal_question_details where appraisal_id=? ");

			/*if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get(MANAGER))) {
				sb.append(" and manager !=0");
			} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("HR"))) {
				sb.append(" and hr !=0");
			} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("Self"))) {
				sb.append(" and  self!=0 ");
			} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("Peer"))) {
				sb.append(" and peer !=0 ");
			}*/

			pst = con.prepareStatement(sb.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			List<List<String>> outerList = new ArrayList<List<String>>();
			
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add("");
				innerList.add(rs.getString("appraisal_id"));
				innerList.add(rs.getString("plan_id"));
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("attribute_id"));
				innerList.add(rs.getString("other_short_description"));
				innerList.add(rs.getString("appraisal_level_id"));
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(rs.getString("other_id"));
				innerList.add("");
				innerList.add("");
				outerList.add(innerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("questionList", outerList);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}

	
	private Map<String, String> getOrientMemberID() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, String> orientationMemberMp = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMemberMp.put(rs.getString("member_name"), rs.getString("member_id"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return orientationMemberMp;
	}

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void getAppraisalDetail(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null; 
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		Map<String, String> hmDesignation = CF.getDesigMap(con);
		Map<String, String> hmGradeMap = CF.getGradeMap(con);
		Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
		Map<String, String> hmLevelMap = getLevelMap(con);
		Map<String, String> hmLocation = getLocationMap(con);
		Map<String, String> mpdepart = CF.getDeptMap(con);
		Map<String, String> orientationMp = getOrientationValue(con);
		Map<String, String> orientationMemberMp = getOrientationMember(con);
		try {
			Map<String, String> hmFrequency = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();

			List<String> appraisalList = new ArrayList<String>();
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			while (rs.next()) {
				oreinted = rs.getString("oriented_type");
				List<String> memberList = new ArrayList<String>();
				memberList = CF.getOrientationMemberDetails(con,uF.parseToInt(oreinted));
				StringBuilder memberName = null;
				if(rs.getString("usertype_member") != null && !(rs.getString("usertype_member")).equals("")) {
					for (int i = 0; i < memberList.size(); i++) {
						if (i == 0) {
							if(memberList.get(i) != null ) {
								memberName = new StringBuilder();
								memberName.append(orientationMemberMp.get(memberList.get(i)));
							}
						} else {
							memberName.append(", " + orientationMemberMp.get(memberList.get(i)));
						}
					}
				}
				if(memberName == null ) {
					memberName = new StringBuilder();
					memberName.append("Anyone"); 
				}
				appraisalList.add(uF.showData(rs.getString("appraisal_details_id"), "")); //0
				appraisalList.add(uF.showData(rs.getString("appraisal_name"), "")); //1
				
				appraisalList.add(uF.showData(orientationMp.get(rs.getString("oriented_type")) + "&deg( " + memberName + " )", "")); //2
				appraisalList.add(uF.showData(rs.getString("self_ids"), "")); //3
				appraisalList.add(uF.showData(getAppendData(rs.getString("level_id"), hmLevelMap), "")); //4
				appraisalList.add(uF.showData(getAppendData(rs.getString("desig_id"), hmDesignation), "")); //5
				appraisalList.add(uF.showData(getAppendData(rs.getString("grade_id"), hmGradeMap), "")); //6
				appraisalList.add(uF.showData(hmFrequency.get(rs.getString("frequency")), "")); //7
				appraisalList.add(uF.showData(getAppendData(rs.getString("wlocation_id"), hmLocation), "")); //8
				appraisalList.add(uF.showData(getAppendData(rs.getString("department_id"), mpdepart), "")); //9
				appraisalList.add(uF.showData(rs.getString("supervisor_id"), "")); //10
				appraisalList.add(uF.showData(rs.getString("peer_ids"), "")); //11
				appraisalList.add(uF.showData(getAppendData(rs.getString("self_ids"), hmEmpName), "")); //12
				appraisalList.add(uF.showData(rs.getString("emp_status"), "")); //13
				appraisalList.add(uF.showData(rs.getString("appraisal_type"), "")); //14
				appraisalList.add(uF.showData(rs.getString("appraisal_description"), "")); //15
				appraisalList.add(uF.showData(rs.getString("appraisal_instruction"), "")); //16
				appraisalList.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat())); //17
				appraisalList.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat())); //18
				boolean flag=uF.parseToBoolean(rs.getString("is_publish"));
				appraisalList.add(""+flag); //19
				appraisalList.add(uF.showData(rs.getString("ceo_ids"), "")); //20
				appraisalList.add(uF.showData(rs.getString("hod_ids"), "")); //21
				
				appraisalList.add(uF.showData(getAppendData(rs.getString("reviewer_id"), hmEmpName), ""));//22
				StringBuilder sbAppraisers = new StringBuilder();
				if(rs.getString("usertype_member") != null && rs.getString("usertype_member").length()>0) {
					List<String> alAppraiserMember = Arrays.asList(rs.getString("usertype_member").split(","));
					for(int i=0; alAppraiserMember != null && i<alAppraiserMember.size(); i++) {
						if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
							sbAppraisers.append("Managers: " + uF.showData(getAppendData(rs.getString("supervisor_id"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(HOD))) {
							sbAppraisers.append("HODs: " + uF.showData(getAppendData(rs.getString("hod_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(CEO))) {
							sbAppraisers.append("CEOs: " + uF.showData(getAppendData(rs.getString("ceo_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(HRMANAGER))) {
							sbAppraisers.append("HRs: " + uF.showData(getAppendData(rs.getString("hr_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(ADMIN))) {
							sbAppraisers.append("Global HRs: " + uF.showData(getAppendData(rs.getString("hr_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == 4) {
							sbAppraisers.append("Peers: " + uF.showData(getAppendData(rs.getString("peer_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == 10) {
							sbAppraisers.append("Anyone: " + uF.showData(getAppendData(rs.getString("other_ids"), hmEmpName), "N/A")+"</br>");
						}
					}
				}
				appraisalList.add(uF.showData(sbAppraisers.toString(), ""));//23
			}
			rs.close();
			pst.close();
			
			request.setAttribute("appraisalList", appraisalList);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public Map<String, String> getLevelMap(Connection con) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new HashMap<String, String>();
		try {
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();

			while (rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name") + "[" + rs.getString("level_code") + "]");
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmLevelMap;
	}
	
	

	private Map<String, String> getLocationMap(Connection con) {
		Map<String, String> mplocation = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {

			pst = con.prepareStatement("select * from work_location_info");
			rst = pst.executeQuery();
			while (rst.next()) {
				mplocation.put(rst.getString("wlocation_id"), rst.getString("wlocation_name"));
			}
			rst.close();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mplocation;
	}
	
	
	private Map<String, String> getOrientationValue(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		Map<String, String> orientationMp = new HashMap<String, String>();
		Map<String, String> hmorientationMembers = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from apparisal_orientation");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMp.put(rs.getString("apparisal_orientation_id"), rs.getString("orientation_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("orientationMp", orientationMp);
			
			pst = con.prepareStatement("select * from orientation_member");
			rs = pst.executeQuery();
			while (rs.next()) {
				//hmorientationMembers.put(rs.getString("orientation_member_id"), rs.getString("member_name"));
				hmorientationMembers.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("hmorientationMembers", hmorientationMembers);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMp;
	}
	
	
	
	private String getAppendData(String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("strID :: "+strID);
		if (strID != null && !strID.equals("") && !strID.isEmpty()) {
			if(strID.trim().length()>0 && strID.trim().substring(0, 1).equals(",") && strID.trim().substring(strID.length()-1, strID.length()).equals(",")){
				strID = strID.substring(1, strID.length()-1);
			}
			if (strID.contains(",")) {
				String[] temp = strID.split(",");
				for (int i = 0; i < temp.length; i++) {
					if(uF.parseToInt(temp[i].trim()) > 0) {
						if (i == 0) {
							sb.append(mp.get(temp[i].trim()));
						} else {
							sb.append(", " + mp.get(temp[i].trim()));
						}
					}
				}
			} else {
				return mp.get(strID);
			}
		} else {
			return null;
		}
		return sb.toString();
	}
	
	
	private void getEmployyDetailsList(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
		boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
		Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
		Map<String, String> hmEmpDepartment = CF.getEmpDepartmentMap(con);
		Map<String, String> hmEmpJoiningDate = CF.getEmpJoiningDateMap(con, uF);
		Map<String, String> hmEmpProbationEnd = CF.getEmpProbationEndDateMap(con, uF);
		Map<String, String> mpdepart = CF.getDeptMap(con);
		try {
			pst = con.prepareStatement("select * from employee_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			rs = pst.executeQuery();
			List<String> empList = new ArrayList<String>();
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			while (rs.next()) {
				empList.add(rs.getString("emp_per_id"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				empList.add(rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname") + " [" + rs.getString("empcode") + "]");
				empList.add(uF.showData(mpdepart.get(hmEmpDepartment.get(rs.getString("emp_per_id"))), ""));
				empList.add(uF.showData(hmEmpCodeDesig.get(rs.getString("emp_per_id")), ""));
				empList.add(uF.showData(hmEmpJoiningDate.get(rs.getString("emp_per_id")), ""));
				empList.add(uF.showData(hmEmpProbationEnd.get(rs.getString("emp_per_id")), ""));
			}
			rs.close();
			pst.close();
			empList.add(orientationMemberMp.get(getUserType()));
			request.setAttribute("empList", empList);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public String getLevelQuestion(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			List<String> alKRAIds = (List<String>)request.getAttribute("alKRAIds");
			List<String> alGoarTargetIds = (List<String>)request.getAttribute("alGoarTargetIds");
			
			System.out.println("alKRAIds ===>> " + alKRAIds + " -- alGoarTargetIds ===>> " + alGoarTargetIds);
			StringBuilder sb = new StringBuilder("select * from appraisal_question_details where appraisal_id=? ");
			sb.append(" order by appraisal_level_id");
			pst = con.prepareStatement(sb.toString());
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
//			System.out.println("pst 1 :::: "+pst);
			Map<String,List<List<String>>> hmLevelQuestion = new LinkedHashMap<String, List<List<String>>>();
			StringBuilder sbLevels = new StringBuilder();
			while (rs.next()) {
				if(getFromPage()!=null && getFromPage().equals("KRATARGET")) {
					if(rs.getInt("app_system_type")==4 && alKRAIds!=null && rs.getString("kra_id")!=null && !alKRAIds.contains(rs.getString("kra_id"))) {
						continue;
					}
					if((rs.getInt("app_system_type")==3 || rs.getInt("app_system_type")==5) && alGoarTargetIds!=null && rs.getString("goal_kra_target_id")!=null && !alGoarTargetIds.contains(rs.getString("goal_kra_target_id"))) {
						continue;
					}
				}
				List<List<String>> outerList=hmLevelQuestion.get(rs.getString("appraisal_level_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add("");
				innerList.add(rs.getString("appraisal_id"));
				innerList.add(rs.getString("plan_id"));
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("attribute_id"));
				innerList.add(rs.getString("other_short_description"));
				innerList.add(rs.getString("appraisal_level_id"));
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(rs.getString("other_id"));
				innerList.add("");
				innerList.add("");
				outerList.add(innerList);
				
				hmLevelQuestion.put(rs.getString("appraisal_level_id"), outerList);
			}

			rs.close();
			pst.close();
			System.out.println("hmLevelQuestion :::: "+hmLevelQuestion);
			request.setAttribute("hmLevelQuestion", hmLevelQuestion);
			
//			System.out.println("getUserType :::: "+ getUserType());
			
			Map<String, List<List<String>>> hmSection = new LinkedHashMap<String, List<List<String>>>();
			 sb = new StringBuilder("select * from appraisal_level_details where main_level_id in(select main_level_id from " +
					"appraisal_main_level_details where appraisal_id=(select appraisal_details_id from appraisal_details where " +
					"appraisal_details_id = ?))");
				sb.append(" order by appraisal_level_id");
				pst = con.prepareStatement(sb.toString());
				pst.setInt(1, uF.parseToInt(id));
				rs = pst.executeQuery();
//				System.out.println("pst 2 :::: "+pst);
				while (rs.next()) {
					List<List<String>> outerList=hmSection.get(rs.getString("main_level_id"));
					if(outerList==null)outerList=new ArrayList<List<String>>();
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("appraisal_level_id"));
					innerList.add(rs.getString("level_title"));
					innerList.add(rs.getString("short_description"));
					innerList.add(rs.getString("long_description"));
					outerList.add(innerList);
					hmSection.put(rs.getString("main_level_id"), outerList);
					
					sbLevels.append(rs.getString("main_level_id")+",");
				}
				rs.close();
				pst.close();
				
			request.setAttribute("hmSection", hmSection);
//			System.out.println("hmSection :::: "+hmSection);
			
			Map hmLevelDetails = new HashMap();
			if(sbLevels.length()>1){
				sbLevels.replace(0, sbLevels.length(), sbLevels.substring(0, sbLevels.length()-1));
				pst = con.prepareStatement("select * from appraisal_main_level_details where main_level_id in ("+sbLevels.toString()+")");
				rs = pst.executeQuery();
//				System.out.println(" pst : "+ pst);
				while(rs.next()){
					hmLevelDetails.put(rs.getString("main_level_id")+"_TITLE", rs.getString("level_title"));
					hmLevelDetails.put(rs.getString("main_level_id")+"_SDESC", rs.getString("short_description"));
					hmLevelDetails.put(rs.getString("main_level_id")+"_LDESC", rs.getString("long_description"));
					hmLevelDetails.put(rs.getString("main_level_id")+"_LWEIGHTAGE", rs.getString("section_weightage")); //added by parvez date: 27-02-2023===
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("hmLevelDetails", hmLevelDetails);
			
		//===start parvez date: 27-02-2023===	
			pst = con.prepareStatement("select * from appraisal_other_question_type_details where appraisal_id=? order by level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, String> othrQueType = new HashMap<String, String>();
			while (rs.next()) {
				othrQueType.put(rs.getString("level_id"), rs.getString("other_question_type"));
			}
			rs.close();
			pst.close();
			request.setAttribute("othrQueType", othrQueType);
		//===end parvez date: 27-02-2023===

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "success";
	}

	
	private void getEmployeeAssignedKRAAndGoalTarget(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		// boolean flag = false;
		try {
			con = db.makeConnection(con);
			List<String> alKRAIds = new ArrayList<String>();
			String kraTypes = INDIVIDUAL_GOAL+","+INDIVIDUAL_KRA+","+EMPLOYEE_KRA;
			pst = con.prepareStatement("select * from goal_kras where goal_type in ("+kraTypes+") and emp_ids like '%,"+strSessionEmpId+",%'");
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while (rs.next()) {
				alKRAIds.add(rs.getString("goal_kra_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("alKRAIds", alKRAIds);
			
			List<String> alGoarTargetIds = new ArrayList<String>();
			pst = con.prepareStatement("select * from goal_details where emp_ids like '%,"+strSessionEmpId+",%'");
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while (rs.next()) {
				alGoarTargetIds.add(rs.getString("goal_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("alGoarTargetIds", alGoarTargetIds);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	// public String getLevelId() {
	// return levelId;
	// }
	//
	// public void setLevelId(String levelId) {
	// this.levelId = levelId;
	// }

	public void getAppraisalQuestionMap(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		// Map<String, String> AppraisalQuestion = new HashMap<String,
		// String>();
		Map<String, List<String>> hmQuestion = new HashMap<String, List<String>>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from question_bank qb, appraisal_question_details aqd where qb.question_bank_id=aqd.question_id and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			List<List<String>> outerList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("question_bank_id"));		//0
				innerList.add(rs.getString("question_text"));			//1
				innerList.add(rs.getString("option_a"));				//2
				innerList.add(rs.getString("option_b"));				//3
				innerList.add(rs.getString("option_c"));				//4
				innerList.add(rs.getString("option_d"));				//5
				innerList.add(rs.getString("correct_ans"));				//6
				innerList.add(rs.getString("is_add"));					//7
				innerList.add(rs.getString("question_type"));			//8
				innerList.add(rs.getString("option_e"));				//9
		//===start parvez date: 17-03-2022===
				innerList.add(rs.getString("weightage"));				//10
				innerList.add(rs.getString("other_short_description"));	//11
		//===end parvez date: 17-03-2022===		
				
				outerList.add(innerList);
				hmQuestion.put(rs.getString("question_bank_id"), innerList);
			}
			rs.close();
			pst.close();
//			System.out.println("hmQuestion ====> "+hmQuestion);
			request.setAttribute("hmQuestion", hmQuestion);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		// return AppraisalQuestion;
	}

	public void getanswerTypeMap(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, List<List<String>>> hmQuestionanswerType = new HashMap<String, List<List<String>>>();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_answer_type_sub ");
			rs = pst.executeQuery();
			while (rs.next()) {

				List<List<String>> outerList = hmQuestionanswerType.get(rs.getString("answer_type_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("score_label"));
				innerList.add(rs.getString("score"));
				outerList.add(innerList);
				hmQuestionanswerType.put(rs.getString("answer_type_id"), outerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmQuestionanswerType", hmQuestionanswerType);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public Map<String, String> getLevelMap(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name") + "[" + rs.getString("level_code") + "]");
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmLevelMap;
	}

	private void getQuestionSubType(UtilityFunctions uF) {
		Map<String, List<List<String>>> answertypeSub = new HashMap<String, List<List<String>>>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from appraisal_answer_type_sub order by appraisal_answer_type_sub_id");
			rst = pst.executeQuery();
			while (rst.next()) {
				List<List<String>> outerList = answertypeSub.get(rst.getString("answer_type_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();

				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("score"));
				innerList.add(rst.getString("score_label"));
				outerList.add(innerList);
				answertypeSub.put(rst.getString("answer_type_id"), outerList);
			}
			rst.close();
			pst.close();
			request.setAttribute("answertypeSub", answertypeSub);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getOreinted() {
		return oreinted;
	}

	public void setOreinted(String oreinted) {
		this.oreinted = oreinted;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}


	private Map<String, String> getOrientationMember(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		Map<String, String> orientationMemberMp = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				//orientationMemberMp.put(rs.getString("orientation_member_id"), rs.getString("member_name"));
				orientationMemberMp.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMemberMp;
	}
}