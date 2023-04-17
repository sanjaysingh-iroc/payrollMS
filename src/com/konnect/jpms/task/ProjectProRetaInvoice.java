package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillBank;
import com.konnect.jpms.select.FillClientAddress;
import com.konnect.jpms.select.FillClientPoc;
import com.konnect.jpms.select.FillCurrency;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserActivities;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectProRetaInvoice extends ActionSupport implements ServletRequestAware,IStatements 
{
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	private HttpServletRequest request;
	String strSessionEmpId; 
	String strUserType;

	List<FillClientPoc> clientPocList;
	String clientPoc;
	List<FillClientAddress> clientAddressList;
	List<FillCurrency> currencyList;
	String clientAddress;
	String strCurrency;
	String proCurrency;

	String invoiceType;
	
	String proOPEAmt;
	String pro_freq_id;
	String pro_id;
	String invoice_format_id;
	String client_id;
	String strStartDate;
	String strEndDate;

	String strStartDateOtherCurr;
	String strEndDateOtherCurr;
	
	List<FillEmployee> projectOwnerList;
	String strProjectOwner;
	String invoiceGenDate;
	String invoiceCode;
	String submit;
	
	String strReferenceNo;
	String strReferenceNoOtherCurr;
	
	String[] strParticulars;
	String[] strParticularsAmt;
	
	String proDescription;
	String proDescriptionOtherCurr;
	
	String otherDescription;
	String otherDescriptionOtherCurr;
	
	String[] taxHead;
	String[] taxNameLabel;
	String[] taxHeadPercent;
	String[] taxHeadAmt;
	
	String[] taxHeadOtherCurr;
	String[] taxNameLabelOtherCurr;
	String[] taxHeadPercentOtherCurr;
	String[] taxHeadAmtINRCurr;
	String[] taxHeadAmtOtherCurr;
	
//	String serviceTaxAmt;
//	String eduCessAmt;
//	String stdTaxAmt;
	
	String particularTotalAmt;
	String totalAmt;
	
	String particularTotalAmtINRCurr;
	String totalAmtINRCurr;
	String particularTotalAmtOtherCurr;
	String totalAmtOtherCurr;

	List<FillBank> bankList;
	String bankName;
	String payPalMailId;
	
	String[] strOtherParticulars;
	String[] strOtherParticularsAmt;
	
	String[] strOtherParticularsINRCurr;
	String[] strOtherParticularsAmtINRCurr;
	String[] strOtherParticularsAmtOtherCurr;
	
	
	String[] hideTaskId;
	String[] hideTaskIdOtherCurr;
	String[] strEmp;
	String[] strEmpOtherCurr;
	
	String[] billDailyHours;
	String[] empRate;
	String[] strEmpAmt;
	
	String[] billDailyHoursINRCurr;
	String[] empRateINRCurr;
	String[] strEmpAmtINRCurr;
	String[] strEmpAmtOtherCurr;
	
	String bill_type;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		UtilityFunctions uF = new UtilityFunctions();
		
//		getProjectDetails(uF);
		
		/*if(getSubmit() != null && getSubmit().equals("Submit")) {
			insertProjectInvoice(uF);
			return SUCCESS;
		}*/
		
		
		
		/*if(uF.parseToInt(getInvoice_format_id()) > 0) {
			return "IFLOAD";
		} else {
			return LOAD;
		}*/
		
		if(uF.parseToInt(getInvoice_format_id()) == 1 || uF.parseToInt(getInvoice_format_id()) == 2) {
			getProjectFormat1Details(uF);
			loadProjectInvoice(uF);
			if(getSubmit() != null && getSubmit().equals("Submit")) {
				insertProjectInvoice(uF);
				return SUCCESS;
			}
			return "FORMAT1LOAD";
		} else {
			getProjectDetails(uF);
			loadProjectInvoice(uF);
			if(getSubmit() != null && getSubmit().equals("Submit")) {
				insertProjectInvoice(uF);
				return SUCCESS;
			}
			return LOAD;
		}
	}

	private void getProjectFormat1Details(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmAccNoBankName = CF.getBankAccNoMap(con, uF);
			
			if(getStrStartDate()==null && getStrEndDate()==null) {
				
				Date currDate=uF.getCurrentDate(CF.getStrTimeZone());
				
				String startdate="01/"+uF.getDateFormat(""+currDate, DBDATE, "MM")+"/"+uF.getDateFormat(""+currDate, DBDATE, "yyyy");
				
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "MM"))-1);
				calendar.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "yyyy")));
				calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));

				Date date = calendar.getTime();
				DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
				String endDate=DATE_FORMAT.format(date);
				
				setStrStartDate(startdate);
				setStrEndDate(endDate);
				
				setStrStartDateOtherCurr(startdate);
				setStrEndDateOtherCurr(endDate);
			}
			
			if(getSubmit() == null) {
				setInvoiceGenDate(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),DBDATE, DATE_FORMAT));
			}
			
			pst = con.prepareStatement("select pmt.*, cd.client_name, cd.client_industry, cd.client_address from projectmntnc pmt,client_details cd where pmt.client_id=cd.client_id and pmt.pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			Map<String,String> hmClientDetails = new HashMap<String, String>();
			Map<String,String> hmProjectDetails = new HashMap<String, String>();
			while (rs.next()) {
				hmClientDetails.put("CLIENT_ID", rs.getString("client_id"));
				hmClientDetails.put("CLIENT_NAME", rs.getString("client_name"));
				hmClientDetails.put("CLIENT_INDUSTRY", rs.getString("client_industry"));
				hmClientDetails.put("CLIENT_ADDRESS", rs.getString("client_address"));
				hmClientDetails.put("CLIENT_SPOC", CF.getClientSPOCNameById(con, rs.getString("poc")));
				
				setClientAddress(CF.getClientAddressIdByClientId(con, rs.getString("client_id")));
				setClientPoc(rs.getString("poc"));
				
				hmProjectDetails.put("PRO_ID", rs.getString("pro_id"));
				hmProjectDetails.put("PRO_NAME", rs.getString("pro_name"));
				hmProjectDetails.put("PRO_ACTUAL_BILLING_TYPE", rs.getString("actual_calculation_type"));
//				System.out.println("getStrProjectOwner() ===>> " + getStrProjectOwner());
				
				if(getStrProjectOwner() == null || getStrProjectOwner().trim().equals("")) { 
					setStrProjectOwner(rs.getString("project_owner"));
				}
				
				hmProjectDetails.put("PRIORITY", rs.getString("priority"));
				hmProjectDetails.put("DESCRIPTION", rs.getString("description"));
				hmProjectDetails.put("ACTIVITY", rs.getString("activity"));
				hmProjectDetails.put("SERVICE", rs.getString("service"));
				hmProjectDetails.put("WLOCATION_ID", rs.getString("wlocation_id"));
				hmProjectDetails.put("DEPARTMENT_ID", rs.getString("department_id"));
				hmProjectDetails.put("CURRENCY_ID", rs.getString("billing_curr_id"));
				hmProjectDetails.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
				hmProjectDetails.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
				hmProjectDetails.put("PRO_ORG_ID", rs.getString("org_id"));
				
//				hmProjectDetails.put("PRO_BANK_NAME", uF.showData(hmAccNoBankName.get(rs.getString("bank_id")), ""));
				hmProjectDetails.put("PRO_BRANCH_ID", uF.showData(rs.getString("bank_id"), "0"));
				hmProjectDetails.put("PRO_PAYPAL_MAIL_ID", uF.showData(rs.getString("paypal_mail_id"), ""));
				
				hmProjectDetails.put("PRO_ACCOUNT_REF", uF.showData(rs.getString("acc_ref"), "-"));
				hmProjectDetails.put("PRO_PO_NO", uF.showData(rs.getString("po_no"), "-"));
				hmProjectDetails.put("PRO_TERMS", uF.showData(rs.getString("terms"), "-"));
				hmProjectDetails.put("PRO_BILL_DUEDATE", uF.getDateFormat(rs.getString("bill_due_date"), DBDATE, CF.getStrReportDateFormat()));
				
				setBankName(rs.getString("bank_id"));
				setPayPalMailId(rs.getString("paypal_mail_id"));
				
				setProCurrency(rs.getString("billing_curr_id"));
				if(getProCurrency() == null) {
					setProCurrency(INR_CURR_ID);
				}
				if(getSubmit() == null) {
					setStrCurrency(rs.getString("billing_curr_id"));
					if(getStrCurrency() == null) {
						setStrCurrency(INR_CURR_ID);
					}
				}
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmBankAccData = CF.getBankAccountDetailsMap(con, uF, getBankName());
			request.setAttribute("hmBankAccData", hmBankAccData);
			
			Map<String, String> hmOrgData = CF.getOrgDetails(con, uF, hmProjectDetails.get("PRO_ORG_ID"));
			request.setAttribute("hmOrgData", hmOrgData);
			
			pst = con.prepareStatement("select freq_start_date,freq_end_date from projectmntnc_frequency where pro_freq_id=?");
			pst.setInt(1, uF.parseToInt(getPro_freq_id()));
			//System.out.println("pst ====>>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmProjectDetails.put("PRO_START_DATE", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT));
				hmProjectDetails.put("PRO_END_DATE", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT));
			}  
			rs.close();
			pst.close();
			
			pst=con.prepareStatement("select sum(er.reimbursement_amount) as reimbursement_amount from emp_reimbursement er " +
				" where (er.from_date, er.to_date) overlaps (to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD')) " +
				" and er.is_billable=true and approval_1 =1 and approval_2=1 and er.pro_id = ?");
			pst.setDate(1, uF.getDateFormat(hmProjectDetails.get("PRO_START_DATE"), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(hmProjectDetails.get("PRO_END_DATE"), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getPro_id()));
			rs=pst.executeQuery();
			int reimbursement_amount=0;
			while(rs.next()) {
				reimbursement_amount=rs.getInt("reimbursement_amount");
			}
			rs.close();
			pst.close();
			double remainReimburseAmt = uF.parseToDouble(reimbursement_amount+"") - uF.parseToDouble(getProOPEAmt());
			if(remainReimburseAmt < 0) {
				remainReimburseAmt = 0;
			}
			System.out.println("remainReimburseAmt =======>> " + remainReimburseAmt);
			request.setAttribute("reimbursement_amount", uF.formatIntoOneDecimalWithOutComma(remainReimburseAmt));
			
			pst = con.prepareStatement("select * from porject_billing_heads_details where pro_id=? and (head_data_type="+DT_OPE+" or head_data_type="+DT_OPE_OVERALL+") order by pro_billing_head_id");
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst======main==="+pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmProBillingHeadData = new LinkedHashMap<String, List<String>>();
			int strPerticnt = 0;
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("pro_billing_head_id"));
				alInner.add(rs.getString("head_label"));
				alInner.add(rs.getString("head_data_type"));
				alInner.add(rs.getString("head_other_variable"));
				alInner.add(rs.getString("billing_head_id"));
				if(uF.parseToInt(rs.getString("head_data_type")) != DT_OPE && uF.parseToInt(rs.getString("head_data_type")) != DT_OPE_OVERALL) {
					strPerticnt++;
				}
				hmProBillingHeadData.put(rs.getString("pro_billing_head_id"), alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProBillingHeadData", hmProBillingHeadData);
			request.setAttribute("strPerticnt", strPerticnt+"");
			
			
			pst = con.prepareStatement("select * from project_tax_setting where pro_id=? and invoice_or_customer=1 and status=true order by pro_tax_setting_id");
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst======main==="+pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmProTaxHeadData = new LinkedHashMap<String, List<String>>();
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("pro_tax_setting_id"));
				alInner.add(rs.getString("tax_name_label"));
				alInner.add(rs.getString("tax_percent"));
				alInner.add(rs.getString("tax_name"));
				hmProTaxHeadData.put(rs.getString("pro_tax_setting_id"), alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProTaxHeadData", hmProTaxHeadData);
			
			
			String otherDescription = "";
			pst = con.prepareStatement("select * from additional_info_of_pro_invoice where pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			while (rs.next()) {
				otherDescription = uF.showData(rs.getString("additional_info_text"), "");
			}
			rs.close();
			pst.close();
			request.setAttribute("otherDescription", otherDescription);
			
			request.setAttribute("hmClientDetails", hmClientDetails);
			request.setAttribute("hmProjectDetails", hmProjectDetails);
			
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			request.setAttribute("hmCurrencyDetails", hmCurrencyDetails);
			
			
//	 		=============================== Invoice Code Generation ======================================
			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con);
			Map<String, String> hmLocation = hmWorkLocation.get(hmProjectDetails.get("WLOCATION_ID"));
			Map<String, String> hmDept = CF.getDeptMap(con);
			
			String[] arr = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
			String locationCode = hmLocation.get("WL_NAME");			
			String departCode = "";
			if(hmDept.get(hmProjectDetails.get("DEPARTMENT_ID"))!=null && hmDept.get(hmProjectDetails.get("DEPARTMENT_ID")).contains(" ")) {
				String[] temp = hmDept.get(hmProjectDetails.get("DEPARTMENT_ID")).toUpperCase().split(" ");
				for(int i=0;i<temp.length;i++) {
					departCode+=temp[i].substring(0,1);
				}
			} else if(hmDept.get(hmProjectDetails.get("DEPARTMENT_ID"))!=null) {
				departCode = hmDept.get(hmProjectDetails.get("DEPARTMENT_ID")).substring(0, hmDept.get(hmProjectDetails.get("DEPARTMENT_ID")).length()>3 ? 3 : hmDept.get(hmProjectDetails.get("DEPARTMENT_ID")).length());
			}
			
			int count=0;
			pst=con.prepareStatement("select max(invoice_no) as invoice_no from promntc_invoice_details where wlocation_id=?");
			pst.setInt(1, uF.parseToInt(hmProjectDetails.get("WLOCATION_ID")));
			rs=pst.executeQuery();
			while(rs.next()) {
				count=rs.getInt("invoice_no");
			}
			rs.close();
			pst.close();
			count++;
			
			String invoiceCode = count + "-" + uF.getDateFormat(arr[0], DATE_FORMAT, "yyyy") + "-" + uF.getDateFormat(arr[1], DATE_FORMAT, "yy") + "/" + locationCode.toUpperCase(); //+"/"+departCode.toUpperCase()
			if(getInvoiceCode() == null || getInvoiceCode().trim().equals("")) {
				setInvoiceCode(invoiceCode);
			}
// 		========================================================= End =========================================================			
	
			
			pst=con.prepareStatement("select emp_id,emp_rate_per_hour,emp_rate_per_day,emp_rate_per_month from project_emp_details where pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs=pst.executeQuery();
			Map<String,String> hmEmpBillRate = new HashMap<String, String>();
			List<String> proEmpList = new ArrayList<String>();
			while(rs.next()) {
				if(hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE") != null && hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE").equals("D")) {
					hmEmpBillRate.put(rs.getString("emp_id"), rs.getString("emp_rate_per_day"));
				} else if(hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE") != null && hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE").equals("M")) {
					hmEmpBillRate.put(rs.getString("emp_id"), rs.getString("emp_rate_per_month"));
				} else {
					hmEmpBillRate.put(rs.getString("emp_id"), rs.getString("emp_rate_per_hour"));
				}
				proEmpList.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			System.out.println("proEmpList ===>> " + proEmpList);
			request.setAttribute("hmEmpBillRate", hmEmpBillRate);
			
			pst=con.prepareStatement("select a.* from (select task_id,activity_name,parent_task_id,pro_id,resource_ids from activity_info where " +
				"task_id not in (select parent_task_id from activity_info where parent_task_id is not null)) a where (parent_task_id in (select " +
				"task_id from activity_info) or parent_task_id = 0) and a.pro_id =?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs=pst.executeQuery();
			Map<String,List<String>> hmTaskEmpMap = new LinkedHashMap<String, List<String>>();
			int taskCount = 0;
			int emp_count = 0;
			double totBillableamt = 0;
			while(rs.next()) {
				
				List<String> taskEmpList = new ArrayList<String>();
				if(rs.getString("resource_ids") != null) {
					taskEmpList = Arrays.asList(rs.getString("resource_ids").split(","));
				}
				for(int a=0; taskEmpList != null && !taskEmpList.isEmpty() && a<taskEmpList.size(); a++) {
					if(taskEmpList.get(a) != null && !taskEmpList.get(a).equals("")) {
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("task_id"));
						StringBuilder sbEmpOption = new StringBuilder();
						for(int i=0; proEmpList != null && i<proEmpList.size(); i++) {
							if(proEmpList.get(i).equals(taskEmpList.get(a))) {
								sbEmpOption.append("<option value=" + proEmpList.get(i) + " selected>" + CF.getEmpNameMapByEmpId(con, proEmpList.get(i))
									+" ("+CF.getProjectTaskNameByTaskId(con, uF, rs.getString("task_id"))+")" + "</option>");
							} else {
								sbEmpOption.append("<option value=" + proEmpList.get(i) + ">" + CF.getEmpNameMapByEmpId(con, proEmpList.get(i)) + "</option>");
							}
						}
						innerList.add(sbEmpOption.toString());
						String taskPaidEfforts = getTaskTotalPaidEfforts(con, rs.getString("task_id"), taskEmpList.get(a));
						String taskEfforts = getTaskTotalDaysOrHours(con, rs.getString("task_id"), taskEmpList.get(a), hmProjectDetails);
//						System.out.println("taskEfforts==>"+taskEfforts);
						Map<String, String> hmEmpTaskTotMonthsAndCost = new HashMap<String, String>();
						if(hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE") != null && hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE").equals("M")) {
							
							hmEmpTaskTotMonthsAndCost = CF.getProjectEmpTaskActualWorkedMonthsCostOrRate(con,request, CF, rs.getString("task_id"), taskEmpList.get(a), hmEmpBillRate,hmProjectDetails);
//							String strBillMonth = hmEmpTaskTotMonthsAndCost.get("BILL_WORKING");
							taskEfforts = hmEmpTaskTotMonthsAndCost.get("BILLABLE_WORKING");
//							double dblTaskPaidEfforts = uF.parseToDouble(taskPaidEfforts) * 30; 
						}
						
						double dblUnpaidEfforts = uF.parseToDouble(taskEfforts) - uF.parseToDouble(taskPaidEfforts);
//						System.out.println(rs.getString("task_id")+" == " + taskEmpList.get(a) + " == taskPaidEfforts == " +taskPaidEfforts + " == taskEfforts == " + taskEfforts +" == dblUnpaidEfforts ==>> " + dblUnpaidEfforts);
						if(dblUnpaidEfforts < 0) 
							dblUnpaidEfforts = 0;
						String taskBillRate = hmEmpBillRate.get(taskEmpList.get(a));
						double taskTotBillAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblUnpaidEfforts)) * uF.parseToDouble(taskBillRate);
						if(hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE") != null && hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE").equals("H")) {
							innerList.add(uF.showData(uF.getTotalTimeMinutes100To60(""+dblUnpaidEfforts), "0"));
						} else {
							innerList.add(uF.showData(uF.formatIntoTwoDecimalWithOutComma(dblUnpaidEfforts), "0"));
						}
						innerList.add(uF.showData(taskBillRate, "0"));
						innerList.add(taskTotBillAmount+"");
						totBillableamt += taskTotBillAmount;
						hmTaskEmpMap.put(rs.getString("task_id")+"_"+taskEmpList.get(a), innerList);
						
						emp_count++;
					}
				}
				taskCount++;
			}
			rs.close();
			pst.close();
			
			System.out.println("taskCount ===>> " + taskCount);
			System.out.println("proEmpList.size() ===>> " + proEmpList.size());
			
			if(proEmpList != null && proEmpList.size() > taskCount) {
				for(int i=taskCount; i<proEmpList.size(); i++) {
					List<String> innerList = new ArrayList<String>();
					innerList.add("");
					StringBuilder sbEmpOption = new StringBuilder();
					for(int a=0; proEmpList != null && a<proEmpList.size(); a++) {
						sbEmpOption.append("<option value="+proEmpList.get(a)+">"+CF.getEmpNameMapByEmpId(con, proEmpList.get(a))+"</option>");
					}
					
					innerList.add(sbEmpOption.toString());
					innerList.add("");
					innerList.add("");
					innerList.add("");
					
					hmTaskEmpMap.put("O_"+i, innerList);
					emp_count++;
				}
			}
			
			System.out.println("hmTaskEmpMap ===>> " + hmTaskEmpMap);
			
			request.setAttribute("hmTaskEmpMap", hmTaskEmpMap);
			request.setAttribute("totBillableamt", uF.formatIntoTwoDecimalWithOutComma(totBillableamt));
			request.setAttribute("emp_count", ""+emp_count);

			/*if(uF.parseToInt(getInvoice_format_id()) > 0) {
				InvoiceFormatwiseData invoiceFormatwiseData = new InvoiceFormatwiseData(request, session, CF, uF, con, getPro_id());
				Map<String, String> hmProInvoiceData = invoiceFormatwiseData.getInvoiceFormatData();
				request.setAttribute("hmProInvoiceData", hmProInvoiceData);
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void insertProjectInvoice(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			boolean flag = false;
			pst = con.prepareStatement("select * from promntc_invoice_details where invoice_code = ? ");
			pst.setString(1, getInvoiceCode().trim());
			rs = pst.executeQuery();
			while (rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			
			Map<String,String> hmProjectDetails=(Map<String,String>)request.getAttribute("hmProjectDetails");
			Map<String, Map<String, String>> hmWorkLocation =CF.getWorkLocationMap(con);
			Map<String, String> hmLocation=hmWorkLocation.get(hmProjectDetails.get("WLOCATION_ID"));
			Map<String,String> hmClientDetails=(Map<String,String>)request.getAttribute("hmClientDetails");
//			Map<String,String> hmTaxMiscSetting=(Map<String,String>)request.getAttribute("hmTaxMiscSetting");
			
			Map<String, String> hmDept =CF.getDeptMap(con);
			
			String[] arr=CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
//			String locationCode=hmLocation.get("WL_NAME").substring(0,3);
			String locationCode=hmLocation.get("WL_NAME");
			String departCode="";
			if(hmDept.get(hmProjectDetails.get("DEPARTMENT_ID"))!=null && hmDept.get(hmProjectDetails.get("DEPARTMENT_ID")).contains(" ")) {
				String[] temp=hmDept.get(hmProjectDetails.get("DEPARTMENT_ID")).toUpperCase().split(" ");
				for(int i=0;i<temp.length;i++) {
					departCode+=temp[i].substring(0,1);
				}
			} else if(hmDept.get(hmProjectDetails.get("DEPARTMENT_ID"))!=null) {
				departCode = hmDept.get(hmProjectDetails.get("DEPARTMENT_ID")).substring(0, hmDept.get(hmProjectDetails.get("DEPARTMENT_ID")).length()>3 ? 3 : hmDept.get(hmProjectDetails.get("DEPARTMENT_ID")).length());
			}
			
			int count=0;
//			pst=con.prepareStatement("select max(promntc_invoice_id) as promntc_invoice_id from promntc_invoice_details where wlocation_id=?");
			pst=con.prepareStatement("select max(invoice_no) as invoice_no from promntc_invoice_details where wlocation_id=?");
			pst.setInt(1, uF.parseToInt(hmProjectDetails.get("WLOCATION_ID")));
//			pst.setInt(2, uF.parseToInt(hmProjectDetails.get("DEPARTMENT_ID")));
			rs=pst.executeQuery();
			while(rs.next()) {
				count=rs.getInt("invoice_no");
			}
			rs.close();
			pst.close();
			count++;
			String invoiceCode=count+"-"+uF.getDateFormat(arr[0], DATE_FORMAT, "yyyy")+"-"+uF.getDateFormat(arr[1], DATE_FORMAT, "yy")+"/"+locationCode.toUpperCase(); //+"/"+departCode.toUpperCase()
			if(getInvoiceCode() == null || getInvoiceCode().trim().equals("")) {
				setInvoiceCode(invoiceCode);
			} else if(flag) {
				setInvoiceCode(invoiceCode);
			}
			
			String btype="";
			if(getBill_type()!=null && getBill_type().equals("Daily")) {
				btype="Days";
			} else if(getBill_type()!=null && getBill_type().equals("Hourly")) {
				btype="Hours";
			} else if(getBill_type()!=null && getBill_type().equals("Monthly")) {
				btype="Months";
			}
			
//			System.out.println("getStrProjectOwner() 1 ===>> " + getStrProjectOwner());
			
			pst = con.prepareStatement("insert into promntc_invoice_details(invoice_generated_date,invoice_generated_by,invoice_from_date," +
				"invoice_to_date,pro_id,invoice_code,project_description,other_description,spoc_id,address_id,pro_owner_id,financial_start_date," +
				"financial_end_date,wlocation_id,depart_id,invoice_amount,particulars_total_amount,curr_id,other_amount,other_particular,bank_branch_id," +
				"invoice_type,client_id,bill_type,standard_tax,education_tax,service_tax,reference_no_desc,oc_particulars_total_amount," +
				"oc_invoice_amount,oc_other_amount,pro_freq_id,entry_date,invoice_no,invoice_template_id)" +
				"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
			if(getInvoiceGenDate() != null && !getInvoiceGenDate().equals("")) {
				pst.setDate(1, uF.getDateFormat(getInvoiceGenDate(), DATE_FORMAT));
			} else {
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			}
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
				pst.setDate(3, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
				pst.setDate(3, uF.getDateFormat(getStrStartDateOtherCurr(), DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(getStrEndDateOtherCurr(), DATE_FORMAT));
			}
			pst.setInt(5, uF.parseToInt(getPro_id()));
			pst.setString(6, getInvoiceCode());
			if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE || uF.parseToInt(getInvoice_format_id()) > 0) {
				pst.setString(7, getProDescription());
				pst.setString(8, getOtherDescription());
			} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
				pst.setString(7, getProDescriptionOtherCurr());
				pst.setString(8, getOtherDescriptionOtherCurr());
			}
			pst.setInt(9, uF.parseToInt(getClientPoc()));
			pst.setInt(10, uF.parseToInt(getClientAddress()));
			pst.setInt(11, uF.parseToInt(getStrProjectOwner()));
			pst.setDate(12, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(13, uF.getDateFormat(arr[1], DATE_FORMAT));
			pst.setInt(14, uF.parseToInt(hmProjectDetails.get("WLOCATION_ID")));
			pst.setInt(15, uF.parseToInt(hmProjectDetails.get("DEPARTMENT_ID"))); 
			if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
				pst.setDouble(16, uF.parseToDouble(getTotalAmt()));
				pst.setDouble(17, uF.parseToDouble(getParticularTotalAmt()));
			} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
				pst.setDouble(16, uF.parseToDouble(getTotalAmtINRCurr()));
				pst.setDouble(17, uF.parseToDouble(getParticularTotalAmtINRCurr()));
			}
//			pst.setInt(18, uF.parseToInt(hmProjectDetails.get("CURRENCY_ID")));
			pst.setInt(18, uF.parseToInt(getStrCurrency()));
			if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
//				pst.setDouble(19, uF.parseToDouble(getStrOtherParticularsAmt()));
//				pst.setString(20, getStrOtherParticulars());
				pst.setDouble(19, 0);
				pst.setString(20, "");
			} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
//				pst.setDouble(19, uF.parseToDouble(getStrOtherParticularsAmtINRCurr()));
//				pst.setString(20, getStrOtherParticularsINRCurr());
				pst.setDouble(19, 0);
				pst.setString(20, "");
			}
			pst.setInt(21, uF.parseToInt(getBankName())); 
			pst.setInt(22, PRORETA_INVOICE); 
			pst.setInt(23, uF.parseToInt(hmClientDetails.get("CLIENT_ID")));			
			pst.setString(24, btype);
//			pst.setDouble(25, uF.parseToDouble(hmTaxMiscSetting.get("STANDARD_TAX")));
//			pst.setDouble(26, uF.parseToDouble(hmTaxMiscSetting.get("EDUCATION_TAX")));
//			pst.setDouble(27, uF.parseToDouble(hmTaxMiscSetting.get("SERVICE_TAX")));
			pst.setDouble(25, 0);
			pst.setDouble(26, 0);
			pst.setDouble(27, 0);
			if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
				pst.setString(28, getStrReferenceNo());
				pst.setDouble(29, uF.parseToDouble(getParticularTotalAmt()));
				pst.setDouble(30, uF.parseToDouble(getTotalAmt()));
//				pst.setDouble(31, uF.parseToDouble(getStrOtherParticularsAmt()));
				pst.setDouble(31, 0);
			} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
				pst.setString(28, getStrReferenceNoOtherCurr());
				pst.setDouble(29, uF.parseToDouble(getParticularTotalAmtOtherCurr()));
				pst.setDouble(30, uF.parseToDouble(getTotalAmtOtherCurr()));
//				pst.setDouble(31, uF.parseToDouble(getStrOtherParticularsAmtOtherCurr()));
				pst.setDouble(31, 0);
			}
			pst.setInt(32, uF.parseToInt(getPro_freq_id()));
			pst.setDate(33, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(34, count);
			pst.setInt(35, uF.parseToInt(getInvoice_format_id()));
			int x = pst.executeUpdate();
			pst.close();
			
			if(x>0) {
				int promntc_invoice_id = 0;
				pst = con.prepareStatement("select max(promntc_invoice_id) as promntc_invoice_id from promntc_invoice_details");
				rs = pst.executeQuery();
				while (rs.next()) {
					promntc_invoice_id = rs.getInt("promntc_invoice_id");
				}
				rs.close();
				pst.close();
				
				//emp_id,days_hours,_rate
				
				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, getPro_id());
				
				if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
					for(int i=0;getStrEmp()!=null && i<getStrEmp().length; i++) {
						if(uF.parseToInt(getStrEmp()[i]) > 0 && uF.parseToDouble(getBillDailyHours()[i]) > 0) {
							pst = con.prepareStatement("insert into promntc_invoice_amt_details(emp_id,days_hours,_rate,invoice_particulars_amount," +
								"promntc_invoice_id,oc_invoice_particulars_amount,task_id) values(?,?,?,?, ?,?,?)");
							pst.setInt(1, uF.parseToInt(getStrEmp()[i]));
							if(hmProjectData.get("PRO_BILLING_ACTUAL_TYPE")!=null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("H")) {
								pst.setDouble(2, uF.parseToDouble(uF.getTotalTimeMinutes60To100(getBillDailyHours()[i])));
							} else {
								pst.setDouble(2, uF.parseToDouble(getBillDailyHours()[i]));
							}
							pst.setDouble(3, uF.parseToDouble(getEmpRate()[i]));
							pst.setDouble(4, uF.parseToDouble(getStrEmpAmt()[i]));
							pst.setInt(5, promntc_invoice_id);
							pst.setDouble(6, uF.parseToDouble(getStrEmpAmt()[i]));
							pst.setInt(7, uF.parseToInt(getHideTaskId()[i]));
							pst.executeUpdate();
							pst.close();
						}
					}
				} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
					for(int i=0;getStrEmpOtherCurr()!=null && i<getStrEmpOtherCurr().length; i++) {
						if(uF.parseToInt(getStrEmpOtherCurr()[i]) > 0 && uF.parseToDouble(getBillDailyHoursINRCurr()[i]) > 0) {
							pst = con.prepareStatement("insert into promntc_invoice_amt_details(emp_id,days_hours,_rate,invoice_particulars_amount," +
								"promntc_invoice_id,oc_invoice_particulars_amount,task_id) values(?,?,?,?, ?,?,?)");
							pst.setInt(1, uF.parseToInt(getStrEmpOtherCurr()[i]));
							if(hmProjectData.get("PRO_BILLING_ACTUAL_TYPE")!=null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("H")) {
								pst.setDouble(2, uF.parseToDouble(uF.getTotalTimeMinutes60To100(getBillDailyHoursINRCurr()[i])));
							} else {
								pst.setDouble(2, uF.parseToDouble(getBillDailyHoursINRCurr()[i]));
							}
							pst.setDouble(3, uF.parseToDouble(getEmpRateINRCurr()[i]));
							pst.setDouble(4, uF.parseToDouble(getStrEmpAmtINRCurr()[i]));
							pst.setInt(5, promntc_invoice_id);
							pst.setDouble(6, uF.parseToDouble(getStrEmpAmtOtherCurr()[i]));
							pst.setInt(7, uF.parseToInt(getHideTaskIdOtherCurr()[i]));
							pst.executeUpdate();
							pst.close();
						}
					}
				}
				
				
				double dblTotOpe = 0;
				double dblTotOpeOtherCurr = 0;
				if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
					for(int i=0; getStrOtherParticulars()!=null && i<getStrOtherParticulars().length; i++) {
						pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars,invoice_particulars_amount," +
							"promntc_invoice_id,oc_invoice_particulars_amount,head_type) values(?,?,?,?, ?)");
						pst.setString(1, getStrOtherParticulars()[i]);
						pst.setDouble(2, uF.parseToDouble(getStrOtherParticularsAmt()[i]));
						pst.setInt(3, promntc_invoice_id);
						pst.setDouble(4, uF.parseToDouble(getStrOtherParticularsAmt()[i]));
						pst.setString(5, HEAD_OPE);
						pst.executeUpdate();
						pst.close();
						dblTotOpe += uF.parseToDouble(getStrOtherParticularsAmt()[i]);
						dblTotOpeOtherCurr += uF.parseToDouble(getStrOtherParticularsAmt()[i]);
					}
				} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
					for(int i=0; getStrOtherParticularsINRCurr()!=null && i<getStrOtherParticularsINRCurr().length; i++) {
					pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars,invoice_particulars_amount," +
						"promntc_invoice_id,oc_invoice_particulars_amount,head_type) values(?,?,?,?, ?)");
					pst.setString(1, getStrOtherParticularsINRCurr()[i]);
					pst.setDouble(2, uF.parseToDouble(getStrOtherParticularsAmtINRCurr()[i]));
					pst.setInt(3, promntc_invoice_id);
					pst.setDouble(4, uF.parseToDouble(getStrOtherParticularsAmtOtherCurr()[i]));
					pst.setString(5, HEAD_OPE);
					pst.executeUpdate();
					pst.close();
					dblTotOpe += uF.parseToDouble(getStrOtherParticularsAmtINRCurr()[i]);
					dblTotOpeOtherCurr += uF.parseToDouble(getStrOtherParticularsAmtOtherCurr()[i]);
				}
				}
				
				pst = con.prepareStatement("update promntc_invoice_details set other_amount=?, oc_other_amount=? where promntc_invoice_id=?");
				pst.setDouble(1, dblTotOpe);
				pst.setDouble(2, dblTotOpeOtherCurr);
				pst.setInt(3, promntc_invoice_id); 
				pst.executeUpdate();   
				pst.close();

				
				if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_ONE) {
					for(int i=0; getTaxHead()!=null && i<getTaxHead().length; i++) {
						pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars, invoice_particulars_amount, promntc_invoice_id," +
							"oc_invoice_particulars_amount,tax_percent,head_type,invoice_particulars_label) values(?,?,?,?, ?,?,?)");
						pst.setString(1, uF.showData(getTaxHead()[i], ""));
						pst.setDouble(2, uF.parseToDouble(getTaxHeadAmt()[i]));
						pst.setInt(3, promntc_invoice_id);
						pst.setDouble(4, uF.parseToDouble(getTaxHeadAmt()[i]));
						pst.setDouble(5, uF.parseToDouble(getTaxHeadPercent()[i]));
						pst.setString(6, HEAD_TAX);
						pst.setString(7, uF.showData(getTaxNameLabel()[i], ""));
						pst.executeUpdate();
						pst.close();
					}
				} else if(uF.parseToInt(getInvoiceType()) == INVC_FORMAT_TWO) {
					for(int i=0; getTaxHeadOtherCurr()!=null && i<getTaxHeadOtherCurr().length; i++) {
						pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars, invoice_particulars_amount, promntc_invoice_id," +
							"oc_invoice_particulars_amount,tax_percent,head_type,invoice_particulars_label) values(?,?,?,?, ?,?,?)");
						pst.setString(1, uF.showData(getTaxHeadOtherCurr()[i], ""));
						pst.setDouble(2, uF.parseToDouble(getTaxHeadAmtINRCurr()[i]));
						pst.setInt(3, promntc_invoice_id);
						pst.setDouble(4, uF.parseToDouble(getTaxHeadAmtOtherCurr()[i]));
						pst.setDouble(5, uF.parseToDouble(getTaxHeadPercentOtherCurr()[i]));
						pst.setString(6, HEAD_TAX);
						pst.setString(7, uF.showData(getTaxNameLabelOtherCurr()[i], ""));
						pst.executeUpdate();
						pst.close();
					}
				}
				
//				pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars,invoice_particulars_amount,promntc_invoice_id," +
//					"oc_invoice_particulars_amount) values(?,?,?,?)");
//				pst.setString(1, "STAX");
//				pst.setDouble(2, uF.parseToDouble(getServiceTaxAmt()));
//				pst.setInt(3, promntc_invoice_id);
//				pst.setDouble(4, uF.parseToDouble(getServiceTaxAmt()));
//				pst.executeUpdate();
//				pst.close();
//				
//				pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars,invoice_particulars_amount,promntc_invoice_id," +
//					"oc_invoice_particulars_amount) values(?,?,?,?)");
//				pst.setString(1, "EDUCESS");
//				pst.setDouble(2, uF.parseToDouble(getEduCessAmt()));
//				pst.setInt(3, promntc_invoice_id);
//				pst.setDouble(4, uF.parseToDouble(getEduCessAmt()));
//				pst.executeUpdate();
//				pst.close();
//				
//				pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars,invoice_particulars_amount,promntc_invoice_id," +
//					"oc_invoice_particulars_amount) values(?,?,?,?)");
//				pst.setString(1, "SHSCESS");
//				pst.setDouble(2, uF.parseToDouble(getStdTaxAmt()));
//				pst.setInt(3, promntc_invoice_id);
//				pst.setDouble(4, uF.parseToDouble(getStdTaxAmt()));
//				pst.executeUpdate();
//				pst.close();
				
				/**
				 * Alerts
				 * */
				String strDomain = request.getServerName().split("\\.")[0];
				
				String proName = CF.getProjectNameById(con, getPro_id());
				String alertData = "<div style=\"float: left;\"> <b>"+getInvoiceCode()+"</b> new invoice has been generated for <b>"+proName+"</b> project by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
				String alertAction = "Billing.action";
				if(uF.parseToInt(getStrProjectOwner()) > 0){
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(getStrProjectOwner());
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
//					userAlerts.set_type(INVOICE_GENERATED_ALERT);
					userAlerts.setStatus(INSERT_TR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
					StringBuilder activityData = new StringBuilder();
					activityData.append("<div style=\"float: left; width: 100%; padding: 5px 0px;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px; margin-right: 2px;\"> I </span> <b>"+getInvoiceCode()+"</b> new invoice has been generated for <b>"+proName+"</b> project by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>");
					UserActivities userAct = new UserActivities(con, uF, CF, request);
					userAct.setStrDomain(strDomain);
					userAct.setStrAlignWith(INVOICE+"");
					userAct.setStrAlignWithId(promntc_invoice_id+"");
					userAct.setStrTaggedWith(","+getStrProjectOwner()+",");
					userAct.setStrVisibilityWith(","+getStrProjectOwner()+",");
					userAct.setStrVisibility("2");
					userAct.setStrData(activityData.toString());
					userAct.setStrSessionEmpId(strSessionEmpId);
					userAct.setStatus(INSERT_TR_ACTIVITY);
					Thread tt = new Thread(userAct);
					tt.run();
				}
				
				if(uF.parseToInt(getClientPoc()) > 0){
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(getClientPoc());
					userAlerts.setStrOther("other");
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
//					userAlerts.set_type(INVOICE_GENERATED_ALERT);
					userAlerts.setStatus(INSERT_TR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
					StringBuilder activityData = new StringBuilder();
					activityData.append("<div style=\"float: left; width: 100%; padding: 5px 0px;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px; margin-right: 2px;\"> I </span> <b>"+getInvoiceCode()+"</b> new invoice has been generated for <b>"+proName+"</b> project by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>");
					UserActivities userAct = new UserActivities(con, uF, CF, request);
					userAct.setStrDomain(strDomain);
					userAct.setStrAlignWith(INVOICE+"");
					userAct.setStrAlignWithId(promntc_invoice_id+"");
					userAct.setStrTaggedWith(","+getClientPoc()+",");
					userAct.setStrVisibilityWith(","+getClientPoc()+",");
					userAct.setStrVisibility("2");
					userAct.setStrData(activityData.toString());
					userAct.setStrSessionEmpId(strSessionEmpId);
					userAct.setStatus(INSERT_TR_ACTIVITY);
					userAct.setStrOther("other");
					if(strUserType.equals(CUSTOMER)) {
						userAct.setStrUserType("C");
					}
					Thread tt = new Thread(userAct);
					tt.run();
				}
			
				
				pst = con.prepareStatement("select pf.*,p.pro_id,actual_calculation_type from projectmntnc p, projectmntnc_frequency pf " +
				"where p.billing_type != 'F' and p.pro_id = pf.pro_id and pf.pro_freq_id=?");
				pst.setInt(1, uF.parseToInt(getPro_freq_id()));
				rs = pst.executeQuery();
				while(rs.next()) {
					hmProjectData.put("PRO_FREQ_NAME", rs.getString("pro_freq_name"));
					hmProjectData.put("PRO_FREQ_START_DATE", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT));
					hmProjectData.put("PRO_FREQ_END_DATE", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT));
				}
				rs.close();
				pst.close();
			
				Notifications nF = new Notifications(N_PAYMENT_ALERT, CF); 
				nF.setDomain(strDomain);
				
				nF.request = request;
				nF.setStrOrgId((String)session.getAttribute(ORGID));
				nF.setEmailTemplate(true);
	
				nF.setStrEmpId(strSessionEmpId);
				pst = con.prepareStatement("select * from client_poc where poc_id = ?");
				pst.setInt(1, uF.parseToInt(getClientPoc()));
				rs = pst.executeQuery();
				boolean flg=false;
				while(rs.next()) {
					nF.setStrCustFName(rs.getString("contact_fname"));
					nF.setStrCustLName(rs.getString("contact_lname"));
					nF.setStrEmpMobileNo(rs.getString("contact_number"));
					if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
						nF.setStrEmpEmail(rs.getString("contact_email"));
						nF.setStrEmailTo(rs.getString("contact_email"));
					}
					flg = true;
				}
				rs.close();
				pst.close();
				
				if(flg) {
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setStrProjectFreqName(hmProjectData.get("PRO_NAME")+" ("+hmProjectData.get("PRO_FREQ_NAME")+")");
					nF.setStrFromDate(hmProjectData.get("PRO_FREQ_START_DATE"));
					nF.setStrToDate(hmProjectData.get("PRO_FREQ_END_DATE"));
					nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
					nF.setStrInvoiceNo(getInvoiceCode());
					nF.sendNotifications(); 
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void getProjectDetails(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			
			if(getStrStartDate()==null && getStrEndDate()==null) {
				
				Date currDate=uF.getCurrentDate(CF.getStrTimeZone());
				
				String startdate="01/"+uF.getDateFormat(""+currDate, DBDATE, "MM")+"/"+uF.getDateFormat(""+currDate, DBDATE, "yyyy");
				
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "MM"))-1);
				calendar.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "yyyy")));
				calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));

				Date date = calendar.getTime();
				DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
				String endDate=DATE_FORMAT.format(date);
				
				setStrStartDate(startdate);
				setStrEndDate(endDate);
				
				setStrStartDateOtherCurr(startdate);
				setStrEndDateOtherCurr(endDate);
			}
			
			if(getSubmit() == null) {
				setInvoiceGenDate(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),DBDATE, DATE_FORMAT));
			}
			
//			if(getStrOtherParticularsAmt()==null || getStrOtherParticularsAmt().equals("") || getStrOtherParticularsAmt().equals(" ")) {
//				setStrOtherParticularsAmt(""+reimbursement_amount);
//			}
//			
//			if(getStrOtherParticularsAmtINRCurr()==null || getStrOtherParticularsAmtINRCurr().equals("") || getStrOtherParticularsAmtINRCurr().equals(" ")) {
//				setStrOtherParticularsAmtINRCurr(""+reimbursement_amount);
//			}
//			
//			if(getStrOtherParticulars()==null || getStrOtherParticulars().equals("") || getStrOtherParticulars().equals(" ")) {
//				setStrOtherParticulars("Out of Pocket Expenses");
//			}
//			
//			if(getStrOtherParticularsINRCurr()==null || getStrOtherParticularsINRCurr().equals("") || getStrOtherParticularsINRCurr().equals(" ")) {
//				setStrOtherParticularsINRCurr("Out of Pocket Expenses");
//			}
			
			pst = con.prepareStatement("select pmt.*, cd.client_name, cd.client_industry, cd.client_address from projectmntnc pmt,client_details cd where pmt.client_id=cd.client_id and pmt.pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			Map<String,String> hmClientDetails=new HashMap<String, String>();
			Map<String,String> hmProjectDetails=new HashMap<String, String>();
			while (rs.next()) {
				hmClientDetails.put("CLIENT_ID", rs.getString("client_id"));
				hmClientDetails.put("CLIENT_NAME", rs.getString("client_name"));
				hmClientDetails.put("CLIENT_INDUSTRY", rs.getString("client_industry"));
				hmClientDetails.put("CLIENT_ADDRESS", rs.getString("client_address"));
				
				setClientAddress(CF.getClientAddressIdByClientId(con, rs.getString("client_id")));
				setClientPoc(rs.getString("poc"));
				
				hmProjectDetails.put("PRO_ID", rs.getString("pro_id"));
				hmProjectDetails.put("PRO_NAME", rs.getString("pro_name"));
				hmProjectDetails.put("PRO_ACTUAL_BILLING_TYPE", rs.getString("actual_calculation_type"));
				if(getStrProjectOwner() == null || getStrProjectOwner().trim().equals("")) { 
					setStrProjectOwner(rs.getString("project_owner"));
				}
				
				hmProjectDetails.put("PRIORITY", rs.getString("priority"));
				hmProjectDetails.put("DESCRIPTION", rs.getString("description"));
				hmProjectDetails.put("ACTIVITY", rs.getString("activity"));
				hmProjectDetails.put("SERVICE", rs.getString("service"));
				hmProjectDetails.put("WLOCATION_ID", rs.getString("wlocation_id"));
				hmProjectDetails.put("DEPARTMENT_ID", rs.getString("department_id"));
				hmProjectDetails.put("CURRENCY_ID", rs.getString("billing_curr_id"));
//				hmProjectDetails.put("PRO_START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
//				hmProjectDetails.put("PRO_END_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
				hmProjectDetails.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
				hmProjectDetails.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
				hmProjectDetails.put("PRO_ORG_ID", rs.getString("org_id"));
				setProCurrency(rs.getString("billing_curr_id"));
				if(getProCurrency() == null) {
					setProCurrency(INR_CURR_ID);
				}
				if(getSubmit() == null) {
					setStrCurrency(rs.getString("billing_curr_id"));
					if(getStrCurrency() == null) {
						setStrCurrency(INR_CURR_ID);
					}
				}
			}  
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select freq_start_date,freq_end_date from projectmntnc_frequency where pro_freq_id=?");
			pst.setInt(1, uF.parseToInt(getPro_freq_id()));
			
			System.out.println("pst ====>>> " + pst);
			
			rs = pst.executeQuery();
			while (rs.next()) {
				hmProjectDetails.put("PRO_START_DATE", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT));
				hmProjectDetails.put("PRO_END_DATE", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT));
				System.out.println("freqStDate==>"+uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT));
				System.out.println("freqEndDate==>"+uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT));
			}  
			rs.close();
			pst.close();
			
			pst=con.prepareStatement("select sum(er.reimbursement_amount) as reimbursement_amount from emp_reimbursement er " +
				" where (er.from_date, er.to_date) overlaps (to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD')) " +
				" and er.is_billable=true and approval_1 =1 and approval_2=1 and er.pro_id = ?");
			pst.setDate(1, uF.getDateFormat(hmProjectDetails.get("PRO_START_DATE"), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(hmProjectDetails.get("PRO_END_DATE"), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getPro_id()));
			rs=pst.executeQuery();
			int reimbursement_amount=0;
			while(rs.next()) {
				reimbursement_amount=rs.getInt("reimbursement_amount");
				System.out.println("reimbursement_amount==>"+rs.getInt("reimbursement_amount"));
			}
			rs.close();
			pst.close();
			double remainReimburseAmt = uF.parseToDouble(reimbursement_amount+"") - uF.parseToDouble(getProOPEAmt());
			if(remainReimburseAmt < 0) {
				remainReimburseAmt = 0;
			}
			System.out.println("remainReimburseAmt =======>> " + remainReimburseAmt);
			request.setAttribute("reimbursement_amount", uF.formatIntoOneDecimalWithOutComma(remainReimburseAmt));
			
			pst = con.prepareStatement("select * from porject_billing_heads_details where pro_id=? and (head_data_type="+DT_OPE+" or head_data_type="+DT_OPE_OVERALL+") order by pro_billing_head_id");
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst======main==="+pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmProBillingHeadData = new LinkedHashMap<String, List<String>>();
			int strPerticnt = 0;
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("pro_billing_head_id"));
				alInner.add(rs.getString("head_label"));
				alInner.add(rs.getString("head_data_type"));
				alInner.add(rs.getString("head_other_variable"));
				alInner.add(rs.getString("billing_head_id"));
				if(uF.parseToInt(rs.getString("head_data_type")) != DT_OPE && uF.parseToInt(rs.getString("head_data_type")) != DT_OPE_OVERALL) {
					strPerticnt++;
				}
				hmProBillingHeadData.put(rs.getString("pro_billing_head_id"), alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProBillingHeadData", hmProBillingHeadData);
			request.setAttribute("strPerticnt", strPerticnt+"");
			
			
			pst = con.prepareStatement("select * from project_tax_setting where pro_id=? and invoice_or_customer=1 and status=true order by pro_tax_setting_id");
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst======main==="+pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmProTaxHeadData = new LinkedHashMap<String, List<String>>();
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("pro_tax_setting_id"));
				alInner.add(rs.getString("tax_name_label"));
				alInner.add(rs.getString("tax_percent"));
				alInner.add(rs.getString("tax_name"));
				hmProTaxHeadData.put(rs.getString("pro_tax_setting_id"), alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProTaxHeadData", hmProTaxHeadData);
			
			
			String otherDescription = "";
			pst = con.prepareStatement("select * from additional_info_of_pro_invoice where pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			while (rs.next()) {
				otherDescription = uF.showData(rs.getString("additional_info_text"), "");
			}
			rs.close();
			pst.close();
			request.setAttribute("otherDescription", otherDescription);
			
//			pst = con.prepareStatement("select * from deduction_tax_misc_details where ? between financial_year_from and financial_year_to");
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
//			Map<String,String> hmTaxMiscSetting = new HashMap<String, String>();
//			while (rs.next()) {
//				hmTaxMiscSetting.put("DEDUCTION_TAX_MISC_ID", rs.getString("deduction_tax_misc_id"));
//				hmTaxMiscSetting.put("STANDARD_TAX", rs.getString("standard_tax"));
//				hmTaxMiscSetting.put("EDUCATION_TAX", rs.getString("education_tax"));
//				hmTaxMiscSetting.put("FLAT_TDS", rs.getString("flat_tds"));				
//				hmTaxMiscSetting.put("SERVICE_TAX", rs.getString("service_tax"));
//				hmTaxMiscSetting.put("DEDUCTION_TYPE", rs.getString("deduction_type"));
//				hmTaxMiscSetting.put("FINANCIAL_YEAR_FROM", rs.getString("financial_year_from"));
//				hmTaxMiscSetting.put("FINANCIAL_YEAR_TO", rs.getString("financial_year_to"));
//				hmTaxMiscSetting.put("YEAR", rs.getString("_year"));
//				hmTaxMiscSetting.put("ENTRY_TIMESTAMP", rs.getString("entry_timestamp"));
//				hmTaxMiscSetting.put("USER_ID", rs.getString("user_id"));
//				hmTaxMiscSetting.put("ACTIVITY", rs.getString("trail_status"));
//				hmTaxMiscSetting.put("STATE_ID", rs.getString("state_id"));
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("hmTaxMiscSetting", hmTaxMiscSetting);			
			
			request.setAttribute("hmClientDetails", hmClientDetails);
			request.setAttribute("hmProjectDetails", hmProjectDetails);
			
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			request.setAttribute("hmCurrencyDetails", hmCurrencyDetails);
			
			
//	 		=============================== Invoice Code Generation ======================================
			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con);
			Map<String, String> hmLocation = hmWorkLocation.get(hmProjectDetails.get("WLOCATION_ID"));
			Map<String, String> hmDept = CF.getDeptMap(con);
			
			String[] arr = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
			String locationCode = hmLocation.get("WL_NAME");			
			String departCode = "";
			if(hmDept.get(hmProjectDetails.get("DEPARTMENT_ID"))!=null && hmDept.get(hmProjectDetails.get("DEPARTMENT_ID")).contains(" ")) {
				String[] temp = hmDept.get(hmProjectDetails.get("DEPARTMENT_ID")).toUpperCase().split(" ");
				for(int i=0;i<temp.length;i++) {
					departCode+=temp[i].substring(0,1);
				}
			} else if(hmDept.get(hmProjectDetails.get("DEPARTMENT_ID"))!=null) {
				departCode = hmDept.get(hmProjectDetails.get("DEPARTMENT_ID")).substring(0, hmDept.get(hmProjectDetails.get("DEPARTMENT_ID")).length()>3 ? 3 : hmDept.get(hmProjectDetails.get("DEPARTMENT_ID")).length());
			}
			
			int count=0;
			pst=con.prepareStatement("select max(invoice_no) as invoice_no from promntc_invoice_details where wlocation_id=?");
			pst.setInt(1, uF.parseToInt(hmProjectDetails.get("WLOCATION_ID")));
			rs=pst.executeQuery();
			while(rs.next()) {
				count=rs.getInt("invoice_no");
			}
			rs.close();
			pst.close();
			count++;
			
			String invoiceCode = count + "-" + uF.getDateFormat(arr[0], DATE_FORMAT, "yyyy") + "-" + uF.getDateFormat(arr[1], DATE_FORMAT, "yy") + "/" + locationCode.toUpperCase(); //+"/"+departCode.toUpperCase()
			if(getInvoiceCode() == null || getInvoiceCode().trim().equals("")) {
				setInvoiceCode(invoiceCode);
			}
// 		========================================================= End =========================================================			
	
			
			pst=con.prepareStatement("select emp_id,emp_rate_per_hour,emp_rate_per_day,emp_rate_per_month from project_emp_details where pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs=pst.executeQuery();
			Map<String,String> hmEmpBillRate = new HashMap<String, String>();
			List<String> proEmpList = new ArrayList<String>();
			while(rs.next()) {
				if(hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE") != null && hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE").equals("D")) {
					hmEmpBillRate.put(rs.getString("emp_id"), rs.getString("emp_rate_per_day"));
				} else if(hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE") != null && hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE").equals("M")) {
					hmEmpBillRate.put(rs.getString("emp_id"), rs.getString("emp_rate_per_month"));
				} else {
					hmEmpBillRate.put(rs.getString("emp_id"), rs.getString("emp_rate_per_hour"));
				}
				proEmpList.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpBillRate", hmEmpBillRate);
			
			
//			pst=con.prepareStatement("select emp_per_id,emp_fname,emp_lname from employee_personal_details where emp_per_id in (select emp_id from activity_info where pro_id=?)");
//			pst=con.prepareStatement("select task_id,resource_ids from activity_info where pro_id = ? ");
			pst=con.prepareStatement("select a.* from (select task_id,activity_name,parent_task_id,pro_id,resource_ids from activity_info where " +
				"task_id not in (select parent_task_id from activity_info where parent_task_id is not null)) a where (parent_task_id in (select " +
				"task_id from activity_info) or parent_task_id = 0) and a.pro_id =?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs=pst.executeQuery();
			Map<String,List<String>> hmTaskEmpMap = new LinkedHashMap<String, List<String>>();
			int taskCount = 0;
			int emp_count = 0;
			double totBillableamt = 0;
			while(rs.next()) {
				
				List<String> taskEmpList = new ArrayList<String>();
				if(rs.getString("resource_ids") != null) {
					taskEmpList = Arrays.asList(rs.getString("resource_ids").split(","));
				}
				for(int a=0; taskEmpList != null && !taskEmpList.isEmpty() && a<taskEmpList.size(); a++) {
					if(taskEmpList.get(a) != null && !taskEmpList.get(a).equals("")) {
						List<String> innerList = new ArrayList<String>();
						
						System.out.println("task id==>"+rs.getString("task_id"));
						
						innerList.add(rs.getString("task_id"));
						
						StringBuilder sbEmpOption = new StringBuilder();
						for(int i=0; proEmpList != null && i<proEmpList.size(); i++) {
							if(proEmpList.get(i).equals(taskEmpList.get(a))) {
								sbEmpOption.append("<option value=" + proEmpList.get(i) + " selected>" + CF.getEmpNameMapByEmpId(con, proEmpList.get(i))
									+" ("+CF.getProjectTaskNameByTaskId(con, uF, rs.getString("task_id"))+")" + "</option>");
							} else {
								sbEmpOption.append("<option value=" + proEmpList.get(i) + ">" + CF.getEmpNameMapByEmpId(con, proEmpList.get(i)) + "</option>");
							}
							
						}
						innerList.add(sbEmpOption.toString());
						
						String taskPaidEfforts = getTaskTotalPaidEfforts(con, rs.getString("task_id"), taskEmpList.get(a));
						
						System.out.println("taskPaidEfforts==>"+taskPaidEfforts);
						
						String taskEfforts = getTaskTotalDaysOrHours(con, rs.getString("task_id"), taskEmpList.get(a), hmProjectDetails);
						System.out.println("taskEfforts1==>"+taskEfforts);
						
						Map<String, String> hmEmpTaskTotMonthsAndCost = new HashMap<String, String>();
						
						
						if(hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE") != null && hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE").equals("M")) {
							
							hmEmpTaskTotMonthsAndCost = CF.getProjectEmpTaskActualWorkedMonthsCostOrRate(con,request, CF, rs.getString("task_id"), taskEmpList.get(a), hmEmpBillRate, hmProjectDetails);
//							String strBillMonth = hmEmpTaskTotMonthsAndCost.get("BILL_WORKING");
							taskEfforts = hmEmpTaskTotMonthsAndCost.get("BILLABLE_WORKING");
//							double dblTaskPaidEfforts = uF.parseToDouble(taskPaidEfforts) * 30; 
						}
											
						double dblUnpaidEfforts = uF.parseToDouble(taskEfforts) - uF.parseToDouble(taskPaidEfforts);
						
						System.out.println(rs.getString("task_id")+" == " + taskEmpList.get(a) + " == taskPaidEfforts == " +taskPaidEfforts + " == taskEfforts == " + taskEfforts +" == dblUnpaidEfforts ==>> " + dblUnpaidEfforts);
						if(dblUnpaidEfforts < 0) 
							dblUnpaidEfforts = 0;
						String taskBillRate = hmEmpBillRate.get(taskEmpList.get(a));
//						System.out.println("taskBillRate==>"+taskBillRate);
						
						double taskTotBillAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblUnpaidEfforts)) * uF.parseToDouble(taskBillRate);
//						System.out.println("taskTotBillAmount==>"+taskTotBillAmount);
						
						if(hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE") != null && hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE").equals("H")) {
							innerList.add(uF.showData(uF.getTotalTimeMinutes100To60(""+dblUnpaidEfforts), "0"));
//							System.out.println("in if after taskToBillAmount");
						} else {
							innerList.add(uF.showData(uF.formatIntoTwoDecimalWithOutComma(dblUnpaidEfforts), "0"));
//							System.out.println("in else of ==>"+uF.showData(uF.formatIntoTwoDecimalWithOutComma(dblUnpaidEfforts), "0"));
						}
						innerList.add(uF.showData(taskBillRate, "0"));
						innerList.add(taskTotBillAmount+"");
						totBillableamt += taskTotBillAmount;
						hmTaskEmpMap.put(rs.getString("task_id")+"_"+taskEmpList.get(a), innerList);
						
						emp_count++;
					}
				}
				taskCount++;
			}
			rs.close();
			pst.close();
			
//			System.out.println("taskCount ===>> " + taskCount);
//			System.out.println("proEmpList ===>> " + proEmpList.size());
			
			if(proEmpList != null && proEmpList.size() > taskCount) {
				for(int i=taskCount; i<proEmpList.size(); i++) {
					List<String> innerList = new ArrayList<String>();
					innerList.add("");
					StringBuilder sbEmpOption = new StringBuilder();
					for(int a=0; proEmpList != null && a<proEmpList.size(); a++) {
						sbEmpOption.append("<option value="+proEmpList.get(a)+">"+CF.getEmpNameMapByEmpId(con, proEmpList.get(a))+"</option>");
					}
					
					innerList.add(sbEmpOption.toString());
					innerList.add("");
					innerList.add("");
					innerList.add("");
					
					hmTaskEmpMap.put("O_"+i, innerList);
					emp_count++;
				}
			}
			
//			System.out.println("hmTaskEmpMap ===>> " + hmTaskEmpMap);
			
			request.setAttribute("hmTaskEmpMap", hmTaskEmpMap);
			request.setAttribute("totBillableamt", uF.formatIntoTwoDecimalWithOutComma(totBillableamt));
			request.setAttribute("emp_count", ""+emp_count);

//			String sercviceTax = uF.showData(hmTaxMiscSetting.get("SERVICE_TAX"),"0");
//			String eduCess = uF.showData(hmTaxMiscSetting.get("EDUCATION_TAX"),"0");
//			String standardTax = uF.showData(hmTaxMiscSetting.get("STANDARD_TAX"),"0");
			
//			double serviceTax = (totBillableamt * uF.parseToDouble(sercviceTax)) /100;
//			double sTaxamt = serviceTax;
//			double eduCessamt = (serviceTax * uF.parseToDouble(eduCess)) / 100;
//			double stdCessamt = (serviceTax * uF.parseToDouble(standardTax)) / 100;
			
//			double totalBillAmount = totBillableamt + sTaxamt+ eduCessamt + stdCessamt;
//			
//			request.setAttribute("sTaxamt", uF.formatIntoTwoDecimalWithOutComma(sTaxamt));
//			request.setAttribute("eduCessamt", uF.formatIntoTwoDecimalWithOutComma(eduCessamt));
//			request.setAttribute("stdCessamt", uF.formatIntoTwoDecimalWithOutComma(stdCessamt));
//			request.setAttribute("totalBillAmount", uF.formatIntoTwoDecimalWithOutComma(totalBillAmount));
			
			if(uF.parseToInt(getInvoice_format_id()) > 0) {
				InvoiceFormatwiseData invoiceFormatwiseData = new InvoiceFormatwiseData(request, session, CF, uF, con, getPro_id());
				Map<String, String> hmProInvoiceData = invoiceFormatwiseData.getInvoiceFormatData();
				
				request.setAttribute("hmProInvoiceData", hmProInvoiceData);
//				System.out.println("hmProInvoiceData ====>>> " + hmProInvoiceData);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
	private String getTaskTotalPaidEfforts(Connection con, String taskId, String resourceId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		String totDayOrHrs = null;
		try {
			
			StringBuilder sbInvcIds = null;
			pst = con.prepareStatement("select promntc_invoice_id from promntc_invoice_details where pro_id=?");				
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();			 
			while(rs.next()) {
				if(sbInvcIds == null) {
					sbInvcIds = new StringBuilder();
					sbInvcIds.append(rs.getString("promntc_invoice_id"));
				} else {
					sbInvcIds.append(","+rs.getString("promntc_invoice_id"));
				}
			}
			rs.close();
			pst.close();
			if(sbInvcIds == null) {
				sbInvcIds = new StringBuilder();
			}
			
			if(sbInvcIds != null && !sbInvcIds.toString().equals("")) {
				pst = con.prepareStatement("select sum(days_hours) as days_hours from promntc_invoice_amt_details where emp_id=? and task_id=? and promntc_invoice_id in ("+sbInvcIds.toString()+")");
				pst.setInt(1, uF.parseToInt(resourceId));
				pst.setInt(2, uF.parseToInt(taskId));
				rs = pst.executeQuery();
//				System.out.println("pst ====>> " + pst);
				while(rs.next()) {
					totDayOrHrs = rs.getString("days_hours");
				}
//				System.out.println("totDayOrHrs ====>> " + totDayOrHrs);
				rs.close();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
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
		return totDayOrHrs;
	}

	
	
	private String getTaskTotalDaysOrHours(Connection con, String taskId, String resourceId, Map<String, String> hmProjectDetails) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		String totDayOrHrs = null;
		try {
			
//			pst=con.prepareStatement("select sum(billable_hrs) as billable_hrs, count(distinct task_date) as task_date from task_activity er where activity_id = ? and emp_id = ? and is_billable = true and is_billable_approved = 1");
//			pst.setInt(1, uF.parseToInt(taskid));
//			pst.setInt(2, uF.parseToInt(resourceId));
//			rs=pst.executeQuery(); 
//			while(rs.next()) {
//				if(billingType != null && billingType.equals("D")) {
//					totDayOrHrs = rs.getString("task_date");
//				} else if(billingType != null && billingType.equals("H")) {
//					totDayOrHrs = rs.getString("billable_hrs");
//				}
//			}
			double totBillableHrs = 0;
			double totBillableDays = 0;
			
				pst = con.prepareStatement("select sum(billable_hrs) as billable_hrs, count(distinct task_date) as task_date from task_activity where activity_id=? and emp_id = ? and task_date between ? and ? and is_approved = 2 and is_billable = true and is_billable_approved = 2");				
				pst.setInt(1, uF.parseToInt(taskId));
				pst.setInt(2, uF.parseToInt(resourceId));
				pst.setDate(3, uF.getDateFormat(hmProjectDetails.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(hmProjectDetails.get("PRO_END_DATE"), DATE_FORMAT));
				rs = pst.executeQuery();			 
				System.out.println("pst ===> " + pst);
				while(rs.next()) {
					totBillableHrs += rs.getDouble("billable_hrs");
				}
				rs.close();
				pst.close();
				System.out.println("totBillableHrs==>"+totBillableHrs);
				pst = con.prepareStatement("select count(distinct task_date) as days from task_activity where activity_id = ? and emp_id = ? and task_date between ? and ? and is_approved = 2 and is_billable = true and is_billable_approved = 2");
				pst.setInt(1, uF.parseToInt(taskId));
				pst.setInt(2, uF.parseToInt(resourceId));
				pst.setDate(3, uF.getDateFormat(hmProjectDetails.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(hmProjectDetails.get("PRO_END_DATE"), DATE_FORMAT));
				rs = pst.executeQuery();			 
//				System.out.println("pst ===> " + pst);
				while(rs.next()) {
//					totBillableDays++;
					totBillableDays += rs.getInt("days");
				}
				rs.close();
				pst.close();
				System.out.println("totBillableDays==>"+totBillableDays);
				
				System.out.println("project billing type==>"+hmProjectDetails.get("PRO_BILL_DAYS_TYPE"));
				System.out.println("PRO_ACTUAL_BILLING_TYPE==>"+hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE"));
				System.out.println("PRO_HOURS_FOR_BILL_DAY==>"+uF.parseToDouble(hmProjectDetails.get("PRO_HOURS_FOR_BILL_DAY")));
				
			if(hmProjectDetails.get("PRO_BILL_DAYS_TYPE") != null && hmProjectDetails.get("PRO_BILL_DAYS_TYPE").equals("2") && uF.parseToDouble(hmProjectDetails.get("PRO_HOURS_FOR_BILL_DAY")) > 0) {
				totBillableDays = totBillableHrs / uF.parseToDouble(hmProjectDetails.get("PRO_HOURS_FOR_BILL_DAY"));
				System.out.println("totBillableDays==>"+totBillableDays);
			}

			if(hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE") != null && hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE").equals("D")) {
				totDayOrHrs = totBillableDays+"";
				System.out.println("totDayOrHrs==>"+totDayOrHrs);
			} else if(hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE") != null && hmProjectDetails.get("PRO_ACTUAL_BILLING_TYPE").equals("M")) {
				
				System.out.println("in monthly");
				Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
				Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
				Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
				Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con,hmProjectDetails.get("PRO_START_DATE"), hmProjectDetails.get("PRO_END_DATE"), CF, uF, hmWeekEndHalfDates, null);
				
				List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
				Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
				CF.getEmpRosterWeekOffTypeByDate(con,uF,CF,hmProjectDetails.get("PRO_START_DATE"), hmProjectDetails.get("PRO_END_DATE"), alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEnds,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);

				String strWLocationId = CF.getEmpWlocationId(con, uF, resourceId);

				Set<String> weeklyOffEndDate = hmWeekEnds.get(strWLocationId);
				if(weeklyOffEndDate==null) weeklyOffEndDate = new HashSet<String>();
				
				Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(resourceId);
				if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
				
				Map<String, String> hmHolidaysCnt = new HashMap<String, String>();
				Map<String, String> hmHolidayDates = new HashMap<String, String>();
//				List<String> empLeaveCountList = new ArrayList<String>();
//				List<String> holidayDateList = new ArrayList<String>();
				
				if(alEmpCheckRosterWeektype!=null && alEmpCheckRosterWeektype.contains(resourceId)) {
					CF.getHolidayListCount(con,request, hmProjectDetails.get("PRO_START_DATE"),hmProjectDetails.get("PRO_END_DATE"), CF, hmHolidayDates, hmHolidaysCnt, rosterWeeklyOffSet, true);
//					empLeaveCountList = CF.getEmpLeaveCount(con, hmProjectDetails.get("PRO_START_DATE"), hmProjectDetails.get("PRO_END_DATE"), CF, hmHolidayDates, rosterWeeklyOffSet, resourceId, strWLocationId);
//					holidayDateList = CF.getHolidayDateList(con, hmProjectDetails.get("PRO_START_DATE"), hmProjectDetails.get("PRO_END_DATE"), CF, rosterWeeklyOffSet, strWLocationId);
				} else {
					CF.getHolidayListCount(con,request,hmProjectDetails.get("PRO_START_DATE"),hmProjectDetails.get("PRO_END_DATE"), CF, hmHolidayDates, hmHolidaysCnt, weeklyOffEndDate, true);
//					empLeaveCountList = CF.getEmpLeaveCount(con, hmProjectDetails.get("PRO_START_DATE"), hmProjectDetails.get("PRO_END_DATE"), CF, hmHolidayDates, weeklyOffEndDate, resourceId, strWLocationId);
//					holidayDateList = CF.getHolidayDateList(con, hmProjectDetails.get("PRO_START_DATE"), hmProjectDetails.get("PRO_END_DATE"), CF, weeklyOffEndDate, strWLocationId);
				}
				
				String diffInDays = uF.dateDifference(hmProjectDetails.get("PRO_START_DATE"), DATE_FORMAT, hmProjectDetails.get("PRO_END_DATE"), DATE_FORMAT, CF.getStrTimeZone());
				
				int nWeekEnd = (alEmpCheckRosterWeektype!=null && alEmpCheckRosterWeektype.contains(resourceId)) ? rosterWeeklyOffSet.size() : weeklyOffEndDate.size();
				int nHolidayCnt = uF.parseToInt(hmHolidaysCnt.get(strWLocationId));
				
//				int nLeaveCnt = empLeaveCountList.size();
				double nWorkDays = (uF.parseToDouble(diffInDays) - nWeekEnd) - nHolidayCnt; // - nLeaveCnt
				
				System.out.println("nWorkDays==>"+nWorkDays);
				double totBillableMonths = 0;
				if(nWorkDays > 0) {
					totBillableMonths = totBillableDays / nWorkDays;
				}
				totDayOrHrs = totBillableMonths+"";
				
			} else {
				totDayOrHrs = totBillableHrs+"";
			}
			System.out.println("TOTALDaysOrHours==>"+totDayOrHrs);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
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
		return totDayOrHrs;
	}
	
	
	private void loadProjectInvoice(UtilityFunctions uF) {
		Map<String,String> hmClientDetails = (Map<String,String>)request.getAttribute("hmClientDetails");
		
		clientPocList = new FillClientPoc(request).fillClientPoc(hmClientDetails.get("CLIENT_ID"));
		clientAddressList = new FillClientAddress(request).fillClientAddress(hmClientDetails.get("CLIENT_ID"), uF);
		currencyList = new FillCurrency(request).fillCurrency();
		projectOwnerList = new FillEmployee(request).fillEmployeeNameByParentLevel(0);
		//bankList = new FillBank().fillBankDetails();
		bankList = new FillBank(request).fillBankAccNo();
	}

	
	public List<FillClientPoc> getClientPocList() {
		return clientPocList;
	}

	public void setClientPocList(List<FillClientPoc> clientPocList) {
		this.clientPocList = clientPocList;
	}

	public String getClientPoc() {
		return clientPoc;
	}

	public void setClientPoc(String clientPoc) {
		this.clientPoc = clientPoc;
	}

	public List<FillClientAddress> getClientAddressList() {
		return clientAddressList;
	}

	public void setClientAddressList(List<FillClientAddress> clientAddressList) {
		this.clientAddressList = clientAddressList;
	}

	public List<FillCurrency> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(List<FillCurrency> currencyList) {
		this.currencyList = currencyList;
	}

	public String getStrCurrency() {
		return strCurrency;
	}

	public void setStrCurrency(String strCurrency) {
		this.strCurrency = strCurrency;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getPro_id() {
		return pro_id;
	}

	public void setPro_id(String pro_id) {
		this.pro_id = pro_id;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
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

	public List<FillEmployee> getProjectOwnerList() {
		return projectOwnerList;
	}

	public void setProjectOwnerList(List<FillEmployee> projectOwnerList) {
		this.projectOwnerList = projectOwnerList;
	}

	public String getStrProjectOwner() {
		return strProjectOwner;
	}

	public void setStrProjectOwner(String strProjectOwner) {
		this.strProjectOwner = strProjectOwner;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public String[] getStrParticulars() {
		return strParticulars;
	}

	public void setStrParticulars(String[] strParticulars) {
		this.strParticulars = strParticulars;
	}

	public String[] getStrParticularsAmt() {
		return strParticularsAmt;
	}

	public void setStrParticularsAmt(String[] strParticularsAmt) {
		this.strParticularsAmt = strParticularsAmt;
	}

	public String getProDescription() {
		return proDescription;
	}

	public void setProDescription(String proDescription) {
		this.proDescription = proDescription;
	}

	public String getOtherDescription() {
		return otherDescription;
	}

	public void setOtherDescription(String otherDescription) {
		this.otherDescription = otherDescription;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public String getParticularTotalAmt() {
		return particularTotalAmt;
	}

	public void setParticularTotalAmt(String particularTotalAmt) {
		this.particularTotalAmt = particularTotalAmt;
	}

	public String getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(String totalAmt) {
		this.totalAmt = totalAmt;
	}

	public List<FillBank> getBankList() {
		return bankList;
	}

	public void setBankList(List<FillBank> bankList) {
		this.bankList = bankList;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getPayPalMailId() {
		return payPalMailId;
	}

	public void setPayPalMailId(String payPalMailId) {
		this.payPalMailId = payPalMailId;
	}

	public String[] getStrEmp() {
		return strEmp;
	}

	public void setStrEmp(String[] strEmp) {
		this.strEmp = strEmp;
	}

	public String[] getBillDailyHours() {
		return billDailyHours;
	}

	public void setBillDailyHours(String[] billDailyHours) {
		this.billDailyHours = billDailyHours;
	}

	public String[] getEmpRate() {
		return empRate;
	}

	public void setEmpRate(String[] empRate) {
		this.empRate = empRate;
	}

	public String[] getStrEmpAmt() {
		return strEmpAmt;
	}

	public void setStrEmpAmt(String[] strEmpAmt) {
		this.strEmpAmt = strEmpAmt;
	}

	public String getBill_type() {
		return bill_type;
	}

	public void setBill_type(String bill_type) {
		this.bill_type = bill_type;
	}

	public String getStrStartDateOtherCurr() {
		return strStartDateOtherCurr;
	}

	public void setStrStartDateOtherCurr(String strStartDateOtherCurr) {
		this.strStartDateOtherCurr = strStartDateOtherCurr;
	}

	public String getStrEndDateOtherCurr() {
		return strEndDateOtherCurr;
	}

	public void setStrEndDateOtherCurr(String strEndDateOtherCurr) {
		this.strEndDateOtherCurr = strEndDateOtherCurr;
	}

	public String[] getStrEmpOtherCurr() {
		return strEmpOtherCurr;
	}

	public void setStrEmpOtherCurr(String[] strEmpOtherCurr) {
		this.strEmpOtherCurr = strEmpOtherCurr;
	}

	public String[] getBillDailyHoursINRCurr() {
		return billDailyHoursINRCurr;
	}

	public void setBillDailyHoursINRCurr(String[] billDailyHoursINRCurr) {
		this.billDailyHoursINRCurr = billDailyHoursINRCurr;
	}

	public String[] getEmpRateINRCurr() {
		return empRateINRCurr;
	}

	public void setEmpRateINRCurr(String[] empRateINRCurr) {
		this.empRateINRCurr = empRateINRCurr;
	}

	public String[] getStrEmpAmtINRCurr() {
		return strEmpAmtINRCurr;
	}

	public void setStrEmpAmtINRCurr(String[] strEmpAmtINRCurr) {
		this.strEmpAmtINRCurr = strEmpAmtINRCurr;
	}

	public String getStrReferenceNo() {
		return strReferenceNo;
	}

	public void setStrReferenceNo(String strReferenceNo) {
		this.strReferenceNo = strReferenceNo;
	}

	public String getStrReferenceNoOtherCurr() {
		return strReferenceNoOtherCurr;
	}

	public void setStrReferenceNoOtherCurr(String strReferenceNoOtherCurr) {
		this.strReferenceNoOtherCurr = strReferenceNoOtherCurr;
	}

	public String getProCurrency() {
		return proCurrency;
	}

	public void setProCurrency(String proCurrency) {
		this.proCurrency = proCurrency;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getParticularTotalAmtOtherCurr() {
		return particularTotalAmtOtherCurr;
	}

	public void setParticularTotalAmtOtherCurr(String particularTotalAmtOtherCurr) {
		this.particularTotalAmtOtherCurr = particularTotalAmtOtherCurr;
	}

	public String getTotalAmtOtherCurr() {
		return totalAmtOtherCurr;
	}

	public void setTotalAmtOtherCurr(String totalAmtOtherCurr) {
		this.totalAmtOtherCurr = totalAmtOtherCurr;
	}

	public String[] getStrEmpAmtOtherCurr() {
		return strEmpAmtOtherCurr;
	}

	public void setStrEmpAmtOtherCurr(String[] strEmpAmtOtherCurr) {
		this.strEmpAmtOtherCurr = strEmpAmtOtherCurr;
	}

	public String[] getHideTaskId() {
		return hideTaskId;
	}

	public void setHideTaskId(String[] hideTaskId) {
		this.hideTaskId = hideTaskId;
	}

	public String[] getHideTaskIdOtherCurr() {
		return hideTaskIdOtherCurr;
	}

	public void setHideTaskIdOtherCurr(String[] hideTaskIdOtherCurr) {
		this.hideTaskIdOtherCurr = hideTaskIdOtherCurr;
	}

	public String getInvoice_format_id() {
		return invoice_format_id;
	}

	public void setInvoice_format_id(String invoice_format_id) {
		this.invoice_format_id = invoice_format_id;
	}

	public String getProDescriptionOtherCurr() {
		return proDescriptionOtherCurr;
	}

	public void setProDescriptionOtherCurr(String proDescriptionOtherCurr) {
		this.proDescriptionOtherCurr = proDescriptionOtherCurr;
	}

	public String[] getTaxHead() {
		return taxHead;
	}

	public void setTaxHead(String[] taxHead) {
		this.taxHead = taxHead;
	}

	public String[] getTaxHeadPercent() {
		return taxHeadPercent;
	}

	public void setTaxHeadPercent(String[] taxHeadPercent) {
		this.taxHeadPercent = taxHeadPercent;
	}

	public String[] getTaxHeadAmt() {
		return taxHeadAmt;
	}

	public void setTaxHeadAmt(String[] taxHeadAmt) {
		this.taxHeadAmt = taxHeadAmt;
	}

	public String[] getTaxHeadOtherCurr() {
		return taxHeadOtherCurr;
	}

	public void setTaxHeadOtherCurr(String[] taxHeadOtherCurr) {
		this.taxHeadOtherCurr = taxHeadOtherCurr;
	}

	public String[] getTaxHeadPercentOtherCurr() {
		return taxHeadPercentOtherCurr;
	}

	public void setTaxHeadPercentOtherCurr(String[] taxHeadPercentOtherCurr) {
		this.taxHeadPercentOtherCurr = taxHeadPercentOtherCurr;
	}

	public String[] getTaxHeadAmtINRCurr() {
		return taxHeadAmtINRCurr;
	}

	public void setTaxHeadAmtINRCurr(String[] taxHeadAmtINRCurr) {
		this.taxHeadAmtINRCurr = taxHeadAmtINRCurr;
	}

	public String[] getTaxHeadAmtOtherCurr() {
		return taxHeadAmtOtherCurr;
	}

	public void setTaxHeadAmtOtherCurr(String[] taxHeadAmtOtherCurr) {
		this.taxHeadAmtOtherCurr = taxHeadAmtOtherCurr;
	}

	public String getParticularTotalAmtINRCurr() {
		return particularTotalAmtINRCurr;
	}

	public void setParticularTotalAmtINRCurr(String particularTotalAmtINRCurr) {
		this.particularTotalAmtINRCurr = particularTotalAmtINRCurr;
	}

	public String getTotalAmtINRCurr() {
		return totalAmtINRCurr;
	}

	public void setTotalAmtINRCurr(String totalAmtINRCurr) {
		this.totalAmtINRCurr = totalAmtINRCurr;
	}

	public String[] getStrOtherParticulars() {
		return strOtherParticulars;
	}

	public void setStrOtherParticulars(String[] strOtherParticulars) {
		this.strOtherParticulars = strOtherParticulars;
	}

	public String[] getStrOtherParticularsAmt() {
		return strOtherParticularsAmt;
	}

	public void setStrOtherParticularsAmt(String[] strOtherParticularsAmt) {
		this.strOtherParticularsAmt = strOtherParticularsAmt;
	}

	public String[] getStrOtherParticularsINRCurr() {
		return strOtherParticularsINRCurr;
	}

	public void setStrOtherParticularsINRCurr(String[] strOtherParticularsINRCurr) {
		this.strOtherParticularsINRCurr = strOtherParticularsINRCurr;
	}

	public String[] getStrOtherParticularsAmtINRCurr() {
		return strOtherParticularsAmtINRCurr;
	}

	public void setStrOtherParticularsAmtINRCurr(String[] strOtherParticularsAmtINRCurr) {
		this.strOtherParticularsAmtINRCurr = strOtherParticularsAmtINRCurr;
	}

	public String[] getStrOtherParticularsAmtOtherCurr() {
		return strOtherParticularsAmtOtherCurr;
	}

	public void setStrOtherParticularsAmtOtherCurr(String[] strOtherParticularsAmtOtherCurr) {
		this.strOtherParticularsAmtOtherCurr = strOtherParticularsAmtOtherCurr;
	}

	public String getOtherDescriptionOtherCurr() {
		return otherDescriptionOtherCurr;
	}

	public void setOtherDescriptionOtherCurr(String otherDescriptionOtherCurr) {
		this.otherDescriptionOtherCurr = otherDescriptionOtherCurr;
	}

	public String getPro_freq_id() {
		return pro_freq_id;
	}

	public void setPro_freq_id(String pro_freq_id) {
		this.pro_freq_id = pro_freq_id;
	}

	public String getProOPEAmt() {
		return proOPEAmt;
	}

	public void setProOPEAmt(String proOPEAmt) {
		this.proOPEAmt = proOPEAmt;
	}

	public String[] getTaxNameLabel() {
		return taxNameLabel;
	}

	public void setTaxNameLabel(String[] taxNameLabel) {
		this.taxNameLabel = taxNameLabel;
	}

	public String[] getTaxNameLabelOtherCurr() {
		return taxNameLabelOtherCurr;
	}

	public void setTaxNameLabelOtherCurr(String[] taxNameLabelOtherCurr) {
		this.taxNameLabelOtherCurr = taxNameLabelOtherCurr;
	}

	public String getInvoiceGenDate() {
		return invoiceGenDate;
	}

	public void setInvoiceGenDate(String invoiceGenDate) {
		this.invoiceGenDate = invoiceGenDate;
	}

	public String getInvoiceCode() {
		return invoiceCode;
	}

	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}
		
}
