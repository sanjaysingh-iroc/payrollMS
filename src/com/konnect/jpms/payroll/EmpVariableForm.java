package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycleDuration;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmpVariableForm extends ActionSupport implements ServletRequestAware, IStatements, ServletResponseAware{

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
	String employee;
	
	List<FillPayCycles> paycycleList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillWLocation> wLocationList;

	List<FillOrganisation> orgList;

	String f_salaryhead;
	String f_org;
	
	String strPaycycleDuration;
	List<FillPayCycleDuration> paycycleDurationList;
	
	List<FillGrade> gradeList;
	List<FillEmploymentType> employementTypeList;
	private List<FillEmployee> empList;
	
	private String btnUpdate;
	private String btnRevoke;
	private String[] salaryHeadId;
	String strBaseUserType = null;
	String strAction = null; 
	
	public String execute() throws Exception {

		try {
			session = request.getSession();
			request.setAttribute(PAGE, "/jsp/payroll/OtherEarningForm.jsp");
			request.setAttribute(TITLE, "Variable Form");
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if (CF == null) return LOGIN;
			
//			System.out.println("getExcelDownload==>"+getExceldownload());
			UtilityFunctions uF = new UtilityFunctions();

			strEmpId = (String) session.getAttribute(EMPID);
			strUserType = (String) session.getAttribute(USERTYPE);
			strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);//Created By Dattatray 14-04-2022
			
			//Created By Dattatray 14-06-2022
			strAction = request.getServletPath();
			if(strAction!=null) {
				strAction = strAction.replace("/","");
			}
			loadPageVisitAuditTrail();
			
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
			
			if (nSalaryStrucuterType == S_GRADE_WISE && uF.parseToInt(getEmployee()) > 0) {
				viewOtherEarningByGrade(uF);
			} else if (uF.parseToInt(getEmployee()) > 0) {
				viewOtherEarning(uF);
			}
			
//			System.out.println("getBtnUpdate() ===>> " + getBtnUpdate());
//			System.out.println("getBtnRevoke() ===>> " + getBtnRevoke());
			if(getBtnUpdate() != null) {
				updateEmpVariableHeadAmount();
			}
			if(getBtnRevoke() != null) {
				revokeEmpVariableHeadAmount();
			}
			
			loadEarning(uF);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return LOAD;
	}

	//Created By Dattatray 14-06-2022
	private void loadPageVisitAuditTrail() {
		Connection con=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpProfile = CF.getEmpNameMap(con,null,getEmployee());
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
			builder.append("\nSelect Employee : "+hmEmpProfile.get(getEmployee()));
			
			
			CF.pageVisitAuditTrail(con,CF,uF, strEmpId, strAction, strBaseUserType, builder.toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			db.closeConnection(con);
		}
	}
	
	public void loadEarning(UtilityFunctions uF) {

		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
 
		paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();
//		paycycleList = new FillPayCycles(getStrPaycycleDuration(),request).fillFuturePayCycles(CF, getF_org(), null);

		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		employementTypeList = new FillEmploymentType().fillEmploymentType(request);
		empList = new FillEmployee(request).fillEmployeeNameOrgLocationDepartSBUDesigGrade(CF, getF_org(), getStrLocation(), getStrDepartment(), null, getStrLevel(), null, getStrGrade(), getStrEmployeType(), false);
		
		getSelectedFilter(uF);

	}
	
	private void revokeEmpVariableHeadAmount() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			int cntUpdate = 0;
			for(int i=0; getSalaryHeadId() != null && i<getSalaryHeadId().length; i++) {
				if(uF.parseToInt(getSalaryHeadId()[i]) > 0) {
					pst = con.prepareStatement("delete from otherearning_individual_details where emp_id=? and salary_head_id=?");
					pst.setInt(1, uF.parseToInt(getEmployee()));
					pst.setInt(2, uF.parseToInt(getSalaryHeadId()[i]));
					pst.execute();
		            pst.close();
		            cntUpdate++;
				}
			}
			if(cntUpdate > 0) {
				request.setAttribute(MESSAGE, SUCCESSM+"All variable amount revoked successfully."+END);
			}

		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be revoked, Please try again");
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void updateEmpVariableHeadAmount() {

		Connection con = null;
		PreparedStatement pst = null, pst1 = null, pst2 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			int cntUpdate = 0;
			for(int i=0; getSalaryHeadId() != null && i<getSalaryHeadId().length; i++) {
				if(uF.parseToInt(getSalaryHeadId()[i]) > 0) {
					String strSalHeadAmount = request.getParameter(getSalaryHeadId()[i]+"_amount");
					String earn_deduct = request.getParameter(getSalaryHeadId()[i]+"_earn_deduct");
					String[] strSalHeadPaycycle = request.getParameterValues(getSalaryHeadId()[i]+"_paycycle");
//					System.out.println("SalaryHeadId ===>> "+getSalaryHeadId()[i]+" -- strSalHeadAmount ===>> "+strSalHeadAmount+" -- strSalHeadPaycycle ===>> "+strSalHeadPaycycle.length);
					for(int j=0; strSalHeadPaycycle != null && j<strSalHeadPaycycle.length; j++) {
//						System.out.println("strSalHeadPaycycle ===>> "+strSalHeadPaycycle[j]);
						String[] strTempPaycycle = strSalHeadPaycycle[j].split("-");
						
						pst = con.prepareStatement("delete from otherearning_individual_details where emp_id=? and pay_paycycle=? and " +
								"salary_head_id=?");
						pst.setInt(1, uF.parseToInt(getEmployee()));
						pst.setInt(2, uF.parseToInt(strTempPaycycle[2]));
						pst.setInt(3, uF.parseToInt(getSalaryHeadId()[i]));
//						System.out.println("pst ===>> " +pst);
						pst.executeUpdate();
			            pst.close();
			            
						double dblAmount = uF.parseToDouble(strSalHeadAmount);
						
						pst = con.prepareStatement("insert into otherearning_individual_details (emp_id, pay_paycycle, percent, salary_head_id, " +
								"amount, pay_amount, added_by, entry_date, paid_from, paid_to, is_approved, earning_deduction, approved_by, " +
								"approved_date) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
						pst.setInt(1, uF.parseToInt(getEmployee()));
						pst.setInt(2, uF.parseToInt(strTempPaycycle[2]));
						pst.setDouble(3, 0);
						pst.setInt(4, uF.parseToInt(getSalaryHeadId()[i]));
						pst.setDouble(5, uF.parseToDouble(strSalHeadAmount));
						pst.setDouble(6, dblAmount);
						pst.setInt(7, uF.parseToInt(strEmpId));
						pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setDate(9, uF.getDateFormat(strTempPaycycle[0], DATE_FORMAT));
						pst.setDate(10, uF.getDateFormat(strTempPaycycle[1], DATE_FORMAT));
						pst.setInt(11, 1);
						pst.setString(12, earn_deduct);
						pst.setInt(13, uF.parseToInt(strEmpId));
						pst.setDate(14, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.executeUpdate();
			            pst.close();
			            cntUpdate++;
					}
				}
			}
			if(cntUpdate > 0) {
				request.setAttribute(MESSAGE, SUCCESSM+"Variable amount updated successfully."+END);
			}
		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeStatements(pst2);
			db.closeConnection(con);
		}

	}
	
	
	public String viewOtherEarningByGrade(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			String empGradeId = CF.getEmpGradeId(con, getEmployee());
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select esd.emp_id,sd.grade_id,esd.salary_head_id,sd.salary_head_name,esd.earning_deduction,esd.effective_date," +
				"esd.isdisplay,sd.is_variable from emp_salary_details esd, salary_details sd where esd.salary_head_id = sd.salary_head_id " +
				"and emp_id = ? and sd.grade_id = ? and effective_date = (select max(effective_date) from emp_salary_details where " +
				"emp_id = ? and is_approved=true) and sd.is_variable=true and (is_contribution is null or is_contribution=false)"); // and esd.isdisplay = true
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getEmployee()));
			pst.setInt(2, uF.parseToInt(empGradeId));
			pst.setInt(3, uF.parseToInt(getEmployee()));
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
//			System.out.println("date 1 ==>> " + new Date());
			Map<String, List<String>> hmSalaryHeadData = new HashMap<String, List<String>>();
			String strEffectiveDate = null;
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("salary_head_id"));
				innerList.add(rs.getString("salary_head_name"));
				innerList.add(rs.getString("earning_deduction"));
				innerList.add(rs.getString("grade_id"));
				innerList.add(rs.getString("emp_id"));
				innerList.add(rs.getString("isdisplay"));
				hmSalaryHeadData.put(rs.getString("salary_head_id"), innerList);
				
				strEffectiveDate = rs.getString("effective_date");
			}
			rs.close();
			pst.close();
//			System.out.println("date 2 ==>> " + new Date());
			paycycleList = new FillPayCycles(getStrPaycycleDuration(),request).fillFuturePayCycles(CF, getF_org(), strEffectiveDate);
//			System.out.println("date 3 ==>> " + new Date());
			StringBuilder sbPaycycle = new StringBuilder();
			for(int i=0; paycycleList !=null && i<paycycleList.size(); i++) {
				sbPaycycle.append("<option value='"+paycycleList.get(i).getPaycycleId()+"'>"+paycycleList.get(i).getPaycycleName()+"</option>");
			}
//			System.out.println("date 4 ==>> " + new Date());
//			if(uF.parseToBoolean(getExceldownload())){
//				generatevaraibleExcel(con,uF,salaryheadname);	
//			}
			
//			System.out.println("hmSalaryHeadData ===>> " + hmSalaryHeadData);
			request.setAttribute("hmSalaryHeadData", hmSalaryHeadData);
			request.setAttribute("sbPaycycle", sbPaycycle.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;
	}
	

//	private void generatevaraibleExcel(Connection con, UtilityFunctions uF ,String filename) {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		try {	
//				
//			Map<String, String> hmempCode = new HashMap<String, String>();
//			Map<String, String> hmempName = new HashMap<String, String>();
//			String[] strPayCycleDates;			
//			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
////				strPayCycleDates = getPaycycle().split("-");
//				String str = URLDecoder.decode(getPaycycle());
//				strPayCycleDates = str.split("-");
//			} else {
//				strPayCycleDates = CF.getCurrentPayCycleUsingDurationByOrg(con, CF.getStrTimeZone(), CF, getF_org(),getStrPaycycleDuration(), request);
//				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
//			}   
//			
////			System.out.println(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive = true and joining_date<= ? ");
//			if(getStrPaycycleDuration()!=null){
//				sbQuery.append(" and eod.paycycle_duration ='"+getStrPaycycleDuration()+"'");
//			}			
//			if (getF_employeType() != null && getF_employeType().length > 0) {
//				sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
//			}
//			/*if(getF_level()!=null && getF_level().length>0){
//                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
//            }*/
//			if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0){
//	            	sbQuery.append(" and eod.grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
//	            	//sbQuery.append(" and grade_id in ( " +StringUtils.join(getF_grade(), ",")+" ) and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");	
//	            }else {
//	            	 if(getF_level()!=null && getF_level().length>0){
//	                     sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
//	                 }
//	            	 if(getF_grade()!=null && getF_grade().length>0){
//	                     sbQuery.append(" and eod.grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
//	                 }
//			}
//            if(getF_department()!=null && getF_department().length>0){
//                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
//            }
//           /* if(getF_service()!=null && getF_service().length>0){
//                sbQuery.append(" and (");
//                for(int i=0; i<getF_service().length; i++){
//                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
//                    if(i<getF_service().length-1){
//                        sbQuery.append(" OR "); 
//                    }
//                }
//                sbQuery.append(" ) ");
//                
//            } */
//            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
//                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
//				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//			}
//            if(uF.parseToInt(getF_org())>0){
//				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
//			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
//				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//			}
//			sbQuery.append(" order by emp_fname, emp_lname");
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			
////			System.out.println("pst===>" + pst); 
//			rs = pst.executeQuery();
//			List<String> alEmp = new ArrayList<String>();			
//			while (rs.next()) {				
//				if(!alEmp.contains(rs.getString("emp_per_id")) && uF.parseToInt(rs.getString("emp_per_id")) > 0){
//					alEmp.add(rs.getString("emp_per_id"));	
//					
//					String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
//					String strEmpName = rs.getString("emp_fname") + " " + uF.showData(strMiddleName, "")+ rs.getString("emp_lname");
//					hmempName.put(rs.getString("emp_per_id"), strEmpName);
//					hmempCode.put(rs.getString("emp_per_id"), rs.getString("empcode"));
//				}				
//			}
//			rs.close();
//			pst.close();
//			
//			Map<String,String> hmEmpPrevVariable = new HashMap<String, String>();
//			if(alEmp!=null && alEmp.size() > 0){
//				String strEmpIds = StringUtils.join(alEmp.toArray(),",");
//				
//				pst = con.prepareStatement("select * from otherearning_individual_details where emp_id in ("+strEmpIds+") and pay_paycycle=? and salary_head_id=?");
//				pst.setInt(1, uF.parseToInt(strPayCycleDates[2])-1);
//				pst.setInt(2, uF.parseToInt(getF_salaryhead()));
////				System.out.println("pst====>"+pst);
//				rs = pst.executeQuery();
//				while(rs.next()){
//					hmEmpPrevVariable.put(rs.getString("emp_id"), rs.getString("pay_amount"));
//				}
//				rs.close();
//				pst.close();				
//			}	
//			
//			HSSFWorkbook workbook = new HSSFWorkbook();
//			HSSFSheet sheet = workbook.createSheet("Variable Head Structure");
//			List<DataStyle> header = new ArrayList<DataStyle>();
//			header.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			header.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			header.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			header.add(new DataStyle("Variable Amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			header.add(new DataStyle("Status",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();
//			for (int i = 0; i < alEmp.size(); i++) {
//				List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
//			    alInnerExport.add(new DataStyle(String.valueOf(i+1),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(hmempCode.get(alEmp.get(i)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(hmempName.get(alEmp.get(i)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
//				
//				if(hmEmpPrevVariable.containsKey(alEmp.get(i))) {
//					String strAmount = uF.parseToDouble(hmEmpPrevVariable.get(alEmp.get(i))) > 0 ? rs.getString("pay_amount") : "0"; 
//					alInnerExport.add(new DataStyle(strAmount,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//					alInnerExport.add(new DataStyle("TRUE",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				} else {
//					alInnerExport.add(new DataStyle("0",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//					alInnerExport.add(new DataStyle("False",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				}
//				
//				reportData.add(alInnerExport);
//			}		
//			
//			
//			ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
//			sheetDesign.generateDefualtExcelSheet(workbook, sheet, header, reportData);		
//			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//			workbook.write(buffer);
//			response.setContentType("application/vnd.ms-excel:UTF-8");
//			response.setContentLength(buffer.size());
//			response.setHeader("Content-Disposition", "attachment; filename=Variableheads "+filename+".xls");
//			ServletOutputStream out = response.getOutputStream();
//			buffer.writeTo(out);
//			out.flush();
//			buffer.close();
//			out.close();
//			//System.out.println("ReportData==>"+reportData);
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
			String empLevelId = CF.getEmpLevelId(con, getEmployee());
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select esd.emp_id,sd.grade_id,esd.salary_head_id,sd.salary_head_name,esd.earning_deduction,esd.effective_date," +
				"esd.isdisplay,sd.is_variable from emp_salary_details esd, salary_details sd where esd.salary_head_id = sd.salary_head_id " +
				"and emp_id = ? and sd.level_id = ? and effective_date = (select max(effective_date) from emp_salary_details where " +
				"emp_id = ? and is_approved=true) and sd.is_variable = true and (is_contribution is null or is_contribution=false)");  // and esd.isdisplay = true
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getEmployee()));
			pst.setInt(2, uF.parseToInt(empLevelId));
			pst.setInt(3, uF.parseToInt(getEmployee()));
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmSalaryHeadData = new HashMap<String, List<String>>();
			String strEffectiveDate = null;
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("salary_head_id"));
				innerList.add(rs.getString("salary_head_name"));
				innerList.add(rs.getString("earning_deduction"));
				innerList.add(rs.getString("grade_id"));
				innerList.add(rs.getString("emp_id"));
				innerList.add(rs.getString("isdisplay"));
				hmSalaryHeadData.put(rs.getString("salary_head_id"), innerList);
				
				strEffectiveDate = rs.getString("effective_date");
			}
			rs.close();
			pst.close();
			paycycleList = new FillPayCycles(getStrPaycycleDuration(),request).fillFuturePayCycles(CF, getF_org(), strEffectiveDate);
			
			StringBuilder sbPaycycle = new StringBuilder();
			for(int i=0; paycycleList !=null && i<paycycleList.size(); i++) {
				sbPaycycle.append("<option value='"+paycycleList.get(i).getPaycycleId()+"'>"+paycycleList.get(i).getPaycycleName()+"</option>");
			}
			
//			if(uF.parseToBoolean(getExceldownload())){
//				generatevaraibleExcel(con,uF,salaryheadname);	
//			}
			
			request.setAttribute("hmSalaryHeadData", hmSalaryHeadData);
			request.setAttribute("sbPaycycle", sbPaycycle.toString());

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
		
		alFilter.add("EMP");
		if(getEmployee()!=null) {
			String strEmployeeName = "";
			for(int i=0;empList!=null && i<empList.size();i++) {
				if(getEmployee().equals(empList.get(i).getEmployeeId())) {
				  strEmployeeName = empList.get(i).getEmployeeName();
				}
			}
			if(strEmployeeName!=null && !strEmployeeName.equals("")) {
				hmFilter.put("EMP", strEmployeeName);
			} else {
				hmFilter.put("EMP", "Select Employee");
			}
		} else {
			hmFilter.put("EMP", "Select Employee");
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

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String getBtnUpdate() {
		return btnUpdate;
	}

	public void setBtnUpdate(String btnUpdate) {
		this.btnUpdate = btnUpdate;
	}

	public String[] getSalaryHeadId() {
		return salaryHeadId;
	}

	public void setSalaryHeadId(String[] salaryHeadId) {
		this.salaryHeadId = salaryHeadId;
	}

	public String getBtnRevoke() {
		return btnRevoke;
	}

	public void setBtnRevoke(String btnRevoke) {
		this.btnRevoke = btnRevoke;
	}

}