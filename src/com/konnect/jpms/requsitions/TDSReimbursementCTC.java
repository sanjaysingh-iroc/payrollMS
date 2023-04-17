package com.konnect.jpms.requsitions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
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
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TDSReimbursementCTC extends ActionSupport  implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	
	private CommonFunctions CF;
	
	private String f_org;
	private String[] f_strWLocation;
	private String[] f_department;
	private String f_level;
	private String[] f_service;
	
	private List<FillOrganisation> orgList; 
	private List<FillWLocation> wLocationList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillServices> serviceList;
	
	private String exportType;
	
	private String financialYear;
	private List<FillFinancialYears> financialYearList;
	private String strApprove;
	private String[] reimbId; 
	private String alertID;
	
	private List<String> strEmpCTCId;
	
	private String strLocation;
	private String strDepartment;
	private String strSbu;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		request.setAttribute(PAGE, "/jsp/requisitions/TDSReimbursementCTC.jsp");
		request.setAttribute(TITLE, "TDS Reimbursement CTC");
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));

		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		String formType = (String) request.getParameter("formType");
		if(formType != null && formType.trim().equalsIgnoreCase("revoke")){
			 revokeTDSReimbursementCTC(uF);
		 } else if(formType != null && formType.trim().equalsIgnoreCase("approve")){
			 approveTDSReimbursementCTC(uF);
		 }
		
		if(uF.parseToInt(getF_org()) == 0){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		if(uF.parseToInt(getF_level())==0 && levelList!=null && levelList.size()>0){
			setF_level(levelList.get(0).getLevelId());
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
		
		viewTDSReimbursementCTC(uF);		

		return loadTDSReimbursementCTC(uF);
	}
	
	private void revokeTDSReimbursementCTC(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
				String[] strFinancialYear = getFinancialYear().split("-");
				String strFinancialYearStart = strFinancialYear[0];
				String strFinancialYearEnd = strFinancialYear[1];
				if (strFinancialYearStart != null && !strFinancialYearStart.trim().equals("") && !strFinancialYearStart.trim().equalsIgnoreCase("NULL")
						&& strFinancialYearEnd != null && !strFinancialYearEnd.trim().equals("") && !strFinancialYearEnd.trim().equalsIgnoreCase("NULL")) {
					String[] revokeEmpId = request.getParameterValues("revokeEmpId");
					int nEmpIds = revokeEmpId != null ? revokeEmpId.length : 0;
		
					if (nEmpIds > 0) {
						for (int i = 0; i < nEmpIds; i++) {
							String strEmpCTCId = revokeEmpId[i];
							String[] temp = strEmpCTCId.split("_");
							String strEmpId = temp[0];
							String strReimCTCId = temp[1];
						
							pst = con.prepareStatement("delete from reimbursement_ctc_tax_pay where emp_id=? and reimbursement_ctc_id=? " +
									"and financial_year_start=? and financial_year_end=?");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setInt(2, uF.parseToInt(strReimCTCId));
							pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
							pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
							int x = pst.executeUpdate();
							if (x > 0) {
								session.setAttribute(MESSAGE, SUCCESSM + "You have successfully Revoked." + END);
							} else {
								session.setAttribute(MESSAGE, ERRORM + "Colud not Revoke. Please,try again." + END);
							}
						}
					} else {
						session.setAttribute(MESSAGE, ERRORM + "Colud not Revoke. Please,try again." + END);
					}
				} else {
					session.setAttribute(MESSAGE, ERRORM + "Colud not Revoke. Please,try again." + END);
				}
			} else {
				session.setAttribute(MESSAGE, ERRORM + "Colud not Revoke. Please,try again." + END);
			}
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM + "Colud not Revoke. Please,try again." + END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void approveTDSReimbursementCTC(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
//			System.out.println("approve getFinancialYear()==>"+getFinancialYear());
			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
				String[] strFinancialYear = getFinancialYear().split("-");
				String strFinancialYearStart = strFinancialYear[0];
				String strFinancialYearEnd = strFinancialYear[1];
				
				if (strFinancialYearStart != null && !strFinancialYearStart.trim().equals("") && !strFinancialYearStart.trim().equalsIgnoreCase("NULL")
						&& strFinancialYearEnd != null && !strFinancialYearEnd.trim().equals("") && !strFinancialYearEnd.trim().equalsIgnoreCase("NULL")) {
				
					String[] strEmpCTCIds = request.getParameterValues("strEmpCTCId");
					int nEmpIds = strEmpCTCIds != null ? strEmpCTCIds.length : 0;
		
					if (nEmpIds > 0) {
						for (int i = 0; i < nEmpIds; i++) {
							String strEmpCTCId = strEmpCTCIds[i];
							String[] temp = strEmpCTCId.split("_");
							String strEmpId = temp[0];
							String strReimCTCId = temp[1];
							String strAmount = request.getParameter("taxableAmt_"+strEmpCTCId);
						
							pst = con.prepareStatement("insert into reimbursement_ctc_tax_pay(emp_id,reimbursement_ctc_id,amount,financial_year_start," +
									"financial_year_end,approve_by,approve_date) values(?,?,?,?, ?,?,?)");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setInt(2, uF.parseToInt(strReimCTCId));
							pst.setDouble(3, uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(strAmount))));
							pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
							pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
							pst.setInt(6, uF.parseToInt(strSessionEmpId));
							pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
							int x = pst.executeUpdate();
							if (x > 0) {
								session.setAttribute(MESSAGE, SUCCESSM + "You have successfully Approved." + END);
							} else {
								session.setAttribute(MESSAGE, ERRORM + "Colud not Approve. Please,try again." + END);
							}
						}
					} else {
						session.setAttribute(MESSAGE, ERRORM + "Colud not Approve. Please,try again." + END);
					}
				} else {
					session.setAttribute(MESSAGE, ERRORM + "Colud not Approve. Please,try again." + END);
				}
			} else {
				session.setAttribute(MESSAGE, ERRORM + "Colud not Approve. Please,try again." + END);
			}
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM + "Colud not Approve. Please,try again." + END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void viewTDSReimbursementCTC(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
		
			
			String[] strFinancialYear = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
				strFinancialYear = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			} else {
				strFinancialYear = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYear[0] + "-" + strFinancialYear[1]);

				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			if (strFinancialYear != null && strFinancialYear.length > 0) {
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
				if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
				Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
				if(hmCurrency == null) hmCurrency = new HashMap<String, Map<String,String>>();
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from (select a.*,rcd.reimbursement_name from (select sum(rcp.amount) as amount,rcp.reimbursement_ctc_id," +
						"rcp.emp_id from reimbursement_ctc_pay rcp join reimbursement_head_details rhd on rhd.reimbursement_head_id=rcp.reimbursement_head_id " +
						"join reimbursement_ctc_details rcd on rhd.reimbursement_ctc_id=rcd.reimbursement_ctc_id where rcp.financial_year_from=? " +
						"and rcp.financial_year_to=? and rhd.level_id=? and rhd.org_id=? ");
				if((getF_service()!=null && getF_service().length>0) || (uF.parseToInt(getF_level()) > 0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
					sbQuery.append(" and emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
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
	            if(uF.parseToInt(getF_level()) > 0){
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd " +
	                		"where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id ="+uF.parseToInt(getF_level())+") ");
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
	            if((getF_service()!=null && getF_service().length>0) || (uF.parseToInt(getF_level()) > 0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
					sbQuery.append(")");
				}
	            sbQuery.append(" group by rcp.reimbursement_ctc_id,rcp.emp_id) a, reimbursement_ctc_details rcd " +
	            		"where a.reimbursement_ctc_id=rcd.reimbursement_ctc_id) b, employee_personal_details epd, employee_official_details eod " +
	            		"where b.emp_id=epd.emp_per_id and b.emp_id=eod.emp_id and epd.emp_per_id=eod.emp_id order by epd.emp_fname,epd.emp_mname," +
	            		"epd.emp_lname,b.reimbursement_ctc_id");
	            pst = con.prepareStatement(sbQuery.toString());
	            pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getF_level()));
				pst.setInt(4, uF.parseToInt(getF_org()));
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				List<Map<String, String>> alTDSReimCTC = new ArrayList<Map<String,String>>(); 
				while(rs.next()){
					Map<String, String> hmReimCTCAmount = new HashMap<String, String>();
					hmReimCTCAmount.put("EMP_ID", rs.getString("emp_id"));
					hmReimCTCAmount.put("EMP_CODE", rs.getString("empcode"));
					String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					String strEmpName = rs.getString("emp_fname") + strEmpMName+" "+ rs.getString("emp_lname");
					hmReimCTCAmount.put("EMP_NAME", strEmpName);
					hmReimCTCAmount.put("REIMBURSEMENT_CTC_ID", rs.getString("reimbursement_ctc_id"));
					hmReimCTCAmount.put("REIMBURSEMENT_CTC_NAME", rs.getString("reimbursement_name"));
					hmReimCTCAmount.put("REIMBURSEMENT_AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(rs.getString("amount"))));
					
					alTDSReimCTC.add(hmReimCTCAmount);
				}
				rs.close();
				pst.close();				
				request.setAttribute("alTDSReimCTC", alTDSReimCTC);
				
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(applied_amount) as amount,emp_id,reimbursement_ctc_id  from reimbursement_ctc_applied_details " +
						"where is_approved=1 and financial_year_start=? and financial_year_end=?");
				if((getF_service()!=null && getF_service().length>0) || (uF.parseToInt(getF_level()) > 0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
					sbQuery.append(" and emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
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
	            if(uF.parseToInt(getF_level()) > 0){
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd " +
	                		"where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id ="+uF.parseToInt(getF_level())+") ");
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
	            if((getF_service()!=null && getF_service().length>0) || (uF.parseToInt(getF_level()) > 0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
					sbQuery.append(")");
				}
				sbQuery.append(" group by emp_Id,reimbursement_ctc_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
				Map<String, String> hmReimAppliedAmount = new HashMap<String, String>();
				while(rs.next()){
					hmReimAppliedAmount.put(rs.getString("emp_id")+"_"+rs.getString("reimbursement_ctc_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(rs.getString("amount"))));
				}
				rs.close();
				pst.close();
				request.setAttribute("hmReimAppliedAmount", hmReimAppliedAmount);
				
				sbQuery = new StringBuilder();
				sbQuery.append("select emp_id,reimbursement_ctc_id,amount from reimbursement_ctc_tax_pay " +
						"where financial_year_start=? and financial_year_end=?");
				if((getF_service()!=null && getF_service().length>0) || (uF.parseToInt(getF_level()) > 0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
					sbQuery.append(" and emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
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
	            if(uF.parseToInt(getF_level()) > 0){
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd " +
	                		"where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id ="+uF.parseToInt(getF_level())+") ");
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
	            if((getF_service()!=null && getF_service().length>0) || (uF.parseToInt(getF_level()) > 0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
					sbQuery.append(")");
				}
				sbQuery.append(" order by emp_Id,reimbursement_ctc_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
				Map<String, String> hmReimTaxAmount = new HashMap<String, String>();
				while(rs.next()){
					hmReimTaxAmount.put(rs.getString("emp_id")+"_"+rs.getString("reimbursement_ctc_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(rs.getString("amount"))));
				}
				rs.close();
				pst.close();
				request.setAttribute("hmReimTaxAmount", hmReimTaxAmount);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public String loadTDSReimbursementCTC(UtilityFunctions uF){
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
				
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
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				if(getF_level().equals(levelList.get(i).getLevelId())) {
					if(k==0) {
						strLevel=levelList.get(i).getLevelCodeName();
					} else {
						strLevel+=", "+levelList.get(i).getLevelCodeName();
					}
					k++;
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
		
		alFilter.add("FINANCIALYEAR");
		String[] strFinancialYears = null;
		if (getFinancialYear() != null) {
			strFinancialYears = getFinancialYear().split("-");
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		} else {
			strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		}
		hmFilter.put("FINANCIALYEAR", uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
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

	public String getStrApprove() {
		return strApprove;
	}

	public void setStrApprove(String strApprove) {
		this.strApprove = strApprove;
	}

	public String[] getReimbId() {
		return reimbId;
	}

	public void setReimbId(String[] reimbId) {
		this.reimbId = reimbId;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getF_level() {
		return f_level;
	}

	public void setF_level(String f_level) {
		this.f_level = f_level;
	}

	public List<String> getStrEmpCTCId() {
		return strEmpCTCId;
	}

	public void setStrEmpCTCId(List<String> strEmpCTCId) {
		this.strEmpCTCId = strEmpCTCId;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
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
	
}