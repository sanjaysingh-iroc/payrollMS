package com.konnect.jpms.reports.factory;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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
 
public class AdultWorkerReport extends ActionSupport implements ServletRequestAware, IStatements,ServletResponseAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AdultWorkerReport.class);
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	
	String pdfGeneration;
	
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
	
	String exportType;	
	String startDate;
	 
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, PAdultWorkerReport);
		request.setAttribute(TITLE, TAdultWorer);
		
		if(getF_org()==null || getF_org().trim().equals("")) {
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
		
		viewAdultReport(uF);			
		return loadAdultReport(uF);
 
	}
	
	
	public String loadAdultReport(UtilityFunctions uF){
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
		hmFilter.put("AS_OF_DATE", uF.getDateFormat(getStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	public String viewAdultReport(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			// for export pdf
			List<List<String>> alPdf = new ArrayList<List<String>>();
			List<String> alInnerPdf = new ArrayList<String>();
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
			if(getStartDate() == null){
				String strCurrentDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),DBDATE,DATE_FORMAT);
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strCurrentDate, DATE_FORMAT, "MM"))-1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strCurrentDate, DATE_FORMAT, "yyyy")));
				int nMonthStart = cal.getActualMinimum(Calendar.DATE);
				int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
				String nStDate = nMonthStart < 10 ? "0"+nMonthStart : ""+nMonthStart;
				String mnth =""+ (uF.parseToInt(uF.getDateFormat(strCurrentDate, DATE_FORMAT, "MM"))< 10 ? "0" +uF.parseToInt(uF.getDateFormat(strCurrentDate, DATE_FORMAT, "MM")) : uF.parseToInt(uF.getDateFormat(strCurrentDate, DATE_FORMAT, "MM")));
				String strDateStart =  nStDate+"/"+mnth+"/"+cal.get(Calendar.YEAR);				
				setStartDate(strDateStart);				
			}
			
			
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			if(hmEmpCodeDesig == null) hmEmpCodeDesig = new HashMap<String, String>();
			
			Map<String, String> hmFamilyInfo = new HashMap<String, String>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_family_members where member_type in ('SPOUSE', 'FATHER') ");

			if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
			}
			
			if (getF_employeType() != null && getF_employeType().length > 0) {
					sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
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
            if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(") ");
			}
           
			pst = con.prepareStatement(sbQuery.toString());
			//System.out.println("pst0==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				hmFamilyInfo.put(rs.getString("emp_id")+"_"+rs.getString("member_type"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details where emp_per_id > 0 and is_alive=true ");
			
			if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(" and emp_per_id in (select emp_id from employee_official_details where emp_id > 0 ");
			}
			
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
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
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0)
            {
            	sbQuery.append(" and grade_id in ( " +StringUtils.join(getF_grade(), ",")+" ) and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");	
            }else {
            	 if(getF_level()!=null && getF_level().length>0){
                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
                 }
            	 if(getF_grade()!=null && getF_grade().length>0){
                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
                 }
			}
            /*if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }*/
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
            if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(") ");
			}
            sbQuery.append("and joining_date <= ? ");
            sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStartDate(), DATE_FORMAT));
			//System.out.println("pst1===>"+pst);
			rs = pst.executeQuery();
			
			int nCount = 0;
			DateTime now = new DateTime();
			while(rs.next()){
				
				
				int years =  0;
				if(rs.getString("emp_date_of_birth")!=null) {
					DateMidnight birthdate = new DateMidnight(uF.parseToInt(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "yyyy")), uF.parseToInt(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "MM")), uF.parseToInt(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "dd")));
					Years age = Years.yearsBetween(birthdate, now);
					years = age.getYears();
				}
				if(years<18) {
					continue;
				}
				alInner = new ArrayList<String>();
				alInner.add(""+ ++nCount);
				alInner.add(uF.showData(rs.getString("empcode"),""));
				/*String middleName = uF.showData(rs.getString("emp_mname"),"");
				if(middleName.length()>0) {
					middleName = " "+middleName;
				}*/
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				alInner.add(uF.showData(rs.getString("emp_fname"),"")+strEmpMName+" "+uF.showData(rs.getString("emp_lname"),""));
				alInner.add(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(""+ years);
				alInner.add(uF.showData(uF.getGender(rs.getString("emp_gender")),""));
				alInner.add(uF.showData(rs.getString("emp_address1"),"")+", "+uF.showData(rs.getString("emp_city_id"),""));

				if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("M")) {
					alInner.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id")+"_FATHER"), ""));
				} else if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("F") && rs.getString("marital_status")!=null && rs.getString("marital_status").equalsIgnoreCase("M")) {
					alInner.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id")+"_SPOUSE"), ""));
				} else {
					alInner.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id")+"_FATHER"), ""));
				}
				
				alInner.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				alInner.add("");
				alInner.add(uF.showData(hmEmpCodeDesig.get(rs.getString("emp_per_id")), ""));
				
				al.add(alInner);
				
				alInnerPdf = new ArrayList<String>();
				alInnerPdf.add(""+ nCount);
				alInnerPdf.add(uF.showData(rs.getString("empcode"),""));
				
				
				
				alInnerPdf.add(uF.showData(rs.getString("emp_fname"),"")+strEmpMName+" "+uF.showData(rs.getString("emp_lname"),""));
				alInnerPdf.add(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT));
				alInnerPdf.add(""+ years);
				alInnerPdf.add(uF.showData(rs.getString("emp_gender"),""));
				alInnerPdf.add(uF.showData(rs.getString("emp_address1"),"")+", "+uF.showData(rs.getString("emp_city_id"),""));

				if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("M")){
					alInnerPdf.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id")+"_FATHER"), ""));
				}else if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("F") && rs.getString("marital_status")!=null && rs.getString("marital_status").equalsIgnoreCase("M")){
					alInnerPdf.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id")+"_SPOUSE"), ""));
				}else{
					alInnerPdf.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id")+"_FATHER"), ""));
				}
				
				alInnerPdf.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				alInnerPdf.add("");
				alInnerPdf.add(uF.showData(hmEmpCodeDesig.get(rs.getString("emp_per_id")), ""));

				alPdf.add(alInnerPdf);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", al);
			
			if(getPdfGeneration()!=null && getPdfGeneration().equalsIgnoreCase("true")){
				PdfAdultWorkerReport objPdf = new PdfAdultWorkerReport(alPdf,response);
				objPdf.exportPdf();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
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
	
	private HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {

        this.response=response;
		
	}
	
	public String getPdfGeneration() {
		return pdfGeneration;
	}


	public void setPdfGeneration(String pdfGeneration) {
		this.pdfGeneration = pdfGeneration;
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


	public String getExportType() {
		return exportType;
	}


	public void setExportType(String exportType) {
		this.exportType = exportType;
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
	
	public String[] getF_grade() {
		return f_grade;
	}

	public void setF_grade(String[] f_grade) {
		this.f_grade = f_grade;
	}
	
	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}
	
	public String[] getF_employeType() {
		return f_employeType;
	}

	public void setF_employeType(String[] f_employeType) {
		this.f_employeType = f_employeType;
	}

	public List<FillEmploymentType> getEmployementTypeList() {
		return employementTypeList;
	}

	public void setEmployementTypeList(List<FillEmploymentType> employementTypeList) {
		this.employementTypeList = employementTypeList;
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
	
	public String getStartDate() {
		return startDate;
	}


	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
}




class PdfAdultWorkerReport{
	
	private Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
	private Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
	private Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 11,Font.BOLD);
	private Font small = new Font(Font.FontFamily.TIMES_ROMAN,7);
	private Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
	
	List reportList;
	PdfAdultWorkerReport(List reportList, HttpServletResponse response){
		this.reportList = reportList;
		this.response=response;
	}
	

	public void exportPdf(){
		
		
		 ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
		 Document document = new Document(PageSize.LETTER.rotate());
		
		 try{
				PdfWriter.getInstance(document, bos);
				document.open();
				
				Paragraph blankSpace = new Paragraph(" ");
				
				Paragraph title = new Paragraph("FORM - 12",heading);
				title.setAlignment(Element.ALIGN_CENTER);
				Paragraph subTitle = new Paragraph("Prescribed under Rule 107",heading);
				subTitle.setAlignment(Element.ALIGN_CENTER);
				Paragraph registerName = new Paragraph("Register of adult workers",heading);
				registerName.setAlignment(Element.ALIGN_CENTER);
				
				PdfPTable table = new PdfPTable(13);
				table.setWidthPercentage(100);
				int[] cols = {4,7,7,7,8,8,7,7,7,10,10,9,9};
				table.setWidths(cols);
				
				PdfPCell srNo = new PdfPCell(new Paragraph("Sl.No",normal));
				srNo.disableBorderSide(Rectangle.BOTTOM);
				PdfPCell name = new PdfPCell(new Paragraph("Name",normal));
				name.disableBorderSide(Rectangle.BOTTOM);
				PdfPCell DOB = new PdfPCell(new Paragraph("Date of Birth",normal));
				DOB.disableBorderSide(Rectangle.BOTTOM);
				PdfPCell sex = new PdfPCell(new Paragraph("Sex",normal));
				sex.disableBorderSide(Rectangle.BOTTOM);
				PdfPCell address = new PdfPCell(new Paragraph("Residential Address",normal));
				address.disableBorderSide(Rectangle.BOTTOM);
				PdfPCell middleName = new PdfPCell(new Paragraph("Father’s/ Husband’s name",normal));
				middleName.disableBorderSide(Rectangle.BOTTOM);
				PdfPCell appointmentDate = new PdfPCell(new Paragraph("Date of appointment",normal));
				appointmentDate.disableBorderSide(Rectangle.BOTTOM);
				PdfPCell groupName = new PdfPCell(new Paragraph("Group to which worker belongs",normal));
				groupName.setColspan(2);
				groupName.setVerticalAlignment(Element.ALIGN_MIDDLE);
				PdfPCell shiftNumber = new PdfPCell(new Paragraph("Number of relay if working in shifts",normal));
				shiftNumber.disableBorderSide(Rectangle.BOTTOM);
				PdfPCell adolescentCerti = new PdfPCell(new Paragraph("Adolescent if certified as adult",normal));
				adolescentCerti.setColspan(2);
				adolescentCerti.setVerticalAlignment(Element.ALIGN_MIDDLE);
				PdfPCell remarks = new PdfPCell(new Paragraph("Remarks",normal));
				remarks.disableBorderSide(Rectangle.BOTTOM);
								
				
				PdfPCell alphabet = new PdfPCell(new Paragraph("Alphabet Assigned",normal));
				PdfPCell workNature = new PdfPCell(new Paragraph("Nature of work",normal));
				
				PdfPCell blank1 = new PdfPCell(new Paragraph(" ",normal));
				blank1.disableBorderSide(Rectangle.TOP);
				PdfPCell blank2 = new PdfPCell(new Paragraph(" ",normal));
				blank2.disableBorderSide(Rectangle.TOP);
				
				PdfPCell dateAndNoOfCerti = new PdfPCell(new Paragraph("Number & date of certificate of fitness",normal));
				PdfPCell sectionNo = new PdfPCell(new Paragraph("Number under section 68",normal));
				
				table.addCell(srNo);
				table.addCell(name);
				table.addCell(DOB);
				table.addCell(sex);
				table.addCell(address);
				table.addCell(middleName);
				table.addCell(appointmentDate);
				table.addCell(groupName);
				table.addCell(shiftNumber);
				table.addCell(adolescentCerti);
				table.addCell(remarks);
				for(int i=0;i<7;i++){
					PdfPCell cell = new PdfPCell(new Paragraph(" "));
					cell.disableBorderSide(Rectangle.TOP);
					table.addCell(cell);
				}
				table.addCell(alphabet);
				table.addCell(workNature);
				table.addCell(blank1);
				table.addCell(dateAndNoOfCerti);
				table.addCell(sectionNo);
				table.addCell(blank2);	
				
				for(int a=0;a<reportList.size();a++){
				
					List<String> alInner=(List)reportList.get(a);
					
				for(int j=1;j<=13;j++){
					PdfPCell cell1 = new PdfPCell(new Paragraph(alInner.get(j),normal));
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell1);
				}				
				
				}
				for(int k=0;k<260;k++){
					PdfPCell cell2 = new PdfPCell(new Paragraph(" ",normal));
					cell2.disableBorderSide(Rectangle.BOTTOM);
					cell2.disableBorderSide(Rectangle.TOP);
					table.addCell(cell2);
				}
				
				for(int l=0;l<13;l++){
					PdfPCell lastCell = new PdfPCell(new Paragraph(" "));
					lastCell.disableBorderSide(Rectangle.TOP);
					table.addCell(lastCell);
				}
				
				
				document.add(title);
				document.add(subTitle);
				document.add(blankSpace);
				document.add(registerName);
				document.add(blankSpace);
				document.add(table);				
				
				
				document.close();
				
				
				response.setContentType("application/pdf");         
				 response.setContentLength(bos.size());
				 response.setHeader("Content-Disposition", "attachment; filename=AdultWorkerReport.pdf");
				
				 ServletOutputStream out = response.getOutputStream();         
				 bos.writeTo(out);         
				 out.flush();      
				 bos.close();
				 out.close();
					
		}catch(Exception e){
			e.printStackTrace();
		}
	
		
	}
	
	public List<String> getHeadings(){
		List<String> headings = new ArrayList<String>();
		headings.add("Sr.NO");
		headings.add("Name");
		headings.add("Father’s / Mother’s name");
		headings.add("Residential address of the worker");
		headings.add("Date of birth");
		headings.add("Date of first employment");
		headings.add("No. of certificate and its date");
		headings.add("Token No. giving reference to certificate");
		headings.add("Letter of groups as in Form");
		headings.add("No. of relay, if working in shifts");
		headings.add("Remarks");		
		
		return headings;
	}
	
	private HttpServletResponse response;
	
}
