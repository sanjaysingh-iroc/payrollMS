package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillBank;
import com.konnect.jpms.select.FillDepartment;
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

public class EmployeeWorkForceReport extends ActionSupport implements ServletRequestAware, IStatements {
	
	public static String strDetails;
	
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
	
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	String[] f_employeType;
	String[] f_grade;
	
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillEmploymentType> employementTypeList;
	List<FillGrade> gradeList;
	private List<FillBank> bankList;
	
	String exportType;
	
	CommonFunctions CF = null;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(TITLE, "Workforce Joinings");
		request.setAttribute(PAGE, "/jsp/reports/WorkForceJoinReport.jsp");
		
		UtilityFunctions uF = new UtilityFunctions();
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		*/
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
		
		viewWorkForceJoin(uF);

		return loadWorkForceJoin(uF);

	}
	
	public String loadWorkForceJoin(UtilityFunctions uF) {
		
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
		
		alFilter.add("AS_OF_DATE");
		hmFilter.put("AS_OF_DATE", uF.getDateFormat(getEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	public String viewWorkForceJoin(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
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
          
			pst = con.prepareStatement(sbQuery.toString());
			
//			System.out.println("pst==>"+pst);
			
			
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
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getEndDate(), DATE_FORMAT));

//			System.out.println("pst=======>"+pst);
			rs = pst.executeQuery();
		
			Map<String,List<List<String>>>hmEmpStEndMonth=new LinkedHashMap<String,List<List<String>>>();
			
			while(rs.next()){
				
				List<List<String>>alOuterEmpStEndMonth = hmEmpStEndMonth.get(rs.getString("emp_id"));
				if(alOuterEmpStEndMonth==null)alOuterEmpStEndMonth = new ArrayList<List<String>>();
				
				List<String>alInnerEmpStEndMonth=new ArrayList<String>();
				
				alInnerEmpStEndMonth.add(uF.showData(rs.getString("from_date"), ""));
				alInnerEmpStEndMonth.add(uF.showData(rs.getString("to_date"), ""));
				
				alOuterEmpStEndMonth.add(alInnerEmpStEndMonth);
				hmEmpStEndMonth.put(uF.showData(rs.getString("emp_id"),""), alOuterEmpStEndMonth);
			}
			rs.close();
			pst.close();
//			System.out.println("pst--->"+pst);
			
			Map<String, String> hmEmpProjects = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select ped.emp_id, p.pro_name, p.pro_id from project_emp_details ped, projectmntnc p where ped.pro_id=p.pro_id and ? between start_date and deadline order by ped.pro_id, ped.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getEndDate(), DATE_FORMAT));
//			System.out.println("EWFR/560---pst===>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				String projectName = null;
				if(!hmEmpProjects.containsKey(rs.getString("emp_id"))){
					projectName = rs.getString("pro_name");
					
				} else{
					projectName = rs.getString("pro_name") + " , " + hmEmpProjects.get(rs.getString("emp_id"));
				}
				hmEmpProjects.put(rs.getString("emp_id"), projectName);
			}
			rs.close();
			pst.close();
			
			//bankList = new FillBank(request).fillBankName();
			Map<String,String>hmBankList=new LinkedHashMap<String,String>();
			pst = con.prepareStatement("select * from bank_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmBankList.put(rs.getString("bank_id"), rs.getString("bank_name"));
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
//					System.out.println(empid+"--"+uF.showData((String)hmEmployeeExperience.get(empid), "N/A"));
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
            	//sbQuery.append(" and grade_id in ( " +StringUtils.join(getF_grade(), ",")+" ) and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");	
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
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getEndDate(), DATE_FORMAT));
//			System.out.println("pst=======>"+pst);
			int count=0;
			rs = pst.executeQuery();
			while(rs.next()){
				alInner = new ArrayList<String>();
				//alEmployees=new ArrayList<String>();
				count++;
				alEmployees.add(rs.getString("emp_id"));
				
				alInner.add(Integer.toString(count));
				alInner.add(rs.getString("empcode"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				
				alInner.add(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				alInner.add(uF.showData(hmOrg.get(rs.getString("org_id")), "-"));
				alInner.add(uF.showData(hmWLocation.get(rs.getString("wlocation_id")), "-"));
				alInner.add(uF.showData(hmDepart.get(rs.getString("depart_id")), "-"));
				
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
				
				alInner.add(strService); 
				alInner.add(uF.showData(hmLevelMap.get(hmEmpLevelMap.get(rs.getString("emp_id"))), "-"));
				alInner.add(uF.showData(hmEmpDesig.get(rs.getString("emp_id")), ""));
				alInner.add(uF.showData(hmEmpCodeName.get(rs.getString("supervisor_emp_id")), "-"));
				alInner.add(uF.showData(hmEmpCodeName.get(rs.getString("hod_emp_id")), "-"));
				
				alInner.add(uF.showData(rs.getString("emp_contactno_mob"), "-"));
				alInner.add(uF.showData(rs.getString("emp_email") ,"-"));
				alInner.add(uF.showData(rs.getString("emp_email_sec"),"-"));
				
				alInner.add(rs.getString("emp_status"));
				alInner.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(uF.showData(hmEmployeeExperience.get(rs.getString("emp_per_id")), "N/A"));//total year of exp.
				alInner.add(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, CF.getStrReportDateFormat()));

				String strAge = "";
				if(rs.getString("emp_date_of_birth") != null && !rs.getString("emp_date_of_birth").equals("")) {
				 strAge = uF.getTimeDurationBetweenDatesNoSpan(rs.getString("emp_date_of_birth"), DBDATE, ""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF, uF, request);
				}
			
				alInner.add(uF.showData(strAge, "-"));
				alInner.add(uF.showData(uF.getGender(rs.getString("emp_gender")), "-"));
				alInner.add(uF.showData(rs.getString("emp_address1_tmp"),"-")+","+uF.showData(rs.getString("emp_city_id_tmp"), "-"));
				alInner.add(uF.showData(rs.getString("emp_address1"),"")+", "+uF.showData(rs.getString("emp_city_id"),"-"));
				alInner.add(uF.showData(uF.getMaritalStatus(rs.getString("marital_status")), "-"));
				
				if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("M")) {
					alInner.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id")+"_FATHER"), "-"));
				} else if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("F") && rs.getString("marital_status")!=null && rs.getString("marital_status").equalsIgnoreCase("M")) {
					alInner.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id")+"_SPOUSE"), "-"));
				} else {
					alInner.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id")+"_FATHER"), "-"));
				}
				
				if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("M")){
					alInner.add(uF.showData(hmFamilyInfoContactNo.get(rs.getString("emp_per_id")+"_FATHER"), "-"));
				}else if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("F") && rs.getString("marital_status")!=null && rs.getString("marital_status").equalsIgnoreCase("M")){
					alInner.add(uF.showData(hmFamilyInfoContactNo.get(rs.getString("emp_per_id")+"_SPOUSE"), "-"));
				}else{
					alInner.add(uF.showData(hmFamilyInfoContactNo.get(rs.getString("emp_per_id")+"_FATHER"), "-"));
				}
				
				alInner.add(uF.showData(rs.getString("emp_pan_no"),"-"));
				
				alInner.add(uF.showData(rs.getString("emp_bank_acct_nbr"),"-"));
			
				alInner.add(uF.showData(hmBankList.get(rs.getString("emp_bank_name")),"-"));
				
				alInner.add(uF.showData(rs.getString("emp_pf_no"),"-"));
				alInner.add(uF.showData(rs.getString("uid_no"),"-"));
				alInner.add(uF.showData(rs.getString("uan_no"),"-"));
				alInner.add(uF.showData(rs.getString("emp_esic_no"),"-"));
				alInner.add(uF.showData(rs.getString("biometrix_id"),"-"));
				alInner.add(uF.showData(hmEmpProjects.get(rs.getString("emp_id")), "-"));
				
				
				reportList.add(alInner);
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("reportList", reportList);
			
			
		} catch (Exception e) {
			e.printStackTrace();  
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public static String getStrDetails() {
		return strDetails;
	}

	public static void setStrDetails(String strDetails) {
		EmployeeWorkForceReport.strDetails = strDetails;
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
	

}
