package com.konnect.jpms.payroll;

import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.export.payroll.ExcelSheetDesign;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycleDuration;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class OtherEarningForm extends ActionSupport implements ServletRequestAware, IStatements , ServletResponseAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	String strUserType = null;

	CommonFunctions CF = null;
	String profileEmpId;

	String strLocation;
	String strDepartment;
	String strLevel;
	String strGrade;
	String strEmployeType;
	String salaryheadname;
	String exceldownload;
	
	String[] f_strWLocation;
	String[] f_level;
	String[] f_department;
//	String[] f_service;
	String paycycle;
	String[] f_grade;
	String[] f_employeType;

	List<FillPayCycles> paycycleList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillWLocation> wLocationList;
	List<FillSalaryHeads> salaryHeadList;

	List<FillOrganisation> orgList;

	String f_salaryhead;
	String f_org;
	
	String strPaycycleDuration;
	List<FillPayCycleDuration> paycycleDurationList;
	
	List<FillGrade> gradeList;
	List<FillEmploymentType> employementTypeList;

	String strBaseUserType = null;
	String strAction = null; 
	
	public String execute() throws Exception {

		try {
			session = request.getSession();
			request.setAttribute(PAGE, "/jsp/payroll/OtherEarningForm.jsp");
			request.setAttribute(TITLE, "Variable Form");
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if (CF == null) return LOGIN;
			
			System.out.println("getExcelDownload==>"+getExceldownload());
			UtilityFunctions uF = new UtilityFunctions();

			strEmpId = (String) session.getAttribute(EMPID);
			strUserType = (String) session.getAttribute(USERTYPE);
			strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);//Created By Dattatray 13-04-2022
			//Created By Dattatray 13-06-2022
			strAction = request.getServletPath();
			if(strAction!=null) {
				strAction = strAction.replace("/","");
			}
//			boolean isView = CF.getAccess(session, request, uF);
//			if (!isView) {
//				request.setAttribute(PAGE, PAccessDenied);
//				request.setAttribute(TITLE, TAccessDenied);
//				return ACCESS_DENIED;
//			}
			
			if(uF.parseToInt(getF_org()) == 0){
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
			if(getStrLevel() != null && !getStrLevel().equals("")) {
				setF_level(getStrLevel().split(","));
			} else {
				setF_level(null);
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
			
			if(getStrPaycycleDuration()==null || getStrPaycycleDuration().trim().equals("") || getStrPaycycleDuration().trim().equalsIgnoreCase("")){
				setStrPaycycleDuration("M");
			}
			
			request.setAttribute("salaryStructure", CF.getStrSalaryStructure());
			int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
			if (nSalaryStrucuterType == S_GRADE_WISE) {
				salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsByVariblesForGrade(true,null,getF_org());
				if ((getF_salaryhead() == null || getF_salaryhead().equals("")) && salaryHeadList != null && !salaryHeadList.isEmpty()) {
					setF_salaryhead(salaryHeadList.get(0).getSalaryHeadId());
				}
				viewOtherEarningByGrade(uF);
			} else {
				salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsByVaribles(true,null,getF_org());
				if ((getF_salaryhead() == null || getF_salaryhead().equals("")) && salaryHeadList != null && !salaryHeadList.isEmpty()) {
					setF_salaryhead(salaryHeadList.get(0).getSalaryHeadId());
				}
				
				viewOtherEarning(uF);
			}
			
			loadEarning(uF);

		} catch (Exception e) {
			e.printStackTrace();
		}
		loadPageVisitAuditTrail();
		return LOAD;
	}

	//Created By Dattatray 13-06-2022
			private void loadPageVisitAuditTrail() {
				Connection con=null;
				Database db = new Database();
				db.setRequest(request);
				UtilityFunctions uF=new UtilityFunctions();
				try {
					con = db.makeConnection(con);
//					Map<String, String> hmEmpProfile = CF.getEmpNameMap(con,null,get);
					StringBuilder builder = new StringBuilder();
					builder.append("Filter : ");
					builder.append("\nDuration : "+getStrPaycycleDuration());
					builder.append("\nOrganisation : "+getF_org());
					builder.append("\nPaycycle : "+getPaycycle());
					builder.append("\nLocation : "+StringUtils.join(getF_strWLocation(),","));
					builder.append("\nDepartment : "+StringUtils.join(getF_department(),","));
					builder.append("\nLevel : "+StringUtils.join(getF_level(),","));
					builder.append("\nEmployee Type : "+getF_employeType());
					builder.append("\nGrade : "+getF_grade());
					builder.append("\nSalary Head : "+getSalaryheadname());
					
					
					CF.pageVisitAuditTrail(con,CF,uF, strEmpId, strAction, strBaseUserType, builder.toString());
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}finally {
					db.closeConnection(con);
				}
				
			}
	public void loadEarning(UtilityFunctions uF) {

		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
 
		paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();
		paycycleList = new FillPayCycles(getStrPaycycleDuration(),request).fillPayCycles(CF, getF_org());

		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		employementTypeList = new FillEmploymentType().fillEmploymentType(request);
		
		getSelectedFilter(uF);

	}
	
	public String viewOtherEarningByGrade(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			String[] strPayCycleDates;			
			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
				String str = URLDecoder.decode(getPaycycle());
				strPayCycleDates = str.split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycleUsingDurationByOrg(con, CF.getStrTimeZone(), CF, getF_org(),getStrPaycycleDuration(), request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
			}   
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive = true and joining_date<= ? ");
			if(getStrPaycycleDuration()!=null){
				sbQuery.append(" and eod.paycycle_duration ='"+getStrPaycycleDuration()+"'");
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
			
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "','") + "') ");
			}
			 
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
//            if(getF_service()!=null && getF_service().length>0){
//                sbQuery.append(" and (");
//                for(int i=0; i<getF_service().length; i++){
//                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
//                    if(i<getF_service().length-1){
//                        sbQuery.append(" OR "); 
//                    }
//                }
//                sbQuery.append(" ) ");
//                
//            } 
            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            sbQuery.append(" and eod.emp_id in (select esd.emp_id from emp_salary_details esd, (select max(effective_date) as max_date, emp_id " +
            		" from emp_salary_details where isdisplay = true and is_approved=true and effective_date <=? and salary_head_id=? group by emp_id ) as b where esd.effective_date = b.max_date " +
            		"and b.emp_id = esd.emp_id and isdisplay = true and is_approved=true and esd.salary_head_id=? and esd.effective_date <=?)");
			sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_salaryhead()));
			pst.setInt(4, uF.parseToInt(getF_salaryhead()));
			pst.setDate(5,  uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			List<List<String>> alEmpReport = new ArrayList<List<String>>();
			List<String> alEmp = new ArrayList<String>();
			while (rs.next()) {
				List<String> alEmpReportInner = new ArrayList<String>();
				alEmpReportInner.add(rs.getString("emp_per_id"));
				
				//String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				String strEmpName = rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname");
				alEmpReportInner.add(strEmpName);
				
				alEmpReportInner.add(rs.getString("empcode"));

				alEmpReport.add(alEmpReportInner);
				
				if(!alEmp.contains(rs.getString("emp_per_id")) && uF.parseToInt(rs.getString("emp_per_id")) > 0){
					alEmp.add(rs.getString("emp_per_id"));
				}
			}
			rs.close();
			pst.close();

			if(alEmp.size() > 0){
				String strEmpIds = StringUtils.join(alEmp.toArray(),",");
				
				pst = con.prepareStatement("select * from otherearning_individual_details where emp_id in ("+strEmpIds+") and paid_from = ? and paid_to=? and pay_paycycle=? and salary_head_id=?");
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
				pst.setInt(4, uF.parseToInt(getF_salaryhead()));
				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
	
				Map<String, String> hmOtherearning = new HashMap<String, String>();
				Map<String, String> hmOtherearningId = new HashMap<String, String>();
				Map<String, String> hmOtherearningValue = new HashMap<String, String>();
				while (rs.next()) {
					hmOtherearning.put(rs.getString("emp_id"),rs.getString("is_approved"));
					hmOtherearningId.put(rs.getString("emp_id"),rs.getString("otherearning_id"));
					hmOtherearningValue.put(rs.getString("emp_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("pay_amount"))));
				}
				rs.close();
				pst.close();
				
				request.setAttribute("hmOtherearning", hmOtherearning);
				request.setAttribute("hmOtherearningId", hmOtherearningId);
				request.setAttribute("hmOtherearningValue", hmOtherearningValue);
				
				pst = con.prepareStatement("select emp_id from payroll_generation where paid_from = ? and paid_to = ? and paycycle = ? " +
						"and emp_id in ("+strEmpIds+") group by emp_id order by emp_id ");
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
				rs = pst.executeQuery();
				List<String> ckEmpPayList = new ArrayList<String>();
				while(rs.next()){
					ckEmpPayList.add(rs.getString("emp_id"));
				}
				rs.close();
				pst.close();
				request.setAttribute("ckEmpPayList", ckEmpPayList);
			}
			pst = con.prepareStatement("select * from salary_details where salary_head_id=? limit 1");
			pst.setInt(1, uF.parseToInt(getF_salaryhead()));
			rs = pst.executeQuery();
			String sHeadType="";
			while(rs.next()){
				sHeadType=rs.getString("earning_deduction");
				setSalaryheadname(rs.getString("salary_head_name"));
			}
			rs.close();
			pst.close();
			
			if(uF.parseToBoolean(getExceldownload())){
				generatevaraibleExcel(con,uF,salaryheadname);	
			}
			
			
			request.setAttribute("sHeadType", sHeadType);
			request.setAttribute("alEmpReport", alEmpReport);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;
	}
	

	private void generatevaraibleExcel(Connection con, UtilityFunctions uF ,String filename) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {	
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmempCode = new HashMap<String, String>();
			Map<String, String> hmempName = new HashMap<String, String>();
			String[] strPayCycleDates;			
			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
//				strPayCycleDates = getPaycycle().split("-");
				String str = URLDecoder.decode(getPaycycle());
				strPayCycleDates = str.split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycleUsingDurationByOrg(con, CF.getStrTimeZone(), CF, getF_org(),getStrPaycycleDuration(), request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
			}   
			
//			System.out.println(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive = true and joining_date<= ? ");
			if(getStrPaycycleDuration()!=null){
				sbQuery.append(" and eod.paycycle_duration ='"+getStrPaycycleDuration()+"'");
			}			
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
			/*if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }*/
			if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0){
	            	sbQuery.append(" and eod.grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
	            	//sbQuery.append(" and grade_id in ( " +StringUtils.join(getF_grade(), ",")+" ) and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");	
	            }else {
	            	 if(getF_level()!=null && getF_level().length>0){
	                     sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	                 }
	            	 if(getF_grade()!=null && getF_grade().length>0){
	                     sbQuery.append(" and eod.grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
	                 }
			}
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
           /* if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            } */
            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			
//			System.out.println("pst===>" + pst); 
			rs = pst.executeQuery();
			List<String> alEmp = new ArrayList<String>();			
			while (rs.next()) {				
				if(!alEmp.contains(rs.getString("emp_per_id")) && uF.parseToInt(rs.getString("emp_per_id")) > 0){
					alEmp.add(rs.getString("emp_per_id"));	
					
				//	String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
				
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					String strEmpName = rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname");
					hmempName.put(rs.getString("emp_per_id"), strEmpName);
					hmempCode.put(rs.getString("emp_per_id"), rs.getString("empcode"));
				}				
			}
			rs.close();
			pst.close();
			
			Map<String,String> hmEmpPrevVariable = new HashMap<String, String>();
			if(alEmp!=null && alEmp.size() > 0){
				String strEmpIds = StringUtils.join(alEmp.toArray(),",");
				
				pst = con.prepareStatement("select * from otherearning_individual_details where emp_id in ("+strEmpIds+") and pay_paycycle=? and salary_head_id=?");
				pst.setInt(1, uF.parseToInt(strPayCycleDates[2])-1);
				pst.setInt(2, uF.parseToInt(getF_salaryhead()));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					hmEmpPrevVariable.put(rs.getString("emp_id"), rs.getString("pay_amount"));
				}
				rs.close();
				pst.close();				
			}	
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Variable Head Structure");
			List<DataStyle> header = new ArrayList<DataStyle>();
			header.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Variable Amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Status",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();
			for (int i = 0; i < alEmp.size(); i++) {
				List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
			    alInnerExport.add(new DataStyle(String.valueOf(i+1),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(hmempCode.get(alEmp.get(i)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(hmempName.get(alEmp.get(i)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
				
				if(hmEmpPrevVariable.containsKey(alEmp.get(i))){
					String strAmount = uF.parseToDouble(hmEmpPrevVariable.get(alEmp.get(i))) > 0 ? rs.getString("pay_amount") : "0"; 
					alInnerExport.add(new DataStyle(strAmount,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("TRUE",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					
				} else {
					alInnerExport.add(new DataStyle("0",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("False",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}
				
				reportData.add(alInnerExport);
			}		
			
			
			ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
			sheetDesign.generateDefualtExcelSheet(workbook, sheet, header, reportData);		
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=Variableheads "+filename+".xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
			//System.out.println("ReportData==>"+reportData);
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
	
	
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	
	public String viewOtherEarning(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			String[] strPayCycleDates;			
			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
//				strPayCycleDates = getPaycycle().split("-");
				String str = URLDecoder.decode(getPaycycle());
				strPayCycleDates = str.split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycleUsingDurationByOrg(con, CF.getStrTimeZone(), CF, getF_org(),getStrPaycycleDuration(), request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
			}   
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive = true and joining_date<= ? ");
			if(getStrPaycycleDuration()!=null){
				sbQuery.append(" and eod.paycycle_duration ='"+getStrPaycycleDuration()+"'");
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
			
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "','") + "') ");
			}
			
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
//            if(getF_service()!=null && getF_service().length>0){
//                sbQuery.append(" and (");
//                for(int i=0; i<getF_service().length; i++){
//                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
//                    if(i<getF_service().length-1){
//                        sbQuery.append(" OR "); 
//                    }
//                }
//                sbQuery.append(" ) ");
//                
//            } 
            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            sbQuery.append(" and eod.emp_id in (select esd.emp_id from emp_salary_details esd, (select max(effective_date) as max_date, emp_id " +
            		" from emp_salary_details where isdisplay = true and is_approved=true and effective_date <=? and salary_head_id=? group by emp_id ) as b where esd.effective_date = b.max_date " +
            		"and b.emp_id = esd.emp_id and isdisplay = true and is_approved=true and esd.salary_head_id=? and esd.effective_date <=?)");
			sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_salaryhead()));
			pst.setInt(4, uF.parseToInt(getF_salaryhead()));
			pst.setDate(5,  uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			System.out.println("pst===>" + pst); 
			rs = pst.executeQuery();
			List<List<String>> alEmpReport = new ArrayList<List<String>>();
			List<String> alEmp = new ArrayList<String>();
			while (rs.next()) {
				List<String> alEmpReportInner = new ArrayList<String>();
				alEmpReportInner.add(rs.getString("emp_per_id"));
				
			//	String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				String strEmpName = rs.getString("emp_fname") +strEmpMName+ " " +rs.getString("emp_lname");
				alEmpReportInner.add(strEmpName);
				
				alEmpReportInner.add(rs.getString("empcode"));

				alEmpReport.add(alEmpReportInner);
				
				if(!alEmp.contains(rs.getString("emp_per_id")) && uF.parseToInt(rs.getString("emp_per_id")) > 0){
					alEmp.add(rs.getString("emp_per_id"));
				}
			}
			rs.close();
			pst.close();

			if(alEmp.size() > 0){
				String strEmpIds = StringUtils.join(alEmp.toArray(),",");
				pst = con.prepareStatement("select * from otherearning_individual_details where emp_id in ("+strEmpIds+") and paid_from = ? and paid_to=? and pay_paycycle=? and salary_head_id=?");
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
				pst.setInt(4, uF.parseToInt(getF_salaryhead()));
				rs = pst.executeQuery();
	
				Map<String, String> hmOtherearning = new HashMap<String, String>();
				Map<String, String> hmOtherearningId = new HashMap<String, String>();
				Map<String, String> hmOtherearningValue = new HashMap<String, String>();
				while (rs.next()) {
					hmOtherearning.put(rs.getString("emp_id"),rs.getString("is_approved"));
					hmOtherearningId.put(rs.getString("emp_id"),rs.getString("otherearning_id"));
					hmOtherearningValue.put(rs.getString("emp_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("pay_amount"))));
				}
				rs.close();
				pst.close();
				
				request.setAttribute("hmOtherearning", hmOtherearning);
				request.setAttribute("hmOtherearningId", hmOtherearningId);
				request.setAttribute("hmOtherearningValue", hmOtherearningValue);
				
				pst = con.prepareStatement("select emp_id from payroll_generation where paid_from = ? and paid_to = ? and paycycle = ? " +
						"and emp_id in ("+strEmpIds+") group by emp_id order by emp_id ");
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
				rs = pst.executeQuery();
				List<String> ckEmpPayList = new ArrayList<String>();
				while(rs.next()){
					ckEmpPayList.add(rs.getString("emp_id"));
				}
				rs.close();
				pst.close();
				request.setAttribute("ckEmpPayList", ckEmpPayList);
			}
			pst = con.prepareStatement("select * from salary_details where salary_head_id=? limit 1");
			pst.setInt(1, uF.parseToInt(getF_salaryhead()));
			rs = pst.executeQuery();
			String sHeadType="";
			
			while(rs.next()){
				sHeadType=rs.getString("earning_deduction");
				setSalaryheadname(rs.getString("salary_head_name"));
			}
			rs.close();
			pst.close();
			
			if(uF.parseToBoolean(getExceldownload())){
				generatevaraibleExcel(con,uF,salaryheadname);	
			}
			
			request.setAttribute("sHeadType", sHeadType);
			request.setAttribute("alEmpReport", alEmpReport);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("DURATION");
		if(getStrPaycycleDuration()!=null){
			String payDuration="";
			int k=0;
			for(int i=0;paycycleDurationList!=null && i<paycycleDurationList.size();i++){
				if(getStrPaycycleDuration().equals(paycycleDurationList.get(i).getPaycycleDurationId())){
					if(k==0){
						payDuration=paycycleDurationList.get(i).getPaycycleDurationName();
					}else{
						payDuration+=", "+paycycleDurationList.get(i).getPaycycleDurationName();
					}
					k++;
				}
			}
			if(payDuration!=null && !payDuration.equals("")){
				hmFilter.put("DURATION", payDuration);
			}else{
				hmFilter.put("DURATION", "All Duration");
			}
		}else{
			hmFilter.put("DURATION", "All Duration");
		}

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
		
		alFilter.add("PAYCYCLE");
		String strPaycycle = "";
		String[] strPayCycleDates = null;
		if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			
			strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
		}
		hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
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
		
		alFilter.add("LEVEL");
		if(getF_level()!=null){
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++){
				for(int j=0;j<getF_level().length;j++){
					if(getF_level()[j].equals(levelList.get(i).getLevelId())){
						if(k==0){
							strLevel=levelList.get(i).getLevelCodeName();
						}else{
							strLevel+=", "+levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if(strLevel!=null && !strLevel.equals("")){
				hmFilter.put("LEVEL", strLevel);
			}else{
				hmFilter.put("LEVEL", "All Levels");
			}
		}else{
			hmFilter.put("LEVEL", "All Levels");
		}
		
		alFilter.add("SALARY HEAD");
		if(getF_salaryhead()!=null) {
			String strSalaryHead="";
			for(int i=0;salaryHeadList!=null && i<salaryHeadList.size();i++) {
				if(getF_salaryhead().equals(salaryHeadList.get(i).getSalaryHeadId())) {
				  strSalaryHead=salaryHeadList.get(i).getSalaryHeadName();
				}
			}
			if(strSalaryHead!=null && !strSalaryHead.equals("")) {
				hmFilter.put("SALARY HEAD", strSalaryHead);
			} else {
				hmFilter.put("SALARY HEAD", "All SALARY HEAD");
			}
		} else {
			hmFilter.put("SALARY HEAD", "All SALARY HEAD");
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
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

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

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getStrPaycycleDuration() {
		return strPaycycleDuration;
	}

	public void setStrPaycycleDuration(String strPaycycleDuration) {
		this.strPaycycleDuration = strPaycycleDuration;
	}

	public List<FillPayCycleDuration> getPaycycleDurationList() {
		return paycycleDurationList;
	}

	public void setPaycycleDurationList(List<FillPayCycleDuration> paycycleDurationList) {
		this.paycycleDurationList = paycycleDurationList;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}

	public String getF_salaryhead() {
		return f_salaryhead;
	}

	public void setF_salaryhead(String f_salaryhead) {
		this.f_salaryhead = f_salaryhead;
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

	public String getExceldownload() {
		return exceldownload;
	}

	public void setExceldownload(String exceldownload) {
		this.exceldownload = exceldownload;
	}

	public String getSalaryheadname() {
		return salaryheadname;
	}

	public void setSalaryheadname(String salaryheadname) {
		this.salaryheadname = salaryheadname;
	}
	
}