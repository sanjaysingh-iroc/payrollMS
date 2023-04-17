package com.konnect.jpms.requsitions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.select.FillBank;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PaidUnpaidReimbursements extends ActionSupport  implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	
	private CommonFunctions CF;
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	String[] reimbId;
//	String financialYear;
	String strSelectedEmpId;
	String paycycleDate;
	String strStartDate;
	String strEndDate;
	
	List<FillOrganisation> orgList; 
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillPayCycles> paycycleList;
	List<FillBank> bankList;
	List<FillFinancialYears> financialYearList;
    List<FillEmployee> empList;
	
	String paycycle;
	String exportType;
	String bankAccount;
	String bankAccountType;
	String strApprove;
	String alertID;

	String paidStatus;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		request.setAttribute(PAGE, PPaidUnpaidReimbursements);
		request.setAttribute(TITLE, "Paid/unpaid Reimbursements");

//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView) {
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		if(getF_org()==null) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
		String[] arrDates = null;
		if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
		//	System.out.print("in if");
			arrDates = getPaycycle().split("-");
			setPaycycle(arrDates[0] + "-" + arrDates[1] + "-" + arrDates[2]);
		} else {
			//System.out.print("in else");
			arrDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
			setPaycycle(arrDates[0] + "-" + arrDates[1] + "-" + arrDates[2]);
		}
		
		if(getPaycycleDate()==null ) {
			setPaycycleDate("1");
		}
		
		//System.out.println("getStrStartDate() ===>> " + getStrStartDate()+ " -- arrDates[0] ===>> " + arrDates[0]);
		if(getStrStartDate()==null) {
			setStrStartDate(arrDates[0]);
		}
		if(getStrEndDate()==null) {
			setStrEndDate(arrDates[1]);
		}
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		if(getStrApprove()!=null && getStrApprove().equalsIgnoreCase("PAY")) {
			payReimbursement(uF);
		}
		
		if(uF.parseToInt(getPaidStatus()) == 0) {
			setPaidStatus("2");
		}
		
		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
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
		
		viewPaidUnPaidReimbursement(uF);

		return loadReimbursements(uF);
	}
	
	
	private void payReimbursement(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbApprovedReimbId = null;
			StringBuilder sbApprovedReimbParentId = null;
			boolean flag = false;
			List<String> alEmpId = new ArrayList<String>();
			for(int i = 0; getReimbId()!=null && i < getReimbId().length; i++) {
				String strRembId = getReimbId()[i];
				
				int intParentId = 0;
				pst = con.prepareStatement("select parent_id from emp_reimbursement where reimbursement_id = ?");
				pst.setInt(1, uF.parseToInt(strRembId));
				rs = pst.executeQuery();
				while(rs.next()) {
					intParentId = rs.getInt("parent_id");
				}
				rs.close();
				pst.close();
				
				if(intParentId == 0) {
					if(sbApprovedReimbId == null) {
						sbApprovedReimbId = new StringBuilder();
						sbApprovedReimbId.append(strRembId);
					} else {
						sbApprovedReimbId.append(","+strRembId);
					}
				} else {
					if(sbApprovedReimbParentId == null) {
						sbApprovedReimbParentId = new StringBuilder();
						sbApprovedReimbParentId.append(""+intParentId);
					} else {
						sbApprovedReimbParentId.append(","+intParentId);
					}
				}
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("update emp_reimbursement set ispaid=?, paid_date=?, paid_by=? where reimbursement_id >0 ");
				if(intParentId>0) {
					sbQuery.append(" and parent_id = "+intParentId);
				} else {
					sbQuery.append(" and reimbursement_id = "+strRembId);
				}
				pst = con.prepareStatement(sbQuery.toString());
				pst.setBoolean(1, true);
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
//				pst.setInt(4, uF.parseToInt(strRembId));
				int x = pst.executeUpdate();
				pst.close();
				
				int intEmpId = 0;
				if(x > 0) {
					flag = true;
				;
					pst = con.prepareStatement("select * from emp_reimbursement where reimbursement_id=?");
					pst.setInt(1, uF.parseToInt(strRembId));
					rs = pst.executeQuery();
					while(rs.next()) {
						if(!alEmpId.contains(rs.getString("emp_id"))) {
							alEmpId.add(rs.getString("emp_id"));
						}
						intEmpId = rs.getInt("emp_id");
					}
					rs.close();
					pst.close();
				}
				
				
				String reimbursCurr = request.getParameter("strRiembursCurr_"+strRembId);
				String reimbursAmt = request.getParameter("strRiembursAmt_"+strRembId);
				String exchangeRate = request.getParameter("strExchangeRate_"+strRembId);
				String exchangeAmt = request.getParameter("strExchangeAmount_"+strRembId);
				StringBuilder sbReimbIds = null;
				if(intParentId == 0) {
					if(sbReimbIds == null) {
						sbReimbIds = new StringBuilder();
						sbReimbIds.append(","+strRembId+",");
					}
				} else {
					pst = con.prepareStatement("select reimbursement_id from emp_reimbursement where parent_id = ?");
					pst.setInt(1, intParentId);
					rs = pst.executeQuery();
					while(rs.next()) {
						if(sbReimbIds == null) {
							sbReimbIds = new StringBuilder();
							sbReimbIds.append(","+rs.getString("reimbursement_id")+",");
						} else {
							sbReimbIds.append(rs.getString("reimbursement_id")+",");
						}
					}
					rs.close();
					pst.close();
				}
				
				sbQuery = new StringBuilder();
				sbQuery.append("insert into emp_reimbursement_paid_trans_details(emp_id,reimbursement_curr,reimbursement_amount,exchange_rate,exchange_amount," +
						"reimbursement_ids,parent_id,paid_by,paid_date_time) values(?,?,?,?, ?,?,?,?, ?)");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, intEmpId);
				pst.setInt(2, uF.parseToInt(reimbursCurr));
				pst.setDouble(3, uF.parseToDouble(reimbursAmt));
				pst.setDouble(4, uF.parseToDouble(exchangeRate));
				pst.setDouble(5, uF.parseToDouble(exchangeAmt));
				pst.setString(6, sbReimbIds.toString());
				pst.setInt(7, intParentId);
				pst.setInt(8, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(9, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
				pst.executeUpdate();
				pst.close();
				
			}
			
			if(flag && (sbApprovedReimbId!=null || sbApprovedReimbParentId != null)) {
				Map<String, String> hmOrg = CF.getOrgDetails(con, uF, getF_org());
				Map<String, String> hmStates = CF.getStateMap(con);
				Map<String, String> hmCountry = CF.getCountryMap(con);
				String strBankCode = null;
				String strBankName = null;
				String strBankAddress = null;
				Map<String, String> hmBankBranch = new HashMap<String, String>();
				pst = con.prepareStatement("select bd.bank_account_no, bd.branch_id, bd1.bank_name,bd.bank_branch, bd.branch_code, bd.bank_address, bd.bank_city, bd.bank_pincode, bd.bank_state_id, bd.bank_country_id from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id");
				rs = pst.executeQuery();
				while(rs.next()) {
					if(rs.getInt("branch_id")==uF.parseToInt(getBankAccount())) {
						strBankCode = rs.getString("branch_code");
						strBankName = rs.getString("bank_name");
						strBankAddress = rs.getString("bank_address")+"<br/>"+rs.getString("bank_city")+" - "+rs.getString("bank_pincode")+"<br/>"+uF.showData(hmStates.get(rs.getString("bank_state_id")), "")+", "+uF.showData(hmCountry.get(rs.getString("bank_country_id")), "");
					}
					hmBankBranch.put(rs.getString("branch_id"), rs.getString("bank_branch")+"["+rs.getString("branch_code")+"]");
				}
				rs.close();
				pst.close();
				
				double dblAmount = 0;
				double dblTotalAmount = 0;
				int nMonth = 0;
				int nYear = 0;
				int nCount = 0;
				StringBuilder sbEmpAmountBankDetails = new StringBuilder();
				if(sbApprovedReimbId != null) {
					pst = con.prepareStatement("select ep.emp_id,emp_fname,emp_mname, emp_lname, emp_bank_name, emp_bank_acct_nbr,reimbursement_amount, " +
							"paid_date,emp_bank_name2,emp_bank_acct_nbr_2,reimbursement_id from employee_personal_details epd, emp_reimbursement ep " +
							"where epd.emp_per_id = ep.emp_id and ep.parent_id = 0 and reimbursement_id in ("+sbApprovedReimbId.toString()+")");
					rs = pst.executeQuery();
//					List<String> alReimbId = new ArrayList<String>();
					while(rs.next()) {
						dblAmount = uF.parseToDouble(rs.getString("reimbursement_amount"));
						nMonth = uF.parseToInt(uF.getDateFormat(rs.getString("paid_date"), DBDATE, "MM"));
						nYear = uF.parseToInt(uF.getDateFormat(rs.getString("paid_date"), DBDATE, "yyyy"));
						
						dblTotalAmount+=dblAmount;
						
						String strBankAccNo = uF.showData(rs.getString("emp_bank_acct_nbr"),"");
						String strBankBranch = uF.showData(hmBankBranch.get(rs.getString("emp_bank_name")),"");
						if(uF.parseToInt(getBankAccountType()) == 2) {
							strBankAccNo = uF.showData(rs.getString("emp_bank_acct_nbr_2"),"");
							strBankBranch = uF.showData(hmBankBranch.get(rs.getString("emp_bank_name2")),"");
						}
						
						sbEmpAmountBankDetails.append("<tr>");
						sbEmpAmountBankDetails.append("<td><font size=\"1\">"+ ++nCount+".</font></td>");
					
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						
						sbEmpAmountBankDetails.append("<td><font size=\"1\">"+uF.showData(rs.getString("emp_fname"),"")+strEmpMName+" "+uF.showData(rs.getString("emp_lname"),"")+"</font></td>");
						sbEmpAmountBankDetails.append("<td><font size=\"1\">"+strBankAccNo+"</font></td>");
						sbEmpAmountBankDetails.append("<td><font size=\"1\">"+strBankBranch+"</font></td>");
						sbEmpAmountBankDetails.append("<td align=\"right\"><font size=\"1\">"+uF.formatIntoTwoDecimal(dblAmount)+"</font></td>");
						sbEmpAmountBankDetails.append("</tr>");
						
//						if(!alReimbId.contains(rs.getString("reimbursement_id"))) {
//							alReimbId.add(rs.getString("reimbursement_id"));
//						}					
					}
					rs.close();
					pst.close();
				}
				
				if(sbApprovedReimbParentId != null) {
					pst = con.prepareStatement("select sum(reimbursement_amount) as reimbursement_amount,ep.emp_id,emp_fname,emp_mname, emp_lname, emp_bank_name, emp_bank_acct_nbr, " +
							"paid_date,emp_bank_name2,emp_bank_acct_nbr_2,ep.parent_id from employee_personal_details epd, emp_reimbursement ep " +
							"where epd.emp_per_id = ep.emp_id and ep.parent_id > 0 and ep.parent_id in ("+sbApprovedReimbParentId.toString()+") group by ep.emp_id,emp_fname,emp_mname, emp_lname, emp_bank_name, emp_bank_acct_nbr,paid_date,emp_bank_name2,emp_bank_acct_nbr_2,ep.parent_id");
					rs = pst.executeQuery();
//					List<String> alReimbId = new ArrayList<String>();
					while(rs.next()) {
						dblAmount = uF.parseToDouble(rs.getString("reimbursement_amount"));
						nMonth = uF.parseToInt(uF.getDateFormat(rs.getString("paid_date"), DBDATE, "MM"));
						nYear = uF.parseToInt(uF.getDateFormat(rs.getString("paid_date"), DBDATE, "yyyy"));
						
						dblTotalAmount+=dblAmount;
						
						String strBankAccNo = uF.showData(rs.getString("emp_bank_acct_nbr"),"");
						String strBankBranch = uF.showData(hmBankBranch.get(rs.getString("emp_bank_name")),"");
						if(uF.parseToInt(getBankAccountType()) == 2) {
							strBankAccNo = uF.showData(rs.getString("emp_bank_acct_nbr_2"),"");
							strBankBranch = uF.showData(hmBankBranch.get(rs.getString("emp_bank_name2")),"");
						}
						
						sbEmpAmountBankDetails.append("<tr>");
						sbEmpAmountBankDetails.append("<td><font size=\"1\">"+ ++nCount+".</font></td>");
						
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						
						sbEmpAmountBankDetails.append("<td><font size=\"1\">"+uF.showData(rs.getString("emp_fname"),"")+strEmpMName+" "+uF.showData(rs.getString("emp_lname"),"")+"</font></td>");
						sbEmpAmountBankDetails.append("<td><font size=\"1\">"+strBankAccNo+"</font></td>");
						sbEmpAmountBankDetails.append("<td><font size=\"1\">"+strBankBranch+"</font></td>");
						sbEmpAmountBankDetails.append("<td align=\"right\"><font size=\"1\">"+uF.formatIntoTwoDecimal(dblAmount)+"</font></td>");
						sbEmpAmountBankDetails.append("</tr>");
						
//						if(!alReimbId.contains(rs.getString("reimbursement_id"))) {
//							alReimbId.add(rs.getString("reimbursement_id"));
//						}					
					}
					rs.close();
					pst.close();
				}
				
				String strContent = null;
				String strName = null;
				
				Map<String, String> hmActivityNode = CF.getActivityNode(con);
				if(hmActivityNode == null) hmActivityNode = new HashMap<String, String>();
				
				int nTriggerNode = uF.parseToInt(hmActivityNode.get(""+ACTIVITY_BANK_ORDER_ID));
				
				if(nMonth>0 && (sbApprovedReimbId != null || sbApprovedReimbParentId != null)) { //&& alReimbId.size() > 0
//					String strReimbIds = StringUtils.join(alReimbId.toArray(),",");
					
					pst = con.prepareStatement("select * from document_comm_details where document_text like '%["+strBankCode+"]%' " +
						"and trigger_nodes like '%,"+nTriggerNode+",%' and status=1 and org_id=? order by document_id desc limit 1");
					pst.setInt(1, uF.parseToInt(getF_org()));
					rs = pst.executeQuery();
					while(rs.next()) {
						strContent = rs.getString("document_text");
					} 
					rs.close();
					pst.close();
					
					if(strContent!=null && strContent.indexOf("["+strBankCode+"]")>=0) {
						strContent = strContent.replace("["+strBankCode+"]", strBankName +"<br/>"+strBankAddress);
					}
					
					if(strContent!=null && strContent.indexOf(DATE)>=0) {
						strContent = strContent.replace(DATE, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
					}
					
					if(strContent!=null && strContent.indexOf(PAYROLL_AMOUNT)>=0) {
						strContent = strContent.replace(PAYROLL_AMOUNT, uF.formatIntoTwoDecimal(dblTotalAmount));
					}
					
					if(strContent!=null && strContent.indexOf(PAYROLL_AMOUNT_WORDS)>=0) {
						String digitTotal="";
				        String strTotalAmt=""+dblTotalAmount;
				        if(strTotalAmt.contains(".")) {
				        	strTotalAmt=strTotalAmt.replace(".", ",");
				        	String[] temp=strTotalAmt.split(",");
				        	digitTotal=uF.digitsToWords(uF.parseToInt(temp[0]));
				        	if(uF.parseToInt(temp[1])>0) {
				        		int pamt=0;
				        		if(temp[1].length()==1) {
				        			pamt=uF.parseToInt(temp[1]+"0");
				        		} else {
				        			pamt=uF.parseToInt(temp[1]);
				        		}
				        		digitTotal+=" and "+uF.digitsToWords(pamt)+" paise";
				        	}
				        } else {
				        	int totalAmt1=(int)dblTotalAmount;
				        	digitTotal=uF.digitsToWords(totalAmt1);
				        }
				        strContent = strContent.replace(PAYROLL_AMOUNT_WORDS, digitTotal);
					}
					
					if(strContent!=null && strContent.indexOf(PAY_MONTH)>=0) {
						strContent = strContent.replace(PAY_MONTH, uF.getMonth(nMonth));
					}
					
					if(strContent!=null && strContent.indexOf(PAY_YEAR)>=0) {
						strContent = strContent.replace(PAY_YEAR, ""+nYear);
					}
					
					if(strContent!=null && strContent.indexOf(LEGAL_ENTITY_NAME)>=0) {
						strContent = strContent.replace(LEGAL_ENTITY_NAME, uF.showData(hmOrg.get("ORG_NAME"), ""));
					}
					
					
//					System.out.println("strContent ===>> " + strContent);
					if(strContent!=null && nMonth>0) {
						StringBuilder sbEmpBankDetails = new StringBuilder();
						
						sbEmpBankDetails.append("<table width=\"100%\">");
						sbEmpBankDetails.append("<tr>");
						sbEmpBankDetails.append("<td width=\"20\"><b>Sr. No.</b></td>");
						sbEmpBankDetails.append("<td><b>Name</b></td>");
						sbEmpBankDetails.append("<td><b>Account No</b></td>");
						sbEmpBankDetails.append("<td><b>Branch</b></td>");
						sbEmpBankDetails.append("<td align=\"right\"><b>Amount</b></td>");
						sbEmpBankDetails.append("</tr>");
						
						sbEmpBankDetails.append(sbEmpAmountBankDetails);
						
						sbEmpBankDetails.append("<tr>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td><b>TOTAL</b></td>");
						sbEmpBankDetails.append("<td align=\"right\"><b>"+uF.formatIntoTwoDecimal(dblTotalAmount)+"</b></td>");
						sbEmpBankDetails.append("</tr>");
						
						sbEmpBankDetails.append("</table>");
						
						strName = "BankStatement_"+nMonth+"_"+nYear;
						
						pst = con.prepareStatement("insert into payroll_bank_statement (statement_name, statement_body, generated_date, generated_by, payroll_amount) values (?,?,?,?,?)");
						pst.setString(1, strName);
						pst.setString(2, strContent+""+sbEmpBankDetails.toString());
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt(strSessionEmpId));
						pst.setDouble(5, uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblTotalAmount))));
						pst.execute();
						pst.close();
						
						pst  = con.prepareStatement("select max(statement_id) as statement_id from payroll_bank_statement");
						rs = pst.executeQuery();
						int nMaxStatementId = 0;
						while(rs.next()) {
							nMaxStatementId = rs.getInt("statement_id");
						}
						rs.close();
						pst.close();

						if(sbApprovedReimbId != null) {
							pst = con.prepareStatement("update emp_reimbursement set statement_id=? where reimbursement_id in ("+sbApprovedReimbId.toString()+")");
							pst.setInt(1, nMaxStatementId);
							pst.executeUpdate();
							pst.close();
						}
						if(sbApprovedReimbParentId != null) {
							pst = con.prepareStatement("update emp_reimbursement set statement_id=? where parent_id in ("+sbApprovedReimbParentId.toString()+")");
							pst.setInt(1, nMaxStatementId);
							pst.executeUpdate();
							pst.close();
						}
					}
				}
			}
			
			/**
			 * User Alerts
			 * */
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			for(int j = 0; j < alEmpId.size(); j++) {
				String strEmpId = alEmpId.get(j);
				String strDomain = request.getServerName().split("\\.")[0];
				
				String alertData = "<div style=\"float: left;\"> Payment, Reimbursement has been released by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
				String alertAction = "MyPay.action?pType=WR";
				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(strEmpId);
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
				userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
				userAlerts.setStatus(INSERT_WR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadReimbursements(UtilityFunctions uF) {
		paycycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
	
		empList = new FillEmployee(request).fillAllLiveEmployees(CF, strUserType, strSessionEmpId, uF.parseToInt(getF_org()));
		bankList = new FillBank(request).fillBankAccNoForDocuments(CF,uF,getF_org());
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
				
		getSelectedFilter(uF);
		
		return LOAD;
	}
	 
	private void getSelectedFilter(UtilityFunctions uF) {
		//start
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("PAID_STATUS");
		if(uF.parseToInt(getPaidStatus())==1) { 
			hmFilter.put("PAID_STATUS", "Paid");
		} else if(uF.parseToInt(getPaidStatus())==2) {
			hmFilter.put("PAID_STATUS", "UnPaid");
		} else {
			hmFilter.put("PAID_STATUS", "All");
		}

		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++) {
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
		
		
		
		
		
		if(getPaycycleDate()!=null && getPaycycleDate().equals("2")) {
			alFilter.add("EMP");
//			System.out.println("getStrSelectedEmpId() ===>> " + getStrSelectedEmpId());
			if(getStrSelectedEmpId()!=null) {
				String strEmpName="";
				for(int i=0;empList!=null && i<empList.size();i++) {
					if(getStrSelectedEmpId().equals(empList.get(i).getEmployeeId())) {
//						System.out.println("in if getStrSelectedEmpId() ===>> " + getStrSelectedEmpId());
						strEmpName = empList.get(i).getEmployeeName();
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
			
			alFilter.add("FROMTO");
			if(getStrStartDate() != null && getStrEndDate() != null) {
				hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
			}
			
		} else {
			alFilter.add("PAYCYCLE");	
			if(getPaycycle()!=null) {
				String strPayCycle="";
				int k=0;
				for(int i=0;paycycleList!=null && i<paycycleList.size();i++) {
					if(getPaycycle().equals(paycycleList.get(i).getPaycycleId())) {
						if(k==0) {
							strPayCycle=paycycleList.get(i).getPaycycleName();
						} else {
							strPayCycle+=", "+paycycleList.get(i).getPaycycleName();
						}
						k++;
					}
				}
				if(strPayCycle!=null && !strPayCycle.equals("")) {
					hmFilter.put("PAYCYCLE", strPayCycle);
				} else {
					hmFilter.put("PAYCYCLE", "All Paycycle");
				}
			}  else {
				hmFilter.put("PAYCYCLE", "All Paycycle");
			}
		}
		
		
		String selectedFilter = CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
		
	//end
	
	}
	
	public String viewPaidUnPaidReimbursement(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			String[] strPayCycleDates = null;
			if (getPaycycleDate() == null)
				{
					setPaycycleDate("1");
				}
			con = db.makeConnection(con);
			
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
			
			alInnerExport.add(new DataStyle("Reimbursement details of Employee ", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Sr.NO", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employee Code", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employee Name", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Paycycle- From Date", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Paycycle- To Date", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Expense Incurred Date", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Submitted Date", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

			alInnerExport.add(new DataStyle("Reimbursement Type",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Type", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Amount", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			alInnerExport.add(new DataStyle("No of Persons",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Mode of Travel", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Place From",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Place To",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("No of Days",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Total KM",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Currency",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Payment Mode",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Vendor",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Receipt No",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Purpose",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Attachment",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Approval Status", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Approval By", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Approval Date", Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			reportListExport.add(alInnerExport);
			
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
			if(hmEmpNames == null) hmEmpNames = new HashMap<String, String>();
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			if(hmEmpCode == null) hmEmpCode = new HashMap<String, String>();
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrency == null) hmCurrency = new HashMap<String, Map<String,String>>();
			Map<String, String> hmPaymentMode = CF.getPaymentMode();
			if(hmPaymentMode ==null) hmPaymentMode = new HashMap<String, String>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_reimbursement where emp_id>0 and approval_1=1 and approval_2=1 and parent_id=0 and ");

			if(getPaycycleDate()!=null && getPaycycleDate().equals("2")) {
				setPaycycle(getStrStartDate() + "-" + getStrEndDate() + "-0");
				strPayCycleDates = getPaycycle().split("-");
				sbQuery.append(" " + "from_date between ? and ? and to_date between ? and ?" );
				if (uF.parseToInt(getStrSelectedEmpId()) > 0) {
					sbQuery.append(" and emp_id=" + uF.parseToInt(getStrSelectedEmpId()));
				}
				
			} else {
				if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
					strPayCycleDates = getPaycycle().split("-");
				} else {
					strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
					setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
				}
				sbQuery.append(" "+"from_date=? and to_date=?");
			}
			
			if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where emp_id>0 ");
			}
			
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
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
                sbQuery.append(" ) ");
            }
            
            if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)) {
				sbQuery.append(") ");
			}
            if(uF.parseToInt(getPaidStatus()) == 2) { 
				sbQuery.append(" and ispaid="+uF.parseToBoolean("FALSE"));
			} else if(uF.parseToInt(getPaidStatus()) == 1) {
				sbQuery.append(" and ispaid="+uF.parseToBoolean("TRUE"));
			}
            sbQuery.append(" order by entry_date desc");
           
			pst = con.prepareStatement(sbQuery.toString());
			// System.out.print("pst=======>"+pst);
			 if(getPaycycleDate().equals("2"))
			 {
				
				 pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				 pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				 pst.setDate(3, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				 pst.setDate(4, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				 
				 
			 }else{
				
				 pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				 pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			 }
		//	System.out.println("pst=======>"+pst);
			rs = pst.executeQuery();
			List<List<String>> alReport = new ArrayList<List<String>>();
			List<String> reimbIds = new ArrayList<String>();
			int nCount = 0;
			double totalExpenseAmt =0.0;
			while(rs.next()) {
				String strCurrId = hmEmpCurrency.get(rs.getString("emp_id"));
				if(rs.getInt("reimb_currency") > 0) {
					strCurrId = rs.getString("reimb_currency"); 
				}
				Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
				if(hmCurrencyInner==null)hmCurrencyInner=new HashMap<String, String>();
				String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
				
				List<String> alInner = new ArrayList<String>();
				
				reimbIds.add(rs.getString("reimbursement_id"));
				
				alInner.add(rs.getString("reimbursement_id"));
				alInner.add(rs.getString("emp_id"));
				alInner.add(hmEmpCode.get(rs.getString("emp_id")));
				alInner.add(hmEmpNames.get(rs.getString("emp_id")));
				alInner.add(""+uF.parseToBoolean(rs.getString("ispaid")));
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(strCurrSymbol+ rs.getString("reimbursement_amount"));
				alInner.add(rs.getString("reimbursement_purpose"));
								
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>2) {
					String[] strDocs = rs.getString("ref_document").split(":_:");
					
					StringBuilder sbDoc = new StringBuilder();
					for (int k = 0; strDocs != null && k < strDocs.length; k++) {
						if(CF.getStrDocRetriveLocation()==null) {
							sbDoc.append("<a target=\"blank\" href=\"" + request.getContextPath()+DOCUMENT_LOCATION + strDocs[k] + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
						} else {
							sbDoc.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_REIMBURSEMENTS+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ strDocs[k] + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
						}
					}
					alInner.add(sbDoc.toString());
				} else {
					alInner.add("");
				}
				
				alInner.add("&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"viewReimbursmentDetails("+ rs.getString("emp_id") + ","+ rs.getString("reimbursement_id") + ");\">View Details</a> "); //9
				alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("reimbursement_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>"); //10
				if(!uF.parseToBoolean(rs.getString("ispaid"))) {
					alInner.add(" <div id=\"myDiv" + nCount + "\"><a href=\"javascript:void(0)\" onclick=\"cancelReimbursement('"+rs.getString("reimbursement_id")+"','myDiv" + nCount + "');\">Cancel</a> </div>"); //11
				} else if(rs.getInt("cancel_by") > 0 && rs.getString("cancel_reason") != null && !rs.getString("cancel_reason").equals("")) {
					alInner.add(" <div><a href=\"javascript:void(0)\" onclick=\"viewCancelReason('"+rs.getString("reimbursement_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\">Cancel Reason</a> </div>"); //11
				} else {
					alInner.add("");
				}
				alInner.add(rs.getString("reimbursement_amount"));
				alInner.add(strCurrId);
				alInner.add(""); //parentId
				
				alReport.add(alInner);
				nCount++;
				
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle(uF.showData(String.valueOf(nCount),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((String)hmEmpCode.get(rs.getString("emp_id")),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(hmEmpNames.get(rs.getString("emp_id")),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((rs.getString("reimb_from_date") != null) ? uF.getDateFormat(rs.getString("reimb_from_date"), DBDATE, CF.getStrReportDateFormat()) : "-",""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((rs.getString("entry_date") != null) ? uF.getDateFormat(rs.getString("reimb_from_date"), DBDATE, CF.getStrReportDateFormat()) : "-",""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(rs.getString("reimbursement_info"),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(rs.getString("reimbursement_type1"),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("reimbursement_amount"))),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				
			
				String strApprovalStatus = "";
				String strApprovalBy = "";
				String strApprovalDate = "";
				
				if (rs.getInt("approval_1") == -1) {
					strApprovalBy = hmEmpNames.get(rs.getString("approval_1_emp_id"));
					strApprovalDate = rs.getString("approval_1_date");
					strApprovalStatus = "Denied";
				} else if (rs.getInt("approval_1") == 0) {
					strApprovalStatus = "Pending";
				} else if (rs.getInt("approval_1") == 1) {
					strApprovalBy = hmEmpNames.get(rs.getString("approval_1_emp_id"));
					strApprovalDate = rs.getString("approval_1_date");
					strApprovalStatus = "Approved";
				} else if (rs.getInt("approval_1") == -2) {
					strApprovalBy = hmEmpNames.get(rs.getString("cancel_by"));
					strApprovalDate = rs.getString("cancel_date");
					strApprovalStatus = "Canceled";
				}
				
//				totalExpenseAmt += uF.parseToDouble(rs.getString("reimbursement_amount"));
				totalExpenseAmt += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("reimbursement_amount"))));
				
				String days =Integer.toString(rs.getInt("no_days"));
				String km =Double.toString(rs.getDouble("travel_distance"));
				String payment_mode = Integer.toString(rs.getInt("reimb_payment_mode"));
				Map<String, String> hmCurrInn = hmCurrency.get(rs.getString("reimb_currency"));
				if(hmCurrInn==null) hmCurrInn = new HashMap<String, String>();
				String no_person = Integer.toString(rs.getInt("no_person"));
			
				alInnerExport.add(new DataStyle(uF.showData(no_person,""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((rs.getString("travel_mode")),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((rs.getString("travel_from")),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((rs.getString("travel_to")),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(days,""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(km,""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(hmCurrInn.get("SHORT_CURR"),""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(hmPaymentMode.get(payment_mode),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((rs.getString("vendor")),""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((rs.getString("receipt_no")),""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((rs.getString("reimbursement_purpose")),""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				String strAttachStatus = "No";
				if(rs.getString("ref_document") !=null && !rs.getString("ref_document").equals("")) {
					strAttachStatus = "Yes";
				}
				alInnerExport.add(new DataStyle(uF.showData(strAttachStatus, ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(strApprovalStatus, ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(strApprovalBy,""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((strApprovalDate != null && !strApprovalDate.equals("")) ? uF.getDateFormat(strApprovalDate, DBDATE, CF.getStrReportDateFormat()) : "", ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				reportListExport.add(alInnerExport);
				
			}
			rs.close();
			pst.close();
			Map<String, Map<String, String>> hmReimbIdPaidData = new LinkedHashMap<String, Map<String,String>>();
			if(reimbIds != null && !reimbIds.equals("")) {
				for (int i=0; i<reimbIds.size(); i++) {
					sbQuery = new StringBuilder();
					sbQuery.append("select * from emp_reimbursement_paid_trans_details where reimbursement_ids like ',"+reimbIds.get(i)+",'");
					pst = con.prepareStatement(sbQuery.toString());
	//				System.out.println("pst=======>"+pst);
					rs = pst.executeQuery();
					while(rs.next()) {
						Map<String, String> hmInner = new HashMap<String, String>();
						hmInner.put("EXCHANGE_RATE", rs.getString("exchange_rate"));
						hmInner.put("EXCHANGE_AMOUNT", rs.getString("exchange_amount"));
						
						hmReimbIdPaidData.put(reimbIds.get(i), hmInner);
					}
					rs.close();
					pst.close();
				}
			}
			request.setAttribute("hmReimbIdPaidData", hmReimbIdPaidData);
			
			
			List<String> reimbParentIds = new ArrayList<String>();
			sbQuery = new StringBuilder();
		
			sbQuery.append("select * from emp_reimbursement where emp_id>0 and approval_1=1 and approval_2=1 and parent_id>0 and ");
			if(getPaycycleDate()!=null && getPaycycleDate().equals("2")) {
				
				setPaycycle(getStrStartDate() + "-" + getStrEndDate() + "-0");
				strPayCycleDates = getPaycycle().split("-");
				/*if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
					strPayCycleDates = getFinancialYear().split("-");*/
					
				/*else {
					strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
					setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
					
				}*/
				
				sbQuery.append(" " + "from_date between ? and ? and to_date between ? and ?" );
				if (uF.parseToInt(getStrSelectedEmpId()) > 0) {
					sbQuery.append(" and emp_id = " + uF.parseToInt(getStrSelectedEmpId()));
				}
				
				} else {
					if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
						strPayCycleDates = getPaycycle().split("-");
						
					} else {
						strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
						setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
					}
					sbQuery.append(" "+"from_date =? and to_date =?");
				}
		
			if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where emp_id>0 ");
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
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
            	sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
            
            if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)) {
				sbQuery.append(") ");
			}
            if(uF.parseToInt(getPaidStatus()) == 2) { 
				sbQuery.append(" and ispaid="+uF.parseToBoolean("FALSE"));
			} else if(uF.parseToInt(getPaidStatus()) == 1) {
				sbQuery.append(" and ispaid="+uF.parseToBoolean("TRUE"));
			}
            sbQuery.append(" order by entry_date desc");
			pst = con.prepareStatement(sbQuery.toString());
	
			 if(getPaycycleDate().equals("2"))
			 {
				
				 pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				 pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				 pst.setDate(3, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				 pst.setDate(4, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				}else{
				
				 pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				 pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			 }
			
		//	 System.out.println("pst222=======>"+pst);
			 rs = pst.executeQuery();
			 Map<String, Map<String, String>> hmBulkExpenseData = new LinkedHashMap<String, Map<String,String>>();
			StringBuilder sbDoc = new StringBuilder();
			while(rs.next()) {
				
				Map<String, String> hmInner = hmBulkExpenseData.get(rs.getString("parent_id"));
				if(hmInner == null)	{
					hmInner = new HashMap<String, String>();
					sbDoc = new StringBuilder();
				}
				
				if (rs.getString("ref_document") != null && rs.getString("ref_document").length()>2) {
					String[] strDocs = rs.getString("ref_document").split(":_:");
//					StringBuilder sbDoc = new StringBuilder();
					for (int k = 0; strDocs != null && k < strDocs.length; k++) {
						if(CF.getStrDocRetriveLocation()==null) {
							sbDoc.append("<a target=\"blank\" href=\"" + request.getContextPath()+DOCUMENT_LOCATION + strDocs[k] + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
						} else {
							sbDoc.append("<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() + I_REIMBURSEMENTS+"/"+I_DOCUMENT+"/"+rs.getString("emp_id") +"/"+ strDocs[k] + "\" class=\"viewattach\" title=\"View Attachment\" ></a>");
						}
					}
				}
				
				
				String strReimbursementAmout = hmInner.get("REIMBURSEMENT_AMOUNT");
				double dblReimbursementAmout = uF.parseToDouble(rs.getString("reimbursement_amount")) + uF.parseToDouble(strReimbursementAmout);
				
				String strCurrId = hmEmpCurrency.get(rs.getString("emp_id"));
				if(rs.getInt("reimb_currency") > 0) {
					strCurrId = rs.getString("reimb_currency"); 
				}
				Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
				if(hmCurrencyInner==null)hmCurrencyInner=new HashMap<String, String>();
				String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
				
				if(!reimbParentIds.contains(rs.getString("parent_id"))) {
					reimbParentIds.add(rs.getString("parent_id"));
				}
				
				hmInner.put("REIMBURSEMENT_ID", rs.getString("reimbursement_id"));
				hmInner.put("EMP_ID", rs.getString("emp_id"));
				hmInner.put("EMP_CODE", hmEmpCode.get(rs.getString("emp_id")));
				hmInner.put("EMP_NAME", hmEmpNames.get(rs.getString("emp_id")));
				hmInner.put("IS_PAID", ""+uF.parseToBoolean(rs.getString("ispaid")));
				hmInner.put("APPLIED_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("REIMBURSEMENT_CURR_AMOUNT", strCurrSymbol+" "+dblReimbursementAmout);
				hmInner.put("REIMBURSEMENT_DOCUMENTS", sbDoc.toString());
				String strViewDetails = "<a href=\"javascript:void(0)\" onclick=\"viewBulkExpenseDetails("+ rs.getString("emp_id") + ","+ rs.getString("parent_id") + ");\">View Details</a>";
				hmInner.put("REIMBURSEMENT_VIEW_DETAILS", strViewDetails);
				String strView = "<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("reimbursement_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>";
				hmInner.put("REIMBURSEMENT_VIEW_WORKFLOW", strView);
				if(!uF.parseToBoolean(rs.getString("ispaid"))) {
					String strCancel = "<div id=\"myDiv" + nCount + "\"><a href=\"javascript:void(0)\" onclick=\"cancelReimbursement('"+rs.getString("reimbursement_id")+"','myDiv" + nCount + "');\">Cancel</a> </div>";
					if(rs.getString("cancel_by") != null && rs.getInt("cancel_by")>0) {
						strCancel = "<div><a href=\"javascript:void(0)\" onclick=\"viewCancelReason('"+rs.getString("reimbursement_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\">Cancel Reason</a> </div>";
					}
					hmInner.put("REIMBURSEMENT_CANCEL", strCancel);
				} else {
					String strCancelReason = "-";
					if(rs.getString("cancel_by") != null && rs.getInt("cancel_by")>0) {
						strCancelReason = "<div><a href=\"javascript:void(0)\" onclick=\"viewCancelReason('"+rs.getString("reimbursement_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"');\">Cancel Reason</a> </div>";
					}
					hmInner.put("REIMBURSEMENT_CANCEL", strCancelReason);
				}
				hmInner.put("REIMBURSEMENT_AMOUNT", ""+dblReimbursementAmout);
				hmInner.put("REIMBURSEMENT_CURRENCY", strCurrId);
				hmBulkExpenseData.put(rs.getString("parent_id"), hmInner);
			
//				totalExpenseAmt += uF.parseToDouble(rs.getString("reimbursement_amount"));
				totalExpenseAmt += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("reimbursement_amount"))));
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle(uF.showData(String.valueOf(nCount),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((String)hmEmpCode.get(rs.getString("emp_id")),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(hmEmpNames.get(rs.getString("emp_id")),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((rs.getString("reimb_from_date") != null) ? uF.getDateFormat(rs.getString("reimb_from_date"), DBDATE, CF.getStrReportDateFormat()) : "-",""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((rs.getString("entry_date") != null) ? uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()) : "-",""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));				
				alInnerExport.add(new DataStyle(uF.showData(rs.getString("reimbursement_info"),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(rs.getString("reimbursement_type1"),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("reimbursement_amount"))),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			
				String strApprovalStatus = "";
				String strApprovalBy = "";
				String strApprovalDate = "";
				if (rs.getInt("approval_1") == -1) {
					strApprovalBy = hmEmpNames.get(rs.getString("approval_1_emp_id"));
					strApprovalDate = rs.getString("approval_1_date");
					strApprovalStatus = "Denied";
				} else if (rs.getInt("approval_1") == 0) {
					strApprovalStatus = "Pending";
				} else if (rs.getInt("approval_1") == 1) {
					strApprovalBy = hmEmpNames.get(rs.getString("approval_1_emp_id"));
					strApprovalDate = rs.getString("approval_1_date");
					strApprovalStatus = "Approved";
				} else if (rs.getInt("approval_1") == -2) {
					strApprovalBy = hmEmpNames.get(rs.getString("cancel_by"));
					strApprovalDate = rs.getString("cancel_date");
					strApprovalStatus = "Canceled";
				}
				String days =Integer.toString(rs.getInt("no_days"));
				String km =Double.toString(rs.getDouble("travel_distance"));
				String payment_mode = Integer.toString(rs.getInt("reimb_payment_mode"));
				Map<String, String> hmCurrInn = hmCurrency.get(rs.getString("reimb_currency"));
				String no_person = Integer.toString(rs.getInt("no_person"));
				alInnerExport.add(new DataStyle(uF.showData(no_person,""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((rs.getString("travel_mode")),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((rs.getString("travel_from")),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((rs.getString("travel_to")),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(days,""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(km,""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(hmCurrInn.get("SHORT_CURR"),""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(hmPaymentMode.get(payment_mode),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((rs.getString("vendor")),""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((rs.getString("receipt_no")),""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((rs.getString("reimbursement_purpose")),""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				String strAttachStatus = "No";
				if(rs.getString("ref_document") !=null && !rs.getString("ref_document").equals("")) {
					strAttachStatus = "Yes";
				}
				alInnerExport.add(new DataStyle(uF.showData(strAttachStatus, ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				
				alInnerExport.add(new DataStyle(uF.showData(strApprovalStatus, ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData(strApprovalBy,""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((strApprovalDate != null && !strApprovalDate.equals("")) ? uF.getDateFormat(strApprovalDate, DBDATE, CF.getStrReportDateFormat()) : "", ""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				reportListExport.add(alInnerExport);
				
			}
			rs.close();
			pst.close();
			
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));				
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("Total Amount", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle(uF.showData(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), totalExpenseAmt),""), Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			alInnerExport.add(new DataStyle("", Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			
			reportListExport.add(alInnerExport);
			
			Iterator<String> it = hmBulkExpenseData.keySet().iterator();
			while(it.hasNext()) {
				String parentId = it.next();
				Map<String, String> hmInner = hmBulkExpenseData.get(parentId);
				List<String> innerList = new ArrayList<String>();
				innerList.add(hmInner.get("REIMBURSEMENT_ID"));
				innerList.add(hmInner.get("EMP_ID"));
				innerList.add(hmInner.get("EMP_CODE"));
				innerList.add(hmInner.get("EMP_NAME"));
				innerList.add(hmInner.get("IS_PAID"));
				innerList.add(hmInner.get("APPLIED_DATE"));
				innerList.add(hmInner.get("REIMBURSEMENT_CURR_AMOUNT"));
				innerList.add("");
				innerList.add(hmInner.get("REIMBURSEMENT_DOCUMENTS"));
				innerList.add(hmInner.get("REIMBURSEMENT_VIEW_DETAILS"));
				innerList.add(hmInner.get("REIMBURSEMENT_VIEW_WORKFLOW"));
				innerList.add(hmInner.get("REIMBURSEMENT_CANCEL"));
				innerList.add(hmInner.get("REIMBURSEMENT_AMOUNT"));
				innerList.add(hmInner.get("REIMBURSEMENT_CURRENCY"));
				innerList.add(parentId); //parentId 14
				
				alReport.add(innerList);
			}
			Map<String, Map<String, String>> hmReimbParentIdPaidData = new LinkedHashMap<String, Map<String,String>>();
			if(reimbParentIds != null && !reimbParentIds.equals("")) {
				for (int i=0; i<reimbParentIds.size(); i++) {
					sbQuery = new StringBuilder();
					
					sbQuery.append("select * from emp_reimbursement_paid_trans_details where parent_id ="+reimbParentIds.get(i));
					pst = con.prepareStatement(sbQuery.toString());
				//	System.out.println("pst=======>"+pst);
					rs = pst.executeQuery();
					while(rs.next()) {
						Map<String, String> hmInner = new HashMap<String, String>();
						hmInner.put("EXCHANGE_RATE", rs.getString("exchange_rate"));
						hmInner.put("EXCHANGE_AMOUNT", rs.getString("exchange_amount"));
						
						hmReimbParentIdPaidData.put(reimbParentIds.get(i), hmInner);
					}
					rs.close();
					pst.close();
				}
			}
			request.setAttribute("hmReimbParentIdPaidData", hmReimbParentIdPaidData);
			session.setAttribute("reportListExport", reportListExport);
			
			request.setAttribute("alReport", alReport);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
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

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getBankAccountType() {
		return bankAccountType;
	}

	public void setBankAccountType(String bankAccountType) {
		this.bankAccountType = bankAccountType;
	}

	public List<FillBank> getBankList() {
		return bankList;
	}

	public void setBankList(List<FillBank> bankList) {
		this.bankList = bankList;
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


	public String getPaidStatus() {
		return paidStatus;
	}


	public void setPaidStatus(String paidStatus) {
		this.paidStatus = paidStatus;
	}
	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}
	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}
	
	public List<FillEmployee> getEmpList() {
		return empList;
	}
	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String getStrSelectedEmpId() {
		return strSelectedEmpId;
	}

	public void setStrSelectedEmpId(String strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}

	public String getPaycycleDate() {
		return paycycleDate;
	}

	public void setPaycycleDate(String paycycleDate) {
		this.paycycleDate = paycycleDate;
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
	

}
