package com.konnect.jpms.requsitions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillFinancialYears;
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

public class ReimbursementCTC extends ActionSupport  implements ServletRequestAware, IStatements {
	
	
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null; 
	
	private CommonFunctions CF;
	
	private String f_org;
//	private String strf_WLocation;
	private String strSelectedEmpId;
	private String f_financialYear;
	
	private String strLocation;
	private String strDepartment;
	private String strSbu;
	private String strLevel;
	private String strGrade;
	
	private String[] strf_WLocation;
	private String[] f_level;
	private String[] f_department;
	private String[] f_service;
	private String[] f_grade;
	
	private List<FillOrganisation> organisationList;
	private List<FillWLocation> wLocationList;
	private List<FillEmployee> empNamesList;
	private List<FillFinancialYears> financialYearList; 
	
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillServices> serviceList;
	private List<FillGrade> gradeList;
	
	String alertID;
	public String execute() {	    
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		request.setAttribute(PAGE, "/jsp/requisitions/ReimbursementCTC.jsp");
		request.setAttribute(TITLE, "Reimbursement Part of CTC");
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(uF.parseToInt(getF_org()) <= 0) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setStrf_WLocation(getStrLocation().split(","));
		} else {
			setStrf_WLocation(null);
		}
		
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			if(strUserType != null && strUserType.equalsIgnoreCase(OTHER_HR) && session.getAttribute(DEPARTMENTID) != null){
				String[] deptArr = {(String)session.getAttribute(DEPARTMENTID)};
				setF_department(deptArr);
			} else{
				setF_department(null);
			}
			
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
		
		if(getStrGrade() != null && !getStrGrade().equals("")) {
			setF_grade(getStrGrade().split(","));
		} else {
			setF_grade(null);
		}
		
		if (getF_financialYear() == null || getF_financialYear().trim().equals("") || getF_financialYear().trim().equalsIgnoreCase("NULL") || getF_financialYear().trim().length() == 0) {
			String[]  strFinancialYear = new FillFinancialYears(request).fillLatestFinancialYears();
			setF_financialYear(strFinancialYear[0] + "-" + strFinancialYear[1]);
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
			gradeList = new FillGrade(request).fillGrade(level_id, getF_org());
		} else {
			gradeList = new FillGrade(request).fillGradebyorganisation(getF_org());
		}
		
		loadReimbursementCTC(uF);
		
		if(strUserType != null && strUserType.equalsIgnoreCase(EMPLOYEE)){
			viewReimbursementCTCEmp(uF);
		} else if(strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(OTHER_HR) || strUserType.equalsIgnoreCase(ACCOUNTANT))){
			viewReimbursementCTC(uF);
		}
		return LOAD;
	}

	private void viewReimbursementCTCEmp(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			if (getF_financialYear() != null && !getF_financialYear().trim().equals("") && !getF_financialYear().trim().equalsIgnoreCase("NULL")) {				
				String[] strFinancialYear = getF_financialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}

			con = db.makeConnection(con);
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			String orgId = CF.getEmpOrgId(con, uF, strSessionEmpId);
			String levelId = CF.getEmpLevelId(con, strSessionEmpId);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			
			pst = con.prepareStatement("select * from reimbursement_ctc_details where level_id=? and org_id=? and reimbursement_ctc_id in " +
					"(select reimbursement_ctc_id from reimbursement_head_details where level_id=? and org_id=? and reimbursement_head_id in " +
					"(select reimbursement_head_id from reimbursement_head_amt_details where financial_year_start=? and financial_year_end=?))" +
					" order by reimbursement_name");
			pst.setInt(1, uF.parseToInt(levelId));
			pst.setInt(2, uF.parseToInt(orgId));
			pst.setInt(3, uF.parseToInt(levelId));
			pst.setInt(4, uF.parseToInt(orgId));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmReimbursementCTCInner = new HashMap<String, String>();
			while(rs.next()){
				hmReimbursementCTCInner.put(rs.getString("reimbursement_ctc_id"), uF.showData(rs.getString("reimbursement_name"), ""));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select rhd.reimbursement_ctc_id,rhd.reimbursement_head_id,rhd.reimbursement_head_code,rhd.reimbursement_head_name," +
					"rhad.reimbursement_head_amt_id,rhad.amount,rhad.is_attachment,rhad.is_optimal from reimbursement_head_details rhd, " +
					"reimbursement_head_amt_details rhad where rhd.reimbursement_head_id=rhad.reimbursement_head_id and rhd.level_id=? " +
					"and rhd.org_id=? and rhad.financial_year_start=? and rhad.financial_year_end=?");
			pst.setInt(1, uF.parseToInt(levelId));
			pst.setInt(2, uF.parseToInt(orgId));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmReimbursementCTCHead = new HashMap<String, Map<String,String>>(); 
			while(rs.next()){
				Map<String, String> hmReimCTCHeadInner = new HashMap<String, String>();
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_ID", rs.getString("reimbursement_head_id"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_CODE", rs.getString("reimbursement_head_code"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_NAME", rs.getString("reimbursement_head_name"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_AMT_ID", rs.getString("reimbursement_head_amt_id"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_AMOUNT", rs.getString("amount"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_IS_ATTACHMENT", rs.getString("is_attachment"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_IS_OPTIMAL", rs.getString("is_optimal"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_CTC_NAME",uF.showData(hmReimbursementCTCInner.get(rs.getString("reimbursement_ctc_id")), ""));
				
				hmReimbursementCTCHead.put(rs.getString("reimbursement_head_id"),hmReimCTCHeadInner);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM reimbursement_ctc_applied_details where emp_id=? and financial_year_start=? " +
					"and financial_year_end=? and emp_id in (select eod.emp_id from employee_personal_details epd, " +
					"employee_official_details eod where eod.emp_id=? and eod.org_id=? and eod.grade_id in (select gd.grade_id " +
					"from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id " +
					"and dd.level_id = ld.level_id and ld.level_id=?)) order by entry_date desc");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, uF.parseToInt(orgId));
			pst.setInt(6, uF.parseToInt(levelId));
			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			List<List<String>> alReport = new ArrayList<List<String>>(); 
			int nCount=0;
			while(rs.next()){
				List<String> alInner = new ArrayList<String>();
				StringBuilder sb = new StringBuilder();
				sb.append("<div style=\"float:left;width:20px;margin-top:1px\" id=\"myDiv"+nCount+"\">");
				if(rs.getInt("is_approved")==0) {
					 /*sb.append("<a href=\"javascript:void();\" onclick=\"((confirm('Are You sure you want to cancel this request?'))?getContent('myDiv"+nCount+"', 'UpdateRequest.action?S=2&RID="+rs.getString("reim_ctc_applied_id")+"&T=REIMBURSEMENTCTC&M=D'):'')\"><img src=\""+request.getContextPath()+"/images1/icons/pending.png\" title=\"Waiting for approval, click to pullout\" border=\"0\"/></a>");*/
					sb.append("<a href=\"javascript:void();\" onclick=\"((confirm('Are You sure you want to cancel this request?'))?getContent('myDiv"+nCount+"', 'UpdateRequest.action?S=2&RID="+rs.getString("reim_ctc_applied_id")+"&T=REIMBURSEMENTCTC&M=D'):'')\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for approval, click to pullout\"></i></a>");
				} else if(rs.getInt("is_approved")==1) {
					/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
				} else if(rs.getInt("is_approved")==-1) {
					/*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
					
				}
				sb.append("</div>");
				
				sb.append("<div style=\"float:left;width:80%;\">");
				
				Map<String, String> hmReimCTCHeadInner = (Map<String, String>)hmReimbursementCTCHead.get(rs.getString("reimbursement_head_id")) ;
				if(hmReimCTCHeadInner == null) hmReimCTCHeadInner = new HashMap<String, String>();
				
				sb.append("Your request for reimbursement ctc for "+uF.showData(hmReimCTCHeadInner.get("REIMBURSEMENT_HEAD_NAME"), "N/A")+
						" on "+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat())
						+" specifying "+"\""+uF.showData(rs.getString("description"), "")+"\"");
				
				if(rs.getInt("is_approved")== -1) {
					sb.append(" has been denied by "+uF.showData(hmEmpNames.get(rs.getString("approved_by")), "")+" ["+uF.showData(hmUserTypeMap.get(rs.getString("approver_user_type_id")), "")+"]");
				} else if(rs.getInt("is_approved")== 0) {
					sb.append(" is waiting for your approval");
				} else if(rs.getInt("is_approved")== 1) {
					sb.append(" is approved by "+uF.showData(hmEmpNames.get(rs.getString("approved_by")), "")+" ["+uF.showData(hmUserTypeMap.get(rs.getString("approver_user_type_id")), "")+"] on "+ uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()));
				} 
				
				String strChecked = "";
				if(uF.parseToBoolean(rs.getString("is_nontaxable"))){
					strChecked = "checked";
				}
				 
				sb.append("<div style=\"width: 90%;\"><strong>Reimbursement Details:</strong>" +
						"<table class=\"table table_no_border\"><thead>" +
						"<tr>" +
						"<th style=\"text-align: left;\">Reimbursement Head &nbsp </th>" +
						"<th style=\"text-align: left;\">Reimbursement CTC &nbsp </th>" +
						"<th style=\"text-align: left;\">Applied Amount &nbsp </th>" +
						"<th style=\"text-align: left;\">Amount &nbsp </th>" +
						/*"<th style=\"text-align: left;\">Non-Taxable</th>" +*/
						"</tr>" +
						"</thead>" +
						"<tbody>" +
						"<tr>" +
						"<td>"+uF.showData(hmReimCTCHeadInner.get("REIMBURSEMENT_HEAD_NAME"),"N/A")+" &nbsp </td>" +
						"<td>"+uF.showData(hmReimCTCHeadInner.get("REIMBURSEMENT_CTC_NAME"), "N/A")+" &nbsp</td>" +
						"<td>"+uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("applied_amount")))+" &nbsp</td>" +
						"<td>"+uF.formatIntoOneDecimal(uF.parseToDouble(uF.showData(hmReimCTCHeadInner.get("REIMBURSEMENT_HEAD_AMOUNT"), "0")))+" &nbsp </td>" +
						/*"<td><input type=\"checkbox\" name=\"nonTaxable\" id=\"nonTaxable_"+rs.getString("reim_ctc_applied_id")+"\" value=\""+rs.getString("reim_ctc_applied_id")+"\" "+strChecked+"/></td>" +*/
						"</tr>" +
						"</tbody>" +
						"</table>" +
						"</div>");
				
				sb.append("</div>");
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>2) {
					if(CF.getStrDocRetriveLocation()==null) {
						sb.append("<a target=\"blank\" href=\"" +request.getContextPath()+ DOCUMENT_LOCATION + rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					} else {
						sb.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation()+ I_REIMBURSEMENTS +"/"+I_REIMBURSEMENTS_CTC_HEAD+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					}
				}
				
				alInner.add(sb.toString());
				
				alReport.add(alInner);
				nCount++;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReport", alReport);
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void viewReimbursementCTC(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			if (getF_financialYear() != null && !getF_financialYear().trim().equals("") && !getF_financialYear().trim().equalsIgnoreCase("NULL")) {				
				String[] strFinancialYear = getF_financialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			con = db.makeConnection(con);
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			
			pst = con.prepareStatement("select * from reimbursement_ctc_details where org_id=? and reimbursement_ctc_id in " +
					"(select reimbursement_ctc_id from reimbursement_head_details where org_id=? and reimbursement_head_id in " +
					"(select reimbursement_head_id from reimbursement_head_amt_details where financial_year_start=? and financial_year_end=?))" +
					" order by reimbursement_name");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getF_org()));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmReimbursementCTCInner = new HashMap<String, String>();
			while(rs.next()){
				hmReimbursementCTCInner.put(rs.getString("reimbursement_ctc_id"), uF.showData(rs.getString("reimbursement_name"), ""));
			}
			rs.close();
			pst.close();
//			System.out.println("hmSalaryHead====>"+hmSalaryHead);
			
			pst = con.prepareStatement("select rhd.reimbursement_ctc_id,rhd.reimbursement_head_id,rhd.reimbursement_head_code,rhd.reimbursement_head_name," +
					"rhad.reimbursement_head_amt_id,rhad.amount,rhad.is_attachment,rhad.is_optimal from reimbursement_head_details rhd, " +
					"reimbursement_head_amt_details rhad where rhd.reimbursement_head_id=rhad.reimbursement_head_id and rhd.org_id=? " +
					"and rhad.financial_year_start=? and rhad.financial_year_end=?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmReimbursementCTCHead = new HashMap<String, Map<String,String>>(); 
			while(rs.next()){
				Map<String, String> hmReimCTCHeadInner = new HashMap<String, String>();
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_ID", rs.getString("reimbursement_head_id"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_CODE", rs.getString("reimbursement_head_code"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_NAME", rs.getString("reimbursement_head_name"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_AMT_ID", rs.getString("reimbursement_head_amt_id"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_AMOUNT", rs.getString("amount"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_IS_ATTACHMENT", rs.getString("is_attachment"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_IS_OPTIMAL", rs.getString("is_optimal"));
				hmReimCTCHeadInner.put("REIMBURSEMENT_CTC_NAME",uF.showData(hmReimbursementCTCInner.get(rs.getString("reimbursement_ctc_id")), ""));
				
				hmReimbursementCTCHead.put(rs.getString("reimbursement_head_id"),hmReimCTCHeadInner);
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery.append("SELECT * FROM reimbursement_ctc_applied_details where financial_year_start=? and financial_year_end=? " +
					"and emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod where eod.emp_id > 0 ");
			if(uF.parseToInt(getStrSelectedEmpId())>0) {
				 sbQuery.append(" and eod.emp_id="+uF.parseToInt(getStrSelectedEmpId()));
			} else {
		//===Ajinkya		
				/*if(uF.parseToInt(getStrf_WLocation())>0) {
	                sbQuery.append(" and wlocation_id="+uF.parseToInt(getStrf_WLocation()));
	            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}*/
				
				if(getStrf_WLocation() != null && getStrf_WLocation().length>0) {
	                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getStrf_WLocation(), ",") +") ");
	            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") ");
				}
				
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
				
				if (getF_department() != null && getF_department().length > 0) {
					sbQuery.append(" and eod.depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
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
		//===end		
			}
            
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(") order by entry_date desc");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			List<List<String>> alReport = new ArrayList<List<String>>(); 
			int nCount=0;
			while(rs.next()){
				List<String> alInner = new ArrayList<String>();
				StringBuilder sb = new StringBuilder();
				sb.append("<div style=\"float:left;width:20px;margin-top:1px\" id=\"myDiv"+nCount+"\">");
				if(rs.getInt("is_approved")==0) {
					 /*sb.append("<img src=\""+request.getContextPath()+"/images1/icons/pending.png\" title=\"Waiting for approval\" border=\"0\"/>");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\"></i>");
					
				} else if(rs.getInt("is_approved")==1) {
					/*sb.append("<img c src=\"images1/icons/approved.png\" border=\"0\" />"); */
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
					
				} else if(rs.getInt("is_approved")==-1) {
					/*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
				}
				sb.append("</div>");
				
				sb.append("<div style=\"float:left;width:80%;\">");
				
				Map<String, String> hmReimCTCHeadInner = (Map<String, String>)hmReimbursementCTCHead.get(rs.getString("reimbursement_head_id"));
				if(hmReimCTCHeadInner == null) hmReimCTCHeadInner = new HashMap<String, String>();
				
				sb.append(uF.showData(hmEmpNames.get(rs.getString("emp_id")), "")+" request for reimbursement ctc for "+uF.showData(hmReimCTCHeadInner.get("REIMBURSEMENT_HEAD_NAME"), "N/A")+
						" on "+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat())
						+" specifying "+"\""+uF.showData(rs.getString("description"), "")+"\"");
				
				if(rs.getInt("is_approved")== -1) {
					sb.append(" has been denied by "+uF.showData(hmEmpNames.get(rs.getString("approved_by")), "")+" ["+uF.showData(hmUserTypeMap.get(rs.getString("approver_user_type_id")), "")+"]");
				} else if(rs.getInt("is_approved")== 0) {
					sb.append(" is waiting for approval");
				} else if(rs.getInt("is_approved")== 1) {
					sb.append(" is approved by "+uF.showData(hmEmpNames.get(rs.getString("approved_by")), "")+" ["+uF.showData(hmUserTypeMap.get(rs.getString("approver_user_type_id")), "")+"] on "+ uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()));
				} 
				
				String strChecked = "";
				if(rs.getInt("is_approved") == 0){
					strChecked = "checked";
				} else if(uF.parseToBoolean(rs.getString("is_nontaxable"))){
					strChecked = "checked";
				}
				
				sb.append("<div style=\"width: 90%;\"><strong>Reimbursement Details:</strong>" +
						"<table class=\"display\"><thead>" +
						"<tr>" +
						"<th style=\"text-align: left;\">Reimbursement Head &nbsp </th>" +
						"<th style=\"text-align: left;\">Reimbursement CTC &nbsp </th>" +
						"<th style=\"text-align: left;\">Applied Amount &nbsp </th>" +
						"<th style=\"text-align: left;\">Amount &nbsp </th>" +
						/*"<th style=\"text-align: left;\">Non-Taxable</th>"+");*/
						"");
				if(rs.getInt("is_approved") == 0) {
					sb.append("<th style=\"text-align: left;\">Action</th>");
				}
				sb.append("</tr>" +
						"</thead>" +
						"<tbody>" +
						"<tr>" +
						"<td>"+uF.showData(hmReimCTCHeadInner.get("REIMBURSEMENT_HEAD_NAME"), "N/A")+" &nbsp</td>" +
						"<td>"+uF.showData(hmReimCTCHeadInner.get("REIMBURSEMENT_CTC_NAME"), "N/A")+"&nbsp</td>" +
						"<td>"+uF.parseToDouble(rs.getString("applied_amount"))+"</td>" +
						"<td>"+uF.showData(hmReimCTCHeadInner.get("REIMBURSEMENT_HEAD_AMOUNT"), "0")+" &nbsp </td>" +
						/*"<td><input type=\"checkbox\" name=\"nonTaxable\" id=\"nonTaxable_"+rs.getString("reim_ctc_applied_id")+"\" value=\""+rs.getString("reim_ctc_applied_id")+"\" "+strChecked+"/>" +*/
						"</td>");
				if(rs.getInt("is_approved") == 0) {
					sb.append("<td><a href=\"javascript:void(0);\" onclick=\"approveDeny('1','"+rs.getString("reim_ctc_applied_id")+"','"+strUserTypeId+"');\">"
							+ "<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" title=\"Approve\"></i></a></a> ");
					sb.append("&nbsp;<a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','"+rs.getString("reim_ctc_applied_id")+"','"+strUserTypeId+"');\">" 
							+ "<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Denied\"></i></a> " +
							"</td>");
				}
				sb.append("</tr>" +
						"</tbody>" +
						"</table>" +
						"</div>");
				
				sb.append("</div>");
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>2) {
					if(CF.getStrDocRetriveLocation()==null) {
						sb.append("<a target=\"blank\" href=\"" +request.getContextPath()+ DOCUMENT_LOCATION + rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					} else {
						sb.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_REIMBURSEMENTS+"/"+I_REIMBURSEMENTS_CTC_HEAD+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					}
				}
				
				alInner.add(sb.toString());
				
				alReport.add(alInner);
				nCount++;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReport", alReport);
			
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadReimbursementCTC(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		empNamesList = getEmpList(uF);
		
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if((strUserType != null && !strUserType.equals(MANAGER) && !strUserType.equals(EMPLOYEE))) {
			alFilter.add("ORGANISATION");
			if(getF_org()!=null) {
				String strOrg="";
				for(int i=0;organisationList!=null && i<organisationList.size();i++) {
					if(getF_org().equals(organisationList.get(i).getOrgId())) {
						strOrg=organisationList.get(i).getOrgName();
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
			
			/*alFilter.add("LOCATION");
			if(getStrf_WLocation()!=null) {
				String strLocation="";
				for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
					if(getStrf_WLocation().equals(wLocationList.get(i).getwLocationId())) {
						strLocation=wLocationList.get(i).getwLocationName();
					}
				}
				if(strLocation!=null && !strLocation.equals("")) {
					hmFilter.put("LOCATION", strLocation);
				} else {
					hmFilter.put("LOCATION", "All Locations");
				}
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}*/
			
			alFilter.add("LOCATION");
			if (getStrf_WLocation() != null) {
				String strLocation = "";
				int k = 0;
				for (int i = 0; wLocationList != null && i < wLocationList.size(); i++) {
					for (int j = 0; j < getStrf_WLocation().length; j++) {
						if (getStrf_WLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
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
			
			alFilter.add("EMP");
			if(getStrSelectedEmpId()!=null) {
				String strEmpName="";
				
				for(int i=0;empNamesList!=null && i<empNamesList.size();i++) {
					
					if(getStrSelectedEmpId().equals(empNamesList.get(i).getEmployeeId())) {
						strEmpName=empNamesList.get(i).getEmployeeCode();
					}
				}
				
				if(strEmpName!=null && !strEmpName.equals("")) {
					hmFilter.put("EMP", strEmpName);
				} else {
					hmFilter.put("EMP", "All Employee");
				}
			} else {
				hmFilter.put("EMP", "All Employee");
			}
			
		}

		if (getF_financialYear() != null && !getF_financialYear().trim().equals("") && !getF_financialYear().trim().equalsIgnoreCase("NULL")) {	
			alFilter.add("FINANCIALYEAR");
			String[] strFinancialYear = getF_financialYear().split("-");
			hmFilter.put("FINANCIALYEAR", uF.getDateFormat(strFinancialYear[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strFinancialYear[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	private List<FillEmployee> getEmpList(UtilityFunctions uF) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			if (getF_financialYear() != null && !getF_financialYear().trim().equals("") && !getF_financialYear().trim().equalsIgnoreCase("NULL")) {				
				String[] strFinancialYear = getF_financialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and epd.is_delete=false ");
			
			/*if(getStrf_WLocation()!=null && getStrf_WLocation().length()>0) {
	            sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getStrf_WLocation()));
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}*/
			
			if(getStrf_WLocation()!=null && getStrf_WLocation().length>0) {
	            sbQuery.append(" and eod.wlocation_id in ( "+ StringUtils.join(getStrf_WLocation(), ",") +") ");
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            
       //====Ajinkya
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
            
            if (getF_department() != null && getF_department().length > 0) {
				sbQuery.append(" and eod.depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
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
       //===end     
			
            sbQuery.append(" and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) ");
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst1==>"+pst);
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				
				String strEmpMName = "";
				
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
			
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") 
						+ " ["+ rsEmpCode.getString("empcode") + "]"));
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
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	/*public String getStrf_WLocation() {
		return strf_WLocation;
	}

	public void setStrf_WLocation(String strf_WLocation) {
		this.strf_WLocation = strf_WLocation;
	}*/

	public String getStrSelectedEmpId() {
		return strSelectedEmpId;
	}

	public void setStrSelectedEmpId(String strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillEmployee> getEmpNamesList() {
		return empNamesList;
	}

	public void setEmpNamesList(List<FillEmployee> empNamesList) {
		this.empNamesList = empNamesList;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public String getF_financialYear() {
		return f_financialYear;
	}

	public void setF_financialYear(String f_financialYear) {
		this.f_financialYear = f_financialYear;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
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

	public String[] getF_grade() {
		return f_grade;
	}

	public void setF_grade(String[] f_grade) {
		this.f_grade = f_grade;
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

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public String[] getStrf_WLocation() {
		return strf_WLocation;
	}

	public void setStrf_WLocation(String[] strf_WLocation) {
		this.strf_WLocation = strf_WLocation;
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
	
}