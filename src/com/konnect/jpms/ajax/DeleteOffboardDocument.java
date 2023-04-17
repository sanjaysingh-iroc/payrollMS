 package com.konnect.jpms.ajax;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.payroll.ApprovePayroll;
import com.konnect.jpms.payroll.PayPayroll;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.EncryptionUtility;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;

public class DeleteOffboardDocument implements IStatements, ServletRequestAware { 

	public HttpSession session;
	String strSessionEmpId;
	CommonFunctions CF;
	
	private String paycycle;
	private String id;
	private int docid;
	private String userId;
	private String resignId;
	private String status; 
	private String operation;
	private String comment;
	private String element;
	private String amount;
	private String feedbackformRemark;
	private String feedbackFormRating;
//===start parvez date: 15-04-2022===
	private String fromPage;
//===end parvez date: 15-04-2022===
	
	public String execute() throws Exception {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions); 
		if (CF == null)
			return "login";
		UtilityFunctions uF = new UtilityFunctions();
//		EncryptionUtility eU = new EncryptionUtility();
		
//		System.out.println("getOperation() ==>> " + getOperation()+ " -- getComment() ==>> " + getComment());
		if (getOperation().equals("A")) {
			deleteOffboard();
		} else if (getOperation().equals("B")) {
			updateOffboardComment();
		} else if (getOperation().equals("C")) {
			approveOffboard();
		} else if (getOperation().equals("D")) {
			return getCommentByDoc();
		} else if (getOperation().equals("E")) {
			return updateApproved();
//		} else if (getOperation().equals("F")) {
//			sendDocument();
		} else if (getOperation().equals("G")) {
			approveExemption();
		} else if (getOperation().equals("H")) {
			return approvedSalary();
		} else if (getOperation().equals("I")) {
			approvedEarningDeduction();
		} else if (getOperation().equals("J")) {
			closeUserAccount();
			/*if(uF.parseToInt(getId()) > 0) {
				String encodeId = eU.encode(getId());
				setId(encodeId);
			}
			if(uF.parseToInt(getResignId()) > 0) {
				String encodeResignId = eU.encode(getResignId());
				setResignId(encodeResignId);
			}*/
			return "reSend";
		}
		return "success";

	}

	private void closeUserAccount() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		try {

			con = db.makeConnection(con); 
			pst = con.prepareStatement("select * from emp_off_board where emp_id=? and approved_1=? and approved_2=?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, 1);
			pst.setInt(3, 1);
			rs = pst.executeQuery();
			java.sql.Date date = null;
			boolean flag = false;
			while (rs.next()) {
				date = rs.getDate("last_day_date");
				flag = true;
			}
			rs.close();
			pst.close();

			if(flag){
				pst = con.prepareStatement("update employee_personal_details set is_alive=?,employment_end_date=? where emp_per_id=? ");
				pst.setBoolean(1, false);
				pst.setDate(2, date);
				pst.setInt(3, uF.parseToInt(getId()));
				int x = pst.executeUpdate();
				pst.close();
				
				if(x > 0){
					pst = con.prepareStatement("update user_details set status = ? where emp_id = ? ");
					pst.setString(1, "INACTIVE");
					pst.setInt(2, uF.parseToInt(getId()));
					int y = pst.executeUpdate();
					pst.close();
					
					if(y > 0){
						pst = con.prepareStatement("delete from leave_register1 where emp_id=? and _date>?");
						pst.setInt(1, uF.parseToInt(getId()));
						pst.setDate(2, date);
						pst.executeUpdate();
						pst.close();
					}
				}
				
			}
				
//			String activity_id = null;
//			String strReason = null;
//			if(uF.parseToInt(getElement()) == 1) {
//				activity_id = ACTIVITY_EXIT_FEEDBACK_FORM_APPROVAL_ID;
//				strReason = "Exit feedback form approved";
//			} else if(uF.parseToInt(getElement()) == 2) {
//				activity_id = ACTIVITY_HANDOVER_DOCUMENTS_APPROVAL_ID;
//				strReason = "Handover documents approved";
//			}
//			
//			insertEmpActivity(con, ""+getId(), CF, strSessionEmpId, activity_id, strReason);
			
//			if(uF.parseToInt(getElement()) == 1) {
//				session.setAttribute(MESSAGE, SUCCESSM+"Feedback form section has been Approved."+END);
//			} else if(uF.parseToInt(getElement()) == 2) {
//				session.setAttribute(MESSAGE, SUCCESSM+"Handover Documents section has been Approved."+END);
//			}
//			request.setAttribute("STATUS_MSG", "Feedback form section has been Approved");

			request.setAttribute("STATUS_MSG", "Account Closed Successfully.");
			
		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String approvedEarningDeduction() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
//		EncryptionUtility eU = new EncryptionUtility();
		CommonFunctions CF = (CommonFunctions) session.getAttribute(CommonFunctions);

		try {
			
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];

			con = db.makeConnection(con);
						
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String, String> hmdetails = new HashMap<String, String>();
			pst = con.prepareStatement("select payment_mode,paycycle_duration from employee_official_details where emp_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmdetails.put("PAYCYCLE_DURATION", rs.getString("paycycle_duration"));
				hmdetails.put("PAYCYCLE_MODE", rs.getString("payment_mode"));

			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from payroll_generation  where emp_id=? and paycycle=(select max(paycycle) from payroll_generation where emp_id=? ) limit 1");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmdetails.put("PAID_FROM", rs.getString("paid_from"));
				hmdetails.put("PAID_TO", rs.getString("paid_to"));
				hmdetails.put("MONTH", rs.getString("month"));
				hmdetails.put("YEAR", rs.getString("year"));
				hmdetails.put("PAYCYCLE", rs.getString("paycycle"));

			}
			rs.close();
			pst.close();
						
			pst = con.prepareStatement("insert into payroll_generation(emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle,financial_year_from_date,financial_year_to_date,"
							+ "	pay_mode,currency_id,service_id,is_paid,earning_deduction,paid_from,paid_to,payment_mode,is_fullfinal)values"
							+ "	  (?,?,?,current_date,current_date,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(hmdetails.get("MONTH")));
			pst.setInt(3, uF.parseToInt(hmdetails.get("YEAR")));
			if (getComment().equals("ACCRUED")) {
				pst.setInt(4, -2);
			} else {
				pst.setInt(4, -1);
			}
			pst.setDouble(5, uF.parseToDouble(getAmount()));
			pst.setInt(6, uF.parseToInt(hmdetails.get("PAYCYCLE")));
			pst.setDate(7, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(9, hmdetails.get("PAYCYCLE_DURATION"));
			pst.setInt(10, uF.parseToInt(hmEmpCurrency.get(getId() + "")));
			pst.setInt(11, 0);
			pst.setBoolean(12, true);
			if (getComment().equals("ACCRUED")) {
				pst.setString(13, "E");
			} else {
				pst.setString(13, "D");
			}

			pst.setDate(14, uF.getDateFormat(hmdetails.get("PAID_FROM"), DBDATE));
			pst.setDate(15, uF.getDateFormat(hmdetails.get("PAID_TO"), DBDATE));
			pst.setInt(16, uF.parseToInt(hmdetails.get("PAYCYCLE_MODE")));
			pst.setBoolean(17, true);
			pst.execute();
			pst.close();

			request.setAttribute("STATUS_MSG", "Approved");
		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		/*if(uF.parseToInt(getId()) > 0) {
			String encodeId = eU.encode(getId());
			setId(encodeId);
		}
		if(uF.parseToInt(getResignId()) > 0) {
			String encodeResignId = eU.encode(getResignId());
			setResignId(encodeResignId);
		}*/
		return "reSend";
	}

	public String approvedSalary() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		CommonFunctions CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		UtilityFunctions uF = new UtilityFunctions();
//		EncryptionUtility eU = new EncryptionUtility();
		try {

			con = db.makeConnection(con);
//			String paycycleType = null;
//			pst = con.prepareStatement("select paycycle_duration from employee_official_details where emp_id=?");
//			pst.setInt(1, getId());
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				paycycleType = rs.getString(1);
//
//			}
			String[] ids = { String.valueOf(getId()) };
			if (getComment() != null) {

				String[] strPayCycleDates = getComment().split("-");

				PayPayroll payPayroll = new PayPayroll();
				payPayroll.session = session;
				payPayroll.CF =CF;
				payPayroll.setServletRequest(request);
//				payPayroll.setStrPC(strPayCycleDates[2]);
				payPayroll.setChbxApprove(ids);
				payPayroll.payApporvedPayroll(true, CF,strPayCycleDates[0],strPayCycleDates[1],strPayCycleDates[2]);
			} else {

				String paymentMode = null;
				String paycycleDuration = null;
				String orgId=null;
				pst = con.prepareStatement("select payment_mode,org_id,paycycle_duration from employee_official_details where emp_id=?");
				pst.setInt(1, uF.parseToInt(getId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					paymentMode = rs.getString("payment_mode");
					paycycleDuration = rs.getString("paycycle_duration");
					orgId=rs.getString("org_id");
				}
				rs.close();
				pst.close();

				String[] strPayCycleDates = getPaycycle().split("-");

//				String[] strPayCycleDates = getNextPayCycle(strPayCycleDates1[0], CF.getStrTimeZone(), CF);

//				StringBuilder payCycle = new StringBuilder();
//				for (int i = 0; i < strPayCycleDates.length; i++) {
//					payCycle.append(strPayCycleDates[i] + "-");
//				}

				ApprovePayroll objApprovePayroll = new ApprovePayroll();
				objApprovePayroll.session=session;
				objApprovePayroll.setServletRequest(request);
				objApprovePayroll.setChbox(ids);
				objApprovePayroll.setPaycycle(getPaycycle());
				objApprovePayroll.setPaymentMode(new String[] { String.valueOf(paymentMode) });
				objApprovePayroll.setStrPaycycleDuration(paycycleDuration);
				objApprovePayroll.setF_org(orgId);
				objApprovePayroll.setPaycycle(getPaycycle());  
//				objApprovePayroll.viewClockEntriesForPayrollApproval(CF, getId()+"", strPayCycleDates[0], strPayCycleDates[1],strPayCycleDates[2]);
		//===start parvez date: 15-04-2022===
//				System.out.println("DOD/347--getFromPage="+getFromPage());
				if(getFromPage() != null && getFromPage().equals("ExitForm")){
					objApprovePayroll.viewClockEntriesForPayrollApprovalExitForm(CF, getId()+"", strPayCycleDates[0], strPayCycleDates[1],strPayCycleDates[2]);
				} else{
					objApprovePayroll.viewClockEntriesForPayrollApproval(CF, getId()+"", strPayCycleDates[0], strPayCycleDates[1],strPayCycleDates[2]);
				}
		//===end parvez date: 15-04-2022===		
				objApprovePayroll.setChbox(ids);
				objApprovePayroll.approvePayrollEntries(CF,strPayCycleDates[0], strPayCycleDates[1]);

				PayPayroll payPayroll = new PayPayroll();
				payPayroll.setServletRequest(request);
				payPayroll.session=session; 
//				payPayroll.setStrPC(strPayCycleDates[2]);
				payPayroll.setChbxApprove(ids);
				payPayroll.payApporvedPayroll(true, CF,strPayCycleDates[0],strPayCycleDates[1],strPayCycleDates[2]);
//				System.out.println("DOBD/363---return");
			}

		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		/*if(uF.parseToInt(getId()) > 0) {
			String encodeId = eU.encode(getId());
			setId(encodeId);
		}
		if(uF.parseToInt(getResignId()) > 0) {
			String encodeResignId = eU.encode(getResignId());
			setResignId(encodeResignId);
		}*/
		return "reSend";
	}

	public void approveExemption() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		CommonFunctions CF = (CommonFunctions) session.getAttribute(CommonFunctions);

		try {
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];
			
			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst = con.prepareStatement("update emp_offboard_status set status=?,approved_by=?,approved_date=to_date(CURRENT_TIMESTAMP::text,'yyyy-MM-dd') where offboard_id=? and section=?");
			pst.setBoolean(1, true);
			pst.setInt(2, uF.parseToInt((String) session.getAttribute(EMPID)));
			pst.setInt(3, uF.parseToInt(getResignId()));
			pst.setInt(4, uF.parseToInt(getElement()));
			int flag = pst.executeUpdate();
			pst.close();
			
			if (flag == 0) {
				pst = con.prepareStatement("insert into  emp_offboard_status(status,offboard_id,section,approved_by,approved_date) values(?,?,?,?,to_date(CURRENT_TIMESTAMP::text,'yyyy-MM-dd'))");
				pst.setBoolean(1, true);
				pst.setInt(2, uF.parseToInt(getResignId()));
				pst.setInt(3, uF.parseToInt(getElement()));
				pst.setInt(4, uF.parseToInt((String) session.getAttribute(EMPID)));
//				System.out.println("pst ===>> " + pst);
				pst.execute();
				pst.close();
			}

			if (getComment().equals("REIMBURSEMENT")) {
				pst = con.prepareStatement("update emp_reimbursement set is_fullandfinal=true ,ispaid=true ,paid_date=CURRENT_TIMESTAMP where emp_id=? and  ispaid=false");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.execute();
				pst.close();

			} else if (getComment().equals("LTA")) {
				
				pst = con.prepareStatement("select sum(applied_amount) as applied_amount,salary_head_id from emp_lta_details where emp_id=? group by salary_head_id");
				pst.setInt(1, uF.parseToInt(getId()));
				rs=pst.executeQuery();
				Map<String, String> hmLtaApplied = new HashMap<String, String>();
				while(rs.next()) {
					hmLtaApplied.put(rs.getString("salary_head_id"), rs.getString("applied_amount"));
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select sum(amount) as amount,salary_head_id from payroll_generation_lta  where emp_id=? group by salary_head_id");
				pst.setInt(1, uF.parseToInt(getId()));
				rs=pst.executeQuery();
				Map<String, String> hmLta = new HashMap<String, String>();
				while(rs.next()) {
					double dblLTAAmount = rs.getDouble("amount");
					double dblAppliedAmount = uF.parseToDouble(hmLtaApplied.get(rs.getString("salary_head_id")));
					
					double dblLTA = dblLTAAmount - dblAppliedAmount;
					if(dblLTA > 0.0d){
						hmLta.put(rs.getString("salary_head_id"), ""+dblLTA);
					}
				}
				rs.close();
				pst.close();
				
				Iterator<String> it = hmLta.keySet().iterator();
				while(it.hasNext()) {
					String strSalaryHeadId = it.next();
					String strLtaAmt = hmLta.get(strSalaryHeadId);
					
					pst = con.prepareStatement("insert into emp_lta_details (emp_id,actual_amount,applied_amount,is_approved,entry_date,ref_document," +
							"lta_purpose,salary_head_id,is_paid,is_approved,approved_by,approved_date,paid_by,paid_date,is_fullandfinal) " +
							"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?);");
					pst.setInt(1, uF.parseToInt(strSessionEmpId));
					pst.setDouble(2, uF.parseToDouble(strLtaAmt));
					pst.setDouble(3, uF.parseToDouble(strLtaAmt));
					pst.setInt(4, 0);
					pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(6, null);
					pst.setString(7, "FULL and FINAL");
					pst.setInt(8, uF.parseToInt(strSalaryHeadId));
					pst.setBoolean(9, true);
					pst.setInt(10, 1);
					pst.setInt(11, uF.parseToInt(strSessionEmpId));
					pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(13, uF.parseToInt(strSessionEmpId));
					pst.setDate(14, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setBoolean(15, true);
					int x = pst.executeUpdate();
					pst.close();

					if(x > 0) {
					
						String empLtaId=null;
						pst = con.prepareStatement("select max(emp_lta_id) as emp_lta_id from emp_lta_details");
						rs=pst.executeQuery();
						while(rs.next()) {
							empLtaId=rs.getString("emp_lta_id");
						}
						rs.close();
						pst.close();
						
						Map<String,Map<String,String>> hmBankMap = getBankMap(con, uF);;
						if(hmBankMap == null) hmBankMap = new HashMap<String, Map<String,String>>();
						
						pst = con.prepareStatement("select elt.emp_id,emp_fname, emp_mname,emp_lname, emp_bank_name, emp_bank_acct_nbr,applied_amount, paid_date from employee_personal_details epd, " +
								"emp_lta_details elt where epd.emp_per_id = elt.emp_id and emp_lta_id = ?");
						pst.setInt(1, uF.parseToInt(empLtaId));
						rs = pst.executeQuery();
						double dblAmount = 0;
						double dblTotalAmount = 0;
						int nMonth = 0;
						int nYear = 0;
						int nCount = 0;
						StringBuilder sbEmpAmountBankDetails = new StringBuilder();
						String strBankCode = null;
						String strBankName = null;
						String strBankAddress = null;
						String strEmpId = null;
						while(rs.next()) {
							strEmpId = rs.getString("emp_id");
							
							dblAmount = uF.parseToDouble(rs.getString("applied_amount"));
							nMonth = uF.parseToInt(uF.getDateFormat(rs.getString("paid_date"), DBDATE, "MM"));
							nYear = uF.parseToInt(uF.getDateFormat(rs.getString("paid_date"), DBDATE, "yyyy"));
							
							dblTotalAmount+=dblAmount;
							
				  			sbEmpAmountBankDetails.append("<tr>");
							sbEmpAmountBankDetails.append("<td><font size=\"1\">"+ ++nCount+".</font></td>");
						
							String strEmpMName = "";
							if(flagMiddleName) {
								if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
									strEmpMName = " "+rs.getString("emp_mname");
								}
							}
						
							
							sbEmpAmountBankDetails.append("<td><font size=\"1\">"+uF.showData(rs.getString("emp_fname"),"")+strEmpMName+" "+uF.showData(rs.getString("emp_lname"),"")+"</font></td>");
							sbEmpAmountBankDetails.append("<td><font size=\"1\">"+uF.showData(rs.getString("emp_bank_acct_nbr"),"")+"</font></td>");
							
							Map<String, String> hmBankBranch = hmBankMap.get(rs.getString("emp_bank_name"));
							if(hmBankBranch == null) hmBankBranch = new HashMap<String, String>();
							
							strBankCode = hmBankBranch.get("BANK_CODE");
							strBankName = hmBankBranch.get("BANK_NAME");
							strBankAddress = hmBankBranch.get("BANK_ADDRESS");
							
							sbEmpAmountBankDetails.append("<td><font size=\"1\">"+uF.showData(hmBankBranch.get("BANK_BRANCH"),"")+"</font></td>");
							sbEmpAmountBankDetails.append("<td align=\"right\"><font size=\"1\">"+uF.formatIntoTwoDecimal(dblAmount)+"</font></td>");
							sbEmpAmountBankDetails.append("</tr>");
						}
						rs.close();
						pst.close();
						
						String strContent = null;
						String strName = null;
						
						Map<String, String> hmActivityNode = CF.getActivityNode(con);
						if(hmActivityNode == null) hmActivityNode = new HashMap<String, String>();
						
						int nTriggerNode = uF.parseToInt(hmActivityNode.get(""+ACTIVITY_BANK_ORDER_ID));
						String strEmpOrgId = CF.getEmpOrgId(con, uF, strEmpId); 
						
						if(nMonth>0) {
							pst = con.prepareStatement("select * from document_comm_details where document_text like '%["+strBankCode+"]%' and trigger_nodes like '%,"+nTriggerNode+",%' and status=1 and org_id=? order by document_id desc limit 1");
							pst.setInt(1, uF.parseToInt(strEmpOrgId));
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
								strContent = strContent.replace(PAYROLL_AMOUNT_WORDS, uF.digitsToWords((int)dblTotalAmount));
							}
							
							if(strContent!=null && strContent.indexOf(PAY_MONTH)>=0) {
								strContent = strContent.replace(PAY_MONTH, uF.getMonth(nMonth));
							}
							
							if(strContent!=null && strContent.indexOf(PAY_YEAR)>=0) {
								strContent = strContent.replace(PAY_YEAR, ""+nYear);
							}
						}
						
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
							sbEmpBankDetails.append("<td colspan=\"5\"><hr width=\"100%\"/></td>");
							sbEmpBankDetails.append("</tr>");
							
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
							pst.setInt(4, uF.parseToInt((String)session.getAttribute(EMPID)));
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

							pst = con.prepareStatement("update emp_lta_details set statement_id=? where emp_lta_id=?");
							pst.setInt(1, nMaxStatementId);
							pst.setInt(2, uF.parseToInt(empLtaId));
							pst.executeUpdate();
							pst.close();
					
						}
						
						/**
						 * User Alerts
						 * */
						if(uF.parseToInt(strEmpId) > 0) {
							String strDomain = request.getServerName().split("\\.")[0];
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmpId);
							userAlerts.set_type(PAY_LTA);
							userAlerts.setStatus(INSERT_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
						}
					}
					
				}
				
			} else if (getComment().equals("PERK")) {
				
				String strLevelId =  CF.getEmpLevelId(con, ""+getId());	
				
				pst = con.prepareStatement("select * from perk_details where financial_year_start=? and financial_year_end=? and level_id=?");
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strLevelId));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				Map<String, List<Map<String, String>>> hmPerkLevel = new HashMap<String, List<Map<String,String>>>();
				while(rs.next()) {
					List<Map<String, String>> al = (List<Map<String, String>>) hmPerkLevel.get(rs.getString("level_id"));
					if(al == null) al = new ArrayList<Map<String,String>>();
					
					Map<String, String> hmPerk = new HashMap<String, String>();
					hmPerk.put("PERK_ID", rs.getString("perk_id"));
					hmPerk.put("PERK_CODE", uF.showData(rs.getString("perk_code"), ""));
					hmPerk.put("PERK_NAME", uF.showData(rs.getString("perk_name"), ""));
					hmPerk.put("PERK_DESCRIPTION", uF.showData(rs.getString("perk_description"), ""));
					hmPerk.put("PERK_PAYMENT_CYCLE", rs.getString("perk_payment_cycle"));
					hmPerk.put("PERK_LEVEL_ID", rs.getString("level_id"));
					hmPerk.put("PERK_MAX_AMOUNT", rs.getString("max_amount"));
					hmPerk.put("PERK_ORG_ID", rs.getString("org_id"));
					
					al.add(hmPerk);
					
					hmPerkLevel.put(rs.getString("level_id"), al);
				}
				rs.close();
				pst.close();
				
				/*
				 * Annual perk Data
				 * **/
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select sum(perk_amount) as perk_amount, emp_id, perk_type_id from emp_perks where " +
						" financial_year_start=? and financial_year_end=? and emp_id in("+getId()+") and perk_type_id in (select perk_id " +
						"from perk_details where financial_year_start=? and financial_year_end=? and perk_payment_cycle='A') group by emp_id, perk_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				Map<String, String> hmAppliedPerk = new HashMap<String, String>();
				while(rs.next()) {
					hmAppliedPerk.put(rs.getString("emp_id")+"_"+rs.getString("perk_type_id"), rs.getString("perk_amount"));
				}
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(perk_amount) as perk_amount, emp_id, perk_type_id from emp_perks where approval_1=1 and approval_2=1 and ispaid=false " +
						"and financial_year_start=? and financial_year_end=? and emp_id in("+getId()+") and perk_type_id in (select perk_id " +
						"from perk_details where financial_year_start=? and financial_year_end=? and perk_payment_cycle='A') group by emp_id, perk_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				Map<String, String> hmApprovedPerk = new HashMap<String, String>();
				while(rs.next()) {
					hmApprovedPerk.put(rs.getString("emp_id")+"_"+rs.getString("perk_type_id"), rs.getString("perk_amount"));
				}
				rs.close();
				pst.close();
				
				/*
				 * Annual perk  Data end
				 * **/
				
				/*
				 * Month perk Data
				 * **/
				String strMonth = ""+uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"));
				
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(perk_amount) as perk_amount, emp_id, perk_type_id from emp_perks where " +
						" financial_year_start=? and financial_year_end=? and emp_id in("+getId()+") and perk_type_id in (select perk_id " +
						"from perk_details where financial_year_start=? and financial_year_end=? and perk_payment_cycle='M') and perk_month=? group by emp_id, perk_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strMonth));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					hmAppliedPerk.put(rs.getString("emp_id")+"_"+rs.getString("perk_type_id"), rs.getString("perk_amount"));
				}
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(perk_amount) as perk_amount, emp_id, perk_type_id from emp_perks where approval_1=1 and approval_2=1 and ispaid=false " +
						"and financial_year_start=? and financial_year_end=? and emp_id in("+getId()+") and perk_type_id in (select perk_id " +
						"from perk_details where financial_year_start=? and financial_year_end=? and perk_payment_cycle='M') and perk_month=? group by emp_id, perk_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strMonth));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					hmApprovedPerk.put(rs.getString("emp_id")+"_"+rs.getString("perk_type_id"), rs.getString("perk_amount"));
				}
				rs.close();
				pst.close();
				/*
				 * Month perk Data end
				 * **/
				List<Map<String, String>> alPerkList = (List<Map<String, String>>) hmPerkLevel.get(strLevelId);
	    		if(alPerkList == null) alPerkList = new ArrayList<Map<String, String>>();
				for(int j = 0; j < alPerkList.size(); j++) {
					Map<String, String> hmPerk = (Map<String, String>) alPerkList.get(j);
	    			if(hmPerk == null) hmPerk = new HashMap<String, String>();
	    			
	    			double dblRemainingAmt = uF.parseToDouble(hmPerk.get("PERK_MAX_AMOUNT")) - uF.parseToDouble(hmAppliedPerk.get(getId()+"_"+hmPerk.get("PERK_ID")));
	    			
	    			if(dblRemainingAmt > 0.0d) {
		    			pst = con.prepareStatement("insert into emp_perks (financial_year_start, financial_year_end, perk_type_id, " +
		    					"perk_purpose, perk_amount, emp_id, entry_date,approval_1,approval_2,approval_1_emp_id,approval_2_emp_id,approval_1_date," +
		    					"approval_2_date,perk_month,is_fullandfinal,ispaid, paid_date, paid_by) " +
		    					"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,? );");
						pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setInt(3, uF.parseToInt(hmPerk.get("PERK_ID")));
						pst.setString(4, "Perk and Incentive In Full and final");
						pst.setDouble(5, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblRemainingAmt)));
						pst.setInt(6, uF.parseToInt(getId()));
						pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(8, 1);
						pst.setInt(9, 1);
						pst.setInt(10, uF.parseToInt(strSessionEmpId));
						pst.setInt(11, uF.parseToInt(strSessionEmpId));
						pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setDate(13, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(14, uF.parseToInt(strMonth));
						pst.setBoolean(15, true);
						pst.setBoolean(16, true);
						pst.setDate(17, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(18, uF.parseToInt(strSessionEmpId));
//						System.out.println("pst=====>"+pst);
						int x = pst.executeUpdate();
						pst.close();
						
						if(x > 0) {
							
							String perksId=null;
							pst = con.prepareStatement("select max(perks_id)as perks_id from emp_perks");
							rs=pst.executeQuery();
							while(rs.next()) {
								perksId=rs.getString("perks_id");
							}
							rs.close();
							pst.close();
							
							Map<String,Map<String,String>> hmBankMap = CF.getBankMap(con, uF);;
							if(hmBankMap == null) hmBankMap = new HashMap<String, Map<String,String>>();
							
							pst = con.prepareStatement("select ep.emp_id,emp_fname,emp_mname, emp_lname, emp_bank_name, emp_bank_acct_nbr,perk_amount, paid_date from employee_personal_details epd, " +
									"emp_perks ep where epd.emp_per_id = ep.emp_id and perks_id = ?");
							pst.setInt(1, uF.parseToInt(perksId));
							rs = pst.executeQuery();
							double dblAmount = 0;
							double dblTotalAmount = 0;
							int nMonth = 0;
							int nYear = 0;
							int nCount = 0;
							StringBuilder sbEmpAmountBankDetails = new StringBuilder();
							String strBankCode = null;
							String strBankName = null;
							String strBankAddress = null;
							String strEmpId = null;
							while(rs.next()) {
								strEmpId = rs.getString("emp_id");
								
								dblAmount = uF.parseToDouble(rs.getString("perk_amount"));
								nMonth = uF.parseToInt(uF.getDateFormat(rs.getString("paid_date"), DBDATE, "MM"));
								nYear = uF.parseToInt(uF.getDateFormat(rs.getString("paid_date"), DBDATE, "yyyy"));
								
								dblTotalAmount+=dblAmount;
								
					  			sbEmpAmountBankDetails.append("<tr>");
								sbEmpAmountBankDetails.append("<td><font size=\"1\">"+ ++nCount+".</font></td>");
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
							
								
								sbEmpAmountBankDetails.append("<td><font size=\"1\">"+uF.showData(rs.getString("emp_fname"),"")+strEmpMName+" "+uF.showData(rs.getString("emp_lname"),"")+"</font></td>");
								sbEmpAmountBankDetails.append("<td><font size=\"1\">"+uF.showData(rs.getString("emp_bank_acct_nbr"),"")+"</font></td>");
								
								Map<String, String> hmBankBranch = hmBankMap.get(rs.getString("emp_bank_name"));
								if(hmBankBranch == null) hmBankBranch = new HashMap<String, String>();
								
								strBankCode = hmBankBranch.get("BANK_CODE");
								strBankName = hmBankBranch.get("BANK_NAME");
								strBankAddress = hmBankBranch.get("BANK_ADDRESS");
								
								sbEmpAmountBankDetails.append("<td><font size=\"1\">"+uF.showData(hmBankBranch.get("BANK_BRANCH"),"")+"</font></td>");
								sbEmpAmountBankDetails.append("<td align=\"right\"><font size=\"1\">"+uF.formatIntoTwoDecimal(dblAmount)+"</font></td>");
								sbEmpAmountBankDetails.append("</tr>");
							}
							rs.close();
							pst.close();
							
							String strContent = null;
							String strName = null;
							
							Map<String, String> hmActivityNode = CF.getActivityNode(con);
							if(hmActivityNode == null) hmActivityNode = new HashMap<String, String>();
							
							int nTriggerNode = uF.parseToInt(hmActivityNode.get(""+ACTIVITY_BANK_ORDER_ID));
							String strEmpOrgId = CF.getEmpOrgId(con, uF, strEmpId); 
							
							if(nMonth>0) {
//								pst = con.prepareStatement("select * from document_comm_details where document_text like '%["+strBankCode+"]%' ");
								pst = con.prepareStatement("select * from document_comm_details where document_text like '%["+strBankCode+"]%' and trigger_nodes like '%,"+nTriggerNode+",%' and status=1 and org_id=? order by document_id desc limit 1");
								pst.setInt(1, uF.parseToInt(strEmpOrgId));
								rs = pst.executeQuery();
								while(rs.next()) {
									strContent = rs.getString("document_text");
								} 
								rs.close();
								pst.close();
								
								
								if(strContent!=null && strContent.indexOf("["+strBankCode+"]")>=0){
									strContent = strContent.replace("["+strBankCode+"]", strBankName +"<br/>"+strBankAddress);
								}
								
								if(strContent!=null && strContent.indexOf(DATE)>=0){
									strContent = strContent.replace(DATE, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
								}
								
								if(strContent!=null && strContent.indexOf(PAYROLL_AMOUNT)>=0){
									strContent = strContent.replace(PAYROLL_AMOUNT, uF.formatIntoTwoDecimal(dblTotalAmount));
								}
								
								if(strContent!=null && strContent.indexOf(PAYROLL_AMOUNT_WORDS)>=0){
									strContent = strContent.replace(PAYROLL_AMOUNT_WORDS, uF.digitsToWords((int)dblTotalAmount));
								}
								
								if(strContent!=null && strContent.indexOf(PAY_MONTH)>=0){
									strContent = strContent.replace(PAY_MONTH, uF.getMonth(nMonth));
								}
								
								if(strContent!=null && strContent.indexOf(PAY_YEAR)>=0){
									strContent = strContent.replace(PAY_YEAR, ""+nYear);
								}
							}
							
							
							
							
							
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
								sbEmpBankDetails.append("<td colspan=\"5\"><hr width=\"100%\"/></td>");
								sbEmpBankDetails.append("</tr>");
								
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
								pst.setInt(4, uF.parseToInt((String)session.getAttribute(EMPID)));
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

								pst = con.prepareStatement("update emp_perks set statement_id=? where perks_id=?");
								pst.setInt(1, nMaxStatementId);
								pst.setInt(2, uF.parseToInt(perksId));
								pst.executeUpdate();
								pst.close();
						
							}
							
							/**
							 * User Alerts
							 * */
							if(uF.parseToInt(strEmpId) > 0) {
								String strDomain = request.getServerName().split("\\.")[0];
								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(strEmpId);
								userAlerts.set_type(PAY_PERK);
								userAlerts.setStatus(INSERT_ALERT);
								Thread t = new Thread(userAlerts);
								t.run();
							}
						}
	    			}
	    			
				}
				
			} 
//			else if (getComment().equals("LOAN")) {
//
//				Map<String, Map<String, String>> loanMp = new HashMap<String, Map<String, String>>();
//				List<String> idList = new ArrayList<String>();
//				double loanAmt = 0.0;
//				pst = con.prepareStatement("select * from loan_applied_details where emp_id=? and is_approved=1");
//				pst.setInt(1, getId());
//				rs = pst.executeQuery();
//				while (rs.next()) {
//					loanAmt += rs.getDouble("balance_amount");
//
//					idList.add(rs.getString("loan_applied_id"));
//
//					Map<String, String> innerMap = new HashMap<String, String>();
//					innerMap.put("BALANCE", rs.getString("balance_amount"));
//					innerMap.put("LOAN_ID", rs.getString("loan_id"));
//
//					loanMp.put(rs.getString("loan_applied_id"), innerMap);
//				}
//				rs.close();
//				pst.close();
//
//				for (String ids : idList) {
//
//					pst = con.prepareStatement("update loan_applied_details set balance_amount=0.0 where loan_applied_id=?");
//					pst.setInt(1, uF.parseToInt(ids));
//					pst.execute();
//					pst.close();
//
//					Map<String, String> innerMap = loanMp.get(ids);
//					pst = con
//							.prepareStatement("insert into  loan_payments (emp_id,loan_id,amount_paid,paid_date,pay_source,loan_applied_id) values(?,?,?,to_date(CURRENT_TIMESTAMP::text,'yyyy-MM-dd'),?,?)");
//					pst.setInt(1, getId());
//					pst.setInt(2, uF.parseToInt(innerMap.get("LOAN_ID")));
//					pst.setDouble(3, uF.parseToDouble(innerMap.get("BALANCE")));
//					pst.setString(4, "F");
//					pst.setInt(5, uF.parseToInt(ids));
//					pst.execute();
//					pst.close();
//
//				}
//			} else if (getComment().equals("ENCASHMENT")) {
//				Map<String, Map<String, String>> loanMp = new HashMap<String, Map<String, String>>();
//				List<String> idList = new ArrayList<String>();
//				pst = con.prepareStatement("select * from leave_register where emp_id=? and (accrued_leaves>0 or leave_carryforward>0) ");
//				pst.setInt(1, getId());
//				rs = pst.executeQuery();
//				while (rs.next()) {
//					idList.add(rs.getString("leave_type_id"));
//
//					Map<String, String> innerMap = new HashMap<String, String>();
//					double leav = rs.getDouble("accrued_leaves") + uF.parseToDouble(rs.getString("leave_carryforward")) - rs.getDouble("taken_leaves");
//					innerMap.put("ACCRUED", String.valueOf(leav));
//					innerMap.put("LEAVE_TYPE_ID", rs.getString("leave_type_id"));
//
//					loanMp.put(rs.getString("leave_type_id"), innerMap);
//				}
//				rs.close();
//				pst.close();
//
//				for (String ids : idList) {
//					Map<String, String> innerMap = loanMp.get(ids);
//					pst = con.prepareStatement("insert into  emp_leave_entry (emp_id,entrydate,reason,manager_reason,emp_no_of_leave,leave_type_id,is_approved,user_id,encashment_status,encashment_date,ispaid,is_fullandfinal) values(?,to_date(CURRENT_TIMESTAMP::text,'yyyy-MM-dd'),?,?,?,?,?,?,?,to_date(CURRENT_TIMESTAMP::text,'yyyy-MM-dd'),?,?)");
//					pst.setInt(1, getId());
//					pst.setString(2, "Full and Final");
//					pst.setString(3, "Full and Final");
//					pst.setDouble(4, uF.parseToDouble(innerMap.get("ACCRUED")));
//					pst.setInt(5, uF.parseToInt(innerMap.get("LEAVE_TYPE_ID")));
//					pst.setInt(6, 1);
//					pst.setInt(7, uF.parseToInt((String) session.getAttribute(EMPID)));
//					pst.setBoolean(8, true);
//					pst.setBoolean(9, true);
//					pst.setBoolean(10, true);
//					pst.execute();
//					pst.close();
//				}
//
//			} 
			else if (getComment().equals("GRATUITY")) {

				String orgId = CF.getEmpOrgId(con, uF, ""+getId());
				
				SetGratuityAmount setGratuityAmount = new SetGratuityAmount();
				setGratuityAmount.setServletRequest(request);
				setGratuityAmount.session = session;
				setGratuityAmount.CF = CF;
				setGratuityAmount.setStrActualAmount(getAmount());
				setGratuityAmount.setStrEmpId(String.valueOf(getId()));
				setGratuityAmount.setStrAmount(getAmount());
				setGratuityAmount.setIsFullAndFinal(""+true);
				
				String[] strPayCycleDates = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, orgId);
				setGratuityAmount.setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
				setGratuityAmount.setGratuityAmount();

			} else if (getComment().equals("OTHERDEDUCT")) {
				
				String deductAmt = (String) request.getParameter("deductAmt");
				pst = con.prepareStatement("update emp_offboard_status set deduct_amount=? where offboard_id=? and section=?");
				pst.setDouble(1, uF.parseToDouble(deductAmt));
				pst.setInt(2, uF.parseToInt(getResignId()));
				pst.setInt(3, uF.parseToInt(getElement()));
//				System.out.println("pst ===>> " + pst);
				pst.execute();
				pst.close();
			}

			request.setAttribute("STATUS_MSG", "Approved");

		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

public Map<String,Map<String,String>> getBankMap(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		Map<String,Map<String,String>> hmBankMap = new HashMap<String, Map<String,String>>();
		try {
			Map<String, String> hmStates = CF.getStateMap(con);
			Map<String, String> hmCountry = CF.getCountryMap(con);
			
			pst = con.prepareStatement("select bd.bank_account_no, bd.branch_id, bd1.bank_name,bd.bank_branch, bd.branch_code, bd.bank_address, bd.bank_city, bd.bank_pincode, bd.bank_state_id, bd.bank_country_id from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id");
			rs = pst.executeQuery();
			while(rs.next()) {
				Map<String, String> hmInner = hmBankMap.get(rs.getString("branch_id"));
				if(hmInner == null) hmInner = new HashMap<String, String>();
				
				String strBankCode = rs.getString("branch_code");
				String strBankName = rs.getString("bank_name");
				String strBankBranch = rs.getString("bank_branch")+"["+rs.getString("branch_code")+"]";
				String strBankAddress = rs.getString("bank_address")+"<br/>"+rs.getString("bank_city")+" - "+rs.getString("bank_pincode")+"<br/>"+uF.showData(hmStates.get(rs.getString("bank_state_id")), "")+", "+uF.showData(hmCountry.get(rs.getString("bank_country_id")), "");
				hmInner.put("BANK_CODE", strBankCode);
				hmInner.put("BANK_NAME", strBankName);
				hmInner.put("BANK_BRANCH", strBankBranch);
				hmInner.put("BANK_ADDRESS", strBankAddress);
				
				hmBankMap.put(rs.getString("branch_id"), hmInner);
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
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
		return hmBankMap;
	}

	public String updateApproved() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
//		EncryptionUtility eU = new EncryptionUtility();
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);

			pst = con.prepareStatement("update emp_offboard_status set status=?, approved_by=?, approved_remark=?, approved_rating=?,emp_id =? , " +
					"approved_date=to_date(CURRENT_TIMESTAMP::text,'yyyy-MM-dd') where offboard_id=? and section=?");
			pst.setBoolean(1, true);
			pst.setInt(2, uF.parseToInt((String) session.getAttribute(EMPID)));
			pst.setString(3, getFeedbackformRemark());
			pst.setDouble(4, uF.parseToDouble(getFeedbackFormRating()));
			pst.setInt(5, uF.parseToInt(getId()));
			pst.setInt(6, uF.parseToInt(getResignId()));
			pst.setInt(7, uF.parseToInt(getElement()));
//			System.out.println("update pst==>"+pst);
			int flag = pst.executeUpdate();
			pst.close();
			
			if (flag == 0) {
				pst = con.prepareStatement("insert into  emp_offboard_status(status,offboard_id,section,approved_by,approved_remark,approved_rating," +
						"emp_id, approved_date) values(?,?,?,?,?,?,?,to_date(CURRENT_TIMESTAMP::text,'yyyy-MM-dd'))");
				pst.setBoolean(1, true);
				pst.setInt(2, uF.parseToInt(getResignId()));
				pst.setInt(3, uF.parseToInt(getElement()));
				pst.setInt(4, uF.parseToInt((String) session.getAttribute(EMPID)));
				pst.setString(5, getFeedbackformRemark());
				pst.setDouble(6, uF.parseToDouble(getFeedbackFormRating()));
				pst.setInt(7, uF.parseToInt(getId()));
//				System.out.println("insert pst==>"+pst);
				pst.execute();
				pst.close();
			}

			String activity_id = null;
			String strReason = null;
			if(uF.parseToInt(getElement()) == 1) {
				activity_id = ACTIVITY_EXIT_FEEDBACK_FORM_APPROVAL_ID;
				strReason = "Exit feedback form approved";
			} else if(uF.parseToInt(getElement()) == 2) {
				activity_id = ACTIVITY_HANDOVER_DOCUMENTS_APPROVAL_ID;
				strReason = "Handover documents approved";
			}
			insertEmpActivity(con, ""+getId(), CF, strSessionEmpId, activity_id, strReason);
			if(uF.parseToInt(getElement()) == 1) {
				session.setAttribute(MESSAGE, SUCCESSM+"Feedback form section has been Approved."+END);
			} else if(uF.parseToInt(getElement()) == 2) {
				session.setAttribute(MESSAGE, SUCCESSM+"Handover Documents section has been Approved."+END);
			}
		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		/*if(uF.parseToInt(getId()) > 0) {
			String encodeId = eU.encode(getId());
			setId(encodeId);
		}
		if(uF.parseToInt(getResignId()) > 0) {
			String encodeResignId = eU.encode(getResignId());
			setResignId(encodeResignId);
		}*/
		return "reSend";

	}

	public String[] getNextPayCycle(String strDate, String strTimeZone, CommonFunctions CF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String[] strPayCycleDate = new String[3];

		try {

			String startDate = null;
			String strDisplayPaycycle = null;
			String strPaycycleDuration = null;

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectSettings);
			rs = pst.executeQuery();

			while (rs.next()) {
				if (rs.getString("options").equalsIgnoreCase(O_START_PAY_CLYCLE)) {
					startDate = rs.getString("value");
				}
				if (rs.getString("options").equalsIgnoreCase(O_DISPLAY_PAY_CLYCLE)) {
					strDisplayPaycycle = rs.getString("value");
				}
				if (rs.getString("options").equalsIgnoreCase(O_PAYCYCLE_DURATION)) {
					strPaycycleDuration = rs.getString("value");
				}
			}
			rs.close();
			pst.close();

			String[] arrDisplayPAycycle = null;
			int minCycle = 0;
			int maxCycle = 0;
			if (strDisplayPaycycle != null) {
				arrDisplayPAycycle = strDisplayPaycycle.split("-");
				minCycle = uF.parseToInt(arrDisplayPAycycle[0]);
				maxCycle = uF.parseToInt(arrDisplayPAycycle[1]);
			}

			Calendar calCurrent = GregorianCalendar.getInstance(TimeZone.getTimeZone((strTimeZone)));
			calCurrent.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "dd")));
			calCurrent.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "MM")));
			calCurrent.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "yyyy")));

			calCurrent.add(Calendar.DAY_OF_MONTH, -1);

			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(strTimeZone));
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(startDate, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(startDate, DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(startDate, DATE_FORMAT, "yyyy")));

			StringBuilder sb = new StringBuilder();
			int nPayCycle = 0;

			int nDurationCount = 0;
			String dt1 = null;
			String dt2 = null;

			// java.util.Date strCurrentDate = calCurrent.getTime();
			java.util.Date strCurrentDate = uF.getDateFormatUtil(
					((calCurrent.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + calCurrent.get(Calendar.DAY_OF_MONTH) : calCurrent.get(Calendar.DAY_OF_MONTH)) + "/"
							+ (((calCurrent.get(Calendar.MONTH) + 1) < 10) ? "0" + (calCurrent.get(Calendar.MONTH) + 1) : (calCurrent.get(Calendar.MONTH) + 1)) + "/"
							+ calCurrent.get(Calendar.YEAR), DATE_FORMAT);

			java.util.Date strCurrentPayCycleD1 = null;
			java.util.Date strCurrentPayCycleD2 = null;

			while (true) {
				sb = new StringBuilder();
				nPayCycle++;

				if (strPaycycleDuration != null && strPaycycleDuration.equalsIgnoreCase("M")) {
					nDurationCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH) - 1;
				} else if (strPaycycleDuration != null && strPaycycleDuration.equalsIgnoreCase("F")) {
					nDurationCount = 15 - 1;
					if (cal.get(Calendar.DAY_OF_MONTH) == 16) {
						int nActual = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
						nDurationCount = nActual - 15 - 1;
					}
				} else if (strPaycycleDuration != null && strPaycycleDuration.equalsIgnoreCase("BW")) {
					nDurationCount = 14 - 1;
				} else if (strPaycycleDuration != null && strPaycycleDuration.equalsIgnoreCase("W")) {
					nDurationCount = 7 - 1;
				} else {
					nDurationCount = cal.getMaximum(Calendar.DAY_OF_MONTH) - 1;
				}

				dt1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/"
						+ (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
				cal.add(Calendar.DAY_OF_MONTH, nDurationCount);
				dt2 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/"
						+ (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);

				if (nPayCycle < minCycle) {
					cal.add(Calendar.DAY_OF_MONTH, 1);
					continue;
				}

				sb.append("Pay Cycle " + nPayCycle + ", " + dt1 + " - " + dt2);

				strCurrentPayCycleD1 = uF.getDateFormatUtil(dt1, DATE_FORMAT);
				strCurrentPayCycleD2 = uF.getDateFormatUtil(dt2, DATE_FORMAT);

				// log.debug("nPayCycle===>"+nPayCycle);

				if (strCurrentDate.equals(strCurrentPayCycleD1) || strCurrentDate.equals(strCurrentPayCycleD2)
						|| (strCurrentDate.after(strCurrentPayCycleD1) && strCurrentDate.before(strCurrentPayCycleD2))) {
					strPayCycleDate[0] = dt1;
					strPayCycleDate[1] = dt2;
					strPayCycleDate[2] = nPayCycle + "";

					// log.debug("nPayCycle= E ==>"+nPayCycle);
				}

				cal.add(Calendar.DAY_OF_MONTH, 1);

				if (nPayCycle >= maxCycle) {
					break;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			// log.error(e.getClass() + ": " + e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return strPayCycleDate;
	}

	public String getCommentByDoc() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst = con.prepareStatement("select bdc.*,epd.emp_fname,epd.emp_mname,epd.emp_lname from emp_off_board_document_comment bdc,employee_personal_details epd where bdc.comment_made_by =epd.emp_per_id and document_id = ?");
			pst.setInt(1, getDocid());
//			System.out.println("pst ===>>> " + pst);
			rs = pst.executeQuery();
			List<List<String>> outerList = new ArrayList<List<String>>();
			List<List<String>> outerList1 = new ArrayList<List<String>>();
			int i = 0;
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("comment_text"));
				innerList.add(rs.getString("doe"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				innerList.add(rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				if (i % 2 == 0) {
					outerList.add(innerList);
				} else {
					outerList1.add(innerList);
				}
				i++;
			}
			rs.close();
			pst.close();
			
			String currentComment = "";
			pst = con.prepareStatement("select * from emp_off_board_document_comment bdc where document_id = ? and bdc.comment_made_by = ?");
			pst.setInt(1, getDocid());
			pst.setInt(2, uF.parseToInt(getUserId()));
//			System.out.println("pst ===>>> " + pst);
			rs = pst.executeQuery();

			while (rs.next()) {
				currentComment = rs.getString("comment_text");
			}
			rs.close();
			pst.close();
			
//			System.out.println("currentComment ===>> " + currentComment);
			
			request.setAttribute("currentComment", currentComment);
			request.setAttribute("outerList", outerList);
			request.setAttribute("outerList1", outerList1);

		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return "comment";

	}

	public void approveOffboard() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			boolean flag = false;
			con = db.makeConnection(con);
			pst = con.prepareStatement("select approved from emp_off_board_document_details where off_board_document_id=? ");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				flag = rs.getBoolean(1);
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("update emp_off_board_document_details set approved=? where off_board_document_id=? ");
			pst.setBoolean(1, !flag);
			pst.setInt(2, uF.parseToInt(getId()));
			pst.execute();
			pst.close();
			if (!flag) {
				String msg = "'DeleteOffboardDocument.action?id=" + getId() + "&element=" + element + "&operation=C'";
				/*request.setAttribute("STATUS_MSG", "<img onclick=\"getContent('" + element + "', " + msg + ");\" title=\"Enable\" src=\"images1/cross.png\">");*/
				request.setAttribute("STATUS_MSG", "<i class=\"fa fa-times cross\" aria-hidden=\"true\" onclick=\"getContent('" + element + "', " + msg + ");\" title=\"Enable\"></i>");
				
			} else {
				String msg = "'DeleteOffboardDocument.action?id=" + getId() + "&element=" + element + "&operation=C'"; 
				/*request.setAttribute("STATUS_MSG", "<img onclick=\"getContent('" + element + "', " + msg + ");\" title=\"Disable\" src=\"images1/tick.png\">");*/
				request.setAttribute("STATUS_MSG", "<i class=\"fa fa-check checknew\" title=\"Disable\"aria-hidden=\"true\" onclick=\"getContent('" + element + "', " + msg + ");\" title=\"Disable\"></i>");
				

			}
		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public void updateOffboardComment() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);

			pst = con.prepareStatement("update emp_off_board_document_details set document_comment=? where off_board_document_id = ?");
			pst.setString(1, getComment());
			pst.setInt(2, getDocid());
			pst.execute();
			pst.close();

			pst = con.prepareStatement("update emp_off_board_document_comment set comment_text=?, doe=CURRENT_TIMESTAMP where comment_made_by=? and document_id=?");
			pst.setString(1, getComment());
			pst.setInt(2, uF.parseToInt(getUserId()));
			pst.setInt(3, getDocid());
			int cnt = pst.executeUpdate();
			pst.close();
			
			if(cnt == 0) {
				pst = con.prepareStatement("insert into  emp_off_board_document_comment(comment_text,comment_made_by,document_id,doe) values(?,?,?,CURRENT_TIMESTAMP)");
				pst.setString(1, getComment());
				pst.setInt(2, uF.parseToInt(getUserId()));
				pst.setInt(3, getDocid());
				pst.execute();
				pst.close();
			}
			request.setAttribute("STATUS_MSG", "Comment Saved");

		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public void deleteOffboard() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);

			pst = con.prepareStatement("delete from emp_off_board_document_details where off_board_document_id = ? ");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.execute();
			pst.close();

			pst = con.prepareStatement("delete from emp_off_board_document_comment where document_id = ? ");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.execute();
			pst.close();
			
			request.setAttribute("STATUS_MSG", "<td class=\"reportHeading\" colspan=\"2\" text-align=\"center\" font-size=\"10px\">Deleted</td>");

		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "<td class=\"reportHeading\" colspan=\"2\" text-align=\"center\" font-size=\"10px\">Could not be updated, Please try again</td>");
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	
public void insertEmpActivity(Connection con,String strEmpId, CommonFunctions CF, String strSessionEmpId, String activity_id, String strReason) {
		
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst = null;
		
		try {
			String strOrg = CF.getEmpOrgId(con, uF, strEmpId);
			String strWLocation = CF.getEmpWlocationMap(con).get(strEmpId); 
			String strDepartment = CF.getEmpDepartmentMap(con).get(strEmpId);
			String strLevel = CF.getEmpLevelMap(con).get(strEmpId);
			String strService = CF.getEmpServiceMap(con).get(strEmpId);
			String strDesignation = CF.getEmpDesigMapId(con).get(strEmpId);
			String strGrade = CF.getEmpGenderMap(con).get(strEmpId);
			String strNewStatus = CF.getEmpEmploymentStatusMap(con).get(strEmpId);

//			pst = con.prepareStatement(insertEmpActivity);
			pst = con.prepareStatement("insert into employee_activity_details (wlocation_id, department_id, level_id, desig_id, grade_id, " +
					"emp_status_code, activity_id, reason, effective_date, entry_date, user_id, emp_id, notice_period, probation_period, appraisal_id, " +
					"extend_probation_period, org_id,service_id,increment_type,increment_percent) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
			pst.setInt(1, uF.parseToInt(strWLocation));
			pst.setInt(2, uF.parseToInt(strDepartment));
			pst.setInt(3, uF.parseToInt(strLevel));
			pst.setInt(4, uF.parseToInt(strDesignation));
			pst.setInt(5, uF.parseToInt(strGrade));
			pst.setString(6, strNewStatus);
			pst.setInt(7, uF.parseToInt(activity_id));
			pst.setString(8, strReason);
			pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(11, uF.parseToInt(strSessionEmpId));
			pst.setInt(12,  uF.parseToInt(strEmpId));
			pst.setInt(13,  0);
			pst.setInt(14,  0);
			pst.setInt(15, 0);
			pst.setInt(16, 0);
			pst.setInt(17, uF.parseToInt(strOrg));
			pst.setString(18, strService);
			pst.setInt(19, 0);
			pst.setDouble(20, 0);
			pst.execute();
			pst.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}				
			}
		}
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getDocid() {
		return docid;
	}

	public void setDocid(int docid) {
		this.docid = docid;
	}

	public String getFeedbackformRemark() {
		return feedbackformRemark;
	}

	public void setFeedbackformRemark(String feedbackformRemark) {
		this.feedbackformRemark = feedbackformRemark;
	}

	public String getFeedbackFormRating() {
		return feedbackFormRating;
	}

	public void setFeedbackFormRating(String feedbackFormRating) {
		this.feedbackFormRating = feedbackFormRating;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getResignId() {
		return resignId;
	}

	public void setResignId(String resignId) {
		this.resignId = resignId;
	}

//===start parvez date: 15-04-2022===	
	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
//===end parvez date: 15-04-2022===	

}