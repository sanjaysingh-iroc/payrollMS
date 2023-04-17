package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

public class AppraisalFinalStatus implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF;
	
	private String id;
	private String empid;
	private String appFreqId;
	public String execute() { 
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		getOrientationMember();
		request.setAttribute(PAGE, "/jsp/performance/AppraisalFinalStatus.jsp");
		request.setAttribute(TITLE, "Appraisal Status");
	
		getAppraisalFinalStatus();
		request.setAttribute("empid", getEmpid());
		return "success";
	}

	

	private void getAppraisalFinalStatus() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		
		try {
			con = db.makeConnection(con);
			
//			int appraisal_details_id = 0;
			Map<String, String> hmDesignation = CF.getDesigMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			Map<String, String> hmLevelMap = getLevelMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String,String> orientationMp=getOrientationValue(con);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig); 
//			Map<String, String> mp = new HashMap<String, String>();
//			Map<String, String> hmEmpDepartment = CF.getEmpDepartmentMap();
//			Map<String, String> mpdepart = CF.getDeptMap();
//			List<String> employeeList = new ArrayList<String>();

			
//			String departId = hmEmpDepartment.get(strSessionEmpId);
//			System.out.println("departId===>" + departId);
			
			Map<String, String> hmFrequency = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"),
						rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();
			
			pst = con
					.prepareStatement("select * from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id= adf.appraisal_id "
							+" and (is_delete is null or is_delete = false) and appraisal_details_id =?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();

			Map<String, String> appraisalMp = new HashMap<String, String>();
//			String appraisalee = null;

			while (rs.next()) {
				String memberName=getOrientationMemberDetails(con, rs.getInt("oriented_type"));
				appraisalMp.put("ID", rs.getString("appraisal_details_id"));
				appraisalMp.put("APPRAISAL", rs.getString("appraisal_name")+" ("+rs.getString("appraisal_freq_name")+")");
				appraisalMp.put("ORIENT", orientationMp.get(rs.getString("oriented_type"))+"&deg( "+memberName+" )");
				appraisalMp.put("EMPLOYEE", uF.showData(getAppendData(rs.getString("self_ids"), hmEmpName),""));
				appraisalMp.put("LEVEL", uF.showData(hmLevelMap.get(rs.getString("level_id")), ""));
				appraisalMp.put("DESIG",hmDesignation.get(rs.getString("desig_id")));
				appraisalMp.put("GRADE",hmGradeMap.get(rs.getString("grade_id")));
				appraisalMp.put("WLOCATION", rs.getString("wlocation_id"));
				appraisalMp.put("PEER", rs.getString("peer_ids"));
				appraisalMp.put("SELFID", rs.getString("self_ids"));
				appraisalMp.put("SUPERVISORID", rs.getString("supervisor_id"));
				
				appraisalMp.put("FREQUENCY", uF.showData(
						hmFrequency.get(rs.getString("frequency")), ""));
				appraisalMp.put("FROM",uF.getDateFormat(rs.getString("from_date"), DBDATE,CF.getStrReportDateFormat()));
				appraisalMp.put("TO",uF.getDateFormat(rs.getString("to_date"), DBDATE,CF.getStrReportDateFormat()));
				appraisalMp.put("CEOID", rs.getString("ceo_ids"));
				appraisalMp.put("HODID", rs.getString("hod_ids"));
				appraisalMp.put("APP_FREQ_ID", rs.getString("appraisal_freq_id"));
				appraisalMp.put("IS_APP_FREQ_PUBLISH", rs.getString("is_appraisal_publish"));
				appraisalMp.put("APP_FREQ_PUBLISH_EXPIRE", rs.getString("freq_publish_expire_status"));
				appraisalMp.put("IS_FREQ_CLOSE", rs.getString("is_appraisal_close"));
				appraisalMp.put("APP_CLOSE_REASON", rs.getString("close_reason"));
				appraisalMp.put("FREQ_START_DATE",uF.getDateFormat(rs.getString("freq_start_date"), DBDATE,CF.getStrReportDateFormat()));
				appraisalMp.put("FREQ_END_DATE",uF.getDateFormat(rs.getString("freq_end_date"), DBDATE,CF.getStrReportDateFormat()));
//				if (rs.getString("self_ids") != null
//						&& !rs.getString("self_ids").equals("")) {
//					appraisalee = rs.getString("self_ids");
//				} else if (rs.getString("grade_id") != null
//						&& !rs.getString("grade_id").equals("")) {
//					appraisalee = rs.getString("grade_id");
//				} else if (rs.getString("desig_id") != null
//						&& !rs.getString("desig_id").equals("")) {
//					appraisalee = rs.getString("desig_id");
//				} else if (rs.getString("level_id") != null
//						&& !rs.getString("level_id").equals("")) {
//					appraisalee = rs.getString("level_id");
//				} else if (rs.getString("department_id") != null
//						&& !rs.getString("department_id").equals("")) {
//					appraisalee = rs.getString("department_id");
//				} else {
//					appraisalee = rs.getString("wlocation_id");
//				}

			}
			rs.close();
			pst.close();
			
			request.setAttribute("appraisalMp", appraisalMp);
			
//			String app_level_id = null;
//			pst=con.prepareStatement("select appraisal_level_id from appraisal_level_details where appraisal_id=?");
//			pst.setInt(1, uF.parseToInt(id));
//			rs=pst.executeQuery();
//			int i=0;
//			while(rs.next()){
//				if(i==0){
//					app_level_id=rs.getString(1);
//				}else{
//					app_level_id=","+rs.getString(1);
//				}
//				i++;				
//			}
			
			Map<String,String> hmAppLevelName=new LinkedHashMap<String, String>();
			List<String> levelList=new ArrayList<String>();
			pst=con.prepareStatement("select appraisal_level_id,level_title from appraisal_level_details where appraisal_id=?");
			pst.setInt(1,uF.parseToInt(id));
			rs=pst.executeQuery();
			while(rs.next()){
				levelList.add(rs.getString(1));
				hmAppLevelName.put(rs.getString(1), rs.getString(2));
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmAppLevelName", hmAppLevelName);
			
			
			
			Map<String, String> hmUserTypeID = new HashMap<String, String>();
			pst = con
					.prepareStatement("select user_type_id,user_type from user_type");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmUserTypeID.put(rs.getString(2),rs.getString(1));
			}
			rs.close();
			pst.close();

			pst = con
					.prepareStatement("select *,(marks*100/weightage) as average from(" +
							"select sum(marks) as marks ,sum(weightage) as weightage,user_type_id,appraisal_level_id from appraisal_question_answer " +
							"where appraisal_id=? and emp_id=? and appraisal_freq_id=? and weightage>0 group by user_type_id,appraisal_level_id)as a");
			pst.setInt(1,uF.parseToInt(getId()));
			pst.setInt(2,uF.parseToInt(getEmpid()));
			pst.setInt(3,uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			Map<String,Map<String,String>> outerMp=new HashMap<String,Map<String,String>>();
			while (rs.next()) {
				
				Map<String,String> value=outerMp.get(rs.getString("appraisal_level_id"));
				 if(value==null)value=new HashMap<String,String>();
				 value.put(rs.getString("user_type_id"), uF.formatIntoTwoDecimal(rs.getDouble("average")));
				 outerMp.put(rs.getString("appraisal_level_id"), value);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("outerMp",outerMp);
			request.setAttribute("hmUserTypeID", hmUserTypeID);
//			List<List<String>> outerList=new ArrayList<List<String>>();
//			for(int j=0;levelList!=null && j<levelList.size();j++){
//				List<String> innerList=new ArrayList<String>();
//				
//				innerList.add(hmAppLevelName.get(levelList.get(j)));
//				
//				Map<String, String> hmMarks = getMarks(levelList.get(j), con);
//				Map<String, String> hmOutOfMarks = getOutOfMarks(levelList.get(j), con);
//				
//				
//				
//				String hravg = getAvg(hmOutOfMarks.get(hmUserTypeID.get(HRMANAGER)), hmMarks.get(hmUserTypeID.get(HRMANAGER)));
//				
//				innerList.add(hravg);
//				String manAvg="";
//				if(uF.parseToInt(appraisalMp.get("ORIENT"))>=180){
//					manAvg = getAvg(hmOutOfMarks.get(hmUserTypeID.get(MANAGER)), hmMarks.get(hmUserTypeID.get(MANAGER)));					
//				}
//				innerList.add(manAvg);
//				
//				String peeravg ="";
//				if(uF.parseToInt(appraisalMp.get("ORIENT"))>=270){
//				peeravg = getAvg(hmOutOfMarks.get(hmUserTypeID.get(EMPLOYEE)), hmMarks.get(hmUserTypeID.get(EMPLOYEE)));
//				}
//				innerList.add(peeravg);
//				
//				Map<String, String> hmSelfMarks = getSelfMarks(levelList.get(j), con);
//				Map<String, String> hmSelfOutOfMarks = getSelfOutOfMarks(levelList.get(j), con);
//				
//				String selfAvg= getAvg(hmSelfOutOfMarks.get(hmUserTypeID.get(EMPLOYEE)), hmSelfMarks.get(hmUserTypeID.get(EMPLOYEE)));
//				
//				innerList.add(selfAvg);
//				
//				outerList.add(innerList);
//				
//			}
			
//			request.setAttribute("levelRemark", outerList);
						
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	private void getOrientationMember() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		ResultSet rs = null;
		
	
		try {
			Map<String,String> orientationMemberMp=new HashMap<String,String>();
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs=pst.executeQuery();
			while(rs.next()){
				//orientationMemberMp.put(rs.getString("orientation_member_id"),rs.getString("member_name"));
				orientationMemberMp.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();
			
				request.setAttribute("orientationMemberMp", orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		}
	
	
	private String getOrientationMemberDetails(Connection con, int id) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> orientationMp=(Map<String,String>)request.getAttribute("orientationMemberMp");
		StringBuilder sb=new StringBuilder();
		try {
			List<String> memberList=new ArrayList<String>();
			
			pst = con.prepareStatement("select * from orientation_details where orientation_id=?");
			pst.setInt(1,id);
			rs=pst.executeQuery();
			
			while(rs.next()){
				sb.append(orientationMp.get(rs.getString("member_id"))+",");
				memberList.add(rs.getString("member_id"));
			}
			rs.close();
			pst.close();
			
				request.setAttribute("memberList", memberList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
		}
	
	
	
	private Map<String,String> getOrientationValue(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		Map<String,String> orientationMp=new HashMap<String,String>();
		try {
			
			pst = con.prepareStatement("select * from apparisal_orientation");
			rs=pst.executeQuery();
			while(rs.next()){
				orientationMp.put(rs.getString("apparisal_orientation_id"),rs.getString("orientation_name"));
			}
			rs.close();
			pst.close();
			
				request.setAttribute("orientationMp", orientationMp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMp;
		}
//	private Map<String, String> getSelfOutOfMarks(String levelid, Connection con) {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		UtilityFunctions uF = new UtilityFunctions();
//		Map<String, String> hmMarks = new HashMap<String, String>();
//		try {
//			pst = con
//					.prepareStatement("select sum(weightage),user_type_id from appraisal_question_answer where emp_id=?" +
//					" and appraisal_level_id =? and appraisal_id=? group by user_type_id");
//			pst.setInt(1, uF.parseToInt(empid));
//			pst.setInt(2, uF.parseToInt(levelid));
//			pst.setInt(3, uF.parseToInt(id));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				hmMarks.put(rs.getString(2), rs.getString(1));
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			/*db.closeResultSet(rs1);
//			db.closeStatements(pst1);*/
//		}
//		return hmMarks;
//	}



//	private Map<String, String> getSelfMarks(String levelid, Connection con) {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
////		Database db = new Database();
//		UtilityFunctions uF = new UtilityFunctions();
//		Map<String, String> hmMarks = new HashMap<String, String>();
//		try {
//			pst = con
//					.prepareStatement("select sum(marks),user_type_id from appraisal_question_answer where emp_id=?" +
//					" and appraisal_level_id =? and appraisal_id=? and emp_id=user_id group by user_type_id");
//			pst.setInt(1, uF.parseToInt(empid));
//			pst.setInt(2, uF.parseToInt(levelid));
//			pst.setInt(3, uF.parseToInt(id));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				hmMarks.put(rs.getString(2), rs.getString(1));
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
////			db.closeResultSet(rs1);
////			db.closeStatements(pst1);
//		}
//		return hmMarks;
//	}



//	private String getAvg(String outofmarks, String marks) {
//		double outavg = 0;
//		double hrratio = 0;
//		UtilityFunctions uF = new UtilityFunctions();
//
//		if (outofmarks != null && marks != null) {
//			outavg = uF.parseToInt(outofmarks) / 100;
//			hrratio = uF.parseToInt(marks) / outavg;
//		}
//		return uF.showData("" + hrratio, "");
//	}
//	private Map<String, String> getOutOfMarks(String levelid, Connection con) {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		UtilityFunctions uF = new UtilityFunctions();
//		Map<String, String> hmMarks = new HashMap<String, String>();
//		try {
//			pst = con
//					.prepareStatement("select sum(weightage),user_type_id from appraisal_question_answer where emp_id=?" +
//					" and appraisal_level_id =? and appraisal_id=? group by user_type_id");
//			pst.setInt(1, uF.parseToInt(empid));
//			pst.setInt(2, uF.parseToInt(levelid));
//			pst.setInt(3, uF.parseToInt(id));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				hmMarks.put(rs.getString(2), rs.getString(1));
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			/*db.closeResultSet(rs1);
//			db.closeStatements(pst1);*/
//		}
//		return hmMarks;
//	}

//	private Map<String, String> getMarks(String levelid,
//			 Connection con) {
//
//		PreparedStatement pst = null;
//		ResultSet rs = null;
////		Database db = new Database();
//		UtilityFunctions uF = new UtilityFunctions();
//		Map<String, String> hmMarks = new HashMap<String, String>();
//		try {
//			pst = con
//					.prepareStatement("select sum(marks),user_type_id from appraisal_question_answer where emp_id=?" +
//					" and appraisal_level_id =? and appraisal_id=? group by user_type_id");
//			pst.setInt(1, uF.parseToInt(empid));
//			pst.setInt(2, uF.parseToInt(levelid));
//			pst.setInt(3, uF.parseToInt(id));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				hmMarks.put(rs.getString(2), rs.getString(1));
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
////			db.closeResultSet(rs1);
////			db.closeStatements(pst1);
//		}
//		return hmMarks;
//	}


	private String getAppendData(String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();

		if (strID != null && !strID.equals("")) {
			strID = strID.substring(1, strID.length()-1);
			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim()));
					} else {
						sb.append("," + mp.get(temp[i].trim()));
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

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmpid() {
		return empid;
	}

	public void setEmpid(String empid) {
		this.empid = empid;
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
	
	
}
