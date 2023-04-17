package com.konnect.jpms.salary;

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

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.payroll.ApprovePayroll;
import com.konnect.jpms.reports.MyProfile;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmpSalaryApproval extends ActionSupport implements ServletRequestAware, IStatements{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5947893602821384559L;
	
	public HttpSession session;
	public CommonFunctions CF;
	String strSessionEmpId;
	
	String leaveStatus; 
	String strStartDate;
	String strEndDate;
	
	String strSelectedEmpId;
	String strUserType = null;
	String approveSubmit; 
	
	String f_org;
	String strOrg;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	String pageFrom;
	
	List<FillEmployee> empList;
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	
	String strAction = null;
	String strBaseUserType = null;

	public String execute()	{ 
 
		session = request.getSession();if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);//Created By Dattatray 10-06-2022

		request.setAttribute(PAGE, "/jsp/salary/EmpSalaryApproval.jsp");
		request.setAttribute(TITLE, "Salary Approval");
		UtilityFunctions uF = new UtilityFunctions();

		//Created By Dattatray 10-6-2022
		strAction = request.getServletPath();
		if(strAction!=null) {
			strAction = strAction.replace("/","");
		}
		loadPageVisitAuditTrail(CF, uF);//Created By Dattatray 10-06-2022

		if(strUserType==null || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(HRMANAGER))) {
		 	
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		request.setAttribute("salaryStructure", CF.getStrSalaryStructure());
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-group\"></i><a href=\"People.action\" style=\"color: #3c8dbc;\"> People</a></li>" +
			"<li class=\"active\">Salary Approval</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		
		if(getApproveSubmit()!=null){
			approveSalaryStruture(uF);
		}
		
		if(uF.parseToInt(getF_org()) == 0){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getLeaveStatus()==null){
			setLeaveStatus("2");
		}
		
		viewSalaryApprovalDetails(uF);
		
		
		return loadEmpSalaryApproval(uF);
	}

	//Created By Dattatray 10-6-2022
	private void loadPageVisitAuditTrail(CommonFunctions CF,UtilityFunctions uF) {
		Connection con=null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			StringBuilder builder = new StringBuilder();
			builder.append("Filter:");
			builder.append("\nOrganization:"+getF_org());
			builder.append("\nLocation:"+StringUtils.join(getF_strWLocation(), ","));	
			builder.append("\nDepartment:"+StringUtils.join(getF_department(), ","));	
			builder.append("\nService:"+StringUtils.join(getF_service(), ","));	
			builder.append("\nLevel:"+StringUtils.join(f_level,","));	
			builder.append("\nStatus:"+getLeaveStatus());	
			builder.append("\nEmployee Id:"+getStrSelectedEmpId());	
			builder.append("\nFrom Date:"+getStrStartDate());	
			builder.append("\nTo Date:"+getStrEndDate());	
			if(getApproveSubmit()!=null){
				String[] strSalaryStruture = request.getParameterValues("strSalaryStruture");
				if(strSalaryStruture !=null){
				for(int j=0; j<strSalaryStruture.length;j++){
					System.out.println("id--"+strSalaryStruture[j]);
					String[] strTemp = strSalaryStruture[j].split("::::");
					String strEmpId = strTemp[0];
					builder.append("\nSalary approved employee id :"+strEmpId);	
				}
				
			}
			}
			CF.pageVisitAuditTrail(con, CF,uF, strSessionEmpId, strAction, strBaseUserType, builder.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}

	
	private String loadEmpSalaryApproval(UtilityFunctions uF) {
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
				
		empList=getEmployeeList(uF);
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
private List<FillEmployee> getEmployeeList(UtilityFunctions uF) {
		
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id ");
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
            if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(") ");
                
            }
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				
				String strEmpMName = "";
				
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
			
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("LEAVE_STATUS");
		if(uF.parseToInt(getLeaveStatus())==1){ 
			hmFilter.put("LEAVE_STATUS", "Approved");
		}else if(uF.parseToInt(getLeaveStatus())==2){
			hmFilter.put("LEAVE_STATUS", "Pending");
		}else {
			hmFilter.put("LEAVE_STATUS", "All");
		}
		
		alFilter.add("EMP");
		if(getStrSelectedEmpId()!=null)  {
			String strEmp="";
			int k=0;
			for(int i=0;empList!=null && i<empList.size();i++){
				if(getStrSelectedEmpId().equals(empList.get(i).getEmployeeId())) {
					if(k==0) {
						strEmp=empList.get(i).getEmployeeCode();
					} else {
						strEmp+=", "+empList.get(i).getEmployeeCode();
					}
					k++;
				}
			}
			if(strEmp!=null && !strEmp.equals("")) {
				hmFilter.put("EMP", strEmp);
			} else {
				hmFilter.put("EMP", "");
			}
			
		} else {
			hmFilter.put("EMP", "");
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
		
		if((getStrStartDate()!=null && !getStrStartDate().trim().equals("") && !getStrStartDate().trim().equalsIgnoreCase("NULL") && !getStrStartDate().trim().equalsIgnoreCase("From Date")) 
				&& (getStrEndDate()!=null && !getStrEndDate().trim().equals("") && !getStrEndDate().trim().equalsIgnoreCase("NULL") && !getStrEndDate().trim().equalsIgnoreCase("To Date"))){
			alFilter.add("FROMTO");
			hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	public void approveSalaryStruture(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			String[] strSalaryStruture = request.getParameterValues("strSalaryStruture");
			
			if(strSalaryStruture !=null){
				for(int j=0; j<strSalaryStruture.length;j++){
					System.out.println("id--"+strSalaryStruture[j]);
					String[] strTemp = strSalaryStruture[j].split("::::");
					String strEmpId = strTemp[0];
					String strEffectiveDate = strTemp[1];
					String strEntryDate = strTemp[2];
					
					pst = con.prepareStatement("update emp_salary_details set is_approved = ?, approved_by =?, approved_date=? where emp_id =? and entry_date=? and effective_date=?");
					pst.setBoolean(1, true);
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(4, uF.parseToInt(strEmpId));
					pst.setDate(5, uF.getDateFormat(strEntryDate, DATE_FORMAT));
					pst.setDate(6, uF.getDateFormat(strEffectiveDate, DATE_FORMAT));
					int x=pst.executeUpdate(); 
		            pst.close();
					
					if(x>0){
						CF.insertNewActivity(con, CF, uF, 16, strEmpId, strSessionEmpId, ""); //16 is for New Salary
//						insertNewArrear(con, uF);
					}
					
					if(x > 0){
						/**
						 * Calaculate CTC
						 * */
						Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, null, strEmpId);
						
						MyProfile myProfile = new MyProfile();
						myProfile.session = session;
						myProfile.request = request;
						myProfile.CF = CF;
						int intEmpIdReq = uF.parseToInt(strEmpId);
						int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
						if(nSalaryStrucuterType == S_GRADE_WISE){
							myProfile.getSalaryHeadsforEmployeeByGrade(con, uF, intEmpIdReq, hmEmpProfile);
						} else {
							myProfile.getSalaryHeadsforEmployee(con, uF, intEmpIdReq, hmEmpProfile);
						}
						double grossAmount = 0.0d;
						double grossYearAmount = 0.0d;
						double deductAmount = 0.0d;
						double deductYearAmount = 0.0d;
						double netAmount = 0.0d;
						double netYearAmount = 0.0d;
						
						List<List<String>> salaryHeadDetailsList = (List<List<String>>) request.getAttribute("salaryHeadDetailsList");
						for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
							List<String> innerList = salaryHeadDetailsList.get(i);
							if(innerList.get(1).equals("E")) {
								grossAmount +=uF.parseToDouble(innerList.get(2));
								grossYearAmount +=uF.parseToDouble(innerList.get(3));
							} else if(innerList.get(1).equals("D")) {
								double dblDeductMonth = 0.0d;
								double dblDeductAnnual = 0.0d;
								if(uF.parseToInt(innerList.get(4)) == EMPLOYEE_ESI){
									dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
									dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
								} else if(uF.parseToInt(innerList.get(4)) == EMPLOYER_ESI){
									dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
									dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
								} else {
									dblDeductMonth += Math.round(uF.parseToDouble(innerList.get(2)));
									dblDeductAnnual += Math.round(uF.parseToDouble(innerList.get(3)));
								}
								deductAmount += dblDeductMonth;
								deductYearAmount += dblDeductAnnual;
							}
						}
						
						Map<String,String> hmContribution = (Map<String,String>) request.getAttribute("hmContribution");
						if(hmContribution == null) hmContribution = new HashMap<String, String>();
						double dblMonthContri = 0.0d;
						double dblAnnualContri = 0.0d;
						boolean isEPF = uF.parseToBoolean((String)request.getAttribute("isEPF"));
						boolean isESIC = uF.parseToBoolean((String)request.getAttribute("isESIC"));
						boolean isLWF = uF.parseToBoolean((String)request.getAttribute("isLWF"));
						if(isEPF || isESIC || isLWF){
							if(isEPF){
								double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
								double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
								dblMonthContri += dblEPFMonth;
								dblAnnualContri += dblEPFAnnual;
							}
							if(isESIC){
								double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
								double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
								dblMonthContri += dblESIMonth;
								dblAnnualContri += dblESIAnnual;
							}
							if(isLWF){
								double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
								double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
								dblMonthContri += dblLWFMonth;
								dblAnnualContri += dblLWFAnnual;
							}
						}
						
						double dblCTCMonthly = grossAmount + dblMonthContri;
						double dblCTCAnnualy = grossYearAmount + dblAnnualContri;
						
						List<List<String>> salaryAnnualVariableDetailsList = (List<List<String>>)request.getAttribute("salaryAnnualVariableDetailsList");
						if(salaryAnnualVariableDetailsList == null) salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
						int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
						if(nAnnualVariSize > 0){
							double grossAnnualAmount = 0.0d;
							double grossAnnualYearAmount = 0.0d;
							for(int i = 0; i < nAnnualVariSize; i++){
								List<String> innerList = salaryAnnualVariableDetailsList.get(i);
								double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
								double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
								grossAnnualAmount += dblEarnMonth;
								grossAnnualYearAmount += dblEarnAnnual;
							}
							dblCTCMonthly += grossAnnualAmount;
							dblCTCAnnualy += grossAnnualYearAmount;
						}
						
						netAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCMonthly));							 
						netYearAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCAnnualy));
						
						Map<String, String> hmPrevCTC = null;
						if(nSalaryStrucuterType == S_GRADE_WISE){
							hmPrevCTC = getPrevCTCDetailsByGrade(con, uF, strEmpId);
						} else {
							 hmPrevCTC = getPrevCTCDetails(con, uF, strEmpId);
						}
						if(hmPrevCTC == null) hmPrevCTC = new HashMap<String, String>();
						double dblIncrementMonthAmt = netAmount - uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC"));
						double dblIncrementAnnualAmt = netAmount - uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC"));
			            
						pst = con.prepareStatement("update employee_official_details set month_ctc=?,annual_ctc=?,prev_month_ctc=?," +
								"prev_annual_ctc=?,incre_month_amount=?,incre_annual_amount=? where emp_id=?");
						pst.setDouble(1, netAmount);
						pst.setDouble(2, netYearAmount);
						pst.setDouble(3, uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC")));
						pst.setDouble(4, uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC")));
						pst.setDouble(5, dblIncrementMonthAmt);
						pst.setDouble(6, dblIncrementAnnualAmt);
						pst.setInt(7, uF.parseToInt(strEmpId));
						pst.execute();
						pst.close();
					}					
				}
			}
			request.setAttribute(MESSAGE, SUCCESSM+"Salary structure approved Successfully."+END);
		} catch (Exception e) {
			request.setAttribute(MESSAGE, SUCCESSM+"Salary structure not approved."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public Map<String, String> getPrevCTCDetails(Connection con, UtilityFunctions uF, String strEmpId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmPrevCTC = new HashMap<String, String>();
		try {
			String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			
			pst = con.prepareStatement("select effective_date from emp_salary_details where emp_id=? " +
					"and isdisplay=true and is_approved=true and effective_date in (select max(effective_date) as effective_date from emp_salary_details where emp_id=? " +
					"and isdisplay=true and is_approved=true and effective_date not in (select max(effective_date) as effective_date " +
					"from emp_salary_details where emp_id=? and effective_date <=? and isdisplay=true and is_approved=true)) limit 1");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setInt(3, uF.parseToInt(strEmpId));
			pst.setDate(4, uF.getDateFormat(currDate, DATE_FORMAT));
			rs = pst.executeQuery();
			boolean flag = false;
			String strPrevEffectiveDate = null;
			while(rs.next()){
				flag = true;
				strPrevEffectiveDate = uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT);
			}
			rs.close();
			pst.close();
			
			if(flag && strPrevEffectiveDate !=null && !strPrevEffectiveDate.trim().equals("") && !strPrevEffectiveDate.trim().equalsIgnoreCase("NULL")){
				String[] strFinancialYearDates = CF.getFinancialYear(con, strPrevEffectiveDate, CF, uF);
				String strFinancialYearStart = strFinancialYearDates[0];
				String strFinancialYearEnd = strFinancialYearDates[1];
				
				Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
				String levelId = hmEmpLevelMap.get(strEmpId);
				
				String strOrg = CF.getEmpOrgId(con, uF, strEmpId);
				String strEmpGender = CF.getEmpGender(con, uF, strEmpId);
				
				String currId = CF.getOrgCurrencyIdByOrg(con, strOrg);
				Map<String, Map<String, String>> hmCurrencyDetailsMap =  CF.getCurrencyDetails(con);
				if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String,String>>();
				Map<String, String> hmCurr = hmCurrencyDetailsMap.get(currId);
				if(hmCurr == null) hmCurr = new HashMap<String, String>();
				String strCurr = hmCurr.get("LONG_CURR")!=null && !hmCurr.get("LONG_CURR").equalsIgnoreCase("null") ? hmCurr.get("LONG_CURR")+" " : "";
				request.setAttribute("strCurr", strCurr);
	
//				Map<String, String> hmEmpGenderMap = CF.getEmpGenderMap(con);
//				Map<String, String> hmEmpAgeMap = CF.getEmpAgeMap(con,CF);
				
				
				String[] strPayCycleDates = CF.getPayCycleFromDate(con, strPrevEffectiveDate, CF.getStrTimeZone(), CF, strOrg);
				
				if(strPayCycleDates!=null && strPayCycleDates.length > 0){
					String strD1 = strPayCycleDates[0];
					String strD2 = strPayCycleDates[1];
					String strPC = strPayCycleDates[2];
					
					int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
				
					Map hmEmpMertoMap = new HashMap();
					Map hmEmpWlocationMap = new HashMap();
					Map hmEmpStateMap = new HashMap();
					CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
					
					String strStateId = (String)hmEmpStateMap.get(strEmpId);
					
					Map<String, String> hmEmpServiceTaxMap = CF.getEmpServiceTax(con, uF, CF);
					
					pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					rs = pst.executeQuery();
		//			System.out.println(" pst==>"+pst);
					Map hmHRAExemption = new HashMap();
					while(rs.next()){
						hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
						hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
						hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
						hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
					}
					rs.close();
					pst.close();
					
					Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
					pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					rs = pst.executeQuery();
					while(rs.next()){
						hmOtherTaxDetails.put(rs.getString("state_id")+"_SERVICE_TAX", rs.getString("service_tax"));
						hmOtherTaxDetails.put(rs.getString("state_id")+"_EDU_TAX", rs.getString("education_tax"));
						hmOtherTaxDetails.put(rs.getString("state_id")+"_STD_TAX", rs.getString("standard_tax"));
						hmOtherTaxDetails.put(rs.getString("state_id")+"_FLAT_TDS", rs.getString("flat_tds"));
						
						hmOtherTaxDetails.put(rs.getString("state_id")+"_MAX_TAX_INCOME", rs.getString("max_net_tax_income"));
						hmOtherTaxDetails.put(rs.getString("state_id")+"_REBATE_AMOUNT", rs.getString("rebate_amt"));
						hmOtherTaxDetails.put(rs.getString("state_id")+"_SWACHHA_BHARAT_CESS", rs.getString("swachha_bharat_cess"));
						hmOtherTaxDetails.put(rs.getString("state_id")+"_KRISHI_KALYAN_CESS", rs.getString("krishi_kalyan_cess"));

						hmOtherTaxDetails.put(rs.getString("state_id")+"_CGST", rs.getString("cgst"));
						hmOtherTaxDetails.put(rs.getString("state_id")+"_SGST", rs.getString("sgst"));
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and section_id=3 order by section_code");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					rs = pst.executeQuery();
		//			System.out.println(" pst==>"+pst);
					double dblInvestmentExemption = 0.0d;
					if (rs.next()) {
						dblInvestmentExemption = uF.parseToDouble(rs.getString("section_exemption_limit"));
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select * from annual_variable_details where level_id=? and org_id=? " +
							"and financial_year_start=? and financial_year_end=? and emp_id=? and salary_head_id in (select salary_head_id from salary_details " +
							"where is_annual_variable=true and (is_delete is null or is_delete = false))");
					pst.setInt(1, uF.parseToInt(levelId));
					pst.setInt(2, uF.parseToInt(strOrg));
					pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(5, uF.parseToInt(strEmpId));
					rs = pst.executeQuery();
					Map<String, String> hmAnnualVariableAmt = new HashMap<String, String>();
					while(rs.next()){
						hmAnnualVariableAmt.put(rs.getString("salary_head_id"), rs.getString("variable_amount"));
					}
					rs.close();
					pst.close();
					request.setAttribute("hmAnnualVariableAmt", hmAnnualVariableAmt);
					
					Map<String, String> hmSalaryDetails = new HashMap<String, String>();
					List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
					List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
					pst = con.prepareStatement("select * from salary_details where salary_head_id not in ("+GROSS+","+CTC+") and org_id=? and level_id=? order by earning_deduction desc, salary_head_id, weight");
					pst.setInt(1, uF.parseToInt(strOrg));
					pst.setInt(2, uF.parseToInt(levelId));
					rs = pst.executeQuery();  
					List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
					List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
					while(rs.next()){
						if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("E")){
							int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
							
							if(index>=0){
								alEmpSalaryDetailsEarning.remove(index);
								alEarningSalaryDuplicationTracer.remove(index);
								alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
							}else{
								alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
							}
							
							alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
						}else if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("D")){
							int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
							if(index>=0){
								alEmpSalaryDetailsDeduction.remove(index);
								alDeductionSalaryDuplicationTracer.remove(index);
								alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
							}else{
								alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
							}
							alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
						}
						
						hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
					}
					rs.close();
					pst.close();				
		
					Map<String, Double> hmSalaryTotal = new LinkedHashMap<String, Double>();
					double grossAmount = 0.0d;
					double grossYearAmount = 0.0d;
					double deductAmount = 0.0d;
					double deductYearAmount = 0.0d;				
					
					ApprovePayroll objAP = new ApprovePayroll();
					objAP.CF = CF;
					objAP.session = session;
					objAP.request = request; 
					
					Map<String, Map<String, String>> hmEmpPaidAmountDetails =  objAP.getEmpPaidAmountDetails(con, uF, strFinancialYearStart, strFinancialYearEnd);
//					Map<String, String> hmEmpExemptionsMap = objAP.getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, dblInvestmentExemption);
//					Map<String, String> hmEmpHomeLoanMap = objAP.getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd);
//					Map<String,String> hmFixedExemptions = objAP.getFixedExemption(con, uF, strFinancialYearStart, strFinancialYearEnd);
//					Map<String, String> hmEmpRentPaidMap = objAP.getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
					
					Map<String, String> hmPrevEmpTdsAmount  = new HashMap<String, String>();
					Map<String, String> hmPrevEmpGrossAmount  = new HashMap<String, String>();
					objAP.getPrevEmpTdsAmount(con,uF,strFinancialYearStart,strFinancialYearEnd,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount);
					
//					Map<String, String> hmEmpIncomeOtherSourcesMap = objAP.getEmpIncomeOtherSources(con, uF, strFinancialYearStart, strFinancialYearEnd);
					Map<String, String> hmPerkAlignAmount = (Map<String, String>) request.getAttribute("hmPerkAlignAmount");
					if(hmPerkAlignAmount == null) hmPerkAlignAmount = new HashMap<String, String>();
					Map<String, String> hmPerkAlignTDSAmount = (Map<String, String>) request.getAttribute("hmPerkAlignTDSAmount");
					if(hmPerkAlignTDSAmount == null) hmPerkAlignTDSAmount = new HashMap<String, String>();
					
					pst = con.prepareStatement("SELECT * FROM (select * from emp_salary_details where emp_id=? and isdisplay=true " +
							"and is_approved=true and effective_date=? and level_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
							"WHERE sd.level_id=? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) " +
							"order by sd.earning_deduction desc, weight ");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getDateFormat(strPrevEffectiveDate, DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(levelId));
					pst.setInt(4, uF.parseToInt(levelId));
//					System.out.println("in level pst ===>> " + pst);  
					rs = pst.executeQuery();
					List<List<String>> salaryHeadDetailsList = new ArrayList<List<String>>();
					Map<String, String> hmTotal = new HashMap<String, String>();
					double dblGrossTDS = 0.0d;
					boolean isEPF = false;
					boolean isESIC = false;
					boolean isLWF = false;
					List<List<String>> salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
					while (rs.next()) {
		
						if(uF.parseToInt(rs.getString("salary_head_id")) == CTC){
							continue;
						}
						
						if(!uF.parseToBoolean(rs.getString("isdisplay"))){
							continue;
						}
		
						if(rs.getString("earning_deduction").equals("E")) {
							if(!uF.parseToBoolean(rs.getString("is_variable")) && uF.parseToBoolean(rs.getString("is_annual_variable"))){
								
								List<String> innerList = new ArrayList<String>();
								innerList.add(rs.getString("salary_head_name"));
								innerList.add(rs.getString("earning_deduction"));
								
								if(uF.parseToBoolean(rs.getString("isdisplay"))){
									double dblAmount = 0.0d;
									double dblYearAmount = uF.parseToDouble(hmAnnualVariableAmt.get(rs.getString("salary_head_id")));
									
									innerList.add(""+dblAmount);
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
									innerList.add(rs.getString("salary_head_id"));
									
									grossAmount += dblAmount;
									grossYearAmount += dblYearAmount;
									
									if(uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT){
										dblGrossTDS += dblAmount;
									} else if(uF.parseToInt(rs.getString("salary_head_id")) != TRAVEL_REIMBURSEMENT){
										dblGrossTDS += dblAmount;
									} else if(uF.parseToInt(rs.getString("salary_head_id")) != MOBILE_REIMBURSEMENT){
										dblGrossTDS += dblAmount;
									} else if(uF.parseToInt(rs.getString("salary_head_id")) != OTHER_REIMBURSEMENT){
										dblGrossTDS += dblAmount;
									}
									
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
								} else {
									innerList.add("0.0");
									innerList.add("0.0");
									innerList.add(rs.getString("salary_head_id"));
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
								}
								salaryAnnualVariableDetailsList.add(innerList);
							
							} else {	
								List<String> innerList = new ArrayList<String>();
								innerList.add(rs.getString("salary_head_name"));
								innerList.add(rs.getString("earning_deduction"));
								
								if(uF.parseToBoolean(rs.getString("isdisplay"))){
									double dblAmount = 0.0d;
									double dblYearAmount = 0.0d;
									if(uF.parseToBoolean(rs.getString("is_variable")) && uF.parseToBoolean(rs.getString("is_annual_variable"))){
										dblAmount = 0.0d;
										dblYearAmount = uF.parseToDouble(hmAnnualVariableAmt.get(rs.getString("salary_head_id")));
									} else {
										dblAmount = rs.getDouble("amount");
										if(hmPerkAlignAmount.containsKey(rs.getString("salary_head_id"))){
											dblAmount = uF.parseToDouble(hmPerkAlignAmount.get(rs.getString("salary_head_id")));
										}
										dblYearAmount = dblAmount * 12;
									}
									
									innerList.add(""+dblAmount);
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
									innerList.add(rs.getString("salary_head_id"));
									
									grossAmount += dblAmount;
									grossYearAmount += dblYearAmount;
									
									if(uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT){
										dblGrossTDS += dblAmount;
									} else if(uF.parseToInt(rs.getString("salary_head_id")) != TRAVEL_REIMBURSEMENT){
										dblGrossTDS += dblAmount;
									} else if(uF.parseToInt(rs.getString("salary_head_id")) != MOBILE_REIMBURSEMENT){
										dblGrossTDS += dblAmount;
									} else if(uF.parseToInt(rs.getString("salary_head_id")) != OTHER_REIMBURSEMENT){
										dblGrossTDS += dblAmount;
									}
									
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
								} else {
									innerList.add("0.0");
									innerList.add("0.0");
									innerList.add(rs.getString("salary_head_id"));
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
								}
								salaryHeadDetailsList.add(innerList);
							}
						} else if(rs.getString("earning_deduction").equals("D")) {
							List<String> innerList = new ArrayList<String>();
							innerList.add(rs.getString("salary_head_name"));
							innerList.add(rs.getString("earning_deduction"));
							if(uF.parseToBoolean(rs.getString("isdisplay"))){
		//						int nPayMonth = uF.parseToInt(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, "MM"));
								switch(rs.getInt("salary_head_id")){
															
									case PROFESSIONAL_TAX :
										  
										double dblAmount = calculateProfessionalTax(con, uF, grossAmount,strFinancialYearStart, strFinancialYearEnd, nPayMonth, strStateId,strEmpGender);
										dblAmount = Math.round(dblAmount);
	//									double dblYearAmount =  dblAmount * 12;
										double dblYearAmount =  getAnnualProfessionalTax(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, strStateId,strEmpGender);
										
										deductAmount += dblAmount;
	//									deductYearAmount += dblYearAmount > 0.0d ? dblYearAmount + 100 : 0.0d;
										deductYearAmount += dblYearAmount;
										
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount));
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
										innerList.add(rs.getString("salary_head_id"));
										hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
										
										break;
									
									case EMPLOYEE_EPF :
										isEPF = true;	
										double dblAmount1 = objAP.calculateEEPF(con, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmTotal, strEmpId, null, null, false, null);
										dblAmount1 = Math.round(dblAmount1);
										double dblYearAmount1 = dblAmount1 * 12;
										
										deductAmount += dblAmount1;
										deductYearAmount += dblYearAmount1;
										
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount1));
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount1));
										innerList.add(rs.getString("salary_head_id"));
										hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount1));
										
										break;
									
	//								case EMPLOYER_EPF :
	//									
	//									double dblAmount2 = objAP.calculateERPF(con, CF, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, getEmpId(), null, null, false, null);
	//									dblAmount2 = Math.round(dblAmount2);
	//									double dblYearAmount2 = dblAmount2 * 12;
	//									
	//									deductAmount += dblAmount2;
	//									deductYearAmount += dblYearAmount2;
	//									
	//									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount2));
	//									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount2));
	//									innerList.add(rs.getString("salary_head_id"));
	//									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount2));
	//									
	//									break;  
									
	//								case EMPLOYER_ESI :
	//									
	//									double dblAmount3 = objAP.calculateERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId,getEmpId());
	//									dblAmount3 = Math.round(dblAmount3);
	//									double dblYearAmount3 = dblAmount3 * 12;
	//									
	//									deductAmount += dblAmount3;
	//									deductYearAmount += dblYearAmount3;
	//									
	//									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount3));
	//									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount3));
	//									innerList.add(rs.getString("salary_head_id"));
	//									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount3));
	//									
	//									break;
									
									case EMPLOYEE_ESI :
										isESIC = true;
										double dblAmount4 = objAP.calculateEEESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, strEmpId);
										dblAmount4 = Math.ceil(dblAmount4);
										double dblYearAmount4 = dblAmount4 * 12;
										dblYearAmount4 = Math.ceil(dblYearAmount4);
										
										deductAmount += dblAmount4;
										deductYearAmount += dblYearAmount4;
	//									System.out.println("dblAmount4====>"+dblAmount4);
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount4));
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount4));
										innerList.add(rs.getString("salary_head_id"));
										hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount4));
										
										break;
									
	//								case EMPLOYER_LWF :
	//									
	//									double dblAmount5 = objAP.calculateERLWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, nPayMonth);
	//									dblAmount5 = Math.round(dblAmount5);
	//									double dblYearAmount5 = dblAmount5 * 12;
	//									
	//									deductAmount += dblAmount5;
	//									deductYearAmount += dblYearAmount5;
	//									
	//									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount5));
	//									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount5));
	//									innerList.add(rs.getString("salary_head_id"));
	//									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount5));
	//									
	//									break;
									
									case EMPLOYEE_LWF :
										isLWF = true;
										double dblAmount6 = objAP.calculateEELWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, strEmpId, nPayMonth, strOrg);
										dblAmount6 = Math.round(dblAmount6);
										double dblYearAmount6 = dblAmount6 * 12;
										
										deductAmount += dblAmount6;
										deductYearAmount += dblYearAmount6;
										
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount6));
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount6));
										innerList.add(rs.getString("salary_head_id"));
										hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount6));
										
										break;
									
									case TDS :
										
	//									double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
	//									double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
										double dblHRA = uF.parseToDouble(hmTotal.get(HRA+""));
										
										String[] hraSalaryHeads = null;
										if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null){
											hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
										}
										
										double dblHraSalHeadsAmount = 0;
										for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++){
											dblHraSalHeadsAmount += uF.parseToDouble((String)hmTotal.get(hraSalaryHeads[i]));
										}
										
										Map<String, String> hmPaidSalaryDetails =  hmEmpPaidAmountDetails.get(strEmpId);
										if(hmPaidSalaryDetails==null){hmPaidSalaryDetails=new HashMap<String, String>();}
										
										double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_EDU_TAX"));
										double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_STD_TAX"));
										double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_FLAT_TDS"));
										 
										if(hmEmpServiceTaxMap.containsKey(strEmpId)){
											dblGrossTDS = grossAmount;
											double  dblServiceTaxAmount = uF.parseToDouble(hmTotal.get(SERVICE_TAX+""));
											dblGrossTDS = dblGrossTDS - dblServiceTaxAmount;
											
											double dblSwachhaBharatCess = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS+""));
											dblGrossTDS = dblGrossTDS - dblSwachhaBharatCess;
											
											double dblKrishiKalyanCess = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS+""));
											dblGrossTDS = dblGrossTDS - dblKrishiKalyanCess;
										}
										
//										double dblAmount7 = objAP.calculateTDS(con, uF,strD2,strD1, dblGrossTDS, dblCess1, dblCess2, dblFlatTDS, dblInvestmentExemption, dblHRA, dblHraSalHeadsAmount,
//												nPayMonth,
//												strD1, strFinancialYearStart, strFinancialYearEnd, strEmpId, hmEmpGenderMap.get(strEmpId),  hmEmpAgeMap.get(strEmpId), strStateId,
//												hmEmpExemptionsMap, hmEmpHomeLoanMap, hmFixedExemptions, hmEmpMertoMap, hmEmpRentPaidMap, hmPaidSalaryDetails,
//												hmTotal, hmSalaryDetails, hmEmpLevelMap, CF,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount,hmEmpIncomeOtherSourcesMap,hmOtherTaxDetails,hmEmpStateMap);
										double dblAmount7 = objAP.calculateTDS(con, CF, uF, dblGrossTDS, dblFlatTDS, nPayMonth, strFinancialYearStart,
												strFinancialYearEnd, strEmpId, hmEmpLevelMap);
										dblAmount7 = Math.round(dblAmount7);
										double dblYearAmount7 = dblAmount7 * 12;
										
										deductAmount += dblAmount7;
										deductYearAmount += dblYearAmount7;
										
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount7));
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount7));
										innerList.add(rs.getString("salary_head_id"));
										hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount7));
										
										break;
									
									default:
										
										double dblAmount9 = uF.parseToDouble(rs.getString("amount"));
										double dblYearAmount9 = dblAmount9 * 12;
										
										deductAmount += dblAmount9;
										deductYearAmount += dblYearAmount9;
										
										innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblAmount9));
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount9));
										innerList.add(rs.getString("salary_head_id"));
										hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount9));
										
										break;
								}
							} else {
								innerList.add("0.0");
								innerList.add("0.0");
								innerList.add(rs.getString("salary_head_id"));
								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
							}
							
							salaryHeadDetailsList.add(innerList);
						}
						
					}
					rs.close();
					pst.close();
		
					hmSalaryTotal.put("GROSS_AMOUNT", grossAmount);
					hmSalaryTotal.put("GROSS_YEAR_AMOUNT", grossYearAmount);
					hmSalaryTotal.put("DEDUCT_AMOUNT", deductAmount);
					hmSalaryTotal.put("DEDUCT_YEAR_AMOUNT", deductYearAmount);
					
					
	//				System.out.println("salaryHeadDetailsList======>"+salaryHeadDetailsList);
	//				System.out.println("salaryAnnualVariableDetailsList======>"+salaryAnnualVariableDetailsList);
					/**
					 * Employer Contribution
					 * */ 
					Map<String,String> hmContribution = new HashMap<String, String>();
					if(isEPF){
	//					double dblAmount = objAP.calculateERPFandEPS(con, CF, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, getEmpId(), null, null, false, null);
						double dblAmount = objAP.calculateERPF(con,CF, null, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, null);
						dblAmount = Math.round(dblAmount);
						double dblYearAmount = dblAmount * 12;
						hmContribution.put("EPF_MONTHLY", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
						hmContribution.put("EPF_ANNUALY", uF.formatIntoTwoDecimalWithOutComma(dblYearAmount));
					}
					if(isESIC){
						double dblAmount = objAP.calculateERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, strEmpId, null, null);
						dblAmount = Math.ceil(dblAmount);
						double dblYearAmount = dblAmount * 12;
						dblYearAmount = Math.ceil(dblYearAmount);
						
						hmContribution.put("ESI_MONTHLY", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
						hmContribution.put("ESI_ANNUALY", uF.formatIntoTwoDecimalWithOutComma(dblYearAmount));
					}
					if(isLWF){
						double dblAmount = objAP.calculateERLWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, nPayMonth, strOrg);
						dblAmount = Math.round(dblAmount);
						double dblYearAmount = dblAmount * 12;
						hmContribution.put("LWF_MONTHLY", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
						hmContribution.put("LWF_ANNUALY", uF.formatIntoTwoDecimalWithOutComma(dblYearAmount));				
					}
					
					
					/**
					 * Employer Contribution End
					 * */ 
					
					
					
					/**
					 * Set Prev CTC
					 * */
					grossAmount = 0.0d;
					grossYearAmount = 0.0d;
					deductAmount = 0.0d;
					deductYearAmount = 0.0d;
					
					for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
						List<String> innerList = salaryHeadDetailsList.get(i);
						if(innerList.get(1).equals("E")) {
							grossAmount +=uF.parseToDouble(innerList.get(2));
							grossYearAmount +=uF.parseToDouble(innerList.get(3));
						} else if(innerList.get(1).equals("D")) {
							double dblDeductMonth = 0.0d;
							double dblDeductAnnual = 0.0d;
							if(uF.parseToInt(innerList.get(4)) == EMPLOYEE_ESI){
								dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
								dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
							} else if(uF.parseToInt(innerList.get(4)) == EMPLOYER_ESI){
								dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
								dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
							} else {
								dblDeductMonth += Math.round(uF.parseToDouble(innerList.get(2)));
								dblDeductAnnual += Math.round(uF.parseToDouble(innerList.get(3)));
							}
							deductAmount += dblDeductMonth;
							deductYearAmount += dblDeductAnnual;
						}
					}
					
					double dblMonthContri = 0.0d;
					double dblAnnualContri = 0.0d;
					if(isEPF || isESIC || isLWF){
						if(isEPF){
							double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
							double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
							dblMonthContri += dblEPFMonth;
							dblAnnualContri += dblEPFAnnual;
						}
						if(isESIC){
							double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
							double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
							dblMonthContri += dblESIMonth;
							dblAnnualContri += dblESIAnnual;
						}
						if(isLWF){
							double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
							double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
							dblMonthContri += dblLWFMonth;
							dblAnnualContri += dblLWFAnnual;
						}
					}
					
					double dblCTCMonthly = grossAmount + dblMonthContri;
					double dblCTCAnnualy = grossYearAmount + dblAnnualContri;
					
					int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
					if(nAnnualVariSize > 0){
						double grossAnnualAmount = 0.0d;
						double grossAnnualYearAmount = 0.0d;
						for(int i = 0; i < nAnnualVariSize; i++){
							List<String> innerList = salaryAnnualVariableDetailsList.get(i);
							double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
							double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
							grossAnnualAmount += dblEarnMonth;
							grossAnnualYearAmount += dblEarnAnnual;
						}
						dblCTCMonthly += grossAnnualAmount;
						dblCTCAnnualy += grossAnnualYearAmount;
					}
					
					hmPrevCTC.put("PREV_MONTH_CTC", uF.formatIntoTwoDecimalWithOutComma(dblCTCMonthly));
					hmPrevCTC.put("PREV_ANNUAL_CTC", uF.formatIntoTwoDecimalWithOutComma(dblCTCAnnualy));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
//		System.out.println("hmPrevCTC=====>"+hmPrevCTC);
		return hmPrevCTC;
	}
	
	public Map<String, String> getPrevCTCDetailsByGrade(Connection con, UtilityFunctions uF, String strEmpId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmPrevCTC = new HashMap<String, String>();
		try {
			String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			
			pst = con.prepareStatement("select effective_date from emp_salary_details where emp_id=? " +
					"and isdisplay=true and is_approved=true and effective_date in (select max(effective_date) as effective_date from emp_salary_details where emp_id=? " +
					"and isdisplay=true and is_approved=true and effective_date not in (select max(effective_date) as effective_date " +
					"from emp_salary_details where emp_id=? and effective_date <=? and isdisplay=true and is_approved=true)) limit 1");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setInt(3, uF.parseToInt(strEmpId));
			pst.setDate(4, uF.getDateFormat(currDate, DATE_FORMAT));
			rs = pst.executeQuery();
			boolean flag = false;
			String strPrevEffectiveDate = null;
			while(rs.next()){
				flag = true;
				strPrevEffectiveDate = uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT);
			}
			rs.close();
			pst.close();
			
			if(flag && strPrevEffectiveDate !=null && !strPrevEffectiveDate.trim().equals("") && !strPrevEffectiveDate.trim().equalsIgnoreCase("NULL")){
				String[] strFinancialYearDates = CF.getFinancialYear(con, strPrevEffectiveDate, CF, uF);
				String strFinancialYearStart = strFinancialYearDates[0];
				String strFinancialYearEnd = strFinancialYearDates[1];
				
				Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
				String levelId = hmEmpLevelMap.get(strEmpId);
				String gradeId = hmEmpLevelMap.get(strEmpId);
				
				String strOrg = CF.getEmpOrgId(con, uF, strEmpId);
				String strEmpGender = CF.getEmpGender(con, uF, strEmpId);
				
				String currId = CF.getOrgCurrencyIdByOrg(con, strOrg);
				Map<String, Map<String, String>> hmCurrencyDetailsMap =  CF.getCurrencyDetails(con);
				if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String,String>>();
				Map<String, String> hmCurr = hmCurrencyDetailsMap.get(currId);
				if(hmCurr == null) hmCurr = new HashMap<String, String>();
				String strCurr = hmCurr.get("LONG_CURR")!=null && !hmCurr.get("LONG_CURR").equalsIgnoreCase("null") ? hmCurr.get("LONG_CURR")+" " : "";
				request.setAttribute("strCurr", strCurr);
	
//				Map<String, String> hmEmpGenderMap = CF.getEmpGenderMap(con);
//				Map<String, String> hmEmpAgeMap = CF.getEmpAgeMap(con,CF);
				
				
				String[] strPayCycleDates = CF.getPayCycleFromDate(con, strPrevEffectiveDate, CF.getStrTimeZone(), CF, strOrg);
				
				if(strPayCycleDates!=null && strPayCycleDates.length > 0){
					String strD1 = strPayCycleDates[0];
					String strD2 = strPayCycleDates[1];
					String strPC = strPayCycleDates[2];
					
					int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
				
					Map hmEmpMertoMap = new HashMap();
					Map hmEmpWlocationMap = new HashMap();
					Map hmEmpStateMap = new HashMap();
					CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
					
					String strStateId = (String)hmEmpStateMap.get(strEmpId);
					
					Map<String, String> hmEmpServiceTaxMap = CF.getEmpServiceTax(con, uF, CF);
					
					pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					rs = pst.executeQuery();
		//			System.out.println(" pst==>"+pst);
					Map hmHRAExemption = new HashMap();
					while(rs.next()){
						hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
						hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
						hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
						hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
					}
					rs.close();
					pst.close();
					
					Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
					pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					rs = pst.executeQuery();
					while(rs.next()){
						hmOtherTaxDetails.put(rs.getString("state_id")+"_SERVICE_TAX", rs.getString("service_tax"));
						hmOtherTaxDetails.put(rs.getString("state_id")+"_EDU_TAX", rs.getString("education_tax"));
						hmOtherTaxDetails.put(rs.getString("state_id")+"_STD_TAX", rs.getString("standard_tax"));
						hmOtherTaxDetails.put(rs.getString("state_id")+"_FLAT_TDS", rs.getString("flat_tds"));
						
						hmOtherTaxDetails.put(rs.getString("state_id")+"_MAX_TAX_INCOME", rs.getString("max_net_tax_income"));
						hmOtherTaxDetails.put(rs.getString("state_id")+"_REBATE_AMOUNT", rs.getString("rebate_amt"));
						hmOtherTaxDetails.put(rs.getString("state_id")+"_SWACHHA_BHARAT_CESS", rs.getString("swachha_bharat_cess"));
						hmOtherTaxDetails.put(rs.getString("state_id")+"_KRISHI_KALYAN_CESS", rs.getString("krishi_kalyan_cess"));

						hmOtherTaxDetails.put(rs.getString("state_id")+"_CGST", rs.getString("cgst"));
						hmOtherTaxDetails.put(rs.getString("state_id")+"_SGST", rs.getString("sgst"));
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and section_id=3 order by section_code");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					rs = pst.executeQuery();
		//			System.out.println(" pst==>"+pst);
					double dblInvestmentExemption = 0.0d;
					if (rs.next()) {
						dblInvestmentExemption = uF.parseToDouble(rs.getString("section_exemption_limit"));
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select * from annual_variable_details where level_id=? and org_id=? " +
							"and financial_year_start=? and financial_year_end=? and emp_id=? and salary_head_id in (select salary_head_id from salary_details " +
							"where is_annual_variable=true and (is_delete is null or is_delete = false))");
					pst.setInt(1, uF.parseToInt(levelId));
					pst.setInt(2, uF.parseToInt(strOrg));
					pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(5, uF.parseToInt(strEmpId));
					rs = pst.executeQuery();
					Map<String, String> hmAnnualVariableAmt = new HashMap<String, String>();
					while(rs.next()){
						hmAnnualVariableAmt.put(rs.getString("salary_head_id"), rs.getString("variable_amount"));
					}
					rs.close();
					pst.close();
					request.setAttribute("hmAnnualVariableAmt", hmAnnualVariableAmt);
					
					Map<String, String> hmSalaryDetails = new HashMap<String, String>();
					List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
					List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
					pst = con.prepareStatement("select * from salary_details where salary_head_id not in ("+GROSS+","+CTC+") " +
							"and org_id=? and grade_id=? order by earning_deduction desc, salary_head_id, weight");
					pst.setInt(1, uF.parseToInt(strOrg));
					pst.setInt(2, uF.parseToInt(gradeId));
					rs = pst.executeQuery();  
					List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
					List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
					while(rs.next()){
						if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("E")){
							int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
							
							if(index>=0){
								alEmpSalaryDetailsEarning.remove(index);
								alEarningSalaryDuplicationTracer.remove(index);
								alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
							}else{
								alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
							}
							
							alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
						}else if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("D")){
							int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
							if(index>=0){
								alEmpSalaryDetailsDeduction.remove(index);
								alDeductionSalaryDuplicationTracer.remove(index);
								alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
							}else{
								alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
							}
							alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
						}
						
						hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
					}
					rs.close();
					pst.close();				
		
					Map<String, Double> hmSalaryTotal = new LinkedHashMap<String, Double>();
					double grossAmount = 0.0d;
					double grossYearAmount = 0.0d;
					double deductAmount = 0.0d;
					double deductYearAmount = 0.0d;				
					
					ApprovePayroll objAP = new ApprovePayroll();
					objAP.CF = CF;
					objAP.session = session;
					objAP.request = request; 
					
					Map<String, Map<String, String>> hmEmpPaidAmountDetails =  objAP.getEmpPaidAmountDetails(con, uF, strFinancialYearStart, strFinancialYearEnd);
//					Map<String, String> hmEmpExemptionsMap = objAP.getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, dblInvestmentExemption);
//					Map<String, String> hmEmpHomeLoanMap = objAP.getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd);
//					Map<String,String> hmFixedExemptions = objAP.getFixedExemption(con, uF, strFinancialYearStart, strFinancialYearEnd);
//					Map<String, String> hmEmpRentPaidMap = objAP.getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
					
					Map<String, String> hmPrevEmpTdsAmount  = new HashMap<String, String>();
					Map<String, String> hmPrevEmpGrossAmount  = new HashMap<String, String>();
					objAP.getPrevEmpTdsAmount(con,uF,strFinancialYearStart,strFinancialYearEnd,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount);
					
//					Map<String, String> hmEmpIncomeOtherSourcesMap = objAP.getEmpIncomeOtherSources(con, uF, strFinancialYearStart, strFinancialYearEnd);
					Map<String, String> hmPerkAlignAmount = (Map<String, String>) request.getAttribute("hmPerkAlignAmount");
					if(hmPerkAlignAmount == null) hmPerkAlignAmount = new HashMap<String, String>();
					Map<String, String> hmPerkAlignTDSAmount = (Map<String, String>) request.getAttribute("hmPerkAlignTDSAmount");
					if(hmPerkAlignTDSAmount == null) hmPerkAlignTDSAmount = new HashMap<String, String>();
					
					pst = con.prepareStatement("SELECT * FROM (select * from emp_salary_details where emp_id=? and isdisplay=true " +
							"and is_approved=true and effective_date=? and grade_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
							"WHERE sd.grade_id=? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) " +
							"order by sd.earning_deduction desc, weight ");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getDateFormat(strPrevEffectiveDate, DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(gradeId));
					pst.setInt(4, uF.parseToInt(gradeId));
//					System.out.println("in level pst ===>> " + pst);  
					rs = pst.executeQuery();
					List<List<String>> salaryHeadDetailsList = new ArrayList<List<String>>();
					Map<String, String> hmTotal = new HashMap<String, String>();
					double dblGrossTDS = 0.0d;
					boolean isEPF = false;
					boolean isESIC = false;
					boolean isLWF = false;
					List<List<String>> salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
					while (rs.next()) {
		
						if(uF.parseToInt(rs.getString("salary_head_id")) == CTC){
							continue;
						}
						
						if(!uF.parseToBoolean(rs.getString("isdisplay"))){
							continue;
						}
		
						if(rs.getString("earning_deduction").equals("E")) {
							if(!uF.parseToBoolean(rs.getString("is_variable")) && uF.parseToBoolean(rs.getString("is_annual_variable"))){
								
								List<String> innerList = new ArrayList<String>();
								innerList.add(rs.getString("salary_head_name"));
								innerList.add(rs.getString("earning_deduction"));
								
								if(uF.parseToBoolean(rs.getString("isdisplay"))){
									double dblAmount = 0.0d;
									double dblYearAmount = uF.parseToDouble(hmAnnualVariableAmt.get(rs.getString("salary_head_id")));
									
									innerList.add(""+dblAmount);
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
									innerList.add(rs.getString("salary_head_id"));
									
									grossAmount += dblAmount;
									grossYearAmount += dblYearAmount;
									
									if(uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT){
										dblGrossTDS += dblAmount;
									} else if(uF.parseToInt(rs.getString("salary_head_id")) != TRAVEL_REIMBURSEMENT){
										dblGrossTDS += dblAmount;
									} else if(uF.parseToInt(rs.getString("salary_head_id")) != MOBILE_REIMBURSEMENT){
										dblGrossTDS += dblAmount;
									} else if(uF.parseToInt(rs.getString("salary_head_id")) != OTHER_REIMBURSEMENT){
										dblGrossTDS += dblAmount;
									}
									
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
								} else {
									innerList.add("0.0");
									innerList.add("0.0");
									innerList.add(rs.getString("salary_head_id"));
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
								}
								salaryAnnualVariableDetailsList.add(innerList);
							
							} else {	
								List<String> innerList = new ArrayList<String>();
								innerList.add(rs.getString("salary_head_name"));
								innerList.add(rs.getString("earning_deduction"));
								
								if(uF.parseToBoolean(rs.getString("isdisplay"))){
									double dblAmount = 0.0d;
									double dblYearAmount = 0.0d;
									if(uF.parseToBoolean(rs.getString("is_variable")) && uF.parseToBoolean(rs.getString("is_annual_variable"))){
										dblAmount = 0.0d;
										dblYearAmount = uF.parseToDouble(hmAnnualVariableAmt.get(rs.getString("salary_head_id")));
									} else {
										dblAmount = rs.getDouble("amount");
										if(hmPerkAlignAmount.containsKey(rs.getString("salary_head_id"))){
											dblAmount = uF.parseToDouble(hmPerkAlignAmount.get(rs.getString("salary_head_id")));
										}
										dblYearAmount = dblAmount * 12;
									}
									
									innerList.add(""+dblAmount);
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
									innerList.add(rs.getString("salary_head_id"));
									
									grossAmount += dblAmount;
									grossYearAmount += dblYearAmount;
									
									if(uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT){
										dblGrossTDS += dblAmount;
									} else if(uF.parseToInt(rs.getString("salary_head_id")) != TRAVEL_REIMBURSEMENT){
										dblGrossTDS += dblAmount;
									} else if(uF.parseToInt(rs.getString("salary_head_id")) != MOBILE_REIMBURSEMENT){
										dblGrossTDS += dblAmount;
									} else if(uF.parseToInt(rs.getString("salary_head_id")) != OTHER_REIMBURSEMENT){
										dblGrossTDS += dblAmount;
									}
									
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
								} else {
									innerList.add("0.0");
									innerList.add("0.0");
									innerList.add(rs.getString("salary_head_id"));
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
								}
								salaryHeadDetailsList.add(innerList);
							}
						} else if(rs.getString("earning_deduction").equals("D")) {
							List<String> innerList = new ArrayList<String>();
							innerList.add(rs.getString("salary_head_name"));
							innerList.add(rs.getString("earning_deduction"));
							if(uF.parseToBoolean(rs.getString("isdisplay"))){
		//						int nPayMonth = uF.parseToInt(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, "MM"));
								switch(rs.getInt("salary_head_id")){
															
									case PROFESSIONAL_TAX :
										  
										double dblAmount = calculateProfessionalTax(con, uF, grossAmount,strFinancialYearStart, strFinancialYearEnd, nPayMonth, strStateId,strEmpGender);
										dblAmount = Math.round(dblAmount);
	//									double dblYearAmount =  dblAmount * 12;
										double dblYearAmount =  getAnnualProfessionalTax(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, strStateId,strEmpGender);
										
										deductAmount += dblAmount;
	//									deductYearAmount += dblYearAmount > 0.0d ? dblYearAmount + 100 : 0.0d;
										deductYearAmount += dblYearAmount;
										
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount));
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
										innerList.add(rs.getString("salary_head_id"));
										hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
										
										break;
									
									case EMPLOYEE_EPF :
										isEPF = true;	
										double dblAmount1 = objAP.calculateEEPF(con, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmTotal, strEmpId, null, null, false, null);
										dblAmount1 = Math.round(dblAmount1);
										double dblYearAmount1 = dblAmount1 * 12;
										
										deductAmount += dblAmount1;
										deductYearAmount += dblYearAmount1;
										
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount1));
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount1));
										innerList.add(rs.getString("salary_head_id"));
										hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount1));
										
										break;
									
	//								case EMPLOYER_EPF :
	//									
	//									double dblAmount2 = objAP.calculateERPF(con, CF, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, getEmpId(), null, null, false, null);
	//									dblAmount2 = Math.round(dblAmount2);
	//									double dblYearAmount2 = dblAmount2 * 12;
	//									
	//									deductAmount += dblAmount2;
	//									deductYearAmount += dblYearAmount2;
	//									
	//									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount2));
	//									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount2));
	//									innerList.add(rs.getString("salary_head_id"));
	//									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount2));
	//									
	//									break;  
									
	//								case EMPLOYER_ESI :
	//									
	//									double dblAmount3 = objAP.calculateERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId,getEmpId());
	//									dblAmount3 = Math.round(dblAmount3);
	//									double dblYearAmount3 = dblAmount3 * 12;
	//									
	//									deductAmount += dblAmount3;
	//									deductYearAmount += dblYearAmount3;
	//									
	//									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount3));
	//									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount3));
	//									innerList.add(rs.getString("salary_head_id"));
	//									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount3));
	//									
	//									break;
									
									case EMPLOYEE_ESI :
										isESIC = true;
										double dblAmount4 = objAP.calculateEEESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, strEmpId);
										dblAmount4 = Math.ceil(dblAmount4);
										double dblYearAmount4 = dblAmount4 * 12;
										dblYearAmount4 = Math.ceil(dblYearAmount4);
										
										deductAmount += dblAmount4;
										deductYearAmount += dblYearAmount4;
	//									System.out.println("dblAmount4====>"+dblAmount4);
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount4));
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount4));
										innerList.add(rs.getString("salary_head_id"));
										hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount4));
										
										break;
									
	//								case EMPLOYER_LWF :
	//									
	//									double dblAmount5 = objAP.calculateERLWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, nPayMonth);
	//									dblAmount5 = Math.round(dblAmount5);
	//									double dblYearAmount5 = dblAmount5 * 12;
	//									
	//									deductAmount += dblAmount5;
	//									deductYearAmount += dblYearAmount5;
	//									
	//									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount5));
	//									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount5));
	//									innerList.add(rs.getString("salary_head_id"));
	//									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount5));
	//									
	//									break;
									
									case EMPLOYEE_LWF :
										isLWF = true;
										double dblAmount6 = objAP.calculateEELWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, strEmpId, nPayMonth, strOrg);
										dblAmount6 = Math.round(dblAmount6);
										double dblYearAmount6 = dblAmount6 * 12;
										
										deductAmount += dblAmount6;
										deductYearAmount += dblYearAmount6;
										
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount6));
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount6));
										innerList.add(rs.getString("salary_head_id"));
										hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount6));
										
										break;
									
									case TDS :
										
	//									double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
	//									double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
										double dblHRA = uF.parseToDouble(hmTotal.get(HRA+""));
										
										String[] hraSalaryHeads = null;
										if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null){
											hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
										}
										
										double dblHraSalHeadsAmount = 0;
										for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++){
											dblHraSalHeadsAmount += uF.parseToDouble((String)hmTotal.get(hraSalaryHeads[i]));
										}
										
										Map<String, String> hmPaidSalaryDetails =  hmEmpPaidAmountDetails.get(strEmpId);
										if(hmPaidSalaryDetails==null){hmPaidSalaryDetails=new HashMap<String, String>();}
										
										double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_EDU_TAX"));
										double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_STD_TAX"));
										double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_FLAT_TDS"));
										 
										if(hmEmpServiceTaxMap.containsKey(strEmpId)){
											dblGrossTDS = grossAmount;
											double  dblServiceTaxAmount = uF.parseToDouble(hmTotal.get(SERVICE_TAX+""));
											dblGrossTDS = dblGrossTDS - dblServiceTaxAmount;
											
											double dblSwachhaBharatCess = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS+""));
											dblGrossTDS = dblGrossTDS - dblSwachhaBharatCess;
											
											double dblKrishiKalyanCess = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS+""));
											dblGrossTDS = dblGrossTDS - dblKrishiKalyanCess;
										}
										
//										double dblAmount7 = objAP.calculateTDS(con, uF,strD2,strD1, dblGrossTDS, dblCess1, dblCess2, dblFlatTDS, dblInvestmentExemption, dblHRA, dblHraSalHeadsAmount,
//												nPayMonth,
//												strD1, strFinancialYearStart, strFinancialYearEnd, strEmpId, hmEmpGenderMap.get(strEmpId),  hmEmpAgeMap.get(strEmpId), strStateId,
//												hmEmpExemptionsMap, hmEmpHomeLoanMap, hmFixedExemptions, hmEmpMertoMap, hmEmpRentPaidMap, hmPaidSalaryDetails,
//												hmTotal, hmSalaryDetails, hmEmpLevelMap, CF,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount,hmEmpIncomeOtherSourcesMap,hmOtherTaxDetails,hmEmpStateMap);
										double dblAmount7 = objAP.calculateTDS(con, CF, uF, dblGrossTDS, dblFlatTDS, nPayMonth, strFinancialYearStart,
												strFinancialYearEnd, strEmpId, hmEmpLevelMap);
										dblAmount7 = Math.round(dblAmount7);
										double dblYearAmount7 = dblAmount7 * 12;
										
										deductAmount += dblAmount7;
										deductYearAmount += dblYearAmount7;
										
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount7));
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount7));
										innerList.add(rs.getString("salary_head_id"));
										hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount7));
										
										break;
									
									default:
										
										double dblAmount9 = uF.parseToDouble(rs.getString("amount"));
										double dblYearAmount9 = dblAmount9 * 12;
										
										deductAmount += dblAmount9;
										deductYearAmount += dblYearAmount9;
										
										innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblAmount9));
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount9));
										innerList.add(rs.getString("salary_head_id"));
										hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount9));
										
										break;
								}
							} else {
								innerList.add("0.0");
								innerList.add("0.0");
								innerList.add(rs.getString("salary_head_id"));
								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
							}
							
							salaryHeadDetailsList.add(innerList);
						}
						
					}
					rs.close();
					pst.close();
		
					hmSalaryTotal.put("GROSS_AMOUNT", grossAmount);
					hmSalaryTotal.put("GROSS_YEAR_AMOUNT", grossYearAmount);
					hmSalaryTotal.put("DEDUCT_AMOUNT", deductAmount);
					hmSalaryTotal.put("DEDUCT_YEAR_AMOUNT", deductYearAmount);
					
					
	//				System.out.println("salaryHeadDetailsList======>"+salaryHeadDetailsList);
	//				System.out.println("salaryAnnualVariableDetailsList======>"+salaryAnnualVariableDetailsList);
					/**
					 * Employer Contribution
					 * */ 
					Map<String,String> hmContribution = new HashMap<String, String>();
					if(isEPF){
	//					double dblAmount = objAP.calculateERPFandEPS(con, CF, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, getEmpId(), null, null, false, null);
						double dblAmount = objAP.calculateERPF(con,CF, null, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, null);
						dblAmount = Math.round(dblAmount);
						double dblYearAmount = dblAmount * 12;
						hmContribution.put("EPF_MONTHLY", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
						hmContribution.put("EPF_ANNUALY", uF.formatIntoTwoDecimalWithOutComma(dblYearAmount));
					}
					if(isESIC){
						double dblAmount = objAP.calculateERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, strEmpId, null, null);
						dblAmount = Math.ceil(dblAmount);
						double dblYearAmount = dblAmount * 12;
						dblYearAmount = Math.ceil(dblYearAmount);
						
						hmContribution.put("ESI_MONTHLY", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
						hmContribution.put("ESI_ANNUALY", uF.formatIntoTwoDecimalWithOutComma(dblYearAmount));
					}
					if(isLWF){
						double dblAmount = objAP.calculateERLWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, nPayMonth, strOrg);
						dblAmount = Math.round(dblAmount);
						double dblYearAmount = dblAmount * 12;
						hmContribution.put("LWF_MONTHLY", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
						hmContribution.put("LWF_ANNUALY", uF.formatIntoTwoDecimalWithOutComma(dblYearAmount));				
					}
					
					
					/**
					 * Employer Contribution End
					 * */ 
					
					
					
					/**
					 * Set Prev CTC
					 * */
					grossAmount = 0.0d;
					grossYearAmount = 0.0d;
					deductAmount = 0.0d;
					deductYearAmount = 0.0d;
					
					for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
						List<String> innerList = salaryHeadDetailsList.get(i);
						if(innerList.get(1).equals("E")) {
							grossAmount +=uF.parseToDouble(innerList.get(2));
							grossYearAmount +=uF.parseToDouble(innerList.get(3));
						} else if(innerList.get(1).equals("D")) {
							double dblDeductMonth = 0.0d;
							double dblDeductAnnual = 0.0d;
							if(uF.parseToInt(innerList.get(4)) == EMPLOYEE_ESI){
								dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
								dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
							} else if(uF.parseToInt(innerList.get(4)) == EMPLOYER_ESI){
								dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
								dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
							} else {
								dblDeductMonth += Math.round(uF.parseToDouble(innerList.get(2)));
								dblDeductAnnual += Math.round(uF.parseToDouble(innerList.get(3)));
							}
							deductAmount += dblDeductMonth;
							deductYearAmount += dblDeductAnnual;
						}
					}
					
					double dblMonthContri = 0.0d;
					double dblAnnualContri = 0.0d;
					if(isEPF || isESIC || isLWF){
						if(isEPF){
							double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
							double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
							dblMonthContri += dblEPFMonth;
							dblAnnualContri += dblEPFAnnual;
						}
						if(isESIC){
							double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
							double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
							dblMonthContri += dblESIMonth;
							dblAnnualContri += dblESIAnnual;
						}
						if(isLWF){
							double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
							double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
							dblMonthContri += dblLWFMonth;
							dblAnnualContri += dblLWFAnnual;
						}
					}
					
					double dblCTCMonthly = grossAmount + dblMonthContri;
					double dblCTCAnnualy = grossYearAmount + dblAnnualContri;
					
					int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
					if(nAnnualVariSize > 0){
						double grossAnnualAmount = 0.0d;
						double grossAnnualYearAmount = 0.0d;
						for(int i = 0; i < nAnnualVariSize; i++){
							List<String> innerList = salaryAnnualVariableDetailsList.get(i);
							double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
							double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
							grossAnnualAmount += dblEarnMonth;
							grossAnnualYearAmount += dblEarnAnnual;
						}
						dblCTCMonthly += grossAnnualAmount;
						dblCTCAnnualy += grossAnnualYearAmount;
					}
					
					hmPrevCTC.put("PREV_MONTH_CTC", uF.formatIntoTwoDecimalWithOutComma(dblCTCMonthly));
					hmPrevCTC.put("PREV_ANNUAL_CTC", uF.formatIntoTwoDecimalWithOutComma(dblCTCAnnualy));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
//		System.out.println("hmPrevCTC=====>"+hmPrevCTC);
		return hmPrevCTC;
	}
	
	private double getAnnualProfessionalTax(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, String strStateId, String strEmpGender) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblDeductionAnnual= 0;
		try {
			pst = con.prepareStatement("select * from deduction_details_india where income_from<=? and income_to>=? and state_id=? " +
					"and financial_year_from=? and financial_year_to=? and gender =? limit 1");
			pst.setDouble(1, dblGross);
			pst.setDouble(2, dblGross);
			pst.setInt(3, uF.parseToInt(strStateId));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(6, strEmpGender);			
			rs = pst.executeQuery();  
			while(rs.next()){
				dblDeductionAnnual = rs.getDouble("deduction_amount");
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblDeductionAnnual;
	}
	
	private double calculateProfessionalTax(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd,
			int nPayMonth, String strStateId, String strEmpGender) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblAmount= 0;
		
		
		try {
			
			pst = con.prepareStatement("select * from deduction_details_india where income_from<=? and income_to>=? and state_id=? " +
					"and financial_year_from=? and financial_year_to=? and gender =? limit 1");
			pst.setDouble(1, dblGross);
			pst.setDouble(2, dblGross);
			pst.setInt(3, uF.parseToInt(strStateId));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(6, strEmpGender);
			rs = pst.executeQuery();  
			while(rs.next()){
				dblAmount = rs.getDouble("deduction_paycycle");
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblAmount;
	}

	public void viewSalaryApprovalDetails(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
//		EncryptionUtils encryption = new EncryptionUtils();// Created By Dattatray Date : 20-July-2021 Note : Encryption
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpCodes = CF.getEmpCodeMap(con); 
			Map<String, String> hmEmpDesig = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(amount) as amount, esd.emp_id, esd.effective_date, esd.is_approved, esd.approved_date,esd.approved_by, esd.entry_date " +
					"from emp_salary_details esd,(select effective_date,emp_id, is_approved from emp_salary_details group by emp_id, effective_date, is_approved ) mesd " +
					"where mesd.effective_date = esd.effective_date and mesd.emp_id = esd.emp_id and mesd.is_approved = esd.is_approved " +
					"and esd.salary_head_id in (select salary_head_id from salary_details where earning_deduction = 'E' and salary_head_id not in ("+CTC+","+GROSS+"))  ");
			
			if(getStrStartDate()!=null && !getStrStartDate().trim().equals("") && !getStrStartDate().trim().equalsIgnoreCase("NULL") && !getStrStartDate().trim().equals("From Date") && getStrEndDate()!=null && !getStrEndDate().trim().equals("") && !getStrEndDate().trim().equalsIgnoreCase("NULL") && !getStrEndDate().trim().equals("To Date")){
				sbQuery.append(" and esd.effective_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'");
			}
			if(uF.parseToInt(getStrSelectedEmpId())>0){
				sbQuery.append(" and esd.emp_id = "+uF.parseToInt(getStrSelectedEmpId()));
			} else {
				sbQuery.append(" and esd.emp_id in (select eod.emp_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id ");
				if(uF.parseToInt(getF_org())>0){
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            if(getF_department()!=null && getF_department().length>0) {
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            if(getF_level()!=null && getF_level().length>0) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }
	            if(getF_service()!=null && getF_service().length>0) {
	                sbQuery.append(" and (");
	                for(int i=0; i<getF_service().length; i++) {
	                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
	                    
	                    if(i<getF_service().length-1) {
	                        sbQuery.append(" OR "); 
	                    }
	                }
	                sbQuery.append(") ");
	            }
	            sbQuery.append(") ");
			}
			if(uF.parseToInt(getLeaveStatus())==2) {
				sbQuery.append(" and esd.is_approved=FALSE");
			} else if(uF.parseToInt(getLeaveStatus())==1) {
				sbQuery.append(" and esd.is_approved=TRUE");
			}
			sbQuery.append(" and isdisplay=true group by esd.emp_id, esd.effective_date, esd.is_approved, esd.approved_date, esd.entry_date, esd.approved_by order by  effective_date");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			List<List<String>> alReport = new ArrayList<List<String>>();
			int count=0;
			while(rs.next()){
				if(hmEmpNames.get(rs.getString("emp_id"))==null){
					continue;
				}
				String strCurrency = "";
				if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp_id"))) > 0){
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp_id")));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				}
				List<String> alInner = new ArrayList<String>();
				alInner.add(uF.showData(hmEmpCodes.get(rs.getString("emp_id")), "NA"));
				alInner.add(hmEmpNames.get(rs.getString("emp_id")));
				alInner.add(uF.showData(hmEmpDesig.get(rs.getString("emp_id")), "NA"));
				alInner.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(strCurrency+uF.formatIntoComma(uF.parseToDouble(rs.getString("amount"))));
				count++;

				if(!uF.parseToBoolean(rs.getString("is_approved"))) {
					alInner.add("");
					alInner.add("");
					/*alInner.add("<div id=\"myDiv_"+count+"\" style=\"float:left\"><img src=\"images1/icons/pending.png\" title=\"Waiting for Approval\" onclick=\"(confirm('Are you Sure you want to approve this salary?')?getContent('myDiv_"+count+"', 'UpdateEmpSalaryApproval.action?strEmpId="+rs.getString("emp_id")+"&strEffectiveDate="+rs.getString("effective_date")+"&strEntryDate="+rs.getString("entry_date")+"&status=1'):'')\" ></div><a href=\"MyProfile.action?empId="+rs.getString("emp_id")+"\" class=\"factsheet\"> </a>");*/
					// Created By Dattatray Date : 21-July-2021 Note : empId Encryption encryption.encrypt(rs.getString("emp_id"))
					alInner.add("<div id=\"myDiv_"+count+"\" style=\"float:left\"><a href=\"javascript:void(0);\" onclick=\"(confirm('Are you Sure you want to approve this salary?')?getContent('myDiv_"+count+"', 'UpdateEmpSalaryApproval.action?strEmpId="+rs.getString("emp_id")+"&strEffectiveDate="+rs.getString("effective_date")+"&strEntryDate="+rs.getString("entry_date")+"&status=1'):'')\" > <i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\"></i> </a></div><a href=\"MyProfile.action?empId="+rs.getString("emp_id")+"\" class=\"factsheet\"> </a>");
					
					alInner.add("Pending");
				} else {
					alInner.add(uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()));
					alInner.add(hmEmpNames.get(rs.getString("approved_by")));
					/*alInner.add("<div id=\"myDiv_"+count+"\" style=\"float:left;width:20px;\"><img src=\"images1/icons/pullout.png\" title=\"Pull out approval\" onclick=\"(confirm('Are you Sure you want to pull out this salary approval?')?getContent('myDiv_"+count+"', 'UpdateEmpSalaryApproval.action?strEmpId="+rs.getString("emp_id")+"&strEffectiveDate="+rs.getString("effective_date")+"&strEntryDate="+rs.getString("entry_date")+"&status=0'):'')\" ></div><img src=\"images1/icons/approved.png\" title=\"Approved\"><a href=\"MyProfile.action?empId="+rs.getString("emp_id")+"\" class=\"factsheet\"> </a>");*/
					// Created By Dattatray Date : 21-July-2021 Note : empId Encryption encryption.encrypt(rs.getString("emp_id"))
					alInner.add("<div id=\"myDiv_"+count+"\" style=\"float:left;width:20px;\"><a href=\"javascript:void(0);\"  onclick=\"(confirm('Are you Sure you want to pull out this salary approval?')?getContent('myDiv_"+count+"', 'UpdateEmpSalaryApproval.action?strEmpId="+rs.getString("emp_id")+"&strEffectiveDate="+rs.getString("effective_date")+"&strEntryDate="+rs.getString("entry_date")+"&status=0'):'')\"> <i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ff9b02\" title=\"Pull out approval\"></i></a></div><i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Approved\" style=\"color:#54aa0d\"></i><a href=\"MyProfile.action?empId="+rs.getString("emp_id")+"\" class=\"factsheet\"> </a>");
					alInner.add("Approved");
				}
				
				alInner.add(rs.getString("emp_id"));      
				alInner.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
				
				alReport.add(alInner);
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReport", alReport);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getLeaveStatus() {
		return leaveStatus;
	}

	public void setLeaveStatus(String leaveStatus) {
		this.leaveStatus = leaveStatus;
	}

	public String getStrStartDate() {
		return strStartDate;
	}

	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}

	public String getStrEndDate() {
		return strEndDate;
	}

	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
	}

	public String getStrSelectedEmpId() {
		return strSelectedEmpId;
	}

	public void setStrSelectedEmpId(String strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}

	public String getApproveSubmit() {
		return approveSubmit;
	}

	public void setApproveSubmit(String approveSubmit) {
		this.approveSubmit = approveSubmit;
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

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
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

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	
}
