package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
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

public class AnnualVariableForm extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId = null;
	String strUserType = null;

	CommonFunctions CF = null;
	String profileEmpId;

	private String strLocation;
	private String strDepartment;
	private String strLevel;
	
	private String[] f_strWLocation;
	private String[] f_level;
	private String[] f_department;
//	String[] f_service;
	private String paycycle;

	private List<FillPayCycles> paycycleList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillServices> serviceList;
	private List<FillWLocation> wLocationList;
	private List<FillSalaryHeads> salaryHeadList;

	private List<FillOrganisation> orgList;
	private String f_salaryhead;
	private  String f_org;
	
	private String strPaycycleDuration;
	private List<FillPayCycleDuration> paycycleDurationList;


	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/payroll/AnnualVariableForm.jsp");
		request.setAttribute(TITLE, "Annual Variable Form");
		
		UtilityFunctions uF = new UtilityFunctions();

		strSessionEmpId = (String) session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(USERTYPE);
		
		String formType = (String) request.getParameter("formType");

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
		 
		if(getStrPaycycleDuration()==null || getStrPaycycleDuration().trim().equals("") || getStrPaycycleDuration().trim().equalsIgnoreCase("")){
			setStrPaycycleDuration("M");
		}
		
		salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsByAnnualVaribles(true,null,getF_org());
		if ((getF_salaryhead() == null || getF_salaryhead().equals("")) && salaryHeadList != null && !salaryHeadList.isEmpty()) {
			setF_salaryhead(salaryHeadList.get(0).getSalaryHeadId());
		}
		if(uF.parseToInt(getF_salaryhead()) > 0){
			if(formType != null && formType.trim().equalsIgnoreCase("approve")){
				approveAnnualVariableAmount(uF);
			}
			viewAnnualVariable(uF);
		}
		
		return loadAnnualVariable(uF);
	}

	private void approveAnnualVariableAmount(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			String[] strPayCycleDates = null;			
			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL") 
					&& !getPaycycle().trim().equalsIgnoreCase("NULL-NULL-NULL")) {
				strPayCycleDates = getPaycycle().split("-");
			}  
			
			con = db.makeConnection(con);
			String[] strEmpIds = request.getParameterValues("strEmpIds");
			if(strEmpIds !=null && strEmpIds.length >0 && strPayCycleDates !=null) {
				List<String> alEmp = Arrays.asList(strEmpIds);
				if(alEmp == null) alEmp = new ArrayList<String>();
				int nEmpSize = alEmp.size();
				if(nEmpSize > 0){
					pst = con.prepareStatement("select earning_deduction from salary_details where salary_head_id=? limit 1 ");
					pst.setInt(1, uF.parseToInt(getF_salaryhead()));
					rs = pst.executeQuery();
					String earn_deduct=null;
					while(rs.next()){
						earn_deduct=rs.getString("earning_deduction");
					}
		            rs.close();
		            pst.close();
					
					for(int i = 0; i < nEmpSize; i++){
						String strEmpId = alEmp.get(i);
						String strAmount = (String) request.getParameter("strIncentiveAmount_"+strEmpId);
						
						pst = con.prepareStatement("insert into annual_variable_individual_details (emp_id, pay_paycycle, salary_head_id, amount, pay_amount, " +
								"added_by, entry_date, paid_from, paid_to, is_approved, earning_deduction, approved_by, approved_date) " +
								"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
						pst.setInt(3, uF.parseToInt(getF_salaryhead()));
						pst.setDouble(4, uF.parseToDouble(strAmount));
						pst.setDouble(5, uF.parseToDouble(strAmount));
						pst.setInt(6, uF.parseToInt(strSessionEmpId));
						pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setDate(8, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
						pst.setDate(9, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
						pst.setInt(10, 1);
						pst.setString(11, earn_deduct);
						pst.setInt(12, uF.parseToInt(strSessionEmpId));
						pst.setDate(13, uF.getCurrentDate(CF.getStrTimeZone()));
//						System.out.println("pst===>"+pst);
						pst.execute();
			            pst.close();		
					}
				}
			}
		}catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadAnnualVariable(UtilityFunctions uF) {

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
		
		getSelectedFilter(uF);
		
		return LOAD;
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
			//StringBuffer sb = new StringBuffer();
//			System.out.println("getPaycycle==>"+getPaycycle());
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			
			strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
			/*sb.append(strPaycycle);
			if(strPayCycleDates[0] != null && !strPayCycleDates[0].equals("") && !strPayCycleDates[0].equalsIgnoreCase("null")) {
				sb.append(uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()));
			}
			
			if(strPayCycleDates[1] != null && !strPayCycleDates[1].equals("") && !strPayCycleDates[1].equalsIgnoreCase("null")) {
				sb.append("-"+uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
			}
			System.out.println("sb==>"+sb);
			if(sb != null && !sb.toString().equals("") && !sb.toString().equalsIgnoreCase("null")) {
				hmFilter.put("PAYCYCLE", sb.toString());
			}*/
			hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		} else {
			hmFilter.put("PAYCYCLE", "All Paycycles");
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
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	
	public String viewAnnualVariable(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			con = db.makeConnection(con);

			String[] strPayCycleDates;			
			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycleUsingDurationByOrg(con, CF.getStrTimeZone(), CF, getF_org(),getStrPaycycleDuration(), request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
			}   
			
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			String[] strFinancialYear = CF.getFinancialYear(con, strPayCycleDates[1], CF, uF);
			if (strFinancialYear != null) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM"))-1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			
			List<String> alMonth = new ArrayList<String>();
			List<Date> alDate = new ArrayList<Date>();
			for(int i=0; i<12;i++){
				String strDate = cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR);
				alMonth.add(uF.getDateFormat(strDate, DATE_FORMAT, "MM"));
				
				int nMonthStart = cal.getActualMinimum(Calendar.DATE);
				int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
				int nMonth = (cal.get(Calendar.MONTH) + 1);
				String strDateStart =  (nMonthStart < 10 ? "0"+nMonthStart : nMonthStart)+"/"+(nMonth < 10 ? "0"+nMonth : nMonth)+"/"+cal.get(Calendar.YEAR);				
				alDate.add(uF.getDateFormat(strDateStart, DATE_FORMAT));
				
				cal.add(Calendar.MONTH, 1);
			}
			
			int i1 = 0;
			String strFYStartPaycycleDate = null;
			String strFYEndPaycycleDate = null;
			for(Date ad : alDate){
				i1++;
				String strDateStart = uF.getDateFormat(""+ad, DBDATE, DATE_FORMAT);
				String[] strPayCycleDates22 = CF.getPayCycleFromDate(con, strDateStart, CF.getStrTimeZone(), CF, getF_org());
				
				if(i1 == 1){
					strFYStartPaycycleDate = strPayCycleDates22[0];
				} else if(i1 == 12){
					strFYEndPaycycleDate = strPayCycleDates22[1];
				}
			}
//			System.out.println("strFYStartPaycycleDate==>"+strFYStartPaycycleDate+"--strFYEndPaycycleDate==>"+strFYEndPaycycleDate);
			
			Map<String, String> hmEmpMap = CF.getEmpNameMap(con,null, null);
//			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con);
			
			pst = con.prepareStatement("select emp_id from payroll_generation where paid_from = ? and paid_to = ? and paycycle = ? group by emp_id order by emp_id ");
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
			
			pst = con.prepareStatement("select salary_head_id, amount, esd.emp_id from emp_salary_details esd, (select max(entry_date) as max_date, emp_id from emp_salary_details group by emp_id ) as b where esd.entry_date = b.max_date and b.emp_id = esd.emp_id and isdisplay = true order by esd.emp_id, salary_head_id ");
			rs = pst.executeQuery();
			Map<String, List<String>> hmSalaryList = new HashMap<String, List<String>>();
			List<String> alSalaryList = new ArrayList<String>();
			String strEmpIdOld = null;
			String strEmpIdNew = null;
			while (rs.next()) {
				strEmpIdNew = rs.getString("emp_id");
				if (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					alSalaryList = new ArrayList<String>();
				}

				alSalaryList.add(rs.getString("salary_head_id"));
				hmSalaryList.put(strEmpIdNew, alSalaryList);

				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
			request.setAttribute("hmSalaryList", hmSalaryList);
//			request.setAttribute("hmSalaryHeadsMap", hmSalaryHeadsMap);

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive = true and joining_date<= ? ");
			if(getStrPaycycleDuration()!=null){
				sbQuery.append(" and eod.paycycle_duration ='"+getStrPaycycleDuration()+"'");
			}
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            
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
            		" from emp_salary_details where isdisplay = true and is_approved=true and effective_date <=? group by emp_id ) as b where esd.effective_date = b.max_date " +
            		"and b.emp_id = esd.emp_id and isdisplay = true and is_approved=true and esd.salary_head_id=? and esd.effective_date <=?)");
			sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_salaryhead()));
			pst.setDate(4,  uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			System.out.println("pst===>" + pst); 
			rs = pst.executeQuery();
			List<List<String>> alEmpReport = new ArrayList<List<String>>();
			List<String> alEmp = new ArrayList<String>();
			while (rs.next()) {
				List<String> alEmpReportInner = new ArrayList<String>();
				alEmpReportInner.add(rs.getString("emp_per_id"));
				alEmpReportInner.add(hmEmpMap.get(rs.getString("emp_per_id")));

				alEmpReport.add(alEmpReportInner);
				
				if(!alEmp.contains(rs.getString("emp_per_id")) && uF.parseToInt(rs.getString("emp_per_id")) > 0){
					alEmp.add(rs.getString("emp_per_id"));
				}
			}
			rs.close();
			pst.close();

			request.setAttribute("alEmpReport", alEmpReport);

			if(alEmp.size() > 0){
				String strEmpIds = StringUtils.join(alEmp.toArray(),",");
				
				pst = con.prepareStatement("select * from annual_variable_individual_details where emp_id in ("+strEmpIds+") and paid_from = ? and paid_to=? and pay_paycycle=? and salary_head_id=?");
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
				pst.setInt(4, uF.parseToInt(getF_salaryhead()));
//				System.out.println("pst===>" + pst); 
				rs = pst.executeQuery();
				Map<String, String> hmAnnualVariable = new HashMap<String, String>();
				Map<String, String> hmAnnualVariableId = new HashMap<String, String>();
				Map<String, String> hmAnnualVariableValue = new HashMap<String, String>();
				while (rs.next()) {
					hmAnnualVariable.put(rs.getString("emp_id"),rs.getString("is_approved"));
					hmAnnualVariableId.put(rs.getString("emp_id"),rs.getString("annual_vari_ind_id"));
					hmAnnualVariableValue.put(rs.getString("emp_id"), uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("pay_amount"))));
				}
				rs.close();
				pst.close();
				
				request.setAttribute("hmAnnualVariable", hmAnnualVariable);
				request.setAttribute("hmAnnualVariableId", hmAnnualVariableId);
				request.setAttribute("hmAnnualVariableValue", hmAnnualVariableValue);
				
				pst = con.prepareStatement("select * from level_details ld " +
						"right join (select * from designation_details dd " +
						"right join (select *, gd.designation_id as designationid " +
						"from employee_official_details eod, grades_details gd, employee_personal_details epd " +
						"where gd.grade_id=eod.grade_id and eod.emp_id = epd.emp_per_id " +
						"and eod.emp_id in("+strEmpIds+")) a on a.designationid=dd.designation_id) a on a.level_id=ld.level_id");
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				Map<String, String> hmEmpLevel = new HashMap<String, String>();
				List<String> alLevel = new ArrayList<String>();
				Map<String, String> hmEmpJoiningDate = new HashMap<String, String>();
				while (rs.next()) {
					hmEmpLevel.put(rs.getString("emp_id"), rs.getString("level_id"));
					
					if(!alLevel.contains(rs.getString("level_id")) && uF.parseToInt(rs.getString("level_id")) > 0){
						alLevel.add(rs.getString("level_id"));
					}
					
					if(rs.getString("joining_date") !=null && !rs.getString("joining_date").trim().equals("") 
							&& !rs.getString("joining_date").trim().equalsIgnoreCase("NULL") 
							&& uF.isDateBetween(uF.getDateFormatUtil(strFinancialYearStart, DATE_FORMAT), uF.getDateFormatUtil(strFinancialYearEnd, DATE_FORMAT), uF.getDateFormatUtil(rs.getString("joining_date"), DBDATE))){
						hmEmpJoiningDate.put(rs.getString("emp_id"), uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
					}
				}
				rs.close();
				pst.close();				
				request.setAttribute("hmEmpLevel", hmEmpLevel);
//				System.out.println("hmEmpJoiningDate==>"+hmEmpJoiningDate);
				
				if(alLevel.size() > 0){
					String strLevelIds = StringUtils.join(alLevel.toArray(),",");
					
					sbQuery = new StringBuilder();
					sbQuery.append("select sum(pay_amount) as amount, emp_id from annual_variable_individual_details where salary_head_id=? " +
							"and paid_from>=? and paid_to<=? and salary_head_id in (select salary_head_id from salary_details where salary_head_id=? "+
							"and level_id in ("+strLevelIds+")");
					if(uF.parseToInt(getF_org())>0){
						sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
					}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					}
					sbQuery.append(" and is_annual_variable=true  and (is_delete is null or is_delete = false)) " +
							"and emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
							"where epd.emp_per_id = eod.emp_id ");
					if(uF.parseToInt(getF_org())>0){
						sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
					}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
						sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					}
					sbQuery.append(" and eod.emp_id in ("+strEmpIds+") and eod.grade_id in (select gd.grade_id from grades_details gd, " +
							"level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  " +
							"and ld.level_id in ("+strLevelIds+"))) group by emp_id");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(getF_salaryhead()));
					pst.setDate(2, uF.getDateFormat(strFYStartPaycycleDate, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strFYEndPaycycleDate, DATE_FORMAT));
					pst.setInt(4, uF.parseToInt(getF_salaryhead()));
//					System.out.println("pst==>"+pst);
					rs = pst.executeQuery();
					Map<String, String> hmFYAnnualEmpAmt = new HashMap<String, String>();
					while(rs.next()){
						hmFYAnnualEmpAmt.put(rs.getString("emp_id"), rs.getString("amount"));
					}
					rs.close();
					pst.close();
//					System.out.println("hmFYAnnualEmpAmt==>"+hmFYAnnualEmpAmt);
					request.setAttribute("hmFYAnnualEmpAmt", hmFYAnnualEmpAmt);	
					
					sbQuery = new StringBuilder();
					sbQuery.append("select * from annual_variable_details where salary_head_id=? and level_id in ("+strLevelIds+") ");
					if(uF.parseToInt(getF_org())>0){
						sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
					}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					}
					sbQuery.append(" and financial_year_start=? and financial_year_end=? and salary_head_id in (select salary_head_id " +
							"from salary_details where salary_head_id=? and level_id in ("+strLevelIds+") ");
					if(uF.parseToInt(getF_org())>0){
						sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
					}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					}
					sbQuery.append(" and is_annual_variable=true  and (is_delete is null or is_delete = false) and (is_contribution is null or is_contribution=false))");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(getF_salaryhead()));
					pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(4, uF.parseToInt(getF_salaryhead()));
//					System.out.println("pst==>"+pst);
					rs = pst.executeQuery();
					Map<String, String> hmAnnualPolicyEmpAmt = new HashMap<String, String>();
					while(rs.next()){
						hmAnnualPolicyEmpAmt.put(rs.getString("emp_id"), rs.getString("variable_amount"));
						if(hmEmpJoiningDate.containsKey(rs.getString("emp_id"))){
							String empStartMonth = uF.getDateFormat(hmEmpJoiningDate.get(rs.getString("emp_id")), DATE_FORMAT, "MM");
							boolean startFlag = false;
							int nMonth = 0;
							for(int i=0; i<alMonth.size(); i++){
								String strMonth = alMonth.get(i); 
								
								if(empStartMonth!=null && empStartMonth.equals(strMonth)){
									startFlag = true;
								}
								
								if(!startFlag){
									continue;
								}
								nMonth++;
							}
//							System.out.println("emp_id==>"+rs.getString("emp_id")+"--nMonth==>"+nMonth);
							if(nMonth > 0){
								double dblAmt = uF.parseToDouble(rs.getString("variable_amount")) > 0.0d ? uF.parseToDouble(rs.getString("variable_amount")) / 12 : 0.0d;
								double dblVariableAmt = 0.0d;
								if(dblAmt > 0){
									dblVariableAmt = dblAmt * nMonth;
								}
								hmAnnualPolicyEmpAmt.put(rs.getString("emp_id")+"_ANNUAL", uF.formatIntoZeroWithOutComma(dblVariableAmt));
							}
						} else {
							hmAnnualPolicyEmpAmt.put(rs.getString("emp_id")+"_ANNUAL", rs.getString("variable_amount"));
						}
					}
					rs.close();
					pst.close();
					request.setAttribute("hmAnnualPolicyEmpAmt", hmAnnualPolicyEmpAmt);	
				}
			}
			
			pst = con.prepareStatement("select * from salary_details where salary_head_id=? and is_annual_variable=true and (is_delete is null or is_delete = false) and (is_contribution is null or is_contribution=false)");
			pst.setInt(1, uF.parseToInt(getF_salaryhead()));
			rs = pst.executeQuery();
			String sHeadType="";
			while(rs.next()){
				sHeadType=rs.getString("earning_deduction");
			}
			rs.close();
			pst.close();
			request.setAttribute("sHeadType", sHeadType);
			

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

}
