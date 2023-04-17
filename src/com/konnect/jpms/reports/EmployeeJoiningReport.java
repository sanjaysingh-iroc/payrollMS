package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillBank;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployeeStatus;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeJoiningReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null; 
	String strEmpId = null;
	String startDate;
	String endDate;
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	String strGrade;
	String strEmployeType;
	String strStatus;
	
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	String[] f_employeType;
	String[] f_grade;
	String[] f_status;
	
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillEmploymentType> employementTypeList;
	List<FillGrade> gradeList;
	private List<FillBank> bankList;
	private List<FillEmployeeStatus> empStatusList;
	
	String exportType;
	
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(WorkForceJoinReport.class);
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(TITLE, "Employee Joinings");
		request.setAttribute(PAGE, "/jsp/reports/EmployeeJoiningReport.jsp");
		
		UtilityFunctions uF = new UtilityFunctions();
		
		empStatusList = new FillEmployeeStatus().fillEmployeeStatus();
		
		if(getF_org()==null || getF_org().trim().equals("")){
			setF_org((String)session.getAttribute(ORGID));
		}
			
		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if(getStrSbu() != null && !getStrSbu().equals("")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		
		if(f_level!=null) {
			String level_id ="";
			for (int i = 0; i < f_level.length; i++) {
				if(i==0) {
					level_id = f_level[i];
					level_id.concat(f_level[i]);
				} else {
					level_id =level_id+","+f_level[i];
				}
			}
			gradeList = new FillGrade(request).fillGrade(level_id,getF_org());
		} else {
			gradeList = new FillGrade(request).fillGradebyorganisation(getF_org());
		}
		
		if(getStrGrade() != null && !getStrGrade().equals("")) {
			setF_grade(getStrGrade().split(","));
		} else {
			setF_grade(null);
		}
		
		if(getStrEmployeType() != null && !getStrEmployeType().equals("")) {
			setF_employeType(getStrEmployeType().split(","));
		} else {
			setF_employeType(null);
		}
		
		if(getStrStatus() != null && !getStrStatus().equals("")) {
			setF_status(getStrStatus().split(","));
		} else {
//			setF_status(null);
			if(empStatusList.size()!=0) {
				String statusId;
				
				for (int i = 0; i < empStatusList.size() - 1; i++) {
					statusId = (empStatusList.get(i)).getStatusId();
//					statusName = empStatusList.get(i).getStatusName();
					if(statusId.equals("PERMANENT")){
						Set<String> setStatusId = new HashSet<String>(Arrays.asList(statusId));
						String[] strStatusId = setStatusId.toArray(new String[0]);
						setF_status(strStatusId);
//						System.out.println("strStatusId"+strStatusId[0]);
					}
				
				}
			}
		}
		
		viewEmployeeJoin(uF);

		return loadEmployeeJoin(uF);
		
	}
	
	public String loadEmployeeJoin(UtilityFunctions uF) {
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		employementTypeList = new FillEmploymentType().fillEmploymentType(request);
//		empStatusList = new FillEmployeeStatus().fillEmployeeStatus();
		
		int i;
		
		if(empStatusList.size()!=0) {
			String statusName, statusId;
			StringBuilder sbStatusList = new StringBuilder();
			sbStatusList.append("{");
			for (i = 0; i < empStatusList.size() - 1; i++) {
				statusId = (empStatusList.get(i)).getStatusId();
				statusName = empStatusList.get(i).getStatusName();
				sbStatusList.append("\"" + statusId + "\":\"" + statusName + "\",");
			
			}
			statusId = (empStatusList.get(i)).getStatusId();
			statusName = empStatusList.get(i).getStatusName();
			sbStatusList.append("\"" + statusId + "\":\"" + statusName + "\"}");
			request.setAttribute("sbStatusList", sbStatusList.toString());
		}
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	public String viewEmployeeJoin(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			if(getF_status() == null){
				
			}
			
			if(getEndDate() == null) {
				String strCurrentDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),DBDATE,DATE_FORMAT);
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strCurrentDate, DATE_FORMAT, "MM"))-1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strCurrentDate, DATE_FORMAT, "yyyy")));
				
				int nMonthStart = cal.getActualMinimum(Calendar.DATE);
				int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
				
				String nStDate = nMonthStart < 10 ? "0"+nMonthStart : ""+nMonthStart;
				String nEdDate = nMonthEnd < 10 ? "0"+nMonthEnd : ""+nMonthEnd;
				String mnth =""+ (uF.parseToInt(uF.getDateFormat(strCurrentDate, DATE_FORMAT, "MM"))< 10 ? "0" +uF.parseToInt(uF.getDateFormat(strCurrentDate, DATE_FORMAT, "MM")) : uF.parseToInt(uF.getDateFormat(strCurrentDate, DATE_FORMAT, "MM")));
				String strDateStart =  nStDate+"/"+mnth+"/"+cal.get(Calendar.YEAR);
				String strDateEnd =  nEdDate+"/"+mnth+"/"+cal.get(Calendar.YEAR);
				
				setStartDate(strDateStart);
				setEndDate(strDateEnd);
			}
			
			List<String> alInner = new ArrayList<String>();
			List<String> alEmployees=new ArrayList<String>();
			List<List<String>> reportList = new ArrayList<List<String>>();
			
			Map<String, String> hmDepart = CF.getDepartmentMap(con,null, null);
			Map<String, String> hmWLocation = CF.getWLocationMap(con,null, null);
			Map<String, String> hmEmpDesig = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			if(hmEmpLevelMap == null) hmEmpLevelMap = new HashMap<String, String>();
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			if(hmLevelMap == null) hmLevelMap = new HashMap<String, String>();
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			if(hmEmpCodeName == null) hmEmpCodeName = new HashMap<String, String>();
			Map<String, String> hmServices = CF.getServicesMap(con, false);
			if(hmServices == null) hmServices = new HashMap<String, String>();
			Map<String, String> hmEmpGenderMap = CF.getEmpGenderMap(con);
			if(hmEmpGenderMap == null) hmEmpGenderMap = new HashMap<String, String>();
			
			pst=con.prepareStatement("select org_id,org_name from org_details");
			Map<String, String> hmOrg=new HashMap<String, String>();
			rs=pst.executeQuery();
			while(rs.next()){
				hmOrg.put(rs.getString("org_id"), rs.getString("org_name"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmOrg", hmOrg);
			
			
			Map<String, String> hmFamilyInfo = new HashMap<String, String>();
			Map<String, String> hmFamilyInfoContactNo = new HashMap<String, String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_family_members where member_type in ('SPOUSE', 'FATHER') ");

			if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details eod where emp_id > 0 ");
			}
			
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if (getF_employeType() != null && getF_employeType().length > 0) {
					sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
			
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0)
            {
            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
            		
            }else {
            	 if(getF_level()!=null && getF_level().length>0){
                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
                 }
            	 if(getF_grade()!=null && getF_grade().length>0){
                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
                 }
			}
            
            if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
            if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(") ");
			}
          
//            System.out.println("EJR/316--sbQuery==>"+sbQuery.toString());
			pst = con.prepareStatement(sbQuery.toString());
			
//			System.out.println("EJR/318--pst==>"+pst);
			
			
			rs = pst.executeQuery();
			while(rs.next()){
				hmFamilyInfo.put(rs.getString("emp_id")+"_"+rs.getString("member_type"), rs.getString("member_name"));
				hmFamilyInfoContactNo.put(rs.getString("emp_id")+"_"+rs.getString("member_type"), rs.getString("member_contact_no"));
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_prev_employment epe, employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and epe.emp_id=eod.emp_id");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
			if(getF_status()!=null && getF_status().length>0) {
				sbQuery.append(" and emp_status  in ("+getDataFromArray(getF_status())+") ");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            
            if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0) {
            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
            		
            }else {
            	
            	if(getF_level()!=null && getF_level().length>0){
            		sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
                }
            	if(getF_grade()!=null && getF_grade().length>0){
                    sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
                }
			}
            if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
            sbQuery.append(" and joining_date <=? order by emp_fname, emp_lname");
//            System.out.println("EJR/377--sbQuery==>"+sbQuery.toString());
            pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getEndDate(), DATE_FORMAT));
			
//			System.out.println("pst=======>"+pst);
			rs = pst.executeQuery();
		
			Map<String,List<List<String>>>hmEmpStEndMonth=new LinkedHashMap<String,List<List<String>>>();
			Map<String,String> hmPreEmployment = new HashMap<String, String>();
			while(rs.next()){
				
				List<List<String>>alOuterEmpStEndMonth = hmEmpStEndMonth.get(rs.getString("emp_id"));
				if(alOuterEmpStEndMonth==null)alOuterEmpStEndMonth = new ArrayList<List<String>>();
				
				List<String>alInnerEmpStEndMonth=new ArrayList<String>();
				
				alInnerEmpStEndMonth.add(uF.showData(rs.getString("from_date"), ""));
				alInnerEmpStEndMonth.add(uF.showData(rs.getString("to_date"), ""));
				
				alOuterEmpStEndMonth.add(alInnerEmpStEndMonth);
				hmEmpStEndMonth.put(uF.showData(rs.getString("emp_id"),""), alOuterEmpStEndMonth);
				
				if(hmPreEmployment.containsKey(rs.getString("emp_id"))){
					String strCompanies = hmPreEmployment.get(rs.getString("emp_id"))+", "+rs.getString("company_name");
					hmPreEmployment.put(rs.getString("emp_id"), strCompanies);
				} else{
					hmPreEmployment.put(rs.getString("emp_id"), rs.getString("company_name"));
				}
				
			}
			rs.close();
			pst.close();
			
			
			Map<String,String>hmBankList=new LinkedHashMap<String,String>();
			pst = con.prepareStatement("select * from bank_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmBankList.put(rs.getString("bank_id"), rs.getString("bank_name"));
			}
			rs.close();
			pst.close();
			
			Map<String,String> hmEmpRef = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_references");
			
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("EJR/429--pst="+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if(hmEmpRef.containsKey(rs.getString("emp_id"))){
					String refName = hmEmpRef.get(rs.getString("emp_id"))+", "+rs.getString("ref_name");
					hmEmpRef.put(rs.getString("emp_id"), refName);
				} else{
					hmEmpRef.put(rs.getString("emp_id"), rs.getString("ref_name"));
				}
			}
			rs.close();
			pst.close();
			
			Map<String,String> skillNameMap = CF.getSkillNameMap(con);
			Map<String,String>hmEmpSkills=new LinkedHashMap<String,String>();
			pst = con.prepareStatement("select * from skills_description");
			rs = pst.executeQuery();
//			System.out.println("EJR/405--pst="+pst);
			while (rs.next()) {
				
				if(hmEmpSkills.containsKey(rs.getString("emp_id"))){
					String skillName = skillNameMap.get(rs.getString("skill_id"))+ ", "+hmEmpSkills.get(rs.getString("emp_id"));
					hmEmpSkills.put(rs.getString("emp_id"), skillName);
				} else{
					hmEmpSkills.put(rs.getString("emp_id"), skillNameMap.get(rs.getString("skill_id")));
				}
				
			}
			rs.close();
			pst.close();
			
			Map<String,String> hmDegreeName = CF.getDegreeNameMap(con);
			Map<String,String> hmEmpEducation = new HashMap<String, String>();
			pst = con.prepareStatement("SELECT * FROM education_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(hmEmpEducation.containsKey(rs.getString("emp_id"))){
					String eduName = hmDegreeName.get(rs.getString("education_id"))+", "+hmEmpEducation.get(rs.getString("emp_id"));
					hmEmpEducation.put(rs.getString("emp_id"), eduName);
				} else{
					hmEmpEducation.put(rs.getString("emp_id"), hmDegreeName.get(rs.getString("education_id")));
				}
			}
			rs.close();
			pst.close();
			
			Map<String,String> hmEmpLanguage = new HashMap<String, String>();
			pst = con.prepareStatement("SELECT * FROM languages_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(hmEmpLanguage.containsKey(rs.getString("emp_id"))){
					String language = rs.getString("language_name")+", "+hmEmpLanguage.get(rs.getString("emp_id"));
					hmEmpLanguage.put(rs.getString("emp_id"), language);
				} else{
					hmEmpLanguage.put(rs.getString("emp_id"), rs.getString("language_name"));
				}
				
			}
			rs.close();
			pst.close();
			
			String docRetriveLocation = CF.getStrDocRetriveLocation();
			Map<String,String> hmEmpDocument = new HashMap<String, String>();
			pst = con.prepareStatement("SELECT * FROM documents_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				StringBuilder strDoc = new StringBuilder();
				if(rs.getString("documents_file_name") != null){
					if(docRetriveLocation == null) {
//						System.out.println("EJR/457--if");
						strDoc.append("<a href=\""+request.getContextPath()+DOCUMENT_LOCATION+rs.getString("documents_file_name")+"\" title=\"Reference Document\">");
						strDoc.append("<i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>");
					} else {
//						System.out.println("EJR/461--else");
						strDoc.append("<a href=\""+docRetriveLocation+I_PEOPLE+"/"+I_DOCUMENT+"/"+I_ATTACHMENT+"/"+rs.getString("emp_id")+"/"+rs.getString("documents_file_name")+"\" title=\"Reference Document\">");
						strDoc.append("<i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>");
					}
				}
				
				if(hmEmpDocument.containsKey(rs.getString("emp_id")+"_DOC_NAME")){
					
					String docName = uF.showData(rs.getString("documents_name"), "")+strDoc.toString()+", "+hmEmpDocument.get(rs.getString("emp_id")+"_DOC_NAME");
					String docFile = strDoc.toString()+", "+hmEmpDocument.get(rs.getString("emp_id")+"_DOC_FILE");
//					hmEmpDocument.put(rs.getString("emp_id")+"_DOC_NAME", docName);
					hmEmpDocument.put(rs.getString("emp_id")+"_DOC_NAME", docName);
					hmEmpDocument.put(rs.getString("emp_id")+"_DOC_FILE", docFile);
					
				} else{
//					hmEmpDocument.put(rs.getString("emp_id")+"_DOC_NAME", rs.getString("documents_name"));
					hmEmpDocument.put(rs.getString("emp_id")+"_DOC_NAME", rs.getString("documents_name")+strDoc.toString());
					hmEmpDocument.put(rs.getString("emp_id")+"_DOC_FILE", strDoc.toString());
				}
			}
			rs.close();
			pst.close();
			
			Iterator<String> it1=hmEmpStEndMonth.keySet().iterator();
			Map<String,String> hmEmployeeExperience=new LinkedHashMap<String, String>();
			while(it1.hasNext()){
				
				String empid = it1.next();
				
				List<List<String>>alOuterEmpStEndMonth = hmEmpStEndMonth.get(empid);
				long datediffOuter=0;
				long datediffInner=0;
				long datediff=0;
				int noyear = 0,nomonth = 0,nodays = 0;
				for(int i=0;i<alOuterEmpStEndMonth.size();i++) {
					List<String>alInnerEmpStEndMonth=alOuterEmpStEndMonth.get(i);
					String stdt=alInnerEmpStEndMonth.get(0);
					String endDt=alInnerEmpStEndMonth.get(1);
					if(stdt!=null && endDt!=null && stdt!="" && endDt!="") {
						String datedif = uF.dateDifference(uF.showData(stdt, ""), DBDATE, uF.showData(endDt, ""), DBDATE);
						datediff = uF.parseToLong(datedif);
						datediffInner = datediff+datediffInner;
					} else {
						datediff=0;
						datediffInner=0;
					}
				}
				
				datediffOuter = datediffInner;
				datediffInner=0;
					
				noyear+=(int) (datediffOuter/365);
			    nomonth+=(int) ((datediffOuter%365)/30);
			    nodays+=(int) ((datediffOuter%365)%30);
			     
			    if(nodays>30){
			    	nomonth=nomonth+1;
			    }
			    if(nomonth>12){
			    	nomonth=nomonth-12;
			    	noyear=noyear+1;
			    }
			    	
			    String yearsLbl = " Years ";
			    if(noyear == 1) {
			    	yearsLbl = " Year ";
			    }
			    	
			    String monthLbl = " Months ";
			    if(nomonth == 1) {
			    	monthLbl = " Month ";
			    }
			    	
			    hmEmployeeExperience.put(empid, ""+noyear+yearsLbl+nomonth+monthLbl); 
//				System.out.println(empid+"--"+uF.showData((String)hmEmployeeExperience.get(empid), "N/A"));
			}
		
			sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id ");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
			if(getF_status()!=null && getF_status().length>0) {
				sbQuery.append(" and emp_status  in ("+getDataFromArray(getF_status())+") ");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            
            if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0)
            {
            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
            		
            }else {
            	 if(getF_level()!=null && getF_level().length>0){
                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
                 }
            	 if(getF_grade()!=null && getF_grade().length>0){
                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
                 }
			}
            if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
            sbQuery.append(" and joining_date <=? order by emp_fname, emp_lname");
//            System.out.println("EJR/621--sbQuery==>"+sbQuery.toString());
            pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getEndDate(), DATE_FORMAT));
//			System.out.println("pst=======>"+pst);
			int count=0;
			rs = pst.executeQuery();
			while(rs.next()){
				alInner = new ArrayList<String>();
				
				count++;
				alEmployees.add(rs.getString("emp_id"));
				
				alInner.add(Integer.toString(count));	//0
				alInner.add(rs.getString("empcode"));	//1
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				alInner.add(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));	//2
				alInner.add(uF.showData(hmOrg.get(rs.getString("org_id")), "-"));					//3
				alInner.add(uF.showData(hmWLocation.get(rs.getString("wlocation_id")), "-"));		//4
				alInner.add(uF.showData(hmDepart.get(rs.getString("depart_id")), "-"));				//5
				
				String strService = "";
				if(rs.getString("service_id")!=null && !rs.getString("service_id").equals("")){
					List<String> alList = Arrays.asList(rs.getString("service_id").split(","));
					for(String service : alList) {
						if(uF.parseToInt(service)>0) {
							if(strService.equals("")) {
								strService = uF.showData(hmServices.get(service), "-");
							} else {
								strService +=","+ uF.showData(hmServices.get(service), "-");
							}
						}
					}
				}
				
				alInner.add(strService); 		//6
				alInner.add(uF.showData(hmLevelMap.get(hmEmpLevelMap.get(rs.getString("emp_id"))), "-"));	//7
				alInner.add(uF.showData(hmEmpDesig.get(rs.getString("emp_id")), ""));						//8
				alInner.add(uF.showData(hmEmpCodeName.get(rs.getString("supervisor_emp_id")), "-"));		//9	
				alInner.add(uF.showData(hmEmpCodeName.get(rs.getString("hod_emp_id")), "-"));				//10
				
				alInner.add(uF.showData(rs.getString("emp_contactno_mob"), "-"));	//11
				alInner.add(uF.showData(rs.getString("emp_email") ,"-"));			//12
				alInner.add(uF.showData(rs.getString("emp_email_sec"),"-"));		//13
				
				alInner.add(rs.getString("emp_status"));							//14
				alInner.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));		//15
				alInner.add(uF.showData(hmEmployeeExperience.get(rs.getString("emp_per_id")), "N/A"));//total year of exp.	//16
				alInner.add(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, CF.getStrReportDateFormat()));		//17

				String strAge = "";
				if(rs.getString("emp_date_of_birth") != null && !rs.getString("emp_date_of_birth").equals("")) {
					strAge = uF.getTimeDurationBetweenDatesNoSpan(rs.getString("emp_date_of_birth"), DBDATE, ""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF, uF, request);
				}
			
				alInner.add(uF.showData(strAge, "-"));		//18
				alInner.add(uF.showData(uF.getGender(rs.getString("emp_gender")), "-"));		//19
				alInner.add(uF.showData(rs.getString("emp_address1_tmp"),"-")+","+uF.showData(rs.getString("emp_city_id_tmp"), "-"));		//20
				alInner.add(uF.showData(rs.getString("emp_address1"),"")+", "+uF.showData(rs.getString("emp_city_id"),"-"));				//21
				alInner.add(uF.showData(uF.getMaritalStatus(rs.getString("marital_status")), "-"));											//22
				
				if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("M")) {
					alInner.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id")+"_FATHER"), "-"));			//23
				} else if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("F") && rs.getString("marital_status")!=null && rs.getString("marital_status").equalsIgnoreCase("M")) {
					alInner.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id")+"_SPOUSE"), "-"));		//23
				} else {
					alInner.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id")+"_FATHER"), "-"));		//23
				}
				
				if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("M")){
					alInner.add(uF.showData(hmFamilyInfoContactNo.get(rs.getString("emp_per_id")+"_FATHER"), "-"));		//24
				}else if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("F") && rs.getString("marital_status")!=null && rs.getString("marital_status").equalsIgnoreCase("M")){
					alInner.add(uF.showData(hmFamilyInfoContactNo.get(rs.getString("emp_per_id")+"_SPOUSE"), "-"));		//24
				}else{
					alInner.add(uF.showData(hmFamilyInfoContactNo.get(rs.getString("emp_per_id")+"_FATHER"), "-"));		//24
				}
				
				alInner.add(uF.showData(rs.getString("emp_pan_no"),"-"));		//25
				
				alInner.add(uF.showData(rs.getString("emp_bank_acct_nbr"),"-"));	//26
			
				alInner.add(uF.showData(hmBankList.get(rs.getString("emp_bank_name")),"-"));	//27
				
				alInner.add(uF.showData(rs.getString("emp_pf_no"),"-"));		//28
				alInner.add(uF.showData(rs.getString("uid_no"),"-"));			//29
				alInner.add(uF.showData(rs.getString("uan_no"),"-"));			//30
				alInner.add(uF.showData(rs.getString("emp_esic_no"),"-"));		//31
				alInner.add(uF.showData(rs.getString("biometrix_id"),"-"));		//32
				alInner.add(uF.showData(hmPreEmployment.get(rs.getString("emp_per_id")), "-"));		//33
				alInner.add(uF.showData(hmEmpRef.get(rs.getString("emp_per_id")), "-"));			//34
				alInner.add(uF.showData(hmEmpSkills.get(rs.getString("emp_per_id")), "-"));			//35
				alInner.add(uF.showData(hmEmpEducation.get(rs.getString("emp_per_id")), "-"));		//36
				alInner.add(uF.showData(hmEmpLanguage.get(rs.getString("emp_per_id")), "-"));		//37
				alInner.add(uF.showData(hmEmpDocument.get(rs.getString("emp_per_id")+"_DOC_NAME"), "-"));		//38
				alInner.add(uF.showData(hmEmpDocument.get(rs.getString("emp_per_id")+"_DOC_FILE"), "-"));		//39
				
				reportList.add(alInner);
			}
			rs.close();
			pst.close();
						
			request.setAttribute("reportList", reportList);
			
			
		} catch (Exception e) {
			e.printStackTrace();  
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	private String getDataFromArray(String[] strVal) {
		StringBuilder sb=new StringBuilder();
		for(int i=0;strVal!=null && i<strVal.length;i++){
			if(i==0){
				sb.append("'"+strVal[i]+"'");
				
			}else{
				sb.append(",'"+strVal[i]+"'");
			}
			if(strVal[i].equals("PROBATION")){
				sb.append(",'ACTIVE'");
			}
		}
		
		return sb.toString();
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
		
		alFilter.add("DEPARTMENT");
		if(getF_department()!=null) {
			String strDepartment="";
			int k=0;
			for(int i=0;departmentList!=null && i<departmentList.size();i++) {
				for(int j=0;j<getF_department().length;j++) {
					if(getF_department()[j].equals(departmentList.get(i).getDeptId())) {
						if(k==0) {
							strDepartment=departmentList.get(i).getDeptName();
						} else {
							strDepartment+=", "+departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if(strDepartment!=null && !strDepartment.equals("")) {
				hmFilter.put("DEPARTMENT", strDepartment);
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}
		
		alFilter.add("SERVICE");
		if(getF_service()!=null) {
			String strService="";
			int k=0;
			for(int i=0;serviceList!=null && i<serviceList.size();i++) {
				for(int j=0;j<getF_service().length;j++) {
					if(getF_service()[j].equals(serviceList.get(i).getServiceId())) {
						if(k==0) {
							strService=serviceList.get(i).getServiceName();
						} else {
							strService+=", "+serviceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strService!=null && !strService.equals("")) {
				hmFilter.put("SERVICE", strService);
			} else {
				hmFilter.put("SERVICE", "All SBUs");
			}
		} else {
			hmFilter.put("SERVICE", "All SBUs");
		}
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				for(int j=0;j<getF_level().length;j++) {
					if(getF_level()[j].equals(levelList.get(i).getLevelId())) {
						if(k==0) {
							strLevel=levelList.get(i).getLevelCodeName();
						} else {
							strLevel+=", "+levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Level's");
			}
		} else {
			hmFilter.put("LEVEL", "All Level's");
		}
		
		alFilter.add("GRADE");
		if (getF_grade() != null) {
			String strgrade = "";
			int k = 0;
			for (int i = 0; gradeList != null && i < gradeList.size(); i++) {
				for (int j = 0; j < getF_grade().length; j++) {
					if (getF_grade()[j].equals(gradeList.get(i).getGradeId())) {
						if (k == 0) {
							strgrade = gradeList.get(i).getGradeCode();
						} else {
							strgrade += ", " + gradeList.get(i).getGradeCode();
						}
						k++;
					}
				}
			}
			if (strgrade != null && !strgrade.equals("")) {
				hmFilter.put("GRADE", strgrade);
			} else {
				hmFilter.put("GRADE", "All Grade's");
			}
		} else {
			hmFilter.put("GRADE", "All Grade's");
		}
		
		alFilter.add("EMPTYPE");
		if (getF_employeType() != null) {
			String stremptype = "";
			int k = 0;
			for (int i = 0; employementTypeList != null && i < employementTypeList.size(); i++) {
				for (int j = 0; j < getF_employeType().length; j++) {
					if (getF_employeType()[j].equals(employementTypeList.get(i).getEmpTypeId())) {
						if (k == 0) {
							stremptype = employementTypeList.get(i).getEmpTypeName();
						} else {
							stremptype += ", " + employementTypeList.get(i).getEmpTypeName();
						}
						k++;
					}
				}
			}
			if (stremptype != null && !stremptype.equals("")) {
				hmFilter.put("EMPTYPE", stremptype);
			} else {
				hmFilter.put("EMPTYPE", "All Employee Type");
			}
		} else {
			hmFilter.put("EMPTYPE", "All Employee Type");
		}
		
		alFilter.add("STATUS");
		if(getF_status()!=null){
			String strStatus="";
			int k=0;
			for(int i=0;empStatusList!=null && i<empStatusList.size();i++){
				for(int j=0;j<getF_status().length;j++){
					if(getF_status()[j].equals(empStatusList.get(i).getStatusId())){
						if(k==0){
							strStatus=empStatusList.get(i).getStatusName();
						}else{
							strStatus+=", "+empStatusList.get(i).getStatusName();
						}
						k++;
					}
				}
			}
			if(strStatus!=null && !strStatus.equals("")){
				hmFilter.put("STATUS", strStatus);
			}else{
				hmFilter.put("STATUS", "All Status");
			}
		}else{
			hmFilter.put("STATUS", "All Status");
		}
		
		alFilter.add("AS_OF_DATE");
		hmFilter.put("AS_OF_DATE", uF.getDateFormat(getEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
		
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrSbu() {
		return strSbu;
	}

	public void setStrSbu(String strSbu) {
		this.strSbu = strSbu;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getStrGrade() {
		return strGrade;
	}

	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}

	public String getStrEmployeType() {
		return strEmployeType;
	}

	public void setStrEmployeType(String strEmployeType) {
		this.strEmployeType = strEmployeType;
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

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public String[] getF_employeType() {
		return f_employeType;
	}

	public void setF_employeType(String[] f_employeType) {
		this.f_employeType = f_employeType;
	}

	public String[] getF_grade() {
		return f_grade;
	}

	public void setF_grade(String[] f_grade) {
		this.f_grade = f_grade;
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

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public List<FillEmploymentType> getEmployementTypeList() {
		return employementTypeList;
	}

	public void setEmployementTypeList(List<FillEmploymentType> employementTypeList) {
		this.employementTypeList = employementTypeList;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public List<FillBank> getBankList() {
		return bankList;
	}

	public void setBankList(List<FillBank> bankList) {
		this.bankList = bankList;
	}

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
	}

	public String getStrStatus() {
		return strStatus;
	}

	public void setStrStatus(String strStatus) {
		this.strStatus = strStatus;
	}

	public String[] getF_status() {
		return f_status;
	}

	public void setF_status(String[] f_status) {
		this.f_status = f_status;
	}

	public List<FillEmployeeStatus> getEmpStatusList() {
		return empStatusList;
	}

	public void setEmpStatusList(List<FillEmployeeStatus> empStatusList) {
		this.empStatusList = empStatusList;
	}
	
}
