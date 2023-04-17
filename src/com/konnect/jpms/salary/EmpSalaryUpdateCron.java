package com.konnect.jpms.salary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.reports.MyProfile;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class EmpSalaryUpdateCron extends Thread implements IStatements,ServletRequestAware {
	
	public CommonFunctions CF;
	public HttpServletRequest request; 
	public HttpSession session;
	public String strDomain;
	public String strLevelId;
	public String strGradeId;
	public String strSalaryHeadId;
	public String strType;
	public double dblAmt;
	public String autoUpdate;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public EmpSalaryUpdateCron() {
		super();
	}
	
	public void setEmpSalaryUpdateCronData() {
		if(!isAlive()){
			start();
		}
	}

	@Override
	public void run() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
			System.out.println("nSalaryStrucuterType==>"+nSalaryStrucuterType);
			if(nSalaryStrucuterType == S_GRADE_WISE){
				if(getStrType() != null && getStrType().trim().equalsIgnoreCase("edit") && uF.parseToInt(getStrGradeId()) > 0 && uF.parseToInt(getStrSalaryHeadId()) > 0){
					updateSalaryUpdatePercentageByGrade(con, uF, uF.parseToInt(getStrGradeId()), uF.parseToInt(getStrSalaryHeadId()));
				} else if(getStrType() != null && getStrType().trim().equalsIgnoreCase("add") && uF.parseToInt(getStrGradeId()) > 0 && uF.parseToInt(getStrSalaryHeadId()) > 0){
					addNewSalaryHeadToEmployeeByGrade(con, uF, uF.parseToInt(getStrGradeId()), uF.parseToInt(getStrSalaryHeadId()));
				} else if(getStrType() != null && getStrType().trim().equalsIgnoreCase("addStatutoryHead") && uF.parseToInt(getStrGradeId()) > 0 && uF.parseToInt(getStrSalaryHeadId()) > 0){
					addNewStatutorySalaryHeadToEmployeeByGrade(con, uF, uF.parseToInt(getStrGradeId()), uF.parseToInt(getStrSalaryHeadId()));
				} else if(getStrType() != null && getStrType().trim().equalsIgnoreCase("addSalaryBasis") && uF.parseToInt(getStrGradeId()) > 0 && uF.parseToInt(getStrSalaryHeadId()) > 0){
					addNewBasisSalaryHeadToEmployeeByGrade(con, uF, uF.parseToInt(getStrGradeId()), uF.parseToInt(getStrSalaryHeadId()));
				}
			} else {
				if(getStrType() != null && getStrType().trim().equalsIgnoreCase("edit") && uF.parseToInt(getStrLevelId()) > 0 && uF.parseToInt(getStrSalaryHeadId()) > 0){
					updateSalaryUpdatePercentage(con, uF, uF.parseToInt(getStrLevelId()), uF.parseToInt(getStrSalaryHeadId()));
				} else if(getStrType() != null && getStrType().trim().equalsIgnoreCase("add") && uF.parseToInt(getStrLevelId()) > 0 && uF.parseToInt(getStrSalaryHeadId()) > 0){
					addNewSalaryHeadToEmployee(con, uF, uF.parseToInt(getStrLevelId()), uF.parseToInt(getStrSalaryHeadId()));
				} else if(getStrType() != null && getStrType().trim().equalsIgnoreCase("addStatutoryHead") && uF.parseToInt(getStrLevelId()) > 0 && uF.parseToInt(getStrSalaryHeadId()) > 0){
					addNewStatutorySalaryHeadToEmployee(con, uF, uF.parseToInt(getStrLevelId()), uF.parseToInt(getStrSalaryHeadId()));
				} else if(getStrType() != null && getStrType().trim().equalsIgnoreCase("addSalaryBasis") && uF.parseToInt(getStrLevelId()) > 0 && uF.parseToInt(getStrSalaryHeadId()) > 0){
					addNewBasisSalaryHeadToEmployee(con, uF, uF.parseToInt(getStrLevelId()), uF.parseToInt(getStrSalaryHeadId()));
				}
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
	
	private synchronized void addNewBasisSalaryHeadToEmployeeByGrade(Connection con, UtilityFunctions uF, int nGradeId, int nSalaryHeadId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id " +
					" and eod.grade_id in (select gd.grade_id from grades_details gd, designation_details dd where " +
					"gd.designation_id = dd.designation_id and gd.grade_id=?)");
			pst.setInt(1, nGradeId);
			rs = pst.executeQuery();
			List<String> alSalEmpList = new ArrayList<String>();
			while(rs.next()){
				alSalEmpList.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			for(int j=0; j<alSalEmpList.size(); j++){ 
				String strEmpId = alSalEmpList.get(j);
				pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id,salary_head_id, amount, entry_date, user_id, pay_type, " +
						"isdisplay, service_id, effective_date, earning_deduction, salary_type,is_approved,approved_by,approved_date,grade_id) " +
						"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setInt(2, nSalaryHeadId);
				pst.setDouble(3, getDblAmt());
				pst.setDate	(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(5, uF.parseToInt((String) session.getAttribute(EMPID)));
				pst.setString(6, "M");
				pst.setBoolean(7, uF.parseToBoolean(getAutoUpdate()));
				pst.setInt(8, 0);
				pst.setDate	(9, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(10, "E");
				pst.setString(11, "M");
				pst.setBoolean(12, true);
				pst.setInt(13, uF.parseToInt((String) session.getAttribute(EMPID)));
				pst.setDate	(14, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(15, nGradeId);
				pst.execute();
				pst.close();
				
				CF.updateNextEmpSalaryEffectiveDate(con, uF, uF.parseToInt(strEmpId), uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), DATE_FORMAT);
				/**
				 * Calaculate CTC
				 * */
//				calculateAndUpdateEmpCTC(con, request, session, CF, uF, strEmpId);
				/**
				 * Calaculate CTC End
				 * */
			}
			
		} catch (Exception e) {
			System.err.println("addNewBasisSalaryHeadToEmployeeByGrade===>"+e);
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private synchronized void addNewBasisSalaryHeadToEmployee(Connection con, UtilityFunctions uF, int nLevelId, int nSalaryHeadId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id " +
				" and eod.grade_id in (select gd.grade_id from grades_details gd, designation_details dd where " +
				"gd.designation_id = dd.designation_id and dd.level_id in (select ld.level_id from level_details ld where ld.level_id=?))");
			pst.setInt(1, nLevelId);
			rs = pst.executeQuery();
			List<String> alSalEmpList = new ArrayList<String>();
			while(rs.next()){
				alSalEmpList.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			for(int j=0; j<alSalEmpList.size(); j++) {
				String strEmpId = alSalEmpList.get(j);
				
				pst = con.prepareStatement("select * from emp_salary_details where emp_id=? and salary_head_id=? and level_id=? and " +
					" effective_date = (select max(effective_date) as effective_date from emp_salary_details) ");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setInt(2, nSalaryHeadId);
				pst.setInt(3, nLevelId);
				rs = pst.executeQuery();
				boolean salHeadFlag = false;
				while(rs.next()){
					salHeadFlag = true;
				}
				rs.close();
				pst.close();
				
				if(!salHeadFlag) {
					pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id,salary_head_id, amount, entry_date, user_id, pay_type, " +
						"isdisplay, service_id, effective_date, earning_deduction, salary_type,is_approved,approved_by,approved_date,level_id) " +
						"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setInt(2, nSalaryHeadId);
					pst.setDouble(3, getDblAmt());
					pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(5, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setString(6, "M");
					pst.setBoolean(7, uF.parseToBoolean(getAutoUpdate()));
					pst.setInt(8, 0);
					pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(10, "E");
					pst.setString(11, "M");
					pst.setBoolean(12, true);
					pst.setInt(13, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setDate(14, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(15, nLevelId);
					pst.execute();
					pst.close();
					
					CF.updateNextEmpSalaryEffectiveDate(con, uF, uF.parseToInt(strEmpId), uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), DATE_FORMAT);
				}
				
				/**
				 * Calaculate CTC
				 * */
//				calculateAndUpdateEmpCTC(con, request, session, CF, uF, strEmpId);
				/**
				 * Calaculate CTC End
				 * */
			}
		} catch (Exception e) {
			System.err.println("addNewBasisSalaryHeadToEmployee===>"+e);
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	private synchronized void addNewStatutorySalaryHeadToEmployeeByGrade(Connection con, UtilityFunctions uF, int nGradeId, int nSalaryHeadId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			pst = con.prepareStatement("select esd.* from (select emp_id,max(emp_salary_id) as emp_salary_id from emp_salary_details where " +
					"emp_id in(select emp_id from employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id=eod.emp_id " +
					"and epd.is_alive = true and approved_flag = true and grade_id in (select gd.grade_id from grades_details gd,level_details ld, " +
					"designation_details dd where gd.designation_id = dd.designation_id and gd.grade_id=?)) and is_approved =true and isdisplay=true " +
					"group by emp_id) a,emp_salary_details esd where a.emp_id=esd.emp_id and a.emp_salary_id=esd.emp_salary_id " +
					"and esd.is_approved =true and esd.isdisplay=true order by emp_id");
			pst.setInt(1, nGradeId);
			rs = pst.executeQuery();
			List<Map<String, String>> alSalEmpList = new ArrayList<Map<String,String>>();
			while(rs.next()){
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("EMP_ID", rs.getString("emp_id"));
				hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
				hmInner.put("APPROVED_DATE", uF.getDateFormat(rs.getString("approved_date"), DBDATE, DATE_FORMAT));
				hmInner.put("EFFECTIVE_DATE", uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
				hmInner.put("USER_ID", rs.getString("user_id"));
				hmInner.put("APPROVED_BY", rs.getString("approved_by"));
				
				alSalEmpList.add(hmInner);
			}
			rs.close();
			pst.close();
			
			for(Map<String, String> hmInner : alSalEmpList){
				pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id,salary_head_id, amount, entry_date, user_id, pay_type, " +
						"isdisplay, service_id, effective_date, earning_deduction, salary_type,is_approved,approved_by,approved_date,grade_id) " +
						"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setInt(1, uF.parseToInt(hmInner.get("EMP_ID")));
				pst.setInt(2, nSalaryHeadId);
				pst.setDouble(3, 0.0d);
				pst.setDate	(4, uF.getDateFormat(hmInner.get("ENTRY_DATE"), DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(hmInner.get("USER_ID")));
				pst.setString(6, "M");
				pst.setBoolean(7, uF.parseToBoolean(getAutoUpdate()));
				pst.setInt(8, 0);
				pst.setDate	(9, uF.getDateFormat(hmInner.get("EFFECTIVE_DATE"), DATE_FORMAT));
				pst.setString(10, "D");
				pst.setString(11, "M");
				pst.setBoolean(12, true);
				pst.setInt(13, uF.parseToInt(hmInner.get("APPROVED_BY")));
				pst.setDate	(14, uF.getDateFormat(hmInner.get("APPROVED_DATE"), DATE_FORMAT));
				pst.setInt(15, nGradeId);
				pst.execute();
				pst.close();
				
				CF.updateNextEmpSalaryEffectiveDate(con, uF, uF.parseToInt(hmInner.get("EMP_ID")), hmInner.get("EFFECTIVE_DATE"), DATE_FORMAT);
				/**
				 * Calaculate CTC
				 * */
//				calculateAndUpdateEmpCTC(con, request, session, CF, uF, hmInner.get("EMP_ID"));
				/**
				 * Calaculate CTC End
				 * */
			}
			
		} catch (Exception e) {
			System.err.println("addNewStatutorySalaryHeadToEmployeeByGrade===>"+e);
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private synchronized void addNewStatutorySalaryHeadToEmployee(Connection con, UtilityFunctions uF, int nLevelId, int nSalaryHeadId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			pst = con.prepareStatement("select esd.* from (select emp_id,max(emp_salary_id) as emp_salary_id from emp_salary_details where " +
					"emp_id in(select emp_id from employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id=eod.emp_id " +
					"and epd.is_alive = true and approved_flag = true and grade_id in (select gd.grade_id from grades_details gd,level_details ld, " +
					"designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id=?)) and is_approved =true and isdisplay=true " +
					"group by emp_id) a,emp_salary_details esd where a.emp_id=esd.emp_id and a.emp_salary_id=esd.emp_salary_id and esd.is_approved =true and esd.isdisplay=true order by emp_id");
			pst.setInt(1, nLevelId);
			rs = pst.executeQuery();
			List<Map<String, String>> alSalEmpList = new ArrayList<Map<String,String>>();
			while(rs.next()){
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("EMP_ID", rs.getString("emp_id"));
				hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
				hmInner.put("APPROVED_DATE", uF.getDateFormat(rs.getString("approved_date"), DBDATE, DATE_FORMAT));
				hmInner.put("EFFECTIVE_DATE", uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
				hmInner.put("USER_ID", rs.getString("user_id"));
				hmInner.put("APPROVED_BY", rs.getString("approved_by"));
				
				alSalEmpList.add(hmInner);
			}
			rs.close();
			pst.close();
			
			for(Map<String, String> hmInner : alSalEmpList){
				pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id,salary_head_id, amount, entry_date, user_id, pay_type, " +
						"isdisplay, service_id, effective_date, earning_deduction, salary_type,is_approved,approved_by,approved_date,level_id) " +
						"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setInt(1, uF.parseToInt(hmInner.get("EMP_ID")));
				pst.setInt(2, nSalaryHeadId);
				pst.setDouble(3, 0.0d);
				pst.setDate	(4, uF.getDateFormat(hmInner.get("ENTRY_DATE"), DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(hmInner.get("USER_ID")));
				pst.setString(6, "M");
				pst.setBoolean(7, uF.parseToBoolean(getAutoUpdate())); 
				pst.setInt(8, 0);
				pst.setDate	(9, uF.getDateFormat(hmInner.get("EFFECTIVE_DATE"), DATE_FORMAT));
				pst.setString(10, "D");
				pst.setString(11, "M");
				pst.setBoolean(12, true);
				pst.setInt(13, uF.parseToInt(hmInner.get("APPROVED_BY")));
				pst.setDate	(14, uF.getDateFormat(hmInner.get("APPROVED_DATE"), DATE_FORMAT));
				pst.setInt(15, nLevelId);
				pst.execute();
				pst.close();
				
				CF.updateNextEmpSalaryEffectiveDate(con, uF, uF.parseToInt(hmInner.get("EMP_ID")), hmInner.get("EFFECTIVE_DATE"), DATE_FORMAT);
				/**
				 * Calaculate CTC
				 * */
//				calculateAndUpdateEmpCTC(con, request, session, CF, uF, hmInner.get("EMP_ID"));
				/**
				 * Calaculate CTC End
				 * */
			}
			
		} catch (Exception e) {
			System.err.println("addNewStatutorySalaryHeadToEmployee===>"+e);
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private synchronized void addNewSalaryHeadToEmployeeByGrade(Connection con, UtilityFunctions uF, int nGradeId, int nSalId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE grade_id = ? and salary_head_id=? " +
					"and (is_delete is null or is_delete=false) order by earning_deduction desc, weight");
			pst.setInt(1, nGradeId);
			pst.setInt(2, nSalId);
			rs = pst.executeQuery();
			String strMulPerCalFormula = null;
			String strSalaryHeadAmtType = null;
			String strPercentage = null;
			String salaryType = null;
			String earningDeduction = null;
			while(rs.next()){
				strMulPerCalFormula = rs.getString("multiple_calculation");
				strSalaryHeadAmtType = rs.getString("salary_head_amount_type");
				strPercentage = rs.getString("salary_head_amount");
				salaryType = rs.getString("salary_type");
				earningDeduction = rs.getString("earning_deduction");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select esd.* from (select emp_id,max(emp_salary_id) as emp_salary_id from emp_salary_details where " +
					"emp_id in(select emp_id from employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id=eod.emp_id " +
					"and epd.is_alive = true and approved_flag = true and grade_id=?) and is_approved =true and isdisplay=true " +
					"group by emp_id) a,emp_salary_details esd where a.emp_id=esd.emp_id and a.emp_salary_id=esd.emp_salary_id and esd.is_approved =true and esd.isdisplay=true order by emp_id");
			pst.setInt(1, nGradeId);
			rs = pst.executeQuery();
			List<Map<String, String>> alSalEmpList = new ArrayList<Map<String,String>>();
			while(rs.next()){
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("EMP_ID", rs.getString("emp_id"));
				hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
				hmInner.put("APPROVED_DATE", uF.getDateFormat(rs.getString("approved_date"), DBDATE, DATE_FORMAT));
				hmInner.put("EFFECTIVE_DATE", uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
				hmInner.put("USER_ID", rs.getString("user_id"));
				hmInner.put("APPROVED_BY", rs.getString("approved_by"));
				
				alSalEmpList.add(hmInner);
			}
			rs.close();
			pst.close();
			
			for(Map<String, String> hmInner : alSalEmpList){
				double dblAmt = 0.0d;
				
				if(strSalaryHeadAmtType!=null && strSalaryHeadAmtType.trim().equals("P")){
					List<String> alHead = CF.getMultipleCalHead(uF,strMulPerCalFormula);
					if(alHead == null) alHead = new ArrayList<String>();
					if(alHead.size() > 0) {
						String strCalHeads = StringUtils.join(alHead.toArray(), ",");
						pst = con.prepareStatement("select * from emp_salary_details where emp_id=? and effective_date=? and isdisplay=true " +
								"and salary_head_id in("+strCalHeads+")");
						pst.setInt(1, uF.parseToInt(hmInner.get("EMP_ID")));
						pst.setDate	(2, uF.getDateFormat(hmInner.get("EFFECTIVE_DATE"), DATE_FORMAT));
						rs = pst.executeQuery();
						Map<String,String> hmEmpSalaryHeadAmt = new HashMap<String, String>();
						while(rs.next()){
							hmEmpSalaryHeadAmt.put(rs.getString("salary_head_id"), rs.getString("amount"));
						}
						rs.close();
						pst.close();
						
						String strFormula = CF.getEmpStructureFormula(con, uF, hmEmpSalaryHeadAmt, strMulPerCalFormula);
						if(uF.parseToDouble(strPercentage) > 0.0d && strFormula != null && strFormula.length() > 0){
							double dblPerAmount = uF.eval(strFormula);	
							dblAmt = (uF.parseToDouble(strPercentage) * dblPerAmount)/100;
						}
					}					
				} else if(strSalaryHeadAmtType !=null && strSalaryHeadAmtType.trim().equals("A")){
					dblAmt = uF.parseToDouble(strPercentage);
				}
				
				pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id,salary_head_id, amount, entry_date, user_id, pay_type, " +
						"isdisplay, service_id, effective_date, earning_deduction, salary_type,is_approved,approved_by,approved_date,grade_id) " +
						"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setInt(1, uF.parseToInt(hmInner.get("EMP_ID")));
				pst.setInt(2, nSalId);
				pst.setDouble(3, dblAmt);
				pst.setDate	(4, uF.getDateFormat(hmInner.get("ENTRY_DATE"), DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(hmInner.get("USER_ID")));
				pst.setString(6, "M");
				pst.setBoolean(7, uF.parseToBoolean(getAutoUpdate()));
				pst.setInt(8, 0);
				pst.setDate	(9, uF.getDateFormat(hmInner.get("EFFECTIVE_DATE"), DATE_FORMAT));
				pst.setString(10, earningDeduction);
				pst.setString(11, salaryType);
				pst.setBoolean(12, true);
				pst.setInt(13, uF.parseToInt(hmInner.get("APPROVED_BY")));
				pst.setDate(14, uF.getDateFormat(hmInner.get("APPROVED_DATE"), DATE_FORMAT));
				pst.setInt(15, nGradeId);
				pst.execute();
				pst.close();
				
				CF.updateNextEmpSalaryEffectiveDate(con, uF, uF.parseToInt(hmInner.get("EMP_ID")), hmInner.get("EFFECTIVE_DATE"), DATE_FORMAT);
				/**
				 * Calaculate CTC
				 * */
//				calculateAndUpdateEmpCTC(con, request, session, CF, uF, hmInner.get("EMP_ID"));
				/**
				 * Calaculate CTC End
				 * */
				
			}
			
		} catch (Exception e) {
			System.err.println("addNewSalaryHeadToEmployeeByGrade===>"+e);
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private synchronized void updateSalaryUpdatePercentageByGrade(Connection con, UtilityFunctions uF, int nGradeId, int nSalaryHeadId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			System.out.println("in update grade==>");
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE grade_id = ? and salary_head_id=? " +
					"and (is_delete is null or is_delete=false) order by earning_deduction desc, weight");
			pst.setInt(1, nGradeId);
			pst.setInt(2, nSalaryHeadId);
			rs = pst.executeQuery();
			String strMulPerCalFormula = null;
			String strSalaryHeadAmtType = null;
			String strPercentage = null;
			while(rs.next()){
				strMulPerCalFormula = rs.getString("multiple_calculation");
				strSalaryHeadAmtType = rs.getString("salary_head_amount_type");
				strPercentage = rs.getString("salary_head_amount");
			}
			rs.close();
			pst.close();
			
			List<String> alHead = CF.getMultipleCalHead(uF,strMulPerCalFormula);
			if(alHead == null) alHead = new ArrayList<String>();
			if(alHead.size() > 0) {
				String strCalHeads = StringUtils.join(alHead.toArray(), ",");
				
				pst = con.prepareStatement("select esd.* from (select emp_id,max(emp_salary_id) as emp_salary_id from emp_salary_details where " +
						"emp_id in(select emp_id from employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id=eod.emp_id " +
						"and epd.is_alive = true and approved_flag = true and grade_id =?) and is_approved =true and isdisplay=true " +
						"group by emp_id) a,emp_salary_details esd where a.emp_id=esd.emp_id and a.emp_salary_id=esd.emp_salary_id and esd.is_approved =true and esd.isdisplay=true order by emp_id");
				pst.setInt(1, nGradeId);
				rs = pst.executeQuery();
				List<Map<String, String>> alSalEmpList = new ArrayList<Map<String,String>>();
				while(rs.next()){
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("EMP_ID", rs.getString("emp_id"));
					hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
					hmInner.put("APPROVED_DATE", uF.getDateFormat(rs.getString("approved_date"), DBDATE, DATE_FORMAT));
					hmInner.put("EFFECTIVE_DATE", uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
					hmInner.put("USER_ID", rs.getString("user_id"));
					hmInner.put("APPROVED_BY", rs.getString("approved_by"));
					
					alSalEmpList.add(hmInner);
				}
				rs.close();
				pst.close();
				
				for(Map<String, String> hmInner : alSalEmpList){
					pst = con.prepareStatement("select * from emp_salary_details where emp_id=? and effective_date=? and isdisplay=true " +
							"and salary_head_id in("+strCalHeads+")");
					pst.setInt(1, uF.parseToInt(hmInner.get("EMP_ID")));
					pst.setDate	(2, uF.getDateFormat(hmInner.get("EFFECTIVE_DATE"), DATE_FORMAT));
					rs = pst.executeQuery();
					Map<String,String> hmEmpSalaryHeadAmt = new HashMap<String, String>();
					while(rs.next()){
						hmEmpSalaryHeadAmt.put(rs.getString("salary_head_id"), rs.getString("amount"));
					}
					rs.close();
					pst.close();
					
					double dblAmount = 0.0d;
					String strFormula = CF.getEmpStructureFormula(con, uF, hmEmpSalaryHeadAmt, strMulPerCalFormula);
					if(uF.parseToDouble(strPercentage) > 0.0d && strFormula != null && strFormula.length() > 0){
						double dblPerAmount = uF.eval(strFormula);	
						dblAmount = (uF.parseToDouble(strPercentage) * dblPerAmount)/100;
					}
					
					pst = con.prepareStatement("update emp_salary_details set amount=? WHERE emp_id=? and effective_date=? and salary_head_id=?");
					pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAmount)));
					pst.setInt(2, uF.parseToInt(hmInner.get("EMP_ID")));
					pst.setDate(3, uF.getDateFormat(hmInner.get("EFFECTIVE_DATE"), DATE_FORMAT));
					pst.setInt(4, nSalaryHeadId);
//					System.out.println("update main pst==>"+pst);
					int x = pst.executeUpdate();
					if(x > 0){
						updateRelativeEmpSalaryHeadAmountByGrade(con, uF, nGradeId, nSalaryHeadId, hmInner.get("EMP_ID"), hmInner.get("EFFECTIVE_DATE"));
						/**
						 * Calaculate CTC
						 * */
//						calculateAndUpdateEmpCTC(con, request, session, CF, uF, hmInner.get("EMP_ID"));
						/**
						 * Calaculate CTC End
						 * */
					}
				}
			}
			
		} catch (Exception e) {
			System.err.println("updateSalaryUpdatePercentageByGrade===>"+e);
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	private synchronized void updateRelativeEmpSalaryHeadAmountByGrade(Connection con, UtilityFunctions uF, int nGradeId, int nSalaryHeadId, String strEmpId, String strEffectiveDate) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			if(nSalaryHeadId > 0){				
				pst = con.prepareStatement("SELECT * FROM salary_details WHERE grade_id = ? and multiple_calculation like '%,"+nSalaryHeadId+",%' " +
						"and (is_delete is null or is_delete=false) order by earning_deduction desc, weight");
				pst.setInt(1, nGradeId);
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				List<Map<String, String>> alSal = new ArrayList<Map<String,String>>();
				while(rs.next()){
					Map<String, String> hmSal = new HashMap<String, String>();
					
					hmSal.put("MULTIPLE_CALCULATION", rs.getString("multiple_calculation"));
					hmSal.put("SALARY_HEAD_AMOUNT_TYPE", rs.getString("salary_head_amount_type"));
					hmSal.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
					hmSal.put("SALARY_ID", rs.getString("salary_id"));
					hmSal.put("SALARY_HEAD_AMOUNT", rs.getString("salary_head_amount"));
					
					alSal.add(hmSal);
				}
				rs.close();
				pst.close();
				
				for(Map<String, String> hmSal : alSal) {
					String strMulPerCalFormula = hmSal.get("MULTIPLE_CALCULATION");
					String strSalaryHeadAmtType = hmSal.get("SALARY_HEAD_AMOUNT_TYPE");
					int nCalSalHeadId = uF.parseToInt(hmSal.get("SALARY_HEAD_ID"));
					int nCalSalId = uF.parseToInt(hmSal.get("SALARY_ID"));
					String strPercentage = hmSal.get("SALARY_HEAD_AMOUNT");
					
					if(nCalSalId > 0 && nCalSalHeadId > 0 && strSalaryHeadAmtType!=null && strSalaryHeadAmtType.trim().equalsIgnoreCase("P")){
						
						List<String> alHead = CF.getMultipleCalHead(uF,strMulPerCalFormula);
						if(alHead == null) alHead = new ArrayList<String>();
						if(alHead.size() > 0) {
							String strCalHeads = StringUtils.join(alHead.toArray(), ",");
						
							pst = con.prepareStatement("select * from emp_salary_details where emp_id=? and effective_date=? and isdisplay=true " +
									"and salary_head_id in("+strCalHeads+")");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getDateFormat(strEffectiveDate, DATE_FORMAT));
							rs = pst.executeQuery();
							Map<String,String> hmEmpSalaryHeadAmt = new HashMap<String, String>();
							while(rs.next()){
								hmEmpSalaryHeadAmt.put(rs.getString("salary_head_id"), rs.getString("amount"));
							}
							rs.close();
							pst.close();
							
							double dblAmount = 0.0d;
							String strFormula = CF.getEmpStructureFormula(con, uF, hmEmpSalaryHeadAmt, strMulPerCalFormula);
							if(uF.parseToDouble(strPercentage) > 0.0d && strFormula != null && strFormula.length() > 0){
								double dblPerAmount = uF.eval(strFormula);	
								dblAmount = (uF.parseToDouble(strPercentage) * dblPerAmount)/100;
							}
							
							pst = con.prepareStatement("update emp_salary_details set amount=? WHERE emp_id=? and effective_date=? and salary_head_id=?");
							pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAmount)));
							pst.setInt(2, uF.parseToInt(strEmpId));
							pst.setDate(3, uF.getDateFormat(strEffectiveDate, DATE_FORMAT));
							pst.setInt(4, nCalSalHeadId);
//							System.out.println("pst==>"+pst);
							int x = pst.executeUpdate();
							if(x > 0){
								updateRelativeEmpSalaryHeadAmountByGrade(con, uF, nGradeId, nCalSalHeadId, strEmpId, strEffectiveDate);
							}
						}
					}
				}
			}
		} catch (Exception e){
			System.err.println("updateRelativeEmpSalaryHeadAmountByGrade===>"+e);
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
	}
	
	private synchronized void addNewSalaryHeadToEmployee(Connection con, UtilityFunctions uF, int nLevelId, int nSalId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id = ? and salary_head_id=? " +
					"and (is_delete is null or is_delete=false) order by earning_deduction desc, weight");
			pst.setInt(1, nLevelId);
			pst.setInt(2, nSalId);
			rs = pst.executeQuery();
			String strMulPerCalFormula = null;
			String strSalaryHeadAmtType = null;
			String strPercentage = null;
			String salaryType = null;
			String earningDeduction = null;
			while(rs.next()){
				strMulPerCalFormula = rs.getString("multiple_calculation");
				strSalaryHeadAmtType = rs.getString("salary_head_amount_type");
				strPercentage = rs.getString("salary_head_amount");
				salaryType = rs.getString("salary_type");
				earningDeduction = rs.getString("earning_deduction");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select esd.* from (select emp_id,max(emp_salary_id) as emp_salary_id from emp_salary_details where " +
					"emp_id in(select emp_id from employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id=eod.emp_id " +
					"and epd.is_alive = true and approved_flag = true and grade_id in (select gd.grade_id from grades_details gd,level_details ld, " +
					"designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id=?)) and is_approved =true and isdisplay=true " +
					"group by emp_id) a,emp_salary_details esd where a.emp_id=esd.emp_id and a.emp_salary_id=esd.emp_salary_id and esd.is_approved =true and esd.isdisplay=true order by emp_id");
			pst.setInt(1, nLevelId);
			rs = pst.executeQuery();
			List<Map<String, String>> alSalEmpList = new ArrayList<Map<String,String>>();
			while(rs.next()){
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("EMP_ID", rs.getString("emp_id"));
				hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
				hmInner.put("APPROVED_DATE", uF.getDateFormat(rs.getString("approved_date"), DBDATE, DATE_FORMAT));
				hmInner.put("EFFECTIVE_DATE", uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
				hmInner.put("USER_ID", rs.getString("user_id"));
				hmInner.put("APPROVED_BY", rs.getString("approved_by"));
				
				alSalEmpList.add(hmInner);
			}
			rs.close();
			pst.close();
			
			for(Map<String, String> hmInner : alSalEmpList){
				double dblAmt = 0.0d;
				if(strSalaryHeadAmtType!=null && strSalaryHeadAmtType.trim().equals("P")){
					List<String> alHead = CF.getMultipleCalHead(uF,strMulPerCalFormula);
					if(alHead == null) alHead = new ArrayList<String>();
					if(alHead.size() > 0) {
						String strCalHeads = StringUtils.join(alHead.toArray(), ",");
						pst = con.prepareStatement("select * from emp_salary_details where emp_id=? and effective_date=? and isdisplay=true " +
								"and salary_head_id in("+strCalHeads+")");
						pst.setInt(1, uF.parseToInt(hmInner.get("EMP_ID")));
						pst.setDate	(2, uF.getDateFormat(hmInner.get("EFFECTIVE_DATE"), DATE_FORMAT));
						rs = pst.executeQuery();
						Map<String,String> hmEmpSalaryHeadAmt = new HashMap<String, String>();
						while(rs.next()){
							hmEmpSalaryHeadAmt.put(rs.getString("salary_head_id"), rs.getString("amount"));
						}
						rs.close();
						pst.close();
						
						String strFormula = CF.getEmpStructureFormula(con, uF, hmEmpSalaryHeadAmt, strMulPerCalFormula);
						if(uF.parseToDouble(strPercentage) > 0.0d && strFormula != null && strFormula.length() > 0){
							double dblPerAmount = uF.eval(strFormula);	
							dblAmt = (uF.parseToDouble(strPercentage) * dblPerAmount)/100;
						}
					}					
				} else if(strSalaryHeadAmtType !=null && strSalaryHeadAmtType.trim().equals("A")){
					dblAmt = uF.parseToDouble(strPercentage);
				}
				
				pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id,salary_head_id, amount, entry_date, user_id, pay_type, " +
						"isdisplay, service_id, effective_date, earning_deduction, salary_type,is_approved,approved_by,approved_date,level_id) " +
						"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setInt(1, uF.parseToInt(hmInner.get("EMP_ID")));
				pst.setInt(2, nSalId);
				pst.setDouble(3, dblAmt);
				pst.setDate	(4, uF.getDateFormat(hmInner.get("ENTRY_DATE"), DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(hmInner.get("USER_ID")));
				pst.setString(6, "M");
				pst.setBoolean(7, uF.parseToBoolean(getAutoUpdate()));
				pst.setInt(8, 0);
				pst.setDate	(9, uF.getDateFormat(hmInner.get("EFFECTIVE_DATE"), DATE_FORMAT));
				pst.setString(10, earningDeduction);
				pst.setString(11, salaryType);
				pst.setBoolean(12, true);
				pst.setInt(13, uF.parseToInt(hmInner.get("APPROVED_BY")));
				pst.setDate	(14, uF.getDateFormat(hmInner.get("APPROVED_DATE"), DATE_FORMAT));
				pst.setInt(15, nLevelId);
				pst.execute();
				pst.close();
				
				CF.updateNextEmpSalaryEffectiveDate(con, uF, uF.parseToInt(hmInner.get("EMP_ID")), hmInner.get("EFFECTIVE_DATE"), DATE_FORMAT);
				/**
				 * Calaculate CTC
				 * */
//				calculateAndUpdateEmpCTC(con, request, session, CF, uF, hmInner.get("EMP_ID"));
				/**
				 * Calaculate CTC End
				 * */ 
			}
			
		} catch (Exception e) {
			System.err.println("addNewSalaryHeadToEmployee===>"+e);
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private synchronized void updateSalaryUpdatePercentage(Connection con, UtilityFunctions uF, int nLevelId, int nSalaryHeadId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
//			System.out.println("in update==>");
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id = ? and salary_head_id=? " +
					"and (is_delete is null or is_delete=false) order by earning_deduction desc, weight");
			pst.setInt(1, nLevelId);
			pst.setInt(2, nSalaryHeadId);
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			String strMulPerCalFormula = null;
			String strSalaryHeadAmtType = null;
			String strPercentage = null;
			while(rs.next()){
				strMulPerCalFormula = rs.getString("multiple_calculation");
				strSalaryHeadAmtType = rs.getString("salary_head_amount_type");
				strPercentage = rs.getString("salary_head_amount");
			}
			rs.close();
			pst.close();
			
			List<String> alHead = CF.getMultipleCalHead(uF,strMulPerCalFormula);
			if(alHead == null) alHead = new ArrayList<String>();
			if(alHead.size() > 0) {
				String strCalHeads = StringUtils.join(alHead.toArray(), ",");
				
				pst = con.prepareStatement("select esd.* from (select emp_id,max(emp_salary_id) as emp_salary_id from emp_salary_details where " +
						"emp_id in(select emp_id from employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id=eod.emp_id " +
						"and epd.is_alive = true and approved_flag = true and grade_id in (select gd.grade_id from grades_details gd,level_details ld, " +
						"designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id=?)) and is_approved =true and isdisplay=true " +
						"group by emp_id) a,emp_salary_details esd where a.emp_id=esd.emp_id and a.emp_salary_id=esd.emp_salary_id and esd.is_approved =true and esd.isdisplay=true order by emp_id");
				pst.setInt(1, nLevelId);
				rs = pst.executeQuery();
				List<Map<String, String>> alSalEmpList = new ArrayList<Map<String,String>>();
				while(rs.next()){
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("EMP_ID", rs.getString("emp_id"));
					hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
					hmInner.put("APPROVED_DATE", uF.getDateFormat(rs.getString("approved_date"), DBDATE, DATE_FORMAT));
					hmInner.put("EFFECTIVE_DATE", uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
					hmInner.put("USER_ID", rs.getString("user_id"));
					hmInner.put("APPROVED_BY", rs.getString("approved_by"));
					
					alSalEmpList.add(hmInner);
				}
				rs.close();
				pst.close();
				
				for(Map<String, String> hmInner : alSalEmpList){
					pst = con.prepareStatement("select * from emp_salary_details where emp_id=? and effective_date=? and isdisplay=true " +
							"and salary_head_id in("+strCalHeads+")");
					pst.setInt(1, uF.parseToInt(hmInner.get("EMP_ID")));
					pst.setDate	(2, uF.getDateFormat(hmInner.get("EFFECTIVE_DATE"), DATE_FORMAT));
					rs = pst.executeQuery();
					Map<String,String> hmEmpSalaryHeadAmt = new HashMap<String, String>();
					while(rs.next()){
						hmEmpSalaryHeadAmt.put(rs.getString("salary_head_id"), rs.getString("amount"));
					}
					rs.close();
					pst.close();
					
					double dblAmount = 0.0d;
					String strFormula = CF.getEmpStructureFormula(con, uF, hmEmpSalaryHeadAmt, strMulPerCalFormula);
//					System.out.println("strFormula==>"+strFormula+"--strMulPerCalFormula==>"+strMulPerCalFormula);
					if(uF.parseToDouble(strPercentage) > 0.0d && strFormula != null && strFormula.length() > 0){
						double dblPerAmount = uF.eval(strFormula);	
						dblAmount = (uF.parseToDouble(strPercentage) * dblPerAmount)/100;
					}
					
					pst = con.prepareStatement("update emp_salary_details set amount=? WHERE emp_id=? and effective_date=? and salary_head_id=?");
					pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAmount)));
					pst.setInt(2, uF.parseToInt(hmInner.get("EMP_ID")));
					pst.setDate(3, uF.getDateFormat(hmInner.get("EFFECTIVE_DATE"), DATE_FORMAT));
					pst.setInt(4, nSalaryHeadId);
//					System.out.println("update main pst==>"+pst);
					int x = pst.executeUpdate();
					if(x > 0){
						updateRelativeEmpSalaryHeadAmount(con, uF, nLevelId, nSalaryHeadId, hmInner.get("EMP_ID"), hmInner.get("EFFECTIVE_DATE"));
						
						/**
						 * Calaculate CTC
						 * */
//						calculateAndUpdateEmpCTC(con, request, session, CF, uF, hmInner.get("EMP_ID"));
						/**
						 * Calaculate CTC End
						 * */
					}
				}
			}
			
		} catch (Exception e) {
			System.err.println("updateSalaryUpdatePercentage===>"+e);
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	private synchronized void updateRelativeEmpSalaryHeadAmount(Connection con, UtilityFunctions uF, int nLevelId, int nSalaryHeadId, String strEmpId, String strEffectiveDate) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			if(nSalaryHeadId > 0){				
				pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id = ? and multiple_calculation like '%,"+nSalaryHeadId+",%' " +
						"and (is_delete is null or is_delete=false) order by earning_deduction desc, weight");
				pst.setInt(1, nLevelId);
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				List<Map<String, String>> alSal = new ArrayList<Map<String,String>>();
				while(rs.next()){
					Map<String, String> hmSal = new HashMap<String, String>();
					
					hmSal.put("MULTIPLE_CALCULATION", rs.getString("multiple_calculation"));
					hmSal.put("SALARY_HEAD_AMOUNT_TYPE", rs.getString("salary_head_amount_type"));
					hmSal.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
					hmSal.put("SALARY_ID", rs.getString("salary_id"));
					hmSal.put("SALARY_HEAD_AMOUNT", rs.getString("salary_head_amount"));
					
					alSal.add(hmSal);
				}
				rs.close();
				pst.close();
				
				for(Map<String, String> hmSal : alSal) {
					String strMulPerCalFormula = hmSal.get("MULTIPLE_CALCULATION");
					String strSalaryHeadAmtType = hmSal.get("SALARY_HEAD_AMOUNT_TYPE");
					int nCalSalHeadId = uF.parseToInt(hmSal.get("SALARY_HEAD_ID"));
					int nCalSalId = uF.parseToInt(hmSal.get("SALARY_ID"));
					String strPercentage = hmSal.get("SALARY_HEAD_AMOUNT");
					
					if(nCalSalId > 0 && nCalSalHeadId > 0 && strSalaryHeadAmtType!=null && strSalaryHeadAmtType.trim().equalsIgnoreCase("P")){
						
						List<String> alHead = CF.getMultipleCalHead(uF,strMulPerCalFormula);
						if(alHead == null) alHead = new ArrayList<String>();
						if(alHead.size() > 0) {
							String strCalHeads = StringUtils.join(alHead.toArray(), ",");
						
							pst = con.prepareStatement("select * from emp_salary_details where emp_id=? and effective_date=? and isdisplay=true " +
									"and salary_head_id in("+strCalHeads+")");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getDateFormat(strEffectiveDate, DATE_FORMAT));
							rs = pst.executeQuery();
							Map<String,String> hmEmpSalaryHeadAmt = new HashMap<String, String>();
							while(rs.next()){
								hmEmpSalaryHeadAmt.put(rs.getString("salary_head_id"), rs.getString("amount"));
							}
							rs.close();
							pst.close();
							
							double dblAmount = 0.0d;
							String strFormula = CF.getEmpStructureFormula(con, uF, hmEmpSalaryHeadAmt, strMulPerCalFormula);
							if(uF.parseToDouble(strPercentage) > 0.0d && strFormula != null && strFormula.length() > 0){
								double dblPerAmount = uF.eval(strFormula);	
								dblAmount = (uF.parseToDouble(strPercentage) * dblPerAmount)/100;
							}
							
							pst = con.prepareStatement("update emp_salary_details set amount=? WHERE emp_id=? and effective_date=? and salary_head_id=?");
							pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAmount)));
							pst.setInt(2, uF.parseToInt(strEmpId));
							pst.setDate(3, uF.getDateFormat(strEffectiveDate, DATE_FORMAT));
							pst.setInt(4, nCalSalHeadId);
//							System.out.println("pst==>"+pst);
							int x = pst.executeUpdate();
							if(x > 0){
								updateRelativeEmpSalaryHeadAmount(con, uF, nLevelId, nCalSalHeadId, strEmpId, strEffectiveDate);
							}
						}
					}
				}
			}
		} catch (Exception e){
			System.err.println("updateRelativeEmpSalaryHeadAmount===>"+e);
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
	}
	
	public synchronized void calculateAndUpdateEmpCTC(Connection con, HttpServletRequest request, HttpSession session, CommonFunctions CF, UtilityFunctions uF, String strEmpId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, null, strEmpId);
			
			MyProfile myProfile = new MyProfile();
			myProfile.session = session;
			myProfile.request = request;
			myProfile.CF = CF;
			int intEmpIdReq = uF.parseToInt(strEmpId);
			myProfile.getSalaryHeadsforEmployee(con, uF, intEmpIdReq, hmEmpProfile);
			
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
            
			EmpSalaryApproval salaryApproval = new EmpSalaryApproval();
			salaryApproval.request = request;
			salaryApproval.session = session;
			salaryApproval.CF = CF;
			Map<String, String> hmPrevCTC = salaryApproval.getPrevCTCDetails(con, uF, strEmpId);
			
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
		} catch (Exception e){
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
	}

	public String getStrLevelId() {
		return strLevelId;
	}

	public void setStrLevelId(String strLevelId) {
		this.strLevelId = strLevelId;
	}

	public String getStrGradeId() {
		return strGradeId;
	}

	public void setStrGradeId(String strGradeId) {
		this.strGradeId = strGradeId;
	}

	public String getStrSalaryHeadId() {
		return strSalaryHeadId;
	}

	public void setStrSalaryHeadId(String strSalaryHeadId) {
		this.strSalaryHeadId = strSalaryHeadId;
	}

	public String getStrType() {
		return strType;
	}

	public void setStrType(String strType) {
		this.strType = strType;
	}

	public double getDblAmt() {
		return dblAmt;
	}

	public void setDblAmt(double dblAmt) {
		this.dblAmt = dblAmt;
	}

	public String getAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(String autoUpdate) {
		this.autoUpdate = autoUpdate;
	}
	
}