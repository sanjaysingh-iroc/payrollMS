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

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PerkInSalary extends ActionSupport  implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null; 
	
	private CommonFunctions CF;
	
	private String f_org;
	private String strf_WLocation;
	private String strSelectedEmpId;
	private String f_financialYear;
	private String strMonth;
	private String strWLocation;
	
	private List<FillOrganisation> organisationList;
	private List<FillWLocation> wLocationList;
	private List<FillEmployee> empNamesList;
	private List<FillFinancialYears> financialYearList; 
	
	
	public String execute() {	    
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		request.setAttribute(PAGE, "/jsp/requisitions/PerkInSalary.jsp");
		request.setAttribute(TITLE, "Perk in Salary");
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if (getF_financialYear() == null || getF_financialYear().trim().equals("") || getF_financialYear().trim().equalsIgnoreCase("NULL") || getF_financialYear().trim().length() == 0) {
			String[]  strFinancialYear = new FillFinancialYears(request).fillLatestFinancialYears();
			setF_financialYear(strFinancialYear[0] + "-" + strFinancialYear[1]);
		}

		if(uF.parseToInt(getF_org()) > 0) {
			setF_org((String)session.getAttribute(ORGID));
		} 
		
		if(strUserType != null && strUserType.equalsIgnoreCase(EMPLOYEE)){
			viewPerkInSalaryEmp(uF);
		} else if(strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT))){
			viewPerkInSalary(uF);
		}
		return loadPerkInSalary(uF); 
	}


	private void viewPerkInSalaryEmp(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			if (getF_financialYear() != null) {				
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
			
			pst = con.prepareStatement("select * from salary_details where is_align_with_perk=true and level_id in (select level_id " +
			"from level_details where org_id=? and level_id=?) and (is_delete is null or is_delete=false)");
			pst.setInt(1, uF.parseToInt(orgId));
			pst.setInt(2, uF.parseToInt(levelId));
//			System.out.println("pst1====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmSalaryHead = new HashMap<String, String>(); 
			while (rs.next()){
				hmSalaryHead.put(rs.getString("salary_head_id"),rs.getString("salary_head_name"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmSalaryHead====>"+hmSalaryHead);
			
			pst = con.prepareStatement("SELECT * FROM perk_salary_details where org_id=? and level_id=? and financial_year_start=? and financial_year_end=?");
			pst.setInt(1, uF.parseToInt(orgId));
			pst.setInt(2, uF.parseToInt(levelId));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst2====>"+pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmPerkAlignSalary = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				Map<String, String> hmPerkSalary = new HashMap<String, String>();
				hmPerkSalary.put("PERK_SALARY_ID",rs.getString("perk_salary_id"));
				hmPerkSalary.put("PERK_CODE",uF.showData(rs.getString("perk_code"), ""));
				hmPerkSalary.put("PERK_NAME",uF.showData(rs.getString("perk_name"), ""));
				hmPerkSalary.put("PERK_DESCRIPTION",uF.showData(rs.getString("perk_description"), ""));
				hmPerkSalary.put("PERK_AMOUNT",uF.showData(rs.getString("amount"), ""));
				hmPerkSalary.put("PERK_ATTACHMENT",uF.showYesNo(rs.getString("is_attachment")));
				hmPerkSalary.put("PERK_SALARY_HEAD_ID",uF.showData(rs.getString("salary_head_id"), ""));
				hmPerkSalary.put("PERK_SALARY_HEAD",uF.showData(hmSalaryHead.get(rs.getString("salary_head_id")), ""));
				
				hmPerkAlignSalary.put(rs.getString("perk_salary_id"), hmPerkSalary);
			}
			rs.close();
			pst.close();
//			System.out.println("hmPerkAlignSalary====>"+hmPerkAlignSalary);
			
			pst = con.prepareStatement("SELECT * FROM perk_salary_applied_details where emp_id=? and financial_year_start=? " +
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
//			System.out.println("pst3=====>"+pst);
			rs = pst.executeQuery();
			List<List<String>> alReport = new ArrayList<List<String>>(); 
			int nCount=0;
			while(rs.next()){
				List<String> alInner = new ArrayList<String>();
				StringBuilder sb = new StringBuilder();
				sb.append("<div style=\"float:left;width:20px;margin-top:1px\" id=\"myDiv"+nCount+"\">");
				if(rs.getInt("is_approved")==0) {
					 /*sb.append("<img src=\""+request.getContextPath()+"/images1/icons/pending.png\" title=\"Waiting for approval, click to pullout\" border=\"0\" onclick=\"getContent('myDiv"+nCount+"', 'UpdateRequest.action?S=2&RID="+rs.getString("perk_salary_applied_id")+"&T=PERKSALARY&M=D')\" />");*/
					sb.append("<a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+nCount+"', 'UpdateRequest.action?S=2&RID="+rs.getString("perk_salary_applied_id")+"&T=PERKSALARY&M=D')\" ><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for approval, click to pullout\"></i> </a>");
					
				} else if(rs.getInt("is_approved")==1) {
					/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\" ></i>");
				} else if(rs.getInt("is_approved")==-1) {
					/*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
				}
				sb.append("</div>");
				
				sb.append("<div style=\"float:left;width:80%;\">");
				
				Map<String, String> hmPerkSalary = (Map<String, String>)hmPerkAlignSalary.get(rs.getString("perk_salary_id")) ;
				if(hmPerkSalary == null) hmPerkSalary = new HashMap<String, String>();
				
				sb.append("Your request for perk in salary for "+uF.showData(hmPerkSalary.get("PERK_NAME"), "N/A")+
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
				 
				sb.append("<div style=\"width: 90%;\"><strong>Perk Details:</strong>" +
						"<table class=\"table table-bordered\"><thead>" +
						"<tr>" +
						"<th style=\"text-align: left;\">Perk in Salary</th>" +
						"<th style=\"text-align: left;\">Salary Head</th>" +
						"<th style=\"text-align: left;\">Applied Amount</th>" +
						"<th style=\"text-align: left;\">Amount</th>" +
						"<th style=\"text-align: left;\">Non-Taxable</th>" +
						"</tr>" +
						"</thead>" +
						"<tbody>" +
						"<tr>" +
						"<td>"+uF.showData(hmPerkSalary.get("PERK_NAME"), "N/A")+"</td>" +
						"<td>"+uF.showData(hmPerkSalary.get("PERK_SALARY_HEAD"), "N/A")+"</td>" +
						"<td>"+uF.parseToDouble(rs.getString("applied_amount"))+"</td>" +
						"<td>"+uF.showData(hmPerkSalary.get("PERK_AMOUNT"), "0")+"</td>" +
						"<td><input type=\"checkbox\" name=\"nonTaxable\" id=\"nonTaxable_"+rs.getString("perk_salary_applied_id")+"\" value=\""+rs.getString("perk_salary_applied_id")+"\" "+strChecked+"/></td>" +
						"</tr>" +
						"</tbody>" +
						"</table>" +
						"</div>");
				
				sb.append("</div>");
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>2) {
					if(CF.getStrDocRetriveLocation()==null) {
						sb.append("<a target=\"blank\" href=\"" +request.getContextPath()+ DOCUMENT_LOCATION + rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
					} else {
						sb.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_PERKS+"/"+I_PERKS_SALARY+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
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

	private void viewPerkInSalary(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			if (getF_financialYear() != null) {				
				String[] strFinancialYear = getF_financialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
//			String[] strPayCycleDates = null;
//			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
//				strPayCycleDates = getPaycycle().split("-");
//			}
//			String strD1 = strPayCycleDates[0];
//			String strD2 = strPayCycleDates[1];
//			String strPC = strPayCycleDates[2];
			
			con = db.makeConnection(con);
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			String orgId = CF.getEmpOrgId(con, uF, strSessionEmpId);
			pst = con.prepareStatement("select * from salary_details where is_align_with_perk=true and level_id in (select level_id " +
			"from level_details where org_id=?) and (is_delete is null or is_delete=false)");
			pst.setInt(1, uF.parseToInt(orgId));
//			System.out.println("Ghr pst1====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmSalaryHead = new HashMap<String, String>(); 
			while (rs.next()){
				hmSalaryHead.put(rs.getString("salary_head_id"),rs.getString("salary_head_name"));
			}
			rs.close();
			pst.close();
//			System.out.println("ghr hmSalaryHead====>"+hmSalaryHead);
			
			pst = con.prepareStatement("SELECT * FROM perk_salary_details where org_id=? and financial_year_start=? and financial_year_end=?");
			pst.setInt(1, uF.parseToInt(orgId));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("ghr pst2====>"+pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmPerkAlignSalary = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				Map<String, String> hmPerkSalary = new HashMap<String, String>();
				hmPerkSalary.put("PERK_SALARY_ID",rs.getString("perk_salary_id"));
				hmPerkSalary.put("PERK_CODE",uF.showData(rs.getString("perk_code"), ""));
				hmPerkSalary.put("PERK_NAME",uF.showData(rs.getString("perk_name"), ""));
				hmPerkSalary.put("PERK_DESCRIPTION",uF.showData(rs.getString("perk_description"), ""));
				hmPerkSalary.put("PERK_AMOUNT",uF.showData(rs.getString("amount"), ""));
				hmPerkSalary.put("PERK_ATTACHMENT",uF.showYesNo(rs.getString("is_attachment")));
				hmPerkSalary.put("PERK_SALARY_HEAD_ID",uF.showData(rs.getString("salary_head_id"), ""));
				hmPerkSalary.put("PERK_SALARY_HEAD",uF.showData(hmSalaryHead.get(rs.getString("salary_head_id")), ""));
				
				hmPerkAlignSalary.put(rs.getString("perk_salary_id"), hmPerkSalary);
			}
			rs.close();
			pst.close();
			
//			System.out.println("GHR hmPerkAlignSalary==>"+hmPerkAlignSalary);
			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery.append("SELECT * FROM perk_salary_applied_details where financial_year_start=? " +
					"and financial_year_end=? and emp_id in (select eod.emp_id from employee_personal_details epd, " +
					"employee_official_details eod where eod.emp_id > 0 ");
			if(uF.parseToInt(getStrSelectedEmpId())>0) {
				 sbQuery.append(" and eod.emp_id="+uF.parseToInt(getStrSelectedEmpId()));
			} else {
				if(uF.parseToInt(getStrf_WLocation())>0) {
	                sbQuery.append(" and wlocation_id="+uF.parseToInt(getStrf_WLocation()));
	            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
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
//			System.out.println("ghr pst3=====>"+pst);
			rs = pst.executeQuery();
			List<List<String>> alReport = new ArrayList<List<String>>(); 
			int nCount=0;
			while(rs.next()){
				List<String> alInner = new ArrayList<String>();
				StringBuilder sb = new StringBuilder();
				sb.append("<div style=\"float:left;width:20px;margin-top:1px\" id=\"myDiv"+nCount+"\">");
				if(rs.getInt("is_approved")==0) {
					/*sb.append("<img src=\""+request.getContextPath()+"/images1/icons/pending.png\" title=\"Waiting for approval\" border=\"0\"/>");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for approval\" ></i>");
					
				} else if(rs.getInt("is_approved")==1) {
					/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
				} else if(rs.getInt("is_approved")==-1) {
					/*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
					
				}
				sb.append("</div>");
				
				sb.append("<div style=\"float:left;width:80%;\">");
				
				Map<String, String> hmPerkSalary = (Map<String, String>)hmPerkAlignSalary.get(rs.getString("perk_salary_id")) ;
				if(hmPerkSalary == null) hmPerkSalary = new HashMap<String, String>();
				
				sb.append(uF.showData(hmEmpNames.get(rs.getString("emp_id")), "")+" request for perk in salary for "+uF.showData(hmPerkSalary.get("PERK_NAME"), "N/A")+
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
				
				sb.append("<div style=\"width: 90%;\"><strong>Perk Details:</strong>" +
						"<table class=\"table\"><thead>" +
						"<tr>" +
						"<th style=\"text-align: left;\">Perk in Salary</th>" +
						"<th style=\"text-align: left;\">Salary Head</th>" +
						"<th style=\"text-align: left;\">Applied Amount</th>" +
						"<th style=\"text-align: left;\">Amount</th>" +
						"<th style=\"text-align: left;\">Non-Taxable</th>");
				if(rs.getInt("is_approved") == 0) {
					sb.append("<th style=\"text-align: left;\">Action</th>");
				}
				sb.append("</tr>" +
						"</thead>" +
						"<tbody>" +
						"<tr>" +
						"<td>"+uF.showData(hmPerkSalary.get("PERK_NAME"), "N/A")+"</td>" +
						"<td>"+uF.showData(hmPerkSalary.get("PERK_SALARY_HEAD"), "N/A")+"</td>" +
						"<td>"+uF.parseToDouble(rs.getString("applied_amount"))+"</td>" +
						"<td>"+uF.showData(hmPerkSalary.get("PERK_AMOUNT"), "0")+"</td>" +
						"<td><input type=\"checkbox\" name=\"nonTaxable\" id=\"nonTaxable_"+rs.getString("perk_salary_applied_id")+"\" value=\""+rs.getString("perk_salary_applied_id")+"\" "+strChecked+"/>" +
						"</td>");
				if(rs.getInt("is_approved") == 0) {
					sb.append("<td><a href=\"javascript:void(0);\" onclick=\"approveDeny('1','"+rs.getString("perk_salary_applied_id")+"','"+strUserTypeId+"');\">"
							+ "<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" title=\"Approved\"></i></a></a> ");
					sb.append("&nbsp;<a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','"+rs.getString("perk_salary_applied_id")+"','"+strUserTypeId+"');\">" 
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
						sb.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_PERKS+"/"+I_PERKS_SALARY+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ rs.getString("ref_document") + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
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


	public String loadPerkInSalary(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			System.out.println("Wlocationadmin"+wLocationList.size());
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
			System.out.println("Wlocation"+wLocationList.size());
		}
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		empNamesList = getEmpList(uF);
		
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
			
			alFilter.add("LOCATION");
//			System.out.println("==>location"+getStrf_WLocation());
			
			if(getStrf_WLocation()!=null) {
				String strLocation="";
				for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
					if(getStrf_WLocation().equals(wLocationList.get(i).getwLocationId())) {
						strLocation=wLocationList.get(i).getwLocationName();
//						System.out.println("==>location Name"+strLocation);
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

		if (getF_financialYear() != null) {	
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

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id ");
			
			if(getStrf_WLocation()!=null && getStrf_WLocation().length()>0) {
	            sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getStrf_WLocation()));
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst==>"+pst);
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

	public String getStrf_WLocation() {
		return strf_WLocation;
	}

	public void setStrf_WLocation(String strf_WLocation) {
		this.strf_WLocation = strf_WLocation;
	}

	public String getStrSelectedEmpId() {
		return strSelectedEmpId;
	}

	public void setStrSelectedEmpId(String strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
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


	public String getStrWLocation() {
		return strWLocation;
	}


	public void setStrWLocation(String strWLocation) {
		this.strWLocation = strWLocation;
	}
	
}
