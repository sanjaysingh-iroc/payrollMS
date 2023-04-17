package com.konnect.jpms.payroll;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.export.payroll.ExcelSheetDesign;
import com.konnect.jpms.select.FillBank;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycleDuration;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillPayMode;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IDBConstant;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.LogDetails;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PayPayroll extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements,IDBConstant {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strEmpId = null;
	String strUserType = null;

	public CommonFunctions CF = null;
	String profileEmpId;

	String strApprove;
	String financialYear;
	String paycycle;
	String approvePC;
	String strMonth;
	String[] chbxApprove;
	List<FillMonth> alMonthList;

	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	String strGrade;
	String strEmployeType;
	
	String[] f_strWLocation; 
	String[] f_level;
	String[] f_department;
	String[] f_service;
	String[] f_grade;
	String[] f_employeType;

	List<FillPayCycles> paycycleList;
	List<FillFinancialYears> financialYearList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillWLocation> wLocationList;

	List<FillPayMode> paymentModeList;
	List<FillBank> bankList;
	List<FillOrganisation> organisationList;
	List<FillGrade> gradeList;
	List<FillEmploymentType> employementTypeList;

	String f_org;
	String f_paymentMode;
	String bankAccount;
	List<FillPayCycleDuration> paycycleDurationList;
	String strPaycycleDuration;

	String empId;
	String operation;
	String bankAccountType;

	String alertID;

	String pageFrom;

	List<FillSalaryHeads> salaryHeadList;
	boolean bifurcatePayOut;
	String primarySalaryHead;
	String secondarySalaryHead;
	private String download = null;
	
	
	String strBankBranch;
	String[] bankBranch;
	List<FillBank> bankBranchList;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.opensymphony.xwork2.ActionSupport#execute()
	 */
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, PPayPayroll);
		request.setAttribute(TITLE, TPayPayroll);

		strEmpId = (String) session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(USERTYPE);
		download = request.getParameter("download");
		
		
		
//		boolean isView = CF.getAccess(session, request, uF);
//		if (!isView) {
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}

		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));

		if (getOperation() != null && getOperation().equals("D") && getPaycycle() != null) {
			unApprovedSalary(uF);
			return "delete";
		}

		if (uF.parseToInt(getF_org()) <= 0) {
			setF_org((String) session.getAttribute(ORGID));
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
		
		if(getStrEmployeType() !=null && !getStrEmployeType().equals("")){
			setF_employeType(getStrEmployeType().split(","));
//			System.out.println("====>"+getF_employeType().length);
		}else{
			setF_employeType(null);
		}
		
		if(getStrGrade() !=null && !getStrGrade().equals("")){
			setF_grade(getStrGrade().split(","));
		}else{
			setF_grade(null);
		}
		
		if(getF_level()!=null) {
			String level_id ="";
			for (int i = 0; i < getF_level().length; i++) {
				if(i==0) {
					level_id = getF_level()[i];
					level_id.concat(getF_level()[i]);
				} else {
					level_id =level_id+","+getF_level()[i];
				}
			}
			gradeList = new FillGrade(request).fillGrade(level_id,getF_org());
		} else {
			gradeList = new FillGrade(request).fillGradebyorganisation(getF_org());
		}
		
		if(getStrBankBranch() !=null && !getStrBankBranch().trim().equals("") && !getStrBankBranch().trim().equalsIgnoreCase("NULL")){
			setBankBranch(getStrBankBranch().split(","));
		} else {
			setBankBranch(null);
		}
		
		if (getStrPaycycleDuration() == null || getStrPaycycleDuration().equals("") || getStrPaycycleDuration().equalsIgnoreCase("NULL")) {
			setStrPaycycleDuration("M");
		}
		
		getEmployeeBankBranchList(uF);
		
		paycycleList = new FillPayCycles(getStrPaycycleDuration(), request).fillPayCycles(CF, getF_org());
		for(int i=0;i<paycycleList.size();i++)
		{
//			System.out.println("id"+paycycleList.get(i).getPaycycleId()+"name: "+paycycleList.get(i).getPaycycleName());
		}
		
		String[] strPayCycleDates = null;
		if (getApprovePC() != null && !getApprovePC().equalsIgnoreCase("NULL") && getApprovePC().length() > 0 && getStrApprove() != null) {
			strPayCycleDates = getApprovePC().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		} else if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
			strPayCycleDates = getPaycycle().split("-");
		} else {
			// strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
			strPayCycleDates = CF.getCurrentPayCycleUsingDurationByOrg(CF.getStrTimeZone(), CF, getF_org(), request, getStrPaycycleDuration());
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}

		String strD1 = strPayCycleDates[0];
		String strD2 = strPayCycleDates[1];
		String strPC = strPayCycleDates[2];

		request.setAttribute("salaryStructure", CF.getStrSalaryStructure());
		int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
		if (nSalaryStrucuterType == S_GRADE_WISE) {
			if (getStrApprove() != null && getStrApprove().equalsIgnoreCase("PAY")) {
				payApporvedPayrollByGrade(strD1, strD2, strPC);
			}
			viewApporvedPayrollByGrade(uF, strD1, strD2, strPC);
		} else {
			if (getStrApprove() != null && getStrApprove().equalsIgnoreCase("PAY")) {
				payApporvedPayroll(strD1, strD2, strPC);
			}
			viewApporvedPayroll(uF, strD1, strD2, strPC);
		}
		
		String strSelectedBankCode = (String) request.getAttribute("BANK_CODE");
		Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
		
		if(download!=null  && download.equals("true") && strSelectedBankCode != null && hmFeatureUserTypeId.get(F_PNB_BANK_CODE+"_USER_IDS") != null && hmFeatureUserTypeId.get(F_PNB_BANK_CODE+"_USER_IDS").contains(strSelectedBankCode)) {
			generateNeftPunjabBank(uF, strD1, strD2, strPC);
		} else if(download!=null  && download.equals("true") && strSelectedBankCode != null && hmFeatureUserTypeId.get(F_CANARA_BANK_CODE+"_USER_IDS") != null && hmFeatureUserTypeId.get(F_CANARA_BANK_CODE+"_USER_IDS").contains(strSelectedBankCode)) {
			generateNeftCanaraBank(uF, strD1, strD2, strPC);
		} else if(download!=null  && download.equals("true")) {
			return null;
		}
		alMonthList = new FillMonth().fillMonth();
		
		return loadPaySlips();
	}
	
	
	private void getEmployeeBankBranchList(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			bankBranchList = new ArrayList<FillBank>();
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			Map<String, String> hmBankDetails = CF.getBankAccountDetailsMap(con, uF, getBankAccount());
			request.setAttribute("BANK_CODE", hmBankDetails.get("BANK_CODE"));
			
			Map<String, String> hmActivityNode = CF.getActivityNode(con);
			if(hmActivityNode == null) hmActivityNode = new HashMap<String, String>();
			
			int nTriggerNode = uF.parseToInt(hmActivityNode.get(""+ACTIVITY_BANK_ORDER_ID));
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select bd.bank_account_no, bd.branch_id, bd1.bank_name,bd.bank_branch,bd.branch_code from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id ");
			sbQuery.append("and (bd.branch_id in (select CAST (epd.emp_bank_name AS integer) as emp_bank_name from employee_personal_details epd, " +
				"employee_official_details eod where epd.emp_per_id = eod.emp_id and (epd.emp_bank_name is not null and epd.emp_bank_name !='')");
			if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0){
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
		
			if (getF_department() != null && getF_department().length > 0) {
				sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
			}

			if (getF_service() != null && getF_service().length > 0) {
				sbQuery.append(" and (");
				for (int i = 0; i < getF_service().length; i++) {
					sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
					if (i < getF_service().length - 1) {
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" ) ");

			}
			if (getStrPaycycleDuration() != null) {
				sbQuery.append(" and eod.paycycle_duration ='" + getStrPaycycleDuration() + "'");
			}

			if (uF.parseToInt(getF_paymentMode()) > 0) {
				sbQuery.append(" and eod.payment_mode =" + uF.parseToInt(getF_paymentMode()));
			}

			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
				sbQuery.append(" and eod.wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(WLOCATION_ACCESS) != null) {
				sbQuery.append(" and eod.wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
			}

			if (uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(ORG_ACCESS) != null) {
				sbQuery.append(" and eod.org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
			}
			sbQuery.append(") or bd.branch_id in (select CAST (epd.emp_bank_name2 AS integer) as emp_bank_name2 from employee_personal_details epd, " +
				"employee_official_details eod where epd.emp_per_id = eod.emp_id and (epd.emp_bank_name2 is not null and epd.emp_bank_name2 !='')");
			if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0){
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
		
			if (getF_department() != null && getF_department().length > 0) {
				sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
			}

			if (getF_service() != null && getF_service().length > 0) {
				sbQuery.append(" and (");
				for (int i = 0; i < getF_service().length; i++) {
					sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
					if (i < getF_service().length - 1) {
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" ) ");

			}
			if (getStrPaycycleDuration() != null) {
				sbQuery.append(" and eod.paycycle_duration ='" + getStrPaycycleDuration() + "'");
			}

			if (uF.parseToInt(getF_paymentMode()) > 0) {
				sbQuery.append(" and eod.payment_mode =" + uF.parseToInt(getF_paymentMode()));
			}

			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
				sbQuery.append(" and eod.wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(WLOCATION_ACCESS) != null) {
				sbQuery.append(" and eod.wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
			}

			if (uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(ORG_ACCESS) != null) {
				sbQuery.append(" and eod.org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
			}
			sbQuery.append("))");			
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("bank branch pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmBranch = new HashMap<String, String>();
			Map<String, String> hmBranchDetails = new HashMap<String, String>();
			while (rs.next()) {
				if(rs.getString("bank_account_no")!=null && rs.getString("bank_account_no").length()>0){
					hmBranch.put(rs.getString("branch_id"), rs.getString("branch_code"));
					hmBranchDetails.put(rs.getString("branch_id"), rs.getString("bank_account_no")+", "+rs.getString("bank_name")+","+rs.getString("bank_branch"));
				}
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmBranch.keySet().iterator();
			while(it.hasNext()){
				String strBranchId = it.next(); 
//				String strBankCode = hmBranch.get(strBranchId);
				
				String strBranchDetails = hmBranchDetails.get(strBranchId);
				bankBranchList.add(new FillBank(strBranchId, null, strBranchDetails));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}


	}

	public void generateNeftPunjabBank(UtilityFunctions uF, String  strD1, String strD2, String strPC){
//		System.out.println("strPC====>"+strPC);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from payroll_generation pg, employee_official_details eod where pg.emp_id = eod.emp_id and paycycle= ? and paid_from =? and paid_to=? ");
			if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0) {
	        	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
	        } else {
	        	 if(getF_level()!=null && getF_level().length>0) {
	                 sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	             }
	        	 if(getF_grade()!=null && getF_grade().length>0) {
	                 sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
	             }
			}
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "','") + "') ");
			}
			if (getF_department() != null && getF_department().length > 0) {
				sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
			}
			if (getF_service() != null && getF_service().length > 0) {
				sbQuery.append(" and (");
				for (int i = 0; i < getF_service().length; i++) {
					sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
					if (i < getF_service().length - 1) {
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" ) ");
			}
			if (getStrPaycycleDuration() != null) {
				sbQuery.append(" and pay_mode ='" + getStrPaycycleDuration() + "'");
			}
			if (uF.parseToInt(getF_paymentMode()) > 0) {
				sbQuery.append(" and pg.payment_mode =" + uF.parseToInt(getF_paymentMode()));
			}
			if (getBankBranch() != null && getBankBranch().length > 0) {
				sbQuery.append(" and eod.emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					"and ((epd.emp_bank_name is not null and epd.emp_bank_name !='' and CAST(epd.emp_bank_name AS integer) in ("+StringUtils.join(getBankBranch(), ",")+")) " +
					"or (epd.emp_bank_name2 is not null and epd.emp_bank_name2 !='' and CAST (epd.emp_bank_name2 AS integer) in ("+StringUtils.join(getBankBranch(), ",")+"))))");
//				sbQuery.append(" and eod.emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
//					"and (CAST(epd.emp_bank_name AS integer) in ("+StringUtils.join(getBankBranch(), ",")+") or CAST (epd.emp_bank_name2 AS integer) in ("+StringUtils.join(getBankBranch(), ",")+")))");
			}

			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
				sbQuery.append(" and eod.wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(WLOCATION_ACCESS) != null) {
				sbQuery.append(" and eod.wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
			}

			if (uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(ORG_ACCESS) != null) {
				sbQuery.append(" and eod.org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
			}
			sbQuery.append(" order by pg.emp_id, earning_deduction desc, salary_head_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strPC));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pst1====>"+pst);
			rs = pst.executeQuery();
			double dblNetAmount = 0.0d;
			Map hmInner = new HashMap();
			Map hmSalary = new HashMap();
			List alEarnings = new ArrayList();
			List alDeductions = new ArrayList();
			List<String> alSalaryHead = new ArrayList<String>();
	 		Map hmPayPayrollNEFT = new HashMap();
			Map hmEmpPayroll = null;
			Map hmIsApprovedSalary = new HashMap();
			Map<String, String> hmPaymentModeMap = CF.getPaymentMode();
			String strEmpIdOld = null;
			String strEmpIdNew = null;
			double dblGross = 0;
			double dblNet = 0;
			while (rs.next()) {
				strEmpIdNew = rs.getString("emp_id");

				if (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					hmEmpPayroll = new HashMap();
					dblNet = 0;
				}

				if ("E".equalsIgnoreCase(rs.getString("earning_deduction")) && !alEarnings.contains(rs.getString("salary_head_id"))) {
					alEarnings.add(rs.getString("salary_head_id"));
					if (!alSalaryHead.contains(rs.getString("salary_head_id"))) {
						alSalaryHead.add(rs.getString("salary_head_id"));
					}
				} else if ("D".equalsIgnoreCase(rs.getString("earning_deduction")) && !alDeductions.contains(rs.getString("salary_head_id"))) {
					alDeductions.add(rs.getString("salary_head_id"));
					if (!alSalaryHead.contains(rs.getString("salary_head_id"))) {
						alSalaryHead.add(rs.getString("salary_head_id"));
					}
				}
				
				hmEmpPayroll.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("amount"))));

				if ("E".equalsIgnoreCase(rs.getString("earning_deduction"))) {
					double dblAmount = rs.getDouble("amount");
					dblGross = uF.parseToDouble((String) hmEmpPayroll.get("GROSS"));
					dblNet += dblAmount;
					hmEmpPayroll.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), Math.round(dblGross + dblAmount)));

				} else {
					double dblAmount = rs.getDouble("amount");
					dblNet -= dblAmount;
				}
				hmEmpPayroll.put("NET", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), Math.round(dblNet)));
				hmEmpPayroll.put("PAYMENT_MODE", hmPaymentModeMap.get(uF.parseToInt(rs.getString("payment_mode")) + ""));
				hmPayPayrollNEFT.put(strEmpIdNew, hmEmpPayroll);

				/*if (rs.getBoolean("is_paid")) {
					hmIsApprovedSalary.put(strEmpIdNew, rs.getString("is_paid"));
				}*/

				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
			pst = con.prepareStatement("select distinct pg.emp_id as emp_id,epd.emp_bank_acct_nbr from payroll_generation pg,employee_personal_details epd " +
					"where pg.emp_id =epd.emp_per_id and pg.is_paid='f' and pg.payment_mode=1 and pg.paycycle=? and paid_from =? and paid_to=?");
			pst.setInt(1,Integer.parseInt(strPC));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pst for unpaid employees=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmEmpAccNo = new HashMap<String, String>();
			while(rs.next()) {
				hmEmpAccNo.put(rs.getString("emp_id"), rs.getString("emp_bank_acct_nbr"));
			}
//			System.out.println("hmPayPayrollNEFT inside neft=====>"+hmPayPayrollNEFT);
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Data_Form");

			List<DataStyle> header = new ArrayList<DataStyle>();
			header.add(new DataStyle("             UPLOAD FILE GENERATION TOOL FOR RTGS (R41) & NEFT(N06)",Element.ALIGN_CENTER, "Calibri", 11, "0", "0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("THERE ARE TWO SHEETS IN THIS FILE", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//1
			header.add(new DataStyle("1 DATAINPUT FORM", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//2
			header.add(new DataStyle("2 NEFT/RTGS FORM", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//3
			header.add(new DataStyle("NO FIELD IS MIDIFYABLE IN SHEETS 2 WHERE AS IN SHEET 1 ONLY THE DATA ENTRY FIELDS (UNCOLORED FIELDS) ARE MODIFIABLE", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//4
			header.add(new DataStyle("FOR RTGS/NEFT", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//5
			header.add(new DataStyle("1 USER SHOULD ENTER THE REQUIRED DATA IN ALL FIELDS", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//6
			header.add(new DataStyle("2 MAXIMUM 225 ROWS CAN BE ENTERED IN SINGLE FILE.", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//7
		
			header.add(new DataStyle("3 AFTER ENTRY INTO THE SHEET 1 (DATA INPUT FORM),USER SHOULD CLICK ON THE 'GENERATE UPLOAD FILE BUTTON AS A RESULT UPLOADED FILE WILL BE", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//8
			header.add(new DataStyle("SAVED IN FORMATTED TEXT (<NAME>.PRN) FORMAT IN C/UPLOAD DIRECTORY.", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//9
			header.add(new DataStyle("4 FROM C/UPLOAD THIS FILE CAN BE UPLOADED USING PCUNIX & RTGSBULK.", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//10
			header.add(new DataStyle("NOTE:THIS TOOL GENERATE UPLOAD FILES IN THE SHORT FORMAT (RECORD LENGTH 234) OF RTGS/NEFT UPLOADS,IF BUSINESS REQUIREMENTS IS OF ADDITIONAL FIELDS WHICH ARE NOT COVERED IN THIS FORMAT \nTHEN USER SHOULD ENTER THAT FIELDS AFTER UPLOAD AND BEFORE POSTING/VERIFICATION OR USE THE UPLOADABLE FILE WITH ALL THE FIELDS (RECORDS LENGTH 761)", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//11
			header.add(new DataStyle("ACCOUNT NUMBER", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//12
			header.add(new DataStyle("AMOUNT", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//13
			header.add(new DataStyle("TRAN PARTICULAR", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//14
			header.add(new DataStyle("IFSC CODE", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//15
			
						
			header.add(new DataStyle("BENEFICIARY ACCOUNT", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//16
			header.add(new DataStyle("BENEFICIARY NAME ", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//17
			header.add(new DataStyle("ADDRESS", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//18
			header.add(new DataStyle("SENDER TO RECEIVER INF", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//19
			header.add(new DataStyle("SENDER TO RECEIVER INF", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//20
			header.add(new DataStyle("CHARGE ACCOUNT", Element.ALIGN_CENTER, "Calibri", 11, "0", "0", BaseColor.LIGHT_GRAY));//21
			
			Map<String, String> hmBankDetails = CF.getBankAccountDetailsMap(con, uF, getBankAccount());
			
			String bank_account = hmBankDetails.get("ACC_NO");
			String bank_ifsc = hmBankDetails.get("IFSC_CODE");
			/*pst = con.prepareStatement("select bank_ifsc_code,bank_account_no from branch_details where branch_id=?");
			pst.setInt(1, uF.parseToInt(getBankAccount()));
			rs = pst.executeQuery();
			if(rs.next()){
				bank_account = rs.getString("bank_account_no");
				bank_ifsc = rs.getString("bank_ifsc_code");
			}
			rs.close();
			pst.close();*/
			//List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();			
			List<DataStyle> innerList = new ArrayList<DataStyle>();
			Iterator<String> it = hmEmpAccNo.keySet().iterator();
			while (it.hasNext()) {
				String empId = it.next();
				String empAccNo = hmEmpAccNo.get(empId);
				Map hmInner1 = (Map)hmPayPayrollNEFT.get(empId);
				String empNet = (String)hmInner1.get("NET");
//							System.out.println("empNet======>"+empNet);
			
			innerList.add(new DataStyle(uF.showData(bank_account, ""), Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//1
			innerList.add(new DataStyle("", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//2
			innerList.add(new DataStyle("" ,Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//3
			innerList.add(new DataStyle(uF.showData(bank_ifsc, ""), Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//4
			innerList.add(new DataStyle(uF.showData(empAccNo, ""), Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//5
			innerList.add(new DataStyle("", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//6
			innerList.add(new DataStyle("", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//7
			
			innerList.add(new DataStyle("", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//8
			innerList.add(new DataStyle("", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//9
			innerList.add(new DataStyle("", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//10

			}
			//reportData.add(innerList);
			//System.out.println("reportData======>"+reportData);
			//ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
			getExcelSheetDesignDataForPnb(workbook, sheet, header, innerList);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=NEFT_Report.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

public void getExcelSheetDesignDataForPnb(HSSFWorkbook workbook, HSSFSheet sheet, List<DataStyle> header, List<DataStyle> reportData) {
	    Row firstRow =sheet.createRow(0);
	    Row secondRow =sheet.createRow(1);
		Row headerRow = sheet.createRow(2);
		Row thirdRow = sheet.createRow(3);
		Row contentRow1 = sheet.createRow(4);
		Row contentRow2 = sheet.createRow(5);
		Row contentRow3 = sheet.createRow(6);
		Row contentRow4 = sheet.createRow(7);
		Row blankRow8 = sheet.createRow(8);
		Row contentRow5 = sheet.createRow(9);
		Row contentRow6 = sheet.createRow(10);
		Row contentRow7 = sheet.createRow(11);
		Row contentRow8 = sheet.createRow(12);
		Row contentRow9 = sheet.createRow(13);
		Row contentRow10 = sheet.createRow(14);
		Row blankRow15 = sheet.createRow(15);
		Row contentRow11 = sheet.createRow(16);
		Row contentRow12 = sheet.createRow(17);
		Row blankRow18 = sheet.createRow(18);
		Row blankRow19 = sheet.createRow(19);
		Row TableHeader = sheet.createRow(20);
		
		HSSFCellStyle firstRowStyle = workbook.createCellStyle();
		firstRowStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		firstRowStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    Cell firstRowCell = firstRow.createCell((short)0);
		firstRowCell.setCellStyle(firstRowStyle);
		sheet.addMergedRegion(CellRangeAddress.valueOf("A1:U1"));
		
		HSSFCellStyle secondRowStyle = workbook.createCellStyle();
		secondRowStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		secondRowStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    Cell secondRowCell = secondRow.createCell((short)0);
		secondRowCell.setCellStyle(secondRowStyle);
		sheet.addMergedRegion(CellRangeAddress.valueOf("A2:U2"));
		
		HSSFCellStyle thirdRowStyle = workbook.createCellStyle();
		thirdRowStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		thirdRowStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    Cell thirdRowCell = thirdRow.createCell((short)0);
		thirdRowCell.setCellStyle(thirdRowStyle);
		sheet.addMergedRegion(CellRangeAddress.valueOf("A4:U4"));
		
		//Main Header
		HSSFCellStyle cellStyleForHalfHeaderRow = workbook.createCellStyle();
		cellStyleForHalfHeaderRow.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		cellStyleForHalfHeaderRow.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    Cell halfHeaderRowCell = headerRow.createCell((short)0);
	    halfHeaderRowCell.setCellStyle(cellStyleForHalfHeaderRow);
		sheet.addMergedRegion(CellRangeAddress.valueOf("A3:D3"));
		
	    Cell halfHeaderRowCell2 = headerRow.createCell((short)9);
	    halfHeaderRowCell2.setCellStyle(cellStyleForHalfHeaderRow);
		sheet.addMergedRegion(CellRangeAddress.valueOf("J3:U3"));
		
		HSSFCellStyle cellStyleForHeaderRow = workbook.createCellStyle();
		cellStyleForHeaderRow.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	    //cellStyleForHeaderRow.setLocked(false);
		cellStyleForHeaderRow.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		cellStyleForHeaderRow.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		cellStyleForHeaderRow.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		cellStyleForHeaderRow.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		cellStyleForHeaderRow.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
		cellStyleForHeaderRow.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	
		HSSFPalette palette = workbook.getCustomPalette();
	    palette.setColorAtIndex(HSSFColor.LIGHT_BLUE.index,
	            (byte) 210,
	            (byte) 142,
	            (byte) 178    
	    );
		//cellStyleForHeaderRow.setFillBackgroundColor(HSSFColor.YELLOW.index);
		//cellStyleForHeaderRow.setFillBackgroundColor(new HSSFColor.BLUE().getIndex());//(IndexedColors.GREEN.getIndex());
		Font headerNameFont = workbook.createFont();
		headerNameFont.setBoldweight((short)3000);	
		headerNameFont.setColor(HSSFColor.RED.index);
		DataStyle ds = (DataStyle)header.get(0);
		Cell headerName = headerRow.createCell((short)4);
		headerName.setCellValue(ds.getStrData());
		cellStyleForHeaderRow.setFont(headerNameFont);
		headerName.setCellStyle(cellStyleForHeaderRow);
		sheet.addMergedRegion(CellRangeAddress.valueOf("E3:I3"));
		
		//Line 1
		HSSFCellStyle cellStyleForContent1 = workbook.createCellStyle();
		cellStyleForContent1.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		cellStyleForContent1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		Cell blankCell = contentRow1.createCell((short)0);
		blankCell.setCellStyle(cellStyleForContent1);
		Font content1Font = workbook.createFont();
		content1Font.setBoldweight((short)2400);	
		DataStyle ds1 = (DataStyle)header.get(1);
		Cell contentLine1 = contentRow1.createCell((short)1);
		contentLine1.setCellValue(ds1.getStrData());
		cellStyleForContent1.setFont(content1Font);
		contentLine1.setCellStyle(cellStyleForContent1);
		sheet.addMergedRegion(CellRangeAddress.valueOf("B5:E5"));
		
		Cell blankCell2 = contentRow1.createCell((short)5);
		blankCell2.setCellStyle(cellStyleForContent1);
		sheet.addMergedRegion(CellRangeAddress.valueOf("F5:U5"));
		
		//Line 2
		HSSFCellStyle cellStyleForContent2 = workbook.createCellStyle();
		cellStyleForContent2.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		cellStyleForContent2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		Cell blankCellLine2 = contentRow2.createCell((short)0);
		blankCellLine2.setCellStyle(cellStyleForContent2);
		sheet.addMergedRegion(CellRangeAddress.valueOf("A6:B6"));
		Font content2Font = workbook.createFont();
		content2Font.setBoldweight((short)2400);	
		DataStyle ds2 = (DataStyle)header.get(2);
		Cell contentLine2 = contentRow2.createCell((short)2);
		contentLine2.setCellValue(ds2.getStrData());
		cellStyleForContent2.setFont(content2Font);
		contentLine2.setCellStyle(cellStyleForContent2);
		sheet.addMergedRegion(CellRangeAddress.valueOf("C6:E6"));
		
		Cell blankCell2Line2 = contentRow2.createCell((short)5);
		blankCell2Line2.setCellStyle(cellStyleForContent2);
		sheet.addMergedRegion(CellRangeAddress.valueOf("F6:U6"));
		
		//Line 3
		HSSFCellStyle cellStyleForContent3 = workbook.createCellStyle();
		cellStyleForContent3.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		cellStyleForContent3.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		Cell blankCellLine3 = contentRow3.createCell((short)0);
		blankCellLine3.setCellStyle(cellStyleForContent3);
		sheet.addMergedRegion(CellRangeAddress.valueOf("A7:B7"));
		Font content3Font = workbook.createFont();
		content3Font.setBoldweight((short)2400);	
		DataStyle ds3 = (DataStyle)header.get(3);
		Cell contentLine3 = contentRow3.createCell((short)2);
		contentLine3.setCellValue(ds3.getStrData());
		cellStyleForContent3.setFont(content3Font);
		contentLine3.setCellStyle(cellStyleForContent3);
		sheet.addMergedRegion(CellRangeAddress.valueOf("C7:E7"));
		
		Cell blankCell2Line3 = contentRow3.createCell((short)5);
		blankCell2Line3.setCellStyle(cellStyleForContent3);
		sheet.addMergedRegion(CellRangeAddress.valueOf("F7:U7"));
		
		//Line 4
		HSSFCellStyle cellStyleForContent4 = workbook.createCellStyle();
		cellStyleForContent4.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		cellStyleForContent4.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		Cell blankCellLine4 = contentRow4.createCell((short)0);
		blankCellLine4.setCellStyle(cellStyleForContent4);
		Font content4Font = workbook.createFont();
		content4Font.setBoldweight((short)2400);	
		DataStyle ds4 = (DataStyle)header.get(4);
		Cell contentLine4 = contentRow4.createCell((short)1);
		contentLine4.setCellValue(ds4.getStrData());
		cellStyleForContent4.setFont(content4Font);
		contentLine4.setCellStyle(cellStyleForContent4);
		sheet.addMergedRegion(CellRangeAddress.valueOf("B8:O8"));
		
		Cell blankCell2Line4 = contentRow3.createCell((short)9);
		blankCell2Line4.setCellStyle(cellStyleForContent4);
		sheet.addMergedRegion(CellRangeAddress.valueOf("J8:U8"));
		
		//Blank row 8
		HSSFCellStyle cellStyleForBlankRow8 = workbook.createCellStyle();
		cellStyleForBlankRow8.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		cellStyleForBlankRow8.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		Cell blankCellRow8 = blankRow8.createCell((short)0);
		blankCellRow8.setCellStyle(cellStyleForBlankRow8);
		sheet.addMergedRegion(CellRangeAddress.valueOf("A9:U9"));
		
		//Line 5
		HSSFCellStyle cellStyleForContent5 = workbook.createCellStyle();
		cellStyleForContent5.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		cellStyleForContent5.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		Cell blankCellLine5 = contentRow5.createCell((short)0);
		blankCellLine5.setCellStyle(cellStyleForContent4);
		Font content5Font = workbook.createFont();
		content5Font.setBoldweight((short)2400);	
		DataStyle ds5 = (DataStyle)header.get(5);
		Cell contentLine5 = contentRow5.createCell((short)1);
		contentLine5.setCellValue(ds5.getStrData());
		cellStyleForContent5.setFont(content5Font);
		contentLine5.setCellStyle(cellStyleForContent5);
		sheet.addMergedRegion(CellRangeAddress.valueOf("B10:C10"));
		
		Cell blankCell2Line5 = contentRow5.createCell((short)3);
		blankCell2Line5.setCellStyle(cellStyleForContent5);
		sheet.addMergedRegion(CellRangeAddress.valueOf("D10:U10"));
		
		//Line 6
		HSSFCellStyle cellStyleForContent6 = workbook.createCellStyle();
		cellStyleForContent6.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		cellStyleForContent6.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		Cell blankCellLine6 = contentRow6.createCell((short)0);
		sheet.addMergedRegion(CellRangeAddress.valueOf("A11:B11"));
		blankCellLine6.setCellStyle(cellStyleForContent6);
		Font content6Font = workbook.createFont();
		content6Font.setBoldweight((short)2400);	
		DataStyle ds6 = (DataStyle)header.get(6);
		Cell contentLine6 = contentRow6.createCell((short)2);
		contentLine6.setCellValue(ds6.getStrData());
		cellStyleForContent6.setFont(content6Font);
		contentLine6.setCellStyle(cellStyleForContent6);
		sheet.addMergedRegion(CellRangeAddress.valueOf("C11:J11"));
		
		Cell blankCell2Line6 = contentRow6.createCell((short)10);
		blankCell2Line6.setCellStyle(cellStyleForContent6);
		sheet.addMergedRegion(CellRangeAddress.valueOf("K11:U11"));
		
		//Line 7
		HSSFCellStyle cellStyleForContent7 = workbook.createCellStyle();
		cellStyleForContent7.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		cellStyleForContent7.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		Cell blankCellLine7 = contentRow7.createCell((short)0);
		sheet.addMergedRegion(CellRangeAddress.valueOf("A12:B12"));
		blankCellLine7.setCellStyle(cellStyleForContent7);
		Font content7Font = workbook.createFont();
		content7Font.setBoldweight((short)2400);	
		DataStyle ds7 = (DataStyle)header.get(7);
		Cell contentLine7 = contentRow7.createCell((short)2);
		contentLine7.setCellValue(ds7.getStrData());
		cellStyleForContent7.setFont(content7Font);
		contentLine7.setCellStyle(cellStyleForContent7);
		sheet.addMergedRegion(CellRangeAddress.valueOf("C12:J12"));
		
		Cell blankCell2Line7 = contentRow7.createCell((short)10);
		blankCell2Line7.setCellStyle(cellStyleForContent7);
		sheet.addMergedRegion(CellRangeAddress.valueOf("K12:U12"));
		
		//Line 8
		HSSFCellStyle cellStyleForContent8 = workbook.createCellStyle();
		cellStyleForContent8.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		cellStyleForContent8.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		Cell blankCellLine8 = contentRow8.createCell((short)0);
		sheet.addMergedRegion(CellRangeAddress.valueOf("A13:B13"));
		blankCellLine8.setCellStyle(cellStyleForContent8);
		Font content8Font = workbook.createFont();
		content8Font.setBoldweight((short)2400);	
		DataStyle ds8 = (DataStyle)header.get(8);
		Cell contentLine8 = contentRow8.createCell((short)2);
		contentLine8.setCellValue(ds8.getStrData());
		cellStyleForContent8.setFont(content8Font);
		contentLine8.setCellStyle(cellStyleForContent8);
		sheet.addMergedRegion(CellRangeAddress.valueOf("C13:R13"));
		
		Cell blankCell2Line8 = contentRow8.createCell((short)10);
		blankCell2Line8.setCellStyle(cellStyleForContent8);
		sheet.addMergedRegion(CellRangeAddress.valueOf("K13:U13"));
		
		//Line 9
		HSSFCellStyle cellStyleForContent9 = workbook.createCellStyle();
		cellStyleForContent9.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		cellStyleForContent9.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		Cell blankCellLine9 = contentRow9.createCell((short)0);
		sheet.addMergedRegion(CellRangeAddress.valueOf("A14:B14"));
		blankCellLine9.setCellStyle(cellStyleForContent9);
		
		Font content9Font = workbook.createFont();
		content9Font.setBoldweight((short)2400);	
		DataStyle ds9 = (DataStyle)header.get(9);
		Cell contentLine9 = contentRow9.createCell((short)2);
		contentLine9.setCellValue(ds9.getStrData());
		cellStyleForContent9.setFont(content9Font);
		contentLine9.setCellStyle(cellStyleForContent9);
		sheet.addMergedRegion(CellRangeAddress.valueOf("C14:J14"));
		
		Cell blankCell2Line9 = contentRow9.createCell((short)10);
		blankCell2Line9.setCellStyle(cellStyleForContent9);
		sheet.addMergedRegion(CellRangeAddress.valueOf("K14:U14"));
		
		//Line 10
		HSSFCellStyle cellStyleForContent10 = workbook.createCellStyle();
		cellStyleForContent10.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		cellStyleForContent10.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		Cell blankCellLine10 = contentRow10.createCell((short)0);
		sheet.addMergedRegion(CellRangeAddress.valueOf("A15:B15"));
		blankCellLine10.setCellStyle(cellStyleForContent10);
		Font content10Font = workbook.createFont();
		content10Font.setBoldweight((short)2400);	
		DataStyle ds10 = (DataStyle)header.get(10);
		Cell contentLine10 = contentRow10.createCell((short)2);
		contentLine10.setCellValue(ds10.getStrData());
		cellStyleForContent10.setFont(content10Font);
		contentLine10.setCellStyle(cellStyleForContent10);
		sheet.addMergedRegion(CellRangeAddress.valueOf("C15:J15"));
		
		Cell blankCell2Line10 = contentRow10.createCell((short)10);
		blankCell2Line10.setCellStyle(cellStyleForContent10);
		sheet.addMergedRegion(CellRangeAddress.valueOf("K15:U15"));
		
		//Blank row 15
		HSSFCellStyle cellStyleForBlankRow15 = workbook.createCellStyle();
		cellStyleForBlankRow15.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		cellStyleForBlankRow15.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		Cell blankCellRow15 = blankRow15.createCell((short)0);
		blankCellRow15.setCellStyle(cellStyleForBlankRow15);
		sheet.addMergedRegion(CellRangeAddress.valueOf("A16:U16"));
		
		//Line 11
		HSSFCellStyle cellStyleForContent11 = workbook.createCellStyle();
		
		cellStyleForContent11.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		cellStyleForContent11.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		cellStyleForContent11.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		cellStyleForContent11.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		cellStyleForContent11.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		cellStyleForContent11.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		Cell blankCellLine11 = contentRow11.createCell((short)0);
		
		blankCellLine11.setCellStyle(cellStyleForContent10);
		
		Font content11Font = workbook.createFont();
		//content11Font.setBoldweight((short)2400);	
		DataStyle ds11 = (DataStyle)header.get(11);
		Cell contentLine11 = contentRow11.createCell((short)1);
		contentLine11.setCellValue(ds11.getStrData());
		cellStyleForContent11.setFont(content11Font);
		contentLine11.setCellStyle(cellStyleForContent11);
		sheet.addMergedRegion(CellRangeAddress.valueOf("B17:T18"));
		
		Cell lastcell = contentRow11.createCell((short)20);
		lastcell.setCellStyle(cellStyleForContent10);
		
		
		Cell firstcell = contentRow12.createCell((short)0);
		firstcell.setCellStyle(cellStyleForContent10);
		
		
		Cell lastcell2 = contentRow12.createCell((short)20);
		lastcell2.setCellStyle(cellStyleForContent10);
		
		
		//Blank row 18
		HSSFCellStyle cellStyleForBlankRow18 = workbook.createCellStyle();
		cellStyleForBlankRow18.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		cellStyleForBlankRow18.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		Cell blankCellRow18 = blankRow18.createCell((short)0);
		blankCellRow18.setCellStyle(cellStyleForBlankRow18);
		sheet.addMergedRegion(CellRangeAddress.valueOf("A19:U19"));
		
		//Blank row 19
		HSSFCellStyle cellStyleForBlankRow19 = workbook.createCellStyle();
		cellStyleForBlankRow19.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		cellStyleForBlankRow19.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		Cell blankCellRow19 = blankRow19.createCell((short)0);
		blankCellRow19.setCellStyle(cellStyleForBlankRow19);
		sheet.addMergedRegion(CellRangeAddress.valueOf("A20:U20"));
		
		//Main Table's first cell
		Cell headerCellZero = TableHeader.createCell(0);
		headerCellZero.setCellStyle(cellStyleForBlankRow19);
		
		//Main Table's Remaining Cells
		Cell headerCellLast = TableHeader.createCell(11);
		headerCellLast.setCellStyle(cellStyleForBlankRow19);
		sheet.addMergedRegion(CellRangeAddress.valueOf("L21:U21"));
		
		//Main Table Starts
		Font font = workbook.createFont();
		font.setBoldweight((short)1200);	
		for(int i=12,y=1;i<header.size();i++,y++){
			Cell headerCell = TableHeader.createCell(y);
			DataStyle dstable = (DataStyle)header.get(i);
		//===start parvez date: 16-08-2022===	
			headerCell.setCellValue(dstable.getStrData());
		//===end parvez date: 16-08-2022===	
			HSSFCellStyle cellStyleForTableHeader = workbook.createCellStyle();
			cellStyleForTableHeader.setBorderBottom(dstable.getBorderStyle());
			cellStyleForTableHeader.setBorderLeft(dstable.getBorderStyle());
			cellStyleForTableHeader.setBorderRight(dstable.getBorderStyle());
			cellStyleForTableHeader.setBorderTop(dstable.getBorderStyle());
			cellStyleForTableHeader.setAlignment(dstable.getCellDataAlign());
			cellStyleForTableHeader.setFillForegroundColor(dstable.getHSSFbackRoundColor());
			//cellStyleForTableHeader.setFillPattern(dstable.getFillPattern());
			cellStyleForTableHeader.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
			cellStyleForTableHeader.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			HSSFPalette palette1 = workbook.getCustomPalette();
		    palette1.setColorAtIndex(HSSFColor.LIGHT_BLUE.index,
		            (byte) 210,
		            (byte) 142,
		            (byte) 178    
		    );
			cellStyleForTableHeader.setFont(font);
     		sheet.autoSizeColumn((short)y);
			headerCell.setCellStyle(cellStyleForTableHeader);		
		}
		
		
		int rownum = 21;
		Row row = sheet.createRow(rownum);
		HSSFCellStyle cellStyleForData;
		int k=1;
		int count =1;
		
		for (int j = 0; reportData!=null && j < reportData.size(); j++) {
			if(k==10){
				rownum++;
				count++;
			    row = sheet.createRow(rownum);
			    k=0;    
			}
			    cellStyleForData = workbook.createCellStyle();
			    cellStyleForData.setLocked(false);
			    cellStyleForData.setBorderTop(ds.getBorderStyle());
				cellStyleForData.setBorderBottom(ds.getBorderStyle());
				cellStyleForData.setBorderLeft(ds.getBorderStyle());
				cellStyleForData.setBorderRight(ds.getBorderStyle());
				cellStyleForData.setAlignment(ds.getCellDataAlign());	
				
			    DataStyle userData = reportData.get(j);
			    Cell numberCell = row.createCell(0);
			    numberCell.setCellValue(count);
			    numberCell.setCellStyle(cellStyleForBlankRow19);
				Cell cell = row.createCell(k);
				cell.setCellStyle(cellStyleForData);
			//===start parvez date: 16-08-2022===	
				cell.setCellValue(userData.getStrData());
			//===end parvez date: 16-08-2022===	
				
//				sheet.autoSizeColumn((short)l);
			
				k++;	
		}	
		cellStyleForData = workbook.createCellStyle();
	    cellStyleForData.setLocked(false);
		while(count<=225){
			 row = sheet.createRow(rownum);
			 Cell numberCell1 = row.createCell(0);
			 numberCell1.setCellValue(count);
			 numberCell1.setCellStyle(cellStyleForBlankRow19);
			 for(int i=1;i<=10;i++){
				 Cell datacell = row.createCell(i);
				 datacell.setCellStyle(cellStyleForData);
			 }
			 rownum++;
			 count++;
		}
	sheet.protectSheet("password");
	}


public void generateNeftCanaraBank(UtilityFunctions uF, String  strD1, String strD2, String strPC) {
//	System.out.println("strPC====>"+strPC);
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {

		con = db.makeConnection(con);
		StringBuilder sbQuery = new StringBuilder();
		sbQuery.append("select * from payroll_generation pg, employee_official_details eod where pg.emp_id = eod.emp_id and paycycle= ? and paid_from =? and paid_to=? ");
		
		if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0) {
        	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
        } else {
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
		if (getF_department() != null && getF_department().length > 0) {
			sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
		}
		if (getF_service() != null && getF_service().length > 0) {
			sbQuery.append(" and (");
			for (int i = 0; i < getF_service().length; i++) {
				sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
				if (i < getF_service().length - 1) {
					sbQuery.append(" OR ");
				}
			}
			sbQuery.append(" ) ");
		}
		
		if (getStrPaycycleDuration() != null) {
			sbQuery.append(" and pay_mode ='" + getStrPaycycleDuration() + "'");
		}

		if (uF.parseToInt(getF_paymentMode()) > 0) {
			sbQuery.append(" and pg.payment_mode =" + uF.parseToInt(getF_paymentMode()));
		}
		
		if (getBankBranch() != null && getBankBranch().length > 0) {
			sbQuery.append(" and eod.emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
				"and ((epd.emp_bank_name is not null and epd.emp_bank_name !='' and CAST(epd.emp_bank_name AS integer) in ("+StringUtils.join(getBankBranch(), ",")+")) " +
				"or (epd.emp_bank_name2 is not null and epd.emp_bank_name2 !='' and CAST (epd.emp_bank_name2 AS integer) in ("+StringUtils.join(getBankBranch(), ",")+"))))");
//			sbQuery.append(" and eod.emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
//				"and (CAST(epd.emp_bank_name AS integer) in ("+StringUtils.join(getBankBranch(), ",")+") or CAST (epd.emp_bank_name2 AS integer) in ("+StringUtils.join(getBankBranch(), ",")+")))");
		}

		if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
			sbQuery.append(" and eod.wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
		} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(WLOCATION_ACCESS) != null) {
			sbQuery.append(" and eod.wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
		}

		if (uF.parseToInt(getF_org()) > 0) {
			sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
		} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(ORG_ACCESS) != null) {
			sbQuery.append(" and eod.org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
		}
		sbQuery.append(" order by pg.emp_id, earning_deduction desc, salary_head_id");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setInt(1, uF.parseToInt(strPC));
		pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
//		System.out.println("pst1====>"+pst);
		rs = pst.executeQuery();
		double dblNetAmount = 0.0d;
		Map hmInner = new HashMap();
		Map hmSalary = new HashMap();
		List alEarnings = new ArrayList();
		List alDeductions = new ArrayList();
		List<String> alSalaryHead = new ArrayList<String>();
 		Map hmPayPayrollNEFT = new HashMap();
		Map hmEmpPayroll = null;
		Map hmIsApprovedSalary = new HashMap();
		Map<String, String> hmPaymentModeMap = CF.getPaymentMode();
		String strEmpIdOld = null;
		String strEmpIdNew = null;
		double dblGross = 0;
		double dblNet = 0;
		while (rs.next()) {
			strEmpIdNew = rs.getString("emp_id");

			if (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
				hmEmpPayroll = new HashMap();
				dblNet = 0;
			}

			if ("E".equalsIgnoreCase(rs.getString("earning_deduction")) && !alEarnings.contains(rs.getString("salary_head_id"))) {
				alEarnings.add(rs.getString("salary_head_id"));
				if (!alSalaryHead.contains(rs.getString("salary_head_id"))) {
					alSalaryHead.add(rs.getString("salary_head_id"));
				}
			} else if ("D".equalsIgnoreCase(rs.getString("earning_deduction")) && !alDeductions.contains(rs.getString("salary_head_id"))) {
				alDeductions.add(rs.getString("salary_head_id"));
				if (!alSalaryHead.contains(rs.getString("salary_head_id"))) {
					alSalaryHead.add(rs.getString("salary_head_id"));
				}
			}
			
			hmEmpPayroll.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("amount"))));

			if ("E".equalsIgnoreCase(rs.getString("earning_deduction"))) {
				double dblAmount = rs.getDouble("amount");
				dblGross = uF.parseToDouble((String) hmEmpPayroll.get("GROSS"));
				dblNet += dblAmount;
				hmEmpPayroll.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), Math.round(dblGross + dblAmount)));

			} else {
				double dblAmount = rs.getDouble("amount");
				dblNet -= dblAmount;
			}
			hmEmpPayroll.put("NET", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), Math.round(dblNet)));
			hmEmpPayroll.put("PAYMENT_MODE", hmPaymentModeMap.get(uF.parseToInt(rs.getString("payment_mode")) + ""));
			hmPayPayrollNEFT.put(strEmpIdNew, hmEmpPayroll);

			/*if (rs.getBoolean("is_paid")) {
				hmIsApprovedSalary.put(strEmpIdNew, rs.getString("is_paid"));
			}*/

			strEmpIdOld = strEmpIdNew;
		}
		rs.close();
		pst.close();
		pst = con.prepareStatement("select distinct pg.emp_id as emp_id,epd.emp_bank_acct_nbr from payroll_generation pg,employee_personal_details epd " +
			"where pg.emp_id =epd.emp_per_id and pg.is_paid='f' and pg.payment_mode=1 and pg.paycycle=? and paid_from =? and paid_to=? ");
		pst.setInt(1,Integer.parseInt(strPC));
		pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
//		System.out.println("pst for unpaid employees=====>"+pst);
		rs = pst.executeQuery();
		Map<String, String> hmEmpAccNo = new HashMap<String, String>();
		while(rs.next()) {
			hmEmpAccNo.put(rs.getString("emp_id"), rs.getString("emp_bank_acct_nbr"));
//			empids.add(rs.getInt("emp_id"));
		}
//		System.out.println("hmPayPayrollNEFT inside neft=====>"+hmPayPayrollNEFT);
			
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Payment Held");

		List<DataStyle> header = new ArrayList<DataStyle>();
		header.add(new DataStyle("Process Date " + uF.showData(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), ""), Element.ALIGN_CENTER, "Arial", 6, "0", "0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Txn Type", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//1
		header.add(new DataStyle("Account Number", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//2
		header.add(new DataStyle("Branch Code", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//3
		header.add(new DataStyle("Txn Code", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//4
		header.add(new DataStyle("Txn Date", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//5
		header.add(new DataStyle("Dr/Cr", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//6
		header.add(new DataStyle("Value Dt", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//7
	
		header.add(new DataStyle("Txn CCY", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//8
		header.add(new DataStyle("Amt LCY", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//9
		header.add(new DataStyle("Amt LCY", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//10
		header.add(new DataStyle("Rate Con", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//11
		header.add(new DataStyle("Ref No.", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//12
		header.add(new DataStyle("Ref Doc No.", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//13
		header.add(new DataStyle("Transaction Description", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//14
		header.add(new DataStyle("Benef IC", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//15
		header.add(new DataStyle("Benef Name", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//16
		
					
		header.add(new DataStyle("Benef Add1", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//17
		header.add(new DataStyle("Benef Add2", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//18
		header.add(new DataStyle("Benef Add3", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//19
		header.add(new DataStyle("Benef City", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//20
		header.add(new DataStyle("Benef State", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//21
		header.add(new DataStyle("Benef Country", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//22
		header.add(new DataStyle("Benef Zip", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//23
		header.add(new DataStyle("Option", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//24
		
		header.add(new DataStyle("Issuer Code", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//25
		header.add(new DataStyle("Payable At", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//26
		header.add(new DataStyle("Flg FDT", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//27
		header.add(new DataStyle("MIS Account Number", Element.ALIGN_CENTER, "Arial", 6, "0", "0", BaseColor.LIGHT_GRAY));//28
		
		
		//List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();			
					List<DataStyle> innerList = new ArrayList<DataStyle>();
					
					Iterator<String> it = hmEmpAccNo.keySet().iterator();
					while (it.hasNext()) {
						String empId = it.next();
						String empAccNo = hmEmpAccNo.get(empId);
						Map hmInner1 = (Map)hmPayPayrollNEFT.get(empId);
						String empNet = (String)hmInner1.get("NET");
//						System.out.println("empNet======>"+empNet);
					
					innerList.add(new DataStyle("1", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//1
					innerList.add(new DataStyle(uF.showData(empAccNo, ""), Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//2
					innerList.add(new DataStyle("0425" ,Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//3
					innerList.add(new DataStyle("1408", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//4
					innerList.add(new DataStyle(uF.showData(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), ""), Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//5
					innerList.add(new DataStyle("Cr", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//6
					innerList.add(new DataStyle(uF.showData(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), ""), Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//7
					
					innerList.add(new DataStyle("104", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//8
					innerList.add(new DataStyle(uF.showData(empNet, "0"), Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//9
					innerList.add(new DataStyle(uF.showData(empNet, "0"), Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//10
					innerList.add(new DataStyle("1", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//11
					innerList.add(new DataStyle("0", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//12
					
					innerList.add(new DataStyle("0", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//13
					innerList.add(new DataStyle("By", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//14
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//15
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//16
					
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//17
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//18
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//19
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//20
					
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//21
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//22
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//23
					innerList.add(new DataStyle("30", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//24
					
					innerList.add(new DataStyle("0", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//25
					innerList.add(new DataStyle("0", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//26
					innerList.add(new DataStyle("N", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//27
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "Arial", 10, "0", "0", BaseColor.WHITE));//28
					}
					//reportData.add(innerList);
					//System.out.println("reportData======>"+reportData);
					//ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
					getExcelSheetDesignDataPay(workbook, sheet, header, innerList);
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					workbook.write(buffer);
					response.setContentType("application/vnd.ms-excel:UTF-8");
					response.setContentLength(buffer.size());
					response.setHeader("Content-Disposition", "attachment; filename=NEFT_Report.xls");
					ServletOutputStream out = response.getOutputStream();
					buffer.writeTo(out);
					out.flush();
					buffer.close();
					out.close();
	
	}
	catch (Exception e) {
		e.printStackTrace();
	}
}

	public void getExcelSheetDesignDataPay(HSSFWorkbook workbook, HSSFSheet sheet, List<DataStyle> header, List<DataStyle> reportData) {
		
		Row headerRow = sheet.createRow(4);
		Row reportNameRow = sheet.createRow(2);
		
		HSSFCellStyle cellStyleForReportName = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight((short)1200);	
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);	
		
		DataStyle ds = (DataStyle)header.get(0);
		
		//Cell reportName = reportNameRow.createCell(header.size()/2);
		Cell reportName = reportNameRow.createCell((short)1);
		
		reportName.setCellValue(ds.getStrData());
		cellStyleForReportName.setFont(reportNameFont);
		reportName.setCellStyle(cellStyleForReportName);
		
		//sheet.addMergedRegion(CellRangeAddress.valueOf("B3:E3"));
		
		for(int i=1,y=0;i<header.size();i++,y++){
			Cell headerCell = headerRow.createCell(y);
			ds = (DataStyle)header.get(i);
			
		//===start parvez date: 16-08-2022===	
			headerCell.setCellValue(ds.getStrData());
		//===end parvez date: 16-08-2022===	
			HSSFCellStyle cellStyleForHeader = workbook.createCellStyle();
			cellStyleForHeader.setBorderBottom(ds.getBorderStyle());
			cellStyleForHeader.setBorderLeft(ds.getBorderStyle());
			cellStyleForHeader.setBorderRight(ds.getBorderStyle());
			cellStyleForHeader.setBorderTop(ds.getBorderStyle());
			cellStyleForHeader.setAlignment(ds.getCellDataAlign());
			cellStyleForHeader.setFillForegroundColor(ds.getHSSFbackRoundColor());
			cellStyleForHeader.setFillPattern(ds.getFillPattern());
			cellStyleForHeader.setFont(font);
//			sheet.autoSizeColumn((short)y);
			headerCell.setCellStyle(cellStyleForHeader);
			
			
		}
		
		
		int rownum = 5;
		Row row = sheet.createRow(rownum);
		HSSFCellStyle cellStyleForData;
		int k=0;
		
		for (int j = 0; reportData!=null && j < reportData.size(); j++) {
			if(k==28){
				rownum++;
			    row = sheet.createRow(rownum);
			    k=0;
			}
			DataStyle userData = reportData.get(j);
			//for (int k = 0, l=0; k < userData.size(); k++,l++) {
				Cell cell = row.createCell(k);
				
			//===start parvez date: 16-08-2022===	
				cell.setCellValue(userData.getStrData());	
			//===end parvez date: 16-08-2022===	
				cellStyleForData = workbook.createCellStyle();
				cellStyleForData.setBorderTop(ds.getBorderStyle());
				cellStyleForData.setBorderBottom(ds.getBorderStyle());
				cellStyleForData.setBorderLeft(ds.getBorderStyle());
				cellStyleForData.setBorderRight(ds.getBorderStyle());
				cellStyleForData.setAlignment(ds.getCellDataAlign());						
				
//				sheet.autoSizeColumn((short)l);
				cell.setCellStyle(cellStyleForData);
				k++;
			//}
			
		}	
	}

	private void viewApporvedPayrollByGrade(UtilityFunctions uF, String strD1, String strD2, String strPC) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			Map hmEmpMap = CF.getEmpNameMap(con, null, null);
			Map hmEmpCodeMap = CF.getEmpCodeMap(con);
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map hmSalaryDetails = CF.getSalaryHeadsMap(con);
			Map<String, String> hmLoanPoliciesMap = CF.getLoanPoliciesMap(con, uF, getF_org());

			List<List<String>> alPaycycleList = new ArrayList<List<String>>();
			for (int i = 0; paycycleList != null && ((i < paycycleList.size() && paycycleList.size()>6 && i< 6) || (i < paycycleList.size() && paycycleList.size()<=6)); i++) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(paycycleList.get(i).getPaycycleId());
				innerList.add(paycycleList.get(i).getPaycycleName());
				String[] strTmp = paycycleList.get(i).getPaycycleId().split("-");
				List<String> alList = getPaidAndUnpaidEmpCount(uF, strTmp[0], strTmp[1], strTmp[2]);
					innerList.add(alList.get(0));
					innerList.add(alList.get(1));
//					innerList.add("0");
//					innerList.add("0");
				alPaycycleList.add(innerList);
			}
			request.setAttribute("alPaycycleList", alPaycycleList);
			
			Map<String, String> hmPaymentModeMap = CF.getPaymentMode();

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from payroll_generation pg, employee_official_details eod where pg.emp_id = eod.emp_id and paycycle= ? and paid_from =? and paid_to=? ");
			if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0) {
	            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
	        } else {
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

			if (getF_service() != null && getF_service().length > 0) {
				sbQuery.append(" and (");
				for (int i = 0; i < getF_service().length; i++) {
					sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
					if (i < getF_service().length - 1) {
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" ) ");
			}
			if (getStrPaycycleDuration() != null) {
				sbQuery.append(" and pay_mode ='" + getStrPaycycleDuration() + "'");
			}
			if (uF.parseToInt(getF_paymentMode()) > 0) {
				sbQuery.append(" and pg.payment_mode =" + uF.parseToInt(getF_paymentMode()));
			}
			
			if (getBankBranch() != null && getBankBranch().length > 0) {
				sbQuery.append(" and eod.emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					"and ((epd.emp_bank_name is not null and epd.emp_bank_name !='' and CAST(epd.emp_bank_name AS integer) in ("+StringUtils.join(getBankBranch(), ",")+")) " +
					"or (epd.emp_bank_name2 is not null and epd.emp_bank_name2 !='' and CAST (epd.emp_bank_name2 AS integer) in ("+StringUtils.join(getBankBranch(), ",")+"))))");
//				sbQuery.append(" and eod.emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
//					"and (CAST(epd.emp_bank_name AS integer) in ("+StringUtils.join(getBankBranch(), ",")+") or CAST (epd.emp_bank_name2 AS integer) in ("+StringUtils.join(getBankBranch(), ",")+")))");
			}
			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
				sbQuery.append(" and eod.wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(WLOCATION_ACCESS) != null) {
				sbQuery.append(" and eod.wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
			}
			if (uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(ORG_ACCESS) != null) {
				sbQuery.append(" and eod.org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
			}
			sbQuery.append(" order by pg.emp_id,sal_effective_date, earning_deduction desc, salary_head_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strPC));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("payroll pst====>"+pst);
			rs = pst.executeQuery();
			double dblNetAmount = 0.0d;
			Map hmInner = new HashMap();
			Map hmSalary = new HashMap();
			List alEarnings = new ArrayList();
			List alDeductions = new ArrayList();
			List<String> alSalaryHead = new ArrayList<String>();
			Map hmPayPayroll = new HashMap();
			Map hmIsApprovedSalary = new HashMap();
			Map<String, String> hmEmpSalLastEffectiveDate = new HashMap<String, String>();
			String strEmpIdNew = null;
			double dblGross = 0;
			double dblNet = 0;
			while (rs.next()) {
				strEmpIdNew = rs.getString("emp_id");
//
//				if (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
//					hmEmpPayroll = new HashMap();
//					dblNet = 0;
//				}
				Map hmEmpPayroll = (Map)hmPayPayroll.get(strEmpIdNew+"_"+rs.getString("sal_effective_date"));
				if(hmEmpPayroll == null) {
					hmEmpPayroll = new HashMap();
					dblNet = 0;
//					System.out.println(rs.getString("sal_effective_date") + " -- dblNet ===>> " + dblNet);
				}
				
				if ("E".equalsIgnoreCase(rs.getString("earning_deduction")) && !alEarnings.contains(rs.getString("salary_head_id"))) {
					alEarnings.add(rs.getString("salary_head_id"));
					if (!alSalaryHead.contains(rs.getString("salary_head_id"))) {
						alSalaryHead.add(rs.getString("salary_head_id"));
					}
				} else if ("D".equalsIgnoreCase(rs.getString("earning_deduction")) && !alDeductions.contains(rs.getString("salary_head_id"))) {
					alDeductions.add(rs.getString("salary_head_id"));
					if (!alSalaryHead.contains(rs.getString("salary_head_id"))) {
						alSalaryHead.add(rs.getString("salary_head_id"));
					}
				}

				hmEmpPayroll.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("amount"))));

				if ("E".equalsIgnoreCase(rs.getString("earning_deduction"))) {
					double dblAmount = rs.getDouble("amount");
					dblGross = uF.parseToDouble((String) hmEmpPayroll.get("GROSS"));
					dblNet += dblAmount;
					hmEmpPayroll.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(dblGross + dblAmount)));

				} else {
					double dblAmount = rs.getDouble("amount");
					dblNet -= dblAmount;
				}

				Map<String, String> hmCurrency = (Map) hmCurrencyDetails.get(rs.getString("currency_id"));
				if (hmCurrency == null)
					hmCurrency = new HashMap<String, String>();

				hmEmpPayroll.put("NET", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNet));
				hmEmpPayroll.put("PAYMENT_MODE", hmPaymentModeMap.get(uF.parseToInt(rs.getString("payment_mode")) + ""));
				
				hmPayPayroll.put(strEmpIdNew+"_"+rs.getString("sal_effective_date"), hmEmpPayroll);
//	        	System.out.println("hmPayPayroll========>"+hmPayPayroll);

				if (rs.getBoolean("is_paid")) {
					hmIsApprovedSalary.put(strEmpIdNew+"_"+rs.getString("sal_effective_date"), rs.getString("is_paid"));
				}
				hmEmpSalLastEffectiveDate.put(strEmpIdNew, rs.getString("sal_effective_date"));
//				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
			// System.out.println("getStrMonth()== 1 =>"+getStrMonth());

			if (salaryHeadList == null) {
				salaryHeadList = new ArrayList<FillSalaryHeads>();
			}
			for (String salaryHeadId : alSalaryHead) {
				salaryHeadList.add(new FillSalaryHeads(salaryHeadId, (String) hmSalaryDetails.get(salaryHeadId)));
			}

			if (getStrMonth() != null) {
				setStrMonth(getStrMonth());
			} else {
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, "MM")) + "");
			}

			List alLoans = new ArrayList();
			Map hmEmpLoan = new HashMap();
			CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, strD1, strD2, hmEmpSalLastEffectiveDate);

			request.setAttribute("hmEmpLoan", hmEmpLoan);
			request.setAttribute("alLoans", alLoans);

			request.setAttribute("alEarnings", alEarnings);
			request.setAttribute("alDeductions", alDeductions);
			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
			request.setAttribute("hmLoanPoliciesMap", hmLoanPoliciesMap);
			request.setAttribute("hmPayPayroll", hmPayPayroll);
			request.setAttribute("hmEmpMap", hmEmpMap);
			request.setAttribute("hmEmpCodeMap", hmEmpCodeMap);
			request.setAttribute("hmIsApprovedSalary", hmIsApprovedSalary);
			
			request.setAttribute("strD1", strD1);
			request.setAttribute("strD2", strD2);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void payApporvedPayrollByGrade(String strD1, String strD2, String strPC) {

		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map<String, Map<String, String>> hmArearAmountMap = CF.getArearDetails(con, uF, CF, strD2);

			pst = con.prepareStatement("select * from payroll_generation where is_paid = false and paycycle = ? and paid_from =? and paid_to=? order by emp_id");
			pst.setInt(1, uF.parseToInt(strPC));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println("pst====>"+pst);
			Map hmEmpSalaryDetails = new HashMap();
			while (rs.next()) {
				Map hmInner = (Map) hmEmpSalaryDetails.get(rs.getString("emp_id")+"_"+rs.getString("sal_effective_date"));
				if (hmInner == null)
					hmInner = new HashMap();

				double dblTotalAmount = uF.parseToDouble((String) hmInner.get("AMOUNT"));
				if ("E".equalsIgnoreCase(rs.getString("earning_deduction"))) {
					dblTotalAmount += rs.getDouble("amount");
				} else {
					dblTotalAmount -= rs.getDouble("amount");
				}

				hmInner.put("AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalAmount));
				hmInner.put("CURR_ID", rs.getString("currency_id") + "");

				hmEmpSalaryDetails.put(rs.getString("emp_id")+"_"+rs.getString("sal_effective_date"), hmInner);
			}
			rs.close();
			pst.close();

			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);

			String arr[] = getChbxApprove();
			StringBuilder sbApprovedEmployees = new StringBuilder();
			for (int i = 0; arr != null && i < arr.length; i++) {
				String[] strTmp = arr[i].split("_");
				sbApprovedEmployees.append(strTmp[0] + ",");
				pst = con.prepareStatement("update payroll_generation set is_paid = true,paid_by=?,paid_date=? where emp_id = ? and paycycle = ? and paid_from =? and paid_to=? and sal_effective_date=?");
				pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(3, uF.parseToInt(strTmp[0]));
				pst.setInt(4, uF.parseToInt(strPC));
				pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(7, uF.getDateFormat(strTmp[1], DBDATE));
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("update payroll_generation_lta set is_paid = true,paid_by=?,paid_date=? where emp_id = ? and paycycle = ? and paid_from =? and paid_to=? and sal_effective_date=?");
				pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(3, uF.parseToInt(strTmp[0]));
				pst.setInt(4, uF.parseToInt(strPC));
				pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(7, uF.getDateFormat(strTmp[1], DBDATE));
				pst.execute();
				pst.close();

				Map hmInner = (Map) hmEmpSalaryDetails.get(arr[i]);
				if (hmInner == null)
					hmInner = new HashMap();
				String strCurrId = (String) hmInner.get("CURR_ID");

				Map hmInnerCurrencyDetails = (Map) hmCurrencyDetails.get(strCurrId);
				if (hmInnerCurrencyDetails == null)
					hmInnerCurrencyDetails = new HashMap();

				pst = con.prepareStatement("select * from payroll_generation where emp_id = ? and paid_from= ? and paid_to=? and paycycle = ?");
				pst.setInt(1, uF.parseToInt(strTmp[0]));
				pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(strPC));
				rs = pst.executeQuery();
				String strFyStart = null;
				String strFyEnd = null;
				String strMonth = null;
				String strYear = null;
				String strPaidDate = null;
				while (rs.next()) {
					strFyStart = uF.getDateFormat(rs.getString("financial_year_from_date"), DBDATE, DATE_FORMAT);
					strFyEnd = uF.getDateFormat(rs.getString("financial_year_to_date"), DBDATE, DATE_FORMAT);
					strMonth = rs.getString("month");
					strYear = rs.getString("year");
					strPaidDate = uF.getDateFormat(rs.getString("paid_date"), DBDATE, DATE_FORMAT);
				}
				rs.close();
				pst.close();

				PayrollHistoryData historyData = new PayrollHistoryData();
				historyData.session = session;
				historyData.CF = CF;
				historyData.request = request;
				historyData.setStrEmpId(arr[i]);
				historyData.setStrD1(strD1);
				historyData.setStrD2(strD2);
				historyData.setStrPC(strPC);
				historyData.setStrFinancialYearStart(strFyStart);
				historyData.setStrFinancialYearEnd(strFyEnd);
				historyData.setStrPaidMonth(strMonth);
				historyData.setStrPaidYear(strYear);
				historyData.setStrPaidDate(strPaidDate);
				historyData.insertHistoryData(con, uF);

				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_NEW_SALARY_PAID, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrEmpId(arr[i]);
				nF.setStrSalaryAmount(uF.showData((String) hmInnerCurrencyDetails.get("LONG_CURR"), "") + "" + (String) hmInner.get("AMOUNT"));
				nF.setEmailTemplate(true);
				nF.sendNotifications();

				String alertData = "<div style=\"float: left;\"> Payment, Salary has been released by <b>" + CF.getEmpNameMapByEmpId(con, strEmpId)
						+ "</b>. </div>";
				String alertAction = "MyPay.action?pType=WR";
				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(arr[i]);
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
				userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
				userAlerts.setStatus(INSERT_WR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();

				/**
				 * Log Details
				 * */
				String strProcessByName = CF.getEmpNameMapByEmpId(con, strEmpId);
				String strEmpName = CF.getEmpNameMapByEmpId(con, "" + Integer.parseInt(strTmp[0]));
				String strProcessMsg = uF.showData(strProcessByName, "") + " has paid salary of " + uF.showData(strEmpName, "") + " on " + ""
						+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
						+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
				LogDetails logDetails = new LogDetails();
				logDetails.session = session;
				logDetails.CF = CF;
				logDetails.request = request;
				logDetails.setProcessId(Integer.parseInt(strTmp[0]));
				logDetails.setProcessType(L_PAID_SALARY);
				logDetails.setProcessActivity(L_ADD);
				logDetails.setProcessMsg(strProcessMsg);
				logDetails.setProcessStep(0);
				logDetails.setProcessBy(uF.parseToInt(strEmpId));
				logDetails.insertLog(con, uF);

			}

			for (int i = 0; arr != null && i < arr.length; i++) {
				String[] strTmp = arr[i].split("_");
				pst = con.prepareStatement("select * from  payroll_generation where emp_id = ? and paycycle = ? and salary_head_id = ? and is_paid= true and paid_from =? and paid_to=? ");
				pst.setInt(1, uF.parseToInt(strTmp[0]));
				pst.setInt(2, uF.parseToInt(strPC));
				pst.setInt(3, AREARS);
				pst.setDate(4, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strD2, DATE_FORMAT));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					double dblArearAmount = rs.getDouble("amount");

					Map hmArearMap = (Map) hmArearAmountMap.get(strTmp[0]);
					if (hmArearMap == null) hmArearMap = new HashMap();

					int nArearId = uF.parseToInt((String) hmArearMap.get("AREAR_ID"));
					double dblTotalAmount = uF.parseToDouble((String) hmArearMap.get("TOTAL_AMOUNT"));
					double dblPaidAmount = uF.parseToDouble((String) hmArearMap.get("AMOUNT_PAID"));
					double dblBalanceAmount = uF.parseToDouble((String) hmArearMap.get("AMOUNT_BALANCE"));

					dblBalanceAmount = dblTotalAmount - dblPaidAmount - dblArearAmount;
					dblPaidAmount += dblArearAmount;

					pst1 = con.prepareStatement("update arear_details set total_amount_paid=?, arear_amount_balance=?, is_paid=? where arear_id=?");
					pst1.setDouble(1, dblPaidAmount);
					pst1.setDouble(2, dblBalanceAmount);
					if (dblBalanceAmount <= 1) {
						pst1.setBoolean(3, true);
					} else {
						pst1.setBoolean(3, false);
					}
					pst1.setInt(4, nArearId);
					pst1.execute();
					pst1.close();

				}
				rs.close();
				pst.close();
			}

			sbApprovedEmployees.replace(sbApprovedEmployees.length() - 1, sbApprovedEmployees.length(), "");

			Map<String, String> hmStates = CF.getStateMap(con);
			Map<String, String> hmCountry = CF.getCountryMap(con);

			pst = con.prepareStatement("select bd.bank_account_no, bd.branch_id, bd1.bank_name,bd.bank_branch, bd.branch_code, bd.bank_address, "
					+ "bd.bank_city, bd.bank_pincode, bd.bank_state_id, bd.bank_country_id from bank_details bd1, branch_details bd "
					+ "where bd1.bank_id = bd.bank_id");
			rs = pst.executeQuery();
//			System.out.println("pst====>"+pst);
			String strBankCode = null;
			String strBankName = null;
			String strBankAddress = null;
			Map<String, String> hmBankBranch = new HashMap<String, String>();
			while (rs.next()) {
				if (rs.getInt("branch_id") == uF.parseToInt(getBankAccount())) {
					strBankCode = rs.getString("branch_code");
					strBankName = rs.getString("bank_name");
					strBankAddress = rs.getString("bank_address") + "<br/>" + rs.getString("bank_city") + " - " + rs.getString("bank_pincode") + "<br/>"
							+ uF.showData(hmStates.get(rs.getString("bank_state_id")), "") + ", "
							+ uF.showData(hmCountry.get(rs.getString("bank_country_id")), "");
				}

				hmBankBranch.put(rs.getString("branch_id"), rs.getString("bank_branch") + "[" + rs.getString("branch_code") + "]");
			}
			rs.close();
			pst.close();

			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Payment Held");

			List<DataStyle> header = new ArrayList<DataStyle>();
			header.add(new DataStyle("Process Date " + uF.showData(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Txn Type", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//1
			header.add(new DataStyle("Account Number", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//2
			header.add(new DataStyle("Branch Code", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//3
			header.add(new DataStyle("Txn Code", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//4
			header.add(new DataStyle("Txn Date", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//5
			header.add(new DataStyle("Dr/Cr", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//6
			header.add(new DataStyle("Value Dt", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//7
		
			header.add(new DataStyle("Txn CCY", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//8
			header.add(new DataStyle("NET PAY Amt LCY", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//9
			header.add(new DataStyle("NET PAY Amt LCY", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//10
			header.add(new DataStyle("Rate Con", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//11
			header.add(new DataStyle("Ref No.", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//12
			header.add(new DataStyle("Ref Doc No.", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//13
			header.add(new DataStyle("Transaction Description", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//14
			header.add(new DataStyle("Benef IC", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//15
			header.add(new DataStyle("Benef Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//16
			
						
			header.add(new DataStyle("Benef Add1", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//17
			header.add(new DataStyle("Benef Add2", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//18
			header.add(new DataStyle("Benef Add3", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//19
			header.add(new DataStyle("Benef City", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//20
			header.add(new DataStyle("Benef State", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//21
			header.add(new DataStyle("Benef Country", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//22
			header.add(new DataStyle("Benef Zip", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//23
			header.add(new DataStyle("Option", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//24
			
			header.add(new DataStyle("Issuer Code", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//25
			header.add(new DataStyle("Payable At", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//26
			header.add(new DataStyle("Fig FDT", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//27
			header.add(new DataStyle("MIS Account Number", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//28
			
			List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();
			
//			System.out.println("strPriSalaryHead====>"+getPrimarySalaryHead());
//			System.out.println("getSecondarySalaryHead====>"+getSecondarySalaryHead());
//			System.out.println("getIsBifurcatePayOut====>"+getIsBifurcatePayOut());
			if (getIsBifurcatePayOut() && ((getPrimarySalaryHead() != null && !getPrimarySalaryHead().trim().equals("") && !getPrimarySalaryHead().trim().equalsIgnoreCase("NULL")) || (getSecondarySalaryHead() != null
					&& !getSecondarySalaryHead().trim().equals("") && !getSecondarySalaryHead().trim().equalsIgnoreCase("NULL")))) {
				if (getPrimarySalaryHead() != null && !getPrimarySalaryHead().trim().equals("") && !getPrimarySalaryHead().trim().equalsIgnoreCase("NULL")) {
					// System.out.println("strPriSalaryHead====>"+getPrimarySalaryHead());
					pst = con.prepareStatement("select epd.*, net_amount, month, year from employee_personal_details epd,(select e_amount - (case when d_amount is null "
						+ "then 0 else d_amount END) as net_amount, a.emp_id, month, year from (select sum(amount) as e_amount, emp_id, month, year "
						+ "from payroll_generation where earning_deduction = 'E'  and is_paid = true  and payment_mode=1 and paycycle = ? and paid_from =? and paid_to=? "
						+ "and salary_head_id in (" + getPrimarySalaryHead()+ ") group by emp_id, month, year ) a left join (select sum(amount) as d_amount, "
						+ "emp_id from payroll_generation where earning_deduction = 'D' and is_paid = true and payment_mode=1 and paycycle = ? and paid_from =? and paid_to=? "
						+ "and salary_head_id in (" + getPrimarySalaryHead() + ") group by emp_id ) b on a.emp_id = b.emp_id) ab where "
						+ "ab.emp_id = epd.emp_per_id and epd.emp_per_id in (" + sbApprovedEmployees.toString() + ") order by epd.emp_fname");
					pst.setInt(1, uF.parseToInt(strPC));
					pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
					pst.setInt(4, uF.parseToInt(strPC));
					pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
//					 System.out.println("pst====>"+pst);
					rs = pst.executeQuery();
					double dblAmount = 0;
					double dblTotalAmount = 0;
					int nMonth = 0;
					int nYear = 0;
					int nCount = 0;
					StringBuilder sbEmpAmountBankDetails = new StringBuilder();
					StringBuilder sbEmpAmountBankDetailsExcel = new StringBuilder();
					while (rs.next()) {
						dblAmount = uF.parseToDouble(rs.getString("net_amount"));
						nMonth = uF.parseToInt(rs.getString("month"));
						nYear = uF.parseToInt(rs.getString("year"));

						dblTotalAmount += dblAmount;

						String strBankAccNo = uF.showData(rs.getString("emp_bank_acct_nbr"), "");
						String strBankBranch = uF.showData(hmBankBranch.get(rs.getString("emp_bank_name")), "");

						sbEmpAmountBankDetails.append("<tr>");
						sbEmpAmountBankDetails.append("<td><font size=\"1\">" + ++nCount + ".</font></td>");
						
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}

						sbEmpAmountBankDetailsExcel.append(":_:" + nCount +"::"+ uF.showData(rs.getString("emp_fname"), "") +strEmpMName+ " "
							+ uF.showData(rs.getString("emp_lname"), "") +"::"+ strBankAccNo +"::"+ strBankBranch +"::" + uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount) );
						sbEmpAmountBankDetails.append("<td><font size=\"1\">" + uF.showData(rs.getString("emp_fname"), "") +strEmpMName+ " "
								+ uF.showData(rs.getString("emp_lname"), "") + "</font></td>");
						sbEmpAmountBankDetails.append("<td><font size=\"1\">" + strBankAccNo + "</font></td>");
						sbEmpAmountBankDetails.append("<td><font size=\"1\">" + strBankBranch + "</font></td>");
						sbEmpAmountBankDetails.append("<td align=\"right\"><font size=\"1\">" + uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount) + "</font></td>");
						sbEmpAmountBankDetails.append("</tr>");
						
						List<DataStyle> innerList = new ArrayList<DataStyle>();

						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//1
						innerList.add(new DataStyle(strBankAccNo, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//2
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//3
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//4
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//5
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//6
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//7
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//8
						innerList.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//9
						innerList.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//10
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//11
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//12
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//13
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//14
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//15
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//16
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//17
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//18
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//19
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//20
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//21
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//22
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//23
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//24
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//25
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//26
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//27
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//28
						
						reportData.add(innerList);
					}
					rs.close();
					pst.close();

					String strContent = null;
					String strName = null;

					Map<String, String> hmActivityNode = CF.getActivityNode(con);
					if (hmActivityNode == null)
						hmActivityNode = new HashMap<String, String>();

					int nTriggerNode = uF.parseToInt(hmActivityNode.get("" + ACTIVITY_BANK_ORDER_ID));

					if (nMonth > 0) {
						Map<String, String> hmOrg = CF.getOrgDetails(con, uF, getF_org());

						pst = con.prepareStatement("select * from document_comm_details where document_text like '%[" + strBankCode
								+ "]%' and trigger_nodes like '%," + nTriggerNode + ",%' and status=1 and org_id=? order by document_id desc limit 1");
						pst.setInt(1, uF.parseToInt(getF_org()));
						// System.out.println("pst=====>"+pst);
						rs = pst.executeQuery();
						while (rs.next()) {
							strContent = rs.getString("document_text");
						}
						rs.close();
						pst.close();

						if (strContent != null && strContent.indexOf("[" + strBankCode + "]") >= 0) {
							strContent = strContent.replace("[" + strBankCode + "]", strBankName + "<br/>" + strBankAddress);
						}

						if (strContent != null && strContent.indexOf(DATE) >= 0) {
							strContent = strContent.replace(DATE,
									uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, CF.getStrReportDateFormat()));
						}

						if (strContent != null && strContent.indexOf(PAYROLL_AMOUNT) >= 0) {
							strContent = strContent.replace(PAYROLL_AMOUNT, uF.formatIntoTwoDecimal(dblTotalAmount));
						}

						if (strContent != null && strContent.indexOf(PAYROLL_AMOUNT_WORDS) >= 0) {
							String digitTotal = "";
							String strTotalAmt = "" + dblTotalAmount;
							if (strTotalAmt.contains(".")) {
								strTotalAmt = strTotalAmt.replace(".", ",");
								String[] temp = strTotalAmt.split(",");
								digitTotal = uF.digitsToWords(uF.parseToInt(temp[0]));
								if (uF.parseToInt(temp[1]) > 0) {
									int pamt = 0;
									if (temp[1].length() == 1) {
										pamt = uF.parseToInt(temp[1] + "0");
									} else {
										pamt = uF.parseToInt(temp[1]);
									}
									digitTotal += " and " + uF.digitsToWords(pamt) + " paise";
								}
							} else {
								int totalAmt1 = (int) dblTotalAmount;
								digitTotal = uF.digitsToWords(totalAmt1);
							}
							strContent = strContent.replace(PAYROLL_AMOUNT_WORDS, digitTotal);
						}

						if (strContent != null && strContent.indexOf(PAY_MONTH) >= 0) {
							strContent = strContent.replace(PAY_MONTH, uF.getMonth(nMonth));
						}

						if (strContent != null && strContent.indexOf(PAY_YEAR) >= 0) {
							strContent = strContent.replace(PAY_YEAR, "" + nYear);
						}

						if (strContent != null && strContent.indexOf(LEGAL_ENTITY_NAME) >= 0) {
							strContent = strContent.replace(LEGAL_ENTITY_NAME, uF.showData(hmOrg.get("ORG_NAME"), ""));
						}

					}

					int nMaxStatementId = 0;
					if (strContent != null && nMonth > 0) {
						StringBuilder sbEmpBankDetails = new StringBuilder();
						StringBuilder sbEmpBankDetailsExcel = new StringBuilder();
						sbEmpBankDetailsExcel.append("Bank Statement::Sr. No.::Name::Account No::Branch::Amount");
						sbEmpBankDetails.append("<table width=\"100%\">");
						sbEmpBankDetails.append("<tr>");
						sbEmpBankDetails.append("<td width=\"20\"><b>Sr. No.</b></td>");
						sbEmpBankDetails.append("<td><b>Name</b></td>");
						sbEmpBankDetails.append("<td><b>Pan No</b></td>");
						sbEmpBankDetails.append("<td><b>Account No</b></td>");
						sbEmpBankDetails.append("<td><b>Branch</b></td>");
						sbEmpBankDetails.append("<td align=\"right\"><b>Amount</b></td>");
						sbEmpBankDetails.append("</tr>");

						sbEmpBankDetails.append(sbEmpAmountBankDetails);
						sbEmpBankDetailsExcel.append(sbEmpAmountBankDetailsExcel);
						
						sbEmpBankDetails.append("<tr>");
						sbEmpBankDetails.append("<td colspan=\"5\">&nbsp;</td>");
						sbEmpBankDetails.append("</tr>");

						sbEmpBankDetailsExcel.append(":_: :: :: ::TOTAL::"+ uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalAmount));
						sbEmpBankDetails.append("<tr>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td><b>TOTAL</b></td>");
						sbEmpBankDetails.append("<td align=\"right\"><b>" + uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalAmount) + "</b></td>");
						sbEmpBankDetails.append("</tr>");

						sbEmpBankDetails.append("</table>");

						strName = "BankStatement_" + nMonth + "_" + nYear;

						pst = con.prepareStatement("insert into payroll_bank_statement(statement_name, statement_body, generated_date, "
							+ "generated_by, payroll_amount, bank_pay_type, statement_body_excel, bank_uploader_excel) values (?,?,?,?, ?,?,?,?)");
						pst.setString(1, strName);
						pst.setString(2, strContent + "" + sbEmpBankDetails.toString());
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt((String) session.getAttribute(EMPID)));
						pst.setDouble(5, uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblTotalAmount))));
						pst.setInt(6, 1);
						pst.setString(7, sbEmpBankDetailsExcel.toString());
						pst.setString(8, "");
						pst.execute();
						pst.close();

						pst = con.prepareStatement("select max(statement_id) as statement_id from payroll_bank_statement");
						rs = pst.executeQuery();
						while (rs.next()) {
							nMaxStatementId = rs.getInt("statement_id");
						}
						rs.close();
						pst.close();
						
						ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
						sheetDesign.getExcelSheetDesignDataPay(workbook, sheet, header, reportData);
						
						String directory = CF.getStrDocSaveLocation()+I_BANK_EXCEL_STMT+"/"+nMaxStatementId; 
						FileUtils.forceMkdir(new File(directory));
						
						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						workbook.write(buffer);
					
						byte[] bytes = buffer.toByteArray();
						String fileName = directory+"/"+strName+".xls";
						File f = new File(fileName);
//						File f = File.("tmp", ".xls", new File(directory));
						FileOutputStream fileOuputStream = new FileOutputStream(f); 
						fileOuputStream.write(bytes);
						
						pst = con.prepareStatement("update payroll_bank_statement set bank_excel_sheet =? where statement_id = ?");
						pst.setString(1, strName+".xls");
						pst.setInt(2, nMaxStatementId);
						pst.executeUpdate();
						pst.close();
					}
					pst = con.prepareStatement("update payroll_generation set statement_id=?, bank_pay_type=? where emp_id in (" + sbApprovedEmployees.toString() + ") "
							+ "and salary_head_id in (" + getPrimarySalaryHead() + ") and paycycle=? and month=? and year=? and paid_from =? and paid_to=? ");
					pst.setInt(1, nMaxStatementId);
					pst.setInt(2, 1);
					pst.setInt(3, uF.parseToInt(strPC));
					pst.setInt(4, nMonth);
					pst.setInt(5, nYear);
					pst.setDate(6, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(7, uF.getDateFormat(strD2, DATE_FORMAT));
//					System.out.println("pst ===========>> " + pst);
					pst.execute();
					pst.close();
				}
				if (getSecondarySalaryHead() != null && !getSecondarySalaryHead().trim().equals("")
						&& !getSecondarySalaryHead().trim().equalsIgnoreCase("NULL")) {
					// System.out.println("strSecondSalaryHead====>"+getSecondarySalaryHead());
					pst = con.prepareStatement("select epd.*, net_amount, month, year from employee_personal_details epd,(select e_amount - (case when d_amount is null "
						+ "then 0 else d_amount END) as net_amount, a.emp_id, month, year from (select sum(amount) as e_amount, emp_id, month, year "
						+ "from payroll_generation where earning_deduction = 'E'  and is_paid = true  and payment_mode=1 and paycycle = ? and paid_from =? and paid_to=? "
						+ "and salary_head_id in (" + getSecondarySalaryHead()+ ") group by emp_id, month, year ) a left join (select sum(amount) as d_amount, "
						+ "emp_id from payroll_generation where earning_deduction = 'D' and is_paid = true and payment_mode=1 and paycycle = ? and paid_from =? and paid_to=? "
						+ "and salary_head_id in (" + getSecondarySalaryHead() + ") group by emp_id ) b on a.emp_id = b.emp_id) ab where "
						+ "ab.emp_id = epd.emp_per_id and epd.emp_per_id in (" + sbApprovedEmployees.toString() + ") order by epd.emp_fname");
					pst.setInt(1, uF.parseToInt(strPC));
					pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
					pst.setInt(4, uF.parseToInt(strPC));
					pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
//					 System.out.println("pst====>"+pst);
					rs = pst.executeQuery();
					double dblAmount = 0;
					double dblTotalAmount = 0;
					int nMonth = 0;
					int nYear = 0;
					int nCount = 0;
					StringBuilder sbEmpAmountBankDetails = new StringBuilder();
					while (rs.next()) {
						dblAmount = uF.parseToDouble(rs.getString("net_amount"));
						nMonth = uF.parseToInt(rs.getString("month"));
						nYear = uF.parseToInt(rs.getString("year"));

						dblTotalAmount += dblAmount;

						String strBankAccNo = uF.showData(rs.getString("emp_bank_acct_nbr_2"), "");
						String strBankBranch = uF.showData(hmBankBranch.get(rs.getString("emp_bank_name2")), "");

						sbEmpAmountBankDetails.append("<tr>");
						sbEmpAmountBankDetails.append("<td><font size=\"1\">" + ++nCount + ".</font></td>");
						
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						
						sbEmpAmountBankDetails.append("<td><font size=\"1\">" + uF.showData(rs.getString("emp_fname"), "") +strEmpMName+ " "
								+ uF.showData(rs.getString("emp_lname"), "") + "</font></td>");
					//===start parvez date: 29-12-2022===	
						sbEmpAmountBankDetails.append("<td><font size=\"1\">" + uF.showData(rs.getString("emp_pan_no"), "") + "</font></td>");
					//===end parvez date: 29-12-2022===	
						sbEmpAmountBankDetails.append("<td><font size=\"1\">" + strBankAccNo + "</font></td>");
						sbEmpAmountBankDetails.append("<td><font size=\"1\">" + strBankBranch + "</font></td>");
						sbEmpAmountBankDetails.append("<td align=\"right\"><font size=\"1\">" + uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount) + "</font></td>");
						sbEmpAmountBankDetails.append("</tr>");
						
						List<DataStyle> innerList = new ArrayList<DataStyle>();

						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//1
						innerList.add(new DataStyle(strBankAccNo, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//2
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//3
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//4
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//5
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//6
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//7
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//8
						innerList.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//9
						innerList.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//10
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//11
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//12
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//13
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//14
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//15
						innerList.add(new DataStyle("" , Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//16
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//17
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//18
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//19
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//20
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//21
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//22
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//23
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//24
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//25
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//26
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//27
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//28
						
						reportData.add(innerList);
					}
					rs.close();
					pst.close();

					String strContent = null;
					String strName = null;

					Map<String, String> hmActivityNode = CF.getActivityNode(con);
					if (hmActivityNode == null)
						hmActivityNode = new HashMap<String, String>();

					int nTriggerNode = uF.parseToInt(hmActivityNode.get("" + ACTIVITY_BANK_ORDER_ID));

					if (nMonth > 0) {
						Map<String, String> hmOrg = CF.getOrgDetails(con, uF, getF_org());

						pst = con.prepareStatement("select * from document_comm_details where document_text like '%[" + strBankCode
								+ "]%' and trigger_nodes like '%," + nTriggerNode + ",%' and status=1 and org_id=? order by document_id desc limit 1");
						pst.setInt(1, uF.parseToInt(getF_org()));
						// System.out.println("pst=====>"+pst);
						rs = pst.executeQuery();
						while (rs.next()) {
							strContent = rs.getString("document_text");
						}
						rs.close();
						pst.close();

						if (strContent != null && strContent.indexOf("[" + strBankCode + "]") >= 0) {
							strContent = strContent.replace("[" + strBankCode + "]", strBankName + "<br/>" + strBankAddress);
						}

						if (strContent != null && strContent.indexOf(DATE) >= 0) {
							strContent = strContent.replace(DATE,
									uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, CF.getStrReportDateFormat()));
						}

						if (strContent != null && strContent.indexOf(PAYROLL_AMOUNT) >= 0) {
							strContent = strContent.replace(PAYROLL_AMOUNT, uF.formatIntoTwoDecimal(dblTotalAmount));
						}

						if (strContent != null && strContent.indexOf(PAYROLL_AMOUNT_WORDS) >= 0) {
							String digitTotal = "";
							String strTotalAmt = "" + dblTotalAmount;
							if (strTotalAmt.contains(".")) {
								strTotalAmt = strTotalAmt.replace(".", ",");
								String[] temp = strTotalAmt.split(",");
								digitTotal = uF.digitsToWords(uF.parseToInt(temp[0]));
								if (uF.parseToInt(temp[1]) > 0) {
									int pamt = 0;
									if (temp[1].length() == 1) {
										pamt = uF.parseToInt(temp[1] + "0");
									} else {
										pamt = uF.parseToInt(temp[1]);
									}
									digitTotal += " and " + uF.digitsToWords(pamt) + " paise";
								}
							} else {
								int totalAmt1 = (int) dblTotalAmount;
								digitTotal = uF.digitsToWords(totalAmt1);
							}
							strContent = strContent.replace(PAYROLL_AMOUNT_WORDS, digitTotal);
						}

						if (strContent != null && strContent.indexOf(PAY_MONTH) >= 0) {
							strContent = strContent.replace(PAY_MONTH, uF.getMonth(nMonth));
						}

						if (strContent != null && strContent.indexOf(PAY_YEAR) >= 0) {
							strContent = strContent.replace(PAY_YEAR, "" + nYear);
						}

						if (strContent != null && strContent.indexOf(LEGAL_ENTITY_NAME) >= 0) {
							strContent = strContent.replace(LEGAL_ENTITY_NAME, uF.showData(hmOrg.get("ORG_NAME"), ""));
						}

					}

					int nMaxStatementId = 0;
					if (strContent != null && nMonth > 0) {
						StringBuilder sbEmpBankDetails = new StringBuilder();
						sbEmpBankDetails.append("<table width=\"100%\">");
						sbEmpBankDetails.append("<tr>");
						sbEmpBankDetails.append("<td width=\"20\"><b>Sr. No.</b></td>");
						sbEmpBankDetails.append("<td><b>Name</b></td>");
					//===start parvez date: 29-12-2022===
						sbEmpBankDetails.append("<td><b>Pan No</b></td>");
					//===end parvez date: 29-12-2022===	
						sbEmpBankDetails.append("<td><b>Account No</b></td>");
						sbEmpBankDetails.append("<td><b>Branch</b></td>");
						sbEmpBankDetails.append("<td align=\"right\"><b>Amount</b></td>");
						sbEmpBankDetails.append("</tr>");

						sbEmpBankDetails.append(sbEmpAmountBankDetails);

						sbEmpBankDetails.append("<tr>");
						sbEmpBankDetails.append("<td colspan=\"5\">&nbsp;</td>");
						sbEmpBankDetails.append("</tr>");

						sbEmpBankDetails.append("<tr>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td><b>TOTAL</b></td>");
						sbEmpBankDetails.append("<td align=\"right\"><b>" + uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalAmount) + "</b></td>");
						sbEmpBankDetails.append("</tr>");

						sbEmpBankDetails.append("</table>");

						strName = "BankStatement_" + nMonth + "_" + nYear;

						pst = con.prepareStatement("insert into payroll_bank_statement(statement_name, statement_body, generated_date, "
								+ "generated_by, payroll_amount,bank_pay_type) values (?,?,?,?, ?,?)");
						pst.setString(1, strName);
						pst.setString(2, strContent + "" + sbEmpBankDetails.toString());
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt((String) session.getAttribute(EMPID)));
						pst.setDouble(5, uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblTotalAmount))));
						pst.setInt(6, 2);
						pst.execute();
						pst.close();

						pst = con.prepareStatement("select max(statement_id) as statement_id from payroll_bank_statement");
						rs = pst.executeQuery();
						while (rs.next()) {
							nMaxStatementId = rs.getInt("statement_id");
						}
						rs.close();
						pst.close();
						
						ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
						sheetDesign.getExcelSheetDesignDataPay(workbook, sheet, header, reportData);
						
						String directory = CF.getStrDocSaveLocation()+I_BANK_EXCEL_STMT+"/"+nMaxStatementId; 
						FileUtils.forceMkdir(new File(directory));
						
						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						workbook.write(buffer);
					
						byte[] bytes = buffer.toByteArray();
						String fileName = directory+"/"+strName+".xls";
						File f = new File(fileName);
//						File f = File.("tmp", ".xls", new File(directory));
						FileOutputStream fileOuputStream = new FileOutputStream(f); 
						fileOuputStream.write(bytes);
						
						pst = con.prepareStatement("update payroll_bank_statement set bank_excel_sheet =? where statement_id = ?");
						pst.setString(1, strName+".xls");
						pst.setInt(2, nMaxStatementId);
						pst.executeUpdate();
						pst.close();

					}
					pst = con.prepareStatement("update payroll_generation set statement_id=?, bank_pay_type=? where emp_id in (" + sbApprovedEmployees.toString() + ") "
							+ "and salary_head_id in (" + getSecondarySalaryHead() + ") and paycycle=? and month=? and year=? and paid_from =? and paid_to=? ");
					pst.setInt(1, nMaxStatementId);
					pst.setInt(2, 2);
					pst.setInt(3, uF.parseToInt(strPC));
					pst.setInt(4, nMonth);
					pst.setInt(5, nYear);
					pst.setDate(6, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(7, uF.getDateFormat(strD2, DATE_FORMAT));
//					System.out.println("pst 2 ================>> " + pst);
					pst.execute();
					pst.close();
				}
			} else {
//				System.out.println("==== IN ELSE =======>> ");
				pst = con.prepareStatement("select epd.*, net_amount, month, year from employee_personal_details epd, ( select e_amount - (case when d_amount is null then 0 else d_amount END) as net_amount, "
					+ "a.emp_id, month, year from (select sum(amount) as e_amount, emp_id, month, year from payroll_generation where "
					+ "earning_deduction = 'E'  and is_paid = true  and payment_mode=1 and paycycle = ? and paid_from =? and paid_to=? group by emp_id, month, year) a "
					+ "left join (select sum(amount) as d_amount, emp_id from payroll_generation where earning_deduction = 'D' "
					+ "and is_paid = true and payment_mode=1 and paycycle = ?  and paid_from =? and paid_to=? group by emp_id ) b on a.emp_id = b.emp_id) ab "
					+ "where ab.emp_id = epd.emp_per_id and epd.emp_per_id in (" + sbApprovedEmployees.toString() + ") order by epd.emp_fname ");
				pst.setInt(1, uF.parseToInt(strPC));
				pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(strPC));
				pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
//				System.out.println("pst in else ===>> " + pst);
				rs = pst.executeQuery();
				double dblAmount = 0;
				double dblTotalAmount = 0;
				int nMonth = 0;
				int nYear = 0;
				int nCount = 0;
				StringBuilder sbEmpAmountBankDetails = new StringBuilder();
				int nBankPayType = 0;
				while (rs.next()) {
					dblAmount = uF.parseToDouble(rs.getString("net_amount"));
					nMonth = uF.parseToInt(rs.getString("month"));
					nYear = uF.parseToInt(rs.getString("year"));

					dblTotalAmount += dblAmount;

					String strBankAccNo = uF.showData(rs.getString("emp_bank_acct_nbr"), "");
					String strBankBranch = uF.showData(hmBankBranch.get(rs.getString("emp_bank_name")), "");
					nBankPayType = 1;
					if (uF.parseToInt(getBankAccountType()) == 2) {
						strBankAccNo = uF.showData(rs.getString("emp_bank_acct_nbr_2"), "");
						strBankBranch = uF.showData(hmBankBranch.get(rs.getString("emp_bank_name2")), "");
						nBankPayType = 2;
					}

					sbEmpAmountBankDetails.append("<tr>");
					sbEmpAmountBankDetails.append("<td><font size=\"1\">" + ++nCount + ".</font></td>");
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					sbEmpAmountBankDetails.append("<td><font size=\"1\">" + uF.showData(rs.getString("emp_fname"), "") +strEmpMName+ " "
							+ uF.showData(rs.getString("emp_lname"), "") + "</font></td>");
				//===start parvez date: 29-12-2022===	
					sbEmpAmountBankDetails.append("<td><font size=\"1\">" + uF.showData(rs.getString("emp_pan_no"), "") + "</font></td>");
				//===end parvez date: 29-12-2022===	
					sbEmpAmountBankDetails.append("<td><font size=\"1\">" + strBankAccNo + "</font></td>");
					sbEmpAmountBankDetails.append("<td><font size=\"1\">" + strBankBranch + "</font></td>");
					sbEmpAmountBankDetails.append("<td align=\"right\"><font size=\"1\">" + uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount) + "</font></td>");
					sbEmpAmountBankDetails.append("</tr>");
					
					List<DataStyle> innerList = new ArrayList<DataStyle>();

					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//1
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//1
					innerList.add(new DataStyle(strBankAccNo, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//2
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//3
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//4
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//5
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//6
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//7
					
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//8
					innerList.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//9
					innerList.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//10
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//11
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//12
					
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//13
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//14
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//15
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//16
					
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//17
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//18
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//19
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//20
					
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//21
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//22
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//23
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//24
					
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//25
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//26
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//27
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//28
					
					reportData.add(innerList);
				}
				rs.close();
				pst.close();

				String strContent = null;
				String strName = null;
				Map<String, String> hmActivityNode = CF.getActivityNode(con);
				if (hmActivityNode == null)
					hmActivityNode = new HashMap<String, String>();
				int nTriggerNode = uF.parseToInt(hmActivityNode.get("" + ACTIVITY_BANK_ORDER_ID));
				if (nMonth > 0) {
					Map<String, String> hmOrg = CF.getOrgDetails(con, uF, getF_org());

					pst = con.prepareStatement("select * from document_comm_details where document_text like '%[" + strBankCode
							+ "]%' and trigger_nodes like '%," + nTriggerNode + ",%' and status=1 and org_id=? order by document_id desc limit 1");
					pst.setInt(1, uF.parseToInt(getF_org()));
					// System.out.println("pst=====>"+pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						strContent = rs.getString("document_text");
					}
					rs.close();
					pst.close();

					if (strContent != null && strContent.indexOf("[" + strBankCode + "]") >= 0) {
						strContent = strContent.replace("[" + strBankCode + "]", strBankName + "<br/>" + strBankAddress);
					}

					if (strContent != null && strContent.indexOf(DATE) >= 0) {
						strContent = strContent.replace(DATE,
								uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, CF.getStrReportDateFormat()));
					}

					if (strContent != null && strContent.indexOf(PAYROLL_AMOUNT) >= 0) {
						strContent = strContent.replace(PAYROLL_AMOUNT, uF.formatIntoTwoDecimal(dblTotalAmount));
					}

					if (strContent != null && strContent.indexOf(PAYROLL_AMOUNT_WORDS) >= 0) {
						// strContent = strContent.replace(PAYROLL_AMOUNT_WORDS,
						// uF.digitsToWords((int)dblTotalAmount));
						String digitTotal = "";
						String strTotalAmt = "" + dblTotalAmount;
						if (strTotalAmt.contains(".")) {
							strTotalAmt = strTotalAmt.replace(".", ",");
							String[] temp = strTotalAmt.split(",");
							digitTotal = uF.digitsToWords(uF.parseToInt(temp[0]));
							if (uF.parseToInt(temp[1]) > 0) {
								int pamt = 0;
								if (temp[1].length() == 1) {
									pamt = uF.parseToInt(temp[1] + "0");
								} else {
									pamt = uF.parseToInt(temp[1]);
								}
								digitTotal += " and " + uF.digitsToWords(pamt) + " paise";
							}
						} else {
							int totalAmt1 = (int) dblTotalAmount;
							digitTotal = uF.digitsToWords(totalAmt1);
						}
						strContent = strContent.replace(PAYROLL_AMOUNT_WORDS, digitTotal);
					}

					if (strContent != null && strContent.indexOf(PAY_MONTH) >= 0) {
						strContent = strContent.replace(PAY_MONTH, uF.getMonth(nMonth));
					}

					if (strContent != null && strContent.indexOf(PAY_YEAR) >= 0) {
						strContent = strContent.replace(PAY_YEAR, "" + nYear);
					}

					if (strContent != null && strContent.indexOf(LEGAL_ENTITY_NAME) >= 0) {
						strContent = strContent.replace(LEGAL_ENTITY_NAME, uF.showData(hmOrg.get("ORG_NAME"), ""));
					}
				}

				int nMaxStatementId = 0;
				if (strContent != null && nMonth > 0) {
					StringBuilder sbEmpBankDetails = new StringBuilder();
					sbEmpBankDetails.append("<table width=\"100%\">");
					sbEmpBankDetails.append("<tr>");
					sbEmpBankDetails.append("<td width=\"20\"><b>Sr. No.</b></td>");
					sbEmpBankDetails.append("<td><b>Name</b></td>");
				//===start parvez date: 29-12-2022===
					sbEmpBankDetails.append("<td><b>Pan No</b></td>");
				//===end parvez date: 29-12-2022===	
					sbEmpBankDetails.append("<td><b>Account No</b></td>");
					sbEmpBankDetails.append("<td><b>Branch</b></td>");
					sbEmpBankDetails.append("<td align=\"right\"><b>Amount</b></td>");
					sbEmpBankDetails.append("</tr>");

					sbEmpBankDetails.append(sbEmpAmountBankDetails);

					sbEmpBankDetails.append("<tr>");
					// sbEmpBankDetails.append("<td colspan=\"5\"><hr width=\"100%\"/></td>");
					sbEmpBankDetails.append("<td colspan=\"5\">&nbsp;</td>");
					sbEmpBankDetails.append("</tr>");
					sbEmpBankDetails.append("<tr>");
					sbEmpBankDetails.append("<td>&nbsp;</td>");
					sbEmpBankDetails.append("<td>&nbsp;</td>");
					sbEmpBankDetails.append("<td>&nbsp;</td>");
					sbEmpBankDetails.append("<td><b>TOTAL</b></td>");
					sbEmpBankDetails.append("<td align=\"right\"><b>" + uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalAmount) + "</b></td>");
					sbEmpBankDetails.append("</tr>");
					sbEmpBankDetails.append("</table>");

					strName = "BankStatement_" + nMonth + "_" + nYear;

					pst = con.prepareStatement("insert into payroll_bank_statement(statement_name, statement_body, generated_date, "
							+ "generated_by, payroll_amount,bank_pay_type) values (?,?,?,?, ?,?)");
					pst.setString(1, strName);
					pst.setString(2, strContent + "" + sbEmpBankDetails.toString());
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(4, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setDouble(5, uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblTotalAmount))));
					pst.setInt(6, nBankPayType);
					pst.execute();
					pst.close();

					pst = con.prepareStatement("select max(statement_id) as statement_id from payroll_bank_statement");
					rs = pst.executeQuery();
					while (rs.next()) {
						nMaxStatementId = rs.getInt("statement_id");
					}
					rs.close();
					pst.close();
					
					ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
					sheetDesign.getExcelSheetDesignDataPay(workbook, sheet, header, reportData);
					
					String directory = CF.getStrDocSaveLocation()+I_BANK_EXCEL_STMT+"/"+nMaxStatementId; 
					FileUtils.forceMkdir(new File(directory));
					
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					workbook.write(buffer);
				
					byte[] bytes = buffer.toByteArray();
					String fileName = directory+"/"+strName+".xls";
					File f = new File(fileName);
//					File f = File.("tmp", ".xls", new File(directory));
					FileOutputStream fileOuputStream = new FileOutputStream(f); 
					fileOuputStream.write(bytes);
					
					pst = con.prepareStatement("update payroll_bank_statement set bank_excel_sheet =? where statement_id = ?");
					pst.setString(1, strName+".xls");
					pst.setInt(2, nMaxStatementId);
					pst.executeUpdate();
					pst.close();

				}
				pst = con.prepareStatement("update payroll_generation set statement_id=?, bank_pay_type=? where emp_id in (" + sbApprovedEmployees.toString()
					+ ") and paycycle=? and month=? and year=? and paid_from =? and paid_to=? ");
				pst.setInt(1, nMaxStatementId);
				pst.setInt(2, uF.parseToInt(getBankAccountType()));
				pst.setInt(3, uF.parseToInt(strPC));
				pst.setInt(4, nMonth);
				pst.setInt(5, nYear);
				pst.setDate(6, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(7, uF.getDateFormat(strD2, DATE_FORMAT));
//				System.out.println("pst====>"+pst);
				pst.execute();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	private void unApprovedSalary(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			con.setAutoCommit(false);

			String[] strPayCycleDates = getPaycycle().split("-");
			
			String[] strTmp = getEmpId().split("_");
			setEmpId(strTmp[0]);
			pst = con.prepareStatement("select * from payroll_generation where paycycle=? and paid_from=? and paid_to=?");
			pst.setInt(1, uF.parseToInt(strPayCycleDates[2]));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			// System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			String strFyStart = null;
			String strFyEnd = null;
			String strMonth = null;
			String strYear = null;
			while (rs.next()) {
				strFyStart = uF.getDateFormat(rs.getString("financial_year_from_date"), DBDATE, DATE_FORMAT);
				strFyEnd = uF.getDateFormat(rs.getString("financial_year_to_date"), DBDATE, DATE_FORMAT);
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			rs.close(); 
			pst.close();
			
			pst = con.prepareStatement("delete from payroll_generation where emp_id=? and paycycle =? and paid_from =? and paid_to=?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
			pst.setDate(3, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			// System.out.println("pst======>"+pst);
			int x = pst.executeUpdate();

			if (x > 0) {
				pst = con.prepareStatement("delete from payroll_generation_lta where emp_id=? and paycycle =? and paid_from =? and paid_to=?");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				// System.out.println("pst======>"+pst);
				pst.execute();
				pst.close();

				pst = con.prepareStatement("delete from emp_epf_details where emp_id=? and paycycle =? and financial_year_start =? and financial_year_end =? and _month =? ");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
				pst.setDate(3, uF.getDateFormat(strFyStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFyEnd, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strMonth));
				// System.out.println("pst======>"+pst);
				pst.execute();
				pst.close();

				pst = con.prepareStatement("delete from emp_esi_details where emp_id=? and paycycle =? and financial_year_start =? and financial_year_end =? and _month =? ");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
				pst.setDate(3, uF.getDateFormat(strFyStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFyEnd, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strMonth));
				// System.out.println("pst======>"+pst);
				pst.execute();
				pst.close();

				pst = con.prepareStatement("delete from emp_lwf_details where emp_id=? and paycycle =? and financial_year_start =? and financial_year_end =? and _month =? ");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
				pst.setDate(3, uF.getDateFormat(strFyStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFyEnd, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strMonth));
				// System.out.println("pst======>"+pst);
				pst.execute();
				pst.close();

				pst = con.prepareStatement("delete from emp_tds_details where emp_id=? and paycycle =? and financial_year_start =? and financial_year_end =? and _month =? ");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
				pst.setDate(3, uF.getDateFormat(strFyStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFyEnd, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strMonth));
				// System.out.println("pst======>"+pst);
				pst.execute();
				pst.close();

				pst = con.prepareStatement("update emp_reimbursement set ispaid=false where emp_id=? and from_date>=? and from_date<=?");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				// System.out.println("pst======>"+pst);
				pst.execute();
				pst.close();
				
				/**
				 * Arrear Days
				 * */
				pst = con.prepareStatement("update arear_details set is_paid=false where (arrear_type=1 or arrear_type=2) and emp_id=? and arear_id in (select arear_id " +
						"from arrear_generation where emp_id=? and paycycle=? and paid_from =? and paid_to=? group by arear_id)");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setInt(2, uF.parseToInt(getEmpId()));
				pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
				pst.setDate(4, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("delete from arrear_generation where emp_id=? and paycycle=? and paid_from =? and paid_to=?");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.execute();
				pst.close();
			}
			con.commit();
			request.setAttribute("STATUS_MSG", "UnApproved");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			request.setAttribute("STATUS_MSG", "failed");
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadPaySlips() {
		UtilityFunctions uF = new UtilityFunctions();
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		alMonthList = new FillMonth().fillMonth();

		paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();
		paymentModeList = new FillPayMode().fillPaymentMode();
		bankList = new FillBank(request).fillBankAccNoForDocuments(CF, uF, getF_org());

		// wLocationList = new FillWLocation().fillWLocation();
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		
		if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String) session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		employementTypeList = new FillEmploymentType().fillEmploymentType(request);
		
		getSelectedFilter(uF);
		return LOAD;
	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String, String> hmFilter = new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		alFilter.add("DURATION");
		if (getStrPaycycleDuration() != null) {
			String payDuration = "";
			int k = 0;
			for (int i = 0; paycycleDurationList != null && i < paycycleDurationList.size(); i++) {
				if (getStrPaycycleDuration().equals(paycycleDurationList.get(i).getPaycycleDurationId())) {
					if (k == 0) {
						payDuration = paycycleDurationList.get(i).getPaycycleDurationName();
					} else {
						payDuration += ", " + paycycleDurationList.get(i).getPaycycleDurationName();
					}
					k++;
				}
			}
			if (payDuration != null && !payDuration.equals("")) {
				hmFilter.put("DURATION", payDuration);
			} else {
				hmFilter.put("DURATION", "All Duration");
			}
		} else {
			hmFilter.put("DURATION", "All Duration");
		}

		alFilter.add("ORGANISATION");
		if (getF_org() != null) {
			String strOrg = "";
			int k = 0;
			for (int i = 0; organisationList != null && i < organisationList.size(); i++) {
				if (getF_org().equals(organisationList.get(i).getOrgId())) {
					if (k == 0) {
						strOrg = organisationList.get(i).getOrgName();
					} else {
						strOrg += ", " + organisationList.get(i).getOrgName();
					}
					k++;
				}
			}
			if (strOrg != null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}

		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}

		alFilter.add("PAYCYCLE");
		if (getPaycycle() != null) {
			String strPayCycle = "";
			int k = 0;
			for (int i = 0; paycycleList != null && i < paycycleList.size(); i++) {
				if (getPaycycle().equals(paycycleList.get(i).getPaycycleId())) {
					if (k == 0) {
						strPayCycle = paycycleList.get(i).getPaycycleName();
					} else {
						strPayCycle += ", " + paycycleList.get(i).getPaycycleName();
					}
					k++;
				}
			}
			if (strPayCycle != null && !strPayCycle.equals("")) {
				hmFilter.put("PAYCYCLE", strPayCycle);
			} else {
				hmFilter.put("PAYCYCLE", "Select Paycycle");
			}
		} else {
			hmFilter.put("PAYCYCLE", "Select Paycycle");
		}
		
		alFilter.add("PAYMENTMODE");
		if (getF_paymentMode() != null) {
			String strPayMode = "";
			int k = 0;
			for (int i = 0; paymentModeList != null && i < paymentModeList.size(); i++) {
				if (getF_paymentMode().equals(paymentModeList.get(i).getPayModeId())) {
					if (k == 0) {
						strPayMode = paymentModeList.get(i).getPayModeName();
					} else {
						strPayMode += ", " + paymentModeList.get(i).getPayModeName();
					}
					k++;
				}
			}
			if (strPayMode != null && !strPayMode.equals("")) {
				hmFilter.put("PAYMENTMODE", strPayMode);
			} else {
				hmFilter.put("PAYMENTMODE", "All Payment Mode");
			}
		} else {
			hmFilter.put("PAYMENTMODE", "All Payment Mode");
		}

		alFilter.add("LOCATION");
		if (getF_strWLocation() != null) {
			String strLocation = "";
			int k = 0;
			for (int i = 0; wLocationList != null && i < wLocationList.size(); i++) {
				for (int j = 0; j < getF_strWLocation().length; j++) {
					if (getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if (k == 0) {
							strLocation = wLocationList.get(i).getwLocationName();
						} else {
							strLocation += ", " + wLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if (strLocation != null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}

		alFilter.add("DEPARTMENT");
		if (getF_department() != null) {
			String strDepartment = "";
			int k = 0;
			for (int i = 0; departmentList != null && i < departmentList.size(); i++) {
				for (int j = 0; j < getF_department().length; j++) {
					if (getF_department()[j].equals(departmentList.get(i).getDeptId())) {
						if (k == 0) {
							strDepartment = departmentList.get(i).getDeptName();
						} else {
							strDepartment += ", " + departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if (strDepartment != null && !strDepartment.equals("")) {
				hmFilter.put("DEPARTMENT", strDepartment);
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}

		alFilter.add("SERVICE");
		if (getF_service() != null) {
			String strService = "";
			int k = 0;
			for (int i = 0; serviceList != null && i < serviceList.size(); i++) {
				for (int j = 0; j < getF_service().length; j++) {
					if (getF_service()[j].equals(serviceList.get(i).getServiceId())) {
						if (k == 0) {
							strService = serviceList.get(i).getServiceName();
						} else {
							strService += ", " + serviceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if (strService != null && !strService.equals("")) {
				hmFilter.put("SERVICE", strService);
			} else {
				hmFilter.put("SERVICE", "All Services");
			}
		} else {
			hmFilter.put("SERVICE", "All Services");
		}

		alFilter.add("LEVEL");
		if (getF_level() != null) {
			String strLevel = "";
			int k = 0;
			for (int i = 0; levelList != null && i < levelList.size(); i++) {
				for (int j = 0; j < getF_level().length; j++) {
					if (getF_level()[j].equals(levelList.get(i).getLevelId())) {
						if (k == 0) {
							strLevel = levelList.get(i).getLevelCodeName();
						} else {
							strLevel += ", " + levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if (strLevel != null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Levels");
			}
		} else {
			hmFilter.put("LEVEL", "All Levels");
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
		
		alFilter.add("BANK");
		if (getBankBranch() != null) {
			String strBankBranch = "";
			int k = 0;
			for (int i = 0; bankBranchList != null && i < bankBranchList.size(); i++) {
				for (int j = 0; j < getBankBranch().length; j++) {
					if (getBankBranch()[j].equals(bankBranchList.get(i).getBankId())) {
						if (k == 0) {
							strBankBranch = bankBranchList.get(i).getBankName();
						} else {
							strBankBranch += ", " + bankBranchList.get(i).getBankName();
						}
						k++;
					}
				}
			}
			if (strBankBranch != null && !strBankBranch.equals("")) {
				hmFilter.put("BANK", strBankBranch);
			} else {
				hmFilter.put("BANK", "All Bank's");
			}
		} else {
			hmFilter.put("BANK", "All Bank's");
		}

		String selectedFilter = CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	public String viewApporvedPayroll(UtilityFunctions uF, String strD1, String strD2, String strPC) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			con = db.makeConnection(con);
			Map hmEmpMap = CF.getEmpNameMap(con, null, null);
			Map hmEmpCodeMap = CF.getEmpCodeMap(con);
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map hmSalaryDetails = CF.getSalaryHeadsMap(con);
			Map<String, String> hmLoanPoliciesMap = CF.getLoanPoliciesMap(con, uF, getF_org());
			
			Map hmWorkLocationMap = CF.getWorkLocationMap(con);
			Map<String, String> hmEmpWLocationId = CF.getEmpWlocationMap(con);

			List<List<String>> alPaycycleList = new ArrayList<List<String>>();
			for (int i = 0; paycycleList != null && ((i < paycycleList.size() && paycycleList.size()>6 && i< 6) || (i < paycycleList.size() && paycycleList.size()<=6)); i++) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(paycycleList.get(i).getPaycycleId());
				innerList.add(paycycleList.get(i).getPaycycleName());
				String[] strTmp = paycycleList.get(i).getPaycycleId().split("-");
				List<String> alList = getPaidAndUnpaidEmpCount(uF, strTmp[0], strTmp[1], strTmp[2]);
				if(alList.size()>0) {
					innerList.add(alList.get(0));
					innerList.add(alList.get(1));
				} else {
					innerList.add("0");
					innerList.add("0");
				}
				alPaycycleList.add(innerList);
			}
			request.setAttribute("alPaycycleList", alPaycycleList);
			
			Map<String, String> hmPaymentModeMap = CF.getPaymentMode();

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from payroll_generation pg, employee_official_details eod where pg.emp_id = eod.emp_id and paycycle= ? " +
				"and paid_from = ? and paid_to=? ");
//			sbQuery.append(" and pg.emp_id = 464 ");
			if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0) {
            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
            } else {
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
		
			if (getF_department() != null && getF_department().length > 0) {
				sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
			}

			if (getF_service() != null && getF_service().length > 0) {
				sbQuery.append(" and (");
				for (int i = 0; i < getF_service().length; i++) {
					sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
					if (i < getF_service().length - 1) {
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" ) ");

			}
			if (getStrPaycycleDuration() != null) {
				sbQuery.append(" and pay_mode ='" + getStrPaycycleDuration() + "'");
			}

			if (uF.parseToInt(getF_paymentMode()) > 0) {
				sbQuery.append(" and pg.payment_mode =" + uF.parseToInt(getF_paymentMode()));
			}
			
			if (getBankBranch() != null && getBankBranch().length > 0) {
				sbQuery.append(" and eod.emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					"and ((epd.emp_bank_name is not null and epd.emp_bank_name !='' and CAST(epd.emp_bank_name AS integer) in ("+StringUtils.join(getBankBranch(), ",")+")) " +
					"or (epd.emp_bank_name2 is not null and epd.emp_bank_name2 !='' and CAST (epd.emp_bank_name2 AS integer) in ("+StringUtils.join(getBankBranch(), ",")+"))))");
//				sbQuery.append(" and eod.emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
//						"and (CAST(epd.emp_bank_name AS integer) in ("+StringUtils.join(getBankBranch(), ",")+") or CAST (epd.emp_bank_name2 AS integer) in ("+StringUtils.join(getBankBranch(), ",")+")))");
			}

			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
				sbQuery.append(" and eod.wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(WLOCATION_ACCESS) != null) {
				sbQuery.append(" and eod.wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
			}

			if (uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(ORG_ACCESS) != null) {
				sbQuery.append(" and eod.org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
			}
			sbQuery.append(" order by pg.emp_id,sal_effective_date, earning_deduction desc, salary_head_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strPC));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pst1 ====> " + pst);
			rs = pst.executeQuery();
			double dblNetAmount = 0.0d;
			Map hmInner = new HashMap();
			Map hmSalary = new HashMap();
			List alEarnings = new ArrayList();
			List alDeductions = new ArrayList();
			List<String> alSalaryHead = new ArrayList<String>();
     		Map hmPayPayroll = new HashMap();
//			Map hmEmpPayroll = null;
			Map hmIsApprovedSalary = new HashMap();
			Map<String, String> hmEmpSalLastEffectiveDate = new HashMap<String, String>();
			String strEmpIdNew = null;
			double dblGross = 0;
			double dblNet = 0;
			while (rs.next()) {
				strEmpIdNew = rs.getString("emp_id");

				/*if (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					hmEmpPayroll = new HashMap();
					dblNet = 0;
				}*/
				Map hmEmpPayroll = (Map)hmPayPayroll.get(strEmpIdNew+"_"+rs.getString("sal_effective_date"));
				if(hmEmpPayroll == null) {
					hmEmpPayroll = new HashMap();
					dblNet = 0;
//					System.out.println(rs.getString("sal_effective_date") + " -- dblNet ===>> " + dblNet);
				}
				
				if ("E".equalsIgnoreCase(rs.getString("earning_deduction")) && !alEarnings.contains(rs.getString("salary_head_id"))) {
					alEarnings.add(rs.getString("salary_head_id"));
					if (!alSalaryHead.contains(rs.getString("salary_head_id"))) {
						alSalaryHead.add(rs.getString("salary_head_id"));
					}
				} else if ("D".equalsIgnoreCase(rs.getString("earning_deduction")) && !alDeductions.contains(rs.getString("salary_head_id"))) {
					alDeductions.add(rs.getString("salary_head_id"));
					if (!alSalaryHead.contains(rs.getString("salary_head_id"))) {
						alSalaryHead.add(rs.getString("salary_head_id"));
					}
				}
				// hmSalaryDetails.put(rs.getString("salary_head_id"),
				// rs.getString("salary_head_name"));

				hmEmpPayroll.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("amount"))));

				if ("E".equalsIgnoreCase(rs.getString("earning_deduction"))) {
					double dblAmount = rs.getDouble("amount");
					dblGross = uF.parseToDouble((String) hmEmpPayroll.get("GROSS"));
					dblNet += dblAmount;
					hmEmpPayroll.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), Math.round(dblGross + dblAmount)));

				} else {
					double dblAmount = rs.getDouble("amount");
					// double dblAmount = uF.parseToDouble((String)hmEmpPayroll.get("GROSS"));
					dblNet -= dblAmount;
					// hmEmpPayroll.put("GROSS", (dblGross + dblAmount)+"");
				}

				Map<String, String> hmCurrency = (Map) hmCurrencyDetails.get(rs.getString("currency_id"));
				if (hmCurrency == null)
					hmCurrency = new HashMap<String, String>();

				// hmEmpPayroll.put("NET",
				// uF.showData(hmCurrency.get("LONG_CURR"),
				// "")+" "+uF.formatIntoTwoDecimal(dblNet));
				hmEmpPayroll.put("NET", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), Math.round(dblNet)));
				hmEmpPayroll.put("PAYMENT_MODE", hmPaymentModeMap.get(uF.parseToInt(rs.getString("payment_mode")) + ""));
				
				hmPayPayroll.put(strEmpIdNew+"_"+rs.getString("sal_effective_date"), hmEmpPayroll);

				if (rs.getBoolean("is_paid")) {
					hmIsApprovedSalary.put(strEmpIdNew+"_"+rs.getString("sal_effective_date"), rs.getString("is_paid"));
				}

				hmEmpSalLastEffectiveDate.put(strEmpIdNew, rs.getString("sal_effective_date"));
			}
			rs.close();
			pst.close();
			
           /* String s1=null;
			Set set = hmPayPayroll.keySet();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String strEmpId = (String) it.next();
				Map hmPayroll = (Map) hmPayPayroll.get(strEmpId);
				s1 = (String) hmPayroll.get("NET");
			}
			System.out.println("NET======>"+s1);*/
			// System.out.println("getStrMonth()== 1 =>"+getStrMonth());

			if (salaryHeadList == null) {
				salaryHeadList = new ArrayList<FillSalaryHeads>();
			}
			for (String salaryHeadId : alSalaryHead) {
				salaryHeadList.add(new FillSalaryHeads(salaryHeadId, (String) hmSalaryDetails.get(salaryHeadId)));
			}

			if (getStrMonth() != null) {
				setStrMonth(getStrMonth());
			} else {
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, "MM")) + "");
			}

			List alLoans = new ArrayList();
			Map hmEmpLoan = new HashMap();
//			Map<String, String> hmEmpSalLastEffectiveDate = null; //need to implement this map
			CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, strD1, strD2, hmEmpSalLastEffectiveDate); 

			request.setAttribute("hmEmpLoan", hmEmpLoan);
			request.setAttribute("alLoans", alLoans);

			request.setAttribute("alEarnings", alEarnings);
			request.setAttribute("alDeductions", alDeductions);
			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
			request.setAttribute("hmLoanPoliciesMap", hmLoanPoliciesMap);
			request.setAttribute("hmPayPayroll", hmPayPayroll);
			request.setAttribute("hmEmpMap", hmEmpMap);
			request.setAttribute("hmEmpCodeMap", hmEmpCodeMap);
			request.setAttribute("hmIsApprovedSalary", hmIsApprovedSalary);
			request.setAttribute("hmWorkLocationMap", hmWorkLocationMap);
			request.setAttribute("hmEmpWLocationId", hmEmpWLocationId);
			
			request.setAttribute("strD1", strD1);
			request.setAttribute("strD2", strD2);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;
	}

	
	public String payApporvedPayroll(String strD1, String strD2, String strPC) {

		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			boolean flagSalBankUploader = uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_SALARY_BANK_UPLOADER));
			System.out.println("flagSalBankUploader ===>> " + flagSalBankUploader);
			
			Map hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map<String, Map<String, String>> hmArearAmountMap = CF.getArearDetails(con, uF, CF, strD2);
			Map<String, Map<String, String>> hmIncrementArearAmountMap = CF.getIncrementArearDetails(con, uF, CF, strD2);

			pst = con.prepareStatement("select * from payroll_generation where is_paid = false and paycycle = ? and paid_from =? and paid_to=? order by emp_id");
			pst.setInt(1, uF.parseToInt(strPC));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
			Map hmEmpSalaryDetails = new HashMap();
			while (rs.next()) {
				Map hmInner = (Map) hmEmpSalaryDetails.get(rs.getString("emp_id")+"_"+rs.getString("sal_effective_date"));
				if (hmInner == null)
					hmInner = new HashMap();

				double dblTotalAmount = uF.parseToDouble((String) hmInner.get("AMOUNT"));
				if ("E".equalsIgnoreCase(rs.getString("earning_deduction"))) {
					dblTotalAmount += rs.getDouble("amount");
				} else {
					dblTotalAmount -= rs.getDouble("amount"); 
				}
//				System.out.println("dblTotalAmount to Pay======>"+dblTotalAmount);
				hmInner.put("AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalAmount));
				hmInner.put("CURR_ID", rs.getString("currency_id") + "");

				hmEmpSalaryDetails.put(rs.getString("emp_id")+"_"+rs.getString("sal_effective_date"), hmInner);
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpSalaryDetails ===>> " + hmEmpSalaryDetails);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);

			// pst =
			// con.prepareStatement("update payroll_generation set is_paid = true,paid_by=?,paid_date=? where emp_id = ? and paycycle = ?");
			String arr[] = getChbxApprove();
			StringBuilder sbApprovedEmployees = new StringBuilder();
			for (int i = 0; arr != null && i < arr.length; i++) {
				String[] strTmp = arr[i].split("_");
				sbApprovedEmployees.append(strTmp[0] + ",");
				pst = con.prepareStatement("update payroll_generation set is_paid=true,paid_by=?,paid_date=? where emp_id=? and paycycle=? " +
					"and paid_from =? and paid_to=? and sal_effective_date=?");
				pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(3, uF.parseToInt(strTmp[0]));
				pst.setInt(4, uF.parseToInt(strPC));
				pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(7, uF.getDateFormat(strTmp[1], DBDATE));
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("update payroll_generation_lta set is_paid=true,paid_by=?,paid_date=? where emp_id=? and paycycle=? and paid_from=? and paid_to=? and sal_effective_date=?");
				pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(3, uF.parseToInt(strTmp[0]));
				pst.setInt(4, uF.parseToInt(strPC));
				pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(7, uF.getDateFormat(strTmp[1], DBDATE));
				pst.execute();
				pst.close();

				Map hmInner = (Map) hmEmpSalaryDetails.get(arr[i]);
				if (hmInner == null) hmInner = new HashMap();
//				System.out.println("hmInner ===>> " + hmInner);
				
				String strCurrId = (String) hmInner.get("CURR_ID");

				Map hmInnerCurrencyDetails = (Map) hmCurrencyDetails.get(strCurrId);
				if (hmInnerCurrencyDetails == null)
					hmInnerCurrencyDetails = new HashMap();

				pst = con.prepareStatement("select * from payroll_generation where emp_id=? and paid_from=? and paid_to=? and paycycle=?");
				pst.setInt(1, uF.parseToInt(strTmp[0]));
				pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(strPC));
				rs = pst.executeQuery();
				String strFyStart = null;
				String strFyEnd = null;
				String strMonth = null;
				String strYear = null;
				String strPaidDate = null;
				while (rs.next()) {
					strFyStart = uF.getDateFormat(rs.getString("financial_year_from_date"), DBDATE, DATE_FORMAT);
					strFyEnd = uF.getDateFormat(rs.getString("financial_year_to_date"), DBDATE, DATE_FORMAT);
					strMonth = rs.getString("month");
					strYear = rs.getString("year");
					strPaidDate = uF.getDateFormat(rs.getString("paid_date"), DBDATE, DATE_FORMAT);
				}
				rs.close();
				pst.close();

				PayrollHistoryData historyData = new PayrollHistoryData();
				historyData.session = session;
				historyData.CF = CF;
				historyData.request = request;
				historyData.setStrEmpId(strTmp[0]);
				historyData.setStrD1(strD1);
				historyData.setStrD2(strD2);
				historyData.setStrPC(strPC);
				historyData.setStrFinancialYearStart(strFyStart);
				historyData.setStrFinancialYearEnd(strFyEnd);
				historyData.setStrPaidMonth(strMonth);
				historyData.setStrPaidYear(strYear);
				historyData.setStrPaidDate(strPaidDate);
				historyData.insertHistoryData(con, uF);

				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_NEW_SALARY_PAID, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrEmpId(strTmp[0]);
				nF.setStrSalaryAmount(uF.showData((String) hmInnerCurrencyDetails.get("LONG_CURR"), "") + "" + (String) hmInner.get("AMOUNT"));
				nF.setStrTextSalaryAmount(uF.showData((String) hmInner.get("AMOUNT"), ""));
				nF.setStrPaycycle(uF.showData(uF.getDateFormat(strD2, DATE_FORMAT, "MMM-yyyy"), ""));
				nF.setEmailTemplate(true);
				nF.sendNotifications();
				
				
				

				String alertData = "<div style=\"float: left;\"> Payment, Salary has been released by <b>" + CF.getEmpNameMapByEmpId(con, strEmpId)
						+ "</b>. </div>";
				String alertAction = "MyPay.action?pType=WR";
				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(strTmp[0]);
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
				userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
				userAlerts.setStatus(INSERT_WR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();

				/**
				 * Log Details
				 * */
				String strProcessByName = CF.getEmpNameMapByEmpId(con, strEmpId);
				String strEmpName = CF.getEmpNameMapByEmpId(con, "" + Integer.parseInt(strTmp[0]));
				String strProcessMsg = uF.showData(strProcessByName, "") + " has paid salary of " + uF.showData(strEmpName, "") + " on " + ""
					+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
					+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
				LogDetails logDetails = new LogDetails();
				logDetails.session = session;
				logDetails.CF = CF;
				logDetails.request = request;
				logDetails.setProcessId(Integer.parseInt(strTmp[0]));
				logDetails.setProcessType(L_PAID_SALARY);
				logDetails.setProcessActivity(L_ADD);
				logDetails.setProcessMsg(strProcessMsg);
				logDetails.setProcessStep(0);
				logDetails.setProcessBy(uF.parseToInt(strEmpId));
				logDetails.insertLog(con, uF);

			}

			for (int i = 0; arr != null && i < arr.length; i++) {
				String[] strTmp = arr[i].split("_");
				pst = con.prepareStatement("select * from payroll_generation where emp_id=? and paycycle=? and salary_head_id=? and is_paid=true and paid_from=? and paid_to=? and sal_effective_date=?");
				pst.setInt(1, uF.parseToInt(strTmp[0]));
				pst.setInt(2, uF.parseToInt(strPC));
				pst.setInt(3, AREARS);
				pst.setDate(4, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strTmp[1], DBDATE));
				rs = pst.executeQuery();
				boolean arrearFlag = false;
				double dblArearAmount = 0.0d;
				while (rs.next()) {
					dblArearAmount = rs.getDouble("amount");

					Map hmArearMap = (Map) hmArearAmountMap.get(strTmp[0]);
					if (hmArearMap == null)
						hmArearMap = new HashMap();

					int nArearId = uF.parseToInt((String) hmArearMap.get("AREAR_ID"));
					double dblTotalAmount = uF.parseToDouble((String) hmArearMap.get("TOTAL_AMOUNT"));
					double dblPaidAmount = uF.parseToDouble((String) hmArearMap.get("AMOUNT_PAID"));
					double dblBalanceAmount = uF.parseToDouble((String) hmArearMap.get("AMOUNT_BALANCE"));

					dblBalanceAmount = dblTotalAmount - dblPaidAmount - dblArearAmount;
					dblPaidAmount += dblArearAmount;

					pst1 = con.prepareStatement("update arear_details set total_amount_paid=?, arear_amount_balance=?, is_paid=? where arear_id=? and (arrear_type is null or arrear_type=0)");
					pst1.setDouble(1, dblPaidAmount>dblTotalAmount ? dblTotalAmount : dblPaidAmount);
					pst1.setDouble(2, dblBalanceAmount<0 ? 0.0d : dblBalanceAmount);
					if (dblBalanceAmount <= 1) {
						pst1.setBoolean(3, true);
					} else {
						pst1.setBoolean(3, false);
					}
					/*if (dblBalanceAmount <= 1) {
						pst1.setBoolean(3, false);
					} else {
						pst1.setBoolean(3, true);
					}*/
					pst1.setInt(4, nArearId);
					System.out.println("1---pst1===>"+pst1);
					pst1.execute();
					pst1.close();
					
					arrearFlag = true;
				}
				rs.close();
				pst.close();
//				System.out.println("arrearFlag=="+arrearFlag);
				if(arrearFlag) {
					pst = con.prepareStatement("update arear_details set is_paid=true where arrear_type=1 and emp_id=? and arear_id in " +
						"(select arear_id from arrear_generation where emp_id=? and paycycle=? and paid_from =? and paid_to=? group by arear_id)");
					pst.setInt(1, uF.parseToInt(strTmp[0]));
					pst.setInt(2, uF.parseToInt(strTmp[0]));
					pst.setInt(3, uF.parseToInt(strPC));
					pst.setDate(4, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(strD2, DATE_FORMAT));
//					System.out.println("2---pst===>"+pst);
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("update arrear_generation set is_paid=true where emp_id=? and paycycle=? and paid_from =? and paid_to=? and sal_effective_date=?");
					pst.setInt(1, uF.parseToInt(strTmp[0]));
					pst.setInt(2, uF.parseToInt(strPC));
					pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(strTmp[1], DBDATE));
//					System.out.println("3---pst===>"+pst);
					pst.execute();
					pst.close();
					
//					System.out.println("arr[i]=="+arr[i]+"-----"+hmIncrementArearAmountMap);
					
					Map hmIncrementArearMap = (Map) hmIncrementArearAmountMap.get(strTmp[0]);
					if (hmIncrementArearMap == null)
						hmIncrementArearMap = new HashMap();

					int nIncrementArearId = uF.parseToInt((String) hmIncrementArearMap.get("AREAR_ID"));
					double dblIncrementTotalAmount = uF.parseToDouble((String) hmIncrementArearMap.get("TOTAL_AMOUNT"));
					double dblIncrementPaidAmount = uF.parseToDouble((String) hmIncrementArearMap.get("AMOUNT_PAID"));
					double dblIncrementBalanceAmount = uF.parseToDouble((String) hmIncrementArearMap.get("AMOUNT_BALANCE"));

					dblIncrementBalanceAmount = dblIncrementTotalAmount - dblIncrementPaidAmount - dblArearAmount;
					dblIncrementPaidAmount += dblArearAmount;

					pst1 = con.prepareStatement("update arear_details set total_amount_paid=?, arear_amount_balance=?, is_paid=? where arear_id=? and arrear_type=2");
					pst1.setDouble(1, dblIncrementPaidAmount>dblIncrementTotalAmount ? dblIncrementTotalAmount : dblIncrementPaidAmount);
					pst1.setDouble(2, dblIncrementBalanceAmount<0 ? 0.0d : dblIncrementBalanceAmount);
					if (dblIncrementBalanceAmount <= 1) {
						pst1.setBoolean(3, true);
					} else {
						pst1.setBoolean(3, false);
					}
					pst1.setInt(4, nIncrementArearId);
//					System.out.println("4----pst=====>"+pst1);
					pst1.execute();
					pst1.close();
					
					pst = con.prepareStatement("update arrear_generation set is_paid=true where emp_id=? and arear_id=?");
					pst.setInt(1, uF.parseToInt(strTmp[0]));
					pst.setInt(2, nIncrementArearId);
					pst.execute();
					pst.close();
					
					
				}
			}

			sbApprovedEmployees.replace(sbApprovedEmployees.length() - 1, sbApprovedEmployees.length(), "");

			Map<String, String> hmStates = CF.getStateMap(con);
			Map<String, String> hmCountry = CF.getCountryMap(con);

			pst = con.prepareStatement("select bd.bank_account_no, bd.branch_id, bd1.bank_name,bd.bank_branch, bd.branch_code, bd.bank_address, bd.bank_city, " +
					"bd.bank_pincode, bd.bank_state_id, bd.bank_country_id, bd.bank_ifsc_code from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id");
			rs = pst.executeQuery();
			String strBankCode = null;
			String strBankName = null;
			String strBankAddress = null;
			Map<String, String> hmBankBranch = new HashMap<String, String>();
			Map<String, String> hmBankBranchIFSC = new HashMap<String, String>();
			Map<String, String> hmBankName = new HashMap<String, String>();
			while (rs.next()) {
				if (rs.getInt("branch_id") == uF.parseToInt(getBankAccount())) {
					strBankCode = rs.getString("branch_code");
					strBankName = rs.getString("bank_name");
					strBankAddress = rs.getString("bank_address") + "<br/>" + rs.getString("bank_city") + " - " + rs.getString("bank_pincode") + "<br/>"
						+ uF.showData(hmStates.get(rs.getString("bank_state_id")), "") + ", " + uF.showData(hmCountry.get(rs.getString("bank_country_id")), "");
				}
				hmBankBranchIFSC.put(rs.getString("branch_id"), rs.getString("bank_ifsc_code"));
				hmBankBranch.put(rs.getString("branch_id"), rs.getString("bank_branch") + "[" + rs.getString("branch_code") + "]");
				hmBankName.put(rs.getString("branch_id"), rs.getString("bank_name"));
			}
			rs.close();
			pst.close();

			// System.out.println("getIsBifurcatePayOut====>"+getIsBifurcatePayOut());
			// System.out.println("getPrimarySalaryHead====>"+getPrimarySalaryHead());
			// System.out.println("getSecondarySalaryHead====>"+getSecondarySalaryHead());
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Payment Held");

			List<DataStyle> header = new ArrayList<DataStyle>();
			header.add(new DataStyle("Process Date " + uF.showData(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",
					BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Txn Type", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//1
			header.add(new DataStyle("Account Number", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//2
			header.add(new DataStyle("Branch Code", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//3
			header.add(new DataStyle("Txn Code", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//4
			header.add(new DataStyle("Txn Date", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//5
			header.add(new DataStyle("Dr/Cr", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//6
			header.add(new DataStyle("Value Dt", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//7
		
			header.add(new DataStyle("Txn CCY", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//8
			header.add(new DataStyle("Net Pay Amt LCY", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//9
			header.add(new DataStyle("Net Pay Amt LCY", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//10
			header.add(new DataStyle("Rate Con", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//11
			header.add(new DataStyle("Ref No.", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//12
			header.add(new DataStyle("Ref Doc No.", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//13
			header.add(new DataStyle("Transaction Description", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//14
			header.add(new DataStyle("Benef IC", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//15
			header.add(new DataStyle("Benef Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//16
			
						
			header.add(new DataStyle("Benef Add1", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//17
			header.add(new DataStyle("Benef Add2", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//18
			header.add(new DataStyle("Benef Add3", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//19
			header.add(new DataStyle("Benef City", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//20
			header.add(new DataStyle("Benef State", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//21
			header.add(new DataStyle("Benef Country", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//22
			header.add(new DataStyle("Benef Zip", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//23
			header.add(new DataStyle("Option", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//24
			
			header.add(new DataStyle("Issuer Code", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//25
			header.add(new DataStyle("Payable At", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//26
			header.add(new DataStyle("Fig FDT", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//27
			header.add(new DataStyle("MIS Account Number", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));//28
			
			List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();
			Map<String, String> hmEmpLocationId = CF.getEmpWlocationMap(con);
			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con);
			
//			System.out.println("hmWorkLocation ======>>> " + hmWorkLocation);
			
			String currDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
			if (getIsBifurcatePayOut() && ((getPrimarySalaryHead() != null && !getPrimarySalaryHead().trim().equals("") && !getPrimarySalaryHead().trim().equalsIgnoreCase("NULL")) || (getSecondarySalaryHead() != null
				&& !getSecondarySalaryHead().trim().equals("") && !getSecondarySalaryHead().trim().equalsIgnoreCase("NULL")))) {
				if (getPrimarySalaryHead() != null && !getPrimarySalaryHead().trim().equals("") && !getPrimarySalaryHead().trim().equalsIgnoreCase("NULL")) {
					// System.out.println("strPriSalaryHead====>"+getPrimarySalaryHead());
				/*	pst = con.prepareStatement("select emp_fname, emp_lname, emp_bank_name, emp_bank_acct_nbr, net_amount, month, year,"
							+ "emp_bank_name2,emp_bank_acct_nbr_2 from employee_personal_details epd,(select e_amount - (case when d_amount is null "
							+ "then 0 else d_amount END) as net_amount, a.emp_id, month, year from (select sum(amount) as e_amount, emp_id, month, year "
							+ "from payroll_generation where earning_deduction = 'E'  and is_paid = true  and payment_mode=1 and paycycle = ? "
							+ "and salary_head_id in (" + getPrimarySalaryHead()
							+ ") group by emp_id, month, year ) a left join (select sum(amount) as d_amount, "
							+ "emp_id from payroll_generation where earning_deduction = 'D' and is_paid = true and payment_mode=1 and paycycle = ? "
							+ "and salary_head_id in (" + getPrimarySalaryHead() + ") group by emp_id ) b on a.emp_id = b.emp_id) ab where "
							+ "ab.emp_id = epd.emp_per_id and epd.emp_per_id in (" + sbApprovedEmployees.toString() + ") order by epd.emp_fname");*/
					pst = con.prepareStatement("select epd.*, net_amount, month, year from employee_personal_details epd,(select e_amount - (case when d_amount is null "
						+ "then 0 else d_amount END) as net_amount, a.emp_id, month, year from (select sum(amount) as e_amount, emp_id, month, year "
						+ "from payroll_generation where earning_deduction = 'E'  and is_paid = true  and payment_mode=1 and paycycle = ? and paid_from =? and paid_to=? "
						+ "and salary_head_id in (" + getPrimarySalaryHead() + ") group by emp_id, month, year ) a left join (select sum(amount) as d_amount, "
						+ "emp_id from payroll_generation where earning_deduction = 'D' and is_paid = true and payment_mode=1 and paycycle = ? and paid_from =? and paid_to=? "
						+ "and salary_head_id in (" + getPrimarySalaryHead() + ") group by emp_id ) b on a.emp_id = b.emp_id) ab where "
						+ "ab.emp_id = epd.emp_per_id and epd.emp_per_id in (" + sbApprovedEmployees.toString() + ") order by epd.emp_fname");
					pst.setInt(1, uF.parseToInt(strPC));
					pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
					pst.setInt(4, uF.parseToInt(strPC));
					pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
//					System.out.println("getPrimarySalaryHead pst1====>"+pst);
					rs = pst.executeQuery();
					double dblAmount = 0;
					double dblTotalAmount = 0;
					int nMonth = 0;
					int nYear = 0;
					int nCount = 0;
					StringBuilder sbEmpAmountBankDetails = new StringBuilder();
					StringBuilder sbEmpAmountBankDetailsExcel = new StringBuilder();
					StringBuilder sbEmpAmountBankUploaderExcel = new StringBuilder();
					while (rs.next()) {
						dblAmount = uF.parseToDouble(rs.getString("net_amount"));
						nMonth = uF.parseToInt(rs.getString("month"));
						nYear = uF.parseToInt(rs.getString("year"));

						dblTotalAmount += dblAmount;

						Map<String, String> hmWLocationInner = hmWorkLocation.get(hmEmpLocationId.get(rs.getString("emp_per_id")));
						String strBankAccNo = uF.showData(rs.getString("emp_bank_acct_nbr"), "");
						String strBankBranch = uF.showData(hmBankBranch.get(rs.getString("emp_bank_name")), "");
						String strEmpBankName = uF.showData(hmBankName.get(rs.getString("emp_bank_name")), "Other");
						String strEmpBankBranchIFSC = uF.showData(hmBankBranchIFSC.get(rs.getString("emp_bank_name")), "");
						if(strEmpBankBranchIFSC==null || strEmpBankBranchIFSC.equals("")) {
							strEmpBankBranchIFSC = uF.showData(rs.getString("emp_other_bank_acct_ifsc_code"), "");
						}
						
						String strEmpMailId = rs.getString("emp_email_sec");
						if(rs.getString("emp_email_sec")==null || rs.getString("emp_email_sec").equals("")) {
							strEmpMailId = rs.getString("emp_email");
						}
						sbEmpAmountBankDetails.append("<tr>");
						sbEmpAmountBankDetails.append("<td><font size=\"1\">" + ++nCount + ".</font></td>");
						
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						
						sbEmpAmountBankUploaderExcel.append(":_:" + "N" +"::"+ "HDFC to HDFC" +"::"+ strBankAccNo +"::" + uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount) 
							+"::"+ uF.showData(rs.getString("emp_fname"), "") +strEmpMName+ " " + uF.showData(rs.getString("emp_lname"), "")+"::"+uF.showData(hmWLocationInner.get("WL_NAME"), "") 
							+"::"+" "+"::"+uF.showData(rs.getString("emp_address1"), "-")+"::"+uF.showData(rs.getString("emp_city_id"), "-")+"::"+" "+"::"+" "+"::"+" "
							+"::"+" "+"::"+uF.showData(rs.getString("emp_fname"), "") +strEmpMName+ " " + uF.showData(rs.getString("emp_lname"), "")
							+"::"+" "+"::"+" "+"::"+" "+"::"+" "+"::"+" "+"::"+" "+"::"+" "+"::"+" "+"::"+currDate
							+"::"+" "+"::"+strEmpBankBranchIFSC+"::"+strEmpBankName+"::"+ strBankBranch+"::"+uF.showData(strEmpMailId, "-"));
						
						sbEmpAmountBankDetailsExcel.append(":_:" + nCount +"::"+ uF.showData(rs.getString("emp_fname"), "") +strEmpMName+ " "
							+ uF.showData(rs.getString("emp_lname"), "") +"::"+ uF.showData(rs.getString("emp_pan_no"), "") +"::"+ strBankAccNo +"::"+ strBankBranch +"::" + uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount) );
						sbEmpAmountBankDetails.append("<td><font size=\"1\">" + uF.showData(rs.getString("emp_fname"), "") +strEmpMName+ " "
							+ uF.showData(rs.getString("emp_lname"), "") + "</font></td>");
						
						sbEmpAmountBankDetails.append("<td><font size=\"1\">" + uF.showData(rs.getString("emp_pan_no"), "") + "</font></td>");
						
						sbEmpAmountBankDetails.append("<td><font size=\"1\">" + strBankAccNo + "</font></td>");
						sbEmpAmountBankDetails.append("<td><font size=\"1\">" + strBankBranch + "</font></td>");
						sbEmpAmountBankDetails.append("<td align=\"right\"><font size=\"1\">" + uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount) + "</font></td>");
						sbEmpAmountBankDetails.append("</tr>");
						
						List<DataStyle> innerList = new ArrayList<DataStyle>();

						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//1
						innerList.add(new DataStyle(strBankAccNo, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//2
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//3
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//4
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//5
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//6
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//7
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//8
						innerList.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//9
						innerList.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//10
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//11
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//12
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//13
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//14
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//15
						innerList.add(new DataStyle("" , Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//16
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//17
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//18
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//19
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//20
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//21
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//22
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//23
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//24
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//25
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//26
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//27
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//28
						
						reportData.add(innerList);
					}
					rs.close();
					pst.close();
					
					String strContent = null;
					String strName = null;

					Map<String, String> hmActivityNode = CF.getActivityNode(con);
					if (hmActivityNode == null)
						hmActivityNode = new HashMap<String, String>();

					int nTriggerNode = uF.parseToInt(hmActivityNode.get("" + ACTIVITY_BANK_ORDER_ID));

					if (nMonth > 0) {
						Map<String, String> hmOrg = CF.getOrgDetails(con, uF, getF_org());

						pst = con.prepareStatement("select * from document_comm_details where document_text like '%[" + strBankCode
								+ "]%' and trigger_nodes like '%," + nTriggerNode + ",%' and status=1 and org_id=? order by document_id desc limit 1");
						pst.setInt(1, uF.parseToInt(getF_org()));
						// System.out.println("pst=====>"+pst);
						rs = pst.executeQuery();
						while (rs.next()) {
							strContent = rs.getString("document_text");
						}
						rs.close();
						pst.close();

						if (strContent != null && strContent.indexOf("[" + strBankCode + "]") >= 0) {
							strContent = strContent.replace("[" + strBankCode + "]", strBankName + "<br/>" + strBankAddress);
						}

						if (strContent != null && strContent.indexOf(DATE) >= 0) {
							strContent = strContent.replace(DATE, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, CF.getStrReportDateFormat()));
						}

						if (strContent != null && strContent.indexOf(PAYROLL_AMOUNT) >= 0) {
							strContent = strContent.replace(PAYROLL_AMOUNT, uF.formatIntoTwoDecimal(dblTotalAmount));
						}

						if (strContent != null && strContent.indexOf(PAYROLL_AMOUNT_WORDS) >= 0) {
							String digitTotal = "";
							String strTotalAmt = "" + dblTotalAmount;
							if (strTotalAmt.contains(".")) {
								strTotalAmt = strTotalAmt.replace(".", ",");
								String[] temp = strTotalAmt.split(",");
								digitTotal = uF.digitsToWords(uF.parseToInt(temp[0]));
								if (uF.parseToInt(temp[1]) > 0) {
									int pamt = 0;
									if (temp[1].length() == 1) {
										pamt = uF.parseToInt(temp[1] + "0");
									} else {
										pamt = uF.parseToInt(temp[1]);
									}
									digitTotal += " and " + uF.digitsToWords(pamt) + " paise";
								}
							} else {
								int totalAmt1 = (int) dblTotalAmount;
								digitTotal = uF.digitsToWords(totalAmt1);
							}
							strContent = strContent.replace(PAYROLL_AMOUNT_WORDS, digitTotal);
						}

						if (strContent != null && strContent.indexOf(PAY_MONTH) >= 0) {
							strContent = strContent.replace(PAY_MONTH, uF.getMonth(nMonth));
						}

						if (strContent != null && strContent.indexOf(PAY_YEAR) >= 0) {
							strContent = strContent.replace(PAY_YEAR, "" + nYear);
						}

						if (strContent != null && strContent.indexOf(LEGAL_ENTITY_NAME) >= 0) {
							strContent = strContent.replace(LEGAL_ENTITY_NAME, uF.showData(hmOrg.get("ORG_NAME"), ""));
						}

					}
					
					int nMaxStatementId = 0;					
					if ((strContent != null || flagSalBankUploader) && nMonth > 0) {
						StringBuilder sbEmpBankDetails = new StringBuilder();
						StringBuilder sbEmpBankDetailsExcel = new StringBuilder();
						sbEmpBankDetailsExcel.append("Bank Statement::Sr. No.::Name::Account No::Branch::Amount");
						StringBuilder sbEmpBankUploaderExcel = new StringBuilder();
						sbEmpBankUploaderExcel.append("Transaction Type (N - NFET, R - RTGS)::Beneficiary Code * (Inter – HDFC to HDFC)::Beneficiary Account Number" +
								"::Instrument Amount::Beneficiary Name (Upto 40 character withput any special character)::Drawee Location::Print Location" +
								"::Bene Address 1::Bene Address 2::Bene Address 3::Bene Address 4::Bene Address 5::Instruction Reference Number" +
								"::Customer Reference Number(Any alpha numeric character upto 20)::Payment details 1::Payment details 2::Payment details 3" +
								"::Payment details 4::Payment details 5::Payment details 6::Payment details 7::Cheque Number::Chq / Trn Date (DD/MM/YYYY)" +
								"::MICR Number::IFSC Code::Bene Bank Name::Bene Bank Branch Name::Beneficiary email id");
						sbEmpBankDetails.append("<table width=\"100%\">");
						sbEmpBankDetails.append("<tr>");
						sbEmpBankDetails.append("<td width=\"20\"><b>Sr. No.</b></td>");
						sbEmpBankDetails.append("<td><b>Name</b></td>");
						sbEmpBankDetails.append("<td><b>Pan No</b></td>");
						sbEmpBankDetails.append("<td><b>Account No</b></td>");
						sbEmpBankDetails.append("<td><b>Branch</b></td>");
						sbEmpBankDetails.append("<td align=\"right\"><b>Amount</b></td>");
						sbEmpBankDetails.append("</tr>");

						sbEmpBankDetails.append(sbEmpAmountBankDetails);
						sbEmpBankDetailsExcel.append(sbEmpAmountBankDetailsExcel);
						sbEmpBankUploaderExcel.append(sbEmpAmountBankUploaderExcel);
						
						sbEmpBankDetails.append("<tr>");
						sbEmpBankDetails.append("<td colspan=\"6\">&nbsp;</td>");
						sbEmpBankDetails.append("</tr>");

						sbEmpBankDetailsExcel.append(":_: :: :: :: ::TOTAL::"+ uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalAmount));
						
						sbEmpBankDetails.append("<tr>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td><b>TOTAL</b></td>");
						sbEmpBankDetails.append("<td align=\"right\"><b>" + uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalAmount) + "</b></td>");
						sbEmpBankDetails.append("</tr>");

						sbEmpBankDetails.append("</table>");

						strName = "BankStatement_" + nMonth + "_" + nYear;

						pst = con.prepareStatement("insert into payroll_bank_statement(statement_name, statement_body, generated_date, "
								+ "generated_by, payroll_amount,bank_pay_type, statement_body_excel, bank_uploader_excel) values (?,?,?,?, ?,?,?,?)");
						pst.setString(1, strName);
						pst.setString(2, strContent + "" + sbEmpBankDetails.toString());
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt((String) session.getAttribute(EMPID)));
						pst.setDouble(5, uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblTotalAmount))));
						pst.setInt(6, 1);
						pst.setString(7, sbEmpBankDetailsExcel.toString());
						pst.setString(8, sbEmpBankUploaderExcel.toString());
						pst.execute();
//						System.out.println("pst 1 ===>> " + pst);
						pst.close();

						pst = con.prepareStatement("select max(statement_id) as statement_id from payroll_bank_statement");
						rs = pst.executeQuery();
						
						while (rs.next()) {
							nMaxStatementId = rs.getInt("statement_id");
						}
						rs.close();
						pst.close();
						
						ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
						sheetDesign.getExcelSheetDesignDataPay(workbook, sheet, header, reportData);
						
						String directory = CF.getStrDocSaveLocation()+I_BANK_EXCEL_STMT+"/"+nMaxStatementId; 
						FileUtils.forceMkdir(new File(directory));
						
						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						workbook.write(buffer);
					
						byte[] bytes = buffer.toByteArray();
						String fileName = directory+"/"+strName+".xls";
						File f = new File(fileName);
//						File f = File.("tmp", ".xls", new File(directory));
						FileOutputStream fileOuputStream = new FileOutputStream(f); 
						fileOuputStream.write(bytes);
						
						pst = con.prepareStatement("update payroll_bank_statement set bank_excel_sheet =? where statement_id = ?");
						pst.setString(1, strName+".xls");
						pst.setInt(2, nMaxStatementId);
						pst.executeUpdate();
						pst.close();
					}
					
					pst = con.prepareStatement("update payroll_generation set statement_id=?, bank_pay_type=? where emp_id in (" + sbApprovedEmployees.toString() + ") "
						+ "and salary_head_id in (" + getPrimarySalaryHead() + ") and paycycle=? and month=? and year=? and paid_from =? and paid_to=?");
					pst.setInt(1, nMaxStatementId);
					pst.setInt(2, 1);
					pst.setInt(3, uF.parseToInt(strPC));
					pst.setInt(4, nMonth);
					pst.setInt(5, nYear);
					pst.setDate(6, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(7, uF.getDateFormat(strD2, DATE_FORMAT));
//					System.out.println("getPrimarySalaryHead pst ===>> " + pst);
					pst.execute();
					pst.close();
				}
				if (getSecondarySalaryHead() != null && !getSecondarySalaryHead().trim().equals("")
						&& !getSecondarySalaryHead().trim().equalsIgnoreCase("NULL")) {
					pst = con.prepareStatement("select epd.*, net_amount, month, year from employee_personal_details epd,(select e_amount - (case when d_amount is null "
						+ "then 0 else d_amount END) as net_amount, a.emp_id, month, year from (select sum(amount) as e_amount, emp_id, month, year "
						+ "from payroll_generation where earning_deduction = 'E'  and is_paid = true  and payment_mode=1 and paycycle = ? and paid_from =? and paid_to=? "
						+ "and salary_head_id in (" + getSecondarySalaryHead() + ") group by emp_id, month, year ) a left join (select sum(amount) as d_amount, "
						+ "emp_id from payroll_generation where earning_deduction = 'D' and is_paid = true and payment_mode=1 and paycycle = ? and paid_from =? and paid_to=? "
						+ "and salary_head_id in (" + getSecondarySalaryHead() + ") group by emp_id ) b on a.emp_id = b.emp_id) ab where "
						+ "ab.emp_id = epd.emp_per_id and epd.emp_per_id in (" + sbApprovedEmployees.toString() + ") order by epd.emp_fname");
					pst.setInt(1, uF.parseToInt(strPC));
					pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
					pst.setInt(4, uF.parseToInt(strPC));
					pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
//					System.out.println("getSecondarySalaryHead pst ====> "+pst);
					rs = pst.executeQuery();
					double dblAmount = 0;
					double dblTotalAmount = 0;
					int nMonth = 0;
					int nYear = 0;
					int nCount = 0;
					StringBuilder sbEmpAmountBankDetails = new StringBuilder();
					StringBuilder sbEmpAmountBankDetailsExcel = new StringBuilder();
					StringBuilder sbEmpAmountBankUploaderExcel = new StringBuilder();
					while (rs.next()) {
						dblAmount = uF.parseToDouble(rs.getString("net_amount"));
						nMonth = uF.parseToInt(rs.getString("month"));
						nYear = uF.parseToInt(rs.getString("year"));

						dblTotalAmount += dblAmount;

						Map<String, String> hmWLocationInner = hmWorkLocation.get(hmEmpLocationId.get(rs.getString("emp_per_id")));
						String strBankAccNo = uF.showData(rs.getString("emp_bank_acct_nbr"), "");
						String strBankBranch = uF.showData(hmBankBranch.get(rs.getString("emp_bank_name")), "");
						String strEmpBankName = uF.showData(hmBankName.get(rs.getString("emp_bank_name")), "Other");
						String strEmpBankBranchIFSC = uF.showData(hmBankBranchIFSC.get(rs.getString("emp_bank_name")), "");
						if(strEmpBankBranchIFSC==null || strEmpBankBranchIFSC.equals("")) {
							strEmpBankBranchIFSC = uF.showData(rs.getString("emp_other_bank_acct_ifsc_code"), "");
						}
						
						String strEmpMailId = rs.getString("emp_email_sec");
						if(rs.getString("emp_email_sec")==null || rs.getString("emp_email_sec").equals("")) {
							strEmpMailId = rs.getString("emp_email");
						}
						sbEmpAmountBankDetails.append("<tr>");
						sbEmpAmountBankDetails.append("<td><font size=\"1\">" + ++nCount + ".</font></td>");
						
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						
						sbEmpAmountBankUploaderExcel.append(":_:" + "N" +"::"+ "HDFC to HDFC" +"::"+ strBankAccNo +"::" + uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount) 
							+"::"+ uF.showData(rs.getString("emp_fname"), "") +strEmpMName+ " " + uF.showData(rs.getString("emp_lname"), "")+"::"+uF.showData(hmWLocationInner.get("WL_NAME"), "") 
							+"::"+" "+"::"+uF.showData(rs.getString("emp_address1"), "-")+"::"+uF.showData(rs.getString("emp_city_id"), "-")+"::"+" "+"::"+" "+"::"+" "
							+"::"+" "+"::"+uF.showData(rs.getString("emp_fname"), "") +strEmpMName+ " " + uF.showData(rs.getString("emp_lname"), "")
							+"::"+" "+"::"+" "+"::"+" "+"::"+" "+"::"+" "+"::"+" "+"::"+" "+"::"+" "+"::"+currDate
							+"::"+" "+"::"+strEmpBankBranchIFSC+"::"+strEmpBankName+"::"+ strBankBranch+"::"+uF.showData(strEmpMailId, "-"));
						
						sbEmpAmountBankDetailsExcel.append(":_:" + nCount +"::"+ uF.showData(rs.getString("emp_fname"), "") +strEmpMName+ " "
							+ uF.showData(rs.getString("emp_lname"), "") +"::"+ uF.showData(rs.getString("emp_pan_no"), "") +"::"+ strBankAccNo +"::"+ strBankBranch +"::" + uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount) );
						sbEmpAmountBankDetails.append("<td><font size=\"1\">" + uF.showData(rs.getString("emp_fname"), "") +strEmpMName+ " "
							+ uF.showData(rs.getString("emp_lname"), "") + "</font></td>");
						
						sbEmpAmountBankDetails.append("<td><font size=\"1\">" + uF.showData(rs.getString("emp_pan_no"), "") + "</font></td>");
						
						sbEmpAmountBankDetails.append("<td><font size=\"1\">" + strBankAccNo + "</font></td>");
						sbEmpAmountBankDetails.append("<td><font size=\"1\">" + strBankBranch + "</font></td>");
						sbEmpAmountBankDetails.append("<td align=\"right\"><font size=\"1\">" + uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount) + "</font></td>");
						sbEmpAmountBankDetails.append("</tr>");
						
						List<DataStyle> innerList = new ArrayList<DataStyle>();

						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//1
						innerList.add(new DataStyle(strBankAccNo, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//2
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//3
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//4
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//5
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//6
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//7
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//8
						innerList.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//9
						innerList.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//10
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//11
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//12
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//13
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//14
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//15
						innerList.add(new DataStyle("" , Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//16
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//17
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//18
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//19
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//20
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//21
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//22
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//23
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//24
						
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//25
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//26
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//27
						innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//28
						
						reportData.add(innerList);
					}
					rs.close();
					pst.close();
					
					String strContent = null;
					String strName = null;

					Map<String, String> hmActivityNode = CF.getActivityNode(con);
					if (hmActivityNode == null)
						hmActivityNode = new HashMap<String, String>();

					int nTriggerNode = uF.parseToInt(hmActivityNode.get("" + ACTIVITY_BANK_ORDER_ID));

					if (nMonth > 0) {
						Map<String, String> hmOrg = CF.getOrgDetails(con, uF, getF_org());

						pst = con.prepareStatement("select * from document_comm_details where document_text like '%[" + strBankCode
								+ "]%' and trigger_nodes like '%," + nTriggerNode + ",%' and status=1 and org_id=? order by document_id desc limit 1");
						pst.setInt(1, uF.parseToInt(getF_org()));
						// System.out.println("pst=====>"+pst);
						rs = pst.executeQuery();
						while (rs.next()) {
							strContent = rs.getString("document_text");
						}
						rs.close();
						pst.close();

						if (strContent != null && strContent.indexOf("[" + strBankCode + "]") >= 0) {
							strContent = strContent.replace("[" + strBankCode + "]", strBankName + "<br/>" + strBankAddress);
						}

						if (strContent != null && strContent.indexOf(DATE) >= 0) {
							strContent = strContent.replace(DATE,
									uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, CF.getStrReportDateFormat()));
						}

						if (strContent != null && strContent.indexOf(PAYROLL_AMOUNT) >= 0) {
							strContent = strContent.replace(PAYROLL_AMOUNT, uF.formatIntoTwoDecimal(dblTotalAmount));
						}

						if (strContent != null && strContent.indexOf(PAYROLL_AMOUNT_WORDS) >= 0) {
							String digitTotal = "";
							String strTotalAmt = "" + dblTotalAmount;
							if (strTotalAmt.contains(".")) {
								strTotalAmt = strTotalAmt.replace(".", ",");
								String[] temp = strTotalAmt.split(",");
								digitTotal = uF.digitsToWords(uF.parseToInt(temp[0]));
								if (uF.parseToInt(temp[1]) > 0) {
									int pamt = 0;
									if (temp[1].length() == 1) {
										pamt = uF.parseToInt(temp[1] + "0");
									} else {
										pamt = uF.parseToInt(temp[1]);
									}
									digitTotal += " and " + uF.digitsToWords(pamt) + " paise";
								}
							} else {
								int totalAmt1 = (int) dblTotalAmount;
								digitTotal = uF.digitsToWords(totalAmt1);
							}
							strContent = strContent.replace(PAYROLL_AMOUNT_WORDS, digitTotal);
						}

						if (strContent != null && strContent.indexOf(PAY_MONTH) >= 0) {
							strContent = strContent.replace(PAY_MONTH, uF.getMonth(nMonth));
						}

						if (strContent != null && strContent.indexOf(PAY_YEAR) >= 0) {
							strContent = strContent.replace(PAY_YEAR, "" + nYear);
						}

						if (strContent != null && strContent.indexOf(LEGAL_ENTITY_NAME) >= 0) {
							strContent = strContent.replace(LEGAL_ENTITY_NAME, uF.showData(hmOrg.get("ORG_NAME"), ""));
						}

					}

					int nMaxStatementId = 0;
					if ((strContent != null || flagSalBankUploader) && nMonth > 0) {
						StringBuilder sbEmpBankDetails = new StringBuilder();
						StringBuilder sbEmpBankDetailsExcel = new StringBuilder();
						sbEmpBankDetailsExcel.append("Bank Statement::Sr. No.::Name::Pan No::Account No::Branch::Amount");
						StringBuilder sbEmpBankUploaderExcel = new StringBuilder();
						sbEmpBankUploaderExcel.append("Transaction Type (N – NFET, R – RTGS)::Beneficiary Code * (Inter – HDFC to HDFC)::Beneficiary Account Number" +
							"::Instrument Amount::Beneficiary Name (Upto 40 character withput any special character)::Drawee Location::Print Location" +
							"::Bene Address 1::Bene Address 2::Bene Address 3::Bene Address 4::Bene Address 5::Instruction Reference Number" +
							"::Customer Reference Number(Any alpha numeric character upto 20)::Payment details 1::Payment details 2::Payment details 3" +
							"::Payment details 4::Payment details 5::Payment details 6::Payment details 7::Cheque Number::Chq / Trn Date (DD/MM/YYYY)" +
							"::MICR Number::IFSC Code::Bene Bank Name::Bene Bank Branch Name::Beneficiary email id");
						sbEmpBankDetails.append("<table width=\"100%\">");
						sbEmpBankDetails.append("<tr>");
						sbEmpBankDetails.append("<td width=\"20\"><b>Sr. No.</b></td>");
						sbEmpBankDetails.append("<td><b>Name</b></td>");
						sbEmpBankDetails.append("<td><b>Pan No</b></td>");
						sbEmpBankDetails.append("<td><b>Account No</b></td>");
						sbEmpBankDetails.append("<td><b>Branch</b></td>");
						sbEmpBankDetails.append("<td align=\"right\"><b>Amount</b></td>");
						sbEmpBankDetails.append("</tr>");

						sbEmpBankDetails.append(sbEmpAmountBankDetails);
						sbEmpBankDetailsExcel.append(sbEmpAmountBankDetailsExcel);
						sbEmpBankUploaderExcel.append(sbEmpAmountBankUploaderExcel);

						sbEmpBankDetails.append("<tr>");
						sbEmpBankDetails.append("<td colspan=\"6\">&nbsp;</td>");
						sbEmpBankDetails.append("</tr>");

						sbEmpBankDetailsExcel.append(":_: :: :: :: ::TOTAL::"+ uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalAmount));
						sbEmpBankDetails.append("<tr>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td><b>TOTAL</b></td>");
						sbEmpBankDetails.append("<td align=\"right\"><b>" + uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalAmount) + "</b></td>");
						sbEmpBankDetails.append("</tr>");

						sbEmpBankDetails.append("</table>");

						strName = "BankStatement_" + nMonth + "_" + nYear;

						// pst =
						// con.prepareStatement("insert into payroll_bank_statement (statement_name, statement_body, generated_date, "
						// +
						// "generated_by, payroll_amount,bank_pay_type) values (?,?,?,?, ?,?)");
						// pst.setString(1, strName);
						// pst.setString(2,
						// strContent+""+sbEmpBankDetails.toString());
						// pst.setDate(3,
						// uF.getCurrentDate(CF.getStrTimeZone()));
						// pst.setInt(4,
						// uF.parseToInt((String)session.getAttribute(EMPID)));
						// pst.setDouble(5,
						// uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblTotalAmount))));
						// pst.execute();
						// pst.close();

						pst = con.prepareStatement("insert into payroll_bank_statement(statement_name, statement_body, generated_date, "
								+ "generated_by, payroll_amount,bank_pay_type, statement_body_excel, bank_uploader_excel) values (?,?,?,?, ?,?,?,?)");
						pst.setString(1, strName);
						pst.setString(2, strContent + "" + sbEmpBankDetails.toString());
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt((String) session.getAttribute(EMPID)));
						pst.setDouble(5, uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblTotalAmount))));
						pst.setInt(6, 2);
						pst.setString(7, sbEmpBankDetailsExcel.toString());
						pst.setString(8, sbEmpBankUploaderExcel.toString());
						pst.execute();
//						System.out.println("pst 2 ===>> " + pst);
						pst.close();

						pst = con.prepareStatement("select max(statement_id) as statement_id from payroll_bank_statement");
						rs = pst.executeQuery();
						while (rs.next()) {
							nMaxStatementId = rs.getInt("statement_id");
						}
						rs.close();
						pst.close();
						
						
						ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
						sheetDesign.getExcelSheetDesignDataPay(workbook, sheet, header, reportData);
						
						String directory = CF.getStrDocSaveLocation()+I_BANK_EXCEL_STMT+"/"+nMaxStatementId; 
						FileUtils.forceMkdir(new File(directory));
						
						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						workbook.write(buffer);
					
						byte[] bytes = buffer.toByteArray();
						String fileName = directory+"/"+strName+".xls";
						File f = new File(fileName);
//						File f = File.("tmp", ".xls", new File(directory));
						FileOutputStream fileOuputStream = new FileOutputStream(f); 
						fileOuputStream.write(bytes);
						
						pst = con.prepareStatement("update payroll_bank_statement set bank_excel_sheet =? where statement_id = ?");
						pst.setString(1, strName+".xls");
						pst.setInt(2, nMaxStatementId);
						pst.executeUpdate();
						pst.close();
					}
					pst = con.prepareStatement("update payroll_generation set statement_id=?, bank_pay_type=? where emp_id in (" + sbApprovedEmployees.toString() + ") "
						+ "and salary_head_id in (" + getSecondarySalaryHead() + ") and paycycle=? and month=? and year=? and paid_from =? and paid_to=?");
					pst.setInt(1, nMaxStatementId);
					pst.setInt(2, 2);
					pst.setInt(3, uF.parseToInt(strPC));
					pst.setInt(4, nMonth);
					pst.setInt(5, nYear);
					pst.setDate(6, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(7, uF.getDateFormat(strD2, DATE_FORMAT));
//					System.out.println("getSecondarySalaryHead pst ====> " + pst);
					pst.execute();
					pst.close();
				}
			} else {
				pst = con.prepareStatement("select epd.*, net_amount, month, year from employee_personal_details epd, ( select e_amount - (case when d_amount is null then 0 else d_amount END) as net_amount, "
					+ "a.emp_id, month, year from (select sum(amount) as e_amount, emp_id, month, year from payroll_generation where earning_deduction = 'E'  and is_paid = true  and payment_mode=1 " +
					"and paycycle =? and paid_from =? and paid_to=? group by emp_id, month, year ) a left join (select sum(amount) as d_amount, emp_id from payroll_generation where earning_deduction = 'D'  and is_paid = true " +
					"and payment_mode=1 and paycycle =? and paid_from =? and paid_to=? group by emp_id ) b on a.emp_id = b.emp_id) ab where ab.emp_id = epd.emp_per_id and epd.emp_per_id in (" + sbApprovedEmployees.toString() + ") order by epd.emp_fname ");
				pst.setInt(1, uF.parseToInt(strPC));
				pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(strPC));
				pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
//				 System.out.println("else pst ====> " + pst);
				rs = pst.executeQuery();
				double dblAmount = 0;
				double dblTotalAmount = 0;
				int nMonth = 0;
				int nYear = 0;
				int nCount = 0;
				StringBuilder sbEmpAmountBankDetails = new StringBuilder();
				StringBuilder sbEmpAmountBankDetailsExcel = new StringBuilder();
				StringBuilder sbEmpAmountBankUploaderExcel = new StringBuilder();
				int nBankPayType = 0;
				while (rs.next()) {
					dblAmount = uF.parseToDouble(rs.getString("net_amount"));
					nMonth = uF.parseToInt(rs.getString("month"));
					nYear = uF.parseToInt(rs.getString("year"));
					dblTotalAmount += dblAmount;

//					System.out.println("hmWorkLocation ===>> " + hmWorkLocation);
					Map<String, String> hmWLocationInner = hmWorkLocation.get(hmEmpLocationId.get(rs.getString("emp_per_id")));
					String strBankAccNo = uF.showData(rs.getString("emp_bank_acct_nbr"), "");
					String strBankBranch = uF.showData(hmBankBranch.get(rs.getString("emp_bank_name")), "");
					String strEmpBankName = uF.showData(hmBankName.get(rs.getString("emp_bank_name")), "Other");
					String strEmpBankBranchIFSC = uF.showData(hmBankBranchIFSC.get(rs.getString("emp_bank_name")), "");
					if(strEmpBankBranchIFSC==null || strEmpBankBranchIFSC.equals("")) {
						strEmpBankBranchIFSC = uF.showData(rs.getString("emp_other_bank_acct_ifsc_code"), "");
					}
					String strEmpMailId = rs.getString("emp_email_sec");
					if(rs.getString("emp_email_sec")==null || rs.getString("emp_email_sec").equals("")) {
						strEmpMailId = rs.getString("emp_email");
					}
					
					nBankPayType = 1;
					if (uF.parseToInt(getBankAccountType()) == 2) {
						strBankAccNo = uF.showData(rs.getString("emp_bank_acct_nbr_2"), "");
						strBankBranch = uF.showData(hmBankBranch.get(rs.getString("emp_bank_name2")), "");
						nBankPayType = 2;
					}

					sbEmpAmountBankDetails.append("<tr>");
					sbEmpAmountBankDetails.append("<td><font size=\"1\">" + ++nCount + ".</font></td>");
				
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
//					System.out.println("hmWLocationInner ===>> " + hmWLocationInner);
					sbEmpAmountBankUploaderExcel.append(":_:" + "N" +"::"+ "HDFC to HDFC" +"::"+ strBankAccNo +"::" + uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount) 
						+"::"+ uF.showData(rs.getString("emp_fname"), "") +strEmpMName+ " " + uF.showData(rs.getString("emp_lname"), "")+"::"+uF.showData(hmWLocationInner.get("WL_NAME"), "") 
						+"::"+" "+"::"+uF.showData(rs.getString("emp_address1"), "-")+"::"+uF.showData(rs.getString("emp_city_id"), "-")+"::"+" "+"::"+" "+"::"+" "
						+"::"+" "+"::"+uF.showData(rs.getString("emp_fname"), "") +strEmpMName+ " " + uF.showData(rs.getString("emp_lname"), "")
						+"::"+" "+"::"+" "+"::"+" "+"::"+" "+"::"+" "+"::"+" "+"::"+" "+"::"+" "+"::"+currDate
						+"::"+" "+"::"+strEmpBankBranchIFSC+"::"+strEmpBankName+"::"+ strBankBranch+"::"+uF.showData(strEmpMailId, "-"));
					
					sbEmpAmountBankDetailsExcel.append(":_:" + nCount +"::"+ uF.showData(rs.getString("emp_fname"), "") +strEmpMName+ " "
							+ uF.showData(rs.getString("emp_lname"), "") +"::"+ uF.showData(rs.getString("emp_pan_no"), "") +"::"+ strBankAccNo +"::"+ strBankBranch +"::" + uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount) );
					sbEmpAmountBankDetails.append("<td><font size=\"1\">" + uF.showData(rs.getString("emp_fname"), "") +strEmpMName+ " "
							+ uF.showData(rs.getString("emp_lname"), "") + "</font></td>");
				//===start parvez date: 29-12-2022===	
					sbEmpAmountBankDetails.append("<td><font size=\"1\">" + uF.showData(rs.getString("emp_pan_no"), "") + "</font></td>");
				//===end parvez date: 29-12-2022===	
					sbEmpAmountBankDetails.append("<td><font size=\"1\">" + strBankAccNo + "</font></td>");
					sbEmpAmountBankDetails.append("<td><font size=\"1\">" + strBankBranch + "</font></td>");
					sbEmpAmountBankDetails.append("<td align=\"right\"><font size=\"1\">" + uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount) + "</font></td>");
					sbEmpAmountBankDetails.append("</tr>");
					
					List<DataStyle> innerList = new ArrayList<DataStyle>();

					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//1
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//1
					innerList.add(new DataStyle(strBankAccNo, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//2
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//3
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//4
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//5
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//6
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//7
					
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//8
					innerList.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//9
					innerList.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//10
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//11
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//12
					
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//13
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//14
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//15
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//16
					
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//17
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//18
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//19
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//20
					
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//21
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//22
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//23
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//24
					
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//25
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//26
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//27
					innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));//28
					
					reportData.add(innerList);
				}
				rs.close();
				pst.close();
				
				String strContent = null;
				String strName = null;

				Map<String, String> hmActivityNode = CF.getActivityNode(con);
				if (hmActivityNode == null)
					hmActivityNode = new HashMap<String, String>();

				int nTriggerNode = uF.parseToInt(hmActivityNode.get("" + ACTIVITY_BANK_ORDER_ID));

				if (nMonth > 0) {
					Map<String, String> hmOrg = CF.getOrgDetails(con, uF, getF_org());

					pst = con.prepareStatement("select * from document_comm_details where document_text like '%[" + strBankCode
							+ "]%' and trigger_nodes like '%," + nTriggerNode + ",%' and status=1 and org_id=? order by document_id desc limit 1");
					pst.setInt(1, uF.parseToInt(getF_org()));
					// System.out.println("pst=====>"+pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						strContent = rs.getString("document_text");
					}
					rs.close();
					pst.close();

					if (strContent != null && strContent.indexOf("[" + strBankCode + "]") >= 0) {
						strContent = strContent.replace("[" + strBankCode + "]", strBankName + "<br/>" + strBankAddress);
					}

					if (strContent != null && strContent.indexOf(DATE) >= 0) {
						strContent = strContent.replace(DATE,
								uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, CF.getStrReportDateFormat()));
					}

					if (strContent != null && strContent.indexOf(PAYROLL_AMOUNT) >= 0) {
						strContent = strContent.replace(PAYROLL_AMOUNT, uF.formatIntoTwoDecimal(dblTotalAmount));
					}

					if (strContent != null && strContent.indexOf(PAYROLL_AMOUNT_WORDS) >= 0) {
						String digitTotal = "";
						String strTotalAmt = "" + dblTotalAmount;
						if (strTotalAmt.contains(".")) {
							strTotalAmt = strTotalAmt.replace(".", ",");
							String[] temp = strTotalAmt.split(",");
							digitTotal = uF.digitsToWords(uF.parseToInt(temp[0]));
							if (uF.parseToInt(temp[1]) > 0) {
								int pamt = 0;
								if (temp[1].length() == 1) {
									pamt = uF.parseToInt(temp[1] + "0");
								} else {
									pamt = uF.parseToInt(temp[1]);
								}
								digitTotal += " and " + uF.digitsToWords(pamt) + " paise";
							}
						} else {
							int totalAmt1 = (int) dblTotalAmount;
							digitTotal = uF.digitsToWords(totalAmt1);
						}
						strContent = strContent.replace(PAYROLL_AMOUNT_WORDS, digitTotal);
					}

					if (strContent != null && strContent.indexOf(PAY_MONTH) >= 0) {
						strContent = strContent.replace(PAY_MONTH, uF.getMonth(nMonth));
					}

					if (strContent != null && strContent.indexOf(PAY_YEAR) >= 0) {
						strContent = strContent.replace(PAY_YEAR, "" + nYear);
					}

					if (strContent != null && strContent.indexOf(LEGAL_ENTITY_NAME) >= 0) {
						strContent = strContent.replace(LEGAL_ENTITY_NAME, uF.showData(hmOrg.get("ORG_NAME"), ""));
					}

				}

				int nMaxStatementId = 0;
				if ((strContent != null || flagSalBankUploader) && nMonth > 0) {
					StringBuilder sbEmpBankDetails = new StringBuilder();
					StringBuilder sbEmpBankDetailsExcel = new StringBuilder();
					sbEmpBankDetailsExcel.append("Bank Statement::Sr. No.::Name::Pan No::Account No::Branch::Amount");
					StringBuilder sbEmpBankUploaderExcel = new StringBuilder();
					sbEmpBankUploaderExcel.append("Transaction Type (N – NFET, R – RTGS)::Beneficiary Code * (Inter – HDFC to HDFC)::Beneficiary Account Number" +
						"::Instrument Amount::Beneficiary Name (Upto 40 character withput any special character)::Drawee Location::Print Location" +
						"::Bene Address 1::Bene Address 2::Bene Address 3::Bene Address 4::Bene Address 5::Instruction Reference Number" +
						"::Customer Reference Number(Any alpha numeric character upto 20)::Payment details 1::Payment details 2::Payment details 3" +
						"::Payment details 4::Payment details 5::Payment details 6::Payment details 7::Cheque Number::Chq / Trn Date (DD/MM/YYYY)" +
						"::MICR Number::IFSC Code::Bene Bank Name::Bene Bank Branch Name::Beneficiary email id");
					sbEmpBankDetails.append("<table width=\"100%\">");
					sbEmpBankDetails.append("<tr>");
					sbEmpBankDetails.append("<td width=\"20\"><b>Sr. No.</b></td>");
					sbEmpBankDetails.append("<td><b>Name</b></td>");
					sbEmpBankDetails.append("<td><b>Pan No.</b></td>");	//added by parvez date: 29-12-2022
					sbEmpBankDetails.append("<td><b>Account No</b></td>");
					sbEmpBankDetails.append("<td><b>Branch</b></td>");
					sbEmpBankDetails.append("<td align=\"right\"><b>Amount</b></td>");
					sbEmpBankDetails.append("</tr>");

					sbEmpBankDetails.append(sbEmpAmountBankDetails);
					sbEmpBankDetailsExcel.append(sbEmpAmountBankDetailsExcel);
					sbEmpBankUploaderExcel.append(sbEmpAmountBankUploaderExcel);
					
					sbEmpBankDetails.append("<tr>");
					sbEmpBankDetails.append("<td colspan=\"6\">&nbsp;</td>");
					sbEmpBankDetails.append("</tr>");

					sbEmpBankDetailsExcel.append(":_: :: :: :: ::TOTAL::"+ uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalAmount));
					sbEmpBankDetails.append("<tr>");
					sbEmpBankDetails.append("<td>&nbsp;</td>");
					sbEmpBankDetails.append("<td>&nbsp;</td>");
					sbEmpBankDetails.append("<td>&nbsp;</td>");
					sbEmpBankDetails.append("<td>&nbsp;</td>");
					sbEmpBankDetails.append("<td><b>TOTAL</b></td>");
					sbEmpBankDetails.append("<td align=\"right\"><b>" + uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalAmount) + "</b></td>");
					sbEmpBankDetails.append("</tr>");

					sbEmpBankDetails.append("</table>");

					strName = "BankStatement_" + nMonth + "_" + nYear;

					// pst =
					// con.prepareStatement("insert into payroll_bank_statement (statement_name, statement_body, generated_date, "
					// +
					// "generated_by, payroll_amount,bank_pay_type) values (?,?,?,?, ?,?)");
					// pst.setString(1, strName);
					// pst.setString(2,
					// strContent+""+sbEmpBankDetails.toString());
					// pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					// pst.setInt(4,
					// uF.parseToInt((String)session.getAttribute(EMPID)));
					// pst.setDouble(5,
					// uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblTotalAmount))));
					// pst.execute();
					// pst.close();
					pst = con.prepareStatement("insert into payroll_bank_statement(statement_name, statement_body, generated_date, "
						+ "generated_by, payroll_amount,bank_pay_type, statement_body_excel, bank_uploader_excel) values (?,?,?,?, ?,?,?,?)");
					pst.setString(1, strName);
					pst.setString(2, strContent + "" + sbEmpBankDetails.toString());
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(4, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setDouble(5, uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblTotalAmount))));
					pst.setInt(6, nBankPayType);
					pst.setString(7, sbEmpBankDetailsExcel.toString());
					pst.setString(8, sbEmpBankUploaderExcel.toString());
					pst.execute();
//					System.out.println("pst 3 ===>> " + pst);
					pst.close();

					pst = con.prepareStatement("select max(statement_id) as statement_id from payroll_bank_statement");
					rs = pst.executeQuery();
					while (rs.next()) {
						nMaxStatementId = rs.getInt("statement_id");
					}
					rs.close();
					pst.close();
					
					/*ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
					sheetDesign.getExcelSheetDesignDataPay(workbook, sheet, header, reportData);
					
					String directory = CF.getStrDocSaveLocation()+I_BANK_EXCEL_STMT+"/"+nMaxStatementId; 
					FileUtils.forceMkdir(new File(directory));
					
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					workbook.write(buffer);
				
					byte[] bytes = buffer.toByteArray();
					String fileName = directory+"/"+strName+".xls";
					File f = new File(fileName);
//					File f = File.("tmp", ".xls", new File(directory));
					FileOutputStream fileOuputStream = new FileOutputStream(f); 
					fileOuputStream.write(bytes);
					*/
					pst = con.prepareStatement("update payroll_bank_statement set bank_excel_sheet =? where statement_id = ?");
					pst.setString(1, strName+".xls");
					pst.setInt(2, nMaxStatementId);
					pst.executeUpdate();
					pst.close();
					
				}
				
				pst = con.prepareStatement("update payroll_generation set statement_id=?, bank_pay_type=? where emp_id in (" + sbApprovedEmployees.toString()
					+ ") and paycycle=? and month=? and year=? and paid_from =? and paid_to=?");
				pst.setInt(1, nMaxStatementId);
				pst.setInt(2, uF.parseToInt(getBankAccountType()));
				pst.setInt(3, uF.parseToInt(strPC));
				pst.setInt(4, nMonth);
				pst.setInt(5, nYear);
				pst.setDate(6, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(7, uF.getDateFormat(strD2, DATE_FORMAT));
//				System.out.println("else pst ===>> " + pst);
				pst.execute();
				pst.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	

	public String payApporvedPayroll(boolean isFullFinal, CommonFunctions CF, String strD1, String strD2, String strPC) {
		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);

			Map hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map<String, Map<String, String>> hmArearAmountMap = CF.getArearDetails(con, uF, CF, strD2);

			pst = con.prepareStatement("select * from payroll_generation where is_paid = false and paycycle = ? and paid_from =? and paid_to=? order by emp_id");
			pst.setInt(1, uF.parseToInt(strPC));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
			
			Map hmEmpSalaryDetails = new HashMap();

			while (rs.next()) {

				Map hmInner = (Map) hmEmpSalaryDetails.get(rs.getString("emp_id"));
				if (hmInner == null) hmInner = new HashMap();

				double dblTotalAmount = uF.parseToDouble((String) hmInner.get("AMOUNT"));
				if ("E".equalsIgnoreCase(rs.getString("earning_deduction"))) {
					dblTotalAmount += rs.getDouble("amount");
				} else {
					dblTotalAmount -= rs.getDouble("amount");
				}

				hmInner.put("AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalAmount));
				hmInner.put("CURR_ID", rs.getString("currency_id") + "");

				hmEmpSalaryDetails.put(rs.getString("emp_id"), hmInner);
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("update payroll_generation set is_paid = true, is_fullfinal=? where emp_id =? and paycycle =? and paid_from =? and paid_to=?");
			String arr[] = getChbxApprove();

			StringBuilder sbApprovedEmployees = new StringBuilder();

			for (int i = 0; arr != null && i < arr.length; i++) {

				sbApprovedEmployees.append(arr[i] + ",");

				pst.setBoolean(1, isFullFinal);
				pst.setInt(2, uF.parseToInt(arr[i]));
				pst.setInt(3, uF.parseToInt(strPC));
				pst.setDate(4, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.addBatch();

				Map hmInner = (Map) hmEmpSalaryDetails.get(arr[i]);
				if (hmInner == null)
					hmInner = new HashMap();
				String strCurrId = (String) hmInner.get("CURR_ID");

				Map hmInnerCurrencyDetails = (Map) hmCurrencyDetails.get(strCurrId);
				if (hmInnerCurrencyDetails == null)
					hmInnerCurrencyDetails = new HashMap();

				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_NEW_SALARY_PAID, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrEmpId(arr[i]);
				nF.setStrSalaryAmount(uF.showData((String) hmInnerCurrencyDetails.get("LONG_CURR"), "") + "" + (String) hmInner.get("AMOUNT"));
				nF.setEmailTemplate(true);
				nF.sendNotifications();

			}
			pst.executeBatch();
			pst.close();
			
			for (int i = 0; arr != null && i < arr.length; i++) {
				pst = con.prepareStatement("update payroll_generation_lta set is_paid = true,paid_by=?,paid_date=? where emp_id =? and paycycle =? and paid_from =? and paid_to=?");
				pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(3, uF.parseToInt(arr[i]));
				pst.setInt(4, uF.parseToInt(strPC));
				pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.execute();
				pst.close();
			}

			for (int i = 0; arr != null && i < arr.length; i++) {
				pst = con.prepareStatement("select * from  payroll_generation where emp_id = ? and paycycle = ? and salary_head_id = ? and is_paid= true and paid_from =? and paid_to=?");
				pst.setInt(1, uF.parseToInt(arr[i]));
				pst.setInt(2, uF.parseToInt(strPC));
				pst.setInt(3, AREARS);
				pst.setDate(4, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strD2, DATE_FORMAT));
				rs = pst.executeQuery();

				while (rs.next()) {
					double dblArearAmount = rs.getDouble("amount");

					Map hmArearMap = (Map) hmArearAmountMap.get(arr[i]);
					if (hmArearMap == null)
						hmArearMap = new HashMap();

					int nArearId = uF.parseToInt((String) hmArearMap.get("AREAR_ID"));
					double dblTotalAmount = uF.parseToDouble((String) hmArearMap.get("TOTAL_AMOUNT"));
					double dblPaidAmount = uF.parseToDouble((String) hmArearMap.get("AMOUNT_PAID"));
					double dblBalanceAmount = uF.parseToDouble((String) hmArearMap.get("AMOUNT_BALANCE"));

					dblBalanceAmount = dblTotalAmount - dblPaidAmount - dblArearAmount;
					dblPaidAmount += dblArearAmount;

					pst1 = con.prepareStatement("update arear_details set total_amount_paid=?, arear_amount_balance=?, is_paid=? where arear_id=?");

					pst1.setDouble(1, dblPaidAmount);
					pst1.setDouble(2, dblBalanceAmount);

					if (dblBalanceAmount <= 1) {
						pst1.setBoolean(3, true);
					} else {
						pst1.setBoolean(3, false);
					}
					pst1.setInt(4, nArearId);
					pst1.execute();
					pst1.close();

				}
				rs.close();
				pst.close();
			}

			sbApprovedEmployees.replace(sbApprovedEmployees.length() - 1, sbApprovedEmployees.length(), "");

			// System.out.println("sbApprovedEmployees=="+sbApprovedEmployees.toString());

			pst = con.prepareStatement("select emp_bank_name::integer, emp_per_id from employee_personal_details epd,employee_official_details eod where payment_mode = 1 and epd.emp_per_id = eod.emp_id  and epd.emp_per_id in ("
							+ sbApprovedEmployees.toString() + ") order by emp_bank_name");
			rs = pst.executeQuery();
//			System.out.println("pst====>"+pst);
			List alBanks = new ArrayList();
			String strBankNew = null;
			String strBankOld = null;
			StringBuilder sbEmpBank = new StringBuilder();
			Map hmEmpBanks = new HashMap();
			while (rs.next()) {
				strBankNew = rs.getString("emp_bank_name");
				if (!alBanks.contains(rs.getString("emp_bank_name"))) {
					alBanks.add(rs.getString("emp_bank_name"));
				}

				if (strBankNew != null && strBankNew.equalsIgnoreCase(strBankOld)) {
					sbEmpBank = new StringBuilder();
				}
				sbEmpBank.append(rs.getString("emp_per_id") + ",");

				hmEmpBanks.put(strBankNew, sbEmpBank.toString());

				strBankOld = strBankNew;
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from bank_details");
			rs = pst.executeQuery();
			Map<String, String> hmBankCode = new HashMap<String, String>();
			while (rs.next()) {
				hmBankCode.put(rs.getString("bank_id"), rs.getString("bank_code"));
			}
			rs.close();
			pst.close();

			Set set = hmEmpBanks.keySet();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String str = (String) it.next();
				String strEmp = (String) hmEmpBanks.get(str);
				strEmp = strEmp.substring(0, strEmp.length() - 1);

				pst = con.prepareStatement("select e_amount - d_amount as net_amount, month, year from (select sum(amount) as e_amount, month, year from payroll_generation where emp_id in ("
					+ strEmp + ") and paycycle = ? and paid_from =? and paid_to=? and is_paid = true and earning_deduction = 'E' group by month, year) a, (select sum(amount) as d_amount from " +
					"payroll_generation where emp_id in ("+ strEmp + ") and paycycle = ? and paid_from =? and paid_to=? and is_paid = true and earning_deduction = 'D') b ");
				pst.setInt(1, uF.parseToInt(strPC));
				pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(strPC));
				pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
				rs = pst.executeQuery();
				// System.out.println("pst=====>"+pst);

				double dblAmount = 0;
				int nMonth = 0;
				int nYear = 0;
				while (rs.next()) {
					dblAmount = uF.parseToDouble(rs.getString("net_amount"));
					nMonth = uF.parseToInt(rs.getString("month"));
					nYear = uF.parseToInt(rs.getString("year"));
				}
				rs.close();
				pst.close();

				pst = con.prepareStatement("select * from document_comm_details where document_text like '%[" + hmBankCode.get(str) + "]%' ");
				rs = pst.executeQuery();
				String strContent = null;
				String strName = null;

				// System.out.println("pst ===> "+pst);
				while (rs.next()) {
					strContent = rs.getString("document_text");
				}
				rs.close();
				pst.close();

				if (strContent != null && strContent.indexOf(DATE) >= 0) {
					strContent = strContent.replace(DATE, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, CF.getStrReportDateFormat()));
				}

				if (strContent != null && strContent.indexOf(PAYROLL_AMOUNT) >= 0) {
					strContent = strContent.replace(PAYROLL_AMOUNT, uF.formatIntoTwoDecimal(dblAmount));
				}

				if (strContent != null && strContent.indexOf(PAYROLL_AMOUNT_WORDS) >= 0) {
					strContent = strContent.replace(PAYROLL_AMOUNT_WORDS, uF.digitsToWords((int) dblAmount));
				}

				if (strContent != null && strContent.indexOf(PAY_MONTH) >= 0) {
					strContent = strContent.replace(PAY_MONTH, uF.getMonth(nMonth));
				}

				if (strContent != null && strContent.indexOf(PAY_YEAR) >= 0) {
					strContent = strContent.replace(PAY_YEAR, "" + nYear);
				}

				strName = "BankStatement_" + nMonth + "_" + nYear;

				pst = con.prepareStatement("insert into payroll_bank_statement(statement_name, statement_body, generated_date, "
					+ "generated_by, payroll_amount,bank_pay_type) values (?,?,?,?, ?,?)");
				pst.setString(1, strName);
				pst.setString(2, strContent);
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4, uF.parseToInt((String) session.getAttribute(EMPID)));
				pst.setDouble(5, uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblAmount))));
				pst.setInt(6, 1);
				pst.execute();
//				System.out.println("pst 4 ===>> " + pst);
				pst.close();

				pst = con.prepareStatement("select max(statement_id) as statement_id from payroll_bank_statement");
				rs = pst.executeQuery();
				int nMaxStatementId = 0;
				while (rs.next()) {
					nMaxStatementId = rs.getInt("statement_id");
				}
				rs.close();
				pst.close();

				pst = con.prepareStatement("update payroll_generation set statement_id=?, bank_pay_type=? where emp_id in (" + strEmp+ ") and paycycle=? and month=? and year=? and paid_from =? and paid_to=?");
				pst.setInt(1, nMaxStatementId);
				pst.setInt(2, 1);
				pst.setInt(3, uF.parseToInt(strPC));
				pst.setInt(4, nMonth);
				pst.setInt(5, nYear);
				pst.setDate(6, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(7, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.execute();
				rs.close();
				pst.close();

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;

	}

	private List<String> getPaidAndUnpaidEmpCount(UtilityFunctions uF, String strD1, String strD2, String strPC) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		List<String> alPaidPayStatus = new ArrayList<String>();
		
		try {
			
			con = db.makeConnection(con);

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(distinct(eod.emp_id)) as emp_cnt,is_paid from payroll_generation pg, employee_official_details eod where pg.emp_id = eod.emp_id and paycycle=? and paid_from =? and paid_to=? ");
        	if(getF_level()!=null && getF_level().length>0) {
        		sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
        	if(getF_grade()!=null && getF_grade().length>0) {
        		sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
            }
			
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "','") + "') ");
			}
		
			if (getF_department() != null && getF_department().length > 0) {
				sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
			}

			if (getF_service() != null && getF_service().length > 0) {
				sbQuery.append(" and (");
				for (int i = 0; i < getF_service().length; i++) {
					sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
					if (i < getF_service().length - 1) {
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" ) ");
			}
			if (getStrPaycycleDuration() != null) {
				sbQuery.append(" and pay_mode ='" + getStrPaycycleDuration() + "'");
			}
			if (uF.parseToInt(getF_paymentMode()) > 0) {
				sbQuery.append(" and pg.payment_mode =" + uF.parseToInt(getF_paymentMode()));
			}
			if (getBankBranch() != null && getBankBranch().length > 0) {
				sbQuery.append(" and eod.emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					"and ((epd.emp_bank_name is not null and epd.emp_bank_name !='' and CAST(epd.emp_bank_name AS integer) in ("+StringUtils.join(getBankBranch(), ",")+")) " +
					"or (epd.emp_bank_name2 is not null and epd.emp_bank_name2 !='' and CAST (epd.emp_bank_name2 AS integer) in ("+StringUtils.join(getBankBranch(), ",")+"))))");
			}

			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
				sbQuery.append(" and eod.wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(WLOCATION_ACCESS) != null) {
				sbQuery.append(" and eod.wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
			}

			if (uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(ORG_ACCESS) != null) {
				sbQuery.append(" and eod.org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
			}
			sbQuery.append(" group by is_paid");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strPC));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pst1====>"+pst);
			rs = pst.executeQuery();
			String strUnpaidEmpCount = "0";
			String strPaidEmpCount = "0";
			while (rs.next()) {
				if (rs.getBoolean("is_paid")) {
					strPaidEmpCount = rs.getString("emp_cnt");
				} else if (!rs.getBoolean("is_paid")) {
					strUnpaidEmpCount = rs.getString("emp_cnt");
				}
			}
			rs.close();
			pst.close();
			alPaidPayStatus.add(strPaidEmpCount);
			alPaidPayStatus.add(strUnpaidEmpCount);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return alPaidPayStatus;
	}
	

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrApprove() {
		return strApprove;
	}

	public void setStrApprove(String strApprove) {
		this.strApprove = strApprove;
	}

	public String[] getChbxApprove() {
		return chbxApprove;
	}

	public void setChbxApprove(String[] chbxApprove) {
		this.chbxApprove = chbxApprove;
	}

	public List<FillMonth> getAlMonthList() {
		return alMonthList;
	}

	public void setAlMonthList(List<FillMonth> alMonthList) {
		this.alMonthList = alMonthList;
	}

	public String getApprovePC() {
		return approvePC;
	}

	public void setApprovePC(String approvePC) {
		this.approvePC = approvePC;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
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
	public List<FillServices> getServiceList() {
		return serviceList;
	}
	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}
	public List<FillPayCycleDuration> getPaycycleDurationList() {
		return paycycleDurationList;
	}
	public void setPaycycleDurationList(List<FillPayCycleDuration> paycycleDurationList) {
		this.paycycleDurationList = paycycleDurationList;
	}
	public String getStrPaycycleDuration() {
		return strPaycycleDuration;
	}
	public void setStrPaycycleDuration(String strPaycycleDuration) {
		this.strPaycycleDuration = strPaycycleDuration;
	}
	public List<FillPayMode> getPaymentModeList() {
		return paymentModeList;
	}
	public void setPaymentModeList(List<FillPayMode> paymentModeList) {
		this.paymentModeList = paymentModeList;
	}
	public String getF_paymentMode() {
		return f_paymentMode;
	}
	public void setF_paymentMode(String f_paymentMode) {
		this.f_paymentMode = f_paymentMode;
	}
	// public String getStrD1() {
	// return strD1;
	// }
	// public void setStrD1(String strD1) {
	// this.strD1 = strD1;
	// }
	// public String getStrD2() {
	// return strD2;
	// }
	// public void setStrD2(String strD2) {
	// this.strD2 = strD2;
	// }
	// public String getStrPC() {
	// return strPC;
	// }
	// public void setStrPC(String strPC) {
	// this.strPC = strPC;
	// }

	public List<FillBank> getBankList() {
		return bankList;
	}

	public void setBankList(List<FillBank> bankList) {
		this.bankList = bankList;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
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

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getBankAccountType() {
		return bankAccountType;
	}

	public void setBankAccountType(String bankAccountType) {
		this.bankAccountType = bankAccountType;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}

	public boolean getIsBifurcatePayOut() {
		return bifurcatePayOut;
	}

	public void setBifurcatePayOut(boolean bifurcatePayOut) {
		this.bifurcatePayOut = bifurcatePayOut;
	}

	public String getPrimarySalaryHead() {
		return primarySalaryHead;
	}

	public void setPrimarySalaryHead(String primarySalaryHead) {
		this.primarySalaryHead = primarySalaryHead;
	}

	public String getSecondarySalaryHead() {
		return secondarySalaryHead;
	}

	public void setSecondarySalaryHead(String secondarySalaryHead) {
		this.secondarySalaryHead = secondarySalaryHead;
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

	public String[] getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String[] bankBranch) {
		this.bankBranch = bankBranch;
	}

	public List<FillBank> getBankBranchList() {
		return bankBranchList;
	}

	public void setBankBranchList(List<FillBank> bankBranchList) {
		this.bankBranchList = bankBranchList;
	}

	public String getStrBankBranch() {
		return strBankBranch;
	}

	public void setStrBankBranch(String strBankBranch) {
		this.strBankBranch = strBankBranch;
	}

	private HttpServletRequest request;
    HttpServletResponse response;
	

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response=response;
		
	}
}