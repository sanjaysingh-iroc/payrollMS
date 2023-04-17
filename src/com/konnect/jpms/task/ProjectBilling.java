package com.konnect.jpms.task;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectBilling extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	HttpSession session;
	String strUserType;
	String strProductType =  null;
	String strSessionEmpId;
	
	String alertStatus;
	String alert_type;
	String alertID;
	
	String invoiceId;
	String proId;
	String operation;
	String proType; 
	String proFreqId;
	
	List<FillBillingType> billingFreqList;
	List<FillBillingType> billingTypeList;
	
	String billingType;
	String strBillingFreq;
	
	String proPage;
	String minLimit;
	
	String btnSubmit;
	
	public String execute() throws Exception {
		session = request.getSession();		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strProductType = (String)session.getAttribute(PRODUCT_TYPE);
		
		request.setAttribute(PAGE, "/jsp/task/ProjectBilling.jsp");
		request.setAttribute(TITLE, "Billing");
		UtilityFunctions uF = new UtilityFunctions();
		
		if(uF.parseToInt(strProductType) != 3) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		} 
		
		/*if(strUserType != null && strUserType.equals(ACCOUNTANT) && getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(PRO_RECURRING_BILLING_ALERT)) {
		updateUserAlerts();
	}*/
//		if(getAlertStatus()!=null && getAlert_type()!=null){
		if(uF.parseToInt(getAlertID()) > 0) {
			updateUserAlerts();
		}
		
//		if(getReportType() == null) {
//			setReportType("1");
//		}
		//System.out.println("getF_strWLocation ===>> " +getF_strWLocation());
		removeTemporaryFile(uF);
		
		if(getOperation() != null && getOperation().equals("D")) {
			deleteProjectFromBilling(uF);
		}
		
//		if(getF_strWLocation() == null) {
//			setF_strWLocation((String)session.getAttribute(WLOCATIONID));
//		}
		
//		System.out.println("getF_org() ===>> " + getF_org());
		
		if(getF_org() == null) {
			setF_org((String)session.getAttribute(ORGID));
		}
		if(getStrClient() != null && !getStrClient().equals("")) {
			setClient(getStrClient().split(","));
		} else {
			setClient(null);
		}
		
//		if((getProType() == null || getProType().equals("P")) && getReportType() == null) {
//			setReportType("1");
//		} else if(getProType() != null && getProType().equals("C") && getReportType() == null) {
//			setReportType("4");
//		}
		if(getProType() == null || getProType().trim().equals("") || getProType().trim().equalsIgnoreCase("null")){
			setProType("PPB");
		} 
		
		if(uF.parseToInt(getProPage()) == 0) {
			setProPage("1");
		}
		
		if((getProType() != null && (getProType().equals("PPB") || getProType().equals("PAB"))) && (getReportType() == null || getReportType().equals(""))) {
			setReportType("1");
		} else if(getProType() != null && (getProType().equals("CPB") || getProType().equals("CAB")) && (getReportType() == null || getReportType().equals(""))) {
			setReportType("4");
		}
		if(getProType() != null && (getProType().equals("PPB") || getProType().equals("CPB"))){
			getProjectBillingDetails(uF);
		} else if(getProType() != null && (getProType().equals("PAB") || getProType().equals("CAB"))){
			getAdHocBillingDetails(uF);
		}
//		System.out.println("getReportType() ===>> " + getReportType());
		
		billingTypeList = new FillBillingType().fillBillingTypeList();
		billingFreqList = new FillBillingType().fillBillingKindList();
		
		request.setAttribute(MESSAGE, (String)session.getAttribute(MESSAGE)); 
		
		if(getBtnSubmit() != null) {
			return SUCCESS;
		} else {
			return LOAD;
		} 
	}

	
	private void getAdHocBillingDetails(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		
		try {
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
				wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
				organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
				
			} else {
				organisationList = new FillOrganisation(request).fillOrganisation();
				wLocationList = new FillWLocation(request).fillWLocation(getF_org());
				
			}
			departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
			clientList = new FillClients(request).fillClients(false);
			
//			if(getF_strWLocation()==null) {
//				setF_strWLocation((String)session.getAttribute(WLOCATIONID));
//			}
			
			getSelectedFilter(uF);
			
			con = db.makeConnection(con);
			
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map<String, String> hmProReceivedAmount = new HashMap<String, String>();
			Map<String, String> hmInvoiceReceivedAmount = new HashMap<String, String>();

			pst = con.prepareStatement("select received_amount,tds_deducted, write_off_amount, invoice_id, is_write_off, exchange_rate,pro_freq_id from promntc_bill_amt_details where invoice_id in(select promntc_invoice_id from promntc_invoice_details where is_cancel=false and invoice_cancel=false and (invoice_type!=? or invoice_type!=?)) order by invoice_id, is_write_off desc");
			pst.setInt(1, ADHOC_INVOICE);
			pst.setInt(2, ADHOC_PRORETA_INVOICE);
			rs = pst.executeQuery();
			while(rs.next()) {
				double dblReceivedAmt = uF.parseToDouble(rs.getString("received_amount"));
				double dblTDSAmt = uF.parseToDouble(rs.getString("tds_deducted"));
				double dblWriteOffAmt = uF.parseToDouble(rs.getString("write_off_amount"));
//				double dblExchageRate = uF.parseToDouble(rs.getString("exchange_rate"));
				
//				double dblTotalAmountP = (dblReceivedAmt/dblExchageRate) + (dblTDSAmt/dblExchageRate) ;
				double dblTotalAmountP = dblReceivedAmt + dblTDSAmt + dblWriteOffAmt;
				dblTotalAmountP += uF.parseToDouble(hmProReceivedAmount.get(rs.getString("pro_freq_id")));
				hmProReceivedAmount.put(rs.getString("pro_freq_id"), uF.formatIntoTwoDecimal(dblTotalAmountP));
				
//				double dblTotalAmount = (dblReceivedAmt/dblExchageRate) + (dblTDSAmt/dblExchageRate) ;
				double dblTotalAmount = dblReceivedAmt + dblTDSAmt + dblWriteOffAmt;
				dblTotalAmount += uF.parseToDouble(hmInvoiceReceivedAmount.get(rs.getString("invoice_id")));
				hmInvoiceReceivedAmount.put(rs.getString("invoice_id"), uF.formatIntoTwoDecimal(dblTotalAmount));
//				if(uF.parseToBoolean(rs.getString("is_write_off"))) {
//					alWriteOff.add(rs.getString("invoice_id"));
//				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmInvoiceReceivedAmount", hmInvoiceReceivedAmount);
			request.setAttribute("hmProReceivedAmount", hmProReceivedAmount);

			Map<String, String> hmCurr1 = new HashMap<String, String>();
			Map hmEmpWlocation = CF.getEmpWlocationMap(con);
			Map hmWorkLocation = CF.getWorkLocationMap(con);
			
			String strWLocation = (String)hmEmpWlocation.get((String)session.getAttribute(EMPID));
			Map hmWlocation = (Map) hmWorkLocation.get(strWLocation);
			if(hmWlocation == null) hmWlocation = new HashMap();		  
			  
			request.setAttribute("hmWlocation", hmWlocation);
			
			Map<String, String> hmPaidAdhocAmount = new HashMap<String, String>();
			Map<String, String> hmProAdhocInvoiceAmount = new HashMap<String, String>();
			Map<String, String> hmAdhocInvoiceAmount = new HashMap<String, String>();
			
			pst = con.prepareStatement("select sum(oc_invoice_amount) as invoice_amount, promntc_invoice_id from promntc_invoice_details where is_cancel=false and (invoice_type=? or invoice_type=?) group by promntc_invoice_id"); //and invoice_cancel=false 
			pst.setInt(1, ADHOC_INVOICE);
			pst.setInt(2, ADHOC_PRORETA_INVOICE);
			rs = pst.executeQuery();
			while(rs.next()) {
				hmProAdhocInvoiceAmount.put(rs.getString("promntc_invoice_id"), rs.getString("invoice_amount"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select received_amount,tds_deducted, write_off_amount, invoice_id, is_write_off, exchange_rate,pro_id from promntc_bill_amt_details where invoice_id in(select promntc_invoice_id from promntc_invoice_details where is_cancel=false and (invoice_type=? or invoice_type=?)) order by invoice_id, is_write_off desc"); //and invoice_cancel=false 
			pst.setInt(1, ADHOC_INVOICE);
			pst.setInt(2, ADHOC_PRORETA_INVOICE);
			rs = pst.executeQuery();
			while(rs.next()) {
				double dblReceivedAmt = uF.parseToDouble(rs.getString("received_amount"));
				double dblTDSAmt = uF.parseToDouble(rs.getString("tds_deducted"));
				double dblWriteOffAmt = uF.parseToDouble(rs.getString("write_off_amount"));
//				double dblExchageRate = uF.parseToDouble(rs.getString("exchange_rate"));
				
//				double dblTotalAmountP = (dblReceivedAmt/dblExchageRate) + (dblTDSAmt/dblExchageRate);
				double dblTotalAmountP = dblReceivedAmt + dblTDSAmt + dblWriteOffAmt;
				dblTotalAmountP += uF.parseToDouble(hmPaidAdhocAmount.get(rs.getString("invoice_id")));
				hmPaidAdhocAmount.put(rs.getString("invoice_id"), uF.formatIntoTwoDecimal(dblTotalAmountP));
				
//				double dblTotalAmount = (dblReceivedAmt/dblExchageRate) + (dblTDSAmt/dblExchageRate);
				double dblTotalAmount = dblReceivedAmt + dblTDSAmt + dblWriteOffAmt;
				dblTotalAmount += uF.parseToDouble(hmAdhocInvoiceAmount.get(rs.getString("invoice_id")));
				hmAdhocInvoiceAmount.put(rs.getString("invoice_id"), uF.formatIntoTwoDecimal(dblTotalAmount));
//				if(uF.parseToBoolean(rs.getString("is_write_off"))) {
//					alWriteOff.add(rs.getString("invoice_id"));
//				}
			}
			rs.close();
			pst.close();
			
			
			Map<String, String> hmOtherProectName = new HashMap<String, String>();
			pst = con.prepareStatement("select pro_id, pro_name from projectmntnc");
			rs = pst.executeQuery();
			while(rs.next()) {
				hmOtherProectName.put(rs.getString("pro_id"), rs.getString("pro_name"));
			}
			rs.close();
			pst.close();
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from promntc_invoice_details where is_cancel=false and (invoice_type=? or invoice_type=?) ");
//			System.out.println("getClient ===>> " + getClient() != null ? getClient().length : "-");
			if(getClient() != null && getClient().length>0 && getClient()[0].trim().length()>0) {
				sbQuery.append(" and client_id in ("+StringUtils.join(getClient(), ",")+") ");
			}
			if(strUserType != null && strUserType.equals(CUSTOMER)) {
				sbQuery.append(" and spoc_id= "+uF.parseToInt(strSessionEmpId)+" ");
			}
			sbQuery.append(" order by invoice_generated_date desc, promntc_invoice_id desc");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, ADHOC_INVOICE);
			pst.setInt(2, ADHOC_PRORETA_INVOICE);
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();			
			List alAdhocReport = new ArrayList();			   
			while(rs.next()) {
				Map hmAdhocInner = new HashMap();
				List alAdhocInner = new ArrayList();
				double dblInvoiceAmt = uF.parseToDouble(hmProAdhocInvoiceAmount.get(rs.getString("promntc_invoice_id")));
				double dblPaidAmt = uF.parseToDouble(hmPaidAdhocAmount.get(rs.getString("promntc_invoice_id")));
				double dblBalanceAmt = dblInvoiceAmt - dblPaidAmt;
				if(getReportType() != null && getReportType().equals("1") && dblInvoiceAmt > 0 && dblBalanceAmt <= 0) {
					continue;
				} else if(getReportType() != null && getReportType().equals("4") && (dblInvoiceAmt == 0 || dblBalanceAmt > 0)) {
					continue;
				}
				
				hmAdhocInner.put("INVOICE_ID", rs.getString("promntc_invoice_id"));
				hmAdhocInner.put("INVOICE_GENERATED_DATE", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
				hmAdhocInner.put("INVOICE_GENERATED_BY", rs.getString("invoice_generated_by"));
				hmAdhocInner.put("INVOICE_FROM_DATE", uF.getDateFormat(rs.getString("invoice_from_date"), DBDATE, CF.getStrReportDateFormat()));
				hmAdhocInner.put("INVOICE_TO_DATE", uF.getDateFormat(rs.getString("invoice_to_date"), DBDATE, CF.getStrReportDateFormat()));
				hmAdhocInner.put("INVOICE_CODE", rs.getString("invoice_code"));
				hmAdhocInner.put("PROJECT_ID", rs.getString("pro_id"));
				hmAdhocInner.put("PRO_FREQ_ID", uF.showData(rs.getString("pro_freq_id"), "0"));
				hmAdhocInner.put("PROJECT_DESCRIPTION", rs.getString("project_description"));
				hmAdhocInner.put("OTHER_DESCRIPTION", rs.getString("other_description"));
				hmAdhocInner.put("SPOC_ID", rs.getString("spoc_id"));
				hmAdhocInner.put("ADDRESS_ID", rs.getString("address_id"));
				hmAdhocInner.put("PRO_OWNER_ID", rs.getString("pro_owner_id"));
				hmAdhocInner.put("FINANCIAL_START_DATE", uF.getDateFormat(rs.getString("financial_start_date"), DBDATE, CF.getStrReportDateFormat()));
				hmAdhocInner.put("FINANCIAL_END_DATE", uF.getDateFormat(rs.getString("financial_end_date"), DBDATE, CF.getStrReportDateFormat()));
				hmAdhocInner.put("WLOCATION_ID", rs.getString("wlocation_id"));
				hmAdhocInner.put("DEPART_ID", rs.getString("depart_id"));
				hmAdhocInner.put("PARTICULARS_TOTAL_AMOUNT", rs.getString("oc_particulars_total_amount"));
				hmAdhocInner.put("INVOICE_AMOUNT", rs.getString("oc_invoice_amount"));
				hmAdhocInner.put("CURR_ID", rs.getString("curr_id"));
				hmAdhocInner.put("IS_CANCEL", rs.getString("invoice_cancel"));
				hmAdhocInner.put("BANK_BRANCH_ID", rs.getString("bank_branch_id"));
				hmAdhocInner.put("INVOICE_TYPE", rs.getString("invoice_type"));
				hmAdhocInner.put("CLIENT_ID", rs.getString("client_id"));
				hmAdhocInner.put("SERVICE_ID", rs.getString("service_id"));
				hmAdhocInner.put("CLIENT_NAME", uF.showData(CF.getClientNameById(con, rs.getString("client_id")), "N/A")+"<br/><i>["+uF.showData(hmOtherProectName.get(rs.getString("pro_id")), "Not Aligned")+"]</i>");
				hmAdhocInner.put("CLIENT_ID", rs.getString("client_id"));
				
				hmCurr1 = hmCurrencyDetails.get(rs.getString("curr_id"));
				
				if(hmCurr1 == null) hmCurr1 = new HashMap();
				
				if(dblBalanceAmt < 0) {
					dblBalanceAmt = 0;
				}
				double dblFinalInvoiceAmt = dblInvoiceAmt;
				double dblFinalPaidAmt = dblPaidAmt;
				double dblFinalBalanceAmt = dblBalanceAmt;
				
				alAdhocInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblFinalInvoiceAmt));
				alAdhocInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblFinalPaidAmt));
				alAdhocInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblFinalBalanceAmt));
				
				hmAdhocInner.put("BILLING_SUMMARY", alAdhocInner);
				
				alAdhocReport.add(hmAdhocInner);
				
			}
			rs.close();
			pst.close();
			
			int proCnt = alAdhocReport.size();
			request.setAttribute("proCnt", proCnt+"");
			
			if(alAdhocReport.size() > 0){
				
				int proCount = alAdhocReport.size()/10;
				if(alAdhocReport.size()%10 != 0) {
					proCount++;
				}
//				System.out.println("ProCount=====>"+proCount);
				request.setAttribute("proCount", proCount+"");
				
//				System.out.println("=======pro wise proCount========"+proCount);
//				System.out.println("=======proCnt========"+proCnt);
//				System.out.println("=======uF.parseToInt(getMinLimit())========"+uF.parseToInt(getMinLimit()));
				if(alAdhocReport.size() > 10){
					int nStart = 0;
					int nEnd = 10;
					if(uF.parseToInt(getMinLimit())>0){
						nStart = uF.parseToInt(getMinLimit());
						nEnd = uF.parseToInt(getMinLimit())+10;
					}
					
//					System.out.println("=======nStart========"+nStart);
//					System.out.println("=======nEnd========"+nEnd);
					if(nEnd > alAdhocReport.size()){
						nEnd = alAdhocReport.size();
//						System.out.println("=======after nEnd========"+nEnd);
					}
					
//					System.out.println("=======alReport========"+alReport.toString());
					alAdhocReport = alAdhocReport.subList(nStart, nEnd);
				}
			}
			
			request.setAttribute("alAdhocReport", alAdhocReport);
			
			
			Map<String, String> hmCurr = new HashMap<String, String>();
			pst = con.prepareStatement("select invoice_generated_date, pro_id,invoice_code,promntc_invoice_id, oc_invoice_amount, curr_id,invoice_cancel,invoice_type," +
				"adhoc_billing_type,invoice_template_id from promntc_invoice_details where is_cancel=false and invoice_type=? or invoice_type=?");
			pst.setInt(1, ADHOC_INVOICE);
			pst.setInt(2, ADHOC_PRORETA_INVOICE);
			rs = pst.executeQuery();
//			System.out.println("pst======>" + pst);
//			Map<String, String> hmAdhocInvoice = new HashMap<String, String>();
			Map<String, List<Map<String, String>>> hmAdhocInvDetails = new HashMap<String, List<Map<String, String>>>();
			while(rs.next()) {
//				if(rs.getString("invoice_generated_date") != null) {
//					hmAdhocInvoice.put(rs.getString("invoice_code")+"_"+rs.getString("promntc_invoice_id"), uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
//				}
				double invcAmount = uF.parseToDouble(rs.getString("oc_invoice_amount"));
				double dblBalance = invcAmount - uF.parseToDouble(hmInvoiceReceivedAmount.get(rs.getString("promntc_invoice_id")));
				if(getReportType() != null && getReportType().equals("1") && invcAmount> 0 && dblBalance <= 0) {
					continue;
				} else if(getReportType() != null && getReportType().equals("4") && (invcAmount == 0 || dblBalance > 0)) {
					continue;
				}
				
				hmCurr = hmCurrencyDetails.get(rs.getString("curr_id"));
				if(hmCurr == null) hmCurr = new HashMap();
				List<Map<String, String>> outerList = hmAdhocInvDetails.get(rs.getString("promntc_invoice_id"));
				if(outerList == null) outerList = new ArrayList<Map<String, String>>();
				
				Map<String, String> hmInnerMap = new HashMap<String, String>();
				hmInnerMap.put("PROJECT_ID", rs.getString("pro_id"));
				hmInnerMap.put("INVOICE_ID", rs.getString("promntc_invoice_id"));
				hmInnerMap.put("INVOICE_CODE", rs.getString("invoice_code"));
				hmInnerMap.put("INVOICE_TYPE", rs.getString("invoice_type"));
				
				double invoiceAmount = uF.parseToDouble(rs.getString("oc_invoice_amount"));
				
				hmInnerMap.put("INVOICE_AMOUNT",uF.showData(hmCurr.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(invoiceAmount));
				hmInnerMap.put("INVOICE_AMOUNT_ONLY", rs.getString("oc_invoice_amount"));
				
//				double dblTotBalance = dblBalance;
//				if(dblBalance>0 && !alWriteOff.contains(rs.getString("promntc_invoice_id"))) {
				if(dblBalance > 0) {
					hmInnerMap.put("BALANCE_AMOUNT", uF.showData(hmCurr.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblBalance));
				}
				hmInnerMap.put("INVOICE_GENERATED_DATE", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInnerMap.put("INVOICE_IS_CANCEL", rs.getString("invoice_cancel"));
				hmInnerMap.put("ADHOC_BILLING_TYPE", rs.getString("adhoc_billing_type"));
				hmInnerMap.put("INVOICE_TEMPLATE_ID", rs.getString("invoice_template_id"));
				outerList.add(hmInnerMap); 
				hmAdhocInvDetails.put(rs.getString("promntc_invoice_id"), outerList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("pst======>"+pst);
			request.setAttribute("hmAdhocInvDetails", hmAdhocInvDetails);
//			request.setAttribute("hmAdhocInvoice", hmAdhocInvoice); 
			
			pst = con.prepareStatement(selectNotifications);
			pst.setInt(1, N_PAYMENT_ALERT); 
			rs = pst.executeQuery();
			boolean isEmail = false;
			while(rs.next()) {
				isEmail = uF.parseToBoolean(rs.getString("isemail"));
			}
			rs.close();
			pst.close();
			request.setAttribute("isEmail", isEmail);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void deleteProjectFromBilling(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update projectmntnc_frequency set is_delete = true where pro_freq_id = ?");
			pst.setInt(1, uF.parseToInt(getProFreqId()));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void removeTemporaryFile(UtilityFunctions uF) {
		try{
			String directory = CF.getStrDocSaveLocation()+I_TEMP+"/";
			File theDir = new File(directory);
			if(theDir.exists()){
				FileUtils.cleanDirectory(theDir);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void updateUserAlerts() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			String strDomain = request.getServerName().split("\\.")[0];
			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
			userAlerts.setStrDomain(strDomain);
			userAlerts.setAlertID(getAlertID()); 
			if(strUserType!=null && strUserType.equals(CUSTOMER)) {
				userAlerts.setStrOther("other");
			}
			userAlerts.setStatus(DELETE_TR_ALERT);
			Thread t = new Thread(userAlerts);
			t.run();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
	

	/*private void updateUserAlerts() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int nEmpId = uF.parseToInt(strSessionEmpId);
		try {
			con = db.makeConnection(con);
			
			String strType = null;
			if(getAlert_type().equals(PRO_RECURRING_BILLING_ALERT)){
				strType = PRO_RECURRING_BILLING_ALERT;
			} else if(getAlert_type().equals(INVOICE_GENERATED_ALERT)){
				strType = INVOICE_GENERATED_ALERT;
			}
			
			if(strType!=null && !strType.trim().equals("")){
				String strDomain = request.getServerName().split("\\.")[0];
				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(""+nEmpId);
				if(strUserType!=null && strUserType.equals(CUSTOMER)){
					userAlerts.setStrOther("other");
				}
				userAlerts.set_type(strType);
				userAlerts.setStatus(UPDATE_ALERT);
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
	}*/
	
	
	String paycycle;
	List<FillPayCycles> payCycleList;
	List<FillProjectList> projectList;
	List<FillWLocation> wLocationList;
	List<FillOrganisation> organisationList;
	List<FillDepartment> departmentList;
	List<FillClients> clientList;
	
	String strProject;
	String f_org;
	String f_strWLocation;
	String f_department;
	String strClient;
	String[] client;
	
	String f_start;
	String f_end;
	String reportType;
	
	public void getProjectBillingDetails(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		
		try {
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
				wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
				organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			} else {
				organisationList = new FillOrganisation(request).fillOrganisation();
				wLocationList = new FillWLocation(request).fillWLocation(getF_org());
				
			}
			departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
			clientList = new FillClients(request).fillClients(false);
			
//			if(getF_strWLocation()==null) {
//				setF_strWLocation((String)session.getAttribute(WLOCATIONID));
//			}
			
			getSelectedFilter(uF);
			
			con = db.makeConnection(con);
			
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			
			Map<String, String> hmProReceivedAmount = new HashMap<String, String>();
			Map<String, String> hmProInvoiceAmount = new HashMap<String, String>();
			Map<String, String> hmProOPEAmount = new HashMap<String, String>();
			Map<String, String> hmInvoiceReceivedAmount = new HashMap<String, String>();
			Map<String, String> hmProMilestoneAmount = new HashMap<String, String>();
//			List<String> alWriteOff = new ArrayList<String>(); 

			pst = con.prepareStatement("select project_milestone_id, pro_milestone_amount from project_milestone_details");
			rs = pst.executeQuery();
			while(rs.next()) {
				hmProMilestoneAmount.put(rs.getString("project_milestone_id"), rs.getString("pro_milestone_amount"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select sum(invoice_amount) as invoice_amount, pro_freq_id from promntc_invoice_details where is_cancel=false and (invoice_type!=? or invoice_type!=?) group by pro_freq_id"); //and invoice_cancel=false 
			pst.setInt(1, ADHOC_INVOICE);
			pst.setInt(2, ADHOC_PRORETA_INVOICE);
			rs = pst.executeQuery();
			while(rs.next()) {
				hmProInvoiceAmount.put(rs.getString("pro_freq_id"), rs.getString("invoice_amount"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmProInvoiceAmount ===>> " + hmProInvoiceAmount);
			
			pst = con.prepareStatement("select sum(oc_other_amount) as ope_amount, pro_freq_id from promntc_invoice_details where is_cancel=false and (invoice_type!=? or invoice_type!=?) group by pro_freq_id"); // and invoice_cancel=false
			pst.setInt(1, ADHOC_INVOICE);
			pst.setInt(2, ADHOC_PRORETA_INVOICE);
			rs = pst.executeQuery();
			while(rs.next()) {
				hmProOPEAmount.put(rs.getString("pro_freq_id"), rs.getString("ope_amount"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select received_amount,tds_deducted, write_off_amount, invoice_id, is_write_off, exchange_rate,pro_freq_id from promntc_bill_amt_details where invoice_id in(select promntc_invoice_id from promntc_invoice_details where is_cancel=false and (invoice_type!=? or invoice_type!=?)) order by invoice_id, is_write_off desc"); //and invoice_cancel=false 
			pst.setInt(1, ADHOC_INVOICE);
			pst.setInt(2, ADHOC_PRORETA_INVOICE);
			rs = pst.executeQuery();
			while(rs.next()) {
				double dblReceivedAmt = uF.parseToDouble(rs.getString("received_amount"));
				double dblTDSAmt = uF.parseToDouble(rs.getString("tds_deducted"));
				double dblWriteOffAmt = uF.parseToDouble(rs.getString("write_off_amount"));
//				double dblExchageRate = uF.parseToDouble(rs.getString("exchange_rate"));
				
//				double dblTotalAmountP = (dblReceivedAmt/dblExchageRate) + (dblTDSAmt/dblExchageRate) ;
				double dblTotalAmountP = dblReceivedAmt + dblTDSAmt + dblWriteOffAmt;
				dblTotalAmountP += uF.parseToDouble(hmProReceivedAmount.get(rs.getString("pro_freq_id")));
				hmProReceivedAmount.put(rs.getString("pro_freq_id"), uF.formatIntoTwoDecimal(dblTotalAmountP));
				
//				double dblTotalAmount = (dblReceivedAmt/dblExchageRate) + (dblTDSAmt/dblExchageRate) ;
				double dblTotalAmount = dblReceivedAmt + dblTDSAmt + dblWriteOffAmt;
				dblTotalAmount += uF.parseToDouble(hmInvoiceReceivedAmount.get(rs.getString("invoice_id")));
				hmInvoiceReceivedAmount.put(rs.getString("invoice_id"), uF.formatIntoTwoDecimal(dblTotalAmount));
//				if(uF.parseToBoolean(rs.getString("is_write_off"))) {
//					alWriteOff.add(rs.getString("invoice_id"));
//				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmInvoiceReceivedAmount", hmInvoiceReceivedAmount);
			request.setAttribute("hmProReceivedAmount", hmProReceivedAmount);
			
			StringBuilder sbQue = new StringBuilder();
			sbQue.append("select invoice_generated_date, pro_freq_id, pro_id, invoice_code,promntc_invoice_id, invoice_amount, curr_id,invoice_cancel," +
				"oc_invoice_amount,invoice_type from promntc_invoice_details where is_cancel=false and invoice_type!=? and invoice_type!=?");
			if(getReportType() != null && getReportType().equals("3")) {
				sbQue.append(" and invoice_type = "+PARTIAL_INVOICE+" ");
			}
			pst = con.prepareStatement(sbQue.toString());
			pst.setInt(1, ADHOC_INVOICE);
			pst.setInt(2, ADHOC_PRORETA_INVOICE);
//			System.out.println("pst =======>> " + pst);
			rs = pst.executeQuery();
			Map<String, List<Map<String,String>>> hmInvDetails = new HashMap<String, List<Map<String,String>>>();
			Map<String, String> hmCurr1 = new HashMap<String, String>();
			StringBuilder sbProId1 = null;
			while(rs.next()) {
				hmCurr1 = hmCurrencyDetails.get(rs.getString("curr_id"));
				
				if(hmCurr1 == null) hmCurr1 = new HashMap<String, String>();
				List<Map<String,String>> outerList = hmInvDetails.get(rs.getString("pro_freq_id"));
				if(outerList == null) outerList = new ArrayList<Map<String,String>>();
				
				Map<String,String> hmInnerMap=new HashMap<String, String>();
				hmInnerMap.put("PROJECT_ID", rs.getString("pro_id"));
				hmInnerMap.put("PROJECT_FREQ_ID", rs.getString("pro_freq_id"));
				hmInnerMap.put("INVOICE_ID", rs.getString("promntc_invoice_id"));
				hmInnerMap.put("INVOICE_CODE", rs.getString("invoice_code"));
				
				double invoiceAmount = uF.parseToDouble(rs.getString("oc_invoice_amount"));
				
				hmInnerMap.put("INVOICE_AMOUNT",uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(invoiceAmount));
				hmInnerMap.put("INVOICE_AMOUNT_ONLY", rs.getString("oc_invoice_amount"));
				double dblBalance = uF.parseToDouble(rs.getString("oc_invoice_amount")) - uF.parseToDouble(hmInvoiceReceivedAmount.get(rs.getString("promntc_invoice_id")));
				
//				double dblTotBalance = dblBalance;
//				if(dblBalance>0 && !alWriteOff.contains(rs.getString("promntc_invoice_id"))) {
				if(dblBalance > 0) {
					hmInnerMap.put("BALANCE_AMOUNT", uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblBalance));					
				}
				hmInnerMap.put("INVOICE_GENERATED_DATE", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInnerMap.put("INVOICE_IS_CANCEL", rs.getString("invoice_cancel"));
				hmInnerMap.put("INVOICE_TYPE", rs.getString("invoice_type"));
				outerList.add(hmInnerMap);
				hmInvDetails.put(rs.getString("pro_freq_id"), outerList);
				
				if(getReportType() != null && getReportType().equals("3")) {
					if(sbProId1 == null) {
						sbProId1 = new StringBuilder();
						sbProId1.append(rs.getString("pro_id"));
					} else {
						sbProId1.append("," + rs.getString("pro_id"));
					}
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("sbProId1===>"+sbProId1);
//			System.out.println("hmInvDetails ===>> " + hmInvDetails);
			
			request.setAttribute("hmInvDetails", hmInvDetails);
			
			
			pst = con.prepareStatement("select pro_freq_id,invoice_id,invoice_amount from promntc_bill_amt_details");
			rs = pst.executeQuery();
			Map<String, String> hmReceiveAmt = new HashMap<String, String>();
			Map<String, String> hmClearedAmt = new HashMap<String, String>();
			while(rs.next()) {
//				hmReceiveAmt.put(rs.getString("pro_freq_id"), rs.getString("pro_freq_id"));
				double dblBalance = uF.parseToDouble(rs.getString("invoice_amount")) - uF.parseToDouble(hmInvoiceReceivedAmount.get(rs.getString("invoice_id")));
				
				if(dblBalance > 0.0) {
					hmReceiveAmt.put(rs.getString("pro_freq_id"), rs.getString("pro_freq_id"));					
				}
//				if(dblBalance<=0.0 || alWriteOff.contains(rs.getString("invoice_id"))) {
				if(dblBalance<=0.0) {
					hmClearedAmt.put(rs.getString("pro_freq_id"), rs.getString("pro_freq_id"));					
				}
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from projectmntnc where pro_id>0 ");
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id ="+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(CUSTOMER) && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(uF.parseToInt(getF_strWLocation())>0) {
				sbQuery.append(" and wlocation_id ="+uF.parseToInt(getF_strWLocation()));
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(CUSTOMER) && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			
			if(getBillingType() != null && getBillingType().length()>0) {
				sbQuery.append(" and billing_type = '"+getBillingType()+"' ");
			}
			
			if(getStrBillingFreq() != null && getStrBillingFreq().length()>0) {
				sbQuery.append(" and billing_kind = '"+getStrBillingFreq()+"' ");
			}
			
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and department_id = "+uF.parseToInt(getF_department()));
			}
			 
			if(getClient() != null && getClient().length>0 && getClient()[0].trim().length()>0) {
				sbQuery.append(" and client_id in ("+StringUtils.join(getClient(), ",")+") ");
			}
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				sbQuery.append(" and added_by = "+ uF.parseToInt(strSessionEmpId) +" ");
			}
			if(strUserType!=null && strUserType.equalsIgnoreCase(CUSTOMER)) {
				sbQuery.append(" and poc = "+ uF.parseToInt(strSessionEmpId) +" ");
			}
			sbQuery.append(" limit 50 ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst 0 ===>> " +pst);
			rs = pst.executeQuery();
			
			Map<String, Map<String, String>> hmProData = new HashMap<String, Map<String, String>>();
			StringBuilder sbProId = null;
			while(rs.next()) {
				
				/*if(getReportType() != null && getReportType().equals("1") && hmInvDetails.get(rs.getString("pro_id")) != null) {
					continue;
				} else if(getReportType() != null && getReportType().equals("2") && hmReceiveAmt.get(rs.getString("pro_id")) != null) {
					continue;
				} else if(getReportType() != null && getReportType().equals("4") && hmClearedAmt.get(rs.getString("pro_id")) == null) {
					continue;
				}*/
				
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("PROJECT_ID", rs.getString("pro_id"));
				hmInner.put("BILLING_CURR_ID", rs.getString("billing_curr_id"));
				hmInner.put("CLIENT_ID", rs.getString("client_id"));
				hmInner.put("CLIENT_NAME", uF.showData(CF.getClientNameById(con, rs.getString("client_id")), "N/A"));
				hmInner.put("PRO_NAME", uF.showData(rs.getString("pro_name"), "-"));
				hmInner.put("PROJECT_INVOICE_FORMAT_ID", rs.getString("invoice_template_type"));
				
				if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("D")) {
					hmInner.put("BILLING_TYPE", "Daily");
				} else if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("H")) {
					hmInner.put("BILLING_TYPE", "Hourly");
				} else if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("M")) {
					hmInner.put("BILLING_TYPE", "Monthly");
				} else if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("F")) {
					hmInner.put("BILLING_TYPE", "Fixed");
				}
				
				if(rs.getString("billing_kind")!=null && rs.getString("billing_kind").equalsIgnoreCase("W")) {
					hmInner.put("BILLING_KIND", "Weekly");
				} else if(rs.getString("billing_kind")!=null && rs.getString("billing_kind").equalsIgnoreCase("B")) {
					hmInner.put("BILLING_KIND", "BiWeekly");
				} else if(rs.getString("billing_kind")!=null && rs.getString("billing_kind").equalsIgnoreCase("M")) {
					hmInner.put("BILLING_KIND", "Monthly");
				} else if(rs.getString("billing_kind")!=null && rs.getString("billing_kind").equalsIgnoreCase("O")) {
					hmInner.put("BILLING_KIND", "One time/Fixed");
				}else if(rs.getString("billing_kind")!=null && rs.getString("billing_kind").equalsIgnoreCase("Q")) {
					hmInner.put("BILLING_KIND", "Quarterly");
				}else if(rs.getString("billing_kind")!=null && rs.getString("billing_kind").equalsIgnoreCase("H")) {
						hmInner.put("BILLING_KIND", "Half Year");
				}else if(rs.getString("billing_kind")!=null && rs.getString("billing_kind").equalsIgnoreCase("A")) {
					hmInner.put("BILLING_KIND", "Annually");
				}	
					
				hmInner.put("PAYMENT_TYPE", "");
				hmInner.put("ADDED_BY", rs.getString("added_by"));
				hmInner.put("ISMONTHLY", rs.getString("ismonthly"));
				
				hmInner.put("PAYCYCLE", getPaycycle());
				
				if(rs.getString("approve_status")!=null && rs.getString("approve_status").equalsIgnoreCase("n")) {
					hmInner.put("COMPLETED", "<img src=\"images1/icons/exclamation_mark_icon.png\" width=\"30\">");
				} else {
					hmInner.put("COMPLETED", "<img src=\"images1/icons/hd_tick_20x20.png\">");
				}
				hmInner.put("BILLING_AMOUNT", rs.getString("billing_amount"));
				
//				alReport.add(hmInner);
				hmProData.put(rs.getString("pro_id"), hmInner);
				
				if(sbProId == null) {
					sbProId = new StringBuilder();
					sbProId.append(rs.getString("pro_id"));
				} else {
					sbProId.append("," + rs.getString("pro_id"));
				}
			}
			rs.close();
			pst.close();
			if(sbProId == null) {
				sbProId = new StringBuilder();
			}
//			 System.out.println("sbproId===>"+sbProId);
			
			
			List alReport = new ArrayList();
			Map<String, Map<String, String>> hmProInvoiceAmt = new HashMap<String, Map<String, String>>();
			
			if(sbProId != null && !sbProId.toString().equals("")) {
				StringBuilder sbQuery1 = new StringBuilder();
				sbQuery1.append("select pf.* from projectmntnc_frequency pf, projectmntnc p where pf.pro_id = p.pro_id and (pf.is_delete != true or pf.is_delete is null) and p.pro_id in ("+ sbProId.toString() +") ");
				if(sbProId1 !=null) {
					sbQuery1.append("and p.pro_id in ("+ sbProId1.toString() +") ");
				}
				sbQuery1.append(" order by pro_freq_id desc limit 100");
				
				pst = con.prepareStatement(sbQuery1.toString());
//				System.out.println("pst ===>> " +pst);
				rs = pst.executeQuery();
//				System.out.println("getReportType ===>> " + getReportType());
//				System.out.println("sbProId1 ===>> " + sbProId1);
//				System.out.println("hmClearedAmt ===>> " + hmClearedAmt);
//				System.out.println("hmReceiveAmt ===>> " + hmReceiveAmt);
//				Map hmProFreqData = new HashMap();
				while(rs.next()) {
					Map hmProFreqInnner = new HashMap();
//					System.out.println("pro_freq_id ===>> " + rs.getString("pro_freq_id"));
					if(getReportType() != null && getReportType().equals("1") && hmClearedAmt.get(rs.getString("pro_freq_id")) != null) {
						continue;
					} else if(getReportType() != null && getReportType().equals("2") && hmReceiveAmt.get(rs.getString("pro_freq_id")) == null) {
						continue;
					} else if(getReportType() != null && getReportType().equals("3") && sbProId1 == null) {
						continue;
					} else if(getReportType() != null && getReportType().equals("4") && hmClearedAmt.get(rs.getString("pro_freq_id")) == null) {
						continue;
					}
//					System.out.println("pro_freq_id ===>> " + rs.getString("pro_freq_id"));
//					System.out.println("pro_freq_id ===>> " + rs.getString("pro_freq_id"));
					List<String> alInner = new ArrayList<String>();
					String custStatus = getCustomerTimeSheetApprovalStatus(con, rs.getString("pro_id"), rs.getString("freq_start_date"), rs.getString("freq_end_date"));
					String managerStatus = getManagerTimeSheetApprovalStatus(con, rs.getString("pro_id"), rs.getString("freq_start_date"), rs.getString("freq_end_date"));
					
					double dblInvoiceAmt = uF.parseToDouble(hmProInvoiceAmount.get(rs.getString("pro_freq_id")));
//					System.out.println(rs.getString("pro_freq_id") +" -- dblInvoiceAmt ===>> " + dblInvoiceAmt);
					if(strUserType != null && strUserType.equals(CUSTOMER) && dblInvoiceAmt == 0.0) {
						continue;
					}
					
					Map<String, String> hmProDataInnner = hmProData.get(rs.getString("pro_id"));
					
					hmProFreqInnner.put("MANAGER_TIMESHEET_APPROVAL", managerStatus);
					hmProFreqInnner.put("CUSTOMER_TIMESHEET_APPROVAL", custStatus);
					
					hmProFreqInnner.put("PRO_FREQ_NAME", "");
					
					if(uF.parseToInt(rs.getString("pro_milestone_id")) > 0) {
						String takOrPercent = getMilestoneTaskORPercent(con,uF,rs.getString("pro_milestone_id"));
						hmProFreqInnner.put("BILLING_KIND", uF.showData(takOrPercent, "-")+"<br/>(" + uF.showData(rs.getString("pro_freq_name"), "-")+")");
						hmProFreqInnner.put("PRO_FREQ_NAME", "");
					} else {
						hmProFreqInnner.put("BILLING_KIND", uF.showData(hmProDataInnner.get("BILLING_KIND"), "-"));
						hmProFreqInnner.put("PRO_FREQ_NAME", uF.showData(rs.getString("pro_freq_name"), "-"));
					}
//					System.out.println(rs.getString("pro_freq_id") + " ======>> " + hmProDataInnner);
					
					hmCurr1 = hmCurrencyDetails.get(hmProDataInnner.get("BILLING_CURR_ID"));
					
					if(hmCurr1 == null) hmCurr1 = new HashMap<String, String>();
					
//					double dblInvoiceAmt = uF.parseToDouble(hmProInvoiceAmount.get(rs.getString("pro_freq_id")));
					double dblPaidAmt = uF.parseToDouble(hmProReceivedAmount.get(rs.getString("pro_freq_id")));
					double dblProjectAmt = 0;
					if(uF.parseToInt(rs.getString("pro_milestone_id")) > 0) {
						dblProjectAmt = uF.parseToDouble(hmProMilestoneAmount.get(rs.getString("pro_milestone_id")));
					} else {
						dblProjectAmt = uF.parseToDouble(hmProDataInnner.get("BILLING_AMOUNT"));
					}
					double dblPendingAmt = dblProjectAmt - dblPaidAmt;
					double dblBalanceAmt = dblInvoiceAmt - dblPaidAmt;
					
					if(dblPendingAmt < 0) {
						dblPendingAmt = 0;
					}
					
					if(dblBalanceAmt < 0) {
						dblBalanceAmt = 0;
					}
					
					double dblFinalInvoiceAmt = dblInvoiceAmt;
					double dblFinalPaidAmt = dblPaidAmt;
					double dblFinalProjectAmt = dblProjectAmt;
					double dblFinalPendingAmt = dblPendingAmt;
					double dblFinalBalanceAmt = dblBalanceAmt;
					
					alInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblFinalProjectAmt));
					alInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblFinalPendingAmt));
					alInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblFinalInvoiceAmt));
					alInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblFinalPaidAmt));
					alInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblFinalBalanceAmt));
					
					hmProFreqInnner.put("PRO_FREQ_ID", rs.getString("pro_freq_id"));
					hmProFreqInnner.put("PRO_ID", rs.getString("pro_id"));
					hmProFreqInnner.put(rs.getString("pro_id"), hmProDataInnner);
					hmProFreqInnner.put("BILLING_SUMMARY", alInner);
					hmProFreqInnner.put("PRO_OPE_AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProInvoiceAmount.get(rs.getString("pro_freq_id")))));
					
					Map<String, String> hmProInvAmt = new HashMap<String, String>();
					hmProInvAmt.put("PRO_AMT", uF.formatIntoTwoDecimal(dblProjectAmt));
					hmProInvAmt.put("INVOICE_AMT", uF.formatIntoTwoDecimal(dblInvoiceAmt));
					hmProInvoiceAmt.put(rs.getString("pro_freq_id"), hmProInvAmt);
					
	//				alReport.add(hmInner);
	//				hmProFreqData.put(rs.getString("pro_freq_id"), hmProFreqInnner);
//					System.out.println("pro_milestone_id ==>>> " + uF.parseToInt(rs.getString("pro_milestone_id")));
					if(uF.parseToInt(rs.getString("pro_milestone_id")) > 0) {
						boolean milestoneStatus = checkMilestoneStatus(con, uF, rs.getString("pro_milestone_id"),rs.getString("pro_id"));
//						System.out.println("milestoneStatus ==>>> " + milestoneStatus);
						if(milestoneStatus) {
							alReport.add(hmProFreqInnner);
						}
					} else {
						alReport.add(hmProFreqInnner);
					}
				}
				rs.close();
				pst.close();
			}
//			System.out.println("alReport ====>>> " + alReport);
			
			int proCnt = alReport.size();
			request.setAttribute("proCnt", proCnt+"");
			
			if(alReport.size() > 0){
//				System.out.println("alReport size()=====>"+alReport.size());
				int proCount = alReport.size()/10;
				if(alReport.size()%10 != 0) {
					proCount++;
				}
				//int proCount = 14;
				request.setAttribute("proCount", proCount+"");
				
//			System.out.println("=======pro wise proCount========"+proCount);
//				System.out.println("=======proCnt========"+proCnt);
//				System.out.println("=======uF.parseToInt(getMinLimit())========"+uF.parseToInt(getMinLimit()));
				if(alReport.size() > 10){
					int nStart = 0;
					int nEnd = 10;
					if(uF.parseToInt(getMinLimit())>0){
						nStart = uF.parseToInt(getMinLimit());
						nEnd = uF.parseToInt(getMinLimit())+10;
					}
					
//					System.out.println("=======nStart========"+nStart);
//					System.out.println("=======nEnd========"+nEnd);
					if(nEnd > alReport.size()){
						nEnd = alReport.size();
//						System.out.println("=======after nEnd========"+nEnd);
					}
					
//					System.out.println("=======alReport========"+alReport.toString());
					alReport = alReport.subList(nStart, nEnd);
				}
			}
			
			
			request.setAttribute("alReport", alReport);
			request.setAttribute("hmProInvoiceAmt", hmProInvoiceAmt);
			
			pst = con.prepareStatement(selectNotifications);
			pst.setInt(1, N_PAYMENT_ALERT); 
			rs = pst.executeQuery();
			boolean isEmail = false;
			while(rs.next()){
				isEmail = uF.parseToBoolean(rs.getString("isemail"));
			}
			rs.close();
			pst.close();
			request.setAttribute("isEmail", isEmail);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
//	public void getProjectBillingDetails(UtilityFunctions uF) {
//		Database db = new Database();
//		db.setRequest(request);
//		Connection con=null;
//		PreparedStatement pst=null;
//		ResultSet rs=null;
//		
//		try {
//			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
//				organisationList = new FillOrganisation(request).fillOrganisation();
//				wLocationList = new FillWLocation(request).fillWLocation(getF_org());
//				
//			} else {
//				wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
//				organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
//			}
//			departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
//			clientList = new FillClients(request).fillClients(false);
//			
////			if(getF_strWLocation()==null) {
////				setF_strWLocation((String)session.getAttribute(WLOCATIONID));
////			}
//			
//			getSelectedFilter(uF);
//			
//			con = db.makeConnection(con);
//			
//			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
//			
//			Map<String, String> hmProReceivedAmount = new HashMap<String, String>();
//			Map<String, String> hmProInvoiceAmount = new HashMap<String, String>();
//			Map<String, String> hmProOPEAmount = new HashMap<String, String>();
//			Map<String, String> hmInvoiceReceivedAmount = new HashMap<String, String>();
//			Map<String, String> hmProMilestoneAmount = new HashMap<String, String>();
////			List<String> alWriteOff = new ArrayList<String>(); 
//
//			pst = con.prepareStatement("select project_milestone_id, pro_milestone_amount from project_milestone_details");
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				hmProMilestoneAmount.put(rs.getString("project_milestone_id"), rs.getString("pro_milestone_amount"));
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select sum(invoice_amount) as invoice_amount, pro_freq_id from promntc_invoice_details where is_cancel=false and invoice_cancel=false and (invoice_type!=? or invoice_type!=?) group by pro_freq_id");
//			pst.setInt(1, ADHOC_INVOICE);
//			pst.setInt(2, ADHOC_PRORETA_INVOICE);
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				hmProInvoiceAmount.put(rs.getString("pro_freq_id"), rs.getString("invoice_amount"));
//			}
//			rs.close();
//			pst.close();
////			System.out.println("hmProInvoiceAmount ===>> " + hmProInvoiceAmount);
//			
//			pst = con.prepareStatement("select sum(oc_other_amount) as ope_amount, pro_freq_id from promntc_invoice_details where is_cancel=false and invoice_cancel=false and (invoice_type!=? or invoice_type!=?) group by pro_freq_id");
//			pst.setInt(1, ADHOC_INVOICE);
//			pst.setInt(2, ADHOC_PRORETA_INVOICE);
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				hmProOPEAmount.put(rs.getString("pro_freq_id"), rs.getString("ope_amount"));
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select received_amount,tds_deducted, write_off_amount, invoice_id, is_write_off, exchange_rate,pro_freq_id from promntc_bill_amt_details where invoice_id in(select promntc_invoice_id from promntc_invoice_details where is_cancel=false and invoice_cancel=false and (invoice_type!=? or invoice_type!=?)) order by invoice_id, is_write_off desc");
//			pst.setInt(1, ADHOC_INVOICE);
//			pst.setInt(2, ADHOC_PRORETA_INVOICE);
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				double dblReceivedAmt = uF.parseToDouble(rs.getString("received_amount"));
//				double dblTDSAmt = uF.parseToDouble(rs.getString("tds_deducted"));
//				double dblWriteOffAmt = uF.parseToDouble(rs.getString("write_off_amount"));
////				double dblExchageRate = uF.parseToDouble(rs.getString("exchange_rate"));
//				
////				double dblTotalAmountP = (dblReceivedAmt/dblExchageRate) + (dblTDSAmt/dblExchageRate) ;
//				double dblTotalAmountP = dblReceivedAmt + dblTDSAmt + dblWriteOffAmt;
//				dblTotalAmountP += uF.parseToDouble(hmProReceivedAmount.get(rs.getString("pro_freq_id")));
//				hmProReceivedAmount.put(rs.getString("pro_freq_id"), uF.formatIntoTwoDecimal(dblTotalAmountP));
//				
////				double dblTotalAmount = (dblReceivedAmt/dblExchageRate) + (dblTDSAmt/dblExchageRate) ;
//				double dblTotalAmount = dblReceivedAmt + dblTDSAmt + dblWriteOffAmt;
//				dblTotalAmount += uF.parseToDouble(hmInvoiceReceivedAmount.get(rs.getString("invoice_id")));
//				hmInvoiceReceivedAmount.put(rs.getString("invoice_id"), uF.formatIntoTwoDecimal(dblTotalAmount));
////				if(uF.parseToBoolean(rs.getString("is_write_off"))) {
////					alWriteOff.add(rs.getString("invoice_id"));
////				}
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("hmInvoiceReceivedAmount", hmInvoiceReceivedAmount);
//			request.setAttribute("hmProReceivedAmount", hmProReceivedAmount);
//			
//			StringBuilder sbQue = new StringBuilder();
//			sbQue.append("select invoice_generated_date, pro_freq_id, pro_id, invoice_code,promntc_invoice_id, invoice_amount, curr_id,invoice_cancel," +
//				"oc_invoice_amount,invoice_type from promntc_invoice_details where is_cancel=false and invoice_type!=? and invoice_type!=?");
//			if(getReportType() != null && getReportType().equals("3")) {
//				sbQue.append(" and invoice_type = "+PARTIAL_INVOICE+" ");
//			}
//			pst = con.prepareStatement(sbQue.toString());
//			pst.setInt(1, ADHOC_INVOICE);
//			pst.setInt(2, ADHOC_PRORETA_INVOICE);
////			System.out.println("pst =======>> " + pst);
//			rs = pst.executeQuery();
//			Map<String, List<Map<String,String>>> hmInvDetails = new HashMap<String, List<Map<String,String>>>();
//			Map<String, String> hmCurr1 = new HashMap<String, String>();
//			StringBuilder sbProId1 = null;
//			while(rs.next()) {
//				hmCurr1 = hmCurrencyDetails.get(rs.getString("curr_id"));
//				
//				if(hmCurr1 == null) hmCurr1 = new HashMap<String, String>();
//				List<Map<String,String>> outerList = hmInvDetails.get(rs.getString("pro_freq_id"));
//				if(outerList == null) outerList = new ArrayList<Map<String,String>>();
//				
//				Map<String,String> hmInnerMap=new HashMap<String, String>();
//				hmInnerMap.put("PROJECT_ID", rs.getString("pro_id"));
//				hmInnerMap.put("PROJECT_FREQ_ID", rs.getString("pro_freq_id"));
//				hmInnerMap.put("INVOICE_ID", rs.getString("promntc_invoice_id"));
//				hmInnerMap.put("INVOICE_CODE", rs.getString("invoice_code"));
//				
//				double invoiceAmount = uF.parseToDouble(rs.getString("oc_invoice_amount"));
//				
//				hmInnerMap.put("INVOICE_AMOUNT",uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(invoiceAmount));
//				hmInnerMap.put("INVOICE_AMOUNT_ONLY", rs.getString("oc_invoice_amount"));
//				double dblBalance = uF.parseToDouble(rs.getString("oc_invoice_amount")) - uF.parseToDouble(hmInvoiceReceivedAmount.get(rs.getString("promntc_invoice_id")));
//				
////				double dblTotBalance = dblBalance;
////				if(dblBalance>0 && !alWriteOff.contains(rs.getString("promntc_invoice_id"))) {
//				if(dblBalance > 0) {
//					hmInnerMap.put("BALANCE_AMOUNT", uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblBalance));					
//				}
//				hmInnerMap.put("INVOICE_GENERATED_DATE", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmInnerMap.put("INVOICE_IS_CANCEL", rs.getString("invoice_cancel"));
//				hmInnerMap.put("INVOICE_TYPE", rs.getString("invoice_type"));
//				outerList.add(hmInnerMap);
//				hmInvDetails.put(rs.getString("pro_freq_id"), outerList);
//				
//				if(getReportType() != null && getReportType().equals("3")) {
//					if(sbProId1 == null) {
//						sbProId1 = new StringBuilder();
//						sbProId1.append(rs.getString("pro_id"));
//					} else {
//						sbProId1.append("," + rs.getString("pro_id"));
//					}
//				}
//			}
//			rs.close();
//			pst.close();
////			System.out.println("hmInvDetails ===>> " + hmInvDetails);
//			
//			request.setAttribute("hmInvDetails", hmInvDetails);
//			
//			
//			pst = con.prepareStatement("select pro_freq_id,invoice_id,invoice_amount from promntc_bill_amt_details");
//			rs = pst.executeQuery();
//			Map<String, String> hmReceiveAmt = new HashMap<String, String>();
//			Map<String, String> hmClearedAmt = new HashMap<String, String>();
//			while(rs.next()) {
////				hmReceiveAmt.put(rs.getString("pro_freq_id"), rs.getString("pro_freq_id"));
//				double dblBalance = uF.parseToDouble(rs.getString("invoice_amount")) - uF.parseToDouble(hmInvoiceReceivedAmount.get(rs.getString("invoice_id")));
//				
//				if(dblBalance > 0.0) {
//					hmReceiveAmt.put(rs.getString("pro_freq_id"), rs.getString("pro_freq_id"));					
//				}
////				if(dblBalance<=0.0 || alWriteOff.contains(rs.getString("invoice_id"))) {
//				if(dblBalance<=0.0) {
//					hmClearedAmt.put(rs.getString("pro_freq_id"), rs.getString("pro_freq_id"));					
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from projectmntnc where pro_id>0 ");
//			if(uF.parseToInt(getF_org())>0) {
//				sbQuery.append(" and org_id ="+uF.parseToInt(getF_org()));
//			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(CUSTOMER) && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
//				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//			}
//			if(uF.parseToInt(getF_strWLocation())>0) {
//				sbQuery.append(" and wlocation_id ="+uF.parseToInt(getF_strWLocation()));
//	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(CUSTOMER) && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//			}
//			
//			if(getBillingType() != null && getBillingType().length()>0) {
//				sbQuery.append(" and billing_type = '"+getBillingType()+"' ");
//			}
//			
//			if(getStrBillingFreq() != null && getStrBillingFreq().length()>0) {
//				sbQuery.append(" and billing_kind = '"+getStrBillingFreq()+"' ");
//			}
//			
//			if(uF.parseToInt(getF_department())>0) {
//				sbQuery.append(" and department_id = "+uF.parseToInt(getF_department()));
//			}
//			 
//			if(getF_client() != null && getF_client().length>0) {
//				sbQuery.append(" and client_id in ("+StringUtils.join(getF_client(), ",")+") ");
//			}
//			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
//				sbQuery.append(" and added_by = "+ uF.parseToInt(strSessionEmpId) +" ");
//			}
//			if(strUserType!=null && strUserType.equalsIgnoreCase(CUSTOMER)) {
//				sbQuery.append(" and poc = "+ uF.parseToInt(strSessionEmpId) +" ");
//			}
//			pst = con.prepareStatement(sbQuery.toString());
////			System.out.println("pst 0 ===>> " +pst);
//			rs = pst.executeQuery();
//			
//			Map<String, Map<String, String>> hmProData = new HashMap<String, Map<String, String>>();
//			StringBuilder sbProId = null;
//			while(rs.next()) {
//				
//				/*if(getReportType() != null && getReportType().equals("1") && hmInvDetails.get(rs.getString("pro_id")) != null) {
//					continue;
//				} else if(getReportType() != null && getReportType().equals("2") && hmReceiveAmt.get(rs.getString("pro_id")) != null) {
//					continue;
//				} else if(getReportType() != null && getReportType().equals("4") && hmClearedAmt.get(rs.getString("pro_id")) == null) {
//					continue;
//				}*/
//				
//				Map<String, String> hmInner = new HashMap<String, String>();
//				hmInner.put("PROJECT_ID", rs.getString("pro_id"));
//				hmInner.put("BILLING_CURR_ID", rs.getString("billing_curr_id"));
//				hmInner.put("CLIENT_ID", rs.getString("client_id"));
//				hmInner.put("CLIENT_NAME", uF.showData(CF.getClientNameById(con, rs.getString("client_id")), "N/A"));
//				hmInner.put("PRO_NAME", uF.showData(rs.getString("pro_name"), "-"));
//				hmInner.put("PROJECT_INVOICE_FORMAT_ID", rs.getString("invoice_template_type"));
//				
//				if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("D")) {
//					hmInner.put("BILLING_TYPE", "Daily");
//				} else if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("H")) {
//					hmInner.put("BILLING_TYPE", "Hourly");
//				} else if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("M")) {
//					hmInner.put("BILLING_TYPE", "Monthly");
//				} else if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("F")) {
//					hmInner.put("BILLING_TYPE", "Fixed");
//				}
//				
//				if(rs.getString("billing_kind")!=null && rs.getString("billing_kind").equalsIgnoreCase("W")) {
//					hmInner.put("BILLING_KIND", "Weekly");
//				} else if(rs.getString("billing_kind")!=null && rs.getString("billing_kind").equalsIgnoreCase("B")) {
//					hmInner.put("BILLING_KIND", "BiWeekly");
//				} else if(rs.getString("billing_kind")!=null && rs.getString("billing_kind").equalsIgnoreCase("M")) {
//					hmInner.put("BILLING_KIND", "Monthly");
//				} else if(rs.getString("billing_kind")!=null && rs.getString("billing_kind").equalsIgnoreCase("O")) {
//					hmInner.put("BILLING_KIND", "One time/Fixed");
//				}
//				
//				hmInner.put("PAYMENT_TYPE", "");
//				hmInner.put("ADDED_BY", rs.getString("added_by"));
//				hmInner.put("ISMONTHLY", rs.getString("ismonthly"));
//				
//				hmInner.put("PAYCYCLE", getPaycycle());
//				
//				if(rs.getString("approve_status")!=null && rs.getString("approve_status").equalsIgnoreCase("n")) {
//					hmInner.put("COMPLETED", "<img src=\"images1/icons/exclamation_mark_icon.png\" width=\"30\">");
//				} else {
//					hmInner.put("COMPLETED", "<img src=\"images1/icons/hd_tick_20x20.png\">");
//				}
//				hmInner.put("BILLING_AMOUNT", rs.getString("billing_amount"));
//				
////				alReport.add(hmInner);
//				hmProData.put(rs.getString("pro_id"), hmInner);
//				
//				if(sbProId == null) {
//					sbProId = new StringBuilder();
//					sbProId.append(rs.getString("pro_id"));
//				} else {
//					sbProId.append("," + rs.getString("pro_id"));
//				}
//			}
//			rs.close();
//			pst.close();
//			if(sbProId == null) {
//				sbProId = new StringBuilder();
//			}
//			
//			
//			
//			List alReport = new ArrayList();
//			Map<String, Map<String, String>> hmProInvoiceAmt = new HashMap<String, Map<String, String>>();
//			
//			if(sbProId != null && !sbProId.toString().equals("")) {
//				StringBuilder sbQuery1 = new StringBuilder();
//				sbQuery1.append("select pf.* from projectmntnc_frequency pf, projectmntnc p where pf.pro_id = p.pro_id and (pf.is_delete != true or pf.is_delete is null) and p.pro_id in ("+ sbProId.toString() +") ");
//				if(sbProId1 !=null) {
//					sbQuery1.append("and p.pro_id in ("+ sbProId1.toString() +") ");
//				}
//				sbQuery1.append(" order by pro_freq_id desc");
//				pst = con.prepareStatement(sbQuery1.toString());
////				System.out.println("pst ===>> " +pst);
//				rs = pst.executeQuery();
////				Map hmProFreqData = new HashMap();
//				while(rs.next()) {
//					Map hmProFreqInnner = new HashMap();
//					
//					if(getReportType() != null && getReportType().equals("1") && hmClearedAmt.get(rs.getString("pro_freq_id")) != null) {
//						continue;
//					} else if(getReportType() != null && getReportType().equals("2") && hmReceiveAmt.get(rs.getString("pro_freq_id")) == null) {
//						continue;
//					} else if(getReportType() != null && getReportType().equals("3") && sbProId1 == null) {
//						continue;
//					} else if(getReportType() != null && getReportType().equals("4") && hmClearedAmt.get(rs.getString("pro_freq_id")) == null) {
//						continue;
//					}
//					
//					List<String> alInner = new ArrayList<String>();
//					String custStatus = getCustomerTimeSheetApprovalStatus(con, rs.getString("pro_id"), rs.getString("freq_start_date"), rs.getString("freq_end_date"));
//					String managerStatus = getManagerTimeSheetApprovalStatus(con, rs.getString("pro_id"), rs.getString("freq_start_date"), rs.getString("freq_end_date"));
//					
//					double dblInvoiceAmt = uF.parseToDouble(hmProInvoiceAmount.get(rs.getString("pro_freq_id")));
//					
//					if(strUserType != null && strUserType.equals(CUSTOMER) && dblInvoiceAmt == 0.0) {
//						continue;
//					}
//					
//					Map<String, String> hmProDataInnner = hmProData.get(rs.getString("pro_id"));
//					
//					hmProFreqInnner.put("MANAGER_TIMESHEET_APPROVAL", managerStatus);
//					hmProFreqInnner.put("CUSTOMER_TIMESHEET_APPROVAL", custStatus);
//					
//					hmProFreqInnner.put("PRO_FREQ_NAME", "");
//					
//					if(uF.parseToInt(rs.getString("pro_milestone_id")) > 0) {
//						String takOrPercent = getMilestoneTaskORPercent(con,uF,rs.getString("pro_milestone_id"));
//						hmProFreqInnner.put("BILLING_KIND", uF.showData(takOrPercent, "-")+"<br/>(" + uF.showData(rs.getString("pro_freq_name"), "-")+")");
//						hmProFreqInnner.put("PRO_FREQ_NAME", "");
//					} else {
//						hmProFreqInnner.put("BILLING_KIND", uF.showData(hmProDataInnner.get("BILLING_KIND"), "-"));
//						hmProFreqInnner.put("PRO_FREQ_NAME", uF.showData(rs.getString("pro_freq_name"), "-"));
//					}
////					System.out.println(rs.getString("pro_freq_id") + " ======>> " + hmProDataInnner);
//					
//					hmCurr1 = hmCurrencyDetails.get(hmProDataInnner.get("BILLING_CURR_ID"));
//					
//					if(hmCurr1 == null) hmCurr1 = new HashMap<String, String>();
//					
////					double dblInvoiceAmt = uF.parseToDouble(hmProInvoiceAmount.get(rs.getString("pro_freq_id")));
//					double dblPaidAmt = uF.parseToDouble(hmProReceivedAmount.get(rs.getString("pro_freq_id")));
//					double dblProjectAmt = 0;
//					if(uF.parseToInt(rs.getString("pro_milestone_id")) > 0) {
//						dblProjectAmt = uF.parseToDouble(hmProMilestoneAmount.get(rs.getString("pro_milestone_id")));
//					} else {
//						dblProjectAmt = uF.parseToDouble(hmProDataInnner.get("BILLING_AMOUNT"));
//					}
//					double dblPendingAmt = dblProjectAmt - dblPaidAmt;
//					double dblBalanceAmt = dblInvoiceAmt - dblPaidAmt;
//					
//					if(dblPendingAmt < 0) {
//						dblPendingAmt = 0;
//					}
//					
//					if(dblBalanceAmt < 0) {
//						dblBalanceAmt = 0;
//					}
//					
//					double dblFinalInvoiceAmt = dblInvoiceAmt;
//					double dblFinalPaidAmt = dblPaidAmt;
//					double dblFinalProjectAmt = dblProjectAmt;
//					double dblFinalPendingAmt = dblPendingAmt;
//					double dblFinalBalanceAmt = dblBalanceAmt;
//					
//					alInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblFinalProjectAmt));
//					alInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblFinalPendingAmt));
//					alInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblFinalInvoiceAmt));
//					alInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblFinalPaidAmt));
//					alInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblFinalBalanceAmt));
//					
//					hmProFreqInnner.put("PRO_FREQ_ID", rs.getString("pro_freq_id"));
//					hmProFreqInnner.put("PRO_ID", rs.getString("pro_id"));
//					hmProFreqInnner.put(rs.getString("pro_id"), hmProDataInnner);
//					hmProFreqInnner.put("BILLING_SUMMARY", alInner);
//					hmProFreqInnner.put("PRO_OPE_AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProInvoiceAmount.get(rs.getString("pro_freq_id")))));
//					
//					Map<String, String> hmProInvAmt = new HashMap<String, String>();
//					hmProInvAmt.put("PRO_AMT", uF.formatIntoTwoDecimal(dblProjectAmt));
//					hmProInvAmt.put("INVOICE_AMT", uF.formatIntoTwoDecimal(dblInvoiceAmt));
//					hmProInvoiceAmt.put(rs.getString("pro_freq_id"), hmProInvAmt);
//					
//	//				alReport.add(hmInner);
//	//				hmProFreqData.put(rs.getString("pro_freq_id"), hmProFreqInnner);
////					System.out.println("pro_milestone_id ==>>> " + uF.parseToInt(rs.getString("pro_milestone_id")));
//					if(uF.parseToInt(rs.getString("pro_milestone_id")) > 0) {
//						boolean milestoneStatus = checkMilestoneStatus(con, uF, rs.getString("pro_milestone_id"),rs.getString("pro_id"));
////						System.out.println("milestoneStatus ==>>> " + milestoneStatus);
//						if(milestoneStatus) {
//							alReport.add(hmProFreqInnner);
//						}
//					} else {
//						alReport.add(hmProFreqInnner);
//					}
//				}
//				rs.close();
//				pst.close();
//			}
////			System.out.println("alReport ====>>> " + alReport);
//			
//			request.setAttribute("alReport", alReport);
//			request.setAttribute("hmProInvoiceAmt", hmProInvoiceAmt);
//			
//			
//			
//			
//			
//			
////		******************************************* AdHoc Invoice Details *****************************************************
//			
//			
//			
//			Map hmEmpWlocation = CF.getEmpWlocationMap(con);
//			Map hmWorkLocation = CF.getWorkLocationMap(con);
//			
//			String strWLocation = (String)hmEmpWlocation.get((String)session.getAttribute(EMPID));
//			Map hmWlocation = (Map) hmWorkLocation.get(strWLocation);
//			if(hmWlocation == null) hmWlocation = new HashMap();		  
//			  
//			request.setAttribute("hmWlocation", hmWlocation);
//			
//			Map<String, String> hmPaidAdhocAmount = new HashMap<String, String>();
//			Map<String, String> hmProAdhocInvoiceAmount = new HashMap<String, String>();
//			Map<String, String> hmAdhocInvoiceAmount = new HashMap<String, String>();
//			
//			pst = con.prepareStatement("select sum(oc_invoice_amount) as invoice_amount, promntc_invoice_id from promntc_invoice_details where is_cancel=false and invoice_cancel=false and (invoice_type=? or invoice_type=?) group by promntc_invoice_id");
//			pst.setInt(1, ADHOC_INVOICE);
//			pst.setInt(2, ADHOC_PRORETA_INVOICE);
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				hmProAdhocInvoiceAmount.put(rs.getString("promntc_invoice_id"), rs.getString("invoice_amount"));
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select received_amount,tds_deducted, write_off_amount, invoice_id, is_write_off, exchange_rate,pro_id from promntc_bill_amt_details where invoice_id in(select promntc_invoice_id from promntc_invoice_details where is_cancel=false and invoice_cancel=false and (invoice_type=? or invoice_type=?)) order by invoice_id, is_write_off desc");
//			pst.setInt(1, ADHOC_INVOICE);
//			pst.setInt(2, ADHOC_PRORETA_INVOICE);
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				double dblReceivedAmt = uF.parseToDouble(rs.getString("received_amount"));
//				double dblTDSAmt = uF.parseToDouble(rs.getString("tds_deducted"));
//				double dblWriteOffAmt = uF.parseToDouble(rs.getString("write_off_amount"));
////				double dblExchageRate = uF.parseToDouble(rs.getString("exchange_rate"));
//				
////				double dblTotalAmountP = (dblReceivedAmt/dblExchageRate) + (dblTDSAmt/dblExchageRate);
//				double dblTotalAmountP = dblReceivedAmt + dblTDSAmt + dblWriteOffAmt;
//				dblTotalAmountP += uF.parseToDouble(hmPaidAdhocAmount.get(rs.getString("invoice_id")));
//				hmPaidAdhocAmount.put(rs.getString("invoice_id"), uF.formatIntoTwoDecimal(dblTotalAmountP));
//				
////				double dblTotalAmount = (dblReceivedAmt/dblExchageRate) + (dblTDSAmt/dblExchageRate);
//				double dblTotalAmount = dblReceivedAmt + dblTDSAmt + dblWriteOffAmt;
//				dblTotalAmount += uF.parseToDouble(hmAdhocInvoiceAmount.get(rs.getString("invoice_id")));
//				hmAdhocInvoiceAmount.put(rs.getString("invoice_id"), uF.formatIntoTwoDecimal(dblTotalAmount));
////				if(uF.parseToBoolean(rs.getString("is_write_off"))) {
////					alWriteOff.add(rs.getString("invoice_id"));
////				}
//			}
//			rs.close();
//			pst.close();
//			
//			
//			Map<String, String> hmOtherProectName = new HashMap<String, String>();
//			pst = con.prepareStatement("select pro_id, pro_name from projectmntnc");
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				hmOtherProectName.put(rs.getString("pro_id"), rs.getString("pro_name"));
//			}
//			rs.close();
//			pst.close();
//			
//			
//			sbQuery = new StringBuilder();
//			sbQuery.append("select * from promntc_invoice_details where is_cancel=false and (invoice_type=? or invoice_type=?) ");		
//			if(getF_client() != null && getF_client().length>0) {
//				sbQuery.append(" and client_id in ("+StringUtils.join(getF_client(), ",")+") ");
//			}
//			if(strUserType != null && strUserType.equals(CUSTOMER)) {
//				sbQuery.append(" and spoc_id= "+uF.parseToInt(strSessionEmpId)+" ");
//			}
//			sbQuery.append(" order by invoice_generated_date desc, promntc_invoice_id desc");
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, ADHOC_INVOICE);
//			pst.setInt(2, ADHOC_PRORETA_INVOICE);
//			rs = pst.executeQuery();			
//			List alAdhocReport = new ArrayList();			   
//			while(rs.next()) {
//				Map hmAdhocInner = new HashMap();
//				List alAdhocInner = new ArrayList();
//				double dblInvoiceAmt = uF.parseToDouble(hmProAdhocInvoiceAmount.get(rs.getString("promntc_invoice_id")));
//				double dblPaidAmt = uF.parseToDouble(hmPaidAdhocAmount.get(rs.getString("promntc_invoice_id")));
//				double dblBalanceAmt = dblInvoiceAmt - dblPaidAmt;
//				if(getReportType() != null && getReportType().equals("1") && dblBalanceAmt <= 0) {
//					continue;
//				} else if(getReportType() != null && getReportType().equals("4") && dblBalanceAmt > 0) {
//					continue;
//				}
//				
//				
//				hmAdhocInner.put("INVOICE_ID", rs.getString("promntc_invoice_id"));
//				hmAdhocInner.put("INVOICE_GENERATED_DATE", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmAdhocInner.put("INVOICE_GENERATED_BY", rs.getString("invoice_generated_by"));
//				hmAdhocInner.put("INVOICE_FROM_DATE", uF.getDateFormat(rs.getString("invoice_from_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmAdhocInner.put("INVOICE_TO_DATE", uF.getDateFormat(rs.getString("invoice_to_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmAdhocInner.put("INVOICE_CODE", rs.getString("invoice_code"));
//				hmAdhocInner.put("PROJECT_ID", rs.getString("pro_id"));
//				hmAdhocInner.put("PRO_FREQ_ID", uF.showData(rs.getString("pro_freq_id"), "0"));
//				hmAdhocInner.put("PROJECT_DESCRIPTION", rs.getString("project_description"));
//				hmAdhocInner.put("OTHER_DESCRIPTION", rs.getString("other_description"));
//				hmAdhocInner.put("SPOC_ID", rs.getString("spoc_id"));
//				hmAdhocInner.put("ADDRESS_ID", rs.getString("address_id"));
//				hmAdhocInner.put("PRO_OWNER_ID", rs.getString("pro_owner_id"));
//				hmAdhocInner.put("FINANCIAL_START_DATE", uF.getDateFormat(rs.getString("financial_start_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmAdhocInner.put("FINANCIAL_END_DATE", uF.getDateFormat(rs.getString("financial_end_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmAdhocInner.put("WLOCATION_ID", rs.getString("wlocation_id"));
//				hmAdhocInner.put("DEPART_ID", rs.getString("depart_id"));
//				hmAdhocInner.put("PARTICULARS_TOTAL_AMOUNT", rs.getString("oc_particulars_total_amount"));
//				hmAdhocInner.put("INVOICE_AMOUNT", rs.getString("oc_invoice_amount"));
//				hmAdhocInner.put("CURR_ID", rs.getString("curr_id"));
//				hmAdhocInner.put("IS_CANCEL", rs.getString("invoice_cancel"));
//				hmAdhocInner.put("BANK_BRANCH_ID", rs.getString("bank_branch_id"));
//				hmAdhocInner.put("INVOICE_TYPE", rs.getString("invoice_type"));
//				hmAdhocInner.put("CLIENT_ID", rs.getString("client_id"));
//				hmAdhocInner.put("SERVICE_ID", rs.getString("service_id"));
//				hmAdhocInner.put("CLIENT_NAME", uF.showData(CF.getClientNameById(con, rs.getString("client_id")), "N/A")+"<br/><i>["+uF.showData(hmOtherProectName.get(rs.getString("pro_id")), "Not Aligned")+"]</i>");
//				hmAdhocInner.put("CLIENT_ID", rs.getString("client_id"));
//				
//				hmCurr1 = hmCurrencyDetails.get(rs.getString("curr_id"));
//				
//				if(hmCurr1 == null) hmCurr1 = new HashMap();
//				
//				if(dblBalanceAmt < 0) {
//					dblBalanceAmt = 0;
//				}
//				double dblFinalInvoiceAmt = dblInvoiceAmt;
//				double dblFinalPaidAmt = dblPaidAmt;
//				double dblFinalBalanceAmt = dblBalanceAmt;
//				
//				alAdhocInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblFinalInvoiceAmt));
//				alAdhocInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblFinalPaidAmt));
//				alAdhocInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblFinalBalanceAmt));
//				
//				hmAdhocInner.put("BILLING_SUMMARY", alAdhocInner);
//				
//				alAdhocReport.add(hmAdhocInner);
//				
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("alAdhocReport", alAdhocReport);
//			
//			
//			Map<String, String> hmCurr = new HashMap<String, String>();
//			pst = con.prepareStatement("select invoice_generated_date, pro_id,invoice_code,promntc_invoice_id, oc_invoice_amount, curr_id,invoice_cancel,invoice_type," +
//				"adhoc_billing_type,invoice_template_id from promntc_invoice_details where is_cancel=false and invoice_type=? or invoice_type=?");
//			pst.setInt(1, ADHOC_INVOICE);
//			pst.setInt(2, ADHOC_PRORETA_INVOICE);
//			rs = pst.executeQuery();
////			System.out.println("pst======>" + pst);
////			Map<String, String> hmAdhocInvoice = new HashMap<String, String>();
//			Map<String, List<Map<String, String>>> hmAdhocInvDetails = new HashMap<String, List<Map<String, String>>>();
//			while(rs.next()) {
////				if(rs.getString("invoice_generated_date") != null) {
////					hmAdhocInvoice.put(rs.getString("invoice_code")+"_"+rs.getString("promntc_invoice_id"), uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
////				}
//				double dblBalance = uF.parseToDouble(rs.getString("oc_invoice_amount")) - uF.parseToDouble(hmInvoiceReceivedAmount.get(rs.getString("promntc_invoice_id")));
//				if(getReportType() != null && getReportType().equals("1") && dblBalance <= 0) {
//					continue;
//				} else if(getReportType() != null && getReportType().equals("4") && dblBalance > 0) {
//					continue;
//				}
//				
//				hmCurr = hmCurrencyDetails.get(rs.getString("curr_id"));
//				if(hmCurr == null) hmCurr = new HashMap();
//				List<Map<String, String>> outerList = hmAdhocInvDetails.get(rs.getString("promntc_invoice_id"));
//				if(outerList == null) outerList = new ArrayList<Map<String, String>>();
//				
//				Map<String, String> hmInnerMap = new HashMap<String, String>();
//				hmInnerMap.put("PROJECT_ID", rs.getString("pro_id"));
//				hmInnerMap.put("INVOICE_ID", rs.getString("promntc_invoice_id"));
//				hmInnerMap.put("INVOICE_CODE", rs.getString("invoice_code"));
//				hmInnerMap.put("INVOICE_TYPE", rs.getString("invoice_type"));
//				
//				double invoiceAmount = uF.parseToDouble(rs.getString("oc_invoice_amount"));
//				
//				hmInnerMap.put("INVOICE_AMOUNT",uF.showData(hmCurr.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(invoiceAmount));
//				hmInnerMap.put("INVOICE_AMOUNT_ONLY", rs.getString("oc_invoice_amount"));
//				
////				double dblTotBalance = dblBalance;
////				if(dblBalance>0 && !alWriteOff.contains(rs.getString("promntc_invoice_id"))) {
//				if(dblBalance > 0) {
//					hmInnerMap.put("BALANCE_AMOUNT", uF.showData(hmCurr.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblBalance));
//				}
//				hmInnerMap.put("INVOICE_GENERATED_DATE", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmInnerMap.put("INVOICE_IS_CANCEL", rs.getString("invoice_cancel"));
//				hmInnerMap.put("ADHOC_BILLING_TYPE", rs.getString("adhoc_billing_type"));
//				hmInnerMap.put("INVOICE_TEMPLATE_ID", rs.getString("invoice_template_id"));
//				outerList.add(hmInnerMap); 
//				hmAdhocInvDetails.put(rs.getString("promntc_invoice_id"), outerList);
//			}
//			rs.close();
//			pst.close();
//			
////			System.out.println("pst======>"+pst);
//			request.setAttribute("hmAdhocInvDetails", hmAdhocInvDetails);
////			request.setAttribute("hmAdhocInvoice", hmAdhocInvoice); 
//			
//			pst = con.prepareStatement(selectNotifications);
//			pst.setInt(1, N_PAYMENT_ALERT); 
//			rs = pst.executeQuery();
//			boolean isEmail = false;
//			while(rs.next()){
//				isEmail = uF.parseToBoolean(rs.getString("isemail"));
//			}
//			rs.close();
//			pst.close();
//			request.setAttribute("isEmail", isEmail);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	
	
	private String getManagerTimeSheetApprovalStatus(Connection con, String proId, String freqStDate, String freqEndDate) {
		 
		PreparedStatement pst = null; 
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		String pendingCnt = "0";
		try {
			
			StringBuilder sbTasks = null;
			pst = con.prepareStatement("select task_id from activity_info where pro_id = ?");				
			pst.setInt(1, uF.parseToInt(proId));
			rs = pst.executeQuery();			 
//		    System.out.println("pst ===> " + pst);
			while(rs.next()) {
				if(sbTasks == null) {
					sbTasks = new StringBuilder();
					sbTasks.append(rs.getString("task_id"));
				} else {
					sbTasks.append(","+rs.getString("task_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(sbTasks == null) {
				sbTasks = new StringBuilder();
			}
			
			if(sbTasks.toString().length() > 0) {
				boolean flag1 = false;
				pst = con.prepareStatement("select is_billable_approved from task_activity where is_billable_approved >= 1 and " +
						"activity_id in("+sbTasks.toString()+") and task_date between ? and ? group by is_billable_approved");
				pst.setDate(1, uF.getDateFormat(freqStDate, DBDATE));
				pst.setDate(2, uF.getDateFormat(freqEndDate, DBDATE));
				rs = pst.executeQuery();
//				System.out.println("pst ===> " + pst);
				while(rs.next()) {
					flag1 = true;
					pendingCnt = "1";
				}
				rs.close();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pendingCnt;
	}
	
	
	
	private String getCustomerTimeSheetApprovalStatus(Connection con, String proId, String freqStDate, String freqEndDate) {
		 
		PreparedStatement pst = null; 
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		String pendingCnt = "0";
		try {
			
			StringBuilder sbTasks = null;
			pst = con.prepareStatement("select task_id from activity_info where pro_id = ?");				
			pst.setInt(1, uF.parseToInt(proId));
			rs = pst.executeQuery();			 
//		    System.out.println("pst ===> " + pst);
			while(rs.next()) {
				if(sbTasks == null) {
					sbTasks = new StringBuilder();
					sbTasks.append(rs.getString("task_id"));
				} else {
					sbTasks.append(","+rs.getString("task_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(sbTasks == null) {
				sbTasks = new StringBuilder();
			}
			
			if(sbTasks.toString().length() > 0) {
				boolean flag1 = false;
				pst = con.prepareStatement("select is_billable_approved from task_activity where is_billable_approved = 2 and " +
						"activity_id in("+sbTasks.toString()+") and task_date between ? and ? group by is_billable_approved");
				pst.setDate(1, uF.getDateFormat(freqStDate, DBDATE));
				pst.setDate(2, uF.getDateFormat(freqEndDate, DBDATE));
				rs = pst.executeQuery();
//			    System.out.println("pst ===> " + pst);
				while(rs.next()) {
					flag1 = true;
					pendingCnt = "1";
				}
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
		
		return pendingCnt;
	}
	
	
//	public void getProjectBillingDetails() {
//		UtilityFunctions uF = new UtilityFunctions();
//		Database db = new Database();
//		db.setRequest(request);
//		Connection con=null;
//		PreparedStatement pst=null;
//		ResultSet rs=null;
//		
//		
//		try {
////			payCycleList = new FillPayCycles().fillPayCycles(CF);
//			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
//				organisationList = new FillOrganisation(request).fillOrganisation();
//				wLocationList = new FillWLocation(request).fillWLocation(getF_org());
//				
//			} else {
//				wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
//				organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
//			}
//			departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
//			clientList = new FillClients(request).fillClients(false);
//			
//			if(getF_strWLocation()==null) {
//				setF_strWLocation((String)session.getAttribute(WLOCATIONID));
//			}
//			
//			getSelectedFilter(uF);
//			
//			String strD1=null;
//			String strD2=null;
//			String strPC=null;
//			String[] strPayCycleDates = null;
//			if (getPaycycle() != null) {
//				strPayCycleDates = getPaycycle().split("-");
//				strD1 = strPayCycleDates[0];
//				strD2 = strPayCycleDates[1];
//				strPC = strPayCycleDates[2];
//			} else {
//				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF ,request);
//				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
//				strD1 = strPayCycleDates[0];
//				strD2 = strPayCycleDates[1];
//				strPC = strPayCycleDates[2];
//			}
//			
//			con = db.makeConnection(con);
//			
//			
//			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
//			
//			Map<String, String> hmPaidAmount = new HashMap<String, String>();
//			Map<String, String> hmProInvoiceAmount = new HashMap<String, String>();
//			Map<String, String> hmInvoiceAmount = new HashMap<String, String>();
//			List<String> alWriteOff = new ArrayList<String>(); 
//			/*pst = con.prepareStatement("select sum(received_amount) as received_amount, sum(tds_deducted) as tds_deducted, pro_id from promntc_bill_amt_details group by pro_id");
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				hmPaidAmount.put(rs.getString("pro_id"), (uF.parseToDouble(rs.getString("received_amount")) + uF.parseToDouble(rs.getString("tds_deducted")))+"");
//			}*/
//			pst = con.prepareStatement("select sum(invoice_amount) as invoice_amount, pro_id from promntc_invoice_details where is_cancel=false and invoice_cancel=false and (invoice_type!=? or invoice_type!=?) group by pro_id");
//			pst.setInt(1, ADHOC_INVOICE);
//			pst.setInt(2, ADHOC_PRORETA_INVOICE);
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				hmProInvoiceAmount.put(rs.getString("pro_id"), rs.getString("invoice_amount"));
//			}
//			rs.close();
//			pst.close();
//			
//			//pst = con.prepareStatement("select received_amount,tds_deducted, invoice_id, is_write_off, exchange_rate,pro_id  from promntc_bill_amt_details order by invoice_id, is_write_off desc");
////			pst = con.prepareStatement("select received_amount,tds_deducted, invoice_id, is_write_off, exchange_rate,pro_id from promntc_bill_amt_details where invoice_id in(select promntc_invoice_id from promntc_invoice_details where invoice_type!=?) order by invoice_id, is_write_off desc");
////			pst.setInt(1,ADHOC_INVOICE);
//			pst = con.prepareStatement("select received_amount,tds_deducted, invoice_id, is_write_off, exchange_rate,pro_id from promntc_bill_amt_details where invoice_id in(select promntc_invoice_id from promntc_invoice_details where is_cancel=false and invoice_cancel=false and (invoice_type!=? or invoice_type!=?)) order by invoice_id, is_write_off desc");
//			pst.setInt(1, ADHOC_INVOICE);
//			pst.setInt(2, ADHOC_PRORETA_INVOICE);
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				double dblReceivedAmt = uF.parseToDouble(rs.getString("received_amount"));
//				double dblTDSAmt = uF.parseToDouble(rs.getString("tds_deducted"));
//				double dblExchageRate = uF.parseToDouble(rs.getString("exchange_rate"));
//				
//				double dblTotalAmountP = (dblReceivedAmt/dblExchageRate) + (dblTDSAmt/dblExchageRate) ;
//				dblTotalAmountP += uF.parseToDouble(hmPaidAmount.get(rs.getString("pro_id")));
//				hmPaidAmount.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(dblTotalAmountP));
//				
//				double dblTotalAmount = (dblReceivedAmt/dblExchageRate) + (dblTDSAmt/dblExchageRate) ;
//				dblTotalAmount += uF.parseToDouble(hmInvoiceAmount.get(rs.getString("invoice_id")));
//				hmInvoiceAmount.put(rs.getString("invoice_id"), uF.formatIntoTwoDecimal(dblTotalAmount));
//				if(uF.parseToBoolean(rs.getString("is_write_off"))) {
//					alWriteOff.add(rs.getString("invoice_id"));
//				}
//				
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("hmInvoiceAmount", hmInvoiceAmount);
//			
//			pst = con.prepareStatement("select sum(particulars_total_amount) as particulars_total_amount, pro_id,invoice_code " +
//					"from promntc_invoice_details pid,promntc_invoice_amt_details piad where pid.promntc_invoice_id=piad.promntc_invoice_id  " +
//					" group by pro_id,invoice_code");
//			rs = pst.executeQuery();
//			Map<String, String> hmInvoiceDetails = new HashMap<String, String>(); 
//			while(rs.next()) {
//				hmInvoiceDetails.put(rs.getString("pro_id")+"_"+rs.getString("invoice_code"), rs.getString("particulars_total_amount"));
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select invoice_code, pro_id, promntc_invoice_id from promntc_invoice_details ");
//			rs = pst.executeQuery();
//			Map<String, String> hmInvoiceCode = new HashMap<String, String>(); 
//			while(rs.next()) {
//				hmInvoiceCode.put(rs.getString("pro_id")+"_"+rs.getString("promntc_invoice_id"), rs.getString("invoice_code"));
//			}
//			rs.close();
//			pst.close();
//			
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from projectmntnc pmc, client_poc cpoc, client_details cd where pmc.client_id = cd.client_id and pmc.poc=cpoc.poc_id");
//			pst = con.prepareStatement(sbQuery.toString());
//			rs = pst.executeQuery();
//			Map<String, String> hmClientDetails = new HashMap<String, String>();
//			while(rs.next()) {
//				hmClientDetails.put(rs.getString("pro_id"), rs.getString("client_name"));
//			}
//			rs.close();
//			pst.close();
//			
//			
//			//pst = con.prepareStatement("select invoice_generated_date, pro_id,invoice_code,promntc_invoice_id from promntc_invoice_details group by pro_id, invoice_generated_date,invoice_code,promntc_invoice_id");
//			pst = con.prepareStatement("select invoice_generated_date, pro_id,invoice_code,promntc_invoice_id, invoice_amount, curr_id,invoice_cancel," +
//				"oc_invoice_amount from promntc_invoice_details where is_cancel=false and invoice_type!=? and invoice_type!=?");
//			pst.setInt(1, ADHOC_INVOICE);
//			pst.setInt(2, ADHOC_PRORETA_INVOICE);
//			rs = pst.executeQuery();
//
//			Map<String, String> hmInvoice = new HashMap<String, String>();
//			Map<String, List<Map<String,String>>> hmInvDetails = new HashMap<String, List<Map<String,String>>>();
//			Map<String, String> hmCurr1 = new HashMap<String, String>();
//			while(rs.next()) {
//				if(rs.getString("invoice_generated_date")!=null) {
//					hmInvoice.put(rs.getString("pro_id")+"_"+rs.getString("promntc_invoice_id"), uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
//				}
//				hmCurr1 = hmCurrencyDetails.get(rs.getString("curr_id"));
//				
//				if(hmCurr1 == null) hmCurr1 = new HashMap();
//				List<Map<String,String>> outerList = hmInvDetails.get(rs.getString("pro_id"));
//				if(outerList == null) outerList = new ArrayList<Map<String,String>>();
//				
//				Map<String,String> hmInnerMap=new HashMap<String, String>();
//				hmInnerMap.put("PROJECT_ID", rs.getString("pro_id"));
//				hmInnerMap.put("INVOICE_ID", rs.getString("promntc_invoice_id"));
//				hmInnerMap.put("INVOICE_CODE", rs.getString("invoice_code"));
//				
////				double currINRValue = 1;
////				if(uF.parseToDouble(hmCurr1.get("CURR_INR_VALUE")) > 0) {
////					currINRValue = uF.parseToDouble(hmCurr1.get("CURR_INR_VALUE"));
////				}
//				double invoiceAmount = uF.parseToDouble(rs.getString("oc_invoice_amount"));
//				
//				hmInnerMap.put("INVOICE_AMOUNT",uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(invoiceAmount));
//				double dblBalance = uF.parseToDouble(rs.getString("oc_invoice_amount")) - uF.parseToDouble(hmInvoiceAmount.get(rs.getString("promntc_invoice_id")));
//				
//				double dblTotBalance = dblBalance;
//				
//				if(dblBalance>0 && !alWriteOff.contains(rs.getString("promntc_invoice_id"))) {
//					hmInnerMap.put("BALANCE_AMOUNT", uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoTwoDecimal(dblTotBalance));					
//				}
//				hmInnerMap.put("INVOICE_GENERATED_DATE", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmInnerMap.put("INVOICE_IS_CANCEL", rs.getString("invoice_cancel"));
//				outerList.add(hmInnerMap); 
//				hmInvDetails.put(rs.getString("pro_id"), outerList);
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("hmInvDetails",hmInvDetails);
//			request.setAttribute("hmInvoice",hmInvoice);
//			
//			
//			pst = con.prepareStatement("select pro_id,invoice_id,invoice_amount from promntc_bill_amt_details");
//			rs = pst.executeQuery();
//			Map<String, String> hmReceiveAmt = new HashMap<String, String>();
//			Map<String, String> hmClearedAmt = new HashMap<String, String>();
//			while(rs.next()) {
//				hmReceiveAmt.put(rs.getString("pro_id"),rs.getString("pro_id"));
//				
//				double dblBalance = uF.parseToDouble(rs.getString("invoice_amount")) - uF.parseToDouble(hmInvoiceAmount.get(rs.getString("invoice_id")));
//				
//				if(dblBalance==0.0 || alWriteOff.contains(rs.getString("invoice_id"))) {
//					hmClearedAmt.put(rs.getString("pro_id"),rs.getString("pro_id"));					
//				}
//			}
//			rs.close();
//			pst.close();
////			System.out.println("hmClearedAmt======>"+hmClearedAmt);
//			
//			sbQuery = new StringBuilder();
////			sbQuery.append("select *, pc.billable_amount as contract_amount from project_cost pc, (select *, pmc.pro_id as proj_id from projectmntnc pmc left join projectmntnc_billing pmcb on pmc.pro_id = pmcb.pro_id order by pmc.pro_id) a where pc.pro_id = a.proj_id");
//			sbQuery.append("select * from projectmntnc where pro_id>0 ");
////			if(getF_start()!=null && getF_end()!=null && !getF_start().equalsIgnoreCase(LABEL_FROM_DATE) && !getF_end().equalsIgnoreCase(LABEL_TO_DATE)) {
////				sbQuery.append(" and start_date between '"+uF.getDateFormat(getF_start(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getF_end(), DATE_FORMAT, DBDATE)+"' ");
////			}
//			if(uF.parseToInt(getF_org())>0) {
//				sbQuery.append(" and org_id ="+uF.parseToInt(getF_org()));
//			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
//				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//			}
//			if(uF.parseToInt(getF_strWLocation())>0) {
//				sbQuery.append(" and wlocation_id ="+uF.parseToInt(getF_strWLocation()));
//	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//			}
//			
//			if(uF.parseToInt(getF_department())>0) {
//				sbQuery.append(" and department_id ="+uF.parseToInt(getF_department()));
//			}
//			
//			if(getF_client() != null && getF_client().length>0) {
//				sbQuery.append(" and client_id in ("+StringUtils.join(getF_client(), ",")+") ");
//			}
//			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
//				sbQuery.append(" and added_by = "+ uF.parseToInt(strSessionEmpId) +" ");
//			}
//			
//			pst = con.prepareStatement(sbQuery.toString());
//			rs = pst.executeQuery();
//			
//			List alReport = new ArrayList();
//			   
//			Map hmInner = new HashMap();
//			List<String> alInner = new ArrayList<String>();
//			
//			String strProjectIdNew = null;
//			String strProjectIdOld = null;
//			Map<String, Map<String, String>> hmProInvoiceAmt=new HashMap<String, Map<String, String>>();
//			while(rs.next()) {
//				strProjectIdNew = rs.getString("pro_id");
//				
//				hmCurr1 = hmCurrencyDetails.get(rs.getString("billing_curr_id"));
//				
//				if(hmCurr1 == null) hmCurr1 = new HashMap();
//				
//				if(getReportType() != null && getReportType().equals("1") && hmInvDetails.get(rs.getString("pro_id")) != null) {
//					continue;
//				} else if(getReportType() != null && getReportType().equals("2") && hmReceiveAmt.get(rs.getString("pro_id")) != null) {
//					continue;
//				} else if(getReportType() != null && getReportType().equals("4") && hmClearedAmt.get(rs.getString("pro_id")) == null) {
//					continue;
//				}
//				
//				
//				
//				if(strProjectIdNew!=null && !strProjectIdNew.equalsIgnoreCase(strProjectIdOld)) {
//					hmInner = new HashMap<String, String>();
//					alInner = new ArrayList<String>();
//
//				}
//				
//				hmInner.put("PROJECT_ID", strProjectIdNew);
//				hmInner.put("CLIENT_ID", rs.getString("client_id"));
//				hmInner.put("CLIENT_NAME", uF.showData((String)hmClientDetails.get(rs.getString("pro_id")), "N/A") +"<br/><i>["+uF.showData(rs.getString("pro_name"), "")+"]</i>");
//				hmInner.put("PROJECT_INVOICE_FORMAT_ID", rs.getString("invoice_template_type"));
//				
//				if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("D")) {
//					hmInner.put("BILLING_TYPE", "Daily");
//				} else if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("H")) {
//					hmInner.put("BILLING_TYPE", "Hourly");
//				} else if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("M")) {
//					hmInner.put("BILLING_TYPE", "Monthly");
//				} else if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("F")) {
//					hmInner.put("BILLING_TYPE", "Fixed");
//				}
//				
//				
//				if(rs.getString("billing_kind")!=null && rs.getString("billing_kind").equalsIgnoreCase("W")) {
//					hmInner.put("BILLING_KIND", "Weekly");
//				} else if(rs.getString("billing_kind")!=null && rs.getString("billing_kind").equalsIgnoreCase("B")) {
//					hmInner.put("BILLING_KIND", "BiWeekly");
//				} else if(rs.getString("billing_kind")!=null && rs.getString("billing_kind").equalsIgnoreCase("M")) {
//					hmInner.put("BILLING_KIND", "Monthly");
//				} else if(rs.getString("billing_kind")!=null && rs.getString("billing_kind").equalsIgnoreCase("O")) {
//					hmInner.put("BILLING_KIND", "One time/Fixed");
//				}
//				
//				hmInner.put("PAYMENT_TYPE", "");
//				hmInner.put("ADDED_BY", rs.getString("added_by"));
//				hmInner.put("ISMONTHLY", rs.getString("ismonthly"));
////				hmInner.put("INVOICE_NO", rs.getString("invoice_number"));
////				hmInner.put("INVOICE_NO", uF.showData(hmInvoiceCode.get(strProjectIdNew), "N/a"));
////				hmInner.put("INVOICE_ID", uF.showData(hmInvoiceCode.get(strProjectIdNew), "N/a"));
////				hmInner.put("INVOICE_VALID", rs.getString("is_invoice_validated"));
//				
//				hmInner.put("PAYCYCLE", getPaycycle());
//				
//				/*if(hmInvoiceDetails.get(strProjectIdNew)!=null) {
//					hmInner.put("INVOICE_VALID", "true");
//				}*/
//				
//				if(rs.getString("approve_status")!=null && rs.getString("approve_status").equalsIgnoreCase("n")) {
//					hmInner.put("COMPLETED", "<img src=\"images1/icons/exclamation_mark_icon.png\" width=\"30\">");
//				} else {
//					hmInner.put("COMPLETED", "<img src=\"images1/icons/hd_tick_20x20.png\">");
//				}
//				
////				alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("contract_amount"))));
////				alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("paid_amount"))));
////				alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("contract_amount")) - uF.parseToDouble(rs.getString("paid_amount"))));
//				
//				
//				double dblInvoiceAmt = uF.parseToDouble(hmProInvoiceAmount.get(strProjectIdNew));
//				double dblPaidAmt = uF.parseToDouble(hmPaidAmount.get(strProjectIdNew));
//				double dblProjectAmt = uF.parseToDouble(rs.getString("billing_amount"));
//				double dblPendingAmt = dblProjectAmt - dblPaidAmt;
//				double dblBalanceAmt = dblInvoiceAmt - dblPaidAmt;
//				
////				double currINRValue = 1;
//				
////				if(uF.parseToDouble(hmCurr1.get("CURR_INR_VALUE")) > 0) {
////					currINRValue = uF.parseToDouble(hmCurr1.get("CURR_INR_VALUE"));
////				}
//				if(dblPendingAmt < 0) {
//					dblPendingAmt = 0;
//				}
//				
//				if(dblBalanceAmt < 0) {
//					dblBalanceAmt = 0;
//				}
//				
//				double dblFinalInvoiceAmt = dblInvoiceAmt;
//				double dblFinalPaidAmt = dblPaidAmt;
//				double dblFinalProjectAmt = dblProjectAmt;
//				double dblFinalPendingAmt = dblPendingAmt;
//				double dblFinalBalanceAmt = dblBalanceAmt;
//				
////				if(hmCurr == null) hmCurr = new HashMap();
//				alInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoOneDecimal(dblFinalProjectAmt));
//				alInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoOneDecimal(dblFinalPendingAmt));
//				alInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoOneDecimal(dblFinalInvoiceAmt));
//				alInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoOneDecimal(dblFinalPaidAmt));
//				alInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoOneDecimal(dblFinalBalanceAmt));
//				
//				
//				hmInner.put("BILLING_SUMMARY", alInner);
//				
//				Map<String, String> hmProInvAmt=new HashMap<String, String>();
//				hmProInvAmt.put("PRO_AMT",uF.formatIntoOneDecimal(dblProjectAmt));
//				hmProInvAmt.put("INVOICE_AMT",uF.formatIntoOneDecimal(dblInvoiceAmt));
//				hmProInvoiceAmt.put(strProjectIdNew, hmProInvAmt);
//				
//				alReport.add(hmInner);
//				
//				
//				strProjectIdOld = strProjectIdNew;
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("alReport", alReport);
//			request.setAttribute("hmProInvoiceAmt", hmProInvoiceAmt);
//			
//			Map hmEmpWlocation = CF.getEmpWlocationMap(con);
//			Map hmWorkLocation = CF.getWorkLocationMap(con);
//			
//			String strWLocation = (String)hmEmpWlocation.get((String)session.getAttribute(EMPID));
//			Map hmWlocation = (Map)hmWorkLocation.get(strWLocation);
//			if(hmWlocation==null)hmWlocation=new HashMap();		  
//			  
//			request.setAttribute("hmWlocation",hmWlocation);
//			
//			
//			//get adhoc data
//			
//			Map<String, String> hmPaidAdhocAmount = new HashMap<String, String>();
//			Map<String, String> hmProAdhocInvoiceAmount = new HashMap<String, String>();
//			Map<String, String> hmAdhocInvoiceAmount = new HashMap<String, String>();
//			
//			pst = con.prepareStatement("select * from client_details");
//			rs = pst.executeQuery();
//			Map<String, String> hmAdhocClientDetails = new HashMap<String, String>();
//			while(rs.next()) {
//				hmAdhocClientDetails.put(rs.getString("client_id"), rs.getString("client_name"));
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select sum(oc_invoice_amount) as invoice_amount, promntc_invoice_id from promntc_invoice_details where is_cancel=false and invoice_cancel=false and (invoice_type=? or invoice_type=?) group by promntc_invoice_id");
//			pst.setInt(1, ADHOC_INVOICE);
//			pst.setInt(2, ADHOC_PRORETA_INVOICE);
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				hmProAdhocInvoiceAmount.put(rs.getString("promntc_invoice_id"), rs.getString("invoice_amount"));
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select received_amount,tds_deducted, invoice_id, is_write_off, exchange_rate,pro_id from promntc_bill_amt_details where invoice_id in(select promntc_invoice_id from promntc_invoice_details where is_cancel=false and invoice_cancel=false and (invoice_type=? or invoice_type=?)) order by invoice_id, is_write_off desc");
//			pst.setInt(1, ADHOC_INVOICE);
//			pst.setInt(2, ADHOC_PRORETA_INVOICE);
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				double dblReceivedAmt = uF.parseToDouble(rs.getString("received_amount"));
//				double dblTDSAmt = uF.parseToDouble(rs.getString("tds_deducted"));
//				double dblExchageRate = uF.parseToDouble(rs.getString("exchange_rate"));
//				
//				double dblTotalAmountP = (dblReceivedAmt/dblExchageRate) + (dblTDSAmt/dblExchageRate) ;
//				dblTotalAmountP += uF.parseToDouble(hmPaidAdhocAmount.get(rs.getString("invoice_id")));
//				hmPaidAdhocAmount.put(rs.getString("invoice_id"), uF.formatIntoTwoDecimal(dblTotalAmountP));
//				
//				double dblTotalAmount = (dblReceivedAmt/dblExchageRate) + (dblTDSAmt/dblExchageRate) ;
//				dblTotalAmount += uF.parseToDouble(hmAdhocInvoiceAmount.get(rs.getString("invoice_id")));
//				hmAdhocInvoiceAmount.put(rs.getString("invoice_id"), uF.formatIntoTwoDecimal(dblTotalAmount));
//				if(uF.parseToBoolean(rs.getString("is_write_off"))) {
//					alWriteOff.add(rs.getString("invoice_id"));
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			Map<String, String> hmOtherProectName = new HashMap<String, String>();
//			pst = con.prepareStatement("select pro_id, pro_name from projectmntnc");
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				hmOtherProectName.put(rs.getString("pro_id"), rs.getString("pro_name"));
//			}
//			rs.close();
//			pst.close();
//			
//			
//			sbQuery = new StringBuilder();
//			sbQuery.append("select * from promntc_invoice_details where is_cancel=false and (invoice_type=? or invoice_type=?) ");		
//			if(getF_client() != null && getF_client().length>0) {
//				sbQuery.append(" and client_id in ("+StringUtils.join(getF_client(), ",")+") ");
//			}
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, ADHOC_INVOICE);
//			pst.setInt(2, ADHOC_PRORETA_INVOICE);
//			rs = pst.executeQuery();			
//			List alAdhocReport = new ArrayList();			   
//			while(rs.next()) {
//				Map hmAdhocInner = new HashMap();
//				List alAdhocInner = new ArrayList();
//				
//				hmAdhocInner.put("INVOICE_ID", rs.getString("promntc_invoice_id"));
//				hmAdhocInner.put("INVOICE_GENERATED_DATE", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmAdhocInner.put("INVOICE_GENERATED_BY", rs.getString("invoice_generated_by"));
//				hmAdhocInner.put("INVOICE_FROM_DATE", uF.getDateFormat(rs.getString("invoice_from_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmAdhocInner.put("INVOICE_TO_DATE", uF.getDateFormat(rs.getString("invoice_to_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmAdhocInner.put("INVOICE_CODE", rs.getString("invoice_code"));
//				hmAdhocInner.put("PROJECT_ID", rs.getString("pro_id"));
//				hmAdhocInner.put("PROJECT_DESCRIPTION", rs.getString("project_description"));
//				hmAdhocInner.put("OTHER_DESCRIPTION", rs.getString("other_description"));
//				hmAdhocInner.put("SPOC_ID", rs.getString("spoc_id"));
//				hmAdhocInner.put("ADDRESS_ID", rs.getString("address_id"));
//				hmAdhocInner.put("PRO_OWNER_ID", rs.getString("pro_owner_id"));
//				hmAdhocInner.put("FINANCIAL_START_DATE", uF.getDateFormat(rs.getString("financial_start_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmAdhocInner.put("FINANCIAL_END_DATE", uF.getDateFormat(rs.getString("financial_end_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmAdhocInner.put("WLOCATION_ID", rs.getString("wlocation_id"));
//				hmAdhocInner.put("DEPART_ID", rs.getString("depart_id"));
//				hmAdhocInner.put("PARTICULARS_TOTAL_AMOUNT", rs.getString("oc_particulars_total_amount"));
//				hmAdhocInner.put("INVOICE_AMOUNT", rs.getString("oc_invoice_amount"));
//				hmAdhocInner.put("CURR_ID", rs.getString("curr_id"));
//				hmAdhocInner.put("IS_CANCEL", rs.getString("invoice_cancel"));
//				hmAdhocInner.put("BANK_BRANCH_ID", rs.getString("bank_branch_id"));
//				hmAdhocInner.put("INVOICE_TYPE", rs.getString("invoice_type"));
//				hmAdhocInner.put("CLIENT_ID", rs.getString("client_id"));
//				hmAdhocInner.put("SERVICE_ID", rs.getString("service_id"));
//				hmAdhocInner.put("CLIENT_NAME", uF.showData((String)hmAdhocClientDetails.get(rs.getString("client_id")), "N/A")+"<br/><i>["+uF.showData(hmOtherProectName.get(rs.getString("pro_id")), "Not Aligned")+"]</i>");
//				hmAdhocInner.put("CLIENT_ID", rs.getString("client_id"));
//				
//				hmCurr1 = hmCurrencyDetails.get(rs.getString("curr_id"));
//				
//				if(hmCurr1 == null) hmCurr1 = new HashMap();
//				
//				double dblInvoiceAmt = uF.parseToDouble(hmProAdhocInvoiceAmount.get(rs.getString("promntc_invoice_id")));
//				double dblPaidAmt = uF.parseToDouble(hmPaidAdhocAmount.get(rs.getString("promntc_invoice_id")));
////				double dblProjectAmt = uF.parseToDouble(rs.getString("billing_amount"));
//				//double dblPendingAmt = dblProjectAmt - dblPaidAmt;
//				double dblBalanceAmt = dblInvoiceAmt - dblPaidAmt;
//				
////				double currINRValue = 1;
////				
////				if(uF.parseToDouble(hmCurr1.get("CURR_INR_VALUE")) > 0) {
////					currINRValue = uF.parseToDouble(hmCurr1.get("CURR_INR_VALUE"));
////				}
//				if(dblBalanceAmt < 0) {
//					dblBalanceAmt = 0;
//				}
//				double dblFinalInvoiceAmt = dblInvoiceAmt;
//				double dblFinalPaidAmt = dblPaidAmt;
//				double dblFinalBalanceAmt = dblBalanceAmt;
//				
////				if(hmCurr == null)hmCurr = new HashMap();
////				alInner.add(uF.showData(hmCurr.get("SHORT_CURR"),"")+" "+uF.formatIntoOneDecimal(dblProjectAmt));
////				alInner.add(uF.showData(hmCurr.get("SHORT_CURR"),"")+" "+uF.formatIntoOneDecimal(dblPendingAmt));
//				alAdhocInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoOneDecimal(dblFinalInvoiceAmt));
//				alAdhocInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoOneDecimal(dblFinalPaidAmt));
//				alAdhocInner.add(uF.showData(hmCurr1.get("SHORT_CURR"),"")+" "+uF.formatIntoOneDecimal(dblFinalBalanceAmt));
//				
//				hmAdhocInner.put("BILLING_SUMMARY", alAdhocInner);
//				
//				alAdhocReport.add(hmAdhocInner);
//				
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("alAdhocReport", alAdhocReport);
//			
//			Map<String, String> hmCurr = new HashMap<String, String>();
//			pst = con.prepareStatement("select invoice_generated_date, pro_id,invoice_code,promntc_invoice_id, oc_invoice_amount, curr_id,invoice_cancel,invoice_type," +
//				"adhoc_billing_type from promntc_invoice_details where is_cancel=false and invoice_type=? or invoice_type=?");
//			pst.setInt(1, ADHOC_INVOICE);
//			pst.setInt(2, ADHOC_PRORETA_INVOICE);
//			rs = pst.executeQuery();
////			System.out.println("pst======>" + pst);
//			Map<String, String> hmAdhocInvoice = new HashMap<String, String>();
//			Map<String, List<Map<String,String>>> hmAdhocInvDetails = new HashMap<String, List<Map<String,String>>>();
//			while(rs.next()) {
//				if(rs.getString("invoice_generated_date") != null) {
//					hmAdhocInvoice.put(rs.getString("invoice_code")+"_"+rs.getString("promntc_invoice_id"), uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
//				}
//				hmCurr = hmCurrencyDetails.get(rs.getString("curr_id"));
//				if(hmCurr==null)hmCurr=new HashMap();
//				List<Map<String,String>> outerList=hmAdhocInvDetails.get(rs.getString("promntc_invoice_id"));
//				if(outerList==null) outerList=new ArrayList<Map<String,String>>();
//				
//				Map<String,String> hmInnerMap=new HashMap<String, String>();
//				hmInnerMap.put("PROJECT_ID", rs.getString("pro_id"));
//				hmInnerMap.put("INVOICE_ID", rs.getString("promntc_invoice_id"));
//				hmInnerMap.put("INVOICE_CODE", rs.getString("invoice_code"));
//				hmInnerMap.put("INVOICE_TYPE", rs.getString("invoice_type"));
//				
////				System.out.println("promntc_invoice_id ===>>> " + rs.getString("promntc_invoice_id"));
////				System.out.println("pro_id ===>>> " + rs.getString("pro_id"));
////				System.out.println("hmCurr ===>>> " + hmCurr);
//				
//				/*double currINRValue = 1;
//				if(uF.parseToDouble(hmCurr.get("CURR_INR_VALUE")) > 0) {
//					currINRValue = uF.parseToDouble(hmCurr.get("CURR_INR_VALUE"));
//				}*/
//				double invoiceAmount = uF.parseToDouble(rs.getString("oc_invoice_amount"));
//				
//				hmInnerMap.put("INVOICE_AMOUNT",uF.showData(hmCurr.get("SHORT_CURR"),"")+" "+uF.formatIntoOneDecimal(invoiceAmount));
//				
//				double dblBalance = uF.parseToDouble(rs.getString("oc_invoice_amount")) - uF.parseToDouble(hmInvoiceAmount.get(rs.getString("promntc_invoice_id")));
//				
//				double dblTotBalance = dblBalance;
//				
//				if(dblBalance>0 && !alWriteOff.contains(rs.getString("promntc_invoice_id"))) {
//					hmInnerMap.put("BALANCE_AMOUNT", uF.showData(hmCurr.get("SHORT_CURR"),"")+" "+uF.formatIntoOneDecimal(dblTotBalance));
//				}
//				hmInnerMap.put("INVOICE_GENERATED_DATE", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmInnerMap.put("INVOICE_IS_CANCEL", rs.getString("invoice_cancel"));
//				hmInnerMap.put("ADHOC_BILLING_TYPE", rs.getString("adhoc_billing_type"));
//				outerList.add(hmInnerMap); 
//				hmAdhocInvDetails.put(rs.getString("promntc_invoice_id"), outerList);
//			}
//			rs.close();
//			pst.close();
//			
////			System.out.println("pst======>"+pst);
//			request.setAttribute("hmAdhocInvDetails", hmAdhocInvDetails);
//			request.setAttribute("hmAdhocInvoice", hmAdhocInvoice);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	
	private String getMilestoneTaskORPercent(Connection con, UtilityFunctions uF, String milestoneId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		String taskOrPercent = null;
		try {
			
			String proTaskId = "";
			pst = con.prepareStatement("select pro_task_id, pro_completion_percent from project_milestone_details where project_milestone_id=?");
			pst.setInt(1, uF.parseToInt(milestoneId));
			rs = pst.executeQuery();
			while(rs.next()) {
				proTaskId = rs.getString("pro_task_id");
				taskOrPercent = rs.getString("pro_completion_percent") +" %";
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(proTaskId) > 0) {
				pst = con.prepareStatement("select activity_name from activity_info where task_id=?");
				pst.setInt(1, uF.parseToInt(proTaskId));
				rs = pst.executeQuery();
				while(rs.next()) {
					taskOrPercent = rs.getString("activity_name");
				}
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
		return taskOrPercent;
	}

	

	private boolean checkMilestoneStatus(Connection con, UtilityFunctions uF, String milestoneId, String proId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean flag = false;
		try {
			
			String proTaskId = "";
			String proCompletionPercent = "";
			pst = con.prepareStatement("select pro_task_id, pro_completion_percent from project_milestone_details where project_milestone_id=?");
			pst.setInt(1, uF.parseToInt(milestoneId));
			rs = pst.executeQuery();
			while(rs.next()) {
				proTaskId = rs.getString("pro_task_id");
				proCompletionPercent = rs.getString("pro_completion_percent");
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(proTaskId) > 0) {
				pst = con.prepareStatement("select task_id from activity_info where task_id=? and approve_status = 'approved' and completed = 100");
				pst.setInt(1, uF.parseToInt(proTaskId));
				rs = pst.executeQuery();
				while(rs.next()) {
					flag = true;
				}
				rs.close();
				pst.close();
			} else {
				pst = con.prepareStatement("select sum(completed) as completed, count(task_id) as taskCnt from activity_info where pro_id=? and parent_task_id = 0");
				pst.setInt(1, uF.parseToInt(proId));
				rs = pst.executeQuery();
				
//				System.out.println("pst ===>> " + pst);
				
				while(rs.next()) {
					String strComplete = rs.getString("completed");
					String strTaskCnt = rs.getString("taskCnt");
					double avgPercent = uF.parseToDouble(strComplete) / uF.parseToDouble(strTaskCnt);
					if(avgPercent >= uF.parseToDouble(proCompletionPercent)) {
						flag = true;
					}
				}
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
		return flag;
	}


	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if(getF_org()!=null) {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++) {
				if(getF_org().equals(organisationList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=organisationList.get(i).getOrgName();
					} else {
						strOrg+=", "+organisationList.get(i).getOrgName();
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
		
		
		if(strUserType != null && !strUserType.equals(CUSTOMER)) {
			alFilter.add("LOCATION");
			if(getF_strWLocation()!=null) {
				String strLocation="";
				int k=0;
				for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
					if(getF_strWLocation().equals(wLocationList.get(i).getwLocationId())) {
						if(k==0) {
							strLocation=wLocationList.get(i).getwLocationName();
						} else {
							strLocation+=", "+wLocationList.get(i).getwLocationName();
						}
						k++;
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
					if(getF_department().equals(departmentList.get(i).getDeptId())) {
						if(k==0) {
							strDepartment=departmentList.get(i).getDeptName();
						} else {
							strDepartment+=", "+departmentList.get(i).getDeptName();
						}
						k++;
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
		}
		
		if(strUserType != null && strUserType.equals(CUSTOMER)) {
			alFilter.add("BILL_TYPE");
			if(getBillingType()!=null) {
				String strBillType="";
				int k=0;
				for(int i=0;billingTypeList!=null && i<billingTypeList.size();i++) {
					if(getBillingType().equals(billingTypeList.get(i).getBillingId())) {
						if(k==0) {
							strBillType=billingTypeList.get(i).getBillingName();
						} else {
							strBillType+=", "+billingTypeList.get(i).getBillingName();
						}
						k++;
					}
				}
				if(strBillType!=null && !strBillType.equals("")) {
					hmFilter.put("BILL_TYPE", strBillType);
				} else {
					hmFilter.put("BILL_TYPE", "All Billing Type");
				}
			} else {
				hmFilter.put("BILL_TYPE", "All Billing Type");
			}
			
			
			alFilter.add("BILL_FREQUENCY");
			if(getStrBillingFreq()!=null) {
				String strBillFreq="";
				int k=0;
				for(int i=0;billingFreqList!=null && i<billingFreqList.size();i++) {
					if(getStrBillingFreq().equals(billingFreqList.get(i).getBillingId())) {
						if(k==0) {
							strBillFreq=billingFreqList.get(i).getBillingName();
						} else {
							strBillFreq+=", "+billingFreqList.get(i).getBillingName();
						}
						k++;
					}
				}
				if(strBillFreq!=null && !strBillFreq.equals("")) {
					hmFilter.put("BILL_FREQUENCY", strBillFreq);
				} else {
					hmFilter.put("BILL_FREQUENCY", "All Billing Frequency");
				}
			} else {
				hmFilter.put("BILL_FREQUENCY", "All Billing Frequency");
			}
		}
		
		
		if(proType == null || proType.equals("") || proType.equalsIgnoreCase("PPB") || proType.equalsIgnoreCase("PAB")) {
			alFilter.add("PROJECT_STATUS");
			if(getReportType()!=null) {
				String strProjectStatus="";
				if(getReportType().equals("1")) {
					strProjectStatus="Pending";
				} else if(getReportType().equals("2")) {
					strProjectStatus="Processing";
				} else if(getReportType().equals("3")) {
					strProjectStatus="Partial";
				} else if(getReportType().equals("4")) {
					strProjectStatus="Cleared";
				}
				
				if(strProjectStatus!=null && !strProjectStatus.equals("")) {
					hmFilter.put("PROJECT_STATUS", strProjectStatus);
				} else {
					hmFilter.put("PROJECT_STATUS", "All");
				}
			} else {
				hmFilter.put("PROJECT_STATUS", "All");
			}
		}
		
		
		if(strUserType != null && !strUserType.equals(CUSTOMER)) {
			alFilter.add("CLIENT");
			if(getClient()!=null) {
				String strClient="";
				int k=0;
				for(int i=0; clientList!=null && i<clientList.size();i++) {
					for(int j=0;j<getClient().length;j++) {
						if(getClient()[j].equals(clientList.get(i).getClientId())) {
							if(k==0) {
								strClient=clientList.get(i).getClientName();
							} else {
								strClient+=", "+clientList.get(i).getClientName();
							}
							k++;
						}
					}
				}
				if(strClient!=null && !strClient.equals("")) {
					hmFilter.put("CLIENT", strClient);
				} else {
					hmFilter.put("CLIENT", "All Clients");
				}
			} else {
				hmFilter.put("CLIENT", "All Clients");
			}
		}
		
		
		String selectedFilter=CF.getSelectedFilter1(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public List<FillProjectList> getProjectList() {
		return projectList;
	}

	public void setProjectList(List<FillProjectList> projectList) {
		this.projectList = projectList;
	}

	public String getStrProject() {
		return strProject;
	}

	public void setStrProject(String strProject) {
		this.strProject = strProject;
	}

	public String getF_start() {
		return f_start;
	}

	public void setF_start(String f_start) {
		this.f_start = f_start;
	}

	public String getF_end() {
		return f_end;
	}

	public void setF_end(String f_end) {
		this.f_end = f_end;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public List<FillPayCycles> getPayCycleList() {
		return payCycleList;
	}

	public void setPayCycleList(List<FillPayCycles> payCycleList) {
		this.payCycleList = payCycleList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public String getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getF_department() {
		return f_department;
	}

	public void setF_department(String f_department) {
		this.f_department = f_department;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String[] getClient() {
		return client;
	}

	public void setClient(String[] client) {
		this.client = client;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	public List<FillBillingType> getBillingFreqList() {
		return billingFreqList;
	}

	public void setBillingFreqList(List<FillBillingType> billingFreqList) {
		this.billingFreqList = billingFreqList;
	}

	public List<FillBillingType> getBillingTypeList() {
		return billingTypeList;
	}

	public void setBillingTypeList(List<FillBillingType> billingTypeList) {
		this.billingTypeList = billingTypeList;
	}

	public String getBillingType() {
		return billingType;
	}

	public void setBillingType(String billingType) {
		this.billingType = billingType;
	}

	public String getStrBillingFreq() {
		return strBillingFreq;
	}

	public void setStrBillingFreq(String strBillingFreq) {
		this.strBillingFreq = strBillingFreq;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getProFreqId() {
		return proFreqId;
	}

	public void setProFreqId(String proFreqId) {
		this.proFreqId = proFreqId;
	}

	public String getProPage() {
		return proPage;
	}

	public void setProPage(String proPage) {
		this.proPage = proPage;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}

	public String getBtnSubmit() {
		return btnSubmit;
	}

	public void setBtnSubmit(String btnSubmit) {
		this.btnSubmit = btnSubmit;
	}


	public String getStrClient() {
		return strClient;
	}


	public void setStrClient(String strClient) {
		this.strClient = strClient;
	}
	
}