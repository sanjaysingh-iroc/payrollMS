package com.konnect.jpms.employee;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.konnect.jpms.charts.LinearZMeter;
import com.konnect.jpms.charts.PieCharts;
import com.konnect.jpms.charts.SemiCircleMeter;
import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class EmpDashboardData implements IStatements {
	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;
	UtilityFunctions uF;
	Connection con;
	String strEmpId;
	String strEmpOrgId;
	String strSessionUserType;
	
	public EmpDashboardData(HttpServletRequest request, HttpSession session, CommonFunctions CF, UtilityFunctions uF, Connection con, String strEmpId) {
		super();
		this.request = request;
		this.session = session;
		this.CF = CF;
		this.uF = uF;
		this.con = con;
		this.strEmpId = strEmpId;
		
	}

	
	public void viewProfile(String strEmpIdReq) {

//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();

		try {
//			con = db.makeConnection(con);
			strSessionUserType = (String)session.getAttribute(USERTYPE);
			
			request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
			
			CF.getEmpProfileDetail(con, request, session, CF, uF, strSessionUserType, strEmpIdReq);
			
			request.setAttribute(TITLE, "My Dashboard");
//			List<List<String>> alSkills = new ArrayList<List<String>>();
//			alSkills = CF.selectSkills(con, uF.parseToInt(strEmpIdReq));
//			request.setAttribute("alSkills", alSkills);
			
		} catch (Exception e) {
			e.printStackTrace();
		} /*finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}*/
//		return SUCCESS;

	}
	
	/*public void getEmpData() {
		PreparedStatement pst = null;
		CallableStatement cst = null;
		ResultSet rs = null;
		try { 
			con.setAutoCommit(false);
			cst = con.prepareCall("{? = call sel_emp_work_location_info(?)}");
			cst.registerOutParameter(1, Types.OTHER);
			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
			cst.execute();
			rs = (ResultSet) cst.getObject(1);
			boolean isRosterDependent = false;
			String strJoiningDate = null;
			if (rs.next()) {
				request.setAttribute("EMPCODE", rs.getString("empcode"));
				request.setAttribute("EMPNAME", rs.getString("emp_fname") + " " + rs.getString("emp_lname"));
				request.setAttribute("DATE", uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", "yyyy-MM-dd", "EEEE, MMMM dd,yyyy"));
				request.setAttribute("IMAGE", ((rs.getString("emp_image") != null && rs.getString("emp_image").length() > 0) ? rs.getString("emp_image") : "avatar_photo.png"));
				request.setAttribute("DEPT", rs.getString("dept_name"));
				request.setAttribute("WL_NAME", rs.getString("wlocation_name"));
				request.setAttribute("EMAIL", rs.getString("emp_email"));
				request.setAttribute("JOINING_DATE", uF.getDateFormat(rs.getString("joining_date"), DBDATE, "dd MMMM, yyyy"));
				strJoiningDate = rs.getString("joining_date");
				isRosterDependent = uF.parseToBoolean(rs.getString("is_roster"));
			}
			cst.close();

			
			con.setAutoCommit(false);
			cst = con.prepareCall("{? = call sel_emp_designation(?)}");
			cst.registerOutParameter(1, Types.OTHER);
			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
			cst.execute();
			rs = (ResultSet) cst.getObject(1);
			if (rs.next()) {
				request.setAttribute("DESIG_NAME", rs.getString("designation_name")+" ["+rs.getString("designation_code")+"]");
			}
			cst.close();
			
			con.setAutoCommit(false);
			cst = con.prepareCall("{? = call sel_emp_details(?)}");
			cst.registerOutParameter(1, Types.OTHER);
			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
			cst.execute();
			rs = (ResultSet) cst.getObject(1);
			if (rs.next()) {
				request.setAttribute("MANAGER", rs.getString("emp_fname") + " " + rs.getString("emp_lname"));
			}
			cst.close();
			
			if(strJoiningDate!=null){
				uF.getTimeDuration(strJoiningDate, CF, uF, request);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}*/

	public void getClockEntries() {
		PreparedStatement pst = null;
		CallableStatement cst = null;
		ResultSet rs = null;
		try { 
			Map hmServices = CF.getServicesMap(con,true); 			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_clockentries(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("select * from attendance_details WHERE emp_id=? order by in_out_timestamp_actual desc, service_id, in_out desc limit 8");			
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			Map<String, Map<String, String>> hmMyAttendence = new LinkedHashMap<String, Map<String, String>>();
			Map<String,List<String>> hmMyAttendence1 = new LinkedHashMap<String,List<String>>();
			Map<String, String> hm = new HashMap<String, String>();

			String strDateNew = "";
			String strDateOld = "";
			String strServiceNewId = null;
			String strServiceOldId = null;
			List<String> alServices = new ArrayList<String>();
			while (rs.next()) {

				strDateNew = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat());
				strServiceNewId = rs.getString("service_id");
				if (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld)) {
					hm = new HashMap<String, String>();
				}

				if (strServiceNewId != null && !strServiceNewId.equalsIgnoreCase(strServiceOldId)) {
					hm = new HashMap<String, String>();
				}

				if ("IN".equalsIgnoreCase(rs.getString("in_out"))) {
					hm.put("IN", uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
				} else {
					hm.put("OUT", uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
				}
				hm.put("SERVICE", (String) hmServices.get(strServiceNewId));

				hmMyAttendence.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + strServiceNewId, hm);

				alServices =  hmMyAttendence1.get(strDateNew);
				if (alServices == null) {
					alServices = new ArrayList<String>();
				}
				if (!alServices.contains(strServiceNewId)) {
					alServices.add(strServiceNewId);
				}

				hmMyAttendence1.put(strDateNew, alServices);

				strDateOld = strDateNew;
				strServiceOldId = strServiceNewId;

			}
			rs.close();
			pst.close();

//			cst.close();
			request.setAttribute("hmMyAttendence", hmMyAttendence);
			request.setAttribute("hmMyAttendence1", hmMyAttendence1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getEmpSkills(UtilityFunctions uF) {
		List<List<String>> alSkills = CF.selectSkills(con, uF.parseToInt((String) session.getAttribute(EMPID)));
//		List<String> strSkillsList = CF.selectEmpSkills(con, uF.parseToInt((String) session.getAttribute(EMPID)));
//		request.setAttribute("strSkillsList", strSkillsList);
		
		request.setAttribute("alSkills", alSkills);
		String []arrEnabledModules = CF.getArrEnabledModules();
		if(ArrayUtils.contains(arrEnabledModules, MODULE_CAREER_DEV_PLANNING+"")>=0) {
			CF.employeeSkillRatingOnAssessments(con, request, CF, uF, (String) session.getAttribute(EMPID));
		}
	}
	
	public void getCertificates() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			List<String> recentAwardedEmpList = new ArrayList<String>();
			
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select emp_per_id,emp_fname,emp_mname,emp_lname,emp_image,lpd.learning_plan_id,training_id,assessment_id,certificate_status,thumbsup_status, " +
				"lpfd.added_by,lpfd.entry_date from learning_plan_finalize_details lpfd, employee_personal_details epd, learning_plan_details lpd where " +
				"lpfd.learning_plan_id =lpd.learning_plan_id and is_publish = true and lpfd.emp_id = epd.emp_per_id and epd.emp_per_id=? and " +
				"(certificate_status = true or thumbsup_status = true) order by emp_fname,emp_lname");
			pst = con.prepareStatement(strQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
//			System.out.println("pst ====> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				StringBuilder sbNewjoineeList = new StringBuilder();
				
				String empimg = uF.showData(rs.getString("emp_image"), "avatar_photo.png");
				if(CF.getStrDocRetriveLocation()==null) { 
					sbNewjoineeList.append("<span style=\"float: left; width:20px; height:20px;\"><img class=\"lazy img-circle\" style=\"border:1px solid #cccccc;\" src=\"userImages/avatar_photo.png\" data-original=\""+ DOCUMENT_LOCATION +empimg+"\" height=\"20\" width=\"20\"> </span>");
				} else { 
					sbNewjoineeList.append("<span style=\"float: left; width:20px; height:20px;\"><img class=\"lazy img-circle\" style=\"border:1px solid #cccccc;\" src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_22x22+"/"+empimg+"\" height=\"20\" width=\"20\"> </span>");
				}   
//				sbNewjoineeList.append("<span style=\"float: left; width:20px; height:20px; border:1px solid #000 \"><img class=\"lazy\" src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +empimg+"\" height=\"20\" width=\"20\"> </span>");
				
				
				sbNewjoineeList.append("<span style=\"float: left; width: 91%; margin-left: 5px;\">");
				sbNewjoineeList.append("<span style=\"float: left; width: 100%; \">");
				sbNewjoineeList.append("You");
				
				if(uF.parseToBoolean(rs.getString("certificate_status")) && uF.parseToBoolean(rs.getString("thumbsup_status"))) {
					sbNewjoineeList.append(" are awarded a certificate ");
//					sbNewjoineeList.append("<img src=\"images1/certificate_img.png\"/>");
					sbNewjoineeList.append("<a onclick=\"viewCertificate('"+rs.getString("emp_per_id")+"','"+rs.getString("learning_plan_id")+"')\" " +
							"href=\"javascript:void(0)\"><img src=\"images1/certificate_img.png\"></a>");
					sbNewjoineeList.append(" and thumbs up ");
					/*sbNewjoineeList.append("<img src=\"images1/thumbs_up.png\"/>");*/
					sbNewjoineeList.append("<i class=\"fa fa-thumbs-up\" aria-hidden=\"true\"></i>");
					
				} else if(uF.parseToBoolean(rs.getString("certificate_status"))) {
					sbNewjoineeList.append(" are awarded a certificate ");
//					sbNewjoineeList.append("<img src=\"images1/certificate_img.png\"/>");
					sbNewjoineeList.append("<a onclick=\"viewCertificate('"+rs.getString("emp_per_id")+"','"+rs.getString("learning_plan_id")+"')\" " +
					"href=\"javascript:void(0)\"><img src=\"images1/certificate_img.png\"></a>");
				} else if(uF.parseToBoolean(rs.getString("thumbsup_status"))) {
					sbNewjoineeList.append(" are awarded a thumbs up ");
					/*sbNewjoineeList.append("<img src=\"images1/thumbs_up.png\"/>");*/
					sbNewjoineeList.append("<i class=\"fa fa-thumbs-up\" aria-hidden=\"true\"></i>");
					
				}
				if(uF.parseToInt(rs.getString("training_id")) > 0) {
					String trainingName = CF.getTrainingNameByTrainingId(con, uF, rs.getString("training_id"));
					sbNewjoineeList.append(" for '"+trainingName+"' classroom training.");
				}
				if(uF.parseToInt(rs.getString("assessment_id")) > 0) {
					String assessmentName = CF.getAssessmentNameByAssessId(con, uF, rs.getString("assessment_id"));
					sbNewjoineeList.append(" for '"+assessmentName+"' assessment.");
				}
				sbNewjoineeList.append("</span>");
				sbNewjoineeList.append("<span style=\"float: left; width: 100%; font-size: 11px; font-style: italic;\">");
				String empName = CF.getEmpNameMapByEmpId(con, rs.getString("added_by"));
				sbNewjoineeList.append("Awarded by " + empName + " on " + uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				sbNewjoineeList.append("</span>");
				sbNewjoineeList.append("</span>");
				recentAwardedEmpList.add(sbNewjoineeList.toString());
			}
			rs.close();
			pst.close();
			
			request.setAttribute("recentAwardedEmpList", recentAwardedEmpList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

//	public void getCertificates() {
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		List<List<String>> certificateList = new ArrayList<List<String>>();
//		try {
//			
//			pst = con.prepareStatement("SELECT * FROM appraisal_attribute order by arribute_id");
//			rs = pst.executeQuery();
//			Map<String, String> hmAttribute = new HashMap<String, String>();
//			while(rs.next()){
//				hmAttribute.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select tp.plan_id,tp.training_title,tp.attribute_id,tp.certificate_id,cd.certificate_name,ts.end_date " +
//					"from training_schedule ts, training_plan tp, training_status tss, certificate_details cd where ts.plan_id =" +
//					" tp.plan_id and tp.certificate_id = cd.certificate_details_id and ts.emp_ids like '%,"+ strEmpId +",%'" +
//					" and tss.emp_id = "+ strEmpId +" and tss.is_completed = 1 group by tss.emp_id," +
//					"tp.plan_id,tp.training_title,tp.attribute_id,tp.certificate_id,cd.certificate_name,ts.end_date order by tp.plan_id");
//			
//			rs = pst.executeQuery();
//			while(rs.next()){
//				
//				List<String> innCertiList = new ArrayList<String>();
//				
//				innCertiList.add(rs.getString("plan_id"));
//				innCertiList.add(rs.getString("training_title"));
//				innCertiList.add(hmAttribute.get(rs.getString("attribute_id")));
//				innCertiList.add(rs.getString("certificate_name"));
//				innCertiList.add(uF.getDateFormat(rs.getString("end_date"), DBDATE, CF.getStrReportDateFormat()));
//				
//				certificateList.add(innCertiList);
//			}
//			rs.close();
//			pst.close();
////			System.out.println("certificateList =========== >> "+certificateList.toString());
//			request.setAttribute("certificateList",certificateList);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if(rs!=null){
//				try {
//					rs.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//			if(pst!=null){
//				try {
//					pst.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//		} 
//	}

//	public void getElementList() {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		try {
//			pst = con.prepareStatement("select * from appraisal_element order by appraisal_element_id");
//			rs = pst.executeQuery();
//			List<List<String>> elementouterList=new ArrayList<List<String>>();
//			while (rs.next()) {
//				List<String> innerList=new ArrayList<String>();
//				innerList.add(rs.getString("appraisal_element_id"));
//				innerList.add(rs.getString("appraisal_element_name"));
//				elementouterList.add(innerList);
//			}
//			request.setAttribute("elementouterList",elementouterList);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//	}

//	public void getEmpAttributesAndRating() {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		try {
//			Map<String, String> empLevelMap = CF.getEmpLevelMap(con);
//			
//			String empLevel = empLevelMap.get((String) session.getAttribute("EMPID"));
//			pst = con.prepareStatement("select aa.arribute_id,aa.attribute_name,aal.level_id,aal.element_id from appraisal_attribute aa, " +
//			"appraisal_attribute_level aal where aal.attribute_id = aa.arribute_id and aal.level_id = ?");
//			pst.setInt(1, uF.parseToInt(empLevel));
//			rs = pst.executeQuery();
//			Map<String,List<List<String>>> hmElementAttribute=new HashMap<String, List<List<String>>>();
//			while (rs.next()) {
//				List<String> innerList=new ArrayList<String>();
//				innerList.add(rs.getString("arribute_id"));
//				innerList.add(rs.getString("attribute_name"));
//				List<List<String>> attributeouterList=hmElementAttribute.get(rs.getString("element_id"));
//				if(attributeouterList==null) attributeouterList=new ArrayList<List<String>>();
//				attributeouterList.add(innerList);
//				hmElementAttribute.put(rs.getString("element_id"), attributeouterList);
//			}
//			Map<String, String> hmScoreAggregateMap = new HashMap<String, String>();
//			
//			pst = con.prepareStatement("select *,(marks*100/weightage) as average from (select sum(marks) as marks, sum(weightage) as weightage, aqw.appraisal_attribute " +
//			" from appraisal_question_answer aqw where emp_id=? group by aqw.appraisal_attribute) as a"); //and aqw.appraisal_attribute = ?
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				hmScoreAggregateMap.put(rs.getString("appraisal_attribute"),uF.showData(rs.getString("average"), "0"));
//			}
////			System.out.println("aggregateAttributeAvg =====> "+ aggregateAttributeAvg);
//			request.setAttribute("hmElementAttribute", hmElementAttribute);
//			request.setAttribute("hmScoreAggregateMap", hmScoreAggregateMap);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//	}

	public void getUpcomingTrainings() {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<List<String>> trainingsList = new ArrayList<List<String>>();
		try {
			
			pst = con.prepareStatement("SELECT * FROM appraisal_attribute order by arribute_id");
			rs = pst.executeQuery();
			Map<String, String> hmAttribute = new HashMap<String, String>();
			while(rs.next()){
				hmAttribute.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select tp.plan_id,tp.training_title,tp.attribute_id,ts.start_date,ts.end_date," +
					"ts.emp_ids from training_schedule ts, training_plan tp where ts.plan_id = tp.plan_id and ts.emp_ids " +
					"like '%,"+ strEmpId +",%'");
			rs = pst.executeQuery();
			while(rs.next()){
				if(rs.getString("start_date")!=null && !rs.getString("start_date").equals("")){
					boolean comparedate = uF.getCurrentDate(CF.getStrTimeZone()).before(uF.getDateFormatUtil(rs.getString("start_date"),DBDATE));
					if(comparedate == true){
						List<String> innTrainList = new ArrayList<String>();
						
						innTrainList.add(rs.getString("plan_id"));
						innTrainList.add(rs.getString("training_title"));
						innTrainList.add(hmAttribute.get(rs.getString("attribute_id")));
						innTrainList.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat()));
						
						trainingsList.add(innTrainList);
					}
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("trainingsList",trainingsList); 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
	}
	
	public void getEmpLeaveStatus() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int nEmpId = uF.parseToInt(strEmpId);
		try {
			
			Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);
			String strWlocationid = hmEmpWlocationMap.get(""+nEmpId);
			String strOrgid = CF.getEmpOrgId(con, uF, strEmpId);
			
			Map<String, String> hmLeaveDetails = new HashMap<String,String>();
			pst = con.prepareStatement("select * from emp_leave_type elt,leave_type lt where lt.leave_type_id = elt.leave_type_id " +
					" and level_id = (select dd.level_id from level_details ld, designation_details dd, grades_details gd " +
					" where ld.level_id = dd.level_id and gd.designation_id = dd.designation_id " +
					"and gd.grade_id = (select grade_id from employee_official_details where emp_id = ?)) and wlocation_id=? and elt.org_id=?" +
					" and lt.is_compensatory=false and lt.is_work_from_home=false and is_constant_balance=false ");
			pst.setInt(1, nEmpId);
			pst.setInt(2, uF.parseToInt(strWlocationid));
			pst.setInt(3, uF.parseToInt(strOrgid));
			rs = pst.executeQuery();
			while(rs.next()){
				hmLeaveDetails.put(rs.getString("leave_type_id"), rs.getString("leave_type_name"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select leave_type_id, balance from leave_register1 where register_id in(select max(register_id) from leave_register1 " +
					"where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type) group by emp_id,leave_type_id)");
            pst.setInt(1, nEmpId);
            rs = pst.executeQuery();
            Map<String, String> hmMainBalance=new HashMap<String, String>();
            while (rs.next()) {
                hmMainBalance.put(rs.getString("leave_type_id"), rs.getString("balance"));
            }
			rs.close();
			pst.close();
			
            pst = con.prepareStatement("select sum(accrued) as accrued,a.leave_type_id from (select max(_date) as daa,leave_type_id " +
            		"from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type) " +
            		"group by emp_id,leave_type_id )as a,leave_register1 lr where emp_id=? and _type!='C' and a.leave_type_id=lr.leave_type_id " +
            		"and a.daa<=lr._date group by a.leave_type_id");
            pst.setInt(1, nEmpId);
            pst.setInt(2, nEmpId);
            rs = pst.executeQuery();
            Map<String, String> hmAccruedBalance=new HashMap<String, String>();
            while (rs.next()) {
            	hmAccruedBalance.put(rs.getString("leave_type_id"), rs.getString("accrued"));                
            }
			rs.close();
			pst.close();
			
            pst = con.prepareStatement("select sum(leave_no) as count,leave_type_id from(select a.daa,lar.* from (select max(_date) as daa,leave_type_id " +
            		"from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type) " +
            		"group by emp_id,leave_type_id) as a,leave_application_register lar where emp_id=? and is_paid=true and (is_modify is null or is_modify=false) " +
            		"and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date) as a group by leave_type_id");
            pst.setInt(1, nEmpId);
            pst.setInt(2, nEmpId);
            rs = pst.executeQuery();
            Map<String, String> hmPaidBalance=new HashMap<String, String>();
            while (rs.next()) {
            	hmPaidBalance.put(rs.getString("leave_type_id"), rs.getString("count"));
            }
			rs.close();
			pst.close();
            
			int nCurrentYear = uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"));
			
			pst = con.prepareStatement("select sum(emp_no_of_leave) as cnt,leave_type_id,is_approved from emp_leave_entry where emp_id = ? and is_approved !=1 " +
					" and entrydate>=? group by leave_type_id,is_approved");
			pst.setInt(1, nEmpId);
			pst.setDate(2, uF.getDateFormat(nCurrentYear+"-01-01", DBDATE));
			rs = pst.executeQuery();
			Map<String,String> hmLeaveStatus=new HashMap<String, String>();
			while(rs.next()){
				hmLeaveStatus.put(rs.getString("leave_type_id")+"_"+rs.getString("is_approved"), ""+rs.getDouble("cnt"));				
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select sum(leave_no) as cnt,leave_type_id from leave_application_register " +
					" where emp_id = ? and is_paid=true and (is_modify is null or is_modify=false) " +
					" and _date between ? and ? group by leave_type_id");
			pst.setInt(1, nEmpId);
			pst.setDate(2, uF.getDateFormat(nCurrentYear+"-01-01", DBDATE));
			pst.setDate(3, uF.getDateFormat(nCurrentYear+"-12-31", DBDATE));
			rs = pst.executeQuery();
			Map<String,String> hmApproveLeaveStatus=new HashMap<String, String>();
			while(rs.next()){
				hmApproveLeaveStatus.put(rs.getString("leave_type_id"), ""+rs.getDouble("cnt"));				
			}
			rs.close();
			pst.close();
			
			double nPending 	= 0; 
			double nApproved 	= 0;
			double nDenied 		= 0;
			double nRemaining 	= 0;
			
			StringBuilder sbLeaveType = new StringBuilder();
			StringBuilder sbLeaveDenied = new StringBuilder();
			StringBuilder sbLeavePending = new StringBuilder();
			StringBuilder sbLeaveAppoved = new StringBuilder();
			StringBuilder sbLeaveBalance = new StringBuilder();
			
			Iterator<String> it = hmLeaveDetails.keySet().iterator();
			int i=0;
			while(it.hasNext()){
				String strLeaveTypeId = (String)it.next();
				String leaveTypeName = hmLeaveDetails.get(strLeaveTypeId);
				
				double dblBalance = uF.parseToDouble(hmMainBalance.get(strLeaveTypeId));
				dblBalance += uF.parseToDouble(hmAccruedBalance.get(strLeaveTypeId));
				
				double dblPaidBalance = uF.parseToDouble(hmPaidBalance.get(strLeaveTypeId));
				
				if(dblBalance > 0 && dblBalance >= dblPaidBalance){
		            dblBalance = dblBalance - dblPaidBalance; 
		        }
				
				nPending = uF.parseToDouble(hmLeaveStatus.get(strLeaveTypeId+"_0"));
				nApproved = uF.parseToDouble(hmApproveLeaveStatus.get(strLeaveTypeId));
				nDenied = uF.parseToDouble(hmLeaveStatus.get(strLeaveTypeId+"_-1"));
				nRemaining = dblBalance;
				
				sbLeaveType.append("'"+leaveTypeName+"'");				
				sbLeaveDenied.append(nDenied);
				sbLeavePending.append(nPending);
				sbLeaveAppoved.append(nApproved);
				sbLeaveBalance.append(nRemaining);
				if(i<hmLeaveDetails.size()-1){
					sbLeaveType.append(",");
					sbLeaveDenied.append(",");
					sbLeavePending.append(",");
					sbLeaveAppoved.append(",");
					sbLeaveBalance.append(",");
				}
			}
			
			request.setAttribute("TYPE", sbLeaveType.toString());	
			request.setAttribute("DENIED", sbLeaveDenied.toString());
			request.setAttribute("PENDING", sbLeavePending.toString());
			request.setAttribute("APPROVED", sbLeaveAppoved.toString());
			request.setAttribute("BALANCE", sbLeaveBalance.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
	}


	public void getPosition() {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder sbPosition = new StringBuilder();
//		StringBuilder sbEmpIds = new StringBuilder();
		
		try {
		
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmEmpNames = new HashMap<String, String>();
			Map<String, String> hmEmpImages = new HashMap<String, String>();
			
			pst = con.prepareStatement("SELECT * FROM employee_official_details where emp_id = ?");
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			rs = pst.executeQuery();
			String strSuprvisorId = null;
			String strGradeId = null;
			String strOrgId = null;
			while(rs.next()){
				strSuprvisorId = rs.getString("supervisor_emp_id");
				strGradeId = rs.getString("grade_id");
				strOrgId = rs.getString("org_id");
//				sbEmpIds.append(rs.getString("supervisor_emp_id")+",");
			}
			rs.close();
			pst.close();
			
			
			if(uF.parseToInt(strSuprvisorId)==0) {
				pst = con.prepareStatement("select * from org_details where org_id=?");
				pst.setInt(1, uF.parseToInt(strOrgId));
				rs = pst.executeQuery();
				while(rs.next()) {
					hmEmpNames.put("0", rs.getString("org_name"));
					
//					hmEmpImages.put("0", rs.getString("org_logo"));					
					String fileName = "";
					if(rs.getString("org_logo")!=null && !rs.getString("org_logo").equalsIgnoreCase("avatar_photo.png")){
						if(CF.getStrDocSaveLocation()==null) {
							fileName = DOCUMENT_LOCATION+rs.getString("org_logo");
						} else {
							fileName = CF.getStrDocRetriveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+I_22x22+"/"+rs.getString("org_logo");
						}	
					} else {
						fileName = "userImages/avatar_photo.png";
					}
					hmEmpImages.put("0", fileName);
					
				}
				rs.close();
				pst.close();
			}
			
			
			
			
			
//			pst = con.prepareStatement("SELECT * FROM employee_official_details where grade_id = ? and emp_id not in (?) and emp_id > 0 limit 2");
			pst = con.prepareStatement("SELECT emp_id FROM employee_personal_details epd, employee_official_details eod where is_alive= true and " +
				"eod.emp_id = epd.emp_per_id and eod.grade_id > 0 and eod.supervisor_emp_id = ? and eod.emp_id not in (?) and eod.org_id = ? and eod.emp_id > 0 limit 2");
			pst.setInt(1, uF.parseToInt(strSuprvisorId));
			pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(3, uF.parseToInt((String)session.getAttribute(ORGID)));
			rs = pst.executeQuery();
			List<String> alPeer = new ArrayList<String>();
			while(rs.next()){
				alPeer.add(rs.getString("emp_id"));
//				sbEmpIds.append(rs.getString("emp_id")+",");
			}
			rs.close();
			pst.close();
			
			
			
			pst = con.prepareStatement("select emp_per_id,emp_fname,emp_mname,emp_lname,emp_image from employee_personal_details epd join " +
	        		"employee_official_details eod on (epd.emp_per_id = eod.emp_id) join department_info di on (depart_id=dept_id) " +
	        		"join grades_details gd on eod.grade_id=gd.grade_id join designation_details dd on gd.designation_id=dd.designation_id " +
	        		"join level_details ld on dd.level_id=ld.level_id join org_details od  on ld.org_id=od.org_id where  is_alive= true " +
	        		" and emp_per_id >0 and supervisor_emp_id = ? order by emp_id"); // limit 2
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			rs = pst.executeQuery();
			List<String> alSub = new ArrayList<String>();
			while(rs.next()) {
				alSub.add(rs.getString("emp_per_id"));
//				sbEmpIds.append(rs.getString("emp_per_id")+",");
			}
			rs.close();
			pst.close();
//			sbEmpIds.append((String)session.getAttribute(EMPID));

			
//			pst = con.prepareStatement("select * from employee_personal_details where emp_per_id in ("+sbEmpIds.toString()+")");
			pst = con.prepareStatement("select * from employee_personal_details");
			rs = pst.executeQuery();
			while(rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmpNames.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
//				hmEmpImages.put(rs.getString("emp_per_id"), rs.getString("emp_image"));
				String fileName = "";
				if(rs.getString("emp_image")!=null && !rs.getString("emp_image").equalsIgnoreCase("avatar_photo.png")){
					if(CF.getStrDocSaveLocation()==null) {
						fileName = DOCUMENT_LOCATION+rs.getString("emp_image");
					} else {
						fileName = CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_22x22+"/"+rs.getString("emp_image");
					}
				} else {
					fileName = "userImages/avatar_photo.png";
				}
				hmEmpImages.put(rs.getString("emp_per_id"), fileName);
			}
			rs.close();
			pst.close();
			
			   
			   
//			String empImg = (hmEmpImages.get(strSuprvisorId) != null && !hmEmpImages.get(strSuprvisorId).equals("avatar_photo.png")) ? CF.getStrDocRetriveLocation()+hmEmpImages.get(strSuprvisorId) : "userImages/avatar_photo.png";
			String empImg = hmEmpImages.get(strSuprvisorId) != null ? hmEmpImages.get(strSuprvisorId) : "userImages/avatar_photo.png";
			sbPosition.append("<li>"+"<img class=\"lazy img-circle\" src=\""+ empImg +"\" width=\"22px\" title=\""+uF.showData(hmEmpNames.get(strSuprvisorId), "-")+"\">");
			sbPosition.append("<div class=\"emp\" style=\"margin-top: 5px;\" id=\""+strSuprvisorId+"\">"+uF.showData(hmEmpNames.get(strSuprvisorId), "N/A")+"</div>");
			sbPosition.append("");  
			sbPosition.append("<ul>");
			
			if(alPeer!=null && alPeer.size()>0) {
				empImg = hmEmpImages.get(alPeer.get(0)) != null ? hmEmpImages.get(alPeer.get(0)) : "userImages/avatar_photo.png";
//				sbPosition.append("<li>"+"<img class=\"lazy\" src=\"userImages/avatar_photo.png\"  data-original=\""+CF.getStrDocRetriveLocation()+uF.showData(hmEmpImages.get(alPeer.get(0)), "avatar_photo.png") +"\"  width=\"22px\"><br/>"+uF.showData(hmEmpNames.get(alPeer.get(0)), "N/A")+"</li>");
				sbPosition.append("<li>"+"<img class=\"lazy img-circle\" src=\"" + empImg + "\" width=\"22px\" title=\""+uF.showData(hmEmpNames.get(alPeer.get(0)), "-")+"\">");
//				sbPosition.append("<div class=\"emp\" style=\"margin-top: 5px;\" id=\""+alPeer.get(0)+"\">"+uF.showData(hmEmpNames.get(alPeer.get(0)), "N/A")+"</div>");
				sbPosition.append("</li>");
			}
			
			empImg = hmEmpImages.get(strEmpId) != null ? hmEmpImages.get(strEmpId) : "userImages/avatar_photo.png";
//			sbPosition.append("<li>You");
			sbPosition.append("<li>"+"<img class=\"lazy img-circle\" src=\"" + empImg + "\" width=\"22px\" title=\""+uF.showData(hmEmpNames.get(strEmpId), "-")+"\">");
			sbPosition.append("<div class=\"emp\" style=\"margin-top: 5px;\" id=\""+strEmpId+"\">You</div>");
			 
			sbPosition.append("<ul>");
			for(int i=0; i<alSub.size(); i++) {
				empImg = hmEmpImages.get(alSub.get(i)) != null ? hmEmpImages.get(alSub.get(i)) : "userImages/avatar_photo.png";
//				sbPosition.append("<li>"+"<img class=\"lazy\" src=\"userImages/avatar_photo.png\"  data-original=\""+CF.getStrDocRetriveLocation()+uF.showData(hmEmpImages.get(alSub.get(i)), "avatar_photo.png") +"\"  width=\"22px\"><br/>"+uF.showData(hmEmpNames.get(alSub.get(i)), "N/A")+"</li>");
				sbPosition.append("<li>"+"<img class=\"lazy img-circle\" src=\"" + empImg + "\" width=\"22px\" title=\""+uF.showData(hmEmpNames.get(alSub.get(i)), "-")+"\">");
//				sbPosition.append("<div class=\"emp\" style=\"margin-top: 5px;\" id=\""+alSub.get(i)+"\">"+uF.showData(hmEmpNames.get(alSub.get(i)), "N/A")+"</div>");
				sbPosition.append("</li>");
			}
			sbPosition.append("</ul>");
			sbPosition.append("</li>");
			
			if(alPeer!=null && alPeer.size()>1) {
				empImg = hmEmpImages.get(alPeer.get(1)) != null ? hmEmpImages.get(alPeer.get(1)) : "userImages/avatar_photo.png";
//				sbPosition.append("<li>"+"<img class=\"lazy\" src=\"userImages/avatar_photo.png\"  data-original=\""+CF.getStrDocRetriveLocation()+uF.showData(hmEmpImages.get(alPeer.get(1)), "avatar_photo.png") +"\"  width=\"22px\"><br/>"+uF.showData(hmEmpNames.get(alPeer.get(1)), "N/A")+"</li>");
				sbPosition.append("<li>"+"<img class=\"lazy img-circle\" src=\"" + empImg + "\" width=\"22px\" title=\""+uF.showData(hmEmpNames.get(alPeer.get(1)), "-")+"\">");
//				sbPosition.append("<div class=\"emp\" style=\"margin-top: 5px;\" id=\""+alPeer.get(1)+"\">"+uF.showData(hmEmpNames.get(alPeer.get(1)), "N/A")+"</div>");
				sbPosition.append("</li>");
			}
			
			sbPosition.append("</ul>");			
			sbPosition.append("</li>");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
		request.setAttribute("sbPosition", sbPosition);
	}

	
	public void getEmpRosterInfo(List<String> alServices, Map hmServices) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
//			con.setAutoCommit(false);
//			CallableStatement cst = con.prepareCall("{? = call sel_emp_roster_summary(?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			cst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.execute();
//			ResultSet rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT * FROM (SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id " +
					"and _date>= ? and emp_id=? order by _date, _from)a LIMIT 3");			
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			Map hmRoster = new LinkedHashMap();
			Map hmRoster1 = new LinkedHashMap();
			Map hm1 = new HashMap();
			String strOldDate = null;
			String strNewDate;
			String strServiceId = null;
			alServices = new ArrayList();

			while (rs.next()) {
				hm1 = new HashMap();
				strNewDate = uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat());
				strServiceId = rs.getString("service_id");

				if (strNewDate != null && !strNewDate.equalsIgnoreCase(strOldDate)) {
					hm1 = new HashMap();
				}
				hm1.put("FROM", uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
				hm1.put("TO", uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));
				hm1.put("SERVICE", (String) hmServices.get(strServiceId));

				alServices = (List) hmRoster1.get(strNewDate);
				if (alServices == null) {
					alServices = new ArrayList();
				}
				if (!alServices.contains(strServiceId)) {
					alServices.add(strServiceId);
				}

				hmRoster1.put(strNewDate, alServices);

				hmRoster.put(strNewDate + "_" + strServiceId, hm1);
				strOldDate = strNewDate;
			}
			rs.close();
			pst.close();
//			cst.close();
			request.setAttribute("hmRoster", hmRoster);
			request.setAttribute("hmRoster1", hmRoster1);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getTaskDetails() {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			Map<String, String> hmEmployeeMap = CF.getEmpNameMap(con,null, null);
					
			Map hmProjectTeamLead = new HashMap();
			pst = con.prepareStatement("select * from project_emp_details where _isteamlead = true");
			rs = pst.executeQuery();
			while(rs.next()){
				hmProjectTeamLead.put(rs.getString("pro_id"), rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			
			
			
			pst = con.prepareStatement("select activity_name,ai.pro_id,pro_name,ai.completed,ai.deadline,ai.start_date," +
					"ai.already_work  from projectmntnc pmc, activity_info ai where pmc.pro_id=ai.pro_id and ai.emp_id = ? " +
					"and ai.approve_status='n' and ai.already_work != 0 order by ai.deadline limit 4");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			List alTaskList = new ArrayList();
			List alTaskInner = new ArrayList();
			while(rs.next()){
				
				Date currentDate = uF.getCurrentDate(CF.getStrTimeZone());
				Date deadLineDate = uF.getDateFormat(rs.getString("deadline"), DBDATE);
				Date startDate = uF.getDateFormat(rs.getString("start_date"), DBDATE);
				
				alTaskInner = new ArrayList();
				
				alTaskInner.add(rs.getString("activity_name") +" ["+rs.getString("pro_name")+"]");
				
				if(uF.parseToInt(rs.getString("completed"))>=100){
					alTaskInner.add("Completed");
				}else{
					
					/*if(currentDate!=null && deadLineDate!=null && currentDate.after(deadLineDate)){
						alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/denied.png\" border=\"0\"><span style=\"color:red\">Overdue</span>");
					}else{
						alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/approved.png\" border=\"0\"><span style=\"color:green\">Working</span>");
					}*/
					
					if(currentDate!=null && deadLineDate!=null && currentDate.after(deadLineDate) && uF.parseToDouble(rs.getString("already_work"))>0){
						 /*alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/denied.png\" border=\"0\"><span style=\"color:red\">Overdue</span>");*/
						alTaskInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i><span style=\"color:red\">Overdue</span>");
					}else if(currentDate!=null && startDate!=null && currentDate.before(startDate) && uF.parseToDouble(rs.getString("already_work"))==0){
						/*alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/pullout.png\" border=\"0\"><span style=\"color:orange\">Planned</span>");*/
						alTaskInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\"></i><span style=\"color:orange\">Planned</span>");
					}else if(currentDate!=null && deadLineDate!=null && uF.parseToDouble(rs.getString("already_work"))>0){
						/*alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/approved.png\" border=\"0\"><span style=\"color:green\">Working</span>");*/
						alTaskInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i><span style=\"color:green\">Working</span>");
					}else{
						/*alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/pullout.png\" border=\"0\"><span style=\"color:orange\">Planned</span>");*/
						alTaskInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\"></i><span style=\"color:orange\">Planned</span>");
					}
				}
				
				alTaskInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
				alTaskInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("already_work"))));
				
				
				
				alTaskInner.add(uF.showData((String)hmEmployeeMap.get((String)hmProjectTeamLead.get(rs.getString("pro_id"))), ""));
				
				alTaskList.add(alTaskInner);
				
			}
			rs.close();
			pst.close();
			request.setAttribute("alTaskList",alTaskList);
//			System.out.println("alTaskList===>"+alTaskList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getUpcomingRequests() {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<List<String>> leaveList = new ArrayList<List<String>>();
		List<List<String>> reimbursList = new ArrayList<List<String>>();
//		List<List<List<String>>> requestList = new ArrayList<List<List<String>>>();
		try {
			
			
			pst = con.prepareStatement("select leave_id,emp_id,leave_from,leave_to,entrydate,leave_type_id,is_approved " +
					"from emp_leave_entry where emp_id = ? and leave_from > ? order by leave_from limit 5");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()){
				List<String> innLeaveList = new ArrayList<String>();
				
				innLeaveList.add(rs.getString("leave_id"));
				innLeaveList.add(uF.getDateFormat(rs.getString("leave_from"), DBDATE, CF.getStrReportDateFormat()));
				innLeaveList.add(uF.getDateFormat(rs.getString("leave_to"), DBDATE, CF.getStrReportDateFormat()));
//				innReqList.add(rs.getString("is_approved"));
				if(uF.parseToInt(rs.getString("is_approved"))==-1){
					/*innLeaveList.add("<img width=\"16px\" src=\"images1/icons/denied.png\">");*/
					innLeaveList.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
				}else if(uF.parseToInt(rs.getString("is_approved"))==1){
					/*innLeaveList.add("<img width=\"16px\" src=\"images1/icons/approved.png\">");*/
					innLeaveList.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
				} else{
					/*innLeaveList.add("<img width=\"16px\" src=\"images1/icons/pending.png\">");*/
					innLeaveList.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\"></i>");
				}
//				innReqList.add(uF.getDateFormat(rs.getString("end_date"), DBDATE, CF.getStrReportDateFormat()));
				
				leaveList.add(innLeaveList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select reimbursement_id,from_date,to_date,reimbursement_type,reimbursement_amount," +
					"emp_id,approval_1,approval_2,ispaid,entry_date from emp_reimbursement where emp_id = ? and from_date > ? " + //and from_date > ?
					" order by from_date limit 5");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()){
				List<String> innReimbursList = new ArrayList<String>();
				
				innReimbursList.add(rs.getString("reimbursement_id"));
				innReimbursList.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));
				innReimbursList.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
				innReimbursList.add(rs.getString("reimbursement_amount"));
				if(uF.parseToInt(rs.getString("approval_1"))==-1 && uF.parseToInt(rs.getString("approval_2"))==-1){
					 /*innReimbursList.add("<img width=\"16px\" src=\"images1/icons/denied.png\">");*/
					innReimbursList.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
					
				}else if(uF.parseToInt(rs.getString("approval_1"))==1 && uF.parseToInt(rs.getString("approval_2"))==1){
					/*innReimbursList.add("<img width=\"16px\" src=\"images1/icons/approved.png\">");*/
					innReimbursList.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
				} else{
					/*innReimbursList.add("<img width=\"16px\" src=\"images1/icons/pending.png\">");*/
					innReimbursList.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\"></i>");
				}
				reimbursList.add(innReimbursList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reimbursList",reimbursList);
			request.setAttribute("leaveList",leaveList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getAchievements() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			Map<String, String> hmGradeMap = new HashMap<String, String>();
			Map<String, String> hmDesigMap = new HashMap<String, String>();
			pst = con.prepareStatement("select * from designation_details dd, grades_details gd where dd.designation_id = gd.designation_id");
			rs = pst.executeQuery();
			while(rs.next()){
				hmGradeMap.put(rs.getString("grade_id"), rs.getString("grade_name"));
				hmDesigMap.put(rs.getString("designation_id"), rs.getString("designation_name"));
			}
			rs.close();
			pst.close();
			
			
			
			
			pst = con.prepareStatement("select * from employee_activity_details ead, activity_details ad where ead.activity_id = ad.activity_id and emp_id = ? and is_achievements = true order by effective_date desc limit 4");
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			rs = pst.executeQuery();
			List<String> alAchievements = new ArrayList<String>();
			while(rs.next()){
				getAchievements(con, uF.parseToInt(rs.getString("activity_id")), uF, alAchievements, uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()), hmGradeMap.get(rs.getString("grade_id")), hmDesigMap.get(rs.getString("desig_id")));
			}
			rs.close();
			pst.close();
			
			StringBuilder sb = new StringBuilder();
			int iA=0; 
			for(iA=0; alAchievements!=null && iA<alAchievements.size(); iA++){
				sb.append("<div style=\"float: left; width:100%;border-bottom: 1px solid #eee;\">");
				sb.append("<div style=\"float:left;margin-right:5px\"><img height=\"35px\" src=\"images1/trophy.png\"></div><div>"+alAchievements.get(iA)+" </div>");
				sb.append("</div>");
			}
			
			if(iA==0){
				sb.append("<div style=\"float:left;margin-right:5px\">No achievements made yet.</div>");
			}
			
			
			
			request.setAttribute("alAchievements", alAchievements);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
public void getAchievements(Connection con, int nId, UtilityFunctions uF, List<String> alAchievements, String effectiveDate, String strNewGrade, String strNewDesignation){
		
		try {
		

			switch(nId){
			case 1:
				alAchievements.add("An <strong>increment</strong> is given to you since <strong>"+effectiveDate+"</strong>.");
				break;
			case 2:
				alAchievements.add("Wow! You got <strong>double increment</strong> since <strong>"+effectiveDate+"</strong>.");
				break;
			case 5:
				alAchievements.add("Your grade is revised to <strong>"+uF.showData(strNewGrade, "Na")+"</strong> since <strong>"+effectiveDate+"</strong>.");
				break;
			case 6:
				alAchievements.add("You are given a <strong>promotion</strong> since <strong>"+effectiveDate+"</strong>.");
				break;
			case 9:
				alAchievements.add("You are marked <strong>permananet</strong> since <strong>"+effectiveDate+"</strong>. Now you can avail benefits as per your level.");
				break;
			case 16:
				
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
				if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
				
				Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
				if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
				
				String strCurrency = "";
				if(uF.parseToInt(hmEmpCurrency.get((String)session.getAttribute(EMPID))) > 0){
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get((String)session.getAttribute(EMPID)));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				}
				
				int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
				String strNewGrossSalary = null;
				if(nSalaryStrucuterType == S_GRADE_WISE){
					String strEmpGradeId = CF.getEmpGradeId(con, (String)session.getAttribute(EMPID));
					PreparedStatement pst = con.prepareStatement("select sum(amount) as amount " +
							"from emp_salary_details where emp_id=? and effective_date = ? " +
							"and earning_deduction = 'E' and isdisplay = true and grade_id=?");
					pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
					pst.setDate(2, uF.getDateFormat(effectiveDate, CF.getStrReportDateFormat()));
					pst.setInt(6, uF.parseToInt(strEmpGradeId));
					ResultSet rs = pst.executeQuery();					
					while(rs.next()){
						strNewGrossSalary = strCurrency +uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount")));
					}
					rs.close();
					pst.close();
				} else {
					String strEmpLevelId = CF.getEmpLevelId(con, (String)session.getAttribute(EMPID));
					PreparedStatement pst = con.prepareStatement("select sum(amount) as amount " +
							"from emp_salary_details where emp_id=? and effective_date = ? " +
							"and earning_deduction = 'E' and isdisplay = true and level_id=?");
					pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
					pst.setDate(2, uF.getDateFormat(effectiveDate, CF.getStrReportDateFormat()));
					pst.setInt(6, uF.parseToInt(strEmpLevelId));
					ResultSet rs = pst.executeQuery();					
					while(rs.next()){
						strNewGrossSalary = strCurrency +uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount")));
					}
					rs.close();
					pst.close();
				}
				alAchievements.add("Your salary has been revised to "+strNewGrossSalary+" since "+effectiveDate+".");
				break;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void getMyTeam() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
//			pst = con.prepareStatement("select emp_ids from goal_details where goal_type = ? and emp_ids like '%,"+strEmpId+",%'");
//			pst.setInt(1, TEAM_GOAL);
//			rs = pst.executeQuery();
//			StringBuilder sb=new StringBuilder();
//			int i=0;
//			while(rs.next()) {
//				String emp_ids=rs.getString("emp_ids");
//				emp_ids=emp_ids.substring(1, emp_ids.length()-1);
//				if(i==0) {
//					sb.append(emp_ids);
//				}else {
//					sb.append(","+emp_ids);
//				}
//				i++;
//			}
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmEmp = new HashMap<String, String>();
			Map<String,String> empImageMap=new HashMap<String,String>();
//			if(sb.length()>0) {				
				pst=con.prepareStatement("select emp_per_id,emp_fname,emp_mname,emp_lname,emp_image from employee_personal_details epd join " +
	        		"employee_official_details eod on (epd.emp_per_id = eod.emp_id) join department_info di on (depart_id=dept_id) " +
	        		"join grades_details gd on eod.grade_id=gd.grade_id join designation_details dd on gd.designation_id=dd.designation_id " +
	        		"join level_details ld on dd.level_id=ld.level_id join org_details od  on ld.org_id=od.org_id where  is_alive= true " +
	        		" and emp_per_id >0 and supervisor_emp_id=? order by emp_id"); //(supervisor_emp_id=? or emp_per_id =?) 
				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setInt(2, uF.parseToInt(strEmpId));
//				System.out.println("pst ===>> " + pst);
				rs=pst.executeQuery();
				while(rs.next()) {
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
				
					
					hmEmp.put(rs.getString("emp_per_id"), rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
					empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
				}
				rs.close();
				pst.close();
//			}
			request.setAttribute("hmEmp", hmEmp);
			request.setAttribute("empImageMap", empImageMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getMyTeamRating() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			StringBuilder sbEmpIds = null;
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_per_id from employee_personal_details epd,employee_official_details eod where " +
				"epd.emp_per_id = eod.emp_id and is_alive= true and emp_per_id>0 and supervisor_emp_id = ? order by emp_id ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(sbEmpIds == null) {
					sbEmpIds = new StringBuilder();
					sbEmpIds.append(rs.getString("emp_per_id"));
				} else {
					sbEmpIds.append(","+rs.getString("emp_per_id"));
				}
			}
			rs.close();
			pst.close();
			if(sbEmpIds == null) {
				sbEmpIds = new StringBuilder();
			}
			
//			System.out.println("sbEmpIds ===>> "  +sbEmpIds.toString());
			
			if(sbEmpIds != null && !sbEmpIds.toString().equals("")) {
				List<String> alElementIds = new ArrayList<String>();
				Map<String, String> hmAnalysisSummaryMap = new HashMap<String, String>();
				Map<String, String> hmReviewAnalysisSummary = new HashMap<String, String>();
				
				String strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 181)+"", DBDATE, DATE_FORMAT);
				String strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
				
				sbQuery = new StringBuilder();
				sbQuery.append("select *,((marks*100/weightage)) as average from ( select sum(marks) as marks, sum(weightage) as weightage,a.appraisal_element" +
					" from (select appraisal_element,appraisal_attribute from appraisal_element_attribute where appraisal_attribute in (select " +
					"aa.arribute_id from appraisal_attribute aa, appraisal_attribute_level aal where aal.attribute_id = aa.arribute_id) group by " +
					"appraisal_element,appraisal_attribute) as a,appraisal_question_answer aqw where a.appraisal_attribute=aqw.appraisal_attribute ");  //aqw.emp_id != "+uF.parseToInt(strEmpId)+"
				sbQuery.append("and aqw.emp_id in ("+sbEmpIds.toString()+") ");
				sbQuery.append(" and aqw.appraisal_attribute >0 and aqw.attempted_on between ? and ? and weightage>0 group by a.appraisal_element) as aa order by aa.appraisal_element");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
	//			System.out.println("PST is in getAnalysisSummary =========== >" + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
//						totAverage += uF.parseToDouble(rs.getString("average"));
					hmReviewAnalysisSummary.put(rs.getString("appraisal_element"), rs.getString("average"));
					if(rs.getInt("appraisal_element")>0 && !alElementIds.contains(rs.getString("appraisal_element"))) {
						alElementIds.add(rs.getString("appraisal_element"));
					}
				}
				rs.close();
				pst.close();
				
			
//				System.out.println("hmReviewAnalysisSummary ===>> " + hmReviewAnalysisSummary);
				
				sbQuery = new StringBuilder();
				sbQuery.append("select gksrd.*, gd.goal_id,gd.goal_element from goal_kra_status_rating_details gksrd, goal_details gd " +
						"where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+sbEmpIds.toString()+") ");
				pst=con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs=pst.executeQuery();
				Map<String, String> hmElementwiseRatingAndCount = new HashMap<String, String>();
				while (rs.next()) {
					double totRating = uF.parseToDouble(hmElementwiseRatingAndCount.get(rs.getString("goal_element")+"_RATING"));
					int totCount = uF.parseToInt(hmElementwiseRatingAndCount.get(rs.getString("goal_element")+"_COUNT"));
					
					if((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals("")) || (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals("")) ) {
						double strCurrGoalORTargetRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
						if(rs.getString("manager_rating") == null) {
							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("hr_rating"));
						} else if(rs.getString("hr_rating") == null) {
							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("manager_rating"));
						}
						totRating += strCurrGoalORTargetRating;
						totCount++;
						hmElementwiseRatingAndCount.put(rs.getString("goal_element")+"_RATING", totRating+"");
						hmElementwiseRatingAndCount.put(rs.getString("goal_element")+"_COUNT", totCount+"");
					}
					if(rs.getInt("goal_element")>0 && !alElementIds.contains(rs.getString("goal_element"))) {
						alElementIds.add(rs.getString("goal_element"));
					}
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmGKTAnalysisSummary = new HashMap<String, String>();
				for (int i = 0; alElementIds != null && i<alElementIds.size(); i++) {
					double elementAvgScore = 0.0d;
					if(uF.parseToDouble(hmElementwiseRatingAndCount.get(alElementIds.get(i)+"_COUNT")) > 0) {
						elementAvgScore = (uF.parseToDouble(hmElementwiseRatingAndCount.get(alElementIds.get(i)+"_RATING"))/uF.parseToDouble(hmElementwiseRatingAndCount.get(alElementIds.get(i)+"_COUNT"))) * 20;
					}
					hmGKTAnalysisSummary.put(alElementIds.get(i), elementAvgScore+"");
				}
//				System.out.println("hmGKTAnalysisSummary ===>> " + hmGKTAnalysisSummary);
//				request.setAttribute("hmGKTAnalysisSummary", hmGKTAnalysisSummary);
				
				Map<String, String> hmEmpwiseGKTAnalysisSummary = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(gksrd.user_rating) as user_rating, count(*) as cnt, gd.goal_element from goal_kra_emp_status_rating_details gksrd," +
					" goal_details gd where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+sbEmpIds.toString()+") and user_type != '-' ");
				sbQuery.append(" group by gd.goal_element");
				pst=con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs=pst.executeQuery();
				while (rs.next()) {
					if((rs.getString("user_rating") != null && !rs.getString("user_rating").equals("")) ) {
	//						double strCurrGoalORTargetRating = rs.getDouble("user_rating")/ rs.getInt("cnt");
						double elementEmpAvgScore = (rs.getDouble("user_rating") / rs.getDouble("cnt")) * 20;
						hmEmpwiseGKTAnalysisSummary.put(rs.getString("goal_element"), elementEmpAvgScore+"");
						if(rs.getInt("goal_element")>0 && !alElementIds.contains(rs.getString("goal_element"))) {
							alElementIds.add(rs.getString("goal_element"));
						}
					}
				}
				rs.close();
				pst.close();
//				System.out.println("hmEmpwiseGKTAnalysisSummary ===>> " + hmEmpwiseGKTAnalysisSummary);
//				request.setAttribute("hmEmpwiseGKTAnalysisSummary",hmEmpwiseGKTAnalysisSummary);
				
//				System.out.println("alElementIds ===>> " + alElementIds);
				double totAverage = 0.0d;
				for (int i = 0; alElementIds != null && i<alElementIds.size(); i++) {
					double dblTotScore = 0.0d;
					int intTotCount = 0;
					if(hmReviewAnalysisSummary != null && uF.parseToDouble(hmReviewAnalysisSummary.get(alElementIds.get(i)))>0) {
						dblTotScore += uF.parseToDouble(hmReviewAnalysisSummary.get(alElementIds.get(i)));
						intTotCount++;
					}
					if(hmGKTAnalysisSummary != null && uF.parseToDouble(hmGKTAnalysisSummary.get(alElementIds.get(i)))>0) {
						dblTotScore += uF.parseToDouble(hmGKTAnalysisSummary.get(alElementIds.get(i)));
						intTotCount++;
					}
					if(hmEmpwiseGKTAnalysisSummary != null && uF.parseToDouble(hmEmpwiseGKTAnalysisSummary.get(alElementIds.get(i)))>0) {
						dblTotScore += uF.parseToDouble(hmEmpwiseGKTAnalysisSummary.get(alElementIds.get(i)));
						intTotCount++;
					}
//					System.out.println("dblTotScore ===>> " + dblTotScore);
					double elementwiseAvgScore = 0.0d;
					if(intTotCount>0) {
						elementwiseAvgScore = dblTotScore / uF.parseToDouble(intTotCount+"");
					}
					totAverage += elementwiseAvgScore;
					hmAnalysisSummaryMap.put(alElementIds.get(i), elementwiseAvgScore+"");
				}
//				System.out.println("totAverage ===>> " + totAverage);
				totAverage = (totAverage / 2) / 20;
//				System.out.println("totAverage after ===>> " + totAverage);
//				request.setAttribute("hmAnalysisSummaryMap", hmAnalysisSummaryMap);
//				request.setAttribute("totAverage", totAverage+"");
				request.setAttribute("dblMyRatings", ""+uF.formatIntoOneDecimal(totAverage));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	public void verifyClockDetails() {
		
		ResultSet rs = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		
		try {
			
			String strPrevRosterDate = null;
			Time tPrevFrom = null;
			Time tPrevTo = null;
			
			String strRosterDate = null;
			String strFrom = null;
			String strTo = null;
			
			String strRosterStartTime = null;
			String strRosterEndTime = null;
			String strPrevRosterStartTime = null;
			String strPrevRosterEndTime = null;
			
			
			int nPrevServiceId = 0;
			int nCurrServiceId = 0;
			int nCount = 0;
			
			
			
//			pst = con.prepareStatement(selectRoster_N_COUNT);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_roster_count(?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectRoster_N_COUNT);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			
			while(rs.next()) {
				nCount = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			
//			pst = con.prepareStatement(selectRosterClockDetails_PREV_N_OUT);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_roster_clock_entry_out(?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone()));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectRosterClockDetails_PREV_N_OUT);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			
			while(rs.next()) {
				nPrevServiceId = rs.getInt("service_id");
			}
			rs.close();
			pst.close();
			
			
//			pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_roster_clock_entry_out(?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone()));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()) {
				nCurrServiceId = rs.getInt("service_id");
			}
			rs.close();
			pst.close();
			
			
			if(nCurrServiceId==0) {
				if(nCount>1) {
//					pst = con.prepareStatement(selectRosterClockDetails_N_IN);
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
					
					
//					con.setAutoCommit(false);
//					cst = con.prepareCall("{? = call sel_emp_roster_clock_entry_in(?,?,?)}");
//					cst.registerOutParameter(1, Types.OTHER);
//					cst.setInt(2, uF.parseToInt(strEmpId));
//					cst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//					cst.setTime(4, uF.getCurrentTime(CF.getStrTimeZone()));
					pst = con.prepareStatement(selectRosterClockDetails_N_IN);
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
					
				}else {
//					pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					
					
//					con.setAutoCommit(false);
//					cst = con.prepareCall("{? = call sel_emp_roster(?,?)}");
//					cst.registerOutParameter(1, Types.OTHER);
//					cst.setInt(2, uF.parseToInt(strEmpId));
//					cst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				}
				
//				cst.execute();
//				rs = (ResultSet) cst.getObject(1);
				rs = pst.executeQuery();
				while(rs.next()){
					nCurrServiceId = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
			}
			
			
			if(nCurrServiceId==0){
				nCurrServiceId = nPrevServiceId;
			}
			
			
			
			
//			pst = con.prepareStatement(selectAttendenceClockDetails_N);
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
//			pst.setString(2, "OUT");
//			pst.setInt(3, uF.parseToInt(strEmpId));
//			pst.setInt(4, nPrevServiceId);
//			rs = pst.executeQuery();
//			
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_attendance_clock_details(?,?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//			cst.setString(3, "OUT");
//			cst.setInt(4, uF.parseToInt(strEmpId));
//			cst.setInt(5, nPrevServiceId);
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectAttendenceClockDetails_N);
			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
			pst.setString(2, "OUT");
			pst.setInt(3, uF.parseToInt(strEmpId));
			pst.setInt(4, nPrevServiceId);
			rs = pst.executeQuery();

			boolean isPrevOut = false;
			boolean isPrevRoster = false;
			if(rs.next()){
				isPrevOut = true;
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement(selectAttendenceClockDetails_N);
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setString(2, "IN");
//			pst.setInt(3, uF.parseToInt(strEmpId));
//			pst.setInt(4, nCurrServiceId);
//			rs = pst.executeQuery();

			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_attendance_clock_details(?,?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//			cst.setString(3, "IN");
//			cst.setInt(4, uF.parseToInt(strEmpId));
//			cst.setInt(5, nPrevServiceId);
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectAttendenceClockDetails_N);
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(2, "IN");
			pst.setInt(3, uF.parseToInt(strEmpId));
			pst.setInt(4, nCurrServiceId);
			rs = pst.executeQuery();
			
			boolean isCurrIn = false;
			if(rs.next()){
				isCurrIn = true;
			}
			rs.close();
			pst.close();
			
			if(!isCurrIn && !isPrevOut){
				
//				pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//				rs = pst.executeQuery();
				
				
//				con.setAutoCommit(false);
//				cst = con.prepareCall("{? = call sel_emp_roster_clock_entry_out(?,?)}");
//				cst.registerOutParameter(1, Types.OTHER);
//				cst.setInt(2, uF.parseToInt(strEmpId));
//				cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone()));
//				cst.execute();
//				rs = (ResultSet) cst.getObject(1);
				pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
				rs = pst.executeQuery();
				if(rs.next()){
					isPrevRoster = true;
					
					tPrevFrom = rs.getTime("_from");
					tPrevTo = rs.getTime("_to");
					strPrevRosterDate = rs.getString("_date");
				}
				rs.close();
				pst.close();
				
			}
			
			
			
			
			if(tPrevFrom!=null && tPrevTo!=null && tPrevFrom.getTime() < tPrevTo.getTime()){
				
//				pst = con.prepareStatement(selectRosterClockDetails_N_IN);
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//				pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//				rs = pst.executeQuery();

				
//				con.setAutoCommit(false);
//				cst = con.prepareCall("{? = call sel_emp_roster_clock_entry_in(?,?,?)}");
//				cst.registerOutParameter(1, Types.OTHER);
//				cst.setInt(2, uF.parseToInt(strEmpId));
//				cst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//				cst.setTime(4, uF.getCurrentTime(CF.getStrTimeZone()));
//				cst.execute();
//				rs = (ResultSet) cst.getObject(1);
				pst = con.prepareStatement(selectRosterClockDetails_N_IN);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
				rs = pst.executeQuery();
				strRosterDate = null;
				strFrom = null;
				strTo = null;

				while (rs.next()) {
					strPrevRosterStartTime = uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat());
					strPrevRosterEndTime = uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat());
					strRosterDate = rs.getString("_date");
				}
				rs.close();
				pst.close();

//				cst.close();
			}else if(tPrevFrom!=null && tPrevTo!=null && tPrevFrom.getTime() > tPrevTo.getTime()){
//				pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//				rs = pst.executeQuery();
				
				
//				con.setAutoCommit(false);
//				cst = con.prepareCall("{? = call sel_emp_roster_clock_entry_out(?,?)}");
//				cst.registerOutParameter(1, Types.OTHER);
//				cst.setInt(2, uF.parseToInt(strEmpId));
//				cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone()));
//				cst.execute();
//				rs = (ResultSet) cst.getObject(1);
				pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
				rs = pst.executeQuery();
				strRosterDate = null;
				strFrom = null;
				strTo = null;

				
				while (rs.next()) {
					strPrevRosterStartTime = uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat());
					strPrevRosterEndTime = uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat());
					strRosterDate = rs.getString("_date");
				}
				rs.close();
				pst.close();

//				cst.close();
			}else{
				
//				pst = con.prepareStatement(selectRosterClockDetails_N1);
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//				pst.setInt(3, nCurrServiceId);
//				rs = pst.executeQuery();
				
				
//				con.setAutoCommit(false);
//				cst = con.prepareCall("{? = call sel_emp_roster(?,?,?)}");
//				cst.registerOutParameter(1, Types.OTHER);
//				cst.setInt(2, uF.parseToInt(strEmpId));
//				cst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//				cst.setInt(4, nCurrServiceId);
//				cst.execute();
//				rs = (ResultSet) cst.getObject(1);
				pst = con.prepareStatement(selectRosterClockDetails_N1);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(3, nCurrServiceId);
				rs = pst.executeQuery();
				
				strRosterDate = null;
				strFrom = null;
				strTo = null;

				while (rs.next()) {
					strRosterStartTime = uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat());
					strRosterEndTime = uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat());
					strRosterDate = rs.getString("_date");
				}
				rs.close();
				pst.close();
				
//				cst.close();
			}
			
			
			
			
			
			
//			pst = con.prepareStatement(selectAttendenceClockDetails1_N);
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));			
//			pst.setInt(2, uF.parseToInt(strEmpId));
//			pst.setInt(3, nCurrServiceId);
//			rs = pst.executeQuery();
//			
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_attendance_clock_details(?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));			
//			cst.setInt(3, uF.parseToInt(strEmpId));
//			cst.setInt(4, nCurrServiceId);
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectAttendenceClockDetails1_N);
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));			
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setInt(3, nCurrServiceId);
			rs = pst.executeQuery();
			
			
			
			boolean isIn=false;
			boolean isOut=false;
			
			while (rs.next()) {

				if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
					isIn=true;
				}else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
					isOut=true;
				}
				
			}
			rs.close();
			pst.close();
			
			if(isIn && isOut){
				request.setAttribute("ROSTER_TIME", ((strRosterEndTime!=null)?"Your rostered end time was :"+strRosterEndTime:""));
			}else if(!isIn && !isOut && strRosterStartTime!=null){
				request.setAttribute("ROSTER_TIME", ((strRosterStartTime!=null)?"Your rostered start time is :"+strRosterStartTime:""));
			}else if(!isIn && !isOut && strPrevRosterStartTime!=null){
				
//				pst = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
//				pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
//				pst.setString(2, "IN");
//				pst.setString(3, "OUT");
//				pst.setInt(4, uF.parseToInt(strEmpId));
//				pst.setInt(5, nPrevServiceId);
//				rs = pst.executeQuery();
//				
//				
				
//				con.setAutoCommit(false);
//				cst = con.prepareCall("{? = call sel_emp_attendance_clock_details(?,?,?,?,?)}");
//				cst.registerOutParameter(1, Types.OTHER);
//				cst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//				cst.setString(3, "IN");
//				cst.setString(4, "OUT");
//				cst.setInt(5, uF.parseToInt(strEmpId));
//				cst.setInt(6, nPrevServiceId);
//				cst.execute();
//				rs = (ResultSet) cst.getObject(1);
				pst = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
				pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
				pst.setString(2, "IN");
				pst.setString(3, "OUT");
				pst.setInt(4, uF.parseToInt(strEmpId));
				pst.setInt(5, nPrevServiceId);
				rs = pst.executeQuery();
				
				boolean isPrevIn = false;
				isPrevOut = false;
				
				while(rs.next()){
					
					if(rs.getString("in_out").equalsIgnoreCase("IN")){
						isPrevIn = true;
					}else if(rs.getString("in_out").equalsIgnoreCase("OUT")){
						isPrevOut = true;
					} 
				}
				rs.close();
				pst.close();
				
				if(isPrevIn && isPrevOut){
					request.setAttribute("ROSTER_TIME", "Your are not rostered for today");
				}else if(isPrevIn && !isPrevOut){
					request.setAttribute("ROSTER_TIME", ((strPrevRosterEndTime!=null)?"Your rostered end time is :"+strPrevRosterEndTime:""));
				}else if(!isPrevIn && !isPrevOut && CF.isRosterDependency(con,strEmpId)){
//					request.setAttribute("ROSTER_TIME", ("Your are not rostered for today"));
					request.setAttribute("ROSTER_TIME", ((strPrevRosterStartTime!=null)?"Your rostered start time is :"+strPrevRosterStartTime:""));
//				}else if(!isPrevIn && !isPrevOut && !new CommonFunctions().isRosterDependency(con,strEmpId)){
				}else if(!isPrevIn && !isPrevOut && !CF.isRosterDependency(con,strEmpId)){	
					request.setAttribute("ROSTER_TIME", ((strPrevRosterStartTime!=null)?"Your rostered start time is :"+strPrevRosterStartTime:""));					
				}
				
				
			}else if(isIn){
				request.setAttribute("ROSTER_TIME", ((strRosterEndTime!=null)?"Your rostered end time is :"+strRosterEndTime:""));
			}else if(isOut){
				request.setAttribute("ROSTER_TIME", ((strRosterEndTime!=null)?"Your rostered start time is :"+strRosterStartTime:""));
			}
			
			
			
			
			
			/**
			 *  IS Roster Dependent
			 */
			
//			pst = con.prepareStatement(selectRosterDependent);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			rs = pst.executeQuery();
//			
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_details(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT * FROM employee_official_details eod, employee_personal_details epd WHERE eod.emp_id = epd.emp_per_id AND emp_id = ?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			String strEmpType = null;
			boolean isRoster = false;
			if(rs.next()){
				
				strEmpType = rs.getString("emptype");
				isRoster = uF.parseToBoolean(rs.getString("is_roster"));
				
				
			}
			rs.close();
			pst.close();
			
			
			if(strEmpType!=null && !isRoster){
				
//				pst = con.prepareStatement(selectAttendenceClockDetailsInOut);
//				pst.setDate(1, uF.getCurrentDate());
//				pst.setString(2, "IN");
//				pst.setString(3, "OUT");
//				pst.setInt(4, uF.parseToInt(strEmpId));
//				rs = pst.executeQuery();
//				
//				while(rs.next()){
//					
//					if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
//						isIn = true;	
//					}else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
//						isOut = true;
//					}
//				}
//				
//				
//				
				request.setAttribute("ROSTER_TIME", "");
				
				
			}
			
			
			
			request.setAttribute("CURRENT_DATE", uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "EEEE "+CF.getStrReportDateFormat()));
			request.setAttribute("CURRENT_TIME", uF.getDateFormat(uF.getCurrentTime(CF.getStrTimeZone())+"", DBTIME, CF.getStrReportTimeFormat()));
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
		
	}

	public void getPaySlipStatus() {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement("select count(*) as count from payroll_generation where emp_id=? and entry_date between ? and ?");			
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 7));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			
			int nPaySlipGeneration = 0;
			while (rs.next()) {
				nPaySlipGeneration = rs.getInt("count");
			}
			rs.close();
			pst.close();
			session.setAttribute("PAYSLIP_GENERATION",nPaySlipGeneration+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}  
	}

	public void getPendingExceptionCount() {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			
			
			if(!CF.isRosterDependency(con,strEmpId))return;
			
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))-1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy")));

			int nMinDate = cal.getActualMinimum(Calendar.DATE);
			int nMaxDate = cal.getActualMaximum(Calendar.DATE);
			
			String strDate1 = nMinDate +"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR);
			String strDate2 = nMaxDate +"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR);
			
			
			
			
			
			Map<String, String> hmExceptionDates = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from exception_reason where _date between ? and ? and emp_id = ?");
			pst.setDate(1, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(3, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while(rs.next()){
				hmExceptionDates.put(rs.getString("_date"), rs.getString("status"));
			}
			rs.close();
			pst.close();
			
			
			
			pst = con.prepareStatement("select * from roster_details rd left join ( select * from attendance_details where to_date(in_out_timestamp::text,'YYYY-MM-DD') between ? and ? and emp_id = ? order by to_date(in_out_timestamp::text,'YYYY-MM-DD'))a on rd.emp_id = a.emp_id and rd._date = to_date(a.in_out_timestamp::text,'YYYY-MM-DD') and rd.service_id = a.service_id where _date between ? and ? and rd.emp_id = ? order by _date desc");			
			pst.setDate(1, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(3, uF.parseToInt(strEmpId));
			pst.setDate(4, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt(strEmpId));
			
			rs = pst.executeQuery();
			
			
//			System.out.println("pst======>"+pst);
			
			int nPendingExceptionCount = 0;
			int nWaitingExceptionCount = 0;
			
			String strCurrentDate = uF.getCurrentDate(CF.getStrTimeZone())+"";
			
			String strDateNew = null;
			String strDateOld = null;
			boolean isIn = false;
			boolean isOut = false;
			while (rs.next()) {
				
				strDateNew = rs.getString("_date");
				String strApproval  = rs.getString("approved");
				
				if(strDateNew!=null && strDateOld!=null && !strDateNew.equals(strDateOld)){
					if(!isIn || !isOut){
						int nStatus = uF.parseToInt((String)hmExceptionDates.get(strDateOld));
						
						if(nStatus==0 && hmExceptionDates.containsKey(strDateOld)){
							nPendingExceptionCount++;
						}else if(strCurrentDate.equals(strDateOld) && isIn){
							
						}else if(nStatus==0){
							nWaitingExceptionCount++;
						}
					}
					isIn = false;
					isOut = false;
				}
				
				if("IN".equalsIgnoreCase(rs.getString("in_out"))){
					isIn = true;
				}else if("OUT".equalsIgnoreCase(rs.getString("in_out"))){
					isOut = true;
				}
				
				
				if(uF.parseToInt(strApproval)==-2){
					nPendingExceptionCount++;
//					System.out.println("Date= IN ==>"+rs.getString("_date")+" "+rs.getString("in_out"));
				}
				
				/*if(rs.getString("approved")==null || (rs.getString("approved")!=null && rs.getString("approved").length()==0)){
					int nStatus = uF.parseToInt((String)hmExceptionDates.get(rs.getString("_date")));
					if(nStatus==0){
						nPendingExceptionCount++;
						System.out.println("Date= IN ==>"+rs.getString("_date")+" "+rs.getString("in_out"));
					}
				}*/
				
				
				strDateOld = strDateNew;
				
			}
			rs.close();
			pst.close();
			
			if(strDateNew!=null && strDateOld!=null && strDateNew.equals(strDateOld)){
				if(!isIn || !isOut){
					int nStatus = uF.parseToInt((String)hmExceptionDates.get(strDateOld));
					
					if(nStatus==0 && hmExceptionDates.containsKey(strDateOld)){
						nPendingExceptionCount++;
					}else if(strCurrentDate.equals(strDateOld) && isIn){
						
					}else if(nStatus==0){
						nWaitingExceptionCount++;
					}
				}
				isIn = false;
				isOut = false;
			}
			
			session.setAttribute("PENDING_EXCEPTION_COUNT",(nPendingExceptionCount+nWaitingExceptionCount)+"");
			request.setAttribute("PENDING_EXCEPTION_COUNT",nPendingExceptionCount+"");
			request.setAttribute("WAITING_EXCEPTION_COUNT",nWaitingExceptionCount+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getApprovedExceptionCount() {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {

			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))-1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy")));

			int nMinDate = cal.getActualMinimum(Calendar.DATE);
			int nMaxDate = cal.getActualMaximum(Calendar.DATE);
			
			String strDate1 = nMinDate +"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR);
			String strDate2 = nMaxDate +"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR);
			
			pst = con.prepareStatement("select count(*) as count from attendance_details where emp_id = ? and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and approved = 1");			
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strDate2, DATE_FORMAT));
			rs = pst.executeQuery();
			
			
			int nApprovedExceptionCount = 0;
			while (rs.next()) {
				nApprovedExceptionCount = rs.getInt("count");
			}
			rs.close();
			pst.close();
			session.setAttribute("APPROVED_EXCEPTION_COUNT",nApprovedExceptionCount+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getRosterStatus() {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select count(*) as count from roster_details where emp_id=? and entry_date = ?");			
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			
			int nRosterStatus = 0;
			while (rs.next()) {
				nRosterStatus = rs.getInt("count");
			}
			rs.close();
			pst.close();
			session.setAttribute("ROSTER_STATUS",nRosterStatus+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}  
	}

	public void getBusinessRuleStatus() {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement("select count(*) as count from roster_policy where entry_date = ?");			
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			
			int nBusinessRuleStatus = 0;
			while (rs.next()) {
				nBusinessRuleStatus = rs.getInt("count");
			}
			rs.close();
			pst.close();
			
			
			if(nBusinessRuleStatus==0){
				pst = con.prepareStatement("select count(*) as count from roster_halfday_policy where entry_date = ?");			
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				rs = pst.executeQuery();
				
				nBusinessRuleStatus = 0;
				while (rs.next()) {
					nBusinessRuleStatus = rs.getInt("count");
				}
				rs.close();
				pst.close();
			}
			
			
			session.setAttribute("BUSINESS_RULE_STATUS",nBusinessRuleStatus+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}  
	}

	public void getProbationStatus() {
		
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			

			
			pst = con.prepareStatement("select * from probation_policy where emp_id = ?");
			pst.setInt(1, uF.parseToInt(strEmpId));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			boolean isProbation = false;
			int nDifference = 0;
			int noticePeriod = 0;
//			int probationPeriod = 0;
			String probationPeriod = "";
			while(rs.next()) {

				if(rs.getString("probation_end_date") != null && rs.getBoolean("is_probation")) {
//					nDifference = uF.parseToInt(uF.dateDifference(rs.getString("probation_end_date"), DBDATE, ""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE));
					nDifference = uF.parseToInt(uF.dateDifference(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, rs.getString("probation_end_date"), DBDATE));
					isProbation = true;
					
//					probationPeriod = uF.parseToInt(rs.getString("extend_probation_duration")) > 0 ? uF.parseToInt(rs.getString("extend_probation_duration")) : uF.parseToInt(rs.getString("probation_duration"));
					probationPeriod = uF.parseToInt(rs.getString("extend_probation_duration")) > 0 ? uF.parseToInt(rs.getString("probation_duration")) +" days (Extend- "+uF.parseToInt(rs.getString("extend_probation_duration"))+") days" : uF.parseToInt(rs.getString("probation_duration")) +" days";
				} else if(rs.getBoolean("is_probation")) {
					isProbation = true;
					
					probationPeriod = uF.parseToInt(rs.getString("extend_probation_duration")) > 0 ? uF.parseToInt(rs.getString("probation_duration")) +" days (Extend- "+uF.parseToInt(rs.getString("extend_probation_duration"))+") days" : uF.parseToInt(rs.getString("probation_duration")) +" days";
				}else if(!rs.getBoolean("is_probation")) {
					
					isProbation = true;
				}
				noticePeriod = uF.parseToInt(rs.getString("notice_duration"));
//				probationPeriod = uF.parseToInt(rs.getString("extend_probation_duration")) > 0 ? uF.parseToInt(rs.getString("extend_probation_duration")) : uF.parseToInt(rs.getString("probation_duration"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("probationPeriod==>"+probationPeriod+"==>nDifference==>"+nDifference+"==>noticePeriod==>"+noticePeriod);
			
			if(isProbation) {
				request.setAttribute("PROBATION_REMAINING", nDifference+"");
			}

			request.setAttribute("NOTICE_PERIOD", noticePeriod+"");
			request.setAttribute("PROBATION_PERIOD", probationPeriod);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getWorkedHours() {
		
		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			
//			pst = con.prepareStatement("SELECT sum(hours_worked) as hours_worked FROM attendance_details WHERE emp_id =?");
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//			rs = pst.executeQuery();
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_worked_hours(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT sum(hours_worked) as hours_worked FROM attendance_details WHERE emp_id =?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			double hrsWorked = 0;
			while(rs.next()){
				hrsWorked = rs.getDouble("hours_worked");
			}
			rs.close();
			pst.close();
			request.setAttribute("HRS_WORKED",uF.formatIntoComma(hrsWorked));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getEmpKPI() {
		
		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			int YEAR = cal.get(Calendar.YEAR);
			int MONTH = cal.get(Calendar.MONTH) + 1;
			int Day = cal.get(Calendar.DAY_OF_MONTH);
			int MinDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
			int MaxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

//			pst = con.prepareStatement(selectPresentDays1);
//			pst.setInt(1, uF.parseToInt(strEmpIdReq));
//			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), "yyyy-MM-dd"));
//			pst.setDate(3, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), "yyyy-MM-dd"));
//			rs = pst.executeQuery();
			
//			con.setAutoCommit(false); 
//			cst = con.prepareCall("{? = call sel_emp_attendance(?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.setDate(3, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), "yyyy-MM-dd"));
//			cst.setDate(4, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), "yyyy-MM-dd"));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectPresentDays1);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), DBDATE));
			pst.setDate(3, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), DBDATE));
			rs = pst.executeQuery();

			double[] PRESENT_ABSENT_DATA = new double[2];
			String[] PRESENT_ABSENT_LABEL = new String[2];
			
			
			if (rs.next()) {
				request.setAttribute("PRESENT_COUNT", rs.getString("count"));
				request.setAttribute("ABSENT_COUNT", Day - uF.parseToInt(rs.getString("count")));

				PRESENT_ABSENT_DATA[0] = rs.getDouble("count");
				PRESENT_ABSENT_DATA[1] = Day - uF.parseToInt(rs.getString("count"));

				PRESENT_ABSENT_LABEL[0] = "Worked";
				PRESENT_ABSENT_LABEL[1] = "Absent";
			}
			rs.close();
			pst.close();

			request.setAttribute("CHART_WORKED_ABSENT", new PieCharts().get3DPieChart(PRESENT_ABSENT_DATA, PRESENT_ABSENT_LABEL));
			
			

//			pst = con.prepareStatement(selectPresentDays1);
//			pst.setInt(1, uF.parseToInt(strEmpIdReq));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 30));
//			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
//			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_attendance(?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 30));
//			cst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectPresentDays1);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 29)); //30day back date
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();

			double []PRESENT_ABSENT_DATA_MONTH = new double[2];
			String []PRESENT_ABSENT_LABEL_MONTH = new String[2];
			
			double dblPresentCount = 0;
			
			if (rs.next()) {
				
				dblPresentCount = uF.parseToDouble(rs.getString("count"));
//				
//				PRESENT_ABSENT_DATA_MONTH[0] = rs.getDouble("count");
//				PRESENT_ABSENT_DATA_MONTH[1] = 30 - uF.parseToInt(rs.getString("count"));
//				
//				PRESENT_ABSENT_LABEL_MONTH[0] = "Worked";
//				PRESENT_ABSENT_LABEL_MONTH[1] = "Absent";
			}
			rs.close();
			pst.close();
			
			
			
//			pst = con.prepareStatement("SELECT sum(hours_worked) as hours_worked FROM attendance_details WHERE emp_id = ? and TO_DATE (in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ?");
//			pst.setInt(1, uF.parseToInt(strEmpIdReq));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 30));
//			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();

			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_worked_hours(?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 30));
//			cst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT sum(hours_worked) as hours_worked FROM attendance_details WHERE emp_id = ? and TO_DATE (in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 29)); //30day back date
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			double []WORKED_HOURS_DATA_MONTH = new double[2];
			double dblWorkedHours = 0;
			
			if (rs.next()) {
				
//				WORKED_HOURS_DATA_MONTH[0] = uF.parseToDouble(rs.getString("hours_worked"));
				dblWorkedHours = uF.parseToDouble(rs.getString("hours_worked"));
			}
			rs.close();
			pst.close();
			
			double dblStandardHours = 8;
			
			
//			pst = con.prepareStatement("SELECT sum(actual_hours) as actual_hours FROM roster_details WHERE emp_id =? and _date BETWEEN ? AND ?");
//			pst.setInt(1, uF.parseToInt(strEmpIdReq));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 30));
//			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_roster_hours(?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 30));
//			cst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT sum(actual_hours) as actual_hours FROM roster_details WHERE emp_id =? and _date BETWEEN ? AND ?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 29)); //30day back date
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();

			double []ACTUAL_HOURS_DATA_MONTH = new double[2];
			double dblActualHours = 0;
			if (rs.next()) {
				dblActualHours = uF.parseToDouble(rs.getString("actual_hours"));
				
				if(dblActualHours==0){
					dblActualHours = dblStandardHours * dblPresentCount;
				}
//				ACTUAL_HOURS_DATA_MONTH[0] = uF.parseToDouble(rs.getString("actual_hours"));
			}
			rs.close();
			pst.close();
			
			PRESENT_ABSENT_DATA_MONTH[0] = dblWorkedHours;
			PRESENT_ABSENT_DATA_MONTH[1] = dblActualHours - dblWorkedHours;
			
			PRESENT_ABSENT_LABEL_MONTH[0] = "Worked";
			PRESENT_ABSENT_LABEL_MONTH[1] = "Actual";
			
			
			
			request.setAttribute("KPI", new SemiCircleMeter().getSemiCircleChart(PRESENT_ABSENT_DATA_MONTH, PRESENT_ABSENT_LABEL_MONTH));
			request.setAttribute("KPIZ", new LinearZMeter().getLinearChart(PRESENT_ABSENT_DATA_MONTH, PRESENT_ABSENT_LABEL_MONTH));
			
			request.setAttribute("ACTUAL_TIME_KPI", uF.formatIntoZeroWithOutComma(dblWorkedHours));
//			request.setAttribute("BUDGET_TIME_KPI", uF.formatIntoZeroWithOutComma((dblActualHours - dblWorkedHours)));
			request.setAttribute("BUDGET_TIME_KPI", uF.formatIntoZeroWithOutComma(dblActualHours)); 
			
			
			double dblMin = 0;
			double dblMax = 0;
			
			
			
			if(dblWorkedHours>dblActualHours){
				dblMin = 0;
				dblMax = (dblActualHours+(dblWorkedHours-dblActualHours));
				
				request.setAttribute("KPI_MIN", dblMin+"");
				request.setAttribute("KPI_MAX", dblMax+"");
			}else{
				dblMin = 0;
				dblMax = dblActualHours;
				
				request.setAttribute("KPI_MIN", dblMin+"");
				request.setAttribute("KPI_MAX", dblMax+"");
			}
			request.setAttribute("KPI_W", uF.formatIntoOneDecimalWithOutComma(dblWorkedHours)+"");
			
			double dbl1 = (int)(40 * dblMax / 100);
			double dbl2 = (int)(80 * dblMax / 100);
			
			request.setAttribute("KPI_1", dbl1+"");
			request.setAttribute("KPI_2", dbl2+"");
			request.setAttribute("KPI_HEADING", "Attendance KPI");
			request.setAttribute("KPI_PREFIX", "Attendance");
			request.setAttribute("KPI_SUFFIX", "hours");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getRosterVsWorkedHours() {
		
		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			
//			pst = con.prepareStatement("SELECT sum(hours_worked) as hours_worked, sum(actual_hours) as actual_hours, (hours_worked - actual_hours) as variance, to_date(in_out_timestamp::text, 'YYYY-MM-DD') as _date FROM attendance_details ad, roster_details rd WHERE rd.emp_id=ad.emp_id and to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') = rd._date and ad.service_id = rd.service_id and ad.emp_id =? and in_out = 'OUT' group by to_date(in_out_timestamp::text, 'YYYY-MM-DD') , variance order by to_date(in_out_timestamp::text, 'YYYY-MM-DD') desc limit 7");
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//			rs = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_roster_actual_hours(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT sum(hours_worked) as hours_worked, sum(actual_hours) as actual_hours, (hours_worked - actual_hours) as variance," +
					" to_date(in_out_timestamp::text, 'YYYY-MM-DD') as _date FROM attendance_details ad, roster_details rd WHERE rd.emp_id=ad.emp_id and " +
					"to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') = rd._date and ad.service_id = rd.service_id and ad.emp_id =? and in_out = 'OUT' " +
					"group by to_date(in_out_timestamp::text, 'YYYY-MM-DD') , variance order by to_date(in_out_timestamp::text, 'YYYY-MM-DD') desc limit 7");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			java.util.Date dtPrev7Days = uF.getPrevDate(CF.getStrTimeZone(), 7);
			java.util.Date dtCurrent = null;
			
			List<String> alWorkedHours = new ArrayList<String>();
			List<String> alRosterHours = new ArrayList<String>();
			List<String> alVarianceHoursE = new ArrayList<String>();
			List<String> alVarianceHoursL = new ArrayList<String>();
			List<String> alLabel = new ArrayList<String>();
			
			while(rs.next()){
				
				dtCurrent = uF.getDateFormatUtil(rs.getString("_date"), DBDATE);
				
				alWorkedHours.add(rs.getString("hours_worked"));
				alRosterHours.add(rs.getString("actual_hours"));
				if(rs.getDouble("variance")>=0){
					alVarianceHoursE.add(rs.getString("variance"));
					alVarianceHoursL.add("0");
				}else{
					alVarianceHoursE.add("0");
					alVarianceHoursL.add(rs.getString("variance"));
				}
				
				
				if(dtCurrent.before(dtPrev7Days)){
					alLabel.add("\'"+uF.getDateFormat(rs.getString("_date"), DBDATE, "dd/MMM")+"\'");
				}else{
					alLabel.add("\'"+uF.getDateFormat(rs.getString("_date"), DBDATE, "E")+"\'");	
				}
				
			}
			rs.close();
			pst.close();
			request.setAttribute("alWorkedHours",alWorkedHours);
			request.setAttribute("alRosterHours",alRosterHours);
			request.setAttribute("alVarianceHoursE",alVarianceHoursE);
			request.setAttribute("alVarianceHoursL",alVarianceHoursL);
			request.setAttribute("alLabel",alLabel);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getEmpServiceWorkingHourCounts() {
		
		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			
			
			Map<String, String> hmThisWeek = new HashMap<String, String>();
			Map<String, String> hmLastWeek = new HashMap<String, String>();
			Map<String, String> hmLastLastWeek = new HashMap<String, String>();
			List<String> alServices = new ArrayList<String>();
			
			
			//This week
			
//			pst = con.prepareStatement("SELECT * FROM ( SELECT service_id, sum(hours_worked) as hours_worked FROM attendance_details " +
//								"WHERE emp_id = ? and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?" +
//								"GROUP BY service_id) asd LEFT JOIN services sd ON asd.service_id = sd.service_id ") ; 
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 7));
//			pst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 0));
//			rs = pst.executeQuery();
//			
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_service_working_hours_count(?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 7));
//			cst.setDate(4, uF.getPrevDate(CF.getStrTimeZone(), 0));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT * FROM ( SELECT service_id, sum(hours_worked) as hours_worked FROM attendance_details WHERE emp_id = ? " +
					"and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? GROUP BY service_id) asd LEFT JOIN services sd ON asd.service_id = sd.service_id ") ; 
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 7));
			pst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 0));
			rs = pst.executeQuery();
			
			while(rs.next()) {
				if(!alServices.contains(rs.getString("service_name"))){
					alServices.add(rs.getString("service_name"));
				}
				
				hmThisWeek.put(rs.getString("service_name"), rs.getString("hours_worked"));
				
			}
			rs.close();
			pst.close();
			
			//Last week
			
//			pst = con.prepareStatement("SELECT * FROM ( SELECT service_id, sum(hours_worked) as hours_worked FROM attendance_details " +
//								"WHERE emp_id = ? and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?" +
//								"GROUP BY service_id) asd LEFT JOIN services sd ON asd.service_id = sd.service_id ") ; 
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 14));
//			pst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 7));
//			rs = pst.executeQuery();
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_service_working_hours_count(?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 14));
//			cst.setDate(4, uF.getPrevDate(CF.getStrTimeZone(), 7));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT * FROM ( SELECT service_id, sum(hours_worked) as hours_worked FROM attendance_details WHERE emp_id = ? " +
					"and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? GROUP BY service_id) asd LEFT JOIN services sd ON asd.service_id = sd.service_id ") ; 
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 14));
			pst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 7));
			rs = pst.executeQuery();
			
			while(rs.next()){
				if(!alServices.contains(rs.getString("service_name"))){
					alServices.add(rs.getString("service_name"));
				}
				
				hmLastWeek.put(rs.getString("service_name"), rs.getString("hours_worked"));
			}
			rs.close();
			pst.close();
			
			//Last to Last week
			
//			pst = con.prepareStatement("SELECT * FROM ( SELECT service_id, sum(hours_worked) as hours_worked FROM attendance_details " +
//								"WHERE emp_id = ? " +
//								"and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?" +
//								"GROUP BY service_id) asd LEFT JOIN services sd ON asd.service_id = sd.service_id ") ; 
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//			pst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 21));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 14));
//			
//			rs = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_service_working_hours_count(?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 21));
//			cst.setDate(4, uF.getPrevDate(CF.getStrTimeZone(), 14));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT * FROM ( SELECT service_id, sum(hours_worked) as hours_worked FROM attendance_details WHERE emp_id = ? " +
					"and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? GROUP BY service_id) asd LEFT JOIN services sd ON asd.service_id = sd.service_id") ; 
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 21));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 14));
			rs = pst.executeQuery();
			while(rs.next()) {
				if(!alServices.contains(rs.getString("service_name"))){
					alServices.add(rs.getString("service_name"));
				}
				
				hmLastLastWeek.put(rs.getString("service_name"), rs.getString("hours_worked"));
			}
			rs.close();
			pst.close();
			
			
			
			StringBuilder sb = new StringBuilder();

			sb.append("series:[");
			
			for(int i=0; i<alServices.size(); i++){
				String strServiceName = alServices.get(i);
				if(strServiceName==null){
					continue;
				}
				
				
				sb.append("{" +
						"name: '"+strServiceName+"'," +
						"data: ["+uF.parseToDouble(hmThisWeek.get(strServiceName))+", "+uF.parseToDouble(hmLastWeek.get(strServiceName))+", "+uF.parseToDouble(hmLastLastWeek.get(strServiceName))+"]" +
						"}");
				
				
				if(i!=alServices.size()-1){
					sb.append("," );
				}
				
			}
			sb.append("]");
			
	      request.setAttribute("ServiceWorkingHours", sb.toString());
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
		
	}

	
	public void getDayThought() {
		
		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			
//			pst = con.prepareStatement(selectThought);			
//			pst.setInt(1, cal.get(Calendar.DAY_OF_YEAR));
//			rs = pst.executeQuery();
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_thought(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, cal.get(Calendar.DAY_OF_YEAR));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectThought);			
			pst.setInt(1, cal.get(Calendar.DAY_OF_YEAR));
			rs = pst.executeQuery();
			
			String strThought = null;
			String strThoughtBy = null;
			while (rs.next()) {
				strThought = rs.getString("thought_text");
				strThoughtBy = rs.getString("thought_by"); 
			}
			rs.close();
			pst.close();
//			cst.close();
			request.setAttribute("DAY_THOUGHT_TEXT",strThought);
			request.setAttribute("DAY_THOUGHT_BY",strThoughtBy);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getResignationStatus() {
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			
//			pst = con.prepareStatement("select * from emp_off_board where emp_id =? order by entry_date desc limit 1");			
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			rs = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_resignation_status(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("select * from emp_off_board where emp_id =? order by entry_date desc limit 1");			
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			String strResignationStatus = null;
			String strResignationStatusD = null;
			String strResigDate = null;
			String strRADay = null;
			String strRAMonth = null;
			int nResigId = 0;
			
			while (rs.next()) {
				
				nResigId = rs.getInt("off_board_id");
				if(rs.getInt("approved_1")==1 && rs.getInt("approved_2")==1) {
					
					if(rs.getString("off_board_type") != null && rs.getString("off_board_type").trim().equalsIgnoreCase("TERMINATED")) {
						strResignationStatus = "Terminated";
						strResignationStatusD = "You are terminated from the service. Please <a href=\"ExitForm.action?id="+session.getAttribute(EMPID)+"&resignId="+nResigId+"\">click here</a> to complete your formalities.";
					}else if(rs.getString("off_board_type") != null && rs.getString("off_board_type").trim().equalsIgnoreCase("RESIGNED")) {
						strResignationStatus = "Your resignation has been accepted";
						strResignationStatusD = "Your resignation has been accepted. Please <a href=\"ExitForm.action?id="+session.getAttribute(EMPID)+"&resignId="+nResigId+"\">click here</a> to complete your formalities.";
					}
					
					request.setAttribute("RESIG_STATUS", "1");
				} else if(rs.getInt("approved_1")==-1 || rs.getInt("approved_2")==-1) {
					strResignationStatus = "Your resignation has been denied";
					strResignationStatusD = "Your resignation has been denied";
				} else if(rs.getInt("approved_1")==1 && rs.getInt("approved_2")==0) {
					strResignationStatus = "Your resignation has been approved by your manager and is waiting for HR's approval";
					strResignationStatusD = "Your resignation has been approved by your manager and is waiting for HR's approval";
				} else if(rs.getInt("approved_1")==0 && rs.getInt("approved_2")==1) {
					strResignationStatus = "Your resignation has been approved by your HR and is waiting for manager's approval";
					strResignationStatusD = "Your resignation has been approved by your HR and is waiting for manager's approval";
				}else if(rs.getString("off_board_type")!=null && rs.getString("off_board_type").equalsIgnoreCase("TERMINATED")){
					strResignationStatus = "Terminated";
					strResignationStatusD = "Terminated";
				} else if((rs.getInt("approved_1")==0 || rs.getInt("approved_2")==0) && rs.getString("off_board_type")!=null && !rs.getString("off_board_type").equalsIgnoreCase("TERMINATED")) {
					strResignationStatus = "Resigned & waiting for approval";
					strResignationStatusD = "Resigned & waiting for approval";
				}
				
				if(rs.getString("approved_2_date")!=null) {
					strRADay = uF.getDateFormat(rs.getString("approved_2_date"), DBDATE, "dd");
					strRAMonth = uF.getDateFormat(rs.getString("approved_2_date"), DBDATE, "MMM");
				} else if(rs.getString("approved_1_date")!=null) {
					strRADay = uF.getDateFormat(rs.getString("approved_1_date"), DBDATE, "dd");
					strRAMonth = uF.getDateFormat(rs.getString("approved_1_date"), DBDATE, "MMM");
				} else if(rs.getString("entry_date")!=null) {
					strRADay = uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, "dd");
					strRAMonth = uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, "MMM");
				}
				 if(rs.getString("entry_date")!=null) {
					 strResigDate = uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, DBDATE);
				}
			}
			rs.close();
			pst.close();
//			cst.close();
			
			
			/*pst = con.prepareStatement("select * from employee_activity_details where emp_id = ? and emp_activity_id =(select max(emp_activity_id) from employee_activity_details  where emp_id = ?)");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			int nNotice = 0;
			while(rs.next()) {
				nNotice = rs.getInt("notice_period");
			}
			rs.close();
			pst.close();*/
			
			int nNotice = CF.getEmpNoticePeriod(con,strEmpId );
			
			int nDifference = 0;
			int nRemaining = 0;
			String resigData = "";
			String lastDate = "";
			if(strResigDate!=null){
				/*nDifference = uF.parseToInt(uF.dateDifference(strApprovedDate, DBDATE, ""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE));
				nRemaining = nNotice - nDifference;*/
			
				String regDate = uF.getDateFormat(strResigDate, DBDATE,DATE_FORMAT);
								
				lastDate = uF.getDateFormat(""+uF.getBiweeklyDate(regDate, nNotice),DBDATE,CF.getStrReportDateFormat());
				String ldate = uF.getDateFormat(""+uF.getBiweeklyDate(regDate, nNotice),DBDATE,DATE_FORMAT);
				
				java.util.Date currDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),DBDATE, DATE_FORMAT),DATE_FORMAT );
				java.util.Date lstDate = uF.getDateFormatUtil(ldate,DATE_FORMAT );
				nDifference = uF.parseToInt(uF.dateDifference(strResigDate, DBDATE, ""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE));
				
				if(lstDate.after(currDate)) {
					nRemaining = nNotice - nDifference;
					resigData = nRemaining + " days remaining";
				} else if(lstDate.before(currDate)) {
					resigData = " last day  "+lastDate;
				} else if(lstDate.equals(currDate)) {
					resigData = " Today is last day  ";
				}
			}
			
			
			request.setAttribute("RESIGNATION_STATUS", strResignationStatus);
			request.setAttribute("strRADay", strRADay);
			request.setAttribute("strRAMonth", strRAMonth);
			request.setAttribute("RESIGNATION_REMAINING",resigData);
			
			
//			if(strResignationStatus!=null){
			
				request.setAttribute("RESIGNATION_STATUS_D",strResignationStatusD);
//			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getMailCount() {
		
		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst =null;
		try {
			
//			pst = con.prepareStatement(getUnreadMailCount);			
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			rs = pst.executeQuery();
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_unread_mail_count(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(getUnreadMailCount);			
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			int nMailCount = 0;
			while (rs.next()) {
				nMailCount = rs.getInt("count");
			}
			rs.close();
			pst.close();
//			cst.close();
			request.setAttribute("MAIL_COUNT",nMailCount+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getBirthdays() {
		 
		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst =null;
		try {
			
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strToday1 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM-dd");
			
			Map<String, String> hmEmployeeMap = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);
			Map<String, String> hmEmpDepartmentMap = CF.getEmpDepartmentMap(con);
			Map<String, Map<String, String>> hmWlocationMap = CF.getWorkLocationMap(con);
			Map<String, String> hmDepartmentMap = CF.getDeptMap(con);
			

			pst = con.prepareStatement(selectBirthDay);			
			
			pst.setDate(1, uF.getFutureDate(CF.getStrTimeZone(), 365));
			rs = pst.executeQuery();

			List<List<String>> alBirthDays = new ArrayList<List<String>>();
			while (rs.next()) {
				
				String strBDate = uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "MM-dd");
				String strBDay = uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "dd");
				String strBMonth = uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "MMM");
				String strWlocationId = hmEmpWlocationMap.get(rs.getString("emp_per_id"));
				String strTomorrow = uF.getDateFormat(uF.getFutureDate(CF.getStrTimeZone(), 1)+"", DBDATE, "MM-dd");
				String strDayAfterTomorrow = uF.getDateFormat(uF.getFutureDate(CF.getStrTimeZone(), 2)+"", DBDATE, "MM-dd");
				
				Map hmWlocation = hmWlocationMap.get(strWlocationId);
				if(hmWlocation==null)hmWlocation=new HashMap();
				
				String strCity = (String)hmWlocation.get("WL_CITY");
				String gender = (String)rs.getString("emp_gender");
				if(strBDate!=null && strBDate.equals(strToday1)) {
					if(hmEmployeeMap.get(rs.getString("emp_per_id")) != null && !hmEmployeeMap.get(rs.getString("emp_per_id")).equals("null")) {
						List<String> innerList = new ArrayList<String>();
						if(gender!=null && gender.equalsIgnoreCase("M")){
							//alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today wish him...!");
							innerList.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has birthday today wish him...!");
							innerList.add(strBDay);
							innerList.add(strBMonth);
						}else if(gender!=null && gender.equalsIgnoreCase("F")){
							//alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today wish her...!");
							innerList.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has birthday today wish her...!");
							innerList.add(strBDay);
							innerList.add(strBMonth);
						}else{
						//	alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today...!");
							innerList.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has birthday today...!");
							innerList.add(strBDay);
							innerList.add(strBMonth);
						}
						alBirthDays.add(innerList);
					}
				}
				
				if(strBDate!=null && strBDate.equals(strTomorrow)) {
					if(hmEmployeeMap.get(rs.getString("emp_per_id")) != null && !hmEmployeeMap.get(rs.getString("emp_per_id")).equals("null")) {
						List<String> innerList = new ArrayList<String>();
						if(gender!=null && gender.equalsIgnoreCase("M")){
							//alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today wish him...!");
							innerList.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has birthday tomorrow wish him...!");
							innerList.add(strBDay);
							innerList.add(strBMonth);
						}else if(gender!=null && gender.equalsIgnoreCase("F")){
							//alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today wish her...!");
							innerList.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has birthday tomorrow wish her...!");
							innerList.add(strBDay);
							innerList.add(strBMonth);
						}else{
						//	alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today...!");
							innerList.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has birthday tomorrow...!");
							innerList.add(strBDay);
							innerList.add(strBMonth);
						}
						alBirthDays.add(innerList);
					}
				}
				
			}
			rs.close();
			pst.close();
//			cst.close();
//			System.out.println("alBirthDays ===>> " + alBirthDays);
			request.setAttribute("alBirthDays", alBirthDays);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*public void getEmpUserTypeId() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try { 
			pst = con.prepareStatement("select usertype_id from user_details where emp_id=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs=pst.executeQuery();
			if (rs.next()) {
				request.setAttribute("EMP_USER_TYPE_ID", rs.getString("usertype_id"));				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}*/

	public void getMyKRA() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try { 
//			pst=con.prepareStatement("select question_bank_id,aaa.kra_id,average,kra_description from (select question_bank_id,question_text,kra_id," +
//				"(marks*100/weightage) as average from (select question_bank_id,question_text,kra_id from question_bank where goal_kra_target_id " +
//				"in(select goal_id from goal_details where measure_kra = 'KRA' and emp_ids like '%,"+strEmpId+",%' and (goal_type = ? or " +
//				" goal_type = ? or goal_type = ?) )) as a, appraisal_question_answer aqa where a.question_bank_id=aqa.question_id) as aaa, " +
//				"goal_kras gk where aaa.kra_id = gk.goal_kra_id");
//			pst.setInt(1, INDIVIDUAL_GOAL);
//			pst.setInt(2, INDIVIDUAL_KRA);
//			pst.setInt(3, EMPLOYEE_KRA);
////			System.out.println("pst ===>> " + pst);
//			rs=pst.executeQuery();
//			Map<String, String> hmKRAAverage = new HashMap<String, String>();
//			while (rs.next()) {
//				hmKRAAverage.put(rs.getString("kra_id"), rs.getString("average"));
//			}
//			rs.close();
//			pst.close();
			

			Map<String, String> hmEmpwiseKRARating = new HashMap<String, String>();
			Map<String, String> hmEmpwiseEmpKRARating = new HashMap<String, String>();
			pst=con.prepareStatement("select gksrd.*, gk.goal_id from goal_kra_status_rating_details gksrd, goal_kras gk where gksrd.kra_id = gk.goal_kra_id and gksrd.emp_id in ("+strEmpId+")");
			rs=pst.executeQuery();
			while (rs.next()) {
				
				if((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals("")) || (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals("")) ) {
					double strTaskRating = uF.parseToDouble(hmEmpwiseKRARating.get(rs.getString("emp_id")+"_"+rs.getString("kra_id")+"_RATING"));
					int strTaskCount = uF.parseToInt(hmEmpwiseKRARating.get(rs.getString("emp_id")+"_"+rs.getString("kra_id")+"_COUNT"));
					strTaskCount++;
					double strCurrTaskRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
					if(rs.getString("manager_rating") == null) {
						strCurrTaskRating = uF.parseToDouble(rs.getString("hr_rating"));
					} else if(rs.getString("hr_rating") == null) {
						strCurrTaskRating = uF.parseToDouble(rs.getString("manager_rating"));
					}
					strTaskRating += strCurrTaskRating;
					hmEmpwiseKRARating.put(rs.getString("emp_id")+"_"+rs.getString("kra_id")+"_RATING", strTaskRating+"");
					hmEmpwiseKRARating.put(rs.getString("emp_id")+"_"+rs.getString("kra_id")+"_COUNT", strTaskCount+"");
				}
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpwiseKRARating === >> " + hmEmpwiseKRARating);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(gksrd.user_rating) as user_rating, count(*) as cnt, gksrd.emp_id,gksrd.kra_id from goal_kra_emp_status_rating_details gksrd, " +
				"goal_details gd where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+strEmpId+") and gksrd.kra_id>0 and user_type != '-' group by gksrd.emp_id,gksrd.kra_id");
			pst=con.prepareStatement(sbQuery.toString());
//				System.out.println("TAS pst ===>> " + pst);
			rs=pst.executeQuery();
			while (rs.next()) {
				if((rs.getString("user_rating") != null && !rs.getString("user_rating").equals("")) ) {
//						double strCurrGoalORTargetRating = rs.getDouble("user_rating")/ rs.getInt("cnt");
					double elementEmpAvgScore = (rs.getDouble("user_rating") / rs.getDouble("cnt"));
					hmEmpwiseEmpKRARating.put(rs.getString("emp_id")+"_"+rs.getString("kra_id")+"_RATING", elementEmpAvgScore+"");
				}
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select gk.* from goal_details gd,goal_kras gk where gd.emp_ids like '%,"+strEmpId+ ",%' and (gd.goal_type = ? or " +
				" gd.goal_type = ? or (gd.goal_type = ? and gk.is_assign = true and gk.is_close = false)) and gd.measure_kra = 'KRA' and " +
				"gd.goal_id=gk.goal_id and gd.is_close = false order by gd.goal_id,gk.goal_kra_id"); //(gd.measure_type='' or gd.measure_type is null)
			pst=con.prepareStatement(sbQuery.toString());
			pst.setInt(1, INDIVIDUAL_GOAL);
			pst.setInt(2, INDIVIDUAL_KRA);
			pst.setInt(3, EMPLOYEE_KRA);
			rs = pst.executeQuery();
			List<Map<String,String>> myKRAList = new ArrayList<Map<String,String>>();
			while (rs.next()) {
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("KRA_NAME", rs.getString("kra_description"));
				String kraRate = getEmpKRARating(hmEmpwiseKRARating, uF, rs.getString("goal_kra_id"), strEmpId);
				double dblempRate = uF.parseToDouble(hmEmpwiseEmpKRARating.get(strEmpId+"_"+rs.getString("goal_kra_id")+"_RATING"));
				double kraAvg = 0.0d;
				if(uF.parseToDouble(kraRate)>0 && dblempRate>0){
					kraAvg = (uF.parseToDouble(kraRate) + dblempRate) / 2;
				} else if(uF.parseToDouble(kraRate)>0 ){
					kraAvg = uF.parseToDouble(kraRate);
				} else if( dblempRate>0){
					kraAvg = dblempRate;
				}
//				System.out.println("kraRate ===>> " + kraRate);
				hmInner.put("KRA_AVERAGE", uF.showData(kraAvg+"", "Not Rated"));
				hmInner.put("KRA_ID", rs.getString("goal_kra_id"));
				myKRAList.add(hmInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("myKRAList", myKRAList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
	}

	private String getEmpKRARating(Map<String, String> hmEmpwiseKRARating, UtilityFunctions uF, String kraId, String strEmpId) {
		String kraAvg = null;
		String kraRating = hmEmpwiseKRARating.get(strEmpId+"_"+kraId+"_RATING");
		String kraTaskCount = hmEmpwiseKRARating.get(strEmpId+"_"+kraId+"_COUNT");
		if(uF.parseToInt(kraTaskCount) > 0) {
			double avgKRARating = uF.parseToDouble(kraRating) / uF.parseToInt(kraTaskCount);
			kraAvg = avgKRARating+"";
		}
		return kraAvg;
	}


	public void getMyGoalAchieve() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			strEmpOrgId = (String) session.getAttribute(ORGID);
			
//			pst=con.prepareStatement("select goal_id,goal_title from goal_details where (goal_type= ? or (goal_type= ? and goalalign_with_teamgoal = true)) " +
//					" and org_id ="+uF.parseToInt(strEmpOrgId)+" and emp_ids like '%,"+strEmpId+",%' order by goal_id desc");
			pst=con.prepareStatement("select goal_id,goal_title from goal_details where (goal_type= ? or goal_type= ? or goal_type= ? or goal_type= ?) " +
					" and org_id ="+uF.parseToInt(strEmpOrgId)+" and emp_ids like '%,"+strEmpId+",%' order by goal_id desc");
			pst.setInt(1, INDIVIDUAL_GOAL);
			pst.setInt(2, PERSONAL_GOAL);
			pst.setInt(3, INDIVIDUAL_TARGET);
			pst.setInt(4, INDIVIDUAL_KRA);
			rs = pst.executeQuery();
			List<List<String>> goalIdList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_title"));
				goalIdList.add(innerList);
			}
			rs.close();
			pst.close();
		
			getGoalRating();
			
			/*pst=con.prepareStatement("select question_bank_id,aaa.goal_kra_target_id,goal_title,average from (select question_bank_id,question_text," +
					"goal_kra_target_id,(marks*100/weightage) as average from (select question_bank_id,question_text,goal_kra_target_id from " +
					"question_bank where goal_kra_target_id in(select goal_id from goal_details where is_measure_kra = false and emp_ids like " +
					"'%,"+strEmpId+",%' and (goal_type = ? or goal_type = ?))) as a, appraisal_question_answer aqa where " +
					"a.question_bank_id=aqa.question_id) as aaa, goal_details gd where aaa.goal_kra_target_id = gd.goal_id");
			pst.setInt(1, INDIVIDUAL_GOAL); 
			pst.setInt(2, PERSONAL_GOAL); 
			rs=pst.executeQuery();
			Map<String, String> hmGoalAverage = new HashMap<String, String>();
			while (rs.next()) {
				hmGoalAverage.put(rs.getString("goal_kra_target_id"),rs.getString("average"));
			}*/
			request.setAttribute("goalIdList", goalIdList);
//			request.setAttribute("hmGoalAverage", hmGoalAverage);
		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
	}

//	public void getMyTeamAchieve() {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		try {
//			
//			getTeamDetails();
////			pst = con.prepareStatement("select emp_ids from goal_details where goal_type = ? and emp_ids like '%,"+strEmpId+",%'");
////			pst.setInt(1, TEAM_GOAL);
////			rs = pst.executeQuery();
////			StringBuilder sb=new StringBuilder();
////			int i=0;
////			
////			while(rs.next()){
////				String emp_ids=rs.getString("emp_ids");
////				emp_ids=emp_ids.substring(1, emp_ids.length()-1);
////				if(i==0){
////					sb.append(emp_ids);
////				}else{
////					sb.append(","+emp_ids);
////				}
////				i++;				
////			}
////			double achievedPercent = 0.0d;
////			double remainPercent = 0.0d;
////			if(sb.length()>0){	
////				List<String> empList=Arrays.asList(sb.toString().split(","));
////				StringBuilder sbQuery=new StringBuilder();
////				sbQuery.append("select (percentage/target_count) as average from (select count(*) as target_count," +
////						"sum(amt_percentage) as percentage from (select max(target_id) as target_id from (select goal_id from goal_details ");
////				sbQuery.append(" where goal_type = ? and (");
////                for(i=0; empList!=null && i<empList.size(); i++){
////                    sbQuery.append(" emp_ids like '%,"+empList.get(i)+",%'");
////                    
////                    if(i<empList.size()-1){
////                        sbQuery.append(" OR "); 
////                    }
////                }
////                sbQuery.append(" )) as a, target_details td where a.goal_id = td.goal_id) as a,target_details td " +
////				"where a.target_id=td.target_id) as b");
////				
////				pst=con.prepareStatement(sbQuery.toString());
////				pst.setInt(1, INDIVIDUAL_GOAL);
////				rs=pst.executeQuery();
////				double achievedAmt = 0.0d;
////				while (rs.next()) {
////					achievedAmt =rs.getDouble("average");
////				}
////				
////				sbQuery=new StringBuilder();
////				sbQuery.append("select (percentage/goal_count) as average from (select count(*) as goal_count, sum(measure_currency_value) as percentage " +
////						"from goal_details ");
////				sbQuery.append(" where goal_type = ? and (");
////                for(i=0; empList!=null && i<empList.size(); i++){
////                    sbQuery.append(" emp_ids like '%,"+empList.get(i)+",%'");
////                    
////                    if(i<empList.size()-1){
////                        sbQuery.append(" OR "); 
////                    }
////                }
////                sbQuery.append(" )) as b");
////				pst=con.prepareStatement(sbQuery.toString());
////				pst.setInt(1, INDIVIDUAL_GOAL);
////				rs=pst.executeQuery();
////				double targetedAmt = 0.0d;
////				while (rs.next()) {
////					targetedAmt = rs.getDouble("average");
////				}
////				
//////				System.out.println("tachievedAmt ===> " + achievedAmt +" ttargetedAmt ===> " + targetedAmt);
////				if(targetedAmt > 0d) {
////					achievedPercent = (achievedAmt*100) / targetedAmt;  
////				}
////				
//////				System.out.println("tachievedPercent ===> " + achievedPercent);
////				
////				remainPercent = 100 - achievedPercent;
////				
//////				System.out.println("tremainPercent ===> " + remainPercent);
////			}
////			request.setAttribute("teamGoalachievedPercent", ""+achievedPercent);
////			request.setAttribute("teamGoalremainPercent", ""+remainPercent);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	
	
	public void getMyManagerGoalAchieve() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, Map<String, String>> hmTeamGoalCalDetailsManager = new HashMap<String, Map<String, String>>();
		try {
			getManagerGoalAverageDetails();
			
			strEmpOrgId = (String) session.getAttribute(ORGID);
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from goal_details where goal_type = ? and org_id =" + uF.parseToInt(strEmpOrgId) + " and is_close = false and emp_ids like '%,"+strEmpId+",%' order by goal_id desc");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, MANAGER_GOAL);
//			System.out.println("pst ======================= >>>> " + pst);
			rs = pst.executeQuery();
			List<List<String>> managerGoalList = new ArrayList<List<String>>();
			while (rs.next()) {
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_title"));
				innerList.add(rs.getString("goal_type"));
				innerList.add(rs.getString("goal_parent_id"));
				innerList.add(rs.getString("goal_objective"));
				innerList.add(rs.getString("goal_description"));
				managerGoalList.add(innerList);
				
				Map<String, String> hmTeamGoalCalDetailsParentManager = new HashMap<String, String>();
				getTeamGoalData(uF, rs.getString("goal_id"), hmTeamGoalCalDetailsParentManager);
				hmTeamGoalCalDetailsManager.put(rs.getString("goal_id"), hmTeamGoalCalDetailsParentManager);
			}
			rs.close();
			pst.close();
			request.setAttribute("managerGoalList",managerGoalList);
			request.setAttribute("hmTeamGoalCalDetailsManager",hmTeamGoalCalDetailsManager);
//			System.out.println("hmManager ===> "+hmManager);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void getManagerGoalAverageDetails() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			strEmpOrgId = (String) session.getAttribute(ORGID);
			pst=con.prepareStatement("select goal_parent_id,goal_id from goal_details where goal_type = ? and goal_id in(select goal_parent_id from " +
					"goal_details where (goal_type= ? or (goal_type= ? and goalalign_with_teamgoal = true)) and " +
					"org_id ="+uF.parseToInt(strEmpOrgId)+" and emp_ids like '%,"+strEmpId+",%') order by goal_id desc ");
			pst.setInt(1, TEAM_GOAL);
			pst.setInt(2, INDIVIDUAL_GOAL);
			pst.setInt(3, PERSONAL_GOAL);
//			System.out.println("pst ======> " + pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmTeamGoalIdManagerwise = new HashMap<String, List<String>>();
			List<String> teanGoalIdList = new ArrayList<String>();
			while (rs.next()) {
				teanGoalIdList = hmTeamGoalIdManagerwise.get(rs.getString("goal_parent_id"));
				if(teanGoalIdList == null) teanGoalIdList = new ArrayList<String>();
				teanGoalIdList.add(rs.getString("goal_id"));
				hmTeamGoalIdManagerwise.put(rs.getString("goal_parent_id"), teanGoalIdList);
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmTeamGoalAverage = (Map<String, String>) request.getAttribute("hmTeamGoalAverage");
			
			Map<String, String> hmManagerGoalAverage = new HashMap<String, String>();
//			System.out.println("hmTeamGoalIdManagerwise =====> " + hmTeamGoalIdManagerwise);
			
			Iterator<String> it = hmTeamGoalIdManagerwise.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				List<String> teanGoalIdList1 = hmTeamGoalIdManagerwise.get(key);
			double managerAllGoalAverage = 0.0d;
			double mangerGoalAverage = 0.0d;
			int teamGoalCnt = 0;
			for(int i=0; teanGoalIdList1 != null && !teanGoalIdList1.isEmpty() && i< teanGoalIdList1.size(); i++) {
				if(hmManagerGoalAverage != null && !hmManagerGoalAverage.isEmpty()){
					if(hmManagerGoalAverage.get(teanGoalIdList1.get(i)) != null) {
						managerAllGoalAverage += uF.parseToDouble(hmManagerGoalAverage.get(teanGoalIdList1.get(i)));
						teamGoalCnt++;
					}
				}
			}
			if(teamGoalCnt > 0){
				mangerGoalAverage = managerAllGoalAverage / teamGoalCnt;
			}
			hmManagerGoalAverage.put(key, ""+mangerGoalAverage);
			}
//			System.out.println("hmManagerGoalAverage =====> " + hmManagerGoalAverage);
			request.setAttribute("hmManagerGoalAverage", hmManagerGoalAverage);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void getTeamGoalData(UtilityFunctions uF, String parentID, Map<String, String> hmTeamGoalCalDetailsParentManager){
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,Map<String,String>> hmTeamGoalCalManager = new HashMap<String, Map<String,String>>();
		Map<String,Map<String,Map<String,String>>> hmTeamGoalCalManagerParent= new HashMap<String, Map<String,Map<String,String>>>();
		try {
			
			pst = con.prepareStatement("select * from goal_details where goal_parent_id = ? order by goal_id ");
			pst.setInt(1, uF.parseToInt(parentID));
			rs = pst.executeQuery();
			while (rs.next()) {
				
				Map<String, String> hmManagerGoalCalDetails = new HashMap<String, String>();
				getIndGoalData(uF, rs.getString("goal_id"), hmManagerGoalCalDetails);
				hmTeamGoalCalManager.put(rs.getString("goal_id"), hmManagerGoalCalDetails);
				hmTeamGoalCalManagerParent.put(rs.getString("goal_parent_id"), hmTeamGoalCalManager);
			}
			rs.close();
			pst.close();
			
			String alltwoDeciTotProgressAvg ="0";
	 		String alltotal100 ="100";
	 		String strtwoDeciTot = "0";
	 		double dblalltwoDeciTotProgressAvg = 0;
	 		double dblalltotal100 = 0;
	 		double dblstrtwoDeciTot = 0;
			Iterator<String> it1 = hmTeamGoalCalManagerParent.keySet().iterator();
			while (it1.hasNext()) {
				String parentid =(String)it1.next();
				Map<String,Map<String,String>> hmTeamGoalCalManager1 = hmTeamGoalCalManagerParent.get(parentid);
				Iterator<String> it2 = hmTeamGoalCalManager1.keySet().iterator();
				int cnt=0;
				while (it2.hasNext()) {
					String goalid =(String)it2.next();
					Map<String, String> hmTeamGoalCalDetails = hmTeamGoalCalManager1.get(goalid);
					dblalltwoDeciTotProgressAvg += uF.parseToDouble(hmTeamGoalCalDetails.get(goalid+"_PERCENT"));
					double tot100 = uF.parseToDouble(hmTeamGoalCalDetails.get(goalid+"_TOTAL"));
					if(tot100 == 0){
						tot100 = 100;	
					}
					dblalltotal100 += tot100;
					dblstrtwoDeciTot += uF.parseToDouble(hmTeamGoalCalDetails.get(goalid+"_STR_PERCENT"));
					cnt++;
				}
				double percentAmt = dblalltwoDeciTotProgressAvg / cnt;
				double totalAmt = dblalltotal100 / cnt;
				double strPercentAmt = dblstrtwoDeciTot / cnt;
				alltwoDeciTotProgressAvg = ""+Math.round(percentAmt);
				alltotal100 = ""+Math.round(totalAmt);
				strtwoDeciTot = ""+Math.round(strPercentAmt);
				hmTeamGoalCalDetailsParentManager.put(parentid+"_PERCENT", alltwoDeciTotProgressAvg);
				hmTeamGoalCalDetailsParentManager.put(parentid+"_TOTAL", alltotal100);
				hmTeamGoalCalDetailsParentManager.put(parentid+"_STR_PERCENT", strtwoDeciTot);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	
	public void getMyTeamGoalAchieve() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		Map<String, Map<String, String>> hmIndGoalCalDetailsTeam = new HashMap<String, Map<String, String>>();
		try {
			
			getGoalRating();
			getTeamGoalAverageDetails();
			strEmpOrgId = (String) session.getAttribute(ORGID);
			pst=con.prepareStatement("select * from goal_details where goal_type = ? and goal_id in(select goal_parent_id from goal_details where " +
				"(goal_type=? or (goal_type=? and goalalign_with_teamgoal = true)) and org_id ="+uF.parseToInt(strEmpOrgId)+" and emp_ids " +
				"like '%,"+strEmpId+",%') order by goal_id desc");
			pst.setInt(1, TEAM_GOAL);
			pst.setInt(2, INDIVIDUAL_GOAL);
			pst.setInt(3, PERSONAL_GOAL);
			rs = pst.executeQuery();
			Map<String, List<List<String>>> hmTeamGoals = new HashMap<String, List<List<String>>>();
			List<List<String>> teamGoalList = null;
			while (rs.next()) {
				teamGoalList = hmTeamGoals.get(rs.getString("goal_parent_id"));
				if(teamGoalList == null) teamGoalList = new ArrayList<List<String>>();
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_title"));
				innerList.add(rs.getString("goal_objective"));
				innerList.add(rs.getString("goal_description"));
				innerList.add(rs.getString("goal_type"));
				innerList.add(rs.getString("goal_parent_id"));
				String priority="";
				String pClass="";
				if(rs.getString("priority")!=null && !rs.getString("priority").equals("")){
					if(rs.getString("priority").equals("1")){
						pClass="high";
						priority="High";
					}else if(rs.getString("priority").equals("2")){
						pClass="medium";
						priority="Medium";
					}else if(rs.getString("priority").equals("3")){
						pClass="low";
						priority="Low";
					}
				}
				innerList.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(priority);
				innerList.add(pClass);
				teamGoalList.add(innerList);
				
				hmTeamGoals.put(rs.getString("goal_parent_id"), teamGoalList);
				
				Map<String, String> hmIndGoalCalDetailsParent = new HashMap<String, String>();
				getIndGoalData(uF, rs.getString("goal_id"), hmIndGoalCalDetailsParent);
				hmIndGoalCalDetailsTeam.put(rs.getString("goal_id"), hmIndGoalCalDetailsParent);
				
			}
			rs.close();
			pst.close();
			request.setAttribute("teamGoalList", teamGoalList);
			request.setAttribute("hmIndGoalCalDetailsTeam", hmIndGoalCalDetailsTeam);
			request.setAttribute("hmTeamGoals", hmTeamGoals);
//			System.out.println("hmIndGoalCalDetailsTeam ===> "+hmIndGoalCalDetailsTeam);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	public void getTeamGoalAverageDetails() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			strEmpOrgId = (String) session.getAttribute(ORGID);
			
			pst=con.prepareStatement("select goal_parent_id, goal_id from goal_details where (goal_type= ? or (goal_type= ? and goalalign_with_teamgoal = true)) " +
					" and org_id ="+uF.parseToInt(strEmpOrgId)+" and emp_ids like '%,"+strEmpId+",%' order by goal_id desc");
			pst.setInt(1, INDIVIDUAL_GOAL);
			pst.setInt(2, PERSONAL_GOAL);
			rs = pst.executeQuery();
			Map<String, List<String>> hmGoalIdTeamwise = new HashMap<String, List<String>>();
			List<String> goalIdList = new ArrayList<String>();
			while (rs.next()) {
				goalIdList = hmGoalIdTeamwise.get(rs.getString("goal_parent_id"));
				if(goalIdList == null) goalIdList = new ArrayList<String>();
				goalIdList.add(rs.getString("goal_id"));
				hmGoalIdTeamwise.put(rs.getString("goal_parent_id"), goalIdList);
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmGoalAverage = (Map<String, String>) request.getAttribute("hmGoalAverage");
			Map<String, String> hmTeamGoalAverage = new HashMap<String, String>();
			Iterator<String> it = hmGoalIdTeamwise.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				List<String> goalIdList1 = hmGoalIdTeamwise.get(key);
			double teamAllGoalAverage = 0.0d;
			double teamGoalAverage = 0.0d;
			int teamGoalCnt = 0;
			for(int i=0; goalIdList1 != null && !goalIdList1.isEmpty() && i< goalIdList1.size(); i++) {
				if(hmGoalAverage != null && !hmGoalAverage.isEmpty()){
					if(hmGoalAverage.get(goalIdList1.get(i)) != null) {
						teamAllGoalAverage += uF.parseToDouble(hmGoalAverage.get(goalIdList1.get(i)));
						teamGoalCnt++;
					}
				}
			}
			if(teamGoalCnt > 0){
				teamGoalAverage = teamAllGoalAverage / teamGoalCnt;
			}
			hmTeamGoalAverage.put(key, ""+teamGoalAverage);
			}
			request.setAttribute("hmTeamGoalAverage", hmTeamGoalAverage);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void getGoalRating() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try { 
			/*pst=con.prepareStatement("select question_bank_id,aaa.goal_kra_target_id,goal_title,average from (select question_bank_id,question_text," +
					"goal_kra_target_id,(marks*100/weightage) as average from (select question_bank_id,question_text,goal_kra_target_id from " +
					"question_bank where goal_kra_target_id in(select goal_id from goal_details where is_measure_kra = false and emp_ids like " +
					"'%,"+strEmpId+",%' and (goal_type = ? or goal_type = ?))) as a, appraisal_question_answer aqa where " +
					"a.question_bank_id=aqa.question_id) as aaa, goal_details gd where aaa.goal_kra_target_id = gd.goal_id");
			pst.setInt(1, INDIVIDUAL_GOAL); 
			pst.setInt(2, PERSONAL_GOAL);
			rs=pst.executeQuery();
			Map<String, String> hmGoalAverage = new HashMap<String, String>();
			while (rs.next()) {
				hmGoalAverage.put(rs.getString("goal_kra_target_id"), rs.getString("average"));
			}
			rs.close();
			pst.close();*/
			
			Map<String, String> hmKRAGoalId = new HashMap<String, String>();
			Map<String, String> hmGoalIds = new HashMap<String, String>();
			Map<String, String> hmGoalAverage = new HashMap<String, String>();
			Map<String, String> hmGoalAvg = new HashMap<String, String>();
			Map<String, String> hmGoalEmpAvg = new HashMap<String, String>();
			pst = con.prepareStatement("select gksrd.*, gd.goal_id from goal_kra_status_rating_details gksrd, goal_details gd where gksrd.goal_id = gd.goal_id and gksrd.emp_id in ("+strEmpId+")");
			rs = pst.executeQuery();
			while (rs.next()) {
				if((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals("")) || (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals("")) ) {
					double strCurrGoalORTargetRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
					if(rs.getString("manager_rating") == null) {
						strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("hr_rating"));
					} else if(rs.getString("hr_rating") == null) {
						strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("manager_rating"));
					}
					hmGoalAvg.put(rs.getString("goal_id"), strCurrGoalORTargetRating+"");
					hmGoalIds.put(rs.getString("goal_id"), rs.getString("goal_id"));
				}
			}
			rs.close();
			pst.close();
	
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(gksrd.user_rating) as user_rating, count(*) as cnt, gksrd.goal_id from goal_kra_emp_status_rating_details gksrd, " +
				"goal_details gd where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+strEmpId+") and user_type != '-' group by gksrd.goal_id");
			pst=con.prepareStatement(sbQuery.toString());
//				System.out.println("TAS pst ===>> " + pst);
			rs=pst.executeQuery();
			while (rs.next()) {
				if((rs.getString("user_rating") != null && !rs.getString("user_rating").equals("")) ) {
//						double strCurrGoalORTargetRating = rs.getDouble("user_rating")/ rs.getInt("cnt");
					double goalEmpAvgScore = (rs.getDouble("user_rating") / rs.getDouble("cnt"));
					hmGoalEmpAvg.put(rs.getString("goal_id"), goalEmpAvgScore+"");
					hmGoalIds.put(rs.getString("goal_id"), rs.getString("goal_id"));
				}
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmGoalRating = new HashMap<String, String>();
			
				pst=con.prepareStatement("select gksrd.*, gk.goal_id from goal_kra_status_rating_details gksrd, goal_kras gk where gksrd.kra_id = gk.goal_kra_id and gksrd.emp_id in ("+strEmpId+")");
				rs=pst.executeQuery();
				while (rs.next()) {
					
					if((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals("")) || (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals("")) ) {
						
						double strGoalwiseTaskRating = uF.parseToDouble(hmGoalRating.get(rs.getString("goal_id")+"_RATING"));
						int strGoalwiseTaskCount = uF.parseToInt(hmGoalRating.get(rs.getString("goal_id")+"_COUNT"));
						strGoalwiseTaskCount++;
						double strGoalwiseCurrTaskRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
						if(rs.getString("manager_rating") == null) {
							strGoalwiseCurrTaskRating = uF.parseToDouble(rs.getString("hr_rating"));
						} else if(rs.getString("hr_rating") == null) {
							strGoalwiseCurrTaskRating = uF.parseToDouble(rs.getString("manager_rating"));
						}
						strGoalwiseTaskRating += strGoalwiseCurrTaskRating;
						hmGoalRating.put(rs.getString("goal_id")+"_RATING", strGoalwiseTaskRating+"");
						hmGoalRating.put(rs.getString("goal_id")+"_COUNT", strGoalwiseTaskCount+"");
						hmGoalIds.put(rs.getString("goal_id"), rs.getString("goal_id"));
						hmKRAGoalId.put(rs.getString("goal_id"), rs.getString("goal_id"));
					}
				}
				rs.close();
				pst.close();
				
				Iterator<String> kraIt = hmKRAGoalId.keySet().iterator();
				while (kraIt.hasNext()) {
					String goalId = kraIt.next();
					String goalRating = hmGoalRating.get(goalId+"_RATING");
					String goalTaskCount = hmGoalRating.get(goalId+"_COUNT");
					double avgGoalRating = 0.0d;
					if(uF.parseToInt(goalTaskCount) > 0) {
						avgGoalRating = uF.parseToDouble(goalRating) / uF.parseToInt(goalTaskCount);
					}
					hmGoalAvg.put(goalId, avgGoalRating+"");
				}
				
				Iterator<String> it = hmGoalIds.keySet().iterator();
				while (it.hasNext()) {
					String goalId = it.next();
					String goalAvg = hmGoalAvg.get(goalId);
					String goalEmpAvg = hmGoalEmpAvg.get(goalId);
					
					double avgGoalRating = 0.0d;
					if(uF.parseToDouble(goalAvg)>0 && uF.parseToDouble(goalEmpAvg)>0) {
						avgGoalRating = (uF.parseToDouble(goalAvg) + uF.parseToDouble(goalEmpAvg)) / 2;
					} else if(uF.parseToDouble(goalAvg)>0) {
						avgGoalRating = uF.parseToDouble(goalAvg);
					} else if(uF.parseToDouble(goalEmpAvg)>0) {
						avgGoalRating = uF.parseToDouble(goalEmpAvg);
					}
					hmGoalAverage.put(goalId, avgGoalRating+"");
				}
				
//			System.out.println("hmGoalAverage === >> " + hmGoalAverage);
			request.setAttribute("hmGoalAverage", hmGoalAverage);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private void getIndGoalData(UtilityFunctions uF, String parentID, Map<String, String> hmIndGoalCalDetailsParent){
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,Map<String,String>> hmIndGoalCalTeam= new HashMap<String, Map<String,String>>();
		Map<String,Map<String,Map<String,String>>> hmIndGoalCalTeamParent= new HashMap<String, Map<String,Map<String,String>>>();
		try {
			
			pst = con.prepareStatement("select * from goal_details where goal_parent_id = ? and is_measure_kra = true and measure_type !='' order by goal_id ");
			pst.setInt(1, uF.parseToInt(parentID));
			rs = pst.executeQuery();
//			System.out.println("PST===> "+pst);
			while (rs.next()) {
				Map<String, String> hmIndGoalCalDetails = new HashMap<String, String>();
				getIndividualGoalTargetCalculation(con, rs.getString("goal_id"), rs.getString("emp_ids"), rs.getString("measure_type"), rs.getString("measure_currency_value"),
						rs.getString("measure_effort_days"), rs.getString("measure_effort_hrs"), uF, hmIndGoalCalDetails);
//				System.out.println("hmIndGoalCalDetails==="+hmIndGoalCalDetails);
				hmIndGoalCalTeam.put(rs.getString("goal_id"), hmIndGoalCalDetails);
				hmIndGoalCalTeamParent.put(rs.getString("goal_parent_id"), hmIndGoalCalTeam);
			}
			rs.close();
			pst.close();
			
			String alltwoDeciTotProgressAvg ="0";
	 		String alltotal100 ="100";
	 		String strtwoDeciTot = "0";
	 		double dblalltwoDeciTotProgressAvg = 0;
	 		double dblalltotal100 = 0;
	 		double dblstrtwoDeciTot = 0;
			Iterator<String> it1 = hmIndGoalCalTeamParent.keySet().iterator();
			while (it1.hasNext()) {
				String parentid =(String)it1.next();
//				System.out.println("parentid ===> "+parentid);
				Map<String,Map<String,String>> hmIndGoalCalTeam1 = hmIndGoalCalTeamParent.get(parentid);
				Iterator<String> it2 = hmIndGoalCalTeam1.keySet().iterator();
				int cnt=0;
				while (it2.hasNext()) {
					String goalid =(String)it2.next();
					Map<String, String> hmIndGoalCalDetails = hmIndGoalCalTeam1.get(goalid);
					dblalltwoDeciTotProgressAvg += uF.parseToDouble(hmIndGoalCalDetails.get(goalid+"_PERCENT"));
					dblalltotal100 += uF.parseToDouble(hmIndGoalCalDetails.get(goalid+"_TOTAL"));
					dblstrtwoDeciTot += uF.parseToDouble(hmIndGoalCalDetails.get(goalid+"_STR_PERCENT"));
					cnt++;
				}
				double percentAmt = dblalltwoDeciTotProgressAvg / cnt;
				double totalAmt = dblalltotal100 / cnt;
				double strPercentAmt = dblstrtwoDeciTot / cnt;
				alltwoDeciTotProgressAvg = ""+Math.round(percentAmt);
				alltotal100 = ""+Math.round(totalAmt);
				strtwoDeciTot = ""+Math.round(strPercentAmt);
				hmIndGoalCalDetailsParent.put(parentid+"_PERCENT", alltwoDeciTotProgressAvg);
				hmIndGoalCalDetailsParent.put(parentid+"_TOTAL", alltotal100);
				hmIndGoalCalDetailsParent.put(parentid+"_STR_PERCENT", strtwoDeciTot);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private void getIndividualGoalTargetCalculation(Connection con, String indGoalId, String empIds, String measureType, String targetAmt, String strTargetDays, String strTargetHrs, UtilityFunctions uF,Map<String, String> hmIndGoalCalDetails){
		String alltwoDeciTotProgressAvg ="0";
 		String alltotal100 ="100";
 		String strtwoDeciTot = "0";
 		String strTotTarget = "0";
 		String strTotDays = "0";
 		String strTotHrs = "0";
 		Map<String,String> hmTargetValue=getMaxAchievedTargetBYEmpAndGoalwise(con, uF);
		if(empIds !=null){
			List<String> emplistID=Arrays.asList(empIds.split(","));
			double alltotalTarget=0, allTotal=0, alltwoDeciTot=0, totTarget=0;
			int empListSize=0;
			int allTotHRS =0;
			for(int i=0; emplistID!=null && i<emplistID.size();i++){
				empListSize = emplistID.size()-1;		
			if(emplistID.get(i)!=null && !emplistID.get(i).equals("")){
			String target="0";
			if(hmTargetValue != null && hmTargetValue.get(emplistID.get(i)+"_"+indGoalId)!= null){
				target=hmTargetValue.get(emplistID.get(i)+"_"+indGoalId);
			}
			
			String twoDeciTotProgressAvg = "0";
			String twoDeciTot = "0";
			String total="100";
			double totalTarget=0;
			if(measureType!=null && !measureType.equals("Effort")){
				totalTarget=(uF.parseToDouble(target)/uF.parseToDouble(targetAmt))*100;
				twoDeciTot=uF.formatIntoTwoDecimal(totalTarget);
			}else{
				String t=""+uF.parseToDouble(target);
				String days="0";
				String hours="0";
				if(t.contains(".")){
					t=t.replace(".","_");
					String[] temp=t.split("_");
					days=temp[0];
					hours=temp[1];
				}	
				String targetDays = strTargetDays;
				String targetHrs = strTargetHrs;
				int daysInHrs = uF.parseToInt(days) * 8;
				int inttotHrs = daysInHrs + uF.parseToInt(hours);
				allTotHRS += inttotHrs;
				
				int targetDaysInHrs = uF.parseToInt(targetDays) * 8;
				int inttotTargetHrs = targetDaysInHrs + uF.parseToInt(targetHrs);
				if(inttotTargetHrs != 0){
					totalTarget= uF.parseToDouble(""+inttotHrs) / uF.parseToDouble(""+inttotTargetHrs) * 100;
				}
				twoDeciTot=uF.formatIntoTwoDecimal(totalTarget);
			}
				if(totalTarget > new Double(100) && totalTarget<=new Double(150)){
					double totalTarget1=(totalTarget/150)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="150";
				}else if(totalTarget > new Double(150) && totalTarget<=new Double(200)){
					double totalTarget1=(totalTarget/200)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="200";
				}else if(totalTarget > new Double(200) && totalTarget<=new Double(250)){
					double totalTarget1=(totalTarget/250)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="250";
				}else if(totalTarget > new Double(250) && totalTarget<=new Double(300)){
					double totalTarget1=(totalTarget/300)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="300";
				}else if(totalTarget > new Double(300) && totalTarget<=new Double(350)){
					double totalTarget1=(totalTarget/350)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="350";
				}else if(totalTarget > new Double(350) && totalTarget<=new Double(400)){
					double totalTarget1=(totalTarget/400)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="400";
				}else if(totalTarget > new Double(400) && totalTarget<=new Double(450)){
					double totalTarget1=(totalTarget/450)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="450";
				}else if(totalTarget > new Double(450) && totalTarget<=new Double(500)){
					double totalTarget1=(totalTarget/500)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="500";
				}else{
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget);
					if(uF.parseToDouble(twoDeciTotProgressAvg) > 100){
						twoDeciTotProgressAvg = "100";
						total=""+Math.round(totalTarget);
					}else{
						total="100";
					}
				}
				alltotalTarget += uF.parseToDouble(twoDeciTotProgressAvg);
				allTotal += uF.parseToDouble(total);
				alltwoDeciTot += uF.parseToDouble(twoDeciTot);
				totTarget += uF.parseToDouble(target);
			}
			}
			double alltotAvg = alltotalTarget/empListSize;
			double alltot100Avg = allTotal/empListSize;
			double alltwoDeciTotAvg = alltwoDeciTot/empListSize;
			double allTotTagetAvg = totTarget/empListSize;
			int allTotHRSAvg = allTotHRS / empListSize;
			int avgDAYS = allTotHRSAvg / 8;
			int avgHRS  = allTotHRSAvg % 8;
			strTotDays = ""+avgDAYS;
			strTotHrs = ""+avgHRS;
			alltwoDeciTotProgressAvg = ""+Math.round(alltotAvg);
			alltotal100 = ""+Math.round(alltot100Avg);
			strtwoDeciTot = ""+Math.round(alltwoDeciTotAvg);
			strTotTarget = ""+Math.round(allTotTagetAvg);
		}
		hmIndGoalCalDetails.put(indGoalId+"_PERCENT", alltwoDeciTotProgressAvg);
		hmIndGoalCalDetails.put(indGoalId+"_TOTAL", alltotal100);
		hmIndGoalCalDetails.put(indGoalId+"_STR_PERCENT", strtwoDeciTot);
		hmIndGoalCalDetails.put(indGoalId+"_ACHIVED_TARGET", strTotTarget);
		hmIndGoalCalDetails.put(indGoalId+"_ACHIVED_DAYS", strTotDays);
		hmIndGoalCalDetails.put(indGoalId+"_ACHIVED_HRS", strTotHrs);
		
	}
	
	
	private Map<String, String> getMaxAchievedTargetBYEmpAndGoalwise(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmTargetValue = new HashMap<String,String>();
		try {
			
			Map<String, String> hmTargetID= new HashMap<String,String>();
			Map<String, String> hmTargetTmpValue= new HashMap<String,String>();
			pst = con.prepareStatement("select * from target_details where target_id in (select max(target_id) from target_details group by goal_id,emp_id)");
			rs= pst.executeQuery();
			while(rs.next()){
				hmTargetValue.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), rs.getString("amt_percentage"));
				hmTargetID.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), rs.getString("target_id"));
				hmTargetTmpValue.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), rs.getString("emp_amt_percentage"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmTargetValue", hmTargetValue);
//			System.out.println("hmTargetValue ===> " + hmTargetValue);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmTargetValue;
	}
	
	
//	private Map<String, String> getAttributeMap(Connection con) {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Map<String, String> hmAttribute=new HashMap<String, String>();
//		try {
//			
//			pst = con.prepareStatement("select * from appraisal_attribute where status=true");
//			rs=pst.executeQuery();
//			
//			while(rs.next()){
//				hmAttribute.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return hmAttribute;
//	}

//	private String getAppendData(Connection con, String strID, Map<String, String> mp, UtilityFunctions uF) {
//		StringBuilder sb = new StringBuilder();
//		Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
//		
//		if (strID != null && !strID.equals("")) {
//			strID=strID.substring(1,strID.length()-1);
//			if (strID.contains(",")) {
//
//				String[] temp = strID.split(",");
//
//				for (int i = 0; i < temp.length; i++) {
//					if (i == 0) {
//						sb.append(mp.get(temp[i].trim())+" ("+uF.showData(hmDesignation.get(temp[i].trim()), "")+")");
//					} else {
//						sb.append(", " + mp.get(temp[i].trim())+" ("+uF.showData(hmDesignation.get(temp[i].trim()), "")+")");
//					}
//				}
//			} else {
//				return mp.get(strID)+" ("+hmDesignation.get(strID)+")";
//			}
//
//		} else {
//			return null;
//		}
//
//		return sb.toString();
//	}
	
	
	
	
	public void getMyTargetAchieve() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try { 
			pst=con.prepareStatement("select (percentage/target_count) as average from (select count(*) as target_count," +
					"sum(amt_percentage) as percentage from (select max(target_id) as target_id from (select goal_id from goal_details " +
					"where emp_ids like '%,"+strEmpId+",%' and goal_type = ? and measure_kra='Measure') as a, target_details td where a.goal_id = td.goal_id group by a.goal_id) as a," +
					"target_details td where a.target_id=td.target_id) as b");
			pst.setInt(1, INDIVIDUAL_GOAL);
//			System.out.println("pst======>"+pst);
			rs=pst.executeQuery();
			double achievedAmt = 0.0d;
			while (rs.next()) {
				achievedAmt =rs.getDouble("average");
			}
			rs.close();
			pst.close();
			
			pst=con.prepareStatement("select (percentage/goal_count) as average, goal_count from (select count(*) as goal_count, sum(measure_currency_value) as percentage " +
					"from goal_details where emp_ids like '%,"+strEmpId+",%' and goal_type = ? and measure_kra='Measure') as b");
			pst.setInt(1, INDIVIDUAL_GOAL);
//			System.out.println("pst======>"+pst);
			rs=pst.executeQuery();
			double targetedAmt = 0.0d;
			int myTargetsCnt = 0;
			while (rs.next()) {
				targetedAmt = rs.getDouble("average");
				myTargetsCnt = rs.getInt("goal_count");
			}
			rs.close();
			pst.close();
			
			double achievedPercent = 0.0d;
			if(targetedAmt > 0d) {
				achievedPercent = (achievedAmt*100) / targetedAmt;  
			}
			
//			System.out.println("tachievedPercent ===> " + achievedPercent);
			
			String twoDeciTotProgressAvg = "0";
			String twoDeciTot = "0";
			String total="100";
			double totalTarget=achievedPercent;
			
				if(totalTarget > new Double(100) && totalTarget<=new Double(150)){
					double totalTarget1=(totalTarget/150)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="150";
				}else if(totalTarget > new Double(150) && totalTarget<=new Double(200)){
					double totalTarget1=(totalTarget/200)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="200";
				}else if(totalTarget > new Double(200) && totalTarget<=new Double(250)){
					double totalTarget1=(totalTarget/250)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="250";
				}else if(totalTarget > new Double(250) && totalTarget<=new Double(300)){
					double totalTarget1=(totalTarget/300)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="300";
				}else if(totalTarget > new Double(300) && totalTarget<=new Double(350)){
					double totalTarget1=(totalTarget/350)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="350";
				}else if(totalTarget > new Double(350) && totalTarget<=new Double(400)){
					double totalTarget1=(totalTarget/400)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="400";
				}else if(totalTarget > new Double(400) && totalTarget<=new Double(450)){
					double totalTarget1=(totalTarget/450)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="450";
				}else if(totalTarget > new Double(450) && totalTarget<=new Double(500)){
					double totalTarget1=(totalTarget/500)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="500";
				}else{
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget);
					if(uF.parseToDouble(twoDeciTotProgressAvg) > 100){
						twoDeciTotProgressAvg = "100";
						total=""+Math.round(totalTarget);
					}else{
						total="100";
					}
				}
			
			double remainPercent = 100 - Math.round(uF.parseToDouble(twoDeciTotProgressAvg));
//			System.out.println("tremainPercent ===> " + remainPercent);
			request.setAttribute("myTargetsCnt", ""+myTargetsCnt);
			request.setAttribute("myTargetachievedPercent", ""+Math.round(uF.parseToDouble(twoDeciTotProgressAvg)));
			request.setAttribute("myTargetremainPercent", ""+remainPercent);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
	}

//	public void getMyTeamTargetAchieve() {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		try {
//			pst = con.prepareStatement("select emp_ids from goal_details where goal_type = ? and emp_ids like '%,"+strEmpId+",%'");
//			pst.setInt(1, TEAM_GOAL);
//			rs = pst.executeQuery();
//			StringBuilder sb=new StringBuilder();
//			int i=0;
//			
//			while(rs.next()){
//				String emp_ids=rs.getString("emp_ids");
//				emp_ids=emp_ids.substring(1, emp_ids.length()-1);
//				if(i==0){
//					sb.append(emp_ids);
//				}else{
//					sb.append(","+emp_ids);
//				}
//				i++;				
//			}
//			double remainPercent = 0.0d;
//			String twoDeciTotProgressAvg = "0";
//			if(sb.length()>0){
//				List<String> empList=Arrays.asList(sb.toString().split(","));
//				StringBuilder sbQuery=new StringBuilder();
//				sbQuery.append("select (percentage/target_count) as average from (select count(*) as target_count," +
//						"sum(amt_percentage) as percentage from (select max(target_id) as target_id from (select goal_id from goal_details");
//				sbQuery.append(" where goal_type = ? and measure_kra='Measure' and (");
//                for(i=0; empList!=null && i<empList.size(); i++){
//                    sbQuery.append(" emp_ids like '%,"+empList.get(i)+",%'");
//                    
//                    if(i<empList.size()-1){
//                        sbQuery.append(" OR "); 
//                    }
//                }
//                sbQuery.append(" )) as a, target_details td where a.goal_id = td.goal_id) as a,target_details td " +
//						"where a.target_id=td.target_id) as b");
//				
//				pst=con.prepareStatement(sbQuery.toString());
//				pst.setInt(1, INDIVIDUAL_GOAL);
//				rs=pst.executeQuery();
//				double achievedAmt = 0.0d;
//				while (rs.next()) {
//					achievedAmt =rs.getDouble("average");
//				}
//				
//				sbQuery=new StringBuilder();
//				sbQuery.append("select (percentage/goal_count) as average from (select count(*) as goal_count, sum(measure_currency_value) as percentage " +
//						"from goal_details ");
//				sbQuery.append(" where goal_type = ? and measure_kra='Measure' and (");
//                for(i=0; empList!=null && i<empList.size(); i++){
//                    sbQuery.append(" emp_ids like '%,"+empList.get(i)+",%'");
//                    
//                    if(i<empList.size()-1){
//                        sbQuery.append(" OR "); 
//                    }
//                }
//                sbQuery.append(" )) as b");
//				pst=con.prepareStatement(sbQuery.toString());
//				pst.setInt(1, INDIVIDUAL_GOAL);
//				rs=pst.executeQuery();
//				double targetedAmt = 0.0d;
//				while (rs.next()) {
//					targetedAmt = rs.getDouble("average");
//				}
//				double achievedPercent = 0.0d;
////				System.out.println("tachievedAmt ===> " + achievedAmt +" targetedAmt ===> " + targetedAmt);
//				if(targetedAmt > 0d) {
//					achievedPercent = (achievedAmt*100) / targetedAmt;  
//				}
//				
////				System.out.println("tachievedPercent ===> " + achievedPercent);
//				
//				
//				String twoDeciTot = "0";
//				String total="100";
//				double totalTarget=achievedPercent;
//				
//					if(totalTarget > new Double(100) && totalTarget<=new Double(150)){
//						double totalTarget1=(totalTarget/150)*100;
//						twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
//						total="150";
//					}else if(totalTarget > new Double(150) && totalTarget<=new Double(200)){
//						double totalTarget1=(totalTarget/200)*100;
//						twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
//						total="200";
//					}else if(totalTarget > new Double(200) && totalTarget<=new Double(250)){
//						double totalTarget1=(totalTarget/250)*100;
//						twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
//						total="250";
//					}else if(totalTarget > new Double(250) && totalTarget<=new Double(300)){
//						double totalTarget1=(totalTarget/300)*100;
//						twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
//						total="300";
//					}else if(totalTarget > new Double(300) && totalTarget<=new Double(350)){
//						double totalTarget1=(totalTarget/350)*100;
//						twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
//						total="350";
//					}else if(totalTarget > new Double(350) && totalTarget<=new Double(400)){
//						double totalTarget1=(totalTarget/400)*100;
//						twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
//						total="400";
//					}else if(totalTarget > new Double(400) && totalTarget<=new Double(450)){
//						double totalTarget1=(totalTarget/450)*100;
//						twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
//						total="450";
//					}else if(totalTarget > new Double(450) && totalTarget<=new Double(500)){
//						double totalTarget1=(totalTarget/500)*100;
//						twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
//						total="500";
//					}else{
//						twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget);
//						if(uF.parseToDouble(twoDeciTotProgressAvg) > 100){
//							twoDeciTotProgressAvg = "100";
//							total=""+Math.round(totalTarget);
//						}else{
//							total="100";
//						}
//					}
//				
//				remainPercent = 100 - Math.round(uF.parseToDouble(twoDeciTotProgressAvg));
//			}
////			System.out.println("tremainPercent ===> " + remainPercent);
//			request.setAttribute("targetachievedPercent", ""+Math.round(uF.parseToDouble(twoDeciTotProgressAvg)));
//			request.setAttribute("targetremainPercent", ""+remainPercent);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//	}

	public void getMyLearningCompleted() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			int hybridLearning = 0;
			int coursesLearning = 0;
			int assessmentLearning = 0;
			int classRoomLearning = 0; 
			
			/*pst = con.prepareStatement("select count(learning_type) as Count,learning_type,learning_plan_id from learning_plan_stage_details " +
					"where learning_plan_id in(select learning_plan_id from learning_plan_details where learner_ids like '%,"+strEmpId+",%') " +
					"group by learning_type,learning_plan_id");*/
			pst = con.prepareStatement("select count(learning_type) as Count,learning_type,learning_plan_id from learning_plan_stage_details " +
					"where learning_plan_id in(select learning_plan_id from learning_plan_details where learner_ids like '%,"+strEmpId+",%' and is_publish=true) " +
					"group by learning_type,learning_plan_id");
//			System.out.println("pst==="+pst);
			rs = pst.executeQuery();
			int i=0;
			Map<String, String> hmlPlanStageCount = new HashMap<String, String>();
			Map<String, String> hmlPlanStageData = new HashMap<String, String>();
			String strLearnId=null;
			List<String> myLearnings = new ArrayList<String>();
			while(rs.next()){
				if(!myLearnings.contains(rs.getString("learning_plan_id"))) {
					myLearnings.add(rs.getString("learning_plan_id"));
				}
				
				int cnt=uF.parseToInt(hmlPlanStageCount.get(rs.getString("learning_plan_id")));
				cnt++;
				hmlPlanStageCount.put(rs.getString("learning_plan_id"), ""+cnt);
				hmlPlanStageData.put(rs.getString("learning_plan_id")+"_TYPE", rs.getString("learning_type"));
				if(strLearnId == null){
					strLearnId = rs.getString("learning_plan_id");
				}else{
					strLearnId += ","+ rs.getString("learning_plan_id");
				}
			}
			rs.close();
			pst.close();
			
			
			if(strLearnId != null){
				Map<String, String> hmlPlanTodate = new HashMap<String, String>();
				pst = con.prepareStatement("select max(to_date) as maxDate,learning_plan_id from learning_plan_stage_details " +
						"where learning_plan_id in ("+strLearnId+") group by learning_plan_id");
//				System.out.println("pst==="+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					String maxToDate = rs.getString("maxDate");
					hmlPlanTodate.put(rs.getString("learning_plan_id"), maxToDate);
				}
				rs.close();
				pst.close();
				
//				System.out.println("hmlPlanTodate====>"+hmlPlanTodate);
				Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
//				System.out.println("currDate====>"+currDate);
				Iterator<String> it = hmlPlanStageCount.keySet().iterator();
				
				Map<String, String> hmlPlanType = new HashMap<String, String>();
				while (it.hasNext()) {
					String str = it.next();
					String cnt = hmlPlanStageCount.get(str);
					Date maxDate= uF.getDateFormat(hmlPlanTodate.get(str), DBDATE);
//					System.out.println("maxDate====>"+maxDate);
					//String toDateDiff = uF.dateDifference(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, hmlPlanTodate.get(str), DBDATE);
//					if(currDate.after(maxDate)){
//						System.out.println("max====>");
						if(uF.parseToInt(cnt) > 1){
							hmlPlanType.put(str, "Hybrid");
						} else {
							hmlPlanType.put(str, hmlPlanStageData.get(str+"_TYPE"));
						}
//					}
				}
//				System.out.println("hmlPlanType====>"+hmlPlanType);
				Iterator<String> it1 = hmlPlanType.keySet().iterator();
				int hybridCnt=0;
				int assessCnt =0;
				int classroomCnt = 0;
				int courseCnt = 0;
				
				while(it1.hasNext()){
					String lplanId = it1.next();
					String lplanType = hmlPlanType.get(lplanId);
					if(lplanType != null && lplanType.equals("Hybrid")) {
						hybridCnt++;
					} else if(lplanType != null && lplanType.equals("Assessment")) {
						assessCnt++;
					} else if(lplanType != null && lplanType.equals("Course")) {
						courseCnt++;
					} else if(lplanType != null && lplanType.equals("Training")) {
						classroomCnt++;
					}  
				}
				hybridLearning = hybridCnt;
				assessmentLearning = assessCnt;
				classRoomLearning = classroomCnt;
				coursesLearning = courseCnt;				
			}
			request.setAttribute("myLearnings", myLearnings);
			
			request.setAttribute("hybridLearning", ""+hybridLearning);
			request.setAttribute("coursesLearning", ""+coursesLearning);
			request.setAttribute("assessmentLearning", ""+assessmentLearning);
			request.setAttribute("classRoomLearning", ""+classRoomLearning);
//			System.out.println("hybridLearning ====>> " + hybridLearning);
//			System.out.println("coursesLearning ====>> " + coursesLearning);
//			System.out.println("assessmentLearning ====>> " + assessmentLearning);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
	}


	public void getResignedEmployees() {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			Map<String, String> hmEmployeeMap = CF.getEmpNameMap(con, null, null);
			
			pst = con.prepareStatement("select * from emp_off_board where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and approved_1 is null order by entry_date desc");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			List<String> alResignedEmployees = new ArrayList<String>();
			int count=0;
			while (rs.next()) {

				StringBuilder sb = new StringBuilder();

				sb.append(hmEmployeeMap.get(rs.getString("emp_id")) +" has resigned on "+rs.getString("entry_date"));
				
				sb.append("<div id=\"myDiv"+count+"\" style=\"float:right;margin-right:10px;\"> ");
				sb.append("<a href=\"ResignationReport.action\">View</a> ");
				sb.append("<a href=\"javascript:void(0)\" onclick=\"getContent('myDiv"+count+"','UpdateRequest.action?S=1&M=1&RID="+rs.getString("off_board_id")+"&T=REG');\" ><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"></i></a> ");
				sb.append("<a href=\"javascript:void(0)\" onclick=\"getContent('myDiv"+count+"','UpdateRequest.action?S=-1&M=1&RID="+rs.getString("off_board_id")+"&T=REG');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i></a> ");
				sb.append("</div>");
				
				alResignedEmployees.add(sb.toString());
				count++;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alResignedEmployees", alResignedEmployees);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public void getPendingAttendanceIssues() {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		try {
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strYesterday = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DBDATE);
			
			pst = con.prepareStatement(selectApprovalsManager);			
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone() , 7));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			List<String> alReasons = new ArrayList<String>();
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strDate = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DBDATE);
				
				if(strDate!=null && strDate.equals(strToday)){
					strDate = "<span>today</span>";
				}else if(strDate!=null && strDate.equals(strYesterday)){
					strDate = "<span>yesterday</span>";
				}else {
					strDate = "on "+ uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, "EEEE");
					strDate = "<span>"+strDate.toLowerCase()+"</span>";
				}
				
				
				if("IN".equalsIgnoreCase(rs.getString("in_out"))){
					if(rs.getDouble("early_late")>0 && uF.parseToInt(rs.getString("approved")) == -2){
						alReasons.add(rs.getString("emp_fname")+", was late for office "+strDate+((rs.getString("reason")!=null && rs.getString("reason").length()>0)?" - \""+rs.getString("reason")+"\".":"."));
					}else if(rs.getDouble("early_late")<0 && uF.parseToInt(rs.getString("approved")) == -2){
						alReasons.add(rs.getString("emp_fname")+", has come early "+strDate+((rs.getString("reason")!=null && rs.getString("reason").length()>0)?" - \""+rs.getString("reason")+"\".":"."));
					}
					
				}else{
					if(rs.getDouble("early_late")>0 && uF.parseToInt(rs.getString("approved")) == -2){
						alReasons.add(rs.getString("emp_fname")+", has left late "+strDate+((rs.getString("reason")!=null && rs.getString("reason").length()>0)?" - \""+rs.getString("reason")+"\".":"."));
					}else if(rs.getDouble("early_late")<0 && uF.parseToInt(rs.getString("approved")) == -2){
						alReasons.add(rs.getString("emp_fname")+", has left early "+strDate+((rs.getString("reason")!=null && rs.getString("reason").length()>0)?" - \""+rs.getString("reason")+"\".":"."));
					}
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReasons", alReasons);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public void getUpcomingTeamLeaves(Map<String, String> hmEmployeeMap) {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			pst = con.prepareStatement("select  ele.emp_id,ele.entrydate,ele.approval_from,ele.approval_to_date,ele.leave_id from emp_leave_entry ele,work_flow_details wft where wft.emp_id=? " +
					"and ele.is_approved=1 and ele.approval_from>=? and ele.approval_to_date<=? " +
					" and ele.leave_id=wft.effective_id and wft.effective_type='"+WORK_FLOW_LEAVE+"' order by ele.approval_from ");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(3, uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 30));
			rs = pst.executeQuery();
			List<String> alLeaves = new ArrayList<String>();
			while(rs.next()){
				alLeaves.add(hmEmployeeMap.get(rs.getString("emp_id"))+", is on leave from "+uF.getDateFormat(rs.getString("approval_from"), DBDATE, "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, "dd MMM"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alLeaves", alLeaves);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getCandidateSalaryNegotiationRequests() {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {

			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
//			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
//			if (hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			
			pst = con.prepareStatement("SELECT * FROM candidate_application_details where offer_negotiation_approver=? and (offer_negotiation_request_approved_by is null " +
				"or offer_negotiation_request_approved_by=0) and need_approval_for_offer_negotiation=true order by offer_negotiation_approval_request_date ");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			List<String> alOfferNegotiationRequests = new ArrayList<String>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				String candiName = CF.getCandiNameByCandiId(con, rs.getString("candidate_id"));
				innerList.add(rs.getString("candidate_id"));
				innerList.add(rs.getString("recruitment_id"));
				innerList.add(candiName);
				innerList.add(rs.getString("job_code"));
				innerList.add(hmEmpName.get(rs.getString("offer_negotiation_approval_requested_by")));
				innerList.add(rs.getString("offer_negotiation_approval_request_date"));
				
				
				if(alOfferNegotiationRequests.size()<=10) {
					alOfferNegotiationRequests.add("<span style=\"width: 84%; float: left;\">"+hmEmpName.get(rs.getString("offer_negotiation_approval_requested_by"))+" has been requested for salary negotiation of <b>"
						+candiName+"</b> for '"+rs.getString("job_code")+"' on "+uF.getDateFormat(rs.getString("offer_negotiation_approval_request_date"), DBDATE, DATE_FORMAT_STR)+"</span>"+
						"<span style=\"float: right; width: 15%;\"> <a href=\"javascript:void(0);\" onclick=\"openCandidateProfilePopup('"+rs.getString("candidate_id")+"','"+rs.getString("recruitment_id")+"');\">View & Approve</a> " +
						"</span>");
						/*"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"approveDenyLeave('1','"+rs.getString("leave_id")+"','"+hmLevelMap.get(rs.getString("emp_id"))+"','"+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("leave_id"))+"');\"><i class=\"fa fa-check-circle checknew\" title=\"Approved\" aria-hidden=\"true\"></i></a> " +
						" <a href=\"javascript:void(0);\" onclick=\"approveDenyLeave('-1','"+rs.getString("leave_id")+"','"+hmLevelMap.get(rs.getString("emp_id"))+"','"+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("leave_id"))+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i></a></span>");*/
					
				}
			}
			rs.close();
			pst.close();
//			System.out.println("alOfferNegotiationRequests ===>> " + alOfferNegotiationRequests);
			
			request.setAttribute("alOfferNegotiationRequests", alOfferNegotiationRequests);
//			request.setAttribute("LEAVE_REQUEST_COUNT", intLeaveReqCnt+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getTeamLeaveRequests(Map<String, String> hmEmployeeMap,Map<String, String> hmLevelMap) {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			String strUserType = (String) session.getAttribute(USERTYPE);
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strYesterday = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DBDATE);
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if (hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			
			pst = con.prepareStatement("SELECT leave_type_id,is_compensatory FROM leave_type  where leave_type_id>0 order by leave_type_name");
			rs = pst.executeQuery();
			Map<String,String> hmLeaveCompensate = new HashMap<String, String>();
			while (rs.next()) {
				hmLeaveCompensate.put(rs.getString("leave_type_id"), rs.getString("is_compensatory"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
				"and effective_type='"+WORK_FLOW_LEAVE+"' and effective_id in (select ele.leave_id from emp_leave_entry ele,leave_type lt " +
				"where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0 and is_approved=0) group by effective_id"); //and ele.entrydate >=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()){
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select a.effective_id,a.member_position,w.user_type_id from (select effective_id,min(member_position)as member_position " +
				"from work_flow_details wfd where emp_id=? and is_approved=0 and effective_type='"+WORK_FLOW_LEAVE+"' " +
				"and effective_id in (select ele.leave_id from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id " +
				"and ele.leave_type_id > 0 and is_approved=0) and user_type_id > 0 group by effective_id) a, " + //and ele.entrydate >=?
				"work_flow_details w where a.effective_id=w.effective_id and a.member_position=w.member_position and w.emp_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 1));
			pst.setInt(2, uF.parseToInt(strEmpId));	
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			Map<String, String> hmMemNextApprovalUserTypeId = new HashMap<String, String>();
			while(rs.next()) {
				hmMemNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
				hmMemNextApprovalUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_LEAVE+"' " +
					"and effective_id in (select ele.leave_id from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id " +
					"and ele.leave_type_id > 0 and is_approved=-1) group by effective_id"); //and ele.entrydate >=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("effective_id"))){
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();			
			
			pst = con.prepareStatement("select leave_id from emp_leave_entry where is_approved=-1"); // and entrydate >=?
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("leave_id"))){
					deniedList.add(rs.getString("leave_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_LEAVE+"'" +
					" and effective_id in (select ele.leave_id from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id " +
					"and ele.leave_type_id > 0 and is_approved=0) order by effective_id,member_position"); //and ele.entrydate >=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
			while(rs.next()){
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select * from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0" +
				" and is_approved=0 and encashment_status=false order by ele.entrydate desc"); //and ele.entrydate >=? 
			pst=con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));
//			System.out.println("pst====>"+pst);
			rs=pst.executeQuery();	
			List<String> alLeaveRequest = new ArrayList<String>();
			int intLeaveReqCnt = 0;
			while(rs.next()) {
				List<String> checkEmpList = hmCheckEmp.get(rs.getString("leave_id"));
				if(checkEmpList==null) checkEmpList = new ArrayList<String>();
				
				if(!checkEmpList.contains(strEmpId) && !strUserType.equalsIgnoreCase(ADMIN)) {
					continue;
				}
				
				if(uF.parseToInt(hmNextApproval.get(rs.getString("leave_id"))) == uF.parseToInt(hmMemNextApproval.get(rs.getString("leave_id"))) && uF.parseToInt(hmNextApproval.get(rs.getString("leave_id")))>0) {
					String strDate = rs.getString("entrydate");
					if(strDate!=null && strDate.equals(strToday)) {
						strDate = ", <span>today</span>";
					} else if(strDate!=null && strDate.equals(strYesterday)) {
						strDate = ", <span>yesterday</span>";
					} else {
						strDate = " on "+ uF.getDateFormat(rs.getString("entrydate"), DBDATE, "dd MMM");
						strDate = "<span>"+strDate.toLowerCase()+"</span>";
					}
					String strLeaveLbl = "leave";
//					String strLeaveNotiLbl = N_MANAGER_LEAVE_REQUEST+"";
					if(uF.parseToBoolean(rs.getString("is_compensate"))) {
						strLeaveLbl = "extra working";
//						strLeaveNotiLbl = N_MANAGER_EXTRA_WORK_REQUEST+"";
					}
					String strApproveUser = hmMemNextApprovalUserTypeId.get(rs.getString("leave_id")) !=null ? "["+uF.showData(hmUserTypeMap.get(hmMemNextApprovalUserTypeId.get(rs.getString("leave_id"))), "")+"]" : ""; 
					if(alLeaveRequest.size()<=10) {
						alLeaveRequest.add("<span style=\"width: 82%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has applied "+strLeaveLbl+" from "+uF.getDateFormat(rs.getString("approval_from"), DBDATE, "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, "dd MMM")+strDate+" "+strApproveUser+"</span>"+
							"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"approveDenyLeave('1','"+rs.getString("leave_id")+"','"+hmLevelMap.get(rs.getString("emp_id"))+"','"+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("leave_id"))+"');\"><i class=\"fa fa-check-circle checknew\" title=\"Approved\" aria-hidden=\"true\"></i></a> " +
							" <a href=\"javascript:void(0);\" onclick=\"approveDenyLeave('-1','"+rs.getString("leave_id")+"','"+hmLevelMap.get(rs.getString("emp_id"))+"','"+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("leave_id"))+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i></a></span>");
					}
					intLeaveReqCnt++;
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alLeaveRequest", alLeaveRequest);
			request.setAttribute("LEAVE_REQUEST_COUNT", intLeaveReqCnt+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public void getTeamReimbursementRequests(Map<String, String> hmEmployeeMap) {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			String strUserType = (String) session.getAttribute(USERTYPE);
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if (hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strYesterday = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DBDATE);
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id, min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
				"and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' and effective_id in (select reimbursement_id from emp_reimbursement " +
				" where approval_1=0) group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()){
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select a.effective_id,a.member_position,w.user_type_id from (select effective_id,min(member_position)as member_position " +
				"from work_flow_details wfd where emp_id=? and is_approved=0 and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' " +
				"and effective_id in (select reimbursement_id from emp_reimbursement where approval_1=0) and user_type_id > 0 group by effective_id) a, " +
				"work_flow_details w where a.effective_id=w.effective_id and a.member_position=w.member_position and w.emp_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 1));
			pst.setInt(2, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			Map<String, String> hmMemNextApprovalUserTypeId = new HashMap<String, String>();
			while(rs.next()){
				hmMemNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
				hmMemNextApprovalUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' " +
				"and effective_id in (select reimbursement_id from emp_reimbursement where approval_1=-1) group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("effective_id"))){
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();			
			
			pst = con.prepareStatement("select reimbursement_id from emp_reimbursement where approval_1=-1 and approval_2=-1"); // and entry_date >=?
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("reimbursement_id"))){
					deniedList.add(rs.getString("reimbursement_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_REIMBURSEMENTS+"'" +
				" and effective_id in (select reimbursement_id from emp_reimbursement where approval_1=0) order by effective_id,member_position"); //entry_date >=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
			while(rs.next()){
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select * from emp_reimbursement where approval_1=0 and approval_2=0 and parent_id=0 order by entry_date desc"); //and entry_date >=? 
			pst=con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
//			System.out.println("pst====>"+pst);
			rs=pst.executeQuery();	
			List<String> alReimbursementRequest = new ArrayList<String>();
			int intReimbursementReqCnt=0; 
			while(rs.next()) {
				List<String> checkEmpList = hmCheckEmp.get(rs.getString("reimbursement_id"));
				if(checkEmpList == null) checkEmpList=new ArrayList<String>();
				
				if(!checkEmpList.contains(strEmpId) && !strUserType.equalsIgnoreCase(ADMIN)) {
					continue;
				}
				
				if(uF.parseToInt(hmNextApproval.get(rs.getString("reimbursement_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("reimbursement_id"))) && uF.parseToInt(hmNextApproval.get(rs.getString("reimbursement_id")))>0){
					String strDate = rs.getString("entry_date");
					if(strDate!=null && strDate.equals(strToday)) {
						strDate = ", <span>today</span>";
					} else if(strDate!=null && strDate.equals(strYesterday)) {
						strDate = ", <span>yesterday</span>";
					} else {
						strDate = " on "+ uF.getDateFormat(rs.getString("entry_date"), DBDATE, "dd MMM");
						strDate = "<span>"+strDate.toLowerCase()+"</span>";
					}
					String strApproveUser = hmMemNextApprovalUserTypeId.get(rs.getString("reimbursement_id")) !=null ? "["+uF.showData(hmUserTypeMap.get(hmMemNextApprovalUserTypeId.get(rs.getString("reimbursement_id"))), "")+"]" : ""; 
					
					String strCurrency = "";
					if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp_id"))) > 0){
						Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp_id")));
						if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
						strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
					} 
					
					if(alReimbursementRequest.size()<=10) {
						StringBuilder sb = new StringBuilder();
						sb.append("<span style=\"width: 100%; float: left;\"><strong>"+hmEmployeeMap.get(rs.getString("emp_id"))+"</strong>, has applied for reimbursement for <strong>"+strCurrency+uF.formatIntoComma(uF.parseToDouble(rs.getString("reimbursement_amount")))+"</strong> from "+uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat())+" "+strApproveUser+"</span>"+
								"<p style=\"float:left; width:90%; font-size:10px; font-style:italic;\">"+"Reimbursement Type:"+uF.showData(rs.getString("reimbursement_type"), "")+"<br/>Reason:"+uF.showData(rs.getString("reimbursement_purpose"), "")+"</p>");
						sb.append("<span style=\"float: right; margin-right: -25px;\"><a href=\"javascript:void(0);\" onclick=\"approveDenyReimbursement('1','" + rs.getString("reimbursement_id")+ "','"+hmMemNextApprovalUserTypeId.get(rs.getString("reimbursement_id"))+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  border=\"0\" title=\"Approved\" ></i></a>&nbsp;" +
								"<a href=\"javascript:void(0);\" onclick=\"approveDenyReimbursement('-1','" + rs.getString("reimbursement_id")+ "','"+hmMemNextApprovalUserTypeId.get(rs.getString("reimbursement_id"))+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i></a>" +
								"</span>");
						alReimbursementRequest.add(sb.toString());
					}
					intReimbursementReqCnt++;
				}
			}
			rs.close();
			pst.close();
			
			
			Map<String, Map<String, String>> hmBulkExpenseData = new LinkedHashMap<String, Map<String,String>>();
			StringBuilder sbExpenseType = null;
			StringBuilder sbExpensePurpose = null;
			sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_reimbursement where approval_1=0 and approval_2=0 and parent_id>0 order by entry_date desc");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();	
//			List<String> alReimbursementRequest = new ArrayList<String>();
			while(rs.next()) {
				List<String> checkEmpList = hmCheckEmp.get(rs.getString("reimbursement_id"));
				if(checkEmpList == null) checkEmpList=new ArrayList<String>();
				
				if(!checkEmpList.contains(strEmpId) && !strUserType.equalsIgnoreCase(ADMIN)) {
					continue;
				}
				if(uF.parseToInt(hmNextApproval.get(rs.getString("reimbursement_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("reimbursement_id"))) && uF.parseToInt(hmNextApproval.get(rs.getString("reimbursement_id")))>0) {
					Map<String, String> hmInner = hmBulkExpenseData.get(rs.getString("parent_id"));
					if(hmInner == null)	hmInner = new HashMap<String, String>();
					
					String strReimbursementAmout = hmInner.get("REIMBURSEMENT_AMOUNT");
					double dblReimbursementAmout = uF.parseToDouble(rs.getString("reimbursement_amount")) + uF.parseToDouble(strReimbursementAmout);
					
					if(hmInner.get("REIMBURSEMENT_TYPE") != null) {
						sbExpenseType.append("<b>,</b> "+rs.getString("reimbursement_type"));
					} else {
						sbExpenseType = new StringBuilder();
						sbExpenseType.append(rs.getString("reimbursement_type"));
					}
					
					if(hmInner.get("REIMBURSEMENT_PURPOSE") != null) {
						sbExpensePurpose.append("<b>,</b> "+rs.getString("reimbursement_purpose"));
					} else {
						sbExpensePurpose = new StringBuilder();
						sbExpensePurpose.append(rs.getString("reimbursement_purpose"));
					}
						
					String strDate = rs.getString("entry_date");
					if(strDate!=null && strDate.equals(strToday)) {
						strDate = ", <span>today</span>";
					} else if(strDate!=null && strDate.equals(strYesterday)) {
						strDate = ", <span>yesterday</span>";
					} else {
						strDate = " on "+ uF.getDateFormat(rs.getString("entry_date"), DBDATE, "dd MMM");
						strDate = "<span>"+strDate.toLowerCase()+"</span>";
					}
					String strApproveUser = hmMemNextApprovalUserTypeId.get(rs.getString("reimbursement_id")) !=null ? "["+uF.showData(hmUserTypeMap.get(hmMemNextApprovalUserTypeId.get(rs.getString("reimbursement_id"))), "")+"]" : ""; 
					
					String strCurrency = "";
					if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp_id"))) > 0){
						Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp_id")));
						if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
						strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
					} 
					
					hmInner.put("EMP_ID", rs.getString("emp_id"));
					hmInner.put("RI_FROM_DATE", rs.getString("from_date"));
					hmInner.put("RI_TO_DATE", rs.getString("to_date"));
					hmInner.put("REIMBURSEMENT_DATE", rs.getString("reimb_from_date"));
					hmInner.put("REIMBURSEMENT_INFO", rs.getString("reimbursement_info"));
					hmInner.put("REIMBURSEMENT_TYPE", sbExpenseType != null ? sbExpenseType.toString() : "");// rs.getString("reimbursement_type"));
					hmInner.put("REIMBURSEMENT_PURPOSE", sbExpensePurpose != null ? sbExpensePurpose.toString() : "");// rs.getString("reimbursement_purpose"));
					hmInner.put("SUBMITTED_DATE", rs.getString("entry_date"));
					hmInner.put("EMP_NAME", hmEmployeeMap.get(rs.getString("emp_id")));
					hmInner.put("CURRENCY", strCurrency);
					hmInner.put("APPROVE_USERTYPE", strApproveUser);
					hmInner.put("APPROVE_USERTYPE_ID", hmMemNextApprovalUserTypeId.get(rs.getString("reimbursement_id")));
					hmInner.put("REIMBURSEMENT_AMOUNT", dblReimbursementAmout+"");
					hmInner.put("REIMBURSEMENT_ID", rs.getString("reimbursement_id"));
					
					hmBulkExpenseData.put(rs.getString("parent_id"), hmInner);
				}
			}
			rs.close();
			pst.close();
			
			
			Iterator<String> it = hmBulkExpenseData.keySet().iterator();
			while(it.hasNext()) {
				String parentId = it.next();
				Map<String, String> hmInner = hmBulkExpenseData.get(parentId);
				if(alReimbursementRequest.size()<=10) {
					StringBuilder sb = new StringBuilder();
					sb.append("<span style=\"width: 100%; float: left;\"><strong>"+hmInner.get("EMP_NAME")+"</strong>, has applied for reimbursement for <strong>"+hmInner.get("CURRENCY")+uF.formatIntoComma(uF.parseToDouble(hmInner.get("REIMBURSEMENT_AMOUNT")))+"</strong> from "+uF.getDateFormat(hmInner.get("RI_FROM_DATE"), DBDATE, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(hmInner.get("RI_TO_DATE"), DBDATE, CF.getStrReportDateFormat())+" "+hmInner.get("APPROVE_USERTYPE")+"</span>"+
							"<p style=\"float:left; width:90%; font-size:10px; font-style:italic;\">"+"Reimbursement Type:"+uF.showData(hmInner.get("REIMBURSEMENT_TYPE"), "")+"<br/>Reason:"+uF.showData(hmInner.get("REIMBURSEMENT_PURPOSE"), "")+"</p>");
					sb.append("<span style=\"float: right; margin-right: -25px;\"><a href=\"javascript:void(0);\" onclick=\"approveDenyReimbursement('1','" + hmInner.get("REIMBURSEMENT_ID")+ "','"+hmInner.get("APPROVE_USERTYPE_ID")+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  border=\"0\" title=\"Approved\" ></i></a>&nbsp;" +
							"<a href=\"javascript:void(0);\" onclick=\"approveDenyReimbursement('-1','" + hmInner.get("REIMBURSEMENT_ID")+ "','"+hmInner.get("APPROVE_USERTYPE_ID")+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i></a>" +
							"</span>");
					alReimbursementRequest.add(sb.toString());
				}
				intReimbursementReqCnt++;
			}
			request.setAttribute("alReimbursementRequest", alReimbursementRequest);
			request.setAttribute("REIMBURSEMENT_REQUEST_COUNT", intReimbursementReqCnt+"");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public void getMyGrowthData() {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			String strLastSixMonthDate = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 180)+"", DBDATE, DATE_FORMAT);
			int nMonth = uF.parseToInt(uF.getDateFormat(strLastSixMonthDate, DATE_FORMAT, "MM"));
			int nYear = uF.parseToInt(uF.getDateFormat(strLastSixMonthDate, DATE_FORMAT, "yyyy"));
			
			String strCurrMonthDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
			int nCurrMonth = uF.parseToInt(uF.getDateFormat(strCurrMonthDate, DATE_FORMAT, "MM"));
			
			
			pst = con.prepareStatement("select earning_deduction,month,year,sum(amount) as amount from payroll_generation where emp_id=? " +
					"and ((month >=? and year >=?) or (month <=? and year>?)) and is_paid=true group by month,year,earning_deduction order by year,month,earning_deduction desc");
			pst.setInt(1, uF.parseToInt(strEmpId)); 
			pst.setInt(2, nMonth);
			pst.setInt(3, nYear); 
			pst.setInt(4, nCurrMonth);
			pst.setInt(5, nYear);
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmEarningAmount = new LinkedHashMap<String, Map<String,String>>();
			Map<String, Map<String, String>> hmDeductionAmount = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				if(rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equals("E")){
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("MONTH", rs.getString("month"));
					hmInner.put("YEAR", rs.getString("year").substring(2, rs.getString("year").length()));
					hmInner.put("AMOUNT", rs.getString("amount"));
					
					hmEarningAmount.put(rs.getString("month")+"_"+rs.getString("year"), hmInner);
				} else if(rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equals("D")){
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("MONTH", rs.getString("month"));
					hmInner.put("YEAR", rs.getString("year").substring(2, rs.getString("year").length()));
					hmInner.put("AMOUNT", rs.getString("amount"));
					
					hmDeductionAmount.put(rs.getString("month")+"_"+rs.getString("year"), hmInner);
				}
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmEarningAmount.keySet().iterator();
			StringBuilder sbCategory = new StringBuilder("[");
			StringBuilder sbSalary = new StringBuilder("[");
			int i = 0;
			while(it.hasNext()){
				String key = it.next();
				Map<String, String> hmEarning = hmEarningAmount.get(key);
				if(hmEarning == null) hmEarning = new HashMap<String, String>(); 
				Map<String, String> hmDeduction = hmDeductionAmount.get(key);
				if(hmDeduction == null) hmDeduction = new HashMap<String, String>();
				
				String strMonthYear = uF.getShortMonth(uF.parseToInt(hmEarning.get("MONTH")))+" "+hmEarning.get("YEAR");
				double dblNetSalary = uF.parseToDouble(hmEarning.get("AMOUNT")) - uF.parseToDouble(hmDeduction.get("AMOUNT"));
				
				sbCategory.append("'"+strMonthYear+"'");
				sbSalary.append(uF.formatIntoTwoDecimalWithOutComma(dblNetSalary));
				if(i < hmEarningAmount.size()-1){
					sbCategory.append(",");
					sbSalary.append(",");
				}
				i++;				
			}
			sbCategory.append("]");
			sbSalary.append("]");
			
			request.setAttribute("sbCategory",sbCategory.toString());
			request.setAttribute("sbSalary",sbSalary.toString());
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
public void getTeamTravelRequests(Map<String, String> hmEmployeeMap,Map<String, String> hmLevelMap) {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			String strUserType = (String) session.getAttribute(USERTYPE);
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strYesterday = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DBDATE);
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if (hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
				"and effective_type='"+WORK_FLOW_TRAVEL+"' and effective_id in (select leave_id from emp_leave_entry where " +
				"leave_type_id="+TRAVEL_LEAVE+" and encashment_status=false and is_approved=0) group by effective_id"); //and entrydate >=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()){
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select a.effective_id,a.member_position,w.user_type_id from (select effective_id,min(member_position)as member_position " +
					"from work_flow_details wfd where emp_id=? and is_approved=0 and effective_type='"+WORK_FLOW_TRAVEL+"' " +
					"and effective_id in (select leave_id from emp_leave_entry where leave_type_id="+TRAVEL_LEAVE+" and encashment_status=false " +
					" and is_approved=0) and user_type_id > 0 group by effective_id) a, work_flow_details w where a.effective_id=w.effective_id " + //and entrydate >=?
					"and a.member_position=w.member_position and w.emp_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 1));
			pst.setInt(2, uF.parseToInt(strEmpId));
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			Map<String, String> hmMemNextApprovalUserTypeId = new HashMap<String, String>();
			while(rs.next()){
				hmMemNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
				hmMemNextApprovalUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_TRAVEL+"' " +
				"and effective_id in (select leave_id from emp_leave_entry where leave_type_id="+TRAVEL_LEAVE+"" +
				" and encashment_status=false and is_approved=-1) group by effective_id"); //and entrydate >=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("effective_id"))){
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();			
			
			pst = con.prepareStatement("select leave_id from emp_leave_entry where leave_type_id="+TRAVEL_LEAVE+" and is_approved=-1"); // and entrydate >=?
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("leave_id"))){
					deniedList.add(rs.getString("leave_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_TRAVEL+"'" +
				" and effective_id in (select leave_id from emp_leave_entry where leave_type_id="+TRAVEL_LEAVE+"" +
				" and encashment_status=false and is_approved=0) order by effective_id,member_position"); //and entrydate >=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
			while(rs.next()){
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select * from emp_leave_entry where leave_type_id="+TRAVEL_LEAVE+" and is_approved=0 and encashment_status=false order by entrydate desc"); //and entrydate >=? 
			pst=con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
//			System.out.println("pst====>"+pst);
			rs=pst.executeQuery();	
			List<String> alTravelRequest = new ArrayList<String>();
			int intTravelReqCnt=0;
			while(rs.next()) {
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("leave_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				if(!checkEmpList.contains(strEmpId) && !strUserType.equalsIgnoreCase(ADMIN)) {
					continue;
				}
				
				if(uF.parseToInt(hmNextApproval.get(rs.getString("leave_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("leave_id"))) && uF.parseToInt(hmNextApproval.get(rs.getString("leave_id")))>0){
					String strDate = rs.getString("entrydate");
					if(strDate!=null && strDate.equals(strToday)){
						strDate = ", <span>today</span>";
					}else if(strDate!=null && strDate.equals(strYesterday)){
						strDate = ", <span>yesterday</span>";
					}else {
						strDate = " on "+ uF.getDateFormat(rs.getString("entrydate"), DBDATE, "dd MMM");
						strDate = "<span>"+strDate.toLowerCase()+"</span>";
					}
					String strApproveUser = hmMemNextApprovalUserTypeId.get(rs.getString("leave_id")) !=null ? "["+uF.showData(hmUserTypeMap.get(hmMemNextApprovalUserTypeId.get(rs.getString("leave_id"))), "")+"]" : ""; 
					
					if(alTravelRequest.size()<=10) {
						// TODO : Start Dattatray
						if (uF.parseToInt(rs.getString("leave_type_id")) == ON_DUTY) {
							// TODO : Start Dattatray
							alTravelRequest.add("<span style=\"width: 90%; float: left;\">"+uF.showData(hmEmployeeMap.get(rs.getString("emp_id")), "")+", has applied On Duty from "+uF.getDateFormat(rs.getString("approval_from"), DBDATE, "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, "dd MMM")+strDate+" "+strApproveUser+"</span>"+
									"<span style=\"float: right;\"><a href=\"javascript:void(0);\" onclick=\"approveDeny('1','" + rs.getString("leave_id") + "','" + hmMemNextApprovalUserTypeId.get(rs.getString("leave_id"))
									+ "');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve On Duty\"></i></a> "
									+ "&nbsp;<a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','" + rs.getString("leave_id") + "','" + hmMemNextApprovalUserTypeId.get(rs.getString("leave_id"))
									+ "','"+rs.getString("emp_id")+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Deny OD\"></i></a></span> ");
							// TODO : End Dattatray
						}else{
							alTravelRequest.add("<span style=\"width: 90%; float: left;\">"+uF.showData(hmEmployeeMap.get(rs.getString("emp_id")), "")+", has applied travel from "+uF.getDateFormat(rs.getString("approval_from"), DBDATE, "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, "dd MMM")+strDate+" "+strApproveUser+"</span>"+
									/*"<span style=\"float: right;\"><a href=\"javascript:void(0);\" onclick=\"approveDenyTravel('"+rs.getString("leave_id")+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("leave_id"))+"','"+uF.showData(hmEmployeeMap.get(rs.getString("emp_id")), "")+"'); \"><img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" /></a></span>");*/
									"<span style=\"float: right;\"><a href=\"javascript:void(0);\" onclick=\"approveDenyTravel('"+rs.getString("leave_id")+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("leave_id"))+"','"+uF.showData(hmEmployeeMap.get(rs.getString("emp_id")), "")+"'); \"><i class=\"fa fa-circle\" title=\"click to approve/deny\" aria-hidden=\"true\" style=\"color:#b71cc5\"></i></a></span>");
						}
						// TODO : End Dattatray
					}
				}
				intTravelReqCnt++;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alTravelRequest",alTravelRequest);
			request.setAttribute("TRAVEL_REQUEST_COUNT", intTravelReqCnt+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getTeamPerkRequests(Map<String, String> hmEmployeeMap) {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			String strUserType = (String) session.getAttribute(USERTYPE);
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strYesterday = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DBDATE);
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if (hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmPerkMap = CF.getPerkMap(con);
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
				"and effective_type='"+WORK_FLOW_PERK+"' and effective_id in (select perks_id from emp_perks where approval_1=0)" + //entry_date >=?
				" group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()){
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select a.effective_id,a.member_position,w.user_type_id from (select effective_id,min(member_position)as member_position " +
				"from work_flow_details wfd where emp_id=? and is_approved=0 and effective_type='"+WORK_FLOW_PERK+"' " +
				"and effective_id in (select perks_id from emp_perks where approval_1=0) and user_type_id > 0 group by effective_id) a, " + //entry_date >=?
				"work_flow_details w where a.effective_id=w.effective_id and a.member_position=w.member_position and w.emp_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 1));
			pst.setInt(2, uF.parseToInt(strEmpId));	
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			Map<String, String> hmMemNextApprovalUserTypeId = new HashMap<String, String>();
			while(rs.next()){
				hmMemNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
				hmMemNextApprovalUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_PERK+"' " +
					"and effective_id in (select perks_id from emp_perks where approval_1=-1) group by effective_id"); //entry_date >=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("effective_id"))){
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();			
			
			pst = con.prepareStatement("select perks_id from emp_perks where approval_1=-1 and approval_2=-1"); // and entry_date >=?
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("perks_id"))){
					deniedList.add(rs.getString("perks_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_PERK+"'" +
				" and effective_id in (select perks_id from emp_perks where approval_1=0 and approval_2=0 ) order by effective_id,member_position"); //and entry_date >=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
			while(rs.next()){
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select * from emp_perks where approval_1=0 and approval_2=0 order by entry_date desc"); //and entry_date >=?
			pst=con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
	//		System.out.println("pst====>"+pst);
			rs=pst.executeQuery();	
			List<String> alPerkRequest = new ArrayList<String>();
			int intPerkReqCnt=0;
			while(rs.next()){
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("perks_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				if(!checkEmpList.contains(strEmpId) && !strUserType.equalsIgnoreCase(ADMIN)){
					continue;
				}
				
				if(uF.parseToInt(hmNextApproval.get(rs.getString("perks_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("perks_id"))) && uF.parseToInt(hmNextApproval.get(rs.getString("perks_id")))>0){
					String strDate = rs.getString("entry_date");
					if(strDate!=null && strDate.equals(strToday)){
						strDate = ", <span>today</span>";
					}else if(strDate!=null && strDate.equals(strYesterday)){
						strDate = ", <span>yesterday</span>";
					}else {
						strDate = " on "+ uF.getDateFormat(rs.getString("entry_date"), DBDATE, "dd MMM");
						strDate = "<span>"+strDate.toLowerCase()+"</span>";
					}
					String strApproveUser = hmMemNextApprovalUserTypeId.get(rs.getString("perks_id")) !=null ? "["+uF.showData(hmUserTypeMap.get(hmMemNextApprovalUserTypeId.get(rs.getString("perks_id"))), "")+"]" : "";
					
					String strCurrId = hmEmpCurrency.get(rs.getString("emp_id"));
					Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
					if(hmCurrencyInner==null)hmCurrencyInner=new HashMap<String, String>();
					String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
					
					if(alPerkRequest.size()<=10) {
						alPerkRequest.add("<span style=\"width: 90%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has submitted a request for perks for "+uF.showData(hmPerkMap.get(rs.getString("perk_type_id")), "N/A")
							+" for "+ strCurrSymbol+rs.getString("perk_amount")+" specifying "+"\""+rs.getString("perk_purpose")+"\""+strDate+" "+strApproveUser+"</span>"+
							"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"approveDenyPerk('1','"+rs.getString("perks_id")+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("perks_id"))+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" title=\"Approved\"></i></a> " +
							" <a href=\"javascript:void(0);\" onclick=\"approveDenyPerk('-1','"+rs.getString("perks_id")+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("perks_id"))+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i></a></span>");
					}
					intPerkReqCnt++;
				}
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alPerkRequest",alPerkRequest);
			request.setAttribute("PERK_REQUEST_COUNT", intPerkReqCnt+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void getTeamLeaveEncashRequests(Map<String, String> hmEmployeeMap) {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			String strUserType = (String) session.getAttribute(USERTYPE);
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strYesterday = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DBDATE);
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if (hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
				"and effective_type='"+WORK_FLOW_LEAVE_ENCASH+"' and effective_id in (select leave_encash_id from emp_leave_encashment where is_approved=0)" + //entry_date >=?
				" group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()){
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select a.effective_id,a.member_position,w.user_type_id from (select effective_id,min(member_position)as member_position " +
				"from work_flow_details wfd where emp_id=? and is_approved=0 and effective_type='"+WORK_FLOW_LEAVE_ENCASH+"' " +
				"and effective_id in (select leave_encash_id from emp_leave_encashment where is_approved=0) and user_type_id > 0 group by effective_id) a, " + // entry_date >=?
				"work_flow_details w where a.effective_id=w.effective_id and a.member_position=w.member_position and w.emp_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			pst.setInt(2, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			Map<String, String> hmMemNextApprovalUserTypeId = new HashMap<String, String>();
			while(rs.next()){
				hmMemNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
				hmMemNextApprovalUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_LEAVE_ENCASH+"' " +
				"and effective_id in (select leave_encash_id from emp_leave_encashment where is_approved=-1) group by effective_id"); //entry_date >=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("effective_id"))){
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();			
			
			pst = con.prepareStatement("select leave_encash_id from emp_leave_encashment where is_approved=-1"); // and entry_date >=?
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("leave_encash_id"))){
					deniedList.add(rs.getString("leave_encash_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_LEAVE_ENCASH+"'" +
				" and effective_id in (select leave_encash_id from emp_leave_encashment where is_approved=0 ) order by effective_id,member_position"); //entry_date >=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
			while(rs.next()){
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select * from emp_leave_encashment where is_approved=0 order by entry_date desc"); // and entry_date >=? 
			pst=con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
	//		System.out.println("pst====>"+pst);
			rs=pst.executeQuery();	
			List<String> alLeaveEncashRequest = new ArrayList<String>();
			int intLeaveEncashReqCnt=0;
			while(rs.next()){
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("leave_encash_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				if(!checkEmpList.contains(strEmpId) && !strUserType.equalsIgnoreCase(ADMIN)){
					continue;
				}
				
				if(uF.parseToInt(hmNextApproval.get(rs.getString("leave_encash_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("leave_encash_id"))) && uF.parseToInt(hmNextApproval.get(rs.getString("leave_encash_id")))>0){
					String strDate = rs.getString("entry_date");
					if(strDate!=null && strDate.equals(strToday)){
						strDate = ", <span>today</span>";
					}else if(strDate!=null && strDate.equals(strYesterday)){
						strDate = ", <span>yesterday</span>";
					}else {
						strDate = " on "+ uF.getDateFormat(rs.getString("entry_date"), DBDATE, "dd MMM");
						strDate = "<span>"+strDate.toLowerCase()+"</span>";
					}
					String strApproveUser = hmMemNextApprovalUserTypeId.get(rs.getString("leave_encash_id")) !=null ? "["+uF.showData(hmUserTypeMap.get(hmMemNextApprovalUserTypeId.get(rs.getString("leave_encash_id"))), "")+"]" : "";
					
					if(alLeaveEncashRequest.size()<=10) {
						alLeaveEncashRequest.add("<span style=\"width: 90%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has submitted a request for leave encashment for "+rs.getString("no_days")+
							" days specifying "+"\""+rs.getString("encash_reason")+"\""+strDate+" "+strApproveUser+"</span>"+
	//						"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"approveDenyLeaveEncash('1','"+rs.getString("leave_encash_id")+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("leave_encash_id"))+"');\"><img title=\"Approved\" src=\"images1/icons/icons/approve_icon.png\" border=\"0\" /></a> " +
							"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to approve this request?')) getApprovalEncashment('1','"+rs.getString("leave_encash_id")+"','"+uF.showData(hmEmployeeMap.get(rs.getString("emp_id")), "")+"','"+rs.getString("emp_id")+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("leave_encash_id"))+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
							" <a href=\"javascript:void(0);\" onclick=\"approveDenyLeaveEncash('-1','"+rs.getString("leave_encash_id")+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("leave_encash_id"))+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a></span>");
					}
					intLeaveEncashReqCnt++;
				}
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alLeaveEncashRequest",alLeaveEncashRequest);
			request.setAttribute("LEAVE_ENCASH_REQUEST_COUNT", intLeaveEncashReqCnt+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void getTeamLTARequests(Map<String, String> hmEmployeeMap) {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			String strUserType = (String) session.getAttribute(USERTYPE);
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strYesterday = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DBDATE);
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if (hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
//			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con);
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
				"and effective_type='"+WORK_FLOW_LTA+"' and effective_id in (select emp_lta_id from emp_lta_details where is_approved=0)" + //entry_date >=?
				" group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()){
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select a.effective_id,a.member_position,w.user_type_id from (select effective_id,min(member_position)as member_position " +
				"from work_flow_details wfd where emp_id=? and is_approved=0 and effective_type='"+WORK_FLOW_LTA+"' " +
				"and effective_id in (select emp_lta_id from emp_lta_details where is_approved=0) and user_type_id > 0 group by effective_id) a, " + //entry_date >=?
				"work_flow_details w where a.effective_id=w.effective_id and a.member_position=w.member_position and w.emp_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			pst.setInt(2, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			Map<String, String> hmMemNextApprovalUserTypeId = new HashMap<String, String>();
			while(rs.next()){
				hmMemNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
				hmMemNextApprovalUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_LTA+"' " +
				"and effective_id in (select emp_lta_id from emp_lta_details where is_approved=-1) group by effective_id"); //entry_date >=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("effective_id"))){
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();			
			
			pst = con.prepareStatement("select emp_lta_id from emp_lta_details where is_approved=-1"); // and entry_date >=?
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("emp_lta_id"))){
					deniedList.add(rs.getString("emp_lta_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_LTA+"'" +
				" and effective_id in (select emp_lta_id from emp_lta_details where is_approved=0) order by effective_id,member_position"); //entry_date >=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
			while(rs.next()){
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select * from emp_lta_details where is_approved=0 order by entry_date desc"); //and entry_date >=? 
			pst=con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
	//		System.out.println("pst====>"+pst);
			rs=pst.executeQuery();	
			List<String> alLtaRequest = new ArrayList<String>();
			int intLtaReqCnt=0;
			while(rs.next()){
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("emp_lta_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				if(!checkEmpList.contains(strEmpId) && !strUserType.equalsIgnoreCase(ADMIN)){
					continue;
				}
				
				if(uF.parseToInt(hmNextApproval.get(rs.getString("emp_lta_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("emp_lta_id"))) && uF.parseToInt(hmNextApproval.get(rs.getString("emp_lta_id")))>0){
					String strDate = rs.getString("entry_date");
					if(strDate!=null && strDate.equals(strToday)){
						strDate = ", <span>today</span>";
					}else if(strDate!=null && strDate.equals(strYesterday)){
						strDate = ", <span>yesterday</span>";
					}else {
						strDate = " on "+ uF.getDateFormat(rs.getString("entry_date"), DBDATE, "dd MMM");
						strDate = "<span>"+strDate.toLowerCase()+"</span>";
					}
					String strApproveUser = hmMemNextApprovalUserTypeId.get(rs.getString("emp_lta_id")) !=null ? "["+uF.showData(hmUserTypeMap.get(hmMemNextApprovalUserTypeId.get(rs.getString("emp_lta_id"))), "")+"]" : "";
					
					String strCurrId = hmEmpCurrency.get(rs.getString("emp_id"));
					Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
					if(hmCurrencyInner==null)hmCurrencyInner=new HashMap<String, String>();
					String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
					
					int nEmpLevelId = CF.getEmpLevelId(rs.getString("emp_id"), request);
					Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con, nEmpLevelId);
					
					if(alLtaRequest.size()<=10) {
						alLtaRequest.add("<span style=\"width: 90%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has submitted a request for "+uF.showData(hmSalaryHeadsMap.get(rs.getString("salary_head_id")), "")
							+" for "+ strCurrSymbol+rs.getString("applied_amount")+" specifying "+"\""+rs.getString("lta_purpose")+"\""+strDate+" "+strApproveUser+"</span>"+
							"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"approveDenyLTA('1','"+rs.getString("emp_lta_id")+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("emp_lta_id"))+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
							" <a href=\"javascript:void(0);\" onclick=\"approveDenyLTA('-1','"+rs.getString("emp_lta_id")+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("emp_lta_id"))+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a></span>");
					}
					intLtaReqCnt++;
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alLtaRequest", alLtaRequest);
			request.setAttribute("LTA_REQUEST_COUNT", intLtaReqCnt+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void getTeamLoanRequests(Map<String, String> hmEmployeeMap) {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			String strUserType = (String) session.getAttribute(USERTYPE);
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strYesterday = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DBDATE);
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if (hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
					"and effective_type='"+WORK_FLOW_LOAN+"' and effective_id in (select loan_applied_id from loan_details ld, " +
					"loan_applied_details lad where lad.loan_id=ld.loan_id and is_approved=0) group by effective_id"); //and applied_date >=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()){
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select a.effective_id,a.member_position,w.user_type_id from (select effective_id,min(member_position)as member_position " +
					"from work_flow_details wfd where emp_id=? and is_approved=0 and effective_type='"+WORK_FLOW_LOAN+"' " +
					"and effective_id in (select loan_applied_id from loan_details ld, loan_applied_details lad where " +
					"lad.loan_id=ld.loan_id and is_approved=0) and user_type_id > 0 group by effective_id) a, " + //and applied_date >=?
					"work_flow_details w where a.effective_id=w.effective_id and a.member_position=w.member_position and w.emp_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			pst.setInt(2, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			Map<String, String> hmMemNextApprovalUserTypeId = new HashMap<String, String>();
			while(rs.next()){
				hmMemNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
				hmMemNextApprovalUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_LOAN+"' " +
					"and effective_id in (select loan_applied_id from loan_details ld, loan_applied_details lad where lad.loan_id=ld.loan_id " +
					" and is_approved=-1) group by effective_id"); //and applied_date >=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("effective_id"))){
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();			
			
			pst = con.prepareStatement("select loan_applied_id from loan_details ld, loan_applied_details lad where lad.is_approved=-1 " +
					"and lad.loan_id=ld.loan_id and is_approved=-1"); //and applied_date >=?
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("loan_applied_id"))){
					deniedList.add(rs.getString("loan_applied_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_LOAN+"'" +
					" and effective_id in (select loan_applied_id from loan_details ld, loan_applied_details lad where lad.loan_id=ld.loan_id " +
					" and is_approved=0) order by effective_id,member_position"); //and applied_date >=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
			while(rs.next()){
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select * from loan_details ld, loan_applied_details lad where lad.loan_id=ld.loan_id " +
					" and lad.is_approved=0 order by applied_date desc"); //and applied_date >=?
			pst=con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
	//		System.out.println("pst====>"+pst);
			rs=pst.executeQuery();	
			List<String> alLoanRequest = new ArrayList<String>();
			int intLoanReqCnt=0; 
			while(rs.next()) {
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("loan_applied_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				if(!checkEmpList.contains(strEmpId) && !strUserType.equalsIgnoreCase(ADMIN)){
					continue;
				}
				
				if(uF.parseToInt(hmNextApproval.get(rs.getString("loan_applied_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("loan_applied_id"))) && uF.parseToInt(hmNextApproval.get(rs.getString("loan_applied_id")))>0){
					String strDate = rs.getString("applied_date");
					if(strDate!=null && strDate.equals(strToday)){
						strDate = ", <span>today</span>";
					}else if(strDate!=null && strDate.equals(strYesterday)){
						strDate = ", <span>yesterday</span>";
					}else {
						strDate = " on "+ uF.getDateFormat(rs.getString("applied_date"), DBDATE, "dd MMM");
						strDate = "<span>"+strDate.toLowerCase()+"</span>";
					}
					String strApproveUser = hmMemNextApprovalUserTypeId.get(rs.getString("loan_applied_id")) !=null ? "["+uF.showData(hmUserTypeMap.get(hmMemNextApprovalUserTypeId.get(rs.getString("loan_applied_id"))), "")+"]" : "";
					
					String strCurrId = hmEmpCurrency.get(rs.getString("emp_id"));
					Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
					if(hmCurrencyInner==null)hmCurrencyInner=new HashMap<String, String>();
					String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
					
					if(alLoanRequest.size()<=10) {
						alLoanRequest.add("<span style=\"width: 90%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has submitted a loan request "
							+" for "+ strCurrSymbol+rs.getString("amount_paid")+" specifying "+"\""+rs.getString("loan_desc")+"\""+strDate+" "+strApproveUser+"</span>"+
							"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"approveDenyLoan('1','"+rs.getString("loan_applied_id")+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("loan_applied_id"))+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
							" <a href=\"javascript:void(0);\" onclick=\"approveDenyLoan('-1','"+rs.getString("loan_applied_id")+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("loan_applied_id"))+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a></span>");
					}
					intLoanReqCnt++;
				}
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alLoanRequest", alLoanRequest);
			request.setAttribute("LOAN_REQUEST_COUNT", intLoanReqCnt+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void getTeamResignRequests(Map<String, String> hmEmployeeMap) {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			String strUserType = (String) session.getAttribute(USERTYPE);
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strYesterday = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DBDATE);
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if (hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
				"and effective_type='"+WORK_FLOW_RESIGN+"' and effective_id in (select off_board_id from emp_off_board where approved_1=0) group by effective_id"); //entry_date >=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()){
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select a.effective_id,a.member_position,w.user_type_id from (select effective_id,min(member_position)as member_position " +
				"from work_flow_details wfd where emp_id=? and is_approved=0 and effective_type='"+WORK_FLOW_RESIGN+"' " +
				"and effective_id in (select off_board_id from emp_off_board where approved_1=0) and user_type_id > 0 " + // entry_date >=?
				"group by effective_id) a, work_flow_details w where a.effective_id=w.effective_id and a.member_position=w.member_position " +
				"and w.emp_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 1));
			pst.setInt(2, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			Map<String, String> hmMemNextApprovalUserTypeId = new HashMap<String, String>();
			while(rs.next()){
				hmMemNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
				hmMemNextApprovalUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_RESIGN+"' " +
				"and effective_id in (select off_board_id from emp_off_board where approved_1=-1) group by effective_id"); //entry_date >=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("effective_id"))){
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();			
			
			pst = con.prepareStatement("select off_board_id from emp_off_board where approved_1=-1 and approved_2=-1"); //entry_date >=? and 
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("off_board_id"))){
					deniedList.add(rs.getString("off_board_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_RESIGN+"'" +
				" and effective_id in (select off_board_id from emp_off_board where approved_1=0) order by effective_id,member_position"); //entry_date >=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
			while(rs.next()){
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select * from emp_off_board where (approved_1 is null or approved_1 =0) and (approved_2 is null or approved_2=0)" +
					" and off_board_type != '"+TERMINATED+"' order by entry_date desc"); // and entry_date >=?
			pst=con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs=pst.executeQuery();	
			List<String> alResignRequest = new ArrayList<String>();
			int intResignReqCnt=0;
			while(rs.next()){
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("off_board_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				if(!checkEmpList.contains(strEmpId) && !strUserType.equalsIgnoreCase(ADMIN)){
					continue;
				}
				
				if(uF.parseToInt(hmNextApproval.get(rs.getString("off_board_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("off_board_id"))) && uF.parseToInt(hmNextApproval.get(rs.getString("off_board_id")))>0){
					String strDate = rs.getString("entry_date");
					if(strDate!=null && strDate.equals(strToday)){
						strDate = ", <span>today</span>";
					}else if(strDate!=null && strDate.equals(strYesterday)){
						strDate = ", <span>yesterday</span>";
					}else {
						strDate = " on "+ uF.getDateFormat(rs.getString("entry_date"), DBDATE, "dd MMM");
						strDate = "<span>"+strDate.toLowerCase()+"</span>";
					}
					String strApproveUser = hmMemNextApprovalUserTypeId.get(rs.getString("off_board_id")) !=null ? "["+uF.showData(hmUserTypeMap.get(hmMemNextApprovalUserTypeId.get(rs.getString("off_board_id"))), "")+"]" : "";
					
					if(alResignRequest.size()<=10) {
						alResignRequest.add("<span style=\"width: 90%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has submitted a resign request "
							+"specifying "+"\""+rs.getString("emp_reason")+"\""+strDate+" "+strApproveUser+"</span>"+
							"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"approveDenyResign('1','1','"+rs.getString("off_board_id")+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("off_board_id"))+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" title=\"Approved\" ></i></i></a> " +
							" <a href=\"javascript:void(0);\" onclick=\"approveDenyResign('-1','1','"+rs.getString("off_board_id")+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("off_board_id"))+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a></span>");
					}
					intResignReqCnt++;
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alResignRequest",alResignRequest);
			request.setAttribute("RESIGN_REQUEST_COUNT", intResignReqCnt+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void getTeamRequisitionRequests(Map<String, String> hmEmployeeMap) {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			String strUserType = (String) session.getAttribute(USERTYPE);
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strYesterday = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DBDATE);
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if (hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
				"and effective_type='"+WORK_FLOW_REQUISITION+"' and effective_id in (select requisition_id from requisition_details where is_approved=0) group by effective_id"); //requisition_date>=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()){
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select a.effective_id,a.member_position,w.user_type_id from (select effective_id,min(member_position)as member_position " +
				"from work_flow_details wfd where emp_id=? and is_approved=0 and effective_type='"+WORK_FLOW_REQUISITION+"' " +
				"and effective_id in (select requisition_id from requisition_details where is_approved=0) and user_type_id > 0 " + //requisition_date>=?
				"group by effective_id) a, work_flow_details w where a.effective_id=w.effective_id and a.member_position=w.member_position " +
				"and w.emp_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			pst.setInt(2, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			Map<String, String> hmMemNextApprovalUserTypeId = new HashMap<String, String>();
			while(rs.next()){
				hmMemNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
				hmMemNextApprovalUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_REQUISITION+"' " +
				"and effective_id in (select requisition_id from requisition_details where is_approved=-1) group by effective_id"); //requisition_date>=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("effective_id"))){
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();			
			
			pst = con.prepareStatement("select requisition_id from requisition_details where is_approved=-1"); // and requisition_date>=?
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("off_board_id"))){
					deniedList.add(rs.getString("off_board_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_REQUISITION+"'" +
				" and effective_id in (select requisition_id from requisition_details where is_approved=0) order by effective_id,member_position"); //requisition_date>=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
			while(rs.next()){
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from requisition_details where is_approved=0 order by requisition_date desc"); //and requisition_date>=? 
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
	//		System.out.println("pst====>"+pst);
			rs = pst.executeQuery();	
			List<String> alRequisitionRequest = new ArrayList<String>();
			int intRequisitionReqCnt=0;
			while(rs.next()){
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("requisition_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				if(!checkEmpList.contains(strEmpId) && !strUserType.equalsIgnoreCase(ADMIN)){
					continue;
				}
				
				if(uF.parseToInt(hmNextApproval.get(rs.getString("requisition_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("requisition_id"))) && uF.parseToInt(hmNextApproval.get(rs.getString("requisition_id")))>0){
					String strDate = rs.getString("requisition_date");
					if(strDate!=null && strDate.equals(strToday)){
						strDate = ", <span>today</span>";
					}else if(strDate!=null && strDate.equals(strYesterday)){
						strDate = ", <span>yesterday</span>";
					}else {
						strDate = " on "+ uF.getDateFormat(rs.getString("requisition_date"), DBDATE, "dd MMM");
						strDate = "<span>"+strDate.toLowerCase()+"</span>";
					}
					String strApproveUser = hmMemNextApprovalUserTypeId.get(rs.getString("requisition_id")) !=null ? "["+uF.showData(hmUserTypeMap.get(hmMemNextApprovalUserTypeId.get(rs.getString("requisition_id"))), "")+"]" : "";
					
					if(alRequisitionRequest.size()<=10) {
						alRequisitionRequest.add("<span style=\"width: 90%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has submitted a requisition request "
							+"specifying "+"\""+rs.getString("purpose")+"\""+strDate+" "+strApproveUser+"</span>"+
							"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"approveDenyRequisition('1','"+rs.getString("requisition_id")+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("requisition_id"))+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
							" <a href=\"javascript:void(0);\" onclick=\"approveDenyRequisition('-1','"+rs.getString("requisition_id")+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("requisition_id"))+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a></span>");
					}
					intRequisitionReqCnt++;
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alRequisitionRequest", alRequisitionRequest);
			request.setAttribute("REQUISITION_REQUEST_COUNT", intRequisitionReqCnt+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	public void getTeamSelfReviewRequests(Map<String, String> hmEmployeeMap) {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			String strUserType = (String) session.getAttribute(USERTYPE);
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strYesterday = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DBDATE);
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if (hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
			" and effective_type='"+WORK_FLOW_SELF_REVIEW+"' and effective_id in (select appraisal_details_id from appraisal_details a," +
			" appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id and (adf.is_delete is null or adf.is_delete = false)"
			+"  and my_review_status = 1) group by effective_id"); // and a.entry_date>?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()){
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select a.effective_id,a.member_position,w.user_type_id from (select effective_id,min(member_position)as member_position " +
				"from work_flow_details wfd where emp_id=? and is_approved=0 and effective_type='"+WORK_FLOW_SELF_REVIEW+"' " +
				"and effective_id in (select appraisal_details_id from appraisal_details a, appraisal_details_frequency adf "+
				" where a.appraisal_details_id = adf.appraisal_id and (adf.is_delete is null or adf.is_delete = false) " +
				"and my_review_status = 1  and (adf.is_appraisal_publish is null or adf.is_appraisal_publish=false)) " + //and a.entry_date>= ?
				"and user_type_id > 0 group by effective_id) a, work_flow_details w where a.effective_id=w.effective_id " +
				"and a.member_position=w.member_position and w.emp_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 1));
			pst.setInt(2, uF.parseToInt(strEmpId));	
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			Map<String, String> hmMemNextApprovalUserTypeId = new HashMap<String, String>();
			while(rs.next()){
				hmMemNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
				hmMemNextApprovalUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_SELF_REVIEW+"' " +
				"and effective_id in (select appraisal_details_id from appraisal_details a, appraisal_details_frequency adf "+
				" where a.appraisal_details_id = adf.appraisal_id and (adf.is_delete is null or adf.is_delete = false) and my_review_status = 1) " +  //and a.entry_date>= ?
				"group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("effective_id"))){
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();			
			
			pst = con.prepareStatement("select appraisal_details_id from appraisal_details  a, appraisal_details_frequency adf "+
				" where a.appraisal_details_id = adf.appraisal_id and (adf.is_delete is null or adf.is_delete = false) and " +
				" my_review_status = 1 and publish_is_approved=-1"); // and a.entry_date>=?
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("appraisal_details_id"))){
					deniedList.add(rs.getString("appraisal_details_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_SELF_REVIEW+"'" +
				" and effective_id in (select appraisal_details_id from appraisal_details  a, appraisal_details_frequency adf  "+
				" where a.appraisal_details_id = adf.appraisal_id and (adf.is_delete is null or adf.is_delete = false) and " +
				"  my_review_status = 1) order by effective_id,member_position"); // and a.entry_date>=?
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			rs = pst.executeQuery();			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
			while(rs.next()){
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select * from appraisal_details a, appraisal_details_frequency adf "+
				" where a.appraisal_details_id = adf.appraisal_id and (adf.is_delete is null or adf.is_delete = false) and " +
				"  my_review_status = 1 and publish_is_approved=0 order by a.entry_date desc"); //and a.entry_date>=? 
			pst=con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
	//		System.out.println("pst====>"+pst);
			rs=pst.executeQuery();	
			List<String> alSelfReviewRequest = new ArrayList<String>();
			int intSelfReviewReqCnt=0;
			while(rs.next()){
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("appraisal_details_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				if(!checkEmpList.contains(strEmpId) && !strUserType.equalsIgnoreCase(ADMIN)){
					continue;
				}
				
				if(uF.parseToInt(hmNextApproval.get(rs.getString("appraisal_details_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("appraisal_details_id"))) && uF.parseToInt(hmNextApproval.get(rs.getString("appraisal_details_id")))>0){
					String strDate = rs.getString("entry_date");
					if(strDate!=null && strDate.equals(strToday)) {
						strDate = ", <span>today</span>";
					} else if(strDate!=null && strDate.equals(strYesterday)) {
						strDate = ", <span>yesterday</span>";
					} else {
						strDate = " on "+ uF.getDateFormat(rs.getString("entry_date"), DBDATE, "dd MMM");
						strDate = "<span>"+strDate.toLowerCase()+"</span>";
					}
					if(alSelfReviewRequest.size()<=10) {
						String strApproveUser = hmMemNextApprovalUserTypeId.get(rs.getString("appraisal_details_id")) !=null ? "["+uF.showData(hmUserTypeMap.get(hmMemNextApprovalUserTypeId.get(rs.getString("appraisal_details_id"))), "")+"]" : "";
						alSelfReviewRequest.add("<span style=\"width: 90%; float: left;\">"+hmEmployeeMap.get(rs.getString("added_by"))+", has submitted a self review request "
							+"specifying "+"\""+rs.getString("appraisal_name")+"\""+strDate+" "+strApproveUser+"</span>"+
							"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"approveDenySelfReviewRequest('1','"+rs.getString("appraisal_details_id")+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("appraisal_details_id"))+"','"+rs.getString("appraisal_freq_id")+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
							" <a href=\"javascript:void(0);\" onclick=\"approveDenySelfReviewRequest('-1','"+rs.getString("appraisal_details_id")+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("appraisal_details_id"))+"','"+rs.getString("appraisal_freq_id")+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a></span>");
					}
					intSelfReviewReqCnt++;
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alSelfReviewRequest", alSelfReviewRequest);
			request.setAttribute("SELF_REVIEW_REQUEST_COUNT", intSelfReviewReqCnt+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
public void getTeamNewJobRequests(Map<String, String> hmEmployeeMap) {
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			String strUserType = (String) session.getAttribute(USERTYPE);
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strYesterday = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DBDATE);
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if (hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
					"and effective_type='"+WORK_FLOW_RECRUITMENT+"' and effective_id in (select recruitment_id from recruitment_details where recruitment_id > 0 ");
				sbQuery.append(" and effective_date >=? and effective_date <=? ");
			sbQuery.append(") group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()){
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select a.effective_id,a.member_position,w.user_type_id from (select effective_id,min(member_position)as member_position " +
				"from work_flow_details wfd where emp_id=? and is_approved=0 and effective_type='"+WORK_FLOW_RECRUITMENT+"' " +
				"and effective_id in (select recruitment_id from recruitment_details where recruitment_id > 0 ");
			sbQuery.append(" and effective_date >=? and effective_date <=?) and user_type_id > 0 group by effective_id) a, work_flow_details w " +
				"where a.effective_id=w.effective_id and a.member_position=w.member_position and w.emp_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			Map<String, String> hmMemNextApprovalUserTypeId = new HashMap<String, String>();
			while(rs.next()){
				hmMemNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
				hmMemNextApprovalUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_RECRUITMENT+"' " +
					"and effective_id in (select recruitment_id from recruitment_details where recruitment_id > 0 ");
				sbQuery.append(" and effective_date >=? and effective_date <=?) " +
					"group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("effective_id"))){
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();			
			
			pst = con.prepareStatement("select recruitment_id from recruitment_details where status=-1 and effective_date >=? and effective_date <=?");
			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("recruitment_id"))){
					deniedList.add(rs.getString("recruitment_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_RECRUITMENT+"'" +
					" and effective_id in (select recruitment_id from recruitment_details where recruitment_id > 0 ");
				sbQuery.append(" and effective_date >=? and effective_date <=?) order by effective_id,member_position");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
			while(rs.next()){
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select d.designation_id, r.priority_job_int,r.status,r.recruitment_id,r.custum_designation,e.emp_fname,e.emp_mname,e.emp_lname,"
					+ "w.wlocation_id,w.wlocation_name,r.entry_date,r.no_position,r.target_deadline,r.comments,existing_emp_count,"
					+ "d.designation_name,r.dept_id,r.added_by,r.req_form_type,r.hiring_manager,r.effective_date from recruitment_details r join work_location_info w on(r.wlocation=w.wlocation_id) "
					+ "join employee_personal_details e on (r.added_by=e.emp_per_id) left join designation_details d using(designation_id) "
					+ "where recruitment_id>0 and requirement_status = 'generate' and r.status = 0 and effective_date >=? and effective_date <=?");
			sbQuery.append(" order by r.status desc,r.recruitment_id desc");
			
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 1));	
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst==>"+pst);
			rs=pst.executeQuery();	
			List<String> alNewJobRequest = new ArrayList<String>();
			while(rs.next()){
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("recruitment_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				if(!checkEmpList.contains(strEmpId) && !strUserType.equalsIgnoreCase(ADMIN)){
					continue;
				}
				
				if(uF.parseToInt(hmNextApproval.get(rs.getString("recruitment_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("recruitment_id"))) && uF.parseToInt(hmNextApproval.get(rs.getString("recruitment_id")))>0) {
					String strDate = rs.getString("effective_date");
					if(strDate!=null && strDate.equals(strToday)){
						strDate = ", <span>today</span>";
					}else if(strDate!=null && strDate.equals(strYesterday)){
						strDate = ", <span>yesterday</span>";
					}else {
						strDate = " on "+ uF.getDateFormat(rs.getString("effective_date"), DBDATE, "dd MMM");
						strDate = "<span>"+strDate.toLowerCase()+"</span>";
					}
					String strApproveUser = hmMemNextApprovalUserTypeId.get(rs.getString("recruitment_id")) !=null ? "["+uF.showData(hmUserTypeMap.get(hmMemNextApprovalUserTypeId.get(rs.getString("recruitment_id"))), "")+"]" : "";
					StringBuilder sbDesig = new StringBuilder(); 
					if (rs.getString("custum_designation") != null && !rs.getString("custum_designation").equals("") && rs.getString("designation_name") == null) {
							sbDesig.append("<b>"+rs.getString("custum_designation")+"</b>");
					} else {
						sbDesig.append("<b>"+rs.getString("designation_name")+"</b>");
					}
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					alNewJobRequest.add("<span style=\"width: 90%; float: left;\">A request for the requirement of <strong>"+rs.getString("no_position")+
							"</strong> resources has been generated by <strong>"+rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname")+"</strong> for "
							+sbDesig+" designation and needs to be accomplished by "+uF.getDateFormat(rs.getString("target_deadline"), DBDATE, CF.getStrReportDateFormat())+"."
							+strApproveUser+"</span>"+
						"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"approveRequest('1','"+rs.getString("recruitment_id")+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("recruitment_id"))+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
						" <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, you want to deny this request?'))denyRequest('-1','"+rs.getString("recruitment_id")+"','"+hmMemNextApprovalUserTypeId.get(rs.getString("recruitment_id"))+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a></span>");
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alNewJobRequest",alNewJobRequest);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public void getMyTeamReviews() {
		PreparedStatement pst = null;
		ResultSet rs = null;
	//	System.out.println("in appraisal .......... ");
		try {
			String strUserTypeID = (String) request.getAttribute(USERTYPEID);
			
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from appraisal_details where is_publish=true and is_close = false and supervisor_id like '%,"+strEmpId+",%' ");
			sbQuery.append("select * from appraisal_details a,appraisal_details_frequency adf  where a.appraisal_details_id = adf.appraisal_id "
					+" and (adf.is_delete is null or adf.is_delete = false )  and is_appraisal_publish = true and appraisal_details_id > 0 "); 
			
			pst = con.prepareStatement(sbQuery.toString());
	//		System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> appraisalDetails = new HashMap<String, Map<String, String>>();
			List<String> appraisalIdList = new ArrayList<String>();
			while (rs.next()) {
				Map<String, String> appraisalMp = new HashMap<String, String>();
				appraisalIdList.add(rs.getString("appraisal_details_id")+"_"+rs.getString("appraisal_freq_id"));
				appraisalMp.put("ID", rs.getString("appraisal_details_id"));
				appraisalMp.put("APPRAISAL", rs.getString("appraisal_name"));
	//			appraisalMp.put("WLOCATION", rs.getString("wlocation_id"));
				appraisalMp.put("SELFID", rs.getString("self_ids"));
				appraisalMp.put("ORIENTED_TYPE", rs.getString("oriented_type"));
	
				appraisalDetails.put(rs.getString("appraisal_details_id")+"_"+rs.getString("appraisal_freq_id"), appraisalMp);
			}
			rs.close();
			pst.close();
	
			Map<String, Map<String, Map<String, String>>> appraisalStatusMp = getEmployeeStatus(con, uF);
			request.setAttribute("appraisalStatusMp", appraisalStatusMp);
			
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
			Map<String, String> hmUserTypeID = CF.getUserTypeIdMap(con);
			
			Map<String, Integer> hmSectionCount = new HashMap<String, Integer>();
	//		int sectionCount = 0;
			pst = con.prepareStatement("select count(main_level_id) as sectionCnt, appraisal_id from appraisal_main_level_details group by appraisal_id");
	//		pst.setInt(1, uF.parseToInt(appraisalIdList.get(i)));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmSectionCount.put(rs.getString("appraisal_id"), rs.getInt("sectionCnt"));
	//			sectionCount = rs.getInt("sectionCnt");
			}
			rs.close();
			pst.close();
			
			Map<String, Integer> hmExistSectionCount = new HashMap<String, Integer>();
			/*pst = con.prepareStatement("select count(distinct section_id) as existSectionCnt,emp_id, appraisal_id from appraisal_question_answer " +
					"where user_id =? and user_type_id = ? group by appraisal_id,emp_id");*/
			pst = con.prepareStatement("select count(distinct section_id) as existSectionCnt,emp_id, appraisal_id, appraisal_freq_id from appraisal_question_answer " +
			"where user_id =? and user_type_id = ? group by appraisal_id,emp_id,appraisal_freq_id");
	//		pst.setInt(1, uF.parseToInt(appraisalIdList.get(i)));
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(hmUserTypeID.get(MANAGER)));
			rs = pst.executeQuery();
	//		System.out.println("pst ::: "+pst);
			while (rs.next()) {
				hmExistSectionCount.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("emp_id"), rs.getInt("existSectionCnt"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> orientationMp = CF.getOrientationValue(con);
			int reviewEmpCount = 0;
			for (int i = 0; i < appraisalIdList.size(); i++) {
				Map<String, String> appraisalMp = appraisalDetails.get(appraisalIdList.get(i));
				
				Map<String, Map<String, String>> userTypeMp = appraisalStatusMp.get(appraisalIdList.get(i));
				if(userTypeMp == null) userTypeMp = new HashMap<String, Map<String, String>>();
				
				String self = appraisalMp.get("SELFID");
				self=self!=null && !self.equals("") ? self.substring(1,self.length()-1) : "";
				int oriented_type = uF.parseToInt(appraisalMp.get("ORIENTED_TYPE"));
				List<String> memberList= CF.getOrientationMemberDetails(con,oriented_type);
	//				System.out.println("strSessionUserType ======= > "+strSessionUserType+"   hmEmployeeIdUserTypeIdMap.get(strSessionEmpId) ========= > "+hmEmployeeIdUserTypeIdMap.get(strSessionEmpId));
//					if(appraisalMp.get("ORIENTED_TYPE")!=null &&  uF.parseToInt(appraisalMp.get("ORIENTED_TYPE"))>1 && uF.parseToInt(appraisalMp.get("ORIENTED_TYPE")) != 5) {
	//					System.out.println("oriented_type in ======= > "+oriented_type);
				if(hmOrientMemberID.get("Manager") != null && memberList.contains(hmOrientMemberID.get("Manager")) ) {
					Map<String, String> empstatusMp = userTypeMp.get(hmOrientMemberID.get("Manager"));
						if(empstatusMp==null)empstatusMp=new HashMap<String, String>();

						pst = con.prepareStatement("select distinct(emp_per_id) from employee_official_details eod,employee_personal_details epd,appraisal_details ad" +
								" where epd.emp_per_id=eod.emp_id  and emp_per_id in("+ self + ") and ad.supervisor_id like '%,"+strEmpId+",%' order by emp_per_id"); //and eod.wlocation_id=? 
						rs = pst.executeQuery();
//						System.out.println("pst manager =====>"+pst);
						while (rs.next()) {
							if (empstatusMp != null && empstatusMp.get(rs.getString("emp_per_id")) == null) {
								reviewEmpCount++;
							} else if(hmExistSectionCount != null && !hmExistSectionCount.isEmpty()) {
								if(hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id")) != null && hmSectionCount.get(appraisalMp.get("ID")) != null && hmSectionCount.get(appraisalMp.get("ID")) != hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id"))) {
									reviewEmpCount++;
								}
							}
						}
						rs.close();
						pst.close();
//-----------------------------------------------------------------------------------
					}
			}
	//		System.out.println("hmRemainOrientDetailsForPeerAppWise ::::: "+hmRemainOrientDetailsForPeerAppWise);
			request.setAttribute("reviewEmpCount", ""+reviewEmpCount);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	private Map<String, String> getOrientMemberID(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> orientationMemberMp = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMemberMp.put(rs.getString("member_name"), rs.getString("member_id"));
			}
			rs.close();
			pst.close();
//			System.out.println("memberid=====>"+orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMemberMp;
	}
	
	
	public Map<String, Map<String, Map<String, String>>> getEmployeeStatus(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, Map<String, Map<String, String>>> appraisalMp = new HashMap<String, Map<String, Map<String, String>>>();
		try {
//			pst = con.prepareStatement("select emp_id,appraisal_id,user_type_id,user_id from appraisal_question_answer group by emp_id,appraisal_id,user_type_id,user_id order by emp_id");
			
			pst = con.prepareStatement("select emp_id,appraisal_id,user_type_id,user_id,appraisal_freq_id from appraisal_question_answer group by emp_id,appraisal_id,"
					+" user_type_id,user_id,appraisal_freq_id order by emp_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, Map<String, String>> userTypeMp = appraisalMp.get(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id"));
				if (userTypeMp == null)userTypeMp = new HashMap<String, Map<String, String>>();
				Map<String, String> empMp = userTypeMp.get(rs.getString("user_type_id"));
				if (empMp == null)empMp = new HashMap<String, String>();
				
				if(uF.parseToInt(rs.getString("user_type_id"))==4 || uF.parseToInt(rs.getString("user_type_id"))==10) {
					empMp.put(rs.getString("emp_id")+"_"+rs.getString("user_id"), rs.getString("emp_id"));
				} else {
					empMp.put(rs.getString("emp_id"), rs.getString("emp_id"));
				}
				userTypeMp.put(rs.getString("user_type_id"), empMp);
//				System.out.println("userTypeMp :: "+userTypeMp);
				appraisalMp.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id"), userTypeMp);
//				System.out.println("appraisalMp :: "+appraisalMp);
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return appraisalMp;
	}
	
	
	public void getInterviews() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
//			System.out.println("hmRecruitWiseRoundId ===> " + hmRecruitWiseRoundId);
			pst = con.prepareStatement("select count(*) as cnt from candidate_interview_panel cip, candidate_application_details cad " +
				" where cip.candidate_id = cad.candidate_id and cip.recruitment_id = cad.recruitment_id and cip.panel_user_id = ? and " +
				" cip.interview_date >= ? and cip.interview_date < ? and status = 0 ");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(3, uF.getFutureDate(CF.getStrTimeZone(), 8));
			rs = pst.executeQuery();
//			System.out.println("pst ===> "+pst);
			int interviewCount = 0;
			while(rs.next()) {
				interviewCount = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("interviewCount", interviewCount+"");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void getMyTeamKRAReview() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			List<String> empList = getEmployeeList(con, uF);	
			StringBuilder sbEmpId = null;
			for(int i=0;empList!=null && !empList.isEmpty() && i<empList.size();i++) {
				if(sbEmpId == null) {
					sbEmpId = new StringBuilder();
					sbEmpId.append(empList.get(i));
				} else {
					sbEmpId.append(","+empList.get(i));
				}
			}
			
			Map<String, String> hmKRATaskStatus = new HashMap<String, String>();
			if(sbEmpId != null) {
				pst = con.prepareStatement("select gksrd.*, gk.goal_id from goal_kra_status_rating_details gksrd, goal_kras gk where " +
					" gksrd.kra_id = gk.goal_kra_id and gksrd.emp_id in ("+sbEmpId.toString()+") and complete_percent >= 100");
				rs=pst.executeQuery();
				while (rs.next()) {
					hmKRATaskStatus.put(rs.getString("emp_id")+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("kra_id"), rs.getString("complete_percent"));
				}
				rs.close();
				pst.close();
			}
			
			int kraFormCount = 0;
			for(int i=0;empList!=null && !empList.isEmpty() && i<empList.size();i++) {
				String goalTyp = INDIVIDUAL_GOAL+","+INDIVIDUAL_KRA+","+INDIVIDUAL_TARGET+","+PERSONAL_GOAL+","+EMPLOYEE_KRA;
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select freq_end_date, freq_start_date, gd.*, gk.kra_description, gk.goal_kra_id, gk.is_assign, gk.kra_weightage, gdf.goal_freq_id, " +
					" gdf.goal_freq_name from goal_details gd left join goal_kras gk on gd.goal_id=gk.goal_id left join goal_details_frequency gdf on gd.goal_id=gdf.goal_id " +
					" where gd.emp_ids like '%,"+empList.get(i)+ ",%' and gd.goal_type in ("+goalTyp+") and gd.is_close = false ");
				sbQuery.append(" order by freq_start_date");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					if(rs.getInt("goal_type") == EMPLOYEE_KRA && rs.getString("is_assign") != null && rs.getString("is_assign").equals("f")) {
						continue;
					}
					
					if(rs.getString("freq_end_date") != null) {
						Date dtDeadLine = uF.getDateFormat(rs.getString("freq_end_date"), DBDATE);
						Date dtCurrDate = uF.getCurrentDate(CF.getStrTimeZone());
//						System.out.println("dtDeadLine ===>> " + dtDeadLine + "  -- dtCurrDate ===>> " + dtCurrDate);
						if(dtCurrDate.after(dtDeadLine) || (hmKRATaskStatus != null && uF.parseToInt(hmKRATaskStatus.get(empList.get(i)+"_"+rs.getString("goal_id")+"_"+rs.getString("goal_freq_id")+"_"+rs.getString("goal_kra_id"))) >= 100)) {
							kraFormCount++;
//							System.out.println("kraFormCount ===>> " + kraFormCount);
						}
					}
				}
				rs.close();
				pst.close();
			}
			
			request.setAttribute("kraFormCount", kraFormCount+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	List<String> getEmployeeList(Connection con, UtilityFunctions uF) {
		List<String> al = new ArrayList<String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
				"and is_alive = true and (eod.supervisor_emp_id = "+strEmpId+" or eod.emp_id = "+strEmpId+") ");
			sbQuery.append(" order by epd.emp_fname");	
			pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if(!al.contains(rs.getString("emp_per_id"))) {
					al.add(rs.getString("emp_per_id"));
				}
			}
			rs.close();
			pst.close();
//			System.out.println("al ===>> " + al);
			request.setAttribute("empList", al);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
	
	public void getMyTeamTargetAchievedAndMissed() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try { 
			List<String> empList = getEmployeeList(con, uF);
			
			String goalTyp = INDIVIDUAL_GOAL+","+INDIVIDUAL_TARGET+","+PERSONAL_GOAL;
//			pst=con.prepareStatement("select (percentage/target_count) as average from (select count(*) as target_count,sum(amt_percentage) as percentage " +
//				" from (select max(target_id) as target_id from (select goal_id from goal_details where emp_ids like '%,"+strEmpId+",%' and " +
//				" goal_type in ("+goalTyp+") and measure_kra='Measure') as a, target_details td where a.goal_id = td.goal_id group by a.goal_id) as a," +
//				" target_details td where a.target_id=td.target_id) as b");
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select (percentage/target_count) as average from (select count(*) as target_count,sum(amt_percentage) as percentage " +
					" from (select max(target_id) as target_id from (select goal_id from goal_details where goal_type in ("+goalTyp+") ");
			if(empList !=null && !empList.isEmpty()) {
                sbQuery.append(" and (");
                for(int i=0; i<empList.size(); i++){
                    sbQuery.append(" emp_ids like '%,"+empList.get(i)+",%'");
                    
                    if(i<empList.size()-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
			sbQuery.append(" and measure_kra='Measure') as a, target_details td where a.goal_id = td.goal_id group by a.goal_id) as a," +
				" target_details td where a.target_id=td.target_id) as b");
			pst=con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, INDIVIDUAL_GOAL);
//			System.out.println("pst======>"+pst);
			rs=pst.executeQuery();
			double achievedAmt = 0.0d;
			while (rs.next()) {
				achievedAmt =rs.getDouble("average");
			}
			rs.close();
			pst.close();
			
//			pst=con.prepareStatement("select (percentage/goal_count) as average, goal_count from (select count(*) as goal_count, sum(measure_currency_value) as percentage " +
//					"from goal_details where emp_ids like '%,"+strEmpId+",%' and goal_type = ? and measure_kra='Measure') as b");
//			pst.setInt(1, INDIVIDUAL_GOAL);
			sbQuery = new StringBuilder();
			sbQuery.append("select (percentage/goal_count) as average, goal_count from (select count(*) as goal_count, sum(measure_currency_value) as percentage " +
				"from goal_details where goal_type in ("+goalTyp+") ");
			if(empList !=null && !empList.isEmpty()) {
                sbQuery.append(" and (");
                for(int i=0; i<empList.size(); i++){
                    sbQuery.append(" emp_ids like '%,"+empList.get(i)+",%'");
                    
                    if(i<empList.size()-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
			sbQuery.append(" and measure_kra='Measure') as b");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs=pst.executeQuery();
			double targetedAmt = 0.0d;
			int myTargetsCnt = 0;
			while (rs.next()) {
				targetedAmt = rs.getDouble("average");
				myTargetsCnt = rs.getInt("goal_count");
			}
			rs.close();
			pst.close();
			
			double achievedPercent = 0.0d;
			if(targetedAmt > 0d) {
				achievedPercent = (achievedAmt*100) / targetedAmt;  
			}
			
//			System.out.println("tachievedPercent ===> " + achievedPercent);
			
			String twoDeciTotProgressAvg = "0";
			String twoDeciTot = "0";
			String total="100";
			double totalTarget=achievedPercent;
			
				if(totalTarget > new Double(100) && totalTarget<=new Double(150)) {
					double totalTarget1=(totalTarget/150)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="150";
				} else if(totalTarget > new Double(150) && totalTarget<=new Double(200)) {
					double totalTarget1=(totalTarget/200)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="200";
				} else if(totalTarget > new Double(200) && totalTarget<=new Double(250)) {
					double totalTarget1=(totalTarget/250)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="250";
				} else if(totalTarget > new Double(250) && totalTarget<=new Double(300)) {
					double totalTarget1=(totalTarget/300)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="300";
				} else if(totalTarget > new Double(300) && totalTarget<=new Double(350)) {
					double totalTarget1=(totalTarget/350)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="350";
				} else if(totalTarget > new Double(350) && totalTarget<=new Double(400)) {
					double totalTarget1=(totalTarget/400)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="400";
				} else if(totalTarget > new Double(400) && totalTarget<=new Double(450)) {
					double totalTarget1=(totalTarget/450)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="450";
				} else if(totalTarget > new Double(450) && totalTarget<=new Double(500)) {
					double totalTarget1=(totalTarget/500)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="500";
				} else {
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget);
					if(uF.parseToDouble(twoDeciTotProgressAvg) > 100) {
						twoDeciTotProgressAvg = "100";
						total=""+Math.round(totalTarget);
					} else {
						total="100";
					}
				}
			
			double remainPercent = 100 - Math.round(uF.parseToDouble(twoDeciTotProgressAvg));
//			System.out.println("tremainPercent ===> " + remainPercent);
			request.setAttribute("myTeamTargetsCnt", ""+myTargetsCnt);
			request.setAttribute("myTeamTargetachievedPercent", ""+Math.round(uF.parseToDouble(twoDeciTotProgressAvg)));
			request.setAttribute("myTeamTargetremainPercent", ""+remainPercent);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
	}
	
	
	
	public void getMyTeamKRAAchievedStatus() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try { 
			
			List<String> empList = getEmployeeList(con, uF);
			
			String goalTyp = INDIVIDUAL_GOAL+","+INDIVIDUAL_KRA+","+EMPLOYEE_KRA;
//			select sum(gksrd.complete_percent), count(*) as kra_count, a.goal_id, a.goal_freq_id from (select gd.goal_id, gdf.goal_freq_id from goal_details gd, goal_details_frequency gdf where gd.goal_id = gdf.goal_id and gd.org_id in (1,2) and gd.goal_type in (4,7,8) and gd.measure_kra='KRA' ) as a left join goal_kra_status_rating_details gksrd on gksrd.goal_freq_id = a.goal_freq_id group by a.goal_id, a.goal_freq_id, gksrd.kra_id order by a.goal_id
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(gksrd.complete_percent) as complete_percent, count(*) as kra_count from goal_details gd, goal_kra_status_rating_details gksrd " +
				" where gd.goal_type in ("+goalTyp+") ");
			if(empList !=null && !empList.isEmpty()) {
                sbQuery.append(" and (");
                for(int i=0; i<empList.size(); i++){
                    sbQuery.append(" emp_ids like '%,"+empList.get(i)+",%'");
                    
                    if(i<empList.size()-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
			sbQuery.append(" and gd.measure_kra='KRA' and gksrd.goal_id = gd.goal_id group by gksrd.goal_id, gksrd.goal_freq_id, gksrd.kra_id ");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, INDIVIDUAL_GOAL);
//			System.out.println("pst======>"+pst);
			rs=pst.executeQuery();
			Map<String, String> hmTeamKRAData = new HashMap<String, String>();
			while (rs.next()) {
				double dblCompletePer = rs.getDouble("complete_percent") / rs.getInt("kra_count");
				double dblTotCompletePer = uF.parseToDouble(hmTeamKRAData.get("PERCENT"));
				dblTotCompletePer += dblCompletePer;
				int intTotCnt = uF.parseToInt(hmTeamKRAData.get("COUNT"));
				intTotCnt++;
//				System.out.println("dblCompletePer ===>> " + dblCompletePer);
				
				hmTeamKRAData.put("PERCENT", dblTotCompletePer+"");
				hmTeamKRAData.put("COUNT", intTotCnt+"");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmTeamKRAData", hmTeamKRAData);
			
//			System.out.println("tremainPercent ===> " + remainPercent);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
	}


	public void getExceptionCount() {
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			String strUserType = (String) session.getAttribute(BASEUSERTYPE);

			if(strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(HOD) || strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))){
				int nExceptionCount = CF.getExceptionCount(con, request, CF, uF, MANAGER, strEmpId, session);
			
				StringBuilder sbQuery = new StringBuilder(); 
				sbQuery.append("select count(*) as cnt from exception_reason where status=0 and (in_out_type='HD' or in_out_type='FD') and emp_id in (select emp_id from employee_official_details " +
					"where is_roster=true and supervisor_emp_id = "+uF.parseToInt(strEmpId)+") ");	
	//			System.out.println("3 pst==6====> " + pst);  
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				while (rs.next()) {
					nExceptionCount += rs.getInt("cnt");
				}
				rs.close();
				pst.close();
				
	//			System.out.println("hmEmpHDFDException ===>> " + hmEmpHDFDException);
				request.setAttribute("exceptionCount",""+nExceptionCount);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
	
//===created by parvez date: 28-10-2022===	
	//===start===
	public void getWorkAnniversary() {
		 
		ResultSet rs = null;
		PreparedStatement pst =null;
		try {
			
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strToday1 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM-dd");
			
			Map<String, String> hmEmployeeMap = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);
			Map<String, String> hmEmpDepartmentMap = CF.getEmpDepartmentMap(con);
			Map<String, Map<String, String>> hmWlocationMap = CF.getWorkLocationMap(con);
			Map<String, String> hmDepartmentMap = CF.getDeptMap(con);
			

			pst = con.prepareStatement("SELECT emp_per_id, joining_date,emp_gender, EXTRACT( YEAR FROM AGE(joining_date) ) as work_years FROM employee_personal_details " +
					" WHERE  EXTRACT( YEAR FROM AGE(joining_date) ) < EXTRACT( YEAR FROM AGE(?, joining_date ) ) and is_alive=true ORDER BY work_years, emp_per_id");			
			
			pst.setDate(1, uF.getFutureDate(CF.getStrTimeZone(), 365));
			rs = pst.executeQuery();

			List<List<String>> alWorkAnniversary = new ArrayList<List<String>>();
			while (rs.next()) {
				
				String strWADate = uF.getDateFormat(rs.getString("joining_date"), DBDATE, "MM-dd");
				String strWADay = uF.getDateFormat(rs.getString("joining_date"), DBDATE, "dd");
				String strWAMonth = uF.getDateFormat(rs.getString("joining_date"), DBDATE, "MMM");
				String strWlocationId = hmEmpWlocationMap.get(rs.getString("emp_per_id"));
				String strTomorrow = uF.getDateFormat(uF.getFutureDate(CF.getStrTimeZone(), 1)+"", DBDATE, "MM-dd");
				String strDayAfterTomorrow = uF.getDateFormat(uF.getFutureDate(CF.getStrTimeZone(), 2)+"", DBDATE, "MM-dd");
				
				Map hmWlocation = hmWlocationMap.get(strWlocationId);
				if(hmWlocation==null)hmWlocation=new HashMap();
				
				String strCity = (String)hmWlocation.get("WL_CITY");
				String gender = (String)rs.getString("emp_gender");
				if(strWADate!=null && strWADate.equals(strToday1)) {
					if(hmEmployeeMap.get(rs.getString("emp_per_id")) != null && !hmEmployeeMap.get(rs.getString("emp_per_id")).equals("null")) {
						List<String> innerList = new ArrayList<String>();
						if(gender!=null && gender.equalsIgnoreCase("M")){
							innerList.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has work anniversary today wish him...!");
							innerList.add(strWADay);
							innerList.add(strWAMonth);
						}else if(gender!=null && gender.equalsIgnoreCase("F")){
							innerList.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has work anniversary wish her...!");
							innerList.add(strWADay);
							innerList.add(strWAMonth);
						}else{
							innerList.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has work anniversary...!");
							innerList.add(strWADay);
							innerList.add(strWAMonth);
						}
						alWorkAnniversary.add(innerList);
					}
				}
				
				if(strWADate!=null && strWADate.equals(strTomorrow)) {
					if(hmEmployeeMap.get(rs.getString("emp_per_id")) != null && !hmEmployeeMap.get(rs.getString("emp_per_id")).equals("null")) {
						List<String> innerList = new ArrayList<String>();
						if(gender!=null && gender.equalsIgnoreCase("M")){
							//alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today wish him...!");
							innerList.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has work anniversary tomorrow wish him...!");
							innerList.add(strWADay);
							innerList.add(strWAMonth);
						}else if(gender!=null && gender.equalsIgnoreCase("F")){
							//alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today wish her...!");
							innerList.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has work anniversary tomorrow wish her...!");
							innerList.add(strWADay);
							innerList.add(strWAMonth);
						}else{
						//	alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today...!");
							innerList.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has work anniversary tomorrow...!");
							innerList.add(strWADay);
							innerList.add(strWAMonth);
						}
						alWorkAnniversary.add(innerList);
					}
				}
				
			}
			rs.close();
			pst.close();
//			cst.close();
//			System.out.println("alBirthDays ===>> " + alBirthDays);
			request.setAttribute("alWorkAnniversary", alWorkAnniversary);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
//===end===	
	
}