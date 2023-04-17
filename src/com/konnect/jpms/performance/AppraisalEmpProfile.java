package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.employee.EmpDashboard;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AppraisalEmpProfile extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	private String empId;
	String strSessionEmpId;
	String strUserType;
	String strBaseUserType =  null;
	String strWLocationAccess =  null;
	
public String execute() throws Exception {
		session=request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);
		strWLocationAccess = (String)session.getAttribute(WLOCATION_ACCESS);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/performance/AppraisalEmpProfile.jsp");

		request.setAttribute("arrEnabledModules", CF.getArrEnabledModules());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		UtilityFunctions uF = new UtilityFunctions();
		List<String> accessEmpList = CF.viewEmployeeIdsList(request, uF, strBaseUserType, strSessionEmpId, strWLocationAccess);
//		if((strBaseUserType != null && strUserType != null && (strBaseUserType.equals(EMPLOYEE) || strUserType.equals(EMPLOYEE))) || !accessEmpList.contains(getEmpId())) {
		if(strBaseUserType!=null && strUserType!=null && (strBaseUserType.equals(EMPLOYEE) || !accessEmpList.contains(getEmpId())) ) {
			setEmpId((String)session.getAttribute(EMPID));
		}
		
		viewProfile(getEmpId());
//			getElementList();
//			getEMPScore();
//			getEmpImage(uF);
		return "success";

	}



//private void getEmpImage(UtilityFunctions uF) {
//	Connection con = null;
//	PreparedStatement pst = null;
//	ResultSet rs = null;
//	Database db = new Database();
//	db.setRequest(request);
//
//	try {
//		con = db.makeConnection(con);
//
//		pst=con.prepareStatement("select emp_image,emp_per_id from employee_personal_details ");
//		rs=pst.executeQuery();
//		Map<String,String> empImageMap=new HashMap<String,String>();
//		while(rs.next()){
//			empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
//		}
//		request.setAttribute("empImageMap", empImageMap);
//
//	} catch (Exception e) {
//		e.printStackTrace();
//	} finally {
//		
//		db.closeStatements(pst);
//		db.closeResultSet(rs);
//		db.closeConnection(con);
//	}
//}

//public void getElementList() {
//	Connection con = null;
//	PreparedStatement pst = null;
//	Database db = new Database();
//	db.setRequest(request);
//	ResultSet rs = null;
//	try {
//		con = db.makeConnection(con);
//		pst = con.prepareStatement("select * from appraisal_element order by appraisal_element_id");
//		rs = pst.executeQuery();
//		List<List<String>> elementouterList=new ArrayList<List<String>>();
//		while (rs.next()) {
//			List<String> innerList=new ArrayList<String>();
//			innerList.add(rs.getString("appraisal_element_id"));
//			innerList.add(rs.getString("appraisal_element_name"));
//			elementouterList.add(innerList);
//		}
//		request.setAttribute("elementouterList",elementouterList);
//
//	} catch (Exception e) {
//		e.printStackTrace();
//	} finally {
//		
//		db.closeStatements(pst);
//		db.closeConnection(con);
//	}
//}


//public void getEMPScore() {
//	Connection con = null;
//	PreparedStatement pst = null;
//	Database db = new Database();
//	db.setRequest(request);
//	ResultSet rs = null;
////	double totAverage=0;
//	UtilityFunctions uF = new UtilityFunctions();
//	try {
//		con = db.makeConnection(con);
////		Map<String, String> hmAnalysisSummaryMap = new HashMap<String, String>();
//		List<List<String>> empScoreList=new ArrayList<List<String>>();
////		List<String> empScoreList = new ArrayList<String>();
//		pst = con.prepareStatement("select *,((marks*100/weightage)) as average from(select sum(marks) as marks, sum(weightage) " +
//				"as weightage,a.appraisal_element from (select appraisal_element,appraisal_attribute from appraisal_element_attribute " +
//				"group by appraisal_element,appraisal_attribute) as a,appraisal_question_answer aqw where aqw.emp_id = ? and " +
//				"a.appraisal_attribute=aqw.appraisal_attribute group by a.appraisal_element ) as aa order by aa.appraisal_element");
////		System.out.println("PST is in getAnalysisSummary =========== >"+pst);
//		pst.setInt(1, uF.parseToInt(getEmpId()));
//		rs = pst.executeQuery();
//		while (rs.next()) {
//			List<String> innerempScoreList = new ArrayList<String>();
////			totAverage += uF.parseToDouble(rs.getString("average"));
//			innerempScoreList.add(rs.getString("average"));
//			innerempScoreList.add(rs.getString("appraisal_element"));
////			hmAnalysisSummaryMap.put(rs.getString("appraisal_element"),rs.getString("average"));
//			empScoreList.add(innerempScoreList);
//		}
//		
//		request.setAttribute("empScoreList",empScoreList);
////		request.setAttribute("totAverage",""+totAverage);
//	} catch (Exception e) {
//		e.printStackTrace();
//	} finally {
//		
//		db.closeStatements(pst);
//		db.closeConnection(con);
//	}
//}


public void viewProfile(String strEmpIdReq) {

	Connection con = null;
	Database db = new Database();
	db.setRequest(request);
	UtilityFunctions uF = new UtilityFunctions();

	try {

		con = db.makeConnection(con);
		
		CF.getEmpProfileDetail(con, request, session, CF, uF, strUserType, strEmpIdReq);
		request.setAttribute(TITLE, "Employee Profile");
		
		CF.getElementList(con, request);
		CF.getAttributes(con, request, strEmpIdReq);
		
		List<List<String>> alSkills = new ArrayList<List<String>>();
		alSkills = CF.selectSkills(con, uF.parseToInt(strEmpIdReq));
		request.setAttribute("alSkills", alSkills);
		
//		request.setAttribute("alActivityDetails", alActivityDetails);
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeConnection(con);
	}
//	return SUCCESS;

}

//public String viewProfile(String strEmpIdReq) {
//
//	Connection con = null;
//	PreparedStatement pst = null;
//	ResultSet rs = null;
//	Database db = new Database();
//	db.setRequest(request);
//	UtilityFunctions uF = new UtilityFunctions();
//
//	List<List<String>> alSkills = new ArrayList<List<String>>();
//
//	try {
//
//		Map<String, String> hm = new HashMap<String, String>();
//		
//		con = db.makeConnection(con);
//		pst = con.prepareStatement("Select * from ( Select * from ( Select * from employee_personal_details epd left join employee_official_details eod on epd.emp_per_id=eod.emp_id where epd.emp_per_id=?) ast left join state s on ast.emp_state_id = s.state_id ) aco left join country co on aco.emp_country_id = co.country_id");
//		if (strEmpIdReq != null) {
//			pst.setInt(1, uF.parseToInt(strEmpIdReq));
//		} else {
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//		}
//		rs = pst.executeQuery();
//		
//		
//		while (rs.next()) {
//
////			strEmpOffId = rs.getString("emp_off_id");
////			strEmpId = rs.getString("emp_per_id");
//
//			
//			
//			
//			hm.put("EMPID", rs.getString("emp_per_id"));
//			hm.put("EMPCODE", rs.getString("empcode"));
//			hm.put("NAME", rs.getString("emp_fname") + " "+ rs.getString("emp_lname"));
////			hm.put("ADDRESS", rs.getString("emp_address1") + " "+ rs.getString("emp_address2"));
////			hm.put("CITY", rs.getString("emp_city_id"));
////			hm.put("STATE", rs.getString("state_name"));
////			hm.put("COUNTRY", rs.getString("country_name"));
////			hm.put("PINCODE", rs.getString("emp_pincode"));
////			hm.put("CONTACT", rs.getString("emp_contactno"));
//			hm.put("CONTACT_MOB", rs.getString("emp_contactno_mob"));
//			hm.put("IMAGE", rs.getString("emp_image"));
//			hm.put("EMAIL", rs.getString("emp_email"));
////			hm.put("EMAIL_SEC", rs.getString("emp_email_sec"));
//			
//			if(rs.getString("emp_email_sec")!=null && rs.getString("emp_email_sec").indexOf("@")>0){
//				hm.put("EMP_EMAIL", rs.getString("emp_email_sec"));
//			}else{
//				hm.put("EMP_EMAIL", rs.getString("emp_email"));
//			}
//			
////			hm.put("SKYPE_ID", rs.getString("skype_id"));
////			hm.put("DESIGNATION", hmEmpDesigMap.get(rs.getString("emp_per_id")));
////			hm.put("LEVEL", hmLevelMap.get(hmEmpLevelMap.get(rs.getString("emp_per_id"))));
////			hm.put("GRADE", hmGrades.get(rs.getString("grade_id")));
////			hm.put("GENDER", rs.getString("emp_gender"));
//			
////			hm.put("PAN_NO", rs.getString("emp_pan_no"));
////			hm.put("PF_NO", rs.getString("emp_pf_no"));
//			
////			hm.put("EMERGENCY_NAME", rs.getString("emergency_contact_name"));
////			hm.put("EMERGENCY_NO", rs.getString("emergency_contact_no"));
//			
////			hm.put("DOB", uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, CF.getStrReportDateFormat()));
////			hm.put("MARITAL_STATUS", rs.getString("marital_status"));
////			hm.put("PASSPORT_NO", rs.getString("passport_no"));
////			hm.put("PASSPORT_EXPIRY", uF.getDateFormat(rs.getString("passport_expiry_date"), DBDATE, CF.getStrReportDateFormat()));
//			
//			
////			Map<String, String> hmWLocation = hmWorkLocationMap.get(rs.getString("wlocation_id"));
////			if (hmWLocation == null) {
////				hmWLocation = new HashMap<String, String>();
////			}
//
////			hm.put("WL_NAME", hmWLocation.get("WL_NAME"));
////			hm.put("WL_CITY", hmWLocation.get("WL_CITY"));
//
////			hm.put("JOINING_DATE", uF.getDateFormat(rs.getString("joining_date"), DBDATE,CF.getStrReportDateFormat()));
////			hm.put("EMP_TYPE", uF.stringMapping(rs.getString("emptype")));
//
////			hm.put("DEPT", hmDeptMap.get(rs.getString("depart_id")));
////			hm.put("SUPER_ID", rs.getString("supervisor_emp_id"));
//			
////			String str = rs.getString("service_id");
////			StringBuilder sbServices = new StringBuilder();
////			
////			
////			if (str != null && str.length() > 0) {
////				strServices = str.split(",");
////				for (int i = 0; i < strServices.length; i++) {
////
////					sbServices.append((String) hmServices.get(strServices[i]));
////					if (i < strServices.length - 1)
////						sbServices.append(", ");
////				}
////			}
////			
////			strServices = new String[1];
////			strServices[0] = "0";
//			
//			
//			
//			
//
////			hm.put("COST_CENTRE", sbServices.toString());
////			hm.put("ROSTER_DEPENDENCY", uF.showYesNo(rs.getString("is_roster")));
////			hm.put("ALLOWANCE", uF.showYesNo(rs.getString("first_aid_allowance")));
////
////			if(rs.getString("joining_date")!=null){
////				uF.getTimeDuration(rs.getString("joining_date"), CF, uF, request);
////			}
//			
//			
//		}
//
//		
//		
////		pst = con.prepareStatement("select * from employee_activity_details where emp_id = ? and activity_id = 6 order by effective_date desc limit 2");
////		if (strEmpIdReq != null) {
////			pst.setInt(1, uF.parseToInt(strEmpIdReq));
////		} else {
////			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
////		}
////		rs = pst.executeQuery();
////		int i=0;
////		while(rs.next()){
////			if(i==0){
////				hm.put("PREV_PROMOTION", uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()));
////			}else{
////				hm.put("PREV_DESIGNATION", hmDesigMap.get(rs.getString("desig_id")));
////			}
////			i++;
////			
////		}
//		
//		
//		
//		
//		
//		
////		pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and eod.emp_id = ?");
////		pst.setInt(1, uF.parseToInt((String)hm.get("SUPER_ID")));
////		rs = pst.executeQuery();
////		while(rs.next()){
////			hm.put("SUPER_CODE", rs.getString("empcode"));
////			hm.put("SUPER_NAME", rs.getString("emp_fname") + " "+ rs.getString("emp_lname"));
////		}
//		
//		
//		request.setAttribute("myProfile", hm);
//
////		Map<String, Map<String, String>> hmPayrollPolicy = new HashMap<String, Map<String, String>>();
////		hmPayrollPolicy = CF.getDailyRates((String) session.getAttribute("EMPID"));
////
////		request.setAttribute("hmPayrollPolicy", hmPayrollPolicy);
////		request.setAttribute("hmServices", hmServices);
//
//		int intEmpIdReq = uF.parseToInt(strEmpIdReq);
////		getSalaryHeadsforEmployee(con, uF, intEmpIdReq, strServices);
//
//
////		getWorkedHours(con, uF);
////		if(strJoiningDate!=null){
////			uF.getTimeDuration(strJoiningDate, CF, uF, request);
////		}
////		request.setAttribute("myProfile", hm);
//		
//		
////		pst = con.prepareStatement("select * from emp_kras where emp_id = ? and is_approved = true order by effective_date desc, kra_order");
////		pst.setInt(1, intEmpIdReq);
////		rs = pst.executeQuery();
////		
////		List<String> alKRA = new ArrayList<String>();
////		List<String> alKRAInner = new ArrayList<String>();
////		Map<String,List<String>> hmKRA = new HashMap<String,List<String>>();
////		String strEffectiveDateNew = null;
////		String strEffectiveDateOld = null;
////		while(rs.next()){
////			strEffectiveDateNew = uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat());
////			if(strEffectiveDateNew!=null && !strEffectiveDateNew.equalsIgnoreCase(strEffectiveDateOld)){
////				alKRAInner = new ArrayList<String>();
////			}
////			if(rs.getString("kra_description")!=null && rs.getString("kra_description").length()>0){
////				alKRAInner.add( rs.getString("kra_description").replace("\n", "<br/>"));
////			}
////			if(!alKRA.contains(strEffectiveDateNew)){
////				alKRA.add(strEffectiveDateNew);
////			}
////			
////			hmKRA.put(strEffectiveDateNew, alKRAInner);
////			strEffectiveDateOld = strEffectiveDateNew;
////		}
//		
////		System.out.println("alKRA==>"+alKRA);
////		System.out.println("hmKRA==>"+hmKRA);
//		
////		request.setAttribute("alKRA", alKRA);
////		request.setAttribute("hmKRA", hmKRA);
//		
//		
//		
//		
//		
//		
////		EmpDashboard objEmpDashboard = new EmpDashboard(request, session, CF, strEmpId);
////		objEmpDashboard.getEmpKPI(con, uF, strEmpIdReq);
////		objEmpDashboard.getResignationStatus(con, uF);
////		objEmpDashboard.getProbationStatus(con, uF);
//
//		alSkills = CF.selectSkills(con, intEmpIdReq);
////		alHobbies = CF.selectHobbies(intEmpIdReq);
////		alLanguages = CF.selectLanguages(intEmpIdReq);
////		alEducation = CF.selectEducation(intEmpIdReq);
////
////		String filePath = request.getRealPath("/userDocuments/");
////		alDocuments = CF.selectDocuments(intEmpIdReq, filePath);
////
////		alFamilyMembers = CF.selectFamilyMembers(intEmpIdReq);
////		alPrevEmployment = CF.selectPrevEmploment(intEmpIdReq);
////
////		alActivityDetails = CF.selectEmpActivityDetails(intEmpIdReq, CF);
//
//		
//		
////		boolean isFilledStatus = uF.getEmpFilledStatus(getEmpId());
//		
//		
////		getOfficialFilledStatus(con, uF);
//		
//		request.setAttribute("alSkills", alSkills);
////		request.setAttribute("alHobbies", alHobbies);
////		request.setAttribute("alLanguages", alLanguages);
////		request.setAttribute("alEducation", alEducation);
////		request.setAttribute("alDocuments", alDocuments);
////		request.setAttribute("alFamilyMembers", alFamilyMembers);
////
////		request.setAttribute("alPrevEmployment", alPrevEmployment);
////		request.setAttribute("alActivityDetails", alActivityDetails);
////		request.setAttribute("isFilledStatus", isFilledStatus+"");
//
//
//		
//	} catch (Exception e) {
//		e.printStackTrace();
//	} finally {
//		
//		db.closeStatements(pst);
//		db.closeResultSet(rs);
//		db.closeConnection(con);
//	}
//	return SUCCESS;
//
//}

private HttpServletRequest request;

@Override
public void setServletRequest(HttpServletRequest request) {
	this.request = request;

}

public String getEmpId() {
	return empId;
}

public void setEmpId(String empId) {
	this.empId = empId;
}
}
